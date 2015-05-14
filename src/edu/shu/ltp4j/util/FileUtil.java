package edu.shu.ltp4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;

public class FileUtil {
	/**
	 * 
	 * <p>
	 * Title: getParagraph
	 * </p>
	 * <p>
	 * Description: 根据给出的文本路径，读取文本内容，并按照段落切分，以段落为单位，存储在List集合中
	 * </p>
	 * 
	 * @param filePath
	 * @return List&lt;String&gt; 段落集合
	 * @throws IOException
	 *
	 */
	public static List<String> getParagraph(String filePath) throws IOException {
		List<String> res = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();// 拼接读取的内容
		InputStream isr = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
		BOMInputStream bomWithOut = new BOMInputStream(isr);
		String temp = "";
		byte[] bytes = new byte[1024];
		int length = 0;
		while ((length = bomWithOut.read(bytes)) != -1) {
			temp += new String(bytes, 0, length);
		}
		sb.append(temp + "\n");
		// \s A whitespace character: [ \t\n\x0B\f\r]
		String p[] = sb.toString().split("\\s*\n");
		for (String string : p) {
			res.add(string.replaceAll("\\s*", ""));
		}
		if (bomWithOut != null)
			bomWithOut.close();
		if (isr != null)
			isr.close();
		return res;
	}

	/**
	 * 
	 * <p>
	 * Title: filterBOM
	 * </p>
	 * <p>
	 * Description: 该方法用来将content中的BOM标记过滤掉
	 * </p>
	 * 
	 * @param content
	 * @return
	 *
	 */
	public static String filterBOM(String content) {
		if ("".equals(content) || null == content) {
			return content;
		}
		byte[] bytes = content.getBytes();
		return new String(bytes, 3, bytes.length - 3);
	}
}
