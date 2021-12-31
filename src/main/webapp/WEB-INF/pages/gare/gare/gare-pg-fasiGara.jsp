<%
/*
 * Created on: 14-lug-2008
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>


<c:choose>
	<c:when test='${not empty param.bustalotti}'>
		<c:set var="bustalotti" value="${param.bustalotti}"  scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="bustalotti" value="${bustalotti}" scope="request"/>
	</c:otherwise>
</c:choose>

<c:set var="filtroValutazione" value="${param.filtroValutazione}"  scope="request"/>

<c:choose>
	<c:when test='${empty param.aggiudProvDefOffertaUnica}'>
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction" parametro="${key}" />
	</c:when>
	<c:otherwise>
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneAggiudProvDefOffertaUnicaFunction" parametro="${key}" />
	</c:otherwise>
</c:choose>

<c:set var="iconaNoteAttiva" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "listaDitte.gare.note")}' scope="request"/>

<jsp:include page="./fasiGara/defStepWizardFasiGara.jsp" />
<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<% // Il campo GARE.FASGAR per compatibilita' con PWB assume valore pari a %>
<% // floor(GARE.STEPGAR/10), cioè il piu' grande intero minore o uguale a %>
<% // GARE.STEPGAR/10 %>

<jsp:include page="/WEB-INF/pages/gare/gare/bloccaModifica-fasiGara.jsp" >
	<jsp:param name="entita" value="V_GARE_TORN" />
	<jsp:param name="inputFiltro" value="${inputFiltro}" />
	<jsp:param name="filtroCampoEntita" value="codgar = #CODGAR#" />
	<jsp:param name="updateLista" value="${updateLista}" />
</jsp:include>

<c:choose>
	<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
		<c:set var="modlicg" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMODLICGFunction", pageContext, "")}' scope="request" />
	</c:when>
	<c:otherwise>
		<c:set var="modlicg" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMODLICFunction", pageContext, key)}' scope="request" />
	</c:otherwise>
</c:choose>

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<%// Gestione dell'ordinamento delle diverse pagine del wizard: per le prime
  // 4 fasi di gara la lista deve essere ordine per le colonne DITG.NPROGG e DITG.NOMIMO
  // (pgSort=4;5), mentre la fase 'Calcolo aggiudicazione' deve essere ordinata
  // per le colonne DITG.RIBAUO, DITG.STAGGI e DITG.NPROGG (pgSort=+/-26;24;4).
  // Inoltre tra una pagina e l'altra viene trascurato l'eventuale ordinamento
  // impostato dall'utente
%>
<c:if test="${updateLista ne 1}" >
	<c:choose>
	  <c:when test='${paginaAttivaWizard >= step1Wizard and paginaAttivaWizard <= step7Wizard}' >
			<c:set var="sortingList" value="5;6" scope="request" />
		</c:when>
		<c:when test='${paginaAttivaWizard eq step8Wizard and modalitaAggiudicazioneGara eq 17}' >
			<c:set var="sortingList" value="-15;17;5" scope="request" />
	  	</c:when>
		<c:when test='${paginaAttivaWizard eq step8Wizard and modalitaAggiudicazioneGara eq 6 and empty ULTDETLIC}' >
			<c:set var="sortingList" value="-15;17;5" scope="request" />
	  	</c:when>
		<c:when test='${paginaAttivaWizard eq step8Wizard and modalitaAggiudicazioneGara ne 6 and empty ULTDETLIC}' >
			<c:set var="sortingList" value="15;17;5" scope="request" />
		</c:when>
		<c:when test='${paginaAttivaWizard eq step8Wizard and modalitaAggiudicazioneGara eq 6 and !empty ULTDETLIC}' >
			<c:set var="sortingList" value="-15;18;5" scope="request" />
	  	</c:when>
		<c:when test='${paginaAttivaWizard eq step8Wizard and modalitaAggiudicazioneGara ne 6 and !empty ULTDETLIC}' >
			<c:set var="sortingList" value="15;18;5" scope="request" />
		</c:when>
	</c:choose>
</c:if>


<% // Set di una variabile temporanea per costruire il valore delle stringhe     %>
<% // strProtVisualizzaFasiGara e strProtModificaFasiGara in funzione dello step %>
<% // del wizard attivo. Questa variabile e' stata introdotta per non modificare %>
<% // i record presenti nella tabella W_OGGETTI (e tabelle collegate W_AZIONI e  %>
<% // W_PROAZI e di tutti di i profili esistenti) in seguito all'introduzione di %>
<% // nuovi step nel wizard fasi di gara %>

<c:set var="varTmp" value="${paginaAttivaWizard/10}" />
<c:if test='${fn:endsWith(varTmp, ".0")}'>
	<c:set var="varTmp" value='${fn:substringBefore(varTmp, ".0")}' />
	<c:set var="whereBusteAttiveWizard" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetBusteDocumentazioneFunction",  pageContext, varTmp,"DOCUMGARA","BUSTA")}'/>
</c:if>

<c:choose>
	<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
		<c:set var="strProtVisualizzaFasiGara" value="FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.VIS-FASE${varTmp}" scope="request" />
		<c:set var="strProtModificaFasiGara" value="FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.MOD-FASE${varTmp}" scope="request" />
	</c:when>
	<c:when test='${isGaraLottiConOffertaUnica eq "true" and paginaAttivaWizard < step8Wizard}'>
		<c:set var="strProtVisualizzaFasiGara" value="FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.FASIGARA.VIS-FASE${varTmp}" scope="request" />
		<c:set var="strProtModificaFasiGara" value="FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.FASIGARA.MOD-FASE${varTmp}" scope="request" />
	</c:when>
	<c:when test='${isGaraLottiConOffertaUnica eq "true" and paginaAttivaWizard >= step8Wizard}'>
		<c:set var="strProtVisualizzaFasiGara" value="FUNZ.VIS.ALT.GARE.GARE-AGGIUDPROVDEF.FASIGARA.VIS-FASE${varTmp}" scope="request" />
		<c:set var="strProtModificaFasiGara" value="FUNZ.VIS.ALT.GARE.GARE-AGGIUDPROVDEF.FASIGARA.MOD-FASE${varTmp}" scope="request" />
	</c:when>
</c:choose>


<c:choose>
	<c:when test='${ param.paginaFasiGara eq "aperturaOffAggProvLottoOffUnica" and (paginaAttivaWizard eq step9Wizard or paginaAttivaWizard eq step6_5Wizard)}'>
		<table class="dettaglio-notab">
	</c:when>
	<c:when test='${ param.paginaFasiGara eq "aperturaOffAggProvLottoOffUnica" and !(paginaAttivaWizard eq step9Wizard or paginaAttivaWizard eq step6_5Wizard)}'>
		<table class="dettaglio-tab-lista">
	</c:when>
	<c:when test='${empty param.aggiudProvDefOffertaUnica}'>
		<table class="dettaglio-tab-lista">
	</c:when>
	<c:when test='${(not empty param.aggiudProvDefOffertaUnica) and paginaAttivaWizard eq step8Wizard}'>
		<table class="dettaglio-noBorderBottom">
	</c:when>
	<c:when test='${(not empty param.aggiudProvDefOffertaUnica) and paginaAttivaWizard >= step9Wizard}'>
		<table class="dettaglio-notab">
	</c:when>
</c:choose>

	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td style="background-color:#EEEEEE; border: 1px dotted #999999; padding: 2px 0px 2px 2px; display: block;" >
			<c:set var="strPagineVisitate" value="" scope="request" />
			<c:set var="strPagineDaVisitare" value="" scope="request" />
			<c:forEach items="${pagineVisitate}" var="pagina" >
				<c:set var="strPagineVisitate" value="${strPagineVisitate} -> ${pagina}" scope="request" />
			</c:forEach>
			<c:set var="strPagineVisitate" value="${fn:substring(strPagineVisitate, 4, fn:length(strPagineVisitate))}" scope="request" />
			<c:if test='${fn:length(pagineDaVisitare) > 0}' >
				<c:forEach items="${pagineDaVisitare}" var="pagina" >
					<c:set var="strPagineDaVisitare" value="${strPagineDaVisitare} -> ${pagina}" scope="request" />
				</c:forEach>
				<c:set var="strPagineDaVisitare" value="${fn:substring(strPagineDaVisitare, 0, fn:length(strPagineDaVisitare))}" scope="request" />
			</c:if>
			<span class="avanzamento-paginevisitate"><c:out value="${fn:trim(strPagineVisitate)}" escapeXml="true" /></span><span class="avanzamento-paginedavisitare"><c:out value="${strPagineDaVisitare}" escapeXml="true" /></span>
		</td>
	</tr>

<c:choose>
	<c:when test='${gene:checkProt(pageContext, strProtVisualizzaFasiGara)}' >
		<c:set var="stileDati" value="" scope="request" />
	</c:when>
	<c:otherwise>
		<c:set var="stileDati" value="style='display: none;'" scope="request" />
		<tr>
			<td>
				<br>
				<br>
					<center>Dati non disponibili per il profilo in uso</center>
				<br>
				<br>
			</td>
		</tr>
	</c:otherwise>
</c:choose>

<%/*La pagina può essere richiamata anche da TORN per le gare divise a lotti con offerta unica*/ %>
<c:choose>
	<c:when test='${isGaraLottiConOffertaUnica eq "true" and paginaAttivaWizard < step9Wizard and empty param.aggiudProvDefOffertaUnica}'>
		<c:set var="whereDITG" value='DITG.NGARA5 = #TORN.CODGAR#' scope="request" />
		<c:set var="whereGARE" value='GARE.NGARA = #TORN.CODGAR#' scope="request" />
		<c:set var="codiceGara" value='${gene:getValCampo(key,"CODGAR")}' scope="request" />
	</c:when>
	<c:when test='${isGaraLottiConOffertaUnica eq "true" and paginaAttivaWizard < step9Wizard and not empty param.aggiudProvDefOffertaUnica}'>
		<c:set var="whereDITG" value='DITG.NGARA5 = #GARE.NGARA#' scope="request" />
		<c:set var="whereGARE" value='GARE.NGARA = #GARE.NGARA#' scope="request" />
		<c:set var="codiceGara" value='${gene:getValCampo(key,"NGARA")}' scope="request" />
	</c:when>
	<c:when test='${isGaraLottiConOffertaUnica eq "true" and paginaAttivaWizard >= step9Wizard and empty param.aggiudProvDefOffertaUnica}'>
		<c:set var="whereDITG" value='DITG.NGARA5 = #TORN.CODGAR#' scope="request" />
		<c:set var="whereGARE" value='GARE.NGARA = #TORN.CODGAR#' scope="request" />
		<c:set var="codiceGara" value='${gene:getValCampo(key,"NGARA")}' scope="request" />
	</c:when>
	<c:when test='${isGaraLottiConOffertaUnica eq "true" and paginaAttivaWizard >= step9Wizard and empty param.aggiudProvDefOffertaUnica}'>
		<c:set var="whereDITG" value='DITG.NGARA5 = #GARE.NGARA#' scope="request" />
		<c:set var="whereGARE" value='GARE.NGARA = #GARE.NGARA#' scope="request" />
		<c:set var="codiceGara" value='${gene:getValCampo(key,"NGARA")}' scope="request" />
	</c:when>
	<c:otherwise>
		<c:set var="whereDITG" value='DITG.NGARA5 = #GARE.NGARA#' scope="request" />
		<c:set var="whereGARE" value='GARE.NGARA = #GARE.NGARA#' scope="request" />
		<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' scope="request"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.idconfi}'>
		<c:set var="idconfi" value="${param.idconfi}"  scope="request"/>
	</c:when>
	<c:otherwise>
		<c:if test="${empty idconfi }">
			<c:set var="uffintGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetUffintGaraFunction",  pageContext,codiceGara)}' />
			<c:set var="idconfi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetWSDMConfiAttivaFunction",  pageContext,uffintGara,sessionScope.moduloAttivo)}' />
		</c:if>
		<c:set var="idconfi" value="${idconfi}" scope="request"/>
	</c:otherwise>
</c:choose>

<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext,idconfi)}'/>

<c:if test="${param.paginaFasiGara eq 'aperturaOffAggProvLottoOffUnica'}">
	<c:set var="isProceduraTelematica" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsProceduraTelematicaGaraFunction", pageContext, gene:getValCampo(key,"NGARA"))}' scope="request" />
</c:if>

<c:if test='${isGaraLottiConOffertaUnica eq "true" and paginaAttivaWizard eq step1Wizard }'>
	<c:set var="valoreChiaveRiservatezza" value="${codiceGara}"/>
	<c:set var="riservatezzaAttiva" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsRiservatezzaAttivaFunction", pageContext, valoreChiaveRiservatezza, idconfi)}' scope="request"/>
</c:if >

<c:choose>
	<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
		<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction", pageContext, gene:getValCampo(key,"NGARA"))}' scope="request"/>
		<c:if test="${paginaAttivaWizard eq step7Wizard && updateLista  ne 1}">
			<c:if test="${modlicg eq 6}">
				<c:set var="esistonoDitteSenzaPunteggio" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePunteggiTecniciNulliFunction", pageContext, gene:getValCampo(key,"NGARA"),"false","true","true")}' />
				<c:if test="${punteggiTuttiValorizzati eq 'no' }">
					<c:set var="bloccoPunteggiNonTuttiValorizzati" value="true"/>
				</c:if>
			</c:if>
			<c:set var="messaggioControlloPunteggi" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlloPunteggioTecnicoEconomicoFunction", pageContext, gene:getValCampo(key,"NGARA"),step7Wizard, "offerteEconomiche","false")}' />
		</c:if>
	</c:when>
 	<c:otherwise>
		<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction", pageContext, gene:getValCampo(key,"CODGAR"))}' scope="request"/>
		<c:if test="${paginaAttivaWizard eq step6Wizard && isProceduraTelematica eq 'true'}">
			<c:if test="${attivaValutazioneTec eq 'true'}">
				<c:set var="esistonoDitteSenzaReqmin" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteRequisitiMinimiNulliFunction", pageContext,  gene:getValCampo(key,"CODGAR"),isGaraLottiConOffertaUnica)}' />
			</c:if>
			<c:if test="${modlicg eq 6}">
				<c:set var="esistonoDitteSenzaPunteggio" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePunteggiTecniciNulliFunction", pageContext, gene:getValCampo(key,"CODGAR"),isGaraLottiConOffertaUnica,"false","false")}' />
			</c:if>
			<c:set var="messaggioControlloPunteggi" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlloPunteggioTecnicoEconomicoFunction", pageContext, gene:getValCampo(key,"CODGAR"),step6Wizard, "offerteEconomiche","false" )}' />
		</c:if>
 	</c:otherwise>
</c:choose>



<c:if test='${paginaAttivaWizard >= step8Wizard}'>
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.PulisciSessioneFiltriFunction" parametro=""/>
</c:if>

<c:if test="${paginaAttivaWizard eq step1Wizard }">
	<c:set var="descTabA1158" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDescrizioneTabellatoFunction", pageContext,"A1158" ,"1")}' />
</c:if>

<c:if test="${updateLista ne 1 }">
	<c:choose>
	<c:when test="${paginaAttivaWizard eq step1Wizard }">
		<c:set var="abilitazioneAperturaBuste" value="${isProceduraTelematica eq 'true' and fn:contains(listaOpzioniDisponibili, 'OP114#') and (faseGara eq 2 or faseGara eq 3 or faseGara eq 4)}"/>
		
	</c:when>
	<c:when test="${paginaAttivaWizard eq step6Wizard }">
		<c:set var="abilitazioneAperturaBuste" value="${isProceduraTelematica eq 'true' and fn:contains(listaOpzioniDisponibili, 'OP114#') and faseGara eq 5}"/>
	</c:when>
	<c:when test="${paginaAttivaWizard eq step7Wizard }">
		<c:set var="abilitazioneAperturaBuste" value="${isProceduraTelematica eq 'true' and fn:contains(listaOpzioniDisponibili, 'OP114#') and faseGara eq 6}"/>
	</c:when>
</c:choose>
</c:if>

<c:choose>
	<c:when test="${bustalotti == '2' }">
		<c:set var="chiaveGara" value='${gene:getValCampo(key,"CODGAR")}'/>
	</c:when>
	<c:otherwise>
		<c:set var="chiaveGara" value='${gene:getValCampo(key,"NGARA")}'/>
	</c:otherwise>
</c:choose>
<c:if test="${isProceduraTelematica eq 'true' && (paginaAttivaWizard eq step6Wizard || paginaAttivaWizard eq step1Wizard) && updateLista  ne 1 }">
	<c:choose>
		<c:when test="${paginaAttivaWizard eq step1Wizard }">
			<c:set var="esistonoAcquisizioniOfferteDaElaborareFS11A" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, chiaveGara, "FS11A" )}' />
		</c:when>
		<c:otherwise>
			<c:set var="esistonoAcquisizioniOfferteDaElaborareFS11B" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, chiaveGara, "FS11B" )}' />
		</c:otherwise>
	</c:choose>
</c:if>

<c:if test="${isProceduraTelematica eq 'true' && paginaAttivaWizard eq step6Wizard && updateLista ne 1 and modlicg eq 6 and bustalotti ne 2}">
	<c:set var="esitocontrolloRiparametrazioneTec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlloPresenzaRiparametrazioneApplicataFunction", pageContext, gene:getValCampo(key,"NGARA"), "1", "singolo" )}' />
</c:if>

<c:if test="${isProceduraTelematica eq 'true' && paginaAttivaWizard eq step6Wizard && updateLista ne 1 and bustalotti eq 2}">
	<c:set var="esitocontrolloRiparametrazioneTec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlloPresenzaRiparametrazioneApplicataFunction", pageContext, gene:getValCampo(key,"CODGAR"), "1", "tutti" )}' />
</c:if>

<c:if test="${paginaAttivaWizard eq step7Wizard && updateLista ne 1 and modlicg eq 6 and bustalotti ne 2}">
	<c:if test="${isProceduraTelematica ne 'true'}">
		<c:set var="esitocontrolloRiparametrazioneTec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlloPresenzaRiparametrazioneApplicataFunction", pageContext, gene:getValCampo(key,"NGARA"), "1", "singolo" )}' />
	</c:if>
	<c:if test="${esitocontrolloRiparametrazioneTec ne 'NOK'}">
		<c:set var="esitocontrolloRiparametrazioneEco" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlloPresenzaRiparametrazioneApplicataFunction", pageContext, gene:getValCampo(key,"NGARA"), "2", "singolo" )}' />
	</c:if>
</c:if>

<c:if test="${isProceduraTelematica eq 'true' && paginaAttivaWizard eq step7Wizard && updateLista ne 1 }">
	<c:set var="esistonoAcquisizioniOfferteDaElaborareFS11C" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, gene:getValCampo(key,"NGARA"), "FS11C" )}' />
</c:if>


<c:if test="${paginaAttivaWizard eq step7_5Wizard and pgAsta eq 2 }">
	<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codiceGara, idconfi)}' scope="request"/>
</c:if>

<c:set var="urlWsArt80" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", urlWsArt80)}'/>

<c:if test='${paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step7Wizard}'>
	<c:set var="garaInversa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetInversaFunction", pageContext, codiceGara)}' />
</c:if>

