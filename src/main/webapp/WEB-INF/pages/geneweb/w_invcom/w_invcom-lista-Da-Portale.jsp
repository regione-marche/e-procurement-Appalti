
<%
	/*
	 * Created on 19-10-2010
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

<gene:template file="lista-template.jsp" gestisciProtezioni="true" idMaschera="W_INVCOM-lista-Da-Portale" schema="GENEWEB">
	<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare") && param.tipo eq 1}' >
		<c:set var="where" value="W_INVCOM.IDPRG='PA' and W_INVCOM.COMSTATO=5 and (COMTIPO in ('FS5','FS6') or (COMTIPO in ('FS2','FS4') and exists (select codice from v_gare_eleditte where codice=comkey2)))"/>
		<c:if test="${profiloUtente.abilitazioneGare ne 'A' }" >
			<c:set var="filtroSubstr" value='${gene:getDBFunction(pageContext,"SUBSTR", "codgar;2;21")}' />
			<c:set var="where" value="W_INVCOM.IDPRG='PA' and W_INVCOM.COMSTATO=5 and (COMTIPO in ('FS5','FS6') or (COMTIPO='FS2' and exists (select codice from v_gare_eleditte where codice=comkey2) and exists (select codgar from g_permessi where syscon=${profiloUtente.id } and autori=1 and comkey2 = ${filtroSubstr }))"/>
			<c:set var="where" value="${where } or (COMTIPO='FS4' and exists (select codice from v_gare_eleditte where codice=comkey2) and exists (select codgar from g_permessi where syscon=${profiloUtente.id } and autori=1 and comkey2 = ${filtroSubstr })))"/>
		</c:if>
		<c:if test="${not empty sessionScope.uffint}" >
			<c:set var="where" value="${where} and (COMTIPO in ('FS5','FS6') or (COMTIPO in ('FS2','FS4') and exists (select torn.codgar from torn,V_GARE_ELEDITTE where torn.codgar = V_GARE_ELEDITTE.codgar and V_GARE_ELEDITTE.codice = W_INVCOM.comkey2 and torn.cenint = '${sessionScope.uffint}')))"/> 
		</c:if>
		<gene:setString name="titoloMaschera" value="Lista richieste iscrizione a elenco e aggiornamento" />
		<c:set var="titoloCampo" value='Elenco' />
	</c:if>
	<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare") && param.tipo eq 1}' >
		<c:set var="where" value="W_INVCOM.IDPRG='PA' and W_INVCOM.COMSTATO=5 and (COMTIPO in ('FS5','FS6') or (COMTIPO in ('FS2','FS4') and exists (select codice from v_gare_catalditte where codice=comkey2)))"/>
		<c:if test="${profiloUtente.abilitazioneGare ne 'A' }" >
			<c:set var="filtroSubstr" value='${gene:getDBFunction(pageContext,"SUBSTR", "codgar;2;21")}' />
			<c:set var="where" value="W_INVCOM.IDPRG='PA' and W_INVCOM.COMSTATO=5 and (COMTIPO in ('FS5','FS6') or (COMTIPO='FS2' and exists (select codice from v_gare_catalditte where codice=comkey2) and exists (select codgar from g_permessi where syscon=${profiloUtente.id } and autori=1 and comkey2 = ${filtroSubstr }))"/>
			<c:set var="where" value="${where } or (COMTIPO='FS4' and exists (select codice from v_gare_catalditte where codice=comkey2) and exists (select codgar from g_permessi where syscon=${profiloUtente.id } and autori=1 and comkey2 = ${filtroSubstr })))"/>
		</c:if>
		<c:if test="${not empty sessionScope.uffint}" >
			<c:set var="where" value="${where} and (COMTIPO in ('FS5','FS6') or (COMTIPO in ('FS2','FS4') and exists (select torn.codgar from torn,v_gare_catalditte where torn.codgar = v_gare_catalditte.codgar and v_gare_catalditte.codice = W_INVCOM.comkey2 and torn.cenint = '${sessionScope.uffint}')))"/> 
		</c:if>
		<gene:setString name="titoloMaschera" value="Lista richieste iscrizione a catalogo elettronico e aggiornamento" />
		<c:set var="titoloCampo" value='Catalogo' />
	</c:if>
	<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AcquisiciRegistrazioniPortale") && param.tipo eq 2}' >
		<c:set var="where" value="W_INVCOM.IDPRG='PA' and W_INVCOM.COMSTATO=5 and COMTIPO in ('FS1')"/>
		<gene:setString name="titoloMaschera" value="Lista richieste registrazione" />
	</c:if>
	<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AcquisisciAggiornamentiPortale") && param.tipo eq 3}' >
		<c:set var="where" value="W_INVCOM.IDPRG='PA' and W_INVCOM.COMSTATO=5 and COMTIPO in ('FS5','FS6')"/>
		<gene:setString name="titoloMaschera" value="Lista richieste aggiornamento anagrafico" />
	</c:if>
	
	<gene:setString name="entita" value="W_INVCOM" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
						
		
		<table class="lista">
			<tr>
				<td><gene:formLista entita="W_INVCOM" where="${where}" pagesize="20" tableclass="datilista" gestisciProtezioni="true" sortColumn="2" >
					<gene:campoLista campo="IDPRG" visibile="false" />
					<gene:campoLista campo="IDCOM" visibile="false" />
					<gene:campoLista campo="COMTIPO" href="javascript:acquisisciRegistrazione('${datiRiga.W_INVCOM_COMTIPO}','${datiRiga.W_INVCOM_IDCOM}','${datiRiga.W_INVCOM_COMKEY2}');" />
					<gene:campoLista campo="COMDATASTATO" title="Data inserimento"/>
					<gene:campoLista campo="COMKEY1" visibile="false"/>
					<gene:campoLista campo="COMMITT" title="Richiedente" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoRichiedente"/>
					<gene:campoLista campo="COMKEY2" title="${titoloCampo }" visibile="${param.tipo eq 1}" href="javascript:visualizzaElenco ('${datiRiga.W_INVCOM_COMKEY2}');" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoCOMKEY2"/>
					<input type="hidden" name="tipo" id="tipo" value="${param.tipo}"/>
				</gene:formLista></td>
			</tr>
		</table>
	</gene:redefineInsert>


	<gene:redefineInsert name="listaNuovo" />
	<gene:redefineInsert name="listaEliminaSelezione" />
	
	<gene:javaScript>
		function visualizzaElenco(elenco){
			var chiave = "V_GARE_ELEDITTE.CODGAR=T:$" + elenco;
			var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/gare/gare-scheda.jsp";
			href += "&key=" + chiave;
			//Viene aperta direttamente la pagina "Iscrizione operatori economici"
			<c:choose>
				<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare")}'>
					href += "&activePage=5"; 
				</c:when>
				<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare")}'>
					href += "&activePage=6"; 
				</c:when>
			</c:choose>
			document.location.href = href;
		}
		
		function acquisisciRegistrazione(tipo,idcom,elenco){
			if (tipo == "FS1") {
				var href = "href=gare/commons/popupRegistraDaPortale.jsp?idcom="+idcom+"&registraImpr=1";
				openPopUpCustom(href, "RegistraDaPortale", 550, 350, "yes", "yes");
			}else if (tipo == "FS5") {
				var href = "href=gare/commons/popupAcquisizioneAggiornamentoDaPortale.jsp";
				//href += "&ngara=" + elenco;
				href += "&idcom=" + idcom;
				//href += "&comkey1=" + comkey1;
				openPopUpCustom(href, "acquisisciDaPortale", 850, 500, "yes", "yes");
			}else if(tipo == "FS2" || tipo == "FS4"){
				var href = "href=gare/commons/popupIscriviAggiornaDaPortale.jsp";
				href += "&ngara=" + elenco;
				href += "&idcom=" + idcom;
				//href += "&comkey1=" + comkey1;
				href += "&tipo=" + tipo;
				openPopUpCustom(href, "acquisisciDaPortale", 850, 500, "yes", "yes");
			}else if (tipo == "FS6") {
				var href = "href=gare/commons/popupAcquisizioneVariazioneDatiIdentificativi.jsp";
				href += "&idcom=" + idcom;
				//href += "&comkey1=" + comkey1;
				//href += "&committ=" + committ;
				openPopUpCustom(href, "acquisisciDaPortale", 850, 500, "yes", "yes");
			}
		}
		
		
	</gene:javaScript>

</gene:template>