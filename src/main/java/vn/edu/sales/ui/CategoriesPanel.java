package vn.edu.sales.ui;

import vn.edu.sales.dao.SalesDAO;
import vn.edu.sales.model.Models.Category;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoriesPanel extends JPanel implements MainFrame.Refreshable {
    private final SalesDAO dao;private final DefaultTableModel model=Theme.model("ID","Tên danh mục","Mô tả","Trạng thái");private final JTable table=Theme.table(model);private final JTextField search=Theme.searchField();private List<Category> current=List.of();
    public CategoriesPanel(SalesDAO dao){this.dao=dao;setLayout(new BorderLayout(0,14));setBackground(Theme.BG);setBorder(new EmptyBorder(22,27,27,27));JPanel tools=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));tools.setOpaque(false);search.setToolTipText("Tìm kiếm được áp dụng trên danh sách hiện tại");JButton add=Theme.primary("+ Thêm danh mục"),edit=Theme.secondary("Sửa"),del=Theme.danger("Xóa"),refresh=Theme.secondary("Làm mới");tools.add(add);tools.add(edit);tools.add(del);tools.add(Box.createHorizontalStrut(12));tools.add(search);tools.add(refresh);add(tools,BorderLayout.NORTH);add(Theme.scroll(table),BorderLayout.CENTER);add.addActionListener(e->edit(null));edit.addActionListener(e->{Category x=selected();if(x!=null)edit(x);});del.addActionListener(e->deleteSelected());refresh.addActionListener(e->refreshData());search.addActionListener(e->refreshData());refreshData();}
    @Override public void refreshData(){try{current=dao.categories(false);model.setRowCount(0);String k=search.getText().trim().toLowerCase();for(Category x:current)if(k.isEmpty()||x.name().toLowerCase().contains(k)||String.valueOf(x.id()).contains(k))model.addRow(new Object[]{x.id(),x.name(),x.description(),x.active()?"Đang sử dụng":"Ngừng sử dụng"});}catch(Exception ex){Theme.error(this,ex);}}
    private Category selected(){int r=table.getSelectedRow();if(r<0){Theme.info(this,"Hãy chọn một danh mục.");return null;}int id=(int)model.getValueAt(table.convertRowIndexToModel(r),0);return current.stream().filter(x->x.id()==id).findFirst().orElse(null);}
    private void edit(Category old){JTextField name=new JTextField(old==null?"":old.name(),22),desc=new JTextField(old==null?"":old.description(),22);JCheckBox active=new JCheckBox("Đang sử dụng",old==null||old.active());JPanel form=new JPanel(new GridLayout(0,1,8,8));form.setBorder(new EmptyBorder(8,8,8,8));Theme.addField(form,"Tên danh mục *",name);Theme.addField(form,"Mô tả",desc);form.add(active);if(JOptionPane.showConfirmDialog(this,form,old==null?"Thêm danh mục":"Sửa danh mục",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION){try{dao.saveCategory(new Category(old==null?0:old.id(),name.getText(),desc.getText(),active.isSelected()));refreshData();Theme.info(this,"Đã lưu danh mục.");}catch(Exception ex){Theme.error(this,ex);}}}
    private void deleteSelected(){Category x=selected();if(x!=null&&Theme.confirm(this,"Xóa danh mục '"+x.name()+"'? Nếu danh mục đang có sản phẩm, SQL Server sẽ từ chối xóa.")){try{dao.deleteCategory(x.id());refreshData();}catch(Exception ex){Theme.error(this,ex);}}}
}
