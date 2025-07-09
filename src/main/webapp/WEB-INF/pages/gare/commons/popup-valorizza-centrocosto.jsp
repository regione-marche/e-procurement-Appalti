<%
	/*
	 *
	 * Copyright (c) Maggioli S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>


<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<div style="display: none">
	<div class="contenitore-errori-custom" >
		<div class="errori-javascript-titolo">
			<div><img src="${pageContext.request.contextPath}/img/jsMsgOn.gif" alt="Messaggi">Messaggi</div>
			<div class="errori-javascript-dettaglio">

			</div>
		</div>
	</div>
</div>

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<c:set var="nomeSA" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select nomein from uffint where codein=?", param.codein)}'/>
	<c:set var="isCUC" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select iscuc from uffint where codein=?", param.codein)}'/>
	
	<c:set var="bloccoAltrisogNonValorizzato" value="${isCUC eq '1' and empty param.modalita && gene:checkProt(pageContext, 'COLS.VIS.GARE.TORN.ALTRISOG')}"/>
	<c:set var="bloccoSANonValorizzata" value="${empty param.codein}"/>
	<c:set var="blocco" value="${bloccoAltrisogNonValorizzato eq 'true' or bloccoSANonValorizzata eq 'true'}"/>

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:setString name="titoloMaschera" value='Selezione del centro di costo per ${nomeSA}' />
	
	<gene:redefineInsert name="head" >
    <script type="text/javascript" src="${contextPath}/js/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${contextPath}/js/jquery.easytabs.js"></script>
	
	<style>
		#errori {
			color: #721c24;
			background-color: #f8d7da;
			border: 1px solid #f5c6cb;
		}

		.message-box {
			padding: 1rem;
			border-radius: 3px;
			margin: 1em 0;
		}
		
		TABLE.dettaglio-notab TD.etichetta-search {
			width: 300px;
			PADDING-RIGHT: 10px;
			TEXT-ALIGN: right;
		}
		
		TABLE.dettaglio-notab INPUT.search-input {
			margin-left: 10px;
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
        width: 50px;
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
		</style>
	
	</gene:redefineInsert>

	<gene:redefineInsert name="corpo">

		<form action="${contextPath}/pg/ScambioDatiPCP.do" method="post" name="formRecuperaCC">
			<input type="hidden" name="metodo" value="recuperaCentriCosto"/>
			<input type="hidden" name="codein" value="${param.codein}"/>
			<input type="hidden" name="modalita" value="${param.modalita}"/>
			<input type="hidden" name="row" value="${param.row}"/>
			<table class="dettaglio-notab">
			<c:choose>
				<c:when test="${empty RISULTATO && bloccoSANonValorizzata eq 'true'}">
					<tr>
						<td colspan="2"><br>
							<b>Non è possibile procedere alla selezione del centro di costo.</b>
							<br><br>
							Valorizzare la stazione appaltante.
							<br><br><br><br>
						</td>
					</tr>
				</c:when>
				<c:when test="${empty RISULTATO && bloccoAltrisogNonValorizzato eq 'true'}">
					<tr>
						<td colspan="2"><br>
							<b>Non è possibile procedere alla selezione del centro di costo.</b>
							<br><br>
							Valorizzare il campo 'Centrale di committenza agisce per conto di'.
							<br><br><br><br>
						</td>
					</tr>
				</c:when>
				<c:when test="${empty RISULTATO && blocco ne 'true'}">
					<tr>
						<td colspan="2"><br>
							Mediante questa funzione è possibile recuperare i centri di costo attivi per la stazione appaltante scelta.
							<br><br><br><br>
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${not empty jsonConErrori}">
						<tr>
						<td colspan="2">
							<br>
							Non è possibile procedere alla selezione del centro di costo.
							<br><br><br>
							</td>
						</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<td colspan="2">
									<b>Non è possibile procedere alla selezione del centro di costo</b>
									<br><br>
									<c:choose>
										<c:when test="${not empty erroriConfigurazione}">
											${erroriConfigurazione.pgMessage}
										</c:when>	
										<c:when test="${empty listaCCJson.centri}">
											Nessun centro di costo attivo per la stazione appaltante scelta.
										</c:when>
									</c:choose>
									<br><br><br><br>
								</td>
							</tr>
						</c:otherwise>				
					</c:choose>
				</c:otherwise>
			</c:choose>
				<tr>
					<td colspan="2" class="comandi-dettaglio">
						<c:if test="${RISULTATO ne 'OK' and blocco ne 'true'}">
						<INPUT type="button" class="bottone-azione" value="Recupera centri di costo" title="Recupera centri di costo" onclick="javascript:recuperaCC();">
						</c:if>
						<INPUT type="button" class="bottone-azione" value="Chiudi"	title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</form>	
	</gene:redefineInsert>
	
	<c:if test="${!empty listaCCJson.centri}">
	
	
	<gene:setString name="titoloMaschera" value='Selezione del centro di costo per ${nomeSA}' />
	<gene:redefineInsert name="corpo">
	
	<br>
	<span style="margin-left: 8px">
		Dati della stazione appaltante:<br>
	</span>
	<span style="margin-left: 8px">
		Codice fiscale${fittizioANAC}: <span id="cfUtilizzato">${cfUtilizzato}</span><br>
	</span>
	<span style="margin-left: 8px">
		Codice AUSA${fittizioANAC}: <span id="codausaUtilizzato">${codausaUtilizzato}</span>
	</span>
	<br><br>
	
	<div id="centriContainer" style="margin-left:8px; width: 98%"></div>
	<table class="datilista">
		<tr>
			<td colspan="4" class="comandi-dettaglio" style="text-align:right">
				<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
			</td>
		</tr>
	</table>
	</gene:redefineInsert>
	</c:if>
	
	<gene:javaScript>
	
		function getCC(link){
			var denom = $(link).closest('tr').find('td:first-child').text();
			var id= $(link).text();
			selezionaCC(id,denom);
		}
		
		function selezionaCC(id, denom){
			<c:choose>
				<c:when test="${param.modalita eq 'altsog'}">
					window.opener.document.forms[0].CODCENTRO_GARALTSOG.value = id;
					window.opener.document.forms[0].NOMCENTRO_GARALTSOG.value = denom;
					window.opener.document.forms[0].CENTROCOSTO_GARALTSOG.value = id + " - "+ denom;
				</c:when>
				<c:when test="${param.modalita eq 'altsog-popup'}">
					var row = ${param.row}
					window.opener.document.getElementById("centrocosto_"+row).value = id + " - "+ denom;
					row++;
					window.opener.document.getElementById("GARALTSOG_CODCENTRO_"+row).value= id;
					window.opener.document.getElementById("GARALTSOG_NOMCENTRO_"+row).value= denom;
				</c:when>
				<c:otherwise>
					window.opener.document.forms[0].TORN_CODCENTRO.value = id;
					window.opener.document.forms[0].TORN_NOMCENTRO.value = denom;
					window.opener.document.forms[0].CENTROCOSTO.value = id + " - "+ denom;
				</c:otherwise>
			</c:choose>
			window.close();
		}
		
		function annulla() {
			window.close();
		}
		
		function recuperaCC() {
				document.formRecuperaCC.submit();
				bloccaRichiesteServer();
		}

<c:if test="${not empty jsonConErrori}">
var jsonConErrori = ${jsonConErrori};
mapResponse(jsonConErrori);

function mapResponse(data){
	console.log(data)
	var anacErrors = data.anacErrors;
	var internalErrors = data.internalErrors;
	var pgErrors = data.pgErrors;
	var $elements = null;

	if(data.done=='N'){
		$elements = $("<li>").addClass('errori-javascript-err').text('ERRORE: ').append($("<small>").text("Si è verificato un errore nell'elaborazione della richiesta da parte di ANAC."));
	}
	if(anacErrors?.length>0){
		$elements = anacErrors.map((error) => $("<li>").addClass('errori-javascript-err').text('ERRORE: ').append($("<small>").text((error.codice=="ERR9999"? "" :error.codice+": ")+(error.dettaglio? error.dettaglio:""))));
	}
	if(internalErrors?.length>0){
		$elements = internalErrors.map((error) => $("<li>").addClass('errori-javascript-err').text('ERRORE: ').append($("<small>").text(error)));
	}
	if(pgErrors){
		$elements = $("<li>").addClass('errori-javascript-err').text('ERRORE: ').append($("<small>").text(pgErrors));
	}
	if(anacErrors?.length>0 || internalErrors?.length>0 || pgErrors || data.done=='N'){
		$('.errori-javascript-dettaglio').append($("<ul>").html($elements));
		$('.errori-javascript-dettaglio').append($("<br>"));
		var $divErr = $('.contenitore-errori-custom')
		$('.contenitore-errori-arealavoro').first().html($divErr);
	}
}
</c:if>
<c:if test="${not empty listaCCJson}">
$(window).on("load", function (){
	
	var _tableCentro = null;
	//$(".contenitore-dettaglio").width("980");
	_popolaCentri();

	$.fn.dataTable.ext.order['dom-checkbox'] = function (settings, col)
	{
		return this.api().column(col, {order:'index'}).nodes().map(function (td, i) {
			return $('input',td).prop('checked') ? '1' : '0';
		} );
	};
	
	function _creaTabellaCentri() {
		
		var _table = $('<table/>', {"id": "centri", "class": "schedagperm", "cellspacing": "0", "width" : "100%"});
		
		var _tr = $('<tr/>', {"class": "intestazione"});
		_tr.append('<th/>', {"class": "codice"});
		_tr.append('<th/>');
								
		var _thead = $('<thead/>');
		_thead.append(_tr);

		var _tbody = $('<tbody/>');
		
		var _tr2 = $('<tr/>', {"class": "intestazione"});
		_tr2.append('<td/>');
		_tr2.append('<td/>');

			
		var _tfoot = $('<tfoot/>');
		_tfoot.append(_tr2);

		_table.append(_thead);
		_table.append(_tbody);
		_table.append(_tfoot);
		
		$("#centriContainer").append(_table);	
		
	}

	/*
	 * Popola la tabella con la lista dei destinatari
	 */
	function _popolaCentri() {
		_creaTabellaCentri();
		var listaCentri = ${listaCCJson};
		console.log(listaCentri)
		_tableCentro = $('#centri').removeAttr('width').DataTable( {
			"data": listaCentri.centri,
			"bAutoWidth": false,
			
			"columnDefs": [
				{	
					"data": "denominazione",
					"visible": true,
					"sTitle": "Denominazione",
					"searchable": true,
					"targets": [ 0 ],
					"defaultContent": ""
				},
				{	
					"data": "id",
					"visible": true,
					"sTitle": "Centro di costo",
					"searchable": true,
					"targets": [ 1 ],
					"defaultContent": "",
					"render": function ( data, type, full, meta ) {
						var _span = $("<span/>");
						var _a = $("<a/>",{id: "id_" + full.id, href:"#", onclick:"getCC(this)", "text": full.id, "class": "link-generico"});
						_span.append(_a);
						return _span.html();
					}
				},
	        ],
	        "language": {
				"sEmptyTable":     "Nessun centro di costo attivo per la stazione appaltante",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ centri di costo",
				"sInfoEmpty":      "Nessun destinatario trovato",
				"sInfoFiltered":   "(su _MAX_ centri di costo)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca centri di costo",
				"sZeroRecords":    "Nessun centro di costo trovato",
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
					$("#centri tfoot").hide();
					$("#centri_info").hide();
					$("#centri_paginate").hide();
				}
			},
			
	        "pagingType": "full_numbers",
	        "lengthMenu": [[50, 70, 100], ["50 centri di costo", "70 centri di costo", "100 centri di costo"]],
	        "ordering": false,
	        "aoColumns": [
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true }
			   ]
	    });
			var columnNo = 0;
			$('#centri tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca denominazione"/>' );
			$('#centri tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca identificativo"/>' );
			
			_tableCentro.columns().eq(0).each( function (colIdx) {
				$('input', _tableCentro.column(colIdx).footer()).on( 'keyup change', function () {
					_tableCentro.column(colIdx).search(this.value).draw();
				});
		    });
		
		$("#centri_filter").hide();

		
	}
	
	
})
</c:if>

  
  


		
		
	
	</gene:javaScript>	
</gene:template>

</div>
