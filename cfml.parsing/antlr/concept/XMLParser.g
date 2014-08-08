parser grammar XMLParser;

options {       
  tokenVocab=XMLLexer;
  output=AST;
  ASTLabelType=CommonTree;
}

tokens {
TAG;
TRY;
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

@parser::header {
package cfml.parsing.cfml.antlr;

import java.util.LinkedList;
}

@parser::members {
  protected boolean isColdFusionTag(String name)
  {   
    boolean isColdfusion = name.toLowerCase().startsWith("cf");
    System.out.println("isColdFusion: " + name + " : " + isColdfusion);
    return isColdfusion;
  }

  protected boolean isAssignmentTag(String name)
  {   
    boolean isSingle = name.toLowerCase().startsWith("cfset");
    System.out.println("isColdFusion: " + name + " : " + isSingle);
    return isSingle;
  }

  protected boolean isSingleTag(String name)
  {   
    boolean isSingle = name.toLowerCase().startsWith("cf");
    System.out.println("isColdFusion: " + name + " : " + isSingle);
    return isSingle;
  }


}

compilationUnit : tag;

tag: element*;

element
scope ElementScope;
    : ( (startTag^)
            (element
            | PCDATA
            )*
            endTag!
        | emptyElement
        )
    ;

startTag
    : el=TAG_START_OPEN name=GENERIC_ID attribute* TAG_CLOSE
            {$ElementScope::currentElementName = $name.text;}
            { System.out.println("current Tag:"+$name.text); }
        -> ^(ELEMENT[$name] attribute*)

    ; 

attribute : aname=GENERIC_ID ATTR_EQ ATTR_VALUE -> ^(ATTRIBUTE[$aname] ATTRIBUTENAME[$aname] ATTR_VALUE) ;

endTag!
      
    : {
    System.out.println(input.LT(1).getText() + input.LT(1).getType());
    System.out.println(input.LT(2).getText()+ input.LT(2).getType());
    System.out.println(input.LT(3).getText()+ input.LT(3).getType());} 
      { $ElementScope::currentElementName.equals(input.LT(2).getText())}?
      TAG_END_OPEN GENERIC_ID
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
