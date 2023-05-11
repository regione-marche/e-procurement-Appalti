/*
*	Funzioni di supporto per la gestione
*	dell'integrazione con il server EldasoftWSDM.
*
*/

var tempo = 400;
var _tipoWSERP;

/*
 * Funzione di attesa
 */
function _wait() {
	document.getElementById('bloccaScreen').style.visibility='visible';
	$('#bloccaScreen').css("width",$(document).width());
	$('#bloccaScreen').css("height",$(document).height());
	document.getElementById('wait').style.visibility='visible';
	$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
}


/*
 * Nasconde l'immagine di attesa
 */
function _nowait() {
	document.getElementById('bloccaScreen').style.visibility='hidden';
	document.getElementById('wait').style.visibility='hidden';
}

/*
 * Lettura del sistema remoto di protocollazione
 */
function _getWSTipoSistemaRemoto() {
	var servizio = $("#servizio").val();
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSERPRemote.do",
		data: "servizio=" + servizio,
		success: function(data){
			if (data) {
				$.map( data, function( item ) {
					$("#tiposistemaremoto").val(item[0]);
				});
        	}
		},
		error: function(e){
			alert("Errore durante la lettura della tipologia di sistema remoto");
		}
	});
}



function _getTipoWSERP(){
	var servizio = $("#servizio").val();
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSERPRemote.do",
		data: "servizio=" + servizio,
		success: function(data){
			if (data) {
				$.map( data, function( item ) {
					_tipoWSERP = item[0];
				});
        	}
		}
	});
}

/*
 * Lettura dell'utente e degli attributi per la connessione al servizio remoto.
 */
function _getWSERP_Login() {
	var syscon = $("#syscon").val();
	var servizio = "WSERP";
	
	/*
	 * Lettura delle informazioni di login memorizzati nella 
	 * tabella WSLogin.
	 */
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSLogin.do",
		data: "syscon=" + syscon + "&servizio=" + servizio,
		success: function(data){
			if (data) {
				$.map( data, function( item ) {
					$("#ga_username").html(item[0]);
					$("#ga_username").val(item[0]);
					$("#ga_password").html("................");
					$("#ga_password").val("................");
				});
        	}
		},
		error: function(e){
			alert("Errore durante la lettura dell'utente e dei suoi attributi");
		}
	});
	
}

function _getWSERP_L190_Login() {
	var syscon = $("#syscon").val();
	var servizio = "WSERP_L190";
	
	/*
	 * Lettura delle informazioni di login memorizzati nella 
	 * tabella WSLogin.
	 */
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSLogin.do",
		data: "syscon=" + syscon + "&servizio=" + servizio,
		success: function(data){
			if (data) {
				$.map( data, function( item ) {
					$("#L190_username").html(item[0]);
					$("#L190_username").val(item[0]);
					$("#L190_password").html("................");
					$("#L190_password").val("................");
				});
        	}
		},
		error: function(e){
			alert("Errore durante la lettura dell'utente e dei suoi attributi");
		}
	});
	
}

/*
 * Salva l'utente e gli attributi attributi per la connessione ai servizi remoti
 */
function _setWSERP_Login() {
	var syscon = $("#syscon").val();
	var servizio = "WSERP";
	var username = $("#ga_username").val();
	var password = $("#ga_password").val();
	
	$.ajax({
		type: "GET",
		async: false,
		url: "pg/SetWSLogin.do",
		data: "syscon=" + syscon + "&servizio=" + servizio + "&username=" + username + "&password=" + password
	});
}

function _setWSERP_L190_Login() {
	var syscon = $("#syscon").val();
	var servizio = "WSERP_L190";
	var username = $("#L190_username").val();
	var password = $("#L190_password").val();
	
	$.ajax({
		type: "GET",
		async: false,
		url: "pg/SetWSLogin.do",
		data: "syscon=" + syscon + "&servizio=" + servizio + "&username=" + username + "&password=" + password
	});
}

function _testURL(url, tns) {
	var _URLvalido = false;
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		timeout: 3000,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetURL.do",
		data: "url=" + url + "?wsdl&tns=" + tns, 
		success: function(data){
			if (data == true) {
				_URLvalido = true;
			} 
		}
	});
	return _URLvalido;
}



function _verificaPresenzaRda(numeroRda) {
	var _pres = false;
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		timeout: 3000,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSERPPresenzaRda.do",
		data: "numeroRda=" + numeroRda, 
		success: function(data){
			if (data == true) {
				_pres = true;
			} 
		}
	});
	return _pres;
}


