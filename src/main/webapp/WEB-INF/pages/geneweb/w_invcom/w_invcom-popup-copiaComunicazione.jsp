<%
/*
 * Created on: 21-11-2011
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
		Finestra per la copia di una comunicazione 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.copiaEseguita and requestScope.copiaEseguita eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">


<c:choose>
	<c:when test='${not empty param.idprg}'>
		<c:set var="idprg" value="${param.idprg}" />
	</c:when>
	<c:otherwise>
		<c:set var="idprg" value="${idprg}" />
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

<c:choose>
	<c:when test='${not empty param.comdatins}'>
		<c:set var="comdatins" value="${param.comdatins}" />
	</c:when>
	<c:otherwise>
		<c:set var="comdatins" value="${comdatins}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.compub}'>
		<c:set var="compub" value="${param.compub}" />
	</c:when>
	<c:otherwise>
		<c:set var="compub" value="${compub}" />
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
	<c:when test='${not empty param.commodello}'>
		<c:set var="commodello" value="${param.commodello}" />
	</c:when>
	<c:otherwise>
		<c:set var="commodello" value="${commodello}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.descc}'>
		<c:set var="descc" value="${param.descc}" />
	</c:when>
	<c:otherwise>
		<c:set var="descc" value="${descc}" />
	</c:otherwise>
</c:choose>

<gene:setString name="titoloMaschera" value="Copia comunicazione del ${comdatins}" />
<c:choose>
	<c:when test="${tipo eq '0' }">
		<c:set var="valoreInizializzazioneDestinatari" value="2"/>
		<c:set var="valoreInizializzazioneDestinatariErrore" value="2"/>
	</c:when>
	<c:otherwise>
		<c:set var="valoreInizializzazioneDestinatari" value="1"/>
		<c:set var="valoreInizializzazioneDestinatariErrore" value="1"/>
	</c:otherwise>
</c:choose>

<c:if test="${commodello eq '1' }">
	<c:set var="valoreInizializzazioneDestinatari" value="1"/>
	<c:set var="valoreInizializzazioneDestinatariErrore" value="2"/>
</c:if>
	
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="W_INVCOM" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInizializzazioniW_INVCOM" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCopiaComunicazione">
	
	<table class="dettaglio-notab">
		<tr>
		 	<td>
		 		<gene:campoScheda campo="ALLEGATI" title="Copia allegati?" value="1" definizione="T2;;;SN;" campoFittizio="true" visibile='${tipo eq "0" }'/>
		 		<gene:campoScheda campo="DOCUMENTI" title="Copia documenti richiesti?" value="${gene:if(commodello eq '1', '1', '2')}" definizione="T2;;;SN;" campoFittizio="true" visibile='${commodello eq "1" and tipo eq "0" }'/>
	 			<gene:campoScheda campo="DESTINATARI" title="Copia soggetti destinatari?" value="${valoreInizializzazioneDestinatari }" definizione="T2;;;SN;" campoFittizio="true" visibile='${compub ne "1" and tipo eq "0" }' modificabile="${commodello ne '1' }"/>
	 			<gene:campoScheda campo="DESTINATARI_ERRORE" title="Copia solo soggetti per cui è fallito l'invio?" value="${valoreInizializzazioneDestinatariErrore }" definizione="T2;;;SN;" campoFittizio="true" visibile='false'/>
	 			<gene:campoScheda visibile="${tipo eq '1' }">
	 				<br>
	 				Mediante questa funzione viene eseguita la copia della comunicazione con i relativi allegati e con destinatari i soli soggetti per cui è fallito il precedente invio.<br>
	 				Si vuole procedere?
	 				<br> 
	 				<br>
	 			</gene:campoScheda>
	 		</td>
		</tr>
		
		<input type="hidden" name="idprg" id="idprg" value="${idprg }">
		<input type="hidden" name="idcom" id="idcom" value="${idcom}">
		<input type="hidden" name="committ" id="committ" value="${inizializzazioneMittente}">
		<input type="hidden" name="comdatins" id="comdatins" value="${comdatins}">
		<input type="hidden" name="compub" id="compub" value="${compub}">
		<input type="hidden" name="commodello" id="commodello" value="${commodello}">
		<input type="hidden" name="descc" id="descc" value="${descc}">
		
	</table>
	
		
		
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="geneweb/w_invcom/w_invcom-popup-copiaComunicazione.jsp";
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