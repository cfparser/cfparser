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

import java.io.*;
import java.util.*;

/**
 * Represents a modified version of an original {@link Source} document or {@link Segment}.
 * <p>
 * An <code>OutputDocument</code> represents an original {@link Source} document or {@link Segment} that
 * has been modified by substituting segments of it with other text.
 * Each of these substitutions must be registered in the output document,
 * which is most commonly done using the various <code>replace</code>, <code>remove</code> or <code>insert</code> methods in this class.
 * These methods internally {@linkplain #register(OutputSegment) register} one or more {@link OutputSegment} objects to define each substitution.
 * <p>
 * If a {@link Segment} is used to construct the output document, all character positions are relative to the source document of the specified segment.
 * <p>
 * After all of the substitutions have been registered, the modified text can be retrieved using the
 * {@link #writeTo(Writer)} or {@link #toString()} methods.
 * <p>
 * The registered {@linkplain OutputSegment output segments} may be adjacent and may also overlap.
 * An output segment that is completely enclosed by another output segment is not included in the output.
 * <p>
 * If unexpected results are being generated from an <code>OutputDocument</code>, the {@link #getDebugInfo()} method provides information on each
 * {@linkplain #getRegisteredOutputSegments() registered output segment}, which should provide enough information to determine the cause of the problem.
 * In most cases the problem will be caused by overlapping output segments.
 * <p>
 * The following example converts all externally referenced style sheets to internal style sheets:
 * <p>
 * <pre>
 *  URL sourceUrl=new URL(sourceUrlString);
 *  String htmlText=Util.getString(new InputStreamReader(sourceUrl.openStream()));
 *  Source source=new Source(htmlText);
 *  OutputDocument outputDocument=new OutputDocument(source);
 *  StringBuilder sb=new StringBuilder();
 *  List linkStartTags=source.getAllStartTags(HTMLElementName.LINK);
 *  for (Iterator i=linkStartTags.iterator(); i.hasNext();) {
 *    StartTag startTag=(StartTag)i.next();
 *    Attributes attributes=startTag.getAttributes();
 *    String rel=attributes.getValue("rel");
 *    if (!"stylesheet".equalsIgnoreCase(rel)) continue;
 *    String href=attributes.getValue("href");
 *    if (href==null) continue;
 *    String styleSheetContent;
 *    try {
 *      styleSheetContent=Util.getString(new InputStreamReader(new URL(sourceUrl,href).openStream()));
 *    } catch (Exception ex) {
 *      continue; // don't convert if URL is invalid
 *    }
 *    sb.setLength(0);
 *    sb.append("&lt;style");
 *    Attribute typeAttribute=attributes.get("type");
 *    if (typeAttribute!=null) sb.append(' ').append(typeAttribute);
 *    sb.append("&gt;\n").append(styleSheetContent).append("\n&lt;/style&gt;");
 *    outputDocument.replace(startTag,sb);
 *  }
 *  String convertedHtmlText=outputDocument.toString();
 * </pre>
 *
 * @see OutputSegment
 */
public final class OutputDocument implements CharStreamSource {
	private CharSequence sourceText;
	private ArrayList<OutputSegment> outputSegments=new ArrayList<OutputSegment>();

	/**
	 * Constructs a new output document based on the specified source document.
	 * @param source  the source document.
	 */
	public OutputDocument(final Source source) {
	  if (source==null) throw new IllegalArgumentException("source argument must not be null");
		this.sourceText=source;
	}

	/**
	 * Constructs a new output document based on the specified {@link Segment}.
	 * @param segment  the original {@link Segment}.
	 */
	public OutputDocument(final Segment segment) {
	  if (segment==null) throw new IllegalArgumentException("segment argument must not be null");
	  Source source=segment.source;
		this.sourceText=source;
		if (segment.begin>0) remove(new Segment(source,0,segment.begin));
		if (segment.end<source.end) remove(new Segment(source,segment.end,source.end));
	}

	OutputDocument(final ParseText parseText) {
		this.sourceText=parseText;
	}

