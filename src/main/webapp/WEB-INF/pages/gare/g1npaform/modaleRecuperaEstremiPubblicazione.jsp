<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test="${param.genereGara eq '10'}">
		<c:set var="dicituraPaginaPubblicazioni" value="Pubblicità elenco"/>
	</c:when>
	<c:when test="${param.genereGara eq '11'}">
		<c:set var="dicituraPaginaPubblicazioni" value="Pubblicità avviso"/>
	</c:when>
	<c:when test="${param.genereGara eq '20'}">
		<c:set var="dicituraPaginaPubblicazioni" value="Pubblicità catalogo"/>
	</c:when>
	<c:otherwise>
		<c:set var="dicituraPaginaPubblicazioni" value="Dati procedimento"/>
	</c:otherwise>
</c:choose>

<c:if test="${not empty gene:callFunction('it.eldasoft.gene.tags.functions.GetPropertyFunction', 'it.eldasoft.bandoavvisosimap.ws.url') && not empty param.uuid_eform and not empty param.iddoc_eform}">
	<c:set var="dicituraInizialeFEU" value=" e aggiornare lo stato dell'eForm su <b>FEU</b>"/>
</c:if>

<div id="mascheraGeneraConFEU">

	<div id="sezioneIniziale">
		Tramite questa funzione è possibile recuperare gli estremi delle pubblicazioni dai servizi ANAC${dicituraInizialeFEU}.
		<br><br>
		Confermi l'operazione?
	</div>

	<div id="sezioneErrore">
		<span><b>Si è verificato un errore durante l'operazione:</b></span><br><br>
		<span id="msgEsitoErrore">Errore inaspettato, controllare il log per i dettagli</span>
	</div>

	<div id="sezioneFinale">

		<div id="operazioneCompletata">
			Operazione completata correttamente.<br>
		</div>

		<div id="pubblicazioneEU">
			<b>Pubblicazione GUUE:</b><br>
		</div>

		<div id="pubblicazioneIT">
			<b>Pubblicazione PVL:</b><br>
		</div>
		<div id="consultaPubblicazioni">
			<br>Si possono consultare gli estremi delle pubblicazioni nella pagina ${dicituraPaginaPubblicazioni}.<br>
        </div>
	</div>

</div>

<style>

	#mascheraGeneraConFEU {
		display: none;
		padding: 20px;
		font-size: 11px;
		font: 11px Verdana, Arial, Helvetica, sans-serif;
	}

	#sezioneErrore, #inviatoConSuccesso, #pubblicazioneEU, #pubblicazioneIT, #consultaPubblicazioni, #esitoFEU, #operazioneCompletata {
		padding: 5px;
		display: none;
	}

</style>

<gene:javaScript>

function apriModaleRecuperaEstremiPubblicazione() {

	pingSessioneAttiva();

	var titolo="Recupera estremi pubblicazione";

	var widthM= 500;
	var	heightM= 375;

	var opt = {
		open: function(event, ui) {
			$(this).parent().children().children('.ui-dialog-titlebar-close').hide();
			$(this).parent().css("border-color","#C0C0C0");
			var _divtitlebar = $(this).parent().find("div.ui-dialog-titlebar");
			_divtitlebar.css("border","0px");
			_divtitlebar.css("background","#FFFFFF");
			var _dialog_title = $(this).parent().find("span.ui-dialog-title");
			_dialog_title.css("font-size","13px");
			_dialog_title.css("font-weight","bold");
			_dialog_title.css("color","#002856");
			$(this).parent().find("div.ui-dialog-buttonpane").css("background","#FFFFFF");
		},
		autoOpen: false,
		modal: true,
		width: widthM,
		height: heightM,
		draggable: false,
		resizable: false,
		title: titolo,
		position: { using: function( pos ) {
            $( this ).css({
				top:'50%',
				left: '50%',
				marginRight: '50%',
				transform: 'translate(-50%, -50%)'
			});
		}},
		buttons: {
			"Seleziona": {
				id:"botConferma",
				text:"Conferma",
				click: function() {
					gestioneAppaltoModale('CONSULTA_AVVISO');
				}
			 },
			"Annulla": {
				id:"botAnnulla",
				text:"Annulla",
				click:function() {
					$( this ).dialog( "close" );
				}
			},
			"Chiudi": {
				id:"botChiudi",
				text:"Chiudi",
				click:function() {
					historyReload();
				}
			}
		}
	};
	$("#mascheraGeneraConFEU").dialog(opt).dialog("open");
	$("#mascheraGeneraConFEU").parent().css("position","fixed");
	$("#botChiudi, #sezioneErrore, #inviatoConSuccesso, #pubblicazioneEU, #pubblicazioneIT").hide();
	$("#sezioneIniziale").show();
}

var notEmptyUUID = '${(not empty gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.bandoavvisosimap.ws.url")) && not empty param.uuid_eform and not empty param.iddoc_eform}';
var notEmptyEFORM = '${not empty param.eform}';

