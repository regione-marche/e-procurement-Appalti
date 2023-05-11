<%
/*
* Created on: 07/11/2008
*
* Copyright (c) EldaSoft S.p.A.
* Tutti i diritti sono riservati.
*
* Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
* In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
* aver prima formalizzato un accordo specifico con EldaSoft.
*/
/*
Descrizione:
	Settaggio della data scadenza nella pagina chiamante
*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script language="javascript" type="text/javascript">

<!--
	function init() {
<c:if test="${empty requestScope.errore}">
		window.opener.setValue("${requestScope.campoDataScadenza}", "${requestScope.dataScadenza}");
</c:if>
<c:if test="${!empty requestScope.errore}">
	<c:if test="${!empty requestScope.descrCampoDataScadenza}">
		window.opener.alert("Non è stato possibile eseguire il calcolo di '${requestScope.descrCampoDataScadenza}'");
	</c:if>
	<c:if test="${empty requestScope.descrCampoDataScadenza}">
		window.opener.alert("Invocazione non valida dal calcolo data scadenza (tipo calcolo errato)");
	</c:if>
</c:if>
		window.close();
	}

init();	
//-->
</script>

