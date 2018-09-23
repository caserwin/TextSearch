package javaBean;

/******************************************
 * @author hduxyd
 * @data:2014-5-11上午09:49:27
 * @note:
 ******************************************/
public class WordBean {

	private String filepath;
	private String word;
	private int times;
	private double TF;
	private double TFIDF;
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	private String pinyin;
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public double getTF() {
		return TF;
	}
	public void setTF(double tF) {
		TF = tF;
	}
	public double getTFIDF() {
		return TFIDF;
	}
	public void setTFIDF(double tFIDF) {
		TFIDF = tFIDF;
	}
}
