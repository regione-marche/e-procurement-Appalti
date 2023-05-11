<%
/*
 * Created on: 22/05/2012
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "MERIC", "CODRIC")}'/>
<c:set var="id" value='${gene:getValCampo(key, "ID")}'/>

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />

<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "MERIC")}' />
<c:if test='${modo ne "NUOVO" }'>
	<c:set var="esistonoPuntiContatto" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoPuntiContattoOrdiniFunction", pageContext, id)}' />
</c:if>

<%/* Dati generali della gara */%>
<gene:formScheda entita="MERIC" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreMERIC">

<gene:redefineInsert name="addToAzioni" >
	<c:if test='${modo ne "MODIFICA" and modo ne "NUOVO" && gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_TORN-lista.Condividi-gara")}'>
		<td class="vocemenulaterale">
     	<c:if test='${isNavigazioneDisattiva ne "1" }'>
 				<a href="javascript:apriGestionePermessiRicercaMercatoStandard('${id}');" title="Punto ordinante e istruttore" tabindex="1503">
			</c:if>
		  Punto ordinante e istruttore
			<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
		</td>
	</c:if>
</gene:redefineInsert>

	<c:set var="msgChiaveErrore">	
		<fmt:message key="match.tags.template.dettaglio.campoChiave.messaggio">
			<fmt:param value="$"/>
		</fmt:message>
	</c:set>

	<c:set var="msgChiaveErrore" value="${fn:replace(msgChiaveErrore, '\\\\', '')}" />	


	<gene:gruppoCampi idProtezioni="GEN" >
		<gene:campoScheda>
			<td colspan="2"><b>Dati generali</b></td>
		</gene:campoScheda>

		<gene:campoScheda campo="ID" visibile="false" />

<c:choose>
	<c:when test='${isCodificaAutomatica eq "false"}'>
		<gene:campoScheda campo="CODRIC" obbligatorio="true" modificabile='${modoAperturaScheda eq "NUOVO"}' >
			<gene:checkCampoScheda funzione='"##".indexOf("$") < 0' obbligatorio="true" messaggio="${msgChiaveErrore}" />
		</gene:campoScheda>
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="CODRIC" modificabile='false' gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoCodificaAutomatica" />
	</c:otherwise>
