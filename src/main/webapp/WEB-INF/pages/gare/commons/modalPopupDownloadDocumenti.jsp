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

<style type="text/css">
	.ui-dialog-titlebar {
		display: none;
	}
	span{
		font:11px Verdana, Arial, Helvetica, sans-serif;
	}
	#buste{
		font:11px Verdana, Arial, Helvetica, sans-serif;
	}
	  #avanzamento{
    position: relative;
  }
  #avanzamento-label {
    position: absolute;
    left: 50%;
    top: 4px;
    font-weight: bold;
    text-shadow: 1px 1px 0 #fff;
  }
</style>
  
<div id="dialog-form" style="display:none">
	<br>
	<b>Esporta documenti presentati dalla ditta su file zip</b>
	<br>
	<div id="progressbardiv" style="display:none">
		<img src="${pageContext.request.contextPath}/img/${applicationScope.pathCss}progressbar.gif" alt=""/>
		<br><br>Esportazione in corso...
	</div>
	<p class="validateTips">
	<span id="tipMsg">Mediante questa funzione è possibile esportare in un unico file zip i documenti presentati dalla ditta '${param.nomimo}' elencati nella lista corrente.</span>
	<span id="tipMsgBari" style="display:none"><br><br><b>ATTENZIONE: Ci sono dei documenti della busta tecnica che dovrebbero essere scaricati solo dopo aver completato la prima fase della valutazione tecnica.</b></span><br>
	
	
	<div id="checkBuste">
		<span id="">Per limitare l'esportazione ai documenti di una singola busta, selezionare una voce di seguito:</span><br><br>
		<select name="buste" id="buste">
		<c:if test="${bustaPrequalifica eq 1}">
				<option value="4">Prequalifica</option>
			</c:if>
			<c:if test="${bustaAmministrativa eq 1}">
				<option value="1">Documentazione amministrativa</option>
			</c:if>
			<c:if test="${bustaTecnica eq 1}">
				<option value="2">Offerta tecnica</option>
			</c:if>
			<c:if test="${bustaEconomica eq 1}">
				<option value="3">Offerta economica</option>
			</c:if>
			<option value="0" selected="selected">seleziona tutto</option>
		</select><br>
	</div>
	<span id= "tipConfirm"><br>Confermi l'operazione ?</span>
	<span id="tipWaiting">Esportazione dei documenti in corso.</span>
	<br><br></p>
	<br>
	<div id="loading">
		<div id="avanzamento"><div id="avanzamento-label"></div></div>
		
	</div>
	<span id="errorMessage" style="color:red;display:none"><br></span>

</div>

<script type="text/javascript"> 


function _nowait() {
	document.getElementById('bloccaScreen').style.visibility = 'hidden';
	document.getElementById('wait').style.visibility = 'hidden';
}

var annullato = false;
var archivioCreato = "false";
var busteChecked = "";

function openModal(ngara,ditta,contextPath,stepWizard,prefissoFile){
	$("#loading").hide();
	$("#avanzamento-label").html("0%");
	annullato = false;
	archivioCreato = "false";
	$("#tipMsg").show();
	$("#tipConfirm").show();
	var genereGara = $( "#genereGara").val();
	if(genereGara == 10 || genereGara == 20){
		$("#checkBuste").hide();
	}else{
		$("#checkBuste").show();
	}
	$("#tipWaiting").hide();
	$("#button-conferma").show();
	$( "#dialog-form" ).css({
		"display":"block",
	});
	var prefissoFileDownloadComBari = "${prefissoFileDownloadComBari}";
	if(prefissoFile!=null && prefissoFile!='' && prefissoFileDownloadComBari != null && prefissoFileDownloadComBari != ''){
		prefissoFile = prefissoFile.toUpperCase();
		var maxCount = "${datiRiga.rowCount}";
		var busta;
		var descrizione;
		var richiestaConfermaBari = false;
		for(i=0; i < maxCount; i++){
			descrizione = $( "#INPUT_DESCRIZIONE_" + (i)).val();
			busta = $( "#INPUT_BUSTA_" + (i)).val();	
			if(busta == "2" && descrizione!=null && descrizione != '' && prefissoFile!=null && !richiestaConfermaBari && stepWizard == '5'){
				descrizione= descrizione.toUpperCase();
				if(descrizione.substring(0,prefissoFile.length) == prefissoFile){
					richiestaConfermaBari=true;
					break;
				}
					
				
			}
			
		}
		if(richiestaConfermaBari)
			$("#tipMsgBari").show();
	}
		
	$( "#dialog-form" ).dialog({
	  resizable: false,
	  height: "auto",
	  width: 500,
	  position: { my: "center", of: ".contenitore-arealavoro"},
	  modal: true,
	  close: function() {
	  },
	  buttons: [
        {
			text:"Conferma",
			id:"button-conferma",
			click: function() {
			$('#continueButton').attr("disabled", true);
			$("#loading").show();
			$( "#avanzamento" ).progressbar({
			  value: 0
			});
			$("#tipMsg").hide();
			$("#tipMsgBari").hide();
			$("#tipConfirm").hide();
			$("#tipWaiting").show();
			$("#checkBuste").hide();
			downloadDocumentazione(ngara,ditta,stepWizard);
			$("#button-conferma").hide();
			},
		
		},{
			text:"Annulla",
			click: function() {
			annullato = true;
			$( this ).dialog( "close" );
			}
		}
		]
	});
	
}


