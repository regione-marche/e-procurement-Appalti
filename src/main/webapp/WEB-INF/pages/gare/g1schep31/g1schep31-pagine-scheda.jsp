	<%/*
       * Created on 22-mag-2024
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

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="G1SCHEP31-scheda">

<c:set var="codiceGara" value="${gene:getValCampo(param.key, 'CODGAR')}"/>
<c:if test="${fn:startsWith(codiceGara,'$')}">
	<c:set var="codiceGara" value="${fn:substringAfter(codiceGara, '$')}"/>
</c:if>

<gene:setString name="titoloMaschera" value='Ridefinisci dati scheda P3_1 per la gara  ${codiceGara}' />
<gene:redefineInsert name="corpo">
<gene:formPagine gestisciProtezioni="true">
	<gene:pagina title="Termini gara" idProtezioni="G1SCHEP31TERMINI">
		<jsp:include page="g1schep31-terminiGara-scheda.jsp" />
	</gene:pagina>
	<gene:pagina title="Importo lotti" idProtezioni="G1SCHEP31IMPORTO">
		<jsp:include page="g1schep31-importoLotti-lista.jsp" />
	</gene:pagina>
	<input type="hidden" name="autorizzatoModifiche" value="${param.autorizzatoModifiche}"/>
</gene:formPagine>
</gene:redefineInsert>
</gene:template>