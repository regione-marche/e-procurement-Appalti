<%
/*
 * Created on: 14-07-2017
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Popup per la funzione di esclusione soglia minima e riparametrazione
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.calcoloEseguito and requestScope.calcoloEseguito eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:choose>
	<c:when test='${!empty param.chiave}'>
		<c:set var="chiave" value="${param.chiave}" />
	</c:when>
	<c:otherwise>
		<c:set var="chiave" value="${chiave}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.tipo}'>
		<c:set var="tipo" value="${param.tipo}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipo" value="${tipo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.tipoTitolo}'>
		<c:set var="tipoTitolo" value="${param.tipoTitolo}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoTitolo" value="${tipoTitolo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.tipoRiparam}'>
		<c:set var="tipoRiparam" value="${param.tipoRiparam}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoRiparam" value="${tipoRiparam}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${fn:contains(chiave,'GARE') }">
		<c:set var="ngara" value='${gene:getValCampo(chiave,"NGARA")}'  />
		<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, ngara)}' />
	</c:when>
	<c:when test="${fn:contains(chiave,'DITG') }">
		<c:set var="ngara" value='${gene:getValCampo(chiave,"NGARA5")}'  />
		<c:set var="codgar" value='${gene:getValCampo(chiave,"CODGAR5")}' />	
	</c:when>
</c:choose>

<c:choose>
	<c:when test="${tipo eq '1' }">
		<c:set var="esistonoDitteSenzaPunteggioTec" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePunteggiTecniciNulliFunction", pageContext, ngara,"false","true","false")}' />
	</c:when>
	<c:otherwise>
		<c:set var="esistonoDitteSenzaPunteggioEco" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePunteggiEconomiciNulliFunction", pageContext, ngara,"false","true")}' />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test="${tipo eq '1' }">
		<c:set var="msgTitolo" value ="tecnica" />
	</c:when>
	<c:otherwise>
		<c:set var="msgTitolo" value ="economica" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${tipoTitolo eq '1' }">
		<gene:setString name="titoloMaschera" value="Esclusione soglia minima e riparametrazione sui punteggi dei criteri di valutazione della busta ${msgTitolo }" />
		<c:set var="msgRiparametrazione" value=" e con la riparametrazione"/>
		<c:choose>
			<c:when test="${tipoRiparam eq '1' }">
				<c:set var="msgRiparametrazioneConferma" value="Successivamente viene eseguita la riparametrazione dei punteggi per le ditte rimaste in gara."/>
			</c:when>
			<c:when test="${tipoRiparam eq '2' }">
				<c:set var="msgRiparametrazioneConferma" value="Prima di procedere, viene eseguita la riparametrazione dei punteggi per le ditte in gara."/>
			</c:when>
		</c:choose>
		
	</c:when>
	<c:otherwise>
		<gene:setString name="titoloMaschera" value="Esclusione soglia minima sui punteggi dei criteri di valutazione della busta ${msgTitolo }" />
		<c:set var="msgRiparametrazione" value=""/>
		<c:set var="msgRiparametrazioneConferma" value=""/>
	</c:otherwise>
</c:choose>

<c:set var="modo" value="NUOVO" scope="request" />
	
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="DPUN" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupEsclusioneSogliaMinima">
	
		<gene:campoScheda>
			<td>
			<br>
		<c:choose>
			<c:when test="${esistonoDitteSenzaPunteggioTec eq 'true' or esistonoDitteSenzaPunteggioEco eq 'true'}">
				<c:set var="blocco" value="si"/>
				Per procedere con l'esclusione delle ditte in gara che hanno punteggi sotto soglia minima${msgRiparametrazione}, 
				deve essere prima attivato il calcolo dei punteggi.<br>
			</c:when>
			<c:when test="${requestScope.punteggiTuttiValorizzati eq 'no'}">
				<c:set var="blocco" value="si"/>
				Per procedere con l'esclusione delle ditte in gara che hanno punteggi sotto soglia minima${msgRiparametrazione}, 
				deve essere prima completata la compilazione del dettaglio valutazione per tutte le ditte in gara 
				e quindi attivato il calcolo dei punteggi.
				<br>Per compilare il dettaglio valutazione, attivare prima la funzione 'Annulla calcolo punteggi'.<br>
			</c:when>
			<c:otherwise>
				Confermi di procedere con l'esclusione delle ditte in gara che hanno ottenuto punteggi per i criteri di valutazione della busta ${msgTitolo} sotto soglia minima?
				<br>${msgRiparametrazioneConferma}
				<br> 
			</c:otherwise>
		</c:choose>
			<br>
			</td>
		</gene:campoScheda>

		<input type="hidden" name="chiave" value="${chiave}">
		<input type="hidden" name="tipo" value="${tipo}">
		<input type="hidden" name="codgar" value="${codgar}">
		<input type="hidden" name="tipoTitolo" value="${tipoTitolo}">
		<input type="hidden" name="tipoRiparam" value="${tipoRiparam}">
		<input type="hidden" name="ngara" value="${ngara}">
	</gene:formScheda>
		<c:if test="${blocco eq 'si' || requestScope.calcoloEseguito eq '2'}">
			<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			</gene:redefineInsert>
		</c:if>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/popupEsclusioneSogliaMinima.jsp";
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