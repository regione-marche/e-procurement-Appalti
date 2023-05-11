

<tr>
	<td colspan="2">
		<br>
		<b>Lista degli elementi documentali del fascicolo</b><span id="notaselezione"><i>&nbsp;(selezionare l'elemento documentale per consultarne il dettaglio)</i></span>
		<br>
		<div style="display: none;" class="error" id="documentifascicolomessaggio"></div>
		<div id="documentifascicolocontainer"></div>
		<div id="tabs" style="display: none;">
			<br>
			<div style="border-bottom: 1px solid #A0AABA; padding-bottom:3px;"><b>Dettaglio dell'elemento documentale selezionato</b></div>
			<ul class='etabs'>
				<li class="tab" id="li-datigenerali"><a href="#tabs-datigenerali"><img style="float: center;" height="18" width="18" src="img/Content-20.png">&nbsp;Dati generali</a></li>
				<li class="tab" id="li-mittenti"><a href="#tabs-mittenti"><img style="float: center;" height="18" width="18" src="img/Users-24.png">&nbsp;Mittenti</a></li>
				<li class="tab" id="li-destinatari"><a href="#tabs-destinatari"><img style="float: center;" height="18" width="18" src="img/Users-24.png">&nbsp;Destinatari</a></li>
				<li class="tab" id="li-allegati"><a href="#tabs-allegati"><img style="float: center;" height="18" width="18" src="img/Edition-49.png">&nbsp;Allegati</a></li>
			</ul>
			<div id="tabs-datigenerali">
				<div style="display: none;" class="error" id="wsdmdocumentomessaggio"></div>
				<form id="formwsdmdocumento" name="formwsdmdocumento">
					<table class="wsdmscheda">
					    <tr>
							<td class="etichetta">Oggetto</td>
							<td class="valore">
								<span id="oggettodocumento"></span>
							</td>						
						</tr>
						<tr>
							<td class="etichetta">Numero documento</td>
							<td class="valore">
								<span id="numerodocumento"></span>
							</td>						
						</tr>
						<tr>
							<td class="etichetta">Anno protocollo</td>
							<td class="valore">
								<span id="annoprotocollo"></span>
							</td>						
						</tr>
						<tr>
							<td class="etichetta">Numero protocollo</td>
							<td class="valore">
								<span id="numeroprotocollo"></span>
							</td>						
						</tr>	
						<tr>
							<td class="etichetta">Ingresso/uscita</td>
							<td class="valore">
								<span id="inout"></span>
							</td>						
						</tr>		
					</table>
				</form>
			</div>
			<div id="tabs-mittenti">
			   <div id="mittenticontainer"></div>
				<div style="display: none;" class="error" id="mittentimessaggio"></div>
			</div>
			<div id="tabs-destinatari">
			   <div id="destinataricontainer"></div>
				<div style="display: none;" class="error" id="destinatarimessaggio"></div>
			</div>
			<div id="tabs-allegati">
			 	<div id="allegaticontainer"></div>
				<div style="display: none;" class="error" id="allegatimessaggio"></div>
			</div>
		</div>
	</td>
</tr>


