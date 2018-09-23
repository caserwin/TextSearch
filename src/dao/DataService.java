/**
 * 
 */
package dao;

import gui.IfIschinese;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javaBean.TFIDFbean;
import javaBean.TFbean;
import javaBean.WordBean;

/******************************************
 * @function：
 * @author hduxyd
 * @data:2014-5-11下午05:54:14
 * @department：杭州电子科技大学通信工程学院
 * @note:
 ******************************************/
public class DataService {

	Connection connection = null;
	PreparedStatement pre = null;
	ResultSet result = null;

	/**
	 * @function:
	 * @param wordBeans
	 * @return
	 */
	public boolean addtoDB(List<WordBean> wordBeans) {
		boolean is = false;
		JDBC jdbc = new JDBC();
		System.out.println(wordBeans.size());
		try {
			connection = jdbc.getConnection();
			connection.setAutoCommit(false);  
			pre = connection.prepareStatement("insert into TB(filepath,word,times,TF,TF_IDF,pinyin) values(?,?,?,?,?,?)");
			for (int i = 0; i < wordBeans.size(); i++) {
				pre.setString(1, wordBeans.get(i).getFilepath());
				pre.setString(2, wordBeans.get(i).getWord());
				pre.setInt(3, wordBeans.get(i).getTimes());
				pre.setDouble(4, wordBeans.get(i).getTF());
				pre.setDouble(5, wordBeans.get(i).getTFIDF());
				pre.setString(6, wordBeans.get(i).getPinyin());
				pre.addBatch();
			}
			pre.executeBatch(); //批量执行   
			connection.commit();//提交事务  
			is = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			jdbc.closeConnection();
		}
		return is;
	}

	
	/**
	 * @function:
	 * @return
	 */
	public boolean deleteAll() {
		boolean is = false;
		JDBC jdbc = new JDBC();
		connection = jdbc.getConnection();
		try {
			pre = connection.prepareStatement("delete from TB");
			pre.execute();
			is = true;
//			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			jdbc.closeConnection();
		}
		return is;
	}
	
	/**
	 * @function:
	 * @return
	 */
	public Set<String> queryWord(List<String> list) {
		Set<String> set=new LinkedHashSet<String>();
		JDBC jdbc = new JDBC();
		connection = jdbc.getConnection();
		String sql="select * from TB where";
		int a=0;
		for (String str : list) {
			if(a!=0){
				sql+=" and";
			}
			if (IfIschinese.isChinese(str)) {
				sql+=" word like '%"+str+"%'";
			}else{
				sql+=" pinyin like '%"+str+"%'";
			}
			a++;
		}
		System.out.println(sql);
		try {
			pre = connection.prepareStatement(sql);
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
	 * @function:
	 * @return
	 */
	public List<WordBean> queryAll() {
		List<WordBean> wordBeans = new ArrayList<WordBean>();
		JDBC jdbc = new JDBC();
		connection = jdbc.getConnection();
		try {
			pre = connection.prepareStatement("select * from TB");
			result = pre.executeQuery();
			while (result.next()) {
				WordBean wordBean = new WordBean();
				String filepath = result.getString("filepath");
				String word = result.getString("word");
				int times = result.getInt("times");
				double TF = result.getDouble("TF");
				double TF_IDF = result.getDouble("TF_IDF");
				wordBean.setFilepath(filepath);
				wordBean.setTimes(times);
				wordBean.setWord(word);
				wordBean.setTF(TF);
				wordBean.setTFIDF(TF_IDF);
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
	 * 
	* @function:根据TFIDF查找
	* @param strings
	* @return
	 */
	public List<TFIDFbean> queryTextByTfidf(String[] strings) {
		List<TFIDFbean> list=new ArrayList<TFIDFbean>();
		int sum=0;
		JDBC jdbc = new JDBC();
		connection = jdbc.getConnection();
		//select filepath,sum(TF_IDF) as correlation from TB where word  in ('联想','价格战','高端') group by filepath ORDER BY sum(TF_IDF) desc
		try {
			StringBuilder sql=new StringBuilder();
			sql.append("select filepath,sum(TF_IDF) as correlation from TB where word  in (");
			for (int i = 0; i < strings.length; i++) {
				if (i==strings.length-1) {
					sql.append("?) group by filepath ORDER BY sum(TF_IDF) desc");
				}else {
					sql.append("?,");
				}
			}
			pre = connection.prepareStatement(sql.toString());
			for (int i = 0; i < strings.length; i++) {
				pre.setString(1+i, strings[i]);
			}
			result = pre.executeQuery();
			while (result.next()) {
				TFIDFbean tFbean=new TFIDFbean();
				String filepath=result.getString("filepath");
				double correlation=result.getDouble("correlation");
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
	 * 
	* @function:根据TF查找
	* @param strings
	* @return
	 */
	public List<TFbean> queryTextByTF(String[] strings) {
		List<TFbean> list=new ArrayList<TFbean>();
		int sum=0;
		JDBC jdbc = new JDBC();
		connection = jdbc.getConnection();
		try {
			StringBuilder sql=new StringBuilder();
			sql.append("select filepath,sum(TF) as correlation from TB where word  in (");
			for (int i = 0; i < strings.length; i++) {
				if (i==strings.length-1) {
					sql.append("?) group by filepath ORDER BY sum(TF) desc");
				}else {
					sql.append("?,");
				}
			}
			System.out.println(sql.toString());
			pre = connection.prepareStatement(sql.toString());
			for (int i = 0; i < strings.length; i++) {
				pre.setString(1+i, strings[i]);
			}
			result = pre.executeQuery();
			while (result.next()) {
				TFbean tFbean=new TFbean();
				String filepath=result.getString("filepath");
				double correlation=result.getDouble("correlation");
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
