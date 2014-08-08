grammar CF;


options {
  output=AST;
  ASTLabelType=CommonTree;
}
tokens {
TAG;
SINGLETAG;
TRY;
CFSETTAG;
CFIFTAG;
CFELSETAG;
 ASSIGN;
 ELEMENT;
 ATTRIBUTE;
 CFMLTAG;
 TAGNAME;
 ATTRIBUTENAME;
}

scope ElementScope {
  String currentElementName;
  int closerLine;
  int closerPosInLine;
}


@parser::header 
{
package cfml.parsing.cfml.antlr;
import java.util.LinkedList;

}

@lexer::header
{
package cfml.parsing.cfml.antlr;

import java.util.Set;

import cfml.dictionary.DictionaryManager;
import cfml.dictionary.SyntaxDictionary;
import cfml.dictionary.Tag;
import cfml.dictionary.preferences.DictionaryPreferences;

}



@lexer::members {
  private static final int TEXT_CHANNEL = 89;
boolean tagMode = false;
boolean internalTagMode = false;
  private static int NONE_MODE = 0;
  private static int ENDTAG_MODE = 1;
  private static int STARTTAG_MODE = 2;
  private static int DOUBLE_QUOTE_STRING_MODE = 3;
  private static int SINGLE_QUOTE_STRING_MODE = 4;
  private static int HASH_CFML_MODE = 5;
  //private static SyntaxDictionary cfdic;

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


TAG_START_OPEN : {  
//DictionaryManager.initDictionaries();
//cfdic = DictionaryManager.getDictionary("CF_DICTIONARY");
//Set<Tag> cfTags = cfdic.getAllTags();
}('<' CFTAG_ID name=ID { tagMode = true; }) 
{ 
if($name.text.equals("set")) {
	$type=SINGLETAG;
}
if($name.text.equals("if")) {
	//$type=CFIFTAG;
	System.out.println("iftag:"+$name.text+ " type:"+$name.type);
}

System.out.println("PCDATA:"+$name.text+ " type:"+$name.type); };

ATTR_EQ : { tagMode }?=> '=' ;  
fragment ATTR_VALUE : { tagMode }?=>
        STRING_LITERAL
                { System.out.println("PCDATA: string"); }
    ;

TAG_EMPTY_CLOSE : { tagMode }?=> '/' CLOSE_CHEVRON { tagMode = false; } ;
TAG_CLOSE : { tagMode }?=> CLOSE_CHEVRON { tagMode = false; } ;
TAG_END_OPEN : (OPEN_CHEVRON '/' CFTAG_ID name=ID { tagMode = true; } );

GENERIC_ID
    : { tagMode }?=>
      ( LETTER | '_' | ':') (NAMECHAR)*
    ;
    
STRING_LITERAL
  :{ tagMode }?=> ( '"' DoubleStringCharacter* '"'
  | '\'' SingleStringCharacter* '\'')
  ;
 
fragment DoubleStringCharacter
  : ~('"')
  | '""'  
  ;

fragment SingleStringCharacter
  : ~('\'')
  | '\'\''  
  ;


PCDATA : { !tagMode }?=> (~'<')+ ;
fragment CFSETDATA : { tagMode }?=> (~'>')+ ;

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

fragment
OPEN_CHEVRON:  '<'
    ;

fragment
CLOSE_CHEVRON:  '>'
    ;

fragment
SINGLETAG:  'placeholder'
    ;

fragment
CFIFTAG:  'placeholder'
    ;

fragment
CFELSETAG:  'placeholder'
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
OTHER
  :
  { !tagMode }?=>
  (options {greedy=false;} : . )
  {
    $channel=TEXT_CHANNEL; //test is on a seperate channel, in case you want it
  } 
  ;


compilationUnit : tag;

tag: element* | singleTag (singleTag|PCDATA|element)*;

element
scope ElementScope;
    :   startTag^
            (element
            | PCDATA
            )*
            endTag!
        | emptyElement
        
    ;



startTag
    : 
    (name=TAG_START_OPEN attribute* TAG_CLOSE
            {
      $ElementScope::currentElementName = $name.text;
            System.out.println("current Tag:"+$name.text);
            }
        -> ^(ELEMENT[$name] attribute*)
)
    ; 

singleTag
    : 
    (name=SINGLETAG attribute* (TAG_CLOSE |TAG_EMPTY_CLOSE)
            {
            System.out.println("current single Tag:"+$name.text);
            }
        -> ^(SINGLETAG[$name] attribute*)
);

cfifTag
    : 
    (name=CFIFTAG ID* TAG_CLOSE CFELSETAG? endTag
            {
            System.out.println("current single Tag:"+$name.text);
            }
        -> ^(CFIFTAG[$name] CFELSETAG?)
);

stringLiteral 
  :  STRING_LITERAL;

attribute : aname=GENERIC_ID ATTR_EQ stringLiteral -> ^(ATTRIBUTE[$aname] ATTRIBUTENAME[$aname] stringLiteral) ;

endTag!
      
    : { $ElementScope::currentElementName.equals(input.LT(2).getText())}?
      (TAG_END_OPEN TAG_CLOSE)
    ;
catch [FailedPredicateException fpe] {
    String hdr = getErrorHeader(fpe);
    String msg = "end tag (" + input.LT(2).getText() +
                 ") does not match start tag (" +
                 $ElementScope::currentElementName +
                 ") currently open, closing it anyway";
    emitErrorMessage(hdr+" "+msg);
    consumeUntil(input, TAG_CLOSE);
    input.consume();
}

emptyElement : el=TAG_START_OPEN attribute* TAG_EMPTY_CLOSE
        -> ^(ELEMENT[$el] attribute*)
    ;


