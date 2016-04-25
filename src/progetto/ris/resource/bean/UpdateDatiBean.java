package progetto.ris.resource.bean;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;



public class UpdateDatiBean {

	private String refertoURI;
	private ArrayList<UpdateDatiExtractedBean> datiExtractedList;
	private ArrayList<UpdateDatiEnrichedBean> datiEnrichedList;
	private ArrayList<UpdateDatiScartatiBean> datiScartatiList;

	//http://progetto.ris/resource/p56347/refmed_28
	public String getRefertoUri() {  
		return refertoURI;  
	}  

	public void setRefertoUri(String refertoURI) {  
		this.refertoURI = refertoURI;  
	}
	
	public void setDatiScartatiList(ArrayList<UpdateDatiScartatiBean> datiScartatiList){
		this.datiScartatiList = datiScartatiList;
	}
	
	@XmlElement  
	public ArrayList<UpdateDatiScartatiBean> getDatiScartatiList(){
		return datiScartatiList;
	}
	
	
	public void setDatiExtractedList(ArrayList<UpdateDatiExtractedBean> datiExtractedList){
		this.datiExtractedList = datiExtractedList;
	}
	
	@XmlElement  
	public ArrayList<UpdateDatiExtractedBean> getDatiExtractedList(){
		return datiExtractedList;
	}
	
	public void setDatiEnrichedList(ArrayList<UpdateDatiEnrichedBean> datiEnrichedList){
		this.datiEnrichedList = datiEnrichedList;
	}
	
	@XmlElement  
	public ArrayList<UpdateDatiEnrichedBean> getDatiEnrichedList(){
		return datiEnrichedList;
	}
	
}
