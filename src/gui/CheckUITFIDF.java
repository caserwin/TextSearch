package gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import javaBean.TFIDFbean;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class CheckUITFIDF extends JFrame {
	private JPanel jPanel1 = new JPanel();
	private JScrollPane jScrollPane1 = new JScrollPane();
	private JTable jTable1 = new JTable();
	private static List<TFIDFbean> TFIDFbeanlist = null;

	public CheckUITFIDF(List<TFIDFbean> TFIDFbeanlist) {
		CheckUITFIDF.TFIDFbeanlist = TFIDFbeanlist;
		initComponents();
		this.setLocationRelativeTo(null);
		this.setSize(600, 300);
	}

	private void jTable1Event() {
		int i = jTable1.getSelectedRow();
		try {
			Runtime.getRuntime().exec(
					"notepad.exe " + TFIDFbeanlist.get(i).getFilepath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initComponents() {
		// 统计计数
		int sum = 0;
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle("TFIDF前30个相关文件");
		Object[][] data = null;
		String[] columnName = { "编号", "文件路径", "相关性" };
		DefaultTableModel dtm = new DefaultTableModel(data, columnName) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;// 返回true表示能编辑，false表示整个jtable不能被编辑
			}
		};
		for (int i = 0; i < TFIDFbeanlist.size(); i++) {
			if (sum == 30) {
				break;
			}
			Object[] row = { i + 1, TFIDFbeanlist.get(i).getFilepath(),
					TFIDFbeanlist.get(i).getTfidf() };
			dtm.addRow(row);
			sum++;
		}
		this.jTable1.setModel(dtm);
		this.jTable1.addMouseListener(new MouseAdapter() {
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