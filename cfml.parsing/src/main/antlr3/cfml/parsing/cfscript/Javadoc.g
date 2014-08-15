grammar Javadoc;

@lexer::header {
package cfml.parsing.cfscript;
}

@parser::header {
package cfml.parsing.cfscript;
}

comment : ( author )* ;

author : '@author' ID {System.out.println("author "+$ID.text);} ;

ID : ('a'..'z'|'A'..'Z')+
        ;


/** When the javadoc parser sees end-of-comment it just says 'I'm done', which
 * consumes the tokens and forces this javadoc parser (feeding
 * off the input stream currently) to exit. It returns from
 * method comment(), which was called from JAVADOC action in the
 * Simple parser's lexer.
 */
END : '*/' {emit(Token.EOF_TOKEN);}
          {System.out.println("exit javadoc");}
        ;

WS : (' '|'\t'|'\n')+ {$channel=HIDDEN;}
        ;

