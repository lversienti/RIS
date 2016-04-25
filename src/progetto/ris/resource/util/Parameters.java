package progetto.ris.resource.util;

import java.io.IOException;
import java.util.Properties;

import com.hp.hpl.jena.rdf.model.Model;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;

public class Parameters {
	
	
	private VirtGraph set;
	private static Parameters parameters;
	
	  Properties configFile = new Properties();
	  Model model;
	  
	  private Parameters() throws IOException{
		  configFile.load(Parameters.class.getClassLoader().getResourceAsStream("config.properties"));
		  String conn_str = configFile.getProperty("conn_str");
		  String login = configFile.getProperty("login");
		  String pw = configFile.getProperty("pw");
		  String iriRicovero = configFile.getProperty("iriRicovero");
		  set = new VirtGraph (conn_str, login, pw);//mi collego al db
		  model = VirtModel.openDatabaseModel(iriRicovero, conn_str, login, pw);
	  }
	 
	  public static synchronized Parameters getInstance() throws IOException {
		  if (parameters == null)
			  parameters = new Parameters();
		  return parameters;
	  }

	  public Model getModelRDF(){
		  return model;
	  }

	  public VirtGraph getVirtGraph(){
		  return set;
	  }


	 
}
