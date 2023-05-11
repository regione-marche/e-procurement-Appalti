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

<c:set var="genereGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction",  pageContext,keyParent)}'/>
<%/*La pagina può essere richiamata anche da TORN per le gare divise a lotti con offerta unica*/ %>
<c:choose>
	<c:when test='${genereGara eq "3"}'>
		<%/*gara divisa a lotti con offerta unica*/ %>
		<c:set var="entita" value='TORN_GARSED'/>
		<c:set var="codiceGara" value='${gene:getValCampo(keyParent,"CODGAR")}' />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
		<c:set var="entita" value='GARSED'/>
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${modo eq "NUOVO"}' >
		<c:choose>
			<c:when test='${genereGara eq "3"}'>
				<%/*gara divisa a lotti con offerta unica*/ %>
				<c:set var="numeroGara" value='${gene:getValCampo(keyParent, "TORN.CODGAR")}' />
			</c:when>
			<c:otherwise>
				<c:set var="numeroGara" value='${gene:getValCampo(keyParent, "GARE.NGARA")}' />
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestionePersoneSedutaGaraFunction" parametro="${key}" />	
		<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneSospensioneSedutaGaraFunction" parametro="${key}" />	
		<c:set var="numeroGara" value='${gene:getValCampo(key, "GARSED.NGARA")}' />
		<c:set var="numeroSeduta" value='${gene:getValCampo(key, "GARSED.NUMSED")}' />
	</c:otherwise>
</c:choose>		

<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneSeduteGaraFunction",  pageContext,genereGara,numeroGara)}'/>


<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GARSED-scheda">

