var widthM= 1300;
var	heightM= 650;

function apriModaleLeggiDocumentiBIC(data) {

	pingSessioneAttiva();
	popolaTabella(data);

	var titolo="Seleziona documenti BIC";

	var opt = {
		open: function(event, ui) {
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
		modal: true,
		width: widthM,
		height: heightM,
		draggable: false,
		resizable: false,
		title: titolo,
		position: { using: function( pos ) {
				$( this ).css({
					top:'50%',
					left: '50%',
					marginRight: '50%',
					transform: 'translate(-50%, -50%)'
				});
			}},
		buttons: {
			"Seleziona": {
				id:"botConferma",
				text:"Conferma",
				click: function() {
					copiaDocumenti();
					copiaDocumenti();
				}
			},
			"Annulla": {
				id:"botAnnulla",
				text:"Annulla",
				click:function() {
					$( this ).dialog( "close" );
				}
			},
			"Chiudi": {
				id:"botChiudi",
				text:"Chiudi",
				click:function() {
					historyReload();
				}
			}
		}
	};
	$("#mascheraCopiaDocumenti").dialog(opt).dialog("open");
	$("#mascheraCopiaDocumenti").parent().css("position","fixed");
	$("#botChiudi, #sezioneFinale, #sezioneErrore, #sezioneSuccesso").hide();
}

function creaTabella() {

	var headerRow = $('<tr/>', {"class": "intestazione"});
	var selChecks = $('<th/>', {"id": "selChecks","class": "ck"});
	headerRow.append(selChecks);
	headerRow.append('<th/>');
	headerRow.append('<th/>');
	headerRow.append('<th/>');
	headerRow.append('<th/>');

	headerRow.append('<th/>');
	headerRow.append('<th/>');

	var thead = $('<thead>');
	thead.append(headerRow);

	var tbody = $('<tbody>');

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

	var tabella = $('<table>', {"id": "documenti", "class": "schedagperm", "cellspacing": "0"});
	tabella.append(thead);
	tabella.append(tbody);
	tabella.append(_tfoot);

	$('#documentiContainer').append(tabella);
}

var regexBase = '';

function popolaTabella(data) {
	$('#documentiContainer').empty();
	creaTabella();

	_tableDocumenti = $('#documenti').removeAttr('width').DataTable({
		"data": data,
		searching: true,
		"ordering": false,
		"bAutoWidth": false,
		"columnDefs": [
			{
				"visible": true,
				"sTitle": "",
				"targets": [ 0 ],
				"sClass": "datadescr",
				"render": function ( data, type, full, meta ) {
					var _div = $("<div/>");
					var _check = $("<input/>",{"type":"checkbox", "name": "documentiSelezionati", "value": full.codiceFiscaleSa+"#"+full.codiceCommessa+"#"+full.numeroAppalto+"#"+full.numeroCantiere+"#"+full.numeroEdificio});
					_div.append(_check);
					return _div.html();
				}
			},
			{
				"searchable": true,
				"data": "codiceFiscaleSa",
				"visible": true,
				"sTitle": "CF S.A.",
				"width": "10%",
				"sClass": "descr",
				"targets": [ 1 ],
			},
			{
				"searchable": true,
				"data": "codiceCommessa",
				"visible": true,
				"sTitle": "Commessa/Appalto",
				"targets": [ 2 ],
				"width": "15%",
				"class" : "descr",
				"render": function ( data, type, full, meta ) {
					return full.codiceCommessa + "/" + full.numeroAppalto;
				},
			},
			{
				"searchable": true,
				"data": "numeroAppalto",
				"visible": false,
				"sTitle": "numeroAppalto",
				"targets": [ 3 ],
				"class" : "descr"
			},
			{
				"searchable": true,
				"data": "codiceEdificio",
				"visible": true,
				"sTitle": "Codice edificio",
				"targets": [ 4 ],
				"class" : "descr"
			},
			{
				"data": "nomeEdificio",
				"visible": true,
				"sTitle": "Nome edificio",
				"targets": [ 5 ],
				"width": "25%",
				"class" : "descr"
			},
			{
				"data": "urlDoc",
				"visible": true,
				"sTitle": "Url documento",
				"searchable": true,
				"width": "25%",
				"targets": [ 6 ]
			}
		],
		"aoColumns": [
			{ "bSortable": false, "bSearchable": false },
			{ "bSortable": false, "bSearchable": true },
			{ "bSortable": false, "bSearchable": true },
			{ "bSortable": false, "bSearchable": true },
			{ "bSortable": false, "bSearchable": true },
			{ "bSortable": false, "bSearchable": true },
			{ "bSortable": false, "bSearchable": true }
		],
		language: {
			"sEmptyTable":     "Non ci sono documenti da copiare",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ documenti",
			"sInfoEmpty":      "Non ci sono documenti da copiare",
			"sInfoFiltered":   "",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sSearch":         "Cerca documenti",
			"sZeroRecords":    "Non ci sono documenti da copiare",
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
				$("#documenti tfoot").hide();
				$("#documenti_info").hide();
				$("#documenti_paginate").hide();
			}
		},
		"pagingType": "full_numbers",
		"lengthMenu": [[10, 25, 50], ["10 documenti", "25 documenti", "50 documenti"]],
	});
	var _center = $("<center/>");
	var _href = "<a href='javascript:showMenuSelezione();' Title='Seleziona tutti'> <img src='"+_contextPath+"/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>";
	var _href = _href + "&nbsp;";
	var _href = _href + "<a href='javascript:showMenuDeselezione();' Title='Deseleziona tutti'><img src='"+_contextPath+"/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>";

	var _href = _href + '<div id="popmenuCheck" class="popupmenuskin" style="visibility: hidden;text-align: left;"><div class="chiusuramenu"><a href="javascript:hideMenuSelezione();"><img src="'+_contextPath+'/img/chiudi.gif" height="8" width="8" alt="Chiudi" title="Chiudi"></a></div><div class="menuitems" id=""><a href="javascript:_selezionaTutti();" tabindex="0">&nbsp;Seleziona tutti su pagina corrente</a></div><div class="menuitems" id=""><a href="javascript:_selezioneTuttePagine();" tabindex="1">&nbsp;Seleziona su tutte le pagine</a></div></div>'
	var _href = _href + "&nbsp;";
	var _href = _href + '<div id="popmenuUncheck" class="popupmenuskin" style="visibility: hidden;text-align: left;"><div class="chiusuramenu"><a href="javascript:hideMenuDeselezione();"><img src="'+_contextPath+'/img/chiudi.gif" height="8" width="8" alt="Chiudi" title="Chiudi"></a></div><div class="menuitems" id=""><a href="javascript:_deselezionaTutti();" tabindex="0">&nbsp;Deseleziona tutti su pag corrente</a></div><div class="menuitems" id=""><a href="javascript:_deselezioneTuttePagine();" tabindex="1">&nbsp;Deseleziona su tutte le pagine</a></div></div>'
	_center.append(_href);
	_center.appendTo($("#selChecks"));

	var tableResultXpage= $('#documenti').DataTable().page.info()?.length;
	var page= $('#documenti').DataTable().page();
	$('#documenti').DataTable().page.len(-1).draw();
	$('#documenti').DataTable().page.len(tableResultXpage)?.draw();
	$('#documenti').DataTable().page(page).draw(false);

	var columnNo = 1;
	$('#documenti tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca cf "/>' );
	$('#documenti tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca commessa"/>' );
	$('#documenti tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca cod. ed."/>' );
	$('#documenti tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca nome ed."/>' );
	$('#documenti tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca url documento"/>' );

	_tableDocumenti.columns().eq(0).each( function (colIdx) {
		$('input', _tableDocumenti.column(colIdx).footer()).on( 'keyup change', function () {
			if (colIdx != 1) {
				_tableDocumenti.column(colIdx).search(this.value).draw();
			}
			else if (colIdx == 1) {
				if (this.value == '') {
					_tableDocumenti.column(1).search(regexBase,true,false).draw();
				} else {
					let regex = '(?=.*' + this.value + ')' + regexBase;
					_tableDocumenti.column(1).search(regex,true,false).draw();
				}
			}
		});
	});


	$("#documenti_filter").hide();
}

