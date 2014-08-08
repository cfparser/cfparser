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

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

/**
 * Represents a source HTML document.
 * <p>
 * The first step in parsing an HTML document is always to construct a <code>Source</code> object from the source data, which can be a
 * <code>String</code>, <code>Reader</code>, <code>InputStream</code>, <code>URLConnection</code> or <code>URL</code>.
 * Each constructor uses all the evidence available to determine the original {@linkplain #getEncoding() character encoding} of the data.
 * <p>
 * Once the <code>Source</code> object has been created, you can immediately start searching for {@linkplain Tag tags} or {@linkplain Element elements} within the document
 * using the <a href="Tag.html#TagSearchMethods">tag search methods</a>.
 * <p>
 * In certain circumstances you may be able to improve performance by calling the {@link #fullSequentialParse()} method before calling any
 * <a href="Tag.html#TagSearchMethods">tag search methods</a>.  See the documentation of the {@link #fullSequentialParse()} method for details.
 * <p>
 * Any issues encountered while parsing are logged to a {@link Logger} object.
 * The {@link #setLogger(Logger)} method can be used to explicitly set a <code>Logger</code> implementation for a particular <code>Source</code> instance,
 * otherwise the static {@link Config#LoggerProvider} property determines how the logger is set by default for all <code>Source</code> instances.
 * See the documentation of the {@link Config#LoggerProvider} property for information about how the default logging provider is determined.
 * <p>
 * Note that many of the useful functions which can be performed on the source document are
 * defined in its superclass, {@link Segment}.
 * The source object is itself a segment which spans the entire document.
 * <p>
 * Most of the methods defined in this class are useful for determining the elements and tags
 * surrounding or neighbouring a particular character position in the document.
 * <p>
 * For information on how to create a modified version of this source document, see the {@link OutputDocument} class.
 * <p>
 * <code>Source</code> objects are not thread safe, and should therefore not be shared between multiple threads unless all access is synchronized using
 * some mechanism external to the library.
 * <p>
 * If memory usage is a major concern, consider using the {@link StreamedSource} class instead of the <code>Source</code> class.
 *
 * @see Segment
 * @see StreamedSource
 */
public final class Source extends Segment implements Iterable<Segment> {
	private final CharSequence sourceText;
	private String documentSpecifiedEncoding=UNINITIALISED;
	private String encoding=UNINITIALISED; // null value means no encoding specified.
	private String encodingSpecificationInfo;
	private String preliminaryEncodingInfo=null;
	private String newLine=UNINITIALISED;
	private ParseText parseText=null;
	private OutputDocument parseTextOutputDocument=null;
	Logger logger; // never null
	private RowColumnVector[] rowColumnVectorCacheArray=null;
	final Cache cache;
	boolean useAllTypesCache=true;
	boolean useSpecialTypesCache=true;
	int[] fullSequentialParseData=null; // non-null iff a fullSequentialParse is underway. In version 2.5 this was passed around as a parameter during full sequential parse, but this approach was found to be error-prone and abandoned in 2.6
	// cached result lists:
	Tag[] allTagsArray=null; // non-null iff fullSequentialParse was called
	List<Tag> allTags=null; // non-null iff fullSequentialParse was called
	List<StartTag> allStartTags=null;
	private List<Element> allElements=null;
	private List<Element> childElements=null;

	private static String lastNewLine=null;

	private static final String UNINITIALISED="";
	private static final String CR="\r";
	private static final String LF="\n";
	private static final String CRLF="\r\n";

	static final String PACKAGE_NAME=Source.class.getPackage().getName(); // "net.htmlparser.jericho"

	/**
	 * Constructs a new <code>Source</code> object from the specified text.
	 * @param text  the source text.
	 */
	public Source(final CharSequence text) {
		super(text.length());
		sourceText=text.toString();
		setLogger(newLogger());
		cache=new Cache(this);
	}

	private Source(final EncodingDetector encodingDetector) throws IOException {
		this(getString(encodingDetector));
		encoding=encodingDetector.getEncoding();
		encodingSpecificationInfo=encodingDetector.getEncodingSpecificationInfo();
		preliminaryEncodingInfo=encodingDetector.getPreliminaryEncoding()+": "+encodingDetector.getPreliminaryEncodingSpecificationInfo();
	}

	Source(final Reader reader, final String encoding) throws IOException {
		this(Util.getString(reader));
		if (encoding!=null) {
			this.encoding=encoding;
			encodingSpecificationInfo="InputStreamReader.getEncoding() of constructor argument";
		}
	}

	// Only called from StreamedSource:
	Source(final CharSequence sourceText, final StreamedParseText streamedParseText, final String encoding, final String encodingSpecificationInfo, final String preliminaryEncodingInfo) {
		super(streamedParseText.getEnd()); // normally Integer.MAX_VALUE unless called from StreamedSource(CharSequence)
		cache=Cache.STREAMED_SOURCE_MARKER;
		useAllTypesCache=false;
		useSpecialTypesCache=false;
		fullSequentialParseData=new int[1];
		if (encoding!=null) this.encoding=encoding;
		this.encodingSpecificationInfo=encodingSpecificationInfo;
		this.preliminaryEncodingInfo=preliminaryEncodingInfo;
		this.sourceText=sourceText;
		parseText=streamedParseText;
		setLogger(newLogger());
	}

	// only called from CharacterReference.parse(CharSequence)
	Source(final CharSequence sourceText, final boolean CHARACTER_REFERENCE_PARSE_METHOD) {
		super(sourceText.length());
		this.sourceText=sourceText;
		cache=null;
		useAllTypesCache=false;
		useSpecialTypesCache=false;
		setLogger(LoggerDisabled.INSTANCE);
	}

	/**
	 * Constructs a new <code>Source</code> object by loading the content from the specified <code>Reader</code>.
	 * <p>
	 * If the specified reader is an instance of <code>InputStreamReader</code>, the {@link #getEncoding()} method of the
	 * created <code>Source</code> object returns the encoding from <code>InputStreamReader.getEncoding()</code>.
	 *
	 * @param reader  the <code>java.io.Reader</code> from which to load the source text.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public Source(final Reader reader) throws IOException {
		this(reader,(reader instanceof InputStreamReader) ? ((InputStreamReader)reader).getEncoding() : null);
	}

	/**
	 * Constructs a new <code>Source</code> object by loading the content from the specified <code>InputStream</code>.
	 * <p>
	 * The algorithm for detecting the character {@linkplain #getEncoding() encoding} of the source document from the raw bytes
	 * of the specified input stream is the same as that for the {@link #Source(URLConnection)} constructor,
	 * except that the first step is not possible as there is no
	 * <a target="_blank" href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17">Content-Type</a> header to check.
	 *
	 * @param inputStream  the <code>java.io.InputStream</code> from which to load the source text.
	 * @throws java.io.IOException if an I/O error occurs.
	 * @see #getEncoding()
	 */
	public Source(final InputStream inputStream) throws IOException {
		this(new EncodingDetector(inputStream));
	}

	/**
	 * Constructs a new <code>Source</code> object by loading the content from the specified URL.
	 * <p>
	 * This is equivalent to {@link #Source(URLConnection) Source(url.openConnection())}.
	 *
	 * @param url  the URL from which to load the source text.
	 * @throws java.io.IOException if an I/O error occurs.
	 * @see #getEncoding()
	 */
	public Source(final URL url) throws IOException {
		this(new EncodingDetector(url.openConnection()));
	}

