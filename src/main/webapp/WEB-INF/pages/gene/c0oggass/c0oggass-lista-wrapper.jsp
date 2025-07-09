<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="entitaPrincipaleModificabile" value="0" scope="request"/>

<jsp:include page="/WEB-INF/pages/gene/c0oggass/c0oggass-lista.jsp" />

<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, param.ngaraaq)}' />

<script type="text/javascript">
function visualizzaFileDIGOGG(c0acod, dignomdoc) {
    if (confirm("Si sta per scaricare (download) una copia del file in locale. Ogni modifica verr\u00E0 apportata alla copia locale ma non all\'originale. Continuare?"))
        {
            var href = "${pageContext.request.contextPath}/pg/DownloadDocumentoAssociato.do";
            document.location.href = href+"?c0acod=" + c0acod + "&dignomdoc=" + dignomdoc+ "&codgar=${codgar}&campo=adesione";
        }
}

function alterForm(){
    var form = $("form").first();
    form.attr("action", "${pageContext.request.contextPath}/ApriPagina.do");
    form.find("input[name='href']").remove();
    $("<input>").attr({
        type: "hidden",
        name: "href",
        value: "gene/c0oggass/c0oggass-scheda-wrapper.jsp"
    }).appendTo(form);
    form.find("input[name='ngaraaq']").remove();
    $("<input>").attr({
        type: "hidden",
        name: "ngaraaq",
        value: "${param.ngaraaq}"
    }).appendTo(form);
}

function listaVisualizza(){
    document.forms[0].key.value=chiaveRiga;
    document.forms[0].metodo.value="apri";
    document.forms[0].activePage.value="0";
    alterForm()
    document.forms[0].submit();
}

function listaModifica(){
    document.forms[0].key.value=chiaveRiga;
    document.forms[0].metodo.value="modifica";
    document.forms[0].activePage.value="0";
    alterForm()
    document.forms[0].submit();
}

</script>
