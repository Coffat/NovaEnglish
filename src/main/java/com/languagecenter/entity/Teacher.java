package com.languagecenter.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Teacher")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TeacherID")
    private int id;

    @Column(name = "FullName")
    private String fullName;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Email")
    private String email;

    @Column(name = "Specialty")
    private String specialty;

    @Column(name = "HireDate")
    private LocalDate hireDate;

    @Column(name = "Status")
    private String status;

    public Teacher() {
    }

    public Teacher(int id, String fullName, String phone, String email, String specialty, LocalDate hireDate,
            String status) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.specialty = specialty;
        this.hireDate = hireDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