<c:choose>
	<c:when test='${empty paginaAttivaWizard or paginaAttivaWizard < step9Wizard and (paginaAttivaWizard ne step3Wizard and paginaAttivaWizard ne step5Wizard and paginaAttivaWizard ne step6_5Wizard and paginaAttivaWizard ne step7_5Wizard)}'>

	<!-- inizia pagine a lista -->

		<c:if test='${paginaAttivaWizard eq step1Wizard }'>
			<c:set var="garaInversa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetInversaFunction", pageContext, codiceGara)}' />
			<c:if test='${garaInversa eq "1" }'>
				<c:if test="${faseGara < 8 }">
					<c:set var="modGaraInversa" value='true' />
					<c:set var="bloccoAggiudicazione" value='0' scope="request"/>
				</c:if>
				<c:if test='${bustalotti eq 2 and updateLista ne 1}'>
						<c:set var="abilitazioneAperturaBuste" value="${fn:contains(listaOpzioniDisponibili, 'OP114#') and faseGara >=2}"/>
				</c:if>
			</c:if>
		</c:if>
		
		<c:set var="condizioniGestioneBusteFS11A" value="${paginaAttivaWizard eq step1Wizard and abilitazioneAperturaBuste and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.AcquisisciDocAmm')}" />
		<c:set var="condizioniGestioneBusteFS11B" value="${paginaAttivaWizard eq step6Wizard and abilitazioneAperturaBuste and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.AttivaAperturaTecniche')}" />
		<c:set var="condizioniGestioneBusteFS11C" value="${paginaAttivaWizard eq step7Wizard and abilitazioneAperturaBuste and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.AttivaAperturaEconomiche')}" />
		
		<c:choose >
			<c:when test="${bustalotti eq 2}">
				<c:set var="whereSezTec" value="CODGAR1='${chiaveGara }' and SEZIONITEC='1'"/>
				<c:set var="numLottiSezionitec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "count(SEZIONITEC)","GARE1", whereSezTec)}'/>
				<c:if test="${not empty numLottiSezionitec and numLottiSezionitec ne 0 }">
					<c:set var="sezionitec" value="1"/>
				</c:if>
			</c:when>
			<c:otherwise>
				<c:set var="whereSezTec" value="NGARA='${chiaveGara }'"/>
				<c:set var="sezionitec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "SEZIONITEC","GARE1", whereSezTec)}'/>
			</c:otherwise>
		</c:choose>
		
		
		<c:choose>
			<c:when test='${paginaAttivaWizard eq step6Wizard and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and modalitaAggiudicazioneGara eq 6 and isGaraLottiConOffertaUnica ne "true"}'>
				<tr>
					<td ${stileDati} >
						<c:choose>
							<c:when test="${punteggioTecnico < 0.0}">
								<c:set var="msgPunteggioTecnico" value="non definito"/>
							</c:when>
							<c:otherwise>
								<fmt:formatNumber type="number" value="${punteggioTecnico}" var="punteggioTec" />
								<c:set var="msgPunteggioTecnico" value="${punteggioTec}"/>
							</c:otherwise>
						</c:choose>
						
						<b>Punteggio tecnico massimo:</b> ${msgPunteggioTecnico} <c:if test="${!empty sogliaTecnicaMinima }"> <fmt:formatNumber type="number" value="${sogliaTecnicaMinima}" var="SogliaMinTecFormat" />&nbsp;&nbsp;&nbsp;&nbsp;<b>Soglia minima:</b>&nbsp;${SogliaMinTecFormat }</c:if>
					</td>
				</tr>
				<c:if test='${sezionitec eq 1}'>
					<c:choose>
						<c:when test="${empty punteggioTecnicoQualitativo }">
							<c:set var="msgPunteggioTecnicoQualitativo" value="non definito"/>
						</c:when>
						<c:otherwise>
							<fmt:formatNumber type="number" value="${punteggioTecnicoQualitativo}" var="punteggioTecQualitativo" />
							<c:set var="msgPunteggioTecnicoQualitativo" value="${punteggioTecQualitativo}"/>
						</c:otherwise>
					</c:choose>
					
					<c:choose>
						<c:when test="${empty punteggioTecnicoQuantitativo }">
							<c:set var="msgPunteggioTecnicoQuantitativo" value="non definito"/>
						</c:when>
						<c:otherwise>
							<fmt:formatNumber type="number" value="${punteggioTecnicoQuantitativo}" var="punteggioTecQuantitativo" />
							<c:set var="msgPunteggioTecnicoQuantitativo" value="${punteggioTecQuantitativo}"/>
						</c:otherwise>
					</c:choose>
					<tr>
					<td ${stileDati}><b>di cui qualitativo:</b> ${msgPunteggioTecnicoQualitativo}</td>
					</tr>
					<tr>
					<td ${stileDati}><b>di cui quantitativo:</b> ${msgPunteggioTecnicoQuantitativo}</td>
					</tr>
				</c:if>
				<tr>
					<td ${stileDati} >
						<b>Riparametrazione ?</b> ${msgRiptec }
					</td>
				</tr>
			</c:when>

			<c:when test='${paginaAttivaWizard eq step7Wizard and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and modalitaAggiudicazioneGara eq 6 and isGaraLottiConOffertaUnica ne "true"}'>
				<tr>
					<td ${stileDati} >
						<c:choose>
							<c:when test="${punteggioEconomico < 0.0}">
								<c:set var="msgPunteggioEconomico" value="non definito"/>
							</c:when>
							<c:otherwise>
								<fmt:formatNumber type="number" value="${punteggioEconomico}" var="punteggioEco" />
								<c:set var="msgPunteggioEconomico" value="${punteggioEco}"/>
							</c:otherwise>
						</c:choose>
						<b>Punteggio economico massimo:</b> ${msgPunteggioEconomico}  <c:if test="${!empty sogliaEconomicaMinima }"> <fmt:formatNumber type="number" value="${sogliaEconomicaMinima}" var="SogliaMinEcoFormat" />&nbsp;&nbsp;&nbsp;&nbsp;<b>Soglia minima:</b>&nbsp;${SogliaMinEcoFormat }</c:if>
					</td>
				</tr>
				<tr>
					<td ${stileDati} >
						<b>Riparametrazione ?</b> ${msgRipeco }
					</td>
				</tr>
			</c:when>
		</c:choose>

		<c:if test="${!empty filtroDitte and (paginaAttivaWizard == step1Wizard  || paginaAttivaWizard == step2Wizard
				|| paginaAttivaWizard == step4Wizard || paginaAttivaWizard == step6Wizard || paginaAttivaWizard == step7Wizard) and gene:checkProt(pageContext, strProtVisualizzaFasiGara)}">
			<tr>
				<td style="font: 11px Verdana, Arial, Helvetica, sans-serif;">
				 <br><img src="${pageContext.request.contextPath}/img/filtro.gif" alt="Filtro">&nbsp;<span style="color: #ff0028; font-weight: bold;">Lista filtrata</span> 
				 <c:if test='${updateLista ne 1}'>
					 &nbsp;&nbsp;&nbsp;[ <a href="javascript:AnnullaFiltro(2);" ><IMG SRC="${pageContext.request.contextPath}/img/cancellaFiltro.gif" alt="Cancella filtro"></a>
					 <a class="link-generico" href="javascript:AnnullaFiltro(2);">Cancella filtro</a> ]
				 </c:if>
				</td>
			</tr>
			
		</c:if>
		<c:if test="${!empty filtroValutazione and (paginaAttivaWizard == step1Wizard  || paginaAttivaWizard == step2Wizard
				|| paginaAttivaWizard == step4Wizard || paginaAttivaWizard == step6Wizard || paginaAttivaWizard == step7Wizard) and gene:checkProt(pageContext, strProtVisualizzaFasiGara)}">
			<tr>
				<td style="font: 11px Verdana, Arial, Helvetica, sans-serif;">
				 <br><img src="${pageContext.request.contextPath}/img/filtro.gif" alt="Filtro">&nbsp;<span style="color: #ff0028; font-weight: bold;">Lista filtrata su dettaglio valutazione non completato</span> 
				 <c:if test='${updateLista ne 1}'>
					 &nbsp;&nbsp;&nbsp;[ <a href="javascript:AnnullaFiltro(8);" ><IMG SRC="${pageContext.request.contextPath}/img/cancellaFiltro.gif" alt="Cancella filtro"></a>
					 <a class="link-generico" href="javascript:AnnullaFiltro(8);">Cancella filtro</a> ]
				 </c:if>
				</td>
			</tr>
		</c:if>
		
		<c:choose>
			<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
				<c:set var="valoreNGARA" value='${ gene:getValCampo(key,"NGARA") }' scope="request"/>
			</c:when>
			<c:otherwise>
				<c:set var="valoreNGARA" value='${ gene:getValCampo(key,"CODGAR") }' scope="request"/>
			</c:otherwise>
		</c:choose>
		
		<c:if test='${paginaAttivaWizard eq step8Wizard or paginaAttivaWizard eq step7Wizard}'>
			<c:set var="ditta" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDittaFunction", pageContext, gene:getValCampo(key,"NGARA"))}'/>
			<c:set var="garaAggiudicata" value='${gene:if(ditta ne null and ditta ne "" ,"true","false" )}'/>
			<c:set var="garaAggProvv" value='${gene:if(requestScope.dittaProv ne null and requestScope.dittaProv ne "" ,"true","false" )}'/>
		</c:if>
		
		
		
		<c:if test='${paginaAttivaWizard eq step8Wizard }'>
			<c:set var="isGaraDLGS2016Manuale" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.IsGaraDopoDLGS2016ManualeFunction", pageContext, gene:getValCampo(key,"NGARA"),"true","false","false")}'/>
			
			<c:choose>
				<c:when test='${empty param.aggiudProvDefOffertaUnica}'>
					<c:set var="valoreChiaveAmminversa" value='${ codiceGara }'/>
				</c:when>
				<c:otherwise>
					<c:set var="valoreChiaveAmminversa" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}'/>
				</c:otherwise>
			</c:choose>
			<c:set var="garaInversa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetInversaFunction", pageContext, valoreChiaveAmminversa)}' />
			<c:if test='${modalitaAggiudicazioneGara eq 6}'>
				<c:set var="oepvDL_32_2019" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.ControlliDL_32_2019OEPVFunction", pageContext, gene:getValCampo(key,"NGARA"))}' />
			</c:if>
		</c:if>
		
		<c:if test="${isProceduraTelematica eq 'true' && (paginaAttivaWizard eq step1Wizard || paginaAttivaWizard eq step76izard || paginaAttivaWizard eq step7Wizard) && updateLista ne 1}">
			<c:set var="whereControlloStastoSoccorso" value="CODGAR='${codiceGara }'"/>
			<c:if test="${bustalotti eq '1' and (paginaAttivaWizard eq step6izard || paginaAttivaWizard eq step7Wizard)}">
				<c:set var="whereControlloStastoSoccorso" value="${whereControlloStastoSoccorso } and ngara='${gene:getValCampo(key,'NGARA')}'" />
			</c:if>
			<c:set var="whereControlloStastoSoccorso" value="${whereControlloStastoSoccorso } and FASGAR=${varTmp} and AMMGAR='10'"/>
			<c:set var="numDitteStatoSoccorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "count(CODGAR)","V_DITGAMMIS", whereControlloStastoSoccorso)}'/>
		</c:if>
		
		
		<c:set var="modificaGaraTelematica" value='${isProceduraTelematica ne "true" or (isProceduraTelematica eq "true" and (faseGara eq 2 or faseGara eq 3 or faseGara eq 4) and (paginaAttivaWizard eq step1Wizard or paginaAttivaWizard eq step2Wizard or paginaAttivaWizard eq step3Wizard or paginaAttivaWizard eq step4Wizard or paginaAttivaWizard eq step5Wizard)) 
										or (isProceduraTelematica eq "true" and (faseGara eq 5) and (paginaAttivaWizard eq step6Wizard || paginaAttivaWizard eq step6_5Wizard)) or (isProceduraTelematica eq "true" and faseGara eq 6 and paginaAttivaWizard eq step7Wizard)
										or (isProceduraTelematica eq "true" and paginaAttivaWizard ne step1Wizard and paginaAttivaWizard ne step2Wizard and paginaAttivaWizard ne step3Wizard and paginaAttivaWizard ne step4Wizard and paginaAttivaWizard ne step5Wizard and paginaAttivaWizard ne step6Wizard and paginaAttivaWizard ne step6_5Wizard and paginaAttivaWizard ne step7Wizard)
										or modGaraInversa eq "true"}' scope="request"/>
		
		<c:set var="condizioniGaraRilancio" value='false'/>
		<c:if test='${paginaAttivaWizard eq step7Wizard and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.AggiornaOfferteRilancio")
			and isProceduraTelematica eq "true" and offtel eq "1"}'>
			<c:set var="esistonoRilanci" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoRilanciFunction", pageContext, gene:getValCampo(key,"NGARA")) }'/>
			<c:if test="${esistonoRilanci eq 'true' }">
				<c:set var="condizioniGaraRilancio" value='true'/>
			</c:if>						
		</c:if>
		
		<c:if test="${paginaAttivaWizard eq step1Wizard and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.FASIGARA.MetodoCalcoloSoglia')  and updateLista ne 1}">
			<c:set var="IsMODLICG_13_14" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsMODLICG_13_14Function", pageContext, valoreNGARA,isGaraLottiConOffertaUnica)}' />
			<c:if test="${IsMODLICG_13_14 eq 'true' }">
				<c:set var="isGaraDopoDLGS2016Manuale" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.IsGaraDopoDLGS2016ManualeFunction", pageContext, valoreNGARA,"true","false","true")}' />
				<c:if test="${esitoControlloDitteDLGS2016 && isGaraDopoDLGS2016Manuale eq '1' && (isGaraDLGS2016 or isGaraDLGS2017)}">
					<c:set var="gestioneSogliaManuale" value="true"/> 
				</c:if>
				<c:if test="${esitoControlloDitteDLGS2016 && isGaraDopoDLGS2016Manuale ne '1' && (isGaraDLGS2016 or isGaraDLGS2017)}">
					<c:set var="gestioneSogliaAutomatica" value="true"/>
				</c:if>
			</c:if>
		</c:if>
		
		<c:set var="riboepvVis" value="${modalitaAggiudicazioneGara eq 6 and (isVecchiaOepv or formato51) and visOffertaEco}"/>	
		<tr>
			<td ${stileDati} >
				<gene:formLista entita="DITG" where='${whereDITG} ${filtroFaseGara}${filtroDitte }${filtroValutazione}' tableclass="datilista" sortColumn="${sortingList}" pagesize="${requestScope.risultatiPerPagina}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiGara" gestisciProtezioni="true" >
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					<gene:redefineInsert name="documentiAzioni" />
					<gene:redefineInsert name="addToAzioni" >
						<c:choose>
							<c:when test='${updateLista eq 1 and (paginaAttivaWizard >= step8Wizard or (paginaAttivaWizard < step8Wizard and bloccoAggiudicazione ne 1))}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:conferma();" title="Salva modifiche" tabindex="1500">
											${gene:resource("label.tags.template.dettaglio.schedaConferma")}
										</a>
									</td>
								</tr>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:annulla();" title="Annulla modifiche" tabindex="1501">
											${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
										</a>
									</td>
								</tr>
							</c:when>
							<c:otherwise>
								<c:if test='${paginaAttivaWizard eq step8Wizard }'>
									<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and garaAggProvv ne "true"
											and ((isGaraLottiConOffertaUnica ne "true" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.Calcolo-soglia-anomalia"))
											 or (isGaraLottiConOffertaUnica eq "true" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-AGGIUDPROVDEF.Calcolo-soglia-anomalia")))}'>
										<c:set var="titoloCalcoloSogliaAnomalia" value="Calcolo soglia anomalia" />
										<c:if test='${(aggiudicazioneEsclusAutom eq 2 and calcoloSogliaAnomalia eq 2) or ((isGaraDLGS2016 or isGaraDLGS2017 or isGaraDL2019) and !esitoControlloDitteDLGS2016 and modalitaAggiudicazioneGara ne 6) or oepvDL_32_2019 eq "graduatoria"}' >
											<c:set var="titoloCalcoloSogliaAnomalia" value="Calcolo graduatoria" />
										</c:if>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:apriPopupSogliaAnomalia('${key}');" title='${titoloCalcoloSogliaAnomalia}' tabindex="1501">
													${titoloCalcoloSogliaAnomalia} (1)
												</a>
											</td>
										</tr>
									</c:if>
									<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and gene:checkProt(pageContext, strProtModificaFasiGara) and garaAggProvv ne "true" }'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:verificaOfferta();" title='Verifica congruit&agrave; offerta' tabindex="1502">
													Verifica congruit&agrave; offerta (2)
												</a>
											</td>
										</tr>
									</c:if>
									<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and garaAggProvv ne "true"
											and ((isGaraLottiConOffertaUnica ne "true" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.Aggiudicazione"))
											 or (isGaraLottiConOffertaUnica eq "true" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-AGGIUDPROVDEF.Aggiudicazione")))}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:apriPopupAggiudicazione('${key}');" title='Aggiudicazione gara' tabindex="1503">
													Aggiudicazione (3)
												</a>
											</td>
										</tr>
									</c:if>
									<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.AnnullaCalcoloAggiudicazione") and garaAggiudicata ne "true"}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:apriPopupAnnullaAggiudicazione('${key}');" title='Annulla calcolo aggiudicazione' tabindex="1504">
													Annulla calcolo aggiudicazione
												</a>
											</td>
										</tr>
									</c:if>
								</c:if>
								
								<c:set var="condizioneModifica" value='${autorizzatoModifiche ne 2 and modificaGaraTelematica and paginaAttivaWizard ne step8Wizard and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and gene:checkProt(pageContext, strProtModificaFasiGara) and datiRiga.rowCount > 0 and (paginaAttivaWizard eq step9Wizard or (paginaAttivaWizard < step8Wizard and paginaAttivaWizard != step6Wizard and paginaAttivaWizard != step7Wizard and bloccoAggiudicazione ne 1) or (paginaAttivaWizard == step6Wizard and not esistonoDitteConPunteggio and bloccoAggiudicazione ne 1) or (paginaAttivaWizard == step7Wizard and not esistonoDitteConPunteggio and bloccoAggiudicazione ne 1))}' />
								<c:if test='${condizioneModifica}'>
									
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:modificaLista();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1499">
													${gene:resource("label.tags.template.dettaglio.schedaModifica")}
												</a>
											</td>
										</tr>
									
								</c:if>
								
								
								
								<c:if test='${condizioniGaraRilancio eq "true"}'>
									<c:if test='${faseGara eq 6 and autorizzatoModifiche ne 2}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:apriPopupAggiornaDaRilanci('${key}');" title='Aggiorna offerte economiche da rilancio' tabindex="1500">
													Aggiorna offerte economiche da rilancio
												</a>
											</td>
										</tr>
									</c:if>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:apriPopupProspettoRilanci('${key}');" title='Prospetto rilanci offerta economica' tabindex="1501">
												Prospetto rilanci offerta economica
											</a>
										</td>
									</tr>
								</c:if>

								<c:if test='${modlicg eq "6"}'>
									<c:if test='${((paginaAttivaWizard eq step6Wizard  and (isProceduraTelematica ne "true" or (isProceduraTelematica eq "true" and faseGara eq 5)) and formatiTecniciDefiniti ne "true") or (paginaAttivaWizard eq step7Wizard and formatiEconomiciDefiniti ne "true")) and bloccoAggiudicazione ne 1 and gene:checkProt(pageContext, strProtVisualizzaFasiGara)}'>
									
											<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.Esporta-excel-oepv")}'>
												<c:if test='${isGaraLottiConOffertaUnica ne "true"}'>
													<tr>
														<td class="vocemenulaterale">
															<a href="javascript:apriPopupEsportaInExcel('${key}');" title='Esporta criteri di valutazione in Excel' tabindex="1500">
																Esporta criteri di valutazione in Excel
															</a>
														</td>
													</tr>
												</c:if>
												<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
													<tr>
														<td class="vocemenulaterale">
															<a href="javascript:apriPopupEsportaInExcelOffertaUnica('${key}');" title='Esporta criteri di valutazione in Excel' tabindex="1500">
																Esporta criteri di valutazione in Excel
															</a>
														</td>
													</tr>
												</c:if>
											</c:if>
											<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.Importa-excel-oepv") and not esistonoDitteConPunteggio}'>
												<c:if test='${isGaraLottiConOffertaUnica ne "true"}'>
													<tr>
														<td class="vocemenulaterale">
															<a href="javascript:apriPopupImportaDaExcel('${key}');" title='Importa dettaglio valutazione da Excel' tabindex="1501">
																Importa dettaglio valutazione da Excel
															</a>
														</td>
													</tr>
												</c:if>
												<c:if test='${isGaraLottiConOffertaUnica eq "true" and not esistonoDitteConPunteggio}'>
													<tr>
														<td class="vocemenulaterale">
															<a href="javascript:apriPopupImportaDaExcelOffertaUnica('${key}');" title='Importa dettaglio valutazione da Excel' tabindex="1501">
																Importa dettaglio valutazione da Excel
															</a>
														</td>
													</tr>
												</c:if>	
											</c:if>
									</c:if>
								</c:if>

								<c:if test='${paginaAttivaWizard eq step6Wizard and isGaraLottiConOffertaUnica eq "true" and updateLista ne 1 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.FASIGARA.DettaglioOffertaPerLotto")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:apriDettaglioPerLotto('${key}');" title='Valutazione tecnica per lotto' tabindex="1499">
												Valutazione tecnica per lotto
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test='${paginaAttivaWizard eq step7Wizard and numeroLottiOEPV >0  and isGaraLottiConOffertaUnica eq "true" and updateLista ne 1 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.FASIGARA.DettaglioOffertaPerLotto")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:apriDettaglioPerLotto('${key}');" title='Valutazione economica per lotto' tabindex="1499">
												Valutazione economica per lotto
											</a>
										</td>
									</tr>
								</c:if>
								
								<c:if test='${autorizzatoModifiche ne 2 and modlicg eq "6" and (paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step7Wizard) and isGaraLottiConOffertaUnica ne "true"}'>
									<c:if test='${((faseGara <7 and isProceduraTelematica ne "true") or (isProceduraTelematica eq "true" and ((paginaAttivaWizard eq step6Wizard and faseGara eq 5) or (paginaAttivaWizard eq step7Wizard and faseGara eq 6) )))}'>
										<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.CalcoloPunteggi")}'>
											<c:choose>
												<c:when test="${paginaAttivaWizard eq step6Wizard }">
													<c:set var="tipoPunteggio" value="1"/>
												</c:when>
												<c:otherwise>
													<c:set var="tipoPunteggio" value="2"/>
												</c:otherwise>
											</c:choose>
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:apriPopupCalcoloPunteggi('${key}',${tipoPunteggio},${param.lottoPlicoUnico eq 1},${paginaAttivaWizard});" title='Calcolo punteggi (1)' tabindex="1505">
														Calcolo punteggi (1)
													</a>
												</td>
											</tr>
										</c:if>
									
										<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.EsclusioneSoglia")}'>
											<c:set var="msgEsclusione" value="Esclusione soglia minima e riparam. (2)"/>
											<c:set var="tipoTitolo" value="1"/>
											<c:choose>
												<c:when test="${paginaAttivaWizard eq step6Wizard }">
													<c:set var="tipoPunteggio" value="1"/>
													<c:set var="tipoRiparam" value="${RIPTEC }"/>
													<c:if test="${RIPTEC eq 3 or empty RIPTEC }">
														<c:set var="msgEsclusione" value="Esclusione soglia minima (2)"/>
														<c:set var="tipoTitolo" value="2"/>
													</c:if>
												</c:when>
												<c:otherwise>
													<c:set var="tipoPunteggio" value="2"/>
													<c:set var="tipoRiparam" value="${RIPECO }"/>
													<c:if test="${RIPECO eq 3 or empty RIPECO }">
														<c:set var="msgEsclusione" value="Esclusione soglia minima (2)"/>
														<c:set var="tipoTitolo" value="2"/>
													</c:if>
												</c:otherwise>
											</c:choose>
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:apriPopupEsclusioneSogliaMinima('${key}',${tipoPunteggio},${tipoTitolo },'${tipoRiparam }');" title='${msgEsclusione}' tabindex="1506">
														${msgEsclusione}
													</a>
												</td>
											</tr>
										</c:if>
										
										<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.AnnullaCalcoloPunteggi")}'>
											<c:choose>
												<c:when test="${paginaAttivaWizard eq step6Wizard }">
													<c:set var="tipoPunteggio" value="1"/>
												</c:when>
												<c:otherwise>
													<c:set var="tipoPunteggio" value="2"/>
												</c:otherwise>
											</c:choose>
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:apriPopupAnnullaCalcoloPunteggi('${key}',${tipoPunteggio});" title='Annulla calcolo punteggi' tabindex="1507">
														Annulla calcolo punteggi
													</a>
												</td>
											</tr>
										</c:if>
									</c:if>
								</c:if>
								
								<c:if test='${modalitaAggiudicazioneGara eq 6 and (((paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step7Wizard) and isGaraLottiConOffertaUnica ne "true") or (paginaAttivaWizard eq step8Wizard)) and updateLista ne 1 }'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:apriPopupProspettoPunteggi('${key}');" title='Prospetto punteggi ditte' tabindex="1508">
												Prospetto punteggi ditte
											</a>
										</td>
									</tr>
								</c:if>
								
								<c:if test="${modalitaAggiudicazioneGara eq 6 and isGaraLottiConOffertaUnica ne 'true' and (paginaAttivaWizard eq step6Wizard  or paginaAttivaWizard eq step7Wizard) and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.ImpostaFiltroVerificaValutazione')}">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:impostaFiltroVerificaValutazione('${gene:getValCampo(key,"NGARA")}');" title='Verifica dettaglio valutazione completato' tabindex="1509">
												Verifica dettaglio valutaz.completato
											</a>
										</td>
									</tr>
								</c:if>
								
								<c:if test='${((modlicg eq "6" and (isVecchiaOepv or formato52)) or modlicg eq "5" or modlicg eq "14" or modlicg eq "16") and paginaAttivaWizard eq step7Wizard and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.Esporta-excel-offertaPrezzi")}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:apriPopupEsportaInExcelOffertaPrezzi('${valoreNGARA}');" title='Esporta dettaglio offerta prezzi in Excel' tabindex="1510">
												Esporta dettaglio offerta prezzi in Excel
											</a>
										</td>
									</tr>
								</c:if>
								
								<c:if test="${paginaAttivaWizard < step8Wizard and gene:checkProt(pageContext, strProtVisualizzaFasiGara)}">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:impostaFiltro();" title='Imposta filtro' tabindex="1511">
												Imposta filtro
											</a>
										</td>
									</tr>
								</c:if>
								
								<c:if test="${gestioneSogliaManuale eq 'true' or gestioneSogliaAutomatica eq 'true'}">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:consultaMetodoCalcoloAnomalia();" title='Consulta metodo calcolo anomalia' tabindex="1512">
												Consulta metodo calcolo anomalia
											</a>
										</td>
									</tr>
								</c:if>
								
								<c:if test="${paginaAttivaWizard eq step1Wizard and compreq eq '1' and fn:startsWith(descTabA1158,'1') and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.SorteggioDitteVerificaRequisiti')}">
									<c:choose>
										<c:when test='${isGaraLottiConOffertaUnica eq "true" and garaInversa eq "1"}'>
											<c:set var="visualizzaFunzione" value='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and gene:checkProt(pageContext, strProtModificaFasiGara) and datiRiga.rowCount > 0 and  faseGara ne 8}'  />
										</c:when>
										<c:otherwise>
											<c:set var="visualizzaFunzione" value='${condizioneModifica}'  />
										</c:otherwise>
									</c:choose>
									<c:if test="${visualizzaFunzione }">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:sorteggioDitteVerificaRequisiti();" title='Sorteggio ditte per verifica requisiti' tabindex="1513">
												Sorteggio ditte per verifica requisiti
											</a>
										</td>
									</tr>
									</c:if>
								</c:if>
								
								<c:if test='${paginaAttivaWizard eq step6_5Wizard and modalitaAggiudicazioneGara eq 6 and updateLista ne 1}'>
									<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIGARA.AttivaOfferteEconomiche")}'>
										<c:if test="${isProceduraTelematica eq 'true' and visOffertaEco}">
											<c:if test="${(faseGara eq '5' and param.paginaFasiGara ne 'aperturaOffAggProvLottoOffUnica') or (faseGara eq '5' and param.paginaFasiGara eq 'aperturaOffAggProvLottoOffUnica' and (modlicg eq '6' or attivaValutazioneTec eq 'true' ))}">
												<tr>
													<td class="vocemenulaterale">
														<a href="javascript:AttivaAperturaEconomiche();" title='Attiva apertura offerte economiche' tabindex="1514">
															Attiva apertura offerte economiche
														</a>
													</td>
												</tr>
											</c:if>
										</c:if>
									</c:if>
								</c:if>
								
								<c:if test="${bustalotti ne '1' && isProceduraTelematica eq 'true' && updateLista ne 1 && meruolo eq '1'  && autorizzatoModifiche ne 2 
									&& ((paginaAttivaWizard eq step6Wizard && faseGara eq 5) || (paginaAttivaWizard eq step7Wizard && faseGara eq 6)) && gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.AnnullaAperturaOfferte')}">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:AnnullaAperturaOfferte('${key}','${bustalotti }');" title='Annulla apertura offerte' tabindex="1515">
												Annulla apertura offerte
											</a>
										</td>
									</tr>
								</c:if>
							
								<c:if test='${autorizzatoModifiche ne "2" and riservatezzaAttiva eq "1" && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIGARA.AnnullaRiservatezza")}' >
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:apriPopupAnnullaRiservatezza('${valoreChiaveRiservatezza}','${idconfi}');" title='Annulla riservatezza su documentale' tabindex="1516">
												Annulla riservatezza su documentale
											</a>
										</td>
									</tr>
								</c:if>
								
					<c:choose>
						<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
							<c:if test='${paginaAttivaWizard > step1Wizard && (paginaAttivaWizard ne step6Wizard  && !(paginaAttivaWizard eq step7Wizard && 
								!attivaValutazioneTec && modalitaAggiudicazioneGara ne "6") )}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:indietro();" title='Fase precedente' tabindex="1517">
												< Fase precedente
											</a>
										</td>
									</tr>
							</c:if>
						</c:when>
						<c:when test='${isGaraLottiConOffertaUnica eq "true"}'>
							<c:if test='${paginaAttivaWizard eq step7Wizard and faseGara <7  and autorizzatoModifiche ne 2}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:confermaChiusuraAperturaFasi('ATTIVA','${bustalotti}');" title='Attiva calcolo aggiudicazione' tabindex="1518">
											Attiva calcolo aggiudicazione
										</a>
									</td>
								</tr>
							</c:if>
							<c:choose>
								<c:when test='${empty param.aggiudProvDefOffertaUnica and paginaAttivaWizard > step1Wizard}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:indietro();" title='Fase precedente' tabindex="1519">
												< Fase precedente
											</a>
										</td>
									</tr>
								</c:when>
								<c:when test='${not empty param.aggiudProvDefOffertaUnica and paginaAttivaWizard > step8Wizard}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:indietro();" title='Fase precedente' tabindex="1519">
												< Fase precedente
											</a>
										</td>
									</tr>
								</c:when>
							</c:choose>
						</c:when>
					</c:choose>

					<c:choose>
						<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
							<c:if test="${(paginaAttivaWizard < step9Wizard and isProceduraTelematica ne 'true') or (paginaAttivaWizard < step9Wizard and paginaAttivaWizard != step3Wizard and paginaAttivaWizard != step5Wizard and paginaAttivaWizard != step6_5Wizard and isProceduraTelematica eq 'true') or (paginaAttivaWizard == step3Wizard and !(isProceduraTelematica eq 'true' and (faseGara eq '2' or faseGara eq '3' or faseGara eq '4')))
									or (paginaAttivaWizard == step5Wizard and !(isProceduraTelematica eq 'true' and (faseGara eq '2' or faseGara eq '3' or faseGara eq '4'))) or (paginaAttivaWizard == step6_5Wizard and !(isProceduraTelematica eq 'true' and (faseGara eq '2' or faseGara eq '3' or faseGara eq '4' or faseGara eq '5')))}"> <!-- c'rea il valore 8 -->
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:avanti();" title='Fase seguente' tabindex="1520">
												Fase seguente >
											</a>
										</td>
									</tr>
							</c:if>
						</c:when>
						<c:when test='${isGaraLottiConOffertaUnica eq "true"}'>
	
							<c:choose>
								<c:when test="${empty param.aggiudProvDefOffertaUnica and ((paginaAttivaWizard < step7Wizard and isProceduraTelematica ne 'true')  or (paginaAttivaWizard < step7Wizard and paginaAttivaWizard != step3Wizard and paginaAttivaWizard != step5Wizard and paginaAttivaWizard != step6_5Wizard and isProceduraTelematica eq 'true') or (paginaAttivaWizard == step3Wizard and !(isProceduraTelematica eq 'true' and (faseGara eq '2' or faseGara eq '3' or faseGara eq '4')))
									or (paginaAttivaWizard == step5Wizard and !(isProceduraTelematica eq 'true' and (faseGara eq '2' or faseGara eq '3' or faseGara eq '4'))) or (paginaAttivaWizard == step6_5Wizard and !(isProceduraTelematica eq 'true' and (faseGara eq '2' or faseGara eq '3' or faseGara eq '4' or faseGara eq '5'))))}">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:avanti();" title='Fase seguente' tabindex="1520">
												Fase seguente >
											</a>
										</td>
									</tr>
								</c:when>
								<c:when test='${not empty param.aggiudProvDefOffertaUnica and paginaAttivaWizard < step9Wizard}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:avanti();" title='Fase seguente' tabindex="1520">
												Fase seguente >
											</a>
										</td>
									</tr>
								</c:when>
							</c:choose>
	
						</c:when>
					</c:choose>
							</c:otherwise>
						</c:choose>
					</gene:redefineInsert>
					
					<c:set var="IsW_CONFCOMPopolata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,"4")}' />
					
					<c:set var="tipoImpresa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaImpresaFunction",  pageContext,chiaveRigaJava)}'/>
					
					<c:choose>
						<c:when test='${(empty updateLista or updateLista ne 1) and (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InvioComunicazione") ||
						 (paginaAttivaWizard == step1Wizard and (tipoImpresa == "2" || tipoImpresa == "11") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.dettaglioDitteConsorziate")) ||
						 (paginaAttivaWizard == step1Wizard and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RicorsoAvvalimento")) ||
						 (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RichiestaSoccorsoIstruttorio") and (paginaAttivaWizard == step6Wizard || paginaAttivaWizard == step7Wizard)))}' >
						 <c:set var="numAvvalimentiDitta" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumeroAvvalimentiDittaFunction", pageContext, gene:getValCampo(chiaveRigaJava, "NGARA5"),gene:getValCampo(chiaveRigaJava, "DITTAO"))}' />
							<c:choose>
								<c:when test="${numAvvalimentiDitta ne '0'}">
									<c:set var="numAvvalimentiDitta" value="(${numAvvalimentiDitta})"/>
								</c:when>
								<c:otherwise>
									<c:set var="numAvvalimentiDitta" value=""/>
								</c:otherwise>
							</c:choose>
							<gene:campoLista title='' width='25'  >
								<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'" >
									<c:if test='${paginaAttivaWizard == step1Wizard and (tipoImpresa == "2" || tipoImpresa == "11") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.dettaglioDitteConsorziate")}'>
										<gene:PopUpItem title="Dettaglio consorziate esecutrici" href="listaDitteConsorziate('${chiaveRigaJava}')" />
									</c:if>
									<c:if test='${paginaAttivaWizard == step1Wizard and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RicorsoAvvalimento")}'>
										<gene:PopUpItem title="Avvalimento ${numAvvalimentiDitta}" href="dettaglioAvvalimento('${chiaveRigaJava}')" />
									</c:if>
									<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InvioComunicazione") }'>
										<c:set var="inviaComunicazioneAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetAbilitazioneInvioComunicazioneFunction",  pageContext, chiaveRigaJava, abilitatoInvioMailDocumentale)}' />
										<gene:PopUpItem title="Invia comunicazione" href="inviaComunicazione('${chiaveRigaJava}','${inviaComunicazioneAbilitato }','${idconfi}')" />
									</c:if>
									<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.RichiestaSoccorsoIstruttorio') && (paginaAttivaWizard == step1Wizard || paginaAttivaWizard == step6Wizard || paginaAttivaWizard == step7Wizard) && autorizzatoModifiche ne 2}">
											<c:set var="statoSoccIstr" value="${datiRiga.V_DITGAMMIS_AMMGAR}"/>
											<c:choose>
												<c:when  test="${paginaAttivaWizard == step1Wizard and modGaraInversa eq 'true' and statoSoccIstr ne 10}">
													<c:set var="whereSoccorso" value="CODGAR5='${codiceGara }' and DITTAO='${datiRiga.DITG_DITTAO}' and AMMINVERSA='10'"/>
													<c:set var="numLottiSoccorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "count(CODGAR5)","DITG", whereSoccorso)}'/>
													<c:if test="${not empty numLottiSoccorso and numLottiSoccorso ne 0 }">
														<c:set var="statoSoccIstr" value="10"/>
													</c:if>
												</c:when>
												<c:when test="${ bustalotti eq '2' and statoSoccIstr ne 10}">
													<c:set var="whereSoccorso" value="CODGAR='${codiceGara }' and DITTAO='${datiRiga.DITG_DITTAO}' and CODGAR!=NGARA and FASGAR=${varTmp} and AMMGAR='10'"/>
													<c:set var="numLottiSoccorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "count(CODGAR)","V_DITGAMMIS", whereSoccorso)}'/>
													<c:if test="${not empty numLottiSoccorso and numLottiSoccorso ne 0 }">
														<c:set var="statoSoccIstr" value="10"/>
													</c:if>
												</c:when>
											</c:choose>
											<c:if test="${statoSoccIstr eq '10' }">
												<c:set var="modelloSoccoroIstruttorioConfigurato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,"56")}' />
												<gene:PopUpItem title="Richiedi soccorso istruttorio" href="richiestaSoccorsoIstruttorio('${chiaveRigaJava}','${modelloSoccoroIstruttorioConfigurato }','${idconfi}','${numeroModello }','${varTmp }')" />	
											</c:if>
											
										</c:if>
								</gene:PopUp>
							</gene:campoLista>
						</c:when>
						<c:otherwise>
							<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" visibile="false" />
						</c:otherwise>
					</c:choose>
					
					<gene:campoLista campo="CODGAR5" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="DITTAO"  visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="NGARA5"  visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista title="N.pl" campo="NUMORDPL" headerClass="sortable" width="32" />
					<c:set var="link" value='javascript:archivioImpresa("${datiRiga.DITG_DITTAO}");' />
					<gene:campoLista campo="NOMIMO" headerClass="sortable" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}' />
					
					<gene:campoLista campo="AMMGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${varTmp}" width="80" headerClass="sortable" ordinabile ="true" visibile="${paginaAttivaWizard eq step1Wizard or (paginaAttivaWizard >= step4Wizard and paginaAttivaWizard <= step7Wizard)}"  edit="${updateLista eq 1  and (modGaraInversa ne 'true' or (modGaraInversa eq 'true' and (empty faseGara or faseGara <5)))}" />
					<c:if test="${paginaAttivaWizard eq step7Wizard and garaInversa eq '1' and bustalotti ne '2'}">
						<gene:campoLista campo="AMMINVERSA" headerClass="sortable" edit="false" title="Esito verif. proc.inversa" width="80" />
					</c:if>
					<gene:campoLista campo="ESTIMP" title="Sortegg.per verifica?" headerClass="sortable" visibile="${paginaAttivaWizard eq step1Wizard && compreq eq '1'}" edit="${updateLista eq 1 && fn:startsWith(descTabA1158,'0')}" />
					<gene:campoLista campo="PARTGAR" headerClass="sortable" visibile="false" edit="${updateLista eq 1}" />
					

					<gene:campoLista campo="INVOFF"  visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="INVGAR"  visibile="false" edit="${updateLista eq 1}" />

					<gene:campoLista campo="FASGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="MOTIVESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="DETMOTESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />
					
					<c:set var="tooltipRibauo" value="Ribasso offerto"/>
					<c:if test="${ribcal eq '3' }">
						<c:set var="tooltipRibauo" value="Ribasso pesato"/>
					</c:if>
					<c:if test="${modalitaAggiudicazioneGara eq '17'}">
						<c:set var="tooltipRibauo" value="Rialzo offerto"/>
					</c:if>

					<c:choose>
						<c:when test='${paginaAttivaWizard eq step8Wizard and modalitaAggiudicazioneGara eq 6}'>
							<gene:campoLista title="Punteggio" width="130" campo="RIBAUO" headerClass="sortable" visibile="${paginaAttivaWizard eq step8Wizard}" edit="false" definizione="F13.9;0;;;RIBAUO"/>
						</c:when>
						<c:when test='${paginaAttivaWizard eq step8Wizard and modalitaAggiudicazioneGara ne 6}'>
							<gene:campoLista title="${tooltipRibauo }" tooltip="${tooltipRibauo }" width="130" campo="RIBAUO" headerClass="sortable" visibile="${paginaAttivaWizard eq step7Wizard or paginaAttivaWizard eq step8Wizard}" edit="${updateLista eq 1 and paginaAttivaWizard ne step8Wizard}" definizione="F13.9;0;;PRC;RIBAUO"/>
						</c:when>
						<c:when test='${paginaAttivaWizard eq step7Wizard and isGaraLottiConOffertaUnica eq "true"}'>
							<gene:campoLista title="${tooltipRibauo }" tooltip="${tooltipRibauo }" width="130" campo="RIBAUO" visibile="false" edit="${updateLista eq 1}" definizione="F13.9;0;;PRC;RIBAUO"/>
						</c:when>
						<c:otherwise>
							<gene:campoLista title="${tooltipRibauo }" tooltip="${tooltipRibauo }" width="130" campo="RIBAUO" headerClass="sortable" visibile="${modalitaAggiudicazioneGara ne 6 and (paginaAttivaWizard eq step7Wizard or paginaAttivaWizard eq step8Wizard)}" edit="${updateLista eq 1 and paginaAttivaWizard ne step8Wizard}" definizione="F13.9;0;;PRC;RIBAUO"/>
						</c:otherwise>
					</c:choose>
					<gene:campoLista title="Ribasso offerto" tooltip="Ribasso offerto" width="130" campo="RIBOEPV" headerClass="sortable" visibile="${riboepvVis and ((paginaAttivaWizard eq step7Wizard and isGaraLottiConOffertaUnica ne 'true' ) or paginaAttivaWizard eq step8Wizard)  }" edit="${updateLista eq 1 and paginaAttivaWizard ne step8Wizard and offtel ne '1' }" definizione="F13.9;0;;PRC;G1RIBOEPV"/>
					<c:choose>
						<c:when test='${paginaAttivaWizard eq step7Wizard and isGaraLottiConOffertaUnica eq "true"}'>
							<gene:campoLista  campo="IMPOFF" title="Importo" visibile="false" edit="${updateLista eq 1}" />
						</c:when>
						<c:when test='${paginaAttivaWizard eq step8Wizard and !empty ULTDETLIC }'>
							<gene:campoLista  campo="IMPOFF" title="Importo" visibile="false" edit="${updateLista eq 1}" />
							
							<gene:campoLista title="Offerta congiunta" computed = "true" ordinabile = "false" campo="IMPOFF - ${gene:getDBFunction(pageContext,'isnull','IMPPERM;0')} + ${gene:getDBFunction(pageContext,'isnull','IMPCANO;0')}" definizione="N24.5;0;;MONEY" visibile="true" />
						</c:when>
						<c:otherwise>
							<c:set var="ImportoVisibile" value="${(paginaAttivaWizard eq step7Wizard or paginaAttivaWizard eq step8Wizard) and (detlicg eq 4 or modalitaAggiudicazioneGara eq 5 or (modalitaAggiudicazioneGara eq 6 and (isVecchiaOepv or formato50 or formato52)) or modalitaAggiudicazioneGara eq 14 or modalitaAggiudicazioneGara eq 16) and visOffertaEco}"/>
							<gene:campoLista campo="IMPOFF" title="Importo" width="${gene:if(updateLista eq 1, '150', '')}" headerClass="sortable" tooltip="${labelImpoff }" visibile="${ImportoVisibile}" edit="${updateLista eq 1 and paginaAttivaWizard ne step8Wizard}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${paginaAttivaWizard == step7Wizard and updateLista ne 1 and (modalitaAggiudicazioneGara eq 6 or modalitaAggiudicazioneGara eq 5 or modalitaAggiudicazioneGara eq 14 or ((modalitaAggiudicazioneGara eq 1 or modalitaAggiudicazioneGara eq 13 or modalitaAggiudicazioneGara eq 17) and ribcal eq '2'))}">
						<gene:campoLista campo="IMPOFF_FIT" visibile="false" edit="true" campoFittizio="true" definizione="F13.9" value="${datiRiga.DITG_IMPOFF}"/>
					</c:if>
					<c:if test="${paginaAttivaWizard == step7Wizard and updateLista ne 1 and (modalitaAggiudicazioneGara eq 1 or modalitaAggiudicazioneGara eq 5 or modalitaAggiudicazioneGara eq 13 or modalitaAggiudicazioneGara eq 14 or modalitaAggiudicazioneGara eq 17)}">
						<gene:campoLista campo="RIBAUO_FIT" visibile="false" edit="true" campoFittizio="true" definizione="F13.9" value="${datiRiga.DITG_RIBAUO}"/>
					</c:if>
					
					<gene:campoLista campo="STAGGI" headerClass="sortable" visibile="${paginaAttivaWizard eq step8Wizard}" />
					<c:if test='${paginaAttivaWizard eq step8Wizard}'>
                          <c:choose>
							<c:when test="${ datiRiga.DITG_STAGGIALI eq 2 and datiRiga.DITG_STAGGI eq 3}">
								<c:set var="nomeImg" value="staggi3sup.png"/>
								<c:set var="desc" value="Anomala e ala superiore"/>
							</c:when>
							<c:when test="${ datiRiga.DITG_STAGGIALI eq 2 and datiRiga.DITG_STAGGI eq 6}">
								<c:set var="nomeImg" value="staggi6sup.png"/>
								<c:set var="desc" value="Non anomala e ala superiore"/>
							</c:when>
							<c:when test="${ datiRiga.DITG_STAGGIALI eq 7 and datiRiga.DITG_STAGGI eq 3}">
								<c:set var="nomeImg" value="staggi3inf.png"/>
								<c:set var="desc" value="Anomala e ala inferiore"/>
							</c:when>
							<c:when test="${ datiRiga.DITG_STAGGIALI eq 7 and datiRiga.DITG_STAGGI eq 6}">
								<c:set var="nomeImg" value="staggi6inf.png"/>
								<c:set var="desc" value="Non anomala e ala inferiore"/>
							</c:when>
							<c:otherwise>
								<c:set var="nomeImg" value="staggi${datiRiga.DITG_STAGGI}.png"/>
								<c:choose>
									<c:when test="${ datiRiga.DITG_STAGGI eq 1}">
										<c:set var="desc" value="Fuori limite"/>
									</c:when>
									<c:when test="${ datiRiga.DITG_STAGGI eq 2}">
										<c:set var="desc" value="Ala superiore"/>
									</c:when>
									<c:when test="${ datiRiga.DITG_STAGGI eq 3}">
										<c:set var="desc" value="Anomala"/>
									</c:when>
									<c:when test="${ datiRiga.DITG_STAGGI eq 4}">
										<c:set var="desc" value="Prima ditta classificata"/>
									</c:when>
									<c:when test="${ datiRiga.DITG_STAGGI eq 5}">
										<c:set var="desc" value="Seconda ditta classificata"/>
									</c:when>
									<c:when test="${ datiRiga.DITG_STAGGI eq 6}">
										<c:set var="desc" value="Non anomala"/>
									</c:when>
									<c:when test="${ datiRiga.DITG_STAGGI eq 7}">
										<c:set var="desc" value="Ala inferiore"/>
									</c:when>
									<c:when test="${ datiRiga.DITG_STAGGI eq 10}">
										<c:set var="desc" value="In graduatoria"/>
									</c:when>
								</c:choose>
								
							</c:otherwise>
						</c:choose>
						<gene:campoLista title="&nbsp;" width="20">
                            <img width="16" height="16" alt= "Stato di aggiudicazione" src="${pageContext.request.contextPath}/img/${nomeImg}" title="${desc}" alt="${desc}"/>
						</gene:campoLista>
					</c:if>
					<gene:campoLista campo="CONGRUO" headerClass="sortable" visibile="${paginaAttivaWizard eq step8Wizard }" edit="${updateLista eq 1}" />
					<c:if test="${paginaAttivaWizard eq step8Wizard && garaInversa eq '1'}">
						<gene:campoLista campo="AMMINVERSA" headerClass="sortable" edit="false" title="Esito verif. proc.inversa" width="80"/>
					</c:if>
					<gene:campoLista campo="CONGMOT" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="STAGGI_FIT" visibile="false" campoFittizio="true" edit="true" definizione="N7" value="${datiRiga.DITG_STAGGI}"/>
					<gene:campoLista campo="STAGGIALI" visibile="false" />
					
					<c:set var="estensioneDisabilitataFS11A" value=''/>
					<c:set var="linkAbilitatoFS11A" value='true'/>
					<c:if test="${condizioniGestioneBusteFS11A}">
						<c:set var="isBustaElaborataFS11A" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.IsBustaElaborataFunction", pageContext,datiRiga.DITG_NGARA5,datiRiga.DITG_DITTAO, "FS11A")}' />
						<c:if test="${isBustaElaborataFS11A eq 'No' }">
							<c:set var="estensioneDisabilitataFS11A" value='-disabilitato'/>
							<c:set var="linkAbilitatoFS11A" value='false'/>
						</c:if>
					</c:if>
						
					<c:set var="estensioneDisabilitataFS11B" value=''/>
					<c:set var="linkAbilitatoFS11B" value='true'/>
					<c:if test="${condizioniGestioneBusteFS11B}">
						<c:set var="isBustaElaborataFS11B" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.IsBustaElaborataFunction", pageContext,datiRiga.DITG_NGARA5,datiRiga.DITG_DITTAO, "FS11B")}' />
						<c:if test="${isBustaElaborataFS11B eq 'No' }">
							<c:set var="estensioneDisabilitataFS11B" value='-disabilitato'/>
							<c:set var="linkAbilitatoFS11B" value='false'/>
						</c:if>
					</c:if>
						
					<c:set var="estensioneDisabilitataFS11C" value=''/>
					<c:set var="linkAbilitatoFS11C" value='true'/>
					<c:if test="${condizioniGestioneBusteFS11C}">
						<c:set var="isBustaElaborataFS11C" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.IsBustaElaborataFunction", pageContext,datiRiga.DITG_NGARA5,datiRiga.DITG_DITTAO, "FS11C")}' />
						<c:if test="${isBustaElaborataFS11C eq 'No' }">
							<c:set var="estensioneDisabilitataFS11C" value='-disabilitato'/>
							<c:set var="linkAbilitatoFS11C" value='false'/>
						</c:if>
					</c:if>
						
					
					<c:if test='${paginaAttivaWizard eq step1Wizard && updateLista ne 1 && (bustalotti eq 1 || bustalotti eq 2)}' >
						<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.DITG-OFFUNICA-PARTGAR") }'>
							<gene:campoLista title="&nbsp;" width="20" >
								<a href="javascript:chiaveRiga='${chiaveRigaJava}';dettaglioPartecipazioneLotti('${chiaveRigaJava}',${bustalotti });" title="Dettaglio partecipazione ai lotti" >
									<img width="16" height="16" title="Dettaglio invio offerta lotti" alt="Dettaglio invio offerta lotti" src="${pageContext.request.contextPath}/img/partecipazioneAiLotti.png"/>
								</a>
							</gene:campoLista>
						</c:if>
						<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.DettaglioAmmissioneDittaLotti")}' >
							<gene:campoLista title="&nbsp;" width="20" >
								<a href="javascript:chiaveRiga='${chiaveRigaJava}';dettaglioAmmissioneLotti('${chiaveRigaJava}',${bustalotti });" title="Dettaglio ammissione ai lotti" >
									<img width="16" height="16" title="Dettaglio ammissione ai lotti" alt="Dettaglio ammissione ai lotti" src="${pageContext.request.contextPath}/img/ammissioneAiLotti.png"/>
								</a>
							</gene:campoLista>
						</c:if>
					</c:if>
					
			<c:choose>
				<c:when test='${paginaAttivaWizard eq step6Wizard}'>
					<c:choose>
						<c:when test='${ isGaraLottiConOffertaUnica eq "true" and (updateLista ne 1) and gene:checkProt(pageContext, "MASC.VIS.GARE.DITG-OFFUNICA")}'>
	          				<gene:campoLista campo="INFO_PUNTEC" title="" visibile="${numeroLottiOEPV >0}" campoFittizio="true" definizione="T20" width="40"  gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoInfoPunteggioTecnico"/>
						</c:when>
					</c:choose>
					<c:choose>
						<c:when test='${isGaraLottiConOffertaUnica eq "true" and updateLista ne 1 and gene:checkProt(pageContext, "MASC.VIS.GARE.DITG-OFFUNICA")}' >
							<gene:campoLista campo="REQMIN" visibile="false" edit="${updateLista eq 1}" />
							<gene:campoLista campo="PUNTEC" visibile="false" edit="${updateLista eq 1}" />
							<gene:campoLista title="&nbsp;" width="20">
								<c:if test="${linkAbilitatoFS11B}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';dettaglioOffertaDittaPerSingoliLotti();" title="Valutazione tecnica della ditta su tutti i lotti" ></c:if>
											<img width="16" height="16" title="Valutazione tecnica della ditta su tutti i lotti" alt="Valutazione tecnica della ditta su tutti i lotti" src="${pageContext.request.contextPath}/img/offertaditta${estensioneDisabilitataFS11B}.png"/>
								<c:if test="${linkAbilitatoFS11B}"></a></c:if>
							</gene:campoLista>
						</c:when>
						<c:when test='${isGaraLottiConOffertaUnica eq "true" and updateLista eq 1}' >
							<gene:campoLista campo="REQMIN" visibile="false" edit="${updateLista eq 1}" />
							<gene:campoLista campo="PUNTEC" visibile="false" edit="${updateLista eq 1}" />
							<gene:campoLista campo="PUNTECRIP" visibile="false" edit="${updateLista eq 1}" />
						</c:when>
						<c:otherwise>
							<gene:campoLista campo="REQMIN" visibile="${attivaValutazioneTec}" edit="${updateLista eq 1}" />
							<c:if test="${garaInversa eq '1'}">
								<gene:campoLista campo="AMMINVERSA" headerClass="sortable" edit="false" title="Esito verif. proc.inversa" width="80" />
							</c:if>
							<gene:campoLista campo="PUNTEC" visibile="${modalitaAggiudicazioneGara eq 6}" edit="false" />
							<gene:campoLista campo="PUNTECRIP" title="Punteggio riparametrato" visibile="${modalitaAggiudicazioneGara eq 6 && (RIPTEC eq 1 || RIPTEC eq 2)}" edit="false" />	
							
						</c:otherwise>
					</c:choose>
					<gene:campoLista campo="PUNTEC_FIT" visibile="false" edit="true" campoFittizio="true" definizione="F13.9" value="${datiRiga.DITG_PUNTEC}"/>
					<gene:campoLista campo="REQMIN_FIT"  visibile="false" edit="true" campoFittizio="true" definizione="T1" value="${datiRiga.DITG_REQMIN}"/>
					<gene:campoLista campo="PUNTECRIP_FIT" visibile="false" edit="true" campoFittizio="true" definizione="F13.9" value="${datiRiga.DITG_PUNTECRIP}"/>
				</c:when>
				<c:otherwise>
					<gene:campoLista campo="REQMIN" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="PUNTEC" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="PUNTEC_FIT" visibile="false" edit="true" campoFittizio="true" definizione="F13.9" value="${datiRiga.DITG_PUNTEC}"/>
					<gene:campoLista campo="PUNTECRIP" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="PUNTECRIP_FIT" visibile="false" edit="true" campoFittizio="true" definizione="F13.9" value="${datiRiga.DITG_PUNTECRIP}"/>
				</c:otherwise>
			</c:choose>
			
			<c:if test="${paginaAttivaWizard eq step6Wizard and isGaraLottiConOffertaUnica ne 'true' and updateLista ne 1 and modlicg eq '6'}">
				<gene:campoLista title="&nbsp;" width="20">
				<c:set var="tipoTec" value="1"/>
				<c:if test="${sezionitec eq '1' }">
					<c:set var="tipoTec" value="1-sez"/>
				</c:if>
				<c:if test="${linkAbilitatoFS11B}"><a href="javascript:dettaglioValutazioneTecEco('${chiaveRigaJava}','${tipoTec}','${esistonoDitteConPunteggio}','${faseGara}','${autorizzatoModifiche}');" title="Dettaglio valutazione tecnica" ></c:if>
							<img width="16" height="16" title="Dettaglio valutazione tecnica" alt="Dettaglio valutazione tecnica" src="${pageContext.request.contextPath}/img/valutazionetecnica${estensioneDisabilitataFS11B}.png"/>
				<c:if test="${linkAbilitatoFS11B}"></a></c:if>
				</gene:campoLista>
			</c:if>
			
			<c:if test="${(paginaAttivaWizard eq step6Wizard and isGaraLottiConOffertaUnica ne 'true' ) and (modlicg eq '5' or modlicg eq '14' or modlicg eq '16' or modlicg eq '6') and (updateLista ne 1) and attivaValutazioneTec and gene:checkProtFunz(pageContext, 'ALT', 'VisualizzaDettaglioPrezzi')}" >
				<gene:campoLista title="&nbsp;" width="20">
					<c:if test="${datiRiga.DITG_PARTGAR ne 2 }">
						<c:if test="${linkAbilitatoFS11B}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';DettaglioOffertaPrezzi('${chiaveRigaJava}');" title="Verifica conformità lavorazioni e forniture" ></c:if>
						<img width="16" height="16" title="Verifica conformità lavorazioni e forniture" alt="Verifica conformità lavorazioni e forniture" src="${pageContext.request.contextPath}/img/offertaprezzi${estensioneDisabilitataFS11B}.png"/>
					<c:if test="${linkAbilitatoFS11B}"></a></c:if>
					</c:if>
					
				</gene:campoLista>
			</c:if>

			<c:choose>
				<c:when test='${paginaAttivaWizard eq step7Wizard}'>
					<c:choose>
	        	  <c:when test='${ isGaraLottiConOffertaUnica eq "true" and (updateLista ne 1) and gene:checkProt(pageContext, "MASC.VIS.GARE.DITG-OFFUNICA")}'>
            		<gene:campoLista campo="INFO_OFFEC" title="" campoFittizio="true" definizione="T20" width="40"  gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoInfoOffertaEconomica"/>
	          	  </c:when>
	        		</c:choose>
					<c:choose>
						<c:when test='${isGaraLottiConOffertaUnica eq "true" and updateLista ne 1 and gene:checkProt(pageContext, "MASC.VIS.GARE.DITG-OFFUNICA")}' >
							<gene:campoLista campo="PUNECO" visibile="false" edit="${updateLista eq 1 and paginaAttivaWizard ne step8Wizard}" definizione="F13.9;0;;;PUNECO"/>
							<gene:campoLista title="&nbsp;" width="20">
								<c:if test="${linkAbilitatoFS11C}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';dettaglioOffertaDittaPerSingoliLotti();" title="Offerta economica della ditta su tutti i lotti" ></c:if>
									<img width="16" height="16" title="Offerta economica della ditta su tutti i lotti" alt="Offerta economica della ditta su tutti i lotti" src="${pageContext.request.contextPath}/img/offertaditta${estensioneDisabilitataFS11C}.png"/>
								<c:if test="${linkAbilitatoFS11C}"></a></c:if>
							</gene:campoLista>
						</c:when>
						<c:when test='${isGaraLottiConOffertaUnica eq "true" and updateLista eq 1}' >
							<gene:campoLista campo="PUNECO" visibile="false" edit="${updateLista eq 1 and paginaAttivaWizard ne step8Wizard}" definizione="F13.9;0;;;PUNECO"/>
						</c:when>
						<c:otherwise>
							<gene:campoLista campo="PUNECO" width="130" headerClass="sortable" visibile="${modalitaAggiudicazioneGara eq 6}" edit="${updateLista eq 1 and paginaAttivaWizard ne step8Wizard and paginaAttivaWizard ne step7Wizard}" definizione="F13.9;0;;;PUNECO"/>
							<gene:campoLista campo="PUNECORIP" title="Punteggio riparametrato" width="130" headerClass="sortable" visibile="${modalitaAggiudicazioneGara eq 6 && (RIPECO eq 1 || RIPECO eq 2)}" edit="${updateLista eq 1 and paginaAttivaWizard ne step8Wizard and paginaAttivaWizard ne step7Wizard}" definizione="F13.9;0;;;PUNECORIP"/>
						</c:otherwise>
					</c:choose>
						<gene:campoLista campo="PUNECO_FIT" visibile="false" edit="true" campoFittizio="true" definizione="F13.9" value="${datiRiga.DITG_PUNECO}"/>
						<gene:campoLista campo="PUNECORIP_FIT" visibile="false" edit="true" campoFittizio="true" definizione="F13.9" value="${datiRiga.DITG_PUNECORIP}"/>
				</c:when>
				<c:otherwise>
					<gene:campoLista campo="PUNECO" visibile="false" edit="${updateLista eq 1 and paginaAttivaWizard ne step8Wizard}" definizione="F13.9;0;;;PUNECO"/>
					<gene:campoLista campo="PUNECORIP" visibile="false" edit="${updateLista eq 1 and paginaAttivaWizard ne step8Wizard}" definizione="F13.9;0;;;PUNECORIP"/>					
				</c:otherwise>
			</c:choose>
			
			
			<c:if test="${paginaAttivaWizard eq step7Wizard and isGaraLottiConOffertaUnica ne 'true' and updateLista ne 1 and modlicg eq '6'}">
				<gene:campoLista title="&nbsp;" width="20">
				<c:if test="${linkAbilitatoFS11C}"><a href="javascript:dettaglioValutazioneTecEco('${chiaveRigaJava}','2','${esistonoDitteConPunteggio}','${faseGara}','${autorizzatoModifiche}');" title="Dettaglio valutazione economica" ></c:if>
							<img width="16" height="16" title="Dettaglio valutazione economica" alt="Dettaglio valutazione economica" src="${pageContext.request.contextPath}/img/valutazionetecnica${estensioneDisabilitataFS11C}.png"/>
				<c:if test="${linkAbilitatoFS11C}"></a></c:if>
				</gene:campoLista>
			</c:if>
					
					<c:if test="${(paginaAttivaWizard eq step7Wizard and isGaraLottiConOffertaUnica ne 'true' ) and (modlicg eq '5' or modlicg eq '14' or modlicg eq '16' or (modlicg eq '6' and (isVecchiaOepv or formato52))) and (updateLista ne 1) and gene:checkProtFunz(pageContext, 'ALT', 'VisualizzaDettaglioPrezzi')}" >
						<gene:campoLista title="&nbsp;" width="20">
							<c:if test="${datiRiga.DITG_PARTGAR ne 2 }">
								<c:if test="${linkAbilitatoFS11C}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';DettaglioOffertaPrezzi('${chiaveRigaJava}');" title="Dettaglio offerta prezzi" ></c:if>
								<img width="16" height="16" title="Dettaglio offerta prezzi" alt="Dettaglio offerta prezzi" src="${pageContext.request.contextPath}/img/offertaprezzi${estensioneDisabilitataFS11C}.png"/>
							<c:if test="${linkAbilitatoFS11C}"></a></c:if>
							</c:if>
							
						</gene:campoLista>
					</c:if>
					<c:set var="locTmp1" value="${step1Wizard}${step4Wizard}${step6Wizard}${step7Wizard}${step8Wizard}"/>
					<c:choose>
						<c:when test="${paginaAttivaWizard eq step1Wizard }" >
							<c:set var="estensioneDisabilitata" value='${estensioneDisabilitataFS11A}'/>
							<c:set var="linkAbilitato" value='${linkAbilitatoFS11A}'/>
						</c:when>
						<c:when test="${paginaAttivaWizard eq step6Wizard }" >
							<c:set var="estensioneDisabilitata" value='${estensioneDisabilitataFS11B}'/>
							<c:set var="linkAbilitato" value='${linkAbilitatoFS11B}'/>
						</c:when>
						<c:when test="${paginaAttivaWizard eq step7Wizard }" >
							<c:set var="estensioneDisabilitata" value='${estensioneDisabilitataFS11C}'/>
							<c:set var="linkAbilitato" value='${linkAbilitatoFS11C}'/>
						</c:when>
						<c:when test="${paginaAttivaWizard eq step8Wizard }" >
							<c:set var="estensioneDisabilitata" value=''/>
							<c:set var="linkAbilitato" value='true'/>
						</c:when>
					</c:choose>
					<c:if test='${fn:contains(locTmp1, paginaAttivaWizard) and updateLista ne 1 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.VerificaDocumenti")}' >
						<gene:campoLista title="&nbsp;" width="20" >
							<c:if test="${linkAbilitato}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';verificaDocumentiRichiesti('${chiaveRigaJava}','VERIFICA','1','false','${autorizzatoModifiche }');" title="Verifica documenti richiesti" ></c:if>
								<img width="16" height="16" title="Verifica documenti richiesti" alt="Verifica documenti richiesti" src="${pageContext.request.contextPath}/img/documentazione${estensioneDisabilitata}.png"/>
							<c:if test="${linkAbilitato}"></a></c:if>
						</gene:campoLista>
					</c:if>
					<c:if test="${paginaAttivaWizard eq step1Wizard and updateLista ne 1 and !empty codiceElenco and codiceElenco != '' and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.VerificaDocumentiSelezioneDitteElenco')}" >
						<gene:campoLista title="&nbsp;" width="20" >
						<c:if test="${datiRiga.DITG_ACQUISIZIONE == 3 }">
							<a href="javascript:chiaveRiga='${chiaveRigaJava}';verificaDocumentiRichiesti('${chiaveRigaJava}','CONSULTAZIONE','0','true','${autorizzatoModifiche }');" title="'Consultazione documenti iscrizione a elenco" >
								<img width="16" height="16" title="Consultazione documenti iscrizione a elenco" alt="Consultazione documenti iscrizione a elenco" src="${pageContext.request.contextPath}/img/documentazione_elenco.png"/>
							</a>
						</c:if>
						</gene:campoLista>
					</c:if>
					<c:set var="locTmp2" value="${step1Wizard}${step4Wizard}${step6Wizard}${step7Wizard}${step8Wizard}"/>
					<c:if test="${fn:contains(locTmp2, paginaAttivaWizard)}" >
					<c:if test="${iconaNoteAttiva eq 1}">
						<gene:campoLista campo="ALTNOT"  visibile="false"/>
					</c:if>
					<gene:campoLista title="&nbsp;" width="20">
						<c:if test="${iconaNoteAttiva eq 1}">
							<c:choose>
								<c:when test="${not empty datiRiga.DITG_ALTNOT}">
									<c:set var="note" value="_note"/>
									<c:set var="iconaTooltip" value="Ulteriori dettagli con note"/>
								</c:when>
								<c:otherwise>
									<c:set var="note" value=""/>
									<c:set var="iconaTooltip" value="Ulteriori dettagli"/>
								</c:otherwise>
							</c:choose>	
						</c:if>
						<c:if test="${linkAbilitato}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';ulterioriCampi(${currentRow+1}, '${chiaveRigaJava}');" title="Ulteriori dettagli" ></c:if>
							<img width="16" height="16" title="${iconaTooltip}" alt="Ulteriori dettagli" src="${pageContext.request.contextPath}/img/opzioni${note}${estensioneDisabilitata}.png"/>
						<c:if test="${linkAbilitato}"></a></c:if>
					</gene:campoLista>
					</c:if>
					<c:if test="${condizioniGestioneBusteFS11A}" >
							<gene:campoLista title="&nbsp;" width="20">
								<c:choose>
									<c:when test="${isBustaElaborataFS11A eq 'No' }">
										<c:if test="${autorizzatoModifiche ne 2}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';aperturaBuste('${chiaveRigaJava}', 'FS11A');" title="Busta amministrativa da acquisire" ></c:if>
											<img width="16" height="16" title="Busta amministrativa da acquisire" alt="Busta amministrativa da acquisire" src="${pageContext.request.contextPath}/img/bustaChiusa.png"/>
										<c:if test="${autorizzatoModifiche ne 2}"></a></c:if>
									</c:when>
									<c:when test="${isBustaElaborataFS11A eq 'NonEsiste' }">
										<img width="16" height="16" title="Busta amministrativa non presentata" alt="Busta amministrativa non presentata" src="${pageContext.request.contextPath}/img/bustaApertaVuota.png"/>
									</c:when>
									<c:otherwise>
										<img width="16" height="16" title="Busta amministrativa acquisita" alt="Busta amministrativa acquisita" src="${pageContext.request.contextPath}/img/bustaAperta.png"/>
									</c:otherwise>
								</c:choose>
							</gene:campoLista>
						</c:if>
					<c:if test="${condizioniGestioneBusteFS11B}" >
						<c:choose>
							<c:when test="${sezionitec eq '1' }">
								<gene:campoLista title="&nbsp;" width="45">
								<c:set var="statoBustaFS11B" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetStatoBustaFunction", pageContext,datiRiga.DITG_NGARA5,datiRiga.DITG_DITTAO, "FS11B")}' />
								<c:choose>
									<c:when test="${statoBustaFS11B eq '5' or  statoBustaFS11B eq '17'}">
										<c:if test="${autorizzatoModifiche ne 2}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';aperturaBuste('${chiaveRigaJava}', 'FS11B-QL');" title="Busta tecnico qualitativa da acquisire" ></c:if>
											<img width="16" height="16" title="Busta tecnico qualitativa da acquisire" alt="Busta tecnico qualitativa da acquisire" src="${pageContext.request.contextPath}/img/bustaChiusa.png"/><c:if test="${autorizzatoModifiche ne 2}"></a></c:if>
									</c:when>
									<c:when test="${empty statoBustaFS11B }">
										<img width="16" height="16" title="Busta tecnico qualitativa non presentata" alt="Busta tecnico qualitativa non presentata" src="${pageContext.request.contextPath}/img/bustaApertaVuota.png"/>
									</c:when>
									<c:when test="${statoBustaFS11B eq '16' or statoBustaFS11B eq '6' or statoBustaFS11B eq '7'}">
										<img width="16" height="16" title="Busta tecnico qualitativa acquisita" alt="Busta tecnico qualitativa acquisita" src="${pageContext.request.contextPath}/img/bustaAperta.png"/>
									</c:when>
								</c:choose>
								&nbsp;
								<c:choose>
									<c:when test="${empty statoBustaFS11B }">
										<img width="16" height="16" title="Busta tecnico quantitativa non presentata" alt="Busta tecnico quantitativa non presentata" src="${pageContext.request.contextPath}/img/bustaApertaVuota.png"/>
									</c:when>
									<c:when test="${statoBustaFS11B eq '6'}">
										<img width="16" height="16" title="Busta tecnico quantitativa acquisita" alt="Busta tecnico quantitativa acquisita" src="${pageContext.request.contextPath}/img/bustaAperta.png"/>
									</c:when>
									<c:otherwise>
										<c:if test="${autorizzatoModifiche ne 2 and statoBustaFS11B eq '16'}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';aperturaBuste('${chiaveRigaJava}', 'FS11B-QN');" title="Busta tecnico quantitativa da acquisire" ></c:if>
											<c:if test="${statoBustaFS11B eq '5' or  statoBustaFS11B eq '17'}">
												<c:set var="disabilitata" value="-disabilitata"/>
											</c:if>
											<img width="16" height="16" title="Busta tecnico quantitativa da acquisire" alt="Busta tecnico quantitativa da acquisire" src="${pageContext.request.contextPath}/img/bustaChiusa${disabilitata}.png"/><c:if test="${autorizzatoModifiche ne 2 and statoBustaFS11B eq '16'}"></a></c:if>
									</c:otherwise>
								</c:choose>
								</gene:campoLista>
							</c:when>
							<c:otherwise>
								<gene:campoLista title="&nbsp;" width="20">
								<c:choose>
									<c:when test="${isBustaElaborataFS11B eq 'No' }">
										<c:if test="${autorizzatoModifiche ne 2}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';aperturaBuste('${chiaveRigaJava}', 'FS11B');" title="Busta tecnica da acquisire" ></c:if>
											<img width="16" height="16" title="Busta tecnica da acquisire" alt="Busta tecnica da acquisire" src="${pageContext.request.contextPath}/img/bustaChiusa.png"/>
										<c:if test="${autorizzatoModifiche ne 2}"></a></c:if>
									</c:when>
									<c:when test="${isBustaElaborataFS11B eq 'NonEsiste' }">
										<img width="16" height="16" title="Busta tecnica non presentata" alt="Busta tecnica non presentata" src="${pageContext.request.contextPath}/img/bustaApertaVuota.png"/>
									</c:when>
									<c:otherwise>
										<img width="16" height="16" title="Busta tecnica acquisita" alt="Busta tecnica acquisita" src="${pageContext.request.contextPath}/img/bustaAperta.png"/>
									</c:otherwise>
								</c:choose>
								</gene:campoLista>
							</c:otherwise>
						</c:choose>
							
					</c:if>
					<c:if test="${condizioniGestioneBusteFS11C}" >
						<gene:campoLista title="&nbsp;" width="20">
							<c:choose>
								<c:when test="${isBustaElaborataFS11C eq 'No' }">
									<c:if test="${autorizzatoModifiche ne 2}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';aperturaBuste('${chiaveRigaJava}', 'FS11C');" title="Busta economica da acquisire" ></c:if>
										<img width="16" height="16" title="Busta economica da acquisire" alt="Busta economica da acquisire" src="${pageContext.request.contextPath}/img/bustaChiusa.png"/>
									<c:if test="${autorizzatoModifiche ne 2}"></a></c:if>
								</c:when>
								<c:when test="${isBustaElaborataFS11C eq 'NonEsiste' }">
										<img width="16" height="16" title="Busta economica non presentata" alt="Busta economica non presentata" src="${pageContext.request.contextPath}/img/bustaApertaVuota.png"/>
									</c:when>
								<c:otherwise>
									<img width="16" height="16" title="Busta economica acquisita" alt="Busta economica acquisita" src="${pageContext.request.contextPath}/img/bustaAperta.png"/>
								</c:otherwise>
							</c:choose>
						</gene:campoLista>
					</c:if>
					<c:if test="${paginaAttivaWizard eq step1Wizard and updateLista ne 1 and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.NoteAvvisiImpresa')}" >
						<c:set var="numAvvalimentiDitta" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumeroAvvalimentiDittaFunction", pageContext, gene:getValCampo(chiaveRigaJava, "NGARA5"),gene:getValCampo(chiaveRigaJava, "DITTAO"))}' />
						<c:set var="result" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.CheckNoteAvvisiImpresaPartecipantiFunction", pageContext,datiRiga.DITG_DITTAO, datiRiga.DITG_NGARA5)}' />
						<gene:campoLista title="&nbsp;" width="20" >
							<c:choose>
								<c:when test="${numAvvalimentiDitta ne '0'}">
									<c:set var="msgAvvalimenti" value="La ditta ha fatto ricorso all'avvalimento. "/>
								</c:when>
								<c:otherwise>
									<c:set var="msgAvvalimenti" value=""/>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${controlloNoteAvvisi eq 1}">
									<c:set var="msgNote" value="Nell'anagrafica della ditta sono presenti note o avvisi in stato 'aperto'. "/>
								</c:when>
								<c:when test="${controlloNoteAvvisi eq 2}">
									<c:set var="msgNote" value="Nell'anagrafica del raggruppamento o delle ditte componenti il raggruppamento sono presenti note o avvisi in stato 'aperto'. "/>
								</c:when>
								<c:when test="${controlloNoteAvvisi eq 3}">
									<c:set var="msgNote" value="Nell'anagrafica del consorzio o delle consorziate designate come esecutrici sono presenti note o avvisi in stato 'aperto'. "/>
								</c:when>
								<c:otherwise>
									<c:set var="msgNote" value=""/>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${controlloComponenti eq 1}">
									<c:set var="msgComponenti" value="Nell'anagrafica del raggruppamento non sono state specificate le ditte componenti (indicare almeno due ditte). "/>
								</c:when>
								<c:otherwise>
									<c:set var="msgComponenti" value=""/>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${contolloMandataria eq 1}">
									<c:set var="msgMandataria" value="Nell'anagrafica del raggruppamento non è stata specificata la mandataria."/>
								</c:when>
								<c:otherwise>
									<c:set var="msgMandataria" value=""/>
								</c:otherwise>
							</c:choose>
							
							<c:if test="${msgAvvalimenti ne '' &&  msgNote ne ''}">
								<c:set var="msgNote" value="&#13;&#13;${msgNote }"/>
							</c:if>
							
							<c:if test="${(msgAvvalimenti ne '' || msgNote ne '') &&  msgComponenti ne ''}">
								<c:set var="msgComponenti" value="&#13;&#13;${msgComponenti }"/>
							</c:if>
							
							<c:if test="${(msgAvvalimenti ne '' || msgNote ne '' ||  msgComponenti ne '') &&  msgMandataria ne ''}">
								<c:set var="msgMandataria" value="&#13;&#13;${msgMandataria }"/>
							</c:if>
							
							<c:choose>
								<c:when test='${msgNote ne "" || msgComponenti ne "" || msgMandataria ne ""  || msgAvvalimenti ne "" }'>
									<img width="16" height="16" title="${msgAvvalimenti}${msgNote}${msgComponenti}${msgMandataria}" alt="${msgAvvalimenti}${msgNote}${msgComponenti}${msgMandataria}" src="${pageContext.request.contextPath}/img/noteAvvisiImpresa.png"/>
								</c:when>
								<c:otherwise>
									&nbsp;
								</c:otherwise>
							</c:choose>
						</gene:campoLista>
					</c:if>
					<c:if test="${ not empty urlWsArt80 and urlWsArt80 ne '' and updateLista ne 1 and (paginaAttivaWizard eq step1Wizard or paginaAttivaWizard eq step8Wizard)}">
						<jsp:include page="/WEB-INF/pages/gare/gare/gare-colonna-art80.jsp">
							<jsp:param name="ditta" value="${datiRiga.DITG_DITTAO }"/>
						</jsp:include>
					</c:if>
					
					
					<gene:campoLista campo="ESCLUDI_DITTA_ALTRI_LOTTI" visibile="false" value="0" campoFittizio="true" definizione="N2" edit="${updateLista eq 1}"/>

					<% // Questo campo non viene MAI modificato nella pagina e la sua valorizzazione viene gestito solo lato server. %>
					<% // E' presente nella pagina come campo hidden per capire se il valore del campo V_DITGAMMIS.AMMGAR e' ereditato %>
					<% // da una fase precedente o e' effettivamente un valore esistente per lo step del wizard in visualizzazione %>
					<gene:campoLista campo="AMMGAR" entita="DITGAMMIS" where="DITGAMMIS.CODGAR=DITG.CODGAR5 and DITGAMMIS.NGARA=DITG.NGARA5 and DITGAMMIS.DITTAO=DITG.DITTAO and DITGAMMIS.FASGAR=${varTmp}" visibile="false" edit="${updateLista eq 1}" />
					
				<c:if test='${updateLista eq 0}' >
					<gene:campoLista campo="V_DITGAMMIS_AMMGAR_FITTIZIO" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.V_DITGAMMIS_AMMGAR}" />
				</c:if>
					
					<gene:campoLista campo="ACQUISIZIONE" visibile="false" edit="${updateLista eq 1}"/>
					<gene:campoLista campo="IMPSICAZI" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPMANO" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPPERM" visibile="false" edit="${updateLista eq 1}" />
					<gene:campoLista campo="IMPCANO" visibile="false" edit="${updateLista eq 1}" />
					
					<c:if test="${(paginaAttivaWizard == step1Wizard or paginaAttivaWizard == step6Wizard or paginaAttivaWizard == step7Wizard) and updateLista ne 1}">
						<gene:campoLista campo="AMMGAR" visibile="false" />
						<gene:campoLista campo="DITG_AMMGAR" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.DITG_AMMGAR}"/>
					</c:if>
					
					<c:if test="${paginaAttivaWizard == step1Wizard and updateLista eq 1}">
						<gene:campoLista  campo="DPROFF_FIT_NASCOSTO" campoFittizio="true" definizione="T25;0;;" value="" edit="true" visibile="false"/>
						<gene:campoLista  campo="DPROFF_FIT_MODIFICATO" campoFittizio="true" definizione="T2;0;;" value="" edit="true" visibile="false"/>
					</c:if>
					
					<c:if test='${updateLista eq 0 and paginaAttivaWizard eq step1Wizard and isProceduraTelematica}' >
						<gene:campoLista campo="DITG_ESTIMP_FITTIZIO" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.DITG_ESTIMP}" />
					</c:if>
					
					<input type="hidden" id="ditteVincitrici_escluseDaAltriLotti" name="ditteVincitrici_escluseDaAltriLotti" value="" />
					<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />
					<input type="hidden" name="DIREZIONE_WIZARD" id="DIREZIONE_WIZARD" value="" />
					<input type="hidden" name="garaLottiOmogenea" id="garaLottiOmogenea" value="${garaLottiOmogenea}" />
					<input type="hidden" name="modalitaAggiudicazioneGara" id="modalitaAggiudicazioneGara" value="${modalitaAggiudicazioneGara}" />
					<input type="hidden" name="numeroDitte" id="numeroDitte" value="" />
					<input type="hidden" name="numeroDitteTotali" id="numeroDitteTotali" value="" />
					<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
					<input type="hidden" name="paginaFasiGara" id="paginaFasiGara" value="${param.paginaFasiGara}" />
					<c:if test="${paginaAttivaWizard == step8Wizard}">
						<input type="hidden" name="dittaProv" id="dittaProv" value="${requestScope.dittaProv}" />
					</c:if>
					<input type="hidden" name="bustalotti" id="bustalotti" value="${bustalotti}" />
					<input type="hidden" name="filtroValutazione" id="filtroValutazione" value="" />
					<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}" />
				</gene:formLista>
			</td>
		</tr>
		
