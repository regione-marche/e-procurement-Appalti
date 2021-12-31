<%
/*
 * Created on: 10-nov-2008
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


<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<jsp:include page="../gare/fasiRicezione/defStepWizardFasiRicezione.jsp" />

<c:choose>
	<c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.inserimentoDitteSMAT")}'>
		<c:set var="inserimentoDitteSMAT" value="SI"/>
	</c:when>
	<c:otherwise>
		<c:set var="inserimentoDitteSMAT" value="NO"/>
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
	<c:when test='${not empty param.isGaraLottiConOffertaUnica}'>
		<c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
	</c:when>
	<c:otherwise>
		<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.faseRicezione}'>
		<c:set var="faseRicezione" value="${param.faseRicezione}" />
	</c:when>
	<c:otherwise>
		<c:set var="faseRicezione" value="${faseRicezione}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.inserimentoDitteIterSemplificato}'>
		<c:set var="inserimentoDitteIterSemplificato" value="${param.inserimentoDitteIterSemplificato}" />
	</c:when>
	<c:otherwise>
		<c:set var="inserimentoDitteIterSemplificato" value="${inserimentoDitteIterSemplificato}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.iscrizioneRT}'>
		<c:set var="iscrizioneRT" value="${param.iscrizioneRT}" />
	</c:when>
	<c:otherwise>
		<c:set var="iscrizioneRT" value="${iscrizioneRT}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipoImpresa}'>
		<c:set var="tipoImpresa" value="${param.tipoImpresa}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoImpresa" value="${tipoImpresa}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codiceDitta}'>
		<c:set var="codiceDitta" value="${param.codiceDitta}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceDitta" value="${codiceDitta}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.numeroGara}'>
		<c:set var="numeroGara" value="${param.numeroGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="numeroGara" value="${numeroGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.offertaRT}'>
		<c:set var="offertaRT" value="${param.offertaRT}" />
	</c:when>
	<c:otherwise>
		<c:set var="offertaRT" value="${offertaRT}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codiceGara}'>
		<c:set var="codiceGara" value="${param.codiceGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${codiceGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.initAcquisizione}'>
		<c:set var="initAcquisizione" value="${param.initAcquisizione}" />
	</c:when>
	<c:otherwise>
		<c:set var="initAcquisizione" value="${initAcquisizione}" />
	</c:otherwise>
</c:choose>

<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "IMPR", "CODIMP")}'/>


<c:set var="fnucase" value='${gene:callFunction("it.eldasoft.gene.tags.utils.functions.GetUpperCaseDBFunction", "")}' />
	
<div style="width:97%;">


<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="DITG-scheda-popup">
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>	
	</gene:redefineInsert>
	
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="corpo">
		<c:choose>
			<c:when test="${offertaRT eq '1' }">
				<gene:setString name="titoloMaschera" value='Presenta offerta in raggruppamento temporaneo' />
			</c:when>
			<c:otherwise>
				<gene:setString name="titoloMaschera" value='Aggiungi ditta da anagrafica' />
			</c:otherwise>
		</c:choose>
		<gene:formScheda entita="DITG" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiRicezione" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInitInserimentoDitta">
			<gene:campoScheda campo="CODGAR5" visibile="false" />
			<gene:campoScheda campo="NGARA5"  visibile="false" />
			<gene:campoScheda campo="NPROGG" visibile="false" defaultValue="${gene:if(offertaRT eq '1', nprogg,'') }"/>
			<c:if test="${isOfferteDistinte }">
				<gene:campoScheda campo="OFFERTALOTTI" title="Applica anche agli altri lotti della gara?" campoFittizio="true" defaultValue="2" definizione="T2;" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoNoSiSenzaNull" />
				<gene:campoScheda addTr="false">
                	<tr id="rowVUOTO" >
                    	<td class="valore-dato" colspan="2">   </td>
                    </tr>
                </gene:campoScheda >
				
			</c:if>
			<c:choose>
				<c:when test="${offertaRT eq '1' }">
					<c:choose>
						<c:when test="${tipoImpresa <= 5 }">
							<c:set var="tipoRTI" value="3"/>
						</c:when>
						<c:otherwise>
							<c:set var="tipoRTI" value="10"/>
						</c:otherwise>
					</c:choose>
					<c:set var="isRTI" value="1"/>
					<gene:campoScheda campo="RTI" title="Raggruppamento temporaneo?" campoFittizio="true" value="${tipoRTI}" visibile="false" definizione="T2;" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoRTI"/>
					<gene:campoScheda campo="RTI_1" title="Raggruppamento temporaneo?" campoFittizio="true" value="${tipoRTI}" definizione="T2;" modificabile="false" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoRTI"/>
				</c:when>
				<c:otherwise>
					<gene:campoScheda campo="RTI" title="Raggruppamento temporaneo?" campoFittizio="true" value="${gene:if(isRTI eq 0,0,tipoRTI)}" definizione="T2;" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoRTI"/>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test='${inserimentoDitteSMAT eq "SI" and isRTI eq 0}'>
					<gene:archivio titolo="ditte"
						lista='gare/v_impr_smat/v_impr_smat-lista-popup.jsp'
						scheda=''
						schedaPopUp=''
						campi="V_IMPR_SMAT.CODIMP;V_IMPR_SMAT.NOMIMP;V_IMPR_SMAT.CFIMP;V_IMPR_SMAT.PIVIMP;V_IMPR_SMAT.ID_SEDE;V_IMPR_SMAT.ID_FORNITORE;V_IMPR_SMAT.IS_IMPRESA_OA"
						chiave=""
						where="(V_IMPR_SMAT.TIPIMP <>3 and V_IMPR_SMAT.TIPIMP <>10) or V_IMPR_SMAT.TIPIMP is null"
						formName="formDitteSMATGara"
						inseribile="false">
						<gene:campoScheda campo="DITTAO_SMAT" campoFittizio="true" definizione="T20;;;;DITTAO" obbligatorio="true" />
						<gene:campoScheda title="Ragione sociale" campo="NOMEST" campoFittizio="true" definizione="T2000" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.NOMEST")}' />
						<gene:campoScheda title="Codice fiscale" campo="CFIMP" campoFittizio="true" definizione="T16" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CFIMP")}' />
						<gene:campoScheda title="Partita I.V.A." campo="PIVIMP" campoFittizio="true" definizione="T16" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.PIVIMP")}' />
						<gene:campoScheda campo="ID_SEDE" campoFittizio="true"  definizione="N10" visibile="false"/>
						<gene:campoScheda campo="ID_FORNITORE" campoFittizio="true"  definizione="T10" visibile="false"/>
						<gene:campoScheda campo="IS_IMPRESA_OA" campoFittizio="true"  definizione="N1" visibile="false"/>
					</gene:archivio>
					<gene:campoScheda campo="DITTAO" entita="DITG" visibile="false" />
				</c:when>
				<c:when test='${isRTI eq 0}'>
					<gene:archivio titolo="ditte"
						lista='gene/impr/impr-lista-popup.jsp?abilitaNuovo=1'
						scheda=''
						schedaPopUp=''
						campi="IMPR.CODIMP;IMPR.NOMEST;IMPR.CFIMP;IMPR.PIVIMP;IMPR.CGENIMP"
						chiave=""
						where="(IMPR.TIPIMP <>3 and IMPR.TIPIMP <>10) or IMPR.TIPIMP is null"
						formName="formDitteGara"
						inseribile="true">
						<gene:campoScheda campo="DITTAO" entita="DITG" obbligatorio="true" />
						<gene:campoScheda title="Ragione sociale" campo="NOMEST" campoFittizio="true" definizione="T2000;;;;NOMIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.NOMEST")}' />
						<gene:campoScheda title="Codice fiscale" campo="CFIMP" campoFittizio="true" definizione="T16" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CFIMP")}'/>
						<gene:campoScheda title="Partita I.V.A." campo="PIVIMP" campoFittizio="true" definizione="T16" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.PIVIMP")}' />
						<gene:campoScheda title="Codice dell'Anagrafico Generale" campo="CGENIMP" campoFittizio="true" definizione="T20" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CGENIMP") && fn:contains(listaOpzioniDisponibili, "OP127#")}'/>
					</gene:archivio>
				</c:when>
				<c:when test='${inserimentoDitteSMAT eq "SI" and raggruppamentoSelezionato ne "SI"}'>
					<gene:campoScheda campo="DITTAO" keyCheck="true" title="Codice raggruppamento temporaneo" entita="DITG" obbligatorio="${isCodificaAutomatica eq 'false'}"  />
							<gene:campoScheda campo="DITTAO_FIT" title="Codice raggruppamento temporaneo" campoFittizio="true" definizione="T10;;;;CODIMP" modificabile="false" />
							<gene:campoScheda title="Ragione sociale" campo="NOMEST" speciale="true" campoFittizio="true" definizione="T2000;;;NOTE;NOMIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.NOMEST")}'  >
								<gene:popupCampo titolo="Selezione raggruppamento da archivio ditte" href="archivioRTI('IMPR')" />
							</gene:campoScheda>
							<gene:archivio titolo="ditte"
								lista='gare/v_impr_smat/v_impr_smat-lista-popup.jsp'
								scheda=''
								schedaPopUp=''
								campi="V_IMPR_SMAT.CODIMP;V_IMPR_SMAT.NOMEST;V_IMPR_SMAT.CFIMP;V_IMPR_SMAT.PIVIMP;V_IMPR_SMAT.ID_SEDE;V_IMPR_SMAT.ID_FORNITORE;V_IMPR_SMAT.IS_IMPRESA_OA"
								chiave=""
								where="(V_IMPR_SMAT.TIPIMP <>3 and V_IMPR_SMAT.TIPIMP <>10) or V_IMPR_SMAT.TIPIMP is null"
								formName="formDitteGara"
								inseribile="true">
								<gene:campoScheda title="Codice ditta mandataria" campo="CODDIC_SMAT" campoFittizio="true" definizione="T10;;;;CODDIC" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.RAGIMP.CODDIC")}' value="${codiceDitta}" modificabile="${offertaRT ne '1' }"/>
								<gene:campoScheda title="Ragione sociale" campo="NOMEST1" campoFittizio="true" definizione="T2000;;;;NOMIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.NOMEST")}' value="${nomestMandataria}" modificabile="${offertaRT ne '1' }"/>
								<gene:campoScheda title="Codice fiscale" campo="CFIMP" campoFittizio="true" definizione="T16" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CFIMP")}' value="${CfMandataria}" modificabile="${offertaRT ne '1' }"/>
								<gene:campoScheda title="Partita I.V.A." campo="PIVIMP" campoFittizio="true" definizione="T16" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.PIVIMP")}' value="${PivaMandataria}" modificabile="${offertaRT ne '1' }"/>
								<gene:campoScheda campo="ID_SEDE" campoFittizio="true"  definizione="N10" visibile="false"/>
								<gene:campoScheda campo="ID_FORNITORE" campoFittizio="true"  definizione="T10" visibile="false"/>
								<gene:campoScheda campo="IS_IMPRESA_OA" campoFittizio="true"  definizione="N1" visibile="false"/>
							</gene:archivio>
							<gene:campoScheda campo="CODDIC" campoFittizio="true" definizione="T10" visibile='false' />
				</c:when>
				
				
				
				<c:otherwise>
														
					<c:choose>
						<c:when test='${raggruppamentoSelezionato ne "SI"}'>
							<gene:campoScheda campo="DITTAO" keyCheck="true" title="Codice raggruppamento temporaneo" entita="DITG" obbligatorio="${isCodificaAutomatica eq 'false'}"  />
							<gene:campoScheda campo="DITTAO_FIT" title="Codice raggruppamento temporaneo" campoFittizio="true" definizione="T10;;;;CODIMP" modificabile="false" />
							<gene:campoScheda title="Ragione sociale" campo="NOMEST" speciale="true" campoFittizio="true" definizione="T2000;;;NOTE;NOMIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.NOMEST")}'  >
								<gene:popupCampo titolo="Selezione raggruppamento da archivio ditte" href="archivioRTI('IMPR')" />
							</gene:campoScheda>
							<gene:archivio titolo="ditte"
								lista='gene/impr/impr-lista-popup.jsp?abilitaNuovo=1'
								scheda=''
								schedaPopUp=''
								campi="IMPR.CODIMP;IMPR.NOMEST;IMPR.CFIMP;IMPR.PIVIMP;IMPR.CGENIMP"
								chiave=""
								where="(IMPR.TIPIMP <>3 and IMPR.TIPIMP <>10) or IMPR.TIPIMP is null"
								formName="formDitteGara"
								inseribile="true">
								<gene:campoScheda title="Codice ditta mandataria" campo="CODDIC" campoFittizio="true" definizione="T10;;;;CODDIC" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.RAGIMP.CODDIC")}' value="${codiceDitta}" modificabile="${offertaRT ne '1' }"/>
								<gene:campoScheda title="Ragione sociale" campo="NOMEST1" campoFittizio="true" definizione="T2000;;;;NOMIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.NOMEST")}' value="${nomestMandataria}" modificabile="${offertaRT ne '1' }"/>
								<gene:campoScheda title="Codice fiscale" campo="CFIMP" campoFittizio="true" definizione="T16" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CFIMP")}' value="${CfMandataria}" modificabile="${offertaRT ne '1' }"/>
								<gene:campoScheda title="Partita I.V.A." campo="PIVIMP" campoFittizio="true" definizione="T16" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.PIVIMP")}' value="${PivaMandataria}" modificabile="${offertaRT ne '1' }"/>
								<gene:campoScheda title="Codice dell'Anagrafico Generale" campo="CGENIMP" campoFittizio="true" definizione="T20" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CGENIMP") && fn:contains(listaOpzioniDisponibili, "OP127#")}'/>
							</gene:archivio>
							<gene:campoScheda title="Quota di partecipazione" campo="QUODIC" campoFittizio="true" definizione="F9.5;;;PRC;QUODIC" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.RAGIMP.QUODIC") && isGaraElenco ne "1"}' />
							
						</c:when>
						<c:otherwise>
							<gene:campoScheda campo="DITTAO" keyCheck="true" entita="DITG" obbligatorio="${isCodificaAutomatica eq 'false'}" value="${codiceRaggruppamento}" visibile="false"/>
							<gene:campoScheda campo="DITTAO_FIT" keyCheck="true" title="Codice raggruppamento temporaneo" campoFittizio="true" obbligatorio="${isCodificaAutomatica eq 'false'}" value="${codiceRaggruppamento}" definizione="T10;;;;CODIMP" modificabile="false"/>
							<gene:campoScheda title="Ragione sociale" campo="NOMEST" speciale="true" campoFittizio="true" definizione="T2000;;;;NOMIMP" value="${ragSocRaggruppamento}" visibile="false"/>
							<gene:campoScheda title="Ragione sociale" campo="NOMEST_FIT" speciale="true" campoFittizio="true" definizione="T2000;;;;NOMIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.NOMEST")}' value="${ragSocRaggruppamento}" modificabile="false">
								<gene:popupCampo titolo="Selezione raggruppamento da archivio ditte" href="archivioRTI()" />
							</gene:campoScheda>	
							<gene:campoScheda title="Codice ditta mandataria" campo="CODDIC" campoFittizio="true" definizione="T10;;;;CODDIC" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.RAGIMP.CODDIC")}' value="${codiceMandataria}" modificabile="false"/>
							<gene:campoScheda title="Ragione sociale" campo="NOMEST1" campoFittizio="true" definizione="T2000;;;;NOMIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.NOMEST")}' value="${ragSocMandataria}" modificabile="false"/>
							<gene:campoScheda title="Codice fiscale" campo="CFIMP" campoFittizio="true" definizione="T16;;;;CFIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CFIMP")}' value="${codfiscMandataria}" modificabile="false"/>
							<gene:campoScheda title="Partita I.V.A." campo="PIVIMP" campoFittizio="true" definizione="T16;;;;PIVIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.PIVIMP")}' value="${pivaMandataria}" modificabile="false"/>
							<gene:campoScheda title="Quota di partecipazione" campo="QUODIC" campoFittizio="true" definizione="F9.5;;;PRC;QUODIC" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.RAGIMP.QUODIC")&& isGaraElenco ne "1"}'  modificabile="false"/>
							
						</c:otherwise>
					</c:choose>
					
					
					
				</c:otherwise>
			</c:choose>
			
			<gene:campoScheda campo="NOMIMO" entita="DITG" visibile="false"/>
			
			
			<c:choose>
				<c:when test="${isGaraElenco eq '1'}">
					<c:set var="tipoDomanda" value="iscrizione"/>
				</c:when>
				<c:otherwise>
					<c:set var="tipoDomanda" value="partecipazione"/>
				</c:otherwise>
			</c:choose>
			<gene:campoScheda campo="DRICIND" title="Data presentaz. domanda ${tipoDomanda }" obbligatorio="${isGaraElenco eq '1'}"/>
			<gene:campoScheda campo="ORADOM" obbligatorio="${isGaraElenco eq '1'}"/>
			<gene:campoScheda campo="NPRDOM" title="N.protocollo"/>
			<gene:campoScheda campo="DPRDOM" visibile="false"/>
			<gene:campoScheda campo="DPRDOM_FIT_NASCOSTO" campoFittizio="true" definizione="T25;0;;" value="" visibile="false"/>
			<c:if test="${faseRicezione eq step1Wizard}">
				<gene:campoScheda campoFittizio="true" addTr="false">
					<td class="etichetta-dato">Data protocollo</td>
					<td class="valore-dato">
						<table id="tabellaDataProtPart" class="grigliaDataProt" style="width: 99%">
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
			<gene:campoScheda campo="MEZDOM" />
			<gene:campoScheda campo="PLIDOM" />
			<gene:campoScheda campo="NOTPDOM" />
			
			<gene:campoScheda campo="DATOFF" defaultValue="${datoff}"/>
			<gene:campoScheda campo="ORAOFF" defaultValue="${oraff}"/>
			<gene:campoScheda campo="NPROFF" defaultValue="${nproff}"/>
			<gene:campoScheda campo="DPROFF" visibile="false"/>
			<gene:campoScheda campo="DPROFF_FIT_NASCOSTO" campoFittizio="true" definizione="T25;0;;" value="" visibile="false"/>
			<c:if test="${faseRicezione eq step5Wizard}">
			<gene:campoScheda campoFittizio="true" addTr="false">
				<td class="etichetta-dato">Data protocollo</td>
				<td class="valore-dato">
					<table id="tabellaDataProtOff" class="grigliaDataProt" style="width: 99%;">
			</gene:campoScheda>
			<gene:campoScheda addTr="false" campoFittizio="true" title="Data" campo="DATAOFF" definizione="D;0;;DATA_ELDA;" defaultValue="${dproffData}"/>
			<gene:campoScheda addTr="false" campoFittizio="true" title="Ora" campo="ORAOFF"  definizione="T8;0;;ORA;"  defaultValue="${dproffOra}"/>
			<gene:campoScheda addTr="false">
					<td class="riempimento"></td>
				</table>
				</td>
			</gene:campoScheda>
			<gene:fnJavaScriptScheda funzione='sbiancaOra("#DATAOFF#","ORAOFF")' elencocampi='DATAOFF' esegui="false"/>
			</c:if>
			
			<gene:campoScheda campo="MEZOFF" defaultValue="${mezoff}"/>
			<gene:campoScheda campo="PLIOFF" defaultValue="${plioff}"/>
			<gene:campoScheda campo="NOTPOFF" defaultValue="${notpoff}"/>
			<gene:campoScheda campo="NPROTG" visibile="false"/>
			<gene:campoScheda campo="DINVIG" visibile="false"/>
			<gene:campoScheda campo="ALTNOT" />
			<c:if test="${offertaRT eq '1' }">
				<gene:campoScheda campo="ACQUISIZIONE" defaultValue="5" visibile="false"/>
				<gene:campoScheda campo="DITTAINV" defaultValue="${codiceDitta}" visibile="false"/>
				
			</c:if>	
			<c:if test="${not empty initAcquisizione and initAcquisizione ne '' }">
				<gene:campoScheda campo="ACQUISIZIONE" defaultValue="${initAcquisizione }" visibile="false"/>
			</c:if>			
			
			<c:if test='${isRTI eq 1}'>
				<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
				<jsp:param name="entita" value='RAGIMP'/>
				<jsp:param name="chiave" value='${codiceRaggruppamento}'/>
				<jsp:param name="nomeAttributoLista" value='listaRaggruppamenti' />
				<jsp:param name="idProtezioni" value="RAGIMP" />
				<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gene/impr/impr-dettaglioRaggruppamentoDaGare.jsp" />
				<jsp:param name="arrayCampi" value="'RAGIMP_CODDIC_', 'RAGIMP_NOMDIC_','IMPR_CGENIMP_','IMPR_CFIMP_','IMPR_PIVIMP_', 'RAGIMP_QUODIC_'" />
				<jsp:param name="titoloSezione" value="Mandante del raggruppamento" />
				<jsp:param name="titoloNuovaSezione" value="Nuova mandante del raggruppamento" />
				<jsp:param name="descEntitaVociLink" value="ditta mandante del raggruppamento" />
				<jsp:param name="msgRaggiuntoMax" value="e ditte mandanti del raggruppamento" />
				<jsp:param name="usaContatoreLista" value="true"/>
				<jsp:param name="sezioneListaVuota" value="false"/>
				<jsp:param name="funzEliminazione" value="delComponente"/>
				<jsp:param name="raggruppamentoSelezionato" value="${raggruppamentoSelezionato}"/>
				<jsp:param name="isGaraElenco" value="${isGaraElenco}"/>
				</jsp:include>
			</c:if>
			
			<c:if test='${faseRicezione eq step1Wizard}'>
				<gene:fnJavaScriptScheda funzione='checkDataRichiestaDomanda("#DITG_DRICIND#")' elencocampi='DITG_DRICIND' esegui="false"/>
				<gene:fnJavaScriptScheda funzione='checkOraRichiestaDomanda("#DITG_ORADOM#")' elencocampi='DITG_ORADOM' esegui="false"/>
			</c:if>
			
			<c:if test='${faseRicezione eq step5Wizard}'>
				<gene:fnJavaScriptScheda funzione='checkDataRicezioneOfferta("#DITG_DATOFF#")' elencocampi='DITG_DATOFF' esegui="false"/>
				<gene:fnJavaScriptScheda funzione='checkOraRicezioneOfferta("#DITG_ORAOFF#")' elencocampi='DITG_ORAOFF' esegui="false"/>
			</c:if>
			
			<gene:fnJavaScriptScheda funzione='aggiornaTitoli("#RTI#")' elencocampi='RTI' esegui="false"/>
						
			<input type="hidden" name="WIZARD_PAGINA_ATTIVA" id="WIZARD_PAGINA_ATTIVA" value="" />
			<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
			<input type="hidden" name="inserimentoDitteSMAT" id="inserimentoDitteSMAT" value="NO" />
			<input type="hidden" name="isGaraElenco" id="isGaraElenco" value="${isGaraElenco}" />
			<input type="hidden" name="faseRicezione" id="faseRicezione" value="${faseRicezione}" />
			<input type="hidden" name="inserimentoDitteIterSemplificato" id="inserimentoDitteIterSemplificato" value="${inserimentoDitteIterSemplificato}" />
			<input type="hidden" name="DTEOFF" value="" />
			<input type="hidden" name="OTEOFF" value="" />
			<input type="hidden" name="DTEPAR" value="" />
			<input type="hidden" name="OTEPAR" value="" />
			<input type="hidden" name="isRTI" id="isRTI" value="0" />
			<input type="hidden" name="codiceRaggruppamento" id="codiceRaggruppamento" value="" />
			<input type="hidden" name="tipoRTI" id="tipoRTI" value="" />
			<input type="hidden" name="iscrizioneRT" id="iscrizioneRT" value="${iscrizioneRT }" />
			<input type="hidden" name="quotaPartecip" id="quotaPartecip" value="" />
			<input type="hidden" name="tipoImpresa" id="tipoImpresa" value="${tipoImpresa }" />
			<input type="hidden" name="codiceDitta" id="codiceDitta" value="${codiceDitta }" />
			<input type="hidden" name="numeroGara" id="numeroGara" value="${numeroGara }" />
			<input type="hidden" name="offertaRT" id="offertaRT" value="${offertaRT }" />
			<input type="hidden" name="codiceGara" id="codiceGara" value="${codiceGara }" />
			<input type="hidden" name="isOfferteDistinte" id="isOfferteDistinte" value="${isOfferteDistinte }" />	
			<input type="hidden" name="initAcquisizione" id="initAcquisizione" value="${initAcquisizione }" />													
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConfermaSalvataggio();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:window.close();">&nbsp;
				</td>
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>

	<gene:javaScript>
	
	//Nel caso di elenco se iscrizioneRT != 1 si deve nascondere il campo RTI
	<c:if test="${iscrizioneRT ne 1 && isGaraElenco eq '1'}">
		showObj("rowRTI", false);
	</c:if>
	
	<c:if test='${inserimentoDitteSMAT ne "SI" && raggruppamentoSelezionato eq "SI"}'>
		var quotaPart = "${partecipazioneMandataria }";
		if(quotaPart!=null && quotaPart!=""){
			if(quotaPart.indexOf(".") >-1){
				quotaPart = quotaPart.replace(".",",");
				var split = quotaPart.split(",");
				if(split.length>1){
					var cifreDec = split[1];
					if(cifreDec=='0' || cifreDec == '00')
						quotaPart = split[0];
				}
			}	
			quotaPart += " %";
		}
		setValue("QUODIC", quotaPart);
	</c:if>
	
	document.forms[0].jspPathTo.value="gare/ditg/ditg-schedaPopup-insert.jsp";
	var openerKeyParent = window.opener.document.forms[0].keyParent.value;
	setValue("DITG_NGARA5", openerKeyParent.substr(openerKeyParent.indexOf(":")+1));
	setValue("WIZARD_PAGINA_ATTIVA", window.opener.getValue("WIZARD_PAGINA_ATTIVA"));
	var winOpener = window.opener;
	setValue("DTEOFF", winOpener.getValue("DTEOFF"));
	setValue("OTEOFF", winOpener.getValue("OTEOFF"));
	setValue("DTEPAR", winOpener.getValue("DTEPAR"));
	setValue("OTEPAR", winOpener.getValue("OTEPAR"));
	<c:if test='${isRTI eq 1 and raggruppamentoSelezionato ne "SI"}'>
		var isCodificaAutomatica = "${isCodificaAutomatica}";
		if (isCodificaAutomatica == 'true' && document.getElementById('DITG_DITTAO')!=null){
		 	//document.getElementById('DITG_DITTAO').disabled = true;
		 	showObj("rowDITG_DITTAO", false);
		 	showObj("rowDITTAO_FIT", true);
		}else{
			showObj("rowDITG_DITTAO", true);
			showObj("rowDITTAO_FIT", false);
		}
	</c:if>
	
	//bloccoCampi();
	<c:if test='${raggruppamentoSelezionato eq "SI"}'>
		showObj("rowLinkAddRAGIMP", false);
	</c:if>
<c:choose>
	<c:when test='${faseRicezione eq step1Wizard}'>
		showObj("rowDITG_NPROFF", false);
		showObj("rowDITG_DATOFF", false);
		showObj("rowDITG_ORAOFF", false);
		showObj("rowDITG_MEZOFF", false);
		//showObj("rowDITG_DINVIG", false);
		//showObj("rowDITG_NPROTG", false);
		showObj("rowDITG_PLIOFF", false);
		showObj("rowDITG_NOTPOFF", false);
		setValue("DITG_NPROFF", "");
		setValue("DITG_DATOFF", "");
		setValue("DITG_ORAOFF", "");
		setValue("DITG_MEZOFF", "");
		setValue("DITG_DINVIG", "");
		setValue("DITG_NPROTG", "");
		setValue("DITG_PLIOFF", "");
		setValue("DITG_NOTPOFF", "");
	</c:when>
	<c:when test='${faseRicezione eq step3Wizard}'>
		showObj("rowDITG_NPRDOM", false);
		showObj("rowDITG_DRICIND", false);
		showObj("rowDITG_ORADOM", false);
		showObj("rowDITG_MEZDOM", false);
		showObj("rowDITG_NPROFF", false);
		showObj("rowDITG_DATOFF", false);
		showObj("rowDITG_ORAOFF", false);
		showObj("rowDITG_MEZOFF", false);
		showObj("rowDITG_PLIOFF", false);
		showObj("rowDITG_NOTPOFF", false);
		showObj("rowDITG_PLIDOM", false);
		showObj("rowDITG_NOTPDOM", false);
		setValue("DITG_NPRDOM" , "");
		setValue("DITG_DRICIND", "");
		setValue("DITG_ORADOM" , "");
		setValue("DITG_MEZDOM" , "");
		setValue("DITG_NPROFF", "");
		setValue("DITG_DATOFF", "");
		setValue("DITG_ORAOFF", "");
		setValue("DITG_MEZOFF", "");
		setValue("DITG_PLIOFF", "");
		setValue("DITG_NOTPOFF", "");
		setValue("DITG_PLIDOM", "");
		setValue("DITG_NOTPDOM", "");
	</c:when>
	<c:when test='${faseRicezione eq step5Wizard}'>
		showObj("rowDITG_NPRDOM", false);
		showObj("rowDITG_DRICIND", false);
		showObj("rowDITG_ORADOM", false);
		showObj("rowDITG_MEZDOM", false);		
		//showObj("rowDITG_DINVIG", false);
		//showObj("rowDITG_NPROTG", false);
		showObj("rowDITG_PLIDOM", false);
		showObj("rowDITG_NOTPDOM", false);
		setValue("DITG_NPRDOM" , "");
		setValue("DITG_DRICIND", "");
		setValue("DITG_ORADOM" , "");
		setValue("DITG_MEZDOM" , "");
		setValue("DITG_DINVIG", "");
		setValue("DITG_NPROTG", "");
		setValue("DITG_PLIDOM", "");
		setValue("DITG_NOTPDOM", "");
	</c:when>
</c:choose>

	<c:if test='${not empty RISULTATO and RISULTATO eq "OK"}' >
		window.opener.document.forms[0].pgSort.value = "";
		window.opener.document.forms[0].pgLastSort.value = "";
		window.opener.document.forms[0].pgLastValori.value = "";
		window.opener.bloccaRichiesteServer();
		window.opener.listaVaiAPagina(0);
		window.close();
	</c:if>
	
	function schedaConfermaSalvataggio(){
		<c:if test='${faseRicezione eq step1Wizard}'>
				var data = getValue("DATAPART");
				var ora = getValue("ORAPART");
				<c:choose>
					<c:when test="${isGaraElenco eq '1'}">
						var tipoDomanda = "iscrizione";
					</c:when>
					<c:otherwise>
						var tipoDomanda = "partecipazione";
					</c:otherwise>
				</c:choose>
				if((data!=null && data!="") && (ora==null || ora =="" )){
					alert("Non è possibile procedere, deve essere inserita l'ora del protocollo presentazione della domanda di " +tipoDomanda);
					return;
				}
				if((data==null || data=="") && (ora!=null && ora !="" )){
					alert("Non è possibile procedere, non può essere inserita l'ora del protocollo presentazione della domanda di " +tipoDomanda + " in mancanza della data");
					return;
				}
				var dprdom = "";
				if(data!=null && ora!=null && data!="" && ora !="")
					dprdom = data + " " + ora + ":00";
				setValue("DITG_DPRDOM", dprdom);
				setValue("DPRDOM_FIT_NASCOSTO", dprdom);
		</c:if>
		
		<c:if test='${faseRicezione eq step5Wizard}'>
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
				var dproff = "";
				if(data!=null && ora!=null && data!="" && ora !="")
					dproff = data + " " + ora + ":00";
				setValue("DITG_DPROFF", dproff);
				setValue("DPROFF_FIT_NASCOSTO", dproff);
		</c:if>
		
		var nomest = getValue("NOMEST");
		setValue("DITG_NOMIMO",nomest);
		
		<c:if test='${inserimentoDitteSMAT ne "SI" && raggruppamentoSelezionato eq "SI"}'>
			setValue("QUODIC", "");
			var quotaPartecip="${quotaPartecip}";
			//document.getElementById('quotaPartecip').value= quotaPartecip;
			setValue("quotaPartecip", quotaPartecip);
		</c:if>
		
		<c:if test='${isRTI eq 1}'>
			setValue("isRTI","1");
			setValue("tipoRTI",${tipoRTI });
			setValue("codiceRaggruppamento","${codiceRaggruppamento }");
			document.getElementById('DITG_DITTAO').disabled = false;
			//Si deve controllare l'unicità dei codici della mandataria e delle componenti
			var codiceMandataria = getValue("CODDIC");
			<c:if test='${inserimentoDitteSMAT eq "SI"}'>
                 codiceMandataria = getValue("CODDIC_SMAT");
            </c:if>
			var messaggio = "Sono state definite pi&ugrave; ditte componenti il raggruppamento con lo stesso codice (";
			var continua = true;
			
			for(var i=1; i < maxIdRAGIMPVisualizzabile; i++){
				var codiceImpresaRagimp = getValue("RAGIMP_CODDIC_" + i);
				if(document.getElementById("rowtitoloRAGIMP_" + i).style.display != "none"){		
					if(codiceImpresaRagimp != null && codiceImpresaRagimp != ""){
						if(codiceMandataria!=null && codiceMandataria!="" && codiceMandataria==codiceImpresaRagimp){
							continua = false;
							outMsg(messaggio + codiceImpresaRagimp + ")", "ERR");
							onOffMsg();
						}else{
						
							for(var jo=(i+1); jo <= maxIdRAGIMPVisualizzabile; jo++){
								if(document.getElementById("rowtitoloRAGIMP_" + jo).style.display != "none" && codiceImpresaRagimp == getValue("RAGIMP_CODDIC_" + jo)){
									continua = false;
									outMsg(messaggio + codiceImpresaRagimp + ")", "ERR");
									onOffMsg();
								}
							}
						}
					}
				}
			}
			if(!continua){
				document.getElementById('DITG_DITTAO').disabled = true;
				return
			}
			
		</c:if>
		
		<c:if test='${inserimentoDitteSMAT eq "SI"}'>
			document.getElementById("inserimentoDitteSMAT").value="SI";
		</c:if>
		
		<c:if test='${inserimentoDitteSMAT eq "SI" and isRTI eq 0}'>
			var is_impresa_oa=getValue("IS_IMPRESA_OA");
			
			//Se impresa proveniente da OA, riporta in DITTAO il valore ID_FORNITORE
			//mentre se impresa Alice, riporta in DITTAO il valore di DITTAO_SMAT 
			var dittao;
			if (is_impresa_oa == 1){
				dittao = getValue("ID_FORNITORE");
			} else {
				dittao = getValue("DITTAO_SMAT");
			}
			setValue("DITG_DITTAO",dittao);
		</c:if>
		
		<c:if test='${inserimentoDitteSMAT eq "SI" and isRTI ne 0}'>
			var is_impresa_oa=getValue("IS_IMPRESA_OA");
			
			//Se impresa proveniente da OA, riporta in CODDIC il valore ID_FORNITORE
			//mentre se impresa Alice, riporta in CODDIC il valore di CODDIC_SMAT 
			var coddic;
			if (is_impresa_oa == 1){
				coddic = getValue("ID_FORNITORE");
			} else {
				coddic = getValue("CODDIC_SMAT");
			}
			setValue("CODDIC",coddic);
			
			for(var i=1; i < maxIdRAGIMPVisualizzabile; i++){
				if(document.getElementById("rowtitoloRAGIMP_" + i).style.display != "none"){
					var is_impresa_oa=getValue("V_IMPR_SMAT_IS_IMPRESA_OA_" + i);
					var coddic;
					if (is_impresa_oa == 1){
						coddic = getValue("V_IMPR_SMAT_ID_FORNITORE_" + i);
						setValue("RAGIMP_CODDIC_" + i,coddic);
					}
				}
			}
		</c:if>
		
				
		var nomimo = getValue("DITG_NOMIMO");
		if (nomimo!= null && nomimo.length > 61){
			nomimo = nomimo.substr(0,61);
			setValue("DITG_NOMIMO",nomimo);
		}
		
		
		schedaConferma();
	}
	
	<c:if test='${faseRicezione eq step1Wizard || faseRicezione eq step5Wizard}'>
			
		function checkDataRicezioneOfferta(data){
			
			if(getValue("DTEOFF") != ""){
				var ora = getValue("DITG_ORAOFF");
				if(data!= null && data != "")
					checkDatiRichiestaOfferta(data, ora,getValue("DTEOFF"),getValue("OTEOFF"),"La data inserita e' successiva alla data di termine ricezione offerte");
			}
		}
		
		function checkOraRicezioneOfferta(ora){
			if(getValue("DTEOFF") != ""){
				var data = getValue("DITG_DATOFF");
				if(ora!= null && ora != "")
					checkDatiRichiestaOfferta(data, ora,getValue("DTEOFF"),getValue("OTEOFF"),"La data inserita e' successiva alla data di termine ricezione offerte");
			}
		}
		
		
		function checkDataRichiestaDomanda(data){
			if(getValue("DTEPAR") != ""){
				var dataOriginale = getOriginalValue("DITG_DRICIND");
				var ora = getValue("DITG_ORADOM");
				if(data!= null && data != "" && data!=dataOriginale && data!= winOpener.getValue("DITG_DRICIND_${param.indiceRiga}"))
					checkDatiRichiestaOfferta(data, ora,getValue("DTEPAR"),getValue("OTEPAR"),"La data inserita e' successiva alla data termine richiesta partecipazione");
			}
			
		}
		
		function checkOraRichiestaDomanda(ora){
			if(getValue("DTEPAR") != ""){
				var oraOrignale = getOriginalValue("DITG_ORADOM");
				var data = getValue("DITG_DRICIND");
				if(ora!= null && ora != "" && ora!=oraOrignale && ora != winOpener.getValue("DITG_ORADOM_${param.indiceRiga}"))
					checkDatiRichiestaOfferta(data, ora,getValue("DTEPAR"),getValue("OTEPAR"),"La data inserita e' successiva alla data termine richiesta partecipazione");
			}
			
		}
		
		
	</c:if>
	
	function aggiornaTitoli(rti){
		var valoreRti = rti;
		if(valoreRti > 0){
			document.getElementById('tipoRTI').value=valoreRti;
			valoreRti=1;
		}
		document.getElementById('isRTI').value=valoreRti;
		document.forms[0].metodo.value="apri";
		document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
		bloccaRichiesteServer();
		document.forms[0].submit();
	}
	
	<c:if test="${offertaRT eq '1' }">
		document.getElementById('tipoRTI').value=${tipoRTI};
		document.getElementById('isRTI').value='1';
	</c:if>
	
	function archivioRTI(tipo){
		var nomest= getValue("NOMEST");
		var dittao = getValue("DITG_DITTAO");
		var href ="href=gene/impr/impr-listaRaggruppamento.jsp";
		if(tipo=="SMAT")
			href ="href=gare/v_impr_smat/v_impr_smat-listaRaggruppamento.jsp";
			
		var tipoRTI = "${tipoRTI }";
		if(tipoRTI!=null && tipoRTI!="")
			href += "&tipoRTI=" + tipoRTI; 
		var raggruppamentoSelezionato = "${raggruppamentoSelezionato }";
		if(raggruppamentoSelezionato != "SI" ){
			var filtroNomest="";
			var filtro ="";
			if(nomest!= null && nomest !=""){
				nomest ="'%" + nomest + "%'";
				nomest = nomest.toUpperCase();
				filtroNomest = "${fnucase}( IMPR.NOMEST ) like " + nomest;
				filtroNomest += " OR ${fnucase}( IMPR.CODIMP ) like " + nomest;
				filtroNomest = "(" + filtroNomest + ")";
			}
			
			if(dittao!= null && dittao !=""){
				dittao ="'%" + dittao + "%'";
				dittao = dittao.toUpperCase();
				var filtroCodimp = "${fnucase}( IMPR.CODIMP ) like " + dittao;
				filtro = filtroCodimp;	
			}
			
			if(filtroNomest!=""){
				if(filtro!="")
					filtro += " AND ";
				filtro += filtroNomest;
			}
			
			<c:if test="${offertaRT eq '1' }">
				var codiceDitta = "${codiceDitta}";
				if(filtro!="")
					filtro += " AND ";
				filtro += " exists( select codime9 from ragimp where codime9=IMPR.CODIMP and coddic='" + codiceDitta + "' and impman='1')";
			</c:if>
			
			if(filtro!="" && filtro!=""){
				filtro = escape(filtro);
				href += "&filtroNomest=" + filtro;
			}
			
			 
		}
		openPopUpCustom(href, "formDitteGaraRTI", 700, 500, 1, 1);
	}
	
	
	function bloccoCampi(){
		var raggruppamentoSelezionato = "${raggruppamentoSelezionato }";
		if(raggruppamentoSelezionato == "SI"){
			
			document.getElementById('DITG_DITTAO').disabled = true;
			document.getElementById('NOMEST').disabled = true;
			
			document.getElementById('CODDIC').disabled = true;
			document.getElementById('NOMEST1').disabled = true;
			document.getElementById('CFIMP').disabled = true;
			document.getElementById('PIVIMP').disabled = true;
			
			
			showObj("rowLinkAddRAGIMP", false);
			
						
			for(i=1;i<=lastIdRAGIMPVisualizzata;i++){
				document.getElementById('RAGIMP_CODDIC_' + i).disable=true;
				document.getElementById('RAGIMP_NOMDIC_' + i).disable=true;
			}
			
				
		}
		
	}
	
	
	//Customizzazione della funzione delElementoSchedaMultipla per evitare che possa
 	//essere eliminata un componente quando è stato selezionato un raggruppamento da elenco
 	function delComponente(id, label, tipo, campi){
		var raggruppamentoSelezionato = "${raggruppamentoSelezionato }";
		if(raggruppamentoSelezionato == "SI")
			alert("Non è possibile procedere con l'eliminazione")
		else
			delElementoSchedaMultipla(id,label,tipo,campi);
	}
	
	function sbiancaOra(data,campo){
		if(data==null || data == "")
			setValue(campo,"");
	}
	
	<c:if test='${(faseRicezione eq step1Wizard or faseRicezione eq step5Wizard) }'>
		//Nella tabella grigliaDataProt è stato inserito il td con classe='riempimento' che ha il solo
		//scopo di riempire una parte della tabella in modo da fare risultare più piccoli e quindi più
		//vicini i campi con l'ora e la data
		$('table.grigliaDataProt tr td.valore-dato').css('width','200');
		$('table.grigliaDataProt tr td.riempimento').css('width','40%');
	</c:if>
	</gene:javaScript>
</gene:template>
</div>