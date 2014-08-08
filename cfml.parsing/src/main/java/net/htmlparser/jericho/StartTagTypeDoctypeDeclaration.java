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

final class StartTagTypeDoctypeDeclaration extends StartTagTypeGenericImplementation {
	static final StartTagTypeDoctypeDeclaration INSTANCE=new StartTagTypeDoctypeDeclaration();

	private StartTagTypeDoctypeDeclaration() {
		super("document type declaration","<!doctype",">",null,false,false,false);
	}

	protected int getEnd(final Source source, int pos) {
		final ParseText parseText=source.getParseText();
		boolean insideQuotes=false;
		boolean insideSquareBrackets=false;
		do {
			final char c=parseText.charAt(pos);
			if (insideQuotes) {
				if (c=='"') insideQuotes=false;
			} else {
				switch (c) {
					case '>':
						if (!insideSquareBrackets) return pos+1;
						break;
					case '"':
						insideQuotes=true;
						break;
					case '[':
						insideSquareBrackets=true;
						break;
					case ']':
						insideSquareBrackets=false;
						break;
				}
			}
		} while ((++pos)<source.getEnd());
		return -1;
	}
}
