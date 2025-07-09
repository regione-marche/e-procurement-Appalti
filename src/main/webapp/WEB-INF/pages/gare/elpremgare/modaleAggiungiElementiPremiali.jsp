<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/dataTable/dataTable/jquery.dataTables.css" >

<div id="mascheraAggiungiElementiPremiali">
	<div id="sezioneIniziale">
		<div id="elementiContainer"></div>
	</div>
	<div id="sezioneFinale">
		<div id="sezioneErrore">
			<b>Errore durante l'inserimento degli elementi:</b><br><br>
			<span id="messaggioErrore">
				Errore inaspettato, consultare il log per i dettagli.
			</span>
		</div>
		<div id="sezioneSuccesso">
			Elementi aggiunti con successo
		</div>
	</div>
</div>

<style>
        #mascheraAggiungiElementiPremiali {
            display: none;
            padding: 20px;
            font-size: 11px;
            font: 11px Verdana, Arial, Helvetica, sans-serif;
        }

	TABLE.schedagperm {
		margin-top: 5px;
		margin-bottom: 5px;
		padding: 0px;
		font-size: 11px;
		border-collapse: collapse;
		border-left: 1px solid #A0AABA;
		border-top: 1px solid #A0AABA;
		border-right: 1px solid #A0AABA;
		width: 100%;
	}
	
	TABLE.schedagperm TR.intestazione {
		background-color: #CCE0FF;
		border-bottom: 1px solid #A0AABA;
	}
	
	TABLE.schedagperm TR.intestazione TD, TABLE.schedagperm TR.intestazione TH {
		padding: 5 2 5 2;
		text-align: center;
		font-weight: bold;
		border-left: 1px solid #A0AABA;
		border-right: 1px solid #A0AABA;
		border-top: 1px solid #A0AABA;
		border-bottom: 1px solid #A0AABA;
		height: 30px;
	}
	
	TABLE.schedagperm TR.sezione {
		background-color: #EFEFEF;
		border-bottom: 1px solid #A0AABA;
	}
	
	TABLE.schedagperm TR.sezione TD, TABLE.schedagperm TR.sezione TH {
		padding: 5 2 5 2;
		text-align: left;
		font-weight: bold;
		height: 25px;
	}
	
	TABLE.schedagperm TR {
		background-color: #FFFFFF;
	}
	
	TABLE.schedagperm TR TD {
		padding-left: 3px;
		padding-top: 1px;
		padding-bottom: 1px;
		padding-right: 3px;
		text-align: left;
		border-left: 1px solid #A0AABA;
		border-right: 1px solid #A0AABA;
		border-top: 1px solid #A0AABA;
		border-bottom: 1px solid #A0AABA;
		height: 25px;
		font: 11px Verdana, Arial, Helvetica, sans-serif;
	}
	
	TABLE.schedagperm TR.intestazione TH.codice, TABLE.schedagperm TR TD.codice {
		width: 20px;
	}
	
	TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.codfisc {
		width: 100px;
	}
	
	TABLE.schedagperm TR.intestazione TH.descr, TABLE.schedagperm TR TD.descr {
		word-break:break-all;
		width: 200px;
	}
	
	TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.datadescr {
		word-break:break-word;
		text-align: center;
	}
	TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.stato {
		width: 100px;
		text-align: center;
	}
	TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.ck {
		width: 50px;
		text-align: center;
	}
	
	
	img.img_titolo {
		padding-left: 8px;
		padding-right: 8px;
		width: 24px;
		height: 24px;
		vertical-align: middle;
	}
	
	.dataTables_length, .dataTables_filter {
		padding-bottom: 5px;
	}
	
	.dataTables_empty {
		padding-top: 6px;
	}
		
	div.tooltip {
		width: 300px;
		margin-top: 3px;
		margin-bottom:3px;
		border: 1px solid #A0AABA;
		padding: 10px;
		display: none;
		position: absolute;
		z-index: 1000;
		background-color: #F4F4F4;
	}
	
	
	input.search {
		height: 16px;
		font: 11px Verdana, Arial, Helvetica, sans-serif;
		background-color: #FFFFFF;
		color: #000000;
		vertical-align: middle;
		border: 1px #366A9B solid;
		width: 98%;
		font-style: italic;
	}
	
	#add {
	  float: right;
	}
	
	#add label select{
		width:105px;
	}

