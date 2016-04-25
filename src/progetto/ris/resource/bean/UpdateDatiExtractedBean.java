package progetto.ris.resource.bean;

public class UpdateDatiExtractedBean {
	
	private String exstractedCodURI;
	private String exstractedValueURI;
	private String exstractedCodICD9;
	private String exstractedCodICD9Value;


	//es: http://progetto.ris/resource/dnc_estrattaCod_2
	public String getExtractedCodURI() {  
		return exstractedCodURI;  
	}  

	public void setExtractedCodURI(String exstractedCodURI) {  
		this.exstractedCodURI = exstractedCodURI;  
	}  


	//es: http://progetto.ris/resource/dnc_estrattaTxT_2
	public String getExtractedValueURI() {  
		return exstractedValueURI;  
	}  

	public void setExtractedValueURI(String exstractedValueURI) {  
		this.exstractedValueURI = exstractedValueURI;  
	}  

	//es: 4169
	public String getExtractedCodICD9() {  
		return exstractedCodICD9;  
	}  

	public void setExtractedCodICD9(String exstractedCodICD9) {  
		this.exstractedCodICD9 = exstractedCodICD9;  
	} 

	//es: Malattia cardiopolmonare cronica, non specificata
	public String getExtractedCodICD9Value() {  
		return exstractedCodICD9Value;  
	}  
	
	public void setExtractedCodICD9Value(String exstractedCodICD9Value) {  
		this.exstractedCodICD9Value = exstractedCodICD9Value;  
	} 

	
}


