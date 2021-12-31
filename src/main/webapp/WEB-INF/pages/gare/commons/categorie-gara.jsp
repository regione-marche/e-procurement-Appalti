<%
/*
 * Created on 28-ott-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione: Categorie associate una gara
	Creato da:   Luca Giacomazzo
 */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>

<fmt:setBundle basename="AliceResources" />

<c:choose>
	<c:when test='${modo eq "NUOVO"}'>
		<c:set var="nGara" value='' />
	</c:when>
	<c:otherwise>
		<c:set var="nGara" value='${gene:getValCampo(key, "NGARA")}' />
	</c:otherwise>
</c:choose>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneCategorieGaraFunction" parametro="${nGara}" />

<c:choose>
	<c:when test='${param.tipgen eq "1"}'>
		<c:set var="titoloCategoriaPrevalente" value='Categoria prevalente' />
		<c:set var="titoloCategoriaUlteriore" value='Ulteriore categoria' />
	</c:when>
	<c:otherwise>
		<c:set var="titoloCategoriaPrevalente" value='Prestazione principale' />
		<c:set var="titoloCategoriaUlteriore" value='Prestazione secondaria' />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.campiModificabili}'>
		<c:set var="campiModificabili" value='${param.campiModificabili}' />
	</c:when>
	<c:otherwise>
		<c:set var="campiModificabili" value='true' />
	</c:otherwise>
</c:choose>

<c:set var="esisteClassificaForniture" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB2","G_z07")}'/>
<c:set var="esisteClassificaServizi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB2","G_z08")}'/>
<c:set var="esisteClassificaLavori150" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB2","G_z11")}'/>
<c:set var="esisteClassificaServiziProfessionali" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB2","G_z12")}'/>

<c:if test="${param.lottoDiGara eq 'true'}">
	<c:set var="codiceGara" value='${param.codiceGara}'/>
	<c:set var="whereCategoriaParent" value=" AND (CAISIM IN (SELECT CATIGA FROM CATG WHERE NGARA = '${codiceGara}') OR CAISIM IN (SELECT CATOFF FROM OPES WHERE NGARA3 = '${codiceGara}'))"/>
</c:if>

<gene:campoScheda entita="CATG" campo="NGARA" visibile="false" where="CATG.NGARA=GARE.NGARA" defaultValue='${nGara}' />
	<gene:gruppoCampi idProtezioni="CATG">
		<c:choose>
			<c:when test="${param.lottoDiGara eq 'true' and modo ne 'VISUALIZZA'}">
				<gene:campoScheda>
					<td id="titoloCategoriaPrevalente" colspan="1">
						<b>${titoloCategoriaPrevalente}</b>
					</td>
					<td style="text-align: right" id="tastoEliminazioneCategoria">
						<a href="javascript:nascondiCategoria(true)" title="Nascondi categoria prevalente" class="link-generico">Elimina</a>&nbsp;
						<a href="javascript:nascondiCategoria(true)" title="Nascondi categoria prevalente" class="link-generico"><img src="${pageContext.request.contextPath}/img/opzioni_del.gif" height="16" width="16" alt="Nascondi categoria prevalente"></a>&nbsp;
					</td>
					<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.INS.GARE.GARE-scheda.DATIGENOFFUNICA.Agg-Categ-Prevalente')}">
					<tr id="rowLinkVisualizzaCat">
						<td style="width:200px;">&nbsp;</td>
						<td class="valore-dato"><a href="javascript:visualizzaCategoria();" class="link-generico"><img src="${pageContext.request.contextPath}/img/opzioni_add.gif" title="" alt="Aggiungi categoria prevalente" height="16" width="16">&nbsp;Aggiungi ${titoloCategoriaPrevalente}</a>
						</td>
					</tr>
					</c:if>
				</gene:campoScheda>
				
			</c:when>
			<c:otherwise>
				<gene:campoScheda>
					<td id="titoloCategoriaPrevalente" colspan="2">
						<b>${titoloCategoriaPrevalente}</b>
					</td>
				</gene:campoScheda>
			</c:otherwise>
		</c:choose>
		<gene:archivio titolo="Categorie d'iscrizione"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.CATG.CATIGA"), "gene/cais/lista-categorie-iscrizione-popup.jsp", "")}'
			scheda=""
			schedaPopUp=""
			campi="V_CAIS_TIT.CAISIM;V_CAIS_TIT.DESCAT;V_CAIS_TIT.ACONTEC;V_CAIS_TIT.QUAOBB;V_CAIS_TIT.TIPLAVG;V_CAIS_TIT.ISFOGLIA"
			chiave=""
			where="${whereCategoriaParent}"
			formName="formCategoriaPrevalenteGare"
			inseribile="false">
			<gene:campoScheda campo="CATIGA" title='${gene:if(param.tipgen eq "1", "Codice categoria", "Codice prestazione")}' entita="CATG" where="CATG.CATIGA=CAIS.CAISIM " obbligatorio="${datiRiga.TORN_TIPGEN eq '1'}" defaultValue="${requestScope.initCATG[0]}"  modificabile="${campiModificabili}"/>
			<gene:campoScheda campo="DESCAT" entita="CAIS" from="CATG" where="CATG.NGARA='${nGara}' and CATG.CATIGA=CAIS.CAISIM" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.CATG.CATIGA") and campiModificabili}' visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.CATG.CATIGA")}' defaultValue="${requestScope.initCATG[1]}" />
			<gene:campoScheda campo="ACONTEC" entita="CAIS" visibile="false" defaultValue="${requestScope.initCATG[2]}" />
			<gene:campoScheda campo="QUAOBB" entita="CAIS" visibile="false" defaultValue="${requestScope.initCATG[3]}" />
			<gene:campoScheda campo="TIPLAVG" entita="CAIS" visibile="false" defaultValue="${requestScope.initCATG[4]}" />
			<gene:campoScheda campo="ISFOGLIA" entita="V_CAIS_TIT"  from="CATG" where="CATG.NGARA='${nGara}' and CATG.CATIGA=V_CAIS_TIT.CAISIM" visibile="false" defaultValue="${requestScope.initCATG[9]}" modificabile="${campiModificabili}"/>
		</gene:archivio>
		
		<gene:campoScheda campo="NCATG" entita="CATG" visibile="false" />
		<gene:campoScheda campo="IMPBASG" entita="CATG" defaultValue="${requestScope.initCATG[6]}" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="NUMCLA" entita="CATG" visibile="false" defaultValue="${requestScope.initCATG[7]}" />
		<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_LAVORI" campoFittizio="true" entita="CATG" definizione="N2;0;G_z09;;G1NUMCLA" value="${datiRiga.CATG_NUMCLA}" modificabile="${campiModificabili}"/>
		<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_FORNITURE" campoFittizio="true" entita="CATG" definizione="N2;0;G_z07;;G1NUMCLA" value="${datiRiga.CATG_NUMCLA}" modificabile="${campiModificabili}"/>
		<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_SERVIZI" campoFittizio="true" entita="CATG" definizione="N2;0;G_z08;;G1NUMCLA" value="${datiRiga.CATG_NUMCLA}" modificabile="${campiModificabili}"/>
		<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_LAVORI150" campoFittizio="true" entita="CATG" definizione="N2;0;G_z11;;G1NUMCLA" value="${datiRiga.CATG_NUMCLA}" modificabile="${campiModificabili}"/>
		<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_SERVIZIPROFESSIONALI" campoFittizio="true" entita="CATG" definizione="N2;0;G_z12;;G1NUMCLA" value="${datiRiga.CATG_NUMCLA}" modificabile="${campiModificabili}"/>
		<gene:campoScheda campo="IMPIGA" entita="CATG" visibile="false" defaultValue="${requestScope.initCATG[8]}" />
	</gene:gruppoCampi>
	<gene:fnJavaScriptScheda funzione='visualizzaNumeroClassifica("#CAIS_TIPLAVG#","#V_CAIS_TIT_ISFOGLIA#", "CATG", null, true)' elencocampi='CAIS_TIPLAVG;V_CAIS_TIT_ISFOGLIA' esegui="false" />
	<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#CATG_NUMCLA_CAT_PRE_LAVORI#", "CATG", null)' elencocampi='CATG_NUMCLA_CAT_PRE_LAVORI' esegui="false" />
	<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#CATG_NUMCLA_CAT_PRE_FORNITURE#", "CATG", null)' elencocampi='CATG_NUMCLA_CAT_PRE_FORNITURE' esegui="false" />
	<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#CATG_NUMCLA_CAT_PRE_SERVIZI#", "CATG", null)' elencocampi='CATG_NUMCLA_CAT_PRE_SERVIZI' esegui="false" />
	<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#CATG_NUMCLA_CAT_PRE_LAVORI150#", "CATG", null)' elencocampi='CATG_NUMCLA_CAT_PRE_LAVORI150' esegui="false" />
	<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#CATG_NUMCLA_CAT_PRE_SERVIZIPROFESSIONALI#", "CATG", null)' elencocampi='CATG_NUMCLA_CAT_PRE_SERVIZIPROFESSIONALI' esegui="false" />
	<gene:fnJavaScriptScheda funzione='aggiornaCategorieAppalto("CATG_IMPBASG")' elencocampi='CATG_IMPBASG${contatore}' esegui="false" />

