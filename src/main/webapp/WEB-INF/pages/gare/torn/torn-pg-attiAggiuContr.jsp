<%
/*
 * Created on: 01-dic-2009
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


<c:choose>
	<c:when test="${param.tipoContratto eq 'stipula' }">
		<c:set var="titoloContratto" value="Visualizza stipula accordo quadro"/>
		<c:set var="codiceProtezione" value='MASC.VIS.GARE.GARE-scheda-contratto'/>
	</c:when>
	<c:when test="${param.tipoContratto eq 'aggEff' }">
		<c:set var="titoloContratto" value="Visualizza aggiudicazione efficace"/>
		<c:set var="codiceProtezione" value='MASC.VIS.GARE.GARE-scheda-contratto'/>
	</c:when>
	<c:otherwise>
		<c:set var="titoloContratto" value="Visualizza contratto"/>
		<c:set var="codiceProtezione" value='MASC.VIS.GARE.GARE-scheda-contratto'/>
	</c:otherwise>
</c:choose>

<table class="dettaglio-tab-lista">
	
	<gene:redefineInsert name="head">
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
		</gene:redefineInsert>
		
	<c:set var="modcont" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetModcontFunction", pageContext, gene:getValCampo(key, "TORN.CODGAR"))}' />
	<c:set var="aqoper" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetAqoperFunction", pageContext, gene:getValCampo(key, "TORN.CODGAR"),"TORN")}' />
	<c:set var="isAccordoQuadro" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsAccordoQuadroFunction", pageContext, gene:getValCampo(key, "TORN.CODGAR"))}'/>
	<c:choose>
		<c:when test="${modcont eq '1' }">
			<c:set var="ordinamento" value="4"/>
		</c:when>
		<c:when test="${empty aqoper || aqoper ne '2'}">
			<c:set var="ordinamento" value="8"/>
		</c:when>
	</c:choose>	
		<!-- Inizio Pagina lista atta contrattuali -->
		
		<tr>
			<td>
				<c:set var="where" value='GARECONT.NGARA = #TORN.CODGAR# and GARECONT.CODIMP is not null'/>
				<gene:formLista entita="GARECONT" where='${where}' tableclass="datilista" sortColumn="${ordinamento }" pagesize="25" gestisciProtezioni="true" >
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
										
					<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
						<c:if test="${currentRow >= 0}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
								<c:if test='${gene:checkProt(pageContext, codiceProtezione)}' >
									<gene:PopUpItem title="${titoloContratto }" href="visualizzaAttoContrattuale('${chiaveRigaJava}', '${datiRiga.GARECONT_CODIMP }', '${datiRiga.GARECONT_NGARAL }')"/>
								</c:if>
							</gene:PopUp>
						</c:if>
					</gene:campoLista>
					
					<gene:campoLista campo="NGARA" visibile="false"  />
					<gene:campoLista campo="NCONT"  visibile="false"  />
					<c:set var="link" value='javascript:visualizzaAttoContrattuale("${chiaveRigaJava}", "${datiRiga.GARECONT_CODIMP }", "${datiRiga.GARECONT_NGARAL }");' />
					<gene:campoLista campo="NGARAL" headerClass="sortable" width="100" visibile="${modcont eq '1' }" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda-contratto"), link, "")}'/>
					<gene:campoLista campo="CODIGA" entita="GARE" where="GARECONT.NGARAL=GARE.NGARA" headerClass="sortable" visibile="${modcont eq '1' }" title="Lotto" width="50" />
					<gene:campoLista campo="CODCIG" entita="GARE" where="GARECONT.NGARAL=GARE.NGARA" headerClass="sortable" visibile="${modcont eq '1' }"/>
					<gene:campoLista campo="NOT_GAR" entita="GARE" where="GARECONT.NGARAL=GARE.NGARA" headerClass="sortable" visibile="${modcont eq '1' }"/>
					<gene:campoLista campo="NOMIMO" title="Ditta aggiudicataria" entita="DITG" where="DITG.NGARA5=GARECONT.NGARA and DITG.CODGAR5=GARECONT.NGARA and DITG.DITTAO=GARECONT.CODIMP" headerClass="sortable" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda-contratto") , link, "")}' visibile="${empty aqoper || aqoper ne '2'}"/>
					<gene:campoLista campo="CODIMP" visibile="false"  />
					<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />
				</gene:formLista>
			</td>
		</tr>
		

</table>
<gene:javaScript>
	
	function visualizzaAttoContrattuale(chiaveRiga,codimp, ngaral){
		var tipocontratto = "${param.tipoContratto }";
		var modcont = "${modcont}";
		var isAccordoQuadro = "${isAccordoQuadro }";
		if(modcont=="2" || isAccordoQuadro == "1"){
			var href = contextPath + "/ApriPagina.do?href=gare/gare/gare-pagine-scheda-attoContrattuale.jsp";
			var ngara = getValCampoChiave(chiaveRiga,"NGARA");
			var ncont = getValCampoChiave(chiaveRiga,"NCONT");
			href += "&key=" + chiaveRiga + "&tipo=" + tipocontratto + "&codimp=" + codimp + "&ngaral=" + ngaral + "&modcont=" + modcont + "&isAccordoQuadro=" + isAccordoQuadro + "&codcont="+ngara+"&ncont="+ncont ;
		}else{
			var chiave = "GARE.NGARA=T:" + ngaral;
			var href = contextPath + "/ApriPagina.do?href=gare/gare/gare-scheda-attoContrattuale.jsp";
			href += "&key=" + chiave + "&tipo=" + tipocontratto + "&codimp=" + codimp;
		}
		href += "&" + csrfToken;
		document.location.href = href;
	}
		
	
	
</gene:javaScript>