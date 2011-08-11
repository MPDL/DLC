package de.mpg.mpdl.dlc.wf.testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import de.mpg.mpdl.dlc.tei.TEITransformer;

public class XMLParser {

	public static void main(String[] args) {

		long time = System.currentTimeMillis();
		XMLParser parser = new XMLParser();
		// String uri =
		// "http://latest-coreservice.mpdl.mpg.de:8080/ir/item/escidoc:1004/components/component/escidoc:3002/content";
		// File tei_sd = new
		// File("/home/frank/data/digitization_lifecycle/tei_bhr_khi/tei/struct.xml");
		// parser.extractStructure(uri, tei_sd);
		parser.transform();
		time = System.currentTimeMillis() - time;
		System.out.println("time to extract structure: " + time);
	}

	public void parse() throws XMLStreamException, IOException {
		String uri = "http://latest-coreservice.mpdl.mpg.de:8080/ir/item/escidoc:1004/components/component/escidoc:3002/content";
		File f = new File(
				"/home/frank/data/digitization_lifecycle/tei_samples/marx.xml");
		URL url = new URL(uri);
		InputStream stream = url.openStream();
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader reader = factory.createXMLEventReader(uri, stream);

		LinkedList<String> lili = new LinkedList<String>();

		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			XMLEvent nextEvent = reader.peek();

			if (event.isStartElement()) {
				if (event.asStartElement().getName().getLocalPart()
						.equalsIgnoreCase("pb")) {
					lili.add(event.asStartElement().getName().getLocalPart()
							+ "[" + event.getLocation().getCharacterOffset()
							+ "]");
				} else {
					lili.add(event.asStartElement().getName().getLocalPart());
				}
				Iterator<Attribute> atts = event.asStartElement()
						.getAttributes();
				while (atts.hasNext()) {
					Attribute att = atts.next();
					System.out.println(buildXPath(
							lili,
							"/@" + att.getName().getLocalPart() + "\""
									+ att.getValue() + "\""));
				}
				if (nextEvent.isCharacters()) {
					Characters chars = reader.nextEvent().asCharacters();
					if (!chars.isWhiteSpace()) {
						String data = chars.getData();
						System.out
								.println(buildXPath(lili, "\"" + data + "\""));
					}
				}
			}
			/*
			 * if (event.isCharacters() &&
			 * event.getLocation().getCharacterOffset() >= 161247 &&
			 * event.getLocation().getCharacterOffset() < 164509) { String txt =
			 * event.asCharacters().getData(); if (txt.trim().length() > 0) {
			 * System.out.println(buildXPath(lili, "\"" + txt + "\"")); } }
			 */
			if (event.isEndElement()) {
				if (lili.size() > 0) {
					lili.removeLast();
				}
			}
		}
		reader.close();
	}

	public String buildXPath(LinkedList<String> tags, String values) {
		StringBuffer sb = new StringBuffer();
		for (String tag : tags) {
			sb.append("/").append(tag);
		}
		sb.append(values);
		return sb.toString();
	}

	public LinkedList<String>[] getTagLists(String uri, int start, int end)
			throws Exception {
		URL url = null;
		if (uri.startsWith("http"))
		{
		try {
			url = new URL(uri);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		// InputStream in = url.openStream();
		}
		InputStream in = new FileInputStream(uri);
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader parser = factory.createXMLEventReader(in);

		StartElement startTag = null;
		EndElement endTag = null;

		LinkedList<String> starttagList = new LinkedList<String>();
		LinkedList<String> endtagList = new LinkedList<String>();

		while (parser.hasNext()) {
			XMLEvent event = parser.nextEvent();
			if (event.getLocation().getCharacterOffset() >= start
					&& event.getLocation().getCharacterOffset() < end) {
				switch (event.getEventType()) {
				case XMLStreamConstants.END_DOCUMENT:
					parser.close();
					break;
				case XMLStreamConstants.START_ELEMENT:
					startTag = event.asStartElement();
					starttagList.add(startTag.getName().getLocalPart());
					break;
				case XMLStreamConstants.END_ELEMENT:
					endTag = event.asEndElement();
					if (starttagList.size() > 0
							&& starttagList.getLast().equalsIgnoreCase(
									endTag.getName().getLocalPart())) {
						starttagList.removeLast();
					} else {
						endtagList.add(endTag.getName().getLocalPart());
					}
					break;
				default:
					break;
				}
			}
		}
		LinkedList[] result = new LinkedList[] { starttagList, endtagList };
		return result;
	}

	public LinkedHashMap<String, Integer> getPageBreakPositions(String uri)
			throws Exception {
		URL url = null;
		if (uri.startsWith("http")) {
			try {
				url = new URL(uri);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			// InputStream in = url.openStream();
		}
		InputStream in = new FileInputStream(uri);
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader parser = factory.createXMLEventReader(in);

		LinkedHashMap<String, Integer> pbPositions = new LinkedHashMap<String, Integer>();

		while (parser.hasNext()) {
			StringBuffer key = new StringBuffer();
			XMLEvent event = parser.nextEvent();
			switch (event.getEventType()) {
			case XMLStreamConstants.END_DOCUMENT:
				parser.close();
				break;
			case XMLStreamConstants.START_ELEMENT:
				if (event.asStartElement().getName().getLocalPart()
						.equalsIgnoreCase("pb")) {
					StartElement startTag = event.asStartElement();
					key.append("<" + startTag.getName().getLocalPart());
					Iterator<Attribute> atts = startTag.getAttributes();
					while (atts.hasNext()) {
						Attribute att = atts.next();
						key.append(" " + att.getName().getLocalPart() + "=\""
								+ att.getValue() + "\"");
					}
					key.append(">");
					pbPositions.put(key.toString(), Integer.valueOf(startTag
							.getLocation().getCharacterOffset()));
				}
				break;
			default:
				break;
			}
		}
		return pbPositions;
	}

	public File createPage(String uri, int start, int end,
			LinkedList<String> missingStartTags,
			LinkedList<String> missingEndTags, File out)
			throws XMLStreamException, IOException {
		// URL url = new URL(uri);
		// InputStream stream = url.openStream();
		InputStream stream = new FileInputStream(uri);
		XMLInputFactory factory = XMLInputFactory.newInstance();

		// XMLEventReader reader = factory.createXMLEventReader(uri, stream);
		XMLEventReader reader = factory.createXMLEventReader(stream);

		XMLOutputFactory of = XMLOutputFactory.newInstance();
		XMLEventWriter writer = of.createXMLEventWriter(new FileOutputStream(
				out));
		XMLEventFactory ef = XMLEventFactory.newInstance();
		StartDocument sd_tei = ef.createStartDocument("UTF-8", "1.0");
		StartElement se_tei = ef.createStartElement("",
				"http://www.tei-c.org/ns/1.0", "TEI");
		Namespace ns = ef.createNamespace("http://www.tei-c.org/ns/1.0");
		EndElement ee_tei = ef.createEndElement("", "", "TEI");
		writer.add(sd_tei);
		writer.add(se_tei);
		writer.add(ns);
		/*
		 * for (String startTag : missingStartTags) { StartElement startElemet =
		 * ef.createStartElement("", "", startTag); writer.add(startElemet); }
		 */
		Iterator<String> startTags = missingStartTags.descendingIterator();
		while (startTags.hasNext()) {
			StartElement startElemet = ef.createStartElement("", "",
					startTags.next());
			writer.add(startElemet);
		}

		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			XMLEvent nextEvent = reader.peek();

			if (event.getLocation().getCharacterOffset() >= start
					&& event.getLocation().getCharacterOffset() < end) {
				switch (event.getEventType()) {
				case XMLStreamConstants.END_DOCUMENT:
					reader.close();
					break;
				case XMLStreamConstants.START_ELEMENT:
					StartElement startTag = event.asStartElement();
					writer.add(startTag);
					break;
				case XMLStreamConstants.CHARACTERS:
					Characters chars = event.asCharacters();
					if (!chars.isWhiteSpace()) {
						writer.add(chars);
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					EndElement endTag = event.asEndElement();
					writer.add(endTag);
					break;
				default:
					break;
				}
			}
		}
		/*
		 * for (String endTag : missingEndTags) {
		 * System.out.println("tryin' 2 create endtag " + endTag); EndElement
		 * endElement = ef.createEndElement("", "", endTag);
		 * writer.add(endElement); }
		 */
		Iterator<String> endTags = missingEndTags.descendingIterator();
		while (endTags.hasNext()) {
			EndElement endElement = ef.createEndElement("", "", endTags.next());
			writer.add(endElement);
		}
		writer.add(ee_tei);
		writer.flush();
		writer.close();
		reader.close();
		return out;
	}

	public String getText(File in, File out) throws XMLStreamException,
			IOException {

		XMLInputFactory factory = XMLInputFactory.newInstance();

		XMLStreamReader reader = factory
				.createXMLStreamReader(new FileInputStream(in));
		XMLOutputFactory of = XMLOutputFactory.newInstance();
		StringBuffer buffer = new StringBuffer();

		while (reader.hasNext()) {
			int event = reader.next();

			switch (event) {
			case XMLStreamConstants.END_DOCUMENT:
				reader.close();
				break;
			case XMLStreamConstants.START_ELEMENT:
				if (reader.getName().getLocalPart().equalsIgnoreCase("lb")) {
					buffer.append("\n");
				}
				System.out.println("processing: " + reader.getName());
				if (reader.hasText()) {
					System.out.println("element text of " + reader.getName()
							+ ": " + reader.getElementText());
				}
				break;
			case XMLStreamConstants.CHARACTERS:
				if (!reader.isWhiteSpace()) {
					String data = reader.getText().replace("           ", "")
							.replace("\n", "");
					buffer.append(data);
				}
				break;
			default:
				break;
			}
		}
		reader.close();
		return buffer.toString();
	}

	public void extractStructure(String uri, File out) {
		URL url;
		String[] teiTags = new String[] { "text", "front", "body", "back",
				"titlePage", "pb", "div", "head", "figure" };
		try {
			url = new URL(uri);
			InputStream stream = url.openStream();
			XMLInputFactory factory = XMLInputFactory.newInstance();

			// XMLEventReader reader = factory.createXMLEventReader(uri,
			// stream);
			XMLEventReader reader = factory
					.createXMLEventReader(new FileReader(
							"/home/frank/data/digitization_lifecycle/tei_bhr_khi/tei/BHR_Dg450-990.tei"));
			XMLOutputFactory of = XMLOutputFactory.newInstance();
			XMLEventWriter writer = of
					.createXMLEventWriter(new FileOutputStream(out));
			XMLEventFactory ef = XMLEventFactory.newInstance();
			StartDocument sd_tei = ef.createStartDocument("UTF-8", "1.0");
			StartElement se_tei = ef.createStartElement("",
					"http://www.tei-c.org/ns/1.0", "TEI");
			Namespace ns = ef.createNamespace("http://www.tei-c.org/ns/1.0");
			EndElement ee_tei = ef.createEndElement("", "", "TEI");

			writer.add(sd_tei);
			writer.add(se_tei);
			writer.add(ns);

			while (reader.hasNext()) {
				XMLEvent event = reader.nextEvent();
				switch (event.getEventType()) {
				case XMLStreamConstants.END_DOCUMENT:
					reader.close();
					break;
				case XMLStreamConstants.START_ELEMENT:
					StartElement startTag = event.asStartElement();
					for (String tag : teiTags) {
						if (startTag.getName().getLocalPart()
								.equalsIgnoreCase(tag)) {
							if (startTag.getName().getLocalPart()
									.equalsIgnoreCase("head")) {
								XMLEvent next = reader.peek();
								if (next.isCharacters()) {
									Characters chars = next.asCharacters();
									writer.add(startTag);
									writer.add(chars);
								}
							} else {
								writer.add(startTag);
							}
						}
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					EndElement endTag = event.asEndElement();
					for (String tag : teiTags) {
						if (endTag.getName().getLocalPart()
								.equalsIgnoreCase(tag)) {
							writer.add(endTag);
						}
					}
					break;
				default:
					break;
				}
			}
			writer.add(ee_tei);
			writer.flush();
			writer.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	public void transform() {
		long time = System.currentTimeMillis();
		XMLParser parser = new XMLParser();
		//String uri =
		//"http://latest-coreservice.mpdl.mpg.de:8080/ir/item/escidoc:1004/components/component/escidoc:3002/content";
		//String uri = "/home/frank/data/digitization_lifecycle/tei_bhr_khi/tei/BHR_Dg450-990.tei";
		String uri = "/home/frank/data/digitization_lifecycle/tei_samples/marx.xml";

		File pagedTEI = new File(
				"/home/frank/data/digitization_lifecycle/tei_bhr_khi/tei/page.xml");
		File pagedTXT = new File(
				"/home/frank/data/digitization_lifecycle/tei_samples/page.txt");
		File marx_struct = new File(
				"/home/frank/data/digitization_lifecycle/tei_samples/marx_struct.xml");
		int start = 0;
		int end = 0;
		try {
			LinkedHashMap<String, Integer> pbs = parser
					.getPageBreakPositions(uri);
			Integer[] offsets = new Integer[pbs.values().size()];
			offsets = pbs.values().toArray(offsets);
			start = offsets[offsets.length - 7];
			end = offsets[offsets.length - 6];
			LinkedList[] taglists = parser.getTagLists(uri, start, end);
			File teiPage = parser.createPage(uri, start, end, taglists[1],
					taglists[0], pagedTEI);
			// String txt = parser.getText(teiPage, pagedTXT);
			// System.out.println(txt);
			// TEIValidator.check(teiPage);
			InputStream xslt = XMLParser.class.getClassLoader().getResourceAsStream("xslt/teiToXhtml/tei_page2xhtml.xsl");
			String result = TEITransformer.teiFileToXhtml(teiPage, xslt);
			time = System.currentTimeMillis() - time;
			System.out
					.println("total time to build and transform TEI page: "
							+ time + " ms");
			System.out.println(result);

		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * for (LinkedList list : taglists) { Iterator<String> li =
	 * list.descendingIterator(); while (li.hasNext()) {
	 * System.out.println(li.next()); } }
	 * 
	 * LinkedHashMap<String, Integer> pbs = parser .getPageBreakPositions(uri);
	 * 
	 * for (Entry<String, Integer> e : pbs.entrySet()) {
	 * System.out.println(e.getValue() + "   " + e.getKey()); }
	 * 
	 * Collection<Integer> posList = pbs.values(); Iterator<Integer> li =
	 * posList.iterator(); while (li.hasNext()) { System.out.println(li.next());
	 * }
	 * 
	 * if (event.isStartElement() && event.getLocation().getCharacterOffset() >=
	 * start && event.getLocation().getCharacterOffset() < end) {
	 * 
	 * StartElement startElement = event.asStartElement();
	 * writer.add(startElement); /*
	 * 
	 * @SuppressWarnings("unchecked") Iterator<Attribute> atts =
	 * startElement.getAttributes(); while (atts.hasNext()) { Attribute att =
	 * atts.next(); buffer.append(" " + att.getName().getLocalPart() + "=\"" +
	 * att.getValue() + "\""); writer.add(att); }
	 */
	/*
	 * } if (event.isCharacters() && event.getLocation().getCharacterOffset() >=
	 * start && event.getLocation().getCharacterOffset() < end) {
	 * 
	 * Characters chars = event.asCharacters(); if (!chars.isWhiteSpace()) {
	 * writer.add(chars); } } if (event.isEndElement() &&
	 * event.getLocation().getCharacterOffset() >= start &&
	 * event.getLocation().getCharacterOffset() < end) {
	 * 
	 * EndElement endElement = event.asEndElement(); writer.add(endElement); }
	 */
}
