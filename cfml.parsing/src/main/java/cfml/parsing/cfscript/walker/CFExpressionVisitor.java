package cfml.parsing.cfscript.walker;

import java.util.Stack;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import cfml.CFSCRIPTLexer;
import cfml.CFSCRIPTParser.AnonymousFunctionDeclarationContext;
import cfml.CFSCRIPTParser.ArgumentContext;
import cfml.CFSCRIPTParser.ArrayContext;
import cfml.CFSCRIPTParser.ArrayMemberExpressionContext;
import cfml.CFSCRIPTParser.AssignmentExpressionContext;
import cfml.CFSCRIPTParser.BaseExpressionContext;
import cfml.CFSCRIPTParser.CfmlFunctionContext;
import cfml.CFSCRIPTParser.CompareExpressionContext;
import cfml.CFSCRIPTParser.ComponentAttributeContext;
import cfml.CFSCRIPTParser.ComponentGutsContext;
import cfml.CFSCRIPTParser.ComponentPathContext;
import cfml.CFSCRIPTParser.ConditionContext;
import cfml.CFSCRIPTParser.ConstantExpressionContext;
import cfml.CFSCRIPTParser.ElvisExpressionContext;
import cfml.CFSCRIPTParser.FloatingPointExpressionContext;
import cfml.CFSCRIPTParser.ForInKeyContext;
import cfml.CFSCRIPTParser.FunctionAttributeContext;
import cfml.CFSCRIPTParser.FunctionCallContext;
import cfml.CFSCRIPTParser.IdentifierContext;
import cfml.CFSCRIPTParser.ImplicitArrayContext;
import cfml.CFSCRIPTParser.ImplicitStructContext;
import cfml.CFSCRIPTParser.ImplicitStructExpressionContext;
import cfml.CFSCRIPTParser.InnerExpressionContext;
import cfml.CFSCRIPTParser.LiteralExpressionContext;
import cfml.CFSCRIPTParser.LocalAssignmentExpressionContext;
import cfml.CFSCRIPTParser.MemberExpressionContext;
import cfml.CFSCRIPTParser.MultipartIdentifierContext;
import cfml.CFSCRIPTParser.NewComponentExpressionContext;
import cfml.CFSCRIPTParser.OtherIdentifiersContext;
import cfml.CFSCRIPTParser.ParameterAttributeContext;
import cfml.CFSCRIPTParser.ParameterContext;
import cfml.CFSCRIPTParser.ParameterTypeContext;
import cfml.CFSCRIPTParser.ParentheticalExpressionContext;
import cfml.CFSCRIPTParser.ParentheticalMemberExpressionContext;
import cfml.CFSCRIPTParser.PrimaryExpressionContext;
import cfml.CFSCRIPTParser.PrimaryExpressionIRWContext;
import cfml.CFSCRIPTParser.QualifiedFunctionCallContext;
import cfml.CFSCRIPTParser.ReservedWordContext;
import cfml.CFSCRIPTParser.SpecialWordContext;
import cfml.CFSCRIPTParser.StringLiteralContext;
import cfml.CFSCRIPTParser.StringLiteralPartContext;
import cfml.CFSCRIPTParser.TagFunctionStatementContext;
import cfml.CFSCRIPTParser.TagOperatorStatementContext;
import cfml.CFSCRIPTParser.TernaryExpressionContext;
import cfml.CFSCRIPTParser.TypeContext;
import cfml.CFSCRIPTParser.TypeSpecContext;
import cfml.CFSCRIPTParser.UnaryExpressionContext;
import cfml.CFSCRIPTParserBaseVisitor;
import cfml.parsing.cfscript.ArgumentsVector;
import cfml.parsing.cfscript.CFAnonymousFunctionExpression;
import cfml.parsing.cfscript.CFArrayExpression;
import cfml.parsing.cfscript.CFAssignmentExpression;
import cfml.parsing.cfscript.CFBinaryExpression;
import cfml.parsing.cfscript.CFElvisExpression;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFFullVarExpression;
import cfml.parsing.cfscript.CFFunctionExpression;
import cfml.parsing.cfscript.CFIdentifier;
import cfml.parsing.cfscript.CFLiteral;
import cfml.parsing.cfscript.CFMember;
import cfml.parsing.cfscript.CFNestedExpression;
import cfml.parsing.cfscript.CFNewExpression;
import cfml.parsing.cfscript.CFStringExpression;
import cfml.parsing.cfscript.CFStructElementExpression;
import cfml.parsing.cfscript.CFStructExpression;
import cfml.parsing.cfscript.CFTernaryExpression;
import cfml.parsing.cfscript.CFUnaryExpression;
import cfml.parsing.cfscript.CFVarDeclExpression;
import cfml.parsing.cfscript.script.CFFuncDeclStatement;