/*
 * Lettura dei lotti disponibili 
 * perche' senza carrello collegato (SmeUp) 
 */
function _popolaLotti(codiceGara,id) {
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSERPLottiSenzaCarrello.do",
		data: "codiceGara=" + codiceGara,
		success: function(data){
			if (data) {
				$("#" + id).html("");
				$("#" + id).append($("<option/>", {value: "" ,text: "" }));
				$.map( data, function( item ) {
					$("#" + id).append($("<option/>", {value: item[0], text: item[1] }));
				});
				
				//Se risulta presente un solo valore nel tabellato allora lo si seleziona
				if($("#" + id + " option").length == 2 ){
					$("#" + id + " option").eq(1).prop('selected', true);
				}
			}
		},
		error: function(e){
			alert("Errore durante la lettura dei lotti disponibili per la gara " + codiceGara);
		}
	});
}

/*
 * Lettura del tabellato identificato da "nome".
 * Richiede che nel DOM sia gia' presente un oggetto
 * di tipo "select" con "id" e "name" uguale a "id" 
 */
function _popolaWSERPTabellato(codice,id) {
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSERPTabellato.do",
		data: "codice=" + codice,
		success: function(data){
			if (data) {
				$("#" + id).append($("<option/>", {value: "" ,text: "" }));
				$.map( data, function( item ) {
					$("#" + id).append($("<option/>", {value: item[0], text: item[1] }));
				});
				
				//Se è presente un solo valore nel tabellato allora lo si seleziona
				if($("#" + id + " option").length == 2 ){
					$("#" + id + " option").eq(1).prop('selected', true);
				}
			}
		},
		error: function(e){
			alert("Errore durante la lettura del tabellato " + codice);
		}
	});
}

function _popolaWSERPListaCondPag(id) {
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSERPCondizioniPagamento.do",
		success: function(data){
			if (data) {
				$("#" + id).append($("<option/>", {value: "" ,text: "" }));
				$.map( data, function( item ) {
					$("#" + id).append($("<option/>", {value: item[0], text: item[1] }));
				});
				
				//Se è presente un solo valore nel tabellato allora lo si seleziona
				if($("#" + id + " option").length == 2 ){
					$("#" + id + " option").eq(1).prop('selected', true);
				}
			}
		},
		error: function(e){
			alert("Errore durante la lettura delle condizioni di pagamento");
		}
	});
}

/*
 * Lettura del dato fisso da "nome".
 */
function _popolaWSERPDatoFisso(codice,id) {
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSERPTabellato.do",
		data: "codice=" + codice,
		success: function(data){
			if (data) {
				$.map( data, function( item ) {
					$("#"+id).val(item[0]);
				});
        	}
		},
		error: function(e){
			alert("Errore durante la lettura del dato fisso " + codice);
		}
	});
}

/*
 * Legge i dati di una rda.
 */
function _getWSERPRda(numeroRda,esercizio) {

	_wait();
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: true,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSERPListaRda.do",
		"data" : { 
				codicerda: numeroRda,
				divisione: esercizio
		},
		success: function(json){
			if (json.esito == true) {
				_popolaTabellaDatiPersonalizzati(json.iTotalRecordsDATIPERSONALIZZATI, json.dataDATIPERSONALIZZATI);
				
			} else {
				var messaggio = "Errore durante la lettura delle rda";
				$('#rdamessaggio').text(messaggio);
				$('#rdamessaggio').show(300);
			}
		},
		error: function(e){
			var messaggio = "Errore durante la lettura delle rda";
			$('#rdamessaggio').text(messaggio);
			$('#rdamessaggio').show(300);
		},
		complete: function() {
			_nowait();
        }
		
		
		
	});
}

/*
 * Legge i dati del dettaglio rda.
 */
function _getWSERPDettaglioRda(numeroRda,callback) {

	var callbackFunction = function(json){
		if (json.esito == true) {
			if (json.xmltojson.rdaArray != null) {
				$("#oggettodocumento").text(json.xmltojson.rdaArray.oggetto);	
			}
		} else {
			;
		}
		if (callback && typeof callback === 'function'){
			callback(json.esito);
		}
	};


	_wait();
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: true,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSERPDettaglioRda.do",
		"data" : { 
				codicerda: numeroRda
		},
		success: callbackFunction,
		error: function(e){
			var messaggio = "Errore durante la lettura delle rda";
			$('#rdamessaggio').text(messaggio);
			$('#rdamessaggio').show(300);
		},
		complete: function() {
			_nowait();
        }
		
		
		
	});
}



/**
 * Popola la scheda rda
 */
