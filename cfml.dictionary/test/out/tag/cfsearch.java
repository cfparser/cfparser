
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfsearch extends GenericStartTagTypeCf {
	protected static final cfsearch INSTANCE = new cfsearch();

	private cfsearch() {
		super("CFML if tag", "<cfsearch", ">", EndTagType.NORMAL, true, true, false);
	}

}
					