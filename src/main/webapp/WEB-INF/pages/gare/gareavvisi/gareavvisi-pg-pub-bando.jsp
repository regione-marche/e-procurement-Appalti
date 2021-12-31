<%
/*
 * Created on: 24/05/2012
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

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>
</gene:redefineInsert>

<c:set var="numeroGara" value='${gene:getValCampo(key, "NGARA")}'/>
<c:set var="dollaro" value="$"/>
<c:set var="codiceGara" value="${dollaro }${numeroGara }"/>

<c:set var="bloccoPubblicazionePortale" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codiceGara,"BANDO","false")}' />
<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codiceGara, idconfi)}'/>
<c:set var="tipoPubSitoIstituzionale" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", tipoPubblicazioneSitoIstituzionale)}'/>

<c:if test="${integrazioneWSDM =='1'}">
	<c:set var="esisteFascicoloAssociato" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteFascicoloAssociatoFunction", pageContext, "GARE", numeroGara,idconfi)}' scope="request"/>
</c:if>

<%/* Altri dati generali della gara */%>
<gene:formScheda entita="GAREAVVISI" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAvvisi">


	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	
	<gene:campoScheda campo="CODGAR" visibile="false" />
	<gene:campoScheda campo="NGARA" visibile="false"/>
	
	<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestionePubblicazioniBandoFunction" parametro='${codiceGara}' />
	
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='PUBBLI'/>
		<jsp:param name="chiave" value='${codiceGara}'/>
		<jsp:param name="nomeAttributoLista" value='pubblicazioniBando' />
		<jsp:param name="idProtezioni" value="PUBBANDO" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/pubbli/pubblicazione-bando.jsp"/>
		<jsp:param name="arrayCampi" value="'PUBBLI_CODGAR9_', 'PUBBLI_NUMPUB_', 'PUBBLI_TIPPUB_', 'PUBBLI_TESPUB_', 'PUBBLI_NPRPUB_', 'PUBBLI_DINPUB_', 'PUBBLI_DATPUB_', 'PUBBLI_IMPPUB_', 'PUBBLI_INTPUB_', 'PUBBLI_DATFIPUB_'"/>		
		<jsp:param name="titoloSezione" value="Pubblicazione" />
		<jsp:param name="titoloNuovaSezione" value="Nuova pubblicazione" />
		<jsp:param name="descEntitaVociLink" value="pubblicazione" />
		<jsp:param name="msgRaggiuntoMax" value="e pubblicazioni"/>
		<jsp:param name="funzEliminazione" value="delPubblicazioniBando"/>
	</jsp:include>

	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="GAREAVVISI"/>
			<jsp:param name="inputFiltro" value="CODGAR=T:${datiRiga.GAREAVVISI_CODGAR}"/>
			<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
		</jsp:include>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>

</gene:formScheda>
<gene:javaScript>


//Apertura popup per la pubblicazione su portale Appalti
function pubblicaSuPortale() {
		
	var href = "href=gare/commons/popupPubblicaSuPortale.jsp?codgar="+getValue("GAREAVVISI_CODGAR")+"&ngara="+getValue("GAREAVVISI_NGARA")+"&bando=1&garavviso=1";
	var dim1 =600;
	var dim2=350;
	<c:if test="${integrazioneWSDM =='1'}">
		<c:if test="${esisteFascicoloAssociato ne true }">
			dim1=800;
			dim2=570;
		</c:if>
		
		var entita="GARE";
		href += "&entita=" + entita + "&garaElencoCatalogo=0";
		
	</c:if>
	<c:if test="${tipoPubSitoIstituzionale eq '1'}">
		dim=800;
	</c:if>
	openPopUpCustom(href, "pubblicaSuPortale", dim1, dim2, "no", "yes");
}

<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AutovieArchiflow")}'>
		addHrefs();
	</c:if>

</gene:javaScript>
