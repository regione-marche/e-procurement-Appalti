<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="entitaPrincipaleModificabile" value="0" scope="request"/>

<jsp:include page="/WEB-INF/pages/gene/c0oggass/c0oggass-scheda.jsp" />

<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, param.ngaraaq)}' />

<script type="text/javascript">
function visualizzaFileDIGOGG(c0acod, dignomdoc) {
    if (confirm("Si sta per scaricare (download) una copia del file in locale. Ogni modifica verr\u00E0 apportata alla copia locale ma non all\'originale. Continuare?"))
        {
            var href = "${pageContext.request.contextPath}/pg/DownloadDocumentoAssociato.do";
            document.location.href = href+"?c0acod=" + c0acod + "&dignomdoc=" + dignomdoc+ "&codgar=${codgar}&campo=adesione";
        }
}

var form = $("form").first();
$("<input>").attr({
    type: "hidden",
    name: "ngaraaq",
    value: "${param.ngaraaq}"
}).appendTo(form);

</script>
