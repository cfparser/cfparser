lexer grammar  CFSCRIPTLexer;

START:
  -> skip, pushMode(DefaultMode);

mode DefaultMode;

//Note: need case insensitive stream: http://www.antlr.org/wiki/pages/viewpage.action?pageId=1782

WS	:	(' ' | '\t' | '\n' | '\r' | '\f' )+ ->skip;//-> channel(HIDDEN);

LINE_COMMENT :
            '//'
            ( ~('\n'|'\r') )*
            ( '\n'|'\r'('\n')? )?
      -> channel(HIDDEN) ;

JAVADOC : '/**' ~[*]+ '*/'
          {
            // create a new javadoc lexer/parser duo that feeds
            // off the current input stream
            //System.out.println("enter javadoc");
            //JavadocLexer j = new JavadocLexer(input);
            //CommonTokenStream tokens = new CommonTokenStream(j);
            //JavadocParser p = new JavadocParser(tokens);
            //p.comment();
            // returns a JAVADOC token to the java parser but on a
            // different channel than the normal token stream so it
            // doesn't get in the way.
            
          }
          -> channel(1) ;
ML_COMMENT
    :   '/*' (.)*? '*/' -> channel(HIDDEN)
    ;
BOOLEAN_LITERAL
	:	[tT][rR][uU][eE]
	|	[fF][aA][lL][sS][eE] 
	|	[yY][eE][sS] 
	|	[nN][oO] 
	;
OPEN_STRING
	: '"'
	-> pushMode(InDoubleQuotes)
;	
OPEN_STRING_SINGLE
	: '\''
	-> type(OPEN_STRING),pushMode(InSingleQuotes)
;

//STRING_LITERAL
//	: '"' DoubleStringCharacter* '"'
//	| '\'' SingleStringCharacter* '\''
//	; 

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


fragment CF_DIGIT 	
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
CONTAINS:	[cC][oO][nN][tT][aA][iI][nN][sS];
CONTAIN: [cC][oO][nN][tT][aA][iI][nN];
DOESNOTCONTAIN: [dD][oO][eE][sS][ ]+[nN][oO][tT][ ]+[cC][oO][nN][tT][aA][iI][nN];
IS:	[iI][sS] -> type(EQ);
IS_NOT:	[iI][sS][ ]+[nN][oO][tT] -> type(NEQ);
GT: [gG][tT];
GE: [gG][eE] -> type(GTE);
GTE: [gG][tT][eE];
LTE: [lL][tT][eE];
LT: [lL][tT];
LE: [lL][eE] -> type(GTE);
EQ: [eE][qQ];
EQUAL: [eE][qQ][uU][aA][lL] -> type(EQ);
EQUALS: [eE][qQ][uU][aA][lL][sS] -> type(EQ);
NOT_EQUALS: [nN][oO][tT][ ]+[eE][qQ][uU][aA][lL][sS] -> type(NEQ);
NEQ: [nN][eE][qQ];
LESS: [lL][eE][sS][sS];
LESS_THAN : [lL][eE][sS][sS][ ]+[tT][hH][aA][nN] -> type(LT);
GREATER_THAN : [gG][rR][eE][aA][tT][eE][rR][ ]+[tT][hH][aA][nN] -> type(GT);
LESSTHANOREQUALTO : [lL][eE][sS][sS][ ]+[tT][hH][aA][nN][ ]+[oO][rR][ ]+[eE][qQ][uU][aA][lL][ ]+[tT][oO] -> type(LTE);
GREATERTHANOREQUALTO : [gG][rR][eE][aA][tT][eE][rR][ ]+[tT][hH][aA][nN][ ]+[oO][rR][ ]+[eE][qQ][uU][aA][lL][ ]+[tT][oO] -> type(GTE);
GREATER: [gG][rR][eE][aA][tT][eE][rR];
OR: [oO][rR];
TO: [tT][oO];
IMP: [iI][mM][pP];
EQV: [eE][qQ][vV];
XOR: [xX][oO][rR];
AND: [aA][nN][dD];
NOT: [nN][oO][tT];
MOD: [mM][oO][dD];
VAR: [vV][aA][rR];
NEW: [nN][eE][wW];
// cfscript
IF: [iI][fF];
ELSE: [eE][lL][sS][eE];
BREAK: [bB][rR][eE][aA][kK];
CONTINUE: [cC][oO][nN][tT][iI][nN][uU][rE];
FUNCTION: [fF][uU][nN][cC][tT][iI][oO][nN];
RETURN: [rR][eE][tT][uU][rR][nN];
WHILE: [wW][hH][iI][lL][eE];
DO: [dD][oO];
FOR: [fF][oO][rR];
IN: [iI][nN];
TRY: [tT][rR][yY];
CATCH: [cC][aA][tT][cC][hH];
SWITCH: [sS][wW][iI][tT][cC][hH];
CASE: [cC][aA][sS][eE];
DEFAULT: [dD][eE][fF][aA][uU][lL][tT];
FINALLY: [fF][iI][nN][aA][lL][lL][yY];
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
MODOPERATOR: '%' -> type(MOD);
CONCAT: '&';
EQUALSEQUALSOP: '==' -> type(EQ);
EQUALSOP: '=';
PLUSEQUALS: '+=';
MINUSEQUALS: '-=';
STAREQUALS: '*=';
SLASHEQUALS: '/=';
MODEQUALS: '%=';
CONCATEQUALS: '&=';
COLON: ':';
NOTNOTOP: '!!'; 
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
INCLUDE: [iI][nN][cC][lL][uU][dD][eE];
IMPORT: [iI][mM][pP][oO][rR][tT];
ABORT: [aA][bB][oO][rR][tT];
THROW: [tT][hH][rR][oO][wW];
RETHROW: [rR][eE][tT][hH][rR][oO][wW];
EXIT: [eE][xX][iI][tT];
PARAM: [pP][aA][rR][aA][mM];
PROPERTY: [pP][rR][oO][pP][eE][rR][tT][yY];
LOCK: [lL][oO][cC][kK];
THREAD: [tT][hH][rR][eE][aA][dD];
TRANSACTION: [tT][rR][aA][nN][sS][aA][cC][tT][iI][oO][nN];
// cfmlfunction (tags you can call from script)
SAVECONTENT: [sS][aA][vV][eE][cC][oO][nN][tT][eE][nN][tT];
HTTP: [hH][tT][tT][pP][sS]?([:][/][/])?;
FILE: [fF][iI][lL][eE];
DIRECTORY: [dD][iI][rR][eE][cC][tT][oO][rR][yY];
LOOP: [lL][oO][oO][pP]; 
SETTING: [sS][eE][tT][tT][iI][nN][gG];
QUERY: [qQ][uU][eE][rR][yY];
//types
STRING: [sS][tT][rR][iI][nN][gG];
NUMERIC: [nN][uU][mM][eE][rR][iI][cC];
BOOLEAN: [bB][oO][oO][lL][eE][aA][nN];
ANY: [aA][nN][yY];
ARRAY: [aA][rR][rR][aA][yY];
STRUCT: [sS][tT][rR][uU][cC][tT];
// function related
PRIVATE: [pP][rR][iI][vV][aA][tT][eE];
PUBLIC: [pP][uU][bB][lL][iI][cC];
REMOTE: [rR][eE][mM][oO][tT][eE];
PACKAGE: [pP][aA][cC][kK][aA][gG][eE];
REQUIRED: [rR][eE][qQ][uU][iI][rR][eE][dD];
COMPONENT: [cC][oO][mM][pP][oO][nN][eE][nN][tT];

