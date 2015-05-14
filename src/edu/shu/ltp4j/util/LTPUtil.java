package edu.shu.ltp4j.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.hit.ir.ltp4j.NER;
import edu.hit.ir.ltp4j.Pair;
import edu.hit.ir.ltp4j.Parser;
import edu.hit.ir.ltp4j.Postagger;
import edu.hit.ir.ltp4j.SRL;
import edu.hit.ir.ltp4j.Segmentor;
import edu.shu.ltp4j.constant.LTPTag;

/**
 * 
 * <p>
 * ClassName LTPUtil
 * </p>
 * <p>
 * Description 构造LTML的工具类
 * </p>
 * 
 * @author TKPad wangx89@126.com
 *         <p>
 *         Date 2015年5月11日 下午8:58:37
 *         </p>
 * @version V1.0.0
 *
 */
public class LTPUtil {
	// 如果 pos=”y”则分词结点中必须包含pos 属性；
	// 如果 ne=”y”则分词结点中必须包含ne 属性；
	// 如果 parser=”y”则分词结点中必须包含parent 及relate 属性；
	// 如果 srl=”y”则凡是谓词(predicate)的分词会包含若干个arg 结点；
	private boolean pos;
	private boolean ne;
	private boolean parser;
	private boolean srl;
	private List<Integer> heads;
	private List<String> deprels;

	/**
	 * <p>
	 * Title: getSegment
	 * </p>
	 * <p>
	 * Description: 获取文本的LTP分词结果
	 * </p>
	 * 
	 * @param sentence
	 * @return Map：key->id，value->分词结果
	 *
	 */
	private Map<Integer, String> getSegment(String sentence) {
		Map<Integer, String> res = new HashMap<Integer, String>();
		if (Segmentor.create("ltp_data/cws.model") < 0) {
			System.err.println("load failed");
			return null;
		}
		List<String> words = new ArrayList<String>();
		if (sentence != null) {
			sentence.trim();
		}
		int size = Segmentor.segment(sentence, words);
		for (int i = 0; i < size; i++) {
			res.put(i, words.get(i));
		}
		Segmentor.release();
		return res;
	}

	/**
	 * 
	 * <p>
	 * Title: getPosTag
	 * </p>
	 * <p>
	 * Description: 获取LTP的词性标注结果
	 * </p>
	 * 
	 * @param segment
	 * @return Map：key->id，value->标注词性
	 *
	 */
	private Map<Integer, String> getPosTag(Map<Integer, String> wordsMap) {
		if (Postagger.create("ltp_data/pos.model") < 0) {
			System.err.println("load failed");
			return null;
		}
		Iterator<Integer> keyIter = wordsMap.keySet().iterator();// 以key作为迭代器遍历
		List<String> words = new ArrayList<String>();
		while (keyIter.hasNext()) {
			Integer key = keyIter.next();
			String value = wordsMap.get(key);
			words.add(value);
		}
		List<String> postags = new ArrayList<String>();
		int size = Postagger.postag(words, postags);
		Map<Integer, String> res = new HashMap<Integer, String>();

		for (int i = 0; i < size; i++) {
			res.put(i, postags.get(i));
		}
		Postagger.release();
		return res;
	}

	/**
	 * 
	 * <p>
	 * Title: getNer
	 * </p>
	 * <p>
	 * Description: 获取LTP的命名实体识别结果
	 * </p>
	 * 
	 * @param wordsMap
	 * @param tagsMap
	 * 
	 * @return Map：key->id，value->命名实体识别
	 *
	 */
	private Map<Integer, String> getNer(Map<Integer, String> wordsMap, Map<Integer, String> tagsMap) {
		if (NER.create("ltp_data/ner.model") < 0) {
			System.err.println("load failed");
			return null;
		}
		// 将分词结果加入链表之中
		List<String> words = getList(wordsMap);
		// 将词性标注结果加入链表之中
		List<String> tags = getList(tagsMap);
		List<String> ners = new ArrayList<String>();
		Map<Integer, String> nersMap = new HashMap<Integer, String>();
		NER.recognize(words, tags, ners);
		for (int i = 0; i < words.size(); i++) {
			nersMap.put(i, ners.get(i));
		}
		NER.release();
		return nersMap;
	}

