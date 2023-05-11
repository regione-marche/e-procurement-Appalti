/*
 * Gestione dei documenti di gara
 * 
 */

/*
 * Genera un documento pdf da modello e lo allega
 * @param {string} ngara il numero di gara
 * @param {string} indice l'indice del documento nella lista documenti della gara in esame
 * @param {string} idstampa identificativo del tipo di documento da generare (CODICE-CLIENTE + "_" + IDENTIFICATIVO-DOCUMENTO)
 * @param {string} contextPath il context path della pagina
 * @returns {undefined}
 */
function generaAllegaPdf(ngara, indice, idstampa, contextPath) {

	_wait();
	$.ajax({
		type: "GET",
		dataType: "text",
		async: false,
		beforeSend: function (x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/text");
			}
		},
		url: contextPath + "/pg/GeneraAllegaPdf.do",
		data: {
			ngara: ngara,
			indice: indice,
			idstampa: idstampa
		},
		success: function (data) {
			if (data && data.length > 0) {
				var spanNomeFIle = "W_DOCDIG_DIGNOMDOC_" + indice;
				setValue(spanNomeFIle, data.toUpperCase());
				var idSelezionaFile = "#selFile[" + indice + "]";
				var idSLinkGenera = "#genera_" + indice;
				var idNomeFileGenerato = "#rowselezioneFile_" + indice;
				$(idSelezionaFile).remove();
				$(idSLinkGenera).remove();
				$(idNomeFileGenerato).find(".valore-dato").html("<span>" + data + "</span>");
				var newLinkDownladFile = "javascript:visualizzaFileGenerato('" + data + "');";
				var idDonwlodFile = "#" + spanNomeFIle + "view";
				$(idDonwlodFile).parent().attr('href', newLinkDownladFile);
				$("#NOMEDOCGEN_" + indice).val(data);
			}
		},
		error: function (e) {
			alert("Errore durante la generazione del documento pdf");
		},
		complete: function () {
			_nowait();
		}
	});
}

/*
 * Attivazione del messaggio di attesa.
 */
function _wait() {
	document.getElementById('bloccaScreen').style.visibility = 'visible';
	document.getElementById('wait').style.visibility = 'visible';
	$("#wait").offset({top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
}


/*
 * Disattivazione del messaggio di attesa
 */
function _nowait() {
	document.getElementById('bloccaScreen').style.visibility = 'hidden';
	document.getElementById('wait').style.visibility = 'hidden';
}


function aggiornaRichiestaFirma(indice, checkbox){
	if(checkbox.checked){
		setValue("W_DOCDIG_DIGFIRMA_" + indice,"1");
	}else{
		setValue("W_DOCDIG_DIGFIRMA_" + indice,"");
	}
}

/*
 * Viene controllato che il file abbia una estensione valida,
 * ossia sia presente fra quelle indicate
 */
function controlloTipoFile(nomeFile, estensioniConsentite){
	var estensioneValida=false;
	if(estensioniConsentite!=null && estensioniConsentite!=""){
		var estensioni=estensioniConsentite.split(";");
		nomeFile = nomeFile.toUpperCase();
		if(nomeFile==null || nomeFile==""){
			estensioneValida=true;
		}else{
			for(var i=0;i<estensioni.length;i++){
				if(estensioni[i]!=null && estensioni[i]!=""){
					if(nomeFile.lastIndexOf(estensioni[i].toUpperCase())>0){
						estensioneValida=true;
						break;
					}
				}
			}
		}
	}else{
		estensioneValida=true;
	}
	return estensioneValida;
}

function apriModaleQform(ngara,busta, titoloBusta,){
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
		width: 550,
		height:300,
		title: "Selezione modalita' inserimento documenti richiesti ai concorrenti",
		buttons: {
		"Conferma": function() {
			confermaModaleQform(ngara,busta, titoloBusta);
		},
		"Annulla": function() {
				$( this ).dialog( "close" );
				$("testoTipoBusta").text("");
			}
		 }
	};
	
	$("#testoTipoBusta").text(titoloBusta);
	var obbligoQformPreq = $('#obbligoformularioPreq').val();
	var obbligoformularioAmm = $('#obbligoformularioAmm').val();
	if((busta==4 && obbligoQformPreq== 'true') || (busta==1 && obbligoformularioAmm== 'true')) {
		$("#radioNormale").attr('disabled', true);
		$("#radioQuestionario").prop('checked', true);
	}else{
		$("#radioNormale").attr('disabled', false);
		$("#radioNormale").prop('checked', true);
	}
	$("#mascheraSceltaQform").dialog(opt).dialog("open");
}

function confermaModaleQform(ngara,busta, titoloBusta, isProceduraTelematica){
	//se busta è 0, allora si deve gestire il qform da elenco
	$("testoTipoBusta").text("");
	var radioValue = $("input[name='tipoInserimento']:checked").val();
	if(radioValue==1){
		if(busta==4)
			$('#gestioneQuestionariPreq').val("MODALE-INSQFORM");
		else if(busta==0)
			$('#gestioneQuestionariIscriz').val("MODALE-INSQFORM");
		else
			$('#gestioneQuestionariAmm').val("MODALE-INSQFORM");
	}else{
		if(busta==4)
			$('#gestioneQuestionariPreq').val("");
		else if(busta==0)
			$('#gestioneQuestionariIscriz').val("");
		else
			$('#gestioneQuestionariAmm').val("");
	}
	$("#mascheraSceltaQform").dialog("close");
	if(busta!=0)
		visualizzaDocumentiConcorrenti(ngara,busta,titoloBusta);
	else
		visualizzaDocumentiOperatori(ngara,1,2,titoloBusta)
}

function insDocumentoMDGUE(ngara,codgar,gruppo,isProceduraTelematica,tipologia){
	var href = "href=gare/documgara/popupCreaDocumentoMDGUE.jsp";
	href += "?ngara=" + ngara + "&codgar=" + codgar+  "&gruppo=" + gruppo + "&isProceduraTelematica=" + isProceduraTelematica + "&tipologia=" + tipologia;
	openPopUpCustom(href, "creaDocumentoMDGUE", 600, 300, "no", "no");
}

function generaConMDGUE(codiceGara,idprg,iddocdig){
	var getUrl = window.location;
	var baseUrl = getUrl .protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1]+'/';
	console.log('baseUrl: '+baseUrl);
	var a = document.createElement("a");
	a.target = 'blank';
	var href = baseUrl+'pg/DgueGenToken.do?'+csrfToken+'&codiceGara='+codiceGara+'&idprg='+idprg
	if(iddocdig)
		href += '&iddocdig='+iddocdig;
	a.href = href;
	document.body.appendChild(a);
	a.click();
	document.body.removeChild(a);
}

