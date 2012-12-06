package de.mpg.mpdl.dlc.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.searchLogic.SearchBean;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion;
import de.mpg.mpdl.dlc.searchLogic.Criterion.Operator;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.VolumeUtilBean;


@ManagedBean
@SessionScoped
@URLMapping(id = "advancedSearch", pattern = "/search", viewId = "/advancedSearch.xhtml")
public class AdvancedSearchBean implements Observer {

	private static Logger logger = Logger.getLogger(AdvancedSearchBean.class);
	
	private List<SearchCriterion> searchCriterionList;	
	
	private List<SearchCriterion> cdcSearchCriterionList;	
	
	private String freeSearch ="";
	
	private SearchCriterion yearFrom = null;
	private SearchCriterion yearTo = null;
	
	private SearchCriterion fulltextSearch= null;
	
	private SearchCriterion cdcSearch= null;

	private ContextServiceBean contextServiceBean = new ContextServiceBean();	
	private OrganizationalUnitServiceBean ouServiceBean =  new OrganizationalUnitServiceBean();	
	private SearchBean searchBean = new SearchBean();
	
	@ManagedProperty("#{advancedSearchResultBean}")
	private AdvancedSearchResultBean advancedSearchResultBean;
	
	//Variables for the Library/Context Search
	private List<SelectItem> allLibItems;
	private List<SelectItem> allConItems;
	//private ContextSearch contextSearchItem;
	private List<ContextSearch> contextScElements = new ArrayList<ContextSearch>();
	
	private List<SelectItem> cdcObjectTypeSelectItems;
	private List<SelectItem> cdcLeafMarkerSelectItems;
	private List<SelectItem> cdcTippedInSelectItems;
	private List<SelectItem> cdcBindingSelectItems;
	private List<SelectItem> cdcBookCoverDecorationSelectItems;
	private List<SelectItem> cdcEndPaperSelectItems;
	private List<SelectItem> cdcMarginaliaSelectItems;
	private List<SelectItem> cdcEdgeSelectItems;
	
	@ManagedProperty("#{internationalizationHelper}")
	private InternationalizationHelper internationalizationHelper;

	

	

	
	
	@PostConstruct
	public void postConstruct()
	{
		internationalizationHelper.addObserver(this);
	}
	
	@PreDestroy
	public void preDestroy()
	{
		internationalizationHelper.deleteObserver(this);
	}
	
	/**
	 * Update menu lists when changing language
	 */
	@Override
	public void update(Observable o, Object arg) {
		initOUList();
		for(ContextSearch cs : contextScElements)
		{
			refreshContextList(cs);
		}
		
	}
	
	public AdvancedSearchBean()
	{
		this.searchCriterionList = new ArrayList<SearchCriterion>();
		this.searchCriterionList.add(new SearchCriterion(SearchType.FREE, ""));
		this.searchCriterionList.add(new SearchCriterion(SearchType.AUTHOR, ""));
		this.searchCriterionList.add(new SearchCriterion(SearchType.TITLE, ""));
		
		this.yearFrom = new SearchCriterion(SearchType.YEAR, "");
		this.yearTo = new SearchCriterion(SearchType.YEAR, "");
		
		this.fulltextSearch = new SearchCriterion(SearchType.FULLTEXT, "");
		
		this.cdcSearch = new SearchCriterion(SearchType.CODICOLOGICAL, "");
		//this.contextSearchItem = new ContextSearch();
		this.cdcSearchCriterionList = new ArrayList<SearchCriterion>();
		this.cdcSearchCriterionList.add(new SearchCriterion(SearchType.CDC_OBJECT_TYPE, ""));
		
		this.init();
	}
	
	
	public void initOUList()
	{
		List<SelectItem> ouSelectItems = new ArrayList<SelectItem>();
		ouSelectItems.add(new SelectItem("",InternationalizationHelper.getLabel("sc_allLib")));
		for(OrganizationalUnit ou : ouServiceBean.retrieveOUs())
		{
			//Only add an ou if it has a context otherwise there is no collection which we can search in
			if (contextServiceBean.retrieveOUContexts(ou.getObjid(), true).size() > 0)
			{
				ouSelectItems.add(new SelectItem(ou.getObjid(),ou.getProperties().getName()));
			}
		}
		this.setAllLibItems(ouSelectItems);
	}
	
	
	
	
	public void init()
	{
		initCdcSelectItems();
		//Set the libraries list
		initOUList();
		
		//Set the contexts list
		List<SelectItem> contextSelectItems = new ArrayList<SelectItem>();
		contextSelectItems.add(new SelectItem("",InternationalizationHelper.getLabel("sc_allCon")));
		for(Context c : contextServiceBean.retrieveAllcontexts())
		{
			contextSelectItems.add(new SelectItem(c.getObjid(),c.getProperties().getName()));
		}
		this.setAllConItems(contextSelectItems);
		
		ContextSearch cs = new ContextSearch();
		refreshContextList(cs);
		this.contextScElements.add(cs);
	}
	
