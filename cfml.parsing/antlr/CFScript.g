/* 
 *  Copyright (C) 2000 - 2010 TagServlet Ltd
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

grammar CFScript;

options {
	output=AST;
	ASTLabelType=CommonTree;
	backtrack=true;
	memoize=true;
}
// imaginary tokens
tokens {
  DOESNOTCONTAIN; // 'does not contain' operator
  VARLOCAL; // local assignment
  FUNCTIONCALL; // function call
  JAVAMETHODCALL; // java method call
  EMPTYARGS; // empty list of arguments
  TERNARY; // ternary operator
  
  COMPDECL; // component declaration
  FUNCDECL; // function declaration
  POSTMINUSMINUS; // '--' post-expression
  POSTPLUSPLUS; // '++' post-expression
  
  IMPLICITSTRUCT; // implicit struct 
  IMPLICITARRAY; // implicit struct
  CONCATSTRUCTKEY; // concat "#wee##hoo#" = wonk
  
  ABORTSTATEMENT; // abort statement
  EXITSTATEMENT; // exit statement
  PARAMSTATEMENT;  // param statement
  PROPERTYSTATEMENT;  // property statement
  THROWSTATEMENT; // throw statement
  RETHROWSTATEMENT; // rethrow statement
  LOCKSTATEMENT; // lock statement
  THREADSTATEMENT; // thread statement
  TRANSACTIONSTATEMENT; // thread statement
  
  CFMLFUNCTIONSTATEMENT; // for calling functions in script (savecontent variable="wee" {} )

  FUNCTION_NAME; // function name (identifier token types may vary, thus this specific type)
  FUNCTION_ACCESS; // function access
  FUNCTION_PARAMETER; // function parameter
  FUNCTION_RETURNTYPE; // function return type
  FUNCTION_ATTRIBUTE; // the attributes of the function 
  PARAMETER_ATTRIBUTE; // the attributes of the parameter
  COMPONENT_ATTRIBUTE; // the attributes of the component 
  PARAMETER_TYPE; // function parameter type
}

@parser::header { package cfml.parsing.cfscript;}

@lexer::header { package cfml.parsing.cfscript;}
 
@members { public boolean scriptMode = true; 

  private IErrorReporter errorReporter = null;
  public void setErrorReporter(IErrorReporter errorReporter) {
      this.errorReporter = errorReporter;
  }
  public void emitErrorMessage(String msg) {
      errorReporter.reportError(msg);
  }


/*
	
	public String getErrorMessage(RecognitionException e,
	                                  String[] tokenNames)
	    {
	        List stack = getRuleInvocationStack(e, this.getClass().getName());
	        String msg = null;
	        String inputContext =
	            input.LT(-3) == null ? "" : ((CommonToken)input.LT(-3)).getText()+" "+
	            input.LT(-2) == null ? "" : ((CommonToken)input.LT(-2)).getText()+" "+
	            input.LT(-1) == null ? "" : ((CommonToken)input.LT(-1)).getText()+" >>>"+
	            ((CommonToken)input.LT(1)).getText()+"<<< "+
	            ((CommonToken)input.LT(2)).getText()+" "+
	            ((CommonToken)input.LT(3)).getText();
	        if ( e instanceof NoViableAltException ) {
	           NoViableAltException nvae = (NoViableAltException)e;
	           msg = " no viable alt; token="+e.token+
	              " (decision="+nvae.decisionNumber+
	              " state "+nvae.stateNumber+")"+
	              " decision=<<"+nvae.grammarDecisionDescription+">>";
	        }
	        else {
	           msg = super.getErrorMessage(e, tokenNames);
	        }
	        return stack+" "+msg+" context=..."+inputContext+"...";
	    }
	    public String getTokenErrorDisplay(Token t) {
	        return t.toString();
	    }
*/
	protected void mismatch( IntStream input, int ttype, BitSet follow ) throws RecognitionException {
	  throw new MismatchedTokenException(ttype, input);
	}
		
	public Object recoverFromMismatchedSet( IntStream input, RecognitionException e, BitSet follow ) throws RecognitionException{
	  throw e;
	}
	
	public Object recoverFromMismatchedToken( IntStream input, int ttype, BitSet follow ) throws RecognitionException{
	  RecognitionException e = null;
	  if ( mismatchIsUnwantedToken(input, ttype) ) {
	    e = new UnwantedTokenException(ttype, input);
	  }else if ( mismatchIsMissingToken(input, follow) ) {
	    Object inserted = getMissingSymbol(input, e, ttype, follow);
	    e = new MissingTokenException(ttype, input, inserted);
	  }else{
	    e = new MismatchedTokenException(ttype, input);
	  }
	  //TODO: get different token names
	  throw new CFParseException( this.getErrorMessage( e, this.getTokenNames() ), e );
	}
}


