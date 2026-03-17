package com.languagecenter.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Class")
public class CourseClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ClassID")
    private int id;

    @Column(name = "ClassName")
    private String className;

    @ManyToOne
    @JoinColumn(name = "CourseID")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "TeacherID")
    private Teacher teacher;

    @Column(name = "StartDate")
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

    @Column(name = "MaxStudent")
    private int maxStudent;

    @Column(name = "Status")
    private String status;

    @Column(name = "SchedulePattern")
    private String schedulePattern; // "2-4-6" or "3-5-7"

    @Column(name = "StartTime")
    private java.time.LocalTime startTime;

    @Column(name = "EndTime")
    private java.time.LocalTime endTime;

    public CourseClass() {
    }

    public CourseClass(int id, String className, Course course, Teacher teacher, LocalDate startDate, LocalDate endDate,
            int maxStudent, String status, String schedulePattern, java.time.LocalTime startTime, java.time.LocalTime endTime) {
        this.id = id;
        this.className = className;
        this.course = course;
        this.teacher = teacher;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxStudent = maxStudent;
        this.status = status;
        this.schedulePattern = schedulePattern;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getMaxStudent() {
        return maxStudent;
    }

    public void setMaxStudent(int maxStudent) {
        this.maxStudent = maxStudent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSchedulePattern() {
        return schedulePattern;
    }

    public void setSchedulePattern(String schedulePattern) {
        this.schedulePattern = schedulePattern;
    }

    public java.time.LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(java.time.LocalTime startTime) {
        this.startTime = startTime;
    }

    public java.time.LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(java.time.LocalTime endTime) {
        this.endTime = endTime;
    }
}
