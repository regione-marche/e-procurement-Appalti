<%
/*
 * Created on: 18-06-2014
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
				
		<c:choose>
			<c:when test='${not empty param.codgar}'>
				<c:set var="codgar" value="${param.codgar}" />
			</c:when>
			<c:otherwise>
				<c:set var="codgar" value="${codgar}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.dittao}'>
				<c:set var="dittao" value="${param.dittao}" />
			</c:when>
			<c:otherwise>
				<c:set var="dittao" value="${dittao}" />
			</c:otherwise>
		</c:choose>
		
		<gene:redefineInsert name="addHistory" />
		<gene:redefineInsert name="gestioneHistory" />
		<gene:setString name="titoloMaschera" value='Acquisizione busta per prequalifica' />
		
		<c:set var="cifraturaBuste" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBustaCifrataFunction", pageContext, ngara, "FS10A")}'/>
						
		<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext,ngara,codgar,dittao)}' />
		
		<gene:redefineInsert name="corpo">
		
			<input type="hidden" id="ngara" value="${ngara}" />
			<input type="hidden" id="codgar" value="${codgar}" />
			<input type="hidden" id="dittao" value="${dittao}" />
									
			<table class="dettaglio-notab">
				<tr id="sezioneIniziale">
					<td class="valore-dato" colspan="2">
						<br>
						Mediante questa funzione &egrave; possibile procedere all'acquisizione della busta per prequalifica della ditta '${nomimo}'.
						<br><br>
						Confermi l'operazione ?
						<br>
						<br>
					</td>
				</tr>
				
				
				<c:if test="${cifraturaBuste eq 'true'}">
						<tr id="rowPWD">
							<td id="etichettaPassword" class="etichetta-dato">Password per la decifratura della busta (*)</td>
							<td class="valore-dato">
								<input id="PWD" name="PWD" title="Password per la decifratura della busta" class="testo" type="password" size="24" value="${sessionScope.passBustePreq }" maxlength="100" />
								<a href="javascript:usaScanner()"><img src="${pageContext.request.contextPath}/img/barcode.png" title="Usa lettore codice a barre" alt="Usa lettore codice a barre" width="15" height="15"></a> 
								<span id="errorePwd" style="display:none;color: red;">
									<b>Password errata</b> 
								</span>
							</td>
						</tr>
						<tr id="rowPWDcryptata" style="display: none;">
							<td id="etichettaPasswordCryptata" class="etichetta-dato">Password per la decifratura della busta (da codice a barre)(*)</td>
							<td class="valore-dato">
								<input id="PWDcryptata" name="PWD" title="Password da barcode scanner" class="testo" type="password" size="24" maxlength="100" autocomplete="off"/>
								<a href="javascript:cancellaBarcode()"><img src="${pageContext.request.contextPath}/img/barcodeStop.png" title="Torna alla digitazione da tastiera" alt="Torna alla digitazione da tastiera" width="15" height="15"></a> 
								<span id="errorePwd" style="display: none;color: red;">
									<b>Password errata</b> 
								</span>
							</td>
						</tr>
						<tr>
							<td class="valore-dato" colspan="2"><br></td>
						</tr>
				</c:if>
				<tr id="selezionetutteDitte">
					<td class="etichetta-dato">Applica a tutte le ditte in gara?</td>
					<td class="valore-dato">
						<select id="tutteDitte" name="GARE_RIDISO" title="Applica a tutte le ditte in gara?" >
							<option value="1" title="Si" >Si</option>
							<option value="2" title="No" selected="selected">No</option>
						</select>
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
						<span id="msgEsito">L'operazione &egrave; stata eseguita con successo</span>.
						<br>
						<span id="sezioneAcquisizioniSuccesso" style="display: none;">
							<br>
							Numero acquisizioni: <span id="numeroAcquisizioni"></span>
						</span>
						<span id="sezioneAcquisizioniErrore" style="display: none;">
							<br>
							Numero acquisizioni non processate o con errori: <span id="numeroAcquisizioniErrore"></span>
						</span>
						<br>
						<span id="sezioneAcquisizioniScartate" style="display: none;">
							Numero acquisizioni non processate perch&egrave; relative a ditte non ammesse alla gara: <span id="numeroAcquisizioniScartate"></span>
						</span>
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
						<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:attivaAJAX('${ngara}');">
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:chiudi();">&nbsp;&nbsp;
					</td>
				</tr>
				
				<tr id="sezioneComandiFinali" style="display: none;">
					<td colspan="2" class="comandi-dettaglio">
						<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
					</td>
				</tr>
				
			</table>
			<div id="overlay" style="background-color: rgba(0, 0, 0, 0.6);top: 0;left: 0;height: 100vh; width: 100%;position: absolute; display:none;text-align:center" onclick="javascript:hideOverlay();">
				<br><br><br><br><br><br><br><br><span style="color:white;font-size:14px;"><b>SCANSIONA IL CODICE A BARRE</b></span>
			</div>
		</gene:redefineInsert>
		
		<gene:javaScript>
		
			document.getElementById("PWDcryptata").addEventListener("input", function(e){
				$("#overlay").hide();
			});
			
			document.getElementById("PWDcryptata").addEventListener("focus", function(e){
				$("#overlay").show();
				$("#PWDcryptata").val("");
			});
			
			function cancellaBarcode(){
				$("#PWDcryptata").val("");
				$("#rowPWD").show();
				$("#rowPWDcryptata").hide();
			}
			
			function hideOverlay(){
				$("#overlay").hide();
			}
			
			function usaScanner(){
				$("#rowPWD").hide();
				$("#errorePwd").hide();
				$("#overlay").show();
				$("#PWD").val("");
				$("#rowPWDcryptata").show();
				$("#PWDcryptata").focus();
				
			}
		
			function chiudi(){
				window.close();
			}
			
			function inizializzaAvanzamento(ngara,dittao) {
			
				$(".ui-progressbar").css("position","relative");
				$("#avanzamento-label").css("position","absolute");
				$("#avanzamento-label").css("left","45%");
				$("#avanzamento-label").css("padding-top","4px");
				$("#avanzamento-label").css("font-weight","bold");
				$("#avanzamento-label").css("font","10px Verdana, Arial, Helvetica, sans-serif");
				$("#avanzamento-label").css("text-shadow","1px 1px 0 #fff");
			
				$("#sezioneAvanzamento").show();
				var ngara = $("#ngara").val();
				$.ajax({
					type: "POST",
					dataType: "json",
					async: false,
					beforeSend: function(x) {
						if(x && x.overrideMimeType) {
							x.overrideMimeType("application/json;charset=UTF-8");
						}
					},
					url: "pg/AvanzamentoApPreqAJAX.do?ngara=" + ngara + "&dittao=" + dittao,
					success: function(res){
						$("#avanzamento").progressbar({
							max: res.cnt
			    		});
			    		$("#avanzamento-label").text("0 %");
					}
				});
				
			}
			
			function avanzamento() {
				var ngara = $("#ngara").val();
				$.ajax({
					type: "POST",
					dataType: "json",
					async: false,
					beforeSend: function(x) {
						if(x && x.overrideMimeType) {
							x.overrideMimeType("application/json;charset=UTF-8");
						}
					},
					url: "pg/AvanzamentoApPreqAJAX.do?ngara=" + ngara,
					success: function(res){
						var curr = res.cnt;
						var max = $("#avanzamento").progressbar( "option", "max" );
						$("#avanzamento").progressbar( "option", "value", max - curr );
						var perc = Math.round(((max - curr)/max)*100);
						$("#avanzamento-label").text(perc + " %");
					}
				});
			}
			
			function attivaAJAX(){
				var pwd;
				var pswCryptata;
				<c:if test="${cifraturaBuste eq 'true' }">
					pwd = $("#PWD").val();
					if($("#PWDcryptata").val()){
						pswCryptata = "true";
						pwd = $("#PWDcryptata").val();
					}
				</c:if>
				var ngara = $("#ngara").val();
				var dittao = $("#dittao").val();
				var tutteDitte = $("#tutteDitte").val();
				if(tutteDitte=="1")
					dittao = "";
				inizializzaAvanzamento(ngara,dittao);
			
				$("#sezioneIniziale").hide();
				$("#sezioneComandiIniziali").hide();
				$("#selezionetutteDitte").hide();
				$("#rowPWD").hide();
				$("#rowPWDcryptata").hide();
								
				$.ajax({
					type: "POST",
					dataType: "json",
					async: true,
					beforeSend: function(x) {
						if(x && x.overrideMimeType) {
							x.overrideMimeType("application/json;charset=UTF-8");
						}
					},
					url: "pg/AttivaApPreqAJAX.do?ngara=" + ngara + "&dittao=" + dittao + "&password=" + encodeURIComponent(pwd) + "&pswCryptata=" + pswCryptata,
					success: function(res){
						esito= res.Esito;
						msgEsito = res.MsgErrore;
						$("#numeroAcquisizioni").text(res.numeroAcquisizioni);
						$("#numeroAcquisizioniErrore").text(res.numeroAcquisizioniErrore);
						$("#sezioneAcquisizioniSuccesso").show();
						$("#sezioneAcquisizioniErrore").show();
						if (res.numeroAcquisizioniScartate > 0) {
							$("#sezioneAcquisizioniScartate").show();
							$("#numeroAcquisizioniScartate").text(res.numeroAcquisizioniScartate);
						}
						if(esito!="Errore" && esito !="ErrorePwdNonCorretta")
							window.opener.ricaricaPagina();
						
					
					},
					error: function(jqXHR, textStatus, errorThrown){
						
					},
					complete: function(jqXHR, textStatus) {
						clearInterval(avanzamentoInterval);
						var max = $("#avanzamento").progressbar( "option", "max" );
						$("#avanzamento").progressbar( "option", "value", max );
						$("#avanzamento-label").text("100 %");
						
						$("#sezioneAvanzamento").hide();
						
						if(textStatus=="error" || textStatus=="parsererror"){
							$("#sezioneErrore").show();
							$("#sezioneComandiFinali").show();
                        }else{
                        	if(esito=="ErrorePwdNonCorretta"){
                        		$("#sezioneIniziale").show();
								$("#sezioneComandiIniziali").show();
								$("#selezionetutteDitte").show();
								$("#rowPWD").show();
								$("#rowPWDcryptata").hide();
								$("#PWDcryptata").val("");
								$("#errorePwd").show();	
                        	}else if(esito=="Errore"){
								$("#msgEsitoErrore").html(msgEsito);
								$("#sezioneComandiFinali").show();
	                        	$("#sezioneErrore").show();	
							}else{
								
								$("#sezioneFinale").show();
								$("#sezioneComandiFinali").show();
							}
	                    }
                    }
				});
				
				var avanzamentoInterval = setInterval(avanzamento, 1000);
				
			}
		
		</gene:javaScript>	
	</gene:template>

</div>


