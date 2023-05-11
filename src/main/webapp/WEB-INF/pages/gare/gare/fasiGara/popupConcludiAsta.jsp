<%
/*
 * Created on: 21-11-2016
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra per l'attivazione della funzione 'Concludi asta'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.conclusioneEseguita and requestScope.conclusioneEseguita eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Concludi asta elettronica' />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test='${!empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:set var="esistonoFasiInCorso" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoFasiAstaInCorsoFunction",  pageContext, ngara)}'/>	

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="TORN" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupConcludiAsta">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			Mediante questa funzione viene conclusa l'asta elettronica, permettendo di procedere all'aggiudicazione della gara.
			<br><br>
			<c:choose>
				<c:when test='${esistonoFasiInCorso eq "true"}'>
					<br> 
					<b>Non è possibile procedere perchè ci sono fasi di asta elettronica in corso o ancora da svolgere</b>
				</c:when>
				<c:otherwise>
					<br>Confermi l'operazione?
				</c:otherwise>
			</c:choose> 
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		
	</gene:formScheda>
	
  </gene:redefineInsert>
	<gene:redefineInsert name="buttons">
			<c:choose>
				<c:when test='${esistonoFasiInCorso eq "true" or requestScope.conclusioneEseguita eq "-1"}'>
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
				</c:when>
				<c:otherwise>
					<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value="Concludi asta" title="Concludi asta" onclick="javascript:conferma()">&nbsp;
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
				</c:otherwise>
			</c:choose>
	</gene:redefineInsert>
	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/gare/fasiGara/popupConcludiAsta.jsp";
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