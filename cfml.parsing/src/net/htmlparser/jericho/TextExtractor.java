// Jericho HTML Parser - Java based library for analysing and manipulating HTML
// Version 3.1
// Copyright (C) 2004-2009 Martin Jericho
// http://jericho.htmlparser.net/
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of either one of the following licences:
//
// 1. The Eclipse Public License (EPL) version 1.0,
// included in this distribution in the file licence-epl-1.0.html
// or available at http://www.eclipse.org/legal/epl-v10.html
//
// 2. The GNU Lesser General Public License (LGPL) version 2.1 or later,
// included in this distribution in the file licence-lgpl-2.1.txt
// or available at http://www.gnu.org/licenses/lgpl.txt
//
// This library is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the individual licence texts for more details.

package net.htmlparser.jericho;

import java.util.*;
import java.io.*;
import java.net.*;

/**
 * Extracts the textual content from HTML markup.
 * <p>
 * The output is ideal for feeding into a text search engine such as <a target="_blank" href="http://lucene.apache.org/java/">Apache Lucene</a>,
 * especially when the {@link #setIncludeAttributes(boolean) IncludeAttributes} property has been set to <code>true</code>.
 * <p>
 * Use one of the following methods to obtain the output:
 * <ul style="margin-top: 0">
 *  <li>{@link #writeTo(Writer)}</li>
 *  <li>{@link #appendTo(Appendable)}</li>
 *  <li>{@link #toString()}</li>
 *  <li>{@link CharStreamSourceUtil#getReader(CharStreamSource) CharStreamSourceUtil.getReader(this)}</li>
 * </ul>
 * <p>
 * The process removes all of the tags and
 * {@linkplain CharacterReference#decodeCollapseWhiteSpace(CharSequence) decodes the result, collapsing all white space}.
 * A space character is included in the output where a <a href="TagType.html#Normal">normal</a> tag is present in the source,
 * unless the tag belongs to an {@linkplain HTMLElements#getInlineLevelElementNames() inline-level} element.
 * An exception to this is the {@link HTMLElementName#BR BR} element, which is also converted to a space despite being an inline-level element.
 * <p>
 * Text inside {@link HTMLElementName#SCRIPT SCRIPT} and {@link HTMLElementName#STYLE STYLE} elements contained within this segment
 * is ignored.
 * <p>
 * Setting the {@link #setExcludeNonHTMLElements(boolean) ExcludeNonHTMLElements} property results in the exclusion of any content within a
 * <a href="HTMLElements.html#NonHTMLElement">non-HTML element</a>.
 * <p>
 * See the {@link #excludeElement(StartTag)} method for details on how to implement a more complex mechanism to determine whether the
 * {@linkplain Element#getContent() content} of each {@link Element} is to be excluded from the output.
 * <p>
 * All tags that are not <a href="TagType.html#Normal">normal</a> tags, such as {@linkplain TagType#isServerTag() server tags},
 * {@linkplain StartTagType#COMMENT comments} etc., are removed from the output without adding white space to the output.
 * <p>
 * Note that segments on which the {@link Segment#ignoreWhenParsing()} method has been called are treated as text rather than markup,
 * resulting in their inclusion in the output.
 * To remove specific segments before extracting the text, create an {@link OutputDocument} and call its {@link OutputDocument#remove(Segment) remove(Segment)} or
 * {@link OutputDocument#replaceWithSpaces(int,int) replaceWithSpaces(int begin, int end)} method for each segment to be removed.
 * Then create a new source document using {@link Source#Source(CharSequence) new Source(outputDocument.toString())}
 * and perform the text extraction on this new source object.
 * <p>
 * Extracting the text from an entire {@link Source} object performs a {@linkplain Source#fullSequentialParse() full sequential parse} automatically.
 * <p>
 * To perform a simple rendering of HTML markup into text, which is more readable than the output of this class, use the {@link Renderer} class instead.
 * <dl>
 *  <dt>Example:</dt>
 *  <dd>Using the default settings, the source segment:<br />
 *   "<code>&lt;div&gt;&lt;b&gt;O&lt;/b&gt;ne&lt;/div&gt;&lt;div title="Two"&gt;&lt;b&gt;Th&lt;/b&gt;&lt;script&gt;//a&nbsp;script&nbsp;&lt;/script&gt;ree&lt;/div&gt;</code>"<br />
 *   produces the text "<code>One Two Three</code>".
 * </dl>
 */
public class TextExtractor implements CharStreamSource {
	private final Segment segment;
	private boolean convertNonBreakingSpaces=Config.ConvertNonBreakingSpaces;
	private boolean includeAttributes=false;
	private boolean excludeNonHTMLElements=false;

	private static Map<String,AttributeIncludeChecker> map=initDefaultAttributeIncludeCheckerMap(); // maps each possibly included attribute name to an AttributeIncludeChecker instance

