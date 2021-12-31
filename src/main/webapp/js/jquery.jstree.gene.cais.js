/*	
 * Albero delle Categorie (CAIS).
 * Operazioni disponibili:
 * 
 * load - caricamento dei dati
 * search - ricerca dei dati
 * delete - cancellazione di una categoria
 * 
 * L'operazione load restituisce un oggetto JSONArray strutturato nel seguente
 * modo:
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
 * 11 - Categoria archiviata ? (boolean, default FALSE)
 * 12 - Categoria utilizzata ? (boolean, default FALSE)
 * 
 */


$(window).on("load", function (){
	
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
  
    
    /*
     * Selezione del nodo: apro il menu' contestuale gia' disponibile
     * mediante il tasto destro del mouse.
     */
    $('#categorietree').bind('select_node.jstree', function(e,data) { 
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
	});

    
	/*
	 * Ad ogni apertura di un nodo evidenzia la stringa cercata.
	 */
	$("#categorietree").bind("open_node.jstree", function (e, data) {
		if ($("#textsearch").val() != null && $("#textsearch").val() != "")
		{
			var words = $("#textsearch").val().split(' ');
			$('#categorietree ul li').highlight(words);
		}
	});	

	$("#categorietree").bind("before.jstree", function (e, data) {
		if ($("#textsearch").val() != null && $("#textsearch").val() != "") {
			searchMessaggio();
		}
	});

	function scrollToSelected() {
		delay(function(){
			var s = $('#categorietree').jstree("get_selected");
			var w = $(window);
		    $('html,body').animate({scrollTop: s.offset().top - (w.height()/2)}, 1000 );
		}, 100);
	};
	
	
	
	/*
	 * Inizializzazione albero.
	 */
    $("#categorietree").jstree(
		{ 
			"core" : {
				"html_titles" : true,
				"animation" : 300
			},
			"plugins" : [ "themes", "json_data", "ui", "types", "search", "cookies", "crrm", "contextmenu"],
			"themes" : { "theme" : "classic", "url" : "css/jquery/jstree/themes/classic/style.css"  },
			"ui" : { "select_limit" : 1	},
			"contextmenu" : { 
				"items" : caisMenu,
				"show_at_node" : false
			},
			"types" : {
				"type_attr" : "tiponodo",
				"types" : {
					"R" : {
						
					},
					"T" : {
						
					},
					"CA" : {
						"icon" : {"image" : "img/categoria_rosso.gif"}
					},
					"C" : {
						"icon" : {"image" : "img/categoria_verde.gif"}
					},
					"CISARCHI" : {
						"icon" : {"image" : "img/categoria_grigio.gif"}
					}
				}
			},
			"search" : {
				"case_insensitive" : true,
				"show_only_matches" : false,
				"search_method" : "jstree_contains_any",
				"ajax" : {
					async: true,
					url: "pg/GestioneArchivioCategorieAlbero.do",
	                data : function (n) { 
						return {
							operation: "search",
							textsearch: $("#textsearch").val()
						};	
					},
					success: function( data ) {
						$("#attesa").show();
	                	SearchCategorieArray = [];
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
	                url: "pg/GestioneArchivioCategorieAlbero.do",
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
							caisim_livello4 : n.attr ? n.attr("caisim_livello4") : ""
						};	
					},
	                success: function( data ) {
	                	$("#attesa").show();
	                	CategorieArray = [];
	                	if (data && data.length > 0) {
							$.map( data, function( item ) {
								
								/*
								 * Identificativo del nodo del nodo
								 * R - Root
								 * T - Titolo
								 * C - Categoria
								 * 
								 */
								var nodeid = "";
								if (item[3] != null && item[3] != "") {
									nodeid += "C_" + item[3] + "_";
								}
								if (item[2] != null && item[2] != "") {
									nodeid += "T_" + item[2] + "_";
								}
								nodeid += "R_" + item[1];
							
								/*
								 * Descrizione del nodo. 
								 */
								var descrizione = "";
								if (item[11] == true) {
									descrizione += "[ARCHIVIATA] ";
								}
								if (item[0] != "-1" && item[0] != "0") {
									descrizione += item[3].replace(/ /g, '\u00a0') + " - ";
								}
								if (item[8] != null) {
									descrizione += item[8];
								}
								var descrizione_tooltip = '<span title="' + descrizione + '">';

								/*
								 * Riduco la dimensione della descrizione se troppo lunga.
								 */
								if (descrizione.length > lunghezzamassimadescrizione) {
									descrizione = descrizione.substring(0,lunghezzamassimadescrizione) + "...";
								}
								descrizione_tooltip += descrizione;
								
								/*
								 * Tipo di nodo (corrispondente alla sezione types)
								 */
								var tiponodo = "";
								if (item[0] == '-1') {
									tiponodo = "R";
								} else if (item[0] == '0') {
									tiponodo = "T";
								} else {
									if (item[12] == true) {
										tiponodo = "CA";
									} else if (item[11] == false) {
										tiponodo = "C";
									} else {
										tiponodo = "CISARCHI";
									}
								}
								
								/*
								 * Stato della categoria. Se il livello e' l'ultimo 
								 * (non ci sono elementi annidati o associati) lo stato
								 * non deve essere closed ma indefinito in modo da non 
								 * visualizzare il simbolo "+" per l'apertura del nodo
								 */
								var statoNodo = "closed";
								if (item[9] == "0" && item[10] == "0") {
									statoNodo = "";
								}
								
								CategoriaItem = {
									"data" : descrizione_tooltip,	
									"attr" : {
										"tiponodo" : tiponodo,
										"livello" : item[0],	
										"id" : nodeid,
										"tiplavg" : item[1],
										"titcat" : item[2] == null ? "" : item[2],
										"caisim" : item[3],
										"caisim_livello1" : item[4],
										"caisim_livello2" : item[5],
										"caisim_livello3" : item[6],
										"caisim_livello4" : item[7],
										"descrizione" : item[8],
										"numcategorienonarch" : item[9],
										"numcategoriearch" : item[10],
										"categoriaarchiviata" : item[11],
										"categoriautilizzata" : item[12]
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
					}
				}
			}
		}
	);
    
    
    /*
     * Menu' personalizzato
     */
    function caisMenu(node) {
    	
        var items = {
        	addItem: {
           		label: "Aggiungi categoria figlia",
           		action: function (obj) { addCaisStandard(node); },
           		_disabled : false
            },
        	visItem: {
        		label: "Visualizza categoria",
        		action: function (obj) { visCaisStandard(node); },
        		_disabled : false,
        		separator_before: true
        	},
        	updItem: {
        		label: "Modifica categoria",
        		action: function (obj) { updCaisStandard(node); },
        		_disabled : false
        	},
        	delItem: {
        		label: "Elimina categoria",
        		action: function (obj) { delCais(node); },
        		_disabled : false
        	}
        };
	        
        /*
         * Gestione menu' aggiungi. 
         * E' possibile aggiungere solo se il livello non e' superiore ad 1 per i 
         * Lavori (tiplavg == 1) oppure non e' superiore a 4 per le altre tipologie.
         */
        if (node.attr("tiplavg") == "1" && Number(node.attr("livello")) >= 1) {
        	items.addItem._disabled = true;
        } else if (Number(node.attr("livello")) > 4) {
        	items.addItem._disabled = true;
        }
        
        /*
         * Controllo categorie archiviate.
         * Se una categoria e' archiviata non e' permesso l'inserimento 
         * di categorie figlie
         */
        if (node.attr("tiponodo") == "CISARCHI") {
        	items.addItem._disabled = true;
        }
        
        /*
         * Gestione dei menu' visualizza, modifica ed elimina
         * I due menu' sono disponibili solo per le categorie.
         * Non devono essere visualizzati per le tipologie root (tiponodo == 'R')
         * e per i titoli (tiponodo == 'T').
         */
        if (node.attr("tiponodo") == "R" || node.attr("tiponodo") == "T") {
        	items.visItem._disabled = true;
        	items.updItem._disabled = true;
        	items.delItem._disabled = true;
        }
        
        /*
         * Se la categoria e' utilizzata (o uno dei suoi figli
         * e' utilizzata) impedisco la cancellazione.
         */
        if (node.attr("tiponodo") == "CA") {
        	items.delItem._disabled = true;
        }
        
        /*
         * Controllo delle protezioni di pagina.
         *  
         */
        if ($("#isAddItemEnabled").val() == 'false') {
        	items.addItem._disabled = true;
        }	
        if ($("#isVisItemEnabled").val() == 'false') {
        	items.visItem._disabled = true;
        }
        if ($("#isUpdItemEnabled").val() == 'false') {
        	items.updItem._disabled = true;
        }
        if ($("#isDelItemEnabled").val() == 'false') {
        	items.delItem._disabled = true;
        }
        
        
        return items;
    };
    
    
    /*
     * Visualizzazione della categoria mediante form standard
     */
    function visCaisStandard(node) {
    	var caisim = node.attr('caisim');
    	document.formCategoria.key.value="CAIS.CAISIM=T:" + caisim;
    	document.formCategoria.modo.value = "VISUALIZZA";
    	bloccaRichiesteServer();
		document.formCategoria.submit();
    };
    
    /*
     * Inserimento di una nuova categoria mediante form standard.
     */
    function addCaisStandard(parent) {
    	var tiplavg = parent.attr('tiplavg');
    	var titcat = parent.attr('titcat');
    	var codliv1 = parent.attr('caisim_livello1');
    	var codliv2 = parent.attr('caisim_livello2');
    	var codliv3 = parent.attr('caisim_livello3');
    	var codliv4 = parent.attr('caisim_livello4');    
    	
		document.formCategoria.tiplavg.value = tiplavg;
		document.formCategoria.titcat.value = titcat;
		document.formCategoria.codliv1.value = codliv1;
		document.formCategoria.codliv2.value = codliv2;
		document.formCategoria.codliv3.value = codliv3;
		document.formCategoria.codliv4.value = codliv4;
		document.formCategoria.modo.value = "NUOVO";
		bloccaRichiesteServer();
		document.formCategoria.submit();
    };
    
    
    /*
     * Modifica di una categoria esistente mediante maschera classica.
     */
    function updCaisStandard(node) {
    	var caisim = node.attr('caisim');
    	document.formCategoria.key.value="CAIS.CAISIM=T:" + caisim;
    	document.formCategoria.modo.value = "MODIFICA";
    	bloccaRichiesteServer();
		document.formCategoria.submit();
    };
	
    /*
     * Elimina la categoria selezionata e le sue sottocategorie.
     */
    function delCais(node) {
    	$( "#dialog-delCais" ).dialog({
    		dialogClass: "no-close",
    		resizable: false,
    		height:140,
    		width: 450,
    		modal: true,
    		buttons: {
    			"Elimina": function() {
    				_delCais(node);
    				$(this).dialog( "close" );
    			},
    			"Annulla": function() {
    				$(this).dialog( "close" );
    			}
    		}
    	});
    };
    
    /*
     * Cancellazione della categoria e di tutte le sue figlie.
     */
	function _delCais(node) {
		var caisim = node.attr('caisim');
		var parent = $.jstree._reference('#categorietree')._get_parent(node);
        $.ajax({
            type: "GET",
            dataType: "json",
            async: false,
            beforeSend: function(x) {
			if(x && x.overrideMimeType) {
    			x.overrideMimeType("application/json;charset=UTF-8");
		       }
			},
            url: "pg/GestioneArchivioCategorieAlbero.do",
            data: "operation=delete&caisim=" + caisim,
            success: function(data){
            	$("#categorietree").jstree("close_node",parent);
            	$("#categorietree").jstree("refresh",parent);
            	$("#categorietree").jstree("open_node",parent);
            },
            error: function(e){
                alert("Errore durante l'eliminazione della categoria selezionata.");
            }
        });
    };

 
    
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
	};
	
	
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
		$('#categorietree ul li').highlight(words);

		if ($("#textsearch").val() == null || $("#textsearch").val() == "") {
			$("#messaggioricerca").html("");
			$("#attesa").hide();
		}
	};
     
    
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
			if (word != "" && word != " ") {
				var descrizioneestesa = a.parentNode.attributes.descrizione.value;
				if (((a.textContent || a.innerText || "").toLowerCase().indexOf(word) >= 0) || 
				    ((descrizioneestesa || "").toLowerCase().indexOf(word) >= 0)) {
					return true;
				}
			}
		}
		return false;
	};
	
});
