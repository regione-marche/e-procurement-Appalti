
<%
  /*
			 * Created on 01-07-2013
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

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<div style="width: 97%;"><gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	
	<c:choose>
		<c:when test="${param.genere eq '10'}">
			<c:set var="oggetto" value=" dell'elenco operatori"/>
		</c:when>
		<c:when test="${param.genere eq '20'}">
			<c:set var="oggetto" value=" del catalogo elettronico"/>
		</c:when>
		<c:when test="${param.genere eq '11'}">
			<c:set var="oggetto" value=" dell'avviso"/>
		</c:when>
		<c:otherwise>
			<c:set var="oggetto" value=" della gara"/>
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value="Export documenti ${oggetto} ${fn:replace(param.codgar,'$','')} su file zip" />
	
	<gene:redefineInsert name="corpo">
	
		<form action="${contextPath}/pg/InviaRichiestaExportDocumenti.do" method="post" name="formInviaExportDocumenti" >
		 	<input type=hidden name="codgar" value="${param.codgar}" />
		 	<input type=hidden name="codice" value="${param.codice}" />
		 	<input type=hidden name="genere" value="${param.genere}" />
		 	<input type=hidden name="oggetto" value="${oggetto}" />
			<c:set var="temp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.UltimoExportDocumentiGaraFunction", pageContext, param.codgar)}'/>
			
			<table class="dettaglio-notab">
				<tr>
					<td class="valore-dato" colspan="2">
						<br>
						Questa funzione inoltra la richiesta al sistema per la 
						generazione di un file zip contenente tutti i documenti${oggetto}.
						Nello zip vengono inclusi tutti i file allegati alla documentazione${oggetto} 
						e ai documenti associati, i file allegati alle comunicazioni inviate dall'ente e 
						quelli allegati alla documentazione presentata dalle ditte. 
						Viene inoltre incluso un file di indice in formato csv che riporta per ogni file i dettagli di riferimento${oggetto}.
						<br><br>
						<c:if test="${!empty ultimoExport}">
							E' stata già inoltrata una richiesta di export documenti ${oggetto} da <b>${ultimoExport}</b>
						<br><br>
						</c:if>
						Confermi l'invio della richiesta?
						<br>
						<br>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="comandi-dettaglio">
						<INPUT type="button" class="bottone-azione" value="Invia richiesta" title="Invia richiesta"	onclick="javascript:inviarichiesta();">
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</form>	
	</gene:redefineInsert>

	<gene:javaScript>
		
		function annulla(){
			window.close();
		}
		
		function inviarichiesta() {
			document.formInviaExportDocumenti.submit();
			bloccaRichiesteServer();
		}
	
	</gene:javaScript>
</gene:template></div>