<!-- fine pagine a lista -->
		
	</c:when>
	<c:when test='${paginaAttivaWizard eq step3Wizard and isGaraLottiConOffertaUnica ne "true"}'>
		<!-- pagina a scheda: Chiusura verifica documentazione amministrativa -->	
		<jsp:include page="./fasiGara/fasiGara-ChiusuraVerificaDocAmm.jsp" />
	</c:when>
	<c:when test='${paginaAttivaWizard eq step3Wizard and isGaraLottiConOffertaUnica eq "true"}'>
		<!-- pagina a scheda: Chiusura verifica documentazione amministrativa per le gare a lotti con offerta unica -->
		<jsp:include page="./fasiGara/fasiGara-ChiusuraVerificaDocAmm-OffertaUnica.jsp" />
	</c:when>
	<c:when test='${paginaAttivaWizard eq step5Wizard and isGaraLottiConOffertaUnica ne "true"}'>
		<!-- pagina a scheda: Conclusione comprova requisiti -->
		<jsp:include page="./fasiGara/fasiGara-ConclusioneComprovaRequisiti.jsp"/>
		<!--/jsp-:-include-->
	</c:when>
	<c:when test='${paginaAttivaWizard eq step5Wizard and isGaraLottiConOffertaUnica eq "true"}'>
		<!-- pagina a scheda: Conclusione comprova requisiti per le gare a lotti con offerta unica -->
		<jsp:include page="./fasiGara/fasiGara-ConclusioneComprovaRequisiti-OffertaUnica.jsp"/>
		<!--/jsp-:-include-->
	</c:when>
	<c:when test='${paginaAttivaWizard eq step6_5Wizard and isGaraLottiConOffertaUnica ne "true"}'>
		<!-- pagina a scheda: Chiusura valutazione tecnica -->	
		<jsp:include page="./fasiGara/fasiGara-ChiusuraValutazioneTec.jsp" />
	</c:when>
	<c:when test='${paginaAttivaWizard eq step6_5Wizard and isGaraLottiConOffertaUnica eq "true"}'>
		<!-- pagina a scheda: Chiusura valutazione tecnica per le gare a lotti con offerta unica -->
		<jsp:include page="./fasiGara/fasiGara-ChiusuraValutazioneTec-OffertaUnica.jsp" />
	</c:when>
	<c:when test='${paginaAttivaWizard eq step7_5Wizard }'>
		<!-- pagina a scheda: Asta elettronica -->
		<c:set var="modificaGaraTelematica" value='${isProceduraTelematica ne "true" or (isProceduraTelematica eq "true" and faseGara eq 6)}' scope="request"/>	
		<jsp:include page="./fasiGara/fasiGara-AstaElettronica.jsp" />
	</c:when>
	<c:when test='${paginaAttivaWizard eq step9Wizard}'>
		<!-- pagina a scheda: aggiudicazione provvisoria -->
		<jsp:include page="./fasiGara/fasiGara-AggiudicazioneProvvisoria.jsp">
		<jsp:param name="isVecchiaOepv" value="${isVecchiaOepv}"/>
		<jsp:param name="formato50" value="${formato50}"/>
		<jsp:param name="formato51" value="${formato51}"/>
		<jsp:param name="formato52" value="${formato52}"/>
		</jsp:include>
		<!-- fine pagina a scheda: aggiudicazione provvisoria -->
	</c:when>
	<c:when test='${paginaAttivaWizard eq step10Wizard}'> <!-- c'era il valore 9 -->