@lexer::members {

  public static final int JAVADOC_CHANNEL = 1;
  public Token nextToken() {
    if ( state.token != null && state.token.getType() == SCRIPTCLOSE ){
      return Token.EOF_TOKEN;
    }
    
    while (true) {
      state.token = null;
      state.channel = Token.DEFAULT_CHANNEL;
      state.tokenStartCharIndex = input.index();
      state.tokenStartCharPositionInLine = input.getCharPositionInLine();
      state.tokenStartLine = input.getLine();
      state.text = null;
      if ( input.LA(1)==CharStream.EOF ) {
        return Token.EOF_TOKEN;
      }
      try {
        mTokens();
        if ( state.token==null ) {
          emit();
        }
        else if ( state.token==Token.SKIP_TOKEN ) {
          continue;
        }
        return state.token;
      } catch (NoViableAltException nva) {
                errorReporter.reportError(nva);
                recover(nva); // throw out current char and try again
      }
      catch (RecognitionException re) {
        errorReporter.reportError(re);
        return Token.EOF_TOKEN;
        //throw new RuntimeException("Bailing out!"); // or throw Error
      }
    }
  } 

  private IErrorReporter errorReporter = null;
  public void setErrorReporter(IErrorReporter errorReporter) {
      this.errorReporter = errorReporter;
  }
  public void emitErrorMessage(String msg) {
      errorReporter.reportError("from lex" + msg);
  }
  
}

// Alter code generation so catch-clauses get replace with
// this action.
@parser::rulecatch {
	catch (RecognitionException e) {
	  //System.out.println("cfscript.g");
	  if(e != null) {
		  errorReporter.reportError(e); 
		  recover(getTokenStream(),e);
	  } else {
      System.out.println("null!");
	  }
	}
}


//Note: need case insensitive stream: http://www.antlr.org/wiki/pages/viewpage.action?pageId=1782

WS	:	(' ' | '\t' | '\n' | '\r' | '\f' )+ {$channel=HIDDEN;};

LINE_COMMENT :
            '//'
            ( ~('\n'|'\r') )*
            ( '\n'|'\r'('\n')? )?
      { $channel=HIDDEN; } ;

JAVADOC : '/**'
          {
            // create a new javadoc lexer/parser duo that feeds
            // off the current input stream
            System.out.println("enter javadoc");
            JavadocLexer j = new JavadocLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(j);
            JavadocParser p = new JavadocParser(tokens);
            p.comment();
            // returns a JAVADOC token to the java parser but on a
            // different channel than the normal token stream so it
            // doesn't get in the way.
            $channel = JAVADOC_CHANNEL;
          }
        ;


ML_COMMENT
    :   '/*' (options {greedy=false;} : .)* '*/' {$channel=HIDDEN;}
    ;

BOOLEAN_LITERAL
	:	'TRUE'
	|	'FALSE' 
	;

STRING_LITERAL
	: '"' DoubleStringCharacter* '"'
	| '\'' SingleStringCharacter* '\''
	;
 
