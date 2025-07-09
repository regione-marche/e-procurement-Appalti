<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="idForm" value="${param.idForm}" />

<c:set var="sql" value="select eform from g1npaform,g1cf_npa where g1npaform.idcfnpa=g1cf_npa.id and g1npaform.id=?"/>
<c:set var="eform" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, idForm)}'/>

<c:set var="propertyFeuUrl" value="feu.url" />
<c:set var="feuUrl" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", propertyFeuUrl)}'/>

<div id="mascheraGeneraConFEU">

	<div id="sezioneIniziale">
            Questa funzione prepara ed invia a FEU i dati della gara per il successivo controllo e produzione dell'eForm <b>${eform}</b>.<br><br>

			Una volta completato l'eForm in FEU è possibile importare il relativo XML nella scheda ANAC da cui è stato generato.

        <p><br>
            <b>Credenziali (utente e password) per la connessione a FEU</b>
        </p>
        <div class="radio-group">
            <input type="radio" name="credenziali" id="credenzialiCorrent" value="CORRENTI" checked="checked"> Credenziali correnti <br>
            <input type="radio" id="customOption" name="credenziali" value="ALTRE"> Altre credenziali <br>
        </div>
        <div class="input-group" id="input-group">
        	<table>
        		<tr>
        			<td class="etichette-modale">Username:</td>
        			<td class="valore-dato"><input type="text" id="username" name="username" disabled></td>
        		</tr>
        		<tr>
        			<td class="etichette-modale">Password:</td>
        			<td class="valore-dato"><input type="password" id="password" name="password" disabled></td>
        		</tr>
        	</table>
        </div>

        <p><br>
            <b>Operazione da effettuare</b>
        </p>
        <div class="radio-group">
		    <input type="radio" id="radioInviaBandoAvviso" name="operazione" value="controllaInvia" checked="checked"> Invio dati <br>
		    <input type="radio" id="radioLeggiXML" value="leggi" name="operazione"> Lettura eForm <br>
		    <input type="radio" id="radioAccediFEU" value="accediFEU" name="operazione"> Accedi a FEU <br>
		</div>

		<br>
		L'operazione <b>Invio dati</b> può essere effettuata una sola volta.<br>
		Per importare l'XML dell'eForm compilato in FEU, selezionare l'operazione <b>Lettura eForm</b>. Alla conferma, il file XML verrà importato nella relativa scheda.

			<span id="eformPresente" style="display: none; color: blue;"><br><br><b>ATTENZIONE:</b> è già associato un eForm alla scheda, procedendo verrà eliminato.</span>

    </div>

    <div id="sezioneErrore">
        <span><b>Si è verificato un errore durante l'operazione:</b></span><br><br>
        <span id="msgEsitoErrore">Errore inaspettato, controllare il log per i dettagli</span>
    </div>

    <div id="sezioneFinale">

        <div id="esitoControllo">
            <div id="erroriBloccanti">
                <b>Non è possibile procedere all'invio dei dati per i seguenti motivi:</b><br><br>
                <ul id="listaErroriBloccanti"></ul>
            </div>
            <div id="erroriNonBloccanti">
                <b>L'invio dei dati non è completo per i seguenti motivi:</b><br><br>
                <ul id="listaErroriNonBloccanti" class="custom-ul"></ul>
            </div>
            <div id="inviaComunque">
                <b>Vuoi proseguire con l'invio dei dati?</b>
            </div>
        </div>
        <div id="inviatoConSuccesso">
            I dati della gara sono stati inviati a <b>FEU</b>.
            <br>
        </div>
        <div id="esitoControlloXML">
        	<div id="impossibileProseguire">
            	<b>Non è possibile procedere all'upload dell'eForm.</b>
            </div>
            <div id="validazioneXML">
            </div>
            <div id="salvaXML">
            	<b>Vuoi proseguire con l'upload dell'eForm?</b>
            </div>
        </div>
		<div id="importatoConSuccesso">
            Upload dell'eForm completato.
            <br>
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

        #sezioneErrore, #inviatoConSuccesso, #importatoConSuccesso, #validazioneXML, #salvaXML, #impossibileProseguire {
            padding: 5px;
            display: none;
        }

        #erroriBloccanti {
            background-color: #f8d7da;
            color: #721c24;;
            padding: 15px;
            border-radius: 5px;
            display: none;
            border: 1px solid #f5c6cb;
        }
        
        #erroriNonBloccanti {
            color: #856404;
		    background-color: #fff3cd;
		    border-color: #ffeeba;
            padding: 15px;
            border-radius: 5px;
            display: none;
        }

        #inviaComunque {
            display: none;
            margin-top: 10px;
            padding-left: 5px;
        }

		.radio-group {
			margin-bottom: 10px;
		}

        .input-group {
            width: 92%;
            margin-left: 4%;
            display: none;
        }

        .etichette-modale {
        	font: 11px Verdana, Arial, Helvetica, sans-serif;
        }

        label {
            margin-bottom: 5px;
            display: block;
        }

        input[type="text"],
        input[type="password"] {
            width: 100%;
        }

        .custom-ul {
            list-style-type: none;
            margin: 0;
            padding: 0;
        }

        .custom-ul .custom-li {
            margin-bottom: 5px;
        }
    </style>

