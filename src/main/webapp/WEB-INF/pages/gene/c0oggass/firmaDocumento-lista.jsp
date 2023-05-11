<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<a style="float:right;" href="javascript:preAperturaModaleRichiestaFirmaC0OGGASS('${sessionScope.moduloAttivo}','${datiRiga.W_DOCDIG_IDDOCDIG}','${param.indiceRiga }');">
<img src="${pageContext.request.contextPath}/img/firmaRemota.png" title="Firma digitale del documento" alt="Firma documento" width="16" height="16">
</a>

