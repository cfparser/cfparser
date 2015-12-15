package cfml.parsing.cfscript.script;

import cfml.parsing.cfscript.CFExpression;

public class CFIfStatement extends CFParsedStatement implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private CFExpression cond;
	private CFScriptStatement thenStatement;
	private CFScriptStatement elseStatement; // null if there is no else clause
	
	public CFIfStatement(org.antlr.v4.runtime.Token _t, CFExpression _cond, CFScriptStatement _then,
			CFScriptStatement _else) {
		super(_t);
		cond = _cond;
		thenStatement = _then;
		elseStatement = _else;
	}
	
	@Override
	public void checkIndirectAssignments(String[] scriptSource) {
		thenStatement.checkIndirectAssignments(scriptSource);
		if (elseStatement != null) {
			elseStatement.checkIndirectAssignments(scriptSource);
		}
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder s = new StringBuilder();
		s.append(Indent(indent));
		s.append("if(");
		s.append(cond.Decompile(indent));
		s.append(" ) ");
		s.append(thenStatement.Decompile(indent + 2));
		if (elseStatement != null) {
			s.append("\n");
			s.append(Indent(indent));
			s.append("else ");
			s.append(elseStatement.Decompile(indent + 2));
		}
		return s.toString();
	}
	
	public CFExpression getCond() {
		return cond;
	}
	
	public CFScriptStatement getThenStatement() {
		return thenStatement;
	}
	
	public CFScriptStatement getElseStatement() {
		return elseStatement;
	}
	
}
