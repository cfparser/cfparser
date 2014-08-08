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

import java.io.*;

/**
 * Implements an {@link OutputSegment} whose content is a <code>CharSequence</code>.
 * <p>
 * This class has been removed from the pulic API and the functionality replaced with the
 * {@link OutputDocument#replace(Segment, CharSequence text)} method.
 */
final class StringOutputSegment implements OutputSegment {
	private final int begin;
	private final int end;
	private final CharSequence text;

	/**
	 * Constructs a new <code>StringOutputSegment</code> with the specified begin and end positions and the specified content.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>text</code> parameter is exactly equivalent to specifying an empty string,
	 * and results in the segment being completely removed from the output document.
	 *
	 * @param begin  the position in the <code>OutputDocument</code> where this output segment begins.
	 * @param end  the position in the <code>OutputDocument</code> where this output segment ends.
	 * @param text  the textual content of the new output segment, or <code>null</code> if no content.
	 */
	public StringOutputSegment(final int begin, final int end, final CharSequence text) {
		this.begin=begin;
		this.end=end;
		this.text=(text==null ? "" : text);
	}

	/**
	 * Constructs a new StringOutputSegment</code> with the same span as the specified {@link Segment}.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>text</code> parameter is exactly equivalent to specifying an empty string,
	 * and results in the segment being completely removed from the output document.
	 *
	 * @param segment  a segment defining the beginning and ending positions of the new output segment.
	 * @param text  the textual content of the new output segment, or <code>null</code> if no content.
	 */
	public StringOutputSegment(final Segment segment, final CharSequence text) {
		this(segment.begin,segment.end,text);
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public void writeTo(final Writer writer) throws IOException {
		appendTo(writer);
	}

	public void appendTo(final Appendable appendable) throws IOException {
		appendable.append(text);
	}

	public long getEstimatedMaximumOutputLength() {
		return text.length();
	}

	public String toString() {
		return text.toString();
	}

	public String getDebugInfo() {
		return "Replace: (p"+begin+"-p"+end+") "+text;
	}

	public void output(final Writer writer) throws IOException {
		writeTo(writer);
	}
}
