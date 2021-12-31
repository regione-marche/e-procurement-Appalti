<%
/*
 * Created on: 20/04/2010
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
		</c:otherwise>
	</c:choose>

	<gene:setString name="titoloMaschera" value='Copia ditte' />
		
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
			gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupCopiaDitte">
			<gene:campoScheda campo="CODGAR" visibile="false" />	
			<gene:campoScheda campo="LOTTODESTINAZIONE" title="Lotto destinazione"
				modificabile="false" definizione="T20" value="${lottoDestinazione}" campoFittizio="true" visibile="false" />
			<gene:campoScheda campo="LOTTOSORGENTE" title="Lotto sorgente"
				modificabile="false" definizione="T20" value="${lottoSorgente}" campoFittizio="true" visibile="false" />
		</gene:formScheda>

		<table class="dettaglio-notab">
			<tr>
				<td colspan="2">
					<c:choose>
						<c:when test="${!empty RISULTATO and RISULTATO eq 'COPIAESEGUITA'}">
							<br>
							La lista delle ditte &egrave; stata copiata correttamente nel lotto <b>${lottoDestinazione}</b>.
							<br>
				  			<br>
						</c:when>
						<c:otherwise>
							Mediante questa funzione è possibile copiare la lista delle ditte del lotto <b>${lottoSorgente}</b>
							in un altro lotto della gara.
							<br><br>
							Scegliere il lotto in cui copiare la lista delle ditte.
							<br>
							<b>Attenzione: la lista originale delle ditte del lotto indicato verr&agrave; cancellata</b>
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
						<INPUT type="button" class="bottone-azione" value="Copia ditte" title="Copia ditte" onclick="javascript:confermaCopiaDitte();">
					</c:if>
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
				
			</tr>
		</table>

  	</gene:redefineInsert>

	<gene:javaScript>

		document.forms[0].jspPathTo.value="gare/gare/gare-popup-copia-ditte.jsp";
		
		function confermaCopiaDitte(){
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
