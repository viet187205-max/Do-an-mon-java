package vn.edu.sales.ui;

import vn.edu.sales.dao.SalesDAO;
import vn.edu.sales.model.Models.Category;
import vn.edu.sales.model.Models.Product;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductsPanel extends JPanel implements MainFrame.Refreshable {
    private final SalesDAO dao;private final DefaultTableModel model=Theme.model("ID","SKU","Tên sản phẩm","Danh mục","Đơn vị","Đơn giá (VND)","Tồn kho","Trạng thái");private final JTable table=Theme.table(model);private final JTextField search=Theme.searchField();private List<Product> current=List.of();
    public ProductsPanel(SalesDAO dao){this.dao=dao;setLayout(new BorderLayout(0,14));setBackground(Theme.BG);setBorder(new EmptyBorder(22,27,27,27));JPanel tools=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));tools.setOpaque(false);search.setToolTipText("Tìm theo SKU, tên sản phẩm hoặc danh mục");JButton find=Theme.secondary("Tìm kiếm"),reset=Theme.secondary("Làm mới"),add=Theme.primary("+ Thêm sản phẩm"),edit=Theme.secondary("Sửa"),del=Theme.danger("Xóa");tools.add(search);tools.add(find);tools.add(reset);tools.add(Box.createHorizontalStrut(9));tools.add(add);tools.add(edit);tools.add(del);add(tools,BorderLayout.NORTH);add(Theme.scroll(table),BorderLayout.CENTER);find.addActionListener(e->refreshData());search.addActionListener(e->refreshData());reset.addActionListener(e->{search.setText("");refreshData();});add.addActionListener(e->edit(null));edit.addActionListener(e->{Product p=selected();if(p!=null)edit(p);});del.addActionListener(e->deleteSelected());refreshData();}
    @Override public void refreshData(){try{current=dao.products(search.getText(),false);model.setRowCount(0);NumberFormat nf=NumberFormat.getNumberInstance(new Locale("vi","VN"));for(Product p:current)model.addRow(new Object[]{p.id(),p.sku(),p.name(),p.categoryName(),p.unit(),nf.format(p.price()),p.stock(),p.active()?"Đang bán":"Ngừng bán"});}catch(Exception ex){Theme.error(this,ex);}}
    private Product selected(){int r=table.getSelectedRow();if(r<0){Theme.info(this,"Hãy chọn một sản phẩm.");return null;}int id=(int)model.getValueAt(table.convertRowIndexToModel(r),0);return current.stream().filter(x->x.id()==id).findFirst().orElse(null);}
    private void edit(Product old){try{List<Category> cats=dao.categories(false);JTextField sku=new JTextField(old==null?"":old.sku(),20),name=new JTextField(old==null?"":old.name(),20),unit=new JTextField(old==null?"Cái":old.unit(),20),price=new JTextField(old==null?"0":old.price().toPlainString(),20),stock=new JTextField(old==null?"0":String.valueOf(old.stock()),20);JComboBox<Category> category=new JComboBox<>();category.addItem(new Category(0,"-- Chưa phân loại --","",true));for(Category c:cats)category.addItem(c);if(old!=null)for(int i=0;i<category.getItemCount();i++)if(category.getItemAt(i).id()==old.categoryId())category.setSelectedIndex(i);JCheckBox active=new JCheckBox("Đang kinh doanh",old==null||old.active());JPanel form=new JPanel(new GridLayout(0,2,10,9));form.setBorder(new EmptyBorder(8,8,8,8));Theme.addField(form,"Mã SKU *",sku);Theme.addField(form,"Tên sản phẩm *",name);Theme.addField(form,"Danh mục",category);Theme.addField(form,"Đơn vị tính",unit);Theme.addField(form,"Đơn giá (VND) *",price);Theme.addField(form,"Số lượng tồn *",stock);form.add(active);form.add(new JLabel("* Bắt buộc"));if(JOptionPane.showConfirmDialog(this,form,old==null?"Thêm sản phẩm":"Cập nhật sản phẩm",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION){Category c=(Category)category.getSelectedItem();dao.saveProduct(new Product(old==null?0:old.id(),sku.getText(),name.getText(),c==null?0:c.id(),c==null?"":c.name(),unit.getText(),new BigDecimal(price.getText().trim()),Integer.parseInt(stock.getText().trim()),active.isSelected()));refreshData();Theme.info(this,"Đã lưu sản phẩm.");}}catch(Exception ex){Theme.error(this,ex);}}
    private void deleteSelected(){Product p=selected();if(p!=null&&Theme.confirm(this,"Xóa sản phẩm '"+p.name()+"'? Nếu sản phẩm đã có trong hóa đơn, hệ thống sẽ không cho xóa.")){try{dao.deleteProduct(p.id());refreshData();}catch(Exception ex){Theme.error(this,ex);}}}
}
