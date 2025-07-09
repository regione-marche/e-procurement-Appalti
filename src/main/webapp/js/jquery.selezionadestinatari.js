var _colonne=["Pec","Email","Fax","Operatore"];

$(window).on("load", function (){
	
	var _tableDestinatari = null;
	$(".contenitore-dettaglio").width("980");
	
	var isAbilitatoInvioMailDocumentale = $('#_abilitatoInvioMailDocumentale').val() === 'true';
	var descc = $('#_descc').val() === '1'
	var isInvioFax=  $('#_isInvioFax').val() === 'true';
	var isGenere10or20= _genereGara == '10' || _genereGara == '20';
	var isComunicazioneAnonima=_compub == '3';
	_popolaDestinatari();
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
	
	function _creaTabellaDestinatari() {
		
		var _table = $('<table/>', {"id": "destinatari", "class": "schedagperm", "cellspacing": "0", "width" : "100%"});
		
		var _tr = $('<tr/>', {"class": "intestazione"});
		var _thpec = $('<th/>', {"id": _colonne[0],"class": "ck"}); //12
		_thpec.text("Utilizza PEC");
		_tr.append(_thpec);
		var _themail = $('<th/>', {"id": _colonne[1],"class": "ck"}); //12
		_themail.text("Utilizza email");
		_tr.append(_themail);
		var _thfax = $('<th/>', {"id": _colonne[2],"class": "ck"}); //12
		_thfax.text("Utilizza FAX");
		_tr.append(_thfax);
		var _thfax = $('<th/>', {"id": _colonne[3],"class": "ck"}); //12
		_thfax.text("Seleziona operatore");
		_tr.append(_thfax);
		_tr.append('<th/>', {"class": "codice"});
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
		
		
								
		var _thead = $('<thead/>');
		_thead.append(_tr);

		var _tbody = $('<tbody/>');
		
		var _tr2 = $('<tr/>', {"class": "intestazione"});
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>', {"class": "codice"});
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
		
		$("#destinatariContainer").append(_table);	
		
	}

	/*
	 * Popola la tabella con la lista dei destinatari
	 */
	function _popolaDestinatari() {
		_waitgperm();
		_creaTabellaDestinatari();

		_tableDestinatari = $('#destinatari').removeAttr('width').DataTable( {
			"ajax": {
				"url": "pg/GetListaDestinatari.do",
				"dataSrc": function ( json ) {
						if(json.messaggio){
							alert(json.messaggio);
							return [];
						}else{
						  return json.listaDestinatari;
						}
					},
				"data" : function (n) { 
					return {
						entita: _entita,
						numeroPopup: _numeroPopUp,
						idprg: _idprg,
						idcom: _idcom
					};	
				},
				"complete": function() {
	            	_nowait();
	            }
			},
			
			"bAutoWidth": false,
			
			"columnDefs": [
				{
				"data": "checkPos",
				"visible": !isComunicazioneAnonima,
				"targets": [ 0 ],
				"class" : "ck",
				"sWidth" : "70px",
				"render": function (data, type, full, meta ) {
						var _div = $("<div/>");
						if(full.pec){
							var _check = $("<input/>",{"type":"checkbox", "name": "keys"+_colonne[0], "id": _colonne[0] +'_'+ full.dittao,  "value": full.dittao+"#;#"+full.nomimp+"#;#"+full.pec, "disabled": full.pecDis || !full.mandatariaPresente});
							_div.append(_check);
						}
						return _div.html();
					}
				},
				{
				"data": "checkPos",
				"visible": (!isAbilitatoInvioMailDocumentale || descc) && !isComunicazioneAnonima,
				"targets": [ 1 ],
				"class" : "ck",
				"sWidth" : "70px",
				"render": function (data, type, full, meta ) {
					var _div = $("<div/>");
						if(full.email){
							var _check = $("<input/>",{"type":"checkbox", "name": "keys"+_colonne[1], "id": _colonne[1] +'_'+ full.dittao,  "value": full.dittao+"#;#"+full.nomimp+"#;#"+full.email, "disabled": full.emailDis || !full.mandatariaPresente});
							_div.append(_check);
						}
						return _div.html();
					}
				},
				{
				"data": "checkPos",
				"visible": (!isAbilitatoInvioMailDocumentale || descc) && isInvioFax && !isComunicazioneAnonima,
				"targets": [ 2 ],
				"class" : "ck",
				"sWidth" : "70px",
				"render": function (data, type, full, meta ) {
					var _div = $("<div/>");
						if(full.fax){
							var _check = $("<input/>",{"type":"checkbox", "name": "keys"+_colonne[2], "id": _colonne[2] +'_'+ full.dittao,  "value": full.dittao+"#;#"+full.nomimp+"#;#"+full.fax, "disabled": full.faxDis || !full.mandatariaPresente});
							_div.append(_check);
						}
						return _div.html();
					}
				},
				{
				"data": "checkPos",
				"visible": isComunicazioneAnonima,
				"targets": [ 3 ],
				"class" : "ck",
				"sWidth" : "70px",
				"render": function (data, type, full, meta ) {
						var _div = $("<div/>");
						var _check = $("<input/>",{"type":"checkbox", "name": "keys"+_colonne[3], "id": _colonne[3] +'_'+ full.dittao,  "value": full.dittao+"#;#"+full.nomimp+"#;#"+full.idanonimo, "disabled": full.anonimaDis});
						_div.append(_check);
						return _div.html();
					}
				},
				{	
					"data": "nomimp",
					"visible": true,
					"sTitle": _entita=="TECNI"? "Intestazione":"Ragione sociale",
					"searchable": true,
					"targets": [ 4 ],
					"defaultContent": ""
				},
{	
					"data": "invgar",
					"visible": !isGenere10or20 && _entita==='DITG',
					"sTitle": "Ditta invitata?",
					"searchable": true,
					"targets": [ 5 ],
					"defaultContent": ""
				},
{	
					"data": "invoff",
					"visible": !isGenere10or20 && _entita==='DITG',
					"sTitle": "Inviato offerta?",
					"searchable": true,
					"targets": [ 6 ],
					"defaultContent": ""
				},
{	
					"data": "ammgar",
					"visible": !isGenere10or20 && _entita==='DITG',
					"sTitle": "Ditta esclusa?",
					"searchable": true,
					"targets": [ 7 ],
					"defaultContent": ""
				},
{	
					"data": "fasgar",
					"visible": !isGenere10or20 && _entita==='DITG',
					"sTitle": "Fase di esclusione",
					"searchable": true,
					"targets": [ 8 ],
					"defaultContent": ""
				},
				{	
					"data": "abilitaz",
					"visible": isGenere10or20 && _entita==='DITG',
					"sTitle": "Abilitazione",
					"searchable": true,
					"targets": [ 9 ],
					"defaultContent": ""
				},
{	
					"data": "cfimp",
					"visible": _entita!=='DITG',
					"sTitle": "Codice fiscale",
					"searchable": true,
					"targets": [ 10 ],
					"defaultContent": ""
				},
{	
					"data": "pivimp",
					"visible": _entita!=='DITG',
					"sTitle": "Partita I.V.A.",
					"searchable": true,
					"targets": [ 11 ],
					"defaultContent": ""
				},
{	
					"data": "locimp",
					"visible": _entita==='IMPR',
					"sTitle": "Comune",
					"searchable": true,
					"targets": [ 12 ],
					"defaultContent": ""
				},
				{	
					"data": "email",
					"visible": _entita!=='DITG',
					"sTitle": "Email",
					"searchable": false,
					"targets": [ 13 ],
					"defaultContent": ""
				},
{	
					"data": "pec",
					"visible": _entita!=='DITG',
					"sTitle": "PEC",
					"searchable": false,
					"targets": [ 14 ],
					"defaultContent": ""
				},
	        ],
	        "language": {
				"sEmptyTable":     "Nessun destinatario trovato",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ destinatari",
				"sInfoEmpty":      "Nessun destinatario trovato",
				"sInfoFiltered":   "(su _MAX_ destinatari)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca destinatari",
				"sZeroRecords":    "Nessun destinatario trovato",
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
					$("#destinatari tfoot").hide();
					$("#destinatari_info").hide();
					$("#destinatari_paginate").hide();
				}
			},
			
	        "pagingType": "full_numbers",
	        "lengthMenu": [[50, 70, 100], ["50 destinatari", "70 destinatari", "100 destinatari"]],
	        "ordering": false,
	        "aoColumns": [
			     { "bSortable": false, "bSearchable": false },
			     { "bSortable": false, "bSearchable": false },
			     { "bSortable": false, "bSearchable": false },
			     { "bSortable": false, "bSearchable": false },
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
				 { "bSortable": true, "bSearchable": true }
			   ]
	    });
			var columnNo = 1;
			if((!isAbilitatoInvioMailDocumentale || descc) && !isComunicazioneAnonima){
				columnNo+=1;
				if(isInvioFax)
					column+=1;
			}
			if(_entita=='DITG'){
				$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca ragione sociale"/>' );
				if(!isGenere10or20){
					$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca"/>' );
					$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca"/>' );
					$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca"/>' );
					$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca fase"/>' );
				}else{
					$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca abilitazione"/>' );
				}
			}if(_entita=='IMPR'){
				$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca ragione sociale"/>' );
				$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca C.F."/>' );
				$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca P.IVA"/>' );
				$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca comune"/>' );
				$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca email"/>' );
				$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca PEC"/>' );
			}if(_entita=='TECNI'){
				$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca intestazione"/>' );
				$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca C.F"/>' );
				$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca P.IVA"/>' );
				$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca email"/>' );
				$('#destinatari tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca PEC"/>' );
			}
			
			
			
			_tableDestinatari.columns().eq(0).each( function (colIdx) {
				$('input', _tableDestinatari.column(colIdx).footer()).on( 'keyup change', function () {
					_tableDestinatari.column(colIdx).search(this.value).draw();
				});
		    });

		
		for(colonna in _colonne){
		var _center = $("<center/>");
		var _href = "<a href='javascript:showMenuSelezione("+colonna+");' Title='Seleziona tutti'> <img src='"+_contextPath+"/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>";
		var _href = _href + "&nbsp;";
		var _href = _href + "<a href='javascript:showMenuDeselezione("+colonna+");' Title='Deseleziona tutti'><img src='"+_contextPath+"/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>";
		
		var _href = _href + '<div id="popmenuCheck'+_colonne[colonna]+'" class="popupmenuskin" style="visibility: hidden;text-align: left"><div class="chiusuramenu"><a href="javascript:hideMenuSelezione('+colonna+');"><img src="'+_contextPath+'/img/chiudi.gif" height="8" width="8" alt="Chiudi" title="Chiudi"></a></div><div class="menuitems" id=""><a href="javascript:_selezionaTutti('+colonna+');" tabindex="0">&nbsp;Seleziona tutti su pagina corrente</a></div><div class="menuitems" id=""><a href="javascript:_archiviaTutto('+colonna+');" tabindex="1">&nbsp;Seleziona su tutte le pagine</a></div></div>'
		var _href = _href + "&nbsp;";
		var _href = _href + '<div id="popmenuUncheck'+_colonne[colonna]+'" class="popupmenuskin" style="visibility: hidden;text-align: left"><div class="chiusuramenu"><a href="javascript:hideMenuDeselezione('+colonna+');"><img src="'+_contextPath+'/img/chiudi.gif" height="8" width="8" alt="Chiudi" title="Chiudi"></a></div><div class="menuitems" id=""><a href="javascript:_deselezionaTutti('+colonna+');" tabindex="0">&nbsp;Deseleziona tutti su pag. corrente</a></div><div class="menuitems" id=""><a href="javascript:_annullaArchiviaTutto('+colonna+');" tabindex="1">&nbsp;Deseleziona su tutte le pagine</a></div></div>'
		
		_center.append(_href);
		_center.appendTo($('#'+_colonne[colonna]));
		
			$('#popmenuCheck'+ _colonne[colonna]+' div a').mouseover(function() {
				$(this).parent('.menuitems').attr('id','mouseoverpopup');
			}).mouseout(function() {
				$(this).parent('.menuitems').attr('id','');
			})
			$('#popmenuUncheck'+ _colonne[colonna]+' div a').mouseover(function() {
				$(this).parent('.menuitems').attr('id','mouseoverpopup');
			}).mouseout(function() {
				$(this).parent('.menuitems').attr('id','');
			})
		}
		
		$(window).click(function(e) {
		  for(colonna in _colonne){
			  if(!document.getElementById('popmenuCheck'+ _colonne[colonna])?.contains(e.target) && $('#popmenuCheck'+ _colonne[colonna]).css("visibility") == "visible" ) {
				  hideMenuSelezione(colonna);
			  }
			  if(!document.getElementById('popmenuUncheck'+ _colonne[colonna])?.contains(e.target) && $('#popmenuUncheck'+ _colonne[colonna]).css("visibility") == "visible" ) {
				  hideMenuDeselezione(colonna);
			  }
		  }
		})
		
		$("#destinatari_filter").hide();

		
	}
})


	function showMenuSelezione(colonna){
		hideMenuDeselezione(colonna);
		$('#popmenuCheck'+_colonne[colonna]).css('visibility','visible');
	}
	function showMenuDeselezione(colonna){
		hideMenuSelezione(colonna);
		$('#popmenuUncheck'+_colonne[colonna]).css('visibility','visible');
	}
	
	function hideMenuSelezione(colonna){
		$('#popmenuCheck'+_colonne[colonna]).css('visibility','hidden');
	}
	function hideMenuDeselezione(colonna){
		$('#popmenuUncheck'+_colonne[colonna]).css('visibility','hidden');
	}
	
	
	function _selezionaTutti(colonna) {
		hideMenuSelezione(colonna);
	    $("input[id^='"+_colonne[colonna]+"_']:not(:disabled)").prop("checked","checked");
	}

	function _deselezionaTutti(colonna) {
		hideMenuDeselezione(colonna);
		$("input[id^='"+_colonne[colonna]+"_']").prop( "checked",false);
	  }
	  
	function _archiviaTutto(colonna) {
		var tableResultXpage= $('#destinatari').DataTable().page.info()?.length;
		var page= $('#destinatari').DataTable().page();
		$('#destinatari').DataTable().page.len(-1).draw();
		_selezionaTutti(colonna);
		$('#destinatari').DataTable().page.len(tableResultXpage)?.draw();
		$('#destinatari').DataTable().page(page).draw(false);
	}
		
	function _annullaArchiviaTutto(colonna) {
		var tableResultXpage= $('#destinatari').DataTable().page.info()?.length;
		var page= $('#destinatari').DataTable().page();
		$('#destinatari').DataTable().page.len(-1).draw();
		_deselezionaTutti(colonna);
		$('#destinatari').DataTable().page.len(tableResultXpage)?.draw();
		$('#destinatari').DataTable().page(page).draw(false);
	}


  
  

