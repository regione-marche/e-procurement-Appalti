
/*
 * Gestione Allegati dell'ERP
 */

$(window).on("load", function (){

	if($("#genere").val()=='1' || $("#genere").val()=='3'){
		var _lottoVisibile = true;
	}else{
		var _lottoVisibile = false;
	}
	
	
	var _tableAllegati = null;
	_getTipoWSERP();
	var _searchRow=$('<tr/>');
	var _bRow=$('<br/>')
	var visTitolo = false;
	var	visTipo = false;
	var nomeDoc = "Nome documento";

	if(_tipoWSERP == 'SMEUP' || _tipoWSERP == 'UGOVPA'){
		visTitolo = false;
		visTipo = false;
		if($("#genere").val()=='1' || $("#genere").val()=='3'){
			nomeDoc ="Documenti raggruppati per carrello e RdA";
		}else{
			nomeDoc ="Documenti raggruppati per RdA";
		}
		nomeDoc = "Nome documento";
	}
	if(_tipoWSERP == 'AVM'){
		visTitolo = true;
	}
		
	$(".contenitore-dettaglio").width("980");
	
	_popolaAllegati();
	_wait();

		
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
	
	function _creaTabellaAllegati() {
		
		var _table = $('<table/>', {"id": "listaallegatirda", "class": "schedagperm", "cellspacing": "0", "width" : "100%"});
		
		var _tr = $('<tr/>', {"class": "intestazione"});
		_tr.append('<th/>', {"class": "codice"});
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>');
		_tr.append('<th/>');
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
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		var _tfoot = $('<tfoot/>');
		_tfoot.append(_tr2);

		_table.append(_thead);
		_table.append(_tbody);
		_table.append(_tfoot);
		
		$("#allegatiContainer").append(_table);	
		
	}

	/*
	 * Popola la tabella con la lista degli allegati
	 */
	function _popolaAllegati() {
		_waitgperm();
		_creaTabellaAllegati();
		
		_tableRda = $('#listaallegatirda').removeAttr('width').DataTable( {
			"ajax": {
				"url": "pg/GetWSERPListaAllegatiRda.do",
				"data" : function (n) { 
					return {
						operation: $("#operation").val(),
						codice: $("#codice").val(),
						genere: $("#genere").val()
					};	
				},
				"complete": function() {
	            	_nowaitgperm();
	            }
			},
			
			"bAutoWidth": false,
			
			"columnDefs": [
				{	
					"data": "codiceCarrello",
					"visible": false,
					"sTitle": "Codice Carrello",
					"searchable": false,
					"targets": [ 0 ]
				},
				{	
					"data": "codiceRda",
					"visible": false,
					"sTitle": "Codice RdA",
					"searchable": false,
					"targets": [ 1 ]
				},
				{	
					"data": "titolo",
					"visible": visTitolo,
					"sTitle": "Titolo",
					"sWidth": "100px",
					"class" : "descr",
					"searchable": false,
					"targets": [ 2 ]
				},
				{	
					"data": "nome",
					"visible": true,
					"sTitle": nomeDoc,
					"sWidth": "100px",
					"searchable": false,
					"targets": [ 3 ],
					"render": function ( data, type, full, meta ) {
						var _span = $("<span/>");
						if(_tipoWSERP == 'SMEUP'){
							var _a = $("<a/>",{id: "nomedoc_" + full.path ,href: "#", "text": full.nome, "class": "link-generico"});
						}
						if(_tipoWSERP == 'UGOVPA'){
							var _a = $("<a/>",{id: "nomedoc_" + full.codiceRda + "_" + full.path ,href: "#", "text": full.nome, "class": "link-generico"});
						}
						if(_tipoWSERP == 'AVM'){
							var _a = $("<a/>",{id: "nomedoc_" + full.codiceRda ,href: "#", "text": full.nome, "class": "link-generico"});
						}
						_span.append(_a);
						return _span.html();
					}
				},
				{	
					"data": "descrRda",
					"visible": true,
					"sTitle": "Descrizione",
					"sWidth": "100px",
					"searchable": false,
					"targets": [ 4 ]
				},
				{	
					"data": "tipo",
					"visible": visTipo,
					"sTitle": "Tipo",
					"sWidth": "50px",
					"sClass": "aligncenter",
					"searchable": false,
					"targets": [ 5 ],
				},
				{	
					"data": "path",
					"visible": false,
					"class" : "descr",
					"searchable": false,
					"targets": [ 6 ]
				}

			],

			//group corrisponde a idLotto per SmeUp
			"drawCallback": function ( settings ) {
				
					var api = this.api();
					var rows = api.rows( {page:'current'} ).nodes();
					var lastc=null;
					var genere= $("#genere").val();
		 
					if((_tipoWSERP == 'SMEUP' || _tipoWSERP == 'UGOVPA') && (genere==1 || genere==3)){
						api.column(0, {page:'current'} ).data().each( function ( groupc, i ) {
							if ( lastc !== groupc ) {
								$(rows).eq( i ).before(
										'<tr class="group" style="background: rgb(192, 192, 192);"><td colspan="4">'+groupc+'</td></tr>'
								);
			 
								lastc = groupc;
							}
						} );
					}
					
				
			},
			
				
	        "language": {
				"sEmptyTable":     "Non ci sono allegati",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ allegati",
				"sInfoEmpty":      "Non ci sono allegati",
				"sInfoFiltered":   "(su _MAX_ allegati totali)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca allegati",
				"sZeroRecords":    "Non ci sono allegati",
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
					$("#listaallegatirda tfoot").hide();
					$("#listaallegatirda_info").hide();
					$("#listaallegatirda_paginate").hide();
				}
			},
			
	        "pagingType": "full_numbers",
	        "lengthMenu": [[20, 50, 70, 100], ["20 allegati", "50 allegati", "70 allegati", "100 allegati"]],
	        "ordering": false,
	    });
	

		
		$("#listaallegatirda_filter").hide();
		
		$("body").delegate('[id^="nomedoc_"]', "click", function() {
			var _id = 0;
			_id = $(this).attr("id");
			_text = $(this).prop("text");
			if (_id.substring(0,8) == 'nomedoc_'){
				_id = _id.substring(8);
			}

			visualizzaFileAllegato(_id,_text);
		});



	
		
	}
	
    
});


  
  

  function visualizzaFileAllegato(id,nomedoc) {
		var contextPath = $("#contextPath").val();
			var vet = nomedoc.split(".");
			var ext = vet[vet.length-1];
			ext = ext.toUpperCase();
			var href = contextPath + "/pg/VisWSERPAllegato.do";
			document.location.href=href+"?"+csrfToken+"&path=" + id + "&nomedoc=" + encodeURIComponent(nomedoc);

 }
  
