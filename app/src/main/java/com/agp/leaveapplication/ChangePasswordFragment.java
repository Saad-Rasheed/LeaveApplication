package com.agp.leaveapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.agp.leaveapplication.Admin.Admin_HomeActivity;
import com.agp.leaveapplication.LeavesInfoDashboard.Fragment_EmployeeLeavesInfo;
import com.android.volley.AuthFailureError;
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

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    private EditText ed_currentPassword, ed_new_password, ed_confirmpassword;
    private Button btn_Submit;
    private KProgressHUD loader;
    private SharedPreferences sharedPreferences;
    private String EmpCode, Password, Role;

    public ChangePasswordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("MyPre", Context.MODE_PRIVATE);
        EmpCode = sharedPreferences.getString("EmpCode", "");
        Password = sharedPreferences.getString("Password", "");
        Role = sharedPreferences.getString("Role", "");

        ed_currentPassword = view.findViewById(R.id.ed_currentPassword);
        ed_new_password = view.findViewById(R.id.ed_new_password);
        ed_confirmpassword = view.findViewById(R.id.ed_confirmpassword);
        btn_Submit = view.findViewById(R.id.btn_Submit);
        btn_Submit.setOnClickListener(this);

        loader = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setWindowColor(Color.parseColor("#201460"));

    }

    public void ChangePassword() {

        loader.show();
        String URL = "http://" + LoginActivity.IP + "/LeaveApi/ChangePassword.php";
        StringRequest req = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            if (msg.equals("success")) {
                                loader.dismiss();
                                if (Role.equals("0")) {
                                    Toast.makeText(getContext(), "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getContext(), EmployeeHomeActivity.class));
                                } else {
                                    Toast.makeText(getContext(), "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getContext(), Admin_HomeActivity.class));
                                }
                            } else {
                                loader.dismiss();
                                Toast.makeText(getActivity(), msg,
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
                        Toast.makeText(getContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("EmpCode", EmpCode);
                map.put("Password", ed_new_password.getText().toString());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(req);
    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(ed_currentPassword.getText().toString())) {
            ed_currentPassword.setError("Current password");
            ed_currentPassword.requestFocus();
        } else if (TextUtils.isEmpty(ed_new_password.getText().toString())) {
            ed_new_password.setError("New password");
            ed_new_password.requestFocus();
        } else if (TextUtils.isEmpty(ed_confirmpassword.getText().toString())) {
            ed_confirmpassword.setError("Re-type new password");
            ed_confirmpassword.requestFocus();
        } else {
            if (ed_currentPassword.getText().toString().equals(Password)) {
                String Pass = ed_new_password.getText().toString();
                if (!Pass.equals(Pass.toLowerCase()) &&
                        !Pass.equals(Pass.toUpperCase()) &&
                        Pass.matches(".*\\d+.*") && Pass.length() >= 8) {

                    String ConfirmPass = ed_confirmpassword.getText().toString();
                    if (Pass.equals(ConfirmPass)) {
                        ChangePassword();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Try Again");
                        builder.setMessage("New Password not matched!")
                                .setCancelable(false);
                        builder.setIcon(R.drawable.error);
                        builder.setPositiveButton("Close", null);
                        builder.show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please set your password according to requirements", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getContext(), "Current Password is Incorrect! Please Try Again", Toast.LENGTH_LONG).show();
            }
        }
    }
}
