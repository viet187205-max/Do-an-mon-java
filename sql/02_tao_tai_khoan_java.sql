/* Chay bang Windows Authentication trong SSMS.
   Thay placeholder bang mat khau rieng truoc khi chay.
   Khong commit mat khau that len GitHub. */
USE master;
GO
IF SUSER_ID(N'qlbh_app') IS NULL
    CREATE LOGIN qlbh_app WITH PASSWORD = N'123456';
ELSE
    ALTER LOGIN qlbh_app WITH PASSWORD = N'123456';
GO
ALTER LOGIN qlbh_app ENABLE;
GO
USE QuanLyBanHang;
GO
IF DATABASE_PRINCIPAL_ID(N'qlbh_app') IS NULL
    CREATE USER qlbh_app FOR LOGIN qlbh_app;
ELSE
    ALTER USER qlbh_app WITH LOGIN = qlbh_app;
GO
ALTER ROLE db_datareader ADD MEMBER qlbh_app;
ALTER ROLE db_datawriter ADD MEMBER qlbh_app;
GO
