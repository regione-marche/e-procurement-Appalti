<%
/*
 * Created on: 09-10-2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 /* Lista popup di selezione del codice del lotto di gara */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<gene:template file="popup-template.jsp">
	<gene:setString name="titoloMaschera" value="Selezione del lotto di gara"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" entita="GARE" inserisciDaArchivio="false" gestisciProtezioni="true" sortColumn="2" where="${filtroLista}">
			<% // Aggiungo gli item al menu contestuale di riga %>
			<gene:campoLista title="Opzioni" width="50">
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<gene:campoLista campo="NGARA" title="Codice lotto" width="150" headerClass="sortable" href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista campo="CODIGA" title="Lotto" headerClass="sortable" />
			<gene:campoLista campo="NOT_GAR" headerClass="sortable"/>
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
