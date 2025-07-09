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

<c:set var="ngara" value="${param.ngara}" />
<c:set var="codgar" value="${param.codiceGara}" />
<c:set var="bustalotti" value="${param.bustalotti}" />
<c:set var="sez" value="${param.sez}" />
<c:set var="anonima" value="${param.anonima}" />

<c:choose>
	<c:when test='${bustalotti eq "1"}'>
		<c:set var="ngaraBusta" value="${codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngaraBusta" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:set var="cifraturaBuste" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBustaCifrataFunction", pageContext, ngaraBusta, "FS11B")}'/>

<c:set var="whereControllo" value="comkey2 = '${ngara}' and (comstato = '5' or comstato='17') and comtipo = 'FS11B'" />
<c:set var="busteDaAcquisire" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "count(IDPRG)","W_INVCOM", whereControllo)}'/>
<c:choose>
	<c:when test="${not empty busteDaAcquisire and busteDaAcquisire ne 0 }">
		<c:set var="bloccoBuste" value="true" />
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test='${bustalotti eq "1" or bustalotti eq "2"}'>
				<c:set var="codice" value="${codgar}" />
			</c:when>
			<c:otherwise>
				<c:set var="codice" value="${ngara}" />
			</c:otherwise>
		</c:choose>
		<c:set var="isValutazioneCommissione" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsValutazioneCommissioneFunction",pageContext,codice)}' />
		<c:set var="esitoControlloCriteri" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlliCriteriQualitativiFunction", pageContext, ngara, codgar, bustalotti, isValutazioneCommissione )}' />
	</c:otherwise>
</c:choose>
	
	
<c:if test='${anonima eq "1"}'>
	<c:set var="garaInversa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetInversaFunction", pageContext, codgar)}' />	
	<c:set var="compreq" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCompreqFunction", pageContext, codgar)}' />
	<c:set var="esisteBloccoFasi2" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoCondizioniFasiAcquisizioniFunction",  pageContext, ngara, "2")}'/>
	<c:set var="esisteBloccoFasi3" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoCondizioniFasiAcquisizioniFunction",  pageContext, ngara, "3")}'/>
	<c:set var="esisteBloccoFasi4" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoCondizioniFasiAcquisizioniFunction",  pageContext, ngara, "4")}'/>
	<c:if test="${esisteBloccoFasi2 eq 'true' && esisteBloccoFasi3 eq 'true' && esisteBloccoFasi4 eq 'true'}">
		<c:set var="esisteBloccoFasi" value='true'/>
	</c:if>
	
	<c:if test="${garaInversa ne '1' }">
		<c:set var="esistonoDitteAmmissioneNulla" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteAmmissioneNullaFunction", pageContext, ngara, "35" )}' />
	</c:if>
	<c:set var="esistonoDitteAmmissioneSoccorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteAmmissioneSoccorsoIstruttorioFunction", pageContext, ngara, "35","false" )}' />
	
	
	<c:if test="${garaInversa ne '1' }">
		<c:set var="esistonoAcquisizioniOfferteDaElaborare" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, ngara, "FS11A" )}' />
	</c:if>
	
	
	<c:set var="esistonoDitteInGara" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteInGaraFunction", pageContext, "NGARA5", ngara," and (fasgar > 2 or fasgar is null)")}' />
	<c:if test="${compreq eq '1' and garaInversa ne '1'  }">
		<c:set var="esistonoDitteEstimpValorizzato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteEstimpValorizzatoFunction", pageContext, ngara )}' />
	</c:if>
	
</c:if>