<!-- inizio pagina a scheda: aggiudicazione definitiva per gare a lotti con offerta unica -->

		<tr>
			<td ${stileDati} >
				&nbsp;
			</td>
		</tr>
		<tr>
			<td ${stileDati} >
				<jsp:include page="gare-pg-aggiudicazione-definitiva.jsp">
					<jsp:param name="lottoDiGara" value="${lottoDiGara}" />
				</jsp:include>
			</td>
		</tr>

<!-- fine pagina a scheda: aggiudicazione definitiva per gare a lotti con offerta unica -->

	</c:when>
</c:choose>

<c:if test='${not empty paginaAttivaWizard and paginaAttivaWizard <= step9Wizard}'>
	<%// Gestione dei diversi pulsanti presenti ai piedi della pagina  %>
	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
				<c:when test='${updateLista eq 1 and (paginaAttivaWizard >= step8Wizard or (paginaAttivaWizard < step8Wizard and bloccoAggiudicazione ne 1))}'>
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:conferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
				</c:when>
				<c:otherwise>
					<c:if test='${paginaAttivaWizard eq step8Wizard}'>
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and garaAggProvv ne "true" and
								((isGaraLottiConOffertaUnica ne "true" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.Calcolo-soglia-anomalia"))
								 or (isGaraLottiConOffertaUnica eq "true" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-AGGIUDPROVDEF.Calcolo-soglia-anomalia")))}'>
							<INPUT type="button" style="width:220px;" class="bottone-azione" value='${titoloCalcoloSogliaAnomalia} (1)' title='${titoloCalcoloSogliaAnomalia}' onclick="javascript:apriPopupSogliaAnomalia('${key}');">&nbsp;
						</c:if>
						<c:if test='${autorizzatoModifiche ne 2 and garaAggProvv ne "true" and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and gene:checkProt(pageContext, strProtModificaFasiGara)}'>
							<INPUT type="button" style="width:230px;" class="bottone-azione" value='Verifica congruit&agrave; offerta (2)' title='Verifica congruit&agrave; offerta' onclick="javascript:verificaOfferta();">&nbsp;
						</c:if>
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and garaAggProvv ne "true" and 
								((isGaraLottiConOffertaUnica ne "true" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.Aggiudicazione"))
								 or (isGaraLottiConOffertaUnica eq "true" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-AGGIUDPROVDEF.Aggiudicazione")))}'>
							<INPUT type="button" style="width:220px;" class="bottone-azione" value='Aggiudicazione (3)' title='Aggiudicazione' onclick="javascript:apriPopupAggiudicazione('${key}');">&nbsp;
						</c:if>
						<c:if test='${autorizzatoModifiche ne 2}' >
							<br><br>
						</c:if>
					</c:if>
					
					<c:if test="${paginaAttivaWizard eq step7_5Wizard and pgAsta eq 2 and numDocAttesaFirma > 0}">
						<INPUT type="button"  class="bottone-azione" value='Rileggi dati' title='Rileggi dati' onclick="javascript:historyReload();">
					</c:if>
					<c:if test='${(paginaAttivaWizard < step8Wizard and datiRiga.rowCount > 0 and bloccoAggiudicazione ne 1 and paginaAttivaWizard ne step6Wizard and paginaAttivaWizard ne step7Wizard) or paginaAttivaWizard eq step9Wizard or ((paginaAttivaWizard eq step3Wizard or paginaAttivaWizard eq step5Wizard or (paginaAttivaWizard eq step6Wizard and not esistonoDitteConPunteggio) or (paginaAttivaWizard eq step7Wizard and not esistonoDitteConPunteggio) or paginaAttivaWizard eq step6_5Wizard or paginaAttivaWizard eq step7_5Wizard) and bloccoAggiudicazione ne 1)}'>
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and gene:checkProt(pageContext, strProtModificaFasiGara)}'>
							<c:if test='${modificaGaraTelematica and pgAsta ne 3}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:modificaLista();">&nbsp;&nbsp;&nbsp;
							</c:if>
						</c:if>
					</c:if>
					
					<c:if test='${autorizzatoModifiche ne 2 and modlicg eq "6" and (paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step7Wizard) and isGaraLottiConOffertaUnica ne "true" and ((faseGara <7 and isProceduraTelematica ne "true") or (isProceduraTelematica eq "true" and ((paginaAttivaWizard eq step6Wizard and faseGara eq 5) or (paginaAttivaWizard eq step7Wizard and faseGara eq 6) ))) }'>
						<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.CalcoloPunteggi") }'>
							<c:choose>
								<c:when test="${paginaAttivaWizard eq step6Wizard }">
									<c:set var="tipoPunteggio" value="1"/>
								</c:when>
								<c:otherwise>
									<c:set var="tipoPunteggio" value="2"/>
								</c:otherwise>
							</c:choose>
							<INPUT type="button" class="bottone-azione" value='Calcolo punteggi (1)' title='Calcolo punteggi (1)' onclick="javascript:apriPopupCalcoloPunteggi('${key}',${tipoPunteggio},${param.lottoPlicoUnico eq 1},${paginaAttivaWizard });">&nbsp;
						</c:if>
						<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA.EsclusioneSoglia")}'>
							<c:set var="msgEsclusione" value="Esclusione soglia minima e riparametrazione (2)"/>
							<c:set var="tipoTitolo" value="1"/>
							<c:choose>
								<c:when test="${paginaAttivaWizard eq step6Wizard }">
									<c:set var="tipoPunteggio" value="1"/>
									<c:set var="tipoRiparam" value="${RIPTEC }"/>
									<c:if test="${RIPTEC eq 3 or empty RIPTEC }">
										<c:set var="msgEsclusione" value="Esclusione soglia minima (2)"/>
										<c:set var="tipoTitolo" value="2"/>
									</c:if>
								</c:when>
								<c:otherwise>
									<c:set var="tipoPunteggio" value="2"/>
									<c:set var="tipoRiparam" value="${RIPECO }"/>
									<c:if test="${RIPECO eq 3 or empty RIPECO }">
										<c:set var="msgEsclusione" value="Esclusione soglia minima (2)"/>
										<c:set var="tipoTitolo" value="2"/>
									</c:if>
								</c:otherwise>
							</c:choose>
							<INPUT type="button" class="bottone-azione" value='${msgEsclusione }' title='${msgEsclusione}' onclick="javascript:apriPopupEsclusioneSogliaMinima('${key}',${tipoPunteggio},${tipoTitolo },'${tipoRiparam }');">&nbsp;
						</c:if>
						<br><br>
					</c:if>
					
					<c:if test='${(param.paginaFasiGara eq "aperturaOffAggProvLottoOffUnica") or (empty param.paginaFasiGara and isGaraLottiConOffertaUnica eq "true")}'>	
						<INPUT type="button"  class="bottone-azione" value='Torna a elenco lotti' title='Torna a elenco lotti' onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;&nbsp;
					</c:if>
					
					<c:if test='${paginaAttivaWizard eq step6Wizard and isGaraLottiConOffertaUnica eq "true" and updateLista ne 1 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.FASIGARA.DettaglioOffertaPerLotto")}'>
						<INPUT type="button"  class="bottone-azione" value='Valutazione tecnica per lotto' title='Valutazione tecnica per lotto' onclick="javascript:apriDettaglioPerLotto('${key}');">&nbsp;&nbsp;&nbsp;
					</c:if>
					<c:if test='${paginaAttivaWizard eq step7Wizard and numeroLottiOEPV >0  and isGaraLottiConOffertaUnica eq "true" and updateLista ne 1 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.FASIGARA.DettaglioOffertaPerLotto")}'>
						<INPUT type="button"  class="bottone-azione" value='Valutazione economica per lotto' title='Valutazione economica per lotto' onclick="javascript:apriDettaglioPerLotto('${key}');">&nbsp;&nbsp;&nbsp;
					</c:if>
					
					<c:choose>
						<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
							<c:if test='${paginaAttivaWizard > step1Wizard && (paginaAttivaWizard ne step6Wizard  && !(paginaAttivaWizard eq step7Wizard && 
								!attivaValutazioneTec && modalitaAggiudicazioneGara ne "6") )}'>
								<INPUT type="button"  class="bottone-azione" value='< Fase precedente' title='Fase precedente' onclick="javascript:indietro();">
							</c:if>
						</c:when>
						<c:when test='${isGaraLottiConOffertaUnica eq "true"}'>
							<c:choose>
								<c:when test='${empty param.aggiudProvDefOffertaUnica and paginaAttivaWizard > step1Wizard}'>
									<INPUT type="button"  class="bottone-azione" value='< Fase precedente' title='Fase precedente' onclick="javascript:indietro();">
								</c:when>
								<c:when test='${not empty param.aggiudProvDefOffertaUnica and paginaAttivaWizard eq step9Wizard}'>
									<INPUT type="button"  class="bottone-azione" value='< Fase precedente' title='Fase precedente' onclick="javascript:indietro();">
								</c:when>
							</c:choose>

							<c:if test='${paginaAttivaWizard eq step7Wizard and faseGara <7 and autorizzatoModifiche ne 2}'>
								<br><br>
								<INPUT type="button" class="bottone-azione" value='Attiva calcolo aggiudicazione' title='Attiva calcolo aggiudicazione' onclick="javascript:confermaChiusuraAperturaFasi('ATTIVA','${bustalotti}');">
							</c:if>

						</c:when>
					</c:choose>			

					<c:choose>
						<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
							<c:if test="${(paginaAttivaWizard < step9Wizard and isProceduraTelematica ne 'true') or (paginaAttivaWizard < step9Wizard and paginaAttivaWizard != step3Wizard and paginaAttivaWizard != step5Wizard and paginaAttivaWizard != step6_5Wizard and paginaAttivaWizard != step7_5Wizard and isProceduraTelematica eq 'true') or (paginaAttivaWizard == step3Wizard and !(isProceduraTelematica eq 'true' and (faseGara eq '2' or faseGara eq '3' or faseGara eq '4'))) 
								or (paginaAttivaWizard == step5Wizard and !(isProceduraTelematica eq 'true' and (faseGara eq '2' or faseGara eq '3' or faseGara eq '4'))) or (paginaAttivaWizard == step6_5Wizard and !(isProceduraTelematica eq 'true' and (faseGara eq '2' or faseGara eq '3' or faseGara eq '4' or (faseGara eq '5' and visOffertaEco))))
								or (paginaAttivaWizard == step7_5Wizard && stepgar > 65)}"> <!-- c'rea il valore 8 -->
								<INPUT type="button"  class="bottone-azione" value='Fase seguente >' title='Fase seguente' onclick="javascript:avanti();">
							</c:if>
						</c:when>
						<c:when test='${isGaraLottiConOffertaUnica eq "true"}'>
							<c:choose>
								<c:when test="${empty param.aggiudProvDefOffertaUnica and (( paginaAttivaWizard < step7Wizard and isProceduraTelematica ne 'true') or (paginaAttivaWizard < step7Wizard and paginaAttivaWizard != step3Wizard and paginaAttivaWizard != step5Wizard and paginaAttivaWizard != step6_5Wizard and isProceduraTelematica eq 'true') or (paginaAttivaWizard == step3Wizard and !(isProceduraTelematica eq 'true' and (faseGara eq '2' or faseGara eq '3' or faseGara eq '4'))) 
								or (paginaAttivaWizard == step5Wizard and !(isProceduraTelematica eq 'true' and (faseGara eq '2' or faseGara eq '3' or faseGara eq '4'))) or paginaAttivaWizard == step6_5Wizard and !(isProceduraTelematica eq 'true' and (faseGara eq '2' or faseGara eq '3' or faseGara eq '4' or faseGara eq '5' or (faseGara >= '7' and bustalotti eq '2' and visOffertaEco ne true) )))}">
									<INPUT type="button"  class="bottone-azione" value='Fase seguente >' title='Fase seguente' onclick="javascript:avanti();">
								</c:when>
								<c:when test='${not empty param.aggiudProvDefOffertaUnica and paginaAttivaWizard < step9Wizard}'>
									<INPUT type="button"  class="bottone-azione" value='Fase seguente >' title='Fase seguente' onclick="javascript:avanti();">
								</c:when>
							</c:choose>
	
						</c:when>
					</c:choose>
					
					<c:if test="${bustalotti == '2' }">
						<c:if test='${paginaAttivaWizard eq step3Wizard and fn:contains(listaOpzioniDisponibili, "OP114#") and autorizzatoModifiche ne 2
							and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIGARA.AttivaOfferteTecniche")
							and bloccoAggiudicazione ne 1 and gene:checkProt(pageContext, strProtModificaFasiGara)}'>
							<c:if test="${isProceduraTelematica eq 'true' and (modalitaAggiudicazioneGara eq '6'  or attivaValutazioneTec)}">
								<c:if test="${faseGara eq '2' or faseGara eq '3' or faseGara eq '4'}">
									<br><br>
									<INPUT type="button"  class="bottone-azione" value='Attiva apertura offerte tecniche' title='Attiva apertura offerte tecniche' onclick="javascript:confermaChiusuraAperturaFasi('ATTIVA','${bustalotti }');">
								</c:if>
							</c:if>
						</c:if>
					</c:if>	
					
					<c:if test='${paginaAttivaWizard eq step3Wizard and fn:contains(listaOpzioniDisponibili, "OP114#") and autorizzatoModifiche ne 2 
						and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIGARA.AttivaOfferteEconomiche")
						and bloccoAggiudicazione ne 1 and gene:checkProt(pageContext, strProtModificaFasiGara)}'>
						<c:if test="${isProceduraTelematica eq 'true' and modalitaAggiudicazioneGara ne '6' and not attivaValutazioneTec and visOffertaEco}">
							<c:if test="${faseGara eq '2' or faseGara eq '3' or faseGara eq '4'}">
								<br><br>
								<INPUT type="button"  class="bottone-azione" value='Attiva apertura offerte economiche' title='Attiva apertura offerte economiche' onclick="javascript:AttivaAperturaEconomiche();">
							</c:if>
						</c:if>
					</c:if>
					
					<c:if test='${paginaAttivaWizard eq step6_5Wizard and updateLista ne 1}'>
						<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIGARA.AttivaOfferteEconomiche")}'>
							<c:if test="${isProceduraTelematica eq 'true' and visOffertaEco}">
								<c:if test="${(faseGara eq '5' and param.paginaFasiGara ne 'aperturaOffAggProvLottoOffUnica') or (faseGara eq '5' and param.paginaFasiGara eq 'aperturaOffAggProvLottoOffUnica' and (modlicg eq '6' or attivaValutazioneTec eq 'true' ))}">
									<br><br>
									<INPUT type="button"  class="bottone-azione" value='Attiva apertura offerte economiche' title='Attiva apertura offerte economiche' onclick="javascript:AttivaAperturaEconomiche();">
								</c:if>
							</c:if>
							<c:if test="${isProceduraTelematica eq 'true' and not visOffertaEco and bustalotti eq '2' and faseGara < 7}">
								<br><br>
								<INPUT type="button"  class="bottone-azione" value='Attiva calcolo aggiudicazione' title='Attiva calcolo aggiudicazione' onclick="javascript:AttivaAperturaEconomiche();">
							</c:if>
						</c:if>
					</c:if>
					
					<c:if test='${autorizzatoModifiche ne 2 and datiRiga.GARE_FASGAR eq 6 and meruolo eq "1" and paginaAttivaWizard eq step7_5Wizard and pgAsta eq 2  and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InviaInvitoAstaElettronica")}'>
						<br><br>
						<INPUT type="button"  class="bottone-azione" value="Invia invito all'asta elettronica" title="Invia invito all'asta elettronica" onclick="javascript:apriPopupInviaInvito('${gene:getValCampo(key,'NGARA')}','${codiceGara}','${integrazioneWSDM }','${idconfi}');">&nbsp;
					</c:if>
					
					<c:if test='${autorizzatoModifiche ne 2 and stepgar eq step7_5Wizard and paginaAttivaWizard eq step7_5Wizard and pgAsta eq 3  and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ConclusioneAstaElettronica")}'>
						<br><br>
						<INPUT type="button"  class="bottone-azione" value='Concludi asta elettronica' title='Concludi asta elettronica' onclick="javascript:concludiAsta('${gene:getValCampo(key,'NGARA')}');">&nbsp;&nbsp;&nbsp;
					</c:if>
									
				</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</tr>
