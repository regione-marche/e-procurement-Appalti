
<%
	/*
	 * Created on 22-11-2011
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty RISULTATO and RISULTATO eq "OK"}' >
		<script type="text/javascript">
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			var jspPath = window.opener.document.forms[0].jspPath.value;
			jspPath+="?AGGIORNAMENTO=OK"; //Serve ad indicare che quando si è giunti a tale popup l'eventuale calcolo del numero ordine degli operatori è stato gia' eseguito
			window.opener.document.forms[0].jspPath.value=jspPath;
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
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
     <c:when test='${not empty param.isGaraLottiConOffertaUnica}'>
             <c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}"  />
     </c:when>
	<c:otherwise>
		<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
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
     <c:when test='${not empty param.criterioRotazione}'>
             <c:set var="criterioRotazione" value="${param.criterioRotazione}"  />
     </c:when>
	<c:otherwise>
		<c:set var="criterioRotazione" value="${criterioRotazione}" />
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
     <c:when test='${not empty param.stazioneAppaltante}'>
             <c:set var="stazioneAppaltante" value="${param.stazioneAppaltante}"  />
     </c:when>
	<c:otherwise>
		<c:set var="stazioneAppaltante" value="${stazioneAppaltante}" />
	</c:otherwise>
</c:choose>	

<c:choose>
     <c:when test='${not empty param.stazioneAppaltante}'>
             <c:set var="stazioneAppaltante" value="${param.stazioneAppaltante}"  />
     </c:when>
	<c:otherwise>
		<c:set var="stazioneAppaltante" value="${stazioneAppaltante}" />
	</c:otherwise>
</c:choose>	

<c:choose>
     <c:when test='${not empty param.elencoIdFiltriSpecificiObbl}'>
             <c:set var="elencoIdFiltriSpecificiObbl" value="${param.elencoIdFiltriSpecificiObbl}"  />
     </c:when>
	<c:otherwise>
		<c:set var="elencoIdFiltriSpecificiObbl" value="${elencoIdFiltriSpecificiObbl}" />
	</c:otherwise>
</c:choose>	



<c:choose>
     <c:when test='${tipoGara eq 1}'>
        <c:set var="whereTipoEle" value="AND (TIPOELE IS NULL OR TIPOELE IN ('100','110','101','111'))"  />
     </c:when>
     <c:when test='${tipoGara eq 2}'>
        <c:set var="whereTipoEle" value="AND (TIPOELE IS NULL OR TIPOELE IN ('10','110','11','111'))"  />
     </c:when>
     <c:when test='${tipoGara eq 3}'>
        <c:set var="whereTipoEle" value="AND (TIPOELE IS NULL OR TIPOELE IN ('1','11','101','111'))"  />
     </c:when>
</c:choose>


<c:set var="garaEle" value="%|${garaElenco}|%"  />
<c:set var="where" value="TIPOFILTRO = 1 AND (FILTROATT='1' OR FILTROMAN='1') AND (APPLICAELE IS NULL OR APPLICAELE LIKE '${garaEle}') ${whereTipoEle}" scope="request" />

<c:set var="modo" value="MODIFICA" scope="request" />

<gene:template file="popup-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Imposta filtro operatori su ulteriori criteri" />
	<gene:setString name="entita" value="${entita}" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
		<br>
		
		<table class="lista">
			<tr>
				<td>
					<table class="arealayout">
						<tr>
							<td>
								Selezionare uno o più criteri con cui filtrare gli operatori dell'elenco.
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td><gene:formLista entita="G1FILTRIELE" tableclass="datilista" gestisciProtezioni="true" sortColumn="2" where="${where}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupFiltroSpecificoOp" pagesize="0">
					
					<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"	width="10">												 
						<c:if test="${currentRow >= 0}">
							<input type="checkbox" name="keys" id="keys-${currentRow}" value="${datiRiga.G1FILTRIELE_ID};${currentRow}" />
						</c:if>
					</gene:campoLista>
					<gene:campoLista campo="ID"  visibile="false"/>
					<gene:campoLista campo="TITOLO" title="Titolo" />
					<gene:campoLista campo="DESCRIZIONE" title="Descrizione" />
					
			
					<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
                    <input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
                    <input type="hidden" name="tipoGara" id="tipoGara" value="${tipoGara }" />
                    <input type="hidden" name="criterioRotazione" id="criterioRotazione" value="${criterioRotazione }" />
                    <input type="hidden" name="garaElenco" id="garaElenco" value="${garaElenco }" />
                    <input type="hidden" name="stazioneAppaltante" id="stazioneAppaltante" value="${stazioneAppaltante }" />
                </gene:formLista></td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<INPUT type="button"  class="bottone-azione" value='Imposta filtro' title='Imposta filtro' onclick="javascript:aggiungiFiltro();">&nbsp;&nbsp;&nbsp;
					<INPUT type="button"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">
								
				</td>
			</tr>			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		
				
		function aggiungiFiltro(){
			var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
	  		if (numeroOggetti == 0 ) {
	  			if(confirm("Attenzione! Senza alcuna selezione verranno eliminati\ntutti i filtri su criteri ulteriori. Continuare?")){
	  		  		listaConferma();
	  			}
	      	} else if(numeroOggetti > 10){
	      		 alert("Non è possibile selezionare più di 10 filtri nella lista");
	      	} else {
	      		listaConferma();
 			}
		}
		
		
		function chiudi(){
			window.close();
		}
		
		//Prelevo dalla variabile di sessione "elencoIdFiltriSpecifici"
		//la lista dei filtri selezionati in precedenza e 
		//imposto i corrispondenti check sulla lista
		function inizializzaLista(){
			var elencoIdFiltriSpecifici = "${elencoIdFiltriSpecifici}";
			var elencoIdFiltriSpecObbl = "${elencoIdFiltriSpecificiObbl}";
			
			var numeroFiltriSpecifici = ${currentRow}+1;
			
			if(elencoIdFiltriSpecifici!=null && elencoIdFiltriSpecifici!=""){
				var vetIdFiltriSpecificiSelezionati = elencoIdFiltriSpecifici.split(',');
								
				for(var t=0; t < numeroFiltriSpecifici; t++){
					var check = document.getElementById("keys-" + t).value;
					var vetValoriCheck = check.split(';');
					var codiceCheck = vetValoriCheck[0];
					
						for(var j=0;j<vetIdFiltriSpecificiSelezionati.length;j++){
							if(codiceCheck == vetIdFiltriSpecificiSelezionati[j]){
								document.getElementById("keys-" + t).checked = "checked";
								break;
							}	
						}
					 
				}
			}
			
			if(elencoIdFiltriSpecObbl!=null && elencoIdFiltriSpecObbl!=""){
				var vetIdFiltriSpecObbl = elencoIdFiltriSpecObbl.split(',');
								
				for(var t=0; t < numeroFiltriSpecifici; t++){
					var check = document.getElementById("keys-" + t).value;
					var vetValoriCheck = check.split(';');
					var codiceCheck = vetValoriCheck[0];
					
						for(var j=0;j<vetIdFiltriSpecObbl.length;j++){
							if(codiceCheck == vetIdFiltriSpecObbl[j]){
								document.getElementById("keys-" + t).checked = "checked";
								document.getElementById("keys-" + t).disabled = true;
								break;
							}	
						}
					 
				}
			}
			
		}
		
		function deselezionaTutti(achkArrayCheckBox) {
	
			if (achkArrayCheckBox) {
			  var arrayLen = "" + achkArrayCheckBox.length;
			  if(arrayLen != 'undefined') {
				for (i = 0; i < achkArrayCheckBox.length; i++) {
					if(achkArrayCheckBox[i].disabled==false){
						achkArrayCheckBox[i].checked = false;	
					}
				}
			  } else {
				if (achkArrayCheckBox)
				  achkArrayCheckBox.checked = false;
			  }	    
			}
		}
		
		
		inizializzaLista();
		
	    
 		
	</gene:javaScript>
</gene:template>
</c:otherwise>
</c:choose>