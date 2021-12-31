<%
/*
 * Created on: 12/06/2014
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


<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.isRicercaMercato}'>
		<c:set var="isRicercaMercato" value="${param.isRicercaMercato}" />
	</c:when>
	<c:otherwise>
		<c:set var="isRicercaMercato" value="${isRicercaMercato}" />
	</c:otherwise>
</c:choose>

<c:set var="where" value="V_CATAPROD.IDARTICOLO = #MEARTCAT.ID# and V_CATAPROD.NGARA = '${ngara }'"/>

<c:if test="${isRicercaMercato eq 'true'}">
	<c:set var="dbms" value="${gene:callFunction('it.eldasoft.gene.tags.utils.functions.GetTipoDBFunction', pageContext)}" />
	<c:choose>
		<c:when test='${dbms eq "ORA"}'>
			<c:set var="where" value="${where } and V_CATAPROD.DATSCADOFF >= sysdate"/>
		</c:when>
		<c:when test='${dbms eq "MSQ"}'>
			<c:set var="where" value="${where } and V_CATAPROD.DATSCADOFF >= getdate()"/>
		</c:when>
		<c:when test='${dbms eq "POS"}'>
			<c:set var="where" value="${where } and V_CATAPROD.DATSCADOFF >= now()"/>
		</c:when>
		<c:when test='${dbms eq "DB2"}'>
			<c:set var="where" value="${where } and V_CATAPROD.DATSCADOFF >= (SELECT current date FROM sysibm.sysdummy1)"/> 
		</c:when>
	</c:choose>
	<c:set var="where" value="${where } and V_CATAPROD.STATO = 4 and exists (select codgar5 from ditg where ngara5=V_CATAPROD.NGARA and dittao=V_CATAPROD.CODIMP and abilitaz=1)"/>
</c:if>
	
		<table class="dettaglio-tab-lista">
			<tr>
				<td>
					<gene:formLista entita="V_CATAPROD" where="${where }" tableclass="datilista" sortColumn='1;2;3' 
							gestisciProtezioni="true"   pagesize="25" >
														
						<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
						<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>				
						<gene:redefineInsert name="pulsanteListaInserisci"></gene:redefineInsert>
						<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>			
						
						<gene:campoLista campo="NOMEST" headerClass="sortable" />
						<gene:campoLista campo="CODOE" headerClass="sortable" href="javascript:visualizzaProdotto(${datiRiga.V_CATAPROD_IDPRODOTTO });"/>
						<gene:campoLista campo="IDPRODOTTO" visibile="false" />
						<gene:campoLista campo="NOME" headerClass="sortable" />
						<gene:campoLista campo="PRZUNITPROD" headerClass="sortable" />
						<gene:campoLista campo="PERCIVA" headerClass="sortable" />
						<gene:campoLista campo="TEMPOCONS" headerClass="sortable" />
						<gene:campoLista campo="STATO" headerClass="sortable" visibile="${param.isRicercaMercato eq 'false'}"/>
						<input type="hidden" id="id" name="id" value="${id}" />
						<input type="hidden" id="ngara" name="ngara" value="${ngara}" />
						<input type="hidden" id="isRicercaMercato" name="isRicercaMercato" value="${isRicercaMercato}" />
					</gene:formLista>
				
			
				</td>
			</tr>
		</table>
		
<gene:javaScript>
	function visualizzaProdotto(idProdotto){
		var chiave = "MEISCRIZPROD.ID=N:"+idProdotto;
		var href = "${pageContext.request.contextPath }/ApriPagina.do?"+csrfToken+"&href=gare/meiscrizprod/meiscrizprod-scheda.jsp&key=" + chiave + "&daListaProdotti=1";
		document.location.href = href;
	}
</gene:javaScript>
		
	


