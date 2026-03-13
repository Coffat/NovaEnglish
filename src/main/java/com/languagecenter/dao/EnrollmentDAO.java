package com.languagecenter.dao;

import com.languagecenter.entity.Enrollment;
import com.languagecenter.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.ArrayList;

public class EnrollmentDAO {

    private static EnrollmentDAO instance;

    private EnrollmentDAO() {}

    public static EnrollmentDAO getInstance() {
        if (instance == null) {
            instance = new EnrollmentDAO();
        }
        return instance;
    }


    public List<Enrollment> getAllEnrollments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Enrollment", Enrollment.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addEnrollment(Enrollment enrollment) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(enrollment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void updateEnrollment(Enrollment enrollment) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(enrollment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteEnrollment(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Enrollment enrollment = session.get(Enrollment.class, id);
            if (enrollment != null) {
                session.remove(enrollment);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public Enrollment getEnrollmentById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Enrollment.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Enrollment> getEnrollmentsByClassId(int classId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Enrollment e WHERE e.courseClass.id = :classId", Enrollment.class)
                    .setParameter("classId", classId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
