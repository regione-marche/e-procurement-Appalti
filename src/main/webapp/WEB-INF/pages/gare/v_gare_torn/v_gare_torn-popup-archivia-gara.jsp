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

<c:choose>
	<c:when test="${!empty param.isCatalogo }">
		<c:set var="isCatalogo" value="${param.isCatalogo }"/>
	</c:when>
	<c:otherwise>
		<c:set var="isCatalogo" value="${isCatalogo }"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${fn:containsIgnoreCase(key, "TORN")}'>
		<c:set var="entita" value="TORN"/>
		<c:set var="titolo" value="Archiviazione della gara"/>
		<c:set var="msgConferma" value="Confermi l'archiviazione della gara selezionata?"/>
		<c:set var="msgOk" value="La gara selezionata è stata archiviata."/>
	</c:when>
	<c:when test='${fn:containsIgnoreCase(key, "GAREAVVISI")}'>
		<c:set var="entita" value="GAREAVVISI"/>
		<c:set var="titolo" value="Archiviazione dell'avviso"/>
		<c:set var="msgConferma" value="Confermi l'archiviazione dell'avviso selezionato?"/>
		<c:set var="msgOk" value="L'avviso selezionato è stato archiviato."/>
	</c:when>
	<c:when test='${fn:containsIgnoreCase(key, "MERIC")}'>
		<c:set var="entita" value="MERIC"/>
		<c:set var="titolo" value="Archiviazione della ricerca di mercato"/>
		<c:set var="msgConferma" value="Confermi l'archiviazione della ricerca di mercato selezionata?"/>
		<c:set var="msgOk" value="La ricerca di mercato selezionata è stata archiviata."/>
	</c:when>
	<c:when test='${fn:containsIgnoreCase(key, "GAREALBO")}'>
		<c:choose>
			<c:when test="${isCatalogo eq 1 }">
				<c:set var="entita" value="GAREALBO"/>
				<c:set var="titolo" value="Archiviazione del catalogo"/>
				<c:set var="msgConferma" value="Confermi l'archiviazione del catalogo selezionato?"/>
				<c:set var="msgOk" value="Il catalogo selezionato è stato archiviato."/>
			</c:when>
			<c:otherwise>
				<c:set var="entita" value="GAREALBO"/>
				<c:set var="titolo" value="Archiviazione dell'elenco"/>
				<c:set var="msgConferma" value="Confermi l'archiviazione dell'elenco selezionato?"/>
				<c:set var="msgOk" value="L'elenco selezionato è stato archiviato."/>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:when test='${fn:containsIgnoreCase(key, "G1STIPULA")}'>
		<c:set var="entita" value="G1STIPULA"/>
		<c:set var="titolo" value="Archiviazione della stipula"/>
		<c:set var="msgConferma" value="Confermi l'archiviazione della stipula selezionata?"/>
		<c:set var="msgOk" value="La stipula selezionata è stata archiviata."/>
	</c:when>
</c:choose>

