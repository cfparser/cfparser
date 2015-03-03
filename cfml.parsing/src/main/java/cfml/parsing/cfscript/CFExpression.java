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

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import cfml.CFSCRIPTLexer;
import cfml.CFSCRIPTParser;
import cfml.CFSCRIPTParser.ExpressionContext;
import cfml.CFSCRIPTParser.ScriptBlockContext;

public abstract class CFExpression extends CFParsedStatement implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	public static byte FUNCTION = 0, ASSIGNMENT = 1, BINARY = 2, LITERAL = 3, IDENTIFIER = 4, VARIABLE = 5, UNARY = 6,
			ARRAYMEMBER = 7, NESTED = 8;
	
	@Deprecated
	public static CFExpression getCFExpression(String _infix) {
		try {
			return getCFExpressionThrows(_infix);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static CFExpression getCFExpressionThrows(String _infix) throws Exception {
		
		ANTLRInputStream input = new ANTLRInputStream(_infix);
		CFSCRIPTLexer lexer = new CFSCRIPTLexer(input);
		TokenStream tokens = new CommonTokenStream(lexer);
		
		ScriptBlockContext scriptStatement = null;
		CFSCRIPTParser parser = new CFSCRIPTParser(tokens);
		
		// lexer.addErrorListener(errorReporter);
		// parser.addErrorListener(errorReporter);
		// p2.scriptMode = false;
		ExpressionContext exp = parser.expression();
		
		// if (exp instanceof CFAssignmentExpression) {
		// ((CFAssignmentExpression) exp).checkIndirect(_infix);
		// }
		// TODO
		return null;
		
	}
	
	public byte getType() {
		return -1;
	}
	
	public boolean isEscapeSingleQuotes() {
		return false;
	}
	
	public CFExpression(org.antlr.v4.runtime.Token t) {
		super(t);
	}
	
}
