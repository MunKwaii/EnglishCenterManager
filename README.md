# EnglishCenterManager
Midterm Project: Management Information System for English Center. Built with Java Swing, Maven, and MySQL.

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

## 📌 Ví dụ

- `fix/login-bug` → Nhánh sửa lỗi đăng nhập  
- `fix/ui-overlap` → Nhánh sửa lỗi UI bị chồng chéo

---

## ⚠️ Lưu ý quan trọng

- Dùng dấu **“-”** thay vì **“_”** hoặc **space** để tránh lỗi  
- Đặt tên **ngắn gọn**, **dễ hiểu**, mô tả rõ nội dung  
- **Không nên dùng tiếng Việt có dấu** trong tên nhánh  
- Mỗi chức năng nên tạo **nhánh riêng**  
- Code chỉnh sửa nên thực hiện trên nhánh `develop`  
  - Sau khi hoàn thành → tạo `release` → merge vào `main`

---
