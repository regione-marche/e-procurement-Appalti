<%/*
       * Created on 29-06-2015
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

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="COMMALBO-scheda">
	<c:set var="idAlbo" value='${gene:getValCampo(key,"ID")}'/>
	<gene:setString name="titoloMaschera" value='Elenco componenti commissione' />
	<gene:redefineInsert name="corpo">
		<gene:formPagine gestisciProtezioni="true" >
			<gene:pagina title="Dati generali" idProtezioni="DATIGEN">
				<jsp:include page="commalbo-pg-datigen.jsp" />
			</gene:pagina>
			<gene:pagina title="Lista nominativi" idProtezioni="NOMINATIVI">
				<jsp:include page="commalbo-pg-listaNominativi.jsp" />
			</gene:pagina>
		</gene:formPagine>
	</gene:redefineInsert>
</gene:template>


