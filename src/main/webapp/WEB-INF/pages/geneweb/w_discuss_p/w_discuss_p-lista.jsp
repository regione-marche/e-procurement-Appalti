<%/*
   * Created on 29/03/2021
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GENEWEB" idMaschera="W_DISCUSS_P-lista">
	<gene:setString name="titoloMaschera" value="Lista conversazioni"/>
	<gene:redefineInsert name="corpo">



		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
		<c:if test='${not empty param.chiave}'>
			<c:set var="campiKey" value='${fn:split(param.chiave,";")}' />
			<c:set var="addKeyRiga" value="" />
			<c:forEach begin="1" end="${fn:length(campiKey)}" step="1" varStatus="indicekey">
				<c:set var="strTmp" value='${fn:substringAfter(campiKey[indicekey.index-1], ":")}' />
				<c:choose>
					<c:when test="${indicekey.last}">
						<c:set var="addKeyRiga" value='${addKeyRiga}W_DISCUSS_P.DISCKEY${indicekey.index}=T:${strTmp}' />
						<c:set var="whereKey" value='${whereKey} W_DISCUSS_P.DISCKEY${indicekey.index}=${gene:concat(gene:concat("\'", strTmp), "\'")}' />
					</c:when>
					<c:otherwise>
						<c:set var="addKeyRiga" value='${addKeyRiga}W_DISCUSS_P.DISCKEY${indicekey.index}=T:${strTmp};' />
						<c:set var="whereKey" value='${whereKey} W_DISCUSS_P.DISCKEY${indicekey.index}=${gene:concat(gene:concat("\'", strTmp), "\'")} AND ' />
					</c:otherwise>
				</c:choose>
			</c:forEach>
	
			<c:set var="whereKey" value="${whereKey} AND W_DISCUSS_P.DISCENT='${param.entita}' " />
		</c:if>

		<table class="lista">
			<tr>
				<td>

		<gene:formLista entita="W_DISCUSS_P" pagesize="20" tableclass="datilista" sortColumn="-7" gestisciProtezioni="true"
				where="${whereKey} " gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_DISCUSS_P" >
		<c:if test='${not empty param.chiave}'>
			<c:set var="key" value="${param.chiave}" />
			<c:set var="keyParent" value="${param.chiave}" />
		</c:if>
				
  		<gene:campoLista title="Opzioni<br><center>${titoloMenu}</center>" width="50">
		
			<c:choose>
			<c:when test='${datiRiga.W_DISCUSS_P_DISCENT eq "GARE"}' >
				<c:set var="entita" value="V_GARE_TORN"/>
				<c:set var="inputFiltro" value="CODGAR=T:$${datiRiga.W_DISCUSS_P_DISCKEY1}"/>
				<c:set var="filtroCampoEntita" value="codgar=#CODGAR#"/>
			</c:when>
			<c:otherwise>
				<c:set var="entita" value="V_GARE_STIPULA"/>
				<c:set var="inputFiltro" value="IDSTIPULA=T:${datiRiga.W_DISCUSS_P_DISCKEY1}"/>
				<c:set var="filtroCampoEntita" value="idstipula=#IDSTIPULA#"/>
			</c:otherwise>
			</c:choose>
		
			<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, entita)}' />
			<c:set var="autorizzatoModifiche" value="1" scope="request" />
			<c:if test='${!empty (filtroLivelloUtente)}'>
				<gene:sqlSelect nome="autori" parametri="${inputFiltro}" tipoOut="VectorString" >
					select autori from g_permessi where ${filtroCampoEntita} and g_permessi.syscon = ${profiloUtente.id}
				</gene:sqlSelect>
				<c:if test='${!empty autori}'>
					<c:set var="autorizzatoModifiche" value="${autori[0]}" scope="request" />
				</c:if>
			</c:if>
		
		
  				<c:set var="risultato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDiscussioneAltriDatiFunction",pageContext,datiRiga.W_DISCUSS_P_DISCID_P)}'/>
  				<c:if test="${currentRow >= 0}" >
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza"/>
						<c:if test='${(autorizzatoModifiche ne "2" and sessionScope.profiloUtente.id eq datiRiga.W_DISCUSS_P_DISCMESSOPE )and gene:checkProt(pageContext, "MASC.VIS.GENEWEB.W_DISCUSS_P-scheda") and gene:checkProt(pageContext, "MASC.MOD.GENEWEB.W_DISCUSS_P-scheda") and gene:checkProtFunz(pageContext, "MOD","MOD")}' >
							<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica"/>
						</c:if>
						<c:if test='${(autorizzatoModifiche ne "2" and sessionScope.profiloUtente.id eq datiRiga.W_DISCUSS_P_DISCMESSOPE) and gene:checkProtFunz(pageContext, "DEL", "LISTADEL")}' >
							<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina" />
						</c:if>
						<c:if test='${(autorizzatoModifiche ne "2" and sessionScope.profiloUtente.id eq datiRiga.W_DISCUSS_P_DISCMESSOPE)}' >
							<input type="checkbox" name="keys" value="${chiaveRigaJava}" />
						</c:if>
					</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<input type="hidden" name="keyAdd" value="${addKeyRiga}" />
			
			<gene:campoLista campo="DISCID_P" visibile="false" />
			<gene:campoLista campo="DISCPRG" visibile="false" />
			<gene:campoLista campo="DISCMESSOPE" visibile="false" />
			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista title="Oggetto" campo="DISCOGGETTO" ordinabile="true" href="${gene:if(visualizzaLink, link, '')}"/>
			<gene:campoLista title="Creata da" campo="SYSUTE" entita="USRSYS" definizione="T" where="USRSYS.SYSCON=W_DISCUSS_P.DISCMESSOPE" ordinabile="true" />
			<gene:campoLista title="Data inserimento" campo="DISCMESSINS" ordinabile="true" />
			<gene:campoLista campoFittizio="true" title="N.messaggi<br>pubblicati" campo="NUMEROMESSAGGI" definizione="N3;0" value="${numeroMessaggi}"/>
			<gene:campoLista campoFittizio="true" title="N.messaggi<br>pubblicati non letti" campo="NUMEROMESSAGGINONLETTI" definizione="N3;0" value="${numeroMessaggioNonLettiUtente}"/>
			<gene:campoLista campoFittizio="true" title="Data ultimo<br>messaggio pubblicato" campo="DATAGGIORNAMENTO" definizione="T20;0" value="${dataAggiornamento}"/>
			<gene:campoLista campo="DISCENT" visibile="false" />
			<gene:campoLista campo="DISCKEY1" visibile="false" />
			
		</gene:formLista>
				</td>
			</tr>
			
			<c:if test='${not (gene:checkProtFunz(pageContext, "INS","LISTANUOVO") and autorizzatoModifiche ne "2")}' >
				<gene:redefineInsert name="pulsanteListaInserisci" />
				<gene:redefineInsert name="listaNuovo" />
			</c:if>
			
			<c:if test='${not (gene:checkProtFunz(pageContext, "DEL", "LISTADELSEL") and autorizzatoModifiche ne "2")}'>
				<gene:redefineInsert name="pulsanteListaEliminaSelezione" />
				<gene:redefineInsert name="listaEliminaSelezione" />
			</c:if>
			<tr>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
			</tr>
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
	<c:if test='${not empty param.chiave}'>
		document.forms[0].keyParent.value="${param.chiave}";
	</c:if>
	</gene:javaScript>
</gene:template>