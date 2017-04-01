parser grammar  CFSCRIPTParser; 

options { tokenVocab=CFSCRIPTLexer; }

//Note: needs case insensitive stream: http://www.antlr.org/wiki/pages/viewpage.action?pageId=1782

scriptBlock
  :
  importStatement*
  componentDeclaration
  | ( element )*
  | cfscriptBlock*
  | EOF
  ;

cfscriptBlock
  : SCRIPTOPEN scriptBlock SCRIPTCLOSE
  ;

componentDeclaration
  : COMPONENT componentAttribute* componentGuts //-> ( COMPDECL componentAttribute* componentGuts)
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
  : (type
  | multipartIdentifier) array?
  | stringLiteral
  ;
  
array
  : LEFTBRACKET RIGHTBRACKET
  ; 
  
stringLiteral
  :  OPEN_STRING (stringLiteralPart | POUND_SIGN startExpression POUND_SIGN)* CLOSE_STRING;

stringLiteralPart
  :  STRING_LITERAL | DOUBLEHASH;
  

parameterList
  : parameter ( COMMA parameter)*
  |
  ;
  
parameter
  : (REQUIRED)? (parameterType)? name=identifier ( EQUALSOP startExpression )? parameterAttribute* //-> ^(FUNCTION_PARAMETER (REQUIRED)? (parameterType)? identifier (EQUALSOP baseExpression)? parameterAttribute*)
  ;
  
parameterType
  : typeSpec //-> ^( PARAMETER_TYPE typeSpec )
  ;

componentAttribute
  : id=identifier //-> ^(COMPONENT_ATTRIBUTE identifier)
  | (prefix=identifier COLON)? id=identifier op=EQUALSOP startExpression //-> ^(COMPONENT_ATTRIBUTE identifier (COLON identifier)? baseExpression)
  ;
//i=identifier EQUALSOP^ v=baseExpression
   
functionAttribute
  : identifierWithColon op=EQUALSOP (value=identifier | valueString=constantExpression)
  | id=identifier ( op=(EQUALSOP|COLON) (value=identifier | valueString=constantExpression) )?
  ;

identifierWithColon
: identifier COLON identifier
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
         //Semicolon OR look for a newline as the next token (in the hidden channel)
  |   continueStatement endOfStatement
  |   breakStatement endOfStatement
  |   returnStatement endOfStatement
  |   tagOperatorStatement
  |   compoundStatement 
  |   localAssignmentExpression endOfStatement
  |   assignmentExpression endOfStatement
  |   startExpression SEMICOLON
  |   SEMICOLON // empty statement
  | functionCall // without semi
  ;
  
endOfStatement
   :
   {_input.get(_input.LT(-1).getTokenIndex()+1).getType()==NEWLINE}?
     semicolon = SEMICOLON?
   | semicolon = SEMICOLON; 
  
breakStatement 
  :BREAK ;
  
continueStatement 
  :CONTINUE ;  
   
condition
  : LEFTPAREN compareExpression RIGHTPAREN
  ;
  
returnStatement
  : RETURN (startExpression | assignmentExpression)?
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
  : CATCH LEFTPAREN typeSpec? identifier RIGHTPAREN compoundStatement
  ;

finallyStatement
  : FINALLY compoundStatement
  ;
  
constantExpression
  : LEFTPAREN constantExpression RIGHTPAREN
  | MINUS? ( INTEGER_LITERAL | floatingPointExpression  )
  | stringLiteral
  | BOOLEAN_LITERAL
  ;
  
switchStatement
  : SWITCH condition LEFTCURLYBRACKET
    ( 
      caseStatement    
    )* 
    
    RIGHTCURLYBRACKET
  ;

caseStatement
  : ( CASE (constantExpression|memberExpression) COLON statement*) 
    | 
    ( DEFAULT COLON statement* ) 
  ;

