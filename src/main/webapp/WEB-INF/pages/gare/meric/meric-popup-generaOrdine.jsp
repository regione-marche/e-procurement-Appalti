<%
/*
 * Created on: 05-06-2014
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
		Finestra per la generazione di un ordine per la ricerca di mercato 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty param.id}'>
		<c:set var="id" value="${param.id}" />
	</c:when>
	<c:otherwise>
		<c:set var="id" value="${id}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.ditta}'>
		<c:set var="ditta" value="${param.ditta}" />
	</c:when>
	<c:otherwise>
		<c:set var="ditta" value="${ditta}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.controlloImporto}'>
		<c:set var="controlloImporto" value="${param.controlloImporto}" />
	</c:when>
	<c:otherwise>
		<c:set var="controlloImporto" value="${controlloImporto}" />
	</c:otherwise>
</c:choose>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value="Genera ordine di acquisto" />

<c:if test="${empty requestScope.creazioneEseguita}">
	<c:set var="controlli" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.RicercaMercatoDisallineamentoFunction", pageContext, id, ditta)}' />
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
</c:if>

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGeneraOrdineAcquisto">
	
	<table class="dettaglio-notab">
	
		<c:if test="${empty requestScope.creazioneEseguita}">
			<c:if test="${numeroErrori > 0 || numeroWarning > 0}">
				<tr>
					<td>
						<textarea id="ctrdistextarea" rows="15" cols="70" readonly="readonly">${ctr}</textarea>			
					</td>
				</tr>	
			</c:if>
			<c:if test="${!(numeroErrori > 0) && controlloImporto eq 'Si'}">
				<c:set var="controlloImportoSuperato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlloImportoOrdineMinimoFunction", pageContext, id,ditta)}'/>
				<c:if test='${controlloImportoSuperato ne "true"}'>
					<tr>
						<td>
							<span id="messaggioControlloImporto" >
							Sono state riscontrate delle anomalie nella verifica che l'ordine rispetti le soglie di importo minimo stabilite da catalogo.
							<br>La verifica consiste nel confrontare l'importo ordine minimo attribuito a categorie in catalogo 
							con l'importo totale dei prodotti dell'ordine che fanno riferimento a queste categorie.
							<br> 
							<br>
							Di seguito vengono elencati i casi in cui l'importo dell'ordine non raggiunge 
							la soglia indicata in catalogo:
							<br>
							<br>&nbsp;
							</span>
							<textarea id="ctrimptextarea" rows="15" cols="70" readonly="readonly">${requestScope.messaggi}</textarea>			
						</td>
					</tr>	
				</c:if>
			</c:if>
		</c:if>
	
		<tr>
		 	<td>
		 		<br>
		 		<c:choose>
		 			<c:when test="${empty requestScope.creazioneEseguita && numeroErrori > 0}">
		 				Non &egrave; possibile procedere in quanto ci sono variazioni relative ai prodotti dell'ordine.
		 				<br>Per poter generare l'ordine &egrave; necessario prima aggiornare il prospetto della valutazione prodotti 
		 				(mediante la funzione 'Aggiorna dati prodotti' nella pagina 'Valutazione prodotti') e prenderne visione.
		 				<br> 
		 			</c:when>
					<c:when test='${empty requestScope.creazioneEseguita}' >
						<c:choose>
							<c:when test="${numeroWarning > 0}">
								<span id="messaggioDisallineamento">
								Poich&egrave; le variazioni riscontrate non riguardano direttamente i prodotti dell'ordine &egrave; possibile 
								procedere alla generazione dell'ordine di acquisto.
								<br>In alternativa aggiornare il prospetto della valutazione prodotti 
								(mediante la funzione 'Aggiorna dati prodotti' nella pagina 'Valutazione prodotti')
								e prenderne visione.
								<br>
								</span>
							</c:when> 
						</c:choose>
				 	    <c:choose>
							<c:when test='${not empty requestScope.NoCodificaAutomatica and requestScope.NoCodificaAutomatica eq "1"}' >
								<br><br><b>Non &egrave; possibile procedere poich&egrave; non è attiva la codifica automatica per gli ordini di acquisto.</b>
							</c:when>
							<c:when test='${empty requestScope.Errore}' >
						 	    <br>Si vuole procedere alla generazione dell'ordine?
							</c:when>
						</c:choose>
					</c:when>
					<c:otherwise>
						Generazione dell'ordine di acquisto conclusa con successo
					</c:otherwise>
				</c:choose>
			 	<br>
				<br>
			</td>
		</tr>
		<input type="hidden" name="id" id="id" value="${id}">
		<input type="hidden" name="ditta" id="ditta" value="${ditta}">
		<input type="hidden" name="controlloImporto" id="controlloImporto" value="${controlloImporto}">
	</table>
	
		
		
	</gene:formScheda>
  </gene:redefineInsert>
	
<c:if test='${(not empty requestScope.NoCodificaAutomatica and requestScope.NoCodificaAutomatica eq "1") or (not empty requestScope.Errore and requestScope.Errore eq "1") or (numeroErrori > 0)}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	</gene:redefineInsert>
</c:if>	
<c:if test='${not empty requestScope.creazioneEseguita and requestScope.creazioneEseguita eq "1"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi()">&nbsp;
	</gene:redefineInsert>
</c:if>	

	<gene:javaScript>
		var procedereConSubmit=true;
		
		function conferma() {
			if(procedereConSubmit){
				document.forms[0].jspPathTo.value="gare/meric/meric-popup-generaOrdine.jsp";
				schedaConferma();
			}else{
				procedereConSubmit=true;
				$("#ctrimptextarea").show();
				$("#messaggioControlloImporto").show();
				$("#ctrdistextarea").hide();
				$("#messaggioDisallineamento").hide();
			}
		}
		
		function annulla(){
			window.close();
		}
		
		function chiudi(){
			var ngara="${requestScope.codiceOrdine}";
			window.close();
			opener.dettaglioOrdine(ngara);
		}
		<c:if test="${!(numeroErrori > 0) && numeroWarning >0 && controlloImporto eq 'Si' && controlloImportoSuperato ne 'true'}">
			$("#ctrimptextarea").hide();
			$("#messaggioControlloImporto").hide();
			procedereConSubmit=false;
		</c:if>	
		
	</gene:javaScript>
</gene:template>
</div>

	