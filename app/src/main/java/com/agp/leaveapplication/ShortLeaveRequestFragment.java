package com.agp.leaveapplication;

import android.app.TimePickerDialog;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.agp.leaveapplication.Admin.Admin_HomeActivity;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class ShortLeaveRequestFragment extends Fragment implements View.OnClickListener {

    private String EmpCode, MgrCode, Role;
    TextView tv_show_time_from, tv_show_time_to, tv_showTotalTime;
    EditText ed_Remarks;
    Button btn_Submit;
    private SharedPreferences sharedPreferences;
    private KProgressHUD loader;

    public ShortLeaveRequestFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_apply_short_leave, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("MyPre", Context.MODE_PRIVATE);
        MgrCode = sharedPreferences.getString("MgrCode", "");
        EmpCode = sharedPreferences.getString("EmpCode", "");
        Role = sharedPreferences.getString("Role", "");

        tv_show_time_from = view.findViewById(R.id.tv_show_time_from);
        tv_show_time_to = view.findViewById(R.id.tv_show_time_to);
        tv_showTotalTime = view.findViewById(R.id.tv_showTotalTime);
        ed_Remarks = view.findViewById(R.id.ed_Remarks);
        btn_Submit = view.findViewById(R.id.btn_Submit);
        btn_Submit.setOnClickListener(this);

        loader = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setWindowColor(Color.parseColor("#201460"));

        tv_show_time_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeFrom();
            }
        });
        tv_show_time_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeTo();
            }
        });

    }

    private void showTimeFrom() {
        // TODO Auto-generated method stub
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                tv_show_time_from.setText(selectedHour + ":" + selectedMinute);
                //timefrom = tv_show_time_from.getText().toString();
                //tv_show_time_to.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void showTimeTo() {
        // TODO Auto-generated method stub
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                tv_show_time_to.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void postShortLeaveData() {

        loader.show();
        String URL = "http://" + LoginActivity.IP + "/LeaveApi/shortLeaveRequest.php";
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
                                    Toast.makeText(getContext(), "Request has been Submitted", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getContext(), EmployeeHomeActivity.class));
                                } else {
                                    Toast.makeText(getContext(), "Request has been Submitted", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getContext(), Admin_HomeActivity.class));
                                }
                            } else {
                                loader.dismiss();
                                Toast.makeText(getContext(), msg,
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
                map.put("EmployeeNo", EmpCode);
                map.put("LeaveType", "ShortLeave");
                map.put("timefrom", tv_show_time_from.getText().toString());
                map.put("timeto", tv_show_time_to.getText().toString());
                map.put("totaltime", "2");
                map.put("remarks", ed_Remarks.getText().toString());
                map.put("status", "1");
                map.put("MgrCode", MgrCode);
                map.put("Count", "0");
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(req);

    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(tv_show_time_from.getText().toString())) {
            tv_show_time_from.setError("Please Select Time From");
            tv_show_time_from.requestFocus();
        } else if (TextUtils.isEmpty(tv_show_time_from.getText().toString())) {
            tv_show_time_to.setError("Please Select Time To");
            tv_show_time_to.requestFocus();
        } else if (TextUtils.isEmpty(ed_Remarks.getText().toString())) {
            ed_Remarks.setError("Please Enter Remarks");
            ed_Remarks.requestFocus();
        } else {
            postShortLeaveData();
        }
    }
}
