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
import java.nio.*;

/**
 * Contains static utility methods for manipulating the way data is retrieved from a {@link CharStreamSource} object.
 * <p>
 * See the documentation of the {@link CharStreamSource} class for details.
 */
public final class CharStreamSourceUtil {
	private static final int DEFAULT_ESTIMATED_MAXIMUM_OUTPUT_LENGTH=2048;

	private CharStreamSourceUtil() {}

	/**
	 * Returns a <code>Reader</code> that reads the output of the specified {@link CharStreamSource}.
	 * <p>
	 * The current implementation of this method simply returns <code>new StringReader(</code>{@link #toString(CharStreamSource) toString(charStreamSource)}<code>)</code>,
	 * but a future version may implement this method in a more memory efficient manner.
	 *
	 * @param charStreamSource  the character stream source producing the output.
	 * @return a <code>Reader</code> that reads the output of the specified {@link CharStreamSource}.
	 */
	public static Reader getReader(final CharStreamSource charStreamSource) {
		return new StringReader(toString(charStreamSource));
	}

	/**
	 * Returns the output of the specified {@link CharStreamSource} as a string.
	 * <p>
	 * The current implementation of this method simply returns <code>new StringReader(</code>{@link #toString(CharStreamSource) toString(charStreamSource)}<code>)</code>,
	 * but a future version may implement this method in a more memory efficient manner, for example by utilising a temporary file.
	 *
	 * @param charStreamSource  the character stream source producing the output.
	 * @return the output of the specified {@link CharStreamSource} as a string.
	 */
	public static String toString(final CharStreamSource charStreamSource) {
		long estimatedMaximumOutputLength=charStreamSource.getEstimatedMaximumOutputLength();
		if (estimatedMaximumOutputLength<=-1L) estimatedMaximumOutputLength=DEFAULT_ESTIMATED_MAXIMUM_OUTPUT_LENGTH;
		final StringBuilder sb=new StringBuilder((int)(estimatedMaximumOutputLength));
		try {
			charStreamSource.appendTo(sb);
		} catch (IOException ex) {throw new RuntimeException(ex);} // assume the IOException is not thrown explicitly by the charStreamSource.output method
		return sb.toString();
	}
}