fragment DoubleStringCharacter
	: ~('"')
	| '""'	
	;

fragment SingleStringCharacter
	: ~('\'')
	| '\'\''	
	;

fragment LETTER	
	: '\u0024'
	| '\u0041'..'\u005a'
	| '\u005f'
	| '\u0061'..'\u007a'
	| '\u00c0'..'\u00d6'
	| '\u00d8'..'\u00f6'
	| '\u00f8'..'\u00ff'
	| '\u0100'..'\u1fff'
	| '\u3040'..'\u318f'
	| '\u3300'..'\u337f'
	| '\u3400'..'\u3d2d'
	| '\u4e00'..'\u9fff'
	| '\uf900'..'\ufaff';


fragment DIGIT 	
	: '\u0030'..'\u0039'
	| '\u0660'..'\u0669'
	| '\u06f0'..'\u06f9'
	| '\u0966'..'\u096f'
	| '\u09e6'..'\u09ef'
	| '\u0a66'..'\u0a6f'
	| '\u0ae6'..'\u0aef'
	| '\u0b66'..'\u0b6f'
	| '\u0be7'..'\u0bef'
	| '\u0c66'..'\u0c6f'
	| '\u0ce6'..'\u0cef'
	| '\u0d66'..'\u0d6f'
	| '\u0e50'..'\u0e59'
	| '\u0ed0'..'\u0ed9'
	| '\u1040'..'\u1049';

// define all the operators/reserved words before the identifier

// not sure why we'd need null, it is not an identifier for CFML, removing
//NULL: 'NULL';

// Operators
CONTAINS:	'CONTAINS';
CONTAIN: 'CONTAIN';
DOES: 'DOES';
IS:	'IS';
GT: 'GT';
GE: 'GE';
GTE: 'GTE';
LTE: 'LTE';
LT: 'LT';
LE: 'LE';
EQ: 'EQ';
EQUAL: 'EQUAL';
EQUALS: 'EQUALS';
NEQ: 'NEQ';
LESS: 'LESS';
THAN: 'THAN';
GREATER: 'GREATER';
OR: 'OR';
TO: 'TO';
IMP: 'IMP';
EQV: 'EQV';
XOR: 'XOR';
AND: 'AND';
NOT: 'NOT';
MOD: 'MOD';
VAR: 'VAR';
NEW: 'NEW';

// cfscript
IF: 'IF';
ELSE: 'ELSE';
BREAK: 'BREAK';
CONTINUE: 'CONTINUE';
FUNCTION: 'FUNCTION';
RETURN: 'RETURN';
WHILE: 'WHILE';
DO: 'DO';
FOR: 'FOR';
IN: 'IN';
TRY: 'TRY';
CATCH: 'CATCH';
SWITCH: 'SWITCH';
CASE: 'CASE';
DEFAULT: 'DEFAULT';
FINALLY: 'FINALLY';

SCRIPTCLOSE: '</CFSCRIPT>'; 

// operators
DOT: '.';
STAR: '*';
SLASH: '/';
BSLASH: '\\';
POWER: '^';
PLUS: '+';
PLUSPLUS: '++';
MINUS: '-';
MINUSMINUS: '--';
MODOPERATOR: '%';
CONCAT: '&';
EQUALSEQUALSOP: '==';
EQUALSOP: '=';
PLUSEQUALS: '+=';
MINUSEQUALS: '-=';
STAREQUALS: '*=';
SLASHEQUALS: '/=';
MODEQUALS: '%=';
CONCATEQUALS: '&=';
COLON: ':';
NOTOP: '!'; 
SEMICOLON: ';';
OROPERATOR: '||';
ANDOPERATOR: '&&';
LEFTBRACKET: '[';
RIGHTBRACKET: ']';
LEFTPAREN: '(';
RIGHTPAREN: ')';
LEFTCURLYBRACKET: '{';
RIGHTCURLYBRACKET: '}';
QUESTIONMARK: '?';

