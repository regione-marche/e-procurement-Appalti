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
<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="G1STIPULA-scheda">

<c:if test='${!empty param.forzaModo}'>
	<c:set var="modo" value="NUOVO" scope="request"/>
</c:if>

<c:if test='${fn:contains(key,"V_GARE_STIPULA")}'>
	<c:set var="key" value='${fn:replace(key,"V_GARE_STIPULA","G1STIPULA")}' scope="request"/>
</c:if>
<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "G1STIPULA")}' />
	<gene:redefineInsert name="corpo">
	  	<jsp:include page="g1stipula-pagine-scheda.jsp" />
	</gene:redefineInsert>
</gene:template>