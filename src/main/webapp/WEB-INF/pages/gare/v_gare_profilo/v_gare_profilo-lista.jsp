<%/*
   * Created on 26-11-2013
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



<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GARE_PROFILO-lista" >
	<gene:setString name="titoloMaschera" value="Lista gare e avvisi"/>
	<c:set var="uffintAbilitati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata")}'/>
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="listaNuovo">
	</gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaInserisci">
	</gene:redefineInsert>
	<c:if test = '${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALotti")}'>
		<c:set var="GestioneGareALotti" value='true'/>
	</c:if>
	<c:if test = '${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareLottiOffUnica")}'>
		<c:set var="GestioneGareLottiOffUnica" value='true'/>
	</c:if>
	<c:if test = '${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALottoUnico")}'>
		<c:set var="GestioneGareALottoUnico" value='true'/>
	</c:if>

	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
		
  	<%// Creo la lista per gare e torn mediante la vista %>
		<table class="lista">
			<tr>
				<td>
  	<gene:formLista entita="V_GARE_PROFILO" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="-2" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreTORN"
  		where="${filtroUffint }">
  	
  	<c:set var='visualizzaPopUp' value='${gene:checkProt(pageContext, "MASC.VIS.GARE.GARE-scheda") || gene:checkProtFunz(pageContext, "MOD","MOD") || gene:checkProtFunz(pageContext, "DEL","DEL") }'/>
	

			
			<% // Campi veri e propri %>
			
			<c:set var="codice" value='${datiRiga.V_GARE_PROFILO_CODICE}'/>
			<c:set var="cenint" value='${datiRiga.V_GARE_PROFILO_CENINT}'/>
			<c:set var="codprofilo" value='${datiRiga.V_GARE_PROFILO_CODPROFILO}'/>
			<c:set var="genere" value='${datiRiga.V_GARE_PROFILO_GENERE}'/>
			<c:set var="link" value="javascript:visualizzaGara('${chiaveRigaJava}','${codice}','${cenint}','${codprofilo}','${genere}')" />
			
			<gene:campoLista title="TEST" visibile="false">
				<c:choose>
					<c:when test="${genere eq '11' }">
						<c:set var="genereGaraAbilitato" value='true' />
					</c:when>
					<c:otherwise>
						<c:set var="genereGaraAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.CheckProtProfiliFunction", pageContext, codprofilo,genere)}' />
					</c:otherwise>
				</c:choose>
				
				
				<c:set var="CODICETEST" value='${gene:getValCampo(chiaveRigaJava, "CODPROFILO")}' />	
			</gene:campoLista>
			<gene:campoLista campo="CODICE"  headerClass="sortable" />
			<gene:campoLista campo="CODGAR"  headerClass="sortable" visibile="false"/>
			<gene:campoLista campo="CODCIG"  headerClass="sortable" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoCigListaGare" />
			<gene:campoLista campo="OGGETTO" headerClass="sortable" />
			<gene:campoLista campo="CODPROFILO" headerClass="sortable" />
			<gene:campoLista campo="CENINT" visibile="false" />
			<gene:campoLista campo="GENERE" visibile="false" />
			<gene:campoLista title="" width="30">
				<c:if test = '${genereGaraAbilitato eq "true"}'>
					<a href="${link}">
					<img width="16" height="16" title="Vai al profilo" alt="Vai al profilo" src="${pageContext.request.contextPath}/img/accediGaraProfilo.png"/>
					</a>
				</c:if>
			</gene:campoLista>
			
			<input type="hidden" name="tipoRicerca" value="${tipoRicerca }" />
			<input type="hidden" name="findstr" value="${findstr}" />
			<input type="hidden" name="valoreCodimp" value="${valoreCodimp }" />
			<input type="hidden" name="valoreNomimo" value="${valoreNomimo }" />
			<input type="hidden" name="valoreCf" value="${valoreCf }" />
			<input type="hidden" name="valorePiva" value="${valorePiva }" />
			<input type="hidden" name="valoreTipimp" value="${valoreTipimp }" />
			<input type="hidden" name="valoreIsmpmi" value="${valoreIsmpmi }" />
			<input type="hidden" name="valoreEmail" value="${valoreEmail }" />
			<input type="hidden" name="valorePec" value="${valorePec }" />
			<input type="hidden" name="valoreCodCat" value="${valoreCodCat }" />
			<input type="hidden" name="valoreDescCat" value="${valoreDescCat }" />
			<input type="hidden" name="valoreTipCat" value="${valoreTipCat }" />
			<input type="hidden" name="valoreNumclass" value="${valoreNumclass }" />
			<input type="hidden" name="valoreAbilitaz" value="${valoreAbilitaz }" />
			<input type="hidden" name="valoreDricind" value="${valoreDricind }" />
			<input type="hidden" name="valoreDscad" value="${valoreDscad }" />
			<input type="hidden" name="valoreStrin" value="${valoreStrin }" />
			<input type="hidden" name="ignoraCaseSensitive" value="${ignoraCaseSensitive }" />
			<input type="hidden" name="genere" value="20" />
		</gene:formLista>
				</td>
			</tr>
			<tr>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
			</tr>
		</table>

		<form name="formVisualizzaPermessiUtenti" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtenti.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="genereGara" id="genereGara" value="20" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
			<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		</form>

		<form name="formVisualizzaPermessiUtentiStandard" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiStandard.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="genereGara" id="genereGara" value="20" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="" />
			<input type="hidden" name="codein" id="codein" value="${sessionScope.uffint}" />
		</form>

  </gene:redefineInsert>
  <gene:javaScript>
  	//Abilitazione alla gestione del codice html nei tooltip solo per il campo CIG e solo quando vi è
	//un elenco di codici CIG
	$(function() {
	  $('.tooltipCig').tooltip({
	    content: function(){
	      var element = $( this );
	      return element.attr('title')
	    }
	  });
	});
	
	<c:set var="GestioneGareALottoUnico" value='true'/>
	
	// Visualizzazione del dettaglio
	function visualizzaGara(chiaveRiga,codice,cenint,codprofilo,genere){
		var link =  '${pageContext.request.contextPath}/SetProfilo.do?'+csrfToken+'&profilo='+ codprofilo;
		if("${uffintAbilitati}" == 1){link =  link + '&uffint=' + cenint;}
		var trovaParameter = "T:" + codice;
		var trovaAddWhere = '';
		if(genere=='11'){
			trovaAddWhere = "GAREAVVISI.NGARA = ?";
			link =  link + '&href=gare/gareavvisi/gareavvisi-lista.jsp';
		}else{
			trovaAddWhere = "V_GARE_TORN.CODICE = ?";
			link =  link + '&href=gare/v_gare_torn/v_gare_torn-lista.jsp';
		}
		link =  link + '&trovaParameter=' + trovaParameter + '&trovaAddWhere=' + trovaAddWhere;
		document.location.href = link;
	}
  </gene:javaScript>


</gene:template>