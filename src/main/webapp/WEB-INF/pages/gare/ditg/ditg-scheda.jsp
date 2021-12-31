<%
/*
 * Created on: 20/11/2008
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

<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />
<c:set var="numeroGara" value='${gene:getValCampo(keyParent, "GARE.NGARA")}' />
<c:set var="tipscad" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTIPSCADFunction", pageContext, numeroGara)}' scope="request"/>
<c:set var="tipgarg" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTIPGARGFunction", pageContext, numeroGara)}' scope="request"/>

<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "IMPR", "CODIMP")}'/>
<c:set var="fnucase" value='${gene:callFunction("it.eldasoft.gene.tags.utils.functions.GetUpperCaseDBFunction", "")}' />

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="DITG-scheda">
	
	<gene:setString name="titoloMaschera" value='Nuova ditta della gara ${numeroGara}'/>
	<c:if test='${!(modo eq "NUOVO")}'>
		<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTitleFunction", pageContext, "DITG_PROT")}'/>
	</c:if>
	
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="DITG" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreDITG" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreInitInserimentoDitta" >
			
			<gene:redefineInsert name="schedaConferma">
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:conferma();" title="Salva modifiche" tabindex="1501">
							${gene:resource("label.tags.template.dettaglio.schedaConferma")}</a></td>
				</tr>
			</gene:redefineInsert>
			
			
			<c:if test='${(!(modo eq "MODIFICA" or modo eq "NUOVO")) and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.DITG-scheda.StampaEtichetta")}'>
				<c:if test='${(tipscad eq "1" and (datiRiga.DITG_DRICIND ne "" and ! empty datiRiga.DITG_DRICIND ) and (datiRiga.DITG_NPRDOM ne "" and ! empty datiRiga.DITG_NPRDOM)) or (tipscad eq "2" and (datiRiga.DITG_DATOFF ne "" and ! empty datiRiga.DITG_DATOFF) and (datiRiga.DITG_NPROFF ne "" and ! empty datiRiga.DITG_NPROFF)) or (tipscad eq "3" and (datiRiga.DITG_DATREQ ne "" and ! empty datiRiga.DITG_DATREQ) and (datiRiga.DITG_NPRREQ ne "" and ! empty datiRiga.DITG_NPRREQ))}' >
					<gene:redefineInsert name="addToAzioni" >
						<tr>
							<td class="vocemenulaterale" >
								<a href="javascript:stampaProtocollo();" title="Stampa etichetta" tabindex="1503">
									Stampa etichetta
								</a>
							</td>
						</tr>
					</gene:redefineInsert>	
				</c:if>
			</c:if>

			
			<c:choose>
				<c:when test='${not ((tipscad eq "1") || (tipscad eq "2" && tipgarg eq "1"))}'>
					<gene:redefineInsert name="schedaNuovo" />
				</c:when>
				<c:otherwise>
					<gene:redefineInsert name="schedaNuovo">
						<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
							<tr>
								<td class="vocemenulaterale" >
									<c:if test='${isNavigazioneDisattiva ne "1"}'><a href="javascript:nuovo();" title="Inserisci" tabindex="1502"></c:if>
										${gene:resource("label.tags.template.lista.listaNuovo")}
									<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>
								</td>
							</tr>
					</c:if>
					</gene:redefineInsert>
				</c:otherwise>
			</c:choose>

			
				<gene:campoScheda campo="CODGAR5" visibile="false" />
				<gene:campoScheda campo="NGARA5"  visibile="false" />
				<gene:campoScheda campo="NPROGG" visibile='${modo ne "NUOVO" and tipscad eq "1"}' modificabile='${tipscad ne "3"}'/>
				<gene:campoScheda campo="NUMORDPL" visibile='${modo ne "NUOVO" and tipscad ne "1"}' modificabile='${tipscad ne "3"}'/>
				
				<gene:campoScheda campo="RTI" title="Raggruppamento temporaneo?" campoFittizio="true" value="${gene:if(isRTI eq 0,0,tipoRTI)}" definizione="T2" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoRTI" modificabile='${modo eq "NUOVO"}'/>
				
				<c:choose>
				<c:when test='${modo ne "NUOVO" and isRTI eq 0}'>
					<gene:archivio titolo="ditte"
						lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.DITG.DITTAO"),"gene/impr/impr-lista-popup.jsp", "")}'
						scheda='${gene:if(gene:checkProtObj(pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
						schedaPopUp='${gene:if(gene:checkProtObj(pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
						campi="IMPR.CODIMP;IMPR.NOMEST;IMPR.CFIMP;IMPR.PIVIMP;IMPR.CGENIMP"
						chiave="DITG_DITTAO"
						where=""
						formName="formDitteGara"
						inseribile="true">
						<gene:campoScheda campo="DITTAO" entita="DITG" obbligatorio="true" modificabile='false'/>
						<gene:campoScheda campo="NOMEST" entita="IMPR" where="DITG.DITTAO = IMPR.CODIMP" modificabile='false'/>
						<gene:campoScheda campo="CFIMP" entita="IMPR" where="DITG.DITTAO = IMPR.CODIMP" modificabile='false'/>
						<gene:campoScheda campo="PIVIMP" entita="IMPR" where="DITG.DITTAO = IMPR.CODIMP" modificabile='false'/>
						<gene:campoScheda campo="CGENIMP" entita="IMPR" where="DITG.DITTAO = IMPR.CODIMP" modificabile='false' visibile='${fn:contains(listaOpzioniDisponibili, "OP127#")}'/>
					</gene:archivio>
					
				</c:when>
				<c:when test='${modo eq "NUOVO" and isRTI eq 0}'>
					<gene:archivio titolo="ditte"
						lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.DITG.DITTAO"),"gene/impr/impr-lista-popup.jsp", "")}'
						scheda='${gene:if(gene:checkProtObj(pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
						schedaPopUp='${gene:if(gene:checkProtObj(pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
						campi="IMPR.CODIMP;IMPR.NOMEST;IMPR.CFIMP;IMPR.PIVIMP;IMPR.CGENIMP"
						chiave="DITG_DITTAO"
						where="(IMPR.TIPIMP <>3 and IMPR.TIPIMP <>10) or IMPR.TIPIMP is null"
						formName="formDitteGara"
						inseribile="true">
						<gene:campoScheda campo="DITTAO" entita="DITG" obbligatorio="true" />
						<gene:campoScheda title="Ragione sociale" campo="NOMEST" campoFittizio="true" definizione="T2000;;;;NOMIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.NOMEST")}'/>
						<gene:campoScheda title="Codice fiscale" campo="CFIMP" campoFittizio="true" definizione="T16;;;;CFIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CFIMP")}'/>
						<gene:campoScheda title="Partita I.V.A." campo="PIVIMP" campoFittizio="true" definizione="T16;;;;PIVIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.PIVIMP")}'/>
						<gene:campoScheda title="Codice dell'Anagrafico Generale" campo="CGENIMP" campoFittizio="true" definizione="T20;;;;CGENIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CGENIMP") && fn:contains(listaOpzioniDisponibili, "OP127#")}'/>
					</gene:archivio>
				</c:when>
				<c:otherwise>
														
					<c:choose>
						<c:when test='${raggruppamentoSelezionato ne "SI"}'>
							<gene:campoScheda campo="DITTAO" keyCheck="true" title="Codice raggruppamento temporaneo" entita="DITG" obbligatorio="${isCodificaAutomatica eq 'false'}"  />
							<gene:campoScheda campo="DITTAO_FIT" title="Codice raggruppamento temporaneo" campoFittizio="true" definizione="T10;;;;CODIMP" modificabile="false" />
							<gene:campoScheda title="Ragione sociale" campo="NOMEST" speciale="true" campoFittizio="true" definizione="T2000;;;NOTE;NOMIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.NOMEST")}'  >
								<gene:popupCampo titolo="Selezione raggruppamento da archivio ditte" href="archivioRTI()" />
							</gene:campoScheda>
							<gene:archivio titolo="ditte"
								lista='gene/impr/impr-lista-popup.jsp?abilitaNuovo=1'
								scheda=''
								schedaPopUp='${gene:if(gene:checkProtObj(pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
								campi="IMPR.CODIMP;IMPR.NOMEST;IMPR.CFIMP;IMPR.PIVIMP;IMPR.CGENIMP"
								chiave="CODDIC"
								where="(IMPR.TIPIMP <>3 and IMPR.TIPIMP <>10) or IMPR.TIPIMP is null"
								formName="formDitteGara"
								inseribile="true">
								<gene:campoScheda title="Codice ditta mandataria" campo="CODDIC" campoFittizio="true" definizione="T10;;;;CODDIC" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.RAGIMP.CODDIC")}' />
								<gene:campoScheda title="Ragione sociale" campo="NOMEST1" campoFittizio="true" definizione="T2000;;;;NOMIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.NOMEST")}' />
								<gene:campoScheda title="Codice fiscale" campo="CFIMP" campoFittizio="true" definizione="T16;;;;CFIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CFIMP")}' />
								<gene:campoScheda title="Partita I.V.A." campo="PIVIMP" campoFittizio="true" definizione="T16;;;;PIVIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.PIVIMP")}' />
								<gene:campoScheda title="Codice dell'Anagrafico Generale" campo="CGENIMP" campoFittizio="true" definizione="T20;;;;CGENIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CGENIMP") && fn:contains(listaOpzioniDisponibili, "OP127#")}'/>
							</gene:archivio>
							<gene:campoScheda title="Quota di partecipazione" campo="QUODIC" campoFittizio="true" definizione="F9.5;;;PRC;QUODIC" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.RAGIMP.QUODIC")}' />
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test='${modo eq "NUOVO"}'>
									<gene:campoScheda campo="DITTAO" keyCheck="true" entita="DITG" obbligatorio="${isCodificaAutomatica eq 'false'}" value="${codiceRaggruppamento}" visibile="false"/>
									<gene:campoScheda campo="DITTAO_FIT" keyCheck="true" title="Codice raggruppamento temporaneo" campoFittizio="true" obbligatorio="${isCodificaAutomatica eq 'false'}" value="${codiceRaggruppamento}" definizione="T10;;;;CODIMP" modificabile="false"/>
									<gene:campoScheda title="Ragione sociale" campo="NOMEST" speciale="true" campoFittizio="true" definizione="T2000;;;;NOMIMP" value="${ragSocRaggruppamento}" visibile="false"/>
									<gene:campoScheda title="Ragione sociale" campo="NOMEST_FIT" speciale="true" campoFittizio="true" definizione="T2000;;;;NOMIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.NOMEST")}' value="${ragSocRaggruppamento}" modificabile="false">
										<gene:popupCampo titolo="Selezione raggruppamento da archivio ditte" href="archivioRTI()" />
									</gene:campoScheda>
									<gene:campoScheda title="Codice ditta mandataria" campo="CODDIC" campoFittizio="true" definizione="T10;;;;CODDIC" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.RAGIMP.CODDIC")}' value="${codiceMandataria}" modificabile="false"/>
									<gene:campoScheda title="Ragione sociale" campo="NOMEST1" campoFittizio="true" definizione="T2000;;;;NOMIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.NOMEST")}' value="${ragSocMandataria}" modificabile="false"/>
									<gene:campoScheda title="Codice fiscale" campo="CFIMP" campoFittizio="true" definizione="T16;;;;CFIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CFIMP")}' value="${codfiscMandataria}" modificabile="false"/>
									<gene:campoScheda title="Partita I.V.A." campo="PIVIMP" campoFittizio="true" definizione="T16;;;;PIVIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.PIVIMP")}' value="${pivaMandataria}" modificabile="false"/>
									<gene:campoScheda title="Quota di partecipazione" campo="QUODIC" campoFittizio="true" definizione="F9.5;;;PRC;QUODIC" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.RAGIMP.QUODIC")&& isGaraElenco ne "1"}'  modificabile="false"/>
								</c:when>
								<c:otherwise>
									<gene:campoScheda campo="DITTAO" keyCheck="true" entita="DITG" obbligatorio="${isCodificaAutomatica eq 'false'}" value="${codiceRaggruppamento}" visibile="false"/>
									<gene:archivio titolo="ditte"
										lista=''
										scheda='${gene:if(gene:checkProtObj(pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
										schedaPopUp='${gene:if(gene:checkProtObj(pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
										campi="IMPR.CODIMP;IMPR.NOMEST"
										chiave="DITTAO_FIT"
										where="(IMPR.TIPIMP <>3 and IMPR.TIPIMP <>10) or IMPR.TIPIMP is null"
										formName="formDitteGara"
										inseribile="true">
										<gene:campoScheda campo="DITTAO_FIT" keyCheck="true" title="Codice raggruppamento temporaneo" campoFittizio="true" obbligatorio="${isCodificaAutomatica eq 'false'}" value="${codiceRaggruppamento}" definizione="T10;;;;CODIMP" modificabile="false"/>
										<gene:campoScheda campo="NOMEST" entita="IMPR" where="DITG.DITTAO = IMPR.CODIMP" modificabile='false'/>
									</gene:archivio>	
									
									<gene:archivio titolo="ditte"
										lista=''
										scheda=''
										schedaPopUp='${gene:if(gene:checkProtObj(pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
										campi="IMPR.CODIMP;IMPR.NOMEST;IMPR.CFIMP;IMPR.PIVIMP"
										chiave="CODDIC"
										where="(IMPR.TIPIMP <>3 and IMPR.TIPIMP <>10) or IMPR.TIPIMP is null"
										formName="formDitteMandataria"
										inseribile="true">
										<gene:campoScheda title="Codice ditta mandataria" campo="CODDIC" campoFittizio="true" definizione="T10;;;;CODDIC" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.RAGIMP.CODDIC")}' value="${codiceMandataria}" modificabile="false"/>
										<gene:campoScheda title="Ragione sociale" campo="NOMEST1" campoFittizio="true" definizione="T2000;;;;NOMIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.NOMEST")}' value="${ragSocMandataria}" modificabile="false"/>
										<gene:campoScheda title="Codice fiscale" campo="CFIMP" campoFittizio="true" definizione="T16;;;;CFIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CFIMP")}' value="${codfiscMandataria}" modificabile="false"/>
										<gene:campoScheda title="Partita I.V.A." campo="PIVIMP" campoFittizio="true" definizione="T16;;;;PIVIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.PIVIMP")}' value="${pivaMandataria}" modificabile="false"/>
									</gene:archivio>
									<gene:campoScheda title="Quota di partecipazione" campo="QUODIC" campoFittizio="true" definizione="F9.5;;;PRC;QUODIC" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.RAGIMP.QUODIC")&& isGaraElenco ne "1"}'  modificabile="false"/>
								
								</c:otherwise>
							</c:choose>
							
						</c:otherwise>
					</c:choose>
					
					
					
				</c:otherwise>
			</c:choose>
			
			<gene:campoScheda campo="NOMIMO" entita="DITG" visibile="false"/>
				
				
				
				<gene:campoScheda campo="NPRDOM" visibile="${tipscad eq '1' and !(empty datiRiga.DITG_NPRDOM)}" modificabile="${empty datiRiga.DITG_NPRDOM}" />
				<gene:campoScheda campo="NPRDOMFIT" visibile="${tipscad eq '1' and (empty datiRiga.DITG_NPRDOM)}" modificabile="false" title="N.protocollo" campoFittizio="true" definizione="T20;0;;;G1NPRDOM" value='${gene:if(!(modo eq "VISUALIZZA"),"Assegna numero protocollo","")}' href="javascript:ApriPopup('1','DITG_NPRDOM','DITG_DRICIND','DITG_ORADOM')"/>
				<gene:campoScheda campo="DRICIND" visibile="${tipscad eq '1'}" />
				<gene:campoScheda campo="ORADOM" visibile="${tipscad eq '1'}" />
				<gene:campoScheda campo="MEZDOM" visibile="${tipscad eq '1'}"/>
				<gene:campoScheda campo="ISPEDDOM" visibile="${tipscad eq '1'}"/>
				<gene:campoScheda campo="NSPEDDOM" visibile="${tipscad eq '1'}"/>
				<gene:campoScheda campo="RITDOM" visibile="${tipscad eq '1'}" modificabile="false" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoRitiroDITG"/>
				<gene:campoScheda campo="NOTPDOM" visibile="${tipscad eq '1'}"/>
								
				<gene:campoScheda campo="NPROFF" visibile="${tipscad eq '2' and !(empty datiRiga.DITG_NPROFF)}" modificabile="${empty datiRiga.DITG_NPROFF}" />
				<gene:campoScheda campo="NPROFFFIT" visibile="${tipscad eq '2' and (empty datiRiga.DITG_NPROFF)}" modificabile="false" title="N.protocollo" campoFittizio="true" definizione="T20;0;;;G1NPROFF" value='${gene:if(!(modo eq "VISUALIZZA"),"Assegna numero protocollo","")}' href="javascript:ApriPopup('2','DITG_NPROFF','DITG_DATOFF','DITG_ORAOFF')"/>
				<gene:campoScheda campo="DATOFF" visibile="${tipscad eq '2'}" />
				<gene:campoScheda campo="ORAOFF" visibile="${tipscad eq '2'}" />
				<gene:campoScheda campo="MEZOFF" visibile="${tipscad eq '2'}"/>
				<gene:campoScheda campo="ISPEDOFF" visibile="${tipscad eq '2'}"/>
				<gene:campoScheda campo="NSPEDOFF" visibile="${tipscad eq '2'}"/>
				<gene:campoScheda campo="RITOFF" visibile="${tipscad eq '2'}" modificabile="false" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoRitiroDITG"/>
				<gene:campoScheda campo="NOTPOFF" visibile="${tipscad eq '2'}"/>
				
				<gene:campoScheda campo="NPRREQ" visibile="${tipscad eq '3' and !(empty datiRiga.DITG_NPRREQ)}" modificabile="${empty datiRiga.DITG_NPRREQ}" />
				<gene:campoScheda campo="NPRREQFIT" visibile="${tipscad eq '3' and (empty datiRiga.DITG_NPRREQ)}" modificabile="false" title="N.prot. presentazione documentaz." campoFittizio="true" definizione="T20;0;;;G1NPRREQ" value='${gene:if(!(modo eq "VISUALIZZA"),"Assegna numero protocollo","")}' href="javascript:ApriPopup('3','DITG_NPRREQ','DITG_DATREQ','DITG_ORARE')"/>
				<gene:campoScheda campo="DATREQ" visibile="${tipscad eq '3'}" />
				<gene:campoScheda campo="ORAREQ" visibile="${tipscad eq '3'}" />
				<gene:campoScheda campo="MEZREQ" visibile="${tipscad eq '3'}"/>
				<gene:campoScheda campo="ISPEDREQ" visibile="${tipscad eq '3'}"/>
				<gene:campoScheda campo="NSPEDREQ" visibile="${tipscad eq '3'}"/>
				<gene:campoScheda campo="RITREQ" visibile="${tipscad eq '3'}" modificabile="false" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoRitiroDITG"/>
				<gene:campoScheda campo="NOTPREQ" visibile="${tipscad eq '3'}"/>
			
				<c:if test='${isRTI eq 1}'>
					<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
						<jsp:param name="entita" value='RAGIMP'/>
						<jsp:param name="chiave" value='${codiceRaggruppamento}'/>
						<jsp:param name="nomeAttributoLista" value='listaRaggruppamenti' />
						<jsp:param name="idProtezioni" value="RAGIMP" />
						<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gene/impr/impr-dettaglioRaggruppamentoDaGare.jsp" />
						<jsp:param name="arrayCampi" value="'RAGIMP_CODDIC_', 'RAGIMP_NOMDIC_','IMPR_CGENIMP_','IMPR_CFIMP_','IMPR_PIVIMP_', 'RAGIMP_QUODIC_'" />
						<jsp:param name="titoloSezione" value="Mandante del raggruppamento" />
						<jsp:param name="titoloNuovaSezione" value="Nuova mandante del raggruppamento" />
						<jsp:param name="descEntitaVociLink" value="ditta mandante del raggruppamento" />
						<jsp:param name="msgRaggiuntoMax" value="e ditte mandanti del raggruppamento" />
						<jsp:param name="usaContatoreLista" value="true"/>
						<jsp:param name="sezioneListaVuota" value="false"/>
						<jsp:param name="funzEliminazione" value="delComponente"/>
						<jsp:param name="raggruppamentoSelezionato" value="${raggruppamentoSelezionato}"/>
						
					</jsp:include>
					
					
				</c:if>
				
				<gene:fnJavaScriptScheda funzione='aggiornaTitoli("#RTI#")' elencocampi='RTI' esegui="false"/>
							
				<input type="hidden" name="isRTI" id="isRTI" value='${gene:if(!empty isRTI ,isRTI,0)}' />
				<input type="hidden" name="codiceRaggruppamento" id="codiceRaggruppamento" value="" />
				<input type="hidden" name="tipoRTI" id="tipoRTI" value="" />
				
			<gene:campoScheda>	
					<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-scheda.jsp">
						<jsp:param name="entita" value="V_GARE_NSCAD"/>
						<jsp:param name="inputFiltro" value="CODGAR=T:${codiceGara}"/>
						<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
					</jsp:include>
			<c:if test='${modo eq "VISUALIZZA" and not ((tipscad eq "1") || (tipscad eq "2" && tipgarg eq "1"))}' >
				<gene:redefineInsert name="pulsanteNuovo" />
			</c:if>	
			<%// Non inserisco la pagina pulsantiScheda.jsp perchè il bottone "Stampa etichetta" viene inserito alla sinistra di quelli standard. %>
			<%// A Cristian questa consa non va bene %>
				<td class="comandi-dettaglio" colSpan="2">
					<gene:insert name="addPulsanti"/>
					<c:choose>
					<c:when test='${modo eq "MODIFICA" or modo eq "NUOVO"}'>
						<gene:insert name="pulsanteSalva">
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:conferma()">
						</gene:insert>
						<gene:insert name="pulsanteAnnulla">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
						</gene:insert>
				
					</c:when>
					<c:otherwise>
						<gene:insert name="pulsanteModifica">
							<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:schedaModifica()">
							</c:if>
						</gene:insert>
						<gene:insert name="pulsanteNuovo">
							<c:if test='${gene:checkProtFunz(pageContext,"INS","SCHEDANUOVO")}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:nuovo()" id="btnNuovo">
							</c:if>
						</gene:insert>
					</c:otherwise>
					</c:choose>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.DITG-scheda.StampaEtichetta") and modo eq "VISUALIZZA" and ((tipscad eq "1" and (datiRiga.DITG_DRICIND ne "" and ! empty datiRiga.DITG_DRICIND) and (datiRiga.DITG_NPRDOM ne "" and ! empty datiRiga.DITG_NPRDOM)) or (tipscad eq "2" and (datiRiga.DITG_DATOFF ne "" and ! empty datiRiga.DITG_DATOFF) and (datiRiga.DITG_NPROFF ne "" and ! empty datiRiga.DITG_NPROFF)) or (tipscad eq "3" and (datiRiga.DITG_DATREQ ne "" and ! empty datiRiga.DITG_DATREQ) and (datiRiga.DITG_NPRREQ ne "" and ! empty datiRiga.DITG_NPRREQ)))}' >
						<INPUT type="button"  class="bottone-azione" value='Stampa etichetta' title='Stampa etichetta' onclick="javascript:stampaProtocollo();">
					</c:if>
					
					
					&nbsp;
				</td>
			</gene:campoScheda>
		</gene:formScheda>

	</gene:redefineInsert>
	<gene:javaScript>
		
		<c:if test='${inserimentoDitteSMAT ne "SI" && raggruppamentoSelezionato eq "SI" and isRTI ne 0}'>
			setValue("QUODIC", "${partecipazioneMandataria }");
		</c:if>
		
		setValue("DITG_NGARA5", "${numeroGara}");
		<c:if test='${modo eq "NUOVO" and isRTI eq 1 and raggruppamentoSelezionato ne "SI"}'>
			var isCodificaAutomatica = "${isCodificaAutomatica}";
			if (isCodificaAutomatica == 'true' && document.getElementById('DITG_DITTAO')!=null){
			 	document.getElementById('DITG_DITTAO').disabled = true;
			 	showObj("rowDITG_DITTAO", false);
		 		showObj("rowDITTAO_FIT", true);
			 }else{
			 	showObj("rowDITG_DITTAO", true);
				showObj("rowDITTAO_FIT", false);
			 }
		</c:if>
	
		//bloccoCampi();
		<c:if test='${raggruppamentoSelezionato eq "SI"}'>
			showObj("rowLinkAddRAGIMP", false);
		</c:if>
		
		
		function ApriPopup(tipscad,campo,campo1,campo2){
			// Eseguo l'apertura della maschera che propone il numero del protocollo	
			
			var data = 0;
			var ora = 0;
			var valoreCampoData;
			var valoreCampoOra;
			var valoreCampoProtocollo;
			
			valoreCampoProtocollo = getValue(campo);
			
			if (valoreCampoProtocollo == "") {
			
				valoreCampoData = getValue(campo1);
				valoreCampoOra = getValue(campo2);
				
				if (valoreCampoData != ""){
					data=1;
				}
				if (valoreCampoOra != ""){
					ora=1;
				}
				var ngara="${numeroGara }";
				openPopUpCustom("href=gare/commons/popupCalcoloNumeroProtocollo.jsp&campo=" + campo + "&tipscad=" + tipscad + "&campodata=" + campo1 + "&data=" + data + "&campoora=" + campo2 +"&ngara=" + ngara, "NumProtocollo", 100, 100, "no", "no");
			}
		}
		
		function stampaProtocollo(){
			setTimeout("stampaEtichetta()", 100);
		}

		function stampaEtichetta(){
			var href = "href=gare/ditg/composizioneEtichettaInCorso.jsp&key=" + document.forms[0].key.value + "&tipoProtocollo=${tipscad}" ;
			openPopUpCustom(href, "composizioneEtichettaProtocollo", 540, 190, 0, 0);
		}
		
		function conferma(){
		 <c:if test='${raggruppamentoSelezionato eq "SI" && isRTI eq 1}'>
			setValue("QUODIC", "");
			var quotaPartecip="${quotaPartecip}";
			//document.getElementById('quotaPartecip').value= quotaPartecip;
			setValue("quotaPartecip", quotaPartecip);
		</c:if>
				
		 <c:if test='${modo eq "NUOVO"}'>
		 	var nomest = getValue("NOMEST");
			setValue("DITG_NOMIMO",nomest);
			var nomimo = getValue("DITG_NOMIMO");
			if (nomimo!= null && nomimo.length > 61){
				nomimo = nomimo.substr(0,61);
				setValue("DITG_NOMIMO",nomimo);
			}
			<c:if test='${ isRTI eq 1}'>
				setValue("isRTI","1");
				setValue("tipoRTI",${tipoRTI });
				setValue("codiceRaggruppamento","${codiceRaggruppamento }");
				document.getElementById('DITG_DITTAO').disabled = false;
				
				//Si deve controllare l'unicità dei codici della mandataria e delle componenti
				var codiceMandataria = getValue("CODDIC");
				var messaggio = "Sono state definite pi&ugrave; ditte con lo stesso codice (";
				var continua = true;
				
				for(var i=1; i < maxIdRAGIMPVisualizzabile; i++){
					var codiceImpresaRagimp = getValue("RAGIMP_CODDIC_" + i);
					if(document.getElementById("rowtitoloRAGIMP_" + i).style.display != "none"){		
						if(codiceImpresaRagimp != null && codiceImpresaRagimp != ""){
							if(codiceMandataria!=null && codiceMandataria!="" && codiceMandataria==codiceImpresaRagimp){
								continua = false;
								outMsg(messaggio + codiceImpresaRagimp + ")", "ERR");
								onOffMsg();
								
							}else{
							
								for(var jo=(i+1); jo <= maxIdRAGIMPVisualizzabile; jo++){
									if(document.getElementById("rowtitoloRAGIMP_" + jo).style.display != "none" && codiceImpresaRagimp == getValue("RAGIMP_CODDIC_" + jo)){
										continua = false;
										outMsg(messaggio + codiceImpresaRagimp + ")", "ERR");
										onOffMsg();
							
									}
								}
							}
						}
					}
				}
				if(!continua){
					document.getElementById('DITG_DITTAO').disabled = true;
					return
				}
				
			</c:if>
		 </c:if>
		 
		 schedaConferma();
		}
		
		function aggiornaTitoli(rti){
			var valoreRti = rti;
			if(valoreRti > 0){
				document.getElementById('tipoRTI').value=valoreRti;
				valoreRti=1;
			}
			document.getElementById('isRTI').value=valoreRti;
			document.forms[0].metodo.value="apri";
			document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
			bloccaRichiesteServer();
			document.forms[0].submit();
		}
		
		function archivioRTI(){
			var nomest= getValue("NOMEST");
			var dittao = getValue("DITG_DITTAO");
			var href ="href=gene/impr/impr-listaRaggruppamento.jsp"
			var tipoRTI = "${tipoRTI }";
			if(tipoRTI!=null && tipoRTI!="")
				href += "&tipoRTI=" + tipoRTI; 
			var raggruppamentoSelezionato = "${raggruppamentoSelezionato }";
			if(raggruppamentoSelezionato != "SI" ){
				var filtroNomest="";
				var filtro ="";
				if(nomest!= null && nomest !=""){
					nomest ="'%" + nomest + "%'";
					nomest = nomest.toUpperCase();
					filtroNomest = "${fnucase}( IMPR.NOMEST ) like " + nomest;
					filtroNomest += " OR ${fnucase}( IMPR.CODIMP ) like " + nomest;
					filtroNomest = "(" + filtroNomest + ")";
				}
				
				if(dittao!= null && dittao !=""){
					dittao ="'%" + dittao + "%'";
					dittao = dittao.toUpperCase();
					var filtroCodimp = "${fnucase}( IMPR.CODIMP ) like " + dittao;
					filtro = filtroCodimp;	
				}
				
				if(filtroNomest!=""){
					if(filtro!="")
						filtro += " AND ";
					filtro += filtroNomest;
				}
				
				if(filtro!="" && filtro!=""){
					filtro = escape(filtro);
					href += "&filtroNomest=" + filtro;
				}
				 
			}
			openPopUpCustom(href, "formDitteGaraRTI", 800, 500, 1, 1);
		}
		
		
		function bloccoCampi(){
			var raggruppamentoSelezionato = "${raggruppamentoSelezionato }";
			if(raggruppamentoSelezionato == "SI"){
				
				document.getElementById('DITG_DITTAO').disabled = true;
				document.getElementById('NOMEST').disabled = true;
				
				document.getElementById('CODDIC').disabled = true;
				document.getElementById('NOMEST1').disabled = true;
				document.getElementById('CFIMP').disabled = true;
				document.getElementById('PIVIMP').disabled = true;
				
				
				showObj("rowLinkAddRAGIMP", false);
				
							
				for(i=1;i<=lastIdRAGIMPVisualizzata;i++){
					document.getElementById('RAGIMP_CODDIC_' + i).disable=true;
					document.getElementById('RAGIMP_NOMDIC_' + i).disable=true;
				}
				
					
			}
			
		}
		
		
		//Customizzazione della funzione delElementoSchedaMultipla per evitare che possa
	 	//essere eliminata un componente quando è stato selezionato un raggruppamento da elenco
	 	function delComponente(id, label, tipo, campi){
			var raggruppamentoSelezionato = "${raggruppamentoSelezionato }";
			if(raggruppamentoSelezionato == "SI")
				alert("Non è possibile procedere con l'eliminazione")
			else
				delElementoSchedaMultipla(id,label,tipo,campi);
		}
		
		function nuovo(){
			setValue("isRTI","0");
			setValue("codiceRaggruppamento","");
			schedaNuovo();
		}
		
	</gene:javaScript>
</gene:template>