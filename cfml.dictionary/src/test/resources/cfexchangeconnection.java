
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfexchangeconnection extends GenericStartTagTypeCf {
	protected static final cfexchangeconnection INSTANCE = new cfexchangeconnection();

	private cfexchangeconnection() {
		super("CFML if tag", "<cfexchangeconnection", ">", EndTagType.NORMAL, true, true, false);
	}

}
					