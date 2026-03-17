package com.languagecenter.dao;

import com.languagecenter.entity.CourseClass;
import com.languagecenter.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.ArrayList;

public class CourseClassDAO {

    private static CourseClassDAO instance;

    private CourseClassDAO() {}

    public static CourseClassDAO getInstance() {
        if (instance == null) {
            instance = new CourseClassDAO();
        }
        return instance;
    }


    public List<CourseClass> getAllClasses() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM CourseClass", CourseClass.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addClass(CourseClass courseClass) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(courseClass);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void updateClass(CourseClass courseClass) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(courseClass);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteClass(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            CourseClass courseClass = session.get(CourseClass.class, id);
            if (courseClass != null) {
                session.remove(courseClass);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public CourseClass getClassById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(CourseClass.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<CourseClass> getClassesByCourseId(int courseId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM CourseClass WHERE course.id = :courseId", CourseClass.class)
                    .setParameter("courseId", courseId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
