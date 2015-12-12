package cfml.parsing.cfscript.script;

import cfml.parsing.cfscript.CFExpression;

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
	
}
