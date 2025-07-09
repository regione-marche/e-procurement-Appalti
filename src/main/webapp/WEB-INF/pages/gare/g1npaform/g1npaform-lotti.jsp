<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:choose>
	<c:when test="${not empty param.idForm}">
		<c:set var="idForm" value="${param.idForm}"/>
	</c:when>
	<c:otherwise>
		<c:set var="idForm" value="${idForm}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.islotti}">
		<c:set var="islotti" value="${param.islotti}"/>
	</c:when>
	<c:otherwise>
		<c:set var="islotti" value="${islotti}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.modpres}">
		<c:set var="modpres" value="${param.modpres}"/>
	</c:when>
	<c:otherwise>
		<c:set var="modpres" value="${modpres}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.nome_scheda}">
		<c:set var="nome_scheda" value="${param.nome_scheda}"/>
	</c:when>
	<c:otherwise>
		<c:set var="nome_scheda" value="${nome_scheda}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${modpres eq '1'}">
		<c:set var="entita" value='GARE'/>
		<c:set var="sql" value="select codgar from g1npaform where id=?"/>
		<c:set var="codiceGara" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, idForm)}'/>
		<c:set var="where" value="CODGAR1 = '${codiceGara}' and codgar1 <> ngara"/>
	</c:when>
	<c:when test="${modpres eq '2'}">
		<c:set var="entita" value='G1NPASCHEDA'/>
		<c:set var="where" value='G1NPASCHEDA.IDFORM = #G1NPAFORM.ID#'/>
	</c:when>
	<c:when test="${modpres eq '3'}">
		<c:set var="entita" value='GARE'/>
		<c:set var="sql" value="select id from g1npascheda where idform=?"/>
		<c:set var="idscheda" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, idForm)}'/>
		<c:set var="where" value='GARE.NGARA in (select ngara from G1NPALOTTISCHEDA where G1NPALOTTISCHEDA.IDSCHEDA = ${idscheda})'/>
	</c:when>
	<c:when test="${modpres eq '4'}">
		<c:set var="entita" value='GARE'/>
		<c:set var="sql" value="select id from g1npascheda where idform=?"/>
		<c:set var="idscheda" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, idForm)}'/>
		<c:set var="where" value="GARE.NGARA=(select ngara from G1NPASCHEDA where ID=${idscheda})"/>
	</c:when>
</c:choose>
<c:set var="nomeScheda" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select tipoconc from torn where codgar=?", codiceGara)}' scope="request"/>

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<gene:formLista entita='${entita}' where='${where}' tableclass="datilista" sortColumn="1"
						gestisciProtezioni="true" pagesize="25" >

				<c:choose>
					<c:when test="${modpres eq '1'}">
						<gene:campoLista campo="CODIGA" headerClass="sortable" value="">
							LOT-<fmt:formatNumber pattern="0000" value="${datiRiga.GARE_CODIGA}"/><c:if test="${empty datiRiga.GARE_CODIGA}">0001</c:if>
						</gene:campoLista>
						<gene:campoLista campo="CODCIG" headerClass="sortable"/>
						<gene:campoLista campo="NOT_GAR" headerClass="sortable"/>
					</c:when>

					<c:when test="${modpres eq '2'}">
						<gene:campoLista campo="ID" visibile="false"/>
						<gene:campoLista campo="NGARA" visibile="false"/>
						<gene:campoLista campo="CODGAR1" entita="GARE" where="G1NPASCHEDA.NGARA = GARE.NGARA" visibile="false"/>

						<gene:campoLista campo="CODCIG" entita="GARE" where="G1NPASCHEDA.NGARA = GARE.NGARA" headerClass="sortable"/>
						<gene:campoLista campo="STATONPA"/>

						<gene:campoLista title="" width="22">
							<c:if test="${currentRow >= 0}">
								<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
									<gene:PopUpItem title="Visualizza e controlla" href="javascript:inserisciNPA('${datiRiga.GARE_CODGAR1}','${datiRiga.G1NPASCHEDA_NGARA}','${nome_scheda}')" />
								</gene:PopUp>
							</c:if>
						</gene:campoLista>
					</c:when>


					<c:when test="${modpres eq '3'}">

						<gene:campoLista campo="CODIGA" headerClass="sortable" value="" width="110">
							LOT-<fmt:formatNumber pattern="0000" value="${datiRiga.GARE_CODIGA}"/><c:if test="${empty datiRiga.GARE_CODIGA}">0001</c:if>
						</gene:campoLista>

						<gene:campoLista campo="CODCIG" headerClass="sortable" width="130"/>
						<gene:campoLista campo="NOT_GAR" headerClass="sortable" />
						<gene:campoLista campo="DITTA" headerClass="sortable" visibile="false"/>
						<gene:campoLista campo="ESINEG" headerClass="sortable" visibile="false"/>
					</c:when>

					<c:when test="${modpres eq '4'}">
						<gene:campoLista campo="CODIGA" headerClass="sortable" value="">
							LOT-<fmt:formatNumber pattern="0000" value="${datiRiga.GARE_CODIGA}"/><c:if test="${empty datiRiga.GARE_CODIGA}">0001</c:if>
						</gene:campoLista>
						<gene:campoLista campo="CODCIG" headerClass="sortable"/>
						<gene:campoLista campo="NOT_GAR" headerClass="sortable"/>
					</c:when>
				</c:choose>


			</gene:formLista >
		</td>
	</tr>

</table>

<gene:redefineInsert name="listaNuovo" />
<gene:redefineInsert name="listaEliminaSelezione" />

<gene:javaScript>
</gene:javaScript>