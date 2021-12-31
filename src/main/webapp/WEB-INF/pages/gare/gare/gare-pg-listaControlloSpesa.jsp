<%
/*
 * Created on: 09/09/2009
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

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<c:choose>
	<c:when test="${not empty param.modcont and param.modcont ne '' }">
		<c:set var="ngara" value='${param.codcont }'/>
		<c:set var="ncont" value='${param.ncont }'/>
		<c:set var="ngaral" value='${param.ngaral }'/>
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value='${gene:getValCampo(key,"NGARA") }'/>
		<c:set var="ncont" value='1'/>
	</c:otherwise>
</c:choose>

<!-- Poichè non esiste una funzione con 6 parametri, concateno più dati in un unico parametro -->
<c:set var="datiFunzione" value="${ngara}:${ncont}:${ngaral }:${param.modcont}:${param.codimp}"/> 
<c:set var="vuoto" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GestioneListaControlloSpeseFunction", pageContext, datiFunzione)}'/>

<table class="dettaglio-tab-lista">
	
	
	<tr>
		<td>
			<table class="arealayout">
				<tr>
					<td width="300px" >Disponibilità (AQ):</td>
					<td align="right" width="135px"> &nbsp;<span id="importodisponibilita">${disponibilita} &euro;&nbsp;&nbsp;</span></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td width="300px">Prenotato autorizzato (B):</td>
					<td align="right" width="135px">&nbsp;<span id="importoPrenotato">${importoPrenotato} &euro;&nbsp;&nbsp;</span>
					</td>
					<td  align="right" width="135px"><c:if test="${not empty percentualePrenotato}">${percentualePrenotato}&nbsp;%</c:if></td>
					<td></td>
				</tr>
				<tr>
					<td width="300px">Residuo da autorizzare (AQ - B):</td>
					<td align="right" width="135px" >&nbsp;<span id="importoResiduo">${importoResiduo} &euro;&nbsp;&nbsp;</span>
					</td>
					<td  align="right" width="135px"><c:if test="${not empty percentualeResiduo}">${percentualeResiduo}&nbsp;%</c:if></td>
					<td></td>
				</tr>
				<tr>
					<td width="300px">Impegnato effettivo in adesioni e confronti competitivi (C):</td>
					<td align="right" width="135px">&nbsp; <span id="totale">${importoImpegnato} &euro;&nbsp;&nbsp;</span></td>
					<td  align="right" width="135px"><c:if test="${not empty percentualeImpegnato}">${percentualeImpegnato}&nbsp;%</c:if></td>
					<td></td>
				</tr>
				<tr>
					<td width="300px">Residuo da impegnare (B - C):</td>
					<td align="right" width="135px">&nbsp; ${importoResiduoDaImpegnare} &euro;&nbsp;&nbsp;</td>
					<td  align="right" width="135px"><c:if test="${not empty percentualeResiduoDaImpegnare}">${percentualeResiduoDaImpegnare}&nbsp;%</c:if></td>
					<td></td>
					
				</tr>
			</table>
		</td>
	</tr>
	
	<c:set var="where" value="V_SPESE_ADESIONI.NGARA = '${ngara}' and V_SPESE_ADESIONI.NCONT = ${ncont}"/>
	<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />	
	<tr>
		<td>
			<gene:formLista entita="V_SPESE_ADESIONI" where='${where}' tableclass="datilista" sortColumn='2' gestisciProtezioni="true"   pagesize="25" >
				
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				
				<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO") && autorizzatoModifiche ne "2"}'>
				<gene:redefineInsert name="listaNuovo">
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:nuovaSpesa();" title="Nuova prenotazione di spesa" tabindex="1501">
							Nuova prenotazione di spesa
							</a>
						</td>
					</tr>
				</gene:redefineInsert>			
				</c:if>
				<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>

				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<c:if test="${currentRow >= 0}">
						<c:set var="chiaveRigaPrenotazione" value="G1AQSPESA.NGARA=T:${datiRiga.V_SPESE_ADESIONI_NGARA};"/>
						<c:set var="chiaveRigaPrenotazione" value="${chiaveRigaPrenotazione }G1AQSPESA.NCONT=N:${datiRiga.V_SPESE_ADESIONI_NCONT}"/>
						<c:if test="${param.modcont eq '2'}">
							<c:set var="chiaveRigaPrenotazione" value="${chiaveRigaPrenotazione };G1AQSPESA.CODIMP=T:${param.codimp}"/>
						</c:if>
						<c:if test="${param.modcont eq '2' or param.modcont eq '1'}">
							<c:set var="chiaveRigaPrenotazione" value="${chiaveRigaPrenotazione };G1AQSPESA.NGARAL=T:${ngaral}"/>
						</c:if>
						<c:set var="cenintRigaPrenotazione" value="${datiRiga.V_SPESE_ADESIONI_CENINT}"/>
						<c:choose>
							<c:when test="${param.modcont eq '1'}">
								<c:set var="garaAdesione" value="${ngaral}"/>
							</c:when>
							<c:otherwise>
								<c:set var="garaAdesione" value="${datiRiga.V_SPESE_ADESIONI_NGARA}"/>
							</c:otherwise>
						</c:choose>
						<c:set var="chiaveRigaImpegnato" value="V_GARE_ADESIONI.NGARAAQ=T:${garaAdesione};"/>
						<c:set var="chiaveRigaImpegnato" value="${chiaveRigaImpegnato }V_GARE_ADESIONI.CENINT=T:${datiRiga.V_SPESE_ADESIONI_CENINT}"/>
						<c:if test="${param.modcont eq '2'}">
							<c:set var="chiaveRigaImpegnato" value="${chiaveRigaImpegnato };V_GARE_ADESIONI.DITTA=T:${param.codimp}"/>
						</c:if>
						
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}">
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.G1AQSPESA-lista")}' >
								<gene:PopUpItem title="Visualizza prenotazioni spesa" href="javascript:listaVisualizzaPrenotazione('${chiaveRigaPrenotazione}','${cenintRigaPrenotazione}')" />
							</c:if>
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.V_GARE_ADESIONI-lista")}' >
								<gene:PopUpItem title="Visualizza dettaglio impegnato" href="javascript:listaVisualizzaImpegnato('${chiaveRigaImpegnato }')" />
							</c:if>
							
						</gene:PopUp>
					
					</c:if>
				</gene:campoLista>
				
				<c:set var="visualizzaLinkPrenotato" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.G1AQSPESA-lista")}'/>
				<c:set var="linkPrenotato" value="javascript:listaVisualizzaPrenotazione('${chiaveRigaPrenotazione}','${cenintRigaPrenotazione}');" />
				<c:set var="visualizzaLinkImpegnato" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.V_GARE_ADESIONI-lista")}'/>
				<c:set var="linkImpegnato" value="javascript:listaVisualizzaImpegnato('${chiaveRigaImpegnato}');" />
				
				<gene:campoLista campo="CENINT" />
				<gene:campoLista campo="NOMEIN" title="Denominazione"/>
				<gene:campoLista campo="IMPRIC" />
				<gene:campoLista campo="IMPAUT" href="${gene:if(visualizzaLinkPrenotato, linkPrenotato, '')}"/>
				<gene:campoLista campo="IMPIMP" href="${gene:if(visualizzaLinkImpegnato, linkImpegnato, '')}"/>
				<gene:campoLista title="Residuo da impegnare" computed = "true" ordinabile = "false" campo="${gene:getDBFunction(pageContext,'isnull','IMPAUT;0')} - ${gene:getDBFunction(pageContext,'isnull','IMPIMP;0')}" definizione="F24.5;0;;MONEY" visibile="true" />
				<gene:campoLista campo="NGARA" visibile="false"/>
				<gene:campoLista campo="NCONT" visibile="false"/>
				
			</gene:formLista>
		</td>
	</tr>
	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<gene:insert name="pulsanteListaInserisci">
				<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO") && autorizzatoModifiche ne "2"}'>
					<INPUT type="button"  class="bottone-azione" value='Nuova prenotazione di spesa' title='Nuova prenotazione di spesa' onclick="javascript:nuovaSpesa()">
				</c:if>
			</gene:insert>
			&nbsp;
		</td>
	</tr>
</table>

<gene:javaScript>
	
	function listaVisualizzaPrenotazione(chiaveRigaPrenotazione,cenintRigaPrenotazione){
		var href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/g1aqspesa/g1aqspesa-lista.jsp";
		href += "&key=" + chiaveRigaPrenotazione + "&cenint="+cenintRigaPrenotazione;
		document.location.href = href;
	}
	
	function listaVisualizzaImpegnato(chiaveRigaImpegnato){
		var href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/v_gare_adesioni/v_gare_adesioni-lista.jsp";
		href += "&key=" + chiaveRigaImpegnato;
		document.location.href = href;
	}	
	
	function nuovaSpesa(){
		document.forms[0].action+="&ngara=${ngara}&ncont=${ncont}&codimp=${param.codimp}&ngaral=${param.ngaral}";
		document.forms[0].entita.value="G1AQSPESA";
		listaNuovo();
	}
	
	var selezionaPaginaDefault = selezionaPagina;
	var selezionaPagina = selezionaPaginaCustom;
	function selezionaPaginaCustom(pageNumber){
		var modcont="${param.modcont }";
		var isAccordoQuadro="${param.isAccordoQuadro }";
		var codcont="${param.codcont }";
		var ncont="${param.ncont }";
		var ngaral="${param.ngaral }";
		var codimp="${param.codimp }";
		document.pagineForm.action += "&modcont=" + modcont + "&isAccordoQuadro=" + isAccordoQuadro + "&codcont=" + codcont + "&ncont=" + ncont + "&ngaral=" + ngaral + "&codimp=" + codimp;
		selezionaPaginaDefault(pageNumber);
	}
</gene:javaScript>


		
