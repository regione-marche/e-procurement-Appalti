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
	
		<gene:gruppoCampi idProtezioni="INTFATT" >
		<gene:campoScheda>
			<td colspan="2"><b>Intestatario della fattura</b></td>
		</gene:campoScheda>
		<gene:archivio titolo="Uffici intestatari"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT"),"gene/uffint/uffint-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda-popup.jsp","")}'
			 campi="UFFINT.CODEIN;UFFINT.NOMEIN;UFFINT.VIAEIN;UFFINT.NCIEIN;UFFINT.CITEIN;UFFINT.PROEIN;UFFINT.CAPEIN;UFFINT.CODNAZ"
			 chiave="NSO_ORDINI_CODEIN_FATTURA" >
				<gene:campoScheda campo="CODEIN_FATTURA" title="Intestatario della fattura" defaultValue="${requestScope.initCENINT}" />
				<gene:campoScheda campo="NOMEIN" title="Denominazione" entita="UFFINT" where="NSO_ORDINI.CODEIN_FATTURA=UFFINT.CODEIN" defaultValue="${requestScope.initNOMEIN}" />
				<gene:campoScheda campo="VIAEIN" title="Via" entita="UFFINT" where="NSO_ORDINI.CODEIN_FATTURA=UFFINT.CODEIN"  />
				<gene:campoScheda campo="NCIEIN" title="Numero Civico" entita="UFFINT" where="NSO_ORDINI.CODEIN_FATTURA=UFFINT.CODEIN"  />
				<gene:campoScheda campo="CITEIN" title="Città" entita="UFFINT" where="NSO_ORDINI.CODEIN_FATTURA=UFFINT.CODEIN"  />
				<gene:campoScheda campo="PROEIN" title="Provincia" entita="UFFINT" where="NSO_ORDINI.CODEIN_FATTURA=UFFINT.CODEIN"  />
				<gene:campoScheda campo="CAPEIN" title="CAP" entita="UFFINT" where="NSO_ORDINI.CODEIN_FATTURA=UFFINT.CODEIN"  />
				<gene:campoScheda campo="CODNAZ" title="Nazione" entita="UFFINT" where="NSO_ORDINI.CODEIN_FATTURA=UFFINT.CODEIN"  />
		</gene:archivio>
		<gene:campoScheda campo="UFFICIO_FATTURA" title="Ufficio"/>
		</gene:gruppoCampi>
		
		<gene:campoScheda>
			<td colspan="2"><b>Condizioni di pagamento</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="CONDIZIONI_PAGAMENTO"  />	
	
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
	

	
</gene:formScheda>

<div id="nso-dialog-verification" title="Verifica Ordine NSO" style="display:none">
  <p id="nso-dialog-verification-content">
  	
  </p>
</div>

<gene:javaScript>

	$(document).ready(function(){
	

	});


</gene:javaScript>

	