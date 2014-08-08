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

import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Represents a segment of a {@link Source} document.
 * <p>
 * Many of the <a href="Tag.html#TagSearchMethods">tag search methods</a> are defined in this class.
 * <p>
 * The <i>span</i> of a segment is defined by the combination of its begin and end character positions.
 */
public class Segment implements Comparable<Segment>, CharSequence {
	final int begin;
	final int end;
	final Source source;
	
	private static final char[] WHITESPACE={' ','\n','\r','\t','\f','\u200B'}; // see comments in isWhiteSpace(char) method

	/**
	 * Constructs a new <code>Segment</code> within the specified {@linkplain Source source} document with the specified begin and end character positions.
	 * @param source  the {@link Source} document, must not be <code>null</code>.
	 * @param begin  the character position in the source where this segment {@linkplain #getBegin() begins}, inclusive.
	 * @param end  the character position in the source where this segment {@linkplain #getEnd() ends}, exclusive.
	 */
	public Segment(final Source source, final int begin, final int end) {
		if (begin==-1 || end==-1 || begin>end) throw new IllegalArgumentException();
		this.begin=begin;
		this.end=end;
		if (source==null) throw new IllegalArgumentException("source argument must not be null");
		this.source=source;
	}

	// Only called from Source constructor
	Segment(final int length) {
		begin=0;
		this.end=length;
		source=(Source)this;
	}

	// Only used for creating dummy flag instances of this type (see Tag.NOT_CACHED and Element.NOT_CACHED)
	Segment() {
		this(0,0);
	}

	// Only used for creating dummy flag instances of this type (see Segment() constructor and StreamedSource.START_SEGMENT)
	Segment(final int begin, final int end) {
		this.begin=begin;
		this.end=end;
		source=null;
	}

	/**
	 * Returns the {@link Source} document containing this segment.
	 * <p>
 	 * If a {@link StreamedSource} is in use, this method throws an <code>UnsupportedOperationException</code>.
	 *
	 * @return the {@link Source} document containing this segment.
	 */
	public final Source getSource() {
		if (source.isStreamed()) throw new UnsupportedOperationException("Source object is not available when using StreamedSource");
		return source;
	}

	/**
	 * Returns the character position in the {@link Source} document at which this segment begins, inclusive.
	 * @return the character position in the {@link Source} document at which this segment begins, inclusive.
	 */
	public final int getBegin() {
		return begin;
	}

	/**
	 * Returns the character position in the {@link Source} document immediately after the end of this segment.
	 * <p>
	 * The character at the position specified by this property is <b>not</b> included in the segment.
	 *
	 * @return the character position in the {@link Source} document immediately after the end of this segment.
	 */
	public final int getEnd() {
		return end;
	}

	/**
	 * Compares the specified object with this <code>Segment</code> for equality.
	 * <p>
	 * Returns <code>true</code> if and only if the specified object is also a <code>Segment</code>,
	 * and both segments have the same {@link Source}, and the same begin and end positions.
	 * @param object  the object to be compared for equality with this <code>Segment</code>.
	 * @return <code>true</code> if the specified object is equal to this <code>Segment</code>, otherwise <code>false</code>.
	 */
	public final boolean equals(final Object object) {
		if (this==object) return true;
		if (object==null || !(object instanceof Segment)) return false;
		final Segment segment=(Segment)object;
		return segment.begin==begin && segment.end==end && segment.source==source;
	}

	/**
	 * Returns a hash code value for the segment.
	 * <p>
	 * The current implementation returns the sum of the begin and end positions, although this is not
	 * guaranteed in future versions.
	 *
	 * @return a hash code value for the segment.
	 */
	public int hashCode() {
		return begin+end;
	}

	/**
	 * Returns the length of the segment.
	 * This is defined as the number of characters between the begin and end positions.
	 * @return the length of the segment.
	 */
	public int length() {
		return end-begin;
	}

	/**
	 * Indicates whether this <code>Segment</code> encloses the specified <code>Segment</code>.
	 * <p>
	 * This is the case if {@link #getBegin()}<code>&lt;=segment.</code>{@link #getBegin()}<code> &amp;&amp; </code>{@link #getEnd()}<code>&gt;=segment.</code>{@link #getEnd()}.
	 * <p>
	 * Note that a segment encloses itself.
	 *
	 * @param segment  the segment to be tested for being enclosed by this segment.
	 * @return <code>true</code> if this <code>Segment</code> encloses the specified <code>Segment</code>, otherwise <code>false</code>.
	 */
	public final boolean encloses(final Segment segment) {
		return begin<=segment.begin && end>=segment.end;
	}

	/**
	 * Indicates whether this segment encloses the specified character position in the source document.
	 * <p>
	 * This is the case if {@link #getBegin()}<code> &lt;= pos &lt; </code>{@link #getEnd()}.
	 *
	 * @param pos  the position in the {@link Source} document.
	 * @return <code>true</code> if this segment encloses the specified character position in the source document, otherwise <code>false</code>.
	 */
	public final boolean encloses(final int pos) {
		return begin<=pos && pos<end;
	}

	/**
	 * Returns the source text of this segment as a <code>String</code>.
	 * <p>
	 * The returned <code>String</code> is newly created with every call to this method, unless this
	 * segment is itself an instance of {@link Source}.
	 *
	 * @return the source text of this segment as a <code>String</code>.
	 */
	public String toString() {
		return source.subSequence(begin,end).toString();
	}

	/**
	 * Performs a simple rendering of the HTML markup in this segment into text.
	 * <p>
	 * The output can be configured by setting any number of properties on the returned {@link Renderer} instance before
	 * {@linkplain Renderer#writeTo(Writer) obtaining its output}.
	 * 
	 * @return an instance of {@link Renderer} based on this segment.
	 * @see #getTextExtractor()
	 */
	public Renderer getRenderer() {
		return new Renderer(this);
	}

