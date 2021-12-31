<%
/*
 * Created on: 19/12/2013
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<c:set var="ngara" value='${fn:substringBefore(param.chiave, ";")}' />

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="ID_${param.contatore}" entita="DITGAQ" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDDQ" value="${item[0]}" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="DITGAQ" campoFittizio="true" visibile="false" definizione="T20;0;;;G1NGARADQ" value="${item[1]}" />
		<gene:archivio titolo="Ditte"
			lista=''
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
			campi="IMPR.CODIMP;IMPR.NOMIMP"
			chiave="DITGAQ_DITTAO_${param.contatore}"
			where=""
			inseribile="false"
			formName="formArchivioDitte${param.contatore}" >
			<gene:campoScheda campo="DITTAO_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="T10;0;;;G1DITTAODQ" value="${item[2]}" />
			<gene:campoScheda campo="NOMIMP_${param.contatore}" entita="IMPR" campoFittizio="true"  modificabile="false" visibile="true" definizione="T60;0;;;G_NOMIMP" value="${item[3]}" />
		</gene:archivio>
		<gene:campoScheda campo="IAGGIU_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1IAGGIUDQ" value="${item[4]}" />
		<gene:campoScheda campo="RICSUB_${param.contatore}" entita="DITG" campoFittizio="true"  definizione="T2;0;;SN;G1RICSUB" value="${item[5]}" />
		<gene:campoScheda campo="RIDISO_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T2;0;;SN;G1RIDISODQ" value="${item[6]}"/>
		<gene:campoScheda campo="IMPGAR_${param.contatore}" entita="DITGAQ" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPGARDQ" value="${item[7]}">
			<gene:calcoloCampoScheda funzione='calcolaIMPGAR(${param.contatore})' elencocampi='DITGAQ_RIDISO_${param.contatore}'/>
		</gene:campoScheda>
		<gene:campoScheda campo="NQUIET_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T40;0;;;G1NQUIETDQ" value="${item[8]}"/>
		<gene:campoScheda campo="DQUIET_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="D;0;;;G1DQUIETDQ" value="${item[9]}"/>
		<gene:campoScheda campo="ISTCRE_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T50;0;;;G1ISTCREDQ" value="${item[10]}"/>
		<gene:campoScheda campo="INDIST_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T50;0;;;G1INDISTDQ" value="${item[11]}"/>
		<gene:campoScheda campo="RIBAUO_${param.contatore}" entita="DITG" campoFittizio="true"  visibile="false" definizione="F13.9" value="${item[12]}" />
		<gene:campoScheda campo="IMPOFF_${param.contatore}" entita="DITG" campoFittizio="true"  visibile="false" definizione="F15;0" value="${item[13]}" />
		<gene:campoScheda campo="BANAPP_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T100;0;;;G1BANAPPDQ" value="${item[14]}" title="Descrizione banca di riferimento della ditta"/>
		<gene:campoScheda campo="COORBA_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T50;0;;;G1COORBADQ" value="${item[15]}" />
		<gene:campoScheda campo="CODBIC_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T11;0;;;G1CODBICDQ" value="${item[16]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="DITGAQ" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDDQ" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="DITGAQ" campoFittizio="true" visibile="false" definizione="T20;0;;;G1NGARADQ" value="${ngara}"/>
		<gene:campoScheda campo="DITTAO_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T10;0;;;G1DITTAODQ" />
		<gene:campoScheda campo="NOMIMP_${param.contatore}" entita="IMPR" campoFittizio="true"  modificabile="false" definizione="T60;0;;;G_NOMIMP"  />
		<gene:campoScheda campo="IAGGIU_${param.contatore}" entita="DITGAQ" campoFittizio="true" definizione="F15;0;;MONEY;G1IAGGIUDQ" />				
		<gene:campoScheda campo="RICSUB_${param.contatore}" entita="DITG" campoFittizio="true"  definizione="T2;0;;SN;G1RICSUB"  />
		<gene:campoScheda campo="RIDISO_${param.contatore}" entita="DITGAQ" campoFittizio="true"   definizione="T2;0;;SN;G1RIDISODQ"/>
		<gene:campoScheda campo="IMPGAR_${param.contatore}" entita="DITGAQ" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPGARDQ"/>
		<gene:campoScheda campo="NQUIET_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T40;0;;;G1NQUIETDQ" />
		<gene:campoScheda campo="DQUIET_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="D;0;;;G1DQUIETDQ" />
		<gene:campoScheda campo="ISTCRE_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T50;0;;;G1ISTCREDQ" />
		<gene:campoScheda campo="INDIST_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T50;0;;;G1INDISTDQ" />
		<gene:campoScheda campo="RIBAUO_${param.contatore}" entita="DITG" campoFittizio="true"  visibile="false" definizione="F13.9"  />
		<gene:campoScheda campo="IMPOFF_${param.contatore}" entita="DITG" campoFittizio="true"  visibile="false" definizione="F15;0"  />
		<gene:campoScheda campo="BANAPP_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T100;0;;;G1BANAPPDQ" title="Descrizione banca di riferimento della ditta"/>
		<gene:campoScheda campo="COORBA_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T50;0;;;G1COORBADQ" />
		<gene:campoScheda campo="CODBIC_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T11;0;;;G1CODBICDQ" />
	</c:otherwise>
</c:choose>





