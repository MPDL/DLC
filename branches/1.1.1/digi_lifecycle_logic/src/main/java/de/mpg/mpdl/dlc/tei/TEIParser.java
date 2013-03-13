/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 ******************************************************************************/
package de.mpg.mpdl.dlc.tei;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.Map.Entry;

import javax.xml.namespace.QName;
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

public class TEIParser {

	static XMLInputFactory inputFactory = null;
	static XMLOutputFactory outputFactory = null;
	static XMLEventReader parser = null;
	static XMLEventWriter writer = null;
	static XMLEventFactory eventFactory = null;

	public static LinkedList<LinkedList<String>> getTagLists(String uri,
			File file, int start, int end) {
		URL url = null;
		InputStream in = null;
		if (uri != null) {
			try {
				url = new URL(uri);
				in = url.openStream();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (file != null) {
				try {
					in = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		inputFactory = XMLInputFactory.newInstance();

		try {
			parser = inputFactory.createXMLEventReader(in);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

		StartElement startTag = null;
		EndElement endTag = null;

		LinkedList<String> starttagList = new LinkedList<String>();
		LinkedList<String> endtagList = new LinkedList<String>();

		while (parser.hasNext()) {
			XMLEvent event;
			try {
				event = parser.nextEvent();
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
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
		LinkedList<LinkedList<String>> tagLists = new LinkedList<LinkedList<String>>();
		tagLists.add(starttagList);
		tagLists.add(endtagList);
		return tagLists;
	}

	public static LinkedHashMap<String, Integer> getPageBreakPositions(
			String uri, File file) {
		URL url = null;
		InputStream in = null;
		if (uri != null) {
			try {
				url = new URL(uri);
				in = url.openStream();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				in = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		inputFactory = XMLInputFactory.newInstance();
		try {
			parser = inputFactory.createXMLEventReader(in);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

		LinkedHashMap<String, Integer> pbPositions = new LinkedHashMap<String, Integer>();

		while (parser.hasNext()) {
			StringBuffer key = new StringBuffer();
			XMLEvent event;
			try {
				event = parser.nextEvent();
				switch (event.getEventType()) {
				case XMLStreamConstants.END_DOCUMENT:
					parser.close();
					break;
				case XMLStreamConstants.START_ELEMENT:
					if (event.asStartElement().getName().getLocalPart()
							.equalsIgnoreCase("pb")) {
						StartElement startTag = event.asStartElement();
						// key.append("<" + startTag.getName().getLocalPart());
						// Iterator<Attribute> atts = startTag..getAttributes();
						// while (atts.hasNext()) {
						// Attribute att = atts.next();
						// key.append(" " + att.getName().getLocalPart()
						// + "=\"" + att.getValue() + "\"");
						// }
						// key.append(">");
						Attribute pbId = startTag.getAttributeByName(new QName(
								"http://www.w3.org/XML/1998/namespace", "id"));
						key.append(pbId.getValue());
						pbPositions.put(key.toString(), Integer
								.valueOf(startTag.getLocation()
										.getCharacterOffset()));
					}
					break;
				default:
					break;
				}
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
		return pbPositions;
	}

	public static File createPage(String uri, File file, int start, int end,
			LinkedList<String> missingStartTags,
			LinkedList<String> missingEndTags, File out) {
		URL url = null;
		InputStream in = null;

		if (uri != null) {
			try {
				url = new URL(uri);
				in = url.openStream();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (file != null) {
				try {
					in = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		inputFactory = XMLInputFactory.newInstance();
		try {
			parser = inputFactory.createXMLEventReader(in);
			outputFactory = XMLOutputFactory.newInstance();
			writer = outputFactory.createXMLEventWriter(new FileOutputStream(
					out));
			eventFactory = XMLEventFactory.newInstance();
			StartDocument sd_tei = eventFactory.createStartDocument("UTF-8",
					"1.0");
			StartElement se_tei = eventFactory.createStartElement("",
					"http://www.tei-c.org/ns/1.0", "TEI");
			Namespace ns = eventFactory
					.createNamespace("http://www.tei-c.org/ns/1.0");
			EndElement ee_tei = eventFactory.createEndElement("", "", "TEI");
			writer.add(sd_tei);
			writer.add(se_tei);
			writer.add(ns);
			/*
			 * for (String startTag : missingStartTags) { StartElement
			 * startElemet = ef.createStartElement("", "", startTag);
			 * writer.add(startElemet); }
			 */
			Iterator<String> startTags = missingStartTags.descendingIterator();
			while (startTags.hasNext()) {
				StartElement startElemet = eventFactory.createStartElement("",
						"", startTags.next());
				writer.add(startElemet);
			}

			while (parser.hasNext()) {
				XMLEvent event = parser.nextEvent();

				if (event.getLocation().getCharacterOffset() >= start
						&& event.getLocation().getCharacterOffset() < end) {
					switch (event.getEventType()) {
					case XMLStreamConstants.END_DOCUMENT:
						parser.close();
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

			Iterator<String> endTags = missingEndTags.descendingIterator();
			while (endTags.hasNext()) {
				EndElement endElement = eventFactory.createEndElement("", "",
						endTags.next());
				writer.add(endElement);
			}
			writer.add(ee_tei);
			writer.flush();
			writer.close();
			parser.close();
			return out;
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void extractStructure(String uri, File teiFile, File out) {
		URL url = null;
		InputStream in = null;
		String[] teiTags = new String[] { "text", "front", "body", "back",
				"titlePage", "pb", "div", "head", "figure" };
		try {
			if (uri != null) {
				url = new URL(uri);
				in = url.openStream();
			} else {
				if (teiFile != null) {
					in = new FileInputStream(teiFile);
				}
			}

			inputFactory = XMLInputFactory.newInstance();
			parser = inputFactory.createXMLEventReader(in);
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

			while (parser.hasNext()) {
				XMLEvent event = parser.nextEvent();
				switch (event.getEventType()) {
				case XMLStreamConstants.END_DOCUMENT:
					parser.close();
					break;
				case XMLStreamConstants.START_ELEMENT:
					StartElement startTag = event.asStartElement();
					for (String tag : teiTags) {
						if (startTag.getName().getLocalPart()
								.equalsIgnoreCase(tag)) {
							if (startTag.getName().getLocalPart()
									.equalsIgnoreCase("head")) {
								XMLEvent next = parser.peek();
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

	public static void transform(String uri, File file, String pbId) {
		int start = 0;
		int end = 0;
		LinkedHashMap<String, Integer> pbs = null;
		LinkedList<LinkedList<String>> taglists = null;
		File teiPage = null;

		long time = System.currentTimeMillis();

		try {
			if (uri != null) {
				pbs = getPageBreakPositions(uri, null);
				start = pbs.get(pbId);
				end = pbs.get(nextPagebreakPosition(pbs, pbId));
				taglists = getTagLists(uri, null, start, end);
				teiPage = createPage(uri, null, start, end, taglists.getLast(),
						taglists.getFirst(),
						File.createTempFile("paged", "tei"));
			} else {
				if (file != null) {
					pbs = getPageBreakPositions(null, file);
					start = pbs.get(pbId);
					end = pbs.get(nextPagebreakPosition(pbs, pbId));
					taglists = getTagLists(null, file, start, end);
					teiPage = createPage(null, file, start, end,
							taglists.getLast(), taglists.getFirst(),
							File.createTempFile("paged", "tei"));
				}
			}

			InputStream xslt = TEIParser.class.getClassLoader()
					.getResourceAsStream("xslt/teiToXhtml/tei_page2xhtml.xsl");

			URL xsltUrl = TEIParser.class.getClassLoader().getResource(
					"xslt/tei/xhtml2/tei.xsl");

			String systemId = xsltUrl.toExternalForm();

			String result = TEITransformer.teiFileToXhtml(teiPage, xslt);

			time = System.currentTimeMillis() - time;
			System.out
					.println("total time to build and transform XML for page: "
							+ time + " ms");
			System.out.println(result);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void transformByPosition(String uri, File file,
			int startPosition) {
		int start = 0;
		int end = 0;
		LinkedHashMap<String, Integer> pbs = null;
		LinkedList<LinkedList<String>> taglists = null;
		File teiPage = null;

		long time = System.currentTimeMillis();

		try {
			if (uri != null) {
				pbs = getPageBreakPositions(uri, null);
				Integer[] offsets = new Integer[pbs.values().size()];
				offsets = pbs.values().toArray(offsets);
				start = offsets[startPosition];
				end = offsets[startPosition + 1];
				taglists = getTagLists(uri, null, start, end);
				teiPage = createPage(uri, null, start, end, taglists.getLast(),
						taglists.getFirst(),
						File.createTempFile("paged", "tei"));
			} else {
				if (file != null) {
					pbs = getPageBreakPositions(null, file);
					Integer[] offsets = new Integer[pbs.values().size()];
					offsets = pbs.values().toArray(offsets);
					start = offsets[startPosition];
					end = offsets[startPosition + 1];
					taglists = getTagLists(null, file, start, end);
					teiPage = createPage(null, file, start, end,
							taglists.getLast(), taglists.getFirst(),
							File.createTempFile("paged", "tei"));
				}
			}

			InputStream xslt = TEIParser.class.getClassLoader()
					.getResourceAsStream("xslt/teiToXhtml/tei_page2xhtml.xsl");

			URL xsltUrl = TEIParser.class.getClassLoader().getResource(
					"xslt/tei/xhtml2/tei.xsl");

			String systemId = xsltUrl.toExternalForm();

			String result = TEITransformer.teiFileToXhtml(teiPage, xslt);

			time = System.currentTimeMillis() - time;
			System.out
					.println("total time to build and transform XML for page: "
							+ time + " ms");
			System.out.println(result);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String nextPagebreakPosition(
			LinkedHashMap<String, Integer> positions, String startId) {
		Iterator<Entry<String, Integer>> iter = positions.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Integer> e = (Entry<String, Integer>) iter.next();
			if (e.getKey().equalsIgnoreCase(startId)) {
				Entry<String, Integer> next = (Entry<String, Integer>) iter
						.next();
				return next.getKey();
			}
		}
		return null;
	}
}
