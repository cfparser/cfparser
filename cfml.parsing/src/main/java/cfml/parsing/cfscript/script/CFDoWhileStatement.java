package cfml.parsing.cfscript.script;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.util.ArrayBuilder;

public class CFDoWhileStatement extends CFParsedStatement implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private CFExpression condition;
	private CFScriptStatement body;
	
	public CFDoWhileStatement(Token t, CFExpression _cond, CFScriptStatement _body) {
		super(t);
		condition = _cond;
		body = _body;
	}
	
	public CFExpression getCondition() {
		return condition;
	}
	
	public CFScriptStatement getBody() {
		return body;
	}
	
	@Override
	public void checkIndirectAssignments(String[] scriptSource) {
		body.checkIndirectAssignments(scriptSource);
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append(Indent(indent));
		sb.append("do");
		sb.append(body.Decompile(indent + 2));
		sb.append("while(");
		sb.append(condition.Decompile(indent));
		sb.append(" ) ");
		return sb.toString();
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression(condition);
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement(body);
	}
}