// tag operators
INCLUDE: 'INCLUDE';
IMPORT: 'IMPORT';
ABORT: 'ABORT';
THROW: 'THROW';
RETHROW: 'RETHROW';
EXIT: 'EXIT';
PARAM: 'PARAM';
PROPERTY: 'PROPERTY';
LOCK: 'LOCK';
THREAD: 'THREAD';
TRANSACTION: 'TRANSACTION';

// cfmlfunction (tags you can call from script)
SAVECONTENT: 'SAVECONTENT';
HTTP: 'HTTP';
FILE: 'FILE';
DIRECTORY: 'DIRECTORY';
LOOP: 'LOOP'; 
SETTING: 'SETTING';
QUERY: 'QUERY';

//types
STRING: 'STRING';
NUMERIC: 'NUMERIC';
BOOLEAN: 'BOOLEAN';
ANY: 'ANY';
ARRAY: 'ARRAY';
STRUCT: 'STRUCT';

// function related
PRIVATE: 'PRIVATE';
PUBLIC: 'PUBLIC';
REMOTE: 'REMOTE';
PACKAGE: 'PACKAGE';
REQUIRED: 'REQUIRED';
COMPONENT: 'COMPONENT';

IDENTIFIER 
	:	LETTER (LETTER|DIGIT)*;
	
INTEGER_LITERAL
  : DecimalDigit+
  ;


fragment DecimalDigit
  : ('0'..'9')
  ;

FLOATING_POINT_LITERAL
  : DecimalDigit+ '.' DecimalDigit* ExponentPart?
  | '.' DecimalDigit+ ExponentPart?
  | DecimalDigit+ ExponentPart?
  ;

fragment ExponentPart
  : ('e'|'E') ('+'|'-')? DecimalDigit+
  ;
 
//--- cfscript grammar rules

scriptBlock
  : componentDeclaration
  | ( element )* endOfScriptBlock
  ; 

componentDeclaration
  : COMPONENT componentAttribute* componentGuts -> ^( COMPDECL componentAttribute* componentGuts)
  ;

endOfScriptBlock
  : SCRIPTCLOSE 
  | EOF
  ;
  
element
  : functionDeclaration
  | statement
  ;

functionDeclaration
  : (functionAccessType)? (functionReturnType)? lc=FUNCTION identifier LEFTPAREN (parameterList)? RIGHTPAREN functionAttribute* compoundStatement -> ^( FUNCDECL[$lc] (functionAccessType)? (functionReturnType)? ^(FUNCTION_NAME identifier) (parameterList)? functionAttribute* compoundStatement )
  ;

functionAccessType
  //: (PUBLIC | PRIVATE | REMOTE | PACKAGE) (functionReturnType|identifier)
  : (accessType functionReturnType? FUNCTION identifier) => accessType -> ^(FUNCTION_ACCESS accessType) 
//  : ((PUBLIC | PRIVATE | REMOTE | PACKAGE) functionReturnType? FUNCTION identifier) => lc=(PUBLIC | PRIVATE | REMOTE | PACKAGE -> ^(FUNCTION_ACCESS[$lc])
  ;

functionReturnType
  : (typeSpec FUNCTION) => typeSpec -> ^( FUNCTION_RETURNTYPE typeSpec )
  ;

accessType
	:PUBLIC | PRIVATE | REMOTE | PACKAGE
	;

typeSpec
  : type
  | identifier ( DOT ( identifier | reservedWord ) )*
  | STRING_LITERAL
  ;
  
parameterList
  : parameter ( ','! parameter)*
  |
  ;
  
parameter
  : (REQUIRED)? (parameterType)? identifier ( EQUALSOP impliesExpression )? parameterAttribute* -> ^(FUNCTION_PARAMETER (REQUIRED)? (parameterType)? identifier (EQUALSOP impliesExpression)? parameterAttribute*)
  ;
  
