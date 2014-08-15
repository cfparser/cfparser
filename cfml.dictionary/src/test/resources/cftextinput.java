
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cftextinput extends GenericStartTagTypeCf {
	protected static final cftextinput INSTANCE = new cftextinput();

	private cftextinput() {
		super("CFML if tag", "<cftextinput", ">", EndTagType.NORMAL, true, true, false);
	}

}
					