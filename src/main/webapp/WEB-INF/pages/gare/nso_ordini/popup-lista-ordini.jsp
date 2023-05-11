<%
/*
 * Created on: 17-mar-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 /* Lista popup di selezione della rda associare alla gara */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions.ComponiWhereNSO_ORDINIFunction" />

<gene:template file="popup-template.jsp">
	<gene:setString name="titoloMaschera" value="Selezione ordini da collegare"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" sortColumn="2;3" tableclass="datilista" entita="NSO_ORDINI" inserisciDaArchivio="false" gestisciProtezioni="true">
			<table class="dettaglio-noBorderBottom">
				<tr><td colspan="2">
					Nella lista sottostante sono elencati gli ordini collegabili
					</td></tr>
				<tr><td>&nbsp;</td></tr>
			</table>		
			
			<% // Aggiungo gli item al menu contestuale di riga %>
			<gene:campoLista title="Opzioni" width="50">
			<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
			</c:if>
			</gene:campoLista>
			<gene:campoLista campo="ID" headerClass="sortable" visibile="false"/>
			<gene:campoLista campo="CODORD" headerClass="sortable" visibile="true" href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista campo="OGGETTO"  headerClass="sortable"  />
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
