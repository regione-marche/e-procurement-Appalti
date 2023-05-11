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

<% // Validazione parametri tramite regex %>
<c:if test='${not empty param.indiceRiga and gene:matches(param.indiceRiga, "^-?[0-9]+$", true)}' />
<c:if test='${not empty param.isGaraCatalogo and gene:matches(param.isGaraCatalogo, "^true|false$", true)}' />
<c:if test='${not empty param.isGaraElenco and gene:matches(param.isGaraElenco, "^true|false$", true)}' />
<c:if test='${not empty param.paginaAttivaWizard and gene:matches(param.paginaAttivaWizard, "^-?[0-9]+$", true)}' />
<c:if test='${not empty param.stepWizard and gene:matches(param.stepWizard, "^-?[0-9]+$", true)}' />

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

<jsp:include page="../gare/fasiRicezione/defStepWizardFasiRicezione.jsp" />

<div style="width:97%;">

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="FasiRicezioneUlterioriDettagli">
	
	
	

	
	
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
		
		<c:choose>
			<c:when test='${not empty param.isGaraCatalogo}'>
				<c:set var="isGaraCatalogo" value="${param.isGaraCatalogo}" />
			</c:when>
			<c:otherwise>
				<c:set var="isGaraCatalogo" value="${isGaraCatalogo}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.garaTelematica}'>
				<c:set var="garaTelematica" value="${param.garaTelematica}" />
			</c:when>
			<c:otherwise>
				<c:set var="garaTelematica" value="${garaTelematica}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.tipologiaElenco}'>
				<c:set var="tipologiaElenco" value="${param.tipologiaElenco}" />
			</c:when>
			<c:otherwise>
				<c:set var="tipologiaElenco" value="${tipologiaElenco}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.giorniValidita}'>
				<c:set var="giorniValidita" value="${param.giorniValidita}" />
			</c:when>
			<c:otherwise>
				<c:set var="giorniValidita" value="${giorniValidita}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.tipoRinnovo}'>
				<c:set var="tipoRinnovo" value="${param.tipoRinnovo}" />
			</c:when>
			<c:otherwise>
				<c:set var="tipoRinnovo" value="${tipoRinnovo}" />
			</c:otherwise>
		</c:choose>
		
		<c:if test="${(isGaraElenco eq true  || isGaraCatalogo eq true) && tmpPaginaAttivaWizard eq step3Wizard}">
			<c:set var="tipoalgo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTIPOALGOFunction", pageContext, gene:getValCampo(key,"NGARA5"))}'/>
		</c:if>
		
		<gene:formScheda entita="DITG" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopupFasiGara" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupFasiRicezione_Gara">
			<gene:campoScheda campo="CODGAR5" visibile="false" />
			<gene:campoScheda campo="DITTAO"  visibile="false" />
			<gene:campoScheda campo="NGARA5"  visibile="false" />

			<gene:campoScheda campo="NPROGG" visibile="${tmpPaginaAttivaWizard ne step5Wizard}"/>
			<gene:campoScheda campo="NUMORDPL" visibile="${tmpPaginaAttivaWizard eq step5Wizard}"/>
			<gene:campoScheda campo="NOMIMO" visibile="false" />
			<c:choose>
				<c:when test="${isGaraElenco eq true || isGaraCatalogo eq true}">
					<c:set var="tipoDomanda" value="iscrizione"/>
				</c:when>
				<c:otherwise>
		 			<c:set var="tipoDomanda" value="partecipazione"/>
				</c:otherwise>
			</c:choose>
			<gene:campoScheda campo="DRICIND" title="Data presentazione domanda ${tipoDomanda }" visibile="${tmpPaginaAttivaWizard eq step1Wizard}" modificabile="${garaTelematica ne 'true' }"/>
			<gene:campoScheda campo="ORADOM" visibile="${tmpPaginaAttivaWizard eq step1Wizard}" modificabile="${garaTelematica ne 'true' }"/>
 			<gene:campoScheda campo="NPRDOM" visibile="${tmpPaginaAttivaWizard eq step1Wizard or tmpPaginaAttivaWizard eq step2Wizard}" title="${gene:if(tmpPaginaAttivaWizard eq step1Wizard,'N.protocollo','N.protocollo presentazione domanda partecipazione')}" />
			<gene:campoScheda campo="DPRDOM" visibile="${modo ne 'MODIFICA' and (tmpPaginaAttivaWizard eq step1Wizard or tmpPaginaAttivaWizard eq step2Wizard)}" />
			<c:if test="${(tmpPaginaAttivaWizard eq step1Wizard or tmpPaginaAttivaWizard eq step2Wizard) and modo eq 'MODIFICA'}">
			<gene:campoScheda campoFittizio="true" addTr="false">
				<td class="etichetta-dato">Data protocollo</td>
				<td class="valore-dato">
					<table id="tabellaDataProtPart" class="grigliaDataProt" style="width: 99%; ">
			</gene:campoScheda>
			<gene:campoScheda addTr="false" campoFittizio="true" title="Data" campo="DATAPART" definizione="D;0;;DATA_ELDA;"/>
			<gene:campoScheda addTr="false" campoFittizio="true" title="Ora" campo="ORAPART"  definizione="T8;0;;ORA;"  />
			
			<gene:campoScheda addTr="false">
					<td class="riempimento"></td>
				</table>
				</td>
			</gene:campoScheda>
			<gene:fnJavaScriptScheda funzione='sbiancaOra("#DATAPART#","ORAPART")' elencocampi='DATAPART' esegui="false"/>
			</c:if>
			
			<gene:campoScheda campo="MEZDOM" visibile="${tmpPaginaAttivaWizard eq step1Wizard}" modificabile="${garaTelematica ne 'true' }"/>
			<gene:campoScheda campo="PLIDOM" visibile="${tmpPaginaAttivaWizard eq step1Wizard and garaTelematica ne 'true'}"/>
			<gene:campoScheda campo="NOTPDOM" visibile="${tmpPaginaAttivaWizard eq step1Wizard and garaTelematica ne 'true'}"/>
			
			<c:if test='${modo eq "MODIFICA" && tmpPaginaAttivaWizard eq step1Wizard and garaTelematica ne "true"}'>
				<gene:fnJavaScriptScheda funzione='checkDataRichiestaDomanda("#DITG_DRICIND#")' elencocampi='DITG_DRICIND' esegui="false"/>
				<gene:fnJavaScriptScheda funzione='checkOraRichiestaDomanda("#DITG_ORADOM#")' elencocampi='DITG_ORADOM' esegui="false"/>		
			</c:if>
			
			<gene:campoScheda campo="INVGAR" modificabile="false" visibile="${tmpPaginaAttivaWizard eq step3Wizard && (isGaraElenco ne true && isGaraCatalogo ne true)}"/>
			<gene:campoScheda campo="NPROTG" visibile="${tmpPaginaAttivaWizard eq step3Wizard && (isGaraElenco ne true && isGaraCatalogo ne true)}"/>
			<gene:campoScheda campo="DINVIG" visibile="${tmpPaginaAttivaWizard eq step3Wizard && (isGaraElenco ne true && isGaraCatalogo ne true)}"/>
			<gene:campoScheda campo="INVOFF" modificabile="false" visibile="${tmpPaginaAttivaWizard eq step5Wizard}"/>
 			<gene:campoScheda campo="TIPRIN" visibile="${tmpPaginaAttivaWizard eq step5Wizard }"/>
			<gene:campoScheda campo="MOTRINUNCIA" modificabile="false" visibile="${tmpPaginaAttivaWizard eq step5Wizard and datiRiga.DITG_INVOFF eq '2' and garaTelematica eq 'true'}"/>
			<gene:campoScheda campo="DATRINUNCIA" modificabile="false" visibile="${tmpPaginaAttivaWizard eq step5Wizard and datiRiga.DITG_INVOFF eq '2' and garaTelematica eq 'true'}"/>
 			<gene:campoScheda campo="DATOFF" visibile="${tmpPaginaAttivaWizard eq step5Wizard}" modificabile="${garaTelematica ne 'true' }"/>
			<gene:campoScheda campo="ORAOFF" visibile="${tmpPaginaAttivaWizard eq step5Wizard}" modificabile="${garaTelematica ne 'true' }"/>
			<gene:campoScheda campo="NPROFF" visibile="${tmpPaginaAttivaWizard eq step5Wizard}"/>
			<gene:campoScheda campo="DPROFF" visibile="${modo ne 'MODIFICA' and tmpPaginaAttivaWizard eq step5Wizard}" />
			<c:if test="${tmpPaginaAttivaWizard eq step5Wizard and  modo eq 'MODIFICA'}">
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
			<gene:campoScheda campo="MEZOFF" visibile="${tmpPaginaAttivaWizard eq step5Wizard}" />
			<gene:campoScheda campo="PLIOFF" visibile="${tmpPaginaAttivaWizard eq step5Wizard and garaTelematica ne 'true'}" />
			<gene:campoScheda campo="NOTPOFF" visibile="${tmpPaginaAttivaWizard eq step5Wizard and garaTelematica ne 'true'}" />
			
            <c:if test='${modo eq "MODIFICA" && tmpPaginaAttivaWizard eq step5Wizard}'>
				<gene:fnJavaScriptScheda funzione='checkDataRicezioneOfferta("#DITG_DATOFF#")' elencocampi='DITG_DATOFF' esegui="false"/>
				<gene:fnJavaScriptScheda funzione='checkOraRicezioneOfferta("#DITG_ORAOFF#")' elencocampi='DITG_ORAOFF' esegui="false"/>		
			</c:if>

			<c:set var="condizioniVisibilita" value="${isGaraElenco || isGaraCatalogo || (tmpPaginaAttivaWizard ne step1Wizard and tmpPaginaAttivaWizard ne step3Wizard and tmpPaginaAttivaWizard ne step5Wizard and isGaraElenco ne true and isGaraCatalogo ne true)}"/>
			
			<gene:campoScheda campo="AMMGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${tmpStepWizard}" modificabile="false" visibile="${condizioniVisibilita}" />
			
			<gene:campoScheda campo="MOTIVESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${tmpStepWizard}" visibile="${condizioniVisibilita}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoMOTIVESCL"/>
			<gene:campoScheda campo="DETMOTESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${tmpStepWizard}" modificabile="${param.moties ne 98 and param.moties ne 99}" visibile="${condizioniVisibilita}"/>
			
			<gene:campoScheda campo="ESTIMP" title="Sorteggiata per verifica documenti?" visibile="${(isGaraElenco eq true || isGaraCatalogo eq true) && tmpPaginaAttivaWizard eq step2Wizard}"/>
			<gene:campoScheda campo="DSORTEV"  visibile="${(isGaraElenco eq true || isGaraCatalogo eq true) && tmpPaginaAttivaWizard eq step2Wizard}"/>
			<gene:campoScheda campo="INOSSERV" visibile="${(isGaraElenco eq true || isGaraCatalogo eq true) && (tmpPaginaAttivaWizard eq step2Wizard || tmpPaginaAttivaWizard eq step3Wizard)}"/>
			<c:if test="${(isGaraElenco eq true || isGaraCatalogo eq true)  && (tmpPaginaAttivaWizard eq step2Wizard || tmpPaginaAttivaWizard eq step3Wizard)}">
				<gene:campoScheda campo="COORDSIC" visibile="false" entita="GAREALBO" where="GAREALBO.NGARA=DITG.NGARA5"/>
				<gene:campoScheda campo="COORDSIC" visibile="${datiRiga.GAREALBO_COORDSIC eq '1'}"/>

				<gene:campoScheda campo="REQTORRE" visibile="false" entita="GAREALBO" where="GAREALBO.NGARA=DITG.NGARA5"/>
				<gene:campoScheda campo="REQTORRE" visibile="${datiRiga.GAREALBO_REQTORRE eq '1'}"/>
			</c:if>
			<gene:campoScheda campo="DATFS10" title="Data acquisizione domanda di partecipazione" entita='DITGEVENTI' where="DITGEVENTI.NGARA=DITG.NGARA5 and DITGEVENTI.DITTAO=DITG.DITTAO and DITGEVENTI.CODGAR=DITG.CODGAR5" visibile="${tmpPaginaAttivaWizard eq step1Wizard and garaTelematica eq 'true'}" modificabile="false"/>
			<gene:campoScheda campo="DATFS10A" title="Data apertura busta prequalifica" entita='DITGEVENTI' where="DITGEVENTI.NGARA=DITG.NGARA5 and DITGEVENTI.DITTAO=DITG.DITTAO and DITGEVENTI.CODGAR=DITG.CODGAR5" visibile="${tmpPaginaAttivaWizard eq step2Wizard and garaTelematica eq 'true'}" modificabile="false"/>
			<gene:campoScheda campo="DATFS11" title="Data acquisizione offerta" entita='DITGEVENTI' where="DITGEVENTI.NGARA=DITG.NGARA5 and DITGEVENTI.DITTAO=DITG.DITTAO and DITGEVENTI.CODGAR=DITG.CODGAR5 and DITG.INVOFF<>'2'" visibile="${tmpPaginaAttivaWizard eq step5Wizard and datiRiga.DITG_INVOFF ne '2' and garaTelematica eq 'true'}" modificabile="false"/>
			<gene:campoScheda campo="DATFS14" title="Data acquisizione rinuncia" entita='DITGEVENTI' where="DITGEVENTI.NGARA=DITG.NGARA5 and DITGEVENTI.DITTAO=DITG.DITTAO and DITGEVENTI.CODGAR=DITG.CODGAR5 and DITG.INVOFF='2'" visibile="${tmpPaginaAttivaWizard eq step5Wizard and datiRiga.DITG_INVOFF eq '2' and garaTelematica eq 'true'}" modificabile="false"/>
			<gene:campoScheda campo="ALTNOT" />
			<c:if test="${(isGaraElenco eq true || isGaraCatalogo eq true) && tmpPaginaAttivaWizard eq step2Wizard}">
				<gene:campoScheda campo="ESTIMP_FIT" campoFittizio="true" visibile="false" value="${datiRiga.DITG_ESTIMP}" definizione="T2"/>
				<gene:fnJavaScriptScheda funzione="showSezioneVerificaRequisiti('#DITG_ESTIMP#')" elencocampi="DITG_ESTIMP" esegui="false"/>
				<gene:fnJavaScriptScheda funzione="showDataSorteggio('#DITG_ESTIMP#')" elencocampi="DITG_ESTIMP" esegui="true"/>
			</c:if>
			<c:if test="${(isGaraElenco eq true || isGaraCatalogo eq true) && tmpPaginaAttivaWizard eq step3Wizard && (tipoalgo eq 2 || tipoalgo eq 6 || tipoalgo eq 7 || tipoalgo eq 8 || tipoalgo eq 9 || tipoalgo eq 10 || tipoalgo eq 13)}">
				<gene:campoScheda>
					<td colspan="2"><b>Penalità</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="ALTPEN" entita="ISCRIZCAT" where="ISCRIZCAT.CODGAR=DITG.CODGAR5 and ISCRIZCAT.NGARA=DITG.NGARA5 and ISCRIZCAT.CODIMP=DITG.DITTAO and ISCRIZCAT.CODCAT='0' and ISCRIZCAT.TIPCAT=1"/>
				<gene:campoScheda campo="NOTPEN" entita="ISCRIZCAT" where="ISCRIZCAT.CODGAR=DITG.CODGAR5 and ISCRIZCAT.NGARA=DITG.NGARA5 and ISCRIZCAT.CODIMP=DITG.DITTAO and ISCRIZCAT.CODCAT='0' and ISCRIZCAT.TIPCAT=1"/>
				<gene:campoScheda campo="ULTNOT" entita="ISCRIZCAT" where="ISCRIZCAT.CODGAR=DITG.CODGAR5 and ISCRIZCAT.NGARA=DITG.NGARA5 and ISCRIZCAT.CODIMP=DITG.DITTAO and ISCRIZCAT.CODCAT='0' and ISCRIZCAT.TIPCAT=1"/>
				
			</c:if>
			
			
		<c:if test='${tmpPaginaAttivaWizard ne step3Wizard}'>
			<gene:gruppoCampi idProtezioni="VERIFICREQUI">
				<gene:campoScheda nome="titoloSezioneVerificaRequisiti">
					<td colspan="2"><b>Verifica requisiti</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="OGGRICHCC" entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
 				<gene:campoScheda campo="NPLETTRICHCC" entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
				<gene:campoScheda campo="DLETTRICHCC" entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
				<gene:campoScheda campo="DTERMPRESCC" entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
				<gene:campoScheda campo="NPPRESDOC"    entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
				<gene:campoScheda campo="DPRESCC" entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
			</gene:gruppoCampi>
			<gene:gruppoCampi idProtezioni="ATAMMESCL">
				<gene:campoScheda nome="titoloSezioneAttoAmmisEsclu">
					<td colspan="2"><b>Comunicazione esclusione</b></td>
				</gene:campoScheda>
		 		<gene:campoScheda campo="NPLETTCOMESCL" entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
				<gene:campoScheda campo="DPLETTCOMESCL" entita="DITGSTATI" where="DITGSTATI.CODGAR=DITG.CODGAR5 and DITGSTATI.NGARA=DITG.NGARA5 and DITGSTATI.DITTAO=DITG.DITTAO and DITGSTATI.FASGAR=${tmpStepWizard}" />
			</gene:gruppoCampi>
		</c:if>
		<c:if test='${tmpPaginaAttivaWizard eq step2Wizard and (isGaraElenco eq true || isGaraCatalogo eq true)}'>
			<gene:gruppoCampi idProtezioni="SOSPENSIONE">
				<gene:campoScheda nome="titoloSezioneSospensione">
					<td colspan="2"><b>Sospensione</b></td>
				</gene:campoScheda>
		 		<gene:campoScheda campo="DSOSPE" />
			</gene:gruppoCampi>
			<gene:gruppoCampi idProtezioni="REVOCA">
				<gene:campoScheda nome="titoloSezioneRevoca">
					<td colspan="2"><b>Revoca o rinuncia iscrizione</b></td>
				</gene:campoScheda>
		 		<gene:campoScheda campo="DREVOCA" />
			</gene:gruppoCampi>
			<c:if test="${tipologiaElenco ne '3' and giorniValidita > 0 }">
				<gene:gruppoCampi idProtezioni="RINNOVO">
					<gene:campoScheda nome="titoloSezioneRinnovo">
						<td colspan="2"><b>Rinnovo iscrizione</b></td>
					</gene:campoScheda>
					<gene:campoScheda campo="DRICRIN" />
			 		<gene:campoScheda campo="DSCAD" />
			 		<gene:campoScheda campo="STRIN" />
				</gene:gruppoCampi>
			</c:if>
			
		</c:if>	
			
		
		
		<%/*Introduzione del generatore attributi nel dettaglio ditta partecipante alla gara */ %>
		<jsp:include page="/WEB-INF/pages/gene/attributi/sezione-attributi-generici.jsp">
			<jsp:param name="entitaParent" value="DITG"/>
		</jsp:include>
		<c:if test='${modoAperturaScheda eq "MODIFICA"}' >	
			<gene:fnJavaScriptScheda funzione="settaAnnotazione('#V_DITGAMMIS_MOTIVESCL#')" elencocampi="V_DITGAMMIS_MOTIVESCL" esegui="false"/>
		</c:if>	
			<input type="hidden" name="indiceRigaOpener" id="indiceRiga" value="${param.indiceRiga}" />
			<input type="hidden" name="garaLottiOmogenea" id="garaLottiOmogenea" value="" />
			<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${tmpPaginaAttivaWizard}" />
			<input type="hidden" name="isProceduraAggiudicazioneAperta" id="isProceduraAggiudicazioneAperta" value="" />
			<input type="hidden" name="DTEOFF" value="" />
			<input type="hidden" name="OTEOFF" value="" />
			<input type="hidden" name="DTEPAR" value="" />
			<input type="hidden" name="OTEPAR" value="" />
			<input type="hidden" name="isGaraElenco" id="isGaraElenco" value="${isGaraElenco }" />
			<input type="hidden" name="isProceduraNegoziata" id="isProceduraNegoziata" value="" />
			<input type="hidden" name="isGaraCatalogo" id="isGaraCatalogo" value="${isGaraCatalogo }" />
			<input type="hidden" name="garaTelematica" id="garaTelematica" value="${garaTelematica }" />
			<input type="hidden" name="tipologiaElenco" id="tipologiaElenco" value="${tipologiaElenco }" />
			<input type="hidden" name="giorniValidita" id="giorniValidita" value="${giorniValidita }" />
			<input type="hidden" name="tipoRinnovo" id="tipoRinnovo" value="${tipoRinnovo }" />
			
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
		setValue("DTEPAR", winOpener.getValue("DTEPAR"));
		setValue("OTEPAR", winOpener.getValue("OTEPAR"));
		setValue("isProceduraAggiudicazioneAperta", winOpener.getValue("isProceduraAggiudicazioneAperta"));
		setValue("isProceduraNegoziata", winOpener.getValue("isProceduraNegoziata"));

		var isProceduraAggiudicazioneAperta = winOpener.getValue("isProceduraAggiudicazioneAperta");
		var faseRicezioneAttiva = "${tmpPaginaAttivaWizard}";
		
		var isProceduraNegoziata = winOpener.getValue("isProceduraNegoziata");
		
		var isGaraElenco = winOpener.getValue("isGaraElenco");
		var isGaraCatalogo = winOpener.getValue("isGaraCatalogo");
				
		var globalOpenerAMMGAR = null;
		var abilitaz = null;
		var dscad = null;
