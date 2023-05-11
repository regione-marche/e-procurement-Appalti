<%
/*
 * Created on: 09/09/2009
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
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>

<style type="text/css">
	.azzurro	 {
	  color: #0000FF;
	}
	td.bordoSuperioreAzzurro {
		border-top: solid #000000 1px;
		border-top-color: #0000FF;
		color: #0000FF;
	}
</style>

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<%/*La pagina può essere richiamata anche da TORN per le gare divise a lotti con offerta unica*/ %>
<c:choose>
	<c:when test='${tipologiaGara eq "3"}'>
		<% //gara divisa a lotti con offerta unica %>
		<c:set var="where" value='GCAP.NGARA = GARE.NGARA and GARE.CODGAR1 = #TORN.CODGAR# and (GARE.GENERE is null or GARE.GENERE=0) and GARE.MODLICG in (6,5,14,16) and GCAP.DITTAO is null'/>
		<c:set var="codiceGara" value='${gene:getValCampo(key, "TORN.CODGAR")}' />
		<c:set var="ngara" value='${codiceGara}' />
		<c:set var="ordinamento" value="3;4;2" />
		<c:set var="lotti" value=" nei lotti di gara" />
	</c:when>
	<c:otherwise>
		<c:set var="where" value='GCAP.NGARA = #GARE.NGARA# and GCAP.DITTAO is null'/>
		<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
		<c:set var="ngara" value='${gene:getValCampo(key, "GARE.NGARA")}' />
		<c:set var="ordinamento" value="4;2" />
		<c:if test="${esitoControlloAdesione eq 'true' }">
			<c:set var="aqoper" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetAqoperFunction", pageContext,ngaraaq,"GARE1")}' />
		</c:if>
	</c:otherwise>
</c:choose>

<c:set var="isAccordoQuadro" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsAccordoQuadroFunction", pageContext, codiceGara)}'/>

<c:if test='${isProceduraTelematica}'>
	<c:set var="itergaMacro" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetITERGAMacroFunction", pageContext, key)}'/>
	<c:set var="iterga" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetITERGAFunction", pageContext, codiceGara)}' />
	<c:set var="bloccoPubblicazionePortaleBando11" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codiceGara,"BANDO11","false")}' />
	<c:set var="bloccoPubblicazionePortaleBando13" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codiceGara,"BANDO13","false")}' />
	<c:choose>
		<c:when test='${lottoOffertaUnica}'>
			<c:set var="chiaveTemp" value="${codiceGara }"/>
		</c:when>
		<c:otherwise>
			<c:set var="chiaveTemp" value="${ngara }"/>
		</c:otherwise>
	</c:choose>
	<c:set var="bloccoPubblicazionePortaleEsito" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,chiaveTemp,"ESITO","false")}' />
	<c:set var="condizioniBloccoTelematica" value='${bloccoPubblicazionePortaleBando13 eq "TRUE" || bloccoPubblicazionePortaleEsito eq "TRUE" || (bloccoPubblicazionePortaleBando11 eq "TRUE" && iterga == 1)}' />
</c:if>

<c:set var="tipoForniture" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipoFornitureFunction",  pageContext,codiceGara)}' />

<c:if test="${lottoDiGara ne 'true' }">
	<c:set var="esisteGaraOLIAMM" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteGaraOLIAMMFunction",  pageContext,ngara)}' />
</c:if>

<c:choose>
	<c:when test='${empty key}'>
		<c:set var="funcParam" value="${keyParent}" />
	</c:when>
	<c:otherwise>
		<c:set var="funcParam" value="${key}" />
	</c:otherwise>
</c:choose>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneListaLavorazioniFornitureFunction" parametro="${funcParam}" />
 <c:set var="tipoArticolo" value="lavorazione o fornitura" />
 <c:if test='${tipoForniture == 98}'>
	<c:set var="tipoArticolo" value="prodotto" />
 </c:if>


<c:if test="${!isProceduraTelematica }">
	<c:if test="${aqoper ne '1' }">
		<c:set var="BloccoOfferteDitte" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlloOfferteDitteFunction", pageContext, ngara,codiceGara)}' />
	</c:if>
	<c:set var="BloccoAggiudicazione" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlloAggiudicazioneDefinitivaFunction", pageContext, ngara,codiceGara)}' />
</c:if>


<c:if test='${integrazioneAUR eq "1"}'>
	<c:set var="codStazioneAppaltante" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetStazioneAppaltanteFunction", pageContext, codiceGara,"TORN")}' />
</c:if>

<c:if test="${isProceduraTelematica eq 'true' }">
	<c:set var="offtel" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetOFFTELFunction",  pageContext,codiceGara)}' scope="request"/>
	<c:set var="esistonoDatiGeneratoreAttributi" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoDatiGeneratoreAttributiFunction", pageContext, "XDPRE")}' />
</c:if>

