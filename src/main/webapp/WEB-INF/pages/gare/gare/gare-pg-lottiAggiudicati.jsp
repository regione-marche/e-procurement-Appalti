<%
/*
 * Created on: 02/12/2009
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

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<c:set var="where" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneLottiAggiudicatiFunction", pageContext,key)}' />

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<gene:formLista entita="GARE" where='${where}' tableclass="datilista" sortColumn="1" pagesize="25" >
				<gene:redefineInsert name="listaNuovo" />
				<gene:redefineInsert name="listaEliminaSelezione" />
				<gene:campoLista campo="NGARA" title="Codice lotto" width="100"/>
				<gene:campoLista campo="CODCIG" />
				<gene:campoLista campo="NOT_GAR" />
				<gene:campoLista campo="IMPAPP" />
				<gene:campoLista campo="IAGGIU" />
				<gene:campoLista campo="IMPGAR" />
				<gene:campoLista campo="RICSUB" entita='DITG' where="DITG.CODGAR5=GARE.CODGAR1 AND DITG.NGARA5=GARE.NGARA AND DITG.DITTAO=GARE.DITTA" width="60"/>
			</gene:formLista >
		</td>
	</tr>
</table>
<gene:javaScript>
var selezionaPaginaDefault = selezionaPagina;
var selezionaPagina = selezionaPaginaCustom;
function selezionaPaginaCustom(pageNumber){
	var modcont="${param.modcont }";
	var isAccordoQuadro="${param.isAccordoQuadro }";
	var codcont="${param.codcont }";
	var ncont="${param.ncont }";
	var ngaral="${param.ngaral }";
	var codimp="${param.codimp }";
	document.pagineForm.action += "&modcont=" + modcont + "&isAccordoQuadro=" + isAccordoQuadro + "&codcont=" + codcont + "&ncont=" + ncont + "&ngaral=" + ngaral + "&codimp=" + codimp;
	selezionaPaginaDefault(pageNumber);
}
</gene:javaScript>