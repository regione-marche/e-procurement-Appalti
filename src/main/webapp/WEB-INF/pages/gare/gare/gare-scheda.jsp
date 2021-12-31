<%/*
   * Created on 15-lug-2008
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
<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GARE-scheda">

<c:choose>
	<c:when test='${modo eq "NUOVO" and param.tipoGara eq "garaLottoUnico" and empty param.garaPerElenco and empty param.garaPerCatalogo}' >
		<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "GARE")}' />
		<c:set var="garaLottoUnico" value='true' scope="request"/>
		<c:set var="genereGara" value='2' />
	</c:when>
	<c:when test='${modo eq "NUOVO" and param.tipoGara eq "garaLottoUnico" and not empty param.garaPerElenco and empty param.garaPerCatalogo}' >
		<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "GAREALBO")}' />
		<c:set var="garaLottoUnico" value='true' scope="request"/>
		<c:set var="genereGara" value='10' />
	</c:when>	
	<c:when test='${modo eq "NUOVO" and param.tipoGara eq "garaLottoUnico" and empty param.garaPerElenco and not empty param.garaPerCatalogo}' >
		<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "MECATALOGO")}' />
		<c:set var="garaLottoUnico" value='true' scope="request"/>
		<c:set var="genereGara" value='20' />
	</c:when>
	<c:when test='${modo eq "NUOVO" and param.tipoGara eq "garaDivisaLotti"}' >
		<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "TORN")}' />
		<c:set var="garaLottoUnico" value='false' scope="request"/>
	</c:when>
	<c:when test='${modo eq "NUOVO" and empty param.tipoGara}' >
		<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "LOTTI")}' />
		<c:set var="garaLottoUnico" value='false' scope="request"/>
		<%/*Si deve stabilire se il lotto è di una gara a lotti con offerte distinte */ %>
		<c:set var="genereGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,keyParent)}' scope="request"/>
	</c:when>
	<c:when test='${fn:contains(key,"V_GARE_NSCAD")}'>
		<%/*Nel caso di personalizzazione ASPI profilo protocollo la chiave è già ngara */ %>
		<c:set var="key" value='${fn:replace(key,"V_GARE_NSCAD","GARE")}' scope="request"/>
	</c:when>
	<c:otherwise>
		<%/* si deve stabilire se è una gara a lotto unico prima di inizializzare key*/ %>
		<c:set var="garaLottoUnico" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GaraLottoUnicoFunction", pageContext, key)}' scope="request"/>

		<c:if test='${gene:getValCampo(key,"CODGAR") != ""}'>
			<c:set var="key" value='GARE.CODGAR1=T:${gene:getValCampo(key,"CODGAR")}' scope="request" />
			<c:set var="key" value='GARE.NGARA=T:${fn:replace(fn:substringAfter(key, ":"), "$", "")}' scope="request" />
		</c:if>

		<c:set var="ngara" value='GARE.NGARA=T:${gene:getValCampo(key,"NGARA")}'/>
		<c:set var="genereGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,ngara)}' scope="request"/>

		<c:if test='${garaLottoUnico and genereGara ne "10"}' >
			<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "GARE")}' />
		</c:if>
		<c:if test='${garaLottoUnico and genereGara eq "10"}' >
			<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "GAREALBO")}' />
		</c:if>
		<c:if test='${garaLottoUnico and genereGara eq "20"}' >
			<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "MECATALOGO")}' />
		</c:if>
		<c:if test='${!garaLottoUnico}' >
			<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "LOTTI")}' />
		</c:if>
	</c:otherwise>
</c:choose>
<%/* se generaGara vale "3" allora il lotto appartiene ad una gara a lotti con offerta unica*/ %>
<c:choose>
	<c:when test='${genereGara eq "3"}'>
		<c:set var="lottoOffertaUnica" value="true" scope="request"/>
	</c:when>
	<c:when test='${genereGara eq "10"}'>
		<c:set var="lottoOffertaUnica" value="false" scope="request"/>
		<c:set var="garaPerElenco" value="true" scope="request"/>
	</c:when>
	<c:when test='${genereGara eq "20"}'>
		<c:set var="lottoOffertaUnica" value="false" scope="request"/>
		<c:set var="garaPerCatalogo" value="true" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="lottoOffertaUnica" value="false" scope="request"/>
	</c:otherwise>
</c:choose>
<c:if test='${not garaLottoUnico}'>
	<c:set var="lottoDiGara" value="true" scope="request"/>
</c:if>
	<gene:redefineInsert name="corpo">
	  	<jsp:include page="gare-pagine-scheda.jsp" />
	</gene:redefineInsert>
</gene:template>