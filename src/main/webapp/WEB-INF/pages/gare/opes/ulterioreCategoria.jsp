<%
/*
 * Created on: 13-ott-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Sezione ulteriori categorie della gara divisa in lotti con offerta singola */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />


<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<c:set var="categoriaUtilizzata" value="${gene:callFunction3('it.eldasoft.sil.pg.tags.funzioni.CheckCategoriaPresenteLottiFunction', pageContext, item[2], param.codgar)}" />
		<gene:campoScheda entita="OPES" campo="NGARA3_${param.contatore}" visibile="false" campoFittizio="true" definizione="T20;1;;;NGARA_OPS" value="${item[0]}" />
		<gene:campoScheda entita="OPES" campo="NOPEGA_${param.contatore}" visibile="false"  campoFittizio="true" definizione="N7;1;;;NOPEGA" value="${item[1]}" />
		<gene:archivio titolo="Categorie d'iscrizione"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.OPES.CATOFF"), "gene/cais/lista-categorie-iscrizione-popup.jsp", "")}'
			scheda=""
			schedaPopUp=""
			campi="V_CAIS_TIT.CAISIM;V_CAIS_TIT.DESCAT;V_CAIS_TIT.ACONTEC;V_CAIS_TIT.QUAOBB;V_CAIS_TIT.TIPLAVG;V_CAIS_TIT.ISFOGLIA"
			chiave=""
			where=""
			formName="formUlterioreCategoriaGare${param.contatore}" inseribile="false">
			<gene:campoScheda entita="OPES" campo="CATOFF_${param.contatore}" title="Codice" campoFittizio="true" definizione="T30;0;;;CATOFF" value="${item[2]}" modificabile="${categoriaUtilizzata eq 'false'}"/>
			<gene:campoScheda title="Descrizione" campo="DESCAT_${param.contatore}" entita="CAIS" campoFittizio="true" definizione="T2000;0;;;DESCAT" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.OPES.CATOFF") and categoriaUtilizzata eq "false"}' visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.OPES.CATOFF")}' value="${fn:split(listaDescrizioni[param.contatore - 1], '|')[0]}"/>
			<gene:campoScheda campo="ACONTEC_${param.contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;ACONTEC" />
			<gene:campoScheda campo="QUAOBB_${param.contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;QUAOBB" />
			<gene:campoScheda campo="TIPLAVG_${param.contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="N7;0;;;TIPLAVG" value="${fn:split(listaDescrizioni[param.contatore - 1], '|')[1]}"/>
			<gene:campoScheda campo="ISFOGLIA_${param.contatore}" entita="V_CAIS_TIT" visibile="false" campoFittizio="true" definizione="T2;0;;;G_ISFOGLI_T" value="${item[6]}"/>
		</gene:archivio>
		<gene:campoScheda entita="OPES" title="Classifica" campo="NUMCLU_${param.contatore}" campoFittizio="true" visibile="false" definizione="N2;0;;;" value="${item[4]}"/>
		<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_LAVORI_${param.contatore}" campoFittizio="true" definizione="N2;0;G_z09;;G1NUMCLU" value="${item[4]}" />
		<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_FORNITURE_${param.contatore}" campoFittizio="true" definizione="N2;0;G_z07;;G1NUMCLU" value="${item[4]}" />
		<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_SERVIZI_${param.contatore}" campoFittizio="true" definizione="N2;0;G_z08;;G1NUMCLU" value="${item[4]}" />
		<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_LAVORI150_${param.contatore}" campoFittizio="true" definizione="N2;0;G_z11;;G1NUMCLU" value="${item[4]}" />
		<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_SERVIZIPROFESSIONALI_${param.contatore}" campoFittizio="true" definizione="N2;0;G_z12;;G1NUMCLU" value="${item[4]}" />
		<gene:campoScheda title="Importo d'iscrizione" campo="ISCOFF_${param.contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="F15;0;;MONEY;ISCOFF" value="${item[5]}" />
		<gene:campoScheda entita="OPES" campo="DESCOP_${param.contatore}"  title="Note"  campoFittizio="true" definizione="T2000;0;;;DESCOP" value="${item[3]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda entita="OPES" campo="NGARA3_${param.contatore}" visibile="false" campoFittizio="true" definizione="T20;1;;;NGARA_OPS"  value="${param.chiave}"/>
		<gene:campoScheda entita="OPES" campo="NOPEGA_${param.contatore}"  visibile="false" campoFittizio="true" definizione="N7;1;;;NOPEGA" />
		<gene:archivio titolo="Categorie d'iscrizione"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.OPES.CATOFF"), "gene/cais/lista-categorie-iscrizione-popup.jsp", "")}'
			scheda=""
			schedaPopUp=""
			campi="V_CAIS_TIT.CAISIM;V_CAIS_TIT.DESCAT;V_CAIS_TIT.ACONTEC;V_CAIS_TIT.QUAOBB;V_CAIS_TIT.TIPLAVG;V_CAIS_TIT.ISFOGLIA"
			chiave=""
			where=""
			formName="formUlterioreCategoriaGare${param.contatore}" inseribile="false">
			<gene:campoScheda entita="OPES" campo="CATOFF_${param.contatore}" title="Codice" campoFittizio="true" definizione="T30;0;;;CATOFF" />
			<gene:campoScheda title="Descrizione" campo="DESCAT_${param.contatore}" entita="CAIS" campoFittizio="true" definizione="T2000;0;;;DESCAT" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.OPES.CATOFF")}' visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.OPES.CATOFF")}'/>
			<gene:campoScheda campo="ACONTEC_${param.contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;ACONTEC" />
			<gene:campoScheda campo="QUAOBB_${param.contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;QUAOBB" />
			<gene:campoScheda campo="TIPLAVG_${param.contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="N7;0;;;TIPLAVG" />
			<gene:campoScheda campo="ISFOGLIA_${param.contatore}" entita="V_CAIS_TIT" visibile="false" campoFittizio="true" definizione="T2;0;;;G_ISFOGLI_T" />
		</gene:archivio>
		<gene:campoScheda entita="OPES" title="Classifica" campo="NUMCLU_${param.contatore}" campoFittizio="true" visibile="false" definizione="N2;0;;;" />
		<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_LAVORI_${param.contatore}" campoFittizio="true" definizione="N2;0;G_z09;;G1NUMCLU" />
		<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_FORNITURE_${param.contatore}" campoFittizio="true" definizione="N2;0;G_z07;;G1NUMCLU" />
		<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_SERVIZI_${param.contatore}" campoFittizio="true" definizione="N2;0;G_z08;;G1NUMCLU" />
		<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_LAVORI150_${param.contatore}" campoFittizio="true" definizione="N2;0;G_z11;;G1NUMCLU" />
		<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_SERVIZIPROFESSIONALI_${param.contatore}" campoFittizio="true" definizione="N2;0;G_z12;;G1NUMCLU" />
		<gene:campoScheda title="Importo d'iscrizione" campo="ISCOFF_${param.contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="F15;0;;MONEY;ISCOFF" />
		<gene:campoScheda entita="OPES" campo="DESCOP_${param.contatore}"  title="Note"  campoFittizio="true" definizione="T2000;0;;;DESCOP" />
	</c:otherwise>