function showMenuSelezione(){
	hideMenuDeselezione();
	$('#popmenuCheck').css('visibility','visible');
}
function showMenuDeselezione(){
	hideMenuSelezione();
	$('#popmenuUncheck').css('visibility','visible');
}

function hideMenuSelezione(){
	$('#popmenuCheck').css('visibility','hidden');
}
function hideMenuDeselezione(){
	$('#popmenuUncheck').css('visibility','hidden');
}

function _selezionaTutti() {
	hideMenuSelezione();
	$("input[name='documentiSelezionati']").prop("checked","checked");
}

function _deselezionaTutti() {
	hideMenuDeselezione();
	$( "input[name='documentiSelezionati']" ).each( function( index ) {
		if ($( this ).prop( "disabled")) {
			;
		}else{
			$( this ).prop( "checked",false)
		}
	});
}

function _selezioneTuttePagine() {
	var tableResultXpage= $('#documenti').DataTable().page.info()?.length;
	var page= $('#documenti').DataTable().page();
	$('#documenti').DataTable().page.len(-1).draw();
	_selezionaTutti();
	$('#documenti').DataTable().page.len(tableResultXpage)?.draw();
	$('#documenti').DataTable().page(page).draw(false);
}

function _deselezioneTuttePagine() {
	var tableResultXpage= $('#documenti').DataTable().page.info()?.length;
	var page= $('#documenti').DataTable().page();
	$('#documenti').DataTable().page.len(-1).draw();
	_deselezionaTutti();
	$('#documenti').DataTable().page.len(tableResultXpage)?.draw();
	$('#documenti').DataTable().page(page).draw(false);
}



function copiaSelezionati() {
	var tableResultXpage= $('#documenti').DataTable().page.info()?.length;
	var page= $('#documenti').DataTable().page();
	$('#documenti').DataTable().page.len(-1).draw();
	var selezionati = 0;
	$( "input[name='documentiSelezionati']:checked").each( function( index ) {
		if ($( this ).prop( "disabled")) {
		}else{
			selezionati++
		}
	});

	if (selezionati === 0) {
		alert('Non è stato selezionato nessun documento.');
	}else{
		document.formRecuperaDocBic.submit();
		bloccaRichiesteServer();
	}
	$('#documenti').DataTable().page.len(tableResultXpage)?.draw();
	$('#documenti').DataTable().page(page).draw(false);
}

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
	}, 50);
}
