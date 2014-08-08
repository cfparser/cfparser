
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfexit extends GenericStartTagTypeCf {
	protected static final cfexit INSTANCE = new cfexit();

	private cfexit() {
		super("CFML if tag", "<cfexit", ">", EndTagType.NORMAL, true, true, false);
	}

}
					