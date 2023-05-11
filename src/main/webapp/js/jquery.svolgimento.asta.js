/*
*	Funzioni di supporto per la gestione
*	della pagina fasiGara-AstaElettronica-Svolgimento.jsp.
*
*/

var timer;
var ngara ;
var ribcal;
var modlicg;
var _tableDitte = null;
var _tableRilanciDitta = null;
var _tablePrezziUnitari = null;
var tipoFase;
var sogliaAggiornamento = "60";   //Soglia in secondi per l'aggiornamento automatico dei dati
var percorsoImg;

function _setPercorsoImg(valore){
	percorsoImg = valore;
}


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
 * Popola i dati dell'ultima fase.
 */
function _popolaDatiFase() {
	
	_wait();
	
	$.ajax({
        type: "GET",
        dataType: "json",
        async: false,
        beforeSend: function(x) {
		if(x && x.overrideMimeType) {
			x.overrideMimeType("application/json;charset=UTF-8");
	       }
		},
        url: "pg/GetDatiFaseAsta.do",
        data: "ngara=" + ngara,
        success: function(data) {
        	if (data && data.length > 0) {
				// 0 - Numero Fase
				var numfase = data[0];
				$("#NUMFASE").text(numfase);
				// 1 - Data apertura
				var dataIni = data[1];
				$("#DATAORAINI").text(dataIni);
				// 2 - Data chiusura
				var dataFineString = data[2];
				$("#DATAORAFINE").text(dataFineString);
				// 3 - Durata minima
				var durmin = data[3];
				$("#DURMIN").text(durmin + " minuti" );
				// 4 - Durata massima
				var durmax = data[4];
				$("#DURMAX").text(durmax + " minuti" );
				// 5 - Tempo base
				var tbase = data[5];
				$("#TBASE").text(tbase + " minuti" );
				// 6 - Tipologia fase
				tipoFase = data[6];
				if(tipoFase==1) {
					$("#TIPO_FASE").text("Fase in corso");
					$("#RIGA_TEMPO_RIMANENTE").show();
					
					//Inizializzazione dei dati per il plugin che effettua il conteggio alla rovescia
					var dataFine = new Date();
					var datiTmp = dataFineString.split("/");
					var giorno = datiTmp[0];
					var mese = datiTmp[1];
					datiTmp = datiTmp[2].split(" ");
					var anno = datiTmp[0];
					datiTmp = datiTmp[1].split(":");
					var ore=datiTmp[0];
					var minuti = datiTmp[1];
					var secondi =datiTmp[2];
					dataFine = new Date(anno, mese - 1, giorno, ore, minuti, secondi, 0);
					$('#defaultCountdown').countdown('destroy');
					$('#defaultCountdown').countdown({until: dataFine, format: 'HMS'});
					$("#tempoResiduo").text(sogliaAggiornamento);
					$("#rigaRicaricaDati").show();
				}else if(tipoFase==2){
					$("#TIPO_FASE").text("Fase conclusa");
					$("#RIGA_TEMPO_RIMANENTE").hide();
					if($('#rigaRicaricaDati').is(':visible'))
						$("#rigaRicaricaDati").hide();
					if(timer!=null)
						clearTimeout(timer);
				}else{
					$("#TIPO_FASE").text("Fase di prossima apertura");
					$("#RIGA_TEMPO_RIMANENTE").hide();
					if($('#rigaRicaricaDati').is(':visible'))
						$("#rigaRicaricaDati").hide();
					if(timer!=null)
						clearTimeout(timer);
				}
        	}
        },
        error: function(e){
            alert("Errore durante la lettura dei dati dell'ultima fase dell'asta");
        },
        complete: function() {
        	_nowait();
        }
    });
}


/*
 * Legge l'elemento documentale dal sistema remoto in
 * funzione del numero documento.
 */
