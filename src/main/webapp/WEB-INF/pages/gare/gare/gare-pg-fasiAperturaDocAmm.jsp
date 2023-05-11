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

<c:set var="showDgueColumn" value="false" />
<c:set var="showDgueExport" value="false" />

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>

<jsp:include page="./fasiGara/defStepWizardFasiGara.jsp" />
<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />

<c:choose>
	<c:when test='${empty param.aggiudProvDefOffertaUnica}'>
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction" parametro="${key}" />
	</c:when>
	<c:otherwise>
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneAggiudProvDefOffertaUnicaFunction" parametro="${key}" />
	</c:otherwise>
</c:choose>

<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneLogAperturaFaseDocAmmFunction",pageContext,key,paginaAttivaWizard)}' />

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
	<c:set var="sortingList" value="5;6" scope="request" />
</c:if>

<% // Set di una variabile temporanea per costruire il valore delle stringhe     %>
<% // strProtVisualizzaFasiGara e strProtModificaFasiGara in funzione dello step %>
<% // del wizard attivo. Questa variabile e' stata introdotta per non modificare %>
<% // i record presenti nella tabella W_OGGETTI (e tabelle collegate W_AZIONI e  %>
<% // W_PROAZI e di tutti di i profili esistenti) in seguito all'introduzione di %>
<% // nuovi step nel wizard fasi di gara %>

<c:set var="varTmp" value="${paginaAttivaWizard/10}" scope="request"/>
<c:if test='${fn:endsWith(varTmp, ".0")}'>
	<c:set var="varTmp" value='${fn:substringBefore(varTmp, ".0")}' />
	<c:set var="whereBusteAttiveWizard" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetBusteDocumentazioneFunction",  pageContext, varTmp,"DOCUMGARA","BUSTA")}' scope="request"/>
</c:if>

<c:set var="propertyDGUE" value='${not empty gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction",  "integrazioneMDgue.url") and not empty gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction",  "integrazioneMDgue.url.info")}' scope="request"/>
<c:if test="${! empty propertyDGUE and propertyDGUE ne 'false'}">
	<c:set var="isDGUEAbilitato" value='1' scope="request"/>
</c:if>	

<c:choose>
	<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
		<c:set var="strProtVisualizzaFasiGara" value="FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA-APERTURA_DOC_AMM.VIS-FASE${varTmp}" scope="request" />
		<c:set var="strProtModificaFasiGara" value="FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA-APERTURA_DOC_AMM.MOD-FASE${varTmp}" scope="request" />
	</c:when>
	<c:when test='${isGaraLottiConOffertaUnica eq "true" and paginaAttivaWizard < step8Wizard}'>
		<c:set var="strProtVisualizzaFasiGara" value="FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.FASIGARA-APERTURA_DOC_AMM.VIS-FASE${varTmp}" scope="request" />
		<c:set var="strProtModificaFasiGara" value="FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.FASIGARA-APERTURA_DOC_AMM.MOD-FASE${varTmp}" scope="request" />
	</c:when>
</c:choose>

<c:choose>
	<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
		<c:set var="valoreChiave" value="${gene:getValCampo(key,'NGARA') }"/>
	</c:when >
	<c:otherwise>
		<c:set var="valoreChiave" value="${gene:getValCampo(key,'CODGAR')}"/>
	</c:otherwise>
</c:choose>

<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext, idconfi)}'/>

<table class="dettaglio-tab-lista">

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
	<c:otherwise>
		<c:set var="whereDITG" value='DITG.NGARA5 = #GARE.NGARA#' scope="request" />
		<c:set var="whereGARE" value='GARE.NGARA = #GARE.NGARA#' scope="request" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
		<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction", pageContext, gene:getValCampo(key,"NGARA"))}' scope="request"/>
		<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
 	</c:when>
 	<c:otherwise>
		<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction", pageContext, gene:getValCampo(key,"CODGAR"))}' scope="request"/>
		<c:if test="${attivaValutazioneTec eq 'true' and paginaAttivaWizard eq step6Wizard && isProceduraTelematica eq 'true'}">
			<c:set var="esistonoDitteSenzaReqmin" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteRequisitiMinimiNulliFunction", pageContext,  gene:getValCampo(key,"CODGAR"),isGaraLottiConOffertaUnica)}' scope="request"/>
			<c:if test="${modlicg eq 6}">
				<c:set var="esistonoDitteSenzaPunteggio" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePunteggiTecniciNulliFunction", pageContext, gene:getValCampo(key,"CODGAR"),isGaraLottiConOffertaUnica,"false","false")}' scope="request"/>
			</c:if>
			<c:set var="messaggioControlloPunteggi" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlloPunteggioTecnicoEconomicoFunction", pageContext, gene:getValCampo(key,"CODGAR"),step6Wizard, "offerteEconomiche","false" )}' scope="request"/>
		</c:if>
 	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${isGaraLottiConOffertaUnica eq "true"}'>
		<c:set var="valoreChiaveRiservatezza" value="${gene:getValCampo(key,'CODGAR')}"/>
	</c:when >
	<c:when test='${lottoDiGara eq "true"}'>
		<c:set var="valoreChiaveRiservatezza" value="${codiceGara}"/>
	</c:when >
	<c:otherwise>
		<c:set var="valoreChiaveRiservatezza" value="${gene:getValCampo(key,'NGARA')}"/>
	</c:otherwise>
</c:choose>

<c:set var="riservatezzaAttiva" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsRiservatezzaAttivaFunction", pageContext, valoreChiaveRiservatezza, idconfi)}' scope="request"/>