	/**
	 * Returns the original source text upon which this output document is based.
	 * <p>
	 * If a {@link Segment} was used to construct the output document, this returns the text of the entire source document rather than just the segment.
	 *
	 * @return the original source text upon which this output document is based.
	 */
	public CharSequence getSourceText() {
		return sourceText;
	}

	/**
	 * Removes the specified {@linkplain Segment segment} from this output document.
	 * <p>
	 * This is equivalent to {@link #replace(Segment,CharSequence) replace}<code>(segment,null)</code>.
	 *
	 * @param segment  the segment to remove.
	 */
	public void remove(final Segment segment) {
		register(new RemoveOutputSegment(segment));
	}

	/**
	 * Removes all the segments from this output document represented by the specified source {@linkplain Segment} objects.
	 * <p>
	 * This is equivalent to the following code:<pre>
	 *  for (Iterator i=segments.iterator(); i.hasNext();)
	 *    {@link #remove(Segment) remove}((Segment)i.next());</pre>
	 *
	 * @param segments  a collection of segments to remove, represented by source {@link Segment} objects.
	 */
	public void remove(final Collection<? extends Segment> segments) {
		for (Segment segment : segments) remove(segment);
	}

	/**
	 * Inserts the specified text at the specified character position in this output document.
	 * @param pos  the character position at which to insert the text.
	 * @param text  the replacement text.
	 */
	public void insert(final int pos, final CharSequence text) {
		register(new StringOutputSegment(pos,pos,text));
	}

	/**
	 * Replaces the specified {@linkplain Segment segment} in this output document with the specified text.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>text</code> parameter is exactly equivalent to specifying an empty string,
	 * and results in the segment being completely removed from the output document.
	 *
	 * @param segment  the segment to replace.
	 * @param text  the replacement text, or <code>null</code> to remove the segment.
	 */
	public void replace(final Segment segment, final CharSequence text) {
		replace(segment.getBegin(),segment.getEnd(),text);
	}

	/**
	 * Replaces the specified segment of this output document with the specified text.
	 * <p>
	 * Specifying a <code>null</code> argument to the <code>text</code> parameter is exactly equivalent to specifying an empty string,
	 * and results in the segment being completely removed from the output document.
	 *
	 * @param begin  the character position at which to begin the replacement.
	 * @param end  the character position at which to end the replacement.
	 * @param text  the replacement text, or <code>null</code> to remove the segment.
	 */
	public void replace(final int begin, final int end, final CharSequence text) {
		register(new StringOutputSegment(begin,end,text));
	}

	/**
	 * Replaces the specified segment of this output document with the specified character.
	 *
	 * @param begin  the character position at which to begin the replacement.
	 * @param end  the character position at which to end the replacement.
	 * @param ch  the replacement character.
	 */
	public void replace(final int begin, final int end, final char ch) {
		register(new CharOutputSegment(begin,end,ch));
	}

	/**
	 * Replaces the specified {@link FormControl} in this output document.
	 * <p>
	 * The effect of this method is to {@linkplain #register(OutputSegment) register} zero or more
	 * {@linkplain OutputSegment output segments} in the output document as required to reflect
	 * previous modifications to the control's state.
	 * The state of a control includes its <a href="FormControl.html#SubmissionValue">submission value</a>,
	 * {@linkplain FormControl#setOutputStyle(FormControlOutputStyle) output style}, and whether it has been
	 * {@linkplain FormControl#setDisabled(boolean) disabled}.
	 * <p>
	 * The state of the form control should not be modified after this method is called, as there is no guarantee that
	 * subsequent changes either will or will not be reflected in the final output.
	 * A second call to this method with the same parameter is not allowed.
	 * It is therefore recommended to call this method as the last action before the output is generated.
	 * <p>
	 * Although the specifics of the number and nature of the output segments added in any particular circumstance
	 * is not defined in the specification, it can generally be assumed that only the minimum changes necessary
	 * are made to the original document.  If the state of the control has not been modified, calling this method
	 * has no effect at all.
	 *
	 * @param formControl  the form control to replace.
	 * @see #replace(FormFields)
	 */
	public void replace(final FormControl formControl) {
		formControl.replaceInOutputDocument(this);
	}

