<%/*
       * Created on 09-mar-2015
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

<c:choose>
	<c:when test="${!empty param.idconfi}">
		<c:set var="idconfi" value="${param.idconfi}"/>
	</c:when>
	<c:otherwise>
		<c:set var="idconfi" value="${idconfi}"/>
	</c:otherwise>
</c:choose>

<c:set var="urlFascicolo" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", "wsdm.fascicoloprotocollo.url",idconfi)}'/>
<c:set var="urlDocumentale" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetPropertyWsdmFunction", "wsdm.documentale.url",idconfi)}'/>

<c:choose>
	<c:when test="${!empty urlFascicolo and urlFascicolo!=''}">
		<c:set var="servizio" value="FASCICOLOPROTOCOLLO"/>
	</c:when>
	<c:otherwise>
		<c:set var="servizio" value="DOCUMENTALE"/>
	</c:otherwise>
</c:choose>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="WSDM-scheda">
	<c:choose>
		<c:when test="${param.entita eq 'G1STIPULA'}">
			<gene:setString name="titoloMaschera" value="Fascicolo documentale della stipula ${param.codstipula}" />
		</c:when>
		<c:when test="${param.genereGara eq '-2'}">
			<gene:setString name="titoloMaschera" value="Fascicolo documentale dell'appalto ${param.key2} della commessa ${param.key1} " />
		</c:when>
		<c:when test="${param.genereGara eq '-1'}">
			<gene:setString name="titoloMaschera" value="Fascicolo documentale della commessa ${param.key1}" />
		</c:when>
		<c:when test="${param.genereGara eq '4'}">
			<gene:setString name="titoloMaschera" value="Fascicolo documentale dell'ordine ${param.key1}" />
		</c:when>
		<c:when test="${param.genereGara eq '10'}">
			<gene:setString name="titoloMaschera" value="Fascicolo documentale dell'elenco ${param.key1}" />
		</c:when>
		<c:when test="${param.genereGara eq '20'}">
			<gene:setString name="titoloMaschera" value="Fascicolo documentale del catalogo ${param.key1}" />
		</c:when>
		<c:when test="${param.genereGara eq '11'}">
			<gene:setString name="titoloMaschera" value="Fascicolo documentale dell'avviso ${param.key1}" />
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value="Fascicolo documentale della gara ${param.key1 }" />
		</c:otherwise>
	</c:choose>
	
	<gene:redefineInsert name="corpo">
		<jsp:include page="wsdm-fascicolo.jsp" >
			<jsp:param name="servizio" value="${servizio}"/>
			<jsp:param name="idconfi" value="${idconfi}"/>
		</jsp:include>
	</gene:redefineInsert>
</gene:template>