</c:if>
</table> 
<gene:javaScript>

setPaginaAttivaWizard("${paginaAttivaWizard}");
setCurrentRow(${currentRow});
setOfftel("${offtel}");
setBloccoCongruo(${bloccoCongruo });
setAggiudicazioneProvvisoria("${aggiudicazioneProvvisoria }");
setAttivaValutazioneTec("${attivaValutazioneTec }");
setRibcal("${ribcal }");
setGaraLottoUnico(${garaLottoUnico});
setPunteggioTecnico("${punteggioTecnico }");
setIsProceduraTelematica("${isProceduraTelematica }");
setModlicg(${modlicg });
setIsGaraLottiConOffertaUnica("${isGaraLottiConOffertaUnica }");
setEsistonoDitteSenzaReqmin("${esistonoDitteSenzaReqmin }");
setBloccoPunteggiNonTuttiValorizzati("${bloccoPunteggiNonTuttiValorizzati }");
setBloccoPunteggiFuoriIntervallo("${bloccoPunteggiFuoriIntervallo }");
setSogliaMinimaCriteriImpostata("${sogliaMinimaCriteriImpostata }");
setEsistonoDitteSenzaPunteggio("${esistonoDitteSenzaPunteggio }");
setMessaggioControlloPunteggi("${messaggioControlloPunteggi }");
setPunteggioEconomico("${punteggioEconomico }");
setSogliaTecnicaMinima("${sogliaTecnicaMinima}");
setSogliaEconomicaMinima("${sogliaEconomicaMinima }");
setContextPath("${pageContext.request.contextPath}");
setUpdateLista("${updateLista }");
setFaseCalcolata("${varTmp }");
setRowCount(${datiRiga.rowCount });

