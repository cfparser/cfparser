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
 * Provides a generic implementation of the abstract {@link StartTagType} class based on the most common start tag behaviour.
 * <p>
 * This class is only of interest to users who wish to create <a href="TagType.html#Custom">custom tag types</a>.
 * <p>
 * The only external difference between this class and its abstract superclass {@link StartTagType} is that it provides a default
 * implementation of the {@link #constructTagAt(Source, int pos)} method.
 * <p>
 * Most of the <a href="Tag.html#Predefined">predefined</a> start tag types are implemented using this class or a subclass of it.
 *
 * @see EndTagTypeGenericImplementation
 */
public class StartTagTypeGenericImplementation extends StartTagType {
	final boolean nameCharAfterPrefixAllowed;

	/**
	 * Constructs a new <code>StartTagTypeGenericImplementation</code> object with the specified properties.
	 * <br />(<a href="TagType.html#ImplementationAssistance">implementation assistance</a> method)
	 * <p>
	 * This is equivalent to calling
	 * <br /><code>new&nbsp;</code>{@link #StartTagTypeGenericImplementation(String,String,String,EndTagType,boolean,boolean,boolean) StartTagTypeGenericImplementation}<code>(description,startDelimiter,closingDelimiter,correspondingEndTagType,isServerTag,false,false)</code>.
	 *
	 * @param description  a {@linkplain #getDescription() description} of the new start tag type useful for debugging purposes.
	 * @param startDelimiter  the {@linkplain #getStartDelimiter() start delimiter} of the new start tag type.
	 * @param closingDelimiter  the {@linkplain #getClosingDelimiter() closing delimiter} of the new start tag type.
	 * @param correspondingEndTagType  the {@linkplain #getCorrespondingEndTagType() corresponding end tag type} of the new start tag type.
	 * @param isServerTag  indicates whether the new start tag type is a {@linkplain #isServerTag() server tag}.
	 */
	protected StartTagTypeGenericImplementation(final String description, final String startDelimiter, final String closingDelimiter, final EndTagType correspondingEndTagType, final boolean isServerTag) {
		this(description,startDelimiter,closingDelimiter,correspondingEndTagType,isServerTag,false,false);
	}

	/**
	 * Constructs a new <code>StartTagTypeGenericImplementation</code> object with the specified properties.
	 * <br />(<a href="TagType.html#ImplementationAssistance">implementation assistance</a> method)
	 *
	 * @param description  a {@linkplain #getDescription() description} of the new start tag type useful for debugging purposes.
	 * @param startDelimiter  the {@linkplain #getStartDelimiter() start delimiter} of the new start tag type.
	 * @param closingDelimiter  the {@linkplain #getClosingDelimiter() closing delimiter} of the new start tag type.
	 * @param correspondingEndTagType  the {@linkplain #getCorrespondingEndTagType() corresponding end tag type} of the new start tag type.
	 * @param isServerTag  indicates whether the new start tag type is a {@linkplain #isServerTag() server tag}.
	 * @param hasAttributes  indicates whether the new start tag type {@linkplain #hasAttributes() has attributes}.
	 * @param isNameAfterPrefixRequired  indicates whether a {@linkplain #isNameAfterPrefixRequired() name is required after the prefix}.
	 */
	protected StartTagTypeGenericImplementation(final String description, final String startDelimiter, final String closingDelimiter, final EndTagType correspondingEndTagType, final boolean isServerTag, final boolean hasAttributes, final boolean isNameAfterPrefixRequired) {
		super(description,startDelimiter,closingDelimiter,correspondingEndTagType,isServerTag,hasAttributes,isNameAfterPrefixRequired);
		nameCharAfterPrefixAllowed=(getNamePrefix().length()==0 || !Character.isLetter(getNamePrefix().charAt(getNamePrefix().length()-1)));
	}

	/**
	 * Constructs a tag of this type at the specified position in the specified source document if it matches all of the required features.
	 * <br />(<a href="TagType.html#DefaultImplementation">default implementation</a> method)
	 * <p>
	 * This default implementation performs the following steps:
	 * <ol class="Separated">
	 *  <li>
	 *   If a {@linkplain #isNameAfterPrefixRequired() name is required after the prefix}, search for a valid
	 *   {@linkplain Tag#isXMLName(CharSequence) XML tag name} directly after the
	 *   {@linkplain #getNamePrefix() name prefix} using the {@link Source#getNameEnd(int pos)} method.
	 *   If one is found, set the {@linkplain Tag#getName() name} to include it, otherwise return <code>null</code>.
	 *  <li>
	 *   If the last character of the {@linkplain #getNamePrefix() name prefix} is a letter
	 *   (indicating that the prefix includes the full {@linkplain Tag#getName() name} of the tag),
	 *   and the character following the prefix in the source text is also a letter
	 *   or any other valid {@linkplain Tag#isXMLNameChar(char) XML name character},
	 *   return <code>null</code>.
	 *   <br />Example: the source text "<code>&lt;?xmlt ?&gt;</code>" should not be recognised as an
	 *   {@linkplain #XML_PROCESSING_INSTRUCTION XML processing instruction}, which has the prefix "<code>&lt;?xml</code>".
	 *  <li>
	 *   If the tag type {@linkplain #hasAttributes() has attributes}, call
	 *   {@link #parseAttributes(Source,int,String) parseAttributes(source,pos,name)} to parse them.
	 *   Return <code>null</code> if too many errors occur while parsing the attributes.
	 *  <li>
	 *   Find the {@linkplain Tag#getEnd() end} of the tag using the {@link #getEnd(Source, int pos)} method,
	 *   where <code>pos</code> is either the end of the {@linkplain StartTag#getAttributes() attributes} segment or the end of the
	 *   {@linkplain Tag#getName() name} depending on whether the tag type {@linkplain #hasAttributes() has attributes}.
	 *   Return <code>null</code> if the end of the tag can not be found.
	 *  <li>
	 *   Construct the {@link StartTag} object using the
	 *   {@link #constructStartTag(Source,int,int,String,Attributes) constructStartTag(Source, int pos, int end, String name, Attributes)}
	 *   method with the argument values collected over the previous steps.
	 * </ol>
	 * <p> 
	 * See {@link TagType#constructTagAt(Source, int pos)} for more important information about this method.
	 *
	 * @param source  the {@link Source} document.
	 * @param pos  the position in the source document.
	 * @return a tag of this type at the specified position in the specified source document if it meets all of the required features, or <code>null</code> if it does not meet the criteria.
	 */
	protected Tag constructTagAt(final Source source, final int pos) {
		final ParseText parseText=source.getParseText();
		final int nameBegin=pos+1;
		String name=getNamePrefix();
		int nameEnd=nameBegin+getNamePrefix().length();
		if (isNameAfterPrefixRequired()) {
			final int extendedNameEnd=source.getNameEnd(nameEnd);
			if (extendedNameEnd==-1) return null;
			name=source.getName(nameBegin,extendedNameEnd);
			nameEnd=extendedNameEnd;
		} else if (!nameCharAfterPrefixAllowed && Tag.isXMLNameChar(parseText.charAt(nameEnd))) {
			return null;
		}
		int end;
		Attributes attributes=null;
		if (hasAttributes()) {
			// it is necessary to get the attributes so that we can be sure that the search on the closing delimiter doesn't pick up
			// anything from the attribute values, which can legally contain ">" characters.
			attributes=parseAttributes(source,pos,name);
			if (attributes==null) return null; // happens if attributes not properly formed
			end=getEnd(source,attributes.getEnd()); // should always return a valid end
		} else {
			end=getEnd(source,nameEnd);
			if (end==-1) {
				if (source.logger.isInfoEnabled()) source.logger.info(source.getRowColumnVector(pos).appendTo(new StringBuilder(200).append("StartTag ").append(name).append(" at ")).append(" not recognised as type '").append(getDescription()).append("' because it has no closing delimiter").toString());
				return null;
			}
		}
		return constructStartTag(source,pos,end,name,attributes);
	}

	/**
	 * Returns the {@linkplain Tag#getEnd() end} of a tag of this type, starting from the specified position in the specified source document.
	 * <br />(<a href="TagType.html#ImplementationAssistance">implementation assistance</a> method)
	 * <p>
	 * This default implementation simply searches for the first occurrence of the
	 * {@linkplain #getClosingDelimiter() closing delimiter} after the specified position, and returns the position immediately
	 * after the end of it.
	 * <p>
	 * If the closing delimiter is not found, the value <code>-1</code> is returned.
	 *
	 * @param source  the {@link Source} document.
	 * @param pos  the position in the source document.
	 * @return the {@linkplain Tag#getEnd() end} of a tag of this type, starting from the specified position in the specified source document, or <code>-1</code> if the end of the tag can not be found.
	 */
	protected int getEnd(final Source source, final int pos) {
		final int delimiterBegin=source.getParseText().indexOf(getClosingDelimiter(),pos);
		return (delimiterBegin==-1 ? -1 : delimiterBegin+getClosingDelimiter().length());
	}
}
