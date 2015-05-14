package edu.shu.ltp4j.test;
import java.util.ArrayList;
import java.util.List;

import edu.hit.ir.ltp4j.Postagger;

/**
 * 
 * <p>
 * ClassName TestPostag
 * </p>
 * <p>
 * Description 词性标注接口
 * </p>
 * 
 * @author TKPad wangx89@126.com
 *         <p>
 *         Date 2015年5月7日 下午10:09:55
 *         </p>
 * @version V1.0.0
 *
 */
public class TestPostag {
	public static void main(String[] args) {
		if (Postagger.create("ltp_data/pos.model") < 0) {
			System.err.println("load failed");
			return;
		}

		List<String> words = new ArrayList<String>();
		words.add("我");
		words.add("是");
		words.add("中国");
		words.add("人");
		words.add("！");
		List<String> postags = new ArrayList<String>();

		int size = Postagger.postag(words, postags);
		for (int i = 0; i < size; i++) {
			System.out.print(words.get(i) + "_" + postags.get(i));
			if (i == size - 1) {
				System.out.println();
			} else {
				System.out.print("|");
			}
		}
		Postagger.release();
	}
}