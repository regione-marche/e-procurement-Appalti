
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
	
	<gene:setString name="titoloMaschera" value="Conservazione documenti aggiudicatario per la gara ${param.codice}" />
	
	<gene:redefineInsert name="corpo">
	
		<form action="${contextPath}/pg/SetRichiestaConservazioneDocumentiAggiudicatari.do" method="post" name="formConservazioneDocumentiAggiudicatari" >
		 	<input type=hidden name="codgar" value="${param.codgar}" />
		 	<input type=hidden name="codice" value="${param.codice}" />
		 	<input type=hidden name="genere" value="${param.genere}" />
		 	<input type=hidden name="idconfi" value="${param.idconfi}" />
			<c:set var="hasDocAggiuArch" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNumeroDocumentiAggiudicatariArchiviati", pageContext, param.codgar)}'/>
			<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.UltimoAggiornamentoConservazioneDocAggiudicatari", pageContext, param.codgar)}'/>
			
			<table class="dettaglio-notab">
				<tr>
					<td class="valore-dato" colspan="2">
						<c:choose>
							<c:when test="${hasDocAggiuArch > 0}">
								<br>
								Per gli aggiudicatari è necessario conservare i documenti 
								per una durata diversa dai rimanenti documenti di gara.
								<br><br>	
								Mediante questa funzione si procede ad aggiornare su Titulus l'elenco dei documenti che dovranno essere conservati a norma per gli aggiudicatari.
								<br>	
								Confermi l'operazione?
								<c:if test="${!empty ultimaConservazione}">
								<br><br><br>
									<b>Ultimo aggiornamento effettuato il ${ultimaConservazione}</b>
								</c:if>
								<c:if test="${richiestaInCorso}">
								<br><br><br>
									<b>Non è possibile procedere. E' già in corso una richiesta di conservazione.</b>
								</c:if>
							</c:when>
							<c:otherwise>
							<br>
							Non è possibile procedere alla conservazione documenti aggiudicatario.
							<br>
							Non sono ancora stati trasferiti al documentale i documenti presentati delle ditte.
							<br>
							</c:otherwise>
						</c:choose>
						<br>
						<br>
					</td>
				</tr>
				<c:choose>
					<c:when test="${hasDocAggiuArch > 0}">
						<tr>
							<td colspan="2" class="comandi-dettaglio">
							<c:if test="${!richiestaInCorso}">
								<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma"	onclick="javascript:inviarichiesta();">
								<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
							</c:if>	
							<c:if test="${richiestaInCorso}">
								<INPUT type="button" class="bottone-azione" value="Chiudi"	title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
							</c:if>	
							</td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<td colspan="2" class="comandi-dettaglio">
								<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi"	onclick="javascript:annulla();">
							</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</table>
		</form>	
	</gene:redefineInsert>

	<gene:javaScript>
		
		function annulla(){
			window.close();
		}
		
		function inviarichiesta() {
			document.formConservazioneDocumentiAggiudicatari.submit();
			bloccaRichiesteServer();
		}
	
	</gene:javaScript>
</gene:template></div>

