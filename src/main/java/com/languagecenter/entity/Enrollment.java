package com.languagecenter.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Enrollment")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EnrollmentID")
    private int id;

    @ManyToOne
    @JoinColumn(name = "StudentID")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "ClassID")
    private CourseClass courseClass;

    @Column(name = "EnrollmentDate")
    private LocalDate enrollmentDate;

    @Column(name = "Status")
    private String status;

    @Column(name = "Result")
    private Float result;

    public Enrollment() {
    }

    public Enrollment(int id, Student student, CourseClass courseClass, LocalDate enrollmentDate, String status, Float result) {
        this.id = id;
        this.student = student;
        this.courseClass = courseClass;
        this.enrollmentDate = enrollmentDate;
        this.status = status;
        this.result = result;
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

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Float getResult() {
        return result;
    }

    public void setResult(Float result) {
        this.result = result;
    }
}