function _creaPopolaSchedaRda(codicerda, tiporda) {
	
	_wait();
	
	// Dati generali
	var _tableDatiGenerali = $("<table/>", {"id": "tableDatiGeneraliContainer", "class": "dettaglio-notab"});
	_tableDatiGenerali.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	_tableDatiGenerali.css("border-top","1px solid #A0AABA ");
	_tableDatiGenerali.css("margin-top","2px");
	_tableDatiGenerali.css("background-color","#FFFFFF");

	var datiGeneraliDescrizione = ["Dati generali",
	    "Tipo RdA",
		"Codice RdA",
		"Piano di approvvigionamento",
		"Tipologia",
		"Oggetto",
		"Data creazione",
		"<br>Codici SIVCA",
		"Codice SIVCA investimento",
		"Codice SIVCA intervento",
		"<br>Altri dati",
		"CIG Padre",
		"Motivazione",
		"Dettaglio motivazione",
		"Privacy",
		"Prezziario di riferimento",
		"Fornitore individuato",
		"Partita IVA fornitore",
		"Codice fiscale fornitore",
		"Protocollo dell'offerta dell'affidatario",
		"Protocolli delle altre offerte richieste",
		"Ribasso offerto",
		"Condizioni di pagamento",
		"Durata",
		"Unità di misura della durata (G - Giorni, M - Mesi)",
		"Decorrenza (data di inizio prevista)",
		"Note sulla durata e la decorrenza",
		"Penali",
		"Contesto sicurezza",
		"DUVRI",
		"Tipo attività",
		"Documentazione tecnica",
		"Importo totale",
		"Oneri sicurezza"];
	                    	
	var datiGeneraliAttributi = ["",
	    "tipoRdaErp",
		"codiceRda",
		"codiceCarrello",
		"tipologia",
		"oggetto",
		"dataCreazioneRda",
		"",
		"codiceSivcaInvestimento",
		"codiceSivcaIntervento",
		"",
		"codiceCigAQ",
		"motivazione",
		"dettaglio_motivazione_NODEF",
		"privacy",
		"prezzarioRiferimento",
		"fornitore",
		"pivaFornitore",
		"cfFornitore",
		"protocolloFornitore",
		"protocolliOfferenti",
		"ribasso_offerto",
		"condizioniPagamento",
		"duratamesigiorni_NODEF",
		"durata_NODEF",
		"decorrenza_NODEF",
		"notedecorrdurata_NODEF",
		"penali",
		"contestoSicurezza",
		"duvri_NODEF",
		"tipoAttivita",
		"documentazionetecnica_NODEF",
		"importototale_NODEF",
		"onerisicurezza_NODEF"];
	                    	
	var iDatiGenerali;
	for (iDatiGenerali = 0; iDatiGenerali < datiGeneraliDescrizione.length; iDatiGenerali++) {
		var _dgtrx = $("<tr/>");
		if (datiGeneraliAttributi[iDatiGenerali] == "") {
			var _dgtdx = $("<td/>",{"colspan": "2"});
			_dgtdx.css("font-weight","bold");
			_dgtdx.css("border-top","1px white solid");
			_dgtdx.append(datiGeneraliDescrizione[iDatiGenerali]);
			_dgtrx.append(_dgtdx);
		} else {
			_dgtrx.append($("<td/>",{"class":"etichetta-dato", "text": datiGeneraliDescrizione[iDatiGenerali]}));
			_dgtrx.append($("<td/>",{"id": datiGeneraliAttributi[iDatiGenerali], "class":"valore-dato", "text": ""}));
		}
		_tableDatiGenerali.append(_dgtrx);
	}
	
	$("#finestraSchedaRda").append(_tableDatiGenerali);
	
	// Lista delle nomine
	var _tableNomine  = $("<table/>", {"id": "tableNomineContainer", "class": "rda", "width" : "100%"});
	_tableNomine.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	_tableNomine.css("border-top","1px solid #A0AABA ");
	_tableNomine.css("margin-top","2px");
	
//	var _postr3 = $("<tr/>", {"class" : "intestazione"});
//	_postr3.append($("<th/>",{"text": "Incarico"}));
//	_postr3.append($("<th/>",{"text": "Denominazione"}));
//	_tableNomine.append(_postr3);
	
	var _nominetr = $("<tr/>");
	_nominetr.append($("<td/>",{"class":"etichetta-dato", "text": "Nomine"}));
	_nominedt = $("<td/>",{"class":"valore-dato"});
	_nominedt.append(_tableNomine);
	_nominetr.append(_nominedt);
	_tableDatiGenerali.append(_nominetr);

	// Lista delle posizioni
	var _tablePosizioniRda  = $("<table/>", {"id": "tablePosizioniRdaContainer", "class": "rda", "width" : "100%"});
	_tablePosizioniRda.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	_tablePosizioniRda.css("border-top","1px solid #A0AABA ");
	_tablePosizioniRda.css("margin-top","2px");
	
	var _postr2 = $("<tr/>", {"class" : "intestazione"});
	_postr2.append($("<th/>",{"text": "Codice articolo"}));
	_postr2.append($("<th/>",{"text": "Descrizione estesa"}));
	_postr2.append($("<th/>",{"text": "Posizione"}));
	_postr2.append($("<th/>",{"text": "Unita' di misura"}));
	_postr2.append($("<th/>",{"text": "Quantita'"}));
	_postr2.append($("<th/>",{"text": "Prezzo previsto"}));
	_postr2.append($("<th/>",{"text": "Conto Co.Ge"}));
	_postr2.append($("<th/>",{"text": "Centro di costo"}));
	_postr2.append($("<th/>",{"text": "WBE"}));
	_tablePosizioniRda.append(_postr2);
		
	$("#finestraSchedaRda").append("<b><br>Posizioni RdA</b><br>").append(_tablePosizioniRda);

	$.ajax({
		"type": "POST",
		"dataType": "json",
		"async": false,
		"beforeSend": function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=utf-8");
			}
		},
		"url": "pg/GetWSERPDettaglioRda.do",
		"data": {
			"codicerda" : codicerda,
			"tiporda" : tiporda
		},
		"success": function(jsonResult){
			_nowait();
			if (jsonResult.esito == true) {
				if (jsonResult.xmltojson.rdaArray != null) {
					$.each(jsonResult.xmltojson.rdaArray, function(k, v) {
						k = k.replace("'", "\\'");
						
						// Tipologia
						if (k == 'tipologia') {
							if (v == 'L') {
								v = "Lavori";
							} else if (v == 'S') {
								v = "Servizi";
							} else if (v == 'F') {
								v = "Forniture";
							}
						}
						
						// Data creazione
						if (k == 'dataCreazioneRda') {
							v = ddformat(v);
						}
						
						$("[id='"+ k + "']").text(v);
					});
				} 
				
				
				if (jsonResult.xmltojson.rdaArray != null) {
					// Nomine
					if (jsonResult.xmltojson.rdaArray.nomineArray != null) {
						var _nomineArray = jsonResult.xmltojson.rdaArray.nomineArray;
						if (_nomineArray.length > 1){
						
						} else {
							_nomineArray = jQuery.makeArray(_nomineArray);
						}
						
						var _postr3 = $("<tr/>", {"class" : "intestazione"});
						_postr3.append($("<th/>",{"text": "Incarico"}));
						_postr3.append($("<th/>",{"text": "Denominazione"}));
						_tableNomine.append(_postr3);
						
						$.each(_nomineArray, function(i,na) {
							var tab1cod = na.codiceRuolo;
							var tab1tip = na.voceRuolo;
							var tipoarchivio = na.tipoArchivio;
							var codicearchivio = na.codiceArchivio;
							var descrizioneincarico = "";
							var denominazioneincaricato = "";
							
							var jsonResultNomina = _getNomina(tab1cod, tab1tip, tipoarchivio, codicearchivio);
							if (jsonResultNomina != null) {
								descrizioneincarico = jsonResultNomina.descrizioneincarico;
								denominazioneincaricato = jsonResultNomina.denominazioneincaricato;
							}

							var _trx = $("<tr/>");
							_trx.append($("<td/>",{"text": descrizioneincarico}));
							_trx.append($("<td/>",{"text": denominazioneincaricato}));
							_tableNomine.append(_trx);
						});
					}
				}
				
				if (jsonResult.xmltojson.rdaArray != null) {
					if (jsonResult.xmltojson.rdaArray.posizioneRdaArray != null) {
						var _posizioneRdaArray = jsonResult.xmltojson.rdaArray.posizioneRdaArray;
						if (_posizioneRdaArray.length > 1){
						
						} else {
							_posizioneRdaArray = jQuery.makeArray(_posizioneRdaArray);
						}
					
						$.each(_posizioneRdaArray, function(i,pa) {
							var _trx = $("<tr/>");
							_trx.append($("<td/>",{"text": pa.codiceArticolo}));
							_trx.append($("<td/>",{"text": pa.descrizioneEstesa}));
							_trx.append($("<td/>",{"text": pa.posizioneRiferimento}));
							_trx.append($("<td/>",{"text": pa.um}));
							_trx.append($("<td/>",{"text": pa.quantita}));
							var _prezzoPrevisto = toMoney(pa.prezzoPrevisto).view;
							_trx.append($("<td/>",{"html": _prezzoPrevisto}));
							_trx.append($("<td/>",{"text": pa.contoCoGe}));
							_trx.append($("<td/>",{"text": pa.cdc}));
							_trx.append($("<td/>",{"text": pa.wbe}));
							_tablePosizioniRda.append(_trx);
						});
					}
				}
			} else {
				alert("Si e' verificato un errore durante l'interazione con i servizi di lettura della RdA: " + jsonResult.messaggio);
			}
		},
		"error": function ( e ) {
			_nowait();
			alert("Si e' verificato un errore durante l'interazione con i servizi di lettura della RdA: " + e);
		}
	});

}

