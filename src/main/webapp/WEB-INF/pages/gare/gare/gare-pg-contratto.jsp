<%
/*
 * Created on: 09/01/2013
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
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.titoli.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.pg.hrefProtocollo.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>
	
<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:if test='${isGaraLottiConOffertaUnica eq "true" and not empty param.codiceGara}' >
	<c:set var="codiceGara" value='${param.codiceGara}' />
</c:if>

<c:set var="integrazioneDec" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.sil.pg.integrazione.dec")}'/>
<c:set var="esisteIntegrazioneLavori" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneLavoriFunction", pageContext)}' />

<c:set var="ngara" value='${gene:getValCampo(key, "GARE.NGARA")}' />
<c:set var="isLavoroAssociato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.ControlloAssociazioneGaraLavoroFunction", pageContext, ngara)}'/>
<c:set var="isAccordoQuadro" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsAccordoQuadroFunction", pageContext, codiceGara)}'/>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetTIPGENFunction" parametro='${codiceGara}' />

<c:set var="modelliPredispostiAttivoIncondizionato" value="1" scope="request" />

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<c:set var="valoreInizializzazioneContspe" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1115","7","true")}'/>

<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' />
<c:if test='${integrazioneWSERP eq "1"}'>
	<c:set var="presenzaRda" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetWSERPPresenzaRdaFunction", pageContext, codiceGara, ngara, requestScope.tipoWSERP)}' />
	<c:set var="tipoWSERP" value='${requestScope.tipoWSERP}' />
</c:if>

<gene:formScheda entita="GARE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreContratto">
	
	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>
	
	<c:if test='${isGaraLottiConOffertaUnica eq "true"}'>
		<gene:redefineInsert name="documentiAssociati" />
		<gene:redefineInsert name="noteAvvisi" />
		<gene:redefineInsert name="helpPagina" />
	</c:if>

	<gene:redefineInsert name="modelliPredisposti" >
		<tr>
			<c:choose>
		        <c:when test='${isNavigazioneDisabilitata ne "1"}'>
		        	<td class="vocemenulaterale">
						<a href="javascript:modelliPredispostiLocale();" title="Modelli predisposti" tabindex="1510">
							${gene:resource("label.tags.template.documenti.modelliPredisposti")}
					  	</a>
		   			</td>
		        </c:when>
		        <c:otherwise>
		          	<td>
						${gene:resource("label.tags.template.documenti.modelliPredisposti")}
					</td>
		        </c:otherwise>
			</c:choose>
		</tr>
	</gene:redefineInsert>
	<gene:redefineInsert name="documentiAssociati" >
	<c:choose>
		  <c:when test='${isNavigazioneDisabilitata ne "1"}'>
		  <c:set var="addWhere" value="COAKEY1=${datiRiga.GARECONT_NGARA};COAKEY2=${ncont}"/>
		  <c:set var="fictitiousVar" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetNumDocAssociatiCustomFunction", pageContext, "GARECONT", addWhere)}' />
			<tr>
				<td class="vocemenulaterale">
					<a href='javascript:documentiAssociatiGarecont();' title="Documenti associati contratto" tabindex="1522">
						Documenti associati contratto <c:if test="${not empty requestScope.numRecordDocAssociatiCustom}">(${requestScope.numRecordDocAssociatiCustom})</c:if>
					</a>
				</td>
			</tr>
		</c:when>
		        <c:otherwise>
		          	<td>
						Documenti associati contratto
					</td>
		        </c:otherwise>
			</c:choose>
	</gene:redefineInsert>
	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${modo eq "VISUALIZZA" && gene:checkProtFunz(pageContext, "MOD","SCHEDAMOD") && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GeneraNumeroOrdine")}'>
			<tr>
		        <td class="vocemenulaterale">
					<a href="javascript:setNOrdine();" title="Genera numero ordine" tabindex="1503">
						Genera numero ordine
					</a>
				</td>					
			</tr>
		</c:if>
		<c:if test='${esisteIntegrazioneLavori eq "TRUE" && gene:checkProtFunz(pageContext, "MOD","SCHEDAMOD") && modo eq "VISUALIZZA" && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.EsecuzioneContratto") && (datiRiga.TORN_ACCQUA ne 1 || (datiRiga.TORN_ACCQUA eq 1 && datiRiga.GARE1_AQOPER eq 1))}'>
			<tr>
		        <td class="vocemenulaterale">
					<a href="javascript:gestioneContrattoDEC('${ngara}');" title="Esecuzione contratto" tabindex="1504">
						Esecuzione contratto
					</a>
				</td>					
			</tr>
		</c:if>
		<c:if test='${autorizzatoModifiche ne "2" and isLavoroAssociato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.DelegaLavoroRup") and modo ne "MODIFICA"}' >
			<tr>
				<td class="vocemenulaterale">
					<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:delegaLavoroRup();" title='Delega commessa al RUP' tabindex="1505"></c:if>
						Delega commessa al RUP
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
				</td>
			</tr>
		</c:if>
		<c:if test='${autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.EvadiIdsAssociati") and modo ne "MODIFICA"}' >
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:EvadiIdsAssociati();" title='Evadi ids associati' tabindex="1511">
						Evadi ids associati
					</a>
				</td>
			</tr>
		</c:if>
		<c:if test='${modo eq "VISUALIZZA" and autorizzatoModifiche ne "2" and (((tipoWSERP eq "CAV" || tipoWSERP eq "AMIU") && empty datiRiga.GARE1_NUMRDO && datiRiga.TORN_ACCQUA ne 1) and (presenzaRda eq "1" || presenzaRda eq "2")) }'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:comunicaEsitoRdaInGara('${key}','${requestScope.tipoWSERP}');" title='Invia dati contratto ad ERP' tabindex="1512">
						Invia dati contratto ad ERP
					</a>
				</td>
			</tr>
		</c:if>
		
	</gene:redefineInsert>
		
	<jsp:include page="gare-interno-contratto.jsp">
		<jsp:param name="chiamante" value="contratto"/>
		<jsp:param name="ngara" value="${ngara }"/> 
		<jsp:param name="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica }"/> 
		<jsp:param name="tipoAppalto" value="${tipoAppalto }"/>
		<jsp:param name="codice" value="${ngara }"/> 
		<jsp:param name="ncont" value="1"/> 
	</jsp:include>
	
	<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARECONT.DCONSD")}'>
		<gene:fnJavaScriptScheda funzione='gestioneDCONSD("#GARE_DAATTO#","#GARECONT_DCONSD#")' elencocampi='GARE_DAATTO' esegui="false" />
	</c:if>
	
	<c:set var="ncont" value="1"/>
	<c:if test='${modoAperturaScheda ne "NUOVO"}' >
		<c:set var="tmp" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestioneAttiAggiuntiviContrattoFunction", pageContext, ngara, ncont)}'/>
	</c:if>

	<c:if test='${isLavoroAssociato ne "1" && isAccordoQuadro ne "1"}'>
		<jsp:include page="/WEB-INF/pages/gare/garattiagg/contratto-sez-atti-aggiuntivi.jsp">	
			<jsp:param name="ngara" value="${ngara}"/>
			<jsp:param name="ncont" value="${ncont}"/>
		</jsp:include>
	</c:if>

	<gene:gruppoCampi visibile='${esisteIntegrazioneLavori eq "TRUE"}' idProtezioni="RIFCONTRATTO">
		<gene:campoScheda>
			<td colspan="2"><b>Riferimento al contratto di ${gene:if(integrazioneDec ne '1','Monitoraggio OO.PP.','Direzione Esecuzione Contratti')}</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="CLAVOR" modificabile="false" visibile="${integrazioneDec ne '1'}"/>
		<gene:campoScheda campo="NUMERA" modificabile="false" visibile="${integrazioneDec ne '1'}"/>
		<gene:campoScheda visibile="${integrazioneDec eq '1'}">
			<td class="etichetta-dato">
				Contratto
			</td>
			<td class="valore-dato">
				<c:if test="${(!empty datiRiga.GARE_CLAVOR) && (!empty datiRiga.GARE_NUMERA)}">
					${datiRiga.GARE_CLAVOR}/${datiRiga.GARE_NUMERA}
				</c:if>
			</td>
		</gene:campoScheda>
	</gene:gruppoCampi>

	<gene:gruppoCampi idProtezioni="GARECONT" visibile="true">
		<gene:campoScheda addTr="false">
			<tr id="titSezEsCont">
				<td colspan="2"><b>Esecuzione contratto</b></td>
			</tr>
		</gene:campoScheda>
		<c:choose>
			<c:when test='${!empty datiRiga.GARE_IAGGIU}'>
				<c:set var="impNettoCont" value="${totNettoAtti+datiRiga.GARE_IAGGIU}"/>
		    </c:when>
		    <c:otherwise>
				<c:set var="impNettoCont" value="${totNettoAtti}"/>
	        </c:otherwise>
		</c:choose>
		<gene:campoScheda campo="IMPNETTOCONT" title="Importo netto contrattuale" campoFittizio="true" definizione="F15;0;;MONEY;G1NIMPCOAA" modificabile="false" visibile='true' value="${impNettoCont}" />
		<gene:campoScheda campo="INCORSO" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="CONSANT" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="MOTANT" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="DAVVES" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="NGIORIT" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="MODPAG" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="LAVSUB" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="PERCAV" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="DTERES" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="ISNCONF" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="DESNCONF" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="DSVIPO" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="NSVIPO" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="NOTE" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="NMESPRO" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
	</gene:gruppoCampi>
		
	<gene:gruppoCampi idProtezioni="GARELIQ" visibile="true">
		<gene:campoScheda addTr="false">
			<tr id="titSezL190">
				<td  colspan="2"><b>Dati di esecuzione contratto rif. L. 190/2012</b></td>
			</tr>
		</gene:campoScheda>
		<gene:campoScheda campo="DVERBC" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="DCERTU" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
		<gene:campoScheda campo="IMPLIQ" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="PUBBLICAZIONE" >
		<gene:campoScheda>
			<td colspan="2"><b>Pubblicazione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="PUBTRASP" obbligatorio="true" entita="GARECONT" where="GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1"/>
	</gene:gruppoCampi>
	<gene:fnJavaScriptScheda funzione='gestionePUBTRASP("#GARECONT_PUBTRASP#")' elencocampi='GARECONT_PUBTRASP' esegui="false" />
	
	<gene:gruppoCampi idProtezioni="CONSULENTI" visibile='${fn:contains(listaOpzioniDisponibili, "OP129#")}'>
		<gene:campoScheda addTr="false">
			<tr id="titSezConsulenti">
				<td colspan="2"><b>Consulenti e collaboratori</b></td>
			</tr>
		</gene:campoScheda>
		<gene:campoScheda campo="RAGCONS" entita="GARECONT" where="GARECONT.NGARA=GARE.CODGAR1 AND GARECONT.NCONT=1" visibile='${fn:contains(listaOpzioniDisponibili, "OP129#")}' />
		<gene:campoScheda campo="VARCONS" entita="GARECONT" where="GARECONT.NGARA=GARE.CODGAR1 AND GARECONT.NCONT1" visibile='${fn:contains(listaOpzioniDisponibili, "OP129#")}' />
	</gene:gruppoCampi>
	
	
	<gene:fnJavaScriptScheda funzione='gestioneVisualizzazione("#GARECONT_CONSANT#","GARECONT_MOTANT")' elencocampi='GARECONT_CONSANT' esegui="true" />
	<gene:fnJavaScriptScheda funzione='gestioneVisualizzazione("#GARECONT_ISNCONF#","GARECONT_DESNCONF")' elencocampi='GARECONT_ISNCONF' esegui="true" />
	<gene:fnJavaScriptScheda funzione='gestioneESECSCIG("#GARECONT_ESECSCIG#")' elencocampi='GARECONT_ESECSCIG' esegui="true" />
	
	<jsp:include page="/WEB-INF/pages/gene/attributi/sezione-attributi-generici.jsp">
		<jsp:param name="joinFrom" value="GARECONT"/>
		<jsp:param name="joinWhere" value="GARE.NGARA=GARECONT.NGARA"/>
        <jsp:param name="entitaParent" value="GARECONT"/>
	</jsp:include>
	

	<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
	<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />
	<input type="hidden" name="DIREZIONE_WIZARD" id="DIREZIONE_WIZARD" value="" />
	<input type="hidden" name="entitaPrincipaleModificabile" id="entitaPrincipaleModificabile" value="${sessionScope.entitaPrincipaleModificabile}" />
	
	<gene:campoScheda>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
			<c:when test='${modo eq "MODIFICA"}'>
				<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
			</c:when>
			<c:otherwise>
				
				<c:if test='${(autorizzatoModifiche ne "2") and isLavoroAssociato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.DelegaLavoroRup")}' >
					<INPUT type="button" class="bottone-azione" value='Delega commessa al RUP' title='Delega commessa al RUP' onclick="javascript:delegaLavoroRup();" id="btnDelegaLavoroRup">
				</c:if>
								
				<c:choose>
					<c:when test='${(autorizzatoModifiche ne "2") and gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
					</c:when>
				</c:choose>
								
				
				
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</gene:campoScheda>

	<gene:javaScript>
	
	setIsGaraLottiConOffertaUnica("${isGaraLottiConOffertaUnica }");
	
	function gestioneContrattoDEC(ngara){
		var href = "href=gare/gare/gare-popup-integrazione-dec.jsp&ngara=" + ngara + "&ncont=1";
		openPopUpCustom(href, "integrazioneDEC", 780, 550, "no", "yes");
	}			
			
	
	var ridisoPrec = getValue("GARE_RIDISO");
			
	function modelliPredispostiLocale(){
	/***********************************************************
		Copia della funzione modelliPredisposti dal file jsAzioniTags.jsp
		e addattamento al caso specifico
	 ***********************************************************/
		var entita,valori;

		try {
			entita = "GARE";
			if(document.forms[0].key.value!=''){
				valori=document.forms[0].name+".key";
			} else if(document.forms[0].keyParent.value!=''){
				valori=document.forms[0].name+".keyParent";
			} else if(document.forms[0].keys.value!=''){
				valori=document.forms[0].name+".keys";
			}
			compositoreModelli('${pageContext.request.contextPath}',entita,'',valori);
		}catch(e){
		}
	}
	
	function delegaLavoroRup(){
		var ngara = getValue("GARE_NGARA");
		var ditta = getValue("GARE_DITTA");
		var codrup = getValue("TORN_CODRUP");
		var nmaximo = getValue("GARE_NMAXIMO");
		var clavor= getValue("GARE_CLAVOR");
		var numera= getValue("GARE_NUMERA");
		var href = "href=gare/gare/gare-popup-delegaLavoroRup.jsp&ngara="+ngara;
		href+="&ditta=" + ditta;
		href+="&codrup=" + codrup;
		href+="&nmaximo=" +encodeURIComponent(nmaximo);
		href+="&clavor=" + clavor;
		href+="&numera=" + numera;
		openPopUpCustom(href, "delegaLavoroRup", 600, 400, "yes","yes");
	}
	
	
	function gestioneVisualizzazione(valore, campo){
		if(valore=='1')
			showObj("row"+campo, true);
		else
			showObj("row"+campo, false);
	}
	
	function archivioImpresaAggDef(){
		var codiceImpresa = getValue("GARE_DITTA");
		var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href;
	}
	
	$('#rowLinkAddGARCOMPREQ td').parent().prepend($("<td>"));
	$('#rowLinkAddGARCOMPREQ td:eq(1)').attr("colspan","4");
	$('#rowMsgLastGARCOMPREQ td').parent().prepend($("<td>"));
	$('#rowMsgLastGARCOMPREQ td:eq(1)').attr("colspan","4");
		
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.PersonalizzazioneAutovie")}'>
		redefineLabels();
		redefineTooltips();
		redefineTitles();
	</c:if>
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.AutovieArchiflow")}'>
		addHrefs();
	</c:if>
	
	function gestionePUBTRASP(pubtrasp){
		var msg;
		if(pubtrasp=='1'){
			msg="Impostando il flag a 'Si' l'affidamento viene pubblicato su portale Appalti.\nConfermi la modifica?";
		}else if(pubtrasp=='2'){
			msg="Impostando il flag a 'No' l'affidamento non viene pubblicato su portale Appalti.\nConfermi la modifica?";
		}
		
		if(pubtrasp!=null && pubtrasp!="" && !confirm(msg))
			setValue("GARECONT_PUBTRASP",getOriginalValue("GARECONT_PUBTRASP"))
	}
	
	function gestioneESECSCIG(esecscig){
		var clavor = getValue("GARE_CLAVOR");
		var numera = getValue("GARE_NUMERA");
		var accqua = getValue("TORN_ACCQUA");
		var aqoper = getValue("GARE1_AQOPER");
		if((clavor=='' && numera=='')||(accqua=='1' && aqoper=='1' && esecscig!= '1')){
			$("#titSezL190").show();
			$("#rowGARECONT_DVERBC").show();
			$("#rowGARECONT_DCERTU").show();
			$("#rowGARECONT_IMPLIQ").show();
		}else{
			$("#titSezL190").hide();
			$("#rowGARECONT_DVERBC").hide();
			$("#GARECONT_DVERBC").val('');
			$("#rowGARECONT_DCERTU").hide();
			$("#GARECONT_DCERTU").val('');
			$("#rowGARECONT_IMPLIQ").hide();
			$("#GARECONT_IMPLIQ").val('');
		}
		
		if((clavor=='' && numera=='' && !((accqua=='1' && aqoper == '2') || (accqua=='1' && aqoper=='1' && esecscig != '1')))){
			$("#titSezEsCont").show();
			$("#rowIMPNETTOCONT").show();
			$("#rowGARECONT_INCORSO").show();
			$("#rowGARECONT_CONSANT").show();
			$("#rowGARECONT_MOTANT").show();
			$("#rowGARECONT_DAVVES").show();
			$("#rowGARECONT_NGIORIT").show();
			$("#rowGARECONT_MODPAG").show();
			$("#rowGARECONT_LAVSUB").show();
			$("#rowGARECONT_PERCAV").show();
			$("#rowGARECONT_DTERES").show();
			$("#rowGARECONT_ISNCONF").show();
			$("#rowGARECONT_DESNCONF").show();
			$("#rowGARECONT_DSVIPO").show();
			$("#rowGARECONT_NSVIPO").show();
			$("#rowGARECONT_NOTE").show();
			$("#rowGARECONT_NMESPRO").show();
		}else{
			$("#titSezEsCont").hide();
			$("#rowIMPNETTOCONT").hide();
			$("#rowGARECONT_INCORSO").hide();
			$("#GARECONT_INCORSO").val('');
			$("#rowGARECONT_CONSANT").hide();
			$("#GARECONT_CONSANT").val('');
			$("#rowGARECONT_MOTANT").hide();
			$("#GARECONT_MOTANT").val('');
			$("#rowGARECONT_DAVVES").hide();
			$("#GARECONT_DAVVES").val('');
			$("#rowGARECONT_NGIORIT").hide();
			$("#GARECONT_NGIORIT").val('');
			$("#rowGARECONT_MODPAG").hide();
			$("#GARECONT_MODPAG").val('');
			$("#rowGARECONT_LAVSUB").hide();
			$("#GARECONT_LAVSUB").val('');
			$("#rowGARECONT_PERCAV").hide();
			$("#GARECONT_PERCAV").val('');
			$("#rowGARECONT_DTERES").hide();
			$("#GARECONT_DTERES").val('');
			$("#rowGARECONT_ISNCONF").hide();
			$("#GARECONT_ISNCONF").val('');
			$("#rowGARECONT_DESNCONF").hide();
			$("#GARECONT_DESNCONF").val('');
			$("#rowGARECONT_DSVIPO").hide();
			$("#GARECONT_DSVIPO").val('');
			$("#rowGARECONT_NSVIPO").hide();
			$("#GARECONT_NSVIPO").val('');
			$("#rowGARECONT_NOTE").hide();
			$("#GARECONT_NOTE").val('');
			$("#rowGARECONT_NMESPRO").hide();
			$("#GARECONT_NMESPRO").val('');
		}
		if(esecscig == 1){
			$("#rowGARECONT_CONTSPE").hide();
			$("#GARECONT_CONTSPE").val('');
		}else{
			if(($("#GARECONT_CONTSPE").val()==null || $("#GARECONT_CONTSPE").val()=="") && esecscig == 2){
				var valoreInizializzazioneContspe="${valoreInizializzazioneContspe }";
				$("#GARECONT_CONTSPE").val(valoreInizializzazioneContspe);
			}
			$("#rowGARECONT_CONTSPE").show();
		}
	}
	
	function gestioneDCONSD(datto, dconsd){
		if(dconsd==null || dconsd == "")
			setValue("GARECONT_DCONSD",datto);
	}
	
	function comunicaEsitoRdaInGara(ngara,tipoWSERP){
		var href="href=gare/gare/gare-popup-comunicaEsitoRda.jsp&ngara=" + ngara + "&isGaraLottiConOffertaUnica=false"+ "&tipoWSERP=" + tipoWSERP;
		openPopUpCustom(href, "comunicaEsitoRda", 700, 600, "yes","yes");
	}
	
	function documentiAssociatiGarecont(){
		var keys = "GARECONT.NGARA=T:"+getValue("GARECONT_NGARA")+";GARECONT.NCONT=N:"+${ncont};
		var href = contextPath+'/ListaDocumentiAssociati.do?'+csrfToken+'&metodo=visualizza&entita=GARECONT&valori='+keys;
		document.location.href = href;
	}
	
	
	</gene:javaScript>
</gene:formScheda>