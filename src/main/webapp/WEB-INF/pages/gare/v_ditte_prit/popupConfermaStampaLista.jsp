<%
	/*
	 * Created on 14-mag-2009
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<div style="width: 96%" >
<gene:template file="popup-message-template.jsp">
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value="Stampa per ritiro definitivo"/>
	<gene:redefineInsert name="corpo">
		<br>&nbsp;Vuoi procedere con la stampa della lista dei plichi ritirati?
		<br>&nbsp;
  </gene:redefineInsert>
  <gene:javaScript>

  	function conferma(){
			var href = "href=gare/v_ditte_prit/popupConfermaStampaLista.jsp&tipprot=${param.tipprot}&datap=${param.datap}&operatoreDatap=${param.operatoreDatap}" + "&" + csrfToken;
			document.location.href = "${contextPath}/ApriPagina.do?href=gare/v_ditte_prit/composizioneInCorso.jsp&tipprot=${param.tipprot}&datap=${param.datap}&operatoreDatap=${param.operatoreDatap}";
		}

		function annulla(){
			window.close();
		}
  </gene:javaScript>
</gene:template>
</div>