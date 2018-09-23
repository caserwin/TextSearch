package operate;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;


/******************************************
 * @author hduxyd
 * @data:2014-4-28下午02:29:42
 * @note:
 ******************************************/
public class DataPreProcess {

    private static String stopWordPath = "";
    private static String corpusPath = "";
    private static List<File> filePathList = null;
    /*
     *用于记录每个文件的内容，,并且进行分词，去除停止词，计算每个词出现的次数
     */
    private static Map<String, List<String>> wenbenziliao = null;

    public DataPreProcess(String stopWordPath, String corpusPath) {
        DataPreProcess.stopWordPath = stopWordPath;
        DataPreProcess.corpusPath = corpusPath;
//		this.jTextArea=jTextArea;
//		jTextArea.append("正在计算数据的TF和TF-IDF....\n");
    }
//	public DataPreProcess(String stopWordPath, String corpusPath){
//		this.stopWordPath = stopWordPath;
//		this.corpusPath = corpusPath;
//	}

    /**
     * @function:处理资料
     */
    public void corpusProcess() {
        filePathList = new ArrayList<>();
        System.out.println("@@@@@@@@@@文本资料库路径：" + corpusPath);
        File sampleDataDir = new File(corpusPath);
        // 得到样本分类目录
        File[] fileList = sampleDataDir.listFiles();
        if (fileList == null) {
            JOptionPane.showMessageDialog(null, "文本资料库为空");
            return;
        }
        // 读取所有的txt文件路径
        readfilePath(fileList);
        // 下一步得到，每个文件的路径，以及对于的分词，还有词的频率，每个词在多少个文件中出现
        wenbenziliao = new LinkedHashMap<>();
        // 用于记录每个单词在多少个文本中出现
        Map<String, Integer> wordCountList = new HashMap<>();
        for (File file : filePathList) {
            String mapPathAndName;
            Map<String, Long> wordList = readTxt(file.getPath());
            for (String key : wordList.keySet()) {
                List<String> list = new ArrayList<String>();
                if (!(wordCountList.containsKey(key))) {
                    wordCountList.put(key, 1);
                } else {
                    wordCountList.put(key, wordCountList.get(key) + 1);
                }
                mapPathAndName = file.getPath() + "*" + key;
                list.add(wordList.get(key).toString());
                list.add(String.valueOf(countTF(wordList, key)));
                wenbenziliao.put(mapPathAndName, list);
            }
        }
        // 计算每个词的idf的值
        Map<String, Float> wordIDFList = countIDF(wordCountList, filePathList.size());
        // 计算TF-IDF
        countTFIDF(wordIDFList);
    }

    public Map<String, List<String>> getwenbenziliao() {
        return wenbenziliao;
    }

    private void countTFIDF(Map<String, Float> wordIDFList) {
        for (String key : wenbenziliao.keySet()) {
            String[] strings = key.split("\\*");
            List<String> list = wenbenziliao.get(key);
            float ti = Float.parseFloat(list.get(1)) * wordIDFList.get(strings[1]);
            list.add(String.valueOf(ti));
            wenbenziliao.put(key, list);
        }
    }

    private Map<String, Float> countIDF(Map<String, Integer> wordCountList, int allFile) {
        // 用于记录每个单词在多少个文本中出现
        Map<String, Float> wordIDFList = new HashMap<>();
        for (String key : wordCountList.keySet()) {
            wordIDFList.put(key, (float) Math.log(allFile / wordCountList.get(key)));
        }
        return wordIDFList;
    }

    private float countTF(Map<String, Long> wordList, String word) {
        long sum = 0;
        for (String key : wordList.keySet()) {
            Long a = wordList.get(key);
            sum = sum + a;
        }
        return (float) wordList.get(word) / sum;
    }

    /**
     * @function:读取txt的文件内容,并且进行分词，去除停止词，计算每个词出现的次数
     */
    private Map<String, Long> readTxt(String filePath) {
        Map<String, Long> words = new HashMap<>();
        String read = "";
        StringBuilder readString = new StringBuilder();
        try {
            // 读取内容
            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(filePath), "GBK"));
            while ((read = bReader.readLine()) != null) {
                readString.append(read);
            }
            // 进行分词，进行去除停止词，words记录下每个文档的分词(key)和词频(value)！
            Tokenizer tokenizer = new Tokenizer();
            words = Tokenizer.segStr(readString.toString(), stopWordPath);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "读取文件出错！");
        }
        return words;
    }

    /**
     * @function:读取所有的txt文件的路径
     */
    private void readfilePath(File[] fileList) {
        for (File file : fileList) {
            if (file.isFile()) {
                // 是txt文件就读出内容
                filePathList.add(file);
            } else if (file.isDirectory()) {
                // 不是txt文件，继续遍历
                File[] list = file.listFiles();
                readfilePath(list);
            }
        }
    }

//	public static void main(String[] args) throws IOException {
////		String pString=Class.class.getClass().getResource(pString).getPath();
////		System.out.println(pString);
//		File file=new File("C:\\Users\\xyd\\Desktop\\文本分类1\\1.txt");
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//		
//		BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(
//				new FileOutputStream("C:\\Users\\xyd\\Desktop\\文本分类1\\8.txt")));
//		DataPreProcess dProcess = new DataPreProcess("",
//				"E:\\训练数据\\SogouC.mini");
//		dProcess.corpusProcess();
//
//		for (String key : wenbenziliao.keySet()) {
//			String[] strings = key.split("\\*");
//			List<String> list = wenbenziliao.get(key);
//			bWriter.write("路径：" + strings[0] + "   词语：" + strings[1]
//					+ "   本文出现次数：" + list.get(0) + "   词频：" + list.get(1)
//					+ "   TF-IDF" + list.get(2) + "\r\n");
//			System.out.println("路径：" + strings[0] + "   词语：" + strings[1]
//					+ "   本文出现次数：" + list.get(0) + "   词频：" + list.get(1)
//					+ "   TF-IDF" + list.get(2));
//		}
//		bWriter.flush();
//		bWriter.close();
//	}
}
