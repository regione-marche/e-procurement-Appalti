<%
	/*
	 * Created on: 04/06/2010
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */

/*
	Descrizione:
		Finestra che visualizza la conferma per l'inserimento delle pubblicazioni bando/esito predefinite
*/
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/dataTable/dataTable/jquery.dataTables.css" >


<style type="text/css">
	.ui-dialog-titlebar {
		display: none;
	}
	span{
		font:11px Verdana, Arial, Helvetica, sans-serif;
	}
	#buste{
		font:11px Verdana, Arial, Helvetica, sans-serif;
	}
	#avanzamento{
		position: relative;
	}
	#avanzamento-label {
		position: absolute;
		left: 50%;
		top: 4px;
		font-weight: bold;
		text-shadow: 1px 1px 0 #fff;
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
		text-align: center;
		width: 20px;
	}

	TABLE.schedagperm TR.intestazione TH.descr, TABLE.schedagperm TR TD.descr {
		word-break:break-all;
		width: 100px;
	}
	TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.ck {
		width: 25px;
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
</style>

<div id="dialog-form" style="display:none">
	<br>
	<b>Esporta documenti presentati dalla ditta su file zip</b>
	<br>
	<div id="progressbardiv" style="display:none">
		<img src="${pageContext.request.contextPath}/img/${applicationScope.pathCss}progressbar.gif" alt=""/>
		<br><br>Esportazione in corso...
	</div>
	<p class="validateTips">
		<span id="tipMsg">Mediante questa funzione è possibile esportare in un unico file zip i documenti presentati dalla ditta '${param.nomimo}' elencati nella lista corrente.</span>
		<span id="tipMsgBari" style="display:none"><br><br><b>ATTENZIONE: Ci sono dei documenti della busta tecnica che dovrebbero essere scaricati solo dopo aver completato la prima fase della valutazione tecnica.</b></span><br>


	<div id="checkBuste">
		<span id="">Per limitare l'esportazione ai documenti di una singola busta, selezionare una voce di seguito:</span><br><br>
		<select name="buste" id="buste">
			<c:if test="${bustaPrequalifica eq 1}">
				<option value="4">Prequalifica</option>
			</c:if>
			<c:if test="${bustaAmministrativa eq 1}">
				<option value="1">Documentazione amministrativa</option>
			</c:if>
			<c:if test="${bustaTecnica eq 1}">
				<option value="2">Offerta tecnica</option>
			</c:if>
			<c:if test="${bustaEconomica eq 1}">
				<option value="3">Offerta economica</option>
			</c:if>
			<option value="0" selected="selected">seleziona tutto</option>
		</select><br>
	</div>
	<div id="documentiContainer"></div>
	<span id= "tipConfirm"><br>Confermi l'operazione ?</span>
	<span id="tipWaiting">Esportazione dei documenti in corso.</span>
	<br><br></p>
	<br>
	<div id="loading">
		<div id="avanzamento"><div id="avanzamento-label"></div></div>

	</div>
	<span id="errorMessage" style="color:red;display:none"><br></span>

</div>

<script type="text/javascript">


	function _nowait() {
		document.getElementById('bloccaScreen').style.visibility = 'hidden';
		document.getElementById('wait').style.visibility = 'hidden';
	}

	var annullato = false;
	var archivioCreato = "false";
	var busteChecked = "";
	var _contextPath = "${pageContext.request.contextPath}";
	var _tabellatoA1062   = null;

	function _getTabellatoA1062() {
		$.ajax({
			type: "POST",
			dataType: "json",
			async: false,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: "pg/GetTabellatoJSON.do",
			data: "tab1cod=A1062",
			success: function(data) {
				if (data) {
					_tabellatoA1062 = data;
				}
			},
			error: function(e){
				alert("Errore durante la lettura del tabellato A1062");
			}
		});
	}

	function openModal(ngara,ditta,contextPath,stepWizard,prefissoFile) {
		pingSessioneAttiva();

		$("#loading").hide();
		$("#avanzamento-label").html("0%");
		annullato = false;
		archivioCreato = "false";
		$("#tipMsg").show();
		$("#tipConfirm").show();
		var genereGara = $("#genereGara").val();
		if (genereGara == 10 || genereGara == 20) {
			$("#checkBuste").hide();
		} else {
			$("#checkBuste").show();
		}
		$("#tipWaiting").hide();
		$("#button-conferma").show();
		$("#dialog-form").css({
			"display": "block"
		});
		var prefissoFileDownloadComBari = "${prefissoFileDownloadComBari}";
		if (prefissoFile != null && prefissoFile != '' && prefissoFileDownloadComBari != null && prefissoFileDownloadComBari != '') {
			prefissoFile = prefissoFile.toUpperCase();
			var maxCount = "${datiRiga.rowCount}";
			var busta;
			var descrizione;
			var richiestaConfermaBari = false;
			for (i = 0; i < maxCount; i++) {
				descrizione = $("#INPUT_DESCRIZIONE_" + (i)).val();
				busta = $("#INPUT_BUSTA_" + (i)).val();
				if (busta == "2" && descrizione != null && descrizione != '' && prefissoFile != null && !richiestaConfermaBari && stepWizard == '5') {
					descrizione = descrizione.toUpperCase();
					if (descrizione.substring(0, prefissoFile.length) == prefissoFile) {
						richiestaConfermaBari = true;
						break;
					}


				}

			}
			if (richiestaConfermaBari)
				$("#tipMsgBari").show();
		}

		$("#dialog-form").dialog({
			resizable: false,
			height: "auto",
			width: genereGara == 10 || genereGara == 20 ? 1000 : 500,
			modal: true,
			position: { using: function( pos ) {
					$( this ).css({
						top:'50%',
						left: '50%',
						marginRight: '50%',
						transform: 'translate(-50%, -50%)'

					});
				}},
			close: function () {
			},
			buttons: [
				{
					text: "Conferma",
					id: "button-conferma",
					click: function () {
						if (genereGara == 10 || genereGara == 20){
							if (!checkAlmenoUnDocumentoSelezionato()){
								return;
							}
						}
						$('#continueButton').attr("disabled", true);
						$("#loading").show();
						$("#avanzamento").progressbar({
							value: 0
						});
						$("#tipMsg").hide();
						$("#tipMsgBari").hide();
						$("#tipConfirm").hide();
						$("#tipWaiting").show();
						$("#checkBuste").hide();
						downloadDocumentazione(ngara, ditta, stepWizard);
						$("#button-conferma").hide();
						$("#documentiContainer").hide();
					},

				}, {
					text: "Annulla",
					click: function () {
						annullato = true;
						$(this).dialog("close");
					}
				}
			]
		});
		//gestione export selettivo per elenchi operatori
		if(genereGara == 10 || genereGara == 20){
			$("#tipConfirm").hide();
			var rows = new Array();
			<c:forEach items="${datiLista}" var="row">
				var items = new Array();
			<c:forEach items="${row}" var="item">
				items.push(`${item}`);
			</c:forEach>
				rows.push(items);
			</c:forEach>
			popolaTabella(rows);
			$("#documentiContainer").show();
		}

	}


	function _wait() {
		document.getElementById('bloccaScreen').style.visibility = 'visible';
		$('#bloccaScreen').css("width",$(document).width());
		$('#bloccaScreen').css("height",$(document).height());
		document.getElementById('wait').style.visibility='visible';
		$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
	}

	function _nowait() {
		document.getElementById('bloccaScreen').style.visibility='hidden';
		document.getElementById('wait').style.visibility='hidden';
	}


	function ajaxCallDownload(counter,path){
		if(annullato){
			//si devono cancellare i fie temporanei
			$.ajax({
				type: "POST",
				url: "${pageContext.request.contextPath}/pg/ScaricaTuttiDocumentiBusta.do",
				data: {
					path: path,
					archivioCreato: archivioCreato,
					metodo: 'cancellaFileTemporanei'
				},
				async: true,
				dataType: "json"

			});
			return;
		}
		var iddocdg;
		var idprg;
		var maxCount = "${datiRiga.rowCount}";
		var genereGara = $( "#genereGara").val();
		var esistonoDati = $('#documentiContainer').text()!=='';
		if(esistonoDati && (genereGara == 10 || genereGara == 20)) {
			var maxCount = 0;
			var tableResultXpage = $('#documenti').DataTable().page.info()?.length; //salvo il numero di occorrenze per pagina
			var page = $('#documenti').DataTable().page(); //salvo la pagina attiva
			var backupSearch = [];
			$('#documenti').DataTable().columns().eq(0).each(function (colIdx) {
				backupSearch.push($('#documenti').DataTable().column(colIdx).search()); //salvo i filtri impostati
				$('#documenti').DataTable().column(colIdx).search(''); //rimuovo i filtri
			})
			$('#documenti').DataTable().page.len(-1).draw(); //setto a infinito il numero di occorrenze per pagina

			$("input[id^='chk_documento']:checked").each(function (index) {
				maxCount++;
				if(index===counter){
					var _id = $(this).prop("id").substring(14);
					iddocdg = _id.split("#;#")[0];
					idprg =_id.split("#;#")[1];
				}
			})
			$('#documenti').DataTable().columns().eq(0).each(function (colIdx) {
				$('#documenti').DataTable().column(colIdx).search(backupSearch[colIdx]); //ripristino i filtri impostati
			})
			$('#documenti').DataTable().page.len(tableResultXpage)?.draw(); //ripristino il numero di occorrenze per pagina
			$('#documenti').DataTable().page(page).draw(false); //ripristino la pagina attiva
		}else{
			var stop = false;
			while(!stop && !(counter > maxCount)){
				iddocdg = $( "#INPUT_IDDOCDG_" + (counter)).val();
				idprg = $( "#INPUT_IDPRG_" + (counter)).val();
				var busta = $( "#INPUT_BUSTA_" + (counter)).val();
				if(busteChecked.indexOf(busta) >= 0 || busteChecked==0){
					stop = true;
				}else{
					counter++;
				}
			}
		}

		if((!maxCount || maxCount == 0) || (counter > maxCount && archivioCreato == "false")){
			alert("Non è presente nessun documento da esportare nella lista");
			return $("#dialog-form").dialog( "close" );
		}else{
			if(counter > maxCount && archivioCreato == "true"){
				document.location.href='pg/ScaricaTuttiDocumentiBusta.do?metodo=download&path='+encodeURIComponent(path);
				return $("#dialog-form").dialog( "close" );
			}
			if((!iddocdg || !idprg)){
				return ajaxCallDownload(counter+1,path);
			}
		}

		$.ajax({
			type: "POST",
			url: "${pageContext.request.contextPath}/pg/ScaricaTuttiDocumentiBusta.do",
			data: {
				path: path,
				idddocdg: iddocdg,
				idprg: idprg,
				archivioCreato: archivioCreato,
				metodo: 'creaArchivio'
			},
			async: true,
			dataType: "json",
			success:function(data){
				archivioCreato = "true";
				counter++;
				var perc = Math.round((counter / maxCount) * 100);
				$("#avanzamento-label").html(perc + "%");
				$( "#avanzamento" ).progressbar({
					value: perc
				});
				if (counter < maxCount){
					ajaxCallDownload(counter,path);
				}else{
					document.location.href='pg/ScaricaTuttiDocumentiBusta.do?metodo=download&path='+encodeURIComponent(path);
					return $("#dialog-form").dialog( "close" );
				}
			}
		});
	}

	function checkAlmenoUnDocumentoSelezionato(){
		var tableResultXpage = $('#documenti').DataTable().page.info()?.length; //salvo il numero di occorrenze per pagina
		var page = $('#documenti').DataTable().page(); //salvo la pagina attiva
		var backupSearch = [];
		$('#documenti').DataTable().columns().eq(0).each(function (colIdx) {
			backupSearch.push($('#documenti').DataTable().column(colIdx).search()); //salvo i filtri impostati
			$('#documenti').DataTable().column(colIdx).search(''); //rimuovo i filtri
		})
		$('#documenti').DataTable().page.len(-1).draw(); //setto a infinito il numero di occorrenze per pagina

		var size = $("input[id^='chk_documento']:checked").length;
		$('#documenti').DataTable().columns().eq(0).each(function (colIdx) {
			$('#documenti').DataTable().column(colIdx).search(backupSearch[colIdx]); //ripristino i filtri impostati
		})
		$('#documenti').DataTable().page.len(tableResultXpage)?.draw(); //ripristino il numero di occorrenze per pagina
		$('#documenti').DataTable().page(page).draw(false); //ripristino la pagina attiva
		if(size==0) {
			alert("Selezionare almeno un documento.")
			return false;
		}
		return true;

	}

	function downloadDocumentazione(ngara, ditta,stepWizard) {

		var prequalifica = $("#prequalifica").is(":checked");
		var amministrativa = $("#amministrativa").is(":checked");
		var tecnica = $("#tecnica").is(":checked");
		var economica = $("#economica").is(":checked");

		busteChecked = $( "#buste option:selected" ).val();

		var documenti = {"documenti":[]};
		var genereGara = $( "#genereGara").val();
		var esistonoDati = $('#documentiContainer').text()!=='';
		if(esistonoDati && (genereGara == 10 || genereGara == 20)) {
			stepWizard = null;
			var tableResultXpage = $('#documenti').DataTable().page.info()?.length; //salvo il numero di occorrenze per pagina
			var page = $('#documenti').DataTable().page(); //salvo la pagina attiva
			var backupSearch = [];
			$('#documenti').DataTable().columns().eq(0).each(function (colIdx) {
				backupSearch.push($('#documenti').DataTable().column(colIdx).search()); //salvo i filtri impostati
				$('#documenti').DataTable().column(colIdx).search(''); //rimuovo i filtri
			})
			$('#documenti').DataTable().page.len(-1).draw(); //setto a infinito il numero di occorrenze per pagina

			$("input[id^='chk_documento']:checked").each(function (index) {
				var _id = $(this).prop("id");
				documenti.documenti.push({
					"iddocdg": _id.split("#;#")[0].substring(14),
					"idprg": _id.split("#;#")[1],
					"nomeDoc": _id.split("#;#")[2]
				});
			})
			$('#documenti').DataTable().columns().eq(0).each(function (colIdx) {
				$('#documenti').DataTable().column(colIdx).search(backupSearch[colIdx]); //ripristino i filtri impostati
			})
			$('#documenti').DataTable().page.len(tableResultXpage)?.draw(); //ripristino il numero di occorrenze per pagina
			$('#documenti').DataTable().page(page).draw(false); //ripristino la pagina attiva
		}else {
			var maxCount = "${datiRiga.rowCount}";
			for (i = 0; i < maxCount; i++) {
				var iddocdg = $("#INPUT_IDDOCDG_" + (i)).val();
				var idprg = $("#INPUT_IDPRG_" + (i)).val();
				var nomeDoc = $("#INPUT_DIGNOMDOC_" + (i)).val();
				var busta = $("#INPUT_BUSTA_" + (i)).val();
				if (busteChecked.indexOf(busta) >= 0 || busteChecked == 0) {
					documenti.documenti.push({
						"idprg": idprg,
						"iddocdg": iddocdg,
						"nomeDoc": nomeDoc
					});
				}
			}
		}

		var documentiStringa = JSON.stringify(documenti);
		var stepWizard = $( "#stepWizard").val();

		$.ajax({
			type: "POST",
			url: "${pageContext.request.contextPath}/pg/ScaricaTuttiDocumentiBusta.do",
			data: {
				ditta: ditta,
				ngara : ngara,
				documenti: documentiStringa,
				stepWizard: stepWizard,
				busta: busteChecked,
				metodo: 'getPath'
			},
			async: true,
			dataType: "json",
			success:function(data){
				ajaxCallDownload(0,data.path);
			},
			error: function(e){
				alert("Si è verificato un problema");
			}
		});

		return;

	}

	function _creaTabellaDocumenti() {

		var _table = $('<table/>', {"id": "documenti", "class": "schedagperm", "cellspacing": "0", "width" : "100%"});

		var _tr = $('<tr/>', {"class": "intestazione"});
		var _thck = $('<th/>', {"id": "selChecks","class": "ck"});
		_tr.append(_thck);
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
		_tr2.append('<td/>', {"class": "codice"});
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

		var messaggioSelezioneDoc = '<p>Selezionare i file che si desidera esportare.</p>'
		$("#documentiContainer").append(messaggioSelezioneDoc);
		$("#documentiContainer").append(_table);

	}

	function popolaTabella(data) {
		_getTabellatoA1062();

		$('#documentiContainer').empty();
		_creaTabellaDocumenti();

		$("#dialog-form").css({
			"max-height": "475px"
		});

		_tableDocumenti = $('#documenti').removeAttr('width').DataTable({
			"data": data,
			searching: true,
			"ordering": false,
			"bAutoWidth": false,
			"columnDefs": [
				{
					"visible": true,
					"targets": [0],
					"class": "ck",
					"render": function ( data, type, full, meta ) {
						var _div = $("<div/>");
						var _check = $("<input/>",{"type":"checkbox", "name": "documentiSelezionati", "id": "chk_documento_"+ full[0]+"#;#"+full[1]+"#;#"+full[3]});
						_div.append(_check);
						return _div.html();
					}
				},
				{
					"visible": false,
					"targets": [1]
				},
				{
					"searchable": true,
					"visible": true,
					"sTitle": "Descrizione",
					"targets": [ 2 ],
					"class" : "descr",
					"render":  function (data) {
						// Render as plain text to prevent XSS
						return $('<div/>').text(data).html();
					}
				},
				{
					"searchable": true,
					"visible": true,
					"sTitle": "Nome file",
					"targets": [ 3 ],
					"class" : "descr",
					"render": function ( data, type, full, meta ) {
						var _div = $("<div/>");
						var _a = $("<a/>",{"href":full[8]}).append(data);
						_div.append(_a);
						return _div.html();

					}
				},
				{
					"searchable": true,
					"visible": true,
					"sTitle": "Data presentazione",
					"targets": [ 4 ],
					"class" : "codice"
				},
				{
					"searchable": true,
					"visible": true,
					"sTitle": "Ora presentazione",
					"targets": [ 5 ],
					"class" : "codice"
				},
				{
					"searchable": true,
					"visible": true,
					"sTitle": "Data scadenza",
					"targets": [ 6 ],
					"class" : "codice"
				},
				{
					"searchable": true,
					"visible": true,
					"sTitle": "Situazione doc.",
					"targets": [ 7 ],
					"width": "50px",
					"render": function ( data, type, full, meta ) {
						var _ret = "";
						$.map(_tabellatoA1062, function (item) {
							if (item.tipoTabellato == data) {
								_ret = item.descTabellato;
							}
						});

						return _ret;
					}
				},
			],
			"aoColumns": [
				{ "bSortable": false, "bSearchable": false },
				{ "bSortable": false, "bSearchable": true },
				{ "bSortable": false, "bSearchable": true },
				{ "bSortable": false, "bSearchable": true },
				{ "bSortable": false, "bSearchable": true },
				{ "bSortable": false, "bSearchable": true },
				{ "bSortable": false, "bSearchable": true },
				{ "bSortable": false, "bSearchable": true }
			],
			language: {
				"sEmptyTable":     "Non ci sono documenti da esportare",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ documenti",
				"sInfoEmpty":      "Non ci sono documenti da esportare",
				"sInfoFiltered":   "",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca documenti",
				"sZeroRecords":    "Non ci sono documenti da esportare",
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
			"lengthMenu": [[10, 20, 50], ["10 documenti", "20 documenti", "50 documenti"]],
		});
		var _center = $("<center/>");
		var _href = "<a href='javascript:showMenuSelezione();' Title='Seleziona tutti'> <img src='"+_contextPath+"/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>";
		_href = _href + "&nbsp;";
		_href = _href + "<a href='javascript:showMenuDeselezione();' Title='Deseleziona tutti'><img src='"+_contextPath+"/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>";

		_href = _href + '<div id="popmenuCheck" class="popupmenuskin" style="visibility: hidden;text-align: left;"><div class="chiusuramenu"><a href="javascript:hideMenuSelezione();"><img src="'+_contextPath+'/img/chiudi.gif" height="8" width="8" alt="Chiudi" title="Chiudi"></a></div><div class="menuitems" id=""><a href="javascript:_selezionaTutti();" tabindex="0">&nbsp;Seleziona tutti su pagina corrente</a></div><div class="menuitems" id=""><a href="javascript:_selezioneTuttePagine();" tabindex="1">&nbsp;Seleziona su tutte le pagine</a></div></div>'
		_href = _href + "&nbsp;";
		_href = _href + '<div id="popmenuUncheck" class="popupmenuskin" style="visibility: hidden;text-align: left;"><div class="chiusuramenu"><a href="javascript:hideMenuDeselezione();"><img src="'+_contextPath+'/img/chiudi.gif" height="8" width="8" alt="Chiudi" title="Chiudi"></a></div><div class="menuitems" id=""><a href="javascript:_deselezionaTutti();" tabindex="0">&nbsp;Deseleziona tutti su pag corrente</a></div><div class="menuitems" id=""><a href="javascript:_deselezioneTuttePagine();" tabindex="1">&nbsp;Deseleziona su tutte le pagine</a></div></div>'
		_center.append(_href);
		_center.appendTo($("#selChecks"));

		var tableResultXpage= $('#documenti').DataTable().page.info()?.length;
		var page= $('#documenti').DataTable().page();
		$('#documenti').DataTable().page.len(-1).draw();
		$('#documenti').DataTable().page.len(tableResultXpage)?.draw();
		$('#documenti').DataTable().page(page).draw(false);

		var columnNo = 1;
		$('#documenti tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca descr. "/>' );
		$('#documenti tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca nome"/>' );
		$('#documenti tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca data pres."/>' );
		$('#documenti tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca ora pres."/>' );
		$('#documenti tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca data scad."/>' );
		$('#documenti tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca situaz. doc."/>' );

		_tableDocumenti.columns().eq(0).each( function (colIdx) {
			$('input', _tableDocumenti.column(colIdx).footer()).on( 'keyup change', function () {
				_tableDocumenti.column(colIdx).search(this.value).draw();
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
</script>



