package cfml.parsing.cfscript.script;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFIdentifier;

public class CFTransactionStatement extends CFParsedAttributeStatement implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CFScriptStatement body;
	
	private static HashSet<String> supportedAttributes;
	
	static {
		supportedAttributes = new HashSet<String>();
		supportedAttributes.add("ACTION");
		supportedAttributes.add("SAVEPOINT");
		supportedAttributes.add("ISOLATION");
	}
	
	public CFScriptStatement getBody() {
		return body;
	}
	
	public static HashSet<String> getSupportedAttributes() {
		return supportedAttributes;
	}
	
	public CFTransactionStatement(Token _t, Map<CFIdentifier, CFExpression> _attr, CFScriptStatement _body) {
		super(_t, _attr);
		
		body = _body;
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append("transaction ");
		DecompileAttributes(sb);
		if (body == null) {
			sb.append(";");
		} else {
			sb.append(body.Decompile(0));
		}
		
		return sb.toString();
	}
	
}
