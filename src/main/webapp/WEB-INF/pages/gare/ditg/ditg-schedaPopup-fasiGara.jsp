<%
/*
 * Created on: 28-lug-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 *
 * Popup per campi ulteriori relativi alla ditta presenta nella lista delle
 * fase di gara in analisi
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

<jsp:include page="../gare/fasiGara/defStepWizardFasiGara.jsp" />

<div style="width:97%;">
<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="FasiGaraUlterioriDettagli">
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "DITG_FASIGARA")}' />
	<c:set var="formato52" value="${gene:callFunction2('it.eldasoft.sil.pg.tags.funzioni.CheckContieneFormatoFunction', pageContext, '52')}" />
	<c:set var="formato50" value="${gene:callFunction2('it.eldasoft.sil.pg.tags.funzioni.CheckContieneFormatoFunction', pageContext, '50')}" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="corpo">
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>	
	</gene:redefineInsert>

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
			<c:when test='${not empty param.isGaraLottiConOffertaUnica}'>
				<c:set var="tmpIsGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
			</c:when>
			<c:otherwise>
				<c:set var="tmpIsGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.isUlterioriCampiVerifica}'>
				<c:set var="isUlterioriCampiVerifica" value="${param.isUlterioriCampiVerifica}" />
			</c:when>
			<c:otherwise>
				<c:set var="isUlterioriCampiVerifica" value="${isUlterioriCampiVerifica}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.visPartgarUlterioriCampiOffUnica}'>
				<c:set var="visPartgarUlterioriCampiOffUnica" value="${param.visPartgarUlterioriCampiOffUnica}" />
			</c:when>
			<c:otherwise>
				<c:set var="visPartgarUlterioriCampiOffUnica" value="${visPartgarUlterioriCampiOffUnica}" />
			</c:otherwise>
		</c:choose>
	
		<c:choose>
			<c:when test='${tmpPaginaAttivaWizard eq step1Wizard}'>
				<c:set var="isPrequalifica" value="true" scope="request" />
			</c:when>
			<c:otherwise>
				<c:set var="isPrequalifica" value="false" scope="request" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.offtel}'>
				<c:set var="offtel" value="${param.offtel}" />
			</c:when>
			<c:otherwise>
				<c:set var="offtel" value="${offtel}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.bustalotti}'>
				<c:set var="bustalotti" value="${param.bustalotti}" />
			</c:when>
			<c:otherwise>
				<c:set var="bustalotti" value="${bustalotti}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.sezionitec}'>
				<c:set var="sezionitec" value="${param.sezionitec}" />
			</c:when>
			<c:otherwise>
				<c:set var="sezionitec" value="${sezionitec}" />
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test='${not empty param.isProceduraTelematica}'>
				<c:set var="isProceduraTelematica" value="${param.isProceduraTelematica}" />
			</c:when>
			<c:otherwise>
				<c:set var="isProceduraTelematica" value="${isProceduraTelematica}" />
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
				<c:set var="modlicg" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMODLICGFunction", pageContext, "")}' scope="request" />
			</c:when>
			<c:otherwise>
				<c:set var="modlicg" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMODLICFunction", pageContext, key)}' scope="request" />
			</c:otherwise>
		</c:choose>
		<c:set var="codiceGara" value='${gene:getValCampo(key, "DITG.CODGAR5")}' />
		<c:set var="nGara" value='${gene:getValCampo(key, "DITG.NGARA5")}' />
		<c:set var="vecchiaOEPV" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.isVecchiaOEPVFunction", pageContext, codiceGara)}' scope="request" />
		<c:if test="${tmpIsGaraLottiConOffertaUnica ne true }">
			<c:set var="garaInversa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetInversaFunction", pageContext, codiceGara)}' />
		</c:if>
				
		<gene:formScheda entita="DITG" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopupFasiGara" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupFasiRicezione_Gara">
			<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${tmpPaginaAttivaWizard}" />

			<gene:campoScheda campo="CODGAR5" visibile="false" />
			<gene:campoScheda campo="DITTAO"  visibile="false" />
			<gene:campoScheda campo="NOMIMO"  visibile="false" />
			<gene:campoScheda campo="NGARA5"  visibile="false" />
			
 			<gene:campoScheda campo="NPRREQ" visibile="${tmpPaginaAttivaWizard eq step4Wizard}"/>

			<gene:campoScheda campo="DATREQ" visibile="${tmpPaginaAttivaWizard eq step4Wizard}"/>
			<gene:campoScheda campo="ORAREQ" visibile="${tmpPaginaAttivaWizard eq step4Wizard}"/>
			<gene:campoScheda campo="MEZREQ" visibile="${tmpPaginaAttivaWizard eq step4Wizard}"/>

			<gene:campoScheda campo="PARTGAR" visibile='${isUlterioriCampiVerifica eq "true" or visPartgarUlterioriCampiOffUnica eq "true" or ((tmpPaginaAttivaWizard eq step6Wizard or tmpPaginaAttivaWizard eq step7Wizard) and tmpIsGaraLottiConOffertaUnica ne "true")}' modificabile="false" />
			<gene:campoScheda campo="REQMIN" visibile='${isUlterioriCampiVerifica eq "true"}' modificabile="false" />
			
			<gene:campoScheda campo="NPROFF" visibile="${tmpPaginaAttivaWizard eq step1Wizard}" title="N.protocollo presentazione offerta"/>
			<gene:campoScheda campo="DPROFF" visibile="${modo ne 'MODIFICA' and tmpPaginaAttivaWizard eq step1Wizard}" />
			<c:if test="${tmpPaginaAttivaWizard eq step1Wizard and  modo eq 'MODIFICA'}">
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
			<gene:campoScheda campo="AMMGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${tmpStepWizard}" modificabile="false" visibile='${tmpPaginaAttivaWizard ne step8Wizard and isUlterioriCampiVerifica ne "true"}' />
			<gene:campoScheda campo="MOTIVESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${tmpStepWizard}"  visibile="${tmpPaginaAttivaWizard ne step8Wizard}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoMOTIVESCL"/>
			<gene:campoScheda campo="DETMOTESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${tmpStepWizard}" visibile="${tmpPaginaAttivaWizard ne step8Wizard}" />
			<gene:campoScheda campo="RICMANO" entita="TORN" where="DITG.CODGAR5 = TORN.CODGAR" visibile="false"/>
			<gene:campoScheda campo="MODMANO" entita="TORN" where="DITG.CODGAR5 = TORN.CODGAR" visibile="false"/>

			
			
			<gene:campoScheda campo="DATFS11A" title="Data apertura busta amministrativa" entita='DITGEVENTI' where="DITGEVENTI.NGARA=DITG.NGARA5 and DITGEVENTI.DITTAO=DITG.DITTAO and DITGEVENTI.CODGAR=DITG.CODGAR5" visibile="${tmpPaginaAttivaWizard eq step1Wizard and isProceduraTelematica eq 'true'}" modificabile="false"/>
			<gene:campoScheda campo="DATFS11B1" title="Data apertura busta tecnico-qualitativa" entita='DITGEVENTI' where="DITGEVENTI.NGARA=DITG.NGARA5 and DITGEVENTI.DITTAO=DITG.DITTAO and DITGEVENTI.CODGAR=DITG.CODGAR5" visibile="${tmpPaginaAttivaWizard eq step6Wizard and isProceduraTelematica eq 'true' and sezionitec eq '1'}" modificabile="false"/>
			<c:choose>
				<c:when test="${sezionitec eq '1' }">
					<c:set var="titoloDatfs11b" value="Data apertura busta tecnico-quantitativa"/>
				</c:when>
				<c:otherwise>
					<c:set var="titoloDatfs11b" value="Data apertura busta tecnica"/>
				</c:otherwise>
			</c:choose>
			<gene:campoScheda campo="DATFS11B" title="${titoloDatfs11b}" entita='DITGEVENTI' where="DITGEVENTI.NGARA=DITG.NGARA5 and DITGEVENTI.DITTAO=DITG.DITTAO and DITGEVENTI.CODGAR=DITG.CODGAR5" visibile="${tmpPaginaAttivaWizard eq step6Wizard and isProceduraTelematica eq 'true'}" modificabile="false"/>
			<gene:campoScheda campo="DATFS11C" title="Data apertura busta economica" entita='DITGEVENTI' where="DITGEVENTI.NGARA=DITG.NGARA5 and DITGEVENTI.DITTAO=DITG.DITTAO and DITGEVENTI.CODGAR=DITG.CODGAR5" visibile="${tmpPaginaAttivaWizard eq step7Wizard and isProceduraTelematica eq 'true'}" modificabile="false"/>

			<gene:campoScheda campo="CONGRUO" modificabile="false" visibile="${tmpPaginaAttivaWizard eq step8Wizard}"/>
			<gene:campoScheda campo="CONGMOT" visibile="${tmpPaginaAttivaWizard eq step8Wizard}"/>
			<gene:campoScheda campo="CONGALT" visibile="${tmpPaginaAttivaWizard eq step8Wizard}"/>
			<gene:campoScheda campo="ALTNOT" />
			<gene:campoScheda campo="RICSUB" visibile='${empty tmpIsGaraLottiConOffertaUnica or (not empty tmpIsGaraLottiConOffertaUnica and tmpIsGaraLottiConOffertaUnica eq "true" and datiRiga.DITG_CODGAR5 ne datiRiga.DITG_NGARA5)}'/>
			
			<c:set var="condizioneLottiFaseEcnomica" value='${(tmpPaginaAttivaWizard eq step7Wizard or tmpPaginaAttivaWizard eq step7_5Wizard) and (empty tmpIsGaraLottiConOffertaUnica or (not empty tmpIsGaraLottiConOffertaUnica and tmpIsGaraLottiConOffertaUnica eq "true" and datiRiga.DITG_CODGAR5 ne datiRiga.DITG_NGARA5))}'/>
			<gene:campoScheda campo="IMPSICAZI" visibile='${condizioneLottiFaseEcnomica and datiRiga.TORN_RICMANO eq "1"}' modificabile="${ offtel ne 1 }"/>
			<gene:campoScheda campo="IMPMANO" visibile='${condizioneLottiFaseEcnomica and datiRiga.TORN_RICMANO eq "1" and datiRiga.TORN_MODMANO eq "1"}' modificabile="${ offtel ne 1 }"/>
			<gene:campoScheda campo="PERCMANO" visibile='${condizioneLottiFaseEcnomica and datiRiga.TORN_RICMANO eq "1" and datiRiga.TORN_MODMANO eq "2"}' modificabile="${ offtel ne 1 }"/>
			<gene:campoScheda campo="IMPPERM" visibile='${modlicg eq "6" and ((ultdetlic eq 1 or ultdetlic eq 3) and (vecchiaOEPV or formato50 or formato52)) or condizioneLottiFaseEcnomica and (ultdetlic eq 1 or ultdetlic eq 3)}' modificabile="${ offtel ne 1 }"/>
			<gene:campoScheda campo="IMPCANO" visibile='${modlicg eq "6" and ((ultdetlic eq 2 or ultdetlic eq 3) and (vecchiaOEPV or formato50 or formato52)) or condizioneLottiFaseEcnomica and (ultdetlic eq 2 or ultdetlic eq 3)}' modificabile="${ offtel ne 1 }"/>
			
		<c:if test='${tmpPaginaAttivaWizard eq step8Wizard}'>
			<gene:campoScheda nome="titoloSezioneGiustificazioni">
				<td colspan="2"><b>Giustificazioni requisiti</b></td>
			</gene:campoScheda>
	 		<gene:campoScheda campo="NPLETTRICHGIU" />
			<gene:campoScheda campo="DLETTRICHGIU" />
			<gene:campoScheda campo="DTERMPRESGIU" />
	 		<gene:campoScheda campo="NPRICEZGIU" />
			<gene:campoScheda campo="DRICEZGIU" />
			<gene:campoScheda campo="OGGRICEZGIU" />
			<gene:gruppoCampi idProtezioni="CONTRADD">
				<gene:campoScheda nome="titoloSezioneContraddittorio">
					<td colspan="2"><b>Contraddittorio</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="NVCOMMTECN" />
				<gene:campoScheda campo="DVCOMMTECN" />
				<gene:campoScheda campo="NVCONTRADD" />
				<gene:campoScheda campo="DVCONTRADD" />
			 </gene:gruppoCampi>
		</c:if>
		
		<c:if test='${tmpPaginaAttivaWizard ne step2Wizard}'>
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
			<input type="hidden" name="modalitaAggiudicazioneGara" id="modalitaAggiudicazioneGara" value="" />
			<input type="hidden" name="isUlterioriCampiVerifica" id="isUlterioriCampiVerifica" value="${isUlterioriCampiVerifica}" />
			<input type="hidden" name="visPartgarUlterioriCampiOffUnica" id="visPartgarUlterioriCampiOffUnica" value="${visPartgarUlterioriCampiOffUnica}" />
			<input type="hidden" name=offtel id="offtel" value="${offtel}" />
			<input type="hidden" name=bustalotti id="bustalotti" value="${bustalotti}" />
			<input type="hidden" name=sezionitec id="sezionitec" value="${sezionitec}" />
			<input type="hidden" name=isProceduraTelematica id="isProceduraTelematica" value="${isProceduraTelematica}" />
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
		
		console.log("${datiRiga.TORN_RICMANO}");

		//Il campo PARTGAR si visualizza solo per le gare ad Offerta unica
		<c:if test='${tmpIsGaraLottiConOffertaUnica ne "true"}'>
			showObj("rowDITG_PARTGAR", false);
		</c:if>
		
		var winOpener = window.opener;
		var arrayFasiGara = new Array();
		<c:forEach items="${tabellatoFasiGara}" var="faseGara" varStatus="indice1" >
		arrayFasiGara[${indice1.index}] = new Array(${faseGara.tipoTabellato}, "${faseGara.descTabellato}");
		</c:forEach>
	
		var arrayAmmgar = new Array();
		<c:forEach items="${tabellatoAmmgar}" var="ammGar" varStatus="indice2" >
		arrayAmmgar[${indice2.index}] = new Array(${ammGar.tipoTabellato}, "${ammGar.descTabellato}");
		</c:forEach>
	
		var arrayMotiviEsclusione = new Array();
		<c:forEach items="${listaMotiviEsclusione}" var="motivoEsclusione" varStatus="indice3" >
		arrayMotiviEsclusione[${indice3.index}] = new Array(${motivoEsclusione.tipoTabellato}, "${motivoEsclusione.descTabellato}");
		</c:forEach>

		var globalOpenerAMMGAR = null;
		var globalOpenerCONGRUO = null;
		var isOffertaCongrua = "true";
		
		<c:if test='${tmpPaginaAttivaWizard eq step8Wizard}'>
			<c:choose>
				<c:when test='${modo eq "MODIFICA"}' >
					globalOpenerCONGRUO = winOpener.getValue("DITG_CONGRUO_${param.indiceRiga}")
				</c:when>
				<c:otherwise>
					globalOpenerCONGRUO = "${datiRiga.DITG_CONGRUO}";
				</c:otherwise>
			</c:choose>
		</c:if>
		if(globalOpenerCONGRUO != null && globalOpenerCONGRUO == "2"){
			isOffertaCongrua = "false";
		}
		
				
		<c:choose>
			<c:when test='${modo eq "MODIFICA"}' >
				var garaInversaAmmgarBloccato;
				if(winOpener.document.getElementById("garaInversaAmmgarBloccato")!=null)
					garaInversaAmmgarBloccato = winOpener.document.getElementById("garaInversaAmmgarBloccato").value;
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
		
		openerPartgar =winOpener.getValue("DITG_PARTGAR_${param.indiceRiga}");

<c:if test='${modoAperturaScheda eq "MODIFICA"}' >
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
			setValue("DITGSTATI_NPPRESDOC",    "${requestScope.valoreDITGSTATI_NPPRESDOC}");
		</c:if>

		<c:if test='${not empty requestScope.valoreDITGSTATI_NPLETTCOMESCL}'>
			setValue("DITGSTATI_NPLETTCOMESCL", ${gene:string4Js(requestScope.valoreDITGSTATI_NPLETTCOMESCL)});
		</c:if>
		<c:if test='${not empty requestScope.valoreDITGSTATI_DPLETTCOMESCL}'>
			setValue("DITGSTATI_DPLETTCOMESCL", "${requestScope.valoreDITGSTATI_DPLETTCOMESCL}");
		</c:if>
		
		<c:if test='${not empty requestScope.valoreDITG_NPRREQ}'>
			setValue("DITG_NPRREQ",  ${gene:string4Js(requestScope.valoreDITG_NPRREQ)});
		</c:if>
		<c:if test='${not empty requestScope.valoreDITG_DATREQ}'>
			setValue("DITG_DATREQ",  "${requestScope.valoreDITG_DATREQ}");
		</c:if>
		<c:if test='${not empty requestScope.valoreDITG_ORAREQ}'>
			setValue("DITG_ORAREQ",  "${requestScope.valoreDITG_ORAREQ}");
		</c:if>
		<c:if test='${not empty requestScope.valoreDITG_MEZREQ}'>
			setValue("DITG_MEZREQ",  "${requestScope.valoreDITG_MEZREQ}");
		</c:if>
		<c:if test='${not empty requestScope.valoreDITG_ALTNOT}'>
			setValue("DITG_ALTNOT",  ${gene:string4Js(requestScope.valoreDITG_ALTNOT)});
		</c:if>
		<c:if test='${not empty requestScope.valoreDITG_RICSUB}'>
			setValue("DITG_RICSUB",  "${requestScope.valoreDITG_RICSUB}");
		</c:if>
		<c:if test='${not empty requestScope.valoreDITG_CONGALT}'>
			setValue("DITG_CONGALT", ${gene:string4Js(requestScope.valoreDITG_CONGALT)});
		</c:if>
		<c:if test='${not empty requestScope.valoreDITG_REQMIN}'>
			setValue("DITG_REQMIN", ${gene:string4Js(requestScope.valoreDITG_REQMIN)});
		</c:if>

		<c:if test='${tmpPaginaAttivaWizard eq step8Wizard}'>
			<c:if test='${not empty requestScope.valoreDITG_NPLETTRICHGIU}'>
				setValue("DITG_NPLETTRICHGIU", ${gene:string4Js(requestScope.valoreDITG_NPLETTRICHGIU)});
			</c:if>
			<c:if test='${not empty requestScope.valoreDITG_DLETTRICHGIU}'>
				setValue("DITG_DLETTRICHGIU", ${gene:string4Js(requestScope.valoreDITG_DLETTRICHGIU)});
			</c:if>
			<c:if test='${not empty requestScope.valoreDITG_DTERMPRESGIU}'>
				setValue("DITG_DTERMPRESGIU", ${gene:string4Js(requestScope.valoreDITG_DTERMPRESGIU)});
			</c:if>
			<c:if test='${not empty requestScope.valoreDITG_NPRICEZGIU}'>
				setValue("DITG_NPRICEZGIU", ${gene:string4Js(requestScope.valoreDITG_NPRICEZGIU)});
			</c:if>
			<c:if test='${not empty requestScope.valoreDITG_DRICEZGIU}'>
				setValue("DITG_DRICEZGIU", ${gene:string4Js(requestScope.valoreDITG_DRICEZGIU)});
			</c:if>
			<c:if test='${not empty requestScope.valoreDITG_OGGRICEZGIU}'>
				setValue("DITG_OGGRICEZGIU", ${gene:string4Js(requestScope.valoreDITG_OGGRICEZGIU)});
			</c:if>
		</c:if>
		<c:if test='${tmpPaginaAttivaWizard eq step1Wizard}'>
			<c:if test='${not empty requestScope.valoreDITG_NPROFF}'>
				setValue("DITG_NPROFF", ${gene:string4Js(requestScope.valoreDITG_NPROFF)});
			</c:if>
		</c:if>
	</c:if>

		// Funzione eseguita all'apertura della popup per copiare i valori dalla 
		// finestra padre, visto che nella finestra padre possono essere stati 
		// modificati dei valori e non ancora salvati
		function copiaCampiDaOpener(){
	<c:choose>
		<c:when test='${tmpPaginaAttivaWizard ne step8Wizard}' >
			//var openerAMMGAR = window.opener.getValue("V_DITGAMMIS_AMMGAR_${param.indiceRiga}");
			var openerImpsocazi = window.opener.getValue("DITG_IMPSICAZI_${param.indiceRiga}");
			setValue("DITG_IMPSICAZI", openerImpsocazi);
			var openerImpmano = window.opener.getValue("DITG_IMPMANO_${param.indiceRiga}");
			setValue("DITG_IMPMANO", openerImpmano);
			<c:if test='${not empty requestScope.valoreDITG_PERCMANO}'>
				setValue("DITG_PERCMANO", ${gene:string4Js(requestScope.valoreDITG_PERCMANO)});
			</c:if>
			var openerIMPPERM = window.opener.getValue("DITG_IMPPERM_${param.indiceRiga}");
			setValue("DITG_IMPPERM", openerIMPPERM);
			var openerIMPCANO = window.opener.getValue("DITG_IMPCANO_${param.indiceRiga}");
			setValue("DITG_IMPCANO", openerIMPCANO);
			var tmp = null;
			
			if(globalOpenerAMMGAR == "" || (globalOpenerAMMGAR != "" && "1345810".indexOf(globalOpenerAMMGAR) >= 0)){
				setValue("V_DITGAMMIS_AMMGAR", "");
				setValue("V_DITGAMMIS_DETMOTESCL", "");
				if(window.opener.getValue("V_DITGAMMIS_MOTIVESCL_${param.indiceRiga}") != 98)
					setValue("V_DITGAMMIS_MOTIVESCL", "");
				//alert("globalOpenerAMMGAR = '" + globalOpenerAMMGAR + "', new Number(globalOpenerAMMGAR) = " + new Number(globalOpenerAMMGAR));
				if(globalOpenerAMMGAR == "")
					tmp = "";
				else
					tmp = arrayAmmgar[new Number(globalOpenerAMMGAR)-1][1];
				
				
			
			} else if("2679".indexOf(globalOpenerAMMGAR) >= 0) {
				//alert("globalOpenerAMMGAR = '" + globalOpenerAMMGAR); // + "', new Number(globalOpenerAMMGAR) = " + new Number(globalOpenerAMMGAR));
				setValue("V_DITGAMMIS_AMMGAR", globalOpenerAMMGAR);
				tmp = arrayAmmgar[new Number(globalOpenerAMMGAR)-1][1];
			} else {
				setValue("V_DITGAMMIS_AMMGAR", "");
				setValue("V_DITGAMMIS_DETMOTESCL", "");
				setValue("V_DITGAMMIS_MOTIVESCL", "");
				tmp = "";
				
			}
			if(globalOpenerAMMGAR == "" || (globalOpenerAMMGAR != "" && "1345810".indexOf(globalOpenerAMMGAR) >= 0) || (globalOpenerAMMGAR != "" && "2679".indexOf(globalOpenerAMMGAR) >= 0)){
			<c:if test='${isUlterioriCampiVerifica eq "true" or visPartgarUlterioriCampiOffUnica eq "true" or ((tmpPaginaAttivaWizard eq step6Wizard or tmpPaginaAttivaWizard eq step7Wizard) and tmpIsGaraLottiConOffertaUnica ne true)}'>
				//var tmpPartgar = winOpener.getValue("DITG_PARTGAR_${param.indiceRiga}");
				setValue("DITG_PARTGAR", openerPartgar);
				if(openerPartgar == "1")
					document.getElementById("DITG_PARTGARview").innerHTML = "Si";
				else if(openerPartgar == "2")
					document.getElementById("DITG_PARTGARview").innerHTML = "No";
				else
					document.getElementById("DITG_PARTGARview").innerHTML = "";
			</c:if>
			}
			<c:if test='${tmpPaginaAttivaWizard ne step8Wizard and isUlterioriCampiVerifica ne "true"}'>
				if(document.getElementById("V_DITGAMMIS_AMMGARview")!= null){
					document.getElementById("V_DITGAMMIS_AMMGARview").innerHTML = tmp;
					showObj("V_DITGAMMIS_AMMGARview", true);
				}
			</c:if>
		</c:when>
		<c:when test='${tmpPaginaAttivaWizard eq step8Wizard}'>
			tmp = window.opener.getValue("DITG_CONGRUO_${param.indiceRiga}");
			if(tmp == "1"){
				setValue("DITG_CONGRUO", "1");
				document.getElementById("DITG_CONGRUOview").innerHTML = "Si";
			} else if(tmp == "2"){
				setValue("DITG_CONGRUO", "2");
				document.getElementById("DITG_CONGRUOview").innerHTML = "No";
			} else {
				setValue("DITG_CONGRUO", "");
				document.getElementById("DITG_CONGRUOview").innerHTML = "";
			}
			setValue("DITG_CONGMOT", window.opener.getValue("DITG_CONGMOT_${param.indiceRiga}"));
			
			if(getValue("DITG_CONGRUO") == "1" || getValue("DITG_CONGRUO") == "" || getValue("DITG_CONGRUO") == "0"){
				document.getElementById("DITG_CONGMOT").disabled = true;
				document.getElementById("DITG_CONGALT").disabled = true;
			} else {
				document.getElementById("DITG_CONGMOT").onblur = aggiornaMotivOffertaConGrua;
				document.getElementById("DITG_CONGALT").onblur = aggiornaMotivOffertaConGrua;
			}
		</c:when>
	</c:choose>
			// Copio dall'opener i seguenti campi hidden: garaLottiOmogenea, modalitaAggiudicazioneGara
			setValue("modalitaAggiudicazioneGara", window.opener.getValue("modalitaAggiudicazioneGara"));
			setValue("garaLottiOmogenea", window.opener.getValue("garaLottiOmogenea"));
			
		<c:if test='${datiRiga.V_DITGAMMIS_MOTIVESCL != 98 and datiRiga.V_DITGAMMIS_MOTIVESCL != 99}' >
			// Controllo per bloccare la drop-down delle motivazioni di esclusione dalla gara
			//alert('getValue("V_DITGAMMIS_AMMGAR") = ' + getValue("V_DITGAMMIS_AMMGAR"));
			if(getValue("V_DITGAMMIS_AMMGAR") == "2" || getValue("V_DITGAMMIS_AMMGAR") == "6" || getValue("V_DITGAMMIS_AMMGAR") == "9"){
				document.getElementById("V_DITGAMMIS_MOTIVESCL").disabled = false;
				setValue("V_DITGAMMIS_MOTIVESCL", window.opener.getValue("V_DITGAMMIS_MOTIVESCL_${param.indiceRiga}"));
				document.getElementById("V_DITGAMMIS_DETMOTESCL").disabled = false;
				setValue("V_DITGAMMIS_DETMOTESCL", window.opener.getValue("V_DITGAMMIS_DETMOTESCL_${param.indiceRiga}"));
			} else {
				setValue("V_DITGAMMIS_MOTIVESCL", "");
				document.getElementById("V_DITGAMMIS_MOTIVESCL").disabled = true;
				setValue("V_DITGAMMIS_DETMOTESCL", "");
				document.getElementById("V_DITGAMMIS_DETMOTESCL").disabled = true;
			}
		</c:if>
		<c:if test='${(isUlterioriCampiVerifica eq "true")}'>
			var reqminOpener = winOpener.getValue("DITG_REQMIN_${param.indiceRiga}")
			setValue("DITG_REQMIN", reqminOpener);
			if(reqminOpener == "1"){
				document.getElementById("DITG_REQMINview").innerHTML = "Si";
			} else if(reqminOpener == "2"){
				document.getElementById("DITG_REQMINview").innerHTML = "No";
			} else {
				document.getElementById("DITG_REQMINview").innerHTML = "";
			}
			
		</c:if>
		<c:if test='${tmpPaginaAttivaWizard eq step1Wizard and tmpIsGaraLottiConOffertaUnica ne true}'>
			//PEr le gare inverse, se il campo ammissione risulta bloccato in modifica, poichè la fase è >=5
			//si devono rendere bloccati in modifica i campi MOTIVESCL e DETMOTESCL
			var garaInversaAmmgarBloccato = winOpener.document.getElementById("garaInversaAmmgarBloccato").value;
			if(garaInversaAmmgarBloccato=="true"){
				document.getElementById("V_DITGAMMIS_MOTIVESCL").disabled = true;
				document.getElementById("V_DITGAMMIS_DETMOTESCL").disabled = true;
			}
		</c:if>
		}

		function aggiornaMotivOffertaConGrua(){
			if(getValue("DITG_CONGMOT") != "" || getValue("DITG_CONGALT") != ""){
				//setValue("DITG_CONGRUO", 2);
				document.getElementById("DITG_CONGRUOview").innerHTML = "No";
			} else {
				document.getElementById("DITG_CONGRUOview").innerHTML = "";
			}
		}

		function conferma(){
			<c:if test='${tmpPaginaAttivaWizard eq step1Wizard}'>
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
					winOpener.setValue("DPROFF_FIT_NASCOSTO" + "_${param.indiceRiga}", dproff);
					winOpener.setValue("DPROFF_FIT_MODIFICATO" + "_${param.indiceRiga}", "SI");
				}
			</c:if>
			
			var arrayCampiModificabili = new Array("V_DITGAMMIS_MOTIVESCL", "V_DITGAMMIS_DETMOTESCL", "DITG_CONGRUO", "DITG_CONGMOT", "DITG_IMPSICAZI","DITG_IMPMANO","DITG_IMPPERM","DITG_IMPCANO");  //, "DITG_RICSUB", "DITG_NPRREQ", "DITG_DATREQ", "DITG_ORAREQ", "DITG_MEZREQ", "DITG_ALTNOT", "DITG_CONGALT"
			for(var i=0; i < arrayCampiModificabili.length; i++){
				window.opener.setValue(arrayCampiModificabili[i] + "_${param.indiceRiga}" , getValue(arrayCampiModificabili[i]));
			}
			document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
			//Riabilita in modifica il campo, altrimenti non ne legge il valore e viene erroneamente sbiancato  
			document.getElementById("DITG_CONGALT").disabled = false;
			schedaConferma();
			//window.close();
		}
		
		function settaAnnotazione(moties){
			if(moties == null || moties == ""){
				setValue("V_DITGAMMIS_DETMOTESCL", "");
			} else {
				for(var i=0; i < arrayMotiviEsclusione.length; i++){
					var motivo = arrayMotiviEsclusione[i][0];
					
					if (motivo == moties) {
						setValue("V_DITGAMMIS_DETMOTESCL", arrayMotiviEsclusione[i][1]);
						break;
					}
				}
			}
		}
</c:if>

		function inizializzaPagina(){
		<c:choose>
			<c:when test='${tmpPaginaAttivaWizard eq step1Wizard}'>
				initStep1Wizard();
			</c:when>
			<c:when test='${tmpPaginaAttivaWizard eq step4Wizard}'>
				initStep4Wizard();
			</c:when>
			<c:when test='${tmpPaginaAttivaWizard eq step6Wizard}'>
				initStep6Wizard();
			</c:when>
			<c:when test='${tmpPaginaAttivaWizard eq step7Wizard}'>
				initStep7Wizard();
			</c:when>
			<c:when test='${tmpPaginaAttivaWizard eq step7_5Wizard}'>
				initStep7_5Wizard();
			</c:when>
			<c:when test='${tmpPaginaAttivaWizard eq step8Wizard}'>
				initStep8Wizard();
			</c:when>
		</c:choose>
		<c:if test='${modo eq "MODIFICA"}' >
			copiaCampiDaOpener();
		</c:if>
		<c:if test='${modo eq "MODIFICA" && tmpPaginaAttivaWizard ne step8Wizard}' >
			if(getValue("V_DITGAMMIS_MOTIVESCL")==98 || getValue("V_DITGAMMIS_MOTIVESCL")==99)
				document.getElementById("V_DITGAMMIS_MOTIVESCL").disabled = true;
		</c:if>
		<c:if test="${tmpPaginaAttivaWizard ne step2Wizard }">
			showObj("rowDITG_PARTGAR", false);
		</c:if>
		}

	<c:set var="codiceJSComune">
				
			//alert("globalOpenerAMMGAR = '" + globalOpenerAMMGAR + "'\nopenerMOTIES = '" + globalOpenerMOTIES + "'");
			if(globalOpenerAMMGAR == "" || (globalOpenerAMMGAR != "" && "2569".indexOf(globalOpenerAMMGAR) < 0 && globalOpenerMOTIES != "98" && globalOpenerMOTIES != "99")){
				//alert("codiceJSComune 1");
				showObj("rowV_DITGAMMIS_MOTIVESCL", false);
				showObj("rowV_DITGAMMIS_DETMOTESCL", false);
				
			}
			
			//Se la gara è inversa e sono nella pagina di apertura doc amministrativa e la ditta non è esclusa nella fase corrente, allora 
			//le sezioni verifica requisiti e comunicazione esclusione sono sempre visibili, altrimenti rimane la gestione attuale
			
			var gestioneGaraInversaDocAmm=false;
			var amminversa;
			<c:if test="${garaInversa eq '1' and tmpPaginaAttivaWizard eq step1Wizard}">
				if(globalOpenerAMMGAR!="2" && globalOpenerAMMGAR!="6" && globalOpenerAMMGAR!="9")
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
			
			if(globalOpenerAMMGAR == "" || (globalOpenerAMMGAR != "" && "2569".indexOf(globalOpenerAMMGAR) < 0 && globalOpenerMOTIES != "98" && globalOpenerMOTIES != "99")){
				if(amminversa!=2)
					visualizzaSezioneAttoAmmissioneEsclusione(false);
			}
			
			
			if(globalOpenerAMMGAR == "" || (globalOpenerAMMGAR != "" && "346".indexOf(globalOpenerAMMGAR) < 0)){
				visualizzaSezioneVerificaRequisiti(false);
			}
		
	</c:set>

		function initStep1Wizard(){
			//alert("initStep1Wizard");
			${codiceJSComune}
						
			<c:choose>
			<c:when test='${modoAperturaScheda eq "MODIFICA"}' >
				var dproff=window.opener.getValue("DPROFF_FIT_NASCOSTO_${param.indiceRiga}");
				var dproff_mod=window.opener.getValue("DPROFF_FIT_MODIFICATO_${param.indiceRiga}");
				if((dproff==null || dproff=="") && (dproff_mod == "NO" || dproff_mod == ""))
					dproff = getValue("DITG_DPROFF");
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
			
			
		}

		function initStep4Wizard(){
			//alert("initStep4Wizard");
			${codiceJSComune}
		}

		function initStep6Wizard(){
			//alert("initStep6Wizard");
			${codiceJSComune}
		}

		function initStep7Wizard(){
			//alert("initStep7Wizard");
			${codiceJSComune}
			/*
			if(! winOpener.isObjShow("DITG_PARTGAR_${param.indiceRiga}")){
				showObj("rowDITG_PARTGAR", false);
			}
			*/
			<c:if test='${modo eq "MODIFICA" && bustalotti eq "2"}' >
				if(openerPartgar == 2){
					if(document.getElementById("DITG_IMPSICAZI")!=null)
						document.getElementById("DITG_IMPSICAZI").disabled = true;
					if(document.getElementById("DITG_IMPMANO")!=null)
						document.getElementById("DITG_IMPMANO").disabled = true;
					if(document.getElementById("DITG_PERCMANO")!=null)
						document.getElementById("DITG_PERCMANO").disabled = true;
					if(document.getElementById("DITG_IMPPERM")!=null)
						document.getElementById("DITG_IMPPERM").disabled = true;
					if(document.getElementById("DITG_IMPCANO")!=null)
						document.getElementById("DITG_IMPCANO").disabled = true;
				}
			</c:if>
			
		}
	
		function initStep7_5Wizard(){
			//alert("initStep1Wizard");
			${codiceJSComune}
		}
		
		function initStep8Wizard(){
			//alert("initStep8Wizard");
			if(isOffertaCongrua == "true"){
				showObj("rowV_DITGAMMIS_MOTIVESCL", false);
				showObj("rowV_DITGAMMIS_DETMOTESCL", false);
				visualizzaSezioneAttoAmmissioneEsclusione(false);
			}
			visualizzaSezioneVerificaRequisiti(false);
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

		inizializzaPagina();
	
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
	
	<c:if test='${tmpPaginaAttivaWizard eq step1Wizard and  modo eq "MODIFICA"}'>
		//Nella tabella grigliaDataProt è stato inserito il td con classe='riempimento' che ha il solo
		//scopo di riempire una parte della tabella in modo da fare risultare più piccoli e quindi più
		//vicini i campi con l'ora e la data
		$('table.grigliaDataProt tr td.valore-dato').css('width','200');
		$('table.grigliaDataProt tr td.riempimento').css('width','40%');
	</c:if>
	
	</gene:javaScript>
</gene:template>
</div>