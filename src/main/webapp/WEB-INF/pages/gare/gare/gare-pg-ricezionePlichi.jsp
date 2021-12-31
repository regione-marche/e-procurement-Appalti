<%
/*
 * Created on: 20/11/2008
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

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="tipgarg" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTIPGARGFunction", pageContext, key)}' scope="request"/>
<c:set var="tipscad" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTIPSCADFunction", pageContext, key)}' scope="request"/>
<c:set var="addWhere" value='' />

<c:set var="start" value='${fn:indexOf(key,":")}' />
<c:set var="len" value='${fn:length(key)}' />
<c:set var="ngara" value='${fn:substring(key,start+1,len)}' />

<gene:setString name="titoloMaschera" value='Gara ${ngara}' />

<c:set var="sortColumn" value="5" />

<c:if test="${tipscad=='2'}">
	<c:set var="addWhere" value=" and DITG.INVGAR='1'" />
	<c:set var="sortColumn" value="6" />
</c:if>

<c:if test="${tipscad=='3'}">
	<c:set var="addWhere" value=" and DITG.ESTIMP='1'" />
	<c:set var="sortColumn" value="6" />
</c:if>

<c:set var="addWhere" value="${addWhere } and DITG.RTOFFERTA is null" />

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<gene:formLista entita="DITG" where='DITG.NGARA5 = #GARE.NGARA# ${addWhere}' tableclass="datilista" sortColumn="${sortColumn}"
						gestisciProtezioni="true" pagesize="25" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreDITG">
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_NSCAD"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				
				
				<c:if test='${((autorizzatoModifiche eq "1"))}' >
					<gene:redefineInsert name="listaNuovo">
						<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
							<c:if test='${(((tipscad eq "1") || (tipscad eq "2" && tipgarg eq "1"))&&(autorizzatoModifiche ne "2"))}' >
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:listaNuovo();" title="Aggiungi ditta da anagrafica" tabindex="1501">
											Aggiungi
										</a></td>
								</tr>
							</c:if>
						</c:if>
					</gene:redefineInsert>
					<gene:redefineInsert name="listaEliminaSelezione" />
				</c:if>
				
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<c:if test="${currentRow >= 0}">
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza ditta"/>
							<c:if test='${(autorizzatoModifiche ne "2") and gene:checkProt(pageContext,"FUNZ.VIS.MOD.GARE.DITG-scheda.SCHEDAMOD")}' >	
								<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica ditta" />
							</c:if>
							<c:if test='${(((tipscad eq "1") || (tipscad eq "2" && tipgarg eq "1"))&&(autorizzatoModifiche ne "2")) and gene:checkProt(pageContext,"FUNZ.VIS.DEL.GARE.GARE-scheda.RICPLICHI.LISTADELSEL")}' >							
								<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina ditta" />
							</c:if>
						</gene:PopUp>
						<c:if test='${(((tipscad eq "1") || (tipscad eq "2" && tipgarg eq "1"))&&(autorizzatoModifiche ne "2")) and gene:checkProt(pageContext,"FUNZ.VIS.DEL.GARE.GARE-scheda.RICPLICHI.LISTADELSEL")}' >
							<input type="checkbox" name="keys" value="${chiaveRiga}"  />
						</c:if>			
					</c:if>
				</gene:campoLista>
				<gene:campoLista campo="CODGAR5" visibile="false" />
				<gene:campoLista campo="NGARA5" visibile="false" />
				<gene:campoLista campo="DITTAO" visibile="false" />
				
				<c:set var="visualizzaLink" value='true'/>				
				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
				<gene:campoLista campo="NPROGG" title="N." headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}" visibile="${tipscad=='1'}"/>
				<gene:campoLista campo="NUMORDPL" title="N." headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}" visibile="${tipscad ne '1'}"/>
				<gene:campoLista campo="NOMIMO" headerClass="sortable" visibile="TRUE"/>
				<gene:campoLista campo="NPRDOM" headerClass="sortable" visibile="${tipscad=='1'}"/>
				<gene:campoLista campo="DRICIND" title="Data" headerClass="sortable" visibile="${tipscad=='1'}"/>
				<gene:campoLista campo="ORADOM" title="Ora" headerClass="sortable" visibile="${tipscad=='1'}"/>
				<gene:campoLista campo="MEZDOM" title="Mezzo" headerClass="sortable" visibile="${tipscad=='1'}"/>
				<gene:campoLista campo="RITDOM" title="Stato plico" width="110" headerClass="sortable" visibile="${tipscad=='1'}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoRitiroDITG"/>
				<gene:campoLista campo="NPROFF" headerClass="sortable" visibile="${tipscad=='2'}"/>
				<gene:campoLista campo="DATOFF" title="Data" headerClass="sortable" visibile="${tipscad=='2'}"/>
				<gene:campoLista campo="ORAOFF" title="Ora" headerClass="sortable" visibile="${tipscad=='2'}"/>
				<gene:campoLista campo="MEZOFF" title="Mezzo" headerClass="sortable" visibile="${tipscad=='2'}"/>
				<gene:campoLista campo="RITOFF" title="Stato plico" width="110" headerClass="sortable" visibile="${tipscad=='2'}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoRitiroDITG"/>
				<gene:campoLista campo="NPRREQ" headerClass="sortable" visibile="${tipscad=='3'}"/>
				<gene:campoLista campo="DATREQ" title="Data" headerClass="sortable" visibile="${tipscad=='3'}"/>
				<gene:campoLista campo="ORAREQ" title="Ora" headerClass="sortable" visibile="${tipscad=='3'}"/>
				<gene:campoLista campo="MEZREQ" title="Mezzo" headerClass="sortable" visibile="${tipscad=='3'}"/>
				<gene:campoLista campo="RITREQ" title="Stato plico" width="110" headerClass="sortable" visibile="${tipscad=='3'}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoRitiroDITG"/>
			</gene:formLista >
		</td>
	</tr>
	<c:if test='${(((tipscad eq "1") || (tipscad eq "2" && tipgarg eq "1"))&&(autorizzatoModifiche ne "2"))}' >
		<tr><jsp:include page="/WEB-INF/pages/commons/pulsantiListaPage.jsp" /></tr>
	</c:if>
</table>