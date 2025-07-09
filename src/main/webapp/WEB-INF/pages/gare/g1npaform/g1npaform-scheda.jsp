<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="G1NPAFORM-scheda">

<c:set var="idForm" value='${gene:getValCampo(key, "G1NPAFORM.ID")}'/>

<c:choose>
	<c:when test="${not empty param.codiceGara}">
		<c:set var="codiceGara" value="${param.codiceGara}"/>
	</c:when>
	<c:otherwise>
		<c:set var="sql" value="select codgar from g1npaform where id=?"/>
		<c:set var="codiceGara" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, idForm)}'/>
	</c:otherwise>
</c:choose>

<c:set var="sql" value="select genere from v_gare_genere where codgar=? and genere < 100"/>
<c:set var="genereGara" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, codiceGara)}'/>

<c:if test="${modo ne 'NUOVO'}">
	<c:set var="nonUsato" value='${gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.npa.GetDatiFormulario", pageContext, idForm)}' />
</c:if>

<c:choose>
	<c:when test="${genereGara eq '10'}">
		<c:set var="dicituraTipologiaPerTitolo" value="dell'elenco"/>
	</c:when>
	<c:when test="${genereGara eq '11'}">
		<c:set var="dicituraTipologiaPerTitolo" value="dell'avviso"/>
	</c:when>
	<c:when test="${genereGara eq '20'}">
		<c:set var="dicituraTipologiaPerTitolo" value="del catalogo"/>
	</c:when>
	<c:otherwise>
		<c:set var="dicituraTipologiaPerTitolo" value="della gara"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${fn:startsWith(codiceGara, '$')}">
		<c:set var="codiceGaraSenzaDollaro" value="${fn:substring(codiceGara, 1, fn:length(codiceGara))}"/>
	</c:when>
	<c:otherwise>
		<c:set var="codiceGaraSenzaDollaro" value='${codiceGara}'/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${modo eq 'NUOVO'}">
		<gene:setString name="titoloMaschera" value='Nuova scheda ANAC ${dicituraTipologiaPerTitolo} ${codiceGaraSenzaDollaro}' />
	</c:when>
	<c:otherwise>
		<gene:setString name="titoloMaschera" value='Scheda ANAC ${dicituraTipologiaPerTitolo} ${codiceGaraSenzaDollaro}${isRettifica? " - RETTIFICA #":"" }${isRettifica ? numeroRettifiche :"" }' />
	</c:otherwise>
</c:choose>

<gene:redefineInsert name="corpo">

<gene:formPagine gestisciProtezioni="true">
<gene:pagina title="Dati generali" visibile='true' idProtezioni="DATIGEN">
	<jsp:include page="g1npaform-datigen.jsp">
		<jsp:param name="idForm" value="${idForm}"/>
		<jsp:param name="modpres" value="${modpres}"/>
		<jsp:param name="uuid_eform_originale" value="${uuid_eform_originale}"/>
	</jsp:include>
</gene:pagina>
<gene:pagina title="Lotti" idProtezioni="LOTTI" visibile='${genereGara ne 10 and genereGara ne 11 and genereGara ne 20}'>
	<jsp:include page="g1npaform-lotti.jsp" >
		<jsp:param name="idForm" value="${idForm}"/>
		<jsp:param name="modpres" value="${modpres}"/>
		<jsp:param name="nome_scheda" value="${nome_scheda}"/>
	</jsp:include>
</gene:pagina>
</gene:formPagine>
</gene:redefineInsert>
</gene:template>