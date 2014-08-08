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
 * Represents the <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#h-17.2.1">control type</a>
 * of a {@link FormControl}.
 * <p>
 * Use the {@link FormControl#getFormControlType()} method to determine the type of a form control.
 * <p>
 * The following table shows the relationship between the HTML 4.01 specification control type descriptions,
 * their associated {@link Element} names and attributes, and the <code>FormControlType</code> constants defined in this class:
 * <table class="bordered" style="margin: 15px" cellspacing="0">
 *  <tr>
 *   <th><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#h-17.2.1">Description</a>
 *	 <th>{@linkplain Element#getName() Element Name}
 *   <th>Distinguishing Attribute
 *   <th><code>FormControlType</code>
 *  <tr>
 *   <td rowspan="3"><a name="submit-button" target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#buttons">buttons</a> - <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#submit-button">submit button</a>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-BUTTON">BUTTON</a></code>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-type-BUTTON">type</a>="submit"</code>
 *   <td>{@link #BUTTON}
 *  <tr>
 *   <td rowspan="2"><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-INPUT">INPUT</a></code>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-type-INPUT">type</a>="submit"</code>
 *   <td>{@link #SUBMIT}
 *  <tr>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-type-INPUT">type</a>="<a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#input-control-types">image</a>"</code>
 *   <td>{@link #IMAGE}
 *  <tr>
 *   <td><a name="reset-button-control" target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#buttons">buttons</a> - <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#reset-button">reset button</a>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-BUTTON">BUTTON</a></code>,
 *       <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-INPUT">INPUT</a></code>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-type-BUTTON">type</a>="reset"</code>
 *   <td>-
 *  <tr>
 *   <td><a name="push-button-control" target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#buttons">buttons</a> - <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#push-button">push button</a>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-BUTTON">BUTTON</a></code>,
 *       <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-INPUT">INPUT</a></code>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-type-BUTTON">type</a>="button"</code>
 *   <td>-
 *  <tr>
 *   <td><a name="checkbox-control" target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#checkbox">checkboxes</a>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-INPUT">INPUT</a></code>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-type-INPUT">type</a>="checkbox"</code>
 *   <td>{@link #CHECKBOX}
 *  <tr>
 *   <td><a name="radio-button-control" target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#radio">radio buttons</a>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-INPUT">INPUT</a></code>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-type-INPUT">type</a>="radio"</code>
 *   <td>{@link #RADIO}
 *  <tr>
 *   <td rowspan="2"><a name="menu-control" target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#menu">menus</a>
 *   <td rowspan="2"><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-SELECT">SELECT</a></code>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-multiple">multiple</a></code>
 *   <td>{@link #SELECT_MULTIPLE}
 *  <tr>
 *   <td>absence of <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-multiple">multiple</a></code>
 *   <td>{@link #SELECT_SINGLE}
 *  <tr>
 *   <td rowspan="3"><a name="text-input-control" target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#text-input">text input</a>
 *   <td rowspan="2"><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-INPUT">INPUT</a></code>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-type-INPUT">type</a>="<a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#idx-text_input_control-1">text</a>"</code>
 *   <td>{@link #TEXT}
 *  <tr>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-type-INPUT">type</a>="<a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#idx-password_input_control">password</a>"</code>
 *   <td>{@link #PASSWORD}
 *  <tr>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-TEXTAREA">TEXTAREA</a></code>
 *   <td>-
 *   <td>{@link #TEXTAREA}
 *  <tr>
 *   <td><a name="file-select-control" target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#file-select">file select</a>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-INPUT">INPUT</a></code>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-type-INPUT">type</a>="file"</code>
 *   <td>{@link #FILE}
 *  <tr>
 *   <td><a name="hidden-control" target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#hidden-control">hidden controls</a>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-INPUT">INPUT</a></code>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-type-INPUT">type</a>="hidden"</code>
 *   <td>{@link #HIDDEN}
 *  <tr>
 *   <td><a name="object-control" target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#object-control">object controls</a>
 *   <td><code><a target="_blank" href="http://www.w3.org/TR/html401/struct/objects.html#edef-OBJECT">OBJECT</a></code>
 *   <td><code>-
 *   <td>-
 * </table>
 * <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#reset-button-control">Reset buttons</a> and
 * <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#push-button-control">push buttons</a>
 * have no associated <code>FormControlType</code> because they do not contribute to the
 * <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#form-data-set">form data set</a>
 * of a <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#submit-format">submitted</a> form,
 * and so have no relevance to the methods provided in the {@link FormControl} and associated classes.
 * If required they can be found and manipulated as normal {@linkplain Element elements}.
 * <p>
 * <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#object-control">Object controls</a>
 * have no associated <code>FormControlType</code> because any data they might contribute to the
 * <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#form-data-set">form data set</a>
 * is entirely dependent on the
 * <a target="_blank" href="http://www.w3.org/TR/html401/struct/objects.html#adef-classid">class</a> of object,
 * the interpretation of which is is beyond the scope of this library.
 * <p>
 * This library does not consider the
 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-OPTION">OPTION</a></code>
 * elements found within
 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-SELECT">SELECT</a></code>
 * elements to be controls themselves, despite them being referred to as such in some
 * <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-value-OPTION">parts</a>
 * of the HTML 4.01 specification.
 * Hence the absence of an <code>OPTION</code> control type.
 *
 * @see FormControl
 * @see FormField
 */
public enum FormControlType {
	/**
	 * The form control type given to a <a href="#submit-button">submit button</a> control implemented using a
	 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-BUTTON">BUTTON</a></code> element.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;button type="submit" name="FieldName" value="PredefinedValue"&gt;Send&lt;/button&gt;</code>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <code>{@link #getElementName()} = {@link HTMLElementName#BUTTON}</code><br />
	 *    <code>{@link #hasPredefinedValue()} = true</code><br />
	 *    <code>{@link #isSubmit()} = true</code><br />
	 * </dl>
	 */
	BUTTON (HTMLElementName.BUTTON,true,true),

	/**
	 * The form control type given to a <a href="#checkbox-control">checkbox</a> control.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;input type="checkbox" name="FieldName" value="PredefinedValue" /&gt;</code>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <code>{@link #getElementName()} = {@link HTMLElementName#INPUT}</code><br />
	 *    <code>{@link #hasPredefinedValue()} = true</code><br />
	 *    <code>{@link #isSubmit()} = false</code><br />
	 * </dl>
	 */
	CHECKBOX (HTMLElementName.INPUT,true,false),

	/**
	 * The form control type given to a <a href="#file-select-control">file select</a> control.
	 * <p>
	 * This library considers the <a href="FormControl.html#SubmissionValue">submission value</a> of this type of control
	 * to be consist of only the selected file name, regardless of whether the file content would normally be included in the
	 * <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#form-data-set">form data set</a>.
	 * <p>
	 * To determine manually whether the file content is included in the form data set, the
	 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-enctype">enctype</a></code>
	 * attribute of the control's associated <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-FORM">FORM</a>
	 * element can be examined.
	 * Although the exact behaviour is not defined in the HTML 4.01 specification, the convention is that the content
	 * is not included unless an <code>enctype</code> value of
	 * "<code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#didx-multipartform-data">multipart/form-data</a></code>"
	 * is specified.
	 * <p>
	 * For more information see the
	 * <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4">HTML 4.01 specification section 17.13.4 - Form content types</a>.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;input type="file" name="FieldName" value="DefaultFileName" /&gt;</code>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <code>{@link #getElementName()} = {@link HTMLElementName#INPUT}</code><br />
	 *    <code>{@link #hasPredefinedValue()} = false</code><br />
	 *    <code>{@link #isSubmit()} = false</code><br />
	 * </dl>
	 */
	FILE (HTMLElementName.INPUT,false,false),

	/**
	 * The form control type given to a <a href="#hidden-control">hidden</a> control.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;input type="hidden" name="FieldName" value="DefaultValue" /&gt;</code>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <code>{@link #getElementName()} = {@link HTMLElementName#INPUT}</code><br />
	 *    <code>{@link #hasPredefinedValue()} = false</code><br />
	 *    <code>{@link #isSubmit()} = false</code><br />
	 * </dl>
	 * Note that {@link #hasPredefinedValue()} returns <code>false</code> for this control type
	 * because the value of hidden fields is usually set via server or client side scripting.
	 */
	HIDDEN (HTMLElementName.INPUT,false,false),

	/**
	 * The form control type given to a <a href="#submit-button">submit button</a> control implemented using an
	 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-INPUT">INPUT</a></code> element with attribute
	 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-type-INPUT">type</a>="<a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#input-control-types">image</a>"</code>.
	 * <p>
	 * See the description under the heading "image" in the
	 * <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#h-17.4.1">HTML 4.01 specification section 17.4.1 - Form control types created with INPUT</a>.
	 * <p>
	 * When a {@linkplain FormControl form control} of type <code>IMAGE</code> is present in the form used to
	 * {@linkplain FormFields#FormFields(Collection) construct} a {@link FormFields} instance, three separate
	 * {@link FormField} objects are created for the one control.
	 * One has the {@linkplain FormField#getName() name} specified in the
	 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-name-INPUT">name</a></code>
	 * attribute of the <code>INPUT</code> element, and the other two have this name with the suffixes
	 * "<code>.x</code>" and "<code>.y</code>" appended to them to represent the additional
	 * <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#idx-coordinates">click coordinates</a>
	 * submitted by this control when activated using a pointing device.
	 * <p>
	 * This type of control is also mentioned in the
	 * <a target="_blank" href="http://www.w3.org/TR/html401/struct/objects.html#h-13.6.2">HTML 4.01 specification section 13.6.2 - Server-side image maps</a>.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;input type="image" name="FieldName" src="ImageURL" value="PredefinedValue" /&gt;</code>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <code>{@link #getElementName()} = {@link HTMLElementName#INPUT}</code><br />
	 *    <code>{@link #hasPredefinedValue()} = true</code><br />
	 *    <code>{@link #isSubmit()} = true</code><br />
	 * </dl>
	 */
	IMAGE (HTMLElementName.INPUT,true,true),

	/**
	 * The form control type given to a <a href="#text-input-control">text input</a> control implemented using an
	 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-INPUT">INPUT</a></code> element with attribute
	 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-type-INPUT">type</a>="<a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#idx-password_input_control">password</a>"</code>.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;input type="password" name="FieldName" value="DefaultValue" /&gt;</code>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <code>{@link #getElementName()} = {@link HTMLElementName#INPUT}</code><br />
	 *    <code>{@link #hasPredefinedValue()} = false</code><br />
	 *    <code>{@link #isSubmit()} = false</code><br />
	 * </dl>
	 */
	PASSWORD (HTMLElementName.INPUT,false,false),

	/**
	 * The form control type given to a <a href="#radio-button-control">radio button</a> control.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;input type="radio" name="FieldName" value="PredefinedValue" /&gt;</code>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <code>{@link #getElementName()} = {@link HTMLElementName#INPUT}</code><br />
	 *    <code>{@link #hasPredefinedValue()} = true</code><br />
	 *    <code>{@link #isSubmit()} = false</code><br />
	 * </dl>
	 */
	RADIO (HTMLElementName.INPUT,true,false),

	/**
	 * The form control type given to a <a href="#menu-control">menu</a> control implemented using a
	 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-SELECT">SELECT</a></code> element containing
	 * the attribute "<code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-multiple">multiple</a></code>".
	 * <p>
	 * <code>SELECT</code> elements that do not contain the attribute "<code>multiple</code>" are represented by the
	 * {@link #SELECT_SINGLE} form control type.
	 * <p>
	 * This is the only control type that can have multiple
	 * <a href="FormControl.html#SubmissionValue">submission values</a> within the one control.
	 * Contrast this with {@link #CHECKBOX} controls, which require multiple separate controls with the same
	 * {@linkplain FormControl#getName() name} in order to contribute multiple submission values.
	 * <p>
	 * The individual {@link HTMLElementName#OPTION OPTION} elements contained within a {@linkplain FormControl form control} of this type can be
	 * obtained using the {@link FormControl#getOptionElementIterator()} method.
	 * <p>
	 * The most efficient way to test whether a form control type is either <code>SELECT_MULTIPLE</code> or <code>SELECT_SINGLE</code>
	 * is to test for {@link #getElementName()}<code>==</code>{@link HTMLElementName#SELECT}.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *   <dd>
	 *    <code>
	 *     &lt;select name="FieldName" multiple&gt;<br />
	 *     &nbsp; &lt;option value="PredefinedValue1" selected&gt;Display Text1&lt;/option&gt;<br />
	 *     &nbsp; &lt;option value="PredefinedValue2"&gt;Display Text2&lt;/option&gt;<br />
	 *     &lt;/select&gt;
	 *    </code>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <code>{@link #getElementName()} = {@link HTMLElementName#SELECT}</code><br />
	 *    <code>{@link #hasPredefinedValue()} = true</code><br />
	 *    <code>{@link #isSubmit()} = false</code><br />
	 * </dl>
	 */
	SELECT_MULTIPLE (HTMLElementName.SELECT,true,false),

	/**
	 * The form control type given to a <a href="#menu-control">menu</a> control implemented using a
	 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-SELECT">SELECT</a></code> element that does
	 * <b>not</b> contain the attribute "<code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-multiple">multiple</a></code>".
	 * <p>
	 * <code>SELECT</code> elements that do contain the attribute "<code>multiple</code>" are represented by the
	 * {@link #SELECT_MULTIPLE} form control type.
	 * <p>
	 * The individual {@link HTMLElementName#OPTION OPTION} elements contained within a {@linkplain FormControl form control} of this type can be
	 * obtained using the {@link FormControl#getOptionElementIterator()} method.
	 * <p>
	 * The most efficient way to test whether a form control type is either <code>SELECT_MULTIPLE</code> or <code>SELECT_SINGLE</code>
	 * is to test for {@link #getElementName()}<code>==</code>{@link HTMLElementName#SELECT}.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *   <dd>
	 *    <code>
	 *     &lt;select name="FieldName"&gt;<br />
	 *     &nbsp; &lt;option value="PredefinedValue1" selected&gt;Display Text1&lt;/option&gt;<br />
	 *     &nbsp; &lt;option value="PredefinedValue2"&gt;Display Text2&lt;/option&gt;<br />
	 *     &lt;/select&gt;
	 *    </code>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <code>{@link #getElementName()} = {@link HTMLElementName#SELECT}</code><br />
	 *    <code>{@link #hasPredefinedValue()} = true</code><br />
	 *    <code>{@link #isSubmit()} = false</code><br />
	 * </dl>
	 */
	SELECT_SINGLE (HTMLElementName.SELECT,true,false),

	/**
	 * The form control type given to a <a href="#submit-button">submit button</a> control implemented using an
	 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-INPUT">INPUT</a></code> element with attribute
	 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-type-INPUT">type</a>="submit"</code>.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;input type="submit" name="FieldName" value="PredefinedValue" /&gt;</code>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <code>{@link #getElementName()} = {@link HTMLElementName#INPUT}</code><br />
	 *    <code>{@link #hasPredefinedValue()} = true</code><br />
	 *    <code>{@link #isSubmit()} = true</code><br />
	 * </dl>
	 */
	SUBMIT (HTMLElementName.INPUT,true,true),

	/**
	 * The form control type given to a <a href="#text-input-control">text input</a> control implemented using an
	 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-INPUT">INPUT</a></code> element with attribute
	 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#adef-type-INPUT">type</a>="text"</code>.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;input type="text" name="FieldName" value="DefaultValue" /&gt;</code>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <code>{@link #getElementName()} = {@link HTMLElementName#INPUT}</code><br />
	 *    <code>{@link #hasPredefinedValue()} = false</code><br />
	 *    <code>{@link #isSubmit()} = false</code><br />
	 * </dl>
	 */
	TEXT (HTMLElementName.INPUT,false,false),

	/**
	 * The form control type given to a <a href="#text-input-control">text input</a> control implemented using a
	 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-TEXTAREA">TEXTAREA</a></code> element.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *   <dd><code>&lt;textarea name="FieldName"&gt;Default Value&lt;/textarea&gt;</code>
	 *  <dt>Properties:</dt>
	 *   <dd>
	 *    <code>{@link #getElementName()} = {@link HTMLElementName#TEXTAREA}</code><br />
	 *    <code>{@link #hasPredefinedValue()} = false</code><br />
	 *    <code>{@link #isSubmit()} = false</code><br />
	 * </dl>
	 */
	TEXTAREA (HTMLElementName.TEXTAREA,false,false);

	private String elementName;
	private boolean hasPredefinedValue;
	private boolean submit;

	private static final HashMap<String,FormControlType> INPUT_ELEMENT_TYPE_MAP=new HashMap<String,FormControlType>(11,1.0F); // 8 input element types in total
	private static final HashSet<String> NON_FORM_CONTROL_TYPE_ATTRIBUTE_SET=new HashSet<String>(3,1.0F); // 2 non form control input element types in total

	static {
		// Map each INPUT element "type" attribute value to a FormControlType:
		INPUT_ELEMENT_TYPE_MAP.put("checkbox",CHECKBOX);
		INPUT_ELEMENT_TYPE_MAP.put("file",FILE);
		INPUT_ELEMENT_TYPE_MAP.put("hidden",HIDDEN);
		INPUT_ELEMENT_TYPE_MAP.put("image",IMAGE);
		INPUT_ELEMENT_TYPE_MAP.put("password",PASSWORD);
		INPUT_ELEMENT_TYPE_MAP.put("radio",RADIO);
		INPUT_ELEMENT_TYPE_MAP.put("submit",SUBMIT);
		INPUT_ELEMENT_TYPE_MAP.put("text",TEXT);
		// The following INPUT element "type" attributes do not produce a form control:
		NON_FORM_CONTROL_TYPE_ATTRIBUTE_SET.add("button");
		NON_FORM_CONTROL_TYPE_ATTRIBUTE_SET.add("reset");
	}

	private FormControlType(final String elementName, final boolean hasPredefinedValue, final boolean submit) {
		this.elementName=elementName;
		this.hasPredefinedValue=hasPredefinedValue;
		this.submit=submit;
	}

	/**
	 * Returns the {@linkplain Element#getName() name} of the {@link Element} that constitues this form control type.
	 * @return the {@linkplain Element#getName() name} of the {@link Element} that constitues this form control type.
	 */
	public String getElementName() {
		return elementName;
	}

	/**
	 * Indicates whether any <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#current-value">value</a>
	 * submitted by this type of control is predefined in the HTML and typically not modified by the user or server/client scripts.
	 * <p>
	 * The word "typically" is used because the use of client side scripts can cause
	 * <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#h-17.2.1">control types</a>
	 * which normally have predefined values to be set by the user, which is a condition which is beyond
	 * the scope of this library to test for.
	 * <p>
	 * The predefined value is defined by the control's <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#initial-value">initial value</a>.
	 * <p>
	 * A return value of <code>true</code> signifies that a form control of this type is a
	 * <a href="FormControl.html#PredefinedValueControl">predefined value control</a>.
	 * <p>
	 * A return value of <code>false</code> signifies that a form control of this type is a
	 * <a href="FormControl.html#UserValueControl">user value control</a>.
	 * <p>
	 * Note that the {@link #HIDDEN} type returns <code>false</code> for this method because the value of hidden fields is usually set via server or client side scripting.
	 *
	 * @return <code>true</code> if any <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#current-value">value</a> submitted by this type of control is predefined in the HTML and typically not modified by the user or server/client scripts, otherwise <code>false</code>.
	 */
	public boolean hasPredefinedValue() {
		return hasPredefinedValue;
	}

	/**
	 * Indicates whether this control type causes the form to be <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#submit-format">submitted</a>.
	 * <p>
	 * Returns <code>true</code> only for the {@link #SUBMIT}, {@link #BUTTON}, and {@link #IMAGE} instances.
	 *
	 * @return <code>true</code> if this control type causes the form to be <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#submit-format">submitted</a>, otherwise <code>false</code>.
	 */
	public boolean isSubmit() {
		return submit;
	}

	static FormControlType getFromInputElementType(final String typeAttributeValue) {
		return INPUT_ELEMENT_TYPE_MAP.get(typeAttributeValue.toLowerCase());
	}

	static boolean isNonFormControl(final String typeAttributeValue) {
		return NON_FORM_CONTROL_TYPE_ATTRIBUTE_SET.contains(typeAttributeValue.toLowerCase());
	}
}
