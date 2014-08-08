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

final class TagTypeRegister {
	private TagTypeRegister parent=null;
	private char ch=NULL_CHAR;
	private TagTypeRegister[] children=null; // always in alphabetical order
	private TagType[] tagTypes=null; // in descending order of priority

	private static final char NULL_CHAR='\u0000';

	private static final TagType[] DEFAULT_TAG_TYPES={
		StartTagType.UNREGISTERED,
		StartTagType.NORMAL,
		StartTagType.COMMENT,
		StartTagType.MARKUP_DECLARATION,
		StartTagType.DOCTYPE_DECLARATION,
		StartTagType.CDATA_SECTION,
		StartTagType.XML_PROCESSING_INSTRUCTION,
		StartTagType.XML_DECLARATION,
		StartTagType.SERVER_COMMON,
		StartTagType.SERVER_COMMON_ESCAPED,
		EndTagType.UNREGISTERED,
		EndTagType.NORMAL
	};

	private static TagTypeRegister root=new TagTypeRegister();

	static {
		add(DEFAULT_TAG_TYPES);
	}

	private TagTypeRegister() {}

	private static synchronized void add(final TagType[] tagTypes) {
		for (int i=0; i<tagTypes.length; i++) add(tagTypes[i]);
	}

	public static synchronized void add(final TagType tagType) {
		TagTypeRegister cursor=root;
		final String startDelimiter=tagType.getStartDelimiter();
		for (int i=0; i<startDelimiter.length(); i++) {
			final char ch=startDelimiter.charAt(i);
			TagTypeRegister child=cursor.getChild(ch);
			if (child==null) {
				child=new TagTypeRegister();
				child.parent=cursor;
				child.ch=ch;
				cursor.addChild(child);
			}
			cursor=child;
		}
		cursor.addTagType(tagType);
	}

	public static synchronized void remove(final TagType tagType) {
		TagTypeRegister cursor=root;
		final String startDelimiter=tagType.getStartDelimiter();
		for (int i=0; i<startDelimiter.length(); i++) {
			final char ch=startDelimiter.charAt(i);
			final TagTypeRegister child=cursor.getChild(ch);
			if (child==null) return;
			cursor=child;
		}
		cursor.removeTagType(tagType);
		// clean up any unrequired children:
		while (cursor!=root && cursor.tagTypes==null && cursor.children==null) {
			cursor.parent.removeChild(cursor);
			cursor=cursor.parent;
		}
	}

	// list is in order of lowest to highest precedence
	public static List<TagType> getList() {
		final ArrayList<TagType> list=new ArrayList<TagType>();
		root.addTagTypesToList(list);
		return list;
	}
	
	private void addTagTypesToList(final List<TagType> list) {
		if (tagTypes!=null)
			for (int i=tagTypes.length-1; i>=0; i--) list.add(tagTypes[i]);
		if (children!=null)
			for (TagTypeRegister tagTypeRegister : children) tagTypeRegister.addTagTypesToList(list);
	}

	public static final String getDebugInfo() {
		return root.appendDebugInfo(new StringBuilder(),0).toString();
	}

	static final class ProspectiveTagTypeIterator implements Iterator<TagType> {
		private TagTypeRegister cursor;
		private int tagTypeIndex=0;
		
		public ProspectiveTagTypeIterator(final Source source, final int pos) {
			// returns empty iterator if pos out of range
			final ParseText parseText=source.getParseText();
			cursor=root;
			int posIndex=0;
			try {
				// find deepest node that matches the text at pos:
				while (true) {
					final TagTypeRegister child=cursor.getChild(parseText.charAt(pos+(posIndex++)));
					if (child==null) break;
					cursor=child;
				}
			} catch (IndexOutOfBoundsException ex) {} // not avoiding this exception is expensive but only happens in the very rare circumstance that the end of file is encountered in the middle of a potential tag.
			// go back up until we reach a node that contains a list of tag types:
			while (cursor.tagTypes==null) if ((cursor=cursor.parent)==null) break;
		}

		public boolean hasNext() {
			return cursor!=null;
		}

