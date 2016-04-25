package progetto.ris.resource.bean;


import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;  
import javax.xml.bind.annotation.XmlRootElement;  




@XmlRootElement(name = "paziente")
	public class Paziente {  
		private String pazienteId;  
		private String pazienteURI;  
		private String sesso;
		private String dataNascita;
		private String dataMining;
		private Ricovero ricovero;  
		private ArrayList<Ricovero> ricoveroList;
		private ArrayList<Analisi> analisiList;
		private ArrayList<DiagnosiArricchitaNotConfermata> arricchitaNotConfermataList;
		private ArrayList<DiagnosiArricchitaConfermata> arricchitaConfermataList;
		private ArrayList<DiagnosiDaScartare> diagnosiDaScartareList;
		
		public ArrayList<DiagnosiDaScartare> getDiagnosiDaScartareList() {
			return diagnosiDaScartareList;
		}

		public void setDiagnosiDaScartareList(
				ArrayList<DiagnosiDaScartare> diagnosiDaScartareList) {
			this.diagnosiDaScartareList = diagnosiDaScartareList;
		}

		public String getDataMining() {
			return dataMining;
		}

		public void setDataMining(String dataMining) {
			this.dataMining = dataMining;
		}

		public ArrayList<DiagnosiArricchitaConfermata> getArricchitaConfermataList() {
			return arricchitaConfermataList;
		}

		public void setArricchitaConfermataList(
				ArrayList<DiagnosiArricchitaConfermata> arricchitaConfermataList) {
			this.arricchitaConfermataList = arricchitaConfermataList;
		}

		private ArrayList<DiagnosiEstrattaNotConfermata> estrattaNotConfermataList;
		private ArrayList<DiagnosiEstrattaConfermata> estrattaConfermataList;
		
		
		
		public ArrayList<DiagnosiEstrattaConfermata> getDiagnosiEstrattaConfermataList() {
			return estrattaConfermataList;
		}

		public void setDiagnosiEstrattaConfermataList(
				ArrayList<DiagnosiEstrattaConfermata> estrattaConfermataList) {
			this.estrattaConfermataList = estrattaConfermataList;
		}

		public void setDiagnosiEstrattaNotConfermataList(ArrayList<DiagnosiEstrattaNotConfermata> estrattaNotConfermataList){
			this.estrattaNotConfermataList = estrattaNotConfermataList;
		}

		@XmlElement  
		public ArrayList<DiagnosiEstrattaNotConfermata> getDiagnosiEstrattaNotConfermataList(){
			return estrattaNotConfermataList;
		}
		
		public void setDiagnosiArrichitaNotConfermataList(ArrayList<DiagnosiArricchitaNotConfermata> arricchitaNotConfermataList){
			this.arricchitaNotConfermataList = arricchitaNotConfermataList;
		}

		@XmlElement  
		public ArrayList<DiagnosiArricchitaNotConfermata> getDiagnosiArrichitaNotConfermataList(){
			return arricchitaNotConfermataList;
		}

		public void setDiagnosiArrichitaConfermataList(ArrayList<DiagnosiArricchitaConfermata> arricchitaConfermataList){
			this.arricchitaConfermataList = arricchitaConfermataList;
		}

		@XmlElement  
		public ArrayList<DiagnosiArricchitaConfermata> getDiagnosiArrichitaConfermataList(){
			return arricchitaConfermataList;
		}
		
		public void setAnalisiList(ArrayList<Analisi> analisiList){
			this.analisiList = analisiList;
		}

		@XmlElement  
		public ArrayList<Analisi> getAnalisiList(){
			return analisiList;
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
		
		
		@XmlElement
		public Ricovero getRicovero() {  
			return ricovero;  
		}  

		public void setRicovero(Ricovero ricovero) {  
			this.ricovero = ricovero;  
		}

		
		@XmlElement  
		public String getPazienteURI() {  
			return pazienteURI;  
		}  

		public void setPazienteURI(String pazienteURI) {  
			this.pazienteURI = pazienteURI;  
		}

		@XmlElement  
		public String getPazienteId() {  
			return pazienteId;  
		}  

		public void setPazienteId(String pazienteId) {  
			this.pazienteId = pazienteId;  
		}  
		
		public void setRicoveroList(ArrayList<Ricovero> ricoveroList){
			this.ricoveroList = ricoveroList;
		}

		@XmlElement  
		public ArrayList<Ricovero> getRicoveroList(){
			return ricoveroList;
		}

	}  

