<%
/*
 * Created on: 25/05/2009
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<c:set var="idcrimod" value='${gene:getValCampo(key,"IDCRIMOD")}' />
<c:set var="criterioModificato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.ControlloPunteggiCriteriModelliFunction", pageContext, idcrimod)}' />
<c:set var="titolo" value="${param.titolo}"/>

<c:choose>
	<c:when test="${empty punteggioTecnico }">
		<c:set var="msgPunteggioTecnico" value="non definito"/>
	</c:when>
	<c:otherwise>
		<fmt:formatNumber type="number" value="${punteggioTecnico}" var="punteggioTec" />
		<c:set var="msgPunteggioTecnico" value="${punteggioTec}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${empty punteggioEconomico}">
		<c:set var="msgPunteggioEconomico" value="non definito"/>
	</c:when>
	<c:otherwise>
		<fmt:formatNumber type="number" value="${punteggioEconomico}" var="punteggioEco" />
		<c:set var="msgPunteggioEconomico" value="${punteggioEco}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${empty param.tipoCriterio}">
		<c:set var="tipoCriterio" value="non definito"/>
	</c:when>
	<c:otherwise>
		<c:set var="tipoCriterio" value="${param.tipoCriterio}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${empty punteggioTotale }">
		<c:set var="msgPunteggioTotale" value="non definito"/>
	</c:when>
	<c:otherwise>
		<fmt:formatNumber type="number" value="${punteggioTotale}" var="punteggioTot" />
		<c:set var="msgPunteggioTotale" value="${punteggioTot}"/>
	</c:otherwise>
</c:choose>

<c:set var="initPaginaCrit" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.InizializzazionePaginaCriteriFunction", pageContext)}' scope="request"/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="G1CRIMOD-scheda" schema="GARE">
<gene:setString name="titoloMaschera" value='Criteri di valutazione del modello ${titolo}' />
<gene:redefineInsert name="corpo">
<table class="dettaglio-home">
	<tr>
		<td>
			<table class="arealayout">
				<tr>
					 <br>
					<input type="radio" value="1" name="filtroPaginaCriteri" id="tecnici" <c:if test='${tipoCriterio eq 1}'>checked="checked"</c:if> onclick="javascript:cambiaPaginaCriteri(1);" />
					 Criteri di valutazione busta tecnica
					 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					 <input type="radio" value="2" name="filtroPaginaCriteri" id="economici" <c:if test='${tipoCriterio eq 2}'>checked="checked"</c:if> onclick="javascript:cambiaPaginaCriteri(2);" />
					 Criteri di valutazione busta economica
					 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					 <br>
				</tr>
				<c:if test='${tipoCriterio eq 1}'>
				<tr>					 
					<br>
					<td width="210px"><b>Punteggio tecnico massimo:</b></td>
					<td width="100px" align="left">&nbsp;<span id="punteggioTec">${msgPunteggioTecnico}</span></td>
					<c:if test='${tipoCriterio eq 1}'>
						<td width="120px">${gene:if(!empty SogliaMinTec, "<b>Soglia minima:</b>", "")}</td>
						<fmt:formatNumber type="number" value="${SogliaMinTec}" var="SogliaMinTecFormat" />
						<td align="left">&nbsp;<span id="SogliaMinTec">${SogliaMinTecFormat}</span></td>
					</c:if>
				</tr>
				</c:if>
				<c:if test='${tipoCriterio eq 2}'>
				<tr>
					<br>
					<td width="210px"><b>Punteggio economico massimo:</b></td>
					<td width="100px" align="left">&nbsp;<span id="punteggioEco">${msgPunteggioEconomico}</span></td>
					<c:if test='${tipoCriterio eq 2}'>
						<td width="120px">${gene:if(!empty SogliaMinEco, "<b>Soglia minima:</b>", "")}</td>
						<fmt:formatNumber type="number" value="${SogliaMinEco}" var="SogliaMinEcoFormat" />
						<td align="left">&nbsp;<span id="SogliaMinEco">${SogliaMinEcoFormat}</span></td>
					</c:if>
				</tr>
				</c:if>
				
			</table>
		</td>
	</tr>
	<tr>
		<td>
					<gene:formLista entita="GOEVMOD" where='GOEVMOD.IDCRIMOD = ${idcrimod} and GOEVMOD.TIPPAR=${tipoCriterio}' tableclass="datilista" sortColumn="4;6;7;3;"
						gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGOEV" pagesize="25" >					
														
						<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
							<c:if test="${currentRow >= 0 and !(datiRiga.GOEV_LIVPAR eq 2)}">
								<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
									<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.GOEVMOD-scheda")}' >
										<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza criterio di valutazione"/>
									</c:if>
								</gene:PopUp>
		
							</c:if>
								<input type="checkbox" name="keys" value="${chiaveRiga}"  />
						</gene:campoLista>
						<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.GOEVMOD-scheda") and !(datiRiga.GOEVMOD_LIVPAR eq 2)}'/>				
						<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
						<gene:campoLista campo="ID" visibile="false" />
						<gene:campoLista campo="NECVAN" visibile="false" />
						<gene:campoLista campo="NORPAR"  title="N." value="${gene:if(datiRiga.GOEVMOD_LIVPAR eq 2, '', datiRiga.GOEVMOD_NORPAR)}"  width="10" ordinabile="false"/>
						<gene:campoLista campo="TIPPAR"  value="${gene:if(datiRiga.GOEVMOD_LIVPAR eq 2, '', datiRiga.GOEVMOD_TIPPAR)}" width="130" ordinabile="false"  visibile="false"/>
						<gene:campoLista campo="NECVAN1"  headerClass="sortable" visibile="false"/>
						<gene:campoLista campo="NORPAR1" title="N.sub" value="${gene:if(datiRiga.GOEVMOD_LIVPAR eq 2, datiRiga.GOEVMOD_NORPAR1,'' )}"  width="10" ordinabile="false"/>
						<gene:campoLista campo="DESPAR"  ordinabile="false" href="${gene:if(visualizzaLink, link, '')}"/>
						<gene:campoLista campo="MAXPUN"  ordinabile="false" width="50"/>
						<gene:campoLista campo="LIVPAR" visibile="false" />
						<gene:campoLista campo="MAXPUNFIT" campoFittizio = "true" value = "${datiRiga.GOEVMOD_MAXPUN}" definizione = "F24.5" edit="true" visibile="false"/>
						<gene:campoLista campo="TIPPARFIT" campoFittizio = "true" value = "${datiRiga.GOEVMOD_TIPPAR}" definizione = "N7" edit="true" visibile="false" />
						<gene:campoLista campo="LIVPARFIT" campoFittizio = "true" value = "${datiRiga.GOEVMOD_LIVPAR}" definizione = "N7" edit="true" visibile="false" />
						<gene:campoLista title="&nbsp;" width="20">
							<c:if test="${datiRiga.GOEVMOD_LIVPAR ne 3 && gene:checkProt(pageContext, 'MASC.VIS.GARE.G1CRIDEF-scheda')}">
								<a href="javascript:dettaglioModalitaAssegnazionePunteggio('${datiRiga.G1CRIDEF_ID}','${datiRiga.GOEVMOD_ID}');" title="Dettaglio assegnazione punteggio" >
									<img width="16" height="16" title="Dettaglio assegnazione punteggio" alt="Dettaglio assegnazione punteggio" src="${pageContext.request.contextPath}/img/dettaglio-criteri.png"/>
								</a>
							</c:if>
						</gene:campoLista>
						<gene:campoLista campo="FORMATO" title="Formato" entita="G1CRIDEF" where="G1CRIDEF.IDGOEVMOD = GOEVMOD.ID" ordinabile="false" width="100"/>
						<gene:campoLista campo="MODPUNTI" title="Assegnazione punteggio" entita="G1CRIDEF" where="G1CRIDEF.IDGOEVMOD = GOEVMOD.ID" ordinabile="false" width="100"/>
						<gene:campoLista campo="MODMANU" entita="G1CRIDEF" where="G1CRIDEF.IDGOEVMOD = GOEVMOD.ID" visibile="false"/>
						<gene:campoLista campo="FORMULA" entita="G1CRIDEF" where="G1CRIDEF.IDGOEVMOD = GOEVMOD.ID" visibile="false"/>
						<gene:campoLista campo="ID" entita="G1CRIDEF" where="G1CRIDEF.IDGOEVMOD = GOEVMOD.ID" visibile="false"/>
						<gene:campoLista campo="TITOLO" entita="G1CRIMOD" where="G1CRIMOD.ID = GOEVMOD.IDCRIMOD" visibile="false"/>
						<gene:campoLista campo="REGOLA" title="Modalità" campoFittizio="true" definizione="T20" width="180"  gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoRegolaValutazione"/>
						<input type="hidden" name="tipoCriterio" value="${tipoCriterio }"/>
					</gene:formLista >
				
					</td>
	</tr>
	
	<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
	
	<gene:javaScript>
		
	function inizializzaLista(){
		var numeroOccorrenze = ${currentRow}+1;
		if(numeroOccorrenze == 1){
			var livpar = getValue("LIVPARFIT_1");
			if(livpar ==2){
				document.forms[0].keys.style.display= "none";
				document.forms[0].keys.disabled = true;
			}
		}else{
			for(var i=1; i <= numeroOccorrenze; i++){
				var livpar = getValue("LIVPARFIT_" + i);
				if(livpar ==2){
					document.forms[0].keys[i - 1].style.display= "none";
					document.forms[0].keys[i - 1].disabled = true;
				}
		   }
	   }
	   
	}
	inizializzaLista();
            
	function cambiaPaginaCriteri(pagina){
		link =  '${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/goevmod/goevmod-lista.jsp&key=GOEVMOD.IDCRIMOD=T:${idcrimod}&tipoCriterio='+ pagina + "&titolo=${titolo}";
		document.location.href = link;	
	}
	
	function dettaglioModalitaAssegnazionePunteggio(id, idgoevmod){
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/g1cridef/g1cridef-modelli-scheda.jsp";
		href += "&key=G1CRIDEF.ID=N:" + id ;
		href += "&idgoevmod=" + idgoevmod ;
		document.location.href = href;
	}    

	</gene:javaScript>
</table>
	</gene:redefineInsert>
</gene:template>