<c:choose>
	<c:when test='${modo eq "MODIFICA"}' >
		if(winOpener.document.getElementById("V_DITGAMMIS_AMMGAR_${param.indiceRiga}").disabled){
			globalOpenerAMMGAR = "${datiRiga.V_DITGAMMIS_AMMGAR}";
		} else {
			globalOpenerAMMGAR = winOpener.getValue("V_DITGAMMIS_AMMGAR_${param.indiceRiga}");
		}
		abilitaz = winOpener.getValue("DITG_ABILITAZ_${param.indiceRiga}");
		dscad = winOpener.getValue("DITG_DSCAD_${param.indiceRiga}");
	</c:when>
	<c:otherwise>
	 	globalOpenerAMMGAR = winOpener.getValue("V_DITGAMMIS_AMMGAR_FITTIZIO_${param.indiceRiga}");
	 	abilitaz = winOpener.getValue("ABILITAZ_FITTIZIO_${param.indiceRiga}");
	 	dscad = winOpener.getValue("DSCAD_FITTIZIO_${param.indiceRiga}");
	</c:otherwise>
</c:choose>

		var globalOpenerMOTIES = '${param.moties}';

		var arrayFasiRicezione = new Array();
	<c:forEach items="${tabellatoFasiGara}" var="faseRicezione" varStatus="indice1" >
		arrayFasiRicezione[${indice1.index}] = new Array(${faseRicezione.tipoTabellato*10}, "${faseRicezione.descTabellato}");
	</c:forEach>
	
		var arrayAmmgar = new Array();
	<c:forEach items="${tabellatoAmmgar}" var="ammGar" varStatus="indice2" >
		arrayAmmgar[${indice2.index}] = new Array(${ammGar.tipoTabellato}, "${ammGar.descTabellato}");
	</c:forEach>
	
		var arrayMotiviEsclusione = new Array();
	<c:forEach items="${listaMotiviEsclusione}" var="motivoEsclusione" varStatus="indice3" >
		arrayMotiviEsclusione[${indice3.index}] = new Array(${motivoEsclusione.tipoTabellato}, "${motivoEsclusione.descTabellato}");
	</c:forEach>
	
	var arrayStrin = new Array();
	<c:forEach items="${tabellatoStrin}" var="statoRin" varStatus="indice4" >
		arrayStrin[${indice4.index}] = new Array(${statoRin.tipoTabellato}, "${statoRin.descTabellato}");
	</c:forEach>
	
	<c:set var="codiceJSComune">
			//alert("openerAMMGAR = '" + globalOpenerAMMGAR + "'\nopenerMOTIES = '" + globalOpenerMOTIES + "'");
			if(globalOpenerAMMGAR == "" || (globalOpenerAMMGAR != "" && "2569".indexOf(globalOpenerAMMGAR) < 0 && globalOpenerMOTIES != "98" && globalOpenerMOTIES != "99")){
				//alert("codiceJSComune 1");
				showObj("rowV_DITGAMMIS_MOTIVESCL", false);
				showObj("rowV_DITGAMMIS_DETMOTESCL", false);
				visualizzaSezioneAttoAmmissioneEsclusione(false);
			}
			//alert("openerAMMGAR != \"\" == '" + globalOpenerAMMGAR != "");
			
			//alert("openerAMMGAR.length() == " + globalOpenerAMMGAR.length);
			
			if(globalOpenerAMMGAR == "" || (globalOpenerAMMGAR != "" && "346".indexOf(globalOpenerAMMGAR) < 0)){
				//alert("codiceJSComune 2");
				visualizzaSezioneVerificaRequisiti(false);
			}
	</c:set>
		
		<c:if test='${modo eq "MODIFICA" && tmpPaginaAttivaWizard eq step1Wizard}'>
			dricindOriginale = winOpener.getValue("DITG_DRICIND_${param.indiceRiga}");
			oradomOriginale = winOpener.getValue("DITG_ORADOM_${param.indiceRiga}");
		</c:if>
		
		<c:if test='${modo eq "MODIFICA" && tmpPaginaAttivaWizard eq step5Wizard}'>
			datoffOriginale = winOpener.getValue("DITG_DATOFF_${param.indiceRiga}");
			oraoffOrignale = winOpener.getValue("DITG_ORAOFF_${param.indiceRiga}");		
		</c:if>
		
		function initStep1Wizard(){
			${codiceJSComune}
			
		<c:choose>
			<c:when test='${modoAperturaScheda eq "MODIFICA"}'>
			var dprdom = winOpener.getValue("DITG_DPRDOM_${param.indiceRiga}");
			if(dprdom!=null && dprdom!=""){
				var splitDprdom = dprdom.split(" ");
				setValue("DATAPART",splitDprdom[0]);
				setOriginalValue("DATAPART",splitDprdom[0]);
				setValue("ORAPART",splitDprdom[1].substring(0, 5));
				setOriginalValue("ORAPART",splitDprdom[1].substring(0, 5));
			}
			</c:when>
			<c:otherwise>
			var dprdom = getValue("DITG_DPRDOM");
			if(dprdom!=null && dprdom!=""){
				var splitDprdom = dprdom.split(" ");
				document.getElementById("DATAPARTview").innerHTML = splitDprdom[0];
				document.getElementById("ORAPARTview").innerHTML = splitDprdom[1].substring(0, 5);
			}
			</c:otherwise>
		</c:choose>
			
		
		<c:if test='${modoAperturaScheda eq "MODIFICA"}' >
			
			// Copia dei campi dall'opener alla popup
			for(var i=1; i < arrayCampiStep1Wizard.length; i++){
				if(i == 1 && isProceduraAggiudicazioneAperta == "true")
					setValue("DITG_NPROGG", winOpener.getValue("DITG_NUM_PROGG_${param.indiceRiga}"));
				if(i > 1)
					setValue(arrayCampiStep1Wizard[i], winOpener.getValue(arrayCampiStep1Wizard[i] + "_${param.indiceRiga}"));
			}
			gestioneFasGar_Moties();
		</c:if>
		}
		
		function initStep2Wizard(){
			<c:choose>
				<c:when test="${(isGaraElenco eq true || isGaraCatalogo eq true) && tmpPaginaAttivaWizard eq step2Wizard}" >
					if(globalOpenerAMMGAR == "" || (globalOpenerAMMGAR != "" && "2569".indexOf(globalOpenerAMMGAR) < 0 && globalOpenerMOTIES != "98" && globalOpenerMOTIES != "99")){
						showObj("rowV_DITGAMMIS_MOTIVESCL", false);
						showObj("rowV_DITGAMMIS_DETMOTESCL", false);
						visualizzaSezioneAttoAmmissioneEsclusione(false);
					}
					var estimp = getValue("ESTIMP_FIT");
					if((globalOpenerAMMGAR == "" || (globalOpenerAMMGAR != "" && "346".indexOf(globalOpenerAMMGAR) < 0)) && estimp != "1"){
						//alert("codiceJSComune 2");
						visualizzaSezioneVerificaRequisiti(false);
					}
					
				</c:when>
				<c:otherwise>
					${codiceJSComune}
				</c:otherwise>
			</c:choose>
			
			<c:choose>
				<c:when test='${modoAperturaScheda eq "MODIFICA"}'>
				var dprdom = winOpener.getValue("DITG_DPRDOM_${param.indiceRiga}");
				if(dprdom!=null && dprdom!=""){
					var splitDprdom = dprdom.split(" ");
					setValue("DATAPART",splitDprdom[0]);
					setOriginalValue("DATAPART",splitDprdom[0]);
					setValue("ORAPART",splitDprdom[1].substring(0, 5));
					setOriginalValue("ORAPART",splitDprdom[1].substring(0, 5));
				}
				setValue("DITG_NPRDOM", winOpener.getValue("DITG_NPRDOM" + "_${param.indiceRiga}"));
				</c:when>
				<c:otherwise>
				var dprdom = getValue("DITG_DPRDOM");
				if(dprdom!=null && dprdom!=""){
					var splitDprdom = dprdom.split(" ");
					document.getElementById("DATAPARTview").innerHTML = splitDprdom[0];
					document.getElementById("ORAPARTview").innerHTML = splitDprdom[1].substring(0, 5);
				}
				</c:otherwise>
			</c:choose>
			
		<c:if test='${modoAperturaScheda eq "MODIFICA"}' >
			gestioneFasGar_Moties();
		</c:if>
		<c:if test='${isGaraElenco eq true || isGaraCatalogo eq true}' >
			<c:if test='${modoAperturaScheda eq "MODIFICA"}' >
				var dsospe = winOpener.getValue("DITG_DSOSPE_${param.indiceRiga}");
				var dricrin = winOpener.getValue("DITG_DRICRIN_${param.indiceRiga}");
				var drevoca = winOpener.getValue("DITG_DREVOCA_${param.indiceRiga}");
				setValue("DITG_DSOSPE",dsospe);
				setValue("DITG_DSCAD",dscad);
				setValue("DITG_DRICRIN",dricrin);
				setValue("DITG_REVOCA",drevoca);
				var drevocaOrigin = $("#DITG_DREVOCA").val();
					$("#DITG_DREVOCA").val(drevoca);
				var dsospeOrigin = $("#DITG_DSOSPE").val();
				if(!dsospeOrigin){
					$("#DITG_DSOSPE").val(dsospe);
				}
				
				var strin = winOpener.getValue("DITG_STRIN_${param.indiceRiga}");
				setValue("DITG_STRIN",strin);
				/*
				if(strin == null || strin == ""){
					setValue("DITG_STRIN","");
				} else {
					for(var i=0; i < arrayStrin.length; i++){
						var statoRin = arrayStrin[i][0];
						if (statoRin == strin) {
							//setValue("DITG_STRIN",  arrayStrin[i][1]);
							document.getElementById("DITG_STRIN").option[i].innerHTML = arrayStrin[i][1];
							break;
						}
					}
				}
				*/
				if(abilitaz!=1 && abilitaz!=8){
					document.getElementById("DITG_DSCAD").disabled = true;
					document.getElementById("DITG_DRICRIN").disabled = true;
				}
			</c:if>
			if(abilitaz!=7 && abilitaz!=8){
				showObj("rowDITG_DSOSPE", false);
				showObj("rowtitoloSezioneSospensione", false);
			}
			if(abilitaz!=5 && abilitaz!=10){
				showObj("rowDITG_DREVOCA", false);
				showObj("rowtitoloSezioneRevoca", false);
			}
			<c:if test="${!empty tipoRinnovo and tipoRinnovo ne 1}">
				showObj("rowDITG_DRICRIN",false);
			</c:if>
			
		</c:if>	
			
		}
	
		function initStep3Wizard(){
			${codiceJSComune}
					
		<c:if test='${modoAperturaScheda eq "MODIFICA"}' >
			var openerINVGAR = winOpener.getValue("DITG_INVGAR_${param.indiceRiga}");
			setValue("DITG_INVGAR", openerINVGAR);
			if(document.getElementById("DITG_INVGARview")!=null){
				if(openerINVGAR == "1"){
					document.getElementById("DITG_INVGARview").innerHTML = "Si";
				}	 else if(openerINVGAR == ""){
					document.getElementById("DITG_INVGARview").innerHTML = "";
				} else {
					document.getElementById("DITG_INVGARview").innerHTML = "No";
					document.getElementById("DITG_NPROTG").disabled = true;
					document.getElementById("DITG_DINVIG").disabled = true;
				}
			}
			
			// Copia valori dall'opener dei soli campi modificabili
	
			setValue("DITG_NPROTG", winOpener.getValue("DITG_NPROTG_${param.indiceRiga}"));
			setValue("DITG_DINVIG", winOpener.getValue("DITG_DINVIG_${param.indiceRiga}"));
			setValue("DITG_TIPRIN", winOpener.getValue("DITG_TIPRIN_${param.indiceRiga}"));
			gestioneFasGar_Moties();
			
			<c:if test="${(isGaraElenco eq true || isGaraCatalogo eq true) && tmpPaginaAttivaWizard eq step3Wizard && (tipoalgo eq 2 || tipoalgo eq 6 || tipoalgo eq 7 || tipoalgo eq 8 || tipoalgo eq 9 || tipoalgo eq 10 || tipoalgo eq 13)}">
				setValue("ISCRIZCAT_ALTPEN", winOpener.getValue("ISCRIZCAT_ALTPEN_${param.indiceRiga}"));
				setValue("ISCRIZCAT_NOTPEN", winOpener.getValue("ISCRIZCAT_NOTPEN_${param.indiceRiga}"));
				setValue("ISCRIZCAT_ULTNOT", winOpener.getValue("ISCRIZCAT_ULTNOT_${param.indiceRiga}"));
			</c:if>
			
		</c:if>
		}
		
		function initStep5Wizard(){
			
			${codiceJSComune}
	
		<c:choose>
			<c:when test='${modoAperturaScheda eq "MODIFICA"}' >
				var garaTelematica ="${garaTelematica }";
				var openerINVOFF = winOpener.getValue("DITG_INVOFF_${param.indiceRiga}");
				setValue("DITG_INVOFF", openerINVOFF);
				//document.getElementById("DITG_DATOFF").onchange = checkDataRicezioneOfferta;
				//alert("DTEOFF = " + getValue("DTEOFF"));
				if(openerINVOFF == "1"){
					document.getElementById("DITG_INVOFFview").innerHTML = "Si";
					setValue("rowDITG_TIPRIN", "");
					showObj("rowDITG_TIPRIN", false);
				} else if(openerINVOFF == "" || openerINVOFF == "0"){
					document.getElementById("DITG_INVOFFview").innerHTML = "";
					setValue("rowDITG_TIPRIN", "");
					showObj("rowDITG_TIPRIN", false);
				} else {
					document.getElementById("DITG_INVOFFview").innerHTML = "No";
					document.getElementById("DITG_NPROFF").disabled = true;
					document.getElementById("DATAOFF").disabled = true;
					document.getElementById("ORAOFF").disabled = true;
					showObj("rowDITG_TIPRIN", true);
				}
				if(garaTelematica == "true" || openerINVOFF == "2"){
					document.getElementById("DITG_DATOFF").disabled = true;
					document.getElementById("DITG_ORAOFF").disabled = true;
					document.getElementById("DITG_MEZOFF").disabled = true;
				}
	
				// Copia valori dall'opener dei soli campi modificabili
				setValue("DITG_NPROFF", winOpener.getValue("DITG_NPROFF_${param.indiceRiga}"));
				setValue("DITG_DATOFF", winOpener.getValue("DITG_DATOFF_${param.indiceRiga}"));
				setValue("DITG_ORAOFF", winOpener.getValue("DITG_ORAOFF_${param.indiceRiga}"));
				setValue("DITG_MEZOFF", winOpener.getValue("DITG_MEZOFF_${param.indiceRiga}"));
				setValue("DITG_NUMORDPL", winOpener.getValue("DITG_NUM_ORDPL_${param.indiceRiga}"));
				setValue("DITG_TIPRIN", winOpener.getValue("DITG_TIPRIN_${param.indiceRiga}"));
				setValue("DITG_PLIOFF", winOpener.getValue("DITG_PLIOFF_${param.indiceRiga}"));
				setValue("DITG_NOTPOFF", winOpener.getValue("DITG_NOTPOFF_${param.indiceRiga}"));				
				gestioneFasGar_Moties();
				
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
				var invoff=getValue("DITG_INVOFF");
				if(invoff!=2)
					showObj("rowDITG_TIPRIN", false);
				
				var dproff = getValue("DITG_DPROFF");
				if(dproff!=null && dproff!=""){
					var splitDproff = dproff.split(" ");
					document.getElementById("DATAOFFview").innerHTML = splitDproff[0];
					document.getElementById("ORAOFFview").innerHTML = splitDproff[1].substring(0, 5);
				}
			</c:otherwise>
		</c:choose>
			
		}

