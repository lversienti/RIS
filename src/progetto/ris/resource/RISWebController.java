package progetto.ris.resource;



//import it.cnr.isti.hpc.ris.wiki.MedicalEntities;

import it.cnr.isti.hpc.ris.wiki.MedicalEntities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;


import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import progetto.ris.resource.bean.Analisi;
import progetto.ris.resource.bean.AnnotateBean;
import progetto.ris.resource.bean.DiagnosiDaScartare;
import progetto.ris.resource.bean.ToolBean;
import progetto.ris.resource.bean.Diagnosi;
import progetto.ris.resource.bean.DiagnosiArricchitaConfermata;
import progetto.ris.resource.bean.DiagnosiArricchitaNotConfermata;
import progetto.ris.resource.bean.DiagnosiEstrattaNotConfermata;
import progetto.ris.resource.bean.DiagnosiEstrattaConfermata;
import progetto.ris.resource.bean.EHealthBean;
import progetto.ris.resource.bean.EntitiesBean;
import progetto.ris.resource.bean.EsameMedico;
import progetto.ris.resource.bean.Paziente;
import progetto.ris.resource.bean.PazientiStoricoBean;
import progetto.ris.resource.bean.RefertoInfo;
import progetto.ris.resource.bean.RefertoInfoNewBean;
import progetto.ris.resource.bean.Ricovero;
import progetto.ris.resource.bean.UpdateDatiBean;
import progetto.ris.resource.bean.UpdateDatiEnrichedBean;
import progetto.ris.resource.bean.UpdateDatiExtractedBean;
import progetto.ris.resource.bean.UpdateDatiScartatiBean;
import progetto.ris.resource.util.DataUtil;
import progetto.ris.resource.util.Parameters;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.apache.log4j.Logger;

import javax.ws.rs.core.Response;

@Path("/webservice")  
public class RISWebController {

	static Logger log = Logger.getLogger(RISWebController.class.getName());

	Date dNow = new Date( );
	SimpleDateFormat ft =  new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
	//	   @GET  
	//	     @Path("/json/paziente/{pazienteId}")  
	//	     @Produces("application/json")  
	//	     public List<Paziente> getEmployeeJSONById(@PathParam("pazienteId")String pazienteId){  
	//		   return getPazienteInfoById(pazienteId);        
	//	     }  
	//	   


	@GET  
	@Path("/json/pazienti/{pazienteId}")  
	@Produces("application/json")  
	public List<Paziente> getPazientiJSONById(@PathParam("pazienteId")String pazienteId){  
		return getPazienteInfoById(pazienteId);        
	} 

