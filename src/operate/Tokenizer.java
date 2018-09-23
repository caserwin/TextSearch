package operate;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;


public class Tokenizer {
	public static Map<String, Long> segStr(String content, String stopWordsPath) {
		// 分词
		Reader input = new StringReader(content);
		// 智能分词关闭（对分词的精度影响很大）
		IKSegmenter iks = new IKSegmenter(input, true);
		Lexeme lexeme = null;
		Map<String, Long> words = new LinkedHashMap<String, Long>();
		try {
			while ((lexeme = iks.next()) != null) {
				String ifNotIsStopWord = lexeme.getLexemeText();
				StopWordsProcess sProcess = new StopWordsProcess(stopWordsPath);
				//如果不是停止词的话，进行下一步操作
				if (sProcess.dropStopWords(ifNotIsStopWord)) {
					if (words.containsKey(lexeme.getLexemeText())) {
						words.put(lexeme.getLexemeText(), words.get(lexeme
								.getLexemeText()) + 1);
					} else {
						words.put(lexeme.getLexemeText(), 1L);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return words;
	}
}