	/**
	 * {@linkplain #replace(FormControl) Replaces} all the constituent {@linkplain FormControl form controls}
	 * from the specified {@link FormFields} in this output document.
	 * <p>
	 * This is equivalent to the following code:
	 * <pre>for (Iterator i=formFields.{@link FormFields#getFormControls() getFormControls()}.iterator(); i.hasNext();)
	 *   {@link #replace(FormControl) replace}((FormControl)i.next());</pre>
	 * <p>
	 * The state of any of the form controls in the specified form fields should not be modified after this method is called,
	 * as there is no guarantee that subsequent changes either will or will not be reflected in the final output.
	 * A second call to this method with the same parameter is not allowed.
	 * It is therefore recommended to call this method as the last action before the output is generated.
	 *
	 * @param formFields  the form fields to replace.
	 * @see #replace(FormControl)
	 */
	public void replace(final FormFields formFields) {
		formFields.replaceInOutputDocument(this);
	}

	/**
	 * Replaces the specified {@link Attributes} segment in this output document with the name/value entries
	 * in the returned <code>Map</code>.
	 * The returned map initially contains entries representing the attributes from the source document,
	 * which can be modified before output.
	 * <p>
	 * The documentation of the {@link #replace(Attributes,Map)} method contains more information about the requirements
	 * of the map entries.
	 * <p>
	 * Specifying a value of <code>true</code> as an argument to the <code>convertNamesToLowerCase</code> parameter
	 * causes all original attribute names to be converted to lower case in the map.
	 * This simplifies the process of finding/updating specific attributes since map keys are case sensitive.
	 * <p>
	 * Attribute values are automatically {@linkplain CharacterReference#decode(CharSequence) decoded} before
	 * being loaded into the map.
	 * <p>
	 * This method is logically equivalent to:<br />
	 * {@link #replace(Attributes,Map) replace}<code>(attributes, attributes.</code>{@link Attributes#populateMap(Map,boolean) populateMap(new LinkedHashMap&lt;String,String&gt;(),convertNamesToLowerCase)}<code>)</code>
	 * <p>
	 * The use of <code>LinkedHashMap</code> to implement the map ensures (probably unnecessarily) that
	 * existing attributes are output in the same order as they appear in the source document, and new
	 * attributes are output in the same order as they are added.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd><pre>
	 *  Source source=new Source(htmlDocument);
	 *  Attributes bodyAttributes
	 *    =source.getNextStartTag(0,HTMLElementName.BODY).getAttributes();
	 *  OutputDocument outputDocument=new OutputDocument(source);
	 *  Map&lt;String,String&gt; attributesMap=outputDocument.replace(bodyAttributes,true);
	 *  attributesMap.put("bgcolor","green");
	 *  String htmlDocumentWithGreenBackground=outputDocument.toString();</pre></dl>
	 *
	 * @param attributes  the <code>Attributes</code> segment defining the span of the segment and initial name/value entries of the returned map.
	 * @param convertNamesToLowerCase  specifies whether all attribute names are converted to lower case in the map.
	 * @return a <code>Map</code> containing the name/value entries to be output.
	 * @see #replace(Attributes,Map)
	 */
	public Map<String,String> replace(final Attributes attributes, boolean convertNamesToLowerCase) {
		AttributesOutputSegment attributesOutputSegment=new AttributesOutputSegment(attributes,convertNamesToLowerCase);
		register(attributesOutputSegment);
		return attributesOutputSegment.getMap();
	}

	/**
	 * Replaces the specified attributes segment in this source document with the name/value entries in the specified <code>Map</code>.
	 * <p>
	 * This method might be used if the <code>Map</code> containing the new attribute values
	 * should not be preloaded with the same entries as the source attributes, or a map implementation
	 * other than <code>LinkedHashMap</code> is required.
	 * Otherwise, the {@link #replace(Attributes, boolean convertNamesToLowerCase)} method is generally more useful.
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
	 *
	 * @param attributes  the <code>Attributes</code> object defining the span of the segment to replace.
	 * @param map  the <code>Map</code> containing the name/value entries.
	 * @see #replace(Attributes, boolean convertNamesToLowerCase)
	 */
	public void replace(final Attributes attributes, final Map<String,String> map) {
		register(new AttributesOutputSegment(attributes,map));
	}