	public void refreshContextList(ContextSearch cs)
	
	{				
				
		//Prepare context list (depends on library selection)
		String ouId = cs.getOuId();
		List<SelectItem> contextSelectItems= new ArrayList<SelectItem>();
		contextSelectItems.add(new SelectItem("",InternationalizationHelper.getLabel("sc_allCon")));			
		if (ouId != null && !ouId.equals(""))
		{
			for(Context c : contextServiceBean.retrieveOUContexts(ouId, true))
			{
				contextSelectItems.add(new SelectItem(c.getObjid(),c.getProperties().getName()));
			}
		}
		else
		{
			for(Context c : contextServiceBean.retrieveAllcontexts())
			{
				contextSelectItems.add(new SelectItem(c.getObjid(),c.getProperties().getName()));
			}
		}
		
		cs.setContextList(contextSelectItems);
		cs.setOuId(ouId);
	}
	
	private void initCdcSelectItems()
	{
		this.cdcObjectTypeSelectItems = new ArrayList<SelectItem>();
		//List<SelectItem> objectTypeSelectItems = new ArrayList<SelectItem>();
		cdcObjectTypeSelectItems.add(new SelectItem("", "Alle"));
		cdcObjectTypeSelectItems.add(new SelectItem("gedrucktes Buch", "gedrucktes Buch"));
		cdcObjectTypeSelectItems.add(new SelectItem("Handschrift", "Handschrift"));
		cdcObjectTypeSelectItems.add(new SelectItem("handschriftliches Dokument", "handschriftliches Dokument"));
		cdcObjectTypeSelectItems.add(new SelectItem("maschinenschriftliches Manuskript", "maschinenschriftliches Manuskript"));
		cdcObjectTypeSelectItems.add(new SelectItem("Tafelband gebunden", "Tafelband gebunden"));
		cdcObjectTypeSelectItems.add(new SelectItem("Tafelband ungebunden", "Tafelband ungebunden"));
		cdcObjectTypeSelectItems.add(new SelectItem("Zeitschrift", "Zeitschrift"));
		cdcObjectTypeSelectItems.add(new SelectItem("Druckgraphik Einzelblatt", "Druckgraphik Einzelblatt"));
		cdcObjectTypeSelectItems.add(new SelectItem("Photographie", "Photographie"));
		cdcObjectTypeSelectItems.add(new SelectItem("Zeitungsartikel", "Zeitungsartikel"));
		cdcObjectTypeSelectItems.add(new SelectItem("Photokopie", "Photokopie"));
		cdcObjectTypeSelectItems.add(new SelectItem("Einzelblatt handschriftlich", "Einzelblatt handschriftlich"));
		cdcObjectTypeSelectItems.add(new SelectItem("Einzelblatt gedruckt", "Einzelblatt gedruckt"));
		cdcObjectTypeSelectItems.add(new SelectItem("Zeichnung", "Zeichnung"));
		cdcObjectTypeSelectItems.add(new SelectItem("Malerei", "Malerei"));
		
		//SelectItemGroup objectTypeGroup = new SelectItemGroup(InternationalizationHelper.getLabel("cdc_objectType"));
		//objectTypeGroup.setSelectItems(cdcObjectTypeSelectItems.toArray(new SelectItem[]{}));
		
		//cdcSelectItems.add(objectTypeGroup);
		
		
		
		this.cdcTippedInSelectItems = new ArrayList<SelectItem>();
		//List<SelectItem> tippedInSelectItems = new ArrayList<SelectItem>();
		cdcTippedInSelectItems.add(new SelectItem("Brief", "Brief"));
		cdcTippedInSelectItems.add(new SelectItem("Foto", "Foto"));
		cdcTippedInSelectItems.add(new SelectItem("Devotionalie", "Devotionalie"));
		cdcTippedInSelectItems.add(new SelectItem("Notizblatt", "Notizblatt"));
		SelectItemGroup tippedInGroup = new SelectItemGroup(InternationalizationHelper.getLabel("cdc_tippedIn"));
		tippedInGroup.setSelectItems(cdcTippedInSelectItems.toArray(new SelectItem[]{}));
		//cdcSelectItems.add(tippedInGroup);
	
		
		this.cdcEndPaperSelectItems = new ArrayList<SelectItem>();
		cdcEndPaperSelectItems.add(new SelectItem("Band ohne Vorsatzblätter", "Band ohne Vorsatzblätter"));
		cdcEndPaperSelectItems.add(new SelectItem("Vorsatzblatt mit Wasserzeichen", "Vorsatzblatt mit Wasserzeichen"));
		cdcEndPaperSelectItems.add(new SelectItem("Vorsatzblätter bei Neubindung", "Vorsatzblätter bei Neubindung"));
		cdcEndPaperSelectItems.add(new SelectItem("Vorsatzblätter bei Neubindung", "Vorsatzblätter bei Neubindung"));
		cdcEndPaperSelectItems.add(new SelectItem("alte Bindung ohne Vorsatzblätter", "alte Bindung ohne Vorsatzblätter"));
		cdcEndPaperSelectItems.add(new SelectItem("Originalbindung ohne Vorsatzblätter", "Originalbindung ohne Vorsatzblätter"));
		cdcEndPaperSelectItems.add(new SelectItem("Vorsatz als Umschlagpapier", "Vorsatz als Umschlagpapier"));
		cdcEndPaperSelectItems.add(new SelectItem("alte Bindung mit neuem Vorsatz", "alte Bindung mit neuem Vorsatz"));
		cdcEndPaperSelectItems.add(new SelectItem("altes Vorsatzblatt mit neuem Vorsatzblatt verbunden", "altes Vorsatzblatt mit neuem Vorsatzblatt verbunden"));
		cdcEndPaperSelectItems.add(new SelectItem("Vorsatzblatt entfernt", "Vorsatzblatt entfernt"));
		cdcEndPaperSelectItems.add(new SelectItem("vorderes Vorsatzblatt handschriftlich beschrieben", "vorderes Vorsatzblatt handschriftlich beschrieben"));
		cdcEndPaperSelectItems.add(new SelectItem("Vorsatzblatt mit Widmungseintrag", "Vorsatzblatt mit Widmungseintrag"));
		cdcEndPaperSelectItems.add(new SelectItem("Vorsatzblatt mit Stiftungsnotiz", "Vorsatzblatt mit Stiftungsnotiz"));
		cdcEndPaperSelectItems.add(new SelectItem("Vorsatzblatt mit Besitzeintrag", "Vorsatzblatt mit Besitzeintrag"));
		cdcEndPaperSelectItems.add(new SelectItem("alte Vorsatzblätter zum Spiegel verklebt", "alte Vorsatzblätter zum Spiegel verklebt"));
		cdcEndPaperSelectItems.add(new SelectItem("neue Vorsatzblätter bei Restaurierung", "neue Vorsatzblätter bei Restaurierung"));
		cdcEndPaperSelectItems.add(new SelectItem("Vorsatz aus Buntpapier", "Vorsatz aus Buntpapier"));
		cdcEndPaperSelectItems.add(new SelectItem("Vorsatz aus Marmorpapier", "Vorsatz aus Marmorpapier"));
		cdcEndPaperSelectItems.add(new SelectItem("vorderes Vorsatzblatt mit alter Signatur", "vorderes Vorsatzblatt mit alter Signatur"));
		cdcEndPaperSelectItems.add(new SelectItem("Vorsatzblatt mit Handzeichnung", "Vorsatzblatt mit Handzeichnung"));
		cdcEndPaperSelectItems.add(new SelectItem("Vorsatz aus Goldbrokatpapier", "Vorsatz aus Goldbrokatpapier"));
		cdcEndPaperSelectItems.add(new SelectItem("Vorsatz aus gefärbtem Papier", "Vorsatz aus gefärbtem Papier"));
		cdcEndPaperSelectItems.add(new SelectItem("altes Vorsatzblatt mit Interimsumschlag verklebt", "altes Vorsatzblatt mit Interimsumschlag verklebt"));
		cdcEndPaperSelectItems.add(new SelectItem("Vorsatzblatt mit Signatur", "Vorsatzblatt mit Signatur"));
		cdcEndPaperSelectItems.add(new SelectItem("Neubindung ohne Vorsatz", "Neubindung ohne Vorsatz"));
		cdcEndPaperSelectItems.add(new SelectItem("Pergament", "Pergament"));
		cdcEndPaperSelectItems.add(new SelectItem("Andere Variante – siehe Details", "Andere Variante – siehe Details"));
		
		
		
		this.cdcEdgeSelectItems = new ArrayList<SelectItem>();
		cdcEdgeSelectItems.add(new SelectItem("Goldschnitt", "Goldschnitt"));
		cdcEdgeSelectItems.add(new SelectItem("Farbschnitt", "Farbschnitt"));
		cdcEdgeSelectItems.add(new SelectItem("Schnitt bemalt", "Schnitt bemalt"));
		cdcEdgeSelectItems.add(new SelectItem("Schnitt mit Titel beschriftet", "Schnitt mit Titel beschriftet"));
		cdcEdgeSelectItems.add(new SelectItem("ohne Schnittverzierung", "ohne Schnittverzierung"));
		cdcEdgeSelectItems.add(new SelectItem("Sprenkelschnitt", "Sprenkelschnitt"));
		cdcEdgeSelectItems.add(new SelectItem("Marmorschnitt", "Marmorschnitt"));
		cdcEdgeSelectItems.add(new SelectItem("Schnitt eingeritzt", "Schnitt eingeritzt"));
		cdcEdgeSelectItems.add(new SelectItem("Tupfschnitt", "Tupfschnitt"));
		cdcEdgeSelectItems.add(new SelectItem("Schnittpunzierung", "Schnittpunzierung"));
		cdcEdgeSelectItems.add(new SelectItem("Abziehschnitt", "Abziehschnitt"));
		cdcEdgeSelectItems.add(new SelectItem("Kreideschnitt", "Kreideschnitt"));
		cdcEdgeSelectItems.add(new SelectItem("verschobener Schnitt", "verschobener Schnitt"));
		cdcEdgeSelectItems.add(new SelectItem("Silberschnitt", "Silberschnitt"));
		cdcEdgeSelectItems.add(new SelectItem("Goldschnitt punziert", "Goldschnitt punziert"));
		cdcEdgeSelectItems.add(new SelectItem("bei Neubindung beschnitten", "bei Neubindung beschnitten"));
		cdcEdgeSelectItems.add(new SelectItem("Blätter unbeschnitten", "Blätter unbeschnitten"));
		cdcEdgeSelectItems.add(new SelectItem("Kopfgoldschnitt", "Kopfgoldschnitt"));
		cdcEdgeSelectItems.add(new SelectItem("Fore-edge-painting", "Fore-edge-painting"));
		cdcEdgeSelectItems.add(new SelectItem("Bögen teilweise unaufgeschnitten", "Bögen teilweise unaufgeschnitten"));
		cdcEdgeSelectItems.add(new SelectItem("aufgeschnitten", "aufgeschnitten"));
		cdcEdgeSelectItems.add(new SelectItem("Blätter teilweise unbeschnitten", "Blätter teilweise unbeschnitten"));
		
		
		this.cdcLeafMarkerSelectItems = new ArrayList<SelectItem>();
		//List<SelectItem> leafMarkerSelectItems = new ArrayList<SelectItem>();
		cdcLeafMarkerSelectItems.add(new SelectItem("Blattweiser Pergament", "Blattweiser Pergament"));
		cdcLeafMarkerSelectItems.add(new SelectItem("Blattweiser Leder", "Blattweiser Leder"));
		cdcLeafMarkerSelectItems.add(new SelectItem("Blattweiser Papier", "Blattweiser Papier"));
		cdcLeafMarkerSelectItems.add(new SelectItem("Lesezeichen, unbeschriebenes Papier", "Lesezeichen, unbeschriebenes Papier"));
		cdcLeafMarkerSelectItems.add(new SelectItem("Lesezeichen lose, mit Tinte beschrieben", "Lesezeichen lose, mit Tinte beschrieben"));
		cdcLeafMarkerSelectItems.add(new SelectItem("Lesezeichen, ausgeschnitten und bedruckt", "Lesezeichen, ausgeschnitten und bedruckt"));
		cdcLeafMarkerSelectItems.add(new SelectItem("Lesezeichen bemalt", "Lesezeichen bemalt"));
		//SelectItemGroup leafMarkerGroup = new SelectItemGroup(InternationalizationHelper.getLabel("cdc_leafMarker"));
		//leafMarkerGroup.setSelectItems(cdcLeafMarkerSelectItems.toArray(new SelectItem[]{}));
		//cdcSelectItems.add(leafMarkerGroup);
		
		
		cdcMarginaliaSelectItems = new ArrayList<SelectItem>();
		cdcMarginaliaSelectItems.add(new SelectItem("Interlinearglossen", "Interlinearglossen"));
		cdcMarginaliaSelectItems.add(new SelectItem("Marginalglossen", "Marginalglossen"));
		cdcMarginaliaSelectItems.add(new SelectItem("Korrekturen", "Korrekturen"));
		cdcMarginaliaSelectItems.add(new SelectItem("Randnotizen", "Randnotizen"));
		cdcMarginaliaSelectItems.add(new SelectItem("Zeigehand", "Zeigehand"));
		cdcMarginaliaSelectItems.add(new SelectItem("autonome Randzeichnungen", "autonome Randzeichnungen"));
		cdcMarginaliaSelectItems.add(new SelectItem("Schemata", "Schemata"));
		cdcMarginaliaSelectItems.add(new SelectItem("Stammbaum", "Stammbaum"));
		cdcMarginaliaSelectItems.add(new SelectItem("Federproben", "Federproben"));
		cdcMarginaliaSelectItems.add(new SelectItem("Anstreichungen", "Anstreichungen"));
		cdcMarginaliaSelectItems.add(new SelectItem("Unterstreichungen", "Unterstreichungen"));
		
		
	
		
		
		this.cdcBindingSelectItems = new ArrayList<SelectItem>();
		//List<SelectItem> bindingSelectItems = new ArrayList<SelectItem>();
		cdcBindingSelectItems.add(new SelectItem("Pergamenteinband", "Pergamenteinband"));
		cdcBindingSelectItems.add(new SelectItem("Pergament auf Holz", "Pergament auf Holz"));
		cdcBindingSelectItems.add(new SelectItem("Pergament auf Pappe", "Pergament auf Pappe"));
		cdcBindingSelectItems.add(new SelectItem("Halbpergamenteinband", "Halbpergamenteinband"));
		cdcBindingSelectItems.add(new SelectItem("Viertelpergamenteinband", "Viertelpergamenteinband"));
		cdcBindingSelectItems.add(new SelectItem("flexibler Pergamenteinband", "flexibler Pergamenteinband"));
		cdcBindingSelectItems.add(new SelectItem("Schweinsledereinband", "Schweinsledereinband"));
		cdcBindingSelectItems.add(new SelectItem("Schweinsleder auf Holz", "Schweinsleder auf Holz"));
		cdcBindingSelectItems.add(new SelectItem("Schweinsleder auf Pappe", "Schweinsleder auf Pappe"));
		cdcBindingSelectItems.add(new SelectItem("Wildschweinledereinband", "Wildschweinledereinband"));
		cdcBindingSelectItems.add(new SelectItem("Wildschweinleder auf Holz", "Wildschweinleder auf Holz"));
		cdcBindingSelectItems.add(new SelectItem("Wildschweinleder auf Pappe", "Wildschweinleder auf Pappe"));
		cdcBindingSelectItems.add(new SelectItem("Kalbsledereinband", "Kalbsledereinband"));
		cdcBindingSelectItems.add(new SelectItem("Kalbsleder auf Holz", "Kalbsleder auf Holz"));
		cdcBindingSelectItems.add(new SelectItem("Kalbsleder auf Pappe", "Kalbsleder auf Pappe"));
		cdcBindingSelectItems.add(new SelectItem("Ziegenledereinband", "Ziegenledereinband"));
		cdcBindingSelectItems.add(new SelectItem("Ziegenleder auf Holz", "Ziegenleder auf Holz"));
		cdcBindingSelectItems.add(new SelectItem("Ziegenleder auf Pappe", "Ziegenleder auf Pappe"));
		cdcBindingSelectItems.add(new SelectItem("Schafsledereinband", "Schafsledereinband"));
		cdcBindingSelectItems.add(new SelectItem("Schafsleder auf Holz", "Schafsleder auf Holz"));
		cdcBindingSelectItems.add(new SelectItem("Schafsleder auf Pappe", "Schafsleder auf Pappe"));
		cdcBindingSelectItems.add(new SelectItem("Halbledereinband", "Halbledereinband"));
		cdcBindingSelectItems.add(new SelectItem("Viertelledereinband", "Viertelledereinband"));
		cdcBindingSelectItems.add(new SelectItem("Pappeinband", "Pappeinband"));
		cdcBindingSelectItems.add(new SelectItem("Pappeinband - Flexibler Pappeinband", "Pappeinband - Flexibler Pappeinband"));
		cdcBindingSelectItems.add(new SelectItem("Pappeinband - Marmorpapier auf Pappe", "Pappeinband - Marmorpapier auf Pappe"));
		cdcBindingSelectItems.add(new SelectItem("Pappeinband - Buntpapier auf Pappe", "Pappeinband - Buntpapier auf Pappe"));
		cdcBindingSelectItems.add(new SelectItem("Pappeinband - Brokatpapier auf Pappe", "Pappeinband - Brokatpapier auf Pappe"));
		cdcBindingSelectItems.add(new SelectItem("Pappeinband - Büttenpapier", "Pappeinband - Büttenpapier"));
		cdcBindingSelectItems.add(new SelectItem("Interimsumschlag - Papier", "Interimsumschlag - Papier"));
		cdcBindingSelectItems.add(new SelectItem("Interimseinband - Pappe", "Interimseinband - Pappe"));
		cdcBindingSelectItems.add(new SelectItem("Ganzgewebeband", "Ganzgewebeband"));
		cdcBindingSelectItems.add(new SelectItem("Ganzgewebeband - Baumwolle", "Ganzgewebeband - Baumwolle"));
		cdcBindingSelectItems.add(new SelectItem("Ganzgewebeband - Leinenband", "Ganzgewebeband - Leinenband"));
		cdcBindingSelectItems.add(new SelectItem("Gewebeband - Efalin Feinleinen", "Gewebeband - Efalin Feinleinen"));
		cdcBindingSelectItems.add(new SelectItem("Ganzgewebeband - Samtband", "Ganzgewebeband - Samtband"));
		cdcBindingSelectItems.add(new SelectItem("Ganzgewebeband - Seide", "Ganzgewebeband - Seide"));
		cdcBindingSelectItems.add(new SelectItem("Halbgewebeband", "Halbgewebeband"));
		cdcBindingSelectItems.add(new SelectItem("Halbgewebeband - Baumwolle", "Halbgewebeband - Baumwolle"));
		cdcBindingSelectItems.add(new SelectItem("Halbgewebeband - Leinen", "Halbgewebeband - Leinen"));
		cdcBindingSelectItems.add(new SelectItem("Halbgewebeband - Efalin Feinleinen", "Halbgewebeband - Efalin Feinleinen"));
		cdcBindingSelectItems.add(new SelectItem("Halbgewebeband - Samt", "Halbgewebeband - Samt"));
		cdcBindingSelectItems.add(new SelectItem("Halbgewebeband - Seide", "Halbgewebeband - Seide"));
		cdcBindingSelectItems.add(new SelectItem("Ganzgewebe - Mappe", "Ganzgewebe - Mappe"));
		cdcBindingSelectItems.add(new SelectItem("Viertelgewebeband", "Viertelgewebeband"));
		cdcBindingSelectItems.add(new SelectItem("Pamphlet-binding", "Pamphlet-binding"));
		cdcBindingSelectItems.add(new SelectItem("ohne festen Einband", "ohne festen Einband"));
		cdcBindingSelectItems.add(new SelectItem("Maroquineinband", "Maroquineinband"));
		cdcBindingSelectItems.add(new SelectItem("Prachteinband", "Prachteinband"));
		cdcBindingSelectItems.add(new SelectItem("Boxcalfband", "Boxcalfband"));
		cdcBindingSelectItems.add(new SelectItem("Pappeinband - Pappmappe", "Pappeinband - Pappmappe"));
		cdcBindingSelectItems.add(new SelectItem("Ledermappe", "Ledermappe"));
		SelectItemGroup bindingGroup = new SelectItemGroup(InternationalizationHelper.getLabel("cdc_binding"));
		bindingGroup.setSelectItems(cdcBindingSelectItems.toArray(new SelectItem[]{}));
		//cdcSelectItems.add(bindingGroup);

		this.cdcBookCoverDecorationSelectItems = new ArrayList<SelectItem>();
		//List<SelectItem> bookCoverDecorationSelectItems = new ArrayList<SelectItem>();
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Flacher Rücken", "Flacher Rücken"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Bünde", "Bünde"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Doppelbünde", "Doppelbünde"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Querbänder", "Querbänder"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Blindstempelung", "Blindstempelung"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Blindstempelung - Einzelstempel", "Blindstempelung - Einzelstempel"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Blindstempelung - Rollenstempel", "Blindstempelung - Rollenstempel"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Blindstempelung - Plattenstempel", "Blindstempelung - Plattenstempel"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Goldprägung", "Goldprägung"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Streicheisenlinien", "Streicheisenlinien"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Lederschnitt", "Lederschnitt"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Goldschnitt punziert", "Goldschnitt punziert"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Goldschnitt bemalt", "Goldschnitt bemalt"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Intarsien", "Intarsien"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Einlegearbeit", "Einlegearbeit"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Wachsmalerei", "Wachsmalerei"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Lackmalerei", "Lackmalerei"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Silberbemalung", "Silberbemalung"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Schalblonierung farbig", "Schalblonierung farbig"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Stehkantenvergoldung", "Stehkantenvergoldung"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Innenkantenvergoldung", "Innenkantenvergoldung"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Wappensupralibros", "Wappensupralibros"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Portraitsupralibros", "Portraitsupralibros"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Cameo", "Cameo"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Motto", "Motto"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Buckel", "Buckel"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Eckbeschläge", "Eckbeschläge"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Schließbänder Leder oder Metall", "Schließbänder Leder oder Metall"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Schleifenschließen Leder", "Schleifenschließen Leder"));
		cdcBookCoverDecorationSelectItems.add(new SelectItem("Schleifenschließen textil", "Schleifenschließen textil"));

		SelectItemGroup bookCoverDecorationGroup = new SelectItemGroup(InternationalizationHelper.getLabel("cdc_bookCoverDecoration"));
		bookCoverDecorationGroup.setSelectItems(cdcBookCoverDecorationSelectItems.toArray(new SelectItem[]{}));
		//cdcSelectItems.add(bookCoverDecorationGroup);
		
	}
	
