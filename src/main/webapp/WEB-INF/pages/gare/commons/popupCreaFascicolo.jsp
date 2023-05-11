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
	<c:when test='${!empty param.idstipula}'>
		<c:set var="idstipula" value="${param.idstipula}" />
	</c:when>
	<c:otherwise>
		<c:set var="idstipula" value="${idstipula}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${!empty param.codStipula}'>
		<c:set var="codStipula" value="${param.codStipula}" />
	</c:when>
	<c:otherwise>
		<c:set var="codStipula" value="${codStipula}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${!empty param.garaStipula}'>
		<c:set var="garaStipula" value="${param.garaStipula}" />
	</c:when>
	<c:otherwise>
		<c:set var="garaStipula" value="${garaStipula}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${!empty param.fascicoloEsistente}'>
		<c:set var="fascicoloEsistente" value="${param.fascicoloEsistente}" />
	</c:when>
	<c:otherwise>
		<c:set var="fascicoloEsistente" value="${fascicoloEsistente}" />
	</c:otherwise>
</c:choose>

<c:set var="chiave1" value="${ngara }"/>
<c:set var="chiave2" value="${codiceGara }"/>

<c:choose>
	<c:when test='${!empty idstipula and idstipula!=""}'>
		<c:set var="entita" value="G1STIPULA" />
		<c:set var="key1" value="${idstipula}" />
		<c:set var="valoreChiaveRiservatezza" value="${codiceGara}" />
		<c:set var="chiave1" value="${idstipula }"/>
		<c:set var="chiave2" value="${garaStipula }"/>
	</c:when>
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
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmuffici.js?v=${sessionScope.versioneModuloAttivo}"></script>
</gene:redefineInsert>