	/**
	 * Extracts the textual content from the HTML markup of this segment.
	 * <p>
	 * The output can be configured by setting properties on the returned {@link TextExtractor} instance before
	 * {@linkplain TextExtractor#writeTo(Writer) obtaining its output}.
	 * <p>
	 * @return an instance of {@link TextExtractor} based on this segment.
	 * @see #getRenderer()
	 */
	public TextExtractor getTextExtractor() {
		return new TextExtractor(this);
	}

	/**
	 * Returns an iterator over every {@linkplain Tag tag}, {@linkplain CharacterReference character reference} and <a href="Source.html#PlainText">plain text</a> segment contained within this segment.
	 * <p>
	 * See the {@link Source#iterator()} method for a detailed description.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd>
	 *   <p>
	 *    The following code demonstrates the typical usage of this method to make an exact copy of this segment to <code>writer</code> (assuming no server tags are present):
	 *   </p>
	 * <pre>
	 * for (Iterator&lt;Segment&gt; nodeIterator=segment.getNoteIterator(); nodeIterator.hasNext();) {
	 *   Segment nodeSegment=nodeIterator.next();
	 *   if (nodeSegment instanceof Tag) {
	 *     Tag tag=(Tag)nodeSegment;
	 *     // HANDLE TAG
	 *     // Uncomment the following line to ensure each tag is valid XML:
	 *     // writer.write(tag.tidy()); continue;
	 *   } else if (nodeSegment instanceof CharacterReference) {
	 *     CharacterReference characterReference=(CharacterReference)nodeSegment;
	 *     // HANDLE CHARACTER REFERENCE
	 *     // Uncomment the following line to decode all character references instead of copying them verbatim:
	 *     // characterReference.appendCharTo(writer); continue;
	 *   } else {
	 *     // HANDLE PLAIN TEXT
	 *   }
	 *   // unless specific handling has prevented getting to here, simply output the segment as is:
	 *   writer.write(nodeSegment.toString());
	 * }</pre>
	 *  </dd>
	 * </dl>
	 * @return an iterator over every {@linkplain Tag tag}, {@linkplain CharacterReference character reference} and <a href="Source.html#PlainText">plain text</a> segment contained within this segment.
	 */
	public Iterator<Segment> getNodeIterator() {
		return new NodeIterator(this);
	}

	/**
	 * Returns a list of all {@link Tag} objects that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * The {@link Source#fullSequentialParse()} method should be called after construction of the {@link Source} object
	 * if this method is to be used on a large proportion of the source.
	 * It is called automatically if this method is called on the {@link Source} object itself.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @return a list of all {@link Tag} objects that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 */
	public List<Tag> getAllTags() {
		return getAllTags(null);
	}

	/**
	 * Returns a list of all {@link Tag} objects of the specified {@linkplain TagType type} that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>tagType</code> parameter is equivalent to {@link #getAllTags()}.
	 *
	 * @param tagType  the {@linkplain TagType type} of tags to get.
	 * @return a list of all {@link Tag} objects of the specified {@linkplain TagType type} that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * @see #getAllStartTags(StartTagType)
	 */
	public List<Tag> getAllTags(final TagType tagType) {
		Tag tag=checkTagEnclosure(Tag.getNextTag(source,begin,tagType));
		if (tag==null) return Collections.emptyList();
		final ArrayList<Tag> list=new ArrayList<Tag>();
		do {
			list.add(tag);
			tag=checkTagEnclosure(tag.getNextTag(tagType));
		} while (tag!=null);
		return list;
	}

	/**
	 * Returns a list of all {@link StartTag} objects that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * The {@link Source#fullSequentialParse()} method should be called after construction of the {@link Source} object
	 * if this method is to be used on a large proportion of the source.
	 * It is called automatically if this method is called on the {@link Source} object itself.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @return a list of all {@link StartTag} objects that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 */
	public List<StartTag> getAllStartTags() {
		StartTag startTag=checkEnclosure(StartTag.getNext(source,begin));
		if (startTag==null) return Collections.emptyList();
		final ArrayList<StartTag> list=new ArrayList<StartTag>();
		do {
			list.add(startTag);
			startTag=checkEnclosure(startTag.getNextStartTag());
		} while (startTag!=null);
		return list;
	}

	/**
	 * Returns a list of all {@link StartTag} objects of the specified {@linkplain StartTagType type} that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>startTagType</code> parameter is equivalent to {@link #getAllStartTags()}.
	 *
	 * @param startTagType  the {@linkplain StartTagType type} of tags to get.
	 * @return a list of all {@link StartTag} objects of the specified {@linkplain StartTagType type} that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 */
	public List<StartTag> getAllStartTags(final StartTagType startTagType) {
		if (startTagType==null) return getAllStartTags();
		StartTag startTag=(StartTag)checkTagEnclosure(Tag.getNextTag(source,begin,startTagType));
		if (startTag==null) return Collections.emptyList();
		final ArrayList<StartTag> list=new ArrayList<StartTag>();
		do {
			list.add(startTag);
			startTag=(StartTag)checkTagEnclosure(startTag.getNextTag(startTagType));
		} while (startTag!=null);
		return list;
	}