	/**
	 * Constructs a new <code>Source</code> object by loading the content from the specified <code>URLConnection</code>.
	 * <p>
	 * The algorithm for detecting the character {@linkplain #getEncoding() encoding} of the source document is as follows:
	 * <br />(process termination is marked by &diams;)
	 * <ol class="HalfSeparated">
	 *  <li>If the HTTP headers received from the URL connection include a 
	 *   <a target="_blank" href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17">Content-Type</a> header
	 *   specifying a <code>charset</code> parameter, then use the encoding specified in the value of the <code>charset</code> parameter. &diams;
	 *  <li>Read the first four bytes of the input stream.
	 *  <li>If the input stream is empty, the created source document has zero length and its {@link #getEncoding()} method 
	 *   returns <code>null</code>. &diams;
	 *  <li>If the input stream starts with a unicode <a target="_blank" href="http://en.wikipedia.org/wiki/Byte_Order_Mark">Byte Order Mark</a> (BOM),
	 *   then use the encoding signified by the BOM. &diams;
	 *   <table class="bordered" cellspacing="0" style="margin: 15px">
	 *    <tr><th>BOM Bytes</th><th>Encoding</th></tr>
	 *    <tr><td><code>EF BB FF</code><td><a target="_blank" href="http://en.wikipedia.org/wiki/UTF-8">UTF-8</a></tr>
	 *    <tr><td><code>FF FE 00 00</code><td><a target="_blank" href="http://en.wikipedia.org/wiki/UTF-16">UTF-32</a> (little-endian)</tr>
	 *    <tr><td><code>00 00 FE FF</code><td><a target="_blank" href="http://en.wikipedia.org/wiki/UTF-16">UTF-32</a> (big-endian)</tr>
	 *    <tr><td><code>FF FE</code><td><a target="_blank" href="http://en.wikipedia.org/wiki/UTF-16">UTF-16</a> (little-endian)</tr>
	 *    <tr><td><code>FE FF</code><td><a target="_blank" href="http://en.wikipedia.org/wiki/UTF-16">UTF-16</a> (big-endian)</tr>
	 *    <tr><td><code>0E FE FF</code><td><a target="_blank" href="http://en.wikipedia.org/wiki/Standard_Compression_Scheme_for_Unicode">SCSU</a></tr>
	 *    <tr><td><code>2B 2F 76</code><td><a target="_blank" href="http://en.wikipedia.org/wiki/UTF-7">UTF-7</a></tr>
	 *    <tr><td><code>DD 73 66 73</code><td><a target="_blank" href="http://en.wikipedia.org/wiki/UTF-EBCDIC">UTF-EBCDIC</a></tr>
	 *    <tr><td><code>FB EE 28</code><td><a target="_blank" href="http://en.wikipedia.org/wiki/BOCU-1">BOCU-1</a></tr>
	 *   </table>
	 *  <li>If the stream contains less than four bytes, then:
	 *   <ol class="Unseparated">
	 *    <li>If the stream contains either one or three bytes, then use the encoding <a target="_blank" href="http://en.wikipedia.org/wiki/ISO-8859-1#ISO-8859-1">ISO-8859-1</a>. &diams;
	 *    <li>If the stream starts with a zero byte, then use the encoding <a target="_blank" href="http://en.wikipedia.org/wiki/UTF-16">UTF-16BE</a>. &diams;
	 *    <li>If the second byte of the stream is zero, then use the encoding <a target="_blank" href="http://en.wikipedia.org/wiki/UTF-16">UTF-16LE</a>. &diams;
	 *    <li>Otherwise use the encoding <a target="_blank" href="http://en.wikipedia.org/wiki/ISO-8859-1#ISO-8859-1">ISO-8859-1</a>. &diams;
	 *   </ol>
	 *  <li>Determine a {@linkplain #getPreliminaryEncodingInfo() preliminary encoding} by examining the first four bytes of the input stream.
	 *   See the {@link #getPreliminaryEncodingInfo()} method for details.
	 *  <li>Read the first 2048 bytes of the input stream and decode it using the preliminary encoding to create a "preview segment".
	 *   If the detected preliminary encoding is not supported on this platform, create the preview segment using
	 *   <a target="_blank" href="http://en.wikipedia.org/wiki/ISO-8859-1#ISO-8859-1">ISO-8859-1</a> instead (this incident is logged at {@linkplain Logger#warn(String) warn} level).
	 *  <li>Search the preview segment for an <a href="#EncodingSpecification">encoding specification</a>, which should always appear at or near the top of the document.
	 *  <li>If an encoding specification is found:
	 *   <ol class="Unseparated">
	 *    <li>If the specified encoding is supported on this platform, use it. &diams;
	 *    <li>If the specified encoding is not supported on this platform, use the encoding that was used to create the preview segment,
	 *     which is normally the detected {@linkplain #getPreliminaryEncodingInfo() preliminary encoding}. &diams;
	 *   </ol>
	 *  <li>If the document {@linkplain #isXML() looks like XML}, then use <a target="_blank" href="http://en.wikipedia.org/wiki/UTF-8">UTF-8</a>. &diams;
	 *   <br/>Section <a target="_blank" href="http://www.w3.org/TR/REC-xml/#charencoding">4.3.3</a> of the XML 1.0 specification states that
	 *   an XML file that is not encoded in UTF-8 must contain either a UTF-16 <a target="_blank" href="http://en.wikipedia.org/wiki/Byte_Order_Mark">BOM</a>
	 *   or an <a target="_blank" href="http://www.w3.org/TR/REC-xml/#IDAS4MS">encoding declaration</a> in its {@linkplain StartTagType#XML_DECLARATION XML declaration}.
	 *   Since neither of these was detected, we can assume the encoding is <a target="_blank" href="http://en.wikipedia.org/wiki/UTF-8">UTF-8</a>.
	 *  <li>Use the encoding that was used to create the preview segment, which is normally the detected {@linkplain #getPreliminaryEncodingInfo() preliminary encoding}. &diams;
	 *   <br />This is the best guess, in the absence of any explicit information about the encoding, based on the first four bytes of the stream.
	 *   The <a target="_blank" href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7.1">HTTP protocol section 3.7.1</a>
	 *   states that an encoding of <a target="_blank" href="http://en.wikipedia.org/wiki/ISO-8859-1#ISO-8859-1">ISO-8859-1</a> can be assumed
	 *   if no <code>charset</code> parameter was included in the HTTP
	 *   <a target="_blank" href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17">Content-Type</a> header.
	 *   This is consistent with the preliminary encoding detected in this scenario.
	 * </ol>
	 *
	 * @param urlConnection  the URL connection from which to load the source text.
	 * @throws java.io.IOException if an I/O error occurs.
	 * @see #getEncoding()
	 */
	public Source(final URLConnection urlConnection) throws IOException {
		this(new EncodingDetector(urlConnection));
	}

	private String setEncoding(final String encoding, final String encodingSpecificationInfo) {
		if (this.encoding==UNINITIALISED) {
			this.encoding=encoding;
			this.encodingSpecificationInfo=encodingSpecificationInfo;
		}
		return encoding;
	}

	/**
	 * Returns the document {@linkplain #getEncoding() encoding} specified within the text of the document.
	 * <p>
	 * The document encoding can be specified within the document text in two ways.
	 * They are referred to generically in this library as an <i><a name="EncodingSpecification">encoding specification</a></i>,
	 * and are listed below in order of precedence:
	 * <ol class="HalfSeparated">
	 *  <li>
	 *   An <a target="_blank" href="http://www.w3.org/TR/REC-xml/#IDAS4MS">encoding declaration</a> within the
	 *   {@linkplain StartTagType#XML_DECLARATION XML declaration} of an XML document,
	 *   which must be present if it has an encoding other than <a target="_blank" href="http://en.wikipedia.org/wiki/UTF-8">UTF-8</a>
	 *   or <a target="_blank" href="http://en.wikipedia.org/wiki/UTF-16">UTF-16</a>.
	 *   <pre>&lt;?xml version="1.0" encoding="ISO-8859-1" ?&gt;</pre>
	 *  <li>
	 *   A <a target="_blank" href="http://www.w3.org/TR/html401/charset.html#spec-char-encoding">META declaration</a>,
	 *   which is in the form of a {@link HTMLElementName#META META} tag with attribute <code>http-equiv="Content-Type"</code>.
	 *   The encoding is specified in the <code>charset</code> parameter of a
	 *   <code><a target="_blank" href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17">Content-Type</a></code>
	 *   HTTP header value, which is placed in the value of the meta tag's <code>content</code> attribute.
	 *   This META declaration should appear as early as possible in the {@link HTMLElementName#HEAD HEAD} element.
	 *   <pre>&lt;META http-equiv=Content-Type content="text/html; charset=iso-8859-1"&gt;</pre>
	 * </ol>
	 * <p>
	 * Both of these tags must only use characters in the range U+0000 to U+007F, and in the case of the META declaration
	 * must use ASCII encoding.  This, along with the fact that they must occur at or near the beginning of the document,
	 * assists in their detection and decoding without the need to know the exact encoding of the full text.
	 *
	 * @return the document {@linkplain #getEncoding() encoding} specified within the text of the document, or <code>null</code> if no encoding is specified.
	 * @see #getEncoding()
	 */
	public String getDocumentSpecifiedEncoding() {
		if (documentSpecifiedEncoding!=UNINITIALISED) return documentSpecifiedEncoding;
		final Tag xmlDeclarationTag=getTagAt(0);
		if (xmlDeclarationTag!=null && xmlDeclarationTag.getTagType()==StartTagType.XML_DECLARATION) {
			documentSpecifiedEncoding=((StartTag)xmlDeclarationTag).getAttributeValue("encoding");
			if (documentSpecifiedEncoding!=null) return setEncoding(documentSpecifiedEncoding,xmlDeclarationTag.toString());
		}
		// Check for Content-Type http-equiv meta tag:
		final StartTag contentTypeMetaTag=getFirstStartTag("http-equiv","content-type",false);
		if (contentTypeMetaTag!=null) {
			final String contentValue=contentTypeMetaTag.getAttributeValue("content");
			if (contentValue!=null) {
				documentSpecifiedEncoding=getCharsetParameterFromHttpHeaderValue(contentValue);
				if (documentSpecifiedEncoding!=null) return setEncoding(documentSpecifiedEncoding,contentTypeMetaTag.toString());
			}
		}
		return setEncoding(null,"No encoding specified in document");
	}

	/**
	 * Returns the character encoding scheme of the source byte stream used to create this object.
	 * <p>
	 * The encoding of a document defines how the original byte stream was encoded into characters.
	 * The <a target="_blank" href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.4">HTTP specification section 3.4</a>
	 * uses the term "character set" to refer to the encoding, and the term "charset" is similarly used in Java
	 * (see the class <code>java.nio.charset.Charset</code>).
	 * This often causes confusion, as a modern "coded character set" such as <a target="_blank" href="http://www.unicode.org/">Unicode</a>
	 * can have several encodings, such as <a target="_blank" href="http://www.unicode.org/faq/utf_bom.html">UTF-8, UTF-16, and UTF-32</a>.
	 * See the Wikipedia <a target="_blank" href="http://en.wikipedia.org/wiki/Character_encoding">character encoding</a> article 
	 * for an explanation of the terminology.
	 * <p>
	 * This method makes the best possible effort to return the name of the encoding used to decode the original source byte stream
	 * into character data.  This decoding takes place in the constructor when a parameter based on a byte stream such as an
	 * <code>InputStream</code> or <code>URLConnection</code> is used to specify the source text.
	 * The documentation of the {@link #Source(InputStream)} and {@link #Source(URLConnection)} constructors describe how the return value of this
	 * method is determined in these cases.
	 * It is also possible in some circumstances for the encoding to be determined in the {@link #Source(Reader)} constructor.
	 * <p>
	 * If a constructor was used that specifies the source text directly in character form (not requiring the decoding of a byte sequence)
	 * then the document itself is searched for an <a href="#EncodingSpecification">encoding specification</a>.  In this case, this
	 * method returns the same value as the {@link #getDocumentSpecifiedEncoding()} method.
	 * <p>
	 * The {@link #getEncodingSpecificationInfo()} method returns a simple description of how the value of this method was determined.
	 *
	 * @return the character encoding scheme of the source byte stream used to create this object, or <code>null</code> if the encoding is not known.
	 * @see #getEncodingSpecificationInfo()
	 */
	public String getEncoding() {
		if (encoding==UNINITIALISED) getDocumentSpecifiedEncoding();
		return encoding;
	}

	/**
	 * Returns a concise description of how the {@linkplain #getEncoding() encoding} of the source document was determined.
	 * <p>
	 * The description is intended for informational purposes only.
	 * It is not guaranteed to have any particular format and can not be reliably parsed.
	 *
	 * @return a concise description of how the {@linkplain #getEncoding() encoding} of the source document was determined.
	 * @see #getEncoding()
	 */
	public String getEncodingSpecificationInfo() {
		if (encoding==UNINITIALISED) getDocumentSpecifiedEncoding();
		return encodingSpecificationInfo;
	}

