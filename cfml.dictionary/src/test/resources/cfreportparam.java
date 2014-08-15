
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfreportparam extends GenericStartTagTypeCf {
	protected static final cfreportparam INSTANCE = new cfreportparam();

	private cfreportparam() {
		super("CFML if tag", "<cfreportparam", ">", EndTagType.NORMAL, true, true, false);
	}

}
					