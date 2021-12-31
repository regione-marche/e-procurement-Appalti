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
 /* Dettaglio configurazione selezione da elenco operatori */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="IsGaraConSelezioneAutomaticaDitte" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsGaraConSelezioneAutomaticaDitteFunction", pageContext)}' />

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="CONFOPECO-scheda">
	<%-- Settaggio delle stringhe utilizzate nel template --%>
	<gene:setString name="titoloMaschera" value="Dettaglio configurazione selezione da elenco operatori" />
	
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="CONFOPECO" gestisciProtezioni="true"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCONFOPECO" >
			
			<gene:campoScheda campo="NUMCONFOP" visibile='false'/>
			<gene:campoScheda campo="TIPOPROCEDURA" obbligatorio="true"/>
			<gene:campoScheda campo="TIPOLOGIA" obbligatorio="true"/>
			<gene:campoScheda campo="DAIMPORTO" >
				<gene:checkCampoScheda funzione='gestioneCampoDaImporto("##")' obbligatorio="true" messaggio="L'importo minimo deve essere inferiore all'importo massimo." 
										onsubmit="true"/>
			</gene:campoScheda>
			<gene:campoScheda campo="AIMPORTO" />
			<gene:campoScheda campo="NUMMINOP" />
			<gene:campoScheda campo="NMAXSEL" visibile="${IsGaraConSelezioneAutomaticaDitte}" />
			<gene:campoScheda campo="ISABILITATO" visibile='false' defaultValue="1"/>
			
			<gene:campoScheda>	
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
			
		</gene:formScheda>
		
		<gene:javaScript>
		
			function gestioneCampoDaImporto(valore){
				var aimporto = getValue("CONFOPECO_AIMPORTO");
				if( (valore != '') && (aimporto != '') ){
					var floatDaImporto = parseFloat(valore);
					var floatAImporto = parseFloat(aimporto);
					if(floatDaImporto >= floatAImporto){
						return false;
					}
				}
				return true;
			}
			
		</gene:javaScript>
		
	</gene:redefineInsert>
	
</gene:template>
