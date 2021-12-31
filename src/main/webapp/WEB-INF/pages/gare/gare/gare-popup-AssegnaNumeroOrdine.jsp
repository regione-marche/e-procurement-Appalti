<%
/*
 * Created on: 51-09-2010
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
		Finestra per l'assegnazione del numero d'ordine 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.numeroAssegnato and requestScope.numeroAssegnato eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<c:set var="ngara" value="${param.ngara}" />
<c:set var="tipoalgo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTIPOALGOFunction", pageContext, ngara)}' />
<c:set var="aggnumord" value='${param.aggnumord}' scope="request"/>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>
	
<gene:setString name="titoloMaschera" value="Assegna numero d'ordine" />
<c:if test="${!empty aggnumord and aggnumord eq 1}">
	<gene:setString name="titoloMaschera" value="Attiva operatori abilitati" />
</c:if>

	
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="DITG" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAssegnaNumOrdine">
	
	<table class="dettaglio-notab">
		<tr>
		 	<td>
		 		<br>
		 		<c:choose>
		 			<c:when test="${!empty aggnumord and aggnumord eq 1}">
				 		Mediante questa funzione è possibile attivare gli operatori che sono stati abilitati all'elenco.
				 		<c:if test="${aggnumord eq 1 }">
				 			<br/>Confermi l'operazione?<br/>
				 		</c:if>
		 			</c:when>
		 			<c:otherwise>
				 		<b>Mediante questa funzione è possibile assegnare il numero d'ordine agli operatori.</b>
		 			</c:otherwise>
		 		</c:choose>
		 	    <br>
		 	    <br>
	 			<span id="msgSelezione">
	 			<p><b>Impostare la modalità di assegnamento:</b>
			 		<br>
					&nbsp;<input type="radio" name="modalitaAssegnamento" value="1" onclick="javascript:aggiornaModalitaAssegnamento(1);"/>&nbsp;Assegnare il numero d'ordine in modalità casuale&nbsp;&nbsp; 
					<span style="float: right;">
					<a href="javascript:apriDocumento('${pageContext.request.contextPath}/doc/algoritmo_estrazione_casuale.pdf');" title="Consulta manuale" style="color:#002E82;">
						<img width="16" height="16" title="Consulta manuale" alt="Consulta manuale" src="${pageContext.request.contextPath}/img/consultazioneManuale.png"/> Consulta manuale
					</a>
					</span>
					<br>
					&nbsp;<input type="radio" name="modalitaAssegnamento" value="2" onclick="javascript:aggiornaModalitaAssegnamento(2);"/>&nbsp;Assegnare il numero d'ordine in base alla data e ora di arrivo delle domande di iscrizione
					<br>
					&nbsp;<input type="radio" name="modalitaAssegnamento" value="4" onclick="javascript:aggiornaModalitaAssegnamento(4);"/>&nbsp;Assegnare il numero d'ordine in base alla data di abilitazione
					<br>
					&nbsp;<input type="radio" name="modalitaAssegnamento" value="3" onclick="javascript:aggiornaModalitaAssegnamento(3);"/>&nbsp;Annullare il numero d'ordine per assegnarlo manualmente
					<br>
					 
			 	</p>
			
		 	    <p><b>Impostare il campo di applicazione:</b>
			 		<br>
					&nbsp;<input type="radio" name="campoApplicazione" value="1" onclick="javascript:aggiornaCampoApplicazione(1);"checked="checked"/>&nbsp;Solo per i nuovi iscritti
					<br>
					&nbsp;<input type="radio" name="campoApplicazione" value="2" onclick="javascript:aggiornaCampoApplicazione(2);"/>&nbsp;Per tutti gli operatori
					<br>
					
			 	<br>
			 	<br>
			 	</p>
			 	</span>
			</td>
		</tr>
		<input type="hidden" name="ngara" id="ngara" value="${ngara}">
		<input type="hidden" name="modalitaAss" id="modalitaAss" value="">
		<input type="hidden" name="campoApp" id="campoApp" value="">
		<input type="hidden" name="aggnumord" id="aggnumord" value="${aggnumord }">
	</table>
	
		
		
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/gare/gare-popup-AssegnaNumeroOrdine.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
		function aggiornaModalitaAssegnamento(value){
			setValue("modalitaAss", value);
			
		}
	
		function aggiornaCampoApplicazione(value){
			setValue("campoApp", value);
		}
		
		function inizializzaChech(){
			var tipoalgo=${tipoalgo };
			var aggnumord = "${aggnumord}";
			var modalitaAssegnamento = document.forms[0].modalitaAssegnamento;
			if (modalitaAssegnamento != null) {
				if(tipoalgo!=2 && aggnumord!='1'){
					modalitaAssegnamento[1].checked= true;
					document.forms[0].modalitaAss.value=2;
				}else{
					modalitaAssegnamento[0].checked =true;
					document.forms[0].modalitaAss.value=1;
				}	
			}
			
			var campoApplicazione = document.forms[0].campoApplicazione;
			if(campoApplicazione!=null){
				if(tipoalgo==2 || aggnumord=='1'){
					campoApplicazione[1].checked= true;
					document.forms[0].campoApp.value=2;
				}else{
					campoApplicazione[0].checked =true;
					document.forms[0].campoApp.value=1;
				}
			}
		}
		
		inizializzaChech();
		
		<c:if test="${aggnumord eq 1 }">
			$("#msgSelezione").hide();
		</c:if>
	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>