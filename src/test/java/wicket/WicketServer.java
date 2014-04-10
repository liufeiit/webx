package wicket;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.thread.QueuedThreadPool;

/**
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月10日 下午8:02:38
 */
public class WicketServer {
	
	private static class App extends WebApplication {
		@Override
		public Class<? extends Page> getHomePage() {
			return AutoCompleteName.class;
		}
		
	}

	public static void main(String[] args) {
		Server server = new Server();
		SelectChannelConnector selectChannelConnector = new SelectChannelConnector();
		selectChannelConnector.setPort(8080);
		selectChannelConnector.setAcceptors(100);
		selectChannelConnector.setMaxIdleTime(3000);
		selectChannelConnector.setLowResourcesConnections(500);
		selectChannelConnector.setLowResourceMaxIdleTime(3000);
		server.addConnector(selectChannelConnector);
		QueuedThreadPool poolInstance = new QueuedThreadPool();
		poolInstance.setMaxThreads(500);
		poolInstance.setMaxIdleTimeMs(3000);
		poolInstance.setMinThreads(500);
		server.setThreadPool(poolInstance);
		Context context = new Context(server, "/", Context.NO_SESSIONS);
		ServletHolder wicketServletHolder = new ServletHolder(new WicketServlet());
		wicketServletHolder.setInitParameter("applicationClassName", App.class.getName());
		context.addServlet(wicketServletHolder, "/wicket/*");
		context.setEventListeners(null);
	}
}
