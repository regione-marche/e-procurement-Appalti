<%
/*
 * Created on: 01-10-2010
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
		Finestra per l'attivazione della funzione 'Crea fascicolo'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="popup-message-template.jsp" gestisciProtezioni="false">

<c:choose>
	<c:when test='${!empty param.idconfi}'>
		<c:set var="idconfi" value="${param.idconfi}" />
	</c:when>
	<c:otherwise>
		<c:set var="idconfi" value="${idconfi}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${genere eq "1"}'>
		<c:set var="entita" value="TORN" />
		<c:set var="key1" value="${codiceGara}" />
		<c:set var="valoreChiaveRiservatezza" value="${codiceGara}" />
		
	</c:when>
	<c:otherwise>
		<c:set var="entita" value="GARE"/>
		<c:set var="key1" value="${ngara}" />
		<c:set var="valoreChiaveRiservatezza" value="${ngara}" />
	</c:otherwise>
</c:choose>

<gene:redefineInsert name="head" >

	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
	
</gene:redefineInsert>

<c:set var="eseguireFunzione" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNumeroFascicoliDaCreare",pageContext, idconfi)}' scope="request" />

<gene:redefineInsert name="corpo">

	<gene:setString name="titoloMaschera" value='Crea fascicolo su pregresso' />
	
	<br>
		<div id="info">
			La funzione crea il fascicolo per tutte le gare, avvisi, elenchi e cataloghi pubblicati su portale senza un fascicolo associato.
			<br>
			<c:if test="${not empty elencoUffint }">Vengono considerati solo i dati delle seguenti stazioni appaltanti: <b>${elencoUffint }</b>.</c:if>
			<br>
			<c:if test="${eseguireFunzione eq '1' }">
			<c:if test="${conteggioGare ne '0' }">N.gare pubblicate senza fascicolo: ${conteggioGare }<br></c:if>
			<c:if test="${conteggioAvvisi ne '0' }">N.avvisi pubblicati senza fascicolo: ${conteggioAvvisi }<br></c:if>
			<c:if test="${conteggioElenchi ne '0' }">N.elenchi pubblicati senza fascicolo: ${conteggioElenchi }<br></c:if>
			<c:if test="${conteggioCataloghi ne '0' }">N.cataloghi pubblicati senza fascicolo: ${conteggioCataloghi }<br></c:if>
			<c:if test="${conteggioRilanci ne '0' }">N.gare rilancio pubblicate senza fascicolo: ${conteggioRilanci }<br></c:if>
			<br>
			Confermi l'operazione?
			<br>
			<div id="msgInfoErr" style="display: none;"><b>Si è presentato un errore durante l'operazione.<br><span id="messaggioErr" style="color: red;"></span></b>
			<br>
			<br>
			</div>
			</c:if>
			<c:if test="${eseguireFunzione eq '0' }">
				<br>
				<div >Non ci sono gare, avvisi o elenchi pubblicati su portale senza un fascicolo associato
				<br>
				<br>
				</div>
			</c:if>
		</div>
		<div id="msgesito" style="display: none;">
			Operazione conclusa.<br>
			<div id="divMsgCalcoloErr" style="display: none;"><b>Si è presentato un errore durante l'operazione.<br><span id="msgCalcoloErr" style="color: red;"></span></b></div>
			Fascicoli creati:<span id="fascicoliOk"></span> <br>
			Fascicoli non creati per errori:<span id="fascicoliNok"></span> <br>
			<c:if test="${conteggioRilanci ne '0' }">
			Fascicoli creati per gare rilancio:<span id="fascicoliRilanciOk"></span> <br>
			Fascicoli non creati per errori per gare rilancio:<span id="fascicoliRilanciNok"></span> <br>
			</c:if>
		</div>
		<br>
		<br>
		
	<c:if test="${eseguireFunzione eq '1'}">
	<form id="parametririchiestafascicolo">
		<table class="dettaglio-notab" id="tabellaProtocollo">	
			<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
			<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
			<input id="tiposistemaremoto" type="hidden" value="ENGINEERINGDOC" />
			<input id="tabellatiInDB" type="hidden" value="" />
			<input id="modoapertura" type="hidden" value="" /> 
			<input id="idconfi" name="idconfi" type="hidden" value="${idconfi }"/>
						
			<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp">
				<jsp:param name="gestioneVisualizzazioneContratta" value="1"/>
				<jsp:param name="contextPath" value="${pageContext.request.contextPath}"/>
			</jsp:include>
		</table>
			<table class="dettaglio-notab" id="datiProtocollo">
				<tr id="sezionedatifascicolo">
					<td colspan="2">
						<b><br>Dati del fascicolo</b>
						<div style="display: none;" class="error" id="documentifascicolomessaggio"></div>
					</td>
				</tr>
								
				<tr>
					<td class="etichetta-dato">Classifica <span id="classificaObbligatoriaNuovo" name="classificaObbligatoriaNuovo" >(*)</span></td>
					<td class="valore-dato">
						<select id="classificafascicolonuovo" name="classificafascicolonuovo"></select>
						
					</td>
				</tr>
								
				
				<tr id="sezionestrutturacompetente" >
					<td colspan="2">
						<b><br>Struttura competente</b>
						<div style="display: none;" class="error" id="strutturacompetentemessaggio"></div>
					</td>
				</tr>
				<tr id="sezionestruttura" >
					<td class="etichetta-dato">Struttura (*)</td>
					<td class="valore-dato">
						<select id="strutturaonuovo" name="strutturaonuovo" style="max-width:450px"></select>
						<span id="struttura" name="struttura" style="display: none;"></span>
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
			</table>
		
	</form>
	</c:if>

</gene:redefineInsert>

<gene:redefineInsert name="buttons">
	<c:if test="${eseguireFunzione eq '1' }">
	<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
	</c:if>
	<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	<INPUT type="button" id="pulsanteChiudi" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla()">&nbsp;
	
</gene:redefineInsert>

	<gene:javaScript>
	
	function annulla(){
			window.close();
	}
	
	
	$("#pulsanteChiudi").hide();
	<c:if test="${eseguireFunzione eq '1' }">
	
		$(window).on("load", function (){
			
			_getTabellatiInDB();
			_tipoWSDM = $("#tiposistemaremoto").val();
			_getWSLogin();
			_gestioneWSLogin();
						
			
			/*
			 * Gestione tabellati per richiesta protocollazione
			 */
			_popolaTabellato("classificafascicolo","classificafascicolonuovo");
			_popolaTabellato("struttura","strutturaonuovo");
						
			if(_logincomune=="1")
				bloccaCampiLoginComune();
			
			
							
					
			
		});
		
		_gestioneWSLoginContratto(false);
		
		
		
		function showParametriUtente(){
			var obj=getObjectById("onParametriUtente");
			var visibile=obj.style.display=="none";
			showObj("offParametriUtente",!visibile);
			showObj("onParametriUtente",visibile);
			_gestioneWSLoginContratto(!visibile);
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
			
			var conteggioTot="${conteggioTot}";
			$("#avanzamento").progressbar({
				max: conteggioTot
			});
			$("#avanzamento-label").text("0 %");
				
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
					url: "pg/AvanzamentoCreazioneFascicoliAJAX.do",
					success: function(res){
						var curr = res.cnt;
						var max = $("#avanzamento").progressbar( "option", "max" );
						$("#avanzamento").progressbar( "option", "value", curr );
						var perc = Math.round((curr/max)*100);
						$("#avanzamento-label").text(perc + " %");
					}
				});
			}
			
		function conferma(){
			var tempo = 400;
			var errori = controlloCampiObbligatori();
			if ( !errori) {
				var conteggioRilanci="${conteggioRilanci}";
				var elencoUffintFiltro = "${elencoUffintFiltro}";
				_wait();
				_setWSLogin();
				inizializzaAvanzamento();
				$.ajax({
		    		type: "POST",
		    		async: true,
		    		dataType: "json",
		    		url: "pg/CreaFascicoloPregresso.do",
		    		data : {
						username: $("#username").val(),
						password: $("#password").val(),
						classificafascicolo : $("#classificafascicolonuovo").val(),
						struttura: $("#strutturaonuovo option:selected").val(),
						idconfi: $("#idconfi").val(),
						conteggioRilanci: conteggioRilanci,
						elencoUffintFiltro : elencoUffintFiltro
						},
		    		success: function(json) {
		    			if (json) {
		    										
							var esito=json.esito;
							if(esito == "OK"){
								$('#info').hide();
			    				$('#tabellaProtocollo').hide();
			    				$('#datiProtocollo').hide();
			    				$('#pulsanteConferma').hide();
			    				$('#pulsanteAnnulla').hide();
			    				$("#pulsanteChiudi").show();
			    				$('#fascicoliOk').text(json.fascicoliCreati);
			    				$('#fascicoliNok').text(json.fascicoliErrore);
			    				$('#msgesito').show(tempo);
			    				if(conteggioRilanci != "0"){
				    				var esitoR = json.esitoR;
				    				$('#fascicoliRilanciOk').text(json.fascicoliRilanciCreati);
				    				$('#fascicoliRilanciNok').text(json.fascicoliRilanciErrore);
				    				
									if(esitoR != "OK"){
				    					var messaggio = json.msgR; 
										$("#msgCalcoloErr").text(messaggio);
										$("#divMsgCalcoloErr").show();	
				    				}
				    			}
							}else{
								var messaggio = json.msg; 
								$("#messaggioErr").text(messaggio);
								$("#msgInfoErr").show();
								
							}
		    			}
		    			_nowait();
		    		},
		    		error: function(e) {
		    			$('#msgInfoErr').show(tempo);
		    			_nowait();
		    		},
					complete: function(jqXHR, textStatus) {
						clearInterval(avanzamentoInterval);
						var max = $("#avanzamento").progressbar( "option", "max" );
						$("#avanzamento").progressbar( "option", "value", max );
						$("#avanzamento-label").text("100 %");
						
						$("#sezioneAvanzamento").hide();
										
					}
		    	});
		    	var avanzamentoInterval = setInterval(avanzamento, 1000);
			}
			
		}
		</c:if>
	</gene:javaScript>
	
</gene:template>

