package com.languagecenter.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CourseID")
    private int id;

    @Column(name = "CourseName")
    private String name;

    @Column(name = "Description")
    private String description;

    @Column(name = "Level")
    private String level;

    @Column(name = "Duration")
    private int duration;

    @Column(name = "Fee")
    private BigDecimal fee;

    @Column(name = "Status")
    private String status;

    public Course() {
    }

    public Course(int id, String name, String description, String level, int duration, BigDecimal fee, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.level = level;
        this.duration = duration;
        this.fee = fee;
        this.status = status;
    }

    public static class Builder {
        private int id;
        private String name;
        private String description;
        private String level;
        private int duration;
        private BigDecimal fee;
        private String status;

        public Builder id(int id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder level(String level) { this.level = level; return this; }
        public Builder duration(int duration) { this.duration = duration; return this; }
        public Builder fee(BigDecimal fee) { this.fee = fee; return this; }
        public Builder status(String status) { this.status = status; return this; }

        public Course build() {
            Course course = new Course();
            course.setId(id);
            course.setName(name);
            course.setDescription(description);
            course.setLevel(level);
            course.setDuration(duration);
            course.setFee(fee);
            course.setStatus(status);
            return course;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