<c:set var="params" value="NGARA=T:${nGara}" />
<c:set var="contatore" value="1" />
<c:choose>
	<c:when test='${modo ne "NUOVO"}'>
		<c:if test='${not empty ulterioriCategorie}'>
			<c:forEach items="${ulterioriCategorie}" var="item" varStatus="stato">
				<gene:gruppoCampi idProtezioni="OPES">
					<gene:campoScheda campo="DEL_ULTERIORE_CATEGORIA_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="T1" value="0" />
					<gene:campoScheda campo="MOD_ULTERIORE_CATEGORIA_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="T1" value="1" />
					<c:set var="nomeSezVis" value="sezioneVisibileOPES_${contatore}" />
					<c:choose>
						<c:when test='${empty param[nomeSezVis] or modo eq "VISUALIZZA"}'>
							<input type="hidden" name="sezioneVisibileOPES_${contatore}" id="sezioneVisibileOPES_${contatore}" value="1" />
						</c:when>
						<c:otherwise>
							<input type="hidden" name="sezioneVisibileOPES_${contatore}" id="sezioneVisibileOPES_${contatore}" value="${param[nomeSezVis]}" />
						</c:otherwise>
					</c:choose>
					<gene:campoScheda nome="titoloUlterioreCategoria_${contatore}">
						<td >
							<b>${titoloCategoriaUlteriore} ${item[6]}</b>
						</td>
																
						<td style="text-align: right">
							<c:if test='${modoAperturaScheda ne "VISUALIZZA" and gene:checkProtFunz(pageContext, "DEL","Elimina-Categ-Ulteriore")}'><a href="javascript:eliminaUlterioreCategoria(${contatore})" title="Elimina ${titoloCategoriaUlteriore}" class="link-generico">Elimina</a>&nbsp;
							<a href="javascript:eliminaUlterioreCategoria(${contatore})" title="Elimina ${titoloCategoriaUlteriore}" class="link-generico"><img src='${pageContext.request.contextPath}/img/opzioni_del.gif' height='16' width='16' alt='Elimina ${titoloCategoriaUlteriore}'></a>&nbsp;</c:if>
						</td>
					</gene:campoScheda>
					
					<gene:archivio titolo="Categorie d'iscrizione"
						lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.OPES.CATOFF"), "gene/cais/lista-categorie-iscrizione-popup.jsp", "")}'
						scheda=""
						schedaPopUp=""
						campi="V_CAIS_TIT.CAISIM;V_CAIS_TIT.DESCAT;V_CAIS_TIT.ACONTEC;V_CAIS_TIT.QUAOBB;V_CAIS_TIT.TIPLAVG;V_CAIS_TIT.ISFOGLIA"
						chiave=""
						where="${whereCategoriaParent}"
						formName="formUlterioreCategoriaGare${contatore}"
						inseribile="false" >
						<gene:campoScheda title='${gene:if(param.tipgen eq "1", "Codice categoria", "Codice prestazione")}' campo="CATOFF_${contatore}" entita="OPES" campoFittizio="true" definizione="T30;0;;;CATOFF" value="${item[0]}"  modificabile="${campiModificabili}"/>
						<gene:campoScheda title="Descrizione" campo="DESCAT_${contatore}" entita="CAIS" campoFittizio="true" definizione="T2000;0;;;DESCAT" value="${item[1]}" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.OPES.CATOFF") and campiModificabili}' visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.OPES.CATOFF")}' />
						<gene:campoScheda campo="ACONTEC_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;ACONTEC" value="${item[2]}" />
						<gene:campoScheda campo="QUAOBB_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;QUAOBB" value="${item[3]}" />
						<gene:campoScheda campo="TIPLAVG_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="N7;0;;;TIPLAVG" value="${item[4]}" title="Tipo lavoro" />
						<gene:campoScheda campo="ISFOGLIA_${contatore}" entita="V_CAIS_TIT"  visibile="false" campoFittizio="true" definizione="T2;0;;;G_ISFOGLI_T" value="${item[13]}"/>
					</gene:archivio>

					<gene:campoScheda title="Codice gara" campo="NGARA3_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="T20;1;;;NGARA_OPS" value="${item[5]}" />
					<gene:campoScheda title="Numero progressivo dell'ulteriore categoria" campo="NOPEGA_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="N3;1;;;NOPEGA" value="${item[6]}" />
					<gene:campoScheda title="Importo" campo="IMPAPO_${contatore}" entita="OPES" campoFittizio="true" definizione="F15;0;;MONEY;IMPAPO" value="${item[7]}" modificabile="${campiModificabili}"/>
					<gene:campoScheda title="Classifica" campo="NUMCLU_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="N2;0;;;" value="${item[8]}" /> 
					<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_PRE_LAVORI_${contatore}" campoFittizio="true" entita="OPES" definizione="N2;0;G_z09;;G1NUMCLU"	value="${item[8]}" modificabile="${campiModificabili}"/>
					<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_PRE_FORNITURE_${contatore}" campoFittizio="true" entita="OPES" definizione="N2;0;G_z07;;G1NUMCLU" value="${item[8]}" modificabile="${campiModificabili}"/>
					<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_PRE_SERVIZI_${contatore}" campoFittizio="true" entita="OPES" definizione="N2;0;G_z08;;G1NUMCLU" value="${item[8]}" modificabile="${campiModificabili}"/>
					<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_PRE_LAVORI150_${contatore}" campoFittizio="true" entita="OPES" definizione="N2;0;G_z11;;G1NUMCLU" value="${item[8]}" modificabile="${campiModificabili}"/>
					<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_${contatore}" campoFittizio="true" entita="OPES" definizione="N2;0;G_z12;;G1NUMCLU" value="${item[8]}" modificabile="${campiModificabili}"/>
					<gene:campoScheda title="Importo d'iscrizione" campo="ISCOFF_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="F15;0;;MONEY;ISCOFF" value="${item[9]}" />
		
					<gene:campoScheda title="Percentuale ${gene:if(param.tipgen eq '1', 'categoria', 'prestazione')}" campo="PERCEN_CATEG_${contatore}" entita="OPES" campoFittizio="true" definizione="T5;0" modificabile="false"/>
		
					<gene:campoScheda title="Superspecialistica subapp.al 30%?" campo="ACONTEC_${contatore}" entita="OPES" campoFittizio="true" definizione="T1;0;;SN;G1ACONTEUL" value="${item[10]}" />
					<gene:campoScheda title="Scorporabile?" campo="QUAOBB_${contatore}" entita="OPES" campoFittizio="true" definizione="T1;0;;SN;G1QUAOBBUL" value="${item[11]}" />
					<gene:campoScheda title="Note" campo="DESCOP_${contatore}" entita="OPES" campoFittizio="true" definizione="T2000;0;;;DESCOP" value="${item[12]}" modificabile="${campiModificabili}"/>
				</gene:gruppoCampi>
				<gene:fnJavaScriptScheda funzione='visualizzaNumeroClassifica("#CAIS_TIPLAVG_${contatore}#","#V_CAIS_TIT_ISFOGLIA_${contatore}#", "OPES", ${contatore}, true)' elencocampi='CAIS_TIPLAVG_${contatore};V_CAIS_TIT_ISFOGLIA_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#OPES_NUMCLU_CAT_PRE_LAVORI_${contatore}#", "OPES", ${contatore})' elencocampi='OPES_NUMCLU_CAT_PRE_LAVORI_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#OPES_NUMCLU_CAT_PRE_FORNITURE_${contatore}#", "OPES", ${contatore})' elencocampi='OPES_NUMCLU_CAT_PRE_FORNITURE_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#OPES_NUMCLU_CAT_PRE_SERVIZI_${contatore}#", "OPES", ${contatore})' elencocampi='OPES_NUMCLU_CAT_PRE_SERVIZI_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#OPES_NUMCLU_CAT_PRE_LAVORI150_${contatore}#", "OPES", ${contatore})' elencocampi='OPES_NUMCLU_CAT_PRE_LAVORI150_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_${contatore}#", "OPES", ${contatore})' elencocampi='OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='aggiornaCategorieAppalto("OPES_IMPAPO_${contatore}")' elencocampi='OPES_IMPAPO_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='aggiornaClassificaCategoriaPrevalente()' elencocampi='OPES_ACONTEC_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='setModificatoUlterioreCategoria(${contatore})' elencocampi='OPES_CATOFF_${contatore};OPES_IMPAPO_${contatore};OPES_NUMCLU_CAT_PRE_LAVORI_${contatore};OPES_NUMCLU_CAT_PRE_FORNITURE_${contatore};OPES_NUMCLU_CAT_PRE_SERVIZI_${contatore};OPES_NUMCLU_CAT_PRE_LAVORI150_${contatore};OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_${contatore};OPES_ISCOFF_${contatore};OPES_ACONTEC_${contatore};OPES_QUAOBB_${contatore};OPES_DESCOP_${contatore}' esegui="false" />
				<c:set var="contatore" value="${contatore + 1}" />
			</c:forEach>
		</c:if>
	</c:when>
	<c:otherwise>
		<c:if test='${not empty requestScope.initOPES}'>
			<c:forEach items="${initOPES}" var="item" varStatus="stato">
				<gene:gruppoCampi idProtezioni="OPES">
					<gene:campoScheda campo="DEL_ULTERIORE_CATEGORIA_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="T1" value="0" />
					<gene:campoScheda campo="MOD_ULTERIORE_CATEGORIA_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="T1" value="2" />
					<c:set var="nomeSezVis" value="sezioneVisibileOPES_${contatore}" />
					<c:choose>
						<c:when test='${empty param[nomeSezVis] or modo eq "VISUALIZZA"}'>
							<input type="hidden" name="sezioneVisibileOPES_${contatore}" id="sezioneVisibileOPES_${contatore}" value="1" />
						</c:when>
						<c:otherwise>
							<input type="hidden" name="sezioneVisibileOPES_${contatore}" id="sezioneVisibileOPES_${contatore}" value="${param[nomeSezVis]}" />
						</c:otherwise>
					</c:choose>
					<gene:campoScheda nome="titoloUlterioreCategoria_${contatore}">
						<td>
							<b>${titoloCategoriaUlteriore} ${item[6]}</b>
						</td>
												
						<td style="text-align: right">
							<c:if test='${modoAperturaScheda ne "VISUALIZZA" and gene:checkProtFunz(pageContext, "DEL","Elimina-Categ-Ulteriore")}'><a href="javascript:eliminaUlterioreCategoria(${contatore})" title="Elimina ${titoloCategoriaUlteriore}" class="link-generico">Elimina</a>&nbsp;
							<a href="javascript:eliminaUlterioreCategoria(${contatore})" title="Elimina ${titoloCategoriaUlteriore}" class="link-generico"><img src='${pageContext.request.contextPath}/img/opzioni_del.gif' height='16' width='16' alt='Elimina ${titoloCategoriaUlteriore}'></a>&nbsp;</c:if>
						</td>
						
					</gene:campoScheda>
					
					<gene:archivio titolo="Categorie d'iscrizione"
						lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.OPES.CATOFF"), "gene/cais/lista-categorie-iscrizione-popup.jsp", "")}'
						scheda=""
						schedaPopUp=""
						campi="V_CAIS_TIT.CAISIM;V_CAIS_TIT.DESCAT;V_CAIS_TIT.ACONTEC;V_CAIS_TIT.QUAOBB;V_CAIS_TIT.TIPLAVG;V_CAIS_TIT.ISFOGLIA"
						chiave=""
						where="${whereCategoriaParent}"
						formName="formUlterioreCategoriaGare${contatore}"
						inseribile="false" >
						<gene:campoScheda title='${gene:if(param.tipgen eq "1", "Codice categoria", "Codice prestazione")}' campo="CATOFF_${contatore}" entita="OPES" campoFittizio="true" definizione="T30;0;;;CATOFF" defaultValue="${item[0]}"  modificabile="${campiModificabili}"/>
						<gene:campoScheda title="Descrizione" campo="DESCAT_${contatore}" entita="CAIS" campoFittizio="true" definizione="T2000;0;;;DESCAT" defaultValue="${item[1]}" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.OPES.CATOFF") and campiModificabili}' visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.OPES.CATOFF")}' />
						<gene:campoScheda campo="ACONTEC_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;ACONTEC" defaultValue="${item[2]}" />
						<gene:campoScheda campo="QUAOBB_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;QUAOBB" defaultValue="${item[3]}" />
						<gene:campoScheda campo="TIPLAVG_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="N7;0;;;TIPLAVG" defaultValue="${item[4]}" title="Tipo lavoro" />
						<gene:campoScheda campo="ISFOGLIA_${contatore}" entita="V_CAIS_TIT"  visibile="false" campoFittizio="true" definizione="T2;0;;;G_ISFOGLI_T" value="${item[13]}"/>
					</gene:archivio>

					<gene:campoScheda title="Codice gara" campo="NGARA3_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="T20;1;;;NGARA_OPS" defaultValue="${item[5]}" />
					<gene:campoScheda title="Numero progressivo dell'ulteriore categoria" campo="NOPEGA_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="N3;1;;;NOPEGA" defaultValue="" />
					<gene:campoScheda title="Importo" campo="IMPAPO_${contatore}" entita="OPES" campoFittizio="true" definizione="F15;0;;MONEY;IMPAPO" defaultValue="${item[7]}"/>
					<gene:campoScheda title="Classifica" campo="NUMCLU_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="N2;0;;;" defaultValue="${item[8]}"  modificabile="${campiModificabili}"/> 
					<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_PRE_LAVORI_${contatore}" campoFittizio="true" entita="OPES" definizione="N2;0;G_z09;;G1NUMCLU"	defaultValue="${item[8]}" modificabile="${campiModificabili}"/>
					<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_PRE_FORNITURE_${contatore}" campoFittizio="true" entita="OPES" definizione="N2;0;G_z07;;G1NUMCLU" defaultValue="${item[8]}" modificabile="${campiModificabili}"/>
					<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_PRE_SERVIZI_${contatore}" campoFittizio="true" entita="OPES" definizione="N2;0;G_z08;;G1NUMCLU" defaultValue="${item[8]}" modificabile="${campiModificabili}"/>
					<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_PRE_LAVORI150_${contatore}" campoFittizio="true" entita="OPES" definizione="N2;0;G_z11;;G1NUMCLU"	defaultValue="${item[8]}"/>
					<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_${contatore}" campoFittizio="true" entita="OPES" definizione="N2;0;G_z12;;G1NUMCLU"	defaultValue="${item[8]}" modificabile="${campiModificabili}"/>
					<gene:campoScheda title="Importo d'iscrizione" campo="ISCOFF_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="F15;0;;MONEY;ISCOFF" defaultValue="${item[9]}" />
		
					<gene:campoScheda title="Percentuale ${gene:if(param.tipgen eq '1', 'categoria', 'prestazione')}" campo="PERCEN_CATEG_${contatore}" entita="OPES" campoFittizio="true" definizione="T5;0" modificabile="false"/>
		
					<gene:campoScheda title="Superspecialistica subapp.al 30%?" campo="ACONTEC_${contatore}" entita="OPES" campoFittizio="true" definizione="T1;0;;SN;G1ACONTEUL" defaultValue="${item[10]}" />
					<gene:campoScheda title="Scorporabile?" campo="QUAOBB_${contatore}" entita="OPES" campoFittizio="true" definizione="T1;0;;SN;G1QUAOBBUL" defaultValue="${item[11]}" />
					<gene:campoScheda title="Note" campo="DESCOP_${contatore}" entita="OPES" campoFittizio="true" definizione="T2000;0;;;DESCOP" defaultValue="${item[12]}" modificabile="${campiModificabili}"/>
				</gene:gruppoCampi>
				<gene:fnJavaScriptScheda funzione='visualizzaNumeroClassifica("#CAIS_TIPLAVG_${contatore}#","#V_CAIS_TIT_ISFOGLIA_${contatore}#", "OPES", ${contatore}, true)' elencocampi='CAIS_TIPLAVG_${contatore};V_CAIS_TIT_ISFOGLIA_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#OPES_NUMCLU_CAT_PRE_LAVORI_${contatore}#", "OPES", ${contatore})' elencocampi='OPES_NUMCLU_CAT_PRE_LAVORI_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#OPES_NUMCLU_CAT_PRE_FORNITURE_${contatore}#", "OPES", ${contatore})' elencocampi='OPES_NUMCLU_CAT_PRE_FORNITURE_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#OPES_NUMCLU_CAT_PRE_SERVIZI_${contatore}#", "OPES", ${contatore})' elencocampi='OPES_NUMCLU_CAT_PRE_SERVIZI_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#OPES_NUMCLU_CAT_PRE_LAVORI150_${contatore}#", "OPES", ${contatore})' elencocampi='OPES_NUMCLU_CAT_PRE_LAVORI150_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_${contatore}#", "OPES", ${contatore})' elencocampi='OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='aggiornaCategorieAppalto("OPES_IMPAPO_${contatore}")' elencocampi='OPES_IMPAPO_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='aggiornaClassificaCategoriaPrevalente()' elencocampi='OPES_ACONTEC_${contatore}' esegui="false" />
				<gene:fnJavaScriptScheda funzione='setModificatoUlterioreCategoria(${contatore})' elencocampi='OPES_CATOFF_${contatore};OPES_IMPAPO_${contatore};OPES_NUMCLU_CAT_PRE_LAVORI_${contatore};OPES_NUMCLU_CAT_PRE_FORNITURE_${contatore};OPES_NUMCLU_CAT_PRE_SERVIZI_${contatore};OPES_NUMCLU_CAT_PRE_LAVORI150_${contatore};OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_${contatore};OPES_ISCOFF_${contatore};OPES_ACONTEC_${contatore};OPES_QUAOBB_${contatore};OPES_DESCOP_${contatore}' esegui="false" />
				<c:set var="contatore" value="${contatore + 1}" />
			</c:forEach>
		</c:if>
	</c:otherwise>
