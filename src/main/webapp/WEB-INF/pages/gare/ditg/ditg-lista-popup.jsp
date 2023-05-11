<%
/*
 * Created on: 27-05-2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 /* Lista popup di selezione delle ditte */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions.ComponiWhereDITGFunction" />

<c:set var="nomeContainerFiltri" value="deftrovaDITG-${empty param.numeroPopUp ? 0 : param.numeroPopUp}"/> 
<c:set var="tmp" value="${sessionScope[nomeContainerFiltri].trovaAddWhere}" />

<c:choose>
	<c:when test='${(!empty tmp) and fn:contains(tmp, "daatto") }' >
		<c:set var="titolo" value="Selezione ditta aggiudicataria" />
	</c:when>
	<c:otherwise>
		<c:set var="titolo" value="Selezione ditta per aggiudicazione definitiva" />
	</c:otherwise>
</c:choose>

<gene:template file="popup-template.jsp" >
	<gene:setString name="titoloMaschera" value="${titolo }"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" entita="DITG" sortColumn="2;3" gestisciProtezioni="true">
			<% // Aggiungo gli item al menu contestuale di riga %>
			<gene:campoLista title="Opzioni" width="50">
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<% // Campi della lista %>

			<gene:campoLista campo="NPROGG" title="N." headerClass="sortable" href="javascript:archivioSeleziona(${datiArchivioArrayJs});" width="50"/>
			<gene:campoLista campo="DITTAO" visibile="false" />
			<gene:campoLista campo="NOMEST" entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" headerClass="sortable" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
			<gene:campoLista campo="NOMIMO" visibile="false" />
			<gene:campoLista campo="RIBAUO" title="Ribasso" headerClass="sortable"/>
			<gene:campoLista campo="IMPOFF" title="Importo offerto" headerClass="sortable" />
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
