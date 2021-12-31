<%
/*
 * Created on: 31-ott-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 *
 * Popup per campi ulteriori relativi alla ditta presenta nella lista delle
 * fasi di ricezione in analisi
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>


<jsp:include page="../gare/fasiRicezione/defStepWizardFasiRicezione.jsp" />

<c:choose>
	<c:when test='${not empty param.paginaAttivaWizard}'>
		<c:set var="tmpPaginaAttivaWizard" value="${param.paginaAttivaWizard}" />
	</c:when>
	<c:otherwise>
		<c:set var="tmpPaginaAttivaWizard" value="${paginaAttivaWizard}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${tmpPaginaAttivaWizard eq step2Wizard}">
		<c:set var="idProfiloMaschera" value="FasiRicezioneUlterioriDettagli"/>
	</c:when>
	<c:otherwise>
		<c:set var="idProfiloMaschera" value="FasiGaraUlterioriDettagli"/>
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.garaInversa}'>
		<c:set var="garaInversa" value="${param.garaInversa}" />
	</c:when>
	<c:otherwise>
		<c:set var="garaInversa" value="${garaInversa}" />
	</c:otherwise>
</c:choose>

<div style="width:97%;">

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="${idProfiloMaschera }">
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="corpo">
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>	
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>	
	</gene:redefineInsert>
	
		<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "DITG")}' />
			
		<c:choose>
			<c:when test='${not empty param.stepWizard}'>
				<c:set var="tmpStepWizard" value="${param.stepWizard}" />
			</c:when>
			<c:otherwise>
				<c:set var="tmpStepWizard" value="${stepWizard}" />
			</c:otherwise>
		</c:choose>
		
				
		<gene:formScheda entita="DITG" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopupFasiGara" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupFasiRicezione_Gara">
			<gene:campoScheda campo="CODGAR5" visibile="false" />
			<gene:campoScheda campo="DITTAO"  visibile="false" />
			<gene:campoScheda campo="NGARA5"  visibile="false" />
			<gene:campoScheda campo="AMMGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${tmpStepWizard}" modificabile="false"  />
			<gene:campoScheda campo="MOTIVESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${tmpStepWizard}"  gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoMOTIVESCL"/>
			<gene:campoScheda campo="DETMOTESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${tmpStepWizard}" modificabile="${param.moties ne 98 and param.moties ne 99}" />
			<gene:campoScheda campo="ALTNOT" />
			
			<gene:gruppoCampi idProtezioni="VERIFICREQUI">
				<gene:campoScheda nome="titoloSezioneVerificaRequisiti">
					<td colspan="2"><b>Verifica requisiti</b></td>
				</gene:campoScheda>
				<% // Presentaazione dei dati caricati direttamenti da DB %>
				<gene:campoScheda campo="OGGRICHCC"    entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
	 			<gene:campoScheda campo="NPLETTRICHCC" entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
				<gene:campoScheda campo="DLETTRICHCC"  entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
				<gene:campoScheda campo="DTERMPRESCC"  entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
				<gene:campoScheda campo="NPPRESDOC"    entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
				<gene:campoScheda campo="DPRESCC"      entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
			</gene:gruppoCampi>

			<gene:gruppoCampi idProtezioni="ATAMMESCL">
				<gene:campoScheda nome="titoloSezioneAttoAmmisEsclu">
					<td colspan="2"><b>Comunicazione esclusione</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="NPLETTCOMESCL" entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
				<gene:campoScheda campo="DPLETTCOMESCL" entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
			</gene:gruppoCampi>
			
			<jsp:include page="/WEB-INF/pages/gene/attributi/sezione-attributi-generici.jsp">
				<jsp:param name="entitaParent" value="DITG"/>
			</jsp:include>
			
			<c:if test='${modoAperturaScheda eq "MODIFICA"}' >	
				<gene:fnJavaScriptScheda funzione="settaAnnotazione('#V_DITGAMMIS_MOTIVESCL#')" elencocampi="V_DITGAMMIS_MOTIVESCL" esegui="false"/>
			</c:if>	
			
			<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${tmpPaginaAttivaWizard}" />
			<input type="hidden" name="garaInversa" value="${garaInversa}" />
			
			
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
				<c:choose>
					<c:when test='${modo eq "MODIFICA"}'>
						<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:conferma();">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:window.close();">
					</c:when>
					<c:otherwise>
						<INPUT type="button" class="bottone-azione" value='Esci' title='Esci' onclick="javascript:window.close();">
					</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>
	<gene:javaScript>
	
		var winOpener = window.opener;
						
		var globalOpenerAMMGAR = null;
		
		<c:choose>
			<c:when test='${modo eq "MODIFICA"}' >
				var garaInversaAmmgarBloccato = winOpener.document.getElementById("garaInversaAmmgarBloccato").value;
				if(garaInversaAmmgarBloccato=="true")
					globalOpenerAMMGAR = winOpener.getValue("V_DITGAMMIS_AMMGAR_FITTIZIO_${param.indiceRiga}");
				else
					globalOpenerAMMGAR = winOpener.getValue("V_DITGAMMIS_AMMGAR_${param.indiceRiga}");
			</c:when>
			<c:otherwise>
			 	globalOpenerAMMGAR = winOpener.getValue("V_DITGAMMIS_AMMGAR_FITTIZIO_${param.indiceRiga}");
			</c:otherwise>
		</c:choose>

		var globalOpenerMOTIES = '${param.moties}';
	
		var arrayAmmgar = new Array();
	<c:forEach items="${tabellatoAmmgar}" var="ammGar" varStatus="indice2" >
		arrayAmmgar[${indice2.index}] = new Array(${ammGar.tipoTabellato}, "${ammGar.descTabellato}");
	</c:forEach>
	
		var arrayMotiviEsclusione = new Array();
	<c:forEach items="${listaMotiviEsclusione}" var="motivoEsclusione" varStatus="indice3" >
		arrayMotiviEsclusione[${indice3.index}] = new Array(${motivoEsclusione.tipoTabellato}, "${motivoEsclusione.descTabellato}");
	</c:forEach>
			
		
		function inizializzaPagina(){
			
			//Se la gara è inversa e la ditta non è esclusa nella fase corrente, allora 
			//le sezioni verifica requisiti e comunicazione esclusione sono sempre visibili, 
			//altrimenti rimane la gestione attuale
									
			if(globalOpenerAMMGAR == "" || (globalOpenerAMMGAR != "" && "256".indexOf(globalOpenerAMMGAR) < 0 && globalOpenerMOTIES != "98" && globalOpenerMOTIES != "99")){
				showObj("rowV_DITGAMMIS_MOTIVESCL", false);
				showObj("rowV_DITGAMMIS_DETMOTESCL", false);
				
			}
			
			var gestioneGaraInversaDocAmm=false;
			var amminversa;
			<c:if test="${garaInversa eq 'true'}">
				if(globalOpenerAMMGAR!="2" && globalOpenerAMMGAR!="6")
					gestioneGaraInversaDocAmm=true;
				<c:choose>
					<c:when test='${modo eq "MODIFICA"}' >
						amminversa = winOpener.getValue("DITG_AMMINVERSA_${param.indiceRiga}"); 
					</c:when>
					<c:otherwise>
					 	amminversa = winOpener.getValue("DITG_AMMINVERSA_FITTIZIO_${param.indiceRiga}"); 
					</c:otherwise>
					
				</c:choose>	 
			</c:if>
			if(globalOpenerAMMGAR == "" || (globalOpenerAMMGAR != "" && "256".indexOf(globalOpenerAMMGAR) < 0 && globalOpenerMOTIES != "98" && globalOpenerMOTIES != "99")){
				if(amminversa!=2)
					visualizzaSezioneAttoAmmissioneEsclusione(false);
			}
			
			if(!gestioneGaraInversaDocAmm && (globalOpenerAMMGAR == "" || (globalOpenerAMMGAR != "" && "346".indexOf(globalOpenerAMMGAR) < 0))){
				visualizzaSezioneVerificaRequisiti(false);
			}
			
			<c:if test='${modoAperturaScheda eq "MODIFICA"}' >
			gestioneFasGar_Moties();
			</c:if>
			//var altnot = winOpener.getValue("DITG_ALTNOT_${param.indiceRiga}");
			//setValue("DITG_ALTNOT", altnot);
		}
		
		function visualizzaSezioneAttoAmmissioneEsclusione(visibile){
			showObj("rowtitoloSezioneAttoAmmisEsclu", visibile);
			showObj("rowDITGSTATI_NPLETTCOMESCL", visibile);
			showObj("rowDITGSTATI_DPLETTCOMESCL", visibile);		
		}
		
		function visualizzaSezioneVerificaRequisiti(visibile){
			showObj("rowtitoloSezioneVerificaRequisiti", visibile);
			showObj("rowDITGSTATI_OGGRICHCC", visibile);
			showObj("rowDITGSTATI_NPLETTRICHCC", visibile);
			showObj("rowDITGSTATI_DLETTRICHCC", visibile);
			showObj("rowDITGSTATI_DTERMPRESCC", visibile);
			showObj("rowDITGSTATI_NPPRESDOC", visibile);
			showObj("rowDITGSTATI_DPRESCC", visibile);
		}
		
<c:if test='${modoAperturaScheda eq "MODIFICA"}' >
	
		function conferma(){
			var arrayCampiModificabili = new Array("V_DITGAMMIS_MOTIVESCL","V_DITGAMMIS_DETMOTESCL");

			for(var i=0; i < arrayCampiModificabili.length; i++){
				winOpener.setValue(arrayCampiModificabili[i] + "_${param.indiceRiga}", getValue(arrayCampiModificabili[i]));
			}
			
			document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
			schedaConferma();
			//window.close();
			
			
		}
		
		function gestioneFasGar_Moties(){
			var openerAMMGAR = globalOpenerAMMGAR;
			var openerMOTIES = '${param.moties}';
			var tmp = null;
			if("1345".indexOf(openerAMMGAR) >= 0 || openerAMMGAR == ""){
				setValue("V_DITGAMMIS_AMMGAR", openerAMMGAR);
				setValue("V_DITGAMMIS_DETMOTESCL", "");
				if(new Number(openerMOTIES) < 98 || new Number(openerMOTIES) > 99)
					setValue("V_DITGAMMIS_MOTIVESCL", "");
				if(openerAMMGAR == ""){
					tmp = "";
				} else {
					tmp = arrayAmmgar[new Number(openerAMMGAR)-1][1];
				}
				setValue("V_DITGAMMIS_DETMOTESCL", winOpener.getValue("V_DITGAMMIS_DETMOTESCL_${param.indiceRiga}"));
			}  else if("26".indexOf(openerAMMGAR) >= 0){ //  openerAMMGAR == "2" || openerAMMGAR == "6"){
				
				setValue("V_DITGAMMIS_AMMGAR", openerAMMGAR);
				if(openerMOTIES != "98" || openerMOTIES != "99")
				  setValue("V_DITGAMMIS_MOTIVESCL", openerMOTIES);
				if(openerAMMGAR == ""){
					tmp = "";
				} else {
					tmp = arrayAmmgar[new Number(openerAMMGAR)-1][1];
				}
				setValue("V_DITGAMMIS_DETMOTESCL", winOpener.getValue("V_DITGAMMIS_DETMOTESCL_${param.indiceRiga}"));
			} else {
				setValue("V_DITGAMMIS_AMMGAR", "");
				setValue("V_DITGAMMIS_DETMOTESCL", "");
				setValue("V_DITGAMMIS_MOTIVESCL", "");
				tmp = "";
			}
			if(document.getElementById("V_DITGAMMIS_AMMGARview"))
				document.getElementById("V_DITGAMMIS_AMMGARview").innerHTML = tmp;
			
			if(! ((openerAMMGAR == "2" || openerAMMGAR == "6" ) && (openerMOTIES != "98" && openerMOTIES != "99"))){
				document.getElementById("V_DITGAMMIS_MOTIVESCL").disabled = true;
				document.getElementById("V_DITGAMMIS_DETMOTESCL").disabled = true;
			}
		}

		function settaAnnotazione(moties){
			if(moties == null || moties == ""){
				setValue("V_DITGAMMIS_DETMOTESCL", "");
			} else {
				for(var i=0; i < arrayMotiviEsclusione.length; i++){
					var motivo = arrayMotiviEsclusione[i][0];
					if (motivo == moties) {
						setValue("V_DITGAMMIS_DETMOTESCL",  arrayMotiviEsclusione[i][1]);
						break;
					}
				}
			}
		}
		
	<c:if test='${not empty requestScope.ulterioriCampiDITG}'>
		<c:if test='${not empty requestScope.valoreDITGSTATI_OGGRICHCC}'>
			setValue("DITGSTATI_OGGRICHCC",    ${gene:string4Js(requestScope.valoreDITGSTATI_OGGRICHCC)});
		</c:if>
		<c:if test='${not empty requestScope.valoreDITGSTATI_NPLETTRICHCC}'>
			setValue("DITGSTATI_NPLETTRICHCC", ${gene:string4Js(requestScope.valoreDITGSTATI_NPLETTRICHCC)});
		</c:if>
		<c:if test='${not empty requestScope.valoreDITGSTATI_DLETTRICHCC}'>
			setValue("DITGSTATI_DLETTRICHCC",  "${requestScope.valoreDITGSTATI_DLETTRICHCC}");
		</c:if>
		<c:if test='${not empty requestScope.valoreDITGSTATI_DTERMPRESCC}'>
			setValue("DITGSTATI_DTERMPRESCC",  "${requestScope.valoreDITGSTATI_DTERMPRESCC}");
		</c:if>
		<c:if test='${not empty requestScope.valoreDITGSTATI_DPRESCC}'>
			setValue("DITGSTATI_DPRESCC",      "${requestScope.valoreDITGSTATI_DPRESCC}");
		</c:if>
		<c:if test='${not empty requestScope.valoreDITGSTATI_NPPRESDOC}'>
			setValue("DITGSTATI_NPPRESDOC",      "${requestScope.valoreDITGSTATI_NPPRESDOC}");
		</c:if>
		
		<c:if test='${not empty requestScope.valoreDITGSTATI_NPLETTCOMESCL}'>
			setValue("DITGSTATI_NPLETTCOMESCL", ${gene:string4Js(requestScope.valoreDITGSTATI_NPLETTCOMESCL)});
		</c:if>
		<c:if test='${not empty requestScope.valoreDITGSTATI_DPLETTCOMESCL}'>
			setValue("DITGSTATI_DPLETTCOMESCL", "${requestScope.valoreDITGSTATI_DPLETTCOMESCL}");
		</c:if>
		<c:if test='${not empty requestScope.valoreDITG_ALTNOT}'>
			setValue("DITG_ALTNOT",  ${gene:string4Js(requestScope.valoreDITG_ALTNOT)});
		</c:if>
	</c:if>	
		
</c:if>
			
		inizializzaPagina();
		
		<c:if test='${not empty RISULTATO_SALVATAGGIO}'>
		window.close();
	</c:if>
		
	</gene:javaScript>
</gene:template>
</div>