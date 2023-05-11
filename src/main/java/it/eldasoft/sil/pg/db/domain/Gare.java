package it.eldasoft.sil.pg.db.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Questa classe mappa interamente la tabella GARE
 * senza distinzione tra attributi e campi a DB
 * @author gabriele.nencini
 *
 */
@JsonInclude(value = Include.NON_EMPTY)
public class Gare {
  //Id
  private String ngara;
  private String codiga;
  //foreign key a Torn, ManyToOne
  private String codgar1;
  private Integer tattog;
  private Date dattog;
  private String nattog;
  private String navvigg;
  private Date davvigg;
  private Date dpubavg;
  private Date dfpubag;
  private Date dibandg;
  private String nproag;
  private Integer nespe2;
  private String preced;
  private String seguen;
  private String navpre;
  private Date davpre;
  private String motiva;
  private String clavor;
  private Integer nappfi;
  private Integer numera;
  private String numssl;
  private String nomssl;
  private String prosla;
  private String loclav;
  private String locint;
  private BigDecimal impapp;
  private BigDecimal impmis;
  private BigDecimal impcor;
  private BigDecimal impnrl;
  private BigDecimal impnrm;
  private BigDecimal impnrc;
  private BigDecimal impecu;
  private String notmis;
  private String notcor;
  private Integer tiplav;
  private Date dinlavg;
  private Integer teutil;
  private String catiga;
  private BigDecimal impiga;
  private BigDecimal garoff;
  private String modcau;
  private String modfin;
  private Integer tipgarg;
  private Integer modastg;
  private Integer modlicg;
  private String modgarg;
  private Date dteparg;
  private BigDecimal limmin;
  private BigDecimal limmax;
  private Integer nofval;
  private Integer nofmed;
  private BigDecimal media;
  private String ditta;
  private String nomima;
  private BigDecimal ribagg;
  private BigDecimal riboepv;
  private BigDecimal iaggiu;
  private Date dverag;
  private Integer tattoa;
  private Date dattoa;
  private String nattoa;
  private String nproaa;
  private String ncomag;
  private Date dcomag;
  private BigDecimal impgar;
  private String nquiet;
  private Date dquiet;
  private String istcre;
  private Date ricdoc;
  private String respre;
  private Date ricpre;
  private Date dacert;
  private String nproce;
  private String estimp;
  private Integer fasgar;
  private BigDecimal impsic;
  private BigDecimal impsmi;
  private BigDecimal impsco;
  private String sicinc;
  private String not_gar;
  private String preinf;
  private String terrid;
  private String conten;
  private String motcon;
  private Integer tiatto;
  private String coddef;
  private Integer appdef;
  private BigDecimal cliv1;
  private BigDecimal cliv2;
  private BigDecimal corgar1;
  private Date dteoff;
  private String oteoff;
  private Date dindoc;
  private Date desdoc;
  private Date desoff;
  private String oesoff;
  private String nprova;
  private String indist;
  private String ridiso;
  private Integer precut;
  private BigDecimal onprge;
  private BigDecimal pgarof;
  private Integer numele;
  private Integer nsorte;
  private BigDecimal alainf;
  private BigDecimal alasup;
  private BigDecimal istaut;
  private BigDecimal idiaut;
  private String segreta;
  private String subgar;
  private Integer oggcont;
  private String cupprg;
  private String cupmst;
  private Integer ivalav;
  private String codcig;
  private BigDecimal massim;
  private BigDecimal masest;
  private BigDecimal masres;
  private String notega;
  private String oteparg;
  private Integer temesi;
  private BigDecimal masprod;
  private BigDecimal masprof;
  private BigDecimal masear;
  private BigDecimal impcom;
  private BigDecimal impliq;
  private Date datliq;
  private Date dcomng;
  private String ncomng;
  private String nrepat;
  private Date daatto;
  private Date dvprov;
  private String nvprov;
  private String codcom;
  private Integer tipneg;
  private String dittap;
  private BigDecimal ribpro;
  private BigDecimal iagpro;
  private Date vercan;
  private String vernum;
  private Integer genere;
  private Integer ribcal;
  private String calcsoang;
  private Integer critlicg;
  private Integer detlicg;
  private Date dvvreqsedris;
  private String nlettrichcc;
  private Date dlettrichcc;
  private Date dtermprescc;
  private Date dvverchcc;
  private Integer tattammescl;
  private String nattammescl;
  private Date dattammescl;
  private String nlettcomescl;
  private Date dlettcomescl;
  private Date dtermrichcdpg;
  private Date dtermrispcdpg;
  private Date dtermrichcpog;
  private Date dtermrispcpog;
  private Date dricesp;
  private Date dinvdoctec;
  private Date dconvditte;
  private Integer stepgar;
  private Date dvapdocamm;
  private Date dvvidonoff;
  private Date dvvccdocamm;
  private Date dvcompreq;
  private String notcompreq;
  private Date driccaptec;
  private Date dacqcig;
  private Integer livpro;
  private String nproti;
  private Date dinvit;
  private Date dsedpubeva;
  private Date davvprvreq;
  private Date drichdoccr;
  private Date driczdoccr;
  private Date dlettaggprov;
  private Date dtermdoccr;
  private String nproreq;
  private Date dproaa;
  private Integer taggeff;
  private String naggeff;
  private Date daggeff;
  private Date dcomaggsa;
  private Date dcomdittagg;
  private String ncomdittagg;
  private Date dcomdittnag;
  private String ncomdittnag;
  private Integer applegregg;
  private String elencoe;
  private String carrello;
  private String nmaximo;
  private Integer esineg;
  private String onsogrib;
  private Date datneg;
  private BigDecimal idric;
  private Integer bustalotti;
  
  /**
   * @return the ngara
   */
  public String getNgara() {
    return ngara;
  }
  
  /**
   * @param ngara the ngara to set
   */
  public void setNgara(String ngara) {
    this.ngara = ngara;
  }
  
  /**
   * @return the codiga
   */
  public String getCodiga() {
    return codiga;
  }
  
  /**
   * @param codiga the codiga to set
   */
  public void setCodiga(String codiga) {
    this.codiga = codiga;
  }
  
  /**
   * @return the codgar1
   */
  public String getCodgar1() {
    return codgar1;
  }
  
  /**
   * @param codgar1 the codgar1 to set
   */
  public void setCodgar1(String codgar1) {
    this.codgar1 = codgar1;
  }
  
  /**
   * @return the tattog
   */
  public Integer getTattog() {
    return tattog;
  }
  
  /**
   * @param tattog the tattog to set
   */
  public void setTattog(Integer tattog) {
    this.tattog = tattog;
  }
  
  /**
   * @return the dattog
   */
  public Date getDattog() {
    return dattog;
  }
  
  /**
   * @param dattog the dattog to set
   */
  public void setDattog(Date dattog) {
    this.dattog = dattog;
  }
  
  /**
   * @return the nattog
   */
  public String getNattog() {
    return nattog;
  }
  
  /**
   * @param nattog the nattog to set
   */
  public void setNattog(String nattog) {
    this.nattog = nattog;
  }
  
  /**
   * @return the navvigg
   */
  public String getNavvigg() {
    return navvigg;
  }
  
  /**
   * @param navvigg the navvigg to set
   */
  public void setNavvigg(String navvigg) {
    this.navvigg = navvigg;
  }
  
  /**
   * @return the davvigg
   */
  public Date getDavvigg() {
    return davvigg;
  }
  
  /**
   * @param davvigg the davvigg to set
   */
  public void setDavvigg(Date davvigg) {
    this.davvigg = davvigg;
  }
  
  /**
   * @return the dpubavg
   */
  public Date getDpubavg() {
    return dpubavg;
  }
  
  /**
   * @param dpubavg the dpubavg to set
   */
  public void setDpubavg(Date dpubavg) {
    this.dpubavg = dpubavg;
  }
  
  /**
   * @return the dfpubag
   */
  public Date getDfpubag() {
    return dfpubag;
  }
  
  /**
   * @param dfpubag the dfpubag to set
   */
  public void setDfpubag(Date dfpubag) {
    this.dfpubag = dfpubag;
  }
  
  /**
   * @return the dibandg
   */
  public Date getDibandg() {
    return dibandg;
  }
  
  /**
   * @param dibandg the dibandg to set
   */
  public void setDibandg(Date dibandg) {
    this.dibandg = dibandg;
  }
  
  /**
   * @return the nproag
   */
  public String getNproag() {
    return nproag;
  }
  
  /**
   * @param nproag the nproag to set
   */
  public void setNproag(String nproag) {
    this.nproag = nproag;
  }
  
  /**
   * @return the nespe2
   */
  public Integer getNespe2() {
    return nespe2;
  }
  
  /**
   * @param nespe2 the nespe2 to set
   */
  public void setNespe2(Integer nespe2) {
    this.nespe2 = nespe2;
  }
  
  /**
   * @return the preced
   */
  public String getPreced() {
    return preced;
  }
  
  /**
   * @param preced the preced to set
   */
  public void setPreced(String preced) {
    this.preced = preced;
  }
  
  /**
   * @return the seguen
   */
  public String getSeguen() {
    return seguen;
  }
  
  /**
   * @param seguen the seguen to set
   */
  public void setSeguen(String seguen) {
    this.seguen = seguen;
  }
  
  /**
   * @return the navpre
   */
  public String getNavpre() {
    return navpre;
  }
  
  /**
   * @param navpre the navpre to set
   */
  public void setNavpre(String navpre) {
    this.navpre = navpre;
  }
  
  /**
   * @return the davpre
   */
  public Date getDavpre() {
    return davpre;
  }
  
  /**
   * @param davpre the davpre to set
   */
  public void setDavpre(Date davpre) {
    this.davpre = davpre;
  }
  
  /**
   * @return the motiva
   */
  public String getMotiva() {
    return motiva;
  }
  
  /**
   * @param motiva the motiva to set
   */
  public void setMotiva(String motiva) {
    this.motiva = motiva;
  }
  
  /**
   * @return the clavor
   */
  public String getClavor() {
    return clavor;
  }
  
  /**
   * @param clavor the clavor to set
   */
  public void setClavor(String clavor) {
    this.clavor = clavor;
  }
  
  /**
   * @return the nappfi
   */
  public Integer getNappfi() {
    return nappfi;
  }
  
  /**
   * @param nappfi the nappfi to set
   */
  public void setNappfi(Integer nappfi) {
    this.nappfi = nappfi;
  }
  
  /**
   * @return the numera
   */
  public Integer getNumera() {
    return numera;
  }
  
  /**
   * @param numera the numera to set
   */
  public void setNumera(Integer numera) {
    this.numera = numera;
  }
  
  /**
   * @return the numssl
   */
  public String getNumssl() {
    return numssl;
  }
  
  /**
   * @param numssl the numssl to set
   */
  public void setNumssl(String numssl) {
    this.numssl = numssl;
  }
  
  /**
   * @return the nomssl
   */
  public String getNomssl() {
    return nomssl;
  }
  
  /**
   * @param nomssl the nomssl to set
   */
  public void setNomssl(String nomssl) {
    this.nomssl = nomssl;
  }
  
  /**
   * @return the prosla
   */
  public String getProsla() {
    return prosla;
  }
  
  /**
   * @param prosla the prosla to set
   */
  public void setProsla(String prosla) {
    this.prosla = prosla;
  }
  
  /**
   * @return the loclav
   */
  public String getLoclav() {
    return loclav;
  }
  
  /**
   * @param loclav the loclav to set
   */
  public void setLoclav(String loclav) {
    this.loclav = loclav;
  }
  
  /**
   * @return the locint
   */
  public String getLocint() {
    return locint;
  }
  
  /**
   * @param locint the locint to set
   */
  public void setLocint(String locint) {
    this.locint = locint;
  }
  
  /**
   * @return the impapp
   */
  public BigDecimal getImpapp() {
    return impapp;
  }
  
  /**
   * @param impapp the impapp to set
   */
  public void setImpapp(BigDecimal impapp) {
    this.impapp = impapp;
  }
  
  /**
   * @return the impmis
   */
  public BigDecimal getImpmis() {
    return impmis;
  }
  
  /**
   * @param impmis the impmis to set
   */
  public void setImpmis(BigDecimal impmis) {
    this.impmis = impmis;
  }
  
  /**
   * @return the impcor
   */
  public BigDecimal getImpcor() {
    return impcor;
  }
  
  /**
   * @param impcor the impcor to set
   */
  public void setImpcor(BigDecimal impcor) {
    this.impcor = impcor;
  }
  
