
<%
	/*
	 * Created on 15-lug-2008
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="profiloUtente" value="${sessionScope.profiloUtente}" scope="request"/>

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	
	<c:set var="entita" value="${param.entita}" />
	<c:set var="numgara" value="${param.numgara}" />
	<c:set var="numlott" value="${param.numlott}" />
	<c:set var="numreq" value="${param.numreq}" />

	<c:choose>
		<c:when test="${entita eq 'W3GARA'}">
			<gene:setString name="titoloMaschera" value='Controllo dei dati della gara' />
		</c:when>
		<c:when test="${entita eq 'W3LOTT'}">
			<gene:setString name="titoloMaschera" value='Controllo dei dati del lotto' />
		</c:when>
		<c:when test="${entita eq 'W3GARAPUBBLICA'}">
			<gene:setString name="titoloMaschera" value='Controllo dei dati di pubblicazione' />
		</c:when>
		<c:when test="${entita eq 'W3GARAREQ'}">
			<gene:setString name="titoloMaschera" value='Controllo dei dati dei requisiti' />
		</c:when>
		<c:when test="${entita eq 'W3SMARTCIG'}">
			<gene:setString name="titoloMaschera" value='Controllo dei dati per la richiesta SMARTCIG' />
		</c:when>
	</c:choose>
	
	<gene:redefineInsert name="corpo">
		<table class="lista">
			
			<c:choose>
				<c:when test="${entita eq 'W3GARA'}">
					<c:set var="stato" value='${gene:callFunction5("it.eldasoft.sil.w3.tags.funzioni.GestioneValidazioneIDGARACIGFunction",pageContext,entita,profiloUtente.id,numgara,"0")}'/>		
				</c:when>
				<c:when test="${entita eq 'W3LOTT'}">
					<c:set var="stato" value='${gene:callFunction5("it.eldasoft.sil.w3.tags.funzioni.GestioneValidazioneIDGARACIGFunction",pageContext,entita,profiloUtente.id,numgara,numlott)}'/>		
				</c:when>
				<c:when test="${entita eq 'W3GARAPUBBLICA'}">
					<c:set var="stato" value='${gene:callFunction5("it.eldasoft.sil.w3.tags.funzioni.GestioneValidazioneIDGARACIGFunction",pageContext,entita,profiloUtente.id,numgara,"0")}'/>		
				</c:when>
				<c:when test="${entita eq 'W3GARAREQ'}">
					<c:set var="stato" value='${gene:callFunction5("it.eldasoft.sil.w3.tags.funzioni.GestioneValidazioneIDGARACIGFunction",pageContext,entita,profiloUtente.id,numgara,numreq)}'/>		
				</c:when>
				<c:when test="${entita eq 'W3SMARTCIG'}">
					<c:set var="stato" value='${gene:callFunction5("it.eldasoft.sil.w3.tags.funzioni.GestioneValidazioneIDGARACIGFunction",pageContext,entita,profiloUtente.id,numgara,"0")}'/>		
				</c:when>
			</c:choose>
			
			<gene:set name="titoloGenerico" value="${titolo}" />
			<gene:set name="listaGenericaControlli" value="${listaControlli}" />
			<gene:set name="numeroErrori" value="${numeroErrori}" />
			<gene:set name="numeroWarning" value="${numeroWarning}" />
			
			<c:choose>
			
				<c:when test="${!empty listaControlli}">
					<jsp:include page="../commons/popup-validazione-interno.jsp" />	
				</c:when>
				
				<c:otherwise>
				<tr>
					<td>
						<b>${titoloGenerico}</b>
						<br>&nbsp;<br>
						<b>Non è stato rilevato alcun problema.</b>
						<br>&nbsp;<br>				
					</td>
				</tr>
				</c:otherwise>
			</c:choose>
			
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<c:if test="${!empty listaControlli}">
						<INPUT type="button" class="bottone-azione" value="Controlla nuovamente" title="Controlla nuovamente" onclick="javascript:controlla()">
					</c:if>
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla()">&nbsp;
				</td>
			</tr>
			
		</table>

	</gene:redefineInsert>
	
<gene:javaScript>

	window.opener.currentPopUp=null;
	
    window.onfocus=resettaCurrentPopup;

	function resettaCurrentPopup() {
		window.opener.currentPopUp=null;
	}
	
	function annulla(){
		window.close();
	}

	function controlla(){
		window.location.reload();
	}

</gene:javaScript>	
	
</gene:template>

</div>
