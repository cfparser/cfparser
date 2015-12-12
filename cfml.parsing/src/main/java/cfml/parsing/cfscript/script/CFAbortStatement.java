package cfml.parsing.cfscript.script;

import cfml.parsing.cfscript.CFExpression;

public class CFAbortStatement extends CFParsedStatement implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CFExpression message;
	
	public CFExpression getMessage() {
		return message;
	}
	
	public CFAbortStatement(org.antlr.v4.runtime.Token t) {
		this(t, null);
	}
	
	public CFAbortStatement(org.antlr.v4.runtime.Token t, CFExpression _message) {
		super(t);
		message = _message;
	}
	
	@Override
	public String Decompile(int indent) {
		if (message != null) {
			return "abort \"" + message + "\"";
		} else {
			return "abort";
		}
	}
	
}