<c:if test="${isProceduraTelematica eq 'true' && paginaAttivaWizard eq step1Wizard}">
	<c:set var="garaInversa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetInversaFunction", pageContext, codiceGara)}' />	
	<c:if test="${garaInversa eq '1' and isGaraLottiConOffertaUnica ne 'true'}">
		<c:set var="dittAggaDef" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDittaFunction", pageContext, gene:getValCampo(key,"NGARA"))}' />
		<c:if test="${dittAggaDef eq '' }">
			<c:set var="modGaraInversa" value='true' />
			<c:set var="bloccoAggiudicazione" value='0' scope="request"/>
			
			<c:if test="${updateLista eq 1 }">
				<c:set var="abilitazioneAperturaBusteMod" value="${fn:contains(listaOpzioniDisponibili, 'OP114#')}"/>
			</c:if>
		</c:if>
	</c:if>
	<c:if test="${garaInversa eq '1' and isGaraLottiConOffertaUnica eq 'true' and faseGara < 8}">
		<c:set var="modGaraInversa" value='true' />
		<c:set var="bloccoAggiudicazione" value='0' scope="request"/>
	</c:if>
	<c:set var="esistonoAcquisizioniOfferteDaElaborareFS11A" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, valoreChiave, "FS11A" )}' />
</c:if>

<c:if test="${paginaAttivaWizard eq step1Wizard}">
	<c:set var="descTabA1158" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDescrizioneTabellatoFunction", pageContext,"A1158" ,"1")}' />
</c:if>

<c:choose>
	<c:when test="${garaInversa ne '1' }">
		<c:set var="abilitazioneAperturaBuste" value="${isProceduraTelematica eq 'true' and fn:contains(listaOpzioniDisponibili, 'OP114#') and  (faseGara eq 2 or faseGara eq 3 or faseGara eq 4) and updateLista ne 1}"/>
	</c:when>
	<c:otherwise>
		<c:set var="abilitazioneAperturaBuste" value="${(dittAggaDef eq '' or empty dittAggaDef) and fn:contains(listaOpzioniDisponibili, 'OP114#') and faseGara >= 2 and updateLista ne 1}"/>
	</c:otherwise>
</c:choose>

<c:if test="${isProceduraTelematica eq 'true' && (paginaAttivaWizard eq step1Wizard || paginaAttivaWizard eq step76izard || paginaAttivaWizard eq step7Wizard) && updateLista ne 1}">
	<c:set var="whereControlloStastoSoccorso" value="CODGAR='${codiceGara }' and FASGAR=${varTmp} and AMMGAR='10'"/>
	<c:set var="numDitteStatoSoccorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "count(CODGAR)","V_DITGAMMIS", whereControlloStastoSoccorso)}'/>
</c:if>

<c:set var="modificaGaraTelematica" value='${isProceduraTelematica ne "true" or (isProceduraTelematica eq "true" and (faseGara eq 2 or faseGara eq 3 or faseGara eq 4) and (paginaAttivaWizard eq step1Wizard or paginaAttivaWizard eq step2Wizard or paginaAttivaWizard eq step3Wizard or paginaAttivaWizard eq step4Wizard or paginaAttivaWizard eq step5Wizard)) 
										or (isProceduraTelematica eq "true" and (faseGara eq 5) and (paginaAttivaWizard eq step6Wizard || paginaAttivaWizard eq step6_5Wizard)) or (isProceduraTelematica eq "true" and (faseGara eq 6) and (paginaAttivaWizard eq step7Wizard))
										or (isProceduraTelematica eq "true" and paginaAttivaWizard ne step1Wizard and paginaAttivaWizard ne step2Wizard and paginaAttivaWizard ne step3Wizard and paginaAttivaWizard ne step4Wizard and paginaAttivaWizard ne step5Wizard and paginaAttivaWizard ne step6Wizard and paginaAttivaWizard ne step6_5Wizard and paginaAttivaWizard ne step7Wizard)
										or modGaraInversa eq "true"}' scope="request"/>

<c:set var="urlWsArt80" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", urlWsArt80)}'/>

