<%
			/*
 
				Descrizione:
					Interno delle scheda di documenti_verifichea
				Creato da:
					Cristian Febas
			*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>


<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/jquery.documenti.gara.js"></script>

<c:set var="idStipula" value='${gene:getValCampo(keyParent,"ID")}' scope="request" />
<c:set var="idDocStipula" value='${gene:getValCampo(key,"ID")}' scope="request" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="firmaProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:set var="isVerificaPDFA" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "uploadFile.verifica.pdfA")}'/>
<c:if test='${firmaProvider eq 2 || firmaProvider eq 3}'>
	<c:set var="firmaRemota" value="true"/>
</c:if>
<c:set var="digitalSignatureUrlCheck" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-check-url")}'/>
<c:set var="digitalSignatureProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>

<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' />
<c:set var="tipoWSERP" value='${requestScope.tipoWSERP}' />

<c:choose>
	<c:when test="${!empty digitalSignatureUrlCheck && !empty digitalSignatureProvider && (digitalSignatureProvider eq 1 || digitalSignatureProvider eq 2)}">
		<c:set var="digitalSignatureWsCheck" value='1'/>
	</c:when>
	<c:otherwise>
		<c:set var="digitalSignatureWsCheck" value='0'/>
	</c:otherwise>
</c:choose>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="DOCSTIPULA-scheda">
<c:choose>
	<c:when test='${modo eq "NUOVO"}'>
		<c:set var="titolo" value='Nuovo documento stipula'/>
	</c:when>
	<c:otherwise>
		<c:set var="titolo" value='Documento stipula'/>
	</c:otherwise>
</c:choose>

<c:if test="${tipoWSERP eq 'INARCASSA'}">
	<c:set var="gestioneODA" value='1'/>
	<c:set var="numoda" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select numoda from g1stipula where id = ?", idStipula)}' />
</c:if>


<gene:setString name="titoloMaschera" value="${titolo}" />

<gene:redefineInsert name="corpo">
	<gene:formScheda entita="G1DOCSTIPULA" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreG1DOCSTIPULA">
	
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="V_GARE_STIPULA"/>
			<jsp:param name="inputFiltro" value="${keyParent}"/>
			<jsp:param name="filtroCampoEntita" value="idstipula=${idStipula}"/>
		</jsp:include>
	
		<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
		<gene:campoScheda campo="ID" visibile="false" />
		<gene:campoScheda campo="IDSTIPULA" defaultValue="${idStipula}" visibile="false" />
		<gene:campoScheda campo="NUMORD" visibile="false"/>
		<gene:campoScheda campo="FASE" obbligatorio="true"/>
		<gene:campoScheda campo="TITOLO" obbligatorio="true"/>
		<gene:campoScheda campo="DESCRIZIONE" />
		<gene:campoScheda campo="NOTE" />
		<gene:campoScheda campo="VISIBILITA" obbligatorio="true" modificabile="${datiRiga.G1DOCSTIPULA_STATODOC == 1}"/>
		<gene:campoScheda campo="OBBLIGATORIO" />
		<gene:campoScheda campo="FORMATO" />
		<gene:campoScheda campo="STATO" entita="G1STIPULA" where="G1DOCSTIPULA.IDSTIPULA=G1STIPULA.ID" visibile="false"/>
		<gene:campoScheda campo="DOCPROV" visibile="false"/>
		
		<gene:campoScheda campo="IDPRG" visibile="false" entita="W_DOCDIG"
		 where="W_DOCDIG.IDPRG='PG' AND W_DOCDIG.DIGENT='G1DOCSTIPULA' AND W_DOCDIG.DIGKEY1='${idDocStipula}'" />
		<gene:campoScheda campo="IDDOCDIG" visibile="false" entita="W_DOCDIG"
		 where="W_DOCDIG.IDPRG='PG' AND W_DOCDIG.DIGENT='G1DOCSTIPULA' AND W_DOCDIG.DIGKEY1='${idDocStipula}'" />
		<gene:campoScheda campo="DIGDESDOC" visibile="false" entita="W_DOCDIG"
		 where="W_DOCDIG.IDPRG='PG' AND W_DOCDIG.DIGENT='G1DOCSTIPULA' AND W_DOCDIG.DIGKEY1='${idDocStipula}'" />
		 
		 <c:if test='${((modo eq "MODIFICA" or modo eq "NUOVO") and gestioneODA eq "1") }'>
			<gene:campoScheda title="" nome="tipoAllegato">
				<input type="radio" value="1" name="allegato" id="file" <c:if test='${datiRiga.G1DOCSTIPULA_DOCPROV != "1"}'>checked="checked"</c:if> onclick="javascript:cambiaTipoAllegato(1,false);" />
				 allega file
				 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				 <input type="radio" value="2" name="allegato" id="docOda" <c:if test='${datiRiga.G1DOCSTIPULA_DOCPROV == "1"}'>checked="checked"</c:if>  onclick="javascript:cambiaTipoAllegato(2,false);" />
				 allega file da oda 
			 
			</gene:campoScheda>
		</c:if>
		 
		<gene:campoScheda campo="DIGNOMDOC" entita="W_DOCDIG" visibile='true' modificabile="false"
		where="W_DOCDIG.IDPRG='PG' AND W_DOCDIG.DIGENT='G1DOCSTIPULA' AND W_DOCDIG.DIGKEY1='${idDocStipula}'" href="javascript:
		visualizzaFileAllegato('${datiRiga.W_DOCDIG_IDPRG}','${datiRiga.W_DOCDIG_IDDOCDIG}', '${datiRiga.W_DOCDIG_DIGNOMDOC}');" speciale='${datiRiga.G1DOCSTIPULA_STATODOC == "1" && modo eq "MODIFICA"}'>
			<c:if test='${datiRiga.G1DOCSTIPULA_STATODOC == "1" && modo eq "MODIFICA"}'>
				<gene:popupCampo titolo="Cancella file allegato" href="cancellaFile();" />
			</c:if>
			<c:if test='${(modo eq "NUOVO" || modo eq "MODIFICA") && firmaProvider eq 3}'>
				<span id="spanRichiestaFirma" style="float:right;">Richiesta firma?
				<input type="checkbox" name="checkboxRichiestaFirma" id="checkboxRichiestaFirma" size="50" <c:if test="${datiRiga.W_DOCDIG_DIGNOMDOC ne '' and datiRiga.W_DOCDIG_DIGFIRMA eq '1'}">checked</c:if> onchange="javascript:aggiornaRichiestaFirma(this);"> </span>
			</c:if>
			<c:if test='${ modo eq "VISUALIZZA" and param.autorizzatoModifiche ne "2" and (datiRiga.W_DOCDIG_DIGNOMDOC ne "")}'>
				<c:if test="${isVerificaPDFA eq '1' && datiRiga.W_DOCDIG_DIGTIPDOC eq 'pdf/a' && (datiRiga.G1DOCSTIPULA_VISIBILITA eq 1 or datiRiga.G1DOCSTIPULA_VISIBILITA eq 2 or datiRiga.G1DOCSTIPULA_VISIBILITA eq 3)}">
					&nbsp;<img id="imgCheckPDFaDocMessage" src="${pageContext.request.contextPath}/img/pdfa.png" title="File conforme al formato PDF/A" alt="File conforme al formato PDF/A">
				</c:if>
				<c:if test='${not empty firmaRemota && (datiRiga.G1DOCSTIPULA_VISIBILITA eq 1 or datiRiga.G1DOCSTIPULA_VISIBILITA eq 2)}'>
					<a style="float:right;" href="javascript:openModal('${datiRiga.W_DOCDIG_IDPRG}','${datiRiga.W_DOCDIG_IDDOCDIG}','${datiRiga.W_DOCDIG_DIGNOMDOC}', '${contextPath}','');">
					<img src="${contextPath}/img/firmaRemota.png" title="Firma digitale del documento" alt="Firma documento" width="16" height="16">
					<span title="Firma digitale del documento">Firma documento</span></a>
					<c:if test="${datiRiga.W_DOCDIG_DIGFIRMA eq '1' && firmaProvider eq 3}">
						<span style="float:right;"><img width="16" height="16" src="${pageContext.request.contextPath}/img/isquantimod.png"/>&nbsp;In attesa di firma&nbsp;&nbsp;</span>
					</c:if>
				</c:if>
			</c:if>
		</gene:campoScheda>
		<gene:campoScheda addTr="false" visibile="${modo ne 'VISUALIZZA'}" >
		<tr id="selFile">
			<td class="etichetta-dato">Nome file</td>
			<td class="valore-dato">
				<input type="file" name="selezioneFile" id="selezioneFile" onchange="javascript:scegliFile(this.value);" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" value=''/>
				<span id="checkPDFaDocMessage" style="display:none; color: blue;"><b>ATTENZIONE:</b> file non conforme al formato PDF/A</span>
				<img id="imgCheckPDFaDocMessage" style="display:none;" src="${contextPath}/img/pdfa.png" title="File conforme al formato PDF/A" alt="File conforme al formato PDF/A">
			</td>
		</tr>
		</gene:campoScheda>
			
		<c:if test="${gestioneODA eq '1'}">
			<gene:campoScheda title="Nome file" nome="selezioneFileOda" visibile="${modo ne 'VISUALIZZA'}">
				<input type="button" value="Sfoglia da Oda" name="selFileOda" id="selFileOda" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);"  onclick='javascript:apriPopupSelezioneOda();'/>
			</gene:campoScheda>	
		</c:if>
		<c:if test="${firmaProvider eq 3}">
			<gene:campoScheda campo="DIGFIRMA" entita="W_DOCDIG" visibile="false" definizione="T2;0;;SN;DIGFIRMA" value="${datiRiga.W_DOCDIG_DIGFIRMA}" />
		</c:if>
		<gene:campoScheda campo="DIGTIPDOC" entita="W_DOCDIG" visibile="false" definizione="T2;0;;SN;DIGTIPDOC" value="${datiRiga.W_DOCDIG_DIGTIPDOC}" />
		<gene:campoScheda title="File da allegare" campo="FILEDAALLEGARE" campoFittizio="true" visibile="false" definizione="T70;0" />
		<gene:campoScheda title="Flag file cancellato" campo="FILECANCELLATO" campoFittizio="true" visibile="false" definizione="T10;0" />

		<gene:campoScheda campo="STATODOC" defaultValue="1" modificabile="false"/>

		<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
		<c:if test="${datiRiga.G1STIPULA_STATO ne 6 }">
		<gene:campoScheda>
			<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
		</gene:campoScheda>
		</c:if>
		
		<gene:fnJavaScriptScheda funzione="gestioneAllegato('#G1DOCSTIPULA_VISIBILITA#','#G1DOCSTIPULA_STATODOC#','${modo}')" elencocampi="G1DOCSTIPULA_VISIBILITA;G1DOCSTIPULA_STATODOC" esegui="true" />		
		<gene:fnJavaScriptScheda funzione='formatoFileOnChangeInit("#G1DOCSTIPULA_FORMATO#")' elencocampi='G1DOCSTIPULA_FORMATO' esegui="true" />
		
		<c:if test="${datiRiga.G1STIPULA_STATO eq 6 }">
			<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
		</c:if>
		
		<input type="hidden" id="getdocumentoallegatoWSERP_codiceOda" name="getdocumentoallegatoWSERP_codiceOda" value="" />
		<input type="hidden" id="getdocumentoallegatoWSERP_nomeallegato" name="getdocumentoallegatoWSERP_nomeallegato" value="" />
		<input type="hidden" id="getdocumentoallegatoWSERP_idFile" name="getdocumentoallegatoWSERP_idFile" value="" />
		
	</gene:formScheda>
	
	<form name="formVisFirmaDigitale" id="formVisFirmaDigitale" action="${contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale.jsp" />
		<input type="hidden" name="idprg" id="idprg" value="" />
		<input type="hidden" name="iddocdig" id="iddocdig" value="" />
	</form>
	
	<jsp:include page="/WEB-INF/pages/gene/system/firmadigitale/modalPopupFirmaDigitaleRemota.jsp" >
		<jsp:param name="firmaProvider" value="${firmaProvider}"/>
	</jsp:include>
	
	
</gene:redefineInsert>
<gene:javaScript>

	

<c:if test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
	document.forms[0].encoding="multipart/form-data";
</c:if>
	
	<c:if test='${modo eq "MODIFICA" || modo eq "NUOVO"}'>
		var dignomdoc= getValue("W_DOCDIG_DIGNOMDOC");
		var selezioneFile = document.getElementById("selezioneFile").value;
	
		if((dignomdoc != null && dignomdoc != "") || (selezioneFile != null && selezioneFile != "")){
			document.getElementById("G1DOCSTIPULA_FORMATO").disabled = true;
		}
	</c:if>
	
	var salvataggioOK = '${requestScope.salvataggioOK}';
	if(salvataggioOK != '' && salvataggioOK){
		historyVaiIndietroDi(1);
	}	
	
	function formatoFileOnChangeInit(formato){
		<c:if test='${modo eq "MODIFICA" || modo eq "NUOVO"}'>
			if(getValue("W_DOCDIG_DIGNOMDOC") == '' && document.getElementById("selezioneFile").value == ''){
				var checkboxRichiestaFirma = document.getElementById("checkboxRichiestaFirma");
				if(formato == 1){
					if(checkboxRichiestaFirma != undefined && checkboxRichiestaFirma != null){
						if(!checkboxRichiestaFirma.checked){
							document.getElementById("checkboxRichiestaFirma").checked = true;
						}
						checkboxRichiestaFirma.disabled = true;
					}
				} else {
					if(checkboxRichiestaFirma != undefined && checkboxRichiestaFirma != null){
						if(checkboxRichiestaFirma.checked){
							document.getElementById("checkboxRichiestaFirma").checked = false;
						}
						checkboxRichiestaFirma.disabled = true;
					}
				}
				
			}
		</c:if>
	}
	
	function gestioneCheckDaFirmare(visibile,abilitato,toglicheck){
		var checkboxRichiestaFirma = document.getElementById("checkboxRichiestaFirma");
		if(checkboxRichiestaFirma != undefined && checkboxRichiestaFirma != null ){
			if(visibile == 1){
				$("#spanRichiestaFirma").css('display','block');
			} else {
				$("#spanRichiestaFirma").css('display','none');
			}
			if(toglicheck == 1){
				if(checkboxRichiestaFirma.checked && (getValue("W_DOCDIG_DIGNOMDOC") == '' && document.getElementById("selezioneFile").value == '')){
					if(!checkboxRichiestaFirma.disabled){
						checkboxRichiestaFirma.click();
					} else {
						checkboxRichiestaFirma.disabled = false;
						checkboxRichiestaFirma.click();
						checkboxRichiestaFirma.disabled = true;
					}
				}
			}
			
			if(abilitato == 1 && (getValue("W_DOCDIG_DIGNOMDOC") != '' || document.getElementById("selezioneFile").value != '')){
				checkboxRichiestaFirma.disabled = false;
			} else {
				checkboxRichiestaFirma.disabled = true;
			}
			var formatoFile = document.getElementById("G1DOCSTIPULA_FORMATO").value;
			if(formatoFile == 1 && checkboxRichiestaFirma.checked && !checkboxRichiestaFirma.disabled && (getValue("W_DOCDIG_DIGNOMDOC") != '' || document.getElementById("selezioneFile").value != '')) {
				setValue("W_DOCDIG_DIGFIRMA","1");
			}
		}
	}
	
	function scegliFile(valore) {
		var file = document.getElementById("selezioneFile").files[0];
		var selezioneFile = document.getElementById("selezioneFile").value;
		var lunghezza_stringa = selezioneFile.length;
		var posizione_barra = selezioneFile.lastIndexOf("\\");
		var nome = selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
		var stato= 'OK';
		var formato=document.getElementById("G1DOCSTIPULA_FORMATO").value;
		var ext = nome.substring(nome.lastIndexOf('.')+1);
		var dignomdoc= getValue("W_DOCDIG_DIGNOMDOC");
		if(nome.length>100){
			alert("Il nome del file non può superare i 100 caratteri!");
			stato='KO';
		}
		if (formato == 1 && (ext.indexOf('P7M')==-1 && ext.indexOf('TSD')==-1 && ext.indexOf('PDF')==-1 && ext.indexOf('XML')==-1)){
			alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: P7M;TSD;PDF;XML");
			stato='KO';
		}
		if (formato == 2 && (ext.indexOf('PDF')==-1 )){
			alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: PDF");
			stato='KO';
		}
		if (formato == 4 && (ext.indexOf('XLS')==-1 && ext.indexOf('XSLX')==-1 && ext.indexOf('ODS')==-1)){
			alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: XLS;XLSX;ODS");
			stato='KO';
		}
		if (formato == '' && dignomdoc == '' && nome == ''){
			stato='KO';
		}
		if(stato=='OK'){
			setValue("FILEDAALLEGARE" ,nome);
			document.getElementById("G1DOCSTIPULA_FORMATO").disabled = true;
			<c:if test="${firmaProvider eq 3}">
				gestioneCheckDaFirmare(1,1,1);
			</c:if>
			<c:if test="${isVerificaPDFA eq '1'}">
				if(nome.includes('.PDF')){
					checkValiditaPDFA(file);
				} else {
					$('#checkPDFaDocMessage').css("display", "none");
					$('#imgCheckPDFaDocMessage').css("display", "none");
					setValue("W_DOCDIG_DIGTIPDOC",null);
				}
			</c:if>
		}
		else{
			setValue("FILEDAALLEGARE","");
			document.getElementById("selezioneFile").value="";
			<c:if test="${isVerificaPDFA eq '1'}">
				$('#checkPDFaDocMessage').css("display", "none");
				$('#imgCheckPDFaDocMessage').css("display", "none");
				setValue("W_DOCDIG_DIGTIPDOC",null);
			</c:if>
			if(dignomdoc == null || dignomdoc == ""){
				document.getElementById("G1DOCSTIPULA_FORMATO").disabled = false;
			}
			if (formato == 1){
				formatoFileOnChangeInit(formato);
			} else {
				gestioneCheckDaFirmare(1,0,1);
			}
		}
	}

	function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
		var vet = dignomdoc.split(".");
		var ext = vet[vet.length-1];
		ext = ext.toUpperCase();
		<c:choose>
			<c:when test="${digitalSignatureWsCheck eq 0}">
				if(ext=='P7M' || ext=='TSD'){
					document.formVisFirmaDigitale.idprg.value = idprg;
					document.formVisFirmaDigitale.iddocdig.value = iddocdig;
					document.formVisFirmaDigitale.submit();
				}else{
					var href = "${contextPath}/pg/VisualizzaFileAllegato.do";
					document.location.href=href+"?idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
				}
			</c:when>
			<c:otherwise>
				if(ext=='P7M' || ext=='TSD' || ext=='XML' || ext=='PDF'){
					document.formVisFirmaDigitale.idprg.value = idprg;
					document.formVisFirmaDigitale.iddocdig.value = iddocdig;
					document.formVisFirmaDigitale.submit();
				}else{
					var href = "${contextPath}/pg/VisualizzaFileAllegato.do";
					document.location.href=href+"?idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
				}
			</c:otherwise>
	</c:choose>
	}
	
	function gestioneAllegato(visibilita,statodoc,modo) {
		if ((visibilita == '' || visibilita == '3') && (statodoc < 4)){
			$("#rowW_DOCDIG_DIGNOMDOC").hide();
			$("#selFile").hide();
			$("#rowselezioneFileOda").hide();
			$("#rowtipoAllegato").hide();
			$("#FILEDAALLEGARE").val('');
			cancellaFile();
		} else if((visibilita == '' || visibilita == '3') && (statodoc >= 4)){
			$("#selFile").hide();
			gestioneCheckDaFirmare(1,0,1);
		}else{
			<c:if test="${gestioneODA eq '1'}">
				var selectedValue = $('input[name="allegato"]:checked').val();
				if(selectedValue == undefined || selectedValue == '1'){
					$("#selFile").show();
				} else {
					$("#rowselezioneFileOda").show();
				}
			</c:if>
			<c:if test="${gestioneODA ne '1'}">
				$("#selFile").show();
			</c:if>
			$("#rowW_DOCDIG_DIGNOMDOC").show();
			$("#rowtipoAllegato").show();
			gestioneCheckDaFirmare(1,1,1);
		}
	}
		
	function cancellaFile(){
		setValue("W_DOCDIG_DIGNOMDOC", "");
		setValue("selezioneFile","");
		if (document.getElementById("G1DOCSTIPULA_FORMATO").value == 1){
			formatoFileOnChangeInit(1);
		} else {
			gestioneCheckDaFirmare(1,0,1);
		}
		setValue("FILECANCELLATO" ,"true");
		setValue("FILEDAALLEGARE","");
		document.getElementById("G1DOCSTIPULA_FORMATO").disabled = false;
		
	}
	
	var schedaConfermaDefault = schedaConferma;
	
	function schedaConfermaCustom() {
		document.getElementById("G1DOCSTIPULA_FORMATO").disabled = false;
		schedaConfermaDefault();
	}
	
	var schedaConferma = schedaConfermaCustom;
	
	function aggiornaRichiestaFirma(checkbox){
		if(checkbox.checked){
			setValue("W_DOCDIG_DIGFIRMA","1");
		}else{
			setValue("W_DOCDIG_DIGFIRMA","");
		}
	}
	
	
	function checkValiditaPDFA(file) {
		if(file != undefined){
			var formdata = false;
			if (window.FormData) {
				formdata = new FormData();
			}
			if (formdata) {
				formdata.append("file", file);
			}
			_wait();
			$.ajax({
				url: "CheckFormatoFilePDFa.do",
				data: formdata,
				enctype: 'multipart/form-data',
				type: 'POST',
				cache: false,
				contentType: false,
				processData: false,
				dataType: "json",
				success: function(res) {
					console.log(res);
					if(res.esito == 'ko'){
						$('#checkPDFaDocMessage').css("display", "contents");
						$('#imgCheckPDFaDocMessage').css("display", "none");
						setValue("W_DOCDIG_DIGTIPDOC","pdf");
					} else {
						$('#checkPDFaDocMessage').css("display", "none");
						$('#imgCheckPDFaDocMessage').css("display", "unset");
						setValue("W_DOCDIG_DIGTIPDOC","pdf/a");
					}
				},
				error: function(e) {
					alert('Errore nella verifica del file!');
				},
				complete: function() {
					_nowait();
				}
			});
		} else {
			$('#checkPDFaDocMessage').css("display", "none");
			$('#imgCheckPDFaDocMessage').css("display", "none");
			setValue("W_DOCDIG_DIGTIPDOC",null);
		}
	}
		
	function _wait() {
		document.getElementById('bloccaScreen').style.visibility = 'visible';
		$('#bloccaScreen').css("width", $(document).width());
		$('#bloccaScreen').css("height", $(document).height());
		document.getElementById('wait').style.visibility = 'visible';
		$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200 });
	}

	function _nowait() {
		document.getElementById('bloccaScreen').style.visibility = 'hidden';
		document.getElementById('wait').style.visibility = 'hidden';
	}	
	
	
<c:if test="${gestioneODA eq '1'}">

	function cambiaTipoAllegato(tipo, inizializza){
		var visibilita = $("#G1DOCSTIPULA_VISIBILITA").val();
		if (visibilita != '' && visibilita != '3'){
			if(tipo==1){
				$("#rowW_DOCDIG_DIGNOMDOC").show();
				if(inizializza==false){
					setValue("W_DOCDIG_DIGNOMDOC","");
					document.getElementById("G1DOCSTIPULA_FORMATO").disabled = false;
				}
				$("#selFile").show();
				$("#rowselezioneFileOda").hide();
				setValue("G1DOCSTIPULA_DOCPROV","");
			}else{
				if(tipo == 2){
					$("#rowW_DOCDIG_DIGNOMDOC").show();
					if(inizializza==false){
						setValue("W_DOCDIG_DIGNOMDOC","");
					}
					$("#selFile").hide();
					$("#checkPDFaDocMessage").hide();
					$("#imgCheckPDFaDocMessage").hide();
					$("input#selezioneFile").val('');
					$("#rowselezioneFileOda").show();
					document.getElementById("G1DOCSTIPULA_FORMATO").disabled = false;
				}else{
					
					setValue("W_DOCDIG_DIGNOMDOC","");
					setValue("W_DOCDIG_IDDOCDIG","");
					document.getElementById("selFile")? document.getElementById("selFile").value="" : null;
					$("#selFile").hide();
					$("#rowselezioneFileOda").hide();
				<c:if test="${param.richiestaFirma eq '1'}">
					setValue("W_DOCDIG_DIGFIRMA","");
					$("#richiestaFirma").removeAttr("checked");
				</c:if>
					}
				}
			}
		}
		
	var tipoDocumentoAllegato = 1;
	<c:if test='${!empty datiRiga.G1DOCSTIPULA_DOCPROV}'>
		tipoDocumentoAllegato = 2;
	</c:if>
	cambiaTipoAllegato(tipoDocumentoAllegato, true);


	function apriPopupSelezioneOda(){
		if(${numoda eq null || numoda eq ""}){
			alert("Numero ODA non presente, per inserirlo vai nella sezione dati generali nel menu a sinistra e clicca su Recupera Oda");
		} else {
			var href = "href=gare/g1docstipula/g1docstipula-popup-seleziona-da-oda.jsp?id=${idStipula}";
			openPopUpCustom(href, "insDocumentiPredefiniti", 700, 500, "no", "yes");
		}
	}
		

	function popolaFormWSERP(codiceOda,nomeallegato,idFile){
		$("#getdocumentoallegatoWSERP_codiceOda").val(codiceOda);
		$("#getdocumentoallegatoWSERP_nomeallegato").val(nomeallegato);
		$("#getdocumentoallegatoWSERP_idFile").val(idFile);
		setValue("G1DOCSTIPULA_DOCPROV","1");
		scegliFileDocumentale(nomeallegato);
	}
	
	
	
	function scegliFileDocumentale(param1) {
		var file = document.getElementById("selezioneFile").files[0];
		var selezioneFile = param1;
		var lunghezza_stringa=selezioneFile.length;
		var posizione_barra=selezioneFile.lastIndexOf("\\");
		var nomeUpper=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
		var nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa);
		var stato= 'OK';
		var formato=document.getElementById("G1DOCSTIPULA_FORMATO").value;
		var ext = nome.substring(nome.lastIndexOf('.')+1).toUpperCase();
		var dignomdoc= getValue("W_DOCDIG_DIGNOMDOC");
		if(nome.length>100){
			alert("Il nome del file non può superare i 100 caratteri!");
			stato='KO';
		}
		if (formato == 1 && (ext.indexOf('P7M')==-1 && ext.indexOf('TSD')==-1 && ext.indexOf('PDF')==-1 && ext.indexOf('XML')==-1)){
			alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: P7M;TSD;PDF;XML");
			stato='KO';
		}
		if (formato == 2 && (ext.indexOf('PDF')==-1 )){
			alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: PDF");
			stato='KO';
		}
		if (formato == 4 && (ext.indexOf('XLS')==-1 && ext.indexOf('XSLX')==-1 && ext.indexOf('ODS')==-1)){
			alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: XLS;XLSX;ODS");
			stato='KO';
		}
		if (formato == '' && dignomdoc == '' && nome == ''){
			stato='KO';
		}
		if(stato=='OK'){
			setValue("FILEDAALLEGARE" ,nome);
			setValue("W_DOCDIG_DIGNOMDOC",nome);
			document.getElementById("G1DOCSTIPULA_FORMATO").disabled = true;
			<c:if test="${firmaProvider eq 3}">
				gestioneCheckDaFirmare(1,1,1);
			</c:if>
			<c:if test="${isVerificaPDFA eq '1'}">
				if(nomeUpper.includes('.PDF')){
					checkValiditaPDFA(file);
				} else {
					$('#checkPDFaDocMessage').css("display", "none");
					$('#imgCheckPDFaDocMessage').css("display", "none");
					setValue("W_DOCDIG_DIGTIPDOC",null);
				}
			</c:if>
		}
		else{
			setValue("FILEDAALLEGARE","");
			document.getElementById("selezioneFile").value="";
			<c:if test="${isVerificaPDFA eq '1'}">
				$('#checkPDFaDocMessage').css("display", "none");
				$('#imgCheckPDFaDocMessage').css("display", "none");
				setValue("W_DOCDIG_DIGTIPDOC",null);
			</c:if>
			if(dignomdoc == null || dignomdoc == ""){
				document.getElementById("G1DOCSTIPULA_FORMATO").disabled = false;
			}
			if (formato == 1){
				formatoFileOnChangeInit(formato);
			} else {
				gestioneCheckDaFirmare(1,0,1);
			}
		}
	}
	
	
	
	
	
</c:if>

</gene:javaScript>

</gene:template>
