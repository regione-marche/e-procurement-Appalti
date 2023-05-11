
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

	<c:set var="entita" value="${param.entita}" scope="request"/>
	<c:set var="numgara" value="${param.numgara}" scope="request"/>
	<c:set var="numlott" value="${param.numlott}" />
	<c:set var="pubblicabile" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.IsGaraLottiPubblicabileFunction",pageContext,numgara)}' />
	<c:set var="garaHasLotti" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.GaraHasLottiFunction",pageContext,numgara)}' />	

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:setString name="titoloMaschera"  value='Richiesta pubblicazione' />
	
	<gene:redefineInsert name="corpo">
		<table class="dettaglio-notab">
		
			<c:choose>
				<c:when test="${pubblicabile eq 'true' && garaHasLotti eq 'true'}">
					<td class="valore-dato" colspan="2">
						<br>
						Questa funzione <b>prepara i dati e li invia all'ANAC</b>
						per la <b>richiesta di pubblicazione della gara e dei lotti</b>.
						<br>
						<br>
						Tale richiesta, tuttavia, è possibile solamente se tutti i dati di pubblicazione inseriti 
						superano i controlli di validit&agrave;.
						<br>
						<br>
					</td>
				</c:when>
				<c:otherwise>
					<td width="70" align="center">
						<img width="36" height="36" src="${pageContext.request.contextPath}/img/attenzione.gif"/>&nbsp;&nbsp;
					</td>
					<c:choose>
						<c:when test="${pubblicabile eq 'false' && garaHasLotti eq 'true'}">
							<td>
								<br>
								<b>Alcuni lotti sono in uno stato non compatibile con la richiesta di pubblicazione.</b> 
								<br>
								<br>
								Prima di inviare la richiesta di pubblicazione della gara &egrave; 
								necessario inviare tutte le richieste 
								(richiesta codice CIG, richiesta modifica o richiesta cancellazione) dei lotti associati alla gara.
								<br>
								<br>
							</td>
						</c:when>
						<c:otherwise>
							<td>
								<br>
								<b>Non è presente alcun lotto.</b> 
								<br>
								<br>
								Prima di inviare la richiesta di pubblicazione della gara &egrave; 
								necessario inserire almeno un lotto.
								<br>
								<br>
							</td>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>

			<tr>
				<td colspan="2" class="comandi-dettaglio">
					<c:if test="${pubblicabile eq 'true' && garaHasLotti eq 'true'}">
						<INPUT type="button" class="bottone-azione" value="Controlla i dati e pubblica" title="Controlla i dati e pubblica" onclick="javascript:avviaPubblica('${entita}','${numgara}');">
					</c:if>		
					<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	
	<gene:javaScript>
		document.forms[0].jspPathTo.value="w3/w3gara/w3gara-avvia-pubblica-gara-lotto.jsp";
		
		function annulla(){
			window.close();
		}
		
		function avviaPubblica(entita,numgara){
			var action = "${pageContext.request.contextPath}/w3/AvviaPubblicaGaraLotto.do";
			action=action + '?' + csrfToken + '&entita=' + entita + '&numgara=' + numgara;
			document.location.href=action;
		}
	
	</gene:javaScript>	
</gene:template>

</div>