<div style="width:97%;">
	
	<gene:template file="popup-template.jsp">
	<gene:setString name="titoloMaschera" value='${titolo }' />
	
	<c:set var="modo" value="MODIFICA" scope="request" />
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="${entita }" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupArchiviaGara">
			<c:choose>
				<c:when test='${RISULTATO eq "OPERAZIONEESEGUITA"}'>
					<gene:campoScheda>
						<td colspan="2">
						<br>
						${msgOk }
						<br><br>
						</td>
					</gene:campoScheda>
				</c:when>
				
				<c:otherwise>
					<gene:campoScheda>
						<td colspan="2">
						<br>
						${msgConferma }
						<br><br>
						</td>
					</gene:campoScheda>
				</c:otherwise>
			</c:choose>
				
			<c:if test='${!(fn:containsIgnoreCase(key, "MERIC") || fn:containsIgnoreCase(key, "G1STIPULA"))}'>
				<gene:campoScheda campo="CODGAR" visibile="false" />
			</c:if>
			<gene:campoScheda campo="ISARCHI" visibile="false" />
			<c:choose>
				<c:when test='${fn:containsIgnoreCase(key, "TORN")}'>
					<gene:campoScheda entita="V_GARE_TORN" campo="CODGAR" where="V_GARE_TORN.CODGAR = TORN.CODGAR" visibile="false"/>
					<gene:campoScheda entita="V_GARE_TORN" campo="CODICE" visibile="true" modificabile="false"/>
					<gene:campoScheda entita="V_GARE_TORN" campo="OGGETTO" visibile="true" modificabile="false"/>
				</c:when>
				<c:when test='${fn:containsIgnoreCase(key, "GAREAVVISI")}'>
					<gene:campoScheda campo="NGARA" modificabile="false" />
					<gene:campoScheda campo="OGGETTO"  modificabile="false"/>
				</c:when>
				<c:when test='${fn:containsIgnoreCase(key, "MERIC")}'>
					<gene:campoScheda campo="ID" visibile="false" />
					<gene:campoScheda campo="CODRIC" modificabile="false" />
					<gene:campoScheda campo="OGGETTO"  modificabile="false"/>
				</c:when>
				<c:when test='${fn:containsIgnoreCase(key, "GAREALBO")}'>
					<gene:campoScheda campo="NGARA" visibile="false" />
					<gene:campoScheda entita="V_GARE_ELEDITTE" campo="CODGAR" where="V_GARE_ELEDITTE.CODGAR=GAREALBO.CODGAR" visibile="false"/>
					<gene:campoScheda entita="V_GARE_ELEDITTE" campo="CODICE" where="V_GARE_ELEDITTE.CODICE=GAREALBO.NGARA" visibile="false" modificabile="false"/>
					<gene:campoScheda entita="V_GARE_ELEDITTE" campo="OGGETTO" visibile="false" modificabile="false"/>
				</c:when>
				<c:when test='${fn:containsIgnoreCase(key, "G1STIPULA")}'>
					<gene:campoScheda campo="ID" visibile="false" />
				</c:when>
			</c:choose>
					
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
			<input type="hidden" id="isCatalogo" value="isCatalogo"/>
		</gene:formScheda>
  	</gene:redefineInsert>

	<gene:javaScript>
	
		<c:choose>
			<c:when test='${fn:containsIgnoreCase(key, "TORN")}'>
				showObj("jsPopUpV_GARE_TORN_CODICE", false);
				showObj("jsPopUpV_GARE_TORN_OGGETTO", false);
			</c:when>
			<c:when test='${fn:containsIgnoreCase(key, "GAREAVVISI")}'>
				showObj("jsPopUpGAREAVVISI_NGARA", false);
				showObj("jsPopUpGAREAVVISI_OGGETTO", false);
			</c:when>
			<c:when test='${fn:containsIgnoreCase(key, "MERIC")}'>
				showObj("jsPopUpMERIC_CODRIC", false);
				showObj("jsPopUpMERIC_OGGETTO", false);
			</c:when>
		</c:choose>
		
		
		<c:if test='${RISULTATO eq "OPERAZIONEESEGUITA"}'>
			window.opener.historyReload();
			window.close();
		</c:if>
		
		function conferma() {
			document.forms[0].jspPathTo.value="gare/v_gare_torn/v_gare_torn-popup-archivia-gara.jsp";
			<c:choose>
				<c:when test='${fn:containsIgnoreCase(key, "TORN")}'>
					document.forms[0].TORN_ISARCHI.value=1;
				</c:when>
				<c:when test='${fn:containsIgnoreCase(key, "GAREAVVISI")}'>
					document.forms[0].GAREAVVISI_ISARCHI.value=1;
				</c:when>
				<c:when test='${fn:containsIgnoreCase(key, "MERIC")}'>
					document.forms[0].MERIC_ISARCHI.value=1;
				</c:when>
				<c:when test='${fn:containsIgnoreCase(key, "G1STIPULA")}'>
					document.forms[0].G1STIPULA_ISARCHI.value=1;
				</c:when>
				<c:when test='${fn:containsIgnoreCase(key, "GAREALBO")}'>
					document.forms[0].GAREALBO_ISARCHI.value=1;
				</c:when>
			</c:choose>
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}

	</gene:javaScript>
	
	</gene:template>
</div>
