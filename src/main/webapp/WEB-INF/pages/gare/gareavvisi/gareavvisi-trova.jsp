<%/*
       * Created on 22-05-2012
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
<c:set var="filtroLivelloUtente"
	value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "GAREAVVISI")}' />

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GAREAVVISI-trova">
	<gene:setString name="titoloMaschera" value="Ricerca avvisi"/>
		
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
  	<% // Creo la form di trova con i campi dell'entità gareavvisi %>
  	<gene:formTrova entita="GAREAVVISI" filtro="${filtroLivelloUtente}" gestisciProtezioni="true" >
		<gene:gruppoCampi idProtezioni="GEN">
			<tr><td colspan="3"><b>Dati generali</b></td></tr>
			<gene:campoTrova campo="NGARA"/>
			<gene:campoTrova campo="TIPOAVV"/>
			<gene:campoTrova campo="TIPOAPP"/>
			<gene:campoTrova campo="OGGETTO" />
			<gene:campoTrova campo="DATSCA" />
			<gene:campoTrova campo="ISARCHI" defaultValue="2" />
		</gene:gruppoCampi>

		
    </gene:formTrova>    
  </gene:redefineInsert>
  
</gene:template>