	/**
	 * Returns a list of all {@linkplain StartTagType#NORMAL normal} {@link StartTag} objects with the specified {@linkplain StartTag#getName() name} that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>name</code> parameter is equivalent to {@link #getAllStartTags()}, which may include non-{@linkplain StartTagType#NORMAL normal} start tags.
	 * <p>
	 * This method also returns {@linkplain Tag#isUnregistered() unregistered} tags if the specified name is not a valid {@linkplain Tag#isXMLName(CharSequence) XML tag name}.
	 *
	 * @param name  the {@linkplain StartTag#getName() name} of the start tags to get.
	 * @return a list of all {@linkplain StartTagType#NORMAL normal} {@link StartTag} objects with the specified {@linkplain StartTag#getName() name} that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 */
	public List<StartTag> getAllStartTags(String name) {
		if (name==null) return getAllStartTags();
		final boolean isXMLTagName=Tag.isXMLName(name);
		StartTag startTag=checkEnclosure(StartTag.getNext(source,begin,name,StartTagType.NORMAL,isXMLTagName));
		if (startTag==null) return Collections.emptyList();
		final ArrayList<StartTag> list=new ArrayList<StartTag>();
		do {
			list.add(startTag);
			startTag=checkEnclosure(StartTag.getNext(source,startTag.begin+1,name,StartTagType.NORMAL,isXMLTagName));
		} while (startTag!=null);
		return list;
	}

	/**
	 * Returns a list of all {@link StartTag} objects with the specified attribute name/value pair that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param attributeName  the attribute name (case insensitive) to search for, must not be <code>null</code>.
	 * @param value  the value of the specified attribute to search for, must not be <code>null</code>.
	 * @param valueCaseSensitive  specifies whether the attribute value matching is case sensitive.
	 * @return a list of all {@link StartTag} objects with the specified attribute name/value pair that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * @see #getAllStartTags(String attributeName, Pattern valueRegexPattern)
	 */
	public List<StartTag> getAllStartTags(final String attributeName, final String value, final boolean valueCaseSensitive) {
		StartTag startTag=checkEnclosure(source.getNextStartTag(begin,attributeName,value,valueCaseSensitive));
		if (startTag==null) return Collections.emptyList();
		final ArrayList<StartTag> list=new ArrayList<StartTag>();
		do {
			list.add(startTag);
			startTag=checkEnclosure(source.getNextStartTag(startTag.begin+1,attributeName,value,valueCaseSensitive));
		} while (startTag!=null);
		return list;
	}

	/**
	 * Returns a list of all {@link StartTag} objects with the specified attribute name and value pattern that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>valueRegexPattern</code> parameter performs the search on the attribute name only,
	 * without regard to the attribute value.  This will also match an attribute that {@linkplain Attribute#hasValue() has no value} at all.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param attributeName  the attribute name (case insensitive) to search for, must not be <code>null</code>.
	 * @param valueRegexPattern  the regular expression pattern that must match the attribute value, may be <code>null</code>.
	 * @return a list of all {@link StartTag} objects with the specified attribute name and value pattern that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * @see #getAllStartTags(String attributeName, String value, boolean valueCaseSensitive)
	 */
	public List<StartTag> getAllStartTags(final String attributeName, final Pattern valueRegexPattern) {
		StartTag startTag=checkEnclosure(source.getNextStartTag(begin,attributeName,valueRegexPattern));
		if (startTag==null) return Collections.emptyList();
		final ArrayList<StartTag> list=new ArrayList<StartTag>();
		do {
			list.add(startTag);
			startTag=checkEnclosure(source.getNextStartTag(startTag.begin+1,attributeName,valueRegexPattern));
		} while (startTag!=null);
		return list;
	}

	/**
	 * Returns a list of all {@link StartTag} objects with the specified class that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * This matches start tags with a <code>class</code> attribute that contains the specified class name, either as an exact match or where the specified class name is one of multiple
	 * class names separated by white space in the attribute value.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param className  the class name (case sensitive) to search for, must not be <code>null</code>.
	 * @return a list of all {@link StartTag} objects with the specified class that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 */
	public List<StartTag> getAllStartTagsByClass(final String className) {
		return getAllStartTags("class",getClassPattern(className));
	}

	/**
	 * Returns a list of the immediate children of this segment in the document element hierarchy.
	 * <p>
	 * The returned list may include an element that extends beyond the end of this segment, as long as it begins within this segment.
	 * <p>
	 * An element found at the start of this segment is included in the list.
	 * Note however that if this segment <i>is</i> an {@link Element}, the overriding {@link Element#getChildElements()} method is called instead,
	 * which only returns the children of the element.
	 * <p>
	 * Calling <code>getChildElements()</code> on an <code>Element</code> is much more efficient than calling it on a <code>Segment</code>.
	 * <p>
	 * The objects in the list are all of type {@link Element}.
	 * <p>
	 * The {@link Source#fullSequentialParse()} method should be called after construction of the {@link Source} object
	 * if this method is to be used on a large proportion of the source.
	 * It is called automatically if this method is called on the {@link Source} object itself.
	 * <p>
	 * See the {@link Source#getChildElements()} method for more details.
	 *
	 * @return the a list of the immediate children of this segment in the document element hierarchy, guaranteed not <code>null</code>.
	 * @see Element#getParentElement()
	 */
	public List<Element> getChildElements() {
		if (length()==0) return Collections.emptyList();
		List<Element> childElements=new ArrayList<Element>();
		int pos=begin;
		while (true) {
			final StartTag childStartTag=source.getNextStartTag(pos);
			if (childStartTag==null || childStartTag.begin>=end) break;
			if (!Config.IncludeServerTagsInElementHierarchy && childStartTag.getTagType().isServerTag()) {
				pos=childStartTag.end;
				continue;
			}
			final Element childElement=childStartTag.getElement();
			childElements.add(childElement);
			childElement.getChildElements();
			pos=childElement.end;
		}
		return childElements;
	}

