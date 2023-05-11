<%
  /*
   * Created on 04-ott-2006
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE LA DEFINIZIONE DEI MENU SPECIFICI DELL'APPLICAZIONE
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="opzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="moduloAttivo" value="${sessionScope.moduloAttivo}" scope="request"/>
<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
			
<c:if test='${gene:checkProt(pageContext,"MENU.VIS.GARE")}' >
   <td>
	  	<c:choose>
	  		<c:when test='${gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-avvisi")}' >
				<c:set var="titoloMenuGareElenchiAvvisiCatologhi" value="Avvisi"/>
 	 		</c:when>
	  		<c:when test='${!gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-elenchi") && !gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-cataloghi")}' >
				<c:set var="titoloMenuGareElenchiAvvisiCatologhi" value="Gare"/>
 	 		</c:when>
 	 		<c:when test='${gene:checkProt(pageContext,"SUBMENU.VIS.GARE.Trova-cataloghi")}' >
				<c:set var="titoloMenuGareElenchiAvvisiCatologhi" value="Cataloghi"/>
 	 		</c:when>
 	 		<c:otherwise>
				<c:set var="titoloMenuGareElenchiAvvisiCatologhi" value="Elenchi"/>  	  				  	
 	  	</c:otherwise>
 	  </c:choose>    

	    <c:choose>
  	  <c:when test='${isNavigazioneDisattiva eq	"1"}'>
	  		<span><c:out value="${titoloMenuGareElenchiAvvisiCatologhi}" /></span>
	  	</c:when>
	  	<c:otherwise>
	  	  <a id="lnavbarGare" href="javascript:showSubmenuNavbar('lnavbarGare',linksetSubMenuGare);" tabindex="1210">${titoloMenuGareElenchiAvvisiCatologhi}</a>
	  	</c:otherwise>
 	  </c:choose>
	  </td>
</c:if>
	
<c:if test='${gene:checkProt(pageContext,"MENU.VIS.RICERCHE")}' >
  	  <td>
	<c:choose>
	  	<c:when test='${isNavigazioneDisattiva eq "1"}'>
			<span><c:out value="Ricerche di mercato" /></span>
		</c:when>
	  	<c:otherwise>
		  <a id="lnavbarRicerche" href="javascript:showSubmenuNavbar('lnavbarRicerche',linksetSubMenuRicerche);" tabindex="1220">Ricerche di mercato</a>
		</c:otherwise>
  	  </c:choose>
	  </td>
</c:if>

<c:if test='${gene:checkProt(pageContext,"MENU.VIS.STIPULE")}' >
  	  <td>
	<c:choose>
	  	<c:when test='${isNavigazioneDisattiva eq "1"}'>
			<span><c:out value="Stipule" /></span>
		</c:when>
	  	<c:otherwise>
		  <a id="lnavbarStipule" href="javascript:showSubmenuNavbar('lnavbarStipule',linksetSubMenuStipule);" tabindex="1230">Stipule</a>
		</c:otherwise>
  	  </c:choose>
	  </td>
</c:if>

 	