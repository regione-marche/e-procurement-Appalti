<%
/*
 * Created on: 16-set-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 *
 * Lista note e avvisi di una gara a lotti, dei singoli lotti e di tutte le entità figlie
 */
%>

<jsp:include page="/WEB-INF/pages/gene/g_noteavvisi/lista-noteavvisiGara.jsp">
	<jsp:param name="schema" value="GARE" />
	<jsp:param name="chiave" value="${key}" />
	<jsp:param name="listaEntita" value="TORN;GARE;GARSED;GOEV;DITG;GCAP" />
</jsp:include>
 