/** 
 * Gestione ricerca uffici
 * 
 */

var _tableListaUffici = null;
var _ctx = null;

/*
var myWsdmUffici = myWsdmUffici || (function(){
	var _ctx;
	return {
		init: function(ctx) {
			_ctx = ctx;
		},
		creaFinestraListaUffici: function() {
			_creaFinestraListaUffici();
		},
		getCtx: function() {
			return _ctx;
		}
	};
}());
*/

$(window).ready(function (){
	_creaFinestraListaUffici();
});

var delay = (function(){
	  var timer = 0;
	  return function(callback, ms){
	    clearTimeout (timer);
	    timer = setTimeout(callback, ms);
	  };
})();


/**
 * Crea la finestra modale con la lista degli uffici
 */
function _creaFinestraListaUffici() {
	var _finestraListaUffici = $("<div/>",{"id": "finestraListaUffici", "title":"Ricerca"});
	_finestraListaUffici.dialog({
		open: function(event, ui) { 
			$(this).parent().css("background","#FFFFFF");
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
       	show: {
       		effect: "blind",
       		duration: 350
           },
        hide: {
           	effect: "blind",
           	duration: 350
        },
        resizable: true,
   		height: 530,
   		width: 1100,
   		minHeight: 350,
   		minWidth: 650,
   		modal: true,
   		focusCleanup: true,
   		cache: false,
        buttons: {
        "Chiudi" : function() {
           		$(this).dialog( "close" );
        	}
        }
	});
		
	_finestraListaUffici.on( "dialogclose", function( event, ui ) {
		if (_tableListaUffici != null) {
			_tableListaUffici.destroy(true);
		}
		$("#listaUfficiContainer").remove();
	});
	
} 

/**
 * Crea il contenitore del datatable con la lista degli uffici
 */
function _creaContainerListaUffici() {
	var _container = $("<table/>", {"id": "listaUfficiContainer", "class": "dettaglio-notab"});
	_container.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	var _tr = $("<tr/>");
	var _td = $("<td/>");
	_td.css("border-bottom","0px");
	
	var _divSearchUfficiContainer = $("<div/>", {"margin-top": "25px", "margin-bottom": "25px"});
	var _divListaUfficiContainer = $("<div/>", {"id": "divListaUfficiContainer", "margin-top": "25px", "margin-bottom": "25px", "width" : "98%"});
	
	var _span_search_descrizione = $("<span/>");
	var _search_descrizione = $("<input/>",{"id":"search_descrizione","size": "70","width":"250px"});
	_span_search_descrizione.append("Descrizione&nbsp;(*)&nbsp;&nbsp;");
	_span_search_descrizione.append(_search_descrizione);
	_divSearchUfficiContainer.append(_span_search_descrizione).append("<br>");
	_divSearchUfficiContainer.append("<br>").append("<i>(*) Per avviare la ricerca indicare almeno tre caratteri</i>");
	
	_td.append(_divSearchUfficiContainer);
	_td.append(_divListaUfficiContainer);
	_td.append("<br/>");
	_tr.append(_td);
	_container.append(_tr);
	$("#finestraListaUffici").append(_container);
	
	_creaListaUffici();
	
	$("body").delegate("#search_descrizione", "keyup", function() {
    	delay(function(){
    		_searchUffici();
    	}, 800);
	});

}

function _searchUffici() {
	var search_descrizione = $("#search_descrizione").val();
	search_descrizione = search_descrizione.trim();
	if (search_descrizione.length > 2) {
		_getListaUffici();	
	} else {
		if (_tableListaUffici != null) {
			_tableListaUffici.destroy(true);
			_creaListaUffici();
		}
	}
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

/**
 * Crea la tabella di base per la lista degli uffici
 */
function _creaListaUffici() {
	
	$("#listaUffici").remove();
	
	var _table = $("<table/>", {"id": "listaUffici", "class": "uffici", "cellspacing": "0", "width" : "100%"});
	var _thead = $("<thead/>");
	var _tr0 = $("<tr/>",{"class":"intestazione"});
	_tr0.append($("<th/>",{"text":"Scegli","class":"associa"}));
	_tr0.append($("<th/>",{"text":"Codice"}));
	_tr0.append($("<th/>",{"text":"Descrizione"}));
 	_thead.append(_tr0);
 	_table.append(_thead);
 	var _tfoot = $('<tfoot/>');
 	var _tr1 = $('<tr/>',{"class":"intestazione"});
	_tr1.append($('<th/>'));
	_tr1.append($('<th/>'));
	_tr1.append($('<th/>'));
	_tfoot.append(_tr1);
	_table.append(_tfoot);
 	$("#listaUfficiContainer").append(_table);
}

function _scegliUfficio(iRow) {
	_waituffici();
	
	var _codice = _tableListaUffici.row(iRow).data().codice;
	var _descrizione = _tableListaUffici.row(iRow).data().descrizione;
	
	$("#uocompetenza").val(_codice);
	$("#uocompetenzadescrizione").val(_descrizione);
	var testoUoCompetenza="";
	if(_codice!=null)
		testoUoCompetenza=_codice;
	if(testoUoCompetenza!="" && _descrizione!=null)
		testoUoCompetenza+= " - "; 
	if(_descrizione!=null)
		testoUoCompetenza+=_descrizione;
	$("#uocompetenzaTxt").val(testoUoCompetenza);	
			
	$("#finestraListaUffici").dialog("close");
	_nowaituffici();
}

function aggiornaUnitaCompetenzaDb(codice,descr){
	_waituffici();
	var esito;
	$.ajax({
		type: "POST",
		async: false,
		dataType: "json",
		url: "pg/SetUnitaCompetenza.do",
		data : {
			entita: $("#entita").val(),
			key1: $("#key1").val(),
			uocompetenza: $("#uocompetenza").val(),
			uocompetenzadescrizione: $("#uocompetenzadescrizione").val(),
			gara: $("#gara").val()
			
		},
		success: function(json) {
			if (json) {
				if (json.esito == true) {
					esito=1;
				} else {
					esito=-1;
				}
			}
		},
		error: function(e) {
			esito=-1;
		},
		complete: function(){
			_nowaituffici();
			
		}
	});
	return esito;
}

/**
 * Lettura della lista 
 * 
 */
function _getListaUffici() {

	if (_tableListaUffici != null) {
		_tableListaUffici.destroy(true);
	}
	
	_creaListaUffici();

	var search_descrizione = $("#search_descrizione").val();
	search_descrizione = search_descrizione.trim();
	
	var servizio = $("#servizio").val();
	var idconfi = $("#idconfi").val();
	var tiposistemaremoto = $("#tiposistemaremoto").val();
	var idprofiloutente = $("#idprofiloutente").val();
	
	var gara = _codiceGara;
		
	_tableListaUffici = $("#listaUffici").DataTable( {
		"ajax": {
			type: "POST",
			async: false,
			dataType: "json",
			url: "pg/GetWSDMListaUffici.do",
			"data": {
				username: $("#username").val(),
				password: $("#password").val(),
				descrizioneUfficio: search_descrizione,
				ruolo: $("#ruolo option:selected").val(),
				servizio: $("#servizio").val(),
				idconfi: $("#idconfi").val(),
				idprofiloutente : idprofiloutente,
				gara: gara,
				tipo: "unitaOp"
			},
			"dataSrc": function ( jsonResult ) {
				_nowaituffici();
				if (jsonResult.esito == false) {
					$("#listaUffici").dataTable().fnSettings().oLanguage.sEmptyTable = jsonResult.messaggio;
				}
				return jsonResult.data;
			},
			"error": function ( e ) {
				_nowaituffici();
				alert("Si e' verificato un errore durante l'interazione con i servizi per la lettura della lista delle unita' operative");
			}
		},
		"columns": [
			{
				"targets": 0,
				"data": null,
				"bSortable": false,
				"sClass": "associa",
		        "fnCreatedCell": function (nTd, sData, oData, iRow, iCol) {
		        	var _html = "<a href='javascript:_scegliUfficio(" + iRow + ")'>";
		        	_html += "<img title='Scegli unit&agrave; operativa' height='20' width='20' alt='Scegli unit&agrave; operativa' src='" + _ctx +  "/img/Edition-49.png'>";
				    _html += "</a>";
				    $(nTd).html(_html);
		        }
			},
			{ "data": "codice" , "defaultContent": "", "className": "center"},
			{ "data": "descrizione" , "defaultContent": "", "className": "center"},
		],
		"language": {
			"sEmptyTable":     "Nessuna unit&agrave; operativa trovata",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ unit&agrave; operative",
			"sInfoEmpty":      "",
			"sInfoFiltered":   "(su _MAX_ uffici totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_",
			"sLoadingRecords": "Elaborazione",
			"sProcessing":     "Elaborazione",
			"sSearch":         "Cerca unit&agrave; operative",
			"sZeroRecords":    "Nessuna unit&agrave; operativa trovata",
			"oPaginate": {
				"sFirst":      "<<",
				"sPrevious":   "<",
				"sNext":       ">",
				"sLast":       ">>"
			}
		},
		"order": [[ 1, "asc" ],[2, "asc"]],
		"lengthMenu": [[10], ["10"]]
	});
	
	$("#listaUffici_length").hide();
	$("#listaUffici_filter").hide();
	
}

jQuery.fn.waitufficicenter = function () {
    this.css("position","absolute");
    this.css("top", "200px");
    this.css("left", Math.max(0, (($(window).width() - $(this).outerWidth()) / 2) + $(window).scrollLeft()) + "px");
    return this;
}

function _waituffici() {
	$("#bloccaScreen").css("visibility","visible");
	$('#bloccaScreen').css("width",$(document).width());
	$('#bloccaScreen').css("height",$(document).height());
	$("#wait").css("visibility","visible");
	$("#wait").waitufficicenter();
}

function _nowaituffici() {
	document.getElementById('bloccaScreen').style.visibility='hidden';
	document.getElementById('wait').style.visibility='hidden';
}

