<%
/*
 * Created on: 11-09-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Popup pubblica XML legge 190/2012 su Portale Alice*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:choose>
	<c:when test='${not empty param.id}'>
		<c:set var="id" value="${param.id}" />
	</c:when>
	<c:otherwise>
		<c:set var="id" value="${id}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.anno}'>
		<c:set var="anno" value="${param.anno}" />
	</c:when>
	<c:otherwise>
		<c:set var="anno" value="${anno}" />
	</c:otherwise>
</c:choose>

<gene:setString name="titoloMaschera" value="Pubblicazione XML su Portale Appalti per l'anno di riferimento ${anno}" />

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="ANTICOR" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupPubblicaXmlPortale">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			 <c:choose>
			 	<c:when test='${requestScope.erroreOperazione eq "1" }'>
			 		Non è possibile procedere con la pubblicazione
			 	</c:when>
			 	<c:when test='${requestScope.operazioneEseguita eq "1" }'>
			 		Pubblicazione eseguita con successo
			 	</c:when>
			 	<c:otherwise>
			 		Si vuole procedere con la pubblicazione dei file xml su Portale Appalti?
			 	</c:otherwise>
			 </c:choose>
			 
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	<input type="hidden" name="id" id="id" value="${id}" />
	<input type="hidden" name="anno" id="anno" value="${anno}" />
			
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${requestScope.erroreOperazione eq "1"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
	</gene:redefineInsert>
</c:if>
<c:if test='${requestScope.operazioneEseguita eq "1"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
	</gene:redefineInsert>
</c:if>	
	
	<gene:javaScript>
		
		function conferma() {
			document.forms[0].jspPathTo.value="gare/anticor/popup-pubblicaXmlPortale.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
		function chiudi(){
			window.opener.historyReload();
			window.close();
		}
	</gene:javaScript>
</gene:template>
</div>

	