	/**
	 * Returns a list of all {@link Element} objects that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * The {@link Source#fullSequentialParse()} method should be called after construction of the {@link Source} object
	 * if this method is to be used on a large proportion of the source.
	 * It is called automatically if this method is called on the {@link Source} object itself.
	 * <p>
	 * The elements returned correspond exactly with the start tags returned in the {@link #getAllStartTags()} method.
	 * <p>
	 * If this segment is itself an {@link Element}, the result includes this element in the list.
	 *
	 * @return a list of all {@link Element} objects that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 */
	public List<Element> getAllElements() {
		return getAllElements(getAllStartTags());
	}

	/**
	 * Returns a list of all {@link Element} objects with the specified name that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * The elements returned correspond with the start tags returned in the {@link #getAllStartTags(String name)} method,
	 * except that elements which are not entirely enclosed by this segment are excluded.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>name</code> parameter is equivalent to {@link #getAllElements()}, which may include elements of non-{@linkplain StartTagType#NORMAL normal} tags.
	 * <p>
	 * This method also returns elements consisting of {@linkplain Tag#isUnregistered() unregistered} tags if the specified name is not a valid {@linkplain Tag#isXMLName(CharSequence) XML tag name}.
	 * <p>
	 * If this segment is itself an {@link Element} with the specified name, the result includes this element in the list.
	 *
	 * @param name  the {@linkplain Element#getName() name} of the elements to get.
	 * @return a list of all {@link Element} objects with the specified name that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 */
	public List<Element> getAllElements(String name) {
		return getAllElements(getAllStartTags(name));
	}

	/**
	 * Returns a list of all {@link Element} objects with start tags of the specified {@linkplain StartTagType type} that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * The elements returned correspond with the start tags returned in the {@link #getAllTags(TagType)} method,
	 * except that elements which are not entirely enclosed by this segment are excluded.
	 * <p>
	 * If this segment is itself an {@link Element} with the specified type, the result includes this element in the list.
	 *
	 * @param startTagType  the {@linkplain StartTagType type} of start tags to get, must not be <code>null</code>.
	 * @return a list of all {@link Element} objects with start tags of the specified {@linkplain StartTagType type} that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 */
	public List<Element> getAllElements(final StartTagType startTagType) {
		if (startTagType==null) throw new IllegalArgumentException("startTagType argument must not be null");
		return getAllElements(getAllStartTags(startTagType));
	}

	/**
	 * Returns a list of all {@link Element} objects with the specified attribute name/value pair that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * The elements returned correspond with the start tags returned in the {@link #getAllStartTags(String attributeName, String value, boolean valueCaseSensitive)} method,
	 * except that elements which are not entirely enclosed by this segment are excluded.
	 * <p>
	 * If this segment is itself an {@link Element} with the specified name/value pair, the result includes this element in the list.
	 *
	 * @param attributeName  the attribute name (case insensitive) to search for, must not be <code>null</code>.
	 * @param value  the value of the specified attribute to search for, must not be <code>null</code>.
	 * @param valueCaseSensitive  specifies whether the attribute value matching is case sensitive.
	 * @return a list of all {@link Element} objects with the specified attribute name/value pair that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * @see #getAllElements(String attributeName, Pattern valueRegexPattern)
	 */
	public List<Element> getAllElements(final String attributeName, final String value, final boolean valueCaseSensitive) {
		return getAllElements(getAllStartTags(attributeName,value,valueCaseSensitive));
	}

	/**
	 * Returns a list of all {@link Element} objects with the specified attribute name and value pattern that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * The elements returned correspond with the start tags returned in the {@link #getAllStartTags(String attributeName, Pattern valueRegexPattern)} method,
	 * except that elements which are not entirely enclosed by this segment are excluded.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>valueRegexPattern</code> parameter performs the search on the attribute name only,
	 * without regard to the attribute value.  This will also match an attribute that {@linkplain Attribute#hasValue() has no value} at all.
	 * <p>
	 * If this segment is itself an {@link Element} with the specified attribute name and value pattern, the result includes this element in the list.
	 *
	 * @param attributeName  the attribute name (case insensitive) to search for, must not be <code>null</code>.
	 * @param valueRegexPattern  the regular expression pattern that must match the attribute value, may be <code>null</code>.
	 * @return a list of all {@link Element} objects with the specified attribute name and value pattern that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * @see #getAllElements(String attributeName, String value, boolean valueCaseSensitive)
	 */
	public List<Element> getAllElements(final String attributeName, final Pattern valueRegexPattern) {
		return getAllElements(getAllStartTags(attributeName,valueRegexPattern));
	}

	/**
	 * Returns a list of all {@link Element} objects with the specified class that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * This matches elements with a <code>class</code> attribute that contains the specified class name, either as an exact match or where the specified class name is one of multiple
	 * class names separated by white space in the attribute value.
	 * <p>
	 * The elements returned correspond with the start tags returned in the {@link #getAllStartTagsByClass(String className)} method,
	 * except that elements which are not entirely enclosed by this segment are excluded.
	 * <p>
	 * If this segment is itself an {@link Element} with the specified class, the result includes this element in the list.
	 *
	 * @param className  the class name (case sensitive) to search for, must not be <code>null</code>.
	 * @return a list of all {@link Element} objects with the specified class that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 */
	public List<Element> getAllElementsByClass(final String className) {
		return getAllElements(getAllStartTagsByClass(className));
	}

	/**
	 * Returns a list of all {@link CharacterReference} objects that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * @return a list of all {@link CharacterReference} objects that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 */
	public List<CharacterReference> getAllCharacterReferences() {
		CharacterReference characterReference=getNextCharacterReference(begin);
		if (characterReference==null) return Collections.emptyList();
		final ArrayList<CharacterReference> list=new ArrayList<CharacterReference>();
		do {
			list.add(characterReference);
			characterReference=getNextCharacterReference(characterReference.end);
		} while (characterReference!=null);
		return list;
	}

