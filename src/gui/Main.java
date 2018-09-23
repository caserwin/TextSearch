package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import javaBean.TFIDFbean;
import javaBean.TFbean;
import javaBean.WordBean;
import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;
import operate.DataPreProcess;
import operate.GetTime;
import operate.Time;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import dao.DataService;

/**
 * @author hduxyd
 */
public class Main extends javax.swing.JFrame {
    private static JMenuBar menuBar;
    private static JMenu fileMenu;
    private static JMenu setMenu;
    private static JMenu helpMenu;
    private static JMenuItem exitMenuItem;
    private static JMenuItem CheckMenuItem1;
    private static JMenuItem aboutMenuItem;
    private static JMenuItem setMenuItem;
    private static JMenuItem setMenuItem1;
    private static JRadioButtonMenuItem setMenuItem2;
    private static JRadioButtonMenuItem setMenuItem3;
    private static JDialog aboutDialog;
    private static JPanel aboutContentPane;
    private static JLabel aboutVersionLabel;
    private static JFileChooser jFileChooser1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private static String stopWordPath = "";
    private static String corpusPath = "";
    /**
     * 设置默认选择TF-IDF方式查询
     */
    private static int a = 1;
    private static ButtonGroup bGroup = null;
    private static List<WordBean> wordBeans = null;
    private static Map<String[], List<String>> map = null;
    private static List<TFIDFbean> TFIDFbeanlist = null;
    private static List<TFbean> TFbeanlist = null;
    private static int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2;
    private static int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2;
    private DataService dataService = new DataService();

