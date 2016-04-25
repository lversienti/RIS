package progetto.ris.resource.bean;


import javax.xml.bind.annotation.XmlElement;



public class Ricovero {
	
		
		private String dataRicovero;
		private Diagnosi diagnosi;
		private EsameMedico esameMedico;
		
		public String getDataRicovero() {  
			return dataRicovero;  
		}  

		public void setDataRicovero(String dataRicovero) {  
			this.dataRicovero = dataRicovero;  
		} 
		
		public void setDiagnosi(Diagnosi diagnosi){
			this.diagnosi = diagnosi;
		}
		
		@XmlElement 
		public Diagnosi getdiagnosi(){
			return diagnosi;
		}	
		
		public void setEsameMedico(EsameMedico esameMedico){
			this.esameMedico = esameMedico;
		}
		
		@XmlElement 
		public EsameMedico getEsameMedico(){
			return esameMedico;
		}
		
	}