function _getDatiDitteInvitate() {
	
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
		url: "pg/GetDatiDitteInvitateAsta.do",
		data : "ngara=" +  ngara,
		success: function(json){
			ribcal = json.ribcal;
			modlicg = json.modlicg;
			_popolaTabellaDitte(json.iTotalRecords, json.data);
		},
		error: function(e){
			 alert("Errore durante la lettura delle ditte invitate all'asta");
		},
		complete: function() {
			_nowait();
        }
	});
}


/*
 * Popola la lista delle ditte invitate all'asta elettronica
 */
function _popolaTabellaDitte(numDitte, ditteInvitate) {
	
	if (_tableDitte != null) {
		_tableDitte.destroy(true);
	}
	
	var ribauoVisibile=(ribcal==1);
	var impoffVisibile=(ribcal==2 || modlicg ==5 || modlicg == 14);
	var colonnaOrdinamentoIni = 2;
	if(ribcal==2)
		colonnaOrdinamentoIni = 3;
	
	var _table = $('<table/>', {"id": "ditte", "class": "ditte", "cellspacing": "0", "width" : "100%"});
	var _thead = $('<thead/>');
	var _tr = $('<tr/>');
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_thead.append(_tr);
	_table.append(_thead);
	var _tbody = $('<tbody/>');
	_table.append(_tbody);
	$("#dittecontainer").append(_table);
	
	_tableDitte = $('#ditte').DataTable( {
		"data": ditteInvitate,
		paging: false,
		"searching": false,
		"scrollY": "200px",
		//"scrollCollapse": true,
		"columnDefs": [
			{
				"data": "numordpl",
				"visible": true,
				"targets": [ 0 ],
				"sWidth": "100px",
				"sClass": "aligncenter",
				"sTitle": "N.pl."
			},
			{
				"data": "nomimo",
				"visible": true,
				"sTitle": "Ragione sociale",
				"targets": [ 1 ]
			},
			{
				"data": "ribauo",
				"visible": ribauoVisibile,
				"targets": [ 2 ],
				"sWidth": "120px",
				"sClass": "aligncenter",
				"sTitle": "Ribasso offerto",
				"mRender": function (data, type, full) {
					if(data!=null && type == "display"){
    					return _formatRibasso(data);
					}else {
						return data;
					}
				}
			},
			{
				"data": "impoff",
				"visible": impoffVisibile,
				"targets": [ 3 ],
				"sWidth": "120px",
				"sClass": "aligncenter",
				"sTitle": "Importo offerto",
				"mRender": function (data, type, full) {
					if(data!=null && type == "display"){
    					return _formattaImporto(data);
					}else {
						return data;
					}
				}
			},
			{
				"data": "totril",
				"visible": true,
				"targets": [ 4 ],
				"sWidth": "100px",
				"sClass": "aligncenter",
				"sTitle": "N. rilanci" 
			},
			{
				"data": "ditta",
				"visible": false,
				"targets": [ 5 ],
				"sClass": "aligncenter",
				"sTitle": "Ditta"
			}
		],
        "language": {
			"sEmptyTable":     "Nessuna ditta trovata",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ ditte",
			"sInfoEmpty":      "Nessun elemento trovato",
			"sInfoFiltered":   "(su _MAX_ elementi documentali totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_ ditte invitate",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sZeroRecords":    "Nessun elemento trovato"
		},
		complete: function() {
			_nowait();
		},
		"order": [[ colonnaOrdinamentoIni , "asc" ]]
    });
	
	    	
	
	
}


function _getDatiRilanciDitta(ditta,ragSociale) {
	_wait();
	
	if(!$('#titolomessaggio').is(':visible')){
		$( '#titolomessaggio' ).show( "fast" );
	}
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: true,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetRilanciDittaAsta.do",
		data : "ngara=" +  ngara + "&ditta=" + ditta,
		success: function(json){
			_popolaTabellaRilanciDitta(json.iTotalRecords, json.data);
			$("#nomeDitta").text(ragSociale);
		},
		error: function(e){
			 alert("Errore durante la lettura dei rilanci della ditta");
		},
		complete: function() {
			_nowait();
		}
	});
	
	
}


