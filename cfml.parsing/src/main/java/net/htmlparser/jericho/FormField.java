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
 * Represents a <em>field</em> in an HTML <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html">form</a>,
 * a <em>field</em> being defined as the group of all {@linkplain FormControl form controls}
 * having the same {@linkplain FormControl#getName() name}.
 * <p>
 * The {@link #getFormControls()} method can be used to obtain the collection of this field's constituent
 * {@link FormControl} objects.
 * <p>
 * The {@link FormFields} class, which represents a collection of <code>FormField</code> objects, provides the highest level
 * interface for dealing with form fields and controls.  For the most common tasks it can be used directly without
 * the need to work with its constituent <code>FormField</code> or {@link FormControl} objects.
 * <p>
 * The <code>FormField</code> class serves two main purposes:
 * <ol>
 *  <li style="margin-bottom: 1.5em">
 *   Provide methods for the modification and retrieval of form control <a href="FormControl.html#SubmissionValue">submission values</a>
 *   while ensuring that the states of all the field's constituent form controls remain consistent with each other.
 *   <p>
 *   The methods available for this purpose are:<br />
 *   {@link #getValues() List getValues()}<br />
 *   {@link #clearValues() void clearValues()}<br />
 *   {@link #setValues(Collection) void setValues(Collection)}<br />
 *   {@link #setValue(String) boolean setValue(String)}<br />
 *   {@link #addValue(String) boolean addValue(String)}<br />
 *   <p>
 *   Although the {@link FormControl} class provides methods for directly modifying the submission values
 *   of individual form controls, it is generally recommended to use the interface provided by the {@link FormFields} class
 *   unless there is a specific requirement for the lower level functionality.
 *   The {@link FormFields} class contains convenience methods providing most of the functionality of the above methods,
 *   as well as some higher level functionality such as the ability to set the form
 *   <a href="#SubmissionValue">submission values</a> as a complete <a href="FormFields.html#FieldDataSet">field data set</a>
 *   using the {@link FormFields#setDataSet(Map)} method.
 *  <li><a name="DataStructureProperties"></a>
 *   Provide a means of determining the data structure of the field, allowing a server receiving a
 *   <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#submit-format">submitted</a>
 *   <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#form-data-set">form data set</a>
 *   to interpret and store the data in an appropriate way.
 *   <p>
 *   The properties available for this purpose are:<br />
 *   {@link #allowsMultipleValues() boolean allowsMultipleValues()}<br />
 *   {@link #getUserValueCount() int getUserValueCount()}<br />
 *   {@link #getPredefinedValues() Collection getPredefinedValues()}<br />
 *   <p>
 *   The {@link FormFields#getColumnLabels()} and {@link FormFields#getColumnValues(Map)} methods utilise these properties
 *   to convert data from a <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#form-data-set">form data set</a>
 *   (represented as a <a href="#FieldDataSet">field data set</a>) into a simple array format,
 *   suitable for storage in a tabular format such as a database table or <code>.CSV</code> file.
 *   <p>
 *   The properties need only be utilised directly in the event that a
 *   <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#form-data-set">form data set</a> is to be converted
 *   from its <a href="FormFields.html#FieldDataSet">normal format</a> into some other type of data structure.
 * </ol>
 * A form field which allows user values normally consists of a single
 * <a href="FormControl.html#UserValueControl">user value control</a>,
 * such as a {@link FormControlType#TEXT TEXT} control.
 * <p>
 * When a form field consists of more than one control, these controls are normally all
 * <a href="FormControl.html#PredefinedValueControl">predefined value controls</a> of the same
 * {@linkplain FormControlType type}, such as {@link FormControlType#CHECKBOX CHECKBOX} controls.
 * <p>
 * Form fields consisting of more than one control do not necessarily return {@linkplain #allowsMultipleValues() multiple values}.
 * A form field consisting of {@link FormControlType#CHECKBOX CHECKBOX} controls can return multiple values, whereas
 * a form field consisting of {@link FormControlType#CHECKBOX RADIO} controls returns at most one value.
 * <p>
 * The HTML author can disregard convention and mix all types of controls with the same name in the same form,
 * or include multiple <a href="FormControl.html#UserValueControl">user value controls</a> of the same name.
 * The evidence that such an unusual combination is present is when {@link #getUserValueCount()}<code>&gt;1</code>.
 * <p>
 * <code>FormField</code> instances are created automatically with the creation of a {@link FormFields} collection.
 * <p>
 * The case sensitivity of form field names is determined by the static
 * {@link Config#CurrentCompatibilityMode}<code>.</code>{@link Config.CompatibilityMode#isFormFieldNameCaseInsensitive() FormFieldNameCaseInsensitive} property.
 *
 * @see FormFields
 * @see FormControl
 * @see FormControlType
 */
public final class FormField {
	private final String name;
	private int userValueCount=0;
	private boolean allowsMultipleValues=false;
	private LinkedHashSet<String> predefinedValues=null; // String objects, null if none
	private final LinkedHashSet<FormControl> formControls=new LinkedHashSet<FormControl>();
	private transient FormControl firstFormControl=null; // this field is simply a cache for the getFirstFormControl() method
	int columnIndex; // see FormFields.initColumns()

	/** Constructor called from FormFields class. */
	FormField(final String name) {
		this.name=name;
	}

	/**
	 * Returns the <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#control-name">control name</a> shared by all of this field's constituent {@linkplain FormControl controls}.
	 * <p>
	 * If the static {@link Config#CurrentCompatibilityMode}<code>.</code>{@link Config.CompatibilityMode#isFormFieldNameCaseInsensitive() isFormFieldNameCaseInsensitive()}
	 * property is set to <code>true</code>, the grouping of the controls by name is case insensitive
	 * and this method always returns the name in lower case.
	 * <p>
	 * Since a form field is simply a group of controls with the same name, the terms <i>control name</i> and
	 * <i>field name</i> are for the most part synonymous, with only a possible difference in case differentiating them.
	 *
	 * @return the <a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#control-name">control name</a> shared by all of this field's constituent {@linkplain FormControl controls}.
	 * @see FormControl#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a collection of all the constituent {@linkplain FormControl form controls} in this field.
	 * <p>
	 * An iterator over this collection returns the controls in the order of appearance in the source.
	 *
	 * @return a collection of all the constituent {@linkplain FormControl form controls} in this field.
	 * @see #getFormControl()
	 * @see #getFormControl(String predefinedValue)
	 */
	public Collection<FormControl> getFormControls() {
		return formControls;
	}

	/**
	 * Returns the constituent {@link FormControl} with the specified {@linkplain FormControl#getPredefinedValue() predefined value}.
	 * <p>
	 * Specifying a predefined value of <code>null</code> returns the first control without a predefined value.
	 *
	 * @param predefinedValue  the predefined value of the control to be returned, or <code>null</code> to return the first control without a predefined value.
	 * @return the constituent {@link FormControl} with the specified {@linkplain FormControl#getPredefinedValue() predefined value}, or <code>null</code> if none exists.
	 * @see #getFormControl()
	 * @see #getFormControls()
	 */
	public FormControl getFormControl(final String predefinedValue) {
		if (predefinedValue==null) {
			for (FormControl formControl : formControls) {
				if (!formControl.getFormControlType().hasPredefinedValue()) return formControl;
				if (formControl.getFormControlType().getElementName()!=HTMLElementName.SELECT && formControl.getPredefinedValue()==null) return formControl;
			}
		} else {
			for (FormControl formControl : formControls) {
				if (formControl.getFormControlType().getElementName()==HTMLElementName.SELECT) {
					if (formControl.getPredefinedValues().contains(predefinedValue)) return formControl;
				} else {
					if (predefinedValue.equals(formControl.getPredefinedValue())) return formControl;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the first {@link FormControl} from this field.
	 * @return the first {@link FormControl} from this field, guaranteed not <code>null</code>.
	 * @see #getFormControl(String predefinedValue)
	 * @see #getFormControls()
	 */
	public FormControl getFormControl() {
		return formControls.iterator().next();
	}

	/**
	 * Indicates whether the field allows multiple values.
	 * <p>
	 * Returns <code>false</code> in any one of the following circumstances:
	 * <ul>
	 *  <li>The field consists of only one control (unless it is a
	 *   {@linkplain FormControlType#SELECT_MULTIPLE multiple select} with more than one option)
	 *  <li>The field consists entirely of {@linkplain FormControlType#RADIO radio buttons}
	 *  <li>The field consists entirely of {@linkplain FormControlType#isSubmit() submit} buttons
	 * </ul>
	 * If none of these three conditions are met, the method returns <code>true</code>.
	 *
	 * @return <code>true</code> if the field allows multiple values, otherwise <code>false</code>.
	 */
	public boolean allowsMultipleValues() {
		return allowsMultipleValues;
	}

	/**
	 * Returns the number of constituent <a href="FormControl.html#UserValueControl">user value controls</a> in this field.
	 * This should in most cases be either <code>0</code> or <code>1</code>.
	 * <p>
	 * A value of <code>0</code> indicates the field values consist only of
	 * {@linkplain #getPredefinedValues() predefined values}, which is the case when the field consists only of
	 * <a href="FormControl.html#PredefinedValueControl">predefined value controls</a>.
	 * <p>
	 * A value of <code>1</code> indicates the field values consist of at most one value set by the user.
	 * It is still possible in this case to receive multiple values in the unlikely event that the HTML author mixed
	 * controls of different types with the same name, but any other values would consist only of
	 * {@linkplain #getPredefinedValues() predefined values}.
	 * <p>
	 * A value greater than <code>1</code> indicates that the HTML author has included more than one
	 * <a href="FormControl.html#UserValueControl">user value control</a> with the same name.
	 * This would nearly always indicate an unintentional error in the HTML source document,
	 * in which case your application can either log a warning that a poorly designed form has been encountered,
	 * or take special action to try to interpret the multiple user values that might be submitted.
	 *
	 * @return the number of constituent <a href="FormControl.html#UserValueControl">user value controls</a> in this field.
	 */
	public int getUserValueCount() {
		return userValueCount;
	}

	/**
	 * Returns a collection of the {@linkplain FormControl#getPredefinedValue() predefined values} of all constituent {@linkplain FormControl controls} in this field.
	 * <p>
	 * All objects in the returned collection are of type <code>String</code>, with no <code>null</code> entries.
	 * <p>
	 * An interator over this collection returns the values in the order of appearance in the source document.
	 *
	 * @return a collection of the {@linkplain FormControl#getPredefinedValue() predefined values} of all constituent {@linkplain FormControl controls} in this field, or <code>null</code> if none.
	 * @see FormControl#getPredefinedValues()
	 */
	public Collection<String> getPredefinedValues() {
		if (predefinedValues==null) return Collections.emptySet();
		return predefinedValues;
	}

	/**
	 * Returns a list of the <a href="#FieldSubmissionValues">field submission values</a> in order of appearance.
	 * <p>
	 * The term <i><a name="FieldSubmissionValues">field submission values</a></i> is used in this library to refer to the aggregate of all the
	 * <a href="FormControl.html#SubmissionValue">submission values</a> of a field's constituent {@linkplain #getFormControls() form controls}.
	 * <p>
	 * All objects in the returned list are of type <code>String</code>, with no <code>null</code> entries.
	 * <p>
	 * The list may contain duplicates if the this field has multiple controls with the same value.
	 *
	 * @return a list of the <a href="#FieldSubmissionValues">field submission values</a> in order of appearance, guaranteed not <code>null</code>.
	 */
	public List<String> getValues() {
		final List<String> values=new ArrayList<String>();
		for (FormControl formControl : formControls) formControl.addValuesTo(values);
		return values;
	}

	/**
	 * Clears the <a href="FormControl.html#SubmissionValue">submission values</a> of all the constituent {@linkplain #getFormControls() form controls} in this field.
	 * @see FormControl#clearValues()
	 */
	public void clearValues() {
		for (FormControl formControl : formControls) formControl.clearValues();
	}

	/**
	 * Sets the <a href="#FieldSubmissionValues">field submission values</a> of this field to the specified values.
	 * <p>
	 * This is equivalent to calling {@link #clearValues()} followed by {@link #addValue(String) addValue(value)} for each
	 * value in the specified collection.
	 * <p>
	 * The specified collection must not contain any <code>null</code> values.
	 *
	 * @param values  the new <a href="#FieldSubmissionValues">field submission values</a> of this field.
	 * @see #addValue(String value)
	 */
	public void setValues(final Collection<String> values) {
		clearValues();
		addValues(values);
	}

	/**
	 * Sets the <a href="#FieldSubmissionValues">field submission values</a> of this field to the single specified value.
	 * <p>
	 * This is equivalent to calling {@link #clearValues()} followed by {@link #addValue(String) addValue(value)}.
	 * <p>
	 * The return value indicates whether any of the constituent form controls "accepted" the value.
	 * A return value of <code>false</code> implies an error condition as the specified value is not compatible with this field.
	 * <p>
	 * Specifying a <code>null</code> value is equivalent to calling {@link #clearValues()} alone, and always returns <code>true</code>.
	 * <p>
	 * See the {@link #addValue(String value)} method for more information.
	 *
	 * @param value  the new <a href="#FieldSubmissionValues">field submission value</a> of this field, or <code>null</code> to {@linkplain #clearValues() clear} the field of all submission values.
	 * @return <code>true</code> if one of the constituent {@linkplain #getFormControls() form controls} accepts the value, otherwise <code>false</code>.
	 * @see FormFields#setValue(String fieldName, String value)
	 */
	public boolean setValue(final String value) {
		clearValues();
		return value!=null ? addValue(value) : true;
	}

	/**
	 * Adds the specified value to the <a href="#FieldSubmissionValues">field submission values</a> of this field.
	 * <p>
	 * This is achieved internally by attempting to {@linkplain FormControl#addValue(String) add the value} to every constituent 
	 * {@linkplain #getFormControls() form control} until one "accepts" it.
	 * <p>
	 * The return value indicates whether any of the constituent form controls accepted the value.
	 * A return value of <code>false</code> implies an error condition as the specified value is not compatible with this field.
	 * <p>
	 * In the unusual case that this field consists of multiple form controls, but not all of them are
	 * <a href="FormControl.html#PredefinedValueControl">predefined value controls</a>, priority is given to the predefined value controls
	 * before attempting to add the value to the <a href="FormControl.html#UserValueControl">user value controls</a>.
	 *
	 * @param value  the new <a href="#FieldSubmissionValues">field submission value</a> to add to this field, must not be <code>null</code>.
	 * @return <code>true</code> if one of the constituent {@linkplain #getFormControls() form controls} accepts the value, otherwise <code>false</code>.
	 */
	public boolean addValue(final String value) {
		if (value==null) throw new IllegalArgumentException("value argument must not be null");
		if (formControls.size()==1) return getFirstFormControl().addValue(value);
		List<FormControl> userValueControls=null;
		for (FormControl formControl : formControls) {
			if (!formControl.getFormControlType().hasPredefinedValue()) {
				// A user value control has been found, but is not the only control with this name.
				// This shouldn't normally happen in a well designed form, but we will save the user value control
				// for later and give all predefined value controls first opportunity to take the value.
				if (userValueControls==null) userValueControls=new LinkedList<FormControl>();
				userValueControls.add(formControl);
				continue;
			}
			if (formControl.addValue(value)) return true; // return value of true from formControl.addValue(value) means the value was taken by the control
		}
		if (userValueControls==null) return false;
		for (FormControl userFormControl : userValueControls) {
			if (userFormControl.addValue(value)) return true;
		}
		return false;
	}

	/**
	 * Returns a string representation of this object useful for debugging purposes.
	 * @return a string representation of this object useful for debugging purposes.
	 */
	public String getDebugInfo() {
		final StringBuilder sb=new StringBuilder();
		sb.append("Field: ").append(name).append(", UserValueCount=").append(userValueCount).append(", AllowsMultipleValues=").append(allowsMultipleValues);
		if (predefinedValues!=null) {
			for (String predefinedValue : predefinedValues) sb.append(Config.NewLine).append("PredefinedValue: ").append(predefinedValue);
		}
		for (FormControl formControl : formControls) sb.append(Config.NewLine).append("FormControl: ").append(formControl.getDebugInfo());
		sb.append(Config.NewLine).append(Config.NewLine);
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

	void addValues(final Collection<String> values) {
		if (values!=null) for (String value : values) addValue(value);
	}

	void addValues(final String[] values) {
		if (values!=null) for (String value : values) addValue(value);
	}

	void addFormControl(final FormControl formControl, final String predefinedValue) {
		// predefinedValue==null if we are adding a user value
		if (predefinedValue==null) {
			userValueCount++;
		} else {
			if (predefinedValues==null) predefinedValues=new LinkedHashSet<String>();
			predefinedValues.add(predefinedValue);
		}
		formControls.add(formControl);
		allowsMultipleValues=calculateAllowsMultipleValues(formControl);
	}

	private boolean calculateAllowsMultipleValues(final FormControl newFormControl) {
		// false if only one control (unless it is a multiple select with more than one option),
		// or all of the controls are radio buttons, or all of the controls are submit buttons
		if (allowsMultipleValues || userValueCount>1) return true;
		if (userValueCount==1) return predefinedValues!=null;
		// at this stage we know userValueCount==0  && predefinedValues.size()>=1
		if (predefinedValues.size()==1) return false;
		final FormControlType newFormControlType=newFormControl.getFormControlType();
		if (formControls.size()==1) return newFormControlType==FormControlType.SELECT_MULTIPLE;
		// at this stage we know there are multiple predefined values in multiple controls.
		// if all of the controls are radio buttons or all are submit buttons, allowsMultipleValues is false, otherwise true.
		// checking only the first control and the new control is equivalent to checking them all because if they weren't all
		// the same allowsMultipleValues would already be true.
		final FormControlType firstFormControlType=getFirstFormControl().getFormControlType();
		if (newFormControlType==FormControlType.RADIO && firstFormControlType==FormControlType.RADIO) return false;
		if (newFormControlType.isSubmit() && firstFormControlType.isSubmit()) return false;
		return true;
	}

	FormControl getFirstFormControl() {
		// formControls must be ordered collection for this method to work.
		// It has to return the first FormControl entered into the collection
		// for the algorithm in calculateAllowsMultipleValues() to work.
		if (firstFormControl==null) firstFormControl=formControls.iterator().next();
		return firstFormControl;
	}

	/** only called from FormFields class */
	void merge(final FormField formField) {
		if (formField.userValueCount>userValueCount) userValueCount=formField.userValueCount;
		allowsMultipleValues=allowsMultipleValues || formField.allowsMultipleValues;
		if (predefinedValues==null) {
			predefinedValues=formField.predefinedValues;
		} else if (formField.predefinedValues!=null) {
			for (String predefinedValue : predefinedValues) predefinedValues.add(predefinedValue);
		}
		for (FormControl formControl : formField.getFormControls()) formControls.add(formControl);
	}
}

