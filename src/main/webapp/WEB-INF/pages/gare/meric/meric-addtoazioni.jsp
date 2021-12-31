<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />

<gene:redefineInsert name="listaEliminaSelezione" />

<c:choose>
	<c:when test='${isNavigazioneDisattiva ne "1"}'>
		<c:if test='${gene:checkProtFunz(pageContext,"MOD","LISTAMODIFICA") && autorizzatoModifiche eq "1" && empty datiRiga.MERIC_DATVAL}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:listaApriInModifica();" title="Modifica" tabindex="1513">
						Modifica
					</a>
				</td>
			</tr>
		</c:if>
		
		<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && autorizzatoModifiche eq "1" && empty datiRiga.MERIC_DATVAL}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:listaEliminaSelezione();" title="Elimina selezionati" tabindex="1514">
						${gene:resource("label.tags.template.lista.listaEliminaSelezione")}</a>
				</td>
			</tr>
		</c:if>
		
		<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.MERIC.valutazioneprodotti")}'>
			<c:if test='${autorizzatoModifiche eq "1"  && empty datiRiga.MERIC_DATVAL}'>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:valutazioneProdotti(${id},'VALUTAZIONE');" title="Procedi alla valutazione prodotti" tabindex="1515">Procedi alla valutazione prodotti</a>
					</td>
				</tr>
			</c:if>
		</c:if>
		
		<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.MERIC.annullavalutazione")}'>
			<c:if test='${autorizzatoModifiche eq "1" && !empty datiRiga.MERIC_DATVAL}'>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:annullaValutazioneProdotti(${id});" title="Annulla valutazione prodotti" tabindex="1515">Annulla valutazione prodotti</a>
					</td>
				</tr>
			</c:if>
		</c:if>
		
	</c:when>
	
	<c:otherwise>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:listaConfermaMessaggio();" title="Conferma" tabindex="1516">Conferma</a>
			</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:listaAnnullaModifica();" title="Annulla" tabindex="1517">Annulla</a>
			</td>
		</tr>
	</c:otherwise>
</c:choose>


				


