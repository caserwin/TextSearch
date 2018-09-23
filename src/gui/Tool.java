package gui;

import operate.Constant;
import java.io.IOException;

/**
 * @author hduxyd
 */
public class Tool {
    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (char c : ch) {
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                   || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                   || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                   || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                   || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                   || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    public static void openTxt(String path) {
        try {
            String os = System.getProperty("os.name");
            if (os.toLowerCase().contains(Constant.OS)) {
                Runtime.getRuntime().exec("open " + path);
            } else {
                Runtime.getRuntime().exec("notepad.exe " + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}