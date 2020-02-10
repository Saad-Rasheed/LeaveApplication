package com.agp.leaveapplication.Admin.ApprovalSection.LeavesBottomTab.Leaves;

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

public class Adapter_Leaves extends RecyclerView.Adapter<Adapter_Leaves.EmployeeViewHolder> {

    private List<Model_Leaves> employeeData;
    private Context context;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private String EmployeeTitle, EmployeeName, Email, MgrEmail, CCEmail;

    public Adapter_Leaves(List<Model_Leaves> employeeData, Context context) {
        this.employeeData = employeeData;
        this.context = context;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_admin_panel, viewGroup, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EmployeeViewHolder holder, int i) {

        sharedPreferences = context.getSharedPreferences("MyPre", Context.MODE_PRIVATE);
        EmployeeTitle = sharedPreferences.getString("EmployeeTitle", "");
        EmployeeName = sharedPreferences.getString("EmployeeName", "");
        Email = sharedPreferences.getString("Email", "");
        CCEmail = sharedPreferences.getString("CCEmail", "");

        final Model_Leaves data = employeeData.get(i);
        holder.tvEmpName.setText(data.getEmpName());
        holder.tvEmpDetails.setText("from " + data.getEmpName() + " on " + data.getFromdate() + " to " + data.getTodate());
        holder.tvTotalDays.setText("Total Days: " + data.getTotaldays());
        holder.tvRemarks.setText("Reason: " + data.getRemarks());
        holder.tvRequestTimestamp.setText("Request Raised Time: " + data.getRequestTimestamp());
        String status = data.getStatus();
        if (status.equals("1")) {
            holder.tvStatus.setText("Status: Pending");

        } else if (status.equals("2")) {
            holder.tvStatus.setText("Status: Approved");
            holder.btn_Approve.setVisibility(View.GONE);
            holder.btn_Reject.setVisibility(View.GONE);

        } else {
            holder.tvStatus.setText("Status: Rejected");
            holder.btn_Approve.setVisibility(View.GONE);
            holder.btn_Reject.setVisibility(View.GONE);
        }

        if (data.getLeaveCode().equals("CL")) {
            holder.tvLeaveType.setText("Leave Type: Casual Leave");
        } else if (data.getLeaveCode().equals("SL")) {
            holder.tvLeaveType.setText("Leave Type: Sick Leave");
        } else if (data.getLeaveCode().equals("LW")) {
            holder.tvLeaveType.setText("Leave Type: Leave Without Pay");
        } else if (data.getLeaveCode().equals("PL")) {
            holder.tvLeaveType.setText("Leave Type: Privilege Leave");
        } else if (data.getLeaveCode().equals("HL")) {
            holder.tvLeaveType.setText("Leave Type: Hajj Leave");
        } else if (data.getLeaveCode().equals("ML")) {
            holder.tvLeaveType.setText("Leave Type: Maternity Leave");
        } else if (data.getLeaveCode().equals("MW")) {
            holder.tvLeaveType.setText("Leave Type: Maternity Leave Without Pay");
        }

        holder.btn_Approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final RequestQueue requestQueue = Volley.newRequestQueue(context);
                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                progressDialog.setCancelable(false);
                final String URL = "http://" + LoginActivity.IP + "/LeaveApi/updateStatus.php";
                StringRequest req = new StringRequest(Request.Method.POST, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String msg = jsonObject.getString("msg");
                                    if (msg.equals("success")) {
                                        String URL_1 = "http://" + LoginActivity.IP + "/LeaveApi/sendEmail.php";
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
                                                                Toast.makeText(context, "Leave Request Accepted", Toast.LENGTH_LONG).show();
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
                                                map.put("LeaveType", holder.tvLeaveType.getText().toString());
                                                map.put("FromDate", data.getFromdate());
                                                map.put("ToDate", data.getTodate());
                                                map.put("Totaldays", data.getTotaldays());
                                                map.put("Reason", data.getRemarks());
                                                map.put("ManagerName", EmployeeName);
                                                map.put("EmployeeEmail", data.getEmployeeEmail()); //is me jis ki approve ki ha us employee ki
                                                map.put("MgrEmail", Email); //ye manager ki apni email arhe hai isme
                                                map.put("CCEmail", CCEmail); //ye agr manager k ccemail me koe assign ha to
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

        holder.btn_Reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final RequestQueue queue = Volley.newRequestQueue(context);
                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                progressDialog.setCancelable(false);
                String URL = "http://" + LoginActivity.IP + "/LeaveApi/LeaveUpdateStatus.php";
                StringRequest req = new StringRequest(Request.Method.POST, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String msg = jsonObject.getString("msg");
                                    if (msg.equals("success")) {
                                        String URL_1 = "http://" + LoginActivity.IP + "/LeaveApi/RejectLeaveSendEmail.php";
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
                                                map.put("LeaveType", holder.tvLeaveType.getText().toString());
                                                map.put("FromDate", data.getFromdate());
                                                map.put("ToDate", data.getTodate());
                                                map.put("Totaldays", data.getTotaldays());
                                                map.put("Reason", data.getRemarks());
                                                map.put("ManagerName", EmployeeName);
                                                map.put("EmployeeEmail", data.getEmployeeEmail()); //is me jis ki approve ki ha us employee ki
                                                map.put("MgrEmail", Email); //ye manager ki apni email arhe hai isme
                                                //map.put("CCEmail", CCEmail); //ye agr manager k ccemail me koe assign ha to
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
        return employeeData.size();
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {

        ImageView EmpImage;
        TextView tvEmpName;
        TextView tvEmpDetails, tvLeaveType, tvTotalDays, tvRemarks, tvStatus, tvRequestTimestamp;
        Button btn_Approve, btn_Reject;

        public EmployeeViewHolder(View itemView) {
            super(itemView);

            EmpImage = itemView.findViewById(R.id.EmpImage);
            tvEmpName = itemView.findViewById(R.id.tvEmpName);
            tvEmpDetails = itemView.findViewById(R.id.tvEmpDetails);
            tvLeaveType = itemView.findViewById(R.id.tvLeaveType);
            tvTotalDays = itemView.findViewById(R.id.tvTotalDays);
            tvRemarks = itemView.findViewById(R.id.tvRemarks);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRequestTimestamp = itemView.findViewById(R.id.tvRequestTimestamp);
            btn_Approve = itemView.findViewById(R.id.btn_Approve);
            btn_Reject = itemView.findViewById(R.id.btn_Reject);
        }
    }
}
