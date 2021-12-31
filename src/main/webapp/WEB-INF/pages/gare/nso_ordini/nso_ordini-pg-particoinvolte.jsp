<%
/*
 * Created on: 13/11/2006
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<c:set var="id" value='${gene:getValCampo(key, "ID")}'/>

<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/nso-ordini.js"></script>
</gene:redefineInsert>

<%/* Dati generali della gara */%>
<gene:formScheda entita="NSO_ORDINI" gestisciProtezioni="true"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreOrdiniNso">

	<gene:redefineInsert name="addToAzioni" >
		<c:if test="${(requestScope.statoOrdine eq 1) || (requestScope.statoOrdine eq 2)}">
			<tr>
				<td class="vocemenulaterale">
						<a href="javascript:validaOrdine();" id="menuValidaOrdine" title="Controlla Dati Inseriti" tabindex="1510">Controlla Dati Inseriti</a>
				</td>
			</tr>
		</c:if>
	</gene:redefineInsert>
	<gene:redefineInsert name="addToDocumenti" />
	<gene:redefineInsert name="schedaNuovo" />
	<gene:redefineInsert name="pulsanteNuovo" />
	<c:if test='${requestScope.statoOrdine eq 3 || requestScope.statoOrdine eq 4}'>
		<gene:redefineInsert name="schedaModifica" />
		<gene:redefineInsert name="pulsanteModifica" />
	</c:if>

	<gene:campoScheda campo="ID" visibile="false"   />
	<gene:campoScheda campo="CODORD" visibile="false" />
	
	<c:if test='${modoAperturaScheda ne "NUOVO"}' >
		<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneOrdinantiNsoFunction", pageContext, "NSO_ORDINANTI", id)}'/>
	</c:if>
	
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='NSO_ORDINANTI'/>
		<jsp:param name="chiave" value='${id}'/>
		<jsp:param name="nomeAttributoLista" value='ordinanti' />
		<jsp:param name="idProtezioni" value="NSO_ORDIN" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/nso_ordinanti/ordinante.jsp" />
		<jsp:param name="arrayCampi"
			value="'NSO_ORDINANTI_ID_', 'NSO_ORDINANTI_NSO_ORDINI_ID_', 'NSO_ORDINANTI_TIPO_', 'NSO_ORDINANTI_CODEIN_','NSO_ORDINANTI_NOMEIN_','NSO_ORDINANTI_ENDPOINT_','NSO_ORDINANTI_VIA_','NSO_ORDINANTI_CITTA_','NSO_ORDINANTI_CAP_','NSO_ORDINANTI_CODNAZ_','NSO_ORDINANTI_PIVA_'" />
		<jsp:param name="usaContatoreLista" value="true" />
		<jsp:param name="titoloSezione" value="Ordinante" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo ordinante" />
		<jsp:param name="descEntitaVociLink" value="ordinante" />
		<jsp:param name="msgRaggiuntoMax" value="gli ordinanti" />
		<jsp:param name="sezioneInseribile" value="true"/>
		<jsp:param name="sezioneEliminabile" value="true"/>
	</jsp:include>

	<gene:gruppoCampi idProtezioni="BENEF" >
		<gene:campoScheda>
			<td colspan="2"><b>Beneficiario</b></td>
		</gene:campoScheda>
	</gene:gruppoCampi>
	
	<gene:campoScheda campo="IS_DIV_BENEF"  />
	<gene:campoScheda campo="ID" entita="NSO_BENEFICIARIO" where="NSO_BENEFICIARIO.NSO_ORDINI_ID=NSO_ORDINI.ID AND NSO_ORDINI.ID=${id}"  visibile="false"   />
	<gene:campoScheda campo="NSO_ORDINI_ID" entita="NSO_BENEFICIARIO" where="NSO_BENEFICIARIO.NSO_ORDINI_ID=NSO_ORDINI.ID" visibile="false"   />
	<gene:campoScheda campo="DENOMINAZIONE" entita="NSO_BENEFICIARIO" where="NSO_BENEFICIARIO.NSO_ORDINI_ID=NSO_ORDINI.ID"  />
	<gene:campoScheda campo="CONTATTO_RIF" title="Contatto di riferimento" entita="NSO_BENEFICIARIO" where="NSO_BENEFICIARIO.NSO_ORDINI_ID=NSO_ORDINI.ID"   />
	<gene:campoScheda campo="INDIRIZZO" entita="NSO_BENEFICIARIO" where="NSO_BENEFICIARIO.NSO_ORDINI_ID=NSO_ORDINI.ID"   />
	<gene:campoScheda campo="LOCALITA" title="Localita'" entita="NSO_BENEFICIARIO" where="NSO_BENEFICIARIO.NSO_ORDINI_ID=NSO_ORDINI.ID"   />
	<gene:campoScheda campo="CAP" entita="NSO_BENEFICIARIO" where="NSO_BENEFICIARIO.NSO_ORDINI_ID=NSO_ORDINI.ID"   />
	<gene:campoScheda campo="CITTA" title="Citta'" entita="NSO_BENEFICIARIO" where="NSO_BENEFICIARIO.NSO_ORDINI_ID=NSO_ORDINI.ID"   />
	<gene:campoScheda campo="CODNAZ" entita="NSO_BENEFICIARIO" where="NSO_BENEFICIARIO.NSO_ORDINI_ID=NSO_ORDINI.ID"   />

	<gene:gruppoCampi idProtezioni="FORN" >
		<gene:campoScheda>
			<td colspan="2"><b>Fornitore</b></td>
		</gene:campoScheda>
		
		<gene:campoScheda campo="ID" entita="NSO_FORNITORE" where="NSO_FORNITORE.NSO_ORDINI_ID=NSO_ORDINI.ID"  visibile="false"   />
		<gene:campoScheda campo="NSO_ORDINI_ID" entita="NSO_FORNITORE" where="NSO_FORNITORE.NSO_ORDINI_ID=NSO_ORDINI.ID" visibile="false"   />
		
		<c:if test='${modo eq "VISUALIZZA"}' >
			<c:set var="link" value='javascript:archivioImpresaAggDef();' />
		</c:if>
		<gene:campoScheda campo="CODIMP"  entita="NSO_FORNITORE" where="NSO_FORNITORE.NSO_ORDINI_ID=NSO_ORDINI.ID" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}' modificabile="false"  />
		<gene:campoScheda campo="ENDPOINT" title="Identificativo NSO del fornitore" entita="NSO_FORNITORE" where="NSO_FORNITORE.NSO_ORDINI_ID=NSO_ORDINI.ID" visibile="false" />
		<gene:campoScheda campo="NOMIMP" title="Denominazione impresa aggiudicataria" entita="NSO_FORNITORE" where="NSO_FORNITORE.NSO_ORDINI_ID=NSO_ORDINI.ID" modificabile="false" />
		<gene:campoScheda campo="CFIMP" title="Partita IVA/Codice fiscale del fornitore" entita="NSO_FORNITORE" where="NSO_FORNITORE.NSO_ORDINI_ID=NSO_ORDINI.ID" modificabile="false" />
		<gene:campoScheda campo="VIA" entita="NSO_FORNITORE" where="NSO_FORNITORE.NSO_ORDINI_ID=NSO_ORDINI.ID" modificabile="false" />
		<gene:campoScheda campo="CITTA" entita="NSO_FORNITORE" where="NSO_FORNITORE.NSO_ORDINI_ID=NSO_ORDINI.ID" modificabile="false" />
		<gene:campoScheda campo="CAP" entita="NSO_FORNITORE" where="NSO_FORNITORE.NSO_ORDINI_ID=NSO_ORDINI.ID" modificabile="false" />
		<gene:campoScheda campo="CODNAZ" entita="NSO_FORNITORE" where="NSO_FORNITORE.NSO_ORDINI_ID=NSO_ORDINI.ID" modificabile="false" />
		<gene:campoScheda campo="PERSONA_RIF" title="Persona di riferimento" entita="NSO_FORNITORE" where="NSO_FORNITORE.NSO_ORDINI_ID=NSO_ORDINI.ID" />
		
	</gene:gruppoCampi>


		<gene:campoScheda>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<c:choose>
			<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
				<gene:insert name="pulsanteSalva">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
				</gene:insert>
				<gene:insert name="pulsanteAnnulla">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
				</gene:insert>
		
			</c:when>
			<c:otherwise>
				<gene:insert name="pulsanteModifica">
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
					</c:if>
				</gene:insert>
				<gene:insert name="pulsanteNuovo">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovo()" id="btnNuovo">
					</c:if>
				</gene:insert>
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>
		

	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	
