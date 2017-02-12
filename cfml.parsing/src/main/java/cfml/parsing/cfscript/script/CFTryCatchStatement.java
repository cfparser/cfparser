package cfml.parsing.cfscript.script;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.reporting.ParseException;
import cfml.parsing.util.ArrayBuilder;

public class CFTryCatchStatement extends CFParsedStatement implements java.io.Serializable {
	
	private static final long serialVersionUID = 1;
	
	private CFScriptStatement body; // body of the try block
	
	public CFScriptStatement getBody() {
		return body;
	}
	
	public List<CFCatchClause> getCatchStatements() {
		return catchStatements;
	}
	
	public CFScriptStatement getFinallyStatement() {
		return finallyStatement;
	}
	
	private List<CFCatchClause> catchStatements;
	private CFScriptStatement finallyStatement;
	
	public CFTryCatchStatement(Token _t1, CFScriptStatement _s1, List<CFCatchClause> _catches, CFScriptStatement _finally) {
		super(_t1);
		body = _s1;
		catchStatements = _catches;
		
		// now stick the catch 'any' if there is one at the end
		// CFCatchClause catchAny = null;
		// CFCatchClause nextClause;
		// for (int i = 0; i < catchStatements.size(); i++) {
		// nextClause = catchStatements.get(i);
		// if (nextClause.isCatchAny()) {
		// catchAny = nextClause;
		// catchStatements.remove(i);
		// i--; // make adjustment for removed clause
		// }
		// }
		//
		// if (catchAny != null) {
		// catchStatements.add(catchAny);
		// }
		
		finallyStatement = _finally;
	}
	
	public void validate() {
		if (catchStatements.size() == 0 && finallyStatement == null) {
			throw new ParseException(token, "try statement must include at least one catch clause or a finally clause.");
		}
	}
	
	@Override
	public void checkIndirectAssignments(String[] scriptSource) {
		body.checkIndirectAssignments(scriptSource);
		for (int i = 0; i < catchStatements.size(); i++) {
			((CFScriptStatement) catchStatements.get(i)).checkIndirectAssignments(scriptSource);
		}
	}
	
	@Override
	public String Decompile(int indent) {
		validate();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < catchStatements.size(); i++) {
			CFCatchStatement clause = (CFCatchStatement) catchStatements.get(i);
			sb.append("catch(");
			sb.append(clause.getType());
			sb.append(" ");
			sb.append(clause.getVariable());
			// sb.append("{");
			sb.append(clause.getCatchBody().Decompile(0));
			// sb.append("}");
		}
		
		sb.insert(0, "try" + body.Decompile(0));
		return sb.toString();
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return ArrayBuilder.createCFExpression();
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		List<CFScriptStatement> retval = new ArrayList<CFScriptStatement>();
		retval.add(body);
		// retval.addAll(catchStatements);
		retval.add(finallyStatement);
		return ArrayBuilder.createCFScriptStatement();
	}
}
