<%
/*
 * Created on: 13/11/2006
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
<c:set var="tipscad" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTIPSCADFunction", pageContext, key)}' scope="request"/>

<c:set var="start" value='${fn:indexOf(key,":")}' />
<c:set var="len" value='${fn:length(key)}' />
<c:set var="ngara" value='${fn:substring(key,start+1,len)}' />

<gene:setString name="titoloMaschera" value='Gara ${ngara}' />

<%/* Dati generali della gara */%>
<gene:formScheda entita="GARE" gestisciProtezioni="true" >



<% //La pagina è specifica per il profilo Protocollo, per cui non è prevista sempre la sola visualizzazione dei dati  %>
<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>

	<c:set var="msgChiaveErrore">	
		<fmt:message key="match.tags.template.dettaglio.campoChiave.messaggio">
			<fmt:param value="$"/>
		</fmt:message>
	</c:set>

	<c:set var="msgChiaveErrore" value="${fn:replace(msgChiaveErrore, '\\\\', '')}" />	

	<gene:gruppoCampi idProtezioni="GEN">
		<gene:campoScheda>
			<td colspan="2"><b>Dati generali</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="NGARA"  />
		<gene:campoScheda campo="TIPGEN" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile='TRUE' />
		<gene:campoScheda campo="GENERE" visibile="false"/>
		<gene:campoScheda campo="NOT_GAR" visibile="${datiRiga.GARE_GENERE ne 3}" />
		<gene:campoScheda campo="DESTOR" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="${datiRiga.GARE_GENERE eq 3}" />

		<gene:archivio titolo="Tecnici"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP"),"gene/tecni/tecni-lista-popup.jsp","")}'
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
			campi="TECNI.CODTEC;TECNI.NOMTEC"
			functionId="skip"
			chiave="TORN_CODRUP">
			<gene:campoScheda campo="CODRUP" title="Codice Responsabile Unico Procedimento" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" />
			<gene:campoScheda campo="NOMTEC" title="Nome" entita="TECNI" from="TORN" where="GARE.CODGAR1=TORN.CODGAR and TORN.CODRUP=TECNI.CODTEC"
						modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP")}' 
						visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CODRUP")}'/>
		</gene:archivio>

		
		<gene:campoScheda campo="TIPSCAD" title="Fase in scadenza" campoFittizio="true" definizione="T100;0;A1043" value="${tipscad}"/>
	</gene:gruppoCampi>
	
	<jsp:include page="/WEB-INF/pages/gene/attributi/sezione-attributi-generici.jsp">
		<jsp:param name="entitaParent" value="GARE"/>
	</jsp:include>


</gene:formScheda>