<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>	
</gene:redefineInsert>
	
	<c:choose>
		<c:when test='${genereGara eq "3" and modo eq "NUOVO"}'>
			<gene:setString name="titoloMaschera" value='Nuova seduta della gara ${codiceGara}'/>
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, entita)}'/>
		</c:otherwise>
	</c:choose>
	
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="GARSED" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGARSED" >
		
		<gene:redefineInsert name="addToAzioni" >
			<c:if test='${modo eq "VISUALIZZA" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARSED-scheda.ComponentiCommissione")}'>
			<tr>
				<c:choose>
					<c:when test='${isNavigazioneDisattiva ne "1"}'>
						<td class="vocemenulaterale">
							<a href='javascript:apriComponentiCommissione();' title="Componenti commissione di gara" tabindex="1502">
								Componenti commissione di gara 
							</a>
						</td>
					</c:when>
					<c:otherwise>
						<td>
							Componenti commissione di gara
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
		</c:if>
		</gene:redefineInsert>
		
		
		<c:if test='${!gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.C0OGGASS") && gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARSED-scheda.docAssociatiSedute")}'>
			<gene:redefineInsert name="addToDocumenti">
			<tr>
				<c:choose>
		        <c:when test='${isNavigazioneDisabilitata ne "1"}'>
		          <td class="vocemenulaterale">
								<a href="javascript:documentiAssociati();" title="Documenti associati" tabindex="1511">
									${gene:resource("label.tags.template.documenti.documentiAssociati")}
								  <c:if test="${not empty requestScope.numRecordDocAssociati}">(${requestScope.numRecordDocAssociati})</c:if>
								</a>
		   				</td>
		        </c:when>
		        <c:otherwise>
		          <td>
							  ${gene:resource("label.tags.template.documenti.documentiAssociati")}
								  <c:if test="${not empty requestScope.numRecordDocAssociati}">(${requestScope.numRecordDocAssociati})</c:if>
						  </td>
		        </c:otherwise>
				</c:choose>
			</tr>
			</gene:redefineInsert>	
		</c:if>	
								
			<gene:gruppoCampi idProtezioni="GARSED">
				<gene:campoScheda>
					<td colspan="2"><b>Seduta di gara</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="NGARA" visibile="false" value='${fn:substringAfter(keyParent, ":")}'/>
				<gene:campoScheda campo="NUMSED" visibile="false" />
				<gene:campoScheda campo="TIPSED" defaultValue="1"/>
				<gene:campoScheda campo="FASE" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoFASE"/>
				<gene:campoScheda campo="DATVERB" >
					<gene:checkCampoScheda funzione='controlloValorizzazioneCampo("#GARSED_ORAFIN#","##")' obbligatorio="true" messaggio='Per valorizzare la "Data verbale" valorizzare prima "Ora fine seduta"' onsubmit="true"/>
				</gene:campoScheda>
				<gene:campoScheda campo="NUMVERB" >
					<gene:checkCampoScheda funzione='controlloValorizzazioneCampo("#GARSED_DATVERB#","##")' obbligatorio="true" messaggio='Per valorizzare il "Numero verbale" valorizzare prima "Data verbale"' onsubmit="true"/>
				</gene:campoScheda>
				<gene:campoScheda campo="NPRVERB" />
				<gene:campoScheda campo="NOTVERB" />
				<gene:campoScheda campo="DATINI" />
				<gene:campoScheda campo="ORAINI" />
				<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
					<jsp:param name="entita" value='GARSEDSOSP'/>
					<jsp:param name="chiave" value='${numeroGara};${numeroSeduta}'/>
					<jsp:param name="nomeAttributoLista" value='sospensioniSeduta' />
					<jsp:param name="idProtezioni" value="GARSEDSOSP" />
					<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/garsedsosp/sospensione-seduta.jsp" />
					<jsp:param name="arrayCampi" value="'GARSEDSOSP_ORAINI_', 'GARSEDSOSP_MOTSON_', 'GARSEDSOSP_ORAFIN_'" />
					<jsp:param name="titoloSezione" value="Sospensione" />
					<jsp:param name="usaContatoreLista" value="true" />
					<jsp:param name="titoloNuovaSezione" value="Nuova sospensione" />
					<jsp:param name="descEntitaVociLink" value="sospensione della seduta" />
					<jsp:param name="msgRaggiuntoMax" value="e sospensioni della seduta" />
					<jsp:param name="sezioneListaVuota" value="false" />
				</jsp:include>				
				<gene:campoScheda campo="ORAFIN" />
			</gene:gruppoCampi>
			<gene:gruppoCampi idProtezioni="RICONV">
				<gene:campoScheda>
					<td colspan="2"><b>Eventuale riconvocazione</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="DATPRE" />
				<gene:campoScheda campo="ORAPRE" />
				<gene:campoScheda campo="MOTSOS" />
				<gene:campoScheda campo="NPROTRICONV" />
			</gene:gruppoCampi>
			<gene:gruppoCampi idProtezioni="RIEPI">
				<gene:campoScheda>
					<td colspan="2"><b>Riepilogo seduta</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="NPROG1" />
				<gene:campoScheda campo="NPROG2" />
				<gene:campoScheda campo="DATCONVDIT" />
				<gene:campoScheda campo="NOTSED" />
			</gene:gruppoCampi>


			<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
				<jsp:param name="entita" value='PERP'/>
				<jsp:param name="chiave" value='${numeroGara};${numeroSeduta}'/>
				<jsp:param name="nomeAttributoLista" value='personePresenti' />
				<jsp:param name="idProtezioni" value="PERP" />
				<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/perp/persona-presente.jsp" />
				<jsp:param name="arrayCampi" value="'PERP_NOMPER_', 'PERP_CODIMP_', 'PERP_NOMIMP_', 'PERP_DESPER_'" />
				<jsp:param name="titoloSezione" value="Persona presente" />
				<jsp:param name="titoloNuovaSezione" value="Nuova persona presente" />
				<jsp:param name="descEntitaVociLink" value="persona presente alla seduta" />
				<jsp:param name="msgRaggiuntoMax" value="e persone presenti alla seduta" />
			</jsp:include>

			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
		</gene:formScheda>

	</gene:redefineInsert>
	<gene:javaScript>
		function controlloValorizzazioneCampo(valoreDaControllare,valore){
			if((valoreDaControllare==null || valoreDaControllare=="") && valore!=null && valore!="")
				return false;
			else
				return true;
		}
		
		function apriComponentiCommissione(){
			var numeroGara = "${numeroGara }";
		    var numeroSeduta = "${numeroSeduta }";
		    var dataSeduta= getValue("GARSED_DATINI");
		    var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/garsedpres/garsedpres-lista.jsp";
			var key="${key }";
			href += "&key=" + key + "&dataSeduta=" + dataSeduta;
			document.location.href = href;
		}
		
			<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie")}'>
				redefineLabels();
				redefineTooltips();
				redefineTitles();
			</c:if>
			<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AutovieArchiflow")}'>
				addHrefs();
			</c:if>
		
		
	</gene:javaScript>
</gene:template>