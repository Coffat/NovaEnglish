<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=timeGradient&height=250&section=header&text=NOVA%20ENGLISH&fontSize=70&animation=fadeIn&fontAlignY=35&desc=System%20Management%20Application&descAlignY=55&descSize=20" width="100%" />

  # 🌟 Language Center Management System 🌟
  
  <p align="center">
    <b>A modern, robust, and elegant desktop application tailored for English centers.</b><br>
    <i>Brings your management workflow to the next level with a stunning UI and powerful backend.</i>
  </p>

  <p align="center">
    <a href="https://java.com/"><img src="https://img.shields.io/badge/Java-21_LTS-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java 21" /></a>
    <a href="https://hibernate.org/"><img src="https://img.shields.io/badge/Hibernate-6.4-59666C?style=for-the-badge&logo=hibernate&logoColor=white" alt="Hibernate" /></a>
    <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" /></a>
    <a href="https://mysql.com/"><img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" /></a>
    <a href="https://www.formdev.com/flatlaf/"><img src="https://img.shields.io/badge/UI-FlatLaf-4169E1?style=for-the-badge&logo=swiggy&logoColor=white" alt="FlatLaf" /></a>
  </p>
</div>

<br/>

## ✨ Key Features

Our app is meticulously crafted to ensure a smooth, delightful user experience. 

<table>
  <tr>
    <td align="center" width="25%">
      <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Smilies/Man%20Student.png" alt="Student" width="50" />
      <br /><b>Student Management</b>
    </td>
    <td align="center" width="25%">
      <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Smilies/Woman%20Teacher.png" alt="Teacher" width="50" />
      <br /><b>Teacher Profiles</b>
    </td>
    <td align="center" width="25%">
      <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Books.png" alt="Courses" width="50" />
      <br /><b>Course Scheduling</b>
    </td>
    <td align="center" width="25%">
      <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Money%20Bag.png" alt="Payments" width="50" />
      <br /><b>Payment Tracking</b>
    </td>
  </tr>
</table>

- 🎨 **Sleek & Modern UI**: Powered by **FlatLaf**, featuring smooth animations, soft drop-shadows, and scalable SVG icons.
- ⚡ **Real-time Search Filter**: Lightning-fast table filtering right out of the box.
- 🔐 **Robust Security**: Secure login forms, smart state management, and password validation.
- 🗄️ **Hibernate ORM Magic**: Jakarta Persistence fully mapped to seamlessly interact with MySQL 8.
- 📱 **Interactive Side Panels**: Edit entity details smoothly without leaving your context map.

---

## 🚀 Getting Started

Follow these aesthetic steps to launch the system locally.

### 1️⃣ Prerequisites
> Make sure your machine meets these requirements

- **Java JDK 21+** ☕
- **Apache Maven 3.x** 🛠️
- **MySQL Server 8.x** 🐬

### 2️⃣ Clone the Repository
```bash
git clone https://github.com/Coffat/heThongTiengAnh.git
cd heThongTiengAnh
```

### 3️⃣ Setup the Database
Create a new MySQL database and establish the schema. 
*Note: Hibernate uses `update` mode, so tables will be auto-generated based on our JPA entities if they do not exist!*
```sql
CREATE DATABASE language_centerdb;
```

Update your connection credentials in `src/main/resources/hibernate.cfg.xml`:
```xml
<property name="hibernate.connection.username">your_mysql_username</property>
<property name="hibernate.connection.password">your_mysql_password</property>
```

### 4️⃣ Build & Run 🔥
Using Maven to resolve dependencies, compile the project and boot up the magic:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.languagecenter.Main"
```

---

## 💻 Tech Stack Architecture

<br>
<div align="center">
  <img src="https://skillicons.dev/icons?i=java,mysql,maven,hibernate,git,github" />
</div>
<br>

<details open>
  <summary><b>View Module Breakdowns</b></summary>
  <br/>
  
  - **Frontend / UI**: `Java Swing` augmented with `FlatLaf` for macOS/Windows native-like soft UI. Utilizes `MigLayout` for complex responsive grids.
  - **Backend Layer**: Java 21 LTS standard backend logic, fully event-driven UI listeners.
  - **Data Integration**: DAO Implementation pattern utilizing `Hibernate 6` `Session` operations. Entity mapping explicitly binds models to MySQL rows.
</details>

---

## 🎨 Design Showcase

Our intuitive UI emphasizes layout hierarchy and distinct micro-interactions. From rounded corners to custom color palettes inspired by TailwindCSS (*Indigo, Slate, White*).

<div align="center">
   <i>Screenshot placeholders (Upload real screenshots to your repo and update here)</i><br>
  <img src="https://placehold.co/800x450/6366F1/FFFFFF/png?text=Dashboard+Interface" width="48%" />
  <img src="https://placehold.co/800x450/1E293B/FFFFFF/png?text=Side-panel+Animations" width="48%" />
</div>

---

## 🤝 Contributing

We welcome contributions to make **Nova English** even better! 

1. `Fork` the repository
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'feat: Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=timeGradient&height=100&section=footer" width="100%" />
  <br/>
  <p>Crafted with ❤️ by Vũ Toàn Thắng</p>
</div>
