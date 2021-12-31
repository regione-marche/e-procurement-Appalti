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
<c:set var="filtroLivelloUtente"
	value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_DITTE_PRIT")}' />
<c:set var="filtro" value="${filtroLivelloUtente}" />

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetPaginazioneListaFunction" />

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_DITTE_PRIT-trova">
	<gene:setString name="titoloMaschera" value="Ricerca plichi da ritirare"/>
	
	<gene:redefineInsert name="trovaCreaNuovo">
	</gene:redefineInsert>
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
  	<% // Creo la form di trova con i campi dell'entità %>
  	<gene:formTrova entita="V_DITTE_PRIT"  filtro="${filtro}" gestisciProtezioni="true" >
		<gene:campoTrova campo="TIPPROT"/>
		<gene:campoTrova campo="DATAP"/>
	</gene:formTrova>    
  </gene:redefineInsert>
  <gene:javaScript>
  	var a= "${elementiPerPagina}";
  	for (i=0;i<document.getElementById('risultatiPerPagina').length;i++)
	{
	
		if (a == document.getElementById('risultatiPerPagina').options[i].text)
		{
			document.getElementById('risultatiPerPagina').options[i].selected = true;
		}
	}
  </gene:javaScript>
</gene:template>
