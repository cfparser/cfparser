package cfml.parsing.cfscript.walker;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import cfml.CFSCRIPTParser.AccessTypeContext;
import cfml.CFSCRIPTParser.ArgumentContext;
import cfml.CFSCRIPTParser.ArgumentListContext;
import cfml.CFSCRIPTParser.AssignmentExpressionContext;
import cfml.CFSCRIPTParser.BaseExpressionContext;
import cfml.CFSCRIPTParser.CfmlFunctionContext;
import cfml.CFSCRIPTParser.CfscriptKeywordsContext;
import cfml.CFSCRIPTParser.CompareExpressionContext;
import cfml.CFSCRIPTParser.ComponentAttributeContext;
import cfml.CFSCRIPTParser.ComponentGutsContext;
import cfml.CFSCRIPTParser.ComponentPathContext;
import cfml.CFSCRIPTParser.ConditionContext;
import cfml.CFSCRIPTParser.ConstantExpressionContext;
import cfml.CFSCRIPTParser.EqualityOperator1Context;
import cfml.CFSCRIPTParser.ForInKeyContext;
import cfml.CFSCRIPTParser.FunctionAttributeContext;
import cfml.CFSCRIPTParser.FunctionCallContext;
import cfml.CFSCRIPTParser.FunctionDeclarationContext;
import cfml.CFSCRIPTParser.IdentifierContext;
import cfml.CFSCRIPTParser.ImplicitArrayContext;
import cfml.CFSCRIPTParser.ImplicitArrayElementsContext;
import cfml.CFSCRIPTParser.ImplicitStructContext;
import cfml.CFSCRIPTParser.ImplicitStructElementsContext;
import cfml.CFSCRIPTParser.ImplicitStructExpressionContext;
import cfml.CFSCRIPTParser.ImplicitStructKeyExpressionContext;
import cfml.CFSCRIPTParser.IndexSuffixContext;
import cfml.CFSCRIPTParser.LocalAssignmentExpressionContext;
import cfml.CFSCRIPTParser.MemberExpressionBContext;
import cfml.CFSCRIPTParser.MemberExpressionContext;
import cfml.CFSCRIPTParser.MemberExpressionSuffixContext;
import cfml.CFSCRIPTParser.MultipartIdentifierContext;
import cfml.CFSCRIPTParser.NewComponentExpressionContext;
import cfml.CFSCRIPTParser.ParamContext;
import cfml.CFSCRIPTParser.ParamStatementAttributesContext;
import cfml.CFSCRIPTParser.ParameterAttributeContext;
import cfml.CFSCRIPTParser.ParameterContext;
import cfml.CFSCRIPTParser.ParameterListContext;
import cfml.CFSCRIPTParser.ParameterTypeContext;
import cfml.CFSCRIPTParser.PrimaryExpressionContext;
import cfml.CFSCRIPTParser.PrimaryExpressionIRWContext;
import cfml.CFSCRIPTParser.PropertyReferenceSuffixContext;
import cfml.CFSCRIPTParser.ReservedWordContext;
import cfml.CFSCRIPTParser.TernaryContext;
import cfml.CFSCRIPTParser.TypeContext;
import cfml.CFSCRIPTParser.TypeSpecContext;
import cfml.CFSCRIPTParser.UnaryExpressionContext;
import cfml.CFSCRIPTParserBaseVisitor;
import cfml.parsing.cfscript.ArgumentsVector;
import cfml.parsing.cfscript.CFAssignmentExpression;
import cfml.parsing.cfscript.CFBinaryExpression;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFFunctionExpression;
import cfml.parsing.cfscript.CFIdentifier;
import cfml.parsing.cfscript.CFLiteral;
import cfml.parsing.cfscript.CFMultipartIdentifier;
import cfml.parsing.cfscript.CFTernaryExpression;
import cfml.parsing.cfscript.CFUnaryExpression;
import cfml.parsing.cfscript.CFVarDeclExpression;

public class CFExpressionVisitor extends CFSCRIPTParserBaseVisitor<CFExpression> {
	
	// @Override
	// public CFExpression visitElement(ElementContext ctx) {
	// System.out.println("CFExpr.visitElement");
	// return super.visitElement(ctx);
	// }
	
	@Override
	public CFExpression visitFunctionDeclaration(FunctionDeclarationContext ctx) {
		System.out.println("CFExpr.visitFunctionDeclaration");
		return super.visitFunctionDeclaration(ctx);
	}
	
	@Override
	public CFExpression visitAccessType(AccessTypeContext ctx) {
		System.out.println("CFExpr.visitAccessType");
		return super.visitAccessType(ctx);
	}
	
