# MARKETFLOW - Phần mềm quản lý bán hàng

Đồ án Java Swing kết nối Microsoft SQL Server thông qua JDBC. Dự án dùng Maven và Java 17, có giao diện desktop, quản lý danh mục, sản phẩm, khách hàng, lập hóa đơn, theo dõi hóa đơn và cập nhật tồn kho.

## 1. Công nghệ

- Java 17
- Java Swing (giao diện desktop)
- Microsoft SQL Server / SQL Server Express
- JDBC Driver for SQL Server
- Maven 3.9+

## 2. Chức năng

- Dashboard: số sản phẩm đang kinh doanh, số khách hàng, số hóa đơn hôm nay, doanh thu tháng và tổng tồn kho.
- Danh mục: thêm, sửa, xóa, tìm kiếm danh mục.
- Sản phẩm: thêm, sửa, xóa, tìm kiếm; quản lý SKU, danh mục, đơn vị tính, đơn giá, tồn kho và trạng thái.
- Khách hàng: thêm, sửa, xóa, tìm kiếm theo tên, số điện thoại hoặc email.
- Bán hàng: tạo giỏ hàng, chọn khách hàng hoặc khách lẻ, chọn phương thức thanh toán, áp dụng giảm giá, xác nhận hóa đơn.
- Hóa đơn: tra cứu theo mã hóa đơn/tên khách hàng và xem chi tiết.
- Khi xác nhận hóa đơn, ứng dụng kiểm tra tồn kho, lấy lại đơn giá từ SQL Server, ghi hóa đơn/chi tiết và trừ tồn kho trong cùng một transaction. Nếu thao tác lỗi, transaction được rollback.

## 3. Cấu trúc thư mục

```text
QuanLyBanHang/
├── src/main/java/vn/edu/sales/
│   ├── App.java
│   ├── db/Database.java
│   ├── dao/SalesDAO.java
│   ├── model/Models.java
│   └── ui/
├── sql/
│   ├── QuanLyBanHang.sql
│   └── 02_tao_tai_khoan_java.sql
├── docs/
├── pom.xml
├── run.bat
├── README.md
├── .gitignore
└── BAO_CAO_DO_AN.docx
```

## 4. Cài đặt SQL Server

1. Cài SQL Server Database Engine và SSMS.
2. Kết nối SSMS bằng Windows Authentication.
3. Mở `sql/QuanLyBanHang.sql`, chạy toàn bộ bằng F5. Script tạo database `QuanLyBanHang`, 5 bảng và dữ liệu mẫu ban đầu.
4. Mở `sql/02_tao_tai_khoan_java.sql`. Thay `CHON_MAT_KHAU_MANH` bằng mật khẩu riêng rồi chạy bằng F5. Tài khoản SQL tạo ra là `qlbh_app`.
5. Không công khai mật khẩu thật trên GitHub. Trước khi push, khôi phục placeholder trong file SQL nếu đã thay bằng mật khẩu thật.

### Cấu hình kết nối mặc định

Ứng dụng mặc định kết nối tới:

- Server: `localhost`
- TCP port: `14331`
- Database: `QuanLyBanHang`
- Login: `qlbh_app`

Dự án giả định SQL Server instance trên máy đang được cấu hình TCP/IP và cổng tĩnh 14331. Nếu dùng cổng khác, có thể ghi đè URL bằng biến môi trường `SALES_DB_URL`, ví dụ:

```bat
set SALES_DB_URL=jdbc:sqlserver://localhost:1433;databaseName=QuanLyBanHang;encrypt=true;trustServerCertificate=true
```

Có thể cấu hình thêm `SALES_DB_USER` và `SALES_DB_PASSWORD` bằng biến môi trường. Mặc định ứng dụng hỏi mật khẩu `qlbh_app` khi khởi động; mật khẩu không được lưu trong mã nguồn.

## 5. Chạy ứng dụng

Yêu cầu JDK 17 và Maven được cài đặt, thêm vào PATH.

- Nhấn đúp `run.bat`, hoặc
- Mở CMD tại thư mục chứa `pom.xml` và chạy:

```bat
mvn clean compile exec:java
```

Ứng dụng sẽ yêu cầu mật khẩu SQL của tài khoản `qlbh_app`. Nếu dùng biến môi trường `SALES_DB_PASSWORD`, ứng dụng dùng giá trị đó và không hỏi lại.

## 6. Kiểm tra nhanh

Sau khi khởi động:

1. Mở Sản phẩm, thử tìm `SP001`.
2. Thêm một khách hàng thử nghiệm ở mục Khách hàng.
3. Mở Bán hàng, chọn khách hàng (hoặc Khách lẻ), thêm một hay nhiều sản phẩm vào giỏ.
4. Đặt giảm giá không vượt quá tạm tính, chọn phương thức thanh toán và xác nhận.
5. Mở Hóa đơn để xem hóa đơn vừa tạo; mở Sản phẩm để kiểm tra tồn kho đã giảm.
6. Kiểm tra trường hợp nhập số lượng lớn hơn tồn kho: hệ thống phải từ chối và không tạo hóa đơn.

## 7. GitHub

Đề xuất repository Public riêng: `quan-ly-ban-hang-java`.

```bat
git init
git branch -M main
git add .
git status
git commit -m "Initial commit - Sales Management System"
git remote add origin https://github.com/USERNAME/quan-ly-ban-hang-java.git
git push -u origin main
```

Trước khi push, kiểm tra `git status` và nội dung file SQL/run configuration; không commit mật khẩu, token, file `.env` hoặc dữ liệu cá nhân. Người tải dự án phải tự cài SQL Server, tạo database và tạo tài khoản trên máy của họ.

## 8. Ghi chú

- CSDL dùng 5 bảng: `DanhMuc`, `SanPham`, `KhachHang`, `HoaDon`, `ChiTietHoaDon`.
- Xóa sản phẩm/khách hàng/danh mục đã được tham chiếu bởi hóa đơn hoặc sản phẩm sẽ bị ràng buộc khóa ngoại từ chối; nên giữ dữ liệu lịch sử.
- Đây là đồ án học tập, không phải hệ thống POS sản xuất. Chưa triển khai phân quyền nhiều vai trò, quản lý ca thu ngân, in hóa đơn nhiệt, hoàn/huỷ hóa đơn, VAT hoặc đồng bộ nhiều chi nhánh.
- Kết nối thực tế cần được kiểm tra trên máy cài SQL Server của người chạy dự án.
