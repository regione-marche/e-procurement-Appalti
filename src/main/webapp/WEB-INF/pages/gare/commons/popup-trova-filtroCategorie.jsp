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
	<c:when test='${not empty param.categoriePerPagina}'>
		<c:set var="categoriePerPagina" value="${param.categoriePerPagina}" />
	</c:when>
	<c:otherwise>
		<c:set var="categoriePerPagina" value="${categoriePerPagina}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipoele}'>
		<c:set var="tipoele" value="${param.tipoele}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoele" value="${tipoele}" />
	</c:otherwise>
</c:choose>


<gene:template file="popup-template.jsp" >
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:setString name="titoloMaschera" value="Imposta filtro"/>
	
	<c:set var="modo" value="MODIFICA" scope="request" />
	
	<gene:redefineInsert name="corpo">
  		<gene:formTrova entita="V_CAIS_TIT"  >
  			<gene:campoTrova campo="CAISIM" />
			<gene:campoTrova campo="DESCAT" />
			<gene:campoTrova campo="TIPLAVG" title="Tipologia" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTipoCategoria"/>
			<input type="hidden" name="tipoele" value="${tipoele}"/>
			<input type="hidden" name="categoriePerPagina" value="${categoriePerPagina}"/>
			
		</gene:formTrova>
		
		<gene:javaScript>	
			document.forms[0].jspPathTo.value="gare/commons/popup-filtro.jsp";
			document.forms[0].action+= "?tipo=Cat";
			
			var categoriePerPagina="${categoriePerPagina}";
			var indiceSelezionato =  document.getElementById('risultatiPerPagina').selectedIndex;
            document.getElementById("risultatiPerPagina").options[indiceSelezionato].value = categoriePerPagina;
            document.getElementById("risultatiPerPagina").options[indiceSelezionato].innerHTML = categoriePerPagina;
			document.getElementById("risultatiPerPagina").disabled=true
			
		</gene:javaScript>
		
	</gene:redefineInsert>
</gene:template>
