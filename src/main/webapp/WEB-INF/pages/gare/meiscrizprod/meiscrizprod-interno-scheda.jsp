<%
/*
 * Created on: 06/12/2013
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

			
			<gene:campoScheda campo="ID"  visibile="false"/>
			<gene:campoScheda campo="CODGAR"  visibile="false"/>
			<gene:campoScheda campo="CODIMP"  visibile="false"/>
			<gene:campoScheda campo="NGARA"  visibile="false"/>
			
			<gene:gruppoCampi >
				<gene:campoScheda>
					<td colspan="2"><b>Categoria</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="CATOFF"  entita="OPES" from="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT and MEARTCAT.NGARA = OPES.NGARA3 and MEARTCAT.NOPEGA = OPES.NOPEGA" modificabile='false'/>
				<gene:campoScheda campo="DESCAT" campoFittizio="true" definizione="T2000;;;;DESCAT" value="${initDescatt }" modificabile='false'/>
			</gene:gruppoCampi>
							
			<gene:gruppoCampi idProtezioni="GEN" >
				<gene:campoScheda>
					<td colspan="2"><b>Dati generali articolo</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="TIPO"  entita="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT" modificabile='false'/>
				<gene:campoScheda campo="COD"  entita="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT" modificabile='false'/>
				<gene:campoScheda campo="DESCR"  entita="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT" modificabile='false'/>
				<gene:campoScheda campo="DESCRTECN"  entita="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT" modificabile='false'/>
				<gene:campoScheda campo="OBBLCERTIF"  entita="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT" visibile='false'/>
				<gene:campoScheda campo="CERTIFRICH"  entita="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT" modificabile='false' visibile="${ datiRiga.MEARTCAT_OBBLCERTIF eq 1}"/>
				<gene:campoScheda campo="IDARTCAT"   visibile='false'/>
								
				<c:if test="${!empty idArticolo and modo eq 'VISUALIZZA'}">
					<c:set var="result" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoriMEALLARTCATFunction", pageContext, idArticolo)}' />
					<c:if test="${!empty datiMEALLARTCAT }">
						<gene:campoScheda addTr="false" >
							<tbody id="sezioneMultiplaMEALLARTCAT">
						</gene:campoScheda>
						
						<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
							<jsp:param name="entita" value='MEALLARTCAT'/>
							<jsp:param name="chiave" value='${idArticolo}'/>
							<jsp:param name="nomeAttributoLista" value='datiMEALLARTCAT' />
							<jsp:param name="idProtezioni" value="MEALLARTCAT" />
							<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/meallartcat/meallartcat-interno-scheda.jsp"/>
							<jsp:param name="arrayCampi" value="'MEALLARTCAT_ID_', 'MEALLARTCAT_IDARTCAT_', 'MEALLARTCAT_IDPRG_','MEALLARTCAT_IDDOCDIG_', 'W_DOCDIG_IDPRG_', 'W_DOCDIG_IDDOCDIG_', 'W_DOCDIG_DIGNOMDOC_','selezioneFile_'"/>		
							<jsp:param name="titoloSezione" value="Facsimile certificato" />
							<jsp:param name="titoloNuovaSezione" value="Nuovo facsimile certificato" />
							<jsp:param name="descEntitaVociLink" value="facsimile certificato" />
							<jsp:param name="msgRaggiuntoMax" value="i facsimile"/>
							<jsp:param name="usaContatoreLista" value="true"/>
							<jsp:param name="sezioneListaVuota" value="false" />
							<jsp:param name="sezioneInseribile" value="false" />
							<jsp:param name="sezioneEliminabile" value="false" />
						</jsp:include>
		
						<gene:campoScheda addTr="false">
							</tbody>
						</gene:campoScheda>
					</c:if>
					
				</c:if>
				
				
				<gene:campoScheda campo="COLORE"  entita="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT" modificabile='false' visibile="${datiRiga.MEARTCAT_TIPO eq 1}"/>
				<gene:campoScheda campo="NOTE"  entita="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT" modificabile='false'/>
			</gene:gruppoCampi>			
			
			<gene:gruppoCampi idProtezioni="SPEC" >
				<gene:campoScheda>
					<td colspan="2"><b>Operatore economico</b></td>
				</gene:campoScheda>
				<c:choose>
					<c:when test="${param.daListaProdotti eq 1}">
						<c:set var="link" value='javascript:archivioImpresa("${datiRiga.MEISCRIZPROD_CODIMP}");' />
						<gene:campoScheda campo="CODIMP" modificabile='false' href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}'/>
						<gene:campoScheda campo="NOMEST" entita="IMPR" where="MEISCRIZPROD.CODIMP=IMPR.CODIMP" modificabile="false" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}'/>
					</c:when>
					<c:otherwise>
						<gene:campoScheda campo="CODIMP" modificabile='false'/>
						<gene:campoScheda campo="NOMEST" entita="IMPR" where="MEISCRIZPROD.CODIMP=IMPR.CODIMP" modificabile="false"/>
					</c:otherwise>
				</c:choose>
					
			</gene:gruppoCampi>
			
			<gene:gruppoCampi idProtezioni="SPEC" >
				<gene:campoScheda>
					<td colspan="2"><b>Specifiche del prodotto</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="CODOE" modificabile='false'/>
				<gene:campoScheda campo="MARCAPRODUT"   visibile="${datiRiga.MEARTCAT_TIPO eq 1}" modificabile='false'/>
				<gene:campoScheda campo="CODPRODUT"   visibile="${datiRiga.MEARTCAT_TIPO eq 1}" modificabile='false'/>
				<gene:campoScheda campo="NOME" modificabile='false'/>
				<c:if test="${!empty datiMEALLISCRIZPROD1 and modo eq 'VISUALIZZA'}">
						<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
							<jsp:param name="entita" value='MEALLISCRIZPROD'/>
							<jsp:param name="chiave" value='${datiRiga.MEISCRIZPROD_ID}'/>
							<jsp:param name="nomeAttributoLista" value='datiMEALLISCRIZPROD1' />
							<jsp:param name="idProtezioni" value="IMMAGINE" />
							<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/mealliscrizprod/mealliscrizprod-interno-scheda.jsp"/>
							<jsp:param name="arrayCampi" value="'MEALLISCRIZPROD_ID_', 'MEALLISCRIZPROD_IDISCRIZPROD_', 'MEALLISCRIZPROD_IDPRG_','MEALLISCRIZPROD_IDDOCDIG_', 'W_DOCDIG_IDPRG_', 'W_DOCDIG_IDDOCDIG_', 'W_DOCDIG_DIGNOMDOC_'"/>		
							<jsp:param name="titoloSezione" value="Immagine" />
							<jsp:param name="descEntitaVociLink" value="immagine" />
							<jsp:param name="usaContatoreLista" value="true"/>
							<jsp:param name="sezioneListaVuota" value="false" />
							<jsp:param name="sezioneInseribile" value="false" />
							<jsp:param name="sezioneEliminabile" value="false" />
						</jsp:include>
				</c:if>
				<gene:campoScheda campo="DESCAGG"   modificabile='false'/>
				<gene:campoScheda campo="DIMENSIONI"  visibile="${datiRiga.MEARTCAT_TIPO eq 1}" modificabile='false'/>
				<c:if test="${!empty datiMEALLISCRIZPROD2 and modo eq 'VISUALIZZA'}">
						<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
							<jsp:param name="entita" value='MEALLISCRIZPROD'/>
							<jsp:param name="chiave" value='${datiRiga.MEISCRIZPROD_ID}'/>
							<jsp:param name="nomeAttributoLista" value='datiMEALLISCRIZPROD2' />
							<jsp:param name="idProtezioni" value="CERTIFICAZIONI" />
							<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/mealliscrizprod/mealliscrizprod-interno-scheda.jsp"/>
							<jsp:param name="arrayCampi" value="'MEALLISCRIZPROD_ID_', 'MEALLISCRIZPROD_IDISCRIZPROD_', 'MEALLISCRIZPROD_IDPRG_','MEALLISCRIZPROD_IDDOCDIG_', 'W_DOCDIG_IDPRG_', 'W_DOCDIG_IDDOCDIG_', 'W_DOCDIG_DIGNOMDOC_'"/>		
							<jsp:param name="titoloSezione" value="Certificazioni richieste" />
							<jsp:param name="titoloNuovaSezione" value="Nuovo certificazione" />
							<jsp:param name="descEntitaVociLink" value="certificazione" />
							<jsp:param name="msgRaggiuntoMax" value="e certificazioni"/>
							<jsp:param name="usaContatoreLista" value="true"/>
							<jsp:param name="sezioneListaVuota" value="false" />
							<jsp:param name="sezioneInseribile" value="false" />
							<jsp:param name="sezioneEliminabile" value="false" />
						</jsp:include>
				</c:if>
				<c:if test="${!empty datiMEALLISCRIZPROD3 and modo eq 'VISUALIZZA'}">
						<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
							<jsp:param name="entita" value='MEALLISCRIZPROD'/>
							<jsp:param name="chiave" value='${datiRiga.MEISCRIZPROD_ID}'/>
							<jsp:param name="nomeAttributoLista" value='datiMEALLISCRIZPROD3' />
							<jsp:param name="idProtezioni" value="SCHEDE" />
							<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/mealliscrizprod/mealliscrizprod-interno-scheda.jsp"/>
							<jsp:param name="arrayCampi" value="'MEALLISCRIZPROD_ID_', 'MEALLISCRIZPROD_IDISCRIZPROD_', 'MEALLISCRIZPROD_IDPRG_','MEALLISCRIZPROD_IDDOCDIG_', 'W_DOCDIG_IDPRG_', 'W_DOCDIG_IDDOCDIG_', 'W_DOCDIG_DIGNOMDOC_'"/>		
							<jsp:param name="titoloSezione" value="Schede tecniche" />
							<jsp:param name="titoloNuovaSezione" value="Nuova scheda" />
							<jsp:param name="descEntitaVociLink" value="scheda" />
							<jsp:param name="msgRaggiuntoMax" value="e schede"/>
							<jsp:param name="usaContatoreLista" value="true"/>
							<jsp:param name="sezioneListaVuota" value="false" />
							<jsp:param name="sezioneInseribile" value="false" />
							<jsp:param name="sezioneEliminabile" value="false" />
						</jsp:include>
				</c:if>
				<gene:campoScheda campo="OBBLGAR" entita="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT"  visibile='false'/>
				<gene:campoScheda campo="GARANZIA" visibile="${datiRiga.MEARTCAT_OBBLGAR eq 1}"  modificabile='false'/>
			</gene:gruppoCampi>		
			
			<gene:gruppoCampi idProtezioni="QUANTI" >
				<gene:campoScheda>
					<td colspan="2"><b>Quantità e prezzi</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="PRZUNITPER"  entita="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT" modificabile='false'/>
				<gene:campoScheda campo="UNIMISPRZ"  entita="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT" modificabile='false'/>
				<gene:campoScheda campo="PRZUNIT"   visibile="${datiRiga.MEARTCAT_PRZUNITPER eq 3}" modificabile='false'/>
				<gene:campoScheda campo="QUNIMISPRZ" visibile="${datiRiga.MEARTCAT_PRZUNITPER eq 3}"  modificabile='false'/>
				<gene:campoScheda campo="UNIMISACQ"  entita="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT" visibile="${datiRiga.MEARTCAT_PRZUNITPER eq 3}" modificabile='false'/>
				<gene:campoScheda campo="PRZUNITPROD"   modificabile='false'/>
				<gene:campoScheda campo="PERCIVA"   modificabile='false'/>
				<gene:campoScheda campo="QUNIMISACQ"   visibile="${datiRiga.MEARTCAT_PRZUNITPER eq 2 or datiRiga.MEARTCAT_PRZUNITPER eq 3}" modificabile='false'/>
				<gene:campoScheda campo="QUNIMISACQ"  entita="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT" visibile="${datiRiga.MEARTCAT_PRZUNITPER eq 1}" modificabile='false'/>
			</gene:gruppoCampi>		
			
			<gene:gruppoCampi idProtezioni="TEMPI" >
				<gene:campoScheda>
					<td colspan="2"><b>Tempi di consegna</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="TEMPOCONS"   modificabile='false'/>
				<gene:campoScheda campo="UNIMISTEMPOCONS"   entita="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT" modificabile='false'/>
			</gene:gruppoCampi>		
			
			<gene:gruppoCampi idProtezioni="VALIDITA" >
				<gene:campoScheda>
					<td colspan="2"><b>Validità</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="DATSCADOFF"   modificabile='false'/>
				<gene:campoScheda campo="CHKPROD"   entita="MEARTCAT" where="MEARTCAT.ID = MEISCRIZPROD.IDARTCAT" visibile='false'/>
				<gene:campoScheda campo="STATO"    obbligatorio="true"/>
				<gene:campoScheda campo="DATINS"    visibile="false"/>
				<gene:campoScheda campo="DATMOD"    visibile="false"/>
				<gene:campoScheda computed="true" nome="DATINS_CAL" campo="${gene:getDBFunction(pageContext,'DATETIMETOSTRING','MEISCRIZPROD.DATINS')}" modificabile="false" definizione="T20;0;;;DATINSMEPROD" />	
				<gene:campoScheda computed="true" nome="DATMOD_CAL" campo="${gene:getDBFunction(pageContext,'DATETIMETOSTRING','MEISCRIZPROD.DATMOD')}" modificabile="false" definizione="T20;0;;;DATMODMEPROD" />
			</gene:gruppoCampi>		
				
			
			