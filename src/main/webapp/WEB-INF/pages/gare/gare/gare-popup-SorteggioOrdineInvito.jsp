<%
/*
 * Created on: 27-02-2021
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
		Finestra per l'assegnazione del numero d'ordine 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.ordineAssegnato and requestScope.ordineAssegnato eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<c:set var="codiceGara" value="${param.codiceGara}" />
<c:set var="ngara" value="${param.ngara}" />
<c:set var="sortinv" value="${param.sortinv}" />

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>
	
<gene:setString name="titoloMaschera" value="Sorteggio ordine invito" />
<c:if test="${sortinv eq '1' }">
	<c:set var="situazioneNOI" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetSituazioneNumOrdineInvitoFunction", pageContext, codiceGara,ngara )}' />
</c:if>

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="DITG" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreSorteggioOrdineInvito">
		<gene:campoScheda>
			<td colSpan="2">
				<br>
				Mediante questa funzione viene effettuata l'assegnazione, mediante sorteggio, del numero ordine di invito agli operatori in gara.
				<br>
				<c:choose>
					<c:when test="${(sortinv eq 1) && (situazioneNOI ne 0)}">
					<c:set var="blocco" value="true"/>
						<br><b>Il sorteggio risulta già fatto.</b>
						<br>
						<br>
					</c:when>
					<c:otherwise>
						<br>
						Confermi l'operazione ?
						<br>
						<br>
					</c:otherwise>
				</c:choose>
			</td>
		</gene:campoScheda>

		<input type="hidden" name="ngara" id="ngara" value="${ngara}">
		<input type="hidden" name="codiceGara" id="codiceGara" value="${codiceGara}">
		<input type="hidden" name="sortinv" id="sortinv" value="${sortinv}">

	</gene:formScheda>
  </gene:redefineInsert>
  		 <c:if test="${blocco eq true}" >
		  	<gene:redefineInsert name="buttons">
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
			</gene:redefineInsert>
		  </c:if>
  
  	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/gare/gare-popup-SorteggioOrdineInvito.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
	
	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>