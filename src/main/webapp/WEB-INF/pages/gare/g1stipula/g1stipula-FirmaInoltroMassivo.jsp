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

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<c:set var="title" value="Inoltra e firma documenti stipule"/>

<style type="text/css">
			.ui-dialog-titlebar {
				display: none;
			}
			#sottoTitolo{
				font:11px Verdana, Arial, Helvetica, sans-serif;
			}
</style>
<div id="dialog-form-inoltra-firma" style="display:none">
	<div id="dialog-form-inoltra" >
	  <b>${title}</b>
	  <br>
	  <p class="validateTips">Mediante questa funzione si procede all'inoltro per approvazione dei contratti selezionati. Confermi l'operazione?</p>
	  <form>
		  <table class="dati-login" style="width:auto!important;">
			<tr>
				<td class="etichetta-dato">
				Inoltra al Contract Manager:
				</td>
				<td class="valore-dato">
					<input type="radio" name="sendTo" value="originatario" checked="checked" />Originario
					<input type="radio" name="sendTo" value="precedente" />Precedente
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">&nbsp;&nbsp;</td>
				<td class="valore-dato">&nbsp;&nbsp;</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Eseguire anche la firma
					dei documenti?
				</td>
				<td class="valore-dato"><input type="checkbox"
					id="checkisVisibleFormFirma" checked="checked"
					onchange="javascript:isVisibleFormFirma();" />
				</td>
			</tr>
		  </table>
		  <input type="hidden" name="withFirma" id="withFirma" value="si" />
	  </form>
	</div>
			
	<div id="dialog-form-firma" style="display:block">
	  <br>
		<span id="sottoTitolo">Documenti da firmare: <span id='numeroDocu'> </span></span>
	  <br>
	  
		<span id="infoMessagesDocDaFirmare" style="font-style: italic; font-size: 10px; font-weight: bold;"><span id='numeroDocuFormato'> </span> per cui &egrave richiesta la firma senza allegato</span>

	  <p class="validateTips">Per procedere alla firma digitale del documento, immettere le credenziali:</p>
	  <form>
		  <table class="dati-login" style="width:auto!important;">
		  <tr>
			<td class="etichetta-dato">Alias&nbsp;&nbsp;
			</td>
			<td class="valore-dato">
				<input id="alias" name="alias" title="Alias" class="testo" type="text" size="24" value="" maxlength="100"/>		
			</td>
		  </tr>
		  <tr>
			<td class="etichetta-dato">Pin&nbsp;&nbsp;
			</td>
			<td class="valore-dato">
				<input id="pin" name="pin" title="Pin" class="testo" type="password" size="24" value="" maxlength="100" autocomplete="new-password"/>
			</td>
		  </tr>
		  <tr>
			<td class="etichetta-dato">OTP&nbsp;&nbsp;
			</td>
			<td class="valore-dato">
				<input id="otp" name="otp" title="otp" class="testo" type="text" size="24" value="" maxlength="100"/>
			</td>
		  </tr>
		  <tr id="trModalitaFirmaM">
			<td colspan="2">
			<br>
			<input type="radio" value="cades" name="modalitaFirma" checked="true"/><span>Firma P7M (CAdES)&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<input type="radio" value="pades" name="modalitaFirma" /><span id="padesLabel" >Firma PDF (PAdES)</span>
			</td>
		  </tr>
		  </table>
		  <input type="hidden" value="${param.numeroDocumenti}" name="numeroDocuHidden" id="numeroDocuHidden"/>
		  <input type="hidden" value="${param.numeroDocumentiFormato}" name="numeroDocuFormatoHidden" id="numeroDocuFormatoHidden"/>
		  <input type="hidden" value="${param.entitaFirma}" name="entitaFirmaM" id="entitaFirmaM"/>
		  <input type="hidden" value="${param.chiaviFirma}" name="chiaviFirma" id="chiaviFirma"/>
	  </form>
	</div>
	<textarea id="errorMessages" style="color:red;resize:none;display:none" disabled name="errorMessages" cols="59" rows="7"></textarea>
</div>
<script type="text/javascript"> 

document.getElementById('trModalitaFirmaM').style.visibility = 'hidden';
document.getElementById('Chiudi').style.visibility = 'hidden';

function _nowait() {
	document.getElementById('bloccaScreen').style.visibility = 'hidden';
	document.getElementById('wait').style.visibility = 'hidden';
}

var pinFirma;
var aliasFirma;

