<%
	/*
	 * Created on: 13-11-2013
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
	/* Popup per l'importazione dati dal modello excel degli adempimenti di legge 190 */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<gene:template file="popup-message-template.jsp">
	
		<c:choose>
		<c:when test='${not empty param.codgar}'>
			<c:set var="codgar" value="${param.codgar}" />
		</c:when>
		<c:otherwise>
			<c:set var="codgar" value="${codgar}" />
		</c:otherwise>
		</c:choose>
		
		<c:choose>
		<c:when test='${not empty param.ngara}'>
			<c:set var="ngara" value="${param.ngara}" />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${ngara}" />
		</c:otherwise>
		</c:choose>
		
		<c:choose>
		<c:when test='${not empty param.isGaraLottiConOffertaUnica}'>
			<c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
		</c:when>
		<c:otherwise>
			<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
		</c:otherwise>
		</c:choose>
		
		<gene:redefineInsert name="gestioneHistory" />
		<gene:redefineInsert name="addHistory" />
		<gene:setString name="titoloMaschera" value="Importazione da formato M-Appalti delle ditte nella gara ${ngara}"/>

		<gene:redefineInsert name="corpo">
		
		<c:choose>
		<c:when test="${param.dittePresenti eq 'true'}">
		<br>
		<span>Non è possibile procedere con l'importazione in quanto ci sono già delle ditte in gara.</span>
		<br>
		<br>
		<gene:redefineInsert name="buttons">
			<input type="button" class="bottone-azione"  id="cancel" value="Chiudi"	title="Chiudi" onclick="chiudi();"/>&nbsp;&nbsp;
		</gene:redefineInsert>
		
		<gene:javaScript>
			
			function chiudi(){
				window.close();
			}
			
		</gene:javaScript>
		
		</c:when>
		<c:when test="${esito eq 'ok'}">
		<br>
		<span><b>Importazione delle ditte completata</b></span>
		<br>
		<br>Numero ditte inserite in gara: ${countImport}.
		<br>Numero ditte inserite in anagrafica: ${countAnagrafica}.
		<br>
		<c:if test='${not empty messageError}'>
			<br>
			<span style="color:red">${messageError}</span>
			<br>
		</c:if>
		<br>
		<gene:redefineInsert name="buttons">
			<input type="button" class="bottone-azione"  id="cancel" value="Chiudi"	title="Chiudi" onclick="chiudi();"/>&nbsp;&nbsp;
		</gene:redefineInsert>
		
		<gene:javaScript>
			
			function chiudi(){
				window.close();
			}
			
			window.onload = function () { 
				window.opener.bloccaRichiesteServer();
				window.opener.location = "${contextPath}/History.do?"+csrfToken+"&metodo=reload";
			}
		</gene:javaScript>
		
		</c:when>
		<c:otherwise>
		
		<c:set var="modo" value="MODIFICA" scope="request" />
		
		<gene:formScheda entita="DITG" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupImportaDitteDaFile">
			<c:if test="${esito eq 'ko'}">
			<span style="color:red">${errorMessage}</span>
			</c:if>
			<input type="hidden" id="ngara" name="ngara" value="${ngara}"/>
			<input type="hidden" id="codgar" name="codgar" value="${codgar}"/>
			<input type="hidden" id="isGaraLottiConOffertaUnica" name="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}"/>
			<gene:campoScheda campo="NGARA5" visibile="false" value="${ngara}" />
			<gene:campoScheda>
			<div id="file-container">
				<br>
				<span>Mediante questa funzione è possibile inserire in gara le ditte da invitare mediante importazione da file interoperabile prodotto secondo lo standard M-Appalti. Il file ha estensione JSON.</span>
				<br><br><br>
				Selezionare il file da importare (con estensione JSON):<br>
				<input type="file" style="width: 400px;" id="selezioneFile" name="selezioneFile" class="file"/>
				<div id="messaggioErroreContainer">
				<br><br>
				<span id="messaggioErrore" style="color:red"></span>
				<br>
				</div>
			</div>
			</gene:campoScheda>
		</gene:formScheda> 
		<div id="importResult">
				<br>
				<span>Dati della gara da cui è stata prodotta l'esportazione:</span>
				<br><br>
				<span><b>Stazione appaltante: </b></span><span id="stazioneAppaltanteDesc"></span><br>
				<span><b>Codice fiscale stazione appaltante: </b></span><span id="stazioneAppaltanteCF"></span><br>
				<span><b>Oggetto: </b></span><span id="oggetto"></span><br>
				<span><b>Tipo di appalto: </b></span><span id="tipoAppaltoDesc"></span><br>
				<span><b>Tipo di procedura: </b></span><span id="tipoProceduraDesc"></span><br>
				<span><b>Categoria prevalente: </b></span><span id="categoria"></span><br>
				<span><b>Classifica: </b></span><span id="classeCategoriaDesc"></span><br>
				<span><b>Importo a base di gara: </b></span><span id="importo"></span><br>
				<br>
				<div id="messaggioContainer">
					<span id="messaggio" style="color:blue"></span>
					<br>
				</div>
				<br>
				<span>Vuoi procedere con l'operazione?</span>
		</div>					
		<br>	
		<gene:redefineInsert name="buttons">
			<input type="button" class="bottone-azione" id="confirm" value="Conferma" title="Conferma"	onclick="conferma();"/>
			<input type="button" class="bottone-azione"  id="cancel" value="Chiudi"	title="Chiudi" onclick="chiudi();"/>&nbsp;&nbsp;
		</gene:redefineInsert>

		</c:otherwise>
		</c:choose>
		
			</gene:redefineInsert>

	<gene:javaScript>
		
		$("#importResult").hide();
		$("#messaggioContainer").hide();
		$("#messaggioErroreContainer").hide();
		
		function conferma() {

			var nomeCompletoFile = $("#selezioneFile").val();
			if(nomeCompletoFile != ""){
				var estensione = nomeCompletoFile.substr(nomeCompletoFile.lastIndexOf('.') + 1).toUpperCase();
				if(estensione == "JSON" || estensione == "json") {
					checkDati();
				} else {
					alert("E' stato selezionato un file non valido.\nSelezionare un file con estensione JSON");
				}
			} else {
				alert("Non è stato selezionato alcun file");
			}
		}
		
		function importa(){
			document.forms[0].encoding="multipart/form-data";  
			document.forms[0].jspPathTo.value="/WEB-INF/pages/gare/gare/popup-import-ditte-gara.jsp";
			schedaConferma();
		}
		
		function checkDati() {
			_wait();
			var codgar = "${codgar}";
			var ngara = "${ngara}";
			var nomeCompletoFile = $("#selezioneFile").val();
			var file = document.getElementById("selezioneFile").files[0];
			var formData = new FormData();
			formData.append('codgar', codgar);
			formData.append('ngara', ngara);
			formData.append('file', file);
			
			$.ajax({
				url: "${pageContext.request.contextPath}/pg/ImportDitteInGara.do",
				data: formData,
				enctype: 'multipart/form-data',
				type: 'POST',
				cache: false,
				contentType: false,
				processData: false,
				dataType: "json",
				success:function(data){
					if(data.esito == "ko"){
						$("#messaggioErrore").html(data.messaggio);
						$("#messaggioErroreContainer").show();
					}else{
						$("#importResult").show();
						$("#file-container").hide();
						
						$("#stazioneAppaltanteDesc").html(data.stazioneAppaltanteDesc);
						$("#stazioneAppaltanteCF").html(data.stazioneAppaltanteCF);
						$("#oggetto").html(data.oggetto);
						$("#tipoAppaltoDesc").html(data.tipoAppaltoDesc);
						$("#tipoProceduraDesc").html(data.tipoProceduraDesc);
						$("#categoria").html(data.categoriaCod + " - " + data.categoriaDesc);
						$("#classeCategoriaDesc").html(data.classeCategoriaDesc);
						$("#importo").html(data.importo);
						conferma = importa;
						
						$("#messaggio").html(data.messaggio);
						$("#messaggioContainer").show();
					}
					_nowait();
				},
				error:function(error){
					$("#messaggioErrore").html("Errore nella lettura dei dati. Il file selezionato non è conforme al formato M-Appalti.");
					$("#messaggioErroreContainer").show();
					_nowait();
				}
			});
		}	
		
		function chiudi(){
			//window.opener.historyReload();
			window.close();
		}
		
		function _nowait() {
			document.getElementById('bloccaScreen').style.visibility='hidden';
			document.getElementById('wait').style.visibility='hidden';
		}
		
		function _wait() {
			document.getElementById('bloccaScreen').style.visibility='visible';
			$('#bloccaScreen').css("width",$(document).width());
			$('#bloccaScreen').css("height",$(document).height());
			document.getElementById('wait').style.visibility='visible';
			$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
		}

	</gene:javaScript>
		
</gene:template>