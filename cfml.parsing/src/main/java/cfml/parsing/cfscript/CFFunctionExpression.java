package cfml.parsing.cfscript;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.reporting.ParseException;
import cfml.parsing.util.ArrayBuilder;

public class CFFunctionExpression extends CFMember {
	private static final long serialVersionUID = 1L;
	
	private CFIdentifier nameId;
	private ArrayList<CFExpression> args; // List of CFExpression's
	private boolean isUDF = true;
	private CFScriptStatement body;
	
	// private boolean isParamExists;
	public CFFunctionExpression(CFIdentifier _name, ArrayList<CFExpression> _args) throws ParseException {
		this(_name.getToken(), _name, _args);
	}
	
	public CFFunctionExpression(Token t, CFIdentifier _name, ArrayList<CFExpression> _args) throws ParseException {
		super(t, null);
		nameId = _name;
		if (nameId != null) {
			nameId.setParent(this);
		}
		args = _args;
		if (args != null) {
			args.forEach(elem -> elem.setParent(this));
		}
		isUDF = false;
	}
	
	@Override
	public byte getType() {
		return CFExpression.FUNCTION;
	}
	
	public String getFunctionName() {
		if (nameId instanceof CFFullVarExpression) {
			return ((CFFullVarExpression) nameId).getLastIdentifier().Decompile(0).toLowerCase();
		}
		return nameId == null ? "" : nameId.Decompile(0).toLowerCase();
	}
	
	public boolean isUDF() {
		return isUDF;
	}
	
	@Override
	public String Decompile(int indent) {
		String s = nameId == null ? "" : nameId.Decompile(indent);
		s += "(";
		
		for (int i = 0; i < args.size(); i++) {
			s += args.get(i).Decompile(indent);
			if (i < args.size() - 1) {
				s += ", ";
			}
		}
		
		s += ")";
		if (body != null) {
			s += body.Decompile(indent + 2);
		}
		return s;
	}
	
	public ArrayList<CFExpression> getArgs() {
		return args;
	}
	
	public String getName() {
		return nameId == null ? "" : nameId.Decompile(0);
	}
	
	public CFIdentifier getIdentifier() {
		return nameId;
	}
	
	public CFIdentifier getNameId() {
		return nameId;
	}
	
	public CFScriptStatement getBody() {
		return body;
	}
	
	public void setBody(CFScriptStatement body) {
		this.body = body;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		ArrayList<CFExpression> retval = new ArrayList<CFExpression>();
		for (final CFExpression expr : getArgs()) {
			if (expr instanceof CFAssignmentExpression) {
				// Only the right hand side of 'assignments' -- these are
				// named parameters.
				retval.add(((CFAssignmentExpression) expr).getRight());
			} else {
				retval.add(expr);
			}
		}
		return retval;
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement(body);
	}
}