	public List<SelectItem> getCdcSelectItemsForSearchType(SearchType st)
	{
		
		switch (SearchType.valueOf(st.name()))
		{
			case CDC_OBJECT_TYPE:
			
				
				return cdcObjectTypeSelectItems;
			
			case CDC_LEAF_MARKER:
			
				return cdcLeafMarkerSelectItems;
			
			case CDC_BINDING:
			
				return cdcBindingSelectItems;
			
			case CDC_BOOK_COVER_DECORATION :
			
				return cdcBookCoverDecorationSelectItems;
			
			case CDC_TIPPED_IN:
			
				return cdcTippedInSelectItems;
			
			case CDC_ENDPAPER:
				
				return cdcEndPaperSelectItems;
			
			case CDC_MARGINALIA:
				
				return cdcMarginaliaSelectItems;
			
			case CDC_EDGE:
				
				return cdcEdgeSelectItems;
			
		}
		
		return null;
	}
	
	public String startSearch()
	{		
		try {
			List<SearchCriterion> scList = new ArrayList<SearchCriterion>();

			scList.addAll(this.searchCriterionList);
			
			this.setCollectionSearch(scList);
			this.setYearSearch(scList);
			
			if (!this.freeSearch.trim().equals(""))
			{
				//Set free search
				SearchCriterion scFree = new SearchCriterion(SearchType.FREE_AND_FULLTEXT, this.freeSearch);
				scList.add(scFree);
			}
			
			//Set fulltext search
			if(!this.fulltextSearch.getValue().trim().isEmpty())
			{
				
				scList.add(this.fulltextSearch);
			}
			
			//Set cdc search
			if(!this.cdcSearch.getValue().trim().isEmpty())
			{
				
				scList.add(this.cdcSearch);
			}
			
			scList.addAll(cdcSearchCriterionList);
			
		
			advancedSearchResultBean.setSearchCriterionList(scList);
			String cql = searchBean.getAdvancedSearchCQL(scList);
			advancedSearchResultBean.setCqlQuery(cql);
			return "pretty:searchResult";
		} catch (Exception e) {
			logger.error("Error while creating CQL query", e);
			return "";
		}
	}
	
