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
 * Compacts HTML source by removing all unnecessary white space.
 * <p>
 * Use one of the following methods to obtain the output:
 * <ul>
 *  <li>{@link #writeTo(Writer)}</li>
 *  <li>{@link #appendTo(Appendable)}</li>
 *  <li>{@link #toString()}</li>
 *  <li>{@link CharStreamSourceUtil#getReader(CharStreamSource) CharStreamSourceUtil.getReader(this)}</li>
 * </ul>
 * <p>
 * The output text is functionally equivalent to the original source and should be rendered identically.
 * <p>
 * Compacting an entire {@link Source} object performs a {@linkplain Source#fullSequentialParse() full sequential parse} automatically.
 */
public final class SourceCompactor implements CharStreamSource {
	private final Segment segment;
	private String newLine=null;

	/**
	 * Constructs a new <code>SourceCompactor</code> based on the specified {@link Segment}.
	 * @param segment  the segment containing the HTML to be compacted.
	 */
	public SourceCompactor(final Segment segment) {
		this.segment=segment;
	}

	// Documentation inherited from CharStreamSource
	public void writeTo(final Writer writer) throws IOException {
		appendTo(writer);
		writer.flush();
	}

	// Documentation inherited from CharStreamSource
	public void appendTo(final Appendable appendable) throws IOException {
		new SourceFormatter(segment).setTidyTags(true).setNewLine(newLine).setRemoveLineBreaks(true).appendTo(appendable);
	}

	// Documentation inherited from CharStreamSource
	public long getEstimatedMaximumOutputLength() {
		return segment.length();
	}

	// Documentation inherited from CharStreamSource
	public String toString() {
		return CharStreamSourceUtil.toString(this);
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
	public SourceCompactor setNewLine(final String newLine) {
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

}