<c:if test='${integrazioneWSERP eq "1"}'>
	<c:choose>
		<c:when test='${tipoWSERP eq "SMEUP" || tipoWSERP eq "UGOVPA"}'>
			<c:set var="visLetturaRda" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetWSERPBloccoLetturaRdaFunction", pageContext, ngara, codiceGara, tipoWSERP)}' />
			<c:set var="bloccoOperazioniSmeUp"  value = "${requestScope.bloccoOperazioniSmeUp}"/>
			<c:set var="bloccoOperazioniAtac"  value = "${requestScope.bloccoOperazioniAtac}"/>
			<c:set var="bloccoImportXLS"  value = "false"/>
			<c:set var="titleCarica"  value = "Carica RdA"/>
		</c:when>
		<c:when test='${tipoWSERP eq "AVM"}'>
			<c:set var="visLetturaRda" value='true' />
			<c:if test='${isAccordoQuadro eq "1"}'>
				<c:set var="bloccoImportXLS"  value = "true"/>
			</c:if>	
			<c:if test='${BloccoOfferteDitte eq "VERO" or BloccoAggiudicazione eq "VERO" or fasgar > 1 or condizioniBloccoTelematica}'>
				<c:set var="visLetturaRda" value='false' />
			</c:if>
			<c:set var="titleCarica"  value = "Carica RdA"/>
		</c:when>
		<c:when test='${tipoWSERP eq "RAIWAY"}'>
			<c:set var="visLetturaRda" value='true' />
			<c:set var="titleCarica"  value = "Importa posizioni Rda"/>
		</c:when>
		<c:when test='${tipoWSERP eq "ATAC"}'>
			<c:set var="visLetturaRda" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetWSERPBloccoLetturaRdaFunction", pageContext, ngara, codiceGara, tipoWSERP)}' />
			<c:set var="bloccoOperazioniSmeUp"  value = "${requestScope.bloccoOperazioniSmeUp}"/>
			<c:set var="bloccoOperazioniAtac"  value = "${requestScope.bloccoOperazioniAtac}"/>
			<c:set var="bloccoImportXLS"  value = "false"/>
			<c:if test='${bloccoOperazioniAtac eq "true"}'>
				<c:set var="bloccoImportXLS"  value = "true"/>
			</c:if>	
			<c:if test='${BloccoOfferteDitte eq "VERO" or BloccoAggiudicazione eq "VERO" or !gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.CaricaRda") or condizioniBloccoTelematica}'>
				<c:set var="visLetturaRda" value='false' />
			</c:if>
			<c:set var="visCaricaRda" value='true' />
			<c:if test='${bloccoPubblicazionePortaleBando11 eq "TRUE" or  bloccoPubblicazionePortaleBando13 eq "TRUE" or !gene:checkProtFunz(pageContext, "ALT","CaricaRda")}'>
				<c:set var="visCaricaRda" value='false' />
			</c:if>
			<c:set var="titleCarica"  value = "Carica appalto"/>
		</c:when>
		<c:when test='${tipoWSERP eq "CAV"}'>
			<c:set var="visLetturaRda" value='true' />
			<c:if test='${isAccordoQuadro eq "2"}'>
				<c:set var="bloccoImportXLS"  value = "true"/>
			</c:if>	
			<c:if test='${BloccoOfferteDitte eq "VERO" or BloccoAggiudicazione eq "VERO" or fasgar > 1 or condizioniBloccoTelematica}'>
				<c:set var="visLetturaRda" value='false' />
			</c:if>
		</c:when>
		<c:otherwise>
			<c:set var="visLetturaRda" value='false' />
			<c:set var="bloccoImportXLS"  value = "false"/>
		</c:otherwise>
	</c:choose>
</c:if>

<c:set var="integrazioneProgrammazione" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneProgrammazioneFunction", pageContext)}'/>					


