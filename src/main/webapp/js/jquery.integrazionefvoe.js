var id_tabella_modale = "selditte";
var numrda = [];
/**
 * Crea la struttura della tabella
 */
function _creaTabellaDitte(id){
	var _table = $('<table/>', {"id": id, "class": "schedagperm", "cellspacing": "0", "width" : "100%"});

	var _tr = $('<tr/>', {"class": "intestazione"});
	var _thck = $('<th/>', {"id": "selpos_"+id,"class": "ck"});
	_tr.append(_thck);
	_tr.append('<th/>');
	_tr.append('<th/>');

	var _thead = $('<thead/>');
	_thead.append(_tr);

	var _tbody = $('<tbody/>');

	var _tr2 = $('<tr/>', {"class": "intestazione"});
	_tr2.append('<td/>');
	_tr2.append('<td/>');
	_tr2.append('<td/>');
	var _tfoot = $('<tfoot/>');
	_tfoot.append(_tr2);

	_table.append(_thead);
	_table.append(_tbody);
	_table.append(_tfoot);

	$("#container_"+id).append(_table);
}

/**
 * Popola i dati della tabella
 */
function _popolaDati(data,codgar,id){
	_creaTabellaDitte(id);
	$('#'+id).removeAttr('width').DataTable( {
		"data": data.map(el => {
				var procData = {
					"idpartecipante": el.idpartecipante|| null,
					"dittao": el.dittao || null,
					"nomimo": el.nomimo || null
					}
				return procData;
				}),

		"bAutoWidth": true,

		"columnDefs": [
			{
				"data": "checkPos",
				"visible": true,
				"targets": [ 0 ],
				"class" : "ck",
				"sTitle": "Seleziona",
				"sWidth" : "20px",
				"searchable": false,
				"render": function (data, type, full, meta ) {
					var _div = $("<div/>");
					var _check = $("<input/>",{"type":"checkbox", "id": id+"_ck_pos_" + full.dittao});
					_div.append(_check);
					return _div.html();
				}
			},
			{
				"data": "dittao",
				"visible": true,
				"sTitle": "Codice ditta",
				"sWidth": "50px",
				"class" : "dt-body-center",
				"searchable": true,
				"targets": [ 1 ]
			},
			{
				"data": "nomimo",
				"visible": true,
				"sTitle": "Ragione sociale ditta",
				"sWidth": "300px",
				"class" : "dt-body-center",
				"searchable": true,
				"targets": [ 2 ]
			}
		],

		"drawCallback": function ( settings ) {
		},

		"language": {
			"sEmptyTable":     "Nessun risultato",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ ditte",
			"sInfoEmpty":      "Nessuna ditta aggiungibile",
			"sInfoFiltered":   "(su _MAX_  ditte totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sSearch":         "Cerca ditta",
			"sZeroRecords":    "Nessuna ditta trovata",
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
				$("#"+id+" tfoot").hide();
				$("#"+id+"_info").hide();
				$("#"+id+"_paginate").hide();
			}
		},

		"pagingType": "full_numbers",
		"lengthMenu": [[20, 50, 100], ["20  ditte", "50 ditte", "100 ditte"]],
		"ordering": true,
		"order": [[ 1, "asc" ]],
		"aoColumns": [
			{ "bSortable": false, "bSearchable": false },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true},
		]
	});
	
	var columns = $('#'+id+' tfoot td').length;
	for(var col=1; col<columns; col++){
		$('#'+id+' tfoot td').eq(col).html( '<input class="search" size="20" type="text" placeholder="Ricerca"/>' );
	}
	
	var _center = $("<center/>");
	var _href = "<a href='javascript:_selezionaTutti(\""+id+"\");' Title='Seleziona tutti'> <img src='"+_contextPath+"/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>";
	var _href = _href + "&nbsp;";
	var _href = _href + "<a href='javascript:_deselezionaTutti(\""+id+"\");' Title='Deseleziona tutti'><img src='"+_contextPath+"/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>";
	_center.append(_href);
	_center.appendTo($("#selpos_"+id));
	$('#'+id).DataTable().columns().eq(0).each( function (colIdx) {
		$('input', $('#'+id).DataTable().column(colIdx).footer()).on( 'keyup change', function () {
			$('#'+id).DataTable().column(colIdx).search(this.value).draw();
		});
	});

	$("#"+id+"_filter").hide();
}
/**
 * Genera la tabella, recuperando i dati del tabellato in primis
 */	
function _generaTabella(data,codgar,id){
    _popolaDati(data,codgar,id);

    $.fn.dataTable.ext.order['dom-checkbox'] = function (settings, col)
    {
        return this.api().column(col, {order:'index'}).nodes().map(function (td, i) {
            return $('input',td).prop('checked') ? '1' : '0';
        } );
    };   
}

function _selezionaTutti(id) {
   $( "input[id^='"+id+"_ck_pos_']" ).prop("checked","checked");
}

function _deselezionaTutti(id) {
    $( "input[id^='"+id+"_ck_pos_']" ).each( function( index ) {
        if ($( this ).prop( "disabled")) {
            ;
        }else{
            $( this ).prop( "checked",false);
        }
    });
}

/**
 * Crea la finestra principale modale
 */
