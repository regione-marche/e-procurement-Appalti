<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
	
<c:set var="stato_simog_gara" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.GetStatoSimogW3GaraFunction",pageContext,numgara)}' />	


<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.W3.richiesteSIMOG.pubblica")}'>
	<c:if test="${stato_simog_gara eq '2' or stato_simog_gara eq '4'}" >	
		<!-- tr>
			<c:choose>
		        <c:when test='${isNavigazioneDisattiva ne "1"}'>
		          <td class="vocemenulaterale">
					<a href="javascript:popupValidazionePubblica('${numgara}');" title="Controlla dati per la pubblicazione" tabindex="1512">
						Controlla dati per la pubblicazione
					</a>
				  </td>
		        </c:when>
			    <c:otherwise>
			       <td>
					  Controlla dati per la pubblicazione
				   </td>
			    </c:otherwise>
			</c:choose>
		</tr -->
		<tr>
			<c:choose>
		        <c:when test='${isNavigazioneDisattiva ne "1"}'>
		          <td class="vocemenulaterale">
					<a href="javascript:popupPubblicaGaraLotto('${numgara}');" title="Invio richiesta pubblicazione gara" tabindex="1513">
						Invia richiesta pubblicazione gara
					</a>
				  </td>
		        </c:when>
			    <c:otherwise>
			       <td>
					 Invia richiesta pubblicazione gara
				   </td>
			    </c:otherwise>
			</c:choose>
		</tr>
	</c:if>
</c:if>
