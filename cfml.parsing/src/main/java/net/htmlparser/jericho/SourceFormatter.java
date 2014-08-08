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
import java.net.*;

/**
 * Formats HTML source by laying out each non-inline-level element on a new line with an appropriate indent.
 * <p>
 * Any indentation present in the original source text is removed.
 * <p>
 * Use one of the following methods to obtain the output:
 * <ul>
 *  <li>{@link #writeTo(Writer)}</li>
 *  <li>{@link #appendTo(Appendable)}</li>
 *  <li>{@link #toString()}</li>
 *  <li>{@link CharStreamSourceUtil#getReader(CharStreamSource) CharStreamSourceUtil.getReader(this)}</li>
 * </ul>
 * <p>
 * The output text is functionally equivalent to the original source and should be rendered identically unless specified below.
 * <p>
 * The following points describe the process in general terms.
 * Any aspect of the algorithm not specifically mentioned here is subject to change without notice in future versions.
 * <p>
 * <ul>
 *  <li>Every element that is not an {@linkplain HTMLElements#getInlineLevelElementNames() inline-level element} appears on a new line
 *   with an indent corresponding to its {@linkplain Element#getDepth() depth} in the <a href="Source.html#DocumentElementHierarchy">document element hierarchy</a>.
 *  <li>The indent is formed by writing <i>n</i> repetitions of the string specified in the {@link #setIndentString(String) IndentString} property,
 *   where <i>n</i> is the depth of the indentation.
 *  <li>The {@linkplain Element#getContent() content} of an indented element starts on a new line and is indented at a depth one greater than that of the element,
 *   with the end tag appearing on a new line at the same depth as the start tag.
 *   If the content contains only text and {@linkplain HTMLElements#getInlineLevelElementNames() inline-level elements},
 *   it may continue on the same line as the start tag.  Additionally, if the output content contains no new lines, the end tag may also continue on the same line.
 *  <li>The content of preformatted elements such as {@link HTMLElementName#PRE PRE} and {@link HTMLElementName#TEXTAREA TEXTAREA} are not indented,
 *   nor is the white space modified in any way.
 *  <li>Only {@linkplain StartTagType#NORMAL normal} and {@linkplain StartTagType#DOCTYPE_DECLARATION document type declaration} elements are indented.
 *   All others are treated as {@linkplain HTMLElements#getInlineLevelElementNames() inline-level elements}.
 *  <li>White space and indentation inside HTML {@linkplain StartTagType#COMMENT comments}, {@linkplain StartTagType#CDATA_SECTION CDATA sections}, or any
 *   {@linkplain TagType#isServerTag() server tag} is preserved, 
 *   but with the indentation of new lines starting at a depth one greater than that of the surrounding text.
 *  <li>White space and indentation inside {@link HTMLElementName#SCRIPT SCRIPT} elements is preserved, 
 *   but with the indentation of new lines starting at a depth one greater than that of the <code>SCRIPT</code> element.
 *  <li>If the {@link #setTidyTags(boolean) TidyTags} property is set to <code>true</code>,
 *   every tag in the document is replaced with the output from its {@link Tag#tidy()} method.
 *   If this property is set to <code>false</code>, the tag from the original text is used, including all white space,
 *   but with any new lines indented at a depth one greater than that of the element.
 *  <li>If the {@link #setCollapseWhiteSpace(boolean) CollapseWhiteSpace} property
 *   is set to <code>true</code>, every string of one or more {@linkplain Segment#isWhiteSpace(char) white space} characters
 *   located outside of a tag is replaced with a single space in the output.
 *   White space located adjacent to a non-inline-level element tag (except {@linkplain TagType#isServerTag() server tags}) may be removed.
 *  <li>If the {@link #setIndentAllElements(boolean) IndentAllElements} property
 *   is set to <code>true</code>, every element appears indented on a new line, including {@linkplain HTMLElements#getInlineLevelElementNames() inline-level elements}.
 *   This generates output that is a good representation of the actual <a href="Source.html#DocumentElementHierarchy">document element hierarchy</a>,
 *   but is very likely to introduce white space that compromises the functional equivalency of the document.
 *  <li>The {@link #setNewLine(String) NewLine} property specifies the character sequence
 *   to use for each <a target="_blank" href="http://en.wikipedia.org/wiki/Newline">newline</a> in the output document.
 *  <li>If the source document contains {@linkplain TagType#isServerTag() server tags}, the functional equivalency of the output document may be compromised.
 * </ul>
 * <p>
 * Formatting an entire {@link Source} object performs a {@linkplain Source#fullSequentialParse() full sequential parse} automatically.
 */