</style>

<gene:javaScript>

var _contextPath="${pageContext.request.contextPath}";
var widthM= 1000;
var	heightM= 600;

function apriModaleAggiungiElementiPremiali() {

	pingSessioneAttiva();
	popolaTabella('${param.elencoe}','${param.ngara}');

	var titolo="Aggiungi elementi premiali";

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
					aggiungiElementi();
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
	$("#mascheraAggiungiElementiPremiali").dialog(opt).dialog("open");
	$("#mascheraAggiungiElementiPremiali").parent().css("position","fixed");
	$("#botChiudi, #sezioneFinale, #sezioneErrore, #sezioneSuccesso").hide();

	impostaClasseMaggiore();
}

    function creaTabella() {

        var headerRow = $('<tr/>', {"class": "intestazione"});
        var selChecks = $('<th/>', {"id": "selChecks","class": "ck"});
        headerRow.append(selChecks);
        headerRow.append('<th/>');
        headerRow.append('<th/>');

        var thead = $('<thead>');
        thead.append(headerRow);

        var tbody = $('<tbody>');

        var _tr2 = $('<tr/>', {"class": "intestazione"});
        _tr2.append('<td/>', {"class": "codice"});
        _tr2.append('<td/>');
        _tr2.append('<td/>');

        var _tfoot = $('<tfoot/>');
        _tfoot.append(_tr2);

        var tabella = $('<table>', {"id": "elementi", "class": "schedagperm", "cellspacing": "0"});
        tabella.append(thead);
        tabella.append(tbody);
        tabella.append(_tfoot);

        $('#elementiContainer').append(tabella);
    }

    var regexBase = '';

    function popolaTabella(elencoe,ngara) {
        $('#elementiContainer').empty();
        creaTabella();
		_tableElementi = $('#elementi').removeAttr('width').DataTable({

			"ajax": {
				"url": "${contextPath}/pg/GetListaElementiPremiali.do",
				"async": false,
				"data": function() {
					return {
						elencoe: elencoe,
						ngara: ngara
					};
				}
			},
			searching: true,
			"ordering": false,
			"bAutoWidth": false,
			"columnDefs": [
				{
					"visible": true,
					"sTitle": "",
					"targets": [ 0 ],
					"width": "5%",
					"sClass": "datadescr",
					"render": function ( data, type, full, meta ) {
						var _div = $('<div/>');
						var _check = $("<input/>",{"type":"checkbox", "name": "checkboxSelezioneElemento", "value": full.IDQUESITO.value});
						_div.append(_check);
						return _div.html();
					}
				},
				{
					"searchable": true,
					"data": "TITOLO.value",
					"visible": true,
					"sTitle": "Titolo",
					"width": "60%",
					"sClass": "descr",
					"targets": [ 1 ],
					"render": function ( data, type, full, meta ) {
						let _div = $('<div/>');
						_div.append($("<span/>", {"title":full.DESCQUESITO.value, "text": full.TITOLO.value}));
						return _div.html();
					}
				},
				{
					"searchable": true,
					"visible": true,
					"sTitle": "Classe",
					"targets": [ 2 ],
					"width": "25%",
					"class" : "descr",
					"render": function ( data, type, full, meta ) {
						let _div = $("<div/>");
						if (full.LISTAOPZIONI != undefined) {
							let _select = $('<select/>',{"id": "selectOpzione_" + full.IDQUESITO.value, "name": "classe", "style":"width:100%"});
							(full.LISTAOPZIONI).forEach(function(currentValue, index, arr) {
								let option = $("<option />",{"value": currentValue[1], "text": currentValue[2]});
								_select.append(option);
							});
							_div.append(_select);
						}
						return _div.html();
					}
				}
			],
			"aoColumns": [
				{ "bSortable": false, "bSearchable": false },
				{ "bSortable": false, "bSearchable": true },
				{ "bSortable": false, "bSearchable": false }
			],
			language: {
				"sEmptyTable":     "Non ci sono elementi da aggiungere",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ elementi",
				"sInfoEmpty":      "Non ci sono elementi da aggiungere",
				"sInfoFiltered":   "",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca elementi",
				"sZeroRecords":    "Non ci sono elementi da aggiungere",
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
					$("#elementi tfoot").hide();
					$("#elementi_info").hide();
					$("#elementi_paginate").hide();
				}
			},
			"pagingType": "full_numbers",
			"lengthMenu": [[10, 25, -1], ["10 elementi", "25 elementi", "Tutti gli elementi"]],
			"dom" : 'l<"#add">frtip'
		});
		let _center = $("<center/>");
		let _href = "<a href='javascript:showMenuSelezione();' Title='Seleziona tutti'> <img src='"+_contextPath+"/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>";
		_href = _href + "&nbsp;";
		_href = _href + "<a href='javascript:showMenuDeselezione();' Title='Deseleziona tutti'><img src='"+_contextPath+"/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>";

		_href = _href + '<div id="popmenuCheck" class="popupmenuskin" style="visibility: hidden;text-align: left;"><div class="chiusuramenu"><a href="javascript:hideMenuSelezione();"><img src="'+_contextPath+'/img/chiudi.gif" height="8" width="8" alt="Chiudi" title="Chiudi"></a></div><div class="menuitems" id=""><a href="javascript:_selezionaTutti();" tabindex="0">&nbsp;Seleziona tutti su pagina corrente</a></div><div class="menuitems" id=""><a href="javascript:_selezioneTuttePagine();" tabindex="1">&nbsp;Seleziona su tutte le pagine</a></div></div>'
		_href = _href + "&nbsp;";
		_href = _href + '<div id="popmenuUncheck" class="popupmenuskin" style="visibility: hidden;text-align: left;"><div class="chiusuramenu"><a href="javascript:hideMenuDeselezione();"><img src="'+_contextPath+'/img/chiudi.gif" height="8" width="8" alt="Chiudi" title="Chiudi"></a></div><div class="menuitems" id=""><a href="javascript:_deselezionaTutti();" tabindex="0">&nbsp;Deseleziona tutti su pag corrente</a></div><div class="menuitems" id=""><a href="javascript:_deselezionaTuttePagine();" tabindex="1">&nbsp;Deseleziona su tutte le pagine</a></div></div>'
		_center.append(_href);
		_center.appendTo($("#selChecks"));

		let tableResultXpage= $('#elementi').DataTable().page.info()?.length;
		let page= $('#elementi').DataTable().page();
		$('#elementi').DataTable().page.len(-1).draw();
		$('#elementi').DataTable().page.len(tableResultXpage)?.draw();
		$('#elementi').DataTable().page(page).draw(false);

		let columnNo = 1;
		$('#elementi tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca titolo "/>' );
		//$('#elementi tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca descrizione"/>' );

		_tableElementi.columns().eq(0).each( function (colIdx) {
			$('input', _tableElementi.column(colIdx).footer()).on( 'keyup change', function () {
				let escapedValue = this.value.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&');
				if (colIdx != 1) {
					_tableElementi.column(colIdx).search(escapedValue).draw();
				}
				else if (colIdx == 1) {
					if (this.value == '') {
						_tableElementi.column(1).search(regexBase,true,false).draw();
					} else {
						let regex = `(?=.*` + escapedValue + `)`;
						_tableElementi.column(1).search(regex,true,false).draw();
					}
				}
			});
		});

		$("#elementi_filter").hide();
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
		$("input[name='checkboxSelezioneElemento']").prop("checked","checked");
	}

	function _deselezionaTutti() {
		hideMenuDeselezione();
		$( "input[name='checkboxSelezioneElemento']" ).each( function( index ) {
			if ($( this ).prop( "disabled")) {
				;
			}else{
				$( this ).prop( "checked",false)
			}
		});
	}

	function _selezioneTuttePagine() {
		var tableResultXpage= $('#elementi').DataTable().page.info()?.length;
		var page= $('#elementi').DataTable().page();
		$('#elementi').DataTable().page.len(-1).draw();
		_selezionaTutti();
		$('#elementi').DataTable().page.len(tableResultXpage)?.draw();
		$('#elementi').DataTable().page(page).draw(false);
	}

	function _deselezionaTuttePagine() {
		var tableResultXpage= $('#elementi').DataTable().page.info()?.length;
		var page= $('#elementi').DataTable().page();
		$('#elementi').DataTable().page.len(-1).draw();
		_deselezionaTutti();
		$('#elementi').DataTable().page.len(tableResultXpage)?.draw();
		$('#elementi').DataTable().page(page).draw(false);
	}

	function impostaClasseMaggiore() {
		var tableResultXpage= $('#elementi').DataTable().page.info()?.length;
		var page= $('#elementi').DataTable().page();
		$('#elementi').DataTable().page.len(-1).draw();
		selezionaUltimoDropdown()
		$('#elementi').DataTable().page.len(tableResultXpage)?.draw();
		$('#elementi').DataTable().page(page).draw(false);
	}

	function selezionaUltimoDropdown() {
		$("select[id^='selectOpzione_']").each(function(){
			$(this).val($(this).children().last().val());
		});
	}

	function prendiSelezionati(elementiSelezionati, classiSelezionate) {
		$( "input[name='checkboxSelezioneElemento']:checked").each( function( index ) {
			let idElemento = $(this).val();
			let classeSelezionata = $('#selectOpzione_'+idElemento).val();
			elementiSelezionati.push(idElemento);
			classiSelezionate.push(classeSelezionata);
		});
	}

	function aggiungiElementi() {

		let elementiSelezionati = [];
		let classiSelezionate = [];

		let tableResultXpage= $('#elementi').DataTable().page.info()?.length;
		let page= $('#elementi').DataTable().page();
		$('#elementi').DataTable().page.len(-1).draw();
		prendiSelezionati(elementiSelezionati, classiSelezionate);
		$('#elementi').DataTable().page.len(tableResultXpage)?.draw();
		$('#elementi').DataTable().page(page).draw(false);

		console.log(elementiSelezionati);
		console.log(classiSelezionate);
	
		if (elementiSelezionati.length === 0) {
			alert('Non è stato selezionato nessun elemento premiale.');
			return;
		}

		var esito = '';
		var msgEsito = '';
		_waitgperm();
		$.ajax({
			type: "POST",
			dataType: "json",
			data: {
				ngara : '${param.ngara}',
				elementiSelezionati: elementiSelezionati,
				classiSelezionate: classiSelezionate
			},
			async: false,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: "${contextPath}/pg/AggiungiElementiPremiali.do",
			error: function(){
				$('#sezioneFinale, #sezioneErrore').show;
			},
			success: function(res){
				esito=res.Esito;
				msgEsito = res.MsgErrore;
				$('#messaggioErrore').empty().text(msgEsito);
			},
			complete: function(jqXHR, textStatus) {
				$("#botConferma, #botAnnulla, #sezioneIniziale").hide();
				$("#botChiudi, #sezioneFinale").show();
				if(textStatus=="error" || textStatus=="parsererror"){
					$("#sezioneErrore").show();
				}else{
					if(esito!="Errore"){
						historyReload();
					} else {
						$("#sezioneErrore").show();
					}
				}
				_nowaitgperm();
			}
		});
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
</gene:javaScript>
