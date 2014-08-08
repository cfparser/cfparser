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
 * Implements a buffered window into a stream of characters.
 * <p>
 * Unless the buffer is explicitly {@linkplain #setBuffer(char[]) set}, it expands automatically as further characters are fetched from the stream.
 * <p>
 * The {@link #setMinRequiredBufferBegin(int)} method can be used to inform the <code>StreamedText</code> object that characters up to a specified
 * position are no longer required, allowing more characters to be fetched without the need to increase the buffer size.
 */
final class StreamedText implements CharSequence {
	private final Reader reader;
	private char[] buffer;
	private boolean expandableBuffer;
	private int bufferBegin=0; // the current position of the first byte of the buffer. all text before it has been discarded.
	private int readerPos=0; // the next position into which text will be loaded from the reader stream. must be >=bufferBegin and <=bufferBegin+buffer.length, except if one of the "text" argument constructors was used, in which case =Integer.MAX_VALUE.
	private int minRequiredBufferBegin=0; // the minimum pos that must be kept in buffer. always >=bufferBegin.
	private int end=Integer.MAX_VALUE;

	public static int INITIAL_EXPANDABLE_BUFFER_SIZE=8192; // same default as StAX

	public StreamedText(final Reader reader, final char[] buffer) {
		this.reader=reader;
		setBuffer(buffer);
	}

	public StreamedText(final Reader reader) {
		this(reader,null);
	}

	private StreamedText(final char[] text, final int length) {
		reader=null;
		buffer=text;
		expandableBuffer=false;
		end=length;
		readerPos=Integer.MAX_VALUE;
	}

	public StreamedText(final char[] text) {
		this(text,text.length);
	}

	public StreamedText(final CharBuffer text) {
		this(text.array(),text.length());
	}

	public StreamedText(final CharSequence text) {
		this(toCharArray(text));
	}

	public StreamedText setBuffer(char[] buffer) {
		if (buffer!=null) {
			this.buffer=buffer;
			expandableBuffer=false;
		} else {
			this.buffer=new char[INITIAL_EXPANDABLE_BUFFER_SIZE];
			expandableBuffer=true;
		}
		return this;
	}

	public boolean hasExpandableBuffer() {
		return expandableBuffer;
	}

	/**
	 * Returns the character at the specified index.
	 * @param index  the index of the character.
	 * @return the character at the specified index.
	 */
	public char charAt(final int pos) {
		if (pos>=readerPos) readToPosition(pos);
		checkPos(pos);
		return buffer[pos-bufferBegin];
	}

	public void setMinRequiredBufferBegin(final int minRequiredBufferBegin) {
		if (minRequiredBufferBegin<bufferBegin) throw new IllegalArgumentException("Cannot set minimum required buffer begin to already discarded position "+minRequiredBufferBegin);
		this.minRequiredBufferBegin=minRequiredBufferBegin;
	}

	public int getMinRequiredBufferBegin() {
		return minRequiredBufferBegin;
	}

	/**
	 * Returns the length of the text stream.
	 * <p>
	 * This method returns Integer.MAX_VALUE until an attempt is made to access a position past the end of the stream.
	 *
	 * @return the length of the text stream.
	 */
	public int length() {
		if (end==Integer.MAX_VALUE) throw new IllegalStateException("Length of streamed text cannot be determined until end of file has been reached");
		return end;
	}

	public int getEnd() {
		return end;
	}

	private void prepareBufferRange(final int begin, final int end) {
		final int lastRequiredPos=end-1;
		if (lastRequiredPos>readerPos) readToPosition(lastRequiredPos);
		checkPos(begin);
		if (end>this.end) throw new IndexOutOfBoundsException();
	}

	public void writeTo(final Writer writer, final int begin, final int end) throws IOException {
		prepareBufferRange(begin,end);
		writer.write(buffer,begin-bufferBegin,end-begin);
	}

	/**
	 * Returns a new string that is a substring of this text.
	 * <p>
	 * The substring begins at the specified <code>begin</code> position and extends to the character at position <code>end</code> - 1.
	 * Thus the length of the substring is <code>end-begin</code>. 
	 *
	 * @param begin  the begin position, inclusive.
	 * @param end  the end position, exclusive.
	 * @return a new string that is a substring of this text.
	 */
	public String substring(final int begin, final int end) {
		prepareBufferRange(begin,end);
		return new String(buffer,begin-bufferBegin,end-begin);
	}	

	/**
	 * Returns a new character sequence that is a subsequence of this sequence.
	 * <p>
	 * The returned <code>CharSequence</code> is only guaranteed to be valid as long as no futher operations are performed on this <code>StreamedText</code> object.
	 * Any subsequent method call could invalidate the underlying buffer used by the <code>CharSequence</code>.
	 *
	 * @param begin  the begin position, inclusive.
	 * @param end  the end position, exclusive.
	 * @return a new character sequence that is a subsequence of this sequence.
	 */
	public CharSequence subSequence(final int begin, final int end) {
		// This has not been benchmarked.  It is possible that returning substring(begin,end) results in faster code even though it requires more memory allocation.
		return getCharBuffer(begin,end);
	}

	public CharBuffer getCharBuffer(final int begin, final int end) {
		prepareBufferRange(begin,end);
		return CharBuffer.wrap(buffer,begin-bufferBegin,end-begin);
	}

	public String toString() {
		throw new UnsupportedOperationException("Streamed text can not be converted to a string");
	}
	
	public String getDebugInfo() {
		return "Buffer size: \""+buffer.length+"\", bufferBegin="+bufferBegin+", minRequiredBufferBegin="+minRequiredBufferBegin+", readerPos="+readerPos;
	}

	public char[] getBuffer() {
		return buffer;
	}

	public int getBufferBegin() {
		return bufferBegin;
	}

	private void checkPos(final int pos) {
		// hopefully inlined by the compiler
		if (pos<bufferBegin) throw new IllegalStateException("StreamedText position "+pos+" has been discarded");
		if (pos>=end) throw new IndexOutOfBoundsException();
	}

	public int getBufferOverflowPosition() {
		return minRequiredBufferBegin+buffer.length;
	}

	private void readToPosition(final int pos) {
		try {
			if (pos>=bufferBegin+buffer.length) {
				if (pos>=minRequiredBufferBegin+buffer.length) {
					if (!expandableBuffer) throw new BufferOverflowException(); // unfortunately BufferOverflowException doesn't accept a message argument, otherwise it would include the message "StreamedText buffer too small to keep positions "+minRequiredBufferBegin+" and "+pos+" simultaneously"
					expandBuffer(pos-minRequiredBufferBegin+1);
				}
				discardUsedText();
			}
			while (readerPos<=pos) {
				final int charCount=reader.read(buffer,readerPos-bufferBegin,bufferBegin+buffer.length-readerPos);
				if (charCount==-1) {
					end=readerPos;
					break;
				}
				readerPos+=charCount;
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void expandBuffer(final int minSize) throws IOException {
		int newSize=buffer.length*2;
		if (newSize<minSize) newSize=minSize;
		final char[] newBuffer=new char[newSize];
		shiftBuffer(buffer,newBuffer);
		buffer=newBuffer;
	}

	private void discardUsedText() throws IOException {
		if (minRequiredBufferBegin==bufferBegin) return;
		shiftBuffer(buffer,buffer);
	}

	private void shiftBuffer(final char[] fromBuffer, final char[] toBuffer) throws IOException {
		final int shift=minRequiredBufferBegin-bufferBegin;
		final int usedBufferLength=readerPos-bufferBegin;
		for (int i=shift; i<usedBufferLength; i++) toBuffer[i-shift]=fromBuffer[i];
		bufferBegin=minRequiredBufferBegin;
		while (readerPos<bufferBegin) {
			final long charCount=reader.skip(bufferBegin-readerPos);
			if (charCount==0) {
				end=readerPos;
				break;
			}
			readerPos+=charCount;
		}
	}

	String getCurrentBufferContent() {
		return substring(bufferBegin,Math.min(end,readerPos));
	}

	private static char[] toCharArray(final CharSequence text) {
		if (text instanceof String) return ((String)text).toCharArray();
		final char[] charArray=new char[text.length()];
		for (int i=0; i<charArray.length; i++) charArray[i]=text.charAt(i);
		return charArray;
	}
}