parameterType
  : typeSpec -> ^( PARAMETER_TYPE typeSpec )
  ;

componentAttribute
  : identifier (COLON identifier)? op=EQUALSOP impliesExpression -> ^(COMPONENT_ATTRIBUTE identifier (COLON identifier)? impliesExpression)
  ;
//i=identifier EQUALSOP^ v=impliesExpression
  
functionAttribute
  : identifier op=EQUALSOP impliesExpression -> ^(FUNCTION_ATTRIBUTE[$op] identifier impliesExpression)
  ;
  
parameterAttribute
  : identifier EQUALSOP impliesExpression -> ^(PARAMETER_ATTRIBUTE identifier impliesExpression)
  | identifier
  ;
  
compoundStatement
  : LEFTCURLYBRACKET^ ( statement )* RIGHTCURLYBRACKET
  ;
  
componentGuts
  : LEFTCURLYBRACKET^ ( element )* RIGHTCURLYBRACKET
  ;
  
statement
  :   tryCatchStatement
  |   ifStatement
  |   whileStatement
  |   doWhileStatement
  |   forStatement
  |   switchStatement
  |   CONTINUE SEMICOLON!
  |   BREAK SEMICOLON!
  |   returnStatement
  |   tagOperatorStatement
  |   compoundStatement 
  |   localAssignmentExpression SEMICOLON!
//  |   localAssignmentExpression
  |   SEMICOLON! // empty statement
  ;
   
condition
  : LEFTPAREN! localAssignmentExpression RIGHTPAREN!
  ;
  
returnStatement
  : RETURN SEMICOLON!
  | RETURN assignmentExpression SEMICOLON!
  ;
  
ifStatement
  : IF^ condition statement ( ELSE statement )?
  ;

whileStatement
  : WHILE^ condition statement
  ;
 
doWhileStatement
  : DO^ statement WHILE condition SEMICOLON
  ;
  
forStatement
  : FOR^ LEFTPAREN! ( localAssignmentExpression )? SEMICOLON ( assignmentExpression )? SEMICOLON  ( assignmentExpression )? RIGHTPAREN! statement
  | FOR^ LEFTPAREN! forInKey IN assignmentExpression RIGHTPAREN! statement
  ;
  
forInKey
  : VAR? identifier ( DOT ( identifier | reservedWord ) )*
  ;

tryCatchStatement
  : TRY^ statement ( catchCondition )* finallyStatement?
  ;
  
catchCondition
  : CATCH^ LEFTPAREN! typeSpec identifier RIGHTPAREN! compoundStatement
  ;

finallyStatement
  : FINALLY^ compoundStatement
  ;
  
constantExpression
  : LEFTPAREN constantExpression RIGHTPAREN
  | MINUS ( INTEGER_LITERAL | FLOATING_POINT_LITERAL  )
  | INTEGER_LITERAL
  | FLOATING_POINT_LITERAL
  | STRING_LITERAL
  | BOOLEAN_LITERAL
//  | NULL
  ;
  
switchStatement
  : SWITCH^ condition LEFTCURLYBRACKET
    ( 
      caseStatement    
    )* 
    
    RIGHTCURLYBRACKET
  ;

caseStatement
  : ( CASE^ constantExpression COLON ( statement )* ) 
    | 
    ( DEFAULT^ COLON ( statement )* ) 
  ;

tagOperatorStatement
  //: INCLUDE^ compoundStatement SEMICOLON!  (poundSignReader kills this :-/)
  : includeStatement
  | importStatement
  | abortStatement
  | throwStatement
  | RETHROW SEMICOLON -> ^(RETHROWSTATEMENT)
  | exitStatement
  | paramStatement
  | propertyStatement
  | lockStatement
  | threadStatement
  | transactionStatement
  | cfmlfunctionStatement
  ;

// component  

