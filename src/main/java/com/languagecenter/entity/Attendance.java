package com.languagecenter.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AttendanceID")
    private int id;

    @ManyToOne
    @JoinColumn(name = "StudentID")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "ClassID")
    private CourseClass courseClass;

    @Column(name = "Date")
    private LocalDate attendanceDate;

    @Column(name = "Status")
    private String status;

    public Attendance() {
    }

    public Attendance(int id, Student student, CourseClass courseClass, LocalDate attendanceDate, String status) {
        this.id = id;
        this.student = student;
        this.courseClass = courseClass;
        this.attendanceDate = attendanceDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public CourseClass getCourseClass() {
        return courseClass;
    }

    public void setCourseClass(CourseClass courseClass) {
        this.courseClass = courseClass;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
