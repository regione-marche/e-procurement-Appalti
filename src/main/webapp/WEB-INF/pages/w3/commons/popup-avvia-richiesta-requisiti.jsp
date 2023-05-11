
<%
	/*
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

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<c:set var="entita" value="${param.entita}" />
	<c:set var="numgara" value="${param.numgara}" />
	<c:set var="idgara" value="${param.idgara}" />
	<c:set var="numreq" value="${param.numreq}" />
	<c:set var="stato_simog_gara" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.GetStatoSimogW3GaraFunction",pageContext,numgara)}' />

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<c:if test="${entita eq 'W3GARAREQ'}">
		<gene:setString name="titoloMaschera"  value='Richiesta requisiti gara' />
	</c:if>
	
	
	<gene:redefineInsert name="corpo">
		<table class="dettaglio-notab">
		
					<td class="valore-dato" colspan="2">
						<br>
						Questa funzione <b>prepara i dati e li invia all'ANAC</b>
						per la <b>richiesta dei requisiti.</b>
						<br>
						<br>
						Tale richiesta, tuttavia, è possibile solamente se tutti i dati inseriti 
						superano i controlli di validit&agrave;.
						<br>
						<br>
					</td>

			<tr>
				<td colspan="2" class="comandi-dettaglio">
					<c:choose>
						<c:when test="${entita eq 'W3GARAREQ'}">
							<INPUT type="button" class="bottone-azione" value="Controlla i dati ed invia la richiesta" title="Controlla i dati ed invia la richiesta" onclick="javascript:avviaRichiestaRequisiti('${entita}','${numgara}','${numreq}','${idgara}');">		
						</c:when>
					</c:choose>
					<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	
	<gene:javaScript>
		document.forms[0].jspPathTo.value="w3/commons/popup-avvia-richiesta-requisiti.jsp";
		
		function annulla(){
			window.close();
		}
		
		function avviaRichiestaRequisiti(entita,numgara,numreq,idgara){
			var action = "${pageContext.request.contextPath}/w3/AvviaRichiestaRequisiti.do";
			action=action + '?' + csrfToken + '&entita=' + entita + '&numgara=' + numgara + '&numreq=' + numreq + '&idgara=' + idgara;
			document.location.href=action;
		}
	
	</gene:javaScript>	
</gene:template>

</div>