public final class SourceFormatter implements CharStreamSource {
	private final Segment segment;
	private String indentString="\t";
	private boolean tidyTags=false;
	private boolean collapseWhiteSpace=false;
	private boolean removeLineBreaks=false;
	private boolean indentAllElements=false;
	private String newLine=null;

	/**
	 * Constructs a new <code>SourceFormatter</code> based on the specified {@link Segment}.
	 * @param segment  the segment containing the HTML to be formatted.
	 * @see Source#getSourceFormatter()
	 */
	public SourceFormatter(final Segment segment) {
		this.segment=segment;
	}

	// Documentation inherited from CharStreamSource
	public void writeTo(final Writer writer) throws IOException {
		appendTo(writer);
		writer.flush();
	}

	// Documentation inherited from CharStreamSource
	public void appendTo(final Appendable appendable) throws IOException {
		new Processor(segment,getIndentString(),getTidyTags(),getCollapseWhiteSpace(),getRemoveLineBreaks(),getIndentAllElements(),getIndentAllElements(),getNewLine()).appendTo(appendable);
	}

	// Documentation inherited from CharStreamSource
	public long getEstimatedMaximumOutputLength() {
		return segment.length()*2;
	}

	// Documentation inherited from CharStreamSource
	public String toString() {
		return CharStreamSourceUtil.toString(this);
	}

	/**
	 * Sets the string to be used for indentation.
	 * <p>
	 * The default value is a string containing a single tab character (U+0009).
	 * <p>
	 * The most commonly used indent strings are <code>"\t"</code> (single tab), <code>"&nbsp;"</code> (single space), <code>"&nbsp;&nbsp;"</code> (2 spaces), and <code>"&nbsp;&nbsp;&nbsp;&nbsp;"</code> (4 spaces).
	 * 
	 * @param indentString  the string to be used for indentation, must not be <code>null</code>.
	 * @return this <code>SourceFormatter</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getIndentString()
	 */
	public SourceFormatter setIndentString(final String indentString) {
		if (indentString==null) throw new IllegalArgumentException("indentString property must not be null");
		this.indentString=indentString;
		return this;
	}

	/**
	 * Returns the string to be used for indentation.
	 * <p>
	 * See the {@link #setIndentString(String)} method for a full description of this property.
	 *
	 * @return the string to be used for indentation.
	 */
	public String getIndentString() {
		return indentString;
	}

	/**
	 * Sets whether the original text of each tag is to be replaced with the output from its {@link Tag#tidy()} method.
	 * <p>
	 * The default value is <code>false</code>.
	 * <p>
	 * If this property is set to <code>false</code>, the tag from the original text is used, including all white space,
	 * but with any new lines indented at a depth one greater than that of the element.
	 *
	 * @param tidyTags  specifies whether the original text of each tag is to be replaced with the output from its {@link Tag#tidy()} method.
	 * @return this <code>SourceFormatter</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getTidyTags()
	 */
	public SourceFormatter setTidyTags(final boolean tidyTags) {
		this.tidyTags=tidyTags;
		return this;
	}

	/**
	 * Indicates whether the original text of each tag is to be replaced with the output from its {@link Tag#tidy()} method.
	 * <p>
	 * See the {@link #setTidyTags(boolean)} method for a full description of this property.
	 * 
	 * @return <code>true</code> if the original text of each tag is to be replaced with the output from its {@link Tag#tidy()} method, otherwise <code>false</code>.
	 */
	public boolean getTidyTags() {
		return tidyTags;
	}

	/**
	 * Sets whether {@linkplain Segment#isWhiteSpace(char) white space} in the text between the tags is to be collapsed.
	 * <p>
	 * The default value is <code>false</code>.
	 * <p>
	 * If this property is set to <code>true</code>, every string of one or more {@linkplain Segment#isWhiteSpace(char) white space} characters
	 * located outside of a tag is replaced with a single space in the output.
	 * White space located adjacent to a non-inline-level element tag (except {@linkplain TagType#isServerTag() server tags}) may be removed.
	 *
	 * @param collapseWhiteSpace  specifies whether {@linkplain Segment#isWhiteSpace(char) white space} in the text between the tags is to be collapsed.
	 * @return this <code>SourceFormatter</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getCollapseWhiteSpace()
	 */
	public SourceFormatter setCollapseWhiteSpace(final boolean collapseWhiteSpace) {
		this.collapseWhiteSpace=collapseWhiteSpace;
		return this;
	}
	
