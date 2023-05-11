<%
/*
 * Created on: 31-08-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra per l'attivazione della funzione 'Pubblica su portale Alice Gare'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:choose>
	<c:when test='${not empty requestScope.pubblicazioneEseguita and requestScope.pubblicazioneEseguita eq "1"}' >
		<gene:template file="popup-message-template.jsp">
			<c:choose>
				<c:when test='${!empty param.idStipula}'>
					<c:set var="idStipula" value="${param.idStipula}" />
				</c:when>
				<c:otherwise>
					<c:set var="idStipula" value="${idStipula}" />
				</c:otherwise>
			</c:choose>
			<gene:redefineInsert name="corpo">
			<gene:setString name="titoloMaschera" value='Invia a contraente e pubblica su portale Appalti' />
			<c:set var="contextPath" value="${pageContext.request.contextPath}" />
			<tr>
				<td colSpan="2">
					<br>
							Pubblicazione su portale completata.
					<br>&nbsp;
					<br>&nbsp;
				</td>	
			<tr>
			
			<gene:redefineInsert name="buttons">
				<input type="button" class="bottone-azione"  id="cancel" value="Chiudi" title="Chiudi" onclick="chiudi();"/>&nbsp;&nbsp;
			</gene:redefineInsert>
			<gene:javaScript>
			
			var dim1=800;
			var dim2=500;
			window.resizeTo(dim1,dim2);	
			
			window.onload = function () { 
				window.opener.bloccaRichiesteServer();
				window.opener.location = "${contextPath}/History.do?"+csrfToken+"&metodo=reload";
			}
			
			function chiudi(){
				window.close();
			}
			function conferma(){
				window.close();
			}
			function download(){
				document.formDownload.submit();
			}
			</gene:javaScript>
			</gene:redefineInsert>
		</gene:template>
	</c:when>
	<c:otherwise>

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${contextPath}/js/jHtmlArea-0.7.5.min.js"></script> 
	<link rel="Stylesheet" type="text/css" href="${contextPath}/css/jquery/jHtmlArea/jHtmlArea.css" />
	<script type="text/javascript" src="${contextPath}/js/common-gare.js"></script>		
	<script type="text/javascript" src="${contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<link rel="stylesheet" type="text/css" href="${contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
</gene:redefineInsert>



<c:set var="modo" value="NUOVO" scope="request" />

	<c:choose>
		<c:when test='${!empty param.idStipula}'>
			<c:set var="idStipula" value="${param.idStipula}" />
		</c:when>
		<c:otherwise>
			<c:set var="idStipula" value="${idStipula}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.opzInvio}'>
			<c:set var="opzInvio" value="${param.opzInvio}" />
		</c:when>
		<c:otherwise>
			<c:set var="opzInvio" value="${opzInvio}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.idconfi}'>
			<c:set var="idconfi" value="${param.idconfi}" />
		</c:when>
		<c:otherwise>
			<c:set var="idconfi" value="${idconfi}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.step}'>
			<c:set var="step" value="${param.step}" />
		</c:when>
		<c:otherwise>
			<c:set var="step" value="${step}" />
		</c:otherwise>
	</c:choose>
	
	<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, "", idconfi)}'/>
		
	<gene:setString name="titoloMaschera" value='Invia a contraente e pubblica su portale Appalti' />
	
	<c:set var="modelloMailPec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetMailPecModelloFunction", pageContext, "57","false",idStipula)}' />
	<c:set var="htmlSupport" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "comunicazione.supportoHtml")}'/>
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="G1STIPULA" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePubblicaStipula" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePubblicaStipula">
			
	<gene:campoScheda nome="msgPagina">
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}' >
					${requestScope.msg }
				</c:when>
				<c:otherwise>
					<c:if test='${not empty opzInvio}' >
						${requestScope.MsgConferma}
						<c:if test='${opzInvio eq "1"}' >
							<br>Procedendo viene anche inviata una comunicazione di avviso al contraente, secondo i dettagli riportati di seguito.
						</c:if>
						<c:if test='${opzInvio eq "2"}' >
							<br><b>Procedendo nessuna comunicazione di avviso viene inviata al contraente</b>.
							<span id="spanMsgFascicolo" style="display:none"><br><br>Con la pubblicazione su portale Appalti si procede anche all'apertura del fascicolo documentale.</span>
						</c:if>
					</c:if>
					<c:if test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "WARNING"}' >
						${requestScope.msg }
					</c:if>
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>

		<gene:campoScheda campo="ID" campoFittizio="true" defaultValue="${param.idStipula}" visibile="false" definizione="N12;0"/>
		<gene:campoScheda campo="STATO" campoFittizio="true" defaultValue="${requestScope.statoStipula}" visibile="false" definizione="N12;0"/>
		<gene:campoScheda campo="OPZINVIO" campoFittizio="true" defaultValue="${param.opzInvio}" visibile="false" definizione="N1;0"/>
		<c:if test="${requestScope.controlloSuperato ne 'NO' and opzInvio eq '1'}">
			<gene:campoScheda nome="TitoloComunicazione">
				<td colspan="2"><b>Comunicazione al contraente</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="COMMSGOGG" campoFittizio="true" definizione="T300;0;;;COMMSGOGG" obbligatorio="true" value="${requestScope.oggettoMail}"/>
			<gene:campoScheda campo="COMINTEST" campoFittizio="true" definizione="T2;0;;SN;COMINTEST" value="${requestScope.abilitaIntestazioneVariabile}" visibile="${ abilitatoInvioMailDocumentale ne 'true'}"/>
			<gene:campoScheda nome="intestazione" visibile="${ abilitatoInvioMailDocumentale ne 'true'}"> 
				<td class="etichetta-dato">Intestazione variabile</td>
				<td class="valore-dato">Spett.le <i>Ragione Sociale</i>
				</td>
			</gene:campoScheda>
			<gene:campoScheda campo="COMMSGTIP" campoFittizio="true" definizione="T2;0;;SN;COMMSGTIP" defaultValue="2" visibile='${htmlSupport eq "1"}'/>
			<gene:campoScheda campo="COMMSGTES" campoFittizio="true" definizione="T2000;0;;CLOB;COMMSGTES" obbligatorio="true" value="${requestScope.testoMail}" gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoTestoComunicazioneHTML"/>
			<gene:campoScheda campo="COMMITT" campoFittizio="true" definizione="T60;0;;;COMMITT" value="${requestScope.mittenteMail}" visibile="${ abilitatoInvioMailDocumentale ne 'true'}"/>
			
			<gene:campoScheda nome="soggettoDestinatario">
				<td colspan="2"><b>Soggetto destinatario</b></td>
			</gene:campoScheda>
			<gene:campoScheda addTr="false" >
					<tr id="tr1">
						<td colspan="2">
							<table id="tabellaDestinatari" class="griglia" >
								<tr style="BACKGROUND-COLOR: #A7BFD9;">
									<td colspan="2" class="titolo-valore-dato" id="Intestazione" style="width:50%">Intestazione</td>
									<td colspan="2" class="titolo-valore-dato" id="Indirizzo" style="width:30%">Indirizzo</td>
									<td colspan="2" class="titolo-valore-dato" id="Tipo" style="width:20%">Tipo indirizzo</td>
								</tr>
				</gene:campoScheda>
					<gene:campoScheda addTr="false">
							<tr id="listaDestinatari">
					</gene:campoScheda>
					<gene:campoScheda title="Intestazione" hideTitle="true" addTr="false" modificabile="false" campo="INTESTAZIONE" campoFittizio="true" visibile="true" definizione="T2000;" value="${soggettoDestinatario[0]}"/>
					<gene:campoScheda title="Indirizzo mail" hideTitle="true" addTr="false" modificabile="false" campo="MAIL" campoFittizio="true" visibile="true" definizione="T100" value="${soggettoDestinatario[1]}"/>
					<gene:campoScheda title="Tipo indirizzo" hideTitle="true" addTr="false" modificabile="false" campo="TIPO" campoFittizio="true" visibile="true" definizione="T10" value="${soggettoDestinatario[2]}"/>
					<gene:campoScheda title="Ditta" hideTitle="true" addTr="false" modificabile="false" campo="DITTA" campoFittizio="true" visibile="false" definizione="T16" value="${soggettoDestinatario[3]}"/>
					<gene:campoScheda addTr="false">									
							</tr>
					</gene:campoScheda>
							
			<gene:campoScheda addTr="false">
							
						</table>
					</td>
				</tr>
			</gene:campoScheda>		
			
			<c:if test="${integrazioneWSDM =='1'}">
				<table class="dettaglio-notab" id="datiLogin">
					<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp"></jsp:include>
				</table>
				<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-internoDati.jsp"></jsp:include>
			</c:if>
			 
						
			<gene:fnJavaScriptScheda funzione="modifyHTMLEditor('#COMMSGTIP#')" elencocampi="COMMSGTIP" esegui="false" />
			<c:if test="${ abilitatoInvioMailDocumentale ne 'true'}">
				<gene:fnJavaScriptScheda funzione="gestioneCOMINTEST('#COMINTEST#')" elencocampi="COMINTEST" esegui="true" />
			</c:if>
		</c:if>
		<c:if test="${requestScope.controlloSuperato ne 'NO' and opzInvio eq '2' and integrazioneWSDM =='1'}">
			<gene:campoScheda addTr="false" >
			<tr id="trDatiWSDM" style="display:none">
						<td colspan="2">
						<table class="dettaglio-notab" id="datiLogin">
							<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp"></jsp:include>
						</table>
						<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-internoDati.jsp"></jsp:include>
						</td>
			</tr>
			</gene:campoScheda>
		</c:if>
		<c:if test="${requestScope.controlloSuperato ne 'NO' and empty opzInvio}">
			<br>Mediante questa funzione si procede alla pubblicazione dei documenti della stipula su area riservata del portale Appalti.
			<br><br>E' possibile procedere inviando o meno una comunicazione di avviso al contraente.
			<br>Selezionare una delle opzioni sottostanti.
			<br>
			<table class="dettaglio-notab">
				<tr>
				 		<br>
						&nbsp;<input type="radio" name="modstip" id="radiomodstip1" value="1" checked="checked" />&nbsp;Pubblicazione dei documenti <b>con invio della comunicazione di avviso</b>
						<br>
						<br>
						&nbsp;<input type="radio" name="modstip" id="radiomodstip2" value="2" />&nbsp;Pubblicazione dei documenti <b>senza invio della comunicazione di avviso</b>
						<br>
				 	    <br>
				</tr>
			</table>		
			<c:set var="STATO" value="${requestScope.statoStipula}" />
			<c:if test="${STATO >= 3}">
				<br><b>ATTENZIONE:</b> la stipula è già stata pubblicata. Procedendo vengono pubblicati i documenti che sono ora in stato 'in compilazione'.<br>
			</c:if>
		</c:if>
		
		<c:if test='${requestScope.controlloSuperato ne "NO" and not empty opzInvio}' >
			
			<gene:campoScheda campo="DATPUB" visibile="false" campoFittizio="true" definizione="D;0;;;DATPUB" obbligatorio="true"/>
		</c:if>
		
		<input type="hidden" name="idStipula" id="idStipula" value="${idStipula}" />
		<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}" />
		<input type="hidden" name="step" id="step" value="${step}" />
		<input type="hidden" name="codstipula" id="codstipula" value="${codstipula}" />
		
		<input type="hidden" name="integrazioneWSDM" id="integrazioneWSDM" value="${integrazioneWSDM}" />
		<input type="hidden" name="step" id="step" value="${step}" />
		<input name="servizio" id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
		<input name="syscon" id="syscon" type="hidden" value="${profiloUtente.id}" /> 
		<input name="tiposistemaremoto" id="tiposistemaremoto" type="hidden" value="" />
		<input name="tabellatiInDB" id="tabellatiInDB" type="hidden" value="" />
		<input name="entita" id="entita" type="hidden" value="G1STIPULA" /> 
		<input name="idprg" id="idprg" type="hidden" value="PG" />
		<input id="key1" type="hidden" name="key1" value="${idStipula }" />
		<input id="key2" type="hidden" name="key2" value="" /> 
		<input id="key3" type="hidden" name="key3" value="" /> 
		<input id="key4" type="hidden" name="key4" value="" />
		<input id="creaFascicolo" type="hidden" name="creaFascicolo" value="no" /> 
		
		<input type="hidden" id="chiaveOriginale" value="${codstipula }" />
		<input id="abilitazioneGare" type="hidden" value="${profiloUtente.abilitazioneGare}" />
	</gene:formScheda>
  </gene:redefineInsert>

	
	<c:choose>
		<c:when test="${opzInvio eq 1}">
			<c:choose>
				<c:when test="${integrazioneWSDM =='1' }">
					<c:set var="testoBottone" value="Protocolla comunicazione e pubblica"/>
				</c:when>
				<c:otherwise>
					<c:set var="testoBottone" value="Invia comunicazione e pubblica"/>
				</c:otherwise>
			</c:choose>
			
		</c:when>
		<c:otherwise>
			<c:set var="testoBottone" value="Pubblica senza invio comunicazione"/>
		</c:otherwise>
	</c:choose>
	
	<gene:redefineInsert name="buttons">
			<c:choose>
				<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO" }'>
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
				</c:when>
				<c:when test='${empty opzInvio}'>
					<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:sceltaInvio();">&nbsp;
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;
				</c:when>
				<c:otherwise>
					<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value="${testoBottone}" title="${testoBottone}" onclick="javascript:conferma()">&nbsp;
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
				</c:otherwise>
			</c:choose>
	</gene:redefineInsert>
	

	
	
	<gene:javaScript>
	
		var $datepicker = $('#DATPUB');
		$datepicker.datepicker();
		$datepicker.datepicker( "option", "dateFormat", "dd/mm/yy" );
		$datepicker.datepicker('setDate', new Date());
		
		<c:if test="${1==1}"> 
			$("#DATPUB").prop("disabled",true);
		</c:if>
		
		<c:if test="${requestScope.controlloSuperato ne 'NO' and opzInvio eq '1'}">
				<c:if test="${ integrazioneWSDM =='1'}">
            		
            		$('#inserimentoinfascicolo').change(function() {
						_gestioneInserimentoInFascicolo();
				    });
				    
					$('#classificafascicolonuovo').change(function() {
						$('#classificadocumento').val($('#classificafascicolonuovo').val());
					});
				    
				    $('#username').change(function() {
						if(_letturaMittenteDaServzio){
							$('#mittenteinterno').empty();
							_popolaTabellatoJirideMittente("mittenteinterno");
							if(_delegaInvioMailDocumentaleAbilitata == 1){
								$('#indirizzomittente').empty();
								_popolaTabellatoJirideMittente("indirizzomittente");
							}
						}
						caricamentoStrutturaJIRIDE();
					});
							 
					
					$('#ruolo').change(function() {
						caricamentoStrutturaJIRIDE();
					});
			   		
			   		   		
			   		$('#listafascicoli').on('change',  function () {
						var str = this.value;
						gestioneSelezioneFascicolo(str);
						
					});
            		
            		var step= $("#step").val();
            		if(step==1){
	            		//document.getElementById("rowTitoloComunicazione").style.display='none';
		               	//document.getElementById("composizione").style.display='none';
		               	//document.getElementById("tabellaNoInvioMail").style.display='';
		               	$("#datiLogin").hide();
						$("#datiProtocollo").hide();
            		}
            		caricamentoDati();
            		_controlloDelegaInvioMailAlDocumentale();
            	</c:if>
            	
				<c:choose>
					<c:when test="${integrazioneWSDM =='1'}">
						var dim1=1000;
						var dim2=700;
					</c:when>
					<c:otherwise>
						var dim1=800;
						var dim2=700;
					</c:otherwise>
				</c:choose>
				window.resizeTo(dim1,dim2);	
		</c:if>
		
		<c:if test="${requestScope.controlloSuperato ne 'NO' and opzInvio eq '2' and integrazioneWSDM =='1'}">
			function caricamentoDati(){
										
				
				_controlloPresenzaFascicolazione();
				_controlloFascicoliAssociati();
				if (_fascicolazioneAbilitata==1 && _fascicoliPresenti==0){
					$("#spanMsgFascicolo").show();
					$("#trDatiWSDM").show();
					$("#TitoloDatiDocumento").hide();
					$("#classificaDocumento").hide();
					$("#idTitolazione").hide();
					$("#codRegistroDocumento").hide();
					$("#tipoDocumento").hide();
					$("#oggettoDocumento").hide();
					$("#mittenteInterno").hide();
					$("#indirizzoMittente").hide();
					$("#mezzoInvio").hide();
					$("#sezionelivelloriservatezza").hide();
					$("#idIndice").hide();
					$("#idUnitaoperativaMittente").hide();
					$("#mezzo").closest('tr').hide();
					$("#rigaSottotipo").hide();
					
					_getWSTipoSistemaRemoto();
					_tipoWSDM = $("#tiposistemaremoto").val();
					_popolaTabellato("ruolo","ruolo");
					_getWSLogin();
					_gestioneWSLogin();
					
					_popolaTabellato("classifica","classificadocumento");
					_popolaTabellato("classificafascicolo","classificafascicolonuovo");
					_popolaTabellato("idindice","idindice");
					_popolaTabellato("idtitolazione","idtitolazione");
					_popolaTabellato("idunitaoperativamittente","idunitaoperativamittente");
										
					if(_logincomune=="1")
						bloccaCampiLoginComune();
					
					$('#inserimentoinfascicolo').change(function() {
						_gestioneInserimentoInFascicolo();
				    });
					
					//Se il campo classificafascicolonuovo contiene un solo valore, si riporta
					//in classificadocumento tale valore
					if($("#classificafascicolonuovo option").length == 2 ){
						$('#classificadocumento').val($('#classificafascicolonuovo').val());
					}
					
					_inizializzazioni();
					
					
					
					$('#username').change(function() {
						if (_tipoWSDM == "JIRIDE"){
							if(_letturaMittenteDaServzio){
								$('#mittenteinterno').empty();
								_popolaTabellatoJirideMittente("mittenteinterno");
								if(_delegaInvioMailDocumentaleAbilitata == 1){
									$('#indirizzomittente').empty();
									_popolaTabellatoJirideMittente("indirizzomittente");
								}
							}
							caricamentoStrutturaJIRIDE();
						}
					});
					
					$('#ruolo').change(function() {
						if (_tipoWSDM == "JIRIDE"){
							caricamentoStrutturaJIRIDE();
						}
				    });
				    
				    $('#listafascicoli').on('change',  function () {
						var str = this.value;
						gestioneSelezioneFascicolo(str);
						
					});
					
					$("#creaFascicolo").val("si");
				}
				
				
				
			}
			
			caricamentoDati();
		</c:if>
	
		function conferma() {
			<c:if test="${integrazioneWSDM =='1' && opzInvio eq '1'}">
				var step= $("#step").val();
				if(step==1){
					$("#step").val("2");
					inizializzazionePagina("2");
					return;
				}else{
					//Controlli sulla valorizzazione dei campi obbligatori
					var errori = controlloCampiObbligatori();
					if(errori)
						return;
					else{
						_setWSLogin();
					}
				}
			</c:if>
			<c:if test="${integrazioneWSDM =='1' && opzInvio eq '2'}">
				var creaFascicolo=$("#creaFascicolo").val();
				if(creaFascicolo=="si"){
					var errori = controlloCampiObbligatori();
					if(errori)
						return;
					else{
						_setWSLogin();
					}
				}
			</c:if>
			
			document.forms[0].jspPathTo.value="gare/g1stipula/g1stipula-PubblicaDocumenti.jsp";
			schedaConferma();
		}
		
		<c:if test="${integrazioneWSDM =='1' && opzInvio eq '1'}">
		function caricamentoDati(){
			
				
			/*
		     * Gestione utente ed attributi per il collegamento remoto
		     */
			_getWSTipoSistemaRemoto();
			_popolaTabellato("ruolo","ruolo");
			_popolaTabellato("codiceuo","codiceuo");
			_getWSLogin();
			_gestioneWSLogin();
			
			/*
			 * Gestione tabellati per richiesta protocollazione
			 */
			_popolaTabellato("classifica","classificadocumento");
			_popolaTabellato("codiceregistro","codiceregistrodocumento");
			_popolaTabellato("tipodocumento","tipodocumento");
			_popolaTabellato("mittenteinterno","mittenteinterno");
			_popolaTabellato("indirizzomittente","indirizzomittente");
			_popolaTabellato("mezzo","mezzoinvio");
			_popolaTabellato("mezzo","mezzo");
			_popolaTabellato("classificafascicolo","classificafascicolonuovo");
			_popolaTabellato("idindice","idindice");
			_popolaTabellato("idtitolazione","idtitolazione");
			_popolaTabellato("idunitaoperativamittente","idunitaoperativamittente");
			_popolaTabellato("supporto","supporto");
			_popolaTabellato("sottotipo","sottotipo");
			_popolaTabellato("tipofirma","tipofirma");
			
			_getComunicazione();
			_controlloPresenzaFascicolazione();
			_controlloFascicoliAssociati();
			_inizializzazioni();
			//Se il campo classificafascicolonuovo contiene un solo valore, si riporta
			//in classificadocumento tale valore
			if($("#classificafascicolonuovo option").length == 2 ){
				$('#classificadocumento').val($('#classificafascicolonuovo').val());
			}
		}
		
		function inizializzazionePagina(step){
        	if(step=="1"){
				$("#datiLogin").hide();
				$("#datiProtocollo").hide();
				$("#RigaTipoMail").show();
				$("#TipoMail").show();
				$(".pagebanner").show();
				$(".datilista").show();
				$("#composizione").show();
				$("#CampiTabellaInvioMail").show();
				$("#oggetto").show();
				$("#valoreOggetto").show();
				$("#intestazione").show();
				$("#valoreIntestazione").show();
				$("#testoMail").show();
				$("#valoreTestoMail").show();
				$("#mittente").show();
				$("#valoreMittente").show();
				if(_delegaInvioMailDocumentaleAbilitata == 1){
					
					$("#TipoMail").hide();
				}
				$("#rowCOMMSGTIP").show();	
			}else{
				$("#datiLogin").show();
				$("#datiProtocollo").show();
				$("#TipoMail").hide();
				$("#RigaTipoMail").hide();
				$(".pagebanner").hide();
				$(".datilista").hide();
				$("#composizione").hide()
				$("#oggetto").hide();
				$("#valoreOggetto").hide();
				$("#intestazione").hide();
				$("#valoreIntestazione").hide();
				$("#testoMail").hide();
				$("#valoreTestoMail").hide();
				$("#mittente").hide();
				$("#valoreMittente").hide();
				$("#oggettodocumento").val($("#COMMSGTES").val());
				
				$("#rowmsgPagina").hide();
				$("#rowTitoloComunicazione").hide();
				$("#rowCOMMSGOGG").hide();
				$("#rowCOMINTEST").hide();
				$("#rowintestazione").hide();
				$("#rowCOMMSGTES").hide();
				$("#rowCOMMITT").hide();
				$("#rowsoggettoDestinatario").hide();
				$("#rowCOMMSGTIP").hide();
				$("#tr1").hide();
				
			}
		}
		
		/*
		 * Lettura della singola comunicazione.
		 * Utilizzata per popolare i dati generali della comunicazione,
		 * la lista dei destinatari e la lista degli allegati.
		 */
		function _getComunicazione() {
					
			var servizio = $("#servizio").val();
			_getTipoWSDM();
			_controlloDelegaInvioMailAlDocumentale();
			
			$("#codiceregistrodocumento").hide();
			$("#codiceregistrodocumento").closest('tr').hide();
			$("#idindice").hide();
			$("#idindice").closest('tr').hide();
			$("#idtitolazione").hide();
			$("#idtitolazione").closest('tr').hide();
			$("#idunitaoperativamittente").hide();
			$("#idunitaoperativamittente").closest('tr').hide();
			
			if ( _delegaInvioMailDocumentaleAbilitata != 1)  {
				$("#indirizzomittente").hide();
				$("#indirizzomittente").closest('tr').hide();
				$('#indirizzomittente option').eq(0).prop('selected', true);
			}
							
			
           	$("#mezzo").hide();
			$("#mezzo").closest('tr').hide();
			
			if($("#inserimentoinfascicolo option:selected").val() == "SI_FASCICOLO_ESISTENTE")
			_setDescrizioneCodiceTabellato("classificafascicolo",$("#classificafascicolonuovo").val(),"classificafascicolodescrizione",2);
			
            
               
				
		}
		
		
		</c:if>
		
		function annulla(){
			window.close();
		}
		
		
		
		
		
		
			document.forms[0].encoding="multipart/form-data";
			
			<c:if test="${opzInvio eq '1'}">
			function modifyHTMLEditor(valore){
				if (valore == '1')
				 	$('#COMMSGTES').htmlarea('hideHTMLView');
				else
				 	$('#COMMSGTES').htmlarea('showHTMLView');
			}
			
			$(document).ready(function() {
					$('#COMMSGTES').htmlarea({
					toolbar: [
					["bold", "italic", "underline", "strikethrough"],
							["increasefontsize", "decreasefontsize"],
							["orderedlist", "unorderedlist"],
							["indent", "outdent"],
							["justifyleft", "justifycenter", "justifyright"],
							["link", "unlink", "image", "horizontalrule"],
							["cut", "copy", "paste"]
						],
					
						toolbarText: $.extend({}, jHtmlArea.defaultOptions.toolbarText, {
							"bold": "Grassetto",
							"italic": "Corsivo",
							"underline": "Sottolineato",
							"strikethrough": "Barrato",
							"increasefontsize": "Ingrandisci carattere",
							"decreasefontsize": "Riduci carattere",
							"orderedlist": "Elenco numerato",
							"unorderedlist": "Elenco puntato",
							"indent": "Aumenta rientro",
							"outdent": "Riduci rientro",
							"justifyleft": "Allinea testo a sinistra",
							"justifycenter": "Centra",
							"justifyright": "Allinea testo a destra",
							"link": "Inserisci collegamento ipertestuale",
							"unlink": "Rimuovi collegamento ipertestuale",
							"image": "Inserisci immagine",
							"horizontalrule": "Inserisci riga orizzontale",
							"cut": "Taglia",
							"copy": "Copia",
							"paste": "Incolla"
						})
					});
					modifyHTMLEditor(getValue('COMMSGTIP'));
			});
			</c:if>
			
			function gestioneCOMINTEST(comintest){
				document.getElementById("rowintestazione").style.display = (comintest=='1' ? '':'none');
			}
					
			function mostraElementiListaDestinatariNascosti(vis){
				var num="${numElementiListaDestinatari}";
			 	if(num>0){
				 	for(var i=0; i<=num-1;i++){
				 		showObj("listaDestinatari_" + i,vis);
				 	}
			 	}
			 	
			 	showObj("Intestazione",vis);
			 	showObj("Indirizzo",vis);
			 	showObj("Tipo",vis);
			 	showObj("Cod.Fisc.",vis);
			}
			
			function showDestinatari(){
				var obj=getObjectById("onDestinatari");
				var visibile=obj.style.display=="none";
				showObj("offDestinatari",!visibile);
				showObj("onDestinatari",visibile);
						
				mostraElementiListaDestinatariNascosti(!visibile);
			} 
			//mostraElementiListaDestinatariNascosti(false);
			
			
			function showDoc(){
				var obj=getObjectById("onDoc");
				var visibile=obj.style.display=="none";
				showObj("offDoc",!visibile);
				showObj("onDoc",visibile);
						
				mostraElementiListaDoc(!visibile);
			}
			
			mostraElementiListaDoc(false);
			
			function mostraElementiListaDoc(vis){
				var num="${numElementiListaDoc}";
			 	if(num>0){
				 	for(var i=0; i<=num-1;i++){
				 		showObj("listaDocumenti_" + i,vis);
				 	}
			 	}
			 	showObj("Descrizione",vis);
			 	showObj("Nome",vis);
			}
			
			
			
			function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
				var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
				document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
			}
			

		
		
		$(":password").bind("cut copy paste",function(e) {e.preventDefault();});
		
		function controlloOggettoTestoLettera(){
			var esito = "OK";
			var oggetto = getValue("COMMSGOGG");
			var testo = getValue("COMMSGTES");
			if(oggetto==null || oggetto==""){
				alert("Il campo 'Oggetto' è obbligatorio");
				esito = "NOK";
			} else if(testo==null || testo==""){
				alert("Il campo 'Testo' è obbligatorio");
				esito = "NOK";
			}
			return esito;
		}
		
		function sceltaInvio() {
			var idStipula = "${idStipula}";
			var idconfi = "${idconfi}";
			var opzInvio = 0;
			if($("#radiomodstip1").prop('checked'))
				opzInvio = 1;

			if($("#radiomodstip2").prop('checked'))
				opzInvio = 2;
				
			if (opzInvio==0){
				alert("Selezionare almeno un opzione");
			}else{
				var ref = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1stipula/g1stipula-PubblicaDocumenti.jsp&idStipula="+idStipula+"&opzInvio="+opzInvio+"&idconfi="+idconfi;
				<c:if test="${integrazioneWSDM =='1' }">
					ref += "&step=1";
				</c:if>
				document.location.href = ref;
			}
		}
	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>