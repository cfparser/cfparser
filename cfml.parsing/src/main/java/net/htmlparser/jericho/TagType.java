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
 * Defines the syntax for a tag type that can be recognised by the parser.
 * <p>
 * This class is the root abstract class common to all tag types, and contains methods to {@linkplain #register() register}
 * and {@linkplain #deregister() deregister} tag types as well as various methods to aid in their implementation.
 * <p>
 * Every tag type is represented by a singleton instance of a class that must be a subclass of either 
 * {@link StartTagType} or {@link EndTagType}.  These two abstract classes, the only direct descendants of this class,
 * represent the two major classifications under which every tag type exists.
 * <p>
 * Because all <code>TagType</code> instaces must be singletons, the '<code>==</code>' operator can be used to test for a particular tag type
 * instead of the <code>equals(Object)</code> method.
 * <p>
 * The term <i><a name="Predefined">predefined tag type</a></i> refers to any of the tag types defined in this library,
 * including both <a href="#Standard">standard</a> and <a href="#Extended">extended</a> tag types.
 * <p>
 * The term <i><a name="Standard">standard tag type</a></i> refers to any of the tag types represented by instances
 * in static fields of the {@link StartTagType} and {@link EndTagType} subclasses.
 * Standard tag types are registered by default, and define the tags most commonly found in HTML documents.
 * <p>
 * The term <i><a name="Extended">extended tag type</a></i> refers to any <a href="#Predefined">predefined</a> tag type
 * that is not a <a href="#Standard">standard</a> tag type.
 * The {@link PHPTagTypes} and {@link MasonTagTypes} classes contain extended tag types related to their respective server platforms.
 * The tag types defined within them must be registered by the user before they are recognised by the parser.
 * <p>
 * The term <i><a name="Custom">custom tag type</a></i> refers to any user-defined tag type, or any tag type that is
 * not a <a href="#Predefined">predefined</a> tag type.
 * <p>
 * The tag recognition process of the parser gives each tag type a <i><a name="Precedence">precedence</a></i> level,
 * which is primarily determined by the length of its {@linkplain #getStartDelimiter() start delimiter}.
 * A tag type with a more specific start delimiter is chosen in preference to one with a less specific start delimiter,
 * assuming they both share the same prefix.  If two tag types have exactly the same start delimiter, the one which was
 * {@linkplain #register() registered} later has the higher precedence.
 * <p>
 * The two special tag types {@link StartTagType#UNREGISTERED} and {@link EndTagType#UNREGISTERED} represent
 * tags that do not match the syntax of any other tag type.  They have the lowest <a href="#Precedence">precedence</a> 
 * of all the tag types.  The {@link Tag#isUnregistered()} method provides a detailed explanation of unregistered tags.
 * <p>
 * See the documentation of the <a href="Tag.html#ParsingProcess">tag parsing process</a> for more information
 * on how each tag is identified by the parser.
 * <p>
 * <a name="Normal"></a>Note that the standard {@linkplain HTMLElementName HTML element names} do not represent different
 * tag <i>types</i>.  All standard HTML tags have a tag type of {@link StartTagType#NORMAL} or {@link EndTagType#NORMAL},
 * and are also referred to as <i>normal</i> tags.
 * <p>
 * Apart from the <a href="#RegistrationRelated">registration related</a> methods, all of the methods in this class and its
 * subclasses relate to the implementation of <a href="#Custom">custom tag types</a> and are not relevant to the majority of users 
 * who just use the <a href="#Predefined">predefined tag types</a>.
 * <p>
 * For perfomance reasons, this library only allows tag types that {@linkplain #getStartDelimiter() start}
 * with a '<code>&lt;</code>' character.
 * The character following this defines the immediate subclass of the tag type.
 * An {@link EndTagType} always has a slash ('<code>/</code>') as the second character, while a {@link StartTagType}
 * has any character other than a slash as the second character.
 * This definition means that tag types which are not intuitively classified as either start tag types or end tag types
 * (such as an HTML {@linkplain StartTagType#COMMENT comment}) are mostly classified as start tag types.
 * <p>
 * Every method in this and the {@link StartTagType} and {@link EndTagType} abstract classes can be categorised
 * as one of the following:
 * <dl>
 *  <dt><a name="Property">Properties:</a>
 *   <dd>Simple properties (marked final) that were either specified as parameters
 *    during construction or are derived from those parameters.
 *  <dt><a name="AbstractImplementation">Abstract implementation methods:</a>
 *   <dd>Methods that must be implemented in a subclass.
 *  <dt><a name="DefaultImplementation">Default implementation methods:</a>
 *   <dd>Methods (not marked final) that implement common behaviour, but may be overridden in a subclass.
 *  <dt><a name="ImplementationAssistance">Implementation assistance methods:</a>
 *   <dd>Protected methods that provide low-level functionality and are only of use within other implementation methods.
 *  <dt><a name="RegistrationRelated">Registration related methods:</a>
 *   <dd>Utility methods (marked final) relating to the {@linkplain #register() registration} of tag type instances.
 * </dl>
 */
public abstract class TagType {
	private final String description;
	private final String startDelimiter;
	private final String closingDelimiter;
	private final boolean isServerTag;
	private final String namePrefix;
	final String startDelimiterPrefix;

	TagType(final String description, final String startDelimiter, final String closingDelimiter, final boolean isServerTag, final String startDelimiterPrefix) {
		// startDelimiterPrefix is either "<" or "</"
		this.description=description;
		this.startDelimiter=startDelimiter;
		this.closingDelimiter=closingDelimiter;
		this.isServerTag=isServerTag;
		this.namePrefix=startDelimiter.substring(startDelimiterPrefix.length());
		this.startDelimiterPrefix=startDelimiterPrefix;
	}

	/**
	 * Registers this tag type for recognition by the parser.
	 * <br />(<a href="TagType.html#RegistrationRelated">registration related</a> method)
	 * <p>
	 * The order of registration affects the <a href="TagType.html#Precedence">precedence</a> of the tag type when a potential tag is being parsed.
	 *
	 * @see #deregister()
	 */
	public final void register() {
		TagTypeRegister.add(this);
	}
	
	/**
	 * Deregisters this tag type.
	 * <br />(<a href="TagType.html#RegistrationRelated">registration related</a> method)
	 *
	 * @see #register()
	 */
	public final void deregister() {
		TagTypeRegister.remove(this);
	}

	/**
	 * Returns a list of all the currently registered tag types in order of lowest to highest <a href="TagType.html#Precedence">precedence</a>.
	 * <br />(<a href="TagType.html#RegistrationRelated">registration related</a> method)
	 * @return a list of all the currently registered tag types in order of lowest to highest <a href="TagType.html#Precedence">precedence</a>.
	 */
	public static final List<TagType> getRegisteredTagTypes() {
		return TagTypeRegister.getList();
	}

	/**
	 * Returns a description of this tag type useful for debugging purposes. 
	 * <br />(<a href="TagType.html#Property">property</a> method)
	 *
	 * @return a description of this tag type useful for debugging purposes.
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * Returns the character sequence that marks the start of the tag.
	 * <br />(<a href="TagType.html#Property">property</a> method)
	 * <p>
	 * The character sequence must be all in lower case.
	 * <p>
	 * The first character in this property <b>must</b> be '<code>&lt;</code>'.
	 * This is a deliberate limitation of the system which is necessary to retain reasonable performance.
	 * <p>
	 * The second character in this property must be '<code>/</code>' if the implementing class is an {@link EndTagType}.
	 * It must <b>not</b> be '<code>/</code>' if the implementing class is a {@link StartTagType}.
	 * <p>
	 * <dl>
	 *  <dt>Standard Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Tag Type<th>Start Delimiter
	 *     <tr><td>{@link StartTagType#UNREGISTERED}<td><code>&lt;</code>
	 *     <tr><td>{@link StartTagType#NORMAL}<td><code>&lt;</code>
	 *     <tr><td>{@link StartTagType#COMMENT}<td><code>&lt;!--</code>
	 *     <tr><td>{@link StartTagType#XML_DECLARATION}<td><code>&lt;?xml</code>
	 *     <tr><td>{@link StartTagType#XML_PROCESSING_INSTRUCTION}<td><code>&lt;?</code>
	 *     <tr><td>{@link StartTagType#DOCTYPE_DECLARATION}<td><code>&lt;!doctype</code>
	 *     <tr><td>{@link StartTagType#MARKUP_DECLARATION}<td><code>&lt;!</code>
	 *     <tr><td>{@link StartTagType#CDATA_SECTION}<td><code>&lt;![cdata[</code>
	 *     <tr><td>{@link StartTagType#SERVER_COMMON}<td><code>&lt;%</code>
	 *     <tr><td>{@link EndTagType#UNREGISTERED}<td><code>&lt;/</code>
	 *     <tr><td>{@link EndTagType#NORMAL}<td><code>&lt;/</code>
	 *    </table>
	 * </dl>
	 * <dl>
	 *  <dt>Extended Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Tag Type<th>Start Delimiter
	 *     <tr><td>{@link MicrosoftTagTypes#DOWNLEVEL_REVEALED_CONDITIONAL_COMMENT}<td><code>&lt;![</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_SCRIPT}<td><code>&lt;script</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_SHORT}<td><code>&lt;?</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_STANDARD}<td><code>&lt;?php</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALL}<td><code>&lt;&amp;</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALLED_WITH_CONTENT}<td><code>&lt;&amp;|</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALLED_WITH_CONTENT_END}<td><code>&lt;/&amp;</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_NAMED_BLOCK}<td><code>&lt;%</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_NAMED_BLOCK_END}<td><code>&lt;/%</code>
	 *    </table>
	 * </dl>
	 *
	 * @return the character sequence that marks the start of the tag.
	 */
	public final String getStartDelimiter() {
		return startDelimiter;
	}

	/**
	 * Returns the character sequence that marks the end of the tag.
	 * <br />(<a href="TagType.html#Property">property</a> method)
	 * <p>
	 * The character sequence must be all in lower case.
	 * <p>
	 * In a {@link StartTag} of a {@linkplain StartTagType type} that {@linkplain StartTagType#hasAttributes() has attributes},
	 * characters appearing inside a quoted attribute value are ignored when determining the location of the closing delimiter.
	 * <p>
	 * Note that the optional '<code>/</code>' character preceding the closing '<code>&gt;</code>' in an
	 * {@linkplain StartTag#isEmptyElementTag() empty-element tag} is not considered part of the end delimiter.
	 * This property must define the closing delimiter common to all instances of the tag type.
	 * <p>
	 * <dl>
	 *  <dt>Standard Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Tag Type<th>Closing Delimiter
	 *     <tr><td>{@link StartTagType#UNREGISTERED}<td><code>&gt;</code>
	 *     <tr><td>{@link StartTagType#NORMAL}<td><code>&gt;</code>
	 *     <tr><td>{@link StartTagType#COMMENT}<td><code>--&gt;</code>
	 *     <tr><td>{@link StartTagType#XML_DECLARATION}<td><code>?&gt;</code>
	 *     <tr><td>{@link StartTagType#XML_PROCESSING_INSTRUCTION}<td><code>?&gt;</code>
	 *     <tr><td>{@link StartTagType#DOCTYPE_DECLARATION}<td><code>&gt;</code>
	 *     <tr><td>{@link StartTagType#MARKUP_DECLARATION}<td><code>&gt;</code>
	 *     <tr><td>{@link StartTagType#CDATA_SECTION}<td><code>]]&gt;</code>
	 *     <tr><td>{@link StartTagType#SERVER_COMMON}<td><code>%&gt;</code>
	 *     <tr><td>{@link EndTagType#UNREGISTERED}<td><code>&gt;</code>
	 *     <tr><td>{@link EndTagType#NORMAL}<td><code>&gt;</code>
	 *    </table>
	 * </dl>
	 * <dl>
	 *  <dt>Extended Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Tag Type<th>Closing Delimiter
	 *     <tr><td>{@link MicrosoftTagTypes#DOWNLEVEL_REVEALED_CONDITIONAL_COMMENT}<td><code>]&gt;</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_SCRIPT}<td><code>&gt;</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_SHORT}<td><code>?&gt;</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_STANDARD}<td><code>?&gt;</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALL}<td><code>&amp;&gt;</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALLED_WITH_CONTENT}<td><code>&amp;&gt;</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALLED_WITH_CONTENT_END}<td><code>&gt;</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_NAMED_BLOCK}<td><code>&gt;</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_NAMED_BLOCK_END}<td><code>&gt;</code>
	 *    </table>
	 * </dl>
	 *
	 * @return the character sequence that marks the end of the tag.
	 */
	public final String getClosingDelimiter() {
		return closingDelimiter;
	}

	/**
	 * Indicates whether this tag type represents a server tag.
	 * <br />(<a href="TagType.html#Property">property</a> method)
	 * <p>
	 * Server tags are typically parsed by some process on the web server and substituted with other text or markup before delivery to the
	 * <a target="_blank" href="http://www.w3.org/TR/html401/conform.html#didx-user_agent">user agent</a>.
	 * This parser therefore handles them differently to non-server tags in that they can occur at any position in the document
	 * without regard for the HTML document structure.
	 * As a result they can occur anywhere inside any other tag, although a non-server tag cannot theoretically occur inside a server tag.
	 * <p>
	 * The documentation of the <a href="Tag.html#ParsingProcess">tag parsing process</a> explains in detail 
	 * how the value of this property affects the recognition of server tags,
	 * as well as how the presence of server tags affects the recognition of non-server tags in and around them.
	 * <p>
	 * Most XML-style server tags can not be represented as a distinct tag type because they are generally indistinguishable from non-server XML tags.
	 * See the {@link Segment#ignoreWhenParsing()} method for information about how to prevent such server tags from interfering with the proper parsing
	 * of the rest of the document.
	 * <p>
	 * <dl>
	 *  <dt>Standard Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Tag Type<th>Is Server Tag
	 *     <tr><td>{@link StartTagType#UNREGISTERED}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#NORMAL}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#COMMENT}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#XML_DECLARATION}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#XML_PROCESSING_INSTRUCTION}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#DOCTYPE_DECLARATION}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#MARKUP_DECLARATION}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#CDATA_SECTION}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#SERVER_COMMON}<td><code>true</code>
	 *     <tr><td>{@link EndTagType#UNREGISTERED}<td><code>false</code>
	 *     <tr><td>{@link EndTagType#NORMAL}<td><code>false</code>
	 *    </table>
	 * </dl>
	 * <dl>
	 *  <dt>Extended Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Tag Type<th>Is Server Tag
	 *     <tr><td>{@link MicrosoftTagTypes#DOWNLEVEL_REVEALED_CONDITIONAL_COMMENT}<td><code>false</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_SCRIPT}<td><code>true</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_SHORT}<td><code>true</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_STANDARD}<td><code>true</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALL}<td><code>true</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALLED_WITH_CONTENT}<td><code>true</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALLED_WITH_CONTENT_END}<td><code>true</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_NAMED_BLOCK}<td><code>true</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_NAMED_BLOCK_END}<td><code>true</code>
	 *    </table>
	 * </dl>
	 *
	 * @return <code>true</code> if this tag type represents a server tag, otherwise <code>false</code>.
	 */
	public final boolean isServerTag() {
		return isServerTag;
	}

	/**
	 * Returns the {@linkplain Tag#getName() name} prefix required by this tag type.
	 * <br />(<a href="TagType.html#Property">property</a> method)
	 * <p>
	 * This string is identical to the {@linkplain #getStartDelimiter() start delimiter}, except that it does not include the
	 * initial "<code>&lt;</code>" or "<code>&lt;/</code>" characters that always prefix the start delimiter of a
	 * {@link StartTagType} or {@link EndTagType} respectively.
	 * <p>
	 * The {@linkplain Tag#getName() name} of a tag of this type may or may not include extra characters after the prefix.
	 * This is determined by properties such as {@link StartTagType#isNameAfterPrefixRequired()}
	 * or {@link EndTagTypeGenericImplementation#isStatic()}. 
	 * <p>
	 * <dl>
	 *  <dt>Standard Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Tag Type<th>Name Prefix
	 *     <tr><td>{@link StartTagType#UNREGISTERED}<td><i>(empty string)</i>
	 *     <tr><td>{@link StartTagType#NORMAL}<td><i>(empty string)</i>
	 *     <tr><td>{@link StartTagType#COMMENT}<td><code>!--</code>
	 *     <tr><td>{@link StartTagType#XML_DECLARATION}<td><code>?xml</code>
	 *     <tr><td>{@link StartTagType#XML_PROCESSING_INSTRUCTION}<td><code>?</code>
	 *     <tr><td>{@link StartTagType#DOCTYPE_DECLARATION}<td><code>!doctype</code>
	 *     <tr><td>{@link StartTagType#MARKUP_DECLARATION}<td><code>!</code>
	 *     <tr><td>{@link StartTagType#CDATA_SECTION}<td><code>![cdata[</code>
	 *     <tr><td>{@link StartTagType#SERVER_COMMON}<td><code>%</code>
	 *     <tr><td>{@link EndTagType#UNREGISTERED}<td><i>(empty string)</i>
	 *     <tr><td>{@link EndTagType#NORMAL}<td><i>(empty string)</i>
	 *    </table>
	 * </dl>
	 * <dl>
	 *  <dt>Extended Tag Type Values:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Tag Type<th>Name Prefix
	 *     <tr><td>{@link MicrosoftTagTypes#DOWNLEVEL_REVEALED_CONDITIONAL_COMMENT}<td><code>![</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_SCRIPT}<td><code>script</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_SHORT}<td><code>?</code>
	 *     <tr><td>{@link PHPTagTypes#PHP_STANDARD}<td><code>?php</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALL}<td><code>&amp;</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALLED_WITH_CONTENT}<td><code>&amp;|</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_COMPONENT_CALLED_WITH_CONTENT_END}<td><code>&amp;</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_NAMED_BLOCK}<td><code>%</code>
	 *     <tr><td>{@link MasonTagTypes#MASON_NAMED_BLOCK_END}<td><code>%</code>
	 *    </table>
	 * </dl>
	 *
	 * @return the {@linkplain Tag#getName() name} prefix required by this tag type.
	 * @see #getStartDelimiter()
	 */
	protected final String getNamePrefix() {
		return namePrefix;
	}

	/**
	 * Indicates whether a tag of this type is valid in the specified position of the specified source document.
	 * <br />(<a href="TagType.html#ImplementationAssistance">implementation assistance</a> method)
	 * <p>
	 * This method is called immediately before {@link #constructTagAt(Source, int pos)}
	 * to do a preliminary check on the validity of a tag of this type in the specified position.
	 * <p>
	 * This check is not performed as part of the {@link #constructTagAt(Source, int pos)} call because the same
	 * validation is used for all the <a href="TagType.html#Standard">standard</a> tag types, and is likely to be sufficient
	 * for all <a href="TagType.html#Custom">custom tag types</a>.
	 * Having this check separated into a different method helps to isolate common code from the code that is unique to each tag type.
	 * <p>
	 * In theory, a {@linkplain TagType#isServerTag() server tag} is valid in any position, but a non-server tag is not valid inside any other tag,
	 * nor inside elements with CDATA content such as {@link HTMLElementName#SCRIPT SCRIPT} and {@link HTMLElementName#STYLE STYLE} elements.
	 * <p>
	 * The common implementation of this method always returns <code>true</code> for server tags, but for non-server tags it behaves slightly differently
	 * depending upon whether or not a {@linkplain Source#fullSequentialParse() full sequential parse} is being peformed.
	 * <p>
	 * When this method is called during a full sequential parse, the <code>fullSequentialParseData</code> argument contains information
	 * allowing the exact theoretical check to be performed, rejecting a non-server tag if it is inside any other tag.
	 * See below for further information about the <code>fullSequentialParseData</code> parameter.
	 * <p>
	 * When this method is called in <a href="Source.html#ParseOnDemand">parse on demand</a> mode
	 * (not during a full sequential parse, <code>fullSequentialParseData==null</code>),
	 * practical constraints prevent the exact theoretical check from being carried out, and non-server tags are only rejected 
	 * if they are found inside HTML {@linkplain StartTagType#COMMENT comments} or {@linkplain StartTagType#CDATA_SECTION CDATA sections}.
	 * <p>
	 * This behaviour is configurable by manipulating the static {@link TagType#getTagTypesIgnoringEnclosedMarkup() TagTypesIgnoringEnclosedMarkup} array
	 * to determine which tag types can not contain non-server tags in <a href="Source.html#ParseOnDemand">parse on demand</a> mode.
	 * The {@linkplain TagType#getTagTypesIgnoringEnclosedMarkup() documentation of this property} contains
	 * a more detailed analysis of the subject and explains why only the {@linkplain StartTagType#COMMENT comment} and 
	 * {@linkplain StartTagType#CDATA_SECTION CDATA section} tag types are included by default.
	 * <p>
	 * See the documentation of the <a href="Tag.html#ParsingProcess">tag parsing process</a> for more information about how this method fits into the whole tag parsing process.
	 * <p>
	 * This method can be overridden in <a href="TagType.html#Custom">custom tag types</a> if the default implementation is unsuitable.
	 * <p>
	 * <b>The <code>fullSequentialParseData</code> parameter:</b>
	 * <p>
	 * This parameter is used to discard non-server tags that are found inside other tags or inside {@link HTMLElementName#SCRIPT SCRIPT} elements.
	 * <p>
	 * In the current version of this library, the <code>fullSequentialParseData</code> argument is either <code>null</code>
	 * (in <a href="Source.html#ParseOnDemand">parse on demand</a> mode) or an integer array containing only a single entry
	 * (if a {@linkplain Source#fullSequentialParse() full sequential parse} is being peformed).
	 * <p>
	 * The integer contained in the array is the maximum position in the document at which the end of a tag has been found,
	 * indicating that no non-server tags should be recognised before that position.
	 * If no tags have yet been encountered, the value of this integer is zero.
	 * <p>
	 * If the last tag encountered was the {@linkplain StartTag start tag} of a {@link HTMLElementName#SCRIPT SCRIPT} element,
	 * the value of this integer is <code>Integer.MAX_VALUE</code>, indicating that no other non-server elements should be recognised until the
	 * {@linkplain EndTag end tag} of the {@link HTMLElementName#SCRIPT SCRIPT} element is found.
	 * According to the <a target="_blank" href="http://www.w3.org/TR/html401/types.html#idx-CDATA-1">HTML 4.01 specification section 6.2</a>,
	 * the first occurrence of the character sequence "<code>&lt;/</code>" terminates the special handling of CDATA within
	 * {@link HTMLElementName#SCRIPT SCRIPT} and {@link HTMLElementName#STYLE STYLE} elements.
	 * This library however only terminates the CDATA handling of {@link HTMLElementName#SCRIPT SCRIPT} element content
	 * when the character sequence "<code>&lt;/script</code>" is detected, in line with the behaviour of the major browsers.
	 * <p>
	 * Note that the implicit treatment of {@link HTMLElementName#SCRIPT SCRIPT} element content as CDATA should theoretically also prevent the recognition of
	 * {@linkplain StartTagType#COMMENT comments} and explicit {@linkplain StartTagType#CDATA_SECTION CDATA sections} inside script elements.
	 * While this is true for explicit {@linkplain StartTagType#CDATA_SECTION CDATA sections}, the parser does still recognise
	 * {@linkplain StartTagType#COMMENT comments} inside {@link HTMLElementName#SCRIPT SCRIPT} elements in order to maintain compatability with the major browsers.
	 * This prevents the character sequence "<code>&lt;/script</code>" from terminating the {@link HTMLElementName#SCRIPT SCRIPT} element
	 * if it occurs inside a {@linkplain StartTagType#COMMENT comment}.  The end of the {@linkplain StartTagType#COMMENT comment} however also
	 * ends the implicit treatment of the {@link HTMLElementName#SCRIPT SCRIPT} element content as CDATA.
	 * <p>
	 * Although {@link HTMLElementName#STYLE STYLE} elements should theoretically be treated in the same way as {@link HTMLElementName#SCRIPT SCRIPT} elements,
	 * the syntax of <a target="_blank" href="http://www.w3.org/Style/CSS/">Cascading Style Sheets</a> (CSS) does not contain any constructs that 
	 * could be misinterpreted as HTML tags, so there is virtually no need to perform any special checks in this case.
	 * <p>
	 * IMPLEMENTATION NOTE: The rationale behind using an integer array to hold this value, rather than a scalar <code>int</code> value,
	 * is to emulate passing the parameter by reference.
	 * This value needs to be shared amongst several internal methods during the {@linkplain Source#fullSequentialParse() full sequential parse} process,
	 * and any one of those methods needs to be able to modify the value and pass it back to the calling method.
	 * This would normally be implemented by passing the parameter by reference, but because Java does not support this language construct, a container for a
	 * mutable integer must be passed instead.  
	 * Because the standard Java library does not provide a class for holding a single mutable integer (the <code>java.lang.Integer</code> class is immutable),
	 * the easiest container to use, without creating a class especially for this purpose, is an integer array.
	 * The use of an array does not imply any intention to use more than a single array entry in subsequent versions.
	 *
	 * @param source  the {@link Source} document.
	 * @param pos  the character position in the source document to check.
	 * @param fullSequentialParseData  an integer array containing data allowing this method to implement a better algorithm when a {@linkplain Source#fullSequentialParse() full sequential parse} is being performed, or <code>null</code> in <a href="Source.html#ParseOnDemand">parse on demand</a> mode.
	 * @return <code>true</code> if a tag of this type is valid in the specified position of the specified source document, otherwise <code>false</code>.
	 */
	protected boolean isValidPosition(final Source source, final int pos, final int[] fullSequentialParseData) {
		if (isServerTag()) return true;
		if (fullSequentialParseData!=null) {
			// use simplified check when doing full sequential parse.  Normally we are only able to check whether a tag is inside specially cached
			// tag types for efficiency reasons, but during a full sequential parse we can reject a tag if it is inside any other tag.
			if (fullSequentialParseData[0]==Integer.MAX_VALUE) { // we are in a SCRIPT element
				if (this==EndTagType.NORMAL && source.getParseText().containsAt("</script",pos)) {
					// The character sequence "</script" terminates the implicit CDATA section inside the SCRIPT element
					fullSequentialParseData[0]=pos;
					return true;
				}
				if (this==StartTagType.COMMENT) {
 					// Although not technically correct, all major browsers also recognise comments inside SCRIPT elements.
 					// The end of the comment will however terminate the implicit CDATA section inside the SCRIPT element.
					fullSequentialParseData[0]=pos;
 					return true;
				}
				return false; // reject any other tags inside SCRIPT element
			}
			return pos>=fullSequentialParseData[0]; // accept the non-server tag only if it is after the end of the last found non-server tag
		}
		// Use the normal method of checking whether the position is inside a tag of a tag type that ignores enclosed markup:
		final TagType[] tagTypesIgnoringEnclosedMarkup=getTagTypesIgnoringEnclosedMarkup();
		for (int i=0; i<tagTypesIgnoringEnclosedMarkup.length; i++) {
			final TagType tagTypeIgnoringEnclosedMarkup=tagTypesIgnoringEnclosedMarkup[i];
			// If this tag type is a comment, don't bother checking whether it is inside another comment.
			// See javadocs for getTagTypesIgnoringEnclosedMarkup() for more explanation.
			// Allowing it might result in multiple comments being recognised with the same end delimiter, but the risk of this occuring in a syntactically invalid document
			// is outweighed by the benefit of not recursively checking all previous comments in a document, risking stack overflow.
			if (this==StartTagType.COMMENT && tagTypeIgnoringEnclosedMarkup==StartTagType.COMMENT) continue;
			if (tagTypeIgnoringEnclosedMarkup.tagEncloses(source,pos)) return false;
		}
		return true;
	}

	/**
	 * Returns an array of all the tag types inside which the parser ignores all non-{@linkplain #isServerTag() server} tags
	 * in <a href="Source.html#ParseOnDemand">parse on demand</a> mode.
	 * <br />(<a href="TagType.html#ImplementationAssistance">implementation assistance</a> method)
	 * <p>
	 * The tag types returned by this property (referred to in the following paragraphs as the "listed types") default to
	 * {@link StartTagType#COMMENT} and {@link StartTagType#CDATA_SECTION}.
	 * <p>
	 * This property is used by the default implementation of the {@link #isValidPosition(Source, int pos, int[] fullSequentialParseData) isValidPosition} method
	 * in <a href="Source.html#ParseOnDemand">parse on demand</a> mode.
	 * It is not used at all during a {@linkplain Source#fullSequentialParse() full sequential parse}.
	 * <p>
	 * In the default implementation of the {@link #isValidPosition(Source, int pos, int[] fullSequentialParseData) isValidPosition} method,
	 * in <a href="Source.html#ParseOnDemand">parse on demand</a> mode,
	 * every new non-server tag found by the parser (referred to as a "new tag") undergoes a check to see whether it is enclosed
	 * by a tag of one of the listed types.
	 * This includes new tags of the listed types themselves if they are non-server tags.
	 * The recursive nature of this check means that <i>all</i> tags of the listed types occurring before the new tag must be found 
	 * by the parser before it can determine whether the new tag should be ignored.
	 * To mitigate any performance issues arising from this process, the listed types are given special treatment in the tag cache.
	 * This dramatically decreases the time taken to search on these tag types, so adding a tag type to this array that 
	 * is easily recognised and occurs infrequently only results in a small degradation in overall performance.
	 * <p>
	 * A special exception to the algorithm described above applies to {@link StartTagType#COMMENT COMMENT} tags.
	 * The default implementation of the {@link #isValidPosition(Source,int,int[]) isValidPosition} method
	 * does not check whether a {@link StartTagType#COMMENT COMMENT} tag is inside another {@link StartTagType#COMMENT COMMENT} tag,
	 * as this should never happen in a syntactically correct document (the characters '<code>--</code>' should not occur inside a comment).
	 * Skipping this check also avoids the need to recursively check every {@link StartTagType#COMMENT COMMENT} tag back to the start of the document,
	 * which has the potential to cause a stack overflow in a large document containing lots of comments.
	 * <p>
	 * Theoretically, non-server tags appearing inside any other tag should be ignored, which is how the parser behaves during a
	 * {@linkplain Source#fullSequentialParse() full sequential parse}.
	 * <p>
	 * Server tags in particular very often contain other "tags" that should not be recognised as tags by the parser.
	 * If this behaviour is required in <a href="Source.html#ParseOnDemand">parse on demand</a>, the tag type of each server tag that might be found
	 * in the source documents can be added to this property using the static {@link #setTagTypesIgnoringEnclosedMarkup(TagType[])} method.
	 * For example, the following command would prevent non-server tags from being recognised inside {@linkplain PHPTagTypes#PHP_STANDARD standard PHP} tags,
	 * as well as the default {@linkplain StartTagType#COMMENT comment} and {@linkplain StartTagType#CDATA_SECTION CDATA section} tags:
	 * <p>
	 * <blockquote><code>TagType.setTagTypesIgnoringEnclosedMarkup(new TagType[] {PHPTagTypes.PHP_STANDARD, StartTagType.COMMENT, StartTagType.CDATA_SECTION});</code></blockquote>
	 * <p>
	 * The only situation where a non-server tag can legitimately contain a sequence of characters that resembles a tag is within an attribute value.
	 * The <a target="_blank" href="http://www.w3.org/TR/html401/charset.html#h-5.3.2">HTML 4.01 specification section 5.3.2</a>
	 * specifically allows the presence of '<code>&lt;</code>' and '<code>&gt;</code>' characters within attribute values.
	 * A common occurrence of this is in <a target="_blank" href="http://www.w3.org/TR/html401/interact/scripts.html#events">event</a> attributes containing scripts,
	 * such as the <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/scripts.html#adef-onclick">onclick</a></code> attribute.
	 * There is no way of preventing such "tags" from being recognised in <a href="Source.html#ParseOnDemand">parse on demand</a> mode, as adding
	 * {@link StartTagType#NORMAL} to this property as a listed type would be far too inefficient.
	 * Performing a {@linkplain Source#fullSequentialParse() full sequential parse} of the source document prevents these attribute values from being
	 * recognised as tags, but can be very expensive if only a few tags in the document need to be parsed.
	 * The penalty of not parsing every tag in the document is that the exactness of this check is compromised, but in practical terms the difference is inconsequential.
	 * The default listed types of {@linkplain StartTagType#COMMENT comments} and {@linkplain StartTagType#CDATA_SECTION CDATA sections} yields sensible results 
	 * in the vast majority of practical applications with only a minor impact on performance.
	 * <p>
	 * In <a target="_blank" href="http://www.w3.org/TR/xhtml1/">XHTML</a>, '<code>&lt;</code>' and '<code>&gt;</code>' characters 
	 * must be represented in attribute values as {@linkplain CharacterReference character references}
	 * (see the XML 1.0 specification section <a target="_blank" href="http://www.w3.org/TR/REC-xml#CleanAttrVals">3.1</a>),
	 * so the situation should never arise that a tag is found inside another tag unless one of them is a
	 * {@linkplain #isServerTag() server tag}.
	 *
	 * @return an array of all the tag types inside which the parser ignores all non-{@linkplain #isServerTag() server} tags.
	 */
	public static final TagType[] getTagTypesIgnoringEnclosedMarkup() {
		return TagTypesIgnoringEnclosedMarkup.array;
	}

	/**
	 * Sets the tag types inside which the parser ignores all non-{@linkplain #isServerTag() server} tags.
	 * <br />(<a href="TagType.html#ImplementationAssistance">implementation assistance</a> method)
	 * <p>
	 * See {@link #getTagTypesIgnoringEnclosedMarkup()} for the documentation of this property.
	 *
	 * @param tagTypes  an array of tag types.
	 */
	public static final void setTagTypesIgnoringEnclosedMarkup(TagType[] tagTypes) {
		if (tagTypes==null) throw new IllegalArgumentException();
		TagTypesIgnoringEnclosedMarkup.array=tagTypes;
	}

	/**
	 * Constructs a tag of this type at the specified position in the specified source document if it matches all of the required features.
	 * <br />(<a href="TagType.html#AbstractImplementation">abstract implementation</a> method)
	 * <p>
	 * The implementation of this method must check that the text at the specified position meets all of
	 * the criteria of this tag type, including such checks as the presence of the correct or well formed
	 * {@linkplain #getClosingDelimiter() closing delimiter}, {@linkplain Tag#getName() name}, {@linkplain Attributes attributes},
	 * {@linkplain EndTag end tag}, or any other distinguishing features.
	 * <p>
	 * It can be assumed that the specified position starts with the {@linkplain #getStartDelimiter() start delimiter} of this tag type,
	 * and that all other tag types with higher <a href="TagType.html#Precedence">precedence</a> (if any) have already been rejected as candidates.
	 * Tag types with lower precedence will be considered if this method returns <code>null</code>.
	 * <p>
	 * This method is only called after a successful check of the tag's position, i.e.
	 * {@link #isValidPosition(Source,int,int[]) isValidPosition(source,pos,fullSequentialParseData)}<code>==true</code>.
	 * <p>
	 * The {@link StartTagTypeGenericImplementation} and {@link EndTagTypeGenericImplementation} subclasses provide default
	 * implementations of this method that allow the use of much simpler <a href="TagType.html#Property">properties</a> and
	 * <a href="TagType.html#ImplementationAssistance">implementation assistance</a> methods and to carry out the required functions.
	 *
	 * @param source  the {@link Source} document.
	 * @param pos  the position in the source document.
	 * @return a tag of this type at the specified position in the specified source document if it meets all of the required features, or <code>null</code> if it does not meet the criteria.
	 */
	protected abstract Tag constructTagAt(Source source, int pos);

	/**
	 * Indicates whether a tag of this type encloses the specified position of the specified source document.
	 * <br />(<a href="TagType.html#ImplementationAssistance">implementation assistance</a> method)
	 * <p>
	 * This is logically equivalent to <code>source.</code>{@link Source#getEnclosingTag(int,TagType) getEnclosingTag(pos,this)}<code>!=null</code>,
	 * but is safe to use within other implementation methods without the risk of causing an infinite recursion.
	 * <p>
	 * This method is called from the default implementation of the {@link #isValidPosition(Source, int pos, int[] fullSequentialParseData)} method.
	 *
	 * @param source  the {@link Source} document.
	 * @param pos  the character position in the source document to check.
	 * @return <code>true</code> if a tag of this type encloses the specified position of the specified source document, otherwise <code>false</code>.
	 */
	protected final boolean tagEncloses(final Source source, final int pos) {
		if (pos==0) return false;
		final Tag enclosingTag=source.getEnclosingTag(pos-1,this); // use pos-1 otherwise a tag at pos could cause infinite recursion when this is called from constructTagAt
		return enclosingTag!=null && pos!=enclosingTag.getEnd(); // make sure pos!=enclosingTag.getEnd() to compensate for using pos-1 above (important if the tag in question immediately follows an end tag delimiter)
	}

	/**
	 * Returns a string representation of this object useful for debugging purposes.
	 * @return a string representation of this object useful for debugging purposes.
	 */
	public String toString() {
		return getDescription();
	}

	static final Tag getTagAt(final Source source, final int pos, final boolean serverTagOnly, final boolean assumeNoNestedTags) {
		final TagTypeRegister.ProspectiveTagTypeIterator prospectiveTagTypeIterator=new TagTypeRegister.ProspectiveTagTypeIterator(source,pos);
		// prospectiveTagTypeIterator is empty if pos is out of range.
		while (prospectiveTagTypeIterator.hasNext()) {
			final TagType tagType=prospectiveTagTypeIterator.next();
			if (serverTagOnly && !tagType.isServerTag()) continue;
			if (!assumeNoNestedTags && !tagType.isValidPosition(source,pos,source.fullSequentialParseData)) continue;
			try {
				final Tag tag=tagType.constructTagAt(source,pos);
				if (tag!=null) return tag;
			} catch (IndexOutOfBoundsException ex) {
				if (source.logger.isInfoEnabled()) source.logger.info(source.getRowColumnVector(pos).appendTo(new StringBuilder(200).append("Tag at ")).append(" not recognised as type '").append(tagType.getDescription()).append("' because it has no end delimiter").toString());
			}
		}
		return null;
	}

	private static final class TagTypesIgnoringEnclosedMarkup {
		// This internal class is used to contain the array because its static initialisation can occur after
		// the StartTagType.COMMENT and StartTagType.CDATA_SECTION members have been created.
		public static TagType[] array=new TagType[] {
			StartTagType.COMMENT,
			StartTagType.CDATA_SECTION
		};
	}
}
