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

<c:set var="cifraturaBuste" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetAbilitazioneCifraturaBusteFunction", pageContext)}'/>

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
			<c:when test='${not empty param.bustalotti}'>
				<c:set var="bustalotti" value="${param.bustalotti}" />
			</c:when>
			<c:otherwise>
				<c:set var="bustalotti" value="${bustalotti}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.bustalotti}'>
				<c:set var="isGaraLottiConOffertaUnica" value="true" />
			</c:when>
			<c:otherwise>
				<c:set var="isGaraLottiConOffertaUnica" value="false" />
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
			<c:when test='${not empty param.sez}'>
				<c:set var="sez" value="${param.sez}" />
			</c:when>
			<c:otherwise>
				<c:set var="sez" value="${sez}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${sez eq "1"}'>
				<c:set var="msgSez" value="tecnico-qualitativa" />
			</c:when>
			<c:when test='${sez eq "2"}'>
				<c:set var="msgSez" value="tecnico-quantitativa" />
			</c:when>
			<c:otherwise>
				<c:set var="msgSez" value="tecnica" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.anonima}'>
				<c:set var="anonima" value="${param.anonima}" />
			</c:when>
			<c:otherwise>
				<c:set var="anonima" value="${anonima}" />
			</c:otherwise>
		</c:choose>
		
		<gene:redefineInsert name="addHistory" />
		<gene:redefineInsert name="gestioneHistory" />
		<c:choose>
			<c:when test='${anonima eq "1"}'>
				<gene:setString name="titoloMaschera" value='Acquisizione buste tecniche in forma anonima' />
			</c:when>
			<c:otherwise>
				<gene:setString name="titoloMaschera" value='Acquisizione busta ${msgSez}' />
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
		
		
		
		<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext,ngara,codgar,dittao)}' />
		
		<c:choose>
			<c:when test='${bustalotti eq "1"}'>
				<c:set var="garaBustalotti" value="${codgar}" />
			</c:when>
			<c:otherwise>
				<c:set var="garaBustalotti" value="${ngara}" />
			</c:otherwise>
		</c:choose>
		
		<c:set var="cifraturaBuste" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBustaCifrataFunction", pageContext, garaBustalotti, "FS11B")}'/>
		
		<c:if test='${sez eq "2"}'>
			<c:set var="whereControllo" value="comkey2 = '${ngara}' and (comstato = '5' or comstato='17') and comtipo = 'FS11B'" />
			<c:set var="busteDaAcquisire" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "count(IDPRG)","W_INVCOM", whereControllo)}'/>
			<c:choose>
				<c:when test="${not empty busteDaAcquisire and busteDaAcquisire ne 0 }">
					<c:set var="bloccoBuste" value="true" />
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test='${bustalotti eq "1" or bustalotti eq "2"}'>
							<c:set var="codice" value="${codgar}" />
						</c:when>
						<c:otherwise>
							<c:set var="codice" value="${ngara}" />
						</c:otherwise>
					</c:choose>
					<c:set var="isValutazioneCommissione" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsValutazioneCommissioneFunction",pageContext,codice)}' />
					<c:set var="esitoControlloCriteri" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlliCriteriQualitativiFunction", pageContext, ngara, codgar, bustalotti, isValutazioneCommissione )}' />
				</c:otherwise>
			</c:choose>
			
			
			
		</c:if>
		
		<c:if test='${anonima eq "1"}'>
			<c:set var="garaInversa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetInversaFunction", pageContext, codgar)}' />	
			<c:set var="compreq" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCompreqFunction", pageContext, codgar)}' />
			<c:set var="esisteBloccoFasi2" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoCondizioniFasiAcquisizioniFunction",  pageContext, ngara, "2")}'/>
			<c:set var="esisteBloccoFasi3" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoCondizioniFasiAcquisizioniFunction",  pageContext, ngara, "3")}'/>
			<c:set var="esisteBloccoFasi4" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoCondizioniFasiAcquisizioniFunction",  pageContext, ngara, "4")}'/>
			<c:if test="${esisteBloccoFasi2 eq 'true' && esisteBloccoFasi3 eq 'true' && esisteBloccoFasi4 eq 'true'}">
				<c:set var="esisteBloccoFasi" value='true'/>
			</c:if>
			
			<c:if test="${garaInversa ne '1' }">
				<c:set var="esistonoDitteAmmissioneNulla" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteAmmissioneNullaFunction", pageContext, ngara, "35" )}' />
			</c:if>
			<c:set var="esistonoDitteAmmissioneSoccorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteAmmissioneSoccorsoIstruttorioFunction", pageContext, ngara, "35","false" )}' />
			
			
			<c:if test="${garaInversa ne '1' }">
				<c:set var="esistonoAcquisizioniOfferteDaElaborare" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, ngara, "FS11A" )}' />
			</c:if>
			
			
			<c:set var="esistonoDitteInGara" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteInGaraFunction", pageContext, "NGARA5", ngara," and (fasgar > 2 or fasgar is null)")}' />
			<c:if test="${compreq eq '1' and garaInversa ne '1'  }">
				<c:set var="esistonoDitteEstimpValorizzato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteEstimpValorizzatoFunction", pageContext, ngara )}' />
			</c:if>
			
		</c:if>
		
		<gene:redefineInsert name="corpo">
		
			<input type="hidden" id="ngara" value="${ngara}" />
			<input type="hidden" id="bustalotti" value="${bustalotti}" />
			<input type="hidden" id="codgar" value="${codgar}" />
			<input type="hidden" id="dittao" value="${dittao}" />
			<input type="hidden" id="sez" value="${sez}" />
			<input type="hidden" id="anonima" value="${anonima}" />
			
			<table class="dettaglio-notab">
				<tr id="sezioneIniziale">
					<td class="valore-dato" colspan="2">
						<br>
						<c:choose>
							<c:when test="${anonima eq '1' && (esisteBloccoFasi eq 'true' ||  esistonoDitteAmmissioneNulla eq 'true' || esistonoAcquisizioniOfferteDaElaborare eq 'true'
								|| esistonoDitteInGara eq 'false' || esistonoDitteEstimpValorizzato eq 'false' || esistonoDitteAmmissioneSoccorso eq 'true')}">
								<c:set var="bloccoSalvataggio" value='true'/>
								<c:choose>
									<c:when test="${esisteBloccoFasi eq 'true'}">
										<br>
										Non &egrave; possibile procedere all'acquisizione delle buste perch&egrave; la fase corrente non &egrave; quella di apertura doc. amministrativa. 
										<br>
										<br>
									</c:when>
									<c:when test="${esistonoDitteAmmissioneNulla eq 'true'}">
										<br>
										Non &egrave; possibile procedere all'acquisizione delle buste perch&egrave; deve essere specificato lo stato di ammissione per ogni ditta in gara. 
										<br>
										<br>
									</c:when>
									<c:when test="${esistonoDitteAmmissioneSoccorso eq 'true'}">
										<br>
										 Non &egrave; possibile procedere all'acquisizione delle buste perch&egrave; ci sono delle ditte in gara con soccorso istruttorio in corso.
										<br>
										<br>
									</c:when>
									<c:when test="${esistonoAcquisizioniOfferteDaElaborare eq 'true'}">
										<br>
										Non &egrave; possibile procedere all'acquisizione delle buste perch&egrave; devono essere prima acquisite tutte le buste amministrative. 
										<br>
										<br>
									</c:when>
									<c:when test="${ esistonoDitteInGara eq 'false'}">
										<br>
										Non &egrave; possibile procedere all'acquisizione delle buste perch&egrave; non ci sono ditte in gara. 
										<br>
										<br>
									</c:when>
									<c:when test="${ esistonoDitteEstimpValorizzato eq 'false'}">
										<br>
										Non &egrave; possibile procedere all'acquisizione delle buste perch&egrave; deve essere prima fatto il sorteggio sulle ditte in gara per la verifica requisiti. 
										<br>
										<br>
									</c:when>
								</c:choose>
							</c:when>
							<c:when test="${bloccoBuste}">
								Non è possibile procedere all'acquisizione della busta tecnico-quantitativa della ditta '${nomimo}' perch&egrave; deve essere prima acquisita la busta tecnico-qualitativa per ogni ditta nella lista. 
							</c:when>
							<c:when test="${esitoControlloCriteri eq 'nok-commissione'}">
								Non è possibile procedere all'acquisizione della busta tecnico-quantitativa della ditta '${nomimo}' perch&egrave; deve essere prima completata la compilazione del dettaglio valutazione dei criteri tecnico-qualitativi per tutte le ditte in gara 
							</c:when>
							<c:when test="${esitoControlloCriteri eq 'nok'}">
								Non è possibile procedere all'acquisizione della busta tecnico-quantitativa della ditta '${nomimo}' perch&egrave; deve essere prima completata la compilazione del dettaglio valutazione dei criteri tecnico-qualitativi per tutte le ditte in gara 
							</c:when>
							<c:when test="${anonima eq '1'}">
								Mediante questa funzione &egrave; possibile procedere all'acquisizione in forma anonima delle buste tecniche di tutte le ditte in gara.
								<br><br>
								Confermi l'operazione ?
							</c:when>
							<c:otherwise>
								Mediante questa funzione &egrave; possibile procedere all'acquisizione della busta ${msgSez} della ditta '${nomimo}'.
								<br><br>
								Confermi l'operazione ?
							</c:otherwise>
						</c:choose>
						
						<br>
						<br>
					</td>
				</tr>
				
				
				<c:if test="${bloccoBuste ne 'true' and esitoControlloCriteri ne 'nok' and esitoControlloCriteri ne 'nok-commissione' and bloccoSalvataggio ne 'true'}">
					<c:if test="${cifraturaBuste eq 'true'}">
							<tr id="rowPWD">
								<td id="etichettaPassword" class="etichetta-dato">Password per la decifratura della busta (*)</td>
								<td class="valore-dato">
									<input id="PWD" name="PWD" title="Password per la decifratura della busta" class="testo" type="password" size="24" value="${sessionScope.passBusteB }" maxlength="100" />
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
				</c:if>
				
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
						<c:if test="${bloccoBuste ne 'true'  and esitoControlloCriteri ne 'nok' and esitoControlloCriteri ne 'nok-commissione' and bloccoSalvataggio ne 'true'}">
							<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:attivaAJAX('${ngara}');">
						</c:if>
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
			
			<c:if test="${anonima eq '1' }">
				$("#selezionetutteDitte").hide();
				$("#tutteDitte").val('1');
			</c:if>
			
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
				
				$.ajax({
					type: "POST",
					dataType: "json",
					async: false,
					beforeSend: function(x) {
						if(x && x.overrideMimeType) {
							x.overrideMimeType("application/json;charset=UTF-8");
						}
					},
					url: "pg/AvanzamentoApTecAJAX.do?ngara=" + ngara + "&dittao=" + dittao,
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
					url: "pg/AvanzamentoApTecAJAX.do?ngara=" + ngara,
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
				var sez = $("#sez").val();
							
				$("#sezioneIniziale").hide();
				$("#sezioneComandiIniziali").hide();
				$("#selezionetutteDitte").hide();
				$("#rowPWD").hide();
				$("#rowPWDcryptata").hide();
				
				var anonima=$("#anonima").val();	
				var action=	"pg/AttivaApTecAJAX.do";
				if(anonima==1)
					action=	"pg/AttivaApMassivaTecAnonimeAJAX.do";			
				$.ajax({
					type: "POST",
					dataType: "json",
					async: true,
					beforeSend: function(x) {
						if(x && x.overrideMimeType) {
							x.overrideMimeType("application/json;charset=UTF-8");
						}
					},
					url: action + "?ngara=" + ngara + "&dittao=" + dittao + "&password=" + encodeURIComponent(pwd) + "&pswCryptata=" + pswCryptata + "&sez=" + sez + "&anonima=" + anonima,
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
					error: function(e){
						//alert("Errore generico durante l'operazione");
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
								<c:if test="${anonima ne '1' }">
									$("#selezionetutteDitte").show();
								</c:if>
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
			
			
			function faseNext() {
				var bustalotti = "${bustalotti }";
				if(bustalotti == null || bustalotti==""){
					var activePage = window.opener.document.forms[0].activePage.value;
					//window.opener.selezionaPagina(8);
					activePage = parseInt(activePage) + 1;
					window.opener.selezionaPagina(activePage);
				}else if(bustalotti == '1'){
					window.opener.aperturaOfferte();
				}else{
					window.opener.avanti();
				}
			}

		
		</gene:javaScript>	
	</gene:template>

</div>


