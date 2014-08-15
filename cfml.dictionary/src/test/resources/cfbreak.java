
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfbreak extends GenericStartTagTypeCf {
	protected static final cfbreak INSTANCE = new cfbreak();

	private cfbreak() {
		super("CFML if tag", "<cfbreak", ">", EndTagType.NORMAL, true, true, false);
	}

}
					