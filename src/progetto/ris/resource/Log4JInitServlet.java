package progetto.ris.resource;

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

public class Log4JInitServlet implements ServletContextListener {

	private static final long serialVersionUID = 1L;
	
	
	public void init(ServletContext sc) throws ServletException {
		//String log4jLocation = config.getInitParameter("log4j-properties-location");
		//ServletContext sc = config.getServletContext();

			System.err.println("***ERRORE non c'è il file di properties No log4j-properties-location init param, so initializing log4j with BasicConfigurator");
			BasicConfigurator.configure();
		
			String webAppPath = sc.getRealPath("/") + "WEB-INF/";
			String log4jProp = webAppPath + "log4j.properties";
			System.out.println("log4j path: " + log4jProp);
			File yoMamaYesThisSaysYoMama = new File(log4jProp);
			if (yoMamaYesThisSaysYoMama.exists()) {
				PropertyConfigurator.configure(log4jProp);
			} else {
				System.err.println("*** " + log4jProp + " file not found, so initializing log4j with BasicConfigurator");
				BasicConfigurator.configure();
			}
	}


	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			init(arg0.getServletContext());
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}