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

    public static class Builder {
        private int id;
        private String fullName;
        private String phone;
        private String email;
        private String specialty;
        private LocalDate hireDate;
        private String status;

        public Builder id(int id) { this.id = id; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder specialty(String specialty) { this.specialty = specialty; return this; }
        public Builder hireDate(LocalDate hireDate) { this.hireDate = hireDate; return this; }
        public Builder status(String status) { this.status = status; return this; }

        public Teacher build() {
            Teacher teacher = new Teacher();
            teacher.setId(id);
            teacher.setFullName(fullName);
            teacher.setPhone(phone);
            teacher.setEmail(email);
            teacher.setSpecialty(specialty);
            teacher.setHireDate(hireDate);
            teacher.setStatus(status);
            return teacher;
        }
    }

    public static Builder builder() {
        return new Builder();
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