	/**
	 * Returns the first {@link StartTag} {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * This is functionally equivalent to {@link #getAllStartTags()}<code>.iterator().next()</code>,
	 * but does not search beyond the first start tag and returns <code>null</code> if no such start tag exists.
	 *
	 * @return the first {@link StartTag} {@linkplain #encloses(Segment) enclosed} by this segment, or <code>null</code> if none exists.
	 */
	public final StartTag getFirstStartTag() {
		return checkEnclosure(source.getNextStartTag(begin));
	}
	
	/**
	 * Returns the first {@link StartTag} of the specified {@linkplain StartTagType type} {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * This is functionally equivalent to {@link #getAllStartTags(StartTagType) getAllStartTags(startTagType)}<code>.iterator().next()</code>,
	 * but does not search beyond the first start tag and returns <code>null</code> if no such start tag exists.
	 *
	 * @param startTagType  the <code>StartTagType</code> to search for.
	 * @return the first {@link StartTag} of the specified {@linkplain StartTagType type} {@linkplain #encloses(Segment) enclosed} by this segment, or <code>null</code> if none exists.
	 */
	public final StartTag getFirstStartTag(StartTagType startTagType) {
		return checkEnclosure(source.getNextStartTag(begin,startTagType));
	}
	
	/**
	 * Returns the first {@linkplain StartTagType#NORMAL normal} {@link StartTag} {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * This is functionally equivalent to {@link #getAllStartTags(String) getAllStartTags(name)}<code>.iterator().next()</code>,
	 * but does not search beyond the first start tag and returns <code>null</code> if no such start tag exists.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>name</code> parameter is equivalent to {@link #getFirstStartTag()}.
	 *
	 * @param name  the {@linkplain StartTag#getName() name} of the start tag to search for, may be <code>null</code>.
	 * @return the first {@linkplain StartTagType#NORMAL normal} {@link StartTag} {@linkplain #encloses(Segment) enclosed} by this segment, or <code>null</code> if none exists.
	 */
	public final StartTag getFirstStartTag(String name) {
		return checkEnclosure(source.getNextStartTag(begin,name));
	}
	
	/**
	 * Returns the first {@link StartTag} with the specified attribute name/value pair {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * This is functionally equivalent to {@link #getAllStartTags(String,String,boolean) getAllStartTags(attributeName,value,valueCaseSensitive)}<code>.iterator().next()</code>,
	 * but does not search beyond the first start tag and returns <code>null</code> if no such start tag exists.
	 *
	 * @param attributeName  the attribute name (case insensitive) to search for, must not be <code>null</code>.
	 * @param value  the value of the specified attribute to search for, must not be <code>null</code>.
	 * @param valueCaseSensitive  specifies whether the attribute value matching is case sensitive.
	 * @return the first {@link StartTag} with the specified attribute name/value pair {@linkplain #encloses(Segment) enclosed} by this segment, or <code>null</code> if none exists.
	 * @see #getFirstStartTag(String attributeName, Pattern valueRegexPattern)
	 */
	public final StartTag getFirstStartTag(String attributeName, String value, boolean valueCaseSensitive) {
		return checkEnclosure(source.getNextStartTag(begin,attributeName,value,valueCaseSensitive));
	}

	/**
	 * Returns the first {@link StartTag} with the specified attribute name and value pattern that is {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * This is functionally equivalent to {@link #getAllStartTags(String,Pattern) getAllStartTags(attributeName,valueRegexPattern)}<code>.iterator().next()</code>,
	 * but does not search beyond the first start tag and returns <code>null</code> if no such start tag exists.
	 *
	 * @param attributeName  the attribute name (case insensitive) to search for, must not be <code>null</code>.
	 * @param valueRegexPattern  the regular expression pattern that must match the attribute value, may be <code>null</code>.
	 * @return the first {@link StartTag} with the specified attribute name and value pattern that is {@linkplain #encloses(Segment) enclosed} by this segment, or <code>null</code> if none exists.
	 * @see #getFirstStartTag(String attributeName, String value, boolean valueCaseSensitive)
	 */
	public final StartTag getFirstStartTag(final String attributeName, final Pattern valueRegexPattern) {
		return checkEnclosure(source.getNextStartTag(begin,attributeName,valueRegexPattern));
	}

	/**
	 * Returns the first {@link StartTag} with the specified class that is {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * This is functionally equivalent to {@link #getAllStartTagsByClass(String) getAllStartTagsByClass(className)}<code>.iterator().next()</code>,
	 * but does not search beyond the first start tag and returns <code>null</code> if no such start tag exists.
	 *
	 * @param className  the class name (case sensitive) to search for, must not be <code>null</code>.
	 * @return the first {@link StartTag} with the specified class that is {@linkplain #encloses(Segment) enclosed} by this segment, or <code>null</code> if none exists.
	 */
	public final StartTag getFirstStartTagByClass(final String className) {
		return checkEnclosure(source.getNextStartTagByClass(begin,className));
	}
	
	/**
	 * Returns the first {@link Element} {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * This is functionally equivalent to {@link #getAllElements()}<code>.iterator().next()</code>,
	 * but does not search beyond the first enclosed element and returns <code>null</code> if no such element exists.
	 * <p>
	 * If this segment is itself an {@link Element}, this element is returned, not the first child element.
	 *
	 * @return the first {@link Element} {@linkplain #encloses(Segment) enclosed} by this segment, or <code>null</code> if none exists.
	 */
	public final Element getFirstElement() {
		StartTag startTag=checkEnclosure(StartTag.getNext(source,begin));
		while (startTag!=null) {
			final Element element=startTag.getElement();
			if (element.end<=end) return element;
			startTag=checkEnclosure(startTag.getNextStartTag());
		}
		return null;
	}
	