	/*
	 * cerca tt le triple relative al paziente con id specificato  
	 *tiene conte del fatto che un paziente è un nuovo paziente, che il referto è domiciliare, che il referto è inserito senza patologie

	 */
	public List<Paziente> getPazienteInfoById(String pazienteId) {
		List<Paziente> pazienteList = new ArrayList<Paziente>();	

		try{
			ft =  new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
			boolean analisi = false;
			ArrayList<Analisi> analisiList = new ArrayList<Analisi>();
			VirtGraph set = Parameters.getInstance().getVirtGraph();
			Model model = Parameters.getInstance().getModelRDF();
			Property testoReferto_property = model.createProperty(DataUtil.uriRisBase+"testoReferto");
			String searchPazienteById = DataUtil.prefix + " SELECT DISTINCT ?dataNascita  ?URIcodicePaziente ?sesso ?URIreferto ?testo  ?ricovero (str(?codice) AS ?stdcodice) (str(?dataRicovero) AS ?stddataRicovero) ?dataMining (str(?nome) AS ?stdNome)  FROM "
					+ DataUtil.IRIRicovero
					+" WHERE { {"
					+ " ?URIcodicePaziente ris:codicePersona \""+pazienteId+"\"^^xsd:nonNegativeInteger. " 
					+ " ?URIcodicePaziente ris:dataNascita ?dataNascita. "
					+ " ?URIcodicePaziente ris:genderPersona ?sesso. "
					+ " ?URIcodicePaziente ris:DataMining  ?dataMining. "
					+ " ?ricovero ris:personaRicoverata ?URIcodicePaziente. "
					+ " ?esameMedico ris:dataInizioEsame ?dataRicovero.  "
					+ " ?esameMedico ris:esameDuranteRicovero ?ricovero. "
					+ " ?esameMedico ris:refertoRisultante ?URIreferto. "
					+ " ?esameMedico ris:codice ?codice. "
					+ " ?esameMedico ris:nome ?nome. "
					+ " ?URIreferto ris:testoReferto ?testo. "
					+ " } UNION {"
					+ " ?URIcodicePaziente ris:codicePersona \""+pazienteId+"\"^^xsd:nonNegativeInteger. " 
					+ " ?URIcodicePaziente ris:dataNascita ?dataNascita. "
					+ " ?URIcodicePaziente ris:genderPersona ?sesso. "
					+ " ?URIcodicePaziente ris:esameDomiciliare ?esameMedico. "
					+ " ?esameMedico ris:refertoRisultante ?URIreferto. "
					+ " ?esameMedico ris:codice ?codice. "
					+ " ?esameMedico ris:dataInizioEsame ?dataRicovero. "
					+ " ?URIreferto ris:testoReferto ?testo "
					+"} }";
			Date dNow = new Date( );
			log.info("data "+ft.format(dNow) +" >>> query searchPazienteById   "+searchPazienteById );
			VirtuosoQueryExecution virtQuerySearchById = VirtuosoQueryExecutionFactory.create (searchPazienteById, set);
			ResultSet pazienteResults = virtQuerySearchById.execSelect();
			while (pazienteResults.hasNext()) {
				Paziente paziente = new Paziente();
				Ricovero ricovero = new Ricovero();
				
				EsameMedico esameMedico = new EsameMedico();
				RefertoInfo refertoInfo = new RefertoInfo();
				ArrayList<DiagnosiArricchitaNotConfermata> arricchitaNotConfermataList = new ArrayList<DiagnosiArricchitaNotConfermata>();
				ArrayList<DiagnosiArricchitaConfermata> arricchitaConfermataList = new ArrayList<DiagnosiArricchitaConfermata>();
				ArrayList<DiagnosiEstrattaNotConfermata> estrattaNotConfermataList = new ArrayList<DiagnosiEstrattaNotConfermata>();
				ArrayList<DiagnosiEstrattaConfermata> estrattaConfermataList = new ArrayList<DiagnosiEstrattaConfermata>();
				ArrayList<DiagnosiDaScartare> daScartareList = new ArrayList<DiagnosiDaScartare>();
				QuerySolution resultPaziente = pazienteResults.nextSolution();
				String uri_paziente = resultPaziente.get("URIcodicePaziente").toString();
				String sesso = "";
				if(resultPaziente.get("sesso")!=null)
					sesso = resultPaziente.get("sesso").toString();
				String dataNascita = "";
				if(resultPaziente.get("dataNascita")!=null)
					dataNascita = resultPaziente.get("dataNascita").toString();
				String dataRicovero = "";
				if(resultPaziente.get("stddataRicovero")!=null)
					dataRicovero = resultPaziente.get("stddataRicovero").toString();
				String uri_referto = resultPaziente.get("URIreferto").toString();
				String testo = resultPaziente.get("testo").toString();
				String uri_ricovero ="";
				if(resultPaziente.get("ricovero")!=null)
					uri_ricovero = resultPaziente.get("ricovero").toString();
				String codiceEsame ="";
				if(resultPaziente.get("stdcodice")!=null)
					codiceEsame = resultPaziente.get("stdcodice").toString();
				String nomeEsame ="";
				if(resultPaziente.get("stdNome")!=null)
					nomeEsame = resultPaziente.get("stdNome").toString();
				String dataMining="";
				if(resultPaziente.get("dataMining")!=null)
					dataMining = resultPaziente.get("dataMining").toString();
				Diagnosi diagnosi = null;
				if(resultPaziente.get("ricovero")!=null){
					diagnosi = new Diagnosi();
					String searchDiagnosiByRicoveroURI = DataUtil.prefix + " SELECT DISTINCT ?diagnosiPrincTXT ?diagnosiPrincCod  ?ConcCod0 ?ConcTxT0 ?ConcCod1 ?ConcTxT1 ?ConcCod2  ?ConcTxT2 ?ConcCod3 ?ConcTxT3 ?ConcCod4 ?ConcTxT4 FROM "
							+ DataUtil.IRIRicovero
							+" WHERE {"
							+ " ?URIcodicePaziente ris:codicePersona \""+pazienteId+"\"^^xsd:nonNegativeInteger. " 
							+ "<"+uri_ricovero+">"+" ris:personaRicoverata ?URIcodicePaziente. "
							+ " ?diagnosiPrinc ris:diagnosiPrincipaleTXT ?diagnosiPrincTXT. "
							+ " ?diagnosiPrinc ris:diagnosiPrincipaleCodice ?diagnosiPrincCod. "
							+ "<"+uri_ricovero+">"+" ris:diagnosiPrincipale ?diagnosiPrinc. "
							+ " ?diagnosiConc ris:diagnosiConcomitanteTXT4 ?ConcTxT4. "
							+ " ?diagnosiConc ris:diagnosiConcomitanteCodice4 ?ConcCod4. "
							+ " ?diagnosiConc ris:diagnosiConcomitanteTXT3 ?ConcTxT3. "
							+ " ?diagnosiConc ris:diagnosiConcomitanteCodice3 ?ConcCod3. "
							+ " ?diagnosiConc ris:diagnosiConcomitanteTXT2 ?ConcTxT2. "
							+ " ?diagnosiConc ris:diagnosiConcomitanteCodice2 ?ConcCod2. "
							+ " ?diagnosiConc ris:diagnosiConcomitanteTXT1 ?ConcTxT1. "
							+ " ?diagnosiConc ris:diagnosiConcomitanteCodice1 ?ConcCod1. "
							+ " ?diagnosiConc ris:diagnosiConcomitanteTXT0 ?ConcTxT0. "
							+ " ?diagnosiConc ris:diagnosiConcomitanteCodice0 ?ConcCod0. "
							+ "<"+uri_ricovero+">"+" ris:diagnosiConcomitante ?diagnosiConc. "
							+"}";
					log.info("searchDiagnosiByRicoveroURI:: "+searchDiagnosiByRicoveroURI);
					VirtuosoQueryExecution virtQueryDiagnosi = VirtuosoQueryExecutionFactory.create (searchDiagnosiByRicoveroURI, set);
					ResultSet diagnosiResults = virtQueryDiagnosi.execSelect();
					while (diagnosiResults.hasNext()) {
						QuerySolution resultDiagnosi_query = diagnosiResults.nextSolution();
						String diagnosiPrincipaleTxT =resultDiagnosi_query.get("diagnosiPrincTXT").toString();
						diagnosi.setDiagnosiPrincipaleTxT(diagnosiPrincipaleTxT);
						String diagnosiPrincipaleConc =resultDiagnosi_query.get("diagnosiPrincCod").toString();
						diagnosi.setDiagnosiPrincipaleCodice(diagnosiPrincipaleConc);
						String patologiaTxT0 = resultDiagnosi_query.get("ConcTxT0").toString();
						diagnosi.setPatologiaTxT1(patologiaTxT0);
						String patologiaCod0 = resultDiagnosi_query.get("ConcCod0").toString();
						diagnosi.setPatologiaCodice1(patologiaCod0);
						String patologiaTxT1 = resultDiagnosi_query.get("ConcTxT1").toString();
						diagnosi.setPatologiaTxT2(patologiaTxT1);
						String patologiaCod1 = resultDiagnosi_query.get("ConcCod1").toString();
						diagnosi.setPatologiaCodice2(patologiaCod1);
						String patologiaTxT2 = resultDiagnosi_query.get("ConcTxT2").toString();
						diagnosi.setPatologiaTxT3(patologiaTxT2);
						String patologiaCod2 = resultDiagnosi_query.get("ConcCod2").toString();
						diagnosi.setPatologiaCodice3(patologiaCod2);
						String patologiaTxT3 = resultDiagnosi_query.get("ConcTxT3").toString();
						diagnosi.setPatologiaTxT4(patologiaTxT3);
						String patologiaCod3 = resultDiagnosi_query.get("ConcCod3").toString();
						diagnosi.setPatologiaCodice4(patologiaCod3);
						String patologiaTxT4 = resultDiagnosi_query.get("ConcTxT4").toString();;
						diagnosi.setPatologiaTxT5(patologiaTxT4);
						String patologiaCod4 = resultDiagnosi_query.get("ConcCod4").toString();
						diagnosi.setPatologiaCodice5(patologiaCod4);
					}
				}
				paziente.setPazienteId(pazienteId);
				paziente.setSesso(sesso);
				paziente.setDataNascita(dataNascita);
				paziente.setPazienteURI(uri_paziente);
				paziente.setDataMining(dataMining);
				ricovero.setDataRicovero(dataRicovero);
				refertoInfo.setReferto(testo);
				refertoInfo.setRefertoURI(uri_referto);
				ricovero.setDiagnosi(diagnosi);
				esameMedico.setCodiceEsame(codiceEsame);
				esameMedico.setValueEsame(nomeEsame);
				esameMedico.setReferto(refertoInfo);
				ricovero.setEsameMedico(esameMedico);
				paziente.setRicovero(ricovero);


				if(!analisi){
					String analistiString_query =  DataUtil.prefix + " SELECT DISTINCT (str(?tipoesame) AS ?stdTipoesame) (str(?normalita) AS ?stdNormalita)  (str(?dataPrelievo) AS ?stdDataPrelievo) (str(?valoreNumerico) AS ?stdValoreNumerico) FROM "
							+ DataUtil.IRIPrelievo
							+" WHERE {"
							+ " ?URIcodicePaziente ris:codicePersona \""+pazienteId+"\"^^xsd:nonNegativeInteger. " 
							+ " ?prelievo ris:prelievoEffettuatoA ?URIcodicePaziente. "
							+ " ?prelievo ris:dataInizioPrelievo ?dataPrelievo. "
							+ " ?tipoEsame ris:tipoEsame ?tipoesame. "
							+ " ?tipoEsame ris:normalita ?normalita. "
							+ " ?tipoEsame ris:risutatoNumericoAnalita ?bn. "
							+ " ?bn ris:modulo ?valoreNumerico. "
							+ " ?tipoEsame ris:inPrelievo ?prelievo. "
							+" } ";
					log.info("analistiString_query:: "+analistiString_query);
					VirtuosoQueryExecution virtQueryAnalisi = VirtuosoQueryExecutionFactory.create (analistiString_query, set);
					ResultSet prelievoResults = virtQueryAnalisi.execSelect();
					while (prelievoResults.hasNext()) {
						QuerySolution resultPrelievo = prelievoResults.nextSolution();
						Analisi prelievo_class = new Analisi();
						String esame =resultPrelievo.get("stdTipoesame").toString();
						String normalita =resultPrelievo.get("stdNormalita").toString();
						String dataPrelievo =resultPrelievo.get("stdDataPrelievo").toString();
						String valoreNumerico =resultPrelievo.get("stdValoreNumerico").toString();
						prelievo_class.setEsame(esame);
						prelievo_class.setNormalita(normalita);
						prelievo_class.setDataPrelievo(dataPrelievo);
						prelievo_class.setValoreNumerico(valoreNumerico);
						analisiList.add(prelievo_class);
					}
					paziente.setAnalisiList(analisiList);
					analisi = true;
				}




			//parte per le diagnosi arricchite estratte
			Resource referto_resource = model.createResource(uri_referto);
					StmtIterator prop_ogg_refUri = model.listStatements(referto_resource,null,(RDFNode) null);
				/*	while(prop_ogg_refUri.hasNext()) {
					  Statement stmt = prop_ogg_refUri.nextStatement();
					 System.out.println(">>>>>>>>>>>>>>>>> "+stmt);
				}
			*/
				List<Statement> finalSet = new ArrayList<Statement>();
				List<Statement> currentSet =  prop_ogg_refUri.toList();
				finalSet.addAll(currentSet);
				int numStatement =((finalSet.size()-3) / 2);
				DiagnosiArricchitaConfermata  dc_arrichita = null;
				DiagnosiArricchitaNotConfermata  dnc_arrichita = null;
				DiagnosiEstrattaNotConfermata dnc_Estratta = null;
				DiagnosiEstrattaConfermata  dc_Estratta = null;
				DiagnosiDaScartare  diagnosiDaScartare = null;
				for( int j =1; j<=numStatement; j++){
					////////////////////////////////////////////
					///sez diagnosi arricchita nn confermata///
					//////////////////////////////////////////
					Property property_cod =  model.createProperty("http://progetto.ris/resource/dnc_arricchitaCod_"+j);
					StmtIterator iterCodice  = model.listStatements(referto_resource,property_cod,(RDFNode) null);
					if (iterCodice.hasNext()) {
						Statement stmt = (Statement) iterCodice.next();
						//System.out.println("-----------predicate "+stmt.getPredicate().toString());
						Statement stmtToCheckCod = model.createStatement(referto_resource,property_cod,stmt.getLiteral().getLexicalForm());
						if(model.contains(stmtToCheckCod)){
							 dnc_arrichita = new DiagnosiArricchitaNotConfermata();
						     dnc_arrichita.setDnc_arricchitaCodUri(property_cod.toString());
						     dnc_arrichita.setDnc_arricchitaCod(stmt.getLiteral().getLexicalForm());
					      }}

					Property property_value =  model.createProperty("http://progetto.ris/resource/dnc_arricchitaTxT_"+j);
					StmtIterator iterValue  = model.listStatements(referto_resource,property_value,(RDFNode) null);
					if (iterValue.hasNext()) {
						Statement stmtValue = (Statement) iterValue.next();
						Statement stmtToCheckCod = model.createStatement(referto_resource,property_value,stmtValue.getLiteral().getLexicalForm());
						if(model.contains(stmtToCheckCod)){
						dnc_arrichita.setDnc_arricchitaTxTUri(property_value.toString());
						dnc_arrichita.setDnc_arricchitaTxT(stmtValue.getLiteral().getLexicalForm());
					}
						arricchitaNotConfermataList.add(dnc_arrichita);
						}
					
					
					
					
					///////////////////////////////////////////
					/// sez diagnosi arricchita confermata ///
					/////////////////////////////////////////
					//codice
					Property property_confCod =  model.createProperty("http://progetto.ris/resource/dc_arricchitaCod_"+j);
					StmtIterator iterCodiceConf  = model.listStatements(referto_resource,property_confCod,(RDFNode) null);
					if (iterCodiceConf.hasNext()) {
						Statement stmt = (Statement) iterCodiceConf.next();
						Statement stmtToCheckCod = model.createStatement(referto_resource,property_confCod,stmt.getLiteral().getLexicalForm());
						if (model.contains(stmtToCheckCod)) {
						   dc_arrichita = new DiagnosiArricchitaConfermata();
						 System.out.println("-----------valore foglia codice "+stmt.getLiteral().getLexicalForm());
						   dc_arrichita.setDc_arricchitaCodUri(property_confCod.toString());
						   dc_arrichita.setDc_arricchitaCod(stmt.getLiteral().getLexicalForm());
					}}
					//value
					Property property_Confvalue =  model.createProperty("http://progetto.ris/resource/dc_arricchitaTxT_"+j);
					StmtIterator iterValueConf  = model.listStatements(referto_resource,property_Confvalue,(RDFNode) null);
					if (iterValueConf.hasNext()) {
						System.out.println("property_Confvalue:: "+property_Confvalue.toString());
						Statement stmtArrichitaConfermata = (Statement) iterValueConf.next();
						Statement stmtToCheckValue = model.createStatement(referto_resource,property_Confvalue,stmtArrichitaConfermata.getLiteral().getLexicalForm());
						System.out.println("valore foglia value "+stmtArrichitaConfermata.getLiteral().getLexicalForm());
						if (model.contains(stmtToCheckValue)) {
						   dc_arrichita.setDc_arricchitaTxTUri(property_Confvalue.toString());
						   dc_arrichita.setDc_arricchitaTxT(stmtArrichitaConfermata.getLiteral().getLexicalForm());
						}
						arricchitaConfermataList.add(dc_arrichita);
					}
					

					/////////////////////////////////////////////
					/// sez diagnosi  estratta non confermata///
					///////////////////////////////////////////
					Property propertyEstratta_cod =  model.createProperty("http://progetto.ris/resource/dnc_estrattaCod_"+j);
					StmtIterator iterCodiceEstratta  = model.listStatements(referto_resource,propertyEstratta_cod,(RDFNode) null);
					//codice
					if (iterCodiceEstratta.hasNext()) {
						Statement stmt = (Statement) iterCodiceEstratta.next();
						System.out.println("-----------predicate estratta "+stmt.getPredicate().toString());
						Statement stmtToCheckCod = model.createStatement(referto_resource,propertyEstratta_cod,stmt.getLiteral().getLexicalForm());
						if(model.contains(stmtToCheckCod)){
							 dnc_Estratta = new DiagnosiEstrattaNotConfermata();
							 dnc_Estratta.setDnc_estrattaCodUri(propertyEstratta_cod.toString());
							 dnc_Estratta.setDnc_estrattaCod(stmt.getLiteral().getLexicalForm());
					      }}
					//value
					Property propertyEstratta_value =  model.createProperty("http://progetto.ris/resource/dnc_estrattaTxT_"+j);
					StmtIterator iterValueEstratta  = model.listStatements(referto_resource,propertyEstratta_value,(RDFNode) null);
					if (iterValueEstratta.hasNext()) {
						Statement stmtValue = (Statement) iterValueEstratta.next();
						Statement stmtToCheckCod = model.createStatement(referto_resource,propertyEstratta_value,stmtValue.getLiteral().getLexicalForm());
						if(model.contains(stmtToCheckCod)){
							dnc_Estratta.setDnc_estrattaTxTUri(propertyEstratta_value.toString());
							dnc_Estratta.setDnc_estrattaTxT(stmtValue.getLiteral().getLexicalForm());
					}
						estrattaNotConfermataList.add(dnc_Estratta);
						}
					

					//////////////////////////////////////////
					/// sez diagnosi  estratta  confermata///
					////////////////////////////////////////
					Property propertyEstrattaConf_cod =  model.createProperty("http://progetto.ris/resource/dc_estrattaCod_"+j);
					StmtIterator iterCodiceEstrattaConf  = model.listStatements(referto_resource,propertyEstrattaConf_cod,(RDFNode) null);
					//codice
					if (iterCodiceEstrattaConf.hasNext()) {
						Statement stmt = (Statement) iterCodiceEstrattaConf.next();
						System.out.println("-----------predicate estratta conf "+stmt.getPredicate().toString());
						Statement stmtToCheckCod = model.createStatement(referto_resource,propertyEstrattaConf_cod,stmt.getLiteral().getLexicalForm());
						if(model.contains(stmtToCheckCod)){
							dc_Estratta = new DiagnosiEstrattaConfermata();
							dc_Estratta.setEstrattaConfCodUri(propertyEstrattaConf_cod.toString());
							dc_Estratta.setEstrattaConfCod(stmt.getLiteral().getLexicalForm());
						}}
					//value
					Property propertyEstrattaConf_value =  model.createProperty("http://progetto.ris/resource/dc_estrattaTxT_"+j);
					StmtIterator iterValueEstrattaConf  = model.listStatements(referto_resource,propertyEstrattaConf_value,(RDFNode) null);
					if (iterValueEstrattaConf.hasNext()) {
						Statement stmtValue = (Statement) iterValueEstrattaConf.next();
						Statement stmtToCheckCod = model.createStatement(referto_resource,propertyEstrattaConf_value,stmtValue.getLiteral().getLexicalForm());
						if(model.contains(stmtToCheckCod)){
							dc_Estratta.setEstrattaConfTxTUri(propertyEstrattaConf_value.toString());
							dc_Estratta.setEstrattaConfTxT(stmtValue.getLiteral().getLexicalForm());
						}
						estrattaConfermataList.add(dc_Estratta);
					}
					
					/////////////////////////////////
					/// sez diagnosi  scartata   ///
					///////////////////////////////
					Property propertyDaScartare_cod =  model.createProperty("http://progetto.ris/resource/dn_scartareCod_"+j);
					StmtIterator iterCodiceDaScartare  = model.listStatements(referto_resource,propertyDaScartare_cod,(RDFNode) null);
					//codice
					if (iterCodiceDaScartare.hasNext()) {
						Statement stmt = (Statement) iterCodiceDaScartare.next();
						System.out.println("-----------predicate Da Scartare"+stmt.getPredicate().toString());
						Statement stmtToCheckCod = model.createStatement(referto_resource,propertyDaScartare_cod,stmt.getLiteral().getLexicalForm());
						if(model.contains(stmtToCheckCod)){
							diagnosiDaScartare = new DiagnosiDaScartare();
							diagnosiDaScartare.setDaScartereCodUri(propertyDaScartare_cod.toString());
							diagnosiDaScartare.setDaScartareCod(stmt.getLiteral().getLexicalForm());
						}}
					//value
					Property propertyDaScartare_value =  model.createProperty("http://progetto.ris/resource/dn_scartareTxT_"+j);
					StmtIterator iterValueDaScartare  = model.listStatements(referto_resource,propertyDaScartare_value,(RDFNode) null);
					if (iterValueDaScartare.hasNext()) {
						Statement stmtValue = (Statement) iterValueDaScartare.next();
						Statement stmtToCheckCod = model.createStatement(referto_resource,propertyDaScartare_value,stmtValue.getLiteral().getLexicalForm());
						if(model.contains(stmtToCheckCod)){
							diagnosiDaScartare.setDaScartareTxTUri(propertyEstrattaConf_value.toString());
							diagnosiDaScartare.setDaScartareTxT(stmtValue.getLiteral().getLexicalForm());
						}
						daScartareList.add(diagnosiDaScartare);
					}
					
				}//chiude for
								
				paziente.setDiagnosiArrichitaNotConfermataList(arricchitaNotConfermataList);
				paziente.setDiagnosiEstrattaNotConfermataList(estrattaNotConfermataList);
				paziente.setDiagnosiArrichitaConfermataList(arricchitaConfermataList);	
				paziente.setDiagnosiEstrattaConfermataList(estrattaConfermataList);	
				paziente.setDiagnosiDaScartareList(daScartareList);
				pazienteList.add(paziente);

			}
			log.info("Query excuted");
		}
		catch(Exception e){
			System.out.println("Error into method  getPazienteInfoById:: "+e);
			e.printStackTrace();
		}
		return pazienteList;
	}

