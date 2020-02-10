package com.agp.leaveapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText ed_empCode, ed_password, ed_confirmpassword;
    private TextView tv_alreadyregistered, tv_volleyError;
    private Button btn_register;
    private LinearLayout layoutData;
    private KProgressHUD loader;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        loader = KProgressHUD.create(SignupActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setWindowColor(Color.parseColor("#201460"));

        ed_empCode = findViewById(R.id.ed_empCode);
        ed_password = findViewById(R.id.ed_password);
        ed_confirmpassword = findViewById(R.id.ed_confirmpassword);
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);

        tv_alreadyregistered = findViewById(R.id.tv_alreadyregistered);
        tv_alreadyregistered.setOnClickListener(this);
        tv_volleyError = findViewById(R.id.tv_volleyError);
        layoutData = findViewById(R.id.layoutData);

        token = SharedPrefManager.getInstance(this).getDeviceToken();
    }

    public void RegisterUser(final String token, final String empcode) {

        loader.show();
        String URL = "http://" + LoginActivity.IP + "/LeaveApi/signup.php";
        StringRequest req = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            layoutData.setVisibility(View.VISIBLE);
                            tv_volleyError.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if (msg.equals("success")) {
                                loader.dismiss();
                                finish();
                                Toast.makeText(getApplicationContext(), "Thank You! Registered Successfully", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            } else {
                                loader.dismiss();
                                Toast.makeText(getApplicationContext(), msg,
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loader.dismiss();
                        //HandleVolleyError(error);
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("EmpCode", empcode);
                map.put("Password", ed_password.getText().toString());
                map.put("Token", token);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(req);
    }

    @Override
    public void onClick(View view) {

        if (view == btn_register) {
            if (TextUtils.isEmpty(ed_empCode.getText().toString())) {
                ed_empCode.setError("Please enter Employee Code");
                ed_empCode.requestFocus();
            } else if (TextUtils.isEmpty(ed_password.getText().toString())) {
                ed_password.setError("Please enter Password");
                ed_password.requestFocus();
            } else if (TextUtils.isEmpty(ed_confirmpassword.getText().toString())) {
                ed_confirmpassword.setError("Please enter confirm password");
                ed_confirmpassword.requestFocus();
            } else {
                String Pass = ed_password.getText().toString();
                if (!Pass.equals(Pass.toLowerCase()) &&
                        !Pass.equals(Pass.toUpperCase()) &&
                        Pass.matches(".*\\d+.*") && Pass.length() >= 8) {

                    String ConfirmPass = ed_confirmpassword.getText().toString();
                    if (Pass.equals(ConfirmPass)) {
                        if (ed_empCode.getText().length() == 1) {
                            String empcode = "0000" + ed_empCode.getText().toString();
                            RegisterUser(token, empcode);
                        } else if (ed_empCode.getText().length() == 2) {
                            String empcode = "000" + ed_empCode.getText().toString();
                            RegisterUser(token, empcode);
                        } else if (ed_empCode.getText().length() == 3) {
                            String empcode = "00" + ed_empCode.getText().toString();
                            RegisterUser(token, empcode);
                        } else if (ed_empCode.getText().length() == 4) {
                            String empcode = "0" + ed_empCode.getText().toString();
                            RegisterUser(token, empcode);
                        } else if (ed_empCode.getText().length() == 5) {
                            String empcode = ed_empCode.getText().toString();
                            RegisterUser(token, empcode);
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                        builder.setTitle("Try Again");
                        builder.setMessage("New Password not matched!")
                                .setCancelable(false);
                        builder.setIcon(R.drawable.error);
                        builder.setPositiveButton("Close", null);
                        builder.show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please set your password according to criteria!", Toast.LENGTH_LONG).show();
                }
            }
        }

        if (view == tv_alreadyregistered) {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void HandleVolleyError(VolleyError error) {

        layoutData.setVisibility(View.GONE);
        tv_volleyError.setVisibility(View.VISIBLE);

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

            tv_volleyError.setText("Connection Error:\nPlease Check your Internet Connection");

        } else if (error instanceof AuthFailureError) {

            tv_volleyError.setText("Auth Failure Error");

        } else if (error instanceof ServerError) {

            tv_volleyError.setText("Server Error:\nServer isn't Responding, Please try again later");

        } else if (error instanceof NetworkError) {

            tv_volleyError.setText("Network Error");

        } else if (error instanceof ParseError) {

            tv_volleyError.setText("JSON Parsing Error");
        }
    }
}
