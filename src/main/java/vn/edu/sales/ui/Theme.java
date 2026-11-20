package vn.edu.sales.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class Theme {
    private Theme() { }
    public static final Color SIDEBAR = new Color(35, 42, 58);
    public static final Color SIDEBAR_ACTIVE = new Color(70, 82, 108);
    public static final Color ACCENT = new Color(236, 112, 62);
    public static final Color ACCENT_DARK = new Color(196, 78, 38);
    public static final Color BG = new Color(246, 245, 242);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(44, 48, 57);
    public static final Color MUTED = new Color(121, 126, 136);
    public static final Color BORDER = new Color(229, 228, 224);
    public static final Color GREEN = new Color(48, 145, 104);
    public static final Color RED = new Color(190, 68, 68);
    public static final Font FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public static void baseFrame(JFrame f,String title){f.setTitle(title);f.setSize(1320,850);f.setMinimumSize(new Dimension(1120,720));f.setLocationRelativeTo(null);f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);}
    public static JLabel heading(String text,int size,Color color){JLabel l=new JLabel(text);l.setFont(FONT.deriveFont(Font.BOLD,(float)size));l.setForeground(color);return l;}
    public static JButton button(String text,Color bg){JButton b=new JButton(text);b.setFont(FONT.deriveFont(Font.BOLD,12.5f));b.setForeground(bg.equals(Color.WHITE)?TEXT:Color.WHITE);b.setBackground(bg);b.setFocusPainted(false);b.setBorder(new EmptyBorder(10,15,10,15));b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setOpaque(true);return b;}
    public static JButton primary(String text){return button(text,ACCENT);}
    public static JButton secondary(String text){return button(text,new Color(235,234,230));}
    public static JButton danger(String text){return button(text,RED);}
    public static JPanel page(String title,String subtitle){JPanel p=new JPanel(new BorderLayout(0,18));p.setBackground(BG);p.setBorder(new EmptyBorder(23,27,26,27));JPanel h=new JPanel(new GridLayout(2,1,0,5));h.setOpaque(false);h.add(heading(title,24,TEXT));JLabel s=new JLabel(subtitle);s.setFont(FONT);s.setForeground(MUTED);h.add(s);p.add(h,BorderLayout.NORTH);return p;}
    public static JPanel card(){JPanel p=new JPanel();p.setBackground(CARD);p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),new EmptyBorder(15,17,15,17)));return p;}
    public static DefaultTableModel model(String... cols){return new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};}
    public static JTable table(DefaultTableModel model){JTable t=new JTable(model);t.setFont(FONT);t.setRowHeight(32);t.setForeground(TEXT);t.setGridColor(new Color(239,238,235));t.setSelectionBackground(new Color(255,232,219));t.setSelectionForeground(TEXT);t.setShowVerticalLines(false);t.setFillsViewportHeight(true);t.setAutoCreateRowSorter(true);t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);JTableHeader h=t.getTableHeader();h.setFont(FONT.deriveFont(Font.BOLD));h.setBackground(new Color(242,240,236));h.setForeground(TEXT);h.setPreferredSize(new Dimension(0,38));return t;}
    public static JScrollPane scroll(JTable t){JScrollPane s=new JScrollPane(t);s.setBorder(BorderFactory.createLineBorder(BORDER));s.getViewport().setBackground(Color.WHITE);return s;}
    public static JTextField searchField(){JTextField f=new JTextField();f.setFont(FONT);f.setPreferredSize(new Dimension(250,38));f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),new EmptyBorder(6,10,6,10)));return f;}
    public static void error(Component c,Exception ex){String m=ex.getMessage()==null?ex.toString():ex.getMessage();JOptionPane.showMessageDialog(c,m,"Không thể thực hiện",JOptionPane.ERROR_MESSAGE);}
    public static boolean confirm(Component c,String m){return JOptionPane.showConfirmDialog(c,m,"Xác nhận",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;}
    public static void info(Component c,String m){JOptionPane.showMessageDialog(c,m,"Thông báo",JOptionPane.INFORMATION_MESSAGE);}
    public static void addField(JPanel p,String label,JComponent input){JPanel cell=new JPanel(new BorderLayout(0,4));cell.setOpaque(false);JLabel l=new JLabel(label);l.setFont(FONT.deriveFont(Font.BOLD,11.5f));l.setForeground(MUTED);cell.add(l,BorderLayout.NORTH);cell.add(input,BorderLayout.CENTER);p.add(cell);}
}
