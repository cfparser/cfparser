/* 
 *  Copyright (C) 2000 - 2010 TagServlet Ltd
 *
 *  This file is part of Open BlueDragon (OpenBD) CFML Server Engine.
 *  
 *  OpenBD is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  Free Software Foundation,version 3.
 *  
 *  OpenBD is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with OpenBD.  If not, see http://www.gnu.org/licenses/
 *  
 *  Additional permission under GNU GPL version 3 section 7
 *  
 *  If you modify this Program, or any covered work, by linking or combining 
 *  it with any of the JARS listed in the README.txt (or a modified version of 
 *  (that library), containing parts covered by the terms of that JAR, the 
 *  licensors of this Program grant you additional permission to convey the 
 *  resulting work. 
 *  README.txt @ http://www.openbluedragon.org/license/README.txt
 *  
 *  http://www.openbluedragon.org/
 */
package cfml.parsing.cfscript.script;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
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
		Iterator<CFIdentifier> attrIt = attributes.keySet().iterator();
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
