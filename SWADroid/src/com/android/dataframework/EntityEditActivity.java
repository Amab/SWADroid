package com.android.dataframework;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.android.dataframework.core.Field;
import com.android.dataframework.core.Table;

import java.util.HashMap;
import java.util.Map;

public abstract class EntityEditActivity extends Activity 
{
	public final static String TABLENAME_ID = "tablename";
	
	protected Long mRowId;
	protected String mKeyField;
	protected Bundle mySavedInstanceState;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		mKeyField = getKeyFieldName();
        mySavedInstanceState = savedInstanceState;
        setView();
	}
	
	/**
	 * Guarda el nombre del campo clave
	 */
	protected String getKeyFieldName()
	{
		return DataFramework.KEY_ID;
	}
	
	abstract protected String getTableName();
	
	
	/**
	 * Asignar vista
	 */
	abstract protected void setView();
	
	@Override
	protected void onPause() {
		super.onPause();
		closeDatabase();
	}
	/**
	 * Cerrar la base de datos.
	 */
	protected void closeDatabase()
	{
		DataFramework.getInstance().close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Abrimos la base de datos
		openDatabase();
		// Cargamos los datos
		loadDataToView();
    }
	
	/**
	 * Cerrar la base de datos.
	 */
	abstract protected void openDatabase();
	
	protected void loadDataToView()
	{
		// Actuamos dependiendo si hay datos guardados en el estado de la instancia. 
		if (mySavedInstanceState == null)
		{
			//  Si no hay datos guardados es que se trata de la primera vez,
			// asi que vemos si hay identificador de fila, si la hay es que es una edicion
			// si no es que es un alta nueva.
			Bundle extras = getIntent().getExtras();
			mRowId = (Long) (extras != null ? extras.get(mKeyField) 
					  : null);

			if (mRowId != null)
			{
				// Si hay RowId cargamos de la base de datos el registro en cuestion
				loadDataFromEntityToView(new Entity(getTableName(), mRowId));
			}
			else
			{
				// Si no hay RowId se trata de un nuevo alta
				blankView();
			}
		}
		else
		{
			// Si hay estado guardado hay que restaurar los datos desde el estado 
			// de la actividad. 
			loadDataFromState(mySavedInstanceState);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Almacenamos el estado
		mySavedInstanceState = outState;

		// Si hay RowId lo guardamos en el estado.
		if (mRowId != null)
			outState.putLong(mKeyField, mRowId);
		
		// Guardamos el resto de datos en el estado
		saveDataToState(mySavedInstanceState);		
	}
	
	abstract protected int getMainViewId();
	
	protected Map<String, View> getAttributeViews()
	{
		Map<String, View> views = new HashMap<String, View>();
		View mainView = findViewById(getMainViewId());
		Table table = new Table(getTableName());
		
		// Buscamos los Views que tengan en el Tag el nombre de uno de los campos.
		for (Field f: table.getFields())
		{
			View v = mainView.findViewWithTag(f.getName());
			if (v != null)
				views.put(f.getName(), v);
		}

		return views;
	}
	
	/**
	 * Carga los datos de la base de datos a la interfaz de usuario. 
	 * Prepara la vista para editar los datos.
	 * @param rowId Identificador unico del registro.
	 */
	protected void loadDataFromEntityToView(Entity entity)
	{
		Table table = new Table(getTableName());
		Map<String, View> views = getAttributeViews();
		
		// Buscamos los Views que tengan en el Tag el nombre de uno de los campos.
		for (Field f: table.getFields())
		{
			Object value = entity.getValue(f.getName());
			View view = views.get(f.getName());
			if (value != null && view != null)
			{
				if (EditText.class.isInstance(view))
				{
					((EditText)view).setText(value.toString());
				}
			}
		}		
	}
	
	/**
	 * Guardar datos en la base de datos, se debe llamar desde el 
	 * boton guardar, no se llama automaticamente.
	 */
	abstract protected void saveToDB();

	/**
	 * Carga los datos del estado de la actividad a la interfaz de usuario
	 * Restaura los valores previamente guardados en el estado de la actividad.
	 * @param state Estado de donde sacar los datos
	 */
	abstract protected void loadDataFromState(Bundle state);
	
	/**
	 * Guarda los datos de la interface en el estado para
	 * ser restaurados despues.
	 * @param outState
	 */
	abstract protected void saveDataToState(Bundle state);
	
	/**
	 * Prepara la interfaz de usuario para crear un nuevo registro.
	 */
	abstract protected void blankView();
	
	
}
