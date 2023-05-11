<%
/*
 * Created on: 17-dic-2008
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
		Finestra che visualizza la conferma per l'inserimento delle pubblicazioni bando/esito predefinite
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div style="width:97%;">

<gene:template file="popup-template.jsp">
	
	<c:choose>
		<c:when test='${!empty RISULTATO}'>
			<c:set var="codgar" value='${CODGAR}' />
			<c:set var="lottoSorgente" value='${LOTTOSORGENTE}' />
			<c:set var="lottoDestinazione" value='${LOTTODESTINAZIONE}' />
		</c:when>
		<c:otherwise>
			<c:set var="codgar" value="${param.codgar}" />
			<c:set var="lottoSorgente" value="${param.lottoSorgente}" />
			<c:set var="lottoDestinazione" value="" />
			${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, codgar, "SC", "21")}
			${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, lottoSorgente, "SC", "20")}
		</c:otherwise>
	</c:choose>

	<c:set var="esistonoComponenti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.ConteggioComponentiCommissioneFunction",pageContext,lottoSorgente)}' />

	<gene:setString name="titoloMaschera" value='Copia componenti della commissione' />
		
	<gene:redefineInsert name="corpo">
	
		<c:choose>
			<c:when test='${RISULTATO eq "COPIAESEGUITA"}'>
				<c:set var="modo" value="APRI" scope="request" />	
			</c:when>
			<c:otherwise>
				<c:set var="modo" value="MODIFICA" scope="request" />
			</c:otherwise>
		</c:choose>
	
		<gene:formScheda entita="TORN" where="TORN.CODGAR = '${codgar}'"
			gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupCopiaCommissione">
			<gene:campoScheda campo="CODGAR" visibile="false"/>	
			<gene:campoScheda campo="LOTTODESTINAZIONE" title="Lotto destinazione"
				modificabile="false" definizione="T20" value="${lottoDestinazione}" campoFittizio="true" visibile="false" />
			<gene:campoScheda campo="LOTTOSORGENTE" title="Lotto sorgente"
				modificabile="false" definizione="T20" value="${lottoSorgente}" campoFittizio="true" visibile="false" />
		</gene:formScheda>

		<table class="dettaglio-notab">
			
			<c:choose>
				<c:when test="${esistonoComponenti eq 'FALSE'}">
					<tr>
						<td colspan="2">
							<br>
							La lista dei componenti della commissione del lotto <b>${lottoSorgente}</b> è vuota.
							<br>
							Non è possibile procedere con la copia.
							<br>
							<br>
						</td>
					</tr>
					<tr>
						<td class="comandi-dettaglio" colSpan="2">
							<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					</tr>
				</c:when>
			
				<c:otherwise>
					<tr>
						<td colspan="2">
							<c:choose>
								<c:when test="${!empty RISULTATO and RISULTATO eq 'COPIAESEGUITA'}">
									<br>
									I componenti della commissione sono stati copiati correttamente nel lotto <b>${lottoDestinazione}</b>.
									<br>
						  			<br>
								</c:when>
								<c:otherwise>
									Mediante questa funzione è possibile copiare i componenti della commissione del lotto <b>${lottoSorgente}</b>
									in un altro lotto della gara.
									<br><br>
									Scegliere il lotto in cui copiare i componenti della commissione.
									<br>
									<b>Attenzione: la lista originale dei componenti della commissione del lotto indicato verr&agrave; cancellata</b>
									<br>				
									<gene:formLista entita="GARE" sortColumn="2" where="GARE.CODGAR1 = '${codgar}' AND GARE.DITTA IS NULL AND GARE.NGARA NOT IN ('${lottoSorgente}')">
										<gene:campoLista title="Scegli" width="40">
											<center><input type="radio" name="lottoSelezionabile" value="${datiRiga.GARE_NGARA}" /></center>
										</gene:campoLista>
										<gene:campoLista campo="CODGAR1" visibile="false" ordinabile="false"/>
										<gene:campoLista campo="NGARA" title="Codice lotto" ordinabile="false" width="110"/>	
										<gene:campoLista campo="CODIGA" title="Lotto" width="65" ordinabile="false"/>
										<gene:campoLista campo="NOT_GAR" ordinabile="false"/>
									</gene:formLista>
									
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					
					<tr>
						<td class="comandi-dettaglio" colSpan="2">
							<c:if test="${empty RISULTATO}" >
								<INPUT type="button" class="bottone-azione" value="Copia commissione" title="Copia commissione" onclick="javascript:confermaCopiaCommissione();">
							</c:if>
							<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
						
					</tr>
				</c:otherwise>			
			</c:choose>
			
		</table>

  	</gene:redefineInsert>

	<gene:javaScript>

		document.forms[0].jspPathTo.value="gare/gare/gare-popup-copia-commissione.jsp";
		
		function confermaCopiaCommissione(){
			if (document.forms[1].lottoSelezionabile) {
				var controllo = false;
				var radio = document.forms[1].lottoSelezionabile;
				
				if (radio.checked) {
					controllo=true;
   					document.forms[0].LOTTODESTINAZIONE.value=radio.value;
   					
				} else {
					for(i=0; i < radio.length; i++) {
	 					if(radio[i].checked) {
	   						controllo=true;
	   						document.forms[0].LOTTODESTINAZIONE.value=radio[i].value;
							break;
	 					}
					}
				}
	
				if(!controllo) {
	 				alert("Selezionato un lotto");
				}
				
				if (controllo) {
					schedaConferma();
				}
			}
		}

		
		function annulla(){
			window.close();
		}

	</gene:javaScript>
	
	</gene:template>
</div>
