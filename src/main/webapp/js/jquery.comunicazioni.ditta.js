/*
*	Funzioni di supporto per la gestione
*	della pagina imprdocg-pg-comunicazioni.jsp.
*
*/

var percorsoImg;
var comkey1;
var soccorsoIstruttorio;
var stepWizard;
var busta;
var bustaDescr;
var genereGara;
var ngara;
var ditta ;
var codiceGara ;
var aut;

function _setPercorsoImg(valore){
	percorsoImg = valore;
}


$(window).ready(function (){
	var _tableComunicazioni = null;
	soccorsoIstruttorio = $("#soccorsoIstruttorio").val();
	stepWizard = $("#stepWizard").val();
	
	//Si allarga la dimensione della finestra
	//$(".contenitore-dettaglio").css( "height", "400px" );
	
	/*
	 * Funzione di attesa
	 */
	function _wait() {
		document.getElementById('bloccaScreen').style.visibility='visible';
		$('#bloccaScreen').css("width",$(document).width());
		$('#bloccaScreen').css("height",$(document).height());
		document.getElementById('wait').style.visibility='visible';
		$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
	}


	/*
	 * Nasconde l'immagine di attesa
	 */
	function _nowait() {
		document.getElementById('bloccaScreen').style.visibility='hidden';
		document.getElementById('wait').style.visibility='hidden';
	}



	/*
	 * Popola la lista delle comunicazioni.
	 */
	function _popolaDatiComunicazioni() {
		
		_wait();
		
		ngara = $("#ngara").val();
		ditta = $("#ditta").val();
		codiceGara = $("#codiceGara").val();
		genereGara = $("#genereGara").val();
		var riceviComunicazioni = $("#riceviComunicazioni").val();
		var whereBusteAttiveWizard = $("#whereBusteAttiveWizard").val();
		aut = $("#aut").val();
		
		$.ajax({
	        type: "POST",
	        dataType: "json",
	        async: true,
	        beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
		       }
			},
	        url: "pg/GetDatiComunicazioniDitte.do",
	        data : "ngara=" +  ngara + "&ditta=" + ditta + "&genereGara=" + genereGara + "&riceviComunicazioni=" + riceviComunicazioni
	        	+"&codgar=" + codiceGara + "&soccorsoIstruttorio=" + soccorsoIstruttorio + "&whereBusteAttiveWizard=" + whereBusteAttiveWizard,
	        success: function(json) {
	        	_popolaTabellaComunicazioni(json.data);
				if(integrazioneWSDM != 1){
					_tableComunicazioni.column( 3 ).visible( false );
				}
	        	comkey1 = json.comkey1;
	        },
	        error: function(e){
	            alert("Errore durante la lettura delle comunicazioni");
	        },
	        complete: function() {
	        	_nowait();
	        }
	    });
	}


	/*
	 * Popola la tabella delle comunicazioni
	 */
	function _popolaTabellaComunicazioni(comunicazioni) {
		
		if (_tableComunicazioni != null) {
			_tableComunicazioni.destroy(true);
		}
		
		_setPercorsoImg($("#percorsoCartellaImgTabella").val());
		
		var _table = $('<table/>', {"id": "comunicazioni", "class": "comunicazioni", "cellspacing": "0", "width" : "100%"});
		var _thead = $('<thead/>');
		var _tr = $('<tr/>');
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_tr.append($('<th/>'));
		_thead.append(_tr);
		_table.append(_thead);
		var _tbody = $('<tbody/>');
		_table.append(_tbody);
		$("#comunicazioniContainer").append(_table);
		
		_tableComunicazioni = $('#comunicazioni').DataTable( {
			"data": comunicazioni,
			paging: false,
			"searching": false,
			"scrollCollapse": false,
			"scrollY": "300px",
			"scrollX": false,
			"columnDefs": [
			    {
					"data": "tipo",
					"visible": true,
					"orderable": false,
					"targets": [ 0 ],
					"sWidth": "20px",
					"sClass": "aligncenter",
					"defaultContent": '',
					"mRender": function (data, type, full) {
						if(type == "display"){
	    					var titolo="Comunicazione ";
	    					var immagine=percorsoImg;
							if(data=="inv"){
	    						titolo+="inviata alla ditta";
	    						immagine += "com_inv.png";
	    					}else{
	    						titolo+="ricevuta dalla ditta";
	    						immagine += "com_ric.png";
	    					}
							
							var strImg = '<img width="16" height="16" title="' + titolo + '" alt="' + titolo + '" src="' + immagine + '"/>'
							return strImg;
						}
					}
				},
				{
					"data": "oggetto",
					"visible": true,
					"targets": [ 1 ],
					"sTitle": "Oggetto"
				},
				{
					"data": "dataInvioInserimento",
					"visible": true,
					"sTitle": "Data invio",
					"targets": [ 2 ],
					"sWidth": "120px",
					"mRender": function (data, type, row) {
						if(data!=null && type == "display"){
	    					var d = new Date();
	    					d.setTime(data);
	    					var g= d.getDate();
	    					if(g<10)
	    						g = '0' + g;
	    					var m = d.getMonth() + 1;
	    					if(m<10)
	    						m = '0' + m;
	    					var a = d.getFullYear(); 
	    					var o = d.getHours();
	    					if(o<10)
	    						o = '0' + o;
	    					var mm = d.getMinutes();
	    					if(mm<10)
	    						mm = '0' + mm;
	    					var s = d.getSeconds();
	    					if(s<10)
	    						s = '0' + s;
	    					var dataString = g + '/' + m + '/' + a + ' ' + o + ':' + mm + ':' + s;
	    					var stato = row.stato;
	    					if(stato=="3" || stato=="5"){
	    						msgErrore = "Mail non inviata in seguito a errori";
	    						dataString+='&ensp; <img width="16" height="16" alt= "' + msgErrore + '" title="' + msgErrore + '"  src="' + percorsoImg + '/isquantimod.png"/>';
	    					}
	    					return dataString;
						}else {
							return data;
						}
					}
				},
				{
					"data": "protocollo",
					"visible": true,
					"targets": [ 3 ],
					"sTitle": "Num.prot."
				},
				{
					"data": "testo",
					"visible": true,
					"targets": [ 4 ],
					"sTitle": "Testo"
				},
				{
					"data": "descBusta",
					"visible": (soccorsoIstruttorio=='1' && (genereGara == '1' || genereGara == '2' || genereGara == '3')),
					"targets": [ 5 ],
					"sTitle": "Busta"
				},
				{
			        "className":      'details-control',
			        "orderable":      false,
			        "data":           null,
			        "targets": [ 6 ],
			        "sWidth": "20px",
			        "defaultContent": '',
			        "fnCreatedCell": function (nTd, sData, oData, iRow, iCol) {
			            if(oData.allegati == 'no'){
			            	//$(nTd).html("<b>Offerta iniziale</b>");
			            	$(nTd).removeClass('details-control');
			            }
					}
			    },
				{
					"data": "idprg",
					"visible": false,
					"targets": [ 7 ]
				},
				{
					"data": "idcom",
					"visible": false,
					"targets": [ 8 ]
				},
				{
					"data": "comtipo",
					"visible": false,
					"targets": [ 9 ]
				},
				{
					"data": "allegati",
					"visible": false,
					"targets": [ 10 ]
				},
				{
					"data": "stato",
					"visible": false,
					"targets": [ 11 ]
				},
				{
					"data": "comtipma",
					"visible": false,
					"targets": [ 12 ]
				}
			],
	        "language": {
				"sEmptyTable":     "",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ comunicazioni",
				"sInfoEmpty":      "Nessun elemento estratto",
				"sInfoFiltered":   "(su _MAX_ elementi documentali totali)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_ comunicazioni",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sZeroRecords":    ""
			},
			complete: function() {
				_nowait();
			},
			"order": [[ 2 , "desc" ]]
	    });
		
		var marginiSin=[];
	    
		//Si prende il margine sinistro di ogni cella
		$('#comunicazioni th').each(function() {
			marginiSin.push($(this).position().left);
			$(this).position();
		});		

		$('#comunicazioni').on('click', 'td.details-control', function () {
	        var tr = $(this).closest('tr');
	        var row = _tableComunicazioni.row(tr);

	        if (row.child.isShown()) {
	            row.child.hide();
	            tr.removeClass('shown');
	        } else {
	        	var idprg=row.data().idprg;
	        	var idcom=row.data().idcom;
	            var comdatins = row.data().dataInvioInserimento;
	        	var comtipo = row.data().comtipo;
	        	var descBusta = row.data().descBusta;
	        	var comtipma = row.data().comtipma;
	        	if(comtipma==null)
	        		comtipma = "";
	        	if(descBusta==null)
	        		descBusta = "";
	            format(row.child,idprg,idcom,comdatins,comtipo,descBusta,comtipma);
	        	tr.addClass('shown');
	        	
	        }
	    });
		
		$('#comunicazioni').on('click', 'span.spanCopiaAllegato', function () {
	        var tr = $(this).closest('tr');
	        var iddocdig =tr.children('td.iddocdig').text();
	        var idprg =tr.children('td.idprg').text();
	        var idprgCom =tr.children('td.idprg').text();
	        var idcom =tr.children('td.idcom').text();
	        var nomeAllegato =tr.children('td.nomeAllegato').text();
	        var datainserimento = tr.children('td.comdatins').text();
	        var descbusta = tr.children('td.descbusta').text();	 
	        var comtipma = tr.children('td.comtipma').text();	      
	       if(genereGara == 2 || genereGara == 3  ){
	        	if(descbusta == "" || descbusta == null || comtipma == null || comtipma == ""){
	        		getBusta();
	        	}else{
	        		busta=comtipma;
					bustaDescr=descbusta;
					$("#spanBusta").text(bustaDescr);
	        	}
	        	$("#spanMsgBusta").show();
	        }else{
	        	busta="";
	        	bustaDescr="";
	        	$("#spanMsgBusta").hide();
	        }
        	apriFinestaModale(idprg,iddocdig,idprgCom,idcom,datainserimento);
	    });
		
		/*
		 * Inserimento di un tooltip sulla colonna
		 */
		$('#comunicazioni').on('mouseenter', 'td.details-control', function () {
			var tr = $(this).closest('tr');
			var row = _tableComunicazioni.row(tr);
			if (row.child.isShown()) {
				$(this).css('cursor','pointer').attr('title', 'Chiudi dettaglio allegati');
			}else{
				$(this).css('cursor','pointer').attr('title', 'Apri dettaglio allegati');
			}
	    });
		
		function format(callback,idprg,idcom,comdatins,comtipo,descBusta,comtipma) {
	        _wait();
			
			$.ajax({
		        type: "POST",
		        dataType: "json",
		        async: true,
		        beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
			       }
				},
		        url: "pg/GetAllegatiComunicazioniDitte.do",
		        data : "idprg=" +  idprg + "&idcom=" + idcom + "&comtipo=" + comtipo,
		        success: function(json) {
		        	if (json) {
		        		var thead = '',  tbody = '';
		        		$.each(json.data, function (i, d) {
		        			tbody += '<tr>';
		        			tbody += '<td class="descrizione">' + d.descrizione + '</td>';
							tbody += '<td class="nomeAllegato"><span title="Scarica allegato" class="spanNomeAllegato"><a>'+ d.nome + '</a></span>';
		        			if(comtipo == 'FS12' && aut != '2'){
		        				var strImg = '  <span class="spanCopiaAllegato"><a><img width="16" height="16" title="Copia allegato" alt="Copia allegato" src="' + percorsoImg + 'copiaAlle.png"/></a></span>' ;
		        				tbody += strImg;
		        			}
		        			tbody += '</td>';
		        			tbody += '<td class="idprg">'+ d.idprg + '</td>';
		        			tbody += '<td class="iddocdig">'+ d.iddocdig + '</td>';
		        			tbody += '<td class="comdatins">'+ comdatins + '</td>';
		        			tbody += '<td class="comtipo">'+ comtipo + '</td>';
		        			tbody += '<td class="idcom">'+ idcom + '</td>';
		        			tbody += '<td class="idprgCom">'+ idprg + '</td>';
		        			tbody += '<td class="descbusta">'+ descBusta + '</td>';
		        			tbody += '<td class="comtipma">'+ comtipma + '</td>';
		        			tbody += '</tr>';
						});
		        		callback($('<table class="allegati" id="allegati" >' + tbody + '</table>'),"rowChildAllegati").show();
		        		
		        		//Si cerca di allineare il margine sinistro della cella della descrizione con quello dell'oggetto
		        		$('#allegati td').each(function() {
		        			if($(this).attr('class')=="descrizione"){
		        				$(this).css("margin-left",marginiSin[1]+"px");
		        			}
		        			
		        		});
		        		
		        	}
		        },
		        error: function(e){
		            alert("Errore durante la lettura delle comunicazioni");
		        },
		        complete: function() {
		        	_nowait();
		        }
		    });
		}
		
		$('#comunicazioni').on('click', 'span.spanNomeAllegato', function () {
	        var tr = $(this).closest('tr');
	        var iddocdig =tr.children('td.iddocdig').text();
	        var idprg =tr.children('td.idprg').text();
	        var nomeAllegato =tr.children('td.nomeAllegato').text();
	        var datainserimento = tr.children('td.comdatins').text();
	        var comtipo = tr.children('td.comtipo').text();
	        visualizzaFileAllegato( idprg ,iddocdig , nomeAllegato ,comtipo, datainserimento);
	    });
	}
		
	_popolaDatiComunicazioni();
	
	
	function visualizzaFileAllegato(idprg,iddocdig,dignomdoc,comtipo,datainserimento) {
		var contextPath = $("#contextPath").val();
		var vet = dignomdoc.split(".");
		var ext = vet[vet.length-1];
		ext = ext.toUpperCase();
		
		var ngara = $("#ngara").val();
		var ditta = $("#ditta").val();
		var codiceGara = $("#codiceGara").val();
		var genereGara = $("#genereGara").val();
		var chiaveTracciaturaFS12 = ngara;
		if(genereGara != 2)
			chiaveTracciaturaFS12 = codiceGara;
		
		if(ext=='P7M' || ext=='TSD'){
			var href = "href=gene/system/firmadigitale/verifica-firmadigitale-popUp.jsp";
			href += "&idprg=" + idprg;
			href += "&iddocdig=" + iddocdig;
			if(idprg == 'PA' && comtipo == 'FS12'){
				var d = new Date();
				d.setTime(datainserimento);
				var g= d.getDate();
				if(g<10)
					g = '0' + g;
				var m = d.getMonth() + 1;
				if(m<10)
					m = '0' + m;
				var a = d.getFullYear(); 
				var data = String(a) + String(m) + String(g);
				var hh= d.getHours();
				if(hh < 10)
					hh = '0' + hh;
				var mm = d.getMinutes();
				if(mm < 10)
					mm = '0' + mm;
				var ss = d.getSeconds();
				if(ss < 10)
					ss = '0' + ss;
				data += " " + String(hh) + ":"+ String(mm) +":"+ String(ss);
				href += "&ckdate=" + data;
				tracciamentoDownloadFS12(idprg, iddocdig,chiaveTracciaturaFS12,comkey1);
			}else{
				tracciamentoDownloadDocimpresa(idprg, iddocdig,ngara,ditta,"2");
			}
				
			openPopUpCustom(href, "DownloadP7M", 900, 550, "yes", "yes");
		}else{
			if(idprg == 'PA' && comtipo == 'FS12'){
				tracciamentoDownloadFS12(idprg, iddocdig,chiaveTracciaturaFS12,comkey1);
			}else{
				tracciamentoDownloadDocimpresa(idprg, iddocdig,ngara,ditta,"2");
			}
			var href = contextPath + "/pg/VisualizzaFileAllegato.do";
			document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
		}
	}
	
	/*
	 * Apertura della finestra modale per la copia degli allegati
	 */
	function apriFinestaModale(idprg,iddocdig,idprgCom,idcom,comdatins){
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
			autoOpen: false,
			modal: true,
			width: 450,
			height:200,
			title: 'Copia allegato comunicazione',
			buttons: {
				
				"Conferma": {
					text: "Conferma",
					id: "bt_conferma",
					click: function() {
						copiaAllegati(idprg,iddocdig,idprgCom,idcom,comdatins);
					}
				},
				"Annulla": function() {
					$( this ).dialog( "close" );
					$("#spanErrore").hide();
		        	$("#bt_conferma").show();
				}
			}
		};
		
		$("#spanErrore").hide();
		$("#mascheraCopiaAllegati").dialog(opt).dialog("open");
	}
	
	function copiaAllegati(idprg,iddocdig,idprgCom,idcom,comdatins){
		_wait();
		$.ajax({
	        type: "POST",
	        dataType: "json",
	        async: true,
	        beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
		       }
			},
	        url: "pg/CopiaAllegatiComunicazioniDitte.do",
	        data : "ngara=" +  ngara + "&ditta=" + ditta + "&genereGara=" + genereGara +"&codgar=" + codiceGara 
	        	+ "&idprg=" + idprg + "&iddocdig=" + iddocdig + "&idcom=" + idcom + "&comdatins=" + comdatins + "&busta=" + busta
	        	+ "&soccorsoIstruttorio=" + soccorsoIstruttorio + "&bustaDescr=" + bustaDescr + "&idprgCom=" + idprgCom,
	        success: function(json) {
	        	if (json) {
		        	var esito = json.esito;
		        	
		        	if(esito!="1"){
		        		$("#spanErrore").show();
		        		$("#bt_conferma").hide();
		        	}else{
		        		$("#mascheraCopiaAllegati").dialog("close");
		        		$("#spanErrore").hide();
		        	}
		        }
	        },
	        error: function(e){
	            alert("Errore durante la copia dell'allegato");
	        },
	        complete: function() {
	        	_nowait();
	        }
	    });
	}
	
	function getBusta(){
		_wait();
		$.ajax({
	        type: "POST",
	        dataType: "json",
	        async: false,
	        beforeSend: function(x) {
			if(x && x.overrideMimeType) {
				x.overrideMimeType("application/json;charset=UTF-8");
		       }
			},
	        url: "pg/GetBustaDaStep.do",
	        data : "stepWizard=" +  stepWizard + "&genereGara=" + genereGara,
	        success: function(json) {
	        	if (json) {
					busta=json.busta;
					bustaDescr=json.bustaDescr;
				}
	        	$("#spanBusta").text(bustaDescr);
	        },
	        error: function(e){
	            alert("Errore durante la lettura del tipo di busta " + e);
	        },
	        complete: function() {
	        	_nowait();
	        }
	    });
	}
	
});