  /**
   * @return the impnrl
   */
  public BigDecimal getImpnrl() {
    return impnrl;
  }
  
  /**
   * @param impnrl the impnrl to set
   */
  public void setImpnrl(BigDecimal impnrl) {
    this.impnrl = impnrl;
  }
  
  /**
   * @return the impnrm
   */
  public BigDecimal getImpnrm() {
    return impnrm;
  }
  
  /**
   * @param impnrm the impnrm to set
   */
  public void setImpnrm(BigDecimal impnrm) {
    this.impnrm = impnrm;
  }
  
  /**
   * @return the impnrc
   */
  public BigDecimal getImpnrc() {
    return impnrc;
  }
  
  /**
   * @param impnrc the impnrc to set
   */
  public void setImpnrc(BigDecimal impnrc) {
    this.impnrc = impnrc;
  }
  
  /**
   * @return the impecu
   */
  public BigDecimal getImpecu() {
    return impecu;
  }
  
  /**
   * @param impecu the impecu to set
   */
  public void setImpecu(BigDecimal impecu) {
    this.impecu = impecu;
  }
  
  /**
   * @return the notmis
   */
  public String getNotmis() {
    return notmis;
  }
  
  /**
   * @param notmis the notmis to set
   */
  public void setNotmis(String notmis) {
    this.notmis = notmis;
  }
  
  /**
   * @return the notcor
   */
  public String getNotcor() {
    return notcor;
  }
  
  /**
   * @param notcor the notcor to set
   */
  public void setNotcor(String notcor) {
    this.notcor = notcor;
  }
  
  /**
   * @return the tiplav
   */
  public Integer getTiplav() {
    return tiplav;
  }
  
  /**
   * @param tiplav the tiplav to set
   */
  public void setTiplav(Integer tiplav) {
    this.tiplav = tiplav;
  }
  
  /**
   * @return the dinlavg
   */
  public Date getDinlavg() {
    return dinlavg;
  }
  
  /**
   * @param dinlavg the dinlavg to set
   */
  public void setDinlavg(Date dinlavg) {
    this.dinlavg = dinlavg;
  }
  
  /**
   * @return the teutil
   */
  public Integer getTeutil() {
    return teutil;
  }
  
  /**
   * @param teutil the teutil to set
   */
  public void setTeutil(Integer teutil) {
    this.teutil = teutil;
  }
  
  /**
   * @return the catiga
   */
  public String getCatiga() {
    return catiga;
  }
  
  /**
   * @param catiga the catiga to set
   */
  public void setCatiga(String catiga) {
    this.catiga = catiga;
  }
  
  /**
   * @return the impiga
   */
  public BigDecimal getImpiga() {
    return impiga;
  }
  
  /**
   * @param impiga the impiga to set
   */
  public void setImpiga(BigDecimal impiga) {
    this.impiga = impiga;
  }
  
  /**
   * @return the garoff
   */
  public BigDecimal getGaroff() {
    return garoff;
  }
  
  /**
   * @param garoff the garoff to set
   */
  public void setGaroff(BigDecimal garoff) {
    this.garoff = garoff;
  }
  
  /**
   * @return the modcau
   */
  public String getModcau() {
    return modcau;
  }
  
  /**
   * @param modcau the modcau to set
   */
  public void setModcau(String modcau) {
    this.modcau = modcau;
  }
  
  /**
   * @return the modfin
   */
  public String getModfin() {
    return modfin;
  }
  
  /**
   * @param modfin the modfin to set
   */
  public void setModfin(String modfin) {
    this.modfin = modfin;
  }
  
  /**
   * @return the tipgarg
   */
  public Integer getTipgarg() {
    return tipgarg;
  }
  
  /**
   * @param tipgarg the tipgarg to set
   */
  public void setTipgarg(Integer tipgarg) {
    this.tipgarg = tipgarg;
  }
  
  /**
   * @return the modastg
   */
  public Integer getModastg() {
    return modastg;
  }
  
  /**
   * @param modastg the modastg to set
   */
  public void setModastg(Integer modastg) {
    this.modastg = modastg;
  }
  
  /**
   * @return the modlicg
   */
  public Integer getModlicg() {
    return modlicg;
  }
  
  /**
   * @param modlicg the modlicg to set
   */
  public void setModlicg(Integer modlicg) {
    this.modlicg = modlicg;
  }
  
  /**
   * @return the modgarg
   */
  public String getModgarg() {
    return modgarg;
  }
  
  /**
   * @param modgarg the modgarg to set
   */
  public void setModgarg(String modgarg) {
    this.modgarg = modgarg;
  }
  
  /**
   * @return the dteparg
   */
  public Date getDteparg() {
    return dteparg;
  }
  
  /**
   * @param dteparg the dteparg to set
   */
  public void setDteparg(Date dteparg) {
    this.dteparg = dteparg;
  }
  
  /**
   * @return the limmin
   */
  public BigDecimal getLimmin() {
    return limmin;
  }
  
  /**
   * @param limmin the limmin to set
   */
  public void setLimmin(BigDecimal limmin) {
    this.limmin = limmin;
  }
  
  /**
   * @return the limmax
   */
  public BigDecimal getLimmax() {
    return limmax;
  }
  
  /**
   * @param limmax the limmax to set
   */
  public void setLimmax(BigDecimal limmax) {
    this.limmax = limmax;
  }
  
  /**
   * @return the nofval
   */
  public Integer getNofval() {
    return nofval;
  }
  
  /**
   * @param nofval the nofval to set
   */
  public void setNofval(Integer nofval) {
    this.nofval = nofval;
  }
  
  /**
   * @return the nofmed
   */
  public Integer getNofmed() {
    return nofmed;
  }
  
  /**
   * @param nofmed the nofmed to set
   */
  public void setNofmed(Integer nofmed) {
    this.nofmed = nofmed;
  }
  
  /**
   * @return the media
   */
  public BigDecimal getMedia() {
    return media;
  }
  
  /**
   * @param media the media to set
   */
  public void setMedia(BigDecimal media) {
    this.media = media;
  }
  
  /**
   * @return the ditta
   */
  public String getDitta() {
    return ditta;
  }
  
  /**
   * @param ditta the ditta to set
   */
  public void setDitta(String ditta) {
    this.ditta = ditta;
  }
  
  /**
   * @return the nomima
   */
  public String getNomima() {
    return nomima;
  }
  
  /**
   * @param nomima the nomima to set
   */
  public void setNomima(String nomima) {
    this.nomima = nomima;
  }
  
  /**
   * @return the ribagg
   */
  public BigDecimal getRibagg() {
    return ribagg;
  }
  
  /**
   * @param ribagg the ribagg to set
   */
  public void setRibagg(BigDecimal ribagg) {
    this.ribagg = ribagg;
  }
  
  /**
   * @return the riboepv
   */
  public BigDecimal getRiboepv() {
    return riboepv;
  }
  
  /**
   * @param riboepv the riboepv to set
   */
  public void setRiboepv(BigDecimal riboepv) {
    this.riboepv = riboepv;
  }
  
  /**
   * @return the iaggiu
   */
  public BigDecimal getIaggiu() {
    return iaggiu;
  }
  
  /**
   * @param iaggiu the iaggiu to set
   */
  public void setIaggiu(BigDecimal iaggiu) {
    this.iaggiu = iaggiu;
  }
  
  /**
   * @return the dverag
   */
  public Date getDverag() {
    return dverag;
  }
  
  /**
   * @param dverag the dverag to set
   */
  public void setDverag(Date dverag) {
    this.dverag = dverag;
  }
  
  /**
   * @return the tattoa
   */
  public Integer getTattoa() {
    return tattoa;
  }
  
  /**
   * @param tattoa the tattoa to set
   */
  public void setTattoa(Integer tattoa) {
    this.tattoa = tattoa;
  }
  
  /**
   * @return the dattoa
   */
  public Date getDattoa() {
    return dattoa;
  }
  
  /**
   * @param dattoa the dattoa to set
   */
  public void setDattoa(Date dattoa) {
    this.dattoa = dattoa;
  }
  
  /**
   * @return the nattoa
   */
  public String getNattoa() {
    return nattoa;
  }
  
  /**
   * @param nattoa the nattoa to set
   */
  public void setNattoa(String nattoa) {
    this.nattoa = nattoa;
  }
  
  /**
   * @return the nproaa
   */
  public String getNproaa() {
    return nproaa;
  }
  
  /**
   * @param nproaa the nproaa to set
   */
  public void setNproaa(String nproaa) {
    this.nproaa = nproaa;
  }
  
  /**
   * @return the ncomag
   */
  public String getNcomag() {
    return ncomag;
  }
  
  /**
   * @param ncomag the ncomag to set
   */
  public void setNcomag(String ncomag) {
    this.ncomag = ncomag;
  }
  
  /**
   * @return the dcomag
   */
  public Date getDcomag() {
    return dcomag;
  }
  
  /**
   * @param dcomag the dcomag to set
   */
  public void setDcomag(Date dcomag) {
    this.dcomag = dcomag;
  }
  
  /**
   * @return the impgar
   */
  public BigDecimal getImpgar() {
    return impgar;
  }
  
  /**
   * @param impgar the impgar to set
   */
  public void setImpgar(BigDecimal impgar) {
    this.impgar = impgar;
  }
  
  /**
   * @return the nquiet
   */
  public String getNquiet() {
    return nquiet;
  }
  
  /**
   * @param nquiet the nquiet to set
   */
  public void setNquiet(String nquiet) {
    this.nquiet = nquiet;
  }
  
  /**
   * @return the dquiet
   */
  public Date getDquiet() {
    return dquiet;
  }
  
  /**
   * @param dquiet the dquiet to set
   */
  public void setDquiet(Date dquiet) {
    this.dquiet = dquiet;
  }
  
  /**
   * @return the istcre
   */
  public String getIstcre() {
    return istcre;
  }
  
  /**
   * @param istcre the istcre to set
   */
  public void setIstcre(String istcre) {
    this.istcre = istcre;
  }
  
  /**
   * @return the ricdoc
   */
  public Date getRicdoc() {
    return ricdoc;
  }
  
  /**
   * @param ricdoc the ricdoc to set
   */
  public void setRicdoc(Date ricdoc) {
    this.ricdoc = ricdoc;
  }
  
  /**
   * @return the respre
   */
  public String getRespre() {
    return respre;
  }
  
  /**
   * @param respre the respre to set
   */
  public void setRespre(String respre) {
    this.respre = respre;
  }
  
  /**
   * @return the ricpre
   */
  public Date getRicpre() {
    return ricpre;
  }
  
  /**
   * @param ricpre the ricpre to set
   */
  public void setRicpre(Date ricpre) {
    this.ricpre = ricpre;
  }
  
  /**
   * @return the dacert
   */
  public Date getDacert() {
    return dacert;
  }
  
  /**
   * @param dacert the dacert to set
   */
  public void setDacert(Date dacert) {
    this.dacert = dacert;
  }
  
  /**
   * @return the nproce
   */
  public String getNproce() {
    return nproce;
  }
  
  /**
   * @param nproce the nproce to set
   */
  public void setNproce(String nproce) {
    this.nproce = nproce;
  }
  
  /**
   * @return the estimp
   */
  public String getEstimp() {
    return estimp;
  }
  
  /**
   * @param estimp the estimp to set
   */
  public void setEstimp(String estimp) {
    this.estimp = estimp;
  }
  
  /**
   * @return the fasgar
   */
  public Integer getFasgar() {
    return fasgar;
  }
  
  /**
   * @param fasgar the fasgar to set
   */
  public void setFasgar(Integer fasgar) {
    this.fasgar = fasgar;
  }
  
  /**
   * @return the impsic
   */
  public BigDecimal getImpsic() {
    return impsic;
  }
  
  /**
   * @param impsic the impsic to set
   */
  public void setImpsic(BigDecimal impsic) {
    this.impsic = impsic;
  }
  
  /**
   * @return the impsmi
   */
  public BigDecimal getImpsmi() {
    return impsmi;
  }
  
  /**
   * @param impsmi the impsmi to set
   */
  public void setImpsmi(BigDecimal impsmi) {
    this.impsmi = impsmi;
  }
  
  /**
   * @return the impsco
   */
  public BigDecimal getImpsco() {
    return impsco;
  }
  
  /**
   * @param impsco the impsco to set
   */
  public void setImpsco(BigDecimal impsco) {
    this.impsco = impsco;
  }
  
