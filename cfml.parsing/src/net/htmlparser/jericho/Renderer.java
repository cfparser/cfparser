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
import java.io.*;

/**
 * Performs a simple rendering of HTML markup into text.
 * <p>
 * This provides a human readable version of the segment content that is modelled on the way
 * <a target="_blank" href="http://www.mozilla.com/thunderbird/">Mozilla Thunderbird</a> and other email clients provide an automatic conversion of
 * HTML content to text in their <a target="_blank" href="http://tools.ietf.org/html/rfc2046#section-5.1.4">alternative MIME encoding</a> of emails.
 * <p>
 * The output using default settings complies with the "text/plain; format=flowed" (DelSp=No) protocol described in
 * <a target="_blank" href="http://tools.ietf.org/html/rfc3676">RFC3676</a>.
 * <p>
 * Many properties are available to customise the output, possibly the most significant of which being {@link #setMaxLineLength(int) MaxLineLength}.
 * See the individual property descriptions for details.
 * <p>
 * Use one of the following methods to obtain the output:
 * <ul>
 *  <li>{@link #writeTo(Writer)}</li>
 *  <li>{@link #appendTo(Appendable)}</li>
 *  <li>{@link #toString()}</li>
 *  <li>{@link CharStreamSourceUtil#getReader(CharStreamSource) CharStreamSourceUtil.getReader(this)}</li>
 * </ul>
 * <p>
 * The rendering of some constructs, especially tables, is very rudimentary.
 * No attempt is made to render nested tables properly, except to ensure that all of the text content is included in the output.
 * <p>
 * Rendering an entire {@link Source} object performs a {@linkplain Source#fullSequentialParse() full sequential parse} automatically.
 * <p>
 * Any aspect of the algorithm not specifically mentioned here is subject to change without notice in future versions.
 * <p>
 * To extract pure text without any rendering of the markup, use the {@link TextExtractor} class instead.
 */
public class Renderer implements CharStreamSource {
	private final Segment rootSegment;
	private int maxLineLength=76;
	private String newLine="\r\n";
	private boolean includeHyperlinkURLs=true;
	private boolean decorateFontStyles=false;
	private boolean convertNonBreakingSpaces=Config.ConvertNonBreakingSpaces;
	private int blockIndentSize=4;
	private int listIndentSize=6;
	private char[] listBullets=new char[] {'*','o','+','#'};
	private String tableCellSeparator=" \t";

	/**
	 * Constructs a new <code>Renderer</code> based on the specified {@link Segment}.
	 * @param segment  the segment containing the HTML to be rendered.
	 * @see Segment#getRenderer()
	 */
	public Renderer(final Segment segment) {
		rootSegment=segment;
	}

	// Documentation inherited from CharStreamSource
	public void writeTo(final Writer writer) throws IOException {
		appendTo(writer);
		writer.flush();
	}

	// Documentation inherited from CharStreamSource
	public void appendTo(final Appendable appendable) throws IOException {
		new Processor(this,rootSegment,getMaxLineLength(),getNewLine(),getIncludeHyperlinkURLs(),getDecorateFontStyles(),getConvertNonBreakingSpaces(),getBlockIndentSize(),getListIndentSize(),getListBullets(),getTableCellSeparator()).appendTo(appendable);
	}
	
	// Documentation inherited from CharStreamSource
	public long getEstimatedMaximumOutputLength() {
		return rootSegment.length();
	}

	// Documentation inherited from CharStreamSource
	public String toString() {
		return CharStreamSourceUtil.toString(this);
	}

	/**
	 * Sets the column at which lines are to be wrapped.
	 * <p>
	 * Lines that would otherwise exceed this length are wrapped onto a new line at a word boundary.
	 * <p>
	 * A Line may still exceed this length if it consists of a single word, where the length of the word plus the line indent exceeds the maximum length.
	 * In this case the line is wrapped immediately after the end of the word.
	 * <p>
	 * The default value is <code>76</code>, which reflects the maximum line length for sending
	 * email data specified in <a target="_blank" href="http://rfc.net/rfc2049.html#s3.">RFC2049 section 3.5</a>.
	 * 
	 * @param maxLineLength  the column at which lines are to be wrapped.
	 * @return this <code>Renderer</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getMaxLineLength()
	 */
	public Renderer setMaxLineLength(final int maxLineLength) {
		this.maxLineLength=maxLineLength;
		return this;
	}

	/**
	 * Returns the column at which lines are to be wrapped.
	 * <p>
	 * See the {@link #setMaxLineLength(int)} method for a full description of this property.
	 *
	 * @return the column at which lines are to be wrapped.
	 */	
	public int getMaxLineLength() {
		return maxLineLength;
	}

