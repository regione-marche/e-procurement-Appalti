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

<c:set var="codiceGara" value="${param.codiceGara}" scope="request" />
<c:set var="indice" value="${param.indice}" scope="request" />
<c:set var="numerowsdocumento" value="${param.numerowsdocumento}" scope="request" />
<c:set var="idconfi" value="${param.idconfi}" scope="request" />

<gene:redefineInsert name="head" >

	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easytabs.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
	
</gene:redefineInsert>

<gene:redefineInsert name="corpo">

<gene:setString name="titoloMaschera" value='Seleziona documento' />

<c:set var="urlFascicolo" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", "wsdm.fascicoloprotocollo.url",idconfi)}'/>
<c:set var="urlDocumentale" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", "wsdm.documentale.url",idconfi)}'/>

<c:choose>
	<c:when test="${!empty urlFascicolo and urlFascicolo!=''}">
		<c:set var="servizio" value="FASCICOLOPROTOCOLLO"/>
	</c:when>
	<c:otherwise>
		<c:set var="servizio" value="DOCUMENTALE"/>
	</c:otherwise>
</c:choose>

<form id="parametririchiestafascicolo">
	<table class="dettaglio-notab">	
		<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
		<input id="servizio" type="hidden" value="${servizio}" />
		<input id="tiposistemaremoto" type="hidden" value="" />
		<input id="tabellatiInDB" type="hidden" value="" />
		<input id="modoapertura" type="hidden" value="VISUALIZZA" /> 
		<input id="entita" type="hidden" value="${param.entita}" /> 
		<input id="key1" type="hidden" value="${param.key1}" /> 
		<input id="key2" type="hidden" value="${param.key2}" />
		<input id="key3" type="hidden" value="${param.key3}" />
		<input id="key4" type="hidden" value="${param.key4}" />
		<input id="idconfi" type="hidden" value="${idconfi}" />
		<input id="autorizzatoModifiche" type="hidden" value="${param.autorizzatoModifiche}" />
		<input id="autorizzatoAssociaFascicolo" type="hidden" value='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.WSDM-scheda.AssociaFascicoloEsistente")}' />
		<input id="classificafascicolonuovo" type="hidden" value=""/>
		
		<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp"></jsp:include>
		
		<tr style="display: none;">
			<td colspan="2"><br><b>Dati del fascicolo</b></td>
		</tr>
		<tr style="display: none;">
			<td class="etichetta-dato">Anno fascicolo</td>
			<td class="valore-dato"><input id="annofascicolo" name="annofascicolo" title="Anno fascicolo" class="testo" type="text" size="6" value="" maxlength="4">
			<span id="spanannofascicolo" name="spanannofascicolo" title="Anno fascicolo" style="display: none;"></span></td>
		</tr>
		<tr style="display: none;">
			<td class="etichetta-dato">Numero fascicolo</td>
			<td class="valore-dato"><input id="numerofascicolo" name="numerofascicolo" title="Numero fascicolo" class="testo" type="text" size="24" value="" maxlength="100">
			<span id="spannumerofascicolo" name="spannumerofascicolo" title="Numero fascicolo" style="display: none;"></span></td>
		</tr>
		<tr style="display: none;">
			<td class="etichetta-dato">Codice fascicolo</td>
			<td class="valore-dato"><input id="codicefascicolo" name="codicefascicolo" title="Codice fascicolo" class="testo" type="text" size="40" value="" maxlength="100"></td>
		</tr>
		<tr style="display: none;">
			<td class="etichetta-dato">Oggetto</td>
			<td class="valore-dato"><span id="oggettofascicolo" name="oggettofascicolo" title="Oggetto"></span></td>
		</tr>	
		<tr style="display: none;">
			<td class="etichetta-dato">Classifica</td>
			<td class="valore-dato"><span id="classificafascicolodescrizione" name="classificafascicolodescrizione" title="Classifica"></span></td>
		</tr>
		<tr style="display: none;">
			<td class="etichetta-dato">Descrizione</td>
			<td class="valore-dato"><span id="descrizionefascicolo" name="descrizionefascicolo" title="Descrizione"></span></td>
		</tr>
		<tr style="display: none;">
			<td class="etichetta-dato">Codice Aoo</td>
			<td class="valore-dato"><input id="codiceaoonuovo" name="codiceaoonuovo" title="Codice aoo" class="testo" type="text" size="6" value="" maxlength="4"></td>
		</tr>
		<tr style="display: none;">
			<td class="etichetta-dato">Codice ufficio</td>
			<td class="valore-dato"><input id="codiceufficionuovo" name="codiceufficionuovo" title="Codice ufficio" class="testo" type="text" size="6" value="" maxlength="4"></td>
		</tr>
		<tr>
			<td class="etichetta">Oggetto</td>
			<td class="valore">
				<span id="oggettodocumento"></span>
			</td>						
		</tr>
		<tr>
			<td class="etichetta">Numero documento</td>
			<td class="valore">
				<span id="numerodocumento"></span>
			</td>						
		</tr>
		<tr>
			<td class="etichetta">Anno protocollo</td>
			<td class="valore">
				<span id="annoprotocollo"></span>
			</td>						
		</tr>
		<tr>
			<td class="etichetta">Numero protocollo</td>
			<td class="valore">
				<span id="numeroprotocollo"></span>
			</td>						
		</tr>	
		<tr>
			<td class="etichetta">Ingresso/uscita</td>
			<td class="valore">
				<span id="inout"></span>
			</td>						
		</tr>	
	</table>
