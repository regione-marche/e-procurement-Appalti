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
		Finestra per l'export di una comunicazione 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

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


<c:set var="modo" value="NUOVO" scope="request" />

<gene:template file="popup-message-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Genera report comunicazione" />
	<gene:redefineInsert name="corpo">
			
		<c:choose>
		<c:when test="${esito eq 'ok'}">
			<br>
			<span>Esportazione del report completato</span>
			<br>
			<br>
			<form action="${pageContext.request.contextPath}/DownloadTempFile.do" method="post" name="downloadExportForm" >
			<input type="hidden" name="nomeTempFile" value="${nomeFile}" />
			</form>
			<gene:redefineInsert name="buttons">
				<input type="button" class="bottone-azione"  id="cancel" value="Chiudi"	title="Chiudi" onclick="annulla();"/>&nbsp;&nbsp;
			</gene:redefineInsert>
		</c:when>
		<c:otherwise>
			<form name="reloadPopup" id="reloadPopup" action="${pageContext.request.contextPath}/pg/EsportaRiepilogoComunicazione.do" method="post">
				<input type="hidden" name="idprg" id="idprg" value="${idprg}">
				<input type="hidden" name="idcom" id="idcom" value="${idcom}">
				<br>
					<span>Confermi la generazione del report per la comunicazione selezionata?</span>
					<br>
				<br>
			</form>
		</c:otherwise>
		</c:choose>
	</gene:redefineInsert>

	<gene:javaScript>
		
		<c:if test='${esito eq "ok"}'>
		window.onfocus=fnFocus;
		window.setTimeout("document.downloadExportForm.submit();", 250);
		</c:if>
		
		function conferma(){
			bloccaRichiesteServer();
			reloadPopup.submit();
		}
		function annulla(){
			window.close();
		}

	</gene:javaScript>

</gene:template>