tagOperatorStatement
  : includeStatement
  | importStatement
  | abortStatement
  | adminStatement
  | throwStatement
  | rethrowStatment 
  | exitStatement
  | paramStatement
  | propertyStatement
  | lockStatement
  | threadStatement
  | transactionStatement
  | cfmlfunctionStatement
  | tagFunctionStatement
  ; 
  
rethrowStatment:
  lc=RETHROW SEMICOLON ;

includeStatement
  : lc=INCLUDE baseExpression (paramStatementAttributes)? SEMICOLON  
  ;

importStatement
  : lc=IMPORT componentPath (DOT all=STAR)? SEMICOLON
  ;

transactionStatement
  : lc=TRANSACTION (paramStatementAttributes)? (compoundStatement)? 
  ;
  
cfmlfunctionStatement
  : cfmlFunction (paramStatementAttributes)? (compoundStatement | SEMICOLON) 
  ;

tagFunctionStatement
  : cfmlFunction (LEFTPAREN parameterList RIGHTPAREN)? (compoundStatement | SEMICOLON)?
  ;

cfmlFunction
  : SAVECONTENT
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
  | HTTP
  | CFHTTP
  | HTTPPARAM
  | CFHTTPPARAM
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
  | ZIP
  | CFCUSTOM_IDENTIFIER
  ;

lockStatement
  : lc=LOCK p=paramStatementAttributes cs=compoundStatement 
  ;

threadStatement
  : lc=THREAD p=paramStatementAttributes (compoundStatement | SEMICOLON) 
  ;

abortStatement
  : lc=ABORT SEMICOLON 
  | lc=ABORT memberExpression SEMICOLON 
  ;
  
adminStatement
  : lc=ADMIN p=paramStatementAttributes SEMICOLON 
  ;

throwStatement
  : lc=THROW SEMICOLON 
  | lc=THROW stringLiteral SEMICOLON 
  | lc=THROW memberExpression SEMICOLON 
  ;

exitStatement
  : lc=EXIT SEMICOLON 
  | lc=EXIT memberExpression SEMICOLON //-> ^(EXITSTATEMENT[$lc] memberExpression)
  ;

paramStatement
  : lc=PARAM (paramStatementAttributes | paramExpression) SEMICOLON //-> ^(PARAMSTATEMENT[$lc] paramStatementAttributes)
  ;
  
paramExpression
  : type? multipartIdentifier EQUALSOP startExpression
  ;
propertyStatement
  : lc=PROPERTY paramStatementAttributes SEMICOLON //-> ^(PROPERTYSTATEMENT[$lc] paramStatementAttributes)
  | lc=PROPERTY typeSpec? name=multipartIdentifier SEMICOLON
  ;
  
paramStatementAttributes
  : ( param )+
  ;
  
param
  : i=multipartIdentifier EQUALSOP startExpression

  ;


//--- expression engine grammar rules (a subset of the cfscript rules)
  
expression 
	: localAssignmentExpression EOF
	|	assignmentExpression EOF
	|   startExpression EOF
	;
	
localAssignmentExpression 
	:	VAR left=startExpression ( (EQUALSOP otherIdentifiers)* EQUALSOP right=startExpression )? //-> ^( VARLOCAL identifier ( EQUALSOP baseExpression )? )
	;
	
otherIdentifiers:
VAR? otherid=identifier;

assignmentExpression 
  :  left=startExpression
     ( ( (EQUALSOP (identifier EQUALSOP)*) | PLUSEQUALS | MINUSEQUALS | STAREQUALS | SLASHEQUALS | MODEQUALS | CONCATEQUALS )
     	right = startExpression
     )
  ;

ternaryExpression
    : QUESTIONMARK ternaryExpression1=startExpression COLON ternaryExpression2=startExpression
    ;

baseExpression
	:
	notExpression
	| notNotExpression
	| concatenationExpression
	| additiveExpression
	| modExpression
	| intDivisionExpression
	| multiplicativeExpression
	| powerOfExpression
	| elvisExpression
	| anonymousFunctionDeclaration
	| unaryExpression
	| baseExpression ternaryExpression
	;
	