/*
 * Popola la lista dei rilanci di una ditta
 */
function _popolaTabellaRilanciDitta(numeroTotale, rilanci) {
	if (_tableRilanciDitta != null) {
		_tableRilanciDitta.destroy(true);
		$('#rilanci').empty();
	}
	
	_setPercorsoImg($("#percorsoCartellaImg").val());
	
	var ribauoVisibile=(ribcal==1);
	var impoffVisibile=(ribcal==2 || modlicg ==5 || modlicg == 14);
	var linkDettaglioVisibile = (modlicg == 5 || modlicg == 14);
	
	var _table = $('<table/>', {"id": "rilanci", "class": "rilanci", "cellspacing": "0", "width" : "100%"});
	var _thead = $('<thead/>');
	var _tr = $('<tr/>');
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_thead.append(_tr);
	_table.append(_thead);
	var _tbody = $('<tbody/>');
	_table.append(_tbody);
	$("#rilancicontainer").append(_table);
	
	_tableRilanciDitta = $('#rilanci').DataTable( {
		"data": rilanci,
		paging: false,
		"searching": false,
		"scrollY": "150px",
		//"scrollCollapse": true,
		"columnDefs": [
			{
				"data": "numfase",
				"visible": true,
				"sWidth": "50px",
				"sClass": "aligncenter",
				"sTitle": "N. fase",
				"orderable": false,
				"targets": [ 0 ]
			},
			{
				"data": "dataoraril",
				"visible": true,
				"sClass": "aligncenter",
				"sTitle": "Data rilancio",
				"orderable": false,
				"targets": [ 1 ],
				"mRender": function (data, type, full) {
					if(data!=null && type == "display"){
    					var d = new Date();
    					d.setTime(data);
    					var g= d.getDate();
    					if(g<10)
    						g = '0' + g;
    					var m = d.getMonth() + 1;
    					if(m<10)
    						m = '0' + m;
    					var a = d.getFullYear(); 
    					var o = d.getHours();
    					if(o<10)
    						o = '0' + o;
    					var mm = d.getMinutes();
    					if(mm<10)
    						mm = '0' + mm;
    					var s = d.getSeconds();
    					if(s<10)
    						s = '0' + s;
    					var dataString = g + '/' + m + '/' + a + ' ' + o + ':' + mm + ':' + s;
    					return dataString;
					}else {
						return data;
					}
				},
				"fnCreatedCell": function (nTd, sData, oData, iRow, iCol) {
		            if(oData.numril == -1){
		            	$(nTd).html("<b>Offerta iniziale</b>");
		            }
				}
			},
			{
				"data": "ribauo",
				"visible": ribauoVisibile,
				"targets": [ 2 ],
				"sWidth": "120px",
				"sClass": "aligncenter",
				"orderable": false,
				"sTitle": "Ribasso offerto",
				"mRender": function (data, type, full) {
					if(data!=null && type == "display"){
    					return _formatRibasso(data);
					}else {
						return data;
					}
				}
			},
			{
				"data": "impoff",
				"visible": impoffVisibile,
				"targets": [ 3 ],
				"sWidth": "120px",
				"sClass": "aligncenter",
				"orderable": false,
				"sTitle": "Importo offerto",
				"mRender": function (data, type, full) {
					if(data!=null && type == "display"){
    					return _formattaImporto(data);
					}else {
						return data;
					}
				}
			},
			{
				"data": "id",
				"visible": false,
				"targets": [ 4 ],
				"sClass": "aligncenter",
				"sTitle": "Id"
			},
			{
				"data": "ditta",
				"visible": false,
				"targets": [ 5 ],
				"sClass": "aligncenter",
				"sTitle": "Ditta"
			},
			{
				"data": "numril",
				"visible": false,
				"targets": [ 6 ],
				"sClass": "aligncenter",
				"sTitle": "Numril"
			},
			{
				"data": null,
				"visible": linkDettaglioVisibile,
				"targets": [ 7 ],
				"sWidth": "50px",
				"sClass": "aligncenter",
				"orderable": false,
				"defaultContent": '<img width="16" height="16" title="Dettaglio offerta prezzi" alt="Dettaglio offerta prezzi" src="' + percorsoImg + 'offertaprezzi.png" class="dettaglioPrezzi"/>'
			}
        ],
        "language": {
			"sEmptyTable":     "Nessun rilancio trovato",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ rilanci",
			"sInfoEmpty":      "Nessun rilancio trovato",
			"sInfoFiltered":   "(su _MAX_ mittenti totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_ rilanci",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sZeroRecords":    "Nessun rilancio trovato"
		},
		"order": [[ 6 , "desc" ]]
    });
	
	
	$("body").delegate(".dettaglioPrezzi", "click", function() {
	    if(modlicg == 5 || modlicg == 14){
	    	var r = _tableRilanciDitta.row($(this).parents('tr').first()).data();
	        var ditta = r.ditta;
	        var id = r.id;
	        var dataRilancio = r.dataoraril;
	        _dettaglioPrezziUnitariDittaRilancio(id,ditta,dataRilancio);
	    }
		
   	});
}


