package com.principalmvl.lojackmykids.Interfaces;

import android.content.DialogInterface;

public interface OnAlertDialogClickListener {
	
	/**
    * This method is invoked when the positive button is clicked
    * @param dialog
    */
   public void onPositiveClick(DialogInterface dialog, int id);
    
   /**
    * This method is invoked when the negative button is clicked
    * @param dialog
    */
   public void onNegativeClick(DialogInterface dialog, int id);
    
   /**This method is invoked hen the neutral button is clicked
    * @param dialog
    */
   public void onNeutralClick(DialogInterface dialog, int id);
}
