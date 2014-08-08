grammar XML2;

options {    
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
import treetool.TreeBuilder;
import javax.swing.tree.DefaultMutableTreeNode;
}

@parser::members {
    private TreeBuilder T;
   // private DefaultMutableTreeNode oldNode;
    public void setTreeBuilder(TreeBuilder T){
        this.T = T;
    }
}

@lexer::header {
package cfml.parsing.cfml.antlr;
}

@lexer::members {
    boolean tagMode = false;
}

compilationUnit : tag;

tag: ELEMENT -> ^(ELEMENT)*;

    
ELEMENT
    : ( START_TAG
            (ELEMENT
            | t=PCDATA
                { System.out.println("PCDATA: \""+$t.getText()+"\""); }
            | t=CDATA
                { System.out.println("CDATA: \""+$t.getText()+"\""); }
            | t=COMMENT
                { System.out.println("Comment: \""+$t.getText()+"\""); }
            )*
            END_TAG
        
        )
    ;

START_TAG 
    : '<' WS? name=GENERIC_ID WS?
          { System.out.println("Start Tag: "+name.getText()); }
        ( ATTRIBUTE WS? )* '>'
    ;

EMPTY_ELEMENT 
    : '<' WS? name=GENERIC_ID WS?
          { System.out.println("Empty Element: "+name.getText()); }
        ( ATTRIBUTE WS? )* '/>'
    ;

ATTRIBUTE 
    : name=GENERIC_ID WS? '=' WS? value=VALUE
        { System.out.println("Attr: "+name.getText()+"="+value.getText()); }
    ;

fragment END_TAG 
    : '</' WS? name=GENERIC_ID WS? '>'
        { System.out.println("End Tag: "+name.getText()); }
    ;
    

fragment COMMENT
  : '<!--' (options {greedy=false;} : .)* '-->'
  ;

fragment CDATA
  : '<![CDATA[' (options {greedy=false;} : .)* ']]>'
  ;

fragment PCDATA : (options {greedy=false;} : (~'<')+) ; 

fragment VALUE : 
        ( '\"' (~'\"')* '\"'
        | '\'' (~'\'')* '\''
        )
  ;

fragment GENERIC_ID 
    : ( LETTER | '_' | ':') 
        ( options {greedy=true;} : LETTER | '0'..'9' | '.' | '-' | '_' | ':' )*
  ;

fragment LETTER
  : 'a'..'z' 
  | 'A'..'Z'
  ;

fragment WS  :
        (   ' '
        |   '\t'
        |  ( '\n'
            | '\r\n'
            | '\r'
            )
        )+
    ;    
