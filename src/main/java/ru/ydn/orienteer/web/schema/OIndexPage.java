package ru.ydn.orienteer.web.schema;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.annotation.mount.MountPath;

import ru.ydn.orienteer.components.SchemaPageHeader;
import ru.ydn.orienteer.components.commands.EditCommand;
import ru.ydn.orienteer.components.commands.EditSchemaCommand;
import ru.ydn.orienteer.components.commands.RebuildOIndexCommand;
import ru.ydn.orienteer.components.commands.SaveSchemaCommand;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.OClassViewPanel;
import ru.ydn.orienteer.components.properties.OIndexMetaPanel;
import ru.ydn.orienteer.components.properties.OPropertyMetaPanel;
import ru.ydn.orienteer.components.structuretable.OrienteerStructureTable;
import ru.ydn.orienteer.web.OrienteerBasePage;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OIndexModel;
import ru.ydn.wicket.wicketorientdb.proto.OIndexPrototyper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;

@MountPath("/index/${indexName}")
@RequiredOrientResource(value=ORule.ResourceGeneric.SCHEMA, permissions=OrientPermission.READ)
public class OIndexPage extends OrienteerBasePage<OIndex<?>>
{
	private static final long serialVersionUID = 1L;

	private OrienteerStructureTable<OIndex<?>, String> structureTable;
	
	private IModel<DisplayMode> modeModel = DisplayMode.VIEW.asModel();
	
	public OIndexPage(IModel<OIndex<?>> model) {
		super(model);
	}

	public OIndexPage(PageParameters parameters) {
		super(parameters);
		DisplayMode mode = DisplayMode.parse(parameters.get("mode").toOptionalString());
		if(mode!=null) modeModel.setObject(mode);
	}

	@Override
	protected IModel<OIndex<?>> resolveByPageParameters(
			PageParameters pageParameters) {
		String indexName = pageParameters.get("indexName").toOptionalString();
		return Strings.isEmpty(indexName)?null:new OIndexModel(indexName);
	}
	
	public IModel<DisplayMode> getDisplayModeModel() {
		return modeModel;
	}
	
	public DisplayMode getDisplayMode()
	{
		return modeModel.getObject();
	}
	
	public OIndexPage setDisplayMode(DisplayMode mode)
	{
		modeModel.setObject(mode);
		return this;
	}

	@Override
	public void initialize() {
		super.initialize();
		Form<OIndex<?>> form = new Form<OIndex<?>>("form");
		structureTable  = new OrienteerStructureTable<OIndex<?>, String>("attributes", getModel(), OIndexPrototyper.OINDEX_ATTRS) {

			@Override
			protected Component getValueComponent(String id, final IModel<String> rowModel) {
				return new OIndexMetaPanel<Object>(id, modeModel, OIndexPage.this.getModel(), rowModel);
			}
		};
		
		form.add(structureTable);
		
		add(form);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		structureTable.addCommand(new EditSchemaCommand<OIndex<?>>(structureTable, modeModel));
		structureTable.addCommand(new SaveSchemaCommand<OIndex<?>>(structureTable, modeModel, getModel()));
		structureTable.addCommand(new RebuildOIndexCommand(structureTable));
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		if(getModelObject()==null) throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	public IModel<String> getTitleModel() {
		return new PropertyModel<String>(getModel(), "name");
	}
	
	@Override
	protected Component newPageHeaderComponent(String componentId) {
		SchemaPageHeader pageHeader = new SchemaPageHeader(componentId);
		pageHeader.addChild(new OClassViewPanel(pageHeader.newChildId(), new OClassModel(new PropertyModel<String>(getModel(), OIndexPrototyper.DEF_CLASS_NAME))));
		pageHeader.addChild(new Label(pageHeader.newChildId(), getTitleModel()));
		return pageHeader;
	}
}