public class CFExpressionVisitor extends CFSCRIPTParserBaseVisitor<CFExpression> {
	
	private Stack<CFExpression> aggregator = new Stack<CFExpression>();
	private CFScriptStatementVisitor scriptStatementVisitor = null;
	
	public CFExpressionVisitor() {
		super();
	}
	
	@Override
	public CFExpression visitCompareExpression(CompareExpressionContext ctx) {
		if (ctx.getChildCount() == 0) {
			return null;
		}
		CFExpression compareExpression = null;
		if (ctx.right != null) {
			compareExpression = new CFBinaryExpression(getTerminalToken(ctx.operator), visit(ctx.left), visit(ctx.right));
		} else {
			compareExpression = visit(ctx.left);
		}
		return compareExpression;
	}
	
	@Override
	public CFExpression visitParameterType(ParameterTypeContext ctx) {
		return super.visitParameterType(ctx);
	}
	
	@Override
	public CFExpression visitComponentAttribute(ComponentAttributeContext ctx) {
		return super.visitComponentAttribute(ctx);
	}
	
	@Override
	public CFExpression visitTypeSpec(TypeSpecContext ctx) {
		// TODO Auto-generated method stub
		return super.visitTypeSpec(ctx);
	}
	
	@Override
	public CFExpression visitFunctionAttribute(FunctionAttributeContext ctx) {
		return super.visitFunctionAttribute(ctx);
	}
	
	@Override
	public CFExpression visitParameterAttribute(ParameterAttributeContext ctx) {
		return super.visitParameterAttribute(ctx);
	}
	
	@Override
	public CFExpression visitComponentGuts(ComponentGutsContext ctx) {
		return super.visitComponentGuts(ctx);
	}
	
	@Override
	public CFExpression visitCondition(ConditionContext ctx) {
		return super.visitCondition(ctx);
	}
	
	@Override
	public CFExpression visitForInKey(ForInKeyContext ctx) {
		if (ctx.VAR() != null) {
			CFVarDeclExpression declExpression = new CFVarDeclExpression(ctx.VAR().getSymbol(), (CFIdentifier) visitChildren(ctx),
					(CFExpression) null);
			return declExpression;
		}
		return super.visitForInKey(ctx);
	}
	
	@Override
	public CFExpression visitConstantExpression(ConstantExpressionContext ctx) {
		if (ctx.floatingPointExpression() != null) {
			return visitFloatingPointExpression(ctx.floatingPointExpression());
		} else if (ctx.MINUS() != null) {
			return new CFUnaryExpression(ctx.MINUS().getSymbol(), visitConstantExpression(ctx.constantExpression()));
		} else if (ctx.LEFTPAREN() != null) {
			return new CFUnaryExpression(ctx.LEFTPAREN().getSymbol(), visitConstantExpression(ctx.constantExpression()));
		} else if (ctx.stringLiteral() != null) {
			return visitStringLiteral(ctx.stringLiteral());
		} else {
			return new CFLiteral(ctx.start);
		}
	}
	
	@Override
	public CFExpression visitStringLiteral(StringLiteralContext ctx) {
		CFStringExpression stringExpression = new CFStringExpression(ctx.getStart());
		for (ParseTree child : ctx.children) {
			CFExpression result = super.visit(child);
			if (result != null)
				stringExpression.getSubExpressions().add(result);
		}
		return stringExpression;
	}
	
	@Override
	public CFExpression visitStringLiteralPart(StringLiteralPartContext ctx) {
		CFLiteral literal = new CFLiteral(ctx.start);
		return literal;
	}
	
	@Override
	public CFExpression visitLocalAssignmentExpression(LocalAssignmentExpressionContext ctx) {
		CFIdentifier identifier = (CFIdentifier) visit(ctx.left);
		CFExpression initExpression = visit(ctx.right);
		CFVarDeclExpression retval = new CFVarDeclExpression(ctx.start, identifier, initExpression);
		if (ctx.otherIdentifiers().size() > 0) {
			for (OtherIdentifiersContext oi : ctx.otherIdentifiers()) {
				CFIdentifier otherid = (CFIdentifier) visit(oi.identifier());
				if (oi.VAR() != null) {
					retval.getOtherVars().add(otherid);
				} else {
					retval.getOtherIds().add(otherid);
				}
			}
		}
		return retval;
	}
	