	/**
	 * Returns the preliminary encoding of the source document together with a concise description of how it was determined.
	 * <p>
	 * It is sometimes necessary for the {@link #Source(InputStream)} and {@link #Source(URLConnection)} constructors to search the document for an 
	 * <a href="#EncodingSpecification">encoding specification</a> in order to determine the exact {@linkplain #getEncoding() encoding}
	 * of the source byte stream.
	 * <p>
	 * In order to search for the {@linkplain #getDocumentSpecifiedEncoding() document specified encoding} before the exact encoding is known,
	 * a <i>preliminary encoding</i> is determined using the first four bytes of the input stream.
	 * <p>
	 * Because the encoding specification must only use characters in the range U+0000 to U+007F, the preliminary encoding need only have the following
	 * basic properties determined:
	 * <ul>
	 *  <li>Code unit size (8-bit, 16-bit or 32-bit)
	 *  <li>Byte order (big-endian or little-endian) if the code unit size is 16-bit or 32-bit
	 *  <li>Basic encoding of characters in the range U+0000 to U+007F (the current implementation only distinguishes between ASCII and EBCDIC)
	 * </ul>
	 * <p>
	 * The encodings used to represent the most commonly encountered combinations of these basic properties are:
	 * <ul>
	 *  <li><a target="_blank" href="http://en.wikipedia.org/wiki/ISO-8859-1#ISO-8859-1">ISO-8859-1</a>: 8-bit <a target="_blank" href="http://en.wikipedia.org/wiki/Ascii">ASCII</a>-compatible encoding
	 *  <li><a target="_blank" href="http://en.wikipedia.org/wiki/EBCDIC_037">Cp037</a>: 8-bit <a target="_blank" href="http://en.wikipedia.org/wiki/Ebcdic">EBCDIC</a>-compatible encoding
	 *  <li><a target="_blank" href="http://en.wikipedia.org/wiki/UTF-16">UTF-16BE</a>: 16-bit big-endian encoding
	 *  <li><a target="_blank" href="http://en.wikipedia.org/wiki/UTF-16">UTF-16LE</a>: 16-bit little-endian encoding
	 *  <li><a target="_blank" href="http://en.wikipedia.org/wiki/UTF-32">UTF-32BE</a>: 32-bit big-endian encoding (not supported on most java platforms)
	 *  <li><a target="_blank" href="http://en.wikipedia.org/wiki/UTF-32">UTF-32LE</a>: 32-bit little-endian encoding (not supported on most java platforms)
	 * </ul>
	 * Note: all encodings with a code unit size greater than 8 bits are assumed to use an
	 * <a target="_blank" href="http://en.wikipedia.org/wiki/Ascii">ASCII</a>-compatible low-order byte.
	 * <p>
	 * In some descriptions returned by this method, and the documentation below, a pattern is used to help demonstrate the contents of the first four bytes of the stream.
	 * The patterns use the characters "<code>00</code>" to signify a zero byte, "<code>XX</code>" to signify a non-zero byte, and "<code>??</code>" to signify
	 * a byte than can be either zero or non-zero.
	 * <p>
	 * The algorithm for determining the preliminary encoding is as follows:
	 * <ol class="HalfSeparated">
	 *  <li>Byte pattern "<code>00 00</code>..." : If the stream starts with two zero bytes, the default 32-bit big-endian encoding <a target="_blank" href="http://en.wikipedia.org/wiki/UTF-32">UTF-32BE</a> is used.
	 *  <li>Byte pattern "<code>00 XX</code>..." : If the stream starts with a single zero byte, the default 16-bit big-endian encoding <a target="_blank" href="http://en.wikipedia.org/wiki/UTF-16">UTF-16BE</a> is used.
	 *  <li>Byte pattern "<code>XX ?? 00 00</code>..." : If the third and fourth bytes of the stream are zero, the default 32-bit little-endian encoding <a target="_blank" href="http://en.wikipedia.org/wiki/UTF-32">UTF-32LE</a> is used.
	 *  <li>Byte pattern "<code>XX 00</code>..." or "<code>XX ?? XX 00</code>..." : If the second or fourth byte of the stream is zero, the default 16-bit little-endian encoding <a target="_blank" href="http://en.wikipedia.org/wiki/UTF-16">UTF-16LE</a> is used.
	 *  <li>Byte pattern "<code>XX XX 00 XX</code>..." : If the third byte of the stream is zero, the default 16-bit big-endian encoding <a target="_blank" href="http://en.wikipedia.org/wiki/UTF-16">UTF-16BE</a> is used (assumes the first character is > U+00FF).
	 *  <li>Byte pattern "<code>4C XX XX XX</code>..." : If the first four bytes are consistent with the <a target="_blank" href="http://en.wikipedia.org/wiki/Ebcdic">EBCDIC</a> encoding of
	 *   an {@linkplain StartTagType#XML_DECLARATION XML declaration} ("<code>&lt;?xm</code>") or
	 *   a {@linkplain StartTagType#DOCTYPE_DECLARATION document type declaration} ("<code>&lt;!DO</code>"),
	 *   or any other string starting with the EBCDIC character '&lt;' followed by three non-ASCII characters (8th bit set),
	 *   which is consistent with EBCDIC alphanumeric characters,
	 *   the default <a target="_blank" href="http://en.wikipedia.org/wiki/Ebcdic">EBCDIC</a>-compatible encoding
	 *   <a target="_blank" href="http://en.wikipedia.org/wiki/EBCDIC_037">Cp037</a> is used.
	 *  <li>Byte pattern "<code>XX XX XX XX</code>..." : Otherwise, if all of the first four bytes of the stream are non-zero,
	 *   the default 8-bit <a target="_blank" href="http://en.wikipedia.org/wiki/Ascii">ASCII</a>-compatible encoding
	 *   <a target="_blank" href="http://en.wikipedia.org/wiki/ISO-8859-1#ISO-8859-1">ISO-8859-1</a> is used.
	 * </ol>
	 * <p>
	 * If it was not necessary to search for a {@linkplain #getDocumentSpecifiedEncoding() document specified encoding} when determining the
	 * {@linkplain #getEncoding() encoding} of this source document from a byte stream, this method returns <code>null</code>.
	 * <p>
	 * See the documentation of the {@link #Source(InputStream)} and {@link #Source(URLConnection)} constructors for more detailed information about when the detection of a
	 * preliminary encoding is required.
	 * <p>
	 * The description returned by this method is intended for informational purposes only.
	 * It is not guaranteed to have any particular format and can not be reliably parsed.
	 *
	 * @return the preliminary encoding of the source document together with a concise description of how it was determined, or <code>null</code> if no preliminary encoding was required.
	 * @see #getEncoding()
	 */
	public String getPreliminaryEncodingInfo() {
		return preliminaryEncodingInfo;
	}

	/**
	 * Indicates whether the source document is likely to be <a target="_blank" href="http://www.w3.org/TR/REC-xml/">XML</a>.
	 * <p>
	 * The algorithm used to determine this is designed to be relatively inexpensive and to provide an accurate result in
	 * most normal situations.
	 * An exact determination of whether the source document is XML would require a much more complex analysis of the text.
	 * <p>
	 * The algorithm is as follows:
	 * <ol class="HalfSeparated">
	 *  <li>If the document begins with an {@linkplain StartTagType#XML_DECLARATION XML declaration}, it is an XML document.
	 *  <li>If the document contains a {@linkplain StartTagType#DOCTYPE_DECLARATION document type declaration} that contains the text
	 *   "<code>xhtml</code>", it is an <a target="_blank" href="http://www.w3.org/TR/xhtml1/">XHTML</a> document, and hence
	 *   also an XML document.
	 *  <li>If none of the above conditions are met, assume the document is normal HTML, and therefore not an XML document.
	 * </ol>
	 *
	 * @return <code>true</code> if the source document is likely to be <a target="_blank" href="http://www.w3.org/TR/REC-xml/">XML</a>, otherwise <code>false</code>.
	 */
	public boolean isXML() {
		final Tag xmlDeclarationTag=getTagAt(0);
		if (xmlDeclarationTag!=null && xmlDeclarationTag.getTagType()==StartTagType.XML_DECLARATION) return true;
		final Tag doctypeTag=getNextTag(0,StartTagType.DOCTYPE_DECLARATION);
		// if document has a DOCTYPE declaration and it contains the text "xhtml", it is an XML document:
		if (doctypeTag!=null && getParseText().indexOf("xhtml",doctypeTag.begin,doctypeTag.end)!=-1) return true;
		return false;
	}

	/**
	 * Returns the <a target="_blank" href="http://en.wikipedia.org/wiki/Newline">newline</a> character sequence used in the source document.
	 * <p>
	 * If the document does not contain any newline characters, this method returns <code>null</code>.
	 * <p>
	 * The three possible return values (aside from <code>null</code>) are <code>"\n"</code>, <code>"\r\n"</code> and <code>"\r"</code>.
	 *
	 * @return the <a target="_blank" href="http://en.wikipedia.org/wiki/Newline">newline</a> character sequence used in the source document, or <code>null</code> if none is present.
	 */
	public String getNewLine() {
		if (newLine!=UNINITIALISED) return newLine;
		for (int i=0; i<end; i++) {
			char ch=sourceText.charAt(i);
			if (ch=='\n') return newLine=lastNewLine=LF;
			if (ch=='\r') return newLine=lastNewLine=(++i<end && sourceText.charAt(i)=='\n') ? CRLF : CR;
		}
		return newLine=null;
	}

	String getBestGuessNewLine() {
		final String newLine=getNewLine();
		if (newLine!=null) return newLine;
		if (lastNewLine!=null) return lastNewLine;
		return Config.NewLine;
	}

	/**
	 * Returns the row number of the specified character position in the source document.
	 * @param pos  the position in the source document.
	 * @return the row number of the specified character position in the source document.
	 * @throws IndexOutOfBoundsException if the specified position is not within the bounds of the document.
	 * @see #getColumn(int pos)
	 * @see #getRowColumnVector(int pos)
	 */
	public int getRow(final int pos) {
		return getRowColumnVector(pos).getRow();
	}

	/**
	 * Returns the column number of the specified character position in the source document.
	 * @param pos  the position in the source document.
	 * @return the column number of the specified character position in the source document.
	 * @throws IndexOutOfBoundsException if the specified position is not within the bounds of the document.
	 * @see #getRow(int pos)
	 * @see #getRowColumnVector(int pos)
	 */
	public int getColumn(final int pos) {
		return getRowColumnVector(pos).getColumn();
	}