function _creaPopolaRdaAmiu(codicerda) {
	
	_wait();
	
	// Dati generali
	var _tableDatiGenerali = $("<table/>", {"id": "tableDatiGeneraliContainer", "class": "dettaglio-notab"});
	_tableDatiGenerali.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	_tableDatiGenerali.css("border-top","1px solid #A0AABA ");
	_tableDatiGenerali.css("margin-top","2px");
	_tableDatiGenerali.css("background-color","#FFFFFF");

	var datiGeneraliDescrizione = ["Dati generali",
	    "Tipo documento RdA",
		"Numero RdA",
		"Valore totale RdA a rilascio",
		"Testata",
		"Smart CIG"];
		
	var datiGeneraliAttributi = ["",
	    "tipoRdaErp",
		"codiceRda",
		"valoreStimato",
		"oggetto",
		"codiceCig"];
		
	var iDatiGenerali;
	for (iDatiGenerali = 0; iDatiGenerali < datiGeneraliDescrizione.length; iDatiGenerali++) {
		var _dgtrx = $("<tr/>");
		if (datiGeneraliAttributi[iDatiGenerali] == "") {
			var _dgtdx = $("<td/>",{"colspan": "2"});
			_dgtdx.css("font-weight","bold");
			_dgtdx.css("border-top","1px white solid");
			_dgtdx.append(datiGeneraliDescrizione[iDatiGenerali]);
			_dgtrx.append(_dgtdx);
		} else {
			_dgtrx.append($("<td/>",{"class":"etichetta-dato", "text": datiGeneraliDescrizione[iDatiGenerali]}));
			_dgtrx.append($("<td/>",{"id": datiGeneraliAttributi[iDatiGenerali], "class":"valore-dato", "text": ""}));
		}
		_tableDatiGenerali.append(_dgtrx);
	}
	
	$("#finestraSchedaRda").append(_tableDatiGenerali);
	
	
	
		// Lista delle posizioni
	var _tablePosizioniRda  = $("<table/>", {"id": "tablePosizioniRdaContainer", "class": "rda", "width" : "100%"});
	_tablePosizioniRda.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	_tablePosizioniRda.css("border-top","1px solid #A0AABA ");
	_tablePosizioniRda.css("margin-top","2px");
	
	var _postr2 = $("<tr/>", {"class" : "intestazione"});
	_postr2.append($("<th/>",{"text": "Posizione RdA"}));
	_postr2.append($("<th/>",{"text": "Richiedente"}));
	_postr2.append($("<th/>",{"text": "Categoria posizione"}));
	_postr2.append($("<th/>",{"text": "Tipo contabilizzazione"}));
	_postr2.append($("<th/>",{"text": "Testo breve"}));
	_postr2.append($("<th/>",{"text": "Quantita'"}));
	_postr2.append($("<th/>",{"text": "Unita'di misura"}));
	_postr2.append($("<th/>",{"text": "Prezzo"}));
	_postr2.append($("<th/>",{"text": "Unita'di prezzo"}));
	_postr2.append($("<th/>",{"text": "Gruppo merci"}));
	_postr2.append($("<th/>",{"text": "Divisione"}));
	_postr2.append($("<th/>",{"text": "Magazzzino"}));
	_postr2.append($("<th/>",{"text": "Org.acquisti"}));
	_tablePosizioniRda.append(_postr2);
		
	$("#finestraSchedaRda").append("<b><br>Posizioni RdA</b><br>").append(_tablePosizioniRda);
	
		
	$.ajax({
		"type": "POST",
		"dataType": "json",
		"async": false,
		"beforeSend": function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=utf-8");
			}
		},
		"url": "pg/GetWSERPDettaglioRda.do",
		"data": {
			"codicerda" : codicerda
		},
		"success": function(jsonResult){
			_nowait();
			if (jsonResult.esito == true) {
				if (jsonResult.xmltojson.rdaArray != null) {
					$.each(jsonResult.xmltojson.rdaArray, function(k, v) {
						k = k.replace("'", "\\'");

						
						// Tipologia
						if (k == 'tipologia') {
							if (v == 'L') {
								v = "Lavori";
							} else if (v == 'S') {
								v = "Servizi";
							} else if (v == 'F') {
								v = "Forniture";
							}
						}
						
						// Data creazione
						if (k == 'dataCreazioneRda') {
							v = ddformat(v);
						}
						
						$("[id='"+ k + "']").text(v);
					});
				} 

				if (jsonResult.xmltojson.rdaArray != null) {
					if (jsonResult.xmltojson.rdaArray.posizioneRdaArray != null) {
						var _posizioneRdaArray = jsonResult.xmltojson.rdaArray.posizioneRdaArray;
						if (_posizioneRdaArray.length > 1){
						
						} else {
							_posizioneRdaArray = jQuery.makeArray(_posizioneRdaArray);
						}
					
						$.each(_posizioneRdaArray, function(i,pa) {
							var _trx = $("<tr/>");
							_trx.append($("<td/>",{"text": pa.posizioneRiferimento}));
							_trx.append($("<td/>",{"text": pa.richiedente}));
							_trx.append($("<td/>",{"text": pa.categoria}));
							_trx.append($("<td/>",{"text": pa.tipoContab}));
							_trx.append($("<td/>",{"text": pa.descrizioneEstesa}));
							_trx.append($("<td/>",{"text": pa.quantita}));
							_trx.append($("<td/>",{"text": pa.um}));
							var _prezzoPrevisto = toMoney(pa.prezzoPrevisto).view;
							_trx.append($("<td/>",{"html": _prezzoPrevisto}));
							_trx.append($("<td/>",{"text": pa.up}));
							_trx.append($("<td/>",{"text": pa.gruppoMerci}));
							_trx.append($("<td/>",{"text": pa.Divisione}));
							_trx.append($("<td/>",{"text": pa.Magazzino}));
							_trx.append($("<td/>",{"text": pa.orgAcq}));
							_tablePosizioniRda.append(_trx);
						});
					}
				}
				
				
				
			} else {
				alert("Si e' verificato un errore durante l'interazione con i servizi di lettura della RdA: " + jsonResult.messaggio);
			}
		},
		"error": function ( e ) {
			_nowait();
			alert("Si e' verificato un errore durante l'interazione con i servizi di lettura della RdA: " + e);
		}
	});


}

