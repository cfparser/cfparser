
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfftp extends GenericStartTagTypeCf {
	protected static final cfftp INSTANCE = new cfftp();

	private cfftp() {
		super("CFML if tag", "<cfftp", ">", EndTagType.NORMAL, true, true, false);
	}

}
					