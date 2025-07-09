<%
/*
 * Created on: 22-04-2024
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
		Finestra per l'aggiornamento codice ERP 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.ricercaODASuccess and requestScope.ricercaODASuccess eq "1"}' >
		<script type="text/javascript">
			window.opener.bloccaRichiesteServer();
			window.opener.selezionaPagina(0);
			window.close();
		</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Recupera ODA' />

<c:choose>
	<c:when test='${not empty param.idStipula}'>
		<c:set var="idStipula" value="${param.idStipula}" />
	</c:when>
	<c:otherwise>
		<c:set var="idStipula" value="${idStipula}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.codStipula}'>
		<c:set var="codStipula" value="${param.codStipula}" />
	</c:when>
	<c:otherwise>
		<c:set var="codStipula" value="${codStipula}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.idPadre}'>
		<c:set var="idPadre" value="${param.idPadre}" />
	</c:when>
	<c:otherwise>
		<c:set var="idPadre" value="${idPadre}" />
	</c:otherwise>
</c:choose>

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	
	<gene:formScheda entita="G1STIPULA" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupRecuperaOda">
		<c:choose>
			<c:when test='${not empty requestScope.messageError}'>
				<gene:campoScheda>
					<td colSpan="2">
						<br>
						Si sono verificati degli errori durante l'operazione: <br><br>
						<b> ${requestScope.messageError} </b>
						<br><br>
					</td>
				</gene:campoScheda>
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="ID" visibile="false" defaultValue="${idStipula}"/>
				<gene:campoScheda campo="CODSTIPULA" visibile="false" defaultValue="${codStipula}"/>
				<gene:campoScheda campo="NGARA" visibile="false" defaultValue="${idPadre}"/>
				
				<gene:campoScheda campo="NUMODA" title="Codice ODA" obbligatorio="true"/>
				
				<input type="hidden" name="idStipula" id="idStipula" value="${idStipula}" />
				<input type="hidden" name="codStipula" id="codStipula" value="${codStipula}" />
				<input type="hidden" name="idPadre" id="idPadre" value="${idPadre}" />			
			</c:otherwise>
		</c:choose>
	</gene:formScheda>
	
  </gene:redefineInsert>


	<gene:redefineInsert name="buttons">
		<c:if test='${empty requestScope.messageError}'>
			<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
		</c:if>
		<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	</gene:redefineInsert>


	
	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/g1stipula/g1stipula-popup-recuperaOda.jsp";
			var numoda = getValue("NUMODA");
			if(numoda!=null){
				schedaConferma();
			} else {
				alert("Codice ODA obbligatorio per proseguire");
				document.getElementById("NUMODA").focus();
			}
		}
		
		function annulla(){
			window.close();
		}
		
	

	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>