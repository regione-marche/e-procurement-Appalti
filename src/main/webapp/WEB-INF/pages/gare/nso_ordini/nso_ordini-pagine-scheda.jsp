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



<c:set var="id" value='${gene:getValCampo(key, "NSO_ORDINI.ID")}' />
<c:set var="ditta" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDittaFunction", pageContext, ngara)}' />
<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneDatiNsoFunction", pageContext, id)}'/>
<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, gene:getValCampo(key,"NGARA"))}' />
<c:set var="uffintGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetUffintGaraFunction",  pageContext,codgar)}' scope="request"/>
<c:set var="idconfi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetWSDMConfiAttivaFunction",  pageContext,uffintGara,sessionScope.moduloAttivo)}' scope="request"/>
<gene:formPagine gestisciProtezioni="true">

		<gene:pagina title="Dati generali" idProtezioni="DATIGEN" visibile='true'>
			<jsp:include page="nso_ordini-pg-datigen.jsp" />
		</gene:pagina>
		<gene:pagina title="Allegati" idProtezioni="ALLEGO" visibile='true'>
			<jsp:include page="nso_ordini-pg-allegati.jsp" />
		</gene:pagina>
		<gene:pagina title="Parti coinvolte" idProtezioni="PARTICO" visibile='true'>
			<jsp:include page="nso_ordini-pg-particoinvolte.jsp" />
		</gene:pagina>
		<gene:pagina title="Consegne" idProtezioni="CONS" visibile='true'>
			<jsp:include page="nso_ordini-pg-consegne.jsp" />
		</gene:pagina>
		<gene:pagina title="Fatturazione" idProtezioni="FATT" visibile='true'>
			<jsp:include page="nso_ordini-pg-fatturazione.jsp" />
		</gene:pagina>
		<gene:pagina title="Linee ordine" idProtezioni="LINEEO" visibile='true'>
			<jsp:include page="nso_ordini-pg-linee.jsp" />
		</gene:pagina>
		<gene:pagina title="Riepilogo importi" idProtezioni="RIMP" visibile='true'>
			<jsp:include page="nso_ordini-pg-riepilogo-importi.jsp" />
		</gene:pagina>
		<c:if test='${!empty requestScope.codiceOrdineCollegato}'>
			<gene:pagina title="Storico ordine" idProtezioni="STORIA" visibile='true'>
				<jsp:include page="nso_ordini-pg-storico.jsp" />
			</gene:pagina>
		</c:if>
		<c:if test='${requestScope.statoOrdine ge 4}'>
			<gene:pagina title="XML inviato" idProtezioni="XMLINV" visibile='true'>
				<jsp:include page="nso_ordini-pg-xml-inviato.jsp" />
			</gene:pagina>
		</c:if>
</gene:formPagine>