<%
/*
 * Created on: 18-04-2011
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
		Finestra per l'acquisizione delle offerte del fornitore da portale AUR
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


<c:choose>
	<c:when test='${not empty param.numeroGara}'>
		<c:set var="numeroGara" value="${param.numeroGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="numeroGara" value="${numeroGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codiceDitta}'>
		<c:set var="codiceDitta" value="${param.codiceDitta}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceDitta" value="${codiceDitta}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.carrello}'>
		<c:set var="carrello" value="${param.carrello}" />
	</c:when>
	<c:otherwise>
		<c:set var="carrello" value="${carrello}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.garaLottiConOffertaUnica}'>
		<c:set var="garaLottiConOffertaUnica" value="${param.garaLottiConOffertaUnica}" />
	</c:when>
	<c:otherwise>
		<c:set var="garaLottiConOffertaUnica" value="${garaLottiConOffertaUnica}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.tipoForniture}'>
		<c:set var="tipoForniture" value="${param.tipoForniture}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoForniture" value="${tipoForniture}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codStazioneAppaltante}'>
		<c:set var="codStazioneAppaltante" value="${param.codStazioneAppaltante}" />
	</c:when>
	<c:otherwise>
		<c:set var="codStazioneAppaltante" value="${codStazioneAppaltante}" />
	</c:otherwise>
</c:choose>

<gene:setString name="titoloMaschera" value='Acquisizione offerta da portale Alice AUR' />



<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GCAP" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupInserOffertaFornitoreAUR">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${requestScope.erroreAcquisizione eq "1"}'>
					&nbsp;Ci sono stati degli errori durante l'acquisizione.${requestScope.msg}
				</c:when>
				<c:otherwise>
					&nbsp;Confermi l'acquisizione dell'offerta dal portale Alice AUR?
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	
	
		
		<input type="hidden" name="numeroGara" id="numeroGara" value="${numeroGara}" />
		<input type="hidden" name="codiceDitta" id="codiceDitta" value="${codiceDitta}" />
		<input type="hidden" name="carrello" id="carrello" value="${carrello}" />
		<input type="hidden" name="garaLottiConOffertaUnica" id="garaLottiConOffertaUnica" value="${garaLottiConOffertaUnica}" />
		<input type="hidden" name="tipoForniture" id="tipoForniture" value="${tipoForniture}" />
		<input type="hidden" name="codStazioneAppaltante" id="codStazioneAppaltante" value="${codStazioneAppaltante}" />
		
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${requestScope.erroreAcquisizione eq "1" || esito eq "-1"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
	</gene:redefineInsert>
</c:if>
	
	<gene:javaScript>
		
		
		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/popupInserOffertaFornitoreAUR.jsp";
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