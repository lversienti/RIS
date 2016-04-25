package progetto.ris.resource.bean;


public class UpdateDatiEnrichedBean {
	
	private String arrichitaCodURI;
	private String arrichitaValueURI;
	private String arrichitaCodICD9;
	private String arrichitaCodICD9Value;


	//es: http://progetto.ris/resource/dnc_arricchitaCod_2
	public String getArrichitaCodURI() {  
		return arrichitaCodURI;  
	}  

	public void setArrichitaCodURI(String arrichitaCodURI) {  
		this.arrichitaCodURI = arrichitaCodURI;  
	}  


	//es: http://progetto.ris/resource/dnc_arricchitaTxT_2
	public String getArrichitaValueURI() {  
		return arrichitaValueURI;  
	}  

	public void setArrichitaValueURI(String arrichitaValueURI) {  
		this.arrichitaValueURI = arrichitaValueURI;  
	}  

	//es: 4169
	public String getArrichitaCodICD9() {  
		return arrichitaCodICD9;  
	}  

	public void setArrichitaCodICD9(String arrichitaCodICD9) {  
		this.arrichitaCodICD9 = arrichitaCodICD9;  
	} 

	//es: Malattia cardiopolmonare cronica, non specificata
	public String getArrichitaCodICD9Value() {  
		return arrichitaCodICD9Value;  
	}  
	
	public void setArrichitaCodICD9Value(String arrichitaCodICD9Value) {  
		this.arrichitaCodICD9Value = arrichitaCodICD9Value;  
	} 

	
}