<table class="dettaglio-tab-lista">
	<c:if test='${tipologiaGara ne "3"}'>
	<tr>
		<td>
			<table class="arealayout">
				<tr>
					<td width="300px" >Totale lavorazioni soggette a ribasso:</td>
					<td align="right" width="135px"> &nbsp;<span id="importoSoggRibasso">${importoSoggRibasso} &euro;&nbsp;&nbsp;</span></td>
					<td width="30px"></td>
					<td width="300px" class="azzurro">Importo a base di gara soggetto a ribasso:</td>
					<td align="right" width="135px" class="azzurro"> &nbsp;<span id="importoGaraSoggRibasso" >${importoGaraSoggRibasso} &euro;&nbsp;&nbsp;</span></td>
					<td></td>
				</tr>
				
				<tr>
					<td width="300px">Totale lavorazioni non soggette a ribasso:</td>
					<td align="right" width="135px">&nbsp;<span id="importoNoRibasso">${importoNoRibasso} &euro;&nbsp;&nbsp;</span>
					</td>
					<td width="30px"></td>
					<td width="300px" class="azzurro">Importo a base di gara non soggetto a ribasso</td>
					<td align="right" width="135px" class="azzurro">&nbsp;<span id="importoGaraNoRibasso">${importoGaraNoRibasso} &euro;&nbsp;&nbsp;</span>
					<td></td>
				</tr>
				
				
				<tr>
					<td width="300px">Totale lavorazioni solo sicurezza:</td>
					<td align="right" width="135px" >&nbsp;<span id="importoSicurezza">${importoSicurezza} &euro;&nbsp;&nbsp;</span>
					</td>
					<td width="30px"></td>
					<td width="300px" class="azzurro">Importo a base di gara solo sicurezza:</td>
					<td align="right" width="135px" class="azzurro">&nbsp;<span id="importoGaraSicurezza">${importoGaraSicurezza} &euro;&nbsp;&nbsp;</span>
					<td></td>
				</tr>
				
				<tr>
					<td width="300px"><b>Totale derivante dal dettaglio lavorazioni:</b></td>
					<td align="right" width="135px"  class="bordoSuperiore">&nbsp;<span id="totale">${totale} &euro;&nbsp;&nbsp;</span></td>
					<td width="30px"></td>
					<td width="300px" class="azzurro"><b>Totale importo a base di gara:</b></td>
					<td align="right" width="135px"  class="bordoSuperioreAzzurro">&nbsp;<span id="totaleGara">${totaleGara} &euro;&nbsp;&nbsp;</span></td>
					<td></td>
				</tr>
				<c:if test="${ribcal eq 3 }">
				<tr>
					<td colspan="6"/> 
				</tr>
				<tr>
					<td width="300px"><b>Somma pesi:</b></td>
					<td align="right" width="135px"> &nbsp;<span id="totalePesi">${totalePesi}&nbsp;&nbsp;</span></td>
					<td width="30px"></td>
					<td></td>
					<td></td>
					<td align="left">&nbsp;&nbsp;&nbsp;</td>
				</tr>
				</c:if>
				<c:if test="${importiDifferenti }">
					<td colspan="6" style="color:#0000FF"/> 
						<br><b>ATTENZIONE</b>: L'importo totale soggetto a ribasso derivante dai prezzi unitari delle lavorazioni e forniture non è pari a quello posto a base di gara
					</td>
				</c:if>
			</table>
		</td>
	</tr>
	</c:if>
	
	<c:if test='${BloccoOfferteDitte eq "VERO" or BloccoAggiudicazione eq "VERO" or fasgar > 1}'>
	<c:set var="bloccoModifica" value="VERO"/>
	<tr>
		<td>
			<table class="arealayout">
				<tr>
					<td>
						<br><b>ATTENZIONE:</b>&nbsp;
						
						<c:choose>
							<c:when test='${BloccoOfferteDitte eq "VERO" and (BloccoAggiudicazione ne "VERO" or fasgar <=1)}'>
								I dati sono in sola consultazione perch&egrave; risultano gi&agrave; dettagliate le offerte a prezzi unitari delle ditte ${lotti }
							</c:when>
							<c:when test='${BloccoOfferteDitte ne "VERO" and (BloccoAggiudicazione eq "VERO" || fasgar >1)}'>
								I dati sono in sola consultazione perch&egrave; la gara &egrave; aggiudicata o in fase di espletamento
							</c:when>
							<c:otherwise>
								I dati sono in sola consultazione perch&egrave; risultano gi&agrave; dettagliate le offerte a prezzi unitari delle ditte ${lotti } e la gara &egrave; aggiudicata o in fase di espletamento
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	</c:if>
	
	<tr>
		<td>
			<gene:formLista entita="GCAP" where='${where}' tableclass="datilista" sortColumn='${ordinamento}' 
					gestisciProtezioni="true"  gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGCAP" pagesize="25" >
				
				<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
					<jsp:param name="entita" value="V_GARE_TORN"/>
					<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
					<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
				</jsp:include>
				
				<c:if test='${bloccoModifica eq "VERO" || esisteGaraOLIAMM eq "true" || condizioniBloccoTelematica
				 || (esitoControlloAdesione eq "true" && aqoper eq "1") || (bloccoOperazioniSmeUp eq "true") || (bloccoOperazioniAtac eq "true")}'>
					<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
				</c:if>

				<c:if test='${bloccoModifica eq "VERO" || esisteGaraOLIAMM eq "true" || condizioniBloccoTelematica
				 || (bloccoOperazioniSmeUp eq "true")}'>
					<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
				</c:if>


				<gene:redefineInsert name="addToAzioni">
					<c:if test='${autorizzatoModifiche ne "2" and ((gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.LISTALAVFORN.ExportXLSOffertaPrezzi") and tipologiaGara ne "3") or (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.LISTALAVFORN.ExportXLSOffertaPrezzi") and tipologiaGara eq "3"))}'>
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:esportaInExcel();" title='Esporta in Excel' tabindex="1503">
									Esporta in Excel
								</a>
							</td>
						</tr>
					</c:if>
					<c:if test='${autorizzatoModifiche ne "2" and esisteGaraOLIAMM ne "true" and esitoControlloAdesione ne "true" and ((gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.LISTALAVFORN.ImportXLSOffertaPrezzi") and tipologiaGara ne "3" ) or (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.LISTALAVFORN.ImportXLSOffertaPrezzi") and tipologiaGara eq "3" ))
					 and bloccoModifica ne "VERO" and !condizioniBloccoTelematica and (bloccoOperazioniSmeUp ne "true") and (bloccoImportXLS ne "true")}'>
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:importaDaExcel();" title='Importa da Excel' tabindex="1504">
									Importa da Excel
								</a>
							</td>
						</tr>	
					</c:if>
					<c:if test='${tipologiaGara ne "3" && autorizzatoModifiche ne "2" and esitoControlloAdesione ne "true" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.IntegrazioneProgrammazione") && integrazioneProgrammazione eq "1"
					and bloccoModifica ne "VERO" and !condizioniBloccoTelematica }'>
						<c:set var="codgarRda" value="${gene:getValCampo(keyParent, 'CODGAR')}" />
						<c:choose>
							<c:when test="${garaLottoUnico eq 'true'}">
								<c:set var="conteggioRDARDI" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GetRDARDIFunction", pageContext, "rdaCollegate",codiceGara,null,null)}' />
							</c:when>
							<c:otherwise>
								<c:set var="conteggioRDARDI" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GetRDARDIFunction", pageContext, "rdaCollegate",codiceGara,ngara,null)}' />
							</c:otherwise>
						</c:choose>
						<c:if test="${conteggioRDARDI > 0}" >
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:importaLavorazioniRda();" title='Importa lavorazioni da RdA/RdI' tabindex="1513">
										Importa da RdA/RdI
								</a>
							</td>
						</tr>
						</c:if>
					</c:if>
					<c:if test='${autorizzatoModifiche ne "2" and integrazioneAUR eq "1" and bloccoModifica ne "VERO" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.LISTALAVFORN.importCarrelloFabbisogni")}'>
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:importaCarrelloFabbisogni();" title='Importa carrello fabbisogni' tabindex="1505">
									Importa carrello fabbisogni
								</a>
							</td>
						</tr>
					</c:if>
					<c:if test='${autorizzatoModifiche ne "2" and esisteGaraOLIAMM eq "true" and bloccoModifica ne "VERO"}'>
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:importaDaOliamm();" title='Importa prodotti da OLIAMM'  tabindex="1506">
									Importa prodotti da OLIAMM
								</a>
							</td>
						</tr>
					</c:if>
					<c:if test='${autorizzatoModifiche ne "2" and isProceduraTelematica eq "true" and esistonoDatiGeneratoreAttributi eq "TRUE" and offtel=="1" and esitoControlloAdesione ne "true"}'>
						<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.LISTALAVFORN.configuraDatiDitte") }'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:configuraDatiDitte();" title='Configura attributi aggiuntivi'  tabindex="1507">
										Configura attributi aggiuntivi
									</a>
								</td>
							</tr>
						</c:if>
						<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.LISTALAVFORN.copiaAttributiAggiuntivi") and garaLottoUnico ne "true" and !condizioniBloccoTelematica}'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:copiaAttributiAggiuntivi();" title='Copia attributi negli altri lotti'  tabindex="1508">
										Copia attributi negli altri lotti
									</a>
								</td>
							</tr>
						</c:if>
					</c:if>
					<c:if test='${autorizzatoModifiche ne "2" and esitoControlloAdesione eq "true" and bloccoModifica ne "VERO" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.LISTALAVFORN.selezioneAccordoQuadro") && !condizioniBloccoTelematica}'>
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:selezioneAccordoQuadro();" title='Selezione da accordo quadro'  tabindex="1509">
									Selezione da accordo quadro
								</a>
							</td>
						</tr>
					</c:if>
					
					<c:if test='${autorizzatoModifiche ne "2" and visLetturaRda eq "true" }'>
						<c:set var="presenzaRda" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetWSERPPresenzaRdaFunction", pageContext, codiceGara, numeroGara, requestScope.tipoWSERP)}' />
						<c:if test='${(tipoWSERP eq "ATAC" and visCaricaRda eq "true") || (tipoWSERP eq "RAIWAY" and presenzaRda eq "1") || (!(tipoWSERP eq "ATAC") and !(tipoWSERP eq "RAIWAY") and !(tipoWSERP eq "CAV") and !((tipoWSERP eq "AVM" || tipoWSERP eq "UGOVPA") and (isAccordoQuadro eq "1" || ! empty requestScope.ngaraaq)))}'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:leggiCarrelloRda('${codiceGara}','${ngara}');" title="${titleCarica}" tabindex="1510">
										${titleCarica}
									</a>
								</td>
							</tr>
						</c:if>
						
						<c:if test='${tipoWSERP eq "AVM"}'>
							<tr>
								<td class="vocemenulaterale" >
									<a href="javascript:verificaIntegrazioneArticoli('${codiceGara}','${ngara}')" title="Verifica integrazione articoli" tabindex="1511">
											Verifica integrazione articoli con ERP
									</a>
								</td>
							</tr>
						</c:if>	
						
						<c:if test='${tipoWSERP eq "CAV"}'>
							<tr>
								<td class="vocemenulaterale" >
									<a href="javascript:apriPopupInsertLavorazioniErp('${codiceGara}','${ngara}')" title="Inserisci lavorazioni da ERP" tabindex="1511">
											Inserisci lavorazioni da ERP
									</a>
								</td>
							</tr>
						</c:if>	
						
					</c:if>
					<c:if test='${autorizzatoModifiche ne "2" and garaAggiudicata and ((gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.LISTALAVFORN.importXLSVariazionePrezzi") and tipologiaGara ne "3" ) or (gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.TORN-OFFUNICA-scheda.LISTALAVFORN.importXLSVariazionePrezzi") and tipologiaGara eq "3") )
					 and esitoControlloAdesione ne "true" and tipoForniture ne 1 and tipoForniture ne 2 and tipoForniture ne 98 }'>
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:importaExcelVarPrezzi('${ngara}','${codiceGara }','${tipologiaGara}','');" title='Importa da Excel per variazione prezzi' tabindex="1512">
									Importa da Excel per variazione prezzi
								</a>
							</td>
						</tr>
					</c:if>
					
				</gene:redefineInsert>

				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<c:if test="${currentRow >= 0}">
						<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GARE.GCAP-scheda")}' >
								<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza ${tipoArticolo}"/>
							</c:if>
							<c:if test='${(autorizzatoModifiche ne "2") && gene:checkProt(pageContext, "MASC.VIS.GARE.GCAP-scheda") && gene:checkProtFunz(pageContext, "MOD","MOD")
							 && (bloccoModifica ne "VERO") && !condizioniBloccoTelematica && (bloccoOperazioniSmeUp ne "true")}' >
								<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica ${tipoArticolo}" />
							</c:if>
							<c:if test='${(autorizzatoModifiche ne "2") && esisteGaraOLIAMM ne "true" && gene:checkProtFunz(pageContext, "DEL","DEL")
							 && (bloccoModifica ne "VERO") && !condizioniBloccoTelematica && (bloccoOperazioniSmeUp ne "true")}' >
								<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina ${tipoArticolo}" />
							</c:if>
						</gene:PopUp>
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && esisteGaraOLIAMM ne "true"
					 && (bloccoModifica ne "VERO") && !condizioniBloccoTelematica && (bloccoOperazioniSmeUp ne "true")}'>
						<input type="checkbox" name="keys" value="${chiaveRiga}"  />
					</c:if>
					</c:if>
				</gene:campoLista>
				
				<gene:campoLista campo="CONTAF" visibile="false" />
				
				<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GARE.GCAP-scheda")}'/>
				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
				<gene:campoLista campo="NGARA" visibile='${tipologiaGara eq "3"}' title='Codice lotto' headerClass="sortable" width="100"/>
				<gene:campoLista campo="NORVOC" title="N." headerClass="sortable" width="30" visibile="false"/>
				<gene:campoLista campo="CODIGA" title="Lotto" entita="GARE" visibile='${tipologiaGara eq "3"}' where="GARE.NGARA = GCAP.NGARA" headerClass="sortable" width="50" />
				<gene:campoLista campo="CODVOC" title="Voce" headerClass="sortable" width="100" href="${gene:if(visualizzaLink, link, '')}" />
				<gene:campoLista campo="VOCE" headerClass="sortable" />
				<gene:campoLista campo="CODCAT" visibile="false"/>
				<gene:campoLista campo="DESCAT" title="Categoria" entita="CAIS" where="GCAP.CODCAT=CAIS.CAISIM" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GCAP.CODCAT") and (tipoForniture eq "3" or tipoForniture eq "")}' width="200"/>
				<gene:campoLista title="Um" entita="UNIMIS" campo="DESUNI" where="UNIMIS.TIPO=GCAP.UNIMIS AND UNIMIS.CONTA=-1" width="55" headerClass="sortable"/>
				<gene:campoLista campo="QUANTI" headerClass="sortable" width="100"/>
				<gene:campoLista campo="PREZUN" headerClass="sortable" width="100"/>
				<gene:campoLista campo="PERCIVA" title="Iva" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GCAP.PERCIVA") and (tipoForniture eq "3" or tipoForniture eq "")}' headerClass="sortable" />
				<gene:campoLista campo="PESO" visibile="${ribcal eq 3 }"/>
				<gene:campoLista campo="DATACONS" title="Data consegna prevista" visibile="${iterga eq 8 }"/>
				<gene:campoLista campo="CODCARR" visibile="false"/>
				<gene:campoLista campo="CODRDA" visibile="false"/>
				<c:if test='${integrazioneProgrammazione eq "1"}'>
					<gene:campoLista title="&nbsp;" width="20" >
						<c:choose>
							<c:when test="${!empty datiRiga.GCAP_CODRDA}">
								<c:set var="ttRifRda" value="Cod. RdA/RdI: ${datiRiga.GCAP_CODRDA}"/>
								<c:set var="imgRifRda" value="inforda.png"/>
								<img width="16" height="16" title="${ttRifRda}" alt="${ttRifRda}" src="${pageContext.request.contextPath}/img/${imgRifRda}"/>
							</c:when>
							<c:otherwise>
								<c:set var="ttRifRda" value=""/>
								<c:set var="imgRifRda" value=""/>
							</c:otherwise>
						</c:choose>
					</gene:campoLista>
				</c:if>
				<gene:campoLista campo="POSRDA" visibile="false"/>
				<c:if test='${integrazioneWSERP eq "1"}'>
					<gene:campoLista title="&nbsp;" width="20" >
						<c:choose>
							<c:when test="${!empty datiRiga.GCAP_CODRDA}">
								<c:set var="ttRifRda" value="Rif.Rda ERP: ${datiRiga.GCAP_CODRDA}-${datiRiga.GCAP_POSRDA}"/>
								<c:if test='${tipoWSERP eq "ATAC" && !empty datiRiga.GCAP_CODRDA}'>
									<c:set var="ttRifRda" value="Rif.Rda ERP: ${datiRiga.GCAP_CODCARR}: ${datiRiga.GCAP_CODRDA} - ${datiRiga.GCAP_POSRDA}"/>
								</c:if>
								<c:set var="imgRifRda" value="inforda.png"/>
								<img width="16" height="16" title="${ttRifRda}" alt="${ttRifRda}" src="${pageContext.request.contextPath}/img/${imgRifRda}"/>
							</c:when>
							<c:otherwise>
								<c:set var="ttRifRda" value=""/>
								<c:set var="imgRifRda" value=""/>
							</c:otherwise>
						</c:choose>
					</gene:campoLista>
				</c:if>
				
				<%//Il campo serve nel caso di gara divisa a lotti con offerta unica per effettuare la where per individuare i lotti della gara %>
				<c:if test='${tipologiaGara eq "3"}'>
					<gene:campoLista campo="NGARA" from="GARE" visibile="false" />
				</c:if>
				<input type="hidden" name="lottoOffertaUnica" id="lottoOffertaUnica" value="${lottoOffertaUnica}"/>
				<input type="hidden" name="codgar" id="codgar" value="${codiceGara}"/>
				<input type="hidden" name="esitoControlloAdesione" id="esitoControlloAdesione" value="${esitoControlloAdesione}"/>
				<input type="hidden" name="aqoper" id="aqoper" value="${aqoper}"/>
				<input type="hidden" name="fasgar" id="fasgar" value="${fasgar}"/>
				<input type="hidden" name="ribcal" id="ribcal" value="${ribcal}"/>
				<input type="hidden" name="codiceRda" id="codiceRda" value="${codiceRda}"/>
			</gene:formLista>
		</td>
	</tr>
	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<gene:insert name="pulsanteListaInserisci">
				<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO") && (bloccoModifica ne "VERO") && esisteGaraOLIAMM ne "true"
				 && !condizioniBloccoTelematica and !(esitoControlloAdesione eq "true" and aqoper eq "1") && (bloccoOperazioniSmeUp ne "true") && (bloccoOperazioniAtac ne "true")}'>
					<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaPageNuovo")}' title='${gene:resource("label.tags.template.lista.listaPageNuovo")}' onclick="javascript:listaNuovo()">
				</c:if>
			</gene:insert>
			<c:if test='${autorizzatoModifiche ne "2" and esitoControlloAdesione eq "true" and bloccoModifica ne "VERO"
			 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE-scheda.LISTALAVFORN.selezioneAccordoQuadro") && !condizioniBloccoTelematica && (bloccoOperazioniSmeUp ne "true")}'>
				<INPUT type="button"  class="bottone-azione" value='Selezione da accordo quadro' title='Selezione da accordo quadro' onclick="javascript:selezioneAccordoQuadro();">
			</c:if>
			<gene:insert name="pulsanteListaEliminaSelezione">
				<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && (bloccoModifica ne "VERO")
				 && esisteGaraOLIAMM ne "true" && !condizioniBloccoTelematica && (bloccoOperazioniSmeUp ne "true")}'>
					<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
				</c:if>
			</gene:insert>
			&nbsp;
		</td>
	</tr>
