package cfml.parsing.cfscript;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

public class CFStructExpression extends CFExpression implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<CFExpression> elements;
	
	private boolean ordered;
	
	public CFStructExpression(Token t) {
		super(t);
		this.ordered = false;
		elements = new ArrayList<CFExpression>();
	}
	
	public CFStructExpression(Token t, boolean ordered) {
		super(t);
		this.ordered = ordered;
		elements = new ArrayList<CFExpression>();
	}
	
	public void addElement(CFStructElementExpression _element) {
		elements.add(_element);
		if (_element != null) {
			_element.setParent(this);
		}
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append(ordered ? '[' : '{');
		for (int i = 0; i < elements.size(); i++) {
			sb.append(((CFStructElementExpression) elements.get(i)).toString());
			sb.append(',');
		}
		
		if (elements.size() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		
		sb.append(ordered ? ']' : '}');
		return sb.toString();
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
	
	public boolean isOrdered() {
		return ordered;
	}
}
