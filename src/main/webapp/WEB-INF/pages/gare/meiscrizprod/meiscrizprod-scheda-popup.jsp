<%
/*
 * Created on: 06/12/2013
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="MEISCRIZPROD-scheda">
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "MEISCRIZPROD")}'/>
	
	
	<gene:redefineInsert name="corpo">
		<gene:redefineInsert name="head">
                <script type="text/javascript" src="${contextPath}/js/date.js"></script>
         </gene:redefineInsert>
		<gene:formScheda entita="MEISCRIZPROD" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInizializzazioniMEISCRIZPROD" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreMEISCRIZPROD">
			
			<jsp:include page="/WEB-INF/pages/gare/meiscrizprod/meiscrizprod-interno-scheda.jsp"/>
				
			
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${datiRiga.MEISCRIZPROD_CODGAR}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
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
							&nbsp;<INPUT type="button"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">&nbsp;
							<INPUT type="button" class="bottone-azione" value="Torna alla lista" title="Torna alla lista" onclick="javascript:historyVaiA(0);">
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>
	<gene:javaScript>
		
		document.forms[0].encoding="multipart/form-data";
				
		function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
				var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
				document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
			}
			
		function chiudi(){
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();
		}
		
		<c:if test="${modo eq 'VISUALIZZA'}">
			$('[id^="rowtitoloMEALLARTCAT_"]').hide();
			$('[id^="rowtitoloIMMAGINE_"]').hide();
			$('[id^="rowtitoloCERTIFICAZIONI_"]').hide();
			$('[id^="rowtitoloSCHEDE_"]').hide();
		</c:if>
		
	</gene:javaScript>
</gene:template>