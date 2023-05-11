<%/*
       * Created on 18-ott-2007
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
	<c:when test='${modo eq "NUOVO" and param.tipoGara eq "garaDivisaLotti"}' >
		<c:set var="tipologiaGara" value='1' scope="request"/>
	</c:when>
	<c:when test='${modo eq "NUOVO" and param.tipoGara eq "garaDivisaLottiOffUnica"}' >
		<c:set var="tipologiaGara" value='3' scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="tipologiaGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,key)}' scope="request"/>
	</c:otherwise>
</c:choose>


<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera='${gene:if(tipologiaGara == "1","TORN-scheda","TORN-OFFUNICA-scheda")}'> 
	<c:set var="key" value='TORN.CODGAR=T:${gene:getValCampo(key,"CODGAR")}' scope="request" />
	
	
	
	<c:choose>
		<c:when test='${tipologiaGara eq "1"}' >
			<gene:setString name="titoloMaschera" 
				value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction",pageContext,"TORN")}'/>
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" 
		value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction",pageContext,"TORN_GARE")}'/>
		</c:otherwise>
	</c:choose>
	
	
	<gene:redefineInsert name="corpo">
	  	<jsp:include page="torn-pagine-scheda.jsp" />
	</gene:redefineInsert>
</gene:template>