<gene:javaScript>

function apriModaleFEU() {

	pingSessioneAttiva();

	var titolo="Gestisci eForm con FEU";

	var widthM= 625;
	var	heightM= 525;

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
					gestisciConFEU();
				}
			 },
			"Annulla": {
				id:"botAnnulla",
				text:"Annulla",
				click:function() {
					controllato = false;
					$( this ).dialog( "close" );
				}
			},
			"Chiudi": {
				id:"botChiudi",
				text:"Chiudi",
				click:function() {
					controllato = false;
					historyReload();
				}
			}
		}
	};
	$("#mascheraGeneraConFEU").dialog(opt).dialog("open");
	$("#mascheraGeneraConFEU").parent().css("position","fixed");
	$("#botChiudi, #sezioneErrore, #sezioneFinale, #erroriBloccanti, #erroriNonBloccanti, #inviatoConSuccesso").hide();
	$("#sezioneIniziale").show();
	$('#listaErroriBloccanti').children().remove();
	$('#listaErroriNonBloccanti').children().remove();
	if (${param.isPresenteDocumentoEFORM} && $('input[name="operazione"]:checked').val() == 'controllaInvia') {
		$('#eformPresente').show();
	}
	controllato = false;
	controllatoXML = false;
}

var controllato = false;
var controllatoXML = false;

$("input[name='credenziali']").on("change", function() {
	if ($("#customOption").prop("checked")) {
		$("#username, #password").prop("disabled", false);
		$("#input-group").show();
	} else {
		$("#input-group").hide();
		$("#username, #password").prop("disabled", true);
	}
});

function gestisciConFEU() {
	var operazione = $('input[name="operazione"]:checked').val();
	if (operazione == 'accediFEU') {
		accediConFEU();
	} else {
		controllaInviaXML(operazione);
	}
}

function accediConFEU() {

	var credenziali = $('input[name="credenziali"]:checked').val();
	var username = $('#username').val();
	var password = $('#password').val();
	formLoginFEU.credenziali.value = credenziali;
	if (credenziali == 'ALTRE') {
		if (username == '' || password == '') {
			alert('Inserire username e password');
			return;
		}
		formLoginFEU.username.value = username;
		formLoginFEU.password.value = password;
	}
	$("#botConferma, #botAnnulla").hide();
	if ('${feuUrl}' == '') {
		$("#sezioneFinale").show();
		$("#sezioneIniziale").hide();
		$("#sezioneErrore").show();
		$("#botConferma").hide();
		$("#botAnnulla").hide();
		$("#botChiudi").show();
		$("#msgEsitoErrore").html('Valorizzare la property "${propertyFeuUrl}"');
		return;
	}
	formLoginFEU.submit();
	$("#botChiudi").click();
}

