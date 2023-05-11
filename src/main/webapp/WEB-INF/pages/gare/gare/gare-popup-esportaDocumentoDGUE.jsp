
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
	<c:when test='${not empty param.codgar}'>
		<c:set var="codgar" value="${param.codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="codgar" value="${codgar}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.codimp}'>
		<c:set var="codimp" value="${param.codimp}" />
	</c:when>
	<c:otherwise>
		<c:set var="codimp" value="${codimp}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.faseCall}'>
		<c:set var="faseCall" value="${param.faseCall}" />
	</c:when>
	<c:otherwise>
		<c:set var="faseCall" value="${faseCall}" />
	</c:otherwise>
</c:choose>
<c:set var="nomimo" value='' />
<c:if test="${not empty codimp}">
	<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext, ngara, codgar, codimp)}' />
</c:if>

<c:set var="modo" value="NUOVO" scope="request" />

<gene:template file="popup-message-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Esportazione analisi documenti DGUE in excel ${not empty codimp? ' della ditta':''}" />
	<gene:redefineInsert name="corpo">
			
		<c:choose>
		<c:when test="${esito eq 'ok'}">
			<br>
			<span>Esportazione completata</span>
			<br>
			<br>
			<form action="${pageContext.request.contextPath}/DownloadTempFile.do" method="post" name="downloadExportForm" >
			<input type="hidden" name="nomeTempFile" value="${nomeFile}" />
			</form>
			<gene:redefineInsert name="buttons">
				<input type="button" class="bottone-azione"  id="cancel" value="Chiudi"	title="Chiudi" onclick="annulla();"/>&nbsp;&nbsp;
			</gene:redefineInsert>
		</c:when>
		<c:when test="${esito eq 'ko'}">
			<br>
			<span>Esportazione fallita</span>
			<br>
			<br>
			<gene:redefineInsert name="buttons">
			<input type="button" class="bottone-azione"  id="cancel" value="Chiudi"	title="Chiudi" onclick="annulla();"/>&nbsp;&nbsp;
			</gene:redefineInsert>
		</c:when>
		<c:otherwise>
			<form name="reloadPopup" id="reloadPopup" action="${pageContext.request.contextPath}/pg/EseguiExportOperatoriEconomiciDGUE.do" method="post">
				<br>
				<p>Mediante questa funzione &egrave; possibile esportare in formato excel l'analisi dei documenti DGUE presentati${not empty codimp? " dalla ditta '".concat(nomimo).concat("'"):""}.</p>
				<br>
				<input type="hidden" name="codgar" id="codgar" value="${codgar}">
				<input type="hidden" name="codimp" id="codimp" value="${codimp}">
				<input type="hidden" name="faseCall" id="faseCall" value="${faseCall}">
				<p>Impostare il formato di export:</p>
				<input type="radio" id="raggruppato" name="tipologiaExcel" value="0" checked>
				<label for="raggruppato">Ditte subappaltatrici e ausiliarie su un'unica riga</label><br>
				<input type="radio" id="nonraggruppato" name="tipologiaExcel" value="1">
				<label for="nonraggruppato">Ditte subappaltatrici e ausiliarie su righe distinte</label><br>
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

