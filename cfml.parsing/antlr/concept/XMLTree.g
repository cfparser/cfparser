tree grammar XMLTree;

options {
  tokenVocab=XML2;
  ASTLabelType=CommonTree;
}

@header {
  package cfml.parsing.cfml.antlr;
}

document : element ;

element
    : ^( ELEMENT name=GENERIC_ID
            { System.out.print("<"+$name.text); }
            (
                ^(ATTRIBUTE attrName=GENERIC_ID value=ATTR_VALUE)
                { System.out.print(" "+$attrName.text+"="+$value.text); }
            )*
            { System.out.println(">"); }
            (element
            | text=PCDATA
                { System.out.print($text.text); }
            )*
            { System.out.println("</"+$name.text+">"); }
        )
    ;