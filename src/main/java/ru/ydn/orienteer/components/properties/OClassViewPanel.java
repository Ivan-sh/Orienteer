package ru.ydn.orienteer.components.properties;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.OClassPageLink;

import com.orientechnologies.orient.core.metadata.schema.OClass;

public class OClassViewPanel extends AbstractLinkViewPanel<OClass> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OClassViewPanel(String id, IModel<OClass> model) {
		super(id, model);
	}

	public OClassViewPanel(String id) {
		super(id);
	}
	
	@Override
	protected AbstractLink newLink(String id) {
		return new OClassPageLink("link", getModel()).setClassNameAsBody(true);
	}

}