	@Override
	public CFExpression visitAssignmentExpression(AssignmentExpressionContext ctx) {
		if (ctx.right == null) {
			return visitStartExpression(ctx.left);
		} else {
			CFAssignmentExpression assignmentExpression = new CFAssignmentExpression(getTerminalToken(ctx.getChild(1)),
					visit(ctx.left), visit(ctx.right));
			for (IdentifierContext oi : ctx.identifier()) {
				CFIdentifier otherid = (CFIdentifier) visit(oi);
				assignmentExpression.getOtherIds().add(otherid);
			}
			return assignmentExpression;
		}
	}
	
	@Override
	public CFExpression visitBaseExpression(BaseExpressionContext ctx) {
		if (ctx.getChildCount() == 0) {
			return null;
		}
		if (ctx.notExpression() != null) {
			return new CFUnaryExpression(getTerminalToken(ctx.notExpression().getChild(0)),
					visit(ctx.notExpression().unaryExpression()));
		} else if (ctx.notNotExpression() != null) {
			return new CFUnaryExpression(getTerminalToken(ctx.notNotExpression().getChild(0)),
					visit(ctx.notNotExpression().unaryExpression()));
		} else if (ctx.unaryExpression() != null) {
			return visitUnaryExpression(ctx.unaryExpression());
		} else if (ctx.ternaryExpression() != null) {
			TernaryExpressionContext tex = ctx.ternaryExpression();
			CFTernaryExpression ternaryExpression = new CFTernaryExpression(tex.getStart(), visit(ctx.baseExpression()),
					visit(tex.ternaryExpression1), visit(tex.ternaryExpression2));
			return ternaryExpression;
		} else if (ctx.getChild(0).getChildCount() == 3)
		
		{
			CFBinaryExpression binaryExpression = new CFBinaryExpression(getTerminalToken(ctx.getChild(0).getChild(1)),
					visit(ctx.getChild(0).getChild(0)), visit(ctx.getChild(0).getChild(2)));
			return binaryExpression;
		} else
		
		{
			return visitChildren(ctx);
		}
		
	}
	
	@Override
	public CFExpression visitUnaryExpression(UnaryExpressionContext ctx) {
		if (ctx.getChildCount() < 2) {
			return super.visitChildren(ctx);
		} else {
			CFUnaryExpression unaryExpression = new CFUnaryExpression(ctx.start, super.visitChildren(ctx));
			return unaryExpression;
		}
	}
	
