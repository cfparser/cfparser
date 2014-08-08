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
 * Implements an {@link OutputSegment} whose content is a string of spaces with the same length as the segment.
 * <p>
 * This class has been removed from the public API and the functionality replaced with the 
 * {@link OutputDocument#ReplaceWithSpaces(int begin, int end)} method.
 */
final class BlankOutputSegment implements OutputSegment {
	private final int begin;
	private final int end;

	/**
	 * Constructs a new <code>BlankOutputSegment</code> with the specified begin and end positions.
	 * @param begin  the position in the {@link OutputDocument} where this <code>OutputSegment</code> begins.
	 * @param end  the position in the {@link OutputDocument} where this <code>OutputSegment</code> ends.
	 */
	public BlankOutputSegment(final int begin, final int end) {
		this.begin=begin;
		this.end=end;
	}

	/**
	 * Constructs a new <code>BlankOutputSegment</code> with the same span as the specified {@link Segment}.
	 * @param segment  a {@link Segment} defining the begin and end character positions of the new <code>OutputSegment</code>.
	 */
	public BlankOutputSegment(final Segment segment) {
		this(segment.getBegin(),segment.getEnd());
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
		for (int i=begin; i<end; i++) appendable.append(' ');
	}

	public long getEstimatedMaximumOutputLength() {
		return end-begin;
	}

	public String toString() {
		StringBuilder sb=new StringBuilder(end-begin);
		for (int i=begin; i<end; i++) sb.append(' ');
		return sb.toString();
	}

	public String getDebugInfo() {
		return "Replace with Spaces: (p"+begin+"-p"+end+')';
	}
}
