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

class CharSequenceParseText implements ParseText {
	private final CharSequence charSequence;

	CharSequenceParseText(final CharSequence charSequence) {
		this.charSequence=charSequence;
	}

	public final char charAt(final int index) {
		final char ch=charSequence.charAt(index);
		return (ch>='A' && ch<='Z') ? ((char)(ch ^ 0x20)) : ch;
	}

	public final boolean containsAt(final String str, final int pos) {
		for (int i=0; i<str.length(); i++)
			if (str.charAt(i)!=charAt(pos+i)) return false;
		return true;
	}

	public final int indexOf(final char searchChar, final int fromIndex) {
		return indexOf(searchChar,fromIndex,NO_BREAK);
	}
	
	public final int indexOf(final char searchChar, final int fromIndex, final int breakAtIndex) {
		final int actualBreakAtIndex=(breakAtIndex==NO_BREAK || breakAtIndex>getEnd() ? getEnd() : breakAtIndex);
		try {
			for (int i=(fromIndex<0 ? 0 : fromIndex); i<actualBreakAtIndex; i++)
				if (charAt(i)==searchChar) return i;
		} catch (IndexOutOfBoundsException ex) {} // only happens in StreamedParseText subclass - this is the normal way to catch end of stream
		return -1;
	}

	public final int indexOf(final String searchString, final int fromIndex) {
		return indexOf(searchString,fromIndex,NO_BREAK);
	}

	public final int indexOf(final String searchString, final int fromIndex, final int breakAtIndex) {
		if (searchString.length()==1) return indexOf(searchString.charAt(0),fromIndex,breakAtIndex);
		if (searchString.length()==0) return fromIndex;
		final char firstChar=searchString.charAt(0);
		final int lastPossibleBreakAtIndex=getEnd()-searchString.length()+1;
		final int actualBreakAtIndex=(breakAtIndex==NO_BREAK || breakAtIndex>lastPossibleBreakAtIndex) ? lastPossibleBreakAtIndex : breakAtIndex;
		outerLoop: for (int i=(fromIndex<0 ? 0 : fromIndex); i<actualBreakAtIndex; i++) {
			if (charAt(i)==firstChar) {
				for (int j=1; j<searchString.length(); j++)
					if (searchString.charAt(j)!=charAt(j+i)) continue outerLoop;
				return i;
			}
		}
		return -1;
	}

	public final int lastIndexOf(final char searchChar, final int fromIndex) {
		return lastIndexOf(searchChar,fromIndex,NO_BREAK);
	}
	
	public final int lastIndexOf(final char searchChar, final int fromIndex, final int breakAtIndex) {
		for (int i=(fromIndex>getEnd() ? getEnd() : fromIndex); i>breakAtIndex; i--)
			if (charAt(i)==searchChar) return i;
		return -1;
	}

	public final int lastIndexOf(final String searchString, final int fromIndex) {
		return lastIndexOf(searchString,fromIndex,NO_BREAK);
	}

	public final int lastIndexOf(final String searchString, int fromIndex, final int breakAtIndex) {
		if (searchString.length()==1) return lastIndexOf(searchString.charAt(0),fromIndex,breakAtIndex);
		if (searchString.length()==0) return fromIndex;
		final int rightIndex=getEnd()-searchString.length();
		if (breakAtIndex>rightIndex) return -1;
		if (fromIndex>rightIndex) fromIndex=rightIndex;
		final int lastCharIndex=searchString.length()-1;
		final char lastChar=searchString.charAt(lastCharIndex);
		final int actualBreakAtPos=breakAtIndex+lastCharIndex;
		outerLoop: for (int i=fromIndex+lastCharIndex; i>actualBreakAtPos; i--) {
			if (charAt(i)==lastChar) {
				final int startIndex=i-lastCharIndex;
				for (int j=lastCharIndex-1; j>=0; j--)
					if (searchString.charAt(j)!=charAt(j+startIndex)) continue outerLoop;
				return startIndex;
			}
		}
		return -1;
	}

	public final int length() {
		return charSequence.length();
	}

	public final CharSequence subSequence(final int begin, final int end) {
		// doesn't have to be efficient because it is not actually used anywhere internally.
		return substring(begin,end);
	}

	public final String toString() {
		return charSequence.toString();
	}

	protected int getEnd() {
		return charSequence.length();
	}

	protected String substring(final int begin, final int end) {
		return charSequence.subSequence(begin,end).toString().toLowerCase();
	}
}
