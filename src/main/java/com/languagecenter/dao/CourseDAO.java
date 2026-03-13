package com.languagecenter.dao;

import com.languagecenter.entity.Course;
import com.languagecenter.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.ArrayList;

public class CourseDAO {

    private static CourseDAO instance;

    private CourseDAO() {}

    public static CourseDAO getInstance() {
        if (instance == null) {
            instance = new CourseDAO();
        }
        return instance;
    }


    public List<Course> getAllCourses() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Course", Course.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addCourse(Course course) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(course);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void updateCourse(Course course) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(course);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteCourse(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Course course = session.get(Course.class, id);
            if (course != null) {
                session.remove(course);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public Course getCourseById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Course.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
