
function _creaPopolaRdaInarcassa(codicerda) {
	
	_wait();
	
	// Dati generali
	var _tableDatiGenerali = $("<table/>", {"id": "tableDatiGeneraliContainer", "class": "dettaglio-notab"});
	_tableDatiGenerali.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	_tableDatiGenerali.css("border-top","1px solid #A0AABA ");
	_tableDatiGenerali.css("margin-top","2px");
	_tableDatiGenerali.css("background-color","#FFFFFF");

	var datiGeneraliDescrizione = ["Dati generali",
		"Codice RdA",
		"Descrizione testata",
		"Importo Rda"
		];
		
	var datiGeneraliAttributi = ["",
	    "codiceRda",
		"oggetto",
		"valoreStimato"
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
	
	/*
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
	*/
	// Lista delle posizioni
	var _tablePosizioniRda  = $("<table/>", {"id": "tablePosizioniRdaContainer", "class": "rda", "width" : "100%"});
	_tablePosizioniRda.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	_tablePosizioniRda.css("border-top","1px solid #A0AABA ");
	_tablePosizioniRda.css("margin-top","2px");
	
	var _postr2 = $("<tr/>", {"class" : "intestazione"});
	_postr2.append($("<th/>",{"text": "Posizione RdA"}));
	_postr2.append($("<th/>",{"text": "Descrizione"}));
	_postr2.append($("<th/>",{"text": "Quantita'"}));
	_postr2.append($("<th/>",{"text": "Unita'di misura"}));
	_postr2.append($("<th/>",{"text": "Prezzo"}));
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
			console.log(jsonResult);
			_nowait();
			if (jsonResult.esito == true) {
				if (jsonResult.xmltojson.rdaArray != null) {
					$.each(jsonResult.xmltojson.rdaArray, function(k, v) {
						k = k.replace("'", "\\'");
						
						if (k == 'valoreStimato') {
							v = toMoney(v).view;
						}
						
						$("[id='"+ k + "']").html(v);
					});
				} 
				/*
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
				*/
				/*
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
							_trx.append($("<td/>",{"text": la.cpvPrimary+" "+la.cpvSecondary.replaceAll(";"," ")}));
							_trx.append($("<td/>",{"html": toMoney(la.importoSicurezza).view}));
							
							_tableLottiRda.append(_trx);
						});
					}
				}						
				*/
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
							_trx.append($("<td/>",{"text": pa.posizioneRiferimento}));
							_trx.append($("<td/>",{"text": pa.descrizioneEstesa}));
							_trx.append($("<td/>",{"text": pa.quantita}));
							_trx.append($("<td/>",{"text": pa.um}));
							var _prezzoPrevisto = toMoney(pa.prezzoPrevisto).view;
							_trx.append($("<td/>",{"html": _prezzoPrevisto}));
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


function _creaPopolaRdaConi(codicerda) {
	
	_wait();
	
	// Dati generali
	var _tableDatiGenerali = $("<table/>", {"id": "tableDatiGeneraliContainer", "class": "dettaglio-notab"});
	_tableDatiGenerali.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	_tableDatiGenerali.css("border-top","1px solid #A0AABA ");
	_tableDatiGenerali.css("margin-top","2px");
	_tableDatiGenerali.css("background-color","#FFFFFF");

	var datiGeneraliDescrizione = ["Dati generali",
		"Codice RdA",
		"Descrizione testata",
		"Importo Rda"
		];
		
	var datiGeneraliAttributi = ["",
	    "codiceRda",
		"oggetto",
		"valoreStimato"
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
	
	/*
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
	*/
	// Lista delle posizioni
	var _tablePosizioniRda  = $("<table/>", {"id": "tablePosizioniRdaContainer", "class": "rda", "width" : "100%"});
	_tablePosizioniRda.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	_tablePosizioniRda.css("border-top","1px solid #A0AABA ");
	_tablePosizioniRda.css("margin-top","2px");
	
	var _postr2 = $("<tr/>", {"class" : "intestazione"});
	_postr2.append($("<th/>",{"text": "Posizione RdA"}));
	_postr2.append($("<th/>",{"text": "Descrizione"}));
	_postr2.append($("<th/>",{"text": "Quantita'"}));
	_postr2.append($("<th/>",{"text": "Unita'di misura"}));
	_postr2.append($("<th/>",{"text": "Prezzo"}));
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
			console.log(jsonResult);
			_nowait();
			if (jsonResult.esito == true) {
				if (jsonResult.xmltojson.rdaArray != null) {
					$.each(jsonResult.xmltojson.rdaArray, function(k, v) {
						k = k.replace("'", "\\'");
						
						if (k == 'valoreStimato') {
							v = toMoney(v).view;
						}
						
						$("[id='"+ k + "']").html(v);
					});
				} 
				/*
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
				*/
				/*
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
							_trx.append($("<td/>",{"text": la.cpvPrimary+" "+la.cpvSecondary.replaceAll(";"," ")}));
							_trx.append($("<td/>",{"html": toMoney(la.importoSicurezza).view}));
							
							_tableLottiRda.append(_trx);
						});
					}
				}						
				*/
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
							_trx.append($("<td/>",{"text": pa.posizioneRiferimento}));
							_trx.append($("<td/>",{"text": pa.descrizioneEstesa}));
							_trx.append($("<td/>",{"text": pa.quantita}));
							_trx.append($("<td/>",{"text": pa.um}));
							var _prezzoPrevisto = toMoney(pa.prezzoPrevisto).view;
							_trx.append($("<td/>",{"html": _prezzoPrevisto}));
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
