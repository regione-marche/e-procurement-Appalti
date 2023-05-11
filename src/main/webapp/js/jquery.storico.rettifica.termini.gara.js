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
function caricamentoStoricoRettificaTermini(codgar, tipo, contextPath, visualizzaRichiestaChiarimenti ,visualizzaRispostaChiarimenti) {
         $.ajax({
             type: "GET",
             dataType: "json",
             async: false,
             beforeSend: function(x) {
 				if(x && x.overrideMimeType) {
     				x.overrideMimeType("application/json;charset=UTF-8");
		    	}
			 },
             url: contextPath + "/pg/GetRettificaTermini.do",
             data: "codgar=" + codgar + "&tipo=" + tipo,
             success: function(data){
             	if (data && data.length > 0) {
					var _intestazione='<tr style="BACKGROUND-COLOR: #EFEFEF;">';
					_intestazione+='<td colspan="2" class="titolo-valore-dato">Data termine</td>';
					_intestazione+='<td colspan="2" class="titolo-valore-dato">Ora termine</td>';
					if(tipo!=3){
						if(visualizzaRichiestaChiarimenti=="true"){
							_intestazione+='<td colspan="2" class="titolo-valore-dato">Data termine rich.chiarimenti</td>';
						}
						if(visualizzaRispostaChiarimenti=="true"){
							_intestazione+='<td colspan="2" class="titolo-valore-dato">Data termine risp.chiarimenti</td>';
						}
						
					}
					_intestazione+='<td colspan="2" class="titolo-valore-dato">Data e ora operazione di rettifica</td>';
					_intestazione+='</tr>';
             		if(tipo==1)
             			$('#tabellaRettificaTerminiPartecipaz ').append(_intestazione);
             		else if(tipo==2)
             			$('#tabellaRettificaTerminiOfferta').append(_intestazione);
             		else if(tipo==3)
             			$('#tabellaRettificaTerminiApertura').append(_intestazione);
             		
             		$.map( data, function( item ) {
             			var _riga = '<tr >';
             			//1 - data termine
             			var DataTermine=item[1];
             			_riga += '<td colspan="2" class="valore-dato">' + DataTermine + '</td>';
             			//2 - ora termine
             			var OraTermine=item[2];
             			_riga += '<td colspan="2" class="valore-dato">' + OraTermine + '</td>';
             			if(tipo!=3){
             				//3 data richiesta chiarimenti
             				if(visualizzaRichiestaChiarimenti=="true"){
	             				var DataRichiestaChiarimenti = item[3];
	             				if(DataRichiestaChiarimenti==null)
	             					DataRichiestaChiarimenti ="";
	             				_riga += '<td colspan="2" class="valore-dato">' + DataRichiestaChiarimenti + '</td>';
             				}
             				//4 data risposta chiarimenti
             				if(visualizzaRispostaChiarimenti=="true"){
	             				var DataRispostaChiarimenti = item[4];
	             				if(DataRispostaChiarimenti==null)
	             					DataRispostaChiarimenti = "";
	             				_riga += '<td colspan="2" class="valore-dato">' + DataRispostaChiarimenti + '</td>';
             				}
             			}
             			//0 - data rettifica
             			var dataRettifica = item[0];
             			_riga += '<td colspan="2" class="valore-dato">' + dataRettifica + '</td>';
             			_riga += '</tr>';
             			if(tipo==1)
             				$('#tabellaRettificaTerminiPartecipaz').append(_riga);
             			else if(tipo==2)
             				$('#tabellaRettificaTerminiOfferta').append(_riga);
             			else if(tipo==3)
             				$('#tabellaRettificaTerminiApertura').append(_riga);
             				
             		});
             	}
            	
             },
             error: function(e){
                 alert("Errore nella lettura dello storico delle rettifiche");
             }
         });
     }