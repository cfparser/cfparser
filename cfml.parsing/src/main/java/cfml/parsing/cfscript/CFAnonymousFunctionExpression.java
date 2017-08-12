package cfml.parsing.cfscript;

import java.util.List;

import cfml.parsing.cfscript.script.CFFuncDeclStatement;
import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

/**
 * Definition of expression tree for a unary expression.
 */

public class CFAnonymousFunctionExpression extends CFExpression implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private CFFuncDeclStatement funcDeclStatement;
	
	public CFAnonymousFunctionExpression(org.antlr.v4.runtime.Token _t, CFFuncDeclStatement funcDeclStatement) {
		super(_t);
		this.funcDeclStatement = funcDeclStatement;
		if (funcDeclStatement != null)
			funcDeclStatement.setParent(this);
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
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression();
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement(funcDeclStatement);
	}
	
}
