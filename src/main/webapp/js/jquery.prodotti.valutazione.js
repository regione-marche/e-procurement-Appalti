/*
 * Gestione del carrello.
 * 
 */


$(window).ready(function (){
	
	/*
	 * Tabella listaProdotti gestita da DataTable()
	 */
	var tableProdottiArticolo;
	
	
	/*
	 * Numero massimo confronto prezzi
	 */ 
	var numeroMassimoConfrontoPrezzi = 4;
	
	/*
	 * Data ed ora della valutazione prodotti
	 */
	$("#dataOraValutazioneProdotti").text($("#DATVAL_CAL").val());
	
	/*
	 *  Dimensionamento del dettaglio
	 */
	$(".dettaglio-tab").width("980");
	$(".contenitore-dettaglio").width($(".dettaglio-tab").width()); 
	
	
	/*
	 * Caricamento drop down con il numero massimo di migliori prezzi
	 */ 
	_popolaNumeroMiglioriPrezzi();
	
	
	/*
	 * Caricamento lista delle imprese per il carrello di confronto.
	 */ 
	_popolaImprese($("#MERIC_ID").val());	
	$("#imprese").width(124);
	
	/*
	 * Gestione drop down imprese
	 */
	if ($("#MODOAPERTURA").val() == "VISUALIZZA") {
		$.cookie($("#MERIC_ID").val() + "_codimp", null);
	} else {
		var codimp = $.cookie($("#MERIC_ID").val() + "_codimp");
		if (codimp != null) {
			$("#imprese").val(codimp).attr("selected", "selected");
		}
	}
	
	/*
	 * Caricamento del carrello per la ricerca di mercato
	 */ 
	_wait();
	var numeroMiglioriPrezzi = $('#numeroMiglioriPrezzi :selected').val();
	_popolaCarrello($("#MERIC_ID").val(), numeroMiglioriPrezzi);
	
	
	
	/*
	 * Attivazione del messaggio di attesa.
	 */
	function _wait() {
		document.getElementById('bloccaScreen').style.visibility='visible';
		document.getElementById('wait').style.visibility='visible';
		$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
	}
	
	
	/*
	 * Disattivazione del messaggio di attesa
	 */
	function _nowait() {
		document.getElementById('bloccaScreen').style.visibility='hidden';
		document.getElementById('wait').style.visibility='hidden';
	}
	
	
	/*
	 * Gestione drop down per la definizione del numero massimo di migliori prezzi
	 * visualizzabili nella lista principale.
	 */
	 function _popolaNumeroMiglioriPrezzi() {
		 
		 var selectMiglioriPrezzi = $('<select/>',{id: "numeroMiglioriPrezzi"});
		 for ( var i = 1; i < 9; i++) {
			 selectMiglioriPrezzi.append($("<option/>", {value: i, text: i }));
		 }
		 $("#numeroMiglioriPrezziContainer").append(selectMiglioriPrezzi);
		 
		 // Lettura del cookie ed eventuale salvataggio del valore di default 1
		 if ($.cookie('numeroMiglioriPrezzi') != "" && $.cookie('numeroMiglioriPrezzi') != "undefined" && $.cookie('numeroMiglioriPrezzi') != null) {
			 var numeroMiglioriPrezzi = $.cookie('numeroMiglioriPrezzi');
			 $("#numeroMiglioriPrezzi option[value='" + numeroMiglioriPrezzi + "']").attr('selected', 'selected');
		 } else {
			 var numeroMiglioriPrezzi = 1;
			 $.cookie('numeroMiglioriPrezzi', numeroMiglioriPrezzi);
			 $("#numeroMiglioriPrezzi option[value='" + numeroMiglioriPrezzi + "']").attr('selected', 'selected');
		 }
		 
		 if ($("#MODOAPERTURA").val() != "VISUALIZZA") {
			 $("#numeroMiglioriPrezzi").attr('readonly','readonly');
			 $("#numeroMiglioriPrezzi").prop("disabled", true);
			 $("#numeroMiglioriPrezzi").css('background-color','#EFEFEF');
		 }
	 }
	
	
	/*
	 * Formattazione dell'importo
	 */
    function _formattaImporto(importo) {
    	var importoFormattato = "";
    	if (importo != null) {
        	var field = $('<p/>',{text: importo});
        	field.formatCurrency({decimalSymbol: ",", digitGroupSymbol : ".", symbol: "", roundToDecimalPlace: "5"});

			var numero = field.text();
			if (numero.substring(numero.length - 1) == '0') {
				numero = numero.substring(0, numero.length - 1);
			}

			if (numero.substring(numero.length - 1) == '0') {
				numero = numero.substring(0, numero.length - 1);
			}	

			if (numero.substring(numero.length - 1) == '0') {
				numero = numero.substring(0, numero.length - 1);
			}			
			
        	importoFormattato = numero + " \u20AC";
    	}
    	return importoFormattato;
    }

    
	/*
	 * Formattazione dell'importo
	 */
    function _formattaQuantita(quantita) {
    	var quantitaFormattata = "";
    	if (quantita != null) {
        	var field = $('<p/>',{text: quantita});
        	
        	var numberOfDecimals = 0;
			if(Math.floor(quantita) === quantita) {
				numberOfDecimals = 0;
			} else {
				numberOfDecimals = quantita.toString().split(".")[1].length || 0; 
			}
			
			var roundToDecimalPlace = 0;
			if (numberOfDecimals > 0) {
				roundToDecimalPlace = numberOfDecimals;
			}
			
			if (roundToDecimalPlace > 5) {
				roundToDecimalPlace = 5;
			}
        	
        	field.formatCurrency({decimalSymbol: ",", digitGroupSymbol : ".", symbol: "", roundToDecimalPlace: roundToDecimalPlace});
        	quantitaFormattata = field.text();
    	}
    	return quantitaFormattata;
    }

    
    /*
     * Popola la tabella con la lista degli articoli e dei prodotti
     * (carica anche il carrello in bozza gia' inserito precedentemente).
     */
    function _popolaCarrello(meric_id, numeroMiglioriPrezzi) {
        $.ajax({
            type: "GET",
            dataType: "json",
            async: true,
            beforeSend: function(x) {
			if(x && x.overrideMimeType) {
    			x.overrideMimeType("application/json;charset=UTF-8");
		       }
			},
            url: "pg/GetRicercaMercatoProdottiValutazione.do",
            data: "meric_id=" + meric_id,
            success: function(data) {
            	if (data && data.length > 0) {
            		
            		// Ciclo sulla struttura principale degli articoli
					$.map( data, function( articolo, iart ) {
						
						var prodotti = articolo[4];
						var _rowspan = prodotti.length;
						if (_rowspan > numeroMiglioriPrezzi) _rowspan = numeroMiglioriPrezzi;
						
						// Ciclo sulla struttura annidata dei prodotti associati all'articolo
						$.map (prodotti, function (prodotto, iprod) {
							
							// Limitazione del numero di migliori prezzi visualizzati
							if (iprod < numeroMiglioriPrezzi) {
								
								var _riga = $('<tr/>');
								
								// Descrizione e colore dell'articolo
								if (iprod == 0) {
									var _colonna_id = "A_" + articolo[0] + "_descrizione";
									var _descrizione = articolo[2];
									if (articolo[3] != null) _descrizione += " (" + articolo[3] + ")";
									var _colonna = $("<td/>", {"class": "coloreA","id": _colonna_id, "rowspan": _rowspan, "text": _descrizione});
									var _img = ($("<img/>", {"class": "img_variazione_articolo", title: "Variazione", alt: "Variazione", src: "img/attenzione.gif"}));
									_colonna.append(_img);
									_colonna.find('img').hide();
									_riga.append(_colonna);
								}
								
								// Carrello di paragone
								// In questa fase la cella viene preparata vuota.
								// Sara' popolata successivamente dalla funzione _popolaProdottiImpresa
								if (iprod == 0) {
									var _colonna_id = "A_" + articolo[0] + "_prodotto_paragone";
									var _colonna = $("<td/>", {id: _colonna_id, rowspan: _rowspan, "class": "coloreA importo"});
									var _input_id = "A_" + articolo[0] + "_id_prodotto_paragone";
									_colonna.append($("<input/>", {id: _input_id, type: "hidden"}));
									var _input_id = "A_" + articolo[0] + "_importo_prodotto_paragone_input";
									_colonna.append($("<input/>", {id: _input_id, type: "hidden"}));
									var _span_id = "A_" + articolo[0] + "_importo_prodotto_paragone";
									_colonna.append($("<span/>", {id: _span_id}));
									var _img = ($("<img/>", {"class": "img_aggiungi_prodotto_paragone", title: "Aggiungi il prodotto al carrello", alt: "Aggiungi il prodotto al carrello", src: "img/carrello_aggiungi.png"}));
									_colonna.append(_img);
									_colonna.find('img').hide();
									_riga.append(_colonna);
								}
								
								// Prodotto offerto, prezzo
								var _colonna_id = "A_" + articolo[0] + "_P_" + prodotto[0] + "_prodotto_offerto";
								var _colonna = $("<td/>", {id: _colonna_id, "class": "coloreB importo"});
								var _input_id = "A_" + articolo[0] + "_P_" + prodotto[0] + "_id_articolo";
								_colonna.append($("<input/>", {id: _input_id, type: "hidden", value: articolo[0]}));
								var _input_id = "A_" + articolo[0] + "_P_" + prodotto[0] + "_id_prodotto_offerto";
								_colonna.append($("<input/>", {id: _input_id, type: "hidden", value: prodotto[0]}));
								var _span_id = "A_" + articolo[0] + "_P_" + prodotto[0] + "_importo_prodotto_offerto";
								_colonna.append($("<span/>", {id: _span_id, text: _formattaImporto(prodotto[5])}));
								var _img = $("<img/>", {"class": "img_aggiungi_prodotto_offerto", title: "Aggiungi il prodotto al carrello", alt: "Aggiungi il prodotto al carrello", src: "img/carrello_aggiungi.png"});
								if ($("#MODOAPERTURA").val() == "VISUALIZZA"  || articolo[12] != null) _img.hide();
								_colonna.append(_img);
								_riga.append(_colonna);
								
								// Prodotto offerto, impresa offerente
								var _colonna_id = "A_" + articolo[0] + "_P_" + prodotto[0] + "_impresa_offerente";
								var _colonna = $("<td/>", {id: _colonna_id, "class": "coloreB impresa", text: prodotto[4]});
								_riga.append(_colonna);
								
								// Cella per la visualizzazione dell'icona di accesso alla lista di
								// comparazione di tutti i prodotti offerti
								if (iprod == 0) {
									var _colonna_id = "A_" + articolo[0] + "_altriprodotti";
									var _colonna = $("<td/>", {id: _colonna_id, "class": "coloreB altriprodotti", rowspan: _rowspan});
									var _img_title = "Apri la lista con il dettaglio di tutti i prodotti";
									var _img = $("<img/>", {"class": "img_altriprodotti", title: _img_title, alt: _img_title, src: "img/altriprodotti.png"});
									_colonna.append(_img);
									_riga.append(_colonna);
								}
								
								// Bozza ordine, prezzo del prodotto nel carrello
								if (iprod == 0) {
									var _colonna_id = "A_" + articolo[0] + "_prodotto_acquistato";
									var _colonna = $("<td/>", {id: _colonna_id, "class": "coloreC importo", rowspan: _rowspan});
									var _input_id = "A_" + articolo[0] + "_id_prodotto_acquistato";
									_colonna.append($("<input/>", {id: _input_id, name: _input_id, type: "hidden", value: articolo[5]}));
									var _input_id = "A_" + articolo[0] + "_ngara_prodotto_ordinato";
									_colonna.append($("<input/>", {id: _input_id, name: _input_id, type: "hidden", value: articolo[12]}));
									var _input_id = "A_" + articolo[0] + "_importo_prodotto_acquistato_input";
									_colonna.append($("<input/>", {id: _input_id, type: "hidden", value: articolo[8]}));
									var _span_id = "A_" + articolo[0] + "_importo_prodotto_acquistato";
									_colonna.append($("<span/>", {id: _span_id, text: _formattaImporto(articolo[8])}));
									var _img = $("<img/>", {"class": "img_elimina_prodotto_acquistato", title:"Elimina il prodotto dal carrello", alt: "Elimina il prodotto dal carrello", src: "img/carrello_elimina.png"});
									_colonna.append(_img);
									if ($("#MODOAPERTURA").val() == "VISUALIZZA" || articolo[5] == null || articolo[12] != null) _colonna.find('img').hide();
									_riga.append(_colonna);
								}
								
								// Aliquota IVA
								if (iprod == 0) {
									var _colonna_id = "A_" + articolo[0] + "_aliquota_iva";
									var aliquota_iva = "";
									if (articolo[11] != null) aliquota_iva = articolo[11];
									var _colonna = $("<td/>", {id: _colonna_id, rowspan: _rowspan, "class": "coloreC iva", text: aliquota_iva});
									_riga.append(_colonna);
								}
								
								// Bozza ordine, impresa fornitrice
								if (iprod == 0) {
									var _colonna_id = "A_" + articolo[0] + "_impresa_fornitrice";
									var impresa_fornitrice = "";
									if (articolo[7] != null) impresa_fornitrice = articolo[7];
									var _colonna = $("<td/>", {id: _colonna_id, rowspan: _rowspan, "class": "coloreC impresa", text: impresa_fornitrice});
									_riga.append(_colonna);
								}
								
							
								// Differenza tra l'importo selezionato nella bozza ordine 
								// ed il miglior prezzo disponibile								
								if (iprod == 0) {
									var _colonna_id = "A_" + articolo[0] + "_differenza";
									var _colonna = $("<td/>", {id: _colonna_id, "class": "coloreD importo", rowspan: _rowspan});
									var _span_id = "A_" + articolo[0] + "_importo_differenza";
									_colonna.append($("<span/>", {id: _span_id, text: _formattaImporto(articolo[10])}));
									var _input_id = "A_" + articolo[0] + "_importo_differenza_input";
									_colonna.append($("<input/>", {id: _input_id, type: "hidden", value: articolo[10]}));
									var _input_id = "A_" + articolo[0] + "_importo_miglior_prezzo";
									_colonna.append($("<input/>", {id: _input_id, type: "hidden", value: articolo[9]}));
									_riga.append(_colonna);
								}
								
								$('#carrello > tbody:last').append(_riga);
							}
						});
					});
					
					var _riga = $('<tr/>',{"class": "riepilogo"});
					_riga.append($("<td/>", {"class": "coloreAI totale", text: "Importi totali" }));
					_riga.append($("<td/>", {id: "importo_totale_paragone", "class": "coloreAI importo"}));
					_riga.append($("<td/>", {"class": "coloreBI" ,colspan: 3 }));
					_riga.append($("<td/>", {id: "importo_totale_carrello", "class": "coloreCI importo"}));
					_riga.append($("<td/>", {"class": "coloreCI"}));
					_riga.append($("<td/>", {"class": "coloreCI"}));
					_riga.append($("<td/>", {id: "importo_totale_differenza", "class": "coloreDI importo"}));
					$('#carrello > tbody:last').append(_riga);					
            	}
            	
            	// Importi totali
            	_calcolaTotaleCarrello();
            	_calcolaTotaleDifferenza();
            	// Numero di prodotto inseriti nel carrello
            	_numeroProdottiAcquistati();
            	// Popolamento del carrello di confronto per la prima impresa
            	var codimp = $('#imprese :selected').val();
            	_popolaProdottiImpresa(meric_id, codimp);
            	
            },
            error: function(e){
                alert("Errore durante la lettura del carrello.");
            },
            complete: function() {
            	_nowait();
            }
        });
    }


    
    /*
     * Popolamento della lista delle imprese per il carrello di confronto 
     */
    function _popolaImprese(meric_id) {
    	$.ajax({
            type: "GET",
            dataType: "json",
            async: false,
            beforeSend: function(x) {
			if(x && x.overrideMimeType) {
    			x.overrideMimeType("application/json;charset=UTF-8");
		       }
			},
            url: "pg/GetRicercaMercatoListaImprese.do",
            data: "meric_id=" + meric_id,
            success: function(data) {
            	if (data && data.length > 0) {
            		var selectImprese = $('<select/>',{id: "imprese"});
					$.map( data, function( item ) {
						if (item[0] != null && item[1] != null) {
							var _nomest = item[1].substr(0,100);
							selectImprese.append($("<option/>", {value: item[0], text: _nomest + " [" + item[2] + "]" }));
						}
					});
					$("#impreseContainer").append(selectImprese);
            	}
            },
            error: function(e){
                alert("Errore durante la lettura della lista delle imprese");
            }
        });
    }
   
    
    /*
     * Popola la lista dei prodotti dell'impresa selezionata 
     * per il carrello di confronto.
     */
    function _popolaProdottiImpresa(meric_id, codimp) {
    	
    	$('[id*="_prodotto_paragone"]').find('span').text("");
    	$('[id*="_prodotto_paragone"]').find('input').val("");
    	$('[id*="_prodotto_paragone"]').find('img').hide();
    	
        $.ajax({
            type: "GET",
            dataType: "json",
            async: false,
            beforeSend: function(x) {
			if(x && x.overrideMimeType) {
    			x.overrideMimeType("application/json;charset=UTF-8");
		       }
			},
            url: "pg/GetListaProdottiImpresa.do",
            data: "meric_id=" + meric_id + "&codimp=" + codimp,
            success: function(data) {
            	if (data && data.length > 0) {
					$.map( data, function( item ) {
						$("#A_" + item[0] + "_id_prodotto_paragone").val(item[2]);
						$("#A_" + item[0] + "_importo_prodotto_paragone_input").val(item[3]);
						$("#A_" + item[0] + "_importo_prodotto_paragone").text(_formattaImporto(item[3],18.2));
						if ($("#MODOAPERTURA").val() != "VISUALIZZA" && $("#A_" + item[0] + "_ngara_prodotto_ordinato").val() == "") {
							$("#A_" + item[0] + "_prodotto_paragone").find('img').show();
						}
					});
            	}
            },
            error: function(e){
                alert("Errore durante la lettura della lista dei prodotti offerti da una impresa");
            }
        });
        
        _calcolaTotaleParagone();
    }

    
    /*
     * Calcolo importo totale del carrello di paragone
     */
    function _calcolaTotaleParagone() {
    	var importi = $('input[id*="_importo_prodotto_paragone_input"]');
    	var importo_totale = 0;
    	importi.each(function(i) {
    		if ($(this).val() != "") {
    			importo_totale += parseFloat($(this).val());
    		}
		});
    	$("#importo_totale_paragone").text(_formattaImporto(importo_totale,18.2));
    } 
    
    
    /*
     * Calcolo importo totale del carrello
     */
    function _calcolaTotaleCarrello() {
    	var importi = $('input[id*="_importo_prodotto_acquistato_input"]');
    	var importo_totale = 0;
    	importi.each(function(i) {
    		if ($(this).val() != "") {
    			importo_totale += parseFloat($(this).val());
    		}
		});
    	$("#importo_totale_carrello").text(_formattaImporto(importo_totale,18.2));
    }
    
    
    /*
     * Calcolo importo totale differenza
     */
    function _calcolaTotaleDifferenza() {
    	var importi = $('input[id*="_importo_differenza_input"]');
    	var importo_totale = 0;
    	importi.each(function(i) {
    		if ($(this).val() != "") {
    			importo_totale += parseFloat($(this).val());
    		}
		});
    	$("#importo_totale_differenza").text(_formattaImporto(importo_totale,18.2));
    }
     
    
    /*
     * Rimozione righe del carrello. 
     * Utilizzato, per esempio, quando si vuole ricaricare il carrello
     * perche' è variato il numero di migliori prezzi offerti 
     * da visualizzare.
     * 
     */
    function _removeCarrello() {
    	$('#carrello tr:not(.intestazione)').remove();
    }
    
    
    /*
     * Evento modifica dell'impresa nel carrello di confronto
     * si devono ricaricare i prezzi offerti in funzione dell'impresa selezionata. 
     */
    $('#imprese').change(
    	function() {
    		var codimp = $(this).val();
    		_popolaProdottiImpresa($("#MERIC_ID").val(), codimp);
    		$.cookie($("#MERIC_ID").val() + "_codimp", codimp);
    	}
    );
    
    
    /*
     * Evento modifica numero massimo di migliori offerte.
     * Si deve ricaricare il carrello.
     */
     $('#numeroMiglioriPrezzi').change(
       	function() {
       		_wait();
   			_removeCarrello();
       		var numeroMiglioriPrezzi = $(this).val();
       		$.cookie('numeroMiglioriPrezzi', numeroMiglioriPrezzi);
       		_popolaCarrello($("#MERIC_ID").val(), numeroMiglioriPrezzi);   
       	}
     );

     
    /*
     * Evento click per l'apertura della lista degli altri prezzi/prodotti.
     * L'evento e' associato mediante "delegate" perche' l'oggetto
     * chiamante e' creato dinamicamente dalla funzione _popolaCarrello
     */
    $("body").delegate( ".img_altriprodotti", "click",
       	function() {
    		var _id = $(this).parent().attr('id');
    		var mericart_id = _id.substring(2,_id.indexOf("_altriprodotti"));
   	    	$(".ui-dialog-titlebar").hide();
   			$("#mascheraListaProdottiArticolo").dialog( "option", { mericart_id: mericart_id } );
   			_popolaListaProdottiArticolo(mericart_id);
   			$("#mascheraListaProdottiArticolo").dialog("open");
			$("#mascheraListaProdottiArticolo").height(550);
       	}
    );

    
    /*
     * Evento click per l'aggiunta, al carrello, del prodotto dal carrello di paragone.
     */
    $("body").delegate( ".img_aggiungi_prodotto_paragone", "click",
       	function() {
    		var _id = $(this).parent().attr('id');
    		var mericart_id = _id.substring(2,_id.indexOf("_prodotto_paragone"));
    		var mericprod_id = $("#A_" + mericart_id + "_id_prodotto_paragone").val();
    		_popolaProdottoAcquistato(mericart_id,mericprod_id);
       	}
    );
    
    
    /*
     * Evento click per l'aggiunta, al carrello, del prodotto offerto.
     */
    $("body").delegate( ".img_aggiungi_prodotto_offerto", "click",
       	function() {
    		var _id = $(this).parent().attr('id');
    		var _id_part = _id.substring(0,_id.indexOf("_prodotto_offerto"));
    		var mericart_id = $("#" + _id_part + "_id_articolo").val();
    		var mericprod_id = $("#" + _id_part + "_id_prodotto_offerto").val();
    		_popolaProdottoAcquistato(mericart_id,mericprod_id);
       	}
    );

    
    /*
     * Evento click per l'aggiunta, al carrello, del prodotto offerto
     * dalla lista complessiva dei prodotti.
     */
    $("body").delegate( ".img_aggiungi_prodotto_da_lista", "click",
       	function() {
    		var options = $("#mascheraListaProdottiArticolo").dialog("option");
    		var mericart_id = options.mericart_id;
    		var _id = $(this).parent().parent().attr('id');
    		var mericprod_id = _id.substring(2);
    		_popolaProdottoAcquistato(mericart_id,mericprod_id);
    		$("#mascheraListaProdottiArticolo").dialog( "close" );
       	}
    );
    

    /*
     * Evento click per l'aggiunta, al carello, del prodotto offerto
     * dalla lista complessiva dei prodotti
     */
    
    $("body").delegate( ".img_aggiungi_prodotto_da_scheda", "click",
       	function() {
    		var options = $("#mascheraListaProdottiArticolo").dialog("option");
    		var mericart_id = options.mericart_id;
    		var mericprod_id = $(this).parent().find('input').val();
    		_popolaProdottoAcquistato(mericart_id,mericprod_id);
    		$("#mascheraListaProdottiArticolo").dialog( "close" );
       	}
    );
    
    
    /*
     * Evento click per l'eliminazione, dal carrello, del prodotto acquistato. 
     */
    $("body").delegate( ".img_elimina_prodotto_acquistato", "click",
       	function() {
			var _id = $(this).parent().attr('id');
			var mericart_id = _id.substring(2,_id.indexOf("_prodotto_acquistato"));
			_eliminaProdottoAcquistato(mericart_id);
    		_calcolaTotaleCarrello();
    		_calcolaTotaleDifferenza();
    		$(this).hide();
       	}
    );
    
    /*
     * Eliminazione del prodotto acquistaot dal carrello in bozza.
     */
    function _eliminaProdottoAcquistato(mericart_id) {
		$("#A_" + mericart_id + "_id_prodotto_acquistato").val("").change();
		$("#A_" + mericart_id + "_importo_prodotto_acquistato_input").val("");
		$("#A_" + mericart_id + "_importo_prodotto_acquistato").text("");
		$("#A_" + mericart_id + "_impresa_fornitrice").text("");
		$("#A_" + mericart_id + "_importo_differenza_input").val("");
		$("#A_" + mericart_id + "_importo_differenza").text("");
		$("#A_" + mericart_id + "_aliquota_iva").text("");
    }    
    
    
    /*
     * Evento change sull'identificativo del prodotto acquistato.
     * Utilizzato per monitorare la variazione del numero di prodotti
     * acquistati (inseriti nella bozza ordine).
     */ 
    $("body").delegate( "[id*=_id_prodotto_acquistato]", "change",
    	function() {	
    		_numeroProdottiAcquistati();
	    }
    );
   
    
    /*
     * Conteggio del numero totale di prodotti acquistati.
     */
    function _numeroProdottiAcquistati() {
    	var ids = $('input[id*="_id_prodotto_acquistato"]');
		var numero_prodotti_acquistati = 0;
		ids.each(function(i) {
    		if ($(this).val() != "") {
    			numero_prodotti_acquistati++;
    		}
		});
	    $("#numero_prodotti_acquistati").text(numero_prodotti_acquistati);
    }
    
    
    /*
     * Aggiunta del prodotto selezionato al carrello in bozza.
     */
    function _popolaProdottoAcquistato(mericart_id, mericprod_id) {
    	$.ajax({
            type: "GET",
            dataType: "json",
            async: false,
            beforeSend: function(x) {
			if(x && x.overrideMimeType) {
    			x.overrideMimeType("application/json;charset=UTF-8");
		       }
			},
            url: "pg/GetRicercaMercatoProdotto.do",
            data: "mericprod_id=" + mericprod_id,
            success: function(data) {
            	if (data && data.length > 0) {
					$.map( data, function( item ) {
						
						var _popolaProdotto = true;
						
						// Verifica se esiste gia' un prodotto acquistato
						var _id_prodotto_gia_acquistato = $("#A_" + mericart_id + "_id_prodotto_acquistato").val();
						if (_id_prodotto_gia_acquistato != "" && _id_prodotto_gia_acquistato != mericprod_id) {
							var r = confirm("Esiste gia' un prodotto selezionato nella bozza ordine. Sostituire il prodotto ?");
							if (r == false) {
								_popolaProdotto = false;
							}
						}
						
						if (_popolaProdotto == true) {
							// Eliminazione preliminare del prodotto acquistato
							_eliminaProdottoAcquistato(mericart_id);
							
							// Aggiunta del nuovo prodotto acquistato
							$("#A_" + mericart_id + "_id_prodotto_acquistato").val(mericprod_id).change();
							var importo_prezzo_migliore = $("#A_" + mericart_id + "_importo_miglior_prezzo").val();
							$("#A_" + mericart_id + "_importo_prodotto_acquistato_input").val(item[2].value);
							$("#A_" + mericart_id + "_importo_prodotto_acquistato").text(_formattaImporto(item[2].value,18.2));
				    		$("#A_" + mericart_id + "_impresa_fornitrice").text(item[1].value);
				    		$("#A_" + mericart_id + "_importo_differenza_input").val(item[2].value - importo_prezzo_migliore);
				    		$("#A_" + mericart_id + "_importo_differenza").text(_formattaImporto(item[2].value - importo_prezzo_migliore,18.2));
				    		$("#A_" + mericart_id + "_prodotto_acquistato").find('img').show();
				    		$("#A_" + mericart_id + "_aliquota_iva").text(item[4].value);
						}

					});
            	}
            },
            error: function(e){
                alert("Errore durante la lettura della lista dei prodotti offerti da una impresa");
            }
        });
		_calcolaTotaleCarrello();
		_calcolaTotaleDifferenza();
    }
    

    /*
     * Definizione proprieta' della maschera a lista di tutti i prodotti per l'articolo indicato.
     */
    $( "#mascheraListaProdottiArticolo" ).dialog({
    	autoOpen: false,
    	width: 1000,
    	show: {
    		effect: "blind",
    		duration: 500
        },
        hide: {
        	effect: "blind",
        	duration: 500
        },
		position: {
			my: "left top",
			at: "left top",
			of: ".arealavoro"
		},
        modal: true,
        resizable: true,
		focusCleanup: true,
		cache: false,
        buttons: {
            "Chiudi" : function() {
            	$(this).dialog( "close" );
            }
          }
    });
    
    
    /*
     * Evento dialogclose,indipendentemente dal tipo di chiusura 
     * (Chiudi o selezione di un prodotto da aggiungere al carrello)
     * devono essere svuotate la lista di tutti i prodotti e la tabella di confronto.
     */
    $( "#mascheraListaProdottiArticolo" ).on( "dialogclose", function( event, ui ) {
    	_removeSchedaArticolo();
    	_removeListaProdotti();
    	_removeConfrontaProdotti();
    });
   
    
    /*
     * Popola la lista di tutti i prodotti per l'articolo indicato.
     */
    function _popolaListaProdottiArticolo(mericart_id) {
    	
    	var meartcat_przunitper;
    	var meartcat_unimistempocons;
    	var mearcat_tipo;
    	
    	/*
    	 * Dati generali dell'articolo.
    	 */
        $.ajax({
            type: "GET",
            dataType: "json",
            async: false,
            beforeSend: function(x) {
			if(x && x.overrideMimeType) {
    			x.overrideMimeType("application/json;charset=UTF-8");
		       }
			},
            url: "pg/GetRicercaMercatoArticolo.do",
            data: "mericart_id=" + mericart_id,
            success: function(data) {
            	if (data && data.length > 0) {
					$.map( data, function( item ) {
					    // 5 - Modalita' di acquisto (valore numerico)
						meartcat_przunitper = (item[5].value != null) ? item[5].value : "";
						// 14 - Unita' di misura del tempo di consegna (descrizione)
						meartcat_unimistempocons = (item[14].value != null) ? item[14].value : "";
						// 12 - Tipo (descrizione)
						mearcat_tipo = (item[12].value != null) ? item[12].value : ""
						$("#schedaArticoloTipo").text(mearcat_tipo);
						// 2 - Descrizione
						$("#schedaArticoloDescrizione").text((item[2].value != null) ? item[2].value : "");
						// 3 - Descrizione tecnica
						$("#schedaArticoloDescrizioneEstesa").text((item[3].value != null) ? item[3].value : "");
						// 4 - Colore
						$("#schedaArticoloColore").text((item[4].value != null) ? item[4].value : "");
						// 13 - Modalita' di acquisto (descrizione)
						$("#schedaArticoloModalitaAcquisto").text((item[13].value != null) ? item[13].value : "");
						// 6 - Quantita' in carrello
						if(meartcat_przunitper == 4){
							var descDett1 =  item[8].value;
							var descDett2 =  item[9].value;
							var quadet1;
							var quadet2;
							if (item[10].value!=null && item[10].value!="")
								quadet1 = _formattaQuantita(item[10].value);
							if (item[11].value!=null && item[11].value!="")
								quadet2 = _formattaQuantita(item[11].value);
							var dettaglio = " (" + descDett1 + " " + quadet1 + ", " + descDett2 + " " + quadet2 + ")";
							$("#schedaArticoloQuantita").text((item[6].value != null) ? _formattaQuantita(item[6].value) +  dettaglio: "");
						}else{
							$("#schedaArticoloQuantita").text((item[6].value != null) ? _formattaQuantita(item[6].value) : "");
						}
						
						if(mearcat_tipo!="Bene"){
							$("#searchMarca").hide();
							$("#etichettaMarca").hide();
						}else{
							$("#searchMarca").show();
							$("#etichettaMarca").show();
						}
						
					});
            	}
            },
            error: function(e){
                alert("Errore durante la lettura dell'articolo");
            }
        });
        
    	// Tabella ed intestazione lista dei prodotti.
        var _table = $('<table/>', {"class": "carrello", id: "listaProdottiArticolo", "width": "950px"});
        var _thead = $('<thead/>');
        
        // Titolo della tabella
        var _tr = $("<tr/>", {"class": "titolo"});
        var _img = ($("<img/>", {"class": "img_titolo", "src": "img/Content-43.png" }));
        var _th = $("<th/>", {"colspan": "15", "text": "Lista dei prodotti"});
        _th.prepend(_img);
        
        // Titolo a messaggio variabile per l'accesso alla sezione con il dettaglio
        // del prodotto o del confronto prodotti
        var _span = $("<span/>", {"id": "confrontaprodotti", "class": "floatright"});
        var _spantext = $("<span/>", {"id": "confrontaprodottitext", "text": "Confronta i prodotti selezionati"});
        var _img = ($("<img/>", {"class": "img_titolo", "src": "img/squared-big-2-01.png" }));
        _span.append(_spantext);
        _span.append(_img);
        _span.hide();
        _th.append(_span); 
        _tr.append(_th);
        
        _thead.append(_tr);
        
        // Intestazioni di colonna
        var _tr = $('<tr/>', {"class": "intestazione"});
        _tr.append($("<th/>", {text: "Codice prodotto"}));
        _tr.append($("<th/>", {"class": "nomecommerciale", text: "Nome commerciale"}));
        _tr.append($("<th/>", {"class": "impresa", text: "Operatore"}));
        _tr.append($("<th/>", {text: "U.M."}));
        _tr.append($("<th/>", {"class": "importo", text: "Prezzo"}));
        _tr.append($("<th/>", {text: "N. componenti U.M. rif. acquisto"}));
        _tr.append($("<th/>", {text: "U.M. rif. acquisto"}));
        _tr.append($("<th/>", {"class": "importo", text: "Prezzo prodotto"}));
        _tr.append($("<th/>", {text: "Lotto minimo"}));
        _tr.append($("<th/>", {text: "Quantita' offerta"}));
        _tr.append($("<th/>", {"class": "importo", text: "Prezzo tot. offerto (esclusa IVA)"}));
        _tr.append($("<th/>", {"class": "iva", text: "% IVA"}));
        
        var _span = $('<span/>', {id: "meartcat_unimistempocons"});
        var _th = $('<th/>', {text: "Tempo consegna "});
        _th.append(_span);
        _tr.append(_th);
        
        _tr.append($("<th/>", {"class": "importo", text: "Prezzo"}));
        _tr.append($("<th/>", {"class": "marca", text: "Marca"}));
        _tr.append($("<th/>", {"class": "dettaglioprodotto", text: ""}));
        _thead.append(_tr);
        
        var _tbody = $('<tbody/>');
        
        _table.append(_thead);
        _table.append(_tbody);
        
        $('#listaProdottiArticoloContainer').append(_table);
        
        // Unita' di misura del tempo di consegna
        if (meartcat_unimistempocons != null) {
        	$("#meartcat_unimistempocons").text("(" + meartcat_unimistempocons + ")");
        }
        
        // Popolamento dati dei prodotti
        $.ajax({
            type: "GET",
            dataType: "json",
            async: false,
            beforeSend: function(x) {
			if(x && x.overrideMimeType) {
    			x.overrideMimeType("application/json;charset=UTF-8");
		       }
			},
            url: "pg/GetListaProdottiArticolo.do",
            data: "idricart=" + mericart_id,
            success: function(data) {
            	if (data && data.length > 0) {
					$.map( data, function( item ) {
						
						// Controllo il prodotto gia' inserito nel carrello in bozza
						var _trclass = "";
						var _id_prodotto_acquistato = $("#A_" + mericart_id + "_id_prodotto_acquistato").val();
						if (_id_prodotto_acquistato != "" && _id_prodotto_acquistato == item[0].value) {
							_trclass = "acquistato";
						}
						
						// 0 - Identificativo prodotto nella ricerca di mercato
						var _riga_id = 'P_' + item[0].value;
						var _riga = $('<tr/>', {id: _riga_id, "class": _trclass});
						// 2 - Codice del prodotto
						_riga.append($("<td/>", {text: (item[2].value != null) ? item[2].value : ""}));
						// 3 - Nome commerciale
						_riga.append($("<td/>", {"class": "nomecommerciale", text: (item[3].value != null) ? item[3].value : ""}));
						// 5 - Nome operatore
						_riga.append($("<td/>", {"class": "impresa", text: (item[5].value != null) ? item[5].value : ""}));
						// 17 - Unita' di misura prezzo (descrizione)
						_riga.append($("<td/>", {"class": "centrato", text: (item[17].value != null) ? item[17].value : ""}));
						// 7 - Prezzo unitario riferito a unita' di misura prezzo
						$("#meiscrizprod_przunit").show();
						_riga.append($("<td/>", {"class": "importo", text: _formattaImporto(item[7].value)}));
					    // 8 - Quantita' di unita' di misura prezzo
						$("#meiscrizprod_qunimisprz").show();
						_riga.append($("<td/>", {"class": "centrato", text: (item[8].value != null) ? _formattaQuantita(item[8].value) : ""}));
						// 18 - Unita' di misura acquisto (descrizione)
						$("#meiscrizprod_unimisacq").show();
						_riga.append($("<td/>", {"class": "centrato", text: (item[18].value != null) ? item[18].value : ""}));
					    // 10 - Prezzo unitario del prodotto riferito a unita' di misura acquisto
						_riga.append($("<td/>", {"class": "importo", text: _formattaImporto(item[10].value)}));
						// 11 - Lotto minimo per unita' di misura
						$("#meiscrizprod_qunimisacq").show();
						_riga.append($("<td/>", {"class": "centrato", text: (item[11].value != null) ? _formattaQuantita(item[11].value) : ""}));							
					    // 12 - Quantita'
						_riga.append($("<td/>", {"class": "centrato", text: (item[12].value != null) ? _formattaQuantita(item[12].value) : ""}));
					    // 13 - Prezzo totale offerto
						var _colonna = $("<td/>", {"class": "importo", text: _formattaImporto(item[13].value)});
						var _img_title = "Aggiungi il prodotto al carrello";
						var _img = $("<img/>", {"class": "img_aggiungi_prodotto_da_lista", title: _img_title, alt: _img_title, src: "img/carrello_aggiungi.png"});
						if ($("#MODOAPERTURA").val() == "VISUALIZZA" || $("#A_" + mericart_id + "_ngara_prodotto_ordinato").val() != "") {
							_img.hide();
						}
						_colonna.append(_img);
						_riga.append(_colonna);						
						// 19 - Aliquota IVA (descrizione)
						_riga.append($("<td/>", {"class": "iva", text: (item[19].value != null) ? item[19].value : ""}));
					    // 15 - Tempo consegna
						_riga.append($("<td/>", {"class": "centrato", text: (item[15].value != null) ? item[15].value : ""}));
						
						// Colonna aggiuntiva per memorizzare il prezzo totale offerto privo di
						// formattazione (serve per la ricerca)
						var _colonna = $("<td/>", {"class": "importo", text: (item[13].value != null) ? item[13].value : ""});
						_riga.append(_colonna);
						
						// 16 - Marca
						_riga.append($("<td/>", {"class": "marca", text: (item[16].value != null) ? item[16].value : ""}));
						
						// Checkbox di selezione per il confronto
						var _colonna = $("<td/>", {"class": "dettaglioprodotto"});
						var _input_id = "dettaglioProdotto_" + item[0].value;
						_colonna.append($("<input/>", {id: _input_id, type: "checkbox" }));
						_riga.append(_colonna);

						$('#listaProdottiArticolo > tbody:last').append(_riga);
					});
            	}
            },
            error: function(e){
                alert("Errore durante la lettura della lista dei prodotti");
            },
            complete: function() {
            	_pluginListaProdottiArticolo(meartcat_przunitper);
            }
        });
    }
    
    
    /*
     * Gestione plugin dataTable su listaProdottiArticolo
     */
    function _pluginListaProdottiArticolo(meartcat_przunitper) {
    	// Aggiunta gestore dataTable alla lista dei prodotti
   		tableProdottiArticolo = $('#listaProdottiArticolo').DataTable( {
   			 "paging": false,
   		     "ordering": false,
   		     "info": false,
   		     "searching" : true,
   		     "language": {
   		    	"zeroRecords": "Nessun prodotto trovato"
   		     }
   		 });
   		
   		// Gestione colonne nascoste
   		tableProdottiArticolo.column(13).visible(false);
   		if (meartcat_przunitper != 3) {
   			tableProdottiArticolo.column(4).visible(false);
   			tableProdottiArticolo.column(5).visible(false);
   			tableProdottiArticolo.column(6).visible(false);
		} 
   		if (meartcat_przunitper != 3 && meartcat_przunitper != 2) {
   			tableProdottiArticolo.column(8).visible(false);
		} 
   		
   		//Si nasconde la colonna Marca
   		tableProdottiArticolo.column(14).visible(false);
   		
   		// Gestione ricerche
   		// Nascondo il campo di ricerca di "default"
   		$("#listaProdottiArticolo_filter").hide();
   		
   		// Popolamente drop down di ricerca delle imprese
   		$("#searchOperatore").append( '<option value=""></option>');
   			tableProdottiArticolo.column(2).data().unique().sort().each( function ( d, j ) {
   				var _nomest =d;
   				
   				if(_nomest.length>100){
   					_nomest = _nomest.substr(0,100);
   					_nomest+="..."
   				}
   				
   				$("#searchOperatore").append( '<option value="'+d+'">'+_nomest+'</option>');
        });

   		// Evento variazione ricerca sull'operatore
        $('#searchOperatore').on( 'change', function () {
        	tableProdottiArticolo.column(2).search($(this).val()).draw();
        	_gestioneConfrontaProdotti();
        });
   		
        // Evento variazione ricerca sul nome commerciale del prodotto
		$('#searchNomeCommerciale').on( 'keyup', function () {
			tableProdottiArticolo.column(1).search(this.value).draw();
			_gestioneConfrontaProdotti();
		});

		// Evento variazione ricerca sulla marca
		$('#searchMarca').on( 'keyup', function () {
			tableProdottiArticolo.column(14).search(this.value).draw();
			_gestioneConfrontaProdotti();
		});
		
		// Gestore personalizzato per la ricerca per quantita' e prezzo totale offerto
		$.fn.dataTable.ext.search.push(
			    function( settings, data, dataIndex ) {
			    	var b_quantita = false;
			    	var b_prezzo = false;
			    	
			    	// Ricerca sulla quantita' offerta
			        var minQuantitaOfferta = $('#searchQuantitaOffertaMin').val() * 1;
			        var maxQuantitaOfferta = $('#searchQuantitaOffertaMax').val() * 1;
			        var quantitaofferta = parseFloat( data[9] ) || 0; 
			 
			        if ( ( minQuantitaOfferta == '' && maxQuantitaOfferta == '' ) ||
			             ( minQuantitaOfferta == '' && quantitaofferta <= maxQuantitaOfferta ) ||
			             ( minQuantitaOfferta <= quantitaofferta && '' == maxQuantitaOfferta ) ||
			             ( minQuantitaOfferta <= quantitaofferta && quantitaofferta <= maxQuantitaOfferta ) )
			        {
			            b_quantita = true;
			        }
			        
			        // Ricerca sul prezzo offerto 
			        var minPrezzoTotaleOfferto = $('#searchPrezzoTotaleOffertoMin').val() * 1;
			        var maxPrezzoTotaleOfferto = $('#searchPrezzoTotaleOffertoMax').val() * 1;
			        var prezzototaleofferto = parseFloat( data[13] ) || 0; 
			 
			        if ( ( minPrezzoTotaleOfferto == '' && maxPrezzoTotaleOfferto == '' ) ||
			             ( minPrezzoTotaleOfferto == '' && prezzototaleofferto <= maxPrezzoTotaleOfferto ) ||
			             ( minPrezzoTotaleOfferto <= prezzototaleofferto && '' == maxPrezzoTotaleOfferto ) ||
			             ( minPrezzoTotaleOfferto <= prezzototaleofferto && prezzototaleofferto <= maxPrezzoTotaleOfferto ) )
			        {
			            b_prezzo = true;
			        }
			        
			        return b_quantita && b_prezzo;
			    }
			);
		
		// Evento variazioni ricerca sulla quantita' ed il prezzo totale offerto
	    $('#searchQuantitaOffertaMin, #searchQuantitaOffertaMax, #searchPrezzoTotaleOffertoMin, #searchPrezzoTotaleOffertoMax').keyup( function() {
	    	tableProdottiArticolo.draw();
	    	_gestioneConfrontaProdotti();
	    } );
    }
    
    
    /*
     * Rimozione della lista dei prodotti (comprensiva di parametri di ricerca)
     */
    function _removeListaProdotti() {
    	$("#searchNomeCommerciale").val("");
    	$("#searchOperatore").empty();
    	$("#searchQuantitaOffertaMin").val("");
    	$("#searchQuantitaOffertaMax").val("");
    	$("#searchPrezzoTotaleOffertoMin").val("");
    	$("#searchPrezzoTotaleOffertoMax").val("");
    	tableProdottiArticolo.destroy(true);
    }
    
    
    /*
     * Evento click per l'apertura del dettaglio prodotto per il confronto delle caratteristiche.
     */
    $("body" ).delegate( "[id^='dettaglioProdotto_']", "click", function() {
    	_gestioneConfrontaProdotti();
    });
    
    
    /*
     * Evento click per l'apertura del confronto prodotti
     */
    $("body" ).delegate( "#confrontaprodotti", "click", function() {
    	_confrontaProdotti();
    });

    
    /*
     * Evento click per ritornare alla lista dei prodotti
     */
    $("body" ).delegate( "#ritornalistaprodotti", "click", function() {
    	_removeConfrontaProdotti();
    });
    
    
    /*
     * Rimozione dei prodotti confrontati.
     */
    function _removeConfrontaProdotti() {
    	$("#schedaArticolo").show();
    	$("#pannelloRicercaListaProdotti").show();
    	$("#listaProdottiArticoloContainer").show();
    	$('#confrontoProdotti').remove();
    }
    
    
    /*
     * Rimozione della scheda articolo
     */
    function _removeSchedaArticolo() {
		$("#schedaArticoloTipo").text("");
		$("#schedaArticoloDescrizione").text("");
		$("#schedaArticoloDescrizioneEstesa").text("");
		$("#schedaArticoloColore").text("");
		$("#schedaArticoloModalitaAcquisto").text("");
		$("#schedaArticoloQuantita").text("");
    }
    

    /*
     * Apertura e chiusura dei dettagli della scheda articolo
     */
	$('#schedaArticoloTitolo').click(function() {
		$('#schedaArticolo tr:not(.intestazione)').toggle(100);
    }); 
    

	/*
	 * Apertura e chiusura dei dettagli del pannello di ricerca della lista dei prodotti
	 */
	$('#pannelloRicercaListaProdottiTitolo').click(function() {
		$('#pannelloRicercaListaProdotti tr:not(.intestazione)').toggle(100);
    }); 
    
    
    /*
     * Gestione delle checkbox di selezione per il confronto delle
     * caratteristiche dei prodotti.
     */
    function _gestioneConfrontaProdotti() {
    	var dettaglioProdotto = $('[id^="dettaglioProdotto_"]:checked');
    	
    	if (dettaglioProdotto.length > 0) {
    		$("#confrontaprodotti").show();
    		if (dettaglioProdotto.length > 1) {
    			$("#confrontaprodottitext").text("Confronta i prodotti selezionati");	
    		} else {
    			$("#confrontaprodottitext").text("Visualizza il dettaglio del prodotto selezionato");
    		}
    	} else {
    		$("#confrontaprodotti").hide();
    	}
    	
    	if (dettaglioProdotto.length >= numeroMassimoConfrontoPrezzi) {
    		$('[id^="dettaglioProdotto_"]:not(:checked)').attr("disabled",true);
    	} else {
    		$('[id^="dettaglioProdotto_"]').attr("disabled",false);
    	}
    }
    
  
    /*
     * Caricamento del prospetto di confronto dei prodotti.
     */
    function _confrontaProdotti() {
    	
    	_removeConfrontaProdotti();
    	$("#schedaArticolo").hide();
    	$("#pannelloRicercaListaProdotti").hide();
    	$("#listaProdottiArticoloContainer").hide();
    	
    	var dettaglioProdotto = $('[id^="dettaglioProdotto_"]:checked');
    	if (dettaglioProdotto.length > 0) {
    		 
    		var meartcat_tipo;
    		var meartcat_obblgar;
    		var meartcat_przunitper;
    		
    		// Creo la tabella
    		var _confrontoProdotti = $("<table/>",{"id": "confrontoProdotti", "class": "scheda", "width": "950px"});
    		// Intestazione
    		var _colspan = dettaglioProdotto.length + 1;
    		var _tdwidth = 710 / dettaglioProdotto.length;
    		
    		var _trintestazione = $("<tr/>", {"class": "intestazione"});
    		var _img = ($("<img/>", {"class": "img_titolo", "src": "img/Content-41.png" }));
    		var _td = $("<td/>", {"colspan": _colspan, "text": "Prospetto di confronto prodotti"});
    		_td.prepend(_img);
    		
    		var _span = $("<span/>", {"id": "ritornalistaprodotti", "class": "floatright", "text": "Ritorna alla lista dei prodotti"});
            var _img = ($("<img/>", {"class": "img_titolo", "src": "img/squared-big-1-01.png" }));
            _span.prepend(_img);
            _td.append(_span); 
    		
    		_trintestazione.append(_td);
    		
    		var _troperatore = $("<tr/>");
    		_troperatore.append($("<td/>", {"class": "etichettacfr", "text": "Operatore"}));
    		
    		var _trspecifiche = $("<tr/>", {"class": "sezione"});
    		_trspecifiche.append($("<td/>", {"colspan": _colspan, "text": "Specifiche del prodotto"}));
    		
    		var _tr1 = $("<tr/>");
        	_tr1.append($("<td/>", {"class": "etichettacfr", "text": "Codice prodotto"}));

        	var _tr2 = $("<tr/>");
        	_tr2.append($("<td/>", {"class": "etichettacfr", "text": "Marca"}));
        	
        	var _tr3 = $("<tr/>");
        	_tr3.append($("<td/>", {"class": "etichettacfr", "text": "Codice prodotto del produttore"}));

        	var _tr4 = $("<tr/>");
        	_tr4.append($("<td/>", {"class": "etichettacfr", "text": "Nome commerciale"}));
        	
        	var _trimage = $("<tr/>");
        	_trimage.append($("<td/>", {"class": "etichettacfr", "text": "Immagine"}));

        	var _tr5 = $("<tr/>");
        	_tr5.append($("<td/>", {"class": "etichettacfr", "text": "Descrizione aggiuntiva"}));

        	var _tr6 = $("<tr/>");
        	_tr6.append($("<td/>", {"class": "etichettacfr", "text": "Dimensioni"}));
        	
        	var _trcertificazioni = $("<tr/>");
        	_trcertificazioni.append($("<td/>", {"class": "etichettacfr", "text": "Certificazioni richieste"}));
        	
        	var _trschedetecniche = $("<tr/>");
        	_trschedetecniche.append($("<td/>", {"class": "etichettacfr", "text": "Schede tecniche"}));

        	var _tr7 = $("<tr/>");
        	_tr7.append($("<td/>", {"class": "etichettacfr", "text": "Durata garanzia espressa in mesi"}));

        	var _trquantitaprezzi = $("<tr/>", {"class": "sezione"});
        	_trquantitaprezzi.append($("<td/>", {"colspan": _colspan, "text": "Quantita' e prezzi"}));
        	
        	var _tr8 = $("<tr/>");
        	_tr8.append($("<td/>", {"class": "etichettacfr", "text": "Modalita' di acquisto"}));

        	var _tr9 = $("<tr/>");
        	_tr9.append($("<td/>", {"class": "etichettacfr", "text": "Unita' di misura su cui e' espresso il prezzo"}));

        	var _tr10 = $("<tr/>");
        	_tr10.append($("<td/>", {"class": "etichettacfr", "text": "Prezzo"}));

        	var _tr11 = $("<tr/>");
        	_tr11.append($("<td/>", {"class": "etichettacfr", "text": "Num. unita' su cui e' espresso il prezzo componenti l'unita' di misura a cui e' riferito l'acquisto"}));

        	var _tr12 = $("<tr/>");
        	_tr12.append($("<td/>", {"class": "etichettacfr", "text": "Unita' di misura a cui e' riferito l'acquisto"}));

        	var _tr13 = $("<tr/>");
        	_tr13.append($("<td/>", {"class": "etichettacfr", "text": "Prezzo del prodotto"}));

        	var _tr14 = $("<tr/>");
        	_tr14.append($("<td/>", {"class": "etichettacfr", "text": "Aliquota IVA"}));

        	var _tr15 = $("<tr/>");
        	_tr15.append($("<td/>", {"class": "etichettacfr", "text": "Lotto minimo per unita' di misura"}));

        	var _tr16 = $("<tr/>");
        	_tr16.append($("<td/>", {"class": "etichettacfr", "text": "Lotto minimo per unita' di misura"}));

        	var _trtempiconsegna = $("<tr/>", {"class": "sezione"});
        	_trtempiconsegna.append($("<td/>", {"colspan": _colspan, "text": "Tempi di consegna"}));
        	var _tr17 = $("<tr/>");
        	_tr17.append($("<td/>", {"class": "etichettacfr", "text": "Tempo di consegna"}));

        	var _tr18 = $("<tr/>");
        	_tr18.append($("<td/>", {"class": "etichettacfr", "text": "Tempo di consegna espresso in"}));

        	var _trvalidita = $("<tr/>", {"class": "sezione"});
        	_trvalidita.append($("<td/>", {"colspan": _colspan, "text": "Validita'"}));
        	var _tr19 = $("<tr/>");
        	_tr19.append($("<td/>", {"class": "etichettacfr", "text": "Data scadenza offerta"}));
        	
        	
        	dettaglioProdotto.each(function(p) {
        		var _id = $(this).attr('id');
        		var mericprod_id = _id.substring(18);
        		
        		// Popolamento
        		$.ajax({
	                type: "GET",
	                dataType: "json",
	                async: false,
	                beforeSend: function(x) {
	    			if(x && x.overrideMimeType) {
	        			x.overrideMimeType("application/json;charset=UTF-8");
	    		       }
	    			},
	                url: "pg/GetDettaglioProdotto.do",
	                data: "mericprod_id=" + mericprod_id,
	                success: function(data) {
	                	if (data && data.length > 0) {
	    					$.map( data, function( item ) {
	    						meartcat_tipo = item[1];
	    			    		meartcat_obblgar = item[7];
	    			    		meartcat_przunitper = item[9];
	    			    		
	    			    		_troperatore.append($("<td/>", {"width": _tdwidth, text: (item[22] != null) ? item[22] : ""}));
	    			    		
	    						_tr1.append($("<td/>", {"width": _tdwidth, text: (item[0] != null) ? item[0] : ""}));
	    						_tr2.append($("<td/>", {"width": _tdwidth, text: (item[2] != null) ? item[2] : ""}));
	    						_tr3.append($("<td/>", {"width": _tdwidth, text: (item[3] != null) ? item[3] : ""}));
	    						_tr4.append($("<td/>", {"width": _tdwidth, text: (item[4] != null) ? item[4] : ""}));
	    						
	    						var _tdimage = $("<td/>", {"width": _tdwidth});
	    						if (item[29] != null) {
	    							$.map (item[29], function (immagine) {
		    							var _href = "pg/VisualizzaFileAllegato.do";
		    							_href = _href + "?idprg=" + immagine[0] + "&iddocdig=" + immagine[1] + "&dignomdoc=" + immagine[2];
		    							var _a = $("<a/>",{href: _href});
		    							var _img = $("<img/>",{"class": "img_prodotto", "title": "Scarica l'immagine originale" ,"src": "data:image/png;base64," + immagine[3]});
		    							_a.append(_img);
		    							_tdimage.append(_a);
		    						});
	    						}
	    						_trimage.append(_tdimage);

	    						_tr5.append($("<td/>", {"width": _tdwidth, text: (item[5] != null) ? item[5] : ""}));
	    						_tr6.append($("<td/>", {"width": _tdwidth, text: (item[6] != null) ? item[6] : ""}));
	    						
	    						var _tdcertificazione = $("<td/>", {"width": _tdwidth});
	    						if (item[30] != null) {
		    						$.map (item[30], function (certificazione) {
		    							var _href = "pg/VisualizzaFileAllegato.do";
		    							_href = _href + "?idprg=" + certificazione[0] + "&iddocdig=" + certificazione[1] + "&dignomdoc=" + certificazione[2];
		    							var _a = $("<a/>",{text: certificazione[2], href: _href});
		    							_tdcertificazione.append(_a);
		    							_tdcertificazione.append("&nbsp;&nbsp;");
		    						});
	    						}
	    						_trcertificazioni.append(_tdcertificazione);
	    						
	    						var _tdschedatecnica = $("<td/>", {"width": _tdwidth});
	    						if (item[31] != null) {
		    						$.map (item[31], function (schedatecnica) {
		    							var _href = "pg/VisualizzaFileAllegato.do";
		    							_href = _href + "?idprg=" + schedatecnica[0] + "&iddocdig=" + schedatecnica[1] + "&dignomdoc=" + schedatecnica[2];
		    							var _a = $("<a/>",{text: schedatecnica[2], href: _href});
		    							_tdschedatecnica.append(_a);
		    							_tdschedatecnica.append("&nbsp;&nbsp;");
		    						});
	    						}
	    						_trschedetecniche.append(_tdschedatecnica);
	    						
	    						_tr7.append($("<td/>", {"width": _tdwidth, text: (item[8] != null) ? item[8] : ""}));
	    						
	    						_tr8.append($("<td/>", {"width": _tdwidth, text: (item[23] != null) ? item[23] : ""}));
	    						_tr9.append($("<td/>", {"width": _tdwidth, text: (item[24] != null) ? item[24] : ""}));
	    						_tr10.append($("<td/>", {"width": _tdwidth, text: _formattaImporto(item[11])}));
	    						_tr11.append($("<td/>", {"width": _tdwidth, text: (item[12] != null) ? _formattaQuantita(item[12]) : ""}));
	    						_tr12.append($("<td/>", {"width": _tdwidth, text: (item[25] != null) ? item[25] : ""}));
	    						
	    						var _tdprezzo = $("<td/>", {"width": _tdwidth, text: _formattaImporto(item[14])});
	    						var _input = ($("<input/>", {type: "text", type: "hidden", value: mericprod_id}));
	    						var _img = $("<img/>", {"class": "img_aggiungi_prodotto_da_scheda", title: "Aggiungi il prodotto al carrello", alt: "Aggiungi il prodotto al carrello", src: "img/carrello_aggiungi.png"});
    							_img.hide();
    							
	    						_tdprezzo.append(_img);
	    						_tdprezzo.append(_input);
	    						_tr13.append(_tdprezzo);
	    						
	    						_tr14.append($("<td/>", {"width": _tdwidth, text: (item[26] != null) ? item[26] : ""}));
	    						_tr15.append($("<td/>", {"width": _tdwidth, text: (item[16] != null) ? _formattaQuantita(item[16]) : ""}));
	    						_tr16.append($("<td/>", {"width": _tdwidth, text: (item[17] != null) ? _formattaQuantita(item[17]) : ""}));
	    						_tr17.append($("<td/>", {"width": _tdwidth, text: (item[18] != null) ? item[18] : ""}));
	    						_tr18.append($("<td/>", {"width": _tdwidth, text: (item[27] != null) ? item[27] : ""}));
	    						_tr19.append($("<td/>", {"width": _tdwidth, text: (item[28] != null) ? item[28] : ""}));    						
	    					});
	                	}
	                }
	            });
			});	

        	_confrontoProdotti.append(_trintestazione);
        	_confrontoProdotti.append(_trspecifiche);
        	_confrontoProdotti.append(_tr1);
        	
        	if (meartcat_tipo == 1) {
	        	_confrontoProdotti.append(_tr2);
	        	_confrontoProdotti.append(_tr3);
        	}
        	
        	_confrontoProdotti.append(_tr4);
        	_confrontoProdotti.append(_troperatore);
        	_confrontoProdotti.append(_trimage);
        	_confrontoProdotti.append(_tr5);
        	
        	if (meartcat_tipo == 1) {
        		_confrontoProdotti.append(_tr6);
        	}

        	_confrontoProdotti.append(_trcertificazioni);
        	_confrontoProdotti.append(_trschedetecniche);
        	
        	if (meartcat_obblgar == 1) {
        		_confrontoProdotti.append(_tr7);
        	}
        	
        	_confrontoProdotti.append(_trquantitaprezzi);
        	_confrontoProdotti.append(_tr8);
        	_confrontoProdotti.append(_tr9);
        	
        	if (meartcat_przunitper == 3) {
        		_confrontoProdotti.append(_tr10);
        		_confrontoProdotti.append(_tr11);
        		_confrontoProdotti.append(_tr12);
        	}
        	
        	_confrontoProdotti.append(_tr13);
        	_confrontoProdotti.append(_tr14);
        	
        	if (meartcat_przunitper == 2 || meartcat_przunitper == 3) {
        	   	_confrontoProdotti.append(_tr15);
    		}
        	
        	if (meartcat_przunitper == 1) {
        		_confrontoProdotti.append(_tr16);
        	}
        	
        	_confrontoProdotti.append(_trtempiconsegna);
        	_confrontoProdotti.append(_tr17);
        	_confrontoProdotti.append(_tr18);
        	_confrontoProdotti.append(_trvalidita);
        	_confrontoProdotti.append(_tr19);        	
        	
    		$("#confrontoProdottiContainer").append(_confrontoProdotti);
    		
    	}      
    }
    
});