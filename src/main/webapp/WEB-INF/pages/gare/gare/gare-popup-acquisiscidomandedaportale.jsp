<%
/*
 * Created on: 18-06-2014
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
			<c:when test='${not empty param.idconfi}'>
				<c:set var="idconfi" value="${param.idconfi}" />
			</c:when>
			<c:otherwise>
				<c:set var="idconfi" value="${idconfi}" />
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
						
		<gene:redefineInsert name="addHistory" />
		<gene:redefineInsert name="gestioneHistory" />
					
		<gene:setString name="titoloMaschera" value='Acquisisci domande di partecipazione da portale Appalti' />
		
		<c:set var="isSuperataDataTerminePresentazione" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsSuperataDataTerminePresentazioneFunction", pageContext, ngara, "2")}' />
				
		<gene:redefineInsert name="corpo">
		
			<input type="hidden" id="ngara" value="${ngara}" />
			<input type="hidden" id="codiceGara" value="${codiceGara}" />
			<input type="hidden" id="tipo" value="${tipo}" />
			<input type="hidden" id="idconfi" value="${idconfi}" />
			
			<table class="dettaglio-notab">
				<tr id="sezioneIniziale">
					<td class="valore-dato" colspan="2">
						<c:choose>
							<c:when test="${isSuperataDataTerminePresentazione == 'false'}">
								<br>
								Non &egrave; possibile procedere con l'acquisizione delle domande di partecipazione da portale Appalti perch&egrave; non &egrave; ancora scaduto il termine di presentazione.
								<br>
								<br>
								Il termine per la presentazione delle domande di partecipazione scade il giorno <b>${dataScadenza}</b> alle ore <b>${oraScadenza}</b>.
								<br>
								<br>
							</c:when>
							<c:otherwise>
								<br>
								Mediante questa funzione &egrave; possibile procedere all'acquisizione delle domande di partecipazione da portale Appalti.
								<br>
								Confermi l'operazione ?
								<br>
								<br>
							</c:otherwise>
						</c:choose>
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
						Numero acquisizioni: <span id="numeroAcquisizioni"></span>
						<br>
						Numero acquisizioni non processate o con errori: <span id="numeroAcquisizioniErrore"></span>
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
						<c:if test="${isSuperataDataTerminePresentazione == 'true'}">
							<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:acquisisciAJAX('${ngara}');">
						</c:if>
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
					url: "pg/AvanzamentoAcqDomandePartecipazioneDaPortaleAJAX.do?ngara=" + ngara,
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
					url: "pg/AvanzamentoAcqDomandePartecipazioneDaPortaleAJAX.do?ngara=" + ngara,
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
				var idconfi = $("#idconfi").val();
				inizializzaAvanzamento();
			
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
					url: "pg/AcquisisciDomandePartecipazioneDaPortale.do?ngara=" + ngara + "&idconfi=" +idconfi,
					success: function(res){
						esito= res.Esito;
						msgEsito = res.MsgErrore;
						$("#numeroAcquisizioni").text(res.numeroAcquisizioni);
						$("#numeroAcquisizioniErrore").text(res.numeroAcquisizioniErrore);
						if (res.sezioneAcquisizioniScartate > 0) {
							$("#sezioneAcquisizioniScartate").show();
							$("#numeroAcquisizioniScartate").text(res.numeroAcquisizioniScartate);
						}
						if(esito!="Errore"){
							faseNext();
						}

					},
					error: function(e){
						alert("Errore generico durante l'operazione");
					},
					complete: function(jqXHR, textStatus) {
						clearInterval(avanzamentoInterval);
						var max = $("#avanzamento").progressbar( "option", "max" );
						$("#avanzamento").progressbar( "option", "value", max );
						$("#avanzamento-label").text("100 %");
						$("#sezioneAvanzamento").hide();
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
				
				var avanzamentoInterval = setInterval(avanzamento, 1000);
				
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


