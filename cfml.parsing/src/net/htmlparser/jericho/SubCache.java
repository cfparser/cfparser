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
 * Represents a cached map of character positions to tags for a particular tag type,
 * or for all tag types if the tagType field is null.
 */
final class SubCache {
	private final Cache cache;
	public final TagType tagType; // does not support unregistered tag types at present
	private final CacheEntry bof; // beginning of file marker
	private final CacheEntry eof; // end of file marker
	private CacheEntry[] array=new CacheEntry[INITIAL_CAPACITY];

	private static final int INITIAL_CAPACITY=64;

	public SubCache(final Cache cache, final TagType tagType) {
		this.cache=cache;
		this.tagType=tagType;
		array[0]=bof=new CacheEntry(0,-1,null,false,false);
		array[1]=eof=new CacheEntry(1,cache.getSourceLength(),null,false,false);
	}

	public int size() {
		return eof.index+1;
	}

	public void clear() {
		bof.nextCached=false;
		eof.index=1;
		eof.previousCached=false;
		array[1]=eof;
	}

	public void bulkLoad_Init(final int tagCount) {
		array=new CacheEntry[tagCount+2];
		array[0]=bof;
		bof.nextCached=true;
		array[eof.index=tagCount+1]=eof;
		eof.previousCached=true;
	}

	public void bulkLoad_Set(final int tagsIndex, final Tag tag) {
		final int index=tagsIndex+1;
		array[index]=new CacheEntry(index,tag.begin,tag,true,true);
	}

	public void bulkLoad_AddToTypeSpecificCache(final Tag tag) {
		final int index=eof.index;
		if (array.length==eof.index+1) doubleCapacity();
		array[index]=new CacheEntry(index,tag.begin,tag,true,true);
		eof.index++;
	}

	public void bulkLoad_FinaliseTypeSpecificCache() {
		bof.nextCached=true;
		eof.previousCached=true;
		array[eof.index]=eof;
	}

	public Tag getTagAt(final int pos, final boolean serverTagOnly) {
		// This must only be called on allTagTypesSubCache (ie tagType==null)
		if (cache.getSourceLength()==0) return null;
		if (pos<0 || pos>=cache.getSourceLength()) return null;
		final int index=getIndexOfPos(pos);
		final CacheEntry cacheEntry=array[index];
		if (cacheEntry.pos==pos) {
			if (serverTagOnly && !cacheEntry.tag.getTagType().isServerTag()) return null;
			return cacheEntry.tag;
		}
		if (cacheEntry.previousCached) return null;
		return cache.addTagAt(pos,serverTagOnly);
	}

	public void addTagAt(final int pos, final Tag tag) {
		final int index=getIndexOfPos(pos);
		final CacheEntry nextCacheEntry=array[index];
		final CacheEntry previousCacheEntry=getPrevious(nextCacheEntry);
		add(previousCacheEntry,new CacheEntry(index,pos,tag,pos==previousCacheEntry.pos+1,pos==nextCacheEntry.pos-1),nextCacheEntry);
	}

	public Tag getPreviousTag(final int pos) {
		// Note that this method never returns tags for which tag.includInSearch() is false, so separate caching of unregistered tags won't work.
		if (cache.getSourceLength()==0) return null;
		if (pos<0 || pos>=cache.getSourceLength()) return null;
		int index=getIndexOfPos(pos);
		final CacheEntry cacheEntry=array[index];
		final Tag tag;
		if (cacheEntry.pos==pos && cacheEntry.tag!=null && cacheEntry.tag.includeInSearch()) return cacheEntry.tag;
		tag=getPreviousTag(getPrevious(cacheEntry),pos,cacheEntry);
		addPreviousTag(pos,tag);
		return tag;
	}

	public Tag getNextTag(final int pos) {
		// Note that this method never returns tags for which tag.includInSearch() is false, so separate caching of unregistered tags won't work.
		if (cache.getSourceLength()==0) return null;
		if (pos<0 || pos>=cache.getSourceLength()) return null;
		int index=getIndexOfPos(pos);
		final CacheEntry cacheEntry=array[index];
		final Tag tag;
		if (cacheEntry.pos==pos) {
			if (cacheEntry.tag!=null && cacheEntry.tag.includeInSearch()) return cacheEntry.tag;
			tag=getNextTag(cacheEntry,pos,getNext(cacheEntry));
		} else {
			tag=getNextTag(getPrevious(cacheEntry),pos,cacheEntry);
		}
		addNextTag(pos,tag);
		return tag;
	}

	public Iterator<Tag> getTagIterator() {
		return new TagIterator();
	}

	public String toString() {
		return appendTo(new StringBuilder()).toString();
	}

