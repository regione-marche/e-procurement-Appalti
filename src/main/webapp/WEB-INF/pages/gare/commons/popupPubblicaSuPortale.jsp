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
				<c:when test='${!empty param.bando}'>
					<c:set var="bando" value="${param.bando}" />
				</c:when>
				<c:otherwise>
					<c:set var="bando" value="${bando}" />
				</c:otherwise>
			</c:choose>
			
			<c:choose>
				<c:when test='${!empty param.codgar}'>
					<c:set var="codgar" value="${param.codgar}" />
				</c:when>
				<c:otherwise>
					<c:set var="codgar" value="${codgar}" />
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test='${!empty param.cifraturaBuste}'>
					<c:set var="cifraturaBuste" value="${param.cifraturaBuste}" />
				</c:when>
				<c:otherwise>
					<c:set var="cifraturaBuste" value="${cifraturaBuste}" />
				</c:otherwise>
			</c:choose>
			<gene:redefineInsert name="corpo">
			<gene:setString name="titoloMaschera" value='Pubblica su portale Appalti' />
			<c:set var="contextPath" value="${pageContext.request.contextPath}" />
			<tr>
				<td colSpan="2">
					<br>
							Pubblicazione su portale completata.
							<c:if test='${cifraturaBuste eq "1"}'>
							<br>
							<p>Cliccando 'Scarica PDF promemoria password' è possibile scaricare un PDF che riporta le password inserite in precedenza da utilizzare come promemoria al momento dell'apertura delle buste digitali.</p>
							<p><b>ATTENZIONE: una volta scaricato il PDF non sarà più possibile rigenerarlo!</b></p>
							<p>Chiunque possegga il file o la stampa dello stesso potrà accedere alle buste digitali. Salvare il file o archiviare la stampa in sicurezza.</p>
							<br>
							<a href="javascript:download()">Scarica PDF promemoria password</a>
							</c:if>
					
					<br>&nbsp;
					<br>&nbsp;
				</td>	
			<tr>
			
			<form name="formDownload" id="formDownload" action="${pageContext.request.contextPath}/pg/StampaRiepilogoPswBuste.do" method="post">
				<input type="hidden" name="codgar" id="codgar" value="${codiceGara}" />
				<input type="hidden" name="url" id="url" value="${url}" />
				<input type="hidden" name="usernameId" id="usernameId" value="${usernameId}" />
				<input type="hidden" name="nomeTecnico" id="nomeTecnico" value="${nomeTecnico}" />
				<input type="hidden" name="oggettoGara" id="oggettoGara" value="${oggettoGara}" />
				<input type="hidden" name="username" id="username" value="${username}" />
				<input type="hidden" name="PWD_A0" id="PWD_A0" value="${PWD_A0}" />
				<input type="hidden" name="PWD_A" id="PWD_A" value="${PWD_A}" />
				<input type="hidden" name="PWD_B" id="PWD_B" value="${PWD_B}" />
				<input type="hidden" name="PWD_C" id="PWD_C" value="${PWD_C}" />
			</form>
			
			<gene:redefineInsert name="buttons">
				<input type="button" class="bottone-azione"  id="cancel" value="Chiudi" title="Chiudi" onclick="chiudi();"/>&nbsp;&nbsp;
			</gene:redefineInsert>
			<gene:javaScript>
			
			window.onload = function () { 
				window.opener.bloccaRichiesteServer();
				window.opener.location = "${contextPath}/History.do?"+csrfToken+"&metodo=reload";
				dim1=800;
				dim2=650;
				window.resizeTo(dim1,dim2);	
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
	<link rel="stylesheet" type="text/css" href="${contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
		
	<script type="text/javascript" src="${contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>



<c:set var="modo" value="NUOVO" scope="request" />

	<c:choose>
		<c:when test='${!empty param.isProceduraTelematica}'>
			<c:set var="isProceduraTelematica" value="${param.isProceduraTelematica}" />
		</c:when>
		<c:otherwise>
			<c:set var="isProceduraTelematica" value="${isProceduraTelematica}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.bando}'>
			<c:set var="bando" value="${param.bando}" />
		</c:when>
		<c:otherwise>
			<c:set var="bando" value="${bando}" />
		</c:otherwise>
	</c:choose>
	
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
		<c:when test='${!empty param.step}'>
			<c:set var="step" value="${param.step}" />
		</c:when>
		<c:otherwise>
			<c:set var="step" value="${step}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.entita}'>
			<c:set var="entita" value="${param.entita}" />
		</c:when>
		<c:otherwise>
			<c:set var="entita" value="${entita}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.garaElencoCatalogo}'>
			<c:set var="garaElencoCatalogo" value="${param.garaElencoCatalogo}" />
		</c:when>
		<c:otherwise>
			<c:set var="garaElencoCatalogo" value="${garaElencoCatalogo}" />
		</c:otherwise>
	</c:choose>
	
	
	<c:choose>
		<c:when test='${!empty param.valtec}'>
			<c:set var="valtec" value="${param.valtec}" />
		</c:when>
		<c:otherwise>
			<c:set var="valtec" value="${valtec}" />
		</c:otherwise>
	</c:choose>
	
	<c:if test="${empty entita ||  entita==''}">
		<c:set var="entita" value="GARE" />
	</c:if>

	<c:choose>
		<c:when test='${!empty ngara}'>
			<c:set var="valoreChiave" value="${ngara}" />
		</c:when>
		<c:otherwise>
			<c:set var="valoreChiave" value="${codgar}" />
		</c:otherwise>
	</c:choose>
		
	<c:choose>
		<c:when test='${!empty param.garavviso}'>
			<c:set var="garavviso" value="${param.garavviso}" />
		</c:when>
		<c:otherwise>
			<c:set var="garavviso" value="${garavviso}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.integrazioneWSDM}'>
			<c:set var="integrazioneWSDM" value="${param.integrazioneWSDM}" />
		</c:when>
		<c:otherwise>
			<c:set var="integrazioneWSDM" value="${integrazioneWSDM}" />
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
	
	<c:if test="${empty integrazioneWSDM}">
		<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar,idconfi)}' />
	</c:if>
		
	<c:if test="${integrazioneWSDM eq 1 && bando=='3'}" >
		<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext, idconfi)}'/>
	</c:if>	
	
	<c:set var="chiaveTorn" value="TORN.CODGAR=T:${codgar}"/>
	<c:set var="tipologiaGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,chiaveTorn)}'/>
	<c:choose>
		<c:when test="${tipologiaGara eq '3' }" >
			<c:set var="offertaUnica" value="true" />
		</c:when>
		<c:otherwise>
			<c:set var="offertaUnica" value="false" />
		</c:otherwise>
	</c:choose>
	<c:set var="controlloPresenzaOffertaTecnica" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlloPresenzaOffertaTecnicaFunction",  pageContext, valoreChiave, offertaUnica)}'/>
	
	<c:set var="modelloMailPec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetMailPecModelloFunction", pageContext, "53","false",valoreChiave)}' />
	
	<c:set var="tipoPubSitoIstituzionale" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", tipoPubblicazioneSitoIstituzionale)}'/>
	<c:if test="${tipoPubSitoIstituzionale eq '2' }">
		<c:choose>
			<c:when test="${bando eq '1' }">
				<c:set var="tipoUuid" value="BANDO" />
			</c:when>
			<c:when test="${bando eq '0' }">
				<c:set var="tipoUuid" value="ESITO" />
			</c:when>
		</c:choose>
		<c:set var="uuid" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetUuidFunction",  pageContext, codgar,tipoUuid)}' scope="request"/>
	</c:if>
	
	<c:choose>
		<c:when test='${offertaUnica eq "true"}'>
			<c:set var="valoreChiaveRiservatezza" value="${codgar}"/>
		</c:when >
		<c:when test='${empty ngara}'>
			<c:set var="valoreChiaveRiservatezza" value="${codgar}"/>
		</c:when >
		<c:otherwise>
			<c:set var="valoreChiaveRiservatezza" value="${ngara}"/>
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value='Pubblica su portale Appalti' />
	<c:if test="${isProceduraTelematica && bando=='3'}">
		<gene:setString name="titoloMaschera" value='Invia invito e pubblica su portale Appalti' />
	</c:if>
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="${entita }" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePubblicaSuPortale" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePubblicaSuPortale">
			
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

		<gene:campoScheda campo="CODGAR" campoFittizio="true" defaultValue="${param.codgar}" visibile="false" definizione="T21;0"/>
		<gene:campoScheda campo="NGARA" campoFittizio="true" defaultValue="${param.ngara}" visibile="false" definizione="T20;0"/>
		<gene:campoScheda campo="LOTTODIGARA" campoFittizio="true" defaultValue="${param.lottoDiGara}" visibile="false" definizione="T1;0"/>
		
		<c:if test="${isProceduraTelematica && bando=='3' && requestScope.visualizzaDettaglioComunicazione && requestScope.controlloSuperato ne 'NO'}">
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
						<gene:campoScheda title="piva" hideTitle="true" addTr="false" visibile="false" campo="PIVA_${status.index }" campoFittizio="true" definizione="T16" value="${ldestinatario[9]}"/>
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
			<c:if test='${requestScope.controlloSuperato ne "NO"}' >
				<gene:campoScheda nome="Pubblicazione">
					<td colspan="2"><b>Pubblicazione</b></td>
				</gene:campoScheda>
			</c:if>
			
			
						
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
					<jsp:param name="valoreChiaveRiservatezza" value="${valoreChiaveRiservatezza}" />
				</jsp:include>
			</c:if>
		</c:if>
		
		<c:if test='${requestScope.controlloSuperato ne "NO"}' >
			
			<gene:campoScheda campo="DATPUB" campoFittizio="true" definizione="D;0;;;DATPUB" obbligatorio="true"/>
			<c:if test="${cifraturaBuste eq '1' }">
					<gene:campoScheda nome="PASSWORDA0" visibile="${(iterga==2 || iterga==4) && bando == '1' }">
						<td colspan="2"><br><b>Password per la cifratura delle buste</b>
						<br>Inserire una stringa di almeno 8 caratteri, composta da numeri o lettere.
						<br>Tale password verrà richiesta al momento dell'acquisizione delle buste</td>
					</gene:campoScheda>
					<gene:campoScheda campo="PWD_A0" title="Password busta prequalifica" campoFittizio="true" definizione="T100" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPassword100Caratteri" obbligatorio="true" visibile="${(iterga==2 || iterga==4) && bando == '1'}"/>
					<gene:campoScheda campo="PWD_A01" title="Conferma password" campoFittizio="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPassword100Caratteri" definizione="T100" obbligatorio="true" visibile="${(iterga==2 || iterga==4) && bando == '1'}"/>
					<gene:campoScheda nome="PASSWORDA" visibile="${!((iterga==2 || iterga==4) && bando == '1' ) }">
						<td colspan="2"><br><b>Password per la cifratura delle buste</b>
						<br>Inserire una stringa di almeno 8 caratteri, composta da numeri o lettere.
						<br>Tale password verrà richiesta al momento dell'acquisizione delle buste. E' prevista una password distinta per ogni tipologia di busta</td>
					</gene:campoScheda>
					<gene:campoScheda campo="PWD_A" title="Password busta amministrativa" campoFittizio="true" definizione="T100" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPassword100Caratteri" obbligatorio="true" visibile="${!((iterga==2 || iterga==4) && bando == '1' )}"/>
					<gene:campoScheda campo="PWD_A1" title="Conferma password" campoFittizio="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPassword100Caratteri" definizione="T100" obbligatorio="true" visibile="${!((iterga==2 || iterga==4) && bando == '1' )}"/>
					<gene:campoScheda nome="PASSWORDB" visibile="${controlloPresenzaOffertaTecnica eq 'true' && !((iterga==2 || iterga==4) && bando == '1' )}">
						<td colspan="2"><br></td>
					</gene:campoScheda>
					<gene:campoScheda campo="PWD_B" title="Password busta tecnica" campoFittizio="true" definizione="T100" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPassword100Caratteri" obbligatorio="${controlloPresenzaOffertaTecnica eq 'true' }"   visibile="${controlloPresenzaOffertaTecnica eq 'true' && !((iterga==2 || iterga==4) && bando == '1' )}"/>
					<gene:campoScheda campo="PWD_B1" title="Conferma password" campoFittizio="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPassword100Caratteri" definizione="T100" obbligatorio="${controlloPresenzaOffertaTecnica eq 'true' }" visibile="${controlloPresenzaOffertaTecnica eq 'true' && !((iterga==2 || iterga==4) && bando == '1' )}"/>
					<gene:campoScheda nome="PASSWORDC" visibile="${!((iterga==2 || iterga==4) && bando == '1' ) && (empty soloPunteggiTec || soloPunteggiTec ne 'true') }">
						<td colspan="2"><br></td>
					</gene:campoScheda>
					<gene:campoScheda campo="PWD_C" title="Password busta economica" campoFittizio="true" definizione="T100" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPassword100Caratteri" obbligatorio="true" visibile="${!((iterga==2 || iterga==4) && bando == '1' ) && (empty soloPunteggiTec || soloPunteggiTec ne 'true')}"/>
					<gene:campoScheda campo="PWD_C1" title="Conferma password" campoFittizio="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoPassword100Caratteri" definizione="T100" obbligatorio="true" visibile="${!((iterga==2 || iterga==4) && bando == '1' ) && (empty soloPunteggiTec || soloPunteggiTec ne 'true')}"/>
					
			</c:if>
			<c:if test="${(bando=='1' || (bando == '3' && !isProceduraTelematica)) && integrazioneWSDM =='1'}">
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
							<jsp:param name="valoreChiaveRiservatezza" value="${valoreChiaveRiservatezza}" />
						</jsp:include>
					</td>
				</gene:campoScheda>
			</c:if>
			<c:if test="${(bando=='1' || (bando=='0' && empty bandoIdRegMarche)) and tipoPubSitoIstituzionale eq '1'}">
				<gene:campoScheda >
				<td colSpan="2" >
					
					<tr>
						<td colspan="2">
							<b>Dati per pubblicazione su sito istituzionale</b>
						</td>
					</tr>
					<tr >
						<td class="etichetta-dato">Struttura (*)</td>
						<td class="valore-dato">
							<select id="strutturaRegionale" name="strutturaRegionale" style="width:350px;">
								<option value=""></option>
								<c:forEach items="${listaStruttureRegionali}" step="1" var="strutturaRegionale" varStatus="status" >
									 <option value="${strutturaRegionale[1]}" <c:if test="${idStrutturaRegionale eq strutturaRegionale[1] }">selected</c:if>>${strutturaRegionale[0]}</option>
								</c:forEach>
							</select>
						</td>						
					</tr>
					<tr >
						<td class="etichetta-dato">Tema (*)</td>
						<td class="valore-dato">
							<select id="temaRegionale" name="temaRegionale">
								<option value=""></option>
								<c:forEach items="${listaTemiRegionali}" step="1" var="temaRegionale" varStatus="status" >
									 <option value="${temaRegionale[1]}" <c:if test="${idTemaRegionale eq  temaRegionale[1]}">selected</c:if>>${temaRegionale[0]}</option>
								</c:forEach>
							</select>
						</td>						
					</tr>
					<tr >
						<td class="etichetta-dato">Tipo procedura (*)</td>
						<td class="valore-dato">
							<select id="tipologiaBando" name="tipologiaBando" style="width:350px;">
								<option value=""></option>
								<c:forEach items="${listaTipologieProcedure}" step="1" var="tipologiaProcedura" varStatus="status" >
									 <option value="${tipologiaProcedura[1]}" <c:if test="${idTipologiaBando eq tipologiaProcedura[1] }">selected</c:if>>${tipologiaProcedura[0]}</option>
								</c:forEach>
							</select>
						</td>						
					</tr>
					
					
				</td>
				</gene:campoScheda>
			</c:if>
			<c:if test="${(bando=='1' || bando=='0' ) and tipoPubSitoIstituzionale eq '2' and empty uuid}">
				<gene:campoScheda >
				<td colSpan="2" >
					
					<tr>
						<td colspan="2">
							<b>Dati per pubblicazione su sito istituzionale</b>
						</td>
					</tr>
					<tr >
						<td class="etichetta-dato">Struttura (*)</td>
						<td class="valore-dato">
							<select id="strutturaATC" name="strutturaATC" style="width:350px;">
								<option value=""></option>
								<c:forEach items="${listaStruttureATC}" step="1" var="strutturATC" varStatus="status" >
									 <option value="${strutturATC[0]}">${strutturATC[1]}</option>
								</c:forEach>
							</select>
						</td>						
					</tr>
				</td>
				</gene:campoScheda>
			</c:if>
		</c:if>
		<gene:campoScheda campo="BANDO" campoFittizio="true" defaultValue="${param.bando}" visibile="false" definizione="T1;0"/>
		<input type="hidden" name="isProceduraTelematica" id="isProceduraTelematica" value="${isProceduraTelematica}" />
		<input type="hidden" name="bando" id="bando" value="${bando}" />
		<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="numElementiListaDestinatari" id="numElementiListaDestinatari" value="${numElementiListaDestinatari}" />
		<input type="hidden" name="numElementiListaDoc" id="numElementiListaDoc" value="${numElementiListaDoc}" />
		<input type="hidden" name="integrazioneWSDM" id="integrazioneWSDM" value="${integrazioneWSDM}" />
		<input type="hidden" name="gestioneFascicoloWSDM" id="gestioneFascicoloWSDM" value="" />
		<input type="hidden" name="garaElencoCatalogo" id="garaElencoCatalogo" value="${garaElencoCatalogo}" />
		<input type="hidden" name="valtec" id="valtec" value="${valtec}" />
		<input type="hidden" name="garavviso" id="garavviso" value="${garavviso}" />
		<input type="hidden" name="iterga" id="iterga" value="${iterga }" />
		<input type="hidden" name="soloPunteggiTec" id="soloPunteggiTec" value="${soloPunteggiTec }" />
		<input type="hidden" name="cifraturaBuste" id="cifraturaBuste" value="${cifraturaBuste }" />
		<input type="hidden" name="idconfi" id="idconfi" value="${idconfi }" />
		<input type="hidden" name="genereGara" id="genereGara" value="${tipologiaGara }" />
		
		
		<c:if test="${(bando=='3' || bando=='1') &&  integrazioneWSDM =='1' && requestScope.visualizzaDettaglioComunicazione}">
			<input type="hidden" name="step" id="step" value="${step}" />
			<input id="servizio" name="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
			<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
			<input  type="hidden" id="tiposistemaremoto" name = "tiposistemaremoto" value="" />
			<input id="tabellatiInDB" type="hidden" value="" />
			<input id="idprg" type="hidden" name="idprg" value="PG" />
			<c:choose>
				<c:when test="${entita eq 'GARE' && ngara!= '' and !empty ngara}">
					<c:set var="key1" value="${ngara }"/>
				</c:when>
				<c:otherwise>
					<c:set var="key1" value="${codgar }"/>
				</c:otherwise>
			</c:choose>
			<input id="entita" type="hidden" value="${entita}" /> 
			<input id="key1" type="hidden" name="key1" value="${key1}" /> 
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
		<c:if test="${(bando=='1' || bando=='0') && tipoPubSitoIstituzionale eq '1' }">
			<input id="bandoIdRegMarche" type="hidden" name="bandoIdRegMarche" value="${bandoIdRegMarche}" /> 
		</c:if>
		<c:if test="${(bando=='1' || bando=='0') && tipoPubSitoIstituzionale eq '2' }">
			<input id="uuid" type="hidden" name="uuid" value="${uuid}" /> 
		</c:if>
	</gene:formScheda>
  </gene:redefineInsert>

	
	<c:choose>
		<c:when test="${isProceduraTelematica && bando=='3' &&  integrazioneWSDM =='1'}">
			<c:set var="testoBottone" value="Protocolla e invia"/>
		</c:when>
		<c:when test="${isProceduraTelematica && bando=='3' }">
			<c:set var="testoBottone" value="Invia"/>
		</c:when>
		<c:otherwise>
			<c:set var="testoBottone" value="Conferma"/>
		</c:otherwise>
	</c:choose>
	
	<gene:redefineInsert name="buttons">
			<c:choose>
				<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}'>
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
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
		
		<c:if test="${isProceduraTelematica}"> 
			$("#DATPUB").prop("disabled",true);
		</c:if>
		
		<c:if test="${requestScope.controlloSuperato ne 'NO'}">
			<c:if test="${cifraturaBuste eq '1' && isProceduraTelematica && (bando eq '3' or (bando eq '1' && integrazioneWSDM eq '1'))}">
				var dim1=800;
				var dim2=850;
				window.resizeTo(dim1,dim2);	
			</c:if>
		</c:if>
		
		<c:choose>
			<c:when test="${isProceduraTelematica && bando=='3' && integrazioneWSDM =='1'}">
				function conferma() {
					var step= $("#step").val();
					if(step==1){
						var dataPubblicazione = getValue("DATPUB");
						if(dataPubblicazione== null || dataPubblicazione==""){
							alert("Specificare la data di pubblicazione");
							return;
						}else{
							var d = new Date();
							var oggi = d.getDate()  + "/" + (d.getMonth()+1) + "/" + d.getFullYear();
							if(dataPubblicazione > oggi){
								alert("la data inserita non può essere precedente a quella attuale");
								return;
							}
						}
						//Controllo presenza oggetto e testo della comunicazione
						if(controlloOggettoTestoLettera() == "NOK")
							return;
						<c:choose>
						<c:when test="${cifraturaBuste eq '1' }">
							var iterga = "${iterga }";
							var bando = "${bando }";
							if((iterga==2 || iterga==4) && bando == '1'){
								var pwd = getValue("PWD_A0");
								var pwd1 = getValue("PWD_A01");
								if(verificaPassword(pwd,pwd1,"A0")<0)
										return;
							}else{
								var pwd = getValue("PWD_A");
								var pwd1 = getValue("PWD_A1");
								if(verificaPassword(pwd,pwd1,"A")<0)
										return;
								<c:if test="${controlloPresenzaOffertaTecnica eq 'true' }" >
									pwd = getValue("PWD_B");
									pwd1 = getValue("PWD_B1");
									if(verificaPassword(pwd,pwd1,"B")<0)
										return;
								</c:if>
								
								<c:if test="${soloPunteggiTec ne 'true' }" >
									pwd = getValue("PWD_C");
									pwd1 = getValue("PWD_C1");
									if(verificaPassword(pwd,pwd1,"C")<0)
											return;
							    </c:if>
							}	
							
							var message = "ATTENZIONE: per ragioni di sicurezza le password NON vengono salvate nel sistema e non possono essere recuperate in alcun modo!\n"+
							"E' necessario prestare attenzione durante la digitazione (caps lock, blocco numerico) e conservare le password per l'apertura delle buste telematiche.\n"+
							"Il Gestore del Sistema o l'Amministratore NON possono recuperare o resettare le password smarrite.";
							if(confirm(message)){
								_getComunicazione();
								_controlloPresenzaFascicolazione();
								_controlloFascicoliAssociati();
								_inizializzazioni();
									
								$("#step").val("2");
								inizializzazionePagina("2");
							}
						</c:when>
						<c:otherwise>
							_getComunicazione();
							_controlloPresenzaFascicolazione();
							_controlloFascicoliAssociati();
							_inizializzazioni();
								
							$("#step").val("2");
							inizializzazionePagina("2");
						</c:otherwise>
						</c:choose>
						
					}else{
						showObj("offParametriUtente",true);
						showObj("onParametriUtente",false);
						mostraParametriUtente(true);
						//Controlli sulla valorizzazione dei campi obbligatori
						var errori = controlloCampiObbligatori();
						
						if(!errori){
							_setWSLogin();
							document.forms[0].jspPathTo.value="gare/commons/popupPubblicaSuPortale.jsp";
							if(_fascicolazioneAbilitata==1 && _fascicoliPresenti==0)
								$("#gestioneFascicoloWSDM").val("1");
							if("ARCHIFLOWFA" == $("#tiposistemaremoto").val())
								$('#classificafascicolonuovo').attr('disabled', false);
							if("PALEO" == $("#tiposistemaremoto").val()){
								
								$("#inout").append("<option value='INT'>interno</option>");
	                            $("#inout").val("INT");
                            }
                            
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

					var dataPubblicazione = getValue("DATPUB");
					if(dataPubblicazione== null || dataPubblicazione==""){
						alert("Specificare la data di pubblicazione");
						return;
					}
					<c:if test="${isProceduraTelematica && bando=='3'}">
						//Controllo presenza oggetto e testo della comunicazione
						if(controlloOggettoTestoLettera() == "NOK")
							return;
					</c:if>
					<c:if test="${(bando=='1' || (bando=='0' && empty bandoIdRegMarche)) and tipoPubSitoIstituzionale eq '1'}">
						var strutturaRegionale = getValue("strutturaRegionale");
						if(strutturaRegionale== null || strutturaRegionale==""){
							alert("Specificare la struttura");
							return;
						}else{
							var d = new Date();
							var oggi = d.getDate()  + "/" + (d.getMonth()+1) + "/" + d.getFullYear();
							if(dataPubblicazione > oggi){
								alert("la data inserita non può essere precedente a quella attuale");
								return;
							}
						}
						var temaRegionale = getValue("temaRegionale");
						if(temaRegionale== null || temaRegionale==""){
							alert("Specificare il tema");
							return;
						}
						var tipologiaBando = getValue("tipologiaBando");
						if(tipologiaBando== null || tipologiaBando==""){
							alert("Specificare il tipo procedura");
							return;
						}
					</c:if>
					
					<c:if test="${(bando=='1' || bando=='0') and tipoPubSitoIstituzionale eq '2' and empty uuid}">
						var strutturaATC = getValue("strutturaATC");
						if(strutturaATC== null || strutturaATC==""){
							alert("Specificare la struttura");
							return;
						}
					</c:if>
					
					<c:if test="${cifraturaBuste eq '1' }">
						var iterga = "${iterga }";
						var bando = "${bando }";
						if((iterga==2 || iterga==4) && bando == '1'){
							var pwd = getValue("PWD_A0");
							var pwd1 = getValue("PWD_A01");
							if(verificaPassword(pwd,pwd1,"A0")<0)
									return;
						}else{
							var pwd = getValue("PWD_A");
							var pwd1 = getValue("PWD_A1");
							if(verificaPassword(pwd,pwd1,"A")<0)
									return;
							<c:if test="${controlloPresenzaOffertaTecnica eq 'true' }" >
								pwd = getValue("PWD_B");
								pwd1 = getValue("PWD_B1");
								if(verificaPassword(pwd,pwd1,"B")<0)
									return;
							</c:if>
							
							<c:if test="${soloPunteggiTec ne 'true' }" >
								pwd = getValue("PWD_C");
								pwd1 = getValue("PWD_C1");
								if(verificaPassword(pwd,pwd1,"C")<0)
									return;	
							</c:if>
						}
						
					</c:if>
					<c:if test="${(bando=='1' || (bando == '3' && !isProceduraTelematica)) && integrazioneWSDM =='1'}"> 
					
					
					
					var procollazioneTitulus = false;
					if(_fascicoliPresenti > 0 && "TITULUS" == _tipoWSDM && _docTuttiNonProtocollati){
						procollazioneTitulus =true;
					}
					
					if(_fascicolazioneAbilitata==1 && (_fascicoliPresenti==0 || procollazioneTitulus)){
						showObj("offParametriUtente",true);
						showObj("onParametriUtente",false);
						mostraParametriUtente(true);
						var errori = controlloCampiObbligatori();
						if(!errori){
							var garaElencoCatalogo = "${garaElencoCatalogo }";
							_setWSLogin();
							$("#tiposistemaremoto").val(_tipoWSDM);
							$("#gestioneFascicoloWSDM").val("1");
							var garavviso ="${garavviso }";
							if("PALEO" == $("#tiposistemaremoto").val() || "TITULUS" == $("#tiposistemaremoto").val() || "FOLIUM" == $("#tiposistemaremoto").val()){
								var iterga="${iterga}";
								if("TITULUS" == $("#tiposistemaremoto").val() && (iterga=="1" || iterga=="2" || iterga=="4")){
									$("#oggettodocumento").val("Bando di gara");
								}else if("TITULUS" == $("#tiposistemaremoto").val() && garaElencoCatalogo == "1"){
									$("#oggettodocumento").val("Pubblicazione elenco");
								}else if("TITULUS" == $("#tiposistemaremoto").val() && garavviso == "1"){
									$("#oggettodocumento").val("Pubblicazione avviso");
								}else{
									$("#oggettodocumento").val("Apertura fascicolo");
									if("PALEO" == $("#tiposistemaremoto").val()){
										$("#inout").append("<option value='INT'>interno</option>");
		                            	$("#inout").val("INT");
	                            	}
								}
								if("TITULUS" == $("#tiposistemaremoto").val()){
									$("#codiceaoodes").val($("#codiceaoonuovo option:selected").text());
									$("#codiceufficiodes").val($("#codiceufficionuovo option:selected").text());
									oggettoDocumentoTitulus();
								}
                               	
                            }
			    if("ARCHIFLOWFA" == $("#tiposistemaremoto").val())
								$('#classificafascicolonuovo').attr('disabled', false);
						}else{
							return;
						}
					}
					</c:if>
					<c:choose>
						<c:when test="${isProceduraTelematica && bando!='0' && cifraturaBuste eq '1'}">
							var message = "ATTENZIONE: per ragioni di sicurezza le password NON vengono salvate nel sistema e non possono essere recuperate in alcun modo!\n"+
							"E' necessario prestare attenzione durante la digitazione (caps lock, blocco numerico) e conservare le password per l'apertura delle buste telematiche.\n"+
							"Il Gestore del Sistema o l'Amministratore NON possono recuperare o resettare le password smarrite.";
							if(confirm(message)){
								document.forms[0].jspPathTo.value="gare/commons/popupPubblicaSuPortale.jsp";
								schedaConferma();
							}
						</c:when>
						<c:otherwise>
							document.forms[0].jspPathTo.value="gare/commons/popupPubblicaSuPortale.jsp";
							schedaConferma();
						</c:otherwise>
					</c:choose>
				}
			</c:otherwise>
		</c:choose>
		
		
		function annulla(){
			window.close();
		}
		
		
		
		
		<c:if test="${isProceduraTelematica && bando=='3' }">
		
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
						if (_tipoWSDM == "ARCHIFLOWFA" ){
							var testoOggettoDocumento= "${valoreChiave} - " + $('#oggettodocumento').val() +" - " + _oggettoGara;
							$("#oggettodocumento").val(testoOggettoDocumento);
						}else if (_tipoWSDM == "TITULUS" ){
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
					if (_tipoWSDM != "TITULUS")
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
					_popolaTabellato("livelloriservatezza","livelloriservatezza");
					_popolaTabellato("sottotipo","sottotipo");
					<c:if test="${requestScope.pubblicazioneEseguita eq 'Errori'}">
						if(step==2){
							//Nel caso di errore(sia gestito che non) nella procedura di invia invito, 
							//si deve forzare la rilettura dei dati, cosÃ¬ come avviene quando si passa dal
							//primo step al secondo.
							_getComunicazione();
							//_tipoWSDM = $("#tiposistemaremoto").val();
							_controlloPresenzaFascicolazione();
							_controlloFascicoliAssociati();

						}
					</c:if>
					_inizializzazioni();
					
					<c:if test="${requestScope.pubblicazioneEseguita eq 'Errori'}">
						if(step==2){
							//Nel caso di errore(sia gestito che non) nella procedura di invia invito, 
							//si deve forzare la rilettura dei dati, cosÃ¬ come avviene quando si passa dal
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
                            //Per TITULUS il campo non ? obbligatorio
                            $("#obblmezzoinvio").hide();
                        }
                        
                        if(_tipoWSDM != "ARCHIFLOW" && _tipoWSDM != "ARCHIFLOWFA" && _tipoWSDM != "JDOC" ){
                        	$("#mezzo").hide();
							$("#mezzo").closest('tr').hide();
                        }
						
						if(_tipoWSDM != "JIRIDE"){
							$("#livelloriservatezza").closest('tr').hide();
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
				    
					
				    
					
				   </c:if>
				   mostraParametriUtente(false);
			</c:if>
		</c:if>
		<c:if test="${(bando=='1' || (bando == '3' && !isProceduraTelematica)) && requestScope.controlloSuperato ne 'NO' && integrazioneWSDM =='1'}">
			_wait();
						
			setTimeout(function(){
				_getTipoWSDM();
				_controlloPresenzaFascicolazione();
				_controlloFascicoliAssociati();
				_inizializzazioniBando1();
			}, 800);
		
			
			function _inizializzazioniBando1(){
				var procollazioneTitulus = false;
				if(_fascicoliPresenti>0 && "TITULUS" == _tipoWSDM){
					_controlloProtocollazioneDocBando();
					if(_docTuttiNonProtocollati)
						procollazioneTitulus =true;
				}	
				if(_fascicolazioneAbilitata==1 && (_fascicoliPresenti==0 || procollazioneTitulus)){
					
					$("#datiWSDM").show();
					/*
					 * Gestione utente ed attributi per il collegamento remoto
					 */
					_getWSTipoSistemaRemoto();
					_popolaTabellato("ruolo","ruolo");
					_popolaTabellato("codiceuo","codiceuo");
					if (_tipoWSDM != "TITULUS")
						_popolaTabellato("tipodocumento","tipodocumento");
					_getWSLogin();
					_gestioneWSLogin();
					
					/*
					 * Gestione tabellati per richiesta protocollazione
					 */
					_popolaTabellato("classificafascicolo","classificafascicolonuovo");
					_inizializzazioni();
					$("#TitoloDatiDocumento").hide();
					$("#classificaDocumento").hide();
					$("#tipoDocumento").hide();
					$("#oggettoDocumento").hide();
					$("#mittenteInterno").hide();
					$("#indirizzoMittente").hide();
					$("#mezzoInvio").hide();
					$("#sezionelivelloriservatezza").hide();
					$("#trMezzo").hide();
					$("#idIndice").hide();
					$("#idUnitaoperativaMittente").hide();
					
					if (_tipoWSDM == "PALEO") {
						var copyCodRegistroDocumento = $('#codRegistroDocumento').clone();
	                	$('#codRegistroDocumento').remove(); // rimuove l'elemento originale
	                	$(copyCodRegistroDocumento).insertAfter('#descFascicoloNuovo');
	                	_popolaTabellato("codiceregistro","codiceregistrodocumento");
	                }else{
	                	$("#codRegistroDocumento").hide();
	                }
					if (_tipoWSDM != "ENGINEERING") {
							$("#idIndice").hide();
							$("#idTitolazione").hide();
							$("#idUnitaoperativaMittente").hide();
					}
	                if (_tipoWSDM == "JIRIDE") {
						_popolaTabellato("livelloriservatezza","livelloriservatezza");
	                }
	                $("#msgFascicolo").show();
	                if(procollazioneTitulus)
						$("#msgFascicolo").html("<br>Con la pubblicazione su portale Appalti si procede anche alla protocollazione dei documenti del bando.");
	                if(_tipoWSDM == "ARCHIFLOWFA")
						$("#trSupporto").hide();
										
					if (_tipoWSDM == "JDOC"){
						$("#acronimoR").val($("#acronimoRup").text());
						$("#nomeR").val($("#nomeRup").text());
						$("#rigaSottotipo").hide();
						
					}
				}
				/*
				else{
					$("#datiLogin").hide();
					$("#datiProtocollo").hide();
				}
				*/
				_nowait();
			}
			
			$('#inserimentoinfascicolo').change(function() {
				_gestioneInserimentoInFascicolo();
		    });
		    
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
				}else if(tipoWSDM == "TITULUS" || tipoWSDM == "ARCHIFLOW" || tipoWSDM == "SMAT" || tipoWSDM == "FOLIUM" || tipoWSDM == "ARCHIFLOWFA" 
					|| tipoWSDM == "EASYDOC" || tipoWSDM == "ITALPROT" || tipoWSDM == "URBI" || tipoWSDM == "JDOC"){
					showObj("rigaPassword",vis);
					showObj("rigaRuolo",false);
				}else if (tipoWSDM == "PRISMA") {
					showObj("rigaPassword",vis);
				}else if (tipoWSDM == "INFOR") {
					showObj("rigaPassword",false);
					showObj("rigaRuolo",false);
				}else if (tipoWSDM == "URBI" || tipoWSDM == "PROTSERVICE" || tipoWSDM == "JPROTOCOL") {
					showObj("rigaRuolo",false);
				}else{
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
	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>