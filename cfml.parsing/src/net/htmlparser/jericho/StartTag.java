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

import java.util.Map;
import java.util.Set;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Represents the <a target="_blank" href="http://www.w3.org/TR/html401/intro/sgmltut.html#didx-element-2">start tag</a> of an 
 * {@linkplain Element element} in a specific {@linkplain Source source} document.
 * <p>
 * A start tag always has a {@linkplain #getTagType() type} that is a subclass of {@link StartTagType}, meaning that any tag
 * that does <b>not</b> start with the characters '<code>&lt;/</code>' is categorised as a start tag.
 * <p>
 * This includes many tags which stand alone, without a {@linkplain StartTagType#getCorrespondingEndTagType() corresponding end tag},
 * and would not intuitively be categorised as a "start tag".
 * For example, an HTML {@linkplain StartTagType#COMMENT comment} is represented as a single start tag that spans the whole comment,
 * and does not have an end tag at all.
 * <p>
 * See the <a href="StartTagType.html#field_summary">static fields</a> defined in the {@link StartTagType} class for a list of the 
 * <a href="TagType.html#Standard">standard</a> start tag types.
 * <p>
 * <code>StartTag</code> instances are obtained using one of the following methods:
 * <ul>
 *  <li>{@link Element#getStartTag()}
 *  <li>{@link Tag#getNextTag()}
 *  <li>{@link Tag#getPreviousTag()}
 *  <li>{@link Source#getPreviousStartTag(int pos)}
 *  <li>{@link Source#getPreviousStartTag(int pos, String name)}
 *  <li>{@link Source#getPreviousTag(int pos)}
 *  <li>{@link Source#getPreviousTag(int pos, TagType)}
 *  <li>{@link Source#getNextStartTag(int pos)}
 *  <li>{@link Source#getNextStartTag(int pos, String name)}
 *  <li>{@link Source#getNextStartTag(int pos, String attributeName, String value, boolean valueCaseSensitive)}
 *  <li>{@link Source#getNextTag(int pos)}
 *  <li>{@link Source#getNextTag(int pos, TagType)}
 *  <li>{@link Source#getEnclosingTag(int pos)}
 *  <li>{@link Source#getEnclosingTag(int pos, TagType)}
 *  <li>{@link Source#getTagAt(int pos)}
 *  <li>{@link Segment#getAllStartTags()}
 *  <li>{@link Segment#getAllStartTags(String name)}
 *  <li>{@link Segment#getAllStartTags(String attributeName, String value, boolean valueCaseSensitive)}
 *  <li>{@link Segment#getAllTags()}
 *  <li>{@link Segment#getAllTags(TagType)}
 * </ul>
 * <p>
 * The methods above which accept a <code>name</code> parameter are categorised as <a href="Tag.html#NamedSearch">named search</a> methods.
 * <p>
 * In such methods dealing with start tags, specifying an argument to the <code>name</code> parameter that ends in a
 * colon (<code>:</code>) searches for all start tags in the specified XML namespace.
 * <p>
 * The constants defined in the {@link HTMLElementName} interface can be used directly as arguments to these <code>name</code> parameters.
 * For example, <code>source.getAllStartTags(</code>{@link HTMLElementName#A}<code>)</code> is equivalent to
 * <code>source.getAllStartTags("a")</code>, and gets all hyperlink start tags.
 * <p>
 * The {@link Tag} superclass defines a method called {@link Tag#getName() getName()} to get the name of this start tag.
 * <p>
 * See also the XML 1.0 specification for <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-stag">start tags</a>.
 *
 * @see Tag
 * @see Element
 * @see EndTag
 */
public final class StartTag extends Tag {
	private final Attributes attributes;
	final StartTagType startTagType;

	/**
	 * Constructs a new <code>StartTag</code>.
	 *
	 * @param source  the {@link Source} document.
	 * @param begin  the character position in the source document where this tag {@linkplain Segment#getBegin() begins}.
	 * @param end  the character position in the source document where this tag {@linkplain Segment#getEnd() ends}.
	 * @param startTagType  the {@linkplain #getStartTagType() type} of the start tag.
	 * @param name  the {@linkplain Tag#getName() name} of the tag.
	 * @param attributes  the {@linkplain #getAttributes() attributes} of the tag.
	 */
	StartTag(final Source source, final int begin, final int end, final StartTagType startTagType, final String name, final Attributes attributes) {
		super(source,begin,end,name);
		this.attributes=attributes;
		this.startTagType=startTagType;
	}

	// only used to create Tag.NOT_CACHED
	StartTag() {
		attributes=null;
		startTagType=null;
	}

	/**
	 * Returns the {@linkplain Element element} that is started by this start tag.
	 * Guaranteed not <code>null</code>.
 	 * <p>
	 * <dl>
	 *  <dt>Example 1: Elements for which the {@linkplain HTMLElements#getEndTagRequiredElementNames() end tag is required}</dt>
	 *  <dd>
	 *   <pre>
	 *    1. &lt;div&gt;
	 *    2.   &lt;div&gt;
	 *    3.     &lt;div&gt;
	 *    4.       &lt;div&gt;This is line 4&lt;/div&gt;
	 *    5.     &lt;/div&gt;
	 *    6.     &lt;div&gt;This is line 6&lt;/div&gt;
	 *    7.   &lt;/div&gt;</pre>
	 *   <ul>
	 *    <li>The start tag on line 1 returns an empty element spanning only the start tag.
	 *     This is because the end tag of a <code>&lt;div&gt;</code> element is required,
	 *     making the sample code invalid as all the end tags are matched with other start tags.
	 *    <li>The start tag on line 2 returns an element spanning to the end of line 7.
	 *    <li>The start tag on line 3 returns an element spanning to the end of line 5.
	 *    <li>The start tag on line 4 returns an element spanning to the end of line 4.
	 *    <li>The start tag on line 6 returns an element spanning to the end of line 6.
	 *   </ul>
	 *   <p>
	 *  </dd>
	 *  <dt>Example 2: Elements for which the {@linkplain HTMLElements#getEndTagOptionalElementNames() end tag is optional}</dt>
	 *  <dd>
	 *   <pre>
	 *    1. &lt;ul&gt;
	 *    2.   &lt;li&gt;item 1
	 *    3.   &lt;li&gt;item 2
	 *    4.     &lt;ul&gt;
	 *    5.       &lt;li&gt;subitem 1&lt;/li&gt;
	 *    6.       &lt;li&gt;subitem 2
	 *    7.     &lt;/ul&gt;
	 *    8.   &lt;li&gt;item 3&lt;/li&gt;
	 *    9. &lt;/ul&gt;</pre>
	 *   <ul>
	 *    <li>The start tag on line 1 returns an element spanning to the end of line 9.
	 *    <li>The start tag on line 2 returns an element spanning to the start of the <code>&lt;li&gt;</code> start tag on line 3.
	 *    <li>The start tag on line 3 returns an element spanning to the start of the <code>&lt;li&gt;</code> start tag on line 8.
	 *    <li>The start tag on line 4 returns an element spanning to the end of line 7.
	 *    <li>The start tag on line 5 returns an element spanning to the end of line 5.
	 *    <li>The start tag on line 6 returns an element spanning to the start of the <code>&lt;/ul&gt;</code> end tag on line 7.
	 *    <li>The start tag on line 8 returns an element spanning to the end of line 8.
	 *   </ul>
	 *  </dd>
	 * </dl>
	 *
	 * @return the {@linkplain Element element} that is started by this start tag.
	 */
	public Element getElement() {
		if (element==Element.NOT_CACHED) {
			final EndTag endTag=getEndTagInternal();
			element=new Element(source,this,endTag);
			if (endTag!=null) {
				if (endTag.element!=Element.NOT_CACHED) {
					// This is presumably impossible, except in certain circumstances where the cache was cleared, such as if the parser decides to do a full sequential parse after some tags have already been found.
					// If the existing element and the current element are not the same, log it.
					if (source.logger.isInfoEnabled() && !element.equals(endTag.element)) source.logger.info(source.getRowColumnVector(endTag.begin).appendTo(new StringBuilder(200).append("End tag ").append(endTag).append(" at ")).append(" terminates more than one element").toString()); 
				}
				endTag.element=element;
			}
		}
		return element;
	}

	/**
	 * Indicates whether this start tag is an <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-eetag">empty-element tag</a>.
	 * <p>
	 * This property checks that the the tag is {@linkplain #isSyntacticalEmptyElementTag() syntactically an empty-element tag},
	 * but in addition checks that the {@linkplain #getName() name} of the tag is not one that is defined in the HTML specification to have a
	 * {@linkplain HTMLElements#getEndTagRequiredElementNames() required} or {@linkplain HTMLElements#getEndTagOptionalElementNames() optional} end tag,
	 * which the major browsers do not recognise as empty-element tags, even in an <a target="_blank" href="http://www.w3.org/TR/xhtml1/">XHTML</a> document.
	 * <p>
	 * This is equivalent to:<br />
	 * {@link #isSyntacticalEmptyElementTag()}<code> && !(</code>{@link HTMLElements#getEndTagOptionalElementNames()}<code>.contains(</code>{@link #getName() getName()}<code>) || </code>{@link HTMLElements#getEndTagRequiredElementNames()}<code>.contains(</code>{@link #getName() getName()}<code>))</code>.
	 *
	 * @return <code>true</code> if this start tag is an <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-eetag">empty-element tag</a>, otherwise <code>false</code>.
	 */
	public boolean isEmptyElementTag() {
		return isSyntacticalEmptyElementTag() && !HTMLElements.isClosingSlashIgnored(name);
	}

	/**
	 * Indicates whether this start tag is syntactically an <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-eetag">empty-element tag</a>.
	 * <p>
	 * This is signified by the characters "/&gt;" at the end of the start tag.
	 * <p>
	 * Only a {@linkplain StartTagType#NORMAL normal} start tag can be syntactically an empty-element tag.
	 * <p>
	 * This property simply reports whether the syntax of the start tag is consistent with that of an empty-element tag,
	 * it does not guarantee that this start tag's {@linkplain #getElement() element} is actually {@linkplain Element#isEmpty() empty}.
	 * <p>
	 * This possible discrepancy reflects the way major browsers interpret illegal empty element tags used in
	 * <a href="HTMLElements.html#HTMLElement">HTML elements</a>, and is explained further in the documentation of the
	 * {@link #isEmptyElementTag()} property.
	 *
	 * @return <code>true</code> if this start tag is syntactically an <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-eetag">empty-element tag</a>, otherwise <code>false</code>.
	 * @see #isEmptyElementTag()
	 */
	public boolean isSyntacticalEmptyElementTag() {
		return startTagType==StartTagType.NORMAL && source.charAt(end-2)=='/';
	}

	/**
	 * Returns the {@linkplain StartTagType type} of this start tag.	
	 * <p>
	 * This is equivalent to <code>(StartTagType)</code>{@link #getTagType()}.
	 *
	 * @return the {@linkplain StartTagType type} of this start tag.	
	 */
	public StartTagType getStartTagType() {
		return startTagType;
	}

	// Documentation inherited from Tag
	public TagType getTagType() {
		return startTagType;
	}

	/**
	 * Returns the attributes specified in this start tag.
	 * <p>
	 * Return value is not <code>null</code> if and only if
	 * {@link #getStartTagType()}<code>.</code>{@link StartTagType#hasAttributes() hasAttributes()}<code>==true</code>.
	 * <p>
	 * To force the parsing of attributes in other start tag types, use the {@link #parseAttributes()} method instead.
	 *
	 * @return the attributes specified in this start tag, or <code>null</code> if the {@linkplain #getStartTagType() type} of this start tag does not {@linkplain StartTagType#hasAttributes() have attributes}.
	 * @see #parseAttributes()
	 * @see Source#parseAttributes(int pos, int maxEnd)
	 */
	public Attributes getAttributes() {
		return attributes;
	}

	/**
	 * Returns the {@linkplain CharacterReference#decode(CharSequence) decoded} value of the attribute with the specified name (case insensitive).
	 * <p>
	 * Returns <code>null</code> if this start tag does not {@linkplain StartTagType#hasAttributes() have attributes},
	 * no attribute with the specified name exists or the attribute {@linkplain Attribute#hasValue() has no value}.
	 * <p>
	 * This is equivalent to {@link #getAttributes()}<code>.</code>{@link Attributes#getValue(String) getValue(attributeName)},
	 * except that it returns <code>null</code> if this start tag does not have attributes instead of throwing a
	 * <code>NullPointerException</code>.
	 *
	 * @param attributeName  the name of the attribute to get.
	 * @return the {@linkplain CharacterReference#decode(CharSequence) decoded} value of the attribute with the specified name, or <code>null</code> if the attribute does not exist or {@linkplain Attribute#hasValue() has no value}.
	 */
	public String getAttributeValue(final String attributeName) {
		return attributes==null ? null : attributes.getValue(attributeName);
	}

	/**
	 * Parses the attributes specified in this start tag, regardless of the type of start tag.
	 * This method is only required in the unusual situation where attributes exist in a start tag whose 
	 * {@linkplain #getStartTagType() type} doesn't {@linkplain StartTagType#hasAttributes() have attributes}.
	 * <p>
	 * This method returns the cached attributes from the {@link StartTag#getAttributes()} method
	 * if its value is not <code>null</code>, otherwise the source is physically parsed with each call to this method.
	 * <p>
	 * This is equivalent to {@link #parseAttributes(int) parseAttributes}<code>(</code>{@link Attributes#getDefaultMaxErrorCount()}<code>)}</code>.
	 *
	 * @return the attributes specified in this start tag, or <code>null</code> if too many errors occur while parsing.
	 * @see #getAttributes()
	 * @see Source#parseAttributes(int pos, int maxEnd)
	 */
	public Attributes parseAttributes() {
		return parseAttributes(Attributes.getDefaultMaxErrorCount());
	}

	/**
	 * Parses the attributes specified in this start tag, regardless of the type of start tag.
	 * This method is only required in the unusual situation where attributes exist in a start tag whose 
	 * {@linkplain #getStartTagType() type} doesn't {@linkplain StartTagType#hasAttributes() have attributes}.
	 * <p>
	 * See the documentation of the {@link #parseAttributes()} method for more information.
	 *
	 * @param maxErrorCount  the maximum number of minor errors allowed while parsing
	 * @return the attributes specified in this start tag, or <code>null</code> if too many errors occur while parsing.
	 * @see #getAttributes()
	 */
	public Attributes parseAttributes(final int maxErrorCount) {
		if (attributes!=null) return attributes;
		final int maxEnd=end-startTagType.getClosingDelimiter().length();
		int attributesBegin=begin+1+name.length();
		// skip any non-name characters directly after the name (which are quite common)
		while (!isXMLNameStartChar(source.charAt(attributesBegin))) {
			attributesBegin++;
			if (attributesBegin==maxEnd) return null;
		}
		return Attributes.construct(source,begin,attributesBegin,maxEnd,startTagType,name,maxErrorCount);
	}

	/**
	 * Returns the segment between the end of the tag's {@linkplain #getName() name} and the start of its <a href="#EndDelimiter">end delimiter</a>.
	 * <p>
	 * This method is normally only of use for start tags whose content is something other than {@linkplain #getAttributes() attributes}.
	 * <p>
	 * A new {@link Segment} object is created with each call to this method.
	 *
	 * @return the segment between the end of the tag's {@linkplain #getName() name} and the start of the <a href="#EndDelimiter">end delimiter</a>.
	 */
	public Segment getTagContent() {
		return new Segment(source,begin+1+name.length(),end-startTagType.getClosingDelimiter().length());
	}

	/**
	 * Returns the {@link FormControl} defined by this start tag.
	 * <p>
	 * This is equivalent to {@link #getElement()}<code>.</code>{@link Element#getFormControl() getFormControl()}.
	 *
	 * @return the {@link FormControl} defined by this start tag, or <code>null</code> if it is not a <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#form-controls">control</a>.
	 */
	public FormControl getFormControl() {
		return getElement().getFormControl();
	}

	/**
	 * Indicates whether a matching end tag is forbidden.
	 * <p>
	 * This property returns <code>true</code> if one of the following conditions is met:
	 * <ul>
	 *  <li>The {@linkplain #getStartTagType() type} of this start tag does not specify a
	 *   {@linkplain StartTagType#getCorrespondingEndTagType() corresponding end tag type}.
	 *  <li>The {@linkplain #getName() name} of this start tag indicates it is the start of an
	 *   <a href="Element.html#HTML">HTML element</a> whose {@linkplain HTMLElements#getEndTagForbiddenElementNames() end tag is forbidden}.
	 *  <li>This start tag is {@linkplain #isSyntacticalEmptyElementTag() syntactically an empty-element tag} and its
	 *   {@linkplain #getName() name} indicates it is the start of a <a href="HTMLElements.html#NonHTMLElement">non-HTML element</a>.
	 * </ul>
	 * <p>
	 * If this property returns <code>true</code> then this start tag's {@linkplain #getElement() element} will always be a
	 * <a href="Element.html#SingleTag">single tag element</a>.
	 *
	 * @return  <code>true</code> if a matching end tag is forbidden, otherwise <code>false</code>.
	 */
	public boolean isEndTagForbidden() {
		if (getStartTagType()!=StartTagType.NORMAL)
			return getStartTagType().getCorrespondingEndTagType()==null;
		if (HTMLElements.getEndTagForbiddenElementNames().contains(name)) return true;
		if (HTMLElements.getElementNames().contains(name)) return false;
		return isSyntacticalEmptyElementTag();
	}

	/**
	 * Indicates whether a matching end tag is required.
	 * <p>
	 * This property returns <code>true</code> if one of the following conditions is met:
	 * <ul>
	 *  <li>The {@linkplain #getStartTagType() type} of this start tag is NOT {@link StartTagType#NORMAL}, but specifies a
	 *   {@linkplain StartTagType#getCorrespondingEndTagType() corresponding end tag type}.
	 *  <li>The {@linkplain #getName() name} of this start tag indicates it is the start of an
	 *   <a href="Element.html#HTML">HTML element</a> whose {@linkplain HTMLElements#getEndTagRequiredElementNames() end tag is required}.
	 *  <li>This start tag is NOT {@linkplain #isSyntacticalEmptyElementTag() syntactically an empty-element tag} and its
	 *   {@linkplain #getName() name} indicates it is the start of a <a href="HTMLElements.html#NonHTMLElement">non-HTML element</a>.
	 * </ul>
	 *
	 * @return  <code>true</code> if a matching end tag is required, otherwise <code>false</code>.
	 */
	public boolean isEndTagRequired() {
		if (getStartTagType()!=StartTagType.NORMAL)
			return getStartTagType().getCorrespondingEndTagType()!=null;
		if (HTMLElements.getEndTagRequiredElementNames().contains(name)) return true;
		if (HTMLElements.getElementNames().contains(name)) return false;
		return !isSyntacticalEmptyElementTag();
	}

	// Documentation inherited from Tag
	public boolean isUnregistered() {
		return startTagType==StartTagType.UNREGISTERED;
	}

	/**
	 * Returns an XML representation of this start tag.
	 * <p>
	 * This is equivalent to {@link #tidy(boolean) tidy(false)}, thereby keeping the {@linkplain #getName() name} of the tag in its original case.
	 * <p>
	 * See the documentation of the {@link #tidy(boolean toXHTML)} method for more details.
	 *
	 * @return an XML representation of this start tag, or the {@linkplain Segment#toString() source text} if it is of a {@linkplain #getStartTagType() type} that does not {@linkplain StartTagType#hasAttributes() have attributes}.
	 */
	public String tidy() {
		return tidy(false);
	}

	/**
	 * Returns an XML or XHTML representation of this start tag.
	 * <p>
	 * The tidying of the tag is carried out as follows:
	 * <ul>
	 *  <li>if this start tag is of a {@linkplain #getStartTagType() type} that does not {@linkplain StartTagType#hasAttributes() have attributes},
	 *   then the original {@linkplain Segment#toString() source text} of the enture tag is returned.
	 *  <li>if this start tag contain any {@linkplain TagType#isServerTag() server tags} outside of an attribute value,
	 *   then the original {@linkplain Segment#toString() source text} of the entire tag is returned.
	 *  <li>name converted to lower case if the <code>toXHTML</code> argument is <code>true</code> and this is a {@linkplain StartTagType#NORMAL normal} start tag
	 *  <li>attributes separated by a single space
	 *  <li>attribute names in original case
	 *  <li>attribute values are enclosed in double quotes and {@linkplain CharacterReference#reencode(CharSequence) re-encoded}
	 *  <li>if this start tag forms an <a href="Element.html#HTML">HTML element</a> that has no {@linkplain Element#getEndTag() end tag},
	 *   a slash is inserted before the closing angle bracket, separated from the {@linkplain #getName() name} or last attribute by a single space.
	 *  <li>if an attribute value contains a {@linkplain TagType#isServerTag() server tag} it is inserted verbatim instead of being
	 *   {@linkplain CharacterReference#encode(CharSequence) encoded}.
	 * </ul>
	 * <p>
	 * The <code>toXHTML</code> parameter determines only whether the name is converted to lower case for {@linkplain StartTagType#NORMAL normal} tags.
	 * In all other respects the generated tag is already valid XHTML.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd>
	 *   <p>
	 *   The following source text:
	 *   <blockquote class="code">
	 *    <code>&lt;INPUT name=Company value='G&amp;uuml;nter O&amp#39;Reilly &amp;amp Associ&eacute;s'&gt;</code>
	 *   </blockquote>
	 *   produces the following regenerated HTML:
	 *   <blockquote class="code">
	 *    <code>&lt;input name="Company" value="G&amp;uuml;nter O'Reilly &amp;amp; Associ&amp;eacute;s" /&gt;</code>
	 *   </blockquote>
	 *  </dd>
	 * </dl>
	 *
	 * @param toXHTML  specifies whether the output is XHTML.
	 * @return an XML or XHTML representation of this start tag, or the {@linkplain Segment#toString() source text} if it is of a {@linkplain #getStartTagType() type} that does not {@linkplain StartTagType#hasAttributes() have attributes}.
	 */
	public String tidy(boolean toXHTML) {
		if (attributes==null || attributes.containsServerTagOutsideOfAttributeValue) return toString();
		final StringBuilder sb=new StringBuilder();
		sb.append('<');
		if (toXHTML && startTagType==StartTagType.NORMAL) {
			sb.append(name);
		} else {
			int i=begin+startTagType.startDelimiterPrefix.length();
			final int nameSegmentEnd=i+name.length();
			while (i<nameSegmentEnd) {
				sb.append(source.charAt(i));
				i++;
			}
		}
		try {
			attributes.appendTidy(sb,getNextTag());
		} catch (IOException ex) {throw new RuntimeException(ex);} // never happens
		if (startTagType==StartTagType.NORMAL && getElement().getEndTag()==null && !HTMLElements.getEndTagOptionalElementNames().contains(name)) sb.append(" /");
		sb.append(startTagType.getClosingDelimiter());
		return sb.toString();
	}

	/**
	 * Generates the HTML text of a {@linkplain StartTagType#NORMAL normal} start tag with the specified tag name and {@linkplain Attributes#populateMap(Map,boolean) attributes map}.
	 * <p>
	 * The output of the attributes is as described in the {@link Attributes#generateHTML(Map attributesMap)} method.
	 * <p>
	 * The <code>emptyElementTag</code> parameter specifies whether the start tag should be an
	 * <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-eetag">empty-element tag</a>,
	 * in which case a slash is inserted before the closing angle bracket, separated from the name
	 * or last attribute by a single space.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd>
	 *   <p>
	 *   The following code:
	 *   <blockquote class="code">
	 * <pre>
	 * LinkedHashMap attributesMap=new LinkedHashMap();
	 * attributesMap.put("name","Company");
	 * attributesMap.put("value","G\n00fcnter O'Reilly & Associ&eacute;s");
	 * System.out.println(StartTag.generateHTML("INPUT",attributesMap,true));</pre>
	 *   </blockquote>
	 *   generates the following output:
	 *   <blockquote class="code">
	 *    <code>&lt;INPUT name="Company" value="G&amp;uuml;nter O'Reilly &amp;amp; Associ&amp;eacute;s" /&gt;</code>
	 *   </blockquote>
	 *  </dd>
	 * </dl>
	 *
	 * @param tagName  the name of the start tag.
	 * @param attributesMap  a map containing attribute name/value pairs.
	 * @param emptyElementTag  specifies whether the start tag should be an <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-eetag">empty-element tag</a>.
	 * @return the HTML text of a {@linkplain StartTagType#NORMAL normal} start tag with the specified tag name and {@linkplain Attributes#populateMap(Map,boolean) attributes map}.
	 * @see EndTag#generateHTML(String tagName)
	 */
	public static String generateHTML(final String tagName, final Map<String,String> attributesMap, final boolean emptyElementTag) {
		final StringBuilder sb=new StringBuilder();
		sb.append('<').append(tagName);
		try {
			Attributes.appendHTML(sb,attributesMap);
		} catch (IOException ex) {throw new RuntimeException(ex);} // never happens
		if (emptyElementTag)
			sb.append(" />");
		else
			sb.append('>');
		return sb.toString();
	}

	public String getDebugInfo() {
		final StringBuilder sb=new StringBuilder();
		appendDebugTag(sb);
		sb.append(' ');
		appendDebugTagType(sb);
		sb.append(super.getDebugInfo());
		return sb.toString();
	}

	StringBuilder appendDebugTag(final StringBuilder sb) {
		if (startTagType==StartTagType.NORMAL && getAttributes().isEmpty()) {
			sb.append(this);
		} else {
			sb.append('<').append(getNameSegment()).append(' ');
			if (isSyntacticalEmptyElementTag()) sb.append('/');
			sb.append(startTagType.getClosingDelimiter());
		}
		return sb;
	}

	StringBuilder appendDebugTagType(final StringBuilder sb) {
		if (startTagType!=StartTagType.NORMAL) sb.append('(').append(startTagType.getDescription()).append(") ");
		return sb;
	}

	private EndTag getEndTagInternal() {
		boolean checkForEmptyElementTag=true;
		// A missing optional end tag returns a zero length EndTag instead of null
		final EndTagType endTagType=startTagType.getCorrespondingEndTagType();
		if (startTagType==StartTagType.NORMAL) {
			final HTMLElementTerminatingTagNameSets terminatingTagNameSets=HTMLElements.getTerminatingTagNameSets(name);
			if (terminatingTagNameSets!=null) // end tag is optional
				return getOptionalEndTag(terminatingTagNameSets);
			if (HTMLElements.getEndTagForbiddenElementNames().contains(name)) // end tag is forbidden
				return null;
			checkForEmptyElementTag=!HTMLElements.getEndTagRequiredElementNames().contains(name); // check for empty-element tags if tag is not an HTML element
			if (checkForEmptyElementTag && isSyntacticalEmptyElementTag()) // non-html empty-element tag
				return null; 
		} else if (endTagType==null) {
			return null;
		}
		// This is either a start tag type other than NORMAL that requires an end tag, or an HTML element tag that requires an end tag,
		// or a non-HTML element tag that is not an empty-element tag.
		// In all of these cases the end tag is required.
		final EndTag nextEndTag=source.getNextEndTag(end,endTagType.getEndTagName(name),endTagType);
		if (nextEndTag!=null) {
			if (startTagType==StartTagType.NORMAL && HTMLElements.END_TAG_REQUIRED_NESTING_FORBIDDEN_SET.contains(name)) {
				final StartTag nextStartTag=source.getNextStartTag(end,name);
				if (nextStartTag==null || nextStartTag.begin>nextEndTag.begin) return nextEndTag;
				if (source.logger.isInfoEnabled()) source.logger.info(source.getRowColumnVector(begin).appendTo(new StringBuilder(200).append("StartTag at ")).append(" missing required end tag - invalid nested start tag encountered before end tag").toString());
				// Terminate the element at the start of the invalidly nested start tag.
				// This is how IE and Mozilla treat illegally nested A elements, but other elements may vary.
				return new EndTag(source,nextStartTag.begin,nextStartTag.begin,EndTagType.NORMAL,name);
			}
			final Segment[] getResult=getEndTag(nextEndTag,checkForEmptyElementTag,Tag.isXMLName(name));
			if (getResult!=null) return (EndTag)getResult[0];
		}
		if (source.logger.isInfoEnabled()) source.logger.info(source.getRowColumnVector(begin).appendTo(new StringBuilder(200).append("StartTag at ")).append(" missing required end tag").toString());
		return null;
	}

	private EndTag getOptionalEndTag(final HTMLElementTerminatingTagNameSets terminatingTagNameSets) {
		int pos=end;
		while (pos<source.end) {
			final Tag tag=Tag.getNextTag(source,pos);
			if (tag==null) break;
			Set terminatingTagNameSet;
			if (tag instanceof EndTag) {
				if (tag.name==name) return (EndTag)tag;
				terminatingTagNameSet=terminatingTagNameSets.TerminatingEndTagNameSet;
			} else {
				terminatingTagNameSet=terminatingTagNameSets.NonterminatingElementNameSet;
				if (terminatingTagNameSet!=null && terminatingTagNameSet.contains(tag.name)) {
					Element nonterminatingElement=((StartTag)tag).getElement();
					pos=nonterminatingElement.end;
					continue;
				}
				terminatingTagNameSet=terminatingTagNameSets.TerminatingStartTagNameSet;
			}
			if (terminatingTagNameSet!=null && terminatingTagNameSet.contains(tag.name)) return new EndTag(source,tag.begin,tag.begin,EndTagType.NORMAL,name);
			pos=tag.begin+1;
		}
		// Ran out of tags. The only legitimate case of this happening is if the HTML end tag is missing, in which case the end of the element is the end of the source document
		return new EndTag(source,source.end,source.end,EndTagType.NORMAL,name);
	}

	static String getStartDelimiter(final String searchName) {
		if (searchName.length()==0) throw new IllegalArgumentException("searchName argument must not be zero length");
		final String startDelimiter=StartTagType.START_DELIMITER_PREFIX+searchName;
		if (startDelimiter.charAt(StartTagType.START_DELIMITER_PREFIX.length())=='/') throw new IllegalArgumentException("searchName argument \""+searchName+"\" must not start with '/'");
		return startDelimiter;
	}

	static StartTag getPrevious(final Source source, final int pos, final String searchName, final StartTagType searchStartTagType) {
		return getPrevious(source,pos,searchName,searchStartTagType,searchStartTagType==StartTagType.NORMAL ? Tag.isXMLName(searchName) : true);
	}

	static StartTag getPrevious(final Source source, final int pos, final String searchName, final StartTagType searchStartTagType, final boolean isXMLTagName) {
		// searchName is already in lower case, but may be null
		// searchStartTagType must not be null
		// isXMLTagName is only used if searchStartTagType==StartTagType.NORMAL
		if (searchName==null) return (StartTag)source.getPreviousTag(pos,searchStartTagType);
		final String startDelimiter=getStartDelimiter(searchName);
		try {
			final ParseText parseText=source.getParseText();
			int begin=pos;
			do {
				begin=parseText.lastIndexOf(startDelimiter,begin);
				if (begin==-1) return null;
				final StartTag startTag=(StartTag)Tag.getTagAt(source,begin,false);
				if (startTag==null) continue; // keep looking if it wasn't a start tag
				if (searchStartTagType!=startTag.getStartTagType()) {
					// The start tag is of the wrong type.  The only case in which we want to return it is if
					// we are looking for a normal start tag, the found start tag is unregistered, and the search name is NOT a valid XML name.
					// This allows users to search for some types of unregistered tags by name rather than having to register custom tag types.
					if (searchStartTagType!=StartTagType.NORMAL || isXMLTagName || !startTag.isUnregistered()) continue;
				}
				if (startTag.getStartTagType().isNameAfterPrefixRequired() && startTag.getName().length()>searchName.length()) {
					// The name of the start tag is longer than the search name, and the type of tag indicates 
					// that we are probably looking for an exact match.
					// (eg searchName="a", startTag.name="applet" -> reject)
					// We only require an exact match if the last character of the search name is part of the name, as the
					// search name might be just the prefix of a server tag.
					// (eg searchName="?", startTag.name="?abc" -> accept, but searchName="?a", startTag.name="?abc" -> reject)
					// The only exception to this is if the last character of the search name is a colon (which also forms part of
					// the name), but signifies that we want to search on the entire namespace.
					// (eg searchName="o:", startTag.name="o:p" -> accept)
					char lastSearchNameChar=searchName.charAt(searchName.length()-1);
					if (lastSearchNameChar!=':' && isXMLNameChar(lastSearchNameChar)) continue;
				}
				return startTag;
			} while ((begin-=2)>=0);
		} catch (IndexOutOfBoundsException ex) {
			// this should never happen during a get previous operation so rethrow it:
			throw ex;
		}
		return null;
	}

	static StartTag getNext(final Source source, final int pos, final String searchName, final StartTagType searchStartTagType) {
		return getNext(source,pos,searchName,searchStartTagType,searchStartTagType==StartTagType.NORMAL ? Tag.isXMLName(searchName) : true);
	}

	static StartTag getNext(final Source source, final int pos, final String searchName, final StartTagType searchStartTagType, final boolean isXMLTagName) {
		// searchName is already in lower case, but may be null
		// searchStartTagType must not be null
		// isXMLTagName is only used if searchStartTagType==StartTagType.NORMAL
		if (searchName==null) return (StartTag)source.getNextTag(pos,searchStartTagType);
		final String startDelimiter=getStartDelimiter(searchName);
		try {
			final ParseText parseText=source.getParseText();
			int begin=pos;
			do {
				begin=parseText.indexOf(startDelimiter,begin);
				if (begin==-1) return null;
				final StartTag startTag=(StartTag)Tag.getTagAt(source,begin,false);
				if (startTag==null) continue; // keep looking if it wasn't a start tag
				if (searchStartTagType!=startTag.getStartTagType()) {
					// The start tag is of the wrong type.  The only case in which we want to return it is if
					// we are looking for a normal start tag, the found start tag is unregistered, and the search name is NOT a valid XML name.
					// This allows users to search for some types of unregistered tags by name rather than having to register custom tag types.
					if (searchStartTagType!=StartTagType.NORMAL || isXMLTagName || !startTag.isUnregistered()) continue;
				}
				if (startTag.getStartTagType().isNameAfterPrefixRequired() && startTag.getName().length()>searchName.length()) {
					// The name of the start tag is longer than the search name, and the type of tag indicates 
					// that we are probably looking for an exact match.
					// (eg searchName="a", startTag.name="applet" -> reject)
					// We only require an exact match if the last character of the search name is part of the name, as the
					// search name might be just the prefix of a server tag.
					// (eg searchName="?", startTag.name="?abc" -> accept, but searchName="?a", startTag.name="?abc" -> reject)
					// The only exception to this is if the last character of the search name is a colon (which also forms part of
					// the name), but signifies that we want to search on the entire namespace.
					// (eg searchName="o:", startTag.name="o:p" -> accept)
					char lastSearchNameChar=searchName.charAt(searchName.length()-1);
					if (lastSearchNameChar!=':' && isXMLNameChar(lastSearchNameChar)) continue;
				}
				return startTag;
			} while ((begin+=1)<source.end);
		} catch (IndexOutOfBoundsException ex) {
			// this should only happen when the end of file is reached in the middle of a tag.
			// we don't have to do anything to handle it as there are no more tags anyway.
		}
		return null;
	}

	static StartTag getPrevious(final Source source, int pos) {
		Tag tag=Tag.getPreviousTag(source,pos);
		if (tag==null) return null;
		if (tag instanceof StartTag) return (StartTag)tag;
		return tag.getPreviousStartTag();
	}

	static StartTag getNext(final Source source, int pos) {
		Tag tag=Tag.getNextTag(source,pos);
		if (tag==null) return null;
		if (tag instanceof StartTag) return (StartTag)tag;
		return tag.getNextStartTag();
	}

	static StartTag getNext(final Source source, final int pos, final String attributeName, final String value, final boolean valueCaseSensitive) {
		if (value==null || attributeName.length()==0) throw new IllegalArgumentException();
		// Determine whether to perform the text search on the name or value:
		// - perform the text search on the value if it is >= 3 chars long.
		// - have to perform the text search on the name if the value is zero length.
		// - perform the text search on the name if the name >= 3 chars long, otherwise on the value.
		final String searchString=value.length()>=3 || (value.length()>0 && attributeName.length()<3) ? value : attributeName;
		final ParseText parseText=source.getParseText();
		int searchPos=pos;
		while (searchPos<source.end) {
			searchPos=parseText.indexOf(searchString.toLowerCase(),searchPos);
			if (searchPos==-1) return null;
			final Tag tag=source.getEnclosingTag(searchPos);
			if (tag==null || !(tag instanceof StartTag)) {
				searchPos++;
				continue;
			}
			if (tag.begin>=pos) {
				final StartTag startTag=(StartTag)tag;
				if (startTag.getAttributes()!=null) {
					final String attributeValue=startTag.getAttributes().getValue(attributeName);
					if (attributeValue!=null) {
						if (value.equals(attributeValue)) return startTag;
						if (value.equalsIgnoreCase(attributeValue)) {
							if (!valueCaseSensitive) return startTag;
							if (source.logger.isInfoEnabled()) source.logger.info(source.getRowColumnVector(searchPos).appendTo(new StringBuilder(200)).append(": StartTag with attribute ").append(attributeName).append("=\"").append(attributeValue).append("\" ignored during search because its case does not match search value \"").append(value).append('"').toString());
						}
					}
				}
			}
			searchPos=tag.end;
		}
		return null;
	}

	static StartTag getNext(final Source source, final int pos, final String attributeName, final Pattern regexPattern) {
		if (attributeName==null || attributeName.length()==0) throw new IllegalArgumentException();
		final String searchString=attributeName;
		final ParseText parseText=source.getParseText();
		int searchPos=pos;
		while (searchPos<source.end) {
			searchPos=parseText.indexOf(searchString.toLowerCase(),searchPos);
			if (searchPos==-1) return null;
			final Tag tag=source.getEnclosingTag(searchPos);
			if (tag==null || !(tag instanceof StartTag)) {
				searchPos++;
				continue;
			}
			if (tag.begin>=pos) {
				final StartTag startTag=(StartTag)tag;
				if (startTag.getAttributes()!=null) {
					final Attribute attribute=startTag.getAttributes().get(attributeName);
					if (attribute!=null) {
						if (regexPattern==null) return startTag;
						final String attributeValue=attribute.getValue();
						if (attributeValue!=null && regexPattern.matcher(attributeValue).matches()) return startTag;
					}
				}
			}
			searchPos=tag.end;
		}
		return null;
	}

	private Segment[] getEndTag(final EndTag nextEndTag, final boolean checkForEmptyElementTag, final boolean isXMLTagName) {
		assert nextEndTag!=null;
		StartTag nextStartTag=getNext(source,end,name,startTagType,isXMLTagName);
		if (checkForEmptyElementTag) {
			while (nextStartTag!=null && nextStartTag.isSyntacticalEmptyElementTag())
				nextStartTag=getNext(source,nextStartTag.end,name,startTagType,isXMLTagName);
		}
		return getEndTag(end,nextStartTag,nextEndTag,checkForEmptyElementTag,isXMLTagName);
	}

	private Segment[] getEndTag(final int afterPos, final StartTag nextStartTag, final EndTag nextEndTag, final boolean checkForEmptyElementTag, final boolean isXMLTagName) {
		// returns null if no end tag exists in the rest of the file, otherwise the following two segments:
		// first is the matching end tag to this start tag.  Must be present if array is returned.
		// second is the next occurrence after the returned end tag of a start tag of the same name. (null if none exists)
		if (nextEndTag==null) return null;  // no end tag in the rest of the file
		final Segment[] returnArray={nextEndTag,nextStartTag};
		if (nextStartTag==null || nextStartTag.begin>nextEndTag.begin) return returnArray;  // no more start tags of the same name in rest of file, or they occur after the end tag that we found.  This means we have found the matching end tag.
		final Segment[] getResult=nextStartTag.getEndTag(nextEndTag,checkForEmptyElementTag,isXMLTagName);  // get the matching end tag to the interloping start tag
		if (getResult==null) return null;  // no end tag in the rest of the file
		final EndTag nextStartTagsEndTag=(EndTag)getResult[0];
		final EndTag nextNextEndTag=EndTag.getNext(source,nextStartTagsEndTag.end,nextEndTag.getName(),nextEndTag.getEndTagType()); // get end tag after the interloping start tag's end tag
		return getEndTag(nextStartTagsEndTag.end,(StartTag)getResult[1],nextNextEndTag,checkForEmptyElementTag,isXMLTagName);  // recurse to see if this is the matching end tag
	}
}

