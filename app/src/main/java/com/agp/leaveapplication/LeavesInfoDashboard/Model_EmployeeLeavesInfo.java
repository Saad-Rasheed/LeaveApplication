package com.agp.leaveapplication.LeavesInfoDashboard;

public class Model_EmployeeLeavesInfo {

    private String leave_name;
    private String leave_qty;
    private String leave_availed;
    private String leave_balance;

    public Model_EmployeeLeavesInfo(String leave_name, String leave_qty, String leave_availed, String leave_balance) {
        this.leave_name = leave_name;
        this.leave_qty = leave_qty;
        this.leave_availed = leave_availed;
        this.leave_balance = leave_balance;
    }

    public String getLeave_name() {
        return leave_name;
    }

    public String getLeave_qty() {
        return leave_qty;
    }

    public String getLeave_availed() {
        return leave_availed;
    }

    public String getLeave_balance() {
        return leave_balance;
    }
}
