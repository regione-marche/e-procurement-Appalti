<%/*
   * Created on 04-09-2013
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
<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="ANCORLOTTI-scheda">
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "ANTICORLOTTI")}' />
	<gene:redefineInsert name="corpo">
	  	<gene:formPagine gestisciProtezioni="true">
	  	<gene:pagina title="Dati generali" idProtezioni="DATIGEN" >
			<jsp:include page="anticorlotti-pg-datigen.jsp" />
		</gene:pagina>
		<gene:pagina title="Partecipanti" idProtezioni="PARTECIPANTI" >
			<jsp:include page="anticorlotti-pg-partecipanti.jsp" >
				<jsp:param name="tipo" value="1"/>
			</jsp:include>
		</gene:pagina>
		</gene:formPagine>
	</gene:redefineInsert>
</gene:template>