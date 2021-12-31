
<%
	/*
	 * Created on 21-02-2011
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="codiceGara" value='${gene:getValCampo(key, "V_GARE_ELEDITTE.CODGAR")}' />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="where" value="DITG.CODGAR5='${codiceGara}' and (DITG.AMMGAR = 1 or DITG.AMMGAR is NULL) and DITG.ABILITAZ=1" />

<% // Fase Elenco concorrenti abilitati (pagina a lista) %>
<c:set var="step3Wizard" value="-30" scope="request" />

<c:set var="numGara" value='${fn:substringAfter(codiceGara,"$")}' />

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneListaOperatoriAbilitatiFunction" parametro="${key}" />

<gene:template file="lista-template.jsp" gestisciProtezioni="false" >
	<gene:setString name="titoloMaschera" value="Lista anonima operatori dell'elenco ${numGara} per sorteggio pubblico" />
	<gene:setString name="entita" value="DITG" />
	
	<c:if test='${historySize <= 1}'>
		<gene:insert name="addHistory">
			<gene:historyAdd titolo='${gene:getString(pageContext,"titoloMaschera",gene:resource("label.tags.template.lista.titolo"))}' id="1" />
		</gene:insert>
	</c:if>
		
	<gene:redefineInsert name="corpo">
					
		<table class="lista">
			<tr>
				<td><gene:formLista entita="DITG" pagesize="20" tableclass="datilista" gestisciProtezioni="false" sortColumn="3;4" where="${where}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiRicezione">
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					<gene:redefineInsert name="addToDocumenti" >
						
							<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.W_MODELLI")}'>
								
									<tr>
										<c:choose>
							        <c:when test='${isNavigazioneDisattiva ne "1"}'>
							          <td class="vocemenulaterale">
												  <a href="javascript:modelliPredisposti();" title="Modelli predisposti" tabindex="1510">
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
								
							</c:if>
						
					</gene:redefineInsert>					
					
										
					<gene:redefineInsert name="addToAzioni">
						<c:choose>
						<c:when test='${updateLista eq 1}'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:listaConferma();" title="Salva modifiche" tabindex="1500">
										${gene:resource("label.tags.template.dettaglio.schedaConferma")}
									</a>
								</td>
							</tr>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:annulla();" title="Annulla modifiche" tabindex="1501">
										${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
									</a>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<c:if test="${datiRiga.rowCount > 0}">
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:assegnaNumOrdine();" title='Assegna numero ordine' tabindex="1502">
										Assegna numero ordine
									</a>
								</td>
							</tr>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:listaApriInModifica();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1505">
										${gene:resource("label.tags.template.dettaglio.schedaModifica")}
									</a>
								</td>
							</tr>
							</c:if>
						</c:otherwise>
						</c:choose>
							
					</gene:redefineInsert>
					
					<gene:campoLista campo="NGARA5"  visibile="false" edit="${updateLista eq 1}"/>
					<gene:campoLista campo="CODGAR5"  visibile="false" edit="${updateLista eq 1}"/>
					<gene:campoLista campo="NPROGG"  title="N." width="50"/>
					<c:set var="link" value='javascript:archivioImpresa("${datiRiga.DITG_DITTAO}");' />
					<gene:campoLista campo="DITTAO"  edit="${updateLista eq 1}" visibile="${updateLista ne 1}" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}'/>
					<gene:campoLista campo="DITTAO_FIT" campoFittizio="true" value ="${datiRiga.DITG_DITTAO}" definizione="T10;;;;DITTAO" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}' visibile="${updateLista eq 1}"/>
					<gene:campoLista campo="NUMORDPL"  title="N.assegnato" width="100" edit="${updateLista eq 1}"/>
					<gene:campoLista campo="DATTIVAZ" edit="${updateLista eq 1}"/>
					<input type="hidden" name="WIZARD_PAGINA_ATTIVA" id="WIZARD_PAGINA_ATTIVA" value="${step3Wizard}" />
					<input type="hidden" name="numeroDitte" id="numeroDitte" value="" />
					<input type="hidden" id="isGaraElenco" name="isGaraElenco" value="1" />
					<input type="hidden" name="keyModelli" value="">
				</gene:formLista></td>
			</tr>
			
			
			
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<c:choose>
						<c:when test='${updateLista eq 1 }'>
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:listaConferma();">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
						</c:when>
						<c:otherwise>
							
							<c:if test="${datiRiga.rowCount > 0}">
								<INPUT type="button"  class="bottone-azione" value='Assegna numero ordine' title='Assegna numero ordine' onclick="javascript:assegnaNumOrdine();">&nbsp;&nbsp;&nbsp;
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:listaApriInModifica();">&nbsp;&nbsp;&nbsp;
							</c:if>
							
						</c:otherwise>
					</c:choose>
					
				</td>
			</tr>
			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		function assegnaNumOrdine(){
			var numGara="${numGara }";
			href = "href=gare/gare/gare-popup-AssegnaNumeroOrdine.jsp";
			href += "&ngara=" + numGara;
			openPopUpCustom(href, "assegnaNumOrdine", 900, 400, "yes", "yes");
		}
		
		function archivioImpresa(codiceImpresa){
<c:choose>
	<c:when test='${updateLista eq 1}' >
		var href = ("href=gene/impr/impr-scheda-popup.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		openPopUp(href, "schedaImpresa");
	</c:when>
	<c:otherwise>
		var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
		document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href + "&key=IMPR.CODIMP=T:" + codiceImpresa;
	</c:otherwise>
</c:choose>
	}
	
	function annulla(){
			document.forms[0].updateLista.value = "0";
			listaAnnullaModifica();
		}
		
		var numeroDitte = ${currentRow}+1;
		document.getElementById("numeroDitte").value = ${currentRow}+1;
		
		function modelliPredisposti(){
			var numeroGara="${numGara}";
			var entita="GARE";
			var chiaveElenco = "GARE.NGARA=T:" + numeroGara;
			document.forms[0].keyModelli.value=chiaveElenco;
			var valori = document.forms[0].name+".keyModelli";
			compositoreModelli('${pageContext.request.contextPath}',entita,'',valori);
		}
</gene:javaScript>
</gene:template>