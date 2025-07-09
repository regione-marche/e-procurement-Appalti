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
<input id="listaCategorie" type="hidden" value=""/>
<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<c:set var="nomeSA" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select nomein from uffint where codein=?", param.codein)}'/>
	<c:set var="isCUC" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select iscuc from uffint where codein=?", param.codein)}'/>
	
	<c:set var="bloccoAltrisogNonValorizzato" value="${isCUC eq '1' and empty param.modalita && gene:checkProt(pageContext, 'COLS.VIS.GARE.TORN.ALTRISOG')}"/>
	<c:set var="bloccoSANonValorizzata" value="${empty param.codein}"/>
	<c:set var="blocco" value="${bloccoAltrisogNonValorizzato eq 'true' or bloccoSANonValorizzata eq 'true'}"/>

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:setString name="titoloMaschera" value='Lista categorie' />
	
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

	<gene:setString name="titoloMaschera" value='Lista categorie' />
	<gene:redefineInsert name="corpo">
	<div id="categorieContainer" style="margin-left:8px; width: 98%"></div>
	<table class="datilista">
		<tr>
			<td colspan="4" class="comandi-dettaglio" style="text-align:right">
				<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">&nbsp;&nbsp;
				<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
			</td>
		</tr>
	</table>
	</gene:redefineInsert>
	
	<gene:javaScript>
	
	getCategorie();
	<c:set var="listaCategorie" value="JSON.parse($('#listaCategorie').val())"/>

	
	function annulla() {
		window.close();
	}
	
	function conferma() {
		$('#categorie').DataTable().columns().eq(0).each( function (colIdx) {
					$('#categorie').DataTable().column(colIdx).search('');
				})
		var tableResultXpage= $('#categorie').DataTable().page.info()?.length;
		$('#categorie').DataTable().page.len(-1).draw();
		
		var selezionati = $("input[type='checkbox']:checked");
		var selezionatiTxt = "";
		
		selezionati.each(function() {
			selezionatiTxt = selezionatiTxt + $(this).attr("id").split("_ck_pos_")[1] + ";";
		});
		selezionatiTxt = selezionatiTxt.substring(0, selezionatiTxt.length - 1);
		window.opener.document.getElementById("CODCAT").value = selezionatiTxt;
		window.close();
	}

<c:if test="${not empty listaCategorie}">
$(window).on("load", function (){
	
	

	var _tableCategoria = null;
	//$(".contenitore-dettaglio").width("980");
	_popolaCategorie();

	$.fn.dataTable.ext.order['dom-checkbox'] = function (settings, col)
	{
		return this.api().column(col, {order:'index'}).nodes().map(function (td, i) {
			return $('input',td).prop('checked') ? '1' : '0';
		} );
	};
	
	function _creaTabellaCategorie() {
		
		var _table = $('<table/>', {"id": "categorie", "class": "schedagperm", "cellspacing": "0", "width" : "100%"});
		
		var _tr = $('<tr/>', {"class": "intestazione"});
		_tr.append('<th/>', {"class": "codice"});
		_tr.append('<th/>');
		_tr.append('<th/>');
		var _thck = $('<th/>', {"id": "selpos","class": "ck"});
		_tr.append(_thck);
								
		var _thead = $('<thead/>');
		_thead.append(_tr);

		var _tbody = $('<tbody/>');
		
		var _tr2 = $('<tr/>', {"class": "intestazione"});
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
			
		var _tfoot = $('<tfoot/>');
		_tfoot.append(_tr2);

		_table.append(_thead);
		_table.append(_tbody);
		_table.append(_tfoot);
		
		$("#categorieContainer").append(_table);	
		
	}

	/*
	 * Popola la tabella con la lista dei destinatari
	 */
	function _popolaCategorie() {
		_creaTabellaCategorie();
		var listaCategorie = ${listaCategorie};
		//console.log(listaCategorie);
		_tableCategoria = $('#categorie').removeAttr('width').DataTable( {
			"data": listaCategorie.categorie,
			"bAutoWidth": false,
			
			"columnDefs": [
				{	
					"data": "caisim",
					"visible": true,
					"sTitle": "Codice categoria",
					"searchable": true,
					"targets": [ 0 ],
					"defaultContent": ""
				},
				{	
					"data": "descat",
					"visible": true,
					"sTitle": "Descrizione",
					"searchable": true,
					"targets": [ 1 ],
					"defaultContent": ""
				},
				{	
					"data": "tipgen",
					"visible": true,
					"sTitle": "Tipo",
					"searchable": true,
					"targets": [ 2 ],
					"defaultContent": ""
				},
				{
					"data": "checkPos",
					"visible": true,
					"sTitle": "Tipo",
					"targets": [ 3 ],
					"sWidth" : "70px",
					"render": function (data, type, full, meta ) {
						var _div = $("<div/>");
						var _check = $("<input/>",{"type":"checkbox", "id": "_ck_pos_" + full.caisim});
						_div.append(_check);
						return _div.html();
					}
				}
	        ],
	        "language": {
				"sEmptyTable":     "Nessun categoria trovata",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ categorie",
				"sInfoEmpty":      "Nessun destinatario trovato",
				"sInfoFiltered":   "(su _MAX_ categorie)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca categorie",
				"sZeroRecords":    "Nessuna categoria trovata",
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
					$("#categorie tfoot").hide();
					$("#categorie_info").hide();
					$("#categorie_paginate").hide();
				}
			},
			
	        "pagingType": "full_numbers",
	        "lengthMenu": [[20, 50, 100], ["20 categorie", "50 categorie", "100 categorie"]],
	        "ordering": false,
	        "aoColumns": [
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true }
			   ]
	    });
		
			var columnNo = 0;
			$('#categorie tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca codice categoria"/>' );
			$('#categorie tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca descrizione"/>' );
			$('#categorie tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca tipo"/>' );
			var _contextPath = "${pageContext.request.contextPath}";
			var _center = $("<center/>");
			var _href = "<a href='javascript:_selezionaTutti();' Title='Seleziona tutti'> <img src='"+_contextPath+"/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>";
			var _href = _href + "&nbsp;";
			var _href = _href + "<a href='javascript:_deselezionaTutti();' Title='Deseleziona tutti'><img src='"+_contextPath+"/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>";
			_center.append(_href);
			_center.appendTo($("#selpos"));
			
		
			_tableCategoria.columns().eq(0).each( function (colIdx) {
				$('input', _tableCategoria.column(colIdx).footer()).on( 'keyup change', function () {
					_tableCategoria.column(colIdx).search(this.value).draw();
				});
		    });
		
		$("#categorie_filter").hide();

	}
	
	

	
})

</c:if>
	
function _selezionaTutti() {
	$("input[id^='_ck_pos_']").prop("checked", true);
}

function _deselezionaTutti() {
	$("input[id^='_ck_pos_']").each(function(index) {
		if ($(this).prop("disabled")) {
			// Se l'input è disabilitato, non fare nulla
		} else {
			$(this).prop("checked", false);
		}
	});
}

function getCategorie(){
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/GetWSERPCategorie.do",
		data: "",
		success: function(rispostaJson) {
			//console.log(rispostaJson);
			$("#listaCategorie").val(JSON.stringify(rispostaJson));
		},
		error: function(e){
			alert("Errore durante il recupero delle categorie");
		}
	});
	
}
		
		
	
	</gene:javaScript>	
</gene:template>

</div>
