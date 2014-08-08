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
import java.util.*;

/**
 * Defines the interface for an output segment, which is used in an {@link OutputDocument} to
 * replace segments of the source document with other text.
 * <p>
 * All text in the <code>OutputDocument</code> between the character positions defined by {@link #getBegin()} and {@link #getEnd()}
 * is replaced by the content of this output segment.
 * If the begin and end character positions are the same, the content is simply
 * inserted at this position without replacing any text.
 *
 * @see OutputDocument#register(OutputSegment)
 */
public interface OutputSegment extends CharStreamSource {

	/**
	 * The comparator used to sort output segments in the {@link OutputDocument} before output.
	 * <p>
	 * The following rules are applied in order compare two output segments:
	 * <ol>
	 *  <li>The output segment that {@linkplain #getBegin() begins} earlier in the document comes first.
	 *  <li>If both output segments begin at the same position, the one that has zero length comes first.
	 *   If neither or both have zero length then neither is guaranteed to come before the other.
	 * </ol>
 	 * <p>
	 * Note: this comparator has a natural ordering that may be inconsistent with the <code>equals</code>
	 * method of classes implementing this interface.
	 * This means that the comparator may treat two output segments as equal where calling the
	 * <code>equals(Object)</code> method with the same two output segments returns <code>false</code>.
	 */
	public static final Comparator<OutputSegment> COMPARATOR=new OutputSegmentComparator();

	/**
	 * Returns the character position in the {@linkplain OutputDocument#getSourceText() source text of the output document} where this segment begins.
	 * @return the character position in the {@linkplain OutputDocument#getSourceText() source text of the output document} where this segment begins.
	 */
	public int getBegin();

	/**
	 * Returns the character position in the {@linkplain OutputDocument#getSourceText() source text of the output document} where this segment ends.
	 * @return the character position in the {@linkplain OutputDocument#getSourceText() source text of the output document} where this segment ends.
	 */
	public int getEnd();

	/**
	 * Writes the content of this output segment to the specified <code>Writer</code>.
	 * @param writer  the destination <code>java.io.Writer</code> for the output.
	 * @throws IOException if an I/O exception occurs.
	 */
	public void writeTo(Writer writer) throws IOException;

	/**
	 * Appends the content of this output segment to the specified <code>Appendable</code> object.
	 * @param appendable  the destination <code>java.lang.Appendable</code> object for the output.
	 * @throws IOException if an I/O exception occurs.
	 */
	public void appendTo(Appendable appendable) throws IOException;

	/**
	 * Returns the content of this output segment as a <code>String</code>.
	 * @return the content of this output segment as a <code>String</code>, guaranteed not <code>null</code>.
	 * @see #writeTo(Writer)
	 */
	public String toString();

	/**
	 * Returns a string representation of this object useful for debugging purposes.
	 * @return a string representation of this object useful for debugging purposes.
	 */
	public String getDebugInfo();
}
