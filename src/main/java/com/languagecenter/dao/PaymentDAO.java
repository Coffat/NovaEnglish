package com.languagecenter.dao;

import com.languagecenter.entity.Payment;
import com.languagecenter.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.ArrayList;

public class PaymentDAO {

    private static PaymentDAO instance;

    private PaymentDAO() {}

    public static PaymentDAO getInstance() {
        if (instance == null) {
            instance = new PaymentDAO();
        }
        return instance;
    }


    public List<Payment> getAllPayments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Payment", Payment.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addPayment(Payment payment) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(payment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void updatePayment(Payment payment) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(payment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deletePayment(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Payment payment = session.get(Payment.class, id);
            if (payment != null) {
                session.remove(payment);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public Payment getPaymentById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Payment.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
