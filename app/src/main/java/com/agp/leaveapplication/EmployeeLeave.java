package com.agp.leaveapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.agp.leaveapplication.Admin.Admin_HomeActivity;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.blackbox_vision.datetimepickeredittext.view.DatePickerEditText;


public class EmployeeLeave extends Fragment implements View.OnClickListener {

    Spinner spinnerLeaveType;
    private ArrayList<String> arr_leave_code;
    private ArrayList<String> arr_leave_name;
    private ArrayList<String> arr_leave_quantity;
    private ArrayList<String> arr_leave_balance;
    private String leavequantity, leavebalance;

    DatePickerEditText edLeavefrom, edLeaveto;
    EditText ed_Remarks;
    TextView tv_days;
    Button btn_Submit;
    RadioGroup radioGroup;
    private String leaveType, MgrCode, EmpCode, Role, MgrToken, EmployeeName;
    private String halfdaytype = "";
    private SharedPreferences sharedPreferences;
    private CheckBox halfday;
    long total_days;
    private KProgressHUD loader;
    RadioGroup HalfTypeResult;
    RadioButton radio_morning, radio_evening;

    public EmployeeLeave() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_leave, container, false);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("MyPre", Context.MODE_PRIVATE);
        MgrCode = sharedPreferences.getString("MgrCode", "");
        EmpCode = sharedPreferences.getString("EmpCode", "");
        Role = sharedPreferences.getString("Role", "");
        MgrToken = sharedPreferences.getString("MgrToken", "");
        EmployeeName = sharedPreferences.getString("EmployeeName", "");

        edLeavefrom = view.findViewById(R.id.edLeavefrom);
        edLeaveto = view.findViewById(R.id.edLeaveto);
        radioGroup = view.findViewById(R.id.radioHalfdayType);
        radio_morning = (RadioButton) view.findViewById(R.id.radio_morning);
        radio_evening = (RadioButton) view.findViewById(R.id.radio_evening);
        tv_days = view.findViewById(R.id.tv_days);
        ed_Remarks = view.findViewById(R.id.ed_Remarks);
        btn_Submit = view.findViewById(R.id.btn_Submit);
        halfday = view.findViewById(R.id.halfday);
        btn_Submit.setOnClickListener(this);

        loader = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setWindowColor(Color.parseColor("#201460"));

        spinnerLeaveType = view.findViewById(R.id.spinnerLeaveType);
        arr_leave_code = new ArrayList<>();
        arr_leave_name = new ArrayList<>();
        arr_leave_quantity = new ArrayList<>();
        arr_leave_balance = new ArrayList<>();

        LeaveType();

        edLeavefrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edLeavefrom.getText().toString().isEmpty()) return;
                edLeaveto.setText(null);
                edLeaveto.setEnabled(true);
                try {
                    edLeaveto.setMinDate(new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd-MM-yyyy").parse(edLeavefrom.getText().toString())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edLeaveto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edLeaveto.getText().toString().isEmpty()) return;

                total_days = DateUtil.GetDifference(edLeavefrom.getText().toString(), edLeaveto.getText().toString());
                tv_days.setText(String.valueOf(total_days));


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        spinnerLeaveType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                leaveType = arr_leave_code.get(position);
                leavequantity = arr_leave_quantity.get(position);
                leavebalance = arr_leave_balance.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        halfday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    double days = (total_days - 1) + 0.5;
                    tv_days.setText(String.valueOf(days));
                } else {
                    tv_days.setText(String.valueOf(total_days));
                }
            }
        });

        HalfTypeResult = view.findViewById(R.id.radioHalfdayType);
        HalfTypeResult.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_morning) {
                    halfdaytype = "morning";
                    //Toast.makeText(getContext(), halfdaytype, Toast.LENGTH_LONG).show();
                } else if (checkedId == R.id.radio_evening) {
                    halfdaytype = "evening";
                    //Toast.makeText(getContext(), halfdaytype, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void LeaveType() {

        String URL = "http://" + LoginActivity.IP + "/LeaveApi/spinnerLeaveType.php";
        StringRequest req = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            arr_leave_name.add("Select");
                            arr_leave_code.add("0");
                            arr_leave_quantity.add("0");
                            arr_leave_balance.add("0");
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //Toast.makeText(getContext(), (CharSequence) arr_leave_name,Toast.LENGTH_LONG).show();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String LeaveCode = jsonObject.getString("LeaveCode");
                                String LeaveName = jsonObject.getString("LeaveName");
                                String LeaveQuantity = jsonObject.getString("LeaveQuantity");
                                String AvailLeaves = jsonObject.getString("AvailLeaves");

                                arr_leave_code.add(LeaveCode);
                                arr_leave_name.add(LeaveName);
                                arr_leave_quantity.add(LeaveQuantity);
                                arr_leave_balance.add(AvailLeaves);
                            }
                            spinnerLeaveType.setAdapter(new ArrayAdapter<>(getContext(),
                                    android.R.layout.simple_spinner_dropdown_item, arr_leave_name));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("EmployeeNo", EmpCode);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(req);
    }

    public void postLeaveData() {

        loader.show();
        String URL = "http://" + LoginActivity.IP + "/LeaveApi/CreateLeaveRequest.php";
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
                                    Toast.makeText(getContext(), "Leave Request has been Submitted", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getContext(), EmployeeHomeActivity.class));
                                } else {
                                    Toast.makeText(getContext(), "Leave Request has been Submitted", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getContext(), Admin_HomeActivity.class));
                                }
                                sendNotification();
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
                map.put("LeaveCode", leaveType);
                map.put("fromdate", edLeavefrom.getText().toString());
                map.put("todate", edLeaveto.getText().toString());
                map.put("totaldays", tv_days.getText().toString());
                map.put("remarks", ed_Remarks.getText().toString());
                map.put("HalfdayType",halfdaytype);
                map.put("status", "1");
                map.put("MgrCode", MgrCode);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(req);
    }

    public void sendNotification() {

        String URL = "http://" + LoginActivity.IP + "/LeaveApi/sendSinglePush.php";
        StringRequest req = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("MgrToken", MgrToken);
                map.put("title", "Leave Request Raised");
                map.put("message", EmployeeName + " has applied for leave.");
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(req);
    }


    @Override
    public void onClick(View view) {
        if (spinnerLeaveType.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Select Leave Type",
                    Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(edLeavefrom.getText().toString())) {
            edLeavefrom.setError("Please Select Date From");
            edLeavefrom.requestFocus();
        } else if (TextUtils.isEmpty(edLeaveto.getText().toString())) {
            edLeaveto.setError("Please Select Date To");
            edLeaveto.requestFocus();
        } else if (!leaveType.equals("0")) {
            if (leavequantity.equals(leavebalance)) {
                Toast.makeText(getContext(), "Sorry! You can't apply", Toast.LENGTH_LONG).show();
            } else {
                postLeaveData();
            }
        }
    }
}
