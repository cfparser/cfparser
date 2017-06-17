package cfml.parsing.cfscript;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.CFSCRIPTLexer;
import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

public class CFLiteral extends CFExpression implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String val;
	private int kind;
	private String image;
	
	public CFLiteral(Token _t, String img) {
		this(_t);
		val = img;
		image = img;
	}
	
	public CFLiteral(Token _t) {
		super(_t);
		kind = _t.getType();
		image = _t.getText();
		switch (kind) {
		case CFSCRIPTLexer.FLOATING_POINT_LITERAL:
		case CFSCRIPTLexer.INTEGER_LITERAL:
			val = _t.getText();
			break;
		case CFSCRIPTLexer.STRING_LITERAL:
			// create a String, stripping off the surrounding quotes and
			// replacing any escaped quotes with a single quote
			String str = _t.getText();// .substring(1, _t.getText().length() - 1);
			// str = str.replaceAll(quote + quote, quote);
			image = str;
			val = str;
			break;
		case CFSCRIPTLexer.BOOLEAN_LITERAL:
			val = _t.getText();
			break;
		// CFML doesn't do nulls, to my knowledge
		// case CFSCRIPTLexer.NULL:
		// val = "";
		// break;
		default:
			break;
		}
	}
	
	@Override
	public byte getType() {
		return CFExpression.LITERAL;
	}
	
	public String getStringImage() {
		return image;
	}
	
	@Override
	public String Decompile(int indent) {
		try {
			if (CFSCRIPTLexer.STRING_LITERAL == kind && val != null) {
				return "'" + val + "'";
			} else {
				return val == null ? "" : val;
			}
		} catch (Exception e) {
			return "Couldn't get literal value";
		}
		
	}
	
	public String getVal() {
		return val;
	}
	
	public int getKind() {
		return kind;
	}
	
	public String getImage() {
		return image;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression();
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
}