<gene:redefineInsert name="corpo">

	<gene:setString name="titoloMaschera" value='Crea fascicolo documentale' />
	
	
	
	
	<c:set var="controlloSuperato" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlliCreaFascicoloFunction",  pageContext, chiave1, chiave2,genere, entita)}'/>
	
	<br>
		<div id="info">
			<c:if test="${controlloSuperato ne 'NO' }">
			<c:set var='msgTipo' value="la gara"/>
			<c:choose>
				<c:when test="${entita eq 'G1STIPULA'}">
					<c:set var='msgTipo' value="la stipula"/>
				</c:when>
				<c:when test="${genere eq '10'}">
					<c:set var='msgTipo' value="l'elenco operatori"/>
				</c:when>
				<c:when test="${genere eq '20'}">
					<c:set var='msgTipo' value="il catalogo"/>
				</c:when>
				<c:when test="${genere eq '11'}">
					<c:set var='msgTipo' value="l'avviso"/>
				</c:when>
			</c:choose>
			Confermi <span id="tipoOp">la creazione</span> del fascicolo documentale per ${msgTipo }?
			<br>
			<c:if test="${fascicoloEsistente eq '1' }"><br><b>ATTENZIONE</b>: Esiste già un fascicolo associato. Mediante questa funzione e' possibile modificare l'associazione.</c:if>
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
				<form id="parametririchiestafascicolo">
					<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
					<input id="tiposistemaremoto" type="hidden" value="" />
					<input id="idconfi" name="idconfi" type="hidden" value="${idconfi }"/>
				</form>
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
			<input id="codStipula" name="codStipula" type="hidden" value="${codStipula }"/>
			<input id="idstipula" name="idstipula" type="hidden" value="${idstipula }"/>
			<input id="fascicoloEsistente" name="fascicoloEsistente" type="hidden" value="${fascicoloEsistente }"/>
			
			<c:choose>
				<c:when test="${entita eq 'G1STIPULA' }">
					<c:set var ="valoreChiaveOriginale" value="${codStipula }"/>
				</c:when>
				<c:otherwise>
					<c:set var ="valoreChiaveOriginale" value="${ngara }"/>
				</c:otherwise>
			</c:choose>
			<input id="chiaveOriginale" name="chiaveOriginale" type="hidden" value="${valoreChiaveOriginale }"/>
			<input id="tabellatiInDB" type="hidden" value="" />
			
			<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp">
				<jsp:param name="gestioneVisualizzazioneContratta" value="1"/>
				<jsp:param name="contextPath" value="${pageContext.request.contextPath}"/>
			</jsp:include>
		</table>
		<div id="divDatiRicerca" style="display: none;">
		<table class="dettaglio-notab" id="tabellaDatiRicerca">
			<tr >
				<td colspan="2">
					<b><br>Parametri di ricerca del fascicolo</b>
					<div style="display: none;" class="error" id="messaggioDatiRicerca"></div>
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Codice</td>
				<td class="valore-dato"><input id="codicefascicoloRic" name="codicefascicoloRic" title="Codice fascicolo" class="testo" type="text" size="47" value="" maxlength="100"></td>
			</tr>
			<tr>
				<td class="etichetta-dato">Classifica</td>
				<td class="valore-dato">
					<select id="classificafascicoloRic" name="classificafascicoloRic"></select>
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Oggetto</td>
				<td class="valore-dato">
					<textarea id="oggettofascicoloRic" name="oggettofascicoloRic" title="Oggetto fascicolo" class="testo" rows="4" cols="45"></textarea>
				</td>
			</tr>
			<tr >
				<td class="etichetta-dato">Struttura</td>
				<td class="valore-dato">
					<select id="strutturaRic" name="strutturaRic" style="max-width:450px"></select>
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Codice procedura</td>
				<td class="valore-dato"><input id="codiceproceduraRic" name="codiceproceduraRic" title="Codice procedura" class="testo" type="text" size="47" value="" maxlength="100"></td>
			</tr>
			<c:if test="${genere eq '1' or  genere eq '2' or genere eq '3'}">
			<tr >
				<td class="etichetta-dato">Cig</td>
				<td class="valore-dato"><input id="cigRic" name="cigRic" title="Cig" class="testo" type="text" size="47" value="" maxlength="100"></td>
			</tr>
			</c:if>
			<tr >
				<td colspan="2">
					<b><br>Selezione fascicolo</b>
				</td>
			</tr>
			<tr >
				<td class="etichetta-dato">Fascicolo</td>
				<td class="valore-dato"><select id="listafascicoliLapis" name="listafascicoliLapis" style="min-width:450px;max-width:450px"></select>
				<a href="javascript:gestioneletturafascicoliLapisopera();" id="linkleggifascicoliLapisopera" >Carica fascicoli</a>
				</td>
			</tr>
		</table>
		</div>	
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
	
	var fascicoloGiaAssociato="${fascicoloEsistente }";
	var codiceFascicoloOrig = "";
	
	function annulla(){
			window.close();
	}
	
	$("#pulsanteChiudi").hide();
	
	$(window).on("load", function (){
		_getWSTipoSistemaRemoto();
		_tipoWSDM = $("#tiposistemaremoto").val();
		if(_tipoWSDM == "LAPISOPERA" || _tipoWSDM == "ITALPROT"){
			$(".titolomaschera").text("Associa fascicolo documentale");
			$("#tipoOp").text("l'associazione");
		}
	});
	
	<c:if test="${controlloSuperato eq 'SI' }">
		$(window).on("load", function (){
			_associaCreaFunz=true;
			
				
			_popolaTabellato("ruolo","ruolo");
			_popolaTabellato("codiceuo","codiceuo");
			_getWSLogin();
			_gestioneWSLogin();
			_codiceGara = "${codiceGara }";
			_genereGara = "${genere }";
			if(_tipoWSDM == "LAPISOPERA"){
				window.resizeTo(800,800);
			}
			
			
			
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
			
							
			
			if(fascicoloGiaAssociato=="1"){
				_fascicoliPresenti=1;
			}else {
				_fascicoliPresenti=0;
			}
					
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
			codiceFascicoloOrig = $("#codicefascicolo").val();
						
			if(_tipoWSDM == "LAPISOPERA"){
				_popolaTabellato("classifica","classificafascicoloRic");
				_popolaTabellato("struttura","strutturaRic");
				$("#divDatiRicerca").show();
				$("#oggettofascicolonuovo").val("");
				$("#descrizionefascicolonuovo").val("");
			}
			
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
		    
		    $('#annofascicolo').change(function() {
				if (_tipoWSDM == "ITALPROT"){
					$('#listafascicoli').empty();
					$('#codicefascicolo').val(); 
				}
			});
		    
		    $('#classificafascicolonuovoItalprot').change(function() {
				$('#listafascicoli').empty();
				$('#codicefascicolo').val(); 
				
			});
		    
			$('#listafascicoli').on('change',  function () {
				var str = this.value;
				gestioneSelezioneFascicolo(str, _tipoWSDM);
				
			});
			
			$('#codicefascicoloRic').change(function() {
				sbiancaFascicolo();
			});
			
			$('#classificafascicoloRic').change(function() {
				sbiancaFascicolo();
			});
			
			$('#oggettofascicoloRic').change(function() {
				sbiancaFascicolo();
			});
			
			$('#strutturaRic').change(function() {
				sbiancaFascicolo();
			});
			
			$('#codiceproceduraRic').change(function() {
				sbiancaFascicolo();
			});
			
			$('#cigRic').change(function() {
				sbiancaFascicolo();
			});
			
			$('#listafascicoliLapis').on('change',  function () {
				var str = this.value;
				gestioneSelezioneFascicolo(str, _tipoWSDM);
				
			});
			
		});
		
		function sbiancaFascicolo(){
			$('#listafascicoliLapis').empty();
			$('#codicefascicolo').val('');
			$('#annofascicolo').val(''); 
			$("#classificafascicolonuovo").empty();
			$('#oggettofascicolonuovo').val('');
			$('#numerofascicolo').val('');
			$("#strutturaonuovo").empty();
			$("#struttura").text('');
			$('#classificafascicolodescrizione').text('');
			$('#oggettofascicolo').text('');
		}
		
		_gestioneWSLoginContratto(false);
		
		function conferma(){
			var tempo = 400;
			var errori = false;
			
			/*
			var codiceFascicoloOrig = $("#codicefascicolo").val();				
			var fascicoloGiaAssociato="${fascicoloEsistente }";
			if(fascicoloGiaAssociato=="1"){
			*/
			
			if(_tipoWSDM == "LAPISOPERA"){
				var codicefascicolo = $("#codicefascicolo").val();
				if(codicefascicolo==null || codicefascicolo == ""){
					errori=true;
					alert("Per procedere si deve selezionare un fascicolo");
				}
				if(fascicoloGiaAssociato=="1"){
					if(codiceFascicoloOrig!=codicefascicolo){
						if(!confirm("E' stato variato il fascicolo associato.\nProcedere ugualmente?"))
						 errori=true;
					}else{
						//Il fascicolo non è stato variato, quindi si può chiudere la popup senza fare nulla
						window.close();
					}
				}
			} else {
				errori = controlloCampiObbligatori();
			}
			
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
						codicefascicolo : $("#codicefascicolo").val(),
						oggettofascicolo : $("#oggettofascicolonuovo").val(),
						classificafascicolo : $("#classificafascicolonuovo").val(),
						classificadescrizione : $("#classificadescrizione").val(),
						tipofascicolo : $("#tipofascicolonuovo").val(),
						annofascicolo : $("#annofascicolo").val(),
						descrizionefascicolo : $("#descrizionefascicolonuovo").val(),
						struttura: $("#strutturaonuovo option:selected").val(),
						numerofascicolo : $("#numerofascicolo").val(),
						nomeRup: $("#nomeRup").text(),
						acronimoRup: $("#acronimoRup").text(),
						tipowsdm : _tipoWSDM,
						idconfi: $("#idconfi").val(),
						entita : $("#entita").val(),
						key1 : $("#key1").val(),
						servizio : $("#servizio").val(),
						genereGara : '${genere }',
						uocompetenza:$("#uocompetenza").val(),
						uocompetenzadescrizione:$("#uocompetenzadescrizione").val()
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
		
		function apriListaUffici() {
			_ctx = "${pageContext.request.contextPath}";
			$("#finestraListaUffici").dialog('option','width',700);
			$("#finestraListaUffici").dialog("open");
			_creaContainerListaUffici();
		}
	</c:if>
	
	</gene:javaScript>
	
</gene:template>

