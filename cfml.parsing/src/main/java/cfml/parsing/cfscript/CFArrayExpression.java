package cfml.parsing.cfscript;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

public class CFArrayExpression extends CFExpression implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<CFExpression> elements;
	
	public CFArrayExpression(Token t) {
		super(t);
		elements = new ArrayList<CFExpression>();
		if (elements != null) {
			elements.forEach(elem -> elem.setParent(this));
		}
	}
	
	public void addElement(CFExpression _e) {
		elements.add(_e);
		if (_e != null)
			_e.setParent(this);
	}
	
	@Override
	public String Decompile(int indent) {
		if (elements.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			for (int i = 0; i < elements.size(); i++) {
				sb.append(elements.get(i).Decompile(0));
				sb.append(',');
			}
			sb.deleteCharAt(sb.length() - 1);
			
			sb.append(']');
			return sb.toString();
		} else {
			return "[]";
		}
	}
	
	public ArrayList<CFExpression> getElements() {
		return elements;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		return elements;
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
}
