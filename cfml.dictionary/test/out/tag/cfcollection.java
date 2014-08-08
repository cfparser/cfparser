
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfcollection extends GenericStartTagTypeCf {
	protected static final cfcollection INSTANCE = new cfcollection();

	private cfcollection() {
		super("CFML if tag", "<cfcollection", ">", EndTagType.NORMAL, true, true, false);
	}

}
					