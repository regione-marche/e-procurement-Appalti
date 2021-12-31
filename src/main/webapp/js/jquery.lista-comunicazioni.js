/*
*	Gestione del fascicolo e dei documenti protocollati e non
*
*/


$(window).ready(function (){
	
	_wait();
	
	var _tableComunicazioni = null;
	var _tableAllegati = null;
	var _tableDestinatari = null;
	$.fn.dataTable.moment('dd/mm/yy');
	
	$('#tabs').easytabs();
	
	
	/*
	 * Definizioni della maschera di dialogo
	 */
    $( "#dialog-message" ).dialog({
    	autoOpen: false,
    	width: 300,
    	show: {
    		effect: "blind",
    		duration: 200
        },
        hide: {
        	effect: "blind",
        	duration: 200
        },
        modal: true,
        resizable: false,
		cache: false,
		focusCleanup: true,
        buttons: {
            "Ok" : function() {
            	$(this).dialog( "close" );
            }
          }
    });
	
	
    /*
     * Gestione utente ed attributi per il collegamento remoto
     */
	_creaWSLogin();
	_validateWSLogin();
		
	
	/*
	 * Definizioni per il metodo validate()
	 */
	$("#formwsdmdocumento").validate({
		rules: {
			classifica: "required",
			tipodocumento: "required",
			oggetto: "required",
			descrizione: "required",
			mittenteinterno: "required",
			inout: "required",
			numerodestinatari: {
				required: true,
				min: 1
			},
			numeroallegati: {
				required: true,
				min: 1
			}			
		},
		messages: {
			classifica: "Specificare la classifica",
			tipodocumento: "Specificare il tipo documento",
			oggetto: "Specificare l'oggetto",
			descrizione: "Specificare la descrizione",
			mittenteinterno: "Specificare il mittente interno",
			inout: "Specificare il verso di protocollazione",
			numerodestinatari: {
				required: "Deve essere indicato almeno un destinatario",
				min: "Deve essere indicato almeno un destinatario"
			},
			numeroallegati: {
				required: "Deve essere presente almeno un allegato",
				min: "Deve essere presente almeno un allegato"
			}
		},
		errorPlacement: function (error, element) {
			error.insertAfter($(element));
			error.css("margin-right","5px");
			error.css("float", "right");
			error.css("vertical-align", "top");
		}
	});
    
	
	/*
	 * Avvio all'apertura della maschera del popolamento 
	 * della lista degli elementi documentali del fascicolo
	 */
	setTimeout(function(){
		_getListaComunicazioni();
	}, 800);
	
	
	/*
	 * Legge i dati di riferimento dei documenti inviati al sistema remoto di
	 * gestione documentale. I dati di riferimento sono contenuti
	 * nella tabella WSDOCUMENTO.
	 */
	function _getListaComunicazioni() {
		var entita = $("#entita").val();
		var key1 = $("#key1").val();
		var key2 = $("#key2").val();
		var key3 = $("#key3").val();
		var key4 = $("#key4").val();
		
		$.ajax({
			type: "POST",
			dataType: "json",
			async: true,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: "pl/GetListaW_INVCOM.do",
			data: "entita=" + entita + "&key1=" + key1 + "&key2=" + key2 + "&key3=" + key3 + "&key4=" + key4, 
			success: function(json){
				_popolaTabellaComunicazioni(json.iTotalRecords, json.data);
			},
			error: function(e){
				var messaggio = "Errore durante la lettura delle comunicazioni";
				$('#comunicazionimessaggio').text(messaggio);
				$('#comunicazionimessaggio').show(tempo);
			}
		});
	}

	/*
	 * Popola la lista delle comunicazioni disponibili
	 */
	function _popolaTabellaComunicazioni(numeroTotale, comunicazioni) {
		
		if (_tableComunicazioni != null) {
			_tableComunicazioni.destroy(true);
		}
		
		var _table = $('<table/>', {"id": "comunicazioni", "class": "elementi", "cellspacing": "0", "width" : "100%"});
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
		$("#comunicazionicontainer").append(_table);
		
		_tableComunicazioni = $('#comunicazioni').DataTable( {
			"data": comunicazioni,
			"columnDefs": [
	   			{
					"data": "idprg",
					"visible": false,
					"targets": [ 0 ]
				},		               
				{
					"data": "idcom",
					"visible": false,
					"targets": [ 1 ]
				}, 
				{
					"data": "commsgogg",
					"visible": true,
					"sTitle": "Oggetto",
					"targets": [ 2 ]
				},
				{
					"data": "compub",
					"visible": false,
					"sTitle": "Tipo comunicazione",
					"sWidth": "120px",
					"targets": [ 3 ]
				},
				{
					"data": "comdatins",
					"visible": true,
					"sTitle": "Data inserimento",
					"sWidth": "110px",
					"sClass": "aligncenter",
					"type": "date",
					"targets": [ 4 ]
				},
				{
					"data": "comstato",
					"visible": true,
					"sTitle": "Stato",
					"sWidth": "80px",
					"sClass": "aligncenter",
					"targets": [ 5 ]
				},
				{
					"data": "comdatprot",
					"visible": true,
					"sTitle": "Anno protocollo",
					"sWidth": "80px",
					"sClass": "aligncenter",
					"targets": [ 6 ]
				},
				{
					"data": "comnumprot",
					"visible": true,
					"sTitle": "Numero protocollo",
					"sWidth": "80px",
					"sClass": "aligncenter",
					"targets": [ 7 ]
				}
	        ],
	        "language": {
				"sEmptyTable":     "Nessuna comunicazione trovata",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ comunicazioni",
				"sInfoEmpty":      "Nessuna comunicazione trovata",
				"sInfoFiltered":   "(su _MAX_ comunicazioni totali)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_ comunicazioni",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca comunicazioni",
				"sZeroRecords":    "Nessuna comunicazione trovata",
				"oPaginate": {
					"sFirst":      "Prima",
					"sPrevious":   "Precedente",
					"sNext":       "Successiva",
					"sLast":       "Ultima"
				}
			},
			"lengthMenu": [[10, 25, 50, 100, 200], ["10", "25", "50", "100", "200"]],
	        "pagingType": "full_numbers",
	        "bLengthChange" : false,
	        "order": [[ 4, "desc" ]],
	        "aoColumns": [
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true }
			   ]
	    });

		if (numeroTotale == 0) {
			$("#comunicazioni tfoot").hide();
			$("#comunicazioni_info").hide();
			$("#comunicazioni_paginate").hide();
		}

		_nowait();

		$("body").delegate("#comunicazioni tbody tr", "click", function() {
			$('#protocollacomunicazionemessaggio').text("");
			$('#protocollacomunicazionemessaggio').hide();
		    if ($(this).hasClass("selected")) {
		        $(this).removeClass("selected");
		        _removeComunicazione();
		        $("#formwsdmdocumento").validate().resetForm();
		        $("#formwslogin").validate().resetForm();
		    }
		    else {
		    	$("#formwsdmdocumento").validate().resetForm();
		        $("#formwslogin").validate().resetForm();
		    	_tableComunicazioni.$("tr.selected").removeClass("selected");
		        $(this).addClass("selected");
		        var index = _tableComunicazioni.row(this).index();
	    		var r = _tableComunicazioni.row(this).data();
		        _getComunicazione(index, r.idprg, r.idcom);
	    		$("#tabs").fadeIn(tempo);
		    }
	   	});
	}
	
	
	/*
	 * Lettura della singola comunicazione.
	 * Utilizzata per popolare i dati generali della comunicazione,
	 * la lista dei destinatari e la lista degli allegati.
	 */
	function _getComunicazione(index, idprg, idcom) {
		$.ajax({
			type: "POST",
			dataType: "json",
			async: true,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: "pl/GetW_INVCOM.do",
			data: "idprg=" + idprg + "&idcom=" + idcom, 
			success: function(json){
				$.map( json.dataW_INVCOM, function( item ) {
					$("#oggetto").val(item.commsgogg);
					$("#oggetto").prop("readonly", true);
					$("#oggetto").addClass("readonly");
					$("#descrizione").val(item.commsgtes);
					$("#descrizione").prop("readonly", true);
					$("#descrizione").addClass("readonly");
					$('#inout').val("OUT").attr("selected", "selected");
				});
				$("#index").val(index);
				$("#idprg").val(idprg);
				$("#idcom").val(idcom);
				$("#numerodestinatari").val(json.iTotalRecordsW_INVCOMDES);
				$("#numerodestinatari").prop("readonly", true);
				$("#numerodestinatari").addClass("readonly");
				_popolaTabellaDestinatari(json.iTotalRecordsW_INVCOMDES, json.dataW_INVCOMDES);
				$("#numeroallegati").val(json.iTotalRecordsW_DOCDIG);
				$("#numeroallegati").prop("readonly", true);
				$("#numeroallegati").addClass("readonly");				
				_popolaTabellaAllegati(json.iTotalRecordsW_DOCDIG, json.dataW_DOCDIG);
			},
			error: function(e){
				var messaggio = "Errore durante la lettura delle comunicazioni";
				$('#comunicazionimessaggio').text(messaggio);
				$('#comunicazionimessaggio').show(tempo);
			}
		});
	}
	
	/*
	 * Popola la lista dei destinatari della comunicazione selezionata
	 */
	function _popolaTabellaDestinatari(numeroTotale, destinatari) {
		
		if (_tableDestinatari != null) {
			_tableDestinatari.destroy(true);
		}
		
		var _table = $('<table/>', {"id": "destinatari", "class": "elementi", "cellspacing": "0", "width" : "100%"});
		var _thead = $('<thead/>');
		var _tr = $('<tr/>');
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_thead.append(_tr);
		_table.append(_thead);
		var _tbody = $('<tbody/>');
		_table.append(_tbody);
		$("#destinataricontainer").append(_table);
		
		_tableDestinatari = $('#destinatari').DataTable( {
			"data": destinatari,
			"columnDefs": [
				{
					"data": "desintest",
					"visible": true,
					"sWidth": "400px",
					"sTitle": "Intestazione",
					"targets": [ 0 ]
				},
				{
					"data": "desmail",
					"visible": true,
					"sTitle": "Indirizzo email",
					"targets": [ 1 ]
				}
	        ],
	        "language": {
				"sEmptyTable":     "Nessun destinatario trovato",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ destinatari",
				"sInfoEmpty":      "Nessun destinatario trovato",
				"sInfoFiltered":   "(su _MAX_ destinatari totali)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_ destinatari",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca destinatari",
				"sZeroRecords":    "Nessun destinatario trovato",
				"oPaginate": {
					"sFirst":      "Prima",
					"sPrevious":   "Precedente",
					"sNext":       "Successiva",
					"sLast":       "Ultima"
				}
			},
			"lengthMenu": [[10, 25, 50, 100, 200], ["10", "25", "50", "100", "200"]],
	        "pagingType": "full_numbers",
	        "bLengthChange" : false,
	        "order": [[ 0, "desc" ]],
	        "aoColumns": [
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true }
			   ]
	    });

		if (numeroTotale == 0) {
			$("#destinatari tfoot").hide();
			$("#destinatari_info").hide();
			$("#destinatari_paginate").hide();
		}
	}


	/*
	 * Popola la lista degli allegati della comunicazione selezionata
	 */
	function _popolaTabellaAllegati(numeroTotale, allegati) {
		
		if (_tableAllegati != null) {
			_tableAllegati.destroy(true);
		}
		
		var _table = $('<table/>', {"id": "allegati", "class": "elementi", "cellspacing": "0", "width" : "100%"});
		var _thead = $('<thead/>');
		var _tr = $('<tr/>');
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_thead.append(_tr);
		_table.append(_thead);
		var _tbody = $('<tbody/>');
		_table.append(_tbody);
		$("#allegaticontainer").append(_table);
		
		_tableAllegati = $('#allegati').DataTable( {
			"data": allegati,
			"columnDefs": [
				{
					"data": "digdesdoc",
					"visible": true,
					"sWidth": "400px",
					"sTitle": "Titolo",
					"targets": [ 0 ]
				},
				{
					"data": "dignomdoc",
					"visible": true,
					"sTitle": "Nome documento",
					"targets": [ 1 ]
				}
	        ],
	        "language": {
				"sEmptyTable":     "Nessun allegato trovato",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ allegati",
				"sInfoEmpty":      "Nessun allegato trovato",
				"sInfoFiltered":   "(su _MAX_ allegati totali)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_ allegati",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca allegati",
				"sZeroRecords":    "Nessun allegato trovato",
				"oPaginate": {
					"sFirst":      "Prima",
					"sPrevious":   "Precedente",
					"sNext":       "Successiva",
					"sLast":       "Ultima"
				}
			},
			"lengthMenu": [[10, 25, 50, 100, 200], ["10", "25", "50", "100", "200"]],
	        "pagingType": "full_numbers",
	        "bLengthChange" : false,
	        "order": [[ 0, "desc" ]],
	        "aoColumns": [
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true }
			   ]
	    });

		if (numeroTotale == 0) {
			$("#allegati tfoot").hide();
			$("#allegati_info").hide();
			$("#allegati_paginate").hide();
		}
	}
	
	
	/*
	 * Rimuove i dati (dati generali, destinatari ed allegati)
	 * della comunicazione e nasconde tabs
	 */
	function _removeComunicazione() {
		$("#tabs").fadeOut(tempo);
		
		$("#index").val("");
		$("#idprg").val("");
		$("#idcom").val("");
		$('#protocollacomunicazionemessaggio').text("");
		$('#protocollacomunicazionemessaggio').hide();
		$("#oggetto").val("");
		$("#descrizione").val("");
		$('#classifica').val("").attr("selected", "selected");
		$('#tipodocumento').val("").attr("selected", "selected");
		$('#mittenteinterno').val("").attr("selected", "selected");
		$("#numerodestinatari").val("0");
		$("#numeroallegati").val("0");
		
		if (_tableDestinatari != null) {
			_tableDestinatari.destroy(true);
		}
		
		if (_tableAllegati != null) {
			_tableAllegati.destroy(true);
		}
	}
	

	/*
	 * Protocolla comunicazione
	 */
	function _protocollaComunicazione() {
		
		_wait();
		
		var username = $("#username").val();
		var password = $("#password").val();
		var ruolo = $("#ruolo option:selected").val();
		var nome = $("#nome").val();
		var cognome = $("#cognome").val();
		var codiceuo = $("#codiceuo option:selected").val();
		var idutente = $("#idutente").val();
		var idutenteuo = $("#idutenteuo").val();
		
		var classifica = $("classifica").val();
		var tipodocumento = $("tipodocumento").val();
		var oggetto = $("oggetto").val();
		var descrizione = $("descrizione").val();
		var mittenteinterno = $("mittenteinterno").val();
		var inout = $("inout").val();
		
    	var idprg = $("#idprg").val();
    	var idcom = $("#idcom").val();
    	
    	var index = $("#index").val();
    	
    	$.ajax({
    		type: "GET",
    		async: true,
    		dataType: "json",
    		url: "pl/ComunicazioneInsProtocollo.do",
    		data : {
					username: $("#username").val(),
					password: $("#password").val(),
					ruolo: $("#ruolo option:selected").val(),
					nome : $("#nome").val(),
					cognome : $("#cognome").val(),
					codiceuo : $("#codiceuo option:selected").val(),
					idutente = $("#idutente").val(),
					idutenteuo = $("#idutenteuo").val(),
					classifica : $("#classifica").val(),
					tipodocumento : $("#tipodocumento").val(),
					oggetto : $("#oggetto").val(),
					descrizione : $("#descrizione").val(),
					mittenteinterno : $("#mittenteinterno").val(),
					inout : $("#inout").val(),
					idprg : $("#idprg").val(),
					idcom : $("#idcom").val(),
					idconfi : $("#idconfi").val()
			},
    		success: function(res) {
    			if (res) {
					if (res[0] == true) {
						$("body").off("click", "#comunicazioni tbody tr");
						_removeComunicazione();
						_getListaComunicazioni();
						_nowait();
						
						$(".ui-dialog-titlebar").hide();
						var messaggio = "La comunicazione e' stata protocollata con il numero " + res[3] + " del " + res[2];
						$("#dialog-message-info").text(messaggio);
				    	$("#dialog-message").dialog("open");
					} else {
						var messaggio = "Il tentativo di invio al protocollo segnala il seguente errore: " + res[1];
						$('#protocollacomunicazionemessaggio').text(messaggio);
						$('#protocollacomunicazionemessaggio').show(tempo);	
						_nowait();
					}
    			}
    		},
    		error: function(e) {
    			var messaggio = "Errore durante l'invio dei dati al protocollo";
				$('#protocollacomunicazionemessaggio').text(messaggio);
				$('#protocollacomunicazionemessaggio').show(tempo);
				_nowait();
    		}
    	});
	}
	
	
	/*
	 * Invio della comunicazione selezionata al sistema di 
	 * protocollazione
	 */
	$('#wsdmprotocollapulsante').click(function() {
		$('#protocollacomunicazionemessaggio').text("");
		$('#protocollacomunicazionemessaggio').hide();
		if ($("#formwslogin").validate().form() && $("#formwsdmdocumento").validate().form()) {
			_protocollaComunicazione();
		}
    });

	
});


