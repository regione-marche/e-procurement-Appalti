<%
/*
 * Created on: 10-feb-2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 /* Lista popup di selezione degli ids collegati/scollegati alla gara */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<c:choose>
	<c:when test='${not empty requestScope.affidamentiCreati and requestScope.affidamentiCreati eq "OK"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="idUtente" value="${sessionScope.profiloUtente.id}" />
<c:set var="codiceGara" value="${param.codiceGara}" />
<c:set var="ngara" value="${param.ngara}" />

<gene:template file="popup-template.jsp">
	<gene:setString name="titoloMaschera" value="Selezione affidamenti da creare"/>
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
	
		
		<table class="dettaglio-noBorderBottom">
			<tr><td colspan="2"><b>
				Nella lista sottostante sono elencate le valutazioni dei prodotti per cui e' possibile creare l'affidamento <br> 
			</b></td></tr>
		</table>		
		<table class="lista">
		<tr>
		<td>
		<gene:formLista pagesize="25" sortColumn="2" tableclass="datilista" entita="V_GARE_PRODOTTI_VALUTATI" where="V_GARE_PRODOTTI_VALUTATI.NGARA = '${ngara}' AND AFFIDAMENTO IS NULL" gestisciProtezioni="true">
			<gene:campoLista  title="Seleziona affidamento<br><center>${titoloMenu}</center>" width="50">
				<c:if test="${currentRow >= 0 && not empty datiRiga.V_GARE_PRODOTTI_VALUTATI_DITTAO}">
							<input type="checkbox" name="keys" value="${datiRiga.V_GARE_PRODOTTI_VALUTATI_DITTAO}" />
				</c:if>
			</gene:campoLista>

				<gene:campoLista campo="CODGAR" visibile="false" />
				<gene:campoLista campo="NGARA" visibile="false" />
				<gene:campoLista campo="DITTAO" visibile="false" />
				<gene:campoLista campo="NOMEST" headerClass="sortable" />
				<gene:campoLista campo="IMPTOT" headerClass="sortable" />
			<input type="hidden" name="codiceGara" id="codiceGara" value="${codiceGara}" />
			<input type="hidden" name="seguen" id="seguen" value="${ngara}" />
			<input type="hidden" name="modo" id="modo" value="2" />
			<input type="hidden" name="uffint" id="uffint" value="${uffint}" />
			<input type="hidden" name="numAffidamenti" id="numAffidamenti" value="" />
		
		</gene:formLista>
		</td>
		</tr>
			<c:if test="${datiRiga.rowCount > 0}">
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<INPUT type="button"  class="bottone-azione" value='Crea affidamenti' title='Crea affidamenti' onclick="javascript:conferma();">&nbsp;
					<INPUT type="button"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:annulla();">&nbsp;
				</td>
			</tr>
			</c:if>
		</table>
  </gene:redefineInsert>

	<gene:javaScript>
		document.getElementById("numAffidamenti").value = ${currentRow}+1;
		
		function conferma() {
			var numAffidamenti = contaCheckSelezionati(document.forms[0].keys);
	  		if (numAffidamenti == 0) {
	      		alert("Selezionare almeno un affidamento");
	      	} else {
	      		document.forms[0].action="${pageContext.request.contextPath}/pg/CreaAffValutazioneProdotti.do?"+csrfToken;
 				bloccaRichiesteServer();
				document.forms[0].submit();
			}
		}
		
		function annulla(){
			window.close();
		}
	</gene:javaScript>

</gene:template>
	</c:otherwise>
</c:choose>		
