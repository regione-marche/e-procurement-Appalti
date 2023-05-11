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



<c:set var="id" value='${gene:getValCampo(key, "G1STIPULA.ID")}' />
<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneDatiStipulaFunction", pageContext,id)}' />
<c:set var="idconfi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetWSDMConfiAttivaFunction",  pageContext,uffintStipula,sessionScope.moduloAttivo)}' scope="request"/>

<gene:formPagine gestisciProtezioni="true">
	<gene:pagina title="Dati generali" idProtezioni="DATIGEN" visibile='true'>
		<jsp:include page="g1stipula-pg-datigen.jsp" />
	</gene:pagina>
	<gene:pagina title="Lotti aggiudicati" idProtezioni="LOTTIAGGSCHEDA" visibile='true'>
		<jsp:include page="g1stipula-pg-lottiAggScheda.jsp" />
	</gene:pagina>
	<gene:pagina title="Documentazione contratto" idProtezioni="DOCSTIP" visibile='true'>
		<jsp:include page="g1stipula-pg-documenti.jsp" />
	</gene:pagina>
	<gene:pagina title="Approvazioni contratto" idProtezioni="APPRSTIP" visibile='true'>
		<jsp:include page="g1stipula-pg-approvazioni.jsp" />
	</gene:pagina>
</gene:formPagine>