</c:choose>

		<gene:campoScheda campo="OGGETTO"  />
		<gene:campoScheda campo="DATDEF"  />
		<gene:campoScheda campo="ISARCHI"  visibile="${datiRiga.MERIC_ISARCHI eq '1'}"/>
	</gene:gruppoCampi>
	
	<c:if test='${modoAperturaScheda ne "NUOVO"}' >
		<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneAttiAutorizzativiFunction", pageContext, "MERICATT", id)}'/>
	</c:if>
	
	
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='MERICATT'/>
		<jsp:param name="chiave" value='${id}'/>
		<jsp:param name="nomeAttributoLista" value='attiAutorizzativi' />
		<jsp:param name="idProtezioni" value="ATAU" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/mericatt/atto-autorizzativo.jsp"/>
		<jsp:param name="arrayCampi" value="'MERICATT_ID_', 'MERICATT_IDRIC_', 'MERICATT_TATTO_', 'MERICATT_DATTO_', 'MERICATT_NATTO_'"/>
		<jsp:param name="sezioneListaVuota" value="true" />
		<jsp:param name="titoloSezione" value="Atto autorizzativo" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo atto autorizzativo" />
		<jsp:param name="descEntitaVociLink" value="atto autorizzativo" />
		<jsp:param name="msgRaggiuntoMax" value="i atti autorizzativi"/>
		<jsp:param name="usaContatoreLista" value="true"/>
	</jsp:include>
		
		
	<c:if test='${modo eq "NUOVO"}'>
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.ValorizzaStazioneAppaltanteFunction" parametro=""/>
	</c:if>
	
	<gene:gruppoCampi idProtezioni="RUP" >
		<gene:campoScheda>
			<td colspan="2"><b>Stazione appaltante e RUP</b></td>
		</gene:campoScheda>
		<gene:archivio titolo="Uffici intestatari"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.MERIC.CENINT") && esistonoPuntiContatto ne "si","gene/uffint/uffint-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda-popup.jsp","")}'
			 campi="UFFINT.CODEIN;UFFINT.NOMEIN"
			 chiave="MERIC_CENINT"
			 functionId="skip|abilitazione:1_parentFormName:formGAREAVVISSITORN"
			 formName="formGAREAVVISSITORN">
				<gene:campoScheda campo="CENINT" defaultValue="${requestScope.initCENINT}" obbligatorio="true" modificabile="${empty sessionScope.uffint }">
					<gene:checkCampoScheda funzione='checkPuntiContatto()' obbligatorio="true" messaggio="Non è possibile modificare il valore perchè ci sono riferimenti ai relativi punti di contatto negli ordini di acquisto" onsubmit="false"/>
				</gene:campoScheda>
				<gene:campoScheda campo="NOMEIN" title="Denominazione" entita="UFFINT" where="MERIC.CENINT=UFFINT.CODEIN" 
					modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.MERIC.CENINT") && empty sessionScope.uffint}' 
					visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.MERIC.CENINT")}' defaultValue="${requestScope.initNOMEIN}" >
					<gene:checkCampoScheda funzione='checkPuntiContatto()' obbligatorio="true" messaggio="Non è possibile modificare il valore perchè ci sono riferimenti ai relativi punti di contatto negli ordini di acquisto" onsubmit="false"/>
				</gene:campoScheda>
		</gene:archivio>
		<gene:archivio titolo="Tecnici"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.MERIC.CODRUP"),"gene/tecni/tecni-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
			 campi="TECNI.CODTEC;TECNI.NOMTEC"
			 functionId="skip"
			 chiave="MERIC_CODRUP"
			 inseribile="true">
				<gene:campoScheda campo="CODRUP"  />
				<gene:campoScheda campo="NOMTEC" title="Nome" entita="TECNI" where="MERIC.CODRUP=TECNI.CODTEC" 
					modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.MERIC.CODRUP")}' 
					visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.MERIC.CODRUP")}' />
		</gene:archivio>
	</gene:gruppoCampi>
	
	<c:if test="${modo ne 'VISUALIZZA' }">
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.ValorizzaCatalogoElettronicoFunction" parametro="${modo}"/>
	</c:if>
	
	<gene:gruppoCampi idProtezioni="CATALOGO" >
		<gene:campoScheda>
			<td colspan="2"><b>Catalogo elettronico</b></td>
		</gene:campoScheda>
		<gene:archivio titolo="Cataloghi elettronici"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.MERIC.CODCATA"),"gare/garealbo/garealbo-lista-popup.jsp","")}'
			 scheda=''
			 schedaPopUp=''
			 campi="GAREALBO.NGARA;GAREALBO.OGGETTO"
			 chiave="MERIC_CODCATA"
			 inseribile="false"
			 functionId="skip" >
				<gene:campoScheda campo="CODCATA"  defaultValue="${requestScope.initCODCATA}" obbligatorio="true" />
				<gene:campoScheda campo="OGGETTO" title="Oggetto del catalogo" entita="GAREALBO" where="MERIC.CODCATA=GAREALBO.NGARA" 
					modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.MERIC.CODCATA")}' 
					visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.MERIC.CODCATA")}' defaultValue="${requestScope.initOGGETTO}"/>
		</gene:archivio>
		
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="ALTRIDATI" >
		<gene:campoScheda>
			<td colspan="2"><b>Altri dati</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="NOTE"  />
	</gene:gruppoCampi>
	
	<gene:campoScheda campo="AUTORI" entita="G_PERMESSI" where="G_PERMESSI.IDMERIC = MERIC.ID AND G_PERMESSI.SYSCON = ${sessionScope.profiloUtente.id}" visibile="false"/>
	<gene:campoScheda campo="PROPRI" entita="G_PERMESSI" where="G_PERMESSI.IDMERIC = MERIC.ID AND G_PERMESSI.SYSCON = ${sessionScope.profiloUtente.id}" visibile="false"/>
		
	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
			<jsp:param name="entita" value="MERIC"/>
			<jsp:param name="inputFiltro" value="ID=N:${id}"/>			
			<jsp:param name="filtroCampoEntita" value="IDMERIC=${id }"/>
		</jsp:include>
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
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
					</c:if>
				</gene:insert>
				<gene:insert name="pulsanteNuovo">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:schedaNuovo()" id="btnNuovo">
					</c:if>
				</gene:insert>
				<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_TORN-lista.Condividi-gara")}'>
					<INPUT type="button"  class="bottone-azione" value='Punto ordinante e istruttore' title='Punto ordinante e istruttore' onclick="javascript:apriGestionePermessiRicercaMercatoStandard('${id}');" >
				</c:if>
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>
	
	
</gene:formScheda>

	<form name="formVisualizzaPermessiUtentiStandard" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiRicercaMercato.do" method="post">
		<input type="hidden" name="metodo" id="metodo" value="apri" />
		<input type="hidden" name="id" id="id" value="" />
		<input type="hidden" name="genereGara" id="genereGara" value="" />
		<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
		<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		<input type="hidden" name="codric" id="codric" value="${datiRiga.MERIC_CODRIC}" />
	</form>	

<gene:javaScript>
	
	function apriGestionePermessiRicercaMercatoStandard(id) {
		document.location.href='${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiRicercaMercato.do?"+csrfToken+"&id='+id;
		bloccaRichiesteServer();
		formVisualizzaPermessiUtentiStandard.id.value = id;
<c:choose>
	<c:when test="${datiRiga.G_PERMESSI_PROPRI eq '1' or abilitazioneGare eq 'A'}" >
			formVisualizzaPermessiUtentiStandard.permessimodificabili.value = 'true';
	</c:when>
	<c:otherwise>
			formVisualizzaPermessiUtentiStandard.permessimodificabili.value = 'false';
	</c:otherwise>
</c:choose>
		formVisualizzaPermessiUtentiStandard.submit();
	}
	
	function checkPuntiContatto(){
		var esistonoPuntiContatto = "${esistonoPuntiContatto }";
		if (esistonoPuntiContatto != null && esistonoPuntiContatto != null) {
			if (esistonoPuntiContatto == "si")
				return false;
		}
		return true;
	}
</gene:javaScript>