	/**
	 * Replaces the specified segment of this output document with a string of spaces of the same length.
	 * <p>
	 * This method is most commonly used to remove segments of the document without affecting the character positions of the remaining elements.
	 * <p>
	 * It is used internally to implement the functionality available through the {@link Segment#ignoreWhenParsing()} method.
	 * <p>
	 * To remove a segment from the output document completely, use the {@link #remove(Segment)} method instead.
	 *
	 * @param begin  the character position at which to begin the replacement.
	 * @param end  the character position at which to end the replacement.
	 */
	public void replaceWithSpaces(final int begin, final int end) {
		register(new BlankOutputSegment(begin,end));
	}

	/**
	 * Registers the specified {@linkplain OutputSegment output segment} in this output document.
	 * <p>
	 * Use this method if you want to use a customised {@link OutputSegment} class.
	 *
	 * @param outputSegment  the output segment to register.
	 */
	public void register(final OutputSegment outputSegment) {
		outputSegments.add(outputSegment);
	}

	/**
	 * Writes the final content of this output document to the specified <code>Writer</code>.
	 * <p>
	 * The {@link #writeTo(Writer, int begin, int end)} method allows the output of a portion of the output document.
	 * <p>
	 * If the output is required in the form of a <code>Reader</code>, use {@link CharStreamSourceUtil#getReader(CharStreamSource) CharStreamSourceUtil.getReader(this)} instead.
	 *
	 * @param writer  the destination <code>java.io.Writer</code> for the output.
	 * @throws IOException if an I/O exception occurs.
	 * @see #toString()
	 */
	public void writeTo(final Writer writer) throws IOException {
		try {
			appendTo(writer);
		} finally {
			writer.flush();
		}
	}
	
	/**
	 * Writes the specified portion of the final content of this output document to the specified <code>Writer</code>.
	 * <p>
	 * Any zero-length output segments located at <code>begin</code> or <code>end</code> are included in the output.
	 *
	 * @param writer  the destination <code>java.io.Writer</code> for the output.
	 * @param begin  the character position at which to start the output, inclusive.
	 * @param end  the character position at which to end the output, exclusive.
	 * @throws IOException if an I/O exception occurs.
	 * @see #writeTo(Writer)
	 */
	public void writeTo(final Writer writer, final int begin, final int end) throws IOException {
		try {
			appendTo(writer,begin,end);
		} finally {
			writer.flush();
		}
	}

	/**
	 * Appends the final content of this output document to the specified <code>Appendable</code> object.
	 * <p>
	 * The {@link #appendTo(Appendable, int begin, int end)} method allows the output of a portion of the output document.
	 *
	 * @param appendable  the destination <code>java.lang.Appendable</code> object for the output.
	 * @throws IOException if an I/O exception occurs.
	 * @see #toString()
	 */
	public void appendTo(final Appendable appendable) throws IOException {
		appendTo(appendable,0,sourceText.length());
	}

