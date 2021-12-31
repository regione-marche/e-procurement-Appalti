<%
/*
 * Created on: 29-mar-2016
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 /* Dettaglio motivo esclusione ditte in gara */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="DETMOT-scheda">
	<%-- Settaggio delle stringhe utilizzate nel template --%>
	<gene:setString name="titoloMaschera" value="Dettaglio motivo esclusione ditte in gara" />
	
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="TAB1" gestisciProtezioni="true"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreDETMOT" >
			
			<gene:campoScheda campo="TAB1TIP" visibile="false"/>
			<gene:campoScheda campo="TAB1COD" visibile="false"/>
			<gene:campoScheda campo="TAB1DESC" visibile="true" modificabile="false" title="Motivo esclusione"/>
			<gene:campoScheda campo="MOTIES" entita="DETMOT" where="TAB1.TAB1TIP = MOTIES" visibile="false"/>
			<gene:campoScheda campo="ANNOFF" entita="DETMOT" where="TAB1.TAB1TIP = MOTIES" title="Dettaglio motivo esclusione"/>
			<gene:campoScheda>	
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
			
			<gene:redefineInsert name="pulsanteNuovo" />
			
		</gene:formScheda>
		
	</gene:redefineInsert>
	
	
	
</gene:template>
