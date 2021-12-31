<%
/*
 * Created on: 13-07-2017
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
		Popup per annulare il calcolo dei punteggi tecnici o economici
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.annullamentoEseguito and requestScope.annullamentoEseguito eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:choose>
	<c:when test='${!empty param.chiave}'>
		<c:set var="chiave" value="${param.chiave}" />
	</c:when>
	<c:otherwise>
		<c:set var="chiave" value="${chiave}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.idconfi}'>
		<c:set var="idconfi" value="${param.idconfi}" />
	</c:when>
	<c:otherwise>
		<c:set var="idconfi" value="${idconfi}" />
	</c:otherwise>
</c:choose>

<c:set var="isRiservatezzaAttiva" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsRiservatezzaAttivaFunction", pageContext, chiave, idconfi )}' />


<gene:setString name="titoloMaschera" value='Annulla riservatezza dati su documentale' />


<c:set var="modo" value="NUOVO" scope="request" />
	
	
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAnnullaRiservatezza">
		
			<gene:campoScheda>
				<td>
				<br>
			<c:choose>
				<c:when test="${isRiservatezzaAttiva ne '1'}">
					Non risulta applicata la riservatezza sul documentale per la gara corrente.<br>
				</c:when>
				<c:otherwise>
					Confermi l'annullamento della riservatezza dei dati sul documentale per la gara corrente?<br>
					<b>ATTENZIONE:</b> una volta annullata la riservatezza, non è più possibile ripristinarla.<br>
				</c:otherwise>
			</c:choose>
				<br>
				</td>
			</gene:campoScheda>

			<input type="hidden" name="chiave" value="${chiave}">
			<input type="hidden" name="idconfi" value="${idconfi}">
		</gene:formScheda>
		<c:if test="${isRiservatezzaAttiva ne '1' || requestScope.annullamentoEseguito eq '2'}">
			<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			</gene:redefineInsert>
		</c:if>
	</gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/popupAnnullaRiservatezza.jsp";
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