<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="nonUsato" value='${gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.GetListaEtichetteComunicazioniFunction", pageContext, "")}' />

<div id="mascheraAssegnaEtichetta"  style="display:none; font:11px Verdana, Arial, Helvetica, sans-serif;" >

	<br style="font:5px Verdana, Arial, Helvetica, sans-serif;">
	
	<span>Mediante questa funzione è possibile assegnare un'etichetta alla comunicazione.
	<br> Selezionare uno dei valori elencati sotto. 
	<br> Per rimuovere un'etichetta assegnata in precedenza, selezionare 'Nessuna etichetta'</span><br><br>

	&nbsp;<input type="radio" name="etichetteComunicazioni" value="nessunaEtichetta" /> <span>&nbsp;Nessuna etichetta</span> <br><br style="font:5px Verdana, Arial, Helvetica, sans-serif;">

	<c:forEach items="${requestScope.listaEtichette}" var="oggettoEtichetta">
		&nbsp;<input type="radio" name="etichetteComunicazioni" value="${oggettoEtichetta[0]}" /> <span>&nbsp;${oggettoEtichetta[1]}</span> <br><br style="font:5px Verdana, Arial, Helvetica, sans-serif;">
	</c:forEach>

</div>
<gene:javaScript>

function apriModaleAssegnaEtichetta(chiaveRiga,etichettaComunicazione) {

	pingSessioneAttiva();

	var titolo="Assegna etichetta alla comunicazione";

	var widthM= 500;
	var	heightM= 350;

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
		position: { using: function( pos ) {
            $( this ).css({
				top:'50%',
				left: '50%',
				marginRight: '50%',
				transform: 'translate(-50%, -50%)'
			});
      	}},
		autoOpen: false,
		modal: true,
		width: widthM,
		height:heightM,
		draggable: false,
		resizable: false,
		title: titolo,
		buttons: {
			"Assegna": {
				id:"botAssegna",
				text:"Conferma",
				click: function() {
					assegnaEtichetta(chiaveRiga);
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
	$("#mascheraAssegnaEtichetta").dialog(opt).dialog("open");
	clickEtichettaPreimpostata(etichettaComunicazione);
}

function assegnaEtichetta(chiaveRiga){
	bloccaRichiesteServer();
	var chiaveINVCOM = chiaveRiga.split(";");
	var idPrg= chiaveINVCOM[0].substring(chiaveINVCOM[0].indexOf(":")+1);
	var idCom = chiaveINVCOM[1].substring(chiaveINVCOM[1].indexOf(":")+1);
	var nuovaEtichettaSelezionata = $('input:radio[name=etichetteComunicazioni]').filter(':checked').val();

	//alert('idPrg: ' + idPrg + '  idCom: ' + idCom + '  etichetta: ' + nuovaEtichettaSelezionata);

	assegnaEtichettaAJAX(idPrg,idCom,nuovaEtichettaSelezionata);

	window.location=contextPath+'/History.do?metodo=reload&numeroPopUp='+getNumeroPopUp();
}

function assegnaEtichettaAJAX(idPrg,idCom,nuovaEtichettaSelezionata) {
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/AssegnaEtichettaComunicazioneAJAX.do?idPrg=" + idPrg + "&idCom=" + idCom + "&etichetta=" + nuovaEtichettaSelezionata,
		error: function(e){
			alert("Errore generico durante l'operazione");
		}
	});
}

function clickEtichettaPreimpostata(etichettaComunicazione){
	var etichettaComunicazioneClick = '';
	if (etichettaComunicazione == '') {
		etichettaComunicazioneClick = 'nessunaEtichetta';
	} else {
		etichettaComunicazioneClick = etichettaComunicazione;
	}
	$('input:radio[name=etichetteComunicazioni]').filter('[value=' + etichettaComunicazioneClick + ']').click();
}

</gene:javaScript>