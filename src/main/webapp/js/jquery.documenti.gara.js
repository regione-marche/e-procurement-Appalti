/*
 * Gestione dei documenti di gara
 * 
 */

/*
 * Genera un documento pdf da modello e lo allega
 * @param {string} ngara il numero di gara
 * @param {string} indice l'indice del documento nella lista documenti della gara in esame
 * @param {string} idstampa identificativo del tipo di documento da generare (CODICE-CLIENTE + "_" + IDENTIFICATIVO-DOCUMENTO)
 * @param {string} contextPath il context path della pagina
 * @returns {undefined}
 */
function generaAllegaPdf(ngara, indice, idstampa, contextPath) {

	_wait();
	$.ajax({
		type: "GET",
		dataType: "text",
		async: false,
		beforeSend: function (x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/text");
			}
		},
		url: contextPath + "/pg/GeneraAllegaPdf.do",
		data: {
			ngara: ngara,
			indice: indice,
			idstampa: idstampa
		},
		success: function (data) {
			if (data && data.length > 0) {
				var spanNomeFIle = "W_DOCDIG_DIGNOMDOC_" + indice;
				setValue(spanNomeFIle, data.toUpperCase());
				var idSelezionaFile = "#selFile[" + indice + "]";
				var idSLinkGenera = "#genera_" + indice;
				var idNomeFileGenerato = "#rowselezioneFile_" + indice;
				$(idSelezionaFile).remove();
				$(idSLinkGenera).remove();
				$(idNomeFileGenerato).find(".valore-dato").html("<span>" + data + "</span>");
				var newLinkDownladFile = "javascript:visualizzaFileGenerato('" + data + "');";
				var idDonwlodFile = "#" + spanNomeFIle + "view";
				$(idDonwlodFile).parent().attr('href', newLinkDownladFile);
				$("#NOMEDOCGEN_" + indice).val(data);
			}
		},
		error: function (e) {
			alert("Errore durante la generazione del documento pdf");
		},
		complete: function () {
			_nowait();
		}
	});
}

/*
 * Attivazione del messaggio di attesa.
 */
function _wait() {
	document.getElementById('bloccaScreen').style.visibility = 'visible';
	document.getElementById('wait').style.visibility = 'visible';
	$("#wait").offset({top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
}


/*
 * Disattivazione del messaggio di attesa
 */
function _nowait() {
	document.getElementById('bloccaScreen').style.visibility = 'hidden';
	document.getElementById('wait').style.visibility = 'hidden';
}


function aggiornaRichiestaFirma(indice, checkbox){
	if(checkbox.checked){
		setValue("W_DOCDIG_DIGFIRMA_" + indice,"1");
	}else{
		setValue("W_DOCDIG_DIGFIRMA_" + indice,"");
	}
}

/*
 * Viene controllato che il file abbia una estensione valida,
 * ossia sia presente fra quelle indicate
 */
function controlloTipoFile(nomeFile, estensioniConsentite){
	var estensioneValida=false;
	if(estensioniConsentite!=null && estensioniConsentite!=""){
		var estensioni=estensioniConsentite.split(";");
		nomeFile = nomeFile.toUpperCase();
		if(nomeFile==null || nomeFile==""){
			estensioneValida=true;
		}else{
			for(var i=0;i<estensioni.length;i++){
				if(estensioni[i]!=null && estensioni[i]!=""){
					if(nomeFile.lastIndexOf(estensioni[i].toUpperCase())>0){
						estensioneValida=true;
						break;
					}
				}
			}
		}
	}else{
		estensioneValida=true;
	}
	return estensioneValida;
}

