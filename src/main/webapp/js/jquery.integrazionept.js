

var myIntegrazionePT = myIntegrazionePT || (function(){
	
	var _ctx;
	var _endpoint;
	var _codlav;
	var _tipo;

	var _args = {};
	return {
		init: function(ctx,endpoint,codlav,tipo) {
			_ctx = ctx;
			_endpoint = endpoint;
			
			var _lastchar = _endpoint.substr(_endpoint.length - 1);
			if (_lastchar == '/') {
				_endpoint = _endpoint.substr(0, _endpoint.length - 1);
			}
			
			_codlav = codlav;
			_tipo = tipo;
			
		},
		creaFinestraSchedaIntervento: function() {
			_creaFinestraSchedaIntervento();
		},
		creaLinkSchedaIntervento: function(anchor) {
			var _cui = $("#GARE1_CODCUI").val();
			if (_cui != null && _cui.trim() != "") {
				_creaLinkSchedaIntervento(anchor);
			}
		},
		getCtx: function() {
			return _ctx;
		},
		getEndpoint: function() {
			return _endpoint;
		},
		getCodlav: function() {
			return _codlav;
		},
		getTipo: function() {
			return _tipo;
		}
	};
}());


var delay = (function(){
	  var timer = 0;
	  return function(callback, ms){
	    clearTimeout (timer);
	    timer = setTimeout(callback, ms);
	  };
})();


/**
 * Crea la finestra modale per la consultazione dell'intervento
 */
function _creaFinestraSchedaIntervento() {
	var _finestraSchedaIntervento = $("<div/>",{"id": "finestraSchedaIntervento", "title":"Scheda dell'intervento/acquisto"});
	_finestraSchedaIntervento.dialog({
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
   		height: 650,
   		width: 1000,
   		minHeight: 350,
   		minWidth: 650,
		position: { my: "center", of: ".contenitore-arealavoro"},
   		modal: true,
   		focusCleanup: true,
   		cache: false,
        buttons: {
        "Chiudi" : function() {
           		$(this).dialog( "close" );
        	}
        }
	});
		
	_finestraSchedaIntervento.on( "dialogclose", function( event, ui ) {
		$("#schedaInterventoContainer").remove();
		$("#schedaInterventoImportiContainer").remove();
	});
	
} 


/**
 * Utilita' per la creazione del link di accesso alla scheda dell'intervento
 * @param anchor
 */

function _creaLinkSchedaIntervento(anchor) {
 	setTimeout(function(){
		var _linkSchedaIntervento = $("<a/>",{"id": "linkSchedaIntervento", "class": "link-generico", "text": "Consulta intervento/acquisto", "title": "Consulta intervento/acquisto"});
		
		_linkSchedaIntervento.css("margin-left","10px");
		_linkSchedaIntervento.css("display","inline-block");

		anchor.append(_linkSchedaIntervento);
		
		_linkSchedaIntervento.click(function() {
			$("#finestraSchedaIntervento").dialog("open");
			_creaPopolaSchedaIntervento();
		 });
 	}, 300);
 } 


