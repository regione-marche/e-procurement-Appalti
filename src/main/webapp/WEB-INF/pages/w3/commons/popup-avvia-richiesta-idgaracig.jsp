
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
	<c:set var="numlott" value="${param.numlott}" />
	<c:set var="stato_simog_gara" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.GetStatoSimogW3GaraFunction",pageContext,numgara)}' />

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<c:choose>
		<c:when test="${entita eq 'W3GARA'}">
			<gene:setString name="titoloMaschera"  value='Richiesta assegnazione numero gara' />
		</c:when>
		<c:when test="${entita eq 'W3LOTT'}">
			<gene:setString name="titoloMaschera"  value='Richiesta assegnazione codice CIG' />
		</c:when>
		<c:when test="${entita eq 'W3SMARTCIG'}">
			<gene:setString name="titoloMaschera"  value='Richiesta assegnazione codice SMARTCIG' />
		</c:when>
	</c:choose>
	
	
	<gene:redefineInsert name="corpo">
		<table class="dettaglio-notab">
		
			<c:choose>
				<c:when test="${entita eq 'W3LOTT' and (stato_simog_gara eq '1')}">
					<td class="valore-dato" colspan="2">
						<br>
						<img width="40" height="36" src="${pageContext.request.contextPath}/img/attenzione.gif"/>&nbsp;&nbsp;
						<b>Per la gara associata non &egrave; ancora stato assegnato il <i>numero gara</i></b>.
						<br>
						<br>
						Per procedere nella richiesta di assegnazione del codice CIG del lotto &egrave; necessario chiedere prima l'assegnazione del <i>numero gara</i>.
						<br>
						<br>
					</td>
				</c:when>
				<c:otherwise>
					<td class="valore-dato" colspan="2">
						<br>
						Questa funzione <b>prepara i dati e li invia all'ANAC</b>
						per la <b>richiesta di assegnazione degli identificativi.</b>
						<br>
						<br>
						Tale richiesta, tuttavia, è possibile solamente se tutti i dati inseriti 
						superano i controlli di validit&agrave;.
						<br>
						<br>
					</td>
				</c:otherwise>
			</c:choose>

			<tr>
				<td colspan="2" class="comandi-dettaglio">
					<c:choose>
						<c:when test="${entita eq 'W3GARA'}">
							<INPUT type="button" class="bottone-azione" value="Controlla i dati ed invia la richiesta" title="Controlla i dati ed invia la richiesta" onclick="javascript:avviaRichiestaIDGARACIG('${entita}','${numgara}','1');">		
						</c:when>
						<c:when test="${entita eq 'W3LOTT' and !(stato_simog_gara eq '1')}">
							<INPUT type="button" class="bottone-azione" value="Controlla i dati ed invia la richiesta" title="Controlla i dati ed invia la richiesta" onclick="javascript:avviaRichiestaIDGARACIG('${entita}','${numgara}','${numlott}');">		
						</c:when>
						<c:when test="${entita eq 'W3SMARTCIG'}">
							<INPUT type="button" class="bottone-azione" value="Controlla i dati ed invia la richiesta" title="Controlla i dati ed invia la richiesta" onclick="javascript:avviaRichiestaSMARTCIG('${entita}','${numgara}');">		
						</c:when>
					</c:choose>
					<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	
	<gene:javaScript>
		document.forms[0].jspPathTo.value="w3/commons/popup-avvia-richiesta-idgaracig.jsp";
		
		function annulla(){
			window.close();
		}
		
		function avviaRichiestaIDGARACIG(entita,numgara,numlott){
			var action = "${pageContext.request.contextPath}/w3/AvviaRichiestaIDGARACIG.do";
			action=action + '?' + csrfToken + '&entita=' + entita + '&numgara=' + numgara + '&numlott=' + numlott;
			document.location.href=action;
		}
	
		function avviaRichiestaSMARTCIG(entita,numgara){
			var action = "${pageContext.request.contextPath}/w3/AvviaRichiestaIDGARACIG.do";
			action=action + '?' + csrfToken + '&entita=' + entita + '&numgara=' + numgara + '&numlott=1';
			document.location.href=action;
		}
		
	</gene:javaScript>	
</gene:template>

</div>

