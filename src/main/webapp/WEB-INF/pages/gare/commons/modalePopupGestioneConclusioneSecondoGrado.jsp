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

<c:set var="ngara" value="${param.ngara}"/>
<c:set var="isriconclusa" value="${param.isriconclusa}"/>
<c:set var="fineConc1grado" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select isfineconc from torn,gare where codgar=codgar1 and ngara=(select g.ngarapa from gare g where g.ngara=?)", ngara)}'/>
<c:set var="ditteInGara" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select count(*) from ditg where (fasgar is null or fasgar=6) and ngara5=?", ngara)}'/>	
<c:set var="dittaAggiudicataria" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select dittap from gare where ngara=?", ngara)}'/>	
 
<c:set var="blocco1" value="${isriconclusa eq '1' && fineConc1grado eq '1'}"/>
<c:set var="blocco2" value="${isriconclusa ne '1' && ditteInGara > 0 && empty dittaAggiudicataria}"/>
<c:set var="alert" value="${ditteInGara == 0 and empty dittaAggiudicataria and isriconclusa ne '1'}"/>
<div id="mascheraGestioneConclusione"  style="display:none;">
	<table class="modale" style="font:11px Verdana, Arial, Helvetica, sans-serif;">
		<tr id="sezioneInizialeConc">
			<td class="valore-dato" colspan="2">
				<c:choose>
					<c:when test="${blocco1}">
						Non è possibile procedere perchè il concorso di progettazione non è in più in fase anonima.
					</c:when>
					<c:when test="${blocco2}">
						Non è possibile procedere perchè non è stato fatto il calcolo dell'aggiudicazione.
					</c:when>
					<c:otherwise>
						<br>
						<c:choose>
							<c:when test="${param.isriconclusa ne '1'}">
								Mediante questa funzione &egrave; possibile impostare la procedura di 2° grado del concorso di progettazione a <b>conclusa</b>.
							</c:when>
							<c:otherwise>
								Mediante questa funzione &egrave; possibile ripristinare la procedura di 2° grado del concorso di progettazione a <b>non conclusa</b>.
							</c:otherwise>
						</c:choose>
						<br>
						<c:if test="${alert}">
							<p style="color: #0000FF;"><b>ATTENZIONE:</b> Non ci sono ditte ammesse alla gara, pertanto non è stato fatto il calcolo della graduatoria.</p>
						</c:if>
						<br><br>
						Confermi l'operazione?
					</c:otherwise>
				</c:choose>
				
				<br>
				<br>
				
			</td>
		</tr>
		<tr id="sezioneErroreConc" style="display: none;">
			<td colspan="2">
				<br>
				<span id="msgEsitoErrore">Si è presentato un errore durante l'esecuzione dell'operazione.</span>
				<br>
				<br>
			</td>
		</tr>
	</table>
</div>

<script type="text/javascript"> 

function apriModaleConclusione() {

	pingSessioneAttiva();
	
	var ngara="${ngara}";
	var isriconclusa="${isriconclusa}";
	if(!isriconclusa)
		isriconclusa='0';
	var controlliSuperati = !(${blocco1} || ${blocco2});
	
	var widthM= 500;
	var	heightM= 300;
	
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
		draggable: false,
		resizable: false,
		width: widthM,
		height:heightM,
		title: isriconclusa=='1'?'Annulla conclusione secondo grado concorso di progettazione':'Conclusione secondo grado concorso di progettazione',
		buttons: {
			"Conferma": {
				id:"botConfermaConc",
				text:"Conferma",
				click: function() {
					setIsriconclusa(ngara,isriconclusa);
				}
			 },
			"Annulla": {
				id:"botAnnullaConc",
				text:"Annulla",
				click:function() {
					$( this ).dialog( "close" );
					$("#sezioneInizialeConc").hide();
					$("#sezioneErroreConc").hide();
				}
			 },
			 "Chiudi": {
				id:"botChiudiConc",
				text:"Chiudi",
				click:function() {
					$( this ).dialog( "close" );
					$("#sezioneInizialeConc").hide();
					$("#sezioneErroreConc").hide();
					if(controlliSuperati){
						ricaricaPagina();
					}
				}
			 }
		 }
	};
	
	$("#mascheraGestioneConclusione").dialog(opt).dialog("open");
	$("#sezioneInizialeConc").show();
	if(controlliSuperati){
		$("#botChiudiConc").hide();
	}else{
		$("#botConfermaConc").hide();
		$("#botAnnullaConc").hide();
	}
}

function setIsriconclusa(ngara,isriconclusa){

	$("#sezioneInizialeConc").hide();
	
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
		url: "pg/SetIsriconclusa.do?ngara=" + ngara+"&isriconclusa="+isriconclusa,
		success: function(res){
			esito=res.Esito;
			msgEsito = res.MsgErrore;
		},
		error: function(e){
			alert("Errore generico durante l'operazione");
		},
		complete: function(jqXHR, textStatus) {
			
			if(textStatus=="error" || textStatus=="parsererror"){	
				$("#sezioneErroreConc").show();
				$("#botConfermaConc").hide();
				$("#botAnnullaConc").hide();
				$("#botChiudiConc").show();
				
            }else{
				if(esito=="Errore"){
					$("#botConfermaConc").hide();
					$("#botAnnullaConc").hide();
					$("#msgEsitoErroreConc").html(msgEsito);
					$("#sezioneErroreConc").show();	
					$("#botChiudiConc").show();
				}else{
					$("#mascheraGestioneConclusione").dialog( "close" );
					ricaricaPagina();
				}
            }
			
		}
	});
}

</script>



