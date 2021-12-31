<%
/*
 * Created on: 04/06/2014
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

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>

<c:set var="where" value='V_ODA.IDRIC = #MERIC.ID# '/>

<c:set var="id" value='${gene:getValCampo(key, "MERIC.ID")}' />

<c:set var="codcata" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodiceCatalogoRicercaMercatoFunction",  pageContext, id)}'/>
<c:set var="conteggioCategorie" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNumeroCategorieCatalogoOrdineMinimoFunction",  pageContext, codcata)}'/>
<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<gene:formLista entita="V_ODA" where='${where}' tableclass="datilista" sortColumn='3' 
					gestisciProtezioni="true"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreOrdine" pagesize="25" >
				
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
					<jsp:param name="entita" value="MERIC"/>
					<jsp:param name="inputFiltro" value="ID=N:${id}"/>
					<jsp:param name="filtroCampoEntita" value="IDMERIC=${id }"/>
				</jsp:include>
				
				<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
				<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>				

				<gene:campoLista title="Opzioni" width="50">
					<c:if test="${currentRow >= 0}">
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<c:if test='${(autorizzatoModifiche ne "2") && gene:checkProtFunz(pageContext,"INS","LISTANUOVO") && datiRiga.V_ODA_STATO eq 1}' >
								<gene:PopUpItem title="Genera ordine di acquisto" href="generaOrdine('${datiRiga.V_ODA_IDRIC}','${datiRiga.V_ODA_DITTA}')" />
							</c:if>
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.GARECONT-scheda" ) && !empty datiRiga.V_ODA_NGARA}' >
								<gene:PopUpItem title="Visualizza ordine" href="dettaglioOrdine('${datiRiga.V_ODA_NGARA}')"/>
							</c:if>
							<c:if test='${(autorizzatoModifiche ne "2") && gene:checkProtFunz(pageContext, "DEL","DEL") && datiRiga.V_ODA_STATO eq 2 && !empty datiRiga.V_ODA_NGARA}' >
								<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina ordine" />
							</c:if>
						</gene:PopUp>
					</c:if>
				</gene:campoLista>
				
				<c:set var="link" value="javascript:dettaglioOrdine('${datiRiga.V_ODA_NGARA }');" />
				<c:set var="link1" value="javascript:listaOrdini('${datiRiga.V_ODA_NGARA }','${datiRiga.V_ODA_DITTA }');" />
				<gene:campoLista campo="IDRIC" visibile="false" />
				<gene:campoLista campo="NOMEST" headerClass="sortable" href="${gene:if(!empty datiRiga.V_ODA_NGARA, link, '')}"/>
				<gene:campoLista campo="NUMODA" headerClass="sortable" />
				<gene:campoLista campo="NUMPROD" headerClass="sortable" href="${link1}"/>
				<gene:campoLista campo="IAGGIU"  headerClass="sortable"  />
				<gene:campoLista campo="IMPIVA" headerClass="sortable"  />
				<gene:campoLista campo="IMPTOT" headerClass="sortable" />
				<gene:campoLista campo="STATO" headerClass="sortable" />
				<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.VerificaDocumentiSelezioneDitteElenco')}">
					<gene:campoLista title="&nbsp;" width="20" >
						<a href="javascript:verificaDocRichiesti('${datiRiga.V_ODA_DITTA}');" title="Consultazione documenti iscrizione al catalogo" >
							<img width="16" height="16" title="Consultazione documenti iscrizione al catalogo" alt="Consultazione documenti iscrizione al catalogo" src="${pageContext.request.contextPath}/img/documentazione_elenco.png"/>
						</a>
					</gene:campoLista>
				</c:if>
				<c:if test="${conteggioCategorie > 0 }">
					<gene:campoLista title="&nbsp;" width="20" >
						<a href="javascript:controlloOrdineMinimo('${datiRiga.V_ODA_IDRIC}','${datiRiga.V_ODA_DITTA}','${datiRiga.V_ODA_NOMEST}');" title="Controllo importo ordine minimo" >
							<img width="16" height="16" title="Controllo importo ordine minimo" alt="Controllo importo ordine minimo" src="${pageContext.request.contextPath}/img/controlloImporto.png"/>
						</a>
					</gene:campoLista>
				</c:if>
				<gene:campoLista campo="NGARA" visibile="false" />
				<gene:campoLista campo="DITTA" visibile="false" />
			</gene:formLista>
		</td>
	</tr>
</table>

<gene:javaScript>
	//Inizializzazioni per la libreria common-gare.js
	setContextPath("${pageContext.request.contextPath}");
	setGenereGara("20");
		
	function generaOrdine(id,ditta){
		var comando = "href=gare/meric/meric-popup-generaOrdine.jsp";
		comando = comando + "&id=" + id + "&ditta=" + ditta;
		<c:if test="${conteggioCategorie >0 }">
			comando += "&controlloImporto=Si";
		</c:if>
		openPopUpCustom(comando, "generaOrdine", 650, 500, "yes", "yes");
	}	
	
	function dettaglioOrdine(ngara){
			var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/garecont/garecont-scheda.jsp";
			href += "&key=GARECONT.NGARA=T:" + ngara + ";GARECONT.NCONT=N:1";
			var id="${id}";
			href += "&id=" + id;
			document.location.href = href;
			bloccaRichiesteServer();
	}
	
	function listaOrdini(ngara,ditta){
			var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/meric/meric-listaProdotti.jsp";
			var id="${id}";
			if(ngara!=null && ngara!="")
				href += "&key=V_ODAPROD.NGARA=T:" + ngara + ";V_ODAPROD.IDRIC=N:"+id;
			else
				href += "&key=V_ODAPROD.CODIMP=T:" + ditta + ";V_ODAPROD.IDRIC=N:"+id;
			document.location.href = href;
			bloccaRichiesteServer();
	}
	
	function verificaDocRichiesti(ditta){
			
			var codcata = "${codcata }";
			var chiave = "DITG.NGARA5=T:" + codcata;
			chiave += ";DITG.CODGAR5=T:$" + codcata;
			chiave += ";DITG.DITTAO=T:" + ditta;
			/*
			var href = "href=gare/imprdocg/imprdocg-listaPopup.jsp";
			href += "&key="+chiave;
			href += "&tipo=CONSULTAZIONE";
			href += "&genereGara=20";
			href += "&comunicazioniVis=0";
			openPopUpCustom(href, "verificaDocumentiRichiesti", 1100, 550, "yes", "yes");
			*/
			verificaDocumentiRichiesti(chiave,"CONSULTAZIONE",0,"false", "{autorizzatoModifiche}");
		}
		
	function controlloOrdineMinimo(idRicerca,ditta,ragSociale){
		var href = "href=gare/meric/meric-popup-controlloOrdineMinimo.jsp";
		href += "&id=" + idRicerca + "&ditta=" + ditta + "&ragSociale=" + ragSociale;
		openPopUpCustom(href, "controlloImportoOrdineMinimo", 850, 450, "yes", "yes");
	}
</gene:javaScript>