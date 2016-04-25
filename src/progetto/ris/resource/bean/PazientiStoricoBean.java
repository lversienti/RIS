package progetto.ris.resource.bean;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author loredana
 *è la classe bean che viene utilizzata per caricare i dati allo startup dell'applicazione, dataNascita e sesso tutte le analisi con il risultato normalità
 */
public class PazientiStoricoBean {
	
	private String pazienteId;  
	private String sesso;
	private String dataNascita;
	private ArrayList<Analisi> analisiList;
	
	public String getPazienteId() {  
		return pazienteId;  
	}  

	public void setPazienteId(String pazienteId) {  
		this.pazienteId = pazienteId;  
	}
	
	
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
	
	public void setAnalisiList(ArrayList<Analisi> analisiList){
		this.analisiList = analisiList;
	}
	
	@XmlElement  
	public ArrayList<Analisi> getAnalisiList(){
		return analisiList;
	}
	
	
}
