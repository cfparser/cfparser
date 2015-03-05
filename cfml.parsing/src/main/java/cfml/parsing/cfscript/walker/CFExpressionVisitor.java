package cfml.parsing.cfscript.walker;

import java.util.Stack;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import cfml.CFSCRIPTParser.AnonymousFunctionDeclarationContext;
import cfml.CFSCRIPTParser.ArgumentContext;
import cfml.CFSCRIPTParser.ArrayMemberExpressionContext;
import cfml.CFSCRIPTParser.AssignmentExpressionContext;
import cfml.CFSCRIPTParser.BaseExpressionContext;
import cfml.CFSCRIPTParser.CompareExpressionContext;
import cfml.CFSCRIPTParser.ComponentAttributeContext;
import cfml.CFSCRIPTParser.ComponentGutsContext;
import cfml.CFSCRIPTParser.ComponentPathContext;
import cfml.CFSCRIPTParser.ConditionContext;
import cfml.CFSCRIPTParser.ConstantExpressionContext;
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
import cfml.CFSCRIPTParser.MemberExpressionBContext;
import cfml.CFSCRIPTParser.MultipartIdentifierContext;
import cfml.CFSCRIPTParser.NewComponentExpressionContext;
import cfml.CFSCRIPTParser.ParameterAttributeContext;
import cfml.CFSCRIPTParser.ParentheticalExpressionContext;
import cfml.CFSCRIPTParser.ParentheticalMemberExpressionContext;
import cfml.CFSCRIPTParser.PrimaryExpressionContext;
import cfml.CFSCRIPTParser.PrimaryExpressionIRWContext;
import cfml.CFSCRIPTParser.StringLiteralContext;
import cfml.CFSCRIPTParser.TernaryContext;
import cfml.CFSCRIPTParser.UnaryExpressionContext;
import cfml.CFSCRIPTParserBaseVisitor;
import cfml.parsing.cfscript.ArgumentsVector;
import cfml.parsing.cfscript.CFAnonymousFunctionExpression;
import cfml.parsing.cfscript.CFArrayExpression;
import cfml.parsing.cfscript.CFAssignmentExpression;
import cfml.parsing.cfscript.CFBinaryExpression;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFFullVarExpression;
import cfml.parsing.cfscript.CFFunctionExpression;
import cfml.parsing.cfscript.CFIdentifier;
import cfml.parsing.cfscript.CFLiteral;
import cfml.parsing.cfscript.CFMember;
import cfml.parsing.cfscript.CFNestedExpression;
import cfml.parsing.cfscript.CFNewExpression;
import cfml.parsing.cfscript.CFStructElementExpression;
import cfml.parsing.cfscript.CFStructExpression;
import cfml.parsing.cfscript.CFTernaryExpression;
import cfml.parsing.cfscript.CFUnaryExpression;
import cfml.parsing.cfscript.CFVarDeclExpression;
import cfml.parsing.cfscript.script.CFFuncDeclStatement;

public class CFExpressionVisitor extends CFSCRIPTParserBaseVisitor<CFExpression> {
	
	// @Override
	// public CFExpression visitElement(ElementContext ctx) {
	// System.out.println("CFExpr.visitElement");
	// return super.visitElement(ctx);
	// }
	
	Stack<CFExpression> aggregator = new Stack<CFExpression>();
	
