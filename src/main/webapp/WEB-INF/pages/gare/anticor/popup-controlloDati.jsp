<%
/*
 * Created on: 02-09-2013
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
	opener.historyReload();
	window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value="Approva dati" />

<c:choose>
	<c:when test='${not empty param.id}'>
		<c:set var="id" value="${param.id}" />
	</c:when>
	<c:otherwise>
		<c:set var="id" value="${id}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipo}'>
		<c:set var="tipo" value="${param.tipo}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipo" value="${tipo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.entitaAdempimenti}'>
		<c:set var="entitaAdempimenti" value="${param.entitaAdempimenti}" />
	</c:when>
	<c:otherwise>
		<c:set var="entitaAdempimenti" value="${entitaAdempimenti}" />
	</c:otherwise>
</c:choose>

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="ANTICORLOTTI" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupRettificaAdempimenti">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			Confermi l'approvazione dei dati dell'adempimento?
			<br>Procedendo non sarà più possibile apportare modifiche all'adempimento.
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	
	
	<input type="hidden" name="id" id="id" value="${id}" />
	<input type="hidden" name="operazione" id="operazione" value="APPROVAZIONE" />
	<input type="hidden" name="tipo" id="tipo" value="${tipo}" />	
	<input type="hidden" name="entitaAdempimenti" id="entitaAdempimenti" value="${entitaAdempimenti}" />	
	</gene:formScheda>
  </gene:redefineInsert>

<c:choose>
	<c:when test='${requestScope.erroreOperazione eq "1" }'>
		<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:window.close();">&nbsp;
		</gene:redefineInsert>
	</c:when>	
	<c:otherwise>
			<gene:redefineInsert name="buttons">
				<c:choose>
					<c:when test="${tipo eq 1 }">
						<INPUT type="button" class="bottone-azione" value="Approva" title="Approva" onclick="javascript:conferma()">&nbsp;
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
					</c:when>
					<c:otherwise>
						<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla()">&nbsp;
					</c:otherwise>
				</c:choose>
				
			</gene:redefineInsert>
	</c:otherwise>
</c:choose>

		
	
	<gene:javaScript>
		
		function conferma() {
			document.forms[0].jspPathTo.value="gare/anticor/popup-controlloDati.jsp";
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