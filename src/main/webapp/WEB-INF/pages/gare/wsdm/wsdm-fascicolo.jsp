<%
/*
 * Created on: 17-feb-2015
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

<gene:redefineInsert name="head" >
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easytabs.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.fascicolo.js"></script>
	
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


<c:set var="isRiservatezzaAttiva" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsRiservatezzaAttivaFunction", pageContext, param.key1, param.idconfi )}' />

<form id="parametririchiestafascicolo">
	<table class="dettaglio-notab">	
		<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
		<input id="servizio" type="hidden" value="${param.servizio }" />
		<input id="tiposistemaremoto" type="hidden" value="" />
		<input id="tabellatiInDB" type="hidden" value="" />
		<input id="modoapertura" type="hidden" value="VISUALIZZA" /> 
		<input id="entita" type="hidden" value="${param.entita}" /> 
		<input id="key1" type="hidden" value="${param.key1}" /> 
		<input id="key2" type="hidden" value="${param.key2}" />
		<input id="key3" type="hidden" value="${param.key3}" />
		<input id="key4" type="hidden" value="${param.key4}" />
		<input id="autorizzatoModifiche" type="hidden" value="${param.autorizzatoModifiche}" />
		<input id="autorizzatoAssociaFascicolo" type="hidden" value='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.WSDM-scheda.AssociaFascicoloEsistente")}' />
		<input id="classificafascicolonuovo" type="hidden" value=""/>
		<input id="idprg" type="hidden" value="PG" />
		<input id="idprofiloutente" type="hidden" value="${profiloUtente.id}"/>
		<jsp:include page="wsdm-login.jsp"></jsp:include>

		<tr>
			<td colspan="2"><br><b>Dati del fascicolo</b> <span style="float:right;"><a href="javascript:gestioneletturafascicolo();" id="linkleggiDatiFascicolo" class="linkLettura" style="display: none;">Rileggi dati fascicolo</a></span>
			</td>
		</tr>
		<tr>
			<td class="etichetta-dato">Anno fascicolo</td>
			<td class="valore-dato"><input id="annofascicolo" name="annofascicolo" title="Anno fascicolo" class="testo" type="text" size="6" value="" maxlength="4">
			<span id="spanannofascicolo" name="spanannofascicolo" title="Anno fascicolo" style="display: none;"></span></td>
		</tr>
		<tr>
			<td class="etichetta-dato">Numero fascicolo</td>
			<td class="valore-dato"><input id="numerofascicolo" name="numerofascicolo" title="Numero fascicolo" class="testo" type="text" size="24" value="" maxlength="100">
			<span id="spannumerofascicolo" name="spannumerofascicolo" title="Numero fascicolo" style="display: none;"></span></td>
		</tr>
		<tr>
			<td class="etichetta-dato">Codice fascicolo</td>
			<td class="valore-dato"><input id="codicefascicolo" name="codicefascicolo" title="Codice fascicolo" class="testo" type="text" size="40" value="" maxlength="100"></td>
		</tr>
		<tr>
			<td class="etichetta-dato">Oggetto</td>
			<td class="valore-dato"><span id="oggettofascicolo" name="oggettofascicolo" title="Oggetto"></span></td>
		</tr>	
		<tr>
			<td class="etichetta-dato">Classifica</td>
			<td class="valore-dato"><span id="classificafascicolodescrizione" name="classificafascicolodescrizione" title="Classifica"></span></td>
			<input type="hidden" id="classificadescrizione"  name="classificadescrizione"/>
		</tr>
		<tr>
			<td class="etichetta-dato">Descrizione</td>
			<td class="valore-dato"><span id="descrizionefascicolo" name="descrizionefascicolo" title="Descrizione"></span></td>
		</tr>
		<tr style="display: none;">
			<td class="etichetta-dato">Codice Aoo</td>
			<td class="valore-dato"><input id="codiceaoonuovo" name="codiceaoonuovo" title="Codice aoo" class="testo" type="text" size="6" value="" maxlength="4"></td>
		</tr>
		<tr style="display: none;">
			<td class="etichetta-dato">Codice ufficio</td>
			<td class="valore-dato"><input id="codiceufficionuovo" name="codiceufficionuovo" title="Codice ufficio" class="testo" type="text" size="6" value="" maxlength="4"></td>
		</tr>
		<INPUT type="hidden" id="strutturaonuovo" name="strutturaonuovo"/>
		<INPUT type="hidden" id="isRiservatezzaAttiva" name="isRiservatezzaAttiva" value="${isRiservatezzaAttiva}"/>
		<INPUT type="hidden" id="idconfi" name="idconfi" value="${param.idconfi}"/>
	</table>
</form>

<table class="dettaglio-notab">	
	<jsp:include page="wsdm-fascicoloElementiDocumentali.jsp"></jsp:include>
	<tr>
		<td class="comandi-dettaglio" colspan="2">
			<INPUT style="display: none;" type="button" id="wsdmSalvaPulsante" class="bottone-azione" value="Salva" title="Salva"/>
			<INPUT style="display: none;" type="button" id="wsdmAnnullaPulsante" class="bottone-azione" value="Annulla" title="Annulla"/>
			<INPUT type="button" id="wsdmModificaPulsante" class="bottone-azione" value="Associa fascicolo esistente" title="Associa fascicolo esistente"/>
			&nbsp;
		</td>
	</tr>
</table>	

<gene:redefineInsert name="addToAzioni">
	<tr>
        <c:if test='${isNavigazioneDisattiva ne "1"}'>
        	<tr style="display: none;" id="wsdmSalvaMenu"><td class="vocemenulaterale"><a title="Salva" tabindex="1512">Salva</a></td></tr>
          	<tr style="display: none;" id="wsdmAnnullaMenu"><td class="vocemenulaterale"><a title="Annulla" tabindex="1512">Annulla</a></td></tr>
          	<tr id="wsdmModificaMenu"><td class="vocemenulaterale"><a title="Associa fascicolo esistente" tabindex="1512">Associa fascicolo esistente</a></td></tr>
          	<tr id="wsdmImpostaCredenziali"><td class="vocemenulaterale"><a title="Imposta credenziali" tabindex="1513">Imposta credenziali</a></td></tr>
        </c:if>
	</tr>
</gene:redefineInsert>

<gene:redefineInsert name="documentiAssociati"></gene:redefineInsert> 
<gene:redefineInsert name="noteAvvisi"></gene:redefineInsert>
<gene:redefineInsert name="helpPagina" >
<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.HELPPAGINA")}'>
	<tr>
		<c:choose>
			<c:when test='${isNavigazioneDisabilitata ne "1"}'>
				<td class="vocemenulaterale" ><a id="alinkHelpPagina"
					href='javascript:helpDiPagina("${gene:getIdPagina(pageContext)}");'
					title="Informazioni sulla pagina" tabindex="1513">
				${gene:resource("label.tags.template.documenti.informazioniPagina")}
				</a></td>
			</c:when>
			<c:otherwise>
				<td>
				${gene:resource("label.tags.template.documenti.informazioniPagina")}
				</td>
			</c:otherwise>
		</c:choose>
	</tr>
</c:if>
			
</gene:redefineInsert>
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
	<input type="hidden" id="getdocumentoallegato_servizio" name="getdocumentoallegato_servizio" value="${param.servizio }" />
	<input type="hidden" id="getdocumentoallegato_idconfi" name="getdocumentoallegato_idconfi" value="${param.idconfi }" />
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
	<input type="hidden" id="getprotocolloallegato_servizio" name="getprotocolloallegato_servizio" value="${param.servizio }" />
	<input type="hidden" id="getprotocolloallegato_idconfi" name="getprotocolloallegato_idconfi" value="${param.idconfi }" />
</form>
