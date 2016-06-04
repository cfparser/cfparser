package cfml.parsing.cfscript.walker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import cfml.CFSCRIPTParser.AbortStatementContext;
import cfml.CFSCRIPTParser.AnonymousFunctionDeclarationContext;
import cfml.CFSCRIPTParser.AssignmentExpressionContext;
import cfml.CFSCRIPTParser.BaseExpressionContext;
import cfml.CFSCRIPTParser.BreakStatementContext;
import cfml.CFSCRIPTParser.CaseStatementContext;
import cfml.CFSCRIPTParser.CatchConditionContext;
import cfml.CFSCRIPTParser.CfmlfunctionStatementContext;
import cfml.CFSCRIPTParser.CompareExpressionContext;
import cfml.CFSCRIPTParser.ComponentAttributeContext;
import cfml.CFSCRIPTParser.ComponentDeclarationContext;
import cfml.CFSCRIPTParser.CompoundStatementContext;
import cfml.CFSCRIPTParser.ContinueStatementContext;
import cfml.CFSCRIPTParser.DoWhileStatementContext;
import cfml.CFSCRIPTParser.ExitStatementContext;
import cfml.CFSCRIPTParser.ExpressionContext;
import cfml.CFSCRIPTParser.ForStatementContext;
import cfml.CFSCRIPTParser.FunctionAttributeContext;
import cfml.CFSCRIPTParser.FunctionDeclarationContext;
import cfml.CFSCRIPTParser.IfStatementContext;
import cfml.CFSCRIPTParser.ImportStatementContext;
import cfml.CFSCRIPTParser.IncludeStatementContext;
import cfml.CFSCRIPTParser.LocalAssignmentExpressionContext;
import cfml.CFSCRIPTParser.LockStatementContext;
import cfml.CFSCRIPTParser.ParamContext;
import cfml.CFSCRIPTParser.ParamStatementAttributesContext;
import cfml.CFSCRIPTParser.ParamStatementContext;
import cfml.CFSCRIPTParser.ParameterContext;
import cfml.CFSCRIPTParser.PropertyStatementContext;
import cfml.CFSCRIPTParser.RethrowStatmentContext;
import cfml.CFSCRIPTParser.ReturnStatementContext;
import cfml.CFSCRIPTParser.ScriptBlockContext;
import cfml.CFSCRIPTParser.StartExpressionContext;
import cfml.CFSCRIPTParser.StatementContext;
import cfml.CFSCRIPTParser.SwitchStatementContext;
import cfml.CFSCRIPTParser.ThreadStatementContext;
import cfml.CFSCRIPTParser.ThrowStatementContext;
import cfml.CFSCRIPTParser.TransactionStatementContext;
import cfml.CFSCRIPTParser.TryCatchStatementContext;
import cfml.CFSCRIPTParser.WhileStatementContext;
import cfml.CFSCRIPTParserBaseVisitor;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFFullVarExpression;
import cfml.parsing.cfscript.CFIdentifier;
import cfml.parsing.cfscript.script.CFAbortStatement;
import cfml.parsing.cfscript.script.CFBreakStatement;
import cfml.parsing.cfscript.script.CFCase;
import cfml.parsing.cfscript.script.CFCatchClause;
import cfml.parsing.cfscript.script.CFCatchStatement;
import cfml.parsing.cfscript.script.CFCompDeclStatement;
import cfml.parsing.cfscript.script.CFCompoundStatement;
import cfml.parsing.cfscript.script.CFContinueStatement;
import cfml.parsing.cfscript.script.CFDoWhileStatement;
import cfml.parsing.cfscript.script.CFExitStatement;
import cfml.parsing.cfscript.script.CFExpressionStatement;
import cfml.parsing.cfscript.script.CFForInStatement;
import cfml.parsing.cfscript.script.CFForStatement;
import cfml.parsing.cfscript.script.CFFuncDeclStatement;
import cfml.parsing.cfscript.script.CFFunctionParameter;
import cfml.parsing.cfscript.script.CFIfStatement;
import cfml.parsing.cfscript.script.CFImportStatement;
import cfml.parsing.cfscript.script.CFIncludeStatement;
import cfml.parsing.cfscript.script.CFLockStatement;
import cfml.parsing.cfscript.script.CFMLFunctionStatement;
import cfml.parsing.cfscript.script.CFParamStatement;
import cfml.parsing.cfscript.script.CFParsedAttributeStatement;
import cfml.parsing.cfscript.script.CFPropertyStatement;
import cfml.parsing.cfscript.script.CFReThrowStatement;
import cfml.parsing.cfscript.script.CFReturnStatement;
import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.cfscript.script.CFSwitchStatement;
import cfml.parsing.cfscript.script.CFThreadStatement;
import cfml.parsing.cfscript.script.CFThrowStatement;
import cfml.parsing.cfscript.script.CFTransactionStatement;
import cfml.parsing.cfscript.script.CFTryCatchStatement;
import cfml.parsing.cfscript.script.CFWhileStatement;
import cfml.parsing.util.ExpressionUtils;

