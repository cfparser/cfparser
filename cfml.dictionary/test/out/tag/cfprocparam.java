
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfprocparam extends GenericStartTagTypeCf {
	protected static final cfprocparam INSTANCE = new cfprocparam();

	private cfprocparam() {
		super("CFML if tag", "<cfprocparam", ">", EndTagType.NORMAL, true, true, false);
	}

}
					