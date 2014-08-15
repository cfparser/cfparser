
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfcontent extends GenericStartTagTypeCf {
	protected static final cfcontent INSTANCE = new cfcontent();

	private cfcontent() {
		super("CFML if tag", "<cfcontent", ">", EndTagType.NORMAL, true, true, false);
	}

}
					