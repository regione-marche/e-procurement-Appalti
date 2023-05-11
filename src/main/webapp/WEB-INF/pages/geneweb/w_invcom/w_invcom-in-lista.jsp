<%
	/*
	 * Created on 22-Lug-2015
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


<%-- l'oggetto in sessione che contiene i filtri per comunicazioni inviate e ricevute e' il medesimo, per cui al primo accesso svuoto --%>
<c:if test="${not empty param.chiave}">
	<c:remove var="trovaW_INVCOM" scope="session" />
	<c:remove var="filtroComunicazioniIn" scope="session" />
	<c:remove var="filtroComunicazioniOut" scope="session" />
</c:if>

<%--
se la lista è su più pagine, quando si apre la prima volta è disponibile il parametro 'chiave' mentre quando
si passa a un'altra pagina, si utilizza il parametro keyParent perchè il parametro 'chiave' non è più disponibile
--%>
<c:choose>
	<c:when test='${not empty param.chiave}'>
		<c:set var="chiave" value='${param.chiave}'/>
	</c:when>
	<c:when test='${not empty param.keyParent}'>
		<c:set var="chiave" value='${param.keyParent}'/>
	</c:when>
</c:choose>
<c:choose>
			<c:when test='${not empty param.entitaWSDM}'>
				<c:set var="entitaWSDM" value='${param.entitaWSDM}'/>
			</c:when>
			<c:when test='${not empty keyParent}'>
				<c:set var="entitaWSDM" value='${entitaWSDM}'/>
			</c:when>
		</c:choose>
		<c:choose>
			<c:when test='${not empty param.chiaveWSDM}'>
				<c:set var="chiaveWSDM" value='${param.chiaveWSDM}'/>
			</c:when>
			<c:when test='${not empty keyParent}'>
				<c:set var="chiaveWSDM" value='${chiaveWSDM}'/>
			</c:when>
		</c:choose>



<c:set var="keyParentComunicazioni" value="${chiave}" scope="session"/>
<c:if test='${not empty chiave}'>
	<c:if test='${fn:contains(chiave,"G1STIPULA")}'>
		<%--
		Nel caso delle stipule originariamente veniva passato come chiave CODSTIPULA, che però non è la chiave di G1STIPULA.
		Con l'introduzione della validazione automatica delle chiavi per risolvere i problemi di sql injection, la validazione delle chiavi su G1STIPULA non veniva superata
		poichè la chiave dell'entità è ID. Quindi si è dovuto passare in chiave G1STIPULA.ID, però poi in COMKEY1 si deve continuare a memorizzare CODSTIPULA, per mantenere
		la compatibilità con i dati pregressi e col Portale
		 --%>
		<c:set var="whereStipula" value='ID=${gene:getValCampo(chiave,"ID")}' />
		<c:set var="codstipula" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "CODSTIPULA","G1STIPULA",whereStipula)}' />
	</c:if>

	<c:set var="campiKey" value='${fn:split(chiave,";")}' />
	<c:set var="addKeyRiga" value="" />
	<c:set var="whereKey" value="W_INVCOM.IDPRG='PA' AND W_INVCOM.COMENT IS NULL AND W_INVCOM.COMTIPO='FS12' AND W_INVCOM.COMSTATO='3' AND " />
	<c:choose>
		<c:when test='${not empty codstipula}'>
			<c:set var="whereKey" value="W_INVCOM.IDPRG='PA' AND W_INVCOM.COMENT='G1STIPULA' AND W_INVCOM.COMTIPO='FS12' AND W_INVCOM.COMSTATO='3' AND " />
		</c:when>
		<c:otherwise>
			<c:set var="whereKey" value="W_INVCOM.IDPRG='PA' AND W_INVCOM.COMENT IS NULL AND W_INVCOM.COMTIPO='FS12' AND W_INVCOM.COMSTATO='3' AND " />
		</c:otherwise>
	</c:choose>
	
	
	<%-- 
	al momento si rimuove il ciclo facendolo lavorare solo sul primo campo della chiave in quanto le ODA, che usano 2 campi chiave,
	da portale ricevono comunicazioni che usano un solo campo chiave. Nel qual caso in futuro si debba gestire le comunicazioni ricevute su entita'
	con chiave costituita da piu' di un campo occorre ripristinare il presente ciclo e attuare delle modifiche lato portale.

	<c:forEach begin="1" end="${fn:length(campiKey)}" step="1" varStatus="indicekey">
	 --%>
	<c:choose>
		<c:when test='${fn:contains(chiave,"G1STIPULA")}'>
			
			<c:set var="addKeyRiga" value='W_INVCOM.COMKEY1=T:${codstipula}' />
			<c:set var="whereKey" value='${whereKey} W_INVCOM.COMKEY2=${gene:concat(gene:concat("\'", codstipula), "\'")}' />
		</c:when>
		<c:otherwise>
			<c:forEach begin="1" end="1" step="1" varStatus="indicekey">
				<c:set var="strTmp" value='${fn:substringAfter(campiKey[indicekey.index-1], ":")}' />
				<c:choose>
					<c:when test="${indicekey.last}">
						<c:set var="addKeyRiga" value='${addKeyRiga}W_INVCOM.COMKEY${indicekey.index}=T:${strTmp}' />
						<c:set var="whereKey" value='${whereKey} W_INVCOM.COMKEY${indicekey.index+1}=${gene:concat(gene:concat("\'", strTmp), "\'")}' />
					</c:when>
					<c:otherwise>
						<c:set var="addKeyRiga" value='${addKeyRiga}W_INVCOM.COMKEY${indicekey.index}=T:${strTmp};' />
						<c:set var="whereKey" value='${whereKey} W_INVCOM.COMKEY${indicekey.index+1}=${gene:concat(gene:concat("\'", strTmp), "\'")} AND ' />
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</c:otherwise>
	</c:choose>
	
	<%-- Nel caso di ODA si deve inserire in addKeyRiga il valore COMKEY2=1 --%>
	<c:if test="${fn:contains(chiave,'GARECONT') }">
		<c:set var="addKeyRiga" value='${addKeyRiga};W_INVCOM.COMKEY2=T:1' />
	</c:if>
</c:if>


<c:if test="${! empty sessionScope.filtroComunicazioniIn}">
	<c:set var="filtroComunicazioniIn" value=" AND ${sessionScope.filtroComunicazioniIn}" scope="page" />
</c:if>

<%--
chiave=${chiave}*<br/>		
keyParent=${keyParent}*<br/>		
param.keyParent=${param.keyParent}*<br/>		
fn:length(campiKey)=${fn:length(campiKey)}<br/>
campiKey[0]=${campiKey[0]}*<br/>
campiKey[1]=${campiKey[1]}*<br/>
campiKey[2]=${campiKey[2]}*<br/>
whereKey=${whereKey}*<br/>		
addKeyRiga=${addKeyRiga}*<br/>		
 --%>

<c:set var="comkey1" value='${gene:getValCampo(addKeyRiga, "COMKEY1")}'/>

<c:set var="genere" value='${param.genere}'/>
<c:choose>
<%-- 
	<c:when test="${genere eq 1}">
		<c:set var="titoloMaschera" value="Comunicazioni ricevute per il lotto di gara ${comkey1}" />
	</c:when>
--%>
	<c:when test="${genere eq 10}">
		<c:set var="titoloMaschera" value="Comunicazioni ricevute per l'elenco ${comkey1}" />
	</c:when>
	<c:when test="${genere eq 20}">
		<c:set var="titoloMaschera" value="Comunicazioni ricevute per il catalogo ${comkey1}" />
	</c:when>
	<c:when test="${genere eq 11}">
		<c:set var="titoloMaschera" value="Comunicazioni ricevute per l'avviso ${comkey1}" />
	</c:when>
	<c:when test="${genere eq 4}">
		<c:set var="titoloMaschera" value="Comunicazioni ricevute per l'ordine di acquisto ${comkey1}" />
	</c:when>
	<c:when test="${genere eq 40}">
		<%-- Section added for NSO Communications --%>
		<c:set var="titoloMaschera" value="Comunicazioni ricevute per l'ordine ${param.COD_ORD}" />
	</c:when>
	<c:otherwise>
		<c:set var="titoloMaschera" value="Comunicazioni ricevute per la gara ${comkey1}" />
	</c:otherwise>
</c:choose>

<c:set var="idconfi" value='${param.idconfi}'/>
<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, comkey1)}'/>
<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar, idconfi)}'/>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" idMaschera="W_INVCOM-IN-lista" schema="GENEWEB">

	<gene:setString name="titoloMaschera" value="${titoloMaschera}" />

	<gene:redefineInsert name="addToAzioni">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:impostaFiltro();" title='Imposta filtro' tabindex="1503">
					Imposta filtro
				</a>
			</td>
		</tr>
	</gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">
		<table class="lista">
		<c:if test="${!empty sessionScope.filtroComunicazioniIn}">
			<tr>
				<td style="font: 11px Verdana, Arial, Helvetica, sans-serif;">
					<br/>
					<img src="${pageContext.request.contextPath}/img/filtro.gif" alt="Filtro">&nbsp;<span style="color: #ff0028; font-weight: bold;">Lista filtrata</span>
					&nbsp;&nbsp;&nbsp;[ <a href="javascript:annullaFiltro();" ><IMG SRC="${pageContext.request.contextPath}/img/cancellaFiltro.gif" alt="Cancella filtro"></a>
					<a class="link-generico" href="javascript:annullaFiltro();">Cancella filtro</a> ]
				</td>
			</tr>
		</c:if>
			<tr>
				<td>
				<c:choose>
					<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare") }'>
						<c:set var="tipo" value="2" />
					</c:when>
					<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare")}' >
						<c:set var="tipo" value="3" />
					</c:when>
					<c:otherwise>
						<c:set var="tipo" value="1" />
					</c:otherwise>
				</c:choose>
				<c:set var="filtroSoccorsoIstruttorio" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetFiltroSoccorsoIstruttorioFunction", pageContext, tipo)}'/>
				
				<gene:formLista entita="W_INVCOM" pagesize="20" 
					tableclass="datilista" gestisciProtezioni="true" sortColumn="-6" where="${whereKey}${filtroComunicazioniIn}${filtroSoccorsoIstruttorio }" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_INVCOM" pathScheda="geneweb/w_invcom/w_invcom-in-scheda.jsp">
					<gene:campoLista title="Opzioni"
						width="50">
						<c:if test="${currentRow >= 0}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"
								onClick="chiaveRiga='${chiaveRigaJava}'">
								<gene:PopUpItemResource
									resource="popupmenu.tags.lista.visualizza"
									title="Visualizza" />
							</gene:PopUp>
						</c:if>
					</gene:campoLista>
				
					<gene:campoLista campo="IDPRG" visibile="false" />
					<gene:campoLista campo="IDCOM" visibile="false" title="N°" width="40" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
					<gene:campoLista campo="COMMITT" />
					<%--gene:campoLista campo="NOMIMP" entita="IMPR" from="W_PUSER" where="W_INVCOM.COMKEY1 = W_PUSER.USERNOME AND W_PUSER.USERENT = 'IMPR' AND W_PUSER.USERKEY1 = IMPR.CODIMP" title="Mittente" ordinabile="false" / --%>
					<gene:campoLista campo="COMMSGOGG" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
					<gene:campoLista campo="COMDATINS" definizione="D;0;;TIMESTAMP;COMDATINS" width="150" title="Data e ora invio" />
					<gene:campoLista campo="COMNUMPROT" width="80" visibile="${integrazioneWSDM =='1'}" title="Num.prot."/>
					<gene:campoLista campo="COMDATLET" width="150" />
					
					<input type="hidden" name="genere" value="${genere}" />
					<input type="hidden" name="idconfi" value="${idconfi}" />
					<input type="hidden" name="keyAdd" value="${addKeyRiga}" />
					<input type="hidden" name="chiaveWSDM" id="chiaveWSDM" value="${chiaveWSDM}" />
					<input type="hidden" name="entitaWSDM" id="entitaWSDM" value="${entitaWSDM}" />
				</gene:formLista>
				</td>
			</tr>
		</table>
	</gene:redefineInsert>

	<gene:redefineInsert name="listaNuovo" />
	<gene:redefineInsert name="listaEliminaSelezione" />
		
	<gene:javaScript>
		<c:if test='${not empty param.chiave}'>
			document.forms[0].keyParent.value="${param.chiave}";
		</c:if>

		function impostaFiltro(){
			var comando = "href=geneweb/w_invcom/w_invcom-in-popup-filtro-trova.jsp&integrazioneWSDM=${integrazioneWSDM}";
			openPopUpCustom(comando, "impostaFiltro", 800, 320, "yes", "yes");
		}

		function annullaFiltro(){
			var comando = "href=gare/commons/popup-filtro.jsp&annulla=6";
			openPopUpCustom(comando, "impostaFiltro", 10, 10, "no", "no");
		}
	</gene:javaScript>
	
</gene:template>