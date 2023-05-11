<%
/*
 * Created on: 19-nov-2009
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

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GARE-APERTURAOFFERTEAGGIUDPROV">
	<% // Settaggio delle stringhe utilizzate nel template %>
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction",pageContext,"APERTURAOFFAGGIUD_LOTTI")}' />
	<gene:redefineInsert name="corpo">
			<jsp:include page="gare-pg-fasiGara.jsp">
				<jsp:param name="paginaFasiGara" value="aperturaOffAggProvLottoOffUnica"/>
				<jsp:param name="bustalotti" value="1"/>
				<jsp:param name="lottoPlicoUnico" value="1"/>
			</jsp:include>
	</gene:redefineInsert>
</gene:template>
