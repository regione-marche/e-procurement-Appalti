<%
/*
 * Created on: 25-07-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 /* Lista popup di selezione delle gare OLIAMM */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="popup-template.jsp">
	<gene:setString name="titoloMaschera" value="Selezione gara OLIAMM"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" sortColumn="2;3" tableclass="datilista" entita="V_GARE_OUT" inserisciDaArchivio="false" gestisciProtezioni="false" distinct="true">
			<% // Aggiungo gli item al menu contestuale di riga %>
			<gene:campoLista title="Opzioni" width="50">
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<gene:campoLista campo="ID_LISTA" headerClass="sortable" href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista campo="RIFERIMENTO" headerClass="sortable" width="50" href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista campo="DATA_GARA" headerClass="sortable" />
			<gene:campoLista campo="OGGETTO" headerClass="sortable" />
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
