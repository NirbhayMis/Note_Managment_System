#  Notes Management System (Spring Boot)

A backend Notes Management System built using **Spring Boot**, **Spring Security**, **JWT**, and **JPA**.  
The application supports **role-based access**, **file uploads**, and **secure note management** for Users and Admin.



 🚀 Features

 #👤 User
- User registration & login (JWT based)
- Create, update, and delete own notes
- Upload multiple files (PDF / Image) to notes
- Delete only own uploaded files
- View public notes

 👑 Admin
- Single admin system (no admin signup)
- Admin auto-created at application startup
- Create, update, and delete any note
- Upload files to any note
- View all public notes

---

 🔐 Security
- JWT-based authentication
- Role-based authorization (USER / ADMIN)
- Secure password storage using BCrypt
- Admin credentials managed via environment variables

---

 🗂️ File Management
- Supports **multiple files per note**
- Each upload creates a new file entry (no overwrite)
- Files tracked with unique IDs
- File ownership maintained (uploadedByRole, uploadedById)

---

 🛠️ Tech Stack

- **Java 21**
- **Spring Boot**
- **Spring Security + JWT**
- **Spring Data JPA (Hibernate)**
- **MySQL / PostgreSQL**
- **Cloudinary (File Storage)**
- **Maven**

---

⚙️ Configuration

All sensitive credentials are managed using environment variables.

Example:
# Admin Credentials
ADMIN_EMAIL=admin@gmail.com
ADMIN_PASSWORD=admin123
ADMIN_NAME="Super Admin"