	/**
	 * Sets the string to be used to represent a <a target="_blank" href="http://en.wikipedia.org/wiki/Newline">newline</a> in the output.
	 * <p>
	 * The default value is <code>"\r\n"</code> <span title="carriage return + line feed">(CR+LF)</span> regardless of the platform on which the library is running.
	 * This is so that the default configuration produces valid 
	 * <a target="_blank" href="http://tools.ietf.org/html/rfc1521#section-7.1.2">MIME plain/text</a> output, which mandates the use of CR+LF for line breaks.
	 * <p>
	 * Specifying a <code>null</code> argument causes the output to use same new line string as is used in the source document, which is
	 * determined via the {@link Source#getNewLine()} method.
	 * If the source document does not contain any new lines, a "best guess" is made by either taking the new line string of a previously parsed document,
	 * or using the value from the static {@link Config#NewLine} property.
	 * 
	 * @param newLine  the string to be used to represent a <a target="_blank" href="http://en.wikipedia.org/wiki/Newline">newline</a> in the output, may be <code>null</code>.
	 * @return this <code>Renderer</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getNewLine()
	 */
	public Renderer setNewLine(final String newLine) {
		this.newLine=newLine;
		return this;
	}

	/**
	 * Returns the string to be used to represent a <a target="_blank" href="http://en.wikipedia.org/wiki/Newline">newline</a> in the output.
	 * <p>
	 * See the {@link #setNewLine(String)} method for a full description of this property.
	 *
	 * @return the string to be used to represent a <a target="_blank" href="http://en.wikipedia.org/wiki/Newline">newline</a> in the output.
	 */
	public String getNewLine() {
		if (newLine==null) newLine=rootSegment.source.getBestGuessNewLine();
		return newLine;
	}

	/**
	 * Sets whether hyperlink URL's are included in the output.
	 * <p>
	 * The default value is <code>true</code>.
	 * <p>
	 * When this property is <code>true</code>, the URL of each hyperlink is included in the output as determined by the implementation of the
	 * {@link #renderHyperlinkURL(StartTag)} method.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd>
	 *   <p>
	 *   Assuming the default implementation of {@link #renderHyperlinkURL(StartTag)}, when this property is <code>true</code>, the following HTML:
	 *   <blockquote class="code">
	 *    <code>&lt;a href="http://jericho.htmlparser.net/"&gt;Jericho HTML Parser&lt;/a&gt;</code>
	 *   </blockquote>
	 *   produces the following output:
	 *   <blockquote class="code">
	 *    <code>Jericho HTML Parser &lt;http://jericho.htmlparser.net/&gt;</code>
	 *   </blockquote>
	 *  </dd>
	 * </dl>
	 *
	 * @param includeHyperlinkURLs  specifies whether hyperlink URL's are included in the output.
	 * @return this <code>Renderer</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getIncludeHyperlinkURLs()
	 */
	public Renderer setIncludeHyperlinkURLs(final boolean includeHyperlinkURLs) {
		this.includeHyperlinkURLs=includeHyperlinkURLs;
		return this;
	}

	/**
	 * Indicates whether hyperlink URL's are included in the output.
	 * <p>
	 * See the {@link #setIncludeHyperlinkURLs(boolean)} method for a full description of this property.
	 *
	 * @return <code>true</code> if hyperlink URL's are included in the output, otherwise <code>false</code>.
	 */
	public boolean getIncludeHyperlinkURLs() {
		return includeHyperlinkURLs;
	}

	/**
	 * Renders the hyperlink URL from the specified {@link StartTag}.
	 * <p>
	 * A return value of <code>null</code> indicates that the hyperlink URL should not be rendered at all.
	 * <p>
	 * The default implementation of this method returns <code>null</code> if the <code>href</code> attribute of the specified start tag
	 * is '<code>#</code>', starts with "<code>javascript:</code>", or is missing.
	 * In all other cases it returns the value of the <code>href</code> attribute enclosed in angle brackets.
	 * <p>
	 * See the documentation of the {@link #setIncludeHyperlinkURLs(boolean)} method for an example of how a hyperlink is rendered by the default implementation.
	 * <p>
	 * This method can be overridden in a subclass to customise the rendering of hyperlink URLs.
	 * <p>
	 * Rendering of hyperlink URLs can be disabled completely without overriding this method by setting the
	 * {@link #setIncludeHyperlinkURLs(boolean) IncludeHyperlinkURLs} property to <code>false</code>.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd>
	 *   To render hyperlink URLs without the enclosing angle brackets:<br /><br />
	 *   <code>
	 *    Renderer renderer=new Renderer(segment) {<br />
	 *    &nbsp; &nbsp; public String renderHyperlinkURL(StartTag startTag) {<br />
	 *    &nbsp; &nbsp; &nbsp; &nbsp; String href=startTag.getAttributeValue("href");<br />
	 *    &nbsp; &nbsp; &nbsp; &nbsp; if (href==null || href.equals("#") || href.startsWith("javascript:")) return null;<br />
	 *    &nbsp; &nbsp; &nbsp; &nbsp; return href;<br />
	 *    &nbsp; &nbsp; }<br />
	 *    };<br />
	 *    String renderedSegment=renderer.toString();
	 *   </code>
	 *  </dd>
	 * </dl>
	 * @param startTag  the start tag of the hyperlink element, must not be <code>null</code>.
	 * @return The rendered hyperlink URL from the specified {@link StartTag}, or <code>null</code> if the hyperlink URL should not be rendered.
	 */
	public String renderHyperlinkURL(final StartTag startTag) {
		final String href=startTag.getAttributeValue("href");
		if (href==null || href.equals("#") || href.startsWith("javascript:")) return null;
		return '<'+href+'>';
	}

