<%/*
   * Created on 20-nov-2014
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI EDIT
  // DEL DETTAGLIO DI UN DOCUMENTO ASSOCIATO RELATIVA AI DATI EFFETTIVI
%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:choose>
	<c:when test='${param.detail eq "WSDM" }'>
		<c:set var="titolo" value="Configurazione propriet&agrave; per integrazione con sistema di protocollazione e gestione documentale" scope="request" />
	</c:when>
	<c:when test='${param.detail eq "WSERP" }'>
		<c:set var="titolo" value="Configurazione propriet&agrave; per integrazione con sistema ERP" scope="request" />
	</c:when>
	<c:when test='${param.detail eq "Vigilanza" }'>
		<c:set var="titolo" value="Configurazione propriet&agrave; per integrazione con Vigilanza" scope="request" />
	</c:when>
	<c:otherwise>
		<c:set var="titolo" value="Configurazione propriet&agrave;" scope="request" />
	</c:otherwise>
</c:choose>

