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

<%--
Viene sbiancata la variabile di sessione keyParentComunicazioni che viene inizializzata nella lista delle comunicazioni.
Se si crea una nuova comunicazione senza passare dalla lista delle comunicazioni la variabile altrimenti rimane valorizzata.
 --%>
<c:set var="keyParentComunicazioni" value="" scope="session"/>
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<fmt:setBundle basename="AliceResources" />
<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/nso-ordini.js?t=<%=System.currentTimeMillis()%>"></script>
</gene:redefineInsert>

<c:set var="chiaveGara" value='${key}'/>

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint}">
	<c:set var="filtroUffint" value=" CODEIN = '${sessionScope.uffint}'"/>
</c:if>

<c:set var="idOrdine" value='${gene:getValCampo(key, "NSO_ORDINI.ID")}' />
<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneDatiNsoFunction", pageContext, idOrdine)}'/>

<c:if test='${modo eq "VISUALIZZA"}'>
	<c:set var="IsW_CONFCOMPopolata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,"2")}' />
</c:if>

<c:set var="functionId" value="default_${modo eq 'MODIFICA'}" />
<c:set var="parametriWhere" value="" />
<c:if test="${modo eq 'MODIFICA'}">
	<c:set var="parametriWhere" value="N:${idOrdine}" />
</c:if>

<c:choose>
	<c:when test="${sessionScope.profiloUtente.ruoloUtenteMercatoElettronico eq 1 || sessionScope.profiloUtente.ruoloUtenteMercatoElettronico eq 3}">
		<c:set var="isPuntoOrdinante" value="true" />
	</c:when>
	<c:otherwise>
		<c:set var="isPuntoOrdinante" value="false" />
	</c:otherwise>
</c:choose>

<%/* Dati generali della gara */%>
<gene:formScheda entita="NSO_ORDINI" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInitOrdiniNso"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreOrdiniNso">

<%/* Viene riportato tipoGara, in modo tale che, in caso di errorie riapertura della pagina, 
     venga riaperta considerando il valore definito inizialmente per la prima apertura della pagina */%>
<input type="hidden" name="tipoGara" value="${param.tipoGara}" />

<gene:redefineInsert name="schedaNuovo" />
<gene:redefineInsert name="pulsanteNuovo" />
<%-- <c:if test="${requestScope.statoOrdine eq 8}">
	<%/*
		se l'ordine è revocato non devo permettere alcuna modifica
	*/ %>
	<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
</c:if> --%>
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
<c:if test='${isPuntoOrdinante}'>
	<c:if test='${requestScope.statoOrdine eq 2}'>
	   	<tr>
			<td class="vocemenulaterale">
					<a href="javascript:inviaOrdine();" id="menuInviaOrdine" title="invia ordine a NSO" tabindex="1510">Invia ordine a NSO</a>
			</td>
		</tr>
	</c:if>
</c:if>
<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#") }'>
<%--and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni") --%>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="titolomenulaterale" title='${gene:resource("label.tags.template.documenti.sezioneComunicazioni")}'>
				${gene:resource("label.tags.template.documenti.sezioneComunicazioni")}</td>
		</tr>
	<tr>
		<td class="vocemenulaterale" >
			<c:if test='${isNavigazioneDisattiva ne "1"}'>
				<a href="javascript:leggiComunicazioni();" title="${gene:resource('label.tags.template.documenti.comunicazioniRicevute.tooltip')}" tabindex="1515">
			</c:if>
			${gene:resource('label.tags.template.documenti.comunicazioniRicevute')}
			<c:set var="numComunicazioniRicevuteNonLette" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNumComunicazioniRicevuteDaLeggereFunction", pageContext, gene:getValCampo(key, "NSO_ORDINI.ID"))}' />
			<c:if test="${numComunicazioniRicevuteNonLette > 0}">(${numComunicazioniRicevuteNonLette} ${gene:resource('label.tags.template.documenti.comunicazioniRicevute.indicatore')})</c:if>
			<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
		</td>
	</tr>
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaComunicazioni")}'>
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:inviaComunicazioni();" title="${gene:resource('label.tags.template.documenti.inviaComunicazioni.tooltip')}" tabindex="1504">
						</c:if>
						${gene:resource('label.tags.template.documenti.inviaComunicazioni')}
						<c:set var="numComunicazioniBozza" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumComunicazioniBozzaFunction", pageContext, "NSO_ORDINI", gene:getValCampo(key, "NSO_ORDINI.ID"))}' />
						<c:if test="${numComunicazioniBozza > 0}">(${numComunicazioniBozza} ${gene:resource('label.tags.template.documenti.inviaComunicazioni.indicatore')})</c:if>
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			<c:if test="${autorizzatoModifiche ne '2' and gene:checkProt(pageContext,'FUNZ.VIS.INS.GENEWEB.W_INVCOM-lista.LISTANUOVO')}">
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:nuovaComunicazione();" title="Nuova comunicazione" tabindex="1517">
						</c:if>
						Nuova comunicazione
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			</c:if>
	</c:if>
