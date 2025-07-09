<%
/*
 * Created on: 13-10-2010
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
		Finestra per l'attivazione della funzione 'Ricarica documenti'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.inserimentoEseguito and requestScope.inserimentoEseguito eq "1"}' >
<script type="text/javascript">
		window.close();
		window.opener.historyReload();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Inserimento documento di grande dimensione' />


<c:choose>
	<c:when test="${!empty param.ngara }">
		<c:set var="ngara" value="${param.ngara }"/>
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara }"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.codgar }">
		<c:set var="codgar" value="${param.codgar }"/>
	</c:when>
	<c:otherwise>
		<c:set var="codgar" value="${codgar }"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.gruppo }">
		<c:set var="gruppo" value="${param.gruppo }"/>
	</c:when>
	<c:otherwise>
		<c:set var="gruppo" value="${gruppo }"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.tipologia }">
		<c:set var="tipologia" value="${param.tipologia }"/>
	</c:when>
	<c:otherwise>
		<c:set var="tipologia" value="${tipologia }"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.isProceduraTelematica }">
		<c:set var="isProceduraTelematica" value="${param.isProceduraTelematica }"/>
	</c:when>
	<c:otherwise>
		<c:set var="isProceduraTelematica" value="${isProceduraTelematica }"/>
	</c:otherwise>
</c:choose>

<c:set var="inizializzaAllmail" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.InizializzazioneAllmailFunction", pageContext)}' scope="request"/>
<c:set var="descTipo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1072", "1", "")}'/>
	
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="TORN" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupCreaDocumento">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			Mediante questa funzione viene inserito un documento a cui è possibile associare un file di grande dimensione. <c:if test="${not empty descTipo}"><br>Il file associato deve superare i ${descTipo.substring(0,descTipo.indexOf("("))}MB.</c:if>
			<br><br>
			Per procedere popolare i seguenti dati:
			<br><br>
		</td>
	</gene:campoScheda>

		<gene:campoScheda campo="NGARA" campoFittizio="true" defaultValue="${ngara}" visibile="false" definizione="T20;0"/>
		<gene:campoScheda campo="CODGAR" campoFittizio="true" defaultValue="${codgar}" visibile="false" definizione="T20;0"/>
		<gene:campoScheda campo="GRUPPO" campoFittizio="true" defaultValue="${gruppo}" visibile="false" definizione="T10;0"/>
		<gene:campoScheda campo="GARTEL" campoFittizio="true" defaultValue="${isProceduraTelematica}" visibile="false" definizione="T10;0"/>
		<gene:campoScheda campo="TIPOLOGIA" campoFittizio="true" defaultValue="${tipologia}" visibile="false" definizione="T10;0"/>
		<gene:campoScheda campo="inizializzaAllmail" campoFittizio="true" defaultValue="${inizializzaAllmail}" visibile="false" definizione="T10;0"/>
		<gene:campoScheda campo="IDSTAMPA" campoFittizio="true" defaultValue="LARGEFILE" visibile="false" definizione="T10;0"/>
		<gene:campoScheda campo="DESCRIZIONE" title="Descrizione file" campoFittizio="true"  obbligatorio="true" definizione="T2000;0"/>
		<gene:campoScheda campo="TIPOATTO" title="Tipologia atto" campoFittizio="true" visibile="${gruppo eq 10}" obbligatorio="true" definizione="T100;0;A1202;;"/>
		<gene:campoScheda>
			<td colSpan="2">
			</td>
		</gene:campoScheda>
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/documgara/popupSetupLargeFile.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
	

	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>