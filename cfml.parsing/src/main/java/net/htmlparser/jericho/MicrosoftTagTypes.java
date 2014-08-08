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
 * Contains {@linkplain TagType tag types} recognised exclusively by Microsoft&reg; Internet Explorer.
 * <p>
 * The tag type defined in this class is not {@linkplain TagType#register() registered} by default.
 */
public final class MicrosoftTagTypes {

	/**
	 * The tag type given to a Microsoft&reg; <a target="_blank" href="http://en.wikipedia.org/wiki/Conditional_comment#Downlevel-revealed_conditional_comment">downlevel-revealed conditional comment</a>
	 * (<code>&lt;&#33;[if<var> &#46;&#46;&#46; </var>]&gt;</code> | <code>&lt;&#33;[endif]&gt;</code>).
	 * <p>
	 * The only valid {@linkplain Tag#getName() names} for tags of this type are "<code>![if</code>" and "<code>![endif</code>".
	 * <p>
	 * This start tag type is used to represent both the "if" and "endif" tags.
	 * Because the "endif" tag can not be represented by an {@linkplain EndTagType end tag type} (it doesn't start with "<code>&lt;/</code>"),
	 * the parser makes no attempt to match if-endif tag pairs to form {@linkplain Element elements}.
	 * <p>
	 * The {@link #isConditionalCommentIfTag(Tag)} and {@link #isConditionalCommentEndifTag(Tag)} methods provide an efficient means of determining whether
	 * a given tag is of the "if" or "endif" variety.
	 * <p>
	 * The expression consituting the condition of an "if" tag can be extracted using the {@link StartTag#getTagContent()} method.
	 * For example, if the variable <code>conditionalCommentIfTag</code> represents the tag <code>&lt;![if !IE]&gt;</code>, then the expression
	 * <code>conditionalCommentIfTag.getTagContent().toString().trim()</code> yields the string "<code>!IE</code>".
	 * <p>
	 * <dl>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <table class="bordered" style="margin: 15px" cellspacing="0">
	 *     <tr><th>Property<th>Value
	 *     <tr><td>{@link StartTagType#getDescription() Description}<td>Microsoft downlevel-revealed conditional comment
	 *     <tr><td>{@link StartTagType#getStartDelimiter() StartDelimiter}<td><code>&lt;![</code>
	 *     <tr><td>{@link StartTagType#getClosingDelimiter() ClosingDelimiter}<td><code>]&gt;</code>
	 *     <tr><td>{@link StartTagType#isServerTag() IsServerTag}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#getNamePrefix() NamePrefix}<td><code>![</code>
	 *     <tr><td>{@link StartTagType#getCorrespondingEndTagType() CorrespondingEndTagType}<td><code>null</code>
	 *     <tr><td>{@link StartTagType#hasAttributes() HasAttributes}<td><code>false</code>
	 *     <tr><td>{@link StartTagType#isNameAfterPrefixRequired() IsNameAfterPrefixRequired}<td><code>true</code>
	 *    </table>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;![if !IE]&gt;</code></dd>
	 * </dl>
	 */
	public static final StartTagType DOWNLEVEL_REVEALED_CONDITIONAL_COMMENT=StartTagTypeMicrosoftDownlevelRevealedConditionalComment.INSTANCE;

	private static final TagType[] TAG_TYPES={
		DOWNLEVEL_REVEALED_CONDITIONAL_COMMENT
	};

	private MicrosoftTagTypes() {}

	/**
	 * Indicates whether the specified tag is a {@linkplain #DOWNLEVEL_REVEALED_CONDITIONAL_COMMENT downlevel-revealed conditional comment} "if" tag
	 * (<code>&lt;&#33;[if<var> &#46;&#46;&#46; </var>]&gt;</code>).
	 *
	 * @param tag  the {@link Tag} to test.
	 * @return <code>true</code> if the specified tag is a <a target="_blank" href="http://en.wikipedia.org/wiki/Conditional_comment">conditional comment</a> "if" tag, otherwise <code>false</code>.
	 */
	public static boolean isConditionalCommentIfTag(final Tag tag) {
		return tag.getName()==StartTagTypeMicrosoftDownlevelRevealedConditionalComment.IF;
	}

	/**
	 * Indicates whether the specified tag is a {@linkplain #DOWNLEVEL_REVEALED_CONDITIONAL_COMMENT downlevel-revealed conditional comment} "endif" tag
	 * (<code>&lt;&#33;[endif]&gt;</code>).
	 *
	 * @param tag  the {@link Tag} to test.
	 * @return <code>true</code> if the specified tag is a <a target="_blank" href="http://en.wikipedia.org/wiki/Conditional_comment">conditional comment</a> "endif" tag, otherwise <code>false</code>.
	 */
	public static boolean isConditionalCommentEndifTag(final Tag tag) {
		return tag.getName()==StartTagTypeMicrosoftDownlevelRevealedConditionalComment.ENDIF;
	}

	/** 
	 * {@linkplain TagType#register() Registers} all of the tag types defined in this class at once.
	 * <p>
	 * The tag types must be registered before the parser will recognise them.
	 */
	public static void register() {
		for (TagType tagType : TAG_TYPES) tagType.register();
	}
	
	/**
	 * Indicates whether the specified tag type is defined in this class.
	 *
	 * @param tagType  the {@link TagType} to test.
	 * @return <code>true</code> if the specified tag type is defined in this class, otherwise <code>false</code>.
	 */
	public static boolean defines(final TagType tagType) {
		for (TagType definedTagType : TAG_TYPES) if (tagType==definedTagType) return true;
		return false;
	}
	
}

