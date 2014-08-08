
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfloginuser extends GenericStartTagTypeCf {
	protected static final cfloginuser INSTANCE = new cfloginuser();

	private cfloginuser() {
		super("CFML if tag", "<cfloginuser", ">", EndTagType.NORMAL, true, true, false);
	}

}
					