	/**
	 * Set the collection search criterion
	 */
	private void setCollectionSearch(List<SearchCriterion> scList)
	{		
		//Set context id
		boolean start = false;
		boolean end = false;
		for(int i = 0; i < contextScElements.size(); i ++)
		{
			SearchCriterion scCon;
			ContextSearch contextSearch = contextScElements.get(i);
			
			if (i == contextScElements.size()-1) end = true;
			
			//Means, a specific context was selected
			if (!contextSearch.getContextId().equals(""))
			{
				if(contextScElements.size()==1)
				{
					scCon = new SearchCriterion(Operator.AND, SearchType.CONTEXT_ID, contextSearch.getContextId(),1,1);
					scList.add(scCon);
				}
				else if (i==0 && !start)
				{
					scCon = new SearchCriterion(Operator.AND, SearchType.CONTEXT_ID, contextSearch.getContextId(),1,0);
					scList.add(scCon);
					start = true;
				}
				else if (i == contextScElements.size()-1 && end)
				{
					scCon = new SearchCriterion(Operator.OR, SearchType.CONTEXT_ID, contextSearch.getContextId(),0,1);
					scList.add(scCon);
				}
				else
				{
					scCon = new SearchCriterion(Operator.OR, SearchType.CONTEXT_ID, contextSearch.getContextId());
					scList.add(scCon);
				}
			}

			//Select all context of a ou
			else if(contextSearch.getOuId()!=null && !contextSearch.getOuId().equals(""))
			{
				if (contextSearch.getContextId().equals("") && contextSearch.getContextList().size() > 0)
				{
					//First elem is 'All collections'
					for (int y = 1; y < contextSearch.getContextList().size(); y ++)
					{
						String currentContextId = contextSearch.getContextList().get(y).getValue().toString();
						
						
						
						if (y == 1 && !start)
						{
							scCon = new SearchCriterion(Operator.AND, SearchType.CONTEXT_ID, currentContextId,1,0);
							scList.add(scCon);
							start = true;
						}
						else
						{
							//last element
							if (y == contextSearch.getContextList().size()-1 && end)
							{
								scCon = new SearchCriterion(Operator.OR, SearchType.CONTEXT_ID, currentContextId,0,1);
								scList.add(scCon);
							}
							else
							{
								scCon = new SearchCriterion(Operator.OR, SearchType.CONTEXT_ID, currentContextId);
								scList.add(scCon);
							}
						}
					}			
				}
			}
		}

	}

