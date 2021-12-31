<%
/*
 * Created on: 05-10-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 *
 * Popup per campi ulteriori della lista imprdocg
 * 
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<div style="width:97%;">

<c:choose>
	<c:when test='${not empty param.sezionitec}'>
		<c:set var="sezionitec" value="${param.sezionitec}" />
	</c:when>
	<c:otherwise>
		<c:set var="sezionitec" value="${sezionitec}" />
	</c:otherwise>
</c:choose>

<gene:template file="popup-template.jsp" schema="GARE" >
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="corpo">
		<gene:setString name="titoloMaschera" value='Ulteriori dettagli documento' />
		<gene:formScheda entita="V_GARE_DOCDITTA">
			<gene:campoScheda campo="CODGAR" visibile="false" />
			<gene:campoScheda campo="CODIMP"  visibile="false" />
			<gene:campoScheda campo="NORDDOCI"  visibile="false" />
			<gene:campoScheda campo="PROVENI"  visibile="false" />
			<gene:campoScheda campo="DOCTEL"  visibile="false" />
			<gene:campoScheda campo="DESCRIZIONE" modificabile="${datiRiga.V_GARE_DOCDITTA_PROVENI ne 1  }"/>
			<gene:campoScheda campo="SEZTEC" modificabile="false" visibile="${datiRiga.V_GARE_DOCDITTA_BUSTA eq 2 and sezionitec eq '1'}"/>
			<gene:campoScheda campo="TIPODOC" modificabile="false" visibile="${datiRiga.V_GARE_DOCDITTA_PROVENI eq 1}"/>
			<gene:campoScheda campo="MODFIRMA" modificabile="false" visibile="${datiRiga.V_GARE_DOCDITTA_PROVENI eq 1}"/>
			<gene:campoScheda campo="DATAPUB" modificabile="false" />
			<gene:campoScheda campo="NOTEDOCI"/>
			<gene:campoScheda campo="BUSTA" visibile="false"/>
			
						
			<input type="hidden" name="indiceRigaOpener" id="indiceRiga" value="${param.indiceRiga}" />
			<input type="hidden" name="sezionitec" id="sezionitec" value="${sezionitec}" />
		
			
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
				<c:choose>
					<c:when test='${modo eq "MODIFICA"}'>
						<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:conferma();">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:window.close();">
					</c:when>
					<c:otherwise>
						<INPUT type="button" class="bottone-azione" value='Esci' title='Esci' onclick="javascript:window.close();">
					</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>
	<gene:javaScript>
		
		var winOpener = window.opener;
		
		function conferma(){
			winOpener.setValue("IMPRDOCG_NOTEDOCI_${param.indiceRiga}", getValue("V_GARE_DOCDITTA_NOTEDOCI"));
			<c:if test="${datiRiga.V_GARE_DOCDITTA_PROVENI ne 1}">
				winOpener.setValue("V_GARE_DOCDITTA_DESCRIZIONE_${param.indiceRiga}", getValue("V_GARE_DOCDITTA_DESCRIZIONE"));
				winOpener.document.getElementById("FIT_VALUE_${param.indiceRiga}").innerHTML = getValue("V_GARE_DOCDITTA_DESCRIZIONE");
			</c:if>
			
									
			window.close();
		}
		
		<c:if test='${modo eq "MODIFICA"}'>
			setValue("V_GARE_DOCDITTA_NOTEDOCI",winOpener.getValue("IMPRDOCG_NOTEDOCI_${param.indiceRiga}"));
			<c:if test="${datiRiga.V_GARE_DOCDITTA_PROVENI ne 1}">
				setValue("V_GARE_DOCDITTA_DESCRIZIONE",winOpener.getValue("V_GARE_DOCDITTA_DESCRIZIONE_${param.indiceRiga}"));
				$("#")
			</c:if>
		</c:if>
		
		
	</gene:javaScript>
</gene:template>
</div>