# BÁO CÁO ĐỒ ÁN JAVA – QUẢN LÝ BÁN HÀNG

**Tên phần mềm:** MARKETFLOW – Sales Management System  
**Công nghệ:** Java 17, Swing, JDBC, Microsoft SQL Server, Maven.

## 1. Mục tiêu đề tài

Xây dựng ứng dụng desktop hỗ trợ cửa hàng quản lý danh mục, sản phẩm, khách hàng và giao dịch bán hàng. Ứng dụng cung cấp thao tác thêm, sửa, xóa, tìm kiếm, lập hóa đơn và theo dõi tồn kho.

## 2. Phạm vi chức năng

1. Dashboard: thống kê sản phẩm đang kinh doanh, khách hàng, hóa đơn hôm nay, doanh thu tháng và tổng số lượng tồn.
2. Quản lý danh mục: thêm, sửa, xóa, tìm kiếm.
3. Quản lý sản phẩm: SKU, tên sản phẩm, danh mục, đơn vị, giá, tồn kho, trạng thái; thêm, sửa, xóa, tìm kiếm.
4. Quản lý khách hàng: thông tin liên hệ; thêm, sửa, xóa, tìm kiếm.
5. Bán hàng: giỏ hàng nhiều sản phẩm, chọn khách hàng hoặc khách lẻ, giảm giá, phương thức thanh toán, ghi chú.
6. Hóa đơn: tra cứu và xem chi tiết.

## 3. Kiến trúc

Giao diện Swing gọi lớp DAO; DAO dùng JDBC và PreparedStatement để truy vấn SQL Server. Lớp Database quản lý kết nối. Nghiệp vụ tạo hóa đơn sử dụng transaction để ghi hóa đơn, chi tiết và cập nhật tồn kho nguyên tử.

## 4. Thiết kế cơ sở dữ liệu

| Bảng | Khóa chính | Khóa ngoại / vai trò |
|---|---|---|
| DanhMuc | MaDanhMuc | Danh mục hàng hóa |
| SanPham | MaSanPham | MaDanhMuc → DanhMuc |
| KhachHang | MaKhachHang | Hồ sơ khách hàng |
| HoaDon | MaHoaDon | MaKhachHang → KhachHang (có thể NULL cho khách lẻ) |
| ChiTietHoaDon | MaChiTiet | MaHoaDon → HoaDon; MaSanPham → SanPham |

### Quy tắc nghiệp vụ chính

- Đơn giá và tồn kho không âm; số lượng trên chi tiết hóa đơn phải lớn hơn 0.
- Giảm giá không vượt tổng tiền hàng.
- Không cho lập hóa đơn nếu sản phẩm ngừng kinh doanh hoặc tồn kho không đủ.
- Giá bán được đọc lại từ SQL Server khi tạo hóa đơn để tránh tin vào giá gửi từ giao diện.
- Ghi hóa đơn, ghi chi tiết và trừ kho chạy trong một transaction; lỗi ở bất kỳ bước nào sẽ rollback.
- Hàng đã xuất hiện trong hóa đơn không thể xóa vật lý do ràng buộc lịch sử; cần cân nhắc chuyển trạng thái ngừng kinh doanh.

## 5. Công nghệ và môi trường

- JDK 17.
- Maven để quản lý phụ thuộc và biên dịch.
- Java Swing để xây dựng giao diện.
- Microsoft SQL Server/SQL Server Express để lưu trữ dữ liệu.
- JDBC Driver for SQL Server để kết nối.

## 6. Các bước triển khai

1. Cài SQL Server Database Engine và SSMS.
2. Chạy `sql/QuanLyBanHang.sql` để tạo database, bảng, chỉ mục và dữ liệu mẫu.
3. Chạy `sql/02_tao_tai_khoan_java.sql` sau khi đặt mật khẩu riêng cho `qlbh_app`.
4. Cấu hình TCP/IP và cổng SQL Server; mặc định dự án dùng `localhost:14331`.
5. Cài JDK 17 và Maven.
6. Chạy `run.bat`; nhập mật khẩu SQL khi được yêu cầu.
7. Kiểm tra sản phẩm, khách hàng, lập hóa đơn, xem hóa đơn và xác nhận tồn kho thay đổi.
8. Đưa source lên một repository GitHub Public riêng sau khi kiểm tra không có thông tin đăng nhập.

## 7. Kịch bản kiểm thử đề xuất

| Mã | Kịch bản | Kết quả mong đợi | Kết quả thực tế |
|---|---|---|---|
| TC01 | Thêm danh mục hợp lệ | Danh mục được lưu | Điền sau khi kiểm thử |
| TC02 | Thêm sản phẩm với SKU hợp lệ | Sản phẩm được lưu | Điền sau khi kiểm thử |
| TC03 | Tìm kiếm theo SKU/tên | Hiển thị kết quả phù hợp | Điền sau khi kiểm thử |
| TC04 | Thêm/sửa khách hàng | Dữ liệu được cập nhật | Điền sau khi kiểm thử |
| TC05 | Lập hóa đơn với tồn kho đủ | Lưu hóa đơn, chi tiết và trừ kho | Điền sau khi kiểm thử |
| TC06 | Bán vượt số lượng tồn | Từ chối, không tạo hóa đơn | Điền sau khi kiểm thử |
| TC07 | Giảm giá lớn hơn tổng tiền | Từ chối giao dịch | Điền sau khi kiểm thử |
| TC08 | Xem chi tiết hóa đơn | Hiển thị các dòng hàng | Điền sau khi kiểm thử |

## 8. Kết quả và giới hạn

Bộ dự án bao gồm mã nguồn Java, cấu hình Maven, script SQL, file chạy Windows, README và báo cáo. Mã nguồn có thể được biên dịch bằng JDK 17. Kết nối JDBC và các kịch bản giao dịch cần được chạy xác nhận trên máy có SQL Server.

Ứng dụng là phiên bản đồ án học tập; chưa có xác thực người dùng, phân quyền, hoàn/hủy hóa đơn, in hóa đơn nhiệt, VAT hoặc quản lý nhiều chi nhánh.

## 9. Hướng phát triển

- Bổ sung đăng nhập và phân quyền quản lý/thu ngân.
- Bổ sung nhập hàng, nhà cung cấp và lịch sử điều chỉnh kho.
- Bổ sung hoàn trả/huỷ hóa đơn có phục hồi tồn kho.
- Bổ sung báo cáo doanh thu theo ngày, tháng và sản phẩm.
- Bổ sung in hóa đơn và xuất Excel/PDF.

## 10. Thông tin sinh viên

- Họ và tên: ........................................................
- Mã sinh viên: .....................................................
- Lớp: ................................................................
- Giảng viên: .......................................................
- Repository GitHub: ...............................................
