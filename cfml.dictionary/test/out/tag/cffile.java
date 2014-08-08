
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cffile extends GenericStartTagTypeCf {
	protected static final cffile INSTANCE = new cffile();

	private cffile() {
		super("CFML if tag", "<cffile", ">", EndTagType.NORMAL, true, true, false);
	}

}
					