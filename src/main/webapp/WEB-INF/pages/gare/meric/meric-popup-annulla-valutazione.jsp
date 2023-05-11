
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

	<c:set var="id" value="${param.id}" />

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:setString name="titoloMaschera"  value='Annulla valutazione prodotti' />
	
	<c:set var="isRicercaMercatoGeneratoOrdineFunction" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsRicercaMercatoGeneratoOrdineFunction", pageContext, id)}' />
	<c:set var="isRicercaMercatoSelezionatoBozzaFunction" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsRicercaMercatoSelezionatoBozzaFunction", pageContext, id)}' />
	
	<gene:redefineInsert name="corpo">
		<table class="dettaglio-notab">
			<td class="valore-dato" colspan="2">
				<br>
				<c:choose>
					<c:when test="${isRicercaMercatoGeneratoOrdineFunction == 'true'}">
						Non &egrave; possibile annullare la valutazione prodotti in quanto risultano generati degli ordini.
					</c:when>
					<c:otherwise>
						Procedere con l'annullamento della valutazione prodotti ?
						<c:if test="${isRicercaMercatoSelezionatoBozzaFunction == 'true'}">
							<br>
							<br>
							Attenzione! Alcuni prodotti sono stati selezionati nella bozza ordine.
						</c:if>					
					</c:otherwise>
				</c:choose>
				<br>
				<br>
			</td>

			<tr>
				<td colspan="2" class="comandi-dettaglio">
					<c:if test="${isRicercaMercatoGeneratoOrdineFunction == 'false'}">
						<INPUT type="button" class="bottone-azione" value="Annulla valutazione prodotti" title="Annulla valutazione prodotti" onclick="javascript:annullaValutazioneProdotti(${id});">
					</c:if>		
					<INPUT type="button" class="bottone-azione" value="Chiudi"	title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	
	<gene:javaScript>
		
		function annulla(){
			window.close();
		}
		
		function annullaValutazioneProdotti(id){
			bloccaRichiesteServer();
			var action = "${pageContext.request.contextPath}/pg/AnnullaValutazioneProdotti.do?"+csrfToken+"&id=" + id;
			document.location.href=action;
		}
	
	</gene:javaScript>	
</gene:template>

</div>

