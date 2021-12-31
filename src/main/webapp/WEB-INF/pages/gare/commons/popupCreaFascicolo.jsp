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
	<c:when test='${!empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${!empty param.codiceGara}'>
		<c:set var="codiceGara" value="${param.codiceGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${codiceGara}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${!empty param.idconfi}'>
		<c:set var="idconfi" value="${param.idconfi}" />
	</c:when>
	<c:otherwise>
		<c:set var="idconfi" value="${idconfi}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${!empty param.genere}'>
		<c:set var="genere" value="${param.genere}" />
	</c:when>
	<c:otherwise>
		<c:set var="genere" value="${genere}" />
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

<gene:redefineInsert name="corpo">

	<gene:setString name="titoloMaschera" value='Crea fascicolo documentale' />
	
	<c:set var="controlloSuperato" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlliCreaFascicoloFunction",  pageContext, ngara, codiceGara,genere)}'/>
	
	<br>
		<div id="info">
			<c:if test="${controlloSuperato ne 'NO' }">
			<c:set var='msgTipo' value="la gara"/>
			<c:choose>
				<c:when test="${genere eq '10'}">
					<c:set var='msgTipo' value="l'elenco"/>
				</c:when>
				<c:when test="${genere eq '20'}">
					<c:set var='msgTipo' value="il catalogo"/>
				</c:when>
				<c:when test="${genere eq '11'}">
					<c:set var='msgTipo' value="l'avviso"/>
				</c:when>
			</c:choose>
			Confermi la creazione del fascicolo documentale per ${msgTipo }?
			<br>
			<div id="msgInfoErr" style="display: none;"><b>Si è presentato un errore durante l'operazione.<br><span id="messaggioErr" style="color: red;"></span></b>
			<br>
			<br>
			</div>
			</c:if>
			<c:if test="${controlloSuperato eq 'NO' }">
				<div >${msg }</b>
				<br>
				<br>
				</div>
			</c:if>
		</div>
		<div id="msgesito" style="display: none;">
			Operazione conclusa con successo.
			<br>
			</div>
			<br>
			<br>
		</div>
<c:if test="${controlloSuperato eq 'SI' }">
	<form id="parametririchiestafascicolo">
		<table class="dettaglio-notab" id="tabellaProtocollo">	
			<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
			<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
			<input id="tiposistemaremoto" type="hidden" value="" />
			<input id="tabellatiInDB" type="hidden" value="" />
			<input id="modoapertura" type="hidden" value="" /> 
			<input id="entita" type="hidden" value="${entita}" /> 
			<input id="key1" type="hidden" value="${key1}" /> 
			<input id="key2" type="hidden" value="" />
			<input id="key3" type="hidden" value="" />
			<input id="key4" type="hidden" value="" />
			<input id="ngara" name="ngara" type="hidden" value="${ngara }"/>
			<input id="codiceGara" name="codiceGara" type="hidden" value="${codiceGara }"/>
			<input id="idconfi" name="idconfi" type="hidden" value="${idconfi }"/>
			<input id="genere" name="genere" type="hidden" value="${genere }"/>
			<input id="chiaveOriginale" name="chiaveOriginale" type="hidden" value="${ngara }"/>
			<input id="tabellatiInDB" type="hidden" value="" />
			
			<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp">
				<jsp:param name="gestioneVisualizzazioneContratta" value="1"/>
				<jsp:param name="contextPath" value="${pageContext.request.contextPath}"/>
			</jsp:include>
		</table>
			<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-internoDati.jsp">
				<jsp:param name="valoreChiaveRiservatezza" value="${valoreChiaveRiservatezza}" />
			</jsp:include>
		
	</form>
</c:if>

</gene:redefineInsert>

<gene:redefineInsert name="buttons">
	<c:if test="${controlloSuperato ne 'NO' }">
	<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
	</c:if>
	<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	
