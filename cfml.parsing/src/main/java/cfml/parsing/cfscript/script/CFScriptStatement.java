package cfml.parsing.cfscript.script;

import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;

public interface CFScriptStatement {
	
	public String Decompile(int indent);
	
	public void checkIndirectAssignments(String[] scriptSource);
	
	public CommonTokenStream getTokens();
	
	public void setTokens(CommonTokenStream tokens);
	
	public Token getToken();
	
	public List<CFScriptStatement> decomposeScript();
	
	public List<CFExpression> decomposeExpression();
	
	public Object getParent();
	
	public void setParent(Object parent);
	
}
