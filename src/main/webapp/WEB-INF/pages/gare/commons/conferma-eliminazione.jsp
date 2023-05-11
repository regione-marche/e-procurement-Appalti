<%
/*
 * Created on: 08-ott-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra che visualizza la conferma di eliminazione della gara
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="popup-message-template.jsp">
<c:choose>
	<c:when test='${fn:containsIgnoreCase(param.chiaveRiga, "CODGAR")}'>
		<c:set var="valoreChiave" value='${gene:getValCampo(param.chiaveRiga, "CODGAR")}' />
		<c:set var="contestoSimog" value='GARA' />
		<c:set var="msgSimog" value='Non è possibile eliminare la gara perchè è stata già creata la relativa anagrafica SIMOG per la richiesta CIG' />
		<c:set var="msgStipulaColl" value='Non è possibile eliminare la gara perchè è collegata ad una stipula' />
		<c:choose>
			<c:when test='${fn:startsWith(valoreChiave, "$") and empty param.genere}' >
				<gene:setString name="titoloMaschera" value='Eliminazione gara a lotto unico ${fn:substringAfter(valoreChiave, "$")}' />
			</c:when>
			<c:when test='${fn:startsWith(valoreChiave, "$") and param.genere eq 10}' >
				<gene:setString name="titoloMaschera" value='Eliminazione elenco ${fn:substringAfter(valoreChiave, "$")}' />
			</c:when>
			<c:when test='${fn:startsWith(valoreChiave, "$") and param.genere eq 20}' >
				<gene:setString name="titoloMaschera" value='Eliminazione catalogo ${fn:substringAfter(valoreChiave, "$")}' />
			</c:when>
			<c:otherwise>
				<gene:setString name="titoloMaschera" value='Eliminazione gara divisa in lotti ${valoreChiave}' />
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:when test='${fn:containsIgnoreCase(param.chiaveRiga, "NGARA") and param.genere eq 11 }'>
		<gene:setString name="titoloMaschera" value='Eliminazione avviso ${gene:getValCampo(param.chiaveRiga,"NGARA")}' />
	</c:when>
	<c:otherwise>
		<gene:setString name="titoloMaschera" value='Eliminazione lotto ${gene:getValCampo(param.chiaveRiga,"NGARA")}' />
		<c:set var="valoreChiave" value='${gene:getValCampo(param.chiaveRiga, "NGARA")}' />
		<c:set var="contestoSimog" value='LOTTO' />
		<c:set var="msgSimog" value='Non è possibile eliminare il lotto perchè è stata già creata la relativa anagrafica SIMOG per la richiesta CIG' />
		<c:set var="msgStipulaColl" value='Non è possibile eliminare il lotto perchè è collegato ad una stipula' />
	</c:otherwise>
</c:choose>
<c:set var="isRichiestaCigGara" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteRichiestaCigGaraFunction",  pageContext, contestoSimog, valoreChiave )}'/>
<c:set var="isStipulaCollegata" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteStipulaAssociataFunction",  pageContext, contestoSimog, valoreChiave )}'/>

<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' />
<c:set var="tipoWSERP" value='${requestScope.tipoWSERP}' />
<c:set var="esitoODACollegata" value='0'/>
<c:if test='${integrazioneWSERP eq "1" && tipoWSERP eq "ATAC"}'>
	<c:set var="esitoODACollegata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetWSERPVerificaOdaCollegataFunction",  pageContext, valoreChiave )}'/>
	<c:choose>
		<c:when test='${esitoODACollegata eq "1"}'>
			<c:set var="msgOdaCollegata" value='Non è possibile eliminare la gara perchè esiste un ordine di acquisto collegato' />
		</c:when>
		<c:when test='${esitoODACollegata eq "3"}'>
			<c:set var="msgOdaCollegata" value='Non è possibile eliminare la gara perchè non è stato possibile determinare se esiste un ordine di acquisto collegato' />	
		</c:when>
	</c:choose>
	
</c:if>

	<c:set var="modo" value="MODIFICA" scope="request" />
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="TORN" gestisciProtezioni="false" >
			<gene:campoScheda campo="CODGAR" visibile="false" />
			
			
		<c:choose>
			<c:when test='${isRichiestaCigGara ne "SI" and isStipulaCollegata ne "true" and (esitoODACollegata eq "0" || esitoODACollegata eq "2" || esitoODACollegata eq "4")}' >
			<gene:campoScheda>
				<td>
				<br>
					&nbsp;&nbsp;Confermi l'eliminazione?
				<br>
				<br>
				</td>
			</gene:campoScheda>
			</c:when>
			<c:otherwise>
			<gene:campoScheda>
				<td>
				<br>
				<c:if test='${isRichiestaCigGara eq "SI"}'>
					${msgSimog}
					<br>
					<br>
				</c:if>
				<c:if test='${isStipulaCollegata eq "true"}'>
					${msgStipulaColl}
					<br>
					<br>
				</c:if>
				<c:if test='${esitoODACollegata eq "1" || esitoODACollegata eq "3"}'>
					${msgOdaCollegata}
					<br>
					<br>
				</c:if>
				</td>
			</gene:campoScheda>
			</c:otherwise>
		</c:choose>
			
			
		</gene:formScheda>
		
			<gene:redefineInsert name="buttons">
			<c:if test='${isRichiestaCigGara ne "SI" and isStipulaCollegata ne "true" and esitoODACollegata ne "1" and esitoODACollegata ne "3"}'>
				<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
			</c:if>
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			</gene:redefineInsert>


  </gene:redefineInsert>
	<gene:javaScript>

		function conferma(){
			chiaveRiga="${param.chiaveRiga}";
			opener.bloccaRichiesteServer();
			opener.confermaDelete();
			
		}

		function annulla(){
			window.close();
		}

	</gene:javaScript>
</gene:template>