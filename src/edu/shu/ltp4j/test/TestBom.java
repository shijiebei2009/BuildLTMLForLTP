package edu.shu.ltp4j.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;

/**
 * 
 * <p>
 * ClassName TestBom
 * </p>
 * <p>
 * Description 先按照UTF-8编码读取文本，之后跳过前三个字符，重新构建一个新的字符串
 * </p>
 * 
 * @author TKPad wangx89@126.com
 *         <p>
 *         Date 2015年5月12日 下午8:08:32
 *         </p>
 * @version V1.0.0
 *
 */
public class TestBom {
	public static void main(String[] args) {
		// test.txt是带BOM编码的UTF-8编码
		BOMInputStream bomWithOut;
		try {
			// 默认排除掉BOM，但是使用hasBOM方法仍然可以检测到BOM的存在
			bomWithOut = new BOMInputStream(new FileInputStream(new File("C:/Users/TKPad/Desktop/test.txt")));
			if (bomWithOut.hasBOM()) {
				System.out.println("当前流中包含BOM！");
			}
			byte[] bytes = new byte[1024];
			int length = 0;
			String res = null;
			while ((length = bomWithOut.read(bytes)) != -1) {
				res += new String(bytes, 0, length);
			}
			System.out.println("无BOM读取：" + res.getBytes().length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 当然你也可以包含BOM读取
		BOMInputStream bomWithIn;
		try {
			bomWithIn = new BOMInputStream(new FileInputStream(new File("C:/Users/TKPad/Desktop/test.txt")), true);
			if (bomWithIn.hasBOM()) {
				System.out.println("当前流中包含BOM！");
			}
			byte[] bytes = new byte[1024];
			int length = 0;
			String res = null;
			while ((length = bomWithIn.read(bytes)) != -1) {
				res += new String(bytes, 0, length);
			}
			System.out.println("有BOM读取：" + res.getBytes().length);

		} catch (IOException e) {
			e.printStackTrace();
		}

		// 注意，此处要注释掉哦！！
		try {
			// 也可以指定检测多种编码的bom，但目前仅支持UTF-8/UTF-16LE/UTF-16BE三种，对于UTF32之类不支持。
			@SuppressWarnings({ "unused", "resource" })
			BOMInputStream bomIn = new BOMInputStream(new FileInputStream(new File("")), ByteOrderMark.UTF_16LE,
					ByteOrderMark.UTF_16BE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
