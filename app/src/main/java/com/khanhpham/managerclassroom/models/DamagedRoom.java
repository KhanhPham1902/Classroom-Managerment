package com.khanhpham.managerclassroom.models;

public class DamagedRoom {
    private String room;
    private String report;
    private String time_report;
    private String date_study;
    private String time_study;

    public DamagedRoom(String room, String report, String time_report, String date_study, String time_study) {
        this.room = room;
        this.report = report;
        this.time_report = time_report;
        this.date_study = date_study;
        this.time_study = time_study;
    }
    public DamagedRoom(){
    }
    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getTime_report() {
        return time_report;
    }

    public void setTime_report(String time_report) {
        this.time_report = time_report;
    }

    public String getDate_study() {
        return date_study;
    }

    public void setDate_study(String date_study) {
        this.date_study = date_study;
    }

    public String getTime_study() {
        return time_study;
    }

    public void setTime_study(String time_study) {
        this.time_study = time_study;
    }
}
