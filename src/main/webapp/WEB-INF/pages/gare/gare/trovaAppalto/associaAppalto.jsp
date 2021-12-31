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

<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "PERI")}' />
<c:if test="${not empty filtroLivelloUtente}" >
	<c:set var="filtroLivelloUtente" value="and ${fn:replace(filtroLivelloUtente, 'PERI.CODLAV', 'APPA.CODLAV')}" />
</c:if>

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint}">
	<c:set var="filtroAbilitato" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "integrazioneLFS.filtroUffint")}'/>
	<c:if test="${filtroAbilitato eq '1' }">
		<c:set var="iscus" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetIscucFunction", pageContext, sessionScope.uffint)}' />
		<c:if test="${iscus ne '1' }">
			<c:set var="filtroUffint" value=" and exists (select codlav from peri where codlav = appa.codlav and CENINT = '${sessionScope.uffint}')"/>
		</c:if>
	</c:if>
</c:if>

<c:set var="integrazioneERPvsWSDM" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneERPvsWSDMFunction", pageContext, idconfi)}'/>

<gene:template file="scheda-template.jsp">
<c:choose>
	<c:when test='${not empty param.tipoGara}'>
		<gene:setString name="titoloMaschera" value="Nuova gara"/>
	</c:when>
	<c:otherwise>
		<gene:setString name="titoloMaschera" value="Nuovo lotto di gara"/>
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
		<gene:formScheda entita="APPA" gestisciProtezioni="true" >
			<gene:campoScheda>
				<td colspan="2">
					<br><b>Impostare l'eventuale riferimento all'appalto:</b>
					<br>
				</td>
			</gene:campoScheda>
			<gene:archivio titolo="Appalti"
				lista='gare/gare/trovaAppalto/popup-lista-appalti.jsp'
				scheda=""
				schedaPopUp=""
				campi="APPA.CODLAV;APPA.NAPPAL;APPA.CODCUA;APPA.NOTAPP;APPA.CODCIG;PERI.CUPPRG"
				chiave=""
				where=" (APPA.DAGG is null and APPA.DVOAGG is null) and (APPA.TIPLAVG = ${param.tipoAppalto}) and not exists (select NGARA from GARE where APPA.CODLAV = GARE.CLAVOR and APPA.NAPPAL = GARE.NUMERA and GARE.ESINEG is null) ${filtroLivelloUtente} ${filtroUffint }"
				inseribile="false"
				formName="formArchivioAppalti" >
				<gene:campoScheda campo="CODLAV" />
				<gene:campoScheda campo="NAPPAL" />
				<gene:campoScheda campo="CODCUA" />
				<gene:campoScheda campo="NOTAPP" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote" />
				<gene:campoScheda campo="CODCIG" />
				<gene:campoScheda campo="CUPPRG" entita="PERI" where="APPA.CODLAV = PERI.CODLAV" visibile="false" />
			</gene:archivio>
			<c:if test='${gene:checkProt(pageContext, "COLS.VIS.LAVO.PERI.CUPPRG")}'>
				<gene:campoScheda campo="CUPPRG" campoFittizio="true" definizione="T15;0;;G2CUPPRG" title="Codice CUP di progetto" />
			</c:if>
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
		      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaCreazione();">&nbsp;&nbsp;&nbsp;&nbsp;
		   	<c:if test='${not empty param.tipoGara}' >
		     	<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;
		    </c:if>
			<c:choose>
		   	<c:when test='${not empty param.modScheda}' >
		      <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:associaAppalto();">&nbsp;
				</c:when>
				<c:otherwise>
		      <INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:creaNuovaGara();">&nbsp;
				</c:otherwise>
			</c:choose>
				</td>
			</gene:campoScheda>
			<input type="hidden" id="lottoOfferteDistinte" name="lottoOfferteDistinte" value="${param.lottoOfferteDistinte }"/>
			
			<gene:fnJavaScriptScheda funzione="setCodiceCUP()" elencocampi="CUPPRG" esegui="false" />
			<gene:fnJavaScriptScheda funzione="setCUP()" elencocampi="PERI_CUPPRG" esegui="false" />
		</gene:formScheda>
	</gene:redefineInsert>

