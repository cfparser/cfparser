
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfpdf extends GenericStartTagTypeCf {
	protected static final cfpdf INSTANCE = new cfpdf();

	private cfpdf() {
		super("CFML if tag", "<cfpdf", ">", EndTagType.NORMAL, true, true, false);
	}

}
					