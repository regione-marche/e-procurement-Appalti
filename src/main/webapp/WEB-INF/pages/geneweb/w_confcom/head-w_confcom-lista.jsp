<%
/*
 * Created on 27-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA GRUPPI 
 // CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
<!--

	// Azioni invocate dal menu contestuale
	function annullaCreazione(){
		bloccaRichiesteServer();
		historyBack();
	}
	
	function creaNuovaComunicazione(){
		var radioModello = document.formRadioBut.numModello;
		var numModello;
		var keyAdd = "${param.keyAdd}";
		var keyParent = "${param.keyParent}";
		var ditta = "${param.ditta}";
		var stepWizard = "${param.stepWizard}";
		var whereBusteAttiveWizard = "${param.whereBusteAttiveWizard}";
		var genere = "${genere}";
		var entitaWSDM = "${param.entitaWSDM}";
		var chiaveWSDM = "${param.chiaveWSDM}";
		var idconfi = "${param.idconfi}";
		
		if (radioModello != null) {
			for(var i = 0; i < radioModello.length; i++) { 
				if(radioModello[i].checked) { // scorre tutti i vari radio button
					numModello = radioModello[i].value; // valore radio scelto
					break; // esco dal cliclo
				}
			}
		}
		if(numModello==null || numModello==""){
			alert("Per procedere, selezionare un modello");
			return;
		}
		listaNuovo.jspPathTo.value = "/WEB-INF/geneweb/w_invcom/w_invcom-scheda.jsp"
		listaNuovo.entita.value = "W_INVCOM";
		var parametri="&numModello=" + numModello + "&keyAdd=" + keyAdd + "&keyParent=" + keyParent;
		parametri+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM + "&idconfi=" + idconfi;
		if(ditta!="" && ditta!=null)
			parametri+="&ditta=" + ditta;
		if(stepWizard!="" && stepWizard!=null)
			parametri+="&stepWizard=" + stepWizard;
		if(whereBusteAttiveWizard!="" && whereBusteAttiveWizard!=null)
			parametri+="&whereBusteAttiveWizard=" + whereBusteAttiveWizard;
		if(genere!="" && genere!=null)
			parametri+="&tipo=" + genere;
		//listaNuovo.action = document.listaNuovo.action + "&numModello=" + numModello + "&keyAdd=" + keyAdd + "&keyParent=" + keyParent;
		listaNuovo.action = document.listaNuovo.action + parametri;
		listaNuovo.submit();
		
	}
-->
</script>