//SCOPE
//    :   [tT][hH][iI][sS] DOT
//    |   [lL][oO][cC][aA][lL] DOT
//    |   [vV][aA][rR][iI][aA][bB][lL][eE][sS] DOT
//    ;
// -- tags as script
LOG: [lL][oO][gG];
//TEXT: [tT][eE][xX][tT];
//TYPE: [tT][yY][pP][eE];
//APPLICATION: [aA][pP][pP][lL][iI][cC][aA][tT][iI][oO][nN];

APPLET: [Aa][Pp][Pp][Ll][Ee][Tt];
ASSOCIATE: [Aa][Ss][Ss][Oo][Cc][Ii][Aa][Tt][Ee];
AUTHENTICATE: [Aa][Uu][Tt][Hh][Ee][Nn][Tt][Ii][Cc][Aa][Tt][Ee];
CACHE: [Cc][Aa][Cc][Hh][Ee];
COL: [Cc][Oo][Ll];
COLLECTION: [Cc][Oo][Ll][Ll][Ee][Cc][Tt][Ii][Oo][Nn];
CONTENT: [Cc][Oo][Nn][Tt][Ee][Nn][Tt];
COOKIE: [Cc][Oo][Oo][Kk][Ii][Ee];
ERROR: [Ee][Rr][Rr][Oo][Rr];
EXECUTE: [Ee][Xx][Ee][Cc][Uu][Tt][Ee];
FORM: [Ff][Oo][Rr][Mm];
FTP: [Ff][Tt][Pp];
GRID: [Gg][Rr][Ii][Dd];
GRIDCOLUMN: [Gg][Rr][Ii][Dd][Cc][Oo][Ll][Uu][Mm][Nn];
GRIDROW: [Gg][Rr][Ii][Dd][Rr][Oo][Ww];
GRIDUPDATE: [Gg][Rr][Ii][Dd][Uu][Pp][Dd][Aa][Tt][Ee];
HEADER: [Hh][Ee][Aa][Dd][Ee][Rr];
HTMLHEAD: [Hh][Tt][Mm][Ll][Hh][Ee][Aa][Dd];
HTTPPARAM: [Hh][Tt][Tt][Pp][Pp][Aa][Rr][Aa][Mm];
IMPERSONATE: [Ii][Mm][Pp][Ee][Rr][Ss][Oo][Nn][Aa][Tt][Ee];
INDEX: [Ii][Nn][Dd][Ee][Xx];
INPUT: [Ii][Nn][Pp][Uu][Tt];
INSERT: [Ii][Nn][Ss][Ee][Rr][Tt];
LDAP: [Ll][Dd][Aa][Pp];
LOCATION: [Ll][Oo][Cc][Aa][Tt][Ii][Oo][Nn];
MAIL: [Mm][Aa][Ii][Ll];
MAILPARAM: [Mm][Aa][Ii][Ll][Pp][Aa][Rr][Aa][Mm];
MODULE: [Mm][Oo][Dd][Uu][Ll][Ee];
OBJECT: [Oo][Bb][Jj][Ee][Cc][Tt];
OUTPUT: [Oo][Uu][Tt][Pp][Uu][Tt];
POP: [Pp][Oo][Pp];
PROCESSINGDIRECTIVE: [Pp][Rr][Oo][Cc][Ee][Ss][Ss][Ii][Nn][Gg][Dd][Ii][Rr][Ee][Cc][Tt][Ii][Vv][Ee];
PROCPARAM: [Pp][Rr][Oo][Cc][Pp][Aa][Rr][Aa][Mm];
PROCRESULT: [Pp][Rr][Oo][Cc][Rr][Ee][Ss][Uu][Ll][Tt];
QUERYPARAM: [Qq][Uu][Ee][Rr][Yy][Pp][Aa][Rr][Aa][Mm];
REGISTRY: [Rr][Ee][Gg][Ii][Ss][Tt][Rr][Yy];
REPORT: [Rr][Ee][Pp][Oo][Rr][Tt];
SCHEDULE: [Ss][Cc][Hh][Ee][Dd][Uu][Ll][Ee];
SCRIPT: [Ss][Cc][Rr][Ii][Pp][Tt];
SEARCH: [Ss][Ee][Aa][Rr][Cc][Hh];
SELECT: [Ss][Ee][Ll][Ee][Cc][Tt];
SERVLET: [Ss][Ee][Rr][Vv][Ll][Ee][Tt];
SERVLETPARAM: [Ss][Ee][Rr][Vv][Ll][Ee][Tt][Pp][Aa][Rr][Aa][Mm];
SET: [Ss][Ee][Tt];
SILENT: [Ss][Ii][Ll][Ee][Nn][Tt];
SLIDER: [Ss][Ll][Ii][Dd][Ee][Rr];
STOREDPROC: [Ss][Tt][Oo][Rr][Ee][Dd][Pp][Rr][Oo][Cc];
TABLE: [Tt][Aa][Bb][Ll][Ee];
TEXTINPUT: [Tt][Ee][Xx][Tt][Ii][Nn][Pp][Uu][Tt];
TREE: [Tt][Rr][Ee][Ee];
TREEITEM: [Tt][Rr][Ee][Ee][Ii][Tt][Ee][Mm];
UPDATE: [Uu][Pp][Dd][Aa][Tt][Ee];
WDDX: [Ww][Dd][Dd][Xx];



