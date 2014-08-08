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
import java.nio.charset.*;
import java.net.*;

/**
 * Based on information in:
 * http://www.w3.org/TR/REC-xml/#sec-guessing-no-ext-info
 * http://www.w3.org/TR/html401/charset.html#h-5.2
 */
final class StreamEncodingDetector {
	private final InputStream inputStream;
	private String encoding=null;
	private String encodingSpecificationInfo=null;
	private boolean definitive=true;
	private boolean documentSpecifiedEncodingPossible=true;
	
	private static final String UTF_16="UTF-16";
	private static final String UTF_16BE="UTF-16BE";
	private static final String UTF_16LE="UTF-16LE";
	private static final String UTF_8="UTF-8";
	private static final String ISO_8859_1="ISO-8859-1";
	private static final String EBCDIC="Cp037"; // aka IBM037, not guaranteed, but available on most platforms

	// All of the following encodings are generally not supported in java and will usually throw an exception if decoding is attempted.
	// Specified explicitly using Byte Order Mark:
	private static final String SCSU="SCSU";
	private static final String UTF_7="UTF-7";
	private static final String UTF_EBCDIC="UTF-EBCDIC";
	private static final String BOCU_1="BOCU-1";
	private static final String UTF_32="UTF-32";
	// Guessed from presence of 00 bytes in first four bytes:
	private static final String UTF_32BE="UTF-32BE";
	private static final String UTF_32LE="UTF-32LE";

	public StreamEncodingDetector(final URLConnection urlConnection) throws IOException {
		final HttpURLConnection httpURLConnection=(urlConnection instanceof HttpURLConnection) ? (HttpURLConnection)urlConnection : null;
		// urlConnection.setRequestProperty("Accept-Charset","UTF-8, ISO-8859-1;q=0"); // used for debugging
		final InputStream urlInputStream=urlConnection.getInputStream();
		final String contentType=urlConnection.getContentType();
		if (contentType!=null) {
			encoding=Source.getCharsetParameterFromHttpHeaderValue(contentType);
			if (encoding!=null) {
				inputStream=urlInputStream;
				encodingSpecificationInfo="HTTP header Content-Type: "+contentType;
				return;
			}
		}
		inputStream=urlInputStream.markSupported() ? urlInputStream : new BufferedInputStream(urlInputStream);
		init();
	}

	public StreamEncodingDetector(final InputStream inputStream) throws IOException {
		this.inputStream=inputStream.markSupported() ? inputStream : new BufferedInputStream(inputStream);
		init();
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}

	public String getEncoding() {
		return encoding;
	}
	
	public String getEncodingSpecificationInfo() {
		return encodingSpecificationInfo;
	}

	public boolean isDifinitive() {
		return definitive;
	}

	public boolean isDocumentSpecifiedEncodingPossible() {
		return documentSpecifiedEncodingPossible;
	}

	public Reader openReader() throws UnsupportedEncodingException {
		if (encoding==null) return new InputStreamReader(inputStream,ISO_8859_1); // encoding==null only if input stream is empty so use an arbitrary encoding.
		if (!Charset.isSupported(encoding)) throw new UnsupportedEncodingException(encoding+" - "+encodingSpecificationInfo);
		return new InputStreamReader(inputStream,encoding);
	}

	private boolean setEncoding(final String encoding, final String encodingSpecificationInfo) {
		this.encoding=encoding;
		this.encodingSpecificationInfo=encodingSpecificationInfo;
		return true;
	}

