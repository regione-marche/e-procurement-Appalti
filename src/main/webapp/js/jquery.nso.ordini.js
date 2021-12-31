
/*
 * Gestione delle lavorazioni per NSO Ordini
 */

 var _tableLavNso = null;
 var _messaggio_ctr = "LA QUERY NON HA PRODOTTO RISULTATO";
 var _multiSelezione = true;
 
$(window).on("load", function (){
	
		
	var genereGara = $("#genere").val();
	
	var _searchRow=$('<tr/>');
	var _bRow=$('<br/>')
	
	var garabustalottiUnica = false;
	if($("#bustalotti").val()=='2'){
		garabustalottiUnica = true;	
	}
	
	$('#rdamessaggio').text("");
	$('#rdamessaggio').hide();
		
	$(".contenitore-dettaglio").width("980");

	_creaTabellaLavNso();
	_popolaLavNso(0);
		
	$.fn.dataTable.ext.order['dom-checkbox'] = function (settings, col)
	{
		return this.api().column(col, {order:'index'}).nodes().map(function (td, i) {
			return $('input',td).prop('checked') ? '1' : '0';
		} );
	};
	
	
	$("body").delegate('[id^="ck_rda_"]', "click", function() {
		checkedState = $(this).attr('checked');
		$(this).prop('checked', checkedState);
	});
	
	$("body").delegate('[id="incl_cons"]', "click", function() {
		checkedState = $(this).attr('checked');
		$("#listaLavNso_wrapper").remove();
		$("#incl_cons_row").remove();
		_creaTabellaLavNso();
		if(checkedState =='checked'){
		_popolaLavNso(1);
		var _trchk = $('<tr id="incl_cons_row" />', {"class": "intestazione"});		
		_trchk.append('<td ><input type="checkbox" id="incl_cons" name="incl_cons" checked= "checked" value="1" /><b><i>Includi lavorazioni consumate</i></b></td>');
		$("#lavNsoContainer").append(_trchk);
		
		}else{
		_popolaLavNso(0);
		var _trchk = $('<tr id="incl_cons_row" />', {"class": "intestazione"});		
		_trchk.append('<td ><input type="checkbox" id="incl_cons" name="incl_cons" value="0" /><b><i>Includi lavorazioni consumate</i></b></td>');
		$("#lavNsoContainer").append(_trchk);
		
		}

	});

	
	
	$("body").delegate('#listaLavNso tr td:nth-child(2)', "mouseover",
		function(event) {
			
			var par = $(this).parent();
			
			var html = par.children("td:nth-child(6)").children("input").attr("value");
			
			if (html != "" && html != null) {
				var offsetY = this.offsetTop;
				var _position = $(this).position();
				var top = _position.top;
				var left = _position.left;
				var _div = $("<div/>", {"id": "div_descrizione",  "class": "tooltip ui-corner-all", "html": html});
				$(this).append(_div);
				var height = _div.height();
				_div.css('left', left);
				_div.css('top', top-height-offsetY);
				_div.show(400);
			}

		}
	);
	
	$("body").delegate('#listaLavNso tr', "mouseout",
		function() {
			$("#div_descrizione").remove();
		}
	);

	
	$('#menuavanti, #pulsavanti').click(function() {
		var nSelected = 0;
		var arrmultikey  = '';
		var oTable = $('#listaLavNso').dataTable();
		$( "input[id^='ck_rda_']", oTable.fnGetNodes() ).each( function( index ) {
			if($( this ).attr( "checked")){
				nSelected= nSelected + 1;
				_ck_id = $(this).attr("id");
				if (_ck_id.substring(0,7) == 'ck_rda_'){
					_ck_key = _ck_id.substring(7);
					
						var arrkey = _ck_key.split('_');
						var tipoAppalto = '2';
						var proceduraTelematica = $("#proceduraTelematica").val();
						var modalitaPresentazione = $("#modalitaPresentazione").val();
					
					//in ogni caso
					arrmultikey  = arrmultikey + _ck_key + ';';
					}
				}
		});
		
		if(!(nSelected > 0)){
			alert("Selezionare almeno una lavorazione");	
		}else{
			var ngara = $("#numeroGara").val();
			document.forms[0].activePage.value = "0";
			document.forms[0].entita.value = "NSO_ORDINI";
			document.forms[0].jspPath.value="/WEB-INF/pages/gare/nso_ordini/nso_ordini-scheda.jsp";
			document.forms[0].jspPathTo.value="/WEB-INF/pages/gare/nso_ordini/nso_ordini-scheda.jsp";
			document.forms[0].action = document.forms[0].action + "&ngara="+ngara+"&arrmultikey="+arrmultikey + "&" + csrfToken;
			bloccaRichiesteServer();
			document.forms[0].submit();
		}
	
    });
	

	$('#menuaggiungi, #pulsaggiungi').click(function() {
		var nSelected = 0;
		var arrmultikey  = '';
		var oTable = $('#listaLavNso').dataTable();
		$( "input[id^='ck_rda_']", oTable.fnGetNodes() ).each( function( index ) {
			if($( this ).attr( "checked")){
				nSelected= nSelected + 1;
				_ck_id = $(this).attr("id");
				if (_ck_id.substring(0,7) == 'ck_rda_'){
					_ck_key = _ck_id.substring(7);
					
						var arrkey = _ck_key.split('_');
						var tipoAppalto = '2';
						var proceduraTelematica = $("#proceduraTelematica").val();
						var modalitaPresentazione = $("#modalitaPresentazione").val();
					
					//in ogni caso
					arrmultikey  = arrmultikey + _ck_key + ';';
					}
				}
		});
		
		if(!(nSelected > 0)){
			alert("Selezionare almeno una lavorazione");	
		}else{
			_waitgperm();
	        $.ajax({
				type: "GET",
				dataType: "json",
				url: "pg/SetNsoLavorazioniOrdine.do",
	    		data : {
	    			idOrdine: $("#idOrdine").val(),
	    			numeroGara: $("#numeroGara").val(),
	    			codiceDitta: $("#codiceDitta").val(),
	    			uffint: $("#uffint").val(),
	    			arrmultikey: arrmultikey
	    		},
				success: function() {
					historyVaiIndietroDi(1);
	            },

				error: function(e){
					alert("Errore nella associazione delle RdA in gara");
				},
				complete: function() {
	            	_nowaitgperm();
	            }
			});
		}
	
    });
	
	
	$('#menuindietro, #pulsindietro').click(function() {
		historyVaiIndietroDi(1);	
	});
	
	$('#menuannulla, #pulsannulla').click(function() {
		historyVaiIndietroDi(1);	
	});

		var _trchk = $('<tr id="incl_cons_row" />', {"class": "intestazione"});		
		_trchk.append('<td ><input type="checkbox" id="incl_cons" name="incl_cons" value="0" /><b><i>Includi lavorazioni consumate</i></b></td>');
		$("#lavNsoContainer").append(_trchk);


    
});



	
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

	
	/**
	 * Crea il contenitore del "datatable" per la lettura delle rda
	 */
	
	
	function _creaTabellaLavNso() {
		
		var _table = $('<table/>', {"id": "listaLavNso", "class": "schedagperm", "cellspacing": "0", "width" : "100%"});
		
		var _tr = $('<tr/>', {"class": "intestazione"});
		_tr.append('<th/>', {"class": "codice"});
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>');
		var _thck = $('<th/>', {"id": "selLavNso","class": "ck"}); //
		_tr.append(_thck);
		
		var _thead = $('<thead/>');
		_thead.append(_tr);

		var _tbody = $('<tbody/>');
		
		var _tr2 = $('<tr/>', {"class": "intestazione"});
		_tr2.append('<td/>', {"class": "codice"});
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		var _tfoot = $('<tfoot/>');
		_tfoot.append(_tr2);

		_table.append(_thead);
		_table.append(_tbody);
		_table.append(_tfoot);
		
		$("#lavNsoContainer").append(_table);
		
	}



	
	  function _selezionaTutti() {
	   $("input[id^='ck_rda_']").attr("checked","checked");
	  }
	
	  function _deselezionaTutti() {
		$( "input[id^='ck_rda_']" ).each( function( index ) {
				if ($( this ).attr( "disabled")) {
					;
				}else{
					$( this ).attr( "checked",false);
				}
		});
	  }
	  

  
		function _popolaLavNso(inclCons) {
			var descriptionMap = [];
			var _visStatoSel = true;
			var visIdLotto = false;
			var visOggetto = false;
			var visDescrizione = true;
			var visQta = false;
			var visUm = false;
			var visValStimato = false;
			var garabustalottiUnica = false;
			var _tipoWSERP = '';
			
			if($("#bustalotti").val()=='2'){
				garabustalottiUnica = true;	
			}
			
				var visDescrizione = false;
				var rangesel = [[5, 50, 100, -1], ["5 Lavorazioni", "50 Lavorazioni", "100 Lavorazioni", "Tutte le lavorazioni"]];
				
				_tableLavNso = $('#listaLavNso').removeAttr('width').DataTable( {
					"ajax": {
						"url": "pg/GetNsoListaLavorazioni.do",
						"data" : function (n) { 
							return {
								operation: $("#operation").val(),
								codiceGara: $("#codiceGara").val(),
								numeroGara: $("#numeroGara").val(),
								codiceDitta: $("#codiceDitta").val(),
								genere: $("#genere").val(),
								tipoAppalto: $("#tipoAppalto").val(),
								uffint: $("#uffint").val(),
								incl_cons: inclCons
							};	
						},
						
						error: function(e){
							var messaggio = "Errore durante la lettura delle Lavorazioni";
							$('#rdamessaggio').text(messaggio);
							$('#rdamessaggio').show(300);
						},
						
						"complete": function() {
						_nowaitgperm();
			            }
					},
					
					"bAutoWidth": false,
					
					"columnDefs": [
						{	
							"data": "voce",
							"visible": true,
							"sTitle": "Voce",
							"searchable":true,
							"targets": [ 0 ]
						},

						{	
							"data": "descrizione",
							"visible": true,
							"sTitle": "Descrizione",
							"searchable": true,
							"targets": [ 1 ]
						},
						{	
							"data": "um",
							"visible": true,
							"sTitle": "Unità di misura",
							"searchable": false,
							"targets": [ 2 ]
						},
						{	
							"data": "quantita",
							"visible": true,
							"sTitle": "Quantita",
							"searchable": false,
							"sWidth": "50px",
							"sClass": "desccenter",
							"targets": [ 3 ]
						},
						{	
							"data": "prezzoUnitario",
							"visible": true,
							"sTitle": "Prezzo unitario",
							"searchable": false,
							"sWidth": "50px",
							"sClass": "desccenter",
							"targets": [ 4 ]
						},
						{	
							"data": "contaf",
							"visible": false,
							"sTitle": "contaf",
							"searchable": false,
							"sWidth": "50px",
							"sClass": "desccenter",
							"targets": [ 5 ]
						},
						{	
							"data": "checkRda",
							"visible": true,
							"targets": [ 6 ],
							"class" : "ck",
							"sWidth" : "70px",
							"render": function (data, type, full, meta ) {
								var _div = $("<div/>");
									if(full.voce!=null){
										var _check = $("<input/>",{"type":"checkbox", "id": "ck_rda_" + full.contaf,"value":full.descrizioneEstesa});
										_div.append(_check);
								  }
								
								
								return _div.html();
							}
						}

					],
					
					 "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
						switch(aData['quantita']){
							case 0:
								$('td', nRow).css('color', 'red')
								break;
						}
					},					
					
			        "language": {
						"sEmptyTable":     "Non ci sono Lavorazioni disponibili",
						"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ Lavorazioni",
						"sInfoEmpty":      "Non ci sono Lavorazioni disponibili",
						"sInfoFiltered":   "(su _MAX_ Lavorazioni totali)",
						"sInfoPostFix":    "",
						"sInfoThousands":  ",",
						"sLengthMenu":     "Visualizza _MENU_",
						"sLoadingRecords": "",
						"sProcessing":     "Elaborazione...",
						"sSearch":         "Cerca Lavorazioni",
						"sZeroRecords":    "Non ci sono Lavorazioni disponibili",
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
							$("#listaLavNso tfoot").hide();
							$("#listaLavNso_info").hide();
							$("#listaLavNso_paginate").hide();
						}
					},
					
			        "pagingType": "full_numbers",
			        "lengthMenu": rangesel,
			        "ordering": false,
					"aoColumns": [
								     { "bSortable": true, "bSearchable": true },
								     { "bSortable": true, "bSearchable": true },
								     { "bSortable": false, "bSearchable": true },
								     { "bSortable": false, "bSearchable": true },
								     { "bSortable": false, "bSearchable": true },
								   ]
			    });
					
			 
   				$('#listaLavNso tfoot td').eq(0).html( '<input class="search" size="20" type="text" placeholder="Ricerca voce"/>' );
				$('#listaLavNso tfoot td').eq(1).html( '<input class="search" size="20" type="text" placeholder="Ricerca descrizione"/>' );
				
				_tableLavNso.columns().eq(0).each( function (colIdx) {
						$('input', _tableLavNso.column(colIdx).footer()).on( 'keyup change', function () {
							_tableLavNso.column(colIdx).search(this.value).draw();
						});
				});
				
				
					var _center = $("<center/>");
					var _href = "<a href='javascript:_selezionaTutti();' Title='Seleziona tutti'> <img src='"+_contextPath+"/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>";
					var _href = _href + "&nbsp;";
					var _href = _href + "<a href='javascript:_deselezionaTutti();' Title='Deseleziona tutti'><img src='"+_contextPath+"/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>";
					_center.append(_href);
					_center.appendTo($("#selLavNso"));
							
				$("#listaLavNso_filter").hide();
				
				
				
				
				
			
		}
  

  
