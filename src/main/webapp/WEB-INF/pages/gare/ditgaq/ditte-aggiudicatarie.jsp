<%
/*
 * Created on: 19/12/2013
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<c:set var="ngara" value='${fn:substringBefore(param.chiave, ";")}' />
<c:set var="tmp" value='${fn:substringAfter(param.chiave, ";")}' />
<c:set var="modlicg" value='${fn:substringBefore(tmp, ":")}' />
<c:set var="impsic" value='${fn:substringAfter(tmp, ":")}' />
<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<c:choose>
			<c:when test="${!empty item[17] and item[17] ne ''}">
				<c:set var="campoRadice" value='DITGAQ.IAGGIUINI' />
			</c:when>
			<c:otherwise>
				<c:set var="campoRadice" value='DITGAQ.IAGGIU' />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test="${param.dettaglioIAGGIU eq 1}">
				<c:set var="elencoCampi" value='DITG.IMPOFF;GARE.IMPSIC' />
			</c:when>
			<c:when test="${param.dettaglioIAGGIU eq 2}">
				<c:set var="elencoCampi" value='DITG.IMPOFF;GARE.IMPSIC;DITG.IMPPERM' />
			</c:when>
			<c:when test="${param.dettaglioIAGGIU eq 3}">
				<c:set var="elencoCampi" value='DITG.IMPOFF;GARE.IMPSIC;DITG.IMPCANO' />
			</c:when>
			<c:when test="${param.dettaglioIAGGIU eq 4}">
				<c:set var="elencoCampi" value='DITG.IMPOFF;GARE.IMPSIC;DITG.IMPPERM;DITG.IMPCANO' />
			</c:when>
			<c:when test="${param.dettaglioIAGGIU eq 5}">
				<c:set var="elencoCampi" value='' />
			</c:when>
			<c:when test="${param.dettaglioIAGGIU eq 6}">
				<c:set var="elencoCampi" value='DITG.IMPOFF;DITG.IMPPERM' />
			</c:when>
			<c:when test="${param.dettaglioIAGGIU eq 7}">
				<c:set var="elencoCampi" value='DITG.IMPOFF;DITG.IMPCANO' />
			</c:when>
			<c:when test="${param.dettaglioIAGGIU eq 8}">
				<c:set var="elencoCampi" value='DITG.IMPOFF;DITG.IMPPERM;DITG.IMPCANO' />
			</c:when>
		</c:choose>
		
		<c:set var="ditta">${item[2]}</c:set>
		<c:choose>
			<c:when test="${param.isGaraLottiConOffertaUnica ne 'true'}">
				<c:set var="valoreGaraRagdet" value='${ngara }' />
			</c:when>
			<c:otherwise>
				<c:set var="valoreGaraRagdet" value='${param.codiceGara }' />
			</c:otherwise>
		</c:choose>
		
		<c:set var="result" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoriDettaglioRaggruppanentoFunction", pageContext, valoreGaraRagdet, ditta, param.contatore)}' />
		<c:set var="nomeVariabileRagdet">datiRagdet${param.contatore}</c:set>
		
		<gene:campoScheda campo="ID_${param.contatore}" entita="DITGAQ" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDDQ" value="${item[0]}" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="DITGAQ" campoFittizio="true" visibile="false" definizione="T20;0;;;G1NGARADQ" value="${item[1]}" />
			<gene:archivio titolo="Ditte"
			lista=''
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
			campi="IMPR.CODIMP;IMPR.NOMIMP"
			functionId="skip"
			chiave="DITGAQ_DITTAO_${param.contatore}"
			inseribile="false"
			formName="formArchivioDitte${param.contatore}" >
			<gene:campoScheda campo="DITTAO_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="T10;0;;;G1DITTAODQ" value="${item[2]}" />
			<gene:campoScheda campo="NOMIMP_${param.contatore}" entita="IMPR" campoFittizio="true"  modificabile="false" visibile="true" definizione="T60;0;;;G_NOMIMP" value="${item[3]}" />
		</gene:archivio>
		<gene:campoScheda campo="PUNTOT_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false"  visibile="${modlicg eq '6' }" definizione="F13.9;0;;;G1PUNTOTDQ" value="${item[5]}"/>
		<gene:campoScheda campo="RIBAGG_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false"  visibile="true" definizione="F13.9;0;;PRC;G1RIBAGGDQ" value="${item[4]}"/>
		<c:choose>
			<c:when test="${!empty item[17] and item[17] ne '' }">
				<gene:campoScheda campo="IAGGIU_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1IAGGIUDQ" value="${item[6]}" tooltip="Importo di aggiudicazione"/>
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="IAGGIU_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1IAGGIUDQ" value="${item[6]}" title='${gene:callFunction4("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseDynamicSectionsFunction",pageContext,campoRadice,elencoCampi,param.contatore)}' tooltip="Importo di aggiudicazione"/>
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="RIBAGGINI_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="F13.9;0;;PRC;G1RIBINIDQ" value="${item[16]}" visibile="${!empty item[16] and item[16] ne ''}"/>
		<c:choose>
			<c:when test="${!empty item[17] and item[17] ne '' }">
				<gene:campoScheda campo="IAGGIUINI_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1IAGINIDQ" value="${item[17]}" visibile="${!empty item[17] and item[17] ne ''}" title='${gene:callFunction4("it.eldasoft.gene.tags.functions.GetTitleWithExpandCollapseDynamicSectionsFunction",pageContext,campoRadice,elencoCampi,param.contatore)}' tooltip="Importo di aggiudicazione precedente a rettifica"/>
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="IAGGIUINI_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1IAGINIDQ" value="${item[17]}" visibile="false"/>
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="IMPOFF_${param.contatore}" entita="DITG" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;IMPOFF" value="${item[7]}" visibile="${param.sicinc eq '2' || !empty param.ultdetlic }" title="di cui importo offerto"/>
		<gene:campoScheda campo="IMPSIC_${param.contatore}" entita="GARE" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1IMPSIC" value="${impsic}"  visibile="${param.sicinc eq '2' }"/>
		<gene:campoScheda campo="IMPPERM_${param.contatore}" entita="DITG" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1IMPPERM" value="${item[8]}" visibile="${ param.ultdetlic eq '1' or param.ultdetlic eq '3'}" title="di cui importo per permuta"/>
		<gene:campoScheda campo="IMPCANO_${param.contatore}" entita="DITG" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1IMPCANO" value="${item[9]}" visibile="${ param.ultdetlic eq '2' or param.ultdetlic eq '3' }" title="di cui importo per canone assistenza"/>
		<gene:campoScheda campo="RICSUB_${param.contatore}" entita="DITG" campoFittizio="true"  definizione="T2;0;;SN;G1RICSUB" value="${item[10]}" />
		<gene:campoScheda campo="RIDISO_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T2;0;;SN;G1RIDISODQ" value="${item[11]}" />
		<gene:campoScheda campo="IMPGAR_${param.contatore}" entita="DITGAQ" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPGARDQ" value="${item[12]}" >
			<gene:calcoloCampoScheda funzione='calcolaIMPGAR(${param.contatore})' elencocampi='DITGAQ_RIDISO_${param.contatore}'/>
		</gene:campoScheda>
				
		<c:if test='${not empty requestScope[nomeVariabileRagdet] and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.dettaglioDitteConsorziate") && modo eq "VISUALIZZA"}'>
			<gene:campoScheda>
				<td colspan="2"><span id="LinkVisualizzaDettaglio${param.contatore }" style="display: none; float:left" ><a id="aLinkVisualizzaDettaglio${param.contatore }" href="javascript:showDettRaggruppamento(${param.contatore });" class="link-generico"><span id="testoLinkVisualizzaDettaglio${param.contatore }">Visualizza dettaglio consorziate esecutrici</span></a></span></td>
			</gene:campoScheda>
			<gene:campoScheda campoFittizio="true" nome="dettaglioRaggruppamento${param.contatore }" >
				<td class="etichetta-dato">Consorziate esecutrici</td>
				<td>
					<table id="tabellaDettaglio" class="griglia" style="width: 99%; margin-left: 1%;">
					<tr>
						<td colspan="2" class="etichetta-dato" style="TEXT-ALIGN: center;BORDER-LEFT: #A0AABA 1px solid; width: 10%">Codice</td>
						<td colspan="2" class="etichetta-dato" style="TEXT-ALIGN: center;BORDER-LEFT: #A0AABA 1px solid; width: 90%">Ragione sociale</td>
					</tr>
			</gene:campoScheda>
			<c:forEach items="${requestScope[nomeVariabileRagdet]}" var="componenteRagg" varStatus="stato">
				<gene:campoScheda/>
				<gene:campoScheda addTr="false" campo="CODDIC_${stato.index}" entita="RAGDET" campoFittizio="true" hideTitle="true" definizione="T10;1" value="${componenteRagg[1]}" href='javascript:archivioImpresa("${componenteRagg[1]}");' modificabile="false"/> 
				<gene:campoScheda addTr="false" campo="NOMEST_${stato.index}" entita="IMPR" hideTitle="true" campoFittizio="true"   definizione="T2000;0;;;" value="${componenteRagg[4]}"  href='javascript:archivioImpresa("${componenteRagg[1]}");' modificabile="false"/>
			</c:forEach>
			<gene:campoScheda addTr="false">
					</table>
				</td>
			</gene:campoScheda>
			<gene:fnJavaScriptScheda funzione="gestioneDettaglioConsorzio(${param.contatore})" elencocampi="" esegui="true" />
		</c:if>
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.VerificaDocumenti") && modo ne "MODIFICA"}' >
			<gene:campoScheda addTr="false">
				<tr id="rowLinkVerificaDoc${param.contatore}">
					<td colspan="2">
						<img width="16" height="16" title="Verifica documenti richiesti" alt="Verifica documenti richiesti" src="${pageContext.request.contextPath}/img/documentazione.png"/>
						<a href="javascript:verificaDocumentiRichiestiDaAgg('${item[2]}');" title="Verifica documenti richiesti" class="link-generico">
							Verifica documenti richiesti
						</a>
					</td>
				</tr>
			</gene:campoScheda>
		</c:if>
		<c:if test='${param.dettagliOffPrezziVisibile && modo ne "MODIFICA"}' >
			<gene:campoScheda addTr="false">
				<tr id="rowLinkDettagliOffertaPrezzi${param.contatore}">
					<td colspan="2">
						<img width="16" height="16" title="Dettaglio offerta prezzi" alt="Dettaglio offerta prezzi" src="${pageContext.request.contextPath}/img/offertaprezzi.png"/>
						<a href="javascript:DettaglioOffertaPrezzi('${item[2]}');" title="Dettaglio offerta prezzi" class="link-generico">
							Dettaglio offerta prezzi
						</a>
					</td>
				</tr>
			</gene:campoScheda>
		</c:if>
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="DITGAQ" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDDQ" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="DITGAQ" campoFittizio="true" visibile="false" definizione="T20;0;;;G1NGARADQ" value="${ngara}"/>
		<gene:campoScheda campo="DITTAO_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="T10;0;;;G1DITTAODQ" />
		<gene:campoScheda campo="NOMIMP_${param.contatore}" entita="IMPR" campoFittizio="true"  modificabile="false" definizione="T60;0;;;G_NOMIMP"  />
		<gene:campoScheda campo="PUNTOT_${param.contatore}" entita="DITGAQ" campoFittizio="true"  visibile="${modlicg eq '6' }"  definizione="F13.9;0;;;G1PUNTOTDQ" />
		<gene:campoScheda campo="RIBAGG_${param.contatore}" entita="DITGAQ" campoFittizio="true"  definizione="F13.9;0;;;G1RIBAGGDQ" />
		<gene:campoScheda campo="IAGGIU_${param.contatore}" entita="DITGAQ" campoFittizio="true" definizione="F15;0;;MONEY;G1IAGGIUDQ" />				
		<gene:campoScheda campo="RIBAGGINI_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="F13.9;0;;PRC;G1RIBINIDQ"  visibile="false" />
		<gene:campoScheda campo="IAGGIUINI_${param.contatore}" entita="DITGAQ" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1IAGINIDQ"  visibile="false"/>
		<gene:campoScheda campo="IMPOFF_${param.contatore}" entita="DITG" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;IMPOFF" title="di cui importo offerto" />
		<gene:campoScheda campo="IMPSIC_${param.contatore}" entita="GARE" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1IMPSIC"  />
		<gene:campoScheda campo="IMPPERM_${param.contatore}" entita="DITG" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1IMPPERM" title="di cui importo per permuta" />
		<gene:campoScheda campo="IMPCANO_${param.contatore}" entita="DITG" campoFittizio="true"  modificabile="false" definizione="F15;0;;MONEY;G1IMPCANO" title="di cui importo per canone assistenza" />
		<gene:campoScheda campo="RICSUB_${param.contatore}" entita="DITG" campoFittizio="true"  definizione="T2;0;;SN;G1RICSUB"  />
		<gene:campoScheda campo="RIDISO_${param.contatore}" entita="DITGAQ" campoFittizio="true"   definizione="T2;0;;SN;G1RIDISODQ" />
		<gene:campoScheda campo="IMPGAR_${param.contatore}" entita="DITGAQ" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPGARDQ" />
		
	</c:otherwise>
</c:choose>

<gene:javaScript>
	
	$(document).ready(function (){
	
		if(typeof(showDettG1IAGGIUDQ_${param.contatore }) == "function" || typeof(showDettG1IAGINIDQ_${param.contatore }) == "function"){
			
			function gestioneIMPPER(){
				var ultdelic = getValue("GARE1_ULTDETLIC");
		 		if(ultdelic!= null && ultdelic!=""){
			 		if ($("#DITG_IMPPERM_${param.contatore }view").is(":visible")) {
			 			var importo = $("#DITG_IMPPERM_${param.contatore}").val();
			 			if(importo!=null){
			 				importo = importo * (-1);
			 				importo = formatNumber(importo, 20.2);
			 			}
			 			<c:choose>
			 				<c:when test="${modo eq 'MODIFICA' }">
			 					$("#DITG_IMPPERM_${param.contatore}edit").val(importo);		
			 				</c:when>
			 				<c:otherwise>
			 					if(importo!=null){
				 					importo =formatCurrency(importo,',','.');
				 					var tmp = $("#DITG_IMPPERM_${param.contatore}view");
				 					tmp.children("span").text("");
				 					tmp.children("span").append(importo);
			 					}
			 				</c:otherwise>
			 			</c:choose>
			 			
			 		}
		 		}
			}
			//Si forza la visualizzazione di un valore negativo in IMPPERM
			
			<c:choose>
				<c:when test="${!empty item[17] and item[17] ne '' }">
					var showDettG1IAGINIDQ_Default_${param.contatore } = showDettG1IAGINIDQ_${param.contatore };
	 		 		
				 	function showDettG1IAGINIDQ_Custom_${param.contatore }(){
				 		showDettG1IAGINIDQ_Default_${param.contatore }();
				 		gestioneIMPPER();
				 	}
				 	showDettG1IAGINIDQ_${param.contatore } =   showDettG1IAGINIDQ_Custom_${param.contatore };
				</c:when>
				<c:otherwise>
					var showDettG1IAGGIUDQ_Default_${param.contatore } = showDettG1IAGGIUDQ_${param.contatore };
	 		
				 	function showDettG1IAGGIUDQ_Custom_${param.contatore }(){
				 		showDettG1IAGGIUDQ_Default_${param.contatore }();
				 		gestioneIMPPER();
				 	}
				 	showDettG1IAGGIUDQ_${param.contatore } =   showDettG1IAGGIUDQ_Custom_${param.contatore };
				</c:otherwise>
			</c:choose>
			
			
	 	}
	
	});
	
</gene:javaScript>



