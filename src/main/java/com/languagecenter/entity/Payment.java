package com.languagecenter.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PaymentID")
    private int id;

    @ManyToOne
    @JoinColumn(name = "StudentID")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "EnrollmentID")
    private Enrollment enrollment;

    @Column(name = "Amount")
    private BigDecimal amount;

    @Column(name = "PaymentDate")
    private LocalDate paymentDate;

    @Column(name = "PaymentMethod")
    private String paymentMethod;

    @Column(name = "Status")
    private String status;

    public Payment() {
    }

    public Payment(int id, Student student, Enrollment enrollment, BigDecimal amount, LocalDate paymentDate,
            String paymentMethod, String status) {
        this.id = id;
        this.student = student;
        this.enrollment = enrollment;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
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

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
