package com.agp.leaveapplication.LeavesInfoDashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment_EmployeeLeavesInfo extends Fragment {

    private SharedPreferences sharedPreferences;
    private String EmpCode;
    private RecyclerView leaves_info;
    private RecyclerView.Adapter adapter;
    private List<Model_EmployeeLeavesInfo> arr_list;
    private TextView tv_error;
    private KProgressHUD loader;

    public Fragment_EmployeeLeavesInfo() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_leaves_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("MyPre", Context.MODE_PRIVATE);
        EmpCode = sharedPreferences.getString("EmpCode", "");

        tv_error = view.findViewById(R.id.tv_error);

        loader = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setWindowColor(Color.parseColor("#201460"));

        leaves_info = view.findViewById(R.id.recyclerViewLeavesInfo);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        leaves_info.setLayoutManager(mLayoutManager);
        leaves_info.setItemAnimator(new DefaultItemAnimator());
        leaves_info.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        arr_list = new ArrayList<>();
        getLeavesInfo();
    }

    public void getLeavesInfo() {

        loader.show();
        String URL = "http://" + LoginActivity.IP + "/LeaveApi/getLeaveEntitlement.php";
        StringRequest req = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.isEmpty()) {
                            try {
                                leaves_info.setVisibility(View.VISIBLE);
                                tv_error.setVisibility(View.GONE);
                                JSONArray jsonArray = new JSONArray(response);
                                loader.dismiss();
                                for (int i = 0; i < jsonArray.length() - 2; i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String leave_name = jsonObject.getString("LeaveName");
                                    String leave_qty = jsonObject.getString("LeaveQuantity");
                                    String leave_availed = jsonObject.getString("AvailLeaves");
                                    String leave_balance = jsonObject.getString("LeaveBalance");

                                    Model_EmployeeLeavesInfo item = new Model_EmployeeLeavesInfo(
                                            leave_name,
                                            leave_qty,
                                            leave_availed,
                                            leave_balance
                                    );
                                    arr_list.add(item);
                                }
                                adapter = new Adapter_EmployeeLeavesInfo(arr_list, getContext());
                                leaves_info.setAdapter(adapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            loader.dismiss();
                            leaves_info.setVisibility(View.GONE);
                            tv_error.setVisibility(View.VISIBLE);
                            tv_error.setText("No Data Found! Because You are not entitle to any leaves Please Contact the Admin Department");
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
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(req);
    }
}
