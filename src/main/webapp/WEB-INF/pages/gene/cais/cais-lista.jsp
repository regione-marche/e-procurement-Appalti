<%--

/*
 * Created on: 10-apr-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

Lista categorie d'iscrizione

--%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GENE.CAIS-scheda")}'/>
<c:set var="tipoCatSelezionato" value="${gene:if(empty param.TIPO,1,param.TIPO)}"/>

<c:set var="opzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" scope="request"/>

<c:choose>
	<c:when test='${not empty param.filtroArchiviata}'>
		<c:set var="filtroArchiviata" value="${param.filtroArchiviata}" />
	</c:when>
	<c:otherwise>
		<c:set var="filtroArchiviata" value="${filtroArchiviata}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.ordinamento}'>
		<c:set var="ordinamento" value="${param.ordinamento}" />
	</c:when>
	<c:otherwise>
		<c:set var="ordinamento" value="${ordinamento}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${filtroArchiviata =='1' }">
		<c:set var="filtroIsarchi" value=" AND UPPER(CAIS.ISARCHI ) = '1'" />
	</c:when>
	<c:otherwise>
		<c:set var="filtroIsarchi" value=" AND ( CAIS.ISARCHI = '2' or CAIS.ISARCHI = '0' or CAIS.ISARCHI is null ) " />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${empty ordinamento }">
		<%//All'apertura della pagina la lista viene ordinata rispetto al campo V_CAIS_TIT.NUMORD, quindi nella costruzione automatica della %>
		<%//from viene inserita l'entità V_CAIS_TIT, senza la join con CAIS, quindi si deve inserire a mano %>
		<c:set var="where" value="CAIS.TIPLAVG=${tipoCatSelezionato} AND CAIS.CAISIM = V_CAIS_TIT.CAISIM " />
	</c:when>
	<c:otherwise>
		<%//Quando si forza l'ordinamento sulle colonne della lista non viene più inserita la  V_CAIS_TIT nella from, %>
		<%//quindi non si deve gestire la join %>
		<c:set var="where" value="CAIS.TIPLAVG=${tipoCatSelezionato} " />
	</c:otherwise>
</c:choose>


<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="CAIS-lista" >
	<gene:setString name="titoloMaschera" value="Lista categorie d'iscrizione"/>
	
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">
		<table class="lista">
		<tr>
			<td>
				<input type="checkbox" name="filtroArchiviata" value="1" onclick="cambiaFiltroArchiata(this);" <c:if test="${filtroArchiviata == '1'}">checked="checked"</c:if>><c:out value="Categorie archiviate?"></c:out></input>
				${gene:callFunction3("it.eldasoft.gene.tags.functions.GetListaValoriTabellatoFunction", pageContext, "G_038", "tipiCategorie")}
				<c:forEach var="cat" items="${requestScope.tipiCategorie}">
				<input type="radio" name="tipologia" value="${cat.tipoTabellato}" onclick="cambiaTipoCategoria('${cat.tipoTabellato}');" <c:if test="${tipoCatSelezionato == cat.tipoTabellato}">checked="checked"</c:if>><c:out value="${cat.descTabellato}"></c:out></input>
				</c:forEach>
			</td>
		</tr>
		<tr>
			<td>
			<gene:formLista entita="CAIS" sortColumn="7" pagesize="20" tableclass="datilista"
				gestisciProtezioni="true" where="${where} ${filtroIsarchi}" 
				gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreCAIS"> 
				<c:set var="id" value='${gene:getValCampo(chiaveRigaJava, "CAISIM")}'/>
								
				<%-- Se il nome del campo è vuoto non lo gestisce come un campo normale --%>
				<gene:campoLista title="Opzioni" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO") && !(empty datiRiga.CAIS_CODLIV1)}'>
							<gene:PopUpItem title="Nuovo con pari livello" href="nuovoPariLivello('${datiRiga.CAIS_CODLIV1}','${datiRiga.CAIS_CODLIV2}','${datiRiga.CAIS_CODLIV3}','${datiRiga.CAIS_CODLIV4}')"/>
						</c:if>
						<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GENE.CAIS-scheda") }' >
							<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.visualizza" title="Visualizza categoria"/>
						</c:if>
						<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GENE.CAIS-scheda") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
							<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.modifica" title="Modifica categoria"/>
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}' >
							<gene:PopUpItem variableJs="rigaPopUpMenu${currentRow}" href="eliminaCategoria()" title="Elimina categoria" />
						</c:if>
						<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.CAIS.RiniominaCategoria") }' >
							<gene:PopUpItem variableJs="rigaPopUpMenu${currentRow}" href="rinominaCategoria()" title="Rinomina categoria" />
						</c:if>
					</gene:PopUp>
				</gene:campoLista>
				<%-- Campi veri e propri --%>
				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
				<gene:campoLista campoFittizio="true" visibile="${tipoCatSelezionato ne 1}" title="">
					<c:choose>
						<c:when test="${! empty datiRiga.CAIS_CODLIV4}">
							<img alt="Categoria di livello 5" src="${pageContext.request.contextPath}/img/livelloCategoria5.gif" />
						</c:when>
						<c:when test="${! empty datiRiga.CAIS_CODLIV3}">
							<img alt="Categoria di livello 4" src="${pageContext.request.contextPath}/img/livelloCategoria4.gif" />
						</c:when>
						<c:when test="${! empty datiRiga.CAIS_CODLIV2}">
							<img alt="Categoria di livello 3" src="${pageContext.request.contextPath}/img/livelloCategoria3.gif" />
						</c:when>
						<c:when test="${! empty datiRiga.CAIS_CODLIV1}">
							<img alt="Categoria di livello 2" src="${pageContext.request.contextPath}/img/livelloCategoria2.gif" />
						</c:when>
					</c:choose>
				</gene:campoLista>
				<gene:campoLista campo="CAISIM" headerClass="sortable" href="${gene:if(visualizzaLink, link, '')}"/>
				<gene:campoLista campo="DESCAT" headerClass="sortable" />
				<gene:campoLista campo="TIPLAVG" visibile="false" />
				<gene:campoLista campo="TITCAT" headerClass="sortable" />
				<gene:campoLista campo="NUMORD" entita="V_CAIS_TIT" where="CAIS.CAISIM = V_CAIS_TIT.CAISIM" visibile="false" />
				<gene:campoLista campo="CODLIV1" headerClass="sortable" visibile="${tipoCatSelezionato ne 1}" />
				<gene:campoLista campo="CODLIV2" headerClass="sortable" visibile="${tipoCatSelezionato ne 1}" />
				<gene:campoLista campo="CODLIV3" headerClass="sortable" visibile="${tipoCatSelezionato ne 1}" />
				<gene:campoLista campo="CODLIV4" headerClass="sortable" visibile="${tipoCatSelezionato ne 1}" />

				<input type="hidden" id="LIV1" name="LIV1" />
				<input type="hidden" id="LIV2" name="LIV2" />
				<input type="hidden" id="LIV3" name="LIV3" />
				<input type="hidden" id="LIV4" name="LIV4" />
				<input type="hidden" name="TIPO" value="${tipoCatSelezionato}" />	
				<input type="hidden" id="filtroArchiviata" name="filtroArchiviata" value="${filtroArchiviata }"/>	
				<input type="hidden" id="ordinamento" name="ordinamento" value="${ordinamento }"/>			
			</gene:formLista>
			</td>
		</tr>
		<tr>
			<gene:redefineInsert name="pulsanteListaEliminaSelezione" />
			<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
		</tr>
		</table>
  </gene:redefineInsert>

	<gene:javaScript>
	function cambiaTipoCategoria(tipo) {
		document.location.href="ApriPagina.do?"+csrfToken+"&href=gene/cais/cais-lista.jsp?TIPO="+tipo+"&filtroArchiviata="+${filtroArchiviata };
	}
	 
	function nuovoPariLivello(livello1,livello2,livello3,livello4) {
		document.getElementById("LIV1").value=livello1;
		document.getElementById("LIV2").value=livello2;
		document.getElementById("LIV3").value=livello3;
		document.getElementById("LIV4").value=livello4;
		listaNuovo();
	}
	
	function eliminaCategoria(){
		var href = "href=gene/cais/conferma-eliminazione.jsp&chiaveRiga="+chiaveRiga;
		win = openPopUpCustom(href, "confermaEliminaCategoria", 500, 200, "no", "yes");
	
		if(win!=null)
			win.focus();
	}

	function confermaEliminaCategoria(){
		closePopUps();
		bloccaRichiesteServer();
		document.forms[0].key.value=chiaveRiga;
		document.forms[0].metodo.value="elimina";
		document.forms[0].submit();
	}
	
	function rinominaCategoria(){
	 	var href = "href=gene/cais/popup-rinomina.jsp&chiaveRiga="+chiaveRiga;
		win = openPopUpCustom(href, "rinominaCategoria", 500, 300, "no", "yes");
	
		if(win!=null)
			win.focus();
	 	
	}
	
	function cambiaFiltroArchiata(cb){
		var filtroArchiviata="";
		if(cb.checked){
			filtroArchiviata="1";
		}else{
			filtroArchiviata="2";
		}
		document.getElementById("filtroArchiviata").value = filtroArchiviata;
		
		document.forms[0].action+="&filtroArchiviata=" + filtroArchiviata;  
		bloccaRichiesteServer();
		listaVaiAPagina(0);
		
	}
	var listaOrdinaPerDefault = listaOrdinaPer;
	var listaOrdinaPer = listaOrdinaPerCustom;
	function listaOrdinaPerCustom(campo){
		document.getElementById("ordinamento").value=campo;
		listaOrdinaPerDefault(campo);
	} 	
  </gene:javaScript>
</gene:template>
