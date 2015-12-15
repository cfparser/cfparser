package cfml.parsing.cfscript.script;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.Token;

import cfml.parsing.cfscript.CFContext;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFIdentifier;
import cfml.parsing.reporting.ParseException;
import cfml.parsing.util.CFException;

abstract public class CFParsedAttributeStatement extends CFParsedStatement implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Map<CFIdentifier, CFExpression> attributes;
	
	protected CFParsedAttributeStatement(Token _t, Map<CFIdentifier, CFExpression> _a) {
		super(_t);
		attributes = _a;
	}
	
	public Map<CFIdentifier, CFExpression> getAttributes() {
		return attributes;
	}
	
	// utility method used for outputting the attributes to string
	protected void DecompileAttributes(StringBuilder sb) {
		List<CFIdentifier> sorted = new ArrayList<CFIdentifier>(attributes.keySet());
		Collections.sort(sorted, new Comparator<CFIdentifier>() {
			
			@Override
			public int compare(CFIdentifier o1, CFIdentifier o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		Iterator<CFIdentifier> attrIt = sorted.iterator();
		while (attrIt.hasNext()) {
			sb.append(" ");
			CFIdentifier nextKey = attrIt.next();
			sb.append(nextKey.Decompile(0));
			sb.append("=");
			sb.append(attributes.get(nextKey).Decompile(0));
		}
	}
	
	protected boolean containsAttribute(String _k) {
		for (CFIdentifier key : attributes.keySet()) {
			if (key.toString().equalsIgnoreCase(_k)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * checks that all the attributes are in the allowed set, throwing a ParseException if an unrecognized attribute is
	 * found
	 */
	protected void validateAttributes(Token _t, HashSet<String> _allowedKeys) {
		
		Iterator<CFIdentifier> it = attributes.keySet().iterator();
		
		while (it.hasNext()) {
			String nextKey = it.next().Decompile(0);
			if (!_allowedKeys.contains(nextKey)) {
				throw new ParseException(_t, "Invalid attribute " + nextKey);
			}
		}
		
	}
	
	/*
	 * checks that all the attributes are in the allowed set, throwing a CFException with the passed in message if an
	 * unrecognized attribute is found
	 */
	protected void validateAttributesRuntime(CFContext _context, HashSet<String> _allowedKeys, String _msg)
			throws CFException {
		Iterator<CFIdentifier> it = attributes.keySet().iterator();
		
		while (it.hasNext()) {
			String nextKey = it.next().Decompile(0);
			if (!_allowedKeys.contains(nextKey)) {
				throw new CFException(_msg, _context);
			}
		}
	}
	
	protected Iterator<CFIdentifier> getAttributeKeyIterator() {
		return attributes.keySet().iterator();
	}
}