public class CFScriptStatementVisitor extends CFSCRIPTParserBaseVisitor<CFScriptStatement> {
	
	Stack<Object> aggregator = new Stack<Object>();
	CFExpressionVisitor cfExpressionVisitor = new CFExpressionVisitor();
	
	@Override
	public CFScriptStatement visitScriptBlock(ScriptBlockContext ctx) {
		// System.out.println("visitScriptBlock");
		return super.visitScriptBlock(ctx);
	}
	
	@Override
	public CFScriptStatement visitComponentDeclaration(ComponentDeclarationContext ctx) {
		Map<CFExpression, CFExpression> _attr = new HashMap<CFExpression, CFExpression>();
		CFCompDeclStatement compDeclStatement = new CFCompDeclStatement(ctx.COMPONENT().getSymbol(), _attr,
				visit(ctx.componentGuts()));
		for (ComponentAttributeContext attr : ctx.componentAttribute()) {
			CFIdentifier name = (CFIdentifier) visitExpression(attr.id);
			if (attr.prefix != null) {
				CFFullVarExpression fullVar = new CFFullVarExpression(attr.prefix.getStart(), name);
				fullVar.addMember(visitExpression(attr.prefix));
				_attr.put(fullVar, visitExpression(attr.startExpression()));
			} else {
				_attr.put(name, visitExpression(attr.startExpression()));
			}
		}
		return compDeclStatement;
	}
	
	//
	// @Override
	// public CFScriptStatement visitElement(ElementContext ctx) {
	// // System.out.println("visitElement");
	// return super.visitElement(ctx);
	// }
	
	@Override
	public CFScriptStatement visitFunctionDeclaration(FunctionDeclarationContext ctx) {
		// System.out.println("visitFunctionDeclaration");
		List<CFFunctionParameter> parameters = new ArrayList<CFFunctionParameter>();
		aggregator.push(parameters);
		visitChildren(ctx.parameterList());
		aggregator.pop();
		
		Map<CFIdentifier, CFExpression> attributes = new HashMap<CFIdentifier, CFExpression>();
		for (FunctionAttributeContext attr : ctx.functionAttribute()) {
			attributes.put((CFIdentifier) cfExpressionVisitor.visitIdentifier(attr.identifier()),
					cfExpressionVisitor.visit(attr.startExpression()));
		}
		
		CFFuncDeclStatement funcDeclStatement = new CFFuncDeclStatement(ctx.FUNCTION().getSymbol(),
				(CFIdentifier) visitExpression(ctx.identifier()), getText(ctx.accessType()),
				(CFIdentifier) visitExpression(ctx.typeSpec()), parameters, attributes, visit(ctx.body));
		return funcDeclStatement;
	}
	
	// @Override
	// public CFScriptStatement visitTypeSpec(TypeSpecContext ctx) {
	// System.out.println("visitTypeSpec");
	// return super.visitTypeSpec(ctx);
	// }
	
	// @Override
	// public CFScriptStatement visitParameterList(ParameterListContext ctx) {
	// System.out.println("visitParameterList");
	// return super.visitParameterList(ctx);
	// }
	
