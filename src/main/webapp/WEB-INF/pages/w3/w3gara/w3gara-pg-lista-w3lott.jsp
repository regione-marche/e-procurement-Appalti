
<%
	/*
	 * Created on 23-Apr-2008
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

<c:set var="stato_simog_gara" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.GetStatoSimogW3GaraFunction",pageContext,numgara)}' />

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>
<style type="text/css">

input[type='number'] {
    -moz-appearance:textfield;
}



/* Webkit browsers like Safari and Chrome */

input[type=number]::-webkit-inner-spin-button,
input[type=number]::-webkit-outer-spin-button {
    -webkit-appearance: none;
    margin: 0;
}
</style>
<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<table class="lista" style="border: 1px dotted #A0AABA; background-color: #EFEFEF">
				<tr>
					<td width="100px">Stato dei lotti:</td>
					<td>
						<select name="statoSimog" id="statoSimog">
							<option value="TUTTI" selected="selected">Visualizza tutti i lotti indipendentemente dallo stato</option>
							<option value="SIMOG">Visualizza solo i lotti da inviare ad ANAC</option>
						</select>
					</td>
					<td width="100px">Oggetto:</td>
					<td>
						<input class="testo" name="oggetto" id="oggetto" title="Oggetto" type="text" size="40">
					</td>
				</tr>
				<tr>
					<td width="100px">Importo minimo:</td>
					<td>
						<input class="testo" name="importo>Inferiore" id="importoInferiore" title="Importo da" type="number" size="40" pattern="[0-9]*">
					<td width="110px">Importo massimo:</td>
					<td>
						<input class="testo" name="importoSuperiore" id="importoSuperiore" title="Importo a" type="number" size="40" pattern="[0-9]*">
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr><td>
	
	</td></tr>
	
	<tr>
		<td>
		<gene:formLista entita="W3LOTT" pagesize="100" sortColumn="3"
			where="W3LOTT.NUMGARA = #W3GARA.NUMGARA#" tableclass="datilista"
			gestisciProtezioni="true" gestore="it.eldasoft.sil.w3.tags.gestori.submit.GestoreW3LOTT"
			plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreW3GARELotti">
			
			<input type="hidden" name="filtroStatoSimog" value="${param.filtroStatoSimog}" />
			<input type="hidden" name="filtroOggetto" value="${param.filtroOggetto}" />
			<input type="hidden" name="filtroImportoMin" value="${param.filtroImportoMin}" />
			<input type="hidden" name="filtroImportoMax" value="${param.filtroImportoMax}" />
			<input type="hidden" name="aggiornaMassivo" value="false" />
			<input type="hidden" name="lottiAggiornamento" value="" />
			<input type="hidden" name="numeroLotti" value="" />
					 
			<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"
				width="50">
				<c:if test="${currentRow >= 0}">
				
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"
						onClick="chiaveRiga='${chiaveRigaJava}'">
						
						<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza"
							title="Visualizza lotto" />
						
						<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD")}'>
							<c:if test="${!(stato_simog_gara eq '5' or stato_simog_gara eq '6' or stato_simog_gara eq '7')}">
								<c:if test="${!(datiRiga.W3LOTT_STATO_SIMOG eq '5' or datiRiga.W3LOTT_STATO_SIMOG eq '6' or datiRiga.W3LOTT_STATO_SIMOG eq '7')}">
									<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica"
										title="Modifica lotto" />
								</c:if>
							</c:if>
						</c:if>
						
						<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
							<c:if test="${!(stato_simog_gara eq '5' or stato_simog_gara eq '6' or stato_simog_gara eq '7')}">
								<c:if test="${empty datiRiga.W3LOTT_CIG}">
									<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"
										title="Elimina lotto" />
								</c:if>
							</c:if>
						</c:if>
					</gene:PopUp>
					
					<c:if test='${gene:checkProtFunz(pageContext, "DEL","LISTADELSEL")}'>
						<c:choose>
							<c:when test="${empty datiRiga.W3LOTT_CIG and !(stato_simog_gara eq '5' or stato_simog_gara eq '6' or stato_simog_gara eq '7')}">
								<input type="checkbox" name="keys" value="${chiaveRiga}" />
							</c:when>
							<c:otherwise>
								<input type="checkbox" name="keys" value="${chiaveRiga}" disabled="disabled"/>
							</c:otherwise>
						</c:choose>
					</c:if>
				</c:if>
			
			</gene:campoLista>
			
			<gene:campoLista campo="NUMGARA" visibile="false"/>
			<gene:campoLista title="N." campo="NUMLOTT" width="30" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista campo="CIG" width="80" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista campo="STATO_SIMOG" width="120"/>
			<gene:campoLista campo="DATA_PUBBLICAZIONE" width="100"/>
			<gene:campoLista campo="OGGETTO" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista campo="TIPO_CONTRATTO"/>
			<gene:campoLista campo="IMPORTO_LOTTO"/>
			<gene:campoLista title="&nbsp;" width="24">
				<c:choose>
					<c:when test="${datiRiga.W3LOTT_STATO_SIMOG eq '1' or datiRiga.W3LOTT_STATO_SIMOG eq '3' or datiRiga.W3LOTT_STATO_SIMOG eq '5'}">
						<img width="24" height="24" title="" alt="" src="${pageContext.request.contextPath}/img/invioFlusso.png"/>	
					</c:when>
				</c:choose>
			</gene:campoLista>
			
	
		</gene:formLista></td>
	</tr>
	<c:if test="${stato_simog_gara eq '5' or stato_simog_gara eq '6' or stato_simog_gara eq '7'}">
		<gene:redefineInsert name="pulsanteListaInserisci"/>
		<gene:redefineInsert name="pulsanteListaEliminaSelezione"/>
		<gene:redefineInsert name="listaNuovo"/>
		<gene:redefineInsert name="listaEliminaSelezione"/>
	</c:if>
	<tr>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiListaPage.jsp" />
	</tr>

	<gene:redefineInsert name="addToAzioni" >
		<jsp:include page="/WEB-INF/pages/w3/commons/addtodocumenti-richieste-lotti.jsp" />
	</gene:redefineInsert>

