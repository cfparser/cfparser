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

/**
 * Represents the text from the {@linkplain Source source} document that is to be parsed.
 * <p>
 * This interface is normally only of interest to users who wish to create <a href="TagType.html#Custom">custom tag types</a>.
 * <p>
 * The parse text is defined as the entire text of the source document in lower case, with all
 * {@linkplain Segment#ignoreWhenParsing() ignored} segments replaced by space characters.
 * <p>
 * The text is stored in lower case to make case insensitive parsing as efficient as possible.
 * <p>
 * This interface provides many methods which are also provided by the <code>java.lang.String</code> class,
 * but adds an extra parameter called <code>breakAtIndex</code> to the various <code>indexOf</code> methods.
 * This parameter allows a search on only a specified segment of the text, which is not possible using the normal <code>String</code> class.
 * <p>
 * <code>ParseText</code> instances are obtained using the {@link Source#getParseText()} method.
 */
public interface ParseText extends CharSequence {
	/** A value to use as the <code>breakAtIndex</code> argument in certain methods to indicate that the search should continue to the start or end of the parse text. */
	public static final int NO_BREAK=-1;

	/**
	 * Returns the character at the specified index.
	 * @param index  the index of the character.
	 * @return the character at the specified index, which is always in lower case.
	 */
	public char charAt(int index);

	/**
	 * Indicates whether this parse text contains the specified string at the specified position.
	 * <p>
	 * This method is analogous to the <code>java.lang.String.startsWith(String prefix, int toffset)</code> method.
	 *
	 * @param str  a string.
	 * @param pos  the position (index) in this parse text at which to check for the specified string.
	 * @return <code>true</code> if this parse text contains the specified string at the specified position, otherwise <code>false</code>.
	 */
	public boolean containsAt(String str, int pos);

	/**
	 * Returns the index within this parse text of the first occurrence of the specified character,
	 * starting the search at the position specified by <code>fromIndex</code>.
	 * <p>
	 * If the specified character is not found then -1 is returned.
	 *
	 * @param searchChar  a character.
	 * @param fromIndex  the index to start the search from. 
	 * @return the index within this parse text of the first occurrence of the specified character within the specified range, or -1 if the character is not found.
	 */
	public int indexOf(char searchChar, int fromIndex);
	
	/**
	 * Returns the index within this parse text of the first occurrence of the specified character,
	 * starting the search at the position specified by <code>fromIndex</code>,
	 * and breaking the search at the index specified by <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in the search.
	 * <p>
	 * If the search is to continue to the end of the text,
	 * the value {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the <code>breakAtIndex</code>.
	 * <p>
	 * If the specified character is not found then -1 is returned.
	 *
	 * @param searchChar  a character.
	 * @param fromIndex  the index to start the search from.
	 * @param breakAtIndex  the index at which to break off the search, or {@link #NO_BREAK} if the search is to continue to the end of the text.
	 * @return the index within this parse text of the first occurrence of the specified character within the specified range, or -1 if the character is not found.
	 */
	public int indexOf(char searchChar, int fromIndex, int breakAtIndex);

	/**
	 * Returns the index within this parse text of the first occurrence of the specified string,
	 * starting the search at the position specified by <code>fromIndex</code>.
	 * <p>
	 * If the specified string is not found then -1 is returned.
	 *
	 * @param searchString  a string.
	 * @param fromIndex  the index to start the search from. 
	 * @return the index within this parse text of the first occurrence of the specified string within the specified range, or -1 if the string is not found.
	 */
	public int indexOf(String searchString, int fromIndex);

	/**
	 * Returns the index within this parse text of the first occurrence of the specified string,
	 * starting the search at the position specified by <code>fromIndex</code>,
	 * and breaking the search at the index specified by <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in the search.
	 * <p>
	 * If the search is to continue to the end of the text,
	 * the value {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the <code>breakAtIndex</code>.
	 * <p>
	 * If the specified string is not found then -1 is returned.
	 *
	 * @param searchString  a string.
	 * @param fromIndex  the index to start the search from.
	 * @param breakAtIndex  the index at which to break off the search, or {@link #NO_BREAK} if the search is to continue to the end of the text.
	 * @return the index within this parse text of the first occurrence of the specified string within the specified range, or -1 if the string is not found.
	 */
	public int indexOf(String searchString, int fromIndex, int breakAtIndex);

	/**
	 * Returns the index within this parse text of the last occurrence of the specified character,
	 * searching backwards starting at the position specified by <code>fromIndex</code>.
	 * <p>
	 * If the specified character is not found then -1 is returned.
	 *
	 * @param searchChar  a character.
	 * @param fromIndex  the index to start the search from. 
	 * @return the index within this parse text of the last occurrence of the specified character within the specified range, or -1 if the character is not found.
	 */
	public int lastIndexOf(char searchChar, int fromIndex);
	
	/**
	 * Returns the index within this parse text of the last occurrence of the specified character,
	 * searching backwards starting at the position specified by <code>fromIndex</code>,
	 * and breaking the search at the index specified by <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in the search.
	 * <p>
	 * If the search is to continue to the start of the text,
	 * the value {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the <code>breakAtIndex</code>.
	 * <p>
	 * If the specified character is not found then -1 is returned.
	 *
	 * @param searchChar  a character.
	 * @param fromIndex  the index to start the search from.
	 * @param breakAtIndex  the index at which to break off the search, or {@link #NO_BREAK} if the search is to continue to the start of the text.
	 * @return the index within this parse text of the last occurrence of the specified character within the specified range, or -1 if the character is not found.
	 */
	public int lastIndexOf(char searchChar, int fromIndex, int breakAtIndex);

	/**
	 * Returns the index within this parse text of the last occurrence of the specified string,
	 * searching backwards starting at the position specified by <code>fromIndex</code>.
	 * <p>
	 * If the specified string is not found then -1 is returned.
	 *
	 * @param searchString  a string.
	 * @param fromIndex  the index to start the search from. 
	 * @return the index within this parse text of the last occurrence of the specified string within the specified range, or -1 if the string is not found.
	 */
	public int lastIndexOf(String searchString, int fromIndex);

	/**
	 * Returns the index within this parse text of the last occurrence of the specified string,
	 * searching backwards starting at the position specified by <code>fromIndex</code>,
	 * and breaking the search at the index specified by <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in the search.
	 * <p>
	 * If the search is to continue to the start of the text,
	 * the value {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the <code>breakAtIndex</code>.
	 * <p>
	 * If the specified string is not found then -1 is returned.
	 *
	 * @param searchString  a string.
	 * @param fromIndex  the index to start the search from.
	 * @param breakAtIndex  the index at which to break off the search, or {@link #NO_BREAK} if the search is to continue to the start of the text.
	 * @return the index within this parse text of the last occurrence of the specified string within the specified range, or -1 if the string is not found.
	 */
	public int lastIndexOf(String searchString, int fromIndex, int breakAtIndex);
	
	/**
	 * Returns the length of the parse text.
	 * @return the length of the parse text.
	 */
	public int length();

	/**
	 * Returns a new character sequence that is a subsequence of this sequence.
	 *
	 * @param begin  the begin position, inclusive.
	 * @param end  the end position, exclusive.
	 * @return a new character sequence that is a subsequence of this sequence.
	 */
	public CharSequence subSequence(int begin, int end);

	/**
	 * Returns the content of the parse text as a <code>String</code>.
	 * @return the content of the parse text as a <code>String</code>.
	 */
	public String toString();
}