	/**
	 * Indicates whether {@linkplain Segment#isWhiteSpace(char) white space} in the text between the tags is to be collapsed.
	 * <p>
	 * See the {@link #setCollapseWhiteSpace(boolean collapseWhiteSpace)} method for a full description of this property.
	 * 
	 * @return <code>true</code> if {@linkplain Segment#isWhiteSpace(char) white space} in the text between the tags is to be collapsed, otherwise <code>false</code>.
	 */
	public boolean getCollapseWhiteSpace() {
		return collapseWhiteSpace;
	}

	/**
	 * Sets whether all non-essential line breaks are removed.
	 * <p>
	 * The default value is <code>false</code>.
	 * <p>
	 * If this property is set to <code>true</code>, only essential line breaks are retained in the output.
	 * <p>
	 * Setting this property automatically engages the {@link #setCollapseWhiteSpace(boolean) CollapseWhiteSpace} option, regardless of its property setting.
	 * <p>
	 * It is recommended to set the {@link #setTidyTags(boolean) TidyTags} property when this option is used so that non-essential line breaks are also removed from tags.
	 *
	 * @param removeLineBreaks  specifies whether all non-essential line breaks are removed.
	 * @return this <code>SourceFormatter</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getRemoveLineBreaks()
	 */
	SourceFormatter setRemoveLineBreaks(final boolean removeLineBreaks) {
		this.removeLineBreaks=removeLineBreaks;
		return this;
	}
	
	/**
	 * Indicates whether all non-essential line breaks are removed.
	 * <p>
	 * See the {@link #setRemoveLineBreaks(boolean removeLineBreaks)} method for a full description of this property.
	 * 
	 * @return <code>true</code> if all non-essential line breaks are removed, otherwise <code>false</code>.
	 */
	boolean getRemoveLineBreaks() {
		return removeLineBreaks;
	}

	/**
	 * Sets whether all elements are to be indented, including {@linkplain HTMLElements#getInlineLevelElementNames() inline-level elements} and those with preformatted contents.
	 * <p>
	 * The default value is <code>false</code>.
	 * <p>
	 * If this property is set to <code>true</code>, every element appears indented on a new line, including
	 * {@linkplain HTMLElements#getInlineLevelElementNames() inline-level elements}.
	 * <p>
	 * This generates output that is a good representation of the actual <a href="Source.html#DocumentElementHierarchy">document element hierarchy</a>,
	 * but is very likely to introduce white space that compromises the functional equivalency of the document.
	 *
	 * @param indentAllElements  specifies whether all elements are to be indented.
	 * @return this <code>SourceFormatter</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getIndentAllElements()
	 */
	public SourceFormatter setIndentAllElements(final boolean indentAllElements) {
		this.indentAllElements=indentAllElements;
		return this;
	}

	/**
	 * Indicates whether all elements are to be indented, including {@linkplain HTMLElements#getInlineLevelElementNames() inline-level elements} and those with preformatted contents.
	 * <p>
	 * See the {@link #setIndentAllElements(boolean)} method for a full description of this property.
	 * 
	 * @return <code>true</code> if all elements are to be indented, otherwise <code>false</code>.
	 */
	public boolean getIndentAllElements() {
		return indentAllElements;
	}
	
	/**
	 * Sets the string to be used to represent a <a target="_blank" href="http://en.wikipedia.org/wiki/Newline">newline</a> in the output.
	 * <p>
	 * The default is to use the same new line string as is used in the source document, which is determined via the {@link Source#getNewLine()} method.
	 * If the source document does not contain any new lines, a "best guess" is made by either taking the new line string of a previously parsed document,
	 * or using the value from the static {@link Config#NewLine} property.
	 * <p>
	 * Specifying a <code>null</code> argument resets the property to its default value, which is to use the same new line string as is used in the source document.
	 * 
	 * @param newLine  the string to be used to represent a <a target="_blank" href="http://en.wikipedia.org/wiki/Newline">newline</a> in the output, may be <code>null</code>.
	 * @return this <code>SourceFormatter</code> instance, allowing multiple property setting methods to be chained in a single statement. 
	 * @see #getNewLine()
	 */
	public SourceFormatter setNewLine(final String newLine) {
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
		if (newLine==null) newLine=segment.source.getBestGuessNewLine();
		return newLine;
	}

