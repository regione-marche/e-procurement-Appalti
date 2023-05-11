<%
/*
 * Created on: 18-09-2020
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
		Popup per annulare ricezione domande di partecipazione o ricezione plichi
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.operazioneEseguita and requestScope.operazioneEseguita eq "1"}' >
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
	<c:when test='${!empty param.tipo}'>
		<c:set var="tipo" value="${param.tipo}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipo" value="${tipo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.iterga}'>
		<c:set var="iterga" value="${param.iterga}" />
	</c:when>
	<c:otherwise>
		<c:set var="iterga" value="${iterga}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.codgar}'>
		<c:set var="codgar" value="${param.codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="codgar" value="${codgar}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.genere}'>
		<c:set var="genere" value="${param.genere}" />
	</c:when>
	<c:otherwise>
		<c:set var="genere" value="${genere}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${tipo eq '1' }">
		<c:set var="msgTitolo" value ="ricezione offerte" />
	</c:when>
	<c:otherwise>
		<c:set var="msgTitolo" value ="ricezione domande di partecipazione" />
	</c:otherwise>
</c:choose>

<c:set var="esitoControllo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlliValiditaFunzAnnullamentoFunction", pageContext, ngara, tipo,genere )}' />

<gene:setString name="titoloMaschera" value='Annulla ${msgTitolo }' />


<c:set var="modo" value="NUOVO" scope="request" />
	
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAnnullaRicezionePlichiDomande">
	
		<gene:campoScheda>
			<td colspan=2>
			<br>
		<c:choose>
			<c:when test="${esitoControllo eq 'NOK'}">
				Operazione non disponibile. Verificare il possesso del privilegio di amministratore di sistema e la fase corrente della gara.
				<br><br>
			</c:when>
			<c:when test="${esitoControllo eq 'NO-BUSTE'}">
				<c:choose>
					<c:when test="${tipo eq '1' }">
						<c:set var="msgBusteNA" value ="Le offerte non sono ancora state acquisite" />
					</c:when>
					<c:otherwise>
						<c:set var="msgBusteNA" value ="Le domande di partecipazione non sono ancora state acquisite" />
					</c:otherwise>
				</c:choose>
				Operazione non disponibile. ${msgBusteNA } da portale Appalti per la gara ${ngara}.
				<br><br>
			</c:when>
			<c:when test="${esitoControllo eq 'BUSTE-AC'}">
				<c:choose>
					<c:when test="${tipo eq '1' }">
						<c:set var="msgBusteA" value ="all'apertura delle offerte dei singoli operatori" />
					</c:when>
					<c:otherwise>
						<c:set var="msgBusteA" value ="alla ricezione delle offerte" />
					</c:otherwise>
				</c:choose>
				Non &egrave; possibile annullare l'operazione di ${msgTitolo } da portale Appalti per la gara ${ngara} perch&egrave; si è già proceduto ${msgBusteA}.
				<br><br>
			</c:when>
			<c:otherwise>
				Mediante questa funzione &egrave; possibile annullare l'operazione di ${msgTitolo } da portale Appalti.
				<br> Per procedere compilare il codice della gara.<br>
				<br><br>
				Confermi l'operazione ?
				<br><br>
			</c:otherwise>
		</c:choose>
			<br>
			</td>
		</gene:campoScheda >
		
		<c:if test="${esitoControllo eq 'OK'}">
			<gene:campoScheda campo="GARA" title="Codice gara" obbligatorio="true" campoFittizio="true" definizione="T21;0"/>
		</c:if>
		
		
		<input type="hidden" name="tipo" value="${tipo}">
		<input type="hidden" name="ngara" value="${ngara}">
		<input type="hidden" name="iterga" value="${iterga}">
		<input type="hidden" name="codgar" value="${codgar}">
		<input type="hidden" name="genere" value="${genere}">
	</gene:formScheda>
		<c:if test="${esitoControllo ne 'OK' || requestScope.operazioneEseguita eq '2'}">
			<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			</gene:redefineInsert>
		</c:if>
  </gene:redefineInsert>

	<gene:javaScript>
		document.forms[0].jspPathTo.value="gare/commons/popupAnnullaRicezionePlichiDomande.jsp";
		
		function conferma() {
			var gara = getValue("GARA");
			if(gara==null || gara ==""){
				alert("Compilare il campo 'Codice gara' con il codice della gara corrente");
				return;
			}
			var ngara= "${ngara }";
			if (gara != ngara){
				alert("Il codice gara specificato non è quello della gara corrente");
				return;
			}
			
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