<c:choose>
	<c:when test='${empty paginaAttivaWizard or paginaAttivaWizard < step9Wizard and (paginaAttivaWizard ne step3Wizard and paginaAttivaWizard ne step5Wizard and paginaAttivaWizard ne step6_5Wizard)}'>

	<!-- inizia pagine a lista -->

		
		<c:if test="${!empty filtroDitte and (paginaAttivaWizard == step1Wizard  || paginaAttivaWizard == step2Wizard
				|| paginaAttivaWizard == step4Wizard ) and gene:checkProt(pageContext, strProtVisualizzaFasiGara)}">
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
		
		
		<c:choose>
			<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
				<c:set var="valoreNGARA" value='${ gene:getValCampo(key,"NGARA") }' scope="request"/>
			</c:when>
			<c:otherwise>
				<c:set var="valoreNGARA" value='${ gene:getValCampo(key,"CODGAR") }' scope="request" />
			</c:otherwise>
		</c:choose>
		
		<c:set var="condizioniGestioneBuste" value="${paginaAttivaWizard eq step1Wizard and abilitazioneAperturaBuste and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.AcquisisciDocAmm')}" />
		
		<c:if test="${paginaAttivaWizard eq step1Wizard and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.GARE-scheda.FASIGARA-APERTURA_DOC_AMM.MetodoCalcoloSoglia') and updateLista ne 1}">
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
		
		<tr>
			<td ${stileDati} >
				<gene:formLista entita="DITG" where='${whereDITG} ${filtroFaseGara}${filtroDitte }' tableclass="datilista" sortColumn="${sortingList}" pagesize="${requestScope.risultatiPerPagina}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiGara" gestisciProtezioni="true" >
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					<gene:redefineInsert name="documentiAzioni" />
					<gene:redefineInsert name="addToAzioni" >
						<c:choose>
							<c:when test='${updateLista eq 1 and paginaAttivaWizard < step8Wizard and bloccoAggiudicazione ne 1}'>
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
								
								<c:set var="condizioneModifica" value='${ autorizzatoModifiche ne 2 and paginaAttivaWizard ne step8Wizard and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and gene:checkProt(pageContext, strProtModificaFasiGara) and datiRiga.rowCount > 0 and  (paginaAttivaWizard < step8Wizard and bloccoAggiudicazione ne 1) and modificaGaraTelematica}' />
																
								<c:if test='${condizioneModifica}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:modificaLista();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1504">
												${gene:resource("label.tags.template.dettaglio.schedaModifica")}
											</a>
										</td>
									</tr>
								</c:if>
								
								<c:if test="${autorizzatoModifiche ne '2' and paginaAttivaWizard eq step1Wizard and gene:checkProt(pageContext, 'COLS.VIS.GARE.DITG.STATODGUEAMM') and isDGUEAbilitato eq '1'}">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:analisiDocumentiDGUE('');" title='Analizza DGUE' tabindex="1505">
												Analizza DGUE
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test="${paginaAttivaWizard eq step1Wizard and gene:checkProt(pageContext, 'COLS.VIS.GARE.DITG.STATODGUEAMM') and isDGUEAbilitato eq '1'}">
									<tr id="dgue_export">
										<td class="vocemenulaterale">
											<a href="javascript:exportExcelDGUE('');" title='Esporta in excel analisi DGUE' tabindex="1506">
												Esporta in excel analisi DGUE
											</a>
										</td>
									</tr>
								</c:if>
								
								<c:if test="${paginaAttivaWizard < step8Wizard and gene:checkProt(pageContext, strProtVisualizzaFasiGara)}">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:impostaFiltro();" title='Imposta filtro' tabindex="1507">
												Imposta filtro
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test="${(gestioneSogliaManuale eq 'true' or gestioneSogliaAutomatica eq 'true') and calcoloSogliaAnomaliaExDLgs2017 ne '1'}">
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:consultaMetodoCalcoloAnomalia();" title='Consulta metodo calcolo anomalia' tabindex="1508">
												Consulta metodo calcolo anomalia
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test="${compreq eq '1' and fn:startsWith(descTabA1158,'1') and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.SorteggioDitteVerificaRequisiti')}">
									<c:choose>
										<c:when test='${isGaraLottiConOffertaUnica eq "true" and garaInversa eq "1"}'>
											<c:set var="visualizzaFunzione" value='${autorizzatoModifiche ne 2 and paginaAttivaWizard eq step1Wizard and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and gene:checkProt(pageContext, strProtModificaFasiGara) and datiRiga.rowCount > 0 and  faseGara ne 8 }'  />
										</c:when>
										<c:otherwise>
											<c:set var="visualizzaFunzione" value='${condizioneModifica}'  />
										</c:otherwise>
									</c:choose>
									<c:if test="${visualizzaFunzione }">
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:sorteggioDitteVerificaRequisiti();" title='Sorteggio ditte per verifica requisiti' tabindex="1509">
													Sorteggio ditte per verifica requisiti
												</a>
											</td>
										</tr>
									</c:if>
								</c:if>
								<c:if test='${autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIGARA.AnnullaRiservatezza") and riservatezzaAttiva eq "1"}' >
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:apriPopupAnnullaRiservatezza('${valoreChiaveRiservatezza}','${idconfi}');" title='Annulla riservatezza su documentale' tabindex="1510">
												Annulla riservatezza su documentale
											</a>
										</td>
									</tr>
								</c:if>
					<c:choose>
						<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
							<c:if test='${paginaAttivaWizard > step1Wizard}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:indietro();" title='Fase precedente' tabindex="1511">
												< Fase precedente
											</a>
										</td>
									</tr>
							</c:if>
						</c:when>
						<c:when test='${isGaraLottiConOffertaUnica eq "true"}'>
	
							<c:choose>
								<c:when test='${empty param.aggiudProvDefOffertaUnica and paginaAttivaWizard > step1Wizard}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:indietro();" title='Fase precedente' tabindex="1511">
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
							<c:if test="${paginaAttivaWizard < step3Wizard }"> <!-- c'rea il valore 8 -->
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:avanti();" title='Fase seguente' tabindex="1512">
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
											<a href="javascript:avanti();" title='Fase seguente' tabindex="1512">
												Fase seguente >
											</a>
										</td>
									</tr>
								</c:when>
								<c:when test='${not empty param.aggiudProvDefOffertaUnica and paginaAttivaWizard < step9Wizard}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:avanti();" title='Fase seguente' tabindex="1512">
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
					
					<c:set var="IsW_CONFCOMPopolata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,"4")}' scope="request"/>
					<c:set var="tipoImpresa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaImpresaFunction",  pageContext,chiaveRigaJava)}' scope="request"/>
					<c:choose>
						<c:when test='${(empty updateLista or updateLista ne 1) and (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InvioComunicazione") || (
							paginaAttivaWizard == step1Wizard and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.dettaglioDitteConsorziate")) 
							|| (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RichiestaSoccorsoIstruttorio") and paginaAttivaWizard == step1Wizard))}' >
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
								<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.InvioComunicazione') ||
								 (paginaAttivaWizard == step1Wizard and (tipoImpresa == '2' || tipoImpresa == '11') and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.dettaglioDitteConsorziate')) ||
								 (paginaAttivaWizard == step1Wizard and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.RicorsoAvvalimento')) ||
								 (gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.RichiestaSoccorsoIstruttorio') and paginaAttivaWizard == step1Wizard)}">
									<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'" >
										<c:if test='${paginaAttivaWizard == step1Wizard and (tipoImpresa == "2" || tipoImpresa == "11") and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.dettaglioDitteConsorziate")}'>
											<gene:PopUpItem title="Dettaglio consorziate esecutrici" href="listaDitteConsorziate('${chiaveRigaJava}')" />
										</c:if>
										<c:if test='${paginaAttivaWizard == step1Wizard and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RicorsoAvvalimento")}'>
											<gene:PopUpItem title="Avvalimento ${numAvvalimentiDitta}" href="dettaglioAvvalimento('${chiaveRigaJava}')" />
										</c:if>
										<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InvioComunicazione") }'>
											<c:set var="inviaComunicazioneAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetAbilitazioneInvioComunicazioneFunction",  pageContext,chiaveRigaJava, abilitatoInvioMailDocumentale)}' />
											<gene:PopUpItem title="Invia comunicazione" href="inviaComunicazione('${chiaveRigaJava}','${inviaComunicazioneAbilitato }','${idconfi}')" />
										</c:if>
										<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.RichiestaSoccorsoIstruttorio') && paginaAttivaWizard == step1Wizard && autorizzatoModifiche ne 2}">
											<c:set var="statoSoccIstr" value="${datiRiga.V_DITGAMMIS_AMMGAR}"/>
											<c:choose>
												<c:when  test="${paginaAttivaWizard == step1Wizard and modGaraInversa eq 'true' and statoSoccIstr ne 10}">
													<c:set var="whereSoccorso" value="CODGAR5='${codiceGara }' and DITTAO='${datiRiga.DITG_DITTAO}' and AMMINVERSA='10'"/>
													<c:set var="numLottiSoccorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "count(CODGAR5)","DITG", whereSoccorso)}'/>
													<c:if test="${not empty numLottiSoccorso and numLottiSoccorso ne 0 }">
														<c:set var="statoSoccIstr" value="10"/>
													</c:if>
												</c:when>
												<c:when test="${paginaAttivaWizard == step1Wizard and bustalotti eq '1' and statoSoccIstr ne 10}">
													<c:set var="whereSoccorso" value="CODGAR='${codiceGara }' and DITTAO='${datiRiga.DITG_DITTAO}' and CODGAR!=NGARA and FASGAR=${varTmp} and AMMGAR='10'"/>
													<c:set var="numLottiSoccorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "count(CODGAR)","V_DITGAMMIS", whereSoccorso)}'/>
													<c:if test="${not empty numLottiSoccorso and numLottiSoccorso ne 0 }">
														<c:set var="statoSoccIstr" value="10"/>
													</c:if>
												</c:when>
											</c:choose>
											<c:if test="${statoSoccIstr eq 10 }">
												<c:set var="modelloSoccoroIstruttorioConfigurato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,"56")}' />
												<gene:PopUpItem title="Richiedi soccorso istruttorio" href="richiestaSoccorsoIstruttorio('${chiaveRigaJava}','${modelloSoccoroIstruttorioConfigurato }','${idconfi}','${numeroModello }','${varTmp }')" />
											</c:if>
										</c:if>
										<c:if test='${autorizzatoModifiche ne "2" and paginaAttivaWizard == step1Wizard and gene:checkProt(pageContext, "COLS.VIS.GARE.DITG.STATODGUEAMM") and isDGUEAbilitato eq "1"}'>
											<gene:PopUpItem title="Analizza DGUE" href="analisiDocumentiDGUE('${datiRiga.DITG_DITTAO}')" />
										</c:if>
										
									</gene:PopUp>
								</c:if>
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
												
						<gene:campoLista campo="AMMGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${varTmp}" width="80" headerClass="sortable" ordinabile ="true" visibile="${paginaAttivaWizard eq step1Wizard or (paginaAttivaWizard >= step4Wizard and paginaAttivaWizard <= step7Wizard)}"  edit="${updateLista eq 1 and (modGaraInversa ne 'true' or (modGaraInversa eq 'true' and (empty faseGara or faseGara <5)))}" />
						<gene:campoLista campo="ESTIMP" title="Sortegg.per verifica?" headerClass="sortable" visibile="${paginaAttivaWizard eq step1Wizard && compreq eq '1'}" edit="${updateLista eq 1 && fn:startsWith(descTabA1158,'0')}" />
						<gene:campoLista campo="AMMINVERSA" width="80" headerClass="sortable" ordinabile ="true" visibile="${garaInversa eq '1' and isGaraLottiConOffertaUnica ne 'true'}"  edit="${updateLista eq 1}" title="Esito verif. proc.inversa" />
						<gene:campoLista campo="PARTGAR" headerClass="sortable" visibile="false" edit="${updateLista eq 1}" />
												
						<gene:campoLista campo="INVOFF"  visibile="false" edit="${updateLista eq 1}" />
						<gene:campoLista campo="INVGAR"  visibile="false" edit="${updateLista eq 1}" />
						
						<gene:campoLista campo="FASGAR" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />
						<gene:campoLista campo="MOTIVESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />
						<gene:campoLista campo="DETMOTESCL" entita="V_DITGAMMIS" where="V_DITGAMMIS.CODGAR=DITG.CODGAR5 and V_DITGAMMIS.NGARA=DITG.NGARA5 and V_DITGAMMIS.DITTAO=DITG.DITTAO and V_DITGAMMIS.FASGAR=${paginaAttivaWizard}" visibile="false" edit="${updateLista eq 1}" />
						
						<gene:campoLista title="Ribasso offerto" width="130" campo="RIBAUO" headerClass="sortable" visibile="false" edit="${updateLista eq 1}" definizione="F13.9;0;;PRC;RIBAUO"/>
						<gene:campoLista campo="IMPOFF" title="Importo" width="${gene:if(updateLista eq 1, '150', '')}" headerClass="sortable" visibile="false" edit="${updateLista eq 1}" />
												
						<gene:campoLista campo="STAGGI" headerClass="sortable" visibile="false" />
						
						<gene:campoLista campo="CONGRUO" headerClass="sortable" visibile="false" edit="${updateLista eq 1}" />
						<gene:campoLista campo="CONGMOT" visibile="false" edit="${updateLista eq 1}" />
						<gene:campoLista campo="STAGGI_FIT" visibile="false" campoFittizio="true" edit="true" definizione="N7" value="${datiRiga.DITG_STAGGI}"/>
						<gene:campoLista campo="REQMIN" visibile="false" edit="${updateLista eq 1}" />
						<gene:campoLista campo="PUNTEC" visibile="false" edit="${updateLista eq 1}" />
						
												
						<gene:campoLista campo="PUNECO" visibile="false" edit="${updateLista eq 1}" definizione="F13.9;0;;;PUNECO"/>	
						
						<gene:campoLista campo="STATODGUEAMM" visibile="false" />
						
						<c:set var="estensioneDisabilitata" value=''/>
						<c:set var="linkAbilitato" value='true'/>
						<c:if test="${condizioniGestioneBuste}">
							<c:set var="isBustaElaborata" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.IsBustaElaborataFunction", pageContext,datiRiga.DITG_NGARA5,datiRiga.DITG_DITTAO, "FS11A")}' />
							<c:if test="${isBustaElaborata eq 'No' }">
								<c:set var="estensioneDisabilitata" value='-disabilitato'/>
								<c:set var="linkAbilitato" value='false'/>
							</c:if>
						</c:if>
						<c:if test="${abilitazioneAperturaBusteMod }">
							<c:set var="isBustaElaborata" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.IsBustaElaborataFunction", pageContext,datiRiga.DITG_NGARA5,datiRiga.DITG_DITTAO, "FS11A")}' />
						</c:if>
						
						<c:if test='${paginaAttivaWizard eq step1Wizard and updateLista ne 1 and (bustalotti eq 1 || bustalotti eq 2)}'>
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.DITG-OFFUNICA-PARTGAR") }' >
								<gene:campoLista title="&nbsp;" width="20" >
									<a href="javascript:chiaveRiga='${chiaveRigaJava}';dettaglioPartecipazioneLotti('${chiaveRigaJava}', ${bustalotti });" title="Dettaglio partecipazione ai lotti" >
										<img width="16" height="16" title="Dettaglio invio offerta lotti" alt="Dettaglioinvio offerta lotti" src="${pageContext.request.contextPath}/img/partecipazioneAiLotti.png"/>
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
						
						<c:set var="locTmp1" value="${step1Wizard}${step4Wizard}${step6Wizard}${step7Wizard}"/>
						<c:if test='${fn:contains(locTmp1, paginaAttivaWizard) and updateLista ne 1 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.VerificaDocumenti")}' >
							<gene:campoLista title="&nbsp;" width="20" >
								<c:if test="${linkAbilitato}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';verificaDocumentiRichiesti('${chiaveRigaJava}','VERIFICA','1','false','${autorizzatoModifiche }');" title="Verifica documenti richiesti" ></c:if>
									<img width="16" height="16" title="Verifica documenti richiesti" alt="Verifica documenti richiesti" src="${pageContext.request.contextPath}/img/documentazione${estensioneDisabilitata }.png"/>
								<c:if test="${linkAbilitato}"></a></c:if>
							</gene:campoLista>
						</c:if>
						
						<c:if test='${fn:contains(step1Wizard, paginaAttivaWizard) and updateLista ne 1 and gene:checkProt(pageContext, "COLS.VIS.GARE.DITG.STATODGUEAMM") and isDGUEAbilitato eq "1"}' >
							<gene:campoLista title="&nbsp;" width="20" headerClass ="dgue_column">
								<c:choose>	
								<c:when test='${(not empty datiRiga.DITG_STATODGUEAMM and datiRiga.DITG_STATODGUEAMM != "")}' >
									<c:set var="showDgueColumn" value="true"/>
									<c:set var="descStatoDGUE" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1186", datiRiga.DITG_STATODGUEAMM, "false")}'/>
									<c:if test="${datiRiga.DITG_STATODGUEAMM ne '1'}">
										<c:set var="showDgueExport" value="true" />
										<a href="javascript:chiaveRiga='${chiaveRigaJava}';esitoAnalisiDocumentiDGUE('${datiRiga.DITG_CODGAR5}','${datiRiga.DITG_NGARA5}','${datiRiga.DITG_DITTAO}','1',${datiRiga.DITG_STATODGUEAMM});" title="Stato analisi DGUE" >
									</c:if>
									<c:choose>
										<c:when test="${datiRiga.DITG_STATODGUEAMM eq 4}">
											<c:set var="mdgue_tootltip" value="presenti file xml non validi"/>
										</c:when>
										<c:when test="${datiRiga.DITG_STATODGUEAMM eq 5}">
											<c:set var="mdgue_tootltip" value="presenti DGUE con dichiarati criteri di esclusione"/>
										</c:when>
										<c:when test="${datiRiga.DITG_STATODGUEAMM eq 3}">
											<c:set var="mdgue_tootltip" value="nessun DGUE con criteri di esclusione dichiarati"/>
										</c:when>
										<c:otherwise>
											<c:set var="mdgue_tootltip" value="${descStatoDGUE}"/>
										</c:otherwise>
									</c:choose>
									<img width="16" height="16" title="Stato analisi documenti DGUE: ${mdgue_tootltip}" alt="Stato analisi documenti DGUE: ${mdgue_tootltip}" src="${pageContext.request.contextPath}/img/statoMDGUE_${datiRiga.DITG_STATODGUEAMM}.png"/>
								</c:when>
								<c:otherwise>
									<c:set var="descStatoDGUE" value='Stato analisi documenti DGUE nessuno documento da elaborare'/>
									<% // <img width="16" height="16" title="${descStatoDGUE}" alt="${descStatoDGUE}" src="${pageContext.request.contextPath}/img/statoMDGUE_dis.png"/>%>
								</c:otherwise>
								</c:choose>	
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
						<gene:campoLista campo="ALTNOT"  visibile="false"/>
							<gene:campoLista title="&nbsp;" width="20">
								<c:set var="iconaNoteAttiva" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "listaDitte.gare.note")}' scope="request"/>
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
						<c:if test="${condizioniGestioneBuste}" >
							<gene:campoLista title="&nbsp;" width="20">
								<c:choose>
									<c:when test="${isBustaElaborata eq 'No' }">
										<c:if test="${autorizzatoModifiche ne 2}"><a href="javascript:chiaveRiga='${chiaveRigaJava}';aperturaBuste('${chiaveRigaJava}', 'FS11A');" title="Busta amministrativa da acquisire" ></c:if>
											<img width="16" height="16" title="Busta amministrativa da acquisire" alt="Busta amministrativa da acquisire" src="${pageContext.request.contextPath}/img/bustaChiusa.png"/>
										<c:if test="${autorizzatoModifiche ne 2}"></a></c:if>
									</c:when>
									<c:when test="${isBustaElaborata eq 'NonEsiste' }">
										<img width="16" height="16" title="Busta amministrativa non presentata" alt="Busta amministrativa non presentata" src="${pageContext.request.contextPath}/img/bustaApertaVuota.png"/>
									</c:when>
									<c:otherwise>
										<img width="16" height="16" title="Busta amministrativa acquisita" alt="Busta amministrativa acquisita" src="${pageContext.request.contextPath}/img/bustaAperta.png"/>
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
						
						<c:if test='${updateLista eq 0 or (updateLista eq 1 and modGaraInversa eq "true" and  faseGara >=5)}' >
						<gene:campoLista campo="V_DITGAMMIS_AMMGAR_FITTIZIO" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.V_DITGAMMIS_AMMGAR}" />
						</c:if>
						
						<c:if test="${updateLista eq 0 and garaInversa eq '1' and isGaraLottiConOffertaUnica ne 'true'}" >
							<gene:campoLista campo="DITG_AMMINVERSA_FITTIZIO" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.DITG_AMMINVERSA}" />
						</c:if>
						
						<c:if test='${updateLista eq 0 and paginaAttivaWizard eq step1Wizard and isProceduraTelematica}' >
						<gene:campoLista campo="DITG_ESTIMP_FITTIZIO" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.DITG_ESTIMP}" />
						</c:if>
						
						<gene:campoLista campo="ACQUISIZIONE" visibile="false" edit="${updateLista eq 1}"/>
						<gene:campoLista campo="IMPSICAZI" visibile="false" edit="${updateLista eq 1}" />
						<gene:campoLista campo="IMPMANO" visibile="false" edit="${updateLista eq 1}" />
						<gene:campoLista campo="PERCMANO" visibile="false" edit="${updateLista eq 1}" />
						<gene:campoLista campo="IMPPERM" visibile="false" edit="${updateLista eq 1}" />
						<gene:campoLista campo="IMPCANO" visibile="false" edit="${updateLista eq 1}" />
						<gene:campoLista campo="FASGAR" visibile="false" edit="true" />
						
						
						<c:if test="${isProceduraTelematica and (paginaAttivaWizard == step1Wizard or paginaAttivaWizard == step6Wizard or paginaAttivaWizard == step7Wizard) and updateLista ne 1}">
							<gene:campoLista campo="AMMGAR" visibile="false" />
							<gene:campoLista campo="DITG_AMMGAR" visibile="false" edit="true" campoFittizio="true" definizione="N7" value="${datiRiga.DITG_AMMGAR}"/>
						</c:if>
						
						<c:if test='${paginaAttivaWizard eq step1Wizard and updateLista eq 1}' >
							<gene:campoLista  campo="DPROFF_FIT_NASCOSTO" campoFittizio="true" definizione="T25;0;;" value="" edit="true" visibile="false"/>
							<gene:campoLista  campo="DPROFF_FIT_MODIFICATO" campoFittizio="true" definizione="T2;0;;" value="" edit="true" visibile="false"/>
						</c:if>
						<c:if test='${modGaraInversa}' >
							<gene:campoLista  campo="STATO_BUSTA" campoFittizio="true" definizione="T50;0;;" value="${isBustaElaborata}" edit="true" visibile="false"/>
							<gene:campoLista campo="STAGGI" visibile="false" edit="${updateLista eq 1}" />
						</c:if>
						
						<input type="hidden" id="ditteVincitrici_escluseDaAltriLotti" name="ditteVincitrici_escluseDaAltriLotti" value="" />
						<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />
						<input type="hidden" name="DIREZIONE_WIZARD" id="DIREZIONE_WIZARD" value="" />
						<input type="hidden" name="garaLottiOmogenea" id="garaLottiOmogenea" value="${garaLottiOmogenea}" />
						<input type="hidden" name="modalitaAggiudicazioneGara" id="modalitaAggiudicazioneGara" value="${modalitaAggiudicazioneGara}" />
						<input type="hidden" name="numeroDitte" id="numeroDitte" value="" />
						<input type="hidden" name="numeroDitteTotali" id="numeroDitteTotali" value="" />
						<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
						<input type="hidden" name="modGaraInversa" id="modGaraInversa" value="${modGaraInversa}" />
						<input type="hidden" name="dittaProv" id="dittaProv" value="${dittaProv}" />
						<input type="hidden" name="dittAggaDef" id="dittAggaDef" value="${dittAggaDef}" />
						<input type="hidden" name="garaInversaAmmgarBloccato" id="garaInversaAmmgarBloccato" value='${updateLista eq 1 and modGaraInversa eq "true" and  faseGara >=5}' />	
						<input type="hidden" name="entitaPrincipaleModificabile" id="entitaPrincipaleModificabile" value="${sessionScope.entitaPrincipaleModificabile}" />
						
					</gene:formLista>
			</td>
		</tr>
		
