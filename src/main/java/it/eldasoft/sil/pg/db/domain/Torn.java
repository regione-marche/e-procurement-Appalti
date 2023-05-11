package it.eldasoft.sil.pg.db.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Questa classe mappa interamente la tabella TORN
 * senza distinzione tra attributi e campi a DB
 * @author gabriele.nencini
 *
 */
@JsonInclude(value = Include.NON_EMPTY)
public class Torn {
  private String codgar;
  private String tiptor;
  private BigDecimal corgar;
  private Integer tattot;
  private Date dattot;
  private String nattot;
  private String noteat;
  private String nproat;
  private Date dgara;
  private String ogara;
  private Date desoff;
  private String oesoff;
  private String locgar;
  private Integer tipgar;
  private Date modast;
  private Integer modlic;
  private String modgar;
  private String offaum;
  private Integer nofdit;
  private Integer ngadit;
  private String navvig;
  private Date davvig;
  private Date dpubav;
  private Date dfpuba;
  private Date diband;
  private Date dtepar;
  private String otepar;
  private String locpre;
  private Date dteoff;
  private String oteoff;
  private String locoff;
  private String valoff;
  private Date dinvit;
  private String nproti;
  private String docgar;
  private String nridoc;
  private Date dridoc;
  private Date dindoc;
  private Date desdoc;
  private Integer tipgen;
  private String destor;
  private BigDecimal imptor;
  private BigDecimal cliv1;
  private BigDecimal cliv2;
  private Integer oggcont;
  private String prourg;
  private String preinf;
  private String terrid;
  private String banweb;
  private String docweb;
  private String cenint;
  private String codrup;
  private String isarchi;
  private String acqalt;
  private String codnuts;
  private Integer offlot;
  private String ammvar;
  private String desqua;
  private String ammopz;
  private String desopz;
  private String ammrin;
  private String desrin;
  private String modfin;
  private String motacc;
  private Integer numope;
  private Integer minope;
  private Integer maxope;
  private String selope;
  private String profas;
  private String elepar;
  private Date datdoc;
  private String oradoc;
  private BigDecimal impdoc;
  private String pagdoc;
  private String progeu;
  private String infcom;
  private String accser;
  private BigDecimal istaut;
  private Integer tipneg;
  private String altneg;
  private Integer profiloweb;
  private String numavcp;
  private String calcsoan;
  private Integer critlic;
  private Integer detlic;
  private Integer tipforn;
  private Date dtermrichcdp;
  private Date dtermrispcdp;
  private Date dtermrichcpo;
  private Date dtermrispcpo;
  private String accappub;
  private String prerispp;
  private String ricastae;
  private String pubprecsa;
  private String pubprecd;
  private String apfinfc;
  private String livacq;
  private Integer applegreg;
  private Integer annorif;
  private String codscp;
  private String urlscp;
  private String codavscp;
  private String urlavscp;
  private Integer selpar;
  private Integer esineg;
  private Date datneg;
  private Integer tattoc;
  private String norma;
  private Integer iterga;
  private Integer norma1;
  private Integer uffdet;
  private String uuid;
  private String urega;
  private String gartel;
  private Integer offtel;
  private String compreq;
  private Integer pcopre;
  private Integer pcodoc;
  private Integer pcooff;
  private Integer pcogar;
  private Integer tus;
  private String codgarcli;
  private Date datrict;
  private String npnominacomm;
  private Date dpubavviso;
  private Date dtpubavviso;
  private Integer contoeco;
  private String valtec;
  private String nobustamm;
  private Integer idcommalbo;
  private String accqua;
  private Integer aqoper;
  private Integer aqnumope;
  private Integer aqdurata;
  private Integer aqtempo;
  private Integer altrisog;
  private Integer ultdetlic;
  private String isadesione;
  private String codcigaq;
  private String ngaraaq;
  private Integer modcont;
  private String urbasco;
  private String sommaur;
  private Integer tiplav;
  private BigDecimal aeribmin;
  private BigDecimal aeribmax;
  private BigDecimal aeimpmin;
  private BigDecimal aeimpmax;
  private Integer aemodvis;
  private String aenote;
  private Date dultagg;
  private String modrea;
  private String ricmano;
  private Integer modmano;
  private String settore;
  private String inversa;
  private String sortinv;
  private String garpriv;
  private Integer prerib;
  private String isgreen;
  private String desgreen;
  
  /**
   * @return the codgar
   */
  public String getCodgar() {
    return codgar;
  }
  
  /**
   * @param codgar the codgar to set
   */
  public void setCodgar(String codgar) {
    this.codgar = codgar;
  }
  
  /**
   * @return the tiptor
   */
  public String getTiptor() {
    return tiptor;
  }
  
  /**
   * @param tiptor the tiptor to set
   */
  public void setTiptor(String tiptor) {
    this.tiptor = tiptor;
  }
  
  /**
   * @return the corgar
   */
  public BigDecimal getCorgar() {
    return corgar;
  }
  
  /**
   * @param corgar the corgar to set
   */
  public void setCorgar(BigDecimal corgar) {
    this.corgar = corgar;
  }
  
  /**
   * @return the tattot
   */
  public Integer getTattot() {
    return tattot;
  }
  
  /**
   * @param tattot the tattot to set
   */
  public void setTattot(Integer tattot) {
    this.tattot = tattot;
  }
  
  /**
   * @return the dattot
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDattot() {
    return dattot;
  }
  
  /**
   * @param dattot the dattot to set
   */
  public void setDattot(Date dattot) {
    this.dattot = dattot;
  }
  
  /**
   * @return the nattot
   */
  public String getNattot() {
    return nattot;
  }
  
  /**
   * @param nattot the nattot to set
   */
  public void setNattot(String nattot) {
    this.nattot = nattot;
  }
  
  /**
   * @return the noteat
   */
  public String getNoteat() {
    return noteat;
  }
  
  /**
   * @param noteat the noteat to set
   */
  public void setNoteat(String noteat) {
    this.noteat = noteat;
  }
  
  /**
   * @return the nproat
   */
  public String getNproat() {
    return nproat;
  }
  
  /**
   * @param nproat the nproat to set
   */
  public void setNproat(String nproat) {
    this.nproat = nproat;
  }
  
  /**
   * @return the dgara
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDgara() {
    return dgara;
  }
  
  /**
   * @param dgara the dgara to set
   */
  public void setDgara(Date dgara) {
    this.dgara = dgara;
  }
  
  /**
   * @return the ogara
   */
  public String getOgara() {
    return ogara;
  }
  
  /**
   * @param ogara the ogara to set
   */
  public void setOgara(String ogara) {
    this.ogara = ogara;
  }
  
  /**
   * @return the desoff
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
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
   * @return the locgar
   */
  public String getLocgar() {
    return locgar;
  }
  
  /**
   * @param locgar the locgar to set
   */
  public void setLocgar(String locgar) {
    this.locgar = locgar;
  }
  
  /**
   * @return the tipgar
   */
  public Integer getTipgar() {
    return tipgar;
  }
  
  /**
   * @param tipgar the tipgar to set
   */
  public void setTipgar(Integer tipgar) {
    this.tipgar = tipgar;
  }
  
  /**
   * @return the modast
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getModast() {
    return modast;
  }
  
  /**
   * @param modast the modast to set
   */
  public void setModast(Date modast) {
    this.modast = modast;
  }
  
  /**
   * @return the modlic
   */
  public Integer getModlic() {
    return modlic;
  }
  
  /**
   * @param modlic the modlic to set
   */
  public void setModlic(Integer modlic) {
    this.modlic = modlic;
  }
  
  /**
   * @return the modgar
   */
  public String getModgar() {
    return modgar;
  }
  
  /**
   * @param modgar the modgar to set
   */
  public void setModgar(String modgar) {
    this.modgar = modgar;
  }
  
  /**
   * @return the offaum
   */
  public String getOffaum() {
    return offaum;
  }
  
  /**
   * @param offaum the offaum to set
   */
  public void setOffaum(String offaum) {
    this.offaum = offaum;
  }
  
  /**
   * @return the nofdit
   */
  public Integer getNofdit() {
    return nofdit;
  }
  
  /**
   * @param nofdit the nofdit to set
   */
  public void setNofdit(Integer nofdit) {
    this.nofdit = nofdit;
  }
  
  /**
   * @return the ngadit
   */
  public Integer getNgadit() {
    return ngadit;
  }
  
  /**
   * @param ngadit the ngadit to set
   */
  public void setNgadit(Integer ngadit) {
    this.ngadit = ngadit;
  }
  
  /**
   * @return the navvig
   */
  public String getNavvig() {
    return navvig;
  }
  
  /**
   * @param navvig the navvig to set
   */
  public void setNavvig(String navvig) {
    this.navvig = navvig;
  }
  
  /**
   * @return the davvig
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDavvig() {
    return davvig;
  }
  
  /**
   * @param davvig the davvig to set
   */
  public void setDavvig(Date davvig) {
    this.davvig = davvig;
  }
  
  /**
   * @return the dpubav
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDpubav() {
    return dpubav;
  }
  
  /**
   * @param dpubav the dpubav to set
   */
  public void setDpubav(Date dpubav) {
    this.dpubav = dpubav;
  }
  
