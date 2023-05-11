var _tabellatoSettore = null;
var _tabellatoUnimis = null;
var _searchRow=$('<tr/>');
var _bRow=$('<br/>');
var id_tabella_modale = "aggrda";
var numrda = [];
/**
 * Recupera i dati del tabellato W3z05
 */
function _getTabellatoSettore() {
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
		data: "tab1cod=W3z05",
		success: function(data) {
			if (data) {
				_tabellatoSettore = data;
			}
		},
		error: function(e){
			alert("Errore durante la lettura del tabellato settore");
		}
	});
}
/**
 * Crea la struttura della tabella
 */
function _creaTabellaRda(id){
	var _table = $('<table/>', {"id": id, "class": "schedagperm", "cellspacing": "0", "width" : "100%"});

	var _tr = $('<tr/>', {"class": "intestazione"});
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<td/>');
	var _thck = $('<th/>', {"id": "selpos_"+id,"class": "ck"});
	_tr.append(_thck);

	var _thead = $('<thead/>');
	_thead.append(_tr);

	var _tbody = $('<tbody/>');

	var _tr2 = $('<tr/>', {"class": "intestazione"});
	_tr2.append('<td/>');
	_tr2.append('<td/>');
	_tr2.append('<td/>');
	_tr2.append('<td/>');
	_tr2.append('<td/>');
	_tr2.append('<td/>');
	_tr2.append('<td/>');
	_tr2.append('<td/>');
	_tr2.append('<td/>');
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

	$("#container_"+id).append(_table);
}

/**
 * Popola i dati della tabella
 */
