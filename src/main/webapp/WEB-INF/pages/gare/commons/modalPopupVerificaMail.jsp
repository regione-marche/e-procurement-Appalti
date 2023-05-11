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

<style type="text/css">
	div.ui-dialog-titlebar {
		border: 0px;
		background: white;
	}
	.ui-dialog-title{
		font-size: 13px;
		font-weight: bold;
		color:rgb(0, 40, 86);
	}
	.ui-dialog-titlebar-close{
		display:none;
	}
	.ui-dialog-buttonpane{
		background:white;
	}
	  #avanzamento{
    position: relative;
	}
	
	#modalbody{
		max-height: 350px;
		overflow-y:auto;
	}
	#modalbody table{
		width:100%;
		margin-bottom: 30px;
		border-collapse: collapse;
	}
	#modalbody table, #modalbody td{
		border: 1px solid #A0AABA;
		background: white;
		text-align: center;
		font-size: 11px;
	}
	
	#modalbody .destinatario{
		font-weight: bold;
		background-color: #CCE0FF
	}
	#modalbody th{
		background: #EFEFEF;
		border: 1px solid #A0AABA;
		height:20px;
	}
	#modalbody td{
		height:30px;
	}
</style>
  
<div id="dialog-form" style="display:none">
	<p class="validateTips">
	</p>
	<div id="modalbody">
	</div>
	<br>
	<span id="errorMessage" style="color:red;display:none"><br></span>

</div>

<script type="text/javascript"> 



function _nowait() {
	document.getElementById('bloccaScreen').style.visibility = 'hidden';
	document.getElementById('wait').style.visibility = 'hidden';
}

var annullato = false;
var archivioCreato = "false";

function openModal(idcom,idprg,idconfi,contextPath){
	_wait();
	$( "#dialog-form" ).css({
		"display":"block",
	});
	$( "#dialog-form" ).dialog({
	  resizable: false,
	  height: "auto",
	  title: "Verifica invio comunicazione in carico al documentale",
	  width: 800,
	  position: { my: "center", of: ".contenitore-arealavoro"},
	  modal: true,
	  close: function() {
	  },
	  buttons: [{
			text:"Chiudi",
			click: function() {
			$("#modalbody").empty();
			$( this ).dialog( "close" );
			}
		}
		]
	});
	ajaxCallVerificMail(idcom,idprg,idconfi);
	
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
	
	
	function ajaxCallVerificMail(idcom,idprg,idconfi){
		$.ajax({
			url: "${pageContext.request.contextPath}/pg/VerificaInvioComunicazioni.do",
			data: {
				idcom: idcom,
				idprg: idprg,
				idconfi: idconfi
			},
			async: true,
			dataType: "json",
			success:function(data){
				_nowait();
				 $(function() {
					var tabellaCreata = false; 
					var tablearea = document.getElementById('modalbody');
					$.each(data, function(i, destinatari) {
						
						var table = document.createElement('table');
						table.setAttribute("class","schedagperm"); 
						
						tabellaCreata = true;
						var trDestinatario = document.createElement('tr');
						var tdDestinatario = document.createElement('td');
						tdDestinatario.setAttribute('colspan', '3');
						tdDestinatario.setAttribute('class', 'instestazione destinatario');
						tdDestinatario.appendChild(document.createTextNode(destinatari.destinatarioDescr + " - " + destinatari.destinatarioEmail));
						trDestinatario.appendChild(tdDestinatario);
						table.appendChild(trDestinatario);
						
						var trIntestazione = document.createElement('tr');
						
						var thDataSpedizione = document.createElement('th');
						var thtipoSpedizione = document.createElement('th');
						var thmessaggioStato = document.createElement('th');	
						
						thDataSpedizione.appendChild(document.createTextNode("Data e ora"));
						thtipoSpedizione.appendChild(document.createTextNode("Tipo"));
						thmessaggioStato.appendChild(document.createTextNode("Stato spedizione"));
						
						trIntestazione.appendChild(thDataSpedizione);
						trIntestazione.appendChild(thtipoSpedizione);
						trIntestazione.appendChild(thmessaggioStato);
						table.appendChild(trIntestazione);
							
						var arrayInterop = destinatari.interopArray;
						$.each(arrayInterop, function(j, infoInterop) {
							var tr = document.createElement('tr'); 
							tr.setAttribute("class","interop");
							var tdstatoSpedizione = document.createElement('td');
							var tdtipoSpedizione = document.createElement('td');
							var tdmessaggioId = document.createElement('td');
							var tddataOra = document.createElement('td');
							tdstatoSpedizione.appendChild(document.createTextNode(infoInterop.statoSpedizione));
							tdtipoSpedizione.appendChild(document.createTextNode(infoInterop.tipoSpedizione));
							tdmessaggioId.appendChild(document.createTextNode(infoInterop.messaggioId));
							tddataOra.appendChild(document.createTextNode(infoInterop.dataOra));
							
							tr.appendChild(tddataOra);
							tr.appendChild(tdtipoSpedizione);
							tr.appendChild(tdstatoSpedizione);
							table.appendChild(tr);
						});
						tablearea.appendChild(table);
					});
					if(!tabellaCreata){
						var p = document.createElement("p");
						var message = document.createTextNode("Nessuna irregolarità segnalata.");
						p.appendChild(message);
						tablearea.appendChild(p);
					}
				});
			},
			error:function(){
				_nowait();
				$("#modalbody").empty();
				$( "#dialog-form"  ).dialog( "close" );
				alert("Errore nella chiamata al servizio");
			}
		});
	}
	

</script>