	/**
	 * Sets whether decoration characters are to be included around the content of some
	 * <a target="_blank" href="http://www.w3.org/TR/html401/present/graphics.html#h-15.2.1">font style elements</a> and
	 * <a target="_blank" href="http://www.w3.org/TR/html401/struct/text.html#h-9.2.1">phrase elements</a>.
	 * <p>
	 * The default value is <code>false</code>.
	 * <p>
	 * Below is a table summarising the decorated elements.
	 * <p>
	 * <style type="text/css">
	 *  table#FontStyleElementSummary td, table#FontStyleElementSummary th {text-align: center; padding-bottom: 2px}
	 * </style>
	 * <table id="FontStyleElementSummary" class="bordered" cellspacing="0">
	 *  <tr><th title="HTML elements decorated">Elements</th><th title="The character placed around the element content">Character</th><th>Example Output</th></tr>
	 *  <tr><td>{@link HTMLElementName#B B} and {@link HTMLElementName#STRONG STRONG}</td><td><code>*</code></td><td><code>*bold text*</code></td></tr>
	 *  <tr><td>{@link HTMLElementName#I I} and {@link HTMLElementName#EM EM}</td><td><code>/</code></td><td><code>/italic text/</code></td></tr>
	 *  <tr><td>{@link HTMLElementName#U U}</td><td><code>_</code></td><td><code>_underlined text_</code></td></tr>
	 *  <tr><td>{@link HTMLElementName#CODE CODE}</td><td><code>|</code></td><td><code>|code|</code></td></tr>
	 * </table>
	 *
	 * @param decorateFontStyles  specifies whether decoration characters are to be included around the content of some font style elements.
	 * @return this <code>Renderer</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getDecorateFontStyles()
	 */
	public Renderer setDecorateFontStyles(final boolean decorateFontStyles) {
		this.decorateFontStyles=decorateFontStyles;
		return this;
	}

	/**
	 * Indicates whether decoration characters are to be included around the content of some
	 * <a target="_blank" href="http://www.w3.org/TR/html401/present/graphics.html#h-15.2.1">font style elements</a> and
	 * <a target="_blank" href="http://www.w3.org/TR/html401/struct/text.html#h-9.2.1">phrase elements</a>.
	 * <p>
	 * See the {@link #setDecorateFontStyles(boolean)} method for a full description of this property.
	 *
	 * @return <code>true</code> if decoration characters are to be included around the content of some font style elements, otherwise <code>false</code>.
	 */
	public boolean getDecorateFontStyles() {
		return decorateFontStyles;
	}

	/**
	 * Sets whether non-breaking space ({@link CharacterEntityReference#_nbsp &amp;nbsp;}) character entity references are converted to spaces.
	 * <p>
	 * The default value is that of the static {@link Config#ConvertNonBreakingSpaces} property at the time the <code>Renderer</code> is instantiated.
	 *
	 * @param convertNonBreakingSpaces  specifies whether non-breaking space ({@link CharacterEntityReference#_nbsp &amp;nbsp;}) character entity references are converted to spaces.
	 * @return this <code>Renderer</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getConvertNonBreakingSpaces()
	 */
	public Renderer setConvertNonBreakingSpaces(boolean convertNonBreakingSpaces) {
		this.convertNonBreakingSpaces=convertNonBreakingSpaces;
		return this;
	}

	/**
	 * Indicates whether non-breaking space ({@link CharacterEntityReference#_nbsp &amp;nbsp;}) character entity references are converted to spaces.
	 * <p>
	 * See the {@link #setConvertNonBreakingSpaces(boolean)} method for a full description of this property.
	 * 
	 * @return <code>true</code> if non-breaking space ({@link CharacterEntityReference#_nbsp &amp;nbsp;}) character entity references are converted to spaces, otherwise <code>false</code>.
	 */
	public boolean getConvertNonBreakingSpaces() {
		return convertNonBreakingSpaces;
	}

	/**
	 * Sets the size of the indent to be used for anything other than {@link HTMLElementName#LI LI} elements.
	 * <p>
	 * At present this applies to {@link HTMLElementName#BLOCKQUOTE BLOCKQUOTE} and {@link HTMLElementName#DD DD} elements.
	 * <p>
	 * The default value is <code>4</code>.
	 * 
	 * @param blockIndentSize  the size of the indent.
	 * @return this <code>Renderer</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getBlockIndentSize()
	 */
	public Renderer setBlockIndentSize(final int blockIndentSize) {
		this.blockIndentSize=blockIndentSize;
		return this;
	}

