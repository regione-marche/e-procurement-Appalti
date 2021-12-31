<%
	/*
   * Created on: 10/10/2008
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */
   
   /*
		Descrizione: Lista delle gare a lotti filtrate per:
		  - V_GARE_TORN.ISLOTTI = 1;
		  - V_GARE_TORN.TIPGEN = <tipo della gara di partenza>;
		ed in base ai permessi dell'utente sulle gare stesse
		
		Creato da:   Luca Giacomazzo
	  */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<gene:template file="popup-template.jsp">

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />
<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_TORN")}' />
<!-- abilitazioneGare = ${abilitazioneGare} -->

<c:if test="${not empty filtroLivelloUtente and abilitazioneGare eq 'U'}" >
	<c:set var="filtroLivelloUtente" value='${fn:replace(filtroLivelloUtente, "))", ") and (G_PERMESSI.AUTORI = 1))")}' />
</c:if>

<c:set var="filtroTipoGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetFiltroTipoGaraFunction", pageContext)}' />

<c:if test="${!empty filtroTipoGara}">
	<c:if test ="${!empty filtroLivelloUtente}">
		<c:set var="filtroLivelloUtente" value="${filtroLivelloUtente} AND " />
	</c:if>
	<c:set var="filtroLivelloUtente" value="${filtroLivelloUtente} ${filtroTipoGara }" />
</c:if>

	<gene:setString name="titoloMaschera" value="Selezione della gara divisa in lotti"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" entita="V_GARE_TORN" inserisciDaArchivio="false" sortColumn="3" where="${filtroLivelloUtente}" >
			<gene:campoLista title="Opzioni" width="50" >
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<gene:campoLista campo="TIPGEN" headerClass="sortable" width="120" />		
			<gene:campoLista campo="CODICE" headerClass="sortable" href="javascript:archivioSeleziona(${datiArchivioArrayJs})"/>
			<gene:campoLista campo="OGGETTO" headerClass="sortable" />
			<gene:campoLista campo="IMPORTO" headerClass="sortable"/>
			<gene:campoLista campo="TIPGAR" entita="TORN" where="TORN.CODGAR = V_GARE_TORN.CODGAR" visibile="false" headerClass="sortable"/>

		</gene:formLista>
  </gene:redefineInsert>

</gene:template>