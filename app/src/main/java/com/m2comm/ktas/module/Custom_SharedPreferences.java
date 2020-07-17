package com.m2comm.ktas.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Custom_SharedPreferences
{	
		//저장이름
	    private final String PREF_NAME = "com.m2comm.ktas";
	 
	  /*  public final static String PREF_INTRO_USER_AGREEMENT = "PREF_USER_AGREEMENT";
	    public final static String PREF_MAIN_VALUE = "PREF_MAIN_VALUE";*/
	  Context mContext;
		SharedPreferences pref;

	    public Custom_SharedPreferences(Context c)
	    {
	        this.mContext = c;
	        this.pref = c.getSharedPreferences(PREF_NAME, 0);
	    }
	 
	    public void put(String key, String value)
	    {
	        SharedPreferences.Editor editor = this.pref.edit();
	        editor.putString(key, value);
	        editor.commit();
	    }

		public void put(String key, Long value)
		{
			SharedPreferences.Editor editor = this.pref.edit();

			editor.putLong(key, value);
			editor.commit();
		}
	 
	    public void put(String key, boolean value)
	    {
	        SharedPreferences.Editor editor = this.pref.edit();
	 
	        editor.putBoolean(key, value);
	        editor.commit();
	    }
	 
	    public void put(String key, int value)
	    {
	        SharedPreferences.Editor editor = this.pref.edit();
	 
	        editor.putInt(key, value);
	        editor.commit();
	    }
	 
	    public String getValue(String key, String dftValue)
	    {

	        try 
	        {
	            return this.pref.getString(key, dftValue);
	        } 
	        catch (Exception e)
	        {
	            return dftValue;
	        }
	 
	    }
	    public int getValue(String key, int dftValue)
	    {
	        try 
	        {
	            return this.pref.getInt(key, dftValue);
	        } 
	        catch (Exception e)
	        {
	            return dftValue;
	        }
	 
	    }
	    public boolean getValue(String key, boolean dftValue)
	    {
	        try
	        {
	            return this.pref.getBoolean(key, dftValue);
	        } 
	        catch (Exception e)
	        {
	            return dftValue;
	        }
	    }

	    public void remove (String key) {
	    	SharedPreferences.Editor editor = this.pref.edit();
	    	editor.remove(key);
	    	editor.commit();
		}
	
	
	
	
	
}