function _creaPopolaRdaRaiway(codicerda) {
	
	_wait();
	
	// Dati generali
	var _tableDatiGenerali = $("<table/>", {"id": "tableDatiGeneraliContainer", "class": "dettaglio-notab"});
	_tableDatiGenerali.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	_tableDatiGenerali.css("border-top","1px solid #A0AABA ");
	_tableDatiGenerali.css("margin-top","2px");
	_tableDatiGenerali.css("background-color","#FFFFFF");

	var datiGeneraliDescrizione = ["Dati generali",
		"Codice RdA",
		"Data creazione RdA",
		"Struttura competente acquisto",
		"Procedura selezione",
		"Oggetto",
		"RUP",
		//"Importo",
		"Durata contratto gg",
		//"Importo opzioni",
		//"codice CPV",
		"Ripartizione in lotti?",
		"Accordo quadro?",
		"Appalto green?",
		"Motivazione appalto green",
		"Data rilascio controller"
		];
		
	var datiGeneraliAttributi = ["",
	    "codiceRda",
		"dataCreazioneRda",
		"divisione",
		"sceltaContraente",
		"oggetto",
		"fornitore",
		//"valoreStimato",
		"durata",
		//"importoOpzioni",
		//"codCpv",
		"divisioneInLotti",
		"accqua",
		"green",
		"motGreen",
		"dataConsegna"
		];
		
	var iDatiGenerali;
	for (iDatiGenerali = 0; iDatiGenerali < datiGeneraliDescrizione.length; iDatiGenerali++) {
		var _dgtrx = $("<tr/>");
		if (datiGeneraliAttributi[iDatiGenerali] == "") {
			var _dgtdx = $("<td/>",{"colspan": "2"});
			_dgtdx.css("font-weight","bold");
			_dgtdx.css("border-top","1px white solid");
			_dgtdx.append(datiGeneraliDescrizione[iDatiGenerali]);
			_dgtrx.append(_dgtdx);
		} else {
			_dgtrx.append($("<td/>",{"class":"etichetta-dato", "text": datiGeneraliDescrizione[iDatiGenerali]}));
			_dgtrx.append($("<td/>",{"id": datiGeneraliAttributi[iDatiGenerali], "class":"valore-dato", "text": ""}));
		}
		_tableDatiGenerali.append(_dgtrx);
	}
	
	$("#finestraSchedaRda").append(_tableDatiGenerali);
	

	// Lista dei lotti
 	var _tableLottiRda  = $("<table/>", {"id": "tableLottiRdaContainer", "class": "rda", "width" : "100%"});
	_tableLottiRda.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	_tableLottiRda.css("border-top","1px solid #A0AABA ");
	_tableLottiRda.css("margin-top","2px");
	 var _lostr3 = $("<tr/>", {"class" : "intestazione"});
	_lostr3.append($("<th/>",{"text": "Numero lotto"}));
	_lostr3.append($("<th/>",{"text": "Descrizione lotto"}));
	
	//Modifica Emanuele
	_lostr3.append($("<th/>",{"text": "Importo"}));
	_lostr3.append($("<th/>",{"text": "Importo Opzioni"}));
	_lostr3.append($("<th/>",{"text": "Codice CPV"}));
	_lostr3.append($("<th/>",{"text": "Importo Sicurezza"}));
	
	_tableLottiRda.append(_lostr3);
	$("#finestraSchedaRda").append("<b><br>Lotti RdA</b><br>").append(_tableLottiRda);
	
	// Lista delle posizioni
	var _tablePosizioniRda  = $("<table/>", {"id": "tablePosizioniRdaContainer", "class": "rda", "width" : "100%"});
	_tablePosizioniRda.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	_tablePosizioniRda.css("border-top","1px solid #A0AABA ");
	_tablePosizioniRda.css("margin-top","2px");
	
	var _postr2 = $("<tr/>", {"class" : "intestazione"});
	_postr2.append($("<th/>",{"text": "Lotto"}));
	_postr2.append($("<th/>",{"text": "Codice articolo"}));
	_postr2.append($("<th/>",{"text": "Numero posizione RdA"}));
	_postr2.append($("<th/>",{"text": "Testo breve"}));
	_postr2.append($("<th/>",{"text": "Quantita'"}));
	_postr2.append($("<th/>",{"text": "Unita'di misura"}));
	_postr2.append($("<th/>",{"text": "Prezzo"}));
	_postr2.append($("<th/>",{"text": "Sicurezza?"}));
	_tablePosizioniRda.append(_postr2);
		
	$("#finestraSchedaRda").append("<b><br>Posizioni RdA</b><br>").append(_tablePosizioniRda);
	
		
	$.ajax({
		"type": "POST",
		"dataType": "json",
		"async": false,
		"beforeSend": function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=utf-8");
			}
		},
		"url": "pg/GetWSERPDettaglioRda.do",
		"data": {
			"codicerda" : codicerda
		},
		"success": function(jsonResult){
			_nowait();
			if (jsonResult.esito == true) {
				if (jsonResult.xmltojson.rdaArray != null) {
					$.each(jsonResult.xmltojson.rdaArray, function(k, v) {
						k = k.replace("'", "\\'");
					
						// Data creazione
						if (k == 'dataCreazioneRda' || k == 'dataConsegna' ) {
							v = ddformat(v);
						}
						
						if (k == 'divisioneInLotti' || k == 'accqua' || k == 'green') {
							v = sinoformat(v);
						}
						
						if (k == 'sceltaContraente') {
							var jsonResultSceltaContraente = _getSceltaContraente('A2044', v);
							if (jsonResultSceltaContraente != null) {
								descrizioneSceltaContraente = jsonResultSceltaContraente.descrizione;
								v = descrizioneSceltaContraente;
							}
						}
						
						$("[id='"+ k + "']").text(v);
					});
				} 
				
				//recupero rup
				if (jsonResult.xmltojson.rdaArray.tecniciArray != null) {
					var _tecniciArray = jsonResult.xmltojson.rdaArray.tecniciArray;
					if (_tecniciArray.length = 1){
						_tecniciArray = jQuery.makeArray(_tecniciArray);
						$.each(_tecniciArray, function(i,ta) {
							rup = ta.denominazione;
							$("[id='fornitore']").text(rup);
						});
					}
				}
				
				//lotti
				if (jsonResult.xmltojson.rdaArray != null) {
					if (jsonResult.xmltojson.rdaArray.lottoArray != null) {
						var _lottoArray = jsonResult.xmltojson.rdaArray.lottoArray;
						if (_lottoArray.length > 1){
							;
						} else {
							_lottoArray = jQuery.makeArray(_lottoArray);
						}
					$.each(_lottoArray, function(i,la) {
							var _trx = $("<tr/>");
							_trx.append($("<td/>",{"text": la.idLotto}));
							_trx.append($("<td/>",{"text": la.descrizione}));
							
							_trx.append($("<td/>",{"html": toMoney(la.importoLotto).view}));
							_trx.append($("<td/>",{"html": toMoney(la.importoOpzioni).view}));
							_trx.append($("<td/>",{"text": la.codCpv}));
							_trx.append($("<td/>",{"html": toMoney(la.importoSicurezza).view}));
							
							_tableLottiRda.append(_trx);
						});
					}
				}						
				
				//posizioni
				if (jsonResult.xmltojson.rdaArray != null) {
					if (jsonResult.xmltojson.rdaArray.posizioneRdaArray != null) {
						var _posizioneRdaArray = jsonResult.xmltojson.rdaArray.posizioneRdaArray;
						if (_posizioneRdaArray.length > 1){
						
						} else {
							_posizioneRdaArray = jQuery.makeArray(_posizioneRdaArray);
						}
					
						$.each(_posizioneRdaArray, function(i,pa) {
							var _trx = $("<tr/>");
							_trx.append($("<td/>",{"text": pa.idLotto}));
							_trx.append($("<td/>",{"text": pa.codiceArticolo}));
							_trx.append($("<td/>",{"text": pa.posizioneRiferimento}));
							_trx.append($("<td/>",{"text": pa.descrizioneEstesa}));
							_trx.append($("<td/>",{"text": pa.quantita}));
							_trx.append($("<td/>",{"text": pa.um}));
							var _prezzoPrevisto = toMoney(pa.prezzoPrevisto).view;
							_trx.append($("<td/>",{"html": _prezzoPrevisto}));
							_trx.append($("<td/>",{"text": sinoformat(pa.sicurezza)}));
							_tablePosizioniRda.append(_trx);
						});
					}
				}
				
				
				
			} else {
				alert("Si e' verificato un errore durante l'interazione con i servizi di lettura della RdA: " + jsonResult.messaggio);
			}
		},
		"error": function ( e ) {
			_nowait();
			alert("Si e' verificato un errore durante l'interazione con i servizi di lettura della RdA: " + e);
		}
	});


}