<div id="mascheraAcquisizioneBusteFS11B"  style="display:none; font:11px Verdana, Arial, Helvetica, sans-serif;">
	<table class="modale">
		<tr id="sezioneIniziale">
				<td class="valore-dato" colspan="2">
					<br>
					<br>
					<span id="BloccoFasi" style="display:none;">Non &egrave; possibile procedere all'acquisizione delle buste perch&egrave; la fase corrente non &egrave; quella di apertura doc. amministrativa.</span>
					<span id="DitteAmmissioneNulla" style="display:none;">Non &egrave; possibile procedere all'acquisizione delle buste perch&egrave; deve essere specificato lo stato di ammissione per ogni ditta in gara.</span>
					<span id="DitteAmmissioneSoccorso" style="display:none;">Non &egrave; possibile procedere all'acquisizione delle buste perch&egrave; ci sono delle ditte in gara con soccorso istruttorio in corso.</span> 
					<span id="AcquisizioniOfferteDaElaborare" style="display:none;">Non &egrave; possibile procedere all'acquisizione delle buste perch&egrave; devono essere prima acquisite tutte le buste amministrative.</span>
					<span id="DitteInGara" style="display:none;">Non &egrave; possibile procedere all'acquisizione delle buste perch&egrave; non ci sono ditte in gara.</span> 
					<span id="DitteEstimpValorizzato" style="display:none;">Non &egrave; possibile procedere all'acquisizione delle buste perch&egrave; deve essere prima fatto il sorteggio sulle ditte in gara per la verifica requisiti.</span>
					<span id="bloccoBuste" style="display:none;">Non è possibile procedere all'acquisizione della busta tecnico-quantitativa della ditta '<span class="nomimo"></span>' perch&egrave; deve essere prima acquisita la busta tecnico-qualitativa per ogni ditta nella lista.</span>
					<span id="ControlloCriteri-NoCommissione" style="display:none;">Non è possibile procedere all'acquisizione della busta tecnico-quantitativa della ditta '<span class="nomimo"></span>' perch&egrave; deve essere prima completata la compilazione del dettaglio valutazione dei criteri tecnico-qualitativi per tutte le ditte in gara</span>
					<span id="ControlloCriteri-NoOmogeneo" style="display:none;">Non è possibile procedere all'acquisizione della busta tecnico-quantitativa della ditta '<span class="nomimo"></span>' perch&egrave; sono state compilate delle valutazioni da parte della commissione per il criterio '${criterioGiudizioCommissione}' solo per alcune ditte. 
					Deve essere compilata la valutazione di tale criterio da parte dei commissari per tutte le ditte in gara</span>
					<span id="ControlloCriteri-Nok" style="display:none;">Non è possibile procedere all'acquisizione della busta tecnico-quantitativa della ditta '<span class="nomimo"></span>' perch&egrave; deve essere prima completata la compilazione del dettaglio valutazione dei criteri tecnico-qualitativi per tutte le ditte in gara </span>
					<span id="ControlloFS12Rettifica-Aperte" style="display:none;">Non è possibile procedere all'acquisizione delle buste perch&egrave;  sono presenti una o piu' richieste di rettifica dell'offerta tecnica da parte delle ditte in gara. Controllare nella lista delle comunicazioni ricevute della gara.</span>
					<span id="ControlloFS12Rettifica-SenzaRisp" style="display:none;">Non è possibile procedere all'acquisizione delle buste perch&egrave; non risulta ancora pervenuta la rettifica dell'offerta tecnica di una o più ditte e non sono scaduti i relativi termini di presentazione.</span>
					<div id="ControlliOk" style="display:none;">
							<span id="FunzAnonima" style="display:none;">Mediante questa funzione &egrave; possibile procedere all'acquisizione in forma anonima delle buste tecniche di tutte le ditte in gara.</span>
							<span id="FunzStandard" style="display:none;">Mediante questa funzione &egrave; possibile procedere all'acquisizione della busta <span id="msgSez"></span> della ditta '<span class="nomimo"></span>'.</span>
							<br><br>
							Confermi l'operazione ?
					</div>
					<br>
					<br>
					
				</td>
			</tr>
														
			<c:if test="${cifraturaBuste eq 'true'}">
					<tr id="rowPWD" style="display: none;">
						<td id="etichettaPassword" class="etichetta-dato">Password per la decifratura della busta (*)</td>
						<td class="valore-dato">
							<input id="PWD" name="PWD" title="Password per la decifratura della busta" class="testo" type="password" size="24" value="${sessionScope.passBusteB }" maxlength="100" />
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
			
			<tr id="selezionetutteDitte" style="display: none;">
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
					<span id="sezioneAcquisizioniSaltatePerFS12RettificaInCorso" style="display: none;">
						Numero acquisizioni non processate perch&egrave; rettifica busta tecnica in corso: <span id="numeroAcquisizioniSaltatePerFS12RettificaInCorso"></span>
					</span>
					<br>							
				</td>
			</tr>

			<tr id="sezioneErrore" style="display: none;">
				<td colspan="2">
					<br>
					<span id="msgEsitoErrore">Si è presentato un errore durante l'esecuzione dell'operazione.</span>
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

var anonima="${anonima}";
var sez="";

