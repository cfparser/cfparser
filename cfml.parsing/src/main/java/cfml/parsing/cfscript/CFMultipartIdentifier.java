package cfml.parsing.cfscript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cfml.parsing.cfscript.script.CFScriptStatement;
import cfml.parsing.util.ArrayBuilder;

public class CFMultipartIdentifier extends CFIdentifier implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	List<CFIdentifier> ids;
	
	public CFMultipartIdentifier(CFIdentifier... identifiers) {
		super(identifiers[0].getToken());
		this.scope = identifiers[0].getScope();
		this.ids = new ArrayList<CFIdentifier>(Arrays.asList(identifiers));
		ids.forEach(id -> id.setParent(this));
	}
	
	@Override
	public String Decompile(int indent) {
		StringBuffer sb = new StringBuffer();
		sb.append(super.Decompile(indent));
		for (int i = 1; i < ids.size(); i++) {
			sb.append(".").append(ids.get(i).Decompile(0));
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return Decompile(0);
	}
	
	public void addIdentifier(CFIdentifier id) {
		ids.add(id);
	}
	
	public List<CFIdentifier> getIds() {
		return ids;
	}
	
	@Override
	public List<CFExpression> decomposeExpression() {
		List<CFExpression> retval = new ArrayList<CFExpression>();
		retval.addAll(ids);
		return retval;
	}
	
	@Override
	public List<CFScriptStatement> decomposeScript() {
		return ArrayBuilder.createCFScriptStatement();
	}
}
