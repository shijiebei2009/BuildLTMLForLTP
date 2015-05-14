package edu.shu.ltp4j.test;

import org.junit.Test;

import edu.shu.ltp4j.util.LTPUtil;

public class TestGetSentectLTML {
	@Test
	public void testSentence() {
		LTPUtil ltpUtil = new LTPUtil();
		ltpUtil.setNe(true);
		ltpUtil.setParser(true);
		ltpUtil.setPos(true);
		ltpUtil.setSrl(true);
		String s = "事件语料的建立对事件及其关系识别和推理有重要意义，因此针对该语料库的建立进行研究有一定的理论意义和应用价值。";
		String ltmlBySentence = ltpUtil.getLTMLBySentence(s);
		System.out.println(ltmlBySentence);
	}
}