	/**
	 * Returns the size of the indent to be used for anything other than {@link HTMLElementName#LI LI} elements.
	 * <p>
	 * See the {@link #setBlockIndentSize(int)} method for a full description of this property.
	 *
	 * @return the size of the indent to be used for anything other than {@link HTMLElementName#LI LI} elements.
	 */
	public int getBlockIndentSize() {
		return blockIndentSize;
	}

	/**
	 * Sets the size of the indent to be used for {@link HTMLElementName#LI LI} elements.
	 * <p>
	 * The default value is <code>6</code>.
	 * <p>
	 * This applies to {@link HTMLElementName#LI LI} elements inside both {@link HTMLElementName#UL UL} and {@link HTMLElementName#OL OL} elements.
	 * <p>
	 * The bullet or number of the list item is included as part of the indent.
	 * 
	 * @param listIndentSize  the size of the indent.
	 * @return this <code>Renderer</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getListIndentSize()
	 */
	public Renderer setListIndentSize(final int listIndentSize) {
		this.listIndentSize=listIndentSize;
		return this;
	}

	/**
	 * Returns the size of the indent to be used for {@link HTMLElementName#LI LI} elements.
	 * <p>
	 * See the {@link #setListIndentSize(int)} method for a full description of this property.
	 *
	 * @return the size of the indent to be used for {@link HTMLElementName#LI LI} elements.
	 */
	public int getListIndentSize() {
		return listIndentSize;
	}

	/**
	 * Sets the bullet characters to use for list items inside {@link HTMLElementName#UL UL} elements.
	 * <p>
	 * The values in the default array are <code>*</code>, <code>o</code>, <code>+</code> and <code>#</code>.
	 * <p>
	 * If the nesting of rendered lists goes deeper than the length of this array, the bullet characters start repeating from the first in the array.
	 * <p>
	 * WARNING: If any of the characters in the default array are modified, this will affect all other instances of this class using the default array.
	 * 
	 * @param listBullets  an array of characters to be used as bullets, must have at least one entry.
	 * @return this <code>Renderer</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getListBullets()
	 */
	public Renderer setListBullets(final char[] listBullets) {
		if (listBullets==null || listBullets.length==0) throw new IllegalArgumentException("listBullets argument must be an array of at least one character");
		this.listBullets=listBullets;
		return this;
	}

	/**
	 * Returns the bullet characters to use for list items inside {@link HTMLElementName#UL UL} elements.
	 * <p>
	 * See the {@link #setListBullets(char[])} method for a full description of this property.
	 *
	 * @return the bullet characters to use for list items inside {@link HTMLElementName#UL UL} elements.
	 */
	public char[] getListBullets() {
		return listBullets;
	}

	/**
	 * Sets the string that is to separate table cells.
	 * <p>
	 * The default value is <code>" \t"</code> (a space followed by a tab).
	 * 
	 * @param tableCellSeparator  the string that is to separate table cells.
	 * @return this <code>Renderer</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getTableCellSeparator()
	 */
	public Renderer setTableCellSeparator(final String tableCellSeparator) {
		this.tableCellSeparator=tableCellSeparator;
		return this;
	}

	/**
	 * Returns the string that is to separate table cells.
	 * <p>
	 * See the {@link #setTableCellSeparator(String)} method for a full description of this property.
	 *
	 * @return the string that is to separate table cells.
	 */
	public String getTableCellSeparator() {
		return tableCellSeparator;
	}
	
	/** This class does the actual work, but is first passed final copies of all the parameters for efficiency. */
	private static final class Processor {
		private final Renderer renderer;
		private final Segment rootSegment;
		private final Source source;
		private final int maxLineLength;
		private final String newLine;
		private final boolean includeHyperlinkURLs;
		private final boolean decorateFontStyles;
		private final boolean convertNonBreakingSpaces;
		private final int blockIndentSize;
		private final int listIndentSize;
		private final char[] listBullets;
		private final String tableCellSeparator;
	
		private Appendable appendable;
		private int renderedIndex; // keeps track of where rendering is up to in case of overlapping elements
		private boolean atStartOfLine;
		private int col;
		private int blockIndentLevel;
		private int listIndentLevel;
		private int blockVerticalMargin; // minimum number of blank lines to output at the current block boundary, or NO_MARGIN (-1) if we are not currently at a block boundary.
		private boolean preformatted;
		private boolean lastCharWhiteSpace;
		private boolean ignoreInitialWhitespace;
		private boolean bullet;
		private int listBulletNumber;
	
		private static final int NO_MARGIN=-1;
		private static final int UNORDERED_LIST=-1;
	
