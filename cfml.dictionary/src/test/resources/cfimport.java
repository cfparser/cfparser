
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfimport extends GenericStartTagTypeCf {
	protected static final cfimport INSTANCE = new cfimport();

	private cfimport() {
		super("CFML if tag", "<cfimport", ">", EndTagType.NORMAL, true, true, false);
	}

}
					