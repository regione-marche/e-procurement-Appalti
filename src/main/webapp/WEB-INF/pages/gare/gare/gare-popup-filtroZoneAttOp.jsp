
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

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetElencoRegioniFunction" />

<c:set var="modo" value="MODIFICA" scope="request" />

<gene:template file="popup-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Imposta filtro operatori su zone attivita" />
	<gene:setString name="entita" value="${entita}" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
		<br>
		
		<table id ="tabRegioni" class="dettaglio-notab">
			
			<tr>

			<td id="td_form"><gene:formScheda entita="G1FILTRIELE" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupFiltroZoneAttOp" >
			
				<gene:campoScheda campo="ID"  visibile="false"/>
			
				<gene:campoScheda >
					Selezionare le regioni in cui devono essere attivi gli operatori dell'elenco:
					<td class="valore-dato" id="td_attr"></td><br>
				</gene:campoScheda>

				<gene:campoScheda >
					<td id="td_span" colspan="1"/>
					<td id="td_chk" class="valore-dato">
						<c:forEach items="${listaRegioni}" step="1" var="valoriRegioni" varStatus="ciclo" >
							<input id="chk_${valoriRegioni[0]}" style="vertical-align: middle;"
							 type="checkbox" name="regioni" value="${valoriRegioni[0]}" onchange="javascript:selezioneSingola();"/>
							<span style="vertical-align: middle;">${valoriRegioni[1]}</span>
							<br>
						</c:forEach>
					<input style="vertical-align: middle;" type="checkbox" id="chk_allreg" name="allreg" value="all" onchange="javascript:selezioneMultipla();"/>
					<span style="vertical-align: middle;">Tutte le regioni</span>

					</td>
				</gene:campoScheda>

					<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
                    <input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
                    <input type="hidden" name="tipoGara" id="tipoGara" value="${tipoGara }" />
                    <input type="hidden" name="criterioRotazione" id="criterioRotazione" value="${criterioRotazione }" />
                    <input type="hidden" name="garaElenco" id="garaElenco" value="${garaElenco }" />
                    <input type="hidden" name="stazioneAppaltante" id="stazioneAppaltante" value="${stazioneAppaltante }" />
                    
                </gene:formScheda></td>
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
		
		$("#td_attr").css("border-bottom","#FFFFFF");
		$("#td_span").css("border-color","#FFFFFF");
		$("#td_chk").css("border-color","#FFFFFF");
		$("#td_all").css("border-color","#FFFFFF");

		
				
		function aggiungiFiltro(){
			document.forms[0].jspPathTo.value="gare/gare/gare-popup-filtroZoneAttOp.jsp";
			schedaConferma();
		}
		
		
		function chiudi(){
			window.close();
		}
		
		//Prelevo dalla variabile di sessione "elencoIdZoneAttivita"
		//la lista dei filtri selezionati in precedenza e 
		//imposto i corrispondenti check sulla lista
		function inizializzaLista(){
			var elencoIdZoneAttivita = "${elencoIdZoneAttivita}";
			if(elencoIdZoneAttivita!=null && elencoIdZoneAttivita!=""){
				if(elencoIdZoneAttivita == "ALL"){
					for(var t=0; t < 20; t++){
					 document.getElementById("chk_" + t).checked = true;
					}
					document.getElementById("chk_allreg").checked = true;
				}else{
					var vetIdelencoIdZoneAttivitaSelezionate = elencoIdZoneAttivita.split(',');
					var numeroZoneAttivita = vetIdelencoIdZoneAttivitaSelezionate.length;
	
					for(var t=0; t < numeroZoneAttivita; t++){
					 var chk_id = vetIdelencoIdZoneAttivitaSelezionate[t];
					 document.getElementById("chk_" + chk_id).checked = true;
					}
				
				}
			}
		}
		
		inizializzaLista();
		
		
		function selezioneSingola() {
			var allIsChecked = document.getElementById("chk_allreg").checked;
			if(allIsChecked){
				document.getElementById("chk_allreg").checked = false;
			}
		}
		
		function selezioneMultipla() {
			var allIsChecked = document.getElementById("chk_allreg").checked;
			if(allIsChecked){
				for(var t=0; t < 20; t++){
					document.getElementById("chk_" + t).checked = true;
				}
			}else{
				for(var t=0; t < 20; t++){
					document.getElementById("chk_" + t).checked = false;
				}
			}
		}
	    
 		
	</gene:javaScript>
</gene:template>
</c:otherwise>
</c:choose>