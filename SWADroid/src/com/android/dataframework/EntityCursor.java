/**
 * 
 * Copyright (C) 2008  Javier Perez Pacheco y Javier Ros Moreno
 *
 * Android Data Framework: Trabajo con BD SQLite en Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Javier Perez Pacheco
 * Cadiz (Spain)
 * javi.pacheco@gmail.com
 * 
 * Javier Ros Moreno
 * jros@jros.org
 * 
 * 
 *  EntityCursor es una clase que permite el acceso a una coleccion de entidades
 *  sin necesidad de tener que crear un objeto Entity por cada registro cargado 
 *  en el cursor.
 *  
 *  Ademas tambien implementa la interface Iterable, con lo que podemos usar el cursor
 *  en una instruccion foreach.
 *   
 *   Ejemplo de uso:
 *   
 *     for (Entity entity: myEntityCursor)
 *     {
 *     		if (entity.getString("name").equals("Javier"))
 *     			giveAGreatPresent(entity);     
 *     }
 *   
 */

package com.android.dataframework;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

import java.util.Iterator;


/**
 * @author javier
 *
 */
public class EntityCursor implements Cursor, Iterable<Entity> {

	private Cursor mCursor;
	private String mTableName;
	private Entity mCurrentEntity;
	
	public EntityCursor(String tableName, Cursor c)
	{
		mTableName = tableName;
		mCursor = c;
		c.moveToFirst();
	}

	/**
	 * Cuando se efect�a un movimiento en el cursor actualizamos la entidad en curso.
	 * 
	 * @param oldPosition
	 * @param newPosition
	 */
	private void onMove(int oldPosition, int newPosition)
	{
		if (oldPosition != newPosition || mCurrentEntity == null){
			mCurrentEntity = new Entity(mTableName, mCursor);
		}
	}
	
	/**
	 * Devuelve la entidad en curso. 
	 * 
	 * @return
	 */
	public Entity getEntity()
	{
		return mCurrentEntity;
	}