elvisExpression:
	unaryExpression QUESTIONMARK COLON baseExpression;
	
compareExpression
	: (
	left=baseExpression 
		(operator=compareExpressionOperator right=compareExpression)?
	)
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
    |   DOESNOTCONTAIN
 ;
	

notExpression
	:	( NOT | NOTOP ) startExpression
	;
	
notNotExpression
	:	NOTNOTOP  startExpression 
	;	
equalityOperator1
    :
       EQ //-> ^(EQ)
    |   LT //-> ^(LT)
    |   LTE //-> ^(LTE)
    |   GT //-> ^(GT)
    |   GTE //-> ^(GTE)
    |   NEQ //-> ^(NEQ)
    |   CONTAINS //-> ^(CONTAINS)
    ;
    
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
	: (MINUS | PLUS) primaryExpression //-> ^(MINUS memberExpression)
	| (MINUSMINUS | PLUSPLUS) unaryExpression
  | memberExpression
  | innerExpression
  | unaryExpression (MINUSMINUS | PLUSPLUS)
  | primaryExpression//-> ^(POSTMINUSMINUS memberExpression)
  ;

innerExpression:
	POUND_SIGN baseOrTernaryExpression POUND_SIGN;
	
memberExpression
  : MINUS? ( //primaryExpression
    functionCall
  	| newComponentExpression
    | firstidentifier=identifier
  	| parentheticalExpression//-> primaryExpression
  ) // set return tree to just primary
  ( 
    DOT+ functionCall
    | arrayMemberExpression parentheticalMemberExpression?
    | DOT+ primaryExpressionIRW 
    | DOT+ identifier
  )*
  ;
  
identifierOrReservedWord:
identifier | reservedWord;

arrayMemberExpression
	:LEFTBRACKET startExpression RIGHTBRACKET 
	;

functionCall
	:(identifier | specialWord) LEFTPAREN argumentList RIGHTPAREN
	body=compoundStatement?
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
	:	stringLiteral
	|	BOOLEAN_LITERAL
	|  INTEGER_LITERAL
	| implicitArray
	| implicitStruct
	| reservedWord
	;
	
literalExpression
	:	stringLiteral
	|	BOOLEAN_LITERAL
	|  floatingPointExpression
	|  INTEGER_LITERAL;
	
floatingPointExpression
    : FLOATING_POINT_LITERAL
    | left=INTEGER_LITERAL? DOT right=INTEGER_LITERAL
    | leftonly=INTEGER_LITERAL DOT;
	
reservedWord
  : specialWord 
  | cfscriptKeywords 
  ;
specialWord
  : CONTAINS 
  | EQ | NEQ | GT | LT | GTE
  | LTE | NOT | AND
  | OR | XOR | EQV | IMP | MOD
  ;

argumentList
  : argument (COMMA argument)*
  | //-> ^(EMPTYARGS)
  ;

argument
  : ( (name=argumentName) COLON startExpression //-> ^( COLON identifier baseExpression ) 
  )
  | ( (name=argumentName) EQUALSOP startExpression //-> ^( COLON identifier baseExpression ) 
  )
  | startExpression 
  | anonymousFunctionDeclaration
  ;
  
argumentName
  : identifier | stringLiteral
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
  | ADMIN
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
  : implicitStructKeyExpression ( COLON | EQUALSOP ) compareExpression //unaryExpression
  ;
  
implicitStructKeyExpression
  : multipartIdentifier
 // | additiveExpression ( CONCAT additiveExpression )*
  | literalExpression
  | reservedWord
  ;

newComponentExpression
  : NEW componentPath LEFTPAREN argumentList RIGHTPAREN
  ;
  
componentPath
  : stringLiteral
  | identifier
  | multipartIdentifier
  ;