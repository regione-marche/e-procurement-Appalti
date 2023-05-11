/*
*	Funzioni di supporto per la gestione
*	della pagina popupDettaglioValutazioneTecEco.jsp.
*
*/

var codiceGara;
var gara ;
var ditta;
var _tableDettaglioValutazione = null;
var tipoDettaglio;
var riptec;
var ripeco;
var ripcritec;
var ripcrieco;
var tabA1z07;
//var formatoCoeff=1.9;
var numCifrePunteg;
var percorsoContesto;
var saltareControlloSogliaMinima;
var profiloCommissioneAttivato = false;
var profiloCommissione;
var commissarioAttivo;

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
 * Legge il dettaglio di valutazione (tecnica o economica) per la ditta 
 * della gara da cui stata lanciata l'apertura della popup.
 */
function _getDettaglio() {
	
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
		url: "pg/GetDettaglioValutazione.do",
		data : "ngara=" +  gara + "&codiceGara=" + codiceGara + "&ditta=" + ditta + "&tipoDettaglio=" + tipoDettaglio,
		success: function(json){
			riptec = json.riptec;
			ripeco = json.ripeco;
			ripcritec = json.ripcritec;
			ripcrieco = json.ripcrieco;
			var tabA1049 = json.tabA1049;
			tabA1z07 = json.tabA1z07;
			numCifrePunteg=parseInt(tabA1049);
			saltareControlloSogliaMinima = json.saltareControlloSogliaMinima;
			_popolaTabellaDettaglioValutazione(json.data);
		},
		error: function(e){
			 alert("Errore durante la lettura del dettaglio di valutazione");
		},
		complete: function() {
			_nowait();
        }
	});
}


/*
 * Popola la lista dei punteggi
 */
