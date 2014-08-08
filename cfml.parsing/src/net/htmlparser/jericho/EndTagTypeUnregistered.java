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

final class EndTagTypeUnregistered extends EndTagType {
	static final EndTagTypeUnregistered INSTANCE=new EndTagTypeUnregistered();

	private EndTagTypeUnregistered() {
		super("/unregistered",START_DELIMITER_PREFIX,">",false);
	}

	protected Tag constructTagAt(final Source source, final int pos) {
		final ParseText parseText=source.getParseText();
		final int nameBegin=pos+getStartDelimiter().length();
		final int nameEnd=parseText.indexOf(getClosingDelimiter(),nameBegin);
		final String name=source.getName(nameBegin,nameEnd); // throws IndexOutOfBoundsException if nameEnd==-1
		final EndTag endTag=constructEndTag(source,pos,nameEnd+getClosingDelimiter().length(),name);
		if (source.logger.isInfoEnabled()) source.logger.info(source.getRowColumnVector(pos).appendTo(new StringBuilder(200).append("Encountered possible EndTag at ")).append(" whose content does not match a registered EndTagType").toString());
		return endTag;
	}
}
