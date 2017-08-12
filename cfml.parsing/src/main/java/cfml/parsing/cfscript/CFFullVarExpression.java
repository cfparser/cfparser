package cfml.parsing.cfscript;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.CFSCRIPTLexer;
import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

public class CFFullVarExpression extends CFIdentifier implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	// private Token token;
	private List<CFExpression> expressions;
	
	public CFFullVarExpression(Token _t, CFExpression _main) {
		super(_t);
		// token = _t;
		expressions = new ArrayList<CFExpression>();
		if (_main != null) {
			expressions.add(_main);
			_main.setParent(this);
		}
	}
	
	public CFIdentifier getIdentifier() {
		return (CFIdentifier) expressions.get(0);
	}
	
	@Override
	public String getScope() {
		return getIdentifier().getScope();
	}
	
	@Override
	public byte getType() {
		return expressions.get(expressions.size() - 1).getType();
	}
	
	@Override
	public boolean isEscapeSingleQuotes() {
		return expressions.get(expressions.size() - 1).isEscapeSingleQuotes();
	}
	
	public void addMember(CFExpression _right) {
		expressions.add(_right);
		if (_right != null) {
			_right.setParent(this);
		}
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		for (CFExpression expression : expressions) {
			if (sb.length() > 0) {
				if (expression.getType() == CFExpression.IDENTIFIER
						&& expression.getToken().getType() == CFSCRIPTLexer.LEFTBRACKET) {
					// Array notation []
				} else if (expression.getType() == CFExpression.IDENTIFIER || expression.getType() == CFExpression.LITERAL) {
					sb.append(".");
				} else if (expression instanceof CFFunctionExpression
						&& ((CFFunctionExpression) expression).getIdentifier() != null) {
					sb.append(".");
				}
			}
			sb.append(expression.Decompile(0));
		}
		return sb.toString();
	}
	
	public List<CFExpression> getExpressions() {
		return expressions;
	}
	
	public CFIdentifier getLastIdentifier() {
		for (int i = expressions.size() - 1; i >= 0; i--) {
			if (expressions.get(i) instanceof CFIdentifier) {
				return (CFIdentifier) expressions.get(i);
			}
		}
		return null;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return expressions;
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
}
