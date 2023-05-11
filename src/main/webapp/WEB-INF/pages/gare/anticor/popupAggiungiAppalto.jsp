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
<gene:setString name="titoloMaschera" value="Aggiungi lotto da dati correnti" />

<c:choose>
	<c:when test='${not empty param.id}'>
		<c:set var="id" value="${param.id}" />
	</c:when>
	<c:otherwise>
		<c:set var="id" value="${id}" />
	</c:otherwise>
</c:choose>


<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="ANTICORLOTTI" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAggiungiAppaltoAdempimento">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${requestScope.erroreOperazione eq "1"}'>
					Si sono verificati degli errori durante l'inserimento.
				</c:when>
				<c:when test='${requestScope.LottiNonImportati eq "1"}'>
					Non è stato importato alcun lotto.
				</c:when>
				<c:otherwise>
					Impostare il codice CIG del lotto che si vuole importare dai dati correnti
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	<gene:campoScheda campo="CIG" campoFittizio="true" value='' definizione="T10;;;;G1CODCIG" obbligatorio="true" visibile="${requestScope.LottiNonImportati ne 1 and requestScope.erroreOperazione ne 1}"/>
	<gene:campoScheda campo="ID" entita="ANTICOR" campoFittizio="true" value='${ id}' definizione="N12" visibile="false"/>		
		<input type="hidden" name="id" id="id" value="${id}" />
		
		
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${requestScope.erroreOperazione eq "1" || requestScope.LottiNonImportati eq "1"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
	</gene:redefineInsert>
</c:if>
	
	<gene:javaScript>
		
		function conferma() {
			var cig=getValue("CIG");
			if(cig=="" || cig==null){
				alert("Inserire il codice CIG");
				return;
			}else if(cig.length != 10){
				alert("Il codice CIG deve essere di 10 caratteri");
				return;
			}
			document.forms[0].jspPathTo.value="gare/anticor/popupAggiungiAppalto.jsp";
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