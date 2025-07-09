<%
/*
 * Created on: 05-08-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 *
 * Popup per campi ulteriori relativi alla ditta presenta nella lista delle
 * fasi di ricezione in analisi
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<% // Validazione parametri tramite regex %>
<c:if test='${not empty param.stepWizard and gene:matches(param.stepWizard, "^-?[\\\\.0-9]+$", true)}' />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="isVerificaPDFA" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "uploadFile.verifica.pdfA")}'/>

<c:choose>
	<c:when test='${not empty param.stepWizard}'>
		<c:set var="stepWizard" value="${param.stepWizard}" />
	</c:when>
	<c:otherwise>
		<c:set var="stepWizard" value="${stepWizard}" />
	</c:otherwise>
</c:choose>

<div style="width:97%;">
<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="corpo">
		<gene:setString name="titoloMaschera" value='Inserimento documentazione' />
		<gene:formScheda entita="IMPRDOCG" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreVerificaDocumenti" >
			<gene:campoScheda campo="CODGAR" visibile="false" />
			<gene:campoScheda campo="CODIMP"  visibile="false" />
			<gene:campoScheda campo="NORDDOCI" visibile="false"/>
			<gene:campoScheda campo="NGARA" visibile="false"/>
			<gene:campoScheda campo="DESCRIZIONE" />
			<gene:campoScheda campo="DATARILASCIO" />
			<gene:campoScheda campo="ORARILASCIO" />
			<gene:campoScheda campo="DATASCADENZA" />
			<gene:campoScheda campo="SITUAZDOCI" />
			<gene:campoScheda campo="NOTEDOCI" />
			<gene:campoScheda campo="PROVENI" visibile="false" defaultValue="2"/>
			<gene:campoScheda title="Nome file" >
				<input type="file" name="selezioneFile" id="selezioneFile" onchange="javascript:scegliFile(this.value);" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" value=''/>
				<span id="checkPDFaDocMessage" style="display:none; color: blue;"><b>ATTENZIONE:</b> file non conforme al formato PDF/A</span>
				<img id="imgCheckPDFaDocMessage" style="display:none;" src="${contextPath}/img/pdfa.png" title="File conforme al formato PDF/A" alt="File conforme al formato PDF/A">
			</gene:campoScheda>
			<gene:campoScheda title="File da allegare" campo="FILEDAALLEGARE" campoFittizio="true" visibile="false" definizione="T70;0" />
			<gene:campoScheda title="Tipo Documento" campo="W_DOCDIG_DIGTIPDOC" campoFittizio="true" visibile="false" definizione="T70;0" />
			<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:window.close();">&nbsp;
				</td>
			</gene:campoScheda>
			<input type="hidden" name="stepWizard" id="stepWizard" value="${stepWizard }" />
		</gene:formScheda>
	</gene:redefineInsert>

	<gene:javaScript>
	document.forms[0].jspPathTo.value="gare/imprdocg/imprdocg-schedaPopup-insert.jsp";
	var openerKeyParent = window.opener.document.forms[0].keyParent.value;
	
	var campiChiave = openerKeyParent.split(";");
	var codiceGara= campiChiave[0].substr(openerKeyParent.indexOf(":")+1);
	var codiceDitta= campiChiave[1].substr(openerKeyParent.indexOf(":"));
	var numeroGara= campiChiave[2].substr(openerKeyParent.indexOf(":"));
		
	setValue("IMPRDOCG_CODGAR", codiceGara);
	setValue("IMPRDOCG_CODIMP", codiceDitta);
	setValue("IMPRDOCG_NGARA", numeroGara);
	
	document.forms[0].encoding="multipart/form-data";
	
	function scegliFile(valore) {
		var file = document.getElementById("selezioneFile").files[0];
		selezioneFile = document.getElementById("selezioneFile").value;
		var lunghezza_stringa = selezioneFile.length;
		var posizione_barra = selezioneFile.lastIndexOf("\\");
		var nome = selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
		if(nome.length>100){
			alert("Il nome del file non può superare i 100 caratteri!");
			document.getElementById("selezioneFile").value="";
			setValue("FILEDAALLEGARE","");
			<c:if test="${isVerificaPDFA eq '1'}">
				$('#checkPDFaDocMessage').css("display", "none");
				$('#imgCheckPDFaDocMessage').css("display", "none");
			</c:if>
			setValue("W_DOCDIG_DIGTIPDOC",null);
		}else{
			setValue("FILEDAALLEGARE" ,nome);
			<c:if test="${isVerificaPDFA eq '1'}">
				if(nome.includes('.PDF')){
					checkValiditaPDFA(file);
				} else {
					$('#checkPDFaDocMessage').css("display", "none");
					$('#imgCheckPDFaDocMessage').css("display", "none");
				}
			</c:if>
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
	
	<c:if test='${not empty RISULTATO and RISULTATO eq "OK"}' >
		window.opener.document.forms[0].pgSort.value = "";
		window.opener.document.forms[0].pgLastSort.value = "";
		window.opener.document.forms[0].pgLastValori.value = "";
		window.opener.bloccaRichiesteServer();
		window.opener.listaVaiAPagina(0);
		window.close();
	</c:if>


	var schedaConferma_Default = schedaConferma;
	function schedaConferma_Custom(){
		var selezioneFile = document.getElementById("selezioneFile").value;
		var fileDaAllegare = getValue("FILEDAALLEGARE");
		if(fileDaAllegare!=null && fileDaAllegare!="" && selezioneFile==null || selezioneFile =="")
			setValue("FILEDAALLEGARE","");
		schedaConferma_Default();
	}
	
	schedaConferma =   schedaConferma_Custom;
	</gene:javaScript>
</gene:template>
</div>