  /**
   * @return the dfpuba
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDfpuba() {
    return dfpuba;
  }
  
  /**
   * @param dfpuba the dfpuba to set
   */
  public void setDfpuba(Date dfpuba) {
    this.dfpuba = dfpuba;
  }
  
  /**
   * @return the diband
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDiband() {
    return diband;
  }
  
  /**
   * @param diband the diband to set
   */
  public void setDiband(Date diband) {
    this.diband = diband;
  }
  
  /**
   * @return the dtepar
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDtepar() {
    return dtepar;
  }
  
  /**
   * @param dtepar the dtepar to set
   */
  public void setDtepar(Date dtepar) {
    this.dtepar = dtepar;
  }
  
  /**
   * @return the otepar
   */
  public String getOtepar() {
    return otepar;
  }
  
  /**
   * @param otepar the otepar to set
   */
  public void setOtepar(String otepar) {
    this.otepar = otepar;
  }
  
  /**
   * @return the locpre
   */
  public String getLocpre() {
    return locpre;
  }
  
  /**
   * @param locpre the locpre to set
   */
  public void setLocpre(String locpre) {
    this.locpre = locpre;
  }
  
  /**
   * @return the dteoff
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
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
   * @return the locoff
   */
  public String getLocoff() {
    return locoff;
  }
  
  /**
   * @param locoff the locoff to set
   */
  public void setLocoff(String locoff) {
    this.locoff = locoff;
  }
  
  /**
   * @return the valoff
   */
  public String getValoff() {
    return valoff;
  }
  
  /**
   * @param valoff the valoff to set
   */
  public void setValoff(String valoff) {
    this.valoff = valoff;
  }
  
  /**
   * @return the dinvit
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
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
   * @return the docgar
   */
  public String getDocgar() {
    return docgar;
  }
  
  /**
   * @param docgar the docgar to set
   */
  public void setDocgar(String docgar) {
    this.docgar = docgar;
  }
  
  /**
   * @return the nridoc
   */
  public String getNridoc() {
    return nridoc;
  }
  
  /**
   * @param nridoc the nridoc to set
   */
  public void setNridoc(String nridoc) {
    this.nridoc = nridoc;
  }
  
  /**
   * @return the dridoc
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDridoc() {
    return dridoc;
  }
  
  /**
   * @param dridoc the dridoc to set
   */
  public void setDridoc(Date dridoc) {
    this.dridoc = dridoc;
  }
  
  /**
   * @return the dindoc
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
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
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
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
   * @return the tipgen
   */
  public Integer getTipgen() {
    return tipgen;
  }
  
  /**
   * @param tipgen the tipgen to set
   */
  public void setTipgen(Integer tipgen) {
    this.tipgen = tipgen;
  }
  
  /**
   * @return the destor
   */
  public String getDestor() {
    return destor;
  }
  
  /**
   * @param destor the destor to set
   */
  public void setDestor(String destor) {
    this.destor = destor;
  }
  
  /**
   * @return the imptor
   */
  public BigDecimal getImptor() {
    return imptor;
  }
  
