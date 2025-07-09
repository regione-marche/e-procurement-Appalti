<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="ngara" value="${param.ngara}" />
<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="ELPREMALBO-lista">

	<gene:redefineInsert name="addHistory">
		<gene:historyAdd titolo="Elementi premiali " id="elementiPremiali" />
	</gene:redefineInsert>

	<gene:setString name="titoloMaschera" value="Lista elementi premiali dell'elenco ${ngara}"/>

	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">

		<%// Creo la lista filtrando per codgar%>
		<c:set var="where" value="NGARA=?" />
		<c:set var="parametri" value="T:${ngara}"/>
		${gene:callFunction4("it.eldasoft.gene.tags.functions.ImpostazioneFiltroFunction", pageContext, "ELPREMALBO", where, parametri)}

		<table class="lista">
			<tr>
				<td>
					<gene:formLista entita="ELPREMALBO" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="1">
						<gene:redefineInsert name="listaNuovo" />
						<gene:redefineInsert name="listaEliminaSelezione" />
						<gene:redefineInsert name="addToAzioni" />

						<gene:campoLista campo="ID" visibile="false"/>
						<gene:campoLista campo="IDQUESITO" visibile="false"/>
						<gene:campoLista campo="IDQUESITOfitt"  width="20" campoFittizio="true" definizione="N12;;;;G1IDQUE_EPE" value="${not empty datiRiga.ELPREMALBO_TIPOQUESITO?datiRiga.ELPREMALBO_IDQUESITO:''}"/>

						<gene:campoLista campo="TITOLO" tooltip="${datiRiga.ELPREMALBO_DESCQUESITO}" ordinabile="false"/>
						<gene:campoLista campo="DESCQUESITO" visibile="true" ordinabile="false" width="450"/>
						<gene:campoLista campo="IDOPZIONE" visibile="false"/>
						<gene:campoLista campo="DESCOPZIONE" visibile="false"/>
						<gene:campoLista campo="TIPOQUESITO" visibile="false"/>
						<gene:campoLista title="Opzioni" definizione="T500;" campo="TITOLOfitt" campoFittizio="true" value="${empty datiRiga.ELPREMALBO_TIPOQUESITO? datiRiga.ELPREMALBO_IDOPZIONE.concat(' - ').concat(datiRiga.ELPREMALBO_DESCOPZIONE):''}"/>
						<gene:campoLista campo="PESOPONMIN" width="35" ordinabile="false"/>
						<gene:campoLista campo="PESOPONMAX" width="35" ordinabile="false"/>
						<input type="hidden" name="ngara" value="${ngara}"/>
					</gene:formLista>
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
</gene:template>
