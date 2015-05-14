package edu.shu.ltp4j.test;
import java.util.ArrayList;
import java.util.List;

import edu.hit.ir.ltp4j.Pair;
import edu.hit.ir.ltp4j.SRL;

/**
 * 
 * <p>
 * ClassName TestSrl
 * </p>
 * <p>
 * Description 语义角色标注接口
 * </p>
 * 
 * @author TKPad wangx89@126.com
 *         <p>
 *         Date 2015年5月7日 下午10:11:46
 *         </p>
 * @version V1.0.0
 *
 */
public class TestSrl {

	public static void main(String[] args) {
		SRL.create("ltp_data/srl");
		ArrayList<String> words = new ArrayList<String>();
		words.add("一把手");
		words.add("亲自");
		words.add("过问");
		words.add("。");
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("n");
		tags.add("d");
		tags.add("v");
		tags.add("wp");
		ArrayList<String> ners = new ArrayList<String>();
		ners.add("O");
		ners.add("O");
		ners.add("O");
		ners.add("O");
		ArrayList<Integer> heads = new ArrayList<Integer>();
		heads.add(2);
		heads.add(2);
		heads.add(-1);
		heads.add(2);
		ArrayList<String> deprels = new ArrayList<String>();
		deprels.add("SBV");
		deprels.add("ADV");
		deprels.add("HED");
		deprels.add("WP");
		List<Pair<Integer, List<Pair<String, Pair<Integer, Integer>>>>> srls = new ArrayList<Pair<Integer, List<Pair<String, Pair<Integer, Integer>>>>>();
		SRL.srl(words, tags, ners, heads, deprels, srls);
		SRL.srl(words, tags, ners, heads, deprels, srls);
		System.out.println(srls.size());
		for (int i = 0; i < srls.size(); ++i) {
			System.out.println(srls.get(i).first + ":");
			for (int j = 0; j < srls.get(i).second.size(); ++j) {
				System.out.println("   tpye = " + srls.get(i).second.get(j).first + " beg = "
						+ srls.get(i).second.get(j).second.first + " end = " + srls.get(i).second.get(j).second.second);
			}
		}
		SRL.release();
	}
}