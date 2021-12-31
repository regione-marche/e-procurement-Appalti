<%
/*
 * Created on: 14-11-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Form per impostare un filtro sulla lista delle categorie */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:choose>
	<c:when test='${not empty param.idalbo}'>
		<c:set var="idalbo" value="${param.idalbo}" />
	</c:when>
	<c:otherwise>
		<c:set var="idalbo" value="${idalbo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.nominativiPerPagina}'>
		<c:set var="nominativiPerPagina" value="${param.nominativiPerPagina}" />
	</c:when>
	<c:otherwise>
		<c:set var="nominativiPerPagina" value="${nominativiPerPagina}" />
	</c:otherwise>
</c:choose>



<gene:template file="popup-template.jsp" >
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value="Imposta filtro"/>
	
	<c:set var="modo" value="MODIFICA" scope="request" />
	
	<gene:redefineInsert name="corpo">
  		<gene:formTrova entita="COMMNOMIN"  >
  			<gene:campoTrova campo="NOMTEC" entita="TECNI" where="TECNI.CODTEC=COMMNOMIN.CODTEC" title="Nome"/>
  			<gene:campoTrova campo="NOMEIN" entita="UFFINT" where="UFFINT.CODEIN = COMMNOMIN.CODEIN" title="Ufficio intestatario" />
			<gene:campoTrova campo="RUOLO" entita="COMMRUOLI" where="COMMNOMIN.ID = COMMRUOLI.IDNOMIN" />
			<input type="hidden" name="idalbo" value="${idalbo}"/>
		</gene:formTrova>
		
		<gene:javaScript>	
			document.forms[0].jspPathTo.value="gare/commons/popup-filtro.jsp";
			document.forms[0].action+= "?tipo=Nominativi";
			
			var nominativiPerPagina="${nominativiPerPagina}";
			var indiceSelezionato =  document.getElementById('risultatiPerPagina').selectedIndex;
            document.getElementById("risultatiPerPagina").options[indiceSelezionato].value = nominativiPerPagina;
            document.getElementById("risultatiPerPagina").options[indiceSelezionato].innerHTML = nominativiPerPagina;
			document.getElementById("risultatiPerPagina").disabled=true
			
		</gene:javaScript>
		
	</gene:redefineInsert>
</gene:template>
