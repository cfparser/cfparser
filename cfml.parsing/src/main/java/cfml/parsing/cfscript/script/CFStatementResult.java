package cfml.parsing.cfscript.script;

import cfml.parsing.cfscript.CFData;

public class CFStatementResult {
	
	private static final int RETURN_TYPE = 0;
	private static final int BREAK_TYPE = 1;
	
	private static final int CONTINUE_TYPE = 2;
	
	public static final CFStatementResult BREAK = new CFStatementResult(BREAK_TYPE);
	public static final CFStatementResult CONTINUE = new CFStatementResult(CONTINUE_TYPE);
	
	private int resultType;
	private CFData returnValue;
	
	private CFStatementResult(int type) {
		resultType = type;
	}
	
	public CFStatementResult(CFData value) {
		resultType = RETURN_TYPE;
		returnValue = value;
	}
	
	public boolean isReturn() {
		return (resultType == RETURN_TYPE);
	}
	
	public boolean isBreak() {
		return (resultType == BREAK_TYPE);
	}
	
	public boolean isContinue() {
		return (resultType == CONTINUE_TYPE);
	}
	
	public CFData getReturnValue() {
		return returnValue;
	}
	
	public int getResultType() {
		return resultType;
	}
}
