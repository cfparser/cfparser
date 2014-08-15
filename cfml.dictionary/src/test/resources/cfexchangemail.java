
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfexchangemail extends GenericStartTagTypeCf {
	protected static final cfexchangemail INSTANCE = new cfexchangemail();

	private cfexchangemail() {
		super("CFML if tag", "<cfexchangemail", ">", EndTagType.NORMAL, true, true, false);
	}

}
					