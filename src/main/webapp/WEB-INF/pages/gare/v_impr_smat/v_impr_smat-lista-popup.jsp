<%
/*
 * Created on: 23-05-2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 /* Lista popup di selezione imprese per smat */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions.ComponiWhereV_IMPR_SMATFunction" />

<gene:template file="popup-template.jsp" gestisciProtezioni="false">
	<gene:setString name="titoloMaschera" value="Selezione dell'impresa"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" entita="V_IMPR_SMAT" sortColumn="3" gestisciProtezioni="true" >
			<% // Aggiungo gli item al menu contestuale di riga %>
			<gene:campoLista title="Opzioni" width="50">
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<% // Campi della lista %>


	<c:set var="hrefDettaglio" value="javascript:archivioSeleziona(${datiArchivioArrayJs});"/> 

			
			<gene:campoLista campo="CODIMP" headerClass="sortable" width="110" href="${hrefDettaglio}" />
			<gene:campoLista campo="NOMEST" headerClass="sortable" href="${hrefDettaglio}"/>
			<gene:campoLista campo="CFIMP" headerClass="sortable" width="120"/>
			<gene:campoLista campo="PIVIMP" headerClass="sortable" width="120"/>
			<gene:campoLista campo="LOCIMP" headerClass="sortable" width="120"/>
			<gene:campoLista campo="NOMIMP" visibile="false"/>
			<gene:campoLista campo="ID_SEDE" visibile="false"/>
			<gene:campoLista campo="ID_FORNITORE" visibile="false"/>
			<gene:campoLista campo="IS_IMPRESA_OA" visibile="false"/>
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
