/*
 * Gestione importazione progetto dalle viste condivise.
 * 
 */

$(window).on("load", function () {
	
	_getTipoWSERP();

	
	// Numero massimo di atttributi da visualizzare
	var maxFORN = 20;
	
	$("table.grigliaforn tr td.valore-dato").removeClass('valore-dato');
	
	$('div.legenda').hide();
	
	if(_tipoWSERP == 'AVM' || _tipoWSERP == 'TPER' || _tipoWSERP == 'CAV' || _tipoWSERP == 'RAIWAY'){
		caricaAttributiFornitore();	
	}

	

	function caricaAttributiFornitore() {
		var ditta = $("#GARE_DITTA").val();
		var ngara = $("#GARE_NGARA").val();
		
        if (ditta != "") { 
        	$.ajax({
                type: "GET",
                dataType: "json",
                async: false,
                beforeSend: function(x) {
    			if(x && x.overrideMimeType) {
        			x.overrideMimeType("application/json;charset=UTF-8");
			       }
				},
                url: "pg/GetWSERPAttributiFornitore.do",
                data: "ditta=" + ditta + "&ngara=" + ngara,
                success: function(data){
                	if (data) {
                		
                		// Gestione dei CIG non ancora importati
                		var maxATTR = 16;
                		var iNextNew = 0;
                		
						$.map( data, function( item ) {

							if (iNextNew < maxATTR) {
									if(iNextNew==0 && (_tipoWSERP == 'TPER' || _tipoWSERP == 'CAV' || _tipoWSERP == 'RAIWAY') ){
										$("#idFornitore").val(item[1]);
									}
									if(iNextNew==0 && _tipoWSERP == 'AVM'){
										$("#idFornitore").val(item[2]);
									}else{
										$("#rowIMPR_DESCRIZIONE_" + iNextNew).show();
										$("#IMPR_DESCRIZIONE_" + iNextNew).text(item[0]);
										$("#rowIMPR_AC_DESCRIZIONE_" + iNextNew).show();
										$("#IMPR_AC_DESCRIZIONE_" + iNextNew).text(item[1]);
										$("#rowIMPR_SAP_DESCRIZIONE_" + iNextNew).show();
										$("#IMPR_SAP_DESCRIZIONE_" + iNextNew).text(item[2]);
										if(_tipoWSERP == 'AVM' && iNextNew > 1 && item[1]!=item[2]){
											$("#IMPR_SAP_DESCRIZIONE_" + iNextNew).css("color", "red")
										}
									}
									
									iNextNew++;
								
							}
						});
						
						if($("#idFornitore").val() != null && $("#idFornitore").val() != ""){
							$("#parCreaFornitore").hide();
						}

												
                		
                	}
                },
                error: function(e) {
                    alert("Errore durante la lettura della lista degli attributi del fornitore");
                }
            });
        }
	}
	
	var delay = (function(){
		  var timer = 0;
		  return function(callback, ms){
		    clearTimeout (timer);
		    timer = setTimeout(callback, ms);
		  };
	})();
	
	
});



