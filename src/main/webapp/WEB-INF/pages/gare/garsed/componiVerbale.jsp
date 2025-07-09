<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="urlPubblica" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "portaleAppalti.urlPubblica")}'/>
<c:set var="idUtente" value="${ profiloUtente.id }" />
<c:set var="nomeFileModello" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "verbaleGara.nomeModello")}'/>
<c:set var="select" value="select id_modello from W_MODELLI where nomefile=?" />
<c:set var="idModello" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, select, nomeFileModello)}'/>
<c:set var="select" value="select nome from W_MODELLI where nomefile=?" />
<c:set var="nomeModello" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, select, nomeFileModello)}'/>
<c:set var="numeroGara" value='${gene:getValCampo(key, "GARSED.NGARA")}' />
<c:set var="numeroGara" value="${param.numeroGara}" />
<c:set var="numeroSeduta" value="${param.numeroSeduta}" />
<c:set var="select" value="select fasgar from gare where ngara=?" />
<c:set var="faseGaraTORN" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, select, numeroGara)}'/>
<c:set var="select" value="select fase from garsed where ngara=?" />
<c:set var="faseGARSED" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, select, numeroGara)}'/>

<div id="mascheraComponiVerbale"  style="display:none;">

	<br style="font:8px Verdana, Arial, Helvetica, sans-serif;">
	<span id="sezioneIniziale">
		Mediante questa funzione di procede alla composizione del verbale della seduta di gara. <br>
		Sotto sono riportate le opzioni di stampa attinenti alla procedura e alla sua fase di espletamento.
		Per visualizzare tutte le opzioni di stampa disponibili selezionare l'opzione 'Mostra tutte le scelte' che compare in coda.
		<br><br>
	</span>

	<table class="modale" id="tabellaComponiVerbale" style="font:11px Verdana, Arial, Helvetica, sans-serif;">
	<input type="hidden" name="urlPubblica" id="urlPubblica" value="${ urlPubblica }" />

	<div id="sezioneNessunaScelta" style="display: none;">
		<span><b>&nbsp;Nessuna opzione di stampa selezionabile per la gara corrente</b></span>
		<br><br>
	</div>

	<tr id="sezioneDomande">
		<td colspan="2">
			<h4><span style="display: none;">&#9656</span><span>&#9662</span> Sezione Domande di partecipazione</h4>
			<div>	
				<div>
					&nbsp;&nbsp;&nbsp;  Stampa elenco plichi?  &nbsp;
					<input type="radio" name="elencoPlichiD" id="elencoPlichiD" value="1" />&nbsp;sì &nbsp;
					<input type="radio" name="elencoPlichiD" id="elencoPlichiD" value="2" />&nbsp;no 
					<br><br>
				</div> 
				<div>
					&nbsp;&nbsp;&nbsp;  Stampa elenco documenti?  &nbsp;
					<input type="radio" name="elencoDocumentiD" id="elencoDocumentiD" value="1" />&nbsp;sì &nbsp;
					<input type="radio" name="elencoDocumentiD" id="elencoDocumentiD" value="2" />&nbsp;no
					<br><br>
				</div> 
				<div>
					&nbsp;&nbsp;&nbsp;  Stampa elenco ammessi/esclusi?  &nbsp;
					<input type="radio" name="elencoAmmissioniD" id="elencoAmmissioniD" value="1" />&nbsp;sì &nbsp;
					<input type="radio"	name="elencoAmmissioniD" id="elencoAmmissioniD" value="2" />&nbsp;no
					<br><br>
				</div> 
				<div>
					&nbsp;&nbsp;&nbsp;  Stampa elenco ditte da invitare?  &nbsp;
					<input type="radio" name="elencoInvitate" id="elencoInvitate" value="1" />&nbsp;sì &nbsp;
					<input type="radio" name="elencoInvitate" id="elencoInvitate" value="2" />&nbsp;no 
					<br><br>
				</div>
			</div>
			<br style="font:4px Verdana, Arial, Helvetica, sans-serif;">
		</td>
	</tr>
	<tr id="sezioneOfferte">
		<td colspan="2">
			<h4><span style="display: none;">&#9656</span><span>&#9662</span> Sezione Ricezione offerte</h4>
			<div>
				<div>
					&nbsp;&nbsp;&nbsp;  Stampa elenco plichi?  &nbsp;
					<input type="radio" name="soloElencoPlichiO" id="soloElencoPlichiO" value="1" />&nbsp;sì &nbsp;
					<input type="radio" name="soloElencoPlichiO" id="soloElencoPlichiO" value="2" />&nbsp;no 
					<br><br>
				</div> 
				<div>
					&nbsp;&nbsp;&nbsp;  Stampa ditte estratte per sorteggio verifica req.?  &nbsp;
					<input type="radio" name="ditteEstratte" id="ditteEstratte" value="1" />&nbsp;sì &nbsp;
					<input type="radio" name="ditteEstratte" id="ditteEstratte" value="2" />&nbsp;no
					<br><br>
				</div> 
				<div>
					&nbsp;&nbsp;&nbsp;  Stampa elenco documenti?  &nbsp;
					<input type="radio" name="elencoDocumentiO" id="elencoDocumentiO" value="1" />&nbsp;sì &nbsp;
					<input type="radio" name="elencoDocumentiO" id="elencoDocumentiO" value="2" />&nbsp;no
					<br><br>
				</div> 
				<div>
					&nbsp;&nbsp;&nbsp;  Stampa elenco ammessi/esclusi?  &nbsp;
					<input type="radio" name="elencoAmmissioniO" id="elencoAmmissioniO" value="1" />&nbsp;sì &nbsp;
					<input type="radio"	name="elencoAmmissioniO" id="elencoAmmissioniO" value="2" />&nbsp;no
					<br><br>
				</div> 
			</div> 
			<br style="font:4px Verdana, Arial, Helvetica, sans-serif;">
		</td>
	</tr>
	<tr id="sezioneValutazioneTecnica">
		<td colspan="2">
			<h4><span style="display: none;">&#9656</span><span>&#9662</span> Sezione Valutazione tecnica</h4>
			<div>
				<div>
					&nbsp;&nbsp;&nbsp;  Stampa elenco documenti?  &nbsp;
					<input type="radio" name="elencoDocumentiT" id="elencoDocumentiT" value="1" />&nbsp;sì &nbsp;
					<input type="radio" name="elencoDocumentiT" id="elencoDocumentiT" value="2" />&nbsp;no
					<br><br>
				</div> 
				<div>
					&nbsp;&nbsp;&nbsp;  Stampa esito valutazioni tecniche/punteggi?  &nbsp;
					<input type="radio" name="valutazioniTecniche" id="valutazioniTecniche" value="1" />&nbsp;sì &nbsp;
					<input type="radio"	name="valutazioniTecniche" id="valutazioniTecniche" value="2" />&nbsp;no
					<br><br>
				</div>
			</div>
			<br style="font:4px Verdana, Arial, Helvetica, sans-serif;">
		</td>
	</tr>
	<tr id="sezioneValutazioneEconomica">
		<td colspan="2">
			<h4><span style="display: none;">&#9656</span><span>&#9662</span> Sezione Apertura offerte economiche</h4>
			<div>
				<div>
					&nbsp;&nbsp;&nbsp;  Stampa elenco documenti?  &nbsp;
					<input type="radio" name="elencoDocumentiE" id="elencoDocumentiE" value="1" />&nbsp;sì &nbsp;
					<input type="radio" name="elencoDocumentiE" id="elencoDocumentiE" value="2" />&nbsp;no
					<br><br>
				</div> 
				<div>
					&nbsp;&nbsp;&nbsp; Stampa offerte/punteggi?  &nbsp;
					<input type="radio" name="offertePunteggi" id="offertePunteggi" value="1" />&nbsp;sì &nbsp;
					<input type="radio"	name="offertePunteggi" id="offertePunteggi" value="2" />&nbsp;no
					<br><br>
				</div> 
			</div>
			<br style="font:5px Verdana, Arial, Helvetica, sans-serif;">
		</td>
	</tr>
	<tr id="sezioneAggiudicazione">
		<td colspan="2">
			<h4><span style="display: none;">&#9656</span><span>&#9662</span> Sezione Proposta di aggiudicazione</h4>
			<div>
				<div>
					&nbsp;&nbsp;&nbsp; Stampa graduatoria? &nbsp;
					<input type="radio" name="graduatoria" id="graduatoria" value="1" />&nbsp;sì &nbsp;
					<input type="radio"	name="graduatoria" id="graduatoria" value="2" />&nbsp;no
					<br><br><br>
				</div>
			</div>
			<br style="font:4px Verdana, Arial, Helvetica, sans-serif;">
		</td>
	</tr>
	
	<tr id="sezioneMostraTutteLeScelte">
		<td colspan="2">
			<div>
				&nbsp; Mostra tutte le scelte &nbsp;
				<input type="radio" name="mostraTutteLeScelte" id="mostraTutteLeScelte" value="1" />&nbsp;sì &nbsp;
				<input type="radio" name="mostraTutteLeScelte" id="mostraTutteLeScelteNo" value="2" />&nbsp;no
			</div>
			<br style="font:4px Verdana, Arial, Helvetica, sans-serif;">
		</td>
	</tr>
	<tr id="sezioneStampaTutto">
		<td colspan="2">
			<div>
				&nbsp; Ignora le scelte e stampa tutto?  &nbsp;
				<input type="radio" name="stampaTutto" id="stampaTutto" value="1" />&nbsp;sì &nbsp;
				<input type="radio" name="stampaTutto" id="stampaTutto" value="2" />&nbsp;no
			</div>
		</td>
	</tr>
	</table>

	<div id="sezioneErrore" style="display: none; font:11px Verdana, Arial, Helvetica, sans-serif;">
		<br style="font:5px Verdana, Arial, Helvetica, sans-serif;">
		<span><b>Non è possibile procedere con la composizione del verbale di gara. </b>
		&nbsp;La fase della seduta di gara non risulta valorizzata.</span>
		
	</div>