<!-- fine pagine a lista -->
		
	</c:when>
	<c:when test='${paginaAttivaWizard eq step3Wizard and isGaraLottiConOffertaUnica ne "true"}'>
		<!-- pagina a scheda: Chiusura verifica documentazione amministrativa -->	
		<jsp:include page="./fasiGara/fasiGara-ChiusuraVerificaDocAmm.jsp">
			<jsp:param name="riservatezzaAttiva" value="${riservatezzaAttiva}" /> 
			<jsp:param name="valoreChiave" value="${valoreChiaveRiservatezza}" /> 
			<jsp:param value="${garaConcorsoProg}" name="gestioneGaraConcorsoProgAttiva"/>
		</jsp:include>
	</c:when>
	<c:when test='${paginaAttivaWizard eq step3Wizard and isGaraLottiConOffertaUnica eq "true"}'>
		<!-- pagina a scheda: Chiusura verifica documentazione amministrativa per le gare a lotti con offerta unica -->
		<jsp:include page="./fasiGara/fasiGara-ChiusuraVerificaDocAmm-OffertaUnica.jsp">
			<jsp:param name="riservatezzaAttiva" value="${riservatezzaAttiva}" /> 
			<jsp:param name="valoreChiave" value="${valoreChiaveRiservatezza}" /> 
		</jsp:include>
	</c:when>
	<c:when test='${paginaAttivaWizard eq step5Wizard and isGaraLottiConOffertaUnica ne "true"}'>
		<!-- pagina a scheda: Conclusione comprova requisiti -->
		<jsp:include page="./fasiGara/fasiGara-ConclusioneComprovaRequisiti.jsp">
			<jsp:param name="riservatezzaAttiva" value="${riservatezzaAttiva}" /> 
			<jsp:param name="valoreChiave" value="${valoreChiaveRiservatezza}" /> 
		</jsp:include>
		<!--/jsp-:-include-->
	</c:when>
	<c:when test='${paginaAttivaWizard eq step5Wizard and isGaraLottiConOffertaUnica eq "true"}'>
		<!-- pagina a scheda: Conclusione comprova requisiti per le gare a lotti con offerta unica -->
		<jsp:include page="./fasiGara/fasiGara-ConclusioneComprovaRequisiti-OffertaUnica.jsp">
			<jsp:param name="riservatezzaAttiva" value="${riservatezzaAttiva}" /> 
			<jsp:param name="valoreChiave" value="${valoreChiaveRiservatezza}" /> 
		</jsp:include>
		<!--/jsp-:-include-->
	</c:when>
	
	</c:choose>
