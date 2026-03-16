# 🏫 English Center Manager (Hệ thống quản lý trung tâm ngoại ngữ)

**Dự án giữa kỳ:** Xây dựng Hệ thống thông tin quản lý dành cho trung tâm ngoại ngữ tại Trường Đại học Sư phạm Kỹ thuật TP.HCM (HCMUTE). 

---

## 👥 Nhóm Tác giả

| STT | Họ và tên | Mã số sinh viên |
| :---: | :--- | :---: |
| 1 | Võ Trí Hiệu | 23110219 |
| 2 | Nguyễn Quốc Khánh | 23110239 |
| 3 | Lê Ngô Nhựt Tân | 23110315 |

---

## 🚀 Công nghệ & Thư viện sử dụng

Dự án được xây dựng và quản lý dựa trên các công nghệ sau:

- **Nền tảng:** Java 25
- **GUI:** Java Swing
- **Build tool:** Maven
- **Cơ sở dữ liệu:** MySQL (MySQL Connector/J 9.1.0)
- **Thư viện hỗ trợ:**
  - **Lombok** (1.18.38): Tối ưu hóa boilerplate code.
  - **jBCrypt** (0.4): Hỗ trợ băm (hashing) và bảo mật mật khẩu người dùng.
  - **SLF4J** (2.0.16): Hỗ trợ ghi log hệ thống.

---
## ⚙️ Hướng dẫn Cài đặt & Chạy Dự án

### 1. Cấu hình Cơ sở dữ liệu (Database)
Hệ thống sử dụng MySQL để lưu trữ dữ liệu. Trước khi chạy ứng dụng, bạn cần thiết lập CSDL như sau:

1. Mở MySQL và tạo một database mới có tên là: `mis_language_center`.
2. Mở file cấu hình cơ sở dữ liệu trong source code tại: `src/main/resources/META-INF/persistence.xml`.
3. Kiểm tra và thay đổi tài khoản/mật khẩu database cho phù hợp với máy của bạn (mặc định hệ thống đang để `root` và mật khẩu là `123456`):
   ```xml
   <property name="jakarta.persistence.jdbc.user" value="root"/>
   <property name="jakarta.persistence.jdbc.password" value="123456"/>
   ```
### 2. Chạy chương trình ở file Main.javam trong package vn.edu.ute
Mở Terminal (hoặc Command Prompt, PowerShell) tại thư mục gốc của dự án (nơi chứa file `pom.xml`) và thực hiện các câu lệnh sau:

 **Biên dịch và tải các thư viện (Dependencies):**
 ```bash
 mvn clean compile
 ```
 **Khởi chạy chương trình (từ class Main.java):**
 ```bash
 mvn exec:java -Dexec.mainClass="vn.edu.ute.Main"
```
Hoặc chạy trực tiếp từ file Main.java


     
---
# 🌿 Git Branch Naming Convention

---

## 🌱 Các loại nhánh chính

- **main** hoặc **master** → Nhánh chính (production)
- **develop** → Nhánh phát triển chính
- **feature/tên-tính-năng** → Nhánh phát triển tính năng mới
- **hotfix/tên-lỗi** → Nhánh sửa lỗi khẩn cấp (sau khi đẩy lên `main`)
- **fix/tên-lỗi** → Nhánh sửa lỗi
- **release/version-x.y.z** → Nhánh chuẩn bị release

---


---
