# Hướng dẫn sử dụng Database Inspector

## Thông tin Database
- **Tên database**: `calculator_pro.db`
- **Bảng**: `calculations`
- **Số bản ghi mẫu**: 10 phép tính

## Cách mở Database Inspector

### Bước 1: Chạy ứng dụng
1. Kết nối thiết bị Android hoặc khởi động emulator
2. Chạy app ở chế độ **Debug** (không phải Release)
3. Đợi app khởi động hoàn toàn

### Bước 2: Mở App Inspection
1. Trong Android Studio, chọn menu: **View → Tool Windows → App Inspection**
2. Hoặc nhấn tổ hợp phím: **Ctrl + Shift + A** (Windows/Linux) hoặc **Cmd + Shift + A** (Mac)
3. Gõ "App Inspection" và chọn

### Bước 3: Chọn Database Inspector
1. Trong cửa sổ App Inspection, chọn tab **Database Inspector**
2. Đảm bảo process `com.example.calpro` đang chạy
3. Nếu không thấy database, nhấn nút **Refresh** (biểu tượng làm mới)

### Bước 4: Xem dữ liệu
1. Mở rộng node **calculator_pro.db**
2. Chọn bảng **calculations**
3. Bạn sẽ thấy 10 bản ghi với các cột:
   - `id`: ID tự động tăng
   - `expression`: Biểu thức tính toán (vd: "2 + 2")
   - `result`: Kết quả (vd: "4")
   - `timestamp`: Thời gian tính toán

## Các tính năng của Database Inspector

### 1. Xem dữ liệu theo thời gian thực
- Database Inspector tự động cập nhật khi có thay đổi
- Bạn có thể thấy dữ liệu mới được thêm vào ngay lập tức

### 2. Chạy truy vấn SQL
- Nhấn vào tab **Query** ở góc trên bên phải
- Gõ truy vấn SQL, ví dụ:
  ```sql
  SELECT * FROM calculations ORDER BY timestamp DESC LIMIT 5
  ```
- Nhấn **Run** để xem kết quả

### 3. Chỉnh sửa dữ liệu (Live Edit)
- Double-click vào ô bất kỳ để chỉnh sửa
- Nhấn Enter để lưu thay đổi
- **Lưu ý**: Chỉ hoạt động với app đang chạy

### 4. Export dữ liệu
- Click chuột phải vào bảng
- Chọn **Export to File**
- Chọn định dạng (CSV, SQL, etc.)

## Xử lý sự cố

### Không thấy database?
1. Đảm bảo app đang chạy ở chế độ Debug
2. Kiểm tra minSdk >= 26 (API level 26+) để sử dụng đầy đủ tính năng
3. Nhấn nút Refresh trong Database Inspector
4. Restart app và thử lại

### Database trống?
1. Kiểm tra Logcat để xem log: "Đã thêm 10 bản ghi mẫu vào database"
2. Nếu không có log, có thể có lỗi khi insert dữ liệu
3. Uninstall app và cài lại để tạo database mới

### App Inspector không mở được?
1. Cập nhật Android Studio lên phiên bản mới nhất
2. Đảm bảo Android Gradle Plugin >= 4.1.0
3. Sync Gradle và rebuild project

## Kiểm tra trong Logcat
Mở Logcat và filter theo tag `MainActivity` để xem log:
```
Đã thêm 10 bản ghi mẫu vào database calculator_pro.db
```

## Cấu trúc bảng calculations

| Cột | Kiểu | Mô tả |
|-----|------|-------|
| id | INTEGER | Primary key, auto increment |
| expression | TEXT | Biểu thức tính toán |
| result | TEXT | Kết quả |
| timestamp | INTEGER | Thời gian (milliseconds) |

## Dữ liệu mẫu

App tự động thêm 10 phép tính mẫu khi khởi động:
1. 2 + 2 = 4
2. 10 - 3 = 7
3. 5 × 6 = 30
4. 20 ÷ 4 = 5
5. 15 + 25 = 40
6. 100 - 37 = 63
7. 8 × 9 = 72
8. 144 ÷ 12 = 12
9. 50 + 75 = 125
10. 200 - 89 = 111

## Lưu ý quan trọng
- Database Inspector chỉ hoạt động với **debug build**
- Cần thiết bị/emulator chạy **API 26+** để có đầy đủ tính năng
- Dữ liệu sẽ bị xóa và tạo lại mỗi khi app khởi động (do có `deleteAll()`)
