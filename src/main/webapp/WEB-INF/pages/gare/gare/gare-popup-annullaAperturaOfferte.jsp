<%
	/*
	 * Created on 06-05-2019
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

<%//Popup per annullare l'apertura delle offerte %>


<c:choose>
		<c:when test='${not empty param.bustalotti}'>
			<c:set var="bustalotti" value='${param.bustalotti}' />
		</c:when>
		<c:otherwise>
			<c:set var="bustalotti" value="${bustalotti}" />
		</c:otherwise>
	</c:choose>

<c:choose>
	<c:when test='${RISULTATO eq "CALCOLOESEGUITO"}' >
		<script type="text/javascript">
			<c:choose>
				<c:when test="${bustalotti eq '2' }">	
					window.opener.selezionaPagina(eval(window.opener.document.forms[0].activePage.value));
					window.close();
				</c:when>
				<c:otherwise>
					window.opener.selezionaPagina(eval(window.opener.document.forms[0].activePage.value) - 1);
					window.close();
				</c:otherwise>
			</c:choose>
		</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<c:choose>
		<c:when test='${not empty param.ngara }'>
			<c:set var="ngara" value='${param.ngara}' />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${ngara}" />
		</c:otherwise>
	</c:choose>
	
	${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlliAnnulaAperturaOfferteFunction", pageContext, ngara, "6",bustalotti)}

	<gene:setString name="titoloMaschera" value="Annulla apertura offerte" />
	
	<gene:redefineInsert name="corpo">
				
		<c:set var="modo" value="MODIFICA" scope="request" />

		<gene:formScheda entita="GARE" where="GARE.NGARA = '${ngara}'" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAnnullaAperturaOfferte">
			<gene:campoScheda>
				<c:choose>
					<c:when test="${esistonoLottiInFase7 eq 'true'}">
					<td colSpan="2"><br>Non è possibile procedere perchè ci sono dei lotti per cui è già stato attivato il calcolo aggiudicazione<br><br></td>
					</c:when>
					<c:when test="${esistonoBusteTecParziali eq 'true'}">
					<td colSpan="2"><br>Non è possibile procedere perchè ci sono ditte in gara con la busta tecnica acquisita parzialmente (solo sezione tecnico-qualitativa)<br><br></td>
					</c:when>
					<c:otherwise>
						<td colSpan="2"><br>Viene annullata l'apertura delle offerte e ripristinata la fase di gara 'Apertura documentazione amministrativa', riabilitando in tale fase la modifica dei dati.<br><br>
						Confermi l'operazione?<br><br>
						</td>
					</c:otherwise>
				</c:choose>
				
			</gene:campoScheda>

			<gene:campoScheda campo="CODGAR1" visibile="false" />
			<gene:campoScheda campo="NGARA" visibile="false" />
			<gene:campoScheda campo="MODLICG" visibile="false" />
			<gene:campoScheda campo="FASGAR" visibile="false" />
										
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
					<c:if test="${esistonoLottiInFase7 ne 'true' and esistonoBusteTecParziali ne 'true'}">
						<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
					</c:if>
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</gene:campoScheda>
			<input type="hidden" name="bustalotti" id="bustalotti" value="${bustalotti}" />
			<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		</gene:formScheda>
		
	</gene:redefineInsert>
	
	<gene:javaScript> 
	
		document.forms[0].jspPathTo.value="gare/gare/gare-popup-annullaAperturaOfferte.jsp";
	
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