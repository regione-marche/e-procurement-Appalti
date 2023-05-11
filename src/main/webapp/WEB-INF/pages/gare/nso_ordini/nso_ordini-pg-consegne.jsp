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

<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneDatiNsoFunction", pageContext, id)}'/>
<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/nso-ordini.js?t=<%=System.currentTimeMillis()%>"></script>
</gene:redefineInsert>

<%/* Dati generali della gara */%>
<gene:formScheda entita="NSO_ORDINI" gestisciProtezioni="true"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreOrdiniNso">
		<c:if test='${(requestScope.statoOrdine ne 1 && requestScope.statoOrdine ne 2)}'>
				<gene:redefineInsert name="schedaModifica" />
				<gene:redefineInsert name="pulsanteModifica" />
		 </c:if>
	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${(requestScope.statoOrdine eq 4 || requestScope.statoOrdine eq 5 || requestScope.statoOrdine eq 6) && requestScope.isPeriodoVariazione eq 1}' >
			<tr>
				<td class="vocemenulaterale">
						<a href="javascript:revocaOrdineNso();" id="menuValidaOrdine" title="Revoca Ordine" tabindex="1510">Revoca Ordine</a>
				</td>
			</tr>
		</c:if>
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
	<c:if test="${requestScope.statoOrdine eq 8}">
		<%/*
			se l'ordine è revocato non devo permettere alcuna modifica
		*/ %>
		<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
		<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
	</c:if>

	<gene:campoScheda campo="ID" visibile="false"   />
	<gene:campoScheda campo="CODORD" visibile="false" />
	
	<gene:gruppoCampi idProtezioni="DATE_CONS">
		<gene:campoScheda>
			<td colspan="2"><b>Date di consegna</b></td>
		</gene:campoScheda>
		<gene:campoScheda title="Data inizio fornitura/erogazione del servizio" campo="DATA_INIZIO_FORN" obbligatorio="${requestScope.isMonoRiga eq 1}" />
		<gene:campoScheda title="Data fine fornitura/erogazione del servizio" campo="DATA_FINE_FORN" obbligatorio="${requestScope.isMonoRiga eq 1}" />
	</gene:gruppoCampi>
		
	<gene:gruppoCampi idProtezioni="PCONS" >
		<gene:campoScheda>
			<td colspan="2"><b>Punto di consegna</b></td>
		</gene:campoScheda>
	</gene:gruppoCampi>
	
	<gene:campoScheda campo="ID" entita="NSO_PUNTICONS" where="NSO_PUNTICONS.NSO_ORDINI_ID=NSO_ORDINI.ID"  visibile="false"   />
	<gene:campoScheda campo="NSO_ORDINI_ID" entita="NSO_PUNTICONS" where="NSO_PUNTICONS.NSO_ORDINI.ID=NSO_ORDINI.ID" visibile="false"   />
	<gene:campoScheda campo="CODEIN" entita="NSO_PUNTICONS" where="NSO_PUNTICONS.NSO_ORDINI.ID=NSO_ORDINI.ID" visibile="false" />
	
	<gene:archivio titolo="Punti di consegna"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.NSO_ORDINI.CENINT"),"gare/nso_puntico/popup-lista-punticons.jsp","")}'
			 scheda=''
			 schedaPopUp=''
			 campi="V_NSO_CONSEGNE.CODCONS_NSO;V_NSO_CONSEGNE.INDIRIZZO;V_NSO_CONSEGNE.LOCALITA;V_NSO_CONSEGNE.CAPEIN;V_NSO_CONSEGNE.CITEIN;V_NSO_CONSEGNE.CODNAZ"
			 functionId="skip"
			 chiave="NSO_PUNTICONS_COD_PUNTO_CONS"
			 inseribile="false">
			 <gene:campoScheda campo="COD_PUNTO_CONS"  entita="NSO_PUNTICONS" where="NSO_PUNTICONS.NSO_ORDINI.ID=NSO_ORDINI.ID"  />
			 <gene:campoScheda campo="INDIRIZZO" entita="NSO_PUNTICONS" where="NSO_PUNTICONS.NSO_ORDINI.ID=NSO_ORDINI.ID" modificabile="false"  />
			 <gene:campoScheda campo="LOCALITA" entita="NSO_PUNTICONS" where="NSO_PUNTICONS.NSO_ORDINI.ID=NSO_ORDINI.ID" modificabile="false"  />
			 <gene:campoScheda campo="CAP" entita="NSO_PUNTICONS" where="NSO_PUNTICONS.NSO_ORDINI.ID=NSO_ORDINI.ID" modificabile="false"  />
			 <gene:campoScheda campo="CITTA" entita="NSO_PUNTICONS" where="NSO_PUNTICONS.NSO_ORDINI.ID=NSO_ORDINI.ID" modificabile="false"  />
			 <gene:campoScheda campo="CODNAZ" entita="NSO_PUNTICONS" where="NSO_PUNTICONS.NSO_ORDINI.ID=NSO_ORDINI.ID" modificabile="false"  />
			 
	</gene:archivio>
	
	
	<gene:campoScheda title="Altre indicazioni" campo="ALTRE_INDIC" entita="NSO_PUNTICONS" where="NSO_PUNTICONS.NSO_ORDINI.ID=NSO_ORDINI.ID" />
	<gene:campoScheda title="Consegna Domiciliare?" campo="CONS_DOMICILIO" entita="NSO_PUNTICONS" where="NSO_PUNTICONS.NSO_ORDINI.ID=NSO_ORDINI.ID" modificabile="false" />
	
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
	<input type="hidden" name="idOrdine" id="idOrdine" value="${id}"/>
	<input type="hidden" name="tipoGara" id="tipoGara" value="${param.tipoGara}"/>
	<input type="hidden" name="chiaveRiga" id="chiaveRiga" value="${param.chiaveRiga}"/>
	
	
	
</gene:formScheda>

<div id="nso-dialog-verification" title="Verifica Ordine NSO" style="display:none">
  <p id="nso-dialog-verification-content">
  	
  </p>
</div>
<div id="nso-dialog-revocation" title="Revoca Ordine NSO" style="display:none">
			  <p id="nso-dialog-revocation-content">
			  	Vuoi revocare l&#39;ordine?<br>Questa operazione non si pu&ograve; annullare.
			  </p>
			</div>

<gene:javaScript>

	<c:if test="${modo eq 'VISUALIZZA' && (requestScope.statoOrdine eq 1 || requestScope.statoOrdine eq 2)}">
		function modificaPuntoConsegna() {
				var idOrdine = "${id}";
				var comando = "href=gare/nso_puntico/popup-modificaPuntoConsegna.jsp&idOrdine=" + idOrdine;
			 	openPopUpCustom(comando, "modificaPuntoConsegna", 700, 350, "yes", "yes");
		
		};
			
		function addHrefConsegna() {
			var _span = $("<span/>");
			_span.css("float", "right");
			_span.css("vertical-align", "top");
			var _href = "javascript:modificaPuntoConsegna();";
			var _a = $("<a/>",{"text": "Modifica punto di consegna", "href": _href});
			_span.append(_a);
			_span.appendTo($("#NSO_PUNTICONS_COD_PUNTO_CONSview").parent());
		};
	
		addHrefConsegna();
	</c:if>

</gene:javaScript>

	