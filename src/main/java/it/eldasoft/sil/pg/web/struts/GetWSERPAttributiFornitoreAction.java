package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreResType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreType;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetWSERPAttributiFornitoreAction extends Action {

  private GestioneWSERPManager gestioneWSERPManager;

  private SqlManager sqlManager;

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }


  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONArray jsonArray = new JSONArray();

    String servizio = request.getParameter("servizio");
    if(servizio==null || "".equals(servizio))
      servizio ="WSERP";

    String tipoWSERP =null;
    WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
    if(configurazione.isEsito()){
      tipoWSERP = configurazione.getRemotewserp();
    }

      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());
      String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);

      String username = credenziali[0];
      String password = credenziali[1];

    try {
      String ngara = request.getParameter("ngara");
      String ditta = request.getParameter("ditta");
      String mandataria = "";


      if (ditta != null) {

        Long tipimp = (Long)this.sqlManager.getObject("select tipimp from impr where codimp = ?", new Object[]{ditta});
        if(tipimp != null && (tipimp.longValue()==3 || tipimp.longValue()==10)){
          mandataria = (String)this.sqlManager.getObject("select coddic from ragimp" +
         		" where codime9 = ? and impman = ? ", new Object[]{ditta, "1"});
          ditta= mandataria;
       }


        String selectIMPR = "select cfimp,pivimp,nomimp,indimp,locimp,nazimp,capimp,nciimp,proimp," +
        		"emai2ip,telimp,emaiip,telcel,cgenimp,iscrcciaa,faximp,coorba" +
        		" from impr where codimp = ?";
        List<?> datiIMPR = sqlManager.getListVector(selectIMPR, new Object[] { ditta });
        if (datiIMPR != null && datiIMPR.size() > 0) {
          // Dati AC
            String cfimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 0).getValue();
            cfimp = UtilityStringhe.convertiNullInStringaVuota(cfimp);
            String pivimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 1).getValue();
            pivimp = UtilityStringhe.convertiNullInStringaVuota(pivimp);
            String nomimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 2).getValue();
            String indimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 3).getValue();
            indimp = UtilityStringhe.convertiNullInStringaVuota(indimp);
            if("".equals(indimp)){
              indimp = "Non presente";
            }
            String locimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 4).getValue();
            Long nazimp = (Long) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 5).getValue();
            String nazStr = "Non presente";
            if(nazimp != null){
              nazStr = (String)this.sqlManager.getObject("select tab1desc from tab1 where tab1cod = 'Ag010' and tab1tip = ?", new Object[]{nazimp});
            }


            if(nazimp==null || new Long(1).equals(nazimp)){
              pivimp = "IT" + pivimp;
            }
            String capimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 6).getValue();
            String nciimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 7).getValue();
            nciimp = UtilityStringhe.convertiNullInStringaVuota(nciimp);
            if("".equals(nciimp)){
              nciimp = "Non presente";
            }
            String proimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 8).getValue();
            proimp = UtilityStringhe.convertiNullInStringaVuota(proimp);
            if("".equals(proimp)){
              proimp = "Non presente";
            }

            String pecimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 9).getValue();
            pecimp = UtilityStringhe.convertiNullInStringaVuota(pecimp);
            if("".equals(pecimp)){
              pecimp = "Non presente";
            }
            String telimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 10).getValue();
            telimp = UtilityStringhe.convertiNullInStringaVuota(telimp);
            if("".equals(telimp)){
              telimp = "Non presente";
            }
            String mailimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 11).getValue();
            mailimp = UtilityStringhe.convertiNullInStringaVuota(mailimp);
            if("".equals(mailimp)){
              mailimp = "Non presente";
            }

            String telcel = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 12).getValue();
            telcel = UtilityStringhe.convertiNullInStringaVuota(telcel);
            if("".equals(telcel)){
              telcel = "Non presente";
            }

            String cgenimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 13).getValue();
            cgenimp = UtilityStringhe.convertiNullInStringaVuota(cgenimp);

            String iscrCCIAA = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 14).getValue();
            iscrCCIAA = UtilityStringhe.convertiNullInStringaVuota(iscrCCIAA);

            String faximp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 15).getValue();
            faximp = UtilityStringhe.convertiNullInStringaVuota(faximp);
            if("".equals(faximp)){
              faximp = "Non presente";
            }

            String iban = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 16).getValue();
            iban = UtilityStringhe.convertiNullInStringaVuota(iban);
            if("".equals(iban)){
              iban = "Non presente";
            }


            //Dati SAP
            if("AVM".equals(tipoWSERP)){
            if(!"".equals(cfimp) || !"".equals(pivimp)){
              WSERPFornitoreResType wserpFornitoreRes = gestioneWSERPManager.wserpDettaglioFornitore(username, password, servizio, cfimp, pivimp, null);
              if(wserpFornitoreRes.isEsito()){
                Long stato = wserpFornitoreRes.getStato();
                String wsIdFornitore = "";
                String wsRagioneSociale = "";
                String wsLocalita = "";
                String wsProvincia = "";
                String wsCap = "";
                String wsIndirizzo = "";
                String wsNCivico = "";
                String wsCodiceFiscale = "";
                String wsPartitaIva = "";
                String wsGruppoConti = "";
                String wsNazionalita = "";
                String wsPec = "";
                String wsTelefono = "";
                String wsMail = "";
                //String wsIscrCCIAA = "";

                if(new Long(1).equals(stato)){//caso buono,trovato
                  WSERPFornitoreType fornitore = wserpFornitoreRes.getFornitore();

                  wsIdFornitore = fornitore.getIdFornitore();
                  wsIdFornitore = UtilityStringhe.convertiNullInStringaVuota(wsIdFornitore);

                  wsRagioneSociale = fornitore.getRagioneSociale();
                  wsRagioneSociale = UtilityStringhe.convertiNullInStringaVuota(wsRagioneSociale);
                  if("".equals(wsRagioneSociale)){
                    wsRagioneSociale = "Non presente";
                  }
                  wsLocalita = fornitore.getLocalita();
                  if("".equals(wsLocalita)){
                    wsLocalita = "Non presente";
                  }
                  wsProvincia = fornitore.getProvincia();
                  if("".equals(wsProvincia)){
                    wsProvincia = "Non presente";
                  }
                  wsCap = fornitore.getCap();
                  if("".equals(wsCap)){
                    wsCap = "Non presente";
                  }
                  wsIndirizzo = fornitore.getIndirizzo();
                  if("".equals(wsIndirizzo)){
                    wsIndirizzo = "Non presente";
                  }
                  wsNCivico = fornitore.getCivico();
                  if("".equals(wsNCivico)){
                    wsNCivico = "Non presente";
                  }
                  wsCodiceFiscale = fornitore.getCodiceFiscale();
                  wsCodiceFiscale = UtilityStringhe.convertiNullInStringaVuota(wsCodiceFiscale);
                  if("".equals(wsCodiceFiscale)){
                    wsCodiceFiscale = "Non presente";
                  }
                  wsPartitaIva = fornitore.getPartitaIva();
                  wsPartitaIva = UtilityStringhe.convertiNullInStringaVuota(wsPartitaIva);
                  if("".equals(wsPartitaIva)){
                    wsPartitaIva = "Non presente";
                  }
                  wsGruppoConti = fornitore.getGruppoConti();
                  wsGruppoConti = UtilityStringhe.convertiNullInStringaVuota(wsGruppoConti);
                  if("".equals(wsGruppoConti)){
                    wsGruppoConti = "Non presente";
                  }

                  wsNazionalita = fornitore.getNazionalita();
                  wsNazionalita = UtilityStringhe.convertiNullInStringaVuota(wsNazionalita);
                  if("".equals(wsNazionalita)){
                    wsNazionalita = "Non presente";
                  }else{
                    wsNazionalita = (String)this.sqlManager.getObject("select tab2d2 from tab2 where tab2cod = 'G_z23' and tab2d1 = ?", new Object[]{wsNazionalita});
                  }

                  wsPec = fornitore.getPec();
                  wsPec = UtilityStringhe.convertiNullInStringaVuota(wsPec);
                  if("".equals(wsPec)){
                    wsPec = "Non presente";
                  }

                  wsTelefono = fornitore.getTelefono();
                  wsTelefono = UtilityStringhe.convertiNullInStringaVuota(wsTelefono);
                  if("".equals(wsTelefono)){
                    wsTelefono = "Non presente";
                  }
                  wsMail = fornitore.getEmail();
                  wsMail = UtilityStringhe.convertiNullInStringaVuota(wsMail);
                  if("".equals(wsMail)){
                    wsMail = "Non presente";
                  }

                }

                Object[] row0 = new Object[3];
                row0[0] = "Id Fornitore";
                row0[1] = "";
                row0[2] = wsIdFornitore;
                jsonArray.add(row0);

                Object[] rowrgrpconti = new Object[3];
                rowrgrpconti[0] = "Gruppo conti";
                rowrgrpconti[1] = "Non presente";
                rowrgrpconti[2] = wsGruppoConti;
                jsonArray.add(rowrgrpconti);


                Object[] rowragsoc = new Object[3];
                rowragsoc[0] = "Ragione sociale";
                if(mandataria != null && mandataria != ""){
                  rowragsoc[1] = nomimp + " Capogruppo";
                  rowragsoc[2] = wsRagioneSociale + " Capogruppo";
                }else{
                  rowragsoc[1] = nomimp;
                  rowragsoc[2] = wsRagioneSociale;
                }
                jsonArray.add(rowragsoc);

                Object[] rowpiva = new Object[3];
                rowpiva[0] = "Partita IVA";
                rowpiva[1] = pivimp;
                rowpiva[2] = wsPartitaIva;
                jsonArray.add(rowpiva);

                Object[] rowcf = new Object[3];
                rowcf[0] = "Codice fiscale";
                rowcf[1] = cfimp;
                rowcf[2] = wsCodiceFiscale;
                jsonArray.add(rowcf);

                Object[] rowloc = new Object[3];
                rowloc[0] = "Localita'";
                rowloc[1] = locimp;
                rowloc[2] = wsLocalita;
                jsonArray.add(rowloc);

                Object[] rowcap = new Object[3];
                rowcap[0] = "C.A.P.";
                rowcap[1] = capimp;
                rowcap[2] = wsCap;
                jsonArray.add(rowcap);

                Object[] rowind = new Object[3];
                rowind[0] = "Indirizzo";
                rowind[1] = indimp;
                rowind[2] = wsIndirizzo;
                jsonArray.add(rowind);

                Object[] rownci = new Object[3];
                rownci[0] = "Civico";
                rownci[1] = nciimp;
                rownci[2] = wsNCivico;
                jsonArray.add(rownci);

                Object[] rowprov = new Object[3];
                rowprov[0] = "Provincia";
                rowprov[1] = proimp;
                rowprov[2] = wsProvincia;
                jsonArray.add(rowprov);

                Object[] rownaz = new Object[3];
                rownaz[0] = "Nazionalita'";
                rownaz[1] = nazStr;
                rownaz[2] = wsNazionalita;
                jsonArray.add(rownaz);

                Object[] rowpec = new Object[3];
                rowpec[0] = "Pec";
                rowpec[1] = pecimp;
                rowpec[2] = wsPec;
                jsonArray.add(rowpec);

                Object[] rowtel = new Object[3];
                rowtel[0] = "Telefono";
                rowtel[1] = telimp;
                rowtel[2] = wsTelefono;
                jsonArray.add(rowtel);

                Object[] rowmail = new Object[3];
                rowmail[0] = "E-Mail";
                rowmail[1] = mailimp;
                rowmail[2] = wsMail;
                jsonArray.add(rowmail);

                request.setAttribute("idFornitore", wsIdFornitore);

                String esitoControlli = this.gestioneWSERPManager.verificaPreliminareRda(ngara, tipoWSERP);
                esitoControlli = ".....";
                request.setAttribute("esitoControlli", esitoControlli);


              }else{
                throw new JspException("Errore durante la lettura degli attributi del fornitore", null);
              }



            }
            }

              //INTEGRAZIONE TPER

            if("TPER".equals(tipoWSERP)){
              Object[] row0 = new Object[2];
              row0[0] = "Codice ERP Fornitore";
              row0[1] = cgenimp;
              jsonArray.add(row0);

              /*
              Object[] rowrgrpconti = new Object[2];
              rowrgrpconti[0] = "Gruppo conti";
              rowrgrpconti[1] = "Non presente";
              jsonArray.add(rowrgrpconti);
              */


              Object[] rowragsoc = new Object[2];
              rowragsoc[0] = "Ragione sociale";
              if(mandataria != null && mandataria != ""){
                rowragsoc[1] = nomimp + " Capogruppo";
              }else{
                rowragsoc[1] = nomimp;
              }
              jsonArray.add(rowragsoc);

              Object[] rowpiva = new Object[2];
              rowpiva[0] = "Partita IVA";
              rowpiva[1] = pivimp;
              jsonArray.add(rowpiva);

              Object[] rowcf = new Object[2];
              rowcf[0] = "Codice fiscale";
              rowcf[1] = cfimp;
              jsonArray.add(rowcf);

              Object[] rowloc = new Object[2];
              rowloc[0] = "Localita'";
              rowloc[1] = locimp;
              jsonArray.add(rowloc);

              Object[] rowcap = new Object[2];
              rowcap[0] = "C.A.P.";
              rowcap[1] = capimp;
              jsonArray.add(rowcap);

              Object[] rowind = new Object[2];
              rowind[0] = "Indirizzo";
              rowind[1] = indimp;
              jsonArray.add(rowind);

              Object[] rownci = new Object[2];
              rownci[0] = "Civico";
              rownci[1] = nciimp;
              jsonArray.add(rownci);

              Object[] rowprov = new Object[2];
              rowprov[0] = "Provincia";
              rowprov[1] = proimp;
              jsonArray.add(rowprov);

              Object[] rownaz = new Object[2];
              rownaz[0] = "Nazionalita'";
              rownaz[1] = nazStr;
              jsonArray.add(rownaz);

              Object[] rowpec = new Object[2];
              rowpec[0] = "Pec";
              rowpec[1] = pecimp;
              jsonArray.add(rowpec);

              Object[] rowmail = new Object[2];
              rowmail[0] = "E-Mail";
              rowmail[1] = mailimp;
              jsonArray.add(rowmail);

              Object[] rowtel = new Object[2];
              rowtel[0] = "Telefono";
              rowtel[1] = telimp;
              jsonArray.add(rowtel);

              Object[] rowfax = new Object[2];
              rowfax[0] = "Fax";
              rowfax[1] = faximp;
              jsonArray.add(rowfax);

              Object[] rowcell = new Object[2];
              rowcell[0] = "Cellulare";
              rowcell[1] = telcel;
              jsonArray.add(rowcell);

              Object[] rowCCIAA = new Object[2];
              rowCCIAA[0] = "Iscrizione CCIAA ?";
              if("1".equals(iscrCCIAA)){
                rowCCIAA[1] = "Si";
              }else{
                rowCCIAA[1] = "No";
              }
              jsonArray.add(rowCCIAA);


              request.setAttribute("idFornitore", cgenimp);

            }

            if("CAV".equals(tipoWSERP)){
              if(!"".equals(cfimp) || !"".equals(pivimp)){
                WSERPFornitoreType fornitoreSearch = new WSERPFornitoreType();
                fornitoreSearch.setIdFornitore(cgenimp);
                fornitoreSearch.setRagioneSociale(nomimp);
                WSERPFornitoreResType wserpFornitoreRes = gestioneWSERPManager.wserpDettaglioFornitore(username, password, servizio, cfimp, pivimp, fornitoreSearch );
                if(wserpFornitoreRes.isEsito()){
                  Long stato = wserpFornitoreRes.getStato();
                  String wsIdFornitore = "";
                  String wsRagioneSociale = "";
                  String wsCodiceFiscale = "";
                  String wsPartitaIva = "";
                  String wsIban = "";
                  if(new Long(1).equals(stato)){//caso buono,trovato
                    WSERPFornitoreType fornitore = wserpFornitoreRes.getFornitore();
                    wsIdFornitore = fornitore.getIdFornitore();
                    wsIdFornitore = UtilityStringhe.convertiNullInStringaVuota(wsIdFornitore);

                    wsRagioneSociale = fornitore.getRagioneSociale();
                    wsRagioneSociale = UtilityStringhe.convertiNullInStringaVuota(wsRagioneSociale);
                    if("".equals(wsRagioneSociale)){
                      wsRagioneSociale = "Non presente";
                    }
                    wsCodiceFiscale = fornitore.getCodiceFiscale();
                    wsCodiceFiscale = UtilityStringhe.convertiNullInStringaVuota(wsCodiceFiscale);
                    if("".equals(wsCodiceFiscale)){
                      wsCodiceFiscale = "Non presente";
                    }
                    wsPartitaIva = fornitore.getPartitaIva();
                    wsPartitaIva = UtilityStringhe.convertiNullInStringaVuota(wsPartitaIva);
                    if("".equals(wsPartitaIva)){
                      wsPartitaIva = "Non presente";
                    }
                    wsIban = fornitore.getIban();
                    wsIban = UtilityStringhe.convertiNullInStringaVuota(wsIban);
                    if("".equals(wsIban)){
                      wsIban = "Non presente";
                    }
                  }

                    Object[] row0 = new Object[3];
                    row0[0] = "Id Fornitore";
                    cgenimp = UtilityStringhe.convertiNullInStringaVuota(cgenimp);
                    if(!"".equals(cgenimp)){
                      row0[1] = cgenimp;
                    }else{
                      if(!"".equals(wsIdFornitore)){
                        row0[1] = wsIdFornitore;
                      }
                    }
                    row0[2] = wsIdFornitore;
                    jsonArray.add(row0);



                    Object[] rowragsoc = new Object[3];
                    rowragsoc[0] = "Ragione sociale";
                    if(mandataria != null && mandataria != ""){
                      rowragsoc[1] = nomimp + " Capogruppo";
                      rowragsoc[2] = wsRagioneSociale + " Capogruppo";
                    }else{
                      rowragsoc[1] = nomimp;
                      rowragsoc[2] = wsRagioneSociale;
                    }
                    jsonArray.add(rowragsoc);

                    Object[] rowpiva = new Object[3];
                    rowpiva[0] = "Partita IVA";
                    rowpiva[1] = pivimp;
                    rowpiva[2] = wsPartitaIva;
                    jsonArray.add(rowpiva);

                    Object[] rowcf = new Object[3];
                    rowcf[0] = "Codice fiscale";
                    rowcf[1] = cfimp;
                    rowcf[2] = wsCodiceFiscale;
                    jsonArray.add(rowcf);

                    Object[] rowiban = new Object[3];
                    rowiban[0] = "Iban";
                    rowiban[1] = iban;
                    rowiban[2] = wsIban;
                    jsonArray.add(rowiban);

                    request.setAttribute("idFornitore", wsIdFornitore);


                }
              }
            }



        }

      }
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura degli attributi del fornitore", e);
    }

    out.println(jsonArray);
    out.flush();
    return null;
  }

}
