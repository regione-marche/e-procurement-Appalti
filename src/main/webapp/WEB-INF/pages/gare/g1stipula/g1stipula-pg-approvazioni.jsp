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

<c:set var="idStipula" value='${gene:getValCampo(key, "G1STIPULA.ID")}' />
<c:set var="digitalSignatureProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />
<c:set var="utente" value="${sessionScope.profiloUtente.id}" />
<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDatiStipulaFunction", pageContext, idStipula)}'/>
<c:set var="numeroDocumenti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNumDocumentiDaFirmareFunction", pageContext,idStipula)}'/>

<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
	<jsp:param name="entita" value="V_GARE_STIPULA"/>
	<jsp:param name="inputFiltro" value="${key}"/>
	<jsp:param name="filtroCampoEntita" value="idstipula=${idStipula}"/>
</jsp:include>

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<gene:formLista entita="G1ITERSTIPULA" where="IDSTIPULA=${idStipula}" tableclass="datilista" sortColumn="-1" pagesize="25" >
				<gene:redefineInsert name="listaNuovo" />
				<gene:redefineInsert name="listaEliminaSelezione" />
				<gene:redefineInsert name="addToAzioni" >
					<c:if test='${(abilitazioneGare eq "A" || utente eq requestScope.creatore || utente eq requestScope.assegnatario) and statoStipula ne 6 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.G1STIPULA-scheda.APPRSTIP.InoltraPerApprovazione")}'>
						<tr>
							<td class="vocemenulaterale">
								<a href='javascript:inoltraPerApprovazione();' title='Inoltra per approvazione' tabindex="1501">
									Inoltra per approvazione
								</a>
							</td>
						</tr>
					</c:if>
					<c:if test='${autorizzatoModifiche ne "2" && digitalSignatureProvider eq 3 && statoStipula ne 6}'>
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:checkNumeroDocumenti('${numeroDocumenti}','PG','','', '${contextPath}','');" title="Firma tutti i documenti con richiesta di firma" tabindex="1510">
									Firma documenti
								</a>
							</td>
						</tr>
					</c:if>
				</gene:redefineInsert>	
				
				<gene:campoLista campo="ID" visibile="false"/>
				<gene:campoLista campo="IDSTIPULA" visibile="false"/>
				<gene:campoLista entita="G1STIPULA" campo="SYSCON" where="G1STIPULA.ID=G1ITERSTIPULA.IDSTIPULA" visibile="false"/>
				<gene:campoLista campo="DATAITER" />
				<gene:campoLista campo="DA_UTENTE" visibile="false"/>
				<c:set var="daUtente" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDescrizioneUtenteStipulaFunction", pageContext, datiRiga.G1ITERSTIPULA_DA_UTENTE)}'/>
				<gene:campoLista title="DA:" campo="DA_UTENTE_DESCR" visibile="true" campoFittizio="true" definizione="T80;0;;;G_USYSUTE" value="${daUtente}" />
				<c:set var="aUtente" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDescrizioneUtenteStipulaFunction", pageContext, datiRiga.G1ITERSTIPULA_A_UTENTE)}'/>
				<gene:campoLista title="A:" campo="A_UTENTE_DESCR" visibile="true" campoFittizio="true" definizione="T80;0;;;G_USYSUTE" value="${aUtente}" />
				<gene:campoLista campo="A_UTENTE" visibile="false"/>
				<gene:campoLista campo="TITOLO" />
				<gene:campoLista campo="TESTO" />
			</gene:formLista >
		</td>
	</tr>
</table>

<jsp:include page="/WEB-INF/pages/gene/system/firmadigitale/modalPopupFirmaDigitaleRemota.jsp" >
	<jsp:param name="modalita" value="massiva"/>
	<jsp:param name="numeroDocumenti" value="${numeroDocumenti}"/>
	<jsp:param name="numeroDocumentiFormato" value="${conteggioFormato}"/>
	<jsp:param name="entitaFirma" value="DOCSTIPULA"/>
	<jsp:param name="chiaveFirma" value="${idStipula}"/>
</jsp:include>

<gene:javaScript>
		
		function inoltraPerApprovazione() {
			var idStipula = "${idStipula}";
			var href = "href=gare/g1stipula/g1stipula-InoltraPerApprovazione.jsp?idStipula="+idStipula;
			dim1 = 800;
			dim2 = 500;
			openPopUpCustom(href, "inoltraPerApprovazione", dim1, dim2, "no", "yes");
		}

		function checkNumeroDocumenti(numeroDocumenti,idprg,iddocdig,nome,contextPath,coacod){
			openModal(idprg,iddocdig,nome,contextPath,coacod);
		}
</gene:javaScript>