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

final class EncodingDetector {
	private final InputStream inputStream;
	private String encoding=null;
	private String encodingSpecificationInfo=null;
	private final String preliminaryEncoding;
	private final String preliminaryEncodingSpecificationInfo;
	private final String alternativePreliminaryEncoding;
	
	private static final int PREVIEW_BYTE_COUNT=2048;

	private static final String UTF_8="UTF-8";
	private static final String ISO_8859_1="ISO-8859-1";

	public EncodingDetector(final URLConnection urlConnection) throws IOException {
		this(new StreamEncodingDetector(urlConnection));
	}

	public EncodingDetector(final InputStream inputStream) throws IOException {
		this(new StreamEncodingDetector(inputStream));
	}

	public EncodingDetector(final InputStream inputStream, final String preliminaryEncoding) throws IOException {
		this(inputStream,preliminaryEncoding,"preliminary encoding set explicitly",null);
		if (!Charset.isSupported(preliminaryEncoding)) throw new UnsupportedEncodingException(preliminaryEncoding+" specified as preliminaryEncoding constructor argument");
		detectDocumentSpecifiedEncoding();
	}

	private EncodingDetector(final StreamEncodingDetector streamEncodingDetector) throws IOException {
		this(streamEncodingDetector,ISO_8859_1);
	}

	private EncodingDetector(final StreamEncodingDetector streamEncodingDetector, final String alternativePreliminaryEncoding) throws IOException {
		this(streamEncodingDetector.getInputStream(),streamEncodingDetector.getEncoding(),streamEncodingDetector.getEncodingSpecificationInfo(),alternativePreliminaryEncoding);
		if (streamEncodingDetector.isDifinitive() || !streamEncodingDetector.isDocumentSpecifiedEncodingPossible()) {
			// don't try to detect the encoding from the document because there is no need or it is not possible
			setEncoding(preliminaryEncoding,preliminaryEncodingSpecificationInfo);
		} else {
			detectDocumentSpecifiedEncoding();
		}
	}
	
	private EncodingDetector(final InputStream inputStream, final String preliminaryEncoding, final String preliminaryEncodingSpecificationInfo, final String alternativePreliminaryEncoding) throws IOException {
		this.inputStream=inputStream.markSupported() ? inputStream : new BufferedInputStream(inputStream);
		this.preliminaryEncoding=preliminaryEncoding;
		this.preliminaryEncodingSpecificationInfo=preliminaryEncodingSpecificationInfo;
		this.alternativePreliminaryEncoding=alternativePreliminaryEncoding;
		if (alternativePreliminaryEncoding!=null && !Charset.isSupported(alternativePreliminaryEncoding)) throw new UnsupportedEncodingException(alternativePreliminaryEncoding+" specified as alternativePreliminaryEncoding constructor argument");
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

	public String getPreliminaryEncoding() {
		return preliminaryEncoding;
	}
	
	public String getPreliminaryEncodingSpecificationInfo() {
		return preliminaryEncodingSpecificationInfo;
	}

	public Reader openReader() throws UnsupportedEncodingException {
		if (encoding==null) return new InputStreamReader(inputStream,ISO_8859_1); // encoding==null only if input stream is empty so use an arbitrary encoding.
		if (!Charset.isSupported(encoding)) {
			throw new UnsupportedEncodingException(encoding+": "+encodingSpecificationInfo);
		}
		return new InputStreamReader(inputStream,encoding);
	}

	private boolean setEncoding(final String encoding, final String encodingSpecificationInfo) {
		this.encoding=encoding;
		this.encodingSpecificationInfo=encodingSpecificationInfo;
		return true;
	}

	private boolean detectDocumentSpecifiedEncoding() throws IOException {
		inputStream.mark(PREVIEW_BYTE_COUNT);
		String safePreliminaryEncoding;
		if (Charset.isSupported(preliminaryEncoding)) {
			safePreliminaryEncoding=preliminaryEncoding;
		} else {
			if (alternativePreliminaryEncoding==null) throw new UnsupportedEncodingException(preliminaryEncoding+": "+preliminaryEncodingSpecificationInfo);
			safePreliminaryEncoding=alternativePreliminaryEncoding;
		}
		final Source previewSource=getPreviewSource(safePreliminaryEncoding); // should never throw UnsupportedEncodingException
		inputStream.reset();
		final Logger logger=previewSource.getLogger();
		previewSource.setLogger(null);
		if (preliminaryEncoding!=safePreliminaryEncoding && logger.isWarnEnabled())
			logger.warn("Alternative encoding "+safePreliminaryEncoding+" substituted for unsupported preliminary encoding "+preliminaryEncoding+": "+preliminaryEncodingSpecificationInfo);
		String documentSpecifiedEncodingInfoSuffix;
		if (previewSource.getDocumentSpecifiedEncoding()==null) {
			if (previewSource.isXML()) {
				// The source looks like an XML document.
				// The XML 1.0 specification section 4.3.3 states that an XML file that is not encoded in UTF-8 must contain
				// either a UTF-16 BOM or an encoding declaration in its XML declaration.
				// Since no encoding declaration was detected, and if we assume this class is only used if no BOM is present, we can then assume it is UTF-8.
				return setEncoding(UTF_8,"mandatory XML encoding when no BOM or encoding declaration is present");
			}
			documentSpecifiedEncodingInfoSuffix="no encoding specified in document";
		} else {
			if (Charset.isSupported(previewSource.getDocumentSpecifiedEncoding()))
				return setEncoding(previewSource.getDocumentSpecifiedEncoding(),previewSource.getEncodingSpecificationInfo());
			// Document specified encoding is not supported. Fall back on preliminary encoding.
			documentSpecifiedEncodingInfoSuffix="encoding "+previewSource.getDocumentSpecifiedEncoding()+" specified in document is not supported";
			if (logger.isWarnEnabled()) logger.warn("Unsupported encoding "+previewSource.getDocumentSpecifiedEncoding()+" specified in document, using preliminary encoding "+safePreliminaryEncoding+" instead");
		}
		// Document does not look like XML, does not specify an encoding in its transport protocol, has no BOM, and does not specify an encoding in the document itself.
		// The HTTP protocol states that such a situation should assume ISO-8859-1 encoding.
		// We will just assume the preliminary encoding, which is the best guess based on the first 4 bytes of the stream.
		// This means ISO-8859-1 will be used for any 8-bit ASCII compatible encoding, consistent with the HTTP protocol default.
		if (preliminaryEncoding!=safePreliminaryEncoding) return setEncoding(safePreliminaryEncoding,"alternative encoding substituted for unsupported preliminary encoding "+preliminaryEncoding+": "+preliminaryEncodingSpecificationInfo+", "+documentSpecifiedEncodingInfoSuffix);
		return setEncoding(preliminaryEncoding,preliminaryEncodingSpecificationInfo+", "+documentSpecifiedEncodingInfoSuffix);
	}

	private Source getPreviewSource(final String previewEncoding) throws IOException {
		final byte[] bytes=new byte[PREVIEW_BYTE_COUNT];
		int i;
		for (i=0; i<PREVIEW_BYTE_COUNT; i++) {
			final int nextByte=inputStream.read();
			if (nextByte==-1) break;
			bytes[i]=(byte)nextByte;
		}
		return new Source(new InputStreamReader(new ByteArrayInputStream(bytes,0,i),previewEncoding),null);
	}
}
