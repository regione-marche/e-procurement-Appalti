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
		
		<gene:campoScheda>
			<td colspan="2"><b>Dati dell'ordine</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="ID" visibile="false"   />
		<gene:campoScheda campo="CODORD" modificabile="false" visibile="true" />
		<gene:campoScheda campo="STATO_ORDINE" modificabile="false" visibile="true" />
		
		<gene:campoScheda>
			<td colspan="2"><b>Importi dell'ordine</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="IMPORTO_ORDINATO" title="Importo ordinato" campoFittizio="true"
		definizione="F24.5;0;;MONEY5;NSO_LO_PU" modificabile="false" value="${requestScope.impOrdinato}"/>

		<gene:campoScheda campo="IMPORTO_IVA" title="IVA" campoFittizio="true"
		definizione="F24.5;0;;MONEY5;NSO_LO_PU" modificabile="false" value="${requestScope.impIva}"/>

		<gene:campoScheda campo="IMPORTO_TOTALE" title="Importo totale" campoFittizio="true"
		definizione="F24.5;0;;MONEY5;NSO_LO_PU" modificabile="false" value="${requestScope.impTotale}"/>

		<gene:campoScheda campo="ARROTONDAMENTO"  />	
		
		<gene:campoScheda campo="IMP_TOTALE_DA_PAGARE" title="Totale da pagare" campoFittizio="true" definizione="F24.5;0;;MONEY5;NSO_LO_PU" modificabile="false" value=""/>
	
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
				<%-- <gene:insert name="pulsanteConfermaCompletato">
					<c:if test='${requestScope.statoOrdine eq 1}'>
					<INPUT type="button" class="bottone-azione" value="Conferma ordine completato" title="Conferma ordine completato" onclick="javascript:confermaOrdineCompletato()">
					</c:if>
				</gene:insert> --%>
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>


<gene:fnJavaScriptScheda funzione='calcolaTotDaPag()' elencocampi='IMPORTO_TOTALE;NSO_ORDINI_ARROTONDAMENTO' esegui="true" />		

	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	

	
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

	$(document).ready(function(){
	

	});
	
	function confermaOrdineCompletato() {
		var idOrdine = getValue("NSO_ORDINI_ID");
		var statoOrdine = "2";
		var comando = "href=gare/nso_ordini/popup-aggiorna-stato-ordine.jsp&idOrdine=" + idOrdine + "&statoOrdine=" +statoOrdine;
		openPopUpCustom(comando, "aggiornaStatoOrdine", 550, 300, "yes", "yes");
	}
	
	function calcolaTotDaPag(){
		var importoTotale = getValue("IMPORTO_TOTALE");
		var impArrotondamento = getValue("NSO_ORDINI_ARROTONDAMENTO");
		var modo="${modo}";
		
		if (importoTotale == null || importoTotale == "" || impArrotondamento == null || impArrotondamento == ""){
			setValue("IMP_TOTALE_DA_PAGARE",  "");
		}else {
			importoTotale = parseFloat(importoTotale);
			impArrotondamento = parseFloat(impArrotondamento);
			
			var temp = importoTotale + impArrotondamento;
			temp = round(eval(temp), 2);
			
			if(modo=="VISUALIZZA"){
				temp=""+temp;
				temp=temp.replace('.',',');
				temp =  temp + " &euro;&nbsp;&nbsp;";
			}else{
				temp =  temp;
			}

			
			
			setValue("IMP_TOTALE_DA_PAGARE", temp);
		}
		
		
		return true;
	}
	


</gene:javaScript>

	