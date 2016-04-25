package progetto.ris.resource.bean;

public class UpdateDatiScartatiBean {
	
	private String scartatiCodURI;
	private String scartatiValueURI;
	private String scartatiCodICD9;
	private String scartatiCodICD9Value;


	//es: http://progetto.ris/resource/dnc_arricchitaCod_2 or  http://progetto.ris/resource/dnc_estrattaCod_2 
	public String getScartatiCodURI() {  
		return scartatiCodURI;  
	}  

	public void setScartatiCodURI(String scartatiCodURI) {  
		this.scartatiCodURI = scartatiCodURI;  
	}  


	//es: http://progetto.ris/resource/dnc_arricchitaTxT_2 or  http://progetto.ris/resource/dnc_estrattaTxT_2 
	public String getScartatiValueURI() {  
		return scartatiValueURI;  
	}  

	public void setScartatiValueURI(String scartatiValueURI) {  
		this.scartatiValueURI = scartatiValueURI;  
	}  

	//es: 4169
	public String getScartatiCodICD9() {  
		return scartatiCodICD9;  
	}  

	public void setScartatiCodICD9(String exstractedCodICD9) {  
		this.scartatiCodICD9 = exstractedCodICD9;  
	} 

	//es: Malattia cardiopolmonare cronica, non specificata
	public String getScartatiCodICD9Value() {  
		return scartatiCodICD9Value;  
	}  
	
	public void setScartatiCodICD9Value(String scartatiCodICD9Value) {  
		this.scartatiCodICD9Value = scartatiCodICD9Value;  
	} 

	
}