    /**
     * Creates new form Main
     */
    public Main() {
        initComponents();
        this.setJMenuBar(buildMenuBar());
        this.setSize(width + 250, height + 200);
        this.setLocationRelativeTo(null);
        this.setTitle("文本查找");
        // 设置显示时间
        Time time = new Time();
        time.init(this.jLabel3);
        time.start();
        this.jLabel4.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.jLabel4.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jLabel4MouseClicked();
            }
        });

        this.jTextArea1.setEditable(false);
        this.jTextArea1.append("1. IDF的计算是以e为底数");
        this.jTextField2.setText("已经存在数据库，那么请点此选择");
        this.jTextField2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.jTextField2.setEditable(false);
        setMenuItem3.setSelected(true);
        this.setResizable(false);
        this.jButton2.setEnabled(true);
        this.jButton3.setEnabled(false);

        this.jTextField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    showData();
                }
            }
        });
    }

    private void jLabel4MouseClicked() {
        if (TFIDFbeanlist == null && TFbeanlist == null) {
            JOptionPane.showMessageDialog(null, "没有更多文件");
        } else if (TFIDFbeanlist != null) {
            CheckUITFIDF checkUI = new CheckUITFIDF(TFIDFbeanlist);
            checkUI.setVisible(true);
        } else {
            CheckUITF checkUI = new CheckUITF(TFbeanlist);
            checkUI.setVisible(true);
        }
    }

    private static JMenuBar buildMenuBar() {
        menuBar = new JMenuBar();
        menuBar.add(getFileMenu());
        menuBar.add(getSetMenu());
        menuBar.add(getHelpMenu());
        return menuBar;
    }

    private static JMenu getSetMenu() {
        setMenu = new JMenu("设置");
        setMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
        setMenuItem = new JMenuItem("设置停止词");
        setMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
        // 为exitMenuItem添加点击事件
        setMenu.add(setMenuItem);
        setMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFileChooser1 = new JFileChooser();
                jFileChooser1.setDialogTitle("选择停止词");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("txt文件", "txt");
                jFileChooser1.setFileFilter(filter);
                if (jFileChooser1.showOpenDialog(null) == 0) {
                    stopWordPath = jFileChooser1.getSelectedFile().getPath() + ".txt";
                    System.out.println("stopWordPath:" + stopWordPath);
                }
            }
        });

        setMenuItem1 = new JMenuItem("设置文本资料库");
        setMenuItem1.setFont(new Font("Dialog", Font.PLAIN, 12));
        // 为exitMenuItem添加点击事件
        setMenu.add(setMenuItem1);
        setMenuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFileChooser1 = new JFileChooser();
                jFileChooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jFileChooser1.setDialogTitle("选择文本资料库");
                if (jFileChooser1.showOpenDialog(null) == 0) {
                    corpusPath = jFileChooser1.getSelectedFile().getPath();
                    System.out.println("corpusPath:" + corpusPath);
                }
            }
        });
        setMenu.addSeparator();
        setMenuItem2 = new JRadioButtonMenuItem("TF查找方式");
        setMenuItem2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TFIDFbeanlist = null;
                TFbeanlist = null;
                a = 0;
            }
        });
        setMenu.add(setMenuItem2);
        setMenuItem3 = new JRadioButtonMenuItem("TF-IDF查找方式");
        setMenuItem3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TFIDFbeanlist = null;
                TFbeanlist = null;
                a = 1;
            }
        });
        bGroup = new ButtonGroup();
        bGroup.add(setMenuItem2);
        bGroup.add(setMenuItem3);

        setMenu.add(setMenuItem3);
        return setMenu;
    }

    /**
     * 获得按钮菜单栏
     */
    private static JMenu getFileMenu() {
        fileMenu = new JMenu("文件");
        fileMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
        exitMenuItem = new JMenuItem("退出");
        exitMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));

        CheckMenuItem1 = new JMenuItem("查看默认停止词");
        CheckMenuItem1.setFont(new Font("Dialog", Font.PLAIN, 12));

        // 为exitMenuItem添加点击事件
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        CheckMenuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tool.openTxt("res/ext_stopword.dic");
            }
        });

        fileMenu.add(CheckMenuItem1);
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    /**
     * 获得按钮菜单栏
     */
    private static JMenu getHelpMenu() {
        helpMenu = new JMenu("帮助");
        helpMenu.setFont(new Font("Dialog", Font.PLAIN, 12));

        aboutMenuItem = new JMenuItem("关于");
        aboutMenuItem.setFont(new Font("Dialog", Font.PLAIN, 12));

        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog aboutDialog = getAboutDialog();
                aboutDialog.pack();
                aboutDialog.setLocationRelativeTo(null);
                aboutDialog.setVisible(true);
            }
        });
        helpMenu.add(aboutMenuItem);
        return helpMenu;
    }

    private static JDialog getAboutDialog() {
        aboutDialog = new JDialog();
        aboutDialog.setTitle("信息");
        aboutDialog.setPreferredSize(new Dimension(250, 140));

        aboutContentPane = new JPanel();
        aboutContentPane.setLayout(new BorderLayout());
        aboutContentPane.add(getAboutVersionLabel(), BorderLayout.CENTER);

        aboutDialog.setContentPane(aboutContentPane);
        return aboutDialog;
    }

    private static Component getAboutVersionLabel() {
        String string = "<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Author:xyd&&casyd_xue@163.com<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;系统版本：文本资源查找软件V1.0";
        aboutVersionLabel = new JLabel(string, JLabel.CENTER);
        aboutVersionLabel.setHorizontalAlignment(2);
        aboutVersionLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        return aboutVersionLabel;
    }

    // GEN-BEGIN:initComponents
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jTabbedPane1.addTab("\u7cfb\u7edf\u8bf4\u660e", jScrollPane1);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jTabbedPane1.addTab("\u7ed3\u679c\u8f93\u51fa", jScrollPane2);

        jLabel1
            .setText("  \u8bf7\u8f93\u5165\u67e5\u8be2\u5185\u5bb9\uff1a--->");

        jButton1.setText("\u67e5\u627e");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("\u6587\u672c\u8d44\u6599\u5e93\u9884\u5904\u7406");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField2MouseClicked(evt);
            }
        });
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jButton3.setText("\u8d44\u6599\u5199\u5230\u6570\u636e\u5e93\u4e2d");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel5.setText("------>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
            jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout
            .setHorizontalGroup(jPanel1Layout
                                    .createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(
                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                        jPanel1Layout
                                            .createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(
                                                jPanel1Layout
                                                    .createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                        false)
                                                    .addComponent(
                                                        jLabel1,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        Short.MAX_VALUE)
                                                    .addComponent(
                                                        jButton2,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        Short.MAX_VALUE))
                                            .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(
                                                jPanel1Layout
                                                    .createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                        jPanel1Layout
                                                            .createSequentialGroup()
                                                            .addComponent(
                                                                jLabel5)
                                                            .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(
                                                                jButton3)
                                                            .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(
                                                                jTextField2,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                181,
                                                                Short.MAX_VALUE))
                                                    .addComponent(
                                                        jTextField1,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        361,
                                                        Short.MAX_VALUE))
                                            .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(
                                                jButton1,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                173,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addContainerGap()));
        jPanel1Layout
            .setVerticalGroup(jPanel1Layout
                                  .createParallelGroup(
                                      javax.swing.GroupLayout.Alignment.LEADING)
                                  .addGroup(
                                      jPanel1Layout
                                          .createSequentialGroup()
                                          .addContainerGap()
                                          .addGroup(
                                              jPanel1Layout
                                                  .createParallelGroup(
                                                      javax.swing.GroupLayout.Alignment.LEADING)
                                                  .addGroup(
                                                      jPanel1Layout
                                                          .createSequentialGroup()
                                                          .addGroup(
                                                              jPanel1Layout
                                                                  .createParallelGroup(
                                                                      javax.swing.GroupLayout.Alignment.LEADING)
                                                                  .addGroup(
                                                                      jPanel1Layout
                                                                          .createParallelGroup(
                                                                              javax.swing.GroupLayout.Alignment.BASELINE)
                                                                          .addComponent(
                                                                              jButton2,
                                                                              javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                              33,
                                                                              Short.MAX_VALUE)
                                                                          .addComponent(
                                                                              jTextField2,
                                                                              javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                              33,
                                                                              Short.MAX_VALUE))
                                                                  .addGroup(
                                                                      jPanel1Layout
                                                                          .createParallelGroup(
                                                                              javax.swing.GroupLayout.Alignment.BASELINE)
                                                                          .addComponent(
                                                                              jLabel5,
                                                                              javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                              30,
                                                                              javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                          .addComponent(
                                                                              jButton3,
                                                                              javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                              33,
                                                                              Short.MAX_VALUE)))
                                                          .addPreferredGap(
                                                              javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                          .addGroup(
                                                              jPanel1Layout
                                                                  .createParallelGroup(
                                                                      javax.swing.GroupLayout.Alignment.BASELINE)
                                                                  .addComponent(
                                                                      jLabel1,
                                                                      javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                      31,
                                                                      javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                  .addComponent(
                                                                      jTextField1,
                                                                      javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                      32,
                                                                      Short.MAX_VALUE)))
                                                  .addComponent(
                                                      jButton1,
                                                      javax.swing.GroupLayout.Alignment.TRAILING,
                                                      javax.swing.GroupLayout.PREFERRED_SIZE,
                                                      73,
                                                      javax.swing.GroupLayout.PREFERRED_SIZE))));

        jLabel2.setText("\u7cfb\u7edf\u65f6\u95f4\uff1a");

        jLabel3.setText("jLabel3");

        jLabel4.setText("\u66f4\u591a\u5185\u5bb9...");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
            jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout
            .setHorizontalGroup(jPanel2Layout
                                    .createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(
                                        jPanel2Layout
                                            .createSequentialGroup()
                                            .addComponent(
                                                jLabel2,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                72,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(
                                                jLabel3,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                201,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                337, Short.MAX_VALUE)
                                            .addComponent(
                                                jLabel4,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                84,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jPanel1,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE).addComponent(jSeparator1,
                    javax.swing.GroupLayout.Alignment.TRAILING,
                    javax.swing.GroupLayout.DEFAULT_SIZE, 701,
                    Short.MAX_VALUE).addComponent(jTabbedPane1,
                    javax.swing.GroupLayout.DEFAULT_SIZE, 701,
                    Short.MAX_VALUE));
        jPanel2Layout
            .setVerticalGroup(jPanel2Layout
                                  .createParallelGroup(
                                      javax.swing.GroupLayout.Alignment.LEADING)
                                  .addGroup(
                                      javax.swing.GroupLayout.Alignment.TRAILING,
                                      jPanel2Layout
                                          .createSequentialGroup()
                                          .addComponent(
                                              jPanel1,
                                              javax.swing.GroupLayout.PREFERRED_SIZE,
                                              javax.swing.GroupLayout.DEFAULT_SIZE,
                                              javax.swing.GroupLayout.PREFERRED_SIZE)
                                          .addPreferredGap(
                                              javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addComponent(
                                              jSeparator1,
                                              javax.swing.GroupLayout.PREFERRED_SIZE,
                                              javax.swing.GroupLayout.DEFAULT_SIZE,
                                              javax.swing.GroupLayout.PREFERRED_SIZE)
                                          .addPreferredGap(
                                              javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addComponent(
                                              jTabbedPane1,
                                              javax.swing.GroupLayout.PREFERRED_SIZE,
                                              height,
                                              javax.swing.GroupLayout.PREFERRED_SIZE)
                                          .addPreferredGap(
                                              javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                              jPanel2Layout
                                                  .createParallelGroup(
                                                      javax.swing.GroupLayout.Alignment.BASELINE)
                                                  .addComponent(
                                                      jLabel3,
                                                      javax.swing.GroupLayout.DEFAULT_SIZE,
                                                      39,
                                                      Short.MAX_VALUE)
                                                  .addComponent(
                                                      jLabel2,
                                                      javax.swing.GroupLayout.DEFAULT_SIZE,
                                                      39,
                                                      Short.MAX_VALUE)
                                                  .addComponent(
                                                      jLabel4,
                                                      javax.swing.GroupLayout.PREFERRED_SIZE,
                                                      39,
                                                      javax.swing.GroupLayout.PREFERRED_SIZE))));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
            getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
            javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup().addContainerGap().addComponent(
                jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(
            javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup().addComponent(jPanel2,
                javax.swing.GroupLayout.PREFERRED_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE)));

        pack();
    }// </editor-fold>

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    @SuppressWarnings("static-access")
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        // 遍历map,并且封装好到集合wordbeans
        for (String[] key : map.keySet()) {
            WordBean wordBean = new WordBean();
            List<String> list = map.get(key);
            wordBean.setFilepath(key[0]);
            wordBean.setWord(key[1]);
            wordBean.setTimes(Integer.parseInt(list.get(0)));
            wordBean.setTF(Double.parseDouble(list.get(1)));
            wordBean.setTFIDF(Double.parseDouble(list.get(2)));
            wordBean.setPinyin(PinyinHelper.convertToPinyinString(key[1], "", PinyinFormat.WITHOUT_TONE));
            wordBeans.add(wordBean);
        }
        // 先删除数据框中所有记录，再写到数据库中
        jTextArea2.append(GetTime.getNowtime() + "  开始删除原来的资料\n");
        jTextArea2.paintImmediately(jTextArea2.getBounds());
        boolean is = dataService.deleteAll();
        if (is) {
            jTextArea2.append(GetTime.getNowtime() + "  原来的资料删除完毕\n");
            jTextArea2.paintImmediately(jTextArea2.getBounds());
        }
        jTextArea2.append(GetTime.getNowtime() + "  开始写入数据库....\n");
        jTextArea2.paintImmediately(jTextArea2.getBounds());
        long a = System.currentTimeMillis();
        dataService.addtoDB(wordBeans);
        System.out.println(System.currentTimeMillis() - a);
        jTextArea2.append(GetTime.getNowtime() + "  写入完毕\n");
        jTextArea2.paintImmediately(jTextArea2.getBounds());
        this.jButton2.setEnabled(true);
        this.jButton3.setEnabled(false);
    }

    private void jTextField2MouseClicked(java.awt.event.MouseEvent evt) {
        String filePath = "";
        jFileChooser1 = new JFileChooser();
        jFileChooser1.setDialogTitle("选择数据库文件");
        jFileChooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (jFileChooser1.showOpenDialog(null) == 0) {
            filePath = jFileChooser1.getSelectedFile().getPath();
        }
        if (!"".equals(filePath)) {
            this.jTextField2.setText(filePath);
        }
        dataService = new DataService(filePath);
        jTextArea2.append(GetTime.getNowtime() + "  已经选择好资料文件\n");
        jTextArea2.paintImmediately(jTextArea2.getBounds());
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        wordBeans = new ArrayList<>();
        this.jTabbedPane1.setSelectedIndex(1);
        if ("".equals(stopWordPath)) {
            JOptionPane.showMessageDialog(null, "无选择停止词文件，选择默认停止词！");
        }
        if ("".equals(corpusPath)) {
            JOptionPane.showMessageDialog(null, "请先选文本资料库");
            return;
        }
        // 对文本资料库进行处理
        jTextArea2.append(GetTime.getNowtime() + "  正在计算数据.....\n");
        jTextArea2.paintImmediately(jTextArea2.getBounds());
        DataPreProcess dataPreProcess = new DataPreProcess(stopWordPath, corpusPath);
        dataPreProcess.corpusProcess();
        // 得到文本资料处理后的文件map
        map = dataPreProcess.getwenbenziliao();
        jTextArea2.append(GetTime.getNowtime() + "  数据计算完成.....\n");
        jTextArea2.paintImmediately(jTextArea2.getBounds());
        this.jButton2.setEnabled(false);
        this.jButton3.setEnabled(true);
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        showData();
    }

    /**
     * @function:数据展示
     */
    private void showData() {
        this.jTextArea2.setText("");
        this.jTabbedPane1.setSelectedIndex(1);
        String str = this.jTextField1.getText();
        if ("".equals(str)) {
            JOptionPane.showMessageDialog(null, "输入为空!");
            return;
        }
        resOutput(str, str);
    }

    private void resOutput(String string, String str) {
        int sum = 0;
        StringBuilder result = new StringBuilder();
        // 分词
        Reader input = new StringReader(string);
        // 智能分词关闭（对分词的精度影响很大）
        IKSegmenter iks = new IKSegmenter(input, true);
        Lexeme lex;
        try {
            while ((lex = iks.next()) != null) {
                result.append(lex.getLexemeText()).append("|");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.jTextArea2.append("---------------------------------------------------\n");
        jTextArea2.paintImmediately(jTextArea2.getBounds());
        this.jTextArea2.append(GetTime.getNowtime() + "  " + str + "的分词结果：" + result + "\n");
        jTextArea2.paintImmediately(jTextArea2.getBounds());
        this.jTextArea2.append("---------------------------------------------------\n");
        jTextArea2.paintImmediately(jTextArea2.getBounds());
        // 查询数据库中的结果
        String[] strings = result.toString().split("\\|");
        if (a == 1) {
            long start = System.currentTimeMillis();
            TFIDFbeanlist = dataService.queryTextByTfidf(strings);
            for (TFIDFbean tfidFbean : TFIDFbeanlist) {
                if (sum == 10) {
                    break;
                }
                this.jTextArea2.append(GetTime.getNowtime() + "  文件路径：" + tfidFbean.getFilepath() + "  相关性：" + tfidFbean.getTfidf() + "\n");
                this.jTextArea2.append(readFromTxt(tfidFbean.getFilepath()));
                this.jTextArea2.append("---------------------------------------------------\n");
                jTextArea2.paintImmediately(jTextArea2.getBounds());
                sum++;
            }
            this.jTextArea2.append("一共花费时间：" + (System.currentTimeMillis() - start) + "ms\n");
        } else if (a == 0) {
            long start = System.currentTimeMillis();
            TFbeanlist = dataService.queryTextByTF(strings);
            for (TFbean tFbean : TFbeanlist) {
                if (sum == 10) {
                    break;
                }
                this.jTextArea2.append(GetTime.getNowtime() + "  文件路径：" + tFbean.getFilepath() + "  相关性：" + tFbean.getTf() + "\n");
                this.jTextArea2.append(readFromTxt(tFbean.getFilepath()));
                this.jTextArea2.append("---------------------------------------------------\n");
                sum++;
            }
            this.jTextArea2.append("一共花费时间：" + (System.currentTimeMillis() - start) + "ms\n");
        }
    }


    private Set<String> matchPattern(String word) {
        List<String> list = new ArrayList<>();
        String pattern = "[\u4e00-\u9fa5]+|\\d+|[a-zA-Z]+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(word);
        while (m.find()) {
            list.add(m.group());
        }
        return dataService.queryWord(list);
    }

    private String readFromTxt(String path) {
        StringBuilder readStr = new StringBuilder();
        String read;
        int a = 0;
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(path), "GBK"));
            while ((read = bReader.readLine()) != null) {
                if (!"".equals(read)) {
                    if (a == 3) {
                        readStr.append(read).append("....\n");
                        break;
                    } else {
                        readStr.append(read).append("\n");
                    }
                    a++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readStr.toString();
    }

    /**
     *
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Main().setVisible(true);
            }
        });
    }
}