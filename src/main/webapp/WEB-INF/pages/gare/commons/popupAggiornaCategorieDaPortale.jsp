<%
/*
 * Created on: 27-01-2017
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
		Finestra per l'acquisizione puntale degli aggiornamenti delle categorie presenti nei messaggi FS4
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.acquisizioneEseguita and requestScope.acquisizioneEseguita eq "1"}' >
<script type="text/javascript">
		window.opener.document.forms[0].pgSort.value = "";
		window.opener.document.forms[0].pgLastSort.value = "";
		window.opener.document.forms[0].pgLastValori.value = "";
		window.opener.bloccaRichiesteServer();
		window.opener.listaVaiAPagina(0);
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">


<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codiceDitta}'>
		<c:set var="codiceDitta" value="${param.codiceDitta}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceDitta" value="${codiceDitta}" />
	</c:otherwise>
</c:choose>

<c:set var="messaggio" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetVariazioniAggiornamentiCategorieSospesoFunction", pageContext, ngara,codiceDitta)}' />

<gene:setString name="titoloMaschera" value="Acquisizione modifiche categorie d'iscrizione da portale Appalti" />

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="IMPR" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAcquisizionePuntualeDaPortaleAggCat">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${requestScope.erroreAcquisizione eq "1"}'>
					&nbsp;Ci sono stati degli errori durante l'acquisizione.${requestScope.msg}
				</c:when>
				<c:otherwise>
					&nbsp;Mediante questa funzione si procede all'acquisizione delle modifiche alle categorie d'iscrizione richieste da portale <br>
					&nbsp;da parte dell'operatore.<br>
					&nbsp;E' possibile accettare o rifiutare le modifiche richieste.<br><br>
					&nbsp;Sono presenti le seguenti variazioni:
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	
	
	<gene:campoScheda>
		<td colSpan="2">
			<textarea cols="90" rows="14" readonly="readonly">${messaggio }</textarea>
		</td>
	</gene:campoScheda>	
	
	
		
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="codiceDitta" id="codiceDitta" value="${codiceDitta}" />
		<input type="hidden" name="opScelta" id="opScelta" value="" />
		<input type="hidden" name="identificativo" id="identificativo" value="${requestScope.identificativo}" />
	</gene:formScheda>
  </gene:redefineInsert>


<gene:redefineInsert name="buttons">
	<c:choose>
		<c:when test='${requestScope.erroreAcquisizione eq "1"}'>
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
		</c:when>
		<c:otherwise>
			<INPUT type="button" class="bottone-azione" value="Accetta modifiche" title="Accetta modifiche" onclick="javascript:conferma()">&nbsp;
			<INPUT type="button" class="bottone-azione" value="Rifiuta modifiche" title="Rifiuta modifiche" onclick="javascript:rifiuta()">&nbsp;
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:chiudi()">&nbsp;
		</c:otherwise>
	</c:choose>
	
</gene:redefineInsert>

	
	<gene:javaScript>
		
		
		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/popupAggiornaCategorieDaPortale.jsp";
			document.forms[0].opScelta.value="1";
			schedaConferma();
		}
		
		function rifiuta() {
			document.forms[0].jspPathTo.value="gare/commons/popupAggiornaCategorieDaPortale.jsp";
			document.forms[0].opScelta.value="2";
			schedaConferma();
		}
		
		function chiudi(){
			window.close();
		}
		
	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>