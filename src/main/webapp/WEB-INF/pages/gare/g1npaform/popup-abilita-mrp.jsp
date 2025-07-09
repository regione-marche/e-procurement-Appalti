<%
	/*
	 *
	 * Copyright (c) Maggioli S.p.A.
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

<gene:template file="popup-message-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="corpo">
		<c:choose>
			<c:when test="${param.inviabile ne '1'}">
				<gene:setString name="titoloMaschera" value='Passa gestione scheda in M-RP' />
			</c:when>
			<c:otherwise>
				<gene:setString name="titoloMaschera" value='Riprendi gestione scheda da M-RP' />
			</c:otherwise>
		</c:choose>

		<form>
			<input type="hidden" name="codiceGara" value="${param.codiceGara}"/>
			<input type="hidden" name="idform" value="${param.idform}"/>
			<input type="hidden" name="inviabile" value="${param.inviabile}"/>
			<input type="hidden" name="drpIsNotRP" value="${param.drpIsNotRP}"/>
			<input type="hidden" name="incaricato" value="${param.nosogg}"/>
			<input type="hidden" name="nomeute" value="${param.nomeute}"/>
			<input type="hidden" name="pubbesito" value="${param.pubbesito}"/>
			<input type="hidden" name="alertBloccoDati" value="${param.alertBloccoDati}"/>
			<br>
			<c:choose>
				<c:when test="${param.nosogg eq 'true' && param.fine ne 'true'}">
						La gestione della scheda attraverso M-RP è possibile solo se per la procedura è stato indicato il RUP o se risulta impostato il campo "Trasmissione schede in carico a".
				</c:when>
				<c:when test="${param.nosogg ne 'true' && param.fine ne 'true' && param.inviabile ne '1'}">
					Tramite questa funzione è possibile attivare la gestione della scheda ANAC nell'applicativo M-RP.
					<c:if test="${param.alertBloccoDati eq 'true'}">
						<br><br>Una volta attivata la gestione, i dati della procedura saranno in sola consultazione.
					</c:if>
					<br><br>In caso di errori o per altre necessità è possibile riprendere in gestione la scheda con la funzione "Riprendi gestione scheda da M-RP" disponibile da menu laterale.
					<c:if test="${param.pubbesito eq 'true'}">
						<br><br>
						<div style="color: blue;"><b>ATTENZIONE:</b> Per la gara non risulta ancora pubblicato l'esito su portale Appalti.
							<br>Per procedere alla pubblicazione attivare la funzione "Pubblica esito su portale Appalti" disponibile nella pagina.
						</div>
					</c:if>
					<br><br>Confermi l'operazione?
				</c:when>
				<c:when test="${param.nosogg ne 'true' && param.fine ne 'true' && param.inviabile eq '1'}">
					Tramite questa funzione è possibile disattivare la gestione della scheda ANAC nell'applicativo M-RP.
					<br><br>Confermi l'operazione?
				</c:when>
				<c:when test="${param.fine eq 'true' && param.drpIsNotRP eq 'true' && param.inviabile ne '1'}">
					Operazione completata correttamente.
					<br><br><b>ATTENZIONE:</b> Avvisare il Responsabile di Fase (DRP) "${param.nomeute}" che adesso può continuare l'iter di trasmissione della scheda dall'applicazione M-RP.
				</c:when>
				<c:when test="${param.fine eq 'true' && param.drpIsNotRP ne 'true' && param.inviabile ne '1'}">
					Operazione completata correttamente.
					<br><br><b>ATTENZIONE:</b> Avvisare il Responsabile di Progetto (RP) "${param.nomeute}" che adesso può continuare l'iter di trasmissione della scheda dall'applicazione M-RP.
				</c:when>
				<c:when test="${param.fine eq 'true' && param.drpIsNotRP ne 'true' && param.inviabile eq '1'}">
					Gestione M-RP disattivata per la scheda.<br><br>Il Responsabile di Progetto (RP) "${param.nomeute}" NON potrà gestire la scheda nell'applicativo M-RP.
				</c:when>
				<c:when test="${param.fine eq 'true' && param.drpIsNotRP eq 'true' && param.inviabile eq '1'}">
					Gestione M-RP disattivata per la scheda.<br><br>Il Responsabile di Fase (DRP) "${param.nomeute}" NON potrà gestire la scheda nell'applicativo M-RP.
				</c:when>
			</c:choose>
			<br><br>
		</form>	
	</gene:redefineInsert>
	<gene:redefineInsert name="buttons">
		<c:choose>
			<c:when test="${param.nosogg ne 'true' && param.fine ne 'true'}">
				<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:abilitaMRP();">
				<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
			</c:when>
			<c:otherwise>
				<INPUT type="button" class="bottone-azione" value="Chiudi"	title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
			</c:otherwise>
		</c:choose>
	</gene:redefineInsert>
	
	<gene:javaScript>

		function abilitaMRP(){
		bloccaRichiesteServer();
			setTimeout(() => {
				$.ajax({
					type: "GET",
					dataType: "json",
					url: "${contextPath}/pg/ScambioDatiPCP.do?metodo=gestioneMRP",
					data: {
						codiceGara: "${param.codiceGara}",
						idForm: "${param.idform}",
						abilitazioneMrp: "${param.inviabile}"!=="1"
					},
					async: false,
					success: function(json) {
						window.location = window.location.href +"&fine=true";
						window.opener.ricaricaPagina();
					}
				});
			},150);
		}

		function annulla() {
			window.close();
		}
	
	</gene:javaScript>	
</gene:template>