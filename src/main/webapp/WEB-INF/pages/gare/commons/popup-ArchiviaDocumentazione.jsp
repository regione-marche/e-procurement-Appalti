<%/*
   * Created on 10-05-2015
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test='${not empty requestScope.RISULTATO and requestScope.RISULTATO eq "OK"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<c:choose>
	<c:when test='${not empty param.tipoDoc}'>
		<c:set var="tipoDoc" value="${param.tipoDoc}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoDoc" value="${tipoDoc}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codgar1}'>
		<c:set var="codgar1" value="${param.codgar1}" />
	</c:when>
	<c:otherwise>
		<c:set var="codgar1" value="${codgar1}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.genereGara}">
		<c:set var="genereGara" value="${param.genereGara}"/>
	</c:when>
	<c:otherwise>
		<c:set var="genereGara" value="${genereGara}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.isarchi}">
		<c:set var="isarchi" value="${param.isarchi}"/>
	</c:when>
	<c:otherwise>
		<c:set var="isarchi" value="${isarchi}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.lottoDiGara}">
		<c:set var="lottoDiGara" value="${param.lottoDiGara}"/>
	</c:when>
	<c:otherwise>
		<c:set var="lottoDiGara" value="${lottoDiGara}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.iterga}">
		<c:set var="iterga" value="${param.iterga}"/>
	</c:when>
	<c:otherwise>
		<c:set var="iterga" value="${iterga}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.modlic}">
		<c:set var="modlic" value="${param.modlic}"/>
	</c:when>
	<c:otherwise>
		<c:set var="modlic" value="${modlic}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.isProceduraTelematica}">
		<c:set var="isProceduraTelematica" value="${param.isProceduraTelematica}"/>
	</c:when>
	<c:otherwise>
		<c:set var="isProceduraTelematica" value="${isProceduraTelematica}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.valtec}">
		<c:set var="valtec" value="${param.valtec}"/>
	</c:when>
	<c:otherwise>
		<c:set var="valtec" value="${valtec}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.avviso}">
		<c:set var="avviso" value="${param.avviso}"/>
	</c:when>
	<c:otherwise>
		<c:set var="avviso" value="${avviso}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.tipologia}">
		<c:set var="tipologia" value="${param.tipologia}"/>
	</c:when>
	<c:otherwise>
		<c:set var="tipologia" value="${tipologia}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.sezionitec}">
		<c:set var="sezionitec" value="${param.sezionitec}"/>
	</c:when>
	<c:otherwise>
		<c:set var="sezionitec" value="${sezionitec}"/>
	</c:otherwise>
</c:choose>


<c:set var="gestioneUrl" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsGestioneUrlDocumentazioneFunction", pageContext)}' scope="request"/>

<c:set var="titoloForm" value="Archivia documenti"/>

<c:choose>
	<c:when test="${tipoDoc eq 1}">
		<c:set var="descTipoDoc" value="Documenti del bando/avviso"/>
	</c:when>
	<c:when test="${tipoDoc eq 2}">
		<c:choose>
			<c:when test="${genereGara eq 10 or genereGara eq 20}">
				<c:set var="descTipoDoc" value="Requisiti degli operatori"/>
			</c:when>
			<c:otherwise>
				<c:set var="descTipoDoc" value="Requisiti dei concorrenti"/>
			</c:otherwise>
		</c:choose>
		<c:set var="titoloForm" value="Archivia requisiti"/>
	</c:when>
	<c:when test="${tipoDoc eq 3}">
		<c:choose>
			<c:when test="${genereGara eq 10 or genereGara eq 20}">
				<c:set var="descTipoDoc" value="Documenti richiesti agli operatori"/>
			</c:when>
			<c:otherwise>
				<c:set var="descTipoDoc" value="Documenti richiesti ai concorrenti"/>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:when test="${tipoDoc eq 4}">
		<c:set var="descTipoDoc" value="Documenti dell'esito"/>
	</c:when>
	<c:when test="${tipoDoc eq 5}">
		<c:set var="descTipoDoc" value="Documenti per la trasparenza"/>
	</c:when>
	<c:when test="${tipoDoc eq 6}">
		<c:set var="descTipoDoc" value="Documenti dell'invito"/>
	</c:when>
	<c:when test="${tipoDoc eq 10 or tipoDoc eq 15}">
		<c:set var="descTipoDoc" value="Atti e documenti (art.29 c.1 DLgs 50/2016)"/>
	</c:when>
</c:choose>

<c:choose>
	<c:when test="${not empty param.titolo}">
		<c:set var="titolo" value="${param.titolo}"/>
	</c:when>
	<c:otherwise>
		<c:set var="titolo" value="${titolo}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.busta}">
		<c:set var="busta" value="${param.busta}"/>
	</c:when>
	<c:otherwise>
		<c:set var="busta" value="${busta}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.fasEle}">
		<c:set var="fasEle" value="${param.fasEle}"/>
	</c:when>
	<c:otherwise>
		<c:set var="fasEle" value="${fasEle}"/>
	</c:otherwise>
</c:choose>

<c:if test="${not empty titolo}">
	<c:set var="descTipoDoc" value="${titolo}"/>
</c:if>


< fmt:formatNumber type="number" value="${tipoDoc}" var="tipoDocumento"/>

<c:if test="${genereGara eq '3'}">
	<c:set var="varTemp" value="TORN.CODGAR=T:${codgar1 }"/>
	<c:set var="bustalotti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetBustalottiFunction", pageContext, varTemp)}' />
</c:if>

${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, codgar1, "SC", "21")}
${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, tipoDoc, "N","")}
<c:set var="where" value="DOCUMGARA.CODGAR='${codgar1 }' and DOCUMGARA.GRUPPO=${tipoDoc} and DOCUMGARA.STATODOC = 5"/>

<c:choose>
	<c:when test="${isarchi eq 1 }">
		<c:set var="where" value="${where} and DOCUMGARA.ISARCHI='1' and STATODOC is not null"/>
	</c:when>
	<c:otherwise>
		<c:set var="where" value="${where} and DOCUMGARA.ISARCHI is null and DOCUMGARA.GENTEL is null and DOCUMGARA.STATODOC = 5"/>
	</c:otherwise>
</c:choose>

<c:if test="${not empty tipologia}">
	${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, tipologia, "N","")}
	<c:set var="where" value="${where} and DOCUMGARA.TIPOLOGIA=${tipologia}"/>
</c:if>

<c:if test="${not empty busta}">
	${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ValidazioneParametroFunction", pageContext, busta, "N","")}
	<c:set var="where" value="${where} and DOCUMGARA.BUSTA=${busta}"/>
</c:if>

<c:if test="${fasEle eq iscrizione}">
	<c:set var="where" value="${where} and (DOCUMGARA.FASELE=1 or DOCUMGARA.FASELE=2)"/>
</c:if>

<c:if test="${fasEle eq rinnovo}">
	<c:choose>
		<c:when test="${isarchi eq 1 }">
			<c:set var="where" value="${where} and (DOCUMGARA.FASELE=2 or DOCUMGARA.FASELE=3)"/>
		</c:when>
		<c:otherwise>
			<c:set var="where" value="${where} and DOCUMGARA.FASELE=3"/>
		</c:otherwise>
	</c:choose>
</c:if>


<gene:template file="popup-template.jsp" gestisciProtezioni="false" >
	<gene:setString name="titoloMaschera" value="${titoloForm}" />
	

	<c:choose>
		<c:when test='${isarchi eq 1}'>
			<c:set var="modo" value="APRI" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="modo" value="MODIFICA" scope="request" />
		</c:otherwise>
	</c:choose>
	
			
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>
	
	<br>
	
	<c:choose>
		<c:when test="${ avviso eq 1}">
			<c:set var="tipoPagina" value="dell'avviso"/>
			<c:set var="sezione" value=""/>
		</c:when>
		<c:otherwise>
			<c:set var="tipoPagina" value="di gara"/>
			<c:set var="sezione" value=" della sezione '${descTipoDoc}'"/>
		</c:otherwise>
	</c:choose>
	
	<c:choose>
			<c:when test='${RISULTATO eq "ERRORI"}'>
				Si sono presentati degli errori durante l'operazione di archiviazione
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${tipoDoc eq 2}">
						<c:choose>
							<c:when test='${isarchi eq 1}'>
								Nella lista sotto sono riportate le voci archiviate della sezione '${descTipoDoc}'.<br>
								E' possibile procedere all'archiviazione di altre voci premendo il pulsante 'Archivia requisiti'.<br>
								
							</c:when>
							<c:otherwise>
								Selezionare le voci della sezione '${descTipoDoc}' che si intendono archiviare.<br>
								Si sottolinea che le voci, una volta archiviate, non possono essere più ripristinate.<br>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test='${isarchi eq 1}'>
								Nella lista sotto sono riportati i documenti ${tipoPagina } archiviati${sezione}.<br>
								E' possibile procedere all'archiviazione di altri documenti premendo il pulsante 'Archivia documenti'.<br>
								
							</c:when>
							<c:otherwise>
								Selezionare i documenti ${sezione} che si intendono archiviare.<br>
								Si sottolinea che i documenti, una volta archiviati, non possono essere più ripristinati.<br>
								<c:if test="${isProceduraTelematica eq 'true' and tipoDoc eq 3}">
									<c:choose>
										<c:when test='${genereGara eq "3"}'>
											<c:set var="chiaveControlloComunicazioni" value='${codgar1}' />
										</c:when>
										<c:otherwise>
											<c:set var="chiaveControlloComunicazioni" value="${ngara}" />
										</c:otherwise>
									</c:choose>
									<c:set var="esistonoComunicazioniFS10" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoComunicazioniFunction", pageContext, chiaveControlloComunicazioni, "FS10" )}' />
									<c:set var="esistonoComunicazioniFS11" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoComunicazioniFunction", pageContext, chiaveControlloComunicazioni, "FS11" )}' />
									<c:choose>
										<c:when test="${esistonoComunicazioniFS10 eq 'true' and  esistonoComunicazioniFS11 ne 'true'}">
											<br><b>Attenzione:</b> Ci sono domande di partecipazione, in fase di composizione o già completate, da parte degli operatori.
										</c:when>
										<c:when test="${esistonoComunicazioniFS11 eq 'true'}">
											<br><b>Attenzione:</b> Ci sono presentazioni di offerta, in fase di composizione o già completate, da parte degli operatori.
										</c:when>
									</c:choose>
								</c:if>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
			
		</c:choose>
	
	
	<c:set var="numeroDocumentiDaArchiviare" value="0"/>
		
  	<%// Creo la lista per gcap e dpre mediante la vista v_gcap_dpre%>
		<table class="lista">
					
			<tr>
				<td ${stileDati}>
  				<gene:formLista entita="DOCUMGARA" where="${where }" pagesize="20" tableclass="datilista" sortColumn="5;4" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupArchiviaDocumenti">
  					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
											
					
					<gene:set name="titoloMenu">
						<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
					</gene:set>
					<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50" visibile="${isarchi ne 1}" >
						<c:if test="${currentRow >= 0 and datiRiga.DOCUMGARA_ISARCHI ne 1}">
							<input type="checkbox" name="keys" value="${datiRiga.DOCUMGARA_NORDDOCG};${datiRiga.DOCUMGARA_IDSTAMPA}"  />
							<c:set var="numeroDocumentiDaArchiviare" value="${numeroDocumentiDaArchiviare + 1}"/>
						</c:if>
					</gene:campoLista>
						
					
					<gene:campoLista campo="CODGAR"  visibile="false" />
					<gene:campoLista campo="NGARA"  visibile="false" />
					<gene:campoLista campo="NORDDOCG"  visibile="false"/>
					<gene:campoLista campo="NUMORD"  visibile="false"/>
					<c:choose>
						<c:when test="${tipoDoc eq 1 or tipoDoc eq 4 or tipoDoc eq 6 or tipoDoc eq 10 or tipoDoc eq 15}">
							<gene:campoLista campo="DESCRIZIONE"  />
							<gene:campoLista campo="DIGNOMDOC"  entita="W_DOCDIG" where="documgara.idprg=w_docdig.idprg and documgara.iddocdg=w_docdig.iddocdig"  href="javascript:visualizzaFileAllegato('${datiRiga.DOCUMGARA_IDPRG}','${datiRiga.DOCUMGARA_IDDOCDG}',${gene:string4Js(datiRiga.W_DOCDIG_DIGNOMDOC)});"/>
							<gene:campoLista campo="URLDOC"  visibile='${gestioneUrl eq "true"}' />
							<gene:campoLista campo="ALLMAIL" title="Allegare a comunicazione?"  visibile='${tipoDoc eq 6}' />
						</c:when>
						<c:when test="${tipoDoc eq 5}">
							<gene:campoLista campo="DESCRIZIONE"  />
							<gene:campoLista campo="DIGNOMDOC"  entita="W_DOCDIG" where="documgara.idprg=w_docdig.idprg and documgara.iddocdg=w_docdig.iddocdig"  href="javascript:visualizzaFileAllegato('${datiRiga.DOCUMGARA_IDPRG}','${datiRiga.DOCUMGARA_IDDOCDG}',${gene:string4Js(datiRiga.W_DOCDIG_DIGNOMDOC)});"/>
						</c:when>
						<c:when test="${tipoDoc eq 2 }">
							<gene:campoLista campo="DESCRIZIONE"  />
						</c:when>
						<c:when test="${tipoDoc eq 3 }">
							<c:if test="${verificaBustaNullo eq false}">
								<gene:campoLista campo="TAB1NORD" entita= "TAB1" where="TAB1COD='A1013' and TAB1TIP=BUSTA" visibile="false"/>
							</c:if>
							<gene:campoLista campo="BUSTA"  visibile="${genereGara ne 10 and genereGara ne 20}"/>
							<gene:campoLista campo="FASELE"  visibile="${genereGara eq 10 or genereGara eq 20}" title="Fase"/>
							<gene:campoLista campo="DESCRIZIONE"  />
							<gene:campoLista campo="CONTESTOVAL"  />
							<gene:campoLista campo="OBBLIGATORIO" title="Obblig.?" />
							<gene:campoLista campo="MODFIRMA"  title="Formato"/>
							<gene:campoLista campo="DIGNOMDOC" title="Fac-simile" entita="W_DOCDIG" where="documgara.idprg=w_docdig.idprg and documgara.iddocdg=w_docdig.iddocdig" href="javascript:visualizzaFileAllegato('${datiRiga.DOCUMGARA_IDPRG}','${datiRiga.DOCUMGARA_IDDOCDG}',${gene:string4Js(datiRiga.W_DOCDIG_DIGNOMDOC)});"/>
							<gene:campoLista campo="BUSTA_FIT" visibile="false" edit="true" campoFittizio="true" definizione="N3;" value="${datiRiga.DOCUMGARA_BUSTA }"/>
						</c:when>
					</c:choose>
					<gene:campoLista campo="IDPRG"  visibile="false"/>
					<gene:campoLista campo="IDDOCDG"  visibile="false"/>
					<gene:campoLista campo="ISARCHI"  visibile="false" />
					<gene:campoLista campo="IDSTAMPA"  visibile="false" />
										
					<input type="hidden" name="numeroDocumenti" id="numeroDocumenti" value="" />
					<input type="hidden" name="codgar1" id="codgar1" value="${codgar1 }" />
					<input type="hidden" name="ngara" id="ngara" value="${ngara }" />
					<input type="hidden" name="tipoDoc" id="tipoDoc" value="${tipoDoc }" />
					<input type="hidden" name="genereGara" id="genereGara" value="${genereGara }" />
					<input type="hidden" name="isarchi" id="isarchi" value="${isarchi }" />
					<input type="hidden" name="lottoDiGara" id="lottoDiGara" value="${lottoDiGara }" />
					<input type="hidden" name="iterga" id="iterga" value="${iterga }" />
					<input type="hidden" name="modlic" id="modlic" value="${modlic }" />
					<input type="hidden" name="isProceduraTelematica" id="isProceduraTelematica" value="${isProceduraTelematica }" />
					<input type="hidden" name="valtec" id="valtec" value="${valtec }" />
					<input type="hidden" name="avviso" id="avviso" value="${avviso }" />
					<input type="hidden" name="tipologia" id="tipologia" value="${tipologia}" />
					<input type="hidden" name="busta" id="busta" value="${busta}" />
					<input type="hidden" name="titolo" id="titolo" value="${descTipoDoc}" />
					<input type="hidden" name="bustalotti" id="bustalotti" value="${bustalotti }" />
					<input type="hidden" name="bustaPrequalificaPresente" id="bustaPrequalificaPresente" value="No" />
					<input type="hidden" name="sezionitec" id="sezionitec" value="${sezionitec}" />
					<input type="hidden" name="fasEle" id="fasEle" value="${fasEle}" />
				</gene:formLista>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<c:choose>
						<c:when test='${isarchi eq 1 }'>
							<INPUT type="button"  class="bottone-azione" value='${titoloForm}' title='${titoloForm}' onclick="javascript:archivia();">
							<INPUT type="button"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">
						</c:when>
						<c:otherwise>
							<c:if test="${requestScope.RISULTATO ne 'ERRORI'}">
								<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:salvaLista();">
							</c:if>
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</tr>
			
		</table>
  </gene:redefineInsert>
	<gene:javaScript>
	
	function chiudi(){
		window.close();
	}
	
	function archivia(){
		document.getElementById("isarchi").value = 2;
		listaConferma();
	}
	
	function annulla(){
		document.getElementById("isarchi").value = 1;
		listaAnnullaModifica();
	}
	
	function salvaLista(){
			var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
			var tipodoc = "${tipoDoc }";
			var isarchi = "${isarchi }";
			var isProceduraTelematica ="${isProceduraTelematica }";
			if(tipodoc=="3" && isProceduraTelematica == "true" && isarchi !="1"){
				if (numeroOggetti > 0) {
					//Si deve controllare se fra i documenti selezionati ve n'è almeno uno con busta=4
					var busta;
					var arrayLen = "" + document.forms[0].keys.length;
			        if (arrayLen != 'undefined') {
			          for (i = 0; i < document.forms[0].keys.length; i++) {
			            if (document.forms[0].keys[i].checked){
			              busta=getValue("BUSTA_FIT_" + (i+1) );
						  if(busta==4){
							 document.getElementById("bustaPrequalificaPresente").value= 'Si';
							 break;
						  }
			            }
			          }
			        } else {
			          if (document.forms[0].keys.checked) {
			          	busta=getValue("BUSTA_FIT_" + (1) );
					  	if(busta==4)
						 	document.getElementById("bustaPrequalificaPresente").value= 'Si';
					  }
				    }
					
					/*
					for(i=0;i < numeroDocumenti; i++){
						//busta=getValue("DOCUMGARA_BUSTA_" + (i+1) );
						busta=getValue("BUSTA_FIT_" + (i+1) );
						if(busta==4){
							document.getElementById("bustaPrequalificaPresente").value= 'Si';
							break;
						}
					}
					*/
				}
			}
			var numeroDocumentiDaArchiviare = ${numeroDocumentiDaArchiviare };
			if(numeroDocumentiDaArchiviare==0){
				alert("Tutti i documenti sono già stati archiviati");
				return;
			}
			
	  		if (numeroOggetti == 0) {
	      		alert("Selezionare almeno un documento nella lista");
	      	} else {
	      		listaConferma();
 			}
		}
	
	function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
		var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do?"+csrfToken;
		document.location.href=href+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
	}

	</gene:javaScript>
	
	
</gene:template>

</c:otherwise>
</c:choose>