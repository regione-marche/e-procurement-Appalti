<%
/*
 * Created on: 31-08-2010
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
		Finestra per l'attivazione della funzione 'Pubblica su portale Alice Gare'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="popup-message-template.jsp">

<c:set var="id" value="${param.id}" scope="request" />
<c:set var="provenienza" value="${param.provenienza}" scope="request" />
<c:set var="idcomunicazione" value="${param.idcomunicazione}" scope="request" />

<gene:redefineInsert name="head" >

	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easytabs.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wserpsupporto.js"></script>
	
</gene:redefineInsert>

<gene:redefineInsert name="corpo">

<gene:setString name="titoloMaschera" value='Seleziona documento da oda' />


<div style="display: none;" class="error" id="documentifascicolomessaggio"></div>
<div style="display: none;" class="error" id="wserpdocumentomessaggio"></div>

<div id="tabs-allegati">
	<div id="allegaticontainer"></div>
	<div style="display: none;" class="error" id="allegatimessaggio"></div>
</div>
<input type="hidden" name="id" id="id" value="${param.id}" />
<input type="hidden" name="provenienza" id="provenienza" value="${param.provenienza}" />
<input type="hidden" name="idcomunicazione" id="idcomunicazione" value="${param.idcomunicazione}" />
			
</gene:redefineInsert>
<gene:redefineInsert name="buttons">
	<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla()">&nbsp;
</gene:redefineInsert>

<gene:javaScript>
	
	/*
     * Gestione utente ed attributi per il collegamento remoto
     */
	 var id = '${id}';
	 var _tableAllegati = null;
	
	_getWSERPAllegati(id);
	

function selectFile(codiceOda,nomeallegato,idFile){
	window.opener.popolaFormWSERP(codiceOda,nomeallegato,idFile);
	window.close();
}

function _getWSERPAllegati(id,callback) {
	
	
	var callbackFunction = function(json){
		if (json.esito == true) {
			_popolaTabellaAllegati(json.iTotalRecordsALLEGATI, json.dataALLEGATI);

		} else {
			//_removeDettaglioDocumento();
			if(!json.messaggio=="undefined"){
				var messaggio = "" + json.messaggio;	
			}
			$('#wserpdocumentomessaggio').text(messaggio);
			$('#wserpdocumentomessaggio').css("color","#000000");
			$('#wserpdocumentomessaggio').show(tempo);
		}
		if (callback && typeof callback === 'function'){
			callback(json.esito);
		}
	};
	
	_wait();
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: true,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		"url": "pg/GetWSERPListaAllegatiOda.do",
		"data" : {
			id:id
		},
		success: callbackFunction,
		error: function(e){
			_removeDettaglioDocumento();
			var messaggio = "Errore durante la lettura del documento";
			$('#wserpdocumentomessaggio').text(messaggio);
			$('#wserpdocumentomessaggio').show(tempo);
		},
		complete: function() {
			//alert('complete');
			_nowait();
	    }
	});
}

	
function _popolaTabellaAllegati(numeroTotale, allegati) {
	if (_tableAllegati != null) {
		_tableAllegati.destroy(true);
	}
	
	if (numeroTotale == 0) {
		$("#li-allegati").hide();
	} else {
		$("#li-allegati").show();
	}
	
	var _table = $('<table/>', {"id": "allegati", "class": "elementi", "cellspacing": "0", "width" : "100%"});
	var _thead = $('<thead/>');
	var _tr = $('<tr/>');
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_tr.append($('<th/>'));
	_thead.append(_tr);
	_table.append(_thead);
	var _tbody = $('<tbody/>');
	_table.append(_tbody);
	$("#allegaticontainer").append(_table);
	
	_tableAllegati = $('#allegati').DataTable( {
		"data": allegati,
		"columnDefs": [
			{
				"data": "titoloallegato",
				"visible": true,
				"sWidth": "50%",
				"sTitle": "Codice Oda",
				"targets": [ 0 ]
			},
			{
				"data": "nomeallegato",
				"visible": true,
				"sTitle": "Nome documento",
				"targets": [ 1 ],
				"render": function ( data, type, full, meta ) {
					var _span = $("<span/>");
					var _numeroRda = $("#numeroRda").text();
					var _href = "javascript:selectFile('" + full.titoloallegato + "','" + full.nomeallegato + "','" + full.idfile + "');";	
					if(${provenienza eq 'comunicazione'}){
						_href = "javascript:insertAllegatoOda('" + full.titoloallegato + "','" + full.nomeallegato + "');";
					}
					var _a = $("<a/>",{href: _href, "text": full.nomeallegato, "class": "link-generico"});
					_span.append(_a);
					return _span.html();
				}
			},
			{
				"data": "tipoallegato",
				"visible": false,
				"sTitle": "Tipo",
				"targets": [ 2 ]
			},
			{
				"data": "idfile",
				"visible": false,
				"sTitle": "IdFile",
				"targets": [ 3 ]
			}
			
        ],
        "language": {
			"sEmptyTable":     "Nessun allegato trovato",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ allegati",
			"sInfoEmpty":      "Nessun allegato trovato",
			"sInfoFiltered":   "(su _MAX_ allegati totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_ allegati",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sSearch":         "Cerca allegati",
			"sZeroRecords":    "Nessun allegato trovato",
			"oPaginate": {
				"sFirst":      "Prima",
				"sPrevious":   "Precedente",
				"sNext":       "Successiva",
				"sLast":       "Ultima"
			}
		},
		"lengthMenu": [[10, 25, 50, 100, 200], ["10", "25", "50", "100", "200"]],
        "pagingType": "full_numbers",
        "bLengthChange" : false,
		"searching": false,
        "order": [[ 0, "desc" ]],
        "aoColumns": [
		     { "bSortable": true},
		     { "bSortable": true},
		     { "bSortable": true}
		   ]
    });

	if (numeroTotale == 0) {
		$("#allegati tfoot").hide();
		$("#allegati_info").hide();
		$("#allegati_paginate").hide();
	}

}
	
	function annulla(){
		window.close();
	}
	
	function insertAllegatoOda(descrizioneFile, nomeFile){
		_wait();
		$.ajax({
		type: "POST",
		dataType: "json",
		async: true,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		"url": "pg/InserisciAllegatoOdaComunicazioni.do",
		"data" : {
			descrizioneFile:descrizioneFile,
			nomeFile:nomeFile,
			codgar:'${id}',
			idcom:'${idcomunicazione}'
		},
		success: function(data) {
			window.opener.listaVaiAPagina(0);
			window.close();
		},
		error: function() {
			alert("Errore nel caricamento della tabella");
		},
		complete: function() {
			_nowait();
	    }
	});
		
		
	}
	
	
	</gene:javaScript>
	
</gene:template>

