package com.example.universalyogaadmin;

public class ClassItem {
    private String className;
    private String classType;
    private String teacher;
    private String time;
    private String capacity;
    private String duration;
    private String price;
    private String description;

    public ClassItem(String className, String classType, String teacher, String time, String capacity, String duration, String price, String description) {
        this.className = className;
        this.classType = classType;
        this.teacher = teacher;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public String getClassType() {
        return classType;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getTime() {
        return time;
    }

    public String getCapacity() {
        return capacity;
    }

    public String getDuration() {
        return duration;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
}