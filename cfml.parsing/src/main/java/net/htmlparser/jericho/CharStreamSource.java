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
 * Represents a character stream source.  The purpose of a class that implements this interface is to output text.
 * <p>
 * For small amounts of data, or when memory usage isn't a prime concern (e.g. in client-side applications), the simplest way to obtain the data is by calling
 * the {@link #toString()} method.  
 * If the character stream might contain a large amount of data it is recommended to use the {@link #writeTo(Writer)} method to access the data,
 * especially if running in a multi-user server environment.
 * <p>
 * The advantage of providing textual data via this interface is that it gives the user the choice as to whether they would like to receive the data as a stream
 * of characters, or all as a single string.  Furthermore, it allows the "active" stream source (see below) to be easily converted into a "passive" stream source 
 * if required.
 * <p>
 * An <i><a name="Active">active stream source</a></i> is a stream source that actively outputs to a passive receiver ("sink").
 * The {@link #writeTo(Writer)} method in this interface signifies an active source as the transmission of the entire data stream takes place when this method is executed.
 * In this case the sink is the object that supplies the <code>Writer</code> object, and would typically contain a <code>getWriter()</code> method.
 * The sink is passive because it just supplies a <code>Writer</code> object to be written to by the code in some other class.
 * <p>
 * A <i><a name="Passive">passive stream source</a></i> is a stream source that is read from by an active sink.
 * For character streams, a passive stream source simply supplies a <code>Reader</code> object.
 * The active sink would typically contain a <code>readFrom(Reader)</code> method which actively reads the entire data stream from the <code>Reader</code> object.
 * <p>
 * The {@link CharStreamSourceUtil#getReader(CharStreamSource)} method converts a <code>CharStreamSource</code> into a <code>Reader</code>,
 * allowing the data from the active <code>CharStreamSource</code> to be consumed by an active sink with a <code>readFrom(Reader)</code> method.
 * <p>
 * Every implementing class must override the {@link #toString()} method to return the output as a string.
 * <p>
 * An easy way to implement this is by calling the {@link CharStreamSourceUtil#toString(CharStreamSource) CharStreamSourceUtil.toString(this)} method,
 * which buffers the output from the {@link #writeTo(Writer)} method into a string.
 *
 * @see OutputDocument
 * @see SourceFormatter
 * @see Renderer
 * @see TextExtractor
 */
public interface CharStreamSource {
	/**
	 * Writes the output to the specified <code>Writer</code>.
	 *
	 * @param writer  the destination <code>java.io.Writer</code> for the output.
	 * @throws IOException if an I/O exception occurs.
	 */
	void writeTo(Writer writer) throws IOException;

	/**
	 * Appends the output to the specified <code>Appendable</code> object.
	 *
	 * @param appendable  the destination <code>java.lang.Appendable</code> object for the output.
	 * @throws IOException if an I/O exception occurs.
	 */
	void appendTo(Appendable appendable) throws IOException;

	/**
	 * Returns the estimated maximum number of characters in the output, or <code>-1</code> if no estimate is available.
	 * <p>
	 * The returned value should be used as a guide for efficiency purposes only, for example to set an initial <code>StringBuilder</code> capacity.
	 * There is no guarantee that the length of the output is indeed less than this value,
	 * as classes implementing this method often use assumptions based on typical usage to calculate the estimate.
	 * <p>
	 * Although implementations of this method should never return a value less than -1, users of this method must not assume that this will always be the case.
	 * Standard practice is to interpret any negative value as meaning that no estimate is available.
	 *
	 * @return the estimated maximum number of characters in the output, or <code>-1</code> if no estimate is available.
	 */
	long getEstimatedMaximumOutputLength();

	/**
	 * Returns the output as a string.
	 * @return the output as a string.
	 */
	String toString();
}
