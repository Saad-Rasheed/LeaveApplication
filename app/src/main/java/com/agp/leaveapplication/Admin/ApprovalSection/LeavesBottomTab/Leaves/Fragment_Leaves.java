package com.agp.leaveapplication.Admin.ApprovalSection.LeavesBottomTab.Leaves;

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
import android.widget.Toast;

import com.agp.leaveapplication.LoginActivity;
import com.agp.leaveapplication.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Leaves extends Fragment {

    private RecyclerView recyclerViewApprovals;
    private RecyclerView.Adapter adapter;
    private List<Model_Leaves> arr_list;
    private SharedPreferences sharedPreferences;
    private String EmpCode;
    private KProgressHUD loader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaves, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loader = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setWindowColor(Color.parseColor("#201460"));

        sharedPreferences = getActivity().getSharedPreferences("MyPre", Context.MODE_PRIVATE);
        EmpCode = sharedPreferences.getString("EmpCode", "");

        recyclerViewApprovals = view.findViewById(R.id.recyclerViewApprovals);
        recyclerViewApprovals.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        arr_list = new ArrayList<>();
        getEmployeeLeaves();
    }

    public void getEmployeeLeaves() {
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
                                JSONObject object = jsonArray.getJSONObject(i);
                                String MgrCode = object.getString("MgrCode");
                                String status = object.getString("status");
                                if (MgrCode.equals(EmpCode)) {
                                    if (status.equals("1")) {
                                        String name = object.getString("EmployeeName");
                                        String EmployeeNo = object.getString("EmployeeNo");
                                        String id = object.getString("id");
                                        String LeaveCode = object.getString("LeaveCode");
                                        JSONObject fromdate = object.getJSONObject("fromdate");
                                        JSONObject todate = object.getJSONObject("todate");
                                        String date = fromdate.getString("date");
                                        String[] arr_date = date.split(" ");
                                        String to_date = todate.getString("date");
                                        String[] arr_to_date = to_date.split(" ");
                                        String remarks = object.getString("remarks");
                                        status = object.getString("status");
                                        String totaldays = object.getString("totaldays");
                                        String EmployeeEmail = object.getString("Email");
                                        JSONObject Timestamp = object.getJSONObject("RequestTimestamp");
                                        String RequestTimestamp = Timestamp.getString("date");
                                        String[] arr_time = RequestTimestamp.split(" ");
                                        Model_Leaves item = new Model_Leaves(
                                                EmployeeNo,
                                                id,
                                                name,
                                                LeaveCode,
                                                arr_date[0],
                                                arr_to_date[0],
                                                remarks,
                                                status,
                                                totaldays,
                                                EmployeeEmail,
                                                arr_time[1]
                                        );
                                        arr_list.add(item);
                                    }
                                }
                            }
                            adapter = new Adapter_Leaves(arr_list, getContext());
                            recyclerViewApprovals.setAdapter(adapter);
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
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(req);
    }
}
