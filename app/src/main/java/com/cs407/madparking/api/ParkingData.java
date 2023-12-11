package com.cs407.madparking.api;

import java.time.LocalDate;
import java.time.LocalTime;

public class ParkingData {
    private int id; // 数据库记录的唯一标识符
    private LocalDate date; // 停车日期
    private String garageName; // 车库名称
    private LocalTime startTime; // 开始时间
    private LocalTime endTime; // 结束时间（可选）

    // 构造函数
    public ParkingData() {
    }

    // 带参数的构造函数（可选）
    public ParkingData(LocalDate date, String garageName, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.garageName = garageName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getGarageName() {
        return garageName;
    }

    public void setGarageName(String garageName) {
        this.garageName = garageName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }


    public double totalHours() {
        return (endTime.toSecondOfDay() - startTime.toSecondOfDay()) / 3600.0;
    }

    // 重写toString方法以便于调试（可选）
    @Override
    public String toString() {
        return "ParkingData{" +
                "id=" + id +
                ", date=" + date +
                ", garageName='" + garageName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}