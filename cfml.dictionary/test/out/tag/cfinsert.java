
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfinsert extends GenericStartTagTypeCf {
	protected static final cfinsert INSTANCE = new cfinsert();

	private cfinsert() {
		super("CFML if tag", "<cfinsert", ">", EndTagType.NORMAL, true, true, false);
	}

}
					