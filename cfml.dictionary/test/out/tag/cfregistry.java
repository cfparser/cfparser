
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfregistry extends GenericStartTagTypeCf {
	protected static final cfregistry INSTANCE = new cfregistry();

	private cfregistry() {
		super("CFML if tag", "<cfregistry", ">", EndTagType.NORMAL, true, true, false);
	}

}
					