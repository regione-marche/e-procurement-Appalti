<%
/*
 * Created on: 06-04-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Form di ricerca dei tecnici */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<c:set var="annulla" value="${param.annulla}" />
<c:set var="tipo" value="${param.tipo}" />

<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.InitFiltroListaSessioneFunction", pageContext,annulla,tipo)}' />

<script type="text/javascript">
	window.opener.document.forms[0].pgSort.value = "";
	window.opener.document.forms[0].pgLastSort.value = "";
	window.opener.document.forms[0].pgLastValori.value = "";
	window.opener.document.forms[0].trovaAddWhere.value = "";
	window.opener.document.forms[0].trovaParameter.value = "";
	window.close();
	window.opener.bloccaRichiesteServer();
	window.opener.listaVaiAPagina(0);
</script>

<gene:template file="popup-template.jsp" >
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value="Imposta filtro"/>
	
	
	
</gene:template>
