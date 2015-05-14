package edu.shu.ltp4j.test;

import org.junit.Test;

import edu.shu.ltp4j.util.LTPUtil;

public class TestGetFileLTML {
	@Test
	public void test() {
		String path = "test.txt";
		LTPUtil ltpUtil = new LTPUtil();
		ltpUtil.setNe(true);
		ltpUtil.setParser(true);
		ltpUtil.setPos(true);
		ltpUtil.setSrl(true);
		String ltml = ltpUtil.getLTML(path);
		System.out.println(ltml);
	}
}