	@Override
	public CFExpression visitCompareExpression(CompareExpressionContext ctx) {
		if (ctx.getChildCount() == 0) {
			return null;
		}
		if (ctx.getChild(0).getChildCount() == 3) {
			CFBinaryExpression binaryExpression = new CFBinaryExpression(getTerminalToken(ctx.getChild(0).getChild(1)),
					visit(ctx.getChild(0).getChild(0)), visit(ctx.getChild(0).getChild(2)));
			return binaryExpression;
		} else if (ctx.getChild(0).getChildCount() == 2) {
			CFUnaryExpression unaryExpression = new CFUnaryExpression(getTerminalToken(ctx.getChild(0).getChild(0)),
					visit(ctx.getChild(0).getChild(1)));
			return unaryExpression;
		} else {
			return visit(ctx.getChild(0));
		}
	}
	
	@Override
	public CFExpression visitTypeSpec(TypeSpecContext ctx) {
		System.out.println("CFExpr.visitTypeSpec");
		return super.visitTypeSpec(ctx);
	}
	
	@Override
	public CFExpression visitParameterList(ParameterListContext ctx) {
		System.out.println("CFExpr.visitParameterList");
		return super.visitParameterList(ctx);
	}
	
	@Override
	public CFExpression visitParameter(ParameterContext ctx) {
		System.out.println("CFExpr.visitParameter");
		return super.visitParameter(ctx);
	}
	
	@Override
	public CFExpression visitParameterType(ParameterTypeContext ctx) {
		System.out.println("CFExpr.visitParameterType");
		return super.visitParameterType(ctx);
	}
	
	@Override
	public CFExpression visitComponentAttribute(ComponentAttributeContext ctx) {
		System.out.println("CFExpr.visitComponentAttribute");
		return super.visitComponentAttribute(ctx);
	}
	
	@Override
	public CFExpression visitFunctionAttribute(FunctionAttributeContext ctx) {
		System.out.println("CFExpr.visitFunctionAttribute");
		return super.visitFunctionAttribute(ctx);
	}
	
	@Override
	public CFExpression visitParameterAttribute(ParameterAttributeContext ctx) {
		System.out.println("CFExpr.visitParameterAttribute");
		return super.visitParameterAttribute(ctx);
	}
	
	@Override
	public CFExpression visitComponentGuts(ComponentGutsContext ctx) {
		System.out.println("CFExpr.visitComponentGuts");
		return super.visitComponentGuts(ctx);
	}
	
	@Override
	public CFExpression visitCondition(ConditionContext ctx) {
		System.out.println("CFExpr.visitCondition");
		return super.visitCondition(ctx);
	}
	
	@Override
	public CFExpression visitForInKey(ForInKeyContext ctx) {
		System.out.println("CFExpr.visitForInKey");
		return super.visitForInKey(ctx);
	}
	
	@Override
	public CFExpression visitConstantExpression(ConstantExpressionContext ctx) {
		System.out.println("CFExpr.visitConstantExpression");
		return super.visitConstantExpression(ctx);
	}
	
	@Override
	public CFExpression visitCfmlFunction(CfmlFunctionContext ctx) {
		System.out.println("CFExpr.visitCfmlFunction");
		return super.visitCfmlFunction(ctx);
	}
	
	@Override
	public CFExpression visitParamStatementAttributes(ParamStatementAttributesContext ctx) {
		System.out.println("CFExpr.visitParamStatementAttributes");
		return super.visitParamStatementAttributes(ctx);
	}
	
	@Override
	public CFExpression visitParam(ParamContext ctx) {
		System.out.println("CFExpr.visitParam");
		return super.visitParam(ctx);
	}
	
	// @Override
	// public CFExpression visitExpression(ExpressionContext ctx) {
	// System.out.println("CFExpr.visitExpression");
	// return super.visitExpression(ctx);
	// }
	
	@Override
	public CFExpression visitLocalAssignmentExpression(LocalAssignmentExpressionContext ctx) {
		
		CFIdentifier identifier = (CFIdentifier) visit(ctx.getChild(1));
		CFExpression initExpression = visit(ctx.getChild(3));
		System.out.println("CFExpr.visitLocalAssignmentExpression");
		CFVarDeclExpression retval = new CFVarDeclExpression(ctx.start, identifier, initExpression);
		// return super.visitLocalAssignmentExpression(ctx);
		return retval;
	}
	