/*
 *  Costruzione e popolamento della finsestra modale dei rilanci della ditta
 */

function _dettaglioPrezziUnitariDittaRilancio(id,ditta,dataRilancio) {
	clearTimeout(timer);
	$(".ui-dialog-titlebar").hide();
	_getDatiPrezziUnitariDitta(id,ditta,dataRilancio);
	$("#dettaglioPrezziUnitari").dialog( "option", { tipoFase: tipoFase } );
	$("#dettaglioPrezziUnitari").dialog("open");
	$("#dettaglioPrezziUnitari").height(550);
	
}

function _getDatiPrezziUnitariDitta(id,ditta,dataRilancio) {
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
		url: "pg/GetPrezziUnitariDittaAsta.do",
		data : "ngara=" +  ngara + "&ditta=" + ditta + "&id=" + id,
		success: function(json){
			_popolaTabellaPrezziUnitariDitta(json.data);
			var titoloDettaglioPrezziUnitari = "Dettaglio offerta prezzi della ditta <b>" + json.Ragsoc + "</b>";
			if(dataRilancio!=null && dataRilancio!=""){
				var d = new Date();
				d.setTime(dataRilancio);
				var g= d.getDate();
				if(g<10)
					g = '0' + g;
				var m = d.getMonth() + 1;
				if(m<10)
					m = '0' + m;
				var a = d.getFullYear(); 
				var o = d.getHours();
				if(o<10)
					o = '0' + o;
				var mm = d.getMinutes();
				if(mm<10)
					mm = '0' + mm;
				var s = d.getSeconds();
				if(s<10)
					s = '0' + s;
				var dataString = g + '/' + m + '/' + a + ' ' + o + ':' + mm + ':' + s;
				titoloDettaglioPrezziUnitari += " relativo al rilancio del <b>" + dataString +"</b>";
			}
			
			$("#titoloDettaglioPrezziUnitari").html(titoloDettaglioPrezziUnitari);
		},
		error: function(e){
			 alert("Errore durante la lettura dei rilanci della ditta");
		},
		complete: function() {
			_nowait();
        }
	});
	
	
}


/*
 * Popola la lista del dettaglio prezzi di una ditta
 */
