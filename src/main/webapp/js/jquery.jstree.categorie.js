/*	
 * Albero delle Categorie (CAIS) e delle Categorie Associate alla gara (OPES).
 * 
 * La chiamata Ajax restituisce oggetto JSONArray strutturato nel seguente modo:
 * 
   * 0 - Livello (-2, -1, 0, 1 ...50, 51)
   * 1 - Tipo di categoria (CAIS.TIPLAVG)
   * 2 - Eventuale titolo per raggruppamento categorie (CAIS.TITCAT)
   * 3 - Codice categoria (CAIS.CAISIM) o identificativo dell'articolo (MEARTCAT.ID)
   * 4 - Codice categoria livello 1
   * 5 - Codice categoria livello 2
   * 6 - Codice categoria livello 3
   * 7 - Codice categoria livello 4
   * 8 - Descrizione del tipo categoria, del titolo o della categoria
   * 9 - Numero CATEGORIE annidate NON ARCHIVIATE (default 0)
   * 10 - Numero CATEGORIE annidate ARCHIVIATE (default 0)
   * 11 - Numero CATEGORIE annidate ASSOCIATE (default 0)
   * 12 - Numero ARTICOLI annidati ASSOCIATI (default 0)
   * 13 - Categoria associata ? (boolean, default FALSE)
   * 14 - Categoria archiviata ? (boolean, default FALSE)
   * 15 - Chiave (OPES.NOPEGA) della categoria associata o identificativo dell'articolo (MEARTCAT.ID)
   * 16 - Importo minimo ordine
   * 
   * 
 */

