
<%
  /*
			 * Created on 03-09-2014
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

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<c:set var="stile" value="width:60%; HEIGHT: 22px; PADDING-RIGHT: 10px; BORDER-TOP: white 1px solid; BACKGROUND-COLOR: #efefef; color:#000000; TEXT-ALIGN: right;"/>

<div style="width: 97%;"><gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:setString name="titoloMaschera" value='Dettaglio quantità richiesta' />

	<gene:redefineInsert name="corpo">
		<table class="dettaglio-notab">
			<td class="valore-dato" colspan="2">
				<br>
				Per questa tipologia di articolo la quantit&agrave; richiesta viene espressa dettagliando le sue due componenti.<br>
				<c:if test="${modo eq 'MODIFICA'}">
					Indicare, oltre alla quantit&agrave;, anche una descrizione per ognuna delle due componenti.
					<br>(Es.<i>Unit&agrave; di misura su cui &egrave; espresso il prezzo = 'Camera per Notte': 
					riportare nella prima componente 'Camera' e nella seconda 'Notte'</i>)
					<br>
				</c:if>
				<br>
			</td>
		</table>
		<gene:formScheda entita="MERICART"  gestisciProtezioni="true"  >
			<gene:campoScheda campo="DESDET1" visibile="false"/>
			<gene:campoScheda campo="QUADET1" visibile="false"/>
			<gene:campoScheda campo="DESDET2"  visibile="false"/>
			<gene:campoScheda campo="QUADET2"  visibile="false"/>
			<gene:campoScheda campo="QUANTI"  visibile="false"/>
			<gene:campoScheda campo="UNIMISPRZ"  entita="MEARTCAT" where="MEARTCAT.ID=MERICART.IDARTCAT" visibile="false"/>
			<gene:campoScheda addTr="false">
			<table class="dettaglio-notab">
				<tr>
					<td style="${stile}" >Unit&agrave; di misura su cui &egrave; espresso il prezzo</td>
					<td class="valore-dato">
						<c:set var="descUniMis" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetDescrizioneTabellatoFunction", pageContext,"ME007" ,datiRiga.MEARTCAT_UNIMISPRZ)}' />
						${descUniMis }
					</td>
				</tr>
				<tr>
					<td style="${stile}" >
						<c:choose>
							<c:when test="${modo eq 'MODIFICA'}">
								Prima voce che compone l'unit&agrave; di misura:<br>Descrizione<br><input type="text" name="DESDET1" id="DESDET1"  maxlength="30" size="30" enabled class="testo" value="${datiRiga.MERICART_DESDET1 }"/>*
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${!empty datiRiga.MERICART_DESDET1 }">
										${datiRiga.MERICART_DESDET1 }
									</c:when>
									<c:otherwise>
										Prima componente quantit&agrave;<br>
									</c:otherwise>
								</c:choose>
								
							</c:otherwise>
						</c:choose>	
					</td>
					<td class="valore-dato">
						<c:choose>
							<c:when test="${modo eq 'MODIFICA'}">
								<br>Quantit&agrave;<br><input type="text" name="QUADET1" id="QUADET1" size="15" enabled class="numero" title="Quantit&agrave; 1"  onchange="javascript:aggiornaTotale(this,'QUADET1');" value="${datiRiga.MERICART_QUADET1 }"/>
							</c:when>
							<c:otherwise>
								${datiRiga.MERICART_QUADET1 }
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>	
					<td style="${stile}" >
						<c:choose>
							<c:when test="${modo eq 'MODIFICA'}">
							 Seconda voce che compone l'unit&agrave; di misura:<br>Descrizione<br><input type="text" name="DESDET2" id="DESDET2"  maxlength="30" size="30" enabled class="testo" value="${datiRiga.MERICART_DESDET2 }"/>*
							 </c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${!empty datiRiga.MERICART_DESDET2 }">
										${datiRiga.MERICART_DESDET2 }
									</c:when>
									<c:otherwise>
										Seconda componente quantit&agrave;<br>
									</c:otherwise>
								</c:choose>
								
							</c:otherwise>
						</c:choose>	
					</td>
					<td class="valore-dato">
						<c:choose>
							<c:when test="${modo eq 'MODIFICA'}">
								<br>Quantit&agrave;<br><input type="text" name="QUADET2" id="QUADET2" size="15" enabled class="numero" title="Quantit&agrave; 2" onchange="javascript:aggiornaTotale(this,'QUADET2');" value="${datiRiga.MERICART_QUADET2 }"/>
							</c:when>
							<c:otherwise>
								${datiRiga.MERICART_QUADET2 }
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>	
					<td style="${stile}" >Quantit&agrave; totale</td>
					<td class="valore-dato">
						<c:choose>
							<c:when test="${modo eq 'MODIFICA'}">
								<input type="text" name="TOT" id="TOT" size="15" class="importoNoEdit" readOnly="readOnly" title="Quantit&agrave; totale" disabled value="${datiRiga.MERICART_QUANTI }"/>
							</c:when>
							<c:otherwise>
								${datiRiga.MERICART_QUANTI }
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="comandi-dettaglio">
						<c:choose>
							<c:when test="${modo eq 'MODIFICA'}">
								<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma"	onclick="javascript:conferma();">
								<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">
							</c:when>
							<c:otherwise>
								<INPUT type="button" class="bottone-azione" value="Esci"	title="Esci" onclick="javascript:annulla();">
							</c:otherwise>
						</c:choose>
						
					</td>
				</tr>
				</table>
			</gene:campoScheda>
		</gene:formScheda>
	</gene:redefineInsert>

	<gene:javaScript>
		
		function annulla(){
			window.close();
		}
		
		function conferma(){
			var desdet1 = document.getElementById("DESDET1").value;
			var desdet2 = document.getElementById("DESDET2").value;
			var quadet1 = document.getElementById("QUADET1").value;
			var quadet2 = document.getElementById("QUADET2").value;
			if(desdet1==null || desdet1=="" || desdet2==null || desdet2=="" || quadet1==null || quadet1=="" || quadet2==null || quadet2==""){
				alert("Tutti i campi devono essere valorizzati");
				return;
			}
			var tot = document.getElementById("TOT").value;
			var winOpener = window.opener;
			winOpener.setValue("MERICART_DESDET1_${param.indiceRiga}",desdet1);
			winOpener.setValue("MERICART_DESDET2_${param.indiceRiga}",desdet2);
			winOpener.setValue("MERICART_QUADET1_${param.indiceRiga}",quadet1);
			winOpener.setValue("MERICART_QUADET2_${param.indiceRiga}",quadet2);
			winOpener.setValue("MERICART_QUANTI_${param.indiceRiga}",tot);
			window.close();
		}
		
		/*
		//var nomeForm = document.forms[0].name
		localform=new FormObj(document.forms[0]);
		localform.setTipo("QUADET1","F");
		localform.setTipo("QUADET2","F");
		*/
		
		<c:if test="${modo eq 'MODIFICA'}">
			function aggiornaTotale(obj, campo){
				if(!checkFloat(obj, "Valore non consentito",null)){
					alert("Attenzione:\nSi e' inserito un valore di campo non consentito: i caratteri ammessi sono le cifre ed il punto come separatore decimale");
					obj.value="";
					document.getElementById("TOT").value="";
					return;
				}
				if(obj.value<=0){
					alert("Attenzione:\nLa quantità deve essere maggiore di 0");
					obj.value="";
					document.getElementById("TOT").value="";
					return;
				}
				var quantitaNew = obj.value;
				quantitaNew = round(parseFloat(quantitaNew), 5)
				obj.value = quantitaNew;
				var quantita;
				if(campo == "QUADET1"){
					quantita= document.getElementById("QUADET2").value;
				}else{
					quantita= document.getElementById("QUADET1").value;
				}
				
				if(quantita!=null && quantita!="")
					document.getElementById("TOT").value = round(parseFloat(quantitaNew * quantita), 5); 
			}
		</c:if>
	</gene:javaScript>
</gene:template></div>

