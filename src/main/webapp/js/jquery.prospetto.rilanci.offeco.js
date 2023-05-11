/*
*	Funzioni di supporto per la gestione
*	della pagina popupProspettoRilanciOffEco.jsp.
*
*/

var timer;
var ngara ;
var modlicg;
var ribcal;
var formato;
var _tableDitte = null;
var _tableRilanciDitta = null;

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
 * Elenco ditte che hanno presentato l'offerta economica.
 */
function _getDatiDitte() {
	
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
		url: "pg/GetDatiDitteRilanciOffEco.do",
		data : "ngara=" +  ngara + "&modlicg=" + modlicg,
		success: function(json){
			ribcal = json.ribcal;
			formato = json.formato;
			_popolaTabellaDitte(json.iTotalRecords, json.data);
		},
		error: function(e){
			 alert("Errore durante la lettura delle ditte che hanno presentato l'offerta economica");
		},
		complete: function() {
			_nowait();
        }
	});
}


/*
 * Popola la lista delle ditte della fase apertura offerta economica
 */
function _popolaTabellaDitte(numDitte, ditteOffEco) {
	
	if (_tableDitte != null) {
		_tableDitte.destroy(true);
	}
		
	var _table = $('<table/>', {"id": "ditte", "class": "ditte", "cellspacing": "0", "width" : "100%"});
	var _thead = $('<thead/>');
	var _tr = $('<tr/>');
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
		"data": ditteOffEco,
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
				"data": "numril",
				"visible": true,
				"targets": [ 2 ],
				"sWidth": "100px",
				"sClass": "aligncenter",
				"sTitle": "N. rilanci" 
			},
			{
				"data": "ditta",
				"visible": false,
				"targets": [ 3 ],
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
		"order": [[ 0, "asc" ]]
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
		url: "pg/GetRilanciDittaOffEco.do",
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
		
	var ribauoVisibile = (modlicg!=6 || (modlicg==6 && formato==51));
	var impoffVisibile = ((modlicg!=6 && ribcal==2) || (modlicg==6 && (formato==50 || formato==52)));
	var ribauoTooltip =  "Ribasso offerto";
	if(modlicg == 17){
		ribauoTooltip = "Rialzo offerto";
	}
	
	var _table = $('<table/>', {"id": "rilanci", "class": "rilanci", "cellspacing": "0", "width" : "100%"});
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
	$("#rilancicontainer").append(_table);
	
	_tableRilanciDitta = $('#rilanci').DataTable( {
		"data": rilanci,
		paging: false,
		"searching": false,
		"scrollY": "150px",
		//"scrollCollapse": true,
		"columnDefs": [
			
			{
				"data": "dataoraril",
				"visible": true,
				"sClass": "aligncenter",
				"sTitle": "Data aggiorn. off.economica",
				"orderable": false,
				"targets": [ 0 ],
				"sWidth": "200px",
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
				"data": "ngararil",
				"sTitle": "Codice gara rilancio",
				"orderable": false,
				"sClass": "aligncenter",
				"sWidth": "150px",
				"targets": [ 1 ]
			},
			{
				"data": "dataorateroff",
				"visible": true,
				"sClass": "aligncenter",
				"sTitle": "Termine presentazione rilancio",
				"orderable": false,
				"targets": [ 2 ],
				"sWidth": "200px",
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
				}
			},
			{
				"data": "ribauo",
				"visible": ribauoVisibile,
				"targets": [ 3 ],
				"sWidth": "120px",
				"sClass": "aligncenter",
				"orderable": false,
				"sTitle": ribauoTooltip,
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
				"targets": [ 4 ],
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
				"data": "numril",
				"visible": false,
				"targets": [ 5 ],
				"sClass": "aligncenter",
				"sTitle": "Numril"
			},
					
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
		"order": [[ 5 , "desc" ]]
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


$(window).ready(function (){
	
	ngara = $("#ngara").val();
	modlicg = $("#modlicg").val();
	
	_getDatiDitte();
	
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
	        var numeroRilanci = r.numril;
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
	
});


