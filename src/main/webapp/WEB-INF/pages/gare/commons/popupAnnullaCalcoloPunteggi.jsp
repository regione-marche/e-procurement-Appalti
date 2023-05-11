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
	<c:when test='${!empty param.chiave}'>
		<c:set var="chiave" value="${param.chiave}" />
	</c:when>
	<c:otherwise>
		<c:set var="chiave" value="${chiave}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.tipo}'>
		<c:set var="tipo" value="${param.tipo}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipo" value="${tipo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${fn:contains(chiave,'GARE') }">
		<c:set var="ngara" value='${gene:getValCampo(chiave,"NGARA")}'  />
		<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, ngara)}' />
	</c:when>
	<c:when test="${fn:contains(chiave,'DITG') }">
		<c:set var="ngara" value='${gene:getValCampo(chiave,"NGARA5")}'  />
		<c:set var="codgar" value='${gene:getValCampo(chiave,"CODGAR5")}' />	
	</c:when>
</c:choose>


<c:set var="esistonoDitteConPunteggio" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePunteggioValorizzatoFunction", pageContext, ngara, tipo )}' />

<c:choose>
	<c:when test="${tipo eq '1' }">
		<c:set var="msgTitolo" value ="tecnica" />
	</c:when>
	<c:otherwise>
		<c:set var="msgTitolo" value ="economica" />
	</c:otherwise>
</c:choose>


<gene:setString name="titoloMaschera" value='Annulla calcolo punteggi criteri di valutazione della busta ${msgTitolo }' />


<c:set var="modo" value="NUOVO" scope="request" />
	
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="DPUN" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAnnullaCalcoloPunteggi">
	
		<gene:campoScheda>
			<td>
			<br>
		<c:choose>
			<c:when test="${esistonoDitteConPunteggio eq 'no'}">
				Nessun calcolo dei punteggi dei criteri di valutazione della busta ${msgTitolo} da annullare.<br>
			</c:when>
			<c:otherwise>
				Confermi l'annullamento del calcolo dei punteggi dei criteri di valutazione della busta ${msgTitolo } delle ditte?<br>
			</c:otherwise>
		</c:choose>
			<br>
			</td>
		</gene:campoScheda>

		<input type="hidden" name="chiave" value="${chiave}">
		<input type="hidden" name="tipo" value="${tipo}">
		<input type="hidden" name="codgar" value="${codgar}">
		<input type="hidden" name="ngara" value="${ngara}">
	</gene:formScheda>
		<c:if test="${esistonoDitteConPunteggio eq 'no' || requestScope.calcoloEseguito eq '2'}">
			<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			</gene:redefineInsert>
		</c:if>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/popupAnnullaCalcoloPunteggi.jsp";
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