package ru.ydn.orienteer.components.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.model.AbstractCheckBoxModel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import com.google.common.base.Converter;
import com.google.common.collect.Lists;
import ru.ydn.orienteer.components.properties.BooleanEditPanel;

public class CheckBoxColumn<T, PK extends Serializable, S> extends AbstractColumn<T, S>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<PK> selected = new ArrayList<PK>();
	private Converter<T, PK> converterToPK;

	public CheckBoxColumn(IModel<String> displayModel, Converter<T, PK> converterToPK) {
		super(displayModel);
		this.converterToPK = converterToPK;
	}

	@Override
	public void populateItem(Item<ICellPopulator<T>> cellItem,
			String componentId, IModel<T> rowModel) {
		cellItem.add(new BooleanEditPanel(componentId, getCheckBoxModel(rowModel)));
	}
	
	protected AbstractCheckBoxModel getCheckBoxModel(final IModel<T> rowModel)
	{
		return new AbstractCheckBoxModel() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void unselect() {
				CheckBoxColumn.this.unselect(rowModel.getObject());
			}
			
			@Override
			public void select() {
				CheckBoxColumn.this.select(rowModel.getObject());
			}
			
			@Override
			public boolean isSelected() {
				return CheckBoxColumn.this.isSelected(rowModel.getObject());
			}
		};
	}
	
	public void resetSelection()
	{
		selected.clear();
	}
	
	public void unselect(T object) {
		selected.remove(converterToPK.convert(object));
	}
	
	public void select(T object) {
		selected.add(converterToPK.convert(object));
	}
	
	public boolean isSelected(T object) {
		return selected.contains(converterToPK.convert(object));
	}
	
	public List<T> getSelected()
	{
		return Lists.transform(selected, converterToPK.reverse());
	}

	@Override
	public String getCssClass() {
		return "checkbox-column";
	}
	
	
	
}