function apriConMDGUE(codiceGara,idprg,iddocdig){
	var getUrl = window.location;
	var baseUrl = getUrl .protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1]+'/';
	console.log('baseUrl: '+baseUrl);
	var a = document.createElement("a");
	a.target = 'blank';
	var href = baseUrl+'pg/DgueGenToken.do?'+csrfToken+'&codiceGara='+codiceGara+'&idprg='+idprg+'&apri=true';
	if(iddocdig)
		href += '&iddocdig='+iddocdig;
	a.href = href;
	document.body.appendChild(a);
	a.click();
	document.body.removeChild(a);
}


function _validazioneFormRichiestaFirma() {
		$("#parametriWSDM").validate({
			rules: {
				username: "required",
				password: "required",
				classifica: "required",
				oggetto: "required",
				firmatario: "required",
				ufficiofirmatario: "required"
			},
			messages: {
				username: "Specificare l'utente",
				password: "Specificare la password",
				classifica: "Specificare la classifica",
				oggetto: "Specificare l'oggetto",
				firmatario: "Specificare il firmatario",
				ufficiofirmatario: "Specificare l'ufficio firmatario"
			},
			errorPlacement: function (error, element) {
				error.insertAfter($(element));
				error.css("margin-right","5px");
				error.css("float", "right");
				error.css("vertical-align", "top");
				error.css("color", "red");
			}
		});
		
	}

function apriModaleRichiestaFirma(idprg, iddocdig,indiceRiga){

_getWSLogin();
_gestioneWSLogin();
_getTabellatiInDB();
_popolaTabellato("firmatario","firmatario");
_popolaTabellato("ufficiofirmatario","ufficiofirmatario");
if(indiceRiga!=null && indiceRiga !=''){
	var desc = $('#DOCUMGARA_DESCRIZIONE_'+ indiceRiga).val();
	$('#oggetto').val(desc);
}else{
	$('#oggetto').val($('#oggettoDocumento').val());
}

getDatiFascicolo();
	
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
		width: 550,
		height:"auto",
		title: "Selezione modalita' inserimento documenti richiesti ai concorrenti",
		buttons: {
		"Conferma": {
			text: "Conferma",
			id: "bt_conferma",
			click: function() {
				inviaRichiesta(idprg, iddocdig);
			}
		},
		"Annulla": function() {
				chiudiEPulisciModale();
			}
		 }
	};
	$("#mascheraParametriWSDM").dialog(opt).dialog("open");
	//if(!fascicoloAssociato)
	//	$("#bt_conferma").hide();
	
	setTimeout(function(){
		_validazioneFormRichiestaFirma();
	}, 800);
	
}