includeStatement
  : INCLUDE impliesExpression* SEMICOLON  -> ^(INCLUDE  impliesExpression* ) 
  ;

importStatement
  : IMPORT^ componentPath (DOT '*')? SEMICOLON! 
  ;

transactionStatement
  : lc=TRANSACTION (paramStatementAttributes)* (compoundStatement)? -> ^(TRANSACTIONSTATEMENT[$lc] (paramStatementAttributes)* (compoundStatement)?)
  ;
  
cfmlfunctionStatement
  : cfmlFunction (param)* (compoundStatement)?-> ^(CFMLFUNCTIONSTATEMENT cfmlFunction (param)* (compoundStatement)?)
  ;
  
cfmlFunction
  : SAVECONTENT
  | HTTP 
  | FILE 
  | PROPERTY
  | DIRECTORY
  | LOOP 
  | SETTING
  | QUERY
  ;

/*

cfmlfunctionStatement
  : savecontentStatement
  ;

savecontentStatement
  : lc=SAVECONTENT p=paramStatementAttributes cs=compoundStatement -> ^(CFMLFUNCTIONSTATEMENT[$lc] paramStatementAttributes compoundStatement)
  ;
*/

lockStatement
  : lc=LOCK p=paramStatementAttributes cs=compoundStatement -> ^(LOCKSTATEMENT[$lc] paramStatementAttributes compoundStatement)
  ;

threadStatement
  : lc=THREAD p=paramStatementAttributes (compoundStatement)? -> ^(THREADSTATEMENT[$lc] paramStatementAttributes (compoundStatement)?)
  ;

abortStatement
  : lc=ABORT SEMICOLON -> ^(ABORTSTATEMENT[$lc])
  | lc=ABORT memberExpression SEMICOLON -> ^(ABORTSTATEMENT[$lc] memberExpression)
  ;

throwStatement
  : lc=THROW SEMICOLON -> ^(THROWSTATEMENT[$lc])
  | lc=THROW memberExpression SEMICOLON -> ^(THROWSTATEMENT[$lc] memberExpression)
  ;

exitStatement
  : lc=EXIT SEMICOLON -> ^(EXITSTATEMENT[$lc])
  | lc=EXIT memberExpression SEMICOLON -> ^(EXITSTATEMENT[$lc] memberExpression)
  ;

paramStatement
  : lc=PARAM paramStatementAttributes  -> ^(PARAMSTATEMENT[$lc] paramStatementAttributes)
  ;
  
propertyStatement
  : lc=PROPERTY paramStatementAttributes  -> ^(PROPERTYSTATEMENT[$lc] paramStatementAttributes)
  ;
  
paramStatementAttributes
  : ( param )+
  ;
  
param
  : i=identifier EQUALSOP^ v=impliesExpression
  ;


//--- expression engine grammar rules (a subset of the cfscript rules)
  
expression 
	: localAssignmentExpression EOF!
	;
	
localAssignmentExpression 
	:	VAR identifier ( EQUALSOP impliesExpression )? -> ^( VARLOCAL identifier ( EQUALSOP impliesExpression )? ) 
	|	assignmentExpression
	;

assignmentExpression 
  : impliesExpression ( ( EQUALSOP | PLUSEQUALS | MINUSEQUALS | STAREQUALS | SLASHEQUALS | MODEQUALS | CONCATEQUALS )^ impliesExpression )?
  ;

impliesExpression
	:	ternary
	| equivalentExpression ( IMP^ equivalentExpression )*
	;

ternary
//   : equivalentExpression QUESTIONMARK localAssignmentExpression COLON localAssignmentExpression -> ^(IF equivalentExpression QUESTIONMARK localAssignmentExpression COLON localAssignmentExpression)
   : equivalentExpression QUESTIONMARK localAssignmentExpression COLON localAssignmentExpression -> ^(TERNARY equivalentExpression localAssignmentExpression localAssignmentExpression)
   ;

