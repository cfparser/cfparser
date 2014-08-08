
package cfml.parsing.cfmentat.tag;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.ParseText;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class cfinvokeargument extends GenericStartTagTypeCf {
	protected static final cfinvokeargument INSTANCE = new cfinvokeargument();

	private cfinvokeargument() {
		super("CFML if tag", "<cfinvokeargument", ">", EndTagType.NORMAL, true, true, false);
	}

}
					