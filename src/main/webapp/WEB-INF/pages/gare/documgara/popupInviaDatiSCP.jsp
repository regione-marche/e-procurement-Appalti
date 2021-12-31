<%
/*
 * Created on: 26-08-2010
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
		Finestra che visualizza la conferma per l'inserimento della documentazione predefinita
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.documentiInseriti and requestScope.documentiInseriti eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Invia dati a Servizio Contratti Pubblici' />
<c:set var="modo" value="NUOVO" scope="request" />
<c:choose>
	<c:when test='${not empty esito or not empty validate}' >
			
		<gene:redefineInsert name="corpo">
			<c:set var="contextPath" value="${pageContext.request.contextPath}" />
			<c:choose>
				<c:when test="${requestScope.esito eq 'ok'}">
					<br>
					Invio dei dati completato correttamente.
					<br>&nbsp;
					<br>&nbsp;
				</c:when>
				<c:otherwise>
					<c:if test="${not empty validate}">
						<br>
						<b>ATTENZIONE:</b> Si è verificato un errore nell'invio dei dati
						<br>
						<font color='red'>
						<b color="red">${validate}</b>
						</font>
						<br>&nbsp;
						<br>&nbsp;
					</c:if>
				</c:otherwise>
			</c:choose>
			<gene:redefineInsert name="buttons">
				<input type="button" class="bottone-azione"  id="cancel" value="Chiudi" title="Chiudi" onclick="chiudi();"/>&nbsp;&nbsp;
			</gene:redefineInsert>
			<gene:javaScript>
			
			function chiudi(){
				window.close();
			}
			
			window.onload = function () { 
				window.opener.bloccaRichiesteServer();
				window.opener.location = "${contextPath}/History.do?"+csrfToken+"&metodo=reload";
			}
			</gene:javaScript>
			</gene:redefineInsert>
	</c:when>
	<c:otherwise>
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="TORN" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInvioDatiSCP" gestisciProtezioni="false" >
	
	
	
		<c:if test="${empty erroriBloccanti}">
			<gene:campoScheda>
				<c:choose>
					<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "WARNING"}' >
						<br>
						<b>L'invio dei dati non è completo per i seguenti motivi:</b>
						<br>
						<ul>
						${requestScope.msg }
						</ul>
						<br>
						<b>Vuoi proseguire con l'invio dei dati a Servizio Contratti Pubblici?</b>
					</c:when>
					<c:otherwise>
						<br>
						Confermi l'invio dei dati a Servizio Contratti Pubblici?
					</c:otherwise>
				</c:choose>
				<br>
				<br>
				<br>
			</gene:campoScheda>
		</c:if>
		<c:if test="${not empty erroriBloccanti}">
			<table class="lista">
				<br>
				<b>Non è possibile procedere all'invio dei dati per i seguenti motivi:</b>
				<br>
				<ul>
				${requestScope.erroriBloccanti}
				</ul>
				<br>
				<gene:redefineInsert name="buttons">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
				</gene:redefineInsert>
				<br>
			</table>
		</c:if>
			
	

		<gene:campoScheda campo="CODGAR" campoFittizio="true" defaultValue="${param.codgar}" visibile="false" definizione="T21;0"/>
		
	</gene:formScheda>
	
	<form name="formSubmitInviaAttiSCP" id="formSubmitInviaAttiSCP" action="${pageContext.request.contextPath}/pg/InviaAttiScp.do" method="post">
		<input type="hidden" name="codgar" id="codgar" value="${param.codiceGara}" />
		<input type="hidden" name="ngara" id="ngara" value="${param.ngara}" />
		<input type="hidden" name="genere" id="genere" value="${param.genere}" />
		<input type="hidden" name="entita" id="entita" value="pubblicazioni" />
	</form>
	
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			_wait();
			document.formSubmitInviaAttiSCP.submit();
		}
		
		function annulla(){
			window.close();
		}
		function _wait() {
			document.getElementById('bloccaScreen').style.visibility='visible';
			$('#bloccaScreen').css("width",$(document).width());
			$('#bloccaScreen').css("height",$(document).height());
			document.getElementById('wait').style.visibility='visible';
			$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
		}
	

	</gene:javaScript>
	</c:otherwise>
</c:choose>
</gene:template>
</div>

	</c:otherwise>
</c:choose>