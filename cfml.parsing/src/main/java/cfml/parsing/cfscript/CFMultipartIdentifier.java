/* 
 *  Copyright (C) 2000 - 2010 TagServlet Ltd
 *
 *  This file is part of Open BlueDragon (OpenBD) CFML Server Engine.
 *  
 *  OpenBD is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  Free Software Foundation,version 3.
 *  
 *  OpenBD is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with OpenBD.  If not, see http://www.gnu.org/licenses/
 *  
 *  Additional permission under GNU GPL version 3 section 7
 *  
 *  If you modify this Program, or any covered work, by linking or combining 
 *  it with any of the JARS listed in the README.txt (or a modified version of 
 *  (that library), containing parts covered by the terms of that JAR, the 
 *  licensors of this Program grant you additional permission to convey the 
 *  resulting work. 
 *  README.txt @ http://www.openbluedragon.org/license/README.txt
 *  
 *  http://www.openbluedragon.org/
 */

package cfml.parsing.cfscript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CFMultipartIdentifier extends CFIdentifier implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	List<CFIdentifier> ids;
	
	public CFMultipartIdentifier(CFIdentifier... identifiers) {
		super(identifiers[0].getToken());
		this.scope = identifiers[0].getScope();
		this.ids = new ArrayList<CFIdentifier>(Arrays.asList(identifiers));
	}
	
	public String Decompile(int indent) {
		StringBuffer sb = new StringBuffer();
		sb.append(super.Decompile(indent));
		for (int i = 1; i < ids.size(); i++) {
			sb.append(".").append(ids.get(i).Decompile(0));
		}
		return sb.toString();
	}
	
	public String toString() {
		return Decompile(0);
	}
	
	public void addIdentifier(CFIdentifier id) {
		ids.add(id);
	}
	
	public List<CFIdentifier> getIds() {
		return ids;
	}
	
}