<c:if test='${not empty paginaAttivaWizard and paginaAttivaWizard <= step9Wizard}'>
	<%// Gestione dei diversi pulsanti presenti ai piedi della pagina  %>
	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
				<c:when test='${updateLista eq 1 and (paginaAttivaWizard < step6Wizard and bloccoAggiudicazione ne 1)}'>
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:conferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
				</c:when>
				<c:otherwise>
										
					<c:if test='${(paginaAttivaWizard < step8Wizard and datiRiga.rowCount > 0 and bloccoAggiudicazione ne 1)  or ((paginaAttivaWizard eq step3Wizard or paginaAttivaWizard eq step5Wizard or paginaAttivaWizard eq step6_5Wizard) and bloccoAggiudicazione ne 1)}'>
						<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext, strProtVisualizzaFasiGara) and gene:checkProt(pageContext, strProtModificaFasiGara)}'>
							<c:if test='${modificaGaraTelematica}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:modificaLista();">&nbsp;&nbsp;&nbsp;
							</c:if>
						</c:if>
					</c:if>
					
					<c:choose>
						<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
							<c:if test='${paginaAttivaWizard > step1Wizard}'>
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
		

						</c:when>
					</c:choose>			

					<c:if test="${paginaAttivaWizard < step3Wizard}"> 
						<INPUT type="button"  class="bottone-azione" value='Fase seguente >' title='Fase seguente' onclick="javascript:avanti();">
					</c:if>
					
					
					<c:if test='${paginaAttivaWizard eq step3Wizard   and faseAperturaDocAmmChiusa eq 1 and bloccoAggiudicazione ne 1 and isProceduraTelematica ne "true" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIGARA.AttivaAperturaOfferte")}'>
						<br><br>
						<INPUT type="button"  class="bottone-azione" value='Disattiva apertura offerte' title='Disattiva apertura offerte' onclick="javascript:confermaChiusuraAperturaFasi('DISATTIVA','${bustalotti }');">
					</c:if>
					<c:if test='${paginaAttivaWizard eq step3Wizard and faseAperturaDocAmmChiusa eq 2 and bloccoAggiudicazione ne 1}'>
						<br><br>
						<c:choose>
							<c:when test="${garaConcorsoProg and autorizzatoModifiche ne 2}">
								<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.AcquisizioneTecAnonima')}">
									<INPUT type="button"  class="bottone-azione" value='${etichettaAcquisizioneAnonima }' title='${etichettaAcquisizioneAnonima }'  onclick="javascript:aperturaBusteTecnicheAnonime('${datiRiga.GARE_NGARA}','${datiRiga.GARE_CODGAR1}');">&nbsp;
				                </c:if>
								<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.ScaricaZipBusteAnonime')}">
									<INPUT type="button"  class="bottone-azione" value='${etichettaScaricaZipAnonima}' title='${etichettaScaricaZipAnonima}'  onclick="javascript:exportZipBusteAnonime('${datiRiga.GARE_NGARA}');">&nbsp;
								</c:if>
								<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.AttivaAperturaOfferte')}">
									<c:set var="etichettaPulsanteApOff" value="${etichettaInserimentoPunteggiAnonima }"  />
									<INPUT type="button"  class="bottone-azione" value='${etichettaPulsanteApOff }' title='${etichettaPulsanteApOff }'  onclick="javascript:confermaChiusuraAperturaFasi('ATTIVA','${bustalotti }');">
								</c:if>
							</c:when>
							<c:otherwise>
								<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.FASIGARA.AttivaAperturaOfferte')}">
									<c:set var="etichettaPulsanteApOff" value="Attiva apertura offerte"  />
									<INPUT type="button"  class="bottone-azione" value='${etichettaPulsanteApOff }' title='${etichettaPulsanteApOff }'  onclick="javascript:confermaChiusuraAperturaFasi('ATTIVA','${bustalotti }');">
								</c:if>
							</c:otherwise>
						</c:choose>
						
						
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
setEsistonoDitteSenzaReqmin("${setEsistonoDitteSenzaReqmin }");
setBloccoPunteggiNonTuttiValorizzati("${bloccoPunteggiNonTuttiValorizzati }");
setBloccoPunteggiFuoriIntervallo("${bloccoPunteggiFuoriIntervallo }");
setSogliaMinimaCriteriImpostata("${sogliaMinimaCriteriImpostata }");
setEsistonoDitteSenzaPunteggio("${esistonoDitteSenzaPunteggio }");
setMessaggioControlloPunteggi("${messaggioControlloPunteggi }");
setPunteggioEconomico("${punteggioEconomico }");
setSogliaEconomicaMinima("${sogliaEconomicaMinima }");
setContextPath("${pageContext.request.contextPath}");
setUpdateLista("${updateLista }");
setFaseCalcolata("${varTmp }");
setRowCount(${datiRiga.rowCount });