	public List<String> searchUriRefertoByidPaziente(String pazienteId) {
		List<String> uriRefList = new ArrayList<String>();	
		try{
			VirtGraph set = Parameters.getInstance().getVirtGraph();
			String searchAllUriReferto = DataUtil.prefix + " SELECT DISTINCT ?URIreferto FROM "
					+ DataUtil.IRIRicovero
					+" WHERE {"
					+ " ?URIcodicePaziente ris:codicePersona \""+pazienteId+"\"^^xsd:nonNegativeInteger. " 
					+ " ?ricovero ris:personaRicoverata ?URIcodicePaziente. "
					+ " ?esameMedico ris:esameDuranteRicovero ?ricovero. "
					+ " ?esameMedico ris:refertoRisultante ?URIreferto. "
					+ " ?URIreferto ris:testoReferto ?testo. "
					+ " }";
			VirtuosoQueryExecution virtQuerySearchById = VirtuosoQueryExecutionFactory.create (searchAllUriReferto, set);
			ResultSet uriRefertoResults = virtQuerySearchById.execSelect();
			while (uriRefertoResults.hasNext()) {
				QuerySolution resultReferto = uriRefertoResults.nextSolution();
				String uri_referto = resultReferto.get("URIreferto").toString();
				uriRefList.add(uri_referto);
				//System.out.println("uri_referto:: "+uri_referto);
			}
		}
		catch(Exception e){
			System.out.println("Error into method  searchUriREfertoByidPaziente:: "+e);
			e.getStackTrace();
		}
		return uriRefList;

	}


 