  /**
   * @return the sicinc
   */
  public String getSicinc() {
    return sicinc;
  }
  
  /**
   * @param sicinc the sicinc to set
   */
  public void setSicinc(String sicinc) {
    this.sicinc = sicinc;
  }
  
  /**
   * @return the not_gar
   */
  public String getNot_gar() {
    return not_gar;
  }
  
  /**
   * @param not_gar the not_gar to set
   */
  public void setNot_gar(String not_gar) {
    this.not_gar = not_gar;
  }
  
  /**
   * @return the preinf
   */
  public String getPreinf() {
    return preinf;
  }
  
  /**
   * @param preinf the preinf to set
   */
  public void setPreinf(String preinf) {
    this.preinf = preinf;
  }
  
  /**
   * @return the terrid
   */
  public String getTerrid() {
    return terrid;
  }
  
  /**
   * @param terrid the terrid to set
   */
  public void setTerrid(String terrid) {
    this.terrid = terrid;
  }
  
  /**
   * @return the conten
   */
  public String getConten() {
    return conten;
  }
  
  /**
   * @param conten the conten to set
   */
  public void setConten(String conten) {
    this.conten = conten;
  }
  
  /**
   * @return the motcon
   */
  public String getMotcon() {
    return motcon;
  }
  
  /**
   * @param motcon the motcon to set
   */
  public void setMotcon(String motcon) {
    this.motcon = motcon;
  }
  
  /**
   * @return the tiatto
   */
  public Integer getTiatto() {
    return tiatto;
  }
  
  /**
   * @param tiatto the tiatto to set
   */
  public void setTiatto(Integer tiatto) {
    this.tiatto = tiatto;
  }
  
  /**
   * @return the coddef
   */
  public String getCoddef() {
    return coddef;
  }
  
  /**
   * @param coddef the coddef to set
   */
  public void setCoddef(String coddef) {
    this.coddef = coddef;
  }
  
  /**
   * @return the appdef
   */
  public Integer getAppdef() {
    return appdef;
  }
  
  /**
   * @param appdef the appdef to set
   */
  public void setAppdef(Integer appdef) {
    this.appdef = appdef;
  }
  
  /**
   * @return the cliv1
   */
  public BigDecimal getCliv1() {
    return cliv1;
  }
  
  /**
   * @param cliv1 the cliv1 to set
   */
  public void setCliv1(BigDecimal cliv1) {
    this.cliv1 = cliv1;
  }
  
  /**
   * @return the cliv2
   */
  public BigDecimal getCliv2() {
    return cliv2;
  }
  
  /**
   * @param cliv2 the cliv2 to set
   */
  public void setCliv2(BigDecimal cliv2) {
    this.cliv2 = cliv2;
  }
  
  /**
   * @return the corgar1
   */
  public BigDecimal getCorgar1() {
    return corgar1;
  }
  
  /**
   * @param corgar1 the corgar1 to set
   */
  public void setCorgar1(BigDecimal corgar1) {
    this.corgar1 = corgar1;
  }
  
  /**
   * @return the dteoff
   */
  public Date getDteoff() {
    return dteoff;
  }
  
  /**
   * @param dteoff the dteoff to set
   */
  public void setDteoff(Date dteoff) {
    this.dteoff = dteoff;
  }
  
  /**
   * @return the oteoff
   */
  public String getOteoff() {
    return oteoff;
  }
  
  /**
   * @param oteoff the oteoff to set
   */
  public void setOteoff(String oteoff) {
    this.oteoff = oteoff;
  }
  
  /**
   * @return the dindoc
   */
  public Date getDindoc() {
    return dindoc;
  }
  
  /**
   * @param dindoc the dindoc to set
   */
  public void setDindoc(Date dindoc) {
    this.dindoc = dindoc;
  }
  
  /**
   * @return the desdoc
   */
  public Date getDesdoc() {
    return desdoc;
  }
  
  /**
   * @param desdoc the desdoc to set
   */
  public void setDesdoc(Date desdoc) {
    this.desdoc = desdoc;
  }
  
  /**
   * @return the desoff
   */
  public Date getDesoff() {
    return desoff;
  }
  
  /**
   * @param desoff the desoff to set
   */
  public void setDesoff(Date desoff) {
    this.desoff = desoff;
  }
  
  /**
   * @return the oesoff
   */
  public String getOesoff() {
    return oesoff;
  }
  
  /**
   * @param oesoff the oesoff to set
   */
  public void setOesoff(String oesoff) {
    this.oesoff = oesoff;
  }
  
  /**
   * @return the nprova
   */
  public String getNprova() {
    return nprova;
  }
  
  /**
   * @param nprova the nprova to set
   */
  public void setNprova(String nprova) {
    this.nprova = nprova;
  }
  
  /**
   * @return the indist
   */
  public String getIndist() {
    return indist;
  }
  
  /**
   * @param indist the indist to set
   */
  public void setIndist(String indist) {
    this.indist = indist;
  }
  
  /**
   * @return the ridiso
   */
  public String getRidiso() {
    return ridiso;
  }
  
  /**
   * @param ridiso the ridiso to set
   */
  public void setRidiso(String ridiso) {
    this.ridiso = ridiso;
  }
  
  /**
   * @return the precut
   */
  public Integer getPrecut() {
    return precut;
  }
  
  /**
   * @param precut the precut to set
   */
  public void setPrecut(Integer precut) {
    this.precut = precut;
  }
  
  /**
   * @return the onprge
   */
  public BigDecimal getOnprge() {
    return onprge;
  }
  
  /**
   * @param onprge the onprge to set
   */
  public void setOnprge(BigDecimal onprge) {
    this.onprge = onprge;
  }
  
  /**
   * @return the pgarof
   */
  public BigDecimal getPgarof() {
    return pgarof;
  }
  
  /**
   * @param pgarof the pgarof to set
   */
  public void setPgarof(BigDecimal pgarof) {
    this.pgarof = pgarof;
  }
  
  /**
   * @return the numele
   */
  public Integer getNumele() {
    return numele;
  }
  
  /**
   * @param numele the numele to set
   */
  public void setNumele(Integer numele) {
    this.numele = numele;
  }
  
  /**
   * @return the nsorte
   */
  public Integer getNsorte() {
    return nsorte;
  }
  
  /**
   * @param nsorte the nsorte to set
   */
  public void setNsorte(Integer nsorte) {
    this.nsorte = nsorte;
  }
  
  /**
   * @return the alainf
   */
  public BigDecimal getAlainf() {
    return alainf;
  }
  
  /**
   * @param alainf the alainf to set
   */
  public void setAlainf(BigDecimal alainf) {
    this.alainf = alainf;
  }
  
  /**
   * @return the alasup
   */
  public BigDecimal getAlasup() {
    return alasup;
  }
  
  /**
   * @param alasup the alasup to set
   */
  public void setAlasup(BigDecimal alasup) {
    this.alasup = alasup;
  }
  
  /**
   * @return the istaut
   */
  public BigDecimal getIstaut() {
    return istaut;
  }
  
  /**
   * @param istaut the istaut to set
   */
  public void setIstaut(BigDecimal istaut) {
    this.istaut = istaut;
  }
  
  /**
   * @return the idiaut
   */
  public BigDecimal getIdiaut() {
    return idiaut;
  }
  
  /**
   * @param idiaut the idiaut to set
   */
  public void setIdiaut(BigDecimal idiaut) {
    this.idiaut = idiaut;
  }
  
  /**
   * @return the segreta
   */
  public String getSegreta() {
    return segreta;
  }
  
  /**
   * @param segreta the segreta to set
   */
  public void setSegreta(String segreta) {
    this.segreta = segreta;
  }
  
  /**
   * @return the subgar
   */
  public String getSubgar() {
    return subgar;
  }
  
  /**
   * @param subgar the subgar to set
   */
  public void setSubgar(String subgar) {
    this.subgar = subgar;
  }
  
  /**
   * @return the oggcont
   */
  public Integer getOggcont() {
    return oggcont;
  }
  
  /**
   * @param oggcont the oggcont to set
   */
  public void setOggcont(Integer oggcont) {
    this.oggcont = oggcont;
  }
  
  /**
   * @return the cupprg
   */
  public String getCupprg() {
    return cupprg;
  }
  
  /**
   * @param cupprg the cupprg to set
   */
  public void setCupprg(String cupprg) {
    this.cupprg = cupprg;
  }
  
  /**
   * @return the cupmst
   */
  public String getCupmst() {
    return cupmst;
  }
  
  /**
   * @param cupmst the cupmst to set
   */
  public void setCupmst(String cupmst) {
    this.cupmst = cupmst;
  }
  
  /**
   * @return the ivalav
   */
  public Integer getIvalav() {
    return ivalav;
  }
  
  /**
   * @param ivalav the ivalav to set
   */
  public void setIvalav(Integer ivalav) {
    this.ivalav = ivalav;
  }
  
  /**
   * @return the codcig
   */
  public String getCodcig() {
    return codcig;
  }
  
  /**
   * @param codcig the codcig to set
   */
  public void setCodcig(String codcig) {
    this.codcig = codcig;
  }
  
  /**
   * @return the massim
   */
  public BigDecimal getMassim() {
    return massim;
  }
  
  /**
   * @param massim the massim to set
   */
  public void setMassim(BigDecimal massim) {
    this.massim = massim;
  }
  
  /**
   * @return the masest
   */
  public BigDecimal getMasest() {
    return masest;
  }
  
  /**
   * @param masest the masest to set
   */
  public void setMasest(BigDecimal masest) {
    this.masest = masest;
  }
  
  /**
   * @return the masres
   */
  public BigDecimal getMasres() {
    return masres;
  }
  
  /**
   * @param masres the masres to set
   */
  public void setMasres(BigDecimal masres) {
    this.masres = masres;
  }
  
  /**
   * @return the notega
   */
  public String getNotega() {
    return notega;
  }
  
  /**
   * @param notega the notega to set
   */
  public void setNotega(String notega) {
    this.notega = notega;
  }
  
  /**
   * @return the oteparg
   */
  public String getOteparg() {
    return oteparg;
  }
  
  /**
   * @param oteparg the oteparg to set
   */
  public void setOteparg(String oteparg) {
    this.oteparg = oteparg;
  }
  
  /**
   * @return the temesi
   */
  public Integer getTemesi() {
    return temesi;
  }
  
  /**
   * @param temesi the temesi to set
   */
  public void setTemesi(Integer temesi) {
    this.temesi = temesi;
  }
  
  /**
   * @return the masprod
   */
  public BigDecimal getMasprod() {
    return masprod;
  }
  
  /**
   * @param masprod the masprod to set
   */
  public void setMasprod(BigDecimal masprod) {
    this.masprod = masprod;
  }
  
  /**
   * @return the masprof
   */
  public BigDecimal getMasprof() {
    return masprof;
  }
  
  /**
   * @param masprof the masprof to set
   */
  public void setMasprof(BigDecimal masprof) {
    this.masprof = masprof;
  }
  
  /**
   * @return the masear
   */
  public BigDecimal getMasear() {
    return masear;
  }
  
  /**
   * @param masear the masear to set
   */
  public void setMasear(BigDecimal masear) {
    this.masear = masear;
  }
  
  /**
   * @return the impcom
   */
  public BigDecimal getImpcom() {
    return impcom;
  }
  
  /**
   * @param impcom the impcom to set
   */
  public void setImpcom(BigDecimal impcom) {
    this.impcom = impcom;
  }
  
  /**
   * @return the impliq
   */
  public BigDecimal getImpliq() {
    return impliq;
  }
  
  /**
   * @param impliq the impliq to set
   */
  public void setImpliq(BigDecimal impliq) {
    this.impliq = impliq;
  }
  
  /**
   * @return the datliq
   */
  public Date getDatliq() {
    return datliq;
  }
  
  /**
   * @param datliq the datliq to set
   */
  public void setDatliq(Date datliq) {
    this.datliq = datliq;
  }
  
  /**
   * @return the dcomng
   */
  public Date getDcomng() {
    return dcomng;
  }
  
  /**
   * @param dcomng the dcomng to set
   */
  public void setDcomng(Date dcomng) {
    this.dcomng = dcomng;
  }
  
  /**
   * @return the ncomng
   */
  public String getNcomng() {
    return ncomng;
  }
  
  /**
   * @param ncomng the ncomng to set
   */
  public void setNcomng(String ncomng) {
    this.ncomng = ncomng;
  }
  
  /**
   * @return the nrepat
   */
  public String getNrepat() {
    return nrepat;
  }
  
