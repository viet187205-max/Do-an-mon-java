package vn.edu.sales.ui;

import vn.edu.sales.dao.SalesDAO;
import vn.edu.sales.model.Models.Invoice;
import vn.edu.sales.model.Models.InvoiceLine;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class InvoicesPanel extends JPanel implements MainFrame.Refreshable {
    private final SalesDAO dao;private final DefaultTableModel model=Theme.model("Mã HĐ","Khách hàng","Ngày lập","Tổng tiền","Giảm giá","Thanh toán","Phương thức","Trạng thái");private final JTable table=Theme.table(model);private final JTextField search=Theme.searchField();private List<Invoice> current=List.of();
    public InvoicesPanel(SalesDAO dao){this.dao=dao;setLayout(new BorderLayout(0,14));setBackground(Theme.BG);setBorder(new EmptyBorder(22,27,27,27));JPanel tools=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));tools.setOpaque(false);search.setToolTipText("Tìm theo mã hóa đơn hoặc tên khách hàng");JButton find=Theme.secondary("Tìm kiếm"),reset=Theme.secondary("Làm mới"),detail=Theme.primary("Xem chi tiết");tools.add(search);tools.add(find);tools.add(reset);tools.add(Box.createHorizontalStrut(10));tools.add(detail);add(tools,BorderLayout.NORTH);add(Theme.scroll(table),BorderLayout.CENTER);find.addActionListener(e->refreshData());search.addActionListener(e->refreshData());reset.addActionListener(e->{search.setText("");refreshData();});detail.addActionListener(e->showDetail());refreshData();}
    @Override public void refreshData(){try{current=dao.invoices(search.getText());model.setRowCount(0);NumberFormat nf=NumberFormat.getNumberInstance(new Locale("vi","VN"));for(Invoice x:current)model.addRow(new Object[]{x.id(),x.customerName(),x.createdAt().toString().replace('T',' '),nf.format(x.total()),nf.format(x.discount()),nf.format(x.payment()),x.method(),x.status()});}catch(Exception ex){Theme.error(this,ex);}}
    private void showDetail(){int r=table.getSelectedRow();if(r<0){Theme.info(this,"Hãy chọn một hóa đơn.");return;}int id=(int)model.getValueAt(table.convertRowIndexToModel(r),0);try{List<InvoiceLine> lines=dao.invoiceLines(id);DefaultTableModel m=Theme.model("SKU","Sản phẩm","Số lượng","Đơn giá","Thành tiền");NumberFormat nf=NumberFormat.getNumberInstance(new Locale("vi","VN"));for(InvoiceLine x:lines)m.addRow(new Object[]{x.sku(),x.productName(),x.quantity(),nf.format(x.unitPrice()),nf.format(x.lineTotal())});JTable t=Theme.table(m);JScrollPane sp=Theme.scroll(t);sp.setPreferredSize(new Dimension(680,260));JOptionPane.showMessageDialog(this,sp,"Chi tiết hóa đơn #"+id,JOptionPane.INFORMATION_MESSAGE);}catch(Exception ex){Theme.error(this,ex);}}
}