	/**
	 * Returns a {@link RowColumnVector} object representing the row and column number of the specified character position in the source document.
	 * @param pos  the position in the source document.
	 * @return a {@link RowColumnVector} object representing the row and column number of the specified character position in the source document.
	 * @throws IndexOutOfBoundsException if the specified position is not within the bounds of the document.
	 * @see #getRow(int pos)
	 * @see #getColumn(int pos)
	 */
	public RowColumnVector getRowColumnVector(final int pos) {
		if (pos>end) throw new IndexOutOfBoundsException();
		if (rowColumnVectorCacheArray==null) rowColumnVectorCacheArray=RowColumnVector.getCacheArray(this);
		return RowColumnVector.get(rowColumnVectorCacheArray,pos);
	}
	
	/**
	 * Returns the source text as a <code>String</code>.
	 * @return the source text as a <code>String</code>.
	 */
	public String toString() {
		return sourceText.toString();
	}

	/**
	 * Parses all of the {@linkplain Tag tags} in this source document sequentially from beginning to end.
	 * <p>
	 * Calling this method can greatly improve performance if most or all of the tags in the document need to be parsed.
	 * <p>
	 * Calling the {@link #getAllTags()}, {@link #getAllStartTags()}, {@link #getAllElements()}, {@link #getChildElements()},
	 * {@link #iterator()} or {@link #getNodeIterator()}
	 * method on the <code>Source</code> object performs a full sequential parse automatically.
	 * There are however still circumstances where it should be called manually, such as when it is known that most or all of the tags in the document will need to be parsed,
	 * but none of the abovementioned methods are used, or are called only after calling one or more other <a href="Tag.html#TagSearchMethods">tag search methods</a>.
	 * <p>
	 * If this method is called manually, is should be called soon after the <code>Source</code> object is created,
	 * before any <a href="Tag.html#TagSearchMethods">tag search methods</a> are called.
	 * <p>
	 * By default, tags are parsed only as needed, which is referred to as <i><a name="ParseOnDemand">parse on demand</a></i> mode.
	 * In this mode, every call to a tag search method that is not returning previously cached tags must perform a relatively complex check to determine whether a
	 * potential tag is in a {@linkplain TagType#isValidPosition(Source,int,int[]) valid position}.
	 * <p>
	 * Generally speaking, a tag is in a valid position if it does not appear inside any another tag.
	 * {@linkplain TagType#isServerTag() Server tags} can appear anywhere in a document, including inside other tags, so this relates only to non-server tags.
	 * Theoretically, checking whether a specified position in the document is enclosed in another tag is only possible if every preceding tag has been parsed,
	 * otherwise it is impossible to tell whether one of the delimiters of the enclosing tag was in fact enclosed by some other tag before it, thereby invalidating it.
	 * <p>
	 * When this method is called, each tag is parsed in sequence starting from the beginning of the document, making it easy to check whether each potential
	 * tag is in a valid position.
	 * In <i>parse on demand</i> mode a compromise technique must be used for this check, since the theoretical requirement of having parsed all preceding tags 
	 * is no longer practical.  
	 * This compromise involves only checking whether the position is enclosed by other tags with {@linkplain TagType#getTagTypesIgnoringEnclosedMarkup() certain tag types}.
	 * The added complexity of this technique makes parsing each tag slower compared to when a full sequential parse is performed, but when only a few tags need
	 * parsing this is an extremely beneficial trade-off.
	 * <p>
	 * The documentation of the {@link TagType#isValidPosition(Source, int pos, int[] fullSequentialParseData)} method,
	 * which is called internally by the parser to perform the valid position check,
	 * includes a more detailed explanation of the differences between the two modes of operation.
	 * <p>
	 * Calling this method a second or subsequent time has no effect.
	 * <p>
	 * This method returns the same list of tags as the {@link Source#getAllTags() Source.getAllTags()} method, but as an array instead of a list.
	 * <p>
	 * If this method is called after any of the <a href="Tag.html#TagSearchMethods">tag search methods</a> are called,
	 * the {@linkplain #getCacheDebugInfo() cache} is cleared of any previously found tags before being restocked via the full sequential parse.
	 * This means that if you still have references to tags or elements from before the full sequential parse, they will not be the same objects as those
	 * that are returned by tag search methods after the full sequential parse, which can cause confusion if you are allocating
	 * {@linkplain Tag#setUserData(Object) user data} to tags.
	 * It is also significant if the {@link Segment#ignoreWhenParsing()} method has been called since the tags were first found, as any tags inside the
	 * ignored segments will no longer be returned by any of the <a href="Tag.html#TagSearchMethods">tag search methods</a>.
	 * <p>
	 * See also the {@link Tag} class documentation for more general details about how tags are parsed.
	 *
	 * @return an array of all {@linkplain Tag tags} in this source document.
	 */
	public Tag[] fullSequentialParse() {
		// The assumeNoNestedTags flag tells the parser not to bother checking for tags inside other tags
		// if the user knows that the document doesn't contain any server tags.
		// This results in a more efficient search, but the difference during benchmark tests was only minimal -
		// about 12% speed improvement in a 1MB document containing 70,000 tags, 75% of which were inside a comment tag.
		// With such a small improvement in a document specifically designed to show an an exaggerated improvement,
		// it is not worth documenting this feature.
		// The flag has been retained internally however as it does not have a measurable performance impact to check for it.
		if (allTagsArray!=null) return allTagsArray;
		final boolean assumeNoNestedTags=false;
		if (cache.getTagCount()!=0) {
			logger.warn("Full sequential parse clearing all tags from cache. Consider calling Source.fullSequentialParse() manually immediately after construction of Source.");
			cache.clear();
		}
		final boolean useAllTypesCacheSave=useAllTypesCache;
		try {
			useAllTypesCache=false;
			useSpecialTypesCache=false;
			return Tag.parseAll(this,assumeNoNestedTags);
		} finally {
			useAllTypesCache=useAllTypesCacheSave;
			useSpecialTypesCache=true;
		}
	}

	/**
	 * Returns an iterator over every {@linkplain Tag tag}, {@linkplain CharacterReference character reference} and plain text segment contained within the source document.
 	 * <p>
	 * <i><a name="PlainText">Plain text</a></i> is defined as all text that is not part of a {@link Tag} or {@link CharacterReference}.
	 * <p>
	 * This results in a sequential walk-through of the entire source document.
	 * The {@linkplain Segment#getEnd() end} position of each segment should correspond with the {@linkplain Segment#getBegin() begin} position of the subsequent segment,
	 * unless any of the tags are enclosed by other tags.
	 * This could happen if there are {@linkplain TagType#isServerTag() server tags} present in the document, or in rare circumstances where the
	 * {@linkplain StartTagType#DOCTYPE_DECLARATION document type declaration} contains {@linkplain StartTagType#MARKUP_DECLARATION markup declarations}.
	 * <p>
	 * Character references that are found inside tags, such as those present inside attribute values, are not included as separate iterated segments.
	 * <p>
	 * This method is implemented by simply calling the {@link Segment#getNodeIterator()} method of the {@link Segment} superclass. 
	 * <p>
	 * Prior to version 3.1, character references were not handled as separate segments, and were instead included unparsed in the plain text segments.
	 * This required the use of the {@link CharacterReference#decode(CharSequence)} method to retrieve the actual text from each plain text segment.
	 * Although it is likely that existing programs based on the previous functionality should still work without modification, the static configuration property
	 * {@link #LegacyIteratorCompatabilityMode} is provided on a temporary basis to revert back to the behaviour of previous versions, ensuring
	 * that existing programs function as intended without major modification.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd>
	 *   <p>
	 *    The following code demonstrates the typical (implied) usage of this method through the <code>Iterable</code> interface
	 *    to make an exact copy of the document from <code>reader</code> to <code>writer</code> (assuming no server tags are present):
	 *   </p>
	 * <pre>
	 * Source source=new Source(reader);
	 * for (Segment segment : source) {
	 *   if (segment instanceof Tag) {
	 *     Tag tag=(Tag)segment;
	 *     // HANDLE TAG
	 *     // Uncomment the following line to ensure each tag is valid XML:
	 *     // writer.write(tag.tidy()); continue;
	 *   } else if (segment instanceof CharacterReference) {
	 *     CharacterReference characterReference=(CharacterReference)segment;
	 *     // HANDLE CHARACTER REFERENCE
	 *     // Uncomment the following line to decode all character references instead of copying them verbatim:
	 *     // characterReference.appendCharTo(writer); continue;
	 *   } else {
	 *     // HANDLE PLAIN TEXT
	 *   }
	 *   // unless specific handling has prevented getting to here, simply output the segment as is:
	 *   writer.write(segment.toString());
	 * }</pre>
	 *  </dd>
	 * </dl>
	 * @return an iterator over every {@linkplain Tag tag}, {@linkplain CharacterReference character reference} and plain text segment contained within the source document.
	 */
	public Iterator<Segment> iterator() {
		return getNodeIterator();
	}

