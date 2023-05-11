<%
/*
 * Created on: 25-10-2010
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
		Finestra per l'attivazione della funzione 'Acquisisci da portale'
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
<gene:setString name="titoloMaschera" value='Acquisizione da portale Appalti' />

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.registraImpr}'>
		<c:set var="registraImpr" value="${param.registraImpr}" />
	</c:when>
	<c:otherwise>
		<c:set var="registraImpr" value="${registraImpr}" />
	</c:otherwise>
</c:choose>

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="IMPR" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAcquisisciDaPortale">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${requestScope.erroreAcquisizione eq "1"}'>
					&nbsp;Ci sono stati degli errori durante l'acquisizione.${requestScope.msg}
				</c:when>
				<c:otherwise>
					&nbsp;Nel contesto di tale operazione saranno considerati anche gli aggiornamenti <br>
					&nbsp;anagrafici in maniera automatica, senza richiesta di conferma.<br>
					&nbsp;Confermi l'acquisizione dal portale Appalti?
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
		
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="WIZARD_PAGINA_ATTIVA" id="WIZARD_PAGINA_ATTIVA" value="" />
		<input type="hidden" name="registraImpr" id="registraImpr" value="${registraImpr}" />
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${requestScope.erroreAcquisizione eq "1"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
	</gene:redefineInsert>
</c:if>
	
	<gene:javaScript>
		
		setValue("WIZARD_PAGINA_ATTIVA", window.opener.getValue("WIZARD_PAGINA_ATTIVA"));
		
		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/popupAcquisisciDaPortale.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
		function chiudi(){
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();	
		}
		
	

	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>