function _popolaTabellaPrezziUnitariDitta(prezzi) {
	
	if (_tablePrezziUnitari != null) {
		_tablePrezziUnitari.destroy(true);
	}
	
	var _table = $('<table/>', {"id": "prezzi", "class": "prezzi", "cellspacing": "0", "width" : "100%"});
	var _thead = $('<thead/>');
	var _tr = $('<tr/>');
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_thead.append(_tr);
	_table.append(_thead);
	var _tbody = $('<tbody/>');
	_table.append(_tbody);
	$("#listaPrezziUnitaricontainer").append(_table);
	
	_tablePrezziUnitari = $('#prezzi').DataTable( {
		"data": prezzi,
		paging: false,
		"searching": false,
		"columnDefs": [
			{
				"data": "codvoc",
				"visible": true,
				"sWidth": "50px",
				"sTitle": "Codice",
				"targets": [ 0 ]
			},
			{
				"data": "voce",
				"visible": true,
				"sWidth": "100px",
				"sTitle": "Descrizione",
				"targets": [ 1 ]
			},
			{
				"data": "unimis",
				"visible": true,
				"targets": [ 2 ],
				"sWidth": "100px",
				"sClass": "aligncenter",
				"sTitle": "Unit&agrave; di misura"
			},
			{
				"data": "quantieff",
				"visible": true,
				"targets": [ 3 ],
				"sWidth": "100px",
				"sClass": "aligncenter",
				"sTitle": "Quantit&agrave;",
				"mRender": function (data, type, full) {
					if(data!=null && type == "display"){
    					return _formattaQuantita(data);
					}else {
						return data;
					}
				}
			},
			{
				"data": "preoff",
				"visible": true,
				"targets": [ 4 ],
				"sWidth": "20px",
				"sClass": "aligncenter",
				"sTitle": "Prezzo",
				"mRender": function (data, type, full) {
					if(data!=null && type == "display"){
    					return _formattaImporto(data);
					}else {
						return data;
					}
				}
			},
			{
				"data": "impoff",
				"visible": true,
				"targets": [ 5 ],
				"sWidth": "20px",
				"sClass": "aligncenter",
				"sTitle": "Importo",
				"mRender": function (data, type, full) {
					if(data!=null && type == "display"){
    					return _formattaImporto(data);
					}else {
						return data;
					}
				}
			}
        ],
        "language": {
			"sEmptyTable":     "Nessun dettaglio prezzi unitari trovato",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ prezzi unitari",
			"sInfoEmpty":      "Nessun dettaglio prezzi unitari trovato",
			"sInfoFiltered":   "(su _MAX_ mittenti totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_ prezzi unitari",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sZeroRecords":    "Nessun dettaglio prezzi unitari trovato"
		}
    });
	
	
	
}

/*
 * Formattazione del ribasso
 */
function _formatRibasso(valore){
	var newdata = valore.toString().replace(".", ",");
	newdata+= " %";
	return newdata;
}

/*
 * Formattazione dell'importo
 */
function _formattaImporto(importo) {
	var importoFormattato = "";
	if (importo != null) {
    	var field = $('<p/>',{text: importo});
    	field.formatCurrency({decimalSymbol: ",", digitGroupSymbol : ".", symbol: "", roundToDecimalPlace: "5"});

		var numero = field.text();
		if (numero.substring(numero.length - 1) == '0') {
			numero = numero.substring(0, numero.length - 1);
		}

		if (numero.substring(numero.length - 1) == '0') {
			numero = numero.substring(0, numero.length - 1);
		}	

		if (numero.substring(numero.length - 1) == '0') {
			numero = numero.substring(0, numero.length - 1);
		}			
		
    	importoFormattato = numero + " \u20AC";
	}
	return importoFormattato;
}

/*
 * Formattazione della quantita
 */
function _formattaQuantita(quantita) {
	var quantitaFormattata = "";
	if (quantita != null) {
    	var field = $('<p/>',{text: quantita});
    	
    	var numberOfDecimals = 0;
		if(Math.floor(quantita) === quantita) {
			numberOfDecimals = 0;
		} else {
			numberOfDecimals = quantita.toString().split(".")[1].length || 0; 
		}
		
		var roundToDecimalPlace = 0;
		if (numberOfDecimals > 0) {
			roundToDecimalPlace = numberOfDecimals;
		}
		
		if (roundToDecimalPlace > 5) {
			roundToDecimalPlace = 5;
		}
    	
    	field.formatCurrency({decimalSymbol: ",", digitGroupSymbol : ".", symbol: "", roundToDecimalPlace: roundToDecimalPlace});
    	quantitaFormattata = field.text();
	}
	return quantitaFormattata;
}





