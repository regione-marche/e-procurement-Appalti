<%
/*
 * Created on: 21-02-2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Popup esporta dati Adempimenti legge 190/2012 */
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

<c:choose>
	<c:when test='${not empty param.id}'>
		<c:set var="id" value="${param.id}" />
	</c:when>
	<c:otherwise>
		<c:set var="id" value="${id}" />
	</c:otherwise>
</c:choose>

<c:if test='${empty param.controlloSuperato}'>
	<c:set var="controlloSuperato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.ControlliRicaricaAdempimentiFunction", pageContext, id)}'/>
</c:if>

<gene:setString name="titoloMaschera" value="Ricarica adempimento da dati correnti"/>
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="ANTICORLOTTI" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupRicaricaDatiAdempimento">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			<c:choose>
			 	<c:when test='${requestScope.erroreOperazione eq "1" }'>
			 		Non è possibile procedere con il ricaricamento dei dati dell'adempimento
			 	</c:when>
			 	<c:when test='${not empty param.operazione or (empty CIGDuplicatiInDB and empty CIGDuplicatiLotti)}' >
			 		Selezionare una delle due opzioni<br><br>
					<input type="radio" name="operazione" value="ricaricaTuttiLotti" id="ricaricaTuttiLotti" checked="checked" > Ricarica tutti i lotti dell'adempimento derivanti dai dati correnti, perdendo le eventuali modifiche precedentemente apportate
					<br><br>
					<input type="radio" name="operazione" value="aggiungiLotti" id="aggiungiLotti" > Aggiungi solo i lotti non presenti nell'adempimento, lasciando inalterati quelli esistenti.
			 	</c:when>
			 	<c:otherwise>
			 		<input type="hidden" name="operazione" id="operazione" value="noOperazione" />
			 		<c:if test='${!empty CIGDuplicatiInDB}'>
		 				<span id="messaggioCIGDuplicatiInDB">
		 					<br><b>ATTENZIONE:</b><br>Nei dati correnti sono presenti più lotti con uguale codice CIG (${CIGDuplicatiInDB}).
		 					<br>Procedendo, eventuali lotti con tali codici CIG attinenti all'anno di riferimento dell'adempimento non verranno importati.
		 					<br>
		 				</span>
		 			</c:if>
		 			<c:if test='${!empty CIGDuplicatiLotti}'>
		 				<span id="messaggioCIGDuplicatiLotti">
		 					<br><b>ATTENZIONE:</b><br>Risultano presenti nell'adempimento dei lotti non derivanti dai dati correnti il cui codice CIG è uguale a quello dei lotti che si stanno per importare (${CIGDuplicatiLotti}).
		 					<br>Procedendo, i lotti dell'adempimento con tali codici CIG verranno sostituiti con quelli importati dai dati correnti.
		 					<br>
		 				</span>
		 			</c:if>
			 	</c:otherwise>
			</c:choose>
			<br>&nbsp;
			<br>&nbsp;
			<br>
		</td>
	</gene:campoScheda>

	<input type="hidden" name="id" id="id" value="${id}" />
	<input type="hidden" name="controlloSuperato" id="controlloSuperato" value="${controlloSuperato}" />

<c:choose>
	<c:when test='${not empty param.CIGDuplicatiLotti}' >
		<input type="hidden" name="CIGDuplicatiLotti" id="CIGDuplicatiLotti" value="${param.CIGDuplicatiLotti}" />
	</c:when>
	<c:otherwise>
		<input type="hidden" name="CIGDuplicatiLotti" id="CIGDuplicatiLotti" value="${CIGDuplicatiLotti}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty  param.CIGDuplicatiInDB}' >
		<input type="hidden" name="CIGDuplicatiInDB" id="CIGDuplicatiInDB" value="${param.CIGDuplicatiInDB}" />
	</c:when>
	<c:otherwise>
		<input type="hidden" name="CIGDuplicatiInDB" id="CIGDuplicatiInDB" value="${CIGDuplicatiInDB}" />
	</c:otherwise>
</c:choose>

	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${requestScope.erroreOperazione eq "1"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
	</gene:redefineInsert>
</c:if>

	<gene:javaScript>
		var procedereConSubmit = true;
		
		function conferma() {
			if (procedereConSubmit) {
				document.forms[0].jspPathTo.value = "gare/anticor/popupRicaricaAdempimento.jsp";
				schedaConferma();
			} else {
				procedereConSubmit = true;
				$("#messaggioCIGDuplicatiLotti").show();
				//$("#messaggioCIGDuplicatiInDB").hide();
			}
		}

		function annulla() {
			window.close();
		}

		<c:if test='${controlloSuperato eq "nok" and !empty CIGDuplicatiInDB  and !empty CIGDuplicatiLotti}'>
			$("#messaggioCIGDuplicatiLotti").hide();
			procedereConSubmit = false;
		</c:if>	
	</gene:javaScript>
</gene:template>
</div>
</c:otherwise>
</c:choose>
	