</form>

<div style="display: none;" class="error" id="documentifascicolomessaggio"></div>
<div style="display: none;" class="error" id="wsdmdocumentomessaggio"></div>

<div id="tabs-allegati">
	<div id="allegaticontainer"></div>
	<div style="display: none;" class="error" id="allegatimessaggio"></div>
</div>

</gene:redefineInsert>
<gene:redefineInsert name="buttons">
	<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
</gene:redefineInsert>

<gene:javaScript>
	
	/*
     * Gestione utente ed attributi per il collegamento remoto
     */
	 _wait();
	var numerowsdocumento = '${numerowsdocumento}';
	_getWSTipoSistemaRemoto();
	_popolaTabellato("ruolo","ruolo");
	_popolaTabellato("codiceuo","codiceuo");
	_getWSLogin();
	_gestioneWSLogin();
	_getWSDMDocumento(numerowsdocumento);
	$("#parametririchiestafascicolo").hide();
	
	

function selectFile(annoprotocollo,numeroprotocollo,nomeallegato,tipoallegato,numerodocumento){
	var indice = "${indice}";
	
	var username = $("#username").val();
	var password =$("#password").val();
	var ruolo =$("#ruolo option:selected").val();
	var nome =$("#nome").val();
	var cognome =$("#cognome").val();
	var codiceuo =$("#codiceuo option:selected").val();
	var idutente =$("#idutente").val();
	var idutenteunop =$("#idutenteunop").val();
	var nomeallegato =nomeallegato;
	var tipoallegato =tipoallegato;
	var servizio = $("#servizio").val();
	var idconfi = $("#idconfi").val();
	
	window.opener.popolaFormDocumentale(indice,username,password,ruolo,nome,cognome,codiceuo,idutente,idutenteunop,annoprotocollo,numeroprotocollo,nomeallegato,tipoallegato,numerodocumento,servizio,idconfi);
	window.close();
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
				"sTitle": "Titolo",
				"targets": [ 0 ]
			},
			{
				"data": "nomeallegato",
				"visible": true,
				"sTitle": "Nome documento",
				"targets": [ 1 ],
				"render": function ( data, type, full, meta ) {
					var _span = $("<span/>");
					var _numerodocumento = $("#numerodocumento").text();
					var _annoprotocollo = $("#annoprotocollo").text();
					var _numeroprotocollo = $("#numeroprotocollo").text();
					var href;
					if ($("#tiposistemaremoto").val() == "JIRIDE") {
							_href = "javascript:selectFile('" + _annoprotocollo + "','" + _numeroprotocollo + "','" + full.nomeallegato + "','" + full.tipoallegato + "','" + _numerodocumento + "');";	
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
	
	</gene:javaScript>
	
</gene:template>

