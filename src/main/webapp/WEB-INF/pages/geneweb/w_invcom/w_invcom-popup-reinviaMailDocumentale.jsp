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
		
		
		
		
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Reinvio comunicazione a destinatari con errore" />
		
	<gene:redefineInsert name="corpo">
		<br>
		<div id="info">
			Mediante questa funzione è possibile reinviare la comunicazione ai destinatari per cui il precedente invio ha dato errore.
			<br><br>
			<br>
			<div id="msgInfoErr" style="display: none;"><b>Si è presentato un errore durante l'operazione di invio.</b>
			<br>
			<br>
			</div>
		</div>
		
		<div id="msgesito" style="display: none;">
			Invio comunicazione in carico al documentale completato.
			<div id="msgErr" style="display: none;">
			Per alcuni destinatari si sono presentati nuovamente degli errori.
			</div> 
			<div id="msgNoDati" style="display: none;">
			Nessun destinatario preso in considerazione.
			<br>
			</div>
			<br>
			<br>
		</div>
			<form id="richiestawslogin">
				<table class="dettaglio-notab">
					<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
					<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
					<input id="tiposistemaremoto" type="hidden" value="${param.tiposistemaremoto }" />
					<input id="tabellatiInDB" type="hidden" value="" />
					<input id="modoapertura" type="hidden" value="MODIFICA" /> 
										
					<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp">
						<jsp:param name="gestioneVisualizzazioneContratta" value="1"/>
						<jsp:param name="contextPath" value="${pageContext.request.contextPath}"/>
					</jsp:include>			
				</table>
				<input type="hidden" name="idprg" id="idprg" value="${param.idprg}" />
				<input type="hidden" name="idcom" id="idcom" value="${param.idcom}" />
				<input type="hidden" name="idconfi" id="idconfi" value="${param.idconfi}" />
				<input type="hidden" name="entitaWSDM" id="entitaWSDM" value="${param.entitaWSDM}" />
				<input type="hidden" name="chiaveWSDM" id="chiaveWSDM" value="${param.chiaveWSDM}" />
			</form>
			
		
		
	</gene:redefineInsert>
	<gene:javaScript>
		$(window).on("load", function (){
		_tipoWSDM="${param.tiposistemaremoto}";
		if(_tipoWSDM=="JIRIDE" || _tipoWSDM=="PALEO")
			_popolaTabellato("ruolo","ruolo");
		if(_tipoWSDM=="PALEO")
			_popolaTabellato("codiceuo","codiceuo");
		_getWSLogin();
		_gestioneWSLogin();
		_validazioneWSLogin();
		
		showObj("offParametriUtente",false);
		showObj("onParametriUtente",true);
		if(_logincomune=="1")
			bloccaCampiLoginComune();
		_gestioneWSLoginContratto(false);
		});
		
		function annulla(){
			window.close();
		}
		
		function conferma(){
			var tempo = 400;
			if ($("#richiestawslogin").validate().form()){
				if(_logincomune!="1")
					_setWSLogin();
				var utente =$("#username").val();
				var ruolo = $("#ruolo option:selected").val();
				var password =$("#password").val();
				var nome =$("#nome").val();
				var cognome =$("#cognome").val();
				var codiceuo =$("#codiceuo option:selected").val();
				var campiNonValorizzati=false;
				if(utente==""){
					outMsg("Il campo 'Utente' deve essere valorizzato", "ERR");
					onOffMsg();
					campiNonValorizzati=true;
				}
				if((_tipoWSDM=="JIRIDE" || _tipoWSDM=="PALEO") && ruolo==""){
					outMsg("Il campo 'Ruolo' deve essere valorizzato", "ERR");
					onOffMsg();
					campiNonValorizzati=true;
				}
				if((_tipoWSDM=="ARCHIFLOW" || _tipoWSDM=="PALEO" || _tipoWSDM=="ARCHIFLOWFA" || _tipoWSDM=="ITALPROT") && password==""){
					outMsg("Il campo 'Password' deve essere valorizzato", "ERR");
					onOffMsg();
					campiNonValorizzati=true;
				}
				if(_tipoWSDM=="PALEO"){
					if(nome==""){
						outMsg("Il campo 'Nome' deve essere valorizzato", "ERR");
						onOffMsg();
						campiNonValorizzati=true;
					}
					if(cognome==""){
						outMsg("Il campo 'Cognome' deve essere valorizzato", "ERR");
						onOffMsg();
						campiNonValorizzati=true;
					}
					if(codiceuo==""){
						outMsg("Il campo 'Codice unità organizzativa' deve essere valorizzato", "ERR");
						onOffMsg();
						campiNonValorizzati=true;
					}
				}
				
				if(campiNonValorizzati)
					return;
				_wait();
		$.ajax({
    		type: "POST",
    		async: true,
    		dataType: "json",
    		url: "pg/ReinviaMailIcCaricoDocumentale.do",
    		data : {
				username: utente,
				ruolo: ruolo,
				password: password,
				nome : nome,
				cognome : cognome,
				codiceuo : codiceuo,
				idprg : $("#idprg").val(),
				idcom : $("#idcom").val(),
				idconfi : $("#idconfi").val(),
				entitaWSDM : $("#entitaWSDM").val(),
				chiaveWSDM : $("#chiaveWSDM").val(),
				tipowsdm : _tipoWSDM,
			},
    		success: function(json) {
    			if (json) {
    				$('#info').hide();
    				
					$('#msgesito').show(tempo);
					$(".comandi-dettaglio INPUT:first-child").hide();
					$(".comandi-dettaglio INPUT:nth-child(2)").val("Chiudi");
					$(".comandi-dettaglio INPUT:nth-child(2)").attr('title','Chiudi');
					$("#richiestawslogin").hide();
					if (json.esitoInviaMail == 'NoDestinatari'){
						$("#msgNoDati").show(tempo);
					}else{
						if (!json.esitoInviaMail){
							$("#msgErr").show(tempo);
						}
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
			_gestioneWSLoginContratto(!visibile);
		}
	</gene:javaScript>
</gene:template>
