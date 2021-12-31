<%/*
       * Created on 02-Dec-2013
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

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="MEARTCAT-scheda">
	<c:set var="idArticolo" value='${gene:getValCampo(key,"ID")}'/>
	<c:set var="stato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetStatoMeartcatFunction", pageContext, idArticolo)}' />
	<c:if test="${! empty param.listachiamante}">
		<c:set var="listachiamante" value='${param.listachiamante}' scope="session" />
	</c:if>
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction",pageContext,"MEARTCAT")}' />
	<gene:redefineInsert name="corpo">
		<gene:formPagine gestisciProtezioni="true" >
			<gene:pagina title="Dati generali" idProtezioni="DATIGEN">
				<jsp:include page="meartcat-datigen.jsp" />
			</gene:pagina>
			<gene:pagina title="Prodotti degli operatori economici" idProtezioni="PRODOTTI" visibile="${stato ne 1 }">
				<jsp:include page="meartcat-pg-listaProdotti.jsp" />
			</gene:pagina>
		</gene:formPagine>
	</gene:redefineInsert>
</gene:template>


