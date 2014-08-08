parser grammar DenParser;

options {       
  tokenVocab=DenLexer;
  output=AST;
  ASTLabelType=CommonTree;
}

tokens {
 ELEMENT;
 ATTRIBUTE;
}

scope ElementScope {
  String currentElementName;
}

@parser::header {
package cfml.parsing.cfml.antlr;

import java.util.LinkedList;
}

document : tags ;

tags : (ifStatement | element);

element
scope ElementScope;
    : ( startTag^
            (element
            | PCDATA
            )*
            endTag!
        | emptyElement
        )
    ;

ifStatement
    : TAG_START_OPEN CFIF attribute* TAG_CLOSE
            {$ElementScope::currentElementName = $CFIF.text; }
        -> ^(ELEMENT CFIF attribute*)
    ; 


startTag
    : TAG_START_OPEN GENERIC_ID attribute* TAG_CLOSE
            {$ElementScope::currentElementName = $GENERIC_ID.text; }
        -> ^(ELEMENT GENERIC_ID attribute*)
    ; 

attribute : GENERIC_ID ATTR_EQ ATTR_VALUE -> ^(ATTRIBUTE GENERIC_ID ATTR_VALUE) ;

endTag!
    : { $ElementScope::currentElementName.equals(input.LT(2).getText()) }?
      TAG_END_OPEN GENERIC_ID TAG_CLOSE
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

emptyElement : TAG_START_OPEN GENERIC_ID attribute* TAG_EMPTY_CLOSE
        -> ^(ELEMENT GENERIC_ID attribute*)
    ;
    