package com.agp.leaveapplication.Admin.ApprovalSection.LeavesBottomTab.ShortLeaves;

public class Model_ShortLeave {

    private String EmployeeNo;
    private String Id;
    private String EmpImage;
    private String EmpName;
    private String LeaveType;
    private String timefrom;
    private String timeto;
    private String remarks;
    private String status;
    private String totaltime;
    private String EmployeeEmail;


    public Model_ShortLeave(String employeeNo, String id, String empName, String leaveType, String timefrom, String timeto, String remarks, String status, String totaltime, String employeeEmail) {
        EmployeeNo = employeeNo;
        Id = id;
        EmpName = empName;
        LeaveType = leaveType;
        this.timefrom = timefrom;
        this.timeto = timeto;
        this.remarks = remarks;
        this.status = status;
        this.totaltime = totaltime;
        EmployeeEmail = employeeEmail;
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

    public String getLeaveType() {
        return LeaveType;
    }

    public String getTimefrom() {
        return timefrom;
    }

    public String getTimeto() {
        return timeto;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getStatus() {
        return status;
    }

    public String getTotaltime() {
        return totaltime;
    }

    public String getEmployeeEmail() {
        return EmployeeEmail;
    }
}