	/**
	 *   Implementaci�n de los m�todos abstractos de la clase Cursor.
	 *   
	 *   Tan solo realizamos las acciones indicadas sobre el Cursor pasado
	 * en el constructor.
	 */

	
	/* (non-Javadoc)
	 * @see android.database.Cursor#close()
	 */
	@Override
	public void close() {
		mCursor.close();
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#copyStringToBuffer(int, android.database.CharArrayBuffer)
	 */
	@Override
	public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
		mCursor.copyStringToBuffer(columnIndex, buffer);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#deactivate()
	 */
	@Override
	public void deactivate() {
		mCursor.deactivate();
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getBlob(int)
	 */
	@Override
	public byte[] getBlob(int columnIndex) {		
		return mCursor.getBlob(columnIndex);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getColumnCount()
	 */
	@Override
	public int getColumnCount() {		
		return mCursor.getColumnCount();
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getColumnIndex(java.lang.String)
	 */
	@Override
	public int getColumnIndex(String columnName) {		
		return mCursor.getColumnIndex(columnName);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getColumnIndexOrThrow(java.lang.String)
	 */
	@Override
	public int getColumnIndexOrThrow(String columnName)
			throws IllegalArgumentException {		
		return mCursor.getColumnIndexOrThrow(columnName);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {		
		return mCursor.getColumnName(columnIndex);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getColumnNames()
	 */
	@Override
	public String[] getColumnNames() {
		return mCursor.getColumnNames();
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getCount()
	 */
	@Override
	public int getCount() {		
		return mCursor.getCount();
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getDouble(int)
	 */
	@Override
	public double getDouble(int columnIndex) {		
		return mCursor.getDouble(columnIndex);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getExtras()
	 */
	@Override
	public Bundle getExtras() {		
		return mCursor.getExtras();
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getFloat(int)
	 */
	@Override
	public float getFloat(int columnIndex) {		
		return mCursor.getFloat(columnIndex);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getInt(int)
	 */
	@Override
	public int getInt(int columnIndex) {
		return mCursor.getInt(columnIndex);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getLong(int)
	 */
	@Override
	public long getLong(int columnIndex) {		
		return mCursor.getLong(columnIndex);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getPosition()
	 */
	@Override
	public int getPosition() {
		return mCursor.getPosition();
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getShort(int)
	 */
	@Override
	public short getShort(int columnIndex) {
		return mCursor.getShort(columnIndex);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getString(int)
	 */
	@Override
	public String getString(int columnIndex) {
		return mCursor.getString(columnIndex);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#getWantsAllOnMoveCalls()
	 */
	@Override
	public boolean getWantsAllOnMoveCalls() {
		return mCursor.getWantsAllOnMoveCalls();
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#isAfterLast()
	 */
	@Override
	public boolean isAfterLast() {
		return mCursor.isAfterLast();
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#isBeforeFirst()
	 */
	@Override
	public boolean isBeforeFirst() {
		return mCursor.isBeforeFirst();
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#isClosed()
	 */
	@Override
	public boolean isClosed() {		
		return mCursor.isClosed();
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#isFirst()
	 */
	@Override
	public boolean isFirst() {		
		return mCursor.isFirst();
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#isLast()
	 */
	@Override
	public boolean isLast() {
		return mCursor.isLast();
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#isNull(int)
	 */
	@Override
	public boolean isNull(int columnIndex) {
		return mCursor.isNull(columnIndex);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#move(int)
	 */
	@Override
	public boolean move(int offset) {
		int oldPos = getPosition();		
		boolean result = mCursor.move(offset);
		int newPos = getPosition();
		onMove(oldPos, newPos);
		return result;		
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#moveToFirst()
	 */
	@Override
	public boolean moveToFirst() {
		int oldPos = getPosition();		
		boolean result = mCursor.moveToFirst();
		int newPos = getPosition();
		onMove(oldPos, newPos);
		return result;		
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#moveToLast()
	 */
	@Override
	public boolean moveToLast() {
		int oldPos = getPosition();		
		boolean result = mCursor.moveToLast();
		int newPos = getPosition();
		onMove(oldPos, newPos);
		return result;
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#moveToNext()
	 */
	@Override
	public boolean moveToNext() {
		int oldPos = getPosition();		
		boolean result = mCursor.moveToNext();
		int newPos = getPosition();
		onMove(oldPos, newPos);
		return result;
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#moveToPosition(int)
	 */
	@Override
	public boolean moveToPosition(int position) {
		int oldPos = getPosition();		
		boolean result = mCursor.moveToPosition(position);
		int newPos = getPosition();
		onMove(oldPos, newPos);
		return result; 
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#moveToPrevious()
	 */
	@Override
	public boolean moveToPrevious() {
		int oldPos = getPosition();		
		boolean result = mCursor.moveToPrevious();
		int newPos = getPosition();
		onMove(oldPos, newPos);
		return result; 
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#registerContentObserver(android.database.ContentObserver)
	 */
	@Override
	public void registerContentObserver(ContentObserver observer) {
		mCursor.registerContentObserver(observer);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#registerDataSetObserver(android.database.DataSetObserver)
	 */
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		mCursor.registerDataSetObserver(observer);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#requery()
	 */
	@Override
	public boolean requery() {
		return mCursor.requery();
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#respond(android.os.Bundle)
	 */
	@Override
	public Bundle respond(Bundle extras) {
		return mCursor.respond(extras);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#setNotificationUri(android.content.ContentResolver, android.net.Uri)
	 */
	@Override
	public void setNotificationUri(ContentResolver cr, Uri uri) {
		mCursor.setNotificationUri(cr, uri);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#unregisterContentObserver(android.database.ContentObserver)
	 */
	@Override
	public void unregisterContentObserver(ContentObserver observer) {
		mCursor.unregisterContentObserver(observer);
	}

	/* (non-Javadoc)
	 * @see android.database.Cursor#unregisterDataSetObserver(android.database.DataSetObserver)
	 */
	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		mCursor.unregisterDataSetObserver(observer);
	}
	
	/**
	 *   Implementaci�n de los m�todos de la Interface Iterable.
	 *   
	 *   Tan solo realizamos las acciones indicadas sobre el Cursor pasado
	 * en el constructor.
	 */
	@Override
	public Iterator<Entity> iterator() {
		return new EntityIterator();
	}
	
	/** 
	 * Definicion e implementacion de la clase EntityIterator
	 */
	public class EntityIterator implements Iterator<Entity> {
		
		@Override
		public boolean hasNext() 
		{
			if (mCursor.isLast() || mCursor.isAfterLast()){
				return false;
			}else{
				return true;
			}			
		}

		@Override
		public Entity next() 
		{
			if (mCurrentEntity == null){
				moveToFirst();
			}else{
				moveToNext();
			}
			return mCurrentEntity;
		}

		@Override
		public void remove() 
		{
		}		
	}

	public int getType(int columnIndex) {
		return 0;
	}

}





