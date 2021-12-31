<%
/*
 * Created on: 23-01-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 *
 * Popup per campi ulteriori della lista delle categorie dell'elenco
 * 
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test='${not empty param.entita}'>
		<c:set var="entita" value="${param.entita}" />
	</c:when>
	<c:otherwise>
		<c:set var="entita" value="${entita}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.modificabile}'>
		<c:set var="modificabile" value="${param.modificabile}" />
	</c:when>
	<c:otherwise>
		<c:set var="modificabile" value="${modificabile}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${entita eq "ISCRIZCAT"}'>
		<c:set var="codiceGara" value='${gene:getValCampo(key, "ISCRIZCAT.CODGAR")}' />
		<c:set var="numeroGara" value='${gene:getValCampo(key, "ISCRIZCAT.NGARA")}' />
		<c:set var="codiceDitta" value='${gene:getValCampo(key, "ISCRIZCAT.CODIMP")}' />
		<c:set var="codiceCategoria" value='${gene:getValCampo(key, "ISCRIZCAT.CODCAT")}' />
		<c:set var="tipoCategoria" value='${gene:getValCampo(key, "ISCRIZCAT.TIPCAT")}' />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value='${gene:getValCampo(key, "ISCRIZCLASSI.CODGAR")}' />
		<c:set var="numeroGara" value='${gene:getValCampo(key, "ISCRIZCLASSI.NGARA")}' />
		<c:set var="codiceDitta" value='${gene:getValCampo(key, "ISCRIZCLASSI.CODIMP")}' />
		<c:set var="codiceCategoria" value='${gene:getValCampo(key, "ISCRIZCLASSI.CODCAT")}' />
		<c:set var="tipoCategoria" value='${gene:getValCampo(key, "ISCRIZCLASSI.TIPCAT")}' />
		<c:set var="numeroClassifica" value='${gene:getValCampo(key, "ISCRIZCLASSI.NUMCLASS")}' />
		<c:set var="descClasse" value=" e classe '${fn:substringAfter(param.descat, 'Classifica:')}'" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.salvato}'>
		<c:set var="salvato" value="${param.salvato}" />
	</c:when>
	<c:otherwise>
		<c:set var="salvato" value="${salvato}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipo}'>
		<c:set var="tipo" value="${param.tipo}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipo" value="${tipo}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.autorizzatoModifiche}'>
		<c:set var="autorizzatoModifiche" value="${param.autorizzatoModifiche}" />
	</c:when>
	<c:otherwise>
		<c:set var="autorizzatoModifiche" value="${autorizzatoModifiche}" />
	</c:otherwise>
</c:choose>

<div style="width:97%;">

<gene:template file="popup-template.jsp" schema="GARE" gestisciProtezioni="true" idMaschera="${entita}-scheda">
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="corpo">
		<c:choose>
			<c:when test="${tipo eq '2' }">
				<c:set var="titolo" value="Ulteriori informazioni della categoria '${codiceCategoria}' " />
			</c:when>
			<c:when test='${codiceCategoria eq "0"}'>
				<c:choose>
					<c:when test='${tipoCategoria eq "1"}'>
						<c:set var="descTipoCategoria" value="lavori"/>
					</c:when>
					<c:when test='${tipoCategoria eq "2"}'>
						<c:set var="descTipoCategoria" value="forniture"/>
					</c:when>
					<c:otherwise>
						<c:set var="descTipoCategoria" value="servizi"/>
					</c:otherwise>
				</c:choose>
				<c:set var="titolo" value="Dettaglio penalità per gare per ${descTipoCategoria} senza categorie" />
			</c:when>
			<c:otherwise>
				<c:set var="titolo" value="Dettaglio penalità della categoria '${codiceCategoria}' ${descClasse }" />
			</c:otherwise>
		</c:choose>
		<gene:setString name="titoloMaschera" value="${titolo}" />
		<gene:formScheda entita="${entita}" gestisciProtezioni="true">
			<gene:campoScheda campo="CODGAR" visibile="false" defaultValue="${codiceGara}"/>
			<gene:campoScheda campo="NGARA"  visibile="false" defaultValue="${numeroGara}"/>
			<gene:campoScheda campo="CODIMP"  visibile="false" defaultValue="${codiceDitta}"/>
			<gene:campoScheda campo="CODCAT"  visibile="false" defaultValue="${codiceCategoria}"/>
			<gene:campoScheda campo="TIPCAT"  visibile="false" defaultValue="${tipoCategoria}"/>
			<c:if test='${entita eq "ISCRIZCLASSI"}'>
				<gene:campoScheda campo="NUMCLASS"  visibile="false" defaultValue="${numeroClassifica}"/>
			</c:if>
			
			<gene:campoScheda campo="ALTPEN" visibile='${tipo eq "1" }'/>
			<gene:campoScheda campo="NOTPEN" visibile='${tipo eq "1" }'/>
			<gene:campoScheda campo="ULTNOT" visibile='${tipo eq "1" }'/>
			<c:if test='${entita eq "ISCRIZCAT" and tipo ne "1"}'>
				<gene:campoScheda campo="ULTINF" />
			</c:if>
			
			<input type="hidden" name="entita" id="entita" value="${entita }" />			
			<input type="hidden" name="modificabile" id="modificabile" value="${modificabile }" />
			<input type="hidden" name="salvato" id="salvato" value="${salvato }" />
			<input type="hidden" name="tipo" id="tipo" value="${tipo }" />
			<input type="hidden" name="autorizzatoModifiche" id="autorizzatoModifiche" value="${autorizzatoModifiche }" />
			
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
					<gene:insert name="addPulsanti"/>
						
					<c:choose>
						<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
							<gene:insert name="pulsanteSalva">
								<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:salva()">
							</gene:insert>
							<gene:insert name="pulsanteAnnulla">
								<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
							</gene:insert>
					
						</c:when>
						<c:otherwise>
							<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.ISCRIZCAT-lista.AssegnaPenalita") and modificabile eq true and tipo eq "1" and autorizzatoModifiche ne "2"}'>
								<INPUT type="button"  class="bottone-azione" value='Assegna penalità' title='Assegna penalità' onclick="javascript:schedaModifica()">
							</c:if>
							<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") and modificabile eq true and tipo eq "2" and autorizzatoModifiche ne "2"}'>
								<INPUT type="button"  class="bottone-azione" value='Modifica' title='Modifica' onclick="javascript:schedaModifica()">
							</c:if>
							<INPUT type="button" class="bottone-azione" value='Esci' title='Esci' onclick="javascript:chiudi();">
						</c:otherwise>
						</c:choose>
					
					&nbsp;
				</td>
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>
	<gene:javaScript>
		
		<c:if test='${tipo=="1"}' >
			if(document.getElementById("salvato").value=="Si"){
				window.opener.bloccaRichiesteServer();
				window.opener.listaVaiAPagina(window.opener.document.forms[0].pgCorrente.value);
			}
		</c:if>
		
		function chiudi(){
			window.close();
		}
		
		function salva(){
			document.getElementById("salvato").value="Si";
			schedaConferma();
		}
	</gene:javaScript>
</gene:template>
</div>