package cfml.parsing.cfscript.script;

/**
 * Compound statement, a container for a sequence of substatements.
 */

import java.util.ArrayList;
import java.util.List;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.util.ArrayBuilder;

public class CFCompoundStatement extends CFParsedStatement implements CFScriptStatement, java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<CFScriptStatement> statements; // List of CFStatements
	
	public CFCompoundStatement() {
		super(1, 1);
		statements = new ArrayList<CFScriptStatement>();
	}
	
	public CFCompoundStatement(org.antlr.v4.runtime.Token t) {
		super(t);
		statements = new ArrayList<CFScriptStatement>();
	}
	
	public void add(CFScriptStatement s) {
		statements.add(s);
	}
	
	public void addFunction(CFScriptStatement s) {
		statements.add(0, s);
	}
	
	public ArrayList<CFScriptStatement> getStatements() {
		return statements;
	}
	
	public int numOfStatements() {
		return statements.size();
	}
	
	@Override
	public void checkIndirectAssignments(String[] scriptSource) {
		for (int i = 0; i < statements.size(); i++) {
			statements.get(i).checkIndirectAssignments(scriptSource);
		}
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder s = new StringBuilder();
		s.append(Indent(indent));
		s.append("{\n");
		for (int i = 0; i < numOfStatements(); i++) {
			CFScriptStatement statement = statements.get(i);
			// prevent endless loop
			if (statement != this) {
				s.append(statement.Decompile(indent + 2)).append(";\n");
			}
		}
		s.append("\n");
		s.append(Indent(indent));
		s.append("}");
		return s.toString();
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression();
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return statements;
	}
}
