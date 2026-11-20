package vn.edu.sales.ui;

import vn.edu.sales.dao.SalesDAO;
import vn.edu.sales.model.Models.Customer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomersPanel extends JPanel implements MainFrame.Refreshable {
    private final SalesDAO dao;private final DefaultTableModel model=Theme.model("ID","Họ và tên","Điện thoại","Email","Địa chỉ","Ngày tạo");private final JTable table=Theme.table(model);private final JTextField search=Theme.searchField();private List<Customer> current=List.of();
    public CustomersPanel(SalesDAO dao){this.dao=dao;setLayout(new BorderLayout(0,14));setBackground(Theme.BG);setBorder(new EmptyBorder(22,27,27,27));JPanel tools=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));tools.setOpaque(false);search.setToolTipText("Tìm theo tên, số điện thoại hoặc email");JButton find=Theme.secondary("Tìm kiếm"),reset=Theme.secondary("Làm mới"),add=Theme.primary("+ Thêm khách hàng"),edit=Theme.secondary("Sửa"),del=Theme.danger("Xóa");tools.add(search);tools.add(find);tools.add(reset);tools.add(Box.createHorizontalStrut(9));tools.add(add);tools.add(edit);tools.add(del);add(tools,BorderLayout.NORTH);add(Theme.scroll(table),BorderLayout.CENTER);find.addActionListener(e->refreshData());search.addActionListener(e->refreshData());reset.addActionListener(e->{search.setText("");refreshData();});add.addActionListener(e->edit(null));edit.addActionListener(e->{Customer c=selected();if(c!=null)edit(c);});del.addActionListener(e->deleteSelected());refreshData();}
    @Override public void refreshData(){try{current=dao.customers(search.getText());model.setRowCount(0);for(Customer c:current)model.addRow(new Object[]{c.id(),c.name(),c.phone(),c.email(),c.address(),c.createdDate()});}catch(Exception ex){Theme.error(this,ex);}}
    private Customer selected(){int r=table.getSelectedRow();if(r<0){Theme.info(this,"Hãy chọn một khách hàng.");return null;}int id=(int)model.getValueAt(table.convertRowIndexToModel(r),0);return current.stream().filter(x->x.id()==id).findFirst().orElse(null);}
    private void edit(Customer old){JTextField name=new JTextField(old==null?"":old.name(),22),phone=new JTextField(old==null?"":old.phone(),22),email=new JTextField(old==null?"":old.email(),22),address=new JTextField(old==null?"":old.address(),22);JPanel form=new JPanel(new GridLayout(0,1,8,8));form.setBorder(new EmptyBorder(8,8,8,8));Theme.addField(form,"Họ và tên *",name);Theme.addField(form,"Số điện thoại",phone);Theme.addField(form,"Email",email);Theme.addField(form,"Địa chỉ",address);if(JOptionPane.showConfirmDialog(this,form,old==null?"Thêm khách hàng":"Cập nhật khách hàng",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION){try{dao.saveCustomer(new Customer(old==null?0:old.id(),name.getText(),phone.getText(),email.getText(),address.getText(),""));refreshData();Theme.info(this,"Đã lưu khách hàng.");}catch(Exception ex){Theme.error(this,ex);}}}
    private void deleteSelected(){Customer c=selected();if(c!=null&&Theme.confirm(this,"Xóa khách hàng '"+c.name()+"'? Nếu khách hàng đã có hóa đơn, hệ thống sẽ không cho xóa.")){try{dao.deleteCustomer(c.id());refreshData();}catch(Exception ex){Theme.error(this,ex);}}}
}