function _popolaRda(data,codgar,id,ngara,bloccoSoloLettura,bloccoPubblicata){
	_creaTabellaRda(id);	
	var importo_collegato = 0;
	$('#'+id).removeAttr('width').DataTable( {
		"data": data.filter(el => (id!=id_tabella_modale || !numrda.includes(el.codRdA+"") )).map(el => {
			if(id!=id_tabella_modale){
				numrda.push(el.codRdA+"");
			}
				var procData = {
					"codRdA": el.codRdA|| null,
					"cfTec": el.cfTec || null,
					"nomTec": el.nomTec || null,
					"settore": el.settore|| null,
					"descrizione": el.descrizione|| null,
					"denominazioneAmministrazione": el.amministrazione|| null,
					"codArtCat": (el.codiceArticolo?el.codiceArticolo:'')+((el.codiceCatalogo || el.codiceArticolo)?'/':'')+(el.codiceCatalogo? el.codiceCatalogo:''),
					"descrizioneArticolo": el.descrizioneArticolo || null,
					"categoriaMerceologicaDesc": el.categoriaMerceologicaDesc ||null,
					"prezzoUnitario": el.prezzoUnitario|| null,
					"unitaMisuraDesc": el.unitaMisuraDesc||null,
					"quantita": el.quantita|| null,
					"importo": el.importo|| null,
					"anno": el.anno|| null,
					"lotti": el.lotti|| 'Non collegato'
					}
				if(id != id_tabella_modale)
					importo_collegato += el.importo|| 0;
				return procData;
				}),

		"bAutoWidth": false,

		"columnDefs": [
			{
				"data": "codRdA",
				"visible": true,
				"sTitle": "Id",
				"className": "dt-body-center",
				"sWidth" : "0px",
				"searchable": true,
				"targets": [ 0 ]
			},
			{
				"data": "nomTec",
				"visible": true,
				"sTitle": "RUP",
				"sWidth": "0px",
				"class" : "dt-body-center",
				"searchable": true,
				"targets": [ 1 ]
			},
			{
				"data": "settore",
				"visible": true,
				"sTitle": "Settore",
				"sWidth": "0px",
				"class" : "dt-body-center",
				"searchable": true,
				"targets": [ 2 ],
				"render": function ( data, type, full, meta ) {
					var _ret = "";
					$.map( _tabellatoSettore, function( item ) {
						if (item.tipoTabellato == full.settore) {
							_ret = item.descTabellato;
						}
						
					})
					return _ret;
				}
				
			},
			{
				"data": "descrizione",
				"visible": true,
				"sTitle": "Descrizione",
				"sWidth": "100px",
				"class" : "descr",
				"searchable": true,
				"targets": [ 3 ]
			},
			 {
				"data": "denominazioneAmministrazione",
				"visible": true,
				"sTitle": "Unit&agrave; organizzativa",
				"sWidth": "100px",
				"class" : "descr",
				"searchable": true,
				"targets": [ 4 ]
			},
			{
				"data": "codArtCat",
				"visible": true,
				"sTitle": "Cod. Art./Cat.",
				"className": "dt-body-center",
				"sWidth" : "0px",
				"searchable": true,
				"targets": [ 5 ]
			},
			{
				"data": "descrizioneArticolo",
				"visible": false,
				"sTitle": "Descrizione articolo",
				"sWidth": "100px",
				"class" : "descr",
				"searchable": true,
				"targets": [ 6 ]
			},
			{
				"data": "categoriaMerceologicaDesc",
				"visible": false,
				"sTitle": "Cat. merceologica",
				"sWidth": "100px",
				"class" : "descr",
				"searchable": true,
				"targets": [ 7 ]
			},
			{
				"data": "unitaMisuraDesc",
				"visible": true,
				"sTitle": "Um",
				"className": "dt-body-center",
				"sWidth" : "0px",
				"searchable": true,
				"targets": [ 8 ]
			},
			{
				"data": "quantita",
				"visible": true,
				"sTitle": "Quantit&agrave;",
				"render": $.fn.dataTable.render.number('.', ',', 2, '','') ,
				"className": "dt-body-center",
				"sWidth" : "0px",
				"searchable": true,
				"targets": [ 9 ]
			},
			{
				"data": "prezzoUnitario",
				"visible": true,
				"sTitle": "Prezzo",
				"className": "dt-body-center",
				"render": $.fn.dataTable.render.number('.', ',', 2, '',' \u20ac') ,
				"sWidth" : "0px",
				"searchable": true,
				"targets": [ 10 ]
			},
			{
				"data": "importo",
				"visible": true,
				"sTitle": "Importo",
				"render": $.fn.dataTable.render.number('.', ',', 2, '',' \u20ac') ,
				"className": "dt-body-center",
				"sWidth" : "0px",
				"searchable": true,
				"targets": [ 11 ]
			},
			{
				"data": "anno",
				"visible": false,
				"sTitle": "Anno",
				"className": "dt-body-center",
				"sWidth" : "0px",
				"searchable": true,
				"targets": [ 12 ]
			},
			{
				"data": "lotti",
				"visible": true && !isLotto && id != id_tabella_modale && !lottoUnico && isNotHomepage,
				"sTitle": "Lotti",
				"className": "dt-body-center",
				"sWidth" : "0px",
				"searchable": true,
				"targets": [ 13 ]
			},
			{
				"data": "checkPos",
				"visible": true && !bloccoSoloLettura,
				"targets": [ 14 ],
				"class" : "ck",
				"sWidth" : "70px",
				"render": function (data, type, full, meta ) {
					var _div = $("<div/>");
					var _check = $("<input/>",{"type":"checkbox", "id": id+"_ck_pos_" + full.codRdA});
					_div.append(_check);
					return _div.html();
				}
			}
		],

		"drawCallback": function ( settings ) {

		},

		"language": {
			"sEmptyTable":     "Nessun risultato",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ RdA/RdI",
			"sInfoEmpty":      "Non ci sono  RdA o RdI",
			"sInfoFiltered":   "(su _MAX_  RdA/RdI totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sSearch":         "Cerca  RdA/RdI",
			"sZeroRecords":    "Non ci sono  RdA o RdI",
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
		"lengthMenu": id == id_tabella_modale ? [[20, 50, 100], ["20  RdA/RdI", "50 RdA/RdI", "100 RdA/RdI"]] : [[50, 70, 100], ["50  RdA/RdI", "70 RdA/RdI", "100 RdA/RdI"]],
		"ordering": true,
		"order": [[ 0, "asc" ]],
		"aoColumns": [
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
		]
	});
	
	var columns = $('#'+id+' tfoot td').length;
	for(var col=0; col<columns-1; col++){
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
	
	if(id != id_tabella_modale){
		 $('#menucrea, #pulscrea').unbind('click').bind('click',function() {
			// Per salvare le modifiche di tutte le pagine della lista paginata
			// si forza la visualizzazione di tutti i record della lista e poi
			// si avvia l'elaborazione di salvataggio dei dati.
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
				alert("Selezionare almeno una RdA/RdI");
			} else{
				document.location.href = contextPath + "/pg/InitNuovaGara.do?"+csrfToken+"&arrRda="+arrmultikey;
			}
		});
		
		$('#menuelimina, #pulselimina').unbind('click').bind('click',function() {
			// Per salvare le modifiche di tutte le pagine della lista paginata
			// si forza la visualizzazione di tutti i record della lista e poi
			// si avvia l'elaborazione di salvataggio dei dati.
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
				alert("Selezionare almeno una RdA/RdI");
			} else{
				bloccaRichiesteServer();
				var newUrl = contextPath + "/pg/CollegaScollegaRda.do?"+csrfToken+"&codgar="+codgar+"&handleRda=scollega&arrRda="+arrmultikey+"&autorizzatoModifiche=1&bloccoModificatiDati="+bloccoPubblicata;
				if(ngaraParam){
					newUrl = newUrl.replace('handleRda=scollega','handleRda=scollegalotto');
					newUrl += "&ngara="+ngara;
				}
				document.location.href = newUrl;
			}
		});
	}

	$("#"+id+"_filter").hide();
	if(id != id_tabella_modale){
		$("#importo_collegato").prepend("<div class='div_importo'><b>Totale derivante dal dettaglio RdA/RdI:</b><span class='importo'>"+importo_collegato.toLocaleString('it-IT', { style: 'currency', currency: 'EUR' })+"</span></div>");
		formCalcolaImporto.importo.value=importo_collegato;
	}
}
/**
 * Genera la tabella, recuperando i dati del tabellato in primis
 */	
