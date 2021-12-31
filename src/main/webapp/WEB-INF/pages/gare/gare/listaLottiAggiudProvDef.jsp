<%
/*
 * Created on: 19-nov-2009
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

<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestionePagineAggProvvDefOffertaUnicaFunction",  pageContext, key,"AggProv")}'/>
<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ImportExportZoo")}'>
	<c:set var="tipoFornitura" value="98" />
 </c:if>
<c:set var="bustalotti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetBustalottiFunction", pageContext, key)}' />
<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<jsp:include page="/WEB-INF/pages/gare/gare/bloccaModifica-fasiGara.jsp" >
	<jsp:param name="entita" value="V_GARE_TORN" />
	<jsp:param name="inputFiltro" value="${inputFiltro}" />
	<jsp:param name="filtroCampoEntita" value="codgar = #CODGAR#" />
	<jsp:param name="updateLista" value="${updateLista}" />
</jsp:include>

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<table class="arealayout">
				<tr>
					<td>
						<br><b>ATTENZIONE:</b>&nbsp;
						Il calcolo dell'aggiudicazione procede sui singoli lotti della gara elencati nella lista sottostante. Cliccare sul 'Codice lotto' per accedere al calcolo di ogni lotto.
					</td>
				</tr>
			</table>
		</td>
	</tr>
		
	<jsp:include page="/WEB-INF/pages/gare/gare/lista-lotti-aggiudicazione.jsp" >
		<jsp:param name="tipoAggiudicazione" value="provvisoria"/>
	</jsp:include>


</table>

<gene:javaScript>

	function calcoloAggiudicazioneLotto(){
		document.location.href=contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/gare/gare-pg-aggiudProvDefLotti.jsp&key="+chiaveRiga + "&bustalotti=${bustalotti}&paginaFasiGara=aggiudProvDefOffertaUnica&idconfi=${idconfi}";
		
	}

	function apriPopupAnnullaAggiudicazione(){
		var codgar = '${fn:substringAfter(key,":")}';
		var bustalotti = '${bustalotti }';
		openPopUpCustom("href=gare/gare/gare-popup-annulla-calcoloAggiudicazione-offertaUnica.jsp&codgar=" + codgar + "&bustalotti=" + bustalotti, "annullaCalcoloAgg", 700, 350, "yes","yes");
	}
	
	
	function inserisciOffertaDitteEscluse(){
		var modlicg="";
		var codiceGara = '${fn:substringAfter(key,":")}';
		var ditta="";
		var ngara = codiceGara;
		var chiave = "DITG.CODGAR5=T:" + codiceGara + ";";
		chiave += "DITG.DITTAO=T:" + ditta + ";";
		chiave += "DITG.NGARA5=T:" + ngara;
				
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/ditg/inserimentoOfferteDitteEscluse-lista.jsp";
		href += "&key=" + chiave;
		href += "&modalitaGara=" + modlicg;
		href += "&isGaraLottiConOffertaUnica=true";
		document.location.href = href;
	}
	
	function apriPopupCalcoloAggiudicazioneLotti(){
		var codiceGara = '${fn:substringAfter(key,":")}';
		openPopUpCustom("href=gare/gare/gare-popup-calcoloAggiudicazione-tuttiLotti.jsp&ngara="+codiceGara, "calcoloAggTuttiLotti", 700, 400, "yes","yes");
		
	}
		
	
</gene:javaScript>