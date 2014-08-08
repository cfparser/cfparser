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
 * This is an internal class used to efficiently map integers to strings, which is used in the CharacterEntityReference class.
 */
final class IntStringHashMap {
	private static final int DEFAULT_INITIAL_CAPACITY=15;
	private static final float DEFAULT_LOAD_FACTOR=0.75f;
	private transient Entry[] entries; // length must always be a power of 2.
	private transient int size;
	private int threshold;
	private float loadFactor;
	private int bitmask; // always entries.length-1

	public IntStringHashMap(int initialCapacity, final float loadFactor) {
		this.loadFactor=loadFactor;
		int capacity=1;
		while (capacity<initialCapacity) capacity<<=1;
		threshold=(int)(capacity*loadFactor);
		entries=new Entry[capacity];
		bitmask=capacity-1;
	}

	public IntStringHashMap(final int initialCapacity) {
		this(initialCapacity,DEFAULT_LOAD_FACTOR);
	}

	public IntStringHashMap() {
		this(DEFAULT_INITIAL_CAPACITY,DEFAULT_LOAD_FACTOR);
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size==0;
	}

	private int getIndex(final int key) {
		return key&bitmask; // equivalent to (key%entries.length) but more efficient.
	}

	public String get(final int key) {
		Entry entry=entries[getIndex(key)];
		while (entry!=null) {
			if (key==entry.key) return entry.value;
			entry=entry.next;
		}
		return null;
	}

	private Entry getEntry(final int key) {
		Entry entry=entries[getIndex(key)];
		while (entry!=null && key!=entry.key) entry=entry.next;
		return entry;
	}

	public boolean containsKey(final int key) {
		return getEntry(key)!=null;
	}

	public String put(final int key, final String value) {
		final int index=getIndex(key);
		for (Entry entry=entries[index]; entry!= null; entry=entry.next) {
			if (key==entry.key) {
				final String oldValue=entry.value;
				entry.value=value;
				return oldValue;
			}
		}
		entries[index]=new Entry(key,value,entries[index]);
		if (size++>=threshold) increaseCapacity();
		return null;
	}

	private void increaseCapacity() {
		final int oldCapacity=entries.length;
		final Entry[] oldEntries=entries;
		entries=new Entry[oldCapacity<<1];
		bitmask=entries.length-1;
		for (Entry entry : oldEntries) {
			while (entry!=null) {
				final Entry next=entry.next;
				final int index=getIndex(entry.key);
				entry.next=entries[index];
				entries[index]=entry;
				entry=next;
			}
		}
		threshold=(int)(entries.length*loadFactor);
	}

	public String remove(final int key) {
		final int index=getIndex(key);
		Entry previous=null;
		for (Entry entry=entries[index]; entry!=null; entry=(previous=entry).next) {
			if (key==entry.key) {
				if (previous==null)
					entries[index]=entry.next;
				else
					previous.next=entry.next;
				size--;
				return entry.value;
			}
		}
		return null;
	}

	public void clear() {
		for (int i=bitmask; i>=0; i--) entries[i]=null;
		size=0;
	}

	public boolean containsValue(final String value) {
		if (value==null) {
			for (int i=bitmask; i>=0; i--)
				for (Entry entry=entries[i]; entry!=null; entry=entry.next)
					if (entry.value==null) return true;
		} else {
			for (int i=bitmask; i>=0; i--)
				for (Entry entry=entries[i]; entry!=null; entry=entry.next)
					if (value.equals(entry.value)) return true;
		}
		return false;
	}

	private static final class Entry {
		final int key;
		String value;
		Entry next;

		public Entry(final int key, final String value, final Entry next) {
			this.key=key;
			this.value=value;
			this.next=next;
		}
	}
}
