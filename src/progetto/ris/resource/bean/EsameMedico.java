package progetto.ris.resource.bean;


public class EsameMedico {
	
	private RefertoInfo refertoBean; 
	private String codiceEsame; 
	private String valueEsame;
	
	
	public String getValueEsame() {
		return valueEsame;
	}

	public void setValueEsame(String valueEsame) {
		this.valueEsame = valueEsame;
	}

	public RefertoInfo getRefertoInfo() {  
		return refertoBean;  
	}  

	public void setReferto(RefertoInfo refertoBean) {  
		this.refertoBean = refertoBean;  
	}


	public String getCodiceEsame() {  
		return codiceEsame;  
	}  

	public void setCodiceEsame(String codiceEsame) {  
		this.codiceEsame = codiceEsame;  
	}
}