</div>

<gene:javaScript>

$(document).ready(function() {

	impostaParametriCache();

	$("h4").parent().children().show();

	impostaNascoste();

	$("h4").click(
		function(){
		$(this).parent().children().toggle();
		$(this).show();
		$(this).children().toggle();
	});

	$("h4").mouseenter(function(){
	  $(this).css("color", "#003399");
	});
	$("h4").mouseleave(function(){
	  $(this).css("color", "black");
	});

	$("#mostraTutteLeScelte").click(function(){
		mostraONascondiLeScelte();
	});

	$("#mostraTutteLeScelteNo").click(function(){
		mostraONascondiLeScelte();
	});	
});

function checkFasi() {

	if ('' == '${ faseGARSED }') {

		$('#sezioneNessunaScelta').hide();
		$('#sezioneErrore').show();
		$("#tabellaComponiVerbale").hide();
		$("#mostraTutteLeScelte").hide();
		$("#mostraTutteLeScelte").next().hide();

		$("#botComponi").hide();
		$("#botReimposta").hide();
		$("#botAnnulla").hide();
		$("#botChiudi").show();
	}
}

function apriModaleComponiVerbale() {

	pingSessioneAttiva();

	var titolo="Componi verbale seduta di gara";

	var widthM= 600;
	var	heightM= 500;

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
			"Componi": {
				id:"botComponi",
				text:"Componi",
				click: function() {
					apriPopupVerbale();
					$( this ).dialog( "close" );
				}
			 },
			 "Reimposta": {
				id:"botReimposta",
				text:"Reimposta",
				click:function() {
					reimpostaParametri();
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
					$( this ).dialog( "close" );
				}
			}
		}
	};
	$("#mascheraComponiVerbale").dialog(opt).dialog("open");
	$('input:radio[name=mostraTutteLeScelte]').filter('[value=2]').click();
	$("#sezioneIniziale").show();
	checkFasi();
	if ($('#sezioneErrore').is(":visible")){
		$("#botChiudi").show();
		$("#botAnnulla").hide();
		$("#botComponi").hide();
		$("#botReimposta").hide();
	}
	else{
		$("#botChiudi").hide();
	}
}

