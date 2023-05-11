
<%
	/*
	 * Created on 13-Oct-2021
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="where" value="DITG.NGARA5=? and exists(select * from ISCRIZCAT where DITG.DITTAO=ISCRIZCAT.CODIMP and DITG.CODGAR5=ISCRIZCAT.CODGAR and DITG.NGARA5=ISCRIZCAT.NGARA and ISCRIZCAT.CODCAT=?)" />
<c:set var="parametri" value="T:${param.opes_ngara};T:${param.cais_caisim}"/>
${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ImpostazioneFiltroFunction", pageContext, "DITG", where, parametri)}

<gene:template file="lista-template.jsp" gestisciProtezioni="true" idMaschera="lista-operatoriIscritti" schema="GARE">
	<gene:setString name="titoloMaschera" value="${param.cais_caisim} - ${param.cais_descat} - Lista operatori iscritti" />
	<gene:setString name="entita" value="DITG" />
	
	<c:set var="garealboCoordsic" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCoordsicGarealboFunction", pageContext, param.opes_ngara)}' />
	<c:set var="garealboReqtorre" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetReqtorreGarealboFunction", pageContext, param.opes_ngara)}' />
	
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
		<table class="lista">
			<tr>
				<td>
					<gene:formLista entita="DITG" pagesize="20" sortColumn="1" tableclass="datilista" 
						gestisciProtezioni="true" >
						
						<c:set var="link" value='javascript:archivioImpresa("${datiRiga.DITG_DITTAO}");' />
						
						<input type="hidden" name="opes_ngara" value="${param.opes_ngara}" />
						<input type="hidden" name="opes_codgar" value="${param.opes_codgar}" />
						<input type="hidden" name="opes_nopega" value="${param.opes_nopega}" />
						<input type="hidden" name="cais_caisim" value="${param.cais_caisim}" />
						<input type="hidden" name="cais_descat" value="${param.cais_descat}" />						
						<input type="hidden" name="listachiamante" value="lista-operatoriIscritti"/>
						
						<gene:campoLista campo="NPROGG" title="N." width="50"/>
						<gene:campoLista campo="CODGAR5" visibile="false"/>
						<gene:campoLista campo="NGARA5" visibile="false" />
						<gene:campoLista campo="DITTAO" visibile="false"/>				
						<gene:campoLista campo="NOMIMO" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}'/>						
						<gene:campoLista campo="CFIMP" entita='IMPR' where='IMPR.CODIMP=DITG.DITTAO' />
						<gene:campoLista campo="PIVIMP" entita='IMPR' where='IMPR.CODIMP=DITG.DITTAO' />					
						<gene:campoLista campo="ABILITAZ"/>
						<gene:campoLista campo="COORDSIC" visibile="${garealboCoordsic eq '1'}"/>
						<gene:campoLista campo="REQTORRE" visibile="${garealboReqtorre eq '1'}"/>
					</gene:formLista>
				</td>
			</tr>
			<tr>
			<gene:redefineInsert name="listaNuovo">
			<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.CATEGORIEGARA.OperatoriElencoPerCategoriaExportExcel")}'>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:esportaInExcel('${param.opes_ngara}','${param.cais_caisim}','${param.genere}');" title='Esporta in Excel' tabindex="1503">
							Esporta in Excel
						</a>
					</td>
				</tr>
			</c:if>
			</gene:redefineInsert>
			<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
			</tr>
		</table>
	</gene:redefineInsert>

<gene:javaScript>
	function esportaInExcel(ngara,categoria,genere){
	  var act = "${pageContext.request.contextPath}/pg/InitExportOperatoriIscritti.do";
	  var par = "ngara=" + ngara;
	  par += "&categoria=" + categoria + "&genere=" + genere;
	  openPopUpActionCustom(act, par, 'exportExportOperatoriIscritti', 700, 500, "yes", "yes");
	}
	
	function archivioImpresa(codiceImpresa){
	<c:choose>
	<c:when test='${updateLista eq 1}' >
		var href = ("href=gene/impr/impr-scheda-popup.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		openPopUp(href, "schedaImpresa");
	</c:when>
	<c:otherwise>
		var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href + "&key=IMPR.CODIMP=T:" + codiceImpresa;
	</c:otherwise>
	</c:choose>
	}
	
</gene:javaScript>
</gene:template>

