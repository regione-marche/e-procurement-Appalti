/**	
 * Ridefinizione per titoli, campi e tooltip 
 */

	
	
	function redefineLabels() {
		 $.each(arrayLabels,function( index, value ) {
			var labels = $("[id='row" + value.entita + "_" + value.campo + "'] td.etichetta-dato");
			if (labels.size() > 0) {
				if (value.search == "") {
					labels.html(value.replace);
				} else {
					labels.html(labels.html().replace(value.search, value.replace));
				}
			}else{
				labels = $("[id^='row" + value.entita + "_" + value.campo + "_'] td.etichetta-dato");
				if (value.search == "") {
					labels.html(value.replace);
				} else {
					labels.html(labels.html().replace(value.search, value.replace));
				}
			}
			
		 });
	};
	
	function redefineLabelsFormRicerca(tipo) {
		var array;
		if(tipo=="Autovie"){
			array = arrayLabelsFormRicercaAutovie;
		}else if(tipo=="RDO"){
			array = arrayLabelsFormRicercaRDO;
		}
		
		$.each(array,function( index, value ) {
			var labels = $("[id='row" + value.campo + "'] td.etichetta-dato");
			if (labels.size() > 0) {
				if (value.search == "") {
					labels.html(value.replace);
				} else {
					labels.html(labels.html().replace(value.search, value.replace));
				}
			}
			
		 });
	};
	
	function redefineTooltips() {
		 $.each(arrayTooltips,function( index, value ) {
			var tooltip = $("[id='" + value.entita + "_" + value.campo + "']").attr ("title"); 
			if(tooltip!=null){
				$("[id='" + value.entita + "_" + value.campo + "']").attr ("title",value.replace);
			}
			tooltip = $("[id^='" + value.entita + "_" + value.campo + "']").attr ("title");
			if(tooltip!=null){
				$("[id^='" + value.entita + "_" + value.campo + "']").attr ("title",value.replace);
			}
			
		 });
	};
	
	function redefineTooltipsFromRicerca(tipo) {
		var array;
		if(tipo=="Autovie"){
			array = arrayTooltipsFormRicercaAutovie;
		}else if(tipo=="RDO"){
			array = arrayTooltipsFormRicercaRDO;
		} 
		$.each(array,function( index, value ) {
			var tooltip = $("[id='" + value.campo + "']").attr ("title"); 
			if(tooltip!=null){
				$("[id='" + value.campo + "']").attr ("title",value.replace);
			}
			
		 });
	};
	
	
	
	function redefineTitlesFromRicerca(tipo) {
		var array;
		if(tipo=="Autovie"){
			array = arrayTitlesFormRicercaAutovie;
		}else if(tipo=="RDO"){
			array = arrayTitlesFormRicercaRDO;
		} 
		$.each(array,function( index, value ) {
			var titles = $("[id='row" + value.titolo + "'] td b");
			if (titles.size() > 0) {
				if (value.search == "") {
					titles.html(value.replace);
				} else {
					titles.html(titles.html().replace(value.search, value.replace));
				}
			}
		 });
	};
	
	
	function redefineTitles() {
		 $.each(arrayTitles,function( index, value ) {
			var titles = $("[id='row" + value.titolo + "'] td b");
			if (titles.size() > 0) {
				if (value.search == "") {
					titles.html(value.replace);
				} else {
					titles.html(titles.html().replace(value.search, value.replace));
				}
			}
		 });
	};
	
	function redefineLablesSezioniDinamiche(contatore){
		
		for(i=1;i<=contatore;i++){
				//Modifica dei titoli 
				$.each(arrayTitlesSezioniDinamiche,function( index, value ) {
					var labels = $("[id='row" + value.campo + i + "'] td table tr td b");
					if (labels.size() > 0) {
						if (value.search == "") {
							labels.html(value.replace);
						} else {
							labels.html(labels.html().replace(value.search, value.replace));
						}
					}
					
				 });
				//Modifica dell'elimina
				$.each(arrayDelSezioniDinamiche,function( index, value ) {
					var labels = $("[id='" + value.campo + i + "']");
					if (labels.size() > 0) {
						if (value.search == "") {
							labels.html(value.replace);
						} else {
							//Poich� per il pulsante di elimina ci sono nella pagina html pi� punti in cui �
							//presente il testo da sostituire e la funzione replace effettua la sostituzione solo
							//sulla prima corrispondenza trovata, ho definito la funzione replaceAll
							labels.html(replaceAll(value.search, value.replace,labels.html()));
						}
					}
					
				 });
		 }
		 
		 //Modifica dell'aggiungi
		 $.each(arrayAddSezioniDinamiche,function( index, value ) {
				//var labels = $("[id='row" + value.entita + "_" + value.campo + "'] td.etichetta-dato");
			 var labels = $("[id='row" + value.campo + "'] td.valore-dato");
				if (labels.size() > 0) {
					if (value.search == "") {
						labels.html(value.replace);
					} else {
						//Poich� per il pulsante di aggiungi ci sono nella pagina html pi� punti in cui �
						//presente il testo da sostituire e la funzione replace effettua la sostituzione solo
						//sulla prima corrispondenza trovata, ho definito la funzione replaceAll
						labels.html(replaceAll(value.search, value.replace,labels.html()));
					}
				}
				
			 });
		
		 //Messaggio raggiunto numero massimo di sezioni da inserire
		 $.each(arrayRaggiuntoMAxSezioniDinamiche,function( index, value ) {
				var titles = $("[id='" + value.campo + "'] td.valore-dato");
				if (titles.size() > 0) {
					if (value.search == "") {
						titles.html(value.replace);
					} else {
						titles.html(titles.html().replace(value.search, value.replace));
					}
				}
			 });
	};
	
	function replaceAll(find, replace, str) {
	        return str.replace(new RegExp(find, 'g'), replace);
	}
	
	var arrayLabels = 
		[
		 {"entita" : "TORN",     "campo" : "NPROTI",       "search" : "",     	        "replace" : "Protocollo lettera di invito"},
		 {"entita" : "DITG",     "campo" : "NPROTG",       "search" : "",     	        "replace" : "Protocollo lettera di invito"},
		 {"entita" : "GARE",     "campo" : "NREPAT",       "search" : "",           	"replace" : "Numero ordine"},
		 {"entita" : "GARE",     "campo" : "DAATTO",       "search" : "",           	"replace" : "Data"},
		 {"entita" : "GARECONT", "campo" : "NPROAT",       "search" : "",           	"replace" : "Numero protocollo"},
		 {"entita" : "GARECONT", "campo" : "DATRES",       "search" : "",           	"replace" : "Data restituzione per accettazione"},
		 {"entita" : "GARE",     "campo" : "DRICHDOCCR",   "search" : "",           	"replace" : "Data richiesta documentazione"},
		 {"entita" : "GARE",     "campo" : "NPROREQ",      "search" : "",           	"replace" : "Num.prot.richiesta documentazione"},
		 {"entita" : "GARCOMPREQ",     "campo" : "DRICREQ",      "search" : "",           	"replace" : "Data ricezione"},
		 {"entita" : "GARCOMPREQ",     "campo" : "NRICREQ",      "search" : "",           	"replace" : "Numero protocollo"},
		 {"entita" : "GARSED",   "campo" : "DATVERB",      "search" : "",           	"replace" : "Data protocollo verbale"},
		 {"entita" : "GARSED",   "campo" : "NUMVERB",      "search" : "",           	"replace" : "Protocollo verbale"},
		 {"entita" : "GARE",     "campo" : "DCOMAG",       "search" : "",           	"replace" : "Data prot. comunicazione ditta aggiudicataria"},
		 {"entita" : "GARE",     "campo" : "DCOMNG",       "search" : "",           	"replace" : "Data prot. comunicazione ditte non aggiudicatarie"}
		];
	
	var arrayTooltips = 
		[
		 {"entita" : "TORN",     "campo" : "NPROTI",       "search" : "",     	        "replace" : "Protocollo lettera di invito"},
		 {"entita" : "DITG",     "campo" : "NPROTG",       "search" : "",     	        "replace" : "Protocollo lettera di invito"},
		 {"entita" : "GARE",     "campo" : "NREPAT",       "search" : "",           	"replace" : "Numero ordine"},
		 {"entita" : "GARE",     "campo" : "DAATTO",       "search" : "",           	"replace" : "Data ordine"},
		 {"entita" : "GARECONT", "campo" : "NPROAT",       "search" : "",           	"replace" : "Numero protocollo ordine"},
		 {"entita" : "GARECONT", "campo" : "DATRES",       "search" : "",           	"replace" : "Data restituzione per accettazione ordine"},
		 {"entita" : "GARE",     "campo" : "DRICHDOCCR",   "search" : "",           	"replace" : "Data richiesta documentazione per qualifica"},
		 {"entita" : "GARE",     "campo" : "NPROREQ",      "search" : "",           	"replace" : "Numero protocollo richiesta document. per qualifica"},
		 {"entita" : "GARCOMPREQ",     "campo" : "DRICREQ",      "search" : "",           	"replace" : "Data ricezione documentazione per qualifica"},
		 {"entita" : "GARCOMPREQ",     "campo" : "NRICREQ",      "search" : "",           	"replace" : "Numero protocollo ricezione document.per qualifica"},
		 {"entita" : "GARSED",   "campo" : "DATVERB",      "search" : "",           	"replace" : "Data protocollo verbale"},
		 {"entita" : "GARSED",   "campo" : "NUMVERB",      "search" : "",           	"replace" : "Protocollo verbale"},
		 {"entita" : "GARE",     "campo" : "DCOMAG",       "search" : "",           	"replace" : "Data prot. comunicazione ditta aggiudicataria"},
		 {"entita" : "GARE",     "campo" : "DCOMNG",       "search" : "",           	"replace" : "Data prot. comunicazione ditte non aggiudicatarie"}
		];
	
	var arrayTitles = 
		[
		 {"titolo" : "TITOLO_ATTO_CONTRATTUALE",      "search" : "",    "replace" : "Ordine"},
		 {"titolo" : "TITOLO_RICHIESTA_DOC",          "search" : "",    "replace" : "Richiesta documentazione per qualifica"},
		 {"titolo" : "TITOLO_RICEZIONE_DOC",          "search" : "",    "replace" : "Ricezione documentazione per qualifica"},
		 {"titolo" : "TITOLO_ATTO_AGGIUDICAZIONE",     "search" : "",    "replace" : "Provvedimento di aggiudicazione"}
		];
	

	var arrayLabelsFormRicercaAutovie = 
		[
		 {"campo" : "GARE_NREPAT",      	"search" : "",      "replace" : "Numero ordine"},
		 {"campo" : "GARE_DAATTO",      	"search" : "",      "replace" : "Data ordine"},
		 {"campo" : "GARECONT_NPROAT",  	"search" : "",      "replace" : "Numero protocollo ordine"},
		 {"campo" : "GARECONT_DATRES",  	"search" : "",      "replace" : "Data restituzione per accettazione ordine"}
		];

	
	var arrayTooltipsFormRicercaAutovie = 
		[
		 {"campo" : "GARE_NREPAT", 		    "search" : "",      "replace" : "Numero ordine"},
		 {"campo" : "GARE_DAATTO",      	"search" : "",      "replace" : "Data ordine"},
		 {"campo" : "GARECONT_NPROAT",  	"search" : "",      "replace" : "Numero protocollo ordine"},
		 {"campo" : "GARECONT_DATRES",  	"search" : "",      "replace" : "Data restituzione per accettazione ordine"}
		];
	
	var arrayTitlesFormRicercaAutovie = 
		[
		 {"titolo" : "TITOLO_CONTRATTO",      "search" : "",    "replace" : "Ordine"}
		];
	

	var arrayLabelsFormRicercaRDO = 
		[
		 {"campo" : "GARE_NREPAT",      "search" : "",      "replace" : "Num.identificativo ordine di acquisto"},
		 {"campo" : "GARE_DAATTO",      "search" : "",      "replace" : "Data ordine di acquisto"},
		 {"campo" : "GARECONT_NPROAT",  "search" : "",      "replace" : "Num.protocollo ordine di acquisto"}
		];

	
	var arrayTooltipsFormRicercaRDO = 
		[
		 {"campo" : "GARE_NREPAT",      "search" : "",      "replace" : "Num.identificativo ordine di acquisto"},
		 {"campo" : "GARE_DAATTO",      "search" : "",      "replace" : "Data ordine di acquisto"},
		 {"campo" : "GARECONT_NPROAT",  "search" : "",      "replace" : "Num.protocollo ordine di acquisto"}
		];
	
	var arrayTitlesFormRicercaRDO = 
		[
		 {"titolo" : "TITOLO_CONTRATTO",      "search" : "",    "replace" : "Ordine di acquisto"}
		];
	
	var arrayTitlesSezioniDinamiche = 
		[
		];
	
	var arrayAddSezioniDinamiche = 
		[
		];
	
	var arrayDelSezioniDinamiche = 
		[
		];
	
	var arrayRaggiuntoMAxSezioniDinamiche = 
		[
		];
	