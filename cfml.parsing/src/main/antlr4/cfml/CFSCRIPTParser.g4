parser grammar  CFSCRIPTParser; 

options { tokenVocab=CFSCRIPTLexer; }


//Note: need case insensitive stream: http://www.antlr.org/wiki/pages/viewpage.action?pageId=1782

	
////--- cfscript grammar rules

scriptBlock
  : 
  importStatement*
  componentDeclaration
  | ( element )* endOfScriptBlock
  ; 

componentDeclaration
  : COMPONENT componentAttribute* componentGuts //-> ( COMPDECL componentAttribute* componentGuts)
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
  : accessType? typeSpec? FUNCTION identifier 
  	LEFTPAREN parameterList? RIGHTPAREN 
  	functionAttribute* body=compoundStatement 
  ;
anonymousFunctionDeclaration
  : accessType? typeSpec? FUNCTION //identifier? 
  	LEFTPAREN parameterList? RIGHTPAREN 
  	functionAttribute* body=compoundStatement 
  ;

accessType
	:PUBLIC | PRIVATE | REMOTE | PACKAGE
	;

typeSpec
  : type
  | multipartIdentifier
  | stringLiteral
  ;
  
stringLiteral
  :  STRING_LITERAL;
  
 
parameterList
  : parameter ( COMMA parameter)*
  |
  ;
  
parameter
  : (REQUIRED)? (parameterType)? identifier ( EQUALSOP startExpression )? parameterAttribute* //-> ^(FUNCTION_PARAMETER (REQUIRED)? (parameterType)? identifier (EQUALSOP baseExpression)? parameterAttribute*)
  ;
  
parameterType
  : typeSpec //-> ^( PARAMETER_TYPE typeSpec )
  ;

componentAttribute
  : (prefix=identifier COLON)? id=identifier op=EQUALSOP startExpression //-> ^(COMPONENT_ATTRIBUTE identifier (COLON identifier)? baseExpression)
  ;
//i=identifier EQUALSOP^ v=baseExpression
   
functionAttribute
  : identifier op=EQUALSOP startExpression //-> ^(FUNCTION_ATTRIBUTE[$op] identifier baseExpression)
  ;
  
parameterAttribute
  : identifier EQUALSOP startExpression //-> ^(PARAMETER_ATTRIBUTE identifier baseExpression)
  | identifier
  ;
  
compoundStatement
  : LEFTCURLYBRACKET ( statement )* RIGHTCURLYBRACKET
  ;
  
componentGuts
  : LEFTCURLYBRACKET ( element )* RIGHTCURLYBRACKET
  ;
  
statement
  :   tryCatchStatement
  |   ifStatement
  |   whileStatement
  |   doWhileStatement
  |   forStatement
  |   switchStatement
  |   continueStatement SEMICOLON
  |   breakStatement SEMICOLON
  |   returnStatement
  |   tagOperatorStatement
  |   compoundStatement 
  |   localAssignmentExpression SEMICOLON
  |   assignmentExpression SEMICOLON
  |   startExpression SEMICOLON
//  |   tagEquivalent
  |   SEMICOLON // empty statement
  ;
  
//tagEquivalent
//  :   logTag
//  ;
//  
//logTag
//  :   LOG
//  	  ( log_Text | log_File | log_Type | log_Application | log_Log)*
//  	  SEMICOLON
//  ;
//  
//log_Text:   TEXT EQUALSOP stringLiteral;
//log_File:   FILE EQUALSOP startExpression;
//log_Type:   TYPE EQUALSOP stringLiteral;
//log_Application:   APPLICATION EQUALSOP stringLiteral;
//log_Log:   LOG EQUALSOP stringLiteral;
  
breakStatement 
  :BREAK ;
  
continueStatement 
  :CONTINUE ;  
   
condition
  : LEFTPAREN compareExpression RIGHTPAREN
  ;
  