		public TagType next() {
			final TagType[] tagTypes=cursor.tagTypes;
			final TagType nextTagType=tagTypes[tagTypeIndex];
			if ((++tagTypeIndex)==tagTypes.length) {
				tagTypeIndex=0;
				do {cursor=cursor.parent;} while (cursor!=null && cursor.tagTypes==null);
			}
			return nextTagType;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public String toString() {
		return appendDebugInfo(new StringBuilder(),0).toString();
	}

	private StringBuilder appendDebugInfo(final StringBuilder sb, final int level) {
		for (int i=0; i<level; i++) sb.append(" ");
		if (ch!=NULL_CHAR) sb.append(ch).append(' ');
		if (tagTypes!=null) {
			sb.append('(');
			for (TagType tagType : tagTypes) sb.append(tagType.getDescription()).append(", ");
			sb.setLength(sb.length()-2);
			sb.append(')');
		}
		sb.append(Config.NewLine);
		if (children!=null) {
			final int childLevel=level+1;
			for (TagTypeRegister tagTypeRegister : children) tagTypeRegister.appendDebugInfo(sb,childLevel);
		}
		return sb;
	}

	private TagTypeRegister getChild(final char ch) {
		if (children==null) return null;
		if (children.length==1) return children[0].ch==ch ? children[0] : null;
		// perform binary search:
		int low=0;
		int high=children.length-1;
		while (low<=high) {
			int mid=(low+high) >> 1;
			final char midChar=children[mid].ch;
			if (midChar<ch)
				low=mid+1;
			else if (midChar>ch)
				high=mid-1;
			else
				return children[mid];
		}
		return null;
	}
	
	private void addChild(final TagTypeRegister child) {
		// assumes the character associated with the child register does not already exist in this register's children.
		if (children==null) {
			children=new TagTypeRegister[] {child};
		} else {
			final TagTypeRegister[] newChildren=new TagTypeRegister[children.length+1];
			int i=0;
			while (i<children.length && children[i].ch<=child.ch) {
				newChildren[i]=children[i];
				i++;
			}
			newChildren[i++]=child;
			while (i<newChildren.length) {
				newChildren[i]=children[i-1];
				i++;
			}
			children=newChildren;
		}
	}

	private void removeChild(final TagTypeRegister child) {
		// this method assumes that the specified child exists in the children array
		if (children.length==1) {
			children=null;
			return;
		}
		final TagTypeRegister[] newChildren=new TagTypeRegister[children.length-1];
		int offset=0;
		for (int i=0; i<children.length; i++) {
			if (children[i]==child)
				offset=-1;
			else
				newChildren[i+offset]=children[i];
		}
		children=newChildren;
	}
	
	private int indexOfTagType(final TagType tagType) {
		if (tagTypes==null) return -1;
		for (int i=0; i<tagTypes.length; i++)
			if (tagTypes[i]==tagType) return i;
		return -1;
	}
	
	private void addTagType(final TagType tagType) {
		final int indexOfTagType=indexOfTagType(tagType);
		if (indexOfTagType==-1) {
			if (tagTypes==null) {
				tagTypes=new TagType[] {tagType};
			} else {
				final TagType[] newTagTypes=new TagType[tagTypes.length+1];
				newTagTypes[0]=tagType;
				for (int i=0; i<tagTypes.length; i++) newTagTypes[i+1]=tagTypes[i];
				tagTypes=newTagTypes;
			}
		} else {
			// tagType already exists in the list, just move it to the front
			for (int i=indexOfTagType; i>0; i--) tagTypes[i]=tagTypes[i-1];
			tagTypes[0]=tagType;
		}
	}

	private void removeTagType(final TagType tagType) {
		final int indexOfTagType=indexOfTagType(tagType);
		if (indexOfTagType==-1) return;
		if (tagTypes.length==1) {
			tagTypes=null;
			return;
		}
		final TagType[] newTagTypes=new TagType[tagTypes.length-1];
		for (int i=0; i<indexOfTagType; i++) newTagTypes[i]=tagTypes[i];
		for (int i=indexOfTagType; i<newTagTypes.length; i++) newTagTypes[i]=tagTypes[i+1];
		tagTypes=newTagTypes;
	}
}

