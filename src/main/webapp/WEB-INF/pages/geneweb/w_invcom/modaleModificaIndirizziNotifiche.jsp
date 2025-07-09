<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="codiceGara" value="${param.codiceGara}" />
<c:set var="isStipula" value="${param.isStipula}" />
<c:set var="idStipula" value="${param.idStipula}" />

<c:choose>
	<c:when test="${ empty isStipula }">
		<c:set var="isStipula" value="false" />
		<c:set var="codiceListaMail" value="${codiceGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceListaMail" value="${idStipula}" />
	</c:otherwise>
</c:choose>

<c:set var="nonUsato" value='${gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.GetListaMailUtentiGaraFunction", pageContext, codiceListaMail, isStipula)}' />

<style type="text/css">

	.rigaTabellaUtenti {
		border: #A0AABA 1px solid;
		background-color: #ffffff;
	}

	.rigaIntestazioneUtenti {
		background-color: #cce0ff;
		border: #A0AABA 1px solid;
	}

</style>

<div id="mascheraSelezionaDestinatari"  style="display:none; font:11px Verdana, Arial, Helvetica, sans-serif;" >

	<br style="font:5px Verdana, Arial, Helvetica, sans-serif;">
	
	<span>Mediante questa funzione è possibile impostare i destinatari delle notifiche per le comunicazioni ricevute, selezionandoli tra gli utenti che hanno privilegio di accesso alla procedura corrente e un indirizzo mail impostato.
	<br><br>Selezionare uno o più tra gli utenti elencati:</span><br><br>
	
	<table class="griglia" id="tabellaMailUtenti">
	<thead>
		<th class="rigaIntestazioneUtenti" style="width:20px;"></th>
		<th class="totale rigaIntestazioneUtenti" style="width:40%;"><span title="Nome utente">Nome utente</span></th>
		<th class="totale rigaIntestazioneUtenti"><span title="Email utente">Email utente</span></th>
	</thead>
	<c:forEach items="${requestScope.listaMailUtentiGara}" var="utenteDellaGara">
		<tr class="rigaTabellaUtenti">
			<td class="rigaTabellaUtenti">
				<input type="checkbox" value="${utenteDellaGara[1]}"/> 
			<td class="rigaTabellaUtenti">
				<span>&nbsp;&nbsp;${utenteDellaGara[0]}</span>
			</td>
			<td class="rigaTabellaUtenti">
				<span>&nbsp;&nbsp;${utenteDellaGara[1]}</span>
			</td>
		</tr>
	</c:forEach>
	</table>
	
</div>
<gene:javaScript>

function apriModaleModificaIndirizziNotifiche() {

	pingSessioneAttiva();

	var titolo="Selezione destinatari notifica comunicazioni ricevute";

	var widthM= 600;
	var	heightM= 450;

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
				id:"botSeleziona",
				text:"Conferma",
				click: function() {
					aggiungiDestinatariNotifiche();
				}
			 },
			"Annulla": {
				id:"botAnnulla",
				text:"Annulla",
				click:function() {
					$( this ).dialog( "close" );
				}
			 },
		}
	};
	$("#mascheraSelezionaDestinatari").dialog(opt).dialog("open");
	$('#tabellaMailUtenti').find('input[type=checkbox]').each(function(){
	    $(this).prop("checked", false);
	});
	$("#mascheraSelezionaDestinatari").parent().css("position","fixed");
	$('html, body').scrollTop($("#placeHolderModaleNotifiche").offset().top);
}

function aggiungiDestinatariNotifiche() {

	var entita = 'TORN';
	<c:if test="${ isStipula eq 'true' }">
		entita = 'G1STIPULA';
	</c:if>
	var indirizziNotifiche = $('#' + entita + '_INDNOTIFICHE').val();
	if (indirizziNotifiche != '') {
		indirizziNotifiche = indirizziNotifiche + ', ';
	}
	var indirizziDaAggiungere = '';
	$('#tabellaMailUtenti').find('input[type=checkbox]').each(function(){
	    if($(this).prop('checked') && !$(this).attr("disabled")) {
	    	indirizziDaAggiungere += $(this).val() + ', ';
	    }
	});
	if (indirizziDaAggiungere.length > 1){
		indirizziDaAggiungere = indirizziDaAggiungere.substring(0, indirizziDaAggiungere.length-2);
		$('#' + entita + '_INDNOTIFICHE').val(indirizziNotifiche + indirizziDaAggiungere);
		$('#botAnnulla').click();
	} else {
		alert('Nessun utente selezionato');
	}
}

$(document).ready(function() {
	$('#tabellaMailUtenti').find('input[type=checkbox]').each(function(){
	    if($(this).val() == '') {
	    	$(this).attr("disabled", true);
	    	$(this).prop("checked", false);
	    }
	});
});

</gene:javaScript>