</c:if>
<%-- <tr>
	<td  class="vocemenulaterale">
		1.<c:out value="${fn:contains(listaOpzioniDisponibili, 'OP114#') }" ></c:out>
		<br>2.<c:out value="${listaOpzioniDisponibili}"></c:out>
	</td>
</tr> --%>
</gene:redefineInsert>
	<gene:redefineInsert name="addToDocumenti" >
		<c:if test='${modo eq "VISUALIZZA" && !empty datiRiga.GARE_NUMERA && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.VisualizzaDocAppaltoDaGare") 
			&& gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.NUMERA") && controlloPahDocAssociatiPl}'>
			<tr>
				<td class="vocemenulaterale">
					<a href='javascript:visualizzaDocumentiAssociatiAppalto();' title="Documenti associati dell'appalto" tabindex="1522">
						Documenti associati dell'appalto
					</a>
				</td>
			</tr>
		</c:if>

	</gene:redefineInsert>
	

		<gene:campoScheda campo="ID" visibile="false" />
		<gene:campoScheda campo="CODORD"  modificabile="false" />
		<gene:campoScheda campo="NGARA" defaultValue="${requestScope.initNGARA}"   />
		<gene:campoScheda campo="ID_ORIGINARIO"  visibile="false" />
		<gene:campoScheda campo="SYSCON"  visibile="false" />
		<gene:campoScheda campo="VERSIONE"  visibile="false" />
		<gene:campoScheda campo="OGGETTO" defaultValue="${requestScope.initOGGETTO}"   />
		<gene:campoScheda campo="CODEIN" visibile="false" defaultValue="${sessionScope.uffint}"   />
		<gene:campoScheda campo="RIF_OFFERTA" title="Riferimento offerta" defaultValue="${requestScope.initNUMORDPL}"   />
		<gene:campoScheda campo="DATA_ORDINE" defaultValue="${requestScope.initDATORD}" />
		<gene:campoScheda campo="REFERENTE" />
		<gene:campoScheda campo="CENTRO_COSTO" title="Centro di costo dell'ordine"/>
		<gene:campoScheda campo="DATA_SCADENZA" obbligatorio="true" />
		<gene:campoScheda campo="DATA_LIMITE_MOD" modificabile="false" />
		
		<gene:campoScheda campo="CODEIN_FATTURA" visibile="false" defaultValue="${requestScope.initUFFINT}"   />
		
		<gene:archivio titolo="Ordini collegabili"
			lista='gare/nso_ordini/popup-lista-ordini.jsp'
			scheda=""
			schedaPopUp=""
			campi="NSO_ORDINI.CODORD"
			functionId="${functionId}"
			parametriWhere="${parametriWhere}"
			chiave=""
			inseribile="false">
			<gene:campoScheda campo="CODORD_COLLEGATO" />
		</gene:archivio>
	
		<gene:campoScheda campo="CIG" modificabile="false" defaultValue="${requestScope.initCODCIG}"/>
		<gene:campoScheda campo="ESENZIONE_CIG" title="Codice esenzione CIG" visibile="${empty datiRiga.NSO_ORDINI_CIG}" obbligatorio="${empty datiRiga.NSO_ORDINI_CIG}"/>
		<gene:campoScheda campo="CUP" defaultValue="${requestScope.initCUP}"/>
		<gene:campoScheda campo="NREPAT" defaultValue="${requestScope.initNREPAT}"/>
		<gene:campoScheda campo="NOTE" />
		<gene:campoScheda campo="IS_REVISIONE" title="Ordine in revisione?" modificabile="false" />
		<gene:campoScheda campo="STATO_ORDINE" defaultValue="1" modificabile="false" />
		
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
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") && requestScope.statoOrdine ne 8}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
					</c:if>
				</gene:insert>
				<gene:insert name="pulsanteConfermaCompletato">
					<c:if test='${requestScope.statoOrdine eq 1}'>
					<INPUT type="button" class="bottone-azione" value="Conferma ordine completato" title="Conferma ordine completato" onclick="javascript:validaOrdine('validateAndConfirm')">
					</c:if>
				</gene:insert>
				<c:if test='${isPuntoOrdinante}'>
					<c:if test='${requestScope.statoOrdine eq 2}'>
						<INPUT type="button"  class="bottone-azione" value='Invia ordine a NSO' title='Invia ordine a NSO' onclick="javascript:inviaOrdine()">
					</c:if>
					<c:if test='${(requestScope.statoOrdine eq 4 || requestScope.statoOrdine eq 5 || requestScope.statoOrdine eq 6) && requestScope.isPeriodoVariazione eq 1}' >
						<INPUT type="button"  class="bottone-azione" value='Revoca Ordine' title='Revoca Ordine' onclick="javascript:revocaOrdineNso()">
					</c:if>
				</c:if>	
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>


