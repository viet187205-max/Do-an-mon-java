/* ================================================================
   DO AN JAVA: QUAN LY BAN HANG
   SQL Server 2019+ / SQL Server Express
   Chay toan bo file nay trong SSMS.
   ================================================================ */

IF DB_ID(N'QuanLyBanHang') IS NULL
    CREATE DATABASE QuanLyBanHang;
GO

USE QuanLyBanHang;
GO

/* 1. DANH MUC SAN PHAM */
IF OBJECT_ID(N'dbo.DanhMuc', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.DanhMuc (
        MaDanhMuc INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_DanhMuc PRIMARY KEY,
        TenDanhMuc NVARCHAR(120) NOT NULL,
        MoTa NVARCHAR(300) NULL,
        TrangThai BIT NOT NULL CONSTRAINT DF_DanhMuc_TrangThai DEFAULT (1),
        CONSTRAINT UQ_DanhMuc_Ten UNIQUE (TenDanhMuc)
    );
END;
GO

/* 2. SAN PHAM */
IF OBJECT_ID(N'dbo.SanPham', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.SanPham (
        MaSanPham INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_SanPham PRIMARY KEY,
        MaSKU VARCHAR(40) NOT NULL,
        TenSanPham NVARCHAR(180) NOT NULL,
        MaDanhMuc INT NULL,
        DonViTinh NVARCHAR(40) NOT NULL CONSTRAINT DF_SanPham_DVT DEFAULT (N'Cái'),
        DonGia DECIMAL(18,2) NOT NULL,
        SoLuongTon INT NOT NULL CONSTRAINT DF_SanPham_Ton DEFAULT (0),
        TrangThai BIT NOT NULL CONSTRAINT DF_SanPham_TrangThai DEFAULT (1),
        CONSTRAINT UQ_SanPham_SKU UNIQUE (MaSKU),
        CONSTRAINT FK_SanPham_DanhMuc FOREIGN KEY (MaDanhMuc) REFERENCES dbo.DanhMuc(MaDanhMuc),
        CONSTRAINT CK_SanPham_DonGia CHECK (DonGia >= 0),
        CONSTRAINT CK_SanPham_SoLuong CHECK (SoLuongTon >= 0)
    );
END;
GO

/* 3. KHACH HANG */
IF OBJECT_ID(N'dbo.KhachHang', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.KhachHang (
        MaKhachHang INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_KhachHang PRIMARY KEY,
        HoTen NVARCHAR(150) NOT NULL,
        SDT VARCHAR(20) NULL,
        Email VARCHAR(120) NULL,
        DiaChi NVARCHAR(250) NULL,
        NgayTao DATE NOT NULL CONSTRAINT DF_KhachHang_NgayTao DEFAULT (CONVERT(DATE, GETDATE()))
    );
END;
GO

/* 4. HOA DON */
IF OBJECT_ID(N'dbo.HoaDon', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.HoaDon (
        MaHoaDon INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_HoaDon PRIMARY KEY,
        MaKhachHang INT NULL,
        NgayLap DATETIME2(0) NOT NULL CONSTRAINT DF_HoaDon_NgayLap DEFAULT (SYSDATETIME()),
        TongTien DECIMAL(18,2) NOT NULL,
        GiamGia DECIMAL(18,2) NOT NULL CONSTRAINT DF_HoaDon_GiamGia DEFAULT (0),
        ThanhToan DECIMAL(18,2) NOT NULL,
        PhuongThuc NVARCHAR(40) NOT NULL,
        GhiChu NVARCHAR(300) NULL,
        TrangThai NVARCHAR(20) NOT NULL CONSTRAINT DF_HoaDon_TrangThai DEFAULT (N'DA_THANH_TOAN'),
        CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (MaKhachHang) REFERENCES dbo.KhachHang(MaKhachHang),
        CONSTRAINT CK_HoaDon_TongTien CHECK (TongTien >= 0),
        CONSTRAINT CK_HoaDon_GiamGia CHECK (GiamGia >= 0 AND GiamGia <= TongTien),
        CONSTRAINT CK_HoaDon_ThanhToan CHECK (ThanhToan = TongTien - GiamGia),
        CONSTRAINT CK_HoaDon_PhuongThuc CHECK (PhuongThuc IN (N'Tiền mặt',N'Chuyển khoản',N'Thẻ ngân hàng',N'Ví điện tử'))
    );
END;
GO

/* 5. CHI TIET HOA DON */
IF OBJECT_ID(N'dbo.ChiTietHoaDon', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ChiTietHoaDon (
        MaChiTiet INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_ChiTietHoaDon PRIMARY KEY,
        MaHoaDon INT NOT NULL,
        MaSanPham INT NOT NULL,
        MaSKU VARCHAR(40) NOT NULL,
        TenSanPham NVARCHAR(180) NOT NULL,
        SoLuong INT NOT NULL,
        DonGia DECIMAL(18,2) NOT NULL,
        ThanhTien DECIMAL(18,2) NOT NULL,
        CONSTRAINT FK_CTHD_HoaDon FOREIGN KEY (MaHoaDon) REFERENCES dbo.HoaDon(MaHoaDon),
        CONSTRAINT FK_CTHD_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham),
        CONSTRAINT CK_CTHD_SoLuong CHECK (SoLuong > 0),
        CONSTRAINT CK_CTHD_DonGia CHECK (DonGia >= 0),
        CONSTRAINT CK_CTHD_ThanhTien CHECK (ThanhTien = SoLuong * DonGia)
    );
END;
GO

/* INDEX - co the chay lai script */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name=N'IX_SanPham_Ten' AND object_id=OBJECT_ID(N'dbo.SanPham'))
    CREATE INDEX IX_SanPham_Ten ON dbo.SanPham(TenSanPham);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name=N'IX_HoaDon_NgayLap' AND object_id=OBJECT_ID(N'dbo.HoaDon'))
    CREATE INDEX IX_HoaDon_NgayLap ON dbo.HoaDon(NgayLap);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name=N'IX_CTHD_HoaDon' AND object_id=OBJECT_ID(N'dbo.ChiTietHoaDon'))
    CREATE INDEX IX_CTHD_HoaDon ON dbo.ChiTietHoaDon(MaHoaDon);
GO

/* DU LIEU MAU */
IF NOT EXISTS (SELECT 1 FROM dbo.DanhMuc)
BEGIN
    INSERT INTO dbo.DanhMuc(TenDanhMuc,MoTa) VALUES
    (N'Đồ uống',N'Nước giải khát và đồ uống đóng chai'),
    (N'Thực phẩm',N'Thực phẩm và đồ ăn đóng gói'),
    (N'Văn phòng phẩm',N'Dụng cụ học tập và văn phòng'),
    (N'Phụ kiện',N'Phụ kiện và hàng tiêu dùng');
END;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.SanPham)
BEGIN
    INSERT INTO dbo.SanPham(MaSKU,TenSanPham,MaDanhMuc,DonViTinh,DonGia,SoLuongTon,TrangThai)
    SELECT 'SP001',N'Nước suối 500ml',MaDanhMuc,N'Chai',7000,120,1 FROM dbo.DanhMuc WHERE TenDanhMuc=N'Đồ uống'
    UNION ALL
    SELECT 'SP002',N'Cà phê lon',MaDanhMuc,N'Lon',12000,80,1 FROM dbo.DanhMuc WHERE TenDanhMuc=N'Đồ uống'
    UNION ALL
    SELECT 'SP003',N'Bánh quy hộp',MaDanhMuc,N'Hộp',35000,45,1 FROM dbo.DanhMuc WHERE TenDanhMuc=N'Thực phẩm'
    UNION ALL
    SELECT 'SP004',N'Bút bi xanh',MaDanhMuc,N'Cây',5000,200,1 FROM dbo.DanhMuc WHERE TenDanhMuc=N'Văn phòng phẩm'
    UNION ALL
    SELECT 'SP005',N'Sổ tay A5',MaDanhMuc,N'Cuốn',22000,60,1 FROM dbo.DanhMuc WHERE TenDanhMuc=N'Văn phòng phẩm';
END;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.KhachHang)
BEGIN
    INSERT INTO dbo.KhachHang(HoTen,SDT,Email,DiaChi) VALUES
    (N'Nguyễn Minh Anh','0900000101','minhanh@example.com',N'Hà Nội'),
    (N'Trần Quốc Bảo','0900000102','quocbao@example.com',N'Bắc Ninh'),
    (N'Lê Thu Hà','0900000103','thuha@example.com',N'Hà Nội');
END;
GO

/* KIEM TRA CAC BANG */
SELECT N'DanhMuc' AS TenBang, COUNT(*) AS SoBanGhi FROM dbo.DanhMuc
UNION ALL SELECT N'SanPham', COUNT(*) FROM dbo.SanPham
UNION ALL SELECT N'KhachHang', COUNT(*) FROM dbo.KhachHang
UNION ALL SELECT N'HoaDon', COUNT(*) FROM dbo.HoaDon
UNION ALL SELECT N'ChiTietHoaDon', COUNT(*) FROM dbo.ChiTietHoaDon;
GO
