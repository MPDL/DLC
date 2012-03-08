package de.mpg.mpdl.dlc.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.log4j.Logger;
import org.richfaces.component.UIExtendedDataTable;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.editor.TeiElementWrapper.PositionType;
import de.mpg.mpdl.dlc.editor.TeiNode.Type;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.RomanNumberConverter;
import de.mpg.mpdl.dlc.viewer.VolumeLoaderBean;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.teisd.Body;
import de.mpg.mpdl.dlc.vo.teisd.Div;
import de.mpg.mpdl.dlc.vo.teisd.Figure;
import de.mpg.mpdl.dlc.vo.teisd.Pagebreak;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv.ElementType;
import de.mpg.mpdl.dlc.vo.teisd.TeiSd;
import de.mpg.mpdl.dlc.vo.teisd.TitlePage;

@ManagedBean
@ViewScoped
@URLMapping(id = "structuralEditor", pattern = "/edit/#{structuralEditorBean.volumeId}", viewId = "/structuralEditor.xhtml")
public class StructuralEditorBean extends VolumeLoaderBean {

	private static Logger logger = Logger.getLogger(StructuralEditorBean.class);
	
	
	
	//Flat list of Treee
	private List<TeiElementWrapper> flatTeiElementList;
	
	
	private TeiElementWrapper selectedPb;
	//private List<PagebreakWrapper> pagebreakWrapperList;
	
	private TeiElementWrapper currentEditElement;
	
	//private PagebreakWrapper selectedPbWrapper;
	

	private ElementType selectedStructuralType;
	
	
	public enum PaginationType
	{
		ALL_ARABIC, ALL_ROMAN, ALL_FREE, SELECTED_ARABIC, SELECTED_ROMAN, SELECTED_FREE
	}
	
	
	public StructuralEditorBean()
	{

		selectedStructuralType = ElementType.DIV;
		
		currentEditElement = new TeiElementWrapper();
		Div div = new Div();
		div.setType("chapter");
		currentEditElement.setTeiElement(div);
	}
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{
		super.loadVolume();
	}
	
	@Override
	protected void volumeLoaded() {
		
		if(volume.getTeiSd() == null)
		{
			//create a new TEI SD with Pagebreaks only
			TeiSd teiSd = new TeiSd();
			Body body = new Body();
			teiSd.getPbOrDiv().add(body);
			for(Page p : volume.getMets().getPages())
			{
				Pagebreak pb = new Pagebreak();
				pb.setFacs(p.getContentIds());
				pb.setParent(body);
				pb.setId(p.getId());
				body.getPbOrDiv().add(pb);
			}
			volume.setTeiSd(teiSd);
		}
		
		
		
		this.flatTeiElementList = teiSdToFlatTeiElementList(volume.getTeiSd(), volume);
		for(TeiElementWrapper teiElWrapper : flatTeiElementList)
		{
			if(ElementType.PB.equals(teiElWrapper.getTeiElement().getElementType()))
			{
				this.selectedPb = teiElWrapper;
				break;
			}
		}
		
		
		
		//this.flatTeiTree = TeiTreeNode.teiSdToTreeNodes(volume.getTeiSd());
		
	}
	
	
	private static TeiSd flatTeiElementListToTeiSd(List<TeiElementWrapper> flatTeiElementList, TeiSd teiSd)
	{
		
		
		teiSd.getPbOrDiv().clear();
		PbOrDiv parentTeiElement = null;
		
		for(TeiElementWrapper teiElementWrapper : flatTeiElementList)
		{
			
			if(parentTeiElement == null)
			{
				teiSd.getPbOrDiv().add(teiElementWrapper.getTeiElement());
			}
			else
			{
				parentTeiElement.getPbOrDiv().add(teiElementWrapper.getTeiElement());
			}
			
			
			if(PositionType.START.equals(teiElementWrapper.getPositionType()))
			{
				teiElementWrapper.getTeiElement().setParent(parentTeiElement);
				parentTeiElement = teiElementWrapper.getTeiElement();
			}
			else if (PositionType.END.equals(teiElementWrapper.getPositionType()))
			{
				parentTeiElement = teiElementWrapper.getPartnerElement().getTeiElement().getParent();
			}
		}
		
		return teiSd;
		
	}
	
	
	
	private static List<TeiElementWrapper> teiSdToFlatTeiElementList(TeiSd teiSd, Volume v)
	{
		List<TeiElementWrapper> flatTeiElementList = new ArrayList<TeiElementWrapper>();
		recursiveTeiSdToFlat(flatTeiElementList, teiSd.getPbOrDiv(), v, 0);
		return flatTeiElementList;
		
	}
	
