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
package cfml.parsing.cfscript;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

public class CFFullVarExpression extends CFVarExpression implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	// private Token token;
	private List<CFExpression> expressions;
	
	public CFFullVarExpression(Token _t, CFExpression _main) {
		super(_t);
		// token = _t;
		expressions = new ArrayList<CFExpression>();
		expressions.add(_main);
	}
	
	public CFIdentifier getIdentifier() {
		return (CFIdentifier) expressions.get(0);
	}
	
	public String getScope() {
		return getIdentifier().getScope();
	}
	
	public byte getType() {
		return expressions.get(expressions.size() - 1).getType();
	}
	
	public boolean isEscapeSingleQuotes() {
		return expressions.get(expressions.size() - 1).isEscapeSingleQuotes();
	}
	
	public void addMember(CFIdentifier _right) {
		expressions.add(_right);
	}
	
	public void addMember(CFMember _right) {
		expressions.add(_right);
	}
	
	public void addMember(CFExpression _right) {
		if (_right instanceof CFIdentifier || _right instanceof CFMember)
			expressions.add(_right);
		else
			throw new RuntimeException("Not CFIdentifier nor CFMember");
	}
	
	public String Decompile(int indent) {
		StringBuilder sb = new StringBuilder();
		for (CFExpression expression : expressions) {
			if (sb.length() > 0
					&& (expression.getType() == CFExpression.IDENTIFIER || expression.getType() == CFExpression.FUNCTION)) {
				sb.append(".");
			}
			sb.append(expression.Decompile(0));
		}
		return sb.toString();
	}
	
	public List<CFExpression> getExpressions() {
		return expressions;
	}
	
}