const elencoParametriNomi = new Array ( 'stampaTutto','elencoPlichiD','elencoDocumentiD','elencoAmmissioniD','elencoInvitate','soloElencoPlichiO','ditteEstratte',
	'elencoDocumentiO','elencoAmmissioniO','elencoDocumentiT','valutazioniTecniche','elencoDocumentiE','offertePunteggi','graduatoria'
);

const elencoSezioniID = new Array ('sezioneDomande','sezioneOfferte','sezioneValutazioneTecnica','sezioneValutazioneEconomica','sezioneAggiudicazione');

const elencoParametriCache = new Array(${gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.GetListaParametriPredefinitiComposizioneFunction", pageContext, "")});

const listaVisibili = new Array(${ gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.GetSezioniVisibiliComponiVerbaleFunction", pageContext, numeroGara, numeroSeduta )});

const scelteVisibili = listaVisibili.slice(5, listaVisibili.lenght);
const sezioniVisibili = listaVisibili.slice(0, 5);

function mostraONascondiLeScelte(){
	if ($('input:radio[name=mostraTutteLeScelte]').filter(':checked').val() == '1'){
		impostaVisibili();
	} else {
		impostaNascoste();
	}
}

function impostaNascoste() {
	tuttoNascosto = 'true';
	for (let i = 0; i < elencoSezioniID.length; i++) {
		if (sezioniVisibili[i] == '2') {
			$('#'+elencoSezioniID[i]).hide();
		} else {
			tuttoNascosto = 'false';
		}
	}
	for (let i = 0; i < scelteVisibili.length; i++) {
		if (scelteVisibili[i] == '2') {
			$('#'+elencoParametriNomi[i+1]).parent().hide();
		}
	}
	$('#'+elencoParametriNomi[0]).parent().hide();
	if (tuttoNascosto == 'true') {
		$('#sezioneNessunaScelta').show();
	}
}

function impostaVisibili() {
	for (let i = 0; i < elencoSezioniID.length; i++) {
		$('#'+elencoSezioniID[i]).show();
	}
	for (let i = 0; i < scelteVisibili.length; i++) {
		$('#'+elencoParametriNomi[i+1]).parent().show();
	}
	$('#sezioneNessunaScelta').hide();
	$('#'+elencoParametriNomi[0]).parent().show();
}

function impostaParametriCache() {

	$('#urlPubblica').val('${ urlPubblica }');
	$('#ignoraEStampaTutto').val('2');

	for (let i = 0; i < elencoParametriNomi.length; i++) {
		$('input:radio[name=' + elencoParametriNomi[i] + ']').filter('[value=' + elencoParametriCache[i] + ']').click();
	}
}

function reimpostaParametri() {

	for (let i = 0; i < elencoParametriNomi.length; i++) {
		$('input:radio[name=' + elencoParametriNomi[i] + ']').filter('[value=2]').click();
	}

}

function estraiParametri(){

	var ret = '&numeroGara=${numeroGara}&numeroSeduta=${numeroSeduta}';

	if (!$("#mostraTutteLeScelte").is(':checked')){
		for (let i = 0; i < scelteVisibili.length; i++) {
			if (scelteVisibili[i] == '2') {
				$('input:radio[name=' + elencoParametriNomi[i+1] + ']').filter('[value=2]').click();
			}
		}
		$('input:radio[name=' + elencoParametriNomi[0] + ']').filter('[value=2]').click();
	}
	elencoParametriNomi.forEach((nomeCampo) => {
		var valoreDaImpostare = $('input:radio[name=' + nomeCampo + ']').filter(':checked').val();
		if (valoreDaImpostare == null || valoreDaImpostare == '') {
			valoreDaImpostare = '2';
		}
    	ret = ret + '&' + nomeCampo + '=' + valoreDaImpostare;
	});
	return ret;
}

function apriPopupVerbale(){
	var href = "href=gare/garsed/popupComponiVerbale.jsp&numeroPopUp=1";
	openPopUpCustom(href + estraiParametri(), "apriPopupVerbale", "700", "500", "no", "no");
}

</gene:javaScript>