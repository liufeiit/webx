package wicket;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * Application class for hello world example.
 * 
 * @author Jonathan Locke
 */
public class HelloWorldApplication extends WebApplication
{
    /**
     * Constructor.
     */
    public HelloWorldApplication()
    {
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends Page> getHomePage()
    {
        return HelloWorld.class;
    }
}