	/** This class does the actual work, but is first passed final copies of all the parameters for efficiency. */
	private static final class Processor {
		private final Segment segment;
		private final CharSequence sourceText;
		private final String indentString;
		private final boolean tidyTags;
		private final boolean collapseWhiteSpace;
		private final boolean removeLineBreaks; // Indicates whether all non-essential line breaks are removed. Must be used with collapseWhiteSpace=true.
		private final boolean indentAllElements;
		private final boolean indentScriptElements; // at present this parameter is tied to indentAllElements.  SCRIPT elements need to be inline to keep functional equivalency of output
		private final String newLine;
	
		private Appendable appendable;
		private Tag nextTag;
		private int index;
	
		public Processor(final Segment segment, final String indentString, final boolean tidyTags, final boolean collapseWhiteSpace, final boolean removeLineBreaks, final boolean indentAllElements, final boolean indentScriptElements, final String newLine) {
			this.segment=segment;
			sourceText=segment.source.toString();
			this.indentString=indentString;
			this.tidyTags=tidyTags;
			this.collapseWhiteSpace=collapseWhiteSpace || removeLineBreaks;
			this.removeLineBreaks=removeLineBreaks;
			this.indentAllElements=indentAllElements;
			this.indentScriptElements=indentScriptElements;
			this.newLine=newLine;
		}
	
		public void appendTo(final Appendable appendable) throws IOException {
			this.appendable=appendable;
			if (segment instanceof Source) ((Source)segment).fullSequentialParse();
			nextTag=segment.source.getNextTag(segment.begin);
			index=segment.begin;
			appendContent(segment.end,segment.getChildElements(),0);
		}
	
		private void appendContent(final int end, final List<Element> childElements, final int depth) throws IOException {
			assert index<=end;
			for (Element element : childElements) {
				final int elementBegin=element.begin;
				if (elementBegin>=end) break;
				if (indentAllElements) {
					appendText(elementBegin,depth);
					appendElement(element,depth,end,false,false);
				} else {
					if (inlinable(element)) continue; // skip over elements that can be inlined.
					appendText(elementBegin,depth);
					final String elementName=element.getName();
					if (elementName==HTMLElementName.PRE || elementName==HTMLElementName.TEXTAREA) {
						appendElement(element,depth,end,true,true);
					} else if (elementName==HTMLElementName.SCRIPT) {
						appendElement(element,depth,end,true,false);
					} else {
						appendElement(element,depth,end,false,!removeLineBreaks && containsOnlyInlineLevelChildElements(element));
					}
				}
			}
			appendText(end,depth);
			assert index==end;
		}
	
		private boolean inlinable(final Element element) {
			// returns true if the specified element should be inlined
			final StartTagType startTagType=element.getStartTag().getStartTagType();
			if (startTagType==StartTagType.DOCTYPE_DECLARATION) return false;
			if (startTagType!=StartTagType.NORMAL) return true;
			// element is a normal type
			final String elementName=element.getName();
			if (elementName==HTMLElementName.SCRIPT) return !indentScriptElements;
			if (removeLineBreaks && !HTMLElements.getElementNames().contains(elementName)) return true; // inline non-HTML elements if removing line breaks
			if (!HTMLElements.getInlineLevelElementNames().contains(elementName)) return false;
			// element is inline type
			if (removeLineBreaks) return true;
			return containsOnlyInlineLevelChildElements(element); // only inline if it doesn't illegally contain non-inline elements
		}
	
		private void appendText(final int end, int depth) throws IOException {
			assert index<=end;
			if (index==end) return;
			while (Segment.isWhiteSpace(sourceText.charAt(index))) if (++index==end) return; // trim whitespace.
			appendIndent(depth);
			if (collapseWhiteSpace) {
				appendTextCollapseWhiteSpace(end,depth);
			} else {
				appendTextInline(end,depth,false);
			}
			appendFormattingNewLine();
			assert index==end;
		}
	
