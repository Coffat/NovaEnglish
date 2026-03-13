package com.languagecenter.dao;

import com.languagecenter.entity.Teacher;
import com.languagecenter.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.ArrayList;

public class TeacherDAO {

    private static TeacherDAO instance;

    private TeacherDAO() {}

    public static TeacherDAO getInstance() {
        if (instance == null) {
            instance = new TeacherDAO();
        }
        return instance;
    }


    public List<Teacher> getAllTeachers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Teacher", Teacher.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addTeacher(Teacher teacher) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(teacher);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void updateTeacher(Teacher teacher) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(teacher);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteTeacher(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Teacher teacher = session.get(Teacher.class, id);
            if (teacher != null) {
                session.remove(teacher);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public Teacher getTeacherById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Teacher.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
