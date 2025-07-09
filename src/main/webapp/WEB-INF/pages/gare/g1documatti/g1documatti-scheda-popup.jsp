<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test="${modo eq 'NUOVO'}">
		<c:set var="keyParent" value="V_DITGPUB.NGARA=T:${param.ngara}" scope="request" />
	</c:when>
	<c:when test="${modo eq 'MODIFICA'}">
		<c:set var="key" value="G1DOCUMATTI.ID=T:${param.idDocumatti}" scope="request" />
	</c:when>
</c:choose>

<c:set var="formatoAllegati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "uploadFile.estensioniAmmesse")}'/>
<c:set var="isVerificaPDFA" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "uploadFile.verifica.pdfA")}'/>

<gene:template file="popup-template.jsp" gestisciProtezioni="false">

	<c:choose>
		<c:when test="${modo eq 'NUOVO'}">
			<gene:setString name="titoloMaschera" value="Aggiungi nuovo documento" />
		</c:when>
		<c:when test="${modo eq 'MODIFICA'}">
			<gene:setString name="titoloMaschera" value="Modifica documento" />
		</c:when>
	</c:choose>
	<gene:redefineInsert name="pulsanteNuovo" />

	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easytabs.js"></script>
	</gene:redefineInsert>
	<gene:redefineInsert name="corpo">

	<gene:formScheda entita="G1DOCUMATTI" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreG1DOCUMATTI">

		<c:if test="${modo eq 'MODIFICA'}">
			<gene:campoScheda campo="ID" visibile="false" />
			<gene:campoScheda campo="IDPRG" visibile="false" />
			<gene:campoScheda campo="IDDOCDIG" visibile="false" />
			<gene:campoScheda campo="STATODOC" visibile="false" />
			<gene:campoScheda campo="TIPOLOGIA" visibile="false" />
		</c:if>

		<gene:campoScheda campo="DIGDESDOC" entita="W_DOCDIG" where="G1DOCUMATTI.IDPRG=W_DOCDIG.IDPRG and G1DOCUMATTI.IDDOCDIG=W_DOCDIG.IDDOCDIG" title="Descrizione (*)" modificabile="${modo eq 'NUOVO'}"/>
		<gene:campoScheda id="campoUpload" title="Nome file (*)" >
			<input type="file" id="selezioneFile" name="selezioneFile" onchange="javascript:scegliFile();" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" />
			<span id="checkPDFaDocMessage" style="display:none; color: blue;"><b>ATTENZIONE:</b> file non conforme al formato PDF/A</span>
			<img id="imgCheckPDFaDocMessage" style="display:none;" src="${contextPath}/img/pdfa.png" title="File conforme al formato PDF/A" alt="File conforme al formato PDF/A">
		</gene:campoScheda>

		<gene:campoScheda campo="DIGTIPDOC" entita="W_DOCDIG" visibile="false" definizione="T2;0;;SN;DIGTIPDOC" />

		<gene:campoScheda campo="NGARA" value="${param.ngara}" visibile="false" />
		<gene:campoScheda campo="DITTAO" value="${param.dittao}" visibile="false" />

		<input type="hidden" name="nomeDocumento" id="nomeDocumento" value=""/>
		<input type="hidden" value="${keyParent}" name="keyParent" />
		<input type="hidden" value="${param.dittao}" name="dittao" />
		<input type="hidden" value="${param.ngara}" name="ngara" />
		<input type="hidden" value="${param.idDocumatti}" name="idDocumatti" />

		<gene:campoScheda>	
			<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
		</gene:campoScheda>

	</gene:formScheda>

</gene:redefineInsert>
<gene:javaScript>

	document.forms[0].encoding="multipart/form-data";

	function scegliFile() {
		
		var file = document.getElementById("selezioneFile").files[0];
		let selezioneFile = document.getElementById("selezioneFile").value;
		let lunghezza_stringa=selezioneFile.length;
		let posizione_barra=selezioneFile.lastIndexOf("\\");
		nome=selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
		var ext = nome.split(".")[1].toLowerCase();

		var formatoAllegati = '${formatoAllegati}';
		var esitoControlli = 'KO';
		if(!controlloTipoFile(nome,formatoAllegati)){
			alert("Il formato del file selezionato non è valido.\nI formati consentiti sono: " + formatoAllegati);
			document.getElementById("selezioneFile").value = "";
		} else if(!isValidFilename(nome)) {
			alert("Il nome del file contiene caratteri speciali non consentiti!\nUnici caratteri speciali ammessi sono -_()[]");
			document.getElementById("selezioneFile" ).value = "";
		} else if(nome.length>100){
			alert("Il nome del file non può superare i 100 caratteri!");
			document.getElementById("selezioneFile").value = "";
		} else{
			setValue("W_DOCDIG_DIGTIPDOC",ext);
			var esitoControlli = 'OK';
			setValue("FILEDAALLEGARE" ,nome);
			<c:if test="${isVerificaPDFA eq '1'}">
				if(nome.includes('.PDF')){
					checkValiditaPDFA(file);
				} else {
					$('#checkPDFaDocMessage').css("display", "none");
					$('#imgCheckPDFaDocMessage').css("display", "none");
				}
			</c:if>
			$('#nomeDocumento').val(nome);
		}
		if (esitoControlli == 'KO') {
			setValue("FILEDAALLEGARE", '');
			$('#checkPDFaDocMessage').css("display", "none");
			$('#imgCheckPDFaDocMessage').css("display", "none");
			setValue("W_DOCDIG_DIGTIPDOC",null);
		}
	}

	function controlloTipoFile(nomeFile, estensioniConsentite){
		console.log('estensioniConsentite: ' + estensioniConsentite);
		console.log('nomeFile: ' + nomeFile);
		var estensioneValida=false;
		if(estensioniConsentite!=null && estensioniConsentite!=""){
			var estensioni=estensioniConsentite.split(";");
			nomeFile = nomeFile.toUpperCase();
			if(nomeFile==null || nomeFile==""){
				estensioneValida=true;
			}else{
				for(var i=0; i < estensioni.length; i++){
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

	function isValidFilename(filename) {
		var reg = new RegExp("^[A-Za-z0-9-_()\\s\\.\\[\\]]+$", "g");
		return (reg.test(filename));
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

	function schedaAnnulla() {
		window.close();
	}

	var schedaConfermaOld=schedaConferma;
	
	schedaConferma=function(){
		if ('' === document.getElementById("W_DOCDIG_DIGDESDOC").value) {
			alert('Inserire una descrizione');
		} else if ('' === document.getElementById("selezioneFile").value) {
			alert('Inserire un documento');
		} else {
			schedaConfermaOld();
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

	<c:if test='${RISULTATO eq "DOCUMENTOAGGIUNTO"}'>
		window.opener.historyReload();
		window.close();
	</c:if>

</gene:javaScript>
</gene:template>

