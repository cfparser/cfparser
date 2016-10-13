package cfml.parsing.cfscript.script;

import org.antlr.v4.runtime.CommonTokenStream;

public interface CFScriptStatement {
	
	public String Decompile(int indent);
	
	public void checkIndirectAssignments(String[] scriptSource);
	
	public CommonTokenStream getTokens();
	
	public void setTokens(CommonTokenStream tokens);
	
}