		private static Map<String,ElementHandler> ELEMENT_HANDLERS=new HashMap<String,ElementHandler>();
		static {
			ELEMENT_HANDLERS.put(HTMLElementName.A,A_ElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.ADDRESS,StandardBlockElementHandler.INSTANCE_0_0);
			ELEMENT_HANDLERS.put(HTMLElementName.APPLET,RemoveElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.B,FontStyleElementHandler.INSTANCE_B);
			ELEMENT_HANDLERS.put(HTMLElementName.BLOCKQUOTE,StandardBlockElementHandler.INSTANCE_1_1_INDENT);
			ELEMENT_HANDLERS.put(HTMLElementName.BR,BR_ElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.BUTTON,RemoveElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.CAPTION,StandardBlockElementHandler.INSTANCE_0_0);
			ELEMENT_HANDLERS.put(HTMLElementName.CENTER,StandardBlockElementHandler.INSTANCE_1_1);
			ELEMENT_HANDLERS.put(HTMLElementName.CODE,FontStyleElementHandler.INSTANCE_CODE);
			ELEMENT_HANDLERS.put(HTMLElementName.DD,StandardBlockElementHandler.INSTANCE_0_0_INDENT);
			ELEMENT_HANDLERS.put(HTMLElementName.DIR,ListElementHandler.INSTANCE_UL);
			ELEMENT_HANDLERS.put(HTMLElementName.DIV,StandardBlockElementHandler.INSTANCE_0_0);
			ELEMENT_HANDLERS.put(HTMLElementName.DT,StandardBlockElementHandler.INSTANCE_0_0);
			ELEMENT_HANDLERS.put(HTMLElementName.EM,FontStyleElementHandler.INSTANCE_I);
			ELEMENT_HANDLERS.put(HTMLElementName.FIELDSET,StandardBlockElementHandler.INSTANCE_1_1);
			ELEMENT_HANDLERS.put(HTMLElementName.FORM,StandardBlockElementHandler.INSTANCE_1_1);
			ELEMENT_HANDLERS.put(HTMLElementName.H1,StandardBlockElementHandler.INSTANCE_2_1);
			ELEMENT_HANDLERS.put(HTMLElementName.H2,StandardBlockElementHandler.INSTANCE_2_1);
			ELEMENT_HANDLERS.put(HTMLElementName.H3,StandardBlockElementHandler.INSTANCE_2_1);
			ELEMENT_HANDLERS.put(HTMLElementName.H4,StandardBlockElementHandler.INSTANCE_2_1);
			ELEMENT_HANDLERS.put(HTMLElementName.H5,StandardBlockElementHandler.INSTANCE_2_1);
			ELEMENT_HANDLERS.put(HTMLElementName.H6,StandardBlockElementHandler.INSTANCE_2_1);
			ELEMENT_HANDLERS.put(HTMLElementName.HEAD,RemoveElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.HR,HR_ElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.I,FontStyleElementHandler.INSTANCE_I);
			ELEMENT_HANDLERS.put(HTMLElementName.LEGEND,StandardBlockElementHandler.INSTANCE_0_0);
			ELEMENT_HANDLERS.put(HTMLElementName.LI,LI_ElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.MENU,ListElementHandler.INSTANCE_UL);
			ELEMENT_HANDLERS.put(HTMLElementName.MAP,RemoveElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.NOFRAMES,RemoveElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.NOSCRIPT,RemoveElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.OL,ListElementHandler.INSTANCE_OL);
			ELEMENT_HANDLERS.put(HTMLElementName.P,StandardBlockElementHandler.INSTANCE_1_1);
			ELEMENT_HANDLERS.put(HTMLElementName.PRE,PRE_ElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.SCRIPT,RemoveElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.SELECT,RemoveElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.STRONG,FontStyleElementHandler.INSTANCE_B);
			ELEMENT_HANDLERS.put(HTMLElementName.STYLE,RemoveElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.TEXTAREA,RemoveElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.TD,TD_ElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.TH,TD_ElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.TR,TR_ElementHandler.INSTANCE);
			ELEMENT_HANDLERS.put(HTMLElementName.U,FontStyleElementHandler.INSTANCE_U);
			ELEMENT_HANDLERS.put(HTMLElementName.UL,ListElementHandler.INSTANCE_UL);
		}	
	
		public Processor(final Renderer renderer, final Segment rootSegment, final int maxLineLength, final String newLine, final boolean includeHyperlinkURLs, final boolean decorateFontStyles, final boolean convertNonBreakingSpaces, final int blockIndentSize, final int listIndentSize, final char[] listBullets, final String tableCellSeparator) {
			this.renderer=renderer;
			this.rootSegment=rootSegment;
			source=rootSegment.source;
			this.maxLineLength=maxLineLength;
			this.newLine=newLine;
			this.includeHyperlinkURLs=includeHyperlinkURLs;
			this.decorateFontStyles=decorateFontStyles;
			this.convertNonBreakingSpaces=convertNonBreakingSpaces;
			this.blockIndentSize=blockIndentSize;
			this.listIndentSize=listIndentSize;
			this.listBullets=listBullets;
			this.tableCellSeparator=tableCellSeparator;
		}
	
