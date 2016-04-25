package progetto.ris.resource.bean;


/*
 * è utilizzato sia dal client Annotazione che dal client arricchimento semantico
 * 
 * */
public class ToolBean {
	
	String refertoId;
	String pazienteId;
	
	public String getRefertoId() {
		return refertoId;
	}
	public void setRefertoId(String refertoId) {
		this.refertoId = refertoId;
	}
	public String getPazienteId() {
		return pazienteId;
	}
	public void setPazienteId(String pazienteId) {
		this.pazienteId = pazienteId;
	}
	
	

}