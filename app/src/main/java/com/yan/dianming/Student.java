package com.yan.dianming;

/**
 * Created by Yan on 2016/10/30.
 */
public class Student {
    private String stu_no;
    private String stu_name;
    private String stu_class;
    private double score;
    private double bad;

    public Student(String stu_no, String stu_name, String stu_class, double score, double bad) {
        this.stu_no = stu_no;
        this.stu_name = stu_name;
        this.stu_class = stu_class;
        this.score = score;
        this.bad = bad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        return stu_no != null ? stu_no.equals(student.stu_no) : student.stu_no == null;

    }

    @Override
    public int hashCode() {
        return stu_no != null ? stu_no.hashCode() : 0;
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

    public double getBad() {
        return bad;
    }

    public void setBad(double bad) {
        this.bad = bad;
    }
}