		private void appendElement(final Element element, final int depth, final int end, final boolean preformatted, boolean renderContentInline) throws IOException {
			assert index==element.begin;
			assert index<end;
			final StartTag startTag=element.getStartTag();
			final EndTag endTag=element.getEndTag();
			appendIndent(depth);
			appendTag(startTag,depth,end);
			if (index==end) {
				appendFormattingNewLine();
				assert index==Math.min(element.end,end) : index;
				return;
			}
			if (!renderContentInline) appendFormattingNewLine();
			int contentEnd=element.getContentEnd();
			if (end<contentEnd) contentEnd=end;
			if (index<contentEnd) {
				if (preformatted) {
					if (renderContentInline) {
						// Preformatted element such as PRE, TEXTAREA
						appendContentPreformatted(contentEnd,depth);
					} else {
						// SCRIPT element
						appendIndentedScriptContent(contentEnd,depth+1);
					}
				} else {
					if (renderContentInline) {
						// Element contains only inline-level elements, so don't bother putting start and end tags on separate lines
						if (collapseWhiteSpace) {
							appendTextCollapseWhiteSpace(contentEnd,depth);
						} else {
							if (!appendTextInline(contentEnd,depth,true)) {
								appendFormattingNewLine();
								renderContentInline=false;
							}
						}
					} else {
						appendContent(contentEnd,element.getChildElements(),depth+1);
					}
				}
			}
			if (endTag!=null && end>endTag.begin) {
				if (!renderContentInline) appendIndent(depth);
				assert index==endTag.begin;
				appendTag(endTag,depth,end);
				appendFormattingNewLine();
			} else if (renderContentInline) {
				appendFormattingNewLine();
			}
			assert index==Math.min(element.end,end) : index;
		}
	
		private void updateNextTag() {
			// ensures that nextTag is up to date
			while (nextTag!=null) {
				if (nextTag.begin>=index) return;
				nextTag=nextTag.getNextTag();
			}
		}
	
		private void appendIndentedScriptContent(final int end, final int depth) throws IOException {
			assert index<end;
			if (removeLineBreaks) {
				appendTextRemoveIndentation(end);
				assert index==end;
				return;
			}
			int startOfLinePos=getStartOfLinePos(end,false);
			if (index==end) return;
			if (startOfLinePos==-1) {
				// Script started on same line as start tag.  Use the start of the next line to determine the original indent.
				appendIndent(depth);
				appendLineKeepWhiteSpace(end,depth);
				appendEssentialNewLine();
				if (index==end) return;
				startOfLinePos=getStartOfLinePos(end,true);
				if (index==end) return;
			}
			appendTextPreserveIndentation(end,depth,index-startOfLinePos);
			appendEssentialNewLine();
			assert index==end;
		}
	
		private boolean appendTextPreserveIndentation(final int end, final int depth) throws IOException {
			// returns true if all text was on one line, otherwise false
			assert index<end;
			if (removeLineBreaks) return appendTextRemoveIndentation(end);
			// Use the start of the next line to determine the original indent.
			appendLineKeepWhiteSpace(end,depth);
			if (index==end) return true;
			int startOfLinePos=getStartOfLinePos(end,true);
			if (index==end) return true;
			appendEssentialNewLine();
			appendTextPreserveIndentation(end,depth+1,index-startOfLinePos);
			assert index==end;
			return false;
		}
	
		private void appendTextPreserveIndentation(final int end, final int depth, final int originalIndentLength) throws IOException {
			assert index<end;
			appendIndent(depth);
			appendLineKeepWhiteSpace(end,depth);
			while (index!=end) {
				// Skip over the original indent:
				for (int x=0; x<originalIndentLength; x++) {
					final char ch=sourceText.charAt(index);
					if (!(ch==' ' || ch=='\t')) break;
					if (++index==end) return;
				}
				appendEssentialNewLine();
				// Insert our indent:
				appendIndent(depth);
				// Write the rest of the line including any indent greater than the first line's indent:
				appendLineKeepWhiteSpace(end,depth);
			}
			assert index==end;
		}

