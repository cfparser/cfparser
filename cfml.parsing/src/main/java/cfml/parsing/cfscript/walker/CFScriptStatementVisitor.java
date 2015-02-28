package cfml.parsing.cfscript.walker;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import cfml.CFSCRIPTParser.AbortStatementContext;
import cfml.CFSCRIPTParser.AccessTypeContext;
import cfml.CFSCRIPTParser.ArgumentContext;
import cfml.CFSCRIPTParser.ArgumentListContext;
import cfml.CFSCRIPTParser.AssignmentExpressionContext;
import cfml.CFSCRIPTParser.BaseExpressionContext;
import cfml.CFSCRIPTParser.CaseStatementContext;
import cfml.CFSCRIPTParser.CatchConditionContext;
import cfml.CFSCRIPTParser.CfmlFunctionContext;
import cfml.CFSCRIPTParser.CfmlfunctionStatementContext;
import cfml.CFSCRIPTParser.CfscriptKeywordsContext;
import cfml.CFSCRIPTParser.ComponentAttributeContext;
import cfml.CFSCRIPTParser.ComponentDeclarationContext;
import cfml.CFSCRIPTParser.ComponentGutsContext;
import cfml.CFSCRIPTParser.ComponentPathContext;
import cfml.CFSCRIPTParser.CompoundStatementContext;
import cfml.CFSCRIPTParser.ConditionContext;
import cfml.CFSCRIPTParser.DoWhileStatementContext;
import cfml.CFSCRIPTParser.ElementContext;
import cfml.CFSCRIPTParser.EndOfScriptBlockContext;
import cfml.CFSCRIPTParser.ExitStatementContext;
import cfml.CFSCRIPTParser.ExpressionContext;
import cfml.CFSCRIPTParser.FinallyStatementContext;
import cfml.CFSCRIPTParser.ForInKeyContext;
import cfml.CFSCRIPTParser.ForStatementContext;
import cfml.CFSCRIPTParser.FunctionAttributeContext;
import cfml.CFSCRIPTParser.FunctionDeclarationContext;
import cfml.CFSCRIPTParser.IdentifierContext;
import cfml.CFSCRIPTParser.IfStatementContext;
import cfml.CFSCRIPTParser.ImportStatementContext;
import cfml.CFSCRIPTParser.IncludeStatementContext;
import cfml.CFSCRIPTParser.IndexSuffixContext;
import cfml.CFSCRIPTParser.LocalAssignmentExpressionContext;
import cfml.CFSCRIPTParser.LockStatementContext;
import cfml.CFSCRIPTParser.ParamContext;
import cfml.CFSCRIPTParser.ParamStatementAttributesContext;
import cfml.CFSCRIPTParser.ParamStatementContext;
import cfml.CFSCRIPTParser.ParameterAttributeContext;
import cfml.CFSCRIPTParser.ParameterContext;
import cfml.CFSCRIPTParser.ParameterListContext;
import cfml.CFSCRIPTParser.ParameterTypeContext;
import cfml.CFSCRIPTParser.PropertyReferenceSuffixContext;
import cfml.CFSCRIPTParser.PropertyStatementContext;
import cfml.CFSCRIPTParser.ReservedWordContext;
import cfml.CFSCRIPTParser.ReturnStatementContext;
import cfml.CFSCRIPTParser.ScriptBlockContext;
import cfml.CFSCRIPTParser.StatementContext;
import cfml.CFSCRIPTParser.SwitchStatementContext;
import cfml.CFSCRIPTParser.TagOperatorStatementContext;
import cfml.CFSCRIPTParser.TernaryContext;
import cfml.CFSCRIPTParser.ThreadStatementContext;
import cfml.CFSCRIPTParser.ThrowStatementContext;
import cfml.CFSCRIPTParser.TransactionStatementContext;
import cfml.CFSCRIPTParser.TryCatchStatementContext;
import cfml.CFSCRIPTParser.TypeContext;
import cfml.CFSCRIPTParser.TypeSpecContext;
import cfml.CFSCRIPTParser.WhileStatementContext;
import cfml.CFSCRIPTParser.XorExpressionContext;
import cfml.CFSCRIPTParserBaseVisitor;
import cfml.parsing.cfscript.script.CFCase;
import cfml.parsing.cfscript.script.CFCatchStatement;
import cfml.parsing.cfscript.script.CFCompoundStatement;
import cfml.parsing.cfscript.script.CFExpressionStatement;
import cfml.parsing.cfscript.script.CFFuncDeclStatement;
import cfml.parsing.cfscript.script.CFIfStatement;
import cfml.parsing.cfscript.script.CFReturnStatement;
import cfml.parsing.cfscript.script.CFScriptStatement;

