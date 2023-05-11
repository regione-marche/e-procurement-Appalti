<%/*
   * Created on 10-dec-2013
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

<c:choose>
	<c:when test='${not empty requestScope.ordineTrasmesso and requestScope.ordineTrasmesso eq "SI"}' >
<script type="text/javascript">
		window.opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>
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
			<c:when test='${!empty param.idconfi}'>
				<c:set var="idconfi" value="${param.idconfi}" />
			</c:when>
			<c:otherwise>
				<c:set var="idconfi" value="${idconfi}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${!empty param.codrup}'>
				<c:set var="codrup" value="${param.codrup}" />
			</c:when>
			<c:otherwise>
				<c:set var="codrup" value="${codrup}" />
			</c:otherwise>
		</c:choose>
		
		<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar, idconfi)}'/>
		<c:if test="${integrazioneWSDM eq 1}" >
			<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext,idconfi)}'/>
		</c:if>
		

		<c:choose>
			<c:when test='${!empty param.ncont}'>
				<c:set var="ncont" value="${param.ncont}" />
			</c:when>
			<c:otherwise>
				<c:set var="ncont" value="${ncont}" />
			</c:otherwise>
		</c:choose>

		<c:choose>
			<c:when test='${!empty param.nprot}'>
				<c:set var="nprot" value="${param.nprot}" />
			</c:when>
			<c:otherwise>
				<c:set var="nprot" value="${nprot}" />
			</c:otherwise>
		</c:choose>

		<c:choose>
			<c:when test='${!empty param.ditta}'>
				<c:set var="ditta" value="${param.ditta}" />
			</c:when>
			<c:otherwise>
				<c:set var="ditta" value="${ditta}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${!empty param.nomeEntita}'>
				<c:set var="nomeEntita" value="${param.nomeEntita}" />
			</c:when>
			<c:otherwise>
				<c:set var="nomeEntita" value="${nomeEntita}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${!empty param.cig}'>
				<c:set var="cig" value="${param.cig}" />
			</c:when>
			<c:otherwise>
				<c:set var="cig" value="${cig}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${!empty param.isODA}'>
				<c:set var="isODA" value="${param.isODA}" />
			</c:when>
			<c:otherwise>
				<c:set var="isODA" value="${isODA}" />
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
			
		<c:set var="genereModelloComunicazione" value="52"/>

		<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="ODA-TRASMETTI">
		<c:if test='${integrazioneWSDM eq "1" && !empty cig}' >
			<gene:redefineInsert name="head">
				<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
				<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
				<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
				<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmuffici.js?v=${sessionScope.versioneModuloAttivo}"></script>
			</gene:redefineInsert>
		</c:if>
		<gene:setString name="titoloMaschera"	value="Trasmetti ordine di acquisto" />

		<c:set var="modelloMailPec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetMailPecModelloFunction", pageContext, genereModelloComunicazione, isODA,ngara)}' />

		<c:set var="esitoControlloMail" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMailPecImpresaFunction", pageContext, ditta)}' />
		
		<c:set var="esitoDomentiAttesaFirma" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDocumentiAttesaFirmaFunction", pageContext, codgar, "11")}' />
		<c:set var="htmlSupport" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "comunicazione.supportoHtml")}'/>

			<gene:redefineInsert name="corpo">		
				<c:choose>
					<c:when test='${not empty requestScope.ordineTrasmesso and requestScope.ordineTrasmesso eq "Errori"}' >
						<table class="dettaglio-notab">
							<tr>
								<td class="valore-dato" colspan="2">
									<br/>
									Non è possibile trasmettere l'ordine di acquisto.
									<br/>
								</td>
							</tr>
							<tr>
								<td colspan="2" class="comandi-dettaglio">
									<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
								</td>
							</tr>
						</table>
					</c:when>
					<c:when test='${(empty nprot && integrazioneWSDM ne "1") || empty cig || esitoDomentiAttesaFirma eq "true"}' >
						<table class="dettaglio-notab">
							<tr>
								<td class="valore-dato" colspan="2">
									<br/>
									Non è possibile trasmettere l'ordine di acquisto.
									<c:if test="${empty nprot && (integrazioneWSDM ne '1')}">
										<br>Non è stato ancora valorizzato il <b>numero protocollo</b>.
									</c:if>
									<c:if test="${empty cig }">
										<br>Non è stato ancora valorizzato il <b>codice CIG</b>.
									</c:if>
									<c:if test="${esitoDomentiAttesaFirma eq 'true' }">
										<br>Vi sono allegati all'ordine in attesa di firma.
									</c:if>
									<br/>
									<br/>
								</td>
							</tr>
							<tr>
								<td colspan="2" class="comandi-dettaglio">
									<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
								</td>
							</tr>
						</table>
					</c:when>
					<c:otherwise>
						<gene:formScheda entita="GARECONT" where= "GARECONT.NGARA = '${ngara}' AND GARECONT.NCONT = ${ncont}" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupTrasmettiOrdine">
								<gene:gruppoCampi>
									<gene:campoScheda nome="Destinatario">
										<td colspan="2"><b>Destinatario</b></td>
									</gene:campoScheda>
									<gene:campoScheda campo="CODGAR1" entita="GARE" where="GARE.NGARA = GARECONT.NGARA" visibile="false"/>
									<gene:campoScheda campo="NOMIMA" entita="GARE" where="GARE.NGARA = GARECONT.NGARA" modificabile="false" title="Ragione sociale operatore"/>
									<c:choose>
										<c:when test='${not empty requestScope.emailPec || abilitatoInvioMailDocumentale eq "true"}' >
											<gene:campoScheda campo="FDESMAIL" obbligatorio="true" modificabile="false" value="${requestScope.emailPec}" campoFittizio="true" definizione="T100;0;;;G_EMAI2IP"/>
											<gene:campoScheda campo="FMAILPEC" campoFittizio="true" value="1" visibile="false" definizione="T100;0;;;"/>
										</c:when>
										<c:otherwise>
											<gene:campoScheda campo="FDESMAIL" obbligatorio="true" modificabile="false" value="${requestScope.email}" campoFittizio="true" definizione="T100;0;;;G_EMAIIP"/>
											<gene:campoScheda campo="FMAILPEC" campoFittizio="true" value="0" visibile="false" definizione="T100;0;;;"/>
										</c:otherwise>
									</c:choose>
								</gene:gruppoCampi>
								<gene:gruppoCampi>
									<gene:campoScheda nome="DettaglioComunicazione">
										<td colspan="2"><b>Dettaglio comunicazione</b></td>
									</gene:campoScheda>
									<gene:campoScheda campo="FCOMMSGOGG" obbligatorio="true" value="${requestScope.oggettoMail}" campoFittizio="true" definizione="T300;0;;;COMMSGOGG"/>
									<gene:campoScheda campo="FCOMINTEST" value="${gene:if(abilitatoInvioMailDocumentale eq 'true', '2', requestScope.abilitaIntestazioneVariabile) }" campoFittizio="true" definizione="T2;0;;SN;COMINTEST" visibile="${ abilitatoInvioMailDocumentale ne 'true'}"/>
									<gene:campoScheda campo="FCOMMSGTIP" value="2" campoFittizio="true" definizione="T2;0;;SN;COMMSGTIP" visibile='${htmlSupport eq "1"}'/>
									<gene:campoScheda campo="FCOMMSGTES" obbligatorio="true" value="${requestScope.testoMail}" campoFittizio="true" gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoTestoComunicazioneHTML" definizione="T2000;0;;CLOB;COMMSGTES"/>
									<gene:campoScheda campo="FCOMMITT" value="${gene:if(abilitatoInvioMailDocumentale eq 'true', '', requestScope.mittenteMail)}" campoFittizio="true" definizione="T60;0;;;COMMITT" visibile="${ abilitatoInvioMailDocumentale ne 'true'}"/>
								</gene:gruppoCampi>
								<gene:gruppoCampi>
									<gene:campoScheda nome="DocumentoOrdine">
										<td colspan="2"><b>Documento d'ordine</b></td>
									</gene:campoScheda>
									<gene:campoScheda campo="FDIGNOMDOC" obbligatorio="true" modificabile="false" campoFittizio="true" definizione="T100;0;;;DIGNOMDOC" visibile="false" value=""/>
									<gene:campoScheda title="Nome file" nome="NomeFile">
										<input type="file" name="selezioneFile" id="selezioneFile" class="file" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFile();'/>
									</gene:campoScheda>
								</gene:gruppoCampi>
								<gene:campoScheda campo="NGARA" visibile="false" />
								<gene:campoScheda campo="NCONT" visibile="false" />
								<gene:campoScheda campo="CODIMP" visibile="false" />
								<input type="hidden" id="ngara"  name="ngara" value="${ngara }" />
								<input type="hidden" name="ditta" id="ditta" value="${ditta}" />
								<input type="hidden" name="nprot" id="nprot" value="${nprot}" />
								<input type="hidden" name="nomeEntita" id="nomeEntita" value="${nomeEntita}" />
								<input type="hidden" name="cig" id="cig" value="${cig}" />
								<input type="hidden" name="isODA" id="isODA" value="${isODA}" />
								<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}" />
								<input type="hidden" name="integrazioneWSDM" id="integrazioneWSDM" value="${integrazioneWSDM}" />
								<c:if test="${integrazioneWSDM =='1'}">
								<table class="dettaglio-notab" id="datiLogin">
									<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp"></jsp:include>
								</table>
								<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-internoDati.jsp"></jsp:include>
									<input type="hidden" name="step" id="step" value="${step}" />
									<input type="hidden" id="servizio"  value="FASCICOLOPROTOCOLLO" />
									<input type="hidden" id="syscon" value="${profiloUtente.id}" /> 
									<input type="hidden" id="tiposistemaremoto" value="" />
									<input id="tabellatiInDB" type="hidden" value="" />
									<input type="hidden" id="entita"  value="GARE" /> 
									<input type="hidden" id="idprg"  value="PG" />
									<input type="hidden" id="key1"  name="key1" value="${ngara }" />
									<input type="hidden" id="key2"  name="key2" value="" /> 
									<input type="hidden" id="key3"  name="key3" value="" /> 
									<input type="hidden" id="key4"  name="key4" value="" /> 
									<input type="hidden" id="chiaveOriginale" value="${ngara }" />
									<input id="abilitazioneGare" type="hidden" value="${profiloUtente.abilitazioneGare}" />
								</c:if>
								<input id="codrup" name="codrup" type="hidden" value="${codrup}" />
								<input id="acronimoR" name="acronimoR" type="hidden" value="" />
								<input id="nomeR" name="nomeR" type="hidden" value="" />
								<c:choose>
									<c:when test="${integrazioneWSDM =='1'}">
										<c:set var="testoBottone" value="Protocolla e invia"/>
									</c:when>
										<c:otherwise>
										<c:set var="testoBottone" value="Conferma"/>
									</c:otherwise>
								</c:choose>
								
								<gene:campoScheda>
									<table class="arealayout" id='tabellaTrasmettiOrdine'>
										<tr>
											<td class="comandi-dettaglio" colSpan="2">
												<INPUT type="button" id="confermaInvio" class="bottone-azione" value='${testoBottone }' title='${testoBottone }' onclick="javascript:trasmettiOrdine();">
												<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
											</td>
										</tr>
									</table>
								</gene:campoScheda>
						</gene:formScheda>
					</c:otherwise>
				</c:choose>
				<gene:javaScript>

					document.forms[0].encoding="multipart/form-data";

					document.forms[0].jspPathTo.value="gare/garecont/garecont-popup-trasmettiOrdine.jsp";

					var idconfi = "${idconfi}";

					function trasmettiOrdine(){
						
						//Controllo presenza oggetto e testo della comunicazione
						if(controlloOggettoTestoLettera() == "NOK")
							return;
						
						var controlliOk = true;
						var selezioneFile = document.getElementById("selezioneFile").value;
						if((selezioneFile == null || selezioneFile == "")){
							alert("Si deve inserire l'allegato.");
							controlliOk = false;
						}	
						
						
						<c:choose>
						<c:when test="${ integrazioneWSDM =='1'}">
							_controlloDelegaInvioMailAlDocumentale();
							if(_delegaInvioMailDocumentaleAbilitata == 1 && (_tipoWSDM == "PALEO" || _tipoWSDM == "JIRIDE" || _tipoWSDM == "ENGINEERING" 
								|| _tipoWSDM == "ENGINEERINGDOC" || _tipoWSDM == "TITULUS" || _tipoWSDM == "ARCHIFLOW" || _tipoWSDM == "SMAT")){
								var esitoControlloMail = "${ esitoControlloMail}";
								if(esitoControlloMail ==0 || esitoControlloMail == 1){
									alert("E' necessario specificare l'indirizzo pec di destinazione.");
									controlliOk = false;
								}
							}
						</c:when>
						<c:otherwise>
							var mail = document.getElementById("FDESMAIL").value;
							if (mail == null || mail == "") {
								alert("E' necessario specificare l'indirizzo pec o la mail di destinazione.");
								controlliOk = false;
							}
						</c:otherwise>
						</c:choose>
						var oggettoComunicazione = document.getElementById("FCOMMSGOGG").value;
						if (oggettoComunicazione == null || oggettoComunicazione == "") {
							alert("Si deve specificare il corpo del messaggio.");
							controlliOk = false;
						}
						<c:if test="${ integrazioneWSDM =='1'}">
							
							
							if (controlliOk) {
								var step= $("#step").val();
								if(step==1){
									_getComunicazione();
									_controlloPresenzaFascicolazione();
									_controlloFascicoliAssociati();
									_inizializzazioni();
									$("#step").val("2");
									inizializzazionePagina("2");
									return;
								}else{
									//Controlli sulla valorizzazione dei campi obbligatori
									var errori = controlloCampiObbligatori();
									if(!errori && "LAPISOPERA" == $("#tiposistemaremoto").val() ){
										var nomePrimoAllegato = getValue("FDIGNOMDOC");
										var esito=controlloFormatoAllegato(nomePrimoAllegato);
										if(!esito){
											var msg="ATTENZIONE:  il sistema di protocollo accetta solo comunicazioni che abbiano un allegato firmato digitalmente  nel formato '.pdf.p7m'.\r\nPer procedere alla trasmissione dell'ordine, occorre selezionare un file valido.";
											alert(msg);
											errori=true;
										}
									}
									if(errori)
										controlliOk = false;
									else{
										_setWSLogin();
										if("ARCHIFLOWFA" == $("#tiposistemaremoto").val())
											$('#classificafascicolonuovo').attr('disabled', false);
									}
									
								}
							}
						</c:if>
						if (controlliOk) {
							<c:if test="${ integrazioneWSDM =='1'}">
							if("TITULUS" == $("#tiposistemaremoto").val()){
								$("#codiceaoodes").val($("#codiceaoonuovo option:selected").text());
								$("#codiceufficiodes").val($("#codiceufficionuovo option:selected").text());
							}
							if("JDOC" == $("#tiposistemaremoto").val()){
								$("#nomeR").val($("#nomeRup").text());
								$("#acronimoR").val($("#acronimoRup").text());
							}
							</c:if>	
							bloccaRichiesteServer();
							setTimeout("schedaConferma();", 150);
						}
					}

					function scegliFile() {
						var selezioneFile = document.getElementById("selezioneFile").value;
						var lunghezza_stringa = selezioneFile.length;
						var posizione_barra = selezioneFile.lastIndexOf("\\");
						var nome = selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
						if(nome.length>100){
							alert("Il nome del file non può superare i 100 caratteri!");
							document.getElementById("selezioneFile").value="";
							setValue("FDIGNOMDOC","");
						}else{
							setValue("FDIGNOMDOC" ,nome);
						}
					}
					
					<c:if test="${ integrazioneWSDM =='1' && !empty cig}">
						
						function apriListaUffici() {
								_ctx = "${pageContext.request.contextPath}";
								$("#finestraListaUffici").dialog('option','width',700);
								$("#finestraListaUffici").dialog("open");
								_creaContainerListaUffici();
							}
						
						function inizializzazionePagina(step){
							if(step=="1"){
								$("#datiLogin").hide();
								$("#datiProtocollo").hide();
							}else{
								$("#datiLogin").show();
								$("#datiProtocollo").show();
								$("#rowDestinatario").hide();
								$("#rowGARE_NOMIMA").hide();
								$("#rowFDESMAIL").hide();
								$("#rowFMAILPEC").hide();
								$("#rowDettaglioComunicazione").hide();
								$("#rowFCOMMSGOGG").hide();
								$("#rowFCOMINTEST").hide();
								$("#rowFCOMMSGTIP").hide();
								$("#rowFCOMMSGTES").hide();
								$("#rowFCOMMITT").hide();
								$("#rowDocumentoOrdine").hide();
								$("#rowFDIGNOMDOC").hide();
								$("#rowNomeFile").hide();
								
								$("#oggettodocumento").val(getValue("FCOMMSGOGG"));
								if (_tipoWSDM == "ARCHIFLOWFA" ){
									var testoOggettoDocumento= "${ngara} - " + $('#oggettodocumento').val() +" - " + _oggettoGara;
									$("#oggettodocumento").val(testoOggettoDocumento);
								}else if (_tipoWSDM == "TITULUS" ){
									oggettoDocumentoTitulus();
								}else if (_tipoWSDM == "ENGINEERINGDOC" ){
									var testoOggettoDocumento= "${ngara} - " + $('#oggettodocumento').val();
									$("#oggettodocumento").val(testoOggettoDocumento);
								}else if(_fascicoliPresenti==0 && "LAPISOPERA" == _tipoWSDM){
									$("#confermaInvio").hide();
								}
							}
						}
					
					
						var step= $("#step").val();
						inizializzazionePagina(step);
						
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
						
						<c:if test="${requestScope.ordineTrasmesso eq 'Errori'}">
							if(step==2){
								//Nel caso di errore(sia gestito che non) nella procedura di protocollazione, 
								//si deve forzare la rilettura dei dati, così come avviene quando si passa dal
								//primo step al secondo.
								_getComunicazione();
								_tipoWSDM = $("#tiposistemaremoto").val();
								_controlloPresenzaFascicolazione();
								_controlloFascicoliAssociati();

							}
						</c:if>
						_inizializzazioni();
						<c:if test="${requestScope.ordineTrasmesso eq 'Errori'}">
							if(step==2){
								//Nel caso di errore(sia gestito che non) nella procedura di protocollazione, 
								//si deve forzare la rilettura dei dati, così come avviene quando si passa dal
								//primo step al secondo.
								_gestioneInserimentoInFascicolo();
							}
						</c:if>
						//Se il campo classificafascicolonuovo contiene un solo valore, si riporta
						//in classificadocumento tale valore
						if($("#classificafascicolonuovo option").length == 2 ){
							$('#classificadocumento').val($('#classificafascicolonuovo').val());
						}
						
						
						
						
						
						
						/*
						 * Lettura della singola comunicazione.
						 * Utilizzata per popolare i dati generali della comunicazione,
						 * la lista dei destinatari e la lista degli allegati.
						 */
						function _getComunicazione() {
							_controlloDelegaInvioMailAlDocumentale();			
							var servizio = $("#servizio").val();
							_getTipoWSDM();
							if (_tipoWSDM == "IRIDE" || _tipoWSDM == "JIRIDE") {
								$("#codiceregistrodocumento").hide();
								$("#codiceregistrodocumento").closest('tr').hide();
								$("#idindice").hide();
								$("#idindice").closest('tr').hide();
								$("#idtitolazione").hide();
								$("#idtitolazione").closest('tr').hide();
								$("#idunitaoperativamittente").hide();
								$("#idunitaoperativamittente").closest('tr').hide();
							}
							
							if (_tipoWSDM == "PALEO") {
								$("#classificadocumento").hide();
								$("#classificadocumento").closest('tr').hide();
								$("#tipodocumento").hide();
								$("#tipodocumento").closest('tr').hide();
								$("#mittenteinterno").hide();
								$("#mittenteinterno").closest('tr').hide();
								$("#idindice").hide();
								$("#idindice").closest('tr').hide();
								$("#idtitolazione").hide();
								$("#idtitolazione").closest('tr').hide();
								$("#idunitaoperativamittente").hide();
								$("#idunitaoperativamittente").closest('tr').hide();
								$("#indirizzomittente").hide();
								$("#indirizzomittente").closest('tr').hide();
							}
							
							if (_tipoWSDM == "ENGINEERING") {
								$("#classificadocumento").hide();
								$("#classificadocumento").closest('tr').hide();
								$("#codiceregistrodocumento").hide();
								$("#codiceregistrodocumento").closest('tr').hide();
								$("#tipodocumento").hide();
								$("#tipodocumento").closest('tr').hide();
								$("#mittenteinterno").hide();
								$("#mittenteinterno").closest('tr').hide();
								$("#mezzoinvio").hide();
								$("#trMezzo").hide();
								$("#mezzoinvio").closest('tr').hide();
								$("#indirizzomittente").hide();
								$("#indirizzomittente").closest('tr').hide();
							}
							
							if ((_tipoWSDM != "JIRIDE"  && _tipoWSDM != "ARCHIFLOW" && _tipoWSDM != "ARCHIFLOWFA") || (_tipoWSDM == "JIRIDE" && _delegaInvioMailDocumentaleAbilitata != 1) || (_tipoWSDM == "ARCHIFLOW" && _delegaInvioMailDocumentaleAbilitata != 1)
								|| (_tipoWSDM == "ARCHIFLOWFA" && _delegaInvioMailDocumentaleAbilitata != 1)) {
								$("#indirizzomittente").hide();
								$("#indirizzomittente").closest('tr').hide();
								$('#indirizzomittente option').eq(0).prop('selected', true);
							}
							
							if (_tipoWSDM != "IRIDE" && _tipoWSDM != "JIRIDE" && _tipoWSDM != "TITULUS") {
								$("#mezzoinvio").hide();
								$("#mezzoinvio").closest('tr').hide();
							}else if(_tipoWSDM == "TITULUS"){
	                            //Per TITULUS il campo non è obbligatorio
	                            $("#obblmezzoinvio").hide();
	                        }	
							
							if(_tipoWSDM != "ARCHIFLOW" && _tipoWSDM != "ARCHIFLOWFA" && _tipoWSDM != "JDOC" && _tipoWSDM != "NUMIX"){
	                        	$("#mezzo").hide();
								$("#mezzo").closest('tr').hide();
								
								if(_tipoWSDM != "ARCHIFLOWFA" && $("#inserimentoinfascicolo option:selected").val() == "SI_FASCICOLO_ESISTENTE")
									_setDescrizioneCodiceTabellato("classificafascicolo",$("#classificafascicolonuovo").val(),"classificafascicolodescrizione",2);
	                        }
														
							if(_delegaInvioMailDocumentaleAbilitata == 1 && (_tipoWSDM == "JIRIDE" || _tipoWSDM == "PALEO" || _tipoWSDM == "ENGINEERING" 
								|| _tipoWSDM == "ENGINEERINGDOC" || _tipoWSDM == "TITULUS" || _tipoWSDM == "ARCHIFLOW" || _tipoWSDM == "SMAT")){
								setValue("FCOMINTEST","2");
								//showObj("rowFCOMINTEST",false);
								setValue("FCOMMITT","");
								//showObj("rowFCOMMITT", false);
							}
							
											
						}
						
						$('#inserimentoinfascicolo').change(function() {
							_gestioneInserimentoInFascicolo();
					    });
					    
						$('#classificafascicolonuovo').change(function() {
							if (_tipoWSDM == "TITULUS"){
								var valore=$( "#classificafascicolonuovo option:selected" ).val();
								var codice = null;
								var voce = null;
								var descrizione =null;
								if(valore != null && valore != ""){
									codice = valore.split(" .-+-. ")[0];
									descrizione = valore.split(" .-+-. ")[1];
									voce = valore.split(" .-+-. ")[2];
									
								}
								$('#classificadocumento').val(codice);
								$("#classificadescrizione").val(descrizione);
								$("#voce").val(voce);
							}else{
								$('#classificadocumento').val($('#classificafascicolonuovo').val());
							}
					    });
				   
						//Per TITULUS alla variazione della login si deve caricare il tabellato del codice AOO
						$('#username').change(function() {
							if (_tipoWSDM == "TITULUS"){
								caricamentoCodiceAooTITULUS();
								_popolaTabellatoClassificaTitulus();
							}else if (_tipoWSDM == "JIRIDE"){
								if(_letturaMittenteDaServzio){
									$('#mittenteinterno').empty();
									_popolaTabellatoJirideMittente("mittenteinterno");
									if(_delegaInvioMailDocumentaleAbilitata == 1){
										$('#indirizzomittente').empty();
										_popolaTabellatoJirideMittente("indirizzomittente");
									}
								}
								caricamentoStrutturaJIRIDE();
							}else if (_tipoWSDM == "NUMIX"){
								caricamentoClassificaNumix();
							}
						});
						
						$('#password').change(function() {
							if (_tipoWSDM == "TITULUS"){
								caricamentoCodiceAooTITULUS();
								_popolaTabellatoClassificaTitulus();
							}else if (_tipoWSDM == "NUMIX"){
								caricamentoClassificaNumix();
							}
					    });
					    
					    $('#codiceaoonuovo').change(function() {
							if (_tipoWSDM == "TITULUS"){
								caricamentoUfficioTITULUS();
							}
					    });
					    
					    $('#codicefascicolo').change(function() {
							if (_tipoWSDM == "ARCHIFLOWFA"){
								gestionemodificacampofascicolo();
							}
			    		});
			    		
			    		$('#annofascicolo').change(function() {
							if (_tipoWSDM == "PRISMA"){
								gestionemodificacampoannofascicolo();
							}
							if (_tipoWSDM == "ITALPROT"){
								$('#listafascicoli').empty();
								$('#codicefascicolo').val(); 
							}
						});
						
						$('#numerofascicolo').change(function() {
							if (_tipoWSDM == "PRISMA"){
								gestionemodificacamponumerofascicolo();
							}
						});
						
						$('#ruolo').change(function() {
							if (_tipoWSDM == "JIRIDE"){
								caricamentoStrutturaJIRIDE();
							}
					    });
						
						$('#classificafascicolonuovoItalprot').change(function() {
							$('#listafascicoli').empty();
							$('#codicefascicolo').val(); 
							
						});
    
						$('#listafascicoli').on('change',  function () {
							var str = this.value;
							gestioneSelezioneFascicolo(str);
							
						});
					</c:if>
					
					function annulla(){
						window.close();
					}
					
					function controlloOggettoTestoLettera(){
						var esito = "OK";
						var oggetto = getValue("FCOMMSGOGG");
						var testo = getValue("FCOMMSGTES");
						if(oggetto==null || oggetto==""){
							alert("Il campo 'Oggetto' è obbligatorio");
							esito = "NOK";
						} else if(testo==null || testo==""){
							alert("Il campo 'Testo' è obbligatorio");
							esito = "NOK";
						}
						return esito;
					}		
				</gene:javaScript>
			</gene:redefineInsert>
		</gene:template>					
	</c:otherwise>
</c:choose>