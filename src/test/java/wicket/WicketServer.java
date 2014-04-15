package wicket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.WicketServlet;
import org.apache.wicket.settings.IExceptionSettings.UnexpectedExceptionDisplay;
import org.apache.wicket.settings.def.ApplicationSettings;
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
	
	public static class App extends WebApplication {
		public App() {
			super();
			setConfigurationType(RuntimeConfigurationType.DEPLOYMENT);
//			getApplicationSettings().set
//			getExceptionSettings().setUnexpectedExceptionDisplay(UnexpectedExceptionDisplay.getValues(c));
//			getSettings().setUnexpectedExceptionDisplay(ApplicationSettings.);
		}

		@Override
		public Class<? extends Page> getHomePage() {
			return AutoCompleteName.class;
		}
		
	}
	
	public static void main1(String[] args) throws Exception {
		StringBuffer b = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(WicketServer.class.getResourceAsStream("/419.txt")));
		String line = reader.readLine();
		while(line != null) {
			b.append(line);
			line = reader.readLine();
		}
		reader.close();
		System.out.println(b.toString());
	}

	public static void main(String[] args) throws Exception {
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
		Context context = new Context(server, "/", Context.SESSIONS);
//		ServletHolder wicketServletHolder = new ServletHolder(new WicketServlet());
		ServletHolder wicketServletHolder = new ServletHolder(new HttpServlet() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
					IOException {
				resp.setContentType("text/html;charset=UTF-8");
				StringBuffer b = new StringBuffer();
				BufferedReader reader = new BufferedReader(new InputStreamReader(WicketServer.class.getResourceAsStream("/419.txt")));
				String line = reader.readLine();
				while(line != null) {
					b.append(line);
					line = reader.readLine();
				}
				reader.close();
				resp.getWriter().write(b.toString());
				resp.getWriter().flush();
				resp.getWriter().close();
			}
		});
		wicketServletHolder.setInitParameter("applicationClassName", App.class.getName());
		wicketServletHolder.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/wicket/*");
		context.addServlet(wicketServletHolder, "/wicket/*");
		context.setEventListeners(null);
		server.start();
	}
}
