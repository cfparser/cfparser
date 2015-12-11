package cfml.parsing.cfscript.script;

import java.util.HashSet;
import java.util.Map;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFIdentifier;
import cfml.parsing.reporting.ParseException;

public class CFLockStatement extends CFParsedAttributeStatement implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CFScriptStatement body;
	
	private static HashSet<String> supportedAttributes;
	
	static {
		supportedAttributes = new HashSet<String>();
		supportedAttributes.add("TYPE");
		supportedAttributes.add("TIMEOUT");
		supportedAttributes.add("NAME");
		supportedAttributes.add("SCOPE");
		supportedAttributes.add("THROWONTIMEOUT");
	}
	
	public CFLockStatement(org.antlr.v4.runtime.Token _t, Map<CFIdentifier, CFExpression> _attr,
			CFScriptStatement _body) {
		super(_t, _attr);
		
		validateAttributes(_t, supportedAttributes);
		
		body = _body;
	}
	
	public void validate() {
		// minimal requirement is the timeout attribute
		if (!containsAttribute("TIMEOUT"))
			throw new ParseException(token, "Lock requires the TIMEOUT attribute");
			
		if (containsAttribute("NAME") && containsAttribute("SCOPE"))
			throw new ParseException(token, "Invalid Attributes: specify either SCOPE or NAME, but not both");
	}
	
	@Override
	public String Decompile(int indent) {
		validate();
		
		StringBuilder sb = new StringBuilder();
		sb.append("lock ");
		DecompileAttributes(sb);
		sb.append(body.Decompile(0));
		
		return sb.toString();
	}
	
	public CFScriptStatement getBody() {
		return body;
	}
	
	public static HashSet<String> getSupportedAttributes() {
		return supportedAttributes;
	}
	
}
