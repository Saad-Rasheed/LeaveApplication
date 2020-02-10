package com.agp.leaveapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private KProgressHUD loader;
    private EditText ed_EmployeeNo, ed_Email;
    private Button btn_Submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        loader = KProgressHUD.create(ForgetPasswordActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setWindowColor(Color.parseColor("#201460"));

        ed_EmployeeNo = findViewById(R.id.ed_EmployeeNo);
        ed_Email = findViewById(R.id.ed_Email);
        btn_Submit = findViewById(R.id.btn_Submit);
        btn_Submit.setOnClickListener(this);
    }

    public String generatePassword() {
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String alphabet = CHAR_LOWER.toUpperCase();
        final int N = alphabet.length();
        Random rd = new Random();
        int iLength = 5;
        StringBuilder sb = new StringBuilder(iLength);
        for (int i = 0; i < iLength; i++) {
            sb.append(alphabet.charAt(rd.nextInt(N)));
        }
        String resultText = sb.toString();

        String Number = "0123456789";
        final int Num = Number.length();
        Random random = new Random();
        int jLength = 2;
        StringBuilder stringBuilder = new StringBuilder(jLength);
        for (int i = 0; i < jLength; i++) {
            stringBuilder.append(Number.codePointAt(random.nextInt(Num)));
        }
        String resultnum = stringBuilder.toString();
        String Password = resultText + resultnum;
        return Password;
    }

    public void ForgetPassword(final String EmployeeNo) {

        final String Password = generatePassword();
        loader.show();
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String URL = "http://" + LoginActivity.IP + "/LeaveApi/ForgotPassword.php";
        StringRequest req = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if (msg.equals("success")) {
                                String URL_1 = "http://" + LoginActivity.IP + "/LeaveApi/ForgotSendEmail.php";
                                StringRequest req1 = new StringRequest(Request.Method.POST, URL_1,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    String msg = jsonObject.getString("msg");
                                                    if (msg.equals("success")) {
                                                        loader.dismiss();
                                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                        Toast.makeText(getApplicationContext(), "Thank You! Please Check your email password is sent Successfully", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(getApplicationContext(), error.getMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("Email", ed_Email.getText().toString());
                                        map.put("Password", Password);
                                        return map;
                                    }
                                };
                                req1.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 5,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)); //This is added bcz req is hitting twice.

                                //RequestQueue requestQueue = Volley.newRequestQueue(context);
                                requestQueue.add(req1);

                            } else {
                                loader.dismiss();
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("EmployeeNo", EmployeeNo);
                map.put("Email", ed_Email.getText().toString());
                map.put("Password", Password);
                return map;
            }
        };
        requestQueue.add(req);
    }

    @Override
    public void onClick(View view) {
        if (view == btn_Submit) {
            if (TextUtils.isEmpty(ed_EmployeeNo.getText().toString())) {
                ed_EmployeeNo.setError("Please enter Employee No");
                ed_EmployeeNo.requestFocus();
            } else if (TextUtils.isEmpty(ed_Email.getText().toString())) {
                ed_Email.setError("Please enter email address");
                ed_Email.requestFocus();
            } else {
                if (ed_EmployeeNo.getText().length() == 1) {
                    String EmployeeNo = "0000" + ed_EmployeeNo.getText().toString();
                    ForgetPassword(EmployeeNo);
                } else if (ed_EmployeeNo.getText().length() == 2) {
                    String EmployeeNo = "000" + ed_EmployeeNo.getText().toString();
                    ForgetPassword(EmployeeNo);
                } else if (ed_EmployeeNo.getText().length() == 3) {
                    String EmployeeNo = "00" + ed_EmployeeNo.getText().toString();
                    ForgetPassword(EmployeeNo);
                } else if (ed_EmployeeNo.getText().length() == 4) {
                    String EmployeeNo = "0" + ed_EmployeeNo.getText().toString();
                    ForgetPassword(EmployeeNo);
                } else if (ed_EmployeeNo.getText().length() == 5) {
                    String EmployeeNo = ed_EmployeeNo.getText().toString();
                    ForgetPassword(EmployeeNo);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

}
