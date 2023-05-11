<%
/*
 * Created on: 31-10-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Popup esporta dati Adempimenti legge 190/2012 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.operazioneEseguita and requestScope.operazioneEseguita eq "1"}' >
<script type="text/javascript">
	opener.historyReload();
	window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:choose>
	<c:when test='${not empty param.idAnticorLotti}'>
		<c:set var="idAnticorLotti" value="${param.idAnticorLotti}" />
	</c:when>
	<c:otherwise>
		<c:set var="idAnticorLotti" value="${idAnticorLotti}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.idAnticor}'>
		<c:set var="idAnticor" value="${param.idAnticor}" />
	</c:when>
	<c:otherwise>
		<c:set var="idAnticor" value="${idAnticor}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.idLotto}'>
		<c:set var="idLotto" value="${param.idLotto}" />
	</c:when>
	<c:otherwise>
		<c:set var="idLotto" value="${idLotto}" />
	</c:otherwise>
</c:choose>

<gene:setString name="titoloMaschera" value="Ricarica lotto da dati correnti"/>
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="ANTICORLOTTI" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupRicaricaDatiLottoAdempimento">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			 <c:choose>
			 	<c:when test='${requestScope.erroreOperazione eq "1" }'>
			 		Non è possibile procedere con il ricaricamento dei dati del lotto
			 	</c:when>
			 	<c:otherwise>
			 		Confermi il ricaricamento dei dati del lotto a partire dai dati correnti?<br><br>
			 		Se si procede, tutti i dati del lotto verranno ricaricati e le eventuali modifiche 
			 		precedentemente apportate verranno perse.<br>
			 	</c:otherwise>
			 </c:choose>
			 
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	<input type="hidden" name="idAnticorLotti" id="idAnticorLotti" value="${idAnticorLotti}" />
	<input type="hidden" name="idAnticor" id="idAnticor" value="${idAnticor}" />
	<input type="hidden" name="cig" id="cig" value="${cig}" />
	<input type="hidden" name="idLotto" id="idLotto" value="${idLotto}" />
	
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${requestScope.erroreOperazione eq "1"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
	</gene:redefineInsert>
</c:if>

	<gene:javaScript>
		
		function conferma() {
			document.forms[0].jspPathTo.value="gare/anticorlotti/popup-ricaricaDati.jsp";
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
	