<%
/*
 * Created on 23-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA DEGLI 
 // UTENTI ASSOCIATI ALL'ENTITA IN ANALISI
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${fn:containsIgnoreCase(campoChiave, "CODGAR")}'>
		<c:set var="isProceduraTelematica" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsProceduraTelematicaFunction", pageContext, valoreChiave)}'/>
	</c:when>
	<c:otherwise>
		<c:set var="isProceduraTelematica" value='false'/>
	</c:otherwise>
</c:choose>

<c:if test='${fn:containsIgnoreCase(campoChiave, "IDMERIC") || isProceduraTelematica eq "true" }'>
<display:column title="Ruolo" sortable="true" headerClass="sortable">
		<c:out value="${hashRuoli[PermessoEntitaForm.ruolo].descTabellato}" />
	</display:column>
<display:column property="ruoloUsrsys" headerClass="nascosto" class="nascosto">
</display:column>
</c:if>