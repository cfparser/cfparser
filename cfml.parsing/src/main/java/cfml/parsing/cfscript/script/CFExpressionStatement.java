package cfml.parsing.cfscript.script;

import java.util.List;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.util.ArrayBuilder;

public class CFExpressionStatement extends CFParsedStatement implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private CFExpression expression;
	
	public CFExpressionStatement(CFExpression _e) {
		super(_e.getLine(), _e.getColumn());
		expression = _e;
	}
	
	@Override
	public String Decompile(int indent) {
		return expression.Decompile(indent);
	}
	
	@Override
	public void checkIndirectAssignments(String[] scriptSource) {
		expression.checkIndirectAssignments(scriptSource);
	}
	
	public CFExpression getExpression() {
		return expression;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression(expression);
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
}
