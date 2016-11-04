package com.yan.dianming;

/**
 * Created by Yan on 2016/10/30.
 */
public class Studentdianming {
    private String stu_no;
    private String stu_name;
    private String stu_class;
    private double score;
    private String week;
    private String classno;
    private String status;
    private double badcount;

    public Studentdianming(String stu_no, String stu_name, String stu_class, double score, String week, String classno, String status, double badcount) {
        this.stu_no = stu_no;
        this.stu_name = stu_name;
        this.stu_class = stu_class;
        this.score = score;
        this.week = week;
        this.classno = classno;
        this.status = status;
        this.badcount = badcount;
    }

    public String getStu_no() {
        return stu_no;
    }

    public void setStu_no(String stu_no) {
        this.stu_no = stu_no;
    }

    public String getStu_name() {
        return stu_name;
    }

    public void setStu_name(String stu_name) {
        this.stu_name = stu_name;
    }

    public String getStu_class() {
        return stu_class;
    }

    public void setStu_class(String stu_class) {
        this.stu_class = stu_class;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getClassno() {
        return classno;
    }

    public void setClassno(String classno) {
        this.classno = classno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getBadcount() {
        return badcount;
    }

    public void setBadcount(double badcount) {
        this.badcount = badcount;
    }
}
