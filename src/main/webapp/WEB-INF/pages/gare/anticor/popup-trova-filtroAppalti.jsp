<%
/*
 * Created on: 30-08-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Form di ricerca per filtrare la pagina anticor-pg-appalti.jsp */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>





<c:choose>
	<c:when test='${not empty param.appaltiPerPagina}'>
		<c:set var="appaltiPerPagina" value="${param.appaltiPerPagina}" />
	</c:when>
	<c:otherwise>
		<c:set var="appaltiPerPagina" value="${appaltiPerPagina}" />
	</c:otherwise>
</c:choose>



<gene:template file="popup-template.jsp" >
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value="Imposta filtro"/>

	<c:set var="modo" value="MODIFICA" scope="request" />
	
	<gene:redefineInsert name="corpo">
  		<gene:formTrova entita="ANTICORLOTTI"  >
  			<c:if test="${gene:checkProt(pageContext, 'COLS.VIS.GARE.ANTICORLOTTI.CIG') }">
				<gene:campoTrova campo="CIG" />
			</c:if>
			<c:if test="${gene:checkProt(pageContext, 'COLS.VIS.GARE.ANTICORLOTTI.OGGETTO') }">
				<gene:campoTrova campo="OGGETTO" />
			</c:if>
			<c:if test="${gene:checkProt(pageContext, 'COLS.VIS.GARE.ANTICORLOTTI.DATAINIZIO') }">
				<gene:campoTrova campo="DATAINIZIO" />
			</c:if>
			<c:if test="${gene:checkProt(pageContext, 'COLS.VIS.GARE.ANTICORLOTTI.DATAULTIMAZIONE') }">
				<gene:campoTrova campo="DATAULTIMAZIONE" />
			</c:if>
			<c:if test="${gene:checkProt(pageContext, 'COLS.VIS.GARE.ANTICORLOTTI.IMPSOMMELIQ') }">
				<gene:campoTrova campo="IMPSOMMELIQ" />
			</c:if>
			<c:if test="${gene:checkProt(pageContext, 'COLS.VIS.GARE.ANTICORLOTTI.INVIABILE') }">
				<gene:campoTrova campo="INVIABILE" />
			</c:if>
			<c:if test="${gene:checkProt(pageContext, 'COLS.VIS.GARE.ANTICORLOTTI.PUBBLICA') }">
				<gene:campoTrova campo="PUBBLICA" />
			</c:if>
			<c:if test="${gene:checkProt(pageContext, 'COLS.VIS.GARE.ANTICORLOTTI.IDLOTTO') }">
				<gene:campoTrova campo="IDLOTTO" />
			</c:if>
			<c:if test="${gene:checkProt(pageContext, 'COLS.VIS.GARE.ANTICORLOTTI.IDCONTRATTO') }">
				<gene:campoTrova campo="IDCONTRATTO" />
			</c:if>
			
			<input type="hidden" name="appaltiPerPagina" value="${appaltiPerPagina}" />
			<input type="hidden" name="resetCampi" value="" />
		</gene:formTrova>
		
		<gene:javaScript>	
			document.forms[0].jspPathTo.value="gare/commons/popup-filtro.jsp";
			document.forms[0].action=document.forms[0].action + "?tipo=Anticor&entFiltro=ANTICORLOTTI";
						
			var appaltiPerPagina="${appaltiPerPagina}";
			var indiceSelezionato =  document.getElementById('risultatiPerPagina').selectedIndex;
            document.getElementById("risultatiPerPagina").options[indiceSelezionato].value = appaltiPerPagina;
            document.getElementById("risultatiPerPagina").options[indiceSelezionato].innerHTML = appaltiPerPagina;
			document.getElementById("risultatiPerPagina").disabled=true

					
			
			
			
			
		</gene:javaScript>
		
	</gene:redefineInsert>
</gene:template>