function openModalFirmaInoltra(idprg,iddocdig,nome,contextPath,coacod){
	
	$( "#nomeDocumento" ).html("<b>" + nome + "</b>");
	$( "#numeroDocu" ).html("<b>" + $("#numeroDocuHidden").val() + "</b>");
	if($("#numeroDocuFormatoHidden").val() > 0){
		if($("#numeroDocuFormatoHidden").val() == 1){
			$( "#numeroDocuFormato" ).html("*C'&egrave <b>" + $("#numeroDocuFormatoHidden").val() + "</b> documento");
		} else {
			$( "#numeroDocuFormato" ).html("*Ci sono <b>" + $("#numeroDocuFormatoHidden").val() + "</b> documenti");
		}
	} else {
		$("#infoMessagesDocDaFirmare").css("display","none");
	}
	
	var vet = nome.split(".");
	var ext = vet[vet.length-1];
	ext = ext.toUpperCase();
	if(ext != 'PDF'){
		$("#PAdES").attr('disabled', true);
		$("#padesLabel").css('color', 'grey');
	}
	
	
	$( "#dialog-form-inoltra-firma" ).css({
		"display":"block",
	});
	$( "#dialog-form-inoltra-firma" ).dialog({
	  resizable: false,
	  height: "auto",
	  width: 500,
	  position: { my: "center", of: ".contenitore-arealavoro"},
	  modal: true,
	  close: function() {
	  },
	  buttons: [
	  {
		text: "Conferma",
		id:"Conferma",
		click: function () {
			var withFirma = $('#withFirma').val();
			var pin = $("#pin").val();
			var alias = $("#alias").val();
			var otp = $("#otp").val();
			
			if(withFirma && withFirma== 'si' && (!pin || !alias || !otp)){
				alert("I campi Alias, Pin e OTP sono obbligatori");
				return;
			}
				
			setCredenzialiEFirmaInoltraMassiva(idprg,iddocdig,contextPath,coacod);
		}
      },
	  {
		text: "Annulla",
		id:"Annulla",
		click: function () {
			sbiancaDatiPopup();
			$("#errorMessages").html("");
			$("#errorMessages").css("display","none");
			$( this ).dialog( "close" );
		}
      },
	  {
		text: "Chiudi",
		id:"Chiudi",
		style:"display:none;",
		click: function () {
			sbiancaDatiPopup();
			$("#errorMessages").html("");
			$("#errorMessages").css("display","none");
			historyReload();
		}
      }]
	});
	
	getCredenziali();
	
}

function setCredenzialiEFirmaInoltraMassiva(idprg,iddocdig,contextPath,coacod){
	var chiaviFirma = document.getElementById('chiaviFirma').value;
	var sendTo = $('input[name="sendTo"]:checked').val();
	var withFirma = $('#withFirma').val();
	var entitaFirma = document.getElementById('entitaFirmaM').value;
	document.getElementById('bloccaScreen').style.visibility='visible';
	$('#bloccaScreen').css("width",$(document).width());
	$('#bloccaScreen').css("height",$(document).height());
	document.getElementById('wait').style.visibility='visible';
	$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
	var modalitaFirma = $('input[name=modalitaFirma]:checked').val();
	if(modalitaFirma == null){alert("Selezionare una tipologia di firma");return;}
	$.ajax({
		url: contextPath+'/pg/SetFirmaInoltraStipulaMassiva.do',
		type: 'POST',
		async: true,
		dataType: 'json',
		data: {
			pin: $("#pin").val(),
			alias: $("#alias").val(),
			otp: $("#otp").val(),
			idprg: idprg,
			iddocdig: iddocdig,
			c0acod: coacod,
			modalitaFirma : modalitaFirma,
			chiaveFirma:chiaviFirma,
			sendTo:sendTo,
			withFirma:withFirma,
			entitaFirma:entitaFirma
		},
		success: function(data) {
			var response = data.message;			
			$("#errorMessages").html(response);
			$("#errorMessages").css("display","block");
			_nowait();
		},
		error: function() {
			_nowait();
			alert("Errore nel caricamento della tabella");
		},
		complete: function() {
			$("#Chiudi").css("display","block");
			$("#Conferma").css("display","none");
			$("#Annulla").css("display","none");
			_nowait();
		}
	});
}

function getCredenziali(){
	$.ajax({
		url: contextPath+'/SetFirmaDigitaleRemota.do',
		type: 'POST',
		async: false,
		dataType: 'json',
		data: {
			metodo: 'getCredenziali'
		},
		success: function(data) {
				pinFirma = data.pin;
				aliasFirma = data.alias;
				$("#pin").val(pinFirma);
				$("#alias").val(aliasFirma);
		},
		error: function() {
			_nowait();
			alert("Errore nel caricamento della tabella");
		}
	});
}

function isVisibleFormFirma() {
	if (document.getElementById('checkisVisibleFormFirma').checked){
		$('#withFirma').val("si");
		getCredenziali();
		$( "#dialog-form-firma" ).css({
			"display":"block",
		});
	} else {
		$('#withFirma').val("no");
		sbiancaDatiPopup();
		$( "#dialog-form-firma" ).css({
			"display":"none",
		});
	}
}

function sbiancaDatiPopup() {
	$("#pin").val("");
	$("#alias").val("");
	$("#otp").val("");
}

</script>



