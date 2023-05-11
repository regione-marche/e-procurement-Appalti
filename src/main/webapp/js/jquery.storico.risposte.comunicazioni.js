/*
 * Gestione della visualizzazione dello storico delle rettifiche dei termini di gara
 * 
 */

/*
 * Genera lo storico per la rettifica dei termini di gara
 * @param {string} ngara codice della gara
 * @param {string} tipo tipo rettifica(1: termini partecipazione, 2: termini offerta, 3: apertura Plichi)
 * @returns {undefined}
 */
function caricamentoStoricoRisposteComunicazioni(idprg, idcom, contextPath) {
         $.ajax({
             type: "GET",
             dataType: "json",
             async: false,
             beforeSend: function(x) {
 				if(x && x.overrideMimeType) {
     				x.overrideMimeType("application/json;charset=UTF-8");
		    	}
			 },
             url: contextPath + "/pg/GetStoricoRisposte.do",
             data: "idprg=" + idprg + "&idcom=" + idcom,
             success: function(data){
				 
             	if (data && data.length > 0) {
					var _intestazione='<tr style="BACKGROUND-COLOR: #EFEFEF;">';
					_intestazione+='<td class="titolo-valore-dato">Oggetto</td>';
					_intestazione+='<td class="titolo-valore-dato">Data inserimento</td>';
					_intestazione+='<td class="titolo-valore-dato">Stato</td>';
					_intestazione+='</tr>';
             		$('#tabellaStoricoRisposte').append(_intestazione);
             		
             		$.map( data, function( item ) {
             			var _riga = '<tr >';
             			var oggetto = item[0];
             			_riga += '<td class="valore-dato">' + oggetto + '</td>';
             			var dataInserimento = item[1];
             			_riga += '<td class="valore-dato">' + dataInserimento + '</td>';
             			var stato = item[2];
             			_riga += '<td class="valore-dato">' + stato + '</td>';
             			_riga += '</tr>';
						
						$('#tabellaStoricoRisposte').append(_riga);
             				
             		});
             	}else{
					$("#rigaElencoRisposte").hide();
				}
             },
             error: function(e){
                 alert("Errore nella lettura dello storico delle risposte");
             }
         });
     }