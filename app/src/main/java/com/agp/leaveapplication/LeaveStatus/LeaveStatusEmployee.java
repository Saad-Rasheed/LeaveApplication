package com.agp.leaveapplication.LeaveStatus;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.agp.leaveapplication.LoginActivity;
import com.agp.leaveapplication.R;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LeaveStatusEmployee extends Fragment implements View.OnClickListener {

    Spinner spinnerLeaveType;
    Button btn_filter;
    private List<Model_LeaveStatus> arr_list;
    private RecyclerView recyclerViewStatus;
    private RecyclerView.Adapter adapter;
    private SharedPreferences sharedPreferences;
    private String EmpCode;
    private KProgressHUD loader;

    public LeaveStatusEmployee() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leave_status_employee, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("MyPre", Context.MODE_PRIVATE);
        EmpCode = sharedPreferences.getString("EmpCode", "");

        loader = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setWindowColor(Color.parseColor("#201460"));

        btn_filter = view.findViewById(R.id.btn_filter);
        btn_filter.setOnClickListener(this);
        spinnerLeaveType = view.findViewById(R.id.spinnerLeaveType);
        arr_list = new ArrayList<>();

        recyclerViewStatus = view.findViewById(R.id.recyclerViewStatus);
        recyclerViewStatus.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        //LeaveType();
        final String[] items = new String[]{
                "Select Leave Type", "All", "Pending", "Approved", "Rejected"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        spinnerLeaveType.setAdapter(adapter);
    }

    public void LeaveType(int position) {

        switch (position) {
            case 1:
                arr_list.clear();
                All();
                break;
            case 2:
                arr_list.clear();
                Pending();
                break;
            case 3:
                arr_list.clear();
                Approved();
                break;
            case 4:
                arr_list.clear();
                Rejected();
                break;
        }
    }

    public void All() {
        loader.show();
        String URL = "http://" + LoginActivity.IP + "/LeaveApi/getemployeeLeave.php";
        StringRequest req = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            loader.dismiss();
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String EmployeeNo = jsonObject.getString("EmployeeNo");
                                if (EmployeeNo.equals(EmpCode)) {
                                    JSONObject innerobj1 = jsonObject.getJSONObject("fromdate");
                                    String fromdate = innerobj1.getString("date");
                                    JSONObject innerobj2 = jsonObject.getJSONObject("todate");
                                    String todate = innerobj2.getString("date");
                                    String remarks = jsonObject.getString("remarks");
                                    String status = jsonObject.getString("status");
                                    String totaldays = jsonObject.getString("totaldays");
                                    String LeaveCode = jsonObject.getString("LeaveCode");

                                    if (status.equals("1")) {
                                        Model_LeaveStatus item = new Model_LeaveStatus(
                                                LeaveCode,
                                                fromdate,
                                                todate,
                                                totaldays,
                                                remarks,
                                                "Pending"
                                        );
                                        //arr_list.clear();
                                        arr_list.add(item);
                                    } else if (status.equals("2")) {
                                        Model_LeaveStatus item = new Model_LeaveStatus(
                                                LeaveCode,
                                                fromdate,
                                                todate,
                                                totaldays,
                                                remarks,
                                                "Approved"
                                        );
                                        //arr_list.clear();
                                        arr_list.add(item);
                                    } else {
                                        Model_LeaveStatus item = new Model_LeaveStatus(
                                                LeaveCode,
                                                fromdate,
                                                todate,
                                                totaldays,
                                                remarks,
                                                "Rejected"
                                        );
                                        //arr_list.clear();
                                        arr_list.add(item);
                                    }
                                }
                            }
                            adapter = new LeaveStatus_Adapter(arr_list, getContext());
                            recyclerViewStatus.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loader.dismiss();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(req);
    }

    public void Pending() {
        String URL = "http://" + LoginActivity.IP + "/LeaveApi/getemployeeLeave.php";
        StringRequest req = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String EmployeeNo = jsonObject.getString("EmployeeNo");
                                String status = jsonObject.getString("status");
                                if (EmployeeNo.equals(EmpCode)) {
                                    if (status.equals("1")) {
                                        JSONObject innerobj1 = jsonObject.getJSONObject("fromdate");
                                        String fromdate = innerobj1.getString("date");
                                        JSONObject innerobj2 = jsonObject.getJSONObject("todate");
                                        String todate = innerobj2.getString("date");
                                        String remarks = jsonObject.getString("remarks");
                                        String totaldays = jsonObject.getString("totaldays");
                                        String LeaveCode = jsonObject.getString("LeaveCode");

                                        Model_LeaveStatus item = new Model_LeaveStatus(
                                                LeaveCode,
                                                fromdate,
                                                todate,
                                                totaldays,
                                                remarks,
                                                "Pending"
                                        );
                                        //arr_list.clear();
                                        arr_list.add(item);
                                    }
                                }
                            }
                            adapter = new LeaveStatus_Adapter(arr_list, getContext());
                            recyclerViewStatus.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(req);
    }

    public void Approved() {
        String URL = "http://" + LoginActivity.IP + "/LeaveApi/getemployeeLeave.php";
        StringRequest req = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String EmployeeNo = jsonObject.getString("EmployeeNo");
                                String status = jsonObject.getString("status");
                                if (EmployeeNo.equals(EmpCode)) {
                                    if (status.equals("2")) {
                                        JSONObject innerobj1 = jsonObject.getJSONObject("fromdate");
                                        String fromdate = innerobj1.getString("date");
                                        JSONObject innerobj2 = jsonObject.getJSONObject("todate");
                                        String todate = innerobj2.getString("date");
                                        String remarks = jsonObject.getString("remarks");
                                        String totaldays = jsonObject.getString("totaldays");
                                        String LeaveCode = jsonObject.getString("LeaveCode");

                                        Model_LeaveStatus item = new Model_LeaveStatus(
                                                LeaveCode,
                                                fromdate,
                                                todate,
                                                totaldays,
                                                remarks,
                                                "Approved"
                                        );
                                        //arr_list.clear();
                                        arr_list.add(item);
                                    }
                                }
                            }
                            adapter = new LeaveStatus_Adapter(arr_list, getContext());
                            recyclerViewStatus.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(req);
    }

    public void Rejected() {
        String URL = "http://" + LoginActivity.IP + "/LeaveApi/getemployeeLeave.php";
        StringRequest req = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String EmployeeNo = jsonObject.getString("EmployeeNo");
                                String status = jsonObject.getString("status");
                                if (EmployeeNo.equals(EmpCode)) {
                                    if (status.equals("3")) {
                                        JSONObject innerobj1 = jsonObject.getJSONObject("fromdate");
                                        String fromdate = innerobj1.getString("date");
                                        JSONObject innerobj2 = jsonObject.getJSONObject("todate");
                                        String todate = innerobj2.getString("date");
                                        String remarks = jsonObject.getString("remarks");
                                        String totaldays = jsonObject.getString("totaldays");
                                        String LeaveCode = jsonObject.getString("LeaveCode");

                                        Model_LeaveStatus item = new Model_LeaveStatus(
                                                LeaveCode,
                                                fromdate,
                                                todate,
                                                totaldays,
                                                remarks,
                                                "Rejected"
                                        );
                                        //arr_list.clear();
                                        arr_list.add(item);
                                    }
                                }
                            }
                            adapter = new LeaveStatus_Adapter(arr_list, getContext());
                            recyclerViewStatus.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(req);
    }

    @Override
    public void onClick(View view) {

        if (view == btn_filter) {
            if (spinnerLeaveType.getSelectedItemPosition() == 0) {
                Toast.makeText(getContext(), "Select Leave Type",
                        Toast.LENGTH_LONG).show();

            } else {
                LeaveType(spinnerLeaveType.getSelectedItemPosition());
            }
        }
    }
}