function _popolaTabellaDettaglioValutazione(dettagliValutazione) {
	
	if (_tableDettaglioValutazione != null) {
		_tableDettaglioValutazione.destroy(true);
	}
	
	var puntegripVisibile=((riptec==1 || riptec == 2)  && tipoDettaglio == '1' ) || ((ripeco==1 || ripeco == 2)  && tipoDettaglio == '2') ;
	var considerareImportiRiparam=(((riptec==2 && (ripcritec == 2 || ripcritec == 3)) && tipoDettaglio == '1' ) || ((ripeco==2 && (ripcrieco == 2 || ripcrieco == 3))  && tipoDettaglio == '2')) ; 
	
	var sezTec = $("#sezTec").val();
	var visSezTec = (sezTec == '1');
	var _table = $('<table/>', {"id": "dettaglio", "class": "dettaglioValutazione", "cellspacing": "0", "width" : "100%"});
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
	$("#dettaglioValutazionecontainer").append(_table);
	
	_tableDettaglioValutazione = $('#dettaglio').DataTable( {
		"data": dettagliValutazione,
		paging: false,
		"searching": false,
		"scrollX": false,
		"scrollY": false,
		"scrollCollapse": true,
		
		"columnDefs": [
			{
				"data": "norpar",
				"visible": true,
				"targets": [ 0 ],
				"sWidth": "50px",
				"sClass": "aligncenter",
				"sTitle": "N. criterio",
				"orderable": false
			},
			{
				"data": "norpar1",
				"visible": true,
				"sTitle": "N. subcrit.",
				"sWidth": "50px",
				"sClass": "aligncenter",
				"orderable": false,
				"targets": [ 1 ],
				"mRender": function (data, type, full) {
					if(data!=null && type == "display" && data.toString()=="0"){
						return null;
					}else {
						return data;
					}
				}
			},
			{
				"data": "despar",
				"visible": true,
				"targets": [ 2 ],
				"sClass": "alignleft",
				"orderable": false,
				"sTitle": "Descrizione"
			},
			{
				"data": "seztec",
				"visible": visSezTec,
				"targets": [ 3 ],
				"sWidth": "80px",
				"sClass": "aligncenter",
				"sTitle": "Sezione",
				"orderable": false
			},
			{
				"data": "puntegDpun",
				"visible": true,
				"targets": [ 4 ],
				"sClass": "aligncenter",
				"sWidth": "50px",
				"orderable": false,
				"sTitle": "Punteggio assegnato alla ditta",
				"mRender": function (data, type, full) {
					if(data!=null && type == "display" ){
						data=data.toString().replace(".",",");
					}
					return data;
				}
			},
			{
				"data": "puntegrip",
				"visible": puntegripVisibile,
				"targets": [ 5 ],
				"sClass": "aligncenter",
				"sWidth": "50px",
				"orderable": false,
				"sTitle": "Punteggio riparametrato",
				"mRender": function (data, type, full) {
					if(data!=null && type == "display" ){
						data=data.toString().replace(".",",");
					}
					return data;
				}
			},
			{
				"data": null,
				"targets": [ 6 ],
				"orderable": false,
				"sTitle": "",
				"sWidth": "20px",
				"mRender": function (data, type, row) {
					ret="";
					var punteggioConfronto = row.puntegDpun;
					if(considerareImportiRiparam)
						punteggioConfronto = row.puntegrip;
					if(saltareControlloSogliaMinima==false && punteggioConfronto!=null && row.minpun!=null && punteggioConfronto<row.minpun){
						var msg="Punteggio ";
						if(considerareImportiRiparam){
							msg+="riparametrato ";
						}
						msg+="inferiore alla soglia minima";
						ret='<img width="16" height="16" alt= "' + msg + '" title="' + msg + '"  src="' + percorsoContesto + '/img/isquantimod.png"/>';
					}
					if(row.puntegDpun!=null && row.maxpun!=null && row.puntegDpun>row.maxpun){
						ret='<img width="16" height="16" alt= "Punteggio superiore al punteggio massimo" title="Punteggio superiore al punteggio massimo"  src="' + percorsoContesto + '/img/isquantimod.png"/>';
					}
					return ret;
				}
			},
			{
				"data": "maxpun",
				"visible": true,
				"targets": [ 7 ],
				"sClass": "aligncenter",
				"sWidth": "50px",
				"orderable": false,
				"sTitle": "Punteggio max",
				"mRender": function (data, type, full) {
					if(data!=null && type == "display" ){
						data=data.toString().replace(".",",");
					}
					return data;
				}
			},
			{
				"data": "minpun",
				"visible": true,
				"targets": [ 8 ],
				"sClass": "aligncenter",
				"sWidth": "50px",
				"orderable": false,
				"sTitle": "Soglia min.",
				"mRender": function (data, type, full) {
					if(data!=null && type == "display" ){
						data=data.toString().replace(".",",");
					}
					return data;
				} 
			},
			{
				"data": "livpar",
				"visible": false,
				"targets": [ 9 ]
			},
			{
				"data": "idCridef",
				"visible": false,
				"targets": [ 10 ]
			},
			{
				"data": "necvan",
				"visible": false,
				"targets": [ 11 ]
			},
			{
				"data": "modmanu",
				"visible": false,
				"targets": [ 12 ]
			},
		],
        "language": {
			"sEmptyTable":     "Nessun criterio trovato",
			"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ criteri",
			"sInfoEmpty":      "Nessun elemento trovato",
			"sInfoFiltered":   "(su _MAX_ elementi documentali totali)",
			"sInfoPostFix":    "",
			"sInfoThousands":  ",",
			"sLengthMenu":     "Visualizza _MENU_ criteri",
			"sLoadingRecords": "",
			"sProcessing":     "Elaborazione...",
			"sZeroRecords":    "Nessun elemento trovato"
		},
		complete: function() {
			_nowait();
		},
		"order": [[ 0 , "asc" ]]
    });
	
	
	//Creazione della tabella per la row child del dettaglio
	function format ( d ) {
		
	    var descModpunti= d.descModpunti;
		var descFormula = d.descFormula;
	    var coeffi= d.coeffi;
	    if(coeffi==null)
	    	coeffi = "";
	    else
	    	coeffi=coeffi.toString().replace(".",",");
	    var punteg= d.punteg;
		var note= d.note;
		if(note==null)
	    	note = "";
	    if(punteg==null)
	    	punteg = "";
	    else
	    	punteg=punteg.toString().replace(".",",");
	    var assegnazioneCoeffAbilitata = d.assegnazioneCoeffAbilitata;
		var assegnazioneCoeffAbilitataCommissione = d.assegnazioneCoeffAbilitataCommissione;
		var nascondiPulsanteCoeffiTotale;
		profiloCommissione = d.profiloCommissione;
		if(profiloCommissioneAttivato){
			nascondiPulsanteCoeffiTotale = false;
		}else{
			nascondiPulsanteCoeffiTotale = d.profiloCommissione;
		}
	    var modpunti = d.modpunti;
	    var maxpunCridef = parseFloat(d.maxpunCridef); 
		var formato = d.formato;
		var valstg = d.valstg;
		var valdat = d.valdat;
		var valnum = d.valnum;
		var descValCommissione;
		if((modpunti == 1 || modpunti == 3) && maxpunCridef>0 && d.dataCommissione){
			descValCommissione = getValCommissione(d,assegnazioneCoeffAbilitataCommissione);
		}
		var numdeci = parseInt(d.numdeci);
		if(!numdeci){numdeci = 2;}
		if(valnum != null){
			if(formato == 6 || formato == 51){valnum = valnum.toString().replace(".",",");}
			if(formato == 50 || formato == 52 || formato == 2){
				numdeci = valnum.toString();
				numdeci = numdeci.split('.')[1];
				if(numdeci)numdeci = numdeci.length;
				if(numdeci > 2){valnum = toMoney(valnum,numdeci).view;}
				else{valnum = toMoney(valnum).view;}
				}
			if(formato == 51){valnum = valnum + '%';}
		} else {
			valnum="";
		}
		var formula = d.formula;
		var esponente = d.esponente;
		if(esponente==null)
	    	esponente = "";
	    else
	    	esponente=esponente.toString().replace(".",",");
		
	    var tabella= '<table class="schedaDettaglio" id="coefficiente"">';
		if(valstg == null)
				valstg="";
		if(formato == 3){		
			tabella += '<tr>'+
				        '<td class= "intestazione">Valore offerto</td>'+
				        '<td class= "dato" colspan="3">' + valstg + '</td>'+
				    '</tr>';
		}
		if(formato == 4){
			tabella += '<tr><td class= "intestazione">Valore offerto</td>';
			if(valstg!=""){
				tabella +='<td class= "dato" colspan="2" style="border-style: none;">'
				tabella += '<textarea title="Valore offerto" type="text" cols="80" rows="3" value="" maxlength="2000" style="border-style: none;" disabled="disabled">'+ valstg +'</textarea></td>';
			}
			tabella += '</tr>';
		}
		if(formato == 1){
			if(valdat == null)
				valdat="";
			tabella += '<tr>'+
				        '<td class= "intestazione">Valore offerto</td>'+
				        '<td class= "dato" colspan="3">' + valdat + '</td>'+
				    '</tr>';
		}
		if(formato == 2 || formato == 5 || formato == 6 || formato == 50 || formato == 51 || formato == 52){
			tabella += '<tr>'+
				        '<td class= "intestazione">Valore offerto</td>'+
				        '<td class= "dato" colspan="3"><span>' + valnum + '</span></td>'+
				    '</tr>';
		}
	    tabella += '<tr>'+
				        '<td class= "intestazione">Modalita\' assegnazione punteggio</td>'+
				        '<td class= "dato" colspan="3">' + descModpunti + '</td>'+
				    '</tr>';
		if(modpunti == 2){
		tabella += '<tr>'+
						'<td class= "intestazione">Formula</td>'+
						'<td class= "dato" colspan="3">' + descFormula + '</td>'+
					'</tr>';
		}
		if(formula==11 || formula==13 || formula==14 || formula==15){
			tabella += '<tr>'+
						'<td class= "intestazione">Esponente</td>'+
						'<td class= "dato" colspan="3">' + esponente + '</td>'+
					'</tr>';
		}
		if((modpunti == 1 || modpunti == 3) && maxpunCridef>0 && d.dataCommissione){
		tabella += '<tr>'+
						'<td class= "intestazione" style="vertical-align: middle;">Valutazione commissione</td>'+
						'<td class= "dato" id="valCommissione" colspan="3">'+descValCommissione+'</td>'+
					'</tr>';
		}		
	    tabella += '<tr>'+
				        '<td class= "intestazione">Coefficiente</td>'+
				        '<td class= "datoCoefficente"  width="40%">' + coeffi + '</td>';
	    tabella +=      '<td class= "linkCoefficente" width="20%">';
	    if(!nascondiPulsanteCoeffiTotale && assegnazioneCoeffAbilitata && (modpunti == 1 || modpunti == 3) && maxpunCridef>0){
	    	tabella += '<input class="bottone-azione" type="button" value="Assegna coefficiente" id="btnCoefficente">';
	    }
	    tabella += '</td>' +
	    '<td class= "colonnaDatiNascosti" width="20%"><span id="spanidCridef">' + d.idCridef + '</span><span id="spannecvan">' + d.necvan + '</span><span id="spanmodmanu">' + d.modmanu +  '</span><span id="spannorpar">' + d.norpar + '</span><span id="spannorpar1">' + d.norpar1 + '</span><span id="spanpunteg">' + punteg + '</span><span id="spanmaxpunCridef">' + d.maxpunCridef + '</span><span id="spanidCrival">' + d.idCrival + '</span><span id="spancoeffi">' + coeffi + '</span><span id="spannote">' + note + '</span></td>'
				    '</tr>' ;
	    tabella += '<tr>'+
				        '<td class= "intestazione" style="vertical-align: middle;">Punteggio</td>'+
				        '<td class= "dato" colspan="3">' + punteg + '</td>'+
				    '</tr>';
		if((modpunti == 1 || modpunti == 3) && maxpunCridef>0){
			tabella += '<tr>'+
							'<td class= "intestazione" style="vertical-align: middle;">Note</td>';
							if(note!=""){
								tabella +='<td class= "dato" colspan="2" style="border-style: none;">'
								tabella += '<textarea title="Note" type="text" cols="80" rows="3" value="" maxlength="2000" style="border-style: none;" disabled="disabled">'+ note +'</textarea></td>';
							}
			tabella += '</tr>'
		}
		tabella += '</table>';
	    
	    return tabella;
	} 
	
	
	function getValCommissione(d,assegnazioneCoeffAbilitataComm){
		var data = d.dataCommissione;
		var tabella;
		
		tabella= '<table class="schedaDettaglio" style="border-style: none;">';
		tabella+='<tr style="border-style: none;"><td style="border-style: none;">'
		for(i=0;i<data.length;i++){
			var dataRow = data[i];
			var coeffi = dataRow.coeffi;
			var note = dataRow.note;
			if(note==null)
	    	note = "";
			if(coeffi==null){
				coeffi = "";
			}else{
				coeffi=coeffi.toString().replace(".",",");
			}
			tabella += '<table class="schedaDettaglio" style="border-style: none;">';
			tabella += '<tr id="riga_componente_comm_'+i+'">'+
				'<td class= "nome-commissario" style="width: 60%;border-style: none;" >' + dataRow.nomfof + '</td>'+
				'<td class= "dato" style="border-style: none;">Coefficiente:   <span id="coeffi">' + coeffi+ '</span><span id="idcrivalcom" style="display:none">' + dataRow.idcrivalcom+ '</span><span id="codfof" style="display:none">' + dataRow.codfof+ '</span></td></tr>'
				if (note != ""){
					tabella +='<tr id="riga_componente_comm_note_'+i+'"><td class= "dato" style="border-style: none;" colspan="2" >Note:   <br>';
					tabella += '<textarea title="Note" id="note" type="text" cols="50" rows="3" value="" maxlength="2000" style="border-style: none;" disabled="disabled" >'+ note +'</textarea></td>';
					tabella += '</td>'+
					'</tr>';
				}
			tabella += "</table>"
		}
		tabella+='</td><td style="border-style: none;vertical-align: middle;">'
		if(assegnazioneCoeffAbilitataComm){
			tabella += '<input class="bottone-azione" type="button" style="float: right;margin: 6px;margin-right: 14px;" value="Assegna coefficiente" id="btnCoefficenteCommissione">';
			tabella += '<input type="hidden" id="commissione-length" value="'+data.length+'"/>';
			createTableModal(d);
		}
		
		tabella += '</td></tr></table>';
		return tabella;
		
	}
	
	
	/*
	 * Apertura della scheda modale
	 */
	$('#dettaglio').on('click', '#btnCoefficente', function () {
        var td = $(this).closest('tr').children('td.colonnaDatiNascosti');
		var idCridef = td.find('#spanidCridef').text();
	    var necvan = td.find('#spannecvan').text();
	    var modmanu =  td.find('#spanmodmanu').text();
	    var norpar =  td.find('#spannorpar').text();
	    var norpar1 =  td.find('#spannorpar1').text();
	    var punteg = td.find('#spanpunteg').text();
	    var maxpun = td.find('#spanmaxpunCridef').text();
		var note = td.find('#spannote').text();
		if(punteg!=null && punteg!="")
			punteg=parseFloat(punteg.replace(',','.'));
		
	    td = $(this).closest('tr').children('td.datoCoefficente');
		var coeffi = td.text();
		coeffi=coeffi.replace(',','.');
		
	    if(modmanu==1){
	    	//$("#Inputcoeff").val(formatNumber(coeffi,formatoCoeff));
	    	$("#Inputcoeff").val(coeffi);
	    	$("#coeffImpostato").val(coeffi);
	    	$("#tdCoeff").show();
	    }else {
    		$("#tdSelectCoeff").show();
    		_popolaTabellatoA1z07(coeffi);
    		$("#coeffImpostato").val(coeffi);
    	}
		
		$("#Inputnote").val(note);
		$("#noteImpostato").val(note);
    	
    	$("#numeroCriterio").text(norpar);
    	if(norpar1!=null && norpar1!=0){
    		$("#numeroSubCriterio").text(norpar1);
    		$("#rigaSubCriterio").show();
    	}else{
    		$("#rigaSubCriterio").hide();
    	}
    	$("#punteggioVal").text(punteg);
    	$("#maxpunG1cridef").val(maxpun);
    	$(".ui-dialog-titlebar").hide();
	   	$("#mascheraSchedaCoefficente").dialog( "option", { idCridef: idCridef, necvan: necvan} );
	   	$("#mascheraSchedaCoefficente").dialog("open");
	});
	
	
	function createTableModal(data){
		var tabella;
		var dataCommissione = data.dataCommissione;
		tabella= '<tbody><tr >'+
						'<td colspan="3">'+
							'<b>Specificare il valore del coefficiente da assegnare al criterio per ogni singolo commissario:</b><br><br>'+
						'</td>'+
					'</tr>'+
					'<tr>'+
						'<td>'+
							'Numero criterio:'+
						'</td>'+
						'<td colpsan="2">'+
							'<span id="numeroCriterio"></span>'+
						'</td>'+
					'</tr>'+
					'<tr id="rigaSubCriterio">'+
						'<td>'+
							'Numero subcriterio: '+
						'</td>'+
						'<td colpsan="2">'+
							'<span id="numeroSubCriterio"></span>'+
						'</td>'+
					'</tr><tr><td></td></tr>';
		commissarioAttivo = data.commissarioAttivo;
		for(i=0;i<dataCommissione.length;i++){
			var dataRow = dataCommissione[i];
			
			var modmanu = data.modmanu;
			tabella += '<tr id="componente_commissione_'+i+'">';
			
			tabella += '<td class= "nome-commissario" style="width: 50%;" ><b>' + dataRow.nomfof + '</b></td>';
			if(!data.profiloCommissione || dataRow.codfof == commissarioAttivo){
				tabella += '<td class="dato" id="tdCoeffComm" style="vertical-align=middle">'+
						'<input type="hidden" name="update_'+i+'" id="update" value="false">'+
						'<input id="InputcoeffComm_'+i+'" name="coeffCommissario_'+i+'" title="Coefficiente" size="20" onchange="onchangeInput(this)" class="textInput" value="" maxlength="18">'+
						'<select id="selectA1z07_'+i+'" name="selectA1z07_'+i+'" onchange="onChangeTabellatoComm(this)" class="tabellato"></select>'+
						'<br>'+
					'</td>';
			}else{
				tabella += '<td class="dato" id="tdCoeffComm" style="vertical-align=middle">'+
				'<input id="InputcoeffComm_'+i+'" name="coeffCommissario_'+i+'" type="hidden">'+
				'<span id="spanCoeffi_'+i+'"></span>'
				'</td>';
			}
			tabella += '<input type="hidden" name="idgfof_'+i+'" id="idgfof" value="'+ dataRow.id +'">'+
			'<input type="hidden" name="nomfof_'+i+'" id="nomfof" value="'+ dataRow.nomfof +'">'+
			'<input type="hidden" id="idcrivalcom_'+i+'" name="idcrivalcom_'+i+'" value=""/>'+
			'</tr>';
			
			if(!data.profiloCommissione || dataRow.codfof == commissarioAttivo){
				tabella += '<tr id="note_componente_commissione_'+i+'">';	
				tabella += '<td colspan="1"> Note </td>';
				tabella += '<td colspan="2" class="dato" id="tdNoteComm" style="vertical-align=middle">'
				tabella += '<textarea id="InputnoteComm_'+i+'" name="noteCommissario_'+i+'" title="Note" type="text" cols="50" rows="4" value="" maxlength="2000" onchange="onchangeInputNote(this,i)"></textarea>'+
						'</td>';
						'</tr>';
			}
			
		}	
		tabella += '<input type="hidden" name="idcridef" id="idcridef" value=""><input type="hidden" name="idcrival" id="idcrival" value=""><input type="hidden" name="necvan" id="necvan" value=""><input name="coeffiTotale" type="hidden" id="coeffiTotale" value=""><input name="length" type="hidden" id="length" value="'+dataCommissione.length+'">';
		tabella += '</tbody>';
		$("#tabModalCoefficenteCommissione").html(tabella);
		
		
	}
	
		/*
	 * Apertura della scheda modale
	 */
	$('#dettaglio').on('click', '#btnCoefficenteCommissione', function () {
        var tabella = $(this).parents('table:first').parents('table:first');
		var datiNascosti = tabella.find('td.colonnaDatiNascosti');
		var length = $('#dettaglio').find("#commissione-length").val();
		var idcridef = datiNascosti.find('#spanidCridef').text();
	    var necvan = datiNascosti.find('#spannecvan').text();
		var modmanu =  datiNascosti.find('#spanmodmanu').text();
	    var norpar = datiNascosti.find('#spannorpar').text();
		var norpar1 =  datiNascosti.find('#spannorpar1').text();
		var coeffiTotale = datiNascosti.find('#spancoeffi').text();
		var idcrival = datiNascosti.find('#spanidCrival').text();

		var modale = $("#mascheraCoefficenteCommissione").find('tbody');
		$(modale).find("#necvan").val(necvan);
		$(modale).find("#idcridef").val(idcridef);
		$(modale).find("#coeffiTotale").val(coeffiTotale);
		$(modale).find("#idcrival").val(idcrival);
		$(modale).find("#ditta").val(ditta);

		$(modale).find("#numeroCriterio").text(norpar);
    	if(norpar1!=null && norpar1!=0){
    		$(modale).find("#numeroSubCriterio").text(norpar1);
    		$(modale).find("#rigaSubCriterio").show();
    	}else{
    		$(modale).find("#rigaSubCriterio").hide();
    	}

		for(var i=0;i<length;i++){
			var rigaModale = modale.find('#componente_commissione_'+i);
			var rigaModaleNote = modale.find('#note_componente_commissione_'+i);
			coeffi = tabella.find("#riga_componente_comm_"+i).find("#coeffi").html();
			coeffi=coeffi.replace(',','.');
			note = tabella.find("#riga_componente_comm_note_"+i).find("#note").html();
			idcrivalcom = tabella.find("#riga_componente_comm_"+i).find("#idcrivalcom").html();
			var codfof = tabella.find("#riga_componente_comm_"+i).find("#codfof").html();
			$(rigaModale).find("#InputcoeffComm_"+i).val(coeffi);
			$(rigaModale).find("#idcrivalcom_"+i).val(idcrivalcom);
			$(rigaModaleNote).find("#InputnoteComm_"+i).val(note);

			if(profiloCommissione && !(codfof == commissarioAttivo)){
				$(modale).find("#spanCoeffi_"+i).html(coeffi);
			}else{
				if(modmanu==1){
					$(modale).find(".tabellato").hide();
					$(modale).find(".textInput").show();
				}else {
					_popolaTabellatoComm(coeffi,i);
					$(modale).find(".textInput").hide();
					$(modale).find(".tabellato").show();
				}
			}
		}
    	$(".ui-dialog-titlebar").hide();
		$("#mascheraCoefficenteCommissione").dialog({height:"auto"});
	   	$("#mascheraCoefficenteCommissione").dialog("open");
		
	});
	
	//Si forza l'apertura delle row child per tutte le righe
	_tableDettaglioValutazione.rows().iterator( 'row', function ( context, index ) {
		var tr =  $( this.row( index ).node() );
		var row = _tableDettaglioValutazione.row( tr );
		var livpar = row.data().livpar;
		if(livpar==1 || livpar==3) {
        	tr.addClass('criterioPadre');
        }
		if(livpar==1 || livpar==2) {
        	row.child(format(row.data())).show();
		}
	});
	
	
	/*
	 * viene popolata la select dei valori del coefficente, adoperando il tabellato A1z07
	 */
	function _popolaTabellatoA1z07(coeffi) {
		$("#selectA1z07").append($("<option/>", {value: "" ,text: "" }));
		var vetValori=tabA1z07.split(";");
		var selezionare=false;
		for(i=0;i<vetValori.length;i++){
			if(parseFloat(coeffi) ==  parseFloat(vetValori[i].split(":")[0]))
				selezionare=true;
			else
				selezionare=false;
			$("#selectA1z07").append($("<option/>", {value: vetValori[i].split(":")[0], text:  vetValori[i].split(":")[1], selected:  selezionare}));
		}
				
		//Se e' presente un solo valore nel tabellato allora lo si seleziona
		if($("#selectA1z07 option").length == 2 ){
			$("#selectA1z07 option").eq(1).prop('selected', true);
		}
	}
	
	function _popolaTabellatoComm(coeffi,index) {
		$("#selectA1z07_"+index).append($("<option/>", {value: "" ,text: "" }));
		var vetValori=tabA1z07.split(";");
		var selezionare=false;
		for(i=0;i<vetValori.length;i++){
			if(parseFloat(coeffi) ==  parseFloat(vetValori[i].split(":")[0]))
				selezionare=true;
			else
				selezionare=false;
			$("#selectA1z07_"+index).append($("<option/>", {value: vetValori[i].split(":")[0], text:  vetValori[i].split(":")[1], selected:  selezionare}));
		}
				
		//Se e' presente un solo valore nel tabellato allora lo si seleziona
		if($("#selectA1z07_"+index+" option").length == 2 ){
			$("#selectA1z07_"+index+" option").eq(1).prop('selected', true);
		}
	}
	
	/*
     * Definizione metodo di validazione coefficente
     */
	jQuery.validator.addMethod("isCoefficenteValido", function(value, element) { return isCoefficenteValido(value);	}, "Formato non valido"	);
    
	/*
	 * Definizione validazione form di inserimento
	 */
    $("#formCoefficente").validate({
    	rules: { 
    		Inputcoeff: "isCoefficenteValido"
    	},
		messages: {
			Inputcoeff: "Attenzione! E' stato inserito un valore non consentito: il valore deve essere compreso fra 0 e 1 e ci devono essere al piu' 9 cifre decimali!"			
		},
		errorPlacement: function(error, element) {
			error.appendTo( $("#errorMessage") );
	    }
	});
    
    
    
    /*
     * Alla modifica del campo del coefficente si impostano un numero di decimali 
     * pari a quelli indicati nel tabellato A1049, si calcola il punteggio e si valorizza
     * il campo nascosto adoperato per il salvataggio del valore del coefficente
     */
	$("#Inputcoeff").change(
    	function() {
    		if ($("#Inputcoeff").val() != "") {
    			$("#Inputcoeff").val($("#Inputcoeff").val().replace(",","."));
    			//$("#Inputcoeff").val(formatNumber($("#Inputcoeff").val(),formatoCoeff));
    			var maxpunG1cridef = parseFloat($("#maxpunG1cridef").val());
        		var coeff=parseFloat($("#Inputcoeff").val());
                //In javascript ci sono errori nella moltiplicazione di numeri decimali, per esempio
        		//10 * 0.756001 restituisce il valore 7,560000000001
        		//Per risolvere il problema ho fatto ricorso ad una libreria esterna(big.js) che gestisce le operazioni
        		//sui numeri

                var x = new Big(maxpunG1cridef);
                var y = new Big(coeff);
                var punteg = x.mul(y).round(numCifrePunteg);
                $("#punteggioVal").text(punteg);
                $("#coeffImpostato").val(coeff);
    		}else{
    			$("#punteggioVal").text("");
    			$("#coeffImpostato").val("");
    		}
    	}
    );
	
	    /*
     * Alla modifica del campo del note si valorizza
     * il campo nascosto adoperato per il salvataggio del valore delle note
     */
	$("#Inputnote").change(
    	function() {
    		if ($("#Inputnote").val() != "") {
        		var note =$("#Inputnote").val();

                $("#noteImpostato").val(note);
    		}else{
    			$("#noteImpostato").val("");
    		}
    	}
    );
    
    /*
     * Alla modifica del campo della select contenente i valori per il coefficente, si 
     * calcola il punteggio e si valorizza il campo nascosto adoperato per il salvataggio 
     * del valore del coefficente
     */
    $("#selectA1z07").change(
        	function() {
        		if ($("#selectA1z07").val() != "") {
        			var maxpunG1cridef = parseFloat($("#maxpunG1cridef").val());
            		var coeff=parseFloat($("#selectA1z07").val());
            		//var punteg = maxpunG1cridef * coeff;
            		//$("#punteggioVal").text(punteg);
            		
            		//In javascript ci sono errori nella moltiplicazione di numeri decimali, per esempio
            		//10 * 0.756001 restituisce il valore 7,560000000001
            		//Per risolvere il problema ho fatto ricorso ad una libreria esterna(big.js) che gestisce le operazioni
            		//sui numeri
            		
                    var x = new Big(maxpunG1cridef);
                    var y = new Big(coeff);
                    var punteg = x.mul(y).round(numCifrePunteg);
                    $("#punteggioVal").text(punteg);
            		$("#coeffImpostato").val(coeff);
        		}else{
        			$("#punteggioVal").text("");
        			$("#coeffImpostato").val("");
        		}
        	}
        );
    
    
}
	
	function onChangeTabellatoComm(ths) {
		var coeff=$(ths).val();
		$(ths).closest("td").find(".textInput").val(coeff);
		$(ths).closest("td").find("#update").val("true");
	}

    /*
     * Controllo validazione del punteggio, valore compreso fra 0 e 1
     * e deve avere al massimo 9 cifre decimali
     */
	function isCoefficenteValido(coeffString) {
		if(coeffString != null){
			var coeff=parseFloat(coeffString);
			if(coeff>1 || coeff<0){
				return false;
			}else{
				var coeffVet= coeffString.split(".");
				if(coeffVet.length>1){
					if(coeffVet[1].length>9)
						return false;
				}
			}
			
		}
		
		return true;
	}	

	function onchangeInput(riga) {
			if ($(riga).val() != "") {
				$(riga).val($(riga).val().replace(",","."));
				var isValid = isCoefficenteValido($(riga).val());
				if(isValid){
					var coeff=parseFloat($(riga).val());
					//coeff =(coeff).toFixed(numCifrePunteg);
					$(riga).val(coeff);
					$("#mascheraCoefficenteCommissione").find('#errorMessage').html("");
				}else{
					$("#mascheraCoefficenteCommissione").find('#errorMessage').html("Attenzione! E' stato inserito un valore non consentito: il valore deve essere compreso fra 0 e 1 e ci devono essere al piu' 9 cifre decimali!");
				}
			}
			$(riga).closest("td").find("#update").val("true");
	}
	
	function onchangeInputNote(riga,i) {
			var j=(riga.id).substring(((riga.id).indexOf('_'))+1);
			var idcoeff= document.getElementById("InputcoeffComm_"+j);
			//onchangeInput(idcoeff);
			$(idcoeff).closest("td").find("#update").val("true");
	}

	function attivaCommissione(){
		var continua = confirm("Confermi l'attivazione della funzione 'Assegna coefficiente' per la modifica diretta del coefficiente complessivo del criterio o subcriterio?");
		if(continua){
			profiloCommissioneAttivato = true;
			$("#attivaCommissioneTr").hide();
			_getDettaglio();
		}
	}
	
