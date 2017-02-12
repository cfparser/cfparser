package cfml.parsing.cfscript;

import java.util.List;

/**
 * Abstract class that takes care of the line and column positions of parsed
 * elements.
 */

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.script.CFScriptStatement;

public abstract class CFParsedStatement implements CFStatement, java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int line;
	private int col;
	private Token token;
	
	public CFParsedStatement(int _line, int _col) {
		line = _line;
		col = _col + 1;
	}
	
	public CFParsedStatement(Token t) {
		setToken(t);
	}
	
	public Token getToken() {
		return token;
	}
	
	public void setToken(Token t) {
		if (t != null) {
			line = t.getLine();
			col = t.getCharPositionInLine() + 1;
		}
		token = t;
	}
	
	@Override
	public abstract String Decompile(int indent);
	
	@Override
	public void checkIndirectAssignments(String[] scriptSource) {
		// default behavior: do nothing
	}
	
	protected void setLineCol(CFContext context) {
		context.setLineCol(line, col);
	}
	
	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return col;
	}
	
	public String Indent(int indent) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indent; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}
	
	public abstract List<CFExpression> decomposeExpression();
	
	public abstract List<CFScriptStatement> decomposeScript();
	
}
