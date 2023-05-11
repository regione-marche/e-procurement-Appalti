<%/*
   * Created on 17-ott-2007
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

<c:set var="codiceGara" value='${gene:getValCampo(key, "DITG.CODGAR5")}' />
<c:set var="numeroGara" value='${gene:getValCampo(key, "DITG.NGARA5")}' />
<c:set var="codiceDitta" value='${gene:getValCampo(key, "DITG.DITTAO")}' />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="digitalSignatureUrlCheck" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-check-url")}'/>
<c:set var="digitalSignatureProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:choose>
	<c:when test="${!empty digitalSignatureUrlCheck && !empty digitalSignatureProvider && (digitalSignatureProvider eq 1 || digitalSignatureProvider eq 2)}">
		<c:set var="digitalSignatureWsCheck" value='1'/>
	</c:when>
	<c:otherwise>
		<c:set var="digitalSignatureWsCheck" value='0'/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.stepWizard}'>
		<c:set var="stepWizard" value="${param.stepWizard}" />
	</c:when>
	<c:otherwise>
		<c:set var="stepWizard" value="${stepWizard}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.genereGara}'>
		<c:set var="genereGara" value="${param.genereGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="genereGara" value="${genereGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.comunicazioniVis}'>
		<c:set var="comunicazioniVis" value="${param.comunicazioniVis}" />
	</c:when>
	<c:otherwise>
		<c:set var="comunicazioniVis" value="${comunicazioniVis}" />
	</c:otherwise>
</c:choose>

<c:if test="${empty genereGara }">
	<c:set var="codgar" value='CODGAR:${codiceGara}' />
	<c:set var="genereGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction", pageContext, codgar)}' />
</c:if>

<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext, numeroGara, codiceGara, codiceDitta)}' />

<table class="dettaglio-tab-lista">
<tr>
	
	<gene:redefineInsert name="documentiAssociati"/>
	<gene:redefineInsert name="noteAvvisi" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${contextPath}/js/jquery.comunicazioni.ditta.js?v=${sessionScope.versioneModuloAttivo}"></script>
		<script type="text/javascript" src="${contextPath}/js/common-gare.js"></script>	
		
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/comunicazioniditta/jquery.comunicazioni.ditta.css" >
				
	</gene:redefineInsert>
	
	
		<input type="hidden" id="ngara" name="ngara" value="${numeroGara}" />
		<input type="hidden" id="codiceGara" name="codiceGara" value="${codiceGara}" />
		<input type="hidden" id="ditta" name="ditta" value="${codiceDitta}" />
		<input type="hidden" id="genereGara" name="genereGara" value="${genereGara}" />
		<input type="hidden" id="percorsoCartellaImgTabella" name="percorsoCartellaImgTabella" value="${contextPath}/img/" />
		<input type="hidden" id="contextPath" name="contextPath" value="${contextPath}" />
		<input type="hidden" id="riceviComunicazioni" name="riceviComunicazioni" value="${riceviComunicazioni}" />
		<input type="hidden" name="comunicazioniVis" id="comunicazioniVis" value="${comunicazioniVis }" />
		<input type="hidden" name="soccorsoIstruttorio" id="soccorsoIstruttorio" value="0" />
		<input type="hidden" name="stepWizard" id="stepWizard" value="${stepWizard }" />
		<input type="hidden" id="digitalSignatureWsCheck" name="digitalSignatureWsCheck" value="${digitalSignatureWsCheck}" />
		
		<table class="lista" style="height: 100%">
			<tr>
				<td colSpan="2">
					<div id="comunicazioniContainer" >
					</div>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<c:choose>
						<c:when test="${genereGara eq 10 or genereGara eq 20}">
							<c:set var="testoIndietro" value="Torna a elenco operatori" />
						</c:when>
						<c:when test="${stepWizard eq '8' }">
							<c:set var="testoIndietro" value="Torna al dettaglio aggiudicazione" />
						</c:when>
						<c:otherwise>
							<c:set var="testoIndietro" value="Torna a elenco ditte" />
						</c:otherwise>
					</c:choose>
					<INPUT type="button"  class="bottone-azione" value='${testoIndietro }' title='${testoIndietro }' onclick="javascript:historyVaiIndietroDi(1);">
					&nbsp;
				</td>
			</tr>
			
		</table>
	
	
	<gene:javaScript>
	
	//Per IE non viene visualizzata la barra blu verticale sotto i tab, poichè la proprietà "border-collapse=collapse"
	//impostata nella classe "arealayout" agisce pure su "dettaglio-tab-lista". Si rimuove quindi tale attributo
	$(".dettaglio-tab-lista").css("border-collapse","separate");
	
	var integrazioneWSDM = "${integrazioneWSDM}";
	
	var selezionaPaginaDefault = selezionaPagina;
	var selezionaPagina = selezionaPaginaCustom;
	function selezionaPaginaCustom(pageNumber){
		if(document.pagineForm.action.indexOf("stepWizard")<0){
			var stepWizard="${stepWizard }";
			var genereGara="${genereGara }";
			var tipo="${tipo }";
			var comunicazioniVis = "${comunicazioniVis }";
			var aut="${aut}";
			document.pagineForm.action += "&stepWizard=" + stepWizard + "&genereGara=" + genereGara + "&tipo=" + tipo + "&comunicazioniVis=" + comunicazioniVis + "&aut=" + aut;
		}
		selezionaPaginaDefault(pageNumber);
	}
	
	function chiudi(){
		window.close();
	}
	
	</gene:javaScript>
	

</tr>
	</table>
	
	<jsp:include page="popupModale-copiaDocumenti.jsp"/>