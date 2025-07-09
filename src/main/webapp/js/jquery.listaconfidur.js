$(window).on("load", function (){
	
	var _tabellaG1CONFIDUR = null;
	var modoapertura = $("#modoapertura").val();
	var filtri = $("#filtri").val();
	var paginazione = $("#paginazione").val();
	var pagina = $("#pagina").val();
	
	_popolaTabellaG1CONFIDUR();
	
	$.fn.dataTable.ext.order['dom-checkbox'] = function (settings, col)
	{
		return this.api().column(col, {order:'index'}).nodes().map(function (td, i) {
			return $('input',td).prop('checked') ? '1' : '0';
		} );
	};
	
	/*
	 * Funzione di attesa
	 */
	function _wait() {
		document.getElementById('bloccaScreen').style.visibility='visible';
		$('#bloccaScreen').css("width",$(document).width());
		$('#bloccaScreen').css("height",$(document).height());
		document.getElementById('wait').style.visibility='visible';
		$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
	}

	/*
	 * Nasconde l'immagine di attesa
	 */
	function _nowait() {
		document.getElementById('bloccaScreen').style.visibility='hidden';
		document.getElementById('wait').style.visibility='hidden';
	}
	
	/*
	 * Crea tabella contenitore
	 */
	function _creaTabellaG1CONFIDUR() {
		
		var _table = $('<table/>', {"id": "tabellaG1CONFIDUR", "class": "scheda", "cellspacing": "0", "width" : "100%"});
		var _thead = $('<thead/>');
		var _tr1 = $('<tr/>', {"class": "intestazione"});
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_thead.append(_tr1);
		_table.append(_thead);
		
		var _tbody = $('<tbody/>');
		
		var _tr2 = $('<tr/>', {"class": "intestazione"});
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
		
		$("#tabellaG1CONFIDURContainer").append(_table);
	}
	
	/*
	 * Popola tabella
	 */
	function _popolaTabellaG1CONFIDUR() {
		
		_wait();
		
		if (_tabellaG1CONFIDUR != null) {
			_tabellaG1CONFIDUR.destroy(true);
		}		
		
		_creaTabellaG1CONFIDUR();
		_tabellaG1CONFIDUR = $('#tabellaG1CONFIDUR').DataTable( {
			"ajax": {
				url: "pg/GestisciListaConfidur.do?metodo=getLista",
				dataSrc: "listaConfidur",
				async: true,
				dataType: "json",
				complete: function(e){
					_nowait();
				}
			},
			"columnDefs": [
				
				{	
					"data": "tab1tipA2044",
					"visible": true,
					"searchable": true,
					"targets": [ 0 ],
					"sTitle": "Codice tipo procedura",
					"sWidth": "10px",
					"className": "dt-body-right"
				},
				{	
					"data": "tab1descA2044",
					"visible": true,
					"searchable": true,
					"targets": [ 1 ],
					"sTitle": "Tipo di procedura",
					"sWidth": "340px"
				},
				{	
					"data": "tab1tipA2081",
					"visible": false,
					"searchable": true,
					"targets": [ 2 ],
					"sTitle": "tab1tipA2081",
					"sWidth": "10px"
				},
				{	
					"data": "tab1descA2081",
					"visible": true,
					"searchable": true,
					"targets": [ 3 ],
					"sTitle": "Criterio di aggiudicazione",
					"sWidth": "340px"
					
				},
				{	
					"data": "durata",
					"visible": true,
					"searchable": true,
					"targets": [ 4 ],
					"sTitle": "Durata (mesi)",
					"sWidth": "50px",
					"className": "dt-body-center",
					"defaultContent": "-",
					"render": function ( data, type, full, meta ) {
						var _div = $("<div/>");
						if (modoapertura != 'MODIFICA') {
							return data;
						} else {
							var _durata = $("<input/>",{"type":"number","value":full.durata,"id": "durata_" + full.tab1tipA2044 + "_" + full.tab1tipA2081})
							_durata.text(data);
							_durata.css("width","100%");
							_durata.css("font","11px Verdana, Arial, Helvetica, sans-serif");
							_div.append(_durata);
							var _isChanged = $("<input/>",{"type":"hidden","value":"false","id": "isChanged_" + full.tab1tipA2044 + "_" + full.tab1tipA2081});
							_div.append(_isChanged);
							return _div.html();
						} 
						
					}
					
					
				}
	        ],
	        "language": {
				"sEmptyTable":     "Nessuna riga trovata",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ righe",
				"sInfoEmpty":      "Nessuna riga trovata",
				"sInfoFiltered":   "(su _MAX_ righe totali)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca",
				"sZeroRecords":    "Nessuna riga trovata",
				"oPaginate": {
					"sFirst":      "Prima",
					"sPrevious":   "Precedente",
					"sNext":       "Successiva",
					"sLast":       "Ultima"
				}
			},
			"lengthMenu": [[50, 70, 100,], ["50 righe", "70 righe", "100 righe"]],
	        "pagingType": "full_numbers",
	        "aoColumns": [
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true }
			   ],
			"displayStart": paginazione && pagina ? paginazione * pagina : 0, 
			"rowCallback": function (row, data) {
				$(row).on("change", 'input[id^=durata_]', function () {
					$('#isChanged_'+ data.tab1tipA2044 + "_" + data.tab1tipA2081).val('true');
				});                      
			}
	    });
		
		
		$('#tabellaG1CONFIDUR tfoot td').eq(1).html( '<input class="search" style="width: 100%" size="20" type="text" placeholder="Ricerca tipologia"/>' );
		$('#tabellaG1CONFIDUR tfoot td').eq(2).html( '<input class="search" style="width: 100%" size="20" type="text" placeholder="Ricerca criterio"/>' );

		_tabellaG1CONFIDUR.columns().eq(0).each( function (colIdx) {
			$('input', _tabellaG1CONFIDUR.column(colIdx).footer()).on( 'keyup change', function () {
				_tabellaG1CONFIDUR.column(colIdx).search(this.value).draw();
			});
	    });
		
		$("#tabellaG1CONFIDUR_filter").hide();
		
		if(filtri)
		$('#tabellaG1CONFIDUR').DataTable().columns().eq(0).each( function (colIdx) {
			var valoreFiltro = filtri.split(",")[colIdx];
					$('#tabellaG1CONFIDUR').DataTable().column(colIdx).search(valoreFiltro); //ripristino i filtri impostati
					$('input',$('#tabellaG1CONFIDUR').DataTable().column(colIdx).footer()).val(valoreFiltro);
		})
		if(paginazione)
			$('#tabellaG1CONFIDUR').DataTable().page.len(paginazione)?.draw(); //ripristino il numero di occorrenze per pagina
		
	}
	
	$('#menumodificadurata, #pulsantemodificadurata').on("click",function() {
		var backupSearch = [];
		$('#tabellaG1CONFIDUR').DataTable().columns().eq(0).each( function (colIdx) {
				backupSearch.push($('#tabellaG1CONFIDUR').DataTable().column(colIdx).search()); //salvo i filtri impostati
		})
		formG1confidur.filtri.value=backupSearch;
		formG1confidur.paginazione.value=$('#tabellaG1CONFIDUR').DataTable().page.info()?.length;
		formG1confidur.pagina.value=$('#tabellaG1CONFIDUR').DataTable().page.info()?.page;
		formG1confidur.modoapertura.value='MODIFICA';
		formG1confidur.submit();
    }); 
	
	$('#menusalvamodifichedurata, #pulsantesalvamodifichedurata').on("click",function() {
		_wait();
		setTimeout(function(){
			_salvaG1CONFIDUR();
		}
		, 500);
    });
	
	$('#menuannullamodifichedurata, #pulsanteannullamodifichedurata').on("click",function() {
		var backupSearch = [];
		$('#tabellaG1CONFIDUR').DataTable().columns().eq(0).each( function (colIdx) {
				backupSearch.push($('#tabellaG1CONFIDUR').DataTable().column(colIdx).search()); //salvo i filtri impostati
		})
		formG1confidur.filtri.value=backupSearch;
		formG1confidur.paginazione.value=$('#tabellaG1CONFIDUR').DataTable().page.info()?.length;
		formG1confidur.pagina.value=$('#tabellaG1CONFIDUR').DataTable().page.info()?.page;
		formG1confidur.modoapertura.value='';
		formG1confidur.submit();
    });
	
	function _salvaG1CONFIDUR() {
		_tabellaG1CONFIDUR.$("tr").each(function () {
			var row = $(this).find('input[id^=durata_]').get(0);
			var durata = row.value;
			var tab1tipA2044 = row.id.split("_")[1];
			var tab1tipA2081 = row.id.split("_")[2];
			var isChanged = $(this).find('input[id^=isChanged_]').get(0).value;
			if(isChanged==='true'){
				if (durata ) {
					$.ajax({
					  "async": false,
					  type: "POST",
					  "url": "pg/GestisciListaConfidur.do?metodo=setLista",
					  "data": {
						  "operation": "INSERTUPDATE",
						  "tipgar": tab1tipA2044,
						  "critlic": tab1tipA2081,
						  "durata": durata
					  }
					});
				} else {
					$.ajax({
					  "async": false,
					  type: "POST",
					  "url": "pg/GestisciListaConfidur.do?metodo=setLista",
					  "data": {
						  "operation": "DELETE",
						  "tipgar": tab1tipA2044,
						  "critlic": tab1tipA2081,
						  "durata": 0
					  }
					});
					
				}
			}
		});
		var backupSearch = [];
		$('#tabellaG1CONFIDUR').DataTable().columns().eq(0).each( function (colIdx) {
				backupSearch.push($('#tabellaG1CONFIDUR').DataTable().column(colIdx).search()); //salvo i filtri impostati
		})
		formG1confidur.filtri.value=backupSearch;
		formG1confidur.paginazione.value=$('#tabellaG1CONFIDUR').DataTable().page.info()?.length;
		formG1confidur.pagina.value=$('#tabellaG1CONFIDUR').DataTable().page.info()?.page;
		formG1confidur.modoapertura.value='';
		formG1confidur.submit();
    };	
	
});
