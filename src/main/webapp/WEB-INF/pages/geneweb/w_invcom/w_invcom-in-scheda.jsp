<%
	/*
	 * Created on 22-Lug-2015
	 *
	 * Copyright (c) Maggioli S.p.A. - Divisione EldaSoft
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

<c:set var="genere" value='${param.genere}' scope="request"/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="W_INVCOM-IN-scheda" schema="GENEWEB">
	<c:choose>
		<c:when test="${empty keyParentComunicazioni && !empty keyParent}">
			<%-- forzato il keyParentComunicazioni per il caso in cui il dettaglio della comunicazione viene aperto dalla lista delle comunicazioni non lette. 
			In questo caso il keyParent viene 'calcolato' via js --%>
			<c:set var="keyParentComunicazioni" value='${keyParent}' scope="session" />
		</c:when>
	</c:choose>

	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleComunicazioneFunction", pageContext, "W_INVCOM_IN")}' />
	
	<gene:redefineInsert name="modelliPredisposti" />
	<gene:redefineInsert name="documentiAssociati" />
	<gene:redefineInsert name="noteAvvisi" />
		
	<gene:redefineInsert name="corpo">
		<gene:formPagine gestisciProtezioni="true">
			<gene:pagina title="Dati generali" idProtezioni="W_INVCOM">
				<jsp:include page="w_invcom-in-pg-datigen.jsp" />
			</gene:pagina>
			<gene:pagina title="Allegati" idProtezioni="W_DOCDIG">
				<jsp:include page="w_invcom-in-pg-lista-w_docdig.jsp" />
			</gene:pagina>	
		</gene:formPagine>
	</gene:redefineInsert>

</gene:template>
