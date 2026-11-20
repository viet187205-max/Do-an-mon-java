package vn.edu.sales.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class Models {
    private Models() { }
    public record Category(int id, String name, String description, boolean active) { @Override public String toString(){ return name; } }
    public record Product(int id, String sku, String name, int categoryId, String categoryName,
                          String unit, BigDecimal price, int stock, boolean active) { @Override public String toString(){ return sku + " - " + name + " (tồn: " + stock + ")"; } }
    public record Customer(int id, String name, String phone, String email, String address, String createdDate) { @Override public String toString(){ return name + (phone==null||phone.isBlank()?"":" - "+phone); } }
    public record Invoice(int id, String customerName, LocalDateTime createdAt, BigDecimal total,
                          BigDecimal discount, BigDecimal payment, String method, String status) { }
    public record InvoiceLine(String sku, String productName, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) { }
    public record CartLine(int productId, String sku, String productName, int quantity, BigDecimal unitPrice) {
        public BigDecimal amount() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
    }
}
