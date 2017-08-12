package cfml.parsing.cfscript;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

public class CFIdentifier extends CFVarExpression implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	protected String name;
	protected String scope;
	
	public String getScope() {
		return scope;
	}
	
	protected Token token;
	
	public CFIdentifier(Token _t) {
		this(_t, null, null);
	}
	
	public CFIdentifier(Token _t, String _img) {
		this(_t, null,_img );
	}
	
	public CFIdentifier(Token _t, String scope, String _img) {
		super(_t);
		name = _img;
		token = _t;
		this.scope = scope;
	}
	
	@Override
	public byte getType() {
		return CFExpression.IDENTIFIER;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFullName() {
		return (scope != null && scope.trim().length() > 0 ? scope.trim() : "") + name;
	}
	
	@Override
	public Token getToken() {
		return token;
	}
	
	@Override
	public String Decompile(int indent) {
		return getFullName();
	}
	
	@Override
	public String toString() {
		return name;
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
