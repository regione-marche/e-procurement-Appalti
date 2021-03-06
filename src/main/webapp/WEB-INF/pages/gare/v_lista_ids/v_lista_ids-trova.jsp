<%/*
       * Created on 08-ott-2008
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
<% // i filtri applicati sono il filtro sul livello utente ed il filtro sul codice profilo %>


<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_LISTA_IDS-trova">
	<gene:setString name="titoloMaschera" value="Ricerca ids"/>
	
	<gene:redefineInsert name="trovaCreaNuovo">
	</gene:redefineInsert>
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
  	<% // Creo la form di trova con i campi dell'entit? %>
  	<gene:formTrova entita="V_LISTA_IDS" gestisciProtezioni="true" >
  		<gene:gruppoCampi idProtezioni="DATIGEN">
  			<gene:campoTrova campo="OGGETTO"/>
			<gene:campoTrova campo="NUMERO_PROTOCOLLO"/>
			<gene:campoTrova campo="DATA_PROTOCOLLO"/>
		</gene:gruppoCampi>
	</gene:formTrova>    
  </gene:redefineInsert>
  
</gene:template>
