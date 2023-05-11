<%
  /*
			 * Created on 12-apr-2013
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

<div style="width: 97%;"><gene:template file="popup-template.jsp">
	<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
	<c:set var="idOrdine" value="${param.idOrdine}" />
	<c:set var="statoOrdine" value="${param.statoOrdine}" />

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:setString name="titoloMaschera" value='Aggiorna stato Ordine' />
	 

	<gene:redefineInsert name="corpo">
	
		<form action="${contextPath}/pg/SetNsoStatoOrdine.do" method="post" name="formAggiornaStatoOrdine" >
			<input type="hidden" name="idOrdine" value="${idOrdine}" />
			<input type="hidden" name="statoOrdine" value="${statoOrdine}" />
			<c:choose>
			<c:when test="${statoOrdine eq 2}">
				<c:set var="msgAggiornamento" value="Questa funzione permette di aggiornare lo stato dell'ordine a Completato.<br>L'ordine in stato Completato risultera' pronto per l'invio a NSO." />
			</c:when>
			<c:otherwise>
				<c:set var="msgAggiornamento" value="" />
			</c:otherwise>
			</c:choose>
			
			
			<table class="dettaglio-notab">
			
			
					<c:choose>
					<c:when test='${not empty RISULTATO and RISULTATO eq "OK"}' >
					<tr>
						<br>&nbsp;
						E' stata effettuato l'aggiornamento dello stato con successo.
						<br>&nbsp;
					</tr>
					</c:when>
					<c:otherwise>
					<tr>
						<br>
							<b>${msgAggiornamento}</b>
						<br>&nbsp;
					</tr>
					</c:otherwise>
				</c:choose>
			
			
			
			
					<tr>
						<td colspan="2" class="comandi-dettaglio">
						<c:if test='${empty RISULTATO}' >
							<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:aggiornaStatoOrdine();">
						</c:if>
							<INPUT type="button" class="bottone-azione" value="Chiudi"	title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					</tr>
			</table>
		</form>	
	</gene:redefineInsert>

	<gene:javaScript>
	
		
		function annulla(){
			window.opener.historyReload();
			window.close();
		}
		
		function aggiornaStatoOrdine() {
			document.formAggiornaStatoOrdine.submit();
			bloccaRichiesteServer();
		}
		
		

	</gene:javaScript>
</gene:template></div>


