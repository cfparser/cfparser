package cfml.parsing.cfscript.script;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFIdentifier;

public class CFAdminStatement extends CFParsedAttributeStatement implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static HashSet<String> validAttributes;
	static {
		validAttributes = new HashSet<String>();
		validAttributes.add("TYPE");
		validAttributes.add("ACTION");
		validAttributes.add("PASSWORD");
		validAttributes.add("RETURNVARIABLE");
	}
	
	public CFAdminStatement(org.antlr.v4.runtime.Token t, Map<CFIdentifier, CFExpression> _attributes) {
		super(t, _attributes);
		// validateAttributes(t, validAttributes);
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append("admin");
		DecompileAttributes(sb);
		return sb.toString();
	}
	
	public static HashSet<String> getValidAttributes() {
		return validAttributes;
	}
	
}
