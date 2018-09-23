package gui;

import javaBean.TFbean;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @author hduxyd
 */
public class CheckUITF extends JFrame {
    private JPanel jPanel1 = new JPanel();
    private JScrollPane jScrollPane1 = new JScrollPane();
    private JTable jTable1 = new JTable();
    private List<TFbean> tfbeanlist;

    /**
     * Creates new form CheckUI
     */
    public CheckUITF(List<TFbean> tfbeanlist) {
        this.tfbeanlist = tfbeanlist;
        initComponents();
        this.setLocationRelativeTo(null);
        this.setSize(600, 300);
    }

    private void jTable1Event() {
        int i = jTable1.getSelectedRow();
        Tool.openTxt(tfbeanlist.get(i).getFilepath());
    }

    private void initComponents() {
        int sum = 0;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("TF前30个相关文件");
        Object[][] data = null;
        String[] columnName = {"编号", "文件路径", "相关性"};
        DefaultTableModel dtm = new DefaultTableModel(data, columnName) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 返回true表示能编辑，false表示整个jtable不能被编辑
                return false;
            }
        };
        for (int i = 0; i < tfbeanlist.size(); i++) {
            if (sum == 30) {
                break;
            }
            Object[] row = {i + 1, tfbeanlist.get(i).getFilepath(),
                tfbeanlist.get(i).getTf()};
            dtm.addRow(row);
            sum++;
        }
        this.jTable1.setModel(dtm);
        this.jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jTable1Event();
            }
        });

        jScrollPane1.setViewportView(jTable1);
        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
            GroupLayout.Alignment.LEADING).addComponent(jScrollPane1,
            GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 461,
            Short.MAX_VALUE));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
            GroupLayout.Alignment.LEADING).addComponent(jScrollPane1,
            GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
            GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup().addContainerGap().addComponent(
                jPanel1, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(
            GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup().addContainerGap().addComponent(
                jPanel1, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap()));

        pack();
    }
}