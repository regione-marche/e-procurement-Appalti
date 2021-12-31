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

<c:choose>
	<c:when test='${fn:containsIgnoreCase(campoChiave, "IDMERIC") || isProceduraTelematica eq "true"}'>
		<display:column title="Ruolo" >
		<input type="hidden" name="ruolo"  id="ruolo${permessoEntitaForm_rowNum - 1}" value="${permessoEntitaForm.ruolo}"/>
		<input type="hidden" name="meruoloUsrsys"  id="meruoloUsrsys${permessoEntitaForm_rowNum - 1}" value="${ permessoEntitaForm.ruoloUsrsys}"/>
		<select id="ruoloSelezionato${permessoEntitaForm_rowNum - 1}" <c:if test='${empty permessoEntitaForm.autorizzazione}'>disabled="true"</c:if> onchange="javascript:setRuolo(${permessoEntitaForm_rowNum - 1});" >
			<c:forEach items="${listaRuoli}" varStatus="indice" >
				<option value="${listaRuoli[indice.index].tipoTabellato}" <c:if test='${listaRuoli[indice.index].tipoTabellato eq permessoEntitaForm.ruolo or (empty permessoEntitaForm.ruolo and (listaRuoli[indice.index].tipoTabellato eq permessoEntitaForm.ruoloUsrsys)) or (empty permessoEntitaForm.ruolo and empty permessoEntitaForm.ruoloUsrsys and indice.index==1)}'>selected="selected"</c:if>>${listaRuoli[indice.index].descTabellato}</option>
			</c:forEach>
		</select>
	</display:column>
	</c:when>	
	<c:otherwise>
		<display:column title="Ruolo" headerClass="nascosto" class="nascosto" >
			<input type="hidden" name="ruolo"  id="ruolo${permessoEntitaForm_rowNum - 1}" value="${permessoEntitaForm.ruolo}"/>
		</display:column>
	</c:otherwise>
</c:choose>
