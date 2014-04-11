package wicket;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Everybody's favorite example!
 * 
 * @author Jonathan Locke
 */
public class HelloWorld extends WebPage
{
    /**
     * Constructor
     */
    public HelloWorld()
    {
        add(new Label("message", "Hello World!"));
    }
}