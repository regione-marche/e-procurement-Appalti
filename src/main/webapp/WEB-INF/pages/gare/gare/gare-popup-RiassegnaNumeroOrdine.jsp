<%
/*
 * Created on: 02-12-2011
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
		Finestra per la riassegnazione del numero d'ordine 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.riassegnamentoEseguito and requestScope.riassegnamentoEseguito eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<jsp:include page="./fasiRicezione/defStepWizardFasiRicezione.jsp" />

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.WIZARD_PAGINA_ATTIVA}'>
		<c:set var="WIZARD_PAGINA_ATTIVA" value="${param.WIZARD_PAGINA_ATTIVA}" />
	</c:when>
	<c:otherwise>
		<c:set var="WIZARD_PAGINA_ATTIVA" value="${WIZARD_PAGINA_ATTIVA}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.procNegaziata}'>
		<c:set var="procNegaziata" value="${param.procNegaziata}" />
	</c:when>
	<c:otherwise>
		<c:set var="procNegaziata" value="${procNegaziata}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.isProceduraAggiudicazioneAperta}'>
		<c:set var="isProceduraAggiudicazioneAperta" value="${param.isProceduraAggiudicazioneAperta}" />
	</c:when>
	<c:otherwise>
		<c:set var="isProceduraAggiudicazioneAperta" value="${isProceduraAggiudicazioneAperta}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.isGaraLottiConOffertaUnica}'>
		<c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
	</c:when>
	<c:otherwise>
		<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.isDitteConcorrenti}'>
		<c:set var="isDitteConcorrenti" value="${param.isDitteConcorrenti}" />
	</c:when>
	<c:otherwise>
		<c:set var="isDitteConcorrenti" value="${isDitteConcorrenti}" />
	</c:otherwise>
</c:choose>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value="Riassegna numero d'ordine" />

<c:if test='${WIZARD_PAGINA_ATTIVA eq step5Wizard}'>
	<c:set var="plichi" value="plichi" />
</c:if>
<gene:setString name="titoloMaschera" value="Riassegna numero d'ordine ${plichi}" />
	
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="DITG" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreRiassegnaNumOrdine">
	
	<table class="dettaglio-notab">
		<tr>
		 	<td>
		 		<br>
		 		Mediante questa funzione è possibile riassegnare il numero d'ordine ${plichi} delle ditte in gara secondo un opportuno criterio di ordinamento.
		 	    <br>
		 	    <br>
	 			<p><b>Impostare il criterio di ordinamento:</b>
			 		<br>
					&nbsp;<input type="radio" name="modalitaRiassegnamento" value="1" onclick="javascript:aggiornaModalitaRiassegnamento(1);" CHECKED/>&nbsp;Ragione sociale&nbsp;&nbsp; 
					<br>
					<c:if test='${WIZARD_PAGINA_ATTIVA eq step1Wizard and not procNegaziata and not isDitteConcorrenti}'>
						&nbsp;<input type="radio" name="modalitaRiassegnamento" value="2" onclick="javascript:aggiornaModalitaRiassegnamento(2);"/>&nbsp;Data e ora presentazione domanda di partecipazione
						<br>
					</c:if>
					<c:if test='${WIZARD_PAGINA_ATTIVA eq step1Wizard and not procNegaziata and not isDitteConcorrenti}'>
						&nbsp;<input type="radio" name="modalitaRiassegnamento" value="3" onclick="javascript:aggiornaModalitaRiassegnamento(3);"/>&nbsp;Numero protocollo presentazione domanda di partecipazione
						<br>
					</c:if>
					<c:if test='${WIZARD_PAGINA_ATTIVA eq step5Wizard and not isDitteConcorrenti}'>
						&nbsp;<input type="radio" name="modalitaRiassegnamento" value="4" onclick="javascript:aggiornaModalitaRiassegnamento(4);"/>&nbsp;Data e ora presentazione offerta
						<br>
					</c:if>
					<c:if test='${WIZARD_PAGINA_ATTIVA eq step5Wizard and not isDitteConcorrenti}'>
						&nbsp;<input type="radio" name="modalitaRiassegnamento" value="5" onclick="javascript:aggiornaModalitaRiassegnamento(5);"/>&nbsp;Numero protocollo presentazione offerta
						<br> 
					</c:if> 
			 	</p>
		 	    <br>
			</td>
		</tr>
		<input type="hidden" name="ngara" id="ngara" value="${ngara}">
		<input type="hidden" name="modalitaRiass" id="modalitaRiass" value="">
		<input type="hidden" name="WIZARD_PAGINA_ATTIVA" id="WIZARD_PAGINA_ATTIVA" value="${WIZARD_PAGINA_ATTIVA}">
		<input type="hidden" name="procNegaziata" id="procNegaziata" value="${procNegaziata}">
		<input type="hidden" name="isProceduraAggiudicazioneAperta" id="isProceduraAggiudicazioneAperta" value="${isProceduraAggiudicazioneAperta}">
		<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}">
		<input type="hidden" name="isDitteConcorrenti" id="isDitteConcorrenti" value="${isDitteConcorrenti}">
	</table>
	
		
		
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/gare/gare-popup-RiassegnaNumeroOrdine.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
		function aggiornaModalitaRiassegnamento(value){
			setValue("modalitaRiass", value);
			
		}
		
		setValue("modalitaRiass", 1);
		
		
	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>