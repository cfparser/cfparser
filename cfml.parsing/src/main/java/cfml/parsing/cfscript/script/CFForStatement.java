package cfml.parsing.cfscript.script;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.util.ArrayBuilder;

public class CFForStatement extends CFParsedStatement implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private CFExpression init;
	private CFExpression cond;
	private CFExpression next;
	private CFScriptStatement body;
	
	public CFForStatement(Token _t, CFExpression _init, CFExpression _cond, CFExpression _next, CFScriptStatement _body) {
		super(_t);
		init = _init;
		cond = _cond;
		next = _next;
		body = _body;
	}
	
	@Override
	public void checkIndirectAssignments(String[] scriptSource) {
		body.checkIndirectAssignments(scriptSource);
	}
	
	@Override
	public String Decompile(int indent) {
		String s = Indent(indent) + "for(";
		if (init != null) {
			s += init.Decompile(indent);
		}
		s += ";";
		if (cond != null) {
			s += cond.Decompile(indent);
		}
		s += ";";
		if (next != null) {
			s += next.Decompile(indent);
		}
		s += ")";
		s += body.Decompile(indent + 2);
		
		return s;
	}
	
	public CFExpression getInit() {
		return init;
	}
	
	public CFExpression getCond() {
		return cond;
	}
	
	public CFExpression getNext() {
		return next;
	}
	
	public CFScriptStatement getBody() {
		return body;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression(init, cond, next);
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement(body);
	}
}
