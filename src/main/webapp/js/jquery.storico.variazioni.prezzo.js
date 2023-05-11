/*
 * Gestione della visualizzazione dello storico delle variazioni dei termini di gara
 * 
 */

/*
 * Genera lo storico per le variazioni prezzo
 * @param ngara
 * @param contaf
 * @param ditta
 * @param contextPath
 */
function caricamentoStoricoRettificaTermini(ngara, contaf, ditta, contextPath) {
         $.ajax({
             type: "GET",
             dataType: "json",
             async: false,
             beforeSend: function(x) {
 				if(x && x.overrideMimeType) {
     				x.overrideMimeType("application/json;charset=UTF-8");
		    	}
			 },
             url: contextPath + "/pg/GetVariazioniPrezzo.do",
             data: "ngara=" + ngara + "&contaf=" + contaf + "&ditta=" + ditta,
             success: function(data){
             	if (data && data.length > 0) {
					var _intestazione='<tr style="BACKGROUND-COLOR: #EFEFEF;">';
					_intestazione+='<td colspan="2" class="titolo-valore-dato">Numero operazione</td>';
					_intestazione+='<td colspan="2" class="titolo-valore-dato">Prezzo unitario</td>';
					_intestazione+='<td colspan="2" class="titolo-valore-dato">Importo totale</td>';
					_intestazione+='<td colspan="2" class="titolo-valore-dato">Data e ora operazione di variazione</td>';
					_intestazione+='</tr>';
					$('#tabellaVariazionePrezzo ').append(_intestazione);             		
             		$.map( data, function( item ) {
             			var _riga = '<tr >';
             			//1 - Numero variazione
             			var numVariazioni=item[0];
             			_riga += '<td colspan="2" class="valore-dato">' + numVariazioni + '</td>';
             			//2 - Prezzo unitario variato
             			var prezzo=item[1];
             			if(prezzo==null) prezzo='';
             			_riga += '<td colspan="2" class="valore-dato">' + prezzo + '</td>';
             			//3 - Importo
             			var importo=item[2];
             			if(importo==null) importo='';
             			_riga += '<td colspan="2" class="valore-dato">' + importo + '</td>';
             			//4 - data variazione
             			var dataVariazione = item[3];
             			_riga += '<td colspan="2" class="valore-dato">' + dataVariazione + '</td>';
             			_riga += '</tr>';
             			$('#tabellaVariazionePrezzo').append(_riga);
             		});
             	}
            	
             },
             error: function(e){
                 alert("Errore nella lettura dello storico delle variazioni prezzo");
             }
         });
     }


