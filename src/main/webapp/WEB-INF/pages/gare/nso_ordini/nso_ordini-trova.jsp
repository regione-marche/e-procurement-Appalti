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
	value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "NSO_ORDINI")}' />

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="NSO_ORDINI-trova">
	<gene:setString name="titoloMaschera" value="Ricerca ordini"/>
	
	<gene:redefineInsert name="trovaCreaNuovo">
		<c:if test='${gene:checkProtFunz(pageContext,"INS","TROVANUOVO")}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:nuovoOrdineNso();" title="Inserisci" tabindex="1503">
						${gene:resource("label.tags.template.trova.trovaCreaNuovo")}
					</a>
				</td>
			</tr>
		</c:if>
	</gene:redefineInsert>
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
  	<% // Creo la form di trova con i campi dell'entità gareavvisi %>
  	<gene:formTrova entita="NSO_ORDINI" filtro="${filtroLivelloUtente}" gestisciProtezioni="true" >
		<gene:gruppoCampi idProtezioni="GEN">
			<tr><td colspan="3"><b>Dati generali</b></td></tr>
			<gene:campoTrova campo="CODORD" title="Codice ordine" />
			<gene:campoTrova campo="NGARA" title= "Gara/Lotto di riferimento" />
			<gene:campoTrova campo="OGGETTO" title="Oggetto" />
			<gene:campoTrova campo="CUP" title="Codice CUP" />
			<gene:campoTrova campo="CIG" title="Codice CIG" />
			<gene:campoTrova campo="DATA_ORDINE" title="Data ordine" />
			<gene:campoTrova campo="DATA_SCADENZA" title="Data scadenza" />
			<gene:campoTrova campo="STATO_ORDINE" title="Stato ordine" />
		</gene:gruppoCampi>

		
    </gene:formTrova>    
  </gene:redefineInsert>
  
  <gene:javaScript>
  
	function nuovoOrdineNso(){
		document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/nso_ordini/nso_associaCig.jsp&tipoGara=garaLottoUnico&modo=NUOVO";
	}
  
  
  </gene:javaScript>
</gene:template>