returnStatement
  : RETURN SEMICOLON
  | RETURN startExpression SEMICOLON
  ;
  
ifStatement
  : IF condition statement ( ELSE statement )?
  ;

whileStatement
  : WHILE condition statement
  ;
 
doWhileStatement
  : DO statement WHILE condition SEMICOLON
  ;
  
forStatement
  : FOR LEFTPAREN ( localAssignmentExpression | initExpression=assignmentExpression )? SEMICOLON 
  	  ( condExpression=startExpression )? SEMICOLON  
  	  ( incrExpression=startExpression | incrExpression2=assignmentExpression )? RIGHTPAREN statement
  | FOR LEFTPAREN forInKey IN inExpr=startExpression RIGHTPAREN statement
  ;
  
startExpression:
  compareExpression;
  
baseOrTernaryExpression:
  compareExpression;
  
forInKey
  : VAR? multipartIdentifier
  ;

tryCatchStatement
  : TRY statement ( catchCondition )* finallyStatement?
  ;
  
catchCondition
  : CATCH LEFTPAREN typeSpec identifier RIGHTPAREN compoundStatement
  ;

finallyStatement
  : FINALLY compoundStatement
  ;
  
constantExpression
  : LEFTPAREN constantExpression RIGHTPAREN
  | MINUS ( INTEGER_LITERAL | floatingPointExpression  )
  | INTEGER_LITERAL
  | floatingPointExpression
  | STRING_LITERAL
  | BOOLEAN_LITERAL
//  | NULL
  ;
  
switchStatement
  : SWITCH condition LEFTCURLYBRACKET
    ( 
      caseStatement    
    )* 
    
    RIGHTCURLYBRACKET
  ;

caseStatement
  : ( CASE constantExpression COLON statement*) 
    | 
    ( DEFAULT COLON statement* ) 
  ;

tagOperatorStatement
  //: INCLUDE^ compoundStatement SEMICOLON  (poundSignReader kills this :-/)
  : includeStatement
  | importStatement
  | abortStatement
  | throwStatement
  | rethrowStatment //-> ^(RETHROWSTATEMENT)
  | exitStatement
  | paramStatement
  | propertyStatement
  | lockStatement
  | threadStatement
  | transactionStatement
  | cfmlfunctionStatement
  ; 
  
rethrowStatment:
  lc=RETHROW SEMICOLON ;

// component  

includeStatement
  : lc=INCLUDE baseExpression SEMICOLON  //-> ^(INCLUDE  baseExpression* ) 
  ;

importStatement
  : lc=IMPORT componentPath (DOT all=STAR)? SEMICOLON 
  ;

transactionStatement
  : lc=TRANSACTION (paramStatementAttributes)? (compoundStatement)? //-> ^(TRANSACTIONSTATEMENT[$lc] (paramStatementAttributes)* (compoundStatement)?)
  ;
  
cfmlfunctionStatement
  : cfmlFunction (paramStatementAttributes)? (compoundStatement | SEMICOLON)//-> ^(CFMLFUNCTIONSTATEMENT cfmlFunction (param)* (compoundStatement)?)
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
  | LOG
  | APPLET
  | ASSOCIATE
  | AUTHENTICATE
  | CACHE
  | COL
  | COLLECTION
  | CONTENT
  | COOKIE
  | ERROR
  | EXECUTE
  | FORM
  | FTP
  | GRID
  | GRIDCOLUMN
  | GRIDROW
  | GRIDUPDATE
  | HEADER
  | HTMLHEAD
  | HTTPPARAM
  | IMPERSONATE
  | INDEX
  | INPUT
  | INSERT
  | LDAP
  | LOCATION
  | MAIL
  | MAILPARAM
  | MODULE
  | OBJECT
  | OUTPUT
  | POP
  | PROCESSINGDIRECTIVE
  | PROCPARAM
  | PROCRESULT
  | QUERYPARAM
  | REGISTRY
  | REPORT
  | SCHEDULE
  | SCRIPT
  | SEARCH
  | SELECT
  | SERVLET
  | SERVLETPARAM
  | SET
  | SILENT
  | SLIDER
  | STOREDPROC
  | TABLE
  | TEXTINPUT
  | TREE
  | TREEITEM
  | UPDATE
  | WDDX
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
  : lc=LOCK p=paramStatementAttributes cs=compoundStatement //-> ^(LOCKSTATEMENT[$lc] paramStatementAttributes compoundStatement)
  ;

