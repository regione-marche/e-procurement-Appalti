<%
/*
 * Created on: 12-11-2021
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
	
		<c:choose>
			<c:when test='${not empty param.ngara}'>
				<c:set var="ngara" value="${param.ngara}" />
			</c:when>
			<c:otherwise>
				<c:set var="ngara" value="${ngara}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.codiceGara}'>
				<c:set var="codiceGara" value="${param.codiceGara}" />
			</c:when>
			<c:otherwise>
				<c:set var="codiceGara" value="${codiceGara}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.codimp}'>
				<c:set var="codimp" value="${param.codimp}" />
			</c:when>
			<c:otherwise>
				<c:set var="codimp" value="${codimp}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.faseCall}'>
				<c:set var="faseCall" value="${param.faseCall}" />
			</c:when>
			<c:otherwise>
				<c:set var="faseCall" value="${faseCall}" />
			</c:otherwise>
		</c:choose>
		
		<gene:redefineInsert name="addHistory" />
		<gene:redefineInsert name="gestioneHistory" />
		<gene:setString name="titoloMaschera" value='Analizza documenti DGUE ${not empty codimp? " della ditta":""}' />
					
		<gene:redefineInsert name="corpo">
		
			<input type="hidden" id="ngara" value="${ngara}" />
			<input type="hidden" id="codimp" value="${codimp}" />
			<input type="hidden" id="faseCall" value="${faseCall}" />
			<input type="hidden" id="codiceGara" value="${codiceGara}" />
			
			<table class="dettaglio-notab">
				<tr id="sezioneIniziale">
					<td class="valore-dato" colspan="2">
						<br>
						Mediante questa funzione &egrave; possibile analizzare i documenti DGUE.
						<br><br>
						Sono oggetto di analisi i documenti presentati dalla ditta con estensione xml.
						<c:if test="${empty codimp}">
						<br>Vengono considerate solo le ditte per cui non sono ancora stati analizzati dei documenti.
						Per forzare l'analisi dei documenti della singola ditta utilizzare la funzione disponibile dal menù di riga.
						</c:if>	
						<br>
						<br>Si sottolinea che questa funzionalità non si sostituisce a un controllo puntuale dei documenti da parte dell'operatore.
						<br>
						<br>	
						Confermi l'operazione ?
						<br>
						<br>
					</td>
				</tr>
				
				<tr id="sezioneAvanzamento" style="display: none;">
					<td colspan="2">
						<br>
						Attendere il completamento dell'operazione
						<br>
						<br>
						<div id="avanzamento"><div id="avanzamento-label"></div></div>
						<br>
					</td>
				</tr>
				
				<tr id="sezioneFinale" style="display: none;">
					<td colspan="2">
						<br>
						L'operazione &egrave; stata eseguita con successo.
						<br>
						<br>								
					</td>
				</tr>
				
				<tr id="sezioneErrore" style="display: none;">
					<td colspan="2">
						<br>
						<span id="msgEsitoErrore"></span>
						<br>
						<br>
					</td>
				</tr>
				
				<tr id="sezioneComandiIniziali">
					<td colspan="2" class="comandi-dettaglio">
						<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:acquisisciAJAX();">
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:chiudi();">&nbsp;&nbsp;
					</td>
				</tr>
				
				<tr id="sezioneComandiFinali" style="display: none;">
					<td colspan="2" class="comandi-dettaglio">
						<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
					</td>
				</tr>
				
			</table>
		</gene:redefineInsert>
		
		<gene:javaScript>
			
			function chiudi(){
				window.close();
			}
			
			function inizializzaAvanzamento() {
			
				$(".ui-progressbar").css("position","relative");
				$("#avanzamento-label").css("position","absolute");
				$("#avanzamento-label").css("left","45%");
				$("#avanzamento-label").css("padding-top","4px");
				$("#avanzamento-label").css("font-weight","bold");
				$("#avanzamento-label").css("font","10px Verdana, Arial, Helvetica, sans-serif");
				$("#avanzamento-label").css("text-shadow","1px 1px 0 #fff");
			
				$("#sezioneAvanzamento").show();
				var ngara = $("#ngara").val();
				$.ajax({
					type: "POST",
					dataType: "json",
					async: false,
					beforeSend: function(x) {
						if(x && x.overrideMimeType) {
							x.overrideMimeType("application/json;charset=UTF-8");
						}
					},
					url: "pg/AvanzamentoAcqOfferteDaPortaleAJAX.do?ngara=" + ngara,
					success: function(res){
						$("#avanzamento").progressbar({
							max: res.cnt
			    		});
			    		$("#avanzamento-label").text("0 %");
					}
				});
				
			}
			
			function avanzamento() {
				var ngara = $("#ngara").val();
				$.ajax({
					type: "POST",
					dataType: "json",
					async: false,
					beforeSend: function(x) {
						if(x && x.overrideMimeType) {
							x.overrideMimeType("application/json;charset=UTF-8");
						}
					},
					url: "pg/AvanzamentoAcqOfferteDaPortaleAJAX.do?ngara=" + ngara,
					success: function(res){
						var curr = res.cnt;
						var max = $("#avanzamento").progressbar( "option", "max" );
						$("#avanzamento").progressbar( "option", "value", max - curr );
						var perc = Math.round(((max - curr)/max)*100);
						$("#avanzamento-label").text(perc + " %");
					}
				});
			}
			
			
			function acquisisciAJAX(){	
				var ngara = $("#ngara").val();
				var codimp = $("#codimp").val();
				var faseCall = $("#faseCall").val();
			
				$("#sezioneIniziale").hide();
				$("#sezioneComandiIniziali").hide();
			
				$.ajax({
					type: "POST",
					dataType: "json",
					async: true,
					beforeSend: function(x) {
						if(x && x.overrideMimeType) {
							x.overrideMimeType("application/json;charset=UTF-8");
						}
					},
					url: "pg/AnalisiDocumentiDGUE.do?ngara=" + ngara + "&codimp=" + codimp + "&faseCall=" + faseCall,
					success: function(res){
						msgEsito = res.MsgErrore;
						esito= res.Esito;
						if(esito!="Errore"){
							faseNext();
						}

					},
					error: function(e){
						alert("Errore generico durante l'operazione");
					},
					complete: function(jqXHR, textStatus) {
						if(esito=="Errore"){
							$("#msgEsitoErrore").html(msgEsito);
							$("#sezioneComandiFinali").show();
                        	$("#sezioneErrore").show();	
						}else{
							
							$("#sezioneFinale").show();
							$("#sezioneComandiFinali").show();
						}
					}
				});
				
			}
			
			
			function faseNext() {
				window.opener.document.forms[0].pgSort.value = "";
				window.opener.document.forms[0].pgLastSort.value = "";
				window.opener.document.forms[0].pgLastValori.value = "";
				window.opener.bloccaRichiesteServer();
				window.opener.listaVaiAPagina(0);
			}
		
		
		</gene:javaScript>	
	</gene:template>

</div>


