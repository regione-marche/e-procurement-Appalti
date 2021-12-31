<%
/*
 * Created on: 23/05/2018
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
		Finestra che visualizza la conferma per l'inserimento delle pubblicazioni bando/esito predefinite
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>



<div style="width:97%;">

<gene:template file="popup-template.jsp">
	
<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.garaElenco}'>
		<c:set var="garaElenco" value="${param.garaElenco}"  />
	</c:when>
	<c:otherwise>
		<c:set var="garaElenco" value="${garaElenco}" />
	</c:otherwise>
</c:choose>

<c:choose>
     <c:when test='${not empty param.categoriaPrev}'>
             <c:set var="categoriaPrev" value="${param.categoriaPrev}"  />
     </c:when>
	<c:otherwise>
		<c:set var="categoriaPrev" value="${categoriaPrev}" />
	</c:otherwise>
</c:choose>

<c:choose>
     <c:when test='${not empty param.classifica}'>
             <c:set var="classifica" value="${param.classifica}"  />
     </c:when>
	<c:otherwise>
		<c:set var="classifica" value="${classifica}" />
	</c:otherwise>
</c:choose>

<c:choose>
     <c:when test='${not empty param.tipoGara}'>
             <c:set var="tipoGara" value="${param.tipoGara}"  />
     </c:when>
	<c:otherwise>
		<c:set var="tipoGara" value="${tipoGara}" />
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


	<gene:setString name="titoloMaschera" value='Selezione affidatario uscente da anagrafica' />
		
	<gene:redefineInsert name="corpo">
	
		
		<br>
		Specificare l'operatore 'affidatario uscente' che deve essere escluso dalla selezione da elenco.
		<c:if test="${not empty elencoAffidatariEsclusi}">
		<br>Per escludere piu' operatori, e' possibile aggiungere l'operatore selezionato al filtro impostato in precedenza (pulsante 'Aggiungi a filtro impostato'), anzichè sostituirlo (pulsante 'Imposta filtro').  
		</c:if>
		<br>
	
	
		<table class="lista" id ="listaAggiudicatari" style="display:visibile">
			<tr>
				<td>
	
				<gene:formLista entita="IMPR" pagesize="20" sortColumn="2" tableclass="datilista" gestisciProtezioni="true" >

					<gene:campoLista title="Scegli" width="40">
						<c:if test="${currentRow >= 0 }">
							<input type="radio" value="${datiRiga.IMPR_CODIMP}" name="keyDittaEsclusa" id="keys${currentRow}"  onclick="javascript:impostaDittaEsclusa('${datiRiga.IMPR_CODIMP}');"  />
						</c:if>
							
					</gene:campoLista>
					<gene:campoLista campo="CODIMP"/>
					<gene:campoLista campo="NOMEST"/>
					<gene:campoLista campo="CFIMP"/>
					<gene:campoLista campo="PIVIMP"/>
					
					<input type="hidden" name="garaElenco" value="${garaElenco}" />
					<input type="hidden" name="tipoGara" value="${tipoGara}" />
					<input type="hidden" name="where" id="where" value="${where}" />
					<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		            <input type="hidden" name="categoriaPrev" id="categoriaPrev" value="${categoriaPrev}" />
		            <input type="hidden" name="classifica" id="classifica" value="${classifica}" />
					<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
					<input type="hidden" name="dittaEsclusa" id="dittaEsclusa" value="" />
					<input type="hidden" name="elencoAffidatariEsclusi" id="elencoAffidatariEsclusi" value="${elencoAffidatariEsclusi}" />
			
					</gene:formLista>

			</tr>
		  
			<tr>
					<td class="comandi-dettaglio" colspan="2">
						<INPUT type="button" class="bottone-azione" id="btn_conferma" value="Imposta filtro" title="Imposta filtro" onclick="javascript:creaRigeneraFiltro()">&nbsp;
						<INPUT type="button" class="bottone-azione" id="btn_integra" value="Aggiungi a filtro impostato" title="Aggiungi a filtro impostato" onclick="javascript:integraFiltro()">&nbsp;
						<INPUT type="hidden" class="bottone-azione" value="Torna alla ricerca" title="Torna alla ricerca" onclick="javascript:historyVaiIndietroDi(1);" >&nbsp;
						<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi()">&nbsp;
				   </td>
			 </tr>
		</table>


			

  	</gene:redefineInsert>

	<gene:javaScript>
	
	
		function creaRigeneraFiltro(){
			var dittaEsclusa = $("#dittaEsclusa").val();
			if (dittaEsclusa==null || dittaEsclusa=="") {
	      		alert("Selezionare almeno una ditta dalla lista");
	      	} else {
	      		document.forms[0].action="${pageContext.request.contextPath}/pg/EscludiAggiudicatarioUscente.do?"+csrfToken+"&creaRigeneraFiltro=true"
 				bloccaRichiesteServer();
				document.forms[0].submit();
 			}
		}

		function integraFiltro(){
			var dittaEsclusa = $("#dittaEsclusa").val();
			var sceltaRipetuta = "false";
			if (dittaEsclusa==null || dittaEsclusa=="") {
	      		alert("Selezionare almeno una ditta dalla lista");
	      	} else {
				var elencoAffidatariEsclusi = "${elencoAffidatariEsclusi}";
	      		var vetAffidatariEsclusiSelezionati = elencoAffidatariEsclusi.split(',');
	      		for(var t=0; t < vetAffidatariEsclusiSelezionati.length; t++){
	      			if(dittaEsclusa == vetAffidatariEsclusiSelezionati[t]){
	      				sceltaRipetuta = "true";
						break;
					}	
				}
	      		if(sceltaRipetuta == "true"){
	      			alert("La ditta risulta già selezionata");
	      		}else{
		      		document.forms[0].action="${pageContext.request.contextPath}/pg/EscludiAggiudicatarioUscente.do?"+csrfToken+"&creaRigeneraFiltro=false"
	 				bloccaRichiesteServer();
					document.forms[0].submit();
	      		}
 			}
		}

		function impostaDittaEsclusa(codice){
			$("#dittaEsclusa").val(codice);
		}
		
		function chiudi(){
			window.close();
		}
		
		
		//Prelevo dalla variabile di sessione "elencoAffidatariEsclusi"
		//la lista dei filtri selezionati in precedenza e 
		//imposto i corrispondenti check sulla lista
		function inizializzaLista(){
			var elencoAffidatariEsclusi = "${elencoAffidatariEsclusi}";
			$("#btn_integra").hide();
			if(elencoAffidatariEsclusi!=null && elencoAffidatariEsclusi!=""){
				$("#btn_integra").show();
			}
		}
		
		inizializzaLista();
		
	</gene:javaScript>
	
	</gene:template>
</div>