	protected StringBuilder appendTo(final StringBuilder sb) {
		sb.append("Cache for TagType : ").append(tagType).append(Config.NewLine);
		for (int i=0; i<=lastIndex(); i++) sb.append(array[i]).append(Config.NewLine);
		return sb;
	}

	private Tag getPreviousTag(CacheEntry previousCacheEntry, int pos, CacheEntry nextCacheEntry) {
		// previousCacheEntry.pos < pos <= nextCacheEntry.pos
		while (true) {
			if (!nextCacheEntry.previousCached) {
				final Tag tag=Tag.getPreviousTagUncached(cache.source,pos,tagType,previousCacheEntry.pos); // if useAllTypesCache is true, automatically adds tag to all caches if one is found, and maybe some unregistered tags along the way.
				if (tag!=null) {
					if (!cache.source.useAllTypesCache) addTagAt(tag.begin,tag); // have to add tag manually if useAllTypesCache is false
					return tag;
				}
			}
			if (previousCacheEntry==bof) return null;
			if (previousCacheEntry.tag!=null && previousCacheEntry.tag.includeInSearch()) return previousCacheEntry.tag;
			pos=previousCacheEntry.pos-1;
			previousCacheEntry=getPrevious(nextCacheEntry=previousCacheEntry);
		}
	}

	private Tag getNextTag(CacheEntry previousCacheEntry, int pos, CacheEntry nextCacheEntry) {
		// previousCacheEntry.pos <= pos < nextCacheEntry.pos
		while (true) {
			if (!previousCacheEntry.nextCached) {
				final Tag tag=Tag.getNextTagUncached(cache.source,pos,tagType,nextCacheEntry.pos); // if useAllTypesCache is true, automatically adds tag to caches if one is found, and maybe some unregistered tags along the way.
				if (tag!=null) {
					if (!cache.source.useAllTypesCache) addTagAt(tag.begin,tag); // have to add tag manually if useAllTypesCache is false
					return tag;
				}
			}
			if (nextCacheEntry==eof) return null;
			if (nextCacheEntry.tag!=null && nextCacheEntry.tag.includeInSearch()) return nextCacheEntry.tag;
			pos=nextCacheEntry.pos+1;
			nextCacheEntry=getNext(previousCacheEntry=nextCacheEntry);
		}
	}

	private void addPreviousTag(final int pos, final Tag tag) {
		final int tagPos=(tag==null) ? bof.pos : tag.begin;
		if (tagPos==pos) return; // the tag was found exactly on pos, so cache has already been fully updated
		// tagPos < pos
		int index=getIndexOfPos(pos);
		CacheEntry stepCacheEntry=array[index];
		// stepCacheEntry.pos is either == or > than tagPos.
		// stepCacheEntry.pos is either == or > pos.
		int compactStartIndex=Integer.MAX_VALUE;
		if (stepCacheEntry.pos==pos) {
			// a cache entry was aleady at pos (containing null or wrong tagType)
			stepCacheEntry.previousCached=true;
			if (stepCacheEntry.isRedundant()) {stepCacheEntry.removed=true; compactStartIndex=Math.min(compactStartIndex,stepCacheEntry.index);}
		} else if (!stepCacheEntry.previousCached) {
			// we have to add a new cacheEntry at pos:
			if (tagType==null)
				cache.addTagAt(pos,false); // this pos has never been checked before, so add it to all relevant SubCaches (a null or unregistered tag entry is always added to this SubCache)
			else
				addTagAt(pos,null); // all we know is that the pos doesn't contain a tag of this SubCache's type, so add a null entry to this SubCache only.
			// now we have to reload the index and stepCacheEntry as they may have changed:
			stepCacheEntry=array[index=getIndexOfPos(pos)];
			// stepCacheEntry.pos is either == or > than tagPos.
			// stepCacheEntry.pos is either == or > pos. (the latter if the added entry was redundant)
			if (stepCacheEntry.pos==pos) {
				// perform same steps as in the (stepCacheEntry.pos==pos) if condition above:
				stepCacheEntry.previousCached=true;
				if (stepCacheEntry.isRedundant()) {stepCacheEntry.removed=true; compactStartIndex=Math.min(compactStartIndex,stepCacheEntry.index);}
			}
		}
		while (true) {
			stepCacheEntry=array[--index];
			if (stepCacheEntry.pos<=tagPos) break;
			if (stepCacheEntry.tag!=null) {
				if (stepCacheEntry.tag.includeInSearch()) throw new SourceCacheEntryMissingInternalError(tagType,tag,this);
				stepCacheEntry.previousCached=true;
				stepCacheEntry.nextCached=true;
			} else {
				stepCacheEntry.removed=true; compactStartIndex=Math.min(compactStartIndex,stepCacheEntry.index);
			}
		}	
		if (stepCacheEntry.pos!=tagPos) throw new FoundCacheEntryMissingInternalError(tagType,tag,this);
		stepCacheEntry.nextCached=true;
		compact(compactStartIndex);
	}

