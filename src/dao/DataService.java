package dao;

import gui.Tool;
import javaBean.TFIDFbean;
import javaBean.TFbean;
import javaBean.WordBean;
import operate.Constant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/******************************************
 * @author hduxyd
 * @data:2014-5-11下午05:54:14
 * @note:
 ******************************************/
public class DataService {

    private Connection connection = null;
    private PreparedStatement pre = null;
    private ResultSet result = null;
    private String dbPath = Constant.DEFAULT_DB;

    public DataService() {
    }

    public DataService(String path) {
        this.dbPath = path;
        System.out.println(this.dbPath);
    }

    /**
     * @function:
     */
    public boolean addtoDB(List<WordBean> wordBeans) {
        boolean is = false;
        System.out.println(wordBeans.size());
        JDBC jdbc = new JDBC(this.dbPath);
        try {
            connection = jdbc.getConnection();
            connection.setAutoCommit(false);
            pre = connection.prepareStatement("insert into TB(filepath,word,times,TF,TF_IDF,pinyin) values(?,?,?,?,?,?)");
            for (WordBean wordBean : wordBeans) {
                pre.setString(1, wordBean.getFilepath());
                pre.setString(2, wordBean.getWord());
                pre.setInt(3, wordBean.getTimes());
                pre.setDouble(4, wordBean.getTF());
                pre.setDouble(5, wordBean.getTFIDF());
                pre.setString(6, wordBean.getPinyin());
                pre.addBatch();
            }
            //批量执行
            pre.executeBatch();
            //提交事务
            connection.commit();
            is = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            jdbc.closeConnection();
        }
        return is;
    }

    /**
     * @return
     * @function:
     */
    public boolean deleteAll() {
        boolean is = false;
        JDBC jdbc = new JDBC(this.dbPath);
        connection = jdbc.getConnection();
        try {
            pre = connection.prepareStatement("delete from TB");
            pre.execute();
            is = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            jdbc.closeConnection();
        }
        return is;
    }

    /**
     * @return
     * @function:
     */
    public Set<String> queryWord(List<String> list) {
        Set<String> set = new LinkedHashSet<>();
        JDBC jdbc = new JDBC(this.dbPath);
        connection = jdbc.getConnection();
        StringBuilder sql = new StringBuilder("select * from TB where");
        int a = 0;
        for (String str : list) {
            if (a != 0) {
                sql.append(" and");
            }
            if (Tool.isChinese(str)) {
                sql.append(" word like '%").append(str).append("%'");
            } else {
                sql.append(" pinyin like '%").append(str).append("%'");
            }
            a++;
        }
        System.out.println(sql);
        try {
            pre = connection.prepareStatement(sql.toString());
            result = pre.executeQuery();
            while (result.next()) {
                String word = result.getString("word");
                set.add(word);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            jdbc.closeConnection();
        }
        return set;
    }

    /**
     * @return
     * @function:
     */
    public List<WordBean> queryAll() {
        List<WordBean> wordBeans = new ArrayList<>();
        JDBC jdbc = new JDBC(this.dbPath);
        connection = jdbc.getConnection();
        try {
            pre = connection.prepareStatement("select * from TB");
            result = pre.executeQuery();
            while (result.next()) {
                WordBean wordBean = new WordBean();
                String filepath = result.getString("filepath");
                String word = result.getString("word");
                int times = result.getInt("times");
                double tf = result.getDouble("TF");
                double tfIdf = result.getDouble("TF_IDF");
                wordBean.setFilepath(filepath);
                wordBean.setTimes(times);
                wordBean.setWord(word);
                wordBean.setTF(tf);
                wordBean.setTFIDF(tfIdf);
                wordBeans.add(wordBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            jdbc.closeConnection();
        }
        return wordBeans;
    }

    /**
     * @return
     * @function:根据TFIDF查找
     */
    public List<TFIDFbean> queryTextByTfidf(String[] strings) {
        List<TFIDFbean> list = new ArrayList<>();
        JDBC jdbc = new JDBC(this.dbPath);
        connection = jdbc.getConnection();
        //select filepath,sum(TF_IDF) as correlation from TB where word  in ('联想','价格战','高端') group by filepath ORDER BY sum(TF_IDF) desc
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("select filepath,sum(TF_IDF) as correlation from TB where word  in (");
            for (int i = 0; i < strings.length; i++) {
                if (i == strings.length - 1) {
                    sql.append("?) group by filepath ORDER BY sum(TF_IDF) desc");
                } else {
                    sql.append("?,");
                }
            }
            pre = connection.prepareStatement(sql.toString());
            for (int i = 0; i < strings.length; i++) {
                pre.setString(1 + i, strings[i]);
            }
            result = pre.executeQuery();
            while (result.next()) {
                TFIDFbean tFbean = new TFIDFbean();
                String filepath = result.getString("filepath");
                double correlation = result.getDouble("correlation");
                tFbean.setFilepath(filepath);
                tFbean.setTfidf(correlation);
                list.add(tFbean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @return
     * @function:根据TF查找
     */
    public List<TFbean> queryTextByTF(String[] strings) {
        List<TFbean> list = new ArrayList<>();
        JDBC jdbc = new JDBC(this.dbPath);
        connection = jdbc.getConnection();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("select filepath,sum(TF) as correlation from TB where word  in (");
            for (int i = 0; i < strings.length; i++) {
                if (i == strings.length - 1) {
                    sql.append("?) group by filepath ORDER BY sum(TF) desc");
                } else {
                    sql.append("?,");
                }
            }
            System.out.println(sql.toString());
            pre = connection.prepareStatement(sql.toString());
            for (int i = 0; i < strings.length; i++) {
                pre.setString(1 + i, strings[i]);
            }
            result = pre.executeQuery();
            while (result.next()) {
                TFbean tFbean = new TFbean();
                String filepath = result.getString("filepath");
                double correlation = result.getDouble("correlation");
                tFbean.setFilepath(filepath);
                tFbean.setTf(correlation);
                list.add(tFbean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