<gene:javaScript>

	var livelloProgettazioneSettato = false;
	var whereArchivioAppalti = document.formArchivioAppalti.archWhereLista.value;

	function setCodiceCUP(){
		var cup = "" + getValue("CUPPRG");
		if(cup != ""){
			document.formArchivioAppalti.archWhereLista.value = whereArchivioAppalti + " and APPA.CODLAV in (select CODLAV from PERI where PERI.CUPPRG like '%" + cup + "%') ";
			livelloProgettazioneSettato = true;
		} else {
			if(livelloProgettazioneSettato){
				document.formArchivioAppalti.archWhereLista.value = whereArchivioAppalti;
				livelloProgettazioneSettato = false;
			}
		}
		setValue("PERI_CUPPRG", cup);
		setValue("APPA_CODLAV", "");
		setValue("APPA_NAPPAL", "");
		setValue("APPA_NOTAPP", "");
		setValue("APPA_CODCUA", "");
	}

	function setCUP(){
	<c:if test='${gene:checkProt(pageContext, "COLS.VIS.LAVO.PERI.CUPPRG")}'>
		if(getValue("PERI_CUPPRG") != "")
			document.getElementById("CUPPRG").value = getValue("PERI_CUPPRG");
	</c:if>
	}

		function annullaCreazione(){
			bloccaRichiesteServer();
			historyBack();
		}

		function indietro(){
			bloccaRichiesteServer();
			document.location.href = "${pageContext.request.contextPath}/pg/InitNuovaGara.do?" + csrfToken;
		}

		function creaNuovaGara(){
			<c:choose>
			<c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.inserimentoRdaSMAT")}'>
				bloccaRichiesteServer();
  			    var chiaveRiga = getValue("APPA_CODLAV") + ";" + getValue("APPA_NAPPAL");
				if(chiaveRiga.length > 1){
					document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/garerda/associaRda.jsp&tipoGara=garaLottoUnico&modo=NUOVO&proceduraTelematica=${param.proceduraTelematica}&modalitaPresentazione=${param.modalitaPresentazione}&tipoAppalto="+${param.tipoAppalto} + "&chiaveRiga=" + chiaveRiga;
				}else{
					document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/garerda/associaRda.jsp&tipoGara=garaLottoUnico&modo=NUOVO&proceduraTelematica=${param.proceduraTelematica}&modalitaPresentazione=${param.modalitaPresentazione}&tipoAppalto="+${param.tipoAppalto};
				}
			</c:when>
			<c:when test='${integrazioneERPvsWSDM eq "1" && not empty param.tipoGara}'>
				bloccaRichiesteServer();
  			    var chiaveRiga = getValue("APPA_CODLAV") + ";" + getValue("APPA_NAPPAL");
				if(chiaveRiga.length > 1){
					document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/garerda/associa-rda-wsdm.jsp&tipoGara=garaLottoUnico&modo=NUOVO&proceduraTelematica=${param.proceduraTelematica}&modalitaPresentazione=${param.modalitaPresentazione}&tipoAppalto="+${param.tipoAppalto} + "&chiaveRiga=" + chiaveRiga+ "&idconfi=${idconfi}";;
				}else{
					document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/garerda/associa-rda-wsdm.jsp&tipoGara=garaLottoUnico&modo=NUOVO&proceduraTelematica=${param.proceduraTelematica}&modalitaPresentazione=${param.modalitaPresentazione}&tipoAppalto="+${param.tipoAppalto} + "&idconfi=${idconfi}";
				}
			</c:when>
			<c:otherwise>
				document.forms[0].activePage.value = 0;
				document.forms[0].jspPath.value="/WEB-INF/pages/gare/gare/gare-scheda.jsp";
				document.forms[0].jspPathTo.value="/WEB-INF/pages/gare/gare/gare-scheda.jsp";
				var chiaveRiga = getValue("APPA_CODLAV") + ";" + getValue("APPA_NAPPAL");
				if(chiaveRiga.length > 1)
					document.forms[0].action = document.forms[0].action + "&tipoAppalto=${param.tipoAppalto}&tipoGara=${param.tipoGara}&proceduraTelematica=${param.proceduraTelematica}&modalitaPresentazione=${param.modalitaPresentazione}&chiaveRiga=" + getValue("APPA_CODLAV") + ";" + getValue("APPA_NAPPAL");
				else
					document.forms[0].action = document.forms[0].action + "&tipoAppalto=${param.tipoAppalto}&tipoGara=${param.tipoGara}&proceduraTelematica=${param.proceduraTelematica}&modalitaPresentazione=${param.modalitaPresentazione}";
				bloccaRichiesteServer();
				document.forms[0].submit();
			</c:otherwise>
			</c:choose>
		}

	<c:if test='${not empty param.chiavePadre}'>
		document.forms[0].keyParent.value = ${param.chiavePadre};
	</c:if>

	</gene:javaScript>
</gene:template>