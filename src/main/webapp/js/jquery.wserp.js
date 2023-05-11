
/*
 * Gestione dell'ERP
 */

var _tableRda = null;
var _messaggio_ctr = "LA QUERY NON HA PRODOTTO RISULTATO";
var _multiSelezione = true;

$(window).on("load", function (){
	if($("#linkrda").val()=='1'){
		var _selLotti = false;
	}else{
		var _selLotti = true;
	}

	var genereGara = $("#genere").val();

	if($("#genere").val()=='1' || $("#genere").val()=='3'){
		var _lottoVisibile = true;
		_multiSelezione = true;
	}else{
		var _lottoVisibile = false;
		_multiSelezione = false;
	}


	_getTipoWSERP();
	var visIdLotto = false;
	var visQta = false;
	var visUm = false;
	var visValStimato = false;
	var _searchRow=$('<tr/>');
	var _bRow=$('<br/>')

	var garabustalottiUnica = false;
	if($("#bustalotti").val()=='2'){
		garabustalottiUnica = true;
	}
	if(_tipoWSERP == 'SMEUP' || _tipoWSERP == 'UGOVPA'){
		visIdLotto = false;
	}
	if(_tipoWSERP == 'AVM'){
		visQta = true;
		visUm = true;
		visValStimato = true;
	}

	if(_tipoWSERP == 'FNM'){
		//visValStimato = true;
	}


	$('#rdamessaggio').text("");
	$('#rdamessaggio').hide();

	$(".contenitore-dettaglio").width("980");

	if(_tipoWSERP == 'SMEUP' || _tipoWSERP == 'UGOVPA' || _tipoWSERP == 'FNM' || _tipoWSERP == 'RAIWAY'){
		_creaTabellaRda();
		_popolaRda();
	}


	if(_tipoWSERP == 'AVM'){

		_creaFinestraSelRda();
		_creaSelettori();

		$('#menutrovarda').click(function() {
			$("#finestraselrda").dialog("open");
		});

		$("#search_cod_rda").keyup(function () {
			$(this).val($(this).val().toUpperCase());
		});

		$("#search_gruppo_acq").keyup(function () {
			$(this).val($(this).val().toUpperCase());
		});

		$("#search_divisione").keyup(function () {
			$(this).val($(this).val().toUpperCase());
		});

		$("#search_cod_mat").keyup(function () {
			$(this).val($(this).val().toUpperCase());
		});

	}









	$.fn.dataTable.ext.order['dom-checkbox'] = function (settings, col)
	{
		return this.api().column(col, {order:'index'}).nodes().map(function (td, i) {
			return $('input',td).prop('checked') ? '1' : '0';
		} );
	};







	$("body").delegate('[id^="ck_rda_"]', "click", function() {
		checkedState = $(this).prop('checked');
		if (_tipoWSERP == 'SMEUP' || _tipoWSERP == 'UGOVPA' || (_tipoWSERP == 'FNM' && !_multiSelezione) || _tipoWSERP == 'RAIWAY') {
			$( 'input[id^="ck_rda_"]' ).each( function( index ) {
				$(this).prop('checked', false);
			});
		}

		$(this).prop('checked', checkedState);
	});




	$('#menuasscarrello, #pulsasscarrello').click(function() {
		var nSelected = 0;
		var arrmultikey  = '';
		$( "input[id^='ck_rda_']" ).each( function( index ) {
			if($( this ).prop( "checked")){
				nSelected= nSelected + 1;
				_ck_id = $(this).attr("id");
				if (_ck_id.substring(0,7) == 'ck_rda_'){
					_ck_key = _ck_id.substring(7);
					if(_tipoWSERP == 'SMEUP' || _tipoWSERP == 'UGOVPA'){
						var arrkey = _ck_key.split('_');
						if(garabustalottiUnica){
							_popolaLotti($("#codgar").val(),"lottosel");
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
								width: 550,
								height:150,
								title: 'Associa lotto',
								buttons: {
									"Conferma": function() {
										if($("#lottosel").val()!= null){
											_waitgperm();
											$.ajax({
												type: "GET",
												dataType: "json",
												url: "pg/SetWSERPRdaInGara.do",
												data : {
													codgar: $("#codgar").val(),
													codice: $("#lottosel").val(),
													linkrda: $("#linkrda").val(),
													uffint: $("#uffint").val(),
													key: _ck_key,
													key0: arrkey[0],
													key1: arrkey[1],
													key2: arrkey[2]
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
									},
									"Annulla": function() {
										$( this ).dialog( "close" );
									}
								}
							};

							$("#mascheraParametriLotto").dialog(opt).dialog("open");

							setTimeout(function(){
							}, 800);

						}else{
							_waitgperm();
							$.ajax({
								type: "GET",
								dataType: "json",
								url: "pg/SetWSERPRdaInGara.do",
								data : {
									codgar: $("#codgar").val(),
									codice: $("#codice").val(),
									linkrda: $("#linkrda").val(),
									uffint: $("#uffint").val(),
									key: _ck_key,
									key0: arrkey[0],
									key1: arrkey[1],
									key2: arrkey[2]
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

					}

					//in ogni caso
					arrmultikey  = arrmultikey + _ck_key + ';';


				}


			}
		});

		if(!(nSelected > 0)){
			if(_tipoWSERP == 'SMEUP' || _tipoWSERP == 'UGOVPA'){
				alert("Selezionare un carrello");
			}else{
				alert("Selezionare almeno una RdA");
			}
		}else{
			if(_tipoWSERP == 'AVM' || (_tipoWSERP == 'FNM' && $("#genere").val()=='3')){
				if(garabustalottiUnica && _selLotti){
					_popolaLotti($("#codgar").val(),"lottosel");
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
						width: 550,
						height:150,
						title: 'Associa lotto',
						buttons: {
							"Conferma": function() {
								if($("#lottosel").val()!= null){
									_waitgperm();
									$.ajax({
										type: "GET",
										dataType: "json",
										url: "pg/SetWSERPRdaInGara.do",
										data : {
											codgar: $("#codgar").val(),
											codice: $("#lottosel").val(),
											linkrda: $("#linkrda").val(),
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
							},
							"Annulla": function() {
								$( this ).dialog( "close" );
							}
						}
					};

					$("#mascheraParametriLotto").dialog(opt).dialog("open");

					setTimeout(function(){
					}, 800);

				}else{
					_waitgperm();
					$.ajax({
						type: "GET",
						dataType: "json",
						url: "pg/SetWSERPRdaInGara.do",
						data : {
							codgar: $("#codgar").val(),
							codice: $("#codice").val(),
							linkrda: $("#linkrda").val(),
							uffint: $("#uffint").val(),
							tipoAppalto: $("#tipoAppalto").val(),
							tipgar: $("#tipoProcedura").val(),
							arrmultikey: arrmultikey
						},
						success: function(res) {
							if(_tipoWSERP == 'FNM' || _tipoWSERP == 'AVM'){
								var esito= res.Esito;
								if(esito!="0"){
									var msgEsito = "Errore nella associazione delle RdA in gara. " + res.MsgErrore;
									alert(msgEsito);
									if(_tipoWSERP == 'FNM'){
										historyVaiIndietroDi(1);
									}
								}else{
									historyVaiIndietroDi(1);
								}
							}else{
								historyVaiIndietroDi(1);
							}
						},

						error: function(e){
							alert("Errore nella associazione delle RdA in gara");
						},
						complete: function() {
							_nowaitgperm();
						}
					});
				}
			}//AVM

		}

	});


	$('#menuassprocedi, #pulsassprocedi').click(function() {
		var nSelected = 0;
		var arrmultikey  = '';
		$( "input[id^='ck_rda_']" ).each( function( index ) {
			if($( this ).prop( "checked")){
				nSelected= nSelected + 1;
				_ck_id = $(this).attr("id");
				if (_ck_id.substring(0,7) == 'ck_rda_'){
					_ck_key = _ck_id.substring(7);
					if(_tipoWSERP == 'FNM'){
						var arrkey = _ck_key.split('_');
						var numRda =  arrkey[0];
						var divisione =  arrkey[1];
						var tipoAppalto = '2';
						var proceduraTelematica = $("#proceduraTelematica").val();
						var modalitaPresentazione = $("#modalitaPresentazione").val();

						document.listaNuovo.entita.value = "GARE";
						document.listaNuovo.activePage.value = 0;
						document.listaNuovo.jspPath.value="/WEB-INF/pages/gare/gare/gare-scheda.jsp";
						document.listaNuovo.jspPathTo.value="/WEB-INF/pages/gare/gare/gare-scheda.jsp";


						document.listaNuovo.action = document.listaNuovo.action + "&tipoGara=garaLottoUnico&tipoAppalto="+tipoAppalto+"&proceduraTelematica="+proceduraTelematica+"&modalitaPresentazione="+modalitaPresentazione+"&numeroRda="+numRda+"&divisione="+divisione;

						bloccaRichiesteServer();
						document.listaNuovo.submit();

					}

					if(_tipoWSERP == 'RAIWAY'){
						var arrkey = _ck_key.split('_');
						var numRda =  arrkey[0];
						var divisione =  arrkey[1];
						var tipoAppalto = '2';
						var proceduraTelematica = $("#proceduraTelematica").val();
						var modalitaPresentazione = $("#modalitaPresentazione").val();
						var tipoGara = $("#tipoGara").val();

						if(tipoGara == "garaLottoUnico"){
							document.listaNuovo.entita.value = "GARE";
							document.listaNuovo.activePage.value = 0;
							document.listaNuovo.jspPath.value="/WEB-INF/pages/gare/gare/gare-scheda.jsp";
							document.listaNuovo.jspPathTo.value="/WEB-INF/pages/gare/gare/gare-scheda.jsp";
						} else{
							document.listaNuovo.entita.value = "TORN";
							document.listaNuovo.activePage.value = 0;
							document.listaNuovo.jspPath.value="/WEB-INF/pages/gare/torn/torn-scheda.jsp";
							document.listaNuovo.jspPathTo.value="/WEB-INF/pages/gare/torn/torn-scheda.jsp";
						}

						document.listaNuovo.action = document.listaNuovo.action + "&tipoGara="+tipoGara+"&tipoAppalto="+tipoAppalto+"&proceduraTelematica="+proceduraTelematica+"&modalitaPresentazione="+modalitaPresentazione+"&numeroRda="+numRda+"&divisione="+divisione;

						bloccaRichiesteServer();
						document.listaNuovo.submit();

					}

					//in ogni caso
					arrmultikey  = arrmultikey + _ck_key + ';';


				}


			}
		});

		if(!(nSelected > 0)){
			if(_tipoWSERP == 'FNM'){
				alert("Selezionare un procedimento");
			}
			if(_tipoWSERP == 'RAIWAY'){
				alert("Selezionare una RdA");
			}
		}else{

		}

	});





});



/**
 * Crea la finestra principale modale
 */
function _creaFinestraSelRda() {
	var _finestraSelRda = $("<div/>",{"id": "finestraselrda", "title":"Cerca Rda: selezione delle rda"});
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
			duration: 350
		},
		hide: {
			effect: "blind",
			duration: 350
		},
		resizable: true,
		height: 320,
		width: 600,
		minHeight: 320,
		minWidth: 600,
		modal: true,
		focusCleanup: true,
		cache: false,
		buttons: {
			"Trova":  _trovaRda,
			"Annulla": function() {
				$( this ).dialog( "close" );
			}
		}
	});

	_finestraSelRda.on( "dialogclose", function( event, ui ) {

		if (_tableRda != null) {
			;
		}else{
			historyVaiIndietroDi(1);
		}

	});

}

function _trovaRda() {
	var nValorized = 0;
	$( "input[id^='search_']" ).each( function( index ) {

		if($( this ).val()!=null && $( this ).val()!=''){
			nValorized = nValorized +1;
		}

	});

	if(nValorized > 0){

		if (_tableRda != null) {
			_tableRda.destroy(true);
		}

		_creaTabellaRda();
		_popolaRda();
		$("#finestraselrda").dialog("close");

	}else{
		alert('Valorizzare almeno un parametro di ricerca');
	}
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
	}, 500);
}


/**
 * Crea il contenitore del "datatable" per la lettura delle rda
 */
function _creaSelettori() {
	var _container = $("<table/>", {"id": "rdaselcontainer", "class": "dettaglio-notab"});
	_container.css("font","11px Verdana, Arial, Helvetica, sans-serif");
	var _tr = $("<tr/>");
	var _td = $("<td/>");
	_td.css("border-bottom","0px");

	var _divSearchRdaContainer = $("<div/>", {"margin-top": "25px", "margin-bottom": "25px"});
	var _divRdaContainer = $("<div/>", {"id": "divrdacontainer", "margin-top": "25px", "margin-bottom": "25px", "width" : "98%"});

	var _span_search_cod_rda = $("<span/>");
	var _search_cod_rda = $("<input/>",{"id":"search_cod_rda","size": "35"});
	_span_search_cod_rda.append($("<td/>", {"class": "etichetta-search", "text": "Codice Rda "}));
	_span_search_cod_rda.append(_search_cod_rda);
	_divSearchRdaContainer.append(_span_search_cod_rda);
	_divSearchRdaContainer.append("<br/><br/>");

	var _span_search_gruppo_acq = $("<span/>");
	var _search_gruppo_acq = $("<input/>",{"id":"search_gruppo_acq","size": "35"});
	_span_search_gruppo_acq.append($("<td/>", {"class": "etichetta-search", "text": "Gruppo acquisti "}));
	_span_search_gruppo_acq.append(_search_gruppo_acq);
	_divSearchRdaContainer.append(_span_search_gruppo_acq);
	_divSearchRdaContainer.append("<br/><br/>");

	var _span_search_divisione = $("<span/>");
	var _search_divisione = $("<input/>",{"id":"search_divisione","size": "35"});
	_span_search_divisione.append($("<td/>", {"class": "etichetta-search", "text": "Divisione "}));
	_span_search_divisione.append(_search_divisione);
	_divSearchRdaContainer.append(_span_search_divisione);
	_divSearchRdaContainer.append("<br/><br/>");

	var _span_search_cod_mat = $("<span/>");
	var _search_cod_mat = $("<input/>",{"id":"search_cod_mat","size": "35"});
	_span_search_cod_mat.append($("<td/>", {"class": "etichetta-search", "text": "Codice materiale "}));
	_span_search_cod_mat.append(_search_cod_mat);
	_divSearchRdaContainer.append(_span_search_cod_mat);
	_divSearchRdaContainer.append("<br/><br/>");

	var _span_search_data_cons = $("<span/>");
	var _search_data_cons = $("<input/>",{"id":"search_data_cons","size": "10"});
	_span_search_data_cons.append($("<td/>", {"class": "etichetta-search", "text": "Data consegna "}));
	_span_search_data_cons.append(_search_data_cons);
	_divSearchRdaContainer.append(_span_search_data_cons);
	_divSearchRdaContainer.append("<br/><br/>");


	var _span_search_descr_rda = $("<span/>");
	var _search_descr_rda = $("<input/>",{"id":"search_descr_rda","size": "35"});
	_span_search_descr_rda.append($("<td/>", {"class": "etichetta-search", "text": "Oggetto "}));
	_span_search_descr_rda.append(_search_descr_rda);
	_divSearchRdaContainer.append(_span_search_descr_rda);



	_td.append(_divSearchRdaContainer);
	_td.append(_divRdaContainer);
	_tr.append(_td);
	_td.append("<br/><br/>");
	_container.append(_tr);
	$("#finestraselrda").append(_container);
	$( "#search_data_cons" ).datepicker();

}


function _creaTabellaRda() {

	var _table = $('<table/>', {"id": "listarda", "class": "schedagperm", "cellspacing": "0", "width" : "100%"});

	var _tr = $('<tr/>', {"class": "intestazione"});
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
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	_tr.append('<th/>');
	var _thck = $('<th/>', {"id": "selrda","class": "ck"}); //
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

	$("#rdaContainer").append(_table);


}




function _selezionaTutti() {
	$("input[id^='ck_rda_']").prop("checked","checked");
}

function _deselezionaTutti() {
	$( "input[id^='ck_rda_']" ).each( function( index ) {
		if ($( this ).prop( "disabled")) {
			;
		}else{
			$( this ).prop( "checked",false);
		}
	});
}

function _getCarrelloAssociato(){
	var codice = $("#codice").val();
	$.ajax({
		dataType: "json",
		url: "pg/GetWSERPCarrelloAssociato.do",
		data: "codice=" + codice,
		success: function(json){
			if (json.esito == true) {
				$("#menuasscarrello").hide();
				$("#pulsasscarrello").hide();
				$("input[id^='ck_rda_']").hide();
			}
		}
	});
}


function _popolaRda() {
	var descriptionMap = [];
	var _visStatoSel = true;
	var visIdLotto = false;
	var visOggetto = false;
	var visDescrizione = true;
	var visQta = false;
	var visUm = false;
	var visValStimato = false;
	var visGrpAcq = false;
	var visDivisione = false;
	var visCodMat = false;
	var visRichiedente = false;
	var visDataConsegna = false;
	var visLuogoConsegna = false;
	var visTotale = false;
	var visNrPos =false;
	var visNrLotti = false;
	var visRDP = false;
	var visPosRda = true;
	var titCodRda = "Codice RdA";
	var sWidthCodRda = "50px";
	var sClassCodRda = "desccenter";

	var titPosRda = "Pos. RdA";
	var titDivisione = "Div.";
	var titCodMat = "Codice materiale";
	var garabustalottiUnica = false;

	if($("#bustalotti").val()=='2'){
		garabustalottiUnica = true;
	}

	if(_tipoWSERP == 'SMEUP'){
		visIdLotto = false;
		visDataConsegna = false;
		visLuogoConsegna = false;
		var rangesel = [[20, 50, 100], ["20 RdA", "50 RdA", "100 RdA"]];
	}

	if(_tipoWSERP == 'UGOVPA'){
		visIdLotto = false;
		visDataConsegna = false;
		visLuogoConsegna = false;
		var visOggetto = false;
		var visDescrizione = false;
		titCodRda = "Descrizione RdA";
		var sWidthCodRda = "";
		var sClassCodRda = "";
		titPosRda = "Descrizione dettaglio Rda";
		var rangesel = [[20, 50, 100], ["20 RdA", "50 RdA", "100 RdA"]];
	}


	if(_tipoWSERP == 'AVM'){
		visOggetto = true;
		visQta = true;
		visUm = true;
		visValStimato = true;
		visDataConsegna = true;
		visGrpAcq = true;
		visDivisione = true;
		visCodMat = true;
		visRichiedente = true;
		visDataConsegna = true;
		visLuogoConsegna = true;
		var rangesel = [[20, 50, 100,-1 ], ["20 RdA", "50 RdA", "100 RdA", "Tutte le Rda"]];
	}

	if(_tipoWSERP == 'FNM'){
		visIdLotto = false;
		titCodRda = "Procedimento";
		visPosRda = false;
		visOggetto = true;
		visValStimato = true;
		visGrpAcq = true;
		visDivisione = true;
		visCodMat = true;
		titDivisione = "Esercizio";
		titCodMat = "Societ&agrave;";
		var visDescrizione = false;
		var rangesel = [[20, 50, 100], ["20 RdA", "50 RdA", "100 RdA"]];
	}

	//Integrazione tabella RAIWAY
	if(_tipoWSERP == 'RAIWAY'){
		visPosRda = false;
		visDescrizione = false;
		titCodRda = 'Numero RdA';
		visGrpAcq = true;
		visOggetto = true;
		visTotale = true;
		visNrPos = true;
		visNrLotti = true;
		visRDP = true;
		var rangesel = [[20, 50, 100], ["20 RdA", "50 RdA", "100 RdA"]];
	}

	if(_tipoWSERP == 'AVM'){
		var _codrda = $("#search_cod_rda").val();
		var _gruppoacq = $("#search_gruppo_acq").val();
		var _divisione = $("#search_divisione").val();
		var _datacons = $("#search_data_cons").val();
		var _codmat = $("#search_cod_mat").val();
		var _oggetto = $("#search_descr_rda").val();
	}

	_waitgperm();

	_tableRda = $('#listarda').removeAttr('width').DataTable( {
		"ajax": {
			"url": "pg/GetWSERPListaRda.do",
			"data" : function (n) {
				return {
					operation: $("#operation").val(),
					codgar: $("#codgar").val(),
					genere: $("#genere").val(),
					tipoAppalto: $("#tipoAppalto").val(),
					tipoGara: $("#tipoGara").val(),
					uffint: $("#uffint").val(),
					scProfilo: $("#scProfilo").val(),
					codicerda: _codrda,
					gruppoacq: _gruppoacq,
					divisione: _divisione,
					codicemateriale: _codmat,
					dataconsegna: _datacons,
					oggetto: _oggetto
				};
			},
			error: function(e){
				var messaggio = "Errore durante la lettura delle rda";
				$('#rdamessaggio').text(messaggio);
				$('#rdamessaggio').show(300);
			},
			"complete": function() {
				_nowaitgperm();
				if(_tipoWSERP == 'SMEUP' || _tipoWSERP == 'UGOVPA'){
					_getCarrelloAssociato();
				}
			}
		},

		"bAutoWidth": false,

		"columnDefs": [
			{
				"data": "idLotto",
				"visible": visIdLotto,
				"sTitle": "ID Lotto",
				"searchable": false,
				"targets": [ 0 ]
			},

			{
				"data": "codiceRda",
				"visible": true,
				"sTitle": titCodRda,
				"searchable": false,
				"sWidth": sWidthCodRda,
				"sClass": sClassCodRda,
				"targets": [ 1 ]
			},
			{
				"data": "posizioneRda",
				"visible": visPosRda,
				"sTitle": titPosRda,
				"searchable": false,
				"targets": [ 2 ]
			},
			{
				"data": "gruppoAcquisti",
				"visible": visGrpAcq,
				"sTitle": "Gr.acquisti",
				"searchable": false,
				"sWidth": "50px",
				"sClass": "desccenter",
				"targets": [ 3 ]
			},
			{
				"data": "divisione",
				"visible": visDivisione,
				"sTitle": titDivisione,
				"searchable": false,
				"sWidth": "50px",
				"sClass": "desccenter",
				"targets": [ 4 ]
			},
			{
				"data": "codiceMateriale",
				"visible": visCodMat,
				"sTitle": titCodMat,
				"searchable": false,
				"sWidth": "110px",
				"sClass": "desccenter",
				"targets": [ 5 ]
			},
			{
				"data": "oggettoRda",
				"visible": visOggetto,
				"sTitle": "Oggetto",
				"sWidth": "100px",
				"sClass": "aligncenter",
				"searchable": false,
				"targets": [ 6 ],
			},
			{
				"data": "descrizioneRda",
				"visible": visDescrizione,
				"sTitle": "Descrizione",
				"searchable": false,
				"sWidth": "100px",
				"targets": [ 7 ]
			},
			{
				"data": "qta",
				"visible": visQta,
				"sTitle": "Q.ta",
				"searchable": false,
				"sWidth": "50px",
				"sClass": "desccenter",
				"targets": [ 8 ]
			},
			{
				"data": "um",
				"visible": visUm,
				"sTitle": "U.M.",
				"searchable": false,
				"sWidth": "60px",
				"targets": [ 9 ]
			},
			{
				"data": "valStimato",
				"visible": visValStimato,
				"sTitle": "Valore stimato",
				"searchable": false,
				"sWidth": "60px",
				"sClass": "desccenter",
				"targets": [ 10 ]
			},
			{
				"data": "richiedente",
				"visible": visRichiedente,
				"sTitle": "Richiedente",
				"searchable": false,
				"sWidth": "100px",
				"targets": [ 11 ]
			},
			{
				"data": "dataConsegna",
				"visible": visDataConsegna,
				"sTitle": "Data consegna",
				"searchable": false,
				"sWidth": "100px",
				"targets": [ 12 ]
			},
			{
				"data": "luogoConsegna",
				"visible": visLuogoConsegna,
				"sTitle": "Luogo consegna",
				"searchable": false,
				"sWidth": "100px",
				"targets": [ 13 ]
			},
			{
				"data": "totale",
				"visible": visTotale,
				"render": $.fn.dataTable.render.number('.', ',', 2, '',' \u20ac') ,
				"sTitle": "Totale",
				"searchable": false,
				"sWidth": "100px",
				"targets": [ 14 ]
			},
			{
				"data": "nrPos",
				"visible": visNrPos,
				"sTitle": "Nr. Pos",
				"className": "dt-body-center",
				"searchable": false,
				"sWidth": "70px",
				"targets": [ 15 ]
			},
			{
				"data": "nrLotti",
				"visible": visNrLotti,
				"sTitle": "Nr. Lotti",
				"className": "dt-body-center",
				"searchable": false,
				"sWidth": "70px",
				"targets": [ 16 ]
			},
			{
				"data": "rdp",
				"visible": visRDP,
				"sTitle": "RDP",
				"searchable": false,
				"sWidth": "100px",
				"targets": [ 17 ]
			},
			{
				"data": "checkRda",
				"visible": true,
				"targets": [ 18 ],
				"class" : "ck",
				"sWidth" : "70px",
				"render": function (data, type, full, meta ) {
					var _div = $("<div/>");
					if(_tipoWSERP == 'SMEUP' || _tipoWSERP == 'UGOVPA'){
						if(full.idLotto!=null){
							descriptionMap[full.idLotto]= full.descrizioneRda;
							var _check = $("<input/>",{"type":"checkbox", "id": "ck_rda_" + full.idLotto + "_" + full.codiceRda + "_" + full.posizioneRda});
						}
					}else{
						if(_tipoWSERP == 'FNM'){
							if(full.codiceRda!=null){
								var _check = $("<input/>",{"type":"checkbox", "id": "ck_rda_" + full.codiceRda + "_" + full.divisione});
								_div.append(_check);
							}
						} else if(_tipoWSERP == 'RAIWAY'){
							var _check = $("<input/>",{"type":"checkbox", "id": "ck_rda_" + full.codiceRda});
							_div.append(_check);
						} else{
							if(full.codiceRda!=null){
								var _check = $("<input/>",{"type":"checkbox", "id": "ck_rda_" + full.codiceRda + "_" + full.posizioneRda});
								_div.append(_check);
							}
						}
					}

					return _div.html();
				}
			}
		],

		//group corrisponde a idLotto per SmeUp
		"drawCallback": function ( settings ) {
			if (_tipoWSERP == 'SMEUP') {
				var api = this.api();
				var rows = api.rows( {page:'current'} ).nodes();
				var last=null;

				api.column(0, {page:'current'} ).data().each( function ( group, i ) {
					if ( last !== group ) {
						$(rows).eq( i ).before(
							'<tr class="group" style="background: rgb(192, 192, 192);"><td colspan="3">'+group+'</td><td class="ck"> <input type="checkbox" id="ck_rda_'+group+'"> </td></tr>'
						);

						last = group;
					}
				} );

			}
			if (_tipoWSERP == 'UGOVPA') {
				var api = this.api();
				var rows = api.rows( {page:'current'} ).nodes();
				var last=null;

				api.column(0, {page:'current'} ).data().each( function ( group, i ) {
					if ( last !== group ) {
						$(rows).eq( i ).before(
							'<tr class="group" ><td colspan="2">'+descriptionMap[group]+'</td><td class="ck"> <input type="checkbox" id="ck_rda_'+group+'"> </td></tr>'
						);

						last = group;
					}
				} );

			}
		},


		"language": {
			"sEmptyTable":     "Non ci sono RdA disponibili",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ RdA",
			"sInfoEmpty":      "Non ci sono RdA disponibili",
			"sInfoFiltered":   "(su _MAX_ RdA totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sSearch":         "Cerca RdA",
			"sZeroRecords":    "Non ci sono RdA disponibili",
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
				$("#listarda tfoot").hide();
				$("#listarda_info").hide();
				$("#listarda_paginate").hide();
			}
		},

		"pagingType": "full_numbers",
		"lengthMenu": rangesel,
		"ordering": false,
		"aoColumns": [
			{ "bSortable": true, "bSearchable": false },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": false, "bSearchable": true },
			{ "bSortable": false, "bSearchable": true },
			{ "bSortable": false, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": true, "bSearchable": true },
			{ "bSortable": false, "bSearchable": false },
			{ "bSortable": false, "bSearchable": false },
			{ "bSortable": false, "bSearchable": false },
			{ "bSortable": false, "bSearchable": false },
			{ "bSortable": false, "bSearchable": false },
			{ "bSortable": false, "bSearchable": false },
			{ "bSortable": false, "bSearchable": false },
			{ "bSortable": false, "bSearchable": false },
			{ "bSortable": false, "bSearchable": false },
			{ "bSortable": false, "bSearchable": true },
		]
	});

	if(_tipoWSERP == 'FNM'){

		$('#listarda tfoot td').eq(0).html( '<input class="search" size="20" type="text" placeholder="Ric.proc."/>' );
		$('#listarda tfoot td').eq(1).html( '<input class="search" size="20" type="text" placeholder="Ric.gr.acq."/>' );
		$('#listarda tfoot td').eq(3).html( '<input class="search" size="20" type="text" placeholder="Ric.societ&agrave; "/>' );
		$('#listarda tfoot td').eq(4).html( '<input class="search" size="20" type="text" placeholder="Ric.oggetto "/>' );

		_tableRda.columns().eq(0).each( function (colIdx) {
			$('input', _tableRda.column(colIdx).footer()).on( 'keyup change', function () {
				_tableRda.column(colIdx).search(this.value).draw();
			});
		});

	}

	if(_tipoWSERP == 'RAIWAY'){

		$('#listarda tfoot td').eq(0).html( '<input class="search" size="20" type="text" placeholder="Ric.Rda."/>' );
		$('#listarda tfoot td').eq(1).html( '<input class="search" size="20" type="text" placeholder="Ric.gr.acq."/>' );
		$('#listarda tfoot td').eq(2).html( '<input class="search" size="20" type="text" placeholder="Ric.oggetto "/>' );
		$('#listarda tfoot td').eq(6).html( '<input class="search" size="20" type="text" placeholder="Ric.RDP "/>' );

		_tableRda.columns().eq(0).each( function (colIdx) {
			$('input', _tableRda.column(colIdx).footer()).on( 'keyup change', function () {
				_tableRda.column(colIdx).search(this.value).draw();
			});
		});

	}

	if(!(_tipoWSERP == 'RAIWAY' || _tipoWSERP == 'SMEUP' || _tipoWSERP == 'UGOVPA' || (_tipoWSERP == 'FNM' && !_multiSelezione))){
		var _center = $("<center/>");
		var _href = "<a href='javascript:_selezionaTutti();' Title='Seleziona tutti'> <img src='"+_contextPath+"/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>";
		var _href = _href + "&nbsp;";
		var _href = _href + "<a href='javascript:_deselezionaTutti();' Title='Deseleziona tutti'><img src='"+_contextPath+"/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>";
		_center.append(_href);
		_center.appendTo($("#selrda"));
	}

	$("#listarda_filter").hide();

}
  

  
