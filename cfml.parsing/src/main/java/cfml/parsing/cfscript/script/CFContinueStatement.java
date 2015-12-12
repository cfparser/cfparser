package cfml.parsing.cfscript.script;

import org.antlr.v4.runtime.Token;

public class CFContinueStatement extends CFParsedStatement implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	public CFContinueStatement(Token t) {
		super(t);
	}
	
	@Override
	public String Decompile(int indent) {
		return Indent(indent) + "continue";
	}
	
}