$(window).on("load", function (){
	
	$('#expandlegendack').click(function() {
		$("#legendack").slideDown(1000);
		$('#expandlegendack').hide();
		$('#collapselegendack').show();
    });
	
	$('#collapselegendack').click(function() {
		$("#legendack").slideUp(1000);
		$('#expandlegendack').show();
		$('#collapselegendack').hide();
    });
	
	var lunghezzamassimadescrizione = 90;

    $('#deletesearch').click(function() {
    	clearSearchCategorie();
    	$("#messaggioricerca").html("");
    });
    
    $('#expandall').click(function() {
    	$("#categorietree").jstree("open_all","-1","true");
    });

    $('#collapseall').click(function() {
    	$("#categorietree").jstree("close_all","-1","true");
    });
	
    $('#textsearch').keyup(function() {
    	delay(function(){
    		searchCategorie();
    	}, 600);
    });
    
    $('#formImportoOrdineMinimo').submit(false);
	
    var menuSelezioneArchivio = $("#menuSelezioneArchivio").val();  
    
    /*
	 * In modalita' modifica provvedo a resettare i cookies
	 */
	if ($("#MODOAPERTURA").val() != "VISUALIZZA") {
		$.cookie($("#GARE_NGARA").val() + "_jstree_load", null);
		$.cookie($("#GARE_NGARA").val() + "_jstree_open", null);
		$.cookie($("#GARE_NGARA").val() + "_jstree_select", null);
	}
	    
	/*
	 * Ad ogni caricamento di un nodo, in visualizzazione, nasconde le checkbox.
	 */
	$("#categorietree").bind("load_node.jstree", function (e, data) {
		if ($("#MODOAPERTURA").val() == "VISUALIZZA") {
			$("li a ins.jstree-checkbox").hide();
		}
		$("li[tiponodo='ART'] a ins.jstree-checkbox").hide();
		$("li[tiponodo='CISARCHI'] a ins.jstree-checkbox").hide();
		$("li[tiponodo='PISARCHI'] a ins.jstree-checkbox").hide();
	});	
	
	/*
	 * Ad ogni apertura di un nodo evidenzia la stringa cercata.
	 */
	$("#categorietree").bind("open_node.jstree", function (e, data) {
		if ($("#textsearch").val() != null && $("#textsearch").val() != "")
		{
			var words = $("#textsearch").val().split(' ');
			for (var i = 0; i < words.length; i++) {
				word = words[i];
				if (word != "" && word != " " && word.length > 2) {
					$('#categorietree ul li').highlight(word);
				}
			}
		}
	});	

	$("#categorietree").bind("before.jstree", function (e, data) {
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
    $('#categorietree').bind('select_node.jstree', function(e,data) {
    	if ($("#GAREGENERE").val() == '20' && $("#MODOAPERTURA").val() == "VISUALIZZA") {
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
						$('#categorietree').jstree("show_contextmenu",'#' + id, x, y);
					}
				}, 100);
    		}
    	}
    });

    /*
     * Popola le variabili con le checkbox selezionate/deselezionate.
     */
	$("#categorietree").bind('uncheck_node.jstree', function (e, data) {
	    setCheckedUnchecked();
	});

	/*
	 * Popola le variabili con le checkbox selezionate/deselezionate. 
	 */
	$("#categorietree").bind('check_node.jstree', function (e, data) {
        setCheckedUnchecked();
	});
	
	
	/*
	 * Inizializzazione albero.
	 */
    $("#categorietree").jstree(
		{ 
			"core" : {
				"html_titles" : true,
				"animation" : 300
			},
			"plugins" : [ "themes", "json_data", "ui", "types", "search", "checkbox", "cookies", "contextmenu" ],
			"themes" : { "theme" : "classic", "url" : "css/jquery/jstree/themes/classic/style.css"  },
			"ui" : { "select_limit" : 1	},
			"contextmenu" : { 
				"items" : caisMenu,
				"show_at_node" : false
			},
			"cookies" : {
				"save_loaded" : $("#GARE_NGARA").val() + "_jstree_load",
				"save_opened" : $("#GARE_NGARA").val() + "_jstree_open",
				"save_selected" : $("#GARE_NGARA").val() + "_jstree_select"
				
			},
			"types" : {
				"type_attr" : "tiponodo",
				"types" : {
					"P" : {
						"valid_children" : [ "default" ]
					},
					"PISARCHI" : {
						"check_node" : false, 
			            "uncheck_node" : true
					},
					"PA" : { 
			            "check_node" : true, 
			            "uncheck_node" : false 
		            },
					"PAISARCHI" : { 
						"check_node" : false, 
			            "uncheck_node" : true
		            },
					"C" : {
						"icon" : {"image" : "img/categoria_blu.gif"}
					},
					"CISARCHI" : {
						"icon" : {"image" : "img/categoria_grigio.gif"},
						"check_node" : false, 
			            "uncheck_node" : true
					},
					"CA" : {
						"icon" : {"image" : "img/categoria_arancione.gif"},
						"check_node" : true, 
			            "uncheck_node" : false
					},
					"CAISARCHI" : {
						"icon" : {"image" : "img/categoria_arancione.gif"},
						"check_node" : true, 
			            "uncheck_node" : false
					},
					"ART" : {
						"icon" : {"image" : "img/documentazione.gif"}
					}
				}
			},
			"checkbox" : {
				"checked_parent_open" : true
			},
			"search" : {
				"case_insensitive" : true,
				"show_only_matches" : false,
				"search_method" : "jstree_contains_any",
				"ajax" : {
					async: true,
					url: "pg/GetCategorieAlbero.do",
	                data : function (n) { 
						return {
							operation: "search",
							ngara: $("#GARE_NGARA").val(),
							textsearch: $("#textsearch").val(),
							modoapertura : $("#MODOAPERTURA").val(),
							genere : $("#GAREGENERE").val()
						};	
					},
					success: function( data ) {
						$("#attesa").show();
	                	SearchCategorieArray = [];
	                	if (data && data.length > 0) {
							$.map( data, function( item ) {
								var search_node = "#";
								if (item[2] != null && item[2] != "") {
									search_node += "C_____" + item[2] + "_____";
								}
								if (item[1] != null && item[1] != "") {
									search_node += "T_____" + item[1] + "_____";
								}
								search_node += "R_____" + item[0];
								search_node = search_node.replace(/\./g,'\\.');
								SearchCategorieArray.push(search_node);
							});
						} 
						return SearchCategorieArray;
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
	                url: "pg/GetCategorieAlbero.do",
	                data : function (n) { 
						return {
							operation : "load",
							modoapertura : $("#MODOAPERTURA").val(),
							livello : n.attr ? n.attr("livello") : "-2",
							tiplavg : n.attr ? n.attr("tiplavg") : "0",
							titcat : n.attr ? n.attr("titcat") : "",
							caisim : n.attr ? n.attr("caisim") : "",
							caisim_livello1 : n.attr ? n.attr("caisim_livello1") : "",
							caisim_livello2 : n.attr ? n.attr("caisim_livello2") : "",
							caisim_livello3 : n.attr ? n.attr("caisim_livello3") : "",
							caisim_livello4 : n.attr ? n.attr("caisim_livello4") : "",
							tipologie : $("#GAREALBO_TIPOELE").val(),
							ngara : $("#GARE_NGARA").val(),
							genere : $("#GAREGENERE").val()
						};	
					},
	                success: function( data ) {
	                	$("#attesa").show();
	                	CategorieArray = [];
	                	if (data && data.length > 0) {
							$.map( data, function( item ) {
								
								/*
								 * Descrizione del nodo. 
								 * Il nodo di tipo 51 � quello degli articoli.
								 */
								var descrizione = "";
								
								if (item[0] == '51') {
									descrizione += item[8] + " [Art. " + item[3] + "]";
								} else {
									if (item[14] == true) {
										descrizione += "[ARCHIVIATA] ";
									}
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

								// Importo minimo ordine
								if (item[0] != '51' && item[16] != null) {
									var importoMinimoOrdine = formatNumber(item[16],18.2);
									descrizione += " <i>[Importo minimo ordine: " + _formattaImporto(importoMinimoOrdine) + " \u20AC]</i>";
								}
								
								descrizione_tooltip += descrizione;
								
								
								/*
								 * Stile della checkbox.
								 * L'algoritmo serve per determinare lo stile delle checkbox
								 * nei nodi padre quando il nodo stesso e' ancora chiuso.
								 * 
								 * item[9] - numero categorie annidate non archiviate
								 * item[10] - numero categorie annidate archiviate
								 * item[11] - numero categorie annidate associate
								 * 
								 */
								var class_checkbox = "";
								if (item[9] == 0 && item[10] == 0) {
									/*
									 * Se non ci sono categorie annidate non archiviate
									 * o archiviate significa che il nodo 
									 * e' l'ultimo livello possibile, di conseguenza decido lo stato
									 * della checkbox solo sulla base se la categoria corrente e'
									 * associata o meno item[13].
									 */
									if (item[13] == true) {
										class_checkbox = "jstree-checked";
									} else {
										class_checkbox = "jstree-unchecked";
									}
								} else {
									/*
									 * Se ci sono categorie annidate, allora 
									 * devo gestire le checkbox su tre stati.
									 */
									if (item[11] == 0) {
										class_checkbox = "jstree-unchecked";
									} else if ((item[9] + item[10]) > item[11]){
										class_checkbox = "jstree-undetermined";
									} else if ((item[9] + item[10]) == item[11]) {
										class_checkbox = "jstree-checked";
									} 
								}
								
								// Se la riga e' un articolo lo setto sempre come ckecked.
								if (item[0] == '51') {
									class_checkbox = "jstree-checked";
								}
								
								
								/*
								 * Identificativo del nodo del nodo
								 * 
								 * R - Root
								 * T - Titolo
								 * C - Categoria
								 * A - Articolo
								 * 
								 */
								var nodeid = "";
								if (item[0] == '51') {
									nodeid = "A_____" + item[3];
								} else {
									if (item[3] != null && item[3] != "") {
										nodeid += "C_____" + item[3] + "_____";
									}
									if (item[2] != null && item[2] != "") {
										nodeid += "T_____" + item[2] + "_____";
									}
									nodeid += "R_____" + item[1];
								}
								
								/*
								 * Tipo di nodo (corrispondente alla sezione types)
								 * Il nodo � di tipo padre (non foglia ossia senza figli)
								 * se il numero di categorie annidati non archiviati (item[9]) o il
								 * numero di categorie annidate archiviate (item[10]) e'
								 * maggiore di 0.
								 * 
								 * Successivamente si deve controllare se esistono articoli annidati
								 * associati (item[12) per assegnare lo stile PA .
								 * 
								 * Infine, sulla categoria foglia (senza ulteriori livelli annidati)
								 * si deve controllare se e' archiviata per assegnare, anche in questo
								 * caso, lo stile CAISARCHI o CISARCHI.
								 * 
								 */
								var tiponodo = "";
								if (item[0] == '-1' || item[0] == '0') {
								//if (item[0] == '-1' || item[0] == '0' || item[9] > 0 || item[10] > 0) {
									if (item[12] > 0) {
										if (item[14] == false) {
											tiponodo = "PA";
										} else {
											tiponodo = "PAISARCHI";
										}
									} else {
										if (item[14] == false) {
											tiponodo = "P";
										} else {
											tiponodo = "PISARCHI";
										}
									}
								} else if (item[0] == '51') {
									tiponodo = "ART";
								} else {
									if (item[12] > 0) {
										if (item[14] == false) { 
											tiponodo = "CA";
										} else {
											tiponodo = "CAISARCHI";
										}
									} else {
										if (item[14] == false) {
											tiponodo = "C";
										} else {
											tiponodo = "CISARCHI";
										}
									}
								}
								
								/*
								 * Stato della categoria. Se il livello e' l'ultimo 
								 * (non ci sono elementi annidati o associata) lo stato
								 * non deve essere closed ma indefinito in modo da non 
								 * visualizzare il simbolo "+" per l'apertura del nodo
								 */
								statoNodo = "closed";
								if ((item[9] == 0 && item[10] == 0 && item[11] == 0 && item[12] == 0 && $("#MODOAPERTURA").val() == "VISUALIZZA") || 
									(item[9] == 0 && item[10] == 0 && item[11] == 0 && item[12] == 0 && $("#MODOAPERTURA").val() != "VISUALIZZA")){
									statoNodo = "";
								}
								
								CategoriaItem = {
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
										"numcategorienonarch" : item[9],
										"numcategoriearch" : item[10],
										"numcategorieassociate" : item[11],
										"numarticoliassociati" : item[12],
										"categoriaassociata": item[13],
										"categoriaarchiviata" : item[14],
										"keydb" : item[15],
										"ordmin" : item[16],
										"class" : class_checkbox
									},
									"title" : item[8],
									"state" : statoNodo
								},
								CategorieArray.push(CategoriaItem);
							});
						} 
						return CategorieArray;
					},
					complete : function( e ) {
						$("#attesa").hide();
						if(menuSelezioneArchivio!="true")
							$(".contenitore-dettaglio").width($("#categorietree").width());
					}
				}
			}
		}
	);
    
    
    /*
     * Menu' personalizzato
     */
    function caisMenu(node) {
    	var items;
    	if(menuSelezioneArchivio!="true"){
        	items = {
                	articoliItem: {
                		label: "Articoli",
                		"separator_before"  : true, 
                		_disabled : false,
                		"submenu" : {
                			listaItem: {
                        		label: "Apri lista articoli",
                        		action: function (obj) { _apriListaCategoria(node); },
                        		_disabled : false
                        	},
                        	schedaItem: {
                        		label: "Apri scheda articolo",
                        		action: function (obj) { _apriSchedaArticolo(node); },
                        		_disabled : false
                        	}
                		}
                	},
                	categoriaItem: {
                		label: "Categoria",
                		"separator_before"  : true, 
                		_disabled : false,
                		"submenu" : {
                			ordineMinimoItem: {
                				label: "Imposta ordine minimo",
                        		action: function (obj) { _ordineMinimo(node); },
                        		_disabled : $("#ABILITAORDINEMINIMO").val() == "false"
                        	}
                		}
                	}        	
                };
                
              
                /*
                 * Il menu' e' disponibile per tutte le categorie
                 * di qualsiasi livello (devono essere esclusi
                 * raggruppamenti di livello superiore, titoli...)
                 * Se l'albero e' in modifica il menu' deve essere disabilitato.
                 */
                if ($("#MODOAPERTURA").val() == "VISUALIZZA" && $("#GAREGENERE").val() == "20") {
        	        if (node.attr("caisim")) {
        		        if (node.attr("tiponodo") == "ART") {
        		        	items.articoliItem.submenu.listaItem._disabled = true;
        		        	items.categoriaItem._disabled = true;
        		        	items.categoriaItem.submenu.ordineMinimoItem._disabled = true;
        		        } else {
        		        	items.articoliItem.submenu.schedaItem._disabled = true;
        		        }
        	        } else {
        	        	items.articoliItem._disabled = true;
        	        	items.articoliItem.submenu.listaItem._disabled = true;
        	        	items.articoliItem.submenu.schedaItem._disabled = true;
        	        	items.categoriaItem._disabled = true;
        	        	items.categoriaItem.submenu.ordineMinimoItem._disabled = true;
        	        }
                } else {
                	items.articoliItem._disabled = true;
                	items.articoliItem.submenu.listaItem._disabled = true;
                	items.articoliItem.submenu.schedaItem._disabled = true;
                	items.categoriaItem._disabled = true;
                	items.categoriaItem.submenu.ordineMinimoItem._disabled = true;
                }
                
                /*
                 * Controllo autorizzazione alla modifiche
                 */
                if ($("#AUTORIZZATOMODIFICHE").val() == "2") {
                	items.categoriaItem._disabled = true;
                	items.categoriaItem.submenu.ordineMinimoItem._disabled = true;
                }
                
            
                
        }else{
        	items = {
                	categoriaItem: {
                		label: "Selezione categoria",
                		action: function (obj) { _selezionaCategoria(node); }, 
                		_disabled : false
                	}        	
                };
        	if (!(node.attr("tiponodo") == "C" || node.attr("tiponodo") == "CISARCHI" || node.attr("tiponodo") =="CA" || node.attr("tiponodo") =="CAISARCHI")) {
        		items.categoriaItem._disabled = true;
        	}
        }
    	
        
        return items; 
    };
    
    
    function _apriListaCategoria(node) {
		var nopega = node.attr("keydb");
		var caisim = node.attr("caisim");
		var descat = node.attr("descrizione");
		apriListaArticoli($("#GARE_NGARA").val(),nopega,caisim,descat);
    }
    
    function _apriSchedaArticolo(node) {
		var id = node.attr("keydb");
		var parent_node = $.jstree._reference('#categorietree')._get_parent(node);
		var nopega_parent = parent_node.attr("keydb");
		var caisim_parent = parent_node.attr("caisim");
		var descat_parent = parent_node.attr("descrizione");
		apriSchedaArticolo(id, $("#GARE_NGARA").val(),nopega_parent,caisim_parent,descat_parent);
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
    		$('#categorietree ul li').unhighlight();
    		$("#categorietree").jstree("clear_search");
		}
	}
	
	/*
	 * Ricerca categorie.
	 */
    function searchCategorie() {
    	
    	$("#attesa").show();
    	
		$("#categorietree").jstree("close_all");
		$("#categorietree").jstree("clear_search");
		$('#categorietree ul li').unhighlight();
		$("#categorietree").jstree("search", $("#textsearch").val());
		
		var words = $("#textsearch").val().split(' ');
		for (var i = 0; i < words.length; i++) {
			word = words[i];
			if (word != "" && word != " " && word.length > 2) {
				$('#categorietree ul li').highlight(word);
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
     * Imposta le variabili con le checkbox selezionate/deselezionate per il salvataggio.
     */
	function setCheckedUnchecked() {
        var checked_ids = [];
        $("#categorietree").jstree("get_checked",null,true).each(function () { 
        	if ($("#" + this.id).attr("tiponodo") != "ART") {
        		checked_ids.push(this.id);
        	}
        });
        $("#CHECKED").val(checked_ids);

        var undetermined_ids = [];
        var undetermined = $("#categorietree").find(".jstree-undetermined");
        undetermined.each(function () { 
        	if ($("#" + this.id).attr("tiponodo") != "ART") {
        		undetermined_ids.push(this.id);
        	}
        });
        $("#UNDETERMINED").val(undetermined_ids);
        
        var unchecked_ids = [];
        var unchecked = $("#categorietree").find("li.jstree-unchecked").not(".jstree-undetermined");
        unchecked.each(function () { 
        	if ($("#" + this.id).attr("tiponodo") != "ART") {
        		unchecked_ids.push(this.id);
        	}
        });
        $("#UNCHECKED").val(unchecked_ids);
	}
	
	/*
	 * Apre la lista degli articoli associati alla categoria.
	 */
	function apriListaArticoli(ngara, nopega, caisim, descat) {
		var where = "MEARTCAT.NGARA = ? AND MEARTCAT.NOPEGA = ?";
		var parametri = "T:" + ngara + ";N:" + nopega + ";";
		document.formListaArticoli.trovaAddWhere.value=where;
		document.formListaArticoli.trovaParameter.value=parametri;
		document.formListaArticoli.opes_ngara.value=ngara;
		document.formListaArticoli.opes_nopega.value=nopega;
		document.formListaArticoli.cais_caisim.value=caisim;
		document.formListaArticoli.cais_descat.value=descat;
		bloccaRichiesteServer();
		document.formListaArticoli.submit();
	}
	
	/*
	 * Apre la scheda dell'articolo selezionato. 
	 */
	function apriSchedaArticolo(id, ngara, nopega, caisim, descat) {
		var key = "MEARTCAT.ID=N:"+ id;
		document.formSchedaArticolo.key.value=key;
		document.formSchedaArticolo.opes_ngara.value=ngara;
		document.formSchedaArticolo.opes_nopega.value=nopega;
		document.formSchedaArticolo.cais_caisim.value=caisim;
		document.formSchedaArticolo.cais_descat.value=descat;		
		bloccaRichiesteServer();
		document.formSchedaArticolo.submit();
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
	 * SEZIONE FUNZIONE PER GESTIONE FINESTRA MODALE DI INSERIMENTO/AGGIORNAMENTO
	 * IMPORTO MINIMO ORDINE
	 * 
	 * ****************************************************************************
	 */
	
    /*
     * Maschera per l'inserimento/modifica dell'importo ordine minimo
     */
    $( "#mascheraImportoOrdineMinimo" ).dialog({
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
				cache: false,
				focusCleanup: true,
        buttons: {
            "Conferma" : function() {
            	if ( $("#formImportoOrdineMinimo").validate().form()) {
            		var options = $("#mascheraImportoOrdineMinimo").dialog( "option" );
            		setImportoMinimoOrdine(options.node,$("#ORDMIN").val());
            	} 
            },
            "Annulla" : function() {
            	$("#ORDMIN").val("");
            	$("#formImportoOrdineMinimo").validate().form();
            	$(this).dialog( "close" );
            }
          }
    });

    $( "#mascheraImportoOrdineMinimo" ).on( "dialogclose", function( event, ui ) {
    	$(".contenitore-dettaglio").width($("#categorietree").width()); 
    });
    
    /*
     * Maschera di avviso
     */
    $( "#mascheraNoImportoOrdineMinimo" ).dialog({
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
     * Aggiornamento importo minimo ordine
     */
	function setImportoMinimoOrdine(node, ordmin) {
		var ngara = $("#GARE_NGARA").val();
		var caisim = node.attr("caisim");
		$.ajax({
		  async: false,
		  url: "pg/SetImportoMinimoOrdine.do?ngara=" + ngara + "&caisim=" + caisim + "&ordmin=" + ordmin
		}).done(function() {
			$("#ORDMIN").val("");
			$("#mascheraImportoOrdineMinimo").dialog("close");
			var parent_node = $.jstree._reference('#categorietree')._get_parent(node);
			$("#categorietree").jstree("refresh", parent_node);
		});
	}
    
    
    /*
     * Definizione metodo di validazione importo
     */
	jQuery.validator.addMethod("isImportoValido", function(value, element) { return isImportoValido(value);	}, "Formato non valido"	);
    
	/*
	 * Definizione validazione form di inserimento
	 */
    $("#formImportoOrdineMinimo").validate({
    	rules: { 
    		ORDMIN: "isImportoValido"
    	},
		messages: {
			ORDMIN: "Attenzione! E' stato inserito un valore non consentito: i caratteri ammessi sono le cifre ed il punto come separatore decimale"			
		},
		errorPlacement: function(error, element) {
			error.appendTo( $("#errorMessage") );
	    }
	});
	
	/*
	 * Bug fix per IE per il dimensionamento corretto della finestra modale degli importi
	 */
	var showed = false;	
	$("#ORDMIN").keyup(function(){
		if ($("#formImportoOrdineMinimo").valid() && showed) {
			var newSize = 120;
			$("#mascheraImportoOrdineMinimo").height(newSize);
			showed = false;
		} else if (!$("#formImportoOrdineMinimo").valid() && !showed) {
			var newSize = 160;
			showed = true;
			$("#mascheraImportoOrdineMinimo").height(newSize);
		}
	});
    
    /*
     * Formattazione importo
     */
    $("#ORDMIN").change(
    	function() {
    		if ($("#ORDMIN").val() != "") {
    			$("#ORDMIN").val(formatNumber($("#ORDMIN").val(),18.2));
    		}
    	}
    );
    
    /*
     * Apertura maschera per la definizione dell'importo minimo.
     * Controlla che, per la categoria selezionata, l'importo
     * sia modificabile. Funzione eseguita dal menu'.
     */
    function _ordineMinimo(node) {
    	
    	var ngara = $("#GARE_NGARA").val();
		var caisim = node.attr("caisim");
		var isORDMINModificabile = _isORDMINModificabile(ngara, caisim);
		$(".ui-dialog-titlebar").hide();
		if (isORDMINModificabile) {
	    	
	    	var ordmin = node.attr("ordmin");
	    	if (ordmin != null) {
	    		$("#ORDMIN").val(formatNumber(ordmin,18.2));
	    	}
	    	
	    	$("#mascheraImportoOrdineMinimo").dialog( "option", { node: node } );
	    	$("#mascheraImportoOrdineMinimo").dialog("open");
		} else {
			$("#mascheraNoImportoOrdineMinimo").dialog("open");
		}
    }
    
    /*
     * Verifico se l'importo minimo dell'ordine e' modificabile
     */
	function _isORDMINModificabile(ngara, caisim) {
		var isORDMINModificabile = false;
        $.ajax({
            type: "GET",
            dataType: "json",
            async: false,
            beforeSend: function(x) {
			if(x && x.overrideMimeType) {
    			x.overrideMimeType("application/json;charset=UTF-8");
		       }
			},
            url: "pg/IsORDMINModificabile.do",
            data: "ngara=" + ngara + "&caisim=" + caisim,
            success: function(data) {
            	if (data.isORDMINModificabile == true) {
            		isORDMINModificabile = true;
                } else {
                	isORDMINModificabile = false;
                }
            },
            error: function(e){
                alert("Errore durante il controllo dell'importo minimo dell'ordine");
            }
        });
        return isORDMINModificabile;
    }
    
    
    /*
     * Controllo validazione importo
     */
	function isImportoValido(importoString) {
		var ValidChars = ".0123456789";
		var Char;
		var findDec=false;
		var idx, i;
		for (i = (importoString.charAt(0)=='-' ? 1:0); i < importoString.length ; i++){ 
			Char = importoString.charAt(i); 
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
    function _formattaImporto(importo) {
    	var importoFormattato = "";
    	if (importo != null) {
        	var field = $('<p/>',{text: importo});
        	field.formatCurrency({decimalSymbol: ",", digitGroupSymbol : ".", symbol: ""});
        	importoFormattato = field.text();
    	}
    	return importoFormattato;
    }
	
    /*
     * Definizione proprieta' della maschera per visualizzare l'albero delle categorie.
     */
    $( "#mascheraAlberoCategorie" ).dialog({
    	autoOpen: false,
    	width: 700,
    	show: {
    		effect: "blind",
    		duration: 500
        },
        hide: {
        	effect: "blind",
        	duration: 500
        },
		modal: true,
        resizable: true,
		focusCleanup: true,
		cache: false,
        buttons: {
        	"Conferma" : function() {
            	_conferma(); 
            },
        	"Annulla" : function() {
            	$(this).dialog( "close" );
            }
          }
    });
    
    /*
     * Se � stata selezionata una categoria dall'albero, si riportano il codice e la descrizione nei campi della finestra chiamante.
     */
    function _conferma(){
    	var nodoCorrente = $('#categorietree').jstree("get_selected");
    	if(nodoCorrente!=null){
    		var tipoNodo = nodoCorrente.attr("tiponodo");
        	if(tipoNodo=="C" || tipoNodo=="CISARCHI" || tipoNodo== "CA" || tipoNodo== "CAISARCHI"){
        		var caisim = nodoCorrente.attr("caisim");
            	var descrizione = nodoCorrente.attr("descrizione");
            	$("#Campo8").val(caisim) ;
            	$("#Campo9").val(descrizione) ;
            	$("#mascheraAlberoCategorie").dialog( "close" );
        	}else{
        		alert("Non e'stata selezionata una categoria");
        	}
    	}else{
    		alert("Non e'stata selezionata una categoria");
    	}
    }
    
    /*
     * Funzione per riportare il codice e la descrizione della categoria selezionea nei relativi campi
     * della finestra di ricerca. Funzione eseguita dal menu'.
     */
    function _selezionaCategoria(node) {
    	var caisim = node.attr("caisim");
    	var descrizione = node.attr("descrizione");
    	$("#Campo8").val(caisim) ;
    	$("#Campo9").val(descrizione) ;
    	$("#mascheraAlberoCategorie").dialog( "close" );
    }
});
