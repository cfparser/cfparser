
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfprint extends GenericStartTagTypeCf {
	protected static final cfprint INSTANCE = new cfprint();

	private cfprint() {
		super("CFML if tag", "<cfprint", ">", EndTagType.NORMAL, true, true, false);
	}

}
					