</c:choose>	
<gene:fnJavaScriptScheda funzione='visualizzaNumeroClassifica("#CAIS_TIPLAVG_${param.contatore}#","#V_CAIS_TIT_ISFOGLIA_${param.contatore}#", "OPES", ${param.contatore}, true)' elencocampi='CAIS_TIPLAVG_${param.contatore};V_CAIS_TIT_ISFOGLIA_${param.contatore}' esegui="false" />
<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLU_CAT_LAVORI_${param.contatore}#", "OPES", ${param.contatore})' elencocampi='NUMCLU_CAT_LAVORI_${param.contatore}' esegui="false" />
<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLU_CAT_FORNITURE_${param.contatore}#", "OPES", ${param.contatore})' elencocampi='NUMCLU_CAT_FORNITURE_${param.contatore}' esegui="false" />
<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLU_CAT_SERVIZI_${param.contatore}#", "OPES", ${param.contatore})' elencocampi='NUMCLU_CAT_SERVIZI_${param.contatore}' esegui="false" />
<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLU_CAT_LAVORI150_${param.contatore}#", "OPES", ${param.contatore})' elencocampi='NUMCLU_CAT_LAVORI150_${param.contatore}' esegui="false" />
<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLU_CAT_SERVIZIPROFESSIONALI_${param.contatore}#", "OPES", ${param.contatore})' elencocampi='NUMCLU_CAT_SERVIZIPROFESSIONALI_${param.contatore}' esegui="false" />