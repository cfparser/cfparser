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

class StartTagTypeMarkupDeclaration extends StartTagTypeGenericImplementation {
	static final StartTagTypeMarkupDeclaration INSTANCE=new StartTagTypeMarkupDeclaration();

	static final String ELEMENT="!element";
	static final String ATTLIST="!attlist";
	static final String ENTITY="!entity";
	static final String NOTATION="!notation";

	private StartTagTypeMarkupDeclaration() {
		super("markup declaration","<!",">",null,false,false,true);
	}

	protected Tag constructTagAt(final Source source, final int pos) {
		final Tag tag=super.constructTagAt(source,pos);
		if (tag==null) return null;
		final String name=tag.getName();
		if (name!=ELEMENT && name!=ATTLIST && name!=ENTITY && name!=NOTATION) return null; // can use == instead of .equals() because the names are in HtmlElements.CONSTANT_NAME_MAP
		return tag;
	}

	protected int getEnd(final Source source, int pos) {
		final ParseText parseText=source.getParseText();
		boolean insideQuotes=false;
		do {
			final char c=parseText.charAt(pos);
			if (c=='"') {
				insideQuotes=!insideQuotes;
			} else if (c=='>' && !insideQuotes) {
				return pos+1;
			}
		} while ((++pos)<source.getEnd());
		return -1;
	}
}
