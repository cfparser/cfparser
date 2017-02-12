package cfml.parsing.cfscript;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.util.ArrayBuilder;

public class CFTernaryExpression extends CFAssignmentExpression implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// instance vars
	// private int _kind;
	private CFExpression _left;
	private CFExpression _right;
	// private String operatorImage;
	
	private CFExpression _cond;
	
	public CFTernaryExpression(Token t, CFExpression e1, CFExpression left, CFExpression right) {
		super(t, right, right);
		// _kind = t.getType();
		// operatorImage = t.getText();
		// if (_kind == CFSCRIPTLexer.ANDOPERATOR) {
		// _kind = CFSCRIPTLexer.AND;
		// } else if (_kind == CFSCRIPTLexer.OROPERATOR) {
		// _kind = CFSCRIPTLexer.OR;
		// } else if (_kind == CFSCRIPTLexer.MODOPERATOR) {
		// _kind = CFSCRIPTLexer.MOD;
		// }
		_cond = e1;
		_left = left;
		_right = right;
	}
	
	@Override
	public byte getType() {
		return CFExpression.BINARY;
	}
	
	@Override
	public String Decompile(int indent) {
		// String endChar = "";
		// if (_kind == CFSCRIPTLexer.LEFTBRACKET) {
		// endChar = "]";
		// }
		return _cond.Decompile(indent) + "?" + _left.Decompile(indent) + ":" + _right.Decompile(indent);
	}
	
	@Override
	public CFExpression getLeft() {
		return _left;
	}
	
	@Override
	public CFExpression getRight() {
		return _right;
	}
	
	public CFExpression getCond() {
		return _cond;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression(_cond, _left, _right);
	}
	
}
