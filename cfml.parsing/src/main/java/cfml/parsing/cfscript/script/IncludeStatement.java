package cfml.parsing.cfscript.script;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.util.ArrayBuilder;

public class IncludeStatement extends CFParsedStatement implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public CFExpression getTemplate() {
		return template;
	}
	
	private CFExpression template;
	
	public IncludeStatement(Token _t, CFExpression _template) {
		super(_t);
		template = _template;
	}
	
	@Override
	public String Decompile(int indent) {
		return "include " + template.Decompile(0);
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression(template);
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
}
