<%
	/*
	 * Created on 03-nov-2009
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%//Popup per annullare l'aggiudicazione definitiva%>

<c:choose>
	<c:when test='${RISULTATO eq "CALCOLOESEGUITO"}' >
		<script type="text/javascript">
				opener.historyReload();
				window.close();
		</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<c:choose>
		<c:when test='${RISULTATO != null}'>
			<c:set var="ngara" value='${NGARA}' />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${fn:substringAfter(param.ngara,':')}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.isGaraLottiConOffertaUnica}'>
			<c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
		</c:when>
		<c:otherwise>
			<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
		</c:otherwise>
	</c:choose>
	
	<c:set var="ditta" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDittaFunction", pageContext, ngara)}'/>
	
	<c:set var="esisteVariazionePrezzo" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.EsistonoVariazioniPrezzoFunction",  pageContext, ngara, "", "NoDitta","2")}'/>
	
	<c:set var="esisteStipulaGara" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteStipulaGaraFunction",  pageContext, ngara, ditta)}'/>
	
	<gene:setString name="titoloMaschera" value="Annulla aggiudicazione definitiva" />
	
	<gene:redefineInsert name="corpo">
		<c:set var="chiave" value="${ngara}" />
		
		<c:choose>
			<c:when test='${RISULTATO eq "CALCOLOESEGUITO"}'>
				<c:set var="modo" value="APRI" scope="request" />	
			</c:when>
			<c:otherwise>
				<c:set var="modo" value="MODIFICA" scope="request" />
			</c:otherwise>
		</c:choose>

		<gene:formScheda entita="GARE" where="GARE.NGARA = '${chiave}'" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAnnullaAggiudicazioneDefinitiva">
			<c:choose>
				<c:when test="${esisteVariazionePrezzo eq 'true' }">
					<gene:campoScheda>
						<td colSpan="2"><br>Non é possibile procedere con l'annullamento dell'aggiudicazione perchè sono state fatte delle variazioni prezzo successivamente all'aggiudicazione
							<br>&nbsp;<br>
						</td>
					</gene:campoScheda>
				</c:when>
				<c:when test="${RISULTATO eq 'VARIAZIONI_PREZZO' }">
					<gene:campoScheda>
						<td colSpan="2"><br>${msgErrore }
							<br>&nbsp;<br>
						</td>
					</gene:campoScheda>
				</c:when>
				<c:when test='${empty ditta or ditta eq ""}'>
					<gene:campoScheda>
						<td colSpan="2"><br>La gara non risulta aggiudicata in via definitiva 
							<br>&nbsp;<br>
						</td>
					</gene:campoScheda>
				</c:when>
				<c:when test="${esisteStipulaGara eq 'true' }">
					<gene:campoScheda>
						<td colSpan="2"><br>Non é possibile procedere con l'annullamento dell'aggiudicazione perchè c'è un contratto in fase di stipula o già attivo 
							<br>&nbsp;<br>
						</td>
					</gene:campoScheda>
				</c:when>
				<c:otherwise>
					<gene:campoScheda>
						<td colSpan="2"><br>Viene annullata l'aggiudicazione definitiva, ripristinando la gara  
						alla fase di 'Proposta di aggiudicazione' e riabilitando in tale fase 
						il calcolo dell'aggiudicazione.<br><br>
						Confermi l'operazione?<br><br>
						</td>
					</gene:campoScheda>
					
									
				</c:otherwise>
			</c:choose>

			<gene:campoScheda campo="CODGAR1" visibile="false" />
			<gene:campoScheda campo="NGARA" visibile="false" />
			<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica }" />							
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
					<c:if test='${!empty ditta and ditta ne "" and RISULTATO ne "VARIAZIONI_PREZZO" and esisteVariazionePrezzo ne "true" and esisteStipulaGara ne "true"}'>
						<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
					</c:if>
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</gene:campoScheda>
			
		</gene:formScheda>
		
	</gene:redefineInsert>
	
	<gene:javaScript> 
	
		document.forms[0].jspPathTo.value="gare/gare/gare-popup-annulla-aggiudicazioneDefinitiva.jsp";
	
	    function annulla(){
			window.close();
		}
		
		function conferma(){
			schedaConferma();
		}
		
	</gene:javaScript>
</gene:template>

</div>

</c:otherwise>
</c:choose>