<%/*
   * Created on 26-11-2013
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

<c:set var="filtroLivelloUtente"
	value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "MERIC")}' />
<c:set var="filtro" value="${filtroLivelloUtente}" />

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="MERIC-trova">
	<gene:setString name="titoloMaschera" value="Ricerca ricerche di mercato"/>
	
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
  		<gene:formTrova entita="MERIC" filtro="${filtro}" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.trova.gestori.MericGestoreTrova" >
			<gene:gruppoCampi idProtezioni="DATIGEN" >
				<tr><td colspan="3"><b>Dati generali</b></td></tr>
				<gene:campoTrova campo="CODRIC" />
				<gene:campoTrova campo="OGGETTO"/>
				<gene:campoTrova campo="ISARCHI" defaultValue="2" />
				<gene:campoTrova campo="NGARA" entita="V_ODA" where="v_oda.idric=meric.id" />
			</gene:gruppoCampi>
			
			<gene:gruppoCampi idProtezioni="ORDINE" >
				<tr><td colspan="3"><b>Dati degli ordini di acquisto</b></td></tr>
				<gene:campoTrova campo="NGARA" entita="V_ODA" where="v_oda.idric=meric.id" />
				<gene:campoTrova campo="NUMODA" entita="V_ODA" where="v_oda.idric=meric.id" />
				<gene:campoTrova campo="CODCIG" entita="GARE" from="V_ODA" where="v_oda.idric=meric.id and v_oda.ngara=gare.ngara" />
				<gene:campoTrova campo="NOT_GAR" entita="GARE" from="V_ODA" where="v_oda.idric=meric.id and v_oda.ngara=gare.ngara" />
				<gene:campoTrova campo="NOMEST" entita="V_ODA" where="v_oda.idric=meric.id" />
				<gene:campoTrova campo="STATO" entita="V_ODA" where="v_oda.idric=meric.id" />
				<tr>
					<td class="etichetta-dato">Ricerche di mercato con articoli per cui non è stato selezionato nessun prodotto</td>
					<td class="operatore-trova"/>
					<td class="valore-dato-trova">
						<input type="checkbox" id="ricercheNonCompletate" name="ricercheNonCompletate" value="1"/>
					</td>
				</tr>
			</gene:gruppoCampi>
    </gene:formTrova>
  </gene:redefineInsert>
</gene:template>