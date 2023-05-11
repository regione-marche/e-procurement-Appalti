<%/*
   * Created on 12-04-2017
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${!empty chiave}'>
		<c:set var="chiave" value='${chiave}' />
	</c:when>
	<c:otherwise>
		<c:set var="chiave" value="${param.chiave}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty tipo}'>
		<c:set var="tipo" value='${tipo}' />
	</c:when>
	<c:otherwise>
		<c:set var="tipo" value="${param.tipo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty faseGara}'>
		<c:set var="faseGara" value='${faseGara}' />
	</c:when>
	<c:otherwise>
		<c:set var="faseGara" value="${param.faseGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty esistonoDitteConPunteggio}'>
		<c:set var="esistonoDitteConPunteggio" value='${esistonoDitteConPunteggio}' />
	</c:when>
	<c:otherwise>
		<c:set var="esistonoDitteConPunteggio" value="${param.esistonoDitteConPunteggio}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty autorizzatoModifiche}'>
		<c:set var="autorizzatoModifiche" value='${autorizzatoModifiche}' />
	</c:when>
	<c:otherwise>
		<c:set var="autorizzatoModifiche" value="${param.autorizzatoModifiche}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty sezTec}'>
		<c:set var="sezTec" value='${sezTec}' />
	</c:when>
	<c:otherwise>
		<c:set var="sezTec" value="${param.sezTec}" />
	</c:otherwise>
</c:choose>

<c:set var="gara" value='${gene:getValCampo(chiave,"DITG.NGARA5")}' scope="request" />
<c:set var="codiceGara" value='${gene:getValCampo(chiave,"DITG.CODGAR5")}' scope="request" />
<c:set var="ditta" value='${gene:getValCampo(chiave,"DITG.DITTAO")}' scope="request" />
<c:set var="nomimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMOFunction",  pageContext, gara,codiceGara,ditta)}'/>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GOEV-dettaglioValutazioneTecEco-lista">
	<c:choose>
		<c:when test="${tipo eq '1' }">
			<gene:setString name="titoloMaschera" value="Dettaglio valutazione tecnica della gara ${gara } per la ditta ${nomimo}"/>
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value="Dettaglio valutazione economica della gara ${gara } per la ditta ${nomimo}"/>
		</c:otherwise>
	</c:choose>
	
	
	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${contextPath}/js/jquery.dettaglio.valutazione.tec_eco.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${contextPath}/js/big.js"></script>
					
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/dettagliovalutazione/jquery.dettaglio.valutazione.tec_eco.css" >
				
	</gene:redefineInsert>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
		
		<gene:redefineInsert name="listaNuovo"/>
		<gene:redefineInsert name="listaEliminaSelezione"/>
		
		<gene:redefineInsert name="addToAzioni" >
			<c:if test='${autorizzatoModifiche ne "2" and faseGara <7 and not esistonoDitteConPunteggio and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AttivaCommissione")}'>
				<tr id="attivaCommissioneTr">
					<td class="vocemenulaterale" >
							<a href="javascript:attivaCommissione();" title='Attiva modifica coeff.complessivo' tabindex="1500">
							Attiva modifica coeff.complessivo</a>
					</td>
				</tr>
			</c:if>
		</gene:redefineInsert>
		<%// Creo la lista per gcap e dpre mediante la vista v_gcap_dpre%>
		<table class="lista">
			<form name="formDettaglio">
				<input type="hidden" id="gara" value="${gara }">
				<input type="hidden" id="codiceGara" value="${codiceGara }">
				<input type="hidden" id="ditta" value="${ditta }">
				<input type="hidden" id="tipo" value="${tipo }">
				<input type="hidden" id="chiave" value="${chiave }">
				<input type="hidden" id="percorsoContesto" value="${pageContext.request.contextPath}">
				<input type="hidden" id="sezTec" value="${sezTec}">
			</form>
			<tr>
				<td colspan="2">
				<br>
				<div id="dettaglioValutazionecontainer"></div>
				
				</td>
			</tr>
						
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<INPUT type="button"  class="bottone-azione" value='Torna a elenco concorrenti' title='Torna a elenco concorrenti' onclick="historyVaiIndietroDi(1);">
					&nbsp;
				</td>
			</tr>
			
		</table>
		<div id="mascheraSchedaCoefficente" style="display: none;">
			<br>
			<form name="formCoefficente" id="formCoefficente">
				<table class="tabellaCoefficente">
					<tr>
						<td colspan="3">
							<b>Specificare il valore del coefficiente da assegnare al criterio:</b><br><br>
						</td>
					</tr>
					<tr>
						<td width="25%">
							Numero criterio 
						</td>
						<td colpsan="2">
							<span id="numeroCriterio"></span>
						</td>
					</tr>
					<tr id="rigaSubCriterio">
						<td width="25%">
							Numero subcriterio 
						</td>
						<td colpsan="2">
							<span id="numeroSubCriterio"></span>
						</td>
					</tr>
					<tr>
						<td width="25%">
							Coefficiente
						</td>
						<td id="tdCoeff" style="display: none;">
							<input id="Inputcoeff" name="Inputcoeff" title="Coefficiente" type="text" size="20" value="" maxlength="18">
							<br><span id="errorMessage"></span>
						</td>
						<td id="tdSelectCoeff" style="display: none;">
							<select id="selectA1z07" name="selectA1z07"></select>
							<br><span id="errorMessage"></span>
						</td>
					</tr>
					<tr>
						<td width="25%">
							Punteggio
						</td>
						<td colpsan="2">
							<span id="punteggioVal"></span>
						</td>
					</tr>
					<tr>
						<td width="25%">
							Note
						</td>
						<td id="tdNote" >
							<textarea id="Inputnote" name="Inputnote" title="Note" type="text" cols="50" rows="4" value="" maxlength="2000"></textarea>
							<br><span id="errorMessage"></span>
						</td>
					</tr>
				</table>
				<input type="hidden" id= "maxpunG1cridef" value="">
				<input type="hidden" id= "coeffImpostato" value="">
				<input type="hidden" id= "noteImpostato" value="">
			</form>
		</div>
		
		<div id="mascheraCoefficenteCommissione" style="display: none;">
			<br>
			<form name="formCoefficenteCommissione" id="formCoefficenteCommissione">
				<table id="tabModalCoefficenteCommissione" class="tabellaCoefficente">
					
				</table>
				<input type="hidden" id= "ditta" name="ditta" value="${ditta}">
				<input type="hidden" id= "gara" name="gara" value="${gara}">
				<br><span id="errorMessage" style="font-size:11px"></span>
			</form>
		</div>
  </gene:redefineInsert>
	<gene:javaScript>
				
		function chiudi(){
			window.close();
		}
		
	</gene:javaScript>
		
</gene:template>