threadStatement
  : lc=THREAD p=paramStatementAttributes (compoundStatement | SEMICOLON) //-> ^(THREADSTATEMENT[$lc] paramStatementAttributes (compoundStatement)?)
  ;

abortStatement
  : lc=ABORT SEMICOLON //-> ^(ABORTSTATEMENT[$lc])
  | lc=ABORT memberExpression SEMICOLON //-> ^(ABORTSTATEMENT[$lc] memberExpression)
  ;

throwStatement
  : lc=THROW SEMICOLON //-> ^(THROWSTATEMENT[$lc])
  | lc=THROW memberExpression SEMICOLON //-> ^(THROWSTATEMENT[$lc] memberExpression)
  ;

exitStatement
  : lc=EXIT SEMICOLON //-> ^(EXITSTATEMENT[$lc])
  | lc=EXIT memberExpression SEMICOLON //-> ^(EXITSTATEMENT[$lc] memberExpression)
  ;

paramStatement
  : lc=PARAM paramStatementAttributes SEMICOLON //-> ^(PARAMSTATEMENT[$lc] paramStatementAttributes)
  ;
  
propertyStatement
  : lc=PROPERTY paramStatementAttributes SEMICOLON //-> ^(PROPERTYSTATEMENT[$lc] paramStatementAttributes)
  ;
  
paramStatementAttributes
  : ( param )+
  ;
  
param
  : i=identifier EQUALSOP startExpression
  ;


//--- expression engine grammar rules (a subset of the cfscript rules)
  
expression 
	: localAssignmentExpression EOF
	|	assignmentExpression EOF
	|   startExpression EOF
	;
	
localAssignmentExpression 
	:	VAR left=identifier ( (EQUALSOP otherIdentifiers)* EQUALSOP right=startExpression )? //-> ^( VARLOCAL identifier ( EQUALSOP baseExpression )? ) 
	;
	
otherIdentifiers:
VAR? otherid=identifier;

assignmentExpression 
  :  left=startExpression
     ( ( (EQUALSOP (identifier EQUALSOP)*) | PLUSEQUALS | MINUSEQUALS | STAREQUALS | SLASHEQUALS | MODEQUALS | CONCATEQUALS )  
     	right = startExpression
     )
  ;

baseExpression
	:	
/* 	| impliedExpression
	| equivalentExpression
	| xorExpression
	| orExpression
	| andExpression
	| notExpression
	| equalityExpression*/
	 concatenationExpression
	| additiveExpression
	| modExpression
	| intDivisionExpression
	| multiplicativeExpression
	| powerOfExpression
	| unaryExpression
	;
	
compareExpression
	: (
	notExpression
	| notNotExpression
	| left=baseExpression 
		(operator=compareExpressionOperator right=compareExpression)?
	) 
	(QUESTIONMARK ternaryExpression1=startExpression COLON ternaryExpression2=startExpression)?
	;	
	
compareExpressionOperator:
 OR 
 | OROPERATOR
 | EQV
 | XOR
 | AND
 | ANDOPERATOR
    |EQ //-> ^(EQ)
    |   LT //-> ^(LT)
    |   LTE //-> ^(LTE)
    |   GT //-> ^(GT)
    |   GTE //-> ^(GTE)
    |   NEQ //-> ^(NEQ)
    |   CONTAINS //-> ^(CONTAINS)
 ;
	