	/**
	 * Constructs a new <code>TextExtractor</code> based on the specified {@link Segment}.
	 * @param segment  the segment from which the text will be extracted.
	 * @see Segment#getTextExtractor()
	 */
	public TextExtractor(final Segment segment) {
		this.segment=segment;
	}

	// Documentation inherited from CharStreamSource
	public void writeTo(final Writer writer) throws IOException {
		appendTo(writer);
		writer.flush();
	}

	// Documentation inherited from CharStreamSource
	public void appendTo(final Appendable appendable) throws IOException {
		appendable.append(toString());
	}

	// Documentation inherited from CharStreamSource
	public long getEstimatedMaximumOutputLength() {
		return segment.length();
	}

	// Documentation inherited from CharStreamSource
	public String toString() {
		return new Processor(segment,getConvertNonBreakingSpaces(),getIncludeAttributes(),getExcludeNonHTMLElements()).toString();
	}

	/**
	 * Sets whether non-breaking space ({@link CharacterEntityReference#_nbsp &amp;nbsp;}) character entity references are converted to spaces.
	 * <p>
	 * The default value is that of the static {@link Config#ConvertNonBreakingSpaces} property at the time the <code>TextExtractor</code> is instantiated.
	 *
	 * @param convertNonBreakingSpaces  specifies whether non-breaking space ({@link CharacterEntityReference#_nbsp &amp;nbsp;}) character entity references are converted to spaces.
	 * @return this <code>TextExtractor</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getConvertNonBreakingSpaces()
	 */
	public TextExtractor setConvertNonBreakingSpaces(boolean convertNonBreakingSpaces) {
		this.convertNonBreakingSpaces=convertNonBreakingSpaces;
		return this;
	}

	/**
	 * Indicates whether non-breaking space ({@link CharacterEntityReference#_nbsp &amp;nbsp;}) character entity references are converted to spaces.
	 * <p>
	 * See the {@link #setConvertNonBreakingSpaces(boolean)} method for a full description of this property.
	 * 
	 * @return <code>true</code> if non-breaking space ({@link CharacterEntityReference#_nbsp &amp;nbsp;}) character entity references are converted to spaces, otherwise <code>false</code>.
	 */
	public boolean getConvertNonBreakingSpaces() {
		return convertNonBreakingSpaces;
	}

	/**
	 * Sets whether any attribute values are included in the output.
	 * <p>
	 * If the value of this property is <code>true</code>, then each attribute still has to match the conditions implemented in the
	 * {@link #includeAttribute(StartTag,Attribute)} method in order for its value to be included in the output.
	 * <p>
	 * The default value is <code>false</code>.
	 *
	 * @param includeAttributes  specifies whether any attribute values are included in the output.
	 * @return this <code>TextExtractor</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getIncludeAttributes()
	 */
	public TextExtractor setIncludeAttributes(boolean includeAttributes) {
		this.includeAttributes=includeAttributes;
		return this;
	}
	
	/**
	 * Indicates whether any attribute values are included in the output.
	 * <p>
	 * See the {@link #setIncludeAttributes(boolean)} method for a full description of this property.
	 * 
	 * @return <code>true</code> if any attribute values are included in the output, otherwise <code>false</code>.
	 */
	public boolean getIncludeAttributes() {
		return includeAttributes;
	}