equivalentExpression
	:	xorExpression ( EQV^ xorExpression )*
	;

xorExpression
	:	orExpression ( XOR^ orExpression )*
	;
	
orExpression
	:	andExpression ( ( OR | OROPERATOR )^ andExpression )*
	;
	
andExpression
	:	notExpression ( ( AND | ANDOPERATOR )^ notExpression )*
	;
	
notExpression
	:	( NOT^ | NOTOP^ )? equalityExpression 
	;

equalityExpression
    : concatenationExpression
      ( ( equalityOperator5^ | equalityOperator3^ |  equalityOperator2^ | equalityOperator1^ ) concatenationExpression )* 
    ;

equalityOperator1
    : 	IS -> ^(EQ)
    |   EQUALSEQUALSOP -> ^(EQ)
    |   LT -> ^(LT)
    |   '<' -> ^(LT)
    |   LTE -> ^(LTE)
    |   '<=' -> ^(LTE)
    |   LE -> ^(LTE)
    |   GT -> ^(GT)
    |   '>' -> ^(GT)
    |   GTE -> ^(GTE)
    |   '>=' -> ^(GTE)
    |   GE -> ^(GTE)
    |   EQ -> ^(EQ)
    |   NEQ -> ^(NEQ)
    |   '!=' -> ^(NEQ)
    |   EQUAL -> ^(EQ)
    |   EQUALS -> ^(EQ)
    |   CONTAINS -> ^(CONTAINS)
    ;
    
equalityOperator2
    :   LESS THAN -> ^(LT)
    |   GREATER THAN -> ^(GT)
    |   NOT EQUAL  -> ^(NEQ)
    |   IS NOT -> ^(NEQ)
    ;

equalityOperator3
    :   lc=DOES NOT CONTAIN -> ^(DOESNOTCONTAIN[$lc])
    ;

equalityOperator5
    :   LESS THAN OR EQUAL TO -> ^(LTE)
    |   GREATER THAN OR EQUAL TO -> ^(GTE)
    ;
    
concatenationExpression
	:	additiveExpression ( CONCAT^ additiveExpression )*
	;
	
additiveExpression
	:	modExpression ( (PLUS^|MINUS^) modExpression )*
	;

modExpression
	:	intDivisionExpression  ( (MOD|MODOPERATOR)^ intDivisionExpression )* 
	;
	
intDivisionExpression
	:	multiplicativeExpression ( BSLASH^ multiplicativeExpression )*
	;

multiplicativeExpression
	:	powerOfExpression ( (STAR^|SLASH^) powerOfExpression )*
	;
	
powerOfExpression
	:	unaryExpression ( POWER^ unaryExpression )*
	;
	
unaryExpression
	: MINUS memberExpression -> ^(MINUS memberExpression)
	| PLUS memberExpression -> ^(PLUS memberExpression)
	| MINUSMINUS memberExpression -> ^(MINUSMINUS memberExpression) 
	| PLUSPLUS memberExpression -> ^(PLUSPLUS memberExpression)
	| newComponentExpression (DOT primaryExpressionIRW (LEFTPAREN argumentList ')')*)*
  | memberExpression MINUSMINUS -> ^(POSTMINUSMINUS memberExpression)
  | memberExpression PLUSPLUS -> ^(POSTPLUSPLUS memberExpression)
  | memberExpression 
	;
	
memberExpression
	:	'#'! memberExpressionB '#'!
	| memberExpressionB
	;
	
memberExpressionB
  : ( primaryExpression -> primaryExpression ) // set return tree to just primary
  ( 
  : DOT primaryExpressionIRW LEFTPAREN argumentList ')' -> ^(JAVAMETHODCALL $memberExpressionB primaryExpressionIRW argumentList )
    |  LEFTPAREN argumentList RIGHTPAREN -> ^(FUNCTIONCALL $memberExpressionB argumentList)
    | LEFTBRACKET impliesExpression RIGHTBRACKET -> ^(LEFTBRACKET $memberExpressionB impliesExpression)
    | DOT primaryExpressionIRW -> ^(DOT $memberExpressionB primaryExpressionIRW)
  )*
  ;
  


