
<%
	/*
	 * Created on 28-Lug-2015
	 *
	 * Copyright (c) Maggioli S.p.A. - Divisione EldaSoft
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


<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>

<c:choose>
	<c:when test='${not empty param.keyAdd}'>
		<c:set var="keyAdd" value="${param.keyAdd}"  />
	</c:when>
	<c:otherwise>
		<c:set var="keyAdd" value="${keyAdd}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.chiaveWSDM}'>
		<c:set var="chiaveWSDM" value="${param.chiaveWSDM}"  />
	</c:when>
	<c:otherwise>
		<c:set var="chiaveWSDM" value="${chiaveWSDM}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.entitaWSDM}'>
		<c:set var="entitaWSDM" value="${param.entitaWSDM}"  />
	</c:when>
	<c:otherwise>
		<c:set var="entitaWSDM" value="${entitaWSDM}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.genere}'>
		<c:set var="genere" value="${param.genere}"  />
	</c:when>
	<c:otherwise>
		<c:set var="genere" value="${genere}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.idconfi}'>
		<c:set var="idconfi" value="${param.idconfi}"  />
	</c:when>
	<c:otherwise>
		<c:set var="idconfi" value="${idconfi}" />
	</c:otherwise>
</c:choose>

<c:set var="idprg" value='${gene:getValCampo(key,"IDPRG")}' scope='request'/>
<c:set var="idcom" value='${gene:getValCampo(key,"IDCOM")}' scope='request'/>

<c:set var="datins" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMDATINSFunction",pageContext,idprg,idcom)}' />

<c:set var="where" value="W_DOCDIG.DIGENT = 'W_INVCOM' AND W_DOCDIG.DIGKEY1 = '${idcom}'"/>

<table class="dettaglio-tab-lista">
	<tr>
		<td><gene:formLista entita="W_DOCDIG" pagesize="0" sortColumn="2"
			where="${where}" tableclass="datilista"
			gestisciProtezioni="true" gestore="">
			<gene:campoLista campo="IDPRG" visibile="false"/>
			<gene:campoLista title="N°" width="40" campo="IDDOCDIG" visibile="false"/>
			
			<gene:campoLista campo="DIGDESDOC" />
			<gene:campoLista campo="DIGNOMDOC" />
			<gene:campoLista title="&nbsp;" width="24">
				<c:set var="nomDoc" value="${gene:string4Js(datiRiga.W_DOCDIG_DIGNOMDOC)}"/>
				<c:set var="nomDoc" value="${fn:replace(nomDoc,'\"','&#34;')}"/>
				<a href="javascript:visualizzaFileAllegato('${datiRiga.W_DOCDIG_IDPRG}','${datiRiga.W_DOCDIG_IDDOCDIG}',${nomDoc},'${datiRiga.W_INVCOM_COMKEY1 }','${datiRiga.W_INVCOM_COMKEY2 }');" title="Visualizza allegato" >
					<img width="24" height="24" title="Visualizza allegato" alt="Visualizza allegato" src="${pageContext.request.contextPath}/img/visualizzafile.gif"/>
				</a>
			<input type="hidden" name="INPUT_IDDOCDG_${currentRow}" id="INPUT_IDDOCDG_${currentRow}" value="${datiRiga.W_DOCDIG_IDDOCDIG}"/>
			<input type="hidden" name="INPUT_IDPRG_${currentRow}" id="INPUT_IDPRG_${currentRow}" value="${datiRiga.W_DOCDIG_IDPRG}"/>
			<input type="hidden" name="INPUT_DIGNOMDOC_${currentRow}" id="INPUT_DIGNOMDOC_${currentRow}" value="${datiRiga.W_DOCDIG_DIGNOMDOC}"/>
			</gene:campoLista>
			<gene:campoLista campo="COMKEY1" entita="W_INVCOM" where="W_INVCOM.IDPRG=W_DOCDIG.IDPRG AND W_INVCOM.IDCOM=W_DOCDIG.DIGKEY1" visibile="false" />
			<gene:campoLista campo="COMKEY2" entita="W_INVCOM" where="W_INVCOM.IDPRG=W_DOCDIG.IDPRG AND W_INVCOM.IDCOM=W_DOCDIG.DIGKEY1" visibile="false"/>
			<input type="hidden" name="chiaveWSDM" id="chiaveWSDM" value="${chiaveWSDM}"/>
			<input type="hidden" name="entitaWSDM" id="entitaWSDM" value="${entitaWSDM}"/>
			<input type="hidden" name="genere" id="genere" value="${genere}"/>
		</gene:formLista></td>
	</tr>

	<gene:redefineInsert name="listaNuovo" />
	<gene:redefineInsert name="listaEliminaSelezione" />
	
	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			&nbsp;
		</td>
	</tr>
	
	<c:if test="${gene:checkProtFunz(pageContext,'ALT','EsportaDocumentiBusta')}">
		<gene:redefineInsert name="addToAzioni" >
			<tr>
				<td class="vocemenulaterale">
					<a href='javascript:openModalDownloadDoc("${datiRiga.W_INVCOM_COMKEY2}","${datiRiga.W_INVCOM_COMKEY1}", "${idcom}" ,"${idprg}","IN","${pageContext.request.contextPath}" );' title='Esporta su file zip' tabindex="1504">
						Esporta su file zip
					</a>
				</td>
			</tr>
		</gene:redefineInsert>	
	</c:if>
</table>

	<jsp:include page="/WEB-INF/pages/gare/commons/modalPopupDownloadAllegatiComunicazioni.jsp" />

	<form name="formVisFirmaDigitale" id="formVisFirmaDigitale" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale.jsp" />
		<input type="hidden" name="idprg" id="idprg" value="" />
		<input type="hidden" name="iddocdig" id="iddocdig" value="" />
		<input type="hidden" name="ckdate" id="ckdate" value="" />
	</form>

<gene:javaScript>
	function visualizzaFileAllegato(idprg,iddocdig,dignomdoc,comkey1,gara) {
		var vet = dignomdoc.split(".");
		var ext = vet[vet.length-1];
		ext = ext.toUpperCase();
		
		tracciamentoDownloadFS12(idprg, iddocdig,gara,comkey1);
		if(ext=='P7M' || ext=='TSD'){
			document.formVisFirmaDigitale.idprg.value = idprg;
			document.formVisFirmaDigitale.iddocdig.value = iddocdig;
			document.formVisFirmaDigitale.ckdate.value = "${datins}";
			document.formVisFirmaDigitale.submit();
		}else{
			var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
			document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
		}
	}	
	
	var selezionaPaginaDefault = selezionaPagina;
	var selezionaPagina = selezionaPaginaCustom;
	function selezionaPaginaCustom(pageNumber){
		var keyAdd="${keyAdd }"
		var chiaveWSDM="${chiaveWSDM }";
		var entitaWSDM="${entitaWSDM }";
		var genere = "${genere }";
		document.pagineForm.action += "&keyAdd=" + keyAdd + "&chiaveWSDM=" + chiaveWSDM + "&entitaWSDM=" + entitaWSDM + "&genere=" + genere;
		selezionaPaginaDefault(pageNumber);
	}
</gene:javaScript>