	/**
	 * Returns a list of the top-level {@linkplain Element elements} in the document element hierarchy.
	 * <p>
	 * The objects in the list are all of type {@link Element}.
	 * <p>
	 * The term <i><a name="TopLevelElement">top-level element</a></i> refers to an element that is not nested within any other element in the document.
	 * <p>
	 * The term <i><a name="DocumentElementHierarchy">document element hierarchy</a></i> refers to the hierarchy of elements that make up this source document.
	 * The source document itself is not considered to be part of the hierarchy, meaning there is typically more than one top-level element.
	 * Even when the source represents an entire HTML document, the {@linkplain StartTagType#DOCTYPE_DECLARATION document type declaration} and/or an
	 * {@linkplain StartTagType#XML_DECLARATION XML declaration} often exist as top-level elements along with the {@link HTMLElementName#HTML HTML} element itself.
	 * <p>
	 * The {@link Element#getChildElements()} method can be used to get the children of the top-level elements, with recursive use providing a means to
	 * visit every element in the document hierarchy.
	 * <p>
	 * The document element hierarchy differs from that of the <a target="_blank" href="http://en.wikipedia.org/wiki/Document_Object_Model">Document Object Model</a>
	 * in that it is only a representation of the elements that are physically present in the source text.  Unlike the DOM, it does not include any "implied" HTML elements
	 * such as {@link HTMLElementName#TBODY TBODY} if they are not present in the source text.
	 * <p>
	 * Elements formed from {@linkplain TagType#isServerTag() server tags} are not included in the hierarchy at all.
	 * <p>
	 * Structural errors in this source document such as overlapping elements are reported in the {@linkplain #getLogger() log}.
	 * When elements are found to overlap, the position of the start tag determines the location of the element in the hierarchy.
	 * <p>
	 * Calling this method on the <code>Source</code> object performs a {@linkplain #fullSequentialParse() full sequential parse} automatically.
	 * <p>
	 * A visual representation of the document element hierarchy can be obtained by calling:<br />
	 * {@link #getSourceFormatter()}<code>.</code>{@link SourceFormatter#setIndentAllElements(boolean) setIndentAllElements(true)}<code>.</code>{@link SourceFormatter#setCollapseWhiteSpace(boolean) setCollapseWhiteSpace(true)}<code>.</code>{@link SourceFormatter#setTidyTags(boolean) setTidyTags(true)}<code>.</code>{@link SourceFormatter#toString() toString()}
	 *
	 * @return a list of the top-level {@linkplain Element elements} in the document element hierarchy, guaranteed not <code>null</code>.
	 * @see Element#getParentElement()
	 * @see Element#getChildElements()
	 * @see Element#getDepth()
	 */
	@Override public List<Element> getChildElements() {
		if (childElements==null) {
			if (length()==0) {
				childElements=Collections.emptyList();
			} else {
				if (allTags==null) fullSequentialParse();
				childElements=new ArrayList<Element>();
				int pos=0;
				while (true) {
					final StartTag childStartTag=source.getNextStartTag(pos);
					if (childStartTag==null) break;
					if (!Config.IncludeServerTagsInElementHierarchy && childStartTag.getTagType().isServerTag()) {
						pos=childStartTag.end;
						continue;
					}
					final Element childElement=childStartTag.getElement();
					childElement.getChildElements(0);
					if (childElement.parentElement==Element.NOT_CACHED) { // make sure element was not added as a child of a descendent element (can happen with overlapping elements)
						childElement.parentElement=null;
						childElements.add(childElement);
					}
					pos=childElement.end;
				}
			}
		}
		return childElements;
	}

	/**
	 * Formats the HTML source by laying out each non-inline-level element on a new line with an appropriate indent.
	 * <p>
	 * The output format can be configured by setting any number of properties on the returned {@link SourceFormatter} instance before
	 * {@linkplain SourceFormatter#writeTo(Writer) obtaining its output}.
	 * <p>
	 * To create a <code>SourceFormatter</code> instance based on a {@link Segment} rather than an entire <code>Source</code> document,
	 * use {@linkplain SourceFormatter#SourceFormatter(Segment) new SourceFormatter(segment)} instead.
	 * 
	 * @return an instance of {@link SourceFormatter} based on this source document.
	 */
	public SourceFormatter getSourceFormatter() {
		return new SourceFormatter(this);
	}

	/**
	 * Returns a list of all {@linkplain Tag tags} in this source document.
	 * <p>
	 * Calling this method on the <code>Source</code> object performs a {@linkplain #fullSequentialParse() full sequential parse} automatically.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @return a list of all {@linkplain Tag tags} in this source document.
	 */
	public List<Tag> getAllTags() {
		if (allTags==null) fullSequentialParse();
		return allTags;
	}

	/**
	 * Returns a list of all {@linkplain StartTag start tags} in this source document.
	 * <p>
	 * Calling this method on the <code>Source</code> object performs a {@linkplain #fullSequentialParse() full sequential parse} automatically.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @return a list of all {@linkplain StartTag start tags} in this source document.
	 */
	public List<StartTag> getAllStartTags() {
		if (allStartTags==null) {
			final List<Tag> allTags=getAllTags();
			allStartTags=new ArrayList<StartTag>(allTags.size());
			for (Tag tag : allTags) if (tag instanceof StartTag) allStartTags.add((StartTag)tag);
		}
		return allStartTags;
	}

	/**
	 * Returns a list of all {@linkplain Element elements} in this source document.
	 * <p>
	 * Calling this method on the <code>Source</code> object performs a {@linkplain #fullSequentialParse() full sequential parse} automatically.
	 * <p>
	 * The elements returned correspond exactly with the start tags returned in the {@link #getAllStartTags()} method.
	 *
	 * @return a list of all {@linkplain Element elements} in this source document.
	 */
	public List<Element> getAllElements() {
		if (allElements==null) {
			final List<StartTag> allStartTags=getAllStartTags();
			if (allStartTags.isEmpty()) return Collections.emptyList();
			allElements=new ArrayList<Element>(allStartTags.size());
			for (StartTag startTag : allStartTags) allElements.add(startTag.getElement());
		}
		return allElements;
	}

	/**
	 * Returns the {@link Element} with the specified <code>id</code> attribute value.
	 * <p>
	 * This simulates the script method
	 * <code><a target="_blank" href="http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-html.html#ID-36113835">getElementById</a></code>
	 * defined in DOM HTML level 1.
	 * <p>
	 * This is equivalent to {@link #getFirstElement(String,String,boolean) getFirstElement}<code>("id",id,true)</code>.
	 * <p>
	 * A well formed HTML document should have no more than one element with any given <code>id</code> attribute value.
	 *
	 * @param id  the <code>id</code> attribute value (case sensitive) to search for, must not be <code>null</code>.
	 * @return the {@link Element} with the specified <code>id</code> attribute value, or <code>null</code> if no such element exists.
	 */
	public Element getElementById(final String id) {
		return getFirstElement(Attribute.ID,id,true);
	}

	/**
	 * Returns the {@link Tag} at the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 * <p>
	 * This method also returns {@linkplain Tag#isUnregistered() unregistered} tags.
	 *
	 * @param pos  the position in the source document, may be out of bounds.
	 * @return the {@link Tag} at the specified position in the source document, or <code>null</code> if no tag exists at the specified position or it is out of bounds.
	 */
	public final Tag getTagAt(final int pos) {
		return Tag.getTagAt(this,pos,false);
	}

	/**
	 * Returns the {@link Tag} beginning at or immediately preceding (or {@linkplain Segment#encloses(int) enclosing}) the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @return the {@link Tag} beginning at or immediately preceding the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public Tag getPreviousTag(final int pos) {
		return Tag.getPreviousTag(this,pos);
	}

	/**
	 * Returns the {@link Tag} of the specified {@linkplain TagType type} beginning at or immediately preceding (or {@linkplain Segment#encloses(int) enclosing}) the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param tagType  the <code>TagType</code> to search for.
	 * @return the {@link Tag} of the specified {@linkplain TagType type} beginning at or immediately preceding the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public Tag getPreviousTag(final int pos, final TagType tagType) {
		return Tag.getPreviousTag(this,pos,tagType);
	}
	
	/**
	 * Returns the {@link Tag} beginning at or immediately following the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 * <p>
	 * Use {@link Tag#getNextTag()} to get the tag immediately following another tag.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @return the {@link Tag} beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public Tag getNextTag(final int pos) {
		return Tag.getNextTag(this,pos);
	}

	Tag getNextNonServerTag(int pos) {
		while (true) {
			final Tag tag=getNextTag(pos);
			if (tag==null) return null;
			if (!tag.getTagType().isServerTag()) return tag;
			pos=tag.end;
		}
	}

	Tag getPreviousNonServerTag(int pos) {
		while (true) {
			final Tag tag=getPreviousTag(pos-1);
			if (tag==null) return null;
			if (!tag.getTagType().isServerTag()) return tag;
			pos=tag.begin-1;
		}
	}

	/**
	 * Returns the {@link Tag} of the specified {@linkplain TagType type} beginning at or immediately following the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param tagType  the <code>TagType</code> to search for.
	 * @return the {@link Tag} of the specified {@linkplain TagType type} beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public Tag getNextTag(final int pos, final TagType tagType) {
		return Tag.getNextTag(this,pos,tagType);
	}

	/**
	 * Returns the {@link Tag} that {@linkplain Segment#encloses(int) encloses} the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document, may be out of bounds.
	 * @return the {@link Tag} that {@linkplain Segment#encloses(int) encloses} the specified position in the source document, or <code>null</code> if the position is not within a tag or is out of bounds.
	 */
	public Tag getEnclosingTag(final int pos) {
		return getEnclosingTag(pos,null);
	}

	/**
	 * Returns the {@link Tag} of the specified {@linkplain TagType type} that {@linkplain Segment#encloses(int) encloses} the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document, may be out of bounds.
	 * @param tagType  the <code>TagType</code> to search for.
	 * @return the {@link Tag} of the specified {@linkplain TagType type} that {@linkplain Segment#encloses(int) encloses} the specified position in the source document, or <code>null</code> if the position is not within a tag of the specified type or is out of bounds.
	 */
	public Tag getEnclosingTag(final int pos, final TagType tagType) {
		final Tag tag=getPreviousTag(pos,tagType);
		if (tag==null || tag.end<=pos) return null;
		return tag;
	}

	/**
	 * Returns the {@link Element} beginning at or immediately following the specified position in the source document.
	 * <p>
	 * This is equivalent to {@link #getNextStartTag(int) getNextStartTag(pos)}<code>.</code>{@link StartTag#getElement() getElement()},
	 * assuming the result is not <code>null</code>.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @return the {@link Element} beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public Element getNextElement(final int pos) {
		final StartTag startTag=getNextStartTag(pos);
		return startTag==null ? null : startTag.getElement();
	}

	/**
	 * Returns the {@linkplain StartTagType#NORMAL normal} {@link Element} with the specified {@linkplain Element#getName() name} beginning at or immediately following the specified position in the source document.
	 * <p>
	 * This is equivalent to {@link #getNextStartTag(int,String) getNextStartTag(pos,name)}<code>.</code>{@link StartTag#getElement() getElement()},
	 * assuming the result is not <code>null</code>.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>name</code> parameter is equivalent to 
	 * {@link #getNextElement(int) getNextElement(pos)}.
	 * <p>
	 * Specifying an argument to the <code>name</code> parameter that ends in a colon (<code>:</code>) searches for all elements 
	 * in the specified XML namespace.
	 * <p>
	 * This method also returns elements consisting of {@linkplain Tag#isUnregistered() unregistered} tags if the specified name is not a valid {@linkplain Tag#isXMLName(CharSequence) XML tag name}.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param name  the {@linkplain Element#getName() name} of the element to search for.
	 * @return the {@linkplain StartTagType#NORMAL normal} {@link Element} with the specified {@linkplain Element#getName() name} beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public Element getNextElement(final int pos, String name) {
		final StartTag startTag=getNextStartTag(pos,name);
		return startTag==null ? null : startTag.getElement();
	}

	/**
	 * Returns the {@link Element} with the specified attribute name/value pair beginning at or immediately following the specified position in the source document.
	 * <p>
	 * This is equivalent to {@link #getNextStartTag(int,String,String,boolean) getNextStartTag(pos,attributeName,value,valueCaseSensitive)}<code>.</code>{@link StartTag#getElement() getElement()},
	 * assuming the result is not <code>null</code>.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param attributeName  the attribute name (case insensitive) to search for, must not be <code>null</code>.
	 * @param value  the value of the specified attribute to search for, must not be <code>null</code>.
	 * @param valueCaseSensitive  specifies whether the attribute value matching is case sensitive.
	 * @return the {@link Element} with the specified attribute name/value pair beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 * @see #getNextElement(int pos, String attributeName, Pattern valueRegexPattern)
	 */
	public Element getNextElement(final int pos, final String attributeName, final String value, final boolean valueCaseSensitive) {
		final StartTag startTag=getNextStartTag(pos,attributeName,value,valueCaseSensitive);
		return startTag==null ? null : startTag.getElement();
	}