function _getNomina(tab1cod, tab1tip, tipoarchivio, codicearchivio) {
	
	var jsonResultNomina;
	
	$.ajax({
		"type": "POST",
		"dataType": "json",
		"async": false,
		"beforeSend": function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		"url": "pg/GetWSERPNomina.do",
		"data": {
			"tab1cod" : tab1cod,
			"tab1tip" : tab1tip,
			"tipoarchivio" : tipoarchivio,
			"codicearchivio" : codicearchivio
		},
		"success": function(jsonResult){
			jsonResultNomina = jsonResult;
		}
	});
	
	return jsonResultNomina
	
}

function _getSceltaContraente(tab1cod, tab1tip) {
	
	var jsonResultSceltaContraente;
	
	$.ajax({
		"type": "POST",
		"dataType": "json",
		"async": false,
		"beforeSend": function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		"url": "pg/GetWSERPSceltaContraente.do",
		"data": {
			"tab1cod" : tab1cod,
			"tab1tip" : tab1tip
		},
		"success": function(jsonResult){
			jsonResultSceltaContraente = jsonResult;
		}
	});
	
	return jsonResultSceltaContraente
	
}


/*
 * Common utility
 */
 
/*
 * Lettura del dato fisso da "nome".
 */
function _popolaWSERPDatoVariabile(codice,id) {
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSERPTabellato.do",
		data: "codice=" + codice,
		success: function(data){
			if (data) {
				$.map( data, function( item ) {
					$("#"+id).val(item[0]);
				});
        	}
		},
		error: function(e){
			alert("Errore durante la lettura del dato fisso " + codice);
		}
	});
}

function appendLeadingZeroes(n){
	if(n <= 9){
		return "0" + n;
	}
	return n
}

function ddformat(ds) {
	var dd = new Date(ds);
	var df = appendLeadingZeroes(dd.getDate()) + "/" + appendLeadingZeroes(dd.getMonth() + 1) + "/" + dd.getFullYear();
	return df;
}

function sinoformat(bs) {
	var sino ="";
	if(bs){
		sino="Si";
	}else{
		sino="No";
		
	}

	return sino;
}