	private void addNextTag(final int pos, final Tag tag) {
		final int tagPos=(tag==null) ? eof.pos : tag.begin;
		if (tagPos==pos) return; // the tag was found exactly on pos, so cache has already been fully updated
		// tagPos > pos
		int index=getIndexOfPos(pos);
		CacheEntry stepCacheEntry=array[index];
		// stepCacheEntry.pos may be <, == or > than tagPos.
		// stepCacheEntry.pos is either == or > pos.
		int compactStartIndex=Integer.MAX_VALUE;
		if (stepCacheEntry.pos==pos) {
			// a cache entry was aleady at pos (containing null or wrong tagType)
			stepCacheEntry.nextCached=true;
			if (stepCacheEntry.isRedundant()) {stepCacheEntry.removed=true; compactStartIndex=Math.min(compactStartIndex,stepCacheEntry.index);}
		} else if (!getPrevious(stepCacheEntry).nextCached) {
			// we have to add a new cacheEntry at pos:
			if (tagType==null)
				cache.addTagAt(pos,false); // this pos has never been checked before, so add it to all relevant SubCaches (a null or unregistered tag entry is always added to this SubCache)
			else
				addTagAt(pos,null); // all we know is that the pos doesn't contain a tag of this SubCache's type, so add a null entry to this SubCache only.
			// now we have to reload the index and stepCacheEntry as they may have changed:
			stepCacheEntry=array[index=getIndexOfPos(pos)];
			// stepCacheEntry.pos may be <, == or > than tagPos.
			// stepCacheEntry.pos is either == or > pos. (the latter if the added entry was redundant)
			if (stepCacheEntry.pos==pos) {
				// perform same steps as in the (stepCacheEntry.pos==pos) if condition above:
				stepCacheEntry.nextCached=true;
				if (stepCacheEntry.isRedundant()) {stepCacheEntry.removed=true; compactStartIndex=Math.min(compactStartIndex,stepCacheEntry.index);}
			}
		}
		if (stepCacheEntry.pos<tagPos) {
			while (true) {
				stepCacheEntry=array[++index];
				if (stepCacheEntry.pos>=tagPos) break;
				if (stepCacheEntry.tag!=null) {
					if (stepCacheEntry.tag.includeInSearch()) throw new SourceCacheEntryMissingInternalError(tagType,tag,this);
					stepCacheEntry.previousCached=true;
					stepCacheEntry.nextCached=true;
				} else {
					stepCacheEntry.removed=true; compactStartIndex=Math.min(compactStartIndex,stepCacheEntry.index);
				}
			}	
			if (stepCacheEntry.pos!=tagPos) throw new FoundCacheEntryMissingInternalError(tagType,tag,this);
		}
		stepCacheEntry.previousCached=true;
		compact(compactStartIndex);
	}

	private void compact(int i) {
		final int lastIndex=lastIndex();
		int removedCount=1;
		while (i<lastIndex) {
			final CacheEntry cacheEntry=array[++i];
			if (cacheEntry.removed)
				removedCount++;
			else
				array[cacheEntry.index=i-removedCount]=cacheEntry;
		}
	}

	private void add(final CacheEntry previousCacheEntry, final CacheEntry newCacheEntry, final CacheEntry nextCacheEntry) {
		if (!newCacheEntry.isRedundant()) insert(newCacheEntry);
		if (newCacheEntry.previousCached) {
			previousCacheEntry.nextCached=true;
			if (previousCacheEntry.isRedundant()) remove(previousCacheEntry);
		}
		if (newCacheEntry.nextCached) {
			nextCacheEntry.previousCached=true;
			if (nextCacheEntry.isRedundant()) remove(nextCacheEntry);
		}
	}

