<%
/*
 * Created on: 09-12-2021
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

<jsp:include page="/WEB-INF/pages/commons/jsSubMenuComune.jsp" />

<c:set var="propertyDocAss" value='${not empty gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction",  "it.eldasoft.documentiAssociati")}' scope="request"/>

<div style="width:97%;">

	<gene:template file="popup-template.jsp">
		
		<gene:redefineInsert name="addHistory" />
		<gene:redefineInsert name="gestioneHistory" />
		<gene:setString name="titoloMaschera" value='Importa CIG e smartCIG da excel ANAC' />
					
		<gene:redefineInsert name="head">
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
		</gene:redefineInsert>
					
		<gene:redefineInsert name="corpo">
			
			<table class="dettaglio-notab">
				<tr id="sezioneIniziale">
					<td class="valore-dato" colspan="2">
						<br>
						Questa funzione permette di creare degli appalti ai fini delle pubblicazioni per la trasparenza partendo dai dati contenuti nei file Excel forniti da ANAC riferiti a:
						<ul>
							<li>Elenco CIG SIMOG emessi e perfezionati</li>
							<li>Elenco SmartCIG</li>
						</ul>
						La procedura di caricamento aggiunger&agrave; delle procedure d&#39;appalto in banca dati solo per i CIG che non sono gi&agrave; presenti, inserendo i dati disponibili nei fogli Excel.
						I file Excel devono rispettare una struttura predefinita e i dati verranno caricati secondo specifiche regole.<br>
						La procedura di caricamento avverr&agrave; in background e al termine verr&agrave; trasmessa una notifica.<br>
						Eventuali errori rilevati durante il caricamento potranno essere visualizzati in apposito log.<br>
						Per ulteriori dettagli su vincoli, regole di caricamento, notifiche e tracciamento degli errori si rimanda al manuale.								
						<a href="javascript:apriDocumento('${pageContext.request.contextPath}/doc/manuale_import_excel_anac.pdf');" title="Consulta manuale" style="color:#002E82;">
							Consulta manuale.
						</a>
						<br><br>
						Scegli il tipo di file da importare:
						<br><br>
						&nbsp;<input type="radio" name="modstip" id="radiomodstip1" value="1" checked="checked" />&nbsp;<b>Elenco CIG SIMOG emessi e perfezionati</b>
						<br>
						&nbsp;<input type="radio" name="modstip" id="radiomodstip2" value="2" />&nbsp;<b>Elenco SmartCIG</b>
						<br><br>
						<input type="file" name="selezioneFile" id="selezioneFile" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" />
						<br><br>
					</td>
				</tr>
				
				<tr id="sezioneAvanzamento" style="display: none;">
					<td colspan="2">
						<br>
						Attendere il completamento dell'operazione
						<br>
						<br>
						<div id="avanzamento"><div id="avanzamento-label"></div></div>
						<br>
					</td>
				</tr>
				
				<tr id="sezioneFinale" style="display: none;">
					<td colspan="2">
						<br>
						Il file &egrave; stato caricato ed &egrave; stato avviato il processo di importazione
						<br>
						<br>								
					</td>
				</tr>
				
				<tr id="sezioneErrore" style="display: none;">
					<td colspan="2">
						<br>
						<span id="msgEsitoErrore"></span>
						<br>
						<br>
					</td>
				</tr>
				
				<tr id="sezioneComandiIniziali">
					<td colspan="2" class="comandi-dettaglio">
						<INPUT type="button" class="bottone-azione" value="Avvia processo di importazione" title="Avvia processo di importazione" onclick="javascript:acquisisciAJAX();">
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
			
			function chiudi(){
				window.close();
			}
			
			function acquisisciAJAX(){	
			
				var file = document.getElementById("selezioneFile").files[0];
				var nomeFile = "";
				
				if(file!=null)
				nomeFile = document.getElementById("selezioneFile").files[0].name.toUpperCase();
			
				if(!${propertyDocAss}){
					alert("Non \u00E8 stato configurato il path dei documenti associati!");
				}
				else if(file==null){
					alert("Non \u00E8 stato selezionato alcun file!");
				}
				else if(nomeFile.lastIndexOf("XLSX")<0){
					alert("E' stato selezionato un file non valido. Il file deve avere estensione .xlsx");
				}
				else{
					var tipoElenco = "";
					
					if($("#radiomodstip1").prop('checked')){
						tipoElenco = "1";
					}else{
						if($("#radiomodstip2").prop('checked')){
						tipoElenco = "2";
					}}	
				
					$("#sezioneIniziale").hide();
					$("#sezioneComandiIniziali").hide();
				
					var syscon = "${ sessionScope.profiloUtente.id}";
				
					var formData = new FormData();
					formData.append('selezioneFile', file);
					formData.append('tipoElenco', tipoElenco);
					formData.append('syscon', syscon);	
				
					$.ajax({
						data: formData,
						enctype: 'multipart/form-data',
						type: 'POST',
						cache: false,
						contentType: false,
						processData: false,
						dataType: "json",
						beforeSend: function(x) {
							if(x && x.overrideMimeType) {
								x.overrideMimeType("application/json;charset=UTF-8");
							}
						},
						url: "pg/ImportExcelANAC.do?",
						success: function(res){
							msgEsito = res.MsgErrore;
							esito= res.Esito;
						},
						error: function(e){
							alert("Errore generico durante l'operazione");
						},
						complete: function(jqXHR, textStatus) {
							if(esito=="Errore"){
								$("#msgEsitoErrore").html(msgEsito);
								$("#sezioneComandiFinali").show();
								$("#sezioneErrore").show();	
							}else{
								$("#sezioneFinale").show();
								$("#sezioneComandiFinali").show();
							}
						}
					});
				}
				
			}		
		
		</gene:javaScript>	
	</gene:template>

</div>