setCodiceElenco("${codiceElenco }");
setMeruolo("${meruolo }");
setIsW_CONFCOMPopolata("${IsW_CONFCOMPopolata }");
setWhereBusteAttiveWizard("${whereBusteAttiveWizard }");
setLottoDiGara("${lottoDiGara }");
setBloccoAggiudicazione("${bloccoAggiudicazione }");
setFaseGara("${faseGara }");
setKey("${key }");
setBustalotti(${bustalotti });
setControlloPartgarLotti("${controlloPartgarLotti }");
setControlloInvoffLotti("${controlloInvoffLotti }");
setCodGara("${codiceGara}");
setPaginaFasiGara("${param.paginaFasiGara }");
setEsistonoAcquisizioniOfferteDaElaborareFS11A("${esistonoAcquisizioniOfferteDaElaborareFS11A }");
setEsistonoAcquisizioniOfferteDaElaborareFS11B("${esistonoAcquisizioniOfferteDaElaborareFS11B }");
setEsistonoAcquisizioniOfferteDaElaborareFS11C("${esistonoAcquisizioniOfferteDaElaborareFS11C }");
setStepgar("${stepgar }");
setVisOffertaEco("${visOffertaEco}");
setEsitoControlloPunteggiTecSopraSogia("${esitoControlloPunteggiTecSopraSogia}");
setEsitocontrolloRiparametrazioneTec("${esitocontrolloRiparametrazioneTec}");
setEsitocontrolloRiparametrazioneEco("${esitocontrolloRiparametrazioneEco}");
setElencoLottiNonRiparam("${elencoLottiNonRiparam }");
setImportoVisibile("${ImportoVisibile}");
setGaraInversa("${garaInversa }");
setCompreq("${compreq}");
setSezionitec("${sezionitec}");
setNumDitteStatoSoccorso("${numDitteStatoSoccorso}");

