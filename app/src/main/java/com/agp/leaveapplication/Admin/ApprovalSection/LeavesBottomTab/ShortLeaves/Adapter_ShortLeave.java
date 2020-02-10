package com.agp.leaveapplication.Admin.ApprovalSection.LeavesBottomTab.ShortLeaves;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agp.leaveapplication.Admin.Admin_HomeActivity;
import com.agp.leaveapplication.LoginActivity;
import com.agp.leaveapplication.R;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter_ShortLeave extends RecyclerView.Adapter<Adapter_ShortLeave.ViewHolder> {

    private List<Model_ShortLeave> employeeLeaveData;
    private Context context;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private String EmployeeTitle,EmployeeName,Email;

    public Adapter_ShortLeave(List<Model_ShortLeave> employeeLeaveData, Context context) {
        this.employeeLeaveData = employeeLeaveData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_shortleavespanel, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        sharedPreferences = context.getSharedPreferences("MyPre", Context.MODE_PRIVATE);
        EmployeeTitle = sharedPreferences.getString("EmployeeTitle", "");
        EmployeeName = sharedPreferences.getString("EmployeeName", "");
        Email = sharedPreferences.getString("Email", "");

        final Model_ShortLeave data = employeeLeaveData.get(i);
        viewHolder.tvEmpName.setText(data.getEmpName());
        viewHolder.tvEmpDetails.setText("from " + data.getTimefrom() + " to " + data.getTimeto());
        viewHolder.tvTotalTime.setText("Total Time: " + data.getTotaltime());
        viewHolder.tvRemarks.setText("Reason: " + data.getRemarks());
        viewHolder.tvLeaveType.setText("Leave Type: Short Leave");
        String status = data.getStatus();
        if (status.equals("1")) {
            viewHolder.tvStatus.setText("Status: Pending");

        } else if (status.equals("2")) {
            viewHolder.tvStatus.setText("Status: Approved");
            viewHolder.btn_Approve.setVisibility(View.GONE);
            viewHolder.btn_Reject.setVisibility(View.GONE);

        } else {
            viewHolder.tvStatus.setText("Status: Rejected");
            viewHolder.btn_Approve.setVisibility(View.GONE);
            viewHolder.btn_Reject.setVisibility(View.GONE);
        }

        viewHolder.btn_Approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final RequestQueue requestQueue = Volley.newRequestQueue(context);
                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                progressDialog.setCancelable(false);
                final String URL = "http://" + LoginActivity.IP + "/LeaveApi/ShortLeaveUpdateStatus.php";
                StringRequest req = new StringRequest(Request.Method.POST, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String msg = jsonObject.getString("msg");
                                    if (msg.equals("success")) {
                                        String URL_1 = "http://" + LoginActivity.IP + "/LeaveApi/shortLeaveSendEmail.php";
                                        StringRequest req1 = new StringRequest(Request.Method.POST, URL_1,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            String msg = jsonObject.getString("msg");
                                                            if (msg.equals("success")) {
                                                                progressDialog.dismiss();
                                                                context.startActivity(new Intent(context, Admin_HomeActivity.class));
                                                                Toast.makeText(context, "Short Leave Request Accepted", Toast.LENGTH_LONG).show();
                                                            } else {
                                                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(context, error.getMessage(),
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                }) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String, String> map = new HashMap<String, String>();
                                                map.put("EmployeeNo", data.getEmployeeNo());
                                                map.put("EmployeeTitle", EmployeeTitle);
                                                map.put("EmployeeName", data.getEmpName());
                                                map.put("LeaveType", viewHolder.tvLeaveType.getText().toString());
                                                map.put("timefrom", data.getTimefrom());
                                                map.put("timeto", data.getTimeto());
                                                map.put("totaltime", data.getTotaltime());
                                                map.put("Reason", data.getRemarks());
                                                map.put("ManagerName", EmployeeName);
                                                map.put("EmployeeEmail", data.getEmployeeEmail()); //is me jis ki approve ki ha us employee ki
                                                map.put("MgrEmail", Email); //ye manager ki apni email arhe hai isme
                                                return map;
                                            }
                                        };
                                        req1.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 5,
                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)); //This is added bcz req is hitting twice.

                                        //RequestQueue requestQueue = Volley.newRequestQueue(context);
                                        requestQueue.add(req1);

                                    } else {
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context, error.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("id", data.getId());
                        map.put("status", "2");
                        return map;
                    }
                };
                requestQueue.add(req);
            }
        });

        viewHolder.btn_Reject.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                final RequestQueue queue = Volley.newRequestQueue(context);
                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                progressDialog.setCancelable(false);
                String URL = "http://" + LoginActivity.IP + "/LeaveApi/ShortLeaveUpdateStatus.php";
                StringRequest req = new StringRequest(Request.Method.POST, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String msg = jsonObject.getString("msg");
                                    if (msg.equals("success")) {
                                        String URL_1 = "http://" + LoginActivity.IP + "/LeaveApi/RejectShortLeaveEmail.php";
                                        StringRequest req1 = new StringRequest(Request.Method.POST, URL_1,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            String msg = jsonObject.getString("msg");
                                                            if (msg.equals("success")) {
                                                                progressDialog.dismiss();
                                                                context.startActivity(new Intent(context, Admin_HomeActivity.class));
                                                                Toast.makeText(context, "Leave Request Rejected", Toast.LENGTH_LONG).show();
                                                            } else {
                                                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(context, error.getMessage(),
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                }) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String, String> map = new HashMap<String, String>();
                                                map.put("EmployeeNo", data.getEmployeeNo());
                                                map.put("EmployeeTitle", EmployeeTitle);
                                                map.put("EmployeeName", data.getEmpName());
                                                map.put("LeaveType", viewHolder.tvLeaveType.getText().toString());
                                                map.put("timefrom", data.getTimefrom());
                                                map.put("timeto", data.getTimeto());
                                                map.put("totaltime", data.getTotaltime());
                                                map.put("Reason", data.getRemarks());
                                                map.put("ManagerName", EmployeeName);
                                                map.put("EmployeeEmail", data.getEmployeeEmail()); //is me jis ki approve ki ha us employee ki
                                                map.put("MgrEmail", Email); //ye manager ki apni email arhe hai isme
                                                return map;
                                            }
                                        };
                                        req1.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 5,
                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)); //This is added bcz req is hitting twice.

                                        //RequestQueue requestQueue = Volley.newRequestQueue(context);
                                        queue.add(req1);

                                    } else {
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context, error.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("id", data.getId());
                        map.put("status", "3");
                        return map;
                    }
                };
                queue.add(req);
            }
        });

    }

    @Override
    public int getItemCount() {
        return employeeLeaveData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView EmpImage;
        TextView tvEmpName;
        TextView tvEmpDetails, tvLeaveType, tvTotalTime, tvRemarks, tvStatus;
        Button btn_Approve, btn_Reject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            EmpImage = itemView.findViewById(R.id.EmpImage);
            tvEmpName = itemView.findViewById(R.id.tvEmpName);
            tvEmpDetails = itemView.findViewById(R.id.tvEmpDetails);
            tvLeaveType = itemView.findViewById(R.id.tvLeaveType);
            tvTotalTime = itemView.findViewById(R.id.tv_TotalTime);
            tvRemarks = itemView.findViewById(R.id.tvRemarks);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btn_Approve = itemView.findViewById(R.id.btn_Approve);
            btn_Reject = itemView.findViewById(R.id.btn_Reject);
        }
    }
}
