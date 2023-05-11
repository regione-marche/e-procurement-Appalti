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
 /* Scheda Modelli di Comunicazioni */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENEWEB" idMaschera="W_CONFCOM-scheda">
	<%-- Settaggio delle stringhe utilizzate nel template --%>
	<gene:setString name="titoloMaschera" value="Dettaglio modelli di comunicazioni" />
	
	<c:if test='${modo eq "NUOVO"}'>
		<c:set var="numord"
		value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.ValorizzaNumeroOrdine",pageContext)}' />
	</c:if>
	
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="W_CONFCOM" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_CONFCOM" >
			<gene:campoScheda>
				<td colspan="2"><b>Dati modello<b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="MODTIT" />
			<gene:campoScheda campo="MODDESC" />
			<gene:campoScheda campo="GENERE" title="Oggetto a cui è riferito il modello"/>
			<gene:campoScheda campo="NUMORD" obbligatorio="true" defaultValue="${numord}"/>
			<gene:campoScheda>
				<td colspan="2"><b>Dati comunicazione<b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="COMINTEST" title="Anteporre intestazione nel testo della comunicazione?" defaultValue="2" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoNoSiSenzaNull" />
			<gene:campoScheda campo="COMMSGOGG" title="Oggetto del messaggio"/>
			<gene:campoScheda campo="CRITTES" title="Criterio composizione testo"/>
			<gene:campoScheda campo="COMMSGTES" title="Testo del messaggio"/>
			<gene:campoScheda campo="FILTROSOG" title="Filtro sui soggetti destinatari" />
			<gene:campoScheda campo="NUMPRO" visibile="false"  />			
			
			<gene:fnJavaScriptScheda funzione='gestioneCampoGenere("#W_CONFCOM_GENERE#")' elencocampi='W_CONFCOM_GENERE' esegui="true" />
			<gene:fnJavaScriptScheda funzione='gestioneCampoCrittes("#W_CONFCOM_CRITTES#")' elencocampi='W_CONFCOM_CRITTES' esegui="false" />
			
			<gene:campoScheda>	
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
		</gene:formScheda>
		<gene:javaScript>
			function gestioneCampoGenere(valore){
				if(valore==4 || valore==5 || valore==6){
					showObj("rowW_CONFCOM_FILTROSOG",false);
					showObj("rowW_CONFCOM_CRITTES",true);
					setValue("W_CONFCOM_FILTROSOG","");
					var crittes = getValue("W_CONFCOM_CRITTES");
					if(crittes==1){
						showObj("rowW_CONFCOM_COMMSGTES",true);
					}else{
						showObj("rowW_CONFCOM_COMMSGTES",false);
						setValue("W_CONFCOM_COMMSGTES","");
					}
				}else{
					showObj("rowW_CONFCOM_CRITTES",false);
					setValue("W_CONFCOM_CRITTES","");
					showObj("rowW_CONFCOM_FILTROSOG",true);
					showObj("rowW_CONFCOM_COMMSGTES",true);
				}
				
			}
			
			function gestioneCampoCrittes(valore){
				if(valore==1){
					showObj("rowW_CONFCOM_COMMSGTES",true);
				}else{
					showObj("rowW_CONFCOM_COMMSGTES",false);
					setValue("W_CONFCOM_COMMSGTES","");
				}
			}
		</gene:javaScript>
	</gene:redefineInsert>
</gene:template>
