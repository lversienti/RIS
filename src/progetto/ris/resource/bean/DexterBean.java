package progetto.ris.resource.bean;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DexterBean {
	
	private String refertoURI;

	ArrayList<Icd9> icd9List;
	
	public String getRefertoURI() {  
		return refertoURI;  
	}  

	public void setRefertoURI(String refertoURI) {  
		this.refertoURI = refertoURI;  
	}

	public void setIcd9List(ArrayList<Icd9> icd9List){
		this.icd9List = icd9List;
	}
	
	@XmlElement  
	public ArrayList<Icd9> getIcd9List(){
		return icd9List;
	}

}
