
<%
	/*
	 * Created on 20-Ott-2008
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

<c:set var="filtroLivelloUtente" value="W3GARA.SYSCON = ${profiloUtente.id} OR W3GARA.RUP_CODTEC IN (SELECT RUP_CODTEC FROM W3USRSYS WHERE SYSCON = ${profiloUtente.id})" />

<gene:template file="ricerca-template.jsp" idMaschera="W3GARA-trova"
	gestisciProtezioni="true" schema="W3">
	<gene:setString name="titoloMaschera" value="Ricerca gare e lotti" />
	<gene:setString name="entita" value="W3GARA" />
	<gene:redefineInsert name="corpo">
		<gene:formTrova entita="W3GARA" filtro="${filtroLivelloUtente}"
			gestisciProtezioni="true">
			<tr>
				<td colspan="3"><b>Dati della gara</b></td>
			</tr>
			<gene:campoTrova campo="ID_GARA" />
			<gene:campoTrova campo="STATO_SIMOG" />
			<gene:campoTrova campo="DATA_CREAZIONE" />
			<gene:campoTrova campo="DATA_CANCELLAZIONE_GARA" />
			<gene:campoTrova campo="DATA_CONFERMA_GARA" />
			<gene:campoTrova campo="OGGETTO" />
			<gene:campoTrova campo="IMPORTO_GARA" />
			<tr>
				<td colspan="3"><b>Dati del lotto</b></td>
			</tr>
			<gene:campoTrova entita="W3LOTT" campo="CIG" where="W3LOTT.NUMGARA = W3GARA.NUMGARA" />
			<gene:campoTrova entita="W3LOTT" campo="STATO_SIMOG" where="W3LOTT.NUMGARA = W3GARA.NUMGARA" />
			<gene:campoTrova entita="W3LOTT" campo="DATA_CREAZIONE_LOTTO" where="W3LOTT.NUMGARA = W3GARA.NUMGARA" />
			<gene:campoTrova entita="W3LOTT" campo="DATA_CANCELLAZIONE_LOTTO" where="W3LOTT.NUMGARA = W3GARA.NUMGARA" />
			<gene:campoTrova entita="W3LOTT" campo="DATA_PUBBLICAZIONE" where="W3LOTT.NUMGARA = W3GARA.NUMGARA" />
			<gene:campoTrova entita="W3LOTT" campo="OGGETTO" where="W3LOTT.NUMGARA = W3GARA.NUMGARA" />
			<gene:campoTrova entita="W3LOTT" campo="TIPO_CONTRATTO" where="W3LOTT.NUMGARA = W3GARA.NUMGARA" />
			<gene:campoTrova entita="W3LOTT" campo="IMPORTO_LOTTO" where="W3LOTT.NUMGARA = W3GARA.NUMGARA" />

		</gene:formTrova>
	</gene:redefineInsert>
</gene:template>
