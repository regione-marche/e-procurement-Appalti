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
					$('#okrettbtn, #korettbtn').hide();
					$('#okrettlink, #korettlink').closest('tr').hide();
             	}else{
					$("#rigaElencoRisposte").hide();
				}
             },
             error: function(e){
                 alert("Errore nella lettura dello storico delle risposte");
             }
         });
     }
 
 function caricamentoStoricoInoltriComunicazioni(idprg, idcom, contextPath) {
     $.ajax({
         type: "GET",
         dataType: "json",
         async: false,
         beforeSend: function(x) {
			if(x && x.overrideMimeType) {
 				x.overrideMimeType("application/json;charset=UTF-8");
	    	}
		 },
         url: contextPath + "/pg/GetStoricoInoltri.do",
         data: "idprg=" + idprg + "&idcom=" + idcom,
         success: function(data){
			 
         	if (data && data.length > 0) {
				var _intestazione='<tr style="BACKGROUND-COLOR: #EFEFEF;">';
				_intestazione+='<td class="titolo-valore-dato" style="width:25%;">Oggetto</td>';
				_intestazione+='<td class="titolo-valore-dato" style="width:15%;">Data inserimento</td>';
				_intestazione+='<td class="titolo-valore-dato" style="width:10%;">Stato</td>';
				_intestazione+='<td class="titolo-valore-dato" style="width:50%;">Destinatari</td>';
				_intestazione+='</tr>';
         		$('#tabellaStoricoInoltri').append(_intestazione);
         		
         		$.map( data, function( item ) {
         			var _riga = '<tr >';
         			var oggetto = item[0];
         			_riga += '<td class="valore-dato">' + oggetto + '</td>';
         			var dataInserimento = item[1];
         			_riga += '<td class="valore-dato">' + dataInserimento + '</td>';
         			var stato = item[2];
         			_riga += '<td class="valore-dato">' + stato + '</td>';
         			var destinatari = item[3];
         			_riga += '<td class="valore-dato">' + destinatari + '</td>';
         			_riga += '</tr>';
					
					$('#tabellaStoricoInoltri').append(_riga);
         				
         		});
         	}else{
				$("#rigaElencoInoltri").hide();
			}
         },
         error: function(e){
             alert("Errore nella lettura dello storico degli inoltri");
         }
     });
 }