public class CFScriptStatementVisitor extends CFSCRIPTParserBaseVisitor<CFScriptStatement> {
	
	@Override
	public CFScriptStatement visit(ParseTree tree) {
		System.out.println("visit");
		return super.visit(tree);
	}
	
	@Override
	public CFScriptStatement visitScriptBlock(ScriptBlockContext ctx) {
		System.out.println("visitScriptBlock");
		return super.visitScriptBlock(ctx);
	}
	
	@Override
	public CFScriptStatement visitComponentDeclaration(ComponentDeclarationContext ctx) {
		System.out.println("visitCD");
		return super.visitComponentDeclaration(ctx);
	}
	
	@Override
	public CFScriptStatement visitEndOfScriptBlock(EndOfScriptBlockContext ctx) {
		System.out.println("visitScriptEnd");
		return super.visitEndOfScriptBlock(ctx);
	}
	
	@Override
	public CFScriptStatement visitElement(ElementContext ctx) {
		System.out.println("visitElement");
		return super.visitElement(ctx);
	}
	
	@Override
	public CFScriptStatement visitFunctionDeclaration(FunctionDeclarationContext ctx) {
		System.out.println("visitFunctionDeclaration");
		return super.visitFunctionDeclaration(ctx);
	}
	
	@Override
	public CFScriptStatement visitAccessType(AccessTypeContext ctx) {
		System.out.println("visitAccessType");
		return super.visitAccessType(ctx);
	}
	
	@Override
	public CFScriptStatement visitTypeSpec(TypeSpecContext ctx) {
		System.out.println("visitTypeSpec");
		return super.visitTypeSpec(ctx);
	}
	
	@Override
	public CFScriptStatement visitParameterList(ParameterListContext ctx) {
		System.out.println("visitParameterList");
		return super.visitParameterList(ctx);
	}
	
	@Override
	public CFScriptStatement visitParameter(ParameterContext ctx) {
		System.out.println("visitParameter");
		return super.visitParameter(ctx);
	}
	
	@Override
	public CFScriptStatement visitParameterType(ParameterTypeContext ctx) {
		System.out.println("visitParameterType");
		return super.visitParameterType(ctx);
	}
	
	@Override
	public CFScriptStatement visitComponentAttribute(ComponentAttributeContext ctx) {
		System.out.println("visitComponentAttribute");
		return super.visitComponentAttribute(ctx);
	}
	
	@Override
	public CFScriptStatement visitFunctionAttribute(FunctionAttributeContext ctx) {
		System.out.println("visitFunctionAttribute");
		return super.visitFunctionAttribute(ctx);
	}
	
	@Override
	public CFScriptStatement visitParameterAttribute(ParameterAttributeContext ctx) {
		System.out.println("visitParameterAttribute");
		return super.visitParameterAttribute(ctx);
	}
	
	@Override
	public CFScriptStatement visitCompoundStatement(CompoundStatementContext ctx) {
		System.out.println("visitCompoundStatement");
		CFCompoundStatement compoundStatement = new CFCompoundStatement(ctx.getStart());
		return aggregateResult(compoundStatement, visitChildren(ctx));
	}
	
	@Override
	public CFScriptStatement visitComponentGuts(ComponentGutsContext ctx) {
		System.out.println("visitComponentGuts");
		return super.visitComponentGuts(ctx);
	}
	
	@Override
	public CFScriptStatement visitStatement(StatementContext ctx) {
		System.out.println("visitStatement");
		if (ctx.getChild(0) instanceof LocalAssignmentExpressionContext
				|| ctx.getChild(0) instanceof AssignmentExpressionContext
				|| ctx.getChild(0) instanceof BaseExpressionContext || ctx.getChild(0) instanceof TernaryContext) {
			CFExpressionStatement expressionStmt = new CFExpressionStatement(new CFExpressionVisitor().visit(ctx
					.getChild(0)));
			return expressionStmt;
		}
		return visitChildren(ctx);
	}
	
	@Override
	public CFScriptStatement visitCondition(ConditionContext ctx) {
		System.out.println("visitCondition");
		return super.visitCondition(ctx);
	}
	
