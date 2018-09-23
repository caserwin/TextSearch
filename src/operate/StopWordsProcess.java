package operate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author hduxyd
 */
public class StopWordsProcess {
	private static Set<String> stopWordsSet = new HashSet<String>();
	private String stopWordsPath = "";
	// 最常用的停止词
//	private String[] cn_stopword = { "也","了","仍","从","以","使","则","却","又","及"
//			,"对","就","并","很","或","把","是","的","着","给","而","被","让","在","还",
//			"比","等","当","与","于","但",};
	
	public StopWordsProcess() {
		setStopWords();
	}

	public StopWordsProcess(String stopWordsPath) {
		this.stopWordsPath = stopWordsPath;
		setStopWords();
	}

	/**
	 * @function:设置停止词
	 */
	public void setStopWords() {
		// 先设置最常用的的停止词
//		for (String string : cn_stopword) {
//			stopWordsSet.add(string);
//		}
		// 如果路径不为空的话，再设置其他停止词
		if (!("".equals(stopWordsPath))) {
			String read = "";
			try {
				BufferedReader bReader = new BufferedReader(
						new InputStreamReader(
								new FileInputStream(stopWordsPath), "GBK"));
				while ((read = bReader.readLine()) != null) {
					stopWordsSet.add(read.trim());
				}
				bReader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Set<String> getstopWordsSet() {
		return stopWordsSet;
	}

	/**
	 * @function:判断是不是停止词
	 */
	public Set<String> dropStopWords(Set<String> oldWords) {
		Set<String> set = new HashSet<String>();
		for (String word : oldWords) {
			if (!(StopWordsProcess.getstopWordsSet().contains(word))) {
				// 不是停用词
				set.add(word);
			}
		}
		return set;
	}

	/**
	* @function:传入一个词语，判断是不是停止词
	 */
	public boolean dropStopWords(String oldWords) {
		boolean is = false;
		if (!(StopWordsProcess.getstopWordsSet().contains(oldWords))) {
			// 不是停用词
			is = true;
		}
		return is;
	}
}
