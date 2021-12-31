
<%
	/*
	 * Created on 30-10-2018
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
	<c:when test='${not empty param.codgar}'>
		<c:set var="codgar" value="${param.codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="codgar" value="${codgar}" />
	</c:otherwise>
</c:choose>

<c:set var="modo" value="NUOVO" scope="request" />

<gene:template file="popup-message-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Esportazione in formato M-Appalti delle ditte della gara ${param.ngara}" />
	<gene:redefineInsert name="corpo">
			
		<c:choose>
		<c:when test="${esito eq 'ok'}">
			<br>
			<span>Esportazione delle ditte completata</span>
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
			<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.ControllaDatiExportDitteFunction" parametro='${codgar}' />
			
			<form name="reloadPopup" id="reloadPopup" action="${pageContext.request.contextPath}/pg/ExportDitteInGara.do" method="post">
				<input type="hidden" name="href" value="gare/gare/popup-export-ditte-gara.jsp" />
				<input type="hidden" name="codgar" value="${codgar}" />
				<input type="hidden" name="metodo" value="download" />
				<br>
				<c:choose>
					<c:when test="${esitocontrollo eq 'error'}">
						<span><b>Non è possibile procedere all'esportazione su file dell'elenco delle ditte in gara.</b></span><br>
						<br>
						${message}
						</br>
					</c:when>
					<c:otherwise>
						<span>Mediante questa funzione viene prodotto l'export su file dell'elenco delle ditte in gara.
						<br>Il file prodotto interoperabile adotta lo standard M-Appalti.
						<br><br>Vuoi procedere con l'operazione?</span>
						<br>
					</c:otherwise>
				</c:choose>
				<br>
			</form>
			
			<c:if test="${esitocontrollo eq 'error'}">
				<gene:redefineInsert name="buttons">
					<input type="button" class="bottone-azione"  id="cancel" value="Chiudi"	title="Chiudi" onclick="annulla();"/>&nbsp;&nbsp;
				</gene:redefineInsert>
			</c:if>
		</c:otherwise>
		</c:choose>
	</gene:redefineInsert>

	<gene:javaScript>
		
		<c:if test='${esito eq "ok"}'>
		window.onfocus=fnFocus;
		window.setTimeout("document.downloadExportForm.submit();", 250);
		</c:if>
		
		function conferma(){
			reloadPopup.submit();
		}
		function annulla(){
			window.close();
		}

	</gene:javaScript>

</gene:template>