	@Override
	public CFScriptStatement visitReturnStatement(ReturnStatementContext ctx) {
		System.out.println("visitReturnStatement");
		CFReturnStatement returnStatement = new CFReturnStatement(ctx.getStart(), new CFExpressionVisitor().visit(ctx
				.getChild(1)));
		return returnStatement;
	}
	
	@Override
	public CFScriptStatement visitIfStatement(IfStatementContext ctx) {
		System.out.println("visitIfStatement");
		CFIfStatement ifStatement = new CFIfStatement(ctx.getStart(), new CFExpressionVisitor().visit(ctx.getChild(1)),
				visit(ctx.getChild(2)), ctx.getChildCount() > 4 ? visit(ctx.getChild(4)) : null);
		return ifStatement;
	}
	
	@Override
	public CFScriptStatement visitWhileStatement(WhileStatementContext ctx) {
		System.out.println("visitWhileStatement");
		return super.visitWhileStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitDoWhileStatement(DoWhileStatementContext ctx) {
		System.out.println("visitDoWhileStatement");
		return super.visitDoWhileStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitForStatement(ForStatementContext ctx) {
		System.out.println("visitForStatement");
		return super.visitForStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitForInKey(ForInKeyContext ctx) {
		System.out.println("visitForInKey");
		return super.visitForInKey(ctx);
	}
	
	@Override
	public CFScriptStatement visitTryCatchStatement(TryCatchStatementContext ctx) {
		System.out.println("visitTryCatchStatement");
		return super.visitTryCatchStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitCatchCondition(CatchConditionContext ctx) {
		CFCatchStatement retval = new CFCatchStatement(null, null, null);// TODO
		return super.visitCatchCondition(ctx);
	}
	
	@Override
	public CFScriptStatement visitFinallyStatement(FinallyStatementContext ctx) {
		System.out.println("visitFinallyStatement");
		return super.visitFinallyStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitSwitchStatement(SwitchStatementContext ctx) {
		System.out.println("visitSwitchStatement");
		return super.visitSwitchStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitCaseStatement(CaseStatementContext ctx) {
		System.out.println("visitCaseStatement");
		CFCase retval = new CFCase(null);// TODO
		return super.visitCaseStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitTagOperatorStatement(TagOperatorStatementContext ctx) {
		System.out.println("visitTagOperatorStatement");
		return super.visitTagOperatorStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitIncludeStatement(IncludeStatementContext ctx) {
		System.out.println("visitIncludeStatement");
		return super.visitIncludeStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitImportStatement(ImportStatementContext ctx) {
		System.out.println("visitImportStatement");
		return super.visitImportStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitTransactionStatement(TransactionStatementContext ctx) {
		System.out.println("visitTransactionStatement");
		return super.visitTransactionStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitCfmlfunctionStatement(CfmlfunctionStatementContext ctx) {
		System.out.println("visitCfmlfunctionStatement");
		return super.visitCfmlfunctionStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitCfmlFunction(CfmlFunctionContext ctx) {
		System.out.println("visitCfmlFunction");
		return super.visitCfmlFunction(ctx);
	}
	
	@Override
	public CFScriptStatement visitLockStatement(LockStatementContext ctx) {
		System.out.println("visitLockStatement");
		return super.visitLockStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitThreadStatement(ThreadStatementContext ctx) {
		System.out.println("visitThreadStatement");
		return super.visitThreadStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitAbortStatement(AbortStatementContext ctx) {
		System.out.println("visitAbortStatement");
		return super.visitAbortStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitThrowStatement(ThrowStatementContext ctx) {
		System.out.println("visitThrowStatement");
		return super.visitThrowStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitExitStatement(ExitStatementContext ctx) {
		System.out.println("visitExitStatement");
		return super.visitExitStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitParamStatement(ParamStatementContext ctx) {
		System.out.println("visitParamStatement");
		return super.visitParamStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitPropertyStatement(PropertyStatementContext ctx) {
		System.out.println("visitPropertyStatement");
		return super.visitPropertyStatement(ctx);
	}
	
	@Override
	public CFScriptStatement visitParamStatementAttributes(ParamStatementAttributesContext ctx) {
		System.out.println("visitParamStatementAttributes");
		return super.visitParamStatementAttributes(ctx);
	}
	
	@Override
	public CFScriptStatement visitParam(ParamContext ctx) {
		System.out.println("visitParam");
		return super.visitParam(ctx);
	}
	
	@Override
	public CFScriptStatement visitExpression(ExpressionContext ctx) {
		System.out.println("visitExpression");
		return super.visitExpression(ctx);
	}
	
	@Override
	public CFScriptStatement visitLocalAssignmentExpression(LocalAssignmentExpressionContext ctx) {
		// CFVarDeclExpression retval = new CFVarDeclExpression(ctx.start, null, null);
		System.out.println("visitLocalAssignmentExpression");
		System.out.println(ctx.getChildCount());
		System.out.println(ctx.getChild(0));
		System.out.println(ctx.getChild(1).getClass());
		System.out.println(ctx.getChild(2));
		System.out.println(ctx.getChild(3).getClass());
		System.out.println("===========");
		return super.visitLocalAssignmentExpression(ctx);
		// return retval;
	}
	
	@Override
	public CFScriptStatement visitAssignmentExpression(AssignmentExpressionContext ctx) {
		System.out.println("visitAssignmentExpression");
		return super.visitAssignmentExpression(ctx);
	}
	
	@Override
	public CFScriptStatement visitBaseExpression(BaseExpressionContext ctx) {
		System.out.println("visitBaseExpression");
		return super.visitBaseExpression(ctx);
	}
	
	@Override
	public CFScriptStatement visitTernary(TernaryContext ctx) {
		System.out.println("visitTernary");
		return super.visitTernary(ctx);
	}
	
	@Override
	public CFScriptStatement visitXorExpression(XorExpressionContext ctx) {
		System.out.println("visitXorExpression");
		return super.visitXorExpression(ctx);
	}
	
	@Override
	public CFScriptStatement visitPropertyReferenceSuffix(PropertyReferenceSuffixContext ctx) {
		System.out.println("visitTODO");
		return super.visitPropertyReferenceSuffix(ctx);
	}
	
	@Override
	public CFScriptStatement visitIndexSuffix(IndexSuffixContext ctx) {
		System.out.println("visitTODO");
		return super.visitIndexSuffix(ctx);
	}
	
	@Override
	public CFScriptStatement visitReservedWord(ReservedWordContext ctx) {
		System.out.println("visitTODO");
		return super.visitReservedWord(ctx);
	}
	
	@Override
	public CFScriptStatement visitArgumentList(ArgumentListContext ctx) {
		System.out.println("visitTODO");
		return super.visitArgumentList(ctx);
	}
	
	@Override
	public CFScriptStatement visitArgument(ArgumentContext ctx) {
		System.out.println("visitTODO");
		return super.visitArgument(ctx);
	}
	
	@Override
	public CFScriptStatement visitIdentifier(IdentifierContext ctx) {
		System.out.println("visitTODO");
		return super.visitIdentifier(ctx);
	}
	
	@Override
	public CFScriptStatement visitType(TypeContext ctx) {
		System.out.println("visitTODO");
		return super.visitType(ctx);
	}
	
	@Override
	public CFScriptStatement visitCfscriptKeywords(CfscriptKeywordsContext ctx) {
		System.out.println("visitTODO");
		return super.visitCfscriptKeywords(ctx);
	}
	
	@Override
	public CFScriptStatement visitComponentPath(ComponentPathContext ctx) {
		System.out.println("visitComponentPath");
		return super.visitComponentPath(ctx);
	}
	
	@Override
	public CFScriptStatement visitChildren(RuleNode node) {
		System.out.println("visitChildren" + node + node.getClass());
		return super.visitChildren(node);
	}
	
	@Override
	public CFScriptStatement visitTerminal(TerminalNode node) {
		System.out.println("visitTerminal" + node);
		return super.visitTerminal(node);
	}
	
	@Override
	public CFScriptStatement visitErrorNode(ErrorNode node) {
		System.out.println("visitErrorNode");
		return super.visitErrorNode(node);
	}
	
	@Override
	protected CFScriptStatement defaultResult() {
		return super.defaultResult();
	}
	
	@Override
	protected CFScriptStatement aggregateResult(CFScriptStatement aggregate, CFScriptStatement nextResult) {
		if (nextResult == null) {
			return aggregate;
		}
		if (aggregate == null) {
			return nextResult;
		}
		System.out.println("aggregateResult --------------------------");
		
		if (aggregate instanceof CFCompoundStatement) {
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
			return statement;
		}
	}
	
}
