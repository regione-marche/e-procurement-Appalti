<%
/*
 * Created on: 21-mar-2017
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 /* Dettaglio Configurazione scadenze gara */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="CATSCA-scheda">
	<%-- Settaggio delle stringhe utilizzate nel template --%>
	<gene:setString name="titoloMaschera" value="Dettaglio configurazione scadenze gara" />
	
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="CATSCA" gestisciProtezioni="true"  >
			
			<gene:campoScheda campo="NUMSCA" visibile='false'/>
			<gene:campoScheda campo="CALCOLO" modificabile="false" />
			<gene:campoScheda>
				<td colspan="2"><b>Caratteristiche della gara<b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="TIPLAV" modificabile="false" />
			<gene:campoScheda campo="LIMINF" modificabile="false" />
			<gene:campoScheda campo="LIMSUP" modificabile="false" />
			<gene:campoScheda campo="TIPGAR" modificabile="false" />
			<gene:campoScheda campo="PROURG" modificabile="false" />
			<gene:campoScheda campo="TERRID" modificabile="false" />
			<gene:campoScheda campo="DOCWEB" modificabile="false" />
			<gene:campoScheda>
				<td colspan="2"><b>Configurazione scadenza<b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="NUMGIO" />
			
			<gene:campoScheda>	
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
			
			<gene:redefineInsert name="pulsanteNuovo" />			
			
		</gene:formScheda>
		
		
	</gene:redefineInsert>
	
</gene:template>
