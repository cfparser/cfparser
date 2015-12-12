package cfml.parsing.cfscript;

import java.util.Set;
import java.util.Vector;

import cfml.parsing.util.SequencedHashMap;

public class ArgumentsVector extends Vector<CFExpression> {
	
	private static final long serialVersionUID = 1L;
	
	private SequencedHashMap namedArgs;
	
	public ArgumentsVector() {
		super();
		namedArgs = new SequencedHashMap();
	}
	
	public void putNamedArg(String _name, CFExpression _val) {
		namedArgs.put(_name.toLowerCase(), _val);
		addElement(_val);
	}
	
	public CFExpression getNamedArg(String _name) {
		return (CFExpression) namedArgs.get(_name.toLowerCase());
	}
	
	public Set<String> keys() {
		return namedArgs.keySet();
	}
	
	public SequencedHashMap getNamedArgs() {
		return namedArgs;
	}
	
}
