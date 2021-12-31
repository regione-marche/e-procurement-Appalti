
/*
 * Gestione dei permessi
 */

$(window).on("load", function (){
	
	var _tableUtenti = null;
	_popolaUtenti();

	$.fn.dataTable.ext.order['dom-checkbox'] = function (settings, col)
	{
		return this.api().column(col, {order:'index'}).nodes().map(function (td, i) {
			return $('input',td).prop('checked') ? '1' : '0';
		} );
	};
	
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
	     }, 500);
	}
	
	function _creaTabellaUtenti() {
		
		var _table = $('<table/>', {"id": "utenti", "class": "schedagperm", "cellspacing": "0", "width" : "100%"});
		
		var _tr = $('<tr/>', {"class": "intestazione"});
		_tr.append('<th/>', {"class": "codice"});
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>', {"class": "ck"});
		_tr.append('<th/>', {"class": "ck"});
		_tr.append('<th/>', {"class": "ck"});
		_tr.append('<th/>');
		var _thead = $('<thead/>');
		_thead.append(_tr);

		var _tbody = $('<tbody/>');
		
		var _tr2 = $('<tr/>', {"class": "intestazione"});
		_tr2.append('<td/>', {"class": "codice"});
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>', {"class": "ck"});
		_tr2.append('<td/>', {"class": "ck"});
		_tr2.append('<td/>', {"class": "ck"});
		_tr2.append('<td/>');
		var _tfoot = $('<tfoot/>');
		_tfoot.append(_tr2);

		_table.append(_thead);
		_table.append(_tbody);
		_table.append(_tfoot);
		
		$("#utentiContainer").append(_table);	
	}

	/*
	 * Popola la tabella con la lista degli utenti
	 */
	function _popolaUtenti() {
		_waitgperm();
		_creaTabellaUtenti();
		_tableUtenti = $('#utenti').DataTable( {
			"ajax": {
				"url": "GetListaPermessiUtentiStandard.do",
				"data" : function (n) { 
					return {
						operation: $("#operation").val(),
						codgar: $("#codgar").val(),
						codein: $("#codein").val()
					};	
				},
				"complete": function() {
	            	_nowaitgperm();
	            }
			},
			"columnDefs": [
				{	
					"data": "SYSCON.value",
					"visible": false,
					"searchable": false,
					"targets": [ 0 ]
				},
				{	
					"data": "NUMPER.value",
					"visible": false,
					"searchable": false,
					"targets": [ 1 ]
				},
				{
					"data": "SYSLOGIN.value",
					"visible": true,
					"sTitle": "Utente",
					"targets": [ 2 ],
					"sWidth": "275px",
					"class" : "matricola"
				},
				{
					"data": "SYSUTE.value",
					"visible": true,
					"sTitle": "Descrizione",
					"targets": [ 3 ],
					"sWidth": "275px",
					"class" : "descr"
				},				
				{
					"data": "AUTORI.value",
					"visible": true,
					"targets": [ 4 ],
					"sTitle": "Lettura",
					"sWidth" : "150px",
					"class" : "ck",
					"orderDataType": "dom-checkbox",
					"render": function ( data, type, full, meta ) {
						var _div = $("<div/>");
						var _check = $("<input/>",{"type":"checkbox", "id": "usr_ck_r_" + full.SYSCON.value});
						if (data == 2 || data == 1) _check.attr("checked","checked");
						if (($("#operation").val() == "VISUALIZZA" || full.AUTORI.value == 1 || full.PROPRI.value == '1')
								||
							($("#operation").val() == "MODIFICA" && full.SYSDISAB.value == 1 && data != 2 && data != 1)) {
							_check.attr("disabled","disabled");
						}
						_div.append(_check);					
						return _div.html();
					}
				},
				{
					"data": "AUTORI.value",
					"visible": true,
					"targets": [ 5 ],
					"sTitle": "Scrittura",
					"sWidth" : "150px",
					"class" : "ck",
					"orderDataType": "dom-checkbox",
					"render": function ( data, type, full, meta ) {
						var _div = $("<div/>");
						var _check = $("<input/>",{"type":"checkbox", "id": "usr_ck_w_" + full.SYSCON.value});
						if (data == 1) _check.attr("checked","checked");
						if ($("#operation").val() == "VISUALIZZA" || full.PROPRI.value == '1') {
							_check.attr("disabled","disabled");
						}
						_div.append(_check);					
						return _div.html();
					}
				},
				{
					"data": "PROPRI.value",
					"visible": true,
					"targets": [ 6 ],
					"sTitle": "Controllo<br>completo",
					"sWidth" : "150px",
					"class" : "ck",
					"orderDataType": "dom-checkbox",
					"render": function ( data, type, full, meta ) {
						var _div = $("<div/>");
						var _check = $("<input/>",{"type":"checkbox", "id": "usr_ck_x_" + full.SYSCON.value});
						if (data == '1') _check.attr("checked","checked");
						if ($("#operation").val() == "VISUALIZZA") {
							_check.attr("disabled","disabled");
						}
						_div.append(_check);
						var _check_u = $("<input/>",{"type":"checkbox", "id": "usr_ck_u_" + full.SYSCON.value});
						_check_u.css("display","none");
						_div.append(_check_u);	
						return _div.html();
					}
				},
				{
					"data": "SYSDISAB.value",
					"visible": false,
					"targets": [ 7 ],
					"sTitle": "sysdisab",
					"sWidth" : "25px",
					"class" : "descr",
					"render": function ( data, type, full, meta ) {
						var _div = $("<div/>");
						var _check = $("<input/>",{"type":"hidden", "id": "usrsysdisab_" + full.SYSCON.value,"value": full.SYSDISAB.value, "length": 15});
						_div.append(_check);					
						return _div.html();
					}
				}
	        ],
	        "language": {
				"sEmptyTable":     "",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ utenti",
				"sInfoEmpty":      "",
				"sInfoFiltered":   "(su _MAX_ utenti totali)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca utenti",
				"sZeroRecords":    "",
				"oPaginate": {
					"sFirst":      "<<",
					"sPrevious":   "<",
					"sNext":       ">",
					"sLast":       ">>"
				}
			},
			"initComplete": function (oSettings, jso) {
	 			var _iTotalRecords = oSettings.fnRecordsTotal();
				if (_iTotalRecords == 0) {
					$("#utenti tfoot").hide();
					$("#utenti_info").hide();
					$("#utenti_paginate").hide();
					$("#impostaCondivisionePredef").hide();
				}
			},
	        "pagingType": "full_numbers",
	        "lengthMenu": [[10, 20, 50], ["10 utenti", "20 utenti", "50 utenti"]],
	        "order": [[ 3, "asc" ]],
	        "aoColumns": [
			     { "bSortable": false, "bSearchable": false },
			     { "bSortable": false, "bSearchable": false },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": false },
			     { "bSortable": true, "bSearchable": false },
			     { "bSortable": true, "bSearchable": false },
			     { "bSortable": false, "bSearchable": false }
			   ]
	    });
	
		if ($("#operation").val() == 'MODIFICA') {
			$('#utenti tfoot td').eq(0).html( '<input class="search" size="10" type="text" placeholder="Ricerca utente"/>' );
			$('#utenti tfoot td').eq(1).html( '<input class="search" size="18" type="text" placeholder="Ricerca descrizione"/>' );

			_tableUtenti.columns().eq(0).each( function (colIdx) {
				$('input', _tableUtenti.column(colIdx).footer()).on( 'keyup change', function () {
					_tableUtenti.column(colIdx).search(this.value).draw();
				});
		    });
		} else {
			$("#utenti tfoot").hide();
		}
		
		$('#utenti thead th').eq(2).attr("title","Lettura");
		$('#utenti thead th').eq(3).attr("title","Scrittura");
		$('#utenti thead th').eq(4).attr("title","Controllo completo, l'utente puo' assegnare i diritti ad altri utenti");
		$("#utenti_filter").hide();
	}
	
	$("body").on('click', '[id^="usr_ck_x_"]', function() {
			var _id = $(this).attr("id");
			_syscon = _id.substring(9);
			if ($(this).is(':checked')) {
				$("#usr_ck_w_" + _syscon).attr("checked","checked").attr("disabled","disabled");
				$("#usr_ck_r_" + _syscon).attr("checked","checked").attr("disabled","disabled");
			} else {
				$("#usr_ck_w_" + _syscon).removeAttr("disabled");
			}
			$("#usr_ck_u_" + _syscon).attr("checked","checked");
	});

	$("body").on('click','[id^="usr_ck_w_"]', function() {
		var _id = $(this).attr("id");
		_syscon = _id.substring(9);
		if ($(this).is(':checked')) {
			$("#usr_ck_r_" + _syscon).attr("checked","checked").attr("disabled","disabled");
		} else {
			$("#usr_ck_r_" + _syscon).removeAttr("disabled");
		}
		$("#usr_ck_u_" + _syscon).attr("checked","checked");
	});

	$("body").on('click','[id^="usr_ck_r_"]', function() {
		var _id = $(this).attr("id");
		_syscon = _id.substring(9);
		$("#usr_ck_u_" + _syscon).attr("checked","checked");
	});
	
	$('#menumodificapermessi, #pulsantemodificapermessi').click(function() {
		formModificaPermessiUtenti.operation.value="MODIFICA";
		formModificaPermessiUtenti.submit();
    }); 
	
	$('#menusalvamodifichepermessi, #pulsantesalvamodifichepermessi').click(function() {
		_salvaPermessiUSR();
		formVisualizzaPermessiUtenti.operation.value="VISUALIZZA";
		formVisualizzaPermessiUtenti.submit();
    });
	
	$('#menuannullamodifichepermessi, #pulsanteannullamodifichepermessi').click(function() {
		formVisualizzaPermessiUtenti.operation.value="VISUALIZZA";
		formVisualizzaPermessiUtenti.submit();
    });
	
	function _salvaPermessiUSR() {
		// Per salvare le modifiche di tutte le pagine della lista paginata
		// si forza la visualizzazione di tutti i record della lista e poi
		// si avvia l'elaborazione di salvataggio dei dati.
		_tableUtenti.page.len(-1).draw();
		
		_tableUtenti.$("tr").each(function () {
			var _ick = $(this).find('input:checked');
			if (_ick.size() > 0) {

				var _syscon = 0;
				var _r = 0;
				var _w = 0;
				var _x = 0;
				var _u = 0;
				
				var _autori = 0;
				var _propri = '';
				
				_ick.each(function () {
					var _id = $(this).attr("id");
					_syscon = _id.substring(9);
					if (_id.substring(0,9) == 'usr_ck_r_') _r = 1;
					if (_id.substring(0,9) == 'usr_ck_w_') _w = 1;
					if (_id.substring(0,9) == 'usr_ck_x_') _x = 1;
					if (_id.substring(0,9) == 'usr_ck_u_') _u = 1;
				});
				
				if (_u == 1) {
					if (_r == 0 && _w == 0 && _x == 0) {
						$.ajax({
							  "async": false,
							  "url": "SetPermessiUtentiStandard.do?operation=DELETE&syscon=" + _syscon + "&codgar=" + $("#codgar").val()
						});
					}
					
					if (_r == 1 || _w == 1 || _x == 1) {
						if (_x == 1) {
							_propri = '1';
							_autori = 1;
						} else {
							_propri = '2';
							if (_w == 1) {
								_autori = 1;
							} else {
								_autori = 2;
							}
						}
						
						$.ajax({
							  "async": false,
							  "url": "SetPermessiUtentiStandard.do?operation=INSERTUPDATE&syscon=" + _syscon + "&codgar=" + $("#codgar").val() + "&autori=" + _autori + "&propri=" + _propri
						});
					}
				}
			}
		});
    };	
    
});
