
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfinput extends GenericStartTagTypeCf {
	protected static final cfinput INSTANCE = new cfinput();

	private cfinput() {
		super("CFML if tag", "<cfinput", ">", EndTagType.NORMAL, true, true, false);
	}

}
					