	private static void recursiveTeiSdToFlat(List<TeiElementWrapper> flatTeiElementList, List<PbOrDiv> currentTeiElementList, Volume volume, int pbCounter)
	{
		for(PbOrDiv currentTeiElement : currentTeiElementList)
		{
			TeiElementWrapper currentTeiStartElementWrapper = new TeiElementWrapper();
			flatTeiElementList.add(currentTeiStartElementWrapper);
			currentTeiStartElementWrapper.setTeiElement(currentTeiElement);
			if(currentTeiElement.getPbOrDiv() == null || currentTeiElement.getPbOrDiv().isEmpty())
			{
				currentTeiStartElementWrapper.setPositionType(PositionType.EMPTY);
			}
			else
			{
				currentTeiStartElementWrapper.setPositionType(PositionType.START);
			}
			if (ElementType.PB.equals(currentTeiElement.getElementType()))
			{
				
				currentTeiStartElementWrapper.setPage(volume.getPages().get(pbCounter));
				pbCounter++;
			}
			
			
			
			recursiveTeiSdToFlat(flatTeiElementList, currentTeiElement.getPbOrDiv(), volume, pbCounter);
			
			if(PositionType.START.equals(currentTeiStartElementWrapper.getPositionType()))
			{
				TeiElementWrapper currentTeiEndElementWrapper = new TeiElementWrapper();
				flatTeiElementList.add(currentTeiEndElementWrapper);
				currentTeiEndElementWrapper.setTeiElement(currentTeiElement);
				currentTeiEndElementWrapper.setPositionType(PositionType.END);
				
				currentTeiEndElementWrapper.setPartnerElement(currentTeiStartElementWrapper);
				currentTeiStartElementWrapper.setPartnerElement(currentTeiEndElementWrapper);
			}

		}
		
	}
	
	
	public void selectedStructuralElementTypeChanged()
	{
		switch (selectedStructuralType)
		{
			case DIV : 
			{
				currentEditElement = new TeiElementWrapper();
				Div div = new Div();
				div.setType("chapter");
				currentEditElement.setTeiElement(div);
				break;
			}
			
			case FIGURE : 
			{
				currentEditElement = new TeiElementWrapper();
				Figure figure = new Figure();
				currentEditElement.setTeiElement(figure);
				break;
			}
			
			case TITLE_PAGE : 
			{
				currentEditElement = new TeiElementWrapper();
				TitlePage titlePage = new TitlePage();
				currentEditElement.setTeiElement(titlePage);
				break;
			}
		
		}
		
	
	}
	
	public void createStructuralElement()
	{
		int selectedPbIndex = flatTeiElementList.indexOf(selectedPb);
		
		currentEditElement.setPositionType(PositionType.START);
		
		TeiElementWrapper endEditElement = new TeiElementWrapper();
		endEditElement.setPositionType(PositionType.END);
		endEditElement.setPartnerElement(currentEditElement);
		endEditElement.setTeiElement(currentEditElement.getTeiElement());
		currentEditElement.setPartnerElement(endEditElement);
		
		
		
		flatTeiElementList.add(selectedPbIndex + 1, currentEditElement);
		flatTeiElementList.add(selectedPbIndex + 2, endEditElement);
		
		selectedStructuralElementTypeChanged();
		
		updateTree();
		
		
	}
	
	private void updateTree()
	{
		flatTeiElementListToTeiSd(flatTeiElementList, volume.getTeiSd());
	}

	public void selectPb(TeiElementWrapper pb)
	{
		this.setSelectedPb(pb);
	}
	
	public List<TeiElementWrapper> getFlatTeiElementList() {
		return flatTeiElementList;
	}

	public void setFlatTeiElementList(List<TeiElementWrapper> flatTeiElementList) {
		this.flatTeiElementList = flatTeiElementList;
	}

	public TeiElementWrapper getSelectedPb() {
		return selectedPb;
	}

	public void setSelectedPb(TeiElementWrapper selectedPb) {
		this.selectedPb = selectedPb;
	}

	public ElementType getSelectedStructuralType() {
		return selectedStructuralType;
	}

	public void setSelectedStructuralType(ElementType selectedStructuralType) {
		this.selectedStructuralType = selectedStructuralType;
	}

	public TeiElementWrapper getCurrentEditElement() {
		return currentEditElement;
	}

	public void setCurrentEditElement(TeiElementWrapper currentEditElement) {
		this.currentEditElement = currentEditElement;
	}
	
	


}