function aperturaBusteFS11B(chiaveRiga,indice,tipoBusta) {

	pingSessioneAttiva();

	var dittao= "";
	var ngara = "";
	var codiceGara = "";
	if(anonima=='1'){
		var vetTmp = chiaveRiga.split(";");
		ngara = vetTmp[0];
		codiceGara = vetTmp[1];
	}else{
		var vetTmp = chiaveRiga.split(";");
		dittao= vetTmp[1].substring(vetTmp[1].indexOf(":") + 1);
		ngara = vetTmp[2].substring(vetTmp[2].indexOf(":") + 1);
		codiceGara = vetTmp[0].substring(vetTmp[0].indexOf(":") + 1);
		var nomimo=$("#colDITG_NOMIMO_" + (indice+1) +" span").text();
		$('.nomimo').text(nomimo);	
	}

	var checkFS12= checkF12RettificaInCorso(ngara,'FS11B');
	
	if(tipoBusta=="FS11B-QL"){
		sez="1";
	}else if(tipoBusta=="FS11B-QN"){
		sez="2";
	}
	
	var msgSez="tecnica";
	if(sez == "1")
		msgSez="tecnico-qualitativa";
	else if(sez == "2")
		msgSez="tecnico-quantitativa";
	
	$('#msgSez').text(msgSez);
	
	var titolo="Acquisizione busta";
	if(anonima==1)
		titolo="Acquisizione buste tecniche in forma anonima";
	else
		titolo="Acquisizione busta " + msgSez;
		
	var widthM= 500;
	var	heightM= 300;
	var cifraturaBuste="${cifraturaBuste}";
	/* if(cifraturaBuste=='true')
		heightM= 300; */
			
	var bloccoBuste ="${bloccoBuste}";
	var esitoControlloCriteri = "${esitoControlloCriteri}";
	if(sez!="2"){
		bloccoBuste="";
		esitoControlloCriteri = "";
	}
		
	
	var esisteBloccoFasi = "${esisteBloccoFasi}";
	var esistonoDitteAmmissioneNulla = "${esistonoDitteAmmissioneNulla}";
	var esistonoAcquisizioniOfferteDaElaborare = "${esistonoAcquisizioniOfferteDaElaborare}";
	var esistonoDitteInGara = "${esistonoDitteInGara}";
	var esistonoDitteEstimpValorizzato = "${esistonoDitteEstimpValorizzato}";
	var esistonoDitteAmmissioneSoccorso = "${esistonoDitteAmmissioneSoccorso}";
	
	var bloccoSalvataggio = "false";
	if(anonima == '1' && (esisteBloccoFasi == 'true' ||  esistonoDitteAmmissioneNulla == 'true' || esistonoAcquisizioniOfferteDaElaborare == 'true'
			|| esistonoDitteInGara == 'false' || esistonoDitteEstimpValorizzato == 'false' || esistonoDitteAmmissioneSoccorso == 'true'))
		bloccoSalvataggio = "true";
	
	var visConferma=false;
	if (bloccoBuste != 'true' && esitoControlloCriteri != 'nok' && esitoControlloCriteri != 'nok-commissione' && bloccoSalvataggio != 'true' && checkFS12 == 'OK')
		visConferma=true;
			
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
					acquisisciAJAX(ngara,dittao);
				}
			 },
			"Annulla": {
				id:"botAnnulla",
				text:"Annulla",
				click:function() {
					$( this ).dialog( "close" );
					inizializzazione();
				}
			 },
			 "Chiudi": {
				id:"botChiudi",
				text:"Chiudi",
				click:function() {
					$( this ).dialog( "close" );
					inizializzazione();
					ricaricaPagina();
				}
			 }
		 }
	};
	
	$("#mascheraAcquisizioneBusteFS11B").dialog(opt).dialog("open");
	$("#sezioneIniziale").show();
	if(bloccoSalvataggio == 'true'){
		if(esisteBloccoFasi == 'true'){
			$("#BloccoFasi").show();
		}else if(esistonoDitteAmmissioneNulla == 'true'){
			$("#DitteAmmissioneNulla").show();
		}else if(esistonoDitteAmmissioneSoccorso == 'true'){
			$("#DitteAmmissioneSoccorso").show();
		}else if(esistonoAcquisizioniOfferteDaElaborare == 'true'){
			$("#AcquisizioniOfferteDaElaborare").show();
		}else if(esistonoDitteInGara == 'false'){
			$("#DitteInGara").show();
		}else if(esistonoDitteEstimpValorizzato == 'false'){
			$("#DitteEstimpValorizzato").show();
		}
	}else if(bloccoBuste == 'true'){
		$("#bloccoBuste").show();
	}else if(esitoControlloCriteri == 'nok-commissione'){
		$("#ControlloCriteri-NoCommissione").show();
	}else if(esitoControlloCriteri == 'ko-nonomogeneo'){
		$("#ControlloCriteri-NoOmogeneo").show();
	}else if(esitoControlloCriteri == 'nok'){
		$("#ControlloCriteri-Nok").show();
	}else if(checkFS12 == 'KO-open'){
		$("#ControlloFS12Rettifica-Aperte").show();
	}else if(checkFS12 == 'KO-noresp'){
		$("#ControlloFS12Rettifica-SenzaRisp").show();
	}else {
		$("#ControlliOk").show();
		if(anonima == '1'){
			$("#FunzAnonima").show();
		}else{
			$("#FunzStandard").show();
		}
			
	}
		
	$("#botChiudi").hide();
	if(visConferma!=true){
		$("#botConferma").hide();
		$("#botAnnulla").hide();
		$("#botChiudi").show();
	}else{
		$("#rowPWD").show();
		if(anonima != '1'){
			$("#selezionetutteDitte").show();
		}else{
			$("#selezionetutteDitte").hide();			
			$("#tutteDitte").val('1');
		}
			
	}
			
	$("#avanzamento").progressbar({
            value: false,
            disabled: true
        });
	
	<c:if test="${cifraturaBuste eq 'true'}">
		if(visConferma==true)
			cancellaBarcode();
		$("#overlay").hide();
	</c:if>
	
	
}

