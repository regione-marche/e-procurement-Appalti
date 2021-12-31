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

<c:set var="codiceGara" value='${gene:getValCampo(key, "DITG.CODGAR5")}'/>
<c:set var="numeroGara" value='${gene:getValCampo(key, "DITG.NGARA5")}'/>
<c:set var="codiceDitta" value='${gene:getValCampo(key, "DITG.DITTAO")}'/>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="inclause" value="(${param.inclause})"/>
<c:set var="numeroDitteAbilitate" value="${param.numeroDitteAbilitate}"/>
<c:set var="genereModelloComunicazione" value="51"/>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.InizializzaUpdateListaFunction" parametro="${key}" />

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<c:choose>
	<c:when test='${!empty param.step}'>
		<c:set var="step" value="${param.step}" />
	</c:when>
	<c:otherwise>
		<c:set var="step" value="${step}" />
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

<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codiceGara, idconfi)}'/>
<c:if test="${integrazioneWSDM eq 1}" >
	<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext,idconfi)}'/>
</c:if>

<gene:template file="popup-template.jsp" gestisciProtezioni="true"	schema="GARE" idMaschera="DITG-lista">
	
	
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${contextPath}/js/jHtmlArea-0.7.5.min.js"></script> 
		<link rel="Stylesheet" type="text/css" href="${contextPath}/css/jquery/jHtmlArea/jHtmlArea.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
				
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera"	value="Operatori in corso di abilitazione" />
	<c:set var="whereDITG"	value="DITG.CODGAR5='${codiceGara}' and (DITG.NGARA5='${numeroGara }') and DITG.DITTAO in ${inclause} " />
		<% // Ridefinisco il corpo della lista %>
	<gene:redefineInsert name="corpo">
		<%// Creo la lista delle occorrenze appena abilitate%>
		<table class="lista" >
		       <tr>
			<br>E' possibile inviare una mail di notifica agli operatori che sono stati abilitati impostando l'opzione sotto.<br><br>
				Procedere al salvataggio dei dati:
			<br>
			<td class="valore-dato" colspan="2">
			<input id="radio1" type="radio" name="modoSalvataggio" value="OP1"
				onclick="javascript:aggiornaModoSalvataggio(1);" />con invio mail
			<br>
			<input id="radio2" type="radio" name="modoSalvataggio" value="OP2" checked
				onclick="javascript:aggiornaModoSalvataggio(2);" />senza invio mail
			<br>
			</td>
			</tr>
            </table>
            <table class="arealayout" id="tabellaTipoMail">
			<tr id="RigaTipoMail">
			<tr >
				<td>&nbsp;</td>
			</tr>
			<td class="valore-dato" colspan="2" id="TipoMail">
				Indicare il tipo di indirizzo da utilizzare per l'invio della notifica:
			<br>
			<br>
			<input id="fs1" type="radio" name="operazione" value="OP1"
				onclick="javascript:aggiornaSelMailPec(1);" /><span id="fs1Desc">E-mail</span>
			<br>
			<input id="fs2" type="radio" name="operazione" value="OP2" checked
				onclick="javascript:aggiornaSelMailPec(2);" /><span id="fs2Desc">PEC</span>
			<br>
			</td>
			</tr>
			<tr>
				<td${stileDati}>
				<gene:formLista entita="DITG"
						where='${whereDITG}' pagesize="20" tableclass="datilista"
						sortColumn="2" gestisciProtezioni="true">
						<gene:redefineInsert name="listaNuovo" />
						<gene:redefineInsert name="listaEliminaSelezione" />
						<gene:redefineInsert name="documentiAzioni" />
						<% // Qui va fatta una funzione che mi tira su 0 = niente 1=email 2 = PEC 3= entrambe.... %>
						<c:set var="modelloMailPec"
							value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetMailPecModelloFunction", pageContext,genereModelloComunicazione, "false",numeroGara)}' />
						<c:set var="resMailPec"
							value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMailPecImpresaFunction", pageContext,datiRiga.DITG_DITTAO)}' />
						<gene:campoLista title="&nbsp;" width="20">
							<img id="idSpuntaSelezione${datiRiga.row}" name="spuntaSelezione" width="16" height="16"
							 title="Invio mail ad operatore"	alt="Invio mail ad operatore" src="${pageContext.request.contextPath}/img/ico_check.gif"
								<c:if test="${empty requestScope.emailPec}">class="nascosto"</c:if> />
							<input id="esisteMail${datiRiga.row}" type="hidden" name="esisteMail" value="${! empty requestScope.email}" />
							<input id="esistePEC${datiRiga.row}" type="hidden"  name="esistePEC" value="${! empty requestScope.emailPec}" />
						</gene:campoLista>
						<gene:campoLista campo="DITTAO" visibile="false" edit="${updateLista eq 1}" />
						<gene:campoLista campo="NOMIMO" visibile="true" edit="${updateLista eq 1}" />
						<gene:campoLista campo="EMAIIP" entita="IMPR" where="IMPR.CODIMP = DITG.DITTAO" visibile="false" edit="${updateLista eq 1}" />
						<gene:campoLista campo="EMAILFITTIZIA" entita="IMPR" title="E-MAIL" campoFittizio="true" definizione="T10" value="${requestScope.email}" width="50" visibile="${abilitatoInvioMailDocumentale ne 'true' }"/>
						<gene:campoLista campo="EMAI2IP" entita="IMPR" where="IMPR.CODIMP = DITG.DITTAO" visibile="false" edit="${updateLista eq 1}" />
						<gene:campoLista campo="PECFITTIZIA" entita="IMPR" title="PEC" campoFittizio="true" definizione="T10" value="${requestScope.emailPec}" width="50" />
						<input type="hidden" name="integrazioneWSDM" id="integrazioneWSDM" value="${integrazioneWSDM}" />
						<c:if test="${integrazioneWSDM =='1'}">
							<table class="dettaglio-notab" id="datiLogin">
								<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp"></jsp:include>
							</table>
							<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-internoDati.jsp"></jsp:include>
							<input type="hidden" name="step" id="step" value="${step}" />
							<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
							<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
							<input id="tiposistemaremoto" type="hidden" value="" />
							<input id="tabellatiInDB" type="hidden" value="" />
							<input id="entita" type="hidden" value="GARE" /> 
							<input id="idprg" type="hidden" value="PG" />
							<input id="key1" type="hidden" name="key1" value="${numeroGara }" />
							<input id="key2" type="hidden" name="key2" value="" /> 
							<input id="key3" type="hidden" name="key3" value="" /> 
							<input id="key4" type="hidden" name="key4" value="" /> 
							<input id="idconfi" type="hidden" name="idconfi" value="${idconfi}" /> 
							<input type="hidden" id="chiaveOriginale" value="${numeroGara }" />
							<input id="abilitazioneGare" type="hidden" value="${profiloUtente.abilitazioneGare}" />
						</c:if>
				</gene:formLista>
				</td>
			</tr>

			<table class="dettaglio-notab" id='tabellaInvioMail'>
				<tr>
					<td>&nbsp;</td>
				</tr>
				<div id="composizione"><b>Composizione della mail:</b></div>
                <td class="etichetta-dato" id="oggetto" >Oggetto(*)</td>
				<td class="valore-dato" id="valoreOggetto"><input type="text" id="OGGETTOMAIL_TESTONOTA" name="defOGGETTOMAIL_TESTONOTA"
					value="${requestScope.oggettoMail}" size=60 /></td>
				<c:if test="${abilitatoInvioMailDocumentale ne 'true' }">
					<tr>
						<td class="etichetta-dato" id="intestazione">Intestazione</td>
						<td class="valore-dato" id="valoreIntestazione">Spett.le <i>Ragione Sociale</i>
						</td>
					</tr>
				</c:if>
				<tr>
					<td class="etichetta-dato" id="testoMail">Testo Mail (*)</td>
					<td class="valore-dato"  id="valoreTestoMail"><input type="hidden" name="defTESTOMAIL_TESTONOTA" value="" />
					 <textarea id="TESTOMAIL_TESTONOTA" name="TESTOMAIL_TEXT" title="Testo"
							onchange="" rows="4" cols="75">${requestScope.testoMail}</textarea>
						&nbsp;</td>
				
				<c:choose>
					<c:when test="${abilitatoInvioMailDocumentale eq 'true' }">
						<input type="hidden" id="MITTENTEMAIL_TESTONOTA" name="defMITTENTEMAIL_TESTONOTA" value="" />
					</c:when>
					<c:otherwise>
						<tr>
							<td class="etichetta-dato" id="mittente">Mittente(*)</td>
							<td class="valore-dato" id="valoreMittente"><input type="text" id="MITTENTEMAIL_TESTONOTA" name="defMITTENTEMAIL_TESTONOTA"
								value="${requestScope.mittenteMail}" size=60 /></td>
						</tr>
					</c:otherwise>
				</c:choose>
				
				<tr>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td class="comandi-dettaglio" colSpan="2">
					<c:if	test='${updateLista ne 1}'>
						<c:choose>
							<c:when test="${integrazioneWSDM =='1'}">
								<c:set var="testoBottone" value="Protocolla e invia"/>
							</c:when>
							<c:otherwise>
								<c:set var="testoBottone" value="Invia"/>
							</c:otherwise>
						</c:choose>
						<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value='${testoBottone }' title='${testoBottone }' onclick="javascript:inviaMail();">
						<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
					</c:if> &nbsp;</td>
				</tr>
			</table>
			<table class="arealayout" id='tabellaNoInvioMail'>
				<tr>
					<td class="comandi-dettaglio" colSpan="2">
					<c:if	test='${updateLista ne 1}'>
						<INPUT type="button" class="bottone-azione" value='Conferma' title='Invia' onclick="javascript:conferma();">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
					</c:if> &nbsp;</td>
				</tr>
			</table>
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
			
		function inviaMail(){
			var winOpener = window.opener;
			winOpener.document.getElementById("isInvioMailOperatoriAbilitati").value="true";
			winOpener.document.getElementById("listaOperatoriAbilitati").value="${inclause}";
			winOpener.document.getElementById("numeroOperatoriAbilitati").value="${numeroDitteAbilitate}";
			var isMailChecked = document.getElementById("fs1").checked;
			var isPecChecked = document.getElementById("fs2").checked;
			var isOggettoValorizzato = document.getElementById("OGGETTOMAIL_TESTONOTA");
			var isMittenteValorizzato = document.getElementById("MITTENTEMAIL_TESTONOTA");
			var abilitatoInvioMailDocumentale ="${abilitatoInvioMailDocumentale }";
			var testo = document.getElementById("TESTOMAIL_TESTONOTA");
			
			if(isMailChecked){
				winOpener.document.getElementById("flagMailPec").value="1";
			}
			if(isPecChecked){
				winOpener.document.getElementById("flagMailPec").value="2";
			}
			
			var numeroOccorrenze = ${currentRow}+1;
			//var numeroOperatoriNonSelezionati=0;
			var esisteOperatoreNonSelezionato=false;
			for(var i=1; i <= numeroOccorrenze; i++){	
				if(isMailChecked){
					if(this.document.getElementById('esisteMail'+i).value=="false"){
						//numeroOperatoriNonSelezionati++;
						esisteOperatoreNonSelezionato=true;
						break;
					}
				}else if(isPecChecked){
					if(this.document.getElementById('esistePEC'+i).value=="false"){
						//numeroOperatoriNonSelezionati++;
						esisteOperatoreNonSelezionato=true;
						break;
					}
				}
				
			}
			
			var testoMail="E-mail";
			if(isPecChecked){
				testoMail="PEC";
			}
			//if(numeroOperatoriNonSelezionati==numeroOccorrenze){
			if(esisteOperatoreNonSelezionato){
		     	alert("Tutti gli operatori a cui deve essere inviata la notifica devono avere un indirizzo " + testoMail + " specificato in anagrafica");
			}else if((isOggettoValorizzato.value=='' || testo.value == '' || isMittenteValorizzato.value=='' ) && abilitatoInvioMailDocumentale != "true"){
				alert("Non è possibile procedere senza valorizzare l'oggetto, il testo ed il mittente!");
			}else if((isOggettoValorizzato.value=='' || testo.value == '') && abilitatoInvioMailDocumentale == "true"){
				alert("Non è possibile procedere senza valorizzare l'oggetto ed il testo!");
			}else{
				<c:if test="${integrazioneWSDM =='1'}">
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
							winOpener.document.getElementById("tiposistemaremoto").value=$("#tiposistemaremoto").val();
							winOpener.document.getElementById("key1").value=$("#key1").val();
							winOpener.document.getElementById("chiaveOriginale").value=$("#chiaveOriginale").val();
							
							winOpener.document.getElementById("classificadocumento").value=$("#classificadocumento").val();
							winOpener.document.getElementById("tipodocumento").value=$("#tipodocumento").val();
							winOpener.document.getElementById("oggettodocumento").value=$("#oggettodocumento").val();
							winOpener.document.getElementById("mittenteinterno").value=$("#mittenteinterno").val();
							winOpener.document.getElementById("indirizzomittente").value=$("#indirizzomittente").val();
							winOpener.document.getElementById("codiceregistrodocumento").value=$("#codiceregistrodocumento").val();
							winOpener.document.getElementById("inout").value=$("#inout").val();
							winOpener.document.getElementById("idindice").value=$("#idindice").val();
							winOpener.document.getElementById("idtitolazione").value=$("#idtitolazione").val();
							winOpener.document.getElementById("idunitaoperativamittente").value=$("#idunitaoperativamittente").val();
							winOpener.document.getElementById("inserimentoinfascicolo").value=$("#inserimentoinfascicolo").val();
							winOpener.document.getElementById("codicefascicolo").value=$("#codicefascicolo").val();
							winOpener.document.getElementById("oggettofascicolo").value=$("#oggettofascicolonuovo").val();
							winOpener.document.getElementById("classificafascicolo").value=$("#classificafascicolonuovo").val();
							winOpener.document.getElementById("descrizionefascicolo").value=$("#descrizionefascicolonuovo").val();
							winOpener.document.getElementById("annofascicolo").value=$("#annofascicolo").val();
							winOpener.document.getElementById("numerofascicolo").value=$("#numerofascicolo").val();
							winOpener.document.getElementById("username").value=$("#username").val();
							winOpener.document.getElementById("password").value=$("#password").val();
							winOpener.document.getElementById("ruolo").value=$("#ruolo").val();
							winOpener.document.getElementById("nome").value=$("#nome").val();
							winOpener.document.getElementById("cognome").value=$("#cognome").val();
							winOpener.document.getElementById("codiceuo").value=$("#codiceuo").val();
							winOpener.document.getElementById("mezzoinvio").value=$("#mezzoinvio").val();
							winOpener.document.getElementById("idutente").value=$("#idutente").val();
							winOpener.document.getElementById("idutenteunop").value=$("#idutenteunop").val();
							winOpener.document.getElementById("codiceaoonuovo").value=$("#codiceaoonuovo").val();
							winOpener.document.getElementById("codiceufficionuovo").value=$("#codiceufficionuovo").val();
							winOpener.document.getElementById("mezzo").value=$("#mezzo").val();
							winOpener.document.getElementById("societa").value=$("#societa").val();
							winOpener.document.getElementById("codiceGaralotto").value=$("#codicegaralotto").val();
							winOpener.document.getElementById("cig").value=$("#cig").val();
							winOpener.document.getElementById("supporto").value=$("#supporto").val();
							winOpener.document.getElementById("strutturaonuovo").value=$("#strutturaonuovo").val();
							winOpener.document.getElementById("tipofascicolonuovo").value=$("#tipofascicolonuovo").val();
							winOpener.document.getElementById("classificadescrizione").value=$("#classificadescrizione").val();
							winOpener.document.getElementById("voce").value=$("#voce").val();
							winOpener.document.getElementById("codiceaoodes").value=$("#codiceaoonuovo option:selected").text();
							winOpener.document.getElementById("codiceufficiodes").value=$("#codiceufficionuovo option:selected").text();
							winOpener.document.getElementById("RUP").value=$("#RUP").val();
							winOpener.document.getElementById("nomeRup").value=$("#nomeRup").text();
							winOpener.document.getElementById("acronimoRup").value=$("#acronimoRup").text();
							winOpener.document.getElementById("sottotipo").value=$("#sottotipo option:selected").val();
							_setWSLogin();
						}
					}
				</c:if>
				winOpener.document.getElementById("oggettoMail").value=document.getElementById("OGGETTOMAIL_TESTONOTA").value;
				winOpener.document.getElementById("testoMail").value=document.getElementById("TESTOMAIL_TESTONOTA").value;
				winOpener.document.getElementById("mittenteMail").value=document.getElementById("MITTENTEMAIL_TESTONOTA").value;
								
				winOpener.listaConferma();
				window.close();
			}
		}
		
		
		
		function conferma(){
			var winOpener = window.opener;
			winOpener.document.getElementById("isInvioMailOperatoriAbilitati").value="false";
			winOpener.listaConferma();
			window.close();
		}
		
		function aggiornaSelMailPec(tipoMail){
			var numeroOccorrenze = ${currentRow}+1;
			for(var i=1; i <= numeroOccorrenze; i++){	
				if(tipoMail =='1' ){
					if(this.document.getElementById('esisteMail'+i).value=="false"){
						$('#idSpuntaSelezione'+i).attr('class', 'nascosto');
					}else{
						$('#idSpuntaSelezione'+i).attr('class', '');
					}
				}
				if(tipoMail =='2' ){
					if(this.document.getElementById('esistePEC'+i).value=="false"){
						$('#idSpuntaSelezione'+i).attr('class', 'nascosto');
					}else{
						$('#idSpuntaSelezione'+i).attr('class', '');
					}
				}
			}
		}
		
	
		
		function annulla(){
			window.close();
		}
		
		function aggiornaModoSalvataggio(modo){
			if(modo==2){
				document.getElementById("tabellaTipoMail").style.display='none';
               	document.getElementById("tabellaInvioMail").style.display='none';
               	document.getElementById("composizione").style.display='none';
               	document.getElementById("tabellaNoInvioMail").style.display='';
               	$("#datiLogin").hide();
				$("#datiProtocollo").hide();
				$("#step").val("1");
            } else{
            	<c:if test="${ integrazioneWSDM =='1'}">
            		caricamentoDati();
            		_controlloDelegaInvioMailAlDocumentale();
            	</c:if>
            	document.getElementById("tabellaTipoMail").style.display='';
               	document.getElementById("tabellaInvioMail").style.display='';
               	document.getElementById("composizione").style.display='';
               	document.getElementById("tabellaNoInvioMail").style.display='none';
               	inizializzazionePagina(1);
               	<c:if test="${ integrazioneWSDM =='1'}">
               		if(_delegaInvioMailDocumentaleAbilitata == 1 && (_tipoWSDM == "PALEO" || _tipoWSDM == "JIRIDE"  || _tipoWSDM == "ENGINEERING" || _tipoWSDM == "TITULUS" || _tipoWSDM == "ARCHIFLOW" || _tipoWSDM == "SMAT")){
            			aggiornaSelMailPec(2);
            			//$("#fs1").attr('disabled',true);
            			//$("#fs1").hide();
            			//$("#fs1Desc").hide();
            			//$("#fs2").hide();
            			//$("#fs2Desc").hide();
            			
            		}
            	</c:if>
            }
	    }
	    
        aggiornaModoSalvataggio(2);
        <c:if test="${ integrazioneWSDM =='1'}">
        	
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
					if(_delegaInvioMailDocumentaleAbilitata == 1 && (_tipoWSDM == "JIRIDE" || _tipoWSDM == "PALEO" || _tipoWSDM == "ENGINEERING" || _tipoWSDM == "TITULUS" || _tipoWSDM == "SMAT")){
						/*
						$("#intestazione").hide();
						$("#valoreIntestazione").hide();
						if(document.getElementById("MITTENTEMAIL_TESTONOTA")!=null)
							document.getElementById("MITTENTEMAIL_TESTONOTA").value="";
						$("#mittente").hide();
						$("#valoreMittente").hide();
						*/
						$("#TipoMail").hide();
					}	
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
					$("#oggettodocumento").val($("#OGGETTOMAIL_TESTONOTA").val());
					if (_tipoWSDM == "ARCHIFLOWFA" ){
						var testoOggettoDocumento= "${numeroGara} - " + $('#oggettodocumento').val() +" - " + _oggettoGara;
						$("#oggettodocumento").val(testoOggettoDocumento);
					}
					if (_tipoWSDM == "TITULUS" ){
						oggettoDocumentoTitulus();
					}
				}
			}
			
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
			
			
			
			
			
			/*
			 * Lettura della singola comunicazione.
			 * Utilizzata per popolare i dati generali della comunicazione,
			 * la lista dei destinatari e la lista degli allegati.
			 */
			function _getComunicazione() {
						
				var servizio = $("#servizio").val();
				_getTipoWSDM();
				_controlloDelegaInvioMailAlDocumentale();
				if (_tipoWSDM == "IRIDE" ||_tipoWSDM == "JIRIDE") {
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
				
				if(_tipoWSDM != "ARCHIFLOW" && _tipoWSDM != "ARCHIFLOWFA" && _tipoWSDM != "JDOC"){
                	$("#mezzo").hide();
					$("#mezzo").closest('tr').hide();
					
					if(_tipoWSDM != "ARCHIFLOWFA" && $("#inserimentoinfascicolo option:selected").val() == "SI_FASCICOLO_ESISTENTE")
					_setDescrizioneCodiceTabellato("classificafascicolo",$("#classificafascicolonuovo").val(),"classificafascicolodescrizione",2);
					
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
				}
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
			
			
			$('#password').change(function() {
				if (_tipoWSDM == "TITULUS"){
					
					caricamentoCodiceAooTITULUS();
					_popolaTabellatoClassificaTitulus();
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
			});
			
			$('#numerofascicolo').change(function() {
				if (_tipoWSDM == "PRISMA"){
					gestionemodificacamponumerofascicolo();
				}
			});
			
			$('#classificafascicolonuovoPrisma').change(function() {
				gestionemodificacampoclassificafascicolo();
			});
			
			$('#ruolo').change(function() {
			if (_tipoWSDM == "JIRIDE"){
				caricamentoStrutturaJIRIDE();
			}
    });
			
        </c:if>
        
	</gene:javaScript>


</gene:template>