	/**
	 * Set the year search criterion
	 */
	private void setYearSearch(List<SearchCriterion> scList) throws RuntimeException
	{
		if (!this.yearFrom.getValue().trim().isEmpty())
		{
			if (this.yearTo.getValue().trim().isEmpty())
			{
				scList.add(this.yearFrom);
			}
			
			else
			{ 
				this.yearFrom.setConnector(">=");
				this.yearFrom.setOpenBracket(1);
				this.yearFrom.setCloseBracket(0);
				scList.add(this.yearFrom);
				this.yearTo.setConnector("<=");
				this.yearTo.setOpenBracket(0);
				this.yearTo.setCloseBracket(1);
				scList.add(yearTo);
			}
		}
		else
		{
			if (!this.yearTo.getValue().trim().isEmpty())
			{
				MessageHelper.errorMessage("Please provide a value for \"year from\"");
				throw new RuntimeException("\"Year from\" not provided");
			}
		}
	}

	
	public String resetSearch()
	{
		this.searchCriterionList = new ArrayList<SearchCriterion>();
		this.searchCriterionList.add(new SearchCriterion(SearchType.FREE, ""));
		this.searchCriterionList.add(new SearchCriterion(SearchType.AUTHOR, ""));
		this.searchCriterionList.add(new SearchCriterion(SearchType.TITLE, ""));
		
		this.freeSearch = "";

		this.fulltextSearch = new SearchCriterion(SearchType.FULLTEXT, "");
		this.yearFrom = new SearchCriterion(SearchType.YEAR, "");
		this.yearTo = new SearchCriterion(SearchType.YEAR, "");
		this.cdcSearch = new SearchCriterion(SearchType.CODICOLOGICAL, "");
		
		this.cdcSearchCriterionList = new ArrayList<SearchCriterion>();
		this.cdcSearchCriterionList.add(new SearchCriterion(SearchType.CDC_OBJECT_TYPE, ""));
		
		//Reset context
		this.contextScElements.clear();
		ContextSearch cs = new ContextSearch();
		refreshContextList(cs);
		this.contextScElements.add(cs);
		
		//Reset ou
		List<SelectItem> ouSelectItems = new ArrayList<SelectItem>();
		ouSelectItems.add(new SelectItem("",InternationalizationHelper.getLabel("sc_allLib")));
		for(OrganizationalUnit ou : ouServiceBean.retrieveOUs())
		{
			ouSelectItems.add(new SelectItem(ou.getObjid(),ou.getProperties().getName()));
		}
		this.setAllLibItems(ouSelectItems);
		
		
		return"";
	}

