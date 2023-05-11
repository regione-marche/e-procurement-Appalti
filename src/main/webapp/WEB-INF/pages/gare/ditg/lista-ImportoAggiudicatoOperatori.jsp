
<%
	/*
	 * Created on 09-07-2010
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

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codgar}'>
		<c:set var="codgar" value="${param.codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="codgar" value="${codgar}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.genereGara}'>
		<c:set var="genereGara" value="${param.genereGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="genereGara" value="${genereGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${genereGara eq 10}'>
		<c:set var="msgTipo" value="dell'elenco" />
	</c:when>
	<c:otherwise>
		<c:set var="msgTipo" value="del catalogo" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.risultatiPerPagina}'>
		<c:set var="risultatiPerPagina" value="${param.risultatiPerPagina}" />
	</c:when>
	<c:otherwise>
		<c:set var="risultatiPerPagina" value="${risultatiPerPagina}" />
	</c:otherwise>
</c:choose>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="where" value="DITG.NGARA5 = '${ngara }' AND DITG.CODGAR5 = '${codgar }' AND DITG.ABILITAZ = 1 AND DITG.DABILITAZ is not null AND DITG.NUMORDPL is not null" />

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneListaImportoAggiudicatoOperatoriFunction" parametro="${ngara }"/>

<jsp:include page="/WEB-INF/pages/gare/gare/bloccaModifica-fasiGara.jsp" >
	<jsp:param name="entita" value="V_GARE_TORN" />
	<jsp:param name="inputFiltro" value="CODGAR=T:${codgar}"/>
	<jsp:param name="filtroCampoEntita" value="codgar = #CODGAR#" />
</jsp:include>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" idMaschera="DITG-listaImportoAggiudicatoOperatori" schema="GARE">
	<gene:setString name="titoloMaschera" value="Prospetto importo aggiudicato nel periodo per gli operatori ${msgTipo } ${ngara }" />
	<gene:redefineInsert name="corpo">
		<gene:redefineInsert name="listaNuovo"/>		
		<gene:redefineInsert name="listaEliminaSelezione"/>	
		<gene:redefineInsert name="addToAzioni" >
			<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.DITG-listaImportoAggiudicatoOperatori.ConteggioImporto")}'>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:conteggioImporto();" title="Calcolo importo aggiudicato" tabindex="1500">
							Calcolo importo aggiudicato
						</a>
					</td>
				</tr>
			</c:if>
		</gene:redefineInsert>
		<table class="lista">
			<tr>
				<td>
					<br>
					Nella lista sottostante sono riportati gli operatori economici attivi ai fini della selezione nelle procedure di gara.
					Per ogni operatore è dettagliato l'importo aggiudicato complessivo, calcolato considerando le procedure, 
					aggiudicate dall'operatore, con data atto aggiudicazione ricadente nel periodo indicato. 
					<br><br>
				</td>
			</tr>
			<tr>
				<td>
					<b>Data ultimo conteggio</b>: ${ctrldata }
				</td>
			</tr>
			<tr>
				<td>
					<b>Importo limite</b>: ${ctrlimp }&nbsp;&euro;
				</td>
			</tr>
			<tr>
				<td>
					<b>Periodo di controllo</b>: ${ctrlgg} giorni, da ${dataCalcolata } a ${dataOggi }
				</td>
			</tr>
			<tr>
				<td><gene:formLista entita="DITG" pagesize="${risultatiPerPagina }" tableclass="datilista" gestisciProtezioni="true" sortColumn="1" where="${where}" gestore="">
					
								
					<gene:campoLista campo="NPROGG" title="N." width="32"/>
					<c:set var="link" value='javascript:archivioImpresa("${datiRiga.DITG_DITTAO}");' />
					<gene:campoLista campo="NOMEST"  entita="IMPR" where="IMPR.CODIMP=DITG.DITTAO" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}'/>
					<gene:campoLista campo="IAGGIUELE" width="100" title="Imp.aggiudicato"/>
					<gene:campoLista campo="DITTAO"  visibile="false"/>
					<gene:campoLista campo="NGARA5"  visibile="false"/>
					<gene:campoLista campo="CODGAR5"  visibile="false"/>
					
					<input type="hidden" id="ngara" name="ngara" value="${ngara }"/>
					<input type="hidden" id="codgar" name="codgar" value="${codgar }"/>
					<input type="hidden" id="genereGara" name="genereGara" value="${genereGara }"/>
					<input type="hidden" id="risultatiPerPagina" name="risultatiPerPagina" value="${risultatiPerPagina }"/>
				</gene:formLista></td>
			</tr>
					
			
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button"  class="bottone-azione" value='Torna a elenco operatori abilitati' title='Torna a elenco operatori abilitati'  onclick="javascript:historyVaiIndietroDi(1);"">&nbsp;
					<c:if test='${autorizzatoModifiche ne 2 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.DITG-listaImportoAggiudicatoOperatori.ConteggioImporto")}'>
						<INPUT type="button"  class="bottone-azione" value='Calcolo importo aggiudicato' title='Calcolo importo aggiudicato' onclick="conteggioImporto();">&nbsp;&nbsp;&nbsp;
					</c:if>
					
				</td>
			</tr>
			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		function conteggioImporto(){
			href = "href=gare/ditg/popupConteggioImportoAggiudicato.jsp";
			href += "&ngara=${ngara }";
			href += "&genereGara=${genereGara }";
			openPopUpCustom(href, "apriConteggioImporto", 500, 300, "no", "no");
		}
		function archivioImpresa(codiceImpresa){
			var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
			document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href;
		}
</gene:javaScript>
</gene:template>