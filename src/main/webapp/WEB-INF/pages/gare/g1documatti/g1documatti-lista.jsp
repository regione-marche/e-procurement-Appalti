<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.dittao}'>
		<c:set var="dittao" value="${param.dittao}" />
	</c:when>
	<c:otherwise>
		<c:set var="dittao" value="${dittao}" />
	</c:otherwise>
</c:choose>

<c:set var="isVerificaPDFA" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "uploadFile.verifica.pdfA")}'/>
<c:set var="dpubatti" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select dpubatti from gare1 where ngara = ?", ngara)}' />
<c:set var="isNotPubblicato" value="${empty dpubatti}"/>

<c:set var="dicituraAggiungiDocumento" value="Nuovo" />
<c:set var="dicituraCopiaDocumenti" value="Copia documenti da archivio" />
<c:set var="dicituraArchiviaDocumenti" value="Archivia documenti" />
<c:set var="dicituraTornaElencoDitte" value="Torna a elenco ditte" />

<c:set var="keyparent" value="GARE.NGARA=T:${ngara}" scope="request"/>
<c:set var="genere" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction", pageContext, keyparent)}' scope="request" />

<c:set var="codgar" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select codgar1 from gare where ngara = ?", ngara)}' />

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="G1DOCUMATTI-lista">

	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codgar}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>

	<c:if test="${empty param.nonAggiungereHistory}">
		<gene:insert name="addHistory">  
	        <gene:historyAdd titolo='' id="${keyparent}" replaceParam='' />
		</gene:insert>
	</c:if>

	<c:choose>
		<c:when test="${not empty dittao}">
			<c:set var="nomimo" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select nomimp from impr where codimp=?", dittao)}' />
			<gene:setString name="titoloMaschera" value="Lista documenti della ditta ${nomimo}" />
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${genere eq 1 or genere eq 3}">
					<gene:setString name="titoloMaschera" value="Lista documenti del lotto di gara" />
				</c:when>
				<c:otherwise>
					<gene:setString name="titoloMaschera" value="Lista documenti della gara" />
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>

	<gene:redefineInsert name="head">
		<script type="text/javascript"
			src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript"
			src="${pageContext.request.contextPath}/js/jquery.easytabs.js"></script>
	</gene:redefineInsert>
	<gene:redefineInsert name="corpo">

		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>

		<c:set var="where" value="G1DOCUMATTI.NGARA=? and (G1DOCUMATTI.ISARCHI is null or G1DOCUMATTI.ISARCHI != '1')"/>
		<c:set var="parametri" value="T:${ngara}"/>
		<c:choose>
			<c:when test="${not empty dittao}">
				<c:set var="where" value="${where} and G1DOCUMATTI.DITTAO=?"/>
				<c:set var="parametri" value="${parametri};T:${dittao}"/>
			</c:when>
			<c:otherwise>
				<c:set var="where" value="${where} and G1DOCUMATTI.DITTAO is null"/>
			</c:otherwise>
		</c:choose>

		<table class="dettaglio-tab-lista" style="border-top: 0px;">
		<tr>
			<td>
			${gene:callFunction4("it.eldasoft.gene.tags.functions.ImpostazioneFiltroFunction", pageContext, "G1DOCUMATTI", where, parametri)}
			<gene:formLista entita="G1DOCUMATTI" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="2" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreG1DOCUMATTI">

				<gene:redefineInsert name="listaNuovo">
					<c:if test="${autorizzatoModifiche ne '2'}">
						<c:if test='${gene:checkProtFunz(pageContext, "INS","INS")}'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:apriPopupAggiungiDocumento();" title="${dicituraAggiungiDocumento}" tabindex="1500">
										${dicituraAggiungiDocumento}
									</a>
								</td>
							</tr>
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext, "ALT","copiaDocumenti")}'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:apriModaleCopiaDocumenti();" title="${dicituraCopiaDocumenti}" tabindex="1501">
										${dicituraCopiaDocumenti}
									</a>
								</td>
							</tr>
						</c:if>
						<c:if test='${not isNotPubblicato and gene:checkProtFunz(pageContext, "ALT","archiviaDocumenti")}'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:apriPopupArchiviaDocumenti();" title="${dicituraArchiviaDocumenti}" tabindex="1501">
										${dicituraArchiviaDocumenti}
									</a>
								</td>
							</tr>
						</c:if>
					</c:if>
				</gene:redefineInsert>
				<c:if test="${not (autorizzatoModifiche ne '2')}">
					<gene:redefineInsert name="listaEliminaSelezione"/>
				</c:if>

				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50" visibile="${autorizzatoModifiche ne '2'}">
					<c:if test="${currentRow >= 0 and autorizzatoModifiche ne '2' and empty datiRiga.G1DOCUMATTI_STATODOC}">
						<center>
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}';">
							<c:if test="${(datiRiga.G1DOCUMATTI_TIPOLOGIA eq 1 or datiRiga.G1DOCUMATTI_TIPOLOGIA eq 2) and gene:checkProtFunz(pageContext, 'MOD','MOD')}">
								<gene:PopUpItem title="Modifica documento" href="javascript:apriPopupModificaDocumento('${datiRiga.G1DOCUMATTI_ID}');"/>
							</c:if>
							<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
								<gene:PopUpItem title="Elimina documento" href="javascript:eliminaDocumento();" />
							</c:if>
						</gene:PopUp>
						<input type="checkbox" name="keys" value="${chiaveRiga}" />
						</center>
					</c:if>
				</gene:campoLista>
				<gene:campoLista campo="ID" visibile="false" />
				<gene:campoLista campo="NGARA" visibile="false" />
				<gene:campoLista campo="DITTAO" visibile="false" />
				<gene:campoLista campo="IDPRG" visibile="false" />
				<gene:campoLista campo="IDDOCDIG" visibile="false" />
				<gene:campoLista campo="DIGDESDOC" entita="W_DOCDIG" where="W_DOCDIG.idprg=G1DOCUMATTI.idprg and W_DOCDIG.iddocdig=G1DOCUMATTI.iddocdig" />
				<gene:campoLista campo="DIGTIPDOC" entita="W_DOCDIG" visibile="false" where="W_DOCDIG.idprg=G1DOCUMATTI.idprg and W_DOCDIG.iddocdig=G1DOCUMATTI.iddocdig" />
				<gene:campoLista campo="DIGNOMDOC" href="javascript:visualizzaFileAllegato('${datiRiga.G1DOCUMATTI_IDPRG}','${datiRiga.G1DOCUMATTI_IDDOCDIG}','${datiRiga.W_DOCDIG_DIGNOMDOC}');" entita="W_DOCDIG" where="W_DOCDIG.idprg=G1DOCUMATTI.idprg and W_DOCDIG.iddocdig=G1DOCUMATTI.iddocdig" >
					<c:if test="${isVerificaPDFA eq '1' and datiRiga.W_DOCDIG_DIGTIPDOC eq 'pdf/a'}">
						&nbsp;<img id="imgCheckPDFaDocMessage" src="${pageContext.request.contextPath}/img/pdfa.png" title="File conforme al formato PDF/A" />
					</c:if>
				</gene:campoLista>
				<gene:campoLista campo="TIPOLOGIA" />
				<gene:campoLista campo="STATODOC" visibile="false" />
				<gene:campoLista campo="DATPUB" visibile="false" definizione="D;0;;TIMESTAMP;DATPUB" />
				<gene:campoLista title="" width="20"><c:if test="${datiRiga.G1DOCUMATTI_STATODOC eq 5}"><img width="16" height="16" title="Documento pubblicato su portale il ${datiRiga.G1DOCUMATTI_DATPUB}" alt="Documento pubblicato su portale" src="/Appalti/img/ditta_acquisita.png"></c:if></gene:campoLista>
				<input type="hidden" name="ngara" value="${ngara}">
				<input type="hidden" name="dittao" value="${dittao}">
				<input type="hidden" name="nonAggiungereHistory" value="true">
			</gene:formLista>
			</td>
		</tr>
		<c:if test="${autorizzatoModifiche ne '2'}">
			<jsp:include page="/WEB-INF/pages/gare/g1documatti/modaleCopiaDocumenti.jsp">
				<jsp:param value="${dittao}" name="dittao"/>
				<jsp:param value="${ngara}" name="ngara"/>
			</jsp:include>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<c:if test='${gene:checkProtFunz(pageContext, "INS","INS")}'>
						<INPUT type="button"  class="bottone-azione" value='${dicituraAggiungiDocumento}' title='${dicituraAggiungiDocumento}' onclick="javascript:apriPopupAggiungiDocumento()">&nbsp;
					</c:if>
					<c:if test='${gene:checkProtFunz(pageContext, "ALT","copiaDocumenti")}'>
						<INPUT type="button"  class="bottone-azione" value='${dicituraCopiaDocumenti}' title='${dicituraCopiaDocumenti}' onclick="javascript:apriModaleCopiaDocumenti()">&nbsp;
					</c:if>
					<c:if test='${gene:checkProtFunz(pageContext, "DEL","LISTADELSEL")}'>
						<INPUT type="button"  class="bottone-azione" value='Elimina selezionati' title='Elimina selezionati' onclick="javascript:listaEliminaSelezione()">&nbsp;
					</c:if>
					<br><br>
					<INPUT type="button"  class="bottone-azione" value='${dicituraTornaElencoDitte}' title='${dicituraTornaElencoDitte}' onclick="javascript:historyVaiIndietroDi(1)">&nbsp;
				</td>
			</tr>
		</c:if>
		</table>

	</gene:redefineInsert>

	<gene:javaScript>

		function apriPopupAggiungiDocumento() {
			var ngara = "${ngara}";
			var dittao = "${dittao}";
			var href = "href=gare/g1documatti/g1documatti-scheda-popup.jsp&modo=NUOVO&dittao=" + dittao + "&ngara="+ngara;
			openPopUpCustom(href, "aggiungiDocumento", 800, 500, "yes","yes");
		}

		function apriPopupModificaDocumento(idDocumatti) {
			var ngara = "${ngara}";
			var dittao = "${dittao}";
			var href = "href=gare/g1documatti/g1documatti-scheda-popup.jsp&modo=MODIFICA&dittao=" + dittao + "&ngara="+ngara+"&idDocumatti="+idDocumatti;
			openPopUpCustom(href, "modificaDocumento", 800, 500, "yes","yes");
		}

		function eliminaDocumento() {
			if (confirm('Confermi l\'eliminazione del documento?')) {
				document.forms[0].metodo.value = "elimina";
				document.forms[0].key.value = chiaveRiga;
				bloccaRichiesteServer();
				document.forms[0].submit();
			}
		}

		function apriPopupArchiviaDocumenti() {
			var ngara = "${ngara}";
			var dittao = "${dittao}";
			var href = "href=gare/g1documatti/g1documatti-lista-popup.jsp&dittao=" + dittao + "&ngara="+ngara + "&isarchi=1";
			openPopUpCustom(href, "archiviaDocumenti", 900, 600, "yes","yes");
		}

		function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
			var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
			document.location.href=href+"?idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + dignomdoc;
		}

	</gene:javaScript>
</gene:template>

