<%
/*
 * Created on: 25/05/2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />

<c:choose>
	<c:when test='${not empty param.sezionitec}'>
		<c:set var="sezionitec" value='${param.sezionitec}' />
	</c:when>
	<c:otherwise>
		<c:set var="sezionitec" value="${sezionitec}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.bloccoDati}'>
		<c:set var="bloccoDati" value='${param.bloccoDati}' />
	</c:when>
	<c:otherwise>
		<c:set var="bloccoDati" value="${bloccoDati}" />
	</c:otherwise>
</c:choose>

<c:set var="faseGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetFASGARFunction", pageContext, gene:getValCampo(key,"NGARA"))}'/>
<c:choose>
	<c:when test='${bloccoDati}'>
		<c:set var="modificaLabel" value='Modifica dopo pubblicazione' />
		<c:set var="bloccoModificaPubblicazione" value='true' />
	</c:when>
	<c:otherwise>
		<c:set var="modificaLabel" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' />
		<c:set var="bloccoModificaPubblicazione" value='false' />
	</c:otherwise>
</c:choose>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GOEV-scheda">
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
	</gene:redefineInsert>
	<gene:setString name="titoloMaschera" value='Dettaglio assegnazione punteggio criterio di valutazione della gara ${gene:getValCampo(key,"NGARA")}'/>
	<gene:redefineInsert name="corpo">
		<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
		<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
		<gene:formScheda entita="GOEV" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCriteriValutazioneGOEV">
			
			<gene:gruppoCampi idProtezioni="DATGEN">
				<gene:campoScheda>
					<td colspan="2"><b>Dati generali</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="NECVAN" visibile="false"/>
				<gene:campoScheda campo="NGARA" visibile="false" />
				<gene:campoScheda campo="DESPAR" modificabile="false" title="Descrizione criterio"/>
				<gene:campoScheda campo="MAXPUN" modificabile="false"/>
				<gene:campoScheda campo="ISNORIPAR" modificabile="true"/>	
			</gene:gruppoCampi>
			
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				
				<gene:redefineInsert name="schedaModifica">
					<c:if test='${ modo eq "VISUALIZZA" and gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and autorizzatoModifiche ne "2" and faseGara < "5"}' >
						<tr>
							<td class="vocemenulaterale" >
									<a href="javascript:schedaModifica();" title="${modificaLabel}">
								${modificaLabel}</a>
							</td>
						</tr>
					</c:if>
				</gene:redefineInsert>
						
					
				
				<td class="comandi-dettaglio" colSpan="2">
					<c:choose>
						<c:when test='${modo eq "MODIFICA"}'>
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
						</c:when>
						<c:otherwise>
							<INPUT type="button"  class="bottone-azione" value='Torna a lista criteri di valutazione' title='Torna a lista criteri di valutazione' onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;&nbsp;
							<c:if test='${(autorizzatoModifiche ne "2") and gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and faseGara < "5"}'>
								<INPUT type="button"  class="bottone-azione" value='${modificaLabel}' title='${modificaLabel}' onclick="javascript:schedaModifica()">
							</c:if>
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</gene:campoScheda>
			
			
			<input type="hidden" name="ngara" value="${ngara }"/>
			<input type="hidden" name="necvan" value="${necvan }"/>
			<input type="hidden" name="bloccoDati" value="${bloccoDati }"/>
			<input type="hidden" name="sezionitec" value="${sezionitec }"/>
			<input type="hidden" name="bloccoModificaPubblicazione" id="bloccoModificaPubblicazione" value="${bloccoModificaPubblicazione}" />
		</gene:formScheda>

	</gene:redefineInsert>
	<gene:javaScript>
		<c:if test='${modo eq "MODIFICA"}' >
			var bloccoModificaPubblicazione = $("#bloccoModificaPubblicazione").val();
			var bloccoModificaPCP = "${bloccoModificaPCP}";
			bloccaDopoPubblicazione(bloccoModificaPubblicazione,"","",bloccoModificaPCP,"${impoOpzRinnModificabili}","${bloccoModificaPubblicazione}");
		</c:if>
	</gene:javaScript>

</gene:template>