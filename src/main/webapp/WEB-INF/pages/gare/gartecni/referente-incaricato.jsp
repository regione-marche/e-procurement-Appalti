<%
/*
 * Created on: 05/05/2010
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

<c:choose>
<c:when test="${param.tipoDettaglio eq 1}">			
			<gene:campoScheda campo="CODGAR_${param.contatore}" entita="GARTECNI" campoFittizio="true" visibile="false" definizione="T21;1;;;G1CODGART" value="${item[0]}" />
			<gene:campoScheda campo="NUMTEC_${param.contatore}" entita="GARTECNI" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NUMTEC" value="${item[1]}" />
			<gene:archivio titolo="Tecnici" 
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GARTECNI.CODTEC"), "gene/tecni/tecni-lista-popup.jsp", "")}'
				scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
				schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
				campi="TECNI.CODTEC;TECNI.NOMTEC"
				chiave="GARTECNI_CODTEC_${param.contatore}"
				formName="formReferenti${param.contatore}"
				inseribile="true">
				<gene:campoScheda campo="CODTEC_${param.contatore}" entita="GARTECNI" campoFittizio="true" definizione="T10;0;;;G1CODTEC" value="${item[2]}" />
				<gene:campoScheda campo="NOMTEC_${param.contatore}" entita="GARTECNI" campoFittizio="true" definizione="T161;0;;;G1NOMTEC" value="${item[3]}" /> 
			</gene:archivio>
			<gene:campoScheda campo="INCTEC_${param.contatore}" entita="GARTECNI" campoFittizio="true" definizione="N7;0;A1060;;G1INCTEC" value="${item[4]}" />
			
</c:when>
<c:otherwise>
			<gene:campoScheda campo="CODGAR_${param.contatore}" entita="GARTECNI" campoFittizio="true" visibile="false" definizione="T21;1;;;G1CODGART" value="${param.chiave}" />
			<gene:campoScheda campo="NUMTEC_${param.contatore}" entita="GARTECNI" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NUMTEC" />
			<gene:archivio titolo="Tecnici" 
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.GARTECNI.CODTEC"), "gene/tecni/tecni-lista-popup.jsp", "")}'
				scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
				schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
				campi="TECNI.CODTEC;TECNI.NOMTEC"
				chiave="GARTECNI_CODTEC_${param.contatore}"
				formName="formReferenti${param.contatore}"
				inseribile="true">
				<gene:campoScheda campo="CODTEC_${param.contatore}" entita="GARTECNI" campoFittizio="true" definizione="T10;0;;;G1CODTEC" />
				<gene:campoScheda campo="NOMTEC_${param.contatore}" entita="GARTECNI" campoFittizio="true" definizione="T161;0;;;G1NOMTEC" /> 
			</gene:archivio>
						 
			<gene:campoScheda campo="INCTEC_${param.contatore}" entita="GARTECNI" campoFittizio="true" definizione="N7;0;A1060;;G1INCTEC" />
	
</c:otherwise>
</c:choose>