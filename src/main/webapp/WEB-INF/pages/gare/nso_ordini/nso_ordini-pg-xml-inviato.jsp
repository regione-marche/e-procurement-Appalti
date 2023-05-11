<%
/*
 * Created on: 13/11/2006
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<c:set var="id" value='${gene:getValCampo(key, "ID")}'/>

<gene:formScheda entita="NSO_ORDINI" gestisciProtezioni="true"  >

	<gene:redefineInsert name="addToAzioni" >
	
	</gene:redefineInsert>

	<gene:redefineInsert name="addToDocumenti" >
	
	</gene:redefineInsert>
	<gene:campoScheda>
		<td colspan="2"><b>Dati relativi all'invio</b></td>
	</gene:campoScheda>
	<gene:campoScheda campo="ID" visibile="false"   />
	<gene:campoScheda campo="ID" entita="NSO_WS_ORDINI" where="NSO_WS_ORDINI.ID_ORDINE=NSO_ORDINI.ID" visibile="false"   />
	<gene:campoScheda campo="CODORD" entita="NSO_WS_ORDINI" where="NSO_WS_ORDINI.ID_ORDINE=NSO_ORDINI.ID" modificabile="false" />
	<gene:campoScheda campo="NOME_FILE" entita="NSO_WS_ORDINI" where="NSO_WS_ORDINI.ID_ORDINE=NSO_ORDINI.ID" modificabile="false" href='javascript:visualizzaFileAllegato("${datiRiga.NSO_WS_ORDINI_NOME_FILE }")'/>
	<gene:campoScheda campo="DATA_ORDINE" entita="NSO_WS_ORDINI" where="NSO_WS_ORDINI.ID_ORDINE=NSO_ORDINI.ID" modificabile="false" />
	<gene:campoScheda campo="IDT" entita="NSO_WS_ORDINI" where="NSO_WS_ORDINI.ID_ORDINE=NSO_ORDINI.ID" modificabile="false" />

	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	
</gene:formScheda>

<gene:javaScript>

	function visualizzaFileAllegato(nomeFile) {
		var url="${pageContext.request.contextPath}/pg/NsoIntegrationDownload.do?method=download&fileName=" + nomeFile;
		console.log('Funzione in sviluppo'+url);
		window.open(url,'Download '+ nomeFile);
	}


</gene:javaScript>