function _generaTabella(data,codgar,id,ngara,bloccoSoloLettura,bloccoPubblicata){
	_getTabellatoSettore();
    _popolaRda(data,codgar,id,ngara,bloccoSoloLettura,bloccoPubblicata);

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
function _creaFinestraSelRda(codgar,ngara) { 
	var id = id_tabella_modale;
	var title = isLotto?'Selezione RdA/RdI da associare al lotto':'Selezione RdA/RdI da associare alla gara';
	var _finestraSelRda = $("<div/>",{"id": "finestraselrda", "title":title});
	_finestraSelRda.dialog({
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
			"Aggiungi":  function() {
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
					alert("Selezionare almeno una RdA/RdI");
				} else{
					bloccaRichiesteServer();
					var newUrl = contextPath + "/pg/CollegaScollegaRda.do?"+csrfToken+"&codgar="+codgar+"&handleRda=collega&arrRda="+arrmultikey+"&autorizzatoModifiche=1&bloccoModificatiDati="+bloccoPubblicata;
					if(ngaraParam){
						newUrl = newUrl.replace('handleRda=collega','handleRda=collegalotto');
						newUrl += "&ngara="+ngara;
					}
					document.location.href = newUrl;
				}
			},
			"Chiudi": function() {
				$( this ).dialog( "close" );
			}
		}
	});
	

	_finestraSelRda.on( "dialogclose", function( event, ui ) {

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
function _trovaRda(codgar,ngara) {

		var tableAgg = $("#"+id_tabella_modale+"_wrapper");
		if (tableAgg != null) {
			tableAgg.remove();
		}
		var _codrda = $("#search_cod_rda").val();
		var _rup = $("#search_search_rup").val();
		var _amministrazione = $("#search_amministrazione").val();
		var _articolo = $("#search_articolo").val();
		//var _rda_collegate = $("#search_rda_collegate").is(":checked");
		var _rda_collegate = $('#search_tiporda').val();

		var jsonData = [];
		$.ajax({
			type: "GET",
			dataType: "json",
			url: "GetRdaRdi.do",
			data : {
					codrda: _codrda,
					rup: _rup,
					amministrazioneDesc: _amministrazione,
					articoloDesc: _articolo,
					type: _rda_collegate,
					codgar: isLotto?codgar:null,
					ngara: isLotto? ngara:null
			},
			async: false,
            success: function(data) {
				if(data.esito){
					jsonData = data.listaRdA;
				}
            }
		})
		_generaTabella(jsonData,codgar,id_tabella_modale,ngara,false);
}


function _creaContainerListaRda(codgar,ngara) {
	var _container = $("<table/>", {"id": "container_modal", "class": "dettaglio-notab"});
	_container.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	var _tr = $("<tr/>");
	var _td = $("<td/>");
	_td.css("border-bottom","0px");
	
	var _divSearchRdaContainer = $("<div/>", {"margin-top": "25px", "margin-bottom": "25px"});
	var _divListaRdaContainer = $("<div/>", {"id": "container_"+id_tabella_modale, "margin-top": "25px", "margin-bottom": "25px", "width" : "98%"});

	
	var _span_search_cod_rda = $("<span/>");
	var _search_cod_rda = $("<input/>",{"id":"search_cod_rda","size": "35","class":"search-input"});
	_span_search_cod_rda.append("&nbsp;&nbsp;&nbsp;Codice RdA/RdI&nbsp;");
	_span_search_cod_rda.append(_search_cod_rda)
	//_divSearchRdaContainer.append(_span_search_cod_rda); nascosto
	
	var _span_search_tiporda = $("<span/>");
	var _search_tiporda = $("<select/>",{"id":"search_tiporda","size": "35","width":"100px"});
	
	var _span_descrizione = $("<span/>");
	if(isLotto){
		_span_descrizione.append("<p>Mediante questa funzione &egrave; possibile collegare RdA/RdI della gara al lotto corrente.</p>");
		//_span_descrizione.append("<p>&Egrave; possibile collegare RdA/RdI gi&agrave; collegate ad altri lotti della gara mediante l'opzione sottostante.</p>");
		_span_search_tiporda.append("Considera RdA/RdI gi\u00E0 collegate ad altri lotti della gara?&nbsp;");
	}else{
		_span_descrizione.append("<p>Mediante questa funzione &egrave; possibile collegare RdA/RdI alla gara corrente.");
		//_span_descrizione.append("<p>Il men&ugrave; a tendina permette di applicare o rimuovere un filtro per visualizzare le sole RdA/RdI non collegate ad alcuna gara.</p>");
		_span_search_tiporda.append("Considera RdA/RdI gi\u00E0 collegate ad altre gare?&nbsp;");
	}
	_divSearchRdaContainer.append(_span_descrizione);

	
	_span_search_tiporda.append(_search_tiporda);
	_search_tiporda.append( '<option value="false">No</option>' )
	_search_tiporda.append( '<option value="true">Si</option>' )
	_divSearchRdaContainer.append(_span_search_tiporda);

	_td.append(_divSearchRdaContainer);
	_td.append("<br/><br/>");
	_td.append(_divListaRdaContainer);
	_td.append("<br/><br/>");
	_tr.append(_td);
	_container.append(_tr);
	$("#finestraselrda").append(_container);
	_trovaRda(codgar,ngara);
	
	$('#search_tiporda').on( "change", function( event, ui ) {
		_trovaRda(codgar,ngara);
	})
}

function _creaFinestraConferma() { 

$("<div id='dialog-message' title='Calcolo importo a base di gara da RdA/RdI'>"
+"<p> Confermi l'aggiornamento dell'importo a base di gara con l'importo derivante dal dettaglio delle RdA/RdI associate?</p>"
+"</div>").dialog({
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
		position: { using: function( pos ) {
                $( this ).css({
					top:'50%',
					left: '50%',
					marginRight: '50%',
					transform: 'translate(-50%, -50%)'
					
				});
          }},
		autoOpen: true,
		height: 200,
		width: 450,
		modal: true,
      buttons: {
        "Conferma": function() {
          $( this ).dialog( "close" );
		  bloccaRichiesteServer();
		  formCalcolaImporto.submit();
        },
		"Annulla": function() {
				$( this ).dialog( "close" );
			}
      }
    });
  }
	