function _wait() {
			document.getElementById('bloccaScreen').style.visibility='visible';
			$('#bloccaScreen').css("width",$(document).width());
			$('#bloccaScreen').css("height",$(document).height());
			document.getElementById('wait').style.visibility='visible';
			$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
	}
	
	function _nowait() {
		document.getElementById('bloccaScreen').style.visibility='hidden';
		document.getElementById('wait').style.visibility='hidden';
	}
	
	
	function ajaxCallDownload(counter,path){
		if(annullato){
			//si devono cancellare i fie temporanei
			$.ajax({
			type: "POST",
			url: "${pageContext.request.contextPath}/pg/ScaricaTuttiDocumentiBusta.do",
			data: {
				path: path,
				archivioCreato: archivioCreato,
				metodo: 'cancellaFileTemporanei'
			},
			async: true,
			dataType: "json"
			
		});
			return;
		}
		var maxCount = "${datiRiga.rowCount}";
		var stop = false;
		while(!stop && !(counter > maxCount)){
			var iddocdg = $( "#INPUT_IDDOCDG_" + (counter)).val();
			var idprg = $( "#INPUT_IDPRG_" + (counter)).val();
			var busta = $( "#INPUT_BUSTA_" + (counter)).val();	
			if(busteChecked.indexOf(busta) >= 0 || busteChecked==0){
				stop = true;
			}else{
				counter++;
			}
		}
		
		if((!maxCount || maxCount == 0) || (counter > maxCount && archivioCreato == "false")){
			alert("Non è presente nessun documento da esportare nella lista");
			return $("#dialog-form").dialog( "close" );
		}else{
			if(counter > maxCount && archivioCreato == "true"){
			document.location.href='pg/ScaricaTuttiDocumentiBusta.do?'+csrfToken+'&metodo=download&path='+encodeURIComponent(path);
			return $("#dialog-form").dialog( "close" );
		}
		if((!iddocdg || !idprg)){
			return ajaxCallDownload(counter+1,path);
			}
		}
		
		$.ajax({
			type: "POST",
			url: "${pageContext.request.contextPath}/pg/ScaricaTuttiDocumentiBusta.do",
			data: {
				path: path,
				idddocdg: iddocdg,
				idprg: idprg,
				archivioCreato: archivioCreato,
				metodo: 'creaArchivio'
			},
			async: true,
			dataType: "json",
			success:function(data){
				archivioCreato = "true";
				counter++;
				var perc = Math.round((counter / maxCount) * 100);
				$("#avanzamento-label").html(perc + "%");
				$( "#avanzamento" ).progressbar({
				  value: perc
				});
				if (counter < maxCount){
					ajaxCallDownload(counter,path);
				}else{
					document.location.href='pg/ScaricaTuttiDocumentiBusta.do?'+csrfToken+'&metodo=download&path='+encodeURIComponent(path);
					return $("#dialog-form").dialog( "close" );
				}
			}
		});
	}
	
	function downloadDocumentazione(ngara, ditta,stepWizard) {
		
		var prequalifica = $("#prequalifica").is(":checked");
		var amministrativa = $("#amministrativa").is(":checked");
		var tecnica = $("#tecnica").is(":checked");
		var economica = $("#economica").is(":checked");
		
		busteChecked = $( "#buste option:selected" ).val();

		var documenti = {"documenti":[]};
		var maxCount = "${datiRiga.rowCount}";
		
		for(i=0; i < maxCount; i++){
			var iddocdg = $( "#INPUT_IDDOCDG_" + (i)).val();
			var idprg = $( "#INPUT_IDPRG_" + (i)).val();	
			var nomeDoc = $( "#INPUT_DIGNOMDOC_" + (i)).val();	
			var busta = $( "#INPUT_BUSTA_" + (i)).val();	
			if(busteChecked.indexOf(busta) >= 0 || busteChecked == 0){
				documenti.documenti.push({
					"idprg": idprg, 
					"iddocdg":iddocdg, 
					"nomeDoc":nomeDoc
				});
			}
		}
		
		var documentiStringa = JSON.stringify(documenti);
		var genereGara = $( "#genereGara").val();
		var stepWizard = $( "#stepWizard").val();
		if(genereGara == 10 || genereGara == 20)
			stepWizard = null;
		
		$.ajax({
			type: "POST",
			url: "${pageContext.request.contextPath}/pg/ScaricaTuttiDocumentiBusta.do",
			data: {
				ditta: ditta,
				ngara : ngara,
				documenti: documentiStringa,
				stepWizard: stepWizard,
				busta: busteChecked,
				metodo: 'getPath'
			},
			async: true,
			dataType: "json",
			success:function(data){
				ajaxCallDownload(0,data.path);
			},
			error: function(e){
				alert("Si è verificato un problema");
			}
		});
	
	return;
	
	}

</script>