	/**
	 * Returns the {@link Element} with the specified attribute name and value pattern beginning at or immediately following the specified position in the source document.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>valueRegexPattern</code> parameter performs the search on the attribute name only,
	 * without regard to the attribute value.  This will also match an attribute that {@linkplain Attribute#hasValue() has no value} at all.
	 * <p>
	 * This is equivalent to {@link #getNextStartTag(int,String,Pattern) getNextStartTag(pos,attributeName,valueRegexPattern)}<code>.</code>{@link StartTag#getElement() getElement()},
	 * assuming the result is not <code>null</code>.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param attributeName  the attribute name (case insensitive) to search for, must not be <code>null</code>.
	 * @param valueRegexPattern  the regular expression pattern that must match the attribute value, may be <code>null</code>.
	 * @return the {@link Element} with the specified attribute name and value pattern beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 * @see #getNextElement(int pos, String attributeName, String value, boolean valueCaseSensitive)
	 */
	public Element getNextElement(final int pos, final String attributeName, final Pattern valueRegexPattern) {
		final StartTag startTag=getNextStartTag(pos,attributeName,valueRegexPattern);
		return startTag==null ? null : startTag.getElement();
	}

	/**
	 * Returns the {@link Element} with the specified class beginning at or immediately following the specified position in the source document.
	 * <p>
	 * This matches an element with a <code>class</code> attribute that contains the specified class name, either as an exact match or where the specified class name is one of multiple
	 * class names separated by white space in the attribute value.
	 * <p>
	 * This is equivalent to {@link #getNextStartTagByClass(int,String) getNextStartTagByClass(pos,className)}<code>.</code>{@link StartTag#getElement() getElement()},
	 * assuming the result is not <code>null</code>.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param className  the class name (case sensitive) to search for, must not be <code>null</code>.
	 * @return the {@link Element} with the specified class beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public Element getNextElementByClass(final int pos, final String className) {
		final StartTag startTag=getNextStartTagByClass(pos,className);
		return startTag==null ? null : startTag.getElement();
	}

	/**
	 * Returns the {@link StartTag} at or immediately preceding (or {@linkplain Segment#encloses(int) enclosing}) the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @return the {@link StartTag} at or immediately preceding the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public StartTag getPreviousStartTag(final int pos) {
		return StartTag.getPrevious(this,pos);
	}

	/**
	 * Returns the {@link StartTag} of the specified {@linkplain StartTagType type} at or immediately preceding (or {@linkplain Segment#encloses(int) enclosing}) the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 * <p>
	 * This is exactly equivalent to <code>(StartTag)</code>{@link #getPreviousTag(int,TagType) getPreviousTag}<code>(pos,startTagType)</code>,
	 * but can be used to avoid the explicit cast to a {@link StartTag} object.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param startTagType  the <code>StartTagType</code> to search for.
	 * @return the {@link StartTag} of the specified {@linkplain StartTagType type} at or immediately preceding (or {@linkplain Segment#encloses(int) enclosing}) the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public StartTag getPreviousStartTag(final int pos, final StartTagType startTagType) {
		return (StartTag)getPreviousTag(pos,startTagType);
	}

	/**
	 * Returns the {@linkplain StartTagType#NORMAL normal} {@link StartTag} with the specified {@linkplain StartTag#getName() name} at or immediately preceding (or {@linkplain Segment#encloses(int) enclosing}) the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>name</code> parameter is equivalent to
	 * {@link #getPreviousStartTag(int) getPreviousStartTag(pos)}.
	 * <p>
	 * This method also returns {@linkplain Tag#isUnregistered() unregistered} tags if the specified name is not a valid {@linkplain Tag#isXMLName(CharSequence) XML tag name}.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param name  the {@linkplain StartTag#getName() name} of the start tag to search for.
	 * @return the {@linkplain StartTagType#NORMAL normal} {@link StartTag} with the specified {@linkplain StartTag#getName() name} at or immediately preceding the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public StartTag getPreviousStartTag(final int pos, final String name) {
		return getPreviousStartTag(pos,name,StartTagType.NORMAL);
	}

	/**
	 * Returns the {@link StartTag} with the specified {@linkplain StartTag#getName() name} and {@linkplain StartTagType type} at or immediately preceding (or {@linkplain Segment#encloses(int) enclosing}) the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 * <p>
	 * Specifying {@link StartTagType#NORMAL} as the argument to the <code>startTagType</code> parameter is equivalent to
	 * {@link #getPreviousStartTag(int,String) getPreviousStartTag(pos,name)}.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param name  the {@linkplain StartTag#getName() name} of the start tag to search for, may be <code>null</code>.
	 * @param startTagType  the {@linkplain StartTagType type} of the start tag to search for, must not be <code>null</code>.
	 * @return the {@link StartTag} with the specified {@linkplain StartTag#getName() name} and {@linkplain StartTagType type} at or immediately preceding (or {@linkplain Segment#encloses(int) enclosing}) the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public StartTag getPreviousStartTag(final int pos, String name, final StartTagType startTagType) {
		if (name!=null) name=name.toLowerCase();
		return StartTag.getPrevious(this,pos,name,startTagType);
	}

	/**
	 * Returns the {@link StartTag} beginning at or immediately following the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @return the {@link StartTag} beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public StartTag getNextStartTag(final int pos) {
		return StartTag.getNext(this,pos);
	}

	/**
	 * Returns the {@link StartTag} of the specified {@linkplain StartTagType type} beginning at or immediately following the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 * <p>
	 * This is exactly equivalent to <code>(StartTag)</code>{@link #getNextTag(int,TagType) getNextTag}<code>(pos,startTagType)</code>,
	 * but can be used to avoid the explicit cast to a {@link StartTag} object.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param startTagType  the <code>StartTagType</code> to search for.
	 * @return the {@link StartTag} of the specified {@linkplain StartTagType type} beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public StartTag getNextStartTag(final int pos, final StartTagType startTagType) {
		return (StartTag)getNextTag(pos,startTagType);
	}

	/**
	 * Returns the {@linkplain StartTagType#NORMAL normal} {@link StartTag} with the specified {@linkplain StartTag#getName() name} beginning at or immediately following the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>name</code> parameter is equivalent to 
	 * {@link #getNextStartTag(int) getNextStartTag(pos)}.
	 * <p>
	 * Specifying an argument to the <code>name</code> parameter that ends in a colon (<code>:</code>) searches for all start tags 
	 * in the specified XML namespace.
	 * <p>
	 * This method also returns {@linkplain Tag#isUnregistered() unregistered} tags if the specified name is not a valid {@linkplain Tag#isXMLName(CharSequence) XML tag name}.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param name  the {@linkplain StartTag#getName() name} of the start tag to search for, may be <code>null</code>.
	 * @return the {@linkplain StartTagType#NORMAL normal} {@link StartTag} with the specified {@linkplain StartTag#getName() name} beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public StartTag getNextStartTag(final int pos, final String name) {
		return getNextStartTag(pos,name,StartTagType.NORMAL);
	}

	/**
	 * Returns the {@link StartTag} with the specified {@linkplain StartTag#getName() name} and {@linkplain StartTagType type} beginning at or immediately following the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 * <p>
	 * Specifying {@link StartTagType#NORMAL} as the argument to the <code>startTagType</code> parameter is equivalent to
	 * {@link #getNextStartTag(int,String) getNextStartTag(pos,name)}.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param name  the {@linkplain StartTag#getName() name} of the start tag to search for, may be <code>null</code>.
	 * @param startTagType  the {@linkplain StartTagType type} of the start tag to search for, must not be <code>null</code>.
	 * @return the {@link StartTag} with the specified {@linkplain StartTag#getName() name} and {@linkplain StartTagType type} beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public StartTag getNextStartTag(final int pos, String name, final StartTagType startTagType) {
		if (name!=null) name=name.toLowerCase();
		return StartTag.getNext(this,pos,name,startTagType);
	}

	/**
	 * Returns the {@link StartTag} with the specified attribute name/value pair beginning at or immediately following the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param attributeName  the attribute name (case insensitive) to search for, must not be <code>null</code>.
	 * @param value  the value of the specified attribute to search for, must not be <code>null</code>.
	 * @param valueCaseSensitive  specifies whether the attribute value matching is case sensitive.
	 * @return the {@link StartTag} with the specified attribute name/value pair beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 * @see #getNextStartTag(int pos, String attributeName, Pattern valueRegexPattern)
	 */
	public StartTag getNextStartTag(final int pos, final String attributeName, final String value, final boolean valueCaseSensitive) {
		return StartTag.getNext(this,pos,attributeName,value,valueCaseSensitive);
	}

	/**
	 * Returns the {@link StartTag} with the specified attribute name and value pattern beginning at or immediately following the specified position in the source document.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>valueRegexPattern</code> parameter performs the search on the attribute name only,
	 * without regard to the attribute value.  This will also match an attribute that {@linkplain Attribute#hasValue() has no value} at all.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param attributeName  the attribute name (case insensitive) to search for, must not be <code>null</code>.
	 * @param valueRegexPattern  the regular expression pattern that must match the attribute value, may be <code>null</code>.
	 * @return the {@link StartTag} with the specified attribute name and value pattern beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 * @see #getNextStartTag(int pos, String attributeName, String value, boolean valueCaseSensitive)
	 */
	public StartTag getNextStartTag(final int pos, final String attributeName, final Pattern valueRegexPattern) {
		return StartTag.getNext(this,pos,attributeName,valueRegexPattern);
	}