memberExpressionSuffix
  : indexSuffix
  | propertyReferenceSuffix
  ;

propertyReferenceSuffix
  : DOT LT!* identifier
  ;

indexSuffix
  : LEFTBRACKET  LT!* primaryExpression  LT!* ']'! 
  ; 
  
primaryExpressionIRW
	:	primaryExpression
	| reservedWord
	;
	
	
reservedWord
  : CONTAINS | IS | EQUAL 
  | EQ | NEQ | GT | LT | GTE
  | GE | LTE | LE | NOT | AND
  | OR | XOR | EQV | IMP | MOD
//  | NULL 
  | EQUALS
  | cfscriptKeywords 
  ;

argumentList
  : argument (','! argument)*
  | -> ^(EMPTYARGS)
  ;

argument
  : ( identifier COLON impliesExpression -> ^( COLON identifier impliesExpression ) )
  | ( identifier EQUALSOP impliesExpression -> ^( COLON identifier impliesExpression ) )
  | impliesExpression 
  ;

identifier
	:	COMPONENT
	| IDENTIFIER
  | DOES 
  | CONTAIN
  | GREATER 
  | THAN 
  | LESS 
  | VAR
  | TO
  | DEFAULT // default is a cfscript keyword that's always allowed as a var name
  | INCLUDE
  | NEW
  | ABORT
  | THROW
  | RETHROW
  | PARAM
  | EXIT
  | THREAD
  | LOCK
  | TRANSACTION
  | PUBLIC
  | PRIVATE
  | REMOTE
  | PACKAGE
  | REQUIRED
  | cfmlFunction
  | {!scriptMode}?=> cfscriptKeywords 
	;

type
  : NUMERIC
  | STRING
  | BOOLEAN
  | COMPONENT
  | ANY
  | ARRAY
  | STRUCT
  ;

cfscriptKeywords
  : IF
  | ELSE
  | BREAK
  | CONTINUE
  | FUNCTION
  | RETURN
  | WHILE
  | DO
  | FOR
  | IN
  | TRY
  | CATCH
  | FINALLY
  | SWITCH
  | CASE
  | DEFAULT
  | IMPORT
  ;
  
primaryExpression
	:	STRING_LITERAL
	|	BOOLEAN_LITERAL
	| FLOATING_POINT_LITERAL
	|	INTEGER_LITERAL
	| implicitArray
	| implicitStruct
//	|	NULL
	| '('! LT!* assignmentExpression LT!* ')'!
	|	identifier
	;

implicitArray
  : lc=LEFTBRACKET implicitArrayElements? RIGHTBRACKET -> ^(IMPLICITARRAY[$lc] implicitArrayElements?) 
  ;
  
implicitArrayElements
  : impliesExpression ( ','! impliesExpression )*
  ;
  
implicitStruct
  : lc=LEFTCURLYBRACKET implicitStructElements? RIGHTCURLYBRACKET -> ^(IMPLICITSTRUCT[$lc] implicitStructElements?)
  ;
  
implicitStructElements
  : implicitStructExpression ( ',' implicitStructExpression )*
  ;

implicitStructExpression
  : implicitStructKeyExpression ( COLON | EQUALSOP )^ impliesExpression 
  ;
  
implicitStructKeyExpression
  : identifier ( DOT ( identifier | reservedWord ) )*
  | additiveExpression ( CONCAT^ additiveExpression )*
  | STRING_LITERAL
  ;

newComponentExpression
  : NEW^ componentPath LEFTPAREN argumentList ')'!
  ;
  
componentPath
  : STRING_LITERAL
  | identifier ( DOT identifier )*
  ;
