package edu.shu.ltp4j.test;
import java.util.ArrayList;
import java.util.List;

import edu.hit.ir.ltp4j.NER;

/**
 * 
 * <p>
 * ClassName TestNer
 * </p>
 * <p>
 * Description 命名实体识别接口
 * </p>
 * 
 * @author TKPad wangx89@126.com
 *         <p>
 *         Date 2015年5月7日 下午10:08:26
 *         </p>
 * @version V1.0.0
 *
 */
public class TestNer {

	public static void main(String[] args) {
		if (NER.create("ltp_data/ner.model") < 0) {
			System.err.println("load failed");
			return;
		}
		List<String> words = new ArrayList<String>();
		List<String> tags = new ArrayList<String>();
		List<String> ners = new ArrayList<String>();
		words.add("中国");
		tags.add("ns");
		words.add("国际");
		tags.add("n");
		words.add("广播");
		tags.add("n");
		words.add("电台");
		tags.add("n");
		words.add("创办");
		tags.add("v");
		words.add("于");
		tags.add("p");
		words.add("1941年");
		tags.add("m");
		words.add("12月");
		tags.add("m");
		words.add("3日");
		tags.add("m");
		words.add("。");
		tags.add("wp");

		NER.recognize(words, tags, ners);

		for (int i = 0; i < words.size(); i++) {
			System.out.println(ners.get(i));
		}

		NER.release();

	}
}