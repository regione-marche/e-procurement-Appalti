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
			<c:when test='${not empty param.codiceGara}'>
				<c:set var="codiceGara" value="${param.codiceGara}" />
			</c:when>
			<c:otherwise>
				<c:set var="codiceGara" value="${codiceGara}" />
			</c:otherwise>
		</c:choose>
		
		<gene:redefineInsert name="addHistory" />
		<gene:redefineInsert name="gestioneHistory" />
		<gene:setString name="titoloMaschera" value='Acquisisci offerte da portale Appalti' />
		
		<c:set var="isSuperataDataTerminePresentazione" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsSuperataDataTerminePresentazioneFunction", pageContext, ngara, "1")}' />
		<c:set var="codStatoGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetStatoGaraFunction", pageContext, codiceGara)}' />	
	
		<c:if test="${isSuperataDataTerminePresentazione ne 'false' }">
			<c:set var="numDitteGaraCorso" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ConteggioDitteFunction", pageContext, ngara,"ACQUISIZIONE = 9 AND AMMGAR = 2")}' />
		</c:if>
				
		<gene:redefineInsert name="corpo">
		
			<input type="hidden" id="ngara" value="${ngara}" />
			<input type="hidden" id="codiceGara" value="${codiceGara}" />
			
			<table class="dettaglio-notab">
				<tr id="sezioneIniziale">
					<td class="valore-dato" colspan="2">
						<c:choose>
							<c:when test="${codStatoGara == '4'}">
								<br>
								Non &egrave; possibile procedere con l'acquisizione delle offerte da portale Appalti perch&egrave; la gara risulta sospesa.
								<br>
								<br>
								<c:set var="bloccoOperazione" value="true"/>
							</c:when>
							<c:when test="${isSuperataDataTerminePresentazione == 'false'}">
								<br>
								Non &egrave; possibile procedere con l'acquisizione delle offerte da portale Appalti perch&egrave; non &egrave; ancora scaduto il termine di presentazione delle offerte.
								<br>
								<br>
								Il termine per la presentazione delle offerte scade il giorno <b>${dataScadenza}</b> alle ore <b>${oraScadenza}</b>.
								<br>
								<br>
								<c:set var="bloccoOperazione" value="true"/>
							</c:when>
							<c:when test='${!empty numDitteGaraCorso and numDitteGaraCorso ne "" and numDitteGaraCorso ne "0"}'>
								<br>
								Non &egrave; possibile procedere con l'acquisizione delle offerte da portale Appalti perch&egrave; ci sono ditte in gara, inserite dopo la pubblicazione in area riservata, per cui non � stato fatto l'invio dell'invito.
								<br>Completare l'invito per queste ditte oppure eliminarle dalla gara.
								<br>
								<br>
								<br>
								<c:set var="bloccoOperazione" value="true"/>
							</c:when>
							<c:otherwise>
								<br>
								Mediante questa funzione &egrave; possibile procedere all'acquisizione delle offerte da portale Appalti.
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
						Numero acquisizioni offerte: <span id="numeroAcquisizioni"></span>
						<br>
						Numero acquisizioni offerte non processate o con errori: <span id="numeroAcquisizioniErrore"></span>
						<br>
						<div id="Rinunce" style="display: none;">
							Numero acquisizioni rinunce: <span id="numeroRinunce"></span>
							<br>
							Numero acquisizioni rinunce non processate o con errori: <span id="numeroRinunceErrore"></span>
							<br>
						</div>
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
						<c:if test="${bloccoOperazione ne 'true'}">
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
					url: "pg/AcquisisciOfferteDaPortale.do?ngara=" + ngara,
					success: function(res){
						esito= res.Esito;
						msgEsito = res.MsgErrore;
						$("#numeroAcquisizioni").text(res.numeroAcquisizioni);
						$("#numeroAcquisizioniErrore").text(res.numeroAcquisizioniErrore);
						if (res.numeroRinunce > 0 || res.numeroRinunceErrore > 0) {
							$("#numeroRinunce").text(res.numeroRinunce);
							$("#numeroRinunceErrore").text(res.numeroRinunceErrore);
							$("#Rinunce").show();
						}
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


