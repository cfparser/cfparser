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

import org.antlr.v4.runtime.Token;

public class CFTernaryExpression extends CFAssignmentExpression implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	static private final int _ERR = 0;
	static private final int _NUM = 1;
	static private final int _STR = 2;
	static private final int _BOOL = 3;
	static private final int _REF = 4; // cfStructs, cfArrays
	static private final int _DATE = 5; // date ops
	
	// instance vars
	// private int _kind;
	private CFExpression _left;
	private CFExpression _right;
	// private String operatorImage;
	
	private CFExpression _cond;
	
	public CFTernaryExpression(Token t, CFExpression e1, CFExpression left, CFExpression right) {
		super(t, right, right);
		// _kind = t.getType();
		// operatorImage = t.getText();
		// if (_kind == CFSCRIPTLexer.ANDOPERATOR) {
		// _kind = CFSCRIPTLexer.AND;
		// } else if (_kind == CFSCRIPTLexer.OROPERATOR) {
		// _kind = CFSCRIPTLexer.OR;
		// } else if (_kind == CFSCRIPTLexer.MODOPERATOR) {
		// _kind = CFSCRIPTLexer.MOD;
		// }
		_cond = e1;
		_left = left;
		_right = right;
	}
	
	public byte getType() {
		return CFExpression.BINARY;
	}
	
	public String Decompile(int indent) {
		// String endChar = "";
		// if (_kind == CFSCRIPTLexer.LEFTBRACKET) {
		// endChar = "]";
		// }
		return _cond.Decompile(indent) + "?" + _left.Decompile(indent) + ":" + _right.Decompile(indent);
	}
	
}
