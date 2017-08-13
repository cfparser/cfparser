package cfml.parsing.cfscript;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

public class CFVarDeclExpression extends CFExpression {
	
	private static final long serialVersionUID = 1L;
	
	private CFExpression var;
	private CFExpression init; // null if none
	List<CFIdentifier> otherVars = new ArrayList<CFIdentifier>();
	List<CFIdentifier> otherIds = new ArrayList<CFIdentifier>();
	
	public List<CFIdentifier> getOtherVars() {
		return otherVars;
	}
	
	public List<CFIdentifier> getOtherIds() {
		return otherIds;
	}
	
	public CFVarDeclExpression(Token _t, CFExpression _var, CFExpression _init) {
		super(_t);
		this.var = _var;
		if (_var != null) {
			_var.setParent(this);
		}
		init = _init;
		if (init != null) {
			init.setParent(this);
		}
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder s = new StringBuilder(Indent(indent));
		s.append("var ");
		s.append(var.Decompile(0));
		for (CFIdentifier id : otherVars) {
			s.append(" = var ");
			s.append(id.Decompile(indent));
		}
		for (CFIdentifier id : otherIds) {
			s.append(" = ");
			s.append(id.Decompile(indent));
		}
		if (init != null) {
			s.append(" = ");
			s.append(init.Decompile(indent + 2));
		}
		return s.toString();
	}
	
	public CFExpression getInit() {
		return init;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		List<CFExpression> retval = new ArrayList<CFExpression>();
		retval.add(var);
		retval.add(init);
		retval.addAll(otherIds);
		retval.addAll(otherVars);
		return retval;
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
	
	public CFExpression getVar() {
		return var;
	}
	
	public String getName() {
		if (var.getClass().equals(CFIdentifier.class))
			return ((CFIdentifier) var).getName();
		return var.Decompile(0);
	}
}
