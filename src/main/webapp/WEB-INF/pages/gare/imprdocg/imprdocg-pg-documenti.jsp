<%/*
   * Created on 17-ott-2007
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

<c:set var="codiceGara" value='${gene:getValCampo(key, "DITG.CODGAR5")}' />
<c:set var="numeroGara" value='${gene:getValCampo(key, "DITG.NGARA5")}' />
<c:set var="codiceDitta" value='${gene:getValCampo(key, "DITG.DITTAO")}' />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="digitalSignatureUrlCheck" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-check-url")}'/>
<c:set var="digitalSignatureProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:choose>
	<c:when test="${!empty digitalSignatureUrlCheck && !empty digitalSignatureProvider && (digitalSignatureProvider eq 1 || digitalSignatureProvider eq 2)}">
		<c:set var="digitalSignatureWsCheck" value='1'/>
	</c:when>
	<c:otherwise>
		<c:set var="digitalSignatureWsCheck" value='0'/>
	</c:otherwise>
</c:choose>

${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, codiceGara, "SC", "21")}
${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, numeroGara, "SC", "20")}
${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, codiceDitta, "SC", "10")}

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:choose>
	<c:when test='${not empty param.stepWizard}'>
		<c:set var="stepWizard" value="${param.stepWizard}" />
	</c:when>
	<c:otherwise>
		<c:set var="stepWizard" value="${stepWizard}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.comunicazioniVis}'>
		<c:set var="comunicazioniVis" value="${param.comunicazioniVis}" />
	</c:when>
	<c:otherwise>
		<c:set var="comunicazioniVis" value="${comunicazioniVis}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${genereGara eq 10 ||  genereGara eq 20}">
		<c:set var="ordinamento" value="-16;-17;25;5;8"/>
	</c:when>
	<c:otherwise>
		<c:set var="ordinamento" value="-7;25;5;8"/>
	</c:otherwise>
</c:choose>

<c:if test="${stepWizard eq '6.5'}" >
	<c:set var="stepWizard" value="6" />
</c:if>

<c:set var="tipoImpresa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaImpresaFunction",  pageContext,key)}'/>

<c:if test='${genereGara ne 10 and genereGara ne 20 and tipo != "CONSULTAZIONE"}' >
	<c:set var="whereBusteAttiveWizard" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetBusteDocumentazioneFunction",  pageContext, stepWizard, "V_GARE_DOCDITTA","BUSTA")}'/>
</c:if>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.InizializzaUpdateListaFunction" parametro="${key}" />

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<c:set var="prefissoFileDownloadComBari" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", prefissoFileDownloadComuneBari)}' scope="request"/>


<c:set var="whereAppoggio" value="CODGAR='${codiceGara }'"/>
<c:set var="gartel" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "GARTEL","TORN", whereAppoggio)}'/>
<c:if test="${gartel eq 1}">
	<c:set var="whereAppoggio" value="CODGAR='${codiceGara }' and genere < 100"/>
	<c:set var="genereGara" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "GENERE","V_GARE_GENERE", whereAppoggio)}'/>
	<c:if test="${genereGara eq 3}">
		<c:set var="whereAppoggio" value="NGARA='${codiceGara }'"/>
		<c:set var="bustalotti" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "BUSTALOTTI","GARE", whereAppoggio)}'/>
	</c:if>
	<c:if test='${genereGara ne 10 and genereGara ne 20}'>
		<c:choose >
			<c:when test="${bustalotti eq 2}">
				<c:set var="whereSezTec" value="CODGAR1='${codiceGara }' and SEZIONITEC='1'"/>
				<c:set var="numLottiSezionitec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "count(SEZIONITEC)","GARE1", whereSezTec)}'/>
				<c:if test="${not empty numLottiSezionitec and numLottiSezionitec ne 0 }">
					<c:set var="sezionitec" value="1"/>
				</c:if>
			</c:when>
			<c:otherwise>
				<c:set var="whereSezTec" value="NGARA='${numeroGara }'"/>
				<c:set var="sezionitec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "SEZIONITEC","GARE1", whereSezTec)}'/>
			</c:otherwise>
		</c:choose>
	</c:if>
</c:if>

<c:choose>
  		<c:when test="${param.listaDocAnnullati eq 'true'}">
  			<c:set var="classe" value=""/>
  		</c:when>
  		<c:otherwise>
  			<c:set var="classe" value='class="dettaglio-tab-lista"'/>
  		</c:otherwise>
  	</c:choose>

<table ${classe}>
<tr>

	<c:choose>
		<c:when test='${genereGara eq 10 or genereGara eq 20}'>
			<c:set var="whereV_GARE_DOCDITTA"
				value="V_GARE_DOCDITTA.CODGAR='${codiceGara }' and (V_GARE_DOCDITTA.NGARA is null or V_GARE_DOCDITTA.NGARA='${numeroGara }') and V_GARE_DOCDITTA.CODIMP='${codiceDitta}'" />
				<c:set var="abilitazione" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDatiAbilitazioneOperatoreFunction",  pageContext, numeroGara,codiceDitta)}'/>
				<c:if test="${abilitaz eq '1' and not empty dabilitazString }">
					<c:set var="whereV_GARE_DOCDITTA" value="${whereV_GARE_DOCDITTA} and not(V_GARE_DOCDITTA.FASELE=1 and V_GARE_DOCDITTA.DATAPUB is not null and V_GARE_DOCDITTA.DATAPUB > ${dabilitazString })" />
				</c:if>
			<c:set var="whereQform" value="KEY1='${numeroGara }'"/>
			<c:set var="idQform" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreCampoDBFunction", pageContext, "ID","QFORM", whereQform)}'/>
		</c:when>
		<c:otherwise>
			<c:set var="whereV_GARE_DOCDITTA"
				value="V_GARE_DOCDITTA.CODGAR='${codiceGara }' and (V_GARE_DOCDITTA.NGARA is null or V_GARE_DOCDITTA.NGARA='${numeroGara }') and V_GARE_DOCDITTA.CODIMP='${codiceDitta}' and (${whereBusteAttiveWizard})" />
		</c:otherwise>
	</c:choose>

	<c:set var="indiceRiga" value="-1"/>
	<c:set var="numCambi" value="0"/>
	
	<c:set var="whereImprdocg" value="IMPRDOCG.CODGAR = V_GARE_DOCDITTA.CODGAR and IMPRDOCG.NGARA = V_GARE_DOCDITTA.NGARA and IMPRDOCG.CODIMP = V_GARE_DOCDITTA.CODIMP and IMPRDOCG.NORDDOCI = V_GARE_DOCDITTA.NORDDOCI and IMPRDOCG.PROVENI = V_GARE_DOCDITTA.PROVENI"/>
	
	<c:set var="whereV_GARE_DOCDITTA" value="${whereV_GARE_DOCDITTA} and (V_GARE_DOCDITTA.ISARCHI='2' or  V_GARE_DOCDITTA.ISARCHI is null or (V_GARE_DOCDITTA.ISARCHI='1' and (V_GARE_DOCDITTA.DATARILASCIO is not null or V_GARE_DOCDITTA.ORARILASCIO is not null or V_GARE_DOCDITTA.IDDOCDG is not null)))"/>
		
	<c:choose>
		<c:when test="${tipoImpresa eq '' or empty tipoImpresa }">
			<c:set var="whereV_GARE_DOCDITTA" value="${whereV_GARE_DOCDITTA} and V_GARE_DOCDITTA.CONTESTOVAL is null"/>
		</c:when>
		<c:otherwise>
			<c:set var="whereV_GARE_DOCDITTA" value="${whereV_GARE_DOCDITTA} and (V_GARE_DOCDITTA.CONTESTOVAL is null or V_GARE_DOCDITTA.CONTESTOVAL = ${tipoImpresa })"/>
		</c:otherwise>
	</c:choose>
	
	<c:if test="${sezionitec ne 1 and genereGara ne 10 and genereGara ne 20}">
		<c:set var="whereV_GARE_DOCDITTA" value="${whereV_GARE_DOCDITTA} and ( (V_GARE_DOCDITTA.BUSTA is null or V_GARE_DOCDITTA.SEZTEC is null) or not (V_GARE_DOCDITTA.BUSTA = 2 and V_GARE_DOCDITTA.SEZTEC=1))"/>
	</c:if>		
  	
  	<c:choose>
  		<c:when test="${param.listaDocAnnullati eq 'true'}">
  			<c:set var="whereV_GARE_DOCDITTA" value="${whereV_GARE_DOCDITTA} and V_GARE_DOCDITTA.DOCANNUL = '1'"/>
  		</c:when>
  		<c:otherwise>
  			<c:set var="whereV_GARE_DOCDITTA" value="${whereV_GARE_DOCDITTA} and (V_GARE_DOCDITTA.DOCANNUL is null or  V_GARE_DOCDITTA.DOCANNUL <> '1')"/>
  		</c:otherwise>
  	</c:choose>
  	
  	
  	<%// Creo la lista per i documenti%>
		<table class="lista">
					
			<tr>
				<td ${stileDati}>
  				<gene:formLista entita="V_GARE_DOCDITTA" where='${whereV_GARE_DOCDITTA}' pagesize="0" tableclass="datilista" sortColumn="${ordinamento }" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreVerificaDocumenti">
  					
  					<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
							<jsp:param name="entita" value="V_GARE_TORN"/>
							<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
							<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
						</jsp:include>
					
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					<gene:redefineInsert name="addToAzioni" >
						<c:choose>
							<c:when test='${updateLista eq 1}'>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:salvaLista();" title="Salva modifiche" tabindex="1500">
											${gene:resource("label.tags.template.dettaglio.schedaConferma")}
										</a>
									</td>
								</tr>
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:listaAnnullaModifica();" title="Annulla modifiche" tabindex="1501">
											${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
										</a>
									</td>
								</tr>
							</c:when>
							<c:otherwise>
								<c:if test="${param.listaDocAnnullati ne 'true'}">
									<c:if test='${autorizzatoModifiche ne 2 and datiRiga.rowCount > 0 and gene:checkProtFunz(pageContext,"MOD","MOD") }'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:listaApriInModifica();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1502">
													${gene:resource("label.tags.template.dettaglio.schedaModifica")}
												</a>
											</td>
										</tr>
									</c:if>
									<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "INS","INS") and tipo != "CONSULTAZIONE" and tipo != "VERIFICA_NO_INS"}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:aggiungi();" title='Aggiungi documentazione' tabindex="1502">
													Aggiungi documentazione
												</a>
											</td>
										</tr>
									</c:if>
									<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") and tipo != "CONSULTAZIONE" }'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:listaEliminaSelezione();" title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' tabindex="1503">
													${gene:resource("label.tags.template.lista.listaEliminaSelezione")}
												</a>
											</td>
										</tr>
									</c:if>
								</c:if>
								<c:if test='${(gene:checkProtFunz(pageContext,"ALT","EsportaDocumentiBusta") and stepWizard ne "2") or (stepWizard eq "2" and gene:checkProtFunz(pageContext,"ALT","FASE-AMM.EsportaDocumentiBusta"))}'>
									<tr>
										<td class="vocemenulaterale">
											<a href='javascript:openModal("${numeroGara}", "${codiceDitta}" ,"${pageContext.request.contextPath}",${stepWizard },"${prefissoFileDownloadComBari }");' title='Esporta su file zip' tabindex="1504">
												Esporta su file zip
											</a>
										</td>
									</tr>
								</c:if>
								<c:if test='${param.listaDocAnnullati ne "true" and  gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.IMPRDOCG-lista.DOCUMENTI.DocAnnullati") and  (genereGara eq 10 or genereGara eq 20) and not empty idQform}'>
									<tr>
										<td class="vocemenulaterale">
											<a href='javascript:apriListaDocAnnullati();' title='Documenti annullati da operatore' tabindex="1504">
												Documenti annullati da operatore
											</a>
										</td>
									</tr>
								</c:if>
							</c:otherwise>
						</c:choose>
					</gene:redefineInsert>
					
					
					<c:set var="oldTab1desc" value="${newTab1desc}"/>
					<c:set var="newTab1desc" value="${datiRiga.V_GARE_DOCDITTA_BUSTADESC}"/>
					<c:set var="busta" value="${datiRiga.V_GARE_DOCDITTA_BUSTA}"/>
					<gene:campoLista campoFittizio="true" visibile="false">
						<%/* Nel caso in cui siano diversi inframezzo il titolo */%>
						<c:if test="${newTab1desc != oldTab1desc}">
							<td colspan="10">
								<b>${newTab1desc }</b> 
							</td>
						</tr>
						
						<c:choose>
							<c:when test="${busta eq 4}">
								<c:set var="bustaPrequalifica" value="1" scope="request" />
							</c:when>
							<c:when test="${busta eq 1}">
								<c:set var="bustaAmministrativa" value="1" scope="request" />
							</c:when>
							<c:when test="${busta eq 2}">
								<c:set var="bustaTecnica" value="1" scope="request" />
							</c:when>
							<c:when test="${busta eq 3}">
								<c:set var="bustaEconomica" value="1" scope="request" />
							</c:when>
						</c:choose>
						
						<tr class="odd">
						<c:set var="numCambi" value="${numCambi + 1}"/>
						</c:if>
						
					</gene:campoLista>
					
					<c:choose>
						<c:when test='${(empty updateLista or updateLista ne 1) and autorizzatoModifiche ne 2 and (gene:checkProtFunz(pageContext, "DEL","DEL") or gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")) and tipo != "CONSULTAZIONE" and param.listaDocAnnullati ne "true"}' >
							<gene:set name="titoloMenu">
								<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
							</gene:set>
							<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50"  >
								<c:if test="${currentRow >= 0}">
									<c:if test="${datiRiga.V_GARE_DOCDITTA_PROVENI eq 2 and datiRiga.V_GARE_DOCDITTA_DOCTEL ne 1}">
										<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'" >
											<gene:PopUpItem title="Elimina" href="listaElimina()" />
										</gene:PopUp>
										<input type="checkbox" name="keys" value="${chiaveRiga}"  />
									</c:if>
								</c:if>
							</gene:campoLista>
						</c:when>
						<c:otherwise>
							<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" visibile="false" />
						</c:otherwise>
					</c:choose>
					
					<jsp:include page="imprdocg-documenti-interno.jsp" >
						<jsp:param name="whereImprdocg" value="${whereImprdocg}"/>
					</jsp:include>
					
					<gene:campoLista campoFittizio="true" visibile="false">
						<input type="hidden" id="INPUT_IDDOCDG_${currentRow}" value="${datiRiga.V_GARE_DOCDITTA_IDDOCDG}"/>
					</gene:campoLista>
					<gene:campoLista campoFittizio="true" visibile="false">
						<input type="hidden" id="INPUT_IDPRG_${currentRow}" value="${datiRiga.V_GARE_DOCDITTA_IDPRG}"/>
					</gene:campoLista>	
					<gene:campoLista campoFittizio="true" visibile="false">
						<input type="hidden" id="INPUT_DIGNOMDOC_${currentRow}" value="${datiRiga.V_GARE_DOCDITTA_DIGNOMDOC}"/>
					</gene:campoLista>
					<gene:campoLista campoFittizio="true" visibile="false">
						<input type="hidden" id="INPUT_BUSTA_${currentRow}" value="${datiRiga.V_GARE_DOCDITTA_BUSTA}"/>
					</gene:campoLista>
					
					<gene:campoLista campoFittizio="true" visibile="false">
						<input type="hidden" id="INPUT_DESCRIZIONE_${currentRow}" value="${datiRiga.V_GARE_DOCDITTA_DESCRIZIONE}"/>
					</gene:campoLista>					
					<input type="hidden" name="numeroDocumenti" id="numeroDocumenti" value="" />
					<input type="hidden" name="codiceDitta" id="codiceDitta" value="${codiceDitta }" />
					<input type="hidden" name="stepWizard" id="stepWizard" value="${stepWizard }" />
					<input type="hidden" name="tipo" id="tipo" value="${tipo }" />
					<input type="hidden" name="genereGara" id="genereGara" value="${genereGara }" />
					<input type="hidden" name="comunicazioniVis" id="comunicazioniVis" value="${comunicazioniVis }" />
					
				</gene:formLista>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<c:choose>
						<c:when test='${updateLista eq 1}'>
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:salvaLista();">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:listaAnnullaModifica();">
						</c:when>
						<c:otherwise>
							<c:if test="${param.listaDocAnnullati ne 'true'}">
								<c:if test='${autorizzatoModifiche ne 2 and datiRiga.rowCount > 0 and gene:checkProtFunz(pageContext,"MOD","MOD") and tipo != "CONSULTAZIONE" }'>
									<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:listaApriInModifica();">&nbsp;&nbsp;&nbsp;
								</c:if>
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext, "INS","INS")  and tipo != "VERIFICA_NO_INS"}'>
									<INPUT type="button"  class="bottone-azione" value='Aggiungi documentazione' title='Aggiungi documentazione' onclick="javascript:aggiungi();">&nbsp;&nbsp;&nbsp;
								</c:if>
								<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") }'>
									<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione();">
								</c:if>
								<c:if test='${autorizzatoModifiche ne 2 and ((gene:checkProtFunz(pageContext, "INS","INS")  and tipo != "VERIFICA_NO_INS") or gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") or (datiRiga.rowCount > 0 and gene:checkProtFunz(pageContext,"MOD","MOD") and tipo != "CONSULTAZIONE")) }'>
									&nbsp;<br><br>
								</c:if>
							</c:if>
							
							<c:choose>
								<c:when test="${genereGara eq 10 or genereGara eq 20}">
									<c:choose>
								  		<c:when test="${param.listaDocAnnullati eq 'true'}">
								  			<c:set var="testoIndietro" value="Torna a documenti richiesti e presentati" />
								  		</c:when>
								  		<c:otherwise>
								  			<c:set var="testoIndietro" value="Torna a elenco operatori" />
								  		</c:otherwise>
								  	</c:choose>
								</c:when>
								<c:when test="${stepWizard eq '8' }">
									<c:set var="testoIndietro" value="Torna al dettaglio aggiudicazione" />
								</c:when>
								<c:otherwise>
									<c:set var="testoIndietro" value="Torna a elenco concorrenti" />
								</c:otherwise>
							</c:choose>
							<INPUT type="button"  class="bottone-azione" value='${testoIndietro }' title='${testoIndietro }' onclick="javascript:historyVaiIndietroDi(1);">
							
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</tr>
			
		</table>
		
		<jsp:include page="/WEB-INF/pages/gare/commons/modalPopupDownloadDocumenti.jsp" />
		
		<form name="formVisFirmaDigitale" id="formVisFirmaDigitale" action="${pageContext.request.contextPath}/ApriPagina.do" method="post" target="popUpFirma">
			<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale-popUp.jsp" />
			<input type="hidden" name="idprg" id="idprg" value="" />
			<input type="hidden" name="iddocdig" id="iddocdig" value="" />
			<input type="hidden" name="opreload" id="opreload" value="1" />
		</form>
		
  
	<gene:javaScript>
	
	//Per IE non viene visualizzata la barra blu verticale sotto i tab, poichè la proprietà "border-collapse=collapse"
	//impostata nella classe "arealayout" agisce pure su "dettaglio-tab-lista". Si rimuove quindi tale attributo
	$(".dettaglio-tab-lista").css("border-collapse","separate");
	
	document.getElementById("numeroDocumenti").value = ${currentRow}+1;
				
	function aggiungi(){
		var stepWizard="${stepWizard }";
		var href = "href=gare/imprdocg/imprdocg-schedaPopup-insert.jsp";
		href += "&modo=NUOVO&stepWizard=" + stepWizard;
		openPopUpCustom(href, "aggiungiDocumentazione", 800, 500, "yes", "yes");
		
	}
	
	function chiudi(){
		window.close();
	}
	
	function ulterioriCampi(indiceRiga, chiaveRiga){
			var href = "href=gare/imprdocg/imprdocg-schedaPopup-ulterioriCampi.jsp";
			<c:if test='${updateLista eq "1"}'>
				href += "&modo=MODIFICA";
			</c:if>
			href += "&indiceRiga=" + indiceRiga;
			href += "&key=" + chiaveRiga;
			var sezionitec = "${sezionitec}";
			href += "&sezionitec=" + sezionitec;
			openPopUpCustom(href, "ulterioriCampi", 800, 550, "yes", "yes");
		}
	
	function visualizzaInMdgue(idprg,iddocdig) {
		console.log('visualizzaInMdgue:'+idprg+','+iddocdig);
		var href = "${pageContext.request.contextPath}/pg/VisualizzaDgueApp.do";
		var codiceDitta = "${codiceDitta}";
		var codiceGara = "${codiceGara }";
		//document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig+ "&codiceDitta=" + codiceDitta+ "&codiceGara=" + codiceGara;
		var getUrl = window.location;
		var baseUrl = getUrl .protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1]+'/';
		console.log('baseUrl: '+baseUrl);
		var a = document.createElement("a");
		a.target = 'blank';
		a.href = baseUrl+'pg/VisualizzaDgueApp.do?'+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig+ "&codiceDitta=" + codiceDitta+ "&codiceGara=" + codiceGara;
		document.body.appendChild(a);
		a.click();
		document.body.removeChild(a);
		
	}
	function visualizzaFileAllegato(idprg,iddocdig,dignomdoc,dataril,oraril,doctel,indiceRiga,norddoci,proveni) {
		<c:if test="${ not empty prefissoFileDownloadComBari and stepWizard eq '5'}">
		var prefissoFile = 	"${prefissoFileDownloadComBari }";
		var busta = $( "#INPUT_BUSTA_" + (indiceRiga)).val();
		var descrizione = $( "#INPUT_DESCRIZIONE_" + (indiceRiga)).val();
		prefissoFile = prefissoFile.toUpperCase();
		if(descrizione!=null && busta == '2'){
			descrizione = descrizione.toUpperCase();
			if(descrizione.substring(0,prefissoFile.length) == prefissoFile){
				if(!confirm("ATTENZIONE: questo documento dovrebbe essere scaricato solo dopo aver completato la prima fase della valutazione tecnica.\nVuoi davvero scaricare il file?"))
					return;
			}
		}
		</c:if>
				
		var numeroGara = "${numeroGara}";
		var codiceDitta = "${codiceDitta}";
		var codiceGara = "${codiceGara }";
		var syscon = "${ sessionScope.profiloUtente.id}";
		aggiornamentoPresaVisioneDocDitta(codiceGara,numeroGara,codiceDitta, norddoci, proveni,syscon);
		
		var vet = dignomdoc.split(".");
		var ext = vet[vet.length-1];
		ext = ext.toUpperCase();
		<c:choose>
			<c:when test="${digitalSignatureWsCheck eq 0}">
				if(ext=='P7M' || ext=='TSD'){
					if(dataril!=null && dataril!=''){
						var res1 = dataril.substring(0,2);
						var res2 = dataril.substring(3,5);
						var res3 = dataril.substring(6,10);
						var fdataril = res3+res2+res1;
						if(oraril!=null && oraril!="")
							fdataril+=" " + oraril +":00";
						if($("#ckdate").size() == 0){
							var _input = $("<input/>", {"type": "hidden","id": "ckdate", "name": "ckdate", value:""});
							$("#formVisFirmaDigitale").append(_input);
						}
						document.formVisFirmaDigitale.ckdate.value = fdataril;
					}else{
					 	if($("#ckdate").size() > 0){
							document.formVisFirmaDigitale.ckdate.remove();
						}
					}
				 	document.formVisFirmaDigitale.idprg.value = idprg;
					document.formVisFirmaDigitale.iddocdig.value = iddocdig;
					var l = Math.floor((screen.width-800)/2);
					var t = Math.floor((screen.height-550)/2);
					var numOpener = getNumeroPopUp()+1;
					l = l - 30 + (numOpener * 30);
					t = t - 50 + (numOpener * 50);
					window.open("","popUpFirma","toolbar=no,menubar=no,width=800,height=550,top="+t+",left="+l+",resizable=yes,scrollbars=yes");
					tracciamentoDownloadDocimpresa(idprg, iddocdig,numeroGara,codiceDitta,doctel);
					document.formVisFirmaDigitale.submit();
				}else{
					tracciamentoDownloadDocimpresa(idprg, iddocdig,numeroGara,codiceDitta,doctel);
					var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
					var nomeCodificato = encodeURIComponent(dignomdoc);
					document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + nomeCodificato;
				}
			</c:when>
			<c:otherwise>
				if(ext=='P7M' || ext=='TSD' || ext=='XML' || ext=='PDF'){
					if(dataril!=null && dataril!=''){
						var res1 = dataril.substring(0,2);
						var res2 = dataril.substring(3,5);
						var res3 = dataril.substring(6,10);
						var fdataril = res3+res2+res1;
						if(oraril!=null && oraril!="")
							fdataril+=" " + oraril +":00";
						if($("#ckdate").size() == 0){
							var _input = $("<input/>", {"type": "hidden","id": "ckdate", "name": "ckdate", value:""});
							$("#formVisFirmaDigitale").append(_input);
						}
						document.formVisFirmaDigitale.ckdate.value = fdataril;
					}else{
					 	if($("#ckdate").size() > 0){
							document.formVisFirmaDigitale.ckdate.remove();
						}
					}
				 	document.formVisFirmaDigitale.idprg.value = idprg;
					document.formVisFirmaDigitale.iddocdig.value = iddocdig;
					var l = Math.floor((screen.width-800)/2);
					var t = Math.floor((screen.height-550)/2);
					var numOpener = getNumeroPopUp()+1;
					l = l - 30 + (numOpener * 30);
					t = t - 50 + (numOpener * 50);
					window.open("","popUpFirma","toolbar=no,menubar=no,width=800,height=550,top="+t+",left="+l+",resizable=yes,scrollbars=yes");
					tracciamentoDownloadDocimpresa(idprg, iddocdig,numeroGara,codiceDitta,doctel);
					document.formVisFirmaDigitale.submit();
				}else{
					tracciamentoDownloadDocimpresa(idprg, iddocdig,numeroGara,codiceDitta,doctel);
					var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
					var nomeCodificato = encodeURIComponent(dignomdoc);
					document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + nomeCodificato;
				}
			</c:otherwise>
		</c:choose>
			
	}
	
	function salvaLista(){
		var contatore = ${currentRow}+1;
		var proveni;
		for(var t=0; t < contatore; t++){
			proveni = getValue("V_GARE_DOCDITTA_PROVENI_" + (t+1));
			if(proveni!=1)
				setValue("IMPRDOCG_DESCRIZIONE_" + (t+1) ,getValue("V_GARE_DOCDITTA_DESCRIZIONE_" + (t+1)));
			var doctel = getValue("V_GARE_DOCDITTA_DOCTEL_" + (t+1));
			if(doctel!=1){
				setValue("IMPRDOCG_DATARILASCIO_" + (t+1) ,getValue("V_GARE_DOCDITTA_DATARILASCIO_" + (t+1)));
				setValue("IMPRDOCG_ORARILASCIO_" + (t+1) ,getValue("V_GARE_DOCDITTA_ORARILASCIO_" + (t+1)));
			}
			setValue("IMPRDOCG_DATASCADENZA_" + (t+1) ,getValue("V_GARE_DOCDITTA_DATASCADENZA_" + (t+1)));
			setValue("IMPRDOCG_SITUAZDOCI_" + (t+1) ,getValue("V_GARE_DOCDITTA_SITUAZDOCI_" + (t+1)));
		}
		
		listaConferma();
	}
	
	
	var selezionaPaginaDefault = selezionaPagina;
	var selezionaPagina = selezionaPaginaCustom;
	function selezionaPaginaCustom(pageNumber){
		if(document.pagineForm.action.indexOf("stepWizard")<0){
			var stepWizard="${stepWizard }";
			var genereGara="${genereGara }";
			var tipo="${tipo }";
			var comunicazioniVis = "${comunicazioniVis }";
			var aut="${aut}";
			document.pagineForm.action += "&stepWizard=" + stepWizard + "&genereGara=" + genereGara + "&tipo=" + tipo + "&comunicazioniVis=" + comunicazioniVis + "&aut=" + aut;
		}
		selezionaPaginaDefault(pageNumber);
	}
	
	function apriListaDocAnnullati(){
		var href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&key=${key}";
		href += "&href=gare/imprdocg/imprdocg-lista-docAnnullati.jsp";
		href += "&stepWizard=${stepWizard }";
		href += "&tipo=${tipo }";
		href += "&comunicazioniVis=${comunicazioniVis }";
		href += "&genereGara=${genereGara }";
		href += "&aut=${aut}";
		document.location.href=href;
	}
	</gene:javaScript>

</tr>
	</table>