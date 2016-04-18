package cfml.parsing.cfscript.script;

import java.util.HashSet;
import java.util.Map;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFIdentifier;

public class CFPropertyStatement extends CFParsedAttributeStatement implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static HashSet<String> validAttributes;
	private static boolean shorthand = false;
	private static CFExpression propertyName;
	private static CFExpression propertyType;
	
	static {
		validAttributes = new HashSet<String>();
		validAttributes.add("DEFAULT");
		validAttributes.add("TYPE");
		validAttributes.add("NAME");
		validAttributes.add("MAX");
		validAttributes.add("MIN");
		validAttributes.add("PATTERN");
	}
	
	public CFPropertyStatement(org.antlr.v4.runtime.Token t, Map<CFIdentifier, CFExpression> _attributes) {
		super(t, _attributes);
		// validateAttributes(t, validAttributes);
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append("property");
		if (!shorthand) {
			DecompileAttributes(sb);
		} else {
			sb.append(" ");
			sb.append(propertyType.Decompile(0));
			sb.append(" ");
			sb.append(propertyName.Decompile(0));
		}
		return sb.toString();
	}
	
	public static HashSet<String> getValidAttributes() {
		return validAttributes;
	}
	
	public void setIsShortHand(boolean b) {
		shorthand = b;
	}
	
	public void setPropertyName(CFExpression cfExpression) {
		propertyName = cfExpression;
	}
	
	public void setPropertyType(CFExpression cfExpression) {
		propertyType = cfExpression;
	}
}
