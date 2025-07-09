<%
	/*
	 * Created on 20-Ott-2008
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="cfImpresa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCFImpresa", pageContext, param.codiceDitta)}'/>

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="head">
			<script type="text/javascript" src="${contextPath}/js/jquery.validate.min.js"></script>
			<link rel="stylesheet" type="text/css" href="${contextPath}/css/jquery/ui/std/jquery-ui.css" />
			<style type="text/css">
				label.error {
					float: right;
					color: white;
					background-color: #E40000;
					padding-left: 5px;
					padding-right: 5px;
					padding-top: 2px;
					padding-bottom: 2px;
					vertical-align: middle;
					margin-left: 5px;
					border: 1px solid #BA0000;
					-moz-border-radius-topleft: 4px;
					-webkit-border-top-left-radius: 4px;
					-khtml-border-top-left-radius: 4px;
					border-top-left-radius: 4px;
					-moz-border-radius-topright: 4px;
					-webkit-border-top-right-radius: 4px;
					-khtml-border-top-right-radius: 4px;
					border-top-right-radius: 4px;
					-moz-border-radius-bottomleft: 4px;
					-webkit-border-bottom-left-radius: 4px;
					-khtml-border-bottom-left-radius: 4px;
					border-bottom-left-radius: 4px;
					-moz-border-radius-bottomright: 4px;
					-webkit-border-bottom-right-radius: 4px;
					-khtml-border-bottom-right-radius: 4px;
					border-bottom-right-radius: 4px;
				}

				.error {
					color:Red;
				}
		</style>
	</gene:redefineInsert>

	<gene:setString name="titoloMaschera" value="Richiesta nuovo documento" />
	<c:set var="listaDocInAttesa" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDocumentiFVOEInAttesaFunction",  pageContext, param.codiceGara, param.codiceDitta)}'/>

	<gene:redefineInsert name="corpo">
		<gene:redefineInsert name="addToAzioni"/>
		<gene:formScheda entita="G1FVOEDOC" gestisciProtezioni="true">
			<gene:campoScheda campo="CODICE" obbligatorio="true"/>
			<gene:fnJavaScriptScheda funzione="toggleSections('#G1FVOEDOC_CODICE#')" elencocampi="G1FVOEDOC_CODICE" esegui="true" />
			<gene:campoScheda nome="DATIRICH">
				<td colspan="2"><b>Dati della richiesta</b></td>
			</gene:campoScheda>
			<gene:campoScheda title="Matricola INPS" campo="MATRICOLAINPS" campoFittizio="true" definizione="T10;" obbligatorio="true"/>
			<gene:campoScheda title="Tipo documento" campo="TIPODOCUMENTOOE" campoFittizio="true" definizione="T255;;;NOTE" obbligatorio="true"/>
			<gene:campoScheda title="Dettaglio documento" campo="DETTAGLIODOCUMENTOOE" campoFittizio="true" definizione="T2000;;;NOTE" obbligatorio="true"/>
			<gene:campoScheda title="Prefettura" campo="PREFETTURA" campoFittizio="true" definizione="T100;;A1z34;" obbligatorio="true"/>
			<gene:campoScheda title="Altro soggetto" campo="ALTRISOG" campoFittizio="true" definizione="T100;;;SN" obbligatorio="true"/>
			<gene:campoScheda nome="DATISOGG">
				<td colspan="2"><b>Dati del soggetto</b></td>
			</gene:campoScheda>
			<gene:fnJavaScriptScheda funzione="toggleSectionAntimafia('#ALTRISOG#')" elencocampi="ALTRISOG" esegui="true" />
			<gene:archivio titolo="Tecnici dell'impresa"
						   lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.G_IMPCOL.CODTEC"),"gene/teim/teim-lista-popup.jsp?abilitaNuovo=1","")}'
						   scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda.jsp","")}'
						   schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda-popup.jsp","")}'
						   campi="TEIM.CODTIM;TEIM.NOMTIM"
						   functionId="filteredCodimp"
						   parametriWhere=""
						   chiave="CODTEC"
						   inseribile="true"
						   formName="formTeim">
				<gene:campoScheda campo="CODTEC" title="Codice tecnico" campoFittizio="true" definizione="T10;;;;CODTIM" visibile="false"/>
				<gene:campoScheda campo="NOMTEC" title="Soggetto" campoFittizio="true" definizione="T100;;;;NOMETIM" visibile="false"/>
			</gene:archivio>
			<gene:fnJavaScriptScheda funzione="getDatiTecnico()" elencocampi="CODTEC" esegui="false" />
			<gene:campoScheda title="Nome" campo="NOME" campoFittizio="true" definizione="T40;;;;NOMTIM" obbligatorio="true" speciale="true">
				<a style="float:right;" href="javascript:getDatiTecnico()">Ricarica da anagrafica</a>
			</gene:campoScheda>
			<gene:campoScheda title="Cognome" campo="COGNOME" campoFittizio="true" definizione="T61;" obbligatorio="true"/>
			<gene:campoScheda title="Forma Giuridica" campo="FORMAGIURIDICA" campoFittizio="true" definizione="T100;;A1z33;" obbligatorio="true"/>
			<gene:campoScheda title="Sesso" campo="SESSO" campoFittizio="true" definizione="T1" obbligatorio="true"/>
			<gene:campoScheda title="Codice fiscale" campo="CF" campoFittizio="true" definizione="T16" obbligatorio="true"/>
			<gene:campoScheda title="Codice catastale stato" campo="CODICESTATO" campoFittizio="true" definizione="T4;" obbligatorio="true"/>
			<gene:campoScheda title="Codice catastale comune (*)" campo="CODICECOMUNE" campoFittizio="true" definizione="T4;"/>
			<gene:campoScheda title="Indirizzo sede" campo="INDIRIZZO" campoFittizio="true" definizione="T60;" obbligatorio="true"/>
			<gene:campoScheda title="Giorno di nascita" campo="GIORNO" campoFittizio="true" definizione="N2" obbligatorio="true"/>
			<gene:campoScheda title="Mese di nascita (numero)" campo="MESE" campoFittizio="true" definizione="N2" obbligatorio="true"/>
			<gene:campoScheda title="Anno" campo="ANNO" campoFittizio="true" definizione="N4;" obbligatorio="true"/>
			<gene:campoScheda title="Lingua tedesca" campo="LINGUATEDESCA" campoFittizio="true" definizione="T4;;;SN"/>

			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:richiediDocumento();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:window.close();">&nbsp;
				</td>
			</gene:campoScheda>
			<input type="hidden" name="codiceGara" value="${param.codiceGara}"/>
			<input type="hidden" name="codiceDitta" value="${param.codiceDitta}"/>
		</gene:formScheda>
		<gene:javaScript>

			function archivioListaCustom(modalita){
				if (2===modalita) {
					functionId = "filteredCodimp";
					parametriWhere = 'T:${param.codiceDitta};T:${param.codiceDitta};T:${param.codiceDitta};T:${param.codiceDitta};';
				}else{
					functionId = "skip";
					parametriWhere = '';
				}
				formTeim.archFunctionId.value = functionId;
				formTeim.archWhereParametriLista.value = parametriWhere;
				archivioLista('formTeim');
			}

			function richiediDocumento(value){
			var listaControllo="${listaDocInAttesa}";
			var codice = getValue("G1FVOEDOC_CODICE");

			if(codice===''){
				alert("Selezionare una tipologia di documento.");
				return;
			}
			var codiciDaIgnorare = ['00001','00004','00005','00035','00036','00037','00041','00899']
			if(listaControllo.indexOf(codice)>-1 && !codiciDaIgnorare.includes(codice)){
				alert("Risulta presente una richiesta per questa tipologia di documento. Attenderne l'elaborazione prima di procedere a una nuova richiesta.");
				return;
			}

			if(!$("form[name^='formScheda']").valid()){
				alert("Compilare i campi richiesti.");
				return;
			}

			var codiceGara= "${param.codiceGara}";
			var codiceDitta= "${param.codiceDitta}";
				bloccoPagina = true;
				setTimeout(() => {
					$.ajax({
						type: "POST",
						dataType: "json",
						url: "pg/ScambioDatiPCP.do?metodo=gestioneAppalto",
						data: {
							codiceGara: codiceGara.replace(/^\\$/, ''),
							codgar: codiceGara,
							servizio: "RICHIEDI_DOCUMENTI_FVOE",
							codimp: codiceDitta,
							codice: getValue("G1FVOEDOC_CODICE"),
							matricolaINPS: getValue("MATRICOLAINPS"),
							anno: getValue("ANNO"),
							tipoDocumentoOE: getValue("TIPODOCUMENTOOE"),
							dettaglioDocumentoOE: getValue("DETTAGLIODOCUMENTOOE"),
							nome: getValue("NOME"),
							cognome: getValue("COGNOME"),
							sesso: getValue("SESSO")?.toUpperCase(),
							cf: getValue("CF")?.toUpperCase(),
							giorno: getValue("GIORNO"),
							mese: getValue("MESE"),
							codiceStato: getValue("CODICESTATO")?.toUpperCase(),
							codiceComune: getValue("CODICECOMUNE")?.toUpperCase(),
							linguaTedesca: getValue("LINGUATEDESCA"),
							formaGiuridica: getValue("FORMAGIURIDICA"),
							indirizzo: getValue("INDIRIZZO"),
							prefettura: getValue("PREFETTURA"),
							altrisog: getValue("ALTRISOG")
						},
						async: false,
						beforeSend: function() {
							bloccaRichiesteServer();
						},
						success: function(json) {
							if(json.done!=='Y'){
								console.log(json)
								if(json.anacErrors)
									alert("Errore "+json.anacErrors[0].codice+" - "+json.anacErrors[0].dettaglio+"\n\nGovwayTransactionId: "+json.govwayTransactionId);
								else if(json.internalErrors)
									alert("Errore "+json.internalErrors[0]);
								else if(json.pgErrors)
									alert("Errore: "+json.pgErrors);
								else
									alert("Errore. Consultare la console");
							}
							window.opener.selezionaPagina(1);
							window.close();
						}
					});
				},150);
			}

			function toggleSectionAntimafia(value){
				if(value=='1'){
					$('#rowANNO td.etichetta-dato').get(0).innerHTML = 'Anno di nascita (*)';
					showObj("rowCODTEC",true);
					showObj("rowNOMTEC",true);
					showObj("rowNOME",true);
					showObj("rowCOGNOME",true);
					showObj("rowSESSO",true);
					showObj("rowCF",true);
					showObj("rowGIORNO",true);
					showObj("rowMESE",true);
					showObj("rowANNO",true);
					showObj("rowCODICESTATO",true);
					showObj("rowCODICECOMUNE",true);
					showObj("rowDATISOGG",true);
				}else{
					$('#rowANNO td.etichetta-dato').get(0).innerHTML = 'Anno (*)';
					showObj("rowCODTEC",false);
					setValue("CODTEC","");
					showObj("rowNOMTEC",false);
					setValue("NOMTEC","");
					showObj("rowNOME",false);
					setValue("NOME","");
					showObj("rowCOGNOME",false);
					setValue("COGNOME","");
					showObj("rowSESSO",false);
					setValue("SESSO","");
					showObj("rowCF",false);
					setValue("CF","");
					showObj("rowGIORNO",false);
					setValue("GIORNO","");
					showObj("rowMESE",false);
					setValue("MESE","");
					showObj("rowANNO",false);
					setValue("ANNO","");
					showObj("rowCODICESTATO",false);
					setValue("CODICESTATO","");
					showObj("rowCODICECOMUNE",false);
					setValue("CODICECOMUNE","");
					showObj("rowDATISOGG",false);
				}

			}
			function toggleSections(value){
				showObj("rowMATRICOLAINPS",false);
				setValue("MATRICOLAINPS","");
				showObj("rowANNO",false);
				setValue("ANNO","");

				showObj("rowTIPODOCUMENTOOE",false);
				setValue("TIPODOCUMENTOOE","");
				showObj("rowDETTAGLIODOCUMENTOOE",false);
				setValue("DETTAGLIODOCUMENTOOE","");

				showObj("rowCODTEC",false);
				setValue("CODTEC","");
				showObj("rowNOMTEC",false);
				setValue("NOMTEC","");
				showObj("rowNOME",false);
				setValue("NOME","");
				showObj("rowCOGNOME",false);
				setValue("COGNOME","");
				showObj("rowSESSO",false);
				setValue("SESSO","");
				showObj("rowCF",false);
				setValue("CF","");
				showObj("rowGIORNO",false);
				setValue("GIORNO","");
				showObj("rowMESE",false);
				setValue("MESE","");
				showObj("rowANNO",false);
				setValue("ANNO","");
				showObj("rowCODICESTATO",false);
				setValue("CODICESTATO","");
				showObj("rowCODICECOMUNE",false);
				setValue("CODICECOMUNE","");
				showObj("rowLINGUATEDESCA",false);
				setValue("LINGUATEDESCA","");

				showObj("rowINDIRIZZO",false);
				setValue("INDIRIZZO","");
				showObj("rowFORMAGIURIDICA",false);
				setValue("FORMAGIURIDICA","");

				showObj("rowPREFETTURA",false);
				setValue("PREFETTURA","");
				showObj("rowALTRISOG",false);
				setValue("ALTRISOG","");


				var codiciConDatiRichiesta = ['00001','00004','00005','00035','00036','00037','00041','00899']
				if(codiciConDatiRichiesta.includes(value)){
					showObj("rowDATIRICH",true);
					//ripristino i nomi originali
					$('#rowCODICECOMUNE td.etichetta-dato').get(0).innerHTML = 'Codice catastale comune (*)';
					$('#rowANNO td.etichetta-dato').get(0).innerHTML = 'Anno (*)';
					$('#rowCF td.etichetta-dato').get(0).innerHTML = 'Codice fiscale (*)';
					$('#rowCOGNOME td.etichetta-dato').get(0).innerHTML = 'Cognome (*)';
					$("form[name^='formScheda']").validate().resetForm();
				}else{
					showObj("rowDATIRICH",false);
				}

				if ("00001" == value ) {
					showObj("rowPREFETTURA",true);
					showObj("rowALTRISOG",true);
				}

				if("00004" == value){
					$('#rowCODICECOMUNE td.etichetta-dato').get(0).innerHTML = 'Codice catastale comune (*)';
					$('#rowANNO td.etichetta-dato').get(0).innerHTML = 'Anno di nascita (*)';
					showObj("rowCODTEC",true);
					showObj("rowNOMTEC",true);
					showObj("rowNOME",true);
					showObj("rowCOGNOME",true);
					showObj("rowSESSO",true);
					showObj("rowCF",true);
					showObj("rowGIORNO",true);
					showObj("rowMESE",true);
					showObj("rowANNO",true);
					showObj("rowCODICESTATO",true);
					showObj("rowCODICECOMUNE",true);
					showObj("rowLINGUATEDESCA",true);
				}
				if ("00005" == value) {
					$('#rowCF td.etichetta-dato').get(0).innerHTML = 'P.Iva (*)';
					$('#rowCOGNOME td.etichetta-dato').get(0).innerHTML = 'Denominazione (*)';
					showObj("rowCOGNOME",true);
					showObj("rowCF",true);
					showObj("rowCODICESTATO",true);
					showObj("rowCODICECOMUNE",true);
					showObj("rowINDIRIZZO",true);
					showObj("rowFORMAGIURIDICA",true);
					showObj("rowLINGUATEDESCA",true);
					getDatiImpresa();
				}
				if ("00036" == value || "00037" == value) {
					showObj("rowMATRICOLAINPS",true);
					showObj("rowANNO",true);
					getDatiImpresa();
				}
				if ("00035" == value || "00041" == value) {
					$('#rowCODICECOMUNE td.etichetta-dato').get(0).innerHTML = 'Codice catastale comune (*)';
					showObj("rowCODICECOMUNE",true);
					showObj("rowANNO",true);
					getDatiImpresa();
				}
				if ("00899" == value ) {
					showObj("rowTIPODOCUMENTOOE",true);
					showObj("rowDETTAGLIODOCUMENTOOE",true);
				}
			}

			$(document).ready(function(){
				var cfImpresa = "${cfImpresa}";
				if(cfImpresa.match(/^[0-9]{11}$/)){
					$('#G1FVOEDOC_CODICE option[value="00041"]').remove();
				}else{
					$('#G1FVOEDOC_CODICE option[value="00035"]').remove();
					$('#G1FVOEDOC_CODICE option[value="00039"]').remove();
				}

				linksetJsPopUpNOME = "";
				linksetJsPopUpNOME +=  creaVocePopUpChiusura("${contextPath}/");
				linksetJsPopUpNOME += creaPopUpSubmenu("javascript:archivioListaCustom(1);hideMenuPopup();",0,"&nbsp;Seleziona da archivio Tecnici delle imprese");
				linksetJsPopUpNOME += creaPopUpSubmenu("javascript:archivioListaCustom(2);hideMenuPopup();",1,"&nbsp;Seleziona da archivio Legali e altri soggetti dell'impresa corrente");
				linksetJsPopUpNOME += creaPopUpSubmenu("javascript:archivioScheda('formTeim');hideMenuPopup();",3,"&nbsp;Visualizza scheda Tecnico dell'impresa");
				//linksetJsPopUpNOME += creaPopUpSubmenu("javascript:javascript:helpMnemonico('CODTIM');hideMenuPopup();",3,"&nbsp;&nbsp;Informazioni campo");

				$("#CF, #SESSO, #CODICESTATO, CODICECOMUNE").css("text-transform","uppercase");
				//	$("#NOME, #COGNOME, #SESSO, #CF, #GIORNO, #MESE, #ANNO, #CODICESTATO, #CODICECOMUNE").prop("readonly", true);
				//	$("#NOME, #COGNOME, #SESSO, #CF, #GIORNO, #MESE, #ANNO, #CODICESTATO, #CODICECOMUNE").css('background-color','#EFEFEF')

				jQuery.validator.addMethod("isMatricolaInpsValida",
				function(value, element) {
					return value.toUpperCase().match(/^[A-Z0-9]{10}$/);
				},"Il valore specificato non rispetta il formato previsto");

				jQuery.validator.addMethod("isAnnoValido",
				function(value, element) {
					return value.match(/^[0-9]{4}$/);
				},"Il valore specificato non è valido");

				jQuery.validator.addMethod("isMF",
				function(value, element) {
				return value.toUpperCase().match(/^[MF]$/);
				},"Il valore specificato non è valido");

				jQuery.validator.addMethod("controlloCF",
					function(value, element) {
					var codice=getValue("G1FVOEDOC_CODICE");
					if(codice === '00005')
						return value.toUpperCase().match(/^[0-9]{11}$/);
					else
						return value.toUpperCase().match(/^(?:[A-Z][AEIOU][AEIOUX]|[AEIOU]X{2}|[B-DF-HJ-NP-TV-Z]{2}[A-Z]){2}(?:[\dLMNP-V]{2}(?:[A-EHLMPR-T](?:[04LQ][1-9MNP-V]|[15MR][\dLMNP-V]|[26NS][0-8LMNP-U])|[DHPS][37PT][0L]|[ACELMRT][37PT][01LM]|[AC-EHLMPR-T][26NS][9V])|(?:[02468LNQSU][048LQU]|[13579MPRTV][26NS])B[26NS][9V])(?:[A-MZ][1-9MNP-V][\dLMNP-V]{2}|[A-M][0L](?:[1-9MNP-V][\dLMNP-V]|[0L][1-9MNP-V]))[A-Z]$/);
				},"Il valore specificato non è valido");

				jQuery.validator.addMethod("isCodComuneRequired",
					function(value, element) {
					var codiceDocumento=getValue("G1FVOEDOC_CODICE");
					if('00004' === codiceDocumento || '00005' === codiceDocumento ||'00035' === codiceDocumento || '00041' === codiceDocumento)
						return value.toUpperCase().match(/^[A-M]\d{3}$/);
					else
						return true;
				},"Il valore specificato non è valido");

				$("form[name^='formScheda']").validate({
					rules:{
						MATRICOLAINPS: {
							required: true,
							isMatricolaInpsValida: true
						},
						ANNO: "required",
						TIPODOCUMENTOOE: "required",
						DETTAGLIODOCUMENTOOE: "required",
						NOME: "required",
						COGNOME: "required",
						SESSO: {
							required: true,
							isMF: true
			 			},
						CF: {
							required: true,
							controlloCF: true
						},
						GIORNO: "required",
						MESE: "required",
						CODICESTATO: {
							required: true,
							minlength: 4
						},
						CODICECOMUNE: {
							minlength: 4,
							isCodComuneRequired: true
						},
						FORMAGIURIDICA: "required",
						INDIRIZZO: "required",
						PREFETTURA: "required",
						ALTRISOG: "required"
					},
					messages:{
						MATRICOLAINPS: "Il campo deve essere di 10 caratteri alfanumeri",
						CF: "Il valore specificato non è valido",
						CODICESTATO: "Il valore specificato non è valido",
						ANNO: "Il campo deve essere di 4 caratteri numerici",
						SESSO: "Indicare M o F",
						NOME: "Il campo deve essere valorizzato",
						COGNOME: "Il campo deve essere valorizzato",
						INDIRIZZO: "Il campo deve essere valorizzato",
						PREFETTURA: "Il campo deve essere valorizzato",
						ALTRISOG: "Il campo deve essere valorizzato",
						GIORNO: "Il campo deve essere valorizzato",
						MESE: "Il campo deve essere valorizzato",
						FORMAGIURIDICA: "Il campo deve essere valorizzato",
						CODICECOMUNE: "Il valore specificato non è valido",
						TIPODOCUMENTOOE: "Il campo deve essere valorizzato",
						DETTAGLIODOCUMENTOOE: "Il campo deve essere valorizzato"
					}
				});
			});

			$("#CODICESTATO").on("input",function() {
				toggleCodStato()
			});

			function toggleCodStato(){
				var codiceDocumento=getValue("G1FVOEDOC_CODICE");
				if ($("#CODICESTATO").val()?.toUpperCase() != "Z000" && '00035' != codiceDocumento && '00041' != codiceDocumento) {
					showObj("rowCODICECOMUNE",false);
					setValue("CODICECOMUNE","");
				}
				else{
					showObj("rowCODICECOMUNE",true);
				}
			}


			function getDatiTecnico() {
				var codtec = getValue("CODTEC");
				setValue("NOME","");
				setValue("COGNOME","");
				setValue("SESSO","");
				setValue("CF","");
				setValue("GIORNO","");
				setValue("MESE","");
				setValue("ANNO","");
				setValue("CODICESTATO","");
				setValue("CODICECOMUNE","");

				if(codtec == '')
					return;

				document.getElementById('bloccaScreen').style.visibility='visible';
				document.getElementById('wait').style.visibility='visible';
				$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});

				$.ajax({
					type: "POST",
					dataType: "json",
					async: false,
					beforeSend: function(x) {
						if(x && x.overrideMimeType) {
							x.overrideMimeType("application/json;charset=UTF-8");
						}
					},
					url: "pg/GetDatiTecnico.do",
					data: {
						codtec: codtec
					},
					success: function(data) {
						if (data) {
							if(data.nome) setValue("NOME",data.nome);
							if(data.cognome) setValue("COGNOME",data.cognome);
							if(data.sesso) setValue("SESSO",data.sesso);
							if(data.cf) setValue("CF",data.cf);
							if(data.giorno) setValue("GIORNO",data.giorno);
							if(data.mese) setValue("MESE",data.mese);
							if(data.anno) setValue("ANNO",data.anno);
							if(data.codiceStato) setValue("CODICESTATO",data.codiceStato);
							if(data.codiceComune) setValue("CODICECOMUNE",data.codiceComune);
							toggleCodStato();
							$("form[name^='formScheda']").valid();
						}
					},
					error: function(e){
						alert("Errore durante la lettura dell'anagrafica del tecnico selezionato.");
					},
					complete: function(e){
						document.getElementById('bloccaScreen').style.visibility='hidden';
						document.getElementById('wait').style.visibility='hidden';
					}
				});
			}

			function getDatiImpresa() {
				var codimp = "${param.codiceDitta}"

				document.getElementById('bloccaScreen').style.visibility='visible';
				document.getElementById('wait').style.visibility='visible';
				$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});

				$.ajax({
					type: "POST",
					dataType: "json",
					async: false,
					beforeSend: function(x) {
						if(x && x.overrideMimeType) {
							x.overrideMimeType("application/json;charset=UTF-8");
						}
					},
					url: "pg/GetDatiImpresa.do",
					data: {
						codimp: codimp
					},
					success: function(data) {
						if (data) {
							if(data.matricolaINPS) setValue("MATRICOLAINPS",data.matricolaINPS);
							if(data.cognome) setValue("COGNOME",data.cognome);
							if(data.cf) setValue("CF",data.cf);
							if(data.indirizzo) setValue("INDIRIZZO",data.indirizzo);
							if(data.codiceStato) setValue("CODICESTATO",data.codiceStato);
							if(data.codiceComune) setValue("CODICECOMUNE",data.codiceComune);
							toggleCodStato();
						}
					},
					error: function(e){
						alert("Errore durante la lettura dell'anagrafica dell'impresa.");
					},
					complete: function(e){
						document.getElementById('bloccaScreen').style.visibility='hidden';
						document.getElementById('wait').style.visibility='hidden';
					}
				});
			}
		</gene:javaScript>
	</gene:redefineInsert>
	<gene:redefineInsert name="noteAvvisi"/>
	<gene:redefineInsert name="documentiAssociati"/>

</gene:template>