/*equivalentExpression
	:	baseExpression EQV startExpression 
	;

xorExpression
	:	baseExpression XOR startExpression 
	;
	
orExpression
	:	baseExpression  ( OR | OROPERATOR ) startExpression 
	;
	
andExpression
	:	baseExpression  ( AND | ANDOPERATOR ) startExpression 
	;
	*/
notExpression
	:	( NOT | NOTOP ) startExpression 
	;
	
notNotExpression
	:	NOTNOTOP  startExpression 
	;	
/*
equalityExpression
    : baseExpression
      ( equalityOperator1 startExpression )
    ;
*/
equalityOperator1
    :
    //IS //-> ^(EQ)
    //|   EQUALSEQUALSOP //-> ^(EQ)
       EQ //-> ^(EQ)
    |   LT //-> ^(LT)
    //|   LESSTHAN //-> ^(LT)
    |   LTE //-> ^(LTE)
    //|   LESSTHANEQUALS //-> ^(LTE)
    |   GT //-> ^(GT)
    |   GTE //-> ^(GTE)
    |   NEQ //-> ^(NEQ)
    //|   NOTEQUALS //-> ^(NEQ)
    //|   EQUAL //-> ^(EQ)
    //|   EQUALS //-> ^(EQ)
    |   CONTAINS //-> ^(CONTAINS)
    ;
    
/*equalityOperator2
    :   LESS THAN //-> ^(LT)
    |   GREATER THAN //-> ^(GT)
    //|   NOT EQUAL  //-> ^(NEQ)
    //|   IS NOT //-> ^(NEQ)
    ;
 
equalityOperator3 
    :   lc=DOES NOT CONTAIN //-> ^(DOESNOTCONTAIN[$lc])
    ;

equalityOperator5
    :   LESS THAN OR EQUAL TO //-> ^(LTE)
    |   GREATER THAN OR EQUAL TO //-> ^(GTE)
    ; */
     
concatenationExpression
	:	unaryExpression CONCAT baseOrTernaryExpression
	;
	
additiveExpression
	:	unaryExpression (PLUS|MINUS) baseOrTernaryExpression
	;

modExpression
	:	unaryExpression  ( MOD baseOrTernaryExpression )
	;
	
intDivisionExpression
	:	unaryExpression ( BSLASH baseOrTernaryExpression )
	;

multiplicativeExpression
	:	unaryExpression ( (STAR|SLASH) baseOrTernaryExpression )
	;
	
powerOfExpression
	:	unaryExpression ( POWER baseOrTernaryExpression )
	;
	
unaryExpression
	: (MINUS | PLUS | MINUSMINUS | PLUSPLUS) primaryExpression //-> ^(MINUS memberExpression)
	//| PLUS primaryExpression //-> ^(PLUS memberExpression)
	//| MINUSMINUS primaryExpression //-> ^(MINUSMINUS memberExpression) 
	//| PLUSPLUS primaryExpression //-> ^(PLUSPLUS memberExpression)
	//| identifier (DOT primaryExpressionIRW (LEFTPAREN argumentList RIGHTPAREN)+)*
  //| primaryExpression PLUSPLUS //-> ^(POSTPLUSPLUS memberExpression)
  //| primaryExpression
//  | parentheticalExpression
  | memberExpression
  | innerExpression
  |  primaryExpression (MINUSMINUS | PLUSPLUS)?//-> ^(POSTMINUSMINUS memberExpression)
  ;

innerExpression:
	POUND_SIGN baseOrTernaryExpression POUND_SIGN;
	
memberExpression
  : ( //primaryExpression
    functionCall
  	| newComponentExpression
    |firstidentifier=identifier 
  	| parentheticalExpression//-> primaryExpression 
  	
  ) // set return tree to just primary
  ( 
   //DOT javaCallMemberExpression //-> ^(JAVAMETHODCALL $memberExpressionB primaryExpressionIRW argumentList )
    DOT functionCall
    //| parentheticalMemberExpression //-> ^(FUNCTIONCALL $memberExpressionB argumentList)
    | arrayMemberExpression parentheticalMemberExpression?//-> ^(LEFTBRACKET $memberExpressionB baseExpression)
    | DOT primaryExpressionIRW //-> ^(DOT $memberExpressionB primaryExpressionIRW)
    | DOT identifier
    //| DOT identifierWithTypeKeyword
  )*
  ;
  
