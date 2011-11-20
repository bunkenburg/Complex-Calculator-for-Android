/*	Copyright 2011 Alexander Bunkenburg alex@inspiracio.com
 * 
 * This file is part of Complex Calculator for Android.
 * 
 * Complex Calculator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Complex Calculator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Complex Calculator for Android. If not, see <http://www.gnu.org/licenses/>.
 * */
package cat.inspiracio.inputmethodservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.InputMethodService;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputConnection;

/** Delegates everything. */
public class InputMethodServiceWrapper extends InputMethodService{

	//State -----------------------------------------------------------------------------
	
	/** the delegate */
	private InputMethodService ims;
	
	private Context context;
	
	//Constructors ----------------------------------------------------------------------
	
	/** Wrap this input method service. */
	public InputMethodServiceWrapper(InputMethodService ims){
		this.ims=ims;
		this.context=ims;
	}
	
	//Accessors -------------------------------------------------------------------------
	
	public void setContext(Context context){this.context=context;}
	public Context getContext(){return context;}
	@Override public Context getBaseContext(){return context;}
	
	//InputMethodService methods --------------------------------------------------------
	
	@Override public int getCandidatesHiddenVisibility(){return ims.getCandidatesHiddenVisibility();}
	@Override public InputBinding getCurrentInputBinding(){return ims.getCurrentInputBinding();}
	@Override public InputConnection getCurrentInputConnection(){return ims.getCurrentInputConnection();}
	@Override public EditorInfo getCurrentInputEditorInfo(){return ims.getCurrentInputEditorInfo();}
	@Override public boolean getCurrentInputStarted(){return ims.getCurrentInputStarted();}
	@Override public LayoutInflater getLayoutInflater(){return ims.getLayoutInflater();}
	@Override public int getMaxWidth(){return ims.getMaxWidth();}
	@Override public CharSequence getTextForImeAction(int imeOptions){return ims.getTextForImeAction(imeOptions);}
	@Override public Dialog getWindow(){return ims.getWindow();}
	@Override public void hideStatusIcon(){ims.hideStatusIcon();}
	@Override public void hideWindow(){ims.hideWindow();}
	@Override public boolean isExtractViewShown(){return ims.isExtractViewShown();}
	@Override public boolean isFullscreenMode(){return ims.isFullscreenMode();}
	@Override public boolean isInputViewShown(){return ims.isInputViewShown();}
	@Override public boolean isShowInputRequested(){return ims.isShowInputRequested();}
	@Override public void onAppPrivateCommand(String action, Bundle data){ims.onAppPrivateCommand(action, data);}
	@Override public void onBindInput(){ims.onBindInput();}
	@Override public void onComputeInsets(Insets outInsets){ims.onComputeInsets(outInsets);}
	@Override public void onConfigurationChanged(Configuration newConfig){ims.onConfigurationChanged(newConfig);}
	@Override public void onConfigureWindow(Window win, boolean isFullscreen,boolean isCandidatesOnly){ims.onConfigureWindow(win, isFullscreen, isCandidatesOnly);}
	
	/**  */
	@Override public void onCreate(){ims.onCreate();}

