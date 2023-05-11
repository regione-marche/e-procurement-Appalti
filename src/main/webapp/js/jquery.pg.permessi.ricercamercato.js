/*
 * Gestione dei permessi
 */

$(window).on("load", function () {
	
	var _tableUtenti = null;
	var _tabellatoRuolo = null;
	var _tabellatoA1137 = null;
	var _descTabA1137   = null;
	
	_getTabellatoRuolo();
	_getTabellatoA1137();
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
	
	/*
	 * Lettura tabellato Ruolo
	 */
	function _getTabellatoRuolo() {
		$.ajax({
			type: "POST",
			dataType: "json",
			async: false,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: "GetTabellatoJSON.do",
			data: "tab1cod=G_058",
			success: function(data) {
				if (data) {
					_tabellatoRuolo = data;
				}
			},
			error: function(e) {
				alert("Errore durante la lettura del tabellato ruolo");
			}
		});
	}
	
	/*
	 * Lettura tabellato A1137 (vincolo unicita' punto ordinante)
	 */
	function _getTabellatoA1137() {
		$.ajax({
			type: "POST",
			dataType: "json",
			async: false,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: "GetTabellatoJSON.do",
			data: "tab1cod=A1137",
			success: function(data) {
				if (data) {
					_tabellatoA1137 = data;
					_descTabA1137 = data[0].descTabellato.substring(0,1);
				}
			},
			error: function(e) {
				alert("Errore durante la lettura del tabellato A1137");
			}
		});
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
				"url": "GetListaPermessiUtentiRicercaMercato.do",
				"data" : function (n) { 
					return {
						operation: $("#operation").val(),
						id: $("#id").val(),
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
					"sWidth": "275px",
					"class" : "matricola",
					"targets": [ 2 ]
				},
				{
					"data": "SYSUTE.value",
					"visible": true,
					"sTitle": "Descrizione",
					"sWidth": "275px",
					"class" : "descr",
					"targets": [ 3 ]
				},				
				{
					"data": "AUTORI.value",
					"visible": true,
					"sTitle": "Lettura",
					"sWidth" : "150px",
					"class" : "ck",
					"targets": [ 4 ],
					"orderDataType": "dom-checkbox",
					"render": function ( data, type, full, meta ) {
						var _div = $("<div/>");
						var _check = $("<input/>",{"type":"checkbox", "id": "usr_ck_r_" + full.SYSCON.value});
						if (data == 2 || data == 1) _check.attr("checked","checked");
						if ($("#operation").val() == "VISUALIZZA" || full.AUTORI.value == 1 || full.PROPRI.value == '1') {
							_check.attr("disabled","disabled");
						}
						_div.append(_check);					
						return _div.html();
					}
				},
				{
					"data": "AUTORI.value",
					"visible": true,
					"sTitle": "Scrittura",
					"sWidth" : "150px",
					"class" : "ck",
					"targets": [ 5 ],
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
					"sTitle": "Controllo<br>completo",
					"sWidth" : "150px",
					"class" : "ck",
					"targets": [ 6 ],
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
					"data": "MERUOLOG.value", 
					"visible": true,
					"searchable": false,
					"sTitle": "Ruolo",
					"sWidth" : "120px",
					"targets": [ 7 ],
					"render": function ( data, type, full, meta ) {
						if ($("#operation").val() == "VISUALIZZA") {
							var _ret = "";
							if (full.AUTORI.value != null || full.PROPRI.value != null) {
								if (full.MERUOLOG.value != null) {
									$.map( _tabellatoRuolo, function( item ) {
										if (item.tipoTabellato == full.MERUOLOG.value) {
											_ret = item.descTabellato;
										}
									});
								} else {
									$.map( _tabellatoRuolo, function( item ) {
										if (item.tipoTabellato == 2) {
											_ret = item.descTabellato;
										}
									});
								}
							}
							return _ret;
						} else {
							var _div = $("<div/>");
							var _id = "tabellatoMeruolo_" + full.SYSCON.value;
							var _select = $("<select/>",{"id": _id});
							var _input1 = $("<input/>",{"type": "hidden", "id": "tabMeruoloG_" + full.SYSCON.value, "value": full.MERUOLOG.value, "name": "tabMeruoloG_" + full.SYSCON.value});
							var _input2 = $("<input/>",{"type": "hidden", "id": "tabMeruoloU_" + full.SYSCON.value, "value": full.MERUOLOU.value, "name": "tabMeruoloU_" + full.SYSCON.value});
							_div.append(_select);
							_div.append(_input1);
							_div.append(_input2);
							return _div.html();
						}
					}
				},
				{
					"data": "SYSDISAB.value",
					"visible": false,
					"targets": [ 8 ],
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
			     { "bSortable": false, "bSearchable": false },
			     { "bSortable": false, "bSearchable": false }
		   ]
	    });

		if ($("#operation").val() == 'MODIFICA') {
			$('#utenti tfoot td').eq(0).html( '<input id="search_2" class="search" size="10" type="text" placeholder="Ricerca utente"/>' );
			$('#utenti tfoot td').eq(1).html( '<input id="search_3" class="search" size="18" type="text" placeholder="Ricerca descrizione"/>' );

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
			$("#tabellatoMeruolo_" + _syscon).removeAttr("disabled");
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
			$("#tabellatoMeruolo_" + _syscon).removeAttr("disabled");
		} else {
			$("#usr_ck_r_" + _syscon).removeAttr("disabled");
			$("#tabellatoMeruolo_" + _syscon).val(2).attr("selected", "selected").attr("disabled","disabled");
		}
		$("#usr_ck_u_" + _syscon).attr("checked","checked");
	});

	$("body").on('click','[id^="usr_ck_r_"]', function() {
		var _id = $(this).attr("id");
		_syscon = _id.substring(9);
		$("#usr_ck_u_" + _syscon).attr("checked","checked");
	});
	
	$("body").on('change','[id^="tabellatoMeruolo_"]', function() {
		var _id = $(this).attr("id");
		_syscon = _id.substring(17);
		$("#usr_ck_u_" + _syscon).attr("checked","checked");
	});
	
	$("#utenti_paginate").on('click', function() {
		if ($("#operation").val() != "VISUALIZZA") {
			_creaOptions();
		}
	});
	
	$("#utenti_length").on('click', function() {
		if ($("#operation").val() != "VISUALIZZA") {
			_creaOptions();
		}
	});
	
	$('#utenti').on( 'order.dt', function () {
		if ($("#operation").val() != "VISUALIZZA") {
			_creaOptions();
		}
	});
	
	$('#menumodificapermessi, #pulsantemodificapermessi').click(function() {
		formModificaPermessiUtentiStandard.operation.value="MODIFICA";
		formModificaPermessiUtentiStandard.submit();
    }); 
	
	$('#menusalvamodifichepermessi, #pulsantesalvamodifichepermessi').click(function() {
		if(_salvaPermessiUSR()) {
			formVisualizzaPermessiUtentiStandard.operation.value="VISUALIZZA";
			formVisualizzaPermessiUtentiStandard.permessimodificabili.value = 'true';
			formVisualizzaPermessiUtentiStandard.submit();
		}
    });
	
	$('#menuannullamodifichepermessi, #pulsanteannullamodifichepermessi').click(function() {
		formVisualizzaPermessiUtentiStandard.operation.value="VISUALIZZA";
		formVisualizzaPermessiUtentiStandard.permessimodificabili.value = 'true';
		formVisualizzaPermessiUtentiStandard.submit();
    });
	
	function _salvaPermessiUSR() {
		// Per salvare le modifiche di tutte le pagine della lista paginata
		// si forza la visualizzazione di tutti i record della lista e poi
		// si avvia l'elaborazione di salvataggio dei dati.
		var _len  = _tableUtenti.page.len();
		var _page = _tableUtenti.page();
		
		//Accorgimento per evitare di perdere i valori nei campi quando si impostano filtri 
		_tableUtenti.columns().eq(0).each( function (colIdx) {
			_tableUtenti.column(colIdx).search('');
		});
		
		_tableUtenti.page.len(-1).draw();
		_tableUtenti.page(0).draw();
		var _strTot = "";
		var _strSearch = ["","","","",""];

		var continua = true;
		if (_descTabA1137 == '1') {
			// Controllo dell'unicita' del punto ordinante
			var conta = 0;
			$.map($("[id^='search_']"), function(item) {
				var _id = item.id;
				var num = _id.substring(7);
				var valore = item.value;
				if (valore != "") {
					_strSearch[num-2] = valore;
					_strTot = _strTot+valore;
				}
				//_tableUtenti.column(num).search("");
			});
			
			//_tableUtenti.draw();
			//alert("_strSearch=[" + _strSearch[0] + ","+_strSearch[1] + "," + _strSearch[2] + "," + _strSearch[3] + "," + _strSearch[4] + "], _strTot=" + _strTot);
			
			$.map($("[id^='tabellatoMeruolo_']"), function(item) {
				var _id = item.id;
				var _syscon = _id.substring(17);
				if ($("#usr_ck_r_" + _syscon).is(':checked')) {
					var rere = $("#tabellatoMeruolo_" + _syscon).val();
					if (rere == '1') {
						conta = conta + 1;
					}
				}
			});

			if (_strTot != "") {
				$.map($("[id^='search_']"), function(item) {
					var _id = item.id;
					var num = _id.substring(7);
					_tableUtenti.column(num).search(_strSearch[num-2]);
				});
			}
			//_tableUtenti.draw();
			
			if (conta > 1) {
				_tableUtenti.page.len(_len).draw();
				_tableUtenti.page(_page).draw(false);
				alert("Il ruolo di 'Punto ordinante' puo' essere assegnato a un solo utente");
				continua = false;
			}
		}
		
		if (continua) {
			_tableUtenti.$("tr").each(function () {
				var _ick = $(this).find('input:checked');
				if (_ick.size() > 0) {
					var _syscon = 0;
					var _r = 0;
					var _w = 0;
					var _x = 0;
					var _u = 0;
					var _p = 0;
					
					var _autori = 0;
					var _propri = '';
					var _ruolo  = '';
					
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
								  "url": "SetPermessiRicercaMercato.do?operation=DELETE&syscon=" + _syscon + "&id=" + $("#id").val()
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
							_ruolo = $("#tabellatoMeruolo_" + _syscon).val();
							$.ajax({
								"async": false,
								"url": "SetPermessiRicercaMercato.do?operation=INSERTUPDATE&syscon=" + _syscon + "&id=" + $("#id").val() + "&autori=" + _autori + "&propri=" + _propri + "&ruolo=" + _ruolo
							});
						}
					}
				}
			});
		}
		return continua;
    }
    	
	function _creaOptions() {
    	$.map($("[id^='tabellatoMeruolo_']"), function(item1) {
    		var _id = item1.id;
    		var _syscon = _id.substring(17);
    		if ($("#tabellatoMeruolo_" + _syscon + " option").length <= 0) {
    			$.map(_tabellatoRuolo, function( item ) {
					$("#" + _id).append($("<option/>", {id: "option_" + item.tipoTabellato + "_" + _syscon, value: item.tipoTabellato, text: item.descTabellato }));
    			});
	    		var meruoloG = $("#tabMeruoloG_" + _syscon).attr("value");
	    		var meruoloU = $("#tabMeruoloU_" + _syscon).attr("value");
	
	    		var meruolo = null;
	    		if (meruoloG != null && meruoloG != "") {
	    			meruolo = meruoloG;
	    		} else if (meruolo == null && meruoloU != null && meruoloU != "") {
	    			meruolo = meruoloU;
	    		} else {
	    			meruolo ="2";
	    			if($("#tabellatoMeruolo_" + _syscon).val() == 3)
						meruolo =  "3";
	    		}
	    		
	    		$("#tabellatoMeruolo_" + _syscon).attr("disabled","disabled");
	    		
	    		if ("1" != meruoloU){
					$("#tabellatoMeruolo_" + _syscon +" option[value=1]").remove();
				}
	    		
	    		if ($("#usr_ck_r_" + _syscon).is(':checked')) {
	    			// Per utenti con permessi sulla gara
	    			if (meruolo != null)
						$("#tabellatoMeruolo_" + _syscon).val(meruolo).attr("selected", "selected");
	    			
	    		} else {
	    			// Per utenti privi di permessi sulla gara
	    			if (meruolo != null) {
	    				$("#tabellatoMeruolo_" + _syscon).val(meruolo).attr("selected", "selected");
	    			} else {
	    				$("#tabellatoMeruolo_" + _syscon).val("2").attr("selected", "selected");
	    			}
	    		}
	    		
	    		if ($("#usr_ck_w_" + _syscon).is(':checked')) {
	    			// Per utenti con permessi sulla gara
	    			if (meruolo != null){
						$("#tabellatoMeruolo_" + _syscon).val(meruolo).attr("selected", "selected");
	    				$("#tabellatoMeruolo_" + _syscon).removeAttr("disabled");
	    			}
	    		} else {
	    			// Per utenti privi di permessi sulla gara
	    			if (meruolo != null) {
	    				$("#tabellatoMeruolo_" + _syscon).val(meruolo).attr("selected", "selected");
	    			} else {
	    				$("#tabellatoMeruolo_" + _syscon).val("2").attr("selected", "selected");
	    			}
	    		}
    		}
    	});
	}

});
