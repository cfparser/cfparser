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
 * Provides a generic implementation of the abstract {@link EndTagType} class based on the most common end tag behaviour.
 * <p>
 * This class is only of interest to users who wish to create <a href="TagType.html#Custom">custom tag types</a>.
 * <p>
 * The differences between this class and its abstract superclass {@link EndTagType} are:
 * <ul>
 *  <li>The introduction of the {@link #isStatic() IsStatic} property.
 *  <li>The {@link #constructTagAt(Source, int pos)} method has a default implementation.
 * </ul>
 * <p>
 * Most of the <a href="Tag.html#Predefined">predefined</a> end tag types are implemented using this class or a subclass of it.
 *
 * @see StartTagTypeGenericImplementation
 */
public class EndTagTypeGenericImplementation extends EndTagType {
	private final String staticString;

	/**
	 * Constructs a new <code>EndTagTypeGenericImplementation</code> object based on the specified properties.
	 * <br />(<a href="TagType.html#ImplementationAssistance">implementation assistance</a> method)
	 * <p>
	 * The purpose of the <code>isStatic</code> parameter is explained in the {@link #isStatic() IsStatic} property description.
	 *
	 * @param description  a {@linkplain #getDescription() description} of the new end tag type useful for debugging purposes.
	 * @param startDelimiter  the {@linkplain #getStartDelimiter() start delimiter} of the new end tag type.
	 * @param closingDelimiter  the {@linkplain #getClosingDelimiter() closing delimiter} of the new end tag type.
	 * @param isServerTag  indicates whether the new end tag type is a {@linkplain #isServerTag() server tag}.
	 * @param isStatic  determines whether the end tag text {@linkplain #isStatic() is static}.
	 */
	protected EndTagTypeGenericImplementation(final String description, final String startDelimiter, final String closingDelimiter, final boolean isServerTag, final boolean isStatic) {
		super(description,startDelimiter,closingDelimiter,isServerTag);
		staticString=isStatic ? (startDelimiter+closingDelimiter) : null;
	}
	
	/**
	 * Indicates whether the {@linkplain #generateHTML(String) end tag text} is static.
	 * <br />(<a href="TagType.html#Property">property</a> and <a href="#ImplementationAssistance">implementation assistance</a> method)
	 * <p>
	 * The purpose of this property is to determine the behaviour of the {@link #generateHTML(String startTagName)} method.
	 * <p>
	 * If this property is <code>true</code>, the {@linkplain #generateHTML(String) end tag text} is constant for all tags of this type.
	 * <p>
	 * If this property is <code>false</code>, the {@linkplain #generateHTML(String) end tag text} includes the
	 * {@linkplain StartTag#getName() name} of the {@linkplain #getCorrespondingStartTagType corresponding} 
	 * {@linkplain StartTag start tag}.
	 * <p>
	 * {@link MasonTagTypes#MASON_COMPONENT_CALLED_WITH_CONTENT_END} is the only <a href="TagType.html#Predefined">predefined</a> end tag
	 * for which this property is <code>true</code>.
	 * All tags of this type have the constant tag text "<code>&lt;/&amp;&gt;</code>".
	 *
	 * @return <code>true</code> if the {@linkplain #generateHTML(String) end tag text} is static, otherwise <code>false</code>.
	 */
	protected final boolean isStatic() {
		return staticString!=null;
	}

	/**
	 * Returns the end tag {@linkplain EndTag#getName() name} that is required to match a {@linkplain #getCorrespondingStartTagType() corresponding} {@linkplain StartTag start tag} with the specified {@linkplain StartTag#getName() name}.
	 * <br />(<a href="TagType.html#Property">property</a> method)
	 * <p>
	 * This implementation overrides the default implementation in {@link EndTagType#getEndTagName(String startTagName)}.
	 * <p>
	 * If the value of the {@link #isStatic() IsStatic} property is <code>false</code>, it returns simply returns <code>startTagName</code>, as in the default implementation.
	 * <p>
	 * If the value of the {@link #isStatic() IsStatic} property is <code>true</code>, it returns this end tag type's {@linkplain #getNamePrefix() name prefix}.
	 * <p>
	 * Note that the <code>startTagName</code> parameter should include the start tag's {@linkplain TagType#getNamePrefix() name prefix} if it has one.
	 *
	 * @param startTagName  the {@linkplain StartTag#getName() name} of a {@linkplain #getCorrespondingStartTagType() corresponding} {@linkplain StartTag start tag}, including its {@linkplain TagType#getNamePrefix() name prefix}.
	 * @return the end tag {@linkplain EndTag#getName() name} that is required to match a {@linkplain #getCorrespondingStartTagType() corresponding} {@linkplain StartTag start tag} with the specified {@linkplain StartTag#getName() name}.
	 */
	public String getEndTagName(final String startTagName) {
		return isStatic() ? getNamePrefix() : startTagName;
	}

	/**
	 * Generates the HTML text of an {@linkplain EndTag end tag} of this type given the {@linkplain StartTag#getName() name} of a {@linkplain #getCorrespondingStartTagType() corresponding} {@linkplain StartTag start tag}.
	 * <br />(<a href="TagType.html#Property">property</a> method)
	 * <p>
	 * This implementation overrides the default implementation in {@link EndTagType#generateHTML(String startTagName)}
	 * to improve efficiency in the case of a {@linkplain #isStatic() static} end tag type, although the functionality is the same.
	 *
	 * @param startTagName  the {@linkplain StartTag#getName() name} of a {@linkplain #getCorrespondingStartTagType() corresponding} {@linkplain StartTag start tag}, including its {@linkplain TagType#getNamePrefix() name prefix}.
	 * @return the HTML text of an {@linkplain EndTag end tag} of this type given the {@linkplain StartTag#getName() name} of a {@linkplain #getCorrespondingStartTagType() corresponding} {@linkplain StartTag start tag}.
	 */
	public String generateHTML(final String startTagName) {
		return isStatic() ? staticString : super.generateHTML(startTagName);
	}

	/**
	 * Constructs a tag of this type at the specified position in the specified source document if it matches all of the required features.
	 * <br />(<a href="TagType.html#DefaultImplementation">default implementation</a> method)
	 * <p>
	 * This default implementation checks the source text for a match according to the following criteria:
	 * <p>
	 * If the value of the {@link #isStatic() IsStatic} property is <code>false</code>, this implementation ensures that the
	 * source text matches the expression:<br />
	 * {@link #getStartDelimiter() getStartDelimiter()}<code>+"<var>name</var>"+<var>optionalWhiteSpace</var>+</code>{@link #getClosingDelimiter() getClosingDelimiter()}<br />
	 * where <var>name</var> is a valid {@linkplain Tag#isXMLName(CharSequence) XML tag name}, and <var>optionalWhiteSpace</var> is a string of zero or more {@linkplain Segment#isWhiteSpace(char) white space} characters.
	 * The {@linkplain Tag#getName() name} of the constructed end tag becomes {@link #getNamePrefix() getNamePrefix()}<code>+"<var>name</var>"</code>.
	 * <p>
	 * If the value of the {@link #isStatic() IsStatic} property is <code>true</code>, this implementation ensures that the 
	 * source text matches the static expression:<br />
	 * {@link #getStartDelimiter() getStartDelimiter()}<code>+</code>{@link #getClosingDelimiter() getClosingDelimiter()}<br />
	 * The {@linkplain Tag#getName() name} of the constructed end tag is the value of the {@link #getNamePrefix() getNamePrefix()} method.
	 * <p>
	 * See {@link TagType#constructTagAt(Source, int pos)} for more important information about this method.
	 *
	 * @param source  the {@link Source} document.
	 * @param pos  the position in the source document.
	 * @return a tag of this type at the specified position in the specified source document if it meets all of the required features, or <code>null</code> if it does not meet the criteria.
	 */
	protected Tag constructTagAt(final Source source, final int pos) {
		final ParseText parseText=source.getParseText();
		final int nameBegin=pos+START_DELIMITER_PREFIX.length();
		String name=null;
		final int startDelimiterEnd=pos+getStartDelimiter().length();
		int end=-1;
		if (isStatic()) {
			name=getNamePrefix();
			if (!parseText.containsAt(getClosingDelimiter(),startDelimiterEnd)) {
				if (source.logger.isInfoEnabled()) source.logger.info(source.getRowColumnVector(pos).appendTo(new StringBuilder(200).append("EndTag of expected format ").append(staticString).append(" at ")).append(" not recognised as type '").append(getDescription()).append("' because it is missing the closing delimiter").toString());
				return null;
			}
			end=startDelimiterEnd+getClosingDelimiter().length();
		} else {
			final int nameEnd=source.getNameEnd(startDelimiterEnd);
			if (nameEnd==-1) return null;
			name=source.getName(nameBegin,nameEnd);
			int expectedClosingDelimiterPos=nameEnd;
			while (Segment.isWhiteSpace(parseText.charAt(expectedClosingDelimiterPos))) expectedClosingDelimiterPos++;
			if (!parseText.containsAt(getClosingDelimiter(),expectedClosingDelimiterPos)) {
				if (source.logger.isInfoEnabled()) source.logger.info(source.getRowColumnVector(pos).appendTo(new StringBuilder(200).append("EndTag ").append(name).append(" at ")).append(" not recognised as type '").append(getDescription()).append("' because its name and closing delimiter are separated by characters other than white space").toString());
				return null;
			}
			end=expectedClosingDelimiterPos+getClosingDelimiter().length();
		}
		return constructEndTag(source,pos,end,name);
	}
}
