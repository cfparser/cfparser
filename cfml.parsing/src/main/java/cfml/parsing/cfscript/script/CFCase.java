package cfml.parsing.cfscript.script;

/**
 * This class represents a case/default label in a switch statement. A cfswitchstatement should
 * hold a vector of these.
 */

import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;

public class CFCase implements CFScriptStatement, java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<CFScriptStatement> statements;
	private boolean isDefault = true;
	private CFExpression constant;
	CommonTokenStream tokens;
	
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
		} else if (constant != null) {
			sb.append("case ");
			sb.append(constant.Decompile(0));
			sb.append(":");
		}
		for (CFScriptStatement statement : statements) {
			sb.append(statement.Decompile(0)).append(";");
		}
		return sb.toString();
	}
	
	public CommonTokenStream getTokens() {
		return tokens;
	}
	
	public void setTokens(CommonTokenStream tokens) {
		this.tokens = tokens;
	}
	
	@Override
	public Token getToken() {
		if (constant != null)
			return constant.getToken();
		if (statements.size() > 0) {
			return statements.get(0).getToken();
		}
		return null;
	}
}