$(window).ready(function (){
	//Caricamento dei dati dalla popup
	gara = $("#gara").val();
	codiceGara = $("#codiceGara").val();
	ditta = $("#ditta").val();
	tipoDettaglio = $("#tipo").val();
	percorsoContesto = $("#percorsoContesto").val();
	
	/*
	 * Definizione proprieta' della maschera a scheda del dettaglio del coefficente e del punteggio.
	 */
	$( "#mascheraSchedaCoefficente" ).dialog({
		autoOpen: false,
		height: 300,
		width: 500,
		show: {
			effect: "blind",
			duration: 500
	    },
	    hide: {
	    	effect: "blind",
	    	duration: 300
	    },
	    modal: true,
	    resizable: false,
		focusCleanup: true,
		cache: false,
		 buttons: {
	            "Conferma" : function() {
	            	//Salvataggio dei dati
	            	if ( $("#formCoefficente").validate().form()) {
	            		var options = $("#mascheraSchedaCoefficente").dialog( "option" );
	            		setCoefficente($("#coeffImpostato").val(),$("#punteggioVal").text(),options.idCridef,options.necvan,$("#noteImpostato").val());
	            	} 
	            	
	            },
	            "Annulla" : function() {
	            	$('#selectA1z07').find('option').remove();
	            	$("#tdCoeff").hide();
	    			$("#tdSelectCoeff").hide();
	    			$("#errorMessage") .text("");
	            	$(this).dialog( "close" );
	            }
	          }
	});
	
	$( "#mascheraCoefficenteCommissione" ).dialog({
		autoOpen: false,
		height: 300,
		width: 500,
		show: {
			effect: "blind",
			duration: 500
	    },
	    hide: {
	    	effect: "blind",
	    	duration: 300
	    },
	    modal: true,
	    resizable: false,
		focusCleanup: true,
		cache: false,
		 buttons: {
	            "Conferma" : function() {
	            	//Salvataggio dei dati
					if ( $("#formCoefficenteCommissione").validate().form()) {
	            		$(this).find("#formCoefficenteCommissione").submit();
	            	} 
					
	            },
	            "Annulla" : function() {
					$(this).find('#errorMessage').html("");
	            	$('.tabellato').each(function() {
						$(this).find('option').remove();
					});
	            	$(this).dialog( "close" );
	            }
	          }
	});
	
	
	$('#formCoefficente').submit(false);
	
	//Creazione della tabella di dettaglio
	_getDettaglio();
	
	/*
     * Aggiornamento coefficente
     */
	function setCoefficente(coeff, punteg, idCridef,necvan,note) {
		$.ajax({
			type: "POST",
			url: "pg/SetCoefficente.do",
			data: {
					gara: gara,
					ditta: ditta,
					coeff: coeff,
					punteg: punteg,
					idCridef: idCridef,
					necvan: necvan,
					tipoDettaglio: tipoDettaglio,
					note: $("#Inputnote").val()
			},
			success: function(data){
				$("#mascheraSchedaCoefficente").dialog("close");
				$("#tdCoeff").hide();
				$("#tdSelectCoeff").hide();
				$("#maxpunG1cridef").val("");
				$("#coeffImpostato").val("");
				$("#Inputcoeff").val("");
				$("#noteImpostato").val("");
				$("#Inputnote").val("");
				$('#selectA1z07').find('option').remove();
				_getDettaglio();
			}
		
	})};
	
	$("#formCoefficenteCommissione").submit(function(e) {
		
		e.preventDefault(); // avoid to execute the actual submit of the form.

		var form = $(this);
		var coeffitot = $(this).find("#coeffiTotale").val();
		var length = $(this).find("#length").val();
		var continua = true;
		for(i=0;i<length && continua;i++){	
			var coeffi = $(this).find("#InputcoeffComm_"+i).val();
			continua = isCoefficenteValido(coeffi);
			if(!continua){
				$("#mascheraCoefficenteCommissione").find('#errorMessage').html("Attenzione! E' stato inserito un valore non consentito: il valore deve essere compreso fra 0 e 1 e ci devono essere al piu' 9 cifre decimali!");
			}
		}
		if(coeffitot && continua){
			continua = confirm("Per il criterio o subcriterio corrente risulta gia' assegnato il punteggio complessivo. Procedendo tale valore verra' sbiancato per essere calcolato in seguito. \nConfermi la modifica dei coefficienti espressi dalla commissione?");
		}
		if(continua){
		$.ajax({
			   type: "POST",
			   url: "pg/SetCoefficentiCommissione.do",
			   data: form.serialize(), // serializes the form's elements.
			   success: function(data){
				$("#mascheraCoefficenteCommissione").dialog("close");
				_getDettaglio();
			   }
			 })
		}
	});

	
		
});

