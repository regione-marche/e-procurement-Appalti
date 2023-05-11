<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
	
<c:set var="stato_simog_gara" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.GetStatoSimogW3GaraFunction",pageContext,numgara)}' />	

<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.W3.richiesteSIMOG")}'>
	<c:if test="${stato_simog_gara eq '2' or stato_simog_gara eq '4'}">	
		<tr>
			<c:choose>
		        <c:when test='${isNavigazioneDisattiva ne "1"}'>
		          <td class="vocemenulaterale">
					<a href="javascript:popupElaboraRichiesteLotti('${numgara}');" title="Elabora tutte le richieste" tabindex="1513">
						Elabora tutte le richieste
					</a>
				  </td>
		        </c:when>
			    <c:otherwise>
			       <td>
					 Elabora tutte le richieste
				   </td>
			    </c:otherwise>
			</c:choose>
		</tr>
	</c:if>
</c:if>