	@Override
	public CFExpression visitCompareExpression(CompareExpressionContext ctx) {
		if (ctx.getChildCount() == 0) {
			return null;
		}
		if (ctx.notExpression() != null) {
			CFUnaryExpression unaryExpression = new CFUnaryExpression(
					getTerminalToken(ctx.notExpression().getChild(0)), visit(ctx.notExpression().startExpression()));
			return unaryExpression;
		} else if (ctx.notNotExpression() != null) {
			CFUnaryExpression unaryExpression = new CFUnaryExpression(getTerminalToken(ctx.notNotExpression().getChild(
					0)), visit(ctx.notNotExpression().startExpression()));
			return unaryExpression;
		} else if (ctx.getChild(0).getChildCount() == 3) {
			CFBinaryExpression binaryExpression = new CFBinaryExpression(getTerminalToken(ctx.getChild(0).getChild(1)),
					visit(ctx.getChild(0).getChild(0)), visit(ctx.getChild(0).getChild(2)));
			return binaryExpression;
		} else {
			return visit(ctx.getChild(0));
		}
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
		if (ctx.VAR() != null) {
			CFVarDeclExpression declExpression = new CFVarDeclExpression(ctx.VAR().getSymbol(),
					(CFIdentifier) visitChildren(ctx), (CFExpression) null);
			return declExpression;
		}
		return super.visitForInKey(ctx);
	}
	
	@Override
	public CFExpression visitConstantExpression(ConstantExpressionContext ctx) {
		// System.out.println("CFExpr.visitConstantExpression");
		if (ctx.MINUS() != null) {
			return new CFUnaryExpression(ctx.MINUS().getSymbol(), visitConstantExpression(ctx.constantExpression()));
		} else if (ctx.LEFTPAREN() != null) {
			return new CFUnaryExpression(ctx.LEFTPAREN().getSymbol(), visitConstantExpression(ctx.constantExpression()));
		} else {
			return new CFLiteral(ctx.start);
		}
	}
	
	// @Override
	// public CFExpression visitExpression(ExpressionContext ctx) {
	// System.out.println("CFExpr.visitExpression");
	// return super.visitExpression(ctx);
	// }
	
	@Override
	public CFExpression visitStringLiteral(StringLiteralContext ctx) {
		return new CFLiteral(ctx.STRING_LITERAL().getSymbol());
	}
	
	@Override
	public CFExpression visitLocalAssignmentExpression(LocalAssignmentExpressionContext ctx) {
		// System.out.println("CFExpr.visitLocalAssignmentExpression");
		CFIdentifier identifier = (CFIdentifier) visit(ctx.getChild(1));
		CFExpression initExpression = visit(ctx.getChild(3));
		CFVarDeclExpression retval = new CFVarDeclExpression(ctx.start, identifier, initExpression);
		// return super.visitLocalAssignmentExpression(ctx);
		return retval;
	}
	
	@Override
	public CFExpression visitAssignmentExpression(AssignmentExpressionContext ctx) {
		// System.out.println("CFExpr.visitAssignmentExpression");
		if (ctx.right == null) {
			return visitStartExpression(ctx.left);
		} else {
			CFAssignmentExpression assignmentExpression = new CFAssignmentExpression(getTerminalToken(ctx.getChild(1)),
					visit(ctx.left), visit(ctx.right));
			return assignmentExpression;
		}
	}
	
	@Override
	public CFExpression visitBaseExpression(BaseExpressionContext ctx) {
		if (ctx.getChildCount() == 0) {
			return null;
		}
		if (ctx.unaryExpression() != null) {
			return visitUnaryExpression(ctx.unaryExpression());
		} else if (ctx.getChild(0).getChildCount() == 3) {
			CFBinaryExpression binaryExpression = new CFBinaryExpression(getTerminalToken(ctx.getChild(0).getChild(1)),
					visit(ctx.getChild(0).getChild(0)), visit(ctx.getChild(0).getChild(2)));
			return binaryExpression;
		} else {
			return visitChildren(ctx);
		}
	}
	
	@Override
	public CFExpression visitTernary(TernaryContext ctx) {
		CFTernaryExpression ternaryExpression = new CFTernaryExpression(ctx.getStart(), visit(ctx.getChild(0)),
				visit(ctx.getChild(2)), visit(ctx.getChild(4)));
		return ternaryExpression;
	}
	
	// @Override
	// public CFExpression visitEqualityOperator1(EqualityOperator1Context ctx) {
	// System.out.println("CFExpr.visitEqualityOperator1");
	// return super.visitEqualityOperator1(ctx);
	// }
	