<c:if test='${modoAperturaScheda eq "MODIFICA"}' >
	<c:if test='${not empty requestScope.ulterioriCampiDITG}' >
		// Set dei valori dei campi precedentemente modificati
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
			setValue("DITGSTATI_NPPRESDOC",    "${requestScope.valoreDITGSTATI_NPPRESDOC}");
		</c:if>

		<c:if test='${not empty requestScope.valoreDITGSTATI_NPLETTCOMESCL}'>
			setValue("DITGSTATI_NPLETTCOMESCL", ${gene:string4Js(requestScope.valoreDITGSTATI_NPLETTCOMESCL)});
		</c:if>
		<c:if test='${not empty requestScope.valoreDITGSTATI_DPLETTCOMESCL}'>
			setValue("DITGSTATI_DPLETTCOMESCL", "${requestScope.valoreDITGSTATI_DPLETTCOMESCL}");
		</c:if>	

		<c:if test='${not empty requestScope.valoreDITG_ORADOM}'>
			setValue("DITG_ORADOM",  "${requestScope.valoreDITG_ORADOM}");
		</c:if>
		<c:if test='${not empty requestScope.valoreDITG_MEZDOM}'>
			setValue("DITG_MEZDOM",  "${requestScope.valoreDITG_MEZDOM}");
		</c:if>
		<c:if test='${not empty requestScope.valoreDITG_PLIDOM}'>
			setValue("DITG_PLIDOM",  "${requestScope.valoreDITG_PLIDOM}");
		</c:if>
		<c:if test='${not empty requestScope.valoreDITG_NOTPDOM}'>
			setValue("DITG_NOTPDOM",  "${requestScope.valoreDITG_NOTPDOM}");
		</c:if>
		<c:if test="${isGaraElenco eq true || isGaraCatalogo eq true}">
			setValue("DITG_ESTIMP",  ${gene:string4Js(requestScope.valoreDITG_ESTIMP)});
			setValue("DITG_INOSSERV",  ${gene:string4Js(requestScope.valoreDITG_INOSSERV)});
			setValue("DITG_COORDSIC",  ${gene:string4Js(requestScope.valoreDITG_COORDSIC)});
			setValue("DITG_REQTORRE",  ${gene:string4Js(requestScope.valoreDITG_REQTORRE)});
			setValue("DITG_DSORTEV",  ${gene:string4Js(requestScope.valoreDITG_DSORTEV)});
		</c:if>		
		
		<c:if test='${not empty requestScope.valoreDITG_ALTNOT}'>
			setValue("DITG_ALTNOT",  ${gene:string4Js(requestScope.valoreDITG_ALTNOT)});
		</c:if>
		
	</c:if>
	
		if(isProceduraAggiudicazioneAperta != "true" && (faseRicezioneAttiva == "${step1Wizard}" || (faseRicezioneAttiva == "${step3Wizard}" && isProceduraNegoziata )) ){
			document.getElementById("DITG_NPROGG").disabled = false;
			setValue("DITG_NPROGG", winOpener.getValue("DITG_NUM_PROGG_" + ${param.indiceRiga}));
		} else {
			document.getElementById("DITG_NPROGG").disabled = true;
		}

		if(faseRicezioneAttiva == "${step5Wizard}"){
			document.getElementById("DITG_NUMORDPL").disabled = false;
			setValue("DITG_NUMORDPL", winOpener.getValue("DITG_NUM_ORDPL_" + ${param.indiceRiga}));
		} else {
			document.getElementById("DITG_NUMORDPL").disabled = true;
		}

		function conferma(){
			<c:if test="${tmpPaginaAttivaWizard eq step1Wizard or tmpPaginaAttivaWizard eq step2Wizard}">
				var data = getValue("DATAPART");
				var ora = getValue("ORAPART");
				<c:choose>
					<c:when test="${isGaraElenco eq true || isGaraCatalogo eq true}">
						var tipoDomanda = "iscrizione";
					</c:when>
					<c:otherwise>
						var tipoDomanda = "partecipazione";
					</c:otherwise>
				</c:choose>
				
				if((data!=null && data!="") && (ora==null || ora =="" )){
					alert("Non è possibile procedere, deve essere inserita l'ora di del protocollo presentazione della domanda di " + tipoDomanda);
					return;
				}
				if((data==null || data=="") && (ora!=null && ora !="" )){
					alert("Non è possibile procedere, non può essere inserita l'ora di del protocollo presentazione della domanda di " + tipoDomanda + " in mancanza della data");
					return;
				}
				var dataOriginale = getOriginalValue("DATAPART");
				var oraOriginale = getOriginalValue("ORAPART");
				if(dataOriginale!=data || ora!=oraOriginale){
					var dprdom = "";
					if(data!=null && ora!=null && data!="" && ora !="")
						dprdom = data + " " + ora + ":00";
					winOpener.setValue("DITG_DPRDOM" + "_${param.indiceRiga}", dprdom);
					<c:if test="${isGaraElenco ne true && isGaraCatalogo ne true }">
						if(winOpener.document.getElementById("colDPRDOM_FIT_${param.indiceRiga}"))
							winOpener.document.getElementById("colDPRDOM_FIT_${param.indiceRiga}").innerHTML = dprdom;
					</c:if>
					winOpener.setValue("DPRDOM_FIT_NASCOSTO" + "_${param.indiceRiga}", dprdom);
				}
				<c:if test="${isGaraElenco ne true && isGaraCatalogo ne true }">
					if(winOpener.document.getElementById("colNPRDOM_FIT_${param.indiceRiga}"))
						winOpener.document.getElementById("colNPRDOM_FIT_${param.indiceRiga}").innerHTML = getValue("DITG_NPRDOM");
				</c:if>
			</c:if>
			
			var arrayCampiModificabili = new Array("DITG_NPRDOM", "DITG_DRICIND", 
							 "DITG_NPROTG", "DITG_DINVIG", "DITG_INVOFF", "DITG_NPROFF", "DITG_DATOFF",
							 "DITG_ORAOFF", "DITG_MEZOFF", "V_DITGAMMIS_AMMGAR", "V_DITGAMMIS_MOTIVESCL",
							 "V_DITGAMMIS_DETMOTESCL","DITG_ORADOM", "DITG_TIPRIN","DITG_PLIDOM","DITG_NOTPDOM", "DITG_PLIOFF", "DITG_NOTPOFF");

			// "DITG_ORADOM", "DITG_MEZDOM", "DITG_ALTNOT"
			for(var i=0; i < arrayCampiModificabili.length; i++){
				
				winOpener.setValue(arrayCampiModificabili[i] + "_${param.indiceRiga}", getValue(arrayCampiModificabili[i]));
				// Copia nel campo di appoggio DITG_NUM_PROGG_<i> dell'opener del numovo valore del progressivo
			}
			//if(getOriginalValue("DITG_NPROGG") != getValue("DITG_NPROGG")){
			<c:if test="${tmpPaginaAttivaWizard ne step5Wizard}">
				winOpener.setValue("DITG_NUM_PROGG_${param.indiceRiga}", getValue("DITG_NPROGG"));
				winOpener.document.getElementById("NPROGG_VALUE_${param.indiceRiga}").innerHTML = getValue("DITG_NPROGG");
			</c:if>
				
			//}
			//if(getOriginalValue("DITG_NUMORDPL") != getValue("DITG_NUMORDPL")){
			<c:if test="${tmpPaginaAttivaWizard eq step5Wizard}">
				winOpener.setValue("DITG_NUM_ORDPL_${param.indiceRiga}", getValue("DITG_NUMORDPL"));
				if(winOpener.document.getElementById("isProceduraAggiudicazioneAperta") && winOpener.document.getElementById("isProceduraAggiudicazioneAperta").value=="true"){
					if(getOriginalValue("DITG_NUMORDPL") != getValue("DITG_NUMORDPL")){
						winOpener.setValue("DITG_NUM_PROGG_${param.indiceRiga}", getValue("DITG_NUMORDPL"));
					}
				}
				winOpener.document.getElementById("NUMORDPL_VALUE_${param.indiceRiga}").innerHTML = getValue("DITG_NUMORDPL");
				
				
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
					winOpener.document.getElementById("colDPROFF_FIT_${param.indiceRiga}").innerHTML = dproff;
					winOpener.setValue("DPROFF_FIT_NASCOSTO" + "_${param.indiceRiga}", dproff);
				}
				winOpener.document.getElementById("colNPROFF_FIT_${param.indiceRiga}").innerHTML = getValue("DITG_NPROFF");
				
			</c:if>
			//}
			
			<c:if test="${(isGaraElenco eq true || isGaraCatalogo eq true) && tmpPaginaAttivaWizard eq step2Wizard}">
				winOpener.setValue("DITG_ESTIMP_${param.indiceRiga}", getValue("DITG_ESTIMP"));
				winOpener.setValue("DITG_DSOSPE_${param.indiceRiga}", getValue("DITG_DSOSPE"));
				winOpener.setValue("DITG_REVOCA_${param.indiceRiga}", getValue("DITG_DREVOCA"));
				winOpener.setValue("DITG_STRIN_${param.indiceRiga}", getValue("DITG_STRIN"));
				winOpener.setValue("DITG_DSCAD_${param.indiceRiga}", getValue("DITG_DSCAD"));
				winOpener.setValue("DITG_DRICRIN_${param.indiceRiga}", getValue("DITG_DRICRIN"));
				winOpener.$("#DITG_DREVOCA_${param.indiceRiga}").val(getValue("DITG_DREVOCA"));
				winOpener.$("#DITG_DSOSPE_${param.indiceRiga}").val(getValue("DITG_DSOSPE"));
			</c:if>
			
			<c:if test="${(isGaraElenco eq true || isGaraCatalogo eq true) && tmpPaginaAttivaWizard eq step3Wizard && (tipoalgo eq 2 || tipoalgo eq 6 || tipoalgo eq 7 || tipoalgo eq 8 || tipoalgo eq 9 || tipoalgo eq 10 || tipoalgo eq 13)}">
				winOpener.setValue("ISCRIZCAT_ALTPEN_${param.indiceRiga}", getValue("ISCRIZCAT_ALTPEN"));
				winOpener.setValue("ISCRIZCAT_NOTPEN_${param.indiceRiga}", getValue("ISCRIZCAT_NOTPEN"));
				winOpener.setValue("ISCRIZCAT_ULTNOT_${param.indiceRiga}", getValue("ISCRIZCAT_ULTNOT"));
			</c:if>
			
			document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
			schedaConferma();
			//window.close();
		}
		
		function gestioneFasGar_Moties(){
			var openerAMMGAR = winOpener.getValue("V_DITGAMMIS_AMMGAR_${param.indiceRiga}");
			var openerMOTIES = '${param.moties}';
			var tmp = null;
			//alert("openerAMMGAR = '" + openerAMMGAR + "'\nopenerMOTIES = '" + openerMOTIES + "'");
			//if(openerAMMGAR == "1" || openerAMMGAR == ""){
			if("1345810".indexOf(openerAMMGAR) >= 0 || openerAMMGAR == ""){
				//alert("1");
				setValue("V_DITGAMMIS_AMMGAR", openerAMMGAR);
				setValue("V_DITGAMMIS_DETMOTESCL", "");
				if(new Number(openerMOTIES) < 98 || new Number(openerMOTIES) > 99)
					setValue("V_DITGAMMIS_MOTIVESCL", "");
				//else
				//	setValue("V_DITGAMMIS_MOTIVESCL", s);
				if(openerAMMGAR == ""){
					tmp = "";
				} else {
					tmp = arrayAmmgar[new Number(openerAMMGAR)-1][1];
				}
	
				setValue("V_DITGAMMIS_DETMOTESCL", winOpener.getValue("V_DITGAMMIS_DETMOTESCL_${param.indiceRiga}"));
				//setValue("DITG_ALTNOT", winOpener.getValue("DITG_ALTNOT_${param.indiceRiga}"));
				
			}  else if("2679".indexOf(openerAMMGAR) >= 0){ //  openerAMMGAR == "2" || openerAMMGAR == "6"){
				//alert("2");
				setValue("V_DITGAMMIS_AMMGAR", openerAMMGAR);
				if(openerMOTIES != "98" || openerMOTIES != "99")
				  setValue("V_DITGAMMIS_MOTIVESCL", openerMOTIES);
				if(openerAMMGAR == ""){
					tmp = "";
				} else {
					tmp = arrayAmmgar[new Number(openerAMMGAR)-1][1];
				}
	
				setValue("V_DITGAMMIS_DETMOTESCL", winOpener.getValue("V_DITGAMMIS_DETMOTESCL_${param.indiceRiga}"));
				//setValue("DITG_ALTNOT", winOpener.getValue("DITG_ALTNOT_${param.indiceRiga}"));
				
				
			} else {
				//alert("3");
				setValue("V_DITGAMMIS_AMMGAR", "");
				setValue("V_DITGAMMIS_DETMOTESCL", "");
				setValue("V_DITGAMMIS_MOTIVESCL", "");
				tmp = "";
				
				
			}
			if(document.getElementById("V_DITGAMMIS_AMMGARview"))
				document.getElementById("V_DITGAMMIS_AMMGARview").innerHTML = tmp;
			
			if(! ((openerAMMGAR == "2" || openerAMMGAR == "6" || openerAMMGAR == "9" )&& (openerMOTIES != "98" && openerMOTIES != "99"))){
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
	</c:if>
	
		var arrayCampiStep1Wizard = new Array("DITG_NOMIMO", "DITG_NPROGG", "DITG_NPRDOM", "DITG_DRICIND", "DITG_ORADOM", "DITG_PLIDOM", "DITG_NOTPDOM"); //, "DITG_MEZDOM");
	
		// Nasconde i campi visibili solo nella fase di ricezione = -5
		function nascondiCampiFase_5(){
			for(var i=0; i < arrayCampiStep1Wizard.length; i++)
				showObj("row" + arrayCampiStep1Wizard[i], false);
		}
		
		// Nasconde i campi visibili solo nella fase di ricezione = -3	
		function nascondiCampiFase_3(){
			showObj("rowDITG_INVGAR", false);
			showObj("rowDITG_NPROTG", false);
			showObj("rowDITG_DINVIG", false);
		}
	
		// Nasconde i campi visibili solo nella fase di ricezione = 1
		function nascondiCampiFase1(){
			showObj("rowDITG_INVOFF", false);
			showObj("rowDITG_NPROFF", false);
			showObj("rowDITG_DATOFF", false);
			showObj("rowDITG_ORAOFF", false);
			showObj("rowDITG_MEZOFF", false);
		}

		function inizializzaPagina(){
		<c:choose>
			<c:when test='${tmpPaginaAttivaWizard eq step1Wizard}'>
				initStep1Wizard();
			</c:when>
			<c:when test='${tmpPaginaAttivaWizard eq step2Wizard}'>
				initStep2Wizard();
			</c:when>
			<c:when test='${tmpPaginaAttivaWizard eq step3Wizard}'>
				showObj("rowV_DITGAMMIS_AMMGAR", false);
				showObj("rowV_DITGAMMIS_MOTIVESCL", false);
				showObj("rowV_DITGAMMIS_DETMOTESCL", false);
				
				visualizzaSezioneAttoAmmissioneEsclusione(false);
				visualizzaSezioneVerificaRequisiti(false);
				initStep3Wizard();
			</c:when>
			<c:when test='${tmpPaginaAttivaWizard eq step5Wizard}'>
				initStep5Wizard();
			</c:when>
		</c:choose>
		<c:if test='${modo eq "MODIFICA" && tmpPaginaAttivaWizard ne step3Wizard}' >
			if(getValue("V_DITGAMMIS_MOTIVESCL")==98 || getValue("V_DITGAMMIS_MOTIVESCL")==99)
				document.getElementById("V_DITGAMMIS_MOTIVESCL").disabled = true;
		</c:if>
		}

		inizializzaPagina();
	
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
		
				
		function checkDataRichiestaDomanda(data){
			if(getValue("DTEPAR") != ""){
				var ora = getValue("DITG_ORADOM");
				if(data!= null && data != "" && data!=dricindOriginale){
					dricindOriginale = data;
					checkDatiRichiestaOfferta(data, ora,getValue("DTEPAR"),getValue("OTEPAR"),"La data inserita e' successiva alla data termine richiesta partecipazione");
				}
			}
			
		}
		
		function checkOraRichiestaDomanda(ora){
			if(getValue("DTEPAR") != ""){
				var data = getValue("DITG_DRICIND");
				if(ora!= null && ora != "" && ora!=oradomOriginale){
					oradomOriginale = ora;
					checkDatiRichiestaOfferta(data, ora,getValue("DTEPAR"),getValue("OTEPAR"),"La data inserita e' successiva alla data termine richiesta partecipazione");
				}
			}
			
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
			showObj("rowDITGSTATI_DPRESCC", visibile);
			showObj("rowDITGSTATI_NPPRESDOC", visibile);
		}
		
		function showSezioneVerificaRequisiti(estimp){
			setValue("ESTIMP_FIT",estimp);
			var winOpener = window.opener;
			var OpenerAMMGAR = null;
			<c:choose>
				<c:when test='${modo eq "MODIFICA"}' >
					if(winOpener.document.getElementById("V_DITGAMMIS_AMMGAR_${param.indiceRiga}").disabled){
						OpenerAMMGAR = "${datiRiga.V_DITGAMMIS_AMMGAR}";
					} else {
						OpenerAMMGAR = winOpener.getValue("V_DITGAMMIS_AMMGAR_${param.indiceRiga}");
					}
				</c:when>
				<c:otherwise>
				 	OpenerAMMGAR = winOpener.getValue("V_DITGAMMIS_AMMGAR_FITTIZIO_${param.indiceRiga}");
				</c:otherwise>
			</c:choose>
			
			
			if((globalOpenerAMMGAR == "" || (globalOpenerAMMGAR != "" && "346".indexOf(globalOpenerAMMGAR) < 0)) && estimp != "1"){
				visualizzaSezioneVerificaRequisiti(false);
			}else{
				visualizzaSezioneVerificaRequisiti(true);
			}
			
		}
		
		function showDataSorteggio(estimp){
			if(estimp==1){
				showObj("rowDITG_DSORTEV", true);
			}else{
				showObj("rowDITG_DSORTEV", false);
				setValue("DITG_DSORTEV","");
			}
			
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
	
	function sbiancaOra(data,campo){
		if(data==null || data == "")
			setValue(campo,"");
	}	
	
	<c:if test='${(tmpPaginaAttivaWizard eq step1Wizard or tmpPaginaAttivaWizard eq step2Wizard or tmpPaginaAttivaWizard eq step5Wizard) and  modo eq "MODIFICA"}'>
		//Nella tabella grigliaDataProt è stato inserito il td con classe='riempimento' che ha il solo
		//scopo di riempire una parte della tabella in modo da fare risultare più piccoli e quindi più
		//vicini i campi con l'ora e la data
		$('table.grigliaDataProt tr td.valore-dato').css('width','200');
		$('table.grigliaDataProt tr td.riempimento').css('width','40%');
	</c:if>
	</gene:javaScript>
</gene:template>
</div>