package com.languagecenter;

import com.languagecenter.entity.UserAccount;
import com.languagecenter.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class TestHibernate {
    public static void main(String[] args) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            // 1. Bắt đầu giao dịch
            transaction = session.beginTransaction();

            // 2. Tạo thử một tài khoản Admin
            UserAccount admin = new UserAccount();
            admin.setUsername("admin_test");
            admin.setPassword("123456");

            // 3. Lưu vào DB (Hibernate 6 dùng persist thay cho save)
            session.persist(admin);

            // 4. Commit dữ liệu
            transaction.commit();

            System.out.println("----------------------------------------------");
            System.out.println("✅ KẾT NỐI VÀ LƯU DỮ LIỆU THÀNH CÔNG RỰC RỠ!");
            System.out.println("----------------------------------------------");

        } catch (Exception e) {
            // Rollback TRƯỚC khi session bị đóng
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("❌ Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng session một cách thủ công sau cùng
            if (session != null) {
                session.close();
            }
            // Đóng SessionFactory
            HibernateUtil.shutdown();
        }
    }
}
