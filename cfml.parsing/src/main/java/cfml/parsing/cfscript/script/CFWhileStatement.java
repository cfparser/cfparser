package cfml.parsing.cfscript.script;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.util.ArrayBuilder;

public class CFWhileStatement extends CFParsedStatement implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private CFExpression cond;
	private CFScriptStatement body;
	
	public CFExpression getCond() {
		return cond;
	}
	
	public CFScriptStatement getBody() {
		return body;
	}
	
	public CFWhileStatement(Token t, CFExpression _cond, CFScriptStatement _body) {
		super(t);
		cond = _cond;
		body = _body;
	}
	
	@Override
	public void checkIndirectAssignments(String[] scriptSource) {
		body.checkIndirectAssignments(scriptSource);
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append(Indent(indent));
		sb.append("while(");
		sb.append(cond.Decompile(indent));
		sb.append(" ) ");
		sb.append(body.Decompile(indent + 2));
		return sb.toString();
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression(cond);
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement(body);
	}
}
