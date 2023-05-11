<%
	/*
	 * Created on 15-lug-2008
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

<div style="width:97%;">

<gene:template file="popup-template.jsp">
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
	</gene:redefineInsert>

	<c:choose>
		<c:when test='${RISULTATO != null}'>
			<c:set var="ngara" value='${NGARA}' />
			<c:set var="modoRichiamo" value='${MODORICHIAMO}' />
			<c:set var="numeroPrimeParimerito" value='${NUMEROPRIMEPARIMERITO}' />
			<c:set var="listaPrimeParimerito" value='${LISTAPRIMEPARIMERITO}' />
			<c:set var="primaAggiudicatariaSelezionata" value='${PRIMAAGGIUDICATARIASELEZIONATA}' />
			<c:set var="numeroParimeritoDaSelezionare" value='${NUMEROULTIMEPARIMERITODASELEZIONARE}' />
			<c:set var="listaUltimeParimerito" value='${LISTAULTIMEPARIMERITO}' />
			<c:set var="ribauoParimerito" value='${RIBAUOPARIMERITO}' />
			<c:set var="aqoper" value='${AQOPER}' />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${fn:substringAfter(param.ngara,':')}" />
			<c:set var="modoRichiamo" value='${param.modoRichiamo}' />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${ not empty param.tipoTitolo}">
			<c:set var="tipoTitolo" value="${param.tipoTitolo }"/>
		</c:when>
		<c:otherwise>
			<c:set var="tipoTitolo" value="${tipoTitolo }"/>
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${ not empty param.gestionePuneco}">
			<c:set var="gestionePuneco" value="${param.gestionePuneco }"/>
		</c:when>
		<c:otherwise>
			<c:set var="gestionePuneco" value="${gestionePuneco }"/>
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${ not empty param.sogliaDitteOepv}">
			<c:set var="sogliaDitteOepv" value="${param.sogliaDitteOepv }"/>
		</c:when>
		<c:otherwise>
			<c:set var="sogliaDitteOepv" value="${sogliaDitteOepv }"/>
		</c:otherwise>
	</c:choose>
	
	

	<c:choose>
		<c:when test="${ not empty param.ditteInGara}">
			<c:set var="ditteInGara" value="${param.ditteInGara }"/>
		</c:when>
		<c:otherwise>
			<c:set var="ditteInGara" value="${ditteInGara}"/>
		</c:otherwise>
	</c:choose>
	
	<c:set var="abilitataGestionePrezzo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1149", "1", "true")}'/>
	
	
	
	<c:if test='${modoRichiamo eq "SOGLIA"}'>
		<c:set var="isGaraDopoDLGS2016Manuale" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.IsGaraDopoDLGS2016ManualeFunction", pageContext, ngara,"true","true","false")}' />
		<c:if test="${isGaraDopoDLGS2016Manuale eq '1' or isGaraDopoDLGS2016Manuale eq '2'}">
			<c:choose>
				<c:when test="${isGaraDLGS2016 }">
					<c:set var="tipoLegge" value="DLGS2016"/>
				</c:when>
				<c:otherwise>
					<c:set var="tipoLegge" value="DLGS2017"/>
				</c:otherwise>
			</c:choose>
			<c:set var="vuoto" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetValoriCampoMETCOEFFFunction", pageContext, tipoLegge)}' />
		</c:if>
		
		<c:if test="${abilitataGestionePrezzo eq '1' }">
			<c:set var="numCriteriEcoNoPrezzo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNumCriteriEcoNoPrezzoFunction", pageContext, ngara)}' />
		</c:if>
		<c:set var="valTabA1160" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1160","1","true")}' />
		<c:choose>
			<c:when test="${valTabA1160 eq '1' }">
				<c:set var="msgTabA1160" value="con arrotondamento (solo calcoli intermedi)"/>
			</c:when>
			<c:otherwise>
				<c:set var="msgTabA1160" value="con troncamento (solo calcoli intermedi)"/>
			</c:otherwise>
		</c:choose>	
	</c:if>
	
	<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function",pageContext,ngara)}'/>
	<c:set var="offtel" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetOFFTELFunction",pageContext,codgar)}'/>
		
	
	<c:set var="modlicg" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMODLICGFunction",pageContext,ngara)}'/>
	<c:if test="${modlicg ne '0'}">
		<c:set var="ditteRibassoNullo" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteRiammesseFunction",pageContext,ngara,gestionePuneco)}'/>
		<c:if test="${ditteRibassoNullo eq 'true' and offtel eq '3'}">
			<c:set var="calcoloGradQform" value="true"/>
		</c:if>
	</c:if>
	
	<c:if test="${modlicg ne '6' and calcsoang ne '2' and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true'}">
		<c:set var="appLegRegSic" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.IsLeggeRegioneSiciliaFunction", pageContext)}' />
		<c:if test='${appLegRegSic eq "1" && (empty modo || modo eq "MODIFICA")}'>
			<c:set var="resLegRegSic" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.InitLeggeRegioneSiciliaFunction", pageContext, ngara, "No")}' />
		</c:if>
	</c:if>
		
	<gene:redefineInsert name="corpo">
		<c:set var="chiave" value="${ngara}" />
		
		<c:choose>
			<c:when test='${RISULTATO eq "CALCOLOESEGUITO"}'>
				<c:set var="modo" value="VISUALIZZA" scope="request" />	
			</c:when>
			<c:otherwise>
				<c:set var="modo" value="MODIFICA" scope="request" />
			</c:otherwise>
		</c:choose>
	
		<gene:formScheda entita="GARE" where="GARE.NGARA = '${chiave}'" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAggiudicazioneProvvisoria">
			<c:choose>
				<c:when test='${modoRichiamo eq "SOGLIA"}'>
					<c:choose>
						<c:when test="${tipoTitolo eq 'SOGLIA'}">
							<gene:setString name="titoloMaschera"
								value="Calcolo della soglia di anomalia" />
						</c:when>
						<c:otherwise>
							<gene:setString name="titoloMaschera"
								value="Calcolo graduatoria" />
						</c:otherwise>
					</c:choose>
					
				</c:when>
				<c:when test='${modoRichiamo eq "AGGIUDICAZIONE"}'>
					<gene:setString name="titoloMaschera"
						value="Calcolo aggiudicazione" />
				</c:when>
			</c:choose>
			<c:choose>
				<c:when test='${RISULTATO != null}'>
					<c:if test='${RISULTATO eq "CALCOLOESEGUITO"}'>
						<gene:campoScheda>
							<td colSpan="2"><b><gene:getString name="titoloMaschera" defaultVal=""/> completato.</b>
							<c:if test='${aqoper eq "2"}'>
								<b>Individuate ${numeroDitteAggiudicatarie } ditte prime classificate.</b>
								<c:if test="${aggSenzaAmminversa eq 'si' }">
									<br><br>
									<b>ATTENZIONE</b><br>
									<b>Per alcune delle ditte prime classificate non risulta specificato l'esito della verifica della documentazione amministrativa successiva all'apertura offerte (procedura inversa)</b>.
									<br> 
								</c:if>
							</c:if>
							<c:if test='${aqoper ne "2" and aggSenzaAmminversa eq "si"}'>
								<br><br>
								<b>ATTENZIONE</b><br>
								<b>Per la ditta prima classificata non risulta specificato l'esito della verifica della documentazione amministrativa successiva all'apertura offerte (procedura inversa)</b>.
								<br> 
							</c:if>
								<br>&nbsp;<br>
							</td>
						</gene:campoScheda>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test='${modoRichiamo eq "SOGLIA"}'>
							<c:choose>
								<c:when test="${((isGaraDLGS2017 or isGaraDLGS2016 or isGaraDL2019) and !esitoControlloDitteDLGS2016 and calcsoang eq '1') or (!empty sogliaDitteOepv and sogliaDitteOepv ne '' and tipoTitolo eq 'GRADUATORIA')}">
									 <gene:campoScheda>
									 	<td colSpan="2"><b>ATTENZIONE</b><br>
									 	<c:choose>
									 		<c:when test="${!empty sogliaDitteOepv and sogliaDitteOepv ne '' }">
									 			<c:set var="numDitte" value="${ sogliaDitteOepv}"/>
									 		</c:when>
									 		<c:otherwise>
									 			<c:set var="numDitte" value="${ sogliaNumDitte}"/>
									 		</c:otherwise>
									 	</c:choose>
									 	<b>Viene effettuato il solo calcolo graduatoria anzichè il calcolo della soglia di anomalia in quanto il numero delle offerte valide è inferiore a ${numDitte}</b>.
									 	<br>
									 	&nbsp;<br>
										&nbsp;</td>
									</gene:campoScheda>
									
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${modlicg ne '0'}">
											<c:if test="${ditteRibassoNullo eq 'true'}">
												<gene:campoScheda>
													<td colSpan="2"><b>ATTENZIONE:</b><br>
													<c:choose>
														<c:when test="${datiRiga.GARE_MODLICG eq 6}">
															<b>Ci sono ditte con punteggio non assegnato.</b> Per inserire i
															punteggi mancanti, premere "Annulla" e ritornare alla fase di
															"Apertura plichi".<br>
															Se si intende procedere nel calcolo, per tutte le ditte prive di
															punteggio non verrà considerata l'offerta.<br>											
														</c:when>
														<c:otherwise>
															<b>Ci sono ditte con ribasso non specificato. </b>
															<c:choose>
																<c:when test="${calcoloGradQform eq 'true' }">
																	<br>Con la compilazione guidata dell'offerta economica non è stato prodotto tale dato.
																	<c:choose>
																		<c:when test="${datiRiga.GARE_CALCSOANG ne 2}">
																			Pertanto non viene effettuato il calcolo della soglia di anomalia ma il solo calcolo graduatoria, basato sull'importo offerto.<br>
																		</c:when>
																		<c:otherwise>
																			Pertanto il calcolo graduatoria viene basato sull'importo offerto.<br>
																		</c:otherwise>
																	</c:choose>
																</c:when>
																<c:otherwise>
																	Per inserire i
																	ribassi mancanti, premere "Annulla" e ritornare alla fase di
																	"Apertura Offerte Economiche".<br>
																	Se si intende procedere nel calcolo, per tutte le ditte prive di
																	ribasso non verrà considerata l'offerta.<br>
																</c:otherwise>
															</c:choose>
																
															
														</c:otherwise>
													</c:choose>
													&nbsp;<br>
													&nbsp;</td>
												</gene:campoScheda>
											</c:if>
											
										</c:when>
										<c:otherwise>
											<gene:campoScheda>
												<td colSpan="2"><b>ATTENZIONE</b><br>
												<b>Non è possibile procedere perchè non risulta valorizzato il criterio di aggiudicazione.</b><br>			
												&nbsp;<br>
												&nbsp;</td>
											</gene:campoScheda>
										</c:otherwise>
									
									</c:choose>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:when test='${modoRichiamo eq "AGGIUDICAZIONE"}'>
							<c:if test='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePerAggiudicazioneFunction",pageContext,ngara) == "false"}'>
								<gene:campoScheda>
									<td colSpan="2"><b>ATTENZIONE</b><br>
									<b>Per aggiudicare la gara è necessario eseguire il
									calcolo della soglia di anomalia.</b> Premere "Annulla" e dalla
									lista premere il pulsante "Calcolo soglia anomalia" o "Calcolo graduatoria".<br>
									&nbsp;<br>
									&nbsp;</td>
								</gene:campoScheda>
							</c:if>
						</c:when>
					</c:choose>
				</c:otherwise>
			</c:choose>
												
			<c:choose>
				<c:when test="${(RISULTATO == null || empty RISULTATO ) && (empty datiRiga.GARE_IMPAPP || datiRiga.GARE_IMPAPP eq '')}">
									
					<gene:campoScheda>
						<td colSpan="2"><b>ATTENZIONE</b><br>
						<b>Non è stato specificato l'importo totale a base di gara</b>.<br>
						&nbsp;<br>
						&nbsp;</td>
					</gene:campoScheda>
				</c:when>
				<c:otherwise>
					<gene:campoScheda>
						
					</gene:campoScheda>
				</c:otherwise>
			</c:choose>
			
			<gene:campoScheda campo="CODGAR1" visibile="false" />
			<gene:campoScheda campo="NGARA" modificabile="false" />
			<gene:campoScheda campo="NGARA"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="false"/>
			<gene:campoScheda campo="TIPGARG" visibile="false" />
			<gene:campoScheda campo="MODLICG" visibile="false" />
			<gene:campoScheda campo="CRITLICG" modificabile="false" />
			<gene:campoScheda campo="ULTDETLIC" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" modificabile="false" visibile="${!empty datiRiga.GARE1_ULTDETLIC && modoRichiamo eq 'AGGIUDICAZIONE'}"/>
			<gene:campoScheda campo="AQOPER" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="false"/>
			<gene:campoScheda visibile="${!empty datiRiga.GARE1_ULTDETLIC && modoRichiamo eq 'AGGIUDICAZIONE' && gestionePuneco eq 'true'}">
				<td colSpan="2">
					<br>
					<b>ATTENZIONE:</b> Nel caso di offerta congiunta, il calcolo dell'aggiudicazione si basa sull'importo dell'offerta congiunta anzich&egrave; sul ribasso o sul punteggio.
					<br>&nbsp;<br>
				</td>
			</gene:campoScheda>
			
			<gene:campoScheda campo="CALCSOANG" visibile="false" />
			<gene:campoScheda campo="CALCSOME" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" visibile="false" />
			
			<c:choose>
				<c:when test='${modoRichiamo eq "SOGLIA"}'>
					
					<c:if test="${datiRiga.GARE_MODLICG ne 6 and datiRiga.GARE_CALCSOANG ne 2 and esitoControlloDitteDLGS2016  and calcoloGradQform ne 'true'}">
							<c:set var="num_max_decimali" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMaxNumeroCifreDecimaliFunction",pageContext,ngara) }'/>
						</c:if>
					<gene:campoScheda campo="NUM_MAX_DECIMALI" title="N.decimali massimo dei valori offerti" visibile="${datiRiga.GARE_MODLICG ne 6 and datiRiga.GARE_CALCSOANG ne 2 and esitoControlloDitteDLGS2016  and calcoloGradQform ne 'true'}" campoFittizio="true" definizione="N9;;;" modificabile="false" value='${num_max_decimali }'/>
					<gene:campoScheda campo="PRECUT" visibile="${datiRiga.GARE_MODLICG ne 6 and datiRiga.GARE_CALCSOANG ne 2 and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true'}">
						&nbsp;${msgTabA1160 }
					</gene:campoScheda>
				 	<gene:campoScheda campo="LEGREGSIC" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="${appLegRegSic eq '1' }" obbligatorio= "true" />
				 	<c:if test="${RISULTATO ne 'CALCOLOESEGUITO'}">
						<gene:campoScheda campo="FIT_NUMDITTE" title="N.Ditte con offerte valide" visibile="false" campoFittizio="true" definizione="N9;;;" modificabile="false" value='${ditteInGara}' />
				 		<gene:campoScheda campo="METSOGLIA" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="${(isGaraDopoDLGS2016Manuale eq '1' or (isGaraDopoDLGS2016Manuale eq '2' and !empty datiRiga.GARE1_METSOGLIA and (empty datiRiga.TORN_CALCSOME or datiRiga.TORN_CALCSOME eq '2' or (datiRiga.TORN_CALCSOME eq '1' and (ditteInGara >4  or (ditteInGara >= 2 and ditteInGara <= 4 and (datiRiga.GARE1_METSOGLIA eq 3 or datiRiga.GARE1_METSOGLIA eq 4))))))) and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true' and (isGaraDLGS2016 or isGaraDLGS2017) and !(RISULTATO eq 'CALCOLOESEGUITO' and datiRiga.GARE1_LEGREGSIC eq '1') }" modificabile="${isGaraDopoDLGS2016Manuale eq '1' }" obbligatorio= "true" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoMetodoCalcoloSogliaAnomalia"/>
				 	</c:if>
				 	<gene:campoScheda campo="METCOEFF" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false" />
				 	
				 	
				 	<c:if test="${isGaraDopoDLGS2016Manuale eq '1' && esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true' && RISULTATO ne 'CALCOLOESEGUITO' }">
					 	<gene:campoScheda nome="METCOEFF_FIT">
						<td class="etichetta-dato">Coefficiente per metodo E (*)</td>
						<td class="valore-dato">
						<select id="METCOEFF_FIT" name="METCOEFF_FIT" title="Coeffic.per calcolo soglia anomalia metodo E"  >
						<c:if test='${not empty listaValoriTabellatoDLGS}'>
							<option value="" title="&nbsp;" selected="selected" >&nbsp;</option>
							<c:forEach items="${listaValoriTabellatoDLGS}" var="valoreTabellato" varStatus="status" >
								<option value="${valoreTabellato[0]}" title="${valoreTabellato[1]}" <c:if test="${datiRiga.GARE1_METCOEFF eq  valoreTabellato[0]}">selected="selected"</c:if>>${valoreTabellato[1]}</option>
							</c:forEach>
						</c:if>
						</select></td>
						</gene:campoScheda>
						<gene:fnJavaScriptScheda funzione='gestioneMETSOGLIA("#GARE1_METSOGLIA#")' elencocampi='GARE1_METSOGLIA' esegui="true" />
					</c:if>
				 	<c:if test="${isGaraDopoDLGS2016Manuale eq '2' && esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true' && RISULTATO ne 'CALCOLOESEGUITO' }">
				 		<gene:campoScheda nome="METCOEFF_FIT" visibile="${ datiRiga.GARE1_METSOGLIA eq 5 && (empty datiRiga.TORN_CALCSOME or datiRiga.TORN_CALCSOME eq '2' or (datiRiga.TORN_CALCSOME eq '1' and ditteInGara >4  ))}">
						<td class="etichetta-dato">Coefficiente per metodo E</td>
						<td class="valore-dato">
							<c:if test='${datiRiga.GARE1_METSOGLIA eq 5 and not empty listaValoriTabellatoDLGS}'>
								<c:forEach items="${listaValoriTabellatoDLGS}" var="valoreTabellato" varStatus="status" >
										<c:if test="${valoreTabellato[0] eq  datiRiga.GARE1_METCOEFF}">${valoreTabellato[1]}</c:if>
								</c:forEach>
							</c:if>
						</td>
						</gene:campoScheda>
				 	</c:if>
				 	
				 	<gene:campoScheda campo="RIPTEC" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false"  />
				 	<gene:campoScheda campo="RIPECO" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false"  />
				 	<gene:campoScheda campo="METPUNTI" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="${datiRiga.GARE_MODLICG eq 6 and datiRiga.GARE_CALCSOANG eq '1' and (datiRiga.GARE1_RIPTEC eq 1 or datiRiga.GARE1_RIPTEC eq 2 or datiRiga.GARE1_RIPECO eq 1 or datiRiga.GARE1_RIPECO eq 2) and tipoTitolo ne 'GRADUATORIA' }" obbligatorio= "true" />
				 	
				 	<gene:campoScheda campo="MODASTG" visibile="${datiRiga.GARE_MODLICG ne 6 and (datiRiga.GARE_CALCSOANG ne 2 and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true') }" modificabile="false"/>
				 	
				 	<c:if test="${RISULTATO ne 'CALCOLOESEGUITO' }">
				 	
			 			<gene:fnJavaScriptScheda funzione='gestioneVisualizzazioneCampiDaLegregsic("#GARE1_LEGREGSIC#")' elencocampi='GARE1_LEGREGSIC' esegui="true" />
			 		
				 		<c:if test="${not empty initEscauto and calcoloGradQform ne 'true' }">
				 			<gene:campoScheda campo="ESCAUTOFIT" title="Esclusione" modificabile="true" definizione="N1" campoFittizio="true" visibile="false"  />
				 			<c:choose>
				 				<c:when test="${initEscauto eq '1/3' }">
				 					<gene:campoScheda nome="ESCLUSIONE_FIT" >
						 				<td class="etichetta-dato"></td>
						 				<td class="valore-dato">
							 				<b>La gara ha importo inferiore alla soglia comunitaria e il numero delle offerte ammesse è inferiore a 10.</b>
							 				<br>Ai sensi della L.120/2020, per tutte le procedure indette entro il 30/06/2023 il numero minimo di offerte presentate per l'applicazione dell'esclusione automatica è pari a 5 (in deroga a quanto previsto dal Codice Appalti).
							 				<br>Selezionare un'opzione:
							 				<br>
							 				<input type="radio" value="1" name="escautoInit" id="esclusioneSi" checked="checked" />
							 				Applica esclusione automatica, come da L.120/2020<br>
							 				<input type="radio" value="3" name="escautoInit" id="esclusioneNo"  />
							 				Non applicare esclusione automatica
						 				</td>
						 			</gene:campoScheda>
				 				</c:when>
				 				<c:when test="${initEscauto eq '2' }">
				 					<gene:campoScheda nome="ESCLUSIONE_FIT" >
						 				<td class="etichetta-dato"></td>
							 				<td class="valore-dato"> <b>La gara ha importo inferiore alla soglia comunitaria e il numero delle offerte ammesse è superiore o uguale a 10.</b>
								 				<br>Viene pertanto applicata l'esclusione automatica delle offerte anomale.
								 				<input type="radio" value="2" name="escautoInit" id="esclusioneSi" checked="checked"  style="display: none;"/>
							 				</td>
						 			</gene:campoScheda>
				 				</c:when>
				 				<c:when test="${initEscauto eq '5' }">
				 					<gene:campoScheda nome="ESCLUSIONE_FIT" >
						 				<td class="etichetta-dato"></td>
							 				<td class="valore-dato"> <b>E' previsto il calcolo soglia con i metodi ex DLgs.56/2017.</b>
							 				<br>Viene pertanto applicata l'esclusione automatica delle offerte anomale.
								 				<input type="radio" value="5" name="escautoInit" id="esclusioneSi" checked="checked"  style="display: none;"/>
							 				</td>
						 			</gene:campoScheda>
				 				</c:when>
				 				<c:otherwise>
				 					<gene:campoScheda nome="ESCLUSIONE_FIT" >
						 				<td class="etichetta-dato"></td>
						 				<td class="valore-dato"> <b>La gara ha importo superiore alla soglia comunitaria.</b>
							 				<br>Non può essere applicata l'esclusione automatica delle offerte anomale.
							 				<input type="radio" value="4" name="escautoInit" id="esclusioneNo" checked="checked"  style="display: none;"/>
						 				</td>
						 			</gene:campoScheda>
				 				</c:otherwise>
				 			</c:choose>
				 			
				 		</c:if>
				 	</c:if>				 	
				 	
				</c:when>
				<c:when test='${modoRichiamo eq "AGGIUDICAZIONE"}'>
					<gene:campoScheda campo="PRECUT" visibile="false" />
					<gene:campoScheda campo="LEGREGSIC" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false" />
				</c:when>
			</c:choose>

			<gene:campoScheda campo="DITTAP" visibile="false" />
			<gene:campoScheda campo="RIBPRO" visibile="false" />
			<gene:campoScheda campo="IAGPRO" visibile="false" />
			<gene:campoScheda campo="IMPGAR" visibile="false" />
			<gene:campoScheda campo="DITTA" visibile="false" />
			<gene:campoScheda campo="NOMIMA" visibile="false" />
			<gene:campoScheda campo="RIBAGG" visibile="false" />
			<gene:campoScheda campo="IAGGIU" visibile="false" />
			
			<gene:campoScheda campo="IMPAPP" visibile="false" />
			
			<gene:campoScheda campo="FASGAR" visibile="false" />
			<gene:campoScheda campo="STEPGAR" visibile="false" />
			<gene:campoScheda campo="LIMMIN" visibile="false" />
						
			<c:choose>
				<c:when test='${modoRichiamo eq "SOGLIA" && RISULTATO eq "CALCOLOESEGUITO"}'>
					<c:choose>
						<c:when test="${datiRiga.GARE1_SOGLIANORMA eq 'DL32_2019_S' or datiRiga.GARE1_SOGLIANORMA eq 'LR13_2019'}">
							<c:set var="titoloSommarib" value="Somma ribassi offerte mediate" />
						</c:when>
						<c:when test="${descrizioneA1132 eq '1' }">
							<c:set var="titoloSommarib" value="Somma ribassi offerte valide" />
						</c:when>
						<c:otherwise>
							<c:set var="titoloSommarib" value="Somma ribassi offerte mediate" />
						</c:otherwise>
					</c:choose>
					<gene:gruppoCampi idProtezioni="AGG">
						<gene:campoScheda>
							<td colspan="2"><br>&nbsp;<br><b>Sintesi aggiudicazione</b></td>
						</gene:campoScheda>
						<gene:campoScheda campo="METSOGLIA" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="${(isGaraDLGS2016 or isGaraDLGS2017) and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true' and datiRiga.GARE1_LEGREGSIC ne '1' }"  />
						<gene:campoScheda campo="NOFVAL" modificabile="false" />
						<gene:campoScheda title="Numero offerte accantonate per taglio delle ali" computed = "true" campo="(NOFVAL - NOFMED)" definizione="N24.5;" visibile="${datiRiga.GARE_MODLICG ne 6 and calcsoang eq '1' and calcoloGradQform ne 'true' and ((esitoControlloDitteDLGS2016  and (isGaraDLGS2016 || isGaraDLGS2017)) || (controlloDitteNormativaPrecedente and !isGaraDLGS2016 and !isGaraDLGS2017)) and (empty datiRiga.GARE1_METSOGLIA || (datiRiga.GARE1_METSOGLIA ne 4 && datiRiga.GARE1_METSOGLIA ne 3) || isGaraDL2019)}">
							&nbsp;&nbsp;
							<c:if test="${!empty datiRiga.GARE1_NOFALASUP}">
								:&nbsp;&nbsp;&nbsp;${datiRiga.GARE1_NOFALASUP} offerte più alte <c:if test="${!empty datiRiga.GARE1_NOFALAINF}">,</c:if>
							</c:if>
							<c:if test="${!empty datiRiga.GARE1_NOFALAINF}">
								<c:if test="${empty datiRiga.GARE1_NOFALASUP}">:&nbsp;&nbsp;&nbsp;</c:if>${datiRiga.GARE1_NOFALAINF} offerte più basse
							</c:if>
						</gene:campoScheda>
						<gene:campoScheda campo="NOFMED" visibile="${datiRiga.GARE_MODLICG ne 6 and datiRiga.GARE1_METSOGLIA ne 3 and datiRiga.GARE1_METSOGLIA ne 4 }" modificabile="false" />
						<gene:campoScheda campo="MEDIA" visibile="${datiRiga.GARE_MODLICG ne 6 and !(datiRiga.GARE1_METSOGLIA eq 4 and isGaraDLGS2016) }" modificabile="false" />			
						<gene:campoScheda campo="MEDIASCA" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" modificabile="false" visibile="${(datiRiga.GARE_MODLICG eq 13 or datiRiga.GARE_MODLICG eq 14) and datiRiga.GARE1_SOGLIANORMA ne 'LR13_2019' and (empty datiRiga.GARE1_METSOGLIA || datiRiga.GARE1_METSOGLIA eq 1 || datiRiga.GARE1_METSOGLIA eq 5) and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true'}"/>
						<gene:campoScheda campo="METCOEFF_VIS" campoFittizio="true" definizione="F1.1;0;;;G1METCOEFF"  value='${datiRiga.GARE1_METCOEFF}' modificabile="false" visibile="${(datiRiga.GARE_MODLICG eq 13 or datiRiga.GARE_MODLICG eq 14) and (isGaraDLGS2016 || isGaraDLGS2017) and datiRiga.GARE1_METSOGLIA eq 5 and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true'}"/>
						<gene:campoScheda campo="SOGLIA1"  entita="GARE1" where="GARE.NGARA = GARE1.NGARA" modificabile="false" visibile="${datiRiga.GARE1_SOGLIANORMA eq 'DL32_2019_S' and datiRiga.GARE_NOFVAL >= 15 }"/>
						<gene:campoScheda campo="SOMMARIB" title="${titoloSommarib }" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" modificabile="false" visibile="${((datiRiga.GARE_MODLICG eq 13 or datiRiga.GARE_MODLICG eq 14) and datiRiga.GARE1_METSOGLIA eq 2 and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true') or (datiRiga.GARE1_SOGLIANORMA eq 'DL32_2019_S' and datiRiga.GARE_NOFVAL >= 15) or datiRiga.GARE1_SOGLIANORMA eq 'LR13_2019' }"/>
						<gene:campoScheda campo="MEDIAIMP" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" modificabile="false" visibile="${(datiRiga.GARE_MODLICG eq 13 or datiRiga.GARE_MODLICG eq 14) and datiRiga.GARE1_METSOGLIA eq 4 and isGaraDLGS2016 }"/>
						<gene:campoScheda campo="SOGLIAIMP" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" modificabile="false" visibile="${(datiRiga.GARE_MODLICG eq 13 or datiRiga.GARE_MODLICG eq 14) and datiRiga.GARE1_METSOGLIA eq 4 and isGaraDLGS2016 }"/>
						<gene:campoScheda campo="SOGLIAVAR"  entita="GARE1" where="GARE.NGARA = GARE1.NGARA" modificabile="false" visibile="${datiRiga.GARE1_SOGLIANORMA eq 'DL32_2019_S' and datiRiga.GARE_NOFVAL >= 15 }"/>
						<gene:campoScheda campo="MEDIARAP"  entita="GARE1" where="GARE.NGARA = GARE1.NGARA" modificabile="false" visibile="${datiRiga.GARE1_SOGLIANORMA eq 'DL32_2019_S' and datiRiga.GARE_NOFVAL < 15 }"/>
						<gene:campoScheda campo="LIMMAX"  visibile="${(datiRiga.GARE_MODLICG eq 13 or datiRiga.GARE_MODLICG eq 14) and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true'}" modificabile="false"/>
						<gene:campoScheda campo="NOFALAINF" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false"/>
						<gene:campoScheda campo="NOFALASUP" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false"/>
						<gene:campoScheda campo="SOGLIANORMA" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="false"/>
						<gene:campoScheda campo="ESCAUTO" entita="GARE1" where="GARE.NGARA = GARE1.NGARA" visibile="${datiRiga.GARE_CALCSOANG eq 1 and datiRiga.GARE_MODASTG eq 1 and esitoControlloDitteDLGS2016 and calcoloGradQform ne 'true'}"/>
					</gene:gruppoCampi>	
				</c:when>
				<c:otherwise>
					<gene:campoScheda campo="NOFVAL" visibile="false" />
					<gene:campoScheda campo="NOFMED" visibile="false" />
					<gene:campoScheda campo="MEDIA" visibile="false" />
					<gene:campoScheda campo="LIMMAX" visibile="false" />
				</c:otherwise>
			</c:choose>
		
			<gene:campoScheda campo="ONPRGE" visibile="false" />
			<gene:campoScheda campo="IMPNRL" visibile="false" />
			<gene:campoScheda campo="INVERSA" entita="TORN" where="GARE.CODGAR1=TORN.CODGAR" visibile="false" />

			<gene:campoScheda campo="MODORICHIAMO" title="Modo richiamo"
				modificabile="false" value='${modoRichiamo}'
				definizione="T100" campoFittizio="true" visibile="false" />
			
			<gene:campoScheda campo="PRIMAAGGIUDICATARIASELEZIONATA" title="Prima classificata"
				modificabile="false" value='${primaAggiudicatariaSelezionata}' 
				definizione="T10" campoFittizio="true" visibile="false" />
				
			
				
			<gene:campoScheda campo="ULTIMEAGGIUDICATARIESELEZIONATE" title="Ultima classificata"
				modificabile="false" definizione="T10" campoFittizio="true" visibile="false" />
			
			<gene:campoScheda campo="LEGREGSICVISIBILE" title="visibilita legregsic"
				modificabile="false" definizione="T10" campoFittizio="true" visibile="false"  value="${appLegRegSic}"/>				
							
			<gene:campoScheda campo="RIBAUOPARIMERITO" title="Ribasso parimerito"
				modificabile="false" definizione="F10.10" campoFittizio="true" visibile="false"  value="${ribauoParimerito}"/>	
				
			<input type="hidden" id="tipoTitolo" name="tipoTitolo" value="${tipoTitolo }"/>
			<input type="hidden" id="gestionePuneco" name="gestionePuneco" value="${gestionePuneco }"/>
			
			<gene:campoScheda campo="SORTEGGIOPARIMERITO" title="Sorteggio parimerito"
				modificabile="false" definizione="T10" campoFittizio="true" visibile="false" />
			
			<gene:campoScheda campo="LISTAPARIMERITO" title="lista parimerito"
				modificabile="false" definizione="T2000" campoFittizio="true" visibile="false" />
			
			<gene:campoScheda campo="NUMEROULTIMEPARIMERITODASELEZIONARE" title="Numero ultime parimerito da selezionare"
				modificabile="false" definizione="T3" campoFittizio="true" visibile="false" value="${NUMEROULTIMEPARIMERITODASELEZIONARE }"/>
				
			<gene:campoScheda campo="calcoloGradQform" title="Calcolo graduatoria per ribasso nullo"
				modificabile="false" definizione="T5" campoFittizio="true" visibile="false" value="${calcoloGradQform }"/>
						
			<input type="hidden" id="isGaraDLGS2016" name="isGaraDLGS2016" value="${isGaraDLGS2016 }"/>
			<input type="hidden" id="isGaraDLGS2017" name="isGaraDLGS2017" value="${isGaraDLGS2017 }"/>
			<input type="hidden" id="isGaraDL2019" name="isGaraDL2019" value="${isGaraDL2019 }"/>
			<input type="hidden" id="esitoControlloDitteDLGS2016" name="esitoControlloDitteDLGS2016" value="${esitoControlloDitteDLGS2016 }"/>
			<input type="hidden" id="sogliaDitteOepv" name="sogliaDitteOepv" value="${sogliaDitteOepv }"/>
			<input type="hidden" id="escauto" name="escauto" value=""/>
			
						
			<c:choose>
				<c:when test='${RISULTATO eq "PRIMEPARIMERITO" || RISULTATO eq "ULTIMEPARIMERITO"}'>
				
				</c:when>
			
				<c:when test='${RISULTATO eq "CALCOLOESEGUITO"}'>
					<c:if test='${modoRichiamo eq "SOGLIA"}'>
						<gene:campoScheda>
							<td class="comandi-dettaglio" colSpan="2">
								<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
							</td>
						</gene:campoScheda>
					</c:if>
				</c:when>
				
				<c:otherwise>
					
					<gene:campoScheda>
						<td class="comandi-dettaglio" colSpan="2">
							<c:choose>
								<c:when test='${modoRichiamo eq "SOGLIA" && modlicg ne "0"}'>
									<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
								</c:when>
								<c:when test='${modoRichiamo eq "AGGIUDICAZIONE"}'>
									<c:if test='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePerAggiudicazioneFunction",pageContext,ngara)}'>
										<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">
									</c:if>
								</c:when>
							</c:choose>
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					</gene:campoScheda>
				</c:otherwise>
			</c:choose>
			
			<c:if test='${modoRichiamo eq "AGGIUDICAZIONE"}'>
				
				<c:if test='${numeroPrimeParimerito == 1}'>
					<gene:campoScheda>
						<td colspan="2"><b><br>&nbsp;<br>Prima classificata</b></td>
					</gene:campoScheda>
					<gene:campoScheda campo="CODGAR5" entita="DITG" where="DITG.NGARA5 = '${ngara}' AND DITG.DITTAO IN (${listaPrimeParimerito})" visibile="false" />
					<gene:campoScheda campo="DITTAO"  entita="DITG" where="DITG.NGARA5 = '${ngara}' AND DITG.DITTAO IN (${listaPrimeParimerito})" modificabile="false"/>
					<gene:campoScheda campo="NGARA5" entita="DITG" where="DITG.NGARA5 = '${ngara}' AND DITG.DITTAO IN (${listaPrimeParimerito})" visibile="false" />
					<gene:campoScheda campo="NOMIMO" entita="DITG" where="DITG.NGARA5 = '${ngara}' AND DITG.DITTAO IN (${listaPrimeParimerito})" modificabile="false"/>
				</c:if>	
				
			
				<c:choose>
					<c:when test='${RISULTATO eq "PRIMEPARIMERITO" }'>
						<gene:campoScheda >
							<jsp:include page="gare-popup-parimerito-interno.jsp" />
						</gene:campoScheda>
					</c:when>
					<c:when test='${ RISULTATO eq "ULTIMEPARIMERITO"}'>
						<gene:campoScheda >
							<jsp:include page="gare-popup-ultimeParimerito-interno.jsp" />
						</gene:campoScheda>
					</c:when>
				</c:choose>
				
					<gene:campoScheda campo="SELPAR"  title="Criterio selezione ditte pari merito" entita="TORN" where="TORN.CODGAR = GARE.CODGAR1" visibile ='${RISULTATO eq "PRIMEPARIMERITO" || RISULTATO eq "ULTIMEPARIMERITO"}' modificabile="false">
						<c:if test="${selpar eq 1 }">
							<span style="float: right;">
					 			<a href="javascript:apriDocumento('${pageContext.request.contextPath}/doc/sorteggioParimeritoAggiudicazione.pdf');" title="Consulta manuale" style="color:#002E82;">
					 				<img width="16" height="16" title="Consulta manuale" alt="Consulta manuale" src="${pageContext.request.contextPath}/img/consultazioneManuale.png"/> Consulta manuale
					 			</a>
					 		</span>
						</c:if>
						
					</gene:campoScheda>
					<gene:campoScheda campo="NOTPROV"  entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile ='${RISULTATO eq "PRIMEPARIMERITO" ||  RISULTATO eq "ULTIMEPARIMERITO"}'/>	
				<c:if test='${RISULTATO eq "PRIMEPARIMERITO" ||  RISULTATO eq "ULTIMEPARIMERITO"}'>	
					<gene:campoScheda>
					
						<td class="comandi-dettaglio" colSpan="2">
							<c:choose>
							<c:when test='${(numeroParimeritoDaSelezionare > 0 || numeroPrimeParimerito > 1 ) && selpar eq 1}'>
								<INPUT type="button" class="bottone-azione" value="Sorteggia e conferma" title="Sorteggia e conferma" onclick="javascript:sorteggiaParimerito();">
							</c:when>
							<c:when test='${numeroPrimeParimerito > 1}'>
								<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:confermaprimaparimerito();">
							</c:when>
							<c:when test='${numeroParimeritoDaSelezionare > 0}'>
								<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:confermaultimaparimerito();">
							</c:when>
							</c:choose>
							<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					
					</gene:campoScheda>
				</c:if>
				<c:if test='${RISULTATO eq "CALCOLOESEGUITO" && aqoper eq "2"}'>
					<gene:campoScheda>
						<td class="comandi-dettaglio" colSpan="2">
							<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					</gene:campoScheda>
				</c:if>
			</c:if>
			
			
		</gene:formScheda>
		
		<c:if test='${modoRichiamo eq "AGGIUDICAZIONE"}'>
			<c:if test='${RISULTATO eq "CALCOLOESEGUITO" && aqoper ne "2"}'>
				<jsp:include page="gare-popup-ditte-aggiudicatarie.jsp" />
			</c:if>	
		</c:if>

	</gene:redefineInsert>
	<gene:javaScript> 
	
		document.forms[0].jspPathTo.value="gare/gare/gare-popup-aggiudicazione-provvisoria.jsp";
	
    	<c:if test='${RISULTATO eq "CALCOLOESEGUITO"}'>
			window.opener.listaVaiAPagina(window.opener.document.forms[0].pgCorrente.value);
			//window.close();
		</c:if>
		
		showObj("jsPopUpGARE_CODGAR1", false);
		showObj("jsPopUpGARE_NGARA", false);
		showObj("jsPopUpGARE_TIPGARG", false);
		showObj("jsPopUpGARE_MODLICG", false);
		showObj("jsPopUpGARE_PRECUT", false);
		showObj("jsPopUpGARE_CRITLICG", false);
		showObj("jsPopUpGARE1_ULTDETLIC", false);
		
		showObj("jsPopUpGARE_NOFVAL", false);
		showObj("jsPopUpGARE_NOFMED", false);
		showObj("jsPopUpGARE_MEDIA", false);
		showObj("jsPopUpGARE_LIMMAX", false);

		showObj("jsPopUpDITG_DITTAO", false);
		showObj("jsPopUpDITG_NOMIMO", false);
		showObj("jsPopUpGARE1_METSOGLIA", false);
		showObj("jsPopUpGARE_MODASTG", false);
		
		function annulla(){
			window.close();
		}
						
		var modlicg = getValue("GARE_MODLICG");
		if(modlicg!='13' && modlicg!='14' && modlicg!="0"){
			showObj("rowGARE1_METSOGLIA", false);
			showObj("rowMETCOEFF_FIT", false);
		}
		
		function conferma(){
			<c:if test="${not empty initEscauto}">
				var init = $('input[name="escautoInit"]:checked').val();
				$('#ESCAUTOFIT').val(init);
			</c:if>
			<c:if test='${modoRichiamo eq "SOGLIA" && isGaraDopoDLGS2016Manuale eq "1" && esitoControlloDitteDLGS2016 and calcoloGradQform ne "true"}'>
				var legregsic = getValue("GARE1_LEGREGSIC");
				if(legregsic==1){
					setValue("GARE1_METSOGLIA","");
					setValue("GARE1_METCOEFF","");
				}else{
					var metsoglia = getValue("GARE1_METSOGLIA");
					var metcoff_fit =  getValue("METCOEFF_FIT"); 
					if(metsoglia==5 && (metcoff_fit==null || metcoff_fit =="")){
						clearMsg();
						outMsg("Il campo \"Coefficiente per calcolo metodo E\" è obbligatorio","ERR");
						onOffMsgFlag(true);
						return;
					}
					setValue("GARE1_METCOEFF", getValue("METCOEFF_FIT"));
				}
				
			</c:if>
			<c:if test='${modoRichiamo eq "SOGLIA" and RISULTATO ne "CALCOLOESEGUITO" and abilitataGestionePrezzo eq "1" and numCriteriEcoNoPrezzo ne "0"}'>
				if( $('#GARE1_METPUNTI').is(':visible') ) {
					var metpunti=getValue("GARE1_METPUNTI");
					if(metpunti=="2"){
						clearMsg();
						outMsg("Essendo stati definiti dei criteri di valutazione economici non relativi al prezzo ai fini del calcolo soglia anomalia, non è possibile impostare il calcolo soglia anomalia sui punteggi riparametrati","ERR");
						onOffMsgFlag(true);
						return;
					}
				}
			</c:if>
			schedaConferma();
		}
		
				
		function confermaprimaparimerito(){
			if (document.forms[0].prima) {
				var controllo = false;
				var radio = document.forms[0].prima;
				for(i=0; i < radio.length; i++) {
 					if(radio[i].checked) {
   						controllo=true;
   						document.forms[0].PRIMAAGGIUDICATARIASELEZIONATA.value=radio[i].value;
						break;
 					}
				}
	
				if(!controllo) {
	 				alert("Deve essere selezionata una ditta.");
				}
				
				if (controllo) {
					schedaConferma();
				}
			}
		}
		
				
		function confermaultimaparimerito(){
			var aggiudicatarieSelezionate=0;
			var numRighe = "${datiRiga.rowCount }";
			var numeroParimeritoDaSelezionare = "${numeroParimeritoDaSelezionare }";
			var elencoDitteSelezionate="";
			for(i=1;i<=numRighe;i++){
				if(document.getElementById('dittaDefinitiva' + i).checked){
					aggiudicatarieSelezionate++;
					if(elencoDitteSelezionate!="")
						elencoDitteSelezionate+=",";
					elencoDitteSelezionate += document.getElementById('dittaDefinitiva' + i).value;
				}
			}
			if(aggiudicatarieSelezionate == 0){
				alert("Devono essere selezionate le ditte dalla lista.");
			}else if(aggiudicatarieSelezionate != numeroParimeritoDaSelezionare && numeroParimeritoDaSelezionare==1){
				alert("Deve essere selezionata " + numeroParimeritoDaSelezionare + " ditta dalla lista.");
			}else if(aggiudicatarieSelezionate != numeroParimeritoDaSelezionare && numeroParimeritoDaSelezionare!=1){
				alert("Devono essere selezionate " + numeroParimeritoDaSelezionare + " ditte dalla lista.");
			}else{
				document.forms[0].ULTIMEAGGIUDICATARIESELEZIONATE.value=elencoDitteSelezionate;
				schedaConferma();
			}
		}
		
				
		<c:if test='${resLegRegSic eq "0" and modoRichiamo eq "SOGLIA"}'>
			setValue("GARE1_LEGREGSIC","${requestScope.initLegRegSic}");
			gestioneVisualizzazioneCampiDaLegregsic("${requestScope.initLegRegSic}");
		</c:if>
		
		function gestioneMETSOGLIA(metsoglia){
			if(metsoglia==5)
				showObj("rowMETCOEFF_FIT", true);
			else{
				showObj("rowMETCOEFF_FIT", false);
				//document.forms[0].GARE1_METCOEFF.value='';
				setValue("METCOEFF_FIT","");
			}
		}
		
		function gestioneVisualizzazioneCampiDaLegregsic(valore){
			if(valore==1){
				showObj("rowGARE1_METSOGLIA", false);
				showObj("rowMETCOEFF_FIT", false);
			}else{
				if(modlicg==13 || modlicg==14){
					showObj("rowGARE1_METSOGLIA", true);
					var metsoglia = getValue("GARE1_METSOGLIA");
					if(metsoglia==5)
						showObj("rowMETCOEFF_FIT", true);
				}
			}
			
		}
		
		
		function sorteggiaParimerito(){
			
			var tipoSorteggio=0;
			var listaParimerito="";
			
			var numeroPrimeParimerito = "${NUMEROPRIMEPARIMERITO}";
			if(numeroPrimeParimerito!= null && numeroPrimeParimerito!="")
				numeroPrimeParimerito=parseInt(numeroPrimeParimerito);
			else
				numeroPrimeParimerito=parseInt(0);
			
						
			var numeroParimeritoDaSelezionare = "${NUMEROULTIMEPARIMERITODASELEZIONARE}";
			if(numeroParimeritoDaSelezionare!= null && numeroParimeritoDaSelezionare!="")
				numeroParimeritoDaSelezionare=parseInt(numeroParimeritoDaSelezionare);
			else
				numeroParimeritoDaSelezionare=parseInt(0);
				
			if(numeroPrimeParimerito>1){
				tipoSorteggio=1;
				listaParimerito="${LISTAPRIMEPARIMERITO}";
			}else{
				tipoSorteggio=3;
				listaParimerito="${LISTAULTIMEPARIMERITO}";
			}
				
			document.forms[0].SORTEGGIOPARIMERITO.value=tipoSorteggio;
			document.forms[0].LISTAPARIMERITO.value=listaParimerito;
			schedaConferma();
		}
	<c:if test='${modoRichiamo eq "SOGLIA" and RISULTATO ne "CALCOLOESEGUITO"}'>
		if( $('#GARE1_METPUNTI').is(':visible') ) {
			var metpunti=getValue("GARE1_METPUNTI");
			if(metpunti==null || metpunti=="")
				setValue("GARE1_METPUNTI","1");
		}else{
			setValue("GARE1_METPUNTI","");
		} 
	</c:if>	
	
	</gene:javaScript>
</gene:template>

</div>