	/**
	 * 
	 * <p>
	 * Title: getList
	 * </p>
	 * <p>
	 * Description: 将Map的value提取出来转成List链表
	 * </p>
	 * 
	 * @param map
	 * @return List：Map的value转成的链表
	 *
	 */
	private List<String> getList(Map<Integer, String> sourceMap) {
		Iterator<Integer> mapIter = sourceMap.keySet().iterator();
		List<String> res = new ArrayList<String>();
		while (mapIter.hasNext()) {
			Integer key = mapIter.next();
			String value = sourceMap.get(key);
			res.add(value);
		}
		return res;
	}

	/**
	 * 
	 * <p>
	 * Title: getParser
	 * </p>
	 * <p>
	 * Description: 获取LTP的依存句法分析结果
	 * </p>
	 * 
	 * @param wordsMap
	 * @param tagsMap
	 * @return Map：key->id，value->依存分析
	 *
	 */
	private Map<Integer, String> getParser(Map<Integer, String> wordsMap, Map<Integer, String> tagsMap) {
		if (Parser.create("ltp_data/parser.model") < 0) {
			System.err.println("load failed");
			return null;
		}
		List<String> words = getList(wordsMap);
		List<String> tags = getList(tagsMap);
		heads = new ArrayList<Integer>();
		deprels = new ArrayList<String>();
		Map<Integer, String> parserRes = new HashMap<Integer, String>();
		int size = Parser.parse(words, tags, heads, deprels);
		for (int i = 0; i < size; i++) {
			parserRes.put(i, deprels.get(i));
		}
		Parser.release();
		return parserRes;
	}

	/**
	 * 
	 * <p>
	 * Title: getSrl
	 * </p>
	 * <p>
	 * Description: 封装了LTP的语义角色标注的分析结果
	 * </p>
	 * 
	 * @param wordsMap
	 * @param tagsMap
	 * @param nersMap
	 * @return
	 *
	 */
	private List<Pair<Integer, List<Pair<String, Pair<Integer, Integer>>>>> getSrls(Map<Integer, String> wordsMap,
			Map<Integer, String> tagsMap, Map<Integer, String> nersMap) {
		SRL.create("ltp_data/srl");
		List<String> words = getList(wordsMap);
		List<String> tags = getList(tagsMap);
		List<String> ners = getList(nersMap);
		// 此处注意把heads作减一操作
		changeList(heads);
		List<Pair<Integer, List<Pair<String, Pair<Integer, Integer>>>>> srls = new ArrayList<Pair<Integer, List<Pair<String, Pair<Integer, Integer>>>>>();
		SRL.srl(words, tags, ners, heads, deprels, srls);
		// System.out.println(srls.size());
		// for (int i = 0; i < srls.size(); ++i) {
		// System.out.println(srls.get(i).first + ":");
		// for (int j = 0; j < srls.get(i).second.size(); ++j) {
		// System.out.println("   tpye = " + srls.get(i).second.get(j).first + " beg = "
		// + srls.get(i).second.get(j).second.first + " end = " + srls.get(i).second.get(j).second.second);
		// }
		// }
		SRL.release();
		return srls;
	}

	/**
	 * 
	 * <p>
	 * Title: changeList
	 * </p>
	 * <p>
	 * Description:修改List链表里的每一个值，将每一个值做减1操作
	 * </p>
	 * 
	 * @param list
	 *
	 */
	private void changeList(List<Integer> list) {
		ListIterator<Integer> listIterator = list.listIterator();
		while (listIterator.hasNext()) {
			Integer value = listIterator.next();
			int index = listIterator.previousIndex();
			list.set(index, value - 1);
		}
	}

