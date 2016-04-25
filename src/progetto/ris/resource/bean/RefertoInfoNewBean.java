package progetto.ris.resource.bean;

public class RefertoInfoNewBean {

	boolean ricoveroBool;
	private String pazienteId;
	private String esameMedicoCodice;
	private String esameMedicoValue;
	private String refertoString;


	public String getEsameMedicoCodice() {
		return esameMedicoCodice;
	}

	public void setEsameMedicoCodice(String esameMedicoCodice) {
		this.esameMedicoCodice = esameMedicoCodice;
	}

	public String getEsameMedicoValue() {
		return esameMedicoValue;
	}

	public void setEsameMedicoValue(String esameMedicoValue) {
		this.esameMedicoValue = esameMedicoValue;
	}

	public boolean getRicoveroBool() {  
		return ricoveroBool;  
	}  

	public void setRicoveroBool(boolean ricoveroBool) {  
		this.ricoveroBool = ricoveroBool;  
	}  

	public String getPazienteId() {  
		return pazienteId;  
	}  

	public void setPazienteId(String pazienteId) {  
		this.pazienteId = pazienteId;  
	}



	public String getRefertoString() {  
		return refertoString;  
	}  

	public void setRefertoString(String refertoString) {  
		this.refertoString = refertoString;  
	}

}
