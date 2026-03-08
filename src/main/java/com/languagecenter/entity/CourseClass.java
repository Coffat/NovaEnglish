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

    public CourseClass() {
    }

    public CourseClass(int id, String className, Course course, Teacher teacher, LocalDate startDate, LocalDate endDate,
            int maxStudent, String status) {
        this.id = id;
        this.className = className;
        this.course = course;
        this.teacher = teacher;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxStudent = maxStudent;
        this.status = status;
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
}
