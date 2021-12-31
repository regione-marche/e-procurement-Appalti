
<%
	/*
	 * Created on 19-10-2010
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

<gene:template file="lista-template.jsp" >
		
		<c:choose>
			<c:when test='${gene:checkProt(pageContext,"MASC.VIS.GARE.V_GARE_TORN-lista")}'>
				<c:set var="filtroLivelloUtente"
					value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_TORN")}' scope="request"/>
				<c:set var="filtroTipoGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetFiltroTipoGaraFunction", pageContext)}' scope="request"/>

				<c:set var="visualizzazioneGareALotti" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALotti") }' scope="request"/>
				<c:set var="visualizzazioneGareLottiOffUnica" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareLottiOffUnica") }' scope="request"/>
				<c:set var="visualizzazioneGareALottoUnico" value='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALottoUnico") }' scope="request"/>
				<c:set var="profilo" value='1'/>
				<c:set var="titoloCodice" value='Codice gara'/>
				
			</c:when>
			<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_PROFILO-lista.ApriGare")}'>
				<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" /> 
				<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou89#")}' >
					<c:set var="amministratore" value="true"/>
				</c:if>
				<c:set var="idUtente" value="${sessionScope.profiloUtente.id}" />
				<c:set var="uffintAbilitata" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata")}'/>
				
				<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_PROFILO")}' scope="request"/>
				<c:set var="filtroProfilo" value=" AND V_GARE_PROFILO.CODPROFILO IN (SELECT COD_PROFILO FROM W_ACCPRO WHERE ID_ACCOUNT = ${idUtente})" scope="request"/>
				<c:if test="${uffintAbilitata eq 1 and !amministratore}">
					<c:set var="filtroUffint" value=" AND CENINT IN (select codein from usr_ein where syscon = ${idUtente} )" scope="request"/>
				</c:if>	
				<c:set var="uffintAbilitati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata")}'/>
				<c:set var="filtroTipoGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetFiltroTipoGaraFunction", pageContext)}' scope="request"/>
				<c:set var="profilo" value='8'/>
				<c:set var="titoloCodice" value='Codice gara'/>
				
			</c:when>
			<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare")}'>
				<c:set var="filtroLivelloUtenteElencoOperatori"
					value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_ELEDITTE")}' scope="request"/>
				<c:set var="profilo" value='2'/>
				<c:set var="titoloCodice" value='Codice elenco'/>
			</c:when>
			<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare")}'>
				<c:set var="filtroLivelloUtenteCataloghi"
					value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_CATALDITTE")}' scope="request"/>
				<c:set var="profilo" value='3'/>
				<c:set var="titoloCodice" value='Codice catalogo'/>
			</c:when>
			<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.MERIC-lista.ApriRicercaMercato")}'>
				<c:set var="filtroLivelloRicercheMercato"
					value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "MERIC")}' scope="request"/>
				<c:set var="profilo" value='4'/>
				<c:set var="titoloCodice" value='Codice ODA'/>
			</c:when>
			<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GAREAVVISI-lista.ApriAvviso")}'>
				<c:set var="filtroLivelloUtenteAvvisi"
					value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "GAREAVVISI")}' scope="request"/>
				<c:set var="profilo" value='5'/>
				<c:set var="titoloCodice" value='Codice avviso'/>
			</c:when>
			<c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.creazioneOrdini")}'>
				<c:set var="profilo" value='9'/>
				<c:set var="titoloCodice" value='Codice Ordine'/>
				<c:set var="idUtente" value="${sessionScope.profiloUtente.id}" />
				<c:set var="filtroUffint" value="${sessionScope.uffint}" scope="request"/>
			</c:when>
		</c:choose>
		<c:set var="conteggioComunicazioniDaLeggere" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetComunicazioniDaLeggereFunction", pageContext, "sel", profilo)}' />
		<gene:setString name="titoloMaschera" value="Comunicazioni ricevute non lette" />
		<c:set var="keyParentComunicazioni" value="" scope="session"/>
		
	
	<gene:setString name="entita" value="W_INVCOM" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
						
		
		<table class="lista">
			<tr>
				<td><gene:formLista pagesize="1000" tableclass="datilista" gestisciProtezioni="false" sortColumn="8" varName="risultatoListaComunicazioni">
					<c:choose>
						<c:when test='${gene:checkProt(pageContext,"MASC.VIS.GARE.V_GARE_TORN-lista") or gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_PROFILO-lista.ApriGare")}'>
							<c:set var="chiaveGara" value= "${datiRiga.OBJ7}"/>
							<c:set var="chiave1" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, chiaveGara)}'/>
							<c:choose>
								<c:when test="${not empty chiave1}">
									<c:set var="chiave2" value= "CODGAR=T:${chiave1}"/>
								</c:when>
								<c:otherwise>
									<c:set var="chiave1" value= "${chiaveGara}"/>
									<c:set var="chiave2" value= "CODGAR=T:${chiaveGara}"/>
								</c:otherwise>
							</c:choose>
							<c:set var="tipoGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction", pageContext, chiave2)}'/>
							<c:set var="chiave2" value= ""/>
						</c:when>
						<c:otherwise>
							<c:set var="chiave1" value='${datiRiga.OBJ7}'/>
							<c:set var="chiaveGara" value='${datiRiga.OBJ7}'/>
							<c:choose>
								<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.MERIC-lista.ApriRicercaMercato")}'>
									<c:set var="chiave2" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodiceRicMercatoFunction", pageContext, datiRiga.OBJ7)}'/>
								</c:when>
								<c:otherwise>
									<c:set var="chiave2" value=''/>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
					<gene:campoLista title="Opzioni" width="50">
						<c:if test="${currentRow >= 0}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
								<gene:PopUpItem title="Visualizza" href="javascript:apriComunicazione('${datiRiga.OBJ1}','${datiRiga.OBJ2 }','${chiaveGara}','${tipoGara }' );" />
							</gene:PopUp>
						</c:if>
					</gene:campoLista>
					<gene:campoLista  campo="C01" definizione="T4;0" visibile="false" ordinabile="false"/>
					<gene:campoLista campo="C02" definizione="N10;0" visibile="false" ordinabile="false"/>
					<gene:campoLista campo="C03" definizione="T20;0" visibile="false" ordinabile="false"/>
					<gene:campoLista ordinabile="false" campo="C04" title="Mittente" definizione="T120;0"/>
					<gene:campoLista  ordinabile="false" campo="C05" title="Oggetto"  definizione="T300;0" href="javascript:apriComunicazione('${datiRiga.OBJ1}','${datiRiga.OBJ2 }','${chiaveGara}','${tipoGara }');"/>
					<gene:campoLista ordinabile="false" campo="C06" title="Data invio" definizione="T19;0;"/>
					<gene:campoLista ordinabile="false" campo="C08" visibile="false" definizione="D;0;"/>
					<c:choose>
						<c:when test="${profilo eq 9}">
							<gene:campoLista ordinabile="false" campo="C09" title="${titoloCodice}" definizione="T20;0" href="javascript:apriDettaglio('${chiave1}','${chiave2 }');"/>
						</c:when>
						<c:when test="${profilo ne 8}">
							<gene:campoLista ordinabile="false" campo="C07" title="${titoloCodice}" definizione="T20;0" href="javascript:apriDettaglio('${chiave1}','${chiave2 }');"/>
						</c:when>
						<c:otherwise>
							<gene:campoLista ordinabile="false" campo="C07" title="${titoloCodice}" definizione="T20;0"/>
							<gene:campoLista ordinabile="false" campo="C09" title="Profilo" definizione="T20;0"/>
							<gene:campoLista visibile="false">
								<c:choose>
									<c:when test="${datiRiga.OBJ11 ne '11'}">
										<c:set var="genereGaraAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.CheckProtProfiliFunction", pageContext, datiRiga.OBJ9,tipoGara)}' />
									</c:when>
									<c:otherwise>
										<c:set var="genereGaraAbilitato" value='true'/>
									</c:otherwise>
									
								</c:choose>
								
							</gene:campoLista>
							<gene:campoLista title="" width="30">
								<c:if test = '${genereGaraAbilitato eq "true"}'>
									<a href="javascript:visualizzaGara('${chiaveGara}','${datiRiga.OBJ10}','${datiRiga.OBJ9}','${datiRiga.OBJ11}');">
									<img width="16" height="16" title="Vai al profilo" alt="Vai al profilo" src="${pageContext.request.contextPath}/img/accediGaraProfilo.png"/>
									</a>
								</c:if>
							</gene:campoLista>
						</c:otherwise>
					</c:choose>
					<input type="hidden" name="chiaveWSDM" id="chiaveWSDM" value="${chiaveWSDM}"/>
					<input type="hidden" name="entitaWSDM" id="entitaWSDM" value="${entitaWSDM}"/>
					<input type="hidden" name="keyAdd" value="" />
					<input type="hidden" name="genere" value="" />
				</gene:formLista></td>
			</tr>
		</table>
	</gene:redefineInsert>


	<gene:redefineInsert name="listaNuovo" />
	<gene:redefineInsert name="listaEliminaSelezione" />
	
	<gene:javaScript>
				
		function apriComunicazione(chiave1,chiave2,chiaveGara,tipoGara){
			var genere;
			var keyParent;
			<c:choose>
				<c:when test='${gene:checkProt(pageContext,"MASC.VIS.GARE.V_GARE_TORN-lista") || gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_PROFILO-lista.ApriGare")}'>
					var entitaWSDM;
					
					if(tipoGara=='1'){
						entitaWSDM="TORN";
						genere = '1';
					} else{
						entitaWSDM="GARE";
					}
					document.forms[0].entitaWSDM.value = entitaWSDM;
					document.forms[0].chiaveWSDM.value = chiaveGara;
					
				</c:when>
				<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare") || gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare") ||
					gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.MERIC-lista.ApriRicercaMercato")}'>
					document.forms[0].entitaWSDM.value = "GARE";
					document.forms[0].chiaveWSDM.value = chiaveGara;
				</c:when>
				<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GAREAVVISI-lista.ApriAvviso") }'>
					document.forms[0].entitaWSDM.value = "GARE";
					document.forms[0].chiaveWSDM.value = chiaveGara;
				</c:when>
			</c:choose>
			<c:choose>
				<c:when test='${gene:checkProt(pageContext,"MASC.VIS.GARE.V_GARE_TORN-lista") || gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_PROFILO-lista.ApriGare")}'>
					if(tipoGara == '3'){
						keyParent = "TORN.CODGAR=T:" + chiaveGara;
					}else {
						keyParent = "GARE.NGARA=T:" + chiaveGara;
					}
				</c:when>
				<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare") || gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare")}' >
					keyParent = "GARE.NGARA=T:" + chiaveGara;
				</c:when>
				<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.MERIC-lista.ApriRicercaMercato")}'>
					keyParent = "GARECONT.NGARA=T:" + chiaveGara +";GARECONT.NCONT=N:1";
				</c:when>
				<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GAREAVVISI-lista.ApriAvviso")}'>
					keyParent = "GAREAVVISI.NGARA=T:" + chiaveGara;
				</c:when>
				<c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.creazioneOrdini")}'>
					keyParent = "NSO_ORDINI.ID=T:"+chiaveGara
					genere=40;
				</c:when>
			</c:choose>
			
			var keyAdd = "W_INVCOM.COMKEY1=T:" + chiaveGara;
			<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.MERIC-lista.ApriRicercaMercato")}'>
				keyAdd += ";W_INVCOM.COMKEY2=T:1";
			</c:if>
			document.forms[0].genere.value = genere;
			document.forms[0].keyAdd.value = keyAdd;
			document.forms[0].keyParent.value = keyParent;
			document.forms[0].entita.value = "W_INVCOM";
			document.forms[0].pathScheda.value = "geneweb/w_invcom/w_invcom-in-scheda.jsp";
			chiaveRiga = "W_INVCOM.IDPRG=T:" + chiave1 + ";W_INVCOM.IDCOM=N:" + chiave2;
			listaVisualizza();
		}
		
		function apriDettaglio(chiave1, chiave2){
			<c:choose>
				<c:when test='${gene:checkProt(pageContext,"MASC.VIS.GARE.V_GARE_TORN-lista")}'>
					chiaveRiga= "V_GARE_TORN.CODGAR=T:" + chiave1;
					if (chiaveRiga.indexOf('$')>0){
						//gara a lotto unico si va su gare
						document.forms[0].keyParent.value=chiaveRiga;
						document.forms[0].entita.value = "GARE";
					} else {
						document.forms[0].entita.value = "TORN";
					}
					listaVisualizza();
				</c:when>
				<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare")}'>
					var chiave = "V_GARE_ELEDITTE.CODGAR=T:$" + chiave1;
					var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/gare/gare-scheda.jsp";
					href += "&key=" + chiave;
					document.location.href = href;
				</c:when>
				<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare")}'>
					var chiave = "V_GARE_CATALDITTE.CODGAR=T:$" + chiave1;
					var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/gare/gare-scheda.jsp";
					href += "&key=" + chiave;
					document.location.href = href;
				</c:when>
				<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GAREAVVISI-lista.ApriAvviso")}'>
					//Per ora non gestito da portale quindi non testato
					var chiave = "GAREAVVISI.NGARA=T:" + chiave1;
					var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/gareavvisi/gareavvisi-scheda.jsp";
					href += "&key=" + chiave;
					document.location.href = href;
				</c:when>
				<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.MERIC-lista.ApriRicercaMercato")}'>
					var chiave = "GARECONT.NGARA=T:" + chiave1 + ";GARECONT.NCONT=N:1";
					var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/garecont/garecont-scheda.jsp";
					href += "&key=" + chiave + "&id=" + chiave2;
					document.location.href = href;
				</c:when>
				<%--
					insert here redirect to page detail order
				 --%>
				 <c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.creazioneOrdini")}'>
				 	var chiave = "NSO_ORDINI.ID=T:" + chiave1;
					var href = contextPath + "/ApriPagina.do?"+csrfToken+"&href=gare/nso_ordini/nso_ordini-scheda.jsp";
					href += "&key=" + chiave
					document.location.href = href;
				 </c:when>
			</c:choose>
			
		}
		
		<c:if test="${profilo eq 8}">
		
		// Visualizzazione del dettaglio
		function visualizzaGara(chiaveRiga,cenint,codprofilo,genere){
			var link =  '${pageContext.request.contextPath}/SetProfilo.do?'+csrfToken+'&profilo='+ codprofilo;
			if("${uffintAbilitati}" == 1){link =  link + '&uffint=' + cenint;}
			var trovaParameter = "T:" + chiaveRiga;
			var trovaAddWhere = '';
			if(genere=='11'){
				trovaAddWhere = "GAREAVVISI.NGARA = ?";
				link =  link + '&href=gare/gareavvisi/gareavvisi-lista.jsp';
			}else{
				trovaAddWhere = "V_GARE_TORN.CODICE = ?";
				link =  link + '&href=gare/v_gare_torn/v_gare_torn-lista.jsp';
			}
			link =  link + '&trovaParameter=' + trovaParameter + '&trovaAddWhere=' + trovaAddWhere;
			
			document.location.href = link;
		}
				
		
				
		</c:if>
	</gene:javaScript>

</gene:template>