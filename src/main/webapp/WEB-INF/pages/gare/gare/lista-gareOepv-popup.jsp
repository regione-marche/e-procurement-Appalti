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

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions.ComponiWhereGAREFunction" />

<gene:template file="popup-template.jsp">

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />

	<gene:setString name="titoloMaschera" value="Selezione delle gare con criterio di aggiudicazione OEPV"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" entita="GARE" inserisciDaArchivio="false" sortColumn="-2;3" where="${filtroLivelloUtente}" >
			<gene:campoLista campo="CODGAR1" visibile="false" />
			<gene:campoLista campo="CODICE" entita="V_GARE_TORN" where="GARE.CODGAR1=V_GARE_TORN.CODGAR" headerClass="sortable" href="javascript:archivioSeleziona(${datiArchivioArrayJs})"/>
			<gene:campoLista campo="NGARA" visibile="false"/>
			<gene:campoLista campo="CODIGA" title="N.lotto"/>
			<gene:campoLista campo="OGGETTO"  entita="V_GARE_TORN" where="GARE.CODGAR1=V_GARE_TORN.CODGAR"/>

		</gene:formLista>
  </gene:redefineInsert>

</gene:template>