function getDatiFascicolo(){
	var urlServizio="pg/GetWsfascicoloByGara.do";
		var currentUrl=window.location.href;
		if(currentUrl.indexOf("/pg/")>0){
			urlServizio="GetWsfascicoloByGara.do";
		}
		_wait();
		$.ajax({
				type: "GET",
				dataType: "json",
				async: true,
				url: urlServizio,
	    		data : {
	    			key1: $("#key1").val()
					
	    		},
	    		
				complete: function() {
	            	_nowait();
	            
	            },
				
				success: function(json){
					if (json.esito == true) {
						_nowait();
						$.map( json.fascicolo, function( item ) {
							$("#codiceFascicolo").val(item[0]);
							$("#classificaFascicolo").val(item[1]);
							$("#annoFascicolo").val(item[2]);
						});
					} else {
						var messaggio = "Non e' possibile procedere con la firma digitale del documento: " + json.messaggio;
						$('#erroriTrasmissione').text(messaggio);
						$('#sezioneErrori').show(tempo);
						$("#bt_conferma").hide();	
						_nowait();
					}
				},
	
				error: function(e){
					var messaggio = "Errore durante la lettura della classifica del fascicolo dalla banca dati";
					$('#erroriTrasmissione').text(messaggio);
					$('#sezioneErrori').show(tempo);
					_nowait();
				},
			});
}

function inviaRichiesta(idprg, iddocdig){
	var tempo = 400;
	if ($("#parametriWSDM").validate().form()) {
		var urlServizio="pg/SetRichiestaFirma.do";
		var currentUrl=window.location.href;
		if(currentUrl.indexOf("/pg/")>0){
			urlServizio="SetRichiestaFirma.do";
		}
		_wait();
		_setWSLogin();
		$.ajax({
				type: "GET",
				dataType: "json",
				async: true,
				url: urlServizio,
	    		data : {
	    			username: $("#username").val(),
					password: $("#password").val(),
					codice: $("#codiceFascicolo").val(),
					classifica: $("#classificaFascicolo").val(),
					anno: $("#annoFascicolo").val(),
					oggetto : $("#oggetto").val(),
					firmatario: $("#firmatario option:selected").val(),
					ufficiofirmatario: $("#ufficiofirmatario option:selected").val(),
					idconfi: $("#idconfi").val(),
					idprg: idprg,
					iddocdig:iddocdig,
					syscon:$("#syscon").val()
	    		},
	    		
				complete: function() {
	            	_nowait();
	            
	            },
				
				success: function(json){
					if (json.esito == true) {
						_nowait();
						chiudiEPulisciModale();
						historyReload();
		        	} else {
						var messaggio = "Il tentativo di richiesta di firma segnala il seguente errore: " + json.messaggio;
						$('#erroriTrasmissione').text(messaggio);
						$('#sezioneErrori').show(tempo);	
						_nowait();
					}
				},
	
				error: function(e){
					var messaggio = "Errore durante la richiesta di firma";
					$('#erroriTrasmissione').text(messaggio);
					$('#sezioneErrori').show(tempo);
					_nowait();
				},
			});
	}
}

function chiudiEPulisciModale(){
	$("#mascheraParametriWSDM").dialog( "close" );
	$('#erroriTrasmissione').text("");
	$('#sezioneErrori').hide();
	$("#classifica").empty();
	$("#firmatario").empty();
	$("#ufficiofirmatario").empty();
	$("#bt_conferma").show();
}

function preAperturaModaleRichiestaFirmaC0OGGASS(idprg,iddocdig,indiceRiga){
	var colonnaDesc=$('#colC0OGGASS_C0ATIT_' + indiceRiga);
	var desc = colonnaDesc.find("span").text();
	$('#oggettoDocumento').val(desc);
	apriModaleRichiestaFirma(idprg, iddocdig,'');
}