	/**
	 * Returns the first {@linkplain StartTagType#NORMAL normal} {@link Element} with the specified {@linkplain Element#getName() name} {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * This is functionally equivalent to {@link #getAllElements(String) getAllElements(name)}<code>.iterator().next()</code>,
	 * but does not search beyond the first enclosed element and returns <code>null</code> if no such element exists.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>name</code> parameter is equivalent to {@link #getFirstElement()}.
	 * <p>
	 * If this segment is itself an {@link Element} with the specified name, this element is returned.
	 *
	 * @param name  the {@linkplain Element#getName() name} of the element to search for.
	 * @return the first {@linkplain StartTagType#NORMAL normal} {@link Element} with the specified {@linkplain Element#getName() name} {@linkplain #encloses(Segment) enclosed} by this segment, or <code>null</code> if none exists.
	 */
	public final Element getFirstElement(String name) {
		if (name==null) return getFirstElement();
		final boolean isXMLTagName=Tag.isXMLName(name);
		StartTag startTag=checkEnclosure(StartTag.getNext(source,begin,name,StartTagType.NORMAL,isXMLTagName));
		while (startTag!=null) {
			final Element element=startTag.getElement();
			if (element.end<=end) return element;
			startTag=checkEnclosure(StartTag.getNext(source,startTag.begin+1,name,StartTagType.NORMAL,isXMLTagName));
		}
		return null;
	}
	
	/**
	 * Returns the first {@link Element} with the specified attribute name/value pair {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * This is functionally equivalent to {@link #getAllElements(String,String,boolean) getAllElements(attributeName,value,valueCaseSensitive)}<code>.iterator().next()</code>,
	 * but does not search beyond the first enclosed element and returns <code>null</code> if no such element exists.
	 * <p>
	 * If this segment is itself an {@link Element} with the specified attribute name/value pair, this element is returned.
	 *
	 * @param attributeName  the attribute name (case insensitive) to search for, must not be <code>null</code>.
	 * @param value  the value of the specified attribute to search for, must not be <code>null</code>.
	 * @param valueCaseSensitive  specifies whether the attribute value matching is case sensitive.
	 * @return the first {@link Element} with the specified attribute name/value pair {@linkplain #encloses(Segment) enclosed} by this segment, or <code>null</code> if none exists.
	 * @see #getFirstElement(String attributeName, Pattern valueRegexPattern)
	 */
	public final Element getFirstElement(String attributeName, String value, boolean valueCaseSensitive) {
		StartTag startTag=checkEnclosure(source.getNextStartTag(begin,attributeName,value,valueCaseSensitive));
		while (startTag!=null) {
			final Element element=startTag.getElement();
			if (element.end<=end) return element;
			startTag=checkEnclosure(source.getNextStartTag(startTag.begin+1,attributeName,value,valueCaseSensitive));
		}
		return null;
	}

	/**
	 * Returns the first {@link Element} with the specified attribute name and value pattern that is {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * This is functionally equivalent to {@link #getAllElements(String,Pattern) getAllElements(attributeName,valueRegexPattern)}<code>.iterator().next()</code>,
	 * but does not search beyond the first enclosed element and returns <code>null</code> if no such element exists.
	 * <p>
	 * If this segment is itself an {@link Element} with the specified attribute name and value pattern, this element is returned.
	 *
	 * @param attributeName  the attribute name (case insensitive) to search for, must not be <code>null</code>.
	 * @param valueRegexPattern  the regular expression pattern that must match the attribute value, may be <code>null</code>.
	 * @return the first {@link Element} with the specified attribute name and value pattern that is {@linkplain #encloses(Segment) enclosed} by this segment, or <code>null</code> if none exists.
	 * @see #getFirstElement(String attributeName, String value, boolean valueCaseSensitive)
	 */
	public final Element getFirstElement(final String attributeName, final Pattern valueRegexPattern) {
		StartTag startTag=checkEnclosure(source.getNextStartTag(begin,attributeName,valueRegexPattern));
		while (startTag!=null) {
			final Element element=startTag.getElement();
			if (element.end<=end) return element;
			startTag=checkEnclosure(source.getNextStartTag(startTag.begin+1,attributeName,valueRegexPattern));
		}
		return null;
	}

	/**
	 * Returns the first {@link Element} with the specified class that is {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * This is functionally equivalent to {@link #getAllElementsByClass(String) getAllElementsByClass(className)}<code>.iterator().next()</code>,
	 * but does not search beyond the first enclosed element and returns <code>null</code> if no such element exists.
	 * <p>
	 * If this segment is itself an {@link Element} with the specified class, this element is returned.
	 *
	 * @param className  the class name (case sensitive) to search for, must not be <code>null</code>.
	 * @return the first {@link Element} with the specified class that is {@linkplain #encloses(Segment) enclosed} by this segment, or <code>null</code> if none exists.
	 */
	public final Element getFirstElementByClass(final String className) {
		StartTag startTag=checkEnclosure(source.getNextStartTagByClass(begin,className));
		while (startTag!=null) {
			final Element element=startTag.getElement();
			if (element.end<=end) return element;
			startTag=checkEnclosure(source.getNextStartTagByClass(startTag.begin+1,className));
		}
		return null;
	}

	/**
	 * Returns a list of the {@link FormControl} objects that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * @return a list of the {@link FormControl} objects that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 */
	public List<FormControl> getFormControls() {
		return FormControl.getAll(this);
	}