		public void appendTo(final Appendable appendable) throws IOException {
			reset();
			this.appendable=appendable;
			appendSegmentProcessingChildElements(rootSegment.begin,rootSegment.end,rootSegment.getChildElements());
		}
	
		private void reset() {
			renderedIndex=0;
			atStartOfLine=true;
			col=0;
			blockIndentLevel=0;
			listIndentLevel=0;
			blockVerticalMargin=NO_MARGIN;
			preformatted=false;
			lastCharWhiteSpace=ignoreInitialWhitespace=false;
			bullet=false;
		}
	
		private void appendElementContent(final Element element) throws IOException {
			final int contentEnd=element.getContentEnd();
			if (element.isEmpty() || renderedIndex>=contentEnd) return;
			final int contentBegin=element.getStartTag().end;
			appendSegmentProcessingChildElements(Math.max(renderedIndex,contentBegin),contentEnd,element.getChildElements());
		}
	
		private void appendSegmentProcessingChildElements(final int begin, final int end, final List<Element> childElements) throws IOException {
			int index=begin;
			for (Element childElement : childElements) {
				if (index>=childElement.end) continue;
				if (index<childElement.begin) appendSegmentRemovingTags(index,childElement.begin);
				getElementHandler(childElement).process(this,childElement);
				index=Math.max(renderedIndex,childElement.end);
			}
			if (index<end) appendSegmentRemovingTags(index,end);
		}
	
		private static ElementHandler getElementHandler(final Element element) {
			if (element.getStartTag().getStartTagType().isServerTag()) return RemoveElementHandler.INSTANCE; // hard-coded configuration does not include server tags in child element hierarchy, so this is normally not executed.
			ElementHandler elementHandler=ELEMENT_HANDLERS.get(element.getName());
			return (elementHandler!=null) ? elementHandler : StandardInlineElementHandler.INSTANCE;
		}
	
		private void appendSegmentRemovingTags(final int begin, final int end) throws IOException {
			int index=begin;
			while (true) {
				Tag tag=source.getNextTag(index);
				if (tag==null || tag.begin>=end) break;
				appendSegment(index,tag.begin);
				index=tag.end;
			}
			appendSegment(index,end);
		}

		private void appendSegment(int begin, final int end) throws IOException {
 			assert begin<=end;
			if (begin<renderedIndex) begin=renderedIndex;
			if (begin>=end) return;
			try {
				if (preformatted)
					appendPreformattedSegment(begin,end);
				else
					appendNonPreformattedSegment(begin,end);
			} finally {
				if (renderedIndex<end) renderedIndex=end;
			}
		}
	
		private void appendPreformattedSegment(final int begin, final int end) throws IOException {
			assert begin<end;
			assert begin>=renderedIndex;
			if (isStartOfBlock()) appendBlockVerticalMargin();
			final String text=CharacterReference.decode(source.subSequence(begin,end),false,convertNonBreakingSpaces);
			for (int i=0; i<text.length(); i++) {
				final char ch=text.charAt(i);
				if (ch=='\n') {
					newLine();
				} else if (ch=='\r') {
					newLine();
					final int nextI=i+1;
					if (nextI==text.length()) break;
					if (text.charAt(nextI)=='\n') i++;
				} else {
					append(ch);
				}
			}
		}

		private void appendNonPreformattedSegment(final int begin, final int end) throws IOException {
			assert begin<end;
			assert begin>=renderedIndex;
			final String text=CharacterReference.decodeCollapseWhiteSpace(source.subSequence(begin,end),convertNonBreakingSpaces);
			if (text.length()==0) {
				if (!ignoreInitialWhitespace) lastCharWhiteSpace=true;
				return;
			}
			if (isStartOfBlock()) {
				appendBlockVerticalMargin();
			} else if (lastCharWhiteSpace || (Segment.isWhiteSpace(source.charAt(begin)) && !ignoreInitialWhitespace)) {
				append(' ');
			}
			int textIndex=0;
			int i=0;
			lastCharWhiteSpace=ignoreInitialWhitespace=false;
			while (true) {
				for (; i<text.length(); i++) {
					if (text.charAt(i)!=' ') continue; // search for end of word
					// At end of word. To comply with RFC264 Format=Flowed protocol, need to make sure we don't wrap immediately before ">" or "From ".
					if (i+1<text.length() && text.charAt(i+1)=='>') continue;
					if (i+6<text.length() && text.startsWith("From ",i+1)) continue;
					break; // OK to wrap here if necessary
				}
				if (col+i-textIndex+1>=maxLineLength) {
					if (lastCharWhiteSpace && (blockIndentLevel|listIndentLevel)==0) append(' ');
					startNewLine(0);
				} else if (lastCharWhiteSpace) {
					append(' ');
				}
				append(text,textIndex,i);
				if (i==text.length()) break;
				lastCharWhiteSpace=true;
				textIndex=++i;
			}
			lastCharWhiteSpace=Segment.isWhiteSpace(source.charAt(end-1));
		}

