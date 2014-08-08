lexer grammar XMLLexer;

options {
	filter	= true;
}

@lexer::header {
package cfml.parsing.cfml.antlr;
}

@lexer::members {
boolean tagMode = false;
boolean internalTagMode = false;
  private static int NONE_MODE = 0;
  private static int ENDTAG_MODE = 1;
  private static int STARTTAG_MODE = 2;
  private static int DOUBLE_QUOTE_STRING_MODE = 3;
  private static int SINGLE_QUOTE_STRING_MODE = 4;
  private static int HASH_CFML_MODE = 5;

  private int mode;
  
  private int getMode()
  {
    return mode;
  }
  
  private void setMode(int mode)
  {
    this.mode = mode;
  }
}

//CF_TAG :  TAG_START_OPEN ATTR* TAG_END_OPEN;

TAG_START_OPEN : ('<' name=CFTAG_ID { tagMode = true; }) 
{ System.out.println("PCDATA:"+$name.text+ " type:"+$name.type); };
fragment ATTR : (at=GENERIC_ID ATTR_EQ av=ATTR_VALUE)
{ System.out.println("PCDATA:"+$at.text+" "+$av.text+" type:" + $av.type); };
fragment ATTR_EQ : { tagMode }?=> '=' ;  
fragment ATTR_VALUE : { tagMode }?=>
        STRING_LITERAL
                { System.out.println("PCDATA: string"); }
    ;

TAG_EMPTY_CLOSE : { tagMode }?=> '/>' { tagMode = false; } ;
TAG_CLOSE : { tagMode }?=> '>' { tagMode = false; } ;
TAG_END_OPEN : ('</' CFTAG_ID { tagMode = true; } );

GENERIC_ID
    : { tagMode }?=>
      ( LETTER | '_' | ':') (NAMECHAR)*
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


fragment PCDATA : { !tagMode }?=> (~'<')+ ;

fragment CFTAG_ID
    : 
      ('c'|'C') ('f'|'F')
    ;

fragment
ID  :   ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*
    ;
fragment
TRY :  'try'
    ;


fragment NAMECHAR
    : LETTER | DIGIT | '.' | '-' | '_' | ':'
    ;

fragment DIGIT
    :    '0'..'9'
    ;

fragment LETTER
    : ('a'..'z'|'A'..'Z')
    ;

WS  :  { tagMode }?=>
       (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=99;}
    ;