	/**
	 * Returns the {@link FormFields} object representing all form fields that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * <p>
	 * This is equivalent to {@link FormFields#FormFields(Collection) new FormFields}<code>(</code>{@link #getFormControls()}<code>)</code>.
	 *
	 * @return the {@link FormFields} object representing all form fields that are {@linkplain #encloses(Segment) enclosed} by this segment.
	 * @see #getFormControls()
	 */
	public FormFields getFormFields() {
		return new FormFields(getFormControls());
	}

	/**
	 * Parses any {@link Attributes} within this segment.
	 * This method is only used in the unusual situation where attributes exist outside of a start tag.
	 * The {@link StartTag#getAttributes()} method should be used in normal situations.
	 * <p>
	 * This is equivalent to <code>source.</code>{@link Source#parseAttributes(int,int) parseAttributes}<code>(</code>{@link #getBegin()}<code>,</code>{@link #getEnd()}<code>)</code>.
	 *
	 * @return the {@link Attributes} within this segment, or <code>null</code> if too many errors occur while parsing.
	 */
	public Attributes parseAttributes() {
		return source.parseAttributes(begin,end);
	}

	/**
	 * Causes the this segment to be ignored when parsing.
	 * <p>
	 * Ignored segments are treated as blank spaces by the parsing mechanism, but are included as normal text in all other functions.
	 * <p>
	 * This method was originally the only means of preventing {@linkplain TagType#isServerTag() server tags} located inside
	 * {@linkplain StartTagType#NORMAL normal} tags from interfering with the parsing of the tags
	 * (such as where an {@linkplain Attribute attribute} of a normal tag uses a server tag to dynamically set its value),
	 * as well as preventing non-server tags from being recognised inside server tags.
	 * <p>
	 * It is not necessary to use this method to ignore {@linkplain TagType#isServerTag() server tags} located inside normal tags,
	 * as the attributes parser automatically ignores any server tags.
	 * <p>
	 * It is not necessary to use this method to ignore non-server tags inside server tags, or the contents of {@link HTMLElementName#SCRIPT SCRIPT} elements,
	 * as the parser does this automatically when performing a {@linkplain Source#fullSequentialParse() full sequential parse}.
	 * <p>
	 * This leaves only very few scenarios where calling this method still provides a significant benefit.
	 * <p>
	 * One such case is where XML-style server tags are used inside {@linkplain StartTagType#NORMAL normal} tags.
	 * Here is an example using an XML-style JSP tag:
	 * <blockquote class="code"><code>&lt;a href="&lt;i18n:resource path="/Portal"/&gt;?BACK=TRUE"&gt;back&lt;/a&gt;</code></blockquote>
	 * The first double-quote of <code>"/Portal"</code> will be interpreted as the end quote for the <code>href</code> attribute,
	 * as there is no way for the parser to recognise the <code>il8n:resource</code> element as a server tag.
	 * Such use of XML-style server tags inside {@linkplain StartTagType#NORMAL normal} tags is generally seen as bad practice,
	 * but it is nevertheless valid JSP.  The only way to ensure that this library is able to parse the normal tag surrounding it is to
	 * find these server tags first and call the <code>ignoreWhenParsing</code> method to ignore them before parsing the rest of the document.
	 * <p>
	 * It is important to understand the difference between ignoring the segment when parsing and removing the segment completely.
	 * Any text inside a segment that is ignored when parsing is treated by most functions as content, and as such is included in the output of
	 * tools such as {@link TextExtractor} and {@link Renderer}.
	 * <p>
	 * To remove segments completely, create an {@link OutputDocument} and call its {@link OutputDocument#remove(Segment) remove(Segment)} or
	 * {@link OutputDocument#replaceWithSpaces(int,int) replaceWithSpaces(int begin, int end)} method for each segment.
	 * Then create a new source document using {@link Source#Source(CharSequence) new Source(outputDocument.toString())}
	 * and perform the desired operations on this new source object.
	 * <p>
	 * Calling this method after the {@link Source#fullSequentialParse()} method has been called is not permitted and throws an <code>IllegalStateException</code>.
	 * <p>
	 * Any tags appearing in this segment that are found before this method is called will remain in the {@linkplain Source#getCacheDebugInfo() tag cache},
	 * and so will continue to be found by the <a href="Tag.html#TagSearchMethods">tag search methods</a>.
	 * If this is undesirable, the {@link Source#clearCache()} method can be called to remove them from the cache.
	 * Calling the {@link Source#fullSequentialParse()} method after this method clears the cache automatically.
	 * <p>
	 * For best performance, this method should be called on all segments that need to be ignored without calling
	 * any of the <a href="Tag.html#TagSearchMethods">tag search methods</a> in between.
	 *
	 * @see Source#ignoreWhenParsing(Collection segments)
	 */
	public void ignoreWhenParsing() {
		source.ignoreWhenParsing(begin,end);
	}

	/**
	 * Compares this <code>Segment</code> object to another object.
	 * <p>
	 * If the argument is not a <code>Segment</code>, a <code>ClassCastException</code> is thrown.
	 * <p>
	 * A segment is considered to be before another segment if its begin position is earlier,
	 * or in the case that both segments begin at the same position, its end position is earlier.
	 * <p>
	 * Segments that begin and end at the same position are considered equal for
	 * the purposes of this comparison, even if they relate to different source documents.
	 * <p>
	 * Note: this class has a natural ordering that is inconsistent with equals.
	 * This means that this method may return zero in some cases where calling the
	 * {@link #equals(Object)} method with the same argument returns <code>false</code>.
	 *
	 * @param segment  the segment to be compared
	 * @return a negative integer, zero, or a positive integer as this segment is before, equal to, or after the specified segment.
	 * @throws ClassCastException if the argument is not a <code>Segment</code>
	 */
	public int compareTo(final Segment segment) {
		if (this==segment) return 0;
		if (begin<segment.begin) return -1;
		if (begin>segment.begin) return 1;
		if (end<segment.end) return -1;
		if (end>segment.end) return 1;
		return 0;
	}

