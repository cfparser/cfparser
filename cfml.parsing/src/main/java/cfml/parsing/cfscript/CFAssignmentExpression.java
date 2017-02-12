package cfml.parsing.cfscript;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

public class CFAssignmentExpression extends CFExpression {
	
	private static final long serialVersionUID = 1L;
	
	private CFExpression left;
	private CFExpression right;
	
	List<CFIdentifier> otherIds = new ArrayList<CFIdentifier>();
	
	public List<CFIdentifier> getOtherIds() {
		return otherIds;
	}
	
	public CFAssignmentExpression(Token t, CFExpression _left, CFExpression _right) {
		super(t);
		left = _left;
		right = _right;
	}
	
	@Override
	public byte getType() {
		return CFExpression.ASSIGNMENT;
	}
	
	public void checkIndirect(String expr) {
		if (left instanceof CFVarExpression) {
			String lhs = expr.substring(0, expr.indexOf('=')).trim();
			// check for special case of "#foo#"="bar" or '#foo#'='bar'
			if ((lhs.startsWith("\"#") && lhs.endsWith("#\"")) || (lhs.startsWith("'#") && lhs.endsWith("#'"))) {
				((CFVarExpression) left).setIndirect(true);
			}
		}
	}
	
	@Override
	public void checkIndirectAssignments(String[] scriptSource) {
		if (left instanceof CFVarExpression) {
			/*
			 * We know this is a valid assignment expression but is the LHS expression a # expression e.g. "#foo#" =
			 * "bar" Since the poundSignFilter removes the #'s for the parser we need to look at the original source. We
			 * can get the line from this CFExpression but the column isn't accurate enough for CFFullVarExpressions
			 * especially as more than one expression may occur on the same line.
			 * 
			 * We look backwards from the '=' skipping any whitespace and looking for #", #' or # on it's own.
			 */
			
			// find the original expression in the script source code
			String expr = scriptSource[left.getLine() - 1];
			int lhsEnd = expr.indexOf("=", left.getColumn());
			if (lhsEnd == -1) {
				// if the expression is split over 2 lines
				// e.g. "#foo#"
				// = "bar"
				lhsEnd = expr.length();
			}
			
			char[] exprChars = expr.substring(0, lhsEnd).toCharArray();
			int i = exprChars.length - 1;
			// skip over whitespace
			while (i >= 0 && (Character.isWhitespace(exprChars[i]))) {
				i--;
			}
			
			// does LHS expression end in #" or #'?
			if (i >= 0 && (exprChars[i] == '\"' || exprChars[i] == '\'')) {
				i--;
				if (i >= 0 && exprChars[i] == '#') {
					((CFVarExpression) left).setIndirect(true);
				}
			}
		}
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		if (left != null) {
			sb.append(left.Decompile(indent));
		}
		sb.append(" = ");
		for (CFIdentifier id : otherIds) {
			sb.append(id.Decompile(indent));
			sb.append(" = ");
		}
		if (right != null) {
			sb.append(right.Decompile(indent));
		}
		return sb.toString();
	}
	
	public CFExpression getLeft() {
		return left;
	}
	
	public CFExpression getRight() {
		return right;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		List<CFExpression> retval = ArrayBuilder.createCFExpression(left, right);
		retval.addAll(otherIds);
		return retval;
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
}
