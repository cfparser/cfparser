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
 * Implements an {@link OutputSegment} whose content is a single character constant.
 * <p>
 * This class has been removed from the public API and the functionality replaced with the 
 * {@link OutputDocument#Replace(int begin, int end, char ch)} method.
 */
final class CharOutputSegment implements OutputSegment {
	private final int begin;
	private final int end;
	private final char ch;

	/**
	 * Constructs a new <code>CharOutputSegment</code> with the specified begin and end character positions and the specified content.
	 * @param begin  the position in the {@link OutputDocument} where this <code>OutputSegment</code> begins.
	 * @param end  the position in the {@link OutputDocument} where this <code>OutputSegment</code> ends.
	 * @param ch  the character output of the new <code>OutputSegment</code>.
	 */
	public CharOutputSegment(final int begin, final int end, final char ch) {
		this.begin=begin;
		this.end=end;
		this.ch=ch;
	}

	/**
	 * Constructs a new <code>CharOutputSegment</code> with the same span as the specified {@link Segment}.
	 * @param segment  a {@link Segment} defining the begin and end character positions of the new <code>OutputSegment</code>.
	 * @param ch  the character output of the new <code>OutputSegment</code>.
	 */
	public CharOutputSegment(final Segment segment, final char ch) {
		begin=segment.begin;
		end=segment.end;
		this.ch=ch;
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
		appendable.append(ch);
	}

	public long getEstimatedMaximumOutputLength() {
		return 1;
	}

	public String toString() {
		return Character.toString(ch);
	}

	public String getDebugInfo() {
		return "Replace: (p"+begin+"-p"+end+") "+ch;
	}
}
