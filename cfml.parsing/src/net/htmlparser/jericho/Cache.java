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
 * Represents a cached map of character positions to tags.
 * The allTagTypesSubCache object is used to cache all tags.
 * Additional subcaches are used to cache single tag types. See the TagType.getTagTypesIgnoringEnclosedMarkup() method for details.
 */
final class Cache {
	public final Source source;
	private final SubCache allTagTypesSubCache;
	private final SubCache[] subCaches; // contains allTagTypesSubCache plus a SubCache object for each separately cached tag type

	static final Cache STREAMED_SOURCE_MARKER=new Cache();

	public Cache(final Source source) {
		this.source=source;
		allTagTypesSubCache=new SubCache(this,null);
		TagType[] separatelyCachedTagTypes=getSeparatelyCachedTagTypes();
		subCaches=new SubCache[separatelyCachedTagTypes.length+1];
		subCaches[0]=allTagTypesSubCache;
		for (int i=0; i<separatelyCachedTagTypes.length; i++)
			subCaches[i+1]=new SubCache(this,separatelyCachedTagTypes[i]);
	}

	// used only to create STREAMED_SOURCE_MARKER
	private Cache() {
		source=null;
		allTagTypesSubCache=null;
		subCaches=null;
	}

	public void clear() {
		for (Iterator<Tag> i=allTagTypesSubCache.getTagIterator(); i.hasNext();) i.next().orphan();
		for (int i=0; i<subCaches.length; i++) subCaches[i].clear();
	}

	public Tag getTagAt(final int pos, final boolean serverTagOnly) {
		return source.useAllTypesCache
			?	allTagTypesSubCache.getTagAt(pos,serverTagOnly)
			: Tag.getTagAtUncached(source,pos,serverTagOnly);
	}

	public Tag getPreviousTag(final int pos) {
		// returns null if pos is out of range.
		return allTagTypesSubCache.getPreviousTag(pos);
	}

	public Tag getNextTag(final int pos) {
		// returns null if pos is out of range.
		return allTagTypesSubCache.getNextTag(pos);
	}

	public Tag getPreviousTag(final int pos, final TagType tagType) {
		// returns null if pos is out of range.
		for (int i=source.useAllTypesCache ? 0 : 1; i<subCaches.length; i++)
			if (tagType==subCaches[i].tagType) return subCaches[i].getPreviousTag(pos);
		return Tag.getPreviousTagUncached(source,pos,tagType,ParseText.NO_BREAK);
	}

	public Tag getNextTag(final int pos, final TagType tagType) {
		// returns null if pos is out of range.
		for (int i=source.useAllTypesCache ? 0 : 1; i<subCaches.length; i++)
			if (tagType==subCaches[i].tagType) return subCaches[i].getNextTag(pos);
		return Tag.getNextTagUncached(source,pos,tagType,ParseText.NO_BREAK);
	}

	public Tag addTagAt(final int pos, final boolean serverTagOnly) {
		final Tag tag=Tag.getTagAtUncached(source,pos,serverTagOnly);
		if (serverTagOnly && tag==null) return null; // don't add null to cache if we were only looking for server tags
		allTagTypesSubCache.addTagAt(pos,tag);
		if (tag==null) return null;
		final TagType tagType=tag.getTagType();
		for (int i=1; i<subCaches.length; i++) {
			if (tagType==subCaches[i].tagType) {
				subCaches[i].addTagAt(pos,tag);
				return tag;
			}
		}
		return tag;
	}

	public int getTagCount() {
		return allTagTypesSubCache.size()-2;
	}

	public Iterator<Tag> getTagIterator() {
		return allTagTypesSubCache.getTagIterator();
	}

	public void loadAllTags(final List<Tag> tags, final Tag[] allRegisteredTags, final StartTag[] allRegisteredStartTags) {
		// assumes the tags list implements RandomAccess
		final int tagCount=tags.size();
		allTagTypesSubCache.bulkLoad_Init(tagCount);
		int registeredTagIndex=0;
		int registeredStartTagIndex=0;
		for (int i=0; i<tagCount; i++) {
			Tag tag=tags.get(i);
			if (!tag.isUnregistered()) {
				allRegisteredTags[registeredTagIndex++]=tag;
				if (tag instanceof StartTag) allRegisteredStartTags[registeredStartTagIndex++]=(StartTag)tag;
			}
			allTagTypesSubCache.bulkLoad_Set(i,tag);
			for (int x=1; x<subCaches.length; x++) {
				if (tag.getTagType()==subCaches[x].tagType) {
					subCaches[x].bulkLoad_AddToTypeSpecificCache(tag);
					break;
				}
			}
		}
		for (int x=1; x<subCaches.length; x++)
			subCaches[x].bulkLoad_FinaliseTypeSpecificCache();
	}

	public String toString() {
		StringBuilder sb=new StringBuilder();
		for (int i=0; i<subCaches.length; i++) subCaches[i].appendTo(sb);
		return sb.toString();
	}

	protected int getSourceLength() {
		return source.end;
	}
	
	private static TagType[] getSeparatelyCachedTagTypes() {
		return TagType.getTagTypesIgnoringEnclosedMarkup();
	}
}
