package vn.edu.sales.ui;

import vn.edu.sales.dao.SalesDAO;
import vn.edu.sales.model.Models.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SalesPanel extends JPanel implements MainFrame.Refreshable {
    private final SalesDAO dao;
    private final JComboBox<Product> productBox=new JComboBox<>();
    private final JComboBox<CustomerChoice> customerBox=new JComboBox<>();
    private final JSpinner quantity=new JSpinner(new SpinnerNumberModel(1,1,9999,1));
    private final JTextField discount=new JTextField("0",10);
    private final JComboBox<String> method=new JComboBox<>(new String[]{"Tiền mặt","Chuyển khoản","Thẻ ngân hàng","Ví điện tử"});
    private final JTextField note=new JTextField(20);
    private final DefaultTableModel model=Theme.model("ID sản phẩm","SKU","Tên sản phẩm","Số lượng","Đơn giá","Thành tiền");
    private final JTable table=Theme.table(model);
    private final JLabel subtotalLabel=Theme.heading("0 đ",18,Theme.TEXT);
    private final JLabel paymentLabel=Theme.heading("0 đ",22,Theme.ACCENT_DARK);
    private final List<CartLine> cart=new ArrayList<>();
    private List<Product> productCache=List.of();

    public SalesPanel(SalesDAO dao){this.dao=dao;setLayout(new BorderLayout(0,14));setBackground(Theme.BG);setBorder(new EmptyBorder(20,25,25,25));
        JPanel top=Theme.card();top.setLayout(new BorderLayout(0,12));top.add(Theme.heading("THÔNG TIN ĐƠN HÀNG",14,Theme.TEXT),BorderLayout.NORTH);
        JPanel fields=new JPanel(new GridLayout(2,3,12,10));fields.setOpaque(false);Theme.addField(fields,"Khách hàng",customerBox);Theme.addField(fields,"Sản phẩm",productBox);Theme.addField(fields,"Số lượng",quantity);Theme.addField(fields,"Giảm giá (VND)",discount);Theme.addField(fields,"Phương thức thanh toán",method);Theme.addField(fields,"Ghi chú",note);top.add(fields,BorderLayout.CENTER);
        JButton add=Theme.primary("+ Thêm vào giỏ");JPanel action=new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));action.setOpaque(false);action.add(add);top.add(action,BorderLayout.SOUTH);add(top,BorderLayout.NORTH);
        JPanel center=new JPanel(new BorderLayout(0,9));center.setOpaque(false);JPanel bar=new JPanel(new BorderLayout());bar.setOpaque(false);bar.add(Theme.heading("GIỎ HÀNG",14,Theme.TEXT),BorderLayout.WEST);JButton remove=Theme.danger("Bỏ sản phẩm đã chọn");bar.add(remove,BorderLayout.EAST);center.add(bar,BorderLayout.NORTH);center.add(Theme.scroll(table),BorderLayout.CENTER);add(center,BorderLayout.CENTER);
        JPanel bottom=Theme.card();bottom.setLayout(new BorderLayout(12,0));JPanel totals=new JPanel(new GridLayout(2,2,10,7));totals.setOpaque(false);JLabel st=new JLabel("Tạm tính");st.setForeground(Theme.MUTED);JLabel pay=new JLabel("Khách cần thanh toán");pay.setForeground(Theme.MUTED);totals.add(st);totals.add(subtotalLabel);totals.add(pay);totals.add(paymentLabel);bottom.add(totals,BorderLayout.CENTER);JButton checkout=Theme.primary("XÁC NHẬN THANH TOÁN");bottom.add(checkout,BorderLayout.EAST);add(bottom,BorderLayout.SOUTH);
        add.addActionListener(e->addCartItem());remove.addActionListener(e->removeCartItem());discount.addActionListener(e->updateTotals());discount.addFocusListener(new java.awt.event.FocusAdapter(){@Override public void focusLost(java.awt.event.FocusEvent e){updateTotals();}});checkout.addActionListener(e->checkout());refreshData();
    }
    @Override public void refreshData(){try{productCache=dao.products("",true);productBox.removeAllItems();for(Product p:productCache)if(p.stock()>0)productBox.addItem(p);customerBox.removeAllItems();customerBox.addItem(new CustomerChoice(null,"Khách lẻ"));for(Customer c:dao.customers(""))customerBox.addItem(new CustomerChoice(c.id(),c.name()));}catch(Exception ex){Theme.error(this,ex);}}
    private void addCartItem(){Product p=(Product)productBox.getSelectedItem();if(p==null){Theme.info(this,"Không có sản phẩm còn hàng để bán.");return;}int q=(Integer)quantity.getValue();for(int i=0;i<cart.size();i++){CartLine x=cart.get(i);if(x.productId()==p.id()){int next=x.quantity()+q;if(next>p.stock()){Theme.info(this,"Số lượng vượt tồn kho hiện tại.");return;}cart.set(i,new CartLine(x.productId(),x.sku(),x.productName(),next,x.unitPrice()));renderCart();return;}}if(q>p.stock()){Theme.info(this,"Số lượng vượt tồn kho hiện tại.");return;}cart.add(new CartLine(p.id(),p.sku(),p.name(),q,p.price()));renderCart();}
    private void removeCartItem(){int r=table.getSelectedRow();if(r<0){Theme.info(this,"Hãy chọn sản phẩm trong giỏ.");return;}int id=(int)model.getValueAt(table.convertRowIndexToModel(r),0);cart.removeIf(x->x.productId()==id);renderCart();}
    private void renderCart(){model.setRowCount(0);NumberFormat nf=NumberFormat.getNumberInstance(new Locale("vi","VN"));for(CartLine x:cart)model.addRow(new Object[]{x.productId(),x.sku(),x.productName(),x.quantity(),nf.format(x.unitPrice()),nf.format(x.amount())});updateTotals();}
    private BigDecimal subtotal(){return cart.stream().map(CartLine::amount).reduce(BigDecimal.ZERO,BigDecimal::add);}
    private void updateTotals(){BigDecimal sub=subtotal();subtotalLabel.setText(money(sub));try{BigDecimal d=discount.getText().isBlank()?BigDecimal.ZERO:new BigDecimal(discount.getText().trim());paymentLabel.setText(money(d.signum()<0||d.compareTo(sub)>0?sub:sub.subtract(d)));}catch(Exception ex){paymentLabel.setText("Kiểm tra giảm giá");}}
    private void checkout(){if(cart.isEmpty()){Theme.info(this,"Giỏ hàng đang trống.");return;}try{BigDecimal d=discount.getText().isBlank()?BigDecimal.ZERO:new BigDecimal(discount.getText().trim());CustomerChoice c=(CustomerChoice)customerBox.getSelectedItem();Integer customerId=c==null?null:c.id();int invoiceId=dao.createInvoice(customerId,d,(String)method.getSelectedItem(),note.getText(),new ArrayList<>(cart));Theme.info(this,"Đã tạo hóa đơn #"+invoiceId+". Tồn kho đã được cập nhật.");cart.clear();discount.setText("0");note.setText("");renderCart();refreshData();}catch(Exception ex){Theme.error(this,ex);}}
    private String money(BigDecimal v){return NumberFormat.getNumberInstance(new Locale("vi","VN")).format(v)+" đ";}
    private record CustomerChoice(Integer id,String name){@Override public String toString(){return name;}}
}