</gene:redefineInsert>

	<gene:javaScript>
	
	function annulla(){
			window.close();
	}
	
	$("#pulsanteChiudi").hide();
	
	<c:if test="${controlloSuperato eq 'SI' }">
		$(window).on("load", function (){
			
			_getWSTipoSistemaRemoto();
			_tipoWSDM = $("#tiposistemaremoto").val();
			_popolaTabellato("ruolo","ruolo");
			_popolaTabellato("codiceuo","codiceuo");
			_getWSLogin();
			_gestioneWSLogin();
			_codiceGara = "${codiceGara }";
			_genereGara = "${genere }";
			
			
			
			/*
			 * Gestione tabellati per richiesta protocollazione
			 */
			 _popolaTabellato("classifica","classificadocumento");
			_popolaTabellato("classificafascicolo","classificafascicolonuovo");
			_popolaTabellato("idindice","idindice");
			_popolaTabellato("idtitolazione","idtitolazione");
			_popolaTabellato("idunitaoperativamittente","idunitaoperativamittente");
			_controlloPresenzaFascicolazione();
			
			if(_logincomune=="1")
				bloccaCampiLoginComune();
			
			$('#inserimentoinfascicolo').change(function() {
						_gestioneInserimentoInFascicolo();
				    });
							
			_fascicoliPresenti=0;		
			_inizializzazioni();
			$("#TitoloDatiDocumento").hide();
			$("#classificaDocumento").hide();
			$("#idTitolazione").hide();
			$("#codRegistroDocumento").hide();
			$("#tipoDocumento").hide();
			$("#oggettoDocumento").hide();
			$("#mittenteInterno").hide();
			$("#indirizzoMittente").hide();
			$("#mezzoInvio").hide();
			$("#sezionelivelloriservatezza").hide();
			$("#idIndice").hide();
			$("#idUnitaoperativaMittente").hide();
			$("#mezzo").closest('tr').hide();
			$("#rigaSottotipo").hide();
			
			$('#username').change(function() {
				if (_tipoWSDM == "JIRIDE"){
					if(_letturaMittenteDaServzio){
						$('#mittenteinterno').empty();
						_popolaTabellatoJirideMittente("mittenteinterno");
						if(_delegaInvioMailDocumentaleAbilitata == 1){
							$('#indirizzomittente').empty();
							_popolaTabellatoJirideMittente("indirizzomittente");
						}
					}
					caricamentoStrutturaJIRIDE();
				}
			});
			
			$('#ruolo').change(function() {
				if (_tipoWSDM == "JIRIDE"){
					caricamentoStrutturaJIRIDE();
				}
		    });
		});
		
		_gestioneWSLoginContratto(false);
		
		function conferma(){
			var tempo = 400;
			var errori = controlloCampiObbligatori();
			if ( !errori) {
				
				_wait();
				_setWSLogin();
				$.ajax({
		    		type: "POST",
		    		async: true,
		    		dataType: "json",
		    		url: "pg/CreaFascicolo.do",
		    		data : {
						username: $("#username").val(),
						password: $("#password").val(),
						ruolo: $("#ruolo option:selected").val(),
						nome : $("#nome").val(),
						cognome : $("#cognome").val(),
						codiceuo : $("#codiceuo option:selected").val(),
						idutente : $("#idutente").val(),
						idutenteunop : $("#idutenteunop").val(),
						oggettofascicolo : $("#oggettofascicolonuovo").val(),
						classificafascicolo : $("#classificafascicolonuovo").val(),
						tipofascicolo : $("#tipofascicolonuovo").val(),
						descrizionefascicolo : $("#descrizionefascicolonuovo").val(),
						struttura: $("#strutturaonuovo option:selected").val(),
						nomeRup: $("#nomeRup").text(),
						acronimoRup: $("#acronimoRup").text(),
						tipowsdm : _tipoWSDM,
						idconfi: $("#idconfi").val(),
						entita : $("#entita").val(),
						key1 : $("#key1").val(),
						servizio : $("#servizio").val(),
						genereGara : ${genere }
							},
		    		success: function(json) {
		    			if (json) {
		    				/*
		    				$('#info').hide();
		    				$('#tabellaProtocollo').hide();
		    				$('#pulsanteConferma').hide();
		    				$('#pulsanteAnnulla').hide();
		    				$("#pulsanteChiudi").show();
							$('#msgesito').show(tempo);
							_nowait();
							*/
							var esito=json.esito;
							if(esito == "OK"){
								opener.historyReload();
								window.close();
							}else{
								var messaggio = json.msg; 
								$("#messaggioErr").text(messaggio);
								$("#msgInfoErr").show();
								_nowait();
							}
		    			}
		    		},
		    		error: function(e) {
		    			$('#msgInfoErr').show(tempo);
		    			
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
	</c:if>
	
	</gene:javaScript>
	
</gene:template>

