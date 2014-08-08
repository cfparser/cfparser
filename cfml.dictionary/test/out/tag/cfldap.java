
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfldap extends GenericStartTagTypeCf {
	protected static final cfldap INSTANCE = new cfldap();

	private cfldap() {
		super("CFML if tag", "<cfldap", ">", EndTagType.NORMAL, true, true, false);
	}

}
					