	@Override
	public CFExpression visitUnaryExpression(UnaryExpressionContext ctx) {
		if (ctx.getChildCount() < 2) {
			return super.visitChildren(ctx);
		} else {
			// System.out.println("CFExpr.visitUnaryExpression");
			CFUnaryExpression unaryExpression = new CFUnaryExpression(ctx.start, super.visitChildren(ctx));
			return unaryExpression;
		}
	}
	
	@Override
	public CFExpression visitParentheticalExpression(ParentheticalExpressionContext ctx) {
		// System.out.println("CFExpr.visitParentheticalExpression");
		if (ctx.getChildCount() == 3) {
			CFUnaryExpression unaryExpression = new CFUnaryExpression(getTerminalToken(ctx.getChild(0)),
					visit(ctx.getChild(1)));
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
					args.putNamedArg(argCtx.name.getText(), visit(argCtx.startExpression()));
				} else {
					args.add(visit(argCtx));
				}
			}
		}
		CFFunctionExpression cfFunctionExpression = new CFFunctionExpression(ctx.LEFTPAREN().getSymbol(), null, args);
		return cfFunctionExpression;
	}
	
	/*
	 * @Override public CFExpression visitMemberExpression(MemberExpressionContext ctx) { if (ctx.getChildCount() < 2) {
	 * return visitChildren(ctx); } else { return visit(ctx.getChild(1)); } }
	 */
	@Override
	public CFExpression visitMemberExpressionB(MemberExpressionBContext ctx) {
		// System.out.println("CFExpr.visitMemberExpressionB+" + ctx.getChildCount());
		CFFullVarExpression fullVarExpression = new CFFullVarExpression(ctx.start, null);
		aggregator.push(fullVarExpression);
		CFExpression retval = visitChildren(ctx);
		aggregator.pop();
		// return fullVarExpression;
		return retval;
	}
	
	@Override
	public CFExpression visitInnerExpression(InnerExpressionContext ctx) {
		return new CFNestedExpression(ctx.POUND_SIGN(0).getSymbol(), visit(ctx.baseExpression()));
	}
	
	@Override
	public CFExpression visitArrayMemberExpression(ArrayMemberExpressionContext ctx) {
		// System.out.println("CFExpr.visitArrayMemberExpression");
		CFMember member = new CFMember(ctx.getStart(), visit(ctx.getChild(1)));
		return member;
	}
	
	//
	// @Override
	// public CFExpression visitMemberExpressionSuffix(MemberExpressionSuffixContext ctx) {
	// System.out.println("CFExpr.visitMemberExpressionSuffix");
	// return super.visitMemberExpressionSuffix(ctx);
	// }
	//
	// @Override
	// public CFExpression visitPropertyReferenceSuffix(PropertyReferenceSuffixContext ctx) {
	// System.out.println("CFExpr.visitPropertyReferenceSuffix");
	// return super.visitPropertyReferenceSuffix(ctx);
	// }
	//
	// @Override
	// public CFExpression visitIndexSuffix(IndexSuffixContext ctx) {
	// System.out.println("CFExpr.visitIndexSuffix");
	// return super.visitIndexSuffix(ctx);
	// }
	
	@Override
	public CFExpression visitPrimaryExpressionIRW(PrimaryExpressionIRWContext ctx) {
		System.out.println("CFExpr.visitPrimaryExpressionIRW:" + ctx.getText());
		return super.visitPrimaryExpressionIRW(ctx);
	}
	
	// @Override
	// public CFExpression visitReservedWord(ReservedWordContext ctx) {
	// System.out.println("CFExpr.visitReservedWord");
	// return super.visitReservedWord(ctx);
	// }
	
	// @Override
	// public CFExpression visitArgumentList(ArgumentListContext ctx) {
	// return null;
	// }
	
	@Override
	public CFExpression visitArgument(ArgumentContext ctx) {
		// System.out.println("CFExpr.visitArgument");
		CFExpression retval = super.visitArgument(ctx);
		return retval;
	}
	
	@Override
	public CFExpression visitIdentifier(IdentifierContext ctx) {
		// System.out.println("CFExpr.visitIdentifier " + ctx.getChild(0));
		if (ctx.getChildCount() > 1)
			return new CFIdentifier(ctx.start, ctx.getChild(0).toString(), ctx.getChild(1).toString());
		else
			return new CFIdentifier(ctx.start, ctx.getChild(0).toString());
	}
	
	// @Override
	// public CFExpression visitType(TypeContext ctx) {
	// System.out.println("CFExpr.visitType");
	// return super.visitType(ctx);
	// }
	
	// @Override
	// public CFExpression visitCfscriptKeywords(CfscriptKeywordsContext ctx) {
	// System.out.println("CFExpr.visitCfscriptKeywords");
	// return super.visitCfscriptKeywords(ctx);
	// }
	//
	@Override
	public CFExpression visitPrimaryExpression(PrimaryExpressionContext ctx) {
		// System.out.println("CFExpr.visitPrimaryExpression" + ctx.getChildCount() + " - " + ctx.getChild(0));
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
		// System.out.println("CFExpr.visitImplicitArray");
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
		// System.out.println("CFExpr.visitImplicitStruct");
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
	public CFExpression visitImplicitStructExpression(ImplicitStructExpressionContext ctx) {
		System.out.println("CFExpr.visitImplicitStructExpression");
		CFStructElementExpression elementExpression = new CFStructElementExpression(ctx.getStart(),
				makeIdentifier(visit(ctx.getChild(0))), visit(ctx.getChild(2)));
		// return super.visitImplicitStructExpression(ctx);
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
		// System.out.println("CFExpr.visitComponentPath");
		return super.visitChildren(ctx);
	}
	
	@Override
	public CFExpression visitLiteralExpression(LiteralExpressionContext ctx) {
		return new CFLiteral(ctx.start);
	}
	
	// @Override
	// public CFExpression visitTerminal(TerminalNode node) {
	// System.out.println("CFExpr.visitTerminal" + node);
	// return super.visitTerminal(node);
	// }
	//
	@Override
	public CFExpression visitFunctionCall(FunctionCallContext ctx) {
		// System.out.println("CFExpr.visitFunctionCall");
		ArgumentsVector args = new ArgumentsVector();
		if (ctx.argumentList() != null) {
			for (ArgumentContext argCtx : ctx.argumentList().argument()) {
				if (argCtx.name != null) {
					args.putNamedArg(argCtx.name.getText(), visit(argCtx.startExpression()));
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
	public CFExpression visitAnonymousFunctionDeclaration(AnonymousFunctionDeclarationContext ctx) {
		System.out.println("visitAnonymousFunctionDeclaration");
		CFFuncDeclStatement funcDeclStatement = (CFFuncDeclStatement) new CFScriptStatementVisitor()
				.visitAnonymousFunctionDeclaration(ctx);
		return new CFAnonymousFunctionExpression(ctx.FUNCTION().getSymbol(), funcDeclStatement);
	}
	
	@Override
	protected CFExpression aggregateResult(CFExpression aggregate, CFExpression nextResult) {
		if (nextResult == null) {
			return aggregate;
		}
		if (aggregate == null) {
			return nextResult;
		}
		// System.out.println("CFExpr.aggregateResult --------------------------"
		// + (aggregator.isEmpty() ? null : aggregator.peek().getClass()));
		// System.out.println("agg:" + aggregate.getClass() + " -> " + aggregate.Decompile(0));
		// System.out.println("next:" + nextResult.getClass() + " -> " + nextResult.Decompile(0));
		
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
			// System.out.println("New aggr:" + aggregate.getClass() + " -> " + aggregate.Decompile(0));
			// System.out.println("--------------------------------------------");
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
	
}