<!--
 Parametri provenienti dalla pagina di associazione della gara all'appalto. Vengono salvati
 perche' in caso di errore in salvataggio della scheda sono necessari a ricaricare 
 correttamente la scheda
 -->
	<input type="hidden" name="tipoAppalto" id="tipoAppalto" value="${param.tipoAppalto}"/>
	<input type="hidden" name="tipoGara" id="tipoGara" value="${param.tipoGara}"/>
	<input type="hidden" name="chiaveRiga" id="chiaveRiga" value="${param.chiaveRiga}"/>
	<input type="hidden" name="modalitaPresentazione" id="modalitaPresentazione" value="${param.modalitaPresentazione}"/>
	<input type="hidden" name="lottoOfferteDistinte" id="lottoOfferteDistinte" value="${param.lottoOfferteDistinte}"/>
	<input type="hidden" name="codCPV" id="codCPV" value="${requestScope.initCODCPV}"/>

	<gene:fnJavaScriptScheda funzione="gestioneBeneficiario('#NSO_ORDINI_IS_DIV_BENEF#')" elencocampi="NSO_ORDINI_IS_DIV_BENEF" esegui="true" />
	
</gene:formScheda>

		<form name="formVisualizzaPermessiUtenti" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtenti.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="genereGara" id="genereGara" value="" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
			<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		</form> 
		
		<form name="formVisualizzaPermessiUtentiStandard" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiStandard.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="genereGara" id="genereGara" value="" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
			<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		</form>
