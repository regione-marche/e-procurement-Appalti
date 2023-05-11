<%/*
       * Created on 21/04/2020
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
	<c:when test='${!empty param.cenint}'>
		<c:set var="cenint" value='${param.cenint}' />
	</c:when>
	<c:otherwise>
		<c:set var="cenint" value="${cenint}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.ngara}'>
		<c:set var="ngara" value='${param.ngara}' />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.ncont}'>
		<c:set var="ncont" value='${param.ncont}' />
	</c:when>
	<c:otherwise>
		<c:set var="ncont" value="${ncont}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.codimp}'>
		<c:set var="codimp" value='${param.codimp}' />
	</c:when>
	<c:otherwise>
		<c:set var="codimp" value="${codimp}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.ngaral}'>
		<c:set var="ngaral" value='${param.ngaral}' />
	</c:when>
	<c:otherwise>
		<c:set var="ngaral" value="${ngaral}" />
	</c:otherwise>
</c:choose>

<c:set var="nomein" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNOMEINFunction", pageContext, cenint)}'/>

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="modcont" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetModcontFunction", pageContext, codiceGara)}' />

<c:set var="garaltsogPopolata" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.EsistonoOccorrenzeGaraltsogFunction", pageContext, ngara, ngaral, modcont, codimp)}'/>
<c:set var="functionId" value="skip" />
<c:if test="${garaltsogPopolata eq 'true'}">
	<c:choose>
		<c:when test="${modcont eq '1' }">
			<c:set var="functionId" value="g1aqspesa_1" />
			<c:set var="parametriWhere" value="T:${ngaral}" />
		</c:when>
		<c:when test="${modcont eq '2' }">
			<c:set var="functionId" value="g1aqspesa_2_(${elencoLotti})" />
		</c:when>
		<c:otherwise>
			<c:set var="functionId" value="g1aqspesa_0" />
			<c:set var="parametriWhere" value="T:${ngara}" />
		</c:otherwise>
	</c:choose>
</c:if>
<c:set var="functionId" value="${functionId}|abilitazione:1"/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="G1AQSPESA-scheda" schema="GARE">
	
	<c:choose>
		<c:when test="${modo eq 'NUOVO' }">
			<gene:setString name="titoloMaschera" value="Nuova prenotazione di spesa dell'accordo quadro ${ngara}" />
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value="Prenotazione di spesa dell'accordo quadro ${ngara}" />
		</c:otherwise>
	</c:choose>
	
		
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="G1AQSPESA" gestisciProtezioni="true"
		gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreG1AQSPESA">
		
			
			<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
			
			<gene:campoScheda campo="ID" modificabile="false" visibile="false"/>
			<gene:archivio titolo="Enti aderenti"
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT"),"gene/uffint/uffint-lista-popup.jsp","")}'
				scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda.jsp","")}'
				schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda-popup.jsp","")}'
				campi="UFFINT.CODEIN;UFFINT.NOMEIN"
				chiave="G1AQSPESA_CENINT"
				functionId="${functionId}_parentFormName:formUFFINTG1AQSPESA"
				parametriWhere="${parametriWhere}"
				inseribile="false"
				formName="formUFFINTG1AQSPESA">
					<gene:campoScheda campo="CENINT" obbligatorio="true"/>
					<gene:campoScheda campo="NOMEIN" title="Denominazione" campoFittizio="true" definizione="T100;;;;NOMEIN" value="${nomein}" />
			</gene:archivio>
			<gene:campoScheda campo="NPRORIC"/>
			<gene:campoScheda campo="DATRIC"/>
			<gene:campoScheda campo="IMPRIC"/>
			<gene:campoScheda campo="NPROAUT"/>
			<gene:campoScheda campo="DATAUT"/>
			<gene:campoScheda campo="IMPAUT"/>
			<gene:campoScheda campo="NOTE"/>
						
			<gene:campoScheda>	
			<td class="comandi-dettaglio" colSpan="2">
				<gene:insert name="addPulsanti"/>
				<c:choose>
					<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
						<gene:insert name="pulsanteSalva">
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
						</gene:insert>
						<gene:insert name="pulsanteAnnulla">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
						</gene:insert>

					</c:when>
					<c:otherwise>
						<gene:insert name="pulsanteModifica">
							<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") && autorizzatoModifiche ne "2"}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
							</c:if>
						</gene:insert>
						<gene:insert name="pulsanteNuovo">
							<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO") && autorizzatoModifiche ne "2"}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovo()" id="btnNuovo">
							</c:if>
						</gene:insert>
					</c:otherwise>
				</c:choose>
				&nbsp;
			</td>
			</gene:campoScheda>
			
			<input type="hidden" name="cenint" id="cenint" value="${cenint}"/>
			<input type="hidden" name="ngara" id="ngara" value="${ngara}"/>
			<input type="hidden" name="ncont" id="ncont" value="${ncont}"/>
			<input type="hidden" name="codimp" id="codimp" value="${codimp}"/>
			<input type="hidden" name="ngaral" id="ngaral" value="${ngaral}"/>
		</gene:formScheda>
	</gene:redefineInsert>

	<gene:javaScript>
	
	function schedaNuovoCustom() {
		document.forms[0].action=document.forms[0].action+"&cenint=${cenint}&ngara=${ngara}&ncont=${ncont}&codimp=${codimp}&ngaral=${ngaral}";
		schedaNuovoDefault();
	}
	
	var schedaNuovoDefault = schedaNuovo;
	var schedaNuovo = schedaNuovoCustom;
	
	function schedaConfermaCustom() {
		var cenint = "${cenint}";
		if(cenint==null || cenint == "") {
			cenint = getValue("G1AQSPESA_CENINT");
			document.getElementById("cenint").value = cenint;
		}
		schedaConfermaDefault();
	}
	
	var schedaConfermaDefault = schedaConferma;
	var schedaConferma = schedaConfermaCustom;
	
	<c:if test="${modo eq 'NUOVO' }">
		var cenint="${cenint}";
		if(cenint != null && cenint != "")
			setValue("G1AQSPESA_CENINT",cenint);
	</c:if>
	
	
	</gene:javaScript>


</gene:template>
