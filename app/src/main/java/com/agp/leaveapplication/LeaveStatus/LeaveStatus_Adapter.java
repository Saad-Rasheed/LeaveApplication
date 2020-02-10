package com.agp.leaveapplication.LeaveStatus;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agp.leaveapplication.R;

import java.util.List;

public class LeaveStatus_Adapter extends RecyclerView.Adapter<LeaveStatus_Adapter.EmployeeViewHolder> {

    private List<Model_LeaveStatus> list;
    private Context context;

    public LeaveStatus_Adapter(List<Model_LeaveStatus> leaveStatus_adapter, Context context) {
        this.list = leaveStatus_adapter;
        this.context = context;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_leaves_status_employee, viewGroup, false);
        return new LeaveStatus_Adapter.EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int i) {
        final Model_LeaveStatus item = list.get(i);
        if (item.getStatus().equals("Pending")) {
            holder.tvStatus.setTextColor(Color.DKGRAY);
        } else if (item.getStatus().equals("Approved")) {
            holder.tvStatus.setTextColor(Color.parseColor("#1A9A55"));
        } else if (item.getStatus().equals("Rejected")) {
            holder.tvStatus.setTextColor(Color.RED);
        }
        holder.tvStatus.setText(item.getStatus());
        String[] f_date = item.getFromdate().split(" ");
        String[] t_date = item.getTodate().split(" ");
        holder.tvDate.setText("Date: From " + f_date[0] + " to " + t_date[0]);
        holder.tvLeaveType.setText("Leave Type: " + item.getLeaveCode());
        holder.tvTotalDays.setText("Total Days: " + item.getTotaldays());
        holder.tvRemarks.setText("Remarks: " + item.getRemarks());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvLeaveType, tvTotalDays, tvRemarks, tvStatus;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLeaveType = itemView.findViewById(R.id.tvLeaveType);
            tvTotalDays = itemView.findViewById(R.id.tvTotalDays);
            tvRemarks = itemView.findViewById(R.id.tvRemarks);

        }
    }
}