		private boolean isStartOfBlock() {
			return blockVerticalMargin!=NO_MARGIN;
		}

		private void appendBlockVerticalMargin() throws IOException {
			assert blockVerticalMargin!=NO_MARGIN;
			startNewLine(blockVerticalMargin);
			blockVerticalMargin=NO_MARGIN;
		}
		
		private void blockBoundary(final int verticalMargin) throws IOException {
			// Set a block boundary with the given vertical margin.  The vertical margin is the minimum number of blank lines to output between the blocks.
			// This method can be called multiple times at a block boundary, and the next textual output will output the number of blank lines determined by the
			// maximum vertical margin of all the method calls.
			if (blockVerticalMargin<verticalMargin) blockVerticalMargin=verticalMargin;
		}
	
		private void startNewLine(int verticalMargin) throws IOException {
			// ensures we end up at the start of a line with the specified vertical margin between the previous textual output and the next textual output.
			final int requiredNewLines=verticalMargin+(atStartOfLine?0:1);
			for (int i=0; i<requiredNewLines; i++) appendable.append(newLine);
			atStartOfLine=true;
			lastCharWhiteSpace=ignoreInitialWhitespace=false;
		}

		private void newLine() throws IOException {
			appendable.append(newLine);
			atStartOfLine=true;
			lastCharWhiteSpace=ignoreInitialWhitespace=false;
		}
	
		private void appendIndent() throws IOException {
			for (int i=blockIndentLevel*blockIndentSize; i>0; i--) appendable.append(' ');
			if (bullet) {
				for (int i=(listIndentLevel-1)*listIndentSize; i>0; i--) appendable.append(' ');
				if (listBulletNumber==UNORDERED_LIST) {
					for (int i=listIndentSize-2; i>0; i--) appendable.append(' ');
					appendable.append(listBullets[(listIndentLevel-1)%listBullets.length]).append(' ');
				} else {
					String bulletNumberString=Integer.toString(listBulletNumber);
					for (int i=listIndentSize-bulletNumberString.length()-2; i>0; i--) appendable.append(' ');
					appendable.append(bulletNumberString).append(". ");
				}
				bullet=false;
			} else {
				for (int i=listIndentLevel*listIndentSize; i>0; i--) appendable.append(' ');
			}
			col=blockIndentLevel*blockIndentSize+listIndentLevel*listIndentSize;
			atStartOfLine=false;
		}
	
		private Processor append(final char ch) throws IOException {
			if (atStartOfLine) appendIndent();
			appendable.append(ch);
			col++;
			return this;
		}
		
		private Processor append(final String text) throws IOException {
			if (atStartOfLine) appendIndent();
			appendable.append(text);
			col+=text.length();
			return this;
		}
	
		private void append(final CharSequence text, final int begin, final int end) throws IOException {
			if (atStartOfLine) appendIndent();
			for (int i=begin; i<end; i++) appendable.append(text.charAt(i));
			col+=end-begin;
		}

		private interface ElementHandler {
			void process(Processor x, Element element) throws IOException;
		}
		
		private static class RemoveElementHandler implements ElementHandler {
			public static final ElementHandler INSTANCE=new RemoveElementHandler();
			public void process(Processor x, Element element) {}
		}
		
		private static class StandardInlineElementHandler implements ElementHandler {
			public static final ElementHandler INSTANCE=new StandardInlineElementHandler();
			public void process(Processor x, Element element) throws IOException {
				x.appendElementContent(element);
			}
		}
	
		private static class FontStyleElementHandler implements ElementHandler {
			public static final ElementHandler INSTANCE_B=new FontStyleElementHandler('*');
			public static final ElementHandler INSTANCE_I=new FontStyleElementHandler('/');
			public static final ElementHandler INSTANCE_U=new FontStyleElementHandler('_');
			public static final ElementHandler INSTANCE_CODE=new FontStyleElementHandler('|');
			private final char decorationChar;
			public FontStyleElementHandler(char decorationChar) {
				this.decorationChar=decorationChar;
			}
			public void process(Processor x, Element element) throws IOException {
				if (x.decorateFontStyles) {
					if (x.lastCharWhiteSpace) {
						x.append(' ');
						x.lastCharWhiteSpace=false;
					}
					x.append(decorationChar);
					x.appendElementContent(element);
					if (x.decorateFontStyles) x.append(decorationChar);
				} else {
					x.appendElementContent(element);
				}
			}
		}
	
