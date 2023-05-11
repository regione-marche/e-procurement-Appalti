
<%
	/*
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>


<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<div style="width:97%;">

<gene:template file="popup-template.jsp">
	<c:choose>
		<c:when test="${!empty param.id}">
			<c:set var="id" value="${param.id}" />
		</c:when>
		<c:otherwise>
			<c:set var="id" value="${id}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${!empty param.tipo}">
			<c:set var="tipo" value="${param.tipo}" />
		</c:when>
		<c:otherwise>
			<c:set var="tipo" value="${tipo}" />
		</c:otherwise>
	</c:choose>

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	
	<c:choose>
		<c:when test="${tipo eq 'VALUTAZIONE'}">
			<c:set var="titolo" value="Valutazione prodotti" />		
			<c:set var="messaggioprocedi" value="Procedere con la valutazione dei prodotti ?" />
			<c:set var="messaggioavviso" value="" />
			<c:set var="titolopulsante" value="Valutazione prodotti" />
			<c:set var="messaggiononesistonoarticoli" value="Non &egrave; possibile procedere in quanto non &egrave; stato selezionato alcun articolo." />
		</c:when>
		<c:otherwise>
			<c:set var="titolo" value="Aggiorna dati prodotti" />
			<c:set var="messaggioprocedi" value="Procedere l'aggiornamento dei dati dei prodotti ?" />
			<c:set var="messaggioavviso" value="Non &egrave; possibile aggiornare i dati dei prodotti in quanto risultano generati degli ordini." />
			<c:set var="titolopulsante" value="Aggiorna dati prodotti" />
			<c:set var="messaggiononesistonoarticoli" value="Non &egrave; possibile aggiornare in quanto non &egrave; stato selezionato alcun articolo." />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera"  value='${titolo}' />
	
	
	<c:set var="isRicercaMercatoGeneratoOrdineFunction" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsRicercaMercatoGeneratoOrdineFunction", pageContext, id)}' />
	<c:set var="isRicercaMercatoEsistonoArticoliFunction" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsRicercaMercatoEsistonoArticoliFunction", pageContext, id)}' />
	
	<gene:redefineInsert name="corpo">
		<table class="dettaglio-notab">
			<td class="valore-dato" colspan="2">
				<br>
				<c:choose>
					<c:when test="${isRicercaMercatoEsistonoArticoliFunction == 'false'}">
						${messaggiononesistonoarticoli}
					</c:when>
					<c:when test="${isRicercaMercatoGeneratoOrdineFunction == 'false'}">
						${messaggioprocedi}
					</c:when>
					<c:otherwise>
						${messaggioavviso}
					</c:otherwise>
				</c:choose>
				<br>
				<br>
			</td>

			<tr>
				<td colspan="2" class="comandi-dettaglio">
					<c:if test="${isRicercaMercatoGeneratoOrdineFunction == 'false'}">
						<INPUT type="button" class="bottone-azione" value="${titolopulsante}" title="${titolopulsante}" onclick="javascript:attivaValutazioneProdotti(${id},'${tipo}');">
					</c:if>		
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	
	<gene:javaScript>
		
		function annulla(){
			window.close();
		}
		
		function attivaValutazioneProdotti(id, tipo){
			bloccaRichiesteServer();
			var action = "${pageContext.request.contextPath}/pg/ValutazioneProdotti.do?"+csrfToken+"&id=" + id + "&tipo=" + tipo;
			document.location.href=action;
		}
	
	</gene:javaScript>	
</gene:template>

</div>