<div id="nso-dialog-verification" title="Verifica Ordine NSO" style="display:none">
  <p id="nso-dialog-verification-content">
  	
  </p>
</div>
<gene:javaScript>

	$(document).ready(function(){
		var i;
		for (i=0; i<10; i++) {
			$("#rowtitoloNSO_ORDIN_"+i+" b").text("Sezione ordinanti/emissori");
			$("#rowtitoloNSO_ORDIN_"+i+" b").css("font-weight","bold");
		}
	});


		function gestioneBeneficiario(isDivBenef) {
			if (isDivBenef == '1') {
				$("#rowNSO_BENEFICIARIO_DENOMINAZIONE").show();
				$("#rowNSO_BENEFICIARIO_CONTATTO_RIF").show();
				$("#rowNSO_BENEFICIARIO_INDIRIZZO").show();
				$("#rowNSO_BENEFICIARIO_LOCALITA").show();
				$("#rowNSO_BENEFICIARIO_CAP").show();
				$("#rowNSO_BENEFICIARIO_CITTA").show();
				$("#rowNSO_BENEFICIARIO_CODNAZ").show();
			} else {
				$("#rowNSO_BENEFICIARIO_DENOMINAZIONE").hide();
				$("#rowNSO_BENEFICIARIO_CONTATTO_RIF").hide();
				$("#rowNSO_BENEFICIARIO_INDIRIZZO").hide();
				$("#rowNSO_BENEFICIARIO_LOCALITA").hide();
				$("#rowNSO_BENEFICIARIO_CAP").hide();
				$("#rowNSO_BENEFICIARIO_CITTA").hide();
				$("#rowNSO_BENEFICIARIO_CODNAZ").hide();
				$("#NSO_BENEFICIARIO_DENOMINAZIONE").val('');
				$("#NSO_BENEFICIARIO_CONTATTO_RIF").val('');
				$("#NSO_BENEFICIARIO_INDIRIZZO").val('');
				$("#NSO_BENEFICIARIO_LOCALITA").val('');
				$("#NSO_BENEFICIARIO_CAP").val('');
				$("#NSO_BENEFICIARIO_CITTA").val('');
				$("#NSO_BENEFICIARIO_CODNAZ").val('');
			}
		}
		
		function archivioImpresaAggDef(){
			var codiceImpresa = getValue("NSO_FORNITORE_CODIMP");
			var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
			document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href;
		}



</gene:javaScript>

	