<input type="hidden" name="dittaAgg" id="dittaAgg" value="${requestScope.initDITTA}"/>		
<input type="hidden" name="arrmultikey" id="arrmultikey" value="${requestScope.initArrmultikey}"/>
<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche }" />

	
<!--

	<gene:insert name="pulsanteNuovo">
		<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
			<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovo()" id="btnNuovo">
		</c:if>
	</gene:insert>


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
	
</gene:formScheda>
<div id="nso-dialog-send-confirm" title="Invio Ordine NSO" style="display:none">
  <p id="nso-dialog-send-confirm-content">
  	Questa funzione permette di inviare l'ordine a NSO e quindi in automatico al fornitore.
  	<br>L'ordine verrà poi impostato in stato Inviato e non sarà possibile alcuna ulteriore modifica.
  </p>
</div>

<div id="nso-dialog-verification" title="Verifica Ordine NSO" style="display:none">
  <p id="nso-dialog-verification-content">
  	
  </p>
</div>
<div id="nso-dialog-revocation" title="Revoca Ordine NSO" style="display:none">
  <p id="nso-dialog-revocation-content">
  	Vuoi revocare l&#39;ordine?<br>Questa operazione non si pu&ograve; annullare.
  </p>
</div>
<c:set var="genere" value="40" />
<gene:javaScript>
		function leggiComunicazioni() {
			var href = contextPath + "/ApriPagina.do?href=geneweb/w_invcom/w_invcom-in-lista.jsp&genere=${genere}&chiave=" + document.forms[0].key.value;
			href+="&" + csrfToken;
			href+="&COD_ORD=" + document.forms[0].NSO_ORDINI_CODORD.value;
			document.location.href = href;
		}
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.InviaComunicazioni")}'>
		function inviaComunicazioni() {
			var entitaWSDM="NSO_ORDINI";
			var chiaveWSDM=getValue("NSO_ORDINI_ID");
			var idconfi=idconfi;
			var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=geneweb/w_invcom/w_invcom-lista.jsp&genere=${genere}&entita=" + document.forms[0].entita.value + "&chiave=" + document.forms[0].key.value;
			href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM + "&idconfi=" + idconfi;
			href+="&COD_ORD=" + document.forms[0].NSO_ORDINI_CODORD.value;
			document.location.href = href;
		}
	</c:if>
		
	<c:if test="${autorizzatoModifiche ne '2' and gene:checkProt(pageContext,'FUNZ.VIS.INS.GENEWEB.W_INVCOM-lista.LISTANUOVO')}">
		function nuovaComunicazione() {
			var IsW_CONFCOMPopolata = "${IsW_CONFCOMPopolata}";
			var tipo = "${genere}";
			var numeroGara = getValue("NSO_ORDINI_ID");
			var keyAdd = "W_INVCOM.COMKEY1=T:" + numeroGara;
			var keyParent = "NSO_ORDINI.ID=T:" + numeroGara;
			var ditta="${requestScope.initDITTA}";
			<%-- <c:choose>
				<c:when test='${garaLottoUnico}'>
					var entitaWSDM="GARE";
					var chiaveWSDM=getValue("GARE_NGARA");
				</c:when>
				<c:otherwise>
					var entitaWSDM="TORN";
					var chiaveWSDM=getValue("GARE_CODGAR1");
				</c:otherwise>
			</c:choose> --%>
			var href = "";
			if (IsW_CONFCOMPopolata == "true") {
				href = contextPath + "/pg/InitNuovaComunicazione.do?genere=" + tipo + "&keyAdd=" + keyAdd+"&keyParent=" + keyParent;// + "&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM;
			} else {
				href = contextPath + "/Lista.do?numModello=0&keyAdd=" + keyAdd ;
				href += "&keyParent=" + keyParent + "&metodo=nuovo&entita=W_INVCOM&jspPathTo=/WEB-INF/geneweb/w_invcom/w_invcom-scheda.jsp"//&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM;
			}
			/* if(idconfi){
				href = href + "&idconfi="+idconfi;
			} */
			href+="&ditta="+ditta;
			document.location.href = href + "&" + csrfToken;
		}
	</c:if>
</gene:javaScript>

		
