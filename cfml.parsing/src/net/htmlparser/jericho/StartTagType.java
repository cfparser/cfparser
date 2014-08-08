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
 * Defines the syntax for a start tag type.
 * <p>
 * A start tag type is any {@link TagType} that {@linkplain #getStartDelimiter() starts} with the character '<code>&lt;</code>'
 * (as with all tag types), but whose second character is <b>not</b> '<code>/</code>'.
 * <p>
 * This includes types for many tags which stand alone, without a {@linkplain #getCorrespondingEndTagType() corresponding end tag},
 * and would not intuitively be categorised as a "start tag".  For example, an HTML {@linkplain #COMMENT comment} in a document
 * is represented as a single start tag that spans the whole comment, and does not have an end tag at all.
 * <p>
 * The singleton instances of all the <a href="TagType.html#Standard">standard</a> start tag types are available in this class as static
 * <a href="#field_summary">fields</a>.
 * <p>
 * Because all <code>StartTagType</code> instaces must be singletons, the '<code>==</code>' operator can be used to test for a particular tag type
 * instead of the <code>equals(Object)</code> method.
 *
 * @see EndTagType
 */
public abstract class StartTagType extends TagType {
	private final EndTagType correspondingEndTagType;
	private final boolean hasAttributes;
	private final boolean isNameAfterPrefixRequired;

	static final String START_DELIMITER_PREFIX="<";

	/**
	 * The tag type given to an {@linkplain Tag#isUnregistered() unregistered} {@linkplain StartTag start tag}
	 * (<code>&lt;<var> &#46;&#46;&#46; </var>&gt;</code>).
	 * <p>
	 * See the documentation of the {@link Tag#isUnregistered()} method for details.
	 * <p>
	 * <dl>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Property<th>Value
	 *     <tr><td>{@link #getDescription() Description}<td>unregistered
	 *     <tr><td>{@link #getStartDelimiter() StartDelimiter}<td><code>&lt;</code>
	 *     <tr><td>{@link #getClosingDelimiter() ClosingDelimiter}<td><code>&gt;</code>
	 *     <tr><td>{@link #isServerTag() IsServerTag}<td><code>false</code>
	 *     <tr><td>{@link #getNamePrefix() NamePrefix}<td><i>(empty string)</i>
	 *     <tr><td>{@link #getCorrespondingEndTagType() CorrespondingEndTagType}<td><code>null</code>
	 *     <tr><td>{@link #hasAttributes() HasAttributes}<td><code>false</code>
	 *     <tr><td>{@link #isNameAfterPrefixRequired() IsNameAfterPrefixRequired}<td><code>false</code>
	 *    </table>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;"This is not recognised as any of the predefined tag types in this library"&gt;</code></dd>
	 * </dl>
	 * @see EndTagType#UNREGISTERED
	 */
	public static final StartTagType UNREGISTERED=StartTagTypeUnregistered.INSTANCE;

	/**
	 * The tag type given to a normal HTML or XML {@linkplain StartTag start tag}
	 * (<code>&lt;<var>name</var><var> &#46;&#46;&#46; </var>&gt;</code>).
	 * <p>
	 * <dl>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Property<th>Value
	 *     <tr><td>{@link #getDescription() Description}<td>normal
	 *     <tr><td>{@link #getStartDelimiter() StartDelimiter}<td><code>&lt;</code>
	 *     <tr><td>{@link #getClosingDelimiter() ClosingDelimiter}<td><code>&gt;</code>
	 *     <tr><td>{@link #isServerTag() IsServerTag}<td><code>false</code>
	 *     <tr><td>{@link #getNamePrefix() NamePrefix}<td><i>(empty string)</i>
	 *     <tr><td>{@link #getCorrespondingEndTagType() CorrespondingEndTagType}<td>{@link EndTagType#NORMAL}
	 *     <tr><td>{@link #hasAttributes() HasAttributes}<td><code>true</code>
	 *     <tr><td>{@link #isNameAfterPrefixRequired() IsNameAfterPrefixRequired}<td><code>true</code>
	 *    </table>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;div class="NormalDivTag"&gt;</code></dd>
	 * </dl>
	 */
	public static final StartTagType NORMAL=StartTagTypeNormal.INSTANCE;

	/**
	 * The tag type given to an HTML <a target="_blank" href="http://www.w3.org/TR/html401/intro/sgmltut.html#h-3.2.4">comment</a>
	 * (<code>&lt;&#33;--<var> &#46;&#46;&#46; </var>--&gt;</code>).
	 * <p>
	 * An HTML comment is an area of the source document enclosed by the delimiters
	 * <code>&lt;!--</code> on the left and <code>--&gt;</code> on the right.
	 * <p>
	 * The <a target="_blank" href="http://www.w3.org/TR/html401/intro/sgmltut.html#h-3.2.4">HTML 4.01 specification section 3.2.4</a>
	 * states that the end of comment delimiter may contain white space between the "<code>--</code>" and "<code>&gt;</code>" characters,
	 * but this library does not recognise end of comment delimiters containing white space.
	 * <p>
	 * In the default configuration, any non-{@linkplain #isServerTag() server} tag appearing within an HTML comment is ignored
	 * by the parser.
	 * See the documentation of the <a href="Tag.html#ParsingProcess">tag parsing process</a> for more information.
	 * <p>
	 * <dl>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Property<th>Value
	 *     <tr><td>{@link #getDescription() Description}<td>comment
	 *     <tr><td>{@link #getStartDelimiter() StartDelimiter}<td><code>&lt;!--</code>
	 *     <tr><td>{@link #getClosingDelimiter() ClosingDelimiter}<td><code>--&gt;</code>
	 *     <tr><td>{@link #isServerTag() IsServerTag}<td><code>false</code>
	 *     <tr><td>{@link #getNamePrefix() NamePrefix}<td><code>!--</code>
	 *     <tr><td>{@link #getCorrespondingEndTagType() CorrespondingEndTagType}<td><code>null</code>
	 *     <tr><td>{@link #hasAttributes() HasAttributes}<td><code>false</code>
	 *     <tr><td>{@link #isNameAfterPrefixRequired() IsNameAfterPrefixRequired}<td><code>false</code>
	 *    </table>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;!-- This is a comment --&gt;</code></dd>
	 * </dl>
	 */
	public static final StartTagType COMMENT=StartTagTypeComment.INSTANCE;

	/**
	 * The tag type given to an <a target="_blank" href="http://www.w3.org/TR/REC-xml/#sec-prolog-dtd">XML declaration</a>
	 * (<code>&lt;&#63;xml<var> &#46;&#46;&#46; </var>&#63;&gt;</code>).
	 * <p>
	 * An XML declaration is often referred to in texts as a special type of processing instruction with the reserved
	 * <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-PITarget">PITarget</a> name of "<code>xml</code>".
	 * Technically it is not an {@linkplain #XML_PROCESSING_INSTRUCTION XML processing instruction} at all, but is still a type of
	 * <a target="_blank" href="http://www.w3.org/TR/html401/appendix/notes.html#h-B.3.6">SGML processing instruction</a>.
	 * <p>
	 * According to section <a target="_blank" href="http://www.w3.org/TR/REC-xml/#sec-prolog-dtd">2.8</a> of the XML 1.0 specification,
	 * a valid XML declaration can specify only "version", "encoding" and "standalone" attributes in that order.
	 * This library parses the {@linkplain Attributes attributes} of an XML declaration in the same way as those of a
	 * {@linkplain #NORMAL normal} tag, without checking that they conform to the specification. 
	 * <p>
	 * <dl>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Property<th>Value
	 *     <tr><td>{@link #getDescription() Description}<td>XML declaration
	 *     <tr><td>{@link #getStartDelimiter() StartDelimiter}<td><code>&lt;?xml</code>
	 *     <tr><td>{@link #getClosingDelimiter() ClosingDelimiter}<td><code>?&gt;</code>
	 *     <tr><td>{@link #isServerTag() IsServerTag}<td><code>false</code>
	 *     <tr><td>{@link #getNamePrefix() NamePrefix}<td><code>?xml</code>
	 *     <tr><td>{@link #getCorrespondingEndTagType() CorrespondingEndTagType}<td><code>null</code>
	 *     <tr><td>{@link #hasAttributes() HasAttributes}<td><code>true</code>
	 *     <tr><td>{@link #isNameAfterPrefixRequired() IsNameAfterPrefixRequired}<td><code>false</code>
	 *    </table>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;?xml version="1.0" encoding="UTF-8"?&gt;</code></dd>
	 * </dl>
	 */
	public static final StartTagType XML_DECLARATION=StartTagTypeXMLDeclaration.INSTANCE;

	/**
	 * The tag type given to an <a target="_blank" href="http://www.w3.org/TR/REC-xml#sec-pi">XML processing instruction</a>
	 * (<code>&lt;&#63;<var>PITarget</var><var> &#46;&#46;&#46; </var>&#63;&gt;</code>).
	 * <p>
	 * An XML processing instruction is a specific form of
	 * <a target="_blank" href="http://www.w3.org/TR/html401/appendix/notes.html#h-B.3.6">SGML processing instruction</a> with the following
	 * two additional constraints:
	 * <ul>
	 *  <li>it must be {@linkplain #getClosingDelimiter() closed} with '<code>?&gt;</code>' instead of just a single
	 *  '<code>&gt;</code>' character.
	 *  <li>it requires a <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-PITarget">PITarget</a>
	 *   (essentially a {@linkplain Tag#getName() name} following the '<code>&lt;?</code>' {@linkplain #getStartDelimiter() start delimiter}).
	 * </ul>
	 * <p>
	 * This library does not include a <a href="TagType.html#Predefined">predefined</a> generic tag type for SGML processing instructions
	 * as the only forms in which they are found in HTML documents are the more specific XML processing instruction and
	 * the {@linkplain #XML_DECLARATION XML declaration}, both of which have their own dedicated predefined tag type.
	 * <p>
	 * There is no restriction on the contents of an XML processing instruction.  In particular, it can not be assumed that the
	 * processing instruction contains {@linkplain Attributes attributes}, in contrast to the {@linkplain #XML_DECLARATION XML declaration}.
	 * <p>
	 * Note that {@linkplain #register() registering} the {@link PHPTagTypes#PHP_SHORT} tag type overrides this tag type.
	 * This is because they both have the same {@linkplain #getStartDelimiter start delimiter},
	 * so the one registered latest takes <a href="TagType.html#Precedence">precedence</a> over the other.
	 * See the documentation of the {@link PHPTagTypes} class for more information.
	 * <p>
	 * <dl>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Property<th>Value
	 *     <tr><td>{@link #getDescription() Description}<td>XML processing instruction
	 *     <tr><td>{@link #getStartDelimiter() StartDelimiter}<td><code>&lt;?</code>
	 *     <tr><td>{@link #getClosingDelimiter() ClosingDelimiter}<td><code>?&gt;</code>
	 *     <tr><td>{@link #isServerTag() IsServerTag}<td><code>false</code>
	 *     <tr><td>{@link #getNamePrefix() NamePrefix}<td><code>?</code>
	 *     <tr><td>{@link #getCorrespondingEndTagType() CorrespondingEndTagType}<td><code>null</code>
	 *     <tr><td>{@link #hasAttributes() HasAttributes}<td><code>false</code>
	 *     <tr><td>{@link #isNameAfterPrefixRequired() IsNameAfterPrefixRequired}<td><code>true</code>
	 *    </table>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;?xml-stylesheet href="standardstyle.css" type="text/css"?&gt;</code></dd>
	 * </dl>
	 */
	public static final StartTagType XML_PROCESSING_INSTRUCTION=StartTagTypeXMLProcessingInstruction.INSTANCE;

	/**
	 * The tag type given to a <a target="_blank" href="http://www.w3.org/TR/html401/struct/global.html#h-7.2">document type declaration</a>
	 * (<code>&lt;&#33;DOCTYPE<var> &#46;&#46;&#46; </var>&gt;</code>).
	 * <p>
	 * Information about the document type declaration can be found in the
	 * <a target="_blank" href="http://www.w3.org/TR/html401/struct/global.html#h-7.2">HTML 4.01 specification section 7.2</a>, and the
	 * <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-doctype">XML 1.0 specification section 2.8</a>.
	 * <p>
	 * The "<code>!DOCTYPE</code>" tag name is required to be in upper case in the source document,
	 * but all tag properties are stored in lower case because this library performs all parsing in lower case.
	 * <p>
	 * <dl>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Property<th>Value
	 *     <tr><td>{@link #getDescription() Description}<td>document type declaration
	 *     <tr><td>{@link #getStartDelimiter() StartDelimiter}<td><code>&lt;!doctype</code>
	 *     <tr><td>{@link #getClosingDelimiter() ClosingDelimiter}<td><code>&gt;</code>
	 *     <tr><td>{@link #isServerTag() IsServerTag}<td><code>false</code>
	 *     <tr><td>{@link #getNamePrefix() NamePrefix}<td><code>!doctype</code>
	 *     <tr><td>{@link #getCorrespondingEndTagType() CorrespondingEndTagType}<td><code>null</code>
	 *     <tr><td>{@link #hasAttributes() HasAttributes}<td><code>false</code>
	 *     <tr><td>{@link #isNameAfterPrefixRequired() IsNameAfterPrefixRequired}<td><code>false</code>
	 *    </table>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"&gt;</code></dd>
	 * </dl>
	 */
	public static final StartTagType DOCTYPE_DECLARATION=StartTagTypeDoctypeDeclaration.INSTANCE;

	/**
	 * The tag type given to a <a target="_blank" href="http://www.w3.org/TR/REC-xml/#dt-markupdecl">markup declaration</a>
	 * (<code>&lt;&#33;ELEMENT<var> &#46;&#46;&#46; </var>&gt;</code> | <code>&lt;&#33;ATTLIST<var> &#46;&#46;&#46; </var>&gt;</code> | <code>&lt;&#33;ENTITY<var> &#46;&#46;&#46; </var>&gt;</code> | <code>&lt;&#33;NOTATION<var> &#46;&#46;&#46; </var>&gt;</code>).
	 * <p>
	 * The {@linkplain Tag#getName() name} of a markup declaration tag is must be one of
	 * "<code>!element</code>", "<code>!attlist</code>", "<code>!entity</code>" or "<code>!notation</code>".
	 * These tag names are required to be in upper case in the source document,
	 * but all tag properties are stored in lower case because this library performs all parsing in lower case.
	 * <p>
	 * Markup declarations usually appear inside a
	 * <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-doctype">document type definition</a> (DTD), which is usually an external
	 * document to the HTML or XML document, but they can also appear directly within the 
	 * {@linkplain #DOCTYPE_DECLARATION document type declaration} which is why they must be recognised by the parser.
	 * <p>
	 * <dl>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Property<th>Value
	 *     <tr><td>{@link #getDescription() Description}<td>markup declaration
	 *     <tr><td>{@link #getStartDelimiter() StartDelimiter}<td><code>&lt;!</code>
	 *     <tr><td>{@link #getClosingDelimiter() ClosingDelimiter}<td><code>&gt;</code>
	 *     <tr><td>{@link #isServerTag() IsServerTag}<td><code>false</code>
	 *     <tr><td>{@link #getNamePrefix() NamePrefix}<td><code>!</code>
	 *     <tr><td>{@link #getCorrespondingEndTagType() CorrespondingEndTagType}<td><code>null</code>
	 *     <tr><td>{@link #hasAttributes() HasAttributes}<td><code>false</code>
	 *     <tr><td>{@link #isNameAfterPrefixRequired() IsNameAfterPrefixRequired}<td><code>true</code>
	 *    </table>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;!ELEMENT BODY O O (%flow;)* +(INS|DEL) -- document body --&gt;</code></dd>
	 * </dl>
	 */
	public static final StartTagType MARKUP_DECLARATION=StartTagTypeMarkupDeclaration.INSTANCE;

	/**
	 * The tag type given to a <a target="_blank" href="http://www.w3.org/TR/html401/appendix/notes.html#h-B.3.5">CDATA section</a>
	 * (<code>&lt;&#33;[CDATA[<var> &#46;&#46;&#46; </var>]]&gt;</code>).
	 * <p>
	 * A CDATA section is a specific form of a
	 * <a target="_blank" href="http://www.w3.org/TR/html401/appendix/notes.html#h-B.3.5">marked section</a>.
	 * This library does not include a <a href="TagType.html#Predefined">predefined</a> generic tag type for marked sections,
	 * as the only type of marked sections found in HTML documents are CDATA sections.
	 * <p>
	 * The <a target="_blank" href="http://www.w3.org/TR/html401/appendix/notes.html#h-B.3.5">HTML 4.01 specification section B.3.5</a>
	 * and the <a target="_blank" href="http://www.w3.org/TR/REC-xml/#sec-cdata-sect">XML 1.0 specification section 2.7</a>
	 * contain definitions for a CDATA section.
	 * <p>
	 * There is inconsistency between the SGML and HTML/XML specifications in the definition of a marked section.
	 * SGML requires the presence of a space between the "<code>&lt;![</code>" prefix and the keyword, and allows a space after the keyword.
	 * The XML specification forbids these spaces, and the examples given in the HTML specification do not include them either.
	 * This library only recognises CDATA sections that do not include the spaces.
	 * <p>
	 * The "<code>![CDATA[</code>" tag name is required to be in upper case in the source document according to the HTML/XML specifications,
	 * but all tag properties are stored in lower case because this makes it more efficient for the library to perform case-insensitive
	 * parsing of all tags.
	 * <p>
	 * In the default configuration, any non-{@linkplain #isServerTag() server} tag appearing within a CDATA section is ignored
	 * by the parser.
	 * See the documentation of the <a href="Tag.html#ParsingProcess">tag parsing process</a> for more information.
	 * <p>
	 * <dl>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Property<th>Value
	 *     <tr><td>{@link #getDescription() Description}<td>CDATA section
	 *     <tr><td>{@link #getStartDelimiter() StartDelimiter}<td><code>&lt;![cdata[</code>
	 *     <tr><td>{@link #getClosingDelimiter() ClosingDelimiter}<td><code>]]&gt;</code>
	 *     <tr><td>{@link #isServerTag() IsServerTag}<td><code>false</code>
	 *     <tr><td>{@link #getNamePrefix() NamePrefix}<td><code>![cdata[</code>
	 *     <tr><td>{@link #getCorrespondingEndTagType() CorrespondingEndTagType}<td><code>null</code>
	 *     <tr><td>{@link #hasAttributes() HasAttributes}<td><code>false</code>
	 *     <tr><td>{@link #isNameAfterPrefixRequired() IsNameAfterPrefixRequired}<td><code>false</code>
	 *    </table>
	 *  <dt>Example:</dt>
	 *   <dd>This example shows the recommended practice of enclosing scripts inside a CDATA section:
	 *    <div style="margin-top: 0.5em">
	 *     <pre>&lt;script type="text/javascript"&gt;<br />  //&lt;![CDATA[<br />    function min(a,b) {return a&lt;b ? a : b;}<br />  //]]&gt<br />&lt;/script&gt;</pre>
	 *    </div>
	 * </dl>
	 */
	public static final StartTagType CDATA_SECTION=StartTagTypeCDATASection.INSTANCE;

	/**
	 * The tag type given to a common server tag
	 * (<code>&lt;%<var> &#46;&#46;&#46; </var>%&gt;</code>).
	 * <p>
	 * Common server tags include
	 * <a target="_blank" href="http://msdn.microsoft.com/asp/">ASP</a>,
	 * <a target="_blank" href="http://java.sun.com/products/jsp/">JSP</a>,
	 * <a target="_blank" href="http://www.modpython.org/">PSP</a>,
	 * <a target="_blank" href="http://au2.php.net/manual/en/configuration.directives.php#ini.asp-tags">ASP-style PHP</a>,
	 * <a target="_blank" href="http://www.rubycentral.com/book/web.html#S2">eRuby</a>, and
	 * <a target="_blank" href="http://www.masonbook.com/book/chapter-2.mhtml#CHP-2-SECT-3.1">Mason substitution</a> tags.
	 * <p>
	 * This tag and the {@linkplain #SERVER_COMMON_ESCAPED escaped common server tag} are the only <a href="TagType.html#Standard">standard</a> tag types
	 * that define {@linkplain #isServerTag() server tags}.
	 * They are included as standard tag types because of the common server tag's widespread use in many platforms, including those listed above.
	 * <p>
	 * <dl>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Property<th>Value
	 *     <tr><td>{@link #getDescription() Description}<td>common server tag
	 *     <tr><td>{@link #getStartDelimiter() StartDelimiter}<td><code>&lt;%</code>
	 *     <tr><td>{@link #getClosingDelimiter() ClosingDelimiter}<td><code>%&gt;</code>
	 *     <tr><td>{@link #isServerTag() IsServerTag}<td><code>true</code>
	 *     <tr><td>{@link #getNamePrefix() NamePrefix}<td><code>%</code>
	 *     <tr><td>{@link #getCorrespondingEndTagType() CorrespondingEndTagType}<td><code>null</code>
	 *     <tr><td>{@link #hasAttributes() HasAttributes}<td><code>false</code>
	 *     <tr><td>{@link #isNameAfterPrefixRequired() IsNameAfterPrefixRequired}<td><code>false</code>
	 *    </table>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;%@ include file="header.html" %&gt;</code></dd>
	 * </dl>
	 */
	public static final StartTagType SERVER_COMMON=StartTagTypeServerCommon.INSTANCE;

	/**
	 * The tag type given to an escaped common server tag
	 * (<code>&lt;\%<var> &#46;&#46;&#46; </var>%&gt;</code>).
	 * <p>
	 * Some of the platforms that support the {@linkplain #SERVER_COMMON common server tag} also support a mechanism to escape that tag by adding a
	 * backslash (<code>\</code>) before the percent (<code>%</code>) character.
	 * Although rarely used, this tag type allows the parser to recognise these escaped tags in addition to the common server tag itself.
	 * <p>
	 * <dl>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Property<th>Value
	 *     <tr><td>{@link #getDescription() Description}<td>escaped common server tag
	 *     <tr><td>{@link #getStartDelimiter() StartDelimiter}<td><code>&lt;\%</code>
	 *     <tr><td>{@link #getClosingDelimiter() ClosingDelimiter}<td><code>%&gt;</code>
	 *     <tr><td>{@link #isServerTag() IsServerTag}<td><code>true</code>
	 *     <tr><td>{@link #getNamePrefix() NamePrefix}<td><code>\%</code>
	 *     <tr><td>{@link #getCorrespondingEndTagType() CorrespondingEndTagType}<td><code>null</code>
	 *     <tr><td>{@link #hasAttributes() HasAttributes}<td><code>false</code>
	 *     <tr><td>{@link #isNameAfterPrefixRequired() IsNameAfterPrefixRequired}<td><code>false</code>
	 *    </table>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;\%@ include file="header.html" %&gt;</code></dd>
	 * </dl>
	 */
	public static final StartTagType SERVER_COMMON_ESCAPED=StartTagTypeServerCommonEscaped.INSTANCE;

	/**
	 * Constructs a new <code>StartTagType</code> object with the specified properties.
	 * <br />(<a href="TagType.html#ImplementationAssistance">implementation assistance</a> method)
	 * <p>
	 * As <code>StartTagType</code> is an abstract class, this constructor is only called from sub-class constructors.
	 *
	 * @param description  a {@linkplain #getDescription() description} of the new start tag type useful for debugging purposes.
	 * @param startDelimiter  the {@linkplain #getStartDelimiter() start delimiter} of the new start tag type.
	 * @param closingDelimiter  the {@linkplain #getClosingDelimiter() closing delimiter} of the new start tag type.
	 * @param correspondingEndTagType  the {@linkplain #getCorrespondingEndTagType() corresponding end tag type} of the new start tag type.
	 * @param isServerTag  indicates whether the new start tag type is a {@linkplain #isServerTag() server tag}.
	 * @param hasAttributes  indicates whether the new start tag type {@linkplain #hasAttributes() has attributes}.
	 * @param isNameAfterPrefixRequired  indicates whether a {@linkplain #isNameAfterPrefixRequired() name is required after the prefix}.
	 */
	protected StartTagType(final String description, final String startDelimiter, final String closingDelimiter, final EndTagType correspondingEndTagType, final boolean isServerTag, final boolean hasAttributes, final boolean isNameAfterPrefixRequired) {
		super(description,startDelimiter.toLowerCase(),closingDelimiter,isServerTag,START_DELIMITER_PREFIX);
		if (!getStartDelimiter().startsWith(START_DELIMITER_PREFIX)) throw new IllegalArgumentException("startDelimiter of a start tag must start with \""+START_DELIMITER_PREFIX+'"');
		this.correspondingEndTagType=correspondingEndTagType;
		this.hasAttributes=hasAttributes;
		this.isNameAfterPrefixRequired=isNameAfterPrefixRequired;
	}

	/**
	 * Returns the {@linkplain EndTagType type} of {@linkplain EndTag end tag} required to pair with a
	 * {@linkplain StartTag start tag} of this type to form an {@linkplain Element element}.
	 * <br />(<a href="TagType.html#Property">property</a> method)
	 * <p>
	 * This can be represented by the following expression that is always <code>true</code> given an arbitrary {@linkplain Element element}
	 * that has an end tag:
	 * <p>
	 * <code>element.</code>{@link Element#getStartTag() getStartTag()}<code>.</code>{@link StartTag#getStartTagType() getStartTagType()}<code>.</code>{@link #getCorrespondingEndTagType()}<code>==element.</code>{@link Element#getEndTag() getEndTag()}<code>.</code>{@link EndTag#getEndTagType() getEndTagType()}
	 * <p>
	 * <dl>
	 *  <dt>Standard Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Start Tag Type<th>Corresponding End Tag Type
	 *     <tr><td>{@link StartTagType#UNREGISTERED}<td><code>null</code>
	 *     <tr><td>{@link StartTagType#NORMAL}<td>{@link EndTagType#NORMAL}
	 *     <tr><td>{@link StartTagType#COMMENT}<td><code>null</code>
	 *     <tr><td>{@link StartTagType#XML_DECLARATION}<td><code>null</code>
	 *     <tr><td>{@link StartTagType#XML_PROCESSING_INSTRUCTION}<td><code>null</code>
	 *     <tr><td>{@link StartTagType#DOCTYPE_DECLARATION}<td><code>null</code>
	 *     <tr><td>{@link StartTagType#MARKUP_DECLARATION}<td><code>null</code>
	 *     <tr><td>{@link StartTagType#CDATA_SECTION}<td><code>null</code>
	 *     <tr><td>{@link StartTagType#SERVER_COMMON}<td><code>null</code>
	 *     <tr><td>{@link StartTagType#SERVER_COMMON_ESCAPED}<td><code>null</code>
	 *    </table>
	 * </dl>
	 * <dl>
	 *  <dt>Extended Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Start Tag Type<th>Corresponding End Tag Type
	 *     <tr><td>{@link MicrosoftTagTypes#DOWNLEVEL_REVEALED_CONDITIONAL_COMMENT}<td><code>null</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_SCRIPT}<td>{@link EndTagType#NORMAL}
	 *     <tr><td>{@link PHPTagTypes#PHP_SHORT}<td><code>null</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_STANDARD}<td><code>null</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALL}<td><code>null</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALLED_WITH_CONTENT}<td>{@link MasonTagTypes#MASON_COMPONENT_CALLED_WITH_CONTENT_END}
	 *     <tr><td>{@link MasonTagTypes#MASON_NAMED_BLOCK}<td>{@link MasonTagTypes#MASON_NAMED_BLOCK_END}
	 *    </table>
	 * </dl>
	 *
	 * @return the {@linkplain EndTagType type} of {@linkplain EndTag end tag} required to pair with a {@linkplain StartTag start tag} of this type to form an {@link Element}.
	 * @see EndTagType#getCorrespondingStartTagType()
	 */
	public final EndTagType getCorrespondingEndTagType() {
		return correspondingEndTagType;
	}

	/**
	 * Indicates whether a start tag of this type contains {@linkplain Attributes attributes}.
	 * <br />(<a href="TagType.html#Property">property</a> method)
	 * <p>
	 * The attributes start at the end of the {@linkplain Tag#getName() name} and continue until the
	 * {@linkplain #getClosingDelimiter() closing delimiter} is encountered.  If the character sequence representing the
	 * closing delimiter occurs within a quoted attribute value it is not recognised as the end of the tag.
	 * <p>
	 * The {@link #atEndOfAttributes(Source, int pos, boolean isClosingSlashIgnored)} method can be overridden to provide more control
	 * over where the attributes end.
	 * <p>
	 * <dl>
	 *  <dt>Standard Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Start Tag Type<th>Has Attributes
	 *     <tr><td>{@link StartTagType#UNREGISTERED}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#NORMAL}<td><code>true</code>
	 *     <tr><td>{@link StartTagType#COMMENT}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#XML_DECLARATION}<td><code>true</code>
	 *     <tr><td>{@link StartTagType#XML_PROCESSING_INSTRUCTION}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#DOCTYPE_DECLARATION}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#MARKUP_DECLARATION}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#CDATA_SECTION}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#SERVER_COMMON}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#SERVER_COMMON_ESCAPED}<td><code>false</code>
	 *    </table>
	 * </dl>
	 * <dl>
	 *  <dt>Extended Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Start Tag Type<th>Has Attributes
	 *     <tr><td>{@link MicrosoftTagTypes#DOWNLEVEL_REVEALED_CONDITIONAL_COMMENT}<td><code>false</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_SCRIPT}<td><code>true</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_SHORT}<td><code>false</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_STANDARD}<td><code>false</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALL}<td><code>false</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALLED_WITH_CONTENT}<td><code>false</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_NAMED_BLOCK}<td><code>false</code>
	 *    </table>
	 * </dl>
	 *
	 * @return <code>true</code> if a start tag of this type contains {@linkplain Attributes attributes}, otherwise <code>false</code>.
	 */
	public final boolean hasAttributes() {
		return hasAttributes;
	}

	/**
	 * Indicates whether a valid {@linkplain Tag#isXMLName(CharSequence) XML tag name} is required directly after the {@linkplain #getNamePrefix() prefix}.
	 * <br />(<a href="TagType.html#Property">property</a> method)
	 * <p>
	 * If this property is <code>true</code>, the {@linkplain Tag#getName() name} of the tag consists of the
	 * {@linkplain #getNamePrefix() prefix} followed by an {@linkplain Tag#isXMLName(CharSequence) XML tag name}.
	 * <p>
	 * If this property is <code>false</code>, the {@linkplain Tag#getName() name} of the tag consists of only the
	 * {@linkplain #getNamePrefix() prefix}.
	 * <p>
	 * <dl>
	 *  <dt>Standard Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Start Tag Type<th>Name After Prefix Required
	 *     <tr><td>{@link StartTagType#UNREGISTERED}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#NORMAL}<td><code>true</code>
	 *     <tr><td>{@link StartTagType#COMMENT}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#XML_DECLARATION}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#XML_PROCESSING_INSTRUCTION}<td><code>true</code>
	 *     <tr><td>{@link StartTagType#DOCTYPE_DECLARATION}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#MARKUP_DECLARATION}<td><code>true</code>
	 *     <tr><td>{@link StartTagType#CDATA_SECTION}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#SERVER_COMMON}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#SERVER_COMMON_ESCAPED}<td><code>false</code>
	 *    </table>
	 * </dl>
	 * <dl>
	 *  <dt>Extended Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Start Tag Type<th>Name After Prefix Required
	 *     <tr><td>{@link MicrosoftTagTypes#DOWNLEVEL_REVEALED_CONDITIONAL_COMMENT}<td><code>true</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_SCRIPT}<td><code>false</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_SHORT}<td><code>false</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_STANDARD}<td><code>false</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALL}<td><code>false</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALLED_WITH_CONTENT}<td><code>false</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_NAMED_BLOCK}<td><code>true</code>
	 *    </table>
	 * </dl>
	 *
	 * @return <code>true</code> if a valid {@linkplain Tag#isXMLName(CharSequence) XML tag name} is required directly after the {@linkplain #getNamePrefix() prefix}, otherwise <code>false</code>.
	 */
	public final boolean isNameAfterPrefixRequired() {
		return isNameAfterPrefixRequired;
	}

	/**
	 * Indicates whether the specified source document position is at the end of a tag's {@linkplain Attributes attributes}.
	 * <br />(<a href="TagType.html#DefaultImplementation">default implementation</a> method)
	 * <p>
	 * This method is called internally while parsing {@linkplain Attributes attributes} to detect where they should end.
	 * <p>
 	 * It can be assumed that the specified position is not inside a quoted attribute value.
 	 * <p>
 	 * The default implementation simply compares the {@linkplain ParseText parse text} at the specified
 	 * position with the {@linkplain #getClosingDelimiter() closing delimiter}, and is equivalent to:<br />
 	 * <code>source.</code>{@link Source#getParseText() getParseText()}<code>.containsAt(</code>{@link #getClosingDelimiter() getClosingDelimiter()}<code>,pos)</code>
	 * <p>
	 * The <code>isClosingSlashIgnored</code> parameter is only relevant in the {@link #NORMAL} start tag type,
	 * which makes use of it to cater for the '<code>/</code>' character that can occur before the 
	 * {@linkplain #getClosingDelimiter() closing delimiter} in {@linkplain StartTag#isEmptyElementTag() empty-element tags}.
	 * It's value is always <code>false</code> when passed to other start tag types.
	 *
	 * @param source  the {@link Source} document.
	 * @param pos  the character position in the source document.
	 * @param isClosingSlashIgnored  indicates whether the {@linkplain StartTag#getName() name} of the {@linkplain StartTag start tag} being tested is incompatible with an {@linkplain StartTag#isEmptyElementTag() empty-element tag}.
	 * @return <code>true</code> if the specified source document position is at the end of a tag's {@linkplain Attributes attributes}, otherwise <code>false</code>.
	 */
	public boolean atEndOfAttributes(final Source source, final int pos, final boolean isClosingSlashIgnored) {
		return source.getParseText().containsAt(getClosingDelimiter(),pos);
	}

	/**
	 * Internal method for the construction of a {@link StartTag} object if this type.
	 * <br />(<a href="TagType.html#ImplementationAssistance">implementation assistance</a> method)
	 * <p>
	 * Intended for use from within the {@link #constructTagAt(Source,int) constructTagAt(Source, int pos)} method.
	 *
	 * @param source  the {@link Source} document.
	 * @param begin  the character position in the source document where the tag {@linkplain Segment#getBegin() begins}.
	 * @param end  the character position in the source document where the tag {@linkplain Segment#getEnd() ends}.
	 * @param name  the {@linkplain Tag#getName() name} of the tag.
	 * @param attributes  the {@linkplain StartTag#getAttributes() attributes} of the tag.
	 * @return the new {@link StartTag} object.
	 */
	protected final StartTag constructStartTag(final Source source, final int begin, final int end, final String name, final Attributes attributes) {
		return new StartTag(source,begin,end,this,name,attributes);
	}
	
	/**
	 * Internal method for the parsing of {@link Attributes}.
	 * <br />(<a href="TagType.html#ImplementationAssistance">implementation assistance</a> method)
	 * <p>
	 * Intended for use from within the {@link #constructTagAt(Source,int) constructTagAt(Source, int pos)} method.
	 * <p>
	 * The returned {@link Attributes} segment begins at <code>startTagBegin+1+tagName.length()</code>,
	 * and ends straight after the last attribute found before the tag's {@linkplain #getClosingDelimiter() closing delimiter}.
	 * <p>
	 * Only returns <code>null</code> if the segment contains a major syntactical error
	 * or more than the {@linkplain Attributes#getDefaultMaxErrorCount() default maximum} number of
	 * minor syntactical errors.
	 *
	 * @param source  the {@link Source} document.
	 * @param startTagBegin  the position in the source document at which the start tag is to begin.
	 * @param tagName  the {@linkplain StartTag#getName() name} of the start tag to be constructed.
	 * @return the {@link Attributes} of the start tag to be constructed, or <code>null</code> if too many errors occur while parsing.
	 */
	protected final Attributes parseAttributes(final Source source, final int startTagBegin, final String tagName) {
		return Attributes.construct(source,startTagBegin,this,tagName);
	}
}