function _creaPopolaSchedaIntervento() {
	
	_wait();
	
	var _table = $("<table/>", {"id": "schedaInterventoContainer", "class": "dettaglio-notab"});
	_table.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	_table.css("border-top","1px solid #A0AABA ");
	_table.css("margin-top","10px");
	
	var descrizioneIntervento = ["Numero intervento CUI",
	     "Descrizione dell'intervento",
	     "RUP",
	     "Annualita' di riferimento",
	     "Livello di priorita'",
	     "Comune luogo di esecuzione del contratto",
	     "Provincia luogo di esecuzione del contratto"];
	
	var campiIntervento = ["CODCUI",
	       "DESINT",
	       "RUP",
	       "ANNTRI",
	       "PRGINT",
	       "LOCALITA",
	       "PROVINCIA"];
	
	var iInterventi;
	for (iInterventi = 0; iInterventi < 7; iInterventi++) {
		var _tr = $("<tr/>");
		var _td1 = $("<td/>", {"text" : descrizioneIntervento[iInterventi]});
		_td1.css("background-color","#EFEFEF");
		_td1.css("border-top","#A0AABA 1px solid");
		_td1.css("width","400px");
		_td1.css("padding-right","10px");
		_td1.css("text-align","right");
		_td1.css("height","25px");
		var _td2 = $("<td/>",{"id": campiIntervento[iInterventi],"class":"valore-dato"});
		_tr.append(_td1).append(_td2);
		_table.append(_tr);
	}
	
	var _tableImporti  = $("<table/>", {"id": "schedaInterventoImportiContainer", "class": "integrazionept", "width" : "100%"});
	_tableImporti.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	_tableImporti.css("border-top","1px solid #A0AABA ");
	_tableImporti.css("margin-top","30px");
	
	// Prima riga del titolo
	var _tr1 = $("<tr/>", {"class" : "intestazione"});
	var _th1 = $("<th/>",{"colspan": "6", "text": "Quadro delle risorse"});
	_tr1.append(_th1);
	_tableImporti.append(_tr1);
	
	// Seconda riga del titolo
	var _tr2 = $("<tr/>", {"class" : "intestazione"});
	var _th1 = $("<th/>",{"rowspan": "2", "text": "Tipologia risorse"});
	var _th2 = $("<th/>",{"colspan": "5", "text": "Stima dei costi"});
	_tr2.append(_th1).append(_th2);
	_tableImporti.append(_tr2);
	
	// Terza riga del titolo
	var _tr3 = $("<tr/>", {"class" : "intestazione"});
	var _th2 = $("<th/>",{"text": "Primo anno"});
	var _th3 = $("<th/>",{"text": "Secondo anno"});
	var _th4 = $("<th/>",{"text": "Terzo anno"});
	var _th5 = $("<th/>",{"text": "Annualita' successive"});
	var _th6 = $("<th/>",{"text": "Totale"});
	_tr3.append(_th2).append(_th3).append(_th4).append(_th5).append(_th6);
	_tableImporti.append(_tr3);
	
	// Righe del prospetto (in totale 11 righe compresi IVA e totale, e 6 colonne)
	var tipologia = ["Risorse derivanti da entrate aventi destinazione vincolata per legge",
			"Risorse derivanti da entrate acquisite mediante contrazione di mutuo",
			"Risorse acquisite mediante apporti di capitale privato",
			"Stanziamenti di bilancio",
			"Finanziamenti art. 3 DL 310/1990",
			"Risorse derivanti da trasferimento immobili",
			"Altra tipologia",
			"Totale"]; 
	
	var primoAnno = ["DV1TRI","MU1TRI","PR1TRI","BI1TRI","AP1TRI","IM1TRI","AL1TRI","TO1INT"];
	var secondoAnno = ["DV2TRI","MU2TRI","PR2TRI","BI2TRI","AP2TRI","IM2TRI","AL2TRI","TO2INT"];
	var terzoAnno = ["DV3TRI","MU3TRI","PR3TRI","BI3TRI","AP3TRI","IM3TRI","AL3TRI","TO3INT"];
	var xAnno = ["DV9TRI","MU9TRI","PR9TRI","BI9TRI","AP9TRI","IM9TRI","AL9TRI","TO9INT"];
	var totale = ["DVNTRI","MUNTRI","PRNTRI","BINTRI","APNTRI","IMNTRI","ALNTRI","TOTINT"];
	
	var iImporti;
	for (iImporti = 0; iImporti < 8; iImporti++) {
		var _trp = $("<tr/>");
		var _tdp1 = $("<td/>", {"text" : tipologia[iImporti]});
		_tdp1.css("text-align", "right");
		
		var _tdp2 = $("<td/>");
		var _span2 = $("<span/>",{"id": primoAnno[iImporti], "text": "0,00"});
		_tdp2.css("text-align", "right");
		_tdp2.append(_span2).append("&nbsp;&euro;");
		
		var _tdp3 = $("<td/>");
		var _span3 = $("<span/>",{"id": secondoAnno[iImporti], "text": "0,00"});
		_tdp3.css("text-align", "right");
		_tdp3.append(_span3).append("&nbsp;&euro;");
		
		var _tdp4 = $("<td/>");
		var _span4 = $("<span/>",{"id": terzoAnno[iImporti], "text": "0,00"});
		_tdp4.css("text-align", "right");
		_tdp4.append(_span4).append("&nbsp;&euro;");
	
		var _tdp5 = $("<td/>");
		var _span5 = $("<span/>",{"id": xAnno[iImporti], "text": "0,00"});
		_tdp5.css("text-align", "right");
		_tdp5.append(_span5).append("&nbsp;&euro;");
		
		var _tdp6 = $("<td/>");
		var _span6 = $("<span/>",{"id": totale[iImporti], "text": "0,00"});
		_tdp6.css("text-align", "right");
		_tdp6.append(_span6).append("&nbsp;&euro;");
		
		_trp.append(_tdp1).append(_tdp2).append(_tdp3).append(_tdp4).append(_tdp5).append(_tdp6);
		_tableImporti.append(_trp);
	}
	
	var _cui = $("#GARE1_CODCUI").val();
	
	$.ajax({
		"type": "POST",
		"dataType": "json",
		"async": true,
		"beforeSend": function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=utf-8");
			}
		},
		"url": myIntegrazionePT.getCtx() + "/pg/GetIPTSchedaIntervento.do",
		"data": {
			"endpoint" : myIntegrazionePT.getEndpoint(),
			"cui" : _cui
		},
		"success": function(jsonResult){
			_nowait();
			if (jsonResult.esito == true) {
				$.each(jsonResult.interventoAcquisto, function(k, v) {
					k = k.replace("'", "\\'");
					$("[id='"+ k + "']").text(v);
				});
			} else {
				var _tr = $("<tr/>");
				var _td1 = $("<td/>",{"colspan":"2"});
				_td1.append("<br>").append(jsonResult.messaggio).append("<br><br>");
				_tr.append(_td1)
				_table.append(_tr);
			}

		},
		"error": function ( e ) {
			_nowait();
			alert("Si e' verificato un errore durante l'interazione con i servizi di integrazione con Programmi Triennali");
		},
		"complete" : function() {

		}
	});
	
	$("#finestraSchedaIntervento").append(_table).append(_tableImporti);

}


jQuery.fn.waitcenter = function () {
    this.css("position","absolute");
    this.css("top", Math.max(0, (($(window).height() - $(this).outerHeight()) / 2) + $(window).scrollTop()) + "px");
    this.css("left", Math.max(0, (($(window).width() - $(this).outerWidth()) / 2) + $(window).scrollLeft()) + "px");
    return this;
}

function _wait() {
	$("#bloccaScreen").css("visibility","visible");
	$('#bloccaScreen').css("width",$(document).width());
	$('#bloccaScreen').css("height",$(document).height());
	$("#wait").css("visibility","visible");
	$("#wait").waitcenter();
}

function _nowait() {
	document.getElementById('bloccaScreen').style.visibility='hidden';
	document.getElementById('wait').style.visibility='hidden';
}





