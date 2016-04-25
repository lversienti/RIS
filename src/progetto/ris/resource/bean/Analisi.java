package progetto.ris.resource.bean;


public class Analisi {
	
	private String normalita;
	private String esame;
	private String dataPrelievo;
	private String valoreNumerico;
	
	public String getValoreNumerico() {
		return valoreNumerico;
	}

	public void setValoreNumerico(String valoreNumerico) {
		this.valoreNumerico = valoreNumerico;
	}

	public String getDataPrelievo() {
		return dataPrelievo;
	}

	public void setDataPrelievo(String dataPrelievo) {
		this.dataPrelievo = dataPrelievo;
	}

	public String getNormalita() {  
		return normalita;  
	}  

	public void setNormalita(String normalita) {  
		this.normalita = normalita;  
	}

	public String getEsame() {  
		return esame;  
	}  

	public void setEsame(String esame) {  
		this.esame = esame;  
	}


}
