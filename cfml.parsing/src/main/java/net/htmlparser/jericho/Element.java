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
 * Represents an <a target="_blank" href="http://www.w3.org/TR/html401/intro/sgmltut.html#h-3.2.1">element</a>
 * in a specific {@linkplain Source source} document, which encompasses a {@linkplain #getStartTag() start tag},
 * an optional {@linkplain #getEndTag() end tag} and all {@linkplain #getContent() content} in between.
 * <p>
 * Take the following HTML segment as an example:
 * <p>
 * <code>&lt;p&gt;This is a sample paragraph.&lt;/p&gt;</code>
 * <p>
 * The whole segment is represented by an <code>Element</code> object.  This is comprised of the {@link StartTag} "<code>&lt;p&gt;</code>",
 * the {@link EndTag} "<code>&lt;/p&gt;</code>", as well as the text in between.
 * An element may also contain other elements between its start and end tags.
 * <p>
 * The term <i><a name="Normal">normal element</a></i> refers to an element having a {@linkplain #getStartTag() start tag}
 * with a {@linkplain StartTag#getStartTagType() type} of {@link StartTagType#NORMAL}.
 * This comprises all {@linkplain HTMLElements HTML elements} and <a href="HTMLElements.html#NonHTMLElement">non-HTML elements</a>.
 * <p>
 * <code>Element</code> instances are obtained using one of the following methods:
 * <ul>
 *  <li>{@link StartTag#getElement()}
 *  <li>{@link EndTag#getElement()}
 *  <li>{@link Segment#getAllElements()}
 *  <li>{@link Segment#getAllElements(String name)}
 *  <li>{@link Segment#getAllElements(StartTagType)}
 * </ul>
 * See also the {@link HTMLElements} class, and the 
 * <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-element">XML 1.0 specification for elements</a>.
 * <h3><a name="Structure">Element Structure</a></h3>
 * <p>
 * The three possible structures of an element are listed below:
 * <dl class="Separated">
 *  <dt><a name="SingleTag">Single Tag Element</a>:
 *   <dd>
 *    Example:<br />
 *    <code>&lt;img src="mypicture.jpg"&gt;</code>
 *    <p>
 *    The element consists only of a single {@linkplain #getStartTag() start tag} and has no {@linkplain #getContent() element content}
 *    (although the start tag itself may have {@linkplain StartTag#getTagContent() tag content}).
 *    <br />{@link #getEndTag()}<code>==null</code>
 *    <br />{@link #isEmpty()}<code>==true</code>
 *    <br />{@link #getEnd() getEnd()}<code>==</code>{@link #getStartTag()}<code>.</code>{@link #getEnd() getEnd()}
 *    <p>
 *    This occurs in the following situations:
 *    <ul class="Unseparated">
 *     <li>An <a href="HTMLElements.html#HTMLElement">HTML element</a> for which the {@linkplain HTMLElements#getEndTagForbiddenElementNames() end tag is forbidden}.
 *     <li>An <a href="HTMLElements.html#HTMLElement">HTML element</a> for which the {@linkplain HTMLElements#getEndTagRequiredElementNames() end tag is required},
 *      but the end tag is not present in the source document.
 *     <li>An <a href="HTMLElements.html#HTMLElement">HTML element</a> for which the {@linkplain HTMLElements#getEndTagOptionalElementNames() end tag is optional},
 *      where the <a href="#ImplicitlyTerminated">implicitly terminating</a> tag is situated immediately after the element's
 *      {@linkplain #getStartTag() start tag}.
 *     <li>An {@linkplain #isEmptyElementTag() empty element tag}
 *     <li>A <a href="HTMLElements.html#NonHTMLElement">non-HTML element</a> that is not an {@linkplain #isEmptyElementTag() empty element tag} but is missing its end tag.
 *     <li>An element with a start tag of a {@linkplain StartTag#getStartTagType() type} that does not define a
 *     {@linkplain StartTagType#getCorrespondingEndTagType() corresponding end tag type}.
 *     <li>An element with a start tag of a {@linkplain StartTag#getStartTagType() type} that does define a
 *     {@linkplain StartTagType#getCorrespondingEndTagType() corresponding end tag type} but is missing its end tag.
 *    </ul>
 *  <dt><a name="ExplicitlyTerminated">Explicitly Terminated Element</a>:
 *   <dd>
 *    Example:<br />
 *    <code>&lt;p&gt;This is a sample paragraph.&lt;/p&gt;</code>
 *    <p>
 *    The element consists of a {@linkplain #getStartTag() start tag}, {@linkplain #getContent() content},
 *    and an {@linkplain #getEndTag() end tag}.
 *    <br />{@link #getEndTag()}<code>!=null</code>.
 *    <br />{@link #isEmpty()}<code>==false</code> (provided the end tag doesn't immediately follow the start tag)
 *    <br />{@link #getEnd() getEnd()}<code>==</code>{@link #getEndTag()}<code>.</code>{@link #getEnd() getEnd()}.
 *    <p>
 *    This occurs in the following situations, assuming the start tag's matching end tag is present in the source document:
 *    <ul class="Unseparated">
 *     <li>An <a href="HTMLElements.html#HTMLElement">HTML element</a> for which the end tag is either
 *      {@linkplain HTMLElements#getEndTagRequiredElementNames() required} or {@linkplain HTMLElements#getEndTagOptionalElementNames() optional}.
 *     <li>A <a href="HTMLElements.html#NonHTMLElement">non-HTML element</a> that is not an {@linkplain #isEmptyElementTag() empty element tag}.
 *     <li>An element with a start tag of a {@linkplain StartTag#getStartTagType() type} that defines a
 *     {@linkplain StartTagType#getCorrespondingEndTagType() corresponding end tag type}.
 *    </ul>
 *  <dt><a name="ImplicitlyTerminated">Implicitly Terminated Element</a>:
 *   <dd>
 *    Example:<br />
 *    <code>&lt;p&gt;This text is included in the paragraph element even though no end tag is present.</code><br />
 *    <code>&lt;p&gt;This is the next paragraph.</code>
 *    <p>
 *    The element consists of a {@linkplain #getStartTag() start tag} and {@linkplain #getContent() content},
 *    but no {@linkplain #getEndTag() end tag}.
 *    <br />{@link #getEndTag()}<code>==null</code>.
 *    <br />{@link #isEmpty()}<code>==false</code>
 *    <br />{@link #getEnd() getEnd()}<code>!=</code>{@link #getStartTag()}<code>.</code>{@link #getEnd() getEnd()}.
 *    <p>
 *    This only occurs in an <a href="HTMLElements.html#HTMLElement">HTML element</a> for which the
 *    {@linkplain HTMLElements#getEndTagOptionalElementNames() end tag is optional}.
 *    <p>
 *    The element ends at the start of a tag which implies the termination of the element, called the <i>implicitly terminating tag</i>.
 *    If the implicitly terminating tag is situated immediately after the element's {@linkplain #getStartTag() start tag},
 *    the element is classed as a <a href="#SingleTag">single tag element</a>.
 *    <p>
 *    See the <a href="Element.html#ParsingRulesHTMLEndTagOptional">element parsing rules for HTML elements with optional end tags</a>
 *    for details on which tags can implicitly terminate a given element.
 *    <p>
 *    See also the documentation of the {@link HTMLElements#getEndTagOptionalElementNames()} method.
 * </dl>
 * <h3><a name="ParsingRules">Element Parsing Rules</a></h3>
 * The following rules describe the algorithm used in the {@link StartTag#getElement()} method to construct an element.
 * The detection of the start tag's matching end tag or other terminating tags always takes into account the possible nesting of elements.
 * <p>
 * <ul class="Separated">
 *  <li>
 *   If the start tag has a {@linkplain StartTag#getStartTagType() type} of {@link StartTagType#NORMAL}:
 *   <ul>
 *    <li>
 *     If the {@linkplain StartTag#getName() name} of the start tag matches one of the
 *     recognised {@linkplain HTMLElementName HTML element names} (indicating an <a href="HTMLElements.html#HTMLElement">HTML element</a>):
 *     <ul>
 *      <li>
 *       <a name="ParsingRulesHTMLEndTagForbidden"></a>
 *       If the end tag for an element of this {@linkplain StartTag#getName() name} is
 *       {@linkplain HTMLElements#getEndTagForbiddenElementNames() forbidden},
 *       the parser does not conduct any search for an end tag and a <a href="#SingleTag">single tag element</a> is created.
 *      <li>
 *       <a name="ParsingRulesHTMLEndTagRequired"></a>
 *       If the end tag for an element of this {@linkplain StartTag#getName() name} is 
 *       {@linkplain HTMLElements#getEndTagRequiredElementNames() required}, the parser searches for the start tag's matching end tag.
 *       <ul class="Unseparated">
 *        <li>
 *         If the matching end tag is found, an <a href="#ExplicitlyTerminated">explicitly terminated element</a> is created.
 *        <li>
 *         If no matching end tag is found, the source document is not valid HTML and the incident is
 *         {@linkplain Source#getLogger() logged} as a missing required end tag.
 *         In this situation a <a href="#SingleTag">single tag element</a> is created.
 *       </ul>
 *      <li>
 *       <a name="ParsingRulesHTMLEndTagOptional"></a>
 *       If the end tag for an element of this {@linkplain StartTag#getName() name} is
 *       {@linkplain HTMLElements#getEndTagOptionalElementNames() optional}, the parser searches not only for the start tag's matching end tag,
 *       but also for any other tag that <a href="#ImplicitlyTerminated">implicitly terminates</a> the element.
 *       <br />For each tag (<i>T2</i>) following the start tag (<i>ST1</i>) of this element (<i>E1</i>):
 *       <ul class="Unseparated">
 *        <li>
 *         If <i>T2</i> is a start tag:
 *         <ul>
 *          <li>
 *           If the {@linkplain StartTag#getName() name} of <i>T2</i> is in the list of
 *           {@linkplain HTMLElements#getNonterminatingElementNames(String) non-terminating element names} for <i>E1</i>,
 *           then continue evaluating tags from the {@linkplain Element#getEnd() end} of <i>T2</i>'s corresponding
 *           {@linkplain StartTag#getElement() element}.
 *          <li>
 *           If the {@linkplain StartTag#getName() name} of <i>T2</i> is in the list of
 *           {@linkplain HTMLElements#getTerminatingStartTagNames(String) terminating start tag names} for <i>E1</i>,
 *           then <i>E1</i> ends at the {@linkplain StartTag#getBegin() beginning} of <i>T2</i>.
 *           If <i>T2</i> follows immediately after <i>ST1</i>, a <a href="#SingleTag">single tag element</a> is created,
 *           otherwise an <a href="#ImplicitlyTerminated">implicitly terminated element</a> is created.
 *         </ul>
 *        <li>
 *         If <i>T2</i> is an end tag:
 *         <ul>
 *          <li>
 *           If the {@linkplain EndTag#getName() name} of <i>T2</i> is the same as that of <i>ST1</i>,
 *           an <a href="#ExplicitlyTerminated">explicitly terminated element</a> is created.
 *          <li>
 *           If the {@linkplain EndTag#getName() name} of <i>T2</i> is in the list of
 *           {@linkplain HTMLElements#getTerminatingEndTagNames(String) terminating end tag names} for <i>E1</i>,
 *           then <i>E1</i> ends at the {@linkplain EndTag#getBegin() beginning} of <i>T2</i>.
 *           If <i>T2</i> follows immediately after <i>ST1</i>, a <a href="#SingleTag">single tag element</a> is created,
 *           otherwise an <a href="#ImplicitlyTerminated">implicitly terminated element</a> is created.
 *         </ul>
 *        <li>
 *         If no more tags are present in the source document, then <i>E1</i> ends at the end of the file, and an
 *         <a href="#ImplicitlyTerminated">implicitly terminated element</a> is created.
 *       </ul>
 *     </ul>
 *     Note that the syntactical indication of an {@linkplain StartTag#isSyntacticalEmptyElementTag() empty-element tag} in the start tag
 *     is ignored when determining the end of <a href="HTMLElements.html#HTMLElement">HTML elements</a>.
 *     See the documentation of the {@link #isEmptyElementTag()} method for more information.
 *    <li>
 *     If the {@linkplain StartTag#getName() name} of the start tag does not match one of the
 *     recognised {@linkplain HTMLElementName HTML element names} (indicating a <a href="HTMLElements.html#NonHTMLElement">non-HTML element</a>):
 *     <ul>
 *      <li>
 *       If the start tag is {@linkplain StartTag#isSyntacticalEmptyElementTag() syntactically an empty-element tag},
 *       the parser does not conduct any search for an end tag and a <a href="#SingleTag">single tag element</a> is created.
 *      <li>
 *       Otherwise, section <a target="_blank" href="http://www.w3.org/TR/REC-xml#CleanAttrVals">3.1</a>
 *       of the XML 1.0 specification states that a matching end tag MUST be present, and
 *       the parser searches for the start tag's matching end tag.
 *       <ul class="Unseparated">
 *        <li>
 *         If the matching end tag is found, an <a href="#ExplicitlyTerminated">explicitly terminated element</a> is created.
 *        <li>
 *         If no matching end tag is found, the source document is not valid XML and the incident is
 *         {@linkplain Source#getLogger() logged} as a missing required end tag.
 *         In this situation a <a href="#SingleTag">single tag element</a> is created.
 *       </ul>
 *     </ul>
 *   </ul>
 *  <li>
 *   If the start tag has any {@linkplain StartTag#getStartTagType() type} other than {@link StartTagType#NORMAL}:
 *   <ul>
 *    <li>
 *     If the start tag's type does not define a {@linkplain StartTagType#getCorrespondingEndTagType() corresponding end tag type},
 *     the parser does not conduct any search for an end tag and a <a href="#SingleTag">single tag element</a> is created.
 *    <li>
 *     If the start tag's type does define a {@linkplain StartTagType#getCorrespondingEndTagType() corresponding end tag type},
 *     the parser assumes that a matching end tag is required and searches for it.
 *     <ul class="Unseparated">
 *      <li>
 *       If the matching end tag is found, an <a href="#ExplicitlyTerminated">explicitly terminated element</a> is created.
 *      <li>
 *       If no matching end tag is found, the missing required end tag is {@linkplain Source#getLogger() logged}
 *       and a <a href="#SingleTag">single tag element</a> is created.
 *     </ul>
 *   </ul>
 * </ul>
 * @see HTMLElements
 */
public final class Element extends Segment {
	private final StartTag startTag;
	private final EndTag endTag;
	private Segment content=null;
	Element parentElement=Element.NOT_CACHED;
	private int depth=-1;
	private List<Element> childElements=null;
	
	static final Element NOT_CACHED=new Element();
	
	private static final boolean INCLUDE_INCORRECTLY_NESTED_CHILDREN_IN_HIERARCHY=true;

	Element(final Source source, final StartTag startTag, final EndTag endTag) {
		super(source, startTag.begin, endTag==null ? startTag.end : endTag.end);
		if (source.isStreamed()) throw new UnsupportedOperationException("Elements are not supported when using StreamedSource");
		this.startTag=startTag;
		this.endTag=(endTag==null || endTag.length()==0) ? null : endTag;
	}

	// used only to construct NOT_CACHED
	private Element() {
		startTag=null;
		endTag=null;
	}

	/**
	 * Returns the parent of this element in the document element hierarchy.
	 * <p>
	 * The {@link Source#fullSequentialParse()} method must be called (either explicitly or implicitly) immediately after construction of the <code>Source</code> object if this method is to be used.
	 * An <code>IllegalStateException</code> is thrown if a full sequential parse has not been performed or if it was performed after this element was found.
	 * <p>
	 * This method returns <code>null</code> for a <a href="Source.html#TopLevelElement">top-level element</a>,
	 * as well as any element formed from a {@linkplain TagType#isServerTag() server tag}, regardless of whether it is nested inside a normal element.
	 * <p>
	 * See the {@link Source#getChildElements()} method for more details.
	 *
	 * @return the parent of this element in the document element hierarchy, or <code>null</code> if this element is a <a href="Source.html#TopLevelElement">top-level element</a>.
	 * @throws IllegalStateException if a {@linkplain Source#fullSequentialParse() full sequential parse} has not been performed or if it was performed after this element was found.
	 * @see #getChildElements()
	 */
	public Element getParentElement() {
		if (parentElement==Element.NOT_CACHED) {
			if (!source.wasFullSequentialParseCalled()) throw new IllegalStateException("This operation is only possible after a full sequential parse has been performed");
			if (startTag.isOrphaned()) throw new IllegalStateException("This operation is only possible if a full sequential parse was performed immediately after construction of the Source object");
			source.getChildElements();
			if (parentElement==Element.NOT_CACHED) parentElement=null;
		}
		return parentElement;
	}

	/**
	 * Returns a list of the immediate children of this element in the document element hierarchy.
	 * <p>
	 * The objects in the list are all of type {@link Element}.
	 * <p>
	 * See the {@link Source#getChildElements()} method for more details.
	 *
	 * @return a list of the immediate children of this element in the document element hierarchy, guaranteed not <code>null</code>.
	 * @see #getParentElement()
	 */
	@Override public final List<Element> getChildElements() {
		return childElements!=null ? childElements : getChildElements(-1);
	}

	final List<Element> getChildElements(int depth) {
		if (depth!=-1) this.depth=depth;
		if (childElements==null) {
			if (!Config.IncludeServerTagsInElementHierarchy && end==startTag.end) {
				childElements=Collections.emptyList();
			} else {
				final int childDepth=(depth==-1 ? -1 : depth+1);
				childElements=new ArrayList<Element>();
				int pos=Config.IncludeServerTagsInElementHierarchy ? begin+1 : startTag.end;
				final int maxChildBegin=(Config.IncludeServerTagsInElementHierarchy || endTag==null) ? end : endTag.begin;
				while (true) {
					final StartTag childStartTag=source.getNextStartTag(pos);
					if (childStartTag==null || childStartTag.begin>=maxChildBegin) break;
					if (Config.IncludeServerTagsInElementHierarchy) {
						if (childStartTag.begin<startTag.end && !childStartTag.getTagType().isServerTag() && !startTag.getTagType().isServerTag()) {
							// A start tag is found within another start tag, but neither is a server tag.
							// This only legitimately happens in very rare cases like entity definitions in doctype.
							// We don't want to include the child elements in the hierarchy.
							pos=childStartTag.end;
							continue;
						}
					} else if (childStartTag.getTagType().isServerTag()) {
						pos=childStartTag.end;
						continue;
					}
					final Element childElement=childStartTag.getElement();
					if (childElement.end>end) {
						if (source.logger.isInfoEnabled()) source.logger.info("Child "+childElement.getDebugInfo()+" extends beyond end of parent "+getDebugInfo());
						if (!INCLUDE_INCORRECTLY_NESTED_CHILDREN_IN_HIERARCHY) {
							pos=childElement.end; 
							continue; 
						}
					}
					childElement.getChildElements(childDepth);
					if (childElement.parentElement==Element.NOT_CACHED) { // make sure element was not added as a child of a descendent element (can happen with overlapping elements)
						childElement.parentElement=this;
						childElements.add(childElement);
					}
					pos=childElement.end;
				}
			}
		}
		return childElements;
	}

	/**
	 * Returns the nesting depth of this element in the document element hierarchy.
	 * <p>
	 * The {@link Source#fullSequentialParse()} method must be called (either explicitly or implicitly) after construction of the <code>Source</code> object if this method is to be used.
	 * An <code>IllegalStateException</code> is thrown if a full sequential parse has not been performed or if it was performed after this element was found.
	 * <p>
	 * A <a href="Source.html#TopLevelElement">top-level element</a> has a nesting depth of <code>0</code>.
	 * <p>
	 * An element formed from a {@linkplain TagType#isServerTag() server tag} always have a nesting depth of <code>0</code>,
	 * regardless of whether it is nested inside a normal element.
	 * <p>
	 * See the {@link Source#getChildElements()} method for more details.
	 *
	 * @return the nesting depth of this element in the document element hierarchy.
	 * @throws IllegalStateException if a {@linkplain Source#fullSequentialParse() full sequential parse} has not been performed or if it was performed after this element was found.
	 * @see #getParentElement()
	 */
	public int getDepth() {
		if (depth==-1) {
			getParentElement();
			if (depth==-1) depth=0;
		}
		return depth;
	}

	/**
	 * Returns the segment representing the <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-content">content</a> of the element.
	 * <p>
	 * This segment spans between the end of the start tag and the start of the end tag.
	 * If the end tag is not present, the content reaches to the end of the element.
	 * <p>
	 * A zero-length segment is returned if the element is {@linkplain #isEmpty() empty},
	 *
	 * @return the segment representing the content of the element, guaranteed not <code>null</code>.
	 */
	public Segment getContent() {
		if (content==null) content=new Segment(source,startTag.end,getContentEnd());
		return content;
	}

	/**
	 * Returns the start tag of the element.
	 * @return the start tag of the element.
	 */
	public StartTag getStartTag() {
		return startTag;
	}

	/**
	 * Returns the end tag of the element.
	 * <p>
	 * If the element has no end tag this method returns <code>null</code>.
	 *
	 * @return the end tag of the element, or <code>null</code> if the element has no end tag.
	 */
	public EndTag getEndTag() {
		return endTag;
	}

	/**
	 * Returns the {@linkplain StartTag#getName() name} of the {@linkplain #getStartTag() start tag} of this element, always in lower case.
	 * <p>
	 * This is equivalent to {@link #getStartTag()}<code>.</code>{@link StartTag#getName() getName()}.
	 * <p>
	 * See the {@link Tag#getName()} method for more information.
	 *
	 * @return the name of the {@linkplain #getStartTag() start tag} of this element, always in lower case.
	 */
	public String getName() {
		return startTag.getName();
	}

	/**
	 * Indicates whether this element has zero-length {@linkplain #getContent() content}.
	 * <p>
	 * This is equivalent to {@link #getContent()}<code>.</code>{@link Segment#length() length()}<code>==0</code>.
	 * <p>
	 * Note that this is a broader definition than that of both the
	 * <a target="_blank" href="http://www.w3.org/TR/html401/intro/sgmltut.html#didx-element-4">HTML definition of an empty element</a>,
	 * which is only those elements whose end tag is {@linkplain HTMLElements#getEndTagForbiddenElementNames() forbidden}, and the
	 * <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-empty">XML definition of an empty element</a>,
	 * which is "either a start-tag immediately followed by an end-tag, or an {@linkplain #isEmptyElementTag() empty-element tag}".
	 * The other possibility covered by this property is the case of an <a href="HTMLElements.html#HTMLElement">HTML element</a> with an
	 * {@linkplain HTMLElements#getEndTagOptionalElementNames() optional} end tag that is immediately followed by another tag that implicitly 
	 * terminates the element.
	 *
	 * @return <code>true</code> if this element has zero-length {@linkplain #getContent() content}, otherwise <code>false</code>.
	 * @see #isEmptyElementTag()
	 */
	public boolean isEmpty() {
		return startTag.end==getContentEnd();
	}

	/**
	 * Indicates whether this element is an <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-eetag">empty-element tag</a>.
	 * <p>
	 * This is equivalent to {@link #getStartTag()}<code>.</code>{@link StartTag#isEmptyElementTag() isEmptyElementTag()}.
	 *
	 * @return <code>true</code> if this element is an <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-eetag">empty-element tag</a>, otherwise <code>false</code>.
	 */
	public boolean isEmptyElementTag() {
		return startTag.isEmptyElementTag();
	}

	/**
	 * Returns the attributes specified in this element's start tag.
	 * <p>
	 * This is equivalent to {@link #getStartTag()}<code>.</code>{@link StartTag#getAttributes() getAttributes()}.
	 *
	 * @return the attributes specified in this element's start tag.
	 * @see StartTag#getAttributes()
	 */
	public Attributes getAttributes() {
		return getStartTag().getAttributes();
	}

	/**
	 * Returns the {@linkplain CharacterReference#decode(CharSequence) decoded} value of the attribute with the specified name (case insensitive).
	 * <p>
	 * Returns <code>null</code> if the {@linkplain #getStartTag() start tag of this element} does not
	 * {@linkplain StartTagType#hasAttributes() have attributes},
	 * no attribute with the specified name exists or the attribute {@linkplain Attribute#hasValue() has no value}.
	 * <p>
	 * This is equivalent to {@link #getStartTag()}<code>.</code>{@link StartTag#getAttributeValue(String) getAttributeValue(attributeName)}.
	 *
	 * @param attributeName  the name of the attribute to get.
	 * @return the {@linkplain CharacterReference#decode(CharSequence) decoded} value of the attribute with the specified name, or <code>null</code> if the attribute does not exist or {@linkplain Attribute#hasValue() has no value}.
	 */
	public String getAttributeValue(final String attributeName) {
		return getStartTag().getAttributeValue(attributeName);
	}

	/**
	 * Returns the {@link FormControl} defined by this element.
	 * @return the {@link FormControl} defined by this element, or <code>null</code> if it is not a <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#form-controls">control</a>.
	 */
	public FormControl getFormControl() {
		return FormControl.construct(this);
	}

	public String getDebugInfo() {
		if (this==NOT_CACHED) return "NOT_CACHED";
		final StringBuilder sb=new StringBuilder();
		sb.append("Element ");
		startTag.appendDebugTag(sb);
		if (!isEmpty()) sb.append('-');
		if (endTag!=null) sb.append(endTag);
		sb.append(' ');
		startTag.appendDebugTagType(sb);
		sb.append(super.getDebugInfo());
		return sb.toString();
	}

	int getContentEnd() {
		return endTag!=null ? endTag.begin : end;
	}
}