	/**
	 * Il metodo viene chiamato per aggiungere allo storico dei referti le informazioni arricchite con il tool di arricchimento semantico
	 * di ISTI-CNR e annotazione di UNIPI
	 * @return
	 */
	@GET 
	@Path("/dexter")
	@Produces({"application/json","application/xml"})
	public String  insertDiagnosi(){
		try{
			ft =  new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
			List<RefertoInfo> refertoList = getURIReferto();
			Model model = Parameters.getInstance().getModelRDF();
			String Oldline;
			//File dexter = new File("C:\\DexterResults\\DexterResults.txt");
			File annotazioni = new File("C:\\DexterResults\\AnnotazioniResults.txt");
		//	FileOutputStream fos = new FileOutputStream(dexter);
			FileOutputStream fos1 = new FileOutputStream(annotazioni);
		//	BufferedWriter bwDexter = new BufferedWriter(new OutputStreamWriter(fos));
			BufferedWriter bwAnnotazioni = new BufferedWriter(new OutputStreamWriter(fos1));
			for(int k=0; k<refertoList.size(); k++){
				String referto = refertoList.get(k).getReferto();
				// System.out.println("referto: "+ referto); 
				String refertoURI = refertoList.get(k).getRefertoURI();
				// System.out.println("refertoURI: "+ refertoURI); 
				// String referto = "Controllo. Recente ricovero presso questo istituto (Ottobre 2009), dimesso con diagnosi di Dispnea (classe NYHA IIb), astenia e cardiopalmo in paziente con miocardiopatia dilatativa post-ischemica, disfunzione ventricolare sinistra sisto-diastolica di grado severo, portatore di dispositivo defibrillatore biventricolare   Malattia aterosclerotica coronarica multivasale gi<E0> sottoposta a rivascolarizzazione chirurgica e ad anuloplastica mitralica con insufficienza mitralica residua di grado moderato. Fibrillazione atriale permanente. Insufficienza renale cronica. Pregressa ablazione tiroidea con radioiodio. Respiro di Cheyne-Stokes. Terapia in corso:  Losartan 12,5 mg 1 cpr/die (ore 20) Congescor 5 mg 1 cpr ore 8 + 1,25 mg 1 cpr ore 8, Kanrenol 50 mg 1 cpr/die Coumadin secondo INR Eutirox 100 mcg 1 cpr/die Zyloric 150 mg 1 cpr/die (ore 22) Lasix 4 cpr/die (ore 8) Carvasin 5 mg s.l. in caso di dolore toracico.  Pregresso episodio prelipotimico con documentatat scarica del defibrillatore.";
				// String referto= "Controllo. Recente ricovero presso questo istituto (Ottobre 2009), dimesso con diagnosi di \"Dispnea (classe NYHA IIb), astenia e cardiopalmo in paziente con miocardiopatia dilatativa post-ischemica, disfunzione ventricolare sinistra sisto-diastolica di grado severo, portatore di dispositivo defibrillatore biventricolare. Malattia aterosclerotica coronarica multivasale già sottoposta a rivascolarizzazione chirurgica e ad anuloplastica mitralica con insufficienza mitralica residua di grado moderato. Fibrillazione atriale permanente. Insufficienza renale cronica. Pregressa ablazione tiroidea con radioiodio. Respiro di Cheyne-Stokes\". Terapia in corso:  Losartan 12,5 mg 1 cpr/die (ore 20) Congescor 5 mg 1 cpr ore 8 + 1,25 mg 1 cpr ore 8, Kanrenol 50 mg 1 cpr/die Coumadin secondo INR Eutirox 100 mcg 1 cpr/die Zyloric 150 mg 1 cpr/die (ore 22) Lasix 4 cpr/die (ore 8) Carvasin 5 mg s.l. in caso di dolore toracico.  Pregresso episodio prelipotimico con documentatat scarica del defibrillatore.";
			/*		HashMap<String, String> mapDexter = invoceDexter(referto);
				System.out.println("mapDexter size: per l'uri "+refertoURI + " è " +mapDexter.size()); 
				
				////////////////////
				///inizio dexter///
				//////////////////
			if(mapDexter.size()>0){
					////parete per vedere il numero di patologie
					Oldline = refertoURI + " numero di ICD9: "+mapDexter.size();
					bwDexter.write(Oldline);
					bwDexter.newLine();
					///fine
					int i = 0;
					Iterator<String> keySetIterator  = mapDexter.keySet().iterator();
					while(keySetIterator.hasNext()) {
						i = i+1;
						//la key è il valore "testo"
						String codIcd9 = (String) keySetIterator.next();
						//il value è il codice ICD9
						String valueIcd9 = mapDexter.get(codIcd9);


						Resource refertoURI_resource = model.createResource(refertoURI);
						Property diagnosiNnconfArricchitaCod = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiNonConfermataArricchitaCod+i);
						Statement statementDiagnosiNnCofArrichitaCod = model.createStatement(refertoURI_resource,diagnosiNnconfArricchitaCod,valueIcd9);
						model.add(statementDiagnosiNnCofArrichitaCod);


						Property diagnosiNnconfArricchitaTXT = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiNonConfermataArricchitaTxT+i);
						Statement statementDiagnosiNnCofArrichitaTXT = model.createStatement(refertoURI_resource,diagnosiNnconfArricchitaTXT,codIcd9);
						model.add(statementDiagnosiNnCofArrichitaTXT);


						Resource diagnosiNnConf_property = model.createProperty("http://progetto.ris/resource/diagnosiNonConfermata");
						Statement statementTypeArriuchitaCod = model.createStatement(diagnosiNnconfArricchitaCod,RDFS.subPropertyOf ,diagnosiNnConf_property);
						model.add(statementTypeArriuchitaCod);
						//Statement statementTypeArriuchitaValue = model.createStatement(diagnosiNnconfArricchitaTXT,model.getProperty(RDFS.subPropertyOf),diagnosiNnConf_property);
						Statement statementTypeArriuchitaValue = model.createStatement(diagnosiNnconfArricchitaTXT,RDFS.subPropertyOf,diagnosiNnConf_property);
						model.add(statementTypeArriuchitaValue);
						Date dNow = new Date( );
						log.info("data "+ft.format(dNow) +" >>> query insertNewDiagnosiDexter   " );
					}

				}*/
				/////////////////////////
				///inizio annotazione///
				///////////////////////
				AnnotateBean annotateBean = new AnnotateBean();
				annotateBean.setText(referto);;


				Gson gson = new Gson();
				String annotateGson = gson.toJson(annotateBean);
				//System.out.println("annotateGson:::"+annotateGson);
				Client client = Client.create();
				WebResource webResource = client.resource("http://tanl.di.unipi.it/ris-ws/annotate");
				ClientResponse response = webResource.type("application/json").accept("application/json").post(ClientResponse.class, annotateGson);
				String output = response.getEntity(String.class);
				//System.out.println("stringa da deserializzare:: "+output);
				
				EntitiesBean[] res = gson.fromJson(output, EntitiesBean[].class);
				if(res.length==0)
					System.out.println("la lista delle annotazioni e' "+res.length);
				else{
					Oldline = refertoURI + " numero di ICD9: "+res.length;
					bwAnnotazioni.write(Oldline);
					bwAnnotazioni.newLine();
				
				for(int i=0; i<res.length; i++){
					//cod 2568
					String codIcd9 = res[i].getCodICD9();
					//il value es diabete mellito
					String valueIcd9 = res[i].getValueICD9();
					//System.out.println("Codice::: "+ res[i].getCodICD9());
					//System.out.println("Value::: "+ res[i].getValueICD9());
					i=i+1;
					Resource refertoURI_resource = model.createResource(refertoURI);
					Property diagnosiNnconfEstrattaCod = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiNonConfermataEstrattaCod+i);
					Statement statementDiagnosiNnCofEstrattaCod = model.createStatement(refertoURI_resource,diagnosiNnconfEstrattaCod,codIcd9);
					model.add(statementDiagnosiNnCofEstrattaCod);


					Property diagnosiNnconfEstrattaTXT = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiNonConfermataEstrattaTxt+i);
					Statement statementDiagnosiNnCofEstrattaTXT = model.createStatement(refertoURI_resource,diagnosiNnconfEstrattaTXT,valueIcd9);
					model.add(statementDiagnosiNnCofEstrattaTXT);


					Resource diagnosiNnConf_property = model.createProperty("http://progetto.ris/resource/diagnosiNonConfermata");
					Statement statementTypeArriuchitaCod = model.createStatement(diagnosiNnconfEstrattaCod,RDFS.subPropertyOf ,diagnosiNnConf_property);
					model.add(statementTypeArriuchitaCod);
					Statement statementTypeArriuchitaValue = model.createStatement(diagnosiNnconfEstrattaTXT,RDFS.subPropertyOf,diagnosiNnConf_property);
					model.add(statementTypeArriuchitaValue);
					i=i-1;
					Date dNow = new Date( );
					log.info("data "+ft.format(dNow) +" >>> query insertNewDiagnosiAnnotazione UNIPI  " );
					
				}}
				
				/////////////////////////
				///fine annotazione/////
				///////////////////////
			}
			log.info("finito l'applicazione dei tool di annotazione arricchimento");
			System.out.println("finito l'applicazione dei tool di annotazione arricchimento");
			//bwDexter.close();
			bwAnnotazioni.close();
		} 
		catch (Exception e){
			System.out.println("Error into method server insertDiagnosi: "+e);
			e.printStackTrace();
		}
		return "finito di inserire arricchimento e annotazione";
	}

	//invoca il web service sviluppato da UNIPI per l'annotazione del testo 
	public String annotate(String referto, String refertoURI){
		String message= "";
		try{
			AnnotateBean annotateBean = new AnnotateBean();
			annotateBean.setText(referto);;
			Gson gson = new Gson();
			String annotateGson = gson.toJson(annotateBean);
			System.out.println("annotateGson:::"+annotateGson);
			Client client = Client.create();
			WebResource webResource = client.resource("http://tanl.di.unipi.it/ris-ws/annotate");
			ClientResponse response = webResource.type("application/json").accept("application/json").post(ClientResponse.class, annotateGson);
			String output = response.getEntity(String.class);
			System.out.println("stringa da deserializzare:: "+output);

			Model model = Parameters.getInstance().getModelRDF();
			EntitiesBean[] res = gson.fromJson(output, EntitiesBean[].class);	
			System.out.println("res lunghezza::: "+res.length);
			if(res.length>0){
				for(int i=0; i<res.length; i++){
					//cod 2568
					String codIcd9 = res[i].getCodICD9();
					//il value es diabete mellito
					String valueIcd9 = res[i].getValueICD9();
					System.out.println("Codice::: "+ res[i].getCodICD9());
					System.out.println("Value::: "+ res[i].getValueICD9());
					i=i+1;
					Resource refertoURI_resource = model.createResource(refertoURI);
					Property diagnosiNnconfEstrattaCod = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiNonConfermataEstrattaCod+i);
					Statement statementDiagnosiNnCofEstrattaCod = model.createStatement(refertoURI_resource,diagnosiNnconfEstrattaCod,codIcd9);
					model.add(statementDiagnosiNnCofEstrattaCod);


					Property diagnosiNnconfEstrattaTXT = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiNonConfermataEstrattaTxt+i);
					Statement statementDiagnosiNnCofEstrattaTXT = model.createStatement(refertoURI_resource,diagnosiNnconfEstrattaTXT,valueIcd9);
					model.add(statementDiagnosiNnCofEstrattaTXT);


					Resource diagnosiNnConf_property = model.createProperty("http://progetto.ris/resource/diagnosiNonConfermata");
					Statement statementTypeEstrattaCod = model.createStatement(diagnosiNnconfEstrattaCod,RDFS.subPropertyOf ,diagnosiNnConf_property);
					model.add(statementTypeEstrattaCod);
					Statement statementTypeEstrattaValue = model.createStatement(diagnosiNnconfEstrattaTXT,RDFS.subPropertyOf,diagnosiNnConf_property);
					model.add(statementTypeEstrattaValue);
					i=i-1;
					Date dNow = new Date( );
					log.info("data "+ft.format(dNow) +" >>> query annotate referto UNIPI  " + refertoURI);
					message = " successful Analisi Testuale";
				}
			}
			else
				message = " non ci sono risultato prodotti dall'Analisi testuale";
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("errore into method  server annotate tool unipi "+e); 
			return "failure";
		}
		return message;
	}


	public HashMap<String, String> invoceDexter(String refertoTesto){
		HashMap<String, String> mapDexter = null;
		try{
			mapDexter = MedicalEntities.annotate(refertoTesto);
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("errore into method  server invoceDexter "+e); 
		}
		return mapDexter;
	}

	public List<RefertoInfo> getURIReferto() {
		List<RefertoInfo> refertoList = new ArrayList<RefertoInfo>();		
		try{

			ft =  new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
			VirtGraph set = Parameters.getInstance().getVirtGraph();

			String searchURIReferto_Referto = DataUtil.prefix + " SELECT DISTINCT ?URIreferto ?testo FROM  "
					+ DataUtil.IRIRicovero
					+" WHERE {"
					+" ?URIreferto ris:testoReferto ?testo."
					+ " }";

			log.info("data "+ft.format(dNow) +" >>> query searchURIReferto_Referto   "+searchURIReferto_Referto );
			VirtuosoQueryExecution excute_queryURIRef_Ref = VirtuosoQueryExecutionFactory.create (searchURIReferto_Referto, set);
			ResultSet refertoResults = excute_queryURIRef_Ref.execSelect();
			while (refertoResults.hasNext()) {
				QuerySolution resultPaziente = refertoResults.nextSolution();
				String uri_referto = resultPaziente.get("URIreferto").toString();
				String referto = resultPaziente.get("testo").toString();
				RefertoInfo refInfo = new RefertoInfo();
				refInfo.setRefertoURI(uri_referto);
				refInfo.setReferto(referto);
				refertoList.add(refInfo);
			}
			log.info("Query excuted");
		}
		catch(Exception e){
			System.out.println("Error into method  getURIRefertoReferto:: "+e);
			e.getStackTrace();
		}
		return refertoList;
	}

	/*
	 * Questo metodo inserisce un nuovo referto dentro il db, distinguendo che sia un referto che viene da persona
	 * ricoverata, oppure da una visita domiciliare
	 */
	@POST 
	@Path("/insertNewReferto")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({"application/json","application/xml"})
	public String insertNewReferto(@FormParam("newRefetoInfo") String newRefetoInfo){
		String messaggio;
		String pazienteId = null;
		if(newRefetoInfo==null)
		   log.info("il parametro non è stato settato ");
		System.out.println("newRefetoInfo::: "+newRefetoInfo);
		try{
			Model model = Parameters.getInstance().getModelRDF();
			Gson gson = new Gson();
			RefertoInfoNewBean refertoInfo = gson.fromJson(newRefetoInfo, RefertoInfoNewBean.class);
			boolean ricoveroBool = refertoInfo.getRicoveroBool();
			pazienteId = refertoInfo.getPazienteId();
			String esameMedicoCodice = refertoInfo.getEsameMedicoCodice();
			String esameMedicoValue = refertoInfo.getEsameMedicoValue();
			String refertoString = refertoInfo.getRefertoString();
			//prendo il max id da ricovero
			System.out.println("pazienteId "+pazienteId);
			System.out.println("ricoveroBool "+ricoveroBool);
			if(isString(esameMedicoCodice)!=-1)
				esameMedicoCodice = "V_"+esameMedicoCodice;
			
			//la prima cosa che faccio è controllare se esiste il paziente di id passato
			String pazienteNum = checkExistedPaziente(pazienteId);
			  if(pazienteNum.equals("0")){
				  messaggio = "non esiste il paziente di id "+pazienteId + " impossibile inserire il referto";
				  return messaggio;
			  }
		    
			//se è true si riferisce a un referto che riguarda il ricovero
			if(ricoveroBool==true){
				
				System.out.println(">>>>>>>>>>>>>>>>>>REFERTO DA RICOVERO ");
				String maxIdURI = searchMaxIdRefmed(pazienteId);
				if(!maxIdURI.equals("0")){
				System.out.println("REFERTO DA RICOVERO  maxIdURI "+maxIdURI);
				Resource ricoveroNew = model.createResource(DataUtil.uriRicovero+"p"+pazienteId+"/ric_"+maxIdURI);
				Property PersonaRicoverata_property = model.createProperty(DataUtil.uriPersonaRicoverata);
				Resource pazienteURI = model.getResource(DataUtil.uriPaziente+pazienteId);
				System.out.println("pazienteURI dentro new referto ricovero "+pazienteURI);
				Statement statmentRicovero = model.createStatement(ricoveroNew, PersonaRicoverata_property, pazienteURI);
				model.add(statmentRicovero);

				//prendo il super
				Property dataInizioEvento_property = model.getProperty(DataUtil.dataInizioEvento);

				//dataora inserimento referto
				Date today = new Date();
				SimpleDateFormat  DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
				String date = DATE_FORMAT.format(today);
				String data = date.substring(0, date.indexOf(":"));
				String ora = date.substring(date.indexOf(":")+1, date.length());


				//esame medico resource 
				Resource esameMedicoResource = model.createResource(DataUtil.uriRisBase+esameMedicoCodice+"/p"+pazienteId+"/esameMedico_"+maxIdURI);
				Property esameDuranteRicovero_property = model.createProperty(DataUtil.uriEsameDuranteRicovero);
				Statement statmentEsameMedico = model.createStatement(esameMedicoResource, esameDuranteRicovero_property, ricoveroNew);
				model.add(statmentEsameMedico);

				//metto la data e l'ora in cui si inserisce un referto e la lego all'esame medico
				Literal data_ora_esame = model.createTypedLiteral(data+"T"+ora+":"+"00", XSDDatatype.XSDdateTime);
				Property dataTimeInseriemnto_property = model.createProperty(DataUtil.dataInizioEsame);
				Statement statmentDataOraProperty = model.createStatement(dataTimeInseriemnto_property,RDF.type, RDF.Property);
				model.add(statmentDataOraProperty);
				Statement statmentdataInizioEvento_property = model.createStatement(dataTimeInseriemnto_property,RDFS.subPropertyOf, dataInizioEvento_property);
				model.add(statmentdataInizioEvento_property);
				Statement statmentDataOraRicovero = model.createStatement(esameMedicoResource, dataTimeInseriemnto_property, data_ora_esame);
				model.add(statmentDataOraRicovero);

				
				//codice esame medico
				Property codice_property = model.createProperty(DataUtil.codiceEsame);
				model.add(codice_property, RDF.type, RDF.Property);
				Literal literal_nomeEsame= model.createTypedLiteral(esameMedicoCodice, XSDDatatype.XSDstring);
				model.add(esameMedicoResource, codice_property, literal_nomeEsame);

				
				//value esame medico
				Property value_property = model.createProperty(DataUtil.valueEsame);
				model.add(value_property, RDF.type, RDF.Property);
				Literal literal_valueEsame= model.createTypedLiteral(esameMedicoValue, XSDDatatype.XSDstring);
				model.add(esameMedicoResource, value_property, literal_valueEsame);
				
				//referto medico
				Property refertoRisultante_property = model.createProperty(DataUtil.uriRefertoRisultante);
				Resource refertoMedico_Resource = model.createResource(DataUtil.uriRisBase+"p"+pazienteId+"/refmed_"+maxIdURI);
				Statement statmentReferto = model.createStatement(esameMedicoResource, refertoRisultante_property, refertoMedico_Resource);
				model.add(statmentReferto);

				//testoReferto property
				Property testoReferto_property = model.createProperty(DataUtil.uriTestoReferto);
				Statement statmentTestoReferto = model.createStatement(refertoMedico_Resource, testoReferto_property, refertoString);
				model.add(statmentTestoReferto);
				// model.add(refertoMedico_Resource, testoReferto_property, refertoString);

				Resource refertoMedico = model.createResource(DataUtil.uriRisBase+"RefertoMedico");

				Statement statmentTypeRefertoMedico = model.createStatement(refertoMedico_Resource,  RDF.type, refertoMedico);
				model.add(statmentTypeRefertoMedico);
				Statement statmentTypeRefertoMedico_resource = model.createStatement(refertoMedico_Resource, RDF.type, RDFS.Resource);
				model.add(statmentTypeRefertoMedico_resource);
				Resource esameMedico = model.createResource(DataUtil.uriRisBase+esameMedicoCodice);
				Statement statmentTypeEsameMedico = model.createStatement(esameMedicoResource,  RDF.type, esameMedico);
				model.add(statmentTypeEsameMedico);
				Statement statmentTypeEsameMedico_resource = model.createStatement(esameMedicoResource,  RDF.type, RDFS.Resource);
				model.add(statmentTypeEsameMedico_resource);
				messaggio = "Il referto per il paziente di id "+pazienteId +" e' stata inserito con successo";
				}else{
				log.info("il paziente id non esiste "  + pazienteId +" nel data base");
				messaggio = "Non esiste il paziente di id " + pazienteId +" nel data base";
				return messaggio;
			}
			}
			//caso in cui il referto medico è domiciliare
			else
			{
				System.out.println(">>>>>>>>>>>>>>>>>>REFERTO DOMICILIARE ");
				String maxIdURI = searchMaxIdRefmed(pazienteId);
				System.out.println(" REFERTO DOMICILIARE maxIdURI "+maxIdURI);
				Resource pazienteURI = model.getResource(DataUtil.uriPaziente+pazienteId);
				Statement statmentpaziente_resource = model.createStatement(pazienteURI, RDF.type, RDFS.Resource);
				model.add(statmentpaziente_resource);

				//esameDomiciliare property
				Property esameDomiciliare_property = model.createProperty(DataUtil.uriEsameDomiciliare);
				Statement statmentEsameDomiciliare = model.createStatement(esameDomiciliare_property, RDF.type, RDF.Property);
				model.add(statmentEsameDomiciliare);

				//esame Medico Codice
				Resource esameMedico_resource = model.createResource(DataUtil.uriRisBase+esameMedicoCodice+"/p"+pazienteId+"/esameMedico_"+maxIdURI);
				Statement statmentEsameMedico = model.createStatement(esameMedico_resource, RDF.type, RDFS.Resource);
				model.add(statmentEsameMedico);
				Statement statmentEsameMedico_resource = model.createStatement(pazienteURI, esameDomiciliare_property, esameMedico_resource);
				model.add(statmentEsameMedico_resource);
				
				
				
				//dataora inserimento referto lo lego all'esame medico
				Date today = new Date();
				SimpleDateFormat  DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
				String date = DATE_FORMAT.format(today);
				String data = date.substring(0, date.indexOf(":"));
				String ora = date.substring(date.indexOf(":")+1, date.length());

				Literal data_ora_esame = model.createTypedLiteral(data+"T"+ora+":"+"00", XSDDatatype.XSDdateTime);
				Property dataTimeInseriemnto_property = model.createProperty(DataUtil.dataInizioEsame);
				Statement statmentDataOraProperty = model.createStatement(dataTimeInseriemnto_property,RDF.type, RDF.Property);
				model.add(statmentDataOraProperty);
				Statement statmentDataOraRicovero = model.createStatement(esameMedico_resource, dataTimeInseriemnto_property, data_ora_esame);
				model.add(statmentDataOraRicovero);

				//codice esame medico
				Property codice_property = model.createProperty(DataUtil.codiceEsame);
				model.add(codice_property, RDF.type, RDF.Property);
				Literal literal_nomeEsame= model.createTypedLiteral(esameMedicoCodice, XSDDatatype.XSDstring);
				model.add(esameMedico_resource, codice_property, literal_nomeEsame);

				//refertoRisultante property
				Property refertoRisultante_property = model.createProperty(DataUtil.uriRefertoRisultante);
				Statement statmentRefertoRisultante_property = model.createStatement(refertoRisultante_property, RDF.type, RDF.Property);
				model.add(statmentRefertoRisultante_property);

				//referto medico
				Resource referto_resource = model.createResource(DataUtil.uriRisBase+"p"+pazienteId+"/refmed_"+maxIdURI);
				Statement statmentReferto_resource= model.createStatement(referto_resource, RDF.type, RDFS.Resource);
				model.add(statmentReferto_resource);
				Statement statmentReferto = model.createStatement(esameMedico_resource, refertoRisultante_property, referto_resource);
				model.add(statmentReferto);

			

				//testoReferto property
				Property uriTestoReferto_property = model.createProperty(DataUtil.uriTestoReferto);
				Statement statmentUriTestoReferto_property = model.createStatement(uriTestoReferto_property, RDF.type, RDF.Property);
				model.add(statmentUriTestoReferto_property);
				Statement statmentRef_Testo = model.createStatement(referto_resource, uriTestoReferto_property, refertoString);
				model.add(statmentRef_Testo);

				Resource refertoMedico = model.createResource(DataUtil.uriRisBase+"RefertoMedico");
				Resource esameMedico = model.createResource(DataUtil.uriRisBase+esameMedicoCodice);
				model.add(esameMedico_resource, RDF.type, esameMedico );
				model.add(referto_resource, RDF.type, refertoMedico );
				model.add(referto_resource, RDF.type, RDFS.Resource);
				
				messaggio = "Il referto per il paziente di id "+pazienteId +" e' stata inserito con successo";
				//model.close();
				System.out.println(">>>>>>>>>>>>>>>>>>FINE REFERTO DOMICILIARE ");

			}
			log.info("data "+ft.format(dNow) +" >>> query inserisci nuovo referto: " );
		} 
		catch (Exception e){
			System.out.println("Error into method insertNewReferto: "+e);
			e.printStackTrace();
			messaggio = "Il referto per il paziente di id "+pazienteId +" non e' stata inserito con successo";
			return "failure";
		}
		return messaggio;
	}

	public int isString(String num){
		try{
			Integer.parseInt(num);
			return 0;
		}
		catch(Exception ex){
			return -1;
		}
	}
	/***
	 * metodo per eseguite l'arricchimento semantico utilizzando il tool di ISTI_CNR 
	 * input pazienteID, refertoID
	 * output successful or failure
	 * 
	 */
	@POST 
	@Path("/arricchimentoSemantico")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({"application/json","application/xml"})
	public String arricchimentoServer(@FormParam("arricchimento") String arricchimento){
		String message;
		System.out.println("arricchimento:::"+arricchimento);
		try{
			VirtGraph set = Parameters.getInstance().getVirtGraph();
			Gson gson = new Gson();
			ToolBean toolBean = gson.fromJson(arricchimento, ToolBean.class);
			String pazienteId = toolBean.getPazienteId();
			String refertoId = toolBean.getRefertoId();
			String testoReferto = null;
			String queryselectTestoReferto =  DataUtil.prefix + " SELECT DISTINCT  ?testo FROM  "
					+ DataUtil.IRIRicovero
					+" WHERE {"
					+ " ?URIcodicePaziente ris:codicePersona \""+pazienteId+"\"^^xsd:nonNegativeInteger. " 
					+ " ?ricovero ris:personaRicoverata ?URIcodicePaziente. "
					+ " ?esameMedico ris:esameDuranteRicovero ?ricovero. "
					+ " ?esameMedico ris:refertoRisultante "+"<"+refertoId+">. "
					+ "<"+refertoId+">"+" ris:testoReferto ?testo. "
					+" } ";
			log.info("data "+ft.format(dNow) +" >>> query queryselectTestoReferto   "+queryselectTestoReferto );
			VirtuosoQueryExecution excute_TestoByRefertoId = VirtuosoQueryExecutionFactory.create (queryselectTestoReferto, set);
			ResultSet testoResults = excute_TestoByRefertoId.execSelect();
			while (testoResults.hasNext()) {
				QuerySolution resultTestoReferto = testoResults.nextSolution();
				testoReferto = resultTestoReferto.get("testo").toString();
			}
			System.out.println("testo su cui eseguire arrichimento semantico:: "+testoReferto);
			message = dexter(testoReferto, refertoId);
		}
		catch(Exception e){
			log.error("errore nel metodo arricchimentoServer:: "+ e);
			e.printStackTrace();
			return "failure";
		}
		return message;
	}

	/***
	 * metodo per esegue sequenzialmente prima l'arricchimento semantico utilizzando il tool di ISTI_CNR e poi 
	 * l'analisi del testo utilizzando il tool di UNIPI
	 * input pazienteID, refertoID
	 * output successful or failure
	 * 
	 */
	@POST 
	@Path("/analizza")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({"application/json","application/xml"})
	public String analizzaServer(@FormParam("analizza") String analizza){
		String messageArrichimento;
		String messageAnnotazione;
		System.out.println("analizza:::"+analizza);
		try{
			VirtGraph set = Parameters.getInstance().getVirtGraph();
			Gson gson = new Gson();
			ToolBean toolBean = gson.fromJson(analizza, ToolBean.class);
			String pazienteId = toolBean.getPazienteId();
			String refertoId = toolBean.getRefertoId();
			String testoReferto = null;
			String queryselectTestoReferto =  DataUtil.prefix + " SELECT DISTINCT  ?testo FROM  "
					+ DataUtil.IRIRicovero
					+" WHERE {"
					+ " ?URIcodicePaziente ris:codicePersona \""+pazienteId+"\"^^xsd:nonNegativeInteger. " 
					+ " ?ricovero ris:personaRicoverata ?URIcodicePaziente. "
					+ " ?esameMedico ris:esameDuranteRicovero ?ricovero. "
					+ " ?esameMedico ris:refertoRisultante "+"<"+refertoId+">. "
					+ "<"+refertoId+">"+" ris:testoReferto ?testo. "
					+" } ";
			log.info("data "+ft.format(dNow) +" >>> query queryselectTestoReferto   "+queryselectTestoReferto );
			VirtuosoQueryExecution excute_TestoByRefertoId = VirtuosoQueryExecutionFactory.create (queryselectTestoReferto, set);
			ResultSet testoResults = excute_TestoByRefertoId.execSelect();
			while (testoResults.hasNext()) {
				QuerySolution resultTestoReferto = testoResults.nextSolution();
				testoReferto = resultTestoReferto.get("testo").toString();
			}
			//chiamo l'arrichimento semantico isti e l'annotazione unipi
			 messageArrichimento = dexter(testoReferto, refertoId);
			 messageAnnotazione = annotate(testoReferto, refertoId);
		}
		catch(Exception e){
			log.error("errore nel metodo arricchimentoServer:: "+ e);
			e.printStackTrace();
			return "failure";
		}
		return messageArrichimento + messageAnnotazione;
	}
	
	//
	public String dexter(String referto, String refertoURI){
		String message = "";
		try{
			HashMap<String, String> mapDexter = invoceDexter(referto);
			System.out.println("mapDexter size: per l'uri "+refertoURI + " è " +mapDexter.size()); 
			Model model = Parameters.getInstance().getModelRDF();
			if(mapDexter.size()>0){
				int i = 0;
				Iterator<String> keySetIterator  = mapDexter.keySet().iterator();
				while(keySetIterator.hasNext()) {
					i = i+1;
					//la key è il valore "testo"
					String codIcd9 = (String) keySetIterator.next();
					//il value è il codice ICD9
					String valueIcd9 = mapDexter.get(codIcd9);


					Resource refertoURI_resource = model.createResource(refertoURI);
					Property diagnosiNnconfArricchitaCod = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiNonConfermataArricchitaCod+i);
					Statement statementDiagnosiNnCofArrichitaCod = model.createStatement(refertoURI_resource,diagnosiNnconfArricchitaCod,valueIcd9);
					System.out.println("statment dexter statementDiagnosiNnCofArrichitaCod "+ statementDiagnosiNnCofArrichitaCod);
					model.add(statementDiagnosiNnCofArrichitaCod);


					Property diagnosiNnconfArricchitaTXT = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiNonConfermataArricchitaTxT+i);
					Statement statementDiagnosiNnCofArrichitaTXT = model.createStatement(refertoURI_resource,diagnosiNnconfArricchitaTXT,codIcd9);
					model.add(statementDiagnosiNnCofArrichitaTXT);


					Resource diagnosiNnConf_property = model.createProperty("http://progetto.ris/resource/diagnosiNonConfermata");
					Statement statementTypeArriuchitaCod = model.createStatement(diagnosiNnconfArricchitaCod,RDFS.subPropertyOf ,diagnosiNnConf_property);
					model.add(statementTypeArriuchitaCod);
					//Statement statementTypeArriuchitaValue = model.createStatement(diagnosiNnconfArricchitaTXT,model.getProperty(RDFS.subPropertyOf),diagnosiNnConf_property);
					Statement statementTypeArriuchitaValue = model.createStatement(diagnosiNnconfArricchitaTXT,RDFS.subPropertyOf,diagnosiNnConf_property);
					model.add(statementTypeArriuchitaValue);
					Date dNow = new Date( );
					log.info("data "+ft.format(dNow) +" >>> query insertNewDiagnosiDexter   " );
					message = "successful Arrichimento Semantico";
				}

			}
			else message= "non ci sono risultati prodotti dall'Arrichimento semantico";
		}

		catch(Exception e){
			log.error("errore nel metodo dexter:: "+ e);
			e.printStackTrace();
			return "connessione rifiutata";
		}
		return message;

	}

	/***
	 * metodo per eseguite l'annotazione utilizzando il tool di UNIPI 
	 * input pazienteID, refertoID
	 * output successful or failure
	 * 
	 */
	@POST 
	@Path("/annotate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({"application/json","application/xml"})
	public String annotateServer(@FormParam("annotate") String annotate){
		String message;
		System.out.println("annotate:::"+annotate);
		try{
			VirtGraph set = Parameters.getInstance().getVirtGraph();
            Gson gson = new Gson();
			ToolBean toolBean = gson.fromJson(annotate, ToolBean.class);
			String pazienteId = toolBean.getPazienteId();
			String refertoId = toolBean.getRefertoId();
			String testoReferto = null;
			String queryselectTestoReferto =  DataUtil.prefix + " SELECT DISTINCT  ?testo FROM  "
					+ DataUtil.IRIRicovero
					+" WHERE {"
					+ " ?URIcodicePaziente ris:codicePersona \""+pazienteId+"\"^^xsd:nonNegativeInteger. " 
					+ " ?ricovero ris:personaRicoverata ?URIcodicePaziente. "
					+ " ?esameMedico ris:esameDuranteRicovero ?ricovero. "
					+ " ?esameMedico ris:refertoRisultante "+"<"+refertoId+">. "
					+ "<"+refertoId+">"+" ris:testoReferto ?testo. "
					+" } ";
			log.info("data "+ft.format(dNow) +" >>> query queryselectTestoReferto   "+queryselectTestoReferto );
			VirtuosoQueryExecution excute_TestoByRefertoId = VirtuosoQueryExecutionFactory.create (queryselectTestoReferto, set);
			ResultSet testoResults = excute_TestoByRefertoId.execSelect();
			while (testoResults.hasNext()) {
				QuerySolution resultTestoReferto = testoResults.nextSolution();
				testoReferto = resultTestoReferto.get("testo").toString();
			}
			message = annotate(testoReferto, refertoId);
		}
		catch(Exception e){
			log.error("errore nel metodo annotateServer:: "+ e);
			e.printStackTrace();
			return "failure";
		}
		return message;
	}
	
	/***
	 * metodo che viene chiamato da OO2 serve per inserire i referti offline che loro hanno 
	 * raccolto
	 */
	@POST 
	@Path("/insertEHealth")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({"application/json","application/xml"})
	public String insertEhealth(@FormParam("eHealthReferto") String eHealthReferto){
		String messaggio = "";
		String pazienteId ;
		try{
			System.out.println("eHealthReferto:::"+eHealthReferto);
			Model model = Parameters.getInstance().getModelRDF();
		//	VirtGraph set = Parameters.getInstance().getVirtGraph();
			Gson gson = new Gson();
			
			EHealthBean eHealthBean = gson.fromJson(eHealthReferto, EHealthBean.class);
			String dataReferto = eHealthBean.getDataReferto();
			String oraReferto = eHealthBean.getOraReferto();
			String referto = eHealthBean.getReferto();
			String nomeMedico = eHealthBean.getNomeMedico();
			pazienteId = eHealthBean.getPazienteId();
			String codiceEsame = eHealthBean.getEsameMedico();
			//String refertoId = eHealthBean.getRefertoId();
			String sesso = eHealthBean.getSesso();
			String dataNascita = eHealthBean.getDataNascita();

			System.out.println("dataReferto:: "+dataReferto);
			System.out.println("oraReferto:: "+oraReferto);
			System.out.println("referto:: "+referto);
			System.out.println("nomeMedico:: "+nomeMedico);
			System.out.println("pazienteId:: "+pazienteId);
			System.out.println("codiceEsame:: "+codiceEsame);
			System.out.println("sesso:: "+sesso);
			System.out.println("dataNascita:: "+dataNascita);


			if(dataReferto.equals(""))
				messaggio = "La data del referto non può essere vuota";
			if(oraReferto.equals(""))
				messaggio = "L'ora del referto non può essere vuota";
			if(nomeMedico.equals(""))
				messaggio = "Il nome del medico non può essere vuoto";
			
			//controllo se il paziente è gia esistente dentro il db
			String pazienteNum = checkExistedPaziente(pazienteId);
			
			
			System.out.println("pazienteNum : "+pazienteNum);
			/*
			 * se il paziente non esiste nel bd lo creo
			 * */
		Resource pazienteURI_resource;
        if(pazienteNum.equals("0")){//vuol dire che nn esiste un paziente con l'id passato => creo paziente
        	//per sapere che id associare al paziente faccio un count sul munero dei pazienti, il nuovo paziente avrà id count+1
        	String countPazienti = countNumPazienti();
    
			//creo paziente Uri e il codice per il paziente
			 int pazienteIdInt = Integer.valueOf(countPazienti)+1;
			 pazienteId=String.valueOf(pazienteIdInt);
			 System.out.println("creo paziente di id "+pazienteId);
			 pazienteURI_resource = model.createResource(DataUtil.uriPaziente+pazienteId);
			System.out.println("pazienteURI_resource:: "+pazienteURI_resource.toString());
			Statement type_paziente_statement = model.createStatement(pazienteURI_resource, RDF.type, model.createResource(DataUtil.uriPersona));
			model.add(type_paziente_statement);
			Statement type_resource_statement = model.createStatement(pazienteURI_resource, RDF.type, RDFS.Resource);
			model.add(type_resource_statement);
			Statement subClass_resource_statement = model.createStatement(pazienteURI_resource, RDFS.subClassOf, model.createResource(DataUtil.uriPersona));
			model.add(subClass_resource_statement);
			
			
			//codicePaziente
			Property codicePaziente_property = model.createProperty(DataUtil.codicePaziente);
			Statement codicePersona_type_statement = model.createStatement(codicePaziente_property, RDF.type, RDF.Property);
			model.add(codicePersona_type_statement);
			Literal codicePaziente_literal = model.createTypedLiteral(pazienteId, XSDDatatype.XSDnonNegativeInteger);
			Statement codicePersona_statement = model.createStatement(pazienteURI_resource, codicePaziente_property, codicePaziente_literal);
			model.add(codicePersona_statement);
			
			//sesso
			Property sessoPaziente_property = model.createProperty(DataUtil.genderPersona);
			Statement sessoPersona_statement = model.createStatement(sessoPaziente_property, RDF.type, RDF.Property);
			model.add(sessoPersona_statement);
			Statement sessoPersona_statementBridge = model.createStatement(pazienteURI_resource, sessoPaziente_property, sesso);
			model.add(sessoPersona_statementBridge);
			
			//data nascita
			Property dataNascitaPaziente_property = model.createProperty(DataUtil.dataNascita);
			Statement dataNascitaPersona_statement = model.createStatement(dataNascitaPaziente_property, RDF.type, RDF.Property);
			model.add(dataNascitaPersona_statement);
			Statement dataNascitaPersona_statementBridge = model.createStatement(pazienteURI_resource, dataNascitaPaziente_property, dataNascita);
			model.add(dataNascitaPersona_statementBridge);
			
			//dataMining real
		/*	Property dataMiningRealPaziente_property = model.createProperty(DataUtil.dataMiningReal);
			Statement dataMiningRealPersona_statement = model.createStatement(dataMiningRealPaziente_property, RDF.type, RDF.Property);
			model.add(dataMiningRealPersona_statement);
			Statement dataMiningRealPersonaAdd_statement = model.createStatement(pazienteURI_resource, dataMiningRealPaziente_property, "Non Calcolabile");
			model.add(dataMiningRealPersonaAdd_statement);
			
			//dataMining predicted
			Property dataMiningPredictedPaziente_property = model.createProperty(DataUtil.dataMiningPredicted);
			Statement dataMiningPredictedPersona_statement = model.createStatement(dataMiningPredictedPaziente_property, RDF.type, RDF.Property);
			model.add(dataMiningPredictedPersona_statement);
			Statement dataMiningPredictedPersonaAdd_statement = model.createStatement(pazienteURI_resource, dataMiningPredictedPaziente_property, "Non Calcolabile");
			model.add(dataMiningPredictedPersonaAdd_statement);
			*/
        }
        //il cod paziente è presente nella DB
        else{
        	System.out.println("già esiste");
        
          pazienteURI_resource = model.getResource(DataUtil.uriPaziente+pazienteId);
          //devo creare l'uri del referto con un valore incrementale perchè il paziente esiste già
      	 //  refertoId = searchMaxIdRefmed(pazienteId);
      	  // System.out.println("refertoId "+refertoId);
        }
        
        String refertoId = searchMaxIdRefmed(pazienteId);
        if(codiceEsame!=null && codiceEsame.equals(""))
			codiceEsame = refertoId;
        
        System.out.println("refertoId "+refertoId);
			//esamedomiciliare
			Property esameDomiciliare_property = model.createProperty(DataUtil.uriEsameDomiciliare);
			model.add(esameDomiciliare_property, RDF.type, RDF.Property);
			//esame medico
			Resource esameMedico_resource = model.createResource(DataUtil.uriRisBase+"esame002_"+codiceEsame+"/p"+pazienteId+"/esameMedico_"+refertoId);
			model.add(esameMedico_resource, RDF.type, RDFS.Resource);
			Resource esameMedico = model.createResource(DataUtil.uriRisBase+"esame002_"+codiceEsame);
			model.add(esameMedico, RDF.type, RDFS.Resource);
			model.add(esameMedico_resource, RDF.type, esameMedico);
			model.add(pazienteURI_resource, esameDomiciliare_property, esameMedico_resource);

			//codice esame medico
			Property codice_property = model.createProperty(DataUtil.codiceEsame);
			model.add(codice_property, RDF.type, RDF.Property);
			Literal literal_nomeEsame= model.createTypedLiteral(codiceEsame, XSDDatatype.XSDstring);
			model.add(esameMedico_resource, codice_property, literal_nomeEsame);
			
			
			//data_ora inserimento referto lo lego all'esame medico
			String[] oraRefertoSplit = oraReferto.split(":");
			String hh = oraRefertoSplit[0];
			String min = oraRefertoSplit[1];
			String sec = oraRefertoSplit[2];


			String[] dataRefertoSplit = dataReferto.split("/");
			String yyyyEsame = dataRefertoSplit[2];
			String mmEsame = dataRefertoSplit[1];
			String ggEsame = dataRefertoSplit[0];
			Literal data_ora_esame = model.createTypedLiteral(yyyyEsame+"-"+mmEsame+"-"+ggEsame+"T"+hh+":"+min+":"+sec, XSDDatatype.XSDdateTime);
			
			Property dataTimeInseriemntoReferto_property = model.createProperty(DataUtil.dataInizioEsame);
			Statement statmentDataOraProperty_ref = model.createStatement(dataTimeInseriemntoReferto_property,RDF.type, RDF.Property);
			model.add(statmentDataOraProperty_ref);
			
			Statement statmentDataOraRicovero = model.createStatement(esameMedico_resource, dataTimeInseriemntoReferto_property, data_ora_esame);
			model.add(statmentDataOraRicovero);

			//referto risultante
			Property uriRefertoRisultante_property = model.createProperty(DataUtil.uriRefertoRisultante);
			model.add(uriRefertoRisultante_property, RDF.type, RDF.Property);
			Resource referto_resource = model.createResource(DataUtil.uriRisBase+"p"+pazienteId+"/refmed_"+refertoId);
			model.add(referto_resource, RDF.type, RDFS.Resource);
			model.add(esameMedico_resource, uriRefertoRisultante_property, referto_resource);

			//testo referto
			Property uriTestoReferto_property = model.createProperty(DataUtil.uriTestoReferto);
			model.add(uriTestoReferto_property, RDF.type, RDF.Property);
			model.add(referto_resource, uriTestoReferto_property, referto);


			Property dataTimeInseriemnto_property = model.createProperty(DataUtil.dataInserimento);
			model.add(dataTimeInseriemnto_property, RDF.type, RDF.Property);
			model.add(referto_resource, dataTimeInseriemnto_property, data_ora_esame);

			//medico che ha stilato il referto
			Property da_property = model.createProperty(DataUtil.uriRisBase+"da");
			model.add(da_property, RDF.type, RDF.Property);
			model.add(referto_resource, da_property, nomeMedico);
			
		
			
		}
		catch(Exception e){
			log.error("errore nel metodo insertEhealth:: "+ e+ " messaggio "+messaggio);
			e.printStackTrace();
			return "failure"+messaggio;
		}
		return "Il referto per il paziente di id " + pazienteId +" è stato inserito con successo";
	}





	/*
	 * modifica i dati estratti arricchiti dai tool dopo che il medico ha confermato o no  
	 * le diagnosi, chiama il metodo addEsaminatoDa che aggiunge al modello le informazioni
	 * medico che effettua la bonta' dei dati estratti dai tool data della verifica 
	 */
	@POST 
	@Path("/updateDiagnosi")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({"application/json","application/xml"})
	public String updateDiagnosi(@FormParam("updataDiagnosiData") String updateData){
		String messaggio = null;
		try{
			Gson gson = new Gson();
			System.out.println("dati per fare l'update delle diagnosi derivanti da tool "+updateData);
			UpdateDatiBean updateDatiDiagnosi = gson.fromJson(updateData, UpdateDatiBean.class);
			String refertoURI = updateDatiDiagnosi.getRefertoUri();
			ArrayList<UpdateDatiEnrichedBean> enrichedList = updateDatiDiagnosi.getDatiEnrichedList();
			ArrayList<UpdateDatiExtractedBean> extractedList = updateDatiDiagnosi.getDatiExtractedList();
			ArrayList<UpdateDatiScartatiBean> scartatiList = updateDatiDiagnosi.getDatiScartatiList();
			
			Model model = Parameters.getInstance().getModelRDF();

			
			Resource referto_resource = model.getResource(refertoURI);
			if(enrichedList != null){
				if(enrichedList.size()>0){
					System.out.println("enrichedList.size() "+enrichedList.size());
					for(int i =0; i<enrichedList.size(); i++){
						/////////////////////
						/////cod arricchita/
						///////////////////
						System.out.println("refertoURI da modificare "+referto_resource.toString());
						String arrichitaCoduRI = enrichedList.get(i).getArrichitaCodURI();
						System.out.println("arrichitaCoduRI "+arrichitaCoduRI);
						String numberCod = arrichitaCoduRI.substring((arrichitaCoduRI.lastIndexOf("_")+1), arrichitaCoduRI.length());
						Property arrichitaCod_property = model.getProperty(arrichitaCoduRI);
						System.out.println("arrichitaCod_property da modificare "+arrichitaCod_property.toString());
						String literal_cod = enrichedList.get(i).getArrichitaCodICD9();
						System.out.println("codice "+literal_cod);
						Statement arrichitaCod_statment = model.createStatement(referto_resource,arrichitaCod_property,literal_cod);
						System.out.println("arrichitaCod_statment "+arrichitaCod_statment);
						if(model.contains(arrichitaCod_statment)){// http://progetto.ris/resource/dnc_arricchitaCod_1 http://progetto.ris/resource/dnc_arricchitaCod_1
							System.out.println("*****************************");
							System.out.println("*****************************");
							//creo la nuova
							Property diagnosiConfArricchitaCod = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiConfermataArricchitaCod+numberCod);
							Statement statementDiagnosiConfArrichitaCod = model.createStatement(referto_resource,diagnosiConfArricchitaCod,literal_cod);
							model.add(statementDiagnosiConfArrichitaCod);

							//rimuovo la vecchia
							model.remove(arrichitaCod_statment);
							System.out.println("sì contienete lo statment codice");
						}
						///////////////////////
						/////value arrichita//
						/////////////////////
						String arrichitaValueURI = enrichedList.get(i).getArrichitaValueURI();
						Property arrichitaValue_property = model.getProperty(arrichitaValueURI);
						String numberValue = arrichitaValueURI.substring((arrichitaValueURI.lastIndexOf("_")+1), arrichitaValueURI.length());
						System.out.println("arrichitaCod_property da modificare "+arrichitaValue_property.toString());
						String literal_value = enrichedList.get(i).getArrichitaCodICD9Value();
						System.out.println("value  "+literal_value);
						Statement arrichitaValue_statment = model.createStatement(referto_resource,arrichitaValue_property,literal_value);
						if(model.contains(arrichitaValue_statment)){
							//creo la nuova
							Property diagnosiConfArricchitaTxT = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiConfermataArricchitaTxT+numberValue);
							Statement statementDiagnosiConfArrichitaTxT = model.createStatement(referto_resource,diagnosiConfArricchitaTxT,literal_value);
							model.add(statementDiagnosiConfArrichitaTxT);

							//rimuovo la vecchia
							model.remove(arrichitaValue_statment);
							System.out.println("sì contienete lo statment value");
						}

					}
				}
			}
			
			//modifica extracted (dati UNIPI daniele)
			if(extractedList != null){
				if(extractedList.size()>0){
					for(int i = 0; i<extractedList.size(); i++){

						/////////////////////
						/////cod estratta //
						///////////////////
						System.out.println("refertoURI da modificare "+referto_resource.toString());
						String extractedCodURI = extractedList.get(i).getExtractedCodURI();
						String numberCod = extractedCodURI.substring((extractedCodURI.lastIndexOf("_")+1), extractedCodURI.length());
						Property extractedCodOLD_property = model.getProperty(extractedCodURI);
						System.out.println("extractedCod_property da modificare "+extractedCodOLD_property.toString());
						String literal_cod = extractedList.get(i).getExtractedCodICD9();
						System.out.println("literal "+literal_cod);
						Statement extractedCod_statment = model.createStatement(referto_resource,extractedCodOLD_property,literal_cod);
						if(model.contains(extractedCod_statment)){
							//creo la nuova
							Property diagnosiConfEstrattaCod = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiConfermataEstrattaCod+numberCod);
							Statement statementDiagnosiConfEstrattaCod = model.createStatement(referto_resource,diagnosiConfEstrattaCod,literal_cod);
							model.add(statementDiagnosiConfEstrattaCod);

							//rimuovo la vecchia
							model.remove(extractedCod_statment);
							System.out.println("sì contienete lo statment codice");
						}
						///////////////////////
						/////value estratta //
						/////////////////////
						String estrattaValueURI = extractedList.get(i).getExtractedValueURI();
						Property estrattaValue_property = model.getProperty(estrattaValueURI);
						String numberValue = estrattaValueURI.substring((estrattaValueURI.lastIndexOf("_")+1), estrattaValueURI.length());
						System.out.println("arrichitaCod_property da modificare "+estrattaValue_property.toString());
						String literal_value = extractedList.get(i).getExtractedCodICD9Value();
						System.out.println("literal "+literal_value);
						Statement estrattaValue_statment = model.createStatement(referto_resource,estrattaValue_property,literal_value);
						if(model.contains(estrattaValue_statment)){
							//creo la nuova
							Property diagnosiConfEstrattaTxT = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiConfermataEstrattaTxt+numberValue);
							Statement statementDiagnosiConfEstrattaTxT = model.createStatement(referto_resource,diagnosiConfEstrattaTxT,literal_value);
							model.add(statementDiagnosiConfEstrattaTxT);

							//rimuovo la vecchia
							model.remove(estrattaValue_statment);
							System.out.println("sì contienete lo statment value");
						}


					}
				}
			}
				
				///////////////////////
				/////dati scartati////
				/////////////////////
				if(scartatiList != null){
					if(scartatiList.size()>0){
						for(int i =0; i<scartatiList.size(); i++){	
							/////////////////////
							/////cod estratta //
							///////////////////
							System.out.println("refertoURI da modificare "+referto_resource.toString());
							String daScartareCodURI = scartatiList.get(i).getScartatiCodURI();
							String numberCod = daScartareCodURI.substring((daScartareCodURI.lastIndexOf("_")+1), daScartareCodURI.length());
							Property daScartareCodOLD_property = model.getProperty(daScartareCodURI);
							System.out.println("daScartareCodOLD_property da modificare "+daScartareCodOLD_property.toString());
							String literal_cod = scartatiList.get(i).getScartatiCodICD9();
							System.out.println("literal "+literal_cod);
							Statement daScartareCod_statment = model.createStatement(referto_resource,daScartareCodOLD_property,literal_cod);
							if(model.contains(daScartareCod_statment)){
								//creo la nuova
								Property diagnosiDaScartareCod = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiScartataCod+numberCod);
								Statement statementDiagnosiDaScartareCod = model.createStatement(referto_resource,diagnosiDaScartareCod,literal_cod);
								model.add(statementDiagnosiDaScartareCod);

								//rimuovo la vecchia
								model.remove(daScartareCod_statment);
								System.out.println("sì contienete lo statment codice");
							}
							///////////////////////
							/////value estratta //
							/////////////////////
							String daScartareValueURI = scartatiList.get(i).getScartatiValueURI();
							Property daScartareValue_property = model.getProperty(daScartareValueURI);
							String numberValue = daScartareValueURI.substring((daScartareValueURI.lastIndexOf("_")+1), daScartareValueURI.length());
							System.out.println("daScartareValue_property da modificare "+daScartareValue_property.toString());
							String literal_value = scartatiList.get(i).getScartatiCodICD9Value();
							System.out.println("literal "+literal_value);
							Statement daScartareValue_statment = model.createStatement(referto_resource,daScartareValue_property,literal_value);
							if(model.contains(daScartareValue_statment)){
								//creo la nuova
								Property diagnosiDaScartareTxT = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiScartataTxT+numberValue);
								Statement statementDaScartareTxT = model.createStatement(referto_resource,diagnosiDaScartareTxT,literal_value);
								model.add(statementDaScartareTxT);

								//rimuovo la vecchia
								model.remove(daScartareValue_statment);
								System.out.println("sì contienete lo statment value");
							}
						}
			} 
				}
				//model.close();
			}
		catch (Exception e){
			System.out.println("Error into method modifica diagnosi: "+e);
			e.printStackTrace();
			return "failure:: "+ messaggio;
		}
		messaggio = "successful";
		return messaggio;
	}



	private void addEsaminatoDa(Resource referto_resource, Model model) {
		//proprietà esaminato
		int i= referto_resource.toString().indexOf("_");
		String number = referto_resource.toString().substring(i+1, referto_resource.toString().length());
		Property esaminato_property = model.createProperty(DataUtil.uriRisBase+"esaminato");
		Resource esame_referto_resource = model.createResource(DataUtil.esame_referto+number);   
		model.add(esame_referto_resource, RDF.type, RDFS.Resource);
		model.add(esaminato_property, RDF.type, RDF.Property);
		Statement esaminato_ref_esame_statement = model.createStatement(referto_resource, esaminato_property, esame_referto_resource);
		model.add(esaminato_ref_esame_statement);

		//proprieta da
		Property da_property = model.createProperty(DataUtil.uriRisBase+"da");
		model.add(da_property, RDF.type, RDF.Property);
		String medico= "Mario Rossi";
		Statement da_ref_esame_statement = model.createStatement(esame_referto_resource, da_property, medico);
		model.add(da_ref_esame_statement);

		//proprieta dataModifica
		Property dataModifica_property = model.createProperty(DataUtil.uriRisBase+"dataModifica");
		model.add(dataModifica_property, RDF.type, RDF.Property);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String date = sdf.format(new Date()); 
		Literal dataMod_literal = model.createTypedLiteral(date, XSDDatatype.XSDdate);
		Statement dataModifica_ref_esame_statement = model.createStatement(esame_referto_resource, dataModifica_property, dataMod_literal);
		model.add(dataModifica_ref_esame_statement);
	}

	//cerca il max id di referto medico (refmed_) dato l'id del paziente
	public String searchMaxIdRefmed(String pazienteId){
		String maxURIRefertoMed = null;

		try{
			VirtGraph set = Parameters.getInstance().getVirtGraph();
			String searchMaxId = DataUtil.prefix+ "  SELECT (COUNT(?URIreferto) AS ?count) FROM " 
					+ DataUtil.IRIRicovero
					+" WHERE {{"
					+ " ?URIcodicePaziente ris:codicePersona \""+pazienteId+"\"^^xsd:nonNegativeInteger. "
					+ " ?ricovero ris:personaRicoverata ?URIcodicePaziente. "
					+ " ?esameMedico ris:esameDuranteRicovero ?ricovero. "
					+ " ?esameMedico ris:refertoRisultante ?URIreferto. "
					+" }  UNION {"
					+ " ?URIcodicePaziente ris:codicePersona \""+pazienteId+"\"^^xsd:nonNegativeInteger. "
					+ " ?URIcodicePaziente ris:esameDomiciliare ?esameMedico. "
					+ " ?esameMedico ris:refertoRisultante ?URIreferto. "
					+ " ?URIreferto ris:testoReferto ?testo"
					+ " } }";

			ft =  new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
			log.info("data "+ft.format(dNow) +" >>> query searchMaxId referto medico   "+searchMaxId );
			VirtuosoQueryExecution virtQuerySearchMaxIdRefertoMedico = VirtuosoQueryExecutionFactory.create (searchMaxId, set);
			ResultSet maxIdRefUri = virtQuerySearchMaxIdRefertoMedico.execSelect();
			while (maxIdRefUri.hasNext()) {
				QuerySolution resultIDMax = maxIdRefUri.nextSolution();
				maxURIRefertoMed = resultIDMax.get("count").toString();
			}
		} 
		catch (Exception e){
			System.out.println("Error into method maxURIRefertoMed: "+e);
		}
		return clearStringResult(maxURIRefertoMed);
	}

	//cerca il max id esameMedico (esameMedico_7) dato l'id del paziente
	public String searchMaxIdEsameMedico(String pazienteId){
		String maxURIesameMedicoMax = null;

		try{
			VirtGraph set = Parameters.getInstance().getVirtGraph();
			String searchMaxId = DataUtil.prefix+ "  SELECT (COUNT(?esameMedico ) AS ?count) FROM " 
					+ DataUtil.IRIRicovero
					+" WHERE {"
					+ " ?URIcodicePaziente ris:codicePersona \""+pazienteId+"\"^^xsd:nonNegativeInteger. "
					+ " ?ricovero ris:personaRicoverata ?URIcodicePaziente. "
					+ " ?esameMedico ris:esameDuranteRicovero ?ricovero. "
					+" }";
			ft =  new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
			log.info("data "+ft.format(dNow) +" >>> query searchMaxId esame medico   "+searchMaxId );
			VirtuosoQueryExecution virtQuerySearchMaxIdRefertoMedico = VirtuosoQueryExecutionFactory.create (searchMaxId, set);
			ResultSet maxIdRefUri = virtQuerySearchMaxIdRefertoMedico.execSelect();
			while (maxIdRefUri.hasNext()) {
				QuerySolution resultIDMax = maxIdRefUri.nextSolution();
				maxURIesameMedicoMax = resultIDMax.get("count").toString();
			}
		} 
		catch (Exception e){
			System.out.println("Error into method searchMaxIdEsameMedico: "+e);
		}
		return clearStringResult(maxURIesameMedicoMax);
	}

	//cerca il max id di ricovero (ricovero_)  dato l'id del paziente
	public String searchMaxIdRicovero(String pazienteId){
		String maxURIRicoveroMax = null;

		try{
			VirtGraph set = Parameters.getInstance().getVirtGraph();
			String searchMaxId = DataUtil.prefix+ "  SELECT (COUNT(?ricovero) AS ?count) FROM " 
					+ DataUtil.IRIRicovero
					+" WHERE {"
					+ " ?URIcodicePaziente ris:codicePersona \""+pazienteId+"\"^^xsd:nonNegativeInteger. "
					+ " ?ricovero ris:personaRicoverata ?URIcodicePaziente. "
					+" }";
			ft =  new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
			log.info("data "+ft.format(dNow) +" >>> query searchMaxId ricovero   "+searchMaxId );
			VirtuosoQueryExecution virtQuerySearchMaxIdRefertoMedico = VirtuosoQueryExecutionFactory.create (searchMaxId, set);
			ResultSet maxIdRefUri = virtQuerySearchMaxIdRefertoMedico.execSelect();
			while (maxIdRefUri.hasNext()) {
				QuerySolution resultIDMax = maxIdRefUri.nextSolution();
				maxURIRicoveroMax = resultIDMax.get("count").toString();
			}
		} 
		catch (Exception e){
			System.out.println("Error into method searchMaxIdRicovero: "+e);
		}
		return clearStringResult(maxURIRicoveroMax);
	}

	@GET  
	@Path("/json/storicoPazienti")  
	@Produces("application/json")  
	public List<PazientiStoricoBean> getStoricoPazienti(){  
		return getStorico();        
	}  
	
	
	//controllo se il paziente è gia esistente dentro il db
	public String checkExistedPaziente(String pazienteId){
		String pazienteNum = null;
		try{
			VirtGraph set = Parameters.getInstance().getVirtGraph();
			String queryExistPazienteId =  DataUtil.prefix + " SELECT (COUNT(?URIcodicePaziente ) AS ?count) FROM  "
					+ DataUtil.IRIRicovero
					+" WHERE {"
					+ " ?URIcodicePaziente ris:codicePersona \""+pazienteId+"\"^^xsd:nonNegativeInteger. " 
					+" } ";
			log.info("data "+ft.format(dNow) +" >>> query queryExistPazienteId   "+queryExistPazienteId );
			VirtuosoQueryExecution excute_ExistedIdPaziente = VirtuosoQueryExecutionFactory.create (queryExistPazienteId, set);
			ResultSet maxIdPazResults = excute_ExistedIdPaziente.execSelect();
			while (maxIdPazResults.hasNext()) {
				QuerySolution resultPaziente = maxIdPazResults.nextSolution();
				pazienteNum = clearStringResult(resultPaziente.get("count").toString());
			}
		}
		catch(Exception e){
			log.error("errore nel metodo check Existed Paziente:: ");
			e.printStackTrace();
		}
		return pazienteNum;

	}
	
	
	//per sapere che l'id da associare al paziente faccio un count sul numero dei pazienti, il nuovo paziente avrà id count+1 come id
	public String countNumPazienti(){
		String countPazienti = null;
		try{
			VirtGraph set = Parameters.getInstance().getVirtGraph();
			String queryCountIdPazienti =  DataUtil.prefix + " SELECT (COUNT(?URIcodicePaziente ) AS ?count) FROM  "
					+ DataUtil.IRIRicovero
					+" WHERE {"
					+ " ?URIcodicePaziente ris:codicePersona ?codice. " 
					+" } ";
			log.info("data "+ft.format(dNow) +" >>> query queryCountIdPazienti   "+queryCountIdPazienti );
			VirtuosoQueryExecution excute_countPazienti = VirtuosoQueryExecutionFactory.create (queryCountIdPazienti, set);
			ResultSet countResults = excute_countPazienti.execSelect();
			while (countResults.hasNext()) {
				QuerySolution resultCountPaziente = countResults.nextSolution();
				countPazienti = clearStringResult(resultCountPaziente.get("count").toString());
			}
		}
		catch(Exception e){
			log.error("errore nel metodo check Existed Paziente:: ");
			e.printStackTrace();
		}
		
		return countPazienti;
	}


   /**
	 * è il primo metodo che viene chiamato dalla gui, restituisce tutti i pazienti con le informazioni 
	 * -codPaziente
	 * -esame
	 * -normalita
	 * -dataNascita
	 * -sesso
	 *
	 */
	public List<PazientiStoricoBean> getStorico() {
		List<PazientiStoricoBean> pazientiStoricoList = new ArrayList<PazientiStoricoBean>();	
		try{
			VirtGraph set = Parameters.getInstance().getVirtGraph();
			String searchStorico = DataUtil.prefix+ "  SELECT ?codice (str(?codice) AS ?stdcodice) ?dataNascita  ?sesso " 
					+ " FROM NAMED " + DataUtil.IRIRicovero
					+" WHERE {"
					+ " GRAPH " + DataUtil.IRIRicovero
					+ " { "
					+ " ?URIcodicePaziente ris:codicePersona ?codice. "
					+ " ?URIcodicePaziente ris:dataNascita ?dataNascita. "
					+ " ?URIcodicePaziente ris:genderPersona ?sesso. "
					+" }"
					+" } order BY ?codice ";
			ft =  new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
			log.info("data "+ft.format(dNow) +" >>> query storico pazienti  "+searchStorico );
			System.out.println("query storico pazienti  "+searchStorico);
			VirtuosoQueryExecution virtStoricoVeriQuery = VirtuosoQueryExecutionFactory.create (searchStorico, set);
			ResultSet resultStorico = virtStoricoVeriQuery.execSelect();
			// -----------------------------------------------------------		
			PazientiStoricoBean pazienteStorico = null;
			//
			while (resultStorico.hasNext()) {

				QuerySolution result = resultStorico.nextSolution();
				String codice =  result.get("stdcodice").toString();
				String dataNascita = result.get("dataNascita").toString();
				String sesso = result.get("sesso").toString();
				pazienteStorico = new PazientiStoricoBean(); 
				pazienteStorico.setPazienteId(codice);
				pazienteStorico.setDataNascita(dataNascita);
				pazienteStorico.setSesso(sesso);
				pazientiStoricoList.add(pazienteStorico);
			}
		}
		catch(Exception e){
			log.error("errore dentro il metodo getStorico"+ e);
			System.out.println("Error into server method getStorico: "+e);
		}
		return pazientiStoricoList;
	}
	//vecchio metodo con tt le analisi
	/*public List<PazientiStoricoBean> getStorico() {
		List<PazientiStoricoBean> pazientiStoricoList = new ArrayList<PazientiStoricoBean>();	
		try{
			VirtGraph set = Parameters.getInstance().getVirtGraph();
			String searchStorico = DataUtil.prefix+ "  SELECT ?codice (str(?codice) AS ?stdcodice) (str(?esame ) AS ?stdEsame) ?dataNascita  ?sesso (str(?normalita) AS ?stdNormalita) " 
					+ " FROM NAMED " + DataUtil.IRIRicovero
					+ " FROM NAMED " + DataUtil.IRIPrelievo
					+" WHERE {"
					+ " GRAPH " + DataUtil.IRIRicovero
					+ " { "
					+ " ?URIcodicePaziente ris:codicePersona ?codice. "
					+ " ?URIcodicePaziente ris:dataNascita ?dataNascita. "
					+ " ?URIcodicePaziente ris:genderPersona ?sesso. "
					+" }"
					+ " GRAPH " + DataUtil.IRIPrelievo
					+ " { "
					+ " ?URIcodicePaziente ris:codicePersona ?codice. "
					+ " ?prelievo ris:prelievoEffettuatoA ?URIcodicePaziente. " 
					+ " ?tipoEsame ris:tipoEsame ?esame. "
					+ " ?tipoEsame ris:inPrelievo ?prelievo. "
					+ " ?tipoEsame ris:normalita ?normalita."
					+" }.} order BY ?codice ";
			ft =  new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
			log.info("data "+ft.format(dNow) +" >>> query storico pazienti  "+searchStorico );
			VirtuosoQueryExecution virtStoricoVeriQuery = VirtuosoQueryExecutionFactory.create (searchStorico, set);
			ResultSet resultStorico = virtStoricoVeriQuery.execSelect();
			// -----------------------------------------------------------
			String codiceTemp = "0"; // un valore che non incontrerò mai !
			Analisi analisi = null;
			ArrayList<Analisi> analisiList = null;
			PazientiStoricoBean pazienteStorico = null;
			//
			while (resultStorico.hasNext()) {

				QuerySolution result = resultStorico.nextSolution();
				String codice =  result.get("stdcodice").toString();
				String dataNascita = result.get("dataNascita").toString();
				String sesso = result.get("sesso").toString();
				if(!codice.equals(codiceTemp))
				{
					if (analisiList != null) {
						pazienteStorico.setAnalisiList(analisiList);
						pazientiStoricoList.add(pazienteStorico);
					}
					// inserire nuovo paziente
					pazienteStorico = new PazientiStoricoBean(); 
					pazienteStorico.setPazienteId(codice);
					pazienteStorico.setDataNascita(dataNascita);
					pazienteStorico.setSesso(sesso);
					// inserire nuova analisi
					analisi = new Analisi();
					analisi.setEsame(result.get("stdEsame").toString());
					analisi.setNormalita(result.get("stdNormalita").toString());
					analisiList = new ArrayList<Analisi>();
					analisiList.add(analisi);
					// !
					codiceTemp = codice;
				} else{
					analisi = new Analisi();
					analisi.setEsame(result.get("stdEsame").toString());
					analisi.setNormalita(result.get("stdNormalita").toString());
					analisiList.add(analisi);
				}
			}
			// inserisco ultimo paziente !
			pazienteStorico.setAnalisiList(analisiList);
			pazientiStoricoList.add(pazienteStorico);
			// ------------------------------------------------------------------------
		}
		catch(Exception e){
			log.error("errore dentro il metodo getStorico"+ e);
			System.out.println("Error into server method getStorico: "+e);
		}
		return pazientiStoricoList;
	}
*/

	public String clearStringResult(String count){
		int lastOccurence = count.indexOf("^^");
		String newCount = count.substring(0, (lastOccurence));
		return newCount;
	}



	/*@POST 
 @Path("/dexter")
 @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
 @Produces({"application/json","application/xml"})
 public Response insertDiagnosi(@FormParam("jsonObject") String jsonObject){
	 try{
		 ft =  new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
		 List<RefertoInfo> refertoList = getURIReferto();
		 Model model = Parameters.getInstance().getModelRDF();
		 for(int k=0; k<refertoList.size(); k++){
			 String referto = refertoList.get(k).getReferto();
			 String refertoURI = refertoList.get(k).getRefertoURI();
			 HashMap<String, String> mapDexter = invoceDexter(referto);
			 if(mapDexter.size()>0){
				 Iterator<String> keySetIterator  = mapDexter.keySet().iterator();
				 while(keySetIterator.hasNext()) {
					 String codIcd9 = (String) keySetIterator.next();
					 String valueIcd9 = mapDexter.get(codIcd9);
					 System.out.println("\ncodIcd9: "+ codIcd9); 
					 System.out.println("\nvalueIcd9: "+ valueIcd9); 

					 Resource refertoURI_resource = model.createResource(refertoURI);
					 Property diagnosiNnconfArricchitaCod = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiNonConfermataArricchitaCod+k);
					 model.add(refertoURI_resource, diagnosiNnconfArricchitaCod, codIcd9);

					 Property diagnosiNnconfArricchitaTXT = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiNonConfermataArricchitaTxT+k);
					 model.add(refertoURI_resource, diagnosiNnconfArricchitaTXT, valueIcd9);

					 Resource diagnosiNnConf_property = model.createProperty("http://progetto.ris/resource/diagnosiNonConfermata");
					 model.add(diagnosiNnconfArricchitaCod, model.getProperty("http://www.w3.org/2000/01/rdf-schema#subPropertyOf"),  diagnosiNnConf_property);
					 model.add(diagnosiNnconfArricchitaTXT, model.getProperty("http://www.w3.org/2000/01/rdf-schema#subPropertyOf"),  diagnosiNnConf_property);

					 log.info("data "+ft.format(dNow) +" >>> query insertNewDiagnosiDexter   " );
				 }
			 }
		 }
		 System.out.println("jsonObject: - "+jsonObject);
	 } 
	 catch (Exception e){
		 System.out.println("Error into method insertDiagnosi: "+e);
	 }
	 return Response.status(200).build();
 }
	 */


	/////////////////////////////////////////////////////
	//////pezzo di codice per navigare il modello///////
	///////////////////////////////////////////////////




	/*			//List<String> refertoURIList =  searchUriRefertoByidPaziente(pazienteId);
//	for(int i = 0; i<numStatement; i++){
		// Resource referto_resource = model.createResource(refertoURIList.get(i));
		  StmtIterator iter  = model.listStatements(referto_resource,null,(RDFNode) null);
		  while(iter.hasNext()) {
			  DiagnosiArricchitaNotConfermata  dnc_arrichita = new DiagnosiArricchitaNotConfermata();
			   Statement stmt = iter.nextStatement();
			   if((stmt.getPredicate().toString()).contains("dnc_arricchitaCod_")){

				   dnc_arrichita.setDnc_arricchitaCod(stmt.getObject().toString());
				   dnc_arrichita.setDnc_arricchitaCodUri(stmt.getPredicate().toString());
			   System.out.println("\n********************************* ");
			   System.out.println("Soggetto "+stmt.getSubject().toString());
			   System.out.println("predicato "+stmt.getPredicate().toString());
			   System.out.println("Oggetto "+stmt.getObject().toString());
			   System.out.println("\n********************************* ");
			   }
			   if((stmt.getPredicate().toString()).contains("dnc_arricchitaTxT_")){
				   dnc_arrichita.setDnc_arricchitaTxT(stmt.getObject().toString());
				   dnc_arrichita.setDnc_arricchitaTxTUri(stmt.getPredicate().toString());
				   System.out.println("\n********************************* ");
				   System.out.println("Soggetto "+stmt.getSubject().toString());
				   System.out.println("predicato "+stmt.getPredicate().toString());
				   System.out.println("Oggetto "+stmt.getObject().toString());
				   System.out.println("\n********************************* ");
				   }
			   if((stmt.getPredicate().toString()).contains("dnc_estrattaCod_")){
				   System.out.println("\n********************************* ");
				   System.out.println("Soggetto "+stmt.getSubject().toString());
				   System.out.println("predicato "+stmt.getPredicate().toString());
				   System.out.println("Oggetto "+stmt.getObject().toString());
				   System.out.println("\n********************************* ");
				   }
			   if((stmt.getPredicate().toString()).contains("dnc_estrattaTxT_")){
				   System.out.println("\n********************************* ");
				   System.out.println("Soggetto "+stmt.getSubject().toString());
			   System.out.println("predicato "+stmt.getPredicate().toString());
				   System.out.println("Oggetto "+stmt.getObject().toString());
		   System.out.println("\n********************************* ");
			   }
	//	}
			   //aggiungo la diagnosi nn confermate arricchite 
			   System.out.println("dnc_arrichita:: "+dnc_arrichita.getDnc_arricchitaCod());
			   arricchitaNotConfermataList.add(dnc_arrichita);

}*/
	// @GET 
	// @Path("/dexter")
	// @Produces({"application/json","application/xml"})
	// public Response insertDiagnosi(){
	//	 try{
	//		 ft =  new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
	//		 List<RefertoInfo> refertoList = getURIReferto();
	//		 Model model = Parameters.getInstance().getModelRDF();
	//	//	 for(int k=0; k<refertoList.size(); k++){
	//			// String referto = refertoList.get(k).getReferto();
	//			// System.out.println("\nreferto: "+ referto); 
	//			// String refertoURI = refertoList.get(k).getRefertoURI();
	//			 String refertoURI = "http://progetto.ris/resource/p100944/refmed_58";
	//			 System.out.println("\nrefertoURI: "+ refertoURI); 
	//			/* HashMap<String, String> mapDexter = invoceDexter(referto);
	//			 System.out.println("\nmapDexter: "+ mapDexter); */
	//			 String[] arrayICD9 ={"4011", "49120", "42731"};
	//			 String[] arrayValueICD9 ={"Ipertensione essenziale benigna", "Bronchite cronica ostruttiva, senza esacerbazione", "Fibrillazione atriale"};
	//			 for(int i = 0; i<arrayICD9.length; i++){
	//			/* if(mapDexter.size()>0){
	//				 Iterator<String> keySetIterator  = mapDexter.keySet().iterator();
	//				 while(keySetIterator.hasNext()) {*/
	//					// String codIcd9 = (String) keySetIterator.next();
	//					 //String valueIcd9 = mapDexter.get(codIcd9);
	//				     String codIcd9 = arrayICD9[i];
	//				    String valueIcd9 = arrayValueICD9[i];
	//					 System.out.println("\ncodIcd9: "+ codIcd9); 
	//					 System.out.println("\nvalueIcd9: "+ valueIcd9); 
	//
	//					 Resource refertoURI_resource = model.createResource(refertoURI);
	//					 Property diagnosiNnconfArricchitaCod = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiNonConfermataArricchitaCod+i);
	//					 Statement statementDiagnosiNnCofArrichitaCod = model.createStatement(refertoURI_resource,diagnosiNnconfArricchitaCod,codIcd9);
	//					 model.add(statementDiagnosiNnCofArrichitaCod);
	//
	//					 Property diagnosiNnconfArricchitaTXT = model.createProperty(DataUtil.uriRisBase+DataUtil.subUriDiagnosiNonConfermataArricchitaTxT+i);
	//					 Statement statementDiagnosiNnCofArrichitaTXT = model.createStatement(refertoURI_resource,diagnosiNnconfArricchitaTXT,valueIcd9);
	//					 model.add(statementDiagnosiNnCofArrichitaTXT);
	//
	//					 Resource diagnosiNnConf_property = model.createProperty("http://progetto.ris/resource/diagnosiNonConfermata");
	//					 Statement statementTypeArriuchitaCod = model.createStatement(diagnosiNnconfArricchitaCod,RDFS.subPropertyOf ,diagnosiNnConf_property);
	//					 model.add(statementTypeArriuchitaCod);
	//					 //Statement statementTypeArriuchitaValue = model.createStatement(diagnosiNnconfArricchitaTXT,model.getProperty(RDFS.subPropertyOf),diagnosiNnConf_property);
	//					 Statement statementTypeArriuchitaValue = model.createStatement(diagnosiNnconfArricchitaTXT,RDFS.subPropertyOf,diagnosiNnConf_property);
	//					 model.add(statementTypeArriuchitaValue);
	//					 
	//					 log.info("data "+ft.format(dNow) +" >>> query insertNewDiagnosiDexter   " );
	//				 }
	//			// }
	//		// }
	//	 } 
	//	 catch (Exception e){
	//		 System.out.println("Error into method server insertDiagnosi: "+e);
	//	 }
	//	 return Response.status(200).build();
	// }

}