setCodiceElenco("${codiceElenco }");
setMeruolo("${meruolo }");
setIsW_CONFCOMPopolata("${IsW_CONFCOMPopolata }");
setLottoDiGara("${lottoDiGara }");
setBloccoAggiudicazione("${bloccoAggiudicazione }");
setFaseGara("${faseGara }");
setKey("${key }");
setCodGara("${codiceGara }");
setEsistonoAcquisizioniOfferteDaElaborareFS11A("${esistonoAcquisizioniOfferteDaElaborareFS11A }");
setGaraInversa("${garaInversa }");
setModGaraInversa("${modGaraInversa }");
setCompreq("${compreq}");
setNumDitteStatoSoccorso("${numDitteStatoSoccorso}");
setIsConcProg("${isconcprog}");
setGestioneConcProg("${garaConcorsoProg}");

<c:if test='${not empty RISULTATO and RISULTATO eq "OFFERTE_ATTIVATE"}'>
	bloccaRichiesteServer();
	document.pagineForm.action += "?"+csrfToken;
	selezionaPagina(eval(document.pagineForm.activePage.value)+1);
</c:if>


<c:if test='${updateLista eq 1}'>
	
	<c:if test='${paginaAttivaWizard ne step9Wizard and paginaAttivaWizard ne step3Wizard and paginaAttivaWizard ne step5Wizard and paginaAttivaWizard ne step6_5Wizard}' > 
				
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

