package wicket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import wicket.markup.html.WebPage;
import wicket.markup.html.form.Form;
import wicket.model.Model;

public class AjaxWorld extends WebPage {
   	private List names = Arrays.asList(new String[] { "Kumarsun", "Ramkishore", 
   	  "Kenneth", "Kingston", "Raju", "Rakesh", "Vijay", "Venkat", "Sachin" });

   	public AjaxWorld() {

      Form form = new Form("form");
      AutoCompleteTextField txtName = new AutoCompleteTextField("name", new Model()){

         protected Iterator getChoices(String input) {
            			List probables = new ArrayList();
            			Iterator iter = names.iterator();
            			while (iter.hasNext()) {
               				String name = (String) iter.next();
               				if (name.startsWith(input)) {
                  					probables.add(name);
               				}
            			}
            			return probables.iterator();
         		}
      	};
   	form.add(txtName);
   	add(form);
   }
}