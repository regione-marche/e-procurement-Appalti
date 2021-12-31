<%
	/*
   * Created on: 17/06/2012
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  /*
		Descrizione:
			Maschera per la funzione di Aggiornamento Importo Offerto
			da una ditta
					
			Creato da:	Marcello Caminiti
	 */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:choose>
	<c:when test='${not empty requestScope.aggiornamentoEseguito and requestScope.aggiornamentoEseguito eq "1" }' >
		<script type="text/javascript">
		window.close();
		window.opener.historyVaiIndietroDi(1);
		</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">

<c:choose>
	<c:when test='${!empty totaleOfferto}'>
		<c:set var="totaleOfferto" value='${totaleOfferto}' />
	</c:when>
	<c:otherwise>
		<c:set var="totaleOfferto" value="${param.totaleOfferto}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty numeroGara}'>
		<c:set var="numeroGara" value='${numeroGara}' />
	</c:when>
	<c:otherwise>
		<c:set var="numeroGara" value="${param.numeroGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty codiceGara}'>
		<c:set var="codiceGara" value='${codiceGara}' />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${param.codiceGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty codiceDitta}'>
		<c:set var="codiceDitta" value='${codiceDitta}' />
	</c:when>
	<c:otherwise>
		<c:set var="codiceDitta" value="${param.codiceDitta}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty dittaAgg}'>
		<c:set var="dittaAgg" value='${dittaAgg}' />
	</c:when>
	<c:otherwise>
		<c:set var="dittaAgg" value="${param.dittaAgg}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty sicinc}'>
		<c:set var="sicinc" value='${sicinc}' />
	</c:when>
	<c:otherwise>
		<c:set var="sicinc" value="${param.sicinc}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty isGaraLottiConOffertaUnica}'>
		<c:set var="isGaraLottiConOffertaUnica" value='${isGaraLottiConOffertaUnica}' />
	</c:when>
	<c:otherwise>
		<c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty faseGara}'>
		<c:set var="faseGara" value='${faseGara}' />
	</c:when>
	<c:otherwise>
		<c:set var="faseGara" value="${param.faseGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty ribcal}'>
		<c:set var="ribcal" value='${ribcal}' />
	</c:when>
	<c:otherwise>
		<c:set var="ribcal" value="${param.ribcal}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty stepWizard}'>
		<c:set var="stepWizard" value='${stepWizard}' />
	</c:when>
	<c:otherwise>
		<c:set var="stepWizard" value="${param.stepWizard}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty isPrequalifica}'>
		<c:set var="isPrequalifica" value='${isPrequalifica}' />
	</c:when>
	<c:otherwise>
		<c:set var="isPrequalifica" value="${param.isPrequalifica}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${!empty bloccoAmmgar}'>
		<c:set var="bloccoAmmgar" value='${bloccoAmmgar}' />
	</c:when>
	<c:otherwise>
		<c:set var="bloccoAmmgar" value="${param.bloccoAmmgar}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${!empty onsogrib}'>
		<c:set var="onsogrib" value='${onsogrib}' />
	</c:when>
	<c:otherwise>
		<c:set var="onsogrib" value="${param.onsogrib}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty riboepvVis}'>
		<c:set var="riboepvVis" value='${riboepvVis}' />
	</c:when>
	<c:otherwise>
		<c:set var="riboepvVis" value="${param.riboepvVis}" />
	</c:otherwise>
</c:choose>

<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction", pageContext,numeroGara,codiceGara,codiceDitta)}' />

<gene:template file="popup-template.jsp">

