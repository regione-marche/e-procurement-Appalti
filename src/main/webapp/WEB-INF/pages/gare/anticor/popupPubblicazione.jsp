<%
/*
 * Created on: 30-08-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Popup rettifica */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.operazioneEseguita and requestScope.operazioneEseguita eq "1"}' >
<script type="text/javascript">
	window.opener.document.forms[0].pgSort.value = "";
	window.opener.document.forms[0].pgLastSort.value = "";
	window.opener.document.forms[0].pgLastValori.value = "";
	window.opener.bloccaRichiesteServer();
	window.opener.listaVaiAPagina(0);
	window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value="Aggiornamento stato 'Pubblica?'" />

<c:choose>
	<c:when test='${not empty param.id}'>
		<c:set var="id" value="${param.id}" />
	</c:when>
	<c:otherwise>
		<c:set var="id" value="${id}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.operazione}'>
		<c:set var="operazione" value="${param.operazione}" />
	</c:when>
	<c:otherwise>
		<c:set var="operazione" value="${operazione}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${operazione eq "1"}'>
		<c:set var="valorePubblica" value='Si' />
	</c:when>
	<c:otherwise>
		<c:set var="valorePubblica" value='No' />
	</c:otherwise>
</c:choose>



<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="ANTICORLOTTI" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupPubblicaAdempimenti">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${requestScope.erroreOperazione eq "1"}'>
					Ci sono stati degli errori durante l'aggiornamento del campo 'Pubblica'.
				</c:when>
				<c:otherwise>
					Se si procede, il valore del campo 'Pubblica?' verrà impostato a '${valorePubblica }'.<br> 
					Vuoi procedere con l'operazione'?
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
		
		<input type="hidden" name="id" id="id" value="${id}" />
		<input type="hidden" name="operazione" id="operazione" value="${operazione}" />
		
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${requestScope.errori eq "1"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
	</gene:redefineInsert>
</c:if>
	
	<gene:javaScript>
		
		function conferma() {
			document.forms[0].jspPathTo.value="gare/anticor/popupPubblicazione.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
		function chiudi(){
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();
		}
		
	

	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>