function gestioneAppaltoModale(servizio,tipoOperazione,tipoRicerca,esito,motivo){

	var esitoFEU = null;
	_waitgperm();
	setTimeout(() => {
		$.ajax({
			type: "POST",
			dataType: "json",
			url: "${contextPath}/pg/ScambioDatiPCP.do?metodo=gestioneAppalto",
			data: {
				codiceGara: "${param.codiceGara}",
				idForm: $("#G1NPASCHEDA_IDFORM").val(),
				servizio: servizio,
				tipoOperazione: tipoOperazione,
				tipoRicerca: tipoRicerca,
				esito: esito,
				motivo: motivo
			},
			async: false,
			success: function(json) {
				successoGestioneAppaltoModale(json,servizio);
			},
			complete: function() {
				$("#botConferma, #botAnnulla, #sezioneIniziale").hide();
				_nowaitgperm();
			}
		});
	},150);
}

function successoGestioneAppaltoModale(json,servizio) {
	var pgErrors = json.pgErrors;
	var sendValidationFailed = json.validationFailedFEU;
	var statoPubbEU = json.statoPubbEU;
	var statoPubbIT = json.statoPubbIT;
	var navpub = json.navpub;

	if (pgErrors != null) {
		$("#msgEsitoErrore").html(pgErrors);
		$("#sezioneErrore, #botChiudi").show();
	} else {

		$("#sezioneFinale, #botChiudi, #operazioneCompletata").show();

		if (statoPubbEU != "PUBB" && notEmptyEFORM == 'true') {
			$('#pubblicazioneEU').show();
			$('#pubblicazioneEU').append($("<span>").html('La scheda è in attesa di pubblicazione.<br>Riprovare più tardi per completare l\'operazione.'));
		} else if (statoPubbEU == "PUBB" && notEmptyEFORM == 'true') {
			$('#pubblicazioneEU').show();
			$('#pubblicazioneEU').append($("<span>").html('La scheda è pubblicata.'));
		}

		if (statoPubbIT != "PUBB") {
			$('#pubblicazioneIT').show();
			$('#pubblicazioneIT').append($("<span>").html('La scheda è in attesa di pubblicazione.<br>Riprovare più tardi per completare l\'operazione.'));
		} else if (statoPubbIT == "PUBB") {
			$('#pubblicazioneIT').show();
			$('#pubblicazioneIT').append($("<span>").html('La scheda è pubblicata.'));
		}
		
		if (statoPubbIT == "PUBB" || statoPubbEU == "PUBB") {
			$('#consultaPubblicazioni').show();
		}

		if (notEmptyUUID == 'true' && statoPubbEU == "PUBB" && notEmptyEFORM == 'true') {
			esitoFEU = gestisciInvioModale(servizio,'after', navpub);
			if (!esitoFEU[0]) {
				if (esitoFEU[1] == "Il bando/avviso indicato e' gia' in stato PUBLISHED o STOPPED") {
					$('#pubblicazioneEU').append($("<span>").html("<br>Dati già inviati a <b>FEU</b>."));
				} else {
					$('#pubblicazioneEU').append($("<span>").html("<br>Errore nell'invio dei dati a <b>FEU</b>:<br>" + esitoFEU[1]));
				}
			} else {
				$('#pubblicazioneEU').append($("<span>").html('<br>I dati della pubblicazione sono stati inviati a <b>FEU</b>.'));
			}
			$('#esitoFEU').show();
		}
	}
}

function gestisciInvioModale(servizio, tempoChiamata, navpub) {

	var esito = '';
	var msgEsito = '';
	var metodo = '';
	var esitoFEU = false;
	if (tempoChiamata == 'after' && servizio == 'CONSULTA_AVVISO') {
		metodo = 'PUBBLICAZIONE_SCHEDA';
	} else {
		return [true,''];
	}
	$.ajax({
		type: "POST",
		dataType: "json",
		data: {
			idForm: $("#G1NPASCHEDA_IDFORM").val(),
			metodo: metodo,
			navpub: navpub
		},
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "${contextPath}/pg/GestisciInvioFEUAJAX.do",
		error: function(){
			esitoFEU = [false,"Errore inaspettato durante l'operazione, controllare il log per i dettagli"];
		},
		success: function(res){
			esito=res.Esito;
			msgEsito = res.MsgErrore;
		},
		complete: function(jqXHR, textStatus) {
			if(textStatus=="error" || textStatus=="parsererror"){
				esitoFEU = [false,msgEsito];
            }else{
            	if(esito!="Errore"){
					esitoFEU = [true,''];
				}
            }
		}
	});
	return [esitoFEU,msgEsito];
}

function _waitgperm() {
	document.getElementById('bloccaScreen').style.visibility='visible';
	document.getElementById('wait').style.visibility='visible';
	$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
}

function _nowaitgperm() {
	var timeout = null;
	timeout = setInterval(function() {
		document.getElementById('bloccaScreen').style.visibility='hidden';
		document.getElementById('wait').style.visibility='hidden';
		clearTimeout(timeout);	
     }, 50);
}

</gene:javaScript>
