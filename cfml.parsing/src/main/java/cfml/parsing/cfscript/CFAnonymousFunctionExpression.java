package cfml.parsing.cfscript;

import cfml.parsing.cfscript.script.CFFuncDeclStatement;

/**
 * Definition of expression tree for a unary expression.
 */

public class CFAnonymousFunctionExpression extends CFExpression implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private CFFuncDeclStatement funcDeclStatement;
	
	public CFAnonymousFunctionExpression(org.antlr.v4.runtime.Token _t, CFFuncDeclStatement funcDeclStatement) {
		super(_t);
		this.funcDeclStatement = funcDeclStatement;
	}
	
	@Override
	public byte getType() {
		return CFExpression.NESTED;
	}
	
	@Override
	public String Decompile(int indent) {
		return (funcDeclStatement.Decompile(0));
	}
	
	public CFFuncDeclStatement getFunctionDeclaration() {
		return funcDeclStatement;
	}
	
	public CFFuncDeclStatement getFuncDeclStatement() {
		return funcDeclStatement;
	}
	
}
