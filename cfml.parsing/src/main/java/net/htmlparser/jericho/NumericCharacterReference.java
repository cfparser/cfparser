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
 * Represents an HTML <a target="_blank" href="http://www.w3.org/TR/REC-html40/charset.html#h-5.3.1">Numeric Character Reference</a>.
 * <p>
 * A numeric character reference can be one of two types:
 * <dl>
 *  <dt><a name="DecimalCharacterReference">Decimal Character Reference</a>
 *   <dd>A numeric character reference specifying the unicode code point in decimal notation.<br />
 *    This is signified by the absence of an '<code>x</code>' character after the '<code>#</code>', (eg "<code>&amp;#62;</code>").
 *  <dt><a name="HexadecimalCharacterReference">Hexadecimal Character Reference</a>
 *   <dd>A numeric character reference specifying the unicode code point in hexadecimal notation.<br />
 *    This is signified by the presence of an '<code>x</code>' character after the '<code>#</code>', (eg "<code>&amp;#x3e;</code>").
 * </dl>
 * <p>
 * Static methods to {@linkplain #encode(CharSequence) encode} and {@linkplain #decode(CharSequence) decode} strings
 * and single characters can be found in the {@link CharacterReference} superclass.
 * <p>
 * <code>NumericCharacterReference</code> instances are obtained using one of the following methods:
 * <ul>
 *  <li>{@link CharacterReference#parse(CharSequence characterReferenceText)}
 *  <li>{@link Source#getNextCharacterReference(int pos)}
 *  <li>{@link Source#getPreviousCharacterReference(int pos)}
 *  <li>{@link Segment#getAllCharacterReferences()}
 * </ul>
 *
 * @see CharacterReference
 * @see CharacterEntityReference
 */
public class NumericCharacterReference extends CharacterReference {
	private boolean hex;

	private NumericCharacterReference(final Source source, final int begin, final int end, final int codePoint, final boolean hex) {
		super(source,begin,end,codePoint);
		this.hex=hex;
	}

	/**
	 * Indicates whether this numeric character reference specifies the unicode code point in decimal format.
	 * <p>
	 * A numeric character reference in decimal format is referred to in this library as a
	 * <a href="#DecimalCharacterReference">decimal character reference</a>.
	 *
	 * @return <code>true</code> if this numeric character reference specifies the unicode code point in decimal format, otherwise <code>false</code>.
	 * @see #isHexadecimal()
	 */
	public boolean isDecimal() {
		return !hex;
	}

	/**
	 * Indicates whether this numeric character reference specifies the unicode code point in hexadecimal format.
	 * <p>
	 * A numeric character reference in hexadecimal format is referred to in this library as a
	 * <a href="#HexadecimalCharacterReference">hexadecimal character reference</a>.
	 *
	 * @return <code>true</code> if this numeric character reference specifies the unicode code point in hexadecimal format, otherwise <code>false</code>.
	 * @see #isDecimal()
	 */
	public boolean isHexadecimal() {
		return hex;
	}

	/**
	 * Encodes the specified text, escaping special characters into numeric character references.
	 * <p>
	 * Each character is encoded only if the {@link #requiresEncoding(char) requiresEncoding(char)} method would return <code>true</code> for that character.
	 * <p>
	 * This method encodes all character references in <a href="#DecimalCharacterReference">decimal format</a>, and is exactly the same as calling
	 * {@link #encodeDecimal(CharSequence)}.
	 * <p>
	 * To encode text using both character entity references and numeric character references, use the<br />
	 * {@link CharacterReference#encode(CharSequence)} method instead.
	 * <p>
	 * To encode text using <a href="#HexadecimalCharacterReference">hexadecimal character references</a> only,
	 * use the {@link #encodeHexadecimal(CharSequence)} method instead.
	 *
	 * @param unencodedText  the text to encode.
	 * @return the encoded string.
	 * @see #decode(CharSequence)
	 */
	public static String encode(final CharSequence unencodedText) {
		if (unencodedText==null) return null;
		final StringBuilder sb=new StringBuilder(unencodedText.length()*2);
		for (int i=0; i<unencodedText.length(); i++) {
			final char ch=unencodedText.charAt(i);
			if (requiresEncoding(ch)) {
				try {
					appendDecimalCharacterReferenceString(sb,ch);
				} catch (IOException ex) {throw new RuntimeException(ex);} // never happens
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	/**
	 * Encodes the specified text, escaping special characters into <a href="#DecimalCharacterReference">decimal character references</a>.
	 * <p>
	 * Each character is encoded only if the {@link #requiresEncoding(char) requiresEncoding(char)} method would return <code>true</code> for that character.
	 * <p>
	 * To encode text using both character entity references and numeric character references, use the<br />
	 * {@link CharacterReference#encode(CharSequence)} method instead.
	 * <p>
	 * To encode text using <a href="#HexadecimalCharacterReference">hexadecimal character references</a> only,
	 * use the {@link #encodeHexadecimal(CharSequence)} method instead.
	 *
	 * @param unencodedText  the text to encode.
	 * @return the encoded string.
	 * @see #decode(CharSequence)
	 */
	public static String encodeDecimal(final CharSequence unencodedText) {
		return encode(unencodedText);
	}

	/**
	 * Encodes the specified text, escaping special characters into <a href="#HexadecimalCharacterReference">hexadecimal character references</a>.
	 * <p>
	 * Each character is encoded only if the {@link #requiresEncoding(char) requiresEncoding(char)} method would return <code>true</code> for that character.
	 * <p>
	 * To encode text using both character entity references and numeric character references, use the<br />
	 * {@link CharacterReference#encode(CharSequence)} method instead.
	 * <p>
	 * To encode text using <a href="#DecimalCharacterReference">decimal character references</a> only,
	 * use the {@link #encodeDecimal(CharSequence)} method instead.
	 *
	 * @param unencodedText  the text to encode.
	 * @return the encoded string.
	 * @see #decode(CharSequence)
	 */
	public static String encodeHexadecimal(final CharSequence unencodedText) {
		if (unencodedText==null) return null;
		final StringBuilder sb=new StringBuilder(unencodedText.length()*2);
		for (int i=0; i<unencodedText.length(); i++) {
			final char ch=unencodedText.charAt(i);
			if (requiresEncoding(ch)) {
				try {
					appendHexadecimalCharacterReferenceString(sb,ch);
				} catch (IOException ex) {throw new RuntimeException(ex);} // never happens
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	/**
	 * Returns the correct encoded form of this numeric character reference.
	 * <p>
	 * The returned string uses the same radix as the original character reference in the source document,
	 * i.e. decimal format if {@link #isDecimal()} is <code>true</code>, and hexadecimal format if {@link #isHexadecimal()} is <code>true</code>.
	 * <p>
	 * Note that the returned string is not necessarily the same as the original source text used to create this object.
	 * This library recognises certain invalid forms of character references,
	 * as detailed in the {@link #decode(CharSequence) decode(CharSequence)} method.
	 * <p>
	 * To retrieve the original source text, use the {@link #toString() toString()} method instead.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *   <dd><code>CharacterReference.parse("&amp;#62").getCharacterReferenceString()</code> returns "<code>&amp;#62;</code>"</dd>
	 * </dl>
	 *
	 * @return the correct encoded form of this numeric character reference.
	 * @see CharacterReference#getCharacterReferenceString(int codePoint)
	 */
	public String getCharacterReferenceString() {
		return hex ? getHexadecimalCharacterReferenceString(codePoint) : getDecimalCharacterReferenceString(codePoint);
	}

	/**
	 * Returns the numeric character reference encoded form of the specified unicode code point.
	 * <p>
	 * This method returns the character reference in decimal format, and is exactly the same as calling
	 * {@link #getDecimalCharacterReferenceString(int codePoint)}.
	 * <p>
	 * To get either the character entity reference or numeric character reference, use the<br />
	 * {@link CharacterReference#getCharacterReferenceString(int codePoint)} method instead.
	 * <p>
	 * To get the character reference in hexadecimal format, use the {@link #getHexadecimalCharacterReferenceString(int codePoint)} method instead.
	 * <p>
	 * <dl>
	 *  <dt>Examples:</dt>
	 *   <dd><code>NumericCharacterReference.getCharacterReferenceString(62)</code> returns "<code>&amp;#62;</code>"</dd>
	 *   <dd><code>NumericCharacterReference.getCharacterReferenceString('&gt;')</code> returns "<code>&amp;#62;</code>"</dd>
	 * </dl>
	 *
	 * @return the numeric character reference encoded form of the specified unicode code point.
	 * @see CharacterReference#getCharacterReferenceString(int codePoint)
	 */
	public static String getCharacterReferenceString(final int codePoint) {
		return getDecimalCharacterReferenceString(codePoint);
	}

	static CharacterReference construct(final Source source, final int begin, final Config.UnterminatedCharacterReferenceSettings unterminatedCharacterReferenceSettings) {
		// only called from CharacterReference.construct(), so we can assume that first characters are "&#"
		final ParseText parseText=source.getParseText();
		int codePointStringBegin=begin+2;
		boolean hex;
		if (hex=(parseText.charAt(codePointStringBegin)=='x')) codePointStringBegin++;
		final int unterminatedMaxCodePoint=hex ? unterminatedCharacterReferenceSettings.hexadecimalCharacterReferenceMaxCodePoint : unterminatedCharacterReferenceSettings.decimalCharacterReferenceMaxCodePoint;
		final int maxSourcePos=source.end-1;
		String codePointString;
		int end;
		int x=codePointStringBegin;
		boolean unterminated=false;
		while (true) {
			final char ch=parseText.charAt(x);
			if (ch==';') {
				end=x+1;
				codePointString=source.substring(codePointStringBegin,x);
				break;
			}
			if ((ch>='0' && ch<='9') || (hex && ((ch>='a' && ch<='f') || (ch>='A' && ch<='F')))) {
				// We have a valid decimal digit (if hex is false), or a hexadecimal digit (if hex is true)
				if (x==maxSourcePos) {
					// We are at the last position in the source text without the terminating semicolon.
					unterminated=true;
					x++; // include this digit
				}
			} else {
				// We don't have a valid digit, meaning the character reference is unterminated.
				unterminated=true;
			}
			if (unterminated) {
				// Different browsers react differently to unterminated numeric character references.
				// The behaviour of this method is determined by the settings in the unterminatedCharacterReferenceSettings parameter.
				if (unterminatedMaxCodePoint==INVALID_CODE_POINT) {
					// reject:
					return null;
				} else {
					// accept:
					end=x;
					codePointString=source.substring(codePointStringBegin,x);
					break;
				}
			}
			x++;
		}
		if (codePointString.length()==0) return null;
		int codePoint=INVALID_CODE_POINT;
		try {
			codePoint=Integer.parseInt(codePointString,hex?16:10);
			if (unterminated && codePoint>unterminatedMaxCodePoint) return null;
			if (codePoint>Character.MAX_CODE_POINT) codePoint=INVALID_CODE_POINT;
		} catch (NumberFormatException ex) {
			// This should only happen if number is larger than Integer.MAX_VALUE.
			if (unterminated) return null;
			// If it is a terminated reference just ignore the exception as codePoint will remain with its value of INVALID_CODE_POINT.
		}
		return new NumericCharacterReference(source,begin,end,codePoint,hex);
	}

	public String getDebugInfo() {
		final StringBuilder sb=new StringBuilder();
		sb.append('"');
		try {
			if (hex)
				appendHexadecimalCharacterReferenceString(sb,codePoint);
			else
				appendDecimalCharacterReferenceString(sb,codePoint);
			sb.append("\" ");
			appendUnicodeText(sb,codePoint);
		} catch (IOException ex) {throw new RuntimeException(ex);} // never happens
		sb.append(' ').append(super.getDebugInfo());
		return sb.toString();
	}
}

