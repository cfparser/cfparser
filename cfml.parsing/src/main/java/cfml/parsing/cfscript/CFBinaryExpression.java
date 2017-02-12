package cfml.parsing.cfscript;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.CFSCRIPTLexer;
import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

public class CFBinaryExpression extends CFExpression implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// instance vars
	private int _kind;
	protected final CFExpression _left;
	protected final CFExpression _right;
	protected String operatorImage;
	
	public CFBinaryExpression(Token t, CFExpression left, CFExpression right) {
		super(t);
		_kind = t.getType();
		operatorImage = t.getText();
		if (_kind == CFSCRIPTLexer.ANDOPERATOR) {
			_kind = CFSCRIPTLexer.AND;
		} else if (_kind == CFSCRIPTLexer.OROPERATOR) {
			_kind = CFSCRIPTLexer.OR;
		} else if (_kind == CFSCRIPTLexer.MODOPERATOR) {
			_kind = CFSCRIPTLexer.MOD;
		}
		_left = left;
		_right = right;
	}
	
	@Override
	public byte getType() {
		return CFExpression.BINARY;
	}
	
	@Override
	public String Decompile(int indent) {
		String endChar = "";
		if (_kind == CFSCRIPTLexer.LEFTBRACKET) {
			endChar = "]";
		}
		return "" + _left.Decompile(indent) + " " + operatorImage + " " + _right.Decompile(indent) + endChar;
	}
	
	public CFExpression getLeft() {
		return _left;
	}
	
	public CFExpression getRight() {
		return _right;
	}
	
	public int getKind() {
		return _kind;
	}
	
	public String getOperatorImage() {
		return operatorImage;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression(_left, _right);
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
	
}