</c:choose>

<c:if test='${modoAperturaScheda ne "VISUALIZZA"}'>
	<c:forEach var="ultCatVuote" begin="1" end="5" >
		<gene:gruppoCampi idProtezioni="OPES" >
			<gene:campoScheda campo="DEL_ULTERIORE_CATEGORIA_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="T1" value="0" />
			<gene:campoScheda campo="MOD_ULTERIORE_CATEGORIA_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="T1" value="0" />
			<c:set var="nomeSezVis" value="sezioneVisibileOPES_${contatore}" />
			<c:choose>
				<c:when test='${empty param[nomeSezVis] or (modo eq "VISUALIZZA" or modo eq "NUOVO")}'>
					<input type="hidden" name="sezioneVisibileOPES_${contatore}" id="sezioneVisibileOPES_${contatore}" value="0" />
				</c:when>
				<c:otherwise>
					<input type="hidden" name="sezioneVisibileOPES_${contatore}" id="sezioneVisibileOPES_${contatore}" value="${param[nomeSezVis]}" />
				</c:otherwise>
			</c:choose>

			<gene:campoScheda nome="titoloUlterioreCategoria_${contatore}">
				<td>
					<b>Nuova ${titoloCategoriaUlteriore}</b>
				</td>
				
				<td style="text-align: right">
							<c:if test='${gene:checkProtFunz(pageContext, "DEL","Elimina-Categ-Ulteriore")}'><a href="javascript:eliminaUlterioreCategoria(${contatore})" title="Elimina ${titoloCategoriaUlteriore}" class="link-generico">Elimina</a>&nbsp;
							<a href="javascript:eliminaUlterioreCategoria(${contatore})" title="Elimina ${titoloCategoriaUlteriore}" class="link-generico"><img src='${pageContext.request.contextPath}/img/opzioni_del.gif' height='16' width='16' alt='Elimina ${titoloCategoriaUlteriore}'></a>&nbsp;</c:if>
						</td>
				
			</gene:campoScheda>
			<gene:archivio titolo="Categorie d'iscrizione"
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.OPES.CATOFF"), "gene/cais/lista-categorie-iscrizione-popup.jsp", "")}'
				scheda=""
				schedaPopUp=""
				campi="V_CAIS_TIT.CAISIM;V_CAIS_TIT.DESCAT;V_CAIS_TIT.ACONTEC;V_CAIS_TIT.QUAOBB;V_CAIS_TIT.TIPLAVG;V_CAIS_TIT.ISFOGLIA"
				chiave=""
				where="${whereCategoriaParent}"
				formName="formUlterioreCategoriaGare${contatore}" inseribile="false">
				<gene:campoScheda title='${gene:if(param.tipgen eq "1", "Codice categoria", "Codice prestazione")}' campo="CATOFF_${contatore}" entita="OPES" campoFittizio="true" definizione="T30;0;;;CATOFF"  modificabile="${campiModificabili}"/>
				<gene:campoScheda title="Descrizione" campo="DESCAT_${contatore}" entita="CAIS" campoFittizio="true" definizione="T2000;0;;;DESCAT" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.OPES.CATOFF") and campiModificabili}' visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.OPES.CATOFF")}' />
				<gene:campoScheda campo="ACONTEC_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;ACONTEC" />
				<gene:campoScheda campo="QUAOBB_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;QUAOBB" />
				<gene:campoScheda campo="TIPLAVG_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="N7;0;;;TIPLAVG" value="1" title="Tipo lavoro" />
				<gene:campoScheda campo="ISFOGLIA_${contatore}" entita="V_CAIS_TIT"  visibile="false" campoFittizio="true" definizione="T2;0;;;G_ISFOGLI_T" />
			</gene:archivio>

			<gene:campoScheda title="Codice lavoro" campo="NGARA3_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="T20;1;;;NGARA_OPS" value="${nGara}" />
			<gene:campoScheda title="Numero progressivo dell'ulteriore categoria" campo="NOPEGA_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="N3;1;;;NOPEGA" />
			<gene:campoScheda title="Importo" campo="IMPAPO_${contatore}" entita="OPES" campoFittizio="true" definizione="F15;0;;MONEY;IMPAPO"  modificabile="${campiModificabili}"/>
			<gene:campoScheda title="Classifica" campo="NUMCLU_${contatore}" campoFittizio="true" entita="OPES" visibile="false" definizione="N2;0;;;" />
			<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_PRE_LAVORI_${contatore}" campoFittizio="true" entita="OPES" definizione="N2;0;G_z09;;G1NUMCLU"  modificabile="${campiModificabili}"/>
			<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_PRE_FORNITURE_${contatore}" campoFittizio="true" entita="OPES" definizione="N2;0;G_z07;;G1NUMCLU"  modificabile="${campiModificabili}"/>
			<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_PRE_SERVIZI_${contatore}" campoFittizio="true" entita="OPES" definizione="N2;0;G_z08;;G1NUMCLU"   modificabile="${campiModificabili}"/>
			<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_PRE_LAVORI150_${contatore}" campoFittizio="true" entita="OPES" definizione="N2;0;G_z11;;G1NUMCLU"  modificabile="${campiModificabili}"/>
			<gene:campoScheda title="Classifica" campo="NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_${contatore}" campoFittizio="true" entita="OPES" definizione="N2;0;G_z12;;G1NUMCLU"  modificabile="${campiModificabili}"/>
			<gene:campoScheda title="Importo d'iscrizione" campo="ISCOFF_${contatore}" entita="OPES" campoFittizio="true" visibile="false" definizione="F15;0;;MONEY;ISCOFF" />

			<gene:campoScheda title="Percentuale ${gene:if(param.tipgen eq '1', 'categoria', 'prestazione')}" campo="PERCEN_CATEG_${contatore}" entita="OPES" campoFittizio="true" definizione="T5;0" modificabile="false" />

			<gene:campoScheda title="Superspecialistica subapp.al 30%?" campo="ACONTEC_${contatore}" entita="OPES" campoFittizio="true" definizione="T1;0;;SN;G1ACONTEUL"  />
			<gene:campoScheda title="Scorporabile?" campo="QUAOBB_${contatore}" entita="OPES" campoFittizio="true" definizione="T1;0;;SN;G1QUAOBBUL" />
			<gene:campoScheda title="Note" campo="DESCOP_${contatore}" entita="OPES" campoFittizio="true" definizione="T2000;0;;;DESCOP" modificabile="${campiModificabili}"/>
		</gene:gruppoCampi>
		<gene:fnJavaScriptScheda funzione='visualizzaNumeroClassifica("#CAIS_TIPLAVG_${contatore}#","#V_CAIS_TIT_ISFOGLIA_${contatore}#", "OPES", ${contatore}, true)' elencocampi='CAIS_TIPLAVG_${contatore};V_CAIS_TIT_ISFOGLIA_${contatore}' esegui="false" />
		<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#OPES_NUMCLU_CAT_PRE_LAVORI_${contatore}#", "OPES", ${contatore})' elencocampi='OPES_NUMCLU_CAT_PRE_LAVORI_${contatore}' esegui="false" />
		<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#OPES_NUMCLU_CAT_PRE_FORNITURE_${contatore}#", "OPES", ${contatore})' elencocampi='OPES_NUMCLU_CAT_PRE_FORNITURE_${contatore}' esegui="false" />
		<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#OPES_NUMCLU_CAT_PRE_SERVIZI_${contatore}#", "OPES", ${contatore})' elencocampi='OPES_NUMCLU_CAT_PRE_SERVIZI_${contatore}' esegui="false" />
		<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#OPES_NUMCLU_CAT_PRE_LAVORI150_${contatore}#", "OPES", ${contatore})' elencocampi='OPES_NUMCLU_CAT_PRE_LAVORI150_${contatore}' esegui="false" />
		<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_${contatore}#", "OPES", ${contatore})' elencocampi='OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_${contatore}' esegui="false" />
		<gene:fnJavaScriptScheda funzione='aggiornaCategorieAppalto("OPES_IMPAPO_${contatore}")' elencocampi='OPES_IMPAPO_${contatore}' esegui="false" />
		<gene:fnJavaScriptScheda funzione='aggiornaClassificaCategoriaPrevalente()' elencocampi='OPES_ACONTEC_${contatore}' esegui="false" />
		<gene:fnJavaScriptScheda funzione='setModificatoUlterioreCategoria(${contatore})' elencocampi='OPES_CATOFF_${contatore};OPES_IMPAPO_${contatore};OPES_NUMCLU_CAT_PRE_LAVORI_${contatore};OPES_NUMCLU_CAT_PRE_FORNITURE_${contatore};OPES_NUMCLU_CAT_PRE_SERVIZI_${contatore};OPES_NUMCLU_CAT_PRE_LAVORI150_${contatore};OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_${contatore};OPES_ISCOFF_${contatore};OPES_ACONTEC_${contatore};OPES_QUAOBB_${contatore};OPES_DESCOP_${contatore}' esegui="false" />
		<c:set var="contatore" value="${contatore + 1}" />
	</c:forEach>
	<c:if test='${gene:checkProtFunz(pageContext, "INS","Agg-Categ-Ulteriore")}'>
		<gene:campoScheda nome="LinkVisualizzaUltCat">
			<td style="width:200px;">&nbsp;</td>
			<td class="valore-dato">
				<a href="javascript:visualizzaProssimaUlterioreCategoria();" class="link-generico"><img src="${pageContext.request.contextPath}/img/opzioni_add.gif" title="" alt="Aggiungi ${titoloCategoriaUlteriore}" height="16" width="16">&nbsp;Aggiungi ${titoloCategoriaUlteriore}</a>
			</td>
		</gene:campoScheda>
		<gene:campoScheda nome="MsgUltimaUltCat">
			<td style="width:200px;">&nbsp;</td>
			<td class="valore-dato">
				<fmt:message key="info.scheda.modifica.raggiuntoMaxDatiInseribili">
					<fmt:param value="e ulteriori categorie"/>
				</fmt:message>
			</td>
		</gene:campoScheda>
	</c:if>
	<gene:campoScheda campo="NUMERO_CATEGORIE" campoFittizio="true" visibile="false" definizione="N3" value="${contatore-1}" />