identifierOrReservedWord:
identifier | reservedWord;



  
arrayMemberExpression
	:LEFTBRACKET startExpression RIGHTBRACKET 
	;
  
functionCall
	:identifierOrReservedWord LEFTPAREN argumentList RIGHTPAREN
	;
	  
parentheticalMemberExpression
	:LEFTPAREN argumentList RIGHTPAREN 
	;
	
javaCallMemberExpression
	:primaryExpressionIRW LEFTPAREN argumentList RIGHTBRACKET 
	;	

memberExpressionSuffix
  : indexSuffix
  | propertyReferenceSuffix
  ;

propertyReferenceSuffix
  : DOT LT* identifier
  ;

indexSuffix
  : LEFTBRACKET  LT* (primaryExpression | parentheticalExpression) LT* RIGHTBRACKET 
  ; 
  
primaryExpressionIRW
	:	literalExpression
	| implicitArray
	| implicitStruct
	| reservedWord
	;
	
literalExpression
	:	STRING_LITERAL
	|	BOOLEAN_LITERAL
	|  floatingPointExpression
	|  INTEGER_LITERAL;
	
floatingPointExpression
    : FLOATING_POINT_LITERAL
    | DOT INTEGER_LITERAL;
	
reservedWord
  : CONTAINS //| IS | EQUAL | GE | LE | EQUALS  
  | EQ | NEQ | GT | LT | GTE
  | LTE | NOT | AND
  | OR | XOR | EQV | IMP | MOD
//  | NULL 
  | cfscriptKeywords 
  ;

argumentList
  : argument (COMMA argument)*
  | //-> ^(EMPTYARGS)
  ;

argument
  : ( name=identifier COLON startExpression //-> ^( COLON identifier baseExpression ) 
  )
  | ( name=identifier EQUALSOP startExpression //-> ^( COLON identifier baseExpression ) 
  )
  | startExpression 
  | anonymousFunctionDeclaration
  ;

multipartIdentifier
	:
		identifier (DOT identifierOrReservedWord)*;

identifier
	:	(COMPONENT
	| IDENTIFIER
  | CONTAIN
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
  | //{!scriptMode}?//=> 
  	cfscriptKeywords 
  | type	)
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
	:	literalExpression
	| implicitArray
	| implicitStruct
//	|	NULL
	|	identifier
	;
	
parentheticalExpression
	: LEFTPAREN startExpression RIGHTPAREN
;	

implicitArray
  : lc=LEFTBRACKET implicitArrayElements? RIGHTBRACKET //-> ^(IMPLICITARRAY[$lc] implicitArrayElements?) 
  ;
  
implicitArrayElements
  : startExpression ( COMMA startExpression )*
  ;
  
implicitStruct
  : lc=LEFTCURLYBRACKET implicitStructElements? RIGHTCURLYBRACKET //-> ^(IMPLICITSTRUCT[$lc] implicitStructElements?)
  ;
  
implicitStructElements
  : implicitStructExpression ( COMMA implicitStructExpression )*
  ;

implicitStructExpression
  : implicitStructKeyExpression ( COLON | EQUALSOP ) unaryExpression 
  ;
  
implicitStructKeyExpression
  : multipartIdentifier
 // | additiveExpression ( CONCAT additiveExpression )*
  | stringLiteral
  ;

newComponentExpression
  : NEW componentPath LEFTPAREN argumentList RIGHTPAREN
  ;
  
componentPath
  : stringLiteral
  | identifier
  | multipartIdentifier
  ;