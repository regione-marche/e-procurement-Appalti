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
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="cifraturaBuste" value="${param.cifraturaBuste}" />
<c:set var="bustalotti" value="${param.bustalotti}" />

  
<div id="mascheraAcquisizioneBusteFS10A"  style="display:none; font:11px Verdana, Arial, Helvetica, sans-serif;">
	<table class="modale">
		<tr id="sezioneIniziale">
				<td class="valore-dato" colspan="2">
					<br>
					Mediante questa funzione &egrave; possibile procedere all'acquisizione della busta per prequalifica della ditta <span id="nomimo"></span>.
					<br><br>
					Confermi l'operazione ?
					<br>
					<br>
				</td>
			</tr>
			
								
			<c:if test="${cifraturaBuste eq 'true'}">
					<tr id="rowPWD">
						<td id="etichettaPassword" class="etichetta-dato">Password per la decifratura della busta (*)</td>
						<td class="valore-dato">
							<input id="PWD" name="PWD" title="Password per la decifratura della busta" class="testo" type="password" size="24" value="${sessionScope.passBustePreq }" maxlength="100" />
							<a href="javascript:usaScanner()"><img src="${pageContext.request.contextPath}/img/barcode.png" title="Usa lettore codice a barre" alt="Usa lettore codice a barre" width="15" height="15"></a> 
							<span id="errorePwd" style="display:none;color: red;">
								<b>Password errata</b> 
							</span>
						</td>
					</tr>
					<tr id="rowPWDcryptata" style="display: none;">
						<td id="etichettaPasswordCryptata" class="etichetta-dato">Password per la decifratura della busta (da codice a barre)(*)</td>
						<td class="valore-dato">
							<input id="PWDcryptata" name="PWD" title="Password da barcode scanner" class="testo" type="password" size="24" maxlength="100" autocomplete="off"/>
							<a href="javascript:cancellaBarcode()"><img src="${pageContext.request.contextPath}/img/barcodeStop.png" title="Torna alla digitazione da tastiera" alt="Torna alla digitazione da tastiera" width="15" height="15"></a> 
							<span id="errorePwd" style="display: none;color: red;">
								<b>Password errata</b> 
							</span>
						</td>
					</tr>
					<tr>
						<td class="valore-dato" colspan="2"><br></td>
					</tr>
			</c:if>
			
			<tr id="selezionetutteDitte">
				<td class="etichetta-dato">Applica a tutte le ditte in gara?</td>
				<td class="valore-dato">
					<select id="tutteDitte" name="GARE_RIDISO" title="Applica a tutte le ditte in gara?" >
						<option value="1" title="Si" >Si</option>
						<option value="2" title="No" selected="selected">No</option>
					</select>
				</td>
			</tr>
				
			
			
			<tr id="sezioneAvanzamento" style="display: none;">
				<td colspan="2">
					<br>
					Attendere il completamento dell'operazione
					<br>
					<br>
					<div id="avanzamento"><div id="avanzamento-label"></div></div>
					<br>
				</td>
			</tr>
			
			<tr id="sezioneFinale" style="display: none;">
				<td colspan="2">
					<br>
					<span id="msgEsito">L'operazione &egrave; stata eseguita con successo</span>.
					<br>
					<span id="sezioneAcquisizioniSuccesso" style="display: none;">
						<br>
						Numero acquisizioni: <span id="numeroAcquisizioni"></span>
					</span>
					<span id="sezioneAcquisizioniErrore" style="display: none;">
						<br>
						Numero acquisizioni non processate o con errori: <span id="numeroAcquisizioniErrore"></span>
					</span>
					<br>
					<span id="sezioneAcquisizioniScartate" style="display: none;">
						Numero acquisizioni non processate perch&egrave; relative a ditte non ammesse alla gara: <span id="numeroAcquisizioniScartate"></span>
					</span>
					<br>							
				</td>
			</tr>

			<tr id="sezioneErrore" style="display: none;">
				<td colspan="2">
					<br>
					<span id="msgEsitoErrore">Si è presentato un errore durante l'esecuzione dell'operazione</span>
					<br>
					<br>
				</td>
			</tr>
			
	</table>
</div>

