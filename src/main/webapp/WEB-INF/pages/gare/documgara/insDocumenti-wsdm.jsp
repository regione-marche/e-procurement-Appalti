<%/*
       * Created on 09-mar-2015
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

<c:choose>
	<c:when test="${!empty param.idconfi}">
		<c:set var="idconfi" value="${param.idconfi}"/>
	</c:when>
	<c:otherwise>
		<c:set var="idconfi" value="${idconfi}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.key1}">
		<c:set var="key1" value="${param.key1}"/>
	</c:when>
	<c:otherwise>
		<c:set var="key1" value="${key1}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.genereGara}">
		<c:set var="genereGara" value="${param.genereGara}"/>
	</c:when>
	<c:otherwise>
		<c:set var="genereGara" value="${genereGara}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.ngara}">
		<c:set var="ngara" value="${param.ngara}"/>
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.codiceGara}">
		<c:set var="codiceGara" value="${param.codiceGara}"/>
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${codiceGara}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.codiceGara}">
		<c:set var="codiceGara" value="${param.codiceGara}"/>
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${codiceGara}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.tipologiaDoc}">
		<c:set var="tipologiaDoc" value="${param.tipologiaDoc}"/>
	</c:when>
	<c:otherwise>
		<c:set var="tipologiaDoc" value="${tipologiaDoc}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.gruppo}">
		<c:set var="gruppo" value="${param.gruppo}"/>
	</c:when>
	<c:otherwise>
		<c:set var="gruppo" value="${gruppo}"/>
	</c:otherwise>
</c:choose>

<c:set var="entita" value="GARE"/> 
<c:if test="${genereGara eq '1' }">
	<c:set var="entita" value="TORN"/>
</c:if>

<c:choose>
	<c:when test="${!empty param.sso}">
		<c:set var="sso" value="${param.sso}"/>
	</c:when>
	<c:otherwise>
		<c:set var="sso" value="${sso}"/>
	</c:otherwise>
</c:choose>

<gene:template file="scheda-template.jsp" gestisciProtezioni="false" >
	<gene:redefineInsert name="head" >
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
				
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
				<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easytabs.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.inseriscidocdaprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
		
		<style type="text/css">
		.dataTables_filter {
	     	display: none;
		}
		
		.dataTables_length {
			padding-top: 5px;
			padding-bottom: 5px;
		}
		
		.dataTables_length label {
			vertical-align: bottom;
		}
		
		.dataTables_paginate {
			padding-bottom: 5px;
		}

	</style>
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Inserisci documenti da protocollo" />
	
	<gene:redefineInsert name="corpo">
		<table class="dettaglio-notab">	
		<tr>
			<span id="titolo0">Mediante questa funzione è possibile inserire i documenti in gara recuperandoli dai documenti protocollati nel sistema documentale.</span>
			<br>
			<span id="titolo1">Specificare il protocollo da cui recuperare i documenti.</span>
			<span id="titolo2">Di seguito sono riportati i dati del protocollo selezionato.</span>
			<span id="titolo3" style="display: none;">L'inserimento dei documenti mediante copia dal protocollo si è concluso correttamente.</span>
		</tr>
		<tr>
		<br>
		</tr>
		<tr>
		<br>
		</tr>
		</table>
		<form id="parametririchiestafascicolo">
		<table class="dettaglio-notab" id="datiLogin">			
			<tr id="rigaTitoloLogin">
				<td colspan="2">
					<b>Parametri utente per l'inoltro delle richieste al servizio remoto</b>
				</td>
			</tr>
			<tr id="rigaUtente">
				<td class="etichetta-dato">Utente (*)</td>
				<td class="valore-dato"><input id="username" name="username" title="Utente" class="testo" type="text" size="24" value="" maxlength="100" /></td>
			</tr>
			<tr id="rigaPassword">
				<td class="etichetta-dato">Password (*)</td>
				<td class="valore-dato"><input id="password" name="password" title="Password" class="testo" type="password" size="24" value="" maxlength="100"/></td>
			</tr>
			
			<tr>
				<td colspan="2">
						<b>Selezione protocollo</b>
					</td>
				</tr>
				<tr id="sezioneprofilo" >
					<td class="etichetta-dato">Profilo (*)</td>
					<td class="valore-dato">
						<select id="profilo" name="profilo" style="display: none;"></select>
					</td>
				</tr>
				<tr>
					<td class="etichetta-dato">Numero protocollo (*)</td>
					<td class="valore-dato"><input id="numeroprotocolloRicerca" name="numeroprotocolloRicerca" title="Numero protocollo" class="testo" type="text" size="24" value="" maxlength="100"></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Anno protocollo (*)</td>
					<td class="valore-dato"><input id="annoprotocolloRicerca" name="annoprotocolloRicerca" title="Anno protocollo" class="testo" type="text" size="6" value="" maxlength="4"></td>
				</tr>
				<tr>
					<td  colspan="2">
						<div  class="error" id="sezioneErrori"></div>
					<td>
				<tr>
				<tr>
				<td class="comandi-dettaglio" colspan="2">
					<INPUT type="button" id="wsdmConfermaStep1" class="bottone-azione" value="Conferma" title="Conferma"  onclick="javascript:confermaStep1();"/>
					<INPUT type="button" id="wsdmAnnullaStep1" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:historyVaiIndietroDi(1);"/>
					&nbsp;
				</td>
			</tr>
						
			
		</table>
				
		<table class="dettaglio-notab"  style="display: none;" id="fascicolo">	
			<div  class="error" id="wsdmdocumentomessaggio"></div>
			<tr>
			<td colspan="2"><br><b>Dati del fascicolo associato al protocollo selezionato</b></td>
			</tr>
			<tr>
				<td class="etichetta-dato">Codice fascicolo</td>
				<td class="valore-dato"><span id="codicefascicolotesto" title="Codice fascicolo"></span>
				<input type="hidden" id="codicefascicolo" name="codicefascicolo" value="">
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Anno</td>
				<td class="valore-dato"><span id="annofascicolotesto" title="anno fascicolo"></span>
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Numero</td>
				<td class="valore-dato"><span id="numerofascicolotesto" title="numero fascicolo"></span>
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Oggetto</td>
				<td class="valore-dato"><span id="oggettofascicolotesto" title="Oggetto"></span>
				<input type="hidden" id="oggettofascicolo" name="oggettofascicolo" value="">
				</td>
			</tr>	
			<tr>
				<td class="etichetta-dato">Classifica</td>
				<td class="valore-dato"><span id="classificafascicolodescrizione" title="Classifica"></span>
				<input type="hidden" id="classificafascicolo" name="classificafascicolo" value="">
				</td>
			</tr>
		</table>
		<INPUT type="hidden" id="idconfi" name="idconfi" value="${idconfi}"/>
		<INPUT type="hidden" id="servizio" name="servizio" value="FASCICOLOPROTOCOLLO"/>
		<INPUT type="hidden" id="genereGara" name="genereGara" value="${genereGara}"/>
		<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
		<input type="hidden" id="step" name="step" value="1" />
		<input type="hidden" id="voce" name="voce" value="" />
		<input type="hidden" id="entita" name="entita" value="${entita }" />
		<input type="hidden" id="key1" name="key1" value="${key1}" />
		<input type="hidden" id="numerofascicolo" name="numerofascicolo" value="" />
		<input type="hidden" id="annofascicolo" name="annofascicolo" value="" />
		<input type="hidden" id="inserimentoinfascicolo" name="inserimentoinfascicolo" value="SI_FASCICOLO_NUOVO" />
		<input type="hidden" id="tiposistemaremoto" name="tiposistemaremoto" value="TITULUS" />
		<input type="hidden" id="ngara" name="ngara" value="${ngara }" />
		<input type="hidden" id="codiceGara" name="codiceGara" value="${codiceGara }" />
		<input type="hidden" id="gruppo" name="gruppo" value="${gruppo }" />
		<input type="hidden" id="tipologiaDoc" name="tipologiaDoc" value="${tipologiaDoc }" />
		<input type="hidden" id="INOUT" name="INOUT" value="" />
		<input type="hidden" id="sso" name="sso" value="${sso }" />
		<input type="hidden" id="utenteSso" name="utenteSso" value="${profiloUtente.login }" />
				 
		<table class="dettaglio-notab" style="display: none;" id="elementiDocumentali">	
			<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-fascicoloElementiDocumentali.jsp"></jsp:include>
			
			<tr id="associazioneFascicoloTitle">
				<td colspan="2" ><br><b>Dati del fascicolo associato alla gara</b></td>
			</tr>
			<tr id="associazioneFascicolo" style="display: none;">
			<td colspan="2">
				<span><br>La gara non ha ancora un fascicolo associato. Impostare la scelta: <br></span>
				<input type="radio" id="associaFascicoloSi" name="associa" value="si" checked="checked"><span><b>Associa la gara al fascicolo del protocollo selezionato</b>
				<br>&nbsp;&nbsp;In questo caso i documenti inseriti in gara rimangono collegati al loro protocollo e non ne è più richiesto il trasferimento nel documentale.</span><br><br>
				<input type="radio" id="associaFascicoloNo" name="associa" value="no"><span><b>Crea il fascicolo della gara in seguito</b>
				<br>&nbsp;&nbsp;In questo caso i documenti inseriti verranno nuovamente protocollati o trasferiti al documentale nell'ambito del nuovo fascicolo.</span><br><br>
			</td>
			</tr>
			<tr id="FascicoliDiversi" style="display: none;">
				<td colspan="2">
				<span><br>La gara risulta associata a un fascicolo che è diverso da quello del protocollo selezionato. La gara rimane associata a tale fascicolo e i documenti inseriti verranno nuovamente protocollati o trasferiti al documentale nell'ambito di tale fascicolo.<br></span>
				</td>
			</tr>
			<tr id="FascicoliUguali" style="display: none;">
				<td colspan="2">
				<span><br>La gara risulta associata allo stesso fascicolo del protocollo selezionato. Pertanto i documenti inseriti in gara rimangono collegati al loro protocollo e non ne è più richiesto il trasferimento nel documentale.<br></span>
				</td>
			</tr>
			
			<tr id="rifFascicoloGara"  style="display: none;">
				<td class="etichetta-dato">Riferimento al fascicolo</td>
				<td class="valore-dato"><span id="fascicoloGara" title="Riferimento al fascicolo"></span>
				</td>
			</tr>
			
			<tr id="sezioneamministrazioneorganizzativa" style="display: none;">
					<td colspan="2">
						<b><br>Amministrazione organizzativa</b>
						<div style="display: none;" class="error" id="amministrazioneorganizzativamessaggio"></div>
					</td>
			</tr>
			<tr style="display: none;" id="rigaCodiceAoo">
				<td class="etichetta-dato">Codice Aoo</td>
				<td class="valore-dato"><select id="codiceaoonuovo" name="codiceaoonuovo" style="display: none;"></select>
						<select id="codiceaoonuovo_filtro" name="codiceaoonuovo_filtro" style="display: none;"></select>
						<span id="codiceaoo" name="codiceaoo" style="display: none;"></span></td>
			</tr>
			<tr style="display: none;" id="rigaCodiceUfficio">
				<td class="etichetta-dato">Codice ufficio</td>
				<td class="valore-dato"><select id="codiceufficionuovo" name="codiceufficionuovo" style="display: none;max-width:450px" ></select>
						<select id="codiceufficionuovo_filtro" name="codiceufficionuovo_filtro" style="display: none;"></select>
						<span id="codiceufficio" name="codiceufficio" style="display: none;"></span></td>
			</tr>
			
			<tr id="provvedimento" style="display: none;" >
			<td colspan="2" ><br><b>Dati del provvedimento</b></td>
			</tr>
			<tr id="rigaDataProv" style="display: none;">
				<td class="etichetta-dato">Data</td>
				<td class="valore-dato">
					<input  id="dataprov" name="dataprov" title="Data provvedimento" class="data testo" type="text" value="" size="10" maxlength="10">
				</td>
			</tr>
			<tr id="rigaNumeroProv" style="display: none;">
				<td class="etichetta-dato">Numero</td>
				<td class="valore-dato"><input id="numeroprov" name="numeroprov" title="Numero provvedimento" class="testo" type="text" value="" size="24" maxlength="20">
				</td>
			</tr>
			
			<tr>
				<td colspan="2"><br>Confermi l'inserimento in gara dei documenti allegati al protocollo selezionato?<br><br></td>
			</tr>
			
			<tr>
				<td class="comandi-dettaglio" colspan="2">
					<INPUT  type="button" id="wsdmConfermaStep2" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:confermaStep2();"/>
					<INPUT  type="button" id="wsdmAnnullaStep2" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:historyVaiIndietroDi(1);"/>
					
					&nbsp;
				</td>
			</tr>
		</table>
		
		</form>
		
		<table class="dettaglio-notab" style="display: none;" id="successo">	
			<tr>
				<td class="comandi-dettaglio" colspan="2">
					<INPUT  type="button" id="wsdmChiudiStep3" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:historyVaiIndietroDi(1);"/>
					
					&nbsp;
				</td>
			</tr>
		</table>
		
			
		<gene:redefineInsert name="documentiAssociati"></gene:redefineInsert> 
		<gene:redefineInsert name="noteAvvisi"></gene:redefineInsert>
		<gene:redefineInsert name="helpPagina" ></gene:redefineInsert>

		<form name="formgetdocumentoallegato" action="${pageContext.request.contextPath}/pg/GetWSDMDocumentoAllegato.do" method="post">
			<input type="hidden" id="getdocumentoallegato_username" name="getdocumentoallegato_username" value="" />
			<input type="hidden" id="getdocumentoallegato_password" name="getdocumentoallegato_password" value="" />
			<input type="hidden" id="getdocumentoallegato_ruolo" name="getdocumentoallegato_ruolo" value="" />
			<input type="hidden" id="getdocumentoallegato_nome" name="getdocumentoallegato_nome" value="" />
			<input type="hidden" id="getdocumentoallegato_cognome" name="getdocumentoallegato_cognome" value="" />
			<input type="hidden" id="getdocumentoallegato_codiceuo"  name="getdocumentoallegato_codiceuo" value="" />
			<input type="hidden" id="getdocumentoallegato_idutente"  name="getdocumentoallegato_idutente" value="" />
			<input type="hidden" id="getdocumentoallegato_idutenteunop"  name="getdocumentoallegato_idutenteunop" value="" />
			<input type="hidden" id="getdocumentoallegato_numerodocumento" name="getdocumentoallegato_numerodocumento" value="" />
			<input type="hidden" id="getdocumentoallegato_nomeallegato" name="getdocumentoallegato_nomeallegato" value="" />
			<input type="hidden" id="getdocumentoallegato_tipoallegato" name="getdocumentoallegato_tipoallegato" value="" />
			<input type="hidden" id="getdocumentoallegato_servizio" name="getdocumentoallegato_servizio" value="FASCICOLOPROTOCOLLO" />
			<input type="hidden" id="getdocumentoallegato_idconfi" name="getdocumentoallegato_idconfi" value="${idconfi }" />
		</form>
		
		<form name="formgetprotocolloallegato" action="${pageContext.request.contextPath}/pg/GetWSDMProtocolloAllegato.do" method="post">
			<input type="hidden" id="getprotocolloallegato_username" name="getprotocolloallegato_username" value="" />
			<input type="hidden" id="getprotocolloallegato_password" name="getprotocolloallegato_password" value="" />
			<input type="hidden" id="getprotocolloallegato_ruolo" name="getprotocolloallegato_ruolo" value="" />
			<input type="hidden" id="getprotocolloallegato_nome" name="getprotocolloallegato_nome" value="" />
			<input type="hidden" id="getprotocolloallegato_cognome" name="getprotocolloallegato_cognome" value="" />
			<input type="hidden" id="getprotocolloallegato_codiceuo"  name="getprotocolloallegato_codiceuo" value="" />
			<input type="hidden" id="getprotocolloallegato_idutente"  name="getprotocolloallegato_idutente" value="" />
			<input type="hidden" id="getprotocolloallegato_idutenteunop"  name="getprotocolloallegato_idutenteunop" value="" />
			<input type="hidden" id="getprotocolloallegato_annoprotocollo" name="getprotocolloallegato_annoprotocollo" value="" />	
			<input type="hidden" id="getprotocolloallegato_numeroprotocollo" name="getprotocolloallegato_numeroprotocollo" value="" />
			<input type="hidden" id="getprotocolloallegato_nomeallegato" name="getprotocolloallegato_nomeallegato" value="" />
			<input type="hidden" id="getprotocolloallegato_tipoallegato" name="getprotocolloallegato_tipoallegato" value="" />
			<input type="hidden" id="getprotocolloallegato_servizio" name="getprotocolloallegato_servizio" value="FASCICOLOPROTOCOLLO" />
			<input type="hidden" id="getprotocolloallegato_idconfi" name="getprotocolloallegato_idconfi" value="${idconfi }" />
		</form>


	</gene:redefineInsert>
	
</gene:template>

