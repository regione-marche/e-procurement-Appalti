<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${!empty param.codgar}'>
		<c:set var="codgar" value="${param.codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="codgar" value="${codgar}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.inviaComunicazione}'>
		<c:set var="inviaComunicazione" value="${param.inviaComunicazione}" />
	</c:when>
	<c:otherwise>
		<c:set var="inviaComunicazione" value="${inviaComunicazione}" />
	</c:otherwise>
</c:choose>

<c:set var="bustalotti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetBustalottiFunction", pageContext, "TORN.CODGAR=T:".concat(codgar))}' />

<c:choose>
	<c:when test="${bustalotti eq '1'}">
		<c:set var="key1" value="${codgar }"/>
	</c:when>
	<c:otherwise>
		<c:set var="key1" value="${ngara }"/>
	</c:otherwise>
</c:choose>

<c:set var="nonusato" value='${gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.GetDpubattiFromNgaraFunction", pageContext, ngara)}' />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<gene:template file="popup-message-template.jsp">
<c:choose>
	<c:when test="${(not empty dpubatti and not empty isIntegrazioneDocumenti) or (not empty dpubatti and empty requestScope.pubblicazioneEseguita)}">
		<gene:setString name="titoloMaschera" value='Integra su portale Appalti' />
	</c:when>
	<c:otherwise>
		<gene:setString name="titoloMaschera" value='Pubblica su portale Appalti' />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty requestScope.pubblicazioneEseguita and requestScope.pubblicazioneEseguita eq "1"}' >

		<gene:redefineInsert name="corpo">
		<br>
		&nbsp;&nbsp;&nbsp;Pubblicazione su portale Appalti completata.
		<br>&nbsp;
		<br>&nbsp;
			<script>
				window.opener.historyReload();
				window.close();
			</script>
		<gene:redefineInsert name="buttons">
			<input type="button" class="bottone-azione"  id="cancel" value="Chiudi" title="Chiudi" onclick="chiudi();"/>&nbsp;&nbsp;
		</gene:redefineInsert>
		<gene:javaScript>

		function chiudi(){
			window.opener.historyReload();
			window.close();
		}
		</gene:javaScript>
		</gene:redefineInsert>
	</c:when>
	<c:otherwise>

	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${contextPath}/js/jHtmlArea-0.7.5.min.js"></script> 
		<link rel="Stylesheet" type="text/css" href="${contextPath}/css/jquery/jHtmlArea/jHtmlArea.css" />
		<link rel="stylesheet" type="text/css" href="${contextPath}/css/jquery/wsdm/jquery.wsdm.css" >

		<script type="text/javascript" src="${contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
		<script type="text/javascript" src="${contextPath}/js/common-gare.js?v=${sessionScope.versioneModuloAttivo}"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmuffici.js?v=${sessionScope.versioneModuloAttivo}"></script>
	</gene:redefineInsert>

