package com.example.universalyogaadmin;

public class ClassInstance {
    private int instanceId;
    private int classId;
    private String date;
    private String teacherName;
    private String comments;

    public ClassInstance(int classId, String date, String teacherName, String comments) {
        this.classId = classId;
        this.date = date;
        this.teacherName = teacherName;
        this.comments = comments;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}