	public AdvancedSearchResultBean getAdvancedSearchResultBean() {
		return advancedSearchResultBean;
	}

	public void setAdvancedSearchResultBean(AdvancedSearchResultBean advancedSearchResultBean) {
		this.advancedSearchResultBean = advancedSearchResultBean;
	}

	public List<SearchCriterion> getSearchCriterionList() {
		return searchCriterionList;
	}

	public void setSearchCriterionList(List<SearchCriterion> searchCriterionList) {
		this.searchCriterionList = searchCriterionList;
	}

	public String getFreeSearch() {
		return freeSearch;
	}

	public void setFreeSearch(String freeSearch) {
		this.freeSearch = freeSearch;
	}
	
	public SearchCriterion getYearFrom() {
		return yearFrom;
	}

	public void setYearFrom(SearchCriterion yearFrom) {
		this.yearFrom = yearFrom;
	}
	
	public SearchCriterion getYearTo() {
		return yearTo;
	}

	public void setYearTo(SearchCriterion yearTo) {
		this.yearTo = yearTo;
	}

	
	
	public void newContextScElement()
	{	
		ContextSearch cs = new ContextSearch();		
		refreshContextList(cs);
		
		this.contextScElements.add(cs);
	}
		
	public List<ContextSearch> getContextScElements() {
		return contextScElements;
	}

