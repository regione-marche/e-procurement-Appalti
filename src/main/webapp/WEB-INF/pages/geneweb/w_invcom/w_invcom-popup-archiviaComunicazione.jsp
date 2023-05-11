<%
/*
 * Created on: 09-03-2018
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
		Finestra per l'archviazione di una comunicazione 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.archiviazioneEseguita and requestScope.archiviazioneEseguita eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">


<c:choose>
	<c:when test='${not empty param.idprg}'>
		<c:set var="idprg" value="${param.idprg}" />
	</c:when>
	<c:otherwise>
		<c:set var="idprg" value="${idprg}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.idcom}'>
		<c:set var="idcom" value="${param.idcom}" />
	</c:when>
	<c:otherwise>
		<c:set var="idcom" value="${idcom}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.comkey1}'>
		<c:set var="comkey1" value="${param.comkey1}" />
	</c:when>
	<c:otherwise>
		<c:set var="comkey1" value="${comkey1}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.comdatins}'>
		<c:set var="comdatins" value="${param.comdatins}" />
	</c:when>
	<c:otherwise>
		<c:set var="comdatins" value="${comdatins}" />
	</c:otherwise>
</c:choose>

<gene:setString name="titoloMaschera" value="Archivia comunicazione del ${comdatins}" />
	
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="W_INVCOM" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreArchiviaComunicazione">
	
	<table class="dettaglio-notab">
		<tr>
		 	<td>
		 		<gene:campoScheda >
	 				<br>
	 				Confermi l'archiviazione della comunicazione?
	 				<br> 
	 				<br>
	 			</gene:campoScheda>
	 		</td>
		</tr>
		
		
		
	</table>
	<input type="hidden" name="idprg" id="idprg" value="${idprg }">
	<input type="hidden" name="idcom" id="idcom" value="${idcom}">
	<input type="hidden" name="comkey1" id="comkey1" value="${comkey1}">
	<input type="hidden" name="comdatins" id="comdatins" value="${comdatins}">	
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="geneweb/w_invcom/w_invcom-popup-archiviaComunicazione.jsp";
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