<c:choose>
	<c:when test='${isPrequalifica eq "true"}'>
		<c:set var="msg" value= "Confermi il calcolo della conformità della ditta sui singoli lotti?"/>
		<gene:setString name="titoloMaschera" value="Calcolo conformità della ditta ${nomimo}"/>
		
	</c:when>
	<c:otherwise>
		<gene:setString name="titoloMaschera" value="Calcolo importo offerto della ditta ${nomimo}"/>
		<c:choose>
			<c:when test='${isGaraLottiConOffertaUnica ne "true"}'>
				<c:choose>
					<c:when test="${ faseGara < 7 && (ribcal == 2 || riboepvVis eq 'true' )}">
						<c:set var="msg" value= "Confermi l'aggiornamento dell'importo offerto della ditta con l'importo derivante dal dettaglio prezzi e l'aggiornamento del corrispondente ribasso percentuale?"/>
					</c:when>
					<c:when test="${faseGara >= 7 && (ribcal == 2 || riboepvVis eq 'true' ) }">
						<c:set var="msg" value= "Confermi l'aggiornamento dell'importo offerto della ditta con l'importo derivante dal dettaglio prezzi?<br>Il corrispondente ribasso percentuale non viene aggiornato perchè la gara risulta già aggiudicata o in fase di aggiudicazione"/>
					</c:when>
					<c:otherwise>
						<c:set var="msg" value= "Confermi l'aggiornamento dell'importo offerto della ditta con l'importo derivante dal dettaglio prezzi?"/>
					</c:otherwise>
				</c:choose>	
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${ faseGara < 7 && ribcal == 2 }">
						<c:set var="msg" value= "Confermi l'aggiornamento dell'importo offerto della ditta sui singoli lotti con l'importo derivante dal dettaglio prezzi e l'aggiornamento del corrispondente ribasso percentuale?"/>
					</c:when>
					<c:when test="${faseGara >= 7 && ribcal == 2 }">
						<c:set var="msg" value= "Confermi l'aggiornamento dell'importo offerto della ditta sui singoli lotti con l'importo derivante dal dettaglio prezzi?<br>Il corrispondente ribasso percentuale non viene aggiornato perchè la gara risulta già aggiudicata o in fase di aggiudicazione"/>
					</c:when>
					<c:otherwise>
						<c:set var="msg" value= "Confermi l'aggiornamento dell'importo offerto della ditta sui singoli lotti con l'importo derivante dal dettaglio prezzi?"/>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>	
	
	<gene:redefineInsert name="corpo">
		<c:set var="modo" value="NUOVO" scope="request" />
		<gene:formScheda entita="GARE" where="GARE.NGARA = '1'" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopupAggiornaImportoOfferto" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAggiornaImportoOfferto">
			<table class="dettaglio-notab">
				<tr >
					<td colSpan="2">
								<br>
									${msg }
									<c:if test="${ !empty messaggioControlloImporto}">
										<br><br>${messaggioControlloImporto }
									</c:if>
								&nbsp;<br>&nbsp;<br>
								&nbsp;</td>
				</tr>
			</table>
			
			<input type="hidden" name="totaleOfferto" value="${totaleOfferto}" />
			<input type="hidden" name="numeroGara" value="${numeroGara}" />
			<input type="hidden" name="codiceGara" value="${codiceGara}" />
			<input type="hidden" name="codiceDitta" value="${codiceDitta}" />
			<input type="hidden" name="dittaAgg" value="${dittaAgg}" />
			<input type="hidden" name="sicinc" value="${sicinc}" />
			<input type="hidden" name="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
			<input type="hidden" name="faseGara" value="${faseGara}" />
			<input type="hidden" name="ribcal" value="${ribcal}" />
			<input type="hidden" name="stepWizard" value="${stepWizard}" />
			<input type="hidden" name="isPrequalifica" value="${isPrequalifica}" />
			<input type="hidden" name= "bloccoAmmgar" value="${bloccoAmmgar }" />
			<input type="hidden" name= "onsogrib" value="${onsogrib }" />
			<input type="hidden" name= "sbiancareAggiudicazione" value="${sbiancareAggiudicazione }" />
			<input type="hidden" name= "riboepvVis" value="${riboepvVis }" />
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:schedaConferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>
	<gene:javaScript> 
		document.forms[0].jspPathTo.value="gare/commons/popupAggiornaImportoOfferto.jsp";
		
		function annulla(){
			window.close();
		}
		
		
	</gene:javaScript>
</gene:template>

</div>

</c:otherwise>
</c:choose>