</c:if>

<gene:javaScript>

	// Variabili Javascript globali necessarie alle categorie ulteriori
	var categoriaNascosta = false;
<c:choose>
	<c:when test='${modo ne "NUOVO"}'>
		<c:choose>
			<c:when test='${empty ulterioriCategorie}'>
				var idUltimaUlterioreCategoriaVisualizzata = 0;
				var maxIdUlterioreCategoriaVisualizzabile = 5;
				<c:set var="numeroUlterioriCategorie" value="0" />
			</c:when>
			<c:otherwise>
				var idUltimaUlterioreCategoriaVisualizzata = ${fn:length(ulterioriCategorie)};
				var maxIdUlterioreCategoriaVisualizzabile = ${fn:length(ulterioriCategorie)+5};
				<c:set var="numeroUlterioriCategorie" value="${fn:length(ulterioriCategorie)}" />
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test='${empty initOPES}'>
				var idUltimaUlterioreCategoriaVisualizzata = 0;
				var maxIdUlterioreCategoriaVisualizzabile = 5;
				<c:set var="numeroUlterioriCategorie" value="0" />
			</c:when>
			<c:otherwise>
				var idUltimaUlterioreCategoriaVisualizzata = ${fn:length(initOPES)};
				var maxIdUlterioreCategoriaVisualizzabile = ${fn:length(initOPES)+5};
				<c:set var="numeroUlterioriCategorie" value="${fn:length(initOPES)}" />
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>	

