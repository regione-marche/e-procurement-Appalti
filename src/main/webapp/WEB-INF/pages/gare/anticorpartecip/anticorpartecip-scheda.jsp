<%
/*
 * Created on: 05/09/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="id" value='${gene:getValCampo(key, "ANTICORPARTECIP.ID")}'/>

<c:if test='${modo ne "NUOVO" }'>
	<c:set var="completato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCompletatoFunction", pageContext, key)}' />
</c:if>
<c:if test='${modo eq "NUOVO" }'>
	<c:set var="idAnticorlotti" value='${gene:getValCampo(keyParent, "ANTICORLOTTI.ID")}'/>
</c:if>
<c:choose>
	<c:when test='${not empty param.tipo}'>
		<c:set var="tipo" value="${param.tipo}" />
	</c:when>
	<c:when test='${not empty requestScope.tipo}'>
		<c:set var="tipo" value="${requestScope.tipo}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipo" value="${tipo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipo}'>
		<c:set var="bloccoModifica" value="${param.bloccoModifica}" />
	</c:when>
	<c:otherwise>
		<c:set var="bloccoModifica" value="${bloccoModifica}" />
	</c:otherwise>
</c:choose>

<c:if test='${modo ne "NUOVO" }'>
	<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneAnticorditteFunction", pageContext, id)}'/>
</c:if>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="ANTICORDITTE-scheda">
	<c:choose>
		<c:when test='${modo eq "NUOVO" }'>
			<gene:setString name="titoloMaschera" value='Nuovo partecipante al lotto'/>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${tipo eq 1 }">
					<gene:setString name="titoloMaschera" value='Dettaglio ditta partecipante al lotto'/>
				</c:when>
				<c:otherwise>
					<gene:setString name="titoloMaschera" value='Dettaglio raggruppamento partecipante al lotto'/>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
	
	
	
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="ANTICORPARTECIP" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreANTICORDITTE" >
								
			<gene:campoScheda campo="ID" visibile="false" />
			<gene:campoScheda campo="IDANTICORLOTTI" visibile="false" defaultValue="${idAnticorlotti }"/>
			
			<c:choose>
				<c:when test='${modo eq "NUOVO" }'>
					<gene:campoScheda campo="ID" entita="ANTICORDITTE" where="ANTICORDITTE.IDANTICORPARTECIP=ANTICORPARTECIP.ID" visibile="false" />
					<gene:campoScheda campo="TIPO" obbligatorio="true"/>
					<gene:campoScheda campo="AGGIUDICATARIA" visibile="false" defaultValue="2"/>
					<gene:gruppoCampi >
						<gene:campoScheda nome="IMPSINGOLA">
							<td colspan="2"><b>Impresa singola</b></td>
						</gene:campoScheda>
						<gene:archivio titolo="imprese"
							obbligatorio="false" 
							scollegabile="true"
							lista='gene/impr/impr-listaL190-popup.jsp' 
							scheda="" 
							schedaPopUp="" 
							campi="IMPR.NOMEST;IMPR.CFIMP;IMPR.PIVIMP;IMPR.NAZIMP" 
							functionId="anticor"
							chiave=""
							inseribile="false"
							formName="formArchivioImprese">
							<gene:campoScheda campo="RAGSOC" entita="ANTICORDITTE" where="ANTICORDITTE.IDANTICORPARTECIP=ANTICORPARTECIP.ID"/>
							<gene:campoScheda campo="CF_FIT" campoFittizio="true" definizione="T50" visibile="false"/>
							<gene:campoScheda campo="PIVA_FIT" campoFittizio="true" definizione="T50" visibile="false"/>
							<gene:campoScheda campo="NAZIMP_FIT" campoFittizio="true" definizione="T50" visibile="false"/>
						</gene:archivio>
						<gene:campoScheda campo="AGGIUD_SINGOLA" campoFittizio="true" value="${datiRiga.ANTICORPARTECIP_AGGIUDICATARIA }" definizione="T2;0;;SN;AGGANTICORP" obbligatorio="true"/>
						<gene:campoScheda campo="CODFISC" entita="ANTICORDITTE" where="ANTICORDITTE.IDANTICORPARTECIP=ANTICORPARTECIP.ID"/>
						<gene:campoScheda campo="IDFISCEST" entita="ANTICORDITTE" where="ANTICORDITTE.IDANTICORPARTECIP=ANTICORPARTECIP.ID" />
						
					</gene:gruppoCampi>
					<gene:gruppoCampi >
						<gene:campoScheda nome="RAGGRUPPAMENTO">
							<td colspan="2"><b>Raggruppamento</b></td>
						</gene:campoScheda>
						 <gene:campoScheda campo="RAGSOC"/>
						 <gene:campoScheda campo="AGGIUD_RAGG" campoFittizio="true" value="${datiRiga.ANTICORPARTECIP_AGGIUDICATARIA }" definizione="T2;0;;SN;AGGANTICORP" obbligatorio="true"/>
						 <jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
							<jsp:param name="entita" value='ANTICORDITTE'/>
							<jsp:param name="chiave" value='${id}'/>
							<jsp:param name="nomeAttributoLista" value='partecipanti' />
							<jsp:param name="idProtezioni" value="ANTICORDITTE" />
							<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/anticorditte/partecipante.jsp" />
							<jsp:param name="arrayCampi" value="'ANTICORDITTE_ID_','ANTICORDITTE_IDANTICORPARTECIP_','ANTICORDITTE_RAGSOC_', 'ANTICORDITTE_CODFISC_', 'ANTICORDITTE_IDFISCEST_', 'ANTICORDITTE_RUOLO_'" />
							<jsp:param name="titoloSezione" value="Partecipante" />
							<jsp:param name="titoloNuovaSezione" value="Nuovo partecipante" />
							<jsp:param name="descEntitaVociLink" value="partecipante" />
							<jsp:param name="msgRaggiuntoMax" value="i partecipanti" />
							<jsp:param name="sezioneEliminabile" value="true" />
							<jsp:param name="sezioneInseribile" value="true" />
							<jsp:param name="usaContatoreLista" value="true" />
						</jsp:include>
					</gene:gruppoCampi>
					
					<gene:fnJavaScriptScheda funzione="showSezioni('#ANTICORPARTECIP_TIPO#')" elencocampi="ANTICORPARTECIP_TIPO" esegui="true" />
					
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${tipo eq 1 }">
							<gene:campoScheda campo="ID" entita="ANTICORDITTE" where="ANTICORDITTE.IDANTICORPARTECIP=ANTICORPARTECIP.ID" visibile="false" />
							<gene:campoScheda campo="TIPO" modificabile="false"/>
							<gene:archivio titolo="imprese"
								obbligatorio="false" 
								scollegabile="true"
								lista='gene/impr/impr-listaL190-popup.jsp' 
								scheda="" 
								schedaPopUp="" 
								campi="IMPR.NOMEST;IMPR.CFIMP;IMPR.PIVIMP;IMPR.NAZIMP" 
								functionId="anticor"
								chiave=""
								inseribile="false"
								formName="formArchivioImprese">
								<gene:campoScheda campo="RAGSOC" entita="ANTICORDITTE" where="ANTICORDITTE.IDANTICORPARTECIP=ANTICORPARTECIP.ID"/>
								<gene:campoScheda campo="CF_FIT" campoFittizio="true" definizione="T50" visibile="false"/>
								<gene:campoScheda campo="PIVA_FIT" campoFittizio="true" definizione="T50" visibile="false"/>
								<gene:campoScheda campo="NAZIMP_FIT" campoFittizio="true" definizione="T50" visibile="false"/>
							</gene:archivio>
							<gene:campoScheda campo="AGGIUDICATARIA" obbligatorio="true"/>
							<gene:campoScheda campo="CODFISC" entita="ANTICORDITTE" where="ANTICORDITTE.IDANTICORPARTECIP=ANTICORPARTECIP.ID"/>
							<gene:campoScheda campo="IDFISCEST" entita="ANTICORDITTE" where="ANTICORDITTE.IDANTICORPARTECIP=ANTICORPARTECIP.ID" />
						</c:when>
						<c:otherwise>
							<gene:campoScheda campo="TIPO" modificabile="false"/>
							<gene:campoScheda campo="RAGSOC" />
							<gene:campoScheda campo="AGGIUDICATARIA" obbligatorio="true"/>
							<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
								<jsp:param name="entita" value='ANTICORDITTE'/>
								<jsp:param name="chiave" value='${id}'/>
								<jsp:param name="nomeAttributoLista" value='partecipanti' />
								<jsp:param name="idProtezioni" value="ANTICORDITTE" />
								<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/anticorditte/partecipante.jsp" />
								<jsp:param name="arrayCampi" value="'ANTICORDITTE_ID_','ANTICORDITTE_IDANTICORPARTECIP_','ANTICORDITTE_RAGSOC_', 'ANTICORDITTE_CODFISC_', 'ANTICORDITTE_IDFISCEST_', 'ANTICORDITTE_RUOLO_'" />
								<jsp:param name="titoloSezione" value="Partecipante" />
								<jsp:param name="titoloNuovaSezione" value="Nuovo partecipante" />
								<jsp:param name="descEntitaVociLink" value="partecipante" />
								<jsp:param name="msgRaggiuntoMax" value="i partecipanti" />
								<jsp:param name="usaContatoreLista" value="true" />
							</jsp:include>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>					
			
					
			<input type="hidden" name="tipo" id="tipo" value="${tipo}"/>
			<input type="hidden" name="bloccoModifica" id="bloccoModifica" value="${bloccoModifica}"/>
			
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
			
			<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
			<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
			<c:if test="${bloccoModifica eq true or completato eq 1}">
				<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
				<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
			</c:if>
			
		</gene:formScheda>	

	</gene:redefineInsert>
	<gene:javaScript>
		
		
		var schedaConfermaDefault = schedaConferma;
		var schedaConferma = schedaConfermaCustom;
		function schedaConfermaCustom(){
			<c:choose>
				<c:when test='${modo eq "NUOVO" }'>
					var tipo = getValue("ANTICORPARTECIP_TIPO");
					if(tipo==1){
						var codfisc=getValue("ANTICORDITTE_CODFISC");
						var esitoControllo=checkCodFis(codfisc);
						if(!esitoControllo){
							if(!confirm("Il codice fiscale inserito non e' valido.\nProcedere ugualmente?"))
							return;
						}
						var idfisc=getValue("ANTICORDITTE_IDFISCEST");
						if(codfisc!=null && codfisc!="" && idfisc!=null && idfisc!=""){
							clearMsg();
							var msg="Codice fiscale e Id fiscale estero non posso essere valorizzati entrambi"; 
							outMsg(msg, "ERR");
							onOffMsg();
							return;
						}
						var aggiud = getValue("AGGIUD_SINGOLA");
						setValue("ANTICORPARTECIP_AGGIUDICATARIA",aggiud);
					}else if(tipo==2){
						for(var i=1; i <= maxIdANTICORDITTEVisualizzabile ; i++){
							var codfisc=getValue("ANTICORDITTE_CODFISC_" + i);
							var esitoControllo=checkCodFis(codfisc);
							if(!esitoControllo){
								var msg="Il codice fiscale '" + codfisc + "'";
								var ragsoc=getValue("ANTICORDITTE_RAGSOC_" + i);
								if(ragsoc!=null && ragsoc!="")
									msg+=" del partecipante " + ragsoc;
								msg+=" non e' valido.\nProcedere ugualmente?"; 	
								if(!confirm(msg))
								return;
							}
							var idfisc=getValue("ANTICORDITTE_IDFISCEST_" + i);
							if(codfisc!=null && codfisc!="" && idfisc!=null && idfisc!=""){
								clearMsg();
								var msg="Codice fiscale e Id fiscale estero non posso essere valorizzati entrambi"; 
								outMsg(msg, "ERR");
								onOffMsg();
								return;
							}
						}
						var aggiud = getValue("AGGIUD_RAGG");
						setValue("ANTICORPARTECIP_AGGIUDICATARIA",aggiud);
					}
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${tipo eq 1 }">
							var codfisc=getValue("ANTICORDITTE_CODFISC");
							var esitoControllo=checkCodFis(codfisc);
							if(!esitoControllo){
								if(!confirm("Il codice fiscale inserito non e' valido.\nProcedere ugualmente?"))
								return;
							}
							var idfisc=getValue("ANTICORDITTE_IDFISCEST");
							if(codfisc!=null && codfisc!="" && idfisc!=null && idfisc!=""){
								clearMsg();
								var msg="Codice fiscale e Id fiscale estero non posso essere valorizzati entrambi"; 
								outMsg(msg, "ERR");
								onOffMsg();
								return;
							}
						</c:when>
						<c:otherwise>
							for(var i=1; i <= maxIdANTICORDITTEVisualizzabile ; i++){
								var codfisc=getValue("ANTICORDITTE_CODFISC_" + i);
								var esitoControllo=checkCodFis(codfisc);
								if(!esitoControllo){
									var msg="Il codice fiscale '" + codfisc + "'";
									var ragsoc=getValue("ANTICORDITTE_RAGSOC_" + i);
									if(ragsoc!=null && ragsoc!="")
										msg+=" del partecipante " + ragsoc;
									msg+=" non e' valido.\nProcedere ugualmente?"; 	
									if(!confirm(msg))
									return;
								}
								var idfisc=getValue("ANTICORDITTE_IDFISCEST_" + i);
								if(codfisc!=null && codfisc!="" && idfisc!=null && idfisc!=""){
									clearMsg();
									var msg="Codice fiscale e Id fiscale estero non posso essere valorizzati entrambi"; 
									outMsg(msg, "ERR");
									onOffMsg();
									return;
								}
							}
							
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
			
			
						
			schedaConfermaDefault();
		}
		
		<c:if test='${modo eq "NUOVO" }'>
			function showSezioni(tipo){
				if(tipo == null || tipo == '' || tipo ==1){
					showObj("rowIMPSINGOLA",true);
					showObj("rowANTICORDITTE_RAGSOC", true);
					showObj("rowANTICORDITTE_CODFISC", true);
					showObj("rowANTICORDITTE_IDFISCEST", true);
					showObj("rowRAGGRUPPAMENTO",false);
					showObj("rowANTICORPARTECIP_RAGSOC",false);
					showObj("rowAGGIUD_SINGOLA",true);
					showObj("rowAGGIUD_RAGG",false);
					setValue("ANTICORPARTECIP_RAGSOC","");
					showObj("rowLinkAddANTICORDITTE",false);
					for(var i=1; i <= maxIdANTICORDITTEVisualizzabile ; i++){
						showObj("rowtitoloANTICORDITTE_" + i,false);
						showObj("rowANTICORDITTE_RAGSOC_" + i,false);
						setValue("ANTICORDITTE_RAGSOC_" + i,"");
						showObj("rowANTICORDITTE_CODFISC_" + i,false);
						setValue("ANTICORDITTE_CODFISC_" + i,"");
						showObj("rowANTICORDITTE_IDFISCEST_" + i,false);
						setValue("ANTICORDITTE_IDFISCEST_" + i,"");
						showObj("rowANTICORDITTE_RUOLO_" + i,false);
						setValue("ANTICORDITTE_RUOLO_" + i,"");
					}
				}else{
					showObj("rowIMPSINGOLA",false);
					showObj("rowANTICORDITTE_RAGSOC", false);
					setValue("ANTICORDITTE_RAGSOC","");
					showObj("rowANTICORDITTE_CODFISC", false);
					setValue("ANTICORDITTE_CODFISC","");
					showObj("rowANTICORDITTE_IDFISCEST", false);
					setValue("ANTICORDITTE_IDFISCEST","");
					showObj("rowRAGGRUPPAMENTO",true);
					showObj("rowAGGIUD_SINGOLA",false);
					showObj("rowAGGIUD_RAGG",true);
					var vis= true;
					var ragsoc;
					showObj("rowANTICORPARTECIP_RAGSOC",true);
					showObj("rowLinkAddANTICORDITTE",true);
					for(var i=1; i <= maxIdANTICORDITTEVisualizzabile ; i++){
						ragsoc=getValue("ANTICORDITTE_RAGSOC_" + i);
						if(i==1 || (i>1 && ragsoc!=null && ragsoc!=''))
							vis= true;
						else
							vis= false;
						showObj("rowtitoloANTICORDITTE_" + i,vis);
						showObj("rowANTICORDITTE_RAGSOC_" + i,vis);
						showObj("rowANTICORDITTE_CODFISC_" + i,vis);
						showObj("rowANTICORDITTE_IDFISCEST_" + i,vis);
						showObj("rowANTICORDITTE_RUOLO_" + i,vis);
					}
				}
			}
			
			if(document.getElementById("ANTICORDITTE_RAGSOC")!= null)
				document.getElementById("ANTICORDITTE_RAGSOC").onchange = modificaCampoArchivio;
				
			function modificaCampoArchivio(){
				var campo =this.id;
				var valore = this.value;
				activeArchivioForm = "formArchivioImprese";
				
				
				if(valore!= null && valore !=""){
					eval("document." + activeArchivioForm +".archValueCampoChanged").value = valore;
					eval("document." + activeArchivioForm +".metodo").value = "lista";
					eval("document." + activeArchivioForm +".archCampoChanged").value = "IMPR.NOMEST";
					eval("document." + activeArchivioForm +".archTipoCampoChanged").value = "T";
					getArchivio(activeArchivioForm).submit(true);
				}else {
					getArchivio(activeArchivioForm).sbiancaCampi(0);
					return;
				}
				
				
				
			}
		</c:if>
	</gene:javaScript>
</gene:template>