	@Override
	public CFExpression visitParentheticalExpression(ParentheticalExpressionContext ctx) {
		if (ctx.getChildCount() == 3) {
			CFUnaryExpression unaryExpression = new CFUnaryExpression(getTerminalToken(ctx.getChild(0)), visit(ctx.getChild(1)));
			return unaryExpression;
			
		} else {
			return super.visitParentheticalExpression(ctx);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see cfml.CFSCRIPTParserBaseVisitor#visitParentheticalMemberExpression(cfml.CFSCRIPTParser.
	 * ParentheticalMemberExpressionContext) Load the member expression as an empty function.
	 */
	@Override
	public CFExpression visitParentheticalMemberExpression(ParentheticalMemberExpressionContext ctx) {
		ArgumentsVector args = new ArgumentsVector();
		if (ctx.argumentList() != null) {
			for (ArgumentContext argCtx : ctx.argumentList().argument()) {
				if (argCtx.name != null) {
					args.addNamedArg(visit(argCtx.name), visit(argCtx.startExpression()));
				} else {
					args.add(visit(argCtx));
				}
			}
		}
		CFFunctionExpression cfFunctionExpression = new CFFunctionExpression(ctx.LEFTPAREN().getSymbol(), null, args);
		return cfFunctionExpression;
	}
	
	@Override
	public CFExpression visitMemberExpression(MemberExpressionContext ctx) {
		CFFullVarExpression fullVarExpression = new CFFullVarExpression(ctx.start, null);
		aggregator.push(fullVarExpression);
		CFExpression retval = visitChildren(ctx);
		aggregator.pop();
		// negative if minus present
		if (ctx.MINUS() != null) {
			retval = new CFUnaryExpression(ctx.MINUS().getSymbol(), retval);
		}
		return retval;
	}
	
	@Override
	public CFExpression visitInnerExpression(InnerExpressionContext ctx) {
		return new CFNestedExpression(ctx.POUND_SIGN(0).getSymbol(), visit(ctx.baseOrTernaryExpression()));
	}
	
	@Override
	public CFExpression visitArrayMemberExpression(ArrayMemberExpressionContext ctx) {
		CFMember member = new CFMember(ctx.getStart(), visit(ctx.getChild(1)));
		return member;
	}
	
	@Override
	public CFExpression visitPrimaryExpressionIRW(PrimaryExpressionIRWContext ctx) {
		if (ctx.start.getType() == CFSCRIPTLexer.BOOLEAN_LITERAL || ctx.start.getType() == CFSCRIPTLexer.INTEGER_LITERAL) {
			return new CFLiteral(ctx.start);
		}
		return super.visitPrimaryExpressionIRW(ctx);
	}
	
	@Override
	public CFExpression visitArgument(ArgumentContext ctx) {
		CFExpression retval = super.visitArgument(ctx);
		return retval;
	}
	
	@Override
	public CFExpression visitIdentifier(IdentifierContext ctx) {
		if (ctx.getChildCount() > 1)
			return new CFIdentifier(ctx.start, ctx.getChild(0).getText(), ctx.getChild(1).getText());
		else
			return new CFIdentifier(ctx.start, ctx.getChild(0).getText());
	}
	
	@Override
	public CFExpression visitType(TypeContext ctx) {
		return new CFIdentifier(ctx.start, ctx.getChild(0).getText());
	}
	
	@Override
	public CFExpression visitPrimaryExpression(PrimaryExpressionContext ctx) {
		ParseTree child = ctx.getChild(0);
		if (child.getChildCount() == 0) {
			CFLiteral literal = new CFLiteral(((TerminalNode) child).getSymbol());
			return literal;
		} else {
			return visitChildren(ctx);
		}
	}
	
	@Override
	public CFExpression visitImplicitArray(ImplicitArrayContext ctx) {
		CFArrayExpression arrayExpression = new CFArrayExpression(ctx.getStart());
		aggregator.push(arrayExpression);
		CFExpression retval = super.visitImplicitArray(ctx);
		aggregator.pop();
		if (retval != null && retval != arrayExpression) {
			arrayExpression.addElement(retval);
		}
		return arrayExpression;
	}
	
	@Override
	public CFExpression visitImplicitStruct(ImplicitStructContext ctx) {
		CFStructExpression structExpression = new CFStructExpression(ctx.getStart());
		aggregator.push(structExpression);
		CFExpression retval = super.visitImplicitStruct(ctx);
		aggregator.pop();
		if (retval instanceof CFStructElementExpression) {
			structExpression.addElement((CFStructElementExpression) retval);
		}
		return structExpression;
	}
	
	@Override
	public CFExpression visitElvisExpression(ElvisExpressionContext ctx) {
		// TODO Auto-generated method stub
		CFElvisExpression expr = new CFElvisExpression(ctx.getStart(), visit(ctx.getChild(0)), visit(ctx.getChild(3)));
		// return super.visitElvisExpression(ctx);
		return expr;
	}
	
	@Override
	public CFExpression visitImplicitStructExpression(ImplicitStructExpressionContext ctx) {
		CFStructElementExpression elementExpression = new CFStructElementExpression(ctx.getStart(),
				makeIdentifier(visit(ctx.getChild(0))), visit(ctx.getChild(2)));
		return elementExpression;
	}
	
	private CFIdentifier makeIdentifier(CFExpression visit) {
		if (visit instanceof CFIdentifier) {
			return (CFIdentifier) visit;
		} else {
			return new CFFullVarExpression(visit.getToken(), visit);
		}
	}
	
	@Override
	public CFExpression visitNewComponentExpression(NewComponentExpressionContext ctx) {
		ArgumentsVector args = new ArgumentsVector();
		if (ctx.getChildCount() > 4) {
			for (ArgumentContext argCtx : ctx.argumentList().argument()) {
				args.add(visit(argCtx));
			}
		}
		CFNewExpression newExpression = new CFNewExpression(ctx.NEW().getSymbol(), visit(ctx.componentPath()), args);
		return newExpression;
	}
	
	@Override
	public CFExpression visitComponentPath(ComponentPathContext ctx) {
		return super.visitChildren(ctx);
	}
	
	@Override
	public CFExpression visitLiteralExpression(LiteralExpressionContext ctx) {
		if (ctx.floatingPointExpression() != null) {
			return visitFloatingPointExpression(ctx.floatingPointExpression());
		}
		if (ctx.stringLiteral() != null) {
			return visitStringLiteral(ctx.stringLiteral());
		}
		return new CFLiteral(ctx.start);
	}
	
	@Override
	public CFExpression visitFloatingPointExpression(FloatingPointExpressionContext ctx) {
		if (ctx.right != null) {
			return new CFLiteral(ctx.start, (ctx.left != null ? ctx.left.getText() : "") + "." + ctx.right.getText());
		} else if (ctx.leftonly != null) {
			return new CFLiteral(ctx.leftonly, ctx.leftonly.getText() + ".");
		} else {
			return new CFLiteral(ctx.start);
		}
	}
	
	@Override
	public CFExpression visitFunctionCall(FunctionCallContext ctx) {
		ArgumentsVector args = new ArgumentsVector();
		if (ctx.argumentList() != null) {
			for (ArgumentContext argCtx : ctx.argumentList().argument()) {
				if (argCtx.name != null) {
					args.addNamedArg(visit(argCtx.name), visit(argCtx.startExpression()));
				} else {
					args.add(visit(argCtx));
				}
			}
		}
		CFFunctionExpression cfFunctionExpression = new CFFunctionExpression((CFIdentifier) visit(ctx.getChild(0)), args);
		if (ctx.body != null) {
			cfFunctionExpression.setBody(getCFScriptStatementVisitor().visit(ctx.body));
		}
		return cfFunctionExpression;
	}
	
	@Override
	public CFExpression visitQualifiedFunctionCall(QualifiedFunctionCallContext ctx) {
		ArgumentsVector args = new ArgumentsVector();
		if (ctx.argumentList() != null) {
			for (ArgumentContext argCtx : ctx.argumentList().argument()) {
				if (argCtx.name != null) {
					args.addNamedArg(visit(argCtx.name), visit(argCtx.startExpression()));
				} else {
					args.add(visit(argCtx));
				}
			}
		}
		CFFunctionExpression cfFunctionExpression = new CFFunctionExpression((CFIdentifier) visit(ctx.getChild(0)), args);
		if (ctx.body != null) {
			cfFunctionExpression.setBody(getCFScriptStatementVisitor().visit(ctx.body));
		}
		return cfFunctionExpression;
	}
	
	@Override
	public CFExpression visitTagOperatorStatement(TagOperatorStatementContext ctx) {
		// TODO Auto-generated method stub
		return super.visitTagOperatorStatement(ctx);
	}
	
	@Override
	public CFExpression visitCfmlFunction(CfmlFunctionContext ctx) {
		return new CFIdentifier(ctx.start, ctx.getChild(0).getText());
	}
	
	@Override
	public CFExpression visitTagFunctionStatement(TagFunctionStatementContext ctx) {
		// TODO Auto-generated method stub
		// return super.visitTagFunctionStatement(ctx);
		ArgumentsVector args = new ArgumentsVector();
		if (ctx.parameterList() != null) {
			for (ParameterContext argCtx : ctx.parameterList().parameter()) {
				if (argCtx.name != null) {
					args.addNamedArg(visit(argCtx.name), visit(argCtx.startExpression()));
				} else {
					args.add(visit(argCtx));
				}
			}
		}
		CFFunctionExpression cfFunctionExpression = new CFFunctionExpression((CFIdentifier) visit(ctx.getChild(0)), args);
		return cfFunctionExpression;
	}
	
	@Override
	public CFExpression visitMultipartIdentifier(MultipartIdentifierContext ctx) {
		if (ctx.getChildCount() < 2) {
			return super.visitMultipartIdentifier(ctx);
		} else {
			CFFullVarExpression fullVarExpression = new CFFullVarExpression(ctx.getStart(), null);
			aggregator.push(fullVarExpression);
			try {
				return visitChildren(ctx);
			} finally {
				aggregator.pop();
			}
		}
	}
	
	@Override
	public CFExpression visitArray(ArrayContext ctx) {
		return new CFIdentifier(ctx.start, ctx.start.getText() + ctx.stop.getText());
	}
	
	@Override
	public CFExpression visitAnonymousFunctionDeclaration(AnonymousFunctionDeclarationContext ctx) {
		CFFuncDeclStatement funcDeclStatement = (CFFuncDeclStatement) getCFScriptStatementVisitor()
				.visitAnonymousFunctionDeclaration(ctx);
		return new CFAnonymousFunctionExpression(ctx.FUNCTION().getSymbol(), funcDeclStatement);
	}
	
	public synchronized CFScriptStatementVisitor getCFScriptStatementVisitor() {
		if (scriptStatementVisitor == null) {
			scriptStatementVisitor = new CFScriptStatementVisitor();
		}
		return scriptStatementVisitor;
	}
	
	@Override
	protected CFExpression aggregateResult(CFExpression aggregate, CFExpression nextResult) {
		if (nextResult == null) {
			return aggregate;
		}
		if (aggregate == null) {
			return nextResult;
		}
		
		try {
			if (aggregate instanceof CFNewExpression) {
				CFFullVarExpression fullVarExpression = new CFFullVarExpression(aggregate.getToken(), aggregate);
				fullVarExpression.addMember(nextResult);
				aggregate = fullVarExpression;
			} else if (aggregate instanceof CFFullVarExpression) {
				((CFFullVarExpression) aggregate).addMember(nextResult);
			} else if (!aggregator.isEmpty() && aggregator.peek() instanceof CFFullVarExpression) {
				CFFullVarExpression fullVarExpression = (CFFullVarExpression) aggregator.peek();
				if (aggregate != fullVarExpression) {
					fullVarExpression.addMember(aggregate);
				}
				fullVarExpression.addMember(nextResult);
				aggregate = fullVarExpression;
			} else if (!aggregator.isEmpty() && aggregator.peek() instanceof CFArrayExpression) {
				CFArrayExpression arrayExpression = (CFArrayExpression) aggregator.peek();
				if (aggregate != arrayExpression) {
					arrayExpression.addElement(aggregate);
				}
				arrayExpression.addElement(nextResult);
				aggregate = arrayExpression;
			} else if (!aggregator.isEmpty() && aggregator.peek() instanceof CFStructExpression) {
				CFStructExpression structExpression = (CFStructExpression) aggregator.peek();
				if (aggregate != structExpression && aggregate instanceof CFStructElementExpression) {
					structExpression.addElement((CFStructElementExpression) aggregate);
				}
				if (nextResult instanceof CFStructElementExpression) {
					structExpression.addElement((CFStructElementExpression) nextResult);
				}
				aggregate = structExpression;
			}
			// Convert an simple identifier to a full var expression with members.
			// or add another member to a full var expression.
			else if (nextResult instanceof CFIdentifier || nextResult instanceof CFMember) {
				CFFullVarExpression fullVar = new CFFullVarExpression(aggregate.getToken(), aggregate);
				fullVar.addMember(nextResult);
				aggregate = fullVar;
			}
			// Struct elements {"x" : "abc", "y" : "123"} are aggregated
			else if (nextResult instanceof CFStructElementExpression) {
				if (aggregate instanceof CFStructElementExpression) {
					CFStructExpression structExpression = new CFStructExpression(aggregate.getToken());
					structExpression.addElement((CFStructElementExpression) aggregate);
					structExpression.addElement((CFStructElementExpression) nextResult);
					aggregate = structExpression;
				} else if (aggregate instanceof CFStructExpression) {
					((CFStructExpression) aggregate).addElement((CFStructElementExpression) nextResult);
				}
			}
			return aggregate;
		} finally {
		}
	}
	
	private Token getTerminalToken(ParseTree child) {
		if (child instanceof TerminalNode) {
			return ((TerminalNode) child).getSymbol();
		}
		if (child.getChildCount() == 1) {
			return getTerminalToken(child.getChild(0));
		}
		return null;
	}
	
	public void clear() {
		aggregator.clear();
	}
	
	@Override
	public CFExpression visitSpecialWord(SpecialWordContext ctx) {
		if (ctx.getChildCount() > 1)
			return new CFIdentifier(ctx.start, ctx.getChild(0).getText(), ctx.getChild(1).getText());
		else
			return new CFIdentifier(ctx.start, ctx.getChild(0).getText());
	}
	
	@Override
	public CFExpression visitReservedWord(ReservedWordContext ctx) {
		return new CFIdentifier(ctx.start, ctx.getChild(0).getText());
	}
	
}