		private boolean appendTextRemoveIndentation(final int end) throws IOException {
			assert index<end;
			appendLineKeepWhiteSpace(end,0);
			if (index==end) return true;
			while (index!=end) {
				// Skip over the original indent:
				while (true) {
					final char ch=sourceText.charAt(index);
					if (!(ch==' ' || ch=='\t')) break;
					if (++index==end) return false;
				}
				appendEssentialNewLine();
				// Write the rest of the line including any indent greater than the first line's indent:
				appendLineKeepWhiteSpace(end,0);
			}
			assert index==end;
			return false;
		}
	
		private int getStartOfLinePos(final int end, final boolean atStartOfLine) {
			// returns the starting position of the next complete line containing text, or -1 if texts starts on the current line (hence not a complete line).
			// sets index to the start of the text following the returned position, or end, whichever comes first.
			int startOfLinePos=atStartOfLine ? index : -1;
			while (true) {
				final char ch=sourceText.charAt(index);
				if (ch=='\n' || ch=='\r') {
					startOfLinePos=index+1;
				} else if (!(ch==' ' || ch=='\t')) break;
				if (++index==end) break;
			}
			return startOfLinePos;
		}
	
		private void appendSpecifiedTextInline(final CharSequence text, int depth) throws IOException {
			final int textLength=text.length();
			int i=appendSpecifiedLine(text,0);
			if (i<textLength) {
				final int subsequentLineDepth=depth+1;
				do {
					while (Segment.isWhiteSpace(text.charAt(i))) if (++i>=textLength) return; // trim whitespace.
					appendEssentialNewLine();
					appendIndent(subsequentLineDepth);
					i=appendSpecifiedLine(text,i);
				} while (i<textLength);
			}
		}
	
		private int appendSpecifiedLine(final CharSequence text, int i) throws IOException {
			// Writes the first line from the specified text starting from the specified position.
			// The line break characters are not written.
			// Returns the position following the first line break character(s), or text.length() if the text contains no line breaks.
			final int textLength=text.length();
			while (true) {
				final char ch=text.charAt(i);
				if (ch=='\r') {
					final int nexti=i+1;
					if (nexti<textLength && text.charAt(nexti)=='\n') return i+2;
				}
				if (ch=='\n') return i+1;
				appendable.append(ch);
				if (++i>=textLength) return i;
			}
		}
	
		private boolean appendTextInline(final int end, int depth, final boolean increaseIndentAfterFirstLineBreak) throws IOException {
			// returns true if all text was on one line, otherwise false
			assert index<end;
			appendLineKeepWhiteSpace(end,depth);
			if (index==end) return true;
			final int subsequentLineDepth=increaseIndentAfterFirstLineBreak ? depth+1 : depth;
			do {
				while (Segment.isWhiteSpace(sourceText.charAt(index))) if (++index==end) return false; // trim whitespace.
				appendEssentialNewLine(); // essential because we might be inside a tag attribute value.  If new lines in normal text aren't required this method wouldn't have been called.
				appendIndent(subsequentLineDepth);
				appendLineKeepWhiteSpace(end,subsequentLineDepth);
			} while (index<end);
			assert index==end;
			return false;
		}
	
		private void appendLineKeepWhiteSpace(final int end, final int depth) throws IOException {
			// Writes the first line from the source text starting from index, ending at the specified end position.
			// The line break characters are not written.
			// Sets index to the position following the first line break character(s), or end if the text contains no line breaks, guaranteed index<=end.
			// Any tags encountered are written using the appendTag method, whose output may include line breaks.
			assert index<end;
			updateNextTag();
			while (true) {
				while (nextTag!=null && index==nextTag.begin) {
					appendTag(nextTag,depth,end);
					if (index==end) return;
				}
				final char ch=sourceText.charAt(index);
				if (ch=='\r') {
					final int nextindex=index+1;
					if (nextindex<end && sourceText.charAt(nextindex)=='\n') {
						index+=2;
						assert index<=end;
						return;
					}
				}
				if (ch=='\n') {
					index++;
					assert index<=end;
					return;
				}
				appendable.append(ch);
				if (++index==end) return;
			}
		}		
	
		private void appendTextCollapseWhiteSpace(final int end, final int depth) throws IOException {
			assert index<end;
			boolean lastWasWhiteSpace=false;
			updateNextTag();
			while (index<end) {
				while (nextTag!=null && index==nextTag.begin) {
					if (lastWasWhiteSpace) {
						appendable.append(' ');
						lastWasWhiteSpace=false;
					}
					appendTag(nextTag,depth,end);
					if (index==end) return;
				}
				final char ch=sourceText.charAt(index++);
				if (Segment.isWhiteSpace(ch)) {
					lastWasWhiteSpace=true;
				} else {
					if (lastWasWhiteSpace) {
						appendable.append(' ');
						lastWasWhiteSpace=false;
					}
					appendable.append(ch);
				}
			}
			if (lastWasWhiteSpace) appendable.append(' ');
			assert index==end;
		}
	