  /**
   * @param nrepat the nrepat to set
   */
  public void setNrepat(String nrepat) {
    this.nrepat = nrepat;
  }
  
  /**
   * @return the daatto
   */
  public Date getDaatto() {
    return daatto;
  }
  
  /**
   * @param daatto the daatto to set
   */
  public void setDaatto(Date daatto) {
    this.daatto = daatto;
  }
  
  /**
   * @return the dvprov
   */
  public Date getDvprov() {
    return dvprov;
  }
  
  /**
   * @param dvprov the dvprov to set
   */
  public void setDvprov(Date dvprov) {
    this.dvprov = dvprov;
  }
  
  /**
   * @return the nvprov
   */
  public String getNvprov() {
    return nvprov;
  }
  
  /**
   * @param nvprov the nvprov to set
   */
  public void setNvprov(String nvprov) {
    this.nvprov = nvprov;
  }
  
  /**
   * @return the codcom
   */
  public String getCodcom() {
    return codcom;
  }
  
  /**
   * @param codcom the codcom to set
   */
  public void setCodcom(String codcom) {
    this.codcom = codcom;
  }
  
  /**
   * @return the tipneg
   */
  public Integer getTipneg() {
    return tipneg;
  }
  
  /**
   * @param tipneg the tipneg to set
   */
  public void setTipneg(Integer tipneg) {
    this.tipneg = tipneg;
  }
  
  /**
   * @return the dittap
   */
  public String getDittap() {
    return dittap;
  }
  
  /**
   * @param dittap the dittap to set
   */
  public void setDittap(String dittap) {
    this.dittap = dittap;
  }
  
  /**
   * @return the ribpro
   */
  public BigDecimal getRibpro() {
    return ribpro;
  }
  
  /**
   * @param ribpro the ribpro to set
   */
  public void setRibpro(BigDecimal ribpro) {
    this.ribpro = ribpro;
  }
  
  /**
   * @return the iagpro
   */
  public BigDecimal getIagpro() {
    return iagpro;
  }
  
  /**
   * @param iagpro the iagpro to set
   */
  public void setIagpro(BigDecimal iagpro) {
    this.iagpro = iagpro;
  }
  
  /**
   * @return the vercan
   */
  public Date getVercan() {
    return vercan;
  }
  
  /**
   * @param vercan the vercan to set
   */
  public void setVercan(Date vercan) {
    this.vercan = vercan;
  }
  
  /**
   * @return the vernum
   */
  public String getVernum() {
    return vernum;
  }
  
  /**
   * @param vernum the vernum to set
   */
  public void setVernum(String vernum) {
    this.vernum = vernum;
  }
  
  /**
   * @return the genere
   */
  public Integer getGenere() {
    return genere;
  }
  
  /**
   * @param genere the genere to set
   */
  public void setGenere(Integer genere) {
    this.genere = genere;
  }
  
  /**
   * @return the ribcal
   */
  public Integer getRibcal() {
    return ribcal;
  }
  
  /**
   * @param ribcal the ribcal to set
   */
  public void setRibcal(Integer ribcal) {
    this.ribcal = ribcal;
  }
  
  /**
   * @return the calcsoang
   */
  public String getCalcsoang() {
    return calcsoang;
  }
  
  /**
   * @param calcsoang the calcsoang to set
   */
  public void setCalcsoang(String calcsoang) {
    this.calcsoang = calcsoang;
  }
  
  /**
   * @return the critlicg
   */
  public Integer getCritlicg() {
    return critlicg;
  }
  
  /**
   * @param critlicg the critlicg to set
   */
  public void setCritlicg(Integer critlicg) {
    this.critlicg = critlicg;
  }
  
  /**
   * @return the detlicg
   */
  public Integer getDetlicg() {
    return detlicg;
  }
  
  /**
   * @param detlicg the detlicg to set
   */
  public void setDetlicg(Integer detlicg) {
    this.detlicg = detlicg;
  }
  
  /**
   * @return the dvvreqsedris
   */
  public Date getDvvreqsedris() {
    return dvvreqsedris;
  }
  
  /**
   * @param dvvreqsedris the dvvreqsedris to set
   */
  public void setDvvreqsedris(Date dvvreqsedris) {
    this.dvvreqsedris = dvvreqsedris;
  }
  
  /**
   * @return the nlettrichcc
   */
  public String getNlettrichcc() {
    return nlettrichcc;
  }
  
  /**
   * @param nlettrichcc the nlettrichcc to set
   */
  public void setNlettrichcc(String nlettrichcc) {
    this.nlettrichcc = nlettrichcc;
  }
  
  /**
   * @return the dlettrichcc
   */
  public Date getDlettrichcc() {
    return dlettrichcc;
  }
  
  /**
   * @param dlettrichcc the dlettrichcc to set
   */
  public void setDlettrichcc(Date dlettrichcc) {
    this.dlettrichcc = dlettrichcc;
  }
  
  /**
   * @return the dtermprescc
   */
  public Date getDtermprescc() {
    return dtermprescc;
  }
  
  /**
   * @param dtermprescc the dtermprescc to set
   */
  public void setDtermprescc(Date dtermprescc) {
    this.dtermprescc = dtermprescc;
  }
  
  /**
   * @return the dvverchcc
   */
  public Date getDvverchcc() {
    return dvverchcc;
  }
  
  /**
   * @param dvverchcc the dvverchcc to set
   */
  public void setDvverchcc(Date dvverchcc) {
    this.dvverchcc = dvverchcc;
  }
  
  /**
   * @return the tattammescl
   */
  public Integer getTattammescl() {
    return tattammescl;
  }
  
  /**
   * @param tattammescl the tattammescl to set
   */
  public void setTattammescl(Integer tattammescl) {
    this.tattammescl = tattammescl;
  }
  
  /**
   * @return the nattammescl
   */
  public String getNattammescl() {
    return nattammescl;
  }
  
  /**
   * @param nattammescl the nattammescl to set
   */
  public void setNattammescl(String nattammescl) {
    this.nattammescl = nattammescl;
  }
  
  /**
   * @return the dattammescl
   */
  public Date getDattammescl() {
    return dattammescl;
  }
  
  /**
   * @param dattammescl the dattammescl to set
   */
  public void setDattammescl(Date dattammescl) {
    this.dattammescl = dattammescl;
  }
  
  /**
   * @return the nlettcomescl
   */
  public String getNlettcomescl() {
    return nlettcomescl;
  }
  
  /**
   * @param nlettcomescl the nlettcomescl to set
   */
  public void setNlettcomescl(String nlettcomescl) {
    this.nlettcomescl = nlettcomescl;
  }
  
  /**
   * @return the dlettcomescl
   */
  public Date getDlettcomescl() {
    return dlettcomescl;
  }
  
  /**
   * @param dlettcomescl the dlettcomescl to set
   */
  public void setDlettcomescl(Date dlettcomescl) {
    this.dlettcomescl = dlettcomescl;
  }
  
  /**
   * @return the dtermrichcdpg
   */
  public Date getDtermrichcdpg() {
    return dtermrichcdpg;
  }
  
  /**
   * @param dtermrichcdpg the dtermrichcdpg to set
   */
  public void setDtermrichcdpg(Date dtermrichcdpg) {
    this.dtermrichcdpg = dtermrichcdpg;
  }
  
  /**
   * @return the dtermrispcdpg
   */
  public Date getDtermrispcdpg() {
    return dtermrispcdpg;
  }
  
  /**
   * @param dtermrispcdpg the dtermrispcdpg to set
   */
  public void setDtermrispcdpg(Date dtermrispcdpg) {
    this.dtermrispcdpg = dtermrispcdpg;
  }
  
  /**
   * @return the dtermrichcpog
   */
  public Date getDtermrichcpog() {
    return dtermrichcpog;
  }
  
  /**
   * @param dtermrichcpog the dtermrichcpog to set
   */
  public void setDtermrichcpog(Date dtermrichcpog) {
    this.dtermrichcpog = dtermrichcpog;
  }
  
  /**
   * @return the dtermrispcpog
   */
  public Date getDtermrispcpog() {
    return dtermrispcpog;
  }
  
  /**
   * @param dtermrispcpog the dtermrispcpog to set
   */
  public void setDtermrispcpog(Date dtermrispcpog) {
    this.dtermrispcpog = dtermrispcpog;
  }
  
  /**
   * @return the dricesp
   */
  public Date getDricesp() {
    return dricesp;
  }
  
  /**
   * @param dricesp the dricesp to set
   */
  public void setDricesp(Date dricesp) {
    this.dricesp = dricesp;
  }
  
  /**
   * @return the dinvdoctec
   */
  public Date getDinvdoctec() {
    return dinvdoctec;
  }
  
  /**
   * @param dinvdoctec the dinvdoctec to set
   */
  public void setDinvdoctec(Date dinvdoctec) {
    this.dinvdoctec = dinvdoctec;
  }
  
  /**
   * @return the dconvditte
   */
  public Date getDconvditte() {
    return dconvditte;
  }
  
  /**
   * @param dconvditte the dconvditte to set
   */
  public void setDconvditte(Date dconvditte) {
    this.dconvditte = dconvditte;
  }
  
  /**
   * @return the stepgar
   */
  public Integer getStepgar() {
    return stepgar;
  }
  
  /**
   * @param stepgar the stepgar to set
   */
  public void setStepgar(Integer stepgar) {
    this.stepgar = stepgar;
  }
  
  /**
   * @return the dvapdocamm
   */
  public Date getDvapdocamm() {
    return dvapdocamm;
  }
  
  /**
   * @param dvapdocamm the dvapdocamm to set
   */
  public void setDvapdocamm(Date dvapdocamm) {
    this.dvapdocamm = dvapdocamm;
  }
  
  /**
   * @return the dvvidonoff
   */
  public Date getDvvidonoff() {
    return dvvidonoff;
  }
  
  /**
   * @param dvvidonoff the dvvidonoff to set
   */
  public void setDvvidonoff(Date dvvidonoff) {
    this.dvvidonoff = dvvidonoff;
  }
  
  /**
   * @return the dvvccdocamm
   */
  public Date getDvvccdocamm() {
    return dvvccdocamm;
  }
  
  /**
   * @param dvvccdocamm the dvvccdocamm to set
   */
  public void setDvvccdocamm(Date dvvccdocamm) {
    this.dvvccdocamm = dvvccdocamm;
  }
  
  /**
   * @return the dvcompreq
   */
  public Date getDvcompreq() {
    return dvcompreq;
  }
  
  /**
   * @param dvcompreq the dvcompreq to set
   */
  public void setDvcompreq(Date dvcompreq) {
    this.dvcompreq = dvcompreq;
  }
  
  /**
   * @return the notcompreq
   */
  public String getNotcompreq() {
    return notcompreq;
  }
  
  /**
   * @param notcompreq the notcompreq to set
   */
  public void setNotcompreq(String notcompreq) {
    this.notcompreq = notcompreq;
  }
  
  /**
   * @return the driccaptec
   */
  public Date getDriccaptec() {
    return driccaptec;
  }
  
  /**
   * @param driccaptec the driccaptec to set
   */
  public void setDriccaptec(Date driccaptec) {
    this.driccaptec = driccaptec;
  }
  
  /**
   * @return the dacqcig
   */
  public Date getDacqcig() {
    return dacqcig;
  }
  
  /**
   * @param dacqcig the dacqcig to set
   */
  public void setDacqcig(Date dacqcig) {
    this.dacqcig = dacqcig;
  }
  
  /**
   * @return the livpro
   */
  public Integer getLivpro() {
    return livpro;
  }
  
  /**
   * @param livpro the livpro to set
   */
  public void setLivpro(Integer livpro) {
    this.livpro = livpro;
  }
  
  /**
   * @return the nproti
   */
  public String getNproti() {
    return nproti;
  }
  
  /**
   * @param nproti the nproti to set
   */
  public void setNproti(String nproti) {
    this.nproti = nproti;
  }
  
  /**
   * @return the dinvit
   */
  public Date getDinvit() {
    return dinvit;
  }
  
  /**
   * @param dinvit the dinvit to set
   */
  public void setDinvit(Date dinvit) {
    this.dinvit = dinvit;
  }
  
  /**
   * @return the dsedpubeva
   */
  public Date getDsedpubeva() {
    return dsedpubeva;
  }
  
  /**
   * @param dsedpubeva the dsedpubeva to set
   */
  public void setDsedpubeva(Date dsedpubeva) {
    this.dsedpubeva = dsedpubeva;
  }
  
