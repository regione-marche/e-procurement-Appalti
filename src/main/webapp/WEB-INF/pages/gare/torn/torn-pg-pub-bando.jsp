<%
/*
 * Created on: 28/10/2008
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

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>
</gene:redefineInsert>

<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, gene:getValCampo(key, "CODGAR"), idconfi)}'/>
<c:if test="${integrazioneWSDM =='1'}">
	<c:set var="esisteFascicoloAssociato" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteFascicoloAssociatoFunction", pageContext, "GARE", gene:getValCampo(key,"CODGAR"),idconfi)}' scope="request"/>
</c:if>
<c:set var="tipologiaGara" value='${param.tipologiaGara}' />

<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction", pageContext, gene:getValCampo(key,"CODGAR"))}' scope="request"/>

<%/* Altri dati generali della gara */%>

	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="documentiAssociati"></gene:redefineInsert>
	<gene:redefineInsert name="noteAvvisi"></gene:redefineInsert>
	
	<gene:campoScheda campo="CODGAR" visibile="false" />
	<gene:campoScheda campo="TIPGAR" visibile="false"/>
	<gene:campoScheda campo="VALTEC" visibile="false"/>
	
	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestionePubblicazioniBandoFunction" parametro='${gene:getValCampo(key, "CODGAR")}' />
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='PUBBLI'/>
		<jsp:param name="chiave" value='${gene:getValCampo(key, "CODGAR")}'/>
		<jsp:param name="nomeAttributoLista" value='pubblicazioniBando' />
		<jsp:param name="idProtezioni" value="PUBBANDO" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/pubbli/pubblicazione-bando.jsp"/>
		<jsp:param name="arrayCampi" value="'PUBBLI_CODGAR9_', 'PUBBLI_NUMPUB_', 'PUBBLI_TIPPUB_', 'PUBBLI_TESPUB_', 'PUBBLI_NPRPUB_', 'PUBBLI_DINPUB_', 'PUBBLI_DATPUB_', 'PUBBLI_IMPPUB_', 'PUBBLI_INTPUB_', 'PUBBLI_DATFIPUB_', 'PUBBLI_TITPUB_', 'PUBBLI_NAVPUB_', 'PUBBLI_NAVNUM_', 'PUBBLI_URLPUB_'"/>		
		<jsp:param name="titoloSezione" value="Pubblicazione" />
		<jsp:param name="titoloNuovaSezione" value="Nuova pubblicazione" />
		<jsp:param name="descEntitaVociLink" value="pubblicazione" />
		<jsp:param name="msgRaggiuntoMax" value="e pubblicazioni"/>
		<jsp:param name="funzEliminazione" value="delPubblicazioniBando"/>
		<jsp:param name="usaContatoreLista" value="true" />
	</jsp:include>
	
	<c:choose>
		<c:when test="${tipologiaGara eq '1' }">
			<c:set var="condizioneModificaSezioneProfilo" value='${gene:checkProt(pageContext,"SEZ.MOD.GARE.TORN-scheda.PUBBLICITA.PUBBANDO") }'/>
		</c:when>
		<c:when test="${tipologiaGara eq '3' }">
			<c:set var="condizioneModificaSezioneProfilo" value='${gene:checkProt(pageContext,"SEZ.MOD.GARE.TORN-OFFUNICA-scheda.PUBBLICITA.PUBBANDO") }'/>
		</c:when>
		
	</c:choose>
	
	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="V_GARE_TORN"/>
			<jsp:param name="inputFiltro" value="${key}"/>
			<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
		</jsp:include>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<c:choose>
			<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
				<gene:insert name="pulsanteSalva">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
				</gene:insert>
				<gene:insert name="pulsanteAnnulla">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
				</gene:insert>
		
			</c:when>
			<c:otherwise>
				<gene:insert name="pulsanteModifica">
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and condizioneModificaSezioneProfilo}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
					</c:if>
				</gene:insert>
				<gene:insert name="pulsanteNuovo">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovo()" id="btnNuovo">
					</c:if>
				</gene:insert>
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>

	<c:if test='${!condizioneModificaSezioneProfilo}'>
		<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
	</c:if>

	<gene:redefineInsert name="addToAzioni" >
	<%/* Poichè si trattano le gare a lotti con offerte distinte ed offerta unica con profili differenti, vi è la doppia gestione della funzione da profilo*/ %>
		<c:if test='${autorizzatoModifiche ne "2" and modoAperturaScheda eq "VISUALIZZA" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.PUBBLICITA.InsertPredefiniti")}'>
			<tr>
				<td class="vocemenulaterale" >
					<c:if test='${isNavigazioneDisattiva ne "1"}'>
						<a href="javascript:apriPopupInsertPredefiniti()" title="Inserisci pubblicazioni predefinite" tabindex="1505">
					</c:if>
						Inserisci pubblicaz. predefinite
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>
	</gene:redefineInsert>

<gene:javaScript>
function apriPopupInsertPredefiniti() {
	var href = "href=gare/commons/conferma-ins-pubbli-predefinite.jsp?codgar="+getValue("TORN_CODGAR")+"&bando=1&genere=${param.tipologiaGara}";
	openPopUpCustom(href, "insPubblicazioniPredefinite", 600, 350, "no", "yes");
}


<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AutovieArchiflow")}'>
		addHrefs();
	</c:if>
</gene:javaScript>
