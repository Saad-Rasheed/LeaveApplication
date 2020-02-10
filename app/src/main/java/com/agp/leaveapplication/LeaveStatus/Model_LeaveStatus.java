package com.agp.leaveapplication.LeaveStatus;

public class Model_LeaveStatus {

    private String LeaveCode;
    private String fromdate;
    private String todate;
    private String totaldays;
    private String remarks;
    private String status;

    public Model_LeaveStatus(String leaveCode, String fromdate, String todate, String totaldays, String remarks, String status) {
        LeaveCode = leaveCode;
        this.fromdate = fromdate;
        this.todate = todate;
        this.totaldays = totaldays;
        this.remarks = remarks;
        this.status = status;
    }

    public String getLeaveCode() {
        return LeaveCode;
    }

    public String getFromdate() {
        return fromdate;
    }

    public String getTodate() {
        return todate;
    }

    public String getTotaldays() {
        return totaldays;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getStatus() {
        return status;
    }
}