<c:set var="modo" value="NUOVO" scope="request" />
	
	<c:choose>
		<c:when test='${!empty param.entita}'>
			<c:set var="entita" value="${param.entita}" />
		</c:when>
		<c:otherwise>
			<c:set var="entita" value="${entita}" />
		</c:otherwise>
	</c:choose>

	<c:if test="${empty entita ||  entita==''}">
		<c:set var="entita" value="GARE" />
	</c:if>

	<c:if test="${empty dpubatti}">
		<c:choose>
			<c:when test='${fn:startsWith(codgar, "$")}'>
				<c:set var="valoreChiave" value="${fn:substringAfter(codgar,'$')}" />
			</c:when>
			<c:otherwise>
				<c:set var="valoreChiave" value="${codgar}" />
			</c:otherwise>
		</c:choose>
	
		<c:choose>
			<c:when test='${!empty param.integrazioneWSDM}'>
				<c:set var="integrazioneWSDM" value="${param.integrazioneWSDM}" />
			</c:when>
			<c:otherwise>
				<c:set var="integrazioneWSDM" value="${integrazioneWSDM}" />
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

		<c:set var="riservatezzaAttiva" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsRiservatezzaAttivaFunction", pageContext, key1, idconfi)}' scope="request"/>
	
		<c:choose>
			<c:when test='${!empty param.step}'>
				<c:set var="step" value="${param.step}" />
			</c:when>
			<c:otherwise>
				<c:set var="step" value="${step}" />
			</c:otherwise>
		</c:choose>
	
		<c:if test="${empty integrazioneWSDM}">
			<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar,idconfi)}' />
		</c:if>
	
		<c:if test="${integrazioneWSDM eq 1}" >
			<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext, idconfi)}'/>
		</c:if>	
		<c:set var="tabellatoA1178" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1178", "1", "true")}'/>
		<c:if test="${tabellatoA1178 eq '1'}" >
			<c:set var="isConcProg" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.CheckIsConcProgFunction", pageContext, codgar)}'/>
		</c:if>	
		<c:set var="itergaTORN" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select iterga from torn where codgar=?", codgar)}'/>
	
		<c:set var="chiaveTorn" value="TORN.CODGAR=T:${codgar}"/>
		<c:set var="tipologiaGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,chiaveTorn)}'/>
		<c:choose>
			<c:when test="${tipologiaGara eq '3' }" >
				<c:set var="offertaUnica" value="true" />
			</c:when>
			<c:otherwise>
				<c:set var="offertaUnica" value="false" />
			</c:otherwise>
		</c:choose>
	
		<c:set var="modelloMailPec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetMailPecModelloFunction", pageContext, "60","false",ngara)}' />
	</c:if>

	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="${entita }" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePubblicaAttiSuPortale" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePubblicaAttiSuPortale">

	<gene:campoScheda nome="msgPagina">
		<td colSpan="2">
			<c:choose>
				<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}' >
					<br>
					<c:choose>
						<c:when test="${not empty dpubatti}">
							<b>Non è possibile procedere all’integrazione dei documenti sul portale Appalti.</b>
						</c:when>
						<c:otherwise>
							<b>Non è possibile procedere alla pubblicazione su portale Appalti.</b>
						</c:otherwise>
					</c:choose>
					<br>
					${requestScope.msg }
					<br><br>
				</c:when>
				<c:otherwise>
					<br>
					<c:choose>
						<c:when test="${not empty dpubatti}">
							Mediante questa funzione si procede all'integrazione documenti degli atti e offerte della gara sul portale Appalti.
							<br><br>Confermi l’operazione?
						</c:when>
						<c:when test="${empty inviaComunicazione}">
							Mediante questa funzione si procede alla pubblicazione su portale Appalti degli atti e offerte della gara.
							<br><br>È possibile procedere inviando o meno una comunicazione alle ditte che hanno accesso agli atti.
							<br>Selezionare una delle opzioni sottostanti.
						</c:when>
						<c:otherwise>
							Confermi la pubblicazione su portale Appalti degli atti e offerte della gara?
							<c:choose>
							<c:when test="${inviaComunicazione eq '1'}">
								<br>Procedendo viene anche inviata una comunicazione di avviso alle ditte che hanno accesso agli atti.
							</c:when>
							<c:otherwise>
								<br><b>Procedendo nessuna comunicazione di avviso viene inviata alle ditte.</b>
							</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>

					<c:if test="${empty inviaComunicazione and empty dpubatti}">
					<table class="dettaglio-notab">
						<tr>
							<br><br>
							&nbsp;<input type="radio" name="inviaComunicazioneRadio" id="inviaComunicazioneRadio1" value="1" checked="checked" />&nbsp;Pubblicazione degli atti <b>con invio della comunicazione di avviso</b>
							<br><br>
							&nbsp;<input type="radio" name="inviaComunicazioneRadio" id="inviaComunicazioneRadio2" value="2" />&nbsp;Pubblicazione degli atti <b>senza invio della comunicazione di avviso</b>
							<br>
						</tr>
					</table>
					</c:if>

					<c:if test='${(not empty inviaComunicazione or not empty dpubatti) and requestScope.controlloSuperato eq "WARNING"}' >
						<span style="color:blue"><b><br><br>ATTENZIONE:</b> ${requestScope.msg}</span>
					</c:if>

					<br><br>
				</c:otherwise>
			</c:choose>
		</td>
	</gene:campoScheda>

		<gene:campoScheda campo="CODGAR" campoFittizio="true" defaultValue="${param.codgar}" visibile="false" definizione="T21;0"/>
		<gene:campoScheda campo="NGARA" campoFittizio="true" defaultValue="${param.ngara}" visibile="false" definizione="T20;0"/>
		<gene:campoScheda campo="LOTTODIGARA" campoFittizio="true" defaultValue="${param.lottoDiGara}" visibile="false" definizione="T1;0"/>

		<c:if test="${requestScope.controlloSuperato ne 'NO' and empty dpubatti}">
		<c:choose>
		<c:when test="${inviaComunicazione eq '1'}">
			<script>
				window.moveTo(300,150);
				window.resizeTo(900,600);
			</script>
			<gene:campoScheda nome="TitoloComunicazione">
				<td colspan="2"><b>Comunicazione pubblicazione atti e offerte</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="COMMSGOGG" campoFittizio="true" definizione="T300;0;;;COMMSGOGG" obbligatorio="true" value="${requestScope.oggettoMail}"/>
			<gene:campoScheda campo="COMINTEST" campoFittizio="true" definizione="T2;0;;SN;COMINTEST" value="${requestScope.abilitaIntestazioneVariabile}" visibile="${ abilitatoInvioMailDocumentale ne 'true' && itergaTORN ne '10'}"/>
			<gene:campoScheda nome="intestazione" visibile="${ abilitatoInvioMailDocumentale ne 'true' && itergaTORN ne '10'}"> 
				<td class="etichetta-dato">Intestazione variabile</td>
				<td class="valore-dato">Spett.le <i>Ragione Sociale</i>
				</td>
			</gene:campoScheda>
			<gene:campoScheda campo="COMMSGTIP" campoFittizio="true" definizione="T2;0;;SN;COMMSGTIP" defaultValue="2" visibile='${htmlSupport eq "1"}'/>
			<gene:campoScheda campo="COMMSGTES" campoFittizio="true" definizione="T2000;0;;CLOB;COMMSGTES" obbligatorio="true" value="${requestScope.testoMail}" gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoTestoComunicazioneHTML"/>
			<gene:campoScheda campo="COMMITT" campoFittizio="true" definizione="T60;0;;;COMMITT" value="${requestScope.mittenteMail}" visibile="${ abilitatoInvioMailDocumentale ne 'true' && itergaTORN ne '10'}"/>

			<gene:fnJavaScriptScheda funzione="modifyHTMLEditor('#COMMSGTIP#')" elencocampi="COMMSGTIP" esegui="false" />
			<c:if test="${ abilitatoInvioMailDocumentale ne 'true' && itergaTORN ne '10'}">
				<gene:fnJavaScriptScheda funzione="gestioneCOMINTEST('#COMINTEST#')" elencocampi="COMINTEST" esegui="true" />
			</c:if>

			<c:if test="${integrazioneWSDM =='1'}">
				<div id="divWSDM">
					<table class="dettaglio-notab" id="datiLogin">
						<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp"></jsp:include>
					</table>
					<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-internoDati.jsp">
						<jsp:param name="valoreChiaveRiservatezza" value="${key1}"/>
					</jsp:include>
				</div>
			</c:if>
		</c:when>
		<c:when test="${inviaComunicazione eq '2'}" >

		</c:when>
		</c:choose>
		</c:if>

		<input type="hidden" name="isProceduraTelematica" id="isProceduraTelematica" value="${isProceduraTelematica}" />
		<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="integrazioneWSDM" id="integrazioneWSDM" value="${integrazioneWSDM}" />
		<input type="hidden" name="gestioneFascicoloWSDM" id="gestioneFascicoloWSDM" value="" />
		<input type="hidden" name="idconfi" id="idconfi" value="${param.idconfi}" />
		<input type="hidden" name="integrazioneWSDM" id="integrazioneWSDM" value="${integrazioneWSDM}" />
		<input type="hidden" name="step" id="step" value="${step}" />
		<input name="servizio" id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
		<input name="syscon" id="syscon" type="hidden" value="${profiloUtente.id}" /> 
		<input name="tiposistemaremoto" id="tiposistemaremoto" type="hidden" value="" />
		<input name="tabellatiInDB" id="tabellatiInDB" type="hidden" value="" />
		<input name="entita" id="entita" type="hidden" value="${entita}" /> 
		<input name="idprg" id="idprg" type="hidden" value="PG" />
		<input id="isRiservatezzaAttiva" name="isRiservatezzaAttiva" type="hidden" value="${riservatezzaAttiva}" />
		<input id="key1" type="hidden" name="key1" value="${valoreChiave}" />
		<input id="key2" type="hidden" name="key2" value="" /> 
		<input id="key3" type="hidden" name="key3" value="" /> 
		<input id="key4" type="hidden" name="key4" value="" />
		<input id="nomeR" name="nomeR"  type="hidden" value="" />
		<input id="inviaComunicazione" name="inviaComunicazione" type="hidden" value="${inviaComunicazione}" />

		<input type="hidden" id="chiaveOriginale" value="${valoreChiave}" />
		<input id="abilitazioneGare" type="hidden" value="${profiloUtente.abilitazioneGare}" />
	</gene:formScheda>
  </gene:redefineInsert>

	<c:choose>
		<c:when test="${empty inviaComunicazione or not empty dpubatti}">
			<c:set var="testoBottone" value="Conferma"/>
		</c:when>

		<c:otherwise>
			<c:choose>
				<c:when test="${inviaComunicazione eq '2'}">
					<c:set var="testoBottone" value="Pubblica"/>
				</c:when>
				<c:otherwise>
					<c:set var="testoBottone" value="Invia comunicazione e pubblica"/>
					<c:if test="${integrazioneWSDM =='1'}">
						<c:set var="testoBottone" value="Protocolla comunicazione e pubblica"/>
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>

	<gene:redefineInsert name="buttons">
		<c:choose>
			<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}'>
				<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			</c:when>
			<c:when test=''>
				<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value="${testoBottone}" title="${testoBottone}" onclick="javascript:conferma()">&nbsp;
				<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			</c:when>
			<c:otherwise>
				<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value="${testoBottone}" title="${testoBottone}" onclick="javascript:conferma();">&nbsp;
				<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			</c:otherwise>
		</c:choose>
	</gene:redefineInsert>

	<gene:javaScript>

	var $datepicker = $('#DATPUB');
	$datepicker.datepicker();
	$datepicker.datepicker( "option", "dateFormat", "dd/mm/yy" );
	$datepicker.datepicker('setDate', new Date());
	$("#DATPUB").prop("disabled",true);

	<c:if test="${requestScope.controlloSuperato ne 'NO'}">
		if(${ integrazioneWSDM =='1'}){
			_getWSTipoSistemaRemoto();
			if (!($("#tiposistemaremoto").val() == 'LAPISOPERA')) {
	       		$('#inserimentoinfascicolo').change(function() {
					_gestioneInserimentoInFascicolo();
			    });
	
				$('#classificafascicolonuovo').change(function() {
					$('#classificadocumento').val($('#classificafascicolonuovo').val());
				});
	
			    $('#username').change(function() {
					if(_letturaMittenteDaServzio){
						$('#mittenteinterno').empty();
						_popolaTabellatoJirideMittente("mittenteinterno");
						if(_delegaInvioMailDocumentaleAbilitata == 1){
							$('#indirizzomittente').empty();
							_popolaTabellatoJirideMittente("indirizzomittente");
						}
					}
					caricamentoStrutturaJIRIDE();
				});
	
				$('#ruolo').change(function() {
					caricamentoStrutturaJIRIDE();
				});
	
		   		$('#listafascicoli').on('change',  function () {
					var str = this.value;
					gestioneSelezioneFascicolo(str);
					
				});
	
	       		var step= $("#step").val();
	       		if(step==1){
		       		//document.getElementById("rowTitoloComunicazione").style.display='none';
		           	//document.getElementById("composizione").style.display='none';
		           	//document.getElementById("tabellaNoInvioMail").style.display='';
		           	$("#datiLogin").hide();
					$("#datiProtocollo").hide();
	       		}
	       		caricamentoDati();
	       		_controlloDelegaInvioMailAlDocumentale();
       		} else {
       			$('#divWSDM').hide();
       			$('#pulsanteConferma').prop('title', 'Conferma');
       			$('#pulsanteConferma').val('Conferma');
       		}
         }

	</c:if>

	<c:if test="${integrazioneWSDM =='1'}">
	function caricamentoDati(){

		/*
	     * Gestione utente ed attributi per il collegamento remoto
	     */
	    //SE LAPISOPERA NON SI EFFETTUA PROTOCOLLAZIONE
		if ($("#tiposistemaremoto").val() == 'LAPISOPERA') {
			return;
		}
		_popolaTabellato("ruolo","ruolo");
		_popolaTabellato("codiceuo","codiceuo");
		_getWSLogin();
		_gestioneWSLogin();

		/*
		 * Gestione tabellati per richiesta protocollazione
		 */
		_popolaTabellato("classifica","classificadocumento");
		_popolaTabellato("codiceregistro","codiceregistrodocumento");
		_popolaTabellato("tipodocumento","tipodocumento");
		_popolaTabellato("mittenteinterno","mittenteinterno");
		_popolaTabellato("indirizzomittente","indirizzomittente");
		_popolaTabellato("mezzo","mezzoinvio");
		_popolaTabellato("mezzo","mezzo");
		_popolaTabellato("classificafascicolo","classificafascicolonuovo");
		_popolaTabellato("idindice","idindice");
		_popolaTabellato("idtitolazione","idtitolazione");
		_popolaTabellato("idunitaoperativamittente","idunitaoperativamittente");
		_popolaTabellato("livelloriservatezza","livelloriservatezza");
		_popolaTabellato("supporto","supporto");
		_popolaTabellato("sottotipo","sottotipo");
		_popolaTabellato("tipofirma","tipofirma");

		_getComunicazione();
		_controlloPresenzaFascicolazione();
		_controlloFascicoliAssociati();
		_inizializzazioni();
		//Se il campo classificafascicolonuovo contiene un solo valore, si riporta
		//in classificadocumento tale valore
		if($("#classificafascicolonuovo option").length == 2 ){
			$('#classificadocumento').val($('#classificafascicolonuovo').val());
		}
	}

	function inizializzazionePagina(step){
       	if(step=="1"){
			$("#datiLogin").hide();
			$("#datiProtocollo").hide();
			$("#RigaTipoMail").show();
			$("#TipoMail").show();
			$(".pagebanner").show();
			$(".datilista").show();
			$("#composizione").show();
			$("#CampiTabellaInvioMail").show();
			$("#oggetto").show();
			$("#valoreOggetto").show();
			$("#intestazione").show();
			$("#valoreIntestazione").show();
			$("#testoMail").show();
			$("#valoreTestoMail").show();
			$("#mittente").show();
			$("#valoreMittente").show();
			if(_delegaInvioMailDocumentaleAbilitata == 1){
				$("#TipoMail").hide();
			}
			$("#rowCOMMSGTIP").show();	
		}else{
			$("#datiLogin").show();
			$("#datiProtocollo").show();
			$("#TipoMail").hide();
			$("#RigaTipoMail").hide();
			$(".pagebanner").hide();
			$(".datilista").hide();
			$("#composizione").hide()
			$("#oggetto").hide();
			$("#valoreOggetto").hide();
			$("#intestazione").hide();
			$("#valoreIntestazione").hide();
			$("#testoMail").hide();
			$("#valoreTestoMail").hide();
			$("#mittente").hide();
			$("#valoreMittente").hide();

			if ($("#tiposistemaremoto").val() == 'TITULUS') {
				$("#oggettodocumento").val($("#COMMSGOGG").val() + $("#oggettodocumento").val());
			} else {
				$("#oggettodocumento").val($("#COMMSGOGG").val());
			}
			if (_tipoWSDM == "ARCHIFLOWFA" ){
				var testoOggettoDocumento= "${valoreChiave} - " + $('#oggettodocumento').val() +" - " + _oggettoGara;
				$("#oggettodocumento").val(testoOggettoDocumento);
			}else if (_tipoWSDM == "ENGINEERINGDOC" ){
				var testoOggettoDocumento= "${valoreChiave} - " + $('#oggettodocumento').val();
				$("#oggettodocumento").val(testoOggettoDocumento);
			}
			if ($("#tiposistemaremoto").val() == "ARCHIFLOW" ){
				$("#nomeR").val($("#nomeRup").text());
			}

			$("#rowmsgPagina").hide();
			$("#rowTitoloComunicazione").hide();
			$("#rowCOMMSGOGG").hide();
			$("#rowCOMINTEST").hide();
			$("#rowintestazione").hide();
			$("#rowCOMMSGTES").hide();
			$("#rowCOMMITT").hide();
			$("#rowsoggettoDestinatario").hide();
			$("#rowCOMMSGTIP").hide();
			$("#tr1").hide();
		}
	}

	/*
	 * Lettura della singola comunicazione.
	 * Utilizzata per popolare i dati generali della comunicazione,
	 * la lista dei destinatari e la lista degli allegati.
	 */
	function _getComunicazione() {
		var servizio = $("#servizio").val();
		_getTipoWSDM();
		_controlloDelegaInvioMailAlDocumentale();

		if (_tipoWSDM == "IRIDE" ||_tipoWSDM == "JIRIDE") {
			$("#codiceregistrodocumento").hide();
			$("#codiceregistrodocumento").closest('tr').hide();
			$("#idindice").hide();
			$("#idindice").closest('tr').hide();
			$("#idtitolazione").hide();
			$("#idtitolazione").closest('tr').hide();
			$("#idunitaoperativamittente").hide();
			$("#idunitaoperativamittente").closest('tr').hide();
		}
		
		if (_tipoWSDM == "PALEO") {
			$("#classificadocumento").hide();
			$("#classificadocumento").closest('tr').hide();
			$("#tipodocumento").hide();
			$("#tipodocumento").closest('tr').hide();
			$("#mittenteinterno").hide();
			$("#mittenteinterno").closest('tr').hide();
			$("#idindice").hide();
			$("#idindice").closest('tr').hide();
			$("#idtitolazione").hide();
			$("#idtitolazione").closest('tr').hide();
			$("#idunitaoperativamittente").hide();
			$("#idunitaoperativamittente").closest('tr').hide();
			$("#indirizzomittente").hide();
			$("#indirizzomittente").closest('tr').hide();
		}

		if (_tipoWSDM == "ENGINEERING") {
			$("#classificadocumento").hide();
			$("#classificadocumento").closest('tr').hide();
			$("#codiceregistrodocumento").hide();
			$("#codiceregistrodocumento").closest('tr').hide();
			$("#tipodocumento").hide();
			$("#tipodocumento").closest('tr').hide();
			$("#mittenteinterno").hide();
			$("#mittenteinterno").closest('tr').hide();
			$("#mezzoinvio").hide();
			$("#mezzoinvio").closest('tr').hide();
			$("#indirizzomittente").hide();
			$("#indirizzomittente").closest('tr').hide();
		}
		
		if ((_tipoWSDM != "JIRIDE"  && _tipoWSDM != "ARCHIFLOW" && _tipoWSDM != "ARCHIFLOWFA" && _tipoWSDM != "HALLEY") || ((_tipoWSDM == "JIRIDE" || _tipoWSDM == "ARCHIFLOW" || _tipoWSDM == "ARCHIFLOWFA" ||  _tipoWSDM == "HALLEY") && _delegaInvioMailDocumentaleAbilitata != 1)) {
			$("#indirizzomittente").hide();
			$("#indirizzomittente").closest('tr').hide();
			$('#indirizzomittente option').eq(0).prop('selected', true);
		}
		
		if (_tipoWSDM != "IRIDE" && _tipoWSDM != "JIRIDE" && _tipoWSDM != "TITULUS") {
			$("#mezzoinvio").hide();
			$("#mezzoinvio").closest('tr').hide();
		}else if(_tipoWSDM == "TITULUS"){
            //Per TITULUS il campo non ? obbligatorio
            $("#obblmezzoinvio").hide();
        }
        
        if(_tipoWSDM != "ARCHIFLOW" && _tipoWSDM != "ARCHIFLOWFA" && _tipoWSDM != "JDOC" && _tipoWSDM != "NUMIX"){
           	$("#mezzo").hide();
			$("#mezzo").closest('tr').hide();
        }
		
		if(_tipoWSDM != "JIRIDE"){
			$("#livelloriservatezza").closest('tr').hide();
        }
		
		if(_delegaInvioMailDocumentaleAbilitata == 1){
			setValue("COMINTEST","2");
			//showObj("rowCOMINTEST",false);
			setValue("COMMITT","");
			//showObj("rowCOMMITT", false);
		}

		if ( _delegaInvioMailDocumentaleAbilitata != 1)  {
			$("#indirizzomittente").hide();
			$("#indirizzomittente").closest('tr').hide();
			$('#indirizzomittente option').eq(0).prop('selected', true);
		}

		if($("#inserimentoinfascicolo option:selected").val() == "SI_FASCICOLO_ESISTENTE")
		_setDescrizioneCodiceTabellato("classificafascicolo",$("#classificafascicolonuovo").val(),"classificafascicolodescrizione",2);
	}
	</c:if>

	function gestioneCOMINTEST(comintest){
		document.getElementById("rowintestazione").style.display = (comintest=='1' ? '':'none');
	}

	function conferma() {
		if (${not empty inviaComunicazione or not empty dpubatti}) {
			if(${inviaComunicazione eq '1'} && ${integrazioneWSDM =='1'} && !($("#tiposistemaremoto").val() == 'LAPISOPERA')) {
				var step= $("#step").val();
				if(step==1){
					$("#step").val("2");
					inizializzazionePagina("2");
					return;
				}else{
					//Controlli sulla valorizzazione dei campi obbligatori
					var errori = controlloCampiObbligatori();
					if(errori) {
						return;
					}
					_setWSLogin();
				}
			}
			document.forms[0].jspPathTo.value="gare/commons/popupPubblicaAttiSuPortale.jsp";
			document.forms[0].step.value='1';
			schedaConferma();
		} else {
			var idconfi = "${param.idconfi}";
			var inviaComunicazione = '';
			if($("#inviaComunicazioneRadio1").prop('checked')) {
				inviaComunicazione = 1;
			} else if($("#inviaComunicazioneRadio2").prop('checked')) {
				inviaComunicazione = 2;
			} else {
				alert("Selezionare almeno un opzione");
				return;
			}

			var codgar = "${codgar}";
			var ngara = "${ngara}";
			var integrazioneWSDM = "${integrazioneWSDM}";
			var href = "href=gare/commons/popupPubblicaAttiSuPortale.jsp?codgar="+codgar+"&ngara="+ngara+"&idconfi="+idconfi+"&step=1";
			if(integrazioneWSDM =='1'){
			var entita="GARE";
			if(codgar.indexOf('$')!=0 )
			entita="TORN";
			href += "&entita=" + entita;
			}
			var ref = "${pageContext.request.contextPath}/ApriPagina.do?href=gare/commons/popupPubblicaAttiSuPortale.jsp?codgar="+codgar+"&ngara="+ngara+"&idconfi="+idconfi+"&step=1"+"&inviaComunicazione="+inviaComunicazione;
			document.location.href = ref;
		}
	}
	function annulla(){
		window.close();
	}

	</gene:javaScript>
	</c:otherwise>
</c:choose>
</gene:template>