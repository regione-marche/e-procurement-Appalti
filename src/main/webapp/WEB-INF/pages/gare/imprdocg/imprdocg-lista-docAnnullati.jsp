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

<c:choose>
	<c:when test='${not empty param.comunicazioniVis}'>
		<c:set var="comunicazioniVis" value="${param.comunicazioniVis}" />
	</c:when>
	<c:otherwise>
		<c:set var="comunicazioniVis" value="${comunicazioniVis}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.genereGara}'>
		<c:set var="genereGara" value="${param.genereGara}" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="genereGara" value="${genereGara}" scope="request"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipo}'>
		<c:set var="tipo" value="${param.tipo}" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="tipo" value="${tipo}" scope="request"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.aut}'>
		<c:set var="aut" value="${param.aut}" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="aut" value="${aut}" scope="request"/>
	</c:otherwise>
</c:choose>

<c:set var="uffintGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetUffintGaraFunction",  pageContext,codiceGara)}' scope="request"/>
<c:set var="idconfi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetWSDMConfiAttivaFunction",  pageContext,uffintGara,sessionScope.moduloAttivo)}' scope="request"/>
<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codiceGara,idconfi)}' scope="request"/>
	
<c:set var="modalita" value="scheda" scope="request"/>

<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext, numeroGara, codiceGara, codiceDitta)}' />

<c:set var="titolo" value="Documenti annullati della ditta ${nomimo}" />

<c:set var="riceviComunicazioni" value="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.GARE.RiceviComunicazioni')}" scope="request"/>
<c:set var="inviaComunicazioni" value="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.GARE.InviaComunicazioni')}" scope="request"/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="false">
	<gene:setString name="titoloMaschera" value='${titolo}' />
	<gene:redefineInsert name="addHistory">
		<gene:historyAdd titolo="${titolo}" id="docAnn" />
	</gene:redefineInsert>	
	<gene:redefineInsert name="corpo">
		<jsp:include page="imprdocg-pg-documenti.jsp">
			<jsp:param value="${nomimo}" name="nomimo"/>
			<jsp:param name="listaDocAnnullati" value="true"/>
		</jsp:include>
	</gene:redefineInsert>
</gene:template>