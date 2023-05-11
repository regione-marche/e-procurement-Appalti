<%
/*
 * Created on: 19-ott-2007
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

<c:choose>
	<c:when test="${!empty param.busta}">
		<c:set var="busta" value='${param.busta}' />
	</c:when>
	<c:otherwise>
		<c:set var="busta" value='${busta}' />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.titoloBusta}">
		<c:set var="titoloBusta" value='${param.titoloBusta}' />
	</c:when>
	<c:otherwise>
		<c:set var="titoloBusta" value='${titoloBusta}' />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test="${!empty param.autorizzatoModifiche}">
		<c:set var="autorizzatoModifiche" value='${param.autorizzatoModifiche}' />
	</c:when>
	<c:otherwise>
		<c:set var="autorizzatoModifiche" value='${autorizzatoModifiche}' />
	</c:otherwise>
</c:choose>

<c:set var="iterga" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetITERGAMacroFunction",  pageContext, key)}'/>

<c:set var="codgar" value='${gene:getValCampo(key, "GARE.NGARA")}' />

${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, codgar, "SC", "20")}
${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, busta, "N","")}


<gene:template file="lista-template.jsp" gestisciProtezioni="false" >
	<gene:setString name="titoloMaschera" value="Associazione per lotto Q-form della ${ titoloBusta}"/>
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaInserisci">	</gene:redefineInsert>
	<gene:redefineInsert name="corpo">
	
	
	<c:choose>
		<c:when test="${busta eq '2' }">
			<c:set var="where" value="(modlicg=6 or not exists(select gare1.valtec from gare1 where  gare1.ngara=gare.ngara))"/>
		</c:when>
		<c:otherwise>
			<c:set var="where" value="not exists(select gare1.costofisso from gare1 where gare1.ngara=gare.ngara and gare1.costofisso='1')"/>
		</c:otherwise>
	</c:choose>
	
	<c:set var="where" value="GARE.CODGAR1 = '${codgar}' AND GARE.CODGAR1!=GARE.NGARA and ${where}"/>
	
	
	<table class="dettaglio-notab">
		<tr>
			<td>
				<gene:formLista entita="GARE" where="${where}" sortColumn="2" tableclass="datilista" pagesize="25" gestisciProtezioni="false" >
					<c:set var="statoQestionario" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GetStatoQuestionarioLottoFunction",  pageContext, datiRiga.GARE_NGARA, datiRiga.GARE_CODGAR1,iterga,busta )}'/>
					<gene:campoLista title="Opzioni" width="50">
						
					<c:if test='${currentRow >= 0 && !(statoQestionario eq "INSQFORM" && autorizzatoModifiche eq "2")}'>
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="${ titoloBusta}" href="javascript:questionario('${datiRiga.GARE_NGARA }','${datiRiga.GARE_CODGAR1}','${statoQestionario}');" />
						</gene:PopUp>
						<c:set var="hrefDettaglio" value="javascript:questionario('${datiRiga.GARE_NGARA }','${datiRiga.GARE_CODGAR1}','${statoQestionario}');"/> 
					</c:if>
					</gene:campoLista>
					<gene:campoLista campo="NGARA" title="Codice lotto" width="100" headerClass="sortable" href="${hrefDettaglio }"/>
					<gene:campoLista campo="CODIGA" title="Lotto" headerClass="sortable" width="50"/>
					<gene:campoLista campo="CODCIG" headerClass="sortable" />
					<gene:campoLista campo="STATO_BUSTA" title="Stato" campoFittizio="true" definizione="T50" value="${labelStato}" />
					<gene:campoLista campo="CODGAR1" visibile="false" />
					
					
					<input type="hidden" name="titoloBusta" value="${titoloBusta }" />	
					<input type="hidden" name="busta" value="${busta }" />
					<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche }" />
					
				</gene:formLista>
			</td>
		</tr>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<gene:insert name="pulsanteIndietro">
				<INPUT type="button"  class="bottone-azione" value='Torna a elenco buste' title='Torna a elenco buste' onclick="javascript:historyVaiIndietroDi(1);">
			</gene:insert>
			
		
			&nbsp;
		</td>
	</table>
	</gene:redefineInsert>
<gene:javaScript>
	function questionario(ngara, codiceGara,statoQuestionario){
		var busta = "${busta}";
		var titoloBusta = "${titoloBusta}";
		
		if(statoQuestionario=="INSQFORM")
			statoQuestionario = "MODALE-INSQFORM";
				
		var autorizzatoModifiche = "${autorizzatoModifiche}";
		if(statoQuestionario=="MODALE-INSQFORM" && autorizzatoModifiche != "2" ){
			var href = contextPath + "/ApriPagina.do?href=gare/documgara/qform.jsp";
			formVisualizzaDocumenti.href.value="gare/documgara/qform.jsp";
			formVisualizzaDocumenti.key.value = "GARE.NGARA=T:" + ngara;
			formVisualizzaDocumenti.busta.value = busta;
			formVisualizzaDocumenti.titoloBusta.value = titoloBusta;
			formVisualizzaDocumenti.gruppo.value = 3;
			formVisualizzaDocumenti.modoQform.value="INSQFORM";
			formVisualizzaDocumenti.codiceGara.value=codiceGara;
			formVisualizzaDocumenti.submit();
		}else if(statoQuestionario=="VISQFORM" ){
			var href = contextPath + "/ApriPagina.do?href=gare/documgara/qform.jsp";
			formVisualizzaDocumenti.href.value="gare/documgara/qform.jsp";
			formVisualizzaDocumenti.key.value = "GARE.NGARA=T:" + ngara;
			formVisualizzaDocumenti.busta.value = busta;
			formVisualizzaDocumenti.titoloBusta.value = titoloBusta;
			formVisualizzaDocumenti.gruppo.value = 3;
			formVisualizzaDocumenti.modoQform.value="VISQFORM";
			formVisualizzaDocumenti.codiceGara.value=codiceGara;
			formVisualizzaDocumenti.submit();
		}
	}
	
</gene:javaScript>

</gene:template>

<form name="formVisualizzaDocumenti" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/documgara/documenti-tipologia.jsp" /> 
	<input type="hidden" name="key" value="" />
	<input type="hidden" name="codiceGara" value="" />
	<input type="hidden" name="tipologiaDoc" value="" />
	<input type="hidden" name="gruppo" value="" />
	<input type="hidden" name="busta" value="" />
	<input type="hidden" name="titoloBusta" value="" />
	<input type="hidden" name="firstTimer" value="true" />
	<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche}" />	
	<input type="hidden" name="idconfi" value="${idconfi}" />	
	<input type="hidden" name="modoQform" id="modoQform" value="" />
</form> 	 	