	@Test
	public void testListIter() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		Iterator<Integer> iterator = list.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			Integer next = iterator.next();
			list.set(index, next - 1);
			index++;
		}
		System.out.println(list);
	}

	/**
	 * 
	 * <p>
	 * Title: getSentLTML
	 * </p>
	 * <p>
	 * Description: 该函数封装的是对一个句子的分析结果，将该句话的分析结果以标准的LTML格式返回<br/>
	 * demo：给我一个句子"我爱你美国！"，返回给你如下结果：<br/>
	 * &lt;sent id="0" cont="我爱你美国！"&gt;<br/>
	 * &lt;word id="0" cont="我" pos="r" ne="O" parent="1" relate="SBV"/&gt; <br/>
	 * &lt;word id="1" cont="爱" pos="v" ne="O" parent="-1" relate="HED"&gt; <br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;arg id="0" type="A0" beg="0" end="0"/&gt; <br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;arg id="1" type="A1" beg="2" end="3"/&gt; <br/>
	 * &lt;/word&gt; <br/>
	 * &lt;word id="2" cont="你" pos="r" ne="O" parent="3" relate="ATT"/&gt;<br/>
	 * &lt;word id="3" cont="美国" pos="ns" ne="S-Ns" parent="1" relate="VOB"/&gt; <br/>
	 * &lt;word id="4" cont="！" pos="wp" ne="O" parent="1" relate="WP"/&gt;<br/>
	 * &lt;/sent&gt;
	 * 
	 * @param sentence
	 *            一个独立的句子
	 * @param sentenceId
	 *            句子的编号
	 * @param paraElement
	 *            该句子所在父节点元素
	 * @param document
	 *            Document的实例对象
	 * @return 句子的LTML分析结果
	 *
	 */
	public void getSentLTML(String sentence, Integer sentenceId, Element paraElement, Document document) {
		// 获取分词结果
		Map<Integer, String> wordsMap = getSegment(sentence);
		// 获取词性标注结果
		Map<Integer, String> posTag = getPosTag(wordsMap);
		// 获取命名实体识别结果
		Map<Integer, String> nersMap = getNer(wordsMap, posTag);
		// 获取依存句法分析结果
		Map<Integer, String> parserMap = getParser(wordsMap, posTag);
		// 获取语义角色标注接口
		List<Pair<Integer, List<Pair<String, Pair<Integer, Integer>>>>> srls = getSrls(wordsMap, posTag, nersMap);

		Iterator<Integer> keyIter = wordsMap.keySet().iterator();
		int index = 0;// 索引号，当srls中的srls.get(i).first等于索引号时，说明要在word标签中加入语义角色标注内容了
		Element sentElement = document.createElement(LTPTag.SENT);
		// 貌似先添加的属性，反而在标签中出现在后边
		sentElement.setAttribute(LTPTag.CONT, sentence);
		sentElement.setAttribute(LTPTag.ID, String.valueOf(sentenceId));
		// document.appendChild(sentElement);
		int wordId = 0;// word标签的id值
		while (keyIter.hasNext()) {
			Element wordElement = document.createElement(LTPTag.WORD);
			Integer key = keyIter.next();
			String word = wordsMap.get(key);
			wordElement.setAttribute(LTPTag.CONT, word);
			wordElement.setAttribute(LTPTag.ID, String.valueOf(wordId));// 加入分词id
			if (isPos()) {
				// 需要标注词性
				String posValue = posTag.get(key);
				wordElement.setAttribute(LTPTag.POS, posValue);
			}
			if (isNe()) {
				// 需要标注命名实体
				String nerValue = nersMap.get(key);
				wordElement.setAttribute(LTPTag.NE, nerValue);
			}

			if (isParser()) {
				// 需要标注parent和relate
				String parserValue = parserMap.get(key);
				wordElement.setAttribute(LTPTag.PSR_PARENT, String.valueOf(heads.get(wordId)));
				wordElement.setAttribute(LTPTag.PSR_RELATE, parserValue);
			}
			if (isSrl()) {
				// 需要标注语义角色
				// 开始遍历srls，如果存在与wordId相同的srls.get(i).first，那么需要添加<arg>标签
				for (int i = 0; i < srls.size(); i++) {
					// 获取到所在word节点的ID
					Integer id = srls.get(i).first;
					if (id == wordId) {
						// 说明该WORD标签下需要添加<arg>标签
						for (int j = 0; j < srls.get(i).second.size(); j++) {
							String typeValue = srls.get(i).second.get(j).first;
							Integer begValue = srls.get(i).second.get(j).second.first;
							Integer endValue = srls.get(i).second.get(j).second.second;
							// 开始装配<arg>节点
							Element argElement = document.createElement(LTPTag.SRL_ARG);
							argElement.setAttribute(LTPTag.ID, String.valueOf(j));
							argElement.setAttribute(LTPTag.SRL_TYPE, typeValue);
							argElement.setAttribute(LTPTag.SRL_BEGIN, String.valueOf(begValue));
							argElement.setAttribute(LTPTag.SRL_END, String.valueOf(endValue));
							wordElement.appendChild(argElement);// 将其装配到word节点
						}
					}
				}
			}

			sentElement.appendChild(wordElement);// 装配word节点
			paraElement.appendChild(sentElement);// 装配Sentence节点
			// 将索引递增
			wordId++;
		}
		// System.out.println("-------------------");
		// System.out.println(wordsMap);
		// System.out.println(posTag);
		// System.out.println(nersMap);
		// System.out.println(parser);
		// System.out.println(srls.size());
		// for (int i = 0; i < srls.size(); i++) {
		// System.out.println(srls.get(i).first);
		// for (int j = 0; j < srls.get(i).second.size(); j++) {
		// System.out.println("   tpye = " + srls.get(i).second.get(j).first + " beg = "
		// + srls.get(i).second.get(j).second.first + " end = " + srls.get(i).second.get(j).second.second);
		// }
		// }
		// System.out.println(heads);
		// System.out.println(deprels);
		// System.out.println("-------------------");
	}

	/**
	 * 
	 * <p>
	 * Title: getLTML
	 * </p>
	 * <p>
	 * Description: 根据指定的文件路径获取LTML返回结果
	 * </p>
	 * 
	 * @param filePath
	 *            ，默认文件是UTF-8编码
	 * @return LTML格式的分析结果
	 *
	 */
	public String getLTML(String filePath) {
		// 切分段落
		List<String> paragraphs = null;
		try {
			paragraphs = FileUtil.getParagraph(filePath);
		} catch (IOException e1) {
			System.err.println("切分段落：" + e1.getMessage());
			e1.printStackTrace();
		}
		Iterator<String> paraIter = paragraphs.iterator();
		Document document = getDocument();

		Element root = getRoot(document);
		Element note = getNote(document);
		Element doc = getDocElem(document);
		document.appendChild(root);// 装配root节点
		root.appendChild(note);// 装配note节点
		root.appendChild(doc);// 装配doc节点
		int paraId = 0;
		while (paraIter.hasNext()) {
			// 构造Paragraph节点
			Element para = getParaElem(document);
			para.setAttribute(LTPTag.ID, String.valueOf(paraId));
			String p = paraIter.next();
			// 在LTP中……会被切分，而......不会被切分，所以将所有的会切分的省略号转成不会被切分的省略号
			p.replaceAll("(…{2})", "......");
			String[] sentences = MySplit.split(p, "。|？|！|；");// 切分句子
			for (int i = 0; i < sentences.length; i++) {
				getSentLTML(sentences[i], i, para, document);
				// 每个句子处理完之后，注意清空
				heads.clear();
				deprels.clear();
			}
			// 一个段落处理完毕，装配para节点
			doc.appendChild(para);
			// 将paraId递增
			paraId++;
		}

		return transformToString(document);
	}

	/**
	 * 
	 * <p>
	 * Title: transformToString
	 * </p>
	 * <p>
	 * Description: 将DOM对象转成String
	 * </p>
	 * 
	 * @param document
	 * @return
	 *
	 */
	private String transformToString(Document document) {
		StringWriter xmlResultResource = new StringWriter();
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(document), new StreamResult(xmlResultResource));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return xmlResultResource.getBuffer().toString();
	}

	/**
	 * 
	 * <p>
	 * Title: getParaElem
	 * </p>
	 * <p>
	 * Description: 获取Paragraph节点元素
	 * </p>
	 * 
	 * @param document
	 * @return
	 *
	 */
	private Element getParaElem(Document document) {
		Element para = document.createElement(LTPTag.PARA);
		return para;
	}

	/**
	 * 
	 * <p>
	 * Title: getDocElem
	 * </p>
	 * <p>
	 * Description: 获取Doc节点元素
	 * </p>
	 * 
	 * @param document
	 * @return
	 *
	 */
	private Element getDocElem(Document document) {
		Element doc = document.createElement(LTPTag.DOC);// 构造doc节点
		return doc;
	}

	/**
	 * 
	 * <p>
	 * Title: getRoot
	 * </p>
	 * <p>
	 * Description: 获取ROOT根节点，即<xml4nlp>
	 * </p>
	 * 
	 * @param document
	 * @return
	 *
	 */
	private Element getRoot(Document document) {
		Element root = document.createElement(LTPTag.ROOT);// 构造根节点
		return root;
	}

	/**
	 * 
	 * <p>
	 * Title: getNote
	 * </p>
	 * <p>
	 * Description: 实例化LTML的note节点
	 * </p>
	 * 
	 * @param document
	 * @return
	 *
	 */
	private Element getNote(Document document) {
		Element note = document.createElement(LTPTag.NOTE);// 构造note节点
		note.setAttribute(LTPTag.NOTE_WORD, "y");// 默认值
		note.setAttribute(LTPTag.NOTE_SENT, "y");// 默认值
		note.setAttribute(LTPTag.NOTE_POS, isPos() == true ? "y" : "n");// 根据用户传递
		note.setAttribute(LTPTag.NOTE_NE, isNe() == true ? "y" : "n");// 根据用户传递
		note.setAttribute(LTPTag.NOTE_PARSER, isParser() == true ? "y" : "n");
		note.setAttribute(LTPTag.NOTE_SRL, isSrl() == true ? "y" : "n");
		return note;
	}

	/**
	 * 
	 * <p>
	 * Title: getDocument
	 * </p>
	 * <p>
	 * Description: 获取DOM实例对象
	 * </p>
	 * 
	 * @return document
	 *
	 */
	private Document getDocument() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.err.println("Document构造失败！");
		}
		Document document = builder.newDocument();
		document.setXmlStandalone(true);// 设置XML的standalone属性为yes
		return document;
	}

	/**
	 * 
	 * <p>
	 * Title: getLTMLBySentence
	 * </p>
	 * <p>
	 * Description: TODO
	 * </p>
	 * 
	 * @param sentence
	 * @return
	 *
	 */
	public String getLTMLBySentence(String sentence) {
		Document document = getDocument();
		Element root = getRoot(document);
		Element note = getNote(document);
		Element docElem = getDocElem(document);
		Element paraElem = getParaElem(document);
		document.appendChild(root);
		root.appendChild(note);
		root.appendChild(docElem);
		paraElem.setAttribute(LTPTag.ID, String.valueOf(0));
		// 开始装配节点
		getSentLTML(sentence, 0, paraElem, document);
		docElem.appendChild(paraElem);
		return transformToString(document);
	}

	public boolean isPos() {
		return pos;
	}

	public void setPos(boolean pos) {
		this.pos = pos;
	}

	public boolean isNe() {
		return ne;
	}

	public void setNe(boolean ne) {
		this.ne = ne;
	}

	public boolean isParser() {
		return parser;
	}

	public void setParser(boolean parser) {
		this.parser = parser;
	}

	public boolean isSrl() {
		return srl;
	}

	public void setSrl(boolean srl) {
		this.srl = srl;
	}

	@Test
	public void testSrl() {
		String s = "事件语料的建立对事件及其关系识别和推理有重要意义，因此针对该语料库的建立进行研究有一定的理论意义和应用价值。";
		LTPUtil ltpUtil = new LTPUtil();
		Map<Integer, String> segment = ltpUtil.getSegment(s);
		Map<Integer, String> posTag = ltpUtil.getPosTag(segment);
		Map<Integer, String> ner = ltpUtil.getNer(segment, posTag);
		Map<Integer, String> parser2 = ltpUtil.getParser(segment, posTag);
		List<Pair<Integer, List<Pair<String, Pair<Integer, Integer>>>>> srls = ltpUtil.getSrls(segment, posTag, ner);
		System.out.println(srls.size());
		for (int i = 0; i < srls.size(); ++i) {
			System.out.println(srls.get(i).first + ":");
			for (int j = 0; j < srls.get(i).second.size(); ++j) {
				System.out.println("   tpye = " + srls.get(i).second.get(j).first + " beg = "
						+ srls.get(i).second.get(j).second.first + " end = " + srls.get(i).second.get(j).second.second);
			}
		}
	}
}