<div id="overlay" style="background-color: rgba(0, 0, 0, 0.6);top:50%;left:50%;	marginRight:50%;transform: translate(-50%, -50%);position: absolute; display:none;text-align:center;z-index:1000;" onclick="javascript:{$('#overlay').hide();}">
	<br><br><br><br><br><br><br><br><span style="color:white;font-size:14px;"><b>SCANSIONA IL CODICE A BARRE</b></span>
</div>

<script type="text/javascript"> 

function aperturaBusteFS10A(chiaveRiga,indice) {

	pingSessioneAttiva();

	var vetTmp = chiaveRiga.split(";");
	var dittao= vetTmp[1].substring(vetTmp[1].indexOf(":") + 1);
	var ngara = vetTmp[2].substring(vetTmp[2].indexOf(":") + 1);
	var codiceGara = vetTmp[0].substring(vetTmp[0].indexOf(":") + 1);
	var nomimo=$("#colDITG_NOMIMO_" + (indice+1) +" span").text();
	$('#nomimo').text(nomimo);
	
	var titolo="Acquisizione busta per prequalifica";
	
	var widthM= 500;
	var	heightM= 300;
	var cifraturaBuste="${cifraturaBuste}";
	/* if(cifraturaBuste=='true')
		heightM= 300; */
	
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
		title: titolo,
		buttons: {
			"Conferma": {
				id:"botConferma",
				text:"Conferma",
				click: function() {
					$("#overlay").hide();
					acquisisciAJAXFS10A(ngara,dittao);
				}
			 },
			"Annulla": {
				id:"botAnnulla",
				text:"Annulla",
				click:function() {
					$( this ).dialog( "close" );
					$("#sezioneIniziale").hide();
					$("#sezioneAvanzamento").hide();
					$("#sezioneFinale").hide();
					$("#sezioneErrore").hide();
					$("#overlay").hide();
				}
			 },
			 "Chiudi": {
				id:"botChiudi",
				text:"Chiudi",
				click:function() {
					$( this ).dialog( "close" );
					$("#sezioneIniziale").hide();
					$("#sezioneAvanzamento").hide();
					$("#sezioneFinale").hide();
					$("#sezioneErrore").hide();
					$("#overlay").hide();
					ricaricaPagina();
				}
			 }
		 }
	};
	
	$("#mascheraAcquisizioneBusteFS10A").dialog(opt).dialog("open");
	$("#sezioneIniziale").show();
	$("#botChiudi").hide();
	$("#avanzamento").progressbar({
            value: false,
            disabled: true
        });
	<c:if test="${cifraturaBuste eq 'true'}">
	cancellaBarcode();
	$("#overlay").hide();
	</c:if>
}

function inizializzaAvanzamentoFS10A(ngara,dittao) {
	$(".ui-progressbar").css("position","relative");
	$("#avanzamento-label").css("position","absolute");
	$("#avanzamento-label").css("left","45%");
	$("#avanzamento-label").css("padding-top","4px");
	$("#avanzamento-label").css("font-weight","bold");
	$("#avanzamento-label").css("font","10px Verdana, Arial, Helvetica, sans-serif");
	$("#avanzamento-label").css("text-shadow","1px 1px 0 #fff");	
	
	$("#avanzamento").progressbar({
        value: 0,
        disabled: false
    });

	$("#sezioneAvanzamento").show();
	$("#botConferma").prop('disabled', true);
	$("#botConferma").css('opacity',0.2);
	$("#botAnnulla").prop('disabled', true);
	$("#botAnnulla").css('opacity',0.2);
	$("#botChiudi").hide();
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/AvanzamentoApPreqAJAX.do?ngara=" + ngara + "&dittao=" + dittao,
		success: function(res){
			var max= res.cnt;
			if(isNaN(max))
				max=0;
			$("#avanzamento").progressbar("option", "max", max);
    		$("#avanzamento-label").text("0 %");
		}
	});
	
}

function avanzamentoFS10A(ngara,dittao) {
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/AvanzamentoApPreqAJAX.do?ngara=" + ngara + "&dittao=" + dittao,
		success: function(res){
			var curr = res.cnt;
			if(isNaN(curr))
				curr=0;
			var max = $("#avanzamento").progressbar( "option", "max" );
			$("#avanzamento").progressbar( "option", "value", max - curr );
			var perc = Math.round(((max - curr)/max)*100);
			$("#avanzamento-label").text(perc + " %");
		}
	});
}

