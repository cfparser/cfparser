package cfml.parsing.cfscript.script;

/**
 * Compound statement, a container for a sequence of substatements.
 */

import java.util.ArrayList;

public class CFCompoundStatement extends CFParsedStatement implements CFScriptStatement, java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<CFScriptStatement> _v; // Vector of CFStatements
	
	public CFCompoundStatement() {
		super(1, 1);
		_v = new ArrayList<CFScriptStatement>();
	}
	
	public CFCompoundStatement(org.antlr.v4.runtime.Token t) {
		super(t);
		_v = new ArrayList<CFScriptStatement>();
	}
	
	public void add(CFScriptStatement s) {
		// System.out.println("add:: " + s.Decompile(0));
		_v.add(s);
	}
	
	public void addFunction(CFScriptStatement s) {
		_v.add(0, s);
	}
	
	public ArrayList<CFScriptStatement> getStatements() {
		return _v;
	}
	
	public int numOfStatements() {
		return _v.size();
	}
	
	@Override
	public void checkIndirectAssignments(String[] scriptSource) {
		for (int i = 0; i < _v.size(); i++) {
			_v.get(i).checkIndirectAssignments(scriptSource);
		}
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder s = new StringBuilder();
		s.append(Indent(indent));
		s.append("{\n");
		for (int i = 0; i < _v.size(); i++) {
			s.append(_v.get(i).Decompile(indent + 2)).append(";\n");
		}
		s.append("\n");
		s.append(Indent(indent));
		s.append("}");
		return s.toString();
	}
	
}
