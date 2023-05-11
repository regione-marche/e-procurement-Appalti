
<%
	/*
	 * Created on 10-02-2014
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty RISULTATO and RISULTATO eq "OK"}' >
		<script type="text/javascript">
		opener.historyReload();
		window.close();
		</script>
	</c:when>
	<c:otherwise>
	<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
	<div style="width:97%;">
	<gene:template file="popup-message-template.jsp" gestisciProtezioni="false" >

<c:choose>
	<c:when test='${not empty param.lotto}'>
		<c:set var="lotto" value="${param.lotto}" />
	</c:when>
	<c:otherwise>
		<c:set var="lotto" value="${lotto}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codgar}'>
		<c:set var="codgar" value="${param.codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="codgar" value="${codgar}" />
	</c:otherwise>
</c:choose>

<c:if test='${not empty lotto and gene:matches(lotto, regExpresValidazStringhe, true)}' />
<c:if test='${not empty codgar and gene:matches(codgar, regExpresValidazStringhe, true)}' />

<c:set var="esistonoDitteConPunteggio" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePunteggioValorizzatoFunction", pageContext, lotto, "1" )}' />

	<gene:setString name="titoloMaschera" value="Ripristina valutazione su M-Eval" />
	<c:set var="modo" value="NUOVO" scope="request" />
	<gene:redefineInsert name="corpo">
			
		<gene:formScheda entita="GFOF" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupRipristinaValutazMEval">
		<gene:campoScheda>
			<td>
			<br>
		<c:choose>
			<c:when test="${esistonoDitteConPunteggio eq 'si'}">
				<c:set var="blocco" value="true"/>
				<c:set var="msgBottone" value="Annulla"/>
				Non &egrave; possibile procedere al ripristino della valutazione delle offerte tecniche su M-Eval perch&egrave; i punteggi dei criteri di valutazione della busta tecnica sono già assegnati alle ditte.<br>
			</c:when>
			<c:when test="${RISULTATO eq 'NOK'}">
				<c:set var="blocco" value="true"/>
				<c:set var="msgBottone" value="Chiudi"/>
				Si sono presentati degli errori. Controllare il log per i dettagli<br>
			</c:when>
			<c:otherwise>
				Mediante questa funzione si ripristina la valutazione delle offerte tecniche su M-Eval per i componenti commissione abilitati.
				<br>Confermi l'operazione?<br>
			</c:otherwise>
		</c:choose>
			<br>
			</td>
		</gene:campoScheda>

		<input type="hidden" name="lotto" value="${lotto}">
		<input type="hidden" name="codgar" value="${codgar}">
				
	</gene:formScheda>
		<c:if test="${blocco eq 'true'}">
			<c:choose>
				<c:when test="${esistonoDitteConPunteggio eq 'si'}">
					<c:set var="msgBottone" value="Annulla"/>
				</c:when>
				<c:otherwise>
					<c:set var="msgBottone" value="Chiudi"/>
				</c:otherwise>
			</c:choose>
			<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="${msgBottone}" title="${msgBottone}" onclick="javascript:annulla()">&nbsp;
			</gene:redefineInsert>
		</c:if>
		
	</gene:redefineInsert>
	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/gare/gare-popup-ripristinaValutazione-MEval.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
	
	</gene:javaScript>
</gene:template>
</div>
</c:otherwise>
</c:choose>