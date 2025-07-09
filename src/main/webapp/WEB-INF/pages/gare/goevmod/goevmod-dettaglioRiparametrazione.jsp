<%
/*
 * Created on: 17/07/2023
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

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GOEVMOD-scheda">

	<gene:setString name="titoloMaschera" value='Dettaglio assegnazione punteggio criterio di valutazione'/>
	<gene:redefineInsert name="corpo">
		<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
		<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
		<gene:formScheda entita="GOEVMOD" gestisciProtezioni="true">
			
			<gene:gruppoCampi idProtezioni="DATGEN">
				<gene:campoScheda>
					<td colspan="2"><b>Dati generali</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="ID" visibile="false" modificabile="false"/>
				<gene:campoScheda campo="DESPAR" modificabile="false" title="Descrizione criterio"/>
				<gene:campoScheda campo="MAXPUN" modificabile="false"/>
				<gene:campoScheda campo="ISNORIPAR" modificabile="true"/>
			</gene:gruppoCampi>
			
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button"  class="bottone-azione" value='Torna a lista criteri di valutazione' title='Torna a lista criteri di valutazione' onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;&nbsp;
					&nbsp;
				</td>
			</gene:campoScheda>
			<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
			<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
			
		</gene:formScheda>

	</gene:redefineInsert>
</gene:template>