		private static class StandardBlockElementHandler implements ElementHandler {
			public static final ElementHandler INSTANCE_0_0=new StandardBlockElementHandler(0,0,false);
			public static final ElementHandler INSTANCE_1_1=new StandardBlockElementHandler(1,1,false);
			public static final ElementHandler INSTANCE_2_1=new StandardBlockElementHandler(2,1,false);
			public static final ElementHandler INSTANCE_0_0_INDENT=new StandardBlockElementHandler(0,0,true);
			public static final ElementHandler INSTANCE_1_1_INDENT=new StandardBlockElementHandler(1,1,true);
			private final int topMargin;
			private final int bottomMargin;
			private final boolean indent;
			public StandardBlockElementHandler(int topMargin, int bottomMargin, boolean indent) {
				this.topMargin=topMargin;
				this.bottomMargin=bottomMargin;
				this.indent=indent;
			}
			public void process(Processor x, Element element) throws IOException {
				x.blockBoundary(topMargin);
				if (indent) x.blockIndentLevel++;
				x.appendElementContent(element);
				if (indent) x.blockIndentLevel--;
				x.blockBoundary(bottomMargin);
			}
		}
	
		private static class A_ElementHandler implements ElementHandler {
			public static final ElementHandler INSTANCE=new A_ElementHandler();
			public void process(Processor x, Element element) throws IOException {
				x.appendElementContent(element);
				if (!x.includeHyperlinkURLs) return;
				String renderedHyperlinkURL=x.renderer.renderHyperlinkURL(element.getStartTag());
				if (renderedHyperlinkURL==null) return;
				int linkLength=renderedHyperlinkURL.length()+1;
				if (x.col+linkLength>=x.maxLineLength) {
					x.startNewLine(0);
				} else {
					x.append(' ');
				}
				x.append(renderedHyperlinkURL);
				x.lastCharWhiteSpace=true;
			}
		}
		
		private static class BR_ElementHandler implements ElementHandler {
			public static final ElementHandler INSTANCE=new BR_ElementHandler();
			public void process(Processor x, Element element) throws IOException {
				x.newLine();
				x.blockBoundary(0);
			}
		}
	
		private static class HR_ElementHandler implements ElementHandler {
			public static final ElementHandler INSTANCE=new HR_ElementHandler();
			public void process(Processor x, Element element) throws IOException {
				x.blockBoundary(0);
				x.appendBlockVerticalMargin();
				for (int i=0; i<72; i++) x.append('-');
				x.blockBoundary(0);
			}
		}
	
		private static class ListElementHandler implements ElementHandler {
			public static final ElementHandler INSTANCE_OL=new ListElementHandler(0);
			public static final ElementHandler INSTANCE_UL=new ListElementHandler(UNORDERED_LIST);
			private final int initialListBulletNumber;
			public ListElementHandler(int initialListBulletNumber) {
				this.initialListBulletNumber=initialListBulletNumber;
			}
			public void process(Processor x, Element element) throws IOException {
				x.blockBoundary(0);
				int oldListBulletNumber=x.listBulletNumber;
				x.listBulletNumber=initialListBulletNumber;
				x.listIndentLevel++;
				x.appendElementContent(element);
				x.listIndentLevel--;
				x.listBulletNumber=oldListBulletNumber;
				x.blockBoundary(0);
			}
		}
	
		private static class LI_ElementHandler implements ElementHandler {
			public static final ElementHandler INSTANCE=new LI_ElementHandler();
			public void process(Processor x, Element element) throws IOException {
				if (x.listBulletNumber!=UNORDERED_LIST) x.listBulletNumber++;
				x.bullet=true;
				x.blockBoundary(0);
				x.appendBlockVerticalMargin();
				x.appendIndent();
				x.ignoreInitialWhitespace=true;
				x.appendElementContent(element);
				x.bullet=false;
				x.blockBoundary(0);
			}
		}
	
		private static class PRE_ElementHandler implements ElementHandler {
			public static final ElementHandler INSTANCE=new PRE_ElementHandler();
			public void process(Processor x, Element element) throws IOException {
				x.blockBoundary(1);
				boolean oldPreformatted=x.preformatted; // should always be false
				x.preformatted=true;
				x.appendElementContent(element);
				x.preformatted=oldPreformatted;
				x.blockBoundary(1);
			}
		}
	
		private static class TD_ElementHandler implements ElementHandler {
			public static final ElementHandler INSTANCE=new TD_ElementHandler();
			public void process(Processor x, Element element) throws IOException {
				if (!x.isStartOfBlock()) x.append(x.tableCellSeparator);
				x.lastCharWhiteSpace=false;
				x.appendElementContent(element);
			}
		}
	
		private static class TR_ElementHandler implements ElementHandler {
			public static final ElementHandler INSTANCE=new TR_ElementHandler();
			public void process(Processor x, Element element) throws IOException {
				x.blockBoundary(0);
				x.appendElementContent(element);
				x.blockBoundary(0);
			}
		}
	}
}
