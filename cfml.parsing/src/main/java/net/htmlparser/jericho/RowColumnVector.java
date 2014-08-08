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

/**
 * Represents the row and column number of a character position in the source document.
 * <p>
 * Obtained using the {@link Source#getRowColumnVector(int pos)} method.
 */
public final class RowColumnVector {
	private final int row;
	private final int column;
	private final int pos;
	
	private static final RowColumnVector FIRST=new RowColumnVector(1,1,0);
	private static final RowColumnVector[] STREAMED=new RowColumnVector[0];

	private RowColumnVector(final int row, final int column, final int pos) {
		this.row=row;
		this.column=column;
		this.pos=pos;
	}

	private RowColumnVector(final int pos) {
		// used in Streamed source where row and column aren't available.
		this(-1,-1,pos);
	}

	/**
	 * Returns the row number of this character position in the source document.
	 * <p>
	 * If a {@link StreamedSource} is in use, this method always returns <code>-1</code>.
	 *
	 * @return the row number of this character position in the source document.
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * Returns the column number of this character position in the source document.
	 * <p>
	 * If a {@link StreamedSource} is in use, this method always returns <code>-1</code>.
	 *
	 * @return the column number of this character position in the source document.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Returns the character position in the source document.
	 * @return the character position in the source document.
	 */
	public int getPos() {
		return pos;
	}
	
	/**
	 * Returns a string representation of this character position.
	 * <p>
	 * The returned string has the format "<code>(<var>row</var>,<var>column</var>:<var>pos</var>)</code>".
	 *
	 * @return a string representation of this character position.
	 */
	public String toString() {
		return appendTo(new StringBuilder(20)).toString();
	}

	StringBuilder appendTo(final StringBuilder sb) {
		if (row!=-1) return sb.append("(r").append(row).append(",c").append(column).append(",p").append(pos).append(')');
		return sb.append("(p").append(pos).append(')');
	}
	
	static RowColumnVector[] getCacheArray(final Source source) {
		if (source.isStreamed()) return STREAMED;
		final int lastSourcePos=source.end-1;
		final ArrayList<RowColumnVector> list=new ArrayList<RowColumnVector>();
		int pos=0;
		list.add(FIRST);
		int row=1;
		while (pos<=lastSourcePos) {
			final char ch=source.charAt(pos);
			if (ch=='\n' || (ch=='\r' && (pos==lastSourcePos || source.charAt(pos+1)!='\n'))) list.add(new RowColumnVector(++row,1,pos+1));
			pos++;
		}
		return list.toArray(new RowColumnVector[list.size()]);
	}

	static RowColumnVector get(final RowColumnVector[] cacheArray, final int pos) {
		if (cacheArray==STREAMED) return new RowColumnVector(pos);
		int low=0;
		int high=cacheArray.length-1;
		while (true) {
			int mid=(low+high) >> 1;
			final RowColumnVector rowColumnVector=cacheArray[mid];
			if (rowColumnVector.pos<pos) {
				if (mid==high) return new RowColumnVector(rowColumnVector.row,pos-rowColumnVector.pos+1,pos);
				low=mid+1;
			} else if (rowColumnVector.pos>pos) {
				high=mid-1;
			} else {
				return rowColumnVector;
			}
		}
	}
}
