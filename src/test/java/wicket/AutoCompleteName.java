package wicket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.form.autocomplete.AutoCompleteTextField;

public class AutoCompleteName extends WebPage {
	private static final long serialVersionUID = 1L;
	private List<String> names = Arrays.asList(new String[] { "Kumarsun", "Ramkishore", "Kenneth", "Kingston", "Raju",
			"Rakesh", "Vijay", "Venkat", "Sachin" });

	public AutoCompleteName() {

		Form<String> form = new Form<String>("form");
		AutoCompleteTextField<String> txtName = new AutoCompleteTextField<String>("name", new Model<String>()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<String> getChoices(String input) {
				return names;
//				List<String> probables = new ArrayList<String>();
//				Iterator<String> iter = names.iterator();
//				while (iter.hasNext()) {
//					String name = (String) iter.next();
//					if (name.startsWith(input)) {
//						probables.add(name);
//					}
//				}
//				return probables;
			}

		};
		form.add(txtName);
		add(form);
	}
}