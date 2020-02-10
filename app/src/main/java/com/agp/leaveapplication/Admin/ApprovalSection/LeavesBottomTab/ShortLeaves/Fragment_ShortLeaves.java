package com.agp.leaveapplication.Admin.ApprovalSection.LeavesBottomTab.ShortLeaves;

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


public class Fragment_ShortLeaves extends Fragment {

    private RecyclerView recyclerViewApprovals;
    private RecyclerView.Adapter adapter;
    private List<Model_ShortLeave> arr_list;
    private SharedPreferences sharedPreferences;
    private String EmpCode;
    private KProgressHUD loader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_short_leaves, container, false);
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
        getEmployeeShortLeaves();
    }

    public void getEmployeeShortLeaves() {
        loader.show();
        String URL = "http://" + LoginActivity.IP + "/LeaveApi/getemployeeShortLeave.php";
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
                                if (MgrCode.equals(EmpCode)) {
                                    String name = object.getString("EmployeeName");
                                    String EmployeeNo = object.getString("EmployeeNo");
                                    String id = object.getString("id");
                                    String LeaveType = object.getString("LeaveType");
                                    String timefrom = object.getString("timefrom");
                                    String timeto = object.getString("timeto");
                                    String totaltime = object.getString("totaltime");
                                    String remarks = object.getString("remarks");
                                    String status = object.getString("status");
                                    String EmployeeEmail = object.getString("Email");
                                    Model_ShortLeave item = new Model_ShortLeave(
                                            EmployeeNo,
                                            id,
                                            name,
                                            LeaveType,
                                            timefrom,
                                            timeto,
                                            remarks,
                                            status,
                                            totaltime,
                                            EmployeeEmail
                                    );
                                    arr_list.add(item);
                                }
                            }
                            adapter = new Adapter_ShortLeave(arr_list, getContext());
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