	private int getIndexOfPos(final int pos) {
		// return the index of the cacheEntry at pos, or the index where it would be inserted if it does not exist.
		int minIndex=0;
		int maxIndex=lastIndex();
		// using the following complex calculation instead of a binary search is likely to result in less iterations but is slower overall:
		// int index=(pos*maxIndex)/cache.getSourceLength(); // approximate first guess at index, assuming even distribution of cache entries
		int index=maxIndex>>1;
		while (true) {
			final CacheEntry cacheEntry=array[index];
			if (pos>cacheEntry.pos) {
				final CacheEntry nextCacheEntry=getNext(cacheEntry);
				if (pos<=nextCacheEntry.pos) return nextCacheEntry.index;
				minIndex=nextCacheEntry.index;
			} else if (pos<cacheEntry.pos) {
				final CacheEntry previousCacheEntry=getPrevious(cacheEntry);
				if (pos==previousCacheEntry.pos) return previousCacheEntry.index;
				if (pos>previousCacheEntry.pos) return index;
				maxIndex=previousCacheEntry.index;
			} else {
				return index;
			}
			index=(minIndex+maxIndex)>>1;
			// using the following complex calculation instead of a binary search is likely to result in less iterations but is slower overall:
			// final int minIndexPos=array[minIndex].pos;
			// index=((maxIndex-minIndex-1)*(pos-minIndexPos))/(array[maxIndex].pos-minIndexPos)+minIndex+1; // approximate next guess at index, assuming even distribution of cache entries between min and max entries
		}
	}

	private CacheEntry getNext(final CacheEntry cacheEntry) {
		return array[cacheEntry.index+1];
	}

	private CacheEntry getPrevious(final CacheEntry cacheEntry) {
		return array[cacheEntry.index-1];
	}
	
	private int lastIndex() {
		return eof.index;
	}

	private void insert(final CacheEntry cacheEntry) {
		final int index=cacheEntry.index;
		if (array.length==size()) doubleCapacity();
		for (int i=lastIndex(); i>=index; i--) {
			final CacheEntry movedCacheEntry=array[i];
			array[movedCacheEntry.index=(i+1)]=movedCacheEntry;
		}
		array[index]=cacheEntry;
	}

	private void remove(final CacheEntry cacheEntry) {
		final int lastIndex=lastIndex();
		for (int i=cacheEntry.index; i<lastIndex; i++) {
			final CacheEntry movedCacheEntry=array[i+1];
			array[movedCacheEntry.index=i]=movedCacheEntry;
		}
	}

	private void doubleCapacity() {
		// assumes size==array.length
		final CacheEntry[] newArray=new CacheEntry[array.length << 1];
		for (int i=lastIndex(); i>=0; i--) newArray[i]=array[i];
		array=newArray;
	}

	private static class CacheEntryMissingInternalError extends AssertionError {
		public CacheEntryMissingInternalError(final TagType tagType, final Tag tag, final SubCache subCache, final String message) {
			super("INTERNAL ERROR: Inconsistent Cache State for TagType \""+tagType+"\" - "+message+' '+tag.getDebugInfo()+'\n'+subCache);
		}
	}

	private static class SourceCacheEntryMissingInternalError extends CacheEntryMissingInternalError {
		public SourceCacheEntryMissingInternalError(final TagType tagType, final Tag tag, final SubCache subCache) {
			super(tagType,tag,subCache,"cache entry no longer found in source:");
		}
	}

	private static class FoundCacheEntryMissingInternalError extends CacheEntryMissingInternalError {
		public FoundCacheEntryMissingInternalError(final TagType tagType, final Tag tag, final SubCache subCache) {
			super(tagType,tag,subCache,"missing cache entry for found tag");
		}
	}

	private final class TagIterator implements Iterator<Tag> {
		private int i=0;
		private Tag nextTag;
		public TagIterator() {
			loadNextTag();
		}
		public boolean hasNext() {
			return nextTag!=null;
		}
		public Tag next() {
			final Tag result=nextTag;
			loadNextTag();
			return result;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
		private void loadNextTag() {
			while (++i<=lastIndex() && (nextTag=array[i].tag)==null) {}
		}
	}

	private static final class CacheEntry {
		public int index;
		public final int pos;
		public final Tag tag;
		public boolean previousCached;
		public boolean nextCached;
		public boolean removed=false;
		
		public CacheEntry(final int index, final int pos, final Tag tag, final boolean previousCached, final boolean nextCached) {
			this.index=index;
			this.pos=pos;
			this.tag=tag;
			this.previousCached=previousCached;
			this.nextCached=nextCached;
		}
		
		public boolean isRedundant() {
			return tag==null && previousCached && nextCached;
		}
		
		public String toString() {
			return pad(index,4)+" "+pad(pos,5)+" "+(previousCached?'|':'-')+' '+(nextCached?'|':'-')+' '+(tag==null ? "null" : tag.getDebugInfo());
		}
		
		private String pad(final int n, final int places) {
			final String nstring=String.valueOf(n);
			final StringBuilder sb=new StringBuilder(places);
			for (int i=places-nstring.length(); i>0; i--) sb.append(' ');
			sb.append(nstring);
			return sb.toString();
		}
	}
}
