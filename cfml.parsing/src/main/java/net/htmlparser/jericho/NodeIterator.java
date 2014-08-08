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
 * Iterates over the "nodes" in a segment.
 * <p>
 * Every object returned is a Segment.  All tags found with the Segment.getAllTags() method are included, as well as segments representing the plain text in between them,
 * and character references within the plain text are also included as separate nodes.
 */
class NodeIterator implements Iterator<Segment> {
	private final Segment segment;
	private final Source source;
	private int pos;
	private Tag nextTag;
	private CharacterReference characterReferenceAtCurrentPosition=null;

	private final boolean legacyIteratorCompatabilityMode=Source.LegacyIteratorCompatabilityMode;

	public NodeIterator(final Segment segment) {
		this.segment=segment;
		source=segment.source;
		if (segment==source) source.fullSequentialParse();
		pos=segment.begin;
		nextTag=source.getNextTag(pos);
		if (nextTag!=null && nextTag.begin>=segment.end) nextTag=null;
	}

	public boolean hasNext() {
		return pos<segment.end || nextTag!=null;
	}	

	public Segment next() {
		final int oldPos=pos;
		if (nextTag!=null) {
			if (oldPos<nextTag.begin) return nextNonTagSegment(oldPos,nextTag.begin);
			final Tag tag=nextTag;
			nextTag=nextTag.getNextTag();
			if (nextTag!=null && nextTag.begin>=segment.end) nextTag=null;
			if (pos<tag.end) pos=tag.end;
			return tag;
		} else {
			if (!hasNext()) throw new NoSuchElementException();
			return nextNonTagSegment(oldPos,segment.end);
		}
	}

	private Segment nextNonTagSegment(final int begin, final int end) {
		if (!legacyIteratorCompatabilityMode) {
			final CharacterReference characterReference=characterReferenceAtCurrentPosition;
			if (characterReference!=null) {
				characterReferenceAtCurrentPosition=null;
				pos=characterReference.end;
				return characterReference;
			}
			final ParseText parseText=source.getParseText();
			int potentialCharacterReferenceBegin=parseText.indexOf('&',begin,end);
			while (potentialCharacterReferenceBegin!=-1) {
				final CharacterReference nextCharacterReference=CharacterReference.construct(source,potentialCharacterReferenceBegin,Config.UnterminatedCharacterReferenceSettings.ACCEPT_ALL);
				if (nextCharacterReference!=null) {
					if (potentialCharacterReferenceBegin==begin) {
						pos=nextCharacterReference.end;
						return nextCharacterReference;
					} else {
						pos=nextCharacterReference.begin;
						characterReferenceAtCurrentPosition=nextCharacterReference;
						return new Segment(source,begin,pos);
					}
				}
				potentialCharacterReferenceBegin=parseText.indexOf('&',potentialCharacterReferenceBegin+1,end);
			}
		}
		return new Segment(source,begin,pos=end);
	}

	public void skipToPos(final int pos) {
		if (pos<this.pos) return; // can't go backwards
		this.pos=pos;
		nextTag=source.getNextTag(pos);
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