<c:if test='${modoAperturaScheda eq "NUOVO" && !garaLottoUnico and empty requestScope.initCATG}'>
	setValue("CATG_CATIGA", "${requestScope.initCATIGA}");
	setValue("CAIS_DESCAT", "${requestScope.initDESCAT}");
</c:if>

<c:if test='${(modoAperturaScheda ne "VISUALIZZA")}'>
	
	var arrayImportiIscrizioneLavori = new Array(); //Il vettore contiene a sua volta il vettore[importo,numero classifica]
	var numeroClassiNegative = 0;
	var indice=0;
        <c:forEach items="${importiIscrizioneLavori}" var="parametro" varStatus="ciclo">
			<c:if test='${empty parametro.arcTabellato  or parametro.arcTabellato != "1"}'>
                arrayImportiIscrizioneLavori[indice] =  new Array("${fn:trim(parametro.datoSupplementare)}","${parametro.tipoTabellato}");
    		    indice++;
                if (${parametro.tipoTabellato < 0})
    				numeroClassiNegative = numeroClassiNegative + 1;
            </c:if>
	</c:forEach>

	var arrayImportiIscrizioneForniture = new Array();
	indice=0;
	<c:forEach items="${importiIscrizioneForniture}" var="parametro" varStatus="ciclo">
		<c:if test='${empty parametro.arcTabellato  or parametro.arcTabellato != "1"}'>
			arrayImportiIscrizioneForniture[${ciclo.index}] = "${fn:trim(parametro.datoSupplementare)}";
			indice++;
		</c:if>	
	</c:forEach>
	
	var arrayImportiIscrizioneServizi = new Array();
	indice=0;
	<c:forEach items="${importiIscrizioneServizi}" var="parametro" varStatus="ciclo">
		<c:if test='${empty parametro.arcTabellato  or parametro.arcTabellato != "1"}'>
			arrayImportiIscrizioneServizi[${ciclo.index}] = "${fn:trim(parametro.datoSupplementare)}";
			indice++;
		</c:if>
	</c:forEach>

	var arrayImportiIscrizioneLavori150 = new Array();
	indice=0;
	<c:forEach items="${importiIscrizioneLavori150}" var="parametro" varStatus="ciclo">
		<c:if test='${empty parametro.arcTabellato  or parametro.arcTabellato != "1"}'>
			arrayImportiIscrizioneLavori150[${ciclo.index}] = "${fn:trim(parametro.datoSupplementare)}";
			indice++;
		</c:if>
	</c:forEach>
	
	var arrayImportiIscrizioneServiziProfessionali = new Array();
	indice=0;
	<c:forEach items="${importiIscrizioneServiziProfessionali}" var="parametro" varStatus="ciclo">
		<c:if test='${empty parametro.arcTabellato  or parametro.arcTabellato != "1"}'>
			arrayImportiIscrizioneServiziProfessionali[${ciclo.index}] = "${fn:trim(parametro.datoSupplementare)}";
			indice++;
		</c:if>
	</c:forEach>
	

	
	function nascondiSezioneUlterioreCategoria(idUltCat, sbiancaValori){
		showObj("rowtitoloUlterioreCategoria_" + idUltCat, false);
		showObj("rowOPES_CATOFF_"  + idUltCat, false);
		showObj("rowCAIS_DESCAT_"    + idUltCat, false);
		showObj("rowOPES_DESCAT_"  + idUltCat, false);
		showObj("rowOPES_IMPAPO_"  + idUltCat, false);
		showObj("rowOPES_NUMCLU_CAT_PRE_LAVORI_"    + idUltCat, false);
		showObj("rowOPES_NUMCLU_CAT_PRE_FORNITURE_" + idUltCat, false);
		showObj("rowOPES_NUMCLU_CAT_PRE_SERVIZI_"   + idUltCat, false);
		showObj("rowOPES_NUMCLU_CAT_PRE_LAVORI150_"    + idUltCat, false);
		showObj("rowOPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_"   + idUltCat, false);
		showObj("rowOPES_ISCOFF_"  + idUltCat, false);
		showObj("rowOPES_PERCEN_CATEG_"  + idUltCat, false);
		showObj("rowOPES_ACONTEC_" + idUltCat, false);
		showObj("rowOPES_QUAOBB_"  + idUltCat, false);
		showObj("rowOPES_INCMAN_"  + idUltCat, false);
		showObj("rowOPES_DESCOP_"  + idUltCat, false);

		if(sbiancaValori){
			setValue("OPES_CATOFF_"  + idUltCat, "");
			setValue("CAIS_DESCAT_" + idUltCat, "");
			setValue("OPES_DESCAT_"  + idUltCat, "");
			setValue("OPES_IMPAPO_"  + idUltCat, "");
			setValue("OPES_NUMCLU_CAT_PRE_LAVORI_"    + idUltCat, "");
			setValue("OPES_NUMCLU_CAT_PRE_FORNITURE_" + idUltCat, "");
			setValue("OPES_NUMCLU_CAT_PRE_SERVIZI_"   + idUltCat, "");
			setValue("OPES_NUMCLU_CAT_PRE_LAVORI150_"    + idUltCat, "");
			setValue("OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_"    + idUltCat, "");
			setValue("OPES_PERCEN_CATEG_"  + idUltCat, "");
			setValue("OPES_ISCOFF_"  + idUltCat, "");
			setValue("OPES_ACONTEC_" + idUltCat, "");
			setValue("OPES_QUAOBB_"  + idUltCat, "");
			setValue("OPES_INCMAN_"  + idUltCat, "");
			setValue("OPES_DESCOP_"  + idUltCat, "");
		}
		
		document.getElementById("sezioneVisibileOPES_" +idUltCat).value = 0;
	}

	function visualizzaSezioneUlterioreCategoria(idUltCat){
		var tipoAppalto=getValue("TORN_TIPGEN");
	    var isfoglia = getValue("V_CAIS_TIT_ISFOGLIA_" + new String(idUltCat));
		showObj("rowtitoloUlterioreCategoria_" + idUltCat, true);
		showObj("rowOPES_CATOFF_"  + idUltCat, true);
		showObj("rowCAIS_DESCAT_"  + idUltCat, true);
		showObj("rowOPES_DESCAT_"  + idUltCat, true);
		showObj("rowOPES_IMPAPO_"  + idUltCat, true);
		visualizzaNumeroClassifica(tipoAppalto,isfoglia, "OPES", new String(idUltCat), false);
		showObj("rowOPES_PERCEN_CATEG_"  + idUltCat, true);
		showObj("rowOPES_ISCOFF_"  + idUltCat, true);
		//showObj("rowOPES_ACONTEC_" + idUltCat, true);
		//showObj("rowOPES_QUAOBB_"  + idUltCat, true);
		showObj("rowOPES_INCMAN_"  + idUltCat, true);
		showObj("rowOPES_DESCOP_"  + idUltCat, true);
		
		document.getElementById("sezioneVisibileOPES_" +idUltCat).value = 1;
	}

	var indiceProgressivo = 1;
	for(indiceProgressivo; indiceProgressivo <= idUltimaUlterioreCategoriaVisualizzata; indiceProgressivo++){
		var valore = document.getElementById("sezioneVisibileOPES_" + indiceProgressivo).value;
		if("" == valore || valore == 0){
			document.getElementById("sezioneVisibileOPES_" + indiceProgressivo).value = 0;
			nascondiSezioneUlterioreCategoria(indiceProgressivo, false);
		} else {
			document.getElementById("sezioneVisibileOPES_" + indiceProgressivo).value = 1;
		}
	}
	
	for(indiceProgressivo = idUltimaUlterioreCategoriaVisualizzata + 1; indiceProgressivo <= maxIdUlterioreCategoriaVisualizzabile; indiceProgressivo++){
		var valore = document.getElementById("sezioneVisibileOPES_" + indiceProgressivo).value;
		if("" == valore || valore == 0){
			document.getElementById("sezioneVisibileOPES_" + indiceProgressivo).value = 0;
			nascondiSezioneUlterioreCategoria(indiceProgressivo, false);
		} else {
			document.getElementById("sezioneVisibileOPES_" + indiceProgressivo).value = 1;
			visualizzaSezioneUlterioreCategoria(indiceProgressivo);
		}
	}

	function visualizzaProssimaUlterioreCategoria(){
		var indice = idUltimaUlterioreCategoriaVisualizzata;
		indice++;
		// Cerco, se esiste, la prima sezione 'Ulteriore Categoria' visualizzabile.
		// Se la i-esima categoria ulteriore non e' stata cancellata		
		var categoriaEliminata = getValue("OPES_DEL_ULTERIORE_CATEGORIA_" + indice);
		while(indice <= maxIdUlterioreCategoriaVisualizzabile && categoriaEliminata != "0"){
			indice++;
		}
		
		if(categoriaEliminata != null && categoriaEliminata == "0"){
			idUltimaUlterioreCategoriaVisualizzata = indice;
			visualizzaSezioneUlterioreCategoria(indice);
		}
		
		// Quando la variabile 'indice' e' uguale alla variabile globale 
		// 'maxIdUlterioreCategoriaVisualizzabile' allora bisogna nascondere il link
		// presente sulla riga con id uguale a 'rowLinkVisualizzaUltCat'
		if(indice == maxIdUlterioreCategoriaVisualizzabile){		
			showObj("rowLinkVisualizzaUltCat", false);
			showObj("rowMsgUltimaUltCat", true);
		}
	}
	
	function eliminaUlterioreCategoria(idUltCat){
		if(confirm("Procedere con l'eliminazione ?")){
			nascondiSezioneUlterioreCategoria(idUltCat, true);
		  setValue("OPES_DEL_ULTERIORE_CATEGORIA_" + idUltCat, "1");
		  //aggiornaClassificaCategoriaPrevalente();
		}
	}

	function setModificatoUlterioreCategoria(id){
		var arrayCampi = new Array("OPES_CATOFF_","OPES_IMPAPO_","OPES_NUMCLU_CAT_PRE_LAVORI_","OPES_NUMCLU_CAT_PRE_FORNITURE_","OPES_NUMCLU_CAT_PRE_SERVIZI_","OPES_NUMCLU_CAT_PRE_LAVORI150_","OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_","OPES_ISCOFF_","OPES_ACONTEC_","OPES_QUAOBB_","OPES_DESCOP_");
		var occorrenzaModificata = false;
		if(getValue("OPES_MOD_ULTERIORE_CATEGORIA_" + id) != "2"){
			for(var i=0; i < arrayCampi.length && !occorrenzaModificata; i++){
				if(isValueChanged(arrayCampi[i] + id)){
					occorrenzaModificata = true;
				}
			}
			if(occorrenzaModificata)
				setValue("OPES_MOD_ULTERIORE_CATEGORIA_" + id, "1");
			else
				setValue("OPES_MOD_ULTERIORE_CATEGORIA_" + id, "0");
		}
	}

	function setCampoNumeroClassifica(numeroClassifica, entita, progressivo){
		var campoNumeroClassifica, campoImportoIscrizione = "";
		var campoTiplavg = "CAIS_TIPLAVG";
		
		if(entita == "CATG"){
			campoNumeroClassifica  = entita + "_" + "NUMCLA";
			campoImportoIscrizione = entita + "_" + "IMPIGA";
		} else {
		  campoNumeroClassifica  = entita + "_" + "NUMCLU_" + progressivo;
		  campoImportoIscrizione = entita + "_" + "ISCOFF_" + progressivo;
		  campoTiplavg           = campoTiplavg + "_" + progressivo;
		}
		setValue(campoNumeroClassifica, numeroClassifica);
		if(numeroClassifica != null && numeroClassifica != ""){
			var tipoAppalto=getValue(campoTiplavg);
			if (tipoAppalto == null || tipoAppalto == "")
				tipoAppalto=getValue("TORN_TIPGEN");
			if(tipoAppalto == "1"){
				for(i=0; i < arrayImportiIscrizioneLavori.length; i++){
					if(toNum(numeroClassifica) == arrayImportiIscrizioneLavori[i][1])
						setValue(campoImportoIscrizione, arrayImportiIscrizioneLavori[i][0]);
				}
			}else if(tipoAppalto == "2")
				setValue(campoImportoIscrizione, arrayImportiIscrizioneForniture[toNum(numeroClassifica) - 1]);
			else if(tipoAppalto == "3")
				setValue(campoImportoIscrizione, arrayImportiIscrizioneServizi[toNum(numeroClassifica) - 1]);
			else if(tipoAppalto == "4")
				setValue(campoImportoIscrizione, arrayImportiIscrizioneLavori150[toNum(numeroClassifica) - 1]);
			else if(tipoAppalto == "5")
				setValue(campoImportoIscrizione, arrayImportiIscrizioneServiziProfessionali[toNum(numeroClassifica) - 1]);
		} else
			setValue(campoImportoIscrizione, "");
	}

	function aggiornaCategorieAppalto(campoModificato){
		if(campoModificato == "GARE_IMPAPP" || campoModificato == "CATG_IMPBASG"){
			aggiornaClassificaCategoriaPrevalente();
			if(campoModificato == "GARE_IMPAPP"){
				for(var j=1; j <= maxIdUlterioreCategoriaVisualizzabile; j++)
					calcoloPercentualeCategoria(j);
			}
		} else {
			aggiornaClassificaCategoriaUlteriore(campoModificato);
			calcoloPercentualeCategoria(campoModificato.substr(campoModificato.lastIndexOf("_")+1));
		}
	}

  // Funzione per l'aggiornamento della classifica della categoria prevalente
  // e valorizzazione dei campi CATG_NUMCLA, CATG_NUMCLA_CAT_PRE_LAVORI e
  // CATG_IMPIGA
	function aggiornaClassificaCategoriaPrevalente(){
		if(categoriaNascosta){return;}
		var importoOneriProgettazione = 0;
		
		if(getValue("GARE_ONPRGE") != null && getValue("GARE_ONPRGE") != "")
			importoOneriProgettazione = toNum(getValue("GARE_ONPRGE"));

		if(getValue("CAIS_TIPLAVG") == "1" && getValue("GARE_IMPAPP") != ""){
			var importoIscrizione = toNum(getValue("GARE_IMPAPP")) - importoOneriProgettazione;
			
			if(importoIscrizione > 0){
				importoIscrizione = importoIscrizione / 1.2;
				
				var numcla = 0;
				var impiga = 0; 
				for(i=0 + numeroClassiNegative; i< arrayImportiIscrizioneLavori.length; i++){
					numcla=arrayImportiIscrizioneLavori[i][1];
					impiga=arrayImportiIscrizioneLavori[i][0];
					if(toNum(arrayImportiIscrizioneLavori[i][0]) >= importoIscrizione)
					break;
				}
				setValue("CATG_NUMCLA", numcla);
				setValue("CATG_NUMCLA_CAT_PRE_LAVORI", numcla);
				setValue("CATG_IMPIGA", impiga);
			}
		}
	}

	function aggiornaClassificaCategoriaUlteriore(campoModificato){
		var indiceUlterioreCategoria = campoModificato.substr(campoModificato.lastIndexOf("_")+1); // prendo l'ultimo carattere
		var tmp = getValue("CAIS_TIPLAVG_" + indiceUlterioreCategoria);
		var importoIscrizione = 0;
		if(getValue("CAIS_TIPLAVG_" + indiceUlterioreCategoria) == "1"){
			if(getValue(campoModificato) != null && getValue(campoModificato) != ""){
				//Solo se categoria per lavori, calcola l'importo della classifica
					var importoIscrizione = eval(eval(getValue(campoModificato)) / 1.2);
					if(tmp == "1"){
						var numcla=0;
				        var impiga=0;
				        for(i=0 + numeroClassiNegative; i< arrayImportiIscrizioneLavori.length; i++){
							numcla=arrayImportiIscrizioneLavori[i][1];
							impiga=arrayImportiIscrizioneLavori[i][0];
							if(toNum(arrayImportiIscrizioneLavori[i][0]) >= importoIscrizione)
							break;
						}
						setValue("OPES_NUMCLU_" + indiceUlterioreCategoria, numcla);
						setValue("OPES_NUMCLU_CAT_PRE_LAVORI_" + indiceUlterioreCategoria, numcla);
						setValue("OPES_ISCOFF_" + indiceUlterioreCategoria, impiga);
					}
					setDivietoSubAppalto_QualifObbligatoria(indiceUlterioreCategoria);
			} else {
				setValue("OPES_NUMCLU_" + indiceUlterioreCategoria, "");
				setValue("OPES_NUMCLU_CAT_PRE_LAVORI_" + indiceUlterioreCategoria, "");
				setValue("OPES_ISCOFF_" + indiceUlterioreCategoria, "");
				setValue("OPES_ACONTEC_" + indiceUlterioreCategoria, "");
				setValue("OPES_QUAOBB_" + indiceUlterioreCategoria, "");
			}
		}
	}

	function setDivietoSubAppalto_QualifObbligatoria(indiceUlterioreCategoria){
		if(getValue("OPES_IMPAPO_" + indiceUlterioreCategoria) != "" && getValue("GARE_IMPAPP") != ""){
			var importoOneriProgettazione = 0;
			if(getValue("GARE_ONPRGE") != null && getValue("GARE_ONPRGE") != "")
				importoOneriProgettazione = toNum(getValue("GARE_ONPRGE"));
		
			if(getValue("OPES_IMPAPO_" + indiceUlterioreCategoria) > ((getValue("GARE_IMPAPP") - importoOneriProgettazione) * 0.1) && getValue("CAIS_ACONTEC_" + indiceUlterioreCategoria) == "1"){
				setValue("OPES_ACONTEC_" + indiceUlterioreCategoria, "1");
				setValue("OPES_QUAOBB_" + indiceUlterioreCategoria, "1");
			} else {
				setValue("OPES_ACONTEC_" + indiceUlterioreCategoria, "2");
				// Set del campo OPES_QUAOBB<i>
				if((getValue("OPES_IMPAPO_" + indiceUlterioreCategoria) > ((getValue("GARE_IMPAPP") - importoOneriProgettazione) * 0.1) || (getValue("OPES_IMPAPO_" + indiceUlterioreCategoria) != "" && getValue("OPES_IMPAPO_" + indiceUlterioreCategoria) > 150000)) &&	getValue("CAIS_QUAOBB_" + indiceUlterioreCategoria) == "1"){
					setValue("OPES_QUAOBB_" + indiceUlterioreCategoria, "1");
				} else {
					setValue("OPES_QUAOBB_" + indiceUlterioreCategoria, "2");
				}
			}
		} else {
			setValue("OPES_ACONTEC_" + indiceUlterioreCategoria, "");
			setValue("OPES_QUAOBB_" + indiceUlterioreCategoria, "");
		}
	}

  // Cambio lo stato della variabile globale 'controlloSezioniDinamiche'
  // per attivare i controlli sulle sezioni dinamiche presenti nella
  // pagina al momento del salvataggio
  controlloSezioniDinamiche = true;

	var arrayCampiSezioneDinamica = new Array("OPES_CATOFF_", "OPES_DESCAT_", "OPES_IMPAPO_", "OPES_NUMCLU_CAT_PRE_LAVORI_", "OPES_NUMCLU_CAT_PRE_FORNITURE_", "OPES_NUMCLU_CAT_PRE_SERVIZI_", "OPES_NUMCLU_CAT_PRE_LAVORI150_", "OPES_NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_","OPES_ACONTEC_", "OPES_QUAOBB_", "OPES_DESCOP_");
	arraySezioniDinamicheObj.push(new SezioneDinamicaObj(arrayCampiSezioneDinamica, maxIdUlterioreCategoriaVisualizzabile, "rowtitoloUlterioreCategoria_"));

	showObj("rowMsgUltimaUltCat", false);
