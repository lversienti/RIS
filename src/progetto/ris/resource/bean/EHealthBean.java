package progetto.ris.resource.bean;

public class EHealthBean {
	 String pazienteId;
	 String esameMedico;
	 String referto;
	 String dataReferto;
	 String oraReferto;
	 String nomeMedico;
	 String sesso;
	 String dataNascita;
	 
	 
	 public String getSesso() {  
		 return sesso;  
	 }  

	 public void setSesso(String sesso) {  
		 this.sesso = sesso;  
	 }  
	 
	 public String getDataNascita() {  
		 return dataNascita;  
	 }  

	 public void setDataNascita(String dataNascita) {  
		 this.dataNascita = dataNascita;  
	 }  
	 
	 
	 public String getNomeMedico() {  
		 return nomeMedico;  
	 }  

	 public void setNomeMedico(String nomeMedico) {  
		 this.nomeMedico = nomeMedico;  
	 }  
	 
	
	 public String getDataReferto() {  
		 return dataReferto;  
	 }  
	 /*
	  * la data deve avere il formato 02/03/2010
	  * */
	 public void setDataReferto(String dataReferto) {  
		 this.dataReferto = dataReferto;  
	 }  
	 
	
	 public String getOraReferto() {  
		 return oraReferto;  
	 }  
	 
	 /*
	  * l'ora  deve avere il formato 13:23:07
	  * */
	 public void setOraReferto(String oraReferto) {  
		 this.oraReferto = oraReferto;  
	 }  
	 
	 public String getPazienteId() {  
		 return pazienteId;  
	 }  

	 public void setPazienteId(String pazienteId) {  
		 this.pazienteId = pazienteId;  
	 }
	 
	 public String getEsameMedico() {  
		 return esameMedico;  
	 }  

	 public void setEsameMedico(String esameMedico) {  
		 this.esameMedico = esameMedico;  
	 }

	 public String getReferto() {  
		 return referto;  
	 }  

	 public void setReferto(String referto) {  
		 this.referto = referto;  
	 }
}