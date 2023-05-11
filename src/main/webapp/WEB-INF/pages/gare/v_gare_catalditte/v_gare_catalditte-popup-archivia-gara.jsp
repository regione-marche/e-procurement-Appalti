<%
/*
 * Created on: 29-07-2010
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
		Popup per archiviazione di una gara per albo fornitori
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div style="width:97%;">
	
	<gene:template file="popup-template.jsp">
	<gene:setString name="titoloMaschera" value="Archiviazione dell'elenco" />
	
	<c:set var="modo" value="MODIFICA" scope="request" />
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="GAREALBO" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupArchiviaAlboFornitore">
			<c:choose>
				<c:when test='${RISULTATO eq "OPERAZIONEESEGUITA"}'>
					<gene:campoScheda>
						<td colspan="2">
						<br>
						L'elenco selezionato è stato archiviato.
						<br><br>
						</td>
					</gene:campoScheda>
				</c:when>
				
				<c:otherwise>
					<gene:campoScheda>
						<td colspan="2">
						<br>
						Confermi l'archiviazione dell'elenco selezionato ?
						<br><br>
						</td>
					</gene:campoScheda>
				</c:otherwise>
			</c:choose>

			<gene:campoScheda campo="CODGAR" visibile="false" />
			<gene:campoScheda campo="NGARA" visibile="false" />
			<gene:campoScheda campo="ISARCHI" visibile="false" />
			<gene:campoScheda entita="V_GARE_ELEDITTE" campo="CODGAR" where="V_GARE_ELEDITTE.CODGAR=GAREALBO.CODGAR" visibile="false"/>
			<gene:campoScheda entita="V_GARE_ELEDITTE" campo="CODICE" where="V_GARE_ELEDITTE.CODICE=GAREALBO.NGARA" visibile="false" modificabile="false"/>
			<gene:campoScheda entita="V_GARE_ELEDITTE" campo="OGGETTO" visibile="false" modificabile="false"/>
		
			<c:choose>
				<c:when test='${!empty RISULTATO}'>
					<gene:campoScheda>
						<td class="comandi-dettaglio" colSpan="2">
							<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					</gene:campoScheda>
				</c:when>
				<c:otherwise>
					<gene:campoScheda>
						<td class="comandi-dettaglio" colSpan="2">
							<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					</gene:campoScheda>
				</c:otherwise>
			</c:choose>
		</gene:formScheda>
 	</gene:redefineInsert>
	<gene:javaScript>
	
		<c:if test='${RISULTATO eq "OPERAZIONEESEGUITA"}'>
			window.opener.historyReload();
			window.close();
		</c:if>
		
		function conferma() {
			document.forms[0].jspPathTo.value="gare/v_gare_eleditte/v_gare_eleditte-popup-archivia-gara.jsp";
			document.forms[0].GAREALBO_ISARCHI.value=1;
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}

	</gene:javaScript>
	
	</gene:template>
</div>