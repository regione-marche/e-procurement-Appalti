
/*
 * Gestione della lista delle gare ed elenchi associati ad una occorrenza di qformlib
 */

$(window).on("load", function (){
	
	var _tableGare = null;
	$(".contenitore-dettaglio").width("980");

	_getDettaglio();
	
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
	
	function _creaTabellaGare() {
		var _table = $('<table/>', {"id": "gare", "class": "schedagperm", "cellspacing": "0", "width" : "100%"});
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
		
		$("#gareContainer").append(_table);	
	}
	
	function _getDettaglio() {
		_waitgperm();
		_creaTabellaGare();
		var id=$("#id").val();
		$.ajax({
			type: "POST",
			dataType: "json",
			async: true,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: "pg/GetDettaglioGareQformlib.do",
			data : "id=" +  id,
			success: function(json){
				_popolaGare(json.data);
			},
			error: function(e){
				 alert("Errore durante la lettura dell'elenco delle gare ed elenchi");
			},
			complete: function() {
				_nowaitgperm();
	        }
		});
	}
	
	

	/*
	 * Popola la tabella con la lista delle gare
	 */
	function _popolaGare(dettagliGare) {
				
		_tableGare = $('#gare').removeAttr('width').DataTable( {
			"data": dettagliGare,			
			"bAutoWidth": false,			
			"columnDefs": [
				{	
					"data": "genere",
					"visible": true,
					"sTitle": "Gara o elenco?",
					"searchable": true,
					"sClass": "aligncenter",
					"sWidth": "50px",
					"targets": [ 0 ]
				},
				{	
					"data": "codice",
					"visible": true,
					"sTitle": "Codice",
					"searchable": true,
					"sClass": "aligncenter",
					"sWidth": "70px",
					"targets": [ 1 ]
				},
				{	
					"data": "oggetto",
					"visible": true,
					"sTitle": "Oggetto",
					"searchable": true,
					"sClass": "aligncenter",
					"targets": [ 2 ]
				},
				{
					"data": "nomtec",
					"visible": true,
					"sTitle": "RUP",
					"targets": [ 3 ],
					"sWidth": "150px",
					"sClass" : "aligncenter",
					"searchable": true
				},
				{
					"data": "busta",
					"visible": true,
					"sTitle": "Busta",
					"targets": [ 4 ],
					"sWidth": "50px",
					"sClass" : "aligncenter",
					"searchable": true
				},
				{
					"data": "data",
					"visible": true,
					"sTitle": "Data ult. attiv.mod.",
					"targets": [ 5 ],
					"sWidth": "100px",
					"searchable": true,
					"class" : "aligncenter"
				},
				{
					"data": "stato",
					"visible": true,
					"sTitle": "Stato",
					"targets": [ 6 ],
					"sWidth": "100px",
					"searchable": true,
					"class" : "aligncenter"
				}
	        ],
	        "language": {
				"sEmptyTable":     "Non ci sono gare o elenchi che utilizzano il modello",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ righe",
				"sInfoEmpty":      "",
				"sInfoFiltered":   "(su _MAX_ gare ed elenchi totali)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca gare ed elenchi",
				"sZeroRecords":    "Non ci sono gare o elenchi che utilizzano il modello",
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
					$("#gare tfoot").hide();
					$("#dgare_info").hide();
					$("#gare_paginate").hide();
				}
			},
			
	        "pagingType": "full_numbers",
	        "lengthMenu": [[50, 70, 100], ["50 righe", "70 righe", "100 righe"]],
	        "ordering": false,
	        "aoColumns": [
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true }
			   ]
	    });
	
		$('#gare tfoot td').eq(0).html( '<input class="search" size="10" type="text" placeholder="Ricerca genere"/>' );
		$('#gare tfoot td').eq(1).html( '<input class="search" size="10" type="text" placeholder="Ricerca codice"/>' );
		$('#gare tfoot td').eq(2).html( '<input class="search" size="10" type="text" placeholder="Ricerca oggetto"/>' );
		$('#gare tfoot td').eq(3).html( '<input class="search" size="10" type="text" placeholder="Ricerca rup"/>' );
		$('#gare tfoot td').eq(4).html( '<input class="search" size="10" type="text" placeholder="Ricerca busta "/>' );
		$('#gare tfoot td').eq(5).html( '<input class="search" size="10" type="text" placeholder="Ricerca data"/>' );
		$('#gare tfoot td').eq(6).html( '<input class="search" size="10" type="text" placeholder="Ricerca stato"/>' );
					
			
		_tableGare.columns().eq(0).each( function (colIdx) {
			$('input', _tableGare.column(colIdx).footer()).on( 'keyup change', function () {
				_tableGare.column(colIdx).search(this.value).draw();
				
			});
	    });
	    
	    $("#gare_filter").hide();

	}
});

