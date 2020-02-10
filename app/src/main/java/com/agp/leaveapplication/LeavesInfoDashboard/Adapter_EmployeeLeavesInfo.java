package com.agp.leaveapplication.LeavesInfoDashboard;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agp.leaveapplication.LeaveStatus.Model_LeaveStatus;
import com.agp.leaveapplication.R;

import java.util.List;

public class Adapter_EmployeeLeavesInfo extends RecyclerView.Adapter<Adapter_EmployeeLeavesInfo.ViewHolder> {

    List<Model_EmployeeLeavesInfo> list;
    Context context;

    public Adapter_EmployeeLeavesInfo(List<Model_EmployeeLeavesInfo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaves_info, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        final Model_EmployeeLeavesInfo item = list.get(i);
        if (item.getLeave_availed().equals(item.getLeave_qty())) {
            holder.tv_availed_leaves.setTextColor(Color.RED);
        }
        if(item.getLeave_name().equals("Leave Without Pay")){
            holder.tv_leave_type.setVisibility(View.GONE);
            holder.tv_total_leaves.setVisibility(View.GONE);
            holder.tv_availed_leaves.setVisibility(View.GONE);
            holder.tv_leaves_balance.setVisibility(View.GONE);

        } else if(item.getLeave_name().equals("On Duty")){
            holder.tv_leave_type.setVisibility(View.GONE);
            holder.tv_total_leaves.setVisibility(View.GONE);
            holder.tv_availed_leaves.setVisibility(View.GONE);
            holder.tv_leaves_balance.setVisibility(View.GONE);
        }
        //Model_EmployeeLeavesInfo item = list.get(i);
        holder.tv_leave_type.setText(item.getLeave_name());
        holder.tv_total_leaves.setText(item.getLeave_qty());
        holder.tv_availed_leaves.setText(item.getLeave_availed());
        holder.tv_leaves_balance.setText(item.getLeave_balance());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_leave_type, tv_availed_leaves, tv_total_leaves, tv_leaves_balance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_leave_type = itemView.findViewById(R.id.tv_leave_type);
            tv_total_leaves = itemView.findViewById(R.id.tv_total_leaves);
            tv_availed_leaves = itemView.findViewById(R.id.tv_availed_leaves);
            tv_leaves_balance = itemView.findViewById(R.id.tv_leaves_balance);
        }
    }
}