	@Override
	public CFExpression visitAssignmentExpression(AssignmentExpressionContext ctx) {
		if (ctx.getChildCount() < 3) {
			return visitChildren(ctx);
		} else {
			CFAssignmentExpression assignmentExpression = new CFAssignmentExpression(getTerminalToken(ctx.getChild(1)),
					visit(ctx.getChild(0)), visit(ctx.getChild(2)));
			return assignmentExpression;
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
	
	@Override
	public CFExpression visitBaseExpression(BaseExpressionContext ctx) {
		if (ctx.getChildCount() == 0) {
			return null;
		}
		if (ctx.getChild(0) instanceof UnaryExpressionContext) {
			return visitUnaryExpression((UnaryExpressionContext) ctx.getChild(0));
		} else if (ctx.getChild(0).getChildCount() == 3) {
			CFBinaryExpression binaryExpression = new CFBinaryExpression(getTerminalToken(ctx.getChild(0).getChild(1)),
					visit(ctx.getChild(0).getChild(0)), visit(ctx.getChild(0).getChild(2)));
			return binaryExpression;
		} else {
			return visit(ctx.getChild(0));
		}
	}
	
	@Override
	public CFExpression visitTernary(TernaryContext ctx) {
		CFTernaryExpression ternaryExpression = new CFTernaryExpression(ctx.getStart(), visit(ctx.getChild(0)),
				visit(ctx.getChild(2)), visit(ctx.getChild(4)));
		return ternaryExpression;
	}
	
	@Override
	public CFExpression visitEqualityOperator1(EqualityOperator1Context ctx) {
		System.out.println("CFExpr.visitEqualityOperator1");
		return super.visitEqualityOperator1(ctx);
	}
	
	@Override
	public CFExpression visitUnaryExpression(UnaryExpressionContext ctx) {
		if (ctx.getChildCount() < 2) {
			return super.visitChildren(ctx);
		} else {
			System.out.println("CFExpr.visitUnaryExpression");
			CFUnaryExpression unaryExpression = new CFUnaryExpression(ctx.start, super.visitChildren(ctx));
			return unaryExpression;
		}
	}
	
	@Override
	public CFExpression visitMemberExpression(MemberExpressionContext ctx) {
		if (ctx.getChildCount() < 2) {
			return visitChildren(ctx);
		} else {
			return visit(ctx.getChild(1));
		}
	}
	
	@Override
	public CFExpression visitMemberExpressionB(MemberExpressionBContext ctx) {
		System.out.println("CFExpr.visitMemberExpressionB+" + ctx.getChildCount());
		// TODO 4 more cases
		if (ctx.getChildCount() == 4 && ctx.getChild(2) instanceof ArgumentListContext) {
			ArgumentsVector args = new ArgumentsVector();
			for (int i = 0; i < ctx.getChild(2).getChildCount(); i++) {
				final CFExpression arg = visit(ctx.getChild(2).getChild(i));
				if (ctx.getChild(2).getChild(i) instanceof ArgumentContext) {
					args.add(arg);
				}
			}
			CFFunctionExpression functionExpression = new CFFunctionExpression((CFIdentifier) visit(ctx.getChild(0)),
					args);
			return functionExpression;
		}
		return super.visitMemberExpressionB(ctx);
	}
	
	@Override
	public CFExpression visitMemberExpressionSuffix(MemberExpressionSuffixContext ctx) {
		System.out.println("CFExpr.visitMemberExpressionSuffix");
		return super.visitMemberExpressionSuffix(ctx);
	}
	
	@Override
	public CFExpression visitPropertyReferenceSuffix(PropertyReferenceSuffixContext ctx) {
		System.out.println("CFExpr.visitPropertyReferenceSuffix");
		return super.visitPropertyReferenceSuffix(ctx);
	}
	
	@Override
	public CFExpression visitIndexSuffix(IndexSuffixContext ctx) {
		System.out.println("CFExpr.visitIndexSuffix");
		return super.visitIndexSuffix(ctx);
	}
	
	@Override
	public CFExpression visitPrimaryExpressionIRW(PrimaryExpressionIRWContext ctx) {
		System.out.println("CFExpr.visitPrimaryExpressionIRW:" + ctx.getText());
		return super.visitPrimaryExpressionIRW(ctx);
	}
	
	@Override
	public CFExpression visitReservedWord(ReservedWordContext ctx) {
		System.out.println("CFExpr.visitReservedWord");
		return super.visitReservedWord(ctx);
	}
	
	@Override
	public CFExpression visitArgumentList(ArgumentListContext ctx) {
		return null;
	}
	
	@Override
	public CFExpression visitArgument(ArgumentContext ctx) {
		System.out.println("CFExpr.visitArgument");
		return super.visitArgument(ctx);
	}
	
	@Override
	public CFExpression visitIdentifier(IdentifierContext ctx) {
		System.out.println("CFExpr.visitIdentifier " + ctx.getChild(0));
		if (ctx.getChildCount() > 1)
			return new CFIdentifier(ctx.start, ctx.getChild(0).toString(), ctx.getChild(1).toString());
		else
			return new CFIdentifier(ctx.start, ctx.getChild(0).toString());
	}
	
	@Override
	public CFExpression visitType(TypeContext ctx) {
		System.out.println("CFExpr.visitType");
		return super.visitType(ctx);
	}
	
	@Override
	public CFExpression visitCfscriptKeywords(CfscriptKeywordsContext ctx) {
		System.out.println("CFExpr.visitCfscriptKeywords");
		return super.visitCfscriptKeywords(ctx);
	}
	
	@Override
	public CFExpression visitPrimaryExpression(PrimaryExpressionContext ctx) {
		System.out.println("CFExpr.visitPrimaryExpression" + ctx.getChildCount() + " - " + ctx.getChild(0));
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
		System.out.println("CFExpr.visitImplicitArray");
		return super.visitImplicitArray(ctx);
	}
	
	@Override
	public CFExpression visitImplicitArrayElements(ImplicitArrayElementsContext ctx) {
		System.out.println("CFExpr.visitImplicitArrayElements");
		return super.visitImplicitArrayElements(ctx);
	}
	
	@Override
	public CFExpression visitImplicitStruct(ImplicitStructContext ctx) {
		System.out.println("CFExpr.visitImplicitStruct");
		return super.visitImplicitStruct(ctx);
	}
	
	@Override
	public CFExpression visitImplicitStructElements(ImplicitStructElementsContext ctx) {
		System.out.println("CFExpr.visitImplicitStructElements");
		return super.visitImplicitStructElements(ctx);
	}
	
	@Override
	public CFExpression visitImplicitStructExpression(ImplicitStructExpressionContext ctx) {
		System.out.println("CFExpr.visitImplicitStructExpression");
		return super.visitImplicitStructExpression(ctx);
	}
	
	@Override
	public CFExpression visitImplicitStructKeyExpression(ImplicitStructKeyExpressionContext ctx) {
		System.out.println("CFExpr.visitImplicitStructKeyExpression");
		return super.visitImplicitStructKeyExpression(ctx);
	}
	
	@Override
	public CFExpression visitNewComponentExpression(NewComponentExpressionContext ctx) {
		System.out.println("CFExpr.visitNewComponentExpression");
		return super.visitNewComponentExpression(ctx);
	}
	
	@Override
	public CFExpression visitComponentPath(ComponentPathContext ctx) {
		System.out.println("CFExpr.visitComponentPath");
		return super.visitComponentPath(ctx);
	}
	
	// @Override
	// public CFExpression visitChildren(RuleNode node) {
	// System.out.println("CFExpr.visitChildren" + node + node.getClass());
	// return super.visitChildren(node);
	// }
	
	@Override
	public CFExpression visitTerminal(TerminalNode node) {
		System.out.println("CFExpr.visitTerminal" + node);
		return super.visitTerminal(node);
	}
	
	@Override
	public CFExpression visitFunctionCall(FunctionCallContext ctx) {
		System.out.println("CFExpr.visitFunctionCall");
		ArgumentsVector args = new ArgumentsVector();
		if (ctx.getChildCount() > 3) {
			for (int i = 0; i < ctx.getChild(2).getChildCount(); i++) {
				ParseTree argCtx = ctx.getChild(2).getChild(i);
				if (argCtx.getChildCount() == 3) {
					args.putNamedArg(argCtx.getChild(0).getText(), visit(argCtx.getChild(2)));
				} else {
					args.add(visit(argCtx));
				}
			}
		}
		CFFunctionExpression cfFunctionExpression = new CFFunctionExpression((CFIdentifier) visit(ctx.getChild(0)),
				args);
		return cfFunctionExpression;
	}
	
	@Override
	public CFExpression visitMultipartIdentifier(MultipartIdentifierContext ctx) {
		CFExpression retval = super.visitMultipartIdentifier(ctx);
		System.out.println("CFExpr.visitMultipartIdentifier " + retval.Decompile(0));
		return retval;
	}
	
	@Override
	protected CFExpression aggregateResult(CFExpression aggregate, CFExpression nextResult) {
		if (nextResult == null) {
			return aggregate;
		}
		if (aggregate == null) {
			return nextResult;
		}
		System.out.println("CFExpr.aggregateResult --------------------------");
		System.out.println(aggregate.getClass() + " -> " + aggregate.Decompile(0));
		System.out.println(nextResult.getClass() + " -> " + nextResult.Decompile(0));
		System.out.println("--------------------------------------------");
		
		if (aggregate instanceof CFIdentifier && nextResult instanceof CFIdentifier) {
			if (aggregate instanceof CFMultipartIdentifier) {
				((CFMultipartIdentifier) aggregate).addIdentifier((CFIdentifier) nextResult);
			} else {
				return new CFMultipartIdentifier((CFIdentifier) aggregate, (CFIdentifier) nextResult);
			}
		}
		return aggregate;
	}
	
}
