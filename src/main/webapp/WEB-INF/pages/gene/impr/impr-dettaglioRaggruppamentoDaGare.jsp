<%
/*
 * Created on: 01-03-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Sezione dinamica impresa componente del raggruppamento richiamata da gare */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<c:set var="codiceImpresaPadre" value='${param.chiave}' />

<c:set var="raggruppamentoSelezionato" value='${param.raggruppamentoSelezionato}' />

<c:set var="isGaraElenco" value='${param.isGaraElenco}' />

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<c:choose>
	<c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.inserimentoDitteSMAT")}'>
		<c:set var="inserimentoDitteSMAT" value="SI"/>
	</c:when>
	<c:otherwise>
		<c:set var="inserimentoDitteSMAT" value="NO"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${inserimentoDitteSMAT ne "SI"}'>
		<c:choose>
			<c:when test='${param.tipoDettaglio eq 1 and raggruppamentoSelezionato ne "SI"}'>
				<gene:campoScheda title="Cod.impr.raggruppam." visibile="false" entita="RAGIMP" campo="CODIME9_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODIME9" value="${item[0]}" />
				<gene:archivio titolo="ditte" 
					lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.CODDIC") and gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.NOMDIC"),"gene/impr/impr-lista-popup.jsp?abilitaNuovo=1","")}'
					scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
					schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
					functionId="ditteDitg"
					campi="IMPR.CODIMP;IMPR.NOMIMP;IMPR.CGENIMP;IMPR.CFIMP;IMPR.PIVIMP"
					inseribile="true"
					chiave="RAGIMP_CODDIC_${param.contatore}">
					<gene:campoScheda title="Codice ditta" entita="RAGIMP" campo="CODDIC_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODDIC" value="${item[1]}" obbligatorio="true" />
					<gene:campoScheda title="Ragione sociale" entita="RAGIMP" campo="NOMDIC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMDIC" value="${item[2]}" />
					<gene:campoScheda title="Codice dell'Anagrafico Generale" entita="IMPR" campo="CGENIMP_${param.contatore}" campoFittizio="true" definizione="T20;;;;CGENIMP" value="${item[5]}" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CGENIMP") && fn:contains(listaOpzioniDisponibili, "OP127#")}'/>
					<gene:campoScheda title="Codice fiscale" entita="IMPR" campo="CFIMP_${param.contatore}" campoFittizio="true" definizione="T16;;;;" value="${item[6]}" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CFIMP")}'/>
					<gene:campoScheda title="Partita I.V.A." entita="IMPR" campo="PIVIMP_${param.contatore}" campoFittizio="true" definizione="T14;;;;" value="${item[7]}" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.PIVIMP")}'/>
				</gene:archivio>
				<gene:campoScheda title="Quota di partecipazione"  entita="RAGIMP" campo="QUODIC_${param.contatore}" campoFittizio="true" definizione="F9.5;;;PRC;QUODIC" value="${item[3]}" visibile="${isGaraElenco ne '1' }"/>
			</c:when>
			<c:when test='${param.tipoDettaglio eq 1 and raggruppamentoSelezionato eq "SI"}'>
				<gene:campoScheda title="Cod.impr.raggruppam." visibile="false" entita="RAGIMP" campo="CODIME9_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODIME9" value="${item[0]}" />
				<c:choose>
					<c:when test='${modo eq "NUOVO"}'>
						<gene:campoScheda title="Codice ditta" entita="RAGIMP" campo="CODDIC_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODDIC" value="${item[1]}" obbligatorio="true" modificabile="false"/>
						<gene:campoScheda title="Ragione sociale" entita="RAGIMP" campo="NOMDIC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMDIC" value="${item[2]}" modificabile="false"/>
						<gene:campoScheda title="Codice fiscale" entita="IMPR" campo="CFIMP_${param.contatore}" campoFittizio="true" definizione="T16;;;;" value="${item[6]}" modificabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CFIMP")}'/>
						<gene:campoScheda title="Partita I.V.A." entita="IMPR" campo="PIVIMP_${param.contatore}" campoFittizio="true" definizione="T14;;;;" value="${item[7]}" modificabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.PIVIMP")}'/>
						<gene:campoScheda title="Quota di partecipazione"  entita="RAGIMP" campo="QUODIC_${param.contatore}" campoFittizio="true" definizione="F9.5;;;PRC;QUODIC" value="${item[3]}" modificabile="false" visibile="${isGaraElenco ne '1' }"/>
					</c:when>
					<c:otherwise>
						<gene:archivio titolo="ditte" 
						lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.CODDIC") and gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.NOMDIC"),"gene/impr/impr-lista-popup.jsp?abilitaNuovo=1","")}'
						scheda=''
						schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
						functionId="ditteDitg"
						campi="IMPR.CODIMP;IMPR.NOMIMP;IMPR.CGENIMP;IMPR.CFIMP;IMPR.PIVIMP"
						inseribile="true"
						chiave="RAGIMP_CODDIC_${param.contatore}">
						<gene:campoScheda title="Codice ditta" entita="RAGIMP" campo="CODDIC_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODDIC" value="${item[1]}" obbligatorio="true" modificabile="false"/>
						<gene:campoScheda title="Ragione sociale" entita="RAGIMP" campo="NOMDIC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMDIC" value="${item[2]}" modificabile="false"/>
						<gene:campoScheda title="Codice dell'Anagrafico Generale" entita="IMPR" campo="CGENIMP_${param.contatore}" campoFittizio="true" definizione="T20;;;;CGENIMP" value="${item[5]}" modificabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CGENIMP") && fn:contains(listaOpzioniDisponibili, "OP127#")}'/>
						<gene:campoScheda title="Codice fiscale" entita="IMPR" campo="CFIMP_${param.contatore}" campoFittizio="true" definizione="T16;;;;" value="${item[6]}" modificabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CFIMP")}'/>
						<gene:campoScheda title="Partita I.V.A." entita="IMPR" campo="PIVIMP_${param.contatore}" campoFittizio="true" definizione="T14;;;;" value="${item[7]}" modificabile="false" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.PIVIMP")}'/>
						</gene:archivio>
						<gene:campoScheda title="Quota di partecipazione"  entita="RAGIMP" campo="QUODIC_${param.contatore}" campoFittizio="true" definizione="F9.5;;;PRC;QUODIC" modificabile="false" value="${item[3]}" visibile="${isGaraElenco ne '1' }"/>
					</c:otherwise>
				</c:choose>
			
			</c:when>
			<c:otherwise>
				<gene:campoScheda title="Cod.impr.raggruppam." visibile="false" entita="RAGIMP" campo="CODIME9_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODIME9" />
				<gene:archivio titolo="ditte" 
					lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.CODDIC") and gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.NOMDIC"),"gene/impr/impr-lista-popup.jsp?abilitaNuovo=1","")}'
					scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
					schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
					functionId="ditteDitg"
					campi="IMPR.CODIMP;IMPR.NOMIMP;IMPR.CGENIMP;IMPR.CFIMP;IMPR.PIVIMP"
					inseribile="true"
					chiave="RAGIMP_CODDIC_${param.contatore}">
					<gene:campoScheda title="Codice ditta" entita="RAGIMP" campo="CODDIC_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODDIC" obbligatorio="true" />
					<gene:campoScheda title="Ragione sociale" entita="RAGIMP" campo="NOMDIC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMDIC" />
					<gene:campoScheda title="Codice dell'Anagrafico Generale" entita="IMPR" campo="CGENIMP_${param.contatore}" campoFittizio="true" definizione="T20;;;;CGENIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CGENIMP") && fn:contains(listaOpzioniDisponibili, "OP127#")}'/>
					<gene:campoScheda title="Codice fiscale" entita="IMPR" campo="CFIMP_${param.contatore}" campoFittizio="true" definizione="T16;;;;" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CFIMP")}'/>
					<gene:campoScheda title="Partita I.V.A." entita="IMPR" campo="PIVIMP_${param.contatore}" campoFittizio="true" definizione="T14;;;;" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.PIVIMP")}'/>
				</gene:archivio>
				<gene:campoScheda title="Quota di partecipazione"  entita="RAGIMP" campo="QUODIC_${param.contatore}" campoFittizio="true" definizione="F9.5;;;PRC;QUODIC" visibile="${isGaraElenco ne '1' }"/>
			</c:otherwise>
		</c:choose>	
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test='${param.tipoDettaglio eq 1 and raggruppamentoSelezionato ne "SI"}'>
				<gene:campoScheda title="Cod.impr.raggruppam." visibile="false" entita="RAGIMP" campo="CODIME9_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODIME9" value="${item[0]}" />
				<gene:archivio titolo="ditte" 
					lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.CODDIC") and gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.NOMDIC"),"gare/v_impr_smat/v_impr_smat-lista-popup.jsp","")}'
					scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
					schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
					campi="V_IMPR_SMAT.CODIMP;V_IMPR_SMAT.NOMIMP;V_IMPR_SMAT.ID_SEDE;V_IMPR_SMAT.ID_FORNITORE;V_IMPR_SMAT.IS_IMPRESA_OA"
					functionId="default"
					inseribile="true"
					chiave="RAGIMP_CODDIC_${param.contatore}">
					<gene:campoScheda title="Codice ditta" entita="RAGIMP" campo="CODDIC_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODDIC" value="${item[1]}" obbligatorio="true" />
					<gene:campoScheda title="Ragione sociale" entita="RAGIMP" campo="NOMDIC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMDIC" value="${item[2]}" />
					<gene:campoScheda campo="ID_SEDE_${param.contatore}" entita="V_IMPR_SMAT" visibile="false" campoFittizio="true" definizione="N10" />
					<gene:campoScheda campo="ID_FORNITORE_${param.contatore}" entita="V_IMPR_SMAT" campoFittizio="true"  definizione="T10" visibile="false" />
					<gene:campoScheda campo="IS_IMPRESA_OA_${param.contatore}" entita="V_IMPR_SMAT" campoFittizio="true"  definizione="N1" visibile="false" />
				</gene:archivio>
				
			</c:when>
			<c:when test='${param.tipoDettaglio eq 1 and raggruppamentoSelezionato eq "SI"}'>
				<gene:campoScheda title="Cod.impr.raggruppam." visibile="false" entita="RAGIMP" campo="CODIME9_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODIME9" value="${item[0]}" />
				<c:choose>
					<c:when test='${modo eq "NUOVO"}'>
						<gene:campoScheda title="Codice ditta" entita="RAGIMP" campo="CODDIC_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODDIC" value="${item[1]}" obbligatorio="true" modificabile="false"/>
						<gene:campoScheda title="Ragione sociale" entita="RAGIMP" campo="NOMDIC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMDIC" value="${item[2]}" modificabile="false"/>
					</c:when>
					<c:otherwise>
						<gene:archivio titolo="ditte" 
						lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.CODDIC") and gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.NOMDIC"),"gare/v_impr_smat/v_impr_smat-lista-popup.jsp","")}'
						scheda=''
						schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
						campi="V_IMPR_SMAT.CODIMP;V_IMPR_SMAT.NOMIMP;V_IMPR_SMAT.ID_SEDE;V_IMPR_SMAT.ID_FORNITORE;V_IMPR_SMAT.IS_IMPRESA_OA"
						functionId="default"
						inseribile="true"
						chiave="RAGIMP_CODDIC_${param.contatore}">
						<gene:campoScheda title="Codice ditta" entita="RAGIMP" campo="CODDIC_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODDIC" value="${item[1]}" obbligatorio="true" modificabile="false"/>
						<gene:campoScheda title="Ragione sociale" entita="RAGIMP" campo="NOMDIC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMDIC" value="${item[2]}" modificabile="false"/>
						<gene:campoScheda campo="ID_SEDE_${param.contatore}" entita="V_IMPR_SMAT" visibile="false" campoFittizio="true" definizione="N10" />
						<gene:campoScheda campo="ID_FORNITORE_${param.contatore}" entita="V_IMPR_SMAT" campoFittizio="true"  definizione="T10" visibile="false" />
						<gene:campoScheda campo="IS_IMPRESA_OA_${param.contatore}" entita="V_IMPR_SMAT" campoFittizio="true"  definizione="N1" visibile="false" />
						</gene:archivio>
					</c:otherwise>
				</c:choose>
			
			</c:when>
			<c:otherwise>
				<gene:campoScheda title="Cod.impr.raggruppam." visibile="false" entita="RAGIMP" campo="CODIME9_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODIME9" />
				<gene:archivio titolo="ditte" 
					lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.CODDIC") and gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.NOMDIC"),"gare/v_impr_smat/v_impr_smat-lista-popup.jsp","")}'
					scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
					schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
					campi="V_IMPR_SMAT.CODIMP;V_IMPR_SMAT.NOMIMP;V_IMPR_SMAT.ID_SEDE;V_IMPR_SMAT.ID_FORNITORE;V_IMPR_SMAT.IS_IMPRESA_OA"
					functionId="default"
					inseribile="true"
					chiave="RAGIMP_CODDIC_${param.contatore}">
					<gene:campoScheda title="Codice ditta" entita="RAGIMP" campo="CODDIC_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODDIC" obbligatorio="true" />
					<gene:campoScheda title="Ragione sociale" entita="RAGIMP" campo="NOMDIC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMDIC" />
					<gene:campoScheda campo="ID_SEDE_${param.contatore}" entita="V_IMPR_SMAT" visibile="false" campoFittizio="true" definizione="N10" />
					<gene:campoScheda campo="ID_FORNITORE_${param.contatore}" entita="V_IMPR_SMAT" campoFittizio="true"  definizione="T10" visibile="false" />
					<gene:campoScheda campo="IS_IMPRESA_OA_${param.contatore}" entita="V_IMPR_SMAT" campoFittizio="true"  definizione="N1" visibile="false" />
				</gene:archivio>
			</c:otherwise>
		</c:choose>	
	</c:otherwise>
</c:choose>