	/**
	 * Returns the {@link StartTag} with the specified class beginning at or immediately following the specified position in the source document.
	 * <p>
	 * This matches a start tag with a <code>class</code> attribute that contains the specified class name, either as an exact match or where the specified class name is one of multiple
	 * class names separated by white space in the attribute value.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param className  the class name (case sensitive) to search for, must not be <code>null</code>.
	 * @return the {@link StartTag} with the specified class beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public StartTag getNextStartTagByClass(final int pos, final String className) {
		return getNextStartTag(pos,"class",getClassPattern(className));
	}

	/**
	 * Returns the {@link EndTag} at or immediately preceding (or {@linkplain Segment#encloses(int) enclosing}) the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @return the {@link EndTag} at or immediately preceding (or {@linkplain Segment#encloses(int) enclosing}) the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public EndTag getPreviousEndTag(final int pos) {
		return EndTag.getPrevious(this,pos);
	}

	/**
	 * Returns the {@link EndTag} of the specified {@linkplain EndTagType type} at or immediately preceding (or {@linkplain Segment#encloses(int) enclosing}) the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 * <p>
	 * This is exactly equivalent to <code>(EndTag)</code>{@link #getPreviousTag(int,TagType) getPreviousTag}<code>(pos,endTagType)</code>,
	 * but can be used to avoid the explicit cast to an {@link EndTag} object.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param endTagType  the <code>EndTagType</code> to search for.
	 * @return the {@link EndTag} of the specified {@linkplain EndTagType type} at or immediately preceding (or {@linkplain Segment#encloses(int) enclosing}) the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public EndTag getPreviousEndTag(final int pos, final EndTagType endTagType) {
		return (EndTag)getPreviousTag(pos,endTagType);
	}

	/**
	 * Returns the {@linkplain EndTagType#NORMAL normal} {@link EndTag} with the specified {@linkplain EndTag#getName() name} at or immediately preceding (or {@linkplain Segment#encloses(int) enclosing}) the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param name  the {@linkplain StartTag#getName() name} of the end tag to search for, must not be <code>null</code>.
	 * @return the {@linkplain EndTagType#NORMAL normal} {@link EndTag} with the specified {@linkplain EndTag#getName() name} at or immediately preceding (or {@linkplain Segment#encloses(int) enclosing}) the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public EndTag getPreviousEndTag(final int pos, final String name) {
		if (name==null) throw new IllegalArgumentException("name argument must not be null");
		return EndTag.getPrevious(this,pos,name.toLowerCase(),EndTagType.NORMAL);
	}

	/**
	 * Returns the {@link EndTag} beginning at or immediately following the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @return the {@link EndTag} beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public EndTag getNextEndTag(final int pos) {
		return EndTag.getNext(this,pos);
	}

	/**
	 * Returns the {@link EndTag} of the specified {@linkplain EndTagType type} beginning at or immediately following the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 * <p>
	 * This is exactly equivalent to <code>(EndTag)</code>{@link #getNextTag(int,TagType) getNextTag}<code>(pos,endTagType)</code>,
	 * but can be used to avoid the explicit cast to an {@link EndTag} object.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param endTagType  the <code>EndTagType</code> to search for.
	 * @return the {@link EndTag} of the specified {@linkplain EndTagType type} beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public EndTag getNextEndTag(final int pos, final EndTagType endTagType) {
		return (EndTag)getNextTag(pos,endTagType);
	}

	/**
	 * Returns the {@linkplain EndTagType#NORMAL normal} {@link EndTag} with the specified {@linkplain EndTag#getName() name} beginning at or immediately following the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param name  the {@linkplain EndTag#getName() name} of the end tag to search for, must not be <code>null</code>.
	 * @return the {@linkplain EndTagType#NORMAL normal} {@link EndTag} with the specified {@linkplain EndTag#getName() name} beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public EndTag getNextEndTag(final int pos, final String name) {
		return getNextEndTag(pos,name,EndTagType.NORMAL);
	}

	/**
	 * Returns the {@link EndTag} with the specified {@linkplain EndTag#getName() name} and {@linkplain EndTagType type} beginning at or immediately following the specified position in the source document.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @param name  the {@linkplain StartTag#getName() name} of the end tag to search for, must not be <code>null</code>.
	 * @param endTagType  the {@linkplain EndTagType type} of the end tag to search for, must not be <code>null</code>.
	 * @return the {@link EndTag} with the specified {@linkplain EndTag#getName() name} and {@linkplain EndTagType type} beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public EndTag getNextEndTag(final int pos, final String name, final EndTagType endTagType) {
		if (name==null) throw new IllegalArgumentException("name argument must not be null");
		return EndTag.getNext(this,pos,name.toLowerCase(),endTagType);
	}

	/**
	 * Returns the most nested {@linkplain StartTagType#NORMAL normal} {@link Element} that {@linkplain Segment#encloses(int) encloses} the specified position in the source document.
	 * <p>
	 * The specified position can be anywhere inside the {@linkplain Element#getStartTag() start tag}, {@linkplain Element#getEndTag() end tag},
	 * or {@linkplain Element#getContent() content} of the element.  There is no requirement that the returned element has an end tag, and it
	 * may be a {@linkplain TagType#isServerTag() server tag} or HTML {@linkplain StartTagType#COMMENT comment}.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @param pos  the position in the source document, may be out of bounds.
	 * @return the most nested {@linkplain StartTagType#NORMAL normal} {@link Element} that {@linkplain Segment#encloses(int) encloses} the specified position in the source document, or <code>null</code> if the position is not within an element or is out of bounds.
	 */
	public Element getEnclosingElement(final int pos) {
		return getEnclosingElement(pos,null);
	}

	/**
	 * Returns the most nested {@linkplain StartTagType#NORMAL normal} {@link Element} with the specified {@linkplain Element#getName() name} that {@linkplain Segment#encloses(int) encloses} the specified position in the source document.
	 * <p>
	 * The specified position can be anywhere inside the {@linkplain Element#getStartTag() start tag}, {@linkplain Element#getEndTag() end tag},
	 * or {@linkplain Element#getContent() content} of the element.  There is no requirement that the returned element has an end tag, and it
	 * may be a {@linkplain TagType#isServerTag() server tag} or HTML {@linkplain StartTagType#COMMENT comment}.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 * <p>
	 * This method also returns elements consisting of {@linkplain Tag#isUnregistered() unregistered} tags if the specified name is not a valid {@linkplain Tag#isXMLName(CharSequence) XML tag name}.
	 *
	 * @param pos  the position in the source document, may be out of bounds.
	 * @param name  the {@linkplain Element#getName() name} of the element to search for.
	 * @return the most nested {@linkplain StartTagType#NORMAL normal} {@link Element} with the specified {@linkplain Element#getName() name} that {@linkplain Segment#encloses(int) encloses} the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public Element getEnclosingElement(final int pos, String name) {
		int startBefore=pos;
		if (name!=null) name=name.toLowerCase();
		final boolean isXMLTagName=Tag.isXMLName(name);
		while (true) {
			StartTag startTag=StartTag.getPrevious(this,startBefore,name,StartTagType.NORMAL,isXMLTagName);
			if (startTag==null) return null;
			Element element=startTag.getElement();
			if (pos < element.end) return element;
			startBefore=startTag.begin-1;
		}
	}

	/**
	 * Returns the {@link CharacterReference} at or immediately preceding (or {@linkplain Segment#encloses(int) enclosing}) the specified position in the source document.
	 * <p>
	 * Character references positioned within an HTML {@linkplain StartTagType#COMMENT comment} are <b>NOT</b> ignored.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @return the {@link CharacterReference} beginning at or immediately preceding the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public CharacterReference getPreviousCharacterReference(final int pos) {
		return CharacterReference.getPrevious(this,pos);
	}

	/**
	 * Returns the {@link CharacterReference} beginning at or immediately following the specified position in the source document.
	 * <p>
	 * Character references positioned within an HTML {@linkplain StartTagType#COMMENT comment} are <b>NOT</b> ignored.
	 *
	 * @param pos  the position in the source document from which to start the search, may be out of bounds.
	 * @return the {@link CharacterReference} beginning at or immediately following the specified position in the source document, or <code>null</code> if none exists or the specified position is out of bounds.
	 */
	public CharacterReference getNextCharacterReference(final int pos) {
		return CharacterReference.getNext(this,pos);
	}

	/**
	 * Returns the end position of the <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Name">XML Name</a> that starts at the
	 * specified position.
	 * <p>
	 * This implementation first checks that the character at the specified position is a valid XML Name start character as defined by the
	 * {@link Tag#isXMLNameStartChar(char)} method.  If this is not the case, the value <code>-1</code> is returned.
	 * <p>
	 * Once the first character has been checked, subsequent characters are checked using the {@link Tag#isXMLNameChar(char)} method until
	 * one is found that is not a valid XML Name character or the end of the document is reached.  This position is then returned.
	 *
	 * @param pos  the position in the source document of the first character of the XML Name.
	 * @return the end position of the <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Name">XML Name</a> that starts at the specified position.
	 * @throws IndexOutOfBoundsException if the specified position is not within the bounds of the document.
	 */
	public int getNameEnd(int pos) {
		if (!Tag.isXMLNameStartChar(sourceText.charAt(pos++))) return -1;
		try {
			while (Tag.isXMLNameChar(sourceText.charAt(pos))) pos++;  // cost of IndexOutOfBoundsException in rare case of name ending at end of file is less than checking for end of file manually with each iteration.
		} catch (IndexOutOfBoundsException ex) {}
		return pos;
	}