</table>

<gene:javaScript>	
	var sSimog='TUTTI';
	if('${param.filtroStatoSimog}')
		sSimog='${param.filtroStatoSimog}';
	
	document.forms[0].filtroStatoSimog.value = sSimog;
	document.forms[0].filtroOggetto.value = '${param.filtroOggetto}';
	document.forms[0].filtroImportoMin.value = '${param.filtroImportoMin}';
	document.forms[0].filtroImportoMax.value = '${param.filtroImportoMax}';

	$('#statoSimog').val(sSimog);
	$('#oggetto').val('${param.filtroOggetto}');
	$('#importoInferiore').val('${param.filtroImportoMin}');
	$('#importoSuperiore').val('${param.filtroImportoMax}');

	$('#oggetto, #importoInferiore, #importoSuperiore').keyup(function() {
		delay(function(){
				fillFilters();
				listaVaiAPagina(0);
			}, 600);
	});

	$('#statoSimog').change(function() {
		delay(function(){
				fillFilters();
				listaVaiAPagina(0);
			}, 0);
	});
	
	function fillFilters() {
		document.forms[0].filtroStatoSimog.value = $("#statoSimog").val();
		document.forms[0].filtroOggetto.value = $("#oggetto").val();
		document.forms[0].filtroImportoMin.value = $("#importoInferiore").val();
		document.forms[0].filtroImportoMax.value = $("#importoSuperiore").val();
	}
	
	var delay = (function(){
		var timer = 0;
		return function(callback, ms){
			clearTimeout (timer);
			timer = setTimeout(callback, ms);
		};
	})();
	
	function listaModificaSelezione(){
		var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
		if (numeroOggetti == 0) {
	      alert("Nessun elemento selezionato nella lista");
		} else {
			listaApriSchedaModificaMassiva();	
		}
	}
	
	function listaApriSchedaModificaMassiva(){
		bloccaRichiesteServer();
		document.forms[0].metodo.value="nuovo";
		document.forms[0].activePage.value="0";
		document.forms[0].aggiornaMassivo.value="true";
		var lotti="";
		var nlotti=0;
		var arrayLen = "" + document.forms[0].keys.length;
        if (arrayLen != 'undefined') {
          for (i = 0; i < document.forms[0].keys.length; i++) {
			  if (document.forms[0].keys[i].checked){
				if(lotti)
					lotti+=";"
              lotti+=document.forms[0].keys[i].value.split("NUMLOTT=N:")[1];
			  nlotti++;
			}
          }
		}else{
			if(document.forms[0].keys.checked){
    			lotti=document.forms[0].keys.value.split("NUMLOTT=N:")[1];
    			nlotti=1;
            }
		}
		document.forms[0].lottiAggiornamento.value=lotti;
		document.forms[0].numeroLotti.value=nlotti;
		document.forms[0].submit();
	}


</gene:javaScript>	




