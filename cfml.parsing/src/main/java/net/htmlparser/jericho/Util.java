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
 * Contains miscellaneous utility methods not directly associated with the HTML Parser library.
 */
public final class Util {
	private static final int BUFFER_SIZE=2048;
	private static final String CSVNewLine=System.getProperty("line.separator");

	private Util() {}

	/**
	 * Returns the text loaded from the specified <code>Reader</code> as a string.
	 * <p>
	 * If a <code>null</code> argument is supplied to this method, an empty string is returned.
	 * <p>
	 * To load text from an <code>InputStream</code>, use <code>getString(new InputStreamReader(inputStream,encoding))</code>.
	 *
	 * @param reader  the <code>java.io.Reader</code> from which to load the text.
	 * @return the text loaded from the specified <code>java.io.Reader</code> as a string.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public static String getString(final Reader reader) throws IOException {
		if (reader==null) return "";
		try {
			int charsRead;
			final char[] copyBuffer=new char[BUFFER_SIZE];
			final StringBuilder sb=new StringBuilder();
			while ((charsRead=reader.read(copyBuffer,0,BUFFER_SIZE))!=-1)
				sb.append(copyBuffer,0,charsRead);
			return sb.toString();
		} finally {
			reader.close();
		}
	}

	/**
	 * Outputs the specified array of strings to the specified <code>Writer</code> in the format of a line for a CSV file.
	 * <p>
	 * "CSV" stands for <i>Comma Separated Values</i>.
	 * There is no formal specification for a CSV file, so there is significant variation in
	 * the way different applications handle issues like the encoding of different data types and special characters.
	 * <p>
	 * Generally, a CSV file contains a list of records separated by line breaks, with each record consisting of a list of 
	 * field values separated by commas.
	 * Each record in the file should contain the same number of field values, with the values at each position representing the same
	 * type of data in all the records.  In this way the file can also be divided into columns, often with the first line of the
	 * file containing the column labels.
	 * <p>
	 * Columns can have different data types such as text, numeric, date / time and boolean.
	 * A text value is often delimited with single (<code>'</code>) or double-quotes (<code>"</code>), 
	 * especially if the value contains a comma, line feed, or other special character that is significant to the syntax.
	 * Encoding techniques for including quote characters themselves in text values vary widely.
	 * Values of other types are generally unquoted to distinguish them from text values.
	 * <p>
	 * This method produces output that is readable by MS-Excel, conforming to the following rules:
	 * <p>
	 * <ul>
	 *  <li>All values are considered to be of type text, except for the static constants {@link Config#ColumnValueTrue}
	 *   and {@link Config#ColumnValueFalse}, representing the boolean values <code>true</code> and <code>false</code> respectively.
	 *  <li>All text values are enclosed in double-quotes.
	 *  <li>Double-quote characters contained in text values are encoded using two consecutive double-quotes (<code>""</code>).
	 *  <li><code>null</code> values are represented as empty fields.
	 *  <li>The end of each record is represented by a carriage-return / line-feed (CR/LF) pair.
	 *  <li>Line breaks inside text values are represented by a single line feed (LF) character.
	 * </ul>
	 *
	 * @param writer  the destination <code>java.io.Writer</code> for the output.
	 * @throws java.io.IOException if an I/O error occurs.
	 * @see FormFields#getColumnLabels()
	 * @see FormFields#getColumnValues(Map)
	 */
  public static void outputCSVLine(final Writer writer, final String[] values) throws IOException {
  	for (int i=0; i<values.length;) {
			final String value=values[i];
  		if (value!=null) {
				if (value==Config.ColumnValueTrue || value==Config.ColumnValueFalse) {
					writer.write(value); // assumes neither ColumnTrue or ColumnFalse contain double quotes.
				} else {
		 			writer.write('"');
					outputValueEscapeQuotes(writer,value);
					writer.write('"');
				}
			}
			if (++i!=values.length) writer.write(',');
  	}
		writer.write(CSVNewLine);
  }

  private static void outputValueEscapeQuotes(final Writer writer, final String text) throws IOException {
		for (int i=0; i<text.length(); i++) {
			final char ch=text.charAt(i);
			writer.write(ch);
			if (ch=='"') writer.write(ch);
		}
  }

	static char[] getConcatenatedCharArray(final String string1, final String string2) {
		final char[] charArray=new char[string1.length()+string2.length()];
		string1.getChars(0,string1.length(),charArray,0);
		string2.getChars(0,string2.length(),charArray,string1.length());
		return charArray;
	}
}
