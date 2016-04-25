package progetto.ris.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/hello")
public class Hello {
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayTextHello(){
		return "Hello Loredana";
	}

	
	@GET
	@Produces(MediaType.TEXT_XML)
	public String sayXmlHello(){
		   return "<?xml version=\"1.0\"?>" + "<hello> Hello Loredana" + "</hello>";
	}

	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello(){
		 return "<html> " + "<title>" + "Hello Loredana" + "</title>"
			        + "<body><h1>" + "Hello Loredana" + "</body></h1>" + "</html> ";
	}
}
