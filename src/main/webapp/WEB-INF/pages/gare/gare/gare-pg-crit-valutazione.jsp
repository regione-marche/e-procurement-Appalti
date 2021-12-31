<%
/*
 * Created on: 25/05/2009
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />

<c:set var="ngara" value='${gene:getValCampo(key,"NGARA")}' />

<c:choose>
	<c:when test="${not empty param.tipoCriterio}">
		<c:set var="tipoCriterio" value="${param.tipoCriterio}"/>
	</c:when>
	<c:otherwise>
		<c:set var="tipoCriterio" value="${tipoCriterio}"/>
	</c:otherwise>
</c:choose>

<c:set var="offtel" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetOFFTELFunction",  pageContext,codiceGara)}' />
<c:set var="costofisso" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCOSTOFISSOFunction",  pageContext,ngara)}' />




<c:choose>
	<c:when test="${lottoOffertaUnica eq true }">
		<c:set var="codiceLotto" value="${codiceGara}"/>
	</c:when>
	<c:otherwise>
		<c:set var="codiceLotto" value="${ngara}"/>
	</c:otherwise>
</c:choose>

<c:set var="initPaginaCrit" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.InizializzazionePaginaCriteriFunction", pageContext)}' scope="request"/>

<c:choose>
	<c:when test='${isProceduraTelematica}'>
		<c:set var="itergaMacro" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetITERGAMacroFunction", pageContext, key)}'/>
		<c:set var="bloccoPubblicazionePortaleBando11" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codiceGara,"BANDO11","false")}' />
		<c:set var="bloccoPubblicazionePortaleBando13" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codiceGara,"BANDO13","false")}' />
		<c:set var="bloccoPubblicazionePortaleEsito" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codiceLotto,"ESITO","false")}' />
		<c:set var="faseGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetFaseGaraFunction", pageContext, codiceLotto)}'/>
		<c:set var="condizioniBloccoTelematica" value='${(bloccoPubblicazionePortaleBando13 eq "TRUE" || bloccoPubblicazionePortaleEsito eq "TRUE" || (bloccoPubblicazionePortaleBando11 eq "TRUE" && iterga == 1))
					 && (tipoCriterio eq 1 || tipoCriterio eq 2 || (tipoCriterio eq 3 && faseGara >= "5"))}' />
		<c:if test="${tipoCriterio eq 1 }">
			<c:set var="where" value="NGARA='${ngara }'"/>
			<c:set var="sezionitec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "SEZIONITEC","GARE1", where)}'/>
		</c:if>			 
	</c:when>
	<c:otherwise>
		<c:set var="BloccoAggiudicazione" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlloAggiudicazioneDefinitivaFunction", pageContext, ngara,codiceGara)}' />
		<c:set var="BloccoPunteggiDitte" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoPunteggiDitteFunction", pageContext, ngara,codiceGara)}' />
		<c:set var="condizioniBloccoNonTelematica" value='${BloccoAggiudicazione eq "VERO" or BloccoPunteggiDitte eq "VERO" or faseGara > 1}' />
	</c:otherwise>
</c:choose>


<c:set var="criterioModificato" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlloPunteggiCriteriFunction", pageContext, ngara, costofisso,sezionitec)}' />

<c:choose>
	<c:when test="${empty punteggioTecnico }">
		<c:set var="msgPunteggioTecnico" value="non definito"/>
	</c:when>
	<c:otherwise>
		<fmt:formatNumber type="number" value="${punteggioTecnico}" var="punteggioTec" />
		<c:set var="msgPunteggioTecnico" value="${punteggioTec}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${empty punteggioEconomico}">
		<c:set var="msgPunteggioEconomico" value="non definito"/>
	</c:when>
	<c:otherwise>
		<fmt:formatNumber type="number" value="${punteggioEconomico}" var="punteggioEco" />
		<c:set var="msgPunteggioEconomico" value="${punteggioEco}"/>
	</c:otherwise>
</c:choose>



<c:choose>
	<c:when test="${empty punteggioTotale }">
		<c:set var="msgPunteggioTotale" value="non definito"/>
	</c:when>
	<c:otherwise>
		<fmt:formatNumber type="number" value="${punteggioTotale}" var="punteggioTot" />
		<c:set var="msgPunteggioTotale" value="${punteggioTot}"/>
	</c:otherwise>
</c:choose>

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<table class="arealayout">
				<tr>
					<input type="radio" value="1" name="filtroPaginaCriteri" id="tecnici" <c:if test='${tipoCriterio eq 1}'>checked="checked"</c:if> onclick="javascript:cambiaPaginaCriteri(1);" />
					 Criteri di valutazione busta tecnica
					 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					 <c:if test="${costofisso ne '1'}">
						 <input type="radio" value="2" name="filtroPaginaCriteri" id="economici" <c:if test='${tipoCriterio eq 2}'>checked="checked"</c:if> onclick="javascript:cambiaPaginaCriteri(2);" />
						 Criteri di valutazione busta economica
						 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					 </c:if>
					  <input type="radio" value="2" name="filtroPaginaCriteri" id="totali" <c:if test='${tipoCriterio eq 3}'>checked="checked"</c:if> onclick="javascript:cambiaPaginaCriteri(3);" />
					 Punteggio totale, soglie minime e riparametrazione
					 <br>
					 <br>
				</tr>
				<c:if test='${tipoCriterio eq 1}'>
				<tr>
					<td width="210px"><b>Punteggio tecnico massimo:</b></td>
					<td width="100px" align="left">&nbsp;<span id="punteggioTec">${msgPunteggioTecnico}</span></td>
					<c:if test='${tipoCriterio eq 1}'>
						<td width="120px">${gene:if(!empty SogliaMinTec, "<b>Soglia minima:</b>", "")}</td>
						<fmt:formatNumber type="number" value="${SogliaMinTec}" var="SogliaMinTecFormat" />
						<td align="left">&nbsp;<span id="SogliaMinTec">${SogliaMinTecFormat}</span></td>
					</c:if>
				</tr>
				<c:if test='${sezionitec eq 1}'>
						<c:choose>
							<c:when test="${empty punteggioTecnicoQualitativo }">
								<c:set var="msgPunteggioTecnicoQualitativo" value="non definito"/>
							</c:when>
							<c:otherwise>
								<fmt:formatNumber type="number" value="${punteggioTecnicoQualitativo}" var="punteggioTecQualitativo" />
								<c:set var="msgPunteggioTecnicoQualitativo" value="${punteggioTecQualitativo}"/>
							</c:otherwise>
						</c:choose>
						
						<c:choose>
							<c:when test="${empty punteggioTecnicoQuantitativo }">
								<c:set var="msgPunteggioTecnicoQuantitativo" value="non definito"/>
							</c:when>
							<c:otherwise>
								<fmt:formatNumber type="number" value="${punteggioTecnicoQuantitativo}" var="punteggioTecQuantitativo" />
								<c:set var="msgPunteggioTecnicoQuantitativo" value="${punteggioTecQuantitativo}"/>
							</c:otherwise>
						</c:choose>
						<tr>
						<td width="210px"><b>di cui qualitativo:</b></td>
						<td width="100px" align="left">&nbsp;<span id="punteggioTecQualitativo">${msgPunteggioTecnicoQualitativo}</span></td>
						</tr>
						<tr>
						<td width="210px"><b>di cui quantitativo:</b></td>
						<td width="100px" align="left">&nbsp;<span id="punteggioTecQuantitativo">${msgPunteggioTecnicoQuantitativo}</span></td>
						</tr>
					</c:if>
				</c:if>
				<c:if test='${tipoCriterio eq 2}'>
				<tr>
					<td width="210px"><b>Punteggio economico massimo:</b></td>
					<td width="100px" align="left">&nbsp;<span id="punteggioEco">${msgPunteggioEconomico}</span></td>
					<c:if test='${tipoCriterio eq 2}'>
						<td width="120px">${gene:if(!empty SogliaMinEco, "<b>Soglia minima:</b>", "")}</td>
						<fmt:formatNumber type="number" value="${SogliaMinEco}" var="SogliaMinEcoFormat" />
						<td align="left">&nbsp;<span id="SogliaMinEco">${SogliaMinEcoFormat}</span></td>
					</c:if>
				</tr>
				</c:if>
								
				<c:if test="${condizioniBloccoNonTelematica }">
					
					<tr>
						<td colspan="4">
							<br><b>ATTENZIONE:</b>&nbsp;
							
							<c:choose>
								<c:when test='${BloccoAggiudicazione eq "VERO" or faseGara > 1}'>
									I dati sono in sola consultazione perch&egrave; la gara &egrave; aggiudicata o in fase di espletamento
								</c:when>
								<c:when test='${BloccoPunteggiDitte eq "VERO" }'>
									I dati sono in sola consultazione perch&egrave; risultano gi&agrave; assegnati i punteggi alle ditte 
								</c:when>
							</c:choose>
							<br>
						</td>
					</tr>
					
				</c:if>
				<c:if test="${condizioniBloccoTelematica }">
					
					<tr>
						<td colspan="4">
							<br><b>ATTENZIONE:</b>&nbsp;
							<c:choose>
								<c:when test="${tipoCriterio eq 1 or tipoCriterio eq 2 }">
								I dati sono in sola consultazione perché la gara è pubblicata su portale Appalti
								</c:when>
								<c:otherwise>
									<c:set var="msgBlocco" value="I dati sono in sola consultazione perché la gara risulta aggiudicata o in fase di espletamento. Se la gara è in fase di espletamento, 
									è possibile ripristinare la modifica di tali dati attivando prima la funzione 'Annulla apertura offerte' disponibile al punto ordinante"/>
									<c:if test="${lottoOffertaUnica eq true }">
										<c:set var="bustalotti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetBustalottiFunction", pageContext, key)}'/>
									</c:if>
									<c:choose>
										<c:when test="${bustalotti eq '1' }">
											<c:set var="msgBlocco" value="${msgBlocco } nella pagina 'Apertura offerte e calcolo aggiudicazione'"/>
										</c:when>
										<c:otherwise>
											<c:set var="msgBlocco" value="${msgBlocco } nelle fasi di gara 'Valutazione tecnica' o 'Apertura offerte economiche'"/>
										</c:otherwise>
									</c:choose>
									${msgBlocco }
									<br>
								</c:otherwise>
							</c:choose>
							
							<br>
						</td>
					</tr>
					
				</c:if>
				
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<c:choose>
				<c:when test="${tipoCriterio eq 2 or tipoCriterio eq 1}">
					
					<gene:formLista entita="GOEV" where='GOEV.NGARA = #GARE.NGARA# and GOEV.TIPPAR=${tipoCriterio}' tableclass="datilista" sortColumn="4;6;7;3;"
						gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGOEV" pagesize="25" >
					
						<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
							<jsp:param name="entita" value="V_GARE_TORN"/>
							<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
							<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
						</jsp:include>
						
						<c:if test='${condizioniBloccoTelematica or condizioniBloccoNonTelematica}'>
							<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
							<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
						</c:if>
														
						<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
							<c:if test="${currentRow >= 0 and !(datiRiga.GOEV_LIVPAR eq 2)}">
								<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
									<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.GOEV-scheda")}' >
										<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza criterio di valutazione"/>
									</c:if>
									<c:if test='${(autorizzatoModifiche ne "2") && gene:checkProt(pageContext, "MASC.VIS.GARE.GOEV-scheda") && gene:checkProtFunz(pageContext, "MOD","MOD") && !condizioniBloccoTelematica &&!condizioniBloccoNonTelematica}' >
										<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica criterio di valutazione" />
									</c:if>
									<c:if test='${(autorizzatoModifiche ne "2") && gene:checkProtFunz(pageContext, "DEL","DEL") && !condizioniBloccoTelematica &&!condizioniBloccoNonTelematica}' >
										<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina criterio di valutazione" href="eliminaCriterio('${datiRiga.GOEV_MAXPUN}','${datiRiga.GOEV_TIPPAR}');" />
									</c:if>
								</gene:PopUp>
		
							</c:if>
							<c:if test='${currentRow >= 0 and gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") }'>
								<input type="checkbox" name="keys" value="${chiaveRiga}"  />
							</c:if>
						</gene:campoLista>
						<gene:campoLista campo="NGARA" visibile="false" />
						<gene:campoLista campo="NECVAN" visibile="false" />
						<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.GOEV-scheda") and !(datiRiga.GOEV_LIVPAR eq 2)}'/>				
						<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
						<gene:campoLista campo="NORPAR"  title="N." value="${gene:if(datiRiga.GOEV_LIVPAR eq 2, '', datiRiga.GOEV_NORPAR)}"  width="10" ordinabile="false"/>
						<gene:campoLista campo="TIPPAR"  value="${gene:if(datiRiga.GOEV_LIVPAR eq 2, '', datiRiga.GOEV_TIPPAR)}" width="130" ordinabile="false"  visibile="false"/>
						<gene:campoLista campo="NECVAN1"  headerClass="sortable" visibile="false"/>
						<gene:campoLista campo="NORPAR1" title="N.sub" value="${gene:if(datiRiga.GOEV_LIVPAR eq 2, datiRiga.GOEV_NORPAR1,'' )}"  width="10" ordinabile="false"/>
						<gene:campoLista campo="DESPAR"  ordinabile="false" href="${gene:if(visualizzaLink, link, '')}"/>
						<gene:campoLista campo="MAXPUN"  ordinabile="false" width="50"/>
						<gene:campoLista campo="SEZTEC"  ordinabile="false" width="50" visibile="false"/>
						<c:if test='${tipoCriterio eq 1 and sezionitec eq "1"}'>
							<gene:campoLista campo="SEZTEC_FIT"  ordinabile="false" width="50" campoFittizio="true" definizione="T100;0;A1168;;G1_SEZTEC" value="${gene:if(datiRiga.GOEV_LIVPAR ne 2, datiRiga.GOEV_SEZTEC,'' )}"/>
						</c:if>
						<gene:campoLista campo="LIVPAR" visibile="false" />
						<gene:campoLista campo="MAXPUNFIT" campoFittizio = "true" value = "${datiRiga.GOEV_MAXPUN}" definizione = "F24.5" edit="true" visibile="false"/>
						<gene:campoLista campo="TIPPARFIT" campoFittizio = "true" value = "${datiRiga.GOEV_TIPPAR}" definizione = "N7" edit="true" visibile="false" />
						<gene:campoLista campo="LIVPARFIT" campoFittizio = "true" value = "${datiRiga.GOEV_LIVPAR}" definizione = "N7" edit="true" visibile="false" />
						<gene:campoLista title="&nbsp;" width="20">
							<c:if test="${datiRiga.GOEV_LIVPAR ne 3 && gene:checkProt(pageContext, 'MASC.VIS.GARE.G1CRIDEF-scheda')}">
								<a href="javascript:dettaglioModalitaAssegnazionePunteggio('${datiRiga.G1CRIDEF_ID}','${datiRiga.GOEV_NGARA}',${datiRiga.GOEV_NECVAN},'${sezionitec}');" title="Dettaglio assegnazione punteggio" >
									<img width="16" height="16" title="Dettaglio assegnazione punteggio" alt="Dettaglio assegnazione punteggio" src="${pageContext.request.contextPath}/img/dettaglio-criteri.png"/>
								</a>
							</c:if>
						</gene:campoLista>
						<gene:campoLista campo="FORMATO" title="Formato" entita="G1CRIDEF" where="G1CRIDEF.NGARA = GOEV.NGARA AND G1CRIDEF.NECVAN = GOEV.NECVAN" ordinabile="false" width="100"/>
						<gene:campoLista campo="MODPUNTI" title="Assegnazione punteggio" entita="G1CRIDEF" where="G1CRIDEF.NGARA = GOEV.NGARA AND G1CRIDEF.NECVAN = GOEV.NECVAN" ordinabile="false" width="100"/>
						<gene:campoLista campo="MODMANU" entita="G1CRIDEF" where="G1CRIDEF.NGARA = GOEV.NGARA AND G1CRIDEF.NECVAN = GOEV.NECVAN" visibile="false"/>
						<gene:campoLista campo="FORMULA" entita="G1CRIDEF" where="G1CRIDEF.NGARA = GOEV.NGARA AND G1CRIDEF.NECVAN = GOEV.NECVAN" visibile="false"/>
						<gene:campoLista campo="ID" entita="G1CRIDEF" where="G1CRIDEF.NGARA = GOEV.NGARA AND G1CRIDEF.NECVAN = GOEV.NECVAN" visibile="false"/>
						<gene:campoLista campo="REGOLA" title="Modalità" campoFittizio="true" definizione="T20" width="180"  gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoRegolaValutazione"/>
						<input type="hidden" name="tipoCriterio" value="${tipoCriterio }"/>
						<input type="hidden" name="sezionitec" value="${sezionitec }"/>
					</gene:formLista >
					
				</c:when>
				<c:otherwise>
					<gene:formScheda entita="GARE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGOEV">
						
						<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
							<jsp:param name="entita" value="V_GARE_TORN"/>
							<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
							<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
						</jsp:include>
						
						<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
						<c:if test="${condizioniBloccoNonTelematica || condizioniBloccoTelematica}">
							<gene:redefineInsert name="schedaModifica"/>
						</c:if>
						<gene:campoScheda>
							<td colspan="2"><b>Punteggi totali</b></td>
						</gene:campoScheda>
						<gene:campoScheda campo="NGARA"  visibile="false" />
						<gene:campoScheda campo="CODGAR1"  visibile="false" />
						<gene:campoScheda campo="MAXPUNTEC" title="Punteggio tecnico massimo" campoFittizio = "true" value = "${msgPunteggioTecnico}" definizione = "T15"  modificabile="false"/>
						<gene:campoScheda campo="MAXPUNECO" title="Punteggio economico massimo" campoFittizio = "true" value = "${msgPunteggioEconomico}" definizione = "T15" modificabile="false" visibile="${costofisso ne '1'}" />
						<gene:campoScheda campo="PUNTEGGITOT" title="Punteggio totale" campoFittizio = "true" value = "${msgPunteggioTotale}" definizione = "T15" modificabile="false"  />
						<gene:campoScheda campo="MINTEC"  entita="GARE1" where="GARE.NGARA = GARE1.NGARA" title="Soglia minima punteggio tecnico"/>
						<gene:campoScheda campo="MINECO"  entita="GARE1" where="GARE.NGARA = GARE1.NGARA" title="Soglia minima punteggio economico" visibile="${costofisso ne '1'}"/>
						<gene:campoScheda campo="NGARA"  entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false"/>
						<input type="hidden" name='paginaCriterio' value="3"/>
						<gene:campoScheda campo="MAXTEC" campoFittizio="true" visibile="false" definizione="F8.3" modificabile="false" value="${punteggioTecnico }"/>
						<gene:campoScheda campo="MAXECO" campoFittizio="true" visibile="false" definizione="F8.3" modificabile="false" value="${punteggioEconomico }"/>

						<gene:campoScheda>
							<td colspan="2"><b>Riparametrazione</b></td>
						</gene:campoScheda>
						<gene:campoScheda campo="RIPTEC"  entita="GARE1" where="GARE.NGARA = GARE1.NGARA" obbligatorio="true"/>
						<gene:campoScheda campo="RIPCRITEC"  entita="GARE1" where="GARE.NGARA = GARE1.NGARA" obbligatorio="true"/>
						<gene:campoScheda campo="RIPECO"  entita="GARE1" where="GARE.NGARA = GARE1.NGARA" obbligatorio="true" visibile="${costofisso ne '1'}"/>
						<gene:campoScheda campo="RIPCRIECO"  entita="GARE1" where="GARE.NGARA = GARE1.NGARA" obbligatorio="true" visibile="${costofisso ne '1'}"/>
						
						<gene:fnJavaScriptScheda funzione="gestioneRIPCRITEC( '#GARE1_RIPTEC#')" elencocampi="GARE1_RIPTEC" esegui="true"/>
						<gene:fnJavaScriptScheda funzione="gestioneRIPCRIECO( '#GARE1_RIPECO#')" elencocampi="GARE1_RIPECO" esegui="true"/>
					</gene:formScheda>
				</c:otherwise>
			</c:choose>
					</td>
	</tr>

	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${autorizzatoModifiche ne "2" and tipoCriterio ne 3 and !condizioniBloccoTelematica and !condizioniBloccoNonTelematica and !(modo eq "MODIFICA" or modo eq "NUOVO")}'>
			<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE-scheda.CRITERI.ImportaDaModello")}'>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:importaModelli();" title="Importa criteri da modello" tabindex="1500">
							Importa criteri da modello
						</a>
					</td>
				</tr>
			</c:if>
			<c:if test='${garaLottoUnico eq "false" and tipoCriterio ne 3 and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE-scheda.CRITERI.CopiaCriteri")}'>
				<tr>
					<td class="vocemenulaterale" >
						<c:if test='${isNavigazioneDisattiva ne "1"}'>
							<a href="javascript:apriPopupCopiaCriteri()" title="Copia criteri negli altri lotti" tabindex="1505">
						</c:if>
							Copia criteri negli altri lotti
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
					</td>
				</tr>
			</c:if>
		</c:if>
	</gene:redefineInsert>
	
	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<c:choose>
				<c:when test="${tipoCriterio eq 2 or tipoCriterio eq 1}">
					<gene:insert name="pulsanteListaInserisci">
						<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO") && !condizioniBloccoTelematica && !condizioniBloccoNonTelematica}'>
							<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaPageNuovo")}' title='${gene:resource("label.tags.template.lista.listaPageNuovo")}' onclick="javascript:listaNuovo()">
						</c:if>
					</gene:insert>
					<gene:insert name="pulsanteListaEliminaSelezione">
						<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && !condizioniBloccoTelematica && !condizioniBloccoNonTelematica}'>
							<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
						</c:if>
					</gene:insert>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
						<gene:insert name="pulsanteSalva">
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:ConfermaModifica();">
						</gene:insert>
						<gene:insert name="pulsanteAnnulla">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:ConfermaAnnulla();">
						</gene:insert>
					</c:when>
					<c:otherwise>
						<gene:insert name="pulsanteModifica">
							<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") && !condizioniBloccoNonTelematica && !condizioniBloccoTelematica }'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
							</c:if>
						</gene:insert>
					</c:otherwise>
					</c:choose>
						
						</c:otherwise>
					</c:choose>
		
			&nbsp;
		</td>
	</tr>
	
	<gene:javaScript>
		function  apriPopupCopiaCriteri(){
			var ngara = "${ngara }";
			var codiceGara = "${codiceGara }";
			var href = "href=gare/gare/gare-popup-copia-criteriValutazione.jsp?lottoSorgente=" + ngara + "&codiceGara=" + codiceGara;
			openPopUpCustom(href, "copiaCriteri", 700, 450, "yes", "yes");
		}
		
		function importaModelli(){
			var ngara = "${ngara }";
			var offtel = "${offtel}";
			var codgar = "${codiceGara}";
			var href = "href=gare/goevmod/goevmod-popup-importa-modelli.jsp?ngara=" + ngara + "&offtel=" + offtel + "&codgar=" + codgar;
			openPopUpCustom(href, "importaModello", 700, 450, "yes", "yes");
		}	
				
		function eliminaCriterio(punteggio,tipoCriterio){
			var punteggioTecnicoMax="${punteggioTecnico}";
	 		var punteggioEconomicoMax = "${punteggioEconomico }";
	 		
	 		if(punteggioTecnicoMax == null || punteggioTecnicoMax == "")
	 			punteggioTecnicoMax = 0;
	 		else
	 			punteggioTecnicoMax = parseFloat(punteggioTecnicoMax);
	 				
	 		if(punteggioEconomicoMax == null || punteggioEconomicoMax == "")
	 			punteggioEconomicoMax = 0;
	 		else
	 			punteggioEconomicoMax = parseFloat(punteggioEconomicoMax);
	 		
			if(controlloSoglie(punteggio,tipoCriterio,punteggioTecnicoMax, punteggioEconomicoMax)==1)
				listaElimina();
		}
		
		
		var listaEliminaSelezioneDefault = listaEliminaSelezione;
		var listaEliminaSelezione = listaEliminaSelezioneCustom;
	 	function listaEliminaSelezioneCustom(){
			var punteggioTecnicoMax="${punteggioTecnico}";
	 		var punteggioEconomicoMax = "${punteggioEconomico }";
	 		
	 		if(punteggioTecnicoMax == null || punteggioTecnicoMax == "")
	 			punteggioTecnicoMax = 0;
	 		else
	 			punteggioTecnicoMax = parseFloat(punteggioTecnicoMax);
	 				
	 		if(punteggioEconomicoMax == null || punteggioEconomicoMax == "")
	 			punteggioEconomicoMax = 0;
	 		else
	 			punteggioEconomicoMax = parseFloat(punteggioEconomicoMax);


			var numeroOccorrenze = ${currentRow}+1;
			for(var i=1; i <= numeroOccorrenze; i++){
	        	var check;
	        	if(numeroOccorrenze == 1)
	        		check = document.forms[0].keys.checked;
	        	else
	        		check = document.forms[0].keys[i - 1].checked;
	        		
	        	if(check) {
            		var punteggio = getValue("MAXPUNFIT_" + i);
 		        	var tipoCriterio = getValue("TIPPARFIT_" + i);
 		        	if(controlloSoglie(punteggio,tipoCriterio,punteggioTecnicoMax, punteggioEconomicoMax)==1){
						
						if(punteggio == null || punteggio == "")
				 			punteggio = 0;
				 		else
				 			punteggio = parseFloat(punteggio);
				 		if(tipoCriterio == 1)
				 			punteggioTecnicoMax = punteggioTecnicoMax - punteggio;
				 		else
				 			punteggioEconomicoMax = punteggioEconomicoMax - punteggio;
					}else
					 return;
 		        	
 		     	}

            }
			listaEliminaSelezioneDefault();
	 		
	 		
		}
	 	
	 	function controlloSoglie(punteggio,tipoCriterio,punteggioTecnicoMax, punteggioEconomicoMax){
	 		//alert("Punteggio:" + punteggioMassimo + " Tipo:" + tipoCriterio);
	 		
	 		var SogliaMinTec = "${SogliaMinTec }";
	 		var SogliaMinEco = "${SogliaMinEco }";
	 		
	 		if(punteggio == null || punteggio == "")
	 			punteggio = 0;
	 		else
	 			punteggio = parseFloat(punteggio);
	 			
	 		

	 		if(SogliaMinTec == null || SogliaMinTec == "")
	 			SogliaMinTec = 0;
	 		else
	 			SogliaMinTec = parseFloat(SogliaMinTec);
	 			
	 		if(SogliaMinEco ==  null || SogliaMinEco == "")
	 			SogliaMinEco = 0;
	 		else
	 			SogliaMinEco = parseFloat(SogliaMinEco);
	 			 			
	 		//alert("Puntec:" + punteggioTecnico + " Puneco:" + punteggioEconomico + " SogliaMinTec:" + SogliaMinTec + " SogliaMinEco:" + SogliaMinEco);
	 		if(tipoCriterio == 1){
	 			//Criterio tecnico
	 			punteggioTecnicoMax = punteggioTecnicoMax - punteggio;
	 			
	 			if(SogliaMinTec != 0 && SogliaMinTec > punteggioTecnicoMax){
	 				alert("Non è possibile procedere con la cancellazione.\nIl punteggio tecnico complessivo risultante dopo la cancellazione sarebbe inferiore alla relativa soglia minima(" + SogliaMinTec + ")");
	 				return -1;
	 			}else
	 				return 1;
	 				
	 			
	 		}else if(tipoCriterio == 2){
	 			//Criterio Economico
	 			
	 			punteggioEconomicoMax = punteggioEconomicoMax - punteggio;
	 			
	 			if(SogliaMinEco != 0 && SogliaMinEco > punteggioEconomicoMax){
	 				alert("Non è possibile procedere con la cancellazione.\nIl punteggio economico complessivo risultante dopo la cancellazione sarebbe inferiore alla relativa soglia minima(" + SogliaMinEco + ")");
	 				return -1;
	 			}else
	 				return 1;
	 		}
	 		
	 	}
	 	
	 	function inizializzaLista(){
			var numeroOccorrenze = ${currentRow}+1;
			if(numeroOccorrenze == 1){
				var livpar = getValue("LIVPARFIT_1");
				if(livpar ==2){
					document.forms[0].keys.style.display= "none";
					document.forms[0].keys.disabled = true;
				}
			}else{
				for(var i=1; i <= numeroOccorrenze; i++){
					var livpar = getValue("LIVPARFIT_" + i);
					if(livpar ==2){
	                	document.forms[0].keys[i - 1].style.display= "none";
	                	document.forms[0].keys[i - 1].disabled = true;
	                }
	           }
           }
           
      }
            inizializzaLista();
            
            
     <c:if test='${condizioniBloccoTelematica || condizioniBloccoNonTelematica}'>
		function listaVisualizza(){
			var bloccoPubblicazione="${condizioniBloccoTelematica }";
			var condizioniBloccoNonTelematica = "${condizioniBloccoNonTelematica }";
			document.forms[0].action=document.forms[0].action+"&bloccoPubblicazione=" + bloccoPubblicazione + "&condizioniBloccoNonTelematica=" + condizioniBloccoNonTelematica;
			document.forms[0].key.value=chiaveRiga;
			document.forms[0].metodo.value="apri";
			document.forms[0].activePage.value="0";
			document.forms[0].submit();
		}
	</c:if> 
	
	function cambiaPaginaCriteri(pagina){
		document.pagineForm.action += "&paginaCriterio=" + pagina;
		document.pagineForm.submit();	
	}
	
	<c:if test="${tipoCriterio eq 3}">
		function ConfermaAnnulla(){
			document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
			schedaAnnulla();
		}
		
		function ConfermaModifica(){
			document.forms[0].action = "${pageContext.request.contextPath}/pg/Scheda.do?"+csrfToken;
			schedaConferma();
		}
		
		function gestioneRIPCRITEC(valore){
			if(valore==1 || valore==2){
				showObj("rowGARE1_RIPCRITEC",true );
			}else{
				showObj("rowGARE1_RIPCRITEC",false);
				setValue("GARE1_RIPCRITEC","")
			}
		}
		
		function gestioneRIPCRIECO(valore){
			if(valore==1 || valore==2){
				showObj("rowGARE1_RIPCRIECO",true );
			}else{
				showObj("rowGARE1_RIPCRIECO",false);
				setValue("GARE1_RIPCRIECO","")
			}
		}
	</c:if>  
	
	function dettaglioModalitaAssegnazionePunteggio(id, ngara, necvan,sezionitec){
		var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/g1cridef/g1cridef-scheda.jsp";
		href += "&key=G1CRIDEF.ID=N:" + id ;
		href += "&ngara=" + ngara + "&necvan=" + necvan + "&sezionitec=" + sezionitec;
		var bloccoDati = false;
		var condizioniBloccoTelematica="${condizioniBloccoTelematica }";
		var condizioniBloccoNonTelematica="${condizioniBloccoNonTelematica }";
		if(condizioniBloccoTelematica == "true" || condizioniBloccoNonTelematica == "true")
			bloccoDati=true;
		href += "&bloccoDati=" + bloccoDati;
		document.location.href = href;
	}    
	
	<c:if test='${modo eq "MODIFICA" and tipoCriterio eq 3}'>
		if(document.getElementById("tecnici")!=null)
			document.getElementById("tecnici").disabled=true;
		if(document.getElementById("economici")!=null)
			document.getElementById("economici").disabled=true;
	</c:if>

	</gene:javaScript>
</table>