	public void setContextScElements(List<ContextSearch> contextScElements) {
		this.contextScElements = contextScElements;
	}
	public List<SelectItem> getAllLibItems() {
		return allLibItems;
	}

	public void setAllLibItems(List<SelectItem> allLibItems) {
		this.allLibItems = allLibItems;
	}

	public List<SelectItem> getAllConItems() {
		return allConItems;
	}

	public void setAllConItems(List<SelectItem> allConItems) {
		this.allConItems = allConItems;
	}

	public SearchCriterion getFulltextSearch() {
		return fulltextSearch;
	}

	public void setFulltextSearch(SearchCriterion fulltextSearch) {
		this.fulltextSearch = fulltextSearch;
	}

	public SearchCriterion getCdcSearch() {
		return cdcSearch;
	}

	public void setCdcSearch(SearchCriterion cdcSearch) {
		this.cdcSearch = cdcSearch;
	}

	public InternationalizationHelper getInternationalizationHelper() {
		return internationalizationHelper;
	}

	public void setInternationalizationHelper(InternationalizationHelper internationalizationHelper) {
		this.internationalizationHelper = internationalizationHelper;
	}


	public List<SearchCriterion> getCdcSearchCriterionList() {
		return cdcSearchCriterionList;
	}

	public void setCdcSearchCriterionList(List<SearchCriterion> cdcSearchCriterionList) {
		this.cdcSearchCriterionList = cdcSearchCriterionList;
	}

