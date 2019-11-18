package cfml.parsing.cfscript;

import java.util.List;

import cfml.CFSCRIPTLexer;
import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

/**
 * Definition of expression tree for a unary expression.
 */

public class CFUnaryExpression extends CFExpression implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private int kind;
	private CFExpression sub;
	private boolean prefixop = true;
	
	public CFUnaryExpression(org.antlr.v4.runtime.Token _t, CFExpression _sub) {
		super(_t);
		createFromToken(_t, _sub);
	}
	
	public CFUnaryExpression(org.antlr.v4.runtime.Token _t, CFExpression _sub, boolean isprefixop) {
		super(_t);
		createFromToken(_t, _sub);
		prefixop = isprefixop;
	}
	
	private void createFromToken(org.antlr.v4.runtime.Token _t, CFExpression _sub) {
		kind = _t.getType();
		sub = _sub;
		if (sub != null) {
			sub.setParent(this);
		}
	}
	
	@Override
	public byte getType() {
		return CFExpression.UNARY;
	}
	
	public boolean isPrefixop() {
		return prefixop;
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		
		switch (kind) {
		case CFSCRIPTLexer.LEFTPAREN:
			sb.append('(');
			sb.append(sub.Decompile(0));
			sb.append(')');
			break;
		case CFSCRIPTLexer.MINUS:
			sb.append('-');
			sb.append(sub.Decompile(0));
			break;
		case CFSCRIPTLexer.NOT:
			sb.append("NOT ");
			sb.append(sub.Decompile(0));
			break;
		case CFSCRIPTLexer.NOTOP:
			sb.append('!');
			sb.append(sub.Decompile(0));
			break;
		case CFSCRIPTLexer.NOTNOTOP:
			sb.append("!!");
			sb.append(sub.Decompile(0));
			break;
		case CFSCRIPTLexer.PLUS:
			sb.append('+');
			sb.append(sub.Decompile(0));
			break;
		case CFSCRIPTLexer.PLUSPLUS:
			if (prefixop)
				sb.append("++");
			sb.append(sub.Decompile(0));
			if (!prefixop)
				sb.append("++");
			break;
		case CFSCRIPTLexer.MINUSMINUS:
			if (prefixop)
				sb.append("--");
			sb.append(sub.Decompile(0));
			if (!prefixop)
				sb.append("--");
			break;
		default:
			sb.append(sub.Decompile(0));
			break;
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return Decompile(0);
	}
	
	public int getKind() {
		return kind;
	}
	
	public CFExpression getSub() {
		return sub;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression(sub);
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
	
}
