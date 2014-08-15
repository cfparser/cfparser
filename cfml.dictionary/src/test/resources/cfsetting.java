
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfsetting extends GenericStartTagTypeCf {
	protected static final cfsetting INSTANCE = new cfsetting();

	private cfsetting() {
		super("CFML if tag", "<cfsetting", ">", EndTagType.NORMAL, true, true, false);
	}

}
					