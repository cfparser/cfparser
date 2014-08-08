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

/**
 * Represents the <a target="_blank" href="http://www.w3.org/TR/html401/intro/sgmltut.html#didx-element-3">end tag</a> of an
 * {@linkplain Element element} in a specific {@linkplain Source source} document.
 * <p>
 * An end tag always has a {@linkplain #getTagType() type} that is a subclass of {@link EndTagType}, meaning it
 * always starts with the characters '<code>&lt;/</code>'.
 * <p>
 * <code>EndTag</code> instances are obtained using one of the following methods:
 * <ul>
 *  <li>{@link Element#getEndTag()}
 *  <li>{@link Tag#getNextTag()}
 *  <li>{@link Tag#getPreviousTag()}
 *  <li>{@link Source#getPreviousEndTag(int pos)}
 *  <li>{@link Source#getPreviousEndTag(int pos, String name)}
 *  <li>{@link Source#getPreviousTag(int pos)}
 *  <li>{@link Source#getPreviousTag(int pos, TagType)}
 *  <li>{@link Source#getNextEndTag(int pos)}
 *  <li>{@link Source#getNextEndTag(int pos, String name)}
 *  <li>{@link Source#getNextEndTag(int pos, String name, EndTagType)}
 *  <li>{@link Source#getNextTag(int pos)}
 *  <li>{@link Source#getNextTag(int pos, TagType)}
 *  <li>{@link Source#getEnclosingTag(int pos)}
 *  <li>{@link Source#getEnclosingTag(int pos, TagType)}
 *  <li>{@link Source#getTagAt(int pos)}
 *  <li>{@link Segment#getAllTags()}
 *  <li>{@link Segment#getAllTags(TagType)}
 * </ul>
 * <p>
 * The {@link Tag} superclass defines the {@link Tag#getName() getName()} method used to get the name of this end tag.
 * <p>
 * See also the XML 1.0 specification for <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-etag">end tags</a>.
 *
 * @see Tag
 * @see StartTag
 * @see Element
 */
public final class EndTag extends Tag {
	private final EndTagType endTagType;

	/**
	 * Constructs a new <code>EndTag</code>.
	 *
	 * @param source  the {@link Source} document.
	 * @param begin  the character position in the source document where this tag {@linkplain Segment#getBegin() begins}.
	 * @param end  the character position in the source document where this tag {@linkplain Segment#getEnd() ends}.
	 * @param endTagType  the {@linkplain #getEndTagType() type} of the end tag.
	 * @param name  the {@linkplain Tag#getName() name} of the tag.
	 */
	EndTag(final Source source, final int begin, final int end, final EndTagType endTagType, final String name) {
		super(source,begin,end,name);
		this.endTagType=endTagType;
	}

	/**
	 * Returns the {@linkplain Element element} that is ended by this end tag.
	 * <p>
	 * Returns <code>null</code> if this end tag is not properly matched to any {@linkplain StartTag start tag} in the source document.
	 * <p>
	 * This method is much less efficient than the {@link StartTag#getElement()} method.
	 * <p>
	 * IMPLEMENTATION NOTE: The explanation for why this method is relatively inefficient lies in the fact that more than one
	 * {@linkplain StartTagType start tag type} can have the same 
	 * {@linkplain StartTagType#getCorrespondingEndTagType() corresponding end tag type}, so it is not possible to know for certain
	 * which type of start tag this end tag is matched to (see {@link EndTagType#getCorrespondingStartTagType()} for more explanation).
	 * Because of this uncertainty, the implementation of this method must check every start tag preceding this end tag, calling its
	 * {@link StartTag#getElement()} method to see whether it is terminated by this end tag.
	 *
	 * @return the {@linkplain Element element} that is ended by this end tag.
	 */
	public Element getElement() {
		if (element!=Element.NOT_CACHED) return element;
		int pos=begin;
		while (pos!=0) {
			StartTag startTag=source.getPreviousStartTag(pos-1);
			if (startTag==null) break;
			Element foundElement=startTag.getElement(); // this automatically sets foundElement.getEndTag().element cache
			if (foundElement.getEndTag()==this) return foundElement; // no need to set element as it was already done in previous statement
			pos=startTag.begin;
		}
		return element=null;
	}

	/**
	 * Returns the {@linkplain EndTagType type} of this end tag.	
	 * <p>
	 * This is equivalent to <code>(EndTagType)</code>{@link #getTagType()}.
	 *
	 * @return the {@linkplain EndTagType type} of this end tag.	
	 */
	public EndTagType getEndTagType() {
		return endTagType;
	}

	// Documentation inherited from Tag
	public TagType getTagType() {
		return endTagType;
	}

	// Documentation inherited from Tag
	public boolean isUnregistered() {
		return endTagType==EndTagType.UNREGISTERED;
	}

	/**
	 * Returns an XML representation of this end tag.
	 * <p>
	 * This method is included for symmetry with the {@link StartTag#tidy()} method and simply
	 * returns the {@linkplain Segment#toString() source text} of the tag.
	 *
	 * @return an XML representation of this end tag.
	 */
	public String tidy() {
		return toString();
	}

