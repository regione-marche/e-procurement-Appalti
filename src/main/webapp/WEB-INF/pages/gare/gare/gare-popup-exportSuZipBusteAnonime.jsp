<%
/*
 * Created on: 28-07-2022
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

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<div style="width:97%;">

	<gene:template file="popup-template.jsp">
	
		<gene:redefineInsert name="head">
			<script type="text/javascript" src="${contextPath}/js/common-gare.js"></script>		
		</gene:redefineInsert>
		
		<c:choose>
			<c:when test='${not empty param.ngara}'>
				<c:set var="ngara" value="${param.ngara}" />
			</c:when>
			<c:otherwise>
				<c:set var="ngara" value="${ngara}" />
			</c:otherwise>
		</c:choose>
		
		<gene:redefineInsert name="addHistory" />
		<gene:redefineInsert name="gestioneHistory" />
		<gene:setString name="titoloMaschera" value='Scarica su zip buste tecniche in forma anonima' />
		
		<c:set var="valoriStato" value="'5','7'"/>		
		<c:set var="esistonoAcquisizioniDaElaborare" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteInStatoDaElaborareFunction", pageContext, ngara, "FS11B" ,valoriStato)}' />
				
		
		<gene:redefineInsert name="corpo">
		
			<input type="hidden" id="ngara" value="${ngara}" />
			
			
			<table class="dettaglio-notab">
				<tr id="sezioneIniziale">
					<td class="valore-dato" colspan="2">
						<br>
						<c:choose>
							<c:when test="${esistonoAcquisizioniDaElaborare eq 'true'}">
								Non &egrave; possibile procedere con lo scarico su file zip dei documenti perch&egrave; devono essere prima acquisite le buste tecniche.
								<br>Attivare la funzione "Acquisisci buste tecniche in forma anonima" nella pagina corrente. 
								<br><br>
							</c:when>	
							<c:otherwise>
								Mediante questa funzione &egrave; possibile scaricare su file zip
								i documenti della busta tecnica di tutte le ditte in gara in forma anonima.
								<br><br>
								Confermi l'operazione ?
							</c:otherwise>
						</c:choose>
						
						<br>
						<br>
					</td>
				</tr>
				
							
				<tr id="sezioneAvanzamento" style="display: none;">
					<td colspan="2">
						<br>
						Attendere il completamento dell'operazione di scarico su file zip delle buste tecniche in forma anonima
						<br>
						<br>
						<div id="avanzamento"><div id="avanzamento-label"></div></div>
						<br>
					</td>
				</tr>
				
				<tr id="sezioneCodifica" style="display: none;">
					<td colspan="2">
						<br>
						<span id="msgEsito">Codifica anonima degli operatori in corso...</span>
						<br>
					</td>
				</tr>
				
				<tr id="sezioneFinale" style="display: none;">
					<td colspan="2">
						<br>
						<span id="msgEsito">L'operazione &egrave; stata eseguita con successo</span>.
						<br>
						<br>
					</td>
				</tr>
		
				<tr id="sezioneErrore" style="display: none;">
					<td colspan="2">
						<br>
						<span id="msgEsitoErrore">Si è presentato un errore durante l'esecuzione dell'operazione.<br>
						Controllare il log per i dettagli</span>
						<br>
						<br>
					</td>
				</tr>
		
				<tr id="sezioneComandiIniziali">
					<td colspan="2" class="comandi-dettaglio">
						<c:if test="${esistonoAcquisizioniDaElaborare ne 'true'}">
							<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:criptazione();">
						</c:if>
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:chiudi();">&nbsp;&nbsp;
					</td>
				</tr>
				
				<tr id="sezioneComandiErrori" style="display: none;">
					<td colspan="2" class="comandi-dettaglio">
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:chiudi();">&nbsp;&nbsp;
					</td>
				</tr>
				
				<tr id="sezioneComandiFinali" style="display: none;">
					<td colspan="2" class="comandi-dettaglio">
						<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
					</td>
				</tr>
				
			</table>
			
		</gene:redefineInsert>
		
		<gene:javaScript>
			var archivioCreato = "false";
								
			function chiudi(){
				window.close();
			}
			
			function inizializzaAvanzamento() {
			
				$(".ui-progressbar").css("position","relative");
				$("#avanzamento-label").css("position","absolute");
				$("#avanzamento-label").css("left","45%");
				$("#avanzamento-label").css("padding-top","4px");
				$("#avanzamento-label").css("font-weight","bold");
				$("#avanzamento-label").css("font","10px Verdana, Arial, Helvetica, sans-serif");
				$("#avanzamento-label").css("text-shadow","1px 1px 0 #fff");
			
				$("#sezioneAvanzamento").show();
				$("#avanzamento-label").text("0 %");
				$( "#avanzamento" ).progressbar({
				  value: 0
				});
			}
			
					
			
			function criptazione(){
				var ngara = $("#ngara").val();
				$("#sezioneIniziale").hide();
				$("#sezioneComandiIniziali").hide();
				$("#sezioneCodifica").show();
				_wait();
				$.ajax({
					type: "POST",
					dataType: "json",
					async: false,
					beforeSend: function(x) {
						if(x && x.overrideMimeType) {
							x.overrideMimeType("application/json;charset=UTF-8");
						}
					},
					url:  "pg/CodificaAnonimaDitte.do?ngara=" + ngara ,
					success: function(res){
						esito= res.Esito;
						msgEsito = res.MsgErrore;
						
						
					},
					error: function(e){
						alert("Errore generico durante l'operazione");
					},
					complete: function(jqXHR, textStatus) {
						_nowait();
						$("#sezioneCodifica").hide();
						if(textStatus=="error"){
							$("#sezioneErrore").show();
							$("#sezioneComandiFinali").show();
                        }else if(esito=="Errore"){
							$("#msgEsitoErrore").html(msgEsito);
							$("#sezioneComandiFinali").show();
                        	$("#sezioneErrore").show();	
						}else{
							inizializzaAvanzamento();
							downloadDocumentazione(ngara);
						}
						
					}
				});
			}
			
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
			
			function downloadDocumentazione(ngara) {
				$.ajax({
					type: "POST",
					url: "pg/ScaricaDocumentiTecAnonimi.do",
					data: {
						ngara: ngara,
						metodo: 'getListaDocumenti'
					},
					async: true,
					dataType: "json",
					success:function(data){			
						getPath(ngara,data.maxCount,data.listaDoc);
					},
					error: function(e){
						alert("Si è verificato un problema");
						$("#sezioneComandiErrori").show();
					}
				});
			
			return;
			
			}
			
			function getPath(ngara,maxCount,listaDoc) {
				var documenti = {"documenti":[]};
				var numDocDitta=0;
				for(i=0; i < maxCount; i++){
					var idprgDoc = listaDoc[i][0].value;
					var iddocdg = listaDoc[i][1].value;
					var nomeDoc = listaDoc[i][2].value;
					var codimp = listaDoc[i][3].value;
					var idanonimo = listaDoc[i][4].value;	
					var nuovaDitta = "0";
					if(i==0)
						nuovaDitta = "1";
					else if(listaDoc[i-1][3].value != codimp)
						nuovaDitta = "1";
					if(nuovaDitta=="1")
						numDocDitta=0;
					numDocDitta+=1;		
					documenti.documenti.push({
					    "idprg": idprgDoc, 
					    "iddocdg":iddocdg, 
					    "nomeDoc":nomeDoc,
						"codimp":codimp,
						"idanonimo":idanonimo,
						"nuovaDitta":nuovaDitta,
						"numDocDitta" : numDocDitta
					  });
				}
				
				var documentiStringa = JSON.stringify(documenti);
				
				$.ajax({
					type: "POST",
					url: "pg/ScaricaDocumentiTecAnonimi.do",
					data: {
						ngara: ngara,
						documenti: documentiStringa,
						metodo: 'getPath'
					},
					async: true,
					dataType: "json",
					success:function(data){
						ajaxCallDownload(0,data.path,maxCount,documenti);
					},
					error: function(e){
						alert("Si è verificato un problema");
						$("#sezioneAvanzamento").hide();
						$("#sezioneComandiErrori").show();
					}
				});
			
			return;
			
		}
		
		function ajaxCallDownload(counter,path,maxCount,listaDoc){
			
			if((!maxCount || maxCount == 0) || (counter > maxCount && archivioCreato == "false")){
				alert("Non è presente nessun documento da esportare");
				return window.close();
			}else{
				if(counter > maxCount && archivioCreato == "true"){
				document.location.href='pg/ScaricaDocumentiTecAnonimi.do?&metodo=download&path='+encodeURIComponent(path);
				$("#sezioneAvanzamento").hide();
				$("#sezioneFinale").show();
				$("#sezioneComandiFinali").show();
				return ;
			}
			
			var datiDoc = listaDoc.documenti[counter];
			var idprgDoc = datiDoc.idprg;
			var iddocdg = datiDoc.iddocdg;
			var nomeDoc = datiDoc.nomeDoc;
			var codimp = datiDoc.codimp;
			var idanonimo = datiDoc.idanonimo;	
			var nuovaDitta = datiDoc.nuovaDitta;
			var numDocDitta = datiDoc.numDocDitta;
										
			if((!iddocdg || !idprgDoc)){
				return ajaxCallDownload(counter+1,path,maxCount,listaDoc);
				}
			}
			
			$.ajax({
				type: "POST",
				url: "pg/ScaricaDocumentiTecAnonimi.do",
				data: {
					path: path,
					idddocdg: iddocdg,
					idprg: idprgDoc,
					nomeDoc: nomeDoc,
					codimp: codimp,
					idanonimo: idanonimo,
					nuovaDitta: nuovaDitta,
					numDocDitta: numDocDitta,
					archivioCreato: archivioCreato,
					metodo: 'creaArchivio'
				},
				async: true,
				dataType: "json",
				success:function(data){
					archivioCreato = "true";
					counter++;
					var perc = Math.round((counter / maxCount) * 100);
					$("#avanzamento-label").html(perc + "%");
					$( "#avanzamento" ).progressbar({
					  value: perc
					});
					if (counter < maxCount){
						ajaxCallDownload(counter,path,maxCount,listaDoc);
					}else{
						document.location.href='pg/ScaricaDocumentiTecAnonimi.do?&metodo=download&path='+encodeURIComponent(path);
						$("#sezioneAvanzamento").hide();
						$("#sezioneFinale").show();
						$("#sezioneComandiFinali").show();
					}
				}
			});
		}
			
		</gene:javaScript>	
	</gene:template>

</div>


