package com.languagecenter;

import com.languagecenter.entity.*;
import com.languagecenter.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SeedData {
    private static final Random random = new Random();

    public static void main(String[] args) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            
            System.out.println("Clearing old data...");
            // Clear existing data (in order of FK constraints)
            session.createMutationQuery("DELETE FROM Attendance").executeUpdate();
            session.createMutationQuery("DELETE FROM Payment").executeUpdate();
            session.createMutationQuery("DELETE FROM Enrollment").executeUpdate();
            session.createMutationQuery("DELETE FROM Schedule").executeUpdate();
            session.createMutationQuery("DELETE FROM CourseClass").executeUpdate();
            session.createMutationQuery("DELETE FROM Course").executeUpdate();
            session.createMutationQuery("DELETE FROM Teacher").executeUpdate();
            session.createMutationQuery("DELETE FROM UserAccount").executeUpdate();
            session.createMutationQuery("DELETE FROM Student").executeUpdate();
            
            System.out.println("Adding users...");
            // Add User Accounts
            addUser(session, "admin", "123456", "ADMIN");
            addUser(session, "staff", "123456", "STAFF");
            
            System.out.println("Adding teachers...");
            List<Teacher> teachers = new ArrayList<>();
            teachers.add(createTeacher(session, "Nguyễn Văn Hùng", "hung.nv@language.edu.vn", "0912123456", "IELTS Expert", 5));
            teachers.add(createTeacher(session, "Trần Thị Lan", "lan.tt@language.edu.vn", "0912123457", "TOEIC Specialist", 3));
            teachers.add(createTeacher(session, "Lê Văn Nam", "nam.lv@language.edu.vn", "0912123458", "Business English", 4));
            teachers.add(createTeacher(session, "Phạm Minh Tuấn", "tuan.pm@language.edu.vn", "0912123459", "English for Kids", 2));
            teachers.add(createTeacher(session, "Hoàng Mỹ Linh", "linh.hm@language.edu.vn", "0912123460", "TOEFL Specialist", 6));

            System.out.println("Adding courses...");
            List<Course> courses = new ArrayList<>();
            courses.add(createCourse(session, "IELTS Masterclass (Band 7.5+)", "Advanced preparation for high-impact results.", "Advanced", 60, new BigDecimal("8500000")));
            courses.add(createCourse(session, "TOEIC Intensive 750+", "Focus on listening and reading skills.", "Intermediate", 45, new BigDecimal("4200000")));
            courses.add(createCourse(session, "Business Communication", "Professional English for modern workplace.", "Advanced", 40, new BigDecimal("6000000")));
            courses.add(createCourse(session, "General English A2", "Basic communication for beginners.", "Beginner", 30, new BigDecimal("3500000")));
            courses.add(createCourse(session, "Placement Test", "Assessment course for level placement.", "Beginner", 1, new BigDecimal("200000")));

            System.out.println("Adding classes...");
            // On-going classes
            CourseClass cl1 = createClass(session, "IELTS-2026-Q1", courses.get(0), teachers.get(0), LocalDate.of(2026, 1, 5), "2-4-6", LocalTime.of(18, 30), LocalTime.of(20, 30), "On-going", 15);
            CourseClass cl2 = createClass(session, "TOEIC-750-N2", courses.get(1), teachers.get(1), LocalDate.of(2026, 2, 10), "3-5-7", LocalTime.of(19, 0), LocalTime.of(21, 0), "On-going", 20);
            
            // Completed classes
            CourseClass cl3 = createClass(session, "IELTS-2025-WINTER", courses.get(0), teachers.get(0), LocalDate.of(2025, 10, 1), "2-4-6", LocalTime.of(8, 0), LocalTime.of(10, 0), "Completed", 12);
            
            // Scheduled (Future) classes
            CourseClass cl4 = createClass(session, "BUS-ENG-SPRING", courses.get(2), teachers.get(2), LocalDate.of(2026, 4, 1), "2-4-6", LocalTime.of(18, 0), LocalTime.of(20, 0), "Opening", 15);

            System.out.println("Adding students, enrollments, payments and schedules...");
            String[] firstNames = {"Minh", "Thu", "Tuấn", "Linh", "Hùng", "Lan", "Nam", "Mai", "Anh", "Đức"};
            String[] lastNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Đặng", "Bùi"};

            for (int i = 0; i < 40; i++) {
                String fullName = lastNames[random.nextInt(10)] + " " + firstNames[random.nextInt(10)] + " (STU" + (1000 + i) + ")";
                Student s = Student.builder()
                    .fullName(fullName)
                    .dateOfBirth(LocalDate.of(1995 + random.nextInt(15), 1 + random.nextInt(12), 1 + random.nextInt(28)))
                    .gender(random.nextBoolean() ? "Male" : "Female")
                    .phone("09" + (10000000 + random.nextInt(90000000)))
                    .email("student" + i + "@example.com")
                    .address("District " + (1 + random.nextInt(12)) + ", HCM City")
                    .registrationDate(LocalDate.now().minusMonths(1 + random.nextInt(12)))
                    .status("Active")
                    .build();
                session.persist(s);

                // Enroll students into classes randomly
                CourseClass targetClass = null;
                if (i < 15) targetClass = cl1;
                else if (i < 30) targetClass = cl2;
                else if (i < 35) targetClass = cl3;
                else targetClass = cl4;

                Enrollment e = createEnrollment(session, s, targetClass, i < 35 ? (i < 30 ? "Ongoing" : "Completed") : "Enrolled");
                
                // Add payments for most students
                if (random.nextDouble() > 0.1) {
                    createPayment(session, s, e, targetClass.getCourse().getFee());
                }
            }

            // Generate initial schedules and some attendance for ongoing classes
            generateClassInfrastructure(session, cl1, 101, true);
            generateClassInfrastructure(session, cl2, 102, true);
            generateClassInfrastructure(session, cl3, 103, false); // No attendance for completed classes in this seed
            generateClassInfrastructure(session, cl4, 201, false);

            transaction.commit();
            System.out.println("SUCCESS: Deep Data Seeding Completed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addUser(Session session, String user, String pass, String role) {
        UserAccount ua = new UserAccount();
        ua.setUsername(user);
        ua.setPassword(pass);
        ua.setRole(role);
        session.persist(ua);
    }

    private static Teacher createTeacher(Session session, String name, String email, String phone, String specialty, int years) {
        Teacher t = Teacher.builder()
            .fullName(name)
            .email(email)
            .phone(phone)
            .specialty(specialty)
            .hireDate(LocalDate.now().minusYears(years))
            .status("Active")
            .build();
        session.persist(t);
        return t;
    }

    private static Course createCourse(Session session, String name, String desc, String level, int duration, BigDecimal fee) {
        Course c = Course.builder()
            .name(name)
            .description(desc)
            .level(level)
            .duration(duration)
            .fee(fee)
            .status("Active")
            .build();
        session.persist(c);
        return c;
    }

    private static CourseClass createClass(Session session, String name, Course course, Teacher teacher, LocalDate start, String pattern, LocalTime st, LocalTime et, String status, int max) {
        CourseClass cc = new CourseClass();
        cc.setClassName(name);
        cc.setCourse(course);
        cc.setTeacher(teacher);
        cc.setStartDate(start);
        cc.setSchedulePattern(pattern);
        cc.setStartTime(st);
        cc.setEndTime(et);
        cc.setEndDate(calculateEndDate(start, course.getDuration(), pattern));
        cc.setMaxStudent(max);
        cc.setStatus(status);
        session.persist(cc);
        return cc;
    }

    private static Enrollment createEnrollment(Session session, Student s, CourseClass cc, String status) {
        Enrollment e = new Enrollment();
        e.setStudent(s);
        e.setCourseClass(cc);
        e.setEnrollmentDate(cc.getStartDate().minusWeeks(1));
        e.setStatus(status);
        e.setResult(cc.getStatus().equals("Completed") ? 7.0f + random.nextFloat() * 2.5f : 0.0f);
        session.persist(e);
        return e;
    }

    private static void createPayment(Session session, Student s, Enrollment e, BigDecimal amount) {
        Payment p = new Payment();
        p.setStudent(s);
        p.setEnrollment(e);
        p.setAmount(amount);
        p.setPaymentDate(e.getEnrollmentDate().plusDays(1));
        p.setPaymentMethod(random.nextBoolean() ? "Transfer" : "Cash");
        p.setStatus("Completed");
        session.persist(p);
    }

    private static LocalDate calculateEndDate(LocalDate start, int duration, String pattern) {
        LocalDate end = start;
        int count = 0;
        while (count < duration) {
            if (isScheduledDay(end, pattern)) {
                count++;
                if (count == duration) break;
            }
            end = end.plusDays(1);
        }
        return end;
    }

    private static boolean isScheduledDay(LocalDate date, String pattern) {
        java.time.DayOfWeek dow = date.getDayOfWeek();
        if ("2-4-6".equals(pattern)) {
            return dow == java.time.DayOfWeek.MONDAY || dow == java.time.DayOfWeek.WEDNESDAY || dow == java.time.DayOfWeek.FRIDAY;
        } else if ("3-5-7".equals(pattern)) {
            return dow == java.time.DayOfWeek.TUESDAY || dow == java.time.DayOfWeek.THURSDAY || dow == java.time.DayOfWeek.SATURDAY;
        }
        return false;
    }

    private static void generateClassInfrastructure(Session session, CourseClass cls, int roomId, boolean generateAttendance) {
        LocalDate current = cls.getStartDate();
        LocalDate end = cls.getEndDate();
        List<Enrollment> enrollments = session.createQuery("FROM Enrollment WHERE courseClass.id = :id", Enrollment.class)
                .setParameter("id", cls.getId()).list();

        while (!current.isAfter(end)) {
            if (isScheduledDay(current, cls.getSchedulePattern())) {
                Schedule s = new Schedule();
                s.setCourseClass(cls);
                s.setScheduleDate(current);
                s.setStartTime(cls.getStartTime());
                s.setEndTime(cls.getEndTime());
                s.setRoomId(roomId);
                session.persist(s);

                // If class is on-going and session is in the past, add some attendance
                if (generateAttendance && current.isBefore(LocalDate.now())) {
                    for (Enrollment e : enrollments) {
                        if (random.nextDouble() > 0.05) { // 95% attendance
                            Attendance a = new Attendance();
                            a.setStudent(e.getStudent());
                            a.setCourseClass(cls);
                            a.setAttendanceDate(current);
                            a.setStatus(random.nextDouble() > 0.1 ? "Present" : "Late");
                            session.persist(a);
                        } else {
                            Attendance a = new Attendance();
                            a.setStudent(e.getStudent());
                            a.setCourseClass(cls);
                            a.setAttendanceDate(current);
                            a.setStatus("Absent");
                            session.persist(a);
                        }
                    }
                }
            }
            current = current.plusDays(1);
        }
    }
}