function inizializzaAvanzamento(ngara,dittao) {
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
		url: "pg/AvanzamentoApTecAJAX.do?ngara=" + ngara + "&dittao=" + dittao,
		success: function(res){
			var max= res.cnt;
			if(isNaN(max))
				max=0;
			$("#avanzamento").progressbar("option", "max", max);
    		$("#avanzamento-label").text("0 %");
		}
	});
	
}

function avanzamento(ngara,dittao) {
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: false,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: "pg/AvanzamentoApTecAJAX.do?ngara=" + ngara + "&dittao=" + dittao,
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

function acquisisciAJAX(ngara,dittao){
	
	var pwd;
	var pswCryptata;
	<c:if test="${cifraturaBuste eq 'true' }">
		pwd = $("#PWD").val();
		if($("#PWDcryptata").val()){
			pswCryptata = "true";
			pwd = $("#PWDcryptata").val();
		}
	</c:if>
	
	var tutteDitte = $("#tutteDitte").val();
	if(tutteDitte=="1")
		dittao = "";
	inizializzaAvanzamento(ngara,dittao);

	$("#sezioneIniziale").hide();
	$("#sezioneComandiIniziali").hide();
	$("#rowPWD").hide();
	$("#rowPWDcryptata").hide();
	$("#selezionetutteDitte").hide();
	
	var esito;
	var msgEsito;
	
	var action=	"pg/AttivaApTecAJAX.do";
	if(anonima==1)
		action=	"pg/AttivaApMassivaTecAnonimeAJAX.do";	
	
	$.ajax({
		type: "POST",
		dataType: "json",
		async: true,
		beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
			}
		},
		url: action + "?ngara=" + ngara + "&dittao=" + dittao + "&password=" + encodeURIComponent(pwd) + "&pswCryptata=" + pswCryptata + "&sez=" + sez + "&anonima=" + anonima,
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
			if (res.numeroAcquisizioniSaltatePerFS12RettificaInCorso > 0) {
				$("#sezioneAcquisizioniSaltatePerFS12RettificaInCorso").show();
				$("#numeroAcquisizioniSaltatePerFS12RettificaInCorso").text(res.numeroAcquisizioniSaltatePerFS12RettificaInCorso);
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
					<c:if test="${anonima ne '1' }">
					$("#selezionetutteDitte").show();
					</c:if>
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
					$("#botAnnulla").hide();
					$("#botConferma").hide();
					$("#sezioneFinale").show();
					$("#botChiudi").show();
				}
            }
			
		}
	});
	
	var avanzamentoInterval = setInterval(function() {avanzamento(ngara,dittao)}, 200);
	
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
	$('#overlay').css("width", $('#mascheraAcquisizioneBusteFS11B').width() + 30);
	$('#overlay').css("height", $('#mascheraAcquisizioneBusteFS11B').height() + 100);
	$("#overlay").show();
	$("#PWD").val("");
	$("#rowPWDcryptata").show();
	$("#PWDcryptata").focus();
	
}
</c:if>

function inizializzazione(){
	$("#sezioneIniziale").hide();
	$("#BloccoFasi").hide();
	$("#DitteAmmissioneNulla").hide();
	$("#DitteAmmissioneSoccorso").hide();
	$("#AcquisizioniOfferteDaElaborare").hide();
	$("#DitteInGara").hide();
	$("#DitteEstimpValorizzato").hide();
	$("#bloccoBuste").hide();
	$("#ControlloCriteri-NoCommissione").hide();
	$("#ControlloCriteri-NoOmogeneo").hide();
	$("#ControlloCriteri-Nok").hide();
	$("#ControlloFS12Rettifica-Aperte").hide();
	$("#ControlloFS12Rettifica-SenzaRisp").hide();
	$("#ControlliOk").hide();
			
	$("#botChiudi").hide();
	$("#botConferma").show();
	$("#rowPWD").hide();
	$("#selezionetutteDitte").hide();			
	$("#tutteDitte").val('2');
	$("#FunzAnonima").hide();
	$("#FunzStandard").hide();
	
	$("#sezioneAvanzamento").hide();
	$("#sezioneFinale").hide();
	$("#sezioneErrore").hide();
	$("#overlay").hide();
}

</script>



