<%
/*
 * Created on: 28-02-2011
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
		Finestra per l'attivazione della funzione 'Acquisizione singola da portale'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.acquisizioneEseguita and requestScope.acquisizioneEseguita eq "1"}' >
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
<gene:setString name="titoloMaschera" value='Acquisizione richiesta variazione dati identificativi anagrafica da portale Appalti' />

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.idcom}'>
		<c:set var="idcom" value="${param.idcom}" />
	</c:when>
	<c:otherwise>
		<c:set var="idcom" value="${idcom}" />
	</c:otherwise>
</c:choose>

<c:set var="comkey1" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMKEYGaraLottoFunction",pageContext,"PA",idcom)}' scope="request"/>

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="IMPR" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreControlliPreliminariVariazioneDatiIdentificativi" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAcquisisciVariazioneDatiIdentificativi">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${requestScope.erroreAcquisizione eq "1"}'>
					&nbsp;Ci sono stati degli errori durante l'acquisizione.
				</c:when>
				<c:when test='${not empty requestScope.messaggi}'>
					L'impresa '${committ}' richiede la variazione dei propri dati identificativi come riportato di seguito.
					<br>Alla conferma, tale richiesta verrà inserita nelle Note e Avvisi dell'impresa stessa.
				</c:when>
				<c:otherwise>
					&nbsp;Non vi sono variazioni significative.
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	<c:if test='${not empty requestScope.messaggi}'>
		<gene:campoScheda>
		<td colSpan="2">
			<textarea cols="90" rows="14" readonly="readonly">${requestScope.messaggi }</textarea>
		</td>
	</gene:campoScheda>	
	</c:if>
	
		
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="idcom" id="idcom" value="${idcom}" />
		<input type="hidden" name="comkey1" id="comkey1" value="${comkey1}" />
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${requestScope.erroreAcquisizione eq "1"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
	</gene:redefineInsert>
</c:if>
	
	<gene:javaScript>
		
				
		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/popupAcquisizioneVariazioneDatiIdentificativi.jsp";
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