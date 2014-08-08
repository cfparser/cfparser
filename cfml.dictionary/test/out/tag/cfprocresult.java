
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfprocresult extends GenericStartTagTypeCf {
	protected static final cfprocresult INSTANCE = new cfprocresult();

	private cfprocresult() {
		super("CFML if tag", "<cfprocresult", ">", EndTagType.NORMAL, true, true, false);
	}

}
					