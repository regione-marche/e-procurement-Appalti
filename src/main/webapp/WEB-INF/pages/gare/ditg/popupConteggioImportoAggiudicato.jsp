<%
/*
 * Created on: 02-10-2017
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
		Popup per il calcolo dell'importo aggiudicato nel periodo per la pagina lista-ImportoAggiudicatoOperatori.jsp
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.calcoloEseguito and requestScope.calcoloEseguito eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:choose>
	<c:when test='${!empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.genereGara}'>
		<c:set var="genereGara" value="${param.genereGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="genereGara" value="${genereGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${genereGara eq 10}'>
		<c:set var="msgTipo" value="dell'elenco" />
	</c:when>
	<c:otherwise>
		<c:set var="msgTipo" value="del catalogo" />
	</c:otherwise>
</c:choose>

<gene:setString name="titoloMaschera" value='Calcolo importo aggiudicato nel periodo' />


<c:set var="modo" value="NUOVO" scope="request" />
	
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="DITG" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupConteggioImportoAggiudicato">
	
		<gene:campoScheda>
			<td>
			<br>
			Vuoi procedere al calcolo dell'importo aggiudicato nel periodo degli operatori economici ${msgTipo}?<br> 
			<br>
			<br>
			</td>
		</gene:campoScheda>

		<input type="hidden" id="ngara" name="ngara" value="${ngara}">
	</gene:formScheda>
	<c:if test="${requestScope.calcoloEseguito eq '2'}">
		<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
		</gene:redefineInsert>
	</c:if>	
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/ditg/popupConteggioImportoAggiudicato.jsp";
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