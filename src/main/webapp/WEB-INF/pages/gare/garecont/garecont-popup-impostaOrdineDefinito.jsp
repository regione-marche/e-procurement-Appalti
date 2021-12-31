<%
/*
 * Created on: 17-06-2014
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
		Finestra per l'attivazione della funzione 'Pubblica su portale Alice Gare'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${!empty requestScope.operazioneEseguita and requestScope.operazioneEseguita eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.ncont}'>
		<c:set var="ncont" value="${param.ncont}" />
	</c:when>
	<c:otherwise>
		<c:set var="ncont" value="${ncont}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${!empty param.eseguireControlliPreliminari}'>
		<c:set var="eseguireControlliPreliminari" value="${param.eseguireControlliPreliminari}" />
	</c:when>
	<c:otherwise>
		<c:set var="eseguireControlliPreliminari" value="${eseguireControlliPreliminari}" />
	</c:otherwise>
</c:choose>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Imposta ordine definito' />

<c:set var="msgControlliCampiObbligatori" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.ControlliCampiObbligatoriOrdineDefinitoFunction", pageContext, ngara)}' />

<c:if test="${eseguireControlliPreliminari ne 'no' and !erroriControlloGare}">
	<c:set var="controlli" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.OrdineDefinitoDisallineamentoFunction", pageContext, ngara)}' />
</c:if>

<c:if test="${numeroErrori > 0 || numeroWarning > 0}">
	<c:set var="ctr" value="Risultano fatti degli aggiornamenti ai prodotti in catalogo oggetto della valutazione oppure ne è variata la disponibilità (ci sono prodotti non più disponibili o nuovi prodotti inseriti).&#10;" />
	<c:set var="ctr" value="${ctr}&#10;Numero di variazioni relative ai prodotti dell'ordine selezionato: ${numeroErrori}&#10;" />
	<c:set var="ctr" value="${ctr}Numero di variazioni sugli altri prodotti: ${numeroWarning}&#10;" />
	<c:forEach items="${listaControlloArticoli}" step="1" var="controlloArticolo" varStatus="statusArticolo" >
		<c:set var="ctr" value="${ctr}&#10;Articolo: ${controlloArticolo[0]}&#10;" />
		<c:forEach items="${controlloArticolo[1]}" step="1" var="controlloProdotto" varStatus="statusProdotto"> 
			<c:choose>
				<c:when test="${controlloProdotto[0] eq 'E'}">
					<c:set var="ctr" value="${ctr}- [Prodotto nell'ordine] Prodotto ${controlloProdotto[1]}: ${controlloProdotto[2]}&#10;" />
				</c:when>
				<c:otherwise>
					<c:set var="ctr" value="${ctr}- Prodotto ${controlloProdotto[1]}: ${controlloProdotto[2]}&#10;" />
				</c:otherwise>
			</c:choose>
		</c:forEach>
		<c:forEach items="${controlloArticolo[2]}" step="1" var="controlloOperatore" varStatus="statusOperatore"> 
			<c:choose>
				<c:when test="${controlloProdotto[0] eq 'E'}">
					<c:set var="ctr" value="${ctr}- [Prodotto nell'ordine] Prodotto ${controlloOperatore[1]}: ${controlloOperatore[2]}&#10;" />
				</c:when>
				<c:otherwise>
					<c:set var="ctr" value="${ctr}- Prodotto ${controlloOperatore[1]}: ${controlloOperatore[2]}&#10;" />
				</c:otherwise>
			</c:choose>
		</c:forEach>
		<c:if test="${!empty controlloArticolo[3]}">
			<c:set var="ctr" value="${ctr}Nuovi prodotti disponibili:&#10;" />
			<c:forEach items="${controlloArticolo[3]}" step="1" var="nuovoProdotto" varStatus="statusNuovoProdotto"> 
				<c:set var="ctr" value="${ctr}- ${nuovoProdotto[0]}&#10;" />
			</c:forEach>
		</c:if>	
	</c:forEach>
</c:if>

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARECONT" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupImpostaOrdineDefinito">
	
	<table class="dettaglio-notab">
	
		<c:if test="${numeroErrori > 0 || numeroWarning > 0 }">
			<tr>
				<td>
					<textarea rows="15" cols="70" readonly="readonly">${ctr}</textarea>		
				</td>
			</tr>	
		</c:if>
	
		<tr>
		 	<td>
		 		<br>
		 			<c:choose>
			 			<c:when test="${empty requestScope.creazioneEseguita && numeroErrori > 0}">
			 				Non &egrave; possibile procedere in quanto ci sono variazioni relative ai prodotti dell'ordine.
			 				<br>Per impostare a definito l'ordine &egrave; necessario prima eliminare tutti gli ordini generati, 
			 				se non ancora in stato 'Definito', aggiornare il prospetto della valutazione prodotti (mediante
			 				la funzione 'Aggiorna dati prodotti' nella pagina 'Valutazione prodotti') e prenderne visione.
		 					<br> 
			 			</c:when>
			 			<c:when test="${empty requestScope.creazioneEseguita && erroriControlloGare}">
			 				${msgControlliCampiObbligatori }
			 			</c:when>
			 			<c:otherwise>
							<c:choose>
								<c:when test="${numeroWarning > 0}">
									Poich&egrave; le variazioni riscontrate non riguardano direttamente i prodotti dell'ordine &egrave; possibile 
									procedere con l'impostazione dell'ordine a 'Definito'.
									<br>
								</c:when> 
							</c:choose>
							<br>Si vuole procedere con l'impostazione dell'ordine a 'Definito'?			 			
			 			</c:otherwise>
					</c:choose>			 		
			 	<br>
				<br>
			</td>
		</tr>
		<input type="hidden" name="ngara" id="ngara" value="${ngara}">
		<input type="hidden" name="ncont" id="ncont" value="${ncont}">
		<input type="hidden" name="eseguireControlliPreliminari" id="eseguireControlliPreliminari" value="${eseguireControlliPreliminari}">
	</table>
	
		
		
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${(!empty requestScope.operazioneErrore and requestScope.operazioneErrore eq "1") or (numeroErrori > 0) or erroriControlloGare}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	</gene:redefineInsert>
</c:if>	
		
	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/garecont/garecont-popup-impostaOrdineDefinito.jsp";
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