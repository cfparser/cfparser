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
 * Represents a collection of {@link FormField} objects.
 * <p>
 * This class provides the main interface for the analysis and manipulation of {@linkplain FormControl form controls}.
 * A <code>FormFields</code> object is a collection of {@link FormField} objects, with each form field consisting of
 * a group of {@linkplain FormControl form controls} having the same {@linkplain FormControl#getName() name}.
 * <p>
 * The functionality provided by this class can be used to accomplish two main tasks:
 * <ol>
 *  <li style="margin-bottom: 1.5em">
 *   Modify the <a href="FormControl.html#SubmissionValue">submission values</a> of the constituent form controls
 *   for subsequent output in an {@link OutputDocument}.
 *   <p>
 *   The methods available for this purpose are:<br />
 *   {@link #getValues(String) List&lt;String&gt; getValues(String fieldName)}<br />
 *   {@link #getDataSet() Map&lt;String,String[]&gt; getDataSet()}<br />
 *   {@link #clearValues() void clearValues()}<br />
 *   {@link #setDataSet(Map) void setDataSet(Map&lt;String,String[]&gt;)}<br />
 *   {@link #setValue(String,String) boolean setValue(String fieldName, String value)}<br />
 *   {@link #addValue(String,String) boolean addValue(String fieldName, String value)}<br />
 *   <p>
 *   Although the {@link FormField} and {@link FormControl} classes provide methods for directly modifying
 *   the submission values of individual form fields and controls, it is generally recommended to use the interface provided by this
 *   (the <code>FormFields</code>) class unless there is a specific requirement for the lower level functionality.
 *   <p>
 *   The <a href="FormControl.html#DisplayCharacteristics">display characteristics</a> of individual controls,
 *   such as whether the control is {@linkplain FormControl#setDisabled(boolean) disabled}, replaced with a simple
 *   {@linkplain FormControlOutputStyle#DISPLAY_VALUE value}, or {@linkplain FormControlOutputStyle#REMOVE removed} altogether,
 *   can only be set on the individual {@link FormControl} objects.
 *   See below for information about retrieving a specific <code>FormControl</code> object from the <code>FormFields</code> object.
 *  <li>
 *   Convert data from a <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#form-data-set">form data set</a>
 *   (represented as a <a href="#FieldDataSet">field data set</a>) into a simple array format,
 *   suitable for storage in a tabular format such as a database table or <code>.CSV</code> file.
 *   <p>
 *   The methods available for this purpose are:<br />
 *   {@link #getColumnLabels() String[] getColumnLabels()}<br />
 *   {@link #getColumnValues(Map) String[] getColumnValues(Map)}<br />
 *   {@link #getColumnValues() String[] getColumnValues()}<br />
 *   <p>
 *   The {@link Util} class contains a method called {@link Util#outputCSVLine(Writer,String[]) outputCSVLine(Writer,String[])}
 *   which writes the <code>String[]</code> output of these methods to the specified <code>Writer</code> in <code>.CSV</code> format.
 *   <p>
 *   The implementation of these methods makes use of certain <a href="FormField.html#DataStructureProperties">properties</a>
 *   in the {@link FormField} class that describe the structure of the data in each field.
 *   These properties can be utilised directly in the event that a
 *   <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#form-data-set">form data set</a> is to be converted
 *   from its <a href="FormFields.html#FieldDataSet">normal format</a> into some other type of data structure.
 * </ol>
 * <p>
 * To access a specific {@link FormControl} from a <code>FormFields</code> object, use:
 * <ul style="margin-top: 0px">
 *  <li><code>formFields.</code>{@link #get(String) get(fieldName)}<code>.</code>{@link FormField#getFormControl() getFormControl()}
 *   if the control is the only one with the specified {@linkplain FormControl#getName() name}, or
 *  <li><code>formFields.</code>{@link #get(String) get(fieldName)}<code>.</code>{@link FormField#getFormControl(String) getFormControl(predefinedValue)}
 *   to retrieve the control having the speficied {@linkplain FormControl#getPredefinedValue() predefined value}
 *   if it is part of a {@linkplain FormField field} containing multiple controls.
 * </ul>
 * <p>
 * The term <i><a name="FieldDataSet">field data set</a></i> is used in this library to refer to a data structure consisting of
 * a set of names (in lower case), each mapped to one or more values.
 * Generally, this is represented by a data type of <code>java.util.Map&lt;String,String[]&gt;</code>,
 * with the keys (names) being of type <code>String</code> and the values represented by an array containing one or more items of type <code>String</code>.
 * A field data set can be used to represent the data in an HTML
 * <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#form-data-set">form data set</a>.
 * <p>
 * <code>FormFields</code> instances are obtained using the {@link #FormFields(Collection formControls)} constructor
 * or by calling the {@link Segment#getFormFields()} method.
 * <p>
 * The case sensitivity of form field names is determined by the static
 * {@link Config#CurrentCompatibilityMode}<code>.</code>{@link Config.CompatibilityMode#isFormFieldNameCaseInsensitive() FormFieldNameCaseInsensitive} property.
 * <p>
 * <b>Examples:</b>
 * <ol>
 *  <li>
 *   Write the data received from in the current <code>ServletRequest</code> to a <code>.CSV</code> file,
 *   and then display the form populated with this data:
 *   <p><pre>
 *    Source source=new Source(htmlTextOfOriginatingForm);
 *    FormFields formFields=source.getFormFields();
 *
 *    File csvOutputFile=new File("FormData.csv");
 *    boolean outputHeadings=!csvOutputFile.exists();
 *    Writer writer=new FileWriter(csvOutputFile,true);
 *    if (outputHeadings) Util.outputCSVLine(writer,formFields.getColumnLabels());
 *    Util.outputCSVLine(writer,formFields.getColumnValues(servletRequest.getParameterMap()));
 *    writer.close();
 *
 *    formFields.setDataSet(servletRequest.getParameterMap());
 *    OutputDocument outputDocument=new OutputDocument(source);
 *    outputDocument.replace(formFields);
 *    outputDocument.writeTo(servletResponse.getWriter());</pre>
 *   <p>See also the sample program FormFieldCSVOutput.<br /><br />
 *  <li>Replace the initial values of controls in the form named "MyForm" with new values:
 *   <p><pre>
 *    Source source=new Source(htmlText);
 *    Element myForm=null;
 *    List formElements=source.getAllElements(Tag.FORM);
 *    for (Iterator i=formElements.iterator(); i.hasNext();) {
 *      Element formElement=(Element)i.next();
 *      String formName=formElement.getAttributes().getValue("name");
 *      if ("MyForm".equals(formName)) {
 *        myForm=form;
 *        break;
 *      }
 *    }
 *    FormFields formFields=myForm.getFormFields();
 *    formFields.clearValues(); // clear any values that might be set in the source document
 *    formFields.addValue("Name","Humphrey Bear");
 *    formFields.addValue("MailingList","A");
 *    formFields.addValue("MailingList","B");
 *    formFields.addValue("FavouriteFare","honey");
 *    OutputDocument outputDocument=new OutputDocument(source);
 *    outputDocument.replace(formFields);
 *    String newHtmlText=outputDocument.toString();</pre>
 *   <p>See also the sample program FormFieldSetValues.<br /><br />
 *  <li>Change the display characteristics of individual controls:
 *   <p><pre>
 *    Source source=new Source(htmlText);
 *    FormFields formFields=source.getFormFields();
 *    // disable some controls:
 *    formFields.get("Password").getFormControl().setDisabled(true);
 *    FormField mailingListFormField=formFields.get("MailingList");
 *    mailingListFormField.setValue("C");
 *    mailingListFormField.getFormControl("C").setDisabled(true);
 *    mailingListFormField.getFormControl("D").setDisabled(true);
 *    // remove some controls:
 *    formFields.get("button1").getFormControl().setOutputStyle(FormControlOutputStyle.REMOVE);
 *    FormControl rhubarbFormControl=formFields.get("FavouriteFare").getFormControl("rhubarb");
 *    rhubarbFormControl.setOutputStyle(FormControlOutputStyle.REMOVE);
 *    // set some controls to display value:
 *    formFields.setValue("Address","The Lodge\nDeakin  ACT  2600\nAustralia");
 *    formFields.get("Address").getFormControl().setOutputStyle(FormControlOutputStyle.DISPLAY_VALUE);
 *    FormField favouriteSportsFormField=formFields.get("FavouriteSports");
 *    favouriteSportsFormField.setValue("BB");
 *    favouriteSportsFormField.addValue("AFL");
 *    favouriteSportsFormField.getFormControl().setOutputStyle(FormControlOutputStyle.DISPLAY_VALUE);
 *    OutputDocument outputDocument=new OutputDocument(source);
 *    outputDocument.replace(formFields); // adds all segments necessary to effect changes
 *    String newHtmlText=outputDocument.toString();</pre>
 *   <p>See also the sample program FormControlDisplayCharacteristics.<br /><br />
 * </ol>
 * @see FormField
 * @see FormControl
 */
public final class FormFields extends AbstractCollection<FormField> {
	private final LinkedHashMap<String,FormField> map=new LinkedHashMap<String,FormField>();
	private final ArrayList<FormControl> formControls=new ArrayList<FormControl>();

	/**
	 * Constructs a new <code>FormFields</code> object consisting of the specified {@linkplain FormControl form controls}.
	 * @param formControls  a collection of {@link FormControl} objects.
	 * @see Segment#getFormFields()
	 */
	public FormFields(final Collection<FormControl> formControls) {
		// Passing "this" as a parameter inside a constructor used to cause some strange problems back in java 1.0,
		// but it seems to work here and there is no explicit mention in the Java language spec about any potential problems.
		// The alternative is an ugly static FormFields constructFrom(List formControls) method.
		for (FormControl formControl : formControls) {
			if (formControl.getName()!=null && formControl.getName().length()!=0) {
				formControl.addToFormFields(this);
				this.formControls.add(formControl);
			}
		}
	}

	/**
	 * Returns the number of <code>FormField</code> objects.
	 * @return the number of <code>FormField</code> objects.
	 */
	public int getCount() {
		return map.size();
	}

	/**
	 * Returns the number of <code>FormField</code> objects.
	 * <p>
	 * This is equivalent to {@link #getCount()},
	 * and is necessary to for the implementation of the <code>java.util.Collection</code> interface.
	 *
	 * @return the number of <code>FormField</code> objects.
	 */
	public int size() {
		return getCount();
	}

	/**
	 * Returns the <code>FormField</code> with the specified {@linkplain FormField#getName() name}.
	 * <p>
	 * The case sensitivity of the <code>fieldName</code> argument is determined by the static
	 * {@link Config#CurrentCompatibilityMode}<code>.</code>{@link Config.CompatibilityMode#isFormFieldNameCaseInsensitive() FormFieldNameCaseInsensitive} property.
	 *
	 * @param fieldName  the name of the <code>FormField</code> to get.
	 * @return the <code>FormField</code> with the specified {@linkplain FormField#getName() name}, or <code>null</code> if no <code>FormField</code> with the specified name exists.
	 */
	public FormField get(String fieldName) {
		if (Config.CurrentCompatibilityMode.isFormFieldNameCaseInsensitive()) fieldName=fieldName.toLowerCase();
		return map.get(fieldName);
	}

	/**
	 * Returns an iterator over the {@link FormField} objects in the collection.
	 * <p>
	 * The order in which the form fields are iterated corresponds to the order of appearance
	 * of each form field's first {@link FormControl} in the source document.
	 * <p>
	 * If this <code>FormFields</code> object has been {@linkplain #merge(FormFields) merged} with another,
	 * the ordering is no longer defined.
	 *
	 * @return an iterator over the {@link FormField} objects in the collection.
	 */
	public Iterator<FormField> iterator() {
		return map.values().iterator();
	}

	/**
	 * Returns a list of the <a href="FormField.html#FieldSubmissionValues">field submission values</a> of all the specified constituent {@linkplain FormField form fields} with the specified {@linkplain FormField#getName() name}.
	 * <p>
	 * All objects in the returned list are of type <code>String</code>, with no <code>null</code> entries.
	 * <p>
	 * This is equivalent to {@link #get(String) get(fieldName)}<code>.</code>{@link FormField#getValues() getValues()},
	 * assuming that a field with the specified name exists in this collection.
	 *
	 * @param fieldName  the {@linkplain FormField#getName() name} of the form field.
	 * @return a list of the <a href="FormField.html#FieldSubmissionValues">field submission values</a> of all the specified constituent {@linkplain FormField form field} with the specified {@linkplain FormField#getName() name}, or <code>null</code> if no form field with this name exists.
	 * @see FormField#getValues()
	 */
	public List<String> getValues(final String fieldName) {
		final FormField formField=get(fieldName);
		return formField==null ? null : formField.getValues();
	}

	/**
	 * Returns the entire <a href="#FieldDataSet">field data set</a> represented by the {@linkplain FormField#getValues() values} of the constituent form fields.
	 * <p>
	 * The values in the map returned by this method are represented as a string array, giving the map a format consistent with the
	 * <code><a target="_blank" href="http://java.sun.com/products/servlet/2.3/javadoc/javax/servlet/ServletRequest.html#getParameterMap()">javax.servlet.ServletRequest.getParameterMap()</a></code>
	 * method.
	 * <p>
	 * Only the {@linkplain FormField#getName() names} of form fields with at least one {@linkplain FormField#getValues() value}
	 * are included in the map, meaning every <code>String[]</code> is guaranteed to have at least one entry.
	 * <p>
	 * Iterating over the map keys returns them in the order of appearance in the source document.
	 *
	 * @return the entire <a href="#FieldDataSet">field data set</a> represented by the {@linkplain FormField#getValues() values} of the constituent form fields.
	 * @see #setDataSet(Map)
	 */
	public Map<String,String[]> getDataSet() {
		final LinkedHashMap<String,String[]> map=new LinkedHashMap<String,String[]>((int)(getCount()/0.7));
		for (FormField formField : this) {
			final List<String> values=formField.getValues();
			if (values.isEmpty()) continue;
			map.put(formField.getName(),values.toArray(new String[values.size()]));
		}
		return map;
	}

	/**
	 * Clears the <a href="FormControl.html#SubmissionValue">submission values</a> of all the constituent {@linkplain #getFormControls() form controls}.
	 * @see FormControl#clearValues()
	 */
	public void clearValues() {
		for (FormControl formControl : formControls) formControl.clearValues();
	}

	/**
	 * Sets the <a href="FormControl.html#SubmissionValue">submission values</a> of all the constituent
	 * {@linkplain FormControl form controls} to match the data in the specified <a href="#FieldDataSet">field data set</a>.
	 * <p>
	 * The map keys must be <code>String</code> {@linkplain FormField#getName() field names},
	 * with each map value an array of <code>String</code> objects containing the field's new {@linkplain FormField#setValues(Collection) values}.
	 * <p>
	 * The map returned by the
	 * <code><a target="_blank" href="http://java.sun.com/products/servlet/2.3/javadoc/javax/servlet/ServletRequest.html#getParameterMap()">javax.servlet.ServletRequest.getParameterMap()</a></code>
	 * method has a suitable format for use with this method.
	 * <p>
	 * All existing values are {@linkplain #clearValues() cleared} before the values from the field data set are added.
	 * <p>
	 * Any map entries with a <code>null</code> value are ignored.
	 *
	 * @param dataSet  the <a href="#FieldDataSet">field data set</a> containing the new {@linkplain FormField#setValues(Collection) values} of the constituent form fields.
	 * @see #getDataSet()
	 */
	public void setDataSet(final Map<String,String[]> dataSet) {
		clearValues();
		if (map==null) return;
		for (Map.Entry<String,String[]> entry : dataSet.entrySet()) {
			final String fieldName=entry.getKey();
			final FormField formField=get(fieldName);
			if (formField!=null) formField.addValues(entry.getValue());
		}
	}

	/**
	 * Sets the <a href="FormField.html#FieldSubmissionValues">field submission values</a> of the constituent
	 * {@linkplain FormField form field} with the specified {@linkplain FormField#getName() name} to the single specified value.
	 * <p>
	 * This is equivalent to {@link #get(String) get(fieldName)}<code>.</code>{@link FormField#setValue(String) setValue(value)},
	 * assuming that a field with the specified name exists in this collection.
 	 * <p>
	 * The return value indicates whether the specified form field "accepted" the value.
	 * A return value of <code>false</code> implies an error condition as either no field with the specified name exists, or
	 * the specified value is not compatible with the specified field.
	 *
	 * @param fieldName  the {@linkplain FormField#getName() name} of the form field.
	 * @param value  the new <a href="FormField.html#FieldSubmissionValues">field submission value</a> of the specified field, or <code>null</code> to {@linkplain FormField#clearValues() clear} the field of all submission values.
	 * @return <code>true</code> if a field of the specified name exists in this collection and it accepts the specified value, otherwise <code>false</code>.
	 */
	public boolean setValue(final String fieldName, final String value) {
		final FormField formField=get(fieldName);
		return formField==null ? false : formField.setValue(value);
	}

	/**
	 * Adds the specified value to the <a href="FormField.html#FieldSubmissionValues">field submission values</a> of the constituent
	 * {@linkplain FormField form field} with the specified {@linkplain FormField#getName() name}.
	 * <p>
	 * This is equivalent to {@link #get(String) get(fieldName)}<code>.</code>{@link FormField#addValue(String) addValue(value)},
	 * assuming that a field with the specified name exists in this collection.
 	 * <p>
	 * The return value indicates whether the specified form field "accepted" the value.
	 * A return value of <code>false</code> implies an error condition as either no field with the specified name exists, or
	 * the specified value is not compatible with the specified field.
	 *
	 * @param fieldName  the {@linkplain FormField#getName() name} of the form field.
	 * @param value  the new <a href="FormField.html#FieldSubmissionValues">field submission value</a> to add to the specified field, must not be <code>null</code>.
	 * @return <code>true</code> if a field of the specified name exists in this collection and it accepts the specified value, otherwise <code>false</code>.
	 */
	public boolean addValue(final String fieldName, final String value) {
		final FormField formField=get(fieldName);
		return formField==null ? false : formField.addValue(value);
	}

	/**
	 * Returns a string array containing the column labels corresponding to the values from the {@link #getColumnValues(Map)} method.
	 * <p>
	 * Instead of using the {@linkplain FormField#getName() name} of each constituent form field to construct the labels,
	 * the {@linkplain FormControl#getName() name} of the first {@linkplain FormControl form control} from each form field is used.
	 * This allows the labels to be constructed using the names with the original case from the source document rather than
	 * unsing the all lower case names of the form fields.
	 * <p>
	 * See the documentation of the {@link #getColumnValues(Map)} method for more details.
	 *
	 * @return a string array containing the column labels corresponding to the values from the {@link #getColumnValues(Map)} method.
	 * @see Util#outputCSVLine(Writer,String[])
	 */
	public String[] getColumnLabels() {
		initColumns();
		final String[] columnLabels=new String[columns.length];
		for (int i=0 ; i<columns.length; i++) {
			final Column column=columns[i];
			final String fieldName=column.formField.getFirstFormControl().getName(); // use this instead of formControl.getName() so that the original case is used even if Config.CurrentCompatibilityMode.isFormFieldNameCaseInsensitive() is true.
			columnLabels[i]=column.predefinedValue!=null
				? fieldName+'.'+column.predefinedValue
				: fieldName;
		}
		return columnLabels;
	}

	/**
	 * Converts the data values in the specified <a href="#FieldDataSet">field data set</a> into a simple string array,
	 * suitable for storage in a tabular format such as a database table or <code>.CSV</code> file.
	 * <p>
	 * The conversion is performed in a way that allows the multiple values of certain fields to be stored in separate columns,
	 * by analysing the possible <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#form-data-set">form data sets</a>
	 * that can be generated from the constituent {@linkplain #getFormControls() form controls}.
	 * <p>
	 * The column labels and values are determined as follows:
	 * <p>
	 * <ul class="HalfSeparated">
	 *  <li>
	 *   For each {@linkplain FormField form field} in this collection (taken in {@linkplain #iterator() iterator} order):
	 *   <ul>
	 *    <li>
	 *     If the form field has no {@linkplain FormField#getPredefinedValues() predefined values},
	 *     such as a single {@linkplain FormControlType#TEXT text control}, then:
	 *     <ul>
	 *      <li>
	 *       Add a single column:
	 *       <table class="CompactDL">
	 *        <tr><td>{@linkplain #getColumnLabels() Label}:<td>the {@linkplain FormField#getName() name} of the form field in original case
	 *        <tr><td>Value:<td>the single value mapped to this field in the specified <a href="#FieldDataSet">field data set</a>.
	 *       </table>
	 *       In the unlikely event that this field contains more than one value, all values are included in this one column and
	 *       separated by the text defined in the static {@link Config#ColumnMultipleValueSeparator} property.
	 *     </ul>
	 *    <li>
	 *     Otherwise, if the form field does have {@linkplain FormField#getPredefinedValues() predefined values},
	 *     but does not {@linkplain FormField#allowsMultipleValues() allow multiple values}, then:
	 *     <ul>
	 *      <li>
	 *       If the form field has only one {@linkplain FormField#getPredefinedValues() predefined value},
	 *       such as a single {@linkplain FormControlType#CHECKBOX checkbox}, then:
 	 *       <ul>
 	 *        <li>
	 *         Add a single boolean column:
	 *         <table class="CompactDL">
	 *          <tr><td>{@linkplain #getColumnLabels() Label}:<td>the {@linkplain FormField#getName() name} of the form field in original case
	 *          <tr><td>Value:<td>the currently configured string representation for <i>{@linkplain Config#ColumnValueTrue true}</i>
	 *           if a value mapped to this field in the specified <a href="#FieldDataSet">field data set</a> matches the
	 *           {@linkplain FormField#getPredefinedValues() predefined value}, otherwise <i>{@linkplain Config#ColumnValueFalse false}</i>
	 *         </table>
	 *       </ul>
	 *      <li>
	 *       Otherwise, if the form field has more than one {@linkplain FormField#getPredefinedValues() predefined value},
	 *       such as a set of {@linkplain FormControlType#RADIO radio buttons}, then:
 	 *       <ul>
 	 *        <li>
	 *         Add a single column:
	 *         <table class="CompactDL">
	 *          <tr><td>{@linkplain #getColumnLabels() Label}:<td>the {@linkplain FormField#getName() name} of the form field in original case
	 *          <tr><td>Value:<td>the single value mapped to this field in the specified <a href="#FieldDataSet">field data set</a>,
	 *           which in the case of a set of radio buttons should be the {@linkplain FormControl#getPredefinedValue() predefined value}
	 *           of the {@linkplain FormControl#isChecked() checked} radio button.
	 *         </table>
	 *       </ul>
	 *     </ul>
	 *    <li>
	 *     Otherwise, if the form field has {@linkplain FormField#getPredefinedValues() predefined values}
	 *     and {@linkplain FormField#allowsMultipleValues() allows multiple values},
	 *     such as a set of {@linkplain FormControlType#CHECKBOX checkboxes}, then:
	 *     <ul>
	 *      <li>
	 *       For each {@linkplain FormField#getPredefinedValues() predefined value} in the form field:
	 *       <ul>
	 *        <li>
	 *         Add a boolean column:
	 *         <table class="CompactDL">
	 *          <tr><td>{@linkplain #getColumnLabels() Label}:<td>"<code><i>FieldName</i>.<i>PredefinedValue</i></code>",
	 *           where <code><i>FieldName</i></code> is the {@linkplain FormField#getName() name} of the form field in original case,
	 *           and <code><i>PredefinedValue</i></code> is the {@linkplain FormField#getPredefinedValues() predefined value}.
	 *          <tr><td>Value:<td>the currently configured string representation for <i>{@linkplain Config#ColumnValueTrue true}</i>
	 *           if a value mapped to this field in the specified <a href="#FieldDataSet">field data set</a> matches the 
	 *           {@linkplain FormField#getPredefinedValues() predefined value}, otherwise <i>{@linkplain Config#ColumnValueFalse false}</i>
	 *         </table>
	 *       </ul>
	 *      <li>
	 *       In addition, if the form field can also contain user values ({@link FormField#getUserValueCount()}<code>&gt;0</code>), then:
 	 *       <ul>
 	 *        <li>
	 *         Add another column:
	 *         <table class="CompactDL">
	 *          <tr><td>{@linkplain #getColumnLabels() Label}:<td>the {@linkplain FormField#getName() name} of the form field in original case
	 *          <tr><td>Value:<td>all values mapped to this field in the specified <a href="#FieldDataSet">field data set</a>
	 *          that do not match any of the {@linkplain FormField#getPredefinedValues() predefined values},
	 *          separated by the text defined in the static {@link Config#ColumnMultipleValueSeparator} property.
	 *         </table>
	 *       </ul>
	 *     </ul>
	 *   </ul>
	 * </ul>
	 * <p>
	 * The sample program FormFieldCSVOutput demonstrates the use of this method and its output.
	 *
	 * @param dataSet  a <a href="#FieldDataSet">field data set</a> containing the data to convert.
	 * @return the data values in the specified <a href="#FieldDataSet">field data set</a> in the form of a simple string array.
	 * @see Util#outputCSVLine(Writer,String[])
	 * @see #getColumnLabels()
	 * @see #getColumnValues()
	 */
	public String[] getColumnValues(final Map<String,String[]> dataSet) {
		initColumns();
		final String[] columnValues=new String[columns.length];
		if (Config.ColumnValueFalse!=null) {
			// initialise all boolean columns with false string
			for (int i=0; i<columns.length; i++)
				if (columns[i].isBoolean) columnValues[i]=Config.ColumnValueFalse;
		}
		for (Map.Entry<String,String[]> entry : dataSet.entrySet()) {
			final String fieldName=entry.getKey();
			final FormField formField=get(fieldName);
			if (formField!=null) {
				final int columnIndex=formField.columnIndex;
				for (String value : entry.getValue()) {
					for (int ci=columnIndex; ci<columns.length; ci++) {
						final Column column=columns[ci];
						if (column.formField!=formField) break;
						if (column.predefinedValue!=null) {
							if (!column.predefinedValue.equals(value)) continue;
							columnValues[ci]=Config.ColumnValueTrue;
						} else {
							if (column.isBoolean) {
								if (value!=null) columnValues[ci]=Config.ColumnValueTrue;
							} else if (columnValues[ci]==null) {
								columnValues[ci]=value;
							} else {
								columnValues[ci]=columnValues[ci]+Config.ColumnMultipleValueSeparator+value;
							}
						}
						break;
					}
				}
			}
		}
		return columnValues;
	}

	/**
	 * Converts all the {@linkplain FormField#getValues() form submission values} of the constituent form fields into a simple string array,
	 * suitable for storage in a tabular format such as a database table or <code>.CSV</code> file.
	 * <p>
	 * This is equivalent to {@link #getColumnValues(Map) getColumnValues}<code>(</code>{@link #getDataSet()}<code>)</code>.
	 *
	 * @return all the {@linkplain FormField#getValues() form submission values} of the constituent form fields in the form of a simple string array.
	 */
	public String[] getColumnValues() {
		return getColumnValues(getDataSet());
	}

	private void initColumns() {
		if (columns!=null) return;
		final ArrayList<Column> columnList=new ArrayList<Column>();
		for (FormField formField : this) {
			formField.columnIndex=columnList.size();
			if (!formField.allowsMultipleValues() || formField.getPredefinedValues().isEmpty()) {
				columnList.add(new Column(formField,formField.getPredefinedValues().size()==1,null));
			} else {
				// add a column for every predefined value
				for (String predefinedValue : formField.getPredefinedValues())
					columnList.add(new Column(formField,true,predefinedValue));
				if (formField.getUserValueCount()>0) columnList.add(new Column(formField,false,null)); // add a column for user values, must come after predefined values for algorithm in getColumnValues to work
			}
		}
		columns=columnList.toArray(new Column[columnList.size()]);
	}
	private Column[] columns=null;

	private static class Column {
		public FormField formField;
		public boolean isBoolean;
		public String predefinedValue;
		public Column(final FormField formField, final boolean isBoolean, final String predefinedValue) {
			this.formField=formField;
			this.isBoolean=isBoolean;
			this.predefinedValue=predefinedValue;
		}
	}

	/**
	 * Returns a list of all the {@linkplain FormField#getFormControls() constituent form controls} from all the {@linkplain FormField form fields} in this collection.
	 * @return a list of all the {@linkplain FormField#getFormControls() constituent form controls} from all the {@linkplain FormField form fields} in this collection.
	 */
	public List getFormControls() {
		return formControls;
	}

	/**
	 * Merges the specified <code>FormFields</code> into this <code>FormFields</code> collection.
	 * This is useful if a full collection of possible form fields is required from multiple {@linkplain Source source} documents.
	 * <p>
	 * If both collections contain a <code>FormField</code> with the same {@linkplain FormField#getName() name},
	 * the resulting <code>FormField</code> has the following properties:
	 * <ul>
	 * <li>{@link FormField#getUserValueCount() getUserValueCount()} : the maximum user value count from both form fields</li>
	 * <li>{@link FormField#allowsMultipleValues() allowsMultipleValues()} : <code>true</code> if either form field allows multiple values</li>
	 * <li>{@link FormField#getPredefinedValues() getPredefinedValues()} : the union of predefined values in both form fields</li>
	 * <li>{@link FormField#getFormControls() getFormControls()} : the union of {@linkplain FormControl form controls} from both form fields</li>
	 * </ul>
	 * <p>
	 * NOTE: Some underlying data structures may end up being shared between the two merged <code>FormFields</code> collections.
	 */
	public void merge(final FormFields formFields) {
		for (FormField formField : formFields) {
			final String fieldName=formField.getName();
			final FormField existingFormField=get(fieldName);
			if (existingFormField==null)
				map.put(formField.getName(),formField);
			else
				existingFormField.merge(formField);
		}
	}

	/**
	 * Returns a string representation of this object useful for debugging purposes.
	 * @return a string representation of this object useful for debugging purposes.
	 */
	public String getDebugInfo() {
		final StringBuilder sb=new StringBuilder();
		for (FormField formField : this) sb.append(formField);
		return sb.toString();
	}

	/**
	 * Returns a string representation of this object useful for debugging purposes.
	 * <p>
	 * This is equivalent to {@link #getDebugInfo()}.
	 *
	 * @return a string representation of this object useful for debugging purposes.
	 */
	public String toString() {
		return getDebugInfo();
	}

	void add(final FormControl formControl) {
		add(formControl,formControl.getPredefinedValue());
	}

	void add(final FormControl formControl, final String predefinedValue) {
		add(formControl,predefinedValue,formControl.name);
	}

	void addName(final FormControl formControl, final String fieldName) {
		add(formControl,null,fieldName);
	}

	void add(final FormControl formControl, final String predefinedValue, String fieldName) {
		if (Config.CurrentCompatibilityMode.isFormFieldNameCaseInsensitive()) fieldName=fieldName.toLowerCase();
		FormField formField=(FormField)map.get(fieldName);
		if (formField==null) {
			formField=new FormField(fieldName);
			map.put(formField.getName(),formField);
		}
		formField.addFormControl(formControl,predefinedValue);
	}

	void replaceInOutputDocument(final OutputDocument outputDocument) {
		for (FormControl formControl : formControls) outputDocument.replace(formControl);
	}
}