	/**
	 * Indicates whether the value of the specified {@linkplain Attribute attribute} in the specified {@linkplain StartTag start tag} is included in the output.
	 * <p>
	 * This method is ignored if the {@link #setIncludeAttributes(boolean) IncludeAttributes} property is set to <code>false</code>, in which case
	 * no attribute values are included in the output.
	 * <p>
	 * If the {@link #setIncludeAttributes(boolean) IncludeAttributes} property is set to <code>true</code>, every attribute of every
	 * start tag encountered in the segment is checked using this method to determine whether the value of the attribute should be included in the output.
	 * <p>
	 * The default implementation of this method returns <code>true</code> if the {@linkplain Attribute#getName() name} of the specified {@linkplain Attribute attribute}
	 * is one of
	 * <a target="_blank" href="http://www.w3.org/TR/html401/struct/global.html#adef-title">title</a>,
	 * <a target="_blank" href="http://www.w3.org/TR/html401/struct/objects.html#adef-alt">alt</a>,
	 * <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-label-OPTION">label</a>,
	 * <a target="_blank" href="http://www.w3.org/TR/html401/struct/tables.html#adef-summary">summary</a>,
	 * <a target="_blank" href="http://www.w3.org/TR/html401/struct/global.html#adef-content">content</a>*, or
	 * <a target="_blank" href="http://www.w3.org/TR/html401/struct/links.html#adef-href">href</a>,
	 * but the method can be overridden in a subclass to perform a check of arbitrary complexity on each attribute.
	 * <p>
	 * * The value of a <a target="_blank" href="http://www.w3.org/TR/html401/struct/global.html#adef-content">content</a> attribute is only included if a 
	 * <a target="_blank" href="http://www.w3.org/TR/html401/struct/global.html#adef-name-META">name</a> attribute is also present in the specified start tag,
	 * as the content attribute of a {@link HTMLElementName#META META} tag only contains human readable text if the name attribute is used as opposed to an
	 * <a target="_blank" href="http://www.w3.org/TR/html401/struct/global.html#adef-http-equiv">http-equiv</a> attribute.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd>
	 *   To include only the value of <a target="_blank" href="http://www.w3.org/TR/html401/struct/global.html#adef-title">title</a> and
	 *   <a target="_blank" href="http://www.w3.org/TR/html401/struct/objects.html#adef-alt">alt</a> attributes:<br /><br />
	 *   <code>
	 *    final Set includeAttributeNames=new HashSet(Arrays.asList(new String[] {"title","alt"}));<br />
	 *    TextExtractor textExtractor=new TextExtractor(segment) {<br />
	 *    &nbsp; &nbsp; public boolean includeAttribute(StartTag startTag, Attribute attribute) {<br />
	 *    &nbsp; &nbsp; &nbsp; &nbsp; return includeAttributeNames.contains(attribute.getKey());<br />
	 *    &nbsp; &nbsp; }<br />
	 *    };<br />
	 *    textExtractor.setIncludeAttributes(true);<br />
	 *    String extractedText=textExtractor.toString();
	 *   </code>
	 *  </dd>
	 * </dl>
	 * @param startTag  the start tag of the element to check for inclusion.
	 * @return <true> if the text inside the {@link Element} of the specified start tag should be excluded from the output, otherwise <code>false</code>.
	 */
	public boolean includeAttribute(final StartTag startTag, final Attribute attribute) {
		AttributeIncludeChecker attributeIncludeChecker=map.get(attribute.getKey());
		if (attributeIncludeChecker==null) return false;
		return attributeIncludeChecker.includeAttribute(startTag,attribute);
	}

	/**
	 * Sets whether the content of <a href="HTMLElements.html#NonHTMLElement">non-HTML elements</a> is excluded from the output.
	 * <p>
	 * The default value is <code>false</code>, meaning that content from all elements meeting the other criteria is included.
	 *
	 * @param excludeNonHTMLElements  specifies whether content <a href="HTMLElements.html#NonHTMLElement">non-HTML elements</a> is excluded from the output.
	 * @return this <code>TextExtractor</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getExcludeNonHTMLElements()
	 */
	public TextExtractor setExcludeNonHTMLElements(boolean excludeNonHTMLElements) {
		this.excludeNonHTMLElements=excludeNonHTMLElements;
		return this;
	}
	
	/**
	 * Indicates whether the content of <a href="HTMLElements.html#NonHTMLElement">non-HTML elements</a> is excluded from the output.
	 * <p>
	 * See the {@link #setExcludeNonHTMLElements(boolean)} method for a full description of this property.
	 * 
	 * @return <code>true</code> if the content of <a href="HTMLElements.html#NonHTMLElement">non-HTML elements</a> is excluded from the output, otherwise <code>false</code>.
	 */
	public boolean getExcludeNonHTMLElements() {
		return excludeNonHTMLElements;
	}

	/**
	 * Indicates whether the text inside the {@link Element} of the specified start tag should be excluded from the output.
	 * <p>
	 * During the text extraction process, every start tag encountered in the segment is checked using this method to determine whether the text inside its
	 * {@linkplain StartTag#getElement() associated element} should be excluded from the output.
	 * <p>
	 * The default implementation of this method is to always return <code>false</code>, so that every element is included,
	 * but the method can be overridden in a subclass to perform a check of arbitrary complexity on each start tag.
	 * <p>
	 * All elements nested inside an excluded element are also implicitly excluded, as are all
	 * {@link HTMLElementName#SCRIPT SCRIPT} and {@link HTMLElementName#STYLE STYLE} elements.
	 * Such elements are skipped over without calling this method, so there is no way to include them by overriding the method.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd>
	 *   To extract the text from a <code>segment</code>, excluding any text inside elements with the attribute <code>class="NotIndexed"</code>:<br /><br />
	 *   <code>
	 *    TextExtractor textExtractor=new TextExtractor(segment) {<br />
	 *    &nbsp; &nbsp; public boolean excludeElement(StartTag startTag) {<br />
	 *    &nbsp; &nbsp; &nbsp; &nbsp; return "NotIndexed".equalsIgnoreCase(startTag.getAttributeValue("class"));<br />
	 *    &nbsp; &nbsp; }<br />
	 *    };<br />
	 *    String extractedText=textExtractor.toString();
	 *   </code>
	 *  </dd>
	 * </dl>
	 * @param startTag  the start tag of the element to check for inclusion.
	 * @return <true> if the text inside the {@link Element} of the specified start tag should be excluded from the output, otherwise <code>false</code>.
	 */
	public boolean excludeElement(final StartTag startTag) {
		return false;
	}

