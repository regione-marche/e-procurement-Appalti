
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

<gene:template file="ricerca-template.jsp" idMaschera="W3GARA-trova"
	gestisciProtezioni="true" schema="W3">
	<gene:setString name="titoloMaschera" value="Ricerca richieste CIG e smartCIG" />
	<gene:setString name="entita" value="V_W3GARE" />
	<gene:redefineInsert name="corpo">
		<gene:formTrova entita="V_W3GARE" 
			gestisciProtezioni="true">
			<tr>
				<td colspan="3"><b>Dati della gara</b></td>
			</tr>
			<gene:campoTrova campo="ID_GARA" />
			<gene:campoTrova campo="STATO_SIMOG" />
			<gene:campoTrova entita="W3GARA" campo="DATA_CREAZIONE" where="V_W3GARE.NUMGARA = W3GARA.NUMGARA"/>
			<gene:campoTrova entita="W3GARA" campo="DATA_CANCELLAZIONE_GARA" where="V_W3GARE.NUMGARA = W3GARA.NUMGARA"/>
			<gene:campoTrova campo="DATA_CONFERMA_GARA" />
			<gene:campoTrova campo="OGGETTO" />
			<gene:campoTrova campo="IMPORTO_GARA" />
			<gene:campoTrova campo="CODICE_GARA" />
			<tr>
				<td colspan="3"><b>Dati del lotto</b></td>
			</tr>
			<gene:campoTrova entita="W3LOTT" campo="CIG" where="W3LOTT.NUMGARA = V_W3GARE.NUMGARA AND V_W3GARE.TIPO_GARA = 'G'" />
			<gene:campoTrova entita="W3LOTT" campo="STATO_SIMOG" where="W3LOTT.NUMGARA = V_W3GARE.NUMGARA AND V_W3GARE.TIPO_GARA = 'G'" />
			<gene:campoTrova entita="W3LOTT" campo="DATA_CREAZIONE_LOTTO" where="W3LOTT.NUMGARA = V_W3GARE.NUMGARA AND V_W3GARE.TIPO_GARA = 'G'" />
			<gene:campoTrova entita="W3LOTT" campo="DATA_CANCELLAZIONE_LOTTO" where="W3LOTT.NUMGARA = V_W3GARE.NUMGARA AND V_W3GARE.TIPO_GARA = 'G'" />
			<gene:campoTrova entita="W3LOTT" campo="DATA_PUBBLICAZIONE" where="W3LOTT.NUMGARA = V_W3GARE.NUMGARA AND V_W3GARE.TIPO_GARA = 'G'" />
			<gene:campoTrova entita="W3LOTT" campo="OGGETTO" where="W3LOTT.NUMGARA = V_W3GARE.NUMGARA AND V_W3GARE.TIPO_GARA = 'G'" />
			<gene:campoTrova entita="W3LOTT" campo="TIPO_CONTRATTO" where="W3LOTT.NUMGARA = V_W3GARE.NUMGARA AND V_W3GARE.TIPO_GARA = 'G'" />
			<gene:campoTrova entita="W3LOTT" campo="IMPORTO_LOTTO" where="W3LOTT.NUMGARA = V_W3GARE.NUMGARA AND V_W3GARE.TIPO_GARA = 'G'" />

		</gene:formTrova>
		
		<gene:redefineInsert name="listaNuovo">
					<td class="vocemenulaterale"><a
					href="javascript:trovaCreaNuovo();" title="Inserisci"
					tabindex="1503"> Nuovo </a></td>
		</gene:redefineInsert>
				
	</gene:redefineInsert>
	<gene:javaScript>
	
		function trovaCreaNuovo(){
			document.location.href = '${pageContext.request.contextPath}/ApriPagina.do?' + csrfToken + '&href=w3/w3gara/w3gara-nuova.jsp';
		}
		
	</gene:javaScript>
</gene:template>