  /**
   * @return the davvprvreq
   */
  public Date getDavvprvreq() {
    return davvprvreq;
  }
  
  /**
   * @param davvprvreq the davvprvreq to set
   */
  public void setDavvprvreq(Date davvprvreq) {
    this.davvprvreq = davvprvreq;
  }
  
  /**
   * @return the drichdoccr
   */
  public Date getDrichdoccr() {
    return drichdoccr;
  }
  
  /**
   * @param drichdoccr the drichdoccr to set
   */
  public void setDrichdoccr(Date drichdoccr) {
    this.drichdoccr = drichdoccr;
  }
  
  /**
   * @return the driczdoccr
   */
  public Date getDriczdoccr() {
    return driczdoccr;
  }
  
  /**
   * @param driczdoccr the driczdoccr to set
   */
  public void setDriczdoccr(Date driczdoccr) {
    this.driczdoccr = driczdoccr;
  }
  
  /**
   * @return the dlettaggprov
   */
  public Date getDlettaggprov() {
    return dlettaggprov;
  }
  
  /**
   * @param dlettaggprov the dlettaggprov to set
   */
  public void setDlettaggprov(Date dlettaggprov) {
    this.dlettaggprov = dlettaggprov;
  }
  
  /**
   * @return the dtermdoccr
   */
  public Date getDtermdoccr() {
    return dtermdoccr;
  }
  
  /**
   * @param dtermdoccr the dtermdoccr to set
   */
  public void setDtermdoccr(Date dtermdoccr) {
    this.dtermdoccr = dtermdoccr;
  }
  
  /**
   * @return the nproreq
   */
  public String getNproreq() {
    return nproreq;
  }
  
  /**
   * @param nproreq the nproreq to set
   */
  public void setNproreq(String nproreq) {
    this.nproreq = nproreq;
  }
  
  /**
   * @return the dproaa
   */
  public Date getDproaa() {
    return dproaa;
  }
  
  /**
   * @param dproaa the dproaa to set
   */
  public void setDproaa(Date dproaa) {
    this.dproaa = dproaa;
  }
  
  /**
   * @return the taggeff
   */
  public Integer getTaggeff() {
    return taggeff;
  }
  
  /**
   * @param taggeff the taggeff to set
   */
  public void setTaggeff(Integer taggeff) {
    this.taggeff = taggeff;
  }
  
  /**
   * @return the naggeff
   */
  public String getNaggeff() {
    return naggeff;
  }
  
  /**
   * @param naggeff the naggeff to set
   */
  public void setNaggeff(String naggeff) {
    this.naggeff = naggeff;
  }
  
  /**
   * @return the daggeff
   */
  public Date getDaggeff() {
    return daggeff;
  }
  
  /**
   * @param daggeff the daggeff to set
   */
  public void setDaggeff(Date daggeff) {
    this.daggeff = daggeff;
  }
  
  /**
   * @return the dcomaggsa
   */
  public Date getDcomaggsa() {
    return dcomaggsa;
  }
  
  /**
   * @param dcomaggsa the dcomaggsa to set
   */
  public void setDcomaggsa(Date dcomaggsa) {
    this.dcomaggsa = dcomaggsa;
  }
  
  /**
   * @return the dcomdittagg
   */
  public Date getDcomdittagg() {
    return dcomdittagg;
  }
  
  /**
   * @param dcomdittagg the dcomdittagg to set
   */
  public void setDcomdittagg(Date dcomdittagg) {
    this.dcomdittagg = dcomdittagg;
  }
  
  /**
   * @return the ncomdittagg
   */
  public String getNcomdittagg() {
    return ncomdittagg;
  }
  
  /**
   * @param ncomdittagg the ncomdittagg to set
   */
  public void setNcomdittagg(String ncomdittagg) {
    this.ncomdittagg = ncomdittagg;
  }
  
  /**
   * @return the dcomdittnag
   */
  public Date getDcomdittnag() {
    return dcomdittnag;
  }
  
  /**
   * @param dcomdittnag the dcomdittnag to set
   */
  public void setDcomdittnag(Date dcomdittnag) {
    this.dcomdittnag = dcomdittnag;
  }
  
  /**
   * @return the ncomdittnag
   */
  public String getNcomdittnag() {
    return ncomdittnag;
  }
  
  /**
   * @param ncomdittnag the ncomdittnag to set
   */
  public void setNcomdittnag(String ncomdittnag) {
    this.ncomdittnag = ncomdittnag;
  }
  
  /**
   * @return the applegregg
   */
  public Integer getApplegregg() {
    return applegregg;
  }
  
  /**
   * @param applegregg the applegregg to set
   */
  public void setApplegregg(Integer applegregg) {
    this.applegregg = applegregg;
  }
  
  /**
   * @return the elencoe
   */
  public String getElencoe() {
    return elencoe;
  }
  
  /**
   * @param elencoe the elencoe to set
   */
  public void setElencoe(String elencoe) {
    this.elencoe = elencoe;
  }
  
  /**
   * @return the carrello
   */
  public String getCarrello() {
    return carrello;
  }
  
  /**
   * @param carrello the carrello to set
   */
  public void setCarrello(String carrello) {
    this.carrello = carrello;
  }
  
  /**
   * @return the nmaximo
   */
  public String getNmaximo() {
    return nmaximo;
  }
  
  /**
   * @param nmaximo the nmaximo to set
   */
  public void setNmaximo(String nmaximo) {
    this.nmaximo = nmaximo;
  }
  
  /**
   * @return the esineg
   */
  public Integer getEsineg() {
    return esineg;
  }
  
  /**
   * @param esineg the esineg to set
   */
  public void setEsineg(Integer esineg) {
    this.esineg = esineg;
  }
  
  /**
   * @return the onsogrib
   */
  public String getOnsogrib() {
    return onsogrib;
  }
  
  /**
   * @param onsogrib the onsogrib to set
   */
  public void setOnsogrib(String onsogrib) {
    this.onsogrib = onsogrib;
  }
  
  /**
   * @return the datneg
   */
  public Date getDatneg() {
    return datneg;
  }
  
  /**
   * @param datneg the datneg to set
   */
  public void setDatneg(Date datneg) {
    this.datneg = datneg;
  }
  
  /**
   * @return the idric
   */
  public BigDecimal getIdric() {
    return idric;
  }
  
  /**
   * @param idric the idric to set
   */
  public void setIdric(BigDecimal idric) {
    this.idric = idric;
  }
  
  /**
   * @return the bustalotti
   */
  public Integer getBustalotti() {
    return bustalotti;
  }
  
