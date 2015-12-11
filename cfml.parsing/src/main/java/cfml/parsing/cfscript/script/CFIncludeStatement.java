package cfml.parsing.cfscript.script;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;

public class CFIncludeStatement extends CFParsedStatement implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CFExpression template;
	
	public CFIncludeStatement(Token _t, CFExpression _template) {
		super(_t);
		template = _template;
	}
	
	@Override
	public String Decompile(int indent) {
		return "include " + template.Decompile(0);
	}
	
	public CFExpression getTemplate() {
		return template;
	}
	
}
