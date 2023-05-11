<%
/*
 * Created on: 31-08-2010
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
		Finestra per l'attivazione della funzione 'Pubblica su portale Alice Gare'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="vuota" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetListaStruttureATCFunction", pageContext)}'/>

<c:set var="modo" value="NUOVO" scope="request" />

	<gene:setString name="titoloMaschera" value='Allineamento dati su sito istituzionale ATC' />
		
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAggionaDatiPubblicatiSuPortaleATC">
			
	<gene:campoScheda nome="msgPagina">
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${empty requestScope.allineamentoEseguito or requestScope.allineamentoEseguito eq "OK" or requestScope.allineamentoEseguito eq "NO-Controlli" }' >
					<c:if test='${requestScope.allineamentoEseguito eq "OK" }' >
						<b>Allineamento dati per la gara ${requestScope.gara } completato.</b>
						<br><br>
					</c:if>
					Mediante questa funzione è possibile aggiornare sul sito istituzionale ATC i dati delle gare pubblicate, allineandoli con quelli correnti.<br>
					Selezionare la gara da aggiornare e specificare la relativa struttura:
					
				</c:when>
				<c:otherwise>
					L'allineamento dati per la gara ${requestScope.gara } si è concluso con errori:<br>
					<c:choose>
						<c:when test='${requestScope.allineamentoEseguito eq "NOK-BANDO" }'>
							Si è presentato un errore nell'allineamento dei dati della pubblicazione del bando
						</c:when>
						<c:when test='${requestScope.allineamentoEseguito eq "NOK-ESITO" }'>
							Si è presentato un errore nell'allineamento dei dati della pubblicazione dell'esito
						</c:when>
						<c:otherwise>
							Si è presentato un errore nell'allineamento dei dati della pubblicazione sia del bando che dell'esito
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			
		</td>
	</gene:campoScheda>
	<c:if test="${requestScope.allineamentoEseguito eq 'OK' or empty requestScope.allineamentoEseguito}">
		<gene:archivio titolo="gare pubblicate su sito istituzionale ATC"
			obbligatorio="true" 
			lista='gare/commons/gare-popup-selezionaGarePubblicateATC.jsp' 
			scheda="" 
			schedaPopUp="" 
			campi="V_GARE_TORN.CODGAR;V_GARE_TORN.CODICE;V_GARE_TORN.GENERE"
			functionId="skip" 
			chiave="GARE.CODGAR1"
			inseribile="false"
			formName="formArchivioPubblicazioniATC">
			<gene:campoScheda campo="CODGAR1" visibile="false" />
			<gene:campoScheda campo="NGARA"   obbligatorio="true" />
			<gene:campoScheda campo="GENERE"   visibile="false"/>
		</gene:archivio>
		<gene:campoScheda >
			<tr >
				<td class="etichetta-dato">Struttura (*)</td>
				<td class="valore-dato">
					<select id="strutturaATC" name="strutturaATC" style="width:350px;">
						<option value=""></option>
						<c:forEach items="${requestScope.listaStruttureATC}" step="1" var="strutturATC" varStatus="status" >
							 <option value="${strutturATC[0]}">${strutturATC[1]}</option>
						</c:forEach>
					</select>
				</td>						
			</tr>
			
		</gene:campoScheda>		
	</c:if>
	
	</gene:formScheda>
  </gene:redefineInsert>

	
	<gene:redefineInsert name="buttons">
			<c:choose>
				<c:when test='${not empty requestScope.allineamentoEseguito && requestScope.allineamentoEseguito ne "OK"}'>
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:window.close();">&nbsp;
				</c:when>
				<c:otherwise>
					<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">&nbsp;
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:window.close();">&nbsp;
				</c:otherwise>
			</c:choose>
	</gene:redefineInsert>

	
	
	<gene:javaScript>
		document.forms[0].jspPathTo.value="gare/commons/popup-allinementoDatiATC.jsp";		
		function conferma(){
			
			var codice= getValue("GARE_NGARA");
			if(codice== null || codice==""){
				alert("Specificare la gara");
				return;
			}
			
			var strutturaATC = getValue("strutturaATC");
			if(strutturaATC== null || strutturaATC==""){
				alert("Specificare la struttura");
				return;
			}
			
			schedaConferma();
		}
	</gene:javaScript>
</gene:template>
</div>

	