/*	
 * Albero delle Categorie (CAIS) e delle Categorie Associate alla gara (OPES).
 * 
 * La chiamata Ajax restituisce oggetto JSONArray strutturato nel seguente modo:
 * 
 * 0 - Livello (-2, -1, 0, 1 ...)
 * 1 - Tipo di categoria (CAIS.TIPLAVG)
 * 2 - Eventuale titolo per raggruppamento categorie (CAIS.TITCAT)
 * 3 - Codice categoria (CAIS.CAISIM)
 * 4 - Codice categoria livello 1
 * 5 - Codice categoria livello 2
 * 6 - Codice categoria livello 3
 * 7 - Codice categoria livello 4
 * 8 - Descrizione del tipo categoria, del titolo o della categoria
 * 9 - Numero ARTICOLI annidati ASSOCIATI (default 0)
 * 10 - Identificativo dell'articolo (MEARTCAT.ID)
 * 11 - Articolo inserito nel carrello articoli
 * 12 - Quantita' dell'articolo nel carrello articoli
 * 
 * 
 */

$(window).on("load", function (){
	
	var lunghezzamassimadescrizione = 90;

    $('#deletesearch').click(function() {
    	clearSearchCategorie();
    	$("#messaggioricerca").html("");
    });
    
    $('#expandall').click(function() {
    	$("#articolitree").jstree("open_all","-1","true");
    });

    $('#collapseall').click(function() {
    	$("#articolitree").jstree("close_all","-1","true");
    });
	
    $('#textsearch').keyup(function() {
    	delay(function(){
    		searchCategorie();
    	}, 600);
    });
    
    $('#formAggiungiArticolo').submit(false);
    $('#formAggiungiArticoloQuantitaUM').submit(false);
    
    
	/*
	 * Ad ogni apertura di un nodo evidenzia la stringa cercata.
	 */
	$("#articolitree").bind("open_node.jstree", function (e, data) {
		if ($("#textsearch").val() != null && $("#textsearch").val() != "")
		{
			var words = $("#textsearch").val().split(' ');
			for (var i = 0; i < words.length; i++) {
				word = words[i];
				if (word != "" && word != " " && word.length > 2) {
					$('#articolitree ul li').highlight(word);
				}
			}
		}
	});	

	$("#articolitree").bind("before.jstree", function (e, data) {
		if ($("#textsearch").val() != null && $("#textsearch").val() != "") {
			var visualizzamessaggio = false;
			var words = $("#textsearch").val().split(' ');
			for (var i = 0; i < words.length; i++) {
				word = words[i];
				if (word != "" && word != " " && word.length > 2 && visualizzamessaggio == false) {
					visualizzamessaggio = true;
				}
			}
			if (visualizzamessaggio == true) {
				searchMessaggio();
			} else {
				$("#messaggioricerca").html("Indicare almeno tre caratteri.");
			}
		}
	});
	
	/*
	 * Apro il menu', gia' disponibile con il tasto destro del mouse, 
	 * alla selezione del nodo.
	 */
    $('#articolitree').bind('select_node.jstree', function(e,data) {
		if (data.rslt.obj.attr("caisim")) {
			delay(function(){
				var x;
				var y;
				if (data.args.length > 2) {
					x = data.args[2].pageX;
					y = data.args[2].pageY;
					var id = data.rslt.obj.attr('id');
					id = id.replace(/\./g,'\\.');
					id = id.replace(/\//g,'\\/');
					$('#articolitree').jstree("show_contextmenu",'#' + id, x, y);
				}
			}, 100);
		}
    });


    /*
	 * Inizializzazione albero.
	 */
    $("#articolitree").jstree(
		{ 
			"core" : {
				"html_titles" : true,
				"animation" : 100
			},
			"plugins" : [ "themes", "json_data", "ui", "types", "search", "cookies", "contextmenu" ],
			"themes" : { "theme" : "classic", "url" : "css/jquery/jstree/themes/classic/style.css"  },
			"ui" : { "select_limit" : 1	},
			"contextmenu" : { 
				"items" : articoliMenu,
				"show_at_node" : false
			},
			"types" : {
				"type_attr" : "tiponodo",
				"types" : {
					"C" : {
						"icon" : {"image" : "img/categoria_arancione.gif"}
					},
					"ART" : {
						"icon" : {"image" : "img/articolo.gif"}
					},
					"ARTCAR" : {
						"icon" : {"image" : "img/articolocarrello.gif"}
					}
				}
			},
			"cookies" : {
				"save_loaded" : $("#MERIC_ID").val() + "_jstree_load",
				"save_opened" : $("#MERIC_ID").val() + "_jstree_open",
				"save_selected" : $("#MERIC_ID").val() + "_jstree_select"
			},
			"search" : {
				"case_insensitive" : true,
				"show_only_matches" : false,
				"search_method" : "jstree_contains_any",
				"ajax" : {
					async: true,
					url: "pg/GetArticoliAlbero.do",
	                data : function (n) { 
						return {
							operation: "search",
							ngara: $("#MERIC_CODCATA").val(),
							textsearch: $("#textsearch").val()
						};	
					},
					success: function( data ) {
						$("#attesa").show();
	                	SearchArticoliArray = [];
	                	if (data && data.length > 0) {
							$.map( data, function( item ) {
								var search_node = "#";
								if (item[2] != null && item[2] != "") {
									search_node += "C_" + item[2] + "_";
								}
								if (item[1] != null && item[1] != "") {
									search_node += "T_" + item[1] + "_";
								}
								search_node += "R_" + item[0];
								search_node = search_node.replace(/\./g,'\\.');
								SearchArticoliArray.push(search_node);
							});
						} 
						return SearchArticoliArray;
					},
					complete: function (e) {
						$("#attesa").hide();
					}
				}
			},
			"json_data" : { 
				"ajax" : {
					async: true,
				    type: "GET",
	                dataType: "json",
	                beforeSend: function(x) {
	    			if(x && x.overrideMimeType) {
	        			x.overrideMimeType("application/json;charset=UTF-8");
				       }
					},
	                url: "pg/GetArticoliAlbero.do",
	                data : function (n) { 
						return {
							operation : "load",
							livello : n.attr ? n.attr("livello") : "-2",
							tiplavg : n.attr ? n.attr("tiplavg") : "0",
							titcat : n.attr ? n.attr("titcat") : "",
							caisim : n.attr ? n.attr("caisim") : "",
							caisim_livello1 : n.attr ? n.attr("caisim_livello1") : "",
							caisim_livello2 : n.attr ? n.attr("caisim_livello2") : "",
							caisim_livello3 : n.attr ? n.attr("caisim_livello3") : "",
							caisim_livello4 : n.attr ? n.attr("caisim_livello4") : "",
							ngara : $("#MERIC_CODCATA").val(),
							meric_id : $("#MERIC_ID").val()
						};	
					},
	                success: function( data ) {
	                	$("#attesa").show();
	                	ArticoliArray = [];
	                	if (data && data.length > 0) {
							$.map( data, function( item ) {
								
								/*
								 * Descrizione del nodo. 
								 * Il nodo di tipo 51 è quello degli articoli.
								 */
								var descrizione = "";
								
								if (item[0] == '51') {
									descrizione += item[8] + " [Art. " + item[3] + "]";
								} else {
									if (item[0] != "-1" && item[0] != "0") {
										descrizione += item[3] + " - ";
									}
									if (item[8] != null) {
										descrizione += item[8];
									}
								}

								var descrizione_tooltip = '<span title="' + descrizione + '">';
								
								/*
								 * Riduco la dimensione della descrizione se troppo lunga.
								 */
								if (descrizione.length > lunghezzamassimadescrizione) {
									descrizione = descrizione.substring(0,lunghezzamassimadescrizione) + "...";
								}
							
								if (item[0] == '51') {
									if (item[11] == true) {
										descrizione += " - Quantit&agrave; in carrello: " + _formattaImporto(item[12]);
									}
								}
								
								// Importo minimo ordine
								if (item[0] != '51' && item[13] != null) {
									var importoMinimoOrdine = formatNumber(item[13],18.2);
									descrizione += " <i>[Importo minimo ordine: " + _formattaImporto(importoMinimoOrdine) + " \u20AC]</i>";
								}
								
								descrizione_tooltip += descrizione;
								
								/*
								 * Identificativo del nodo
								 * 
								 * R - Root
								 * T - Titolo
								 * C - Categoria
								 * A - Articolo
								 * 
								 */
								var nodeid = "";
								if (item[0] == '51') {
									nodeid = "A_" + item[3];
								} else {
									if (item[3] != null && item[3] != "") {
										nodeid += "C_" + item[3] + "_";
									}
									if (item[2] != null && item[2] != "") {
										nodeid += "T_" + item[2] + "_";
									}
									nodeid += "R_" + item[1];
								}
								
								/*
								 * Tipo di nodo (corrispondente alla sezione types) 
								 */
								var tiponodo = "";
								if (item[0] == '-1' || item[0] == '0') {
									tiponodo = "P";
								} else if (item[0] == '51') {
									if (item[11]) {
										tiponodo = "ARTCAR";
									} else {
										tiponodo = "ART";
									}
								} else {
									tiponodo = "C";
								}
								
								/*
								 * Stato della categoria. Se il livello e' l'ultimo 
								 * (non ci sono elementi annidati o associata) lo stato
								 * non deve essere closed ma indefinito in modo da non 
								 * visualizzare il simbolo "+" per l'apertura del nodo
								 */
								statoNodo = "closed";
								if (item[9] == 0){
									statoNodo = "";
								}
								
								ArticoloItem = {
									"data" : descrizione_tooltip,	
									"attr" : {
										"tiponodo" : tiponodo,
										"livello" : item[0],	
										"id" : nodeid,
										"tiplavg" : item[1],
										"titcat" : item[2],
										"caisim" : item[3],
										"caisim_livello1" : item[4],
										"caisim_livello2" : item[5],
										"caisim_livello3" : item[6],
										"caisim_livello4" : item[7],
										"descrizione" : item[8],
										"numcategorieassociate" : item[9],
										"meartcat_id" : item[10],
										"incarrello" : item[11],
										"quantita_articolo" : item[12]
									},
									"title" : item[8],
									"state" : statoNodo
								},
								ArticoliArray.push(ArticoloItem);
							});
						} 
						return ArticoliArray;
					},
					complete : function( e ) {
						$("#attesa").hide();
						$(".contenitore-dettaglio").width($("#articolitree").width());
					}
				}
			}
		}
	);
    
    
    /*
     * Menu' personalizzato
     */
    function articoliMenu(node) {
        var items = {
        	schedaItem: {
        		label: "Apri scheda articolo",
        		action: function (obj) { _apriSchedaArticolo(node); },
        		_disabled : false
        	},
        	aggiungiItem: {
        		label: "Aggiungi al carrello",
        		"separator_before"  : true,
        		action: function (obj) { _apriaggiungiarticolo(node); },
        		icon: false,
        		_disabled: false
        	}
        };
        
      
        /*
         * Il menu' e' disponibile solo per gli articoli
         */
        if (node.attr("tiponodo") == "C") {
        	items.aggiungiItem._disabled = true;
        	items.schedaItem._disabled = true;
        } else {
        	if (node.attr("incarrello") == "true") {
        		items.aggiungiItem._disabled = true;	
        	} 
        }
        
        /*
         * Controllo autorizzazione alla modifiche, se non autorizzato
         * e' possibile consultare la scheda dell'articolo ma non
         * gestire l'inserimento in carrello.
         */
        if ($("#AUTORIZZATOMODIFICHE").val() == "2" || ($("#MERIC_DATVAL").val() != null && $("#MERIC_DATVAL").val() != "")) {
        	items.aggiungiItem._disabled = true;
        } else {
            /*
             * Verifica esistenza esistenza prodotti associati (solo per nodi articolo)
             */
        	if (node.attr("tiponodo") != "C") {
	            var meartcat_id = node.attr("meartcat_id");
	    		var numeroProdottiAssociati = _esistonoProdottiAssociati(meartcat_id);
	    		items.aggiungiItem.label = "Aggiungi al carrello [" + numeroProdottiAssociati + " prodotti associati]";
	    		if (numeroProdottiAssociati==0) {
	    			items.aggiungiItem._disabled = true;
	    			items.aggiungiItem.icon = "img/eliminaricerca.png";
	    		}
    		}
        }
        
        return items;
    };
    
    /*
     * Apertura della scheda dell'articolo.
     */
    function _apriSchedaArticolo(node) {
		var key = "MEARTCAT.ID=N:" + node.attr("meartcat_id");
		document.formSchedaArticolo.key.value=key;
		bloccaRichiesteServer();
		document.formSchedaArticolo.submit();
    }

    
    /*
     * Ritardo.
     */
	var delay = (function(){
		  var timer = 0;
		  return function(callback, ms){
		    clearTimeout (timer);
		    timer = setTimeout(callback, ms);
		  };
	})();
    
	
	/*
	 * Ripulisce la ricerca (casella di input ed albero).
	 */
	function clearSearchCategorie() {
		if ($("#textsearch").val() != null && $("#textsearch").val() != "") {
    		$("#textsearch").val(null);
    		$('#articolitree ul li').unhighlight();
    		$("#articolitree").jstree("clear_search");
		}
	}
	
	
	/*
	 * Ricerca categorie.
	 */
    function searchCategorie() {
    	
    	$("#attesa").show();
    	
		$("#articolitree").jstree("close_all");
		$("#articolitree").jstree("clear_search");
		$('#articolitree ul li').unhighlight();
		$("#articolitree").jstree("search", $("#textsearch").val());
		
		var words = $("#textsearch").val().split(' ');
		for (var i = 0; i < words.length; i++) {
			word = words[i];
			if (word != "" && word != " " && word.length > 2) {
				$('#articolitree ul li').highlight(word);
			}
		}

		if ($("#textsearch").val() == null || $("#textsearch").val() == "") {
			$("#messaggioricerca").html("");
			$("#attesa").hide();
		}
	}
   
    
    /*
     * Conteggio elementi trovati
     */
    function searchMessaggio() {
    	var numero = $("a.jstree-search").length;
		if (numero) {
	    	if (numero == 0) {
				$("#messaggioricerca").html("Nessun elemento trovato.");
			} else if (numero == 1) {
				$("#messaggioricerca").html("Trovato 1 elemento.");
			} else {
				$("#messaggioricerca").html("Trovati " +  numero + " elementi.");
			}
		} else {
			$("#messaggioricerca").html("Nessun elemento trovato.");
		}
    }
    

	/*
	 * Metodo di ricerca aggiuntivo per "search" di jstree.
	 * Questo metodo permette la ricerca in AND di
	 * vari termini separati da "spazio".
	 */
	$.expr[':'].jstree_contains_all = function(a,i,m) {
		var word, words = [];
		var searchFor = m[3].toLowerCase().replace(/^\s+/g,'').replace(/\s+$/g,'');
		if (searchFor.indexOf(' ') >= 0) {
			words = searchFor.split(' ');
		}
		else {
			words = [searchFor];
		}
		for (var i = 0; i < words.length; i++) {
			word = words[i];
			if (word != "" && word != " " && word.length > 2) {
				if ((a.textContent || a.innerText || "").toLowerCase().indexOf(word) == -1) {
					return false;
				}
			}
		}
		return true;
	};
	
	
	/*
	 * Metodo di ricerca aggiuntivo per "search" di jstree.
	 * Questo metodo permette la ricerca in OR di
	 * vari termini separati da "spazio".
	 */
	$.expr[':'].jstree_contains_any = function(a,i,m) {
		var word, words = [];
		var searchFor = m[3].toLowerCase().replace(/^\s+/g,'').replace(/\s+$/g,'');
		if (searchFor.indexOf(' ') >= 0) {
			words = searchFor.split(' ');
		}
		else {
			words = [searchFor];
		}
		for (var i = 0; i < words.length; i++) {
			word = words[i];
			if (word != "" && word != " " && word.length > 2) {
				var descrizioneestesa = a.parentNode.attributes.descrizione.value;
				if (((a.textContent || a.innerText || "").toLowerCase().indexOf(word) >= 0) || 
				    ((descrizioneestesa || "").toLowerCase().indexOf(word) >= 0)) {
					return true;
				}
			}
		}
		return false;
	};
	
	
	/* 
	 * ****************************************************************************
	 * 
	 * MASCHERE DI GESTIONE
	 * 
	 * ****************************************************************************
	 */
	
	/* 
	 * Maschera di gestione per l'inserimento dell'articolo.
	 * 
	 */
    $( "#mascheraAggiungiArticolo" ).dialog({
    	autoOpen: false,
    	width: 450,
    	height: 200,
    	show: {
    		effect: "blind",
    		duration: 200
        },
        hide: {
        	effect: "blind",
        	duration: 200
        },
        modal: true,
        resizable: false,
        buttons: {
            "Conferma" : function() {
            	if ( $("#formAggiungiArticolo").validate().form()) {
            		var options = $("#mascheraAggiungiArticolo").dialog( "option" );
            		_aggiungiarticolo(options.node,$("#quantita_articolo").val());
            	} 
            },
            "Annulla" : function() {
            	$(this).dialog( "close" );
            }
          }
    });
    
    /* 
	 * Maschera di gestione per l'inserimento dell'articolo con modalità acquisto quantità per UM.
	 * 
	 */
    $( "#mascheraAggiungiArticoloQuantitaUM" ).dialog({
    	autoOpen: false,
    	width: 550,
    	height: 430,
    	show: {
    		effect: "blind",
    		duration: 400
        },
        hide: {
        	effect: "blind",
        	duration: 400
        },
        modal: true,
        resizable: false,
        buttons: {
            "Conferma" : function() {
            	if ( $("#formAggiungiArticoloQuantitaUM").validate().form()) {
            		var options = $("#mascheraAggiungiArticoloQuantitaUM").dialog( "option" );
            		_aggiungiarticoloQuantitaUM(options.node,$("#desdet1").val(),
            				$("#desdet2").val(),$("#quadet1").val(),$("#quadet2").val(),
            				$("#quantita_Tot").val());
            	} 
            },
            "Annulla" : function() {
            	$(this).dialog( "close" );
            }
          }
    });
    
    /*
     * Maschera di gestione per l'eliminazione dell'articolo.
     * 
     */
    $( "#mascheraEliminaArticolo" ).dialog({
    	autoOpen: false,
    	width: 400,
    	height: 200,
    	show: {
    		effect: "blind",
    		duration: 200
        },
        hide: {
        	effect: "blind",
        	duration: 200
        },
        modal: true,
        resizable: false,
        buttons: {
            "Conferma" : function() {
           		var options = $("#mascheraEliminaArticolo").dialog( "option" );
           		_eliminaarticolo(options.node);

            },
            "Annulla" : function() {
            	$(this).dialog( "close" );
            }
          }
    });
    
    
	
    $( "#mascheraAggiungiArticolo" ).on( "dialogclose", function( event, ui ) {
    	$(".contenitore-dettaglio").width($("#articolitree").width()); 
    	$("#quantita_articolo").val("1");
    });
    
    $( "#mascheraAggiungiArticoloQuantitaUM" ).on( "dialogclose", function( event, ui ) {
    	$(".contenitore-dettaglio").width($("#articolitree").width()); 
    	$("#desdet1").val("");
    	$("#desdet2").val("");
    	$("#quadet1").val("");
    	$("#quadet2").val("");
    	$("#quantita_Fit").val("");
    	$("#quantita_Tot").val("");
    	validatoreUM.resetForm();
    });
	
    $( "#mascheraEliminaArticolo" ).on( "dialogclose", function( event, ui ) {
    	$("#messaggioArticoloAcquistato").hide();
    });
    
    /*
     * Definizione metodo di validazione importo
     */
	jQuery.validator.addMethod("isImportoValido", function(value, element) { return isImportoValido(value);	}, "Formato non valido"	);
    
	/*
	 * Definizione validazione form di inserimento
	 */
	 var validatoreUM = $("#formAggiungiArticoloQuantitaUM").validate({
    	rules: { 
    		desdet1:"required",
    		desdet2:"required",
			quadet1:{
				isImportoValido: true,
    			required: true,
    			min: 1
			},
			quadet2:{
				isImportoValido: true,
    			required: true,
    			min: 1
			}
    	},
		messages: {
			desdet1:{
				required: "Valore obbligatorio"
			},
			desdet2:{
				required: "Valore obbligatorio"
			},
			quadet1: {
				isImportoValido: "Attenzione! E' stato inserito un valore non consentito: i caratteri ammessi sono le cifre ed il punto come separatore decimale",
				required: "Valore obbligatorio",
				min: "Inserire un valore maggiore di 0"
			},
			quadet2: {
				isImportoValido: "Attenzione! E' stato inserito un valore non consentito: i caratteri ammessi sono le cifre ed il punto come separatore decimale",
				required: "Valore obbligatorio",
				min: "Inserire un valore maggiore di 0"
			}
		},
		errorPlacement: function(error, element) {
			var msgErr;
			if(element.attr("name")=="desdet1"){
				msgErr ="#errorMessageDesc1";
			}else if(element.attr("name")=="desdet2"){
				msgErr ="#errorMessageDesc2";
			}else if(element.attr("name")=="quadet1"){
				msgErr ="#errorMessageQuant1";
			}else if(element.attr("name")=="quadet2"){
				msgErr ="#errorMessageQuant2";
			}   
			error.appendTo( $(msgErr) );
	    }
	});
    
    $("#formAggiungiArticolo").validate({
    	rules: { 
    		quantita_articolo: {
    			isImportoValido: true,
    			required: true,
    			min: 1
    		}
    	},
		messages: {
			quantita_articolo: {
				isImportoValido: "Attenzione! E' stato inserito un valore non consentito: i caratteri ammessi sono le cifre ed il punto come separatore decimale",
				required: "Valore obbligatorio",
				min: "Inserire un valore maggiore di 0"
			} 	
		},
		errorPlacement: function(error, element) {
			error.appendTo( $("#errorMessage") );
	    }
	});
    
    /*
     * Maschera di avviso
     */
    $( "#mascheraNoAggiungiArticolo" ).dialog({
    	autoOpen: false,
    	width: 400,
    	show: {
    		effect: "blind",
    		duration: 200
        },
        hide: {
        	effect: "blind",
        	duration: 200
        },
        modal: true,
        resizable: false,
				focusCleanup: true,
				cache: false,
        buttons: {
            "Annulla" : function() {
            	$(this).dialog( "close" );
            }
        }
    });
    
    /*
     * Apertura maschera di conferma inserimento nel carrello.
     * E' possibile assegnare anche la quantita'
     * 
     */
    function _apriaggiungiarticolo(node) {
		$(".ui-dialog-titlebar").hide();
		$("#mascheraAggiungiArticolo").dialog( "option", { node: node } );
		
		var meartcat_id = node.attr("meartcat_id");

		var isQuantitaArticoloPerUM = _isQuantitaArticoloPerUM(meartcat_id);
		if(isQuantitaArticoloPerUM){
			$("#mascheraAggiungiArticoloQuantitaUM").dialog( "option", { node: node } );
			var descUnimis = _getDesrizioneUnintaMisura(meartcat_id);
			document.getElementById('unimis').innerHTML = descUnimis;
			$("#mascheraAggiungiArticoloQuantitaUM").dialog( "open" );
		}else{
			$("#quantita_articolo").val("1");
			$("#mascheraAggiungiArticolo").dialog( "open" );
		}
    }


    /*
     * Inserimento dell'articolo selezionato in carrello.
     */
	function _aggiungiarticolo(node, quanti) {
		var meric_id = $("#MERIC_ID").val();
		var meartcat_id = node.attr("meartcat_id");
		$.ajax({
		  async: false,
		  url: "pg/AggiungiArticoloACarrello.do?meric_id=" + meric_id + "&meartcat_id=" + meartcat_id + "&quanti=" + quanti
		}).done(function() {
			$("#mascheraAggiungiArticolo").dialog("close");
			var parent_node = $.jstree._reference('#articolitree')._get_parent(node);
			$("#articolitree").jstree("refresh", parent_node);
		});
	}
	
	function _aggiungiarticoloQuantitaUM(node, des1,des2,quanti1, quanti2,quantiTot) {
		var meric_id = $("#MERIC_ID").val();
		var meartcat_id = node.attr("meartcat_id");
		$.ajax({
		  async: false,
		  url: "pg/AggiungiArticoloACarrello.do?meric_id=" + meric_id + "&meartcat_id=" + meartcat_id + "&des1=" + des1 +
		  	"&des2=" + des2 + "&quanti1=" + quanti1 + "&quanti2=" + quanti2 + "&quanti=" + quantiTot
		}).done(function() {
			$("#mascheraAggiungiArticoloQuantitaUM").dialog("close");
			var parent_node = $.jstree._reference('#articolitree')._get_parent(node);
			$("#articolitree").jstree("refresh", parent_node);
		});
	}
    
    
    /*
     * Apertura maschera di conferma eliminazione dal carrello.
     */
    function _aprieliminaarticolo(node) {
		$(".ui-dialog-titlebar").hide();
		$("#mascheraEliminaArticolo").dialog( "option", { node: node } );
		$("#mascheraEliminaArticolo").dialog( "open" );
    }
    
    
    /*
     * Eliminazione articolo.
     */
	function _eliminaarticolo(node) {
		var meric_id = $("#MERIC_ID").val();
		var meartcat_id = node.attr("meartcat_id");
		$.ajax({
		  async: false,
		  url: "pg/EliminaArticoloDaCarrello.do?meric_id=" + meric_id + "&meartcat_id=" + meartcat_id
		}).done(function() {
			$("#mascheraEliminaArticolo").dialog("close");
			var parent_node = $.jstree._reference('#articolitree')._get_parent(node);
			$("#articolitree").jstree("refresh", parent_node);
		});
	}
	
    /*
     * Verifico se esistono prodotti associati all'articoli
     */
	function _esistonoProdottiAssociati(meartcat_id) {
		var numeroProdottiAssociati = 0;
		$.ajax({
            type: "GET",
            dataType: "json",
            async: false,
            beforeSend: function(x) {
			if(x && x.overrideMimeType) {
    			x.overrideMimeType("application/json;charset=UTF-8");
		       }
			},
            url: "pg/EsistonoProdottiAssociati.do",
            data: "meartcat_id=" + meartcat_id,
            success: function(data) {
            	numeroProdottiAssociati = data.numeroProdottiAssociati;
            },
            error: function(e){
                alert("Errore durante il controllo di esistenza dei prodotti associati all'articolo");
            }
        });
        return numeroProdottiAssociati;
    }
	
    /*
     * Controllo validazione importo
     */
	function isImportoValido(quantitaString) {
		var ValidChars = ".0123456789";
		var Char;
		var findDec=false;
		var idx, i;
		for (i = (quantitaString.charAt(0)=='-' ? 1:0); i < quantitaString.length ; i++){ 
			Char = quantitaString.charAt(i); 
			idx=ValidChars.indexOf(Char);
			if ( idx == -1){
				return false;
			}
			if(idx == 0){
				if(findDec) 
					return false;
				else
					findDec=true;
			}
		}
		return true;
	}
	
	/*
	 * Formattazione dell'importo
	 */
    function _formattaImporto(quantita) {
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
     * Verifico se la modalità di acquisto è articolo\prodotto UM
     */
	function _isQuantitaArticoloPerUM(meartcat_id) {
		var isQuantitaArticoloPerUM = false;
        $.ajax({
            type: "GET",
            dataType: "json",
            async: false,
            beforeSend: function(x) {
			if(x && x.overrideMimeType) {
    			x.overrideMimeType("application/json;charset=UTF-8");
		       }
			},
            url: "pg/IsQuantitaArticoloPerUM.do",
            data: "meartcat_id=" + meartcat_id,
            success: function(data) {
            	if (data.isQuantitaArticoloPerUM) {
            		isQuantitaArticoloPerUM = true;
                } else {
                	isQuantitaArticoloPerUM = false;
                }
            },
            error: function(e){
                alert("Errore durante il controllo del valore della modalità di acquisto dell'articolo");
            }
        });
        return isQuantitaArticoloPerUM;
    }
	
	/*
     * Verifico se esistono prodotti associati all'articoli
     */
	function _getDesrizioneUnintaMisura(meartcat_id) {
		var desrizioneUnintaMisura;
        $.ajax({
            type: "GET",
            dataType: "json",
            async: false,
            beforeSend: function(x) {
			if(x && x.overrideMimeType) {
    			x.overrideMimeType("application/json;charset=UTF-8");
		       }
			},
            url: "pg/GetDesrizioneUnitaMisuraArticolo.do",
            data: "meartcat_id=" + meartcat_id,
            success: function(data) {
            	desrizioneUnintaMisura=data.descrizioneUnimis;
            },
            error: function(e){
                alert("Errore durante il controllo del valore della modalità di acquisto dell'articolo");
            }
        });
        return desrizioneUnintaMisura;
    }
	
	$("#quadet1").change(function(){
		  	var quanti;
			if(this.value!=null && this.value!=""){
			  var quanti1 = this.value;
			  var quanti2 = $("#quadet2").val();
			  if(quanti2!=null && quanti2!=""){
				  var quanti = round(parseFloat(quanti1 * quanti2), 5); 
			  }
			  
		  }
			$("#quantita_Fit").val(quanti);	
			$("#quantita_Tot").val(quanti);	
			
	});
    
	$("#quadet2").change(function(){
	  	var quanti;
		if(this.value!=null && this.value!=""){
		  var quanti2 = this.value;
		  var quanti1 = $("#quadet1").val();
		  if(quanti1!=null && quanti1!=""){
			  var quanti = round(parseFloat(quanti1 * quanti2), 5); 
		  }
		  
	  }
		$("#quantita_Fit").val(quanti);
		$("#quantita_Tot").val(quanti);	
		
		
});
	
});
