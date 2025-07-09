<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, param.ngara)}' />
<c:set var="genere" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetGenereGaraFunction", pageContext, codgar)}' />

<c:set var="nonusato" value='${gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.GetListaDocumentiAggiuntiDOCUMATTI", pageContext, param.ngara, param.dittao)}' />
<c:set var="codiga" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select codiga from gare where ngara = ?", param.ngara)}' />

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/dataTable/dataTable/jquery.dataTables.css" >

<div id="mascheraCopiaDocumenti">
	<div id="sezioneIniziale">
		<div id="documentiContainer"></div>
	</div>
	<div id="sezioneFinale">
		<div id="sezioneErrore">
			<b>Errore durante la copia dei documenti:</b><br><br>
			<span id="messaggioErrore">
				Errore inaspettato, consultare il log per i dettagli.
			</span>
		</div>
		<div id="sezioneSuccesso">
			Documenti copiati con successo
		</div>
	</div>
</div>

<style>
        #mascheraCopiaDocumenti {
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
var widthM= 1300;
var	heightM= 650;

function apriModaleCopiaDocumenti() {

	pingSessioneAttiva();
	popolaTabella('${codgar}');

	var titolo="Copia documenti da archivio";

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

function popolaTabella(codgar) {
	$('#documentiContainer').empty();
	creaTabella();

	let dittao = '${param.dittao}';
	if (codgar === '${codgar}' && ${not empty codgarSecondoGrado} && ${not empty dittaoPrimoGrado}) {
		dittao = '${dittaoPrimoGrado}';
	}

	_tableDocumenti = $('#documenti').removeAttr('width').DataTable({

		"ajax": {
			"url": "pg/GetListaDocumenti.do",
			"async": false,
			"data": function (n) {
				return {
				    operation: 'VISUALIZZA',
				    codgar: codgar,
					genere: '${genere}',
					dittao: dittao,
					isDocumentiGara: '${empty param.dittao}'
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
				"width": "4%",
				"sClass": "datadescr",
				"render": function ( data, type, full, meta ) {
					var _div = $("<div/>");
					var _check = $("<input/>",{"type":"checkbox", "name": "documentiSelezionati", "value": full.IDPRG.value + full.IDDOCDIG.value +(full.PROVENIENZA.value==4? 'filesystem':'')});
					_div.append(_check);
					return _div.html();
				}
			},
			{
				"searchable": true,
				"data": "GRUPPO.value",
				"visible": true,
				"sTitle": "Tipologia",
				"width": "20%",
				"sClass": "descr",
				"targets": [ 1 ],
				"render": function ( data, type, full, meta ) {
					var TipoDocDesc = "";
					switch(full.GRUPPO.value) {
						case 1: TipoDocDesc = "Documento del bando/avviso"; break;
						case 2:
							if($("#genere").val()=='10' || $("#genere").val()=='20'){
								TipoDocDesc = "Requisiti degli operatori"; break;
							}else{
								TipoDocDesc = "Requisiti dei concorrenti"; break;
							}
						case 3: 
							if($("#genere").val()=='10' || $("#genere").val()=='20'){
								TipoDocDesc = "Fac-simile documento richiesto agli operatori"; break;
							}else{
								TipoDocDesc = "Fac-simile documento richiesto ai concorrenti"; break;
							}
						case 4: TipoDocDesc = "Documento dell'esito"; break;
						case 5: TipoDocDesc = "Documento per la trasparenza"; break;
						case 6 : TipoDocDesc = "Documento dell'invito a presentare offerta"; break;
						case 10: TipoDocDesc = "Atto o documento"; break;
						case 11: TipoDocDesc = "Documento allegato all'ordine di acquisto"; break;
						case 12: TipoDocDesc = "Documento dell'invito all'asta elettronica"; break;
						case 15: TipoDocDesc = "Delibera a contrarre o atto equivalente"; break;
						default:
							TipoDocDesc = full.GRUPPO.value; break;
					}
					if (full.ARCHIVIATO != null && full.ARCHIVIATO.value == "1" ){
						TipoDocDesc = TipoDocDesc + "\r\n(ARCHIVIATO)";
					}else if (full.ARCHIVIATO != null && full.ARCHIVIATO.value == "3" ){
						TipoDocDesc = TipoDocDesc + "\r\n(ANNULLATO DA OPERATORE)";
					}
					return TipoDocDesc;
				}
			},
			{
				"searchable": true,
				"data": "DIGNOMDOC.value",
				"visible": true,
				"sTitle": "Nome documento",
				"targets": [ 2 ],
				"width": "30%",
				"class" : "descr"
			},
			{
				"searchable": true,
				"data": "DESCRIZIONE.value",
				"visible": true,
				"sTitle": "Descrizione",
				"targets": [ 3 ],
				"width": "30%",
				"render": function ( data, type, full, meta ) {
						var customId = full.IDPRG.value + full.IDDOCDIG.value +(full.PROVENIENZA.value==4? 'filesystem':'') + "_descrizione";
						return $('<div/>', { "id": customId }).text(data)[0].outerHTML;
					},
				"class" : "descr"
			},
			{
				"searchable": true,
				"data": "DATA.value",
				"visible": true,
				"sTitle": "Data",
				"targets": [ 4 ],
				"width": "11%",
				"class" : "datadescr"
			},
			{
				"data": "DITTA.value",
				"visible": false,
				"sTitle": "Ditta",
				"targets": [ 5 ],
				"width": "10%",
				"class" : "descr"
			},
			{
				"data": "LOTTO.value",
				"visible": false,
				"sTitle": "Lotto",
				"searchable": false,
				"width": "10%",
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
		"dom" : 'l<"#add">frtip'
	});
	var _center = $("<center/>");
	var _href = "<a href='javascript:showMenuSelezione();' Title='Seleziona tutti'> <img src='"+_contextPath+"/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>";
	var _href = _href + "&nbsp;";
	var _href = _href + "<a href='javascript:showMenuDeselezione();' Title='Deseleziona tutti'><img src='"+_contextPath+"/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>";
	
	var _href = _href + '<div id="popmenuCheck" class="popupmenuskin" style="visibility: hidden;text-align: left;"><div class="chiusuramenu"><a href="javascript:hideMenuSelezione();"><img src="'+_contextPath+'/img/chiudi.gif" height="8" width="8" alt="Chiudi" title="Chiudi"></a></div><div class="menuitems" id=""><a href="javascript:_selezionaTutti();" tabindex="0">&nbsp;Seleziona tutti su pagina corrente</a></div><div class="menuitems" id=""><a href="javascript:_selezioneTuttePagine();" tabindex="1">&nbsp;Seleziona su tutte le pagine</a></div></div>'
	var _href = _href + "&nbsp;";
	var _href = _href + '<div id="popmenuUncheck" class="popupmenuskin" style="visibility: hidden;text-align: left;"><div class="chiusuramenu"><a href="javascript:hideMenuDeselezione();"><img src="'+_contextPath+'/img/chiudi.gif" height="8" width="8" alt="Chiudi" title="Chiudi"></a></div><div class="menuitems" id=""><a href="javascript:_deselezionaTutti();" tabindex="0">&nbsp;Deseleziona tutti su pag corrente</a></div><div class="menuitems" id=""><a href="javascript:_deselezionaTuttePagine();" tabindex="1">&nbsp;Deseleziona su tutte le pagine</a></div></div>'
	_center.append(_href);
	_center.appendTo($("#selChecks"));

	var tableResultXpage= $('#documenti').DataTable().page.info()?.length;
	var page= $('#documenti').DataTable().page();
	$('#documenti').DataTable().page.len(-1).draw();
	disabilitaGiaAggiunti();
	$('#documenti').DataTable().page.len(tableResultXpage)?.draw();
	$('#documenti').DataTable().page(page).draw(false);

	var columnNo = 1;
	$('#documenti tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca tipologia "/>' );
	$('#documenti tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca nome"/>' );
	$('#documenti tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca descrizione "/>' );
	$('#documenti tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca data"/>' );

	_tableDocumenti.columns().eq(0).each( function (colIdx) {
		$('input', _tableDocumenti.column(colIdx).footer()).on( 'keyup change', function () {
			let escapedValue = this.value.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&');
			if (colIdx != 1) {
				_tableDocumenti.column(colIdx).search(escapedValue).draw();
			}
			else if (colIdx == 1) {
				if (this.value == '') {
					_tableDocumenti.column(1).search(regexBase,true,false).draw();
				} else {
					let regex = `(?=.*` + escapedValue + `)` + regexBase;
					_tableDocumenti.column(1).search(regex,true,false).draw();
				}
			}
		});
    });

	// inizializzazione filtro su tipologia, ditta, lotto
	<c:choose>
		<c:when test="${not empty param.dittao}">
			_tableDocumenti.column(5).search(`^$|` + $.ui.autocomplete.escapeRegex(`${nomimoDittaPrincipale}`),true,false).draw();
			regexBase = '(^Riepilogo busta domanda di partecipazione$|^Riepilogo busta offerta$|^Notifica invio rinuncia offerta$|^documento inviato alla ditta$|^documento inviato dalla ditta$|^documento ricevuto dalla ditta$';
			regexBase += '|^Documentazione amministrativa$|^Offerta economica$|^Prequalifica$|^Offerta tecnica$)';
		</c:when>
		<c:otherwise>
			_tableDocumenti.column(5).search('^$',true,false).draw();
			regexBase = '(^documento del bando/avviso$|documento richiesto ai concorrenti$|documento richiesto agli operatori$|^documento dell\'esito$|^documento per la trasparenza$';
			regexBase += '|^atto o documento|^documento allegato all\'ordine di acquisto$|^Delibera a contrarre o atto equivalente$|^comunicazione pubblica$|^documento associato$|^Documento dell\'invito a presentare offerta$|^Documento dell\'invito all\'asta elettronica$)';
		</c:otherwise>
	</c:choose>
	_tableDocumenti.column(1).search(regexBase,true,false).draw();
	<c:choose>
		<c:when test="${not empty codiga}">
			_tableDocumenti.column(6).search('^$|^${codiga}$',true,false).draw();
		</c:when>
		<c:otherwise>
			_tableDocumenti.column(6).search('^$',true,false).draw();
		</c:otherwise>
	</c:choose>

	$("#documenti_filter").hide();

	var codgarPrimoGrado = "${codgar}";
	var codgarSecondoGrado = "${codgarSecondoGrado}";
	if (codgarSecondoGrado) {
		var selectHtml = '<label id="#dicituraSecondoGrado" style="float: right;">Accedi all\'archivio documenti delle procedure collegate ' +
		'<select name="selectSecondoGrado" id="selectSecondoGrado">' +
		'<option value="' + codgarPrimoGrado + '">' + (codgarPrimoGrado.startsWith("$") ? codgarPrimoGrado.substring(1) : codgarPrimoGrado) + '</option>' +
		'<option value="' + codgarSecondoGrado + '">' + (codgarSecondoGrado.startsWith("$") ? codgarSecondoGrado.substring(1) : codgarSecondoGrado) + '</option>' +
		'</select></label>';
		$('#documenti_length').after(selectHtml);
		$('#selectSecondoGrado').css({
			'border': '1px solid #aaa',
			'border-radius': '3px',
			'padding': '5px',
			'background-color': 'transparent',
			'color': 'inherit'
		});
		$('#selectSecondoGrado').change(function(){
			popolaTabella($('#selectSecondoGrado').val());
		});
		$('#selectSecondoGrado').val(codgar);
	}
}

function disabilitaGiaAggiunti() {
<c:forEach var="documentoAggiuto" items="${requestScope.listaDocumentiAggiunti}">
	$('input[type="checkbox"][name="documentiSelezionati"][value="${documentoAggiuto}"]').click();
	$('input[type="checkbox"][name="documentiSelezionati"][value="${documentoAggiuto}"]').prop('disabled','disabled');
</c:forEach>
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
	
function _deselezionaTuttePagine() {
	var tableResultXpage= $('#documenti').DataTable().page.info()?.length;
	var page= $('#documenti').DataTable().page();
	$('#documenti').DataTable().page.len(-1).draw();
	_deselezionaTutti();
	$('#documenti').DataTable().page.len(tableResultXpage)?.draw();
	$('#documenti').DataTable().page(page).draw(false);
}

var arrayIdDocumentiSelezionati = [];
var arrayDescrizioniDocumentiSelezionati = [];

function prendiSelezionati() {
	$( "input[name='documentiSelezionati']:checked").each( function( index ) {
		if ($( this ).prop( "disabled")) {
		}else{
			arrayIdDocumentiSelezionati.push($(this).val());
			descrizione = $('#' + $(this).val() + '_descrizione').text();
			arrayDescrizioniDocumentiSelezionati.push(descrizione);
		}
	});
}

function copiaDocumenti() {
	
	var tableResultXpage= $('#documenti').DataTable().page.info()?.length;
	var page= $('#documenti').DataTable().page();
	$('#documenti').DataTable().page.len(-1).draw();
	prendiSelezionati();
	$('#documenti').DataTable().page.len(tableResultXpage)?.draw();
	$('#documenti').DataTable().page(page).draw(false);

	if (arrayIdDocumentiSelezionati.length === 0) {
		alert('Non è stato selezionato nessun documento.');
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
			dittao : '${param.dittao}',
			arrayIdDocumentiSelezionati: arrayIdDocumentiSelezionati,
			arrayDescrizioniDocumentiSelezionati: arrayDescrizioniDocumentiSelezionati
		},
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "${contextPath}/pg/CopiaDocumentiInDOCUMATTI.do",
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