	/**
	 * Appends the specified portion of the final content of this output document to the specified <code>Appendable</code> object.
	 * <p>
	 * Any zero-length output segments located at <code>begin</code> or <code>end</code> are included in the output.
	 *
	 * @param appendable  the destination <code>java.lang.Appendable</code> object for the output.
	 * @param begin  the character position at which to start the output, inclusive.
	 * @param end  the character position at which to end the output, exclusive.
	 * @throws IOException if an I/O exception occurs.
	 * @see #appendTo(Appendable)
	 */
	public void appendTo(final Appendable appendable, final int begin, final int end) throws IOException {
		if (outputSegments.isEmpty()) {
			appendable.append(sourceText,begin,end);
			return;
		}
		int pos=begin;
		Collections.sort(outputSegments,OutputSegment.COMPARATOR);
		for (OutputSegment outputSegment : outputSegments) {
			if (outputSegment.getEnd()<pos) continue; // skip output segments before begin, and any that are enclosed by other output segments
			if (outputSegment.getEnd()==pos && outputSegment.getBegin()<pos) continue; // skip output segments that end at pos unless they are zero length
			if (outputSegment.getBegin()>end) break; // stop processing output segments if they are not longer in the desired output range
			if (outputSegment.getBegin()==end && outputSegment.getEnd()>end) break; // stop processing output segments if they start at end unless they are zero length
			if (outputSegment.getBegin()>pos) {
				appendable.append(sourceText,pos,outputSegment.getBegin());
			}
			if (outputSegment.getBegin()<pos && outputSegment instanceof BlankOutputSegment) {
				// Overlapping BlankOutputSegments requires special handling to ensure the correct number of blanks are inserted.
				for (final int outputSegmentEnd=outputSegment.getEnd(); pos<outputSegmentEnd; pos++) appendable.append(' ');
			} else {
				outputSegment.appendTo(appendable);
				pos=outputSegment.getEnd();
			}
		}
		if (pos<end) appendable.append(sourceText,pos,end);
	}

	// Documentation inherited from CharStreamSource
	public long getEstimatedMaximumOutputLength() {
		long estimatedMaximumOutputLength=sourceText.length();
		for (OutputSegment outputSegment : outputSegments) {
			final int outputSegmentOriginalLength=outputSegment.getEnd()-outputSegment.getBegin();
			estimatedMaximumOutputLength+=(outputSegment.getEstimatedMaximumOutputLength()-outputSegmentOriginalLength);
		}
		return estimatedMaximumOutputLength>=0L ? estimatedMaximumOutputLength : -1L;
	}

	/**
	 * Returns the final content of this output document as a <code>String</code>.
	 * @return the final content of this output document as a <code>String</code>.
	 * @see #writeTo(Writer)
	 */
	public String toString() {
		return CharStreamSourceUtil.toString(this);
	}

	/**
	 * Returns a string representation of this object useful for debugging purposes.
	 * <p>
	 * The output includes details of all the {@link #getRegisteredOutputSegments() registered output segments}.
	 *
	 * @return a string representation of this object useful for debugging purposes.
	 */
	public String getDebugInfo() {
		StringBuilder sb=new StringBuilder();
		for (OutputSegment outputSegment : getRegisteredOutputSegments()) {
			if (outputSegment instanceof BlankOutputSegment)
				sb.append("Replace with Spaces: ");
			else if (outputSegment instanceof RemoveOutputSegment)
				sb.append("Remove: ");
			else
				sb.append("Replace: ");
			if (sourceText instanceof Source) {
				Source source=(Source)sourceText;
				sb.append('(');
				source.getRowColumnVector(outputSegment.getBegin()).appendTo(sb);
				sb.append('-');
				source.getRowColumnVector(outputSegment.getEnd()).appendTo(sb);
				sb.append(')');
			} else {
				sb.append("(p").append(outputSegment.getBegin()).append("-p").append(outputSegment.getEnd()).append(')');
			}
			sb.append(' ');
			String outputFromSegment=outputSegment.toString();
			if (outputFromSegment.length()<=20) {
				sb.append(outputFromSegment);
			} else {
				sb.append(outputFromSegment.substring(0,20)).append("...");
			}
			sb.append(Config.NewLine);
		}
		return sb.toString();
	}

	/**
	 * Returns a list all of the {@linkplain #register(OutputSegment) registered} {@link OutputSegment} objects in this output document.
	 * <p>
	 * The output segments are sorted in order of their {@linkplain OutputSegment#getBegin() starting position} in the document.
	 * <p>
	 * The returned list is modifiable and any changes will affect the output generated by this <code>OutputDocument</code>.
	 *
	 * @return a list all of the {@linkplain #register(OutputSegment) registered} {@link OutputSegment} objects in this output document.
	 */
	public List<OutputSegment> getRegisteredOutputSegments() {
		Collections.sort(outputSegments,OutputSegment.COMPARATOR);
		return outputSegments;
	}
}