</c:if>

<c:if test='${updateLista ne 1 and modGaraInversa}'>
	inizializzaVisualizzazioneListaModGaraInversa();
</c:if>

<c:choose>
	<c:when test='${not empty paginaAttivaWizard and paginaAttivaWizard >= step1Wizard and paginaAttivaWizard < step9Wizard and paginaAttivaWizard ne step3Wizard and paginaAttivaWizard ne step5Wizard and paginaAttivaWizard ne step6_5Wizard }' >
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
	<c:when test='${not empty paginaAttivaWizard and (paginaAttivaWizard >= step9Wizard or paginaAttivaWizard eq step3Wizard or paginaAttivaWizard eq step5Wizard or paginaAttivaWizard eq step6_5Wizard)}' >
		function annulla(){
			document.forms[0].updateLista.value = "0";
			schedaAnnulla();
		}
		
		function modificaLista(){
			document.forms[0].updateLista.value = "1";
			schedaModifica();
		}
	</c:when>
</c:choose>

	<c:if test='${paginaAttivaWizard > step1Wizard}'>
		function indietro(){
			if(document.forms[0].action.indexOf("Scheda.do") >= 0){
				document.forms[0].action = "${pageContext.request.contextPath}/Lista.do?"+csrfToken;
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



	function dettaglioOffertaDittaPerSingoliLotti(){
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/ditg/dettaglioOfferteDitta-OffertaUnicaLotti.jsp";
		href += "&key=" + chiaveRiga + "&paginaAttivaWizard=${paginaAttivaWizard}";
		document.location.href = href;
	}

	
	function listaDitteConsorziate(chiaveRiga){
		var bloccoPagina=false;	
		var bloccoAggiudicazione = "${bloccoAggiudicazione }";		
		var isProceduraTelematica = "${isProceduraTelematica }";
		var faseGara = "${faseGara }";
		if(faseGara!=null || faseGara!="")
			faseGara = parseInt(faseGara);
		else
			faseGara = 0;
		if(bloccoAggiudicazione=="1" || (isProceduraTelematica=="true" && faseGara>=5))
			bloccoPagina=true;			
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gene/ragdet/ragdet-lista.jsp";
		href += "&key=" + chiaveRiga+ "&bloccoPagina=" + bloccoPagina;
		document.location.href = href;
	}
		
	function ricaricaPagina(){
		bloccaRichiesteServer();
		window.location=contextPath+'/History.do?'+csrfToken+'&metodo=reload&numeroPopUp='+getNumeroPopUp();
	}
	
	<c:if test="${gestioneSogliaManuale eq 'true' or gestioneSogliaAutomatica eq 'true'}">
		
		function consultaMetodoCalcoloAnomalia(){
			dettaglioMetodoCalcoloAnomalia("${valoreNGARA }", "${isGaraLottiConOffertaUnica}", "${gestioneSogliaAutomatica }","${isGaraDLGS2017}","${requestScope.risultatiPerPagina}","${autorizzatoModifiche eq 2 or faseGara >=7 or gestioneSogliaAutomatica eq 'true'}");
		}
	
	</c:if>
		
		function sorteggioDitteVerificaRequisiti(){
			var lottoDiGara ="${lottoDiGara}";
			var garaLottoUnico ="${garaLottoUnico}";
			var lottoOffertaUnica ="${lottoOffertaUnica }";
			var lotto=(garaLottoUnico=="false" && lottoDiGara== "true" && lottoOffertaUnica == "false");
			apriDettaglioSorteggioDitteVerificaRequisiti("${valoreNGARA }", lotto);
		}
		
		function analisiDocumentiDGUE(dittao) {
			var chiave="${key}";
			var faseCall = 'Apertura doc. amministrativa'
			var ngara = chiave.substr(chiave.lastIndexOf(":") + 1);
			var codiceGara = "${codiceGara }";		
			
			href = "href=gare/gare/gare-popup-analisiDocumentiDGUE.jsp&ngara=" + ngara + "&codiceGara=" + codiceGara + "&codimp=" + dittao + "&faseCall=" + faseCall;
			openPopUpCustom(href, "analisiDocumentiDGUE", 450, 350, "yes", "yes");
		}
		
		function exportExcelDGUE(codimp) {
			var chiave="${key}";
			var faseCall = 'Apertura doc. amministrativa'
			var ngara = chiave.substr(chiave.lastIndexOf(":") + 1);
			var codgar = "${codiceGara }";	
			
			href = "href=gare/gare/gare-popup-esportaDocumentoDGUE.jsp&codgar=" + codgar +"&ngara=" + ngara + "&codimp=" + codimp + "&faseCall=" + faseCall;
			openPopUpCustom(href, "analisiDocumentiDGUE", 450, 350, "yes", "yes");
		}
		
		$(document).ready(function(){
			var showDgueColumn = "${showDgueColumn}";
			var showDgueExport = "${showDgueExport}";
			if(showDgueColumn=="false"){
				var index =$('.dgue_column').index();
				$('.dgue_column').closest('table').find('thead tr').find('th:eq('+index+')').hide();
				$('.dgue_column').closest('table').find('tbody tr').find('td:eq('+index+')').hide();
			}
			if(showDgueExport=="false"){
				$('#dgue_export').hide();
			}
		})
	
</gene:javaScript>