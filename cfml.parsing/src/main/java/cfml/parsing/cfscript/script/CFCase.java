package cfml.parsing.cfscript.script;

/**
 * This class represents a case/default label in a switch statement. A cfswitchstatement should
 * hold a vector of these.
 */

import java.util.List;

import cfml.parsing.cfscript.CFExpression;

public class CFCase implements CFScriptStatement, java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<CFScriptStatement> statements;
	private boolean isDefault = true;
	private CFExpression constant;
	
	public CFCase(CFExpression _constant, List<CFScriptStatement> _statement) {
		this(_statement);
		isDefault = false;
		constant = _constant;
	}
	
	public CFExpression getConstant() {
		return constant;
	}
	
	public List<CFScriptStatement> getStatements() {
		return statements;
	}
	
	public CFCase(List<CFScriptStatement> _statement) {
		statements = _statement;
	}
	
	public boolean isDefault() {
		return isDefault;
	}
	
	@Override
	public void checkIndirectAssignments(String[] scriptSource) {
		for (CFScriptStatement statement : statements)
			statement.checkIndirectAssignments(scriptSource);
	}
	
	@Override
	public String toString() {
		return Decompile(0);
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		if (isDefault) {
			sb.append("default:");
		} else {
			sb.append("case ");
			sb.append(constant.Decompile(0));
			sb.append(":");
		}
		for (CFScriptStatement statement : statements) {
			sb.append(statement.Decompile(0)).append(";");
		}
		return sb.toString();
	}
	
}