	private boolean init() throws IOException {
		inputStream.mark(4);
		final int b1=inputStream.read();
		if (b1==-1) return setEncoding(null,"empty input stream");
		final int b2=inputStream.read();
		final int b3=inputStream.read();
		final int b4=inputStream.read();
		inputStream.reset();
		// Check for Unicode Byte Order Mark:
		if (b1==0xEF) {
			if (b2==0xBB && b3==0xBF) return setEncoding(UTF_8,"UTF-8 Byte Order Mark (EF BB BF)");
		} else if (b1==0xFE) {
 			if (b2==0xFF) return setEncoding(UTF_16,"UTF-16 big-endian Byte Order Mark (FE FF)");
		} else if (b1==0xFF) {
 			if (b2==0xFE) {
 				if (b3==0 && b4==0) return setEncoding(UTF_32,"UTF-32 little-endian Byte Order Mark (FF EE 00 00)");
				return setEncoding(UTF_16,"UTF-16 little-endian Byte Order Mark (FF EE)");
			}
		} else if (b1==0) {
 			if (b2==0 && b3==0xFE && b4==0xFF) return setEncoding(UTF_32,"UTF-32 big-endian Byte Order Mark (00 00 FE FF)");
		} else if (b1==0x0E) {
 			if (b2==0xFE && b3==0xFF) return setEncoding(SCSU,"SCSU Byte Order Mark (0E FE FF)");
		} else if (b1==0x2B) {
 			if (b2==0x2F && b3==0x76) return setEncoding(UTF_7,"UTF-7 Byte Order Mark (2B 2F 76)");
		} else if (b1==0xDD) {
 			if (b2==0x73 && b3==0x66 && b4==0x73) return setEncoding(UTF_EBCDIC,"UTF-EBCDIC Byte Order Mark (DD 73 66 73)");
		} else if (b1==0xFB) {
 			if (b2==0xEE && b3==0x28) return setEncoding(BOCU_1,"BOCU-1 Byte Order Mark (FB EE 28)");
		}
		// No Unicode Byte Order Mark found.  Have to start guessing.
		definitive=false;
		// The best we can do is to provide an encoding that reflects the correct number and ordering of bytes for characters in the ASCII range.
		// The result will be one of ISO_8859_1, EBCDIC, UTF_16BE, UTF_16LE, UTF_32BE or UTF_32LE.
		// Assumes 00 bytes indicate multi-byte encodings rather than the presence of NUL characters or characters with a code that is a multiple of 0x100.
		if (b4==-1) {
			// The stream contains between 1 and 3 bytes.
			// This means the document can't possibly specify the encoding, so make a best guess based on the first 3 bytes.
			documentSpecifiedEncodingPossible=false;
			// It might be possible to rule out some encodings based on these bytes, but it is impossible to make a definite determination.
			// The main thing to determine is whether it is an 8-bit or 16-bit encoding.
			// In order to guess the most likely encoding, assume that the text contains only ASCII characters, and that any 00 bytes indicate a 16-bit encoding.
			// The only strictly 8-bit encoding guaranteed to be supported on all java platforms is ISO-8859-1 (UTF-8 uses a variable number of bytes per character).
			// If no 00 bytes are present it is safest to assume ISO-8859-1, as this accepts the full range of values 00-FF in every byte.
			if (b2==-1 || b3!=-1) return setEncoding(ISO_8859_1,"default 8-bit ASCII-compatible encoding (stream 3 bytes long)"); // The stream contains exactly 1 or 3 bytes, so assume an 8-bit encoding regardless of whether any 00 bytes are present.
			// The stream contains exactly 2 bytes.
			if (b1==0) return setEncoding(UTF_16BE,"default 16-bit BE encoding (byte stream starts with 00, stream 2 bytes long)");
			if (b2==0) return setEncoding(UTF_16LE,"default 16-bit LE encoding (byte stream pattern XX 00, stream 2 bytes long)");
			// No 00 bytes present, assume 8-bit encoding:
			return setEncoding(ISO_8859_1,"default 8-bit ASCII-compatible encoding (no 00 bytes present, stream 2 bytes long)");
		}
		// Stream contains at least 4 bytes.
		// The patterns used for documentation are made up of:
		//   0 - zero byte
		//   X - non-zero byte
		//   ? - byte value not yet determined
		if (b1==0) {
			// pattern 0???
			if (b2==0) return setEncoding(UTF_32BE,"default 32-bit BE encoding (byte stream starts with 00 00)"); // pattern 00?? most likely indicates UTF-32BE
			// pattern 0X??
			// Regardless of the final two bytes, assume that the first two bytes indicate a 16-bit BE encoding.
			// There are many circumstances where this could be an incorrect assumption, for example:
			//   - UTF-16LE encoding with first character U+0100 (or any other character whose code is a multiple of 100Hex)
			//   - any encoding with first character NUL
			//   - UTF-32BE encoding with first character outside of Basic Multilingual Plane (BMP)
			// Checking the final two bytes might give some clues as to whether any of these other situations are more likely,
			// but none of the clues will yield less than a 50% chance that the encoding is in fact UTF-16BE as suggested by the first two bytes.
			return setEncoding(UTF_16BE,"default 16-bit BE encoding (byte stream starts with 00)"); // >=50% chance that encoding is UTF-16BE
		}
		// pattern X???
		if (b4==0) {
			// pattern X??0
			if (b3==0) return setEncoding(UTF_32LE,"default 32-bit LE encoding (byte stream starts with pattern XX ?? 00 00)"); // pattern X?00 most likely indicates UTF-32LE
			// pattern X?X0
			return setEncoding(UTF_16LE,"default 16-bit LE encoding (byte stream stars with pattern XX ?? XX 00)"); // Regardless of the second byte, assume the fourth 00 byte indicates UTF-16LE.
		}
		// pattern X??X
		if (b2==0) {
			// pattern X0?X
			// Assuming the second 00 byte doesn't indicate a NUL character, and that it is very unlikely that this is a 32-bit encoding
			// of a character outside of the BMP, we can assume that it indicates a 16-bit encoding.
			// If the pattern is X00X, there is a 50/50 chance that the encoding is BE or LE, with one of the characters have a code that is a multiple of 0x100.
			// This should be a very rare occurrence, and there is no more than a 50% chance that the encoding
			// will be different to that assumed (UTF-16LE) without checking for this occurrence, so don't bother checking for it.
			// If the pattern is X0XX, this is likely to indicate a 16-bit LE encoding with the second character > U+00FF.
			return setEncoding(UTF_16LE,"default 16-bit LE encoding (byte stream starts with pattern XX 00 ?? XX)");
		}
		// pattern XX?X
		if (b3==0) return setEncoding(UTF_16BE,"default 16-bit BE encoding (byte stream starts with pattern XX XX 00 XX)"); // pattern XX0X likely to indicate a 16-bit BE encoding with the first character > U+00FF.
		// pattern XXXX
		// Although it is still possible that this is a 16-bit encoding with the first two characters > U+00FF
		// Assume the more likely case of four 8-bit characters <= U+00FF.
		// Check whether it fits some common EBCDIC strings that might be found at the start of a document:
		if (b1==0x4C) { // first character is EBCDIC '<' (ASCII 'L'), check a couple more characters before assuming EBCDIC encoding:
			if (b2==0x6F && b3==0xA7 && b4==0x94) return setEncoding(EBCDIC,"default EBCDIC encoding (<?xml...> detected)"); // first four bytes are "<?xm" in EBCDIC ("Lo§”" in Windows-1252)
			if (b2==0x5A && b3==0xC4 && b4==0xD6) return setEncoding(EBCDIC,"default EBCDIC encoding (<!DOCTYPE...> detected)"); // first four bytes are "<!DO" in EBCDIC ("LZÄÖ" in Windows-1252)
			if ((b2&b3&b4&0x80)!=0) return setEncoding(EBCDIC,"default EBCDIC-compatible encoding (HTML element detected)"); // all of the 3 bytes after the '<' have the high-order bit set, indicating EBCDIC letters such as "<HTM" ("LÈãÔ" in Windows-1252), or "<htm" ("Lˆ£”" in Windows-1252)
			// although this is not an exhaustive check for EBCDIC, it is safer to assume a more common preliminary encoding if none of these conditions are met.
		}
		// Now confident that it is not EBCDIC, but some other 8-bit encoding.
		// Most other 8-bit encodings are compatible with ASCII.
		// Since a document specified encoding requires only ASCII characters, just choose an arbitrary 8-bit preliminary encoding.
		// UTF-8 is however not a good choice as it is not strictly an 8-bit encoding.
		// UTF-8 bytes with a value >= 0x80 indicate the presence of a multi-byte character, and there are many byte values that are illegal.
		// Therefore, choose the only true 8-bit encoding that accepts all byte values and is guaranteed to be available on all java implementations.
		return setEncoding(ISO_8859_1,"default 8-bit ASCII-compatible encoding (no 00 bytes present in first four bytes of stream)");
	}
}
