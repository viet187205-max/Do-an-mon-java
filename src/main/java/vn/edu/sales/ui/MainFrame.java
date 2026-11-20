package vn.edu.sales.ui;

import vn.edu.sales.dao.SalesDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final CardLayout layout=new CardLayout();
    private final JPanel host=new JPanel(layout);
    private final JLabel title=Theme.heading("Tổng quan",22,Theme.TEXT);
    private final JLabel subtitle=new JLabel("Theo dõi hoạt động kinh doanh trong một giao diện.");
    private final Map<String,JPanel> pages=new LinkedHashMap<>();
    private final SalesDAO dao=new SalesDAO();

    public MainFrame(){
        Theme.baseFrame(this,"MARKETFLOW | Quản lý bán hàng");
        JPanel root=new JPanel(new BorderLayout());root.setBackground(Theme.BG);setContentPane(root);root.add(sidebar(),BorderLayout.WEST);
        JPanel content=new JPanel(new BorderLayout());content.setBackground(Theme.BG);content.add(topbar(),BorderLayout.NORTH);
        pages.put("dashboard",new DashboardPanel(dao));pages.put("products",new ProductsPanel(dao));pages.put("categories",new CategoriesPanel(dao));pages.put("customers",new CustomersPanel(dao));pages.put("sales",new SalesPanel(dao));pages.put("invoices",new InvoicesPanel(dao));
        pages.forEach((key,page)->host.add(page,key));host.setBackground(Theme.BG);content.add(host,BorderLayout.CENTER);root.add(content,BorderLayout.CENTER);
        showPage("dashboard","Tổng quan","Theo dõi hoạt động bán hàng và tồn kho.");
    }
    private JPanel sidebar(){
        JPanel side=new JPanel(new BorderLayout());side.setPreferredSize(new Dimension(238,0));side.setBackground(Theme.SIDEBAR);
        JPanel brand=new JPanel(new BorderLayout(0,4));brand.setBackground(Theme.SIDEBAR);brand.setBorder(new EmptyBorder(24,20,26,18));
        JLabel mark=new JLabel("MF");mark.setOpaque(true);mark.setBackground(Theme.ACCENT);mark.setForeground(Color.WHITE);mark.setFont(new Font("Segoe UI",Font.BOLD,20));mark.setHorizontalAlignment(SwingConstants.CENTER);mark.setPreferredSize(new Dimension(48,48));
        JPanel text=new JPanel(new GridLayout(2,1));text.setOpaque(false);JLabel name=new JLabel("MARKETFLOW");name.setForeground(Color.WHITE);name.setFont(new Font("Segoe UI",Font.BOLD,18));JLabel sub=new JLabel("SALES MANAGEMENT");sub.setForeground(new Color(179,183,194));sub.setFont(new Font("Segoe UI",Font.BOLD,9));text.add(name);text.add(sub);
        JPanel line=new JPanel(new FlowLayout(FlowLayout.LEFT,11,0));line.setOpaque(false);line.add(mark);line.add(text);brand.add(line,BorderLayout.CENTER);side.add(brand,BorderLayout.NORTH);
        JPanel nav=new JPanel();nav.setBackground(Theme.SIDEBAR);nav.setLayout(new BoxLayout(nav,BoxLayout.Y_AXIS));JLabel label=new JLabel("  QUẢN LÝ CỬA HÀNG");label.setForeground(new Color(151,158,174));label.setFont(new Font("Segoe UI",Font.BOLD,10));label.setBorder(new EmptyBorder(8,17,12,8));nav.add(label);
        nav.add(navButton("▦","Tổng quan","dashboard",true));nav.add(navButton("▣","Sản phẩm","products",false));nav.add(navButton("▤","Danh mục","categories",false));nav.add(navButton("♙","Khách hàng","customers",false));nav.add(navButton("＋","Bán hàng","sales",false));nav.add(navButton("▧","Hóa đơn","invoices",false));side.add(nav,BorderLayout.CENTER);
        JPanel foot=new JPanel(new BorderLayout(0,6));foot.setBackground(Theme.SIDEBAR);foot.setBorder(new EmptyBorder(15,20,19,20));foot.add(new JSeparator(),BorderLayout.NORTH);JLabel user=new JLabel("●  QUẢN TRỊ VIÊN");user.setForeground(new Color(224,226,233));user.setFont(new Font("Segoe UI",Font.BOLD,10));foot.add(user,BorderLayout.CENTER);JLabel ver=new JLabel("MARKETFLOW Desktop 1.0");ver.setForeground(new Color(151,158,174));ver.setFont(new Font("Segoe UI",Font.PLAIN,10));foot.add(ver,BorderLayout.SOUTH);side.add(foot,BorderLayout.SOUTH);return side;
    }
    private JButton navButton(String icon,String text,String key,boolean active){JButton b=new JButton("  "+icon+"     "+text);b.setHorizontalAlignment(SwingConstants.LEFT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,47));b.setFont(new Font("Segoe UI",Font.BOLD,13));b.setForeground(active?Color.WHITE:new Color(211,214,222));b.setBackground(active?Theme.SIDEBAR_ACTIVE:Theme.SIDEBAR);b.setBorder(new EmptyBorder(0,17,0,10));b.setFocusPainted(false);b.setOpaque(true);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.addActionListener(e->{for(Component c:b.getParent().getComponents())if(c instanceof JButton x){x.setBackground(Theme.SIDEBAR);x.setForeground(new Color(211,214,222));}b.setBackground(Theme.SIDEBAR_ACTIVE);b.setForeground(Color.WHITE);String t=switch(key){case"dashboard"->"Tổng quan";case"products"->"Quản lý sản phẩm";case"categories"->"Quản lý danh mục";case"customers"->"Quản lý khách hàng";case"sales"->"Lập hóa đơn bán hàng";default->"Danh sách hóa đơn";};String s=switch(key){case"dashboard"->"Theo dõi hoạt động bán hàng và tồn kho.";case"products"->"Quản lý thông tin, giá bán và số lượng tồn.";case"categories"->"Phân nhóm và quản lý danh mục sản phẩm.";case"customers"->"Lưu trữ thông tin và tra cứu khách hàng.";case"sales"->"Tạo hóa đơn, tính tiền và cập nhật tồn kho.";default->"Tra cứu hóa đơn và xem chi tiết giao dịch.";};showPage(key,t,s);});return b;}
    private JPanel topbar(){JPanel top=new JPanel(new BorderLayout());top.setBackground(Color.WHITE);top.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,0,Theme.BORDER),new EmptyBorder(14,26,14,26)));JPanel left=new JPanel(new GridLayout(2,1,0,3));left.setOpaque(false);left.add(title);subtitle.setForeground(Theme.MUTED);subtitle.setFont(Theme.FONT);left.add(subtitle);JLabel chip=new JLabel("●  JAVA SWING  •  SQL SERVER");chip.setForeground(Theme.ACCENT_DARK);chip.setBackground(new Color(255,239,230));chip.setOpaque(true);chip.setBorder(new EmptyBorder(9,12,9,12));chip.setFont(new Font("Segoe UI",Font.BOLD,10));top.add(left,BorderLayout.WEST);top.add(chip,BorderLayout.EAST);return top;}
    private void showPage(String key,String t,String s){title.setText(t);subtitle.setText(s);layout.show(host,key);JPanel p=pages.get(key);if(p instanceof Refreshable r)r.refreshData();}
    public interface Refreshable{void refreshData();}
}