<c:if test='${updateLista eq 1}'>
	
	<c:if test='${paginaAttivaWizard ne step9Wizard and paginaAttivaWizard ne step3Wizard and paginaAttivaWizard ne step5Wizard and paginaAttivaWizard ne step6_5Wizard and pgAsta ne 2 and pgAsta ne 3}' > 
	
		// Funzione per associare le funzioni JS da eseguire al momento di modifica
		// di un campo. Questo e' necessario perchè la lista e' stata progetta per
		// non essere mai modificabile e quindi il tag gene:campoLista non associa
		// mai una funzione JS all'evento onchange.
		function associaFunzioniEventoOnchange(){
			for(var i=1; i <= ${currentRow}+1; i++){
	<c:choose>
		<c:when test='${paginaAttivaWizard eq step1Wizard}' >
			if(document.getElementById("V_DITGAMMIS_AMMGAR_" + i)!=null)
				document.getElementById("V_DITGAMMIS_AMMGAR_" + i).onchange = aggiornaPerCambioAmmessaGara;
		</c:when>
		<c:when test='${paginaAttivaWizard eq step4Wizard}' >
			document.getElementById("V_DITGAMMIS_AMMGAR_" + i).onchange = aggiornaPerCambioAmmessaGara;
		</c:when>
		<c:when test='${paginaAttivaWizard eq step6Wizard}' >
			document.getElementById("V_DITGAMMIS_AMMGAR_" + i).onchange = aggiornaPerCambioAmmessaGara;
			<c:if test='${modalitaAggiudicazioneGara eq 6}'>
				document.getElementById("DITG_PARTGAR_" + i).onchange = aggiornaPerCambioPartecipazioneGara;
			</c:if>
			<c:if test='${attivaValutazioneTec}'>
				document.getElementById("DITG_REQMIN_" + i).onchange = aggiornaPerCambioRequisitiMinimi;
			</c:if>
		</c:when>
		<c:when test='${paginaAttivaWizard eq step7Wizard}' >
			document.getElementById("V_DITGAMMIS_AMMGAR_" + i).onchange = aggiornaPerCambioAmmessaGara;
			document.getElementById("DITG_PARTGAR_" + i).onchange = aggiornaPerCambioPartecipazioneGara;
			<c:if test='${ribcal eq "2" and isGaraLottiConOffertaUnica ne "true"}'>
				document.getElementById("DITG_IMPOFF_" + i).onchange = aggiornaPerCambioImportoOfferto;
			</c:if>
			
			<c:choose>
				<c:when test='${modalitaAggiudicazioneGara eq 6}'>
					activeForm.setDominio("DITG_RIBAUO_" + i,"");
					<c:if test="${ isGaraLottiConOffertaUnica ne 'true'  }">
						if(document.getElementById("DITG_RIBOEPV_" + i)!=null)
							document.getElementById("DITG_RIBOEPV_" + i).onchange = aggiornaPerCambioRibassoOEPV;
						<c:if test="${riboepvVis and offtel ne '1'}">
							document.getElementById("DITG_IMPOFF_" + i).onchange = aggiornaRIBOEPVPerCambioImporto;
						</c:if>
					</c:if>
				</c:when>
				<c:otherwise>
					document.getElementById("DITG_RIBAUO_" + i).onchange = aggiornaPerCambioRibassoAumento;
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:when test='${paginaAttivaWizard eq step8Wizard}' >
			document.getElementById("DITG_CONGRUO_" + i).onchange = aggiornaPerCambioOffertaCongrua;
		</c:when>
		<c:when test='${paginaAttivaWizard eq step9Wizard}' >

		</c:when>
	</c:choose>
			}
		}
		
		// Funzioni JS richiamate subito dopo la creazione della pagina per l'
		associaFunzioniEventoOnchange();
		inizializzaLista();
		document.getElementById("numeroDitte").value = ${currentRow}+1;
		document.getElementById("numeroDitteTotali").value = ${datiRiga.rowCount};
		<c:if test='${paginaAttivaWizard eq step7Wizard and modalitaAggiudicazioneGara ne 6}'>
			if(document.getElementById("numeroDitte").value==1){
	          document.onkeypress = stopRKey;
	        }
		</c:if>
		
	</c:if>
	
<c:choose>
	<c:when test='${paginaAttivaWizard eq step6Wizard and modalitaAggiudicazioneGara eq 6}' >
		<% // Funzione per la pagina punteggio tecnico %>
		function aggiornaPerCambioPartecipazioneGara(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			if(this.value == "2"){
				
				if(document.getElementById("DITG_REQMIN_" + (i+1))!=null)
					setValue("DITG_REQMIN_" + numeroRiga, "");
				setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga, "");
				
				
				setValue("DITG_IMPOFF_" + numeroRiga, "");
				setValue("DITG_RIBAUO_" + numeroRiga, "");
				if(document.getElementById("DITG_REQMIN_" + (i+1))!=null)
					setValue("DITG_REQMIN_" + numeroRiga, "");
				setValue("DITG_IMPSICAZI_" + numeroRiga, "");
				setValue("DITG_IMPMANO_" + numeroRiga, "");
				setValue("DITG_IMPPERM_" + numeroRiga, "");
				setValue("DITG_IMPCANO_" + numeroRiga, "");
				
				
				if(document.getElementById("DITG_REQMIN_" + (i+1))!=null)
					document.getElementById("DITG_REQMIN_" + numeroRiga).disabled = true;
				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).disabled = true;
				//Si richama l'evento onchange del campo AMMGAR
				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).onchange();
			} else {
				
				
				if(document.getElementById("DITG_REQMIN_" + (i+1))!=null)
					document.getElementById("DITG_REQMIN_" + numeroRiga).disabled = false;
				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).disabled = false;
			}
		}
	</c:when>
	<c:when test='${paginaAttivaWizard eq step7Wizard and modalitaAggiudicazioneGara eq 6}' >
		<% // Funzione per la pagina punteggio economico per gare OEPV %>
		function aggiornaPerCambioPartecipazioneGara(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			if(this.value == "2"){
				
				setValue("DITG_IMPOFF_" + numeroRiga, "");
				setValue("DITG_IMPSICAZI_" + numeroRiga, "");
				setValue("DITG_IMPMANO_" + numeroRiga, "");
				setValue("DITG_IMPPERM_" + numeroRiga, "");
				setValue("DITG_IMPCANO_" + numeroRiga, "");
				setValue("DITG_RIBAUO_" + numeroRiga, "");
				setValue("DITG_RIBOEPV_" + numeroRiga, "");
				setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga, "");
				
				if(document.getElementById("DITG_REQMIN_" + (i+1))!=null)
					setValue("DITG_REQMIN_" + numeroRiga, "");
				
				document.getElementById("DITG_IMPOFF_" + numeroRiga).disabled = true;
				document.getElementById("DITG_RIBAUO_" + numeroRiga).disabled = true;
				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).disabled = true;
				//Si richama l'evento onchange del campo AMMGAR
				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).onchange();
			} else {
				document.getElementById("DITG_IMPOFF_" + numeroRiga).disabled = false;
				document.getElementById("DITG_RIBAUO_" + numeroRiga).disabled = false;
				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).disabled = false;
			}
		}
	</c:when>
	<c:when test='${paginaAttivaWizard eq step7Wizard and modalitaAggiudicazioneGara ne 6}' >
		<% // Funzione per la pagina punteggio economico per gare non OEPV %>
		function aggiornaPerCambioPartecipazioneGara(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			if(this.value == "2"){
				setValue("DITG_IMPOFF_" + numeroRiga, "");
				setValue("DITG_IMPSICAZI_" + numeroRiga, "");
				setValue("DITG_IMPMANO_" + numeroRiga, "");
				setValue("DITG_IMPPERM_" + numeroRiga, "");
				setValue("DITG_IMPCANO_" + numeroRiga, "");
				setValue("DITG_RIBAUO_" + numeroRiga, "");
				setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga, "");
				document.getElementById("DITG_IMPOFF_" + numeroRiga).disabled = true;
				document.getElementById("DITG_RIBAUO_" + numeroRiga).disabled = true;
				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).disabled = true;
				//Si richama l'evento onchange del campo AMMGAR
				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).onchange();
			} else {
				//setValue("DITG_IMPOFF_" + numeroRiga, "");
				//setValue("DITG_RIBAUO_" + numeroRiga, "");
				document.getElementById("DITG_IMPOFF_" + numeroRiga).disabled = false;
				document.getElementById("DITG_RIBAUO_" + numeroRiga).disabled = false;
				document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).disabled = false;
			}
		}
	</c:when>
</c:choose>


	<c:if test='${paginaAttivaWizard eq step6Wizard}' >
				
		function aggiornaPerCambioRequisitiMinimi(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			var reqmin = this.value;
			if(reqmin==2){
				var ammissione = getValue("V_DITGAMMIS_AMMGAR_" + numeroRiga);
				if(ammissione == "" || (ammissione != 2 && ammissione !=6)){
					var msg="Confermi l'esclusione della ditta dalla gara in quanto non conforme ai requisiti minimi?";	
					if(confirm(msg)){
						setValue("V_DITGAMMIS_AMMGAR_" + numeroRiga,"2");
						document.getElementById("V_DITGAMMIS_AMMGAR_" + numeroRiga).onchange();
						setValue("V_DITGAMMIS_MOTIVESCL_" + numeroRiga,"102");
					}
				}
			}
			var tmp = getValue("DITG_PARTGAR_" + numeroRiga);
			if((tmp == null || tmp == "" || tmp == "0") && (reqmin != null && reqmin !="")){
				setValue("DITG_INVGAR_" + numeroRiga, 1);
				setValue("DITG_INVOFF_" + numeroRiga, 1);
				setValue("DITG_PARTGAR_" + numeroRiga, 1);
				
				
			}
		}
	</c:if>

	<c:choose>
		<c:when test="${paginaAttivaWizard eq step7Wizard and modalitaAggiudicazioneGara ne 6}">
			function aggiornaPerCambioRibassoAumento(){
				var objId = this.id;
				var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			
				// All'oggetto JS CampoObj del campo DITG_RIBAUO_<i> si cambia la funzione
				// di validazione, per poter effettuare in un'unica funzione i controlli
				// necessari al ribasso offerto o al punteggio all'onchange del campo
				activeForm.getCampo("DITG_RIBAUO_" + numeroRiga).setFnValidazione(validazioneRIBAUO);
				if(callObjFn("local" + activeForm.getFormName(), 'onChange', this)){
					var tmp = null;
					tmp = getValue("DITG_PARTGAR_" + numeroRiga);
					var ribauo = this.value;
					if((tmp == null || tmp == "" || tmp == "0") && ${modalitaAggiudicazioneGara ne 6} && (ribauo != null && ribauo!="")){
						setValue("DITG_PARTGAR_" + numeroRiga, 1);
						setValue("DITG_INVGAR_" + numeroRiga, 1);
						setValue("DITG_INVOFF_" + numeroRiga, 1);
						
					}
					<c:if test="${(modalitaAggiudicazioneGara eq 5 or modalitaAggiudicazioneGara eq 14 or modalitaAggiudicazioneGara eq 16 or modalitaAggiudicazioneGara eq 17) and ribcal eq '1'}" >
						//Aggiornamento di impoff
						
						if(ribauo != null && ribauo!=""){
							ribauo = toVal(ribauo);
							var importo1 = ${importo1 };
							importo1 = parseFloat(importo1);
							var importo2 = ${importo2 }; 
							importo2 = parseFloat(importo2);
							var impoff = (importo1 * (1 + ribauo/100)) + importo2;
							setValue("DITG_IMPOFF_" + numeroRiga, round(impoff,5));
						
						}else{
							setValue("DITG_IMPOFF_" + numeroRiga, "");
						}
					</c:if>
					
				}
			}
	
			function validazioneRIBAUO(refVal, msg, obj){
				var result = false;
				if(checkFloat(refVal, msg, obj)){
					if(${modalitaAggiudicazioneGara ne 6} && refVal != null && refVal.value != "" && refVal.value > 0 && ${requestScope.offaum != "1"})  
						refVal.setValue((-1) * refVal.value);
	
			<c:choose>
				<c:when test='${not empty numeroCifreDecimaliRibasso}'>
					var tmp = null;
					if(this.getValue() != null && this.getValue() != ""){
						// Controllo del numero di cifre decimali rispetto
						// (il campo RIBAUO e' definito in C0CAMPI come F13.9)
						var numeroCifreDecimaliRibasso = ${numeroCifreDecimaliRibasso};
						tmp = this.getValue();
						if(this.getValue().indexOf(".") >= 0){
							if(tmp.substr(tmp.indexOf(".") + 1).length <= numeroCifreDecimaliRibasso){
								result = true;
							} else {
								msg.setValue("Per il ribasso è possibile indicare al piu' ${numeroCifreDecimaliRibasso} cifre decimali");
								sbiancaCampi = false;
							}
						} else {
							result = true;
						}
					}
				</c:when>
				<c:otherwise>
					result = true;
				</c:otherwise>
			</c:choose>
				}
				return result;
			}
		
		</c:when>
		<c:when test="${paginaAttivaWizard eq step7Wizard and modalitaAggiudicazioneGara eq 6  and isGaraLottiConOffertaUnica ne 'true'  }">
			function aggiornaPerCambioRibassoOEPV(){
				var objId = this.id;
				var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			
				activeForm.getCampo("DITG_RIBOEPV_" + numeroRiga).setFnValidazione(validazioneRIBOEPV);
				if(callObjFn("local" + activeForm.getFormName(), 'onChange', this)){
					var tmp = null;
					tmp = getValue("DITG_PARTGAR_" + numeroRiga);
					var riboepv = this.value;
					
					<c:if test="${ribcal eq '1'}" >
						//Aggiornamento di impoff
						
						if(riboepv != null && riboepv!=""){
							riboepv = toVal(riboepv);
							var importo1 = ${importo1 };
							importo1 = parseFloat(importo1);
							var importo2 = ${importo2 }; 
							importo2 = parseFloat(importo2);
							var impoff = (importo1 * (1 + riboepv/100)) + importo2;
							setValue("DITG_IMPOFF_" + numeroRiga, round(impoff,5));
						
						}else{
							setValue("DITG_IMPOFF_" + numeroRiga, "");
						}
					</c:if>
					
				}
			}
	
			function validazioneRIBOEPV(refVal, msg, obj){
				var result = false;
				if(checkFloat(refVal, msg, obj)){
					if(refVal != null && refVal.value != "" && refVal.value > 0 && ${requestScope.offaum != "1"})  
						refVal.setValue((-1) * refVal.value);
	
			<c:choose>
				<c:when test='${not empty numeroCifreDecimaliRibasso}'>
					var tmp = null;
					if(this.getValue() != null && this.getValue() != ""){
						// Controllo del numero di cifre decimali rispetto
						// (il campo RIBOEPV e' definito in C0CAMPI come F13.9)
						var numeroCifreDecimaliRibasso = ${numeroCifreDecimaliRibasso};
						tmp = this.getValue();
						if(this.getValue().indexOf(".") >= 0){
							if(tmp.substr(tmp.indexOf(".") + 1).length <= numeroCifreDecimaliRibasso){
								result = true;
							} else {
								msg.setValue("Per il ribasso è possibile indicare al piu' ${numeroCifreDecimaliRibasso} cifre decimali");
								sbiancaCampi = false;
							}
						} else {
							result = true;
						}
					}
				</c:when>
				<c:otherwise>
					result = true;
				</c:otherwise>
			</c:choose>
				}
				return result;
			}
		
		
		
		</c:when>
	</c:choose>
		


	<c:if test='${paginaAttivaWizard eq step8Wizard}'>
	
		function aggiornaPerCambioOffertaCongrua(){
			var objId = this.id;	
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			if(this.value == "1" || this.value == ""){
				setValue("DITG_CONGMOT_" + numeroRiga, "");
			}
		}
	</c:if>