function controllaInviaXML(operazione) {

	var credenziali = $('input[name="credenziali"]:checked').val();
	var username = $('#username').val();
	var password = $('#password').val();
	if (credenziali == 'ALTRE') {
		if (username == '' || password == '') {
			alert('Inserire username e password');
			return;
		}
	}
	_waitgperm();
	var uuid_eform_originale = $('#G1NPASCHEDA_UUID_EFORM_ORIGINALE').val();

	var success = false;
	var erroriBloccanti = null;
	var erroriNonBloccanti = null;
	var eformsValidation = null;
	var eformsXML = null;
	var idAvviso = null;
	$("#erroriNonBloccanti, #inviaComunque").hide();
	$.ajax({
		type: "POST",
		dataType: "json",
		data: {
			username: username
			, password: password
			, credenziali: credenziali
			, codgar: "${param.codgar}"
			, idForm: "${param.idForm}"
			, controllato: controllato
			, controllatoXML: controllatoXML
			, operazione: operazione
			, uuid_eform_originale: uuid_eform_originale
			, numeroRettifiche: '${param.numeroRettifiche}'
		},
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "${contextPath}/pg/ControllaInviaFEUAJAX.do",
		error: function(){
			//alert("Errore inaspettato durante l'operazione, controllare il log per i dettagli");
			$("#sezioneErrore").show();
		},
		success: function(res){
			esito=res.Esito;
			msgEsito = res.MsgErrore;
			idAvviso = res.idAvviso;
			if (!(controllato) && operazione == 'controllaInvia'){
				erroriBloccanti = res.erroriBloccanti;
				erroriNonBloccanti = res.erroriNonBloccanti;
			} else if (operazione == 'leggi') {
				eformsValidation = res.eformsValidate;
				eformsXML = res.eformsXML;
			}
		},
		complete: function(jqXHR, textStatus) {
			$("#sezioneFinale").show();
			$("#sezioneIniziale").hide();
			if(textStatus=="error" || textStatus=="parsererror"){
				$("#sezioneErrore").show();
				$("#botConferma").hide();
				$("#botAnnulla").hide();
				$("#botChiudi").show();
				$("#msgEsitoErrore").html(msgEsito);
            }else{
            	if(esito=="Errore"){
					$("#botConferma, #botAnnulla").hide();
					$("#msgEsitoErrore").html(msgEsito);
					$("#sezioneErrore, #botChiudi").show();	
				}else{
					console.log(controllato);
					console.log(operazione);
					console.log(!(controllato) && operazione == 'controllaInvia');
					if (!(controllato) && operazione == 'controllaInvia') {
						if (erroriBloccanti !=null || erroriNonBloccanti != null){
							var listaErroriBloccanti = document.getElementById('listaErroriBloccanti');
							for (var i = 0; i < erroriBloccanti.length; i++) {
							    var listItem = document.createElement('li');
							    listItem.classList.add('custom-li');
							    listItem.textContent = erroriBloccanti[i];
							    listaErroriBloccanti.appendChild(listItem);
							}
							var listaErroriNonBloccanti = document.getElementById('listaErroriNonBloccanti');
							for (var i = 0; i < erroriNonBloccanti.length; i++) {
							    var listItem = document.createElement('li');
							    listItem.classList.add('custom-li');
							    listItem.textContent = erroriNonBloccanti[i];
							    listaErroriNonBloccanti.appendChild(listItem);
							}

							controllato = true;

							if (erroriBloccanti.length > 0) {
								$("#botConferma").hide();
								$("#botAnnulla").hide();
								$("#botChiudi").show();
								$("#erroriBloccanti").show();
							}
							if (erroriNonBloccanti.length > 0) {
								$("#erroriNonBloccanti").show();
								if (erroriBloccanti.length === 0) {
									$("#inviaComunque").show();
								}
							}
						} else {
							appendLinkAccediFEU(idAvviso);
						}
					} else if (controllato  && operazione == 'controllaInvia') {
						appendLinkAccediFEU(idAvviso);
					} else if (!(controllatoXML) && operazione == 'leggi'){
						var numeroErrori = eformsValidation.numeroErrori;
						var numeroWarning = eformsValidation.numeroWarning;
						var messaggioValidazione = "Durante il controllo dei dati sono stati rilevati ";
						if (numeroErrori > 0) {
							messaggioValidazione += "<b>" + numeroErrori + "</b> errori bloccanti";

							$('#botConferma, #botAnnulla').hide();
							$('#botChiudi, #impossibileProseguire').show();
						} else {
							$('#salvaXML').show();
							controllatoXML = true;
						}

						if (numeroWarning > 0) {
							if (numeroErrori > 0) {
								messaggioValidazione += " e ";
							}
							messaggioValidazione +="<b>" + numeroWarning + "</b> avvisi";
						}
						messaggioValidazione += ".<br><br> Attivare la funzione 'Controlla dati' in FEU per consultare il dettaglio degli errori e avvisi rilevati per l'eForm.";
						if (numeroWarning > 0 || numeroErrori > 0) {
							$('#validazioneXML').html(messaggioValidazione);
						} else {
							$('#validazioneXML').remove();
							$('#salvaXML').html("Confermi l'upload dell'eForm nella scheda?");
						}
						$('#validazioneXML').show();
					} else if (controllatoXML && operazione == 'leggi'){
						$("#botConferma, #botAnnulla, #esitoControlloXML").hide();
						$("#botChiudi, #importatoConSuccesso").show();
					}
				}
            }
			_nowaitgperm();
		}
	});
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

function appendLinkAccediFEU(idAvviso) {
	$("#botConferma, #botAnnulla, #inviaComunque").hide();

	$("#inviatoConSuccesso").append($("<span>").html("<br>"));
	$("#inviatoConSuccesso").append($("<span>").html("È stato generato l'avviso con id:<b>" + idAvviso + "</b>"));

	$("#inviatoConSuccesso").append($("<span>").html("<br><br>"));
	$("#inviatoConSuccesso").append($("<a>").html("Accedi a <b>FEU</b>.").attr("href","javascript:accediConFEU();"));

	$("#botChiudi, #inviatoConSuccesso").show();
}

</gene:javaScript>