$(window).ready(function (){
	
	ngara = $("#GARE_NGARA").val();
	
	
	/*
	 * Definizione proprieta' della maschera a lista del dettaglio dei prezzi unitari per ditta.
	 */
	$( "#dettaglioPrezziUnitari" ).dialog({
		autoOpen: false,
		width: 1000,
		show: {
			effect: "blind",
			duration: 500
	    },
	    hide: {
	    	effect: "blind",
	    	duration: 500
	    },
		position: {
			my: "left top",
			at: "left top",
			of: ".arealavoro"
		},
	    modal: true,
	    resizable: true,
		focusCleanup: true,
		cache: false,
	    buttons: {
	        "Chiudi" : function() {
	        	var options = $(this).dialog( "option" );
        		$(this).dialog( "close" );
	        	if(options.tipoFase==1)
	        			timer = setInterval(calcolaTempoResiduo,1000);
	        }
	      }
	});
	
	
	_popolaDatiFase();
	_getDatiDitteInvitate();
	
	/*
	 *  Se il numero di rilanci è maggiore di zero si visualizza la lista dei rilanci,
	 *  altrimenti si nasconde
	 */
	$("body").delegate("#ditte tbody tr", "click", function() {
	    if ($(this).hasClass("selected")) {
	        $(this).removeClass("selected");
	    }else {
	    	_tableDitte.$("tr.selected").removeClass("selected");
	        $(this).addClass("selected");
	        var r = _tableDitte.row(this).data();
	        var numeroRilanci = r.totril;
	        if(numeroRilanci>0){
	        	var ditta = r.ditta;
    	        var ragSoc = r.nomimo;
    	        _getDatiRilanciDitta(ditta,ragSoc);
	        }else{
	        	if (_tableRilanciDitta != null) {
	        		_tableRilanciDitta.destroy(true);
	        	}
	        	if($('#titolomessaggio').is(':visible'))
	        		$('#titolomessaggio').hide();
	        }
	        
	    }
   	});
	
	/*
	 * Lancio manuale dell'aggiornamento dei dati.
	 * Viene reimpostato da capo il conteggio automatico
	 */
	$("#ricaricaDitte").click(
		function() {
			clearTimeout(timer);
			aggiornaDati();
			$("#tempoResiduo").text(sogliaAggiornamento);
			timer = setInterval(calcolaTempoResiduo,1000);
		}
	);
	
	if(tipoFase==1){
		timer = setInterval(calcolaTempoResiduo,1000);
	}
});


/*
 * Funzione per forzare l'aggiornamento dei dati, senza ricaricare la pagina
 */
function aggiornaDati(){
	_popolaDatiFase();
	_getDatiDitteInvitate();
	if($('#titolomessaggio').is(':visible'))
		$('#titolomessaggio').hide();
	if (_tableRilanciDitta != null) {
		_tableRilanciDitta.destroy(true);
	}
}

/*
 * Conteggio inverso del tempo residuo per l'aggiornamento automatico dei dati
 * Dopo che si esegue l'aggiornamento il conteggio riparte da capo
 */
function calcolaTempoResiduo() {
	var tempoResiduo = $("#tempoResiduo").text();
	tempoResiduo = tempoResiduo - 1;
	$("#tempoResiduo").text(tempoResiduo);
	if (tempoResiduo <= 0) {
		//clearTimeout(t);
		//reindirizzaLogin();
		aggiornaDati();
		$("#tempoResiduo").text(sogliaAggiornamento);
	}
}


	

