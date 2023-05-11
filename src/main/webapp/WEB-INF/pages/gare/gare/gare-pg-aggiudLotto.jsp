<%
/*
 * Created on: 27-05-2011
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

<c:set var="isGaraLottiConOffertaUnica" value= "true" scope="request"/>

<c:set var="codiceLotto" value='${gene:getValCampo(key, "GARE.NGARA")}'/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GARE-AGGIUDLOTTI">
	<% // Settaggio delle stringhe utilizzate nel template %>
	<gene:setString name="titoloMaschera" value='Aggiudicazione del lotto ${codiceLotto}' />
	<gene:redefineInsert name="corpo">
			<jsp:include page="gare-pg-aggiudicazione-definitiva.jsp">
				<jsp:param name="lottoDiGara" value="${lottoDiGara}" />
			</jsp:include>
	</gene:redefineInsert>
</gene:template>
