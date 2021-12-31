<%/*
       * Created on 25-07-2013
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="ANTICOR-trova" >
	<gene:setString name="titoloMaschera" value="Ricerca gare e contratti - adempimenti Legge 190/2012"/>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
  	<gene:formTrova entita="ANTICOR" gestisciProtezioni="false" >
		<gene:campoTrova campo="ANNORIF"/>
		<gene:campoTrova campo="TITOLO"/>
		<gene:campoTrova campo="COMPLETATO"/>
		<gene:campoTrova campo="ESPORTATO" />
		<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#")}'>
			<gene:campoTrova campo="PUBBLICATO"/>
		</c:if>
		
		<c:if test="${sessionScope.profiloUtente.abilitazioneGare ne 'A'}" >
			<gene:redefineInsert name="trovaCreaNuovo" />
		</c:if>	
	</gene:formTrova>    
  </gene:redefineInsert>
  
  
</gene:template>
