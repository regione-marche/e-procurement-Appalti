<%
/*
 * Created on: 19/04/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /* La jsp contiene i campi comuni alle pagine  
 	gare-pg-contratto.jsp
 	gare-pg-aggiudicazione-efficace.jsp
 	gare-pg-stipula-accordo-quadro.jsp
 */
%>


<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

	<c:set var="statoArt80" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.CheckStatoArt80Function", pageContext,param.ditta)}' />
	<gene:campoLista title="&nbsp;" width="20" >
		<c:choose>
			<c:when test='${statoArt80 eq "In lavorazione" }'>
				<c:set var="nomeIconaStato" value="art80_inLavorazione.png"/>
			</c:when>
			<c:when test='${statoArt80 eq "Anomalo" }'>
				<c:set var="nomeIconaStato" value="art80_validoNo.png"/>
			</c:when>
			<c:when test='${statoArt80 eq "Non anomalo" }'>
				<c:set var="nomeIconaStato" value="art80_validoSi.png"/>
			</c:when>
		</c:choose>
		<c:choose>
			<c:when test='${not empty statoArt80}'>
				<img width="16" height="16" title="Esito verifica requisiti art.80 DLgs.50/2016:&#13; ${statoArt80}" alt="Esito verifica requisiti art.80 DLgs.50/2016 - ${statoArt80}" src="${pageContext.request.contextPath}/img/${nomeIconaStato}"/>
			</c:when>
			<c:otherwise>
				&nbsp;
			</c:otherwise>
		</c:choose>
	</gene:campoLista>

	