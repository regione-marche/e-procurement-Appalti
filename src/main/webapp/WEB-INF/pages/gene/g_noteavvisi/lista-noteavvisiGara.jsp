<%/*
   * Created on 17-set-2009
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   *
   * Lista delle note e avvisi associati ad una gara: questa pagina e' una
   * customizzazione della pagina lista-noteavvisiDiPratica.jsp del progetto Gene
   * (Tale jsp non è stata ridefinita la perchè le modifiche sono specifiche per
   * PG e non per PL)
   */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

	<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaInserisci"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>

	<c:set var="campiKey" value='${fn:split(param.chiave,";")}' />
	<c:forEach begin="1" end="${fn:length(campiKey)}" step="1" varStatus="indicekey">
		<c:set var="strTmp" value='${fn:substringAfter(campiKey[indicekey.index-1], ":")}' />
			<c:choose>
				<c:when test="${indicekey.first}">
					<c:set var="whereKey" value='${whereKey} G_NOTEAVVISI.NOTEKEY1 in (${gene:concat(gene:concat("\'", strTmp), "\'")}, ${gene:concat(gene:concat("\'$", strTmp), "\'")}) or G_NOTEAVVISI.NOTEKEY1 in (select NGARA from GARE where codgar1 in (${gene:concat(gene:concat("\'", strTmp), "\'")}, ${gene:concat(gene:concat("\'$", strTmp), "\'")}) )' />
				</c:when>
				<c:when test="${indicekey.last}">
					<c:set var="whereKey" value='${whereKey} G_NOTEAVVISI.NOTEKEY${indicekey.index}=${gene:concat(gene:concat("\'", strTmp), "\'")}' />
				</c:when>
				<c:otherwise>
					<c:set var="whereKey" value='${whereKey} G_NOTEAVVISI.NOTEKEY${indicekey.index}=${gene:concat(gene:concat("\'", strTmp), "\'")} AND ' />
				</c:otherwise>
			</c:choose>
		</c:forEach>

		<c:choose>
			<c:when test='${empty param.listaEntita}' >
				<c:set var="whereKey" value="${whereKey} " />
			</c:when>
			<c:otherwise>
				<c:set var="separatore" value="', '" />
				<c:set var="whereKey" value="${whereKey} AND G_NOTEAVVISI.NOTEENT in ('${fn:replace(param.listaEntita, ';', separatore)}')" />
			</c:otherwise>
		</c:choose>

	<table class="dettaglio-tab-lista">
		<tr>
			<td>
				<gene:formLista entita="G_NOTEAVVISI" pagesize="20" sortColumn="-5" tableclass="datilista" gestisciProtezioni="true" where="${whereKey}" >
		 			<gene:campoLista title="Opzioni" width="50" >
						<c:if test="${currentRow >= 0}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
								<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GENE.G_NOTEAVVISI-scheda")}' >
									<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza nota o avviso"/>
								</c:if>
							</gene:PopUp>
						</c:if>
					</gene:campoLista>
					<% // Campi veri e propri %>
					<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
					<gene:campoLista campo="NOTECOD" visibile="false" />
					<gene:campoLista campo="TIPONOTA" title="Tipo" href="${link}"/>
					<gene:campoLista campo="SYSUTE" entita="USRSYS" title="Autore" definizione="T" where="USRSYS.SYSCON=G_NOTEAVVISI.AUTORENOTA" ordinabile="false" width="100" />
					<gene:campoLista campo="DATANOTA" title="Data" headerClass="sortable"/>
					<gene:campoLista campo="STATONOTA" headerClass="sortable"  />
					<c:set var="parametriConcat" value="G_NOTEAVVISI.NOTEENT;'.${param.schema}'" />
					<c:set var="filtroConcat" value='${gene:getDBFunction(pageContext,"CONCAT", parametriConcat)}' />

					<gene:campoLista campo="C0E_DES" entita="C0ENTIT" title="Oggetto" definizione="T" where='C0ENTIT.C0E_NOM=${filtroConcat}' ordinabile="false" />
					<gene:campoLista campo="TITOLONOTA" headerClass="sortable"/>
					
					<input type="hidden" name="noteAvvisiDellaPratica" value="1" />
				</gene:formLista>
			</td>
		</tr>
		<tr>
			<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
		</tr>
	</table>
	
	<gene:javaScript>
	<c:if test='${not empty param.chiave}'>
		document.forms[0].keyParent.value="${param.chiave}";
		document.forms[0].trovaAddWhere.value="${whereKey}";
	</c:if>
	</gene:javaScript>