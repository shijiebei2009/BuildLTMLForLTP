package edu.shu.ltp4j.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BOMInputStream;

import edu.hit.ir.ltp4j.Segmentor;

/**
 * 
 * <p>
 * ClassName TestSegment
 * </p>
 * <p>
 * Description 分词接口
 * </p>
 * 
 * @author TKPad wangx89@126.com
 *         <p>
 *         Date 2015年5月7日 下午10:09:23
 *         </p>
 * @version V1.0.0
 *
 */
public class TestSegment {
	public static void main(String[] args) {
		if (Segmentor.create("ltp_data/cws.model") < 0) {
			System.err.println("load failed");
			return;
		}

		String content = null;
		try {
			@SuppressWarnings("resource")
			BOMInputStream bomWithOut = new BOMInputStream(new FileInputStream(new File("C:/Users/TKPad/Desktop/test.txt")));
			content = FileUtils.readFileToString(new File("C:/Users/TKPad/Desktop/test.txt"), "UTF-8");
			if (bomWithOut.hasBOM()) {
				// 包含有Bom
				content = filterBOM(content);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// String sent = "我是中国人";
		List<String> words = new ArrayList<String>();
		int size = Segmentor.segment(content, words);
		for (int i = 0; i < size; i++) {
			System.out.println(i + "->" + words.get(i).trim());
			if (i == size - 1) {
				System.out.println();
			} else {
				System.out.print("\t");
			}
		}
		Segmentor.release();
	}
	private static String filterBOM(String content) {
		if ("".equals(content) || null == content) {
			return content;
		}
		byte[] bytes = content.getBytes();
		return new String(bytes, 3, bytes.length - 3);
	}
}
