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
import java.util.NoSuchElementException;
import java.io.Closeable;
import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.BufferOverflowException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Represents a streamed source HTML document.
 * <p>
 * This class provides a means, via the {@link #iterator()} method, of sequentially parsing every {@linkplain Tag tag}, {@linkplain CharacterReference character reference}
 * and <a href="#PlainText">plain text</a> segment contained within the source document using a minimum amount of memory.
 * <p>
 * In contrast, the standard {@link Source} class stores the entire source text in memory and caches every tag parsed,
 * resulting in memory problems when attempting to parse very large files.
 * <p>
 * The {@link #iterator() iterator} parses and returns each segment as the source text is streamed in.
 * Previous segments are discarded for garbage collection.
 * Source documents up to 2GB in size can be processed, a limit which is imposed by the java language because of its use of the <code>int</code> data type to index string operations.
 * <p>
 * There is however a significant trade-off in functionality when using the <code>StreamedSource</code> class as opposed to the {@link Source} class.
 * The {@link Tag#getElement()} method is not supported on tags that are returned by the iterator, nor are any methods that use the {@link Element} class in any way.
 * The {@link Segment#getSource()} method is also not supported.
 * <p>
 * Most of the methods and constructors in this class mirror similarly named methods in the {@link Source} class where the same functionality is available.
 * <p>
 * See the description of the {@link #iterator()} method for a typical usage example of this class.
 * <p>
 * In contrast to a {@link Source} object, the <code>Reader</code> or <code>InputStream</code> specified in the constructor or created implicitly by the constructor
 * remains open for the life of the <code>StreamedSource</code> object.  If the stream is created internally, it is automatically {@linkplain #close() closed}
 * when the end of the stream is reached or the <code>StreamedSource</code> object is {@linkplain #finalize() finalized}.
 * However a <code>Reader</code> or <code>InputStream</code> that is specified directly in a constructor is never closed automatically, as it can not be assumed
 * that the application has no further use for it.  It is the user's responsibility to ensure it is closed in this case.
 * Explicitly calling the {@link #close()} method on the <code>StreamedSource</code> object ensures that all resources used by it are closed, regardless of whether
 * they were created internally or supplied externally.
 * <p>
 * The functionality provided by <code>StreamedSource</code> is similar to a <a target="_blank" href="http://en.wikipedia.org/wiki/StAX">StAX</a> parser,
 * but with some important benefits:
 * <ul>
 *  <li>
 *   The source document does not have to be valid XML.  It can be plain HTML, can contain invalid syntax, undefined entities,
 *   incorrectly nested elements, {@linkplain TagType#isServerTag() server tags}, or anything else that is commonly found in
 *   "<a target="_blank" href="http://en.wikipedia.org/wiki/Tag_soup">tag soup</a>".
 *  <li>
 *   Every single syntactical construct in the source document's original text is included in the iterator, including the
 *   {@linkplain StartTagType#XML_DECLARATION XML declaration}, {@linkplain CharacterReference character references}, {@linkplain StartTagType#COMMENT comments},
 *   {@linkplain StartTagType#CDATA_SECTION CDATA sections} and {@linkplain TagType#isServerTag() server tags},
 *   each providing the segment's {@linkplain Segment#getBegin() begin} and {@linkplain Segment#getEnd() end} position in the source document.
 *   This allows an exact copy of the original document to be generated, allowing modifications to be made only where they are explicitly required.
 *   This is not possible with either <a target="_blank" href="http://en.wikipedia.org/wiki/Simple_API_for_XML">SAX</a> or
 *   <a target="_blank" href="http://en.wikipedia.org/wiki/StAX">StAX</a>, which to some extent provide interpretations of the content of the XML
 *   instead of the syntactial structures used in the original source document.
 * </ul>
 * <p>
 * The following table summarises the differences between the <code>StreamedSource</code>, StAX and SAX interfaces.
 * Note that some of the available features are documented as optional and may not be supported by all implementations of StAX and SAX.
 * <p>
 * <style type="text/css">
 *  table#ParserComparison td, table#ParserComparison th {padding: 0px 5px 0px 5px}
 *  .checkmark {text-align: center; font-size: 12pt}
 * </style>
 * <table id="ParserComparison" class="bordered" cellspacing="0">
 *  <tr><th class="LabelColumn">Feature</th><th><code>StreamedSource</code></th><th><a target="_blank" href="http://en.wikipedia.org/wiki/StAX">StAX</a></th><th><a target="_blank" href="http://en.wikipedia.org/wiki/Simple_API_for_XML">SAX</a></th></tr>
 *  <tr><td class="LabelColumn">Parse XML</td><td class="checkmark">&#9679;</td><td class="checkmark">&#9679;</td><td class="checkmark">&#9679;</td></tr>
 *  <tr><td class="LabelColumn">Parse entities without DTD</td><td class="checkmark">&#9679;</td><td class="checkmark"></td><td class="checkmark"></td></tr>
 *  <tr><td class="LabelColumn">Automatically validate XML</td><td class="checkmark"></td><td class="checkmark">&#9679;</td><td class="checkmark">&#9679;</td></tr>
 *  <tr><td class="LabelColumn">Parse HTML</td><td class="checkmark">&#9679;</td><td class="checkmark"></td><td class="checkmark"></td></tr>
 *  <tr><td class="LabelColumn">Tolerant of syntax or nesting errors</td><td class="checkmark">&#9679;</td><td class="checkmark"></td><td class="checkmark"></td></tr>
 *  <tr><td class="LabelColumn">Provide begin and end character positions of each event<sup>1</sup></td><td class="checkmark">&#9679;</td><td class="checkmark">&#9675;</td><td class="checkmark"></td></tr>
 *  <tr><td class="LabelColumn">Provide source text of each event</td><td class="checkmark">&#9679;</td><td class="checkmark"></td><td class="checkmark"></td></tr>
 *  <tr><td class="LabelColumn">Handle {@linkplain TagType#isServerTag() server tag} events</td><td class="checkmark">&#9679;</td><td class="checkmark"></td><td class="checkmark"></td></tr>
 *  <tr><td class="LabelColumn">Handle {@linkplain StartTagType#XML_DECLARATION XML declaration} event</td><td class="checkmark">&#9679;</td><td class="checkmark"></td><td class="checkmark"></td></tr>
 *  <tr><td class="LabelColumn">Handle {@linkplain StartTagType#COMMENT comment} events</td><td class="checkmark">&#9679;</td><td class="checkmark">&#9679;</td><td class="checkmark">&#9679;</td></tr>
 *  <tr><td class="LabelColumn">Handle {@linkplain StartTagType#CDATA_SECTION CDATA section} events</td><td class="checkmark">&#9679;</td><td class="checkmark">&#9679;</td><td class="checkmark">&#9679;</td></tr>
 *  <tr><td class="LabelColumn">Handle {@linkplain StartTagType#DOCTYPE_DECLARATION document type declaration} event</td><td class="checkmark">&#9679;</td><td class="checkmark">&#9679;</td><td class="checkmark">&#9679;</td></tr>
 *  <tr><td class="LabelColumn">Handle {@linkplain CharacterReference character reference} events</td><td class="checkmark">&#9679;</td><td class="checkmark"></td><td class="checkmark"></td></tr>
 *  <tr><td class="LabelColumn">Allow chunking of plain text</td><td class="checkmark">&#9679;</td><td class="checkmark">&#9679;</td><td class="checkmark">&#9679;</td></tr>
 *  <tr><td class="LabelColumn">Allow chunking of comment text</td><td class="checkmark"></td><td class="checkmark"></td><td class="checkmark"></td></tr>
 *  <tr><td class="LabelColumn">Allow chunking of CDATA section text</td><td class="checkmark"></td><td class="checkmark"></td><td class="checkmark">&#9679;</td></tr>
 *  <tr><td class="LabelColumn">Allow specification of maximum buffer size</td><td class="checkmark">&#9679;</td><td class="checkmark"></td><td class="checkmark"></td></tr>
 * </table>
 * <sup>1</sup> StAX optionally reports the "offset" of each event but this could be either byte or character position depending on the source.
 * <p>
 * Note that the {@link OutputDocument} class can not be used to create a modified version of a streamed source document.
 * Instead, the output document must be constructed manually from the segments provided by the {@link #iterator() iterator}.
 * <p>
 * <code>StreamedSource</code> objects are not thread safe.
 */
public final class StreamedSource implements Iterable<Segment>, Closeable {
	private final StreamedText streamedText;
	private final StreamedParseText streamedParseText;
	private final Source source;
	private final Closeable closeable; // internally created closeable object should be cleaned up internally.
	private final boolean automaticClose;
	private boolean coalescing=false;
	private boolean handleTags=true;
	private boolean isInitialised=false;
	private Segment currentSegment=null;
	private Segment nextParsedSegment=START_SEGMENT;
	private boolean isXML;

	private static final boolean assumeNoNestedTags=false;
	private static final Segment START_SEGMENT=new Segment(-1,-1);

	private StreamedSource(final Reader reader, final boolean automaticClose, final String encoding, final String encodingSpecificationInfo, final String preliminaryEncodingInfo) throws IOException {
		closeable=reader;
		this.automaticClose=automaticClose;
		streamedText=new StreamedText(reader);
		streamedParseText=new StreamedParseText(streamedText);
		source=new Source(streamedText,streamedParseText,encoding,encodingSpecificationInfo,preliminaryEncodingInfo);
	}

	private StreamedSource(final EncodingDetector encodingDetector, final boolean automaticClose) throws IOException {
		this(encodingDetector.openReader(),automaticClose,encodingDetector.getEncoding(),encodingDetector.getEncodingSpecificationInfo(),encodingDetector.getPreliminaryEncoding()+": "+encodingDetector.getPreliminaryEncodingSpecificationInfo());
	}

	/**
	 * Constructs a new <code>StreamedSource</code> object by loading the content from the specified <code>Reader</code>.
	 * <p>
	 * If the specified reader is an instance of <code>InputStreamReader</code>, the {@link #getEncoding()} method of the
	 * created <code>StreamedSource</code> object returns the encoding from <code>InputStreamReader.getEncoding()</code>.
	 *
	 * @param reader  the <code>java.io.Reader</code> from which to load the source text.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public StreamedSource(final Reader reader) throws IOException {
		this(reader,false,(reader instanceof InputStreamReader) ? ((InputStreamReader)reader).getEncoding() : null,(reader instanceof InputStreamReader) ? "InputStreamReader.getEncoding() of constructor argument" : null,null);
	}

	/**
	 * Constructs a new <code>StreamedSource</code> object by loading the content from the specified <code>InputStream</code>.
	 * <p>
	 * The algorithm for detecting the character {@linkplain #getEncoding() encoding} of the source document from the raw bytes
	 * of the specified input stream is the same as that for the {@link Source#Source(URLConnection) Source(URLConnection)} constructor of the {@link Source} class,
	 * except that the first step is not possible as there is no
	 * <a target="_blank" href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17">Content-Type</a> header to check.
	 * <p>
	 * If the specified <code>InputStream</code> does not support the <code>mark</code> method, the algorithm that determines the encoding may have to wrap it
	 * in a <code>BufferedInputStream</code> in order to look ahead at the encoding meta data.
	 * This extra layer of buffering will then remain in place for the life of the <code>StreamedSource</code>, possibly impacting memory usage and/or degrading performance.
	 * It is always preferable to use the {@link #StreamedSource(Reader)} constructor if the encoding is known in advance.
	 *
	 * @param inputStream  the <code>java.io.InputStream</code> from which to load the source text.
	 * @throws java.io.IOException if an I/O error occurs.
	 * @see #getEncoding()
	 */
	public StreamedSource(final InputStream inputStream) throws IOException {
		this(new EncodingDetector(inputStream),false);
	}

	/**
	 * Constructs a new <code>StreamedSource</code> object by loading the content from the specified URL.
	 * <p>
	 * This is equivalent to {@link #StreamedSource(URLConnection) StreamedSource(url.openConnection())}.
	 *
	 * @param url  the URL from which to load the source text.
	 * @throws java.io.IOException if an I/O error occurs.
	 * @see #getEncoding()
	 */
	public StreamedSource(final URL url) throws IOException {
		this(new EncodingDetector(url.openConnection()),true);
	}

	/**
	 * Constructs a new <code>StreamedSource</code> object by loading the content from the specified <code>URLConnection</code>.
	 * <p>
	 * The algorithm for detecting the character {@linkplain #getEncoding() encoding} of the source document is identical to that described in the
	 * {@link Source#Source(URLConnection) Source(URLConnection)} constructor of the {@link Source} class.
	 * <p>
	 * The algorithm that determines the encoding may have to wrap the input stream in a <code>BufferedInputStream</code> in order to look ahead
	 * at the encoding meta data if the encoding is not specified in the HTTP headers.
	 * This extra layer of buffering will then remain in place for the life of the <code>StreamedSource</code>, possibly impacting memory usage and/or degrading performance.
	 * It is always preferable to use the {@link #StreamedSource(Reader)} constructor if the encoding is known in advance.
	 *
	 * @param urlConnection  the URL connection from which to load the source text.
	 * @throws java.io.IOException if an I/O error occurs.
	 * @see #getEncoding()
	 */
	public StreamedSource(final URLConnection urlConnection) throws IOException {
		this(new EncodingDetector(urlConnection),true);
	}

	/**
	 * Constructs a new <code>StreamedSource</code> object from the specified text.
	 * <p>
	 * Although the <code>CharSequence</code> argument of this constructor apparently contradicts the notion of streaming in the source text,
	 * it can still benefits over the equivalent use of the standard {@link Source} class.
	 * <p>
	 * Firstly, using the <code>StreamedSource</code> class to iterate the nodes of an in-memory <code>CharSequence</code> source document still requires much less memory
	 * than the equivalent operation using the standard {@link Source} class.
	 * <p>
	 * Secondly, the specified <code>CharSequence</code> object could possibly implement its own paging mechanism to minimise memory usage.
	 * <p>
	 * If the specified <code>CharSequence</code> is mutable, its state must not be modified while the <code>StreamedSource</code> is in use.
	 *
	 * @param text  the source text.
	 */
	public StreamedSource(final CharSequence text) {
		closeable=null;
		automaticClose=false;
		streamedText=new StreamedText(text);
		streamedParseText=new StreamedParseText(streamedText);
		source=new Source(text,streamedParseText,null,"Document specified encoding can not be determined automatically from a streamed source",null);
	}

	/**
	 * Specifies an existing character array to use for buffering the incoming character stream.
	 * <p>
	 * The specified buffer is fixed for the life of the <code>StreamedSource</code> object, in contrast to the default buffer which can be automatically replaced
	 * by a larger buffer as needed.
	 * This means that if a {@linkplain Tag tag} (including a {@linkplain StartTagType#COMMENT comment} or {@linkplain StartTagType#CDATA_SECTION CDATA section}) is
	 * encountered that is larger than the specified buffer, an unrecoverable <code>BufferOverflowException</code> is thrown.
	 * This exception is also thrown if {@link #setCoalescing(boolean) coalescing} has been enabled and a plain text segment is encountered
	 * that is larger than the specified buffer.
	 * <p>
	 * In general this method should only be used if there needs to be an absolute maximum memory limit imposed on the parser, where that requirement is more important
	 * than the ability to parse any source document successfully.
	 * <p>
	 * This method can only be called before the {@link #iterator()} method has been called.
	 *
	 * @param buffer  an existing character array to use for buffering the incoming character stream, must not be <code>null</code>.
	 * @return this <code>StreamedSource</code> instance, allowing multiple property setting methods to be chained in a single statement.
	 * @throws IllegalStateException if the {@link #iterator()} method has already been called.
	 */
	public StreamedSource setBuffer(char[] buffer) {
		if (isInitialised) throw new IllegalStateException("setBuffer() can only be called before iterator() is called");
		streamedText.setBuffer(buffer);
		return this;
	}

	/**
	 * Specifies whether an unbroken section of <a href="#PlainText">plain text</a> in the source document should always be coalesced into a single {@link Segment} by the {@linkplain #iterator() iterator}.
	 * <p>
	 * If this property is set to the default value of <code>false</code>, 
	 * and a section of plain text is encountered in the document that is larger than the current {@linkplain #getBufferSize() buffer size},
	 * the text is <i>chunked</i> into multiple consecutive plain text segments in order to minimise memory usage.
	 * <p>
	 * If this property is set to <code>true</code> then chunking is disabled, ensuring that consecutive plain text segments are never generated,
	 * but instead forcing the internal buffer to expand to fit the largest section of plain text.
	 * <p>
	 * Note that {@link CharacterReference} segments are always handled separately from plain text, regardless of the value of this property.
	 * For this reason, algorithms that process element content almost always have to be designed to expect the text in multiple segments
	 * in order to handle character references, so there is usually no advantage in {@linkplain #setCoalescing(boolean) coalescing} plain text segments.
	 * 
	 * @param coalescing  the new value of the coalescing property.
	 * @return this <code>StreamedSource</code> instance, allowing multiple property setting methods to be chained in a single statement.
	 * @throws IllegalStateException if the {@link #iterator()} method has already been called.
 	 */
	public StreamedSource setCoalescing(final boolean coalescing) {
		if (isInitialised) throw new IllegalStateException("setPlainTextWriter() can only be called before iterator() is called");
		this.coalescing=coalescing;
		return this;
	}

	/**
	 * Closes the underlying <code>Reader</code> or <code>InputStream</code> and releases any system resources associated with it.
	 * <p>
	 * If the stream is already closed then invoking this method has no effect.
	 *
	 * @throws IOException if an I/O error occurs.
	 */
	public void close() throws IOException {
		if (closeable!=null) closeable.close();
	}

	/**
	 * Returns the character encoding scheme of the source byte stream used to create this object.
	 * <p>
	 * This method works in essentially the same way as the {@link Source#getEncoding()} method.
	 * <p>
	 * If the byte stream used to create this object does not support the <code>mark</code> method, the algorithm that determines the encoding may have to wrap it
	 * in a <code>BufferedInputStream</code> in order to look ahead at the encoding meta data.
	 * This extra layer of buffering will then remain in place for the life of the <code>StreamedSource</code>, possibly impacting memory usage and/or degrading performance.
	 * It is always preferable to use the {@link #StreamedSource(Reader)} constructor if the encoding is known in advance.
	 * <p>
	 * The {@link #getEncodingSpecificationInfo()} method returns a simple description of how the value of this method was determined.
	 *
	 * @return the character encoding scheme of the source byte stream used to create this object, or <code>null</code> if the encoding is not known.
	 * @see #getEncodingSpecificationInfo()
	 */
	public String getEncoding() {
		return source.getEncoding();
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
		return source.getEncodingSpecificationInfo();
	}

	/**
	 * Returns the preliminary encoding of the source document together with a concise description of how it was determined.
	 * <p>
	 * This method works in essentially the same way as the {@link Source#getPreliminaryEncodingInfo()} method.
	 * <p>
	 * The description returned by this method is intended for informational purposes only.
	 * It is not guaranteed to have any particular format and can not be reliably parsed.
	 *
	 * @return the preliminary encoding of the source document together with a concise description of how it was determined, or <code>null</code> if no preliminary encoding was required.
	 * @see #getEncoding()
	 */
	public String getPreliminaryEncodingInfo() {
		return source.getPreliminaryEncodingInfo();
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
	 * Each segment generated by the iterator is parsed as the source text is streamed in.  Previous segments are discarded for garbage collection.
	 * <p>
	 * If a section of plain text is encountered in the document that is larger than the current {@linkplain #getBufferSize() buffer size},
	 * the text is <i>chunked</i> into multiple consecutive plain text segments in order to minimise memory usage.
	 * Setting the {@link #setCoalescing(boolean) Coalescing} property to <code>true</code> disables chunking, ensuring that consecutive plain text segments are never generated,
	 * but instead forcing the internal buffer to expand to fit the largest section of plain text.
	 * Note that {@link CharacterReference} segments are always handled separately from plain text, regardless of whether {@linkplain #setCoalescing(boolean) coalescing}
	 * is enabled.  For this reason, algorithms that process element content almost always have to be designed to expect the text in multiple segments
	 * in order to handle character references, so there is usually no advantage in {@linkplain #setCoalescing(boolean) coalescing} plain text segments.
	 * <p>
	 * Character references that are found inside tags, such as those present inside attribute values, do not generate separate segments from the iterator.
	 * <p>
	 * This method may only be called once on any particular <code>StreamedSource</code> instance.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd>
	 *   <p>
	 *    The following code demonstrates the typical (implied) usage of this method through the <code>Iterable</code> interface
	 *    to make an exact copy of the document from <code>reader</code> to <code>writer</code> (assuming no server tags are present):
	 *   </p>
	 * <pre>
	 * StreamedSource streamedSource=new StreamedSource(reader);
	 * for (Segment segment : streamedSource) {
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
	 * <p>Note that the last line <code>writer.write(segment.toString())</code> in the above code can be replaced with the following for improved performance:</p>
	 * <pre>
	 * CharBuffer charBuffer=streamedSource.getCurrentSegmentCharBuffer();
	 * writer.write(charBuffer.array(),charBuffer.position(),charBuffer.length());</pre>
	 *  </dd>
	 *  <dd>
	 *   <p>
	 *    The following code demonstrates how to process the plain text content of a specific element, in this case to print the content of every paragraph element:
	 *   </p>
	 * <pre>
	 * StreamedSource streamedSource=new StreamedSource(reader);
	 * StringBuilder sb=new StringBuilder();
	 * boolean insideParagraphElement=false;
	 * for (Segment segment : streamedSource) {
	 *   if (segment instanceof Tag) {
	 *     Tag tag=(Tag)segment;
	 *     if (tag.getName().equals("p")) {
	 *       if (tag instanceof StartTag) {
	 *         insideParagraphElement=true;
	 *         sb.setLength(0);
	 *       } else { // tag instanceof EndTag
	 *         insideParagraphElement=false;
	 *         System.out.println(sb.toString());
	 *       }
	 *     }
	 *   } else if (insideParagraphElement) {
	 *     if (segment instanceof CharacterReference) {
	 *       ((CharacterReference)segment).appendCharTo(sb);
	 *     } else {
	 *       sb.append(segment);
	 *     }
	 *   }
	 * }</pre>
	 *  </dd>
	 * </dl>
	 * @return an iterator over every {@linkplain Tag tag}, {@linkplain CharacterReference character reference} and plain text segment contained within the source document.
	 */
	public Iterator<Segment> iterator() {
		if (isInitialised) throw new IllegalStateException("iterator() can only be called once");
		isInitialised=true;
		return new StreamedSourceIterator();
	}

	/**
	 * Returns the current {@link Segment} from the {@linkplain #iterator()}.
	 * <p>
	 * This is defined as the last {@link Segment} returned from the iterator's <code>next()</code> method.
	 * <p>
	 * This method returns <code>null</code> if the iterator's <code>next()</code> method has never been called, or its
	 * <code>hasNext()</code> method has returned the value <code>false</code>.
	 *
	 * @return the current {@link Segment} from the {@linkplain #iterator()}.
	 */
	public Segment getCurrentSegment() {
		return currentSegment;
	}
	
	/**
	 * Returns a <code>CharBuffer</code> containing the source text of the {@linkplain #getCurrentSegment() current segment}.
	 * <p>
	 * The returned <code>CharBuffer</code> provides a window into the internal <code>char[]</code> buffer including the position and length that spans the
	 * {@linkplain #getCurrentSegment() current segment}.
	 * <p>
	 * For example, the following code writes the source text of the current segment to <code>writer</code>:
	 * <p>
	 * <code>CharBuffer charBuffer=streamedSource.getCurrentSegmentCharBuffer();</code><br />
	 * <code>writer.write(charBuffer.array(),charBuffer.position(),charBuffer.length());</code>
	 * <p>
	 * This may provide a performance benefit over the standard way of accessing the source text of the current segment,
	 * which is to use the <code>CharSequence</code> interface of the segment directly, or to call {@link Segment#toString()}.
	 * <p>
	 * Because this <code>CharBuffer</code> is a direct window into the internal buffer of the <code>StreamedSource</code>, the contents of the
	 * <code>CharBuffer.array()</code> must not be modified, and the array is only guaranteed to hold the segment source text until the
	 * iterator's <code>hasNext()</code> or <code>next()</code> method is next called.
	 *
	 * @return a <code>CharBuffer</code> containing the source text of the {@linkplain #getCurrentSegment() current segment}.
	 */
	public CharBuffer getCurrentSegmentCharBuffer() {
		return streamedText.getCharBuffer(currentSegment.getBegin(),currentSegment.end);
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
	 *  <li>If the document begins with a {@linkplain StartTagType#DOCTYPE_DECLARATION document type declaration} that contains the text
	 *   "<code>xhtml</code>", it is an <a target="_blank" href="http://www.w3.org/TR/xhtml1/">XHTML</a> document, and hence
	 *   also an XML document.
	 *  <li>If none of the above conditions are met, assume the document is normal HTML, and therefore not an XML document.
	 * </ol>
	 * <p>
	 * This method can only be called after the {@link #iterator()} method has been called.
	 *
	 * @return <code>true</code> if the source document is likely to be <a target="_blank" href="http://www.w3.org/TR/REC-xml/">XML</a>, otherwise <code>false</code>.
	 * @throws IllegalStateException if the {@link #iterator()} method has not yet been called.
	 */
	public boolean isXML() {
		if (!isInitialised) throw new IllegalStateException("isXML() method only available after iterator() has been called");
		return isXML;
	}

	/**
	 * Sets the {@link Logger} that handles log messages.
	 * <p>
	 * Specifying a <code>null</code> argument disables logging completely for operations performed on this <code>StreamedSource</code> object.
	 * <p>
	 * A logger instance is created automatically for each <code>StreamedSource</code> object in the same way as is described in the
	 * {@link Source#setLogger(Logger)} method.
	 *
	 * @param logger  the logger that will handle log messages, or <code>null</code> to disable logging.
	 * @see Config#LoggerProvider
	 */
	public void setLogger(final Logger logger) {
		source.setLogger(logger);
	}

	/**
	 * Returns the {@link Logger} that handles log messages.
	 * <p>
	 * A logger instance is created automatically for each <code>StreamedSource</code> object using the {@link LoggerProvider}
	 * specified by the static {@link Config#LoggerProvider} property.
	 * This can be overridden by calling the {@link #setLogger(Logger)} method.
	 * The name used for all automatically created logger instances is "<code>net.htmlparser.jericho</code>".
	 *
	 * @return the {@link Logger} that handles log messages, or <code>null</code> if logging is disabled.
	 */
	public Logger getLogger() {
		return source.getLogger();
	}

	/**
	 * Returns the current size of the internal character buffer.
	 * <p>
	 * This information is generally useful only for investigating memory and performance issues.
	 *
	 * @return the current size of the internal character buffer.
	 */
	public int getBufferSize() {
		return streamedText.getBuffer().length;
	}

	/**
	 * Returns a string representation of the object as generated by the default <code>Object.toString()</code> implementation.
	 * <p>
	 * In contrast to the {@link Source#toString()} implementation, it is generally not possible for this method to return the entire source text.
	 *
	 * @return a string representation of the object as generated by the default <code>Object.toString()</code> implementation.
	 */
	public String toString() {
		return super.toString();
	}

	/**
	 * Called by the garbage collector on an object when garbage collection determines that there are no more references to the object.
	 * <p>
	 * This implementation calls the {@link #close()} method if the underlying <code>Reader</code> or <code>InputStream</code> stream was created internally.
	 */
	protected void finalize() {
		automaticClose();
	}

	StreamedSource setHandleTags(final boolean handleTags) {
		this.handleTags=handleTags;
		return this;
	}

	StreamedSource setSearchBegin(final int begin) {
		if (isInitialised) throw new IllegalStateException("setSearchBegin() can only be called before iterator() is called");
		final int segmentEnd=begin-1;
		nextParsedSegment=new Segment(segmentEnd,segmentEnd);
		return this;
	}

	private void automaticClose() {
		if (automaticClose) try {close();} catch (IOException ex) {}
	}

	private static boolean isXML(final Segment firstNonTextSegment) {
		if (firstNonTextSegment==null || !(firstNonTextSegment instanceof Tag)) return false;
		Tag tag=(Tag)firstNonTextSegment;
		if (tag.getTagType()==StartTagType.XML_DECLARATION) return true;
		// if document has a DOCTYPE declaration and it contains the text "xhtml", it is an XML document:
		if (tag.source.getParseText().indexOf("xhtml",tag.begin,tag.end)!=-1) return true;
		return false;
	}

	private class StreamedSourceIterator implements Iterator<Segment> {
		private final boolean coalescing;
		private final boolean handleTags;
		private Segment nextSegment;
		private int plainTextSegmentBegin=0;
		private final char[] charByRef=new char[1]; // used to pass a single character by reference

		public StreamedSourceIterator() {
			coalescing=StreamedSource.this.coalescing;
			handleTags=StreamedSource.this.handleTags;
			loadNextParsedSegment();
			isXML=isXML(nextParsedSegment);
		}

		public boolean hasNext() {
			if (nextSegment==Tag.NOT_CACHED) loadNextParsedSegment();
			return nextSegment!=null;
		}	
	
		public Segment next() {
			if (!hasNext()) throw new NoSuchElementException();
			final Segment result=nextSegment;
			nextSegment=(result==nextParsedSegment) ? Tag.NOT_CACHED : nextParsedSegment;
			streamedText.setMinRequiredBufferBegin(result.end); // guaranteed not to be discarded until next call to loadNextParsedSegment()
			currentSegment=result;
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		private final void loadNextParsedSegment() {
			nextParsedSegment=findNextParsedSegment();
			final int plainTextSegmentEnd=(nextParsedSegment!=null) ? nextParsedSegment.begin : streamedText.length();
			nextSegment=(plainTextSegmentBegin<plainTextSegmentEnd) ? new Segment(source,plainTextSegmentBegin,plainTextSegmentEnd) : nextParsedSegment;
			if (nextParsedSegment!=null && plainTextSegmentBegin<nextParsedSegment.end) plainTextSegmentBegin=nextParsedSegment.end;
		}
	
		private final Segment findNextParsedSegment() {
			try {
				int i=nextParsedSegment.getBegin()+1;
				final int searchEnd=coalescing ? streamedText.getEnd() : streamedText.getBufferOverflowPosition();
				while (i<searchEnd) {
					final char ch=streamedText.charAt(i);
					if (ch=='&') {
						if (i>=source.fullSequentialParseData[0]) { // do not handle character references inside tags or script elements
							final CharacterReference characterReference=CharacterReference.construct(source,i,Config.UnterminatedCharacterReferenceSettings.ACCEPT_ALL);
							if (characterReference!=null) return characterReference;
						}
					} else if (handleTags && ch=='<') {
						final Tag tag=TagType.getTagAt(source,i,false,assumeNoNestedTags);
						if (tag!=null && !tag.isUnregistered()) {
							final TagType tagType=tag.getTagType();
							if (tag.end>source.fullSequentialParseData[0] && tagType!=StartTagType.DOCTYPE_DECLARATION) {
								source.fullSequentialParseData[0]=(tagType==StartTagType.NORMAL && tag.name==HTMLElementName.SCRIPT) ? Integer.MAX_VALUE : tag.end;
							}
							return tag;
						}
					}
					i++;
				}
				if (i<streamedText.getEnd()) {
					// not coalescing, reached buffer overflow position
					return new Segment(source,plainTextSegmentBegin,i);
				}
			} catch (BufferOverflowException ex) {
				// Unrecoverable buffer overflow - close the reader if it was created internally:
				automaticClose();
				throw ex;
			} catch (IndexOutOfBoundsException ex) {
				// normal way to catch end of stream.
			}
			// streamedText.length() is now guaranteed to return document length
			// End of stream has been reached, can close the reader if it was created internally:
			automaticClose();
			return null;
		}
	}
}
