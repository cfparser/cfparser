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
import java.io.*;

/**
 * Implements an {@link OutputSegment} whose content is a list of attribute name/value pairs.
 * <p>
 * This output segment is designed to replace the original {@link Attributes} segment in the source,
 * providing a simple means of adding, modifying and removing attributes.
 * <p>
 * Each instance of this class contains a <code>java.util.Map</code> of name/value pairs which can either be
 * specified directly in the constructor or initialised to the same entries as the source {@link Attributes}
 * specified in the constructor.
 * This map can be accessed via the {@link #getMap()} method, and its entries modified as required before output.
 * <p>
 * Keys in the map must be <code>String</code> objects, and values must implement the <code>CharSequence</code> interface.
 * <p>
 * An attribute with no value is represented by a map entry with a <code>null</code> value.
 * <p>
 * Attribute values are stored unencoded in the map, and are automatically
 * {@linkplain CharacterReference#encode(CharSequence) encoded} if necessary during output.
 * <p>
 * The use of invalid characters in attribute names results in unspecified behaviour.
 * <p>
 * Note that methods in the <code>Attributes</code> class treat attribute names as case insensitive,
 * whereas the <code>Map</code> treats them as case sensitive.
 * <p>
 * This class has been removed from the public API and the functionality replaced with the
 * {@link OutputDocument#replace(Attributes, Map)} and {@link OutputDocument#replace(Attributes, boolean convertNamesToLowerCase)} methods.
 *
 * @see OutputDocument
 * @see Attributes
 */
class AttributesOutputSegment implements OutputSegment {
	private final int begin;
	private final int end;
	private final Map<String,String> map;

	/**
	 * Constructs a new <code>AttributesOutputSegment</code> with the same span and initial name/value entries as the specified source {@link Attributes}.
	 * <p>
	 * Specifying a value of <code>true</code> as an argument to the <code>convertNamesToLowerCase</code> parameter
	 * causes all attribute names to be converted to lower case in the map.
	 * This simplifies the process of finding/updating specific attributes since map keys are case sensitive.
	 * <p>
	 * Attribute values are automatically {@linkplain CharacterReference#decode(CharSequence) decoded} before
	 * being loaded into the map.
	 * <p>
	 * Calling this constructor with the following code:
	 * <div style="margin-left: 2em"><code>new AttributesOutputSegment(attributes, convertNamesToLowerCase)</code></div>
	 * is logically equivalent to calling:
	 * <div style="margin-left: 2em"><code>new AttributesOutputSegment(attributes, attributes.populateMap(new LinkedHashMap(), convertNamesToLowerCase))</code></div>
	 * <p>
	 * The use of <code>LinkedHashMap</code> to implement the map ensures (probably unnecessarily) that
	 * existing attributes are output in the same order as they appear in the source document, and new
	 * attributes are output in the same order as they are added.
	 *
	 * @param attributes  the <code>Attributes</code> defining the span and initial name/value entries of the new <code>AttributesOutputSegment</code>.
	 * @param convertNamesToLowerCase  specifies whether all attribute names are converted to lower case in the map.
	 * @see #AttributesOutputSegment(Attributes,Map)
	 */
	public AttributesOutputSegment(final Attributes attributes, final boolean convertNamesToLowerCase) {
		this(attributes,attributes.getMap(convertNamesToLowerCase));
	}

	/**
	 * Constructs a new <code>AttributesOutputSegment</code> with the same span
	 * as the specified source {@link Attributes}, using the specified <code>Map</code> to
	 * store the entries.
	 * <p>
	 * This constructor might be used if the <code>Map</code> containing the new attribute values
	 * should not be preloaded with the same entries as the source attributes, or a map implementation
	 * other than <code>LinkedHashMap</code> is required.
	 *
	 * @param attributes  the <code>Attributes</code> defining the span of the new <code>AttributesOutputSegment</code>.
	 * @param map  the <code>Map</code> containing the name/value entries.
	 * @see #AttributesOutputSegment(Attributes, boolean convertNamesToLowerCase)
	 */
	public AttributesOutputSegment(final Attributes attributes, final Map<String,String> map) {
		if (map==null || attributes==null) throw new IllegalArgumentException("both arguments must be non-null");
		begin=attributes.getBegin();
		end=attributes.getEnd();
		this.map=map;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	/**
	 * Returns the <code>Map</code> containing the name/value entries to be output.
	 * @return the <code>Map</code> containing the name/value entries to be output.
	 */
	public Map<String,String> getMap() {
		return map;
	}

	/**
	 * Writes the contents of the {@linkplain #getMap() map} as HTML attribute name/value pairs to the specified <code>Writer</code>.
	 * <p>
	 * This is equivalent to {@link #appendTo(Appendable) appendTo}<code>(writer)</code>.
	 *
	 * @param writer  the destination <code>java.io.Writer</code> for the output.
	 * @throws IOException if an I/O exception occurs.
	 * @see Attributes#generateHTML(Map attributesMap)
	 */
	public void writeTo(final Writer writer) throws IOException {
		Attributes.appendHTML(writer,map);
	}

	/**
	 * Appends the contents of the {@linkplain #getMap() map} as HTML attribute name/value pairs to the specified <code>Appendable</code> object.
	 * <p>
	 * Each attribute is preceded by a single space, and all values are
	 * {@linkplain CharacterReference#encode(CharSequence) encoded} and enclosed in double quotes.
	 *
	 * @param appendable  the destination <code>java.lang.Appendable</code> object for the output.
	 * @throws IOException if an I/O exception occurs.
	 * @see Attributes#generateHTML(Map attributesMap)
	 */
	public void appendTo(final Appendable appendable) throws IOException {
		Attributes.appendHTML(appendable,map);
	}

	public long getEstimatedMaximumOutputLength() {
		return (end-begin)*2;
	}

	public String toString() {
		return Attributes.generateHTML(map);
	}

	public String getDebugInfo() {
		StringBuilder sb=new StringBuilder();
		sb.append("(p").append(begin).append("-p").append(end).append("):");
		try {
			appendTo(sb);
		} catch (IOException ex) {throw new RuntimeException(ex);} // never happens
		return sb.toString();
	}
}