	@Override public View onCreateCandidatesView(){return ims.onCreateCandidatesView();}
	@Override public View onCreateExtractTextView(){return ims.onCreateExtractTextView();}
	@Override public View onCreateInputView(){return ims.onCreateInputView();}
	@Override public void onDestroy(){ims.onDestroy();}
	@Override public void onDisplayCompletions(CompletionInfo[] completions){ims.onDisplayCompletions(completions);}
	@Override public boolean onEvaluateFullscreenMode(){return ims.onEvaluateFullscreenMode();}
	@Override public boolean onEvaluateInputViewShown(){return ims.onEvaluateInputViewShown();}
	@Override public boolean onExtractTextContextMenuItem(int id){return ims.onExtractTextContextMenuItem(id);}
	@Override public void onExtractedCursorMovement(int dx, int dy){ims.onExtractedCursorMovement(dx, dy);}
	@Override public void onExtractedSelectionChanged(int start, int end){ims.onExtractedSelectionChanged(start, end);}
	@Override public void onExtractedTextClicked(){ims.onExtractedTextClicked();}
	@Override public void onExtractingInputChanged(EditorInfo ei){ims.onExtractingInputChanged(ei);}
	@Override public void onFinishCandidatesView(boolean finishingInput){ims.onFinishCandidatesView(finishingInput);}
	@Override public void onFinishInput(){ims.onFinishInput();}
	@Override public void onFinishInputView(boolean finishingInput){ims.onFinishInputView(finishingInput);}
	@Override public void onInitializeInterface(){ims.onInitializeInterface();}
	@Override public boolean onKeyDown(int keyCode, KeyEvent event){return ims.onKeyDown(keyCode, event);}
	@Override public boolean onKeyLongPress(int keyCode, KeyEvent event){return ims.onKeyLongPress(keyCode, event);}
	@Override public boolean onKeyMultiple(int keyCode, int count, KeyEvent event){return ims.onKeyMultiple(keyCode, count, event);}
	@Override public boolean onKeyUp(int keyCode, KeyEvent event){return ims.onKeyUp(keyCode, event);}
	@Override public boolean onShowInputRequested(int flags, boolean configChange){return ims.onShowInputRequested(flags, configChange);}
	@Override public void onStartCandidatesView(EditorInfo info, boolean restarting){ims.onStartCandidatesView(info, restarting);}
	@Override public void onStartInput(EditorInfo attribute, boolean restarting){ims.onStartInput(attribute, restarting);}
	@Override public void onStartInputView(EditorInfo info, boolean restarting){ims.onStartInputView(info, restarting);}
	@Override public boolean onTrackballEvent(MotionEvent event){return ims.onTrackballEvent(event);}
	@Override public void onUnbindInput(){ims.onUnbindInput();}
	@Override public void onUpdateCursor(Rect newCursor){ims.onUpdateCursor(newCursor);}
	@Override public void onUpdateExtractedText(int token, ExtractedText text){ims.onUpdateExtractedText(token, text);}
	@Override public void onUpdateExtractingViews(EditorInfo ei){ims.onUpdateExtractingViews(ei);}
	@Override public void onUpdateExtractingVisibility(EditorInfo ei){ims.onUpdateExtractingVisibility(ei);}
	@Override public void onUpdateSelection(int oldSelStart, int oldSelEnd,int newSelStart, int newSelEnd, int candidatesStart,int candidatesEnd){ims.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,candidatesStart, candidatesEnd);}
	@Override public void onWindowHidden(){ims.onWindowHidden();}
	@Override public void onWindowShown(){ims.onWindowShown();}
	@Override public void requestHideSelf(int flags){ims.requestHideSelf(flags);}
	@Override public boolean sendDefaultEditorAction(boolean fromEnterKey){return ims.sendDefaultEditorAction(fromEnterKey);}
	@Override public void sendDownUpKeyEvents(int keyEventCode){ims.sendDownUpKeyEvents(keyEventCode);}
	@Override public void sendKeyChar(char charCode){ims.sendKeyChar(charCode);}
	@Override public void setCandidatesView(View view){ims.setCandidatesView(view);}
	@Override public void setCandidatesViewShown(boolean shown){ims.setCandidatesViewShown(shown);}
	@Override public void setExtractView(View view){ims.setExtractView(view);}
	@Override public void setExtractViewShown(boolean shown){ims.setExtractViewShown(shown);}
	@Override public void setInputView(View view){ims.setInputView(view);}
	@Override public void setTheme(int theme){ims.setTheme(theme);}
	@Override public void showStatusIcon(int iconResId){ims.showStatusIcon(iconResId);}
	@Override public void showWindow(boolean showInput){ims.showWindow(showInput);}
	@Override public void switchInputMethod(String id){ims.switchInputMethod(id);}
	@Override public void updateFullscreenMode(){ims.updateFullscreenMode();}
	@Override public void updateInputViewShown(){ims.updateInputViewShown();}
	
	//AbstractInputMethodService methods ------------------------------------------------

	@Override public DispatcherState getKeyDispatcherState(){return ims.getKeyDispatcherState();}
	@Override public AbstractInputMethodImpl onCreateInputMethodInterface(){return ims.onCreateInputMethodInterface();}
	@Override public AbstractInputMethodSessionImpl onCreateInputMethodSessionInterface(){return ims.onCreateInputMethodSessionInterface();}
	
	//Service methods -------------------------------------------------------------------

	//@Override protected void finalize() throws Throwable{ims.finalize();}
	@Override public void onLowMemory(){ims.onLowMemory();}
	@Override public void onRebind(Intent intent){ims.onRebind(intent);}
	@Override public void onStart(Intent intent, int startId){ims.onStart(intent, startId);}
	@Override public int onStartCommand(Intent intent, int flags, int startId){return ims.onStartCommand(intent, flags, startId);}
	@Override public boolean onUnbind(Intent intent){return ims.onUnbind(intent);}
	
	//ContextWrapper methods -------------------------------------------------------------------

	/** I'm not sure how to implement. */
	@Override public String getPackageCodePath(){
		return ims.getPackageCodePath();
		//return context.getPackageCodePath();
	}

	/** I'm not sure how to implement. */
	@Override public String getPackageResourcePath(){
		return ims.getPackageResourcePath();
		//return context.getPackageResourcePath();
	}

	//Context methods -------------------------------------------------------------------

	//@Override protected void attachBaseContext(Context base){context.attachBaseContext(base);}
	@Override public boolean bindService(Intent service, ServiceConnection conn, int flags){return context.bindService(service, conn, flags);}
	@Override public int checkCallingOrSelfPermission(String permission){return context.checkCallingOrSelfPermission(permission);}
	@Override public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags){return context.checkCallingOrSelfUriPermission(uri, modeFlags);}
	@Override public int checkCallingPermission(String permission){return context.checkCallingPermission(permission);}
	@Override public int checkCallingUriPermission(Uri uri, int modeFlags){return context.checkCallingUriPermission(uri, modeFlags);}
	@Override public int checkPermission(String permission, int pid, int uid){return context.checkPermission(permission, pid, uid);}
	@Override public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags){return context.checkUriPermission(uri, pid, uid, modeFlags);}
	@Override public int checkUriPermission(Uri uri, String readPermission,String writePermission, int pid, int uid, int modeFlags){return context.checkUriPermission(uri, readPermission, writePermission, pid, uid,modeFlags);}
	@Override public void clearWallpaper() throws IOException{context.clearWallpaper();}
	@Override public Context createPackageContext(String packageName, int flags)throws NameNotFoundException{return context.createPackageContext(packageName, flags);}
	@Override public String[] databaseList(){return context.databaseList();}
	@Override public boolean deleteDatabase(String name){return context.deleteDatabase(name);}
	@Override public boolean deleteFile(String name){return context.deleteFile(name);}
	@Override public void enforceCallingOrSelfPermission(String permission, String message){context.enforceCallingOrSelfPermission(permission, message);}
	@Override public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags,String message){context.enforceCallingOrSelfUriPermission(uri, modeFlags, message);}
	@Override public void enforceCallingPermission(String permission, String message){context.enforceCallingPermission(permission, message);}
	@Override public void enforceCallingUriPermission(Uri uri, int modeFlags,String message){context.enforceCallingUriPermission(uri, modeFlags, message);}
	@Override public void enforcePermission(String permission, int pid, int uid,String message){context.enforcePermission(permission, pid, uid, message);}
	@Override public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags,String message){context.enforceUriPermission(uri, pid, uid, modeFlags, message);}
	@Override public void enforceUriPermission(Uri uri, String readPermission,String writePermission, int pid, int uid, int modeFlags,String message) {context.enforceUriPermission(uri, readPermission, writePermission, pid, uid,modeFlags, message);}
	@Override public String[] fileList(){return context.fileList();}
	@Override public Context getApplicationContext(){return context.getApplicationContext();}
	@Override public ApplicationInfo getApplicationInfo(){return context.getApplicationInfo();}
	@Override public AssetManager getAssets(){return context.getAssets();}
	@Override public File getCacheDir(){return context.getCacheDir();}
	@Override public ClassLoader getClassLoader(){return context.getClassLoader();}
	@Override public ContentResolver getContentResolver(){return context.getContentResolver();}
	@Override public File getDatabasePath(String name){return context.getDatabasePath(name);}
	@Override public File getDir(String name, int mode){return context.getDir(name, mode);}
	@Override public File getFileStreamPath(String name){return context.getFileStreamPath(name);}
	@Override public File getFilesDir(){return context.getFilesDir();}
	@Override public Looper getMainLooper(){return context.getMainLooper();}
	@Override public PackageManager getPackageManager(){return context.getPackageManager();}
	@Override public String getPackageName(){return context.getPackageName();}
	@Override public Resources getResources(){return context.getResources();}
	@Override public SharedPreferences getSharedPreferences(String name, int mode){return context.getSharedPreferences(name, mode);}
	@Override public Object getSystemService(String name){return context.getSystemService(name);}
	@Override public Theme getTheme(){return context.getTheme();}
	@Override public Drawable getWallpaper(){return context.getWallpaper();}
	@Override public int getWallpaperDesiredMinimumHeight(){return context.getWallpaperDesiredMinimumHeight();}
	@Override public int getWallpaperDesiredMinimumWidth(){return context.getWallpaperDesiredMinimumWidth();}
	@Override public void grantUriPermission(String toPackage, Uri uri, int modeFlags){context.grantUriPermission(toPackage, uri, modeFlags);}
	@Override public boolean isRestricted(){return context.isRestricted();}
	@Override public FileInputStream openFileInput(String name)throws FileNotFoundException{return context.openFileInput(name);}
	@Override public FileOutputStream openFileOutput(String name, int mode)throws FileNotFoundException{return context.openFileOutput(name, mode);}
	@Override public SQLiteDatabase openOrCreateDatabase(String name, int mode,CursorFactory factory){return context.openOrCreateDatabase(name, mode, factory);}
	@Override public Drawable peekWallpaper(){return context.peekWallpaper();}
	@Override public Intent registerReceiver(BroadcastReceiver receiver,IntentFilter filter){return context.registerReceiver(receiver, filter);}
	@Override public Intent registerReceiver(BroadcastReceiver receiver,IntentFilter filter, String broadcastPermission, Handler scheduler){return context.registerReceiver(receiver, filter, broadcastPermission, scheduler);}
	@Override public void removeStickyBroadcast(Intent intent){context.removeStickyBroadcast(intent);}
	@Override public void revokeUriPermission(Uri uri, int modeFlags){context.revokeUriPermission(uri, modeFlags);}
	@Override public void sendBroadcast(Intent intent){context.sendBroadcast(intent);}
	@Override public void sendBroadcast(Intent intent, String receiverPermission){context.sendBroadcast(intent, receiverPermission);}
	@Override public void sendOrderedBroadcast(Intent intent, String receiverPermission){context.sendOrderedBroadcast(intent, receiverPermission);}
	@Override public void sendOrderedBroadcast(Intent intent, String receiverPermission,BroadcastReceiver resultReceiver, Handler scheduler,int initialCode, String initialData, Bundle initialExtras){context.sendOrderedBroadcast(intent, receiverPermission, resultReceiver,scheduler, initialCode, initialData, initialExtras);}
	@Override public void sendStickyBroadcast(Intent intent){context.sendStickyBroadcast(intent);}
	@Override public void sendStickyOrderedBroadcast(Intent intent,BroadcastReceiver resultReceiver, Handler scheduler,int initialCode, String initialData, Bundle initialExtras){context.sendStickyOrderedBroadcast(intent, resultReceiver, scheduler,initialCode, initialData, initialExtras);}
	@Override public void setWallpaper(Bitmap bitmap) throws IOException{context.setWallpaper(bitmap);}
	@Override public void setWallpaper(InputStream data) throws IOException{context.setWallpaper(data);}
	@Override public void startActivity(Intent intent){context.startActivity(intent);}
	@Override public boolean startInstrumentation(ComponentName className,String profileFile, Bundle arguments){return context.startInstrumentation(className, profileFile, arguments);}
	@Override public void startIntentSender(IntentSender intent, Intent fillInIntent,int flagsMask, int flagsValues, int extraFlags)throws SendIntentException {context.startIntentSender(intent, fillInIntent, flagsMask, flagsValues,extraFlags);}
	@Override public ComponentName startService(Intent service) {return context.startService(service);}
	@Override public boolean stopService(Intent name){return context.stopService(name);}
	@Override public void unbindService(ServiceConnection conn){context.unbindService(conn);}
	@Override public void unregisterReceiver(BroadcastReceiver receiver){ims.unregisterReceiver(receiver);}	
}