IDENTIFIER 
	:	LETTER (LETTER|CF_DIGIT)*;
	
INTEGER_LITERAL
  : DecimalDigit+
  ;
  
POUND_SIGN_1: '#' {_modeStack.contains(HashMode)}? -> type(POUND_SIGN),popMode,popMode;
POUND_SIGN: '#';
LESSTHAN: '<' -> type(LT);
LESSTHANEQUALS: '<=' -> type(LTE);
GREATERTHAN: '>' -> type(GT);
GREATERTHANEQUALS: '>=' -> type(GTE);
NOTEQUALS: '!=' -> type(NEQ);
COMMA: ',';
  
  
fragment DecimalDigit
  : ('0'..'9')
  ;
FLOATING_POINT_LITERAL
  : DecimalDigit+ '.' DecimalDigit* ExponentPart?
  | '.' DecimalDigit+ ExponentPart
  | DecimalDigit+ ExponentPart?
  ;
fragment ExponentPart
  : [eE] [+-]? DecimalDigit+
  ;
  
  mode InDoubleQuotes;
CLOSE_STRING
	: '"'
	-> popMode
;	
DOUBLEHASH
	: '##' 
;
STRING_LITERAL
	: ~["#]+
;
HASH
	: '#' -> type(POUND_SIGN),pushMode(HashMode),pushMode(DefaultMode)
;

mode InSingleQuotes;
CLOSE_STRING_SINGLE
	: '\''
	-> type(CLOSE_STRING),popMode
;	
DOUBLEHASH_SINGLE
	: '##' -> type(DOUBLEHASH)
;
STRING_LITERAL_SINGLE
	: ~['#]+ -> type(STRING_LITERAL)
;
HASH_SINGLE
	: '#' -> type(POUND_SIGN),pushMode(HashMode),pushMode(DefaultMode)
;

mode HashMode;
HashMode_ANY:  -> popMode,skip;
 