function _creaFinestraSelDitte(codgar) {
	var id = id_tabella_modale;
	var title = "Seleziona le ditte per cui richiedere l'accesso FVOE";
	var _finestraSelDitte = $("<div/>",{"id": "finestraselditte", "title":title});
	_finestraSelDitte.dialog({
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
		
		autoOpen: true,
		show: {
			effect: "blind",
			duration: 0
		},
		hide: {
			effect: "blind",
			duration: 0
		},
		
		height: 425,
		width: 1000,
		modal: true,
		
		buttons: {
			"Conferma":  function () {
				_aggiugiDitte(codgar)
			},
			"Annulla": function() {
				_deselezionaTutti(id)
				$( this ).dialog( "close" );
			}
		}
	});
	

	_finestraSelDitte.on( "dialogclose", function( event, ui ) {

		if ($('#'+id).DataTable() != null) {
			;
		}else{
			historyVaiIndietroDi(1);
		}

	});

}

/**
 * Avvia la ricerca
 */
function _trovaDitte(codgar, soloNonInserite) {

		var tableAgg = $("#"+id_tabella_modale+"_wrapper");
		if (tableAgg != null) {
			tableAgg.remove();
		}
		var jsonData = [];
		$.ajax({
			type: "GET",
			dataType: "json",
			url: "pg/GestioneDitteFVOE.do",
			data : {
					metodo: 'getDitte',
					codgar: codgar,
					soloNonInserite: soloNonInserite
			},
			async: false,
            success: function(data) {
				if(data.esito){
					jsonData = data.listaDitte;
				}
            }
		})
		_generaTabella(jsonData,codgar,id_tabella_modale);
}

function _aggiugiDitte(codgar){
	var id = id_tabella_modale;
	$('#'+id).DataTable().columns().eq(0).each( function (colIdx) {
		$('#'+id).DataTable().column(colIdx).search('');
	})
	var tableResultXpage= $('#'+id).DataTable().page.info()?.length;
	$('#'+id).DataTable().page.len(-1).draw();

	var nSelected = 0;
	var arrmultikey  = '';
	$( "input[id^='"+id+"_ck_pos_']" ).each( function( index ) {
		if($( this ).prop( "checked")){
			nSelected= nSelected + 1;
			_ck_id = $(this).attr("id");
			_ck_key = _ck_id.split(id+'_ck_pos_')[1];
			arrmultikey  = arrmultikey + _ck_key + ';';
		}
	});

	if (!(nSelected > 0)){
		$('#'+id).DataTable().page.len(tableResultXpage)?.draw();
		alert("Selezionare almeno una ditta");
	} else {
		bloccaRichiesteServer();
		$.ajax({
			type: "POST",
			dataType: "json",
			url: "pg/GestioneDitteFVOE.do",
			data: {
				metodo: 'setDitte',
				codgar: codgar,
				arrmultikey: arrmultikey
			},
			async: false,
			success: function (data) {
				if(data.esito === 'OK')
					ricaricaPagina();

			},
		})

	}
}


function _creaContainerListaDitte(codgar) {
	var _container = $("<table/>", {"id": "container_modal", "class": "dettaglio-notab"});
	_container.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	var _tr = $("<tr/>");
	var _td = $("<td/>");
	_td.css("border-bottom","0px");
	
	var _divSearchDitteContainer = $("<div/>", {"margin-top": "25px", "margin-bottom": "25px"});
	var _divListaDitteContainer = $("<div/>", {"id": "container_"+id_tabella_modale, "margin-top": "25px", "margin-bottom": "25px", "width" : "98%"});

	var _span_descrizione = $("<span/>");

	_span_descrizione.append("<p>Mediante questa funzione &egrave; possibile selezionare le ditte partecipanti alla gara per cui richiedere l'accesso a FVOE</p>");
	_span_descrizione.append("<p>Per le ditte partecipanti in RT, vengono considerate direttamente le componenti del raggruppamento. Nel caso di consorzio vengono inoltre riportate le eventuali consorziate esecutrici mentre per le ditte che fanno ricorso ad avvalimento le relative ausiliarie.</p>");
	_span_descrizione.append("<p>Nel caso di rettifiche, è possibile integrare la lista delle ditte oggetto di verifica forzando, mediante il flag 'Mostra ditte già selezionate', la selezione delle ditte già inserite nella lista.</p>");

	_divSearchDitteContainer.append(_span_descrizione);


	var _span_search_filtro = $("<span/>");
	var _search_filtro = $("<select/>",{"id":"_search_filtro","size": "35","width":"40px"});

	_span_search_filtro.append("Mostra ditte gi\u00E0 selezionate: &nbsp;");

	_span_search_filtro.append(_search_filtro);
	_search_filtro.append( '<option value="true">No</option>' )
	_search_filtro.append( '<option value="false">Si</option>' )
	_divSearchDitteContainer.append(_span_search_filtro);

		_search_filtro.on("change", function() {
		_trovaDitte(codgar,$('#_search_filtro').val());
	});

	_td.append(_divSearchDitteContainer);
	_td.append("<br/><br/>");
	_td.append(_divListaDitteContainer);
	_td.append("<br/><br/>");
	_tr.append(_td);
	_container.append(_tr);
	$("#finestraselditte").append(_container);
	_trovaDitte(codgar,"true");
}

	

