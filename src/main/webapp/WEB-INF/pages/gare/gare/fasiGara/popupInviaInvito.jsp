<%
/*
 * Created on: 15-11-2016
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra per l'invio dell'invito
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.invioEseguito and requestScope.invioEseguito eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${contextPath}/js/jHtmlArea-0.7.5.min.js"></script> 
	<link rel="Stylesheet" type="text/css" href="${contextPath}/css/jquery/jHtmlArea/jHtmlArea.css" />
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
		
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>



<c:set var="modo" value="NUOVO" scope="request" />

	<c:choose>
		<c:when test='${!empty param.codgar}'>
			<c:set var="codgar" value="${param.codgar}" />
		</c:when>
		<c:otherwise>
			<c:set var="codgar" value="${codgar}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.ngara}'>
			<c:set var="ngara" value="${param.ngara}" />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${ngara}" />
		</c:otherwise>
	</c:choose>
	
	
	<c:choose>
		<c:when test='${!empty ngara}'>
			<c:set var="valoreChiave" value="${ngara}" />
		</c:when>
		<c:otherwise>
			<c:set var="valoreChiave" value="${codgar}" />
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
	
	<c:choose>
		<c:when test='${!empty param.bustalotti}'>
			<c:set var="bustalotti" value="${param.bustalotti}" />
		</c:when>
		<c:otherwise>
			<c:set var="bustalotti" value="${bustalotti}" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test="${!empty param.idconfi}">
			<c:set var="idconfi" value="${param.idconfi}"/>
		</c:when>
		<c:otherwise>
			<c:set var="idconfi" value="${idconfi }"/>
		</c:otherwise>
	</c:choose>
	
	<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar, idconfi)}' />
	
	<c:choose>
		<c:when test="${bustalotti eq '1'}">
			<c:set var="key1" value="${codgar }"/>
		</c:when>
		<c:otherwise>
			<c:set var="key1" value="${ngara }"/>
		</c:otherwise>
	</c:choose>
	
	<c:if test="${integrazioneWSDM eq 1}" >
		<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext, idconfi)}'/>
	</c:if>	
	
	<c:set var="modelloMailPec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetMailPecModelloFunction", pageContext, "55","false",valoreChiave)}' />
	
	<gene:setString name="titoloMaschera" value="Invia invito all'asta elettronica" />
		
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInvioInvitoAstaElettronica" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreInvioInvitoAstaElettronica">
			
	<gene:campoScheda nome="msgPagina">
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}' >
					${requestScope.msg }
				</c:when>
				<c:otherwise>
					${requestScope.MsgConferma}
					<c:if test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "WARNING"}' >
						${requestScope.msg }
					</c:if>
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>

		<gene:campoScheda campo="CODGAR" campoFittizio="true" defaultValue="${codgar}" visibile="false" definizione="T21;0"/>
		<gene:campoScheda campo="NGARA" campoFittizio="true" defaultValue="${ngara}" visibile="false" definizione="T20;0"/>
				
		<c:if test="${requestScope.visualizzaDettaglioComunicazione && requestScope.controlloSuperato ne 'NO'}">
			<gene:campoScheda nome="TitoloComunicazione">
				<td colspan="2"><b>Comunicazione invito</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="COMMSGOGG" campoFittizio="true" definizione="T300;0;;;COMMSGOGG" obbligatorio="true" value="${requestScope.oggettoMail}"/>
			<gene:campoScheda campo="COMINTEST" campoFittizio="true" definizione="T2;0;;SN;COMINTEST" value="${requestScope.abilitaIntestazioneVariabile}" visibile="${ abilitatoInvioMailDocumentale ne 'true'}"/>
			<gene:campoScheda nome="intestazione" visibile="${ abilitatoInvioMailDocumentale ne 'true'}"> 
				<td class="etichetta-dato">Intestazione variabile</td>
				<td class="valore-dato">Spett.le <i>Ragione Sociale</i>
				</td>
			</gene:campoScheda>
			<gene:campoScheda campo="COMMSGTIP" campoFittizio="true" definizione="T2;0;;SN;COMMSGTIP" defaultValue="2" />
			<gene:campoScheda campo="COMMSGTES" campoFittizio="true" definizione="T2000;0;;CLOB;COMMSGTES" obbligatorio="true" value="${requestScope.testoMail}" gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoTestoComunicazioneHTML"/>
			<gene:campoScheda campo="COMMITT" campoFittizio="true" definizione="T60;0;;;COMMITT" value="${requestScope.mittenteMail}" visibile="${ abilitatoInvioMailDocumentale ne 'true'}"/>
			
			<c:set var="titoloDestinatari" value='<a class="left" href="javascript:showDestinatari()"><img id="onDestinatari" src="${pageContext.request.contextPath}/img/TreeExpand.png" alt="Apri dettaglio" title="Apri dettaglio" ><img id="offDestinatari" src="${pageContext.request.contextPath}/img/TreeCollapse.png" alt="Chiudi dettaglio" title="Chiudi dettaglio" style="display: none" ></a><b>Soggetti destinatari</b>' />
			<gene:campoScheda title="${titoloDestinatari }" modificabile="false" nome="titoloDestinatari"/>
			<gene:campoScheda addTr="false" >
					<tr id="tr1">
						<td colspan="2">
							<table id="tabellaDestinatari" class="griglia" >
								<tr style="BACKGROUND-COLOR: #A7BFD9;">
									<td colspan="2" class="titolo-valore-dato" id="Intestazione" style="width:50%">Intestazione</td>
									<td colspan="2" class="titolo-valore-dato" id="Indirizzo" style="width:30%">Indirizzo</td>
									<td colspan="2" class="titolo-valore-dato" id="Tipo" style="width:10%">Tipo indirizzo</td>
									<c:if test="${integrazioneWSDM =='1'}">
										<td col span="2" class="titolo-valore-dato" id="Cod.Fisc." style="width:10%">Cod.fisc.</td>
										<td col span="2" class="titolo-valore-dato" id="indimp" style="display: none;width:0%"> </td>
										<td col span="2" class="titolo-valore-dato" id="nciimp" style="display: none;width:0%"> </td>
										<td col span="2" class="titolo-valore-dato" id="locimp" style="display: none;width:0%"> </td>
										<td col span="2" class="titolo-valore-dato" id="codcit" style="display: none;width:0%"> </td>
										<td col span="2" class="titolo-valore-dato" id="piva" style="display: none;width:0%"> </td>
									</c:if>
								</tr>
				</gene:campoScheda>
			<c:set var="numElementiListaDestinatari" value="0"/>
			<c:forEach items="${listaDestinatari}" step="1" var="ldestinatario" varStatus="status" >
					<gene:campoScheda addTr="false">
							<tr id="listaDestinatari_${status.index }">
					</gene:campoScheda>
					<gene:campoScheda title="Intestazione" hideTitle="true" addTr="false" modificabile="false" campo="INTESTAZIONE_${status.index }" campoFittizio="true" visibile="true" definizione="T2000;" value="${ldestinatario[0]}"/>
					<gene:campoScheda title="Indirizzo mail" hideTitle="true" addTr="false" modificabile="false" campo="MAIL_${status.index }" campoFittizio="true" visibile="true" definizione="T100" value="${ldestinatario[1]}"/>
					<gene:campoScheda title="Tipo indirizzo" hideTitle="true" addTr="false" modificabile="false" campo="TIPO_${status.index }" campoFittizio="true" visibile="true" definizione="T10" value="${ldestinatario[2]}"/>
					<gene:campoScheda title="Ditta" hideTitle="true" addTr="false" modificabile="false" campo="DITTA_${status.index }" campoFittizio="true" visibile="false" definizione="T16" value="${ldestinatario[3]}"/>
					<c:if test="${integrazioneWSDM =='1'}">
						<gene:campoScheda title="cod.fisc." hideTitle="true" addTr="false" modificabile="false" campo="CODFISC_${status.index }" campoFittizio="true" definizione="T16" value="${ldestinatario[4]}"/>
						<gene:campoScheda title="indimp" hideTitle="true" addTr="false" visibile="false" campo="INDIMP_${status.index }" campoFittizio="true" definizione="T100" value="${ldestinatario[5]}"/>
						<gene:campoScheda title="nciimp" hideTitle="true" addTr="false" visibile="false" campo="NCIIMPC_${status.index }" campoFittizio="true" definizione="T100" value="${ldestinatario[6]}"/>
						<gene:campoScheda title="locimp" hideTitle="true" addTr="false" visibile="false" campo="LOCIMP_${status.index }" campoFittizio="true" definizione="T100" value="${ldestinatario[7]}"/>
						<gene:campoScheda title="codcit" hideTitle="true" addTr="false" visibile="false" campo="CODICIT_${status.index }" campoFittizio="true" definizione="T100" value="${ldestinatario[8]}"/>
						<gene:campoScheda title="piva" hideTitle="true" addTr="false" visibile="false" campo="PIVA_${status.index }" campoFittizio="true" definizione="T20" value="${ldestinatario[9]}"/>
						<gene:campoScheda title="mailAgg" hideTitle="true" addTr="false" visibile="false" campo="MAIL_AGGIUNTIVA_${status.index }" campoFittizio="true" definizione="T100" value="${ldestinatario[10]}"/>
						<gene:campoScheda title="provinciaRes" hideTitle="true" addTr="false" visibile="false" campo="PROVINCIA_RES_${status.index }" campoFittizio="true" definizione="T100" value="${ldestinatario[11]}"/>
						<gene:campoScheda title="capRes" hideTitle="true" addTr="false" visibile="false" campo="CAP_RES_${status.index }" campoFittizio="true" definizione="T100" value="${ldestinatario[12]}"/>
					</c:if>
					<gene:campoScheda addTr="false">									
							</tr>
					</gene:campoScheda>
					<c:set var="numElementiListaDestinatari" value="${status.count }"/>
			</c:forEach>
							
			<gene:campoScheda addTr="false">
							
						</table>
					<td>
				<tr>
			</gene:campoScheda>		
			
			<c:set var="titoloDoc" value='<a class="left" href="javascript:showDoc()"><img id="onDoc" src="${pageContext.request.contextPath}/img/TreeExpand.png" alt="Apri dettaglio" title="Apri dettaglio" ><img id="offDoc" src="${pageContext.request.contextPath}/img/TreeCollapse.png" alt="Chiudi dettaglio" title="Chiudi dettaglio" style="display: none" ></a><b>Allegati</b>' />
			<gene:campoScheda title="${titoloDoc }" modificabile="false" nome="titoloDoc"/>
			<gene:campoScheda addTr="false">
					<tr id="tr2">
						<td colspan="2">
							<table id="tabellaDoc" class="griglia" >
								<tr style="BACKGROUND-COLOR: #A7BFD9;">
									<td colspan="2" class="titolo-valore-dato" id="Descrizione">Descrizione</td>
									<td colspan="2" class="titolo-valore-dato" id="Nome">Nome documento</td>
								</tr>
				</gene:campoScheda>
			<c:set var="numElementiListaDoc" value="0"/>
			<c:forEach items="${listaDocumenti}" step="1" var="doc" varStatus="status" >
					<gene:campoScheda addTr="false">
							<tr id="listaDocumenti_${status.index }">
					</gene:campoScheda>
					<gene:campoScheda title="Descrizione" hideTitle="true" addTr="false" modificabile="false" campo="DESCRIZIONE_${status.index }" campoFittizio="true" visibile="true" definizione="T2000;" value="${doc[0]}"/>
					<gene:campoScheda title="Nome" hideTitle="true" addTr="false" modificabile="false" campo="NOME_${status.index }" campoFittizio="true" visibile="true" definizione="T100" value="${doc[1]}" href="javascript:visualizzaFileAllegato('${doc[2]}','${doc[3]}',${gene:string4Js(doc[1])});"/>
					<gene:campoScheda title="IDPRG" hideTitle="true" addTr="false" modificabile="false" campo="IDPRG_${status.index }" campoFittizio="true" visibile="false" definizione="T5" value="${doc[2]}" />
					<gene:campoScheda title="IDDOCDIG" hideTitle="true" addTr="false" modificabile="false" campo="IDDOCDIG_${status.index }" campoFittizio="true" visibile="false" definizione="N7" value="${doc[3]}" />
					<gene:campoScheda addTr="false">									
							</tr>
					</gene:campoScheda>
					<c:set var="numElementiListaDoc" value="${status.count }"/>
			</c:forEach>
							
			<gene:campoScheda addTr="false">
							
						</table>
					<td>
				<tr>
			</gene:campoScheda>		
						
			<gene:fnJavaScriptScheda funzione="modifyHTMLEditor('#COMMSGTIP#')" elencocampi="COMMSGTIP" esegui="false" />
			<c:if test="${ abilitatoInvioMailDocumentale ne 'true'}">
				<gene:fnJavaScriptScheda funzione="gestioneCOMINTEST('#COMINTEST#')" elencocampi="COMINTEST" esegui="true" />
			</c:if>
			
			
			<c:if test="${ integrazioneWSDM =='1'}">
				<table class="dettaglio-notab" id="datiLogin">
					<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp">
						<jsp:param name="gestioneVisualizzazioneContratta" value="1"/>
						<jsp:param name="contextPath" value="${pageContext.request.contextPath}"/>
					</jsp:include>
				</table>
				<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-internoDati.jsp">
					<jsp:param name="valoreChiaveRiservatezza" value="${key1}"/>
				</jsp:include>
			</c:if>
		</c:if>
		
		<c:if test='${requestScope.controlloSuperato ne "NO"}' >
			<c:if test="${integrazioneWSDM =='1'}">
				<gene:campoScheda >
	                <td colSpan="2" id="datiWSDM" style="display: none;">
						<table class="dettaglio-notab" id="datiLogin">
							<tr>
								<br>
							</tr>
							<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp">
								<jsp:param name="gestioneVisualizzazioneContratta" value="1"/>
								<jsp:param name="contextPath" value="${pageContext.request.contextPath}"/>
							</jsp:include>
						</table>
						<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-internoDati.jsp">
							<jsp:param name="valoreChiaveRiservatezza" value="${key1}"/>
						</jsp:include>
					</td>
				</gene:campoScheda>
			</c:if>
		</c:if>
		
		<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}" />
		<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="bustalotti" id="bustalotti" value="${bustalotti}" />
		<input type="hidden" name="numElementiListaDestinatari" id="numElementiListaDestinatari" value="${numElementiListaDestinatari}" />
		<input type="hidden" name="numElementiListaDoc" id="numElementiListaDoc" value="${numElementiListaDoc}" />
		<input type="hidden" name="integrazioneWSDM" id="integrazioneWSDM" value="${integrazioneWSDM}" />
		<input type="hidden" name="gestioneFascicoloWSDM" id="gestioneFascicoloWSDM" value="" />
		
		<c:if test="${integrazioneWSDM =='1' && requestScope.visualizzaDettaglioComunicazione}">
			<input type="hidden" name="step" id="step" value="${step}" />
			<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
			<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
			<input type="hidden" id="tiposistemaremoto" name = "tiposistemaremoto" value="" />
			<input id="tabellatiInDB" type="hidden" value="" />
			<input id="idprg" type="hidden" name="idprg" value="PG" />
			<input id="entita" type="hidden" value="GARE" /> 
			<input id="key1" type="hidden" name="key1" value="${key1 }" /> 
			<input id="key2" type="hidden" name="key2" value="" /> 
			<input id="key3" type="hidden" name="key3" value="" /> 
			<input id="key4" type="hidden" name="key4" value="" /> 
			<input id="tipoPagina" type="hidden" name="tipoPagina" value="PUBBLICAZIONE" />
			<input id="modoapertura" type="hidden" value="MODIFICA" />
			<input id="nomeR" name="nomeR"  type="hidden" value="" />
			<input id="acronimoR" name="acronimoR"  type="hidden" value="" />
			<c:choose>
				<c:when test="${ngara!= '' and !empty ngara}">
					<c:set var="chiaveOriginale" value="${ngara }"/>
				</c:when>
				<c:otherwise>
					<c:set var="chiaveOriginale" value="${codgar }"/>
				</c:otherwise>
			</c:choose>
			<input type="hidden" id="chiaveOriginale" value="${chiaveOriginale }" />
			<input id="abilitazioneGare" type="hidden" value="${profiloUtente.abilitazioneGare}" />
		</c:if>
	</gene:formScheda>
  </gene:redefineInsert>

	
	<c:choose>
		<c:when test="${integrazioneWSDM =='1'}">
			<c:set var="testoBottone" value="Protocolla e invia"/>
		</c:when>
		<c:otherwise>
			<c:set var="testoBottone" value="Invia"/>
		</c:otherwise>
	</c:choose>
	
	<gene:redefineInsert name="buttons">
			<c:choose>
				<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}'>
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
				</c:when>
				<c:otherwise>
					<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value="${testoBottone }" title="${testoBottone }" onclick="javascript:conferma()">&nbsp;
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
				</c:otherwise>
			</c:choose>
	</gene:redefineInsert>

	
	
	<gene:javaScript>
		
		<c:choose>
			<c:when test="${integrazioneWSDM =='1'}">
				function conferma() {
					var step= $("#step").val();
					if(step==1){
						 //Controllo presenza oggetto e testo della comunicazione
						if(controlloOggettoTestoLettera() == "NOK")
							return;
							
						_getComunicazione();
						_controlloPresenzaFascicolazione();
						_controlloFascicoliAssociati();
						_inizializzazioni();
						$("#step").val("2");
						inizializzazionePagina("2");
					}else{
						showObj("offParametriUtente",true);
						showObj("onParametriUtente",false);
						mostraParametriUtente(true);
						//Controlli sulla valorizzazione dei campi obbligatori
						var errori = controlloCampiObbligatori();
						
						if(!errori){
							_setWSLogin();
							document.forms[0].jspPathTo.value="gare/gare/fasiGara/popupInviaInvito.jsp";
							 if("ARCHIFLOWFA" == $("#tiposistemaremoto").val())
								$('#classificafascicolonuovo').attr('disabled', false);
							
							if("TITULUS" == $("#tiposistemaremoto").val()){
								$("#codiceaoodes").val($("#codiceaoonuovo option:selected").text());
								$("#codiceufficiodes").val($("#codiceufficionuovo option:selected").text());
							}
							if ($("#tiposistemaremoto").val() == "JDOC" ){
								$("#acronimoR").val($("#acronimoRup").text());
								$("#nomeR").val($("#nomeRup").text());
								
							}
							schedaConferma();
						}
					}
					
				}
			</c:when>
			<c:otherwise>
				function conferma() {
					
					document.forms[0].jspPathTo.value="gare/gare/fasiGara/popupInviaInvito.jsp";
					schedaConferma();
				}
			</c:otherwise>
		</c:choose>
				
			
		function annulla(){
			window.close();
		}
		
			
			document.forms[0].encoding="multipart/form-data";
			
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
			mostraElementiListaDestinatariNascosti(false);
			
			
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
			
			<c:if test="${ integrazioneWSDM =='1'}">
				function inizializzazionePagina(step){
					if(step=="1"){
						$("#datiLogin").hide();
						$("#datiProtocollo").hide();
					}else{
						$("#datiLogin").show();
						$("#datiProtocollo").show();
						$("#rowTitoloComunicazione").hide();
						$("#rowCOMMSGOGG").hide();
						$("#rowCOMINTEST").hide();
						$("#rowintestazione").hide();
						$("#rowCOMMSGTIP").hide();
						$("#rowCOMMSGTES").hide();
						$("#rowCOMMITT").hide();
						$("#rowtitoloDestinatari").hide();
						$("#tr1").hide();
						$("#rowtitoloDoc").hide();
						$("#tr2").hide();
						$("#rowPubblicazione").hide();
						$("#rowDATPUB").hide();
						$("#rowmsgPagina").hide();
						$("#oggettodocumento").val(getValue("COMMSGOGG"));
						if (_tipoWSDM == "TITULUS" ){
							oggettoDocumentoTitulus();
						}
						$("#rowPASSWORDA").hide();
						$("#rowPASSWORDB").hide();
						$("#rowPASSWORDC").hide();
						$("#rowPWD_A").hide();
						$("#rowPWD_A1").hide();
						$("#rowPWD_B").hide();
						$("#rowPWD_B1").hide();
						$("#rowPWD_C").hide();
						$("#rowPWD_C1").hide();
						
					}
				}
				
				<c:if test="${ requestScope.visualizzaDettaglioComunicazione}">
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
					_popolaTabellato("supporto","supporto");
					_popolaTabellato("classificafascicolo","classificafascicolonuovo");
					_popolaTabellato("idindice","idindice");
					_popolaTabellato("idtitolazione","idtitolazione");
					_popolaTabellato("idunitaoperativamittente","idunitaoperativamittente");
					_popolaTabellato("livelloriservatezza","livelloriservatezza");
					_popolaTabellato("sottotipo","sottotipo");
					//_controlloPresenzaFascicolazione();
					//_controlloFascicoliAssociati();
					<c:if test="${requestScope.invioEseguito eq 'Errori'}">
						if(step==2){
							//Nel caso di errore(sia gestito che non) nella procedura di invia invito, 
							//si deve forzare la rilettura dei dati, così come avviene quando si passa dal
							//primo step al secondo.
							_getComunicazione();
							//_tipoWSDM = $("#tiposistemaremoto").val();
							_controlloPresenzaFascicolazione();
							_controlloFascicoliAssociati();

						}
					</c:if>
					
					_inizializzazioni();
					<c:if test="${requestScope.invioEseguito eq 'Errori'}">
						if(step==2){
							//Nel caso di errore(sia gestito che non) nella procedura di invia invito, 
							//si deve forzare la rilettura dei dati, così come avviene quando si passa dal
							//primo step al secondo.
							_gestioneInserimentoInFascicolo();
						}
					</c:if>
					
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
							$("#trMezzo").hide();
							$("#indirizzomittente").hide();
							$("#indirizzomittente").closest('tr').hide();
						}
						
						if ((_tipoWSDM != "JIRIDE"  && _tipoWSDM != "ARCHIFLOW" && _tipoWSDM != "ARCHIFLOWFA") || ((_tipoWSDM == "JIRIDE" || _tipoWSDM == "ARCHIFLOW" || _tipoWSDM == "ARCHIFLOWFA") && _delegaInvioMailDocumentaleAbilitata != 1)) {
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
	                        }
						
						if(_tipoWSDM != "ARCHIFLOWFA" && _tipoWSDM != "PRISMA" && _tipoWSDM != "JIRIDE"){
							$( "#supporto" ).hide();
							$( "#supporto" ).closest('tr').hide();
							$( "#strutturaonuovo" ).hide();
							$( "#strutturaonuovo" ).closest('tr').hide();
						}

						if(_delegaInvioMailDocumentaleAbilitata == 1){
							setValue("COMINTEST","2");
							//showObj("rowCOMINTEST",false);
							setValue("COMMITT","");
							//showObj("rowCOMMITT", false);
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
				   </c:if>
				   mostraParametriUtente(false);
			</c:if>
		
		
		<c:if test="${ integrazioneWSDM =='1'}">
			function showParametriUtente(){
				var obj=getObjectById("onParametriUtente");
				var visibile=obj.style.display=="none";
				showObj("offParametriUtente",!visibile);
				showObj("onParametriUtente",visibile);
						
				mostraParametriUtente(!visibile);
			}
			
			function mostraParametriUtente(vis){
				var tipoWSDM = "${tipoWSDM }";
				showObj("rigaUtente",vis);
				if (tipoWSDM == "ENGINEERING") {
					showObj("rigaPassword",vis);
					showObj("rigaIdUtente",vis);
					showObj("rigaIdUtenteUnitaOperativa",vis);
				}else if(tipoWSDM == "TITULUS" || tipoWSDM == "ARCHIFLOW" || tipoWSDM == "SMAT" || tipoWSDM == "FOLIUM" || tipoWSDM == "ARCHIFLOWFA" || tipoWSDM == "EASYDOC" || tipoWSDM == "ITALPROT" || tipoWSDM == "JDOC"){
					showObj("rigaPassword",vis);
					showObj("rigaRuolo",false);
				}else if (tipoWSDM == "PRISMA") {
					showObj("rigaPassword",vis);
				}else if (tipoWSDM == "URBI" || tipoWSDM == "PROTSERVICE" || tipoWSDM == "JPROTOCOL") {
					showObj("rigaRuolo",false);
				}else {
					showObj("rigaRuolo",vis);
				}
				if(tipoWSDM=="PALEO"){
					showObj("rigaPassword",vis);
					showObj("rigaNome",vis);
					showObj("rigaCognome",vis);
			 		showObj("rigaUnitaOrganizzativa",vis);	
				}
			}
			
			mostraParametriUtente(false);	
			
			
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
	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>