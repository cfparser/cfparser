package cfml.parsing.cfscript.script;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFIdentifier;
import cfml.parsing.util.ArrayBuilder;

public class CFThreadStatement extends CFParsedAttributeStatement implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CFScriptStatement body;
	
	public CFScriptStatement getBody() {
		return body;
	}
	
	public static HashSet<String> getSupportedAttributes() {
		return supportedAttributes;
	}
	
	private static HashSet<String> supportedAttributes;
	
	static {
		supportedAttributes = new HashSet<String>();
		supportedAttributes.add("NAME");
		supportedAttributes.add("ATTRIBUTECOLLECTION");
		supportedAttributes.add("OUTPUT");
		supportedAttributes.add("ACTION");
		supportedAttributes.add("PRIORITY");
		supportedAttributes.add("DURATION");
		supportedAttributes.add("TIMEOUT");
	}
	
	public CFThreadStatement(Token _t, Map<CFIdentifier, CFExpression> _attr, CFScriptStatement _body) {
		super(_t, _attr);
		
		body = _body;
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append("thread ");
		DecompileAttributes(sb);
		if (body == null) {
			sb.append(";");
		} else {
			sb.append(body.Decompile(0));
		}
		
		return sb.toString();
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement(body);
	}
}
