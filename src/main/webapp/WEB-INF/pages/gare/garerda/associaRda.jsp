<%/*
   * Created on 16-Mar-2009
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<gene:template file="scheda-template.jsp">
<c:choose>
	<c:when test='${not empty param.tipoGara}'>
		<gene:setString name="titoloMaschera" value="Nuova gara"/>
	</c:when>
	<c:otherwise>
		<gene:setString name="titoloMaschera" value="Nuovo lotto di gara"/>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.chiaveRiga}'>
		<c:set var="chiaveRiga" value="${param.chiaveRiga}" />
	</c:when>
	<c:otherwise>
		<c:set var="chiaveRiga" value="${chiaveRiga}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.livpro}'>
		<c:set var="livpro" value="${param.livpro}" />
	</c:when>
	<c:otherwise>
		<c:set var="livpro" value="${livpro}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.tipoAppalto}'>
		<c:set var="tipoAppalto" value="${param.tipoAppalto}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoAppalto" value="${tipoAppalto}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.proceduraTelematica}'>
		<c:set var="proceduraTelematica" value="${param.proceduraTelematica}" />
	</c:when>
	<c:otherwise>
		<c:set var="proceduraTelematica" value="${proceduraTelematica}" />
	</c:otherwise>
</c:choose>


	<c:set var="modo" value="NUOVO" />
	<gene:redefineInsert name="documentiAzioni" />
	<gene:redefineInsert name="schedaConferma" >
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:creaNuovaGara();" title="Avanti" tabindex="1502">
					Avanti &gt;
				</a>
			</td>
		</tr>
	<c:if test='${not empty param.tipoGara}' >
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:indietro();" title="Indietro" tabindex="1503">
					&lt; Indietro
				</a>
			</td>
		</tr>
	</c:if>
	</gene:redefineInsert>
	<gene:redefineInsert name="schedaAnnulla" >
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:annullaCreazione();" title="Annulla" tabindex="1504">
					Annulla
				</a>
			</td>
		</tr>	
	</gene:redefineInsert>
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="V_SMAT_RDA" gestisciProtezioni="true" >
			<gene:campoScheda>
				<td colspan="2">
					<br><b>Impostare il riferimento alla RdA:</b>
					<br>
				</td>
			</gene:campoScheda>
			<gene:archivio titolo="RdA"
				lista='gare/garerda/popup-lista-rda.jsp'
				scheda=""
				schedaPopUp=""
				campi="V_SMAT_RDA.ID_RICHIESTA;V_SMAT_RDA.NUMERO_RDA;V_SMAT_RDA.DESCRIZIONE;V_SMAT_RDA.VALORE;V_SMAT_RDA.STATO;V_SMAT_RDA.DATA_APPROVAZIONE"
				functionId="default"
				chiave=""
				inseribile="false"
				formName="formArchivioAppalti" >
				<gene:campoScheda campo="ID_RICHIESTA" visibile="false" />
				<gene:campoScheda campo="NUMERO_RDA" />
				<gene:campoScheda campo="DESCRIZIONE" />
				<gene:campoScheda campo="VALORE"  modificabile="false"/>
				<gene:campoScheda campo="STATO"  visibile="false"/>
				<gene:campoScheda campo="DATA_APPROVAZIONE" modificabile="false" />
			</gene:archivio>
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
		      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaCreazione();">&nbsp;&nbsp;&nbsp;&nbsp;
		   	<c:if test='${not empty param.tipoGara}' >
		     	<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;
		    </c:if>
		      <INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:creaNuovaGara();">&nbsp;
				</td>
			</gene:campoScheda>
			<input type="hidden" name="codlav" value="${codlav}" />
			<input type="hidden" name="nappal" value="${nappal}" />
			<input type="hidden" name="chiaveRiga" value="${chiaveRiga}" />
		</gene:formScheda>
	</gene:redefineInsert>

<gene:javaScript>



		function annullaCreazione(){
			bloccaRichiesteServer();
			historyBack();
		}

		function indietro(){
			bloccaRichiesteServer();
			document.location.href = "${pageContext.request.contextPath}/pg/InitNuovaGara.do?" + csrfToken;
		}

		function creaNuovaGara(){
			if(getValue("V_SMAT_RDA_NUMERO_RDA") == ""){
				alert('Il numero RdA risulta obbligatorio!')
				return -1;
			}
			var tipoAppalto = ${param.tipoAppalto};
			<c:choose>
			<c:when test='${param.tipoGara=="garaDivisaLottiOffUnica"}'>
				document.forms[0].activePage.value = 0;
				document.forms[0].jspPath.value="/WEB-INF/pages/gare/torn/torn-scheda.jsp";
				document.forms[0].jspPathTo.value="/WEB-INF/pages/gare/torn/torn-scheda.jsp";
			</c:when>
			<c:otherwise>
				document.forms[0].activePage.value = 0;
				document.forms[0].jspPath.value="/WEB-INF/pages/gare/gare/gare-scheda.jsp";
				document.forms[0].jspPathTo.value="/WEB-INF/pages/gare/gare/gare-scheda.jsp";
			</c:otherwise>
			</c:choose>

			if(tipoAppalto == '1'){
				document.forms[0].action+="&tipoAppalto=${param.tipoAppalto}&tipoGara=${param.tipoGara}&proceduraTelematica=${param.proceduraTelematica}&modalitaPresentazione=${param.modalitaPresentazione}&chiaveRiga=${param.chiaveRiga}&livpro=${param.livpro}&numeroRda=" + getValue("V_SMAT_RDA_NUMERO_RDA");
			}else
				document.forms[0].action+="&tipoAppalto=${param.tipoAppalto}&tipoGara=${param.tipoGara}&proceduraTelematica=${param.proceduraTelematica}&modalitaPresentazione=${param.modalitaPresentazione}&numeroRda=" + getValue("V_SMAT_RDA_NUMERO_RDA");

			bloccaRichiesteServer();
			document.forms[0].submit();
		}

	<c:if test='${not empty param.chiavePadre}'>
		document.forms[0].keyParent.value = ${param.chiavePadre};
	</c:if>

	</gene:javaScript>
</gene:template>