		private void appendContentPreformatted(final int end, final int depth) throws IOException {
			assert index<end;
			updateNextTag();
			do {
				while (nextTag!=null && index==nextTag.begin) {
					appendTag(nextTag,depth,end);
					if (index==end) return;
				}
				appendable.append(sourceText.charAt(index));
			} while (++index<end);
			assert index==end;
		}
	
		private void appendTag(final Tag tag, final int depth, final int end) throws IOException {
			// sets index to last position written
			assert index==tag.begin;
			assert index<end;
			nextTag=tag.getNextTag();
			final int tagEnd=(tag.end<end) ? tag.end : end;
			assert index<tagEnd;
			if (tag.getTagType()==StartTagType.COMMENT || tag.getTagType()==StartTagType.CDATA_SECTION || tag.getTagType().isServerTag()) {
				appendTextPreserveIndentation(tagEnd,depth);
			} else if (tidyTags) {
				final String tidyTag=tag.tidy();
				if ((tag instanceof StartTag) && ((StartTag)tag).getAttributes()!=null)
					appendable.append(tidyTag);
				else
					appendSpecifiedTextInline(tidyTag,depth);
				index=tagEnd;
			} else {
				appendTextInline(tagEnd,depth,true); // Write tag keeping linefeeds. This will add an indent to any attribute values containing linefeeds, but the normal situation where line breaks are between attributes will look nice.
			}
			if (end<=tag.end || !(tag instanceof StartTag)) {
				assert index<=end;
				return;
			}
			if ((tag.name==HTMLElementName.SCRIPT && !indentScriptElements) || tag.getTagType().isServerTag()) {
				// NOTE SERVER ELEMENTS CONTAINING NON-INLINE TAGS WILL NOT FORMAT PROPERLY. NEED TO INVESTIGATE INCLUDING SUCH SERVER ELEMENTS IN DOCUMENT HIERARCHY.
				// this is a script or server start tag, we may need to append the whole element:
				final Element element=tag.getElement();
				final EndTag endTag=element.getEndTag();
				if (endTag==null) {
					assert index<=end;
					return;
				}
				final int contentEnd=(end<endTag.begin) ? end : endTag.begin;
				boolean singleLineContent=true;
				if (index!=contentEnd) {
					// elementContainsMarkup should be made into a TagType property one day.
					// for the time being assume all server element content is code, although this is not true for some Mason elements.
					final boolean elementContainsMarkup=false;
					if (elementContainsMarkup) {
						singleLineContent=appendTextInline(contentEnd,depth+1,false);
					} else {
						singleLineContent=appendTextPreserveIndentation(contentEnd,depth);
					}
				}
				if (endTag.begin>=end) {
					assert index<=end;
					return;
				}
				if (!singleLineContent) {
					appendEssentialNewLine(); // some server or client side scripting languages might need the final new line
					appendIndent(depth);
				}
				assert index==endTag.begin;
				appendTag(endTag,depth,end);
			}
			assert index<=end;
		}
		
	  private void appendIndent(final int depth) throws IOException {
			if (!removeLineBreaks) for (int x=0; x<depth; x++) appendable.append(indentString);
	  }
	
		private void appendFormattingNewLine() throws IOException {
			if (!removeLineBreaks) appendable.append(newLine);
		}

		private void appendEssentialNewLine() throws IOException {
			appendable.append(newLine);
		}
	
		private boolean containsOnlyInlineLevelChildElements(final Element element) {
			// returns true if the element contains only inline-level elements except for SCRIPT elements.
			final Collection<Element> childElements=element.getChildElements();
			if (childElements.isEmpty()) return true;
			for (Element childElement : childElements) {
				final String elementName=childElement.getName();
				if (elementName==HTMLElementName.SCRIPT || !HTMLElements.getInlineLevelElementNames().contains(elementName)) return false;
				if (!containsOnlyInlineLevelChildElements(childElement)) return false;
			}
			return true;
		}
	}
}