</table>

<gene:javaScript>
	
	setContextPath("${pageContext.request.contextPath}");
	
	<c:choose>
		<c:when test="${tipoForniture == 98}">
			<c:set var="msg" value="prodotti"/>
		</c:when>
		<c:otherwise>
			<c:set var="msg" value="lavorazioni e forniture"/>
		</c:otherwise>
	</c:choose>
	
	function esportaInExcel(){
		var ngara = document.forms[0].keyParent.value;
	  var act = "${pageContext.request.contextPath}/pg/InitExportLavorazioniForniture.do";
  	var par = "ngara=" + ngara.substring(ngara.indexOf(":")+1);
  	var codiceGara="${codiceGara }";
  	par += "&codiceGara=" + codiceGara;
  <c:if test='${bloccoModifica eq "VERO"}'>
  	par += "&bloccoOfferteDitte=1";	
  </c:if>
<c:choose>
  <c:when test='${tipologiaGara eq "3"}'>
  	par += "&garaLottiConOffertaUnica=1";
  </c:when>
  <c:otherwise>
  	par += "&garaLottiConOffertaUnica=2";
  </c:otherwise>
</c:choose>
   par += "&ribcal=${ribcal }";
	  openPopUpActionCustom(act, par, 'exportOffertaPrezzi', 700, 500, "yes", "yes");
	}

	function importaDaExcel(){
<c:choose>
	<c:when test='${esistonoLotti eq "true" or (esistonoLotti eq "false" and isCodificaAutomatica eq "true")}' >
		avviaImport();
	</c:when>
	<c:otherwise>
		alert("E' necessario definire almeno un lotto prima di poter avviare\nla procedura 'Importa da Excel' di ${msg}");
	</c:otherwise>
</c:choose>
	}
	
	function avviaImport(){
		var ngara = document.forms[0].keyParent.value;
	  var act = "${pageContext.request.contextPath}/pg/InitImportLavorazioniFornitureGara.do";
  	var par = "ngara=" + ngara.substring(ngara.indexOf(":")+1);
  	var codiceGara="${codiceGara }";
  	par += "&codiceGara=" + codiceGara;
<c:choose>
  <c:when test='${tipologiaGara eq "3"}'>
  	par += "&garaLottiConOffertaUnica=1";
  </c:when>
  <c:otherwise>
  	par += "&garaLottiConOffertaUnica=2";
  </c:otherwise>
</c:choose>

<c:choose>
  <c:when test='${isCodificaAutomatica eq "true"}'>
  	par += "&isCodificaAutomatica=1";
  </c:when>
  <c:otherwise>
  	par += "&isCodificaAutomatica=2";
  </c:otherwise>
</c:choose>

		openPopUpActionCustom(act, par, 'importOffertaPrezzi', 700, 500, "yes", "yes");
	}

	function listaNuovaLavorazione(){
<c:choose>
	<c:when test='${esistonoLotti eq "true"}' >
		listaNuovo();
	</c:when>
	<c:otherwise>
		alert("E' necessario definire almeno un lotto prima di\npoter aggiungere ${msg}");
	</c:otherwise>
</c:choose>
	}

	<c:if test='${esistonoLotti eq "false"}'>
		var tmpListaNuovo = listaNuovo;
		listaNuovo = listaNuovaLavorazione;
	</c:if>
	
		
	//Si imposta la scheda che deve essere aperta se TIPFORN.TORN = 1 oppure 2
	var tipoforniture="${tipoForniture}"
	if (tipoforniture == "1" || tipoforniture == "2")
		document.forms[0].pathScheda.value="/WEB-INF/pages/gare/gcap/gcap_farmaci_dispositivi-scheda.jsp";
	

	function importaCarrelloFabbisogni(){
		if (tipoforniture != "1" && tipoforniture != "2"){
			alert("La funzionalità non è disponibile per il Tipo forniture della gara");
		}else
		{
			var ngara = document.forms[0].keyParent.value;
			var par = "ngara=" + ngara.substring(ngara.indexOf(":")+1);
			var tipForniture = tipoforniture;
			var codStazioneAppaltante = "${codStazioneAppaltante }";
			if (tipForniture == null || tipForniture=="")
				tipForniture=3;
			par += "&tipoFornituraGara=" + tipForniture;
			par += "&codStazioneAppaltante=" + codStazioneAppaltante;
			<c:choose>
			  <c:when test='${tipologiaGara eq "3"}'>
			  	par += "&garaLottiConOffertaUnica=1";
			  </c:when>
			  <c:otherwise>
			  	par += "&garaLottiConOffertaUnica=2";
			  </c:otherwise>
			</c:choose>
			openPopUpActionCustom(contextPath + "/pg/CarrelloFabbisogni.do", par, "importaCarrelloFabbisogni", 900, 750, 1, 1);
		}
		
	}
	
	function importaDaOliamm(){
		var ngara= "${ngara }";
		var href="href=gare/commons/popup-importOliamm.jsp&ngara=" + ngara ;
		<c:choose>
		  <c:when test='${tipologiaGara eq "3"}'>
		  	href += "&garaLottiConOffertaUnica=1";
		  </c:when>
		  <c:otherwise>
		  	href += "&garaLottiConOffertaUnica=2";
		  </c:otherwise>
		</c:choose>
		<c:choose>
		  <c:when test='${isCodificaAutomatica eq "true"}'>
		  	href += "&isCodificaAutomatica=1";
		  </c:when>
		  <c:otherwise>
		  	href += "&isCodificaAutomatica=2";
		  </c:otherwise>
		</c:choose>
	  	openPopUpCustom(href, "importaDaOliamm", 700, 400, "yes", "yes");
	}	
	
	<c:if test='${condizioniBloccoTelematica || (bloccoOperazioniSmeUp eq "true")}'>
		function listaVisualizza(){
			document.forms[0].action+="&bloccoPubblicazione=true";
			document.forms[0].key.value=chiaveRiga;
			document.forms[0].metodo.value="apri";
			document.forms[0].activePage.value="0";
			document.forms[0].submit();
		}
	</c:if>
	
	
	function configuraDatiDitte(){
		var ngara= "${ngara }";
		<c:choose>
			<c:when test='${condizioniBloccoTelematica}'>
				var bloccoModifica = true;
			</c:when>
			<c:otherwise>
				var bloccoModifica = false;
			</c:otherwise>
		</c:choose>
				
		var href = "href=gare/commons/popup-ConfiguraDatiRichiestiDitte.jsp?ngara=" + ngara + "&modalita=vis&bloccoModifica=" +bloccoModifica ;
		openPopUpCustom(href, "configuraDatiRichiestiDitte", 900, 500, "no", "yes");
	}	
	
	<c:if test="${esitoControlloAdesione eq 'true' }">
		function selezioneAccordoQuadro(){
			var ngara= "${ngara }";
			var ngaraaq= "${ngaraaq }";
			var aqoper= "${aqoper }";
			var href = "href=gare/gare/gare-popup-selDaAccordoQuadro.jsp?ngara=" + ngara + "&ngaraaq=" + ngaraaq + "&aqoper=" + aqoper;
			href += "&ribcal=${ribcal }";
			openPopUpCustom(href, "selezioneLavDaAccordoQuadro", 900, 500, "no", "yes");
		}
	</c:if>
	
	<c:if test='${integrazioneWSERP eq "1"}'>
	 <c:choose>
		<c:when test='${tipoWSERP eq "SMEUP" || tipoWSERP eq "UGOVPA" || tipoWSERP eq "AVM"  || tipoWSERP eq "ATAC" || tipoWSERP eq "RAIWAY"}'>
			function leggiCarrelloRda(codgar,ngara){
				<c:choose>
				<c:when test='${tipoWSERP eq "ATAC"}'>
					var href = "href=gare/commons/popup-ImportaLavorazioniRda.jsp?ngara=" + ngara;
					openPopUpCustom(href, "importaRdaInGara", 500, 300, "no", "yes");
				</c:when>
					<c:when test='${tipoWSERP eq "RAIWAY"}'>
						bloccaRichiesteServer();
						formListaRda.href.value = "gare/commons/lista-posizioniRda-scheda.jsp";
						formListaRda.codgar.value = codgar;
						formListaRda.codiceRda.value = '${codiceRda}';
						formListaRda.modo.value = "posRda";
						formListaRda.codice.value = ngara;
						formListaRda.genere.value = "2";
						<c:if test='${lottoOffertaUnica eq "true" || lottoOfferteDistinte eq "true"}'>
							formListaRda.filtroLotto.value = "true";
						</c:if>

						formListaRda.submit();
					</c:when>
					<c:when test='${!empty presenzaRda && presenzaRda eq "1"}'>
					alert("Tale funzione non risulta disponibile in quanto esistono rda collegate direttamente alla gara!");
				</c:when>
				<c:otherwise>
					bloccaRichiesteServer();
					formListaRda.href.value = "gare/commons/lista-rda-scheda.jsp";
					formListaRda.codgar.value = codgar;
					formListaRda.codice.value = ngara;
					formListaRda.genere.value = "2";
					formListaRda.bustalotti.value = "${bustalotti}";
					formListaRda.submit();
				</c:otherwise>
	 			</c:choose>
			}
		</c:when>
		<c:otherwise>
		</c:otherwise>
	 </c:choose>
	 
 		//Apertura popup per verifica integrazione articoli SAP
		function verificaIntegrazioneArticoli(codgar,ngara) {
			var href = "href=gare/commons/popupWserpMsg.jsp?codgar="+codgar+"&ngara="+ngara;
			openPopUpCustom(href, "WserpMsg", 600, 250, "no", "yes");
		}
			
		function apriPopupInsertLavorazioniErp(codgar,ngara) {
			var href = "href=gare/commons/conferma-ins-lavorazioni-erp.jsp?codgar="+codgar+"&ngara="+ngara;
			openPopUpCustom(href, "insLavorazioniErp", 600, 350, "no", "yes");
		}
	 
	</c:if>	
	
	function copiaAttributiAggiuntivi(){
		var ngara = "${ngara }";
		var codiceGara = "${codiceGara }";
		<c:choose>
			<c:when test='${condizioniBloccoTelematica}'>
				var bloccoModifica = true;
			</c:when>
			<c:otherwise>
				var bloccoModifica = false;
			</c:otherwise>
		</c:choose>
		var href = "href=gare/gare/gare-popup-copia-attributiAggiuntiviXDPRE.jsp?lottoSorgente=" + ngara + "&codiceGara=" + codiceGara;
		href+="&bloccoModifica="+bloccoModifica;
		openPopUpCustom(href, "copiaAttributi", 700, 450, "yes", "yes");
	}
	
	function importaLavorazioniRda(){
		var ngara = "${ngara}";
		var codiceGara = "${codiceGara}";
		var lottoUni = "${garaLottoUnico=='true'}";
		var href = "href=gare/gare/gare-popup-importaLavorazioniRda.jsp?lottoSorgente=" + ngara + "&codiceGara=" + codiceGara+"&lottounico="+lottoUni;
		openPopUpCustom(href, "importaLavorazioniRda", 700, 450, "yes", "yes");
	}
	
</gene:javaScript>


		<form name="formListaRda" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
			<input type="hidden" name="href" value="" /> 
			<input type="hidden" name="codgar" id="codgar" value="" />
			<input type="hidden" name="codice" id="codice" value="" />
			<input type="hidden" name="codiceRda" id="codiceRda" value="" />
			<input type="hidden" name="filtroLotto" id="filtroLotto" value="" />
			<input type="hidden" name="modo" id="modo" value="" />
			<input type="hidden" name="genere" id="genere" value="" />
			<input type="hidden" name="bustalotti" id="bustalotti" value="" />
			<input type="hidden" name="uffint" id="uffint" value="${sessionScope.uffint}" />
		</form> 