	/**
	 * Generates the HTML text of a {@linkplain EndTagType#NORMAL normal} end tag with the specified tag {@linkplain #getName() name}.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd>
	 *   <p>
	 *   The following method call:
	 *   <blockquote class="code">
	 *    <code>EndTag.generateHTML("INPUT")</code>
	 *   </blockquote>
	 *   returns the following output:
	 *   <blockquote class="code">
	 *    <code>&lt;/INPUT&gt;</code>
	 *   </blockquote>
	 *  </dd>
	 * </dl>
	 *
	 * @param tagName  the {@linkplain #getName() name} of the end tag.
	 * @return the HTML text of a {@linkplain EndTagType#NORMAL normal} end tag with the specified tag {@linkplain #getName() name}.
	 * @see StartTag#generateHTML(String tagName, Map attributesMap, boolean emptyElementTag)
	 */
	public static String generateHTML(final String tagName) {
		return EndTagType.NORMAL.generateHTML(tagName);
	}

	public String getDebugInfo() {
		final StringBuilder sb=new StringBuilder();
		sb.append(this).append(' ');
		if (endTagType!=EndTagType.NORMAL) sb.append('(').append(endTagType.getDescription()).append(") ");
		sb.append(super.getDebugInfo());
		return sb.toString();
	}

	/**
	 * Returns the previous end tag matching the specified {@linkplain #getName() name} and {@linkplain EndTagType type}, starting at the specified position.
	 * <p>
	 * Called from {@link Source#getPreviousEndTag(int pos, String name)}.
	 *
	 * @param source  the {@link Source} document.
	 * @param pos  the position to search from.
	 * @param name  the {@linkplain #getName() name} of the tag including its {@linkplain TagType#getNamePrefix() prefix} (must be lower case, may be null).
	 * @param endTagType the {@linkplain EndTagType type} of end tag to search for.
	 * @return the previous end tag matching the specified {@linkplain #getName() name} and {@linkplain EndTagType type}, starting at the specified position, or null if none is found.
	 */
	static EndTag getPrevious(final Source source, final int pos, final String name, final EndTagType endTagType) {
		if (name==null) return (EndTag)Tag.getPreviousTag(source,pos,endTagType);
		if (name.length()==0) throw new IllegalArgumentException("name argument must not be zero length");
		final String searchString=endTagType.START_DELIMITER_PREFIX+name;
		try {
			final ParseText parseText=source.getParseText();
			int begin=pos;
			do {
				begin=parseText.lastIndexOf(searchString,begin);
				if (begin==-1) return null;
				final EndTag endTag=(EndTag)source.getTagAt(begin);
				if (endTag!=null && endTag.getEndTagType()==endTagType && name.equals(endTag.getName())) return endTag;
			} while ((begin-=1)>=0);
		} catch (IndexOutOfBoundsException ex) {
			// this should never happen during a get previous operation so rethrow it:
			throw ex;
		}
		return null;
	}

	/**
	 * Returns the next end tag matching the specified {@linkplain #getName() name} and {@linkplain EndTagType type}, starting at the specified position.
	 * <p>
	 * Called from {@link Source#getNextEndTag(int pos, String name, EndTagType endTagType)}.
	 *
	 * @param source  the {@link Source} document.
	 * @param pos  the position to search from.
	 * @param name  the {@linkplain #getName() name} of the tag including its {@linkplain TagType#getNamePrefix() prefix} (must be lower case, may be null).
	 * @param endTagType the {@linkplain EndTagType type} of end tag to search for.
	 * @return the next end tag matching the specified {@linkplain #getName() name} and {@linkplain EndTagType type}, starting at the specified position, or null if none is found.
	 */
	static EndTag getNext(final Source source, final int pos, final String name, final EndTagType endTagType) {
		if (name==null) return (EndTag)Tag.getNextTag(source,pos,endTagType);
		if (name.length()==0) throw new IllegalArgumentException("name argument must not be zero length");
		final String searchString=endTagType.START_DELIMITER_PREFIX+name;
		try {
			final ParseText parseText=source.getParseText();
			int begin=pos;
			do {
				begin=parseText.indexOf(searchString,begin);
				if (begin==-1) return null;
				final EndTag endTag=(EndTag)source.getTagAt(begin);
				if (endTag!=null && endTag.getEndTagType()==endTagType && name.equals(endTag.getName())) return endTag;
			} while ((begin+=1)<source.end);
		} catch (IndexOutOfBoundsException ex) {
			// this should only happen when the end of file is reached in the middle of a tag.
			// we don't have to do anything to handle it as there will be no more tags anyway.
		}
		return null;
	}

	static EndTag getPrevious(final Source source, int pos) {
		while (true) {
			final Tag tag=Tag.getPreviousTag(source,pos);
			if (tag==null) return null;
			if (tag instanceof EndTag) return (EndTag)tag;
			pos-=1;
		}
	}

	static EndTag getNext(final Source source, int pos) {
		while (true) {
			final Tag tag=Tag.getNextTag(source,pos);
			if (tag==null) return null;
			if (tag instanceof EndTag) return (EndTag)tag;
			pos+=1;
		}
	}
}