	/**
	 * Parses any {@link Attributes} starting at the specified position.
	 * This method is only used in the unusual situation where attributes exist outside of a start tag.
	 * The {@link StartTag#getAttributes()} method should be used in normal situations.
	 * <p>
	 * The returned Attributes segment always begins at <code>pos</code>,
	 * and ends at the end of the last attribute before either <code>maxEnd</code> or 
	 * the first occurrence of "/&gt;" or "&gt;" outside of a quoted attribute value, whichever comes first.
	 * <p>
	 * Only returns <code>null</code> if the segment contains a major syntactical error
	 * or more than the {@linkplain Attributes#getDefaultMaxErrorCount() default maximum} number of
	 * minor syntactical errors.
	 * <p>
	 * This is equivalent to
	 * {@link #parseAttributes(int,int,int) parseAttributes}<code>(pos,maxEnd,</code>{@link Attributes#getDefaultMaxErrorCount()}<code>)}</code>.
	 *
	 * @param pos  the position in the source document at the beginning of the attribute list, may be out of bounds.
	 * @param maxEnd  the maximum end position of the attribute list, or -1 if no maximum.
	 * @return the {@link Attributes} starting at the specified position, or <code>null</code> if too many errors occur while parsing or the specified position is out of bounds.
	 * @see StartTag#getAttributes()
	 * @see Segment#parseAttributes()
	 */
	public Attributes parseAttributes(final int pos, final int maxEnd) {
		return parseAttributes(pos,maxEnd,Attributes.getDefaultMaxErrorCount());
	}

	/**
	 * Parses any {@link Attributes} starting at the specified position.
	 * This method is only used in the unusual situation where attributes exist outside of a start tag.
	 * The {@link StartTag#getAttributes()} method should be used in normal situations.
	 * <p>
	 * Only returns <code>null</code> if the segment contains a major syntactical error
	 * or more than the specified number of minor syntactical errors.
	 * <p>
	 * The <code>maxErrorCount</code> argument overrides the {@linkplain Attributes#getDefaultMaxErrorCount() default maximum error count}.
	 * <p>
	 * See {@link #parseAttributes(int pos, int maxEnd)} for more information.
	 *
	 * @param pos  the position in the source document at the beginning of the attribute list, may be out of bounds.
	 * @param maxEnd  the maximum end position of the attribute list, or -1 if no maximum.
	 * @param maxErrorCount  the maximum number of minor errors allowed while parsing.
	 * @return the {@link Attributes} starting at the specified position, or <code>null</code> if too many errors occur while parsing or the specified position is out of bounds.
	 * @see StartTag#getAttributes()
	 * @see #parseAttributes(int pos, int MaxEnd)
	 */
	public Attributes parseAttributes(final int pos, final int maxEnd, final int maxErrorCount) {
		return Attributes.construct(this,pos,maxEnd,maxErrorCount);
	}

	/**
	 * Causes the specified range of the source text to be ignored when parsing.
	 * <p>
	 * See the documentation of the {@link Segment#ignoreWhenParsing()} method for more information.
	 *
	 * @param begin  the beginning character position in the source text.
	 * @param end  the end character position in the source text.
	 */
	public void ignoreWhenParsing(final int begin, final int end) {
		if (wasFullSequentialParseCalled()) throw new IllegalStateException("ignoreWhenParsing can not be used after a full sequential parse has been performed");
		if (parseTextOutputDocument==null) {
			parseTextOutputDocument=new OutputDocument(getParseText());
			parseText=null;
		}
		parseTextOutputDocument.replaceWithSpaces(begin,end);
	}

	/**
	 * Causes all of the segments in the specified collection to be ignored when parsing.
	 * <p>
	 * This is equivalent to calling {@link Segment#ignoreWhenParsing()} on each segment in the collection.
	 */
	public void ignoreWhenParsing(final Collection<? extends Segment> segments) {
		for (Segment segment : segments) segment.ignoreWhenParsing();
	}

	/**
	 * Sets the {@link Logger} that handles log messages.
	 * <p>
	 * Specifying a <code>null</code> argument disables logging completely for operations performed on this <code>Source</code> object.
	 * <p>
	 * A logger instance is created automatically for each <code>Source</code> object using the {@link LoggerProvider}
	 * specified by the static {@link Config#LoggerProvider} property.
	 * The name used for all automatically created logger instances is "<code>net.htmlparser.jericho</code>".
	 * <p>
	 * Use of this method with a non-null argument is therefore not usually necessary,
	 * unless specifying an instance of {@link WriterLogger} or a user-defined {@link Logger} implementation.
	 *
	 * @param logger  the logger that will handle log messages, or <code>null</code> to disable logging.
	 * @see Config#LoggerProvider
	 */
	public void setLogger(final Logger logger) {
		this.logger=(logger!=null ? logger : LoggerDisabled.INSTANCE);
	}

	/**
	 * Returns the {@link Logger} that handles log messages.
	 * <p>
	 * A logger instance is created automatically for each <code>Source</code> object using the {@link LoggerProvider}
	 * specified by the static {@link Config#LoggerProvider} property.
	 * This can be overridden by calling the {@link #setLogger(Logger)} method.
	 * The name used for all automatically created logger instances is "<code>net.htmlparser.jericho</code>".
	 *
	 * @return the {@link Logger} that handles log messages, or <code>null</code> if logging is disabled.
	 */
	public Logger getLogger() {
		return logger!=LoggerDisabled.INSTANCE ? logger : null;
	}

	/**
	 * Clears the {@linkplain #getCacheDebugInfo() tag cache} of all tags.
	 * <p>
	 * This method may be useful after calling the {@link Segment#ignoreWhenParsing()} method so that any tags previously found within the ignored segments
	 * will no longer be returned by the <a href="Tag.html#TagSearchMethods">tag search methods</a>.
	 */
	public void clearCache() {
		cache.clear();
		allTagsArray=null;
		allTags=null;
		allStartTags=null;
		allElements=null;
	}

	/**
	 * Returns a string representation of the tag cache, useful for debugging purposes.
	 * @return a string representation of the tag cache, useful for debugging purposes.
	 */
	public String getCacheDebugInfo() {
		return cache.toString();
	}

	/**
	 * Gets a list of all the tags that have been parsed so far.
	 * <p>
	 * This information may be useful for debugging purposes.
	 * Execution of this method collects information from the internal cache and is relatively expensive.
	 *
	 * @return a list of all the tags that have been parsed so far.
	 * @see #getCacheDebugInfo()
	 */
	List<Tag> getParsedTags() {
		final ArrayList<Tag> list=new ArrayList<Tag>();
		for (final Iterator<Tag> i=cache.getTagIterator(); i.hasNext();) list.add(i.next());
		return list;
	}

	/**
	 * Returns the {@linkplain ParseText parse text} of this source document.
	 * <p>
	 * This method is normally only of interest to users who wish to create <a href="TagType.html#Custom">custom tag types</a>.
	 * <p>
	 * The parse text is defined as the entire text of the source document in lower case, with all
	 * {@linkplain Segment#ignoreWhenParsing() ignored} segments replaced by space characters.
	 *
	 * @return the {@linkplain ParseText parse text} of this source document.
	 */
	public final ParseText getParseText() {
		if (parseText==null) {
			if (parseTextOutputDocument!=null) {
				parseText=new CharSequenceParseText(parseTextOutputDocument.toString());
				parseTextOutputDocument=null;
			} else {
				parseText=new CharSequenceParseText(sourceText);
			}
		}
		return parseText;
	}

	/**
	 * Returns a new character sequence that is a subsequence of this source document.
	 *
	 * @param begin  the begin position, inclusive.
	 * @param end  the end position, exclusive.
	 * @return a new character sequence that is a subsequence of this source document.
	 */
	public final CharSequence subSequence(final int begin, final int end) {
		return sourceText.subSequence(begin,end);
	}

	final String substring(final int begin, final int end) {
		return subSequence(begin,end).toString();
	}

	final String getName(final int begin, final int end) {
		// change this implentation if we want to provide the option for case sensitive names
		return substring(begin,end).toLowerCase();
	}

	public final char charAt(final int index) {
		return sourceText.charAt(index);
	}

	/**
	 * Returns the length of the source document.
	 * @return the length of the source document.
	 */
	public final int length() {
		return sourceText.length();
	}

	/**
	 * Specifies whether to enable the legacy {@link Segment#getNodeIterator()} compatability mode.
	 * <p>
	 * Prior to version 3.1, {@link Segment#getNodeIterator()} and {@link #iterator() Source.iterator()} did not handle {@linkplain CharacterReference character references}
	 * as separate segments, and they were instead included unparsed in the plain text segments.
	 * This required the use of the {@link CharacterReference#decode(CharSequence)} method to retrieve the actual text from each plain text segment.
	 * <p>
	 * Although it is likely that existing programs based on the previous functionality should still work without modification,
	 * this static configuration property is provided on a temporary basis to revert back to the behaviour of previous versions, ensuring
	 * that existing programs function as intended without major modification.
	 * <p>
	 * Setting this configuration property to <code>true</code> restores compatability with previous versions.
	 * <p>
	 * This property and compatability mode will be removed in a future release.
	 *
	 * @deprecated  Modify existing code to explicitly handle {@linkplain CharacterReference} segments.
	 */
	@Deprecated
	public static boolean LegacyIteratorCompatabilityMode=false;

	boolean wasFullSequentialParseCalled() {
		return allTagsArray!=null;
	}

	static String getCharsetParameterFromHttpHeaderValue(final String httpHeaderValue) {
		final int charsetParameterPos=httpHeaderValue.toLowerCase().indexOf("charset=");
		if (charsetParameterPos==-1) return null;
		final int charsetBegin=charsetParameterPos+8;
		int charsetEnd=httpHeaderValue.indexOf(';',charsetBegin);
		final String charset=(charsetEnd==-1) ? httpHeaderValue.substring(charsetBegin) : httpHeaderValue.substring(charsetBegin,charsetEnd);
		return charset.trim();
	}
	
	static Logger newLogger() {
		return LoggerFactory.getLogger(PACKAGE_NAME);
	}

	private static String getString(final EncodingDetector encodingDetector) throws IOException {
		try {
			return Util.getString(encodingDetector.openReader());
		} catch (IOException ex) {
			try {
				Logger logger=newLogger();
				if (logger.isInfoEnabled()) logger.info("IOException constructing encoded source. Encoding: "+encodingDetector.getEncoding()+" - "+encodingDetector.getEncodingSpecificationInfo()+". PreliminaryEncoding: "+encodingDetector.getPreliminaryEncoding()+" - "+encodingDetector.getPreliminaryEncodingSpecificationInfo());
			} catch (Exception ex2) {} // make sure attempting to log does not cause a new exception
			throw ex;
		}
	}

	final boolean isStreamed() {
		return cache==Cache.STREAMED_SOURCE_MARKER;
	}
}
