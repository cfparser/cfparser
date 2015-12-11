package cfml.parsing.cfscript.script;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFIdentifier;

public class CFFunctionParameter {
	
	private String name; // the name of the parameter
	private boolean required; // whether the parameter is a required one
	private String type; // the expected type of the parameter (for validation), if specified
	private CFExpression defaultExp; // the default value to give the parameter
	
	public CFFunctionParameter(CFIdentifier t, boolean _required, String _type, CFExpression _default) {
		name = t.getName();
		required = _required;
		type = _type;
		defaultExp = _default;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public boolean isDefaulted() {
		return defaultExp != null;
	}
	
	public boolean isFormallyTyped() {
		return type != null;
	}
	
	public String getType() {
		return type;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (required) {
			sb.append("required ");
		}
		sb.append(name);
		if (defaultExp != null) {
			sb.append('=');
			sb.append(defaultExp.Decompile(0));
		}
		
		return sb.toString();
	}
	
	public CFExpression getDefaultExpression() {
		return defaultExp;
	}
}