	@Override
	public CFScriptStatement visitAnonymousFunctionDeclaration(AnonymousFunctionDeclarationContext ctx) {
		List<CFFunctionParameter> parameters = new ArrayList<CFFunctionParameter>();
		aggregator.push(parameters);
		visitChildren(ctx.parameterList());
		aggregator.pop();
		
		Map<CFIdentifier, CFExpression> attributes = new HashMap<CFIdentifier, CFExpression>();
		for (FunctionAttributeContext attr : ctx.functionAttribute()) {
			attributes.put((CFIdentifier) cfExpressionVisitor.visitIdentifier(attr.identifier()),
					cfExpressionVisitor.visit(attr.startExpression()));
		}
		
		CFFuncDeclStatement funcDeclStatement = new CFFuncDeclStatement(ctx.FUNCTION().getSymbol(), (CFIdentifier) null,
				getText(ctx.accessType()), (CFIdentifier) visitExpression(ctx.typeSpec()), parameters, attributes,
				visit(ctx.body));
		return funcDeclStatement;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CFScriptStatement visitParameter(ParameterContext ctx) {
		// System.out.println("visitParameter");
		
		CFExpression defaultExpr = (ctx.startExpression() != null)
				? cfExpressionVisitor.visitStartExpression(ctx.startExpression()) : null;
		CFFunctionParameter functionParameter = new CFFunctionParameter(
				(CFIdentifier) cfExpressionVisitor.visitIdentifier(ctx.identifier()), ctx.REQUIRED() != null,
				getText(ctx.parameterType()), defaultExpr);
		if (!aggregator.isEmpty() && aggregator.peek() instanceof List<?>) {
			((List<CFFunctionParameter>) aggregator.peek()).add(functionParameter);
		}
		return super.visitParameter(ctx);
	}
	
	// @Override
	// public CFScriptStatement visitParameterType(ParameterTypeContext ctx) {
	// System.out.println("visitParameterType");
	// return super.visitParameterType(ctx);
	// }
	
	// @Override
	// public CFScriptStatement visitFunctionAttribute(FunctionAttributeContext ctx) {
	// System.out.println("visitFunctionAttribute");
	// return super.visitFunctionAttribute(ctx);
	// }
	
	// @Override
	// public CFScriptStatement visitParameterAttribute(ParameterAttributeContext ctx) {
	// System.out.println("visitParameterAttribute");
	// return super.visitParameterAttribute(ctx);
	// }
	
	@Override
	public CFScriptStatement visitCompoundStatement(CompoundStatementContext ctx) {
		// System.out.println("visitCompoundStatement");
		CFCompoundStatement compoundStatement = new CFCompoundStatement(ctx.getStart());
		if (ctx.statement().size() > 0) {
			aggregator.push(compoundStatement);
			CFScriptStatement result = visitChildren(ctx);
			if (result != compoundStatement) {
				compoundStatement.add(result);
			}
			aggregator.pop();
		}
		return compoundStatement;
	}
	
	//
	// @Override
	// public CFScriptStatement visitComponentGuts(ComponentGutsContext ctx) {
	// System.out.println("visitComponentGuts");
	// return super.visitComponentGuts(ctx);
	// }
	//
	@Override
	public CFScriptStatement visitStatement(StatementContext ctx) {
		// System.out.println("visitStatement");
		if (ctx.getChild(0) instanceof LocalAssignmentExpressionContext || ctx.getChild(0) instanceof AssignmentExpressionContext
				|| ctx.getChild(0) instanceof BaseExpressionContext || ctx.getChild(0) instanceof CompareExpressionContext
		// || ctx.getChild(0) instanceof TagOperatorStatementContext
		) {
			CFExpressionStatement expressionStmt = new CFExpressionStatement(cfExpressionVisitor.visit(ctx.getChild(0)));
			// System.out.println("visitStatement.b" + expressionStmt.Decompile(0));
			return expressionStmt;
		}
		return visitChildren(ctx);
	}
	
	@Override
	public CFScriptStatement visitReturnStatement(ReturnStatementContext ctx) {
		// System.out.println("visitReturnStatement");
		CFReturnStatement returnStatement = new CFReturnStatement(ctx.getStart(), cfExpressionVisitor.visit(ctx.getChild(1)));
		return returnStatement;
	}
	
	@Override
	public CFScriptStatement visitIfStatement(IfStatementContext ctx) {
		// System.out.println("visitIfStatement");
		CFIfStatement ifStatement = new CFIfStatement(ctx.getStart(), cfExpressionVisitor.visit(ctx.getChild(1)),
				visit(ctx.getChild(2)), ctx.getChildCount() > 4 ? visit(ctx.getChild(4)) : null);
		return ifStatement;
	}
	
	@Override
	public CFScriptStatement visitWhileStatement(WhileStatementContext ctx) {
		// System.out.println("visitWhileStatement");
		CFWhileStatement whileStatement = new CFWhileStatement(ctx.WHILE().getSymbol(), visitExpression(ctx.condition()),
				visit(ctx.statement()));
		return whileStatement;
	}
	
	@Override
	public CFScriptStatement visitDoWhileStatement(DoWhileStatementContext ctx) {
		// System.out.println("visitDoWhileStatement");
		CFDoWhileStatement doWhileStatement = new CFDoWhileStatement(ctx.DO().getSymbol(), visitExpression(ctx.condition()),
				visit(ctx.statement()));
		return doWhileStatement;
	}
	
	@Override
	public CFScriptStatement visitForStatement(ForStatementContext ctx) {
		// System.out.println("visitForStatement");
		if (ctx.forInKey() != null) {
			CFForInStatement forInStatement = new CFForInStatement(ctx.FOR().getSymbol(),
					cfExpressionVisitor.visit(ctx.forInKey()), cfExpressionVisitor.visit(ctx.inExpr), visit(ctx.statement()));
			return forInStatement;
		} else {
			ParserRuleContext localOrInit = ExpressionUtils.coalesce(ctx.localAssignmentExpression(), ctx.initExpression);
			CFExpression LocalOrInitVisited = cfExpressionVisitor.visit(localOrInit);
			CFForStatement forStatement = new CFForStatement(ctx.FOR().getSymbol(), LocalOrInitVisited,
					cfExpressionVisitor.visit(ctx.condExpression),
					cfExpressionVisitor.visit(ExpressionUtils.coalesce(ctx.incrExpression, ctx.incrExpression2)),
					visit(ctx.statement()));
			return forStatement;
		}
		// return super.visitForStatement(ctx);
	}
	
	//
	// @Override
	// public CFScriptStatement visitForInKey(ForInKeyContext ctx) {
	// System.out.println("visitForInKey");
	// return super.visitForInKey(ctx);
	// }
	
	@Override
	public CFScriptStatement visitTryCatchStatement(TryCatchStatementContext ctx) {
		// System.out.println("visitTryCatchStatement");
		List<CFCatchClause> _catches = new ArrayList<CFCatchClause>();
		for (CatchConditionContext catchCond : ctx.catchCondition()) {
			CFCatchStatement clause = new CFCatchStatement(getText(catchCond.typeSpec()),
					(CFIdentifier) cfExpressionVisitor.visit(catchCond.identifier()), visit(catchCond.compoundStatement()));
			_catches.add(clause);
			// System.out.println("visitTryCatchStatement." + visit(catchCond.compoundStatement()).Decompile(0));
		}
		CFTryCatchStatement tryCatchStatement = new CFTryCatchStatement(ctx.start, visit(ctx.statement()), _catches,
				visitNullSafe(ctx.finallyStatement()));
		return tryCatchStatement;
	}
	
	@Override
	public CFScriptStatement visitCatchCondition(CatchConditionContext ctx) {
		return new CFCatchStatement((CFIdentifier) visit(ctx.typeSpec()), (CFIdentifier) visit(ctx.identifier()),
				visit(ctx.compoundStatement()));
	}
	
	@Override
	public CFScriptStatement visitSwitchStatement(SwitchStatementContext ctx) {
		// System.out.println("visitSwitchStatement");
		List<CFCase> _cases = new ArrayList<CFCase>();
		CFSwitchStatement switchStatement = new CFSwitchStatement(ctx.SWITCH().getSymbol(),
				cfExpressionVisitor.visit(ctx.condition()), _cases);
		aggregator.push(switchStatement);
		for (CaseStatementContext caseSt : ctx.caseStatement()) {
			_cases.add((CFCase) visit(caseSt));
		}
		aggregator.pop();
		return switchStatement;
	}
	
	@Override
	public CFScriptStatement visitCaseStatement(CaseStatementContext ctx) {
		// System.out.println("visitCaseStatement");
		List<CFScriptStatement> statements = new ArrayList<CFScriptStatement>();
		for (StatementContext statement : ctx.statement()) {
			CFScriptStatement st = visit(statement);
			if (st != null) {
				statements.add(st);
			}
		}
		if (ctx.DEFAULT() != null) {
			return new CFCase(statements);
		} else if (ctx.constantExpression() != null) {
			return new CFCase(cfExpressionVisitor.visit(ctx.constantExpression()), statements);
		} else {
			return new CFCase(cfExpressionVisitor.visit(ctx.memberExpression()), statements);
		}
	}
	
	@Override
	public CFScriptStatement visitBreakStatement(BreakStatementContext ctx) {
		return new CFBreakStatement(ctx.BREAK().getSymbol());
	}
	
	@Override
	public CFScriptStatement visitContinueStatement(ContinueStatementContext ctx) {
		return new CFContinueStatement(ctx.CONTINUE().getSymbol());
	}
	
	@Override
	public CFScriptStatement visitRethrowStatment(RethrowStatmentContext ctx) {
		// System.out.println("visitIncludeStatement");
		return new CFReThrowStatement(ctx.RETHROW().getSymbol());
	}
	
	@Override
	public CFScriptStatement visitIncludeStatement(IncludeStatementContext ctx) {
		// System.out.println("visitIncludeStatement");
		CFIncludeStatement includeStatement = new CFIncludeStatement(ctx.INCLUDE().getSymbol(),
				cfExpressionVisitor.visit(ctx.baseExpression()));
		return includeStatement;
	}
	
	@Override
	public CFScriptStatement visitImportStatement(ImportStatementContext ctx) {
		// System.out.println("visitImportStatement");
		CFImportStatement importStatement = new CFImportStatement(ctx.IMPORT().getSymbol(), visitExpression(ctx.componentPath()),
				ctx.all != null);
		return importStatement;
	}
	
	@Override
	public CFScriptStatement visitTransactionStatement(TransactionStatementContext ctx) {
		// System.out.println("visitTransactionStatement");
		Map<CFIdentifier, CFExpression> _attr = new HashMap<CFIdentifier, CFExpression>();
		CFTransactionStatement transactionStatement = new CFTransactionStatement(ctx.TRANSACTION().getSymbol(), _attr,
				visitNullSafe(ctx.compoundStatement()));
		if (ctx.paramStatementAttributes() != null) {
			aggregator.push(transactionStatement);
			visitChildren(ctx.paramStatementAttributes());
			aggregator.pop();
		}
		return transactionStatement;
	}
	
	@Override
	public CFScriptStatement visitCfmlfunctionStatement(CfmlfunctionStatementContext ctx) {
		// System.out.println("visitCfmlfunctionStatement");
		Map<CFIdentifier, CFExpression> _attr = new HashMap<CFIdentifier, CFExpression>();
		CFMLFunctionStatement cfmlFunctionStatement = new CFMLFunctionStatement(ctx.start, ctx.cfmlFunction().start, _attr,
				visitNullSafe(ctx.compoundStatement()));
		if (ctx.paramStatementAttributes() != null) {
			aggregator.push(cfmlFunctionStatement);
			visitChildren(ctx.paramStatementAttributes());
			aggregator.pop();
		}
		return cfmlFunctionStatement;
	}
	
	// @Override
	// public CFScriptStatement visitCfmlFunction(CfmlFunctionContext ctx) {
	// System.out.println("visitCfmlFunction");
	// return super.visitCfmlFunction(ctx);
	// }
	//
	@Override
	public CFScriptStatement visitLockStatement(LockStatementContext ctx) {
		// System.out.println("visitLockStatement");
		Map<CFIdentifier, CFExpression> _attr = new HashMap<CFIdentifier, CFExpression>();
		CFLockStatement lockStatement = new CFLockStatement(ctx.LOCK().getSymbol(), _attr, visit(ctx.compoundStatement()));
		aggregator.push(lockStatement);
		visitChildren(ctx.paramStatementAttributes());
		aggregator.pop();
		return lockStatement;
	}
	
	@Override
	public CFScriptStatement visitThreadStatement(ThreadStatementContext ctx) {
		// System.out.println("visitThreadStatement");
		Map<CFIdentifier, CFExpression> _attr = new HashMap<CFIdentifier, CFExpression>();
		CFThreadStatement threadStatement = new CFThreadStatement(ctx.THREAD().getSymbol(), _attr,
				visitNullSafe(ctx.compoundStatement()));
		aggregator.push(threadStatement);
		visitChildren(ctx.paramStatementAttributes());
		aggregator.pop();
		return threadStatement;
	}
	
	@Override
	public CFScriptStatement visitAbortStatement(AbortStatementContext ctx) {
		// System.out.println("visitAbortStatement");
		CFAbortStatement abortStatement = new CFAbortStatement(ctx.ABORT().getSymbol(), visitExpression(ctx.memberExpression()));
		return abortStatement;
	}
	
	@Override
	public CFScriptStatement visitThrowStatement(ThrowStatementContext ctx) {
		// System.out.println("visitThrowStatement");
		CFThrowStatement throwStatement = new CFThrowStatement(ctx.THROW().getSymbol(), visitExpression(ctx.memberExpression()));
		return throwStatement;
	}
	
	@Override
	public CFScriptStatement visitExitStatement(ExitStatementContext ctx) {
		// System.out.println("visitExitStatement");
		CFExitStatement exitStatement = new CFExitStatement(ctx.EXIT().getSymbol(), visitExpression(ctx.memberExpression()));
		return exitStatement;
	}
	
	@Override
	public CFScriptStatement visitParamStatement(ParamStatementContext ctx) {
		// System.out.println("visitParamStatement");
		Map<CFIdentifier, CFExpression> _attributes = new HashMap<CFIdentifier, CFExpression>();
		CFParamStatement paramStatement = new CFParamStatement(ctx.PARAM().getSymbol(), _attributes);
		aggregator.push(paramStatement);
		visitChildren(ctx.paramStatementAttributes());
		aggregator.pop();
		return paramStatement;
	}
	
	@Override
	public CFScriptStatement visitPropertyStatement(PropertyStatementContext ctx) {
		// System.out.println("visitPropertyStatement");
		Map<CFIdentifier, CFExpression> _attributes = new HashMap<CFIdentifier, CFExpression>();
		CFPropertyStatement propertyStatement = new CFPropertyStatement(ctx.PROPERTY().getSymbol(), _attributes);
		aggregator.push(propertyStatement);
		if (ctx.paramStatementAttributes() != null) {
			visitChildren(ctx.paramStatementAttributes());
		} else {
			propertyStatement.setIsShortHand(true);
			propertyStatement.setPropertyName(visitExpression(ctx.identifier()));
			propertyStatement.setPropertyType(visitExpression(ctx.type()));
			/*
			 * if (ctx.type() != null) { propertyStatement.getAttributes().put(new CFIdentifier(ctx.type().start,
			 * "type"), visitExpression(ctx.type())); } propertyStatement.getAttributes().put(new
			 * CFIdentifier(ctx.identifier().start, "name"), visitExpression(ctx.identifier()));
			 */
		}
		aggregator.pop();
		return propertyStatement;
	}
	
	@Override
	public CFScriptStatement visitParamStatementAttributes(ParamStatementAttributesContext ctx) {
		// System.out.println("visitParamStatementAttributes");
		return super.visitParamStatementAttributes(ctx);
	}
	
	@Override
	public CFScriptStatement visitParam(ParamContext ctx) {
		// System.out.println("visitParam");
		if (!aggregator.isEmpty() && aggregator.peek() instanceof CFParsedAttributeStatement) {
			((CFParsedAttributeStatement) aggregator.peek()).getAttributes()
					.put((CFIdentifier) visitExpression(ctx.multipartIdentifier()), visitExpression(ctx.startExpression()));
			return null;
		} else {
			return super.visitParam(ctx);
		}
	}
	
	@Override
	public CFScriptStatement visitExpression(ExpressionContext ctx) {
		// System.out.println("visitExpression");
		return super.visitExpression(ctx);
	}
	
	@Override
	public CFScriptStatement visitLocalAssignmentExpression(LocalAssignmentExpressionContext ctx) {
		return new CFExpressionStatement(visitExpression(ctx));
	}
	
	@Override
	public CFScriptStatement visitAssignmentExpression(AssignmentExpressionContext ctx) {
		return new CFExpressionStatement(visitExpression(ctx));
	}
	
	@Override
	public CFScriptStatement visitStartExpression(StartExpressionContext ctx) {
		return new CFExpressionStatement(visitExpression(ctx));
	}
	
	//
	// @Override
	// public CFScriptStatement visitChildren(RuleNode node) {
	// System.out.println("visitChildren" + node + node.getClass());
	// return super.visitChildren(node);
	// }
	//
	// @Override
	// public CFScriptStatement visitTerminal(TerminalNode node) {
	// System.out.println("visitTerminal" + node);
	// return super.visitTerminal(node);
	// }
	
	@Override
	protected CFScriptStatement aggregateResult(CFScriptStatement aggregate, CFScriptStatement nextResult) {
		if (nextResult == null) {
			return aggregate;
		}
		if (aggregate == null) {
			return nextResult;
		}
		// System.out.println("aggregateResult --------------------------");
		// try {
		// System.out.println("agg:" + aggregate.getClass() + " -> " + aggregate.Decompile(0));
		// } catch (Exception e) {
		// System.out.println("agg:" + aggregate.getClass() + " e-> " + e.getMessage());
		// }
		//
		// try {
		// System.out.println("next:" + nextResult.getClass() + " -> " + nextResult.Decompile(0));
		// } catch (Exception e) {
		// System.out.println("next:" + nextResult.getClass() + " e-> " + e.getMessage());
		// }
		
		try {
			if (!aggregator.isEmpty() && aggregator.peek() instanceof CFCompoundStatement && aggregate != aggregator.peek()) {
				((CFCompoundStatement) aggregator.peek()).add(aggregate);
				((CFCompoundStatement) aggregator.peek()).add(nextResult);
				return (CFCompoundStatement) aggregator.peek();
			} else if (aggregate instanceof CFCompoundStatement) {
				((CFCompoundStatement) aggregate).add(nextResult);
				return aggregate;
			} else {
				CFCompoundStatement statement = new CFCompoundStatement();
				if (aggregate instanceof CFFuncDeclStatement) {
					statement.addFunction(aggregate);
				} else {
					statement.add(aggregate);
				}
				if (nextResult instanceof CFFuncDeclStatement) {
					statement.addFunction(nextResult);
				} else {
					statement.add(nextResult);
				}
				aggregate = statement;
				return statement;
			}
		} finally {
			// try {
			// System.out.println("New aggr:" + aggregate.getClass() + " -> " + aggregate.Decompile(0));
			// } catch (Exception e) {
			// System.out.println("New aggr:" + aggregate.getClass() + " e-> " + e.getMessage());
			// }
			//
			// System.out.println("--------------------------------------------");
		}
	}
	
	String getText(ParseTree t) {
		if (t == null)
			return null;
		else if (t.getChildCount() > 0) {
			return getText(t.getChild(0));
		} else
			return t.getText();
	}
	
	CFScriptStatement visitNullSafe(ParseTree tree) {
		if (tree == null) {
			return null;
		}
		return visit(tree);
	}
	
	CFExpression visitExpression(ParseTree tree) {
		if (tree != null) {
			return cfExpressionVisitor.visit(tree);
		}
		return null;
	}
	
}