  /**
   * @param bustalotti the bustalotti to set
   */
  public void setBustalotti(Integer bustalotti) {
    this.bustalotti = bustalotti;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((alainf == null) ? 0 : alainf.hashCode());
    result = prime * result + ((alasup == null) ? 0 : alasup.hashCode());
    result = prime * result + ((appdef == null) ? 0 : appdef.hashCode());
    result = prime * result + ((applegregg == null) ? 0 : applegregg.hashCode());
    result = prime * result + ((bustalotti == null) ? 0 : bustalotti.hashCode());
    result = prime * result + ((calcsoang == null) ? 0 : calcsoang.hashCode());
    result = prime * result + ((carrello == null) ? 0 : carrello.hashCode());
    result = prime * result + ((catiga == null) ? 0 : catiga.hashCode());
    result = prime * result + ((clavor == null) ? 0 : clavor.hashCode());
    result = prime * result + ((cliv1 == null) ? 0 : cliv1.hashCode());
    result = prime * result + ((cliv2 == null) ? 0 : cliv2.hashCode());
    result = prime * result + ((codcig == null) ? 0 : codcig.hashCode());
    result = prime * result + ((codcom == null) ? 0 : codcom.hashCode());
    result = prime * result + ((coddef == null) ? 0 : coddef.hashCode());
    result = prime * result + ((codgar1 == null) ? 0 : codgar1.hashCode());
    result = prime * result + ((codiga == null) ? 0 : codiga.hashCode());
    result = prime * result + ((conten == null) ? 0 : conten.hashCode());
    result = prime * result + ((corgar1 == null) ? 0 : corgar1.hashCode());
    result = prime * result + ((critlicg == null) ? 0 : critlicg.hashCode());
    result = prime * result + ((cupmst == null) ? 0 : cupmst.hashCode());
    result = prime * result + ((cupprg == null) ? 0 : cupprg.hashCode());
    result = prime * result + ((daatto == null) ? 0 : daatto.hashCode());
    result = prime * result + ((dacert == null) ? 0 : dacert.hashCode());
    result = prime * result + ((dacqcig == null) ? 0 : dacqcig.hashCode());
    result = prime * result + ((daggeff == null) ? 0 : daggeff.hashCode());
    result = prime * result + ((datliq == null) ? 0 : datliq.hashCode());
    result = prime * result + ((datneg == null) ? 0 : datneg.hashCode());
    result = prime * result + ((dattammescl == null) ? 0 : dattammescl.hashCode());
    result = prime * result + ((dattoa == null) ? 0 : dattoa.hashCode());
    result = prime * result + ((dattog == null) ? 0 : dattog.hashCode());
    result = prime * result + ((davpre == null) ? 0 : davpre.hashCode());
    result = prime * result + ((davvigg == null) ? 0 : davvigg.hashCode());
    result = prime * result + ((davvprvreq == null) ? 0 : davvprvreq.hashCode());
    result = prime * result + ((dcomag == null) ? 0 : dcomag.hashCode());
    result = prime * result + ((dcomaggsa == null) ? 0 : dcomaggsa.hashCode());
    result = prime * result + ((dcomdittagg == null) ? 0 : dcomdittagg.hashCode());
    result = prime * result + ((dcomdittnag == null) ? 0 : dcomdittnag.hashCode());
    result = prime * result + ((dcomng == null) ? 0 : dcomng.hashCode());
    result = prime * result + ((dconvditte == null) ? 0 : dconvditte.hashCode());
    result = prime * result + ((desdoc == null) ? 0 : desdoc.hashCode());
    result = prime * result + ((desoff == null) ? 0 : desoff.hashCode());
    result = prime * result + ((detlicg == null) ? 0 : detlicg.hashCode());
    result = prime * result + ((dfpubag == null) ? 0 : dfpubag.hashCode());
    result = prime * result + ((dibandg == null) ? 0 : dibandg.hashCode());
    result = prime * result + ((dindoc == null) ? 0 : dindoc.hashCode());
    result = prime * result + ((dinlavg == null) ? 0 : dinlavg.hashCode());
    result = prime * result + ((dinvdoctec == null) ? 0 : dinvdoctec.hashCode());
    result = prime * result + ((dinvit == null) ? 0 : dinvit.hashCode());
    result = prime * result + ((ditta == null) ? 0 : ditta.hashCode());
    result = prime * result + ((dittap == null) ? 0 : dittap.hashCode());
    result = prime * result + ((dlettaggprov == null) ? 0 : dlettaggprov.hashCode());
    result = prime * result + ((dlettcomescl == null) ? 0 : dlettcomescl.hashCode());
    result = prime * result + ((dlettrichcc == null) ? 0 : dlettrichcc.hashCode());
    result = prime * result + ((dproaa == null) ? 0 : dproaa.hashCode());
    result = prime * result + ((dpubavg == null) ? 0 : dpubavg.hashCode());
    result = prime * result + ((dquiet == null) ? 0 : dquiet.hashCode());
    result = prime * result + ((driccaptec == null) ? 0 : driccaptec.hashCode());
    result = prime * result + ((dricesp == null) ? 0 : dricesp.hashCode());
    result = prime * result + ((drichdoccr == null) ? 0 : drichdoccr.hashCode());
    result = prime * result + ((driczdoccr == null) ? 0 : driczdoccr.hashCode());
    result = prime * result + ((dsedpubeva == null) ? 0 : dsedpubeva.hashCode());
    result = prime * result + ((dteoff == null) ? 0 : dteoff.hashCode());
    result = prime * result + ((dteparg == null) ? 0 : dteparg.hashCode());
    result = prime * result + ((dtermdoccr == null) ? 0 : dtermdoccr.hashCode());
    result = prime * result + ((dtermprescc == null) ? 0 : dtermprescc.hashCode());
    result = prime * result + ((dtermrichcdpg == null) ? 0 : dtermrichcdpg.hashCode());
    result = prime * result + ((dtermrichcpog == null) ? 0 : dtermrichcpog.hashCode());
    result = prime * result + ((dtermrispcdpg == null) ? 0 : dtermrispcdpg.hashCode());
    result = prime * result + ((dtermrispcpog == null) ? 0 : dtermrispcpog.hashCode());
    result = prime * result + ((dvapdocamm == null) ? 0 : dvapdocamm.hashCode());
    result = prime * result + ((dvcompreq == null) ? 0 : dvcompreq.hashCode());
    result = prime * result + ((dverag == null) ? 0 : dverag.hashCode());
    result = prime * result + ((dvprov == null) ? 0 : dvprov.hashCode());
    result = prime * result + ((dvvccdocamm == null) ? 0 : dvvccdocamm.hashCode());
    result = prime * result + ((dvverchcc == null) ? 0 : dvverchcc.hashCode());
    result = prime * result + ((dvvidonoff == null) ? 0 : dvvidonoff.hashCode());
    result = prime * result + ((dvvreqsedris == null) ? 0 : dvvreqsedris.hashCode());
    result = prime * result + ((elencoe == null) ? 0 : elencoe.hashCode());
    result = prime * result + ((esineg == null) ? 0 : esineg.hashCode());
    result = prime * result + ((estimp == null) ? 0 : estimp.hashCode());
    result = prime * result + ((fasgar == null) ? 0 : fasgar.hashCode());
    result = prime * result + ((garoff == null) ? 0 : garoff.hashCode());
    result = prime * result + ((genere == null) ? 0 : genere.hashCode());
    result = prime * result + ((iaggiu == null) ? 0 : iaggiu.hashCode());
    result = prime * result + ((iagpro == null) ? 0 : iagpro.hashCode());
    result = prime * result + ((idiaut == null) ? 0 : idiaut.hashCode());
    result = prime * result + ((idric == null) ? 0 : idric.hashCode());
    result = prime * result + ((impapp == null) ? 0 : impapp.hashCode());
    result = prime * result + ((impcom == null) ? 0 : impcom.hashCode());
    result = prime * result + ((impcor == null) ? 0 : impcor.hashCode());
    result = prime * result + ((impecu == null) ? 0 : impecu.hashCode());
    result = prime * result + ((impgar == null) ? 0 : impgar.hashCode());
    result = prime * result + ((impiga == null) ? 0 : impiga.hashCode());
    result = prime * result + ((impliq == null) ? 0 : impliq.hashCode());
    result = prime * result + ((impmis == null) ? 0 : impmis.hashCode());
    result = prime * result + ((impnrc == null) ? 0 : impnrc.hashCode());
    result = prime * result + ((impnrl == null) ? 0 : impnrl.hashCode());
    result = prime * result + ((impnrm == null) ? 0 : impnrm.hashCode());
    result = prime * result + ((impsco == null) ? 0 : impsco.hashCode());
    result = prime * result + ((impsic == null) ? 0 : impsic.hashCode());
    result = prime * result + ((impsmi == null) ? 0 : impsmi.hashCode());
    result = prime * result + ((indist == null) ? 0 : indist.hashCode());
    result = prime * result + ((istaut == null) ? 0 : istaut.hashCode());
    result = prime * result + ((istcre == null) ? 0 : istcre.hashCode());
    result = prime * result + ((ivalav == null) ? 0 : ivalav.hashCode());
    result = prime * result + ((limmax == null) ? 0 : limmax.hashCode());
    result = prime * result + ((limmin == null) ? 0 : limmin.hashCode());
    result = prime * result + ((livpro == null) ? 0 : livpro.hashCode());
    result = prime * result + ((locint == null) ? 0 : locint.hashCode());
    result = prime * result + ((loclav == null) ? 0 : loclav.hashCode());
    result = prime * result + ((masear == null) ? 0 : masear.hashCode());
    result = prime * result + ((masest == null) ? 0 : masest.hashCode());
    result = prime * result + ((masprod == null) ? 0 : masprod.hashCode());
    result = prime * result + ((masprof == null) ? 0 : masprof.hashCode());
    result = prime * result + ((masres == null) ? 0 : masres.hashCode());
    result = prime * result + ((massim == null) ? 0 : massim.hashCode());
    result = prime * result + ((media == null) ? 0 : media.hashCode());
    result = prime * result + ((modastg == null) ? 0 : modastg.hashCode());
    result = prime * result + ((modcau == null) ? 0 : modcau.hashCode());
    result = prime * result + ((modfin == null) ? 0 : modfin.hashCode());
    result = prime * result + ((modgarg == null) ? 0 : modgarg.hashCode());
    result = prime * result + ((modlicg == null) ? 0 : modlicg.hashCode());
    result = prime * result + ((motcon == null) ? 0 : motcon.hashCode());
    result = prime * result + ((motiva == null) ? 0 : motiva.hashCode());
    result = prime * result + ((naggeff == null) ? 0 : naggeff.hashCode());
    result = prime * result + ((nappfi == null) ? 0 : nappfi.hashCode());
    result = prime * result + ((nattammescl == null) ? 0 : nattammescl.hashCode());
    result = prime * result + ((nattoa == null) ? 0 : nattoa.hashCode());
    result = prime * result + ((nattog == null) ? 0 : nattog.hashCode());
    result = prime * result + ((navpre == null) ? 0 : navpre.hashCode());
    result = prime * result + ((navvigg == null) ? 0 : navvigg.hashCode());
    result = prime * result + ((ncomag == null) ? 0 : ncomag.hashCode());
    result = prime * result + ((ncomdittagg == null) ? 0 : ncomdittagg.hashCode());
    result = prime * result + ((ncomdittnag == null) ? 0 : ncomdittnag.hashCode());
    result = prime * result + ((ncomng == null) ? 0 : ncomng.hashCode());
    result = prime * result + ((nespe2 == null) ? 0 : nespe2.hashCode());
    result = prime * result + ((ngara == null) ? 0 : ngara.hashCode());
    result = prime * result + ((nlettcomescl == null) ? 0 : nlettcomescl.hashCode());
    result = prime * result + ((nlettrichcc == null) ? 0 : nlettrichcc.hashCode());
    result = prime * result + ((nmaximo == null) ? 0 : nmaximo.hashCode());
    result = prime * result + ((nofmed == null) ? 0 : nofmed.hashCode());
    result = prime * result + ((nofval == null) ? 0 : nofval.hashCode());
    result = prime * result + ((nomima == null) ? 0 : nomima.hashCode());
    result = prime * result + ((nomssl == null) ? 0 : nomssl.hashCode());
    result = prime * result + ((not_gar == null) ? 0 : not_gar.hashCode());
    result = prime * result + ((notcompreq == null) ? 0 : notcompreq.hashCode());
    result = prime * result + ((notcor == null) ? 0 : notcor.hashCode());
    result = prime * result + ((notega == null) ? 0 : notega.hashCode());
    result = prime * result + ((notmis == null) ? 0 : notmis.hashCode());
    result = prime * result + ((nproaa == null) ? 0 : nproaa.hashCode());
    result = prime * result + ((nproag == null) ? 0 : nproag.hashCode());
    result = prime * result + ((nproce == null) ? 0 : nproce.hashCode());
    result = prime * result + ((nproreq == null) ? 0 : nproreq.hashCode());
    result = prime * result + ((nproti == null) ? 0 : nproti.hashCode());
    result = prime * result + ((nprova == null) ? 0 : nprova.hashCode());
    result = prime * result + ((nquiet == null) ? 0 : nquiet.hashCode());
    result = prime * result + ((nrepat == null) ? 0 : nrepat.hashCode());
    result = prime * result + ((nsorte == null) ? 0 : nsorte.hashCode());
    result = prime * result + ((numele == null) ? 0 : numele.hashCode());
    result = prime * result + ((numera == null) ? 0 : numera.hashCode());
    result = prime * result + ((numssl == null) ? 0 : numssl.hashCode());
    result = prime * result + ((nvprov == null) ? 0 : nvprov.hashCode());
    result = prime * result + ((oesoff == null) ? 0 : oesoff.hashCode());
    result = prime * result + ((oggcont == null) ? 0 : oggcont.hashCode());
    result = prime * result + ((onprge == null) ? 0 : onprge.hashCode());
    result = prime * result + ((onsogrib == null) ? 0 : onsogrib.hashCode());
    result = prime * result + ((oteoff == null) ? 0 : oteoff.hashCode());
    result = prime * result + ((oteparg == null) ? 0 : oteparg.hashCode());
    result = prime * result + ((pgarof == null) ? 0 : pgarof.hashCode());
    result = prime * result + ((preced == null) ? 0 : preced.hashCode());
    result = prime * result + ((precut == null) ? 0 : precut.hashCode());
    result = prime * result + ((preinf == null) ? 0 : preinf.hashCode());
    result = prime * result + ((prosla == null) ? 0 : prosla.hashCode());
    result = prime * result + ((respre == null) ? 0 : respre.hashCode());
    result = prime * result + ((ribagg == null) ? 0 : ribagg.hashCode());
    result = prime * result + ((ribcal == null) ? 0 : ribcal.hashCode());
    result = prime * result + ((riboepv == null) ? 0 : riboepv.hashCode());
    result = prime * result + ((ribpro == null) ? 0 : ribpro.hashCode());
    result = prime * result + ((ricdoc == null) ? 0 : ricdoc.hashCode());
    result = prime * result + ((ricpre == null) ? 0 : ricpre.hashCode());
    result = prime * result + ((ridiso == null) ? 0 : ridiso.hashCode());
    result = prime * result + ((segreta == null) ? 0 : segreta.hashCode());
    result = prime * result + ((seguen == null) ? 0 : seguen.hashCode());
    result = prime * result + ((sicinc == null) ? 0 : sicinc.hashCode());
    result = prime * result + ((stepgar == null) ? 0 : stepgar.hashCode());
    result = prime * result + ((subgar == null) ? 0 : subgar.hashCode());
    result = prime * result + ((taggeff == null) ? 0 : taggeff.hashCode());
    result = prime * result + ((tattammescl == null) ? 0 : tattammescl.hashCode());
    result = prime * result + ((tattoa == null) ? 0 : tattoa.hashCode());
    result = prime * result + ((tattog == null) ? 0 : tattog.hashCode());
    result = prime * result + ((temesi == null) ? 0 : temesi.hashCode());
    result = prime * result + ((terrid == null) ? 0 : terrid.hashCode());
    result = prime * result + ((teutil == null) ? 0 : teutil.hashCode());
    result = prime * result + ((tiatto == null) ? 0 : tiatto.hashCode());
    result = prime * result + ((tipgarg == null) ? 0 : tipgarg.hashCode());
    result = prime * result + ((tiplav == null) ? 0 : tiplav.hashCode());
    result = prime * result + ((tipneg == null) ? 0 : tipneg.hashCode());
    result = prime * result + ((vercan == null) ? 0 : vercan.hashCode());
    result = prime * result + ((vernum == null) ? 0 : vernum.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Gare other = (Gare) obj;
    if (alainf == null) {
      if (other.alainf != null) return false;
    } else if (!alainf.equals(other.alainf)) return false;
    if (alasup == null) {
      if (other.alasup != null) return false;
    } else if (!alasup.equals(other.alasup)) return false;
    if (appdef == null) {
      if (other.appdef != null) return false;
    } else if (!appdef.equals(other.appdef)) return false;
    if (applegregg == null) {
      if (other.applegregg != null) return false;
    } else if (!applegregg.equals(other.applegregg)) return false;
    if (bustalotti == null) {
      if (other.bustalotti != null) return false;
    } else if (!bustalotti.equals(other.bustalotti)) return false;
    if (calcsoang == null) {
      if (other.calcsoang != null) return false;
    } else if (!calcsoang.equals(other.calcsoang)) return false;
    if (carrello == null) {
      if (other.carrello != null) return false;
    } else if (!carrello.equals(other.carrello)) return false;
    if (catiga == null) {
      if (other.catiga != null) return false;
    } else if (!catiga.equals(other.catiga)) return false;
    if (clavor == null) {
      if (other.clavor != null) return false;
    } else if (!clavor.equals(other.clavor)) return false;
    if (cliv1 == null) {
      if (other.cliv1 != null) return false;
    } else if (!cliv1.equals(other.cliv1)) return false;
    if (cliv2 == null) {
      if (other.cliv2 != null) return false;
    } else if (!cliv2.equals(other.cliv2)) return false;
    if (codcig == null) {
      if (other.codcig != null) return false;
    } else if (!codcig.equals(other.codcig)) return false;
    if (codcom == null) {
      if (other.codcom != null) return false;
    } else if (!codcom.equals(other.codcom)) return false;
    if (coddef == null) {
      if (other.coddef != null) return false;
    } else if (!coddef.equals(other.coddef)) return false;
    if (codgar1 == null) {
      if (other.codgar1 != null) return false;
    } else if (!codgar1.equals(other.codgar1)) return false;
    if (codiga == null) {
      if (other.codiga != null) return false;
    } else if (!codiga.equals(other.codiga)) return false;
    if (conten == null) {
      if (other.conten != null) return false;
    } else if (!conten.equals(other.conten)) return false;
    if (corgar1 == null) {
      if (other.corgar1 != null) return false;
    } else if (!corgar1.equals(other.corgar1)) return false;
    if (critlicg == null) {
      if (other.critlicg != null) return false;
    } else if (!critlicg.equals(other.critlicg)) return false;
    if (cupmst == null) {
      if (other.cupmst != null) return false;
    } else if (!cupmst.equals(other.cupmst)) return false;
    if (cupprg == null) {
      if (other.cupprg != null) return false;
    } else if (!cupprg.equals(other.cupprg)) return false;
    if (daatto == null) {
      if (other.daatto != null) return false;
    } else if (!daatto.equals(other.daatto)) return false;
    if (dacert == null) {
      if (other.dacert != null) return false;
    } else if (!dacert.equals(other.dacert)) return false;
    if (dacqcig == null) {
      if (other.dacqcig != null) return false;
    } else if (!dacqcig.equals(other.dacqcig)) return false;
    if (daggeff == null) {
      if (other.daggeff != null) return false;
    } else if (!daggeff.equals(other.daggeff)) return false;
    if (datliq == null) {
      if (other.datliq != null) return false;
    } else if (!datliq.equals(other.datliq)) return false;
    if (datneg == null) {
      if (other.datneg != null) return false;
    } else if (!datneg.equals(other.datneg)) return false;
    if (dattammescl == null) {
      if (other.dattammescl != null) return false;
    } else if (!dattammescl.equals(other.dattammescl)) return false;
    if (dattoa == null) {
      if (other.dattoa != null) return false;
    } else if (!dattoa.equals(other.dattoa)) return false;
    if (dattog == null) {
      if (other.dattog != null) return false;
    } else if (!dattog.equals(other.dattog)) return false;
    if (davpre == null) {
      if (other.davpre != null) return false;
    } else if (!davpre.equals(other.davpre)) return false;
    if (davvigg == null) {
      if (other.davvigg != null) return false;
    } else if (!davvigg.equals(other.davvigg)) return false;
    if (davvprvreq == null) {
      if (other.davvprvreq != null) return false;
    } else if (!davvprvreq.equals(other.davvprvreq)) return false;
    if (dcomag == null) {
      if (other.dcomag != null) return false;
    } else if (!dcomag.equals(other.dcomag)) return false;
    if (dcomaggsa == null) {
      if (other.dcomaggsa != null) return false;
    } else if (!dcomaggsa.equals(other.dcomaggsa)) return false;
    if (dcomdittagg == null) {
      if (other.dcomdittagg != null) return false;
    } else if (!dcomdittagg.equals(other.dcomdittagg)) return false;
    if (dcomdittnag == null) {
      if (other.dcomdittnag != null) return false;
    } else if (!dcomdittnag.equals(other.dcomdittnag)) return false;
    if (dcomng == null) {
      if (other.dcomng != null) return false;
    } else if (!dcomng.equals(other.dcomng)) return false;
    if (dconvditte == null) {
      if (other.dconvditte != null) return false;
    } else if (!dconvditte.equals(other.dconvditte)) return false;
    if (desdoc == null) {
      if (other.desdoc != null) return false;
    } else if (!desdoc.equals(other.desdoc)) return false;
    if (desoff == null) {
      if (other.desoff != null) return false;
    } else if (!desoff.equals(other.desoff)) return false;
    if (detlicg == null) {
      if (other.detlicg != null) return false;
    } else if (!detlicg.equals(other.detlicg)) return false;
    if (dfpubag == null) {
      if (other.dfpubag != null) return false;
    } else if (!dfpubag.equals(other.dfpubag)) return false;
    if (dibandg == null) {
      if (other.dibandg != null) return false;
    } else if (!dibandg.equals(other.dibandg)) return false;
    if (dindoc == null) {
      if (other.dindoc != null) return false;
    } else if (!dindoc.equals(other.dindoc)) return false;
    if (dinlavg == null) {
      if (other.dinlavg != null) return false;
    } else if (!dinlavg.equals(other.dinlavg)) return false;
    if (dinvdoctec == null) {
      if (other.dinvdoctec != null) return false;
    } else if (!dinvdoctec.equals(other.dinvdoctec)) return false;
    if (dinvit == null) {
      if (other.dinvit != null) return false;
    } else if (!dinvit.equals(other.dinvit)) return false;
    if (ditta == null) {
      if (other.ditta != null) return false;
    } else if (!ditta.equals(other.ditta)) return false;
    if (dittap == null) {
      if (other.dittap != null) return false;
    } else if (!dittap.equals(other.dittap)) return false;
    if (dlettaggprov == null) {
      if (other.dlettaggprov != null) return false;
    } else if (!dlettaggprov.equals(other.dlettaggprov)) return false;
    if (dlettcomescl == null) {
      if (other.dlettcomescl != null) return false;
    } else if (!dlettcomescl.equals(other.dlettcomescl)) return false;
    if (dlettrichcc == null) {
      if (other.dlettrichcc != null) return false;
    } else if (!dlettrichcc.equals(other.dlettrichcc)) return false;
    if (dproaa == null) {
      if (other.dproaa != null) return false;
    } else if (!dproaa.equals(other.dproaa)) return false;
    if (dpubavg == null) {
      if (other.dpubavg != null) return false;
    } else if (!dpubavg.equals(other.dpubavg)) return false;
    if (dquiet == null) {
      if (other.dquiet != null) return false;
    } else if (!dquiet.equals(other.dquiet)) return false;
    if (driccaptec == null) {
      if (other.driccaptec != null) return false;
    } else if (!driccaptec.equals(other.driccaptec)) return false;
    if (dricesp == null) {
      if (other.dricesp != null) return false;
    } else if (!dricesp.equals(other.dricesp)) return false;
    if (drichdoccr == null) {
      if (other.drichdoccr != null) return false;
    } else if (!drichdoccr.equals(other.drichdoccr)) return false;
    if (driczdoccr == null) {
      if (other.driczdoccr != null) return false;
    } else if (!driczdoccr.equals(other.driczdoccr)) return false;
    if (dsedpubeva == null) {
      if (other.dsedpubeva != null) return false;
    } else if (!dsedpubeva.equals(other.dsedpubeva)) return false;
    if (dteoff == null) {
      if (other.dteoff != null) return false;
    } else if (!dteoff.equals(other.dteoff)) return false;
    if (dteparg == null) {
      if (other.dteparg != null) return false;
    } else if (!dteparg.equals(other.dteparg)) return false;
    if (dtermdoccr == null) {
      if (other.dtermdoccr != null) return false;
    } else if (!dtermdoccr.equals(other.dtermdoccr)) return false;
    if (dtermprescc == null) {
      if (other.dtermprescc != null) return false;
    } else if (!dtermprescc.equals(other.dtermprescc)) return false;
    if (dtermrichcdpg == null) {
      if (other.dtermrichcdpg != null) return false;
    } else if (!dtermrichcdpg.equals(other.dtermrichcdpg)) return false;
    if (dtermrichcpog == null) {
      if (other.dtermrichcpog != null) return false;
    } else if (!dtermrichcpog.equals(other.dtermrichcpog)) return false;
    if (dtermrispcdpg == null) {
      if (other.dtermrispcdpg != null) return false;
    } else if (!dtermrispcdpg.equals(other.dtermrispcdpg)) return false;
    if (dtermrispcpog == null) {
      if (other.dtermrispcpog != null) return false;
    } else if (!dtermrispcpog.equals(other.dtermrispcpog)) return false;
    if (dvapdocamm == null) {
      if (other.dvapdocamm != null) return false;
    } else if (!dvapdocamm.equals(other.dvapdocamm)) return false;
    if (dvcompreq == null) {
      if (other.dvcompreq != null) return false;
    } else if (!dvcompreq.equals(other.dvcompreq)) return false;
    if (dverag == null) {
      if (other.dverag != null) return false;
    } else if (!dverag.equals(other.dverag)) return false;
    if (dvprov == null) {
      if (other.dvprov != null) return false;
    } else if (!dvprov.equals(other.dvprov)) return false;
    if (dvvccdocamm == null) {
      if (other.dvvccdocamm != null) return false;
    } else if (!dvvccdocamm.equals(other.dvvccdocamm)) return false;
    if (dvverchcc == null) {
      if (other.dvverchcc != null) return false;
    } else if (!dvverchcc.equals(other.dvverchcc)) return false;
    if (dvvidonoff == null) {
      if (other.dvvidonoff != null) return false;
    } else if (!dvvidonoff.equals(other.dvvidonoff)) return false;
    if (dvvreqsedris == null) {
      if (other.dvvreqsedris != null) return false;
    } else if (!dvvreqsedris.equals(other.dvvreqsedris)) return false;
    if (elencoe == null) {
      if (other.elencoe != null) return false;
    } else if (!elencoe.equals(other.elencoe)) return false;
    if (esineg == null) {
      if (other.esineg != null) return false;
    } else if (!esineg.equals(other.esineg)) return false;
    if (estimp == null) {
      if (other.estimp != null) return false;
    } else if (!estimp.equals(other.estimp)) return false;
    if (fasgar == null) {
      if (other.fasgar != null) return false;
    } else if (!fasgar.equals(other.fasgar)) return false;
    if (garoff == null) {
      if (other.garoff != null) return false;
    } else if (!garoff.equals(other.garoff)) return false;
    if (genere == null) {
      if (other.genere != null) return false;
    } else if (!genere.equals(other.genere)) return false;
    if (iaggiu == null) {
      if (other.iaggiu != null) return false;
    } else if (!iaggiu.equals(other.iaggiu)) return false;
    if (iagpro == null) {
      if (other.iagpro != null) return false;
    } else if (!iagpro.equals(other.iagpro)) return false;
    if (idiaut == null) {
      if (other.idiaut != null) return false;
    } else if (!idiaut.equals(other.idiaut)) return false;
    if (idric == null) {
      if (other.idric != null) return false;
    } else if (!idric.equals(other.idric)) return false;
    if (impapp == null) {
      if (other.impapp != null) return false;
    } else if (!impapp.equals(other.impapp)) return false;
    if (impcom == null) {
      if (other.impcom != null) return false;
    } else if (!impcom.equals(other.impcom)) return false;
    if (impcor == null) {
      if (other.impcor != null) return false;
    } else if (!impcor.equals(other.impcor)) return false;
    if (impecu == null) {
      if (other.impecu != null) return false;
    } else if (!impecu.equals(other.impecu)) return false;
    if (impgar == null) {
      if (other.impgar != null) return false;
    } else if (!impgar.equals(other.impgar)) return false;
    if (impiga == null) {
      if (other.impiga != null) return false;
    } else if (!impiga.equals(other.impiga)) return false;
    if (impliq == null) {
      if (other.impliq != null) return false;
    } else if (!impliq.equals(other.impliq)) return false;
    if (impmis == null) {
      if (other.impmis != null) return false;
    } else if (!impmis.equals(other.impmis)) return false;
    if (impnrc == null) {
      if (other.impnrc != null) return false;
    } else if (!impnrc.equals(other.impnrc)) return false;
    if (impnrl == null) {
      if (other.impnrl != null) return false;
    } else if (!impnrl.equals(other.impnrl)) return false;
    if (impnrm == null) {
      if (other.impnrm != null) return false;
    } else if (!impnrm.equals(other.impnrm)) return false;
    if (impsco == null) {
      if (other.impsco != null) return false;
    } else if (!impsco.equals(other.impsco)) return false;
    if (impsic == null) {
      if (other.impsic != null) return false;
    } else if (!impsic.equals(other.impsic)) return false;
    if (impsmi == null) {
      if (other.impsmi != null) return false;
    } else if (!impsmi.equals(other.impsmi)) return false;
    if (indist == null) {
      if (other.indist != null) return false;
    } else if (!indist.equals(other.indist)) return false;
    if (istaut == null) {
      if (other.istaut != null) return false;
    } else if (!istaut.equals(other.istaut)) return false;
    if (istcre == null) {
      if (other.istcre != null) return false;
    } else if (!istcre.equals(other.istcre)) return false;
    if (ivalav == null) {
      if (other.ivalav != null) return false;
    } else if (!ivalav.equals(other.ivalav)) return false;
    if (limmax == null) {
      if (other.limmax != null) return false;
    } else if (!limmax.equals(other.limmax)) return false;
    if (limmin == null) {
      if (other.limmin != null) return false;
    } else if (!limmin.equals(other.limmin)) return false;
    if (livpro == null) {
      if (other.livpro != null) return false;
    } else if (!livpro.equals(other.livpro)) return false;
    if (locint == null) {
      if (other.locint != null) return false;
    } else if (!locint.equals(other.locint)) return false;
    if (loclav == null) {
      if (other.loclav != null) return false;
    } else if (!loclav.equals(other.loclav)) return false;
    if (masear == null) {
      if (other.masear != null) return false;
    } else if (!masear.equals(other.masear)) return false;
    if (masest == null) {
      if (other.masest != null) return false;
    } else if (!masest.equals(other.masest)) return false;
    if (masprod == null) {
      if (other.masprod != null) return false;
    } else if (!masprod.equals(other.masprod)) return false;
    if (masprof == null) {
      if (other.masprof != null) return false;
    } else if (!masprof.equals(other.masprof)) return false;
    if (masres == null) {
      if (other.masres != null) return false;
    } else if (!masres.equals(other.masres)) return false;
    if (massim == null) {
      if (other.massim != null) return false;
    } else if (!massim.equals(other.massim)) return false;
    if (media == null) {
      if (other.media != null) return false;
    } else if (!media.equals(other.media)) return false;
    if (modastg == null) {
      if (other.modastg != null) return false;
    } else if (!modastg.equals(other.modastg)) return false;
    if (modcau == null) {
      if (other.modcau != null) return false;
    } else if (!modcau.equals(other.modcau)) return false;
    if (modfin == null) {
      if (other.modfin != null) return false;
    } else if (!modfin.equals(other.modfin)) return false;
    if (modgarg == null) {
      if (other.modgarg != null) return false;
    } else if (!modgarg.equals(other.modgarg)) return false;
    if (modlicg == null) {
      if (other.modlicg != null) return false;
    } else if (!modlicg.equals(other.modlicg)) return false;
    if (motcon == null) {
      if (other.motcon != null) return false;
    } else if (!motcon.equals(other.motcon)) return false;
    if (motiva == null) {
      if (other.motiva != null) return false;
    } else if (!motiva.equals(other.motiva)) return false;
    if (naggeff == null) {
      if (other.naggeff != null) return false;
    } else if (!naggeff.equals(other.naggeff)) return false;
    if (nappfi == null) {
      if (other.nappfi != null) return false;
    } else if (!nappfi.equals(other.nappfi)) return false;
    if (nattammescl == null) {
      if (other.nattammescl != null) return false;
    } else if (!nattammescl.equals(other.nattammescl)) return false;
    if (nattoa == null) {
      if (other.nattoa != null) return false;
    } else if (!nattoa.equals(other.nattoa)) return false;
    if (nattog == null) {
      if (other.nattog != null) return false;
    } else if (!nattog.equals(other.nattog)) return false;
    if (navpre == null) {
      if (other.navpre != null) return false;
    } else if (!navpre.equals(other.navpre)) return false;
    if (navvigg == null) {
      if (other.navvigg != null) return false;
    } else if (!navvigg.equals(other.navvigg)) return false;
    if (ncomag == null) {
      if (other.ncomag != null) return false;
    } else if (!ncomag.equals(other.ncomag)) return false;
    if (ncomdittagg == null) {
      if (other.ncomdittagg != null) return false;
    } else if (!ncomdittagg.equals(other.ncomdittagg)) return false;
    if (ncomdittnag == null) {
      if (other.ncomdittnag != null) return false;
    } else if (!ncomdittnag.equals(other.ncomdittnag)) return false;
    if (ncomng == null) {
      if (other.ncomng != null) return false;
    } else if (!ncomng.equals(other.ncomng)) return false;
    if (nespe2 == null) {
      if (other.nespe2 != null) return false;
    } else if (!nespe2.equals(other.nespe2)) return false;
    if (ngara == null) {
      if (other.ngara != null) return false;
    } else if (!ngara.equals(other.ngara)) return false;
    if (nlettcomescl == null) {
      if (other.nlettcomescl != null) return false;
    } else if (!nlettcomescl.equals(other.nlettcomescl)) return false;
    if (nlettrichcc == null) {
      if (other.nlettrichcc != null) return false;
    } else if (!nlettrichcc.equals(other.nlettrichcc)) return false;
    if (nmaximo == null) {
      if (other.nmaximo != null) return false;
    } else if (!nmaximo.equals(other.nmaximo)) return false;
    if (nofmed == null) {
      if (other.nofmed != null) return false;
    } else if (!nofmed.equals(other.nofmed)) return false;
    if (nofval == null) {
      if (other.nofval != null) return false;
    } else if (!nofval.equals(other.nofval)) return false;
    if (nomima == null) {
      if (other.nomima != null) return false;
    } else if (!nomima.equals(other.nomima)) return false;
    if (nomssl == null) {
      if (other.nomssl != null) return false;
    } else if (!nomssl.equals(other.nomssl)) return false;
    if (not_gar == null) {
      if (other.not_gar != null) return false;
    } else if (!not_gar.equals(other.not_gar)) return false;
    if (notcompreq == null) {
      if (other.notcompreq != null) return false;
    } else if (!notcompreq.equals(other.notcompreq)) return false;
    if (notcor == null) {
      if (other.notcor != null) return false;
    } else if (!notcor.equals(other.notcor)) return false;
    if (notega == null) {
      if (other.notega != null) return false;
    } else if (!notega.equals(other.notega)) return false;
    if (notmis == null) {
      if (other.notmis != null) return false;
    } else if (!notmis.equals(other.notmis)) return false;
    if (nproaa == null) {
      if (other.nproaa != null) return false;
    } else if (!nproaa.equals(other.nproaa)) return false;
    if (nproag == null) {
      if (other.nproag != null) return false;
    } else if (!nproag.equals(other.nproag)) return false;
    if (nproce == null) {
      if (other.nproce != null) return false;
    } else if (!nproce.equals(other.nproce)) return false;
    if (nproreq == null) {
      if (other.nproreq != null) return false;
    } else if (!nproreq.equals(other.nproreq)) return false;
    if (nproti == null) {
      if (other.nproti != null) return false;
    } else if (!nproti.equals(other.nproti)) return false;
    if (nprova == null) {
      if (other.nprova != null) return false;
    } else if (!nprova.equals(other.nprova)) return false;
    if (nquiet == null) {
      if (other.nquiet != null) return false;
    } else if (!nquiet.equals(other.nquiet)) return false;
    if (nrepat == null) {
      if (other.nrepat != null) return false;
    } else if (!nrepat.equals(other.nrepat)) return false;
    if (nsorte == null) {
      if (other.nsorte != null) return false;
    } else if (!nsorte.equals(other.nsorte)) return false;
    if (numele == null) {
      if (other.numele != null) return false;
    } else if (!numele.equals(other.numele)) return false;
    if (numera == null) {
      if (other.numera != null) return false;
    } else if (!numera.equals(other.numera)) return false;
    if (numssl == null) {
      if (other.numssl != null) return false;
    } else if (!numssl.equals(other.numssl)) return false;
    if (nvprov == null) {
      if (other.nvprov != null) return false;
    } else if (!nvprov.equals(other.nvprov)) return false;
    if (oesoff == null) {
      if (other.oesoff != null) return false;
    } else if (!oesoff.equals(other.oesoff)) return false;
    if (oggcont == null) {
      if (other.oggcont != null) return false;
    } else if (!oggcont.equals(other.oggcont)) return false;
    if (onprge == null) {
      if (other.onprge != null) return false;
    } else if (!onprge.equals(other.onprge)) return false;
    if (onsogrib == null) {
      if (other.onsogrib != null) return false;
    } else if (!onsogrib.equals(other.onsogrib)) return false;
    if (oteoff == null) {
      if (other.oteoff != null) return false;
    } else if (!oteoff.equals(other.oteoff)) return false;
    if (oteparg == null) {
      if (other.oteparg != null) return false;
    } else if (!oteparg.equals(other.oteparg)) return false;
    if (pgarof == null) {
      if (other.pgarof != null) return false;
    } else if (!pgarof.equals(other.pgarof)) return false;
    if (preced == null) {
      if (other.preced != null) return false;
    } else if (!preced.equals(other.preced)) return false;
    if (precut == null) {
      if (other.precut != null) return false;
    } else if (!precut.equals(other.precut)) return false;
    if (preinf == null) {
      if (other.preinf != null) return false;
    } else if (!preinf.equals(other.preinf)) return false;
    if (prosla == null) {
      if (other.prosla != null) return false;
    } else if (!prosla.equals(other.prosla)) return false;
    if (respre == null) {
      if (other.respre != null) return false;
    } else if (!respre.equals(other.respre)) return false;
    if (ribagg == null) {
      if (other.ribagg != null) return false;
    } else if (!ribagg.equals(other.ribagg)) return false;
    if (ribcal == null) {
      if (other.ribcal != null) return false;
    } else if (!ribcal.equals(other.ribcal)) return false;
    if (riboepv == null) {
      if (other.riboepv != null) return false;
    } else if (!riboepv.equals(other.riboepv)) return false;
    if (ribpro == null) {
      if (other.ribpro != null) return false;
    } else if (!ribpro.equals(other.ribpro)) return false;
    if (ricdoc == null) {
      if (other.ricdoc != null) return false;
    } else if (!ricdoc.equals(other.ricdoc)) return false;
    if (ricpre == null) {
      if (other.ricpre != null) return false;
    } else if (!ricpre.equals(other.ricpre)) return false;
    if (ridiso == null) {
      if (other.ridiso != null) return false;
    } else if (!ridiso.equals(other.ridiso)) return false;
    if (segreta == null) {
      if (other.segreta != null) return false;
    } else if (!segreta.equals(other.segreta)) return false;
    if (seguen == null) {
      if (other.seguen != null) return false;
    } else if (!seguen.equals(other.seguen)) return false;
    if (sicinc == null) {
      if (other.sicinc != null) return false;
    } else if (!sicinc.equals(other.sicinc)) return false;
    if (stepgar == null) {
      if (other.stepgar != null) return false;
    } else if (!stepgar.equals(other.stepgar)) return false;
    if (subgar == null) {
      if (other.subgar != null) return false;
    } else if (!subgar.equals(other.subgar)) return false;
    if (taggeff == null) {
      if (other.taggeff != null) return false;
    } else if (!taggeff.equals(other.taggeff)) return false;
    if (tattammescl == null) {
      if (other.tattammescl != null) return false;
    } else if (!tattammescl.equals(other.tattammescl)) return false;
    if (tattoa == null) {
      if (other.tattoa != null) return false;
    } else if (!tattoa.equals(other.tattoa)) return false;
    if (tattog == null) {
      if (other.tattog != null) return false;
    } else if (!tattog.equals(other.tattog)) return false;
    if (temesi == null) {
      if (other.temesi != null) return false;
    } else if (!temesi.equals(other.temesi)) return false;
    if (terrid == null) {
      if (other.terrid != null) return false;
    } else if (!terrid.equals(other.terrid)) return false;
    if (teutil == null) {
      if (other.teutil != null) return false;
    } else if (!teutil.equals(other.teutil)) return false;
    if (tiatto == null) {
      if (other.tiatto != null) return false;
    } else if (!tiatto.equals(other.tiatto)) return false;
    if (tipgarg == null) {
      if (other.tipgarg != null) return false;
    } else if (!tipgarg.equals(other.tipgarg)) return false;
    if (tiplav == null) {
      if (other.tiplav != null) return false;
    } else if (!tiplav.equals(other.tiplav)) return false;
    if (tipneg == null) {
      if (other.tipneg != null) return false;
    } else if (!tipneg.equals(other.tipneg)) return false;
    if (vercan == null) {
      if (other.vercan != null) return false;
    } else if (!vercan.equals(other.vercan)) return false;
    if (vernum == null) {
      if (other.vernum != null) return false;
    } else if (!vernum.equals(other.vernum)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Gare [" + (ngara != null ? "ngara=" + ngara + ", " : "") + (codgar1 != null ? "codgar1=" + codgar1 : "") + "]";
  }
  
  
}