	public List<SelectItem> getCdcObjectTypeSelectItems() {
		return cdcObjectTypeSelectItems;
	}

	public void setCdcObjectTypeSelectItems(
			List<SelectItem> cdcObjectTypeSelectItems) {
		this.cdcObjectTypeSelectItems = cdcObjectTypeSelectItems;
	}

	public List<SelectItem> getCdcLeafMarkerSelectItems() {
		return cdcLeafMarkerSelectItems;
	}

	public void setCdcLeafMarkerSelectItems(
			List<SelectItem> cdcLeafMarkerSelectItems) {
		this.cdcLeafMarkerSelectItems = cdcLeafMarkerSelectItems;
	}

	public List<SelectItem> getCdcTippedInSelectItems() {
		return cdcTippedInSelectItems;
	}

	public void setCdcTippedInSelectItems(List<SelectItem> cdcTippedInSelectItems) {
		this.cdcTippedInSelectItems = cdcTippedInSelectItems;
	}

	public List<SelectItem> getCdcBindingSelectItems() {
		return cdcBindingSelectItems;
	}

	public void setCdcBindingSelectItems(List<SelectItem> cdcBindingSelectItems) {
		this.cdcBindingSelectItems = cdcBindingSelectItems;
	}

	public List<SelectItem> getCdcBookCoverDecorationSelectItems() {
		return cdcBookCoverDecorationSelectItems;
	}

	public void setCdcBookCoverDecorationSelectItems(
			List<SelectItem> cdcBookCoverDecorationSelectItems) {
		this.cdcBookCoverDecorationSelectItems = cdcBookCoverDecorationSelectItems;
	}

	
	
}
