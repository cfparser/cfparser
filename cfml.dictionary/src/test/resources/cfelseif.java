
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfelseif extends GenericStartTagTypeCf {
	protected static final cfelseif INSTANCE = new cfelseif();

	private cfelseif() {
		super("CFML if tag", "<cfelseif", ">", EndTagType.NORMAL, true, true, false);
	}

}
					