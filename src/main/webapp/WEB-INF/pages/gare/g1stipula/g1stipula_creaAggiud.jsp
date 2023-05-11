<%/*
   * Created on 02-10-2018
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>


<gene:template file="scheda-template.jsp">
<gene:setString name="titoloMaschera" value="Crea nuova stipula"/>

	<gene:redefineInsert name="head">	
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvp.mod.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.cpvvsupp.mod.js"></script>
			<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/cpvvp/jquery.cpvvp.mod.css" >
			
	</gene:redefineInsert>
	
	
	<c:set var="modo" value="NUOVO" />
	<gene:redefineInsert name="documentiAzioni" />
	<gene:redefineInsert name="schedaConferma" >
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:conferma();" title="Avanti" tabindex="1502">
					Avanti &gt;
				</a>
			</td>
		</tr>
	</gene:redefineInsert>
	<gene:redefineInsert name="schedaAnnulla" >
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:annullaCreazione();" title="Annulla" tabindex="1504">
					Annulla
				</a>
			</td>
		</tr>	
	</gene:redefineInsert>
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="GARE" gestisciProtezioni="true" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInitGaraStipula" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreAffidamentoStipula">
		
		<input type="hidden" name="tipoGara" value="garaLottoUnico" />
		<input type="hidden" name="garaLottoUnico" value="garaLottoUnico" />
		
		<gene:campoScheda>
			<td colspan="2">
				<br><b>Compilare i dati dell'affidamento per il quale creare la stipula</b>
				<br><br>
			</td>
		</gene:campoScheda>
		<gene:campoScheda >
			<td colspan="2"><b>Dati generali</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="NUMAVCP" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" />
		<gene:campoScheda campo="CODCIG"/>
		<c:choose>
		<c:when test='${fn:startsWith(datiRiga.GARE_CODCIG,"#") or fn:startsWith(datiRiga.GARE_CODCIG,"$") or fn:startsWith(datiRiga.GARE_CODCIG,"NOCIG")}'>
			<gene:campoScheda campo="CODCIG_FIT" title="Codice CIG fittizio" campoFittizio="true" value="${datiRiga.GARE_CODCIG}" definizione="T10;;;;G1CODCIG" modificabile="false" visibile="false"/>
		</c:when>
		<c:otherwise>
			<gene:campoScheda campo="CODCIG_FIT" title="Codice CIG fittizio" campoFittizio="true" value="" definizione="T10;;;;G1CODCIG" modificabile="false" visibile="false" />
		</c:otherwise>
		</c:choose>
	
		<gene:campoScheda campo="DACQCIG"/>
		
		<gene:campoScheda campo="ESENTE_CIG" campoFittizio="true" computed="true" title="Esente CIG?" definizione="T10;;;SN" defaultValue="2" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoSenzaNull" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.CODCIG") }' modificabile="true"/>
		
		<gene:fnJavaScriptScheda funzione="gestioneEsenteCIG()" elencocampi="ESENTE_CIG" esegui="false" />
		
		<gene:campoScheda campo="TIPGEN" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" obbligatorio="true" />
		<gene:campoScheda campo="SETTORE" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" value="O" obbligatorio="true" />
		<gene:campoScheda campo="NOT_GAR" obbligatorio="true" />
		<gene:campoScheda campo="TIPGARG" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPGARstipule" obbligatorio="true" />
		

		<c:set var="inizializzazionePrerib" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1028","1","false")}'/>
		<gene:campoScheda campo="PRERIB" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" defaultValue='${inizializzazionePrerib }' visibile="false" />
		
		<c:if test='${modo eq "NUOVO" && (empty initCENINT || ! empty sessionScope.uffint)}'>
			<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.ValorizzaStazioneAppaltanteFunction" parametro=""/>
		</c:if>
		<gene:campoScheda title="Utente referente della gara" campo="CLIV2" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" defaultValue="${sessionScope.profiloUtente.id}" visibile="false"/>
		
		<gene:campoScheda >
			<td colspan="2"><b>Stazione appaltante e RUP</b></td>
		</gene:campoScheda>
		<gene:archivio titolo="Uffici intestatari"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CENINT"),"gene/uffint/uffint-lista-popup.jsp","")}'
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaUffint"),"gene/uffint/uffint-scheda-popup.jsp","")}'
			campi="UFFINT.CODEIN;UFFINT.NOMEIN;UFFINT.ISCUC"
			 chiave="TORN_CENINT"
			 functionId="skip|abilitazione:1_parentFormName:formUFFINTStipula"
			 formName="formUFFINTStipula">
			 <gene:campoScheda campo="CENINT" entita="TORN" title="Codice stazione appaltante aderente" where="GARE.CODGAR1 = TORN.CODGAR" value="${requestScope.initCENINT}" 
			 	modificabile='${empty initCENINT}' />
			 <gene:campoScheda campo="NOMEIN" title="Denominazione" where="GARE.CODGAR1 = TORN.CODGAR" campoFittizio="true" definizione="T254;0;;NOTE;NOMEIN" defaultValue="${requestScope.initNOMEIN}" 
				modificabile='${empty initNOMEIN}' />
			<gene:campoScheda campo="ISCUC" entita="UFFINT" from ="TORN" where="TORN.CENINT = UFFINT.CODEIN and TORN.CODGAR=GARE.CODGAR1" visibile="false" defaultValue="${requestScope.initISCUC}"/>
		</gene:archivio>
		
		<gene:archivio titolo="Tecnici"
			 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP"),"gene/tecni/tecni-lista-popup.jsp","")}'
			 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
			 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
			 campi="TECNI.CODTEC;TECNI.NOMTEC"
			 functionId="skip"
			 chiave="TORN_CODRUP"
			 inseribile="true">
				<gene:campoScheda campo="CODRUP" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" obbligatorio="true"/>
				<gene:campoScheda campo="NOMTEC" title="Nome" campoFittizio="true" definizione="T161;;;;NOMTEC1"
				modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP")}' />
		</gene:archivio>
		<gene:campoScheda campo="ACCQUA" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" defaultValue="2" obbligatorio="true" visibile="false" />
		<gene:campoScheda campo="ISADESIONE" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" defaultValue="2" obbligatorio="true" visibile="false" />
		<gene:campoScheda >
			<td colspan="2"><b>Importo a base di gara, IVA esclusa</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="IMPAPP" obbligatorio="true" />
		
		<jsp:include page="/WEB-INF/pages/gare/garcpv/codiciCPV-gara.jsp">
			<jsp:param name="datiModificabili" value="true"/>
		</jsp:include> 
		
		<gene:campoScheda >
			<td colspan="2"><b>Aggiudicazione</b></td>
		</gene:campoScheda>		
		<gene:archivio titolo="Ditte concorrenti"
			lista='gene/impr/impr-lista-popup.jsp?abilitaNuovo=1'
			scheda="gene/impr/impr-scheda.jsp"
			schedaPopUp="gene/impr/impr-scheda-popup.jsp"
			campi="IMPR.CODIMP;IMPR.NOMIMP"
			functionId="skip"
			chiave="GARE_DITTA"
			formName="formArchivioDitte" >
			<gene:campoScheda campo="DITTA" obbligatorio="true" />
			<gene:campoScheda campo="NOMIMA" />
		</gene:archivio>
		<gene:campoScheda campo="IAGGIU" obbligatorio="true" />
		<gene:campoScheda campo="DATTOA"/>
			
		<gene:campoScheda>
			<td class="comandi-dettaglio" colSpan="2">
		      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaCreazione();">&nbsp;&nbsp;&nbsp;&nbsp;
		      <INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:conferma();">&nbsp;
			</td>
		</gene:campoScheda>
	</gene:formScheda>
	</gene:redefineInsert>

<gene:javaScript>
	
		$("#GARE_CODCIG").css({'text-transform': 'uppercase' });
	
		$(function() {
	    $('#GARE_CODCIG').change(function() {
				if (!controllaCIG("GARE_CODCIG")) {
					alert("Codice CIG non valido")
					this.focus();
				}
			});
			
		$('#GARE_IMPAPP').change(function() {
				setValue("GARE_IAGGIU",getValue("GARE_IMPAPP"));
			});
			
		});	
	
		function annullaCreazione(){
			bloccaRichiesteServer();
			historyBack();
		}

		function indietro(){
			bloccaRichiesteServer();
			historyBack();
		}



		function conferma(){
			if (!controllaCIG("GARE_CODCIG")) {
				outMsg("Codice CIG non valido", "ERR");
				onOffMsg();
				return;
			}
			document.forms[0].activePage.value = 0;
			document.forms[0].jspPathTo.value="/WEB-INF/pages/gare/g1stipula/g1stipula-scheda.jsp";
			document.forms[0].action = document.forms[0].action + "&ncont=" + "1&forzaModo=1";
			schedaConferma();
		}
		
		function gestioneEsenteCIG() {
		<c:if test='${modo ne "VISUALIZZA"}'>
		var esenteCig = getValue("ESENTE_CIG");
		var codcig = getValue("GARE_CODCIG");
		if ("1" == esenteCig) {
			showObj("rowGARE_CODCIG", false);
			//setValue("GARE_CODCIG", "", false);
			if (getOriginalValue("CODCIG_FIT") == getValue("CODCIG_FIT")) {
				setValue("CODCIG_FIT", "", false);
			} else {
				setValue("CODCIG_FIT", getOriginalValue("CODCIG_FIT"), false);
			}
			<c:if test='${gene:checkProt(pageContext, "COLS.MAN.GARE.GARE.CODCIG")}'>
			if(getValue("CODCIG_FIT")==null || getValue("CODCIG_FIT")=="" )
				setValue("CODCIG_FIT", " ", false);
			</c:if>
			showObj("rowCODCIG_FIT", true);
			showObj("rowGARE_DACQCIG", false);
			showObj("rowTORN_NUMAVCP", false);
		} else {
			showObj("rowGARE_CODCIG", true);
			showObj("rowCODCIG_FIT", false);
			setValue("CODCIG_FIT", "", false);
			showObj("rowGARE_DACQCIG", true);
			showObj("rowTORN_NUMAVCP", true);
		}
	</c:if>
	}

	</gene:javaScript>
</gene:template>