<%
/*
 * Created on: 10-10-2011
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

<style type="text/css">
	
	TABLE.grigliaDataProt {
	margin: 0;
	PADDING: 0px;
	width: 100%;
	FONT-SIZE: 11px;
	border-collapse: collapse;
	border-left: 1px solid #A0AABA;
	border-top: 1px solid #A0AABA;
	border-right: 1px solid #A0AABA;
}

TABLE.grigliaDataProt TD {
	PADDING: 2px 0px 2px;
	BORDER-BOTTOM: #A0AABA 1px solid;
}

TABLE.grigliaDataProt TD.no-border {
	PADDING: 2px 0px 2px;
	BORDER-BOTTOM: 0px;
}

TABLE.grigliaDataProt TD.etichetta-dato {
	width: 300px;
	HEIGHT: 22px;
	PADDING-RIGHT: 10px;
	BORDER-TOP: #A0AABA 1px solid;
	BACKGROUND-COLOR: #EFEFEF;
	color: #000000;
	TEXT-ALIGN: right;
}


TABLE.grigliaDataProt TD.valore-dato {
	width: 190px;
	PADDING-LEFT: 10px;
	TEXT-ALIGN: left;
	BORDER-LEFT: #A0AABA 1px solid;
}

TABLE.grigliaDataProt TD.valore-dato-numerico {
	width: 190px;
	PADDING-LEFT: 10px;
	TEXT-ALIGN: right;
	BORDER-LEFT: #A0AABA 1px solid;
}

TABLE.grigliaDataProt TD.titolo-valore-dato {
	width: 300px;
	PADDING-LEFT: 10px;
	TEXT-ALIGN: left;
	BORDER-LEFT: #A0AABA 1px solid;
}

TABLE.grigliaDataProt TD.valore-dato A {
	text-decoration: underline;
	color: #000000;
}

TABLE.grigliaDataProt TD.valore-dato A:hover {
	text-decoration: none;
}

</style>

<div style="width:97%;">

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="DitteConcorrentiUlterioriDettagli">
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="corpo">
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>	
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>	
	</gene:redefineInsert>
	
		<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "DITG")}' />
			
		<c:choose>
			<c:when test='${fn:startsWith(gene:getValCampo(param.key, "CODGAR5"), "$")}'>
				<c:set var="isGaraLottoUnico" value="true" />
			</c:when>
			<c:otherwise>
				<c:set var="isGaraLottoUnico" value="false" />
			</c:otherwise>
		</c:choose>

		<c:choose>
			<c:when test='${not empty param.stepWizard}'>
				<c:set var="tmpStepWizard" value="${param.stepWizard}" />
			</c:when>
			<c:otherwise>
				<c:set var="tmpStepWizard" value="${stepWizard}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.paginaAttivaWizard}'>
				<c:set var="tmpPaginaAttivaWizard" value="${param.paginaAttivaWizard}" />
			</c:when>
			<c:otherwise>
				<c:set var="tmpPaginaAttivaWizard" value="${paginaAttivaWizard}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.isGaraElenco}'>
				<c:set var="isGaraElenco" value="${param.isGaraElenco}" />
			</c:when>
			<c:otherwise>
				<c:set var="isGaraElenco" value="${isGaraElenco}" />
			</c:otherwise>
		</c:choose>
		
		<gene:formScheda entita="DITG" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopupFasiGara" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupFasiRicezione_Gara">
			<gene:campoScheda campo="CODGAR5" visibile="false" />
			<gene:campoScheda campo="DITTAO"  visibile="false" />
			<gene:campoScheda campo="NGARA5"  visibile="false" />
			<gene:campoScheda campo="NPROGG" />
			<gene:campoScheda campo="NPROTG" />
			<gene:campoScheda campo="DINVIG" />
			<gene:campoScheda campo="INVOFF" modificabile="false" />
			<gene:campoScheda campo="TIPRIN" />
			<gene:campoScheda campo="DATOFF" />
			<gene:campoScheda campo="DATOFF_FIT" title="Data" modificabile="false" campoFittizio="true" visibile='${modo eq "MODIFICA"}' definizione="T10;;;;G1DATOFF" value="${datiRiga.DITG_DATOFF}"/>
			<gene:campoScheda campo="ORAOFF" />
			<gene:campoScheda campo="ORAOFF_FIT" title="Ora" modificabile="false" campoFittizio="true" visibile='${modo eq "MODIFICA"}' definizione="T6;;;;G1ORAROFF" value="${datiRiga.DITG_ORAOFF}"/>
			<gene:campoScheda campo="NPROFF" />
			<gene:campoScheda campo="NPROFF_FIT" title="N.prot. presentazione offerta" modificabile="false" visibile='${modo eq "MODIFICA"}' campoFittizio="true" definizione="T20;;;;G1NPROFF" value="${datiRiga.DITG_NPROFF}"/>
			<gene:campoScheda campo="DPROFF" visibile="${modo ne 'MODIFICA' }" />
			<c:if test="${ modo eq 'MODIFICA'}">
			<gene:campoScheda campoFittizio="true" addTr="false">
				<td class="etichetta-dato">Data protocollo</td>
				<td class="valore-dato">
					<table id="tabellaDataProtOff" class="grigliaDataProt" style="width: 99%; ">
			</gene:campoScheda>
			<gene:campoScheda addTr="false" campoFittizio="true" title="Data" campo="DATAOFF" definizione="D;0;;DATA_ELDA;"/>
			<gene:campoScheda addTr="false" campoFittizio="true" title="Ora" campo="ORAOFF"  definizione="T8;0;;ORA;"  />
			
			<gene:campoScheda addTr="false">
					<td class="riempimento"></td>
				</table>
				</td>
			</gene:campoScheda>
			<gene:fnJavaScriptScheda funzione='sbiancaOra("#DATAOFF#","ORAOFF")' elencocampi='DATAOFF' esegui="false"/>
			</c:if>			
			
			<gene:campoScheda campo="MEZOFF" />
			<gene:campoScheda campo="MEZOFF_FIT" title="Mezzo" modificabile="false" campoFittizio="true" visibile='${modo eq "MODIFICA"}' definizione="T100;;A1030;;G1MEZOFF" value="${datiRiga.DITG_MEZOFF}"/>
			
			<gene:campoScheda campo="AMMGAR" visibile='${modo eq "MODIFICA" || (modo ne "MODIFICA" && datiRiga.DITG_INVOFF ne 2)}'/>
			<gene:campoScheda campo="MOTIES"  visibile='${modo eq "MODIFICA" || (modo ne "MODIFICA" && datiRiga.DITG_INVOFF ne 2 && datiRiga.DITG_AMMGAR eq 2)}'/>
			<gene:campoScheda campo="ANNOFF"  visibile='${modo eq "MODIFICA" || (modo ne "MODIFICA" && datiRiga.DITG_INVOFF ne 2 && datiRiga.DITG_AMMGAR eq 2)}'/>
			
			<gene:campoScheda campo="ALTNOT" />
			
			<c:if test='${modo eq "MODIFICA"}'>
				<gene:fnJavaScriptScheda funzione='checkDataRicezioneOfferta("#DITG_DATOFF#")' elencocampi='DITG_DATOFF' esegui="false"/>
				<gene:fnJavaScriptScheda funzione='checkOraRicezioneOfferta("#DITG_ORAOFF#")' elencocampi='DITG_ORAOFF' esegui="false"/>		
			</c:if>
		
			<input type="hidden" name="indiceRigaOpener" id="indiceRiga" value="${param.indiceRiga}" />
			<input type="hidden" name="garaLottiOmogenea" id="garaLottiOmogenea" value="" />
			<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${tmpPaginaAttivaWizard}" />
			<input type="hidden" name="isProceduraAggiudicazioneAperta" id="isProceduraAggiudicazioneAperta" value="" />
			<input type="hidden" name="DTEOFF" value="" />
			<input type="hidden" name="OTEOFF" value="" />
			<input type="hidden" name="isGaraElenco" id="isGaraElenco" value="${isGaraElenco }" />
			
			<c:if test='${modoAperturaScheda eq "MODIFICA"}' >	
				<gene:fnJavaScriptScheda funzione="settaAnnotazione('#DITG_MOTIES#')" elencocampi="DITG_MOTIES" esegui="false"/>
				<gene:fnJavaScriptScheda funzione='gestioneVisualizzazioneDettagliEsclusione("#DITG_AMMGAR#")' elencocampi='DITG_AMMGAR' esegui="false"/>
			</c:if>	
			
			
			
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
	
		// Copia dall'opener i seguenti campi hidden: garaLottiOmogenea, WIZARD_PAGINA_ATTIVA
		// e isProceduraAggiudicazioneAperta
		setValue("garaLottiOmogenea", winOpener.getValue("garaLottiOmogenea"));
		//setValue("WIZARD_PAGINA_ATTIVA", winOpener.getValue("WIZARD_PAGINA_ATTIVA"));
		setValue("DTEOFF", winOpener.getValue("DTEOFF"));
		setValue("OTEOFF", winOpener.getValue("OTEOFF"));
		setValue("isProceduraAggiudicazioneAperta", winOpener.getValue("isProceduraAggiudicazioneAperta"));

		var isProceduraAggiudicazioneAperta = winOpener.getValue("isProceduraAggiudicazioneAperta");
		var faseRicezioneAttiva = "${tmpPaginaAttivaWizard}";
		
		var isGaraElenco = winOpener.getValue("isGaraElenco");
		
		
		var arrayMotiviEsclusione = new Array();
	<c:forEach items="${listaMotiviEsclusione}" var="motivoEsclusione" varStatus="indice3" >
		arrayMotiviEsclusione[${indice3.index}] = new Array(${motivoEsclusione.tipoTabellato}, "${motivoEsclusione.descTabellato}");
	</c:forEach>
		
		var arrayCampi = new Array("DITG_NPROGG", "DITG_DINVIG", "DITG_INVOFF", "DITG_NPROTG",
						"DITG_DATOFF","DITG_NPROFF","DITG_ORAOFF","DITG_MEZOFF","DITG_AMMGAR","DITG_MOTIES","DITG_ANNOFF");
		
		<c:choose>
		<c:when test='${modoAperturaScheda eq "MODIFICA"}' >
			<c:if test='${not empty requestScope.ulterioriCampiDITG}' >
				// Set dei valori dei campi precedentemente modificati
				<c:if test='${not empty requestScope.valoreDITG_ALTNOT}'>
					setValue("DITG_ALTNOT",  ${gene:string4Js(requestScope.valoreDITG_ALTNOT)});
				</c:if>
						
			</c:if>
		
		datoffOriginale = winOpener.getValue("DITG_DATOFF_${param.indiceRiga}");
		oraoffOrignale = winOpener.getValue("DITG_ORAOFF_${param.indiceRiga}");		
		
		openerINVOFF = winOpener.getValue("DITG_INVOFF_${param.indiceRiga}");
		ammgarOrignale = winOpener.getValue("DITG_AMMGAR_${param.indiceRiga}");
		
		setValue("DITG_TIPRIN", winOpener.getValue("DITG_TIPRIN_${param.indiceRiga}"));
									
		function inizializzazione(){
				
			// Copia dei campi dall'opener alla popup
			for(var i=0; i < arrayCampi.length; i++){
				if(i == 0 ){
					setValue("DITG_NPROGG", winOpener.getValue("DITG_NUM_PROGG_${param.indiceRiga}"));
				}else{
					setValue(arrayCampi[i], winOpener.getValue(arrayCampi[i] + "_${param.indiceRiga}"));
					if(i>3){
						var nomeCampo = arrayCampi[i];
						nomeCampo = nomeCampo.substring(5) + "_FIT";
						//alert(nomeCampo);
						setValue(nomeCampo, winOpener.getValue(arrayCampi[i] + "_${param.indiceRiga}"));
					}
				}
			}
			
			var visualizza = true;
			var openerAMMGAR = winOpener.getValue("DITG_AMMGAR_${param.indiceRiga}");
			
			if(openerINVOFF == 2){
				document.getElementById("DITG_NPROFF").disabled = true;
				document.getElementById("DATAOFF").disabled = true;
				document.getElementById("ORAOFF").disabled = true;
				document.getElementById("DITG_DATOFF").disabled = true;
				document.getElementById("DITG_ORAOFF").disabled = true;
				document.getElementById("DITG_MEZOFF").disabled = true;
				
				document.getElementById("DITG_INVOFFview").innerHTML = "No";
				visualizza = false;
				showObj("rowDITG_TIPRIN", true);
			}else if(openerINVOFF == "1"){
				document.getElementById("DITG_INVOFFview").innerHTML = "Si";
				setValue("rowDITG_TIPRIN", "");
				showObj("rowDITG_TIPRIN", false);
			}else{
				document.getElementById("DITG_INVOFFview").innerHTML = "";
				setValue("rowDITG_TIPRIN", "");
				showObj("rowDITG_TIPRIN", false);
			}
			
			showObj("rowDITG_NPROFF", visualizza);
			showObj("rowDITG_DATOFF", visualizza);
			showObj("rowDITG_ORAOFF", visualizza);
			showObj("rowDITG_MEZOFF", visualizza);
			
			showObj("rowDITG_AMMGAR", visualizza);
			//showObj("rowDITG_MOTIES", visualizza);
			//showObj("rowDITG_ANNOFF", visualizza);
			if(visualizza)
				gestioneVisualizzazioneDettagliEsclusione(openerAMMGAR);
			
			showObj("rowNPROFF_FIT", !visualizza);
			showObj("rowDATOFF_FIT", !visualizza);
			showObj("rowORAOFF_FIT", !visualizza);
			showObj("rowMEZOFF_FIT", !visualizza);
			
			//document.getElementById("DITG_DATOFF").onchange = checkDataRicezioneOfferta;
			//document.getElementById("DITG_ORAOFF").onchange = checkOraRicezioneOfferta;
		}
		
		inizializzazione();
		
		
		
			
		
		
		function checkDataRicezioneOfferta(data){
			if(getValue("DTEOFF") != ""){
				var ora = getValue("DITG_ORAOFF");
				if(data!= null && data != "" && data!=datoffOriginale ){
					datoffOriginale = data;
					checkDatiRichiestaOfferta(data, ora,getValue("DTEOFF"),getValue("OTEOFF"),"La data inserita e' successiva alla data di termine ricezione offerte");
				}
			}
		}
		
		function checkOraRicezioneOfferta(ora){
			if(getValue("DTEOFF") != ""){
				var data = getValue("DITG_DATOFF");
				if(ora!= null && ora != "" && ora!=oraoffOrignale){
					oraoffOrignale = ora;
					checkDatiRichiestaOfferta(data, ora,getValue("DTEOFF"),getValue("OTEOFF"),"La data inserita e' successiva alla data di termine ricezione offerte");
				}
			}
			
		}
		
		
		function settaAnnotazione(moties){
			if(moties == null || moties == ""){
				setValue("DITG_ANNOFF", "");
			} else {
				for(var i=0; i < arrayMotiviEsclusione.length; i++){
					var motivo = arrayMotiviEsclusione[i][0];
					if (motivo == moties) {
						setValue("DITG_ANNOFF",  arrayMotiviEsclusione[i][1]);
						break;
					}
				}
			}
		}
		
		</c:when>
		<c:otherwise>
				var invoff=getValue("DITG_INVOFF");
				if(invoff!=2)
					showObj("rowDITG_TIPRIN", false);
			</c:otherwise>
		</c:choose>
		function conferma(){
			var data = getValue("DATAOFF");
			var ora = getValue("ORAOFF");
			if((data!=null && data!="") && (ora==null || ora =="" )){
				alert("Non è possibile procedere, deve essere inserita l'ora del protocollo presentazione della domanda di offerta");
				return;
			}
			if((data==null || data=="") && (ora!=null && ora !="" )){
				alert("Non è possibile procedere, non può essere inserita l'ora del protocollo presentazione della domanda di offerta in mancanza della data");
				return;
			}
			var dataOriginale = getOriginalValue("DATAOFF");
			var oraOriginale = getOriginalValue("ORAOFF");
			if(dataOriginale!=data || ora!=oraOriginale){
				var dproff = "";
				if(data!=null && ora!=null && data!="" && ora !="")
					dproff = data + " " + ora + ":00";
				winOpener.setValue("DITG_DPROFF" + "_${param.indiceRiga}", dproff);
				winOpener.setValue("DPROFF_FIT_NASCOSTO" + "_${param.indiceRiga}", dproff);
			}
						
			var arrayCampiModificabili = new Array("DITG_NPROTG", "DITG_DINVIG", "DITG_NPROFF",
							 "DITG_DATOFF", "DITG_ORAOFF", "DITG_MEZOFF", "DITG_AMMGAR", "DITG_MOTIES", "DITG_ANNOFF",
							 "DITG_TIPRIN");

			for(var i=0; i < arrayCampiModificabili.length; i++){
				winOpener.setValue(arrayCampiModificabili[i] + "_${param.indiceRiga}", getValue(arrayCampiModificabili[i]));
				// Copia nel campo di appoggio DITG_NUM_PROGG_<i> dell'opener del numovo valore del progressivo
			}
			
			//if(getOriginalValue("DITG_NPROGG") != getValue("DITG_NPROGG")){
				winOpener.setValue("DITG_NUM_PROGG_${param.indiceRiga}", getValue("DITG_NPROGG"));
				winOpener.document.getElementById("NPROGG_VALUE_${param.indiceRiga}").innerHTML = getValue("DITG_NPROGG");
			//}
			
			
			document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
			schedaConferma();
			//window.close();
		}
		
		function gestioneVisualizzazioneDettagliEsclusione(ammgar){
			var visualizza = false;
			if(ammgar==2  && openerINVOFF!=2){
				visualizza = true;
				
			}else{
				if(ammgarOrignale!=ammgar){
					setValue("DITG_MOTIES", "");
					setValue("DITG_ANNOFF", "");
				}
			}
			showObj("rowDITG_MOTIES", visualizza);
			showObj("rowDITG_ANNOFF", visualizza);
		}
		
		<c:if test='${not empty RISULTATO_SALVATAGGIO}'>
			window.close();
		</c:if>
		
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie")}'>
			redefineLabels();
			redefineTooltips();
			redefineTitles();
		</c:if>
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AutovieArchiflow")}'>
			addHrefs();
		</c:if>
		
		<c:choose>
			<c:when test='${modoAperturaScheda eq "MODIFICA"}' >
				var dproff = winOpener.getValue("DITG_DPROFF_${param.indiceRiga}");
				if(dproff!=null && dproff!=""){
					var splitDproff = dproff.split(" ");
					setValue("DATAOFF",splitDproff[0]);
					setOriginalValue("DATAOFF",splitDproff[0]);
					setValue("ORAOFF",splitDproff[1].substring(0, 5));
					setOriginalValue("ORAOFF",splitDproff[1].substring(0, 5));
				}
			</c:when>
			<c:otherwise>
				var dproff = getValue("DITG_DPROFF");
				if(dproff!=null && dproff!=""){
					var splitDproff = dproff.split(" ");
					document.getElementById("DATAOFFview").innerHTML = splitDproff[0];
					document.getElementById("ORAOFFview").innerHTML = splitDproff[1].substring(0, 5);
				}
			</c:otherwise>
		</c:choose>
		
		function sbiancaOra(data,campo){
			if(data==null || data == "")
				setValue(campo,"");
		}	
	
		<c:if test="${ modo eq 'MODIFICA'}">
		//Nella tabella grigliaDataProt è stato inserito il td con classe='riempimento' che ha il solo
		//scopo di riempire una parte della tabella in modo da fare risultare più piccoli e quindi più
		//vicini i campi con l'ora e la data
		$('table.grigliaDataProt tr td.valore-dato').css('width','200');
		$('table.grigliaDataProt tr td.riempimento').css('width','40%');
		</c:if>
	</gene:javaScript>
</gene:template>
</div>