	/**
	 * Indicates whether this segment consists entirely of {@linkplain #isWhiteSpace(char) white space}.
	 * @return <code>true</code> if this segment consists entirely of {@linkplain #isWhiteSpace(char) white space}, otherwise <code>false</code>.
	 */
	public final boolean isWhiteSpace() {
		for (int i=begin; i<end; i++)
			if (!isWhiteSpace(source.charAt(i))) return false;
		return true;
	}

	/**
	 * Indicates whether the specified character is <a target="_blank" href="http://www.w3.org/TR/html401/struct/text.html#h-9.1">white space</a>.
	 * <p>
	 * The <a target="_blank" href="http://www.w3.org/TR/html401/struct/text.html#h-9.1">HTML 4.01 specification section 9.1</a>
	 * specifies the following white space characters:
	 * <ul>
	 *  <li>space (U+0020)
	 *  <li>tab (U+0009)
	 *  <li>form feed (U+000C)
	 *  <li>line feed (U+000A)
	 *  <li>carriage return (U+000D)
	 *  <li>zero-width space (U+200B)
	 * </ul>
	 * <p>
	 * Despite the explicit inclusion of the zero-width space in the HTML specification, Microsoft IE6 does not
	 * recognise them as white space and renders them as an unprintable character (empty square).
	 * Even zero-width spaces included using the numeric character reference <code>&amp;#x200B;</code> are rendered this way.
	 *
	 * @param ch  the character to test.
	 * @return <code>true</code> if the specified character is <a target="_blank" href="http://www.w3.org/TR/html401/struct/text.html#h-9.1">white space</a>, otherwise <code>false</code>.
	 */
	public static final boolean isWhiteSpace(final char ch) {
		for (char whiteSpaceChar : WHITESPACE) if (ch==whiteSpaceChar) return true;
		return false;
	}

	/**
	 * Returns a string representation of this object useful for debugging purposes.
	 * @return a string representation of this object useful for debugging purposes.
	 */
	public String getDebugInfo() {
		final StringBuilder sb=new StringBuilder(50);
		sb.append('(');
		source.getRowColumnVector(begin).appendTo(sb);
		sb.append('-');
		source.getRowColumnVector(end).appendTo(sb);
		sb.append(')');
		return sb.toString();
	}

	/**
	 * Returns the character at the specified index.
	 * <p>
	 * This is logically equivalent to <code>toString().charAt(index)</code>
	 * for valid argument values <code>0 <= index < length()</code>.
	 * <p>
	 * However because this implementation works directly on the underlying document source string,
	 * it should not be assumed that an <code>IndexOutOfBoundsException</code> is thrown
	 * for an invalid argument value.
	 *
	 * @param index  the index of the character.
	 * @return the character at the specified index.
	 */
	public char charAt(final int index) {
		return source.charAt(begin+index);
	}

	/**
	 * Returns a new character sequence that is a subsequence of this sequence.
	 * <p>
	 * This is logically equivalent to <code>toString().subSequence(beginIndex,endIndex)</code>
	 * for valid values of <code>beginIndex</code> and <code>endIndex</code>.
	 * <p>
	 * However because this implementation works directly on the underlying document source text,
	 * it should not be assumed that an <code>IndexOutOfBoundsException</code> is thrown
	 * for invalid argument values as described in the <code>String.subSequence(int,int)</code> method.
	 *
	 * @param beginIndex  the begin index, inclusive.
	 * @param endIndex  the end index, exclusive.
	 * @return a new character sequence that is a subsequence of this sequence.
	 */
	public CharSequence subSequence(final int beginIndex, final int endIndex) {
		return source.subSequence(begin+beginIndex,begin+endIndex);
	}

	/**
	 * Collapses the {@linkplain #isWhiteSpace(char) white space} in the specified text.
	 * All leading and trailing white space is omitted, and any sections of internal white space are replaced by a single space.
	 */
	static final StringBuilder appendCollapseWhiteSpace(final StringBuilder sb, final CharSequence text) {
		final int textLength=text.length();
		int i=0;
		boolean lastWasWhiteSpace=false;
		while (true) {
			if (i>=textLength) return sb;
			if (!isWhiteSpace(text.charAt(i))) break;
			i++;
		}
		do {
			final char ch=text.charAt(i++);
			if (isWhiteSpace(ch)) {
				lastWasWhiteSpace=true;
			} else {
				if (lastWasWhiteSpace) {
					sb.append(' ');
					lastWasWhiteSpace=false;
				}
				sb.append(ch);
			}
		} while (i<textLength);
		return sb;
	}

	static final Pattern getClassPattern(final String className) {
		return Pattern.compile(".*(\\s|^)"+className+"(\\s|$).*",Pattern.DOTALL);
	}

	private List<Element> getAllElements(final List<StartTag> startTags) {
		if (startTags.isEmpty()) return Collections.emptyList();
		final ArrayList<Element> elements=new ArrayList<Element>(startTags.size());
		for (StartTag startTag : startTags) {
			final Element element=startTag.getElement();
			if (element.end<=end) elements.add(element);
		}
		return elements;
	}

	private StartTag checkEnclosure(final StartTag startTag) {
		if (startTag==null || startTag.end>end) return null;
		return startTag;
	}

	private Tag checkTagEnclosure(final Tag tag) {
		if (tag==null || tag.end>end) return null;
		return tag;
	}

	private CharacterReference getNextCharacterReference(final int pos) {
		final CharacterReference characterReference=source.getNextCharacterReference(pos);
		if (characterReference==null || characterReference.end>end) return null;
		return characterReference;
	}
}

