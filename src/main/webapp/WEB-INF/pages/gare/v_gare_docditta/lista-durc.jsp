<%/*
   * Created on 22-05-2012
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

<!--
<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "GAREAVVISI")}' />

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint}">
	<c:set var="filtroUffint" value=" exists (select codgar from torn where codgar = gareavvisi.codgar and CENINT = '${sessionScope.uffint}')"/>
</c:if>
-->
<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GARE_DOCDITTA-lista-durc" >
	<gene:setString name="titoloMaschera" value="Lista documenti Durc"/>
	
	<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>

	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
		
		<%// Creo la lista per gare e torn mediante la vista %>
		<table class="lista">
			<tr>
				<td>
		<gene:formLista entita="V_GARE_DOCDITTA" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="-4">
  	
			<% // Campi veri e propri %>
			
			<gene:campoLista campo="CODIMP" visibile="false" />
			<gene:campoLista campo="NOMIMP" entita="IMPR" where="V_GARE_DOCDITTA.CODIMP=IMPR.CODIMP"/>
			<gene:campoLista campo="DIGNOMDOC" href="javascript:visualizzaFileAllegato('${datiRiga.V_GARE_DOCDITTA_IDPRG}','${datiRiga.V_GARE_DOCDITTA_IDDOCDG}',${gene:string4Js(datiRiga.V_GARE_DOCDITTA_DIGNOMDOC)});"/>
			<gene:campoLista campo="DATARILASCIO"  headerClass="sortable" />
			<gene:campoLista campo="DATASCADENZA" headerClass="sortable" />
			<gene:campoLista campo="IDPRG" visibile="false" />
			<gene:campoLista campo="IDDOCDG" visibile="false" />
			
		</gene:formLista>
				</td>
			</tr>
		</table>
		
  </gene:redefineInsert>

	<gene:javaScript>

	function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
		var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
		document.location.href=href+"?idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
	}
	
	</gene:javaScript>
</gene:template>