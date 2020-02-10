package com.agp.leaveapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.agp.leaveapplication.Admin.Admin_HomeActivity;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout layoutData;
    private Button btn_loginemp, btn_register;
    private TextView tv_forgetpassword, tv_volleyError;
    private EditText ed_username, ed_password;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static String IP = "agpapp02.agp.com.pk";
    //public static String IP = "101.11.16.120";
    private KProgressHUD loader;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loader = KProgressHUD.create(LoginActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setWindowColor(Color.parseColor("#201460"));

        layoutData = findViewById(R.id.layoutData);
        tv_volleyError = findViewById(R.id.tv_volleyError);

        ed_username = findViewById(R.id.ed_username);
        ed_password = findViewById(R.id.ed_password);

        btn_loginemp = findViewById(R.id.btn_loginEmp);
        btn_loginemp.setOnClickListener(this);
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);

        tv_forgetpassword = findViewById(R.id.tv_forgetpassword);
        tv_forgetpassword.setOnClickListener(this);

        sharedPreferences = getSharedPreferences("MyPre", MODE_PRIVATE);
        token = SharedPrefManager.getInstance(this).getDeviceToken();
    }

    public void UserLogin(final String empcode) {

        loader.show();
        String URL = "http://" + IP + "/LeaveApi/login.php"; //for home
        StringRequest req = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if (msg.equals("success")) {
                                layoutData.setVisibility(View.VISIBLE);
                                tv_volleyError.setVisibility(View.GONE);
                                loader.dismiss();
                                String role = jsonObject.getString("Role");
                                if (role.equals("0")) {
                                    editor = sharedPreferences.edit();
                                    editor.putString("EmpCode", jsonObject.getString("EmpCode"));
                                    editor.putString("Password", jsonObject.getString("Password"));
                                    editor.putString("Role", role);
                                    editor.putString("MgrCode", jsonObject.getString("MgrCode"));
                                    editor.putString("MgrToken", jsonObject.getString("MgrToken"));
                                    /*Get Name,Department, Desgination and stored in SharedPreference and get the details into EmployeeDetails */
                                    editor.putString("EmployeeTitle", jsonObject.getString("EmployeeTitle"));
                                    editor.putString("EmployeeName", jsonObject.getString("EmployeeName"));
                                    editor.putString("Email", jsonObject.getString("Email"));
                                    editor.putString("MgrName", jsonObject.getString("MgrName"));
                                    editor.putString("MgrEmail", jsonObject.getString("MgrEmail"));
                                    editor.putString("CCEmail", jsonObject.getString("CCEmail"));
                                    editor.putString("Designation", jsonObject.getString("Designation"));
                                    editor.putBoolean("saveLogin", true);
                                    editor.apply();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), EmployeeHomeActivity.class));
                                } else {
                                    editor = sharedPreferences.edit();
                                    editor.putString("EmpCode", jsonObject.getString("EmpCode"));
                                    editor.putString("Password", jsonObject.getString("Password"));
                                    editor.putString("Role", role);
                                    editor.putString("MgrCode", jsonObject.getString("MgrCode"));
                                    editor.putString("MgrToken", jsonObject.getString("MgrToken"));
                                    /*Get Name,Department, Desgination and stored in SharedPreference and get the details into EmployeeDetails */
                                    editor.putString("EmployeeTitle", jsonObject.getString("EmployeeTitle"));
                                    editor.putString("EmployeeName", jsonObject.getString("EmployeeName"));
                                    editor.putString("Email", jsonObject.getString("Email"));
                                    editor.putString("MgrName", jsonObject.getString("MgrName"));
                                    editor.putString("MgrEmail", jsonObject.getString("MgrEmail"));
                                    editor.putString("CCEmail", jsonObject.getString("CCEmail"));
                                    editor.putString("Designation", jsonObject.getString("Designation"));
                                    editor.putBoolean("saveLogin", true);
                                    editor.apply();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), Admin_HomeActivity.class));
                                }
                                UpdateToken(jsonObject.getString("EmpCode"));
                            } else {
                                loader.dismiss();
                                /*layoutData.setVisibility(View.GONE);
                                tv_volleyError.setVisibility(View.VISIBLE);
                                tv_volleyError.setText(msg);*/
                                showDialog(LoginActivity.this, msg);
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
                        //Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                        HandleVolleyError(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("EmpCode", empcode);
                map.put("Password", ed_password.getText().toString().trim());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(req);
    }

    public void UpdateToken(final String EmpCode){
        String URL = "http://" + LoginActivity.IP + "/LeaveApi/TokenVerification.php";
        StringRequest req = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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
                map.put("EmpCode", EmpCode);
                map.put("Token", token);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(req);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {

        if (view == btn_register) {
            startActivity(new Intent(getApplicationContext(), SignupActivity.class));
        }

        if (view == btn_loginemp) {
            if (TextUtils.isEmpty(ed_username.getText().toString())) {
                ed_username.setError("Please enter Employee Code");
                ed_username.requestFocus();
            } else if (TextUtils.isEmpty(ed_password.getText().toString())) {
                ed_password.setError("Please enter Password");
                ed_password.requestFocus();
            } else {
                if (ed_username.getText().length() == 1) {
                    String empcode = "0000" + ed_username.getText().toString();
                    UserLogin(empcode);
                } else if (ed_username.getText().length() == 2) {
                    String empcode = "000" + ed_username.getText().toString();
                    UserLogin(empcode);
                } else if (ed_username.getText().length() == 3) {
                    String empcode = "00" + ed_username.getText().toString();
                    //Toast.makeText(getApplicationContext(), empcode, Toast.LENGTH_LONG).show();
                    UserLogin(empcode);
                } else if (ed_username.getText().length() == 4) {
                    String empcode = "0" + ed_username.getText().toString();
                    UserLogin(empcode);
                } else if (ed_username.getText().length() == 5) {
                    String empcode = ed_username.getText().toString();
                    UserLogin(empcode);
                }
            }
        }
        if (view == tv_forgetpassword) {
            finish();
            startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
        }
    }

    private void HandleVolleyError(VolleyError error) {

        /*layoutData.setVisibility(View.GONE);
        tv_volleyError.setVisibility(View.VISIBLE);*/

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

            //tv_volleyError.setText("Connection Error:\nPlease Check your Internet Connection");
            showDialog(this, "Connection Error:\nPlease Check your Internet Connection");

        } else if (error instanceof AuthFailureError) {

            //tv_volleyError.setText("Auth Failure Error");
            showDialog(this, "Auth Failure Error");

        } else if (error instanceof ServerError) {

            //tv_volleyError.setText("Server Error:\nServer isn't Responding, Please try again later");
            showDialog(this, "Server Error:\nServer isn't Responding, Please try again later");

        } else if (error instanceof NetworkError) {

            //tv_volleyError.setText("Network Error");
            showDialog(this, "Network Error");

        } else if (error instanceof ParseError) {

            //tv_volleyError.setText("JSON Parsing Error");
            showDialog(this, "JSON Parsing Error");
        }
    }

    public void showDialog(Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.tv_error);
        text.setText(msg);

        ImageView dialogCancel = (ImageView) dialog.findViewById(R.id.img_close);
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