	private static interface AttributeIncludeChecker {
		boolean includeAttribute(final StartTag startTag, final Attribute attribute);
	}

	private static AttributeIncludeChecker ALWAYS_INCLUDE=new AttributeIncludeChecker() {
		public boolean includeAttribute(final StartTag startTag, final Attribute attribute) {
			return true;
		}
	};

	private static AttributeIncludeChecker INCLUDE_IF_NAME_ATTRIBUTE_PRESENT=new AttributeIncludeChecker() {
		public boolean includeAttribute(final StartTag startTag, final Attribute attribute) {
			return startTag.getAttributes().get("name")!=null;
		}
	};

	private static Map<String,AttributeIncludeChecker> initDefaultAttributeIncludeCheckerMap() {
		Map<String,AttributeIncludeChecker> map=new HashMap<String,AttributeIncludeChecker>();
		map.put("title",ALWAYS_INCLUDE); // add title attribute
		map.put("alt",ALWAYS_INCLUDE); // add alt attribute (APPLET, AREA, IMG and INPUT elements)
		map.put("label",ALWAYS_INCLUDE); // add label attribute (OPTION and OPTGROUP elements)
		map.put("summary",ALWAYS_INCLUDE); // add summary attribute (TABLE element)
		map.put("content",INCLUDE_IF_NAME_ATTRIBUTE_PRESENT); // add content attribute (META element)
		map.put("href",ALWAYS_INCLUDE); // add href attribute (A, AREA and LINK elements)
		// don't bother with the prompt attribute from the ININDEX element as the element is deprecated and very rarely used.
		return map;
	}

	/**
	 * This class does the actual work, but is first passed final copies of all the parameters for efficiency.
	 * Note at present this is not implemented in a memory-efficient manner.
	 * Once the CharacterReference.decodeCollapseWhiteSpace functionality is available as a FilterWriter (possible with java 5 support),
	 * the main algorithm can be implemented in the writeTo(Writer) method to allow for more memory-efficient processing.
	 */
	private final class Processor {
		private final Segment segment;
		private final Source source;
		private final boolean convertNonBreakingSpaces;
		private final boolean includeAttributes;
		private final boolean excludeNonHTMLElements;

		public Processor(final Segment segment, final boolean convertNonBreakingSpaces, final boolean includeAttributes, final boolean excludeNonHTMLElements) {
			this.segment=segment;
			source=segment.source;
			this.convertNonBreakingSpaces=convertNonBreakingSpaces;
			this.includeAttributes=includeAttributes;
			this.excludeNonHTMLElements=excludeNonHTMLElements;
		}

		public String toString() {
			final StringBuilder sb=new StringBuilder(segment.length());
			for (NodeIterator nodeIterator=new NodeIterator(segment); nodeIterator.hasNext();) {
				Segment segment=nodeIterator.next();
				if (segment instanceof Tag) {
					final Tag tag=(Tag)segment;
					if (tag.getTagType().isServerTag()) {
						// elementContainsMarkup should be made into a TagType property one day.
						// for the time being assume all server element content is code, although this is not true for some Mason elements.
						final boolean elementContainsMarkup=false;
						if (!elementContainsMarkup) {
							final Element element=tag.getElement();
							if (element!=null && element.getEnd()>tag.getEnd()) nodeIterator.skipToPos(element.getEnd());
						}
						continue;
					}
					if (tag.getTagType()==StartTagType.NORMAL) {
						final StartTag startTag=(StartTag)tag;
						if (tag.name==HTMLElementName.SCRIPT || tag.name==HTMLElementName.STYLE || excludeElement(startTag) || (excludeNonHTMLElements && !HTMLElements.getElementNames().contains(tag.name))) {
							nodeIterator.skipToPos(startTag.getElement().getEnd());
							continue;
						}
						if (includeAttributes) {
							for (Attribute attribute : startTag.getAttributes()) {
								if (includeAttribute(startTag,attribute)) sb.append(' ').append(attribute.getValueSegment()).append(' ');
							}
						}
					}
					// Treat both start and end tags not belonging to inline-level elements as whitespace:
					if (tag.getName()==HTMLElementName.BR || !HTMLElements.getInlineLevelElementNames().contains(tag.getName())) sb.append(' ');
				} else {
					sb.append(segment);
				}
			}
			final String decodedText=CharacterReference.decodeCollapseWhiteSpace(sb,convertNonBreakingSpaces);
			return decodedText;
		}
	}
}
