package vn.edu.sales.dao;

import vn.edu.sales.db.Database;
import vn.edu.sales.model.Models.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** JDBC repository for sales, catalog, customers and invoices. */
public class SalesDAO {

    public long[] dashboardStats() throws SQLException {
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM dbo.SanPham WHERE TrangThai=1)," +
                "(SELECT COUNT(*) FROM dbo.KhachHang)," +
                "(SELECT COUNT(*) FROM dbo.HoaDon WHERE CAST(NgayLap AS DATE)=CAST(GETDATE() AS DATE))," +
                "(SELECT COALESCE(SUM(ThanhToan),0) FROM dbo.HoaDon WHERE YEAR(NgayLap)=YEAR(GETDATE()) AND MONTH(NgayLap)=MONTH(GETDATE()))," +
                "(SELECT COALESCE(SUM(SoLuongTon),0) FROM dbo.SanPham WHERE TrangThai=1)";
        try (Connection c=Database.getConnection(); Statement st=c.createStatement(); ResultSet rs=st.executeQuery(sql)) {
            if (rs.next()) return new long[]{rs.getLong(1),rs.getLong(2),rs.getLong(3),rs.getBigDecimal(4).longValue(),rs.getLong(5)};
        }
        return new long[5];
    }

    public List<Category> categories(boolean activeOnly) throws SQLException {
        String sql="SELECT MaDanhMuc,TenDanhMuc,MoTa,TrangThai FROM dbo.DanhMuc WHERE (?=0 OR TrangThai=1) ORDER BY TenDanhMuc";
        List<Category> out=new ArrayList<>();
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){
            ps.setInt(1,activeOnly?1:0);
            try(ResultSet rs=ps.executeQuery()){while(rs.next())out.add(new Category(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getBoolean(4)));}
        }
        return out;
    }

    public void saveCategory(Category x) throws SQLException {
        if(blank(x.name())) throw new SQLException("Tên danh mục không được để trống.");
        String sql=x.id()==0?"INSERT INTO dbo.DanhMuc(TenDanhMuc,MoTa,TrangThai) VALUES(?,?,?)":
                "UPDATE dbo.DanhMuc SET TenDanhMuc=?,MoTa=?,TrangThai=? WHERE MaDanhMuc=?";
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,x.name().trim()); setNullable(ps,2,x.description()); ps.setBoolean(3,x.active()); if(x.id()!=0)ps.setInt(4,x.id()); ps.executeUpdate();
        }
    }

    public void deleteCategory(int id) throws SQLException {
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("DELETE FROM dbo.DanhMuc WHERE MaDanhMuc=?")){
            ps.setInt(1,id);ps.executeUpdate();
        }
    }

    public List<Product> products(String keyword, boolean activeOnly) throws SQLException {
        String sql="SELECT p.MaSanPham,p.MaSKU,p.TenSanPham,COALESCE(p.MaDanhMuc,0),COALESCE(d.TenDanhMuc,N'Chưa phân loại'),p.DonViTinh,p.DonGia,p.SoLuongTon,p.TrangThai " +
                "FROM dbo.SanPham p LEFT JOIN dbo.DanhMuc d ON d.MaDanhMuc=p.MaDanhMuc " +
                "WHERE (?=N'' OR p.MaSKU LIKE ? OR p.TenSanPham LIKE ? OR d.TenDanhMuc LIKE ?) AND (?=0 OR p.TrangThai=1) ORDER BY p.MaSanPham DESC";
        String k=clean(keyword);List<Product> out=new ArrayList<>();
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,k);for(int i=2;i<=4;i++)ps.setString(i,"%"+k+"%");ps.setInt(5,activeOnly?1:0);
            try(ResultSet rs=ps.executeQuery()){while(rs.next())out.add(new Product(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getString(5),rs.getString(6),rs.getBigDecimal(7),rs.getInt(8),rs.getBoolean(9)));}
        }
        return out;
    }

    public void saveProduct(Product x) throws SQLException {
        if(blank(x.sku())||blank(x.name()))throw new SQLException("Mã SKU và tên sản phẩm là bắt buộc.");
        if(x.price()==null||x.price().signum()<0)throw new SQLException("Đơn giá không hợp lệ.");
        if(x.stock()<0)throw new SQLException("Số lượng tồn không được âm.");
        String sql=x.id()==0?"INSERT INTO dbo.SanPham(MaSKU,TenSanPham,MaDanhMuc,DonViTinh,DonGia,SoLuongTon,TrangThai) VALUES(?,?,?,?,?,?,?)":
                "UPDATE dbo.SanPham SET MaSKU=?,TenSanPham=?,MaDanhMuc=?,DonViTinh=?,DonGia=?,SoLuongTon=?,TrangThai=? WHERE MaSanPham=?";
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,x.sku().trim());ps.setString(2,x.name().trim());if(x.categoryId()==0)ps.setNull(3,Types.INTEGER);else ps.setInt(3,x.categoryId());
            ps.setString(4,blank(x.unit())?"Cái":x.unit().trim());ps.setBigDecimal(5,x.price());ps.setInt(6,x.stock());ps.setBoolean(7,x.active());if(x.id()!=0)ps.setInt(8,x.id());ps.executeUpdate();
        }
    }

    public void deleteProduct(int id) throws SQLException {
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("DELETE FROM dbo.SanPham WHERE MaSanPham=?")) {ps.setInt(1,id);ps.executeUpdate();}
    }

    public List<Customer> customers(String keyword) throws SQLException {
        String sql="SELECT MaKhachHang,HoTen,SDT,Email,DiaChi,NgayTao FROM dbo.KhachHang WHERE (?=N'' OR HoTen LIKE ? OR SDT LIKE ? OR Email LIKE ?) ORDER BY MaKhachHang DESC";
        String k=clean(keyword);List<Customer> out=new ArrayList<>();
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,k);for(int i=2;i<=4;i++)ps.setString(i,"%"+k+"%");
            try(ResultSet rs=ps.executeQuery()){while(rs.next())out.add(new Customer(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getDate(6).toString()));}
        }
        return out;
    }

    public void saveCustomer(Customer x) throws SQLException {
        if(blank(x.name()))throw new SQLException("Họ tên khách hàng không được để trống.");
        String sql=x.id()==0?"INSERT INTO dbo.KhachHang(HoTen,SDT,Email,DiaChi) VALUES(?,?,?,?)":
                "UPDATE dbo.KhachHang SET HoTen=?,SDT=?,Email=?,DiaChi=? WHERE MaKhachHang=?";
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,x.name().trim());setNullable(ps,2,x.phone());setNullable(ps,3,x.email());setNullable(ps,4,x.address());if(x.id()!=0)ps.setInt(5,x.id());ps.executeUpdate();
        }
    }

    public void deleteCustomer(int id) throws SQLException {
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement("DELETE FROM dbo.KhachHang WHERE MaKhachHang=?")){ps.setInt(1,id);ps.executeUpdate();}
    }

    public List<Invoice> invoices(String keyword) throws SQLException {
        String sql="SELECT h.MaHoaDon,COALESCE(k.HoTen,N'Khách lẻ'),h.NgayLap,h.TongTien,h.GiamGia,h.ThanhToan,h.PhuongThuc,h.TrangThai " +
                "FROM dbo.HoaDon h LEFT JOIN dbo.KhachHang k ON k.MaKhachHang=h.MaKhachHang " +
                "WHERE (?=N'' OR CAST(h.MaHoaDon AS NVARCHAR(20)) LIKE ? OR k.HoTen LIKE ?) ORDER BY h.MaHoaDon DESC";
        String k=clean(keyword);List<Invoice> out=new ArrayList<>();
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,k);ps.setString(2,"%"+k+"%");ps.setString(3,"%"+k+"%");
            try(ResultSet rs=ps.executeQuery()){while(rs.next()){Timestamp ts=rs.getTimestamp(3);out.add(new Invoice(rs.getInt(1),rs.getString(2),ts.toLocalDateTime(),rs.getBigDecimal(4),rs.getBigDecimal(5),rs.getBigDecimal(6),rs.getString(7),rs.getString(8)));}}
        }
        return out;
    }

    public List<InvoiceLine> invoiceLines(int invoiceId) throws SQLException {
        String sql="SELECT c.MaSKU,c.TenSanPham,c.SoLuong,c.DonGia,c.ThanhTien FROM dbo.ChiTietHoaDon c WHERE c.MaHoaDon=? ORDER BY c.MaChiTiet";
        List<InvoiceLine> out=new ArrayList<>();
        try(Connection c=Database.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setInt(1,invoiceId);try(ResultSet rs=ps.executeQuery()){while(rs.next())out.add(new InvoiceLine(rs.getString(1),rs.getString(2),rs.getInt(3),rs.getBigDecimal(4),rs.getBigDecimal(5)));}}
        return out;
    }

    /** Creates invoice, invoice lines and stock updates atomically. Prices are re-read and locked from SQL Server. */
    public int createInvoice(Integer customerId, BigDecimal discount, String method, String note, List<CartLine> cart) throws SQLException {
        if(cart==null||cart.isEmpty())throw new SQLException("Hóa đơn phải có ít nhất một sản phẩm.");
        if(discount==null||discount.signum()<0)throw new SQLException("Giảm giá không hợp lệ.");
        if(blank(method))throw new SQLException("Hãy chọn phương thức thanh toán.");
        try(Connection c=Database.getConnection()){
            c.setAutoCommit(false);
            try {
                BigDecimal subtotal=BigDecimal.ZERO;
                List<CartLine> priced=new ArrayList<>();
                for(CartLine line:cart){
                    if(line.quantity()<=0)throw new SQLException("Số lượng sản phẩm phải lớn hơn 0.");
                    String q="SELECT MaSKU,TenSanPham,DonGia,SoLuongTon,TrangThai FROM dbo.SanPham WITH (UPDLOCK,HOLDLOCK) WHERE MaSanPham=?";
                    try(PreparedStatement ps=c.prepareStatement(q)){ps.setInt(1,line.productId());try(ResultSet rs=ps.executeQuery()){
                        if(!rs.next())throw new SQLException("Sản phẩm không còn tồn tại: "+line.productName());
                        if(!rs.getBoolean("TrangThai"))throw new SQLException("Sản phẩm đã ngừng kinh doanh: "+rs.getString("TenSanPham"));
                        if(rs.getInt("SoLuongTon")<line.quantity())throw new SQLException("Không đủ tồn kho cho sản phẩm "+rs.getString("TenSanPham")+". Còn "+rs.getInt("SoLuongTon"));
                        BigDecimal dbPrice=rs.getBigDecimal("DonGia");
                        CartLine x=new CartLine(line.productId(),rs.getString("MaSKU"),rs.getString("TenSanPham"),line.quantity(),dbPrice);
                        priced.add(x);subtotal=subtotal.add(x.amount());
                    }}
                }
                if(discount.compareTo(subtotal)>0)throw new SQLException("Giảm giá không được lớn hơn tổng tiền hàng.");
                BigDecimal payment=subtotal.subtract(discount);
                String insert="INSERT INTO dbo.HoaDon(MaKhachHang,TongTien,GiamGia,ThanhToan,PhuongThuc,GhiChu) OUTPUT INSERTED.MaHoaDon VALUES(?,?,?,?,?,?)";
                int invoiceId;
                try(PreparedStatement ps=c.prepareStatement(insert)){
                    if(customerId==null)ps.setNull(1,Types.INTEGER);else ps.setInt(1,customerId);
                    ps.setBigDecimal(2,subtotal);ps.setBigDecimal(3,discount);ps.setBigDecimal(4,payment);ps.setString(5,method);setNullable(ps,6,note);
                    try(ResultSet rs=ps.executeQuery()){if(!rs.next())throw new SQLException("Không tạo được mã hóa đơn.");invoiceId=rs.getInt(1);}
                }
                String detail="INSERT INTO dbo.ChiTietHoaDon(MaHoaDon,MaSanPham,MaSKU,TenSanPham,SoLuong,DonGia,ThanhTien) VALUES(?,?,?,?,?,?,?)";
                String stock="UPDATE dbo.SanPham SET SoLuongTon=SoLuongTon-? WHERE MaSanPham=? AND SoLuongTon>=?";
                for(CartLine x:priced){
                    try(PreparedStatement ps=c.prepareStatement(detail)){ps.setInt(1,invoiceId);ps.setInt(2,x.productId());ps.setString(3,x.sku());ps.setString(4,x.productName());ps.setInt(5,x.quantity());ps.setBigDecimal(6,x.unitPrice());ps.setBigDecimal(7,x.amount());ps.executeUpdate();}
                    try(PreparedStatement ps=c.prepareStatement(stock)){ps.setInt(1,x.quantity());ps.setInt(2,x.productId());ps.setInt(3,x.quantity());if(ps.executeUpdate()!=1)throw new SQLException("Tồn kho vừa thay đổi. Vui lòng tải lại danh sách sản phẩm.");}
                }
                c.commit();return invoiceId;
            } catch(Exception ex){c.rollback();if(ex instanceof SQLException sql)throw sql;throw new SQLException("Không thể tạo hóa đơn.",ex);}
            finally{c.setAutoCommit(true);}
        }
    }

    private static String clean(String s){return s==null?"":s.trim();}
    private static boolean blank(String s){return s==null||s.isBlank();}
    private static void setNullable(PreparedStatement ps,int index,String value)throws SQLException{if(blank(value))ps.setNull(index,Types.NVARCHAR);else ps.setString(index,value.trim());}
}