</c:if>

	function calcoloPercentualeCategoria(progressivo){
		if(isObjShow("rowtitoloUlterioreCategoria_" + progressivo)){
			var importoCateg = toVal(getValue("OPES_IMPAPO_" + progressivo));
			var importoAppalto = toVal(getValue("GARE_IMPAPP"));
			var importoOneriProg = toVal(getValue("GARE_ONPRGE"));
	
			if(importoCateg != 0){
				//alert("progressivo = " + progressivo + "\nimportoCateg = " + importoCateg + "\nimportoAppalto = " + importoAppalto +"\nimportoOneriProg = " + importoOneriProg +"\nPercentuale = " + "" + (importoCateg * 100/(importoAppalto - importoOneriProg)).toFixed(2) + " %" );
				setValue("OPES_PERCEN_CATEG_" + progressivo, "" + (importoCateg * 100/(importoAppalto - importoOneriProg)).toFixed(2) + " %");
			} else {
				setValue("OPES_PERCEN_CATEG_" + progressivo, "");
			}
		}
	}

	function visualizzaNumeroClassifica(tipoAppalto, isfoglia, entita, progressivo, sbiancaValori){
		if(categoriaNascosta){
			return;
		}
		var idRiga1, idRiga2, idRiga3, idRiga4, idRiga5, idRigaDivietoSubappalto, idRigaQualificazioneObbligatoria = "";
		var nomeCampo1, nomeCampo2, nomeCampo3, nomeCampo4, nomeCampo5, nomeCampo6, campoDivietoSubappalto, campoQualificazioneObbligatoria = "";
		
		if(entita == "CATG"){
			nomeCampo1 = entita + "_" + "NUMCLA_CAT_PRE_LAVORI";
			nomeCampo2 = entita + "_" + "NUMCLA_CAT_PRE_FORNITURE";
			nomeCampo3 = entita + "_" + "NUMCLA_CAT_PRE_SERVIZI";
			nomeCampo4 = entita + "_" + "NUMCLA_CAT_PRE_LAVORI150";
			nomeCampo5 = entita + "_" + "NUMCLA_CAT_PRE_SERVIZIPROFESSIONALI";
			nomeCampo6 = entita + "_" + "IMPBASG";
			//nomeCampo5 = entita + "_" + "IMPBASG";
		} else {
			if(progressivo == null) progressivo = "";
		    nomeCampo1 = entita + "_" + "NUMCLU_CAT_PRE_LAVORI_" + progressivo;
		    nomeCampo2 = entita + "_" + "NUMCLU_CAT_PRE_FORNITURE_" + progressivo;
		    nomeCampo3 = entita + "_" + "NUMCLU_CAT_PRE_SERVIZI_" + progressivo;
		    nomeCampo4 = entita + "_" + "NUMCLU_CAT_PRE_LAVORI150_" + progressivo;
		    nomeCampo5 = entita + "_" + "NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI_" + progressivo;
			nomeCampo6 = entita + "_" + "IMPAPO_" + progressivo;
			//nomeCampo5 = entita + "_" + "IMPAPO_" + progressivo;
			
		    campoDivietoSubappalto = entita + "_" + "ACONTEC_" + progressivo;
		    campoQualificazioneObbligatoria = entita + "_" + "QUAOBB_" + progressivo;
		  
			idRigaDivietoSubappalto = "row" + campoDivietoSubappalto;
			idRigaQualificazioneObbligatoria = "row" + campoQualificazioneObbligatoria;
		}
		
	    idRiga1 = "row" + nomeCampo1;
  	    idRiga2 = "row" + nomeCampo2;
	    idRiga3 = "row" + nomeCampo3;
	    idRiga4 = "row" + nomeCampo4;
	    idRiga5 = "row" + nomeCampo5;
		
		if (tipoAppalto == "")
			tipoAppalto=getValue("TORN_TIPGEN");
		if(tipoAppalto == "1"){
			showObj(idRiga1, true);
			showObj(idRiga2, false);
			showObj(idRiga3, false);
			showObj(idRiga4, false);
			showObj(idRiga5, false);
						
			if (entita == "OPES"){
				showObj(idRigaDivietoSubappalto, true);
				showObj(idRigaQualificazioneObbligatoria, true);
			}

			if(sbiancaValori){
				aggiornaCategorieAppalto(nomeCampo6);
			}
				
		} else if(tipoAppalto == "2"){
			showObj(idRiga1, false);
			if(${esisteClassificaForniture}){
				showObj(idRiga2, true);

                if(isfoglia=="2"){
					document.getElementById(nomeCampo2).disabled = true;
					if(sbiancaValori)
						setCampoNumeroClassifica("", entita, progressivo);
				}else {
                       document.getElementById(nomeCampo2).disabled = false;
				}
			}else
				showObj(idRiga2, false);
			
			showObj(idRiga3, false);
			showObj(idRiga4, false);
			showObj(idRiga5, false);
						
			if (entita == "OPES"){
				showObj(idRigaDivietoSubappalto, false);
				showObj(idRigaQualificazioneObbligatoria, false);
			}

			if(sbiancaValori){
				setValue(nomeCampo2, "");
				if (entita == "OPES"){
					setValue(campoDivietoSubappalto, "");
					setValue(campoQualificazioneObbligatoria, "");
				}
			}
		} else if(tipoAppalto == "3"){
			showObj(idRiga1, false);
			showObj(idRiga2, false);
			if(${esisteClassificaServizi}){
				showObj(idRiga3, true);
				if(isfoglia=="2"){
					document.getElementById(nomeCampo3).disabled = true;
					if(sbiancaValori)
						setCampoNumeroClassifica("", entita, progressivo);
				}else {
				       document.getElementById(nomeCampo3).disabled = false;
				}
			}else
				showObj(idRiga3, false);

                        showObj(idRiga4, false);
			showObj(idRiga5, false);
			
			if (entita == "OPES"){
				showObj(idRigaDivietoSubappalto, false);
				showObj(idRigaQualificazioneObbligatoria, false);
			}

			if(sbiancaValori){
				setValue(nomeCampo3, "");
				if (entita == "OPES"){
					setValue(campoDivietoSubappalto, "");
					setValue(campoQualificazioneObbligatoria, "");
				}
			}
		} else if(tipoAppalto == "4"){
			showObj(idRiga1, false);
			showObj(idRiga2, false);
			showObj(idRiga3, false);
			if(${esisteClassificaLavori150})
				showObj(idRiga4, true);
			else
				showObj(idRiga4, false);
			showObj(idRiga5, false);
			
			if (entita == "OPES"){
				showObj(idRigaDivietoSubappalto, false);
				showObj(idRigaQualificazioneObbligatoria, false);
			}

			if(sbiancaValori){
				setValue(nomeCampo4, "");
				if (entita == "OPES"){
					setValue(campoDivietoSubappalto, "");
					setValue(campoQualificazioneObbligatoria, "");
				}
			}
		}else if(tipoAppalto == "5"){
			showObj(idRiga1, false);
			showObj(idRiga2, false);
			showObj(idRiga3, false);
			showObj(idRiga4, false);
			if(${esisteClassificaServiziProfessionali}){
				showObj(idRiga5, true);
				if(isfoglia=="2"){
					document.getElementById(nomeCampo5).disabled = true;
					if(sbiancaValori)
						setCampoNumeroClassifica("", entita, progressivo);
				}else {
					document.getElementById(nomeCampo5).disabled = false;
				}
			}else
				showObj(idRiga5, false);
			
			if (entita == "OPES"){
				showObj(idRigaDivietoSubappalto, false);
				showObj(idRigaQualificazioneObbligatoria, false);
			}

			if(sbiancaValori){
				setValue(nomeCampo5, "");
				if (entita == "OPES"){
					setValue(campoDivietoSubappalto, "");
					setValue(campoQualificazioneObbligatoria, "");
				}
			}
		}
	}

	// funzione da eseguire al caricamento della pagina per visualizzare la
	// riga corretta relativa al campo "Classifica"
	function initVisualizzazioneCampiNumeroClassifica(){
		var tipoAppalto=getValue("TORN_TIPGEN");
		var str = getValue("CAIS_TIPLAVG");
		var isfoglia = getValue("V_CAIS_TIT_ISFOGLIA");
		if(str != null && str != "")
			visualizzaNumeroClassifica(str, isfoglia, "CATG", null, false);
		else
			visualizzaNumeroClassifica(tipoAppalto, isfoglia, "CATG", null, false);
		
		for(var i=1; i <= ${numeroUlterioriCategorie}; i++){
			str = getValue("CAIS_TIPLAVG_" + i);
			isfoglia = getValue("V_CAIS_TIT_ISFOGLIA_" + i);
			if(str != null && str != "")
				visualizzaNumeroClassifica(str, isfoglia, "OPES", new String(i), false);
			else
				visualizzaNumeroClassifica(tipoAppalto, isfoglia,  "OPES", new String(i), false);
			
			calcoloPercentualeCategoria(i);
		}
	}

	initVisualizzazioneCampiNumeroClassifica();
	
	<c:if test="${param.lottoDiGara eq 'true'}">
		var categoria=getValue("CATG_CATIGA");
		var tastoEliminazioneCategoria;
		if(!categoria){
			nascondiCategoria(false);
		}else{
			showObj("rowLinkVisualizzaCat", false);
		}
		
		function nascondiCategoria(alert){
			if(alert){
				flag = confirm("Procedere con l'eliminazione ?");
				if(!flag){
					return;
				}
			}
			<c:choose>
			<c:when test="${modo ne 'VISUALIZZA' and campiModificabili}">
				showObj("rowLinkVisualizzaCat", true);
			</c:when>
			<c:otherwise>
				showObj("rowLinkVisualizzaCat", false);
			</c:otherwise>
			</c:choose>
			$('#titoloCategoriaPrevalente').closest('tr').hide();
			showObj("rowLinkVisualizzaUltCat", false);
			showObj("rowCATG_CATIGA", false);
			showObj("rowCAIS_DESCAT", false);
			showObj("rowCATG_IMPBASG", false);
			
			$("#CAIS_ACONTEC").removeAttr('value');
			$("#CAIS_QUAOBB").removeAttr('value');
			$("#CAIS_TIPLAVG").removeAttr('value');
			$("#CATG_CATIGA").removeAttr('value');
			$("#CAIS_DESCAT").removeAttr('value');
			$("#CATG_IMPBASG").removeAttr('value');
			$("#CATG_IMPIGA").removeAttr('value');
			$("#CATG_NUMCLA").removeAttr('value');
			$("#V_CAIS_TIT_ISFOGLIA").removeAttr('value');
			
			showObj("rowCATG_NUMCLA_CAT_PRE_LAVORI", false);
			showObj("rowCATG_NUMCLA_CAT_PRE_FORNITURE", false);
			showObj("rowCATG_NUMCLA_CAT_PRE_SERVIZI", false);
			showObj("rowCATG_NUMCLA_CAT_PRE_LAVORI150", false);
			showObj("rowCATG_NUMCLA_CAT_PRE_SERVIZIPROFESSIONALI", false);
			
			$("#CATG_NUMCLA_CAT_PRE_LAVORI").prop('selectedIndex',0);
			$("#CATG_NUMCLA_CAT_PRE_FORNITURE").prop('selectedIndex',0);
			$("#CATG_NUMCLA_CAT_PRE_SERVIZI").prop('selectedIndex',0);
			$("#CATG_NUMCLA_CAT_PRE_LAVORI150").prop('selectedIndex',0);
			$("#CATG_NUMCLA_CAT_PRE_SERVIZIPROFESSIONALI").prop('selectedIndex',0);
			
			showObj("tastoEliminazioneCategoria", false);
			$("#titoloCategoriaPrevalente").attr("colspan", 2);
			categoriaNascosta = true;
		}
		
		function visualizzaCategoria(){
			
			$('#titoloCategoriaPrevalente').closest('tr').show();
			showObj("rowCATG_CATIGA", true);
			showObj("rowCAIS_DESCAT", true);
			showObj("rowCATG_IMPBASG", true);
			showObj("rowCATG_NUMCLA_CAT_PRE_LAVORI", true);
			showObj("rowLinkVisualizzaCat", false);
			showObj("rowLinkVisualizzaUltCat", true);
			$("#titoloCategoriaPrevalente").attr("colspan", 1);
			showObj("tastoEliminazioneCategoria", true);
			
			var tipoAppalto=getValue("TORN_TIPGEN");
			var str = getValue("CAIS_TIPLAVG");
			var isfoglia = getValue("V_CAIS_TIT_ISFOGLIA");
			console.log(tipoAppalto);
			console.log(isfoglia);
			visualizzaNumeroClassifica(null, isfoglia, "CATG", null, false);
			categoriaNascosta = false;
		}
	</c:if>

</gene:javaScript>