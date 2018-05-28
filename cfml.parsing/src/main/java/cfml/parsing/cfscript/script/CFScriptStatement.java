package cfml.parsing.cfscript.script;

import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.HasToken;

public interface CFScriptStatement extends HasToken {
	
	public String Decompile(int indent);
	
	public void checkIndirectAssignments(String[] scriptSource);
	
	public CommonTokenStream getTokens();
	
	public void setTokens(CommonTokenStream tokens);
	
	public List<CFScriptStatement> decomposeScript();
	
	public List<CFExpression> decomposeExpression();
	
	public void setParent(Object parent);
	
	public int getOffset();
	
	public int getLine();
	
	public int getColumn();
	
}
