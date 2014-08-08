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

final class HTMLElementNameSet extends HashSet<String> {
	public HTMLElementNameSet() {
		super(1);
	}

	public HTMLElementNameSet(final String[] items) {
		super(items.length*2);
		for (int i=0; i<items.length; i++) add(items[i]);
	}

	public HTMLElementNameSet(final Collection<String> collection) {
		super(collection.size()*2);
		union(collection);
	}

	public HTMLElementNameSet(final String item) {
		super(2);
		add(item);
	}

	HTMLElementNameSet union(final String item) {
		add(item);
		return this;
	}

	HTMLElementNameSet union(final Collection<String> collection) {
		for (String item : collection) add(item);
		return this;
	}

	HTMLElementNameSet minus(final String item) {
		remove(item);
		return this;
	}

	HTMLElementNameSet minus(final Collection<String> collection) {
		for (String item : collection) remove(item);
		return this;
	}
}
