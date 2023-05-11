<%
/*
 * Created on: 13-10-2010
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
		Finestra per l'attivazione della funzione 'Ricarica documenti'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.ricaricaEseguita and requestScope.ricaricaEseguita eq "1"}' >
<script type="text/javascript">
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Ricarica documenti' />

<c:set var="codiceGara" value='${gene:getValCampo(key, "DITG.CODGAR5")}' />
<c:set var="numeroGara" value='${gene:getValCampo(key, "DITG.NGARA5")}' />
<c:set var="codiceDitta" value='${gene:getValCampo(key, "DITG.DITTAO")}' />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test="${!empty param.isElenco }">
		<c:set var="isElenco" value="${param.isElenco }"/>
	</c:when>
	<c:otherwise>
		<c:set var="isElenco" value="${isElenco }"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${isElenco == 'SI'}">
		<c:set var="msg" value="dell'elenco"/>
		<c:set var="msg1" value="elenco"/>
	</c:when>
	<c:otherwise>
		<c:set var="msg" value="della gara"/>
		<c:set var="msg1" value="gara"/>
	</c:otherwise>
</c:choose>
	
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="TORN" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupRicaricaDocumenti">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			Mediante questa funzione è possibile reinizializzare la lista dei documenti richiesti alla ditta con quelli definiti nella documentazione ${msg }.
			<br>Confermi l'operazione?
			<br> 
			<br><b>ATTENZIONE: se si procede con l'operazione, tutti i dati già inseriti per la ditta selezionata verranno cancellati.</b> 
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>

		<gene:campoScheda campo="CODGAR" campoFittizio="true" defaultValue="${codiceGara}" visibile="false" definizione="T21;0"/>
		<gene:campoScheda campo="NGARA" campoFittizio="true" defaultValue="${numeroGara}" visibile="false" definizione="T20;0"/>
		<gene:campoScheda campo="CODICEDITTA" campoFittizio="true" defaultValue="${codiceDitta}" visibile="false" definizione="T10;0"/>
		<gene:campoScheda campo="GARAOFFERTAUNICA" campoFittizio="true" defaultValue="${param.isGaraLottiConOffertaUnica}" visibile="false" definizione="T2;0"/>
		<gene:campoScheda campo="TUTTEDITTE" campoFittizio="true" title="Applicare a tutte le ditte in ${msg1 }?" defaultValue="2" definizione="T2;0;;SN"/>
		<input type="hidden" id="isElenco" value="isElenco"/>
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/popupRicaricaDocumenti.jsp";
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