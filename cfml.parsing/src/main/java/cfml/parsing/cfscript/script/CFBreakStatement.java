package cfml.parsing.cfscript.script;

import java.util.List;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.util.ArrayBuilder;

public class CFBreakStatement extends CFParsedStatement implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	public CFBreakStatement(org.antlr.v4.runtime.Token t) {
		super(t);
	}
	
	@Override
	public String Decompile(int indent) {
		return Indent(indent) + "break";
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression();
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
}
