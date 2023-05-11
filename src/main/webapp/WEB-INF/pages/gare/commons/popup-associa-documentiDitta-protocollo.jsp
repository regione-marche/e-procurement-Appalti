<%
/*
 * Created on: 17-feb-2015
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

<gene:template file="popup-message-template.jsp" gestisciProtezioni="false" >

	<gene:redefineInsert name="head" >
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
		
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
		
		
		<style type="text/css">
			.dataTables_filter {
		     	display: none;
			}
			
			.dataTables_length {
				padding-top: 5px;
				padding-bottom: 5px;
			}
			
			.dataTables_length label {
				vertical-align: bottom;
			}
			
			.dataTables_paginate {
				padding-bottom: 5px;
			}
		
			.etabs {
				margin-bottom: 5px;
			}
		</style>
		
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Associa documenti buste telematiche a protocollo" />
		
	<gene:redefineInsert name="corpo">
		<br>
		<div id="info">
			Mediante questa funzione i documenti, forniti dagli operatori mediante le procedure telematiche di presentazione della domanda di partecipazione e dell'offerta,
			e disponibili nella piattaforma dopo l'apertura delle buste,
			vengono collegati al protocollo associato alla relativa busta.
			<br><br>
			<b>ATTENZIONE:</b> Vengono considerati solo i documenti che sono stati già trasferiti al documentale (dati pregressi o operazione di collegamento fallita).
			Per i documenti non ancora trasferiti al documentale, l'operazione di collegamento al protocollo viene fatta contestualmente a quella di trasferimento. 
			<br>
			<br>
			<div id="msgInfoErr" style="display: none;"><b>Si è presentato un errore durante l'operazione!</b>
			<br>
			<br>
			</div>
		</div>
		<div id="msgesito" style="display: none;">
			Operazione conclusa. 
			<div id="msgNumDoc" style="display: none;">
			Sono stati elaborati <span id="numDoc"></span> documenti.
			</div>
			<div id="msgErr" style="display: none;">
			Per alcuni di questi l'operazione è fallita.
			</div>
			<div id="msgNoDati" style="display: none;">
			Nessun documento da collegare.
			<br>
			</div>
			<br>
			<br>
		</div>
			<form id="richiestawslogin">
				<table class="dettaglio-notab">
					<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
					<input id="servizio" type="hidden" value="DOCUMENTALE" />
					<input id="tiposistemaremoto" type="hidden" value="JIRIDE" />
					<input id="tabellatiInDB" type="hidden" value="" />
					<input id="modoapertura" type="hidden" value="MODIFICA" /> 
					<select id="tipocollegamento" name="tipocollegamento" style="display: none;"></select>
					<input id="idconfi" type="hidden" value="${param.idconfi}" />
					<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp">
						<jsp:param name="gestioneVisualizzazioneContratta" value="1"/>
						<jsp:param name="contextPath" value="${pageContext.request.contextPath}"/>
					</jsp:include>			
				</table>
				<input type="hidden" name="codgar" id="codgar" value="${param.codgar}" />
			</form>
			
		
		
	</gene:redefineInsert>
	<gene:javaScript>
		$(window).on("load", function (){
		_popolaTabellato("ruolo","ruolo");
		_popolaTabellato("tipocollegamento","tipocollegamento");
		_getWSLogin();
		_gestioneWSLogin();
		_validazioneWSLogin();
		//$(".comandi-dettaglio INPUT").first().hide();
		showObj("offParametriUtente",false);
		showObj("onParametriUtente",true);
		showObj("rigaUtente",false);
		showObj("rigaRuolo",false);	
		});
		
		function annulla(){
			window.close();
		}
		
		function conferma(){
			var tempo = 400;
			if ($("#richiestawslogin").validate().form()){
				var utente =$("#username").val();
				var ruolo = $("#ruolo option:selected").val();
				if(utente=="" || ruolo==""){
					outMsg("I campi 'Utente' e 'Ruolo' devono essere valorizzati", "ERR");
					onOffMsg();
					return;
				}
				_wait();
		$.ajax({
    		type: "POST",
    		async: true,
    		dataType: "json",
    		url: "pg/AssociaDocumentiDittaProtocollo.do",
    		data : {
				username: $("#username").val(),
				ruolo: $("#ruolo option:selected").val(),
				tipocollegamento: $("#tipocollegamento option:selected").val(),
				codgar : $("#codgar").val(),
			},
    		success: function(json) {
    			if (json) {
    				$('#info').hide();
    				
					$('#msgesito').show(tempo);
					$(".comandi-dettaglio INPUT:first-child").hide();
					$(".comandi-dettaglio INPUT:nth-child(2)").val("Chiudi");
					$(".comandi-dettaglio INPUT:nth-child(2)").attr('title','Chiudi');
					$("#richiestawslogin").hide();
					if (json.esito == 0){
						$("#msgNoDati").show(tempo);
					}else{
						$('#numDoc').text(json.numeroElaborazioni);
						$("#msgNumDoc").show(tempo);
						if (json.esito == -1)
							$("#msgErr").show(tempo);
						opener.window.location=contextPath+'/History.do?'+csrfToken+'&metodo=reload';
					}
					_nowait();
    			}
    		},
    		error: function(e) {
    			$('#msgInfoErr').show(tempo);
    			$(".comandi-dettaglio INPUT:first-child").hide();
				_nowait();
    		}
    	});
			}
		}
		
		function showParametriUtente(){
			var obj=getObjectById("onParametriUtente");
			var visibile=obj.style.display=="none";
			showObj("offParametriUtente",!visibile);
			showObj("onParametriUtente",visibile);
			showObj("rigaUtente",!visibile);
			showObj("rigaRuolo",!visibile);	
		}
	</gene:javaScript>
</gene:template>