function acquisisciAJAXFS10A(ngara,dittao){
	
	var pwd;
	var pswCryptata;
	<c:if test="${param.cifraturaBuste eq 'true' }">
		pwd = $("#PWD").val();
		if($("#PWDcryptata").val()){
			pswCryptata = "true";
			pwd = $("#PWDcryptata").val();
		}
	</c:if>
	
	var tutteDitte = $("#tutteDitte").val();
	if(tutteDitte=="1")
		dittao = "";
	inizializzaAvanzamentoFS10A(ngara,dittao);
	$("#sezioneIniziale").hide();
	$("#sezioneComandiIniziali").hide();
	$("#rowPWD").hide();
	$("#rowPWDcryptata").hide();
	$("#selezionetutteDitte").hide();
	
	var esito;
	var msgEsito;
	$.ajax({
		type: "POST",
		dataType: "json",
		async: true,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/AttivaApPreqAJAX.do?ngara=" + ngara + "&dittao=" + dittao + "&password=" + encodeURIComponent(pwd) + "&pswCryptata=" + pswCryptata,
		success: function(res){
			esito=res.Esito;
			msgEsito = res.MsgErrore;
						
			$("#numeroAcquisizioni").text(res.numeroAcquisizioni);
			$("#numeroAcquisizioniErrore").text(res.numeroAcquisizioniErrore);
			$("#sezioneAcquisizioniSuccesso").show();
			$("#sezioneAcquisizioniErrore").show();
			if (res.numeroAcquisizioniScartate > 0) {
				$("#sezioneAcquisizioniScartate").show();
				$("#numeroAcquisizioniScartate").text(res.numeroAcquisizioniScartate);
			}
			

		},
		error: function(e){
			alert("Errore generico durante l'operazione");
		},
		complete: function(jqXHR, textStatus) {
			clearInterval(avanzamentoInterval);
			var max = $("#avanzamento").progressbar( "option", "max" );
			$("#avanzamento").progressbar( "option", "value", max );
			$("#avanzamento-label").text("100 %");
			$("#sezioneAvanzamento").hide();
			
			if(textStatus=="error" || textStatus=="parsererror"){
				$("#sezioneErrore").show();
				$("#botConferma").hide();
				$("#botAnnulla").hide();
				$("#botChiudi").show();
			}else{
            	if(esito=="ErrorePwdNonCorretta"){
            		$("#sezioneIniziale").show();
					$("#sezioneComandiIniziali").show();
					$("#selezionetutteDitte").show();
					$("#rowPWD").show();
					$("#rowPWDcryptata").hide();
					$("#PWDcryptata").val("");
					$("#errorePwd").show();	
					$("#botConferma").prop('disabled', false);
					$("#botConferma").css('opacity',1);
					$("#botAnnulla").prop('disabled', false);
					$("#botAnnulla").css('opacity',1);
            	}else if(esito=="Errore"){
					$("#botConferma").hide();
					$("#botAnnulla").hide();
					$("#msgEsitoErrore").html(msgEsito);
					$("#sezioneErrore").show();	
					$("#botChiudi").show();
				}else{
					$("#sezioneFinale").show();
					$("#botConferma").hide();
					$("#botAnnulla").hide();
					$("#botChiudi").show();
				}
            }
			
		}
	});
	
	var avanzamentoInterval = setInterval(function() {avanzamentoFS10A(ngara,dittao)}, 200);
	
}



<c:if test="${cifraturaBuste eq 'true'}">
document.getElementById("PWDcryptata").addEventListener("input", function(e){
	$("#overlay").hide();
});

document.getElementById("PWDcryptata").addEventListener("focus", function(e){
	$("#overlay").show();
	$("#PWDcryptata").val("");
});

function cancellaBarcode(){
	$("#PWDcryptata").val("");
	$("#rowPWD").show();
	$("#rowPWDcryptata").hide();
}


function usaScanner(){
	$("#rowPWD").hide();
	$('#overlay').css("width", $('#mascheraAcquisizioneBusteFS10A').width() + 30);
	$('#overlay').css("height", $('#mascheraAcquisizioneBusteFS10A').height() + 100);
	$("#overlay").show();
	$("#PWD").val("");
	$("#rowPWDcryptata").show();
	$("#PWDcryptata").focus();
	
}
</c:if>

</script>



