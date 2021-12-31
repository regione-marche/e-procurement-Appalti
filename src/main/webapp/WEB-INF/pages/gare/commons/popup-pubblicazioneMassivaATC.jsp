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

	<gene:setString name="titoloMaschera" value='Pubblicazione massiva sul sito istituzionale ATC' />
		
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="CATG" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePubblicaSuPortaleATC">
			
	<gene:campoScheda nome="msgPagina">
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${empty requestScope.pubblicazioneEseguita }' >
					Confermi la pubblicazione massiva sul sito istituzionale ATC dei bandi/esiti pubblicati?
				</c:when>
				<c:otherwise>
					Elaborazione conclusa.<br>
					<c:if test="${ not empty requestScope.messaggi}">
					Di seguito sono riportati gli errori riscontrati
					</c:if>
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	
	<gene:campoScheda campo="NGARA" visibile="false" defaultValue="$#########"/>
	<gene:campoScheda campo="NCATG" visibile="false" defaultValue="1000"/>
	
	<c:choose>
		<c:when test='${not empty requestScope.pubblicazioneEseguita and requestScope.pubblicazioneEseguita eq "1"}'>
			<gene:campoScheda visibile="${ not empty requestScope.messaggi}">
				<td colSpan="2">
					<textarea cols="95" rows="14" readonly="readonly">${requestScope.messaggi}</textarea>
				</td>
			</gene:campoScheda>
		</c:when>
		<c:otherwise>
			<gene:campoScheda >
				<td colSpan="2">
					<tr>
						<td colspan="2">
							<b>Dati per pubblicazione su sito istituzionale</b>
						</td>
					</tr>
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
				</td>
			</gene:campoScheda>
		</c:otherwise>
	</c:choose>
	
	</gene:formScheda>
  </gene:redefineInsert>

	
	<gene:redefineInsert name="buttons">
			<c:choose>
				<c:when test='${not empty requestScope.pubblicazioneEseguita and requestScope.pubblicazioneEseguita eq "1"}'>
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:window.close();">&nbsp;
				</c:when>
				<c:otherwise>
					<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">&nbsp;
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:window.close();">&nbsp;
				</c:otherwise>
			</c:choose>
	</gene:redefineInsert>

	
	
	<gene:javaScript>
				
		function conferma(){
			var strutturaATC = getValue("strutturaATC");
			if(strutturaATC== null || strutturaATC==""){
				alert("Specificare la struttura");
				return;
			}
			document.forms[0].jspPathTo.value="gare/commons/popup-pubblicazioneMassivaATC.jsp";
			schedaConferma();
		}
	</gene:javaScript>
</gene:template>
</div>

	