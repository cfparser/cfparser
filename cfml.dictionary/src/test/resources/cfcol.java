
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfcol extends GenericStartTagTypeCf {
	protected static final cfcol INSTANCE = new cfcol();

	private cfcol() {
		super("CFML if tag", "<cfcol", ">", EndTagType.NORMAL, true, true, false);
	}

}
					