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
 * Defines the syntax for an end tag type.
 * <p>
 * An end tag type is a {@link TagType} that {@linkplain #getStartDelimiter() starts} with the characters '<code>&lt;/</code>'.
 * <p>
 * The singleton instances of all the <a href="TagType.html#Standard">standard</a> end tag types are available in this class as static
 * <a href="#field_summary">fields</a>.
 * <p>
 * Because all <code>EndTagType</code> instaces must be singletons, the '<code>==</code>' operator can be used to test for a particular tag type
 * instead of the <code>equals(Object)</code> method.
 *
 * @see StartTagType
 */
public abstract class EndTagType extends TagType {
	static final String START_DELIMITER_PREFIX="</";

	/**
	 * The tag type given to an {@linkplain Tag#isUnregistered() unregistered} {@linkplain EndTag end tag} (<code>&lt;/<var> &#46;&#46;&#46; </var>&gt;</code>).
	 * <p>
	 * See the documentation of the {@link Tag#isUnregistered()} method for details.
	 * <p>
	 * <dl>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Property/Method<th>Value
	 *     <tr><td>{@link #getDescription() Description}<td>/unregistered
	 *     <tr><td>{@link #getStartDelimiter() StartDelimiter}<td><code>&lt;/</code>
	 *     <tr><td>{@link #getClosingDelimiter() ClosingDelimiter}<td><code>&gt;</code>
	 *     <tr><td>{@link #isServerTag() IsServerTag}<td><code>false</code>
	 *     <tr><td>{@link #getNamePrefix() NamePrefix}<td><i>(empty string)</i>
	 *     <tr><td>{@link #getCorrespondingStartTagType() CorrespondingStartTagType}<td><code>null</code>
	 *     <tr><td>{@link #generateHTML(String) generateHTML}<code>("<var>StartTagName</var>")</code><td><code>&lt;/<var>StartTagName</var>&gt;</code>
	 *    </table>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;/ "This is not recognised as any of the predefined end tag types in this library"&gt;</code></dd>
	 * </dl>
	 * @see StartTagType#UNREGISTERED
	 */
	public static final EndTagType UNREGISTERED=EndTagTypeUnregistered.INSTANCE;

	/**
	 * The tag type given to a normal HTML or XML {@linkplain EndTag end tag} (<code>&lt;/<var>name</var>&gt;</code>).
	 * <p>
	 * <dl>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Property/Method<th>Value
	 *     <tr><td>{@link #getDescription() Description}<td>/normal
	 *     <tr><td>{@link #getStartDelimiter() StartDelimiter}<td><code>&lt;/</code>
	 *     <tr><td>{@link #getClosingDelimiter() ClosingDelimiter}<td><code>&gt;</code>
	 *     <tr><td>{@link #isServerTag() IsServerTag}<td><code>false</code>
	 *     <tr><td>{@link #getNamePrefix() NamePrefix}<td><i>(empty string)</i>
	 *     <tr><td>{@link #getCorrespondingStartTagType() CorrespondingStartTagType}<td>{@link StartTagType#NORMAL}
	 *     <tr><td>{@link #generateHTML(String) generateHTML}<code>("<var>StartTagName</var>")</code><td><code>&lt;/<var>StartTagName</var>&gt;</code>
	 *    </table>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;/div&gt;</code></dd>
	 * </dl>
	 */
	public static final EndTagType NORMAL=EndTagTypeNormal.INSTANCE;

	/**
	 * Constructs a new <code>EndTagType</code> object with the specified properties.
	 * <br />(<a href="TagType.html#ImplementationAssistance">implementation assistance</a> method)
	 * <p>
	 * As <code>EndTagType</code> is an abstract class, this constructor is only called from sub-class constructors.
	 *
	 * @param description  a {@linkplain #getDescription() description} of the new end tag type useful for debugging purposes.
	 * @param startDelimiter  the {@linkplain #getStartDelimiter() start delimiter} of the new end tag type.
	 * @param closingDelimiter  the {@linkplain #getClosingDelimiter() closing delimiter} of the new end tag type.
	 * @param isServerTag  indicates whether the new end tag type is a {@linkplain #isServerTag() server tag}.
	 */
	protected EndTagType(final String description, final String startDelimiter, final String closingDelimiter, final boolean isServerTag) {
		super(description,startDelimiter.toLowerCase(),closingDelimiter,isServerTag,START_DELIMITER_PREFIX);
		if (!getStartDelimiter().startsWith(START_DELIMITER_PREFIX)) throw new IllegalArgumentException("startDelimiter of an end tag must start with \""+START_DELIMITER_PREFIX+'"');
	}

	/**
	 * Returns the {@linkplain StartTagType type} of {@linkplain StartTag start tag} that is <i>usually</i> paired with an 
	 * {@linkplain EndTag end tag} of this type to form an {@link Element}.
	 * <br />(<a href="TagType.html#DefaultImplementation">default implementation</a> method)
	 * <p>
	 * The default implementation returns <code>null</code>.
	 * <p>
	 * This property is informational only and is not used by the parser in any way.
	 * <p>
	 * The mapping of end tag type to the corresponding start tag type is in any case one-to-many, which is why the definition
	 * emphasises the word "usually".
	 * An example of this is the {@link PHPTagTypes#PHP_SCRIPT} start tag type,
	 * whose {@linkplain StartTagType#getCorrespondingEndTagType() corresponding end tag type} is {@link #NORMAL EndTagType.NORMAL},
	 * while the converse is not true.
	 * <p>
	 * The only <a href="TagType.html#Predefined">predefined</a> end tag type that returns <code>null</code> for this property is the
	 * special {@link #UNREGISTERED} end tag type.
	 * <p>
	 * Although this method is used like a <a href="TagType.html#Property">property</a> method, it is implemented as a
	 * <a href="TagType.html#DefaultImplementation">default implementation</a> method to avoid cyclic references between statically
	 * instantiated {@link StartTagType} and <code>EndTagType</code> objects.
	 * <p>
	 * <dl>
	 *  <dt>Standard Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>End Tag Type<th>Corresponding Start Tag Type
	 *     <tr><td>{@link EndTagType#UNREGISTERED}<td><code>null</code>
	 *     <tr><td>{@link EndTagType#NORMAL}<td>{@link StartTagType#NORMAL}
	 *    </table>
	 * </dl>
	 * <dl>
	 *  <dt>Extended Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>End Tag Type<th>Corresponding Start Tag Type
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALLED_WITH_CONTENT_END}<td>{@link MasonTagTypes#MASON_COMPONENT_CALLED_WITH_CONTENT}
	 *     <tr><td>{@link MasonTagTypes#MASON_NAMED_BLOCK_END}<td>{@link MasonTagTypes#MASON_NAMED_BLOCK}
	 *    </table>
	 * </dl>
	 *
	 * @return the {@linkplain StartTagType type} of {@linkplain StartTag start tag} that is <i>usually</i> paired with an {@linkplain EndTag end tag} of this type to form an {@link Element}.
	 * @see StartTagType#getCorrespondingEndTagType()
	 */
	public StartTagType getCorrespondingStartTagType() {
		return null;
	}

	/**
	 * Returns the end tag {@linkplain EndTag#getName() name} that is required to match a {@linkplain #getCorrespondingStartTagType() corresponding} {@linkplain StartTag start tag} with the specified {@linkplain StartTag#getName() name}.
	 * <br />(<a href="TagType.html#Property">property</a> method)
	 * <p>
	 * This default implementation simply returns <code>startTagName</code>.
	 * <p>
	 * Note that the <code>startTagName</code> parameter should include the start tag's {@linkplain TagType#getNamePrefix() name prefix} if it has one.
	 *
	 * @param startTagName  the {@linkplain StartTag#getName() name} of a {@linkplain #getCorrespondingStartTagType() corresponding} {@linkplain StartTag start tag}, including its {@linkplain TagType#getNamePrefix() name prefix}.
	 * @return the end tag {@linkplain EndTag#getName() name} that is required to match a {@linkplain #getCorrespondingStartTagType() corresponding} {@linkplain StartTag start tag} with the specified {@linkplain StartTag#getName() name}.
	 */
	public String getEndTagName(final String startTagName) {
		return startTagName;
	}

	/**
	 * Generates the HTML text of an {@linkplain EndTag end tag} of this type given the {@linkplain StartTag#getName() name} of a {@linkplain #getCorrespondingStartTagType() corresponding} {@linkplain StartTag start tag}.
	 * <br />(<a href="TagType.html#Property">property</a> method)
	 * <p>
	 * This default implementation returns <code>"&lt;/"+</code>{@link #getEndTagName(String) getEndTagName}<code>(startTagName)+</code>{@link #getClosingDelimiter()}.
	 * <p>
	 * Note that the <code>startTagName</code> parameter should include the start tag's {@linkplain TagType#getNamePrefix() name prefix} if it has one.
	 *
	 * @param startTagName  the {@linkplain StartTag#getName() name} of a {@linkplain #getCorrespondingStartTagType() corresponding} {@linkplain StartTag start tag}, including its {@linkplain TagType#getNamePrefix() name prefix}.
	 * @return the HTML text of an {@linkplain EndTag end tag} of this type given the {@linkplain StartTag#getName() name} of a {@linkplain #getCorrespondingStartTagType() corresponding} {@linkplain StartTag start tag}.
	 */
	public String generateHTML(final String startTagName) {
		return START_DELIMITER_PREFIX+getEndTagName(startTagName)+getClosingDelimiter();
	}
	
	/**
	 * Internal method for the construction of an {@link EndTag} object of this type.
	 * <br />(<a href="TagType.html#ImplementationAssistance">implementation assistance</a> method)
	 * <p>
	 * Intended for use from within the {@link #constructTagAt(Source,int) constructTagAt(Source, int pos)} method.
	 *
	 * @param source  the {@link Source} document.
	 * @param begin  the character position in the source document where this tag {@linkplain Segment#getBegin() begins}.
	 * @param end  the character position in the source document where this tag {@linkplain Segment#getEnd() ends}.
	 * @param name  the {@linkplain Tag#getName() name} of the tag.
	 * @return the new {@link EndTag} object.
	 */
	protected final EndTag constructEndTag(final Source source, final int begin, final int end, final String name) {
		return new EndTag(source,begin,end,this,name);
	}
}
