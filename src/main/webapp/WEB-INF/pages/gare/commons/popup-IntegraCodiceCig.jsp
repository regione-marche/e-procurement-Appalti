<%
/*
 * Created on: 11-10-2012
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
		Finestra per la valorizzazione dei campi CODCIG, DACQCIG 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>

<c:choose>
	<c:when test='${not empty requestScope.esito and requestScope.esito eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<c:choose>
	<c:when test='${param.isPlicoUnico eq "No"}'>
		<c:set var="codiceGara" value="${param.ngara}" />
		<c:set var="msgGara" value="della gara"/>
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${param.codgar1}" />
		<c:set var="msgGara" value="del lotto"/>
	</c:otherwise>
</c:choose>

<c:set var="codcig" value="${param.codcig}" />
<c:set var="numavcp" value="${param.numavcp}" />

<c:set var="getDacqcigFunctionResult" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDacqcigFunction",pageContext,codiceGara)}' />

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<gene:setString name="titoloMaschera" value='Integrazione codice CIG' />	


<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupIntegraCodiceCig">
	
		<gene:campoScheda>
			<td colSpan="2"><br>Specificare il codice CIG ${msgGara} e la sua data di acquisizione. Se previsto specificare anche il numero gara ANAC.<br><br></td>
		</gene:campoScheda>
		<gene:campoScheda campo="NGARA" defaultValue="${param.ngara}" visibile="false" />
		<gene:campoScheda campo="CODGAR1" defaultValue="${param.codgar1}" visibile="false" />
		<input type="hidden" name="plicoUnico" id="plicoUnico" value="${param.isPlicoUnico}"/>
		<input type="hidden" name="codcig" id="codcig" value="${param.codcig}"/>
		<input type="hidden" name="numavcp" id="numavcp" value="${param.numavcp}"/>
		
		<gene:campoScheda campo="NUMAVCP" defaultValue="${numavcp}" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" modificabile="${empty numavcp}" />
		
		<gene:campoScheda campo="CODCIG" defaultValue="${codcig}" obbligatorio="true" modificabile="${empty codcig}" >
			<gene:checkCampoScheda funzione='gestioneCodiceCIG("##")'  obbligatorio="true" messaggio="Codice CIG non valido" onsubmit="true"/>
		</gene:campoScheda>		
		<gene:campoScheda campo="ESENTE_CIG" campoFittizio="true" computed="true" title="Esente CIG?" definizione="T10;;;SN" defaultValue="2" modificabile="${empty codcig}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoSenzaNull" />
		<gene:fnJavaScriptScheda funzione="gestioneEsenteCIG()" elencocampi="ESENTE_CIG" esegui="false"/>
		
		<gene:campoScheda campo="DACQCIG" title="Data acquisizione" defaultValue="${getDacqcigFunctionResult}" obbligatorio="true"/>
		<gene:campoScheda>
			<td colSpan="2"><br></td>
		</gene:campoScheda>
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:javaScript>
		document.forms[0].jspPathTo.value="gare/commons/popup-IntegraCodiceCig.jsp";
		
		$("#GARE_CODCIG").css({'text-transform': 'uppercase' });
		
		gestioneEsenteCIG();
		
		function gestioneCodiceCIG(valore){
			if(valore!=null && valore!=""){
				setValue("GARE_CODCIG", valore.toUpperCase(), false);
				if ("2" == getValue("ESENTE_CIG")) {
					if (!controllaCIG("GARE_CODCIG")) {
						return false;
					}
				}
			}
			return true;
		}
		
		function gestioneEsenteCIG() {
			var esenteCig = getValue("ESENTE_CIG");
			if ("1" == esenteCig) {
				showObj("rowGARE_CODCIG", false);
			} else {
				showObj("rowGARE_CODCIG", true);
			}
		}
		
		function conferma() {
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
