$(window).ready(function (){
	$('#QFORMLIB_GENERE').on('change', function () {
		var genere = $(this).val();
		if(genere != 1){
			$('#rowQFORMLIB_BUSTA').hide();
			$('#rowQFORMLIB_DAIMPORTO').hide();
			$('#rowQFORMLIB_AIMPORTO').hide();
			$("#QFORMLIB_BUSTA option").eq(0).prop('selected', true);
			$('#QFORMLIB_DAIMPORTO').val("");
			$('#QFORMLIB_AIMPORTO').val("");
		}else {
			$('#rowQFORMLIB_BUSTA').show();
			$('#rowQFORMLIB_DAIMPORTO').show();
			$('#rowQFORMLIB_AIMPORTO').show();
		}
    });
    
    var genere = getValue("QFORMLIB_GENERE");
    if(genere != 1){
    	showObj("rowQFORMLIB_BUSTA",false);
		showObj('rowQFORMLIB_DAIMPORTO',false);
		showObj('rowQFORMLIB_AIMPORTO',false);
	}
        
    /*
    $('QFORMLIB_DAIMPORTO').on('focusin', function(){
	    $(this).data('val', $(this).val());
	});
	
	$('QFORMLIB_AIMPORTO').on('focusin', function(){
	    $(this).data('val', $(this).val());
	});
    
    */
    
    $('#QFORMLIB_DAIMPORTO').on('change', function () {
		var valInf = $(this).val();
		var valInfPrec = $(this).data('val');
		var valSup =  $('#QFORMLIB_AIMPORTO').val();
		if(valInf == null || valInf == "")
			valInf = null;
		if(valSup == null || valSup == "")
			valSup = null;
		if(valInf!=null && valSup!=null){
			if(valInf > valSup){
				alert("il valore 'Da importo' deve essere minore di 'A importo'");
				$(this).val(valInfPrec);
			}
		}
    });
    
    
    $('#QFORMLIB_AIMPORTO').on('change', function () {
		var valSup = $(this).val();
		var valSupPrec = $(this).data('val');
		var valInf =  $('#QFORMLIB_DAIMPORTO').val();
		if(valInf == null || valInf == "")
			valInf = null;
		if(valSup == null || valSup == "")
			valSup = null;
		if(valInf!=null && valSup!=null){
			if(valInf > valSup){
				alert("il valore 'Da importo' deve essere minore di 'A importo'");
				$(this).val(valSupPrec);
			}
		}
    });
    
    
    
});

function schedaConfermaCustom(){
	clearMsg();
	var busta = $('#QFORMLIB_BUSTA').val();
	var genere = $('#QFORMLIB_GENERE').val();
	var tipologia = $('#QFORMLIB_TIPOLOGIA').val();
	if(genere==1){
		if(tipologia==4 && busta != 3){
			outMsg("Il q-form di tipo 'Automazione documenti richiesti e offerta economica' puo' essere associato solo alla busta 'Offerta economica'", "ERR");
			onOffMsg();
			return;
		}else if(tipologia==1 && busta == 3){
			outMsg("Il q-form di tipo 'Automazione documenti richiesti' non puo' essere associato alla busta 'Offerta economica'", "ERR");
			onOffMsg();
			return;
		}else if(tipologia==5 && busta != 1 && busta != 4){
			outMsg("Il q-form di tipo 'Automazione documenti richiesti con integrazione M-DGUE' non puo' essere associato alla busta 'Offerta economica' e 'Offerta tecnica'", "ERR");
			onOffMsg();
			return;
		}
	}else{
		if(tipologia==5 ){
			outMsg("Il q-form di tipo 'Automazione documenti richiesti con integrazione M-DGUE' puo' essere associato solo al formulario per 'Gare'", "ERR");
			onOffMsg();
			return;
		}
		if(tipologia==4 ){
			outMsg("Il q-form di tipo 'Automazione documenti richiesti e offerta economica' puo' essere associato solo al formulario per 'Gare'", "ERR");
			onOffMsg();
			return;
		}
	}
	var valSup =  $('#QFORMLIB_AIMPORTO').val();
	var valInf =  $('#QFORMLIB_DAIMPORTO').val();
	if(valInf == null || valInf == "")
		valInf = null;
	if(valSup == null || valSup == "")
		valSup = null;
	if(valInf!=null && valSup!=null){
		if(valInf > valSup){
			alert("il valore 'Da importo' deve essere minore di 'A importo'");
			return;
		}
	}
	schedaConfermaDefault();
}		

var schedaConfermaDefault = schedaConferma;
var schedaConferma = schedaConfermaCustom;	

function listaGare(id){
 	var href = contextPath + "/ApriPagina.do?"+csrfToken + "&href=geneweb/qformlib/listaGare.jsp&id=" + id;
 	document.location.href = href;
}