  /**
   * @param imptor the imptor to set
   */
  public void setImptor(BigDecimal imptor) {
    this.imptor = imptor;
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
   * @return the prourg
   */
  public String getProurg() {
    return prourg;
  }
  
  /**
   * @param prourg the prourg to set
   */
  public void setProurg(String prourg) {
    this.prourg = prourg;
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
   * @return the banweb
   */
  public String getBanweb() {
    return banweb;
  }
  
  /**
   * @param banweb the banweb to set
   */
  public void setBanweb(String banweb) {
    this.banweb = banweb;
  }
  
  /**
   * @return the docweb
   */
  public String getDocweb() {
    return docweb;
  }
  
  /**
   * @param docweb the docweb to set
   */
  public void setDocweb(String docweb) {
    this.docweb = docweb;
  }
  
  /**
   * @return the cenint
   */
  public String getCenint() {
    return cenint;
  }
  
  /**
   * @param cenint the cenint to set
   */
  public void setCenint(String cenint) {
    this.cenint = cenint;
  }
  
  /**
   * @return the codrup
   */
  public String getCodrup() {
    return codrup;
  }
  
  /**
   * @param codrup the codrup to set
   */
  public void setCodrup(String codrup) {
    this.codrup = codrup;
  }
  
  /**
   * @return the isarchi
   */
  public String getIsarchi() {
    return isarchi;
  }
  
  /**
   * @param isarchi the isarchi to set
   */
  public void setIsarchi(String isarchi) {
    this.isarchi = isarchi;
  }
  
  /**
   * @return the acqalt
   */
  public String getAcqalt() {
    return acqalt;
  }
  
  /**
   * @param acqalt the acqalt to set
   */
  public void setAcqalt(String acqalt) {
    this.acqalt = acqalt;
  }
  
  /**
   * @return the codnuts
   */
  public String getCodnuts() {
    return codnuts;
  }
  
  /**
   * @param codnuts the codnuts to set
   */
  public void setCodnuts(String codnuts) {
    this.codnuts = codnuts;
  }
  
  /**
   * @return the offlot
   */
  public Integer getOfflot() {
    return offlot;
  }
  
  /**
   * @param offlot the offlot to set
   */
  public void setOfflot(Integer offlot) {
    this.offlot = offlot;
  }
  
  /**
   * @return the ammvar
   */
  public String getAmmvar() {
    return ammvar;
  }
  
  /**
   * @param ammvar the ammvar to set
   */
  public void setAmmvar(String ammvar) {
    this.ammvar = ammvar;
  }
  
  /**
   * @return the desqua
   */
  public String getDesqua() {
    return desqua;
  }
  
  /**
   * @param desqua the desqua to set
   */
  public void setDesqua(String desqua) {
    this.desqua = desqua;
  }
  
  /**
   * @return the ammopz
   */
  public String getAmmopz() {
    return ammopz;
  }
  
  /**
   * @param ammopz the ammopz to set
   */
  public void setAmmopz(String ammopz) {
    this.ammopz = ammopz;
  }
  
  /**
   * @return the desopz
   */
  public String getDesopz() {
    return desopz;
  }
  
  /**
   * @param desopz the desopz to set
   */
  public void setDesopz(String desopz) {
    this.desopz = desopz;
  }
  
  /**
   * @return the ammrin
   */
  public String getAmmrin() {
    return ammrin;
  }
  
  /**
   * @param ammrin the ammrin to set
   */
  public void setAmmrin(String ammrin) {
    this.ammrin = ammrin;
  }
  
  /**
   * @return the desrin
   */
  public String getDesrin() {
    return desrin;
  }
  
  /**
   * @param desrin the desrin to set
   */
  public void setDesrin(String desrin) {
    this.desrin = desrin;
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
   * @return the motacc
   */
  public String getMotacc() {
    return motacc;
  }
  
  /**
   * @param motacc the motacc to set
   */
  public void setMotacc(String motacc) {
    this.motacc = motacc;
  }
  
  /**
   * @return the numope
   */
  public Integer getNumope() {
    return numope;
  }
  
  /**
   * @param numope the numope to set
   */
  public void setNumope(Integer numope) {
    this.numope = numope;
  }
  
  /**
   * @return the minope
   */
  public Integer getMinope() {
    return minope;
  }
  
  /**
   * @param minope the minope to set
   */
  public void setMinope(Integer minope) {
    this.minope = minope;
  }
  
  /**
   * @return the maxope
   */
  public Integer getMaxope() {
    return maxope;
  }
  
  /**
   * @param maxope the maxope to set
   */
  public void setMaxope(Integer maxope) {
    this.maxope = maxope;
  }
  
  /**
   * @return the selope
   */
  public String getSelope() {
    return selope;
  }
  
  /**
   * @param selope the selope to set
   */
  public void setSelope(String selope) {
    this.selope = selope;
  }
  
  /**
   * @return the profas
   */
  public String getProfas() {
    return profas;
  }
  
  /**
   * @param profas the profas to set
   */
  public void setProfas(String profas) {
    this.profas = profas;
  }
  
  /**
   * @return the elepar
   */
  public String getElepar() {
    return elepar;
  }
  
  /**
   * @param elepar the elepar to set
   */
  public void setElepar(String elepar) {
    this.elepar = elepar;
  }
  
  /**
   * @return the datdoc
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDatdoc() {
    return datdoc;
  }
  
  /**
   * @param datdoc the datdoc to set
   */
  public void setDatdoc(Date datdoc) {
    this.datdoc = datdoc;
  }
  
  /**
   * @return the oradoc
   */
  public String getOradoc() {
    return oradoc;
  }
  
  /**
   * @param oradoc the oradoc to set
   */
  public void setOradoc(String oradoc) {
    this.oradoc = oradoc;
  }
  
  /**
   * @return the impdoc
   */
  public BigDecimal getImpdoc() {
    return impdoc;
  }
  
  /**
   * @param impdoc the impdoc to set
   */
  public void setImpdoc(BigDecimal impdoc) {
    this.impdoc = impdoc;
  }
  
  /**
   * @return the pagdoc
   */
  public String getPagdoc() {
    return pagdoc;
  }
  
  /**
   * @param pagdoc the pagdoc to set
   */
  public void setPagdoc(String pagdoc) {
    this.pagdoc = pagdoc;
  }
  
  /**
   * @return the progeu
   */
  public String getProgeu() {
    return progeu;
  }
  
  /**
   * @param progeu the progeu to set
   */
  public void setProgeu(String progeu) {
    this.progeu = progeu;
  }
  
  /**
   * @return the infcom
   */
  public String getInfcom() {
    return infcom;
  }
  
  /**
   * @param infcom the infcom to set
   */
  public void setInfcom(String infcom) {
    this.infcom = infcom;
  }
  
  /**
   * @return the accser
   */
  public String getAccser() {
    return accser;
  }
  
  /**
   * @param accser the accser to set
   */
  public void setAccser(String accser) {
    this.accser = accser;
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
   * @return the altneg
   */
  public String getAltneg() {
    return altneg;
  }
  
  /**
   * @param altneg the altneg to set
   */
  public void setAltneg(String altneg) {
    this.altneg = altneg;
  }
  
  /**
   * @return the profiloweb
   */
  public Integer getProfiloweb() {
    return profiloweb;
  }
  
  /**
   * @param profiloweb the profiloweb to set
   */
  public void setProfiloweb(Integer profiloweb) {
    this.profiloweb = profiloweb;
  }
  
  /**
   * @return the numavcp
   */
  public String getNumavcp() {
    return numavcp;
  }
  
  /**
   * @param numavcp the numavcp to set
   */
  public void setNumavcp(String numavcp) {
    this.numavcp = numavcp;
  }
  
  /**
   * @return the calcsoan
   */
  public String getCalcsoan() {
    return calcsoan;
  }
  
  /**
   * @param calcsoan the calcsoan to set
   */
  public void setCalcsoan(String calcsoan) {
    this.calcsoan = calcsoan;
  }
  
  /**
   * @return the critlic
   */
  public Integer getCritlic() {
    return critlic;
  }
  
  /**
   * @param critlic the critlic to set
   */
  public void setCritlic(Integer critlic) {
    this.critlic = critlic;
  }
  
  /**
   * @return the detlic
   */
  public Integer getDetlic() {
    return detlic;
  }
  
  /**
   * @param detlic the detlic to set
   */
  public void setDetlic(Integer detlic) {
    this.detlic = detlic;
  }
  
  /**
   * @return the tipforn
   */
  public Integer getTipforn() {
    return tipforn;
  }
  
  /**
   * @param tipforn the tipforn to set
   */
  public void setTipforn(Integer tipforn) {
    this.tipforn = tipforn;
  }
  
  /**
   * @return the dtermrichcdp
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDtermrichcdp() {
    return dtermrichcdp;
  }
  
  /**
   * @param dtermrichcdp the dtermrichcdp to set
   */
  public void setDtermrichcdp(Date dtermrichcdp) {
    this.dtermrichcdp = dtermrichcdp;
  }
  
  /**
   * @return the dtermrispcdp
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDtermrispcdp() {
    return dtermrispcdp;
  }
  
  /**
   * @param dtermrispcdp the dtermrispcdp to set
   */
  public void setDtermrispcdp(Date dtermrispcdp) {
    this.dtermrispcdp = dtermrispcdp;
  }
  
  /**
   * @return the dtermrichcpo
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDtermrichcpo() {
    return dtermrichcpo;
  }
  
  /**
   * @param dtermrichcpo the dtermrichcpo to set
   */
  public void setDtermrichcpo(Date dtermrichcpo) {
    this.dtermrichcpo = dtermrichcpo;
  }
  
  /**
   * @return the dtermrispcpo
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDtermrispcpo() {
    return dtermrispcpo;
  }
  
  /**
   * @param dtermrispcpo the dtermrispcpo to set
   */
  public void setDtermrispcpo(Date dtermrispcpo) {
    this.dtermrispcpo = dtermrispcpo;
  }
  
  /**
   * @return the accappub
   */
  public String getAccappub() {
    return accappub;
  }
  
  /**
   * @param accappub the accappub to set
   */
  public void setAccappub(String accappub) {
    this.accappub = accappub;
  }
  
  /**
   * @return the prerispp
   */
  public String getPrerispp() {
    return prerispp;
  }
  
  /**
   * @param prerispp the prerispp to set
   */
  public void setPrerispp(String prerispp) {
    this.prerispp = prerispp;
  }
  
  /**
   * @return the ricastae
   */
  public String getRicastae() {
    return ricastae;
  }
  
  /**
   * @param ricastae the ricastae to set
   */
  public void setRicastae(String ricastae) {
    this.ricastae = ricastae;
  }
  
  /**
   * @return the pubprecsa
   */
  public String getPubprecsa() {
    return pubprecsa;
  }
  
  /**
   * @param pubprecsa the pubprecsa to set
   */
  public void setPubprecsa(String pubprecsa) {
    this.pubprecsa = pubprecsa;
  }
  
  /**
   * @return the pubprecd
   */
  public String getPubprecd() {
    return pubprecd;
  }
  
  /**
   * @param pubprecd the pubprecd to set
   */
  public void setPubprecd(String pubprecd) {
    this.pubprecd = pubprecd;
  }
  
  /**
   * @return the apfinfc
   */
  public String getApfinfc() {
    return apfinfc;
  }
  
  /**
   * @param apfinfc the apfinfc to set
   */
  public void setApfinfc(String apfinfc) {
    this.apfinfc = apfinfc;
  }
  
  /**
   * @return the livacq
   */
  public String getLivacq() {
    return livacq;
  }
  
  /**
   * @param livacq the livacq to set
   */
  public void setLivacq(String livacq) {
    this.livacq = livacq;
  }
  
  /**
   * @return the applegreg
   */
  public Integer getApplegreg() {
    return applegreg;
  }
  
  /**
   * @param applegreg the applegreg to set
   */
  public void setApplegreg(Integer applegreg) {
    this.applegreg = applegreg;
  }
  
  /**
   * @return the annorif
   */
  public Integer getAnnorif() {
    return annorif;
  }
  
  /**
   * @param annorif the annorif to set
   */
  public void setAnnorif(Integer annorif) {
    this.annorif = annorif;
  }
  
  /**
   * @return the codscp
   */
  public String getCodscp() {
    return codscp;
  }
  
  /**
   * @param codscp the codscp to set
   */
  public void setCodscp(String codscp) {
    this.codscp = codscp;
  }
  
  /**
   * @return the urlscp
   */
  public String getUrlscp() {
    return urlscp;
  }
  
  /**
   * @param urlscp the urlscp to set
   */
  public void setUrlscp(String urlscp) {
    this.urlscp = urlscp;
  }
  
  /**
   * @return the codavscp
   */
  public String getCodavscp() {
    return codavscp;
  }
  
  /**
   * @param codavscp the codavscp to set
   */
  public void setCodavscp(String codavscp) {
    this.codavscp = codavscp;
  }
  
  /**
   * @return the urlavscp
   */
  public String getUrlavscp() {
    return urlavscp;
  }
  
  /**
   * @param urlavscp the urlavscp to set
   */
  public void setUrlavscp(String urlavscp) {
    this.urlavscp = urlavscp;
  }
  
  /**
   * @return the selpar
   */
  public Integer getSelpar() {
    return selpar;
  }
  
  /**
   * @param selpar the selpar to set
   */
  public void setSelpar(Integer selpar) {
    this.selpar = selpar;
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
   * @return the datneg
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
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
   * @return the tattoc
   */
  public Integer getTattoc() {
    return tattoc;
  }
  
  /**
   * @param tattoc the tattoc to set
   */
  public void setTattoc(Integer tattoc) {
    this.tattoc = tattoc;
  }
  
  /**
   * @return the norma
   */
  public String getNorma() {
    return norma;
  }
  
  /**
   * @param norma the norma to set
   */
  public void setNorma(String norma) {
    this.norma = norma;
  }
  
  /**
   * @return the iterga
   */
  public Integer getIterga() {
    return iterga;
  }
  
  /**
   * @param iterga the iterga to set
   */
  public void setIterga(Integer iterga) {
    this.iterga = iterga;
  }
  
  /**
   * @return the norma1
   */
  public Integer getNorma1() {
    return norma1;
  }
  
  /**
   * @param norma1 the norma1 to set
   */
  public void setNorma1(Integer norma1) {
    this.norma1 = norma1;
  }
  
  /**
   * @return the uffdet
   */
  public Integer getUffdet() {
    return uffdet;
  }
  
  /**
   * @param uffdet the uffdet to set
   */
  public void setUffdet(Integer uffdet) {
    this.uffdet = uffdet;
  }
  
  /**
   * @return the uuid
   */
  public String getUuid() {
    return uuid;
  }
  
  /**
   * @param uuid the uuid to set
   */
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }
  
  /**
   * @return the urega
   */
  public String getUrega() {
    return urega;
  }
  
  /**
   * @param urega the urega to set
   */
  public void setUrega(String urega) {
    this.urega = urega;
  }
  
  /**
   * @return the gartel
   */
  public String getGartel() {
    return gartel;
  }
  
  /**
   * @param gartel the gartel to set
   */
  public void setGartel(String gartel) {
    this.gartel = gartel;
  }
  
  /**
   * @return the offtel
   */
  public Integer getOfftel() {
    return offtel;
  }
  
  /**
   * @param offtel the offtel to set
   */
  public void setOfftel(Integer offtel) {
    this.offtel = offtel;
  }
  
  /**
   * @return the compreq
   */
  public String getCompreq() {
    return compreq;
  }
  
  /**
   * @param compreq the compreq to set
   */
  public void setCompreq(String compreq) {
    this.compreq = compreq;
  }
  
  /**
   * @return the pcopre
   */
  public Integer getPcopre() {
    return pcopre;
  }
  
  /**
   * @param pcopre the pcopre to set
   */
  public void setPcopre(Integer pcopre) {
    this.pcopre = pcopre;
  }
  
  /**
   * @return the pcodoc
   */
  public Integer getPcodoc() {
    return pcodoc;
  }
  
  /**
   * @param pcodoc the pcodoc to set
   */
  public void setPcodoc(Integer pcodoc) {
    this.pcodoc = pcodoc;
  }
  
  /**
   * @return the pcooff
   */
  public Integer getPcooff() {
    return pcooff;
  }
  
  /**
   * @param pcooff the pcooff to set
   */
  public void setPcooff(Integer pcooff) {
    this.pcooff = pcooff;
  }
  
  /**
   * @return the pcogar
   */
  public Integer getPcogar() {
    return pcogar;
  }
  
  /**
   * @param pcogar the pcogar to set
   */
  public void setPcogar(Integer pcogar) {
    this.pcogar = pcogar;
  }
  
  /**
   * @return the tus
   */
  public Integer getTus() {
    return tus;
  }
  
  /**
   * @param tus the tus to set
   */
  public void setTus(Integer tus) {
    this.tus = tus;
  }
  
  /**
   * @return the codgarcli
   */
  public String getCodgarcli() {
    return codgarcli;
  }
  
  /**
   * @param codgarcli the codgarcli to set
   */
  public void setCodgarcli(String codgarcli) {
    this.codgarcli = codgarcli;
  }
  
  /**
   * @return the datrict
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDatrict() {
    return datrict;
  }
  
  /**
   * @param datrict the datrict to set
   */
  public void setDatrict(Date datrict) {
    this.datrict = datrict;
  }
  
  /**
   * @return the npnominacomm
   */
  public String getNpnominacomm() {
    return npnominacomm;
  }
  
  /**
   * @param npnominacomm the npnominacomm to set
   */
  public void setNpnominacomm(String npnominacomm) {
    this.npnominacomm = npnominacomm;
  }
  
  /**
   * @return the dpubavviso
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDpubavviso() {
    return dpubavviso;
  }
  
  /**
   * @param dpubavviso the dpubavviso to set
   */
  public void setDpubavviso(Date dpubavviso) {
    this.dpubavviso = dpubavviso;
  }
  
  /**
   * @return the dtpubavviso
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDtpubavviso() {
    return dtpubavviso;
  }
  
  /**
   * @param dtpubavviso the dtpubavviso to set
   */
  public void setDtpubavviso(Date dtpubavviso) {
    this.dtpubavviso = dtpubavviso;
  }
  
  /**
   * @return the contoeco
   */
  public Integer getContoeco() {
    return contoeco;
  }
  
  /**
   * @param contoeco the contoeco to set
   */
  public void setContoeco(Integer contoeco) {
    this.contoeco = contoeco;
  }
  
  /**
   * @return the valtec
   */
  public String getValtec() {
    return valtec;
  }
  
  /**
   * @param valtec the valtec to set
   */
  public void setValtec(String valtec) {
    this.valtec = valtec;
  }
  
  /**
   * @return the nobustamm
   */
  public String getNobustamm() {
    return nobustamm;
  }
  
  /**
   * @param nobustamm the nobustamm to set
   */
  public void setNobustamm(String nobustamm) {
    this.nobustamm = nobustamm;
  }
  
  /**
   * @return the idcommalbo
   */
  public Integer getIdcommalbo() {
    return idcommalbo;
  }
  
  /**
   * @param idcommalbo the idcommalbo to set
   */
  public void setIdcommalbo(Integer idcommalbo) {
    this.idcommalbo = idcommalbo;
  }
  
  /**
   * @return the accqua
   */
  public String getAccqua() {
    return accqua;
  }
  
  /**
   * @param accqua the accqua to set
   */
  public void setAccqua(String accqua) {
    this.accqua = accqua;
  }
  
  /**
   * @return the aqoper
   */
  public Integer getAqoper() {
    return aqoper;
  }
  
  /**
   * @param aqoper the aqoper to set
   */
  public void setAqoper(Integer aqoper) {
    this.aqoper = aqoper;
  }
  
  /**
   * @return the aqnumope
   */
  public Integer getAqnumope() {
    return aqnumope;
  }
  
  /**
   * @param aqnumope the aqnumope to set
   */
  public void setAqnumope(Integer aqnumope) {
    this.aqnumope = aqnumope;
  }
  
  /**
   * @return the aqdurata
   */
  public Integer getAqdurata() {
    return aqdurata;
  }
  
  /**
   * @param aqdurata the aqdurata to set
   */
  public void setAqdurata(Integer aqdurata) {
    this.aqdurata = aqdurata;
  }
  
  /**
   * @return the aqtempo
   */
  public Integer getAqtempo() {
    return aqtempo;
  }
  
  /**
   * @param aqtempo the aqtempo to set
   */
  public void setAqtempo(Integer aqtempo) {
    this.aqtempo = aqtempo;
  }
  
  /**
   * @return the altrisog
   */
  public Integer getAltrisog() {
    return altrisog;
  }
  
  /**
   * @param altrisog the altrisog to set
   */
  public void setAltrisog(Integer altrisog) {
    this.altrisog = altrisog;
  }
  
  /**
   * @return the ultdetlic
   */
  public Integer getUltdetlic() {
    return ultdetlic;
  }
  
  /**
   * @param ultdetlic the ultdetlic to set
   */
  public void setUltdetlic(Integer ultdetlic) {
    this.ultdetlic = ultdetlic;
  }
  
  /**
   * @return the isadesione
   */
  public String getIsadesione() {
    return isadesione;
  }
  
  /**
   * @param isadesione the isadesione to set
   */
  public void setIsadesione(String isadesione) {
    this.isadesione = isadesione;
  }
  
  /**
   * @return the codcigaq
   */
  public String getCodcigaq() {
    return codcigaq;
  }
  
  /**
   * @param codcigaq the codcigaq to set
   */
  public void setCodcigaq(String codcigaq) {
    this.codcigaq = codcigaq;
  }
  
  /**
   * @return the ngaraaq
   */
  public String getNgaraaq() {
    return ngaraaq;
  }
  
  /**
   * @param ngaraaq the ngaraaq to set
   */
  public void setNgaraaq(String ngaraaq) {
    this.ngaraaq = ngaraaq;
  }
  
  /**
   * @return the modcont
   */
  public Integer getModcont() {
    return modcont;
  }
  
  /**
   * @param modcont the modcont to set
   */
  public void setModcont(Integer modcont) {
    this.modcont = modcont;
  }
  
  /**
   * @return the urbasco
   */
  public String getUrbasco() {
    return urbasco;
  }
  
  /**
   * @param urbasco the urbasco to set
   */
  public void setUrbasco(String urbasco) {
    this.urbasco = urbasco;
  }
  
  /**
   * @return the sommaur
   */
  public String getSommaur() {
    return sommaur;
  }
  
  /**
   * @param sommaur the sommaur to set
   */
  public void setSommaur(String sommaur) {
    this.sommaur = sommaur;
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
   * @return the aeribmin
   */
  public BigDecimal getAeribmin() {
    return aeribmin;
  }
  
  /**
   * @param aeribmin the aeribmin to set
   */
  public void setAeribmin(BigDecimal aeribmin) {
    this.aeribmin = aeribmin;
  }
  
  /**
   * @return the aeribmax
   */
  public BigDecimal getAeribmax() {
    return aeribmax;
  }
  
  /**
   * @param aeribmax the aeribmax to set
   */
  public void setAeribmax(BigDecimal aeribmax) {
    this.aeribmax = aeribmax;
  }
  
  /**
   * @return the aeimpmin
   */
  public BigDecimal getAeimpmin() {
    return aeimpmin;
  }
  
  /**
   * @param aeimpmin the aeimpmin to set
   */
  public void setAeimpmin(BigDecimal aeimpmin) {
    this.aeimpmin = aeimpmin;
  }
  
  /**
   * @return the aeimpmax
   */
  public BigDecimal getAeimpmax() {
    return aeimpmax;
  }
  
  /**
   * @param aeimpmax the aeimpmax to set
   */
  public void setAeimpmax(BigDecimal aeimpmax) {
    this.aeimpmax = aeimpmax;
  }
  
  /**
   * @return the aemodvis
   */
  public Integer getAemodvis() {
    return aemodvis;
  }
  
  /**
   * @param aemodvis the aemodvis to set
   */
  public void setAemodvis(Integer aemodvis) {
    this.aemodvis = aemodvis;
  }
  
  /**
   * @return the aenote
   */
  public String getAenote() {
    return aenote;
  }
  
  /**
   * @param aenote the aenote to set
   */
  public void setAenote(String aenote) {
    this.aenote = aenote;
  }
  
  /**
   * @return the dultagg
   */
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  public Date getDultagg() {
    return dultagg;
  }
  
  /**
   * @param dultagg the dultagg to set
   */
  public void setDultagg(Date dultagg) {
    this.dultagg = dultagg;
  }
  
  /**
   * @return the modrea
   */
  public String getModrea() {
    return modrea;
  }
  
  /**
   * @param modrea the modrea to set
   */
  public void setModrea(String modrea) {
    this.modrea = modrea;
  }
  
  /**
   * @return the ricmano
   */
  public String getRicmano() {
    return ricmano;
  }
  
  /**
   * @param ricmano the ricmano to set
   */
  public void setRicmano(String ricmano) {
    this.ricmano = ricmano;
  }
  
  /**
   * @return the modmano
   */
  public Integer getModmano() {
    return modmano;
  }
  
  /**
   * @param modmano the modmano to set
   */
  public void setModmano(Integer modmano) {
    this.modmano = modmano;
  }
  
  /**
   * @return the settore
   */
  public String getSettore() {
    return settore;
  }
  
  /**
   * @param settore the settore to set
   */
  public void setSettore(String settore) {
    this.settore = settore;
  }
  
  /**
   * @return the inversa
   */
  public String getInversa() {
    return inversa;
  }
  
  /**
   * @param inversa the inversa to set
   */
  public void setInversa(String inversa) {
    this.inversa = inversa;
  }
  
  /**
   * @return the sortinv
   */
  public String getSortinv() {
    return sortinv;
  }
  
  /**
   * @param sortinv the sortinv to set
   */
  public void setSortinv(String sortinv) {
    this.sortinv = sortinv;
  }
  
  /**
   * @return the garpriv
   */
  public String getGarpriv() {
    return garpriv;
  }
  
  /**
   * @param garpriv the garpriv to set
   */
  public void setGarpriv(String garpriv) {
    this.garpriv = garpriv;
  }
  
  /**
   * @return the prerib
   */
  public Integer getPrerib() {
    return prerib;
  }
  
  /**
   * @param prerib the prerib to set
   */
  public void setPrerib(Integer prerib) {
    this.prerib = prerib;
  }
  
  /**
   * @return the isgreen
   */
  public String getIsgreen() {
    return isgreen;
  }
  
  /**
   * @param isgreen the isgreen to set
   */
  public void setIsgreen(String isgreen) {
    this.isgreen = isgreen;
  }
  
  /**
   * @return the desgreen
   */
  public String getDesgreen() {
    return desgreen;
  }
  
  /**
   * @param desgreen the desgreen to set
   */
  public void setDesgreen(String desgreen) {
    this.desgreen = desgreen;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((accappub == null) ? 0 : accappub.hashCode());
    result = prime * result + ((accqua == null) ? 0 : accqua.hashCode());
    result = prime * result + ((accser == null) ? 0 : accser.hashCode());
    result = prime * result + ((acqalt == null) ? 0 : acqalt.hashCode());
    result = prime * result + ((aeimpmax == null) ? 0 : aeimpmax.hashCode());
    result = prime * result + ((aeimpmin == null) ? 0 : aeimpmin.hashCode());
    result = prime * result + ((aemodvis == null) ? 0 : aemodvis.hashCode());
    result = prime * result + ((aenote == null) ? 0 : aenote.hashCode());
    result = prime * result + ((aeribmax == null) ? 0 : aeribmax.hashCode());
    result = prime * result + ((aeribmin == null) ? 0 : aeribmin.hashCode());
    result = prime * result + ((altneg == null) ? 0 : altneg.hashCode());
    result = prime * result + ((altrisog == null) ? 0 : altrisog.hashCode());
    result = prime * result + ((ammopz == null) ? 0 : ammopz.hashCode());
    result = prime * result + ((ammrin == null) ? 0 : ammrin.hashCode());
    result = prime * result + ((ammvar == null) ? 0 : ammvar.hashCode());
    result = prime * result + ((annorif == null) ? 0 : annorif.hashCode());
    result = prime * result + ((apfinfc == null) ? 0 : apfinfc.hashCode());
    result = prime * result + ((applegreg == null) ? 0 : applegreg.hashCode());
    result = prime * result + ((aqdurata == null) ? 0 : aqdurata.hashCode());
    result = prime * result + ((aqnumope == null) ? 0 : aqnumope.hashCode());
    result = prime * result + ((aqoper == null) ? 0 : aqoper.hashCode());
    result = prime * result + ((aqtempo == null) ? 0 : aqtempo.hashCode());
    result = prime * result + ((banweb == null) ? 0 : banweb.hashCode());
    result = prime * result + ((calcsoan == null) ? 0 : calcsoan.hashCode());
    result = prime * result + ((cenint == null) ? 0 : cenint.hashCode());
    result = prime * result + ((cliv1 == null) ? 0 : cliv1.hashCode());
    result = prime * result + ((cliv2 == null) ? 0 : cliv2.hashCode());
    result = prime * result + ((codavscp == null) ? 0 : codavscp.hashCode());
    result = prime * result + ((codcigaq == null) ? 0 : codcigaq.hashCode());
    result = prime * result + ((codgar == null) ? 0 : codgar.hashCode());
    result = prime * result + ((codgarcli == null) ? 0 : codgarcli.hashCode());
    result = prime * result + ((codnuts == null) ? 0 : codnuts.hashCode());
    result = prime * result + ((codrup == null) ? 0 : codrup.hashCode());
    result = prime * result + ((codscp == null) ? 0 : codscp.hashCode());
    result = prime * result + ((compreq == null) ? 0 : compreq.hashCode());
    result = prime * result + ((contoeco == null) ? 0 : contoeco.hashCode());
    result = prime * result + ((corgar == null) ? 0 : corgar.hashCode());
    result = prime * result + ((critlic == null) ? 0 : critlic.hashCode());
    result = prime * result + ((datdoc == null) ? 0 : datdoc.hashCode());
    result = prime * result + ((datneg == null) ? 0 : datneg.hashCode());
    result = prime * result + ((datrict == null) ? 0 : datrict.hashCode());
    result = prime * result + ((dattot == null) ? 0 : dattot.hashCode());
    result = prime * result + ((davvig == null) ? 0 : davvig.hashCode());
    result = prime * result + ((desdoc == null) ? 0 : desdoc.hashCode());
    result = prime * result + ((desgreen == null) ? 0 : desgreen.hashCode());
    result = prime * result + ((desoff == null) ? 0 : desoff.hashCode());
    result = prime * result + ((desopz == null) ? 0 : desopz.hashCode());
    result = prime * result + ((desqua == null) ? 0 : desqua.hashCode());
    result = prime * result + ((desrin == null) ? 0 : desrin.hashCode());
    result = prime * result + ((destor == null) ? 0 : destor.hashCode());
    result = prime * result + ((detlic == null) ? 0 : detlic.hashCode());
    result = prime * result + ((dfpuba == null) ? 0 : dfpuba.hashCode());
    result = prime * result + ((dgara == null) ? 0 : dgara.hashCode());
    result = prime * result + ((diband == null) ? 0 : diband.hashCode());
    result = prime * result + ((dindoc == null) ? 0 : dindoc.hashCode());
    result = prime * result + ((dinvit == null) ? 0 : dinvit.hashCode());
    result = prime * result + ((docgar == null) ? 0 : docgar.hashCode());
    result = prime * result + ((docweb == null) ? 0 : docweb.hashCode());
    result = prime * result + ((dpubav == null) ? 0 : dpubav.hashCode());
    result = prime * result + ((dpubavviso == null) ? 0 : dpubavviso.hashCode());
    result = prime * result + ((dridoc == null) ? 0 : dridoc.hashCode());
    result = prime * result + ((dteoff == null) ? 0 : dteoff.hashCode());
    result = prime * result + ((dtepar == null) ? 0 : dtepar.hashCode());
    result = prime * result + ((dtermrichcdp == null) ? 0 : dtermrichcdp.hashCode());
    result = prime * result + ((dtermrichcpo == null) ? 0 : dtermrichcpo.hashCode());
    result = prime * result + ((dtermrispcdp == null) ? 0 : dtermrispcdp.hashCode());
    result = prime * result + ((dtermrispcpo == null) ? 0 : dtermrispcpo.hashCode());
    result = prime * result + ((dtpubavviso == null) ? 0 : dtpubavviso.hashCode());
    result = prime * result + ((dultagg == null) ? 0 : dultagg.hashCode());
    result = prime * result + ((elepar == null) ? 0 : elepar.hashCode());
    result = prime * result + ((esineg == null) ? 0 : esineg.hashCode());
    result = prime * result + ((garpriv == null) ? 0 : garpriv.hashCode());
    result = prime * result + ((gartel == null) ? 0 : gartel.hashCode());
    result = prime * result + ((idcommalbo == null) ? 0 : idcommalbo.hashCode());
    result = prime * result + ((impdoc == null) ? 0 : impdoc.hashCode());
    result = prime * result + ((imptor == null) ? 0 : imptor.hashCode());
    result = prime * result + ((infcom == null) ? 0 : infcom.hashCode());
    result = prime * result + ((inversa == null) ? 0 : inversa.hashCode());
    result = prime * result + ((isadesione == null) ? 0 : isadesione.hashCode());
    result = prime * result + ((isarchi == null) ? 0 : isarchi.hashCode());
    result = prime * result + ((isgreen == null) ? 0 : isgreen.hashCode());
    result = prime * result + ((istaut == null) ? 0 : istaut.hashCode());
    result = prime * result + ((iterga == null) ? 0 : iterga.hashCode());
    result = prime * result + ((livacq == null) ? 0 : livacq.hashCode());
    result = prime * result + ((locgar == null) ? 0 : locgar.hashCode());
    result = prime * result + ((locoff == null) ? 0 : locoff.hashCode());
    result = prime * result + ((locpre == null) ? 0 : locpre.hashCode());
    result = prime * result + ((maxope == null) ? 0 : maxope.hashCode());
    result = prime * result + ((minope == null) ? 0 : minope.hashCode());
    result = prime * result + ((modast == null) ? 0 : modast.hashCode());
    result = prime * result + ((modcont == null) ? 0 : modcont.hashCode());
    result = prime * result + ((modfin == null) ? 0 : modfin.hashCode());
    result = prime * result + ((modgar == null) ? 0 : modgar.hashCode());
    result = prime * result + ((modlic == null) ? 0 : modlic.hashCode());
    result = prime * result + ((modmano == null) ? 0 : modmano.hashCode());
    result = prime * result + ((modrea == null) ? 0 : modrea.hashCode());
    result = prime * result + ((motacc == null) ? 0 : motacc.hashCode());
    result = prime * result + ((nattot == null) ? 0 : nattot.hashCode());
    result = prime * result + ((navvig == null) ? 0 : navvig.hashCode());
    result = prime * result + ((ngadit == null) ? 0 : ngadit.hashCode());
    result = prime * result + ((ngaraaq == null) ? 0 : ngaraaq.hashCode());
    result = prime * result + ((nobustamm == null) ? 0 : nobustamm.hashCode());
    result = prime * result + ((nofdit == null) ? 0 : nofdit.hashCode());
    result = prime * result + ((norma == null) ? 0 : norma.hashCode());
    result = prime * result + ((norma1 == null) ? 0 : norma1.hashCode());
    result = prime * result + ((noteat == null) ? 0 : noteat.hashCode());
    result = prime * result + ((npnominacomm == null) ? 0 : npnominacomm.hashCode());
    result = prime * result + ((nproat == null) ? 0 : nproat.hashCode());
    result = prime * result + ((nproti == null) ? 0 : nproti.hashCode());
    result = prime * result + ((nridoc == null) ? 0 : nridoc.hashCode());
    result = prime * result + ((numavcp == null) ? 0 : numavcp.hashCode());
    result = prime * result + ((numope == null) ? 0 : numope.hashCode());
    result = prime * result + ((oesoff == null) ? 0 : oesoff.hashCode());
    result = prime * result + ((offaum == null) ? 0 : offaum.hashCode());
    result = prime * result + ((offlot == null) ? 0 : offlot.hashCode());
    result = prime * result + ((offtel == null) ? 0 : offtel.hashCode());
    result = prime * result + ((ogara == null) ? 0 : ogara.hashCode());
    result = prime * result + ((oggcont == null) ? 0 : oggcont.hashCode());
    result = prime * result + ((oradoc == null) ? 0 : oradoc.hashCode());
    result = prime * result + ((oteoff == null) ? 0 : oteoff.hashCode());
    result = prime * result + ((otepar == null) ? 0 : otepar.hashCode());
    result = prime * result + ((pagdoc == null) ? 0 : pagdoc.hashCode());
    result = prime * result + ((pcodoc == null) ? 0 : pcodoc.hashCode());
    result = prime * result + ((pcogar == null) ? 0 : pcogar.hashCode());
    result = prime * result + ((pcooff == null) ? 0 : pcooff.hashCode());
    result = prime * result + ((pcopre == null) ? 0 : pcopre.hashCode());
    result = prime * result + ((preinf == null) ? 0 : preinf.hashCode());
    result = prime * result + ((prerib == null) ? 0 : prerib.hashCode());
    result = prime * result + ((prerispp == null) ? 0 : prerispp.hashCode());
    result = prime * result + ((profas == null) ? 0 : profas.hashCode());
    result = prime * result + ((profiloweb == null) ? 0 : profiloweb.hashCode());
    result = prime * result + ((progeu == null) ? 0 : progeu.hashCode());
    result = prime * result + ((prourg == null) ? 0 : prourg.hashCode());
    result = prime * result + ((pubprecd == null) ? 0 : pubprecd.hashCode());
    result = prime * result + ((pubprecsa == null) ? 0 : pubprecsa.hashCode());
    result = prime * result + ((ricastae == null) ? 0 : ricastae.hashCode());
    result = prime * result + ((ricmano == null) ? 0 : ricmano.hashCode());
    result = prime * result + ((selope == null) ? 0 : selope.hashCode());
    result = prime * result + ((selpar == null) ? 0 : selpar.hashCode());
    result = prime * result + ((settore == null) ? 0 : settore.hashCode());
    result = prime * result + ((sommaur == null) ? 0 : sommaur.hashCode());
    result = prime * result + ((sortinv == null) ? 0 : sortinv.hashCode());
    result = prime * result + ((tattoc == null) ? 0 : tattoc.hashCode());
    result = prime * result + ((tattot == null) ? 0 : tattot.hashCode());
    result = prime * result + ((terrid == null) ? 0 : terrid.hashCode());
    result = prime * result + ((tipforn == null) ? 0 : tipforn.hashCode());
    result = prime * result + ((tipgar == null) ? 0 : tipgar.hashCode());
    result = prime * result + ((tipgen == null) ? 0 : tipgen.hashCode());
    result = prime * result + ((tiplav == null) ? 0 : tiplav.hashCode());
    result = prime * result + ((tipneg == null) ? 0 : tipneg.hashCode());
    result = prime * result + ((tiptor == null) ? 0 : tiptor.hashCode());
    result = prime * result + ((tus == null) ? 0 : tus.hashCode());
    result = prime * result + ((uffdet == null) ? 0 : uffdet.hashCode());
    result = prime * result + ((ultdetlic == null) ? 0 : ultdetlic.hashCode());
    result = prime * result + ((urbasco == null) ? 0 : urbasco.hashCode());
    result = prime * result + ((urega == null) ? 0 : urega.hashCode());
    result = prime * result + ((urlavscp == null) ? 0 : urlavscp.hashCode());
    result = prime * result + ((urlscp == null) ? 0 : urlscp.hashCode());
    result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
    result = prime * result + ((valoff == null) ? 0 : valoff.hashCode());
    result = prime * result + ((valtec == null) ? 0 : valtec.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Torn other = (Torn) obj;
    if (accappub == null) {
      if (other.accappub != null) return false;
    } else if (!accappub.equals(other.accappub)) return false;
    if (accqua == null) {
      if (other.accqua != null) return false;
    } else if (!accqua.equals(other.accqua)) return false;
    if (accser == null) {
      if (other.accser != null) return false;
    } else if (!accser.equals(other.accser)) return false;
    if (acqalt == null) {
      if (other.acqalt != null) return false;
    } else if (!acqalt.equals(other.acqalt)) return false;
    if (aeimpmax == null) {
      if (other.aeimpmax != null) return false;
    } else if (!aeimpmax.equals(other.aeimpmax)) return false;
    if (aeimpmin == null) {
      if (other.aeimpmin != null) return false;
    } else if (!aeimpmin.equals(other.aeimpmin)) return false;
    if (aemodvis == null) {
      if (other.aemodvis != null) return false;
    } else if (!aemodvis.equals(other.aemodvis)) return false;
    if (aenote == null) {
      if (other.aenote != null) return false;
    } else if (!aenote.equals(other.aenote)) return false;
    if (aeribmax == null) {
      if (other.aeribmax != null) return false;
    } else if (!aeribmax.equals(other.aeribmax)) return false;
    if (aeribmin == null) {
      if (other.aeribmin != null) return false;
    } else if (!aeribmin.equals(other.aeribmin)) return false;
    if (altneg == null) {
      if (other.altneg != null) return false;
    } else if (!altneg.equals(other.altneg)) return false;
    if (altrisog == null) {
      if (other.altrisog != null) return false;
    } else if (!altrisog.equals(other.altrisog)) return false;
    if (ammopz == null) {
      if (other.ammopz != null) return false;
    } else if (!ammopz.equals(other.ammopz)) return false;
    if (ammrin == null) {
      if (other.ammrin != null) return false;
    } else if (!ammrin.equals(other.ammrin)) return false;
    if (ammvar == null) {
      if (other.ammvar != null) return false;
    } else if (!ammvar.equals(other.ammvar)) return false;
    if (annorif == null) {
      if (other.annorif != null) return false;
    } else if (!annorif.equals(other.annorif)) return false;
    if (apfinfc == null) {
      if (other.apfinfc != null) return false;
    } else if (!apfinfc.equals(other.apfinfc)) return false;
    if (applegreg == null) {
      if (other.applegreg != null) return false;
    } else if (!applegreg.equals(other.applegreg)) return false;
    if (aqdurata == null) {
      if (other.aqdurata != null) return false;
    } else if (!aqdurata.equals(other.aqdurata)) return false;
    if (aqnumope == null) {
      if (other.aqnumope != null) return false;
    } else if (!aqnumope.equals(other.aqnumope)) return false;
    if (aqoper == null) {
      if (other.aqoper != null) return false;
    } else if (!aqoper.equals(other.aqoper)) return false;
    if (aqtempo == null) {
      if (other.aqtempo != null) return false;
    } else if (!aqtempo.equals(other.aqtempo)) return false;
    if (banweb == null) {
      if (other.banweb != null) return false;
    } else if (!banweb.equals(other.banweb)) return false;
    if (calcsoan == null) {
      if (other.calcsoan != null) return false;
    } else if (!calcsoan.equals(other.calcsoan)) return false;
    if (cenint == null) {
      if (other.cenint != null) return false;
    } else if (!cenint.equals(other.cenint)) return false;
    if (cliv1 == null) {
      if (other.cliv1 != null) return false;
    } else if (!cliv1.equals(other.cliv1)) return false;
    if (cliv2 == null) {
      if (other.cliv2 != null) return false;
    } else if (!cliv2.equals(other.cliv2)) return false;
    if (codavscp == null) {
      if (other.codavscp != null) return false;
    } else if (!codavscp.equals(other.codavscp)) return false;
    if (codcigaq == null) {
      if (other.codcigaq != null) return false;
    } else if (!codcigaq.equals(other.codcigaq)) return false;
    if (codgar == null) {
      if (other.codgar != null) return false;
    } else if (!codgar.equals(other.codgar)) return false;
    if (codgarcli == null) {
      if (other.codgarcli != null) return false;
    } else if (!codgarcli.equals(other.codgarcli)) return false;
    if (codnuts == null) {
      if (other.codnuts != null) return false;
    } else if (!codnuts.equals(other.codnuts)) return false;
    if (codrup == null) {
      if (other.codrup != null) return false;
    } else if (!codrup.equals(other.codrup)) return false;
    if (codscp == null) {
      if (other.codscp != null) return false;
    } else if (!codscp.equals(other.codscp)) return false;
    if (compreq == null) {
      if (other.compreq != null) return false;
    } else if (!compreq.equals(other.compreq)) return false;
    if (contoeco == null) {
      if (other.contoeco != null) return false;
    } else if (!contoeco.equals(other.contoeco)) return false;
    if (corgar == null) {
      if (other.corgar != null) return false;
    } else if (!corgar.equals(other.corgar)) return false;
    if (critlic == null) {
      if (other.critlic != null) return false;
    } else if (!critlic.equals(other.critlic)) return false;
    if (datdoc == null) {
      if (other.datdoc != null) return false;
    } else if (!datdoc.equals(other.datdoc)) return false;
    if (datneg == null) {
      if (other.datneg != null) return false;
    } else if (!datneg.equals(other.datneg)) return false;
    if (datrict == null) {
      if (other.datrict != null) return false;
    } else if (!datrict.equals(other.datrict)) return false;
    if (dattot == null) {
      if (other.dattot != null) return false;
    } else if (!dattot.equals(other.dattot)) return false;
    if (davvig == null) {
      if (other.davvig != null) return false;
    } else if (!davvig.equals(other.davvig)) return false;
    if (desdoc == null) {
      if (other.desdoc != null) return false;
    } else if (!desdoc.equals(other.desdoc)) return false;
    if (desgreen == null) {
      if (other.desgreen != null) return false;
    } else if (!desgreen.equals(other.desgreen)) return false;
    if (desoff == null) {
      if (other.desoff != null) return false;
    } else if (!desoff.equals(other.desoff)) return false;
    if (desopz == null) {
      if (other.desopz != null) return false;
    } else if (!desopz.equals(other.desopz)) return false;
    if (desqua == null) {
      if (other.desqua != null) return false;
    } else if (!desqua.equals(other.desqua)) return false;
    if (desrin == null) {
      if (other.desrin != null) return false;
    } else if (!desrin.equals(other.desrin)) return false;
    if (destor == null) {
      if (other.destor != null) return false;
    } else if (!destor.equals(other.destor)) return false;
    if (detlic == null) {
      if (other.detlic != null) return false;
    } else if (!detlic.equals(other.detlic)) return false;
    if (dfpuba == null) {
      if (other.dfpuba != null) return false;
    } else if (!dfpuba.equals(other.dfpuba)) return false;
    if (dgara == null) {
      if (other.dgara != null) return false;
    } else if (!dgara.equals(other.dgara)) return false;
    if (diband == null) {
      if (other.diband != null) return false;
    } else if (!diband.equals(other.diband)) return false;
    if (dindoc == null) {
      if (other.dindoc != null) return false;
    } else if (!dindoc.equals(other.dindoc)) return false;
    if (dinvit == null) {
      if (other.dinvit != null) return false;
    } else if (!dinvit.equals(other.dinvit)) return false;
    if (docgar == null) {
      if (other.docgar != null) return false;
    } else if (!docgar.equals(other.docgar)) return false;
    if (docweb == null) {
      if (other.docweb != null) return false;
    } else if (!docweb.equals(other.docweb)) return false;
    if (dpubav == null) {
      if (other.dpubav != null) return false;
    } else if (!dpubav.equals(other.dpubav)) return false;
    if (dpubavviso == null) {
      if (other.dpubavviso != null) return false;
    } else if (!dpubavviso.equals(other.dpubavviso)) return false;
    if (dridoc == null) {
      if (other.dridoc != null) return false;
    } else if (!dridoc.equals(other.dridoc)) return false;
    if (dteoff == null) {
      if (other.dteoff != null) return false;
    } else if (!dteoff.equals(other.dteoff)) return false;
    if (dtepar == null) {
      if (other.dtepar != null) return false;
    } else if (!dtepar.equals(other.dtepar)) return false;
    if (dtermrichcdp == null) {
      if (other.dtermrichcdp != null) return false;
    } else if (!dtermrichcdp.equals(other.dtermrichcdp)) return false;
    if (dtermrichcpo == null) {
      if (other.dtermrichcpo != null) return false;
    } else if (!dtermrichcpo.equals(other.dtermrichcpo)) return false;
    if (dtermrispcdp == null) {
      if (other.dtermrispcdp != null) return false;
    } else if (!dtermrispcdp.equals(other.dtermrispcdp)) return false;
    if (dtermrispcpo == null) {
      if (other.dtermrispcpo != null) return false;
    } else if (!dtermrispcpo.equals(other.dtermrispcpo)) return false;
    if (dtpubavviso == null) {
      if (other.dtpubavviso != null) return false;
    } else if (!dtpubavviso.equals(other.dtpubavviso)) return false;
    if (dultagg == null) {
      if (other.dultagg != null) return false;
    } else if (!dultagg.equals(other.dultagg)) return false;
    if (elepar == null) {
      if (other.elepar != null) return false;
    } else if (!elepar.equals(other.elepar)) return false;
    if (esineg == null) {
      if (other.esineg != null) return false;
    } else if (!esineg.equals(other.esineg)) return false;
    if (garpriv == null) {
      if (other.garpriv != null) return false;
    } else if (!garpriv.equals(other.garpriv)) return false;
    if (gartel == null) {
      if (other.gartel != null) return false;
    } else if (!gartel.equals(other.gartel)) return false;
    if (idcommalbo == null) {
      if (other.idcommalbo != null) return false;
    } else if (!idcommalbo.equals(other.idcommalbo)) return false;
    if (impdoc == null) {
      if (other.impdoc != null) return false;
    } else if (!impdoc.equals(other.impdoc)) return false;
    if (imptor == null) {
      if (other.imptor != null) return false;
    } else if (!imptor.equals(other.imptor)) return false;
    if (infcom == null) {
      if (other.infcom != null) return false;
    } else if (!infcom.equals(other.infcom)) return false;
    if (inversa == null) {
      if (other.inversa != null) return false;
    } else if (!inversa.equals(other.inversa)) return false;
    if (isadesione == null) {
      if (other.isadesione != null) return false;
    } else if (!isadesione.equals(other.isadesione)) return false;
    if (isarchi == null) {
      if (other.isarchi != null) return false;
    } else if (!isarchi.equals(other.isarchi)) return false;
    if (isgreen == null) {
      if (other.isgreen != null) return false;
    } else if (!isgreen.equals(other.isgreen)) return false;
    if (istaut == null) {
      if (other.istaut != null) return false;
    } else if (!istaut.equals(other.istaut)) return false;
    if (iterga == null) {
      if (other.iterga != null) return false;
    } else if (!iterga.equals(other.iterga)) return false;
    if (livacq == null) {
      if (other.livacq != null) return false;
    } else if (!livacq.equals(other.livacq)) return false;
    if (locgar == null) {
      if (other.locgar != null) return false;
    } else if (!locgar.equals(other.locgar)) return false;
    if (locoff == null) {
      if (other.locoff != null) return false;
    } else if (!locoff.equals(other.locoff)) return false;
    if (locpre == null) {
      if (other.locpre != null) return false;
    } else if (!locpre.equals(other.locpre)) return false;
    if (maxope == null) {
      if (other.maxope != null) return false;
    } else if (!maxope.equals(other.maxope)) return false;
    if (minope == null) {
      if (other.minope != null) return false;
    } else if (!minope.equals(other.minope)) return false;
    if (modast == null) {
      if (other.modast != null) return false;
    } else if (!modast.equals(other.modast)) return false;
    if (modcont == null) {
      if (other.modcont != null) return false;
    } else if (!modcont.equals(other.modcont)) return false;
    if (modfin == null) {
      if (other.modfin != null) return false;
    } else if (!modfin.equals(other.modfin)) return false;
    if (modgar == null) {
      if (other.modgar != null) return false;
    } else if (!modgar.equals(other.modgar)) return false;
    if (modlic == null) {
      if (other.modlic != null) return false;
    } else if (!modlic.equals(other.modlic)) return false;
    if (modmano == null) {
      if (other.modmano != null) return false;
    } else if (!modmano.equals(other.modmano)) return false;
    if (modrea == null) {
      if (other.modrea != null) return false;
    } else if (!modrea.equals(other.modrea)) return false;
    if (motacc == null) {
      if (other.motacc != null) return false;
    } else if (!motacc.equals(other.motacc)) return false;
    if (nattot == null) {
      if (other.nattot != null) return false;
    } else if (!nattot.equals(other.nattot)) return false;
    if (navvig == null) {
      if (other.navvig != null) return false;
    } else if (!navvig.equals(other.navvig)) return false;
    if (ngadit == null) {
      if (other.ngadit != null) return false;
    } else if (!ngadit.equals(other.ngadit)) return false;
    if (ngaraaq == null) {
      if (other.ngaraaq != null) return false;
    } else if (!ngaraaq.equals(other.ngaraaq)) return false;
    if (nobustamm == null) {
      if (other.nobustamm != null) return false;
    } else if (!nobustamm.equals(other.nobustamm)) return false;
    if (nofdit == null) {
      if (other.nofdit != null) return false;
    } else if (!nofdit.equals(other.nofdit)) return false;
    if (norma == null) {
      if (other.norma != null) return false;
    } else if (!norma.equals(other.norma)) return false;
    if (norma1 == null) {
      if (other.norma1 != null) return false;
    } else if (!norma1.equals(other.norma1)) return false;
    if (noteat == null) {
      if (other.noteat != null) return false;
    } else if (!noteat.equals(other.noteat)) return false;
    if (npnominacomm == null) {
      if (other.npnominacomm != null) return false;
    } else if (!npnominacomm.equals(other.npnominacomm)) return false;
    if (nproat == null) {
      if (other.nproat != null) return false;
    } else if (!nproat.equals(other.nproat)) return false;
    if (nproti == null) {
      if (other.nproti != null) return false;
    } else if (!nproti.equals(other.nproti)) return false;
    if (nridoc == null) {
      if (other.nridoc != null) return false;
    } else if (!nridoc.equals(other.nridoc)) return false;
    if (numavcp == null) {
      if (other.numavcp != null) return false;
    } else if (!numavcp.equals(other.numavcp)) return false;
    if (numope == null) {
      if (other.numope != null) return false;
    } else if (!numope.equals(other.numope)) return false;
    if (oesoff == null) {
      if (other.oesoff != null) return false;
    } else if (!oesoff.equals(other.oesoff)) return false;
    if (offaum == null) {
      if (other.offaum != null) return false;
    } else if (!offaum.equals(other.offaum)) return false;
    if (offlot == null) {
      if (other.offlot != null) return false;
    } else if (!offlot.equals(other.offlot)) return false;
    if (offtel == null) {
      if (other.offtel != null) return false;
    } else if (!offtel.equals(other.offtel)) return false;
    if (ogara == null) {
      if (other.ogara != null) return false;
    } else if (!ogara.equals(other.ogara)) return false;
    if (oggcont == null) {
      if (other.oggcont != null) return false;
    } else if (!oggcont.equals(other.oggcont)) return false;
    if (oradoc == null) {
      if (other.oradoc != null) return false;
    } else if (!oradoc.equals(other.oradoc)) return false;
    if (oteoff == null) {
      if (other.oteoff != null) return false;
    } else if (!oteoff.equals(other.oteoff)) return false;
    if (otepar == null) {
      if (other.otepar != null) return false;
    } else if (!otepar.equals(other.otepar)) return false;
    if (pagdoc == null) {
      if (other.pagdoc != null) return false;
    } else if (!pagdoc.equals(other.pagdoc)) return false;
    if (pcodoc == null) {
      if (other.pcodoc != null) return false;
    } else if (!pcodoc.equals(other.pcodoc)) return false;
    if (pcogar == null) {
      if (other.pcogar != null) return false;
    } else if (!pcogar.equals(other.pcogar)) return false;
    if (pcooff == null) {
      if (other.pcooff != null) return false;
    } else if (!pcooff.equals(other.pcooff)) return false;
    if (pcopre == null) {
      if (other.pcopre != null) return false;
    } else if (!pcopre.equals(other.pcopre)) return false;
    if (preinf == null) {
      if (other.preinf != null) return false;
    } else if (!preinf.equals(other.preinf)) return false;
    if (prerib == null) {
      if (other.prerib != null) return false;
    } else if (!prerib.equals(other.prerib)) return false;
    if (prerispp == null) {
      if (other.prerispp != null) return false;
    } else if (!prerispp.equals(other.prerispp)) return false;
    if (profas == null) {
      if (other.profas != null) return false;
    } else if (!profas.equals(other.profas)) return false;
    if (profiloweb == null) {
      if (other.profiloweb != null) return false;
    } else if (!profiloweb.equals(other.profiloweb)) return false;
    if (progeu == null) {
      if (other.progeu != null) return false;
    } else if (!progeu.equals(other.progeu)) return false;
    if (prourg == null) {
      if (other.prourg != null) return false;
    } else if (!prourg.equals(other.prourg)) return false;
    if (pubprecd == null) {
      if (other.pubprecd != null) return false;
    } else if (!pubprecd.equals(other.pubprecd)) return false;
    if (pubprecsa == null) {
      if (other.pubprecsa != null) return false;
    } else if (!pubprecsa.equals(other.pubprecsa)) return false;
    if (ricastae == null) {
      if (other.ricastae != null) return false;
    } else if (!ricastae.equals(other.ricastae)) return false;
    if (ricmano == null) {
      if (other.ricmano != null) return false;
    } else if (!ricmano.equals(other.ricmano)) return false;
    if (selope == null) {
      if (other.selope != null) return false;
    } else if (!selope.equals(other.selope)) return false;
    if (selpar == null) {
      if (other.selpar != null) return false;
    } else if (!selpar.equals(other.selpar)) return false;
    if (settore == null) {
      if (other.settore != null) return false;
    } else if (!settore.equals(other.settore)) return false;
    if (sommaur == null) {
      if (other.sommaur != null) return false;
    } else if (!sommaur.equals(other.sommaur)) return false;
    if (sortinv == null) {
      if (other.sortinv != null) return false;
    } else if (!sortinv.equals(other.sortinv)) return false;
    if (tattoc == null) {
      if (other.tattoc != null) return false;
    } else if (!tattoc.equals(other.tattoc)) return false;
    if (tattot == null) {
      if (other.tattot != null) return false;
    } else if (!tattot.equals(other.tattot)) return false;
    if (terrid == null) {
      if (other.terrid != null) return false;
    } else if (!terrid.equals(other.terrid)) return false;
    if (tipforn == null) {
      if (other.tipforn != null) return false;
    } else if (!tipforn.equals(other.tipforn)) return false;
    if (tipgar == null) {
      if (other.tipgar != null) return false;
    } else if (!tipgar.equals(other.tipgar)) return false;
    if (tipgen == null) {
      if (other.tipgen != null) return false;
    } else if (!tipgen.equals(other.tipgen)) return false;
    if (tiplav == null) {
      if (other.tiplav != null) return false;
    } else if (!tiplav.equals(other.tiplav)) return false;
    if (tipneg == null) {
      if (other.tipneg != null) return false;
    } else if (!tipneg.equals(other.tipneg)) return false;
    if (tiptor == null) {
      if (other.tiptor != null) return false;
    } else if (!tiptor.equals(other.tiptor)) return false;
    if (tus == null) {
      if (other.tus != null) return false;
    } else if (!tus.equals(other.tus)) return false;
    if (uffdet == null) {
      if (other.uffdet != null) return false;
    } else if (!uffdet.equals(other.uffdet)) return false;
    if (ultdetlic == null) {
      if (other.ultdetlic != null) return false;
    } else if (!ultdetlic.equals(other.ultdetlic)) return false;
    if (urbasco == null) {
      if (other.urbasco != null) return false;
    } else if (!urbasco.equals(other.urbasco)) return false;
    if (urega == null) {
      if (other.urega != null) return false;
    } else if (!urega.equals(other.urega)) return false;
    if (urlavscp == null) {
      if (other.urlavscp != null) return false;
    } else if (!urlavscp.equals(other.urlavscp)) return false;
    if (urlscp == null) {
      if (other.urlscp != null) return false;
    } else if (!urlscp.equals(other.urlscp)) return false;
    if (uuid == null) {
      if (other.uuid != null) return false;
    } else if (!uuid.equals(other.uuid)) return false;
    if (valoff == null) {
      if (other.valoff != null) return false;
    } else if (!valoff.equals(other.valoff)) return false;
    if (valtec == null) {
      if (other.valtec != null) return false;
    } else if (!valtec.equals(other.valtec)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Torn [" + (codgar != null ? "codgar=" + codgar : "") + "]";
  }
  
  
}
