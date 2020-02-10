package com.agp.leaveapplication.Admin.ApprovalSection.LeavesBottomTab.Leaves;

public class Model_Leaves {

    private String EmployeeNo;
    private String Id;
    private String EmpImage;
    private String EmpName;
    private String LeaveCode;
    private String fromdate;
    private String todate;
    private String remarks;
    private String status;
    private String totaldays;
    private String EmployeeEmail;
    private String RequestTimestamp;

    public Model_Leaves(String employeeNo, String id, String empName, String leaveCode, String fromdate, String todate, String remarks, String status, String totaldays, String employeeEmail, String requestTimestamp) {
        EmployeeNo = employeeNo;
        Id = id;
        EmpName = empName;
        LeaveCode = leaveCode;
        this.fromdate = fromdate;
        this.todate = todate;
        this.remarks = remarks;
        this.status = status;
        this.totaldays = totaldays;
        EmployeeEmail = employeeEmail;
        RequestTimestamp = requestTimestamp;
    }

    public String getEmployeeNo() {
        return EmployeeNo;
    }

    public String getId() {
        return Id;
    }

    public String getEmpImage() {
        return EmpImage;
    }

    public String getEmpName() {
        return EmpName;
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

    public String getRemarks() {
        return remarks;
    }

    public String getStatus() {
        return status;
    }

    public String getTotaldays() {
        return totaldays;
    }

    public String getEmployeeEmail() {
        return EmployeeEmail;
    }

    public String getRequestTimestamp() {
        return RequestTimestamp;
    }
}