</c:if>

<c:choose>
	<c:when test='${not empty paginaAttivaWizard and paginaAttivaWizard >= step1Wizard and paginaAttivaWizard < step9Wizard and paginaAttivaWizard ne step3Wizard and paginaAttivaWizard ne step5Wizard and paginaAttivaWizard ne step6_5Wizard 
			and pgAsta ne 2 and pgAsta ne 3}' >
		function annulla(){
			listaAnnullaModifica();
		}

		function modificaLista(){
			document.getElementById("ditteVincitrici_escluseDaAltriLotti").value = 2;
		<!--c:if test='${not empty confermaDitteVincitrici_O_EscluseDaAltriLotti}' -->
			/*if(confirm("Alcune ditte partecipanti al lotto di gara corrente risultano escluse da altri lotti della stessa gara.\nNe confermi l'esclusione anche dal lotto corrente?")){
				document.getElementById("ditteVincitrici_escluseDaAltriLotti").value = 1;
			} else {
				document.getElementById("ditteVincitrici_escluseDaAltriLotti").value = 2;
			}*/
		<!--/c:if-->
			listaApriInModifica();
		}
	
	</c:when>
	<c:when test='${not empty paginaAttivaWizard and (paginaAttivaWizard >= step9Wizard or paginaAttivaWizard eq step3Wizard or paginaAttivaWizard eq step5Wizard or paginaAttivaWizard eq step6_5Wizard or (paginaAttivaWizard eq step7_5Wizard and pgAsta eq 2))}' >
		function annulla(){
			document.forms[0].updateLista.value = "0";
			<c:if test="${paginaAttivaWizard eq step7_5Wizard and paginaAsta eq 2 }">
				document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
			</c:if>
			schedaAnnulla();
		}
		
		function modificaLista(){
			document.forms[0].updateLista.value = "1";
			<c:if test="${paginaAttivaWizard eq step7_5Wizard and paginaAsta eq 2 }">
				document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
			</c:if> 
			schedaModifica();
		}
	</c:when>
</c:choose>

	
	
	<c:if test='${paginaAttivaWizard eq step9Wizard and isGaraLottiConOffertaUnica eq "true"}'>
		function avanti(){
			document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/gare/gare-pg-aggiudProvDefLotti.jsp&key=${key}&WIZARD_PAGINA_ATTIVA=${step9Wizard}&DIREZIONE_WIZARD=AVANTI";
		}
	</c:if>

	<c:if test='${paginaAttivaWizard > step1Wizard}'>
		function indietro(){
			if(document.forms[0].action.indexOf("Scheda.do") >= 0){
				document.forms[0].action = "${pageContext.request.contextPath}/Lista.do?"+csrfToken;
				<c:if test='${paginaAttivaWizard eq step7_5Wizard and pgAsta eq 2}'>
					document.forms[0].action += "&metodo=leggi";
				</c:if>
				document.forms[0].keyParent.value = document.forms[0].key.value;
			} else {
				document.forms[0].pgSort.value = "";
				document.forms[0].pgLastSort.value = "";
				document.forms[0].pgLastValori.value = "";
			}

			setValue("DIREZIONE_WIZARD", "INDIETRO");
			listaVaiAPagina(0);
		}
	</c:if>

	<c:if test='${paginaAttivaWizard eq step10Wizard and isGaraLottiConOffertaUnica eq "true"}'>
		function indietro(){
			document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/gare/gare-pg-aggiudProvDefLotti.jsp&key=${key}&WIZARD_PAGINA_ATTIVA=${step10Wizard}&DIREZIONE_WIZARD=INDIETRO";
		}
	</c:if>


	<c:if test='${paginaAttivaWizard eq step8Wizard}'>
		
		function verificaOfferta(){
			document.forms[0].updateLista.value = "1";
			listaVaiAPagina(document.forms[0].pgCorrente.value);
		}
		
		function apriPopupSogliaAnomalia(ngara){
			var tipoTitolo="SOGLIA";
			<c:if test='${(aggiudicazioneEsclusAutom eq 2 and calcoloSogliaAnomalia eq 2) or ((isGaraDLGS2016 or isGaraDLGS2017 or isGARADL2019) and !esitoControlloDitteDLGS2016 and modalitaAggiudicazioneGara ne 6) or oepvDL_32_2019 eq "graduatoria"}' >
				tipoTitolo="GRADUATORIA";
			</c:if>
			var visOffertaEco = "${visOffertaEco }";
			var gestionePuneco = "true";
			if(visOffertaEco == "false")
				gestionePuneco = "false";
			var href= "href=gare/gare/gare-popup-aggiudicazione-provvisoria.jsp&modoRichiamo=SOGLIA&ngara=" + ngara + "&tipoTitolo=" + tipoTitolo + "&gestionePuneco=" + gestionePuneco;
			<c:if test="${oepvDL_32_2019 eq 'graduatoria' }">
				var sogliaDitteOepv = "${sogliaNumDitteOEPV }";
				href += "&sogliaDitteOepv=" + sogliaDitteOepv;
			</c:if>
			openPopUpCustom(href, "sogliaAnomalia", 700, 580, "yes", "yes");
		}

		function apriPopupAggiudicazione(ngara){
			var visOffertaEco = "${visOffertaEco }";
			var gestionePuneco = "true";
			if(visOffertaEco == "false")
				gestionePuneco = "false";
			var href="href=gare/gare/gare-popup-aggiudicazione-provvisoria.jsp&modoRichiamo=AGGIUDICAZIONE&ngara="+ngara + "&gestionePuneco=" + gestionePuneco;
			openPopUpCustom(href, "sogliaAnomalia", 700, 500, "yes","yes");
		}
		
		function apriPopupAnnullaAggiudicazione(ngara){
			var href="href=gare/gare/gare-popup-annulla-calcoloAggiudicazione.jsp&ngara="+ngara;
			<c:if test="${param.paginaFasiGara eq 'aperturaOffAggProvLottoOffUnica' }">
				href+="&bustalotti=1";
			</c:if>
			<c:if test="${param.aggiudProvDefOffertaUnica eq '1' }">
				href+="&annullamentoLottoOffUnica=1";
			</c:if>
			<c:if test="${!empty ricastae }">
				href+="&ricastae=${ricastae}";
			</c:if>
			openPopUpCustom(href, "annullaCalcoloAgg", 700, 350, "yes","yes");
		}
	</c:if>

	
	<c:if test='${paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step7Wizard}'>
	
		function apriPopupEsportaInExcel(ngara){
			var step="${paginaAttivaWizard }";
			openPopUpCustom("href=gare/gare/gare-popup-exportOEPV.jsp&modoRichiamo=ESPORTA&ngara="+ngara+ "&step=" + step, "esportaImportaExcel", 700, 500, "yes", "yes");
		}
		
		function apriPopupEsportaInExcelOffertaPrezzi(ngara){
			var ribcal="${ribcal}";
			var isGaraLottiConOffertaUnica = '${isGaraLottiConOffertaUnica}';
			var act = "${pageContext.request.contextPath}/pg/InitExportOffertaPrezziOEPV.do";
			var par = "ngara=" + ngara + "&isGaraLottiConOffertaUnica=" + isGaraLottiConOffertaUnica + "&ribcal=" + ribcal;
			
			openPopUpActionCustom(act, par, 'ExportExcelOEPV', 500, 300, "no", "no");
		}
		

		function apriPopupEsportaInExcelOffertaUnica(codgar){
			var step="${paginaAttivaWizard }";
			openPopUpCustom("href=gare/gare/gare-popup-exportOEPV-offertaUnica.jsp&modoRichiamo=ESPORTA&codgar="+codgar+ "&step=" + step, "esportaImportaExcel", 700, 500, "yes", "yes");
		}

	</c:if>
	
	<c:if test='${paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step7Wizard}'>
		function apriPopupImportaDaExcel(ngara){
			var step="${paginaAttivaWizard }";
			var isProceduraTelematica="${isProceduraTelematica }";
			var bustalotti ="${bustalotti}";
			openPopUpCustom("href=gare/gare/gare-popup-importOEPV.jsp&ngara="+ngara + "&step=" + step + "&isProceduraTelematica=" + isProceduraTelematica + "&bustalotti="+bustalotti, "esportaImportaExcel", 700, 500, "yes", "yes");
		}	
		
		function apriPopupImportaDaExcelOffertaUnica(codgar){
			var step="${paginaAttivaWizard }";
			var isProceduraTelematica="${isProceduraTelematica }";
			openPopUpCustom("href=gare/gare/gare-popup-importOEPV-offertaUnica.jsp&codgar=" + codgar + "&step=" + step + "&isProceduraTelematica=" + isProceduraTelematica, "esportaImportaExcel", 780, 500, "yes", "yes");
		}
	</c:if>		

	

	
	
	<c:if test="${(paginaAttivaWizard eq step6Wizard or paginaAttivaWizard eq step7Wizard) and (modlicg eq '5' or modlicg eq '14' or modlicg eq '16' or modlicg eq '6') and (updateLista ne 1) and gene:checkProt(pageContext, 'MASC.VIS.GARE.V_GCAP_DPRE-lista')}" >
		function DettaglioOffertaPrezzi(chiaveRiga){
			var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/v_gcap_dpre/v_gcap_dpre-lista.jsp";
			href += "&key="+chiaveRiga;
			href += "&offtel=${offtel }";
			href += "&stepWizard=${paginaAttivaWizard }";
			href += "&faseGara=${faseGara }";
			href += "&isGaraTelematica=${isProceduraTelematica }";
			href += "&modlicg=${modlicg }";
			href += "&riboepvVis=${riboepvVis }";
			document.location.href = href;
		}
	</c:if>

		function dettaglioOffertaDittaPerSingoliLotti(){
			var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/ditg/dettaglioOfferteDitta-OffertaUnicaLotti.jsp";
			href += "&key=" + chiaveRiga + "&paginaAttivaWizard=${paginaAttivaWizard}";
			var visOffertaEco = "${visOffertaEco }";
			href+="&visOffertaEco=" + visOffertaEco;
			document.location.href = href;
		}
	
	<c:if test="${(paginaAttivaWizard eq step7Wizard) and isGaraLottiConOffertaUnica ne 'true'}" >
		function aggiornaPerCambioImportoOfferto(){
			
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			activeForm.getCampo("DITG_IMPOFF_" + numeroRiga).setFnValidazione(validazioneIMPOFF);
			if(callObjFn("local" + activeForm.getFormName(), 'onChange', this)){
				var impoff = toVal(this.value);
				var cifreDecimali = 9;
				if (impoff == null || impoff == ""){
						setValue("DITG_RIBAUO_" + numeroRiga, "");
				}else{
					var numeratore = ${numeratore }; 
					numeratore = parseFloat(numeratore);
					var denominatore = ${denominatore };
					denominatore = parseFloat(denominatore);
					var ribauo; 
					if (denominatore != 0)
						ribauo = (impoff + numeratore) * 100 / denominatore ;
					else
						ribauo = 0;
					
					if (ribauo >0 && ${requestScope.offaum != "1"}){
						alert('Non sono ammesse offerte in aumento');
						setValue("DITG_RIBAUO_" + numeroRiga, "");
	
					}else {
						<c:if test='${not empty numeroCifreDecimaliRibasso}'>
							cifreDecimali =${numeroCifreDecimaliRibasso};
						</c:if>
						
						setValue("DITG_RIBAUO_" + numeroRiga, round(ribauo,cifreDecimali));
						
						//Si richama l'evento onchange del campo 
						document.getElementById("DITG_RIBAUO_" + numeroRiga).onchange();
					}
				}
			} 
		}
		
		function validazioneIMPOFF(refVal, msg, obj){
			var result = false;
			if(checkFloat(refVal, msg, obj)){
				result=true;  
			}
			return result;
		}
		
		function aggiornaRIBOEPVPerCambioImporto(){
			var objId = this.id;
			var numeroRiga = objId.substr(objId.lastIndexOf("_") + 1);
			activeForm.getCampo("DITG_IMPOFF_" + numeroRiga).setFnValidazione(validazioneIMPOFF);
			if(callObjFn("local" + activeForm.getFormName(), 'onChange', this)){
				var impoff = toVal(this.value);
				var cifreDecimali = 9;
				if (impoff == null || impoff == ""){
						setValue("DITG_RIBOEPV_" + numeroRiga, "");
				}else{
					var numeratore = ${numeratore }; 
					numeratore = parseFloat(numeratore);
					var denominatore = ${denominatore };
					denominatore = parseFloat(denominatore);
					var ribauo; 
					if (denominatore != 0)
						ribauo = (impoff + numeratore) * 100 / denominatore ;
					else
						ribauo = 0;
					
					if (ribauo >0 && ${requestScope.offaum != "1"}){
						alert('Non sono ammesse offerte in aumento');
						setValue("DITG_RIBOEPV_" + numeroRiga, "");
	
					}else {
						<c:if test='${not empty numeroCifreDecimaliRibasso}'>
							cifreDecimali =${numeroCifreDecimaliRibasso};
						</c:if>
						
						setValue("DITG_RIBOEPV_" + numeroRiga, round(ribauo,cifreDecimali));
						
					}
				}
			} 
		}
	</c:if>
	
	
	function avanza(){
		selezionaPagina(${activePage } + 1);
	}
		
	
	<c:if test='${ param.paginaFasiGara eq "aperturaOffAggProvLottoOffUnica" and !(paginaAttivaWizard eq step9Wizard or paginaAttivaWizard eq step6_5Wizard)}'>
		//Poichè nello stile dettaglio-tab-lista c'è il bordo superiore della tabella ( border-top: 4px solid #939ebd;), tolgo questo stile 
		$( ".dettaglio-tab-lista" ).css( "border-top", "1px" );
	</c:if>	

			
	function ricaricaPagina(){
		bloccaRichiesteServer();
		window.location=contextPath+'/History.do?'+csrfToken+'&metodo=reload&numeroPopUp='+getNumeroPopUp();
	}
	
	function apriPopupAggiornaDaRilanci(chiave){
		chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
		var href = "href=gare/gare/gare-popup-aggiornaDaRilanci.jsp";
		href += "&ngara=" + chiave;
		href += "&isGaraLottiConOffertaUnica=${isGaraLottiConOffertaUnica}";
		href += "&risultatiPerPagina=${requestScope.risultatiPerPagina}";
		href += "&modlicg=${modlicg}";
		openPopUpCustom(href, "aggiornaDaRilanci", 900, 600, "yes", "yes");
	}	
	
	function apriPopupProspettoRilanci(chiave){
		chiave= chiave.substr(chiave.lastIndexOf(":") + 1);
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/gare/gare-prospettoRilanciOffEco.jsp";
		href += "&ngara=" + chiave;
		href += "&isGaraLottiConOffertaUnica=${isGaraLottiConOffertaUnica}";
		href += "&modlicg=${modlicg}";
		document.location.href = href;
	}	
	
	<c:if test="${gestioneSogliaManuale eq 'true' or gestioneSogliaAutomatica eq 'true'}">
		function consultaMetodoCalcoloAnomalia(){
			dettaglioMetodoCalcoloAnomalia("${valoreNGARA }", "${isGaraLottiConOffertaUnica}", "${gestioneSogliaAutomatica }","${isGaraDLGS2017}","${requestScope.risultatiPerPagina}","${autorizzatoModifiche eq 2 or faseGara >=7 or gestioneSogliaAutomatica eq 'true'}");
		}
	</c:if>
		
	function impostaFiltroVerificaValutazione(ngara){
		var tippar;
		<c:if test="${paginaAttivaWizard eq step6Wizard}">
			tippar = "1";
		</c:if>
		<c:if test="${paginaAttivaWizard eq step7Wizard}">
			tippar = "2";
		</c:if>
		var lottoPlicoUnico = "${param.lottoPlicoUnico}";
		var codgar = "${codiceGara}";
		var comando = "href=gare/commons/popup-trova-filtroValutazioneCommissione.jsp";
		var risultatiPerPagina = document.forms[0].risultatiPerPagina.value;
		comando+="&dittePerPagina=" + risultatiPerPagina;
		comando+="&ngara=" + ngara;
		comando+="&codgar=" + codgar;
		comando+="&tippar=" + tippar;
		comando+="&lottoPlicoUnico=" + lottoPlicoUnico;
		openPopUpCustom(comando, "impostaFiltroValutazione", 900, 550, "yes", "yes");
	}

		function sorteggioDitteVerificaRequisiti(){
			var lottoDiGara ="${lottoDiGara}";
			var garaLottoUnico ="${garaLottoUnico}";
			var lottoOffertaUnica ="${lottoOffertaUnica }";
			var lotto=(garaLottoUnico=="false" && lottoDiGara== "true" && lottoOffertaUnica == "false");
			apriDettaglioSorteggioDitteVerificaRequisiti("${valoreNGARA }",lotto);
		}	
</gene:javaScript>