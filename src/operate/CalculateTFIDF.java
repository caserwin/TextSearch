package operate;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import java.io.*;
import java.util.*;

/**
 * @author hduxyd
 */
public class CalculateTFIDF {

    private static ArrayList<String> FileList = new ArrayList<>();

    public static List<String> readDirs(String filepath) {
        File file = new File(filepath);
        if (!file.isDirectory()) {
            System.out.println("输入的[]");
            System.out.println("filepath:" + file.getAbsolutePath());
        } else {
            String[] flist = file.list();
            for (String aFlist : flist) {
                File newfile = new File(filepath + "\\" + aFlist);
                if (!newfile.isDirectory()) {
                    FileList.add(newfile.getAbsolutePath());
                } else if (newfile.isDirectory()) {
                    readDirs(filepath + "\\" + aFlist);
                }
            }
        }
        return FileList;
    }

    /**
     * read file
     */
    public static String readFile(String file) throws IOException {
        StringBuffer strSb = new StringBuffer();
        InputStreamReader inStrR = new InputStreamReader(new FileInputStream(file), "gbk");
        BufferedReader br = new BufferedReader(inStrR);
        String line = br.readLine();
        while (line != null) {
            strSb.append(line).append("\r\n");
            line = br.readLine();
        }

        return strSb.toString();
    }

    public static ArrayList<String> cutWords(String file) throws IOException {
        ArrayList<String> words = new ArrayList<>();
        String text = CalculateTFIDF.readFile(file);
        StringReader sr = new StringReader(text);
        IKSegmenter ik = new IKSegmenter(sr, true);

        Lexeme lex = null;
        while ((lex = ik.next()) != null) {
            words.add(lex.getLexemeText());
        }

        return words;
    }

    public static HashMap<String, Integer> normalTF(ArrayList<String> cutwords) {
        HashMap<String, Integer> resTF = new HashMap<>();

        for (String word : cutwords) {
            if (resTF.get(word) == null) {
                resTF.put(word, 1);
                System.out.println(word);
            } else {
                resTF.put(word, resTF.get(word) + 1);
                System.out.println(word);
            }
        }
        return resTF;
    }

    public static HashMap<String, Float> tf(ArrayList<String> cutwords) {
        HashMap<String, Float> resTF = new HashMap<>();
        int wordLen = cutwords.size();
        HashMap<String, Integer> intTF = CalculateTFIDF.normalTF(cutwords);

        // from TF
        for (Object o : intTF.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            resTF.put(entry.getKey().toString(), Float.parseFloat(entry.getValue().toString()) / wordLen);
            System.out.println(entry.getKey().toString() + " = "
                                   + Float.parseFloat(entry.getValue().toString()) / wordLen);
        }
        return resTF;
    }

    public static HashMap<String, HashMap<String, Integer>> normalTFAllFiles(
        String dirc) throws IOException {
        HashMap<String, HashMap<String, Integer>> allNormalTF = new HashMap<>();

        List<String> filelist = CalculateTFIDF.readDirs(dirc);
        for (String file : filelist) {
            ArrayList<String> cutwords = CalculateTFIDF.cutWords(file);
            HashMap<String, Integer> dict = CalculateTFIDF.normalTF(cutwords);
            allNormalTF.put(file, dict);
        }
        return allNormalTF;
    }

    /**
     * tf for all file
     */
    public static HashMap<String, HashMap<String, Float>> tfAllFiles(String dirc)
        throws IOException {
        HashMap<String, HashMap<String, Float>> allTF = new HashMap<>();
        List<String> filelist = CalculateTFIDF.readDirs(dirc);

        for (String file : filelist) {
            HashMap<String, Float> dict;
            ArrayList<String> cutwords = CalculateTFIDF.cutWords(file);
            dict = CalculateTFIDF.tf(cutwords);
            allTF.put(file, dict);
        }
        return allTF;
    }

    public static HashMap<String, Float> idf(
        HashMap<String, HashMap<String, Float>> all_tf) {
        HashMap<String, Float> resIdf = new HashMap<>();
        HashMap<String, Integer> dict = new HashMap<>();
        int docNum = FileList.size();

        for (String aFileList : FileList) {
            HashMap<String, Float> temp = all_tf.get(aFileList);
            Iterator iter = temp.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String word = entry.getKey().toString();
                if (dict.get(word) == null) {
                    dict.put(word, 1);
                } else {
                    dict.put(word, dict.get(word) + 1);
                }
            }
        }
        System.out.println("IDF for every word is:");
        for (Object o : dict.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            float value = (float) Math.log(docNum / Float.parseFloat(entry.getValue().toString()));
            resIdf.put(entry.getKey().toString(), value);
            System.out.println(entry.getKey().toString() + " = " + value);
        }
        return resIdf;
    }

    public static void tf_idf(HashMap<String, HashMap<String, Float>> all_tf, HashMap<String, Float> idfs) {
        HashMap<String, HashMap<String, Float>> resTfIdf = new HashMap<>();

        for (String filepath : FileList) {
            HashMap<String, Float> tfidf = new HashMap<>();
            HashMap<String, Float> temp = all_tf.get(filepath);
            Iterator iter = temp.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String word = entry.getKey().toString();
                Float value = Float.parseFloat(entry.getValue().toString()) * idfs.get(word);
                tfidf.put(word, value);
            }
            resTfIdf.put(filepath, tfidf);
        }
        System.out.println("TF-IDF for Every file is :");
        disTfIdf(resTfIdf);
    }

    public static void disTfIdf(HashMap<String, HashMap<String, Float>> tfidf) {
        for (Object o1 : tfidf.entrySet()) {
            Map.Entry entrys = (Map.Entry) o1;
            System.out.println("FileName: " + entrys.getKey().toString());
            System.out.print("{");
            HashMap<String, Float> temp = (HashMap<String, Float>) entrys.getValue();
            for (Object o : temp.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                System.out.print(entry.getKey().toString() + " = " + entry.getValue().toString() + ", ");
            }
            System.out.println("}");
        }
    }

    public static void main(String[] args) throws IOException {
        String file = "C:\\Users\\xyd\\Desktop\\文本分类\\搜狗新闻分类文本资料库\\SogouC.mini";

        HashMap<String, HashMap<String, Float>> all_tf = tfAllFiles(file);
        System.out.println();
        HashMap<String, Float> idfs = idf(all_tf);
        System.out.println();
        tf_idf(all_tf, idfs);
    }
}