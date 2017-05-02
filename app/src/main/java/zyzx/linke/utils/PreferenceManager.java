/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zyzx.linke.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import zyzx.linke.model.bean.UserVO;

public class PreferenceManager {
	/**
	 * name of preference
	 */
	public static final String PREFERENCE_NAME = "saveInfo";
	private static SharedPreferences mSharedPreferences;
	private static PreferenceManager mPreferencemManager;
	private static SharedPreferences.Editor editor;

	private String KEY_SETTING_NOTIFICATION = "KEY_setting_notification";
	private String KEY_SETTING_SOUND = "KEY_setting_sound";
	private String KEY_SETTING_VIBRATE = "KEY_setting_vibrate";
	private String KEY_SETTING_SPEAKER = "KEY_setting_speaker";

	private static String KEY_SETTING_CHATROOM_OWNER_LEAVE = "KEY_setting_chatroom_owner_leave";
    private static String KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_GROUP = "KEY_setting_delete_messages_when_exit_group";
    private static String KEY_SETTING_AUTO_ACCEPT_GROUP_INVITATION = "KEY_setting_auto_accept_group_invitation";
    private static String KEY_SETTING_ADAPTIVE_VIDEO_ENCODE = "KEY_setting_adaptive_video_encode";
	private static String KEY_SETTING_OFFLINE_PUSH_CALL = "KEY_setting_offline_push_call";

	private static String KEY_SETTING_GROUPS_SYNCED = "KEY_SETTING_GROUPS_SYNCED";
	private static String KEY_SETTING_CONTACT_SYNCED = "KEY_SETTING_CONTACT_SYNCED";
	private static String KEY_SETTING_BALCKLIST_SYNCED = "KEY_SETTING_BALCKLIST_SYNCED";

	private static String KEY_CURRENTUSER_USERNAME = "KEY_CURRENTUSER_USERNAME";
	private static String KEY_CURRENTUSER_NICK = "KEY_CURRENTUSER_NICK";
	private static String KEY_CURRENTUSER_AVATAR = "KEY_CURRENTUSER_AVATAR";

	private static String KEY_REST_SERVER = "KEY_REST_SERVER";
	private static String KEY_IM_SERVER = "KEY_IM_SERVER";
	private static String KEY_ENABLE_CUSTOM_SERVER = "KEY_ENABLE_CUSTOM_SERVER";
	private static String KEY_ENABLE_CUSTOM_APPKEY = "KEY_ENABLE_CUSTOM_APPKEY";
	private static String KEY_CUSTOM_APPKEY = "KEY_CUSTOM_APPKEY";

	private static String KEY_CALL_MIN_VIDEO_KBPS = "KEY_CALL_MIN_VIDEO_KBPS";
	private static String KEY_CALL_MAX_VIDEO_KBPS = "KEY_CALL_Max_VIDEO_KBPS";
	private static String KEY_CALL_MAX_FRAME_RATE = "KEY_CALL_MAX_FRAME_RATE";
	private static String KEY_CALL_AUDIO_SAMPLE_RATE = "KEY_CALL_AUDIO_SAMPLE_RATE";
	private static String KEY_CALL_BACK_CAMERA_RESOLUTION = "KEY_CALL_BACK_CAMERA_RESOLUTION";
	private static String KEY_CALL_FRONT_CAMERA_RESOLUTION = "KEY_FRONT_CAMERA_RESOLUTIOIN";
	private static String KEY_CALL_FIX_SAMPLE_RATE = "KEY_CALL_FIX_SAMPLE_RATE";
	private static String KEY_LAST_LOGIN_USER_NICK = "KEY_LAST_LOGIN_USER_NICK";
	private static String KEY_LAST_LOGIN_USER_ID = "KEY_LAST_LOGIN_USER_ID";
	private static String KEY_LAST_LOGIN_USER_HSH = "KEY_LAST_LOGIN_USER_HSH";
	private static String KEY_CURR_USER_PSW = "KEY_CURR_USER_PSW";
	private static String KEY_AUTO_LOGIN_FLAG = "KEY_AUTO_LOGIN_FLAG";
	private static String KEY_MOBILE_PHONE = "KEY_MOBILE_PHONE";
	private static String KEY_ADDRESS = "KEY_ADDRESS";
	private static String KEY_AGE = "KEY_AGE";
	private static String KEY_GENDER = "KEY_GENDER";
	private static String KEY_GENDER_NAME = "KEY_GENDER_NAME";
	private static String KEY_HOBBY = "KEY_HOBBY";
	private static String KEY_EMAIL = "KEY_EMAIL";
	private static String KEY_REAL_NAME = "KEY_REAL_NAME";
	private static String KEY_CITY_NAME = "KEY_CITY_NAME";
	private static String KEY_CITY_ID = "KEY_CITY_ID";
	private static String KEY_LAST_LOGIN_TIME = "KEY_LAST_LOGIN_TIME";
	private static String KEY_SIGNATURE = "KEY_SIGNATURE";
	private static String KEY_BAK4 = "KEY_BAK4";
	private static String KEY_BIRTHDAY = "KEY_BIRTHDAY";
	private static String KEY_SCHOOL = "KEY_SCHOOL";
	private static String KEY_DEPARTMENT = "KEY_DEPARTMENT";
	private static String KEY_DIPLOMAID = "KEY_DIPLOMAID";
	private static String KEY_DIPLOMANAME = "KEY_DIPLOMANAME";
	private static String KEY_SOLILOQUY = "KEY_SOLILOQUY";
	private static String KEY_CREDITSCORE = "KEY_CREDITSCORE";

	@SuppressLint("CommitPrefEdits")
	private PreferenceManager(Context cxt) {
		mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		editor = mSharedPreferences.edit();
	}

	public static synchronized void init(Context cxt){
	    if(mPreferencemManager == null){
	        mPreferencemManager = new PreferenceManager(cxt);
	    }
	}

	/**
	 * get instance of PreferenceManager
	 *
	 * @param
	 * @return
	 */
	public synchronized static PreferenceManager getInstance() {
		if (mPreferencemManager == null) {
			throw new RuntimeException("please init first!");
		}

		return mPreferencemManager;
	}

	public void setSettingMsgNotification(boolean paramBoolean) {
		editor.putBoolean(KEY_SETTING_NOTIFICATION, paramBoolean);
		editor.apply();
	}
	public void setAutoLoginFlag(boolean autoLogin) {
		editor.putBoolean(KEY_AUTO_LOGIN_FLAG, autoLogin).commit();
	}

	public boolean getSettingMsgNotification() {
		return mSharedPreferences.getBoolean(KEY_SETTING_NOTIFICATION, true);
	}

	public void setSettingMsgSound(boolean paramBoolean) {
		editor.putBoolean(KEY_SETTING_SOUND, paramBoolean);
		editor.apply();
	}

	public boolean getSettingMsgSound() {

		return mSharedPreferences.getBoolean(KEY_SETTING_SOUND, true);
	}
	public boolean getAutoLoginFlag() {
		return mSharedPreferences.getBoolean(KEY_AUTO_LOGIN_FLAG, false);
	}

	public void setSettingMsgVibrate(boolean paramBoolean) {
		editor.putBoolean(KEY_SETTING_VIBRATE, paramBoolean);
		editor.apply();
	}

	public boolean getSettingMsgVibrate() {
		return mSharedPreferences.getBoolean(KEY_SETTING_VIBRATE, true);
	}

	public void setSettingMsgSpeaker(boolean paramBoolean) {
		editor.putBoolean(KEY_SETTING_SPEAKER, paramBoolean);
		editor.apply();
	}

	public boolean getSettingMsgSpeaker() {
		return mSharedPreferences.getBoolean(KEY_SETTING_SPEAKER, true);
	}

	public void setSettingAllowChatroomOwnerLeave(boolean value) {
        editor.putBoolean(KEY_SETTING_CHATROOM_OWNER_LEAVE, value);
        editor.apply();
    }

	public boolean getSettingAllowChatroomOwnerLeave() {
        return mSharedPreferences.getBoolean(KEY_SETTING_CHATROOM_OWNER_LEAVE, true);
    }

    public void setDeleteMessagesAsExitGroup(boolean value){
        editor.putBoolean(KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_GROUP, value);
        editor.apply();
    }

    public boolean isDeleteMessagesAsExitGroup() {
        return mSharedPreferences.getBoolean(KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_GROUP, true);
    }

    public void setAutoAcceptGroupInvitation(boolean value) {
        editor.putBoolean(KEY_SETTING_AUTO_ACCEPT_GROUP_INVITATION, value);
        editor.commit();
    }

    public boolean isAutoAcceptGroupInvitation() {
        return mSharedPreferences.getBoolean(KEY_SETTING_AUTO_ACCEPT_GROUP_INVITATION, true);
    }

    public void setAdaptiveVideoEncode(boolean value) {
        editor.putBoolean(KEY_SETTING_ADAPTIVE_VIDEO_ENCODE, value);
        editor.apply();
    }

    public boolean isAdaptiveVideoEncode() {
        return mSharedPreferences.getBoolean(KEY_SETTING_ADAPTIVE_VIDEO_ENCODE, false);
    }

	public void setPushCall(boolean value) {
		editor.putBoolean(KEY_SETTING_OFFLINE_PUSH_CALL, value);
		editor.apply();
	}

	public boolean isPushCall() {
		return mSharedPreferences.getBoolean(KEY_SETTING_OFFLINE_PUSH_CALL, false);
	}
    
	public void setGroupsSynced(boolean synced){
	    editor.putBoolean(KEY_SETTING_GROUPS_SYNCED, synced);
        editor.apply();
	}

	public boolean isGroupsSynced(){
	    return mSharedPreferences.getBoolean(KEY_SETTING_GROUPS_SYNCED, false);
	}

	public void setContactSynced(boolean synced){
        editor.putBoolean(KEY_SETTING_CONTACT_SYNCED, synced);
        editor.apply();
    }

    public boolean isContactSynced(){
        return mSharedPreferences.getBoolean(KEY_SETTING_CONTACT_SYNCED, false);
    }

    public void setBlacklistSynced(boolean synced){
        editor.putBoolean(KEY_SETTING_BALCKLIST_SYNCED, synced);
        editor.apply();
    }

    public boolean isBacklistSynced(){
        return mSharedPreferences.getBoolean(KEY_SETTING_BALCKLIST_SYNCED, false);
    }

	public void setCurrentUserNick(String nick) {
		editor.putString(KEY_CURRENTUSER_NICK, nick).commit();
	}
	public void setLastLoginUserNick(String nick) {
		editor.putString(KEY_LAST_LOGIN_USER_NICK, nick).commit();
	}

	public void setCurrentUserAvatar(String avatar) {
		editor.putString(KEY_CURRENTUSER_AVATAR, avatar);
		editor.apply();
	}
	public void setCurrentUserPSW(String psw) {
		editor.putString(KEY_CURR_USER_PSW, psw);
		editor.apply();
	}

	public String getCurrentUserNick() {
		return mSharedPreferences.getString(KEY_CURRENTUSER_NICK, null);
	}
	public String getCurrentUserPsw() {
		return mSharedPreferences.getString(KEY_CURR_USER_PSW, null);
	}
	public String getLastLoginUserNick() {
		return mSharedPreferences.getString(KEY_LAST_LOGIN_USER_NICK, null);
	}

	public UserVO getLastLoginUser(){
		UserVO u = new UserVO();
		u.setUserid(mSharedPreferences.getInt(KEY_LAST_LOGIN_USER_ID,0));
		if(u.getUserid()==0){
			return null;
		}
		int value;
		u.setLogin_name(mSharedPreferences.getString(KEY_LAST_LOGIN_USER_NICK,""));
		u.setMobile_phone(mSharedPreferences.getString(KEY_MOBILE_PHONE,""));
		u.setAddress(mSharedPreferences.getString(KEY_ADDRESS,""));
		u.setPassword(mSharedPreferences.getString(KEY_LAST_LOGIN_USER_HSH,""));
		value = mSharedPreferences.getInt(KEY_AGE,-1);
		if(value!=-1){
			u.setAge(value);
		}
		value = mSharedPreferences.getInt(KEY_GENDER,-1);
		if(value!=-1){
			u.setGender(value);
		}
		u.setGenderName(mSharedPreferences.getString(KEY_GENDER_NAME,""));
		u.setHobby(mSharedPreferences.getString(KEY_HOBBY,""));
		u.setEmail(mSharedPreferences.getString(KEY_EMAIL,""));
		u.setReal_name(mSharedPreferences.getString(KEY_REAL_NAME,""));
		u.setCityName(mSharedPreferences.getString(KEY_CITY_NAME,""));
		value = mSharedPreferences.getInt(KEY_CITY_ID,-1);
		if(value!=-1){
			u.setCityId(value);
		}
		u.setLastLoginTime(mSharedPreferences.getString(KEY_LAST_LOGIN_TIME,""));
		u.setSignature(mSharedPreferences.getString(KEY_SIGNATURE,""));
		u.setHead_icon(mSharedPreferences.getString(KEY_CURRENTUSER_AVATAR,""));
		u.setBak4(mSharedPreferences.getString(KEY_BAK4,""));
		u.setBirthday(mSharedPreferences.getString(KEY_BIRTHDAY,""));
		u.setSchool(mSharedPreferences.getString(KEY_SCHOOL,""));
		u.setDepartment(mSharedPreferences.getString(KEY_DEPARTMENT,""));
		value = mSharedPreferences.getInt(KEY_DIPLOMAID,-1);
		if(value!=-1){
			u.setDiplomaId(value);
		}
		u.setDiplomaName(mSharedPreferences.getString(KEY_DIPLOMANAME,""));
		u.setSoliloquy(mSharedPreferences.getString(KEY_SOLILOQUY,""));
		value = mSharedPreferences.getInt(KEY_CREDITSCORE,-1);
		if(value!=-1){
			u.setCreditScore(value);
		}
		return u;
	}
	public void saveLastLoginUser(UserVO u){
		if(u==null){
			editor.remove(KEY_LAST_LOGIN_USER_NICK);
			editor.remove(KEY_LAST_LOGIN_USER_ID);
			editor.remove(KEY_MOBILE_PHONE);
			editor.remove(KEY_ADDRESS);
			editor.remove(KEY_LAST_LOGIN_USER_HSH);
			editor.remove(KEY_AGE);
			editor.remove(KEY_GENDER);
			editor.remove(KEY_GENDER_NAME);
			editor.remove(KEY_HOBBY);
			editor.remove(KEY_EMAIL);
			editor.remove(KEY_REAL_NAME);
			editor.remove(KEY_CITY_NAME);
			editor.remove(KEY_CITY_ID);
			editor.remove(KEY_LAST_LOGIN_TIME);
			editor.remove(KEY_SIGNATURE);
			editor.remove(KEY_CURRENTUSER_AVATAR);
			editor.remove(KEY_BAK4);
			editor.remove(KEY_BIRTHDAY);
			editor.remove(KEY_SCHOOL);
			editor.remove(KEY_DEPARTMENT);
			editor.remove(KEY_DIPLOMAID);
			editor.remove(KEY_DIPLOMANAME);
			editor.remove(KEY_SOLILOQUY);
			editor.remove(KEY_CREDITSCORE);
			editor.commit();
			return;
		}
		editor.putString(KEY_LAST_LOGIN_USER_NICK, u.getLogin_name());
		if(u.getUserid()!=null){
			editor.putInt(KEY_LAST_LOGIN_USER_ID,u.getUserid());
		}
		if(!TextUtils.isEmpty(u.getMobile_phone())){
			editor.putString(KEY_MOBILE_PHONE,u.getMobile_phone());
		}
		if(!TextUtils.isEmpty(u.getAddress())){
			editor.putString(KEY_ADDRESS,u.getAddress());
		}
		if(!TextUtils.isEmpty(u.getPassword())){
			editor.putString(KEY_LAST_LOGIN_USER_HSH, u.getPassword());
		}
		if(u.getAge()!=null){
			editor.putInt(KEY_AGE, u.getAge());
		}
		if(u.getGender()!=null){
			editor.putInt(KEY_GENDER, u.getGender());
		}
		if(!TextUtils.isEmpty(u.getGenderName())){
			editor.putString(KEY_GENDER_NAME,u.getGenderName());
		}

		if(!TextUtils.isEmpty(u.getHobby())){
            editor.putString(KEY_HOBBY,u.getHobby());
        }
		if(!TextUtils.isEmpty(u.getEmail())){
            editor.putString(KEY_EMAIL,u.getEmail());
        }
		if(!TextUtils.isEmpty(u.getReal_name())){
            editor.putString(KEY_REAL_NAME,u.getReal_name());
        }
        if(!TextUtils.isEmpty(u.getCityName())){
			editor.putString(KEY_CITY_NAME,u.getCityName());
		}
        if(u.getCityId()!=null){
			editor.putInt(KEY_CITY_ID,u.getCityId());
		}
		if(!TextUtils.isEmpty(u.getLastLoginTime())){
			editor.putString(KEY_LAST_LOGIN_TIME,u.getLastLoginTime());
		}
		if(!TextUtils.isEmpty(u.getSignature())){
			editor.putString(KEY_SIGNATURE,u.getSignature());
		}
		if(!TextUtils.isEmpty(u.getHead_icon())){
			editor.putString(KEY_CURRENTUSER_AVATAR,u.getHead_icon());
		}
		if(!TextUtils.isEmpty(u.getBak4())){
			editor.putString(KEY_BAK4,u.getBak4());
		}
		if(!TextUtils.isEmpty(u.getBirthday())){
			editor.putString(KEY_BIRTHDAY,u.getBirthday());
		}
		if(!TextUtils.isEmpty(u.getSchool())){
			editor.putString(KEY_SCHOOL,u.getSchool());
		}
		if(!TextUtils.isEmpty(u.getDepartment())){
			editor.putString(KEY_DEPARTMENT,u.getDepartment());
		}
		if(u.getDiplomaId()!=null){
			editor.putInt(KEY_DIPLOMAID,u.getDiplomaId());
		}
		if(StringUtil.isEmpty(u.getDiplomaName())){
			editor.putString(KEY_DIPLOMANAME,u.getDiplomaName());
		}
		if(StringUtil.isEmpty(u.getSoliloquy())){
			editor.putString(KEY_SOLILOQUY,u.getSoliloquy());
		}
		if(u.getCreditScore()!=null){
			editor.putInt(KEY_CREDITSCORE,u.getCreditScore());
		}
		editor.commit();
	}

	public String getCurrentUserAvatar() {
		return mSharedPreferences.getString(KEY_CURRENTUSER_AVATAR, null);
	}

	public String getCurrentUsername(){
		return mSharedPreferences.getString(KEY_CURRENTUSER_USERNAME, null);
	}

	public void setRestServer(String restServer){
		editor.putString(KEY_REST_SERVER, restServer).commit();
		editor.commit();
	}

	public String getRestServer(){
		return mSharedPreferences.getString(KEY_REST_SERVER, null);
	}

	public void setIMServer(String imServer){
		editor.putString(KEY_IM_SERVER, imServer);
		editor.commit();
	}

	public String getIMServer(){
		return mSharedPreferences.getString(KEY_IM_SERVER, null);
	}

	public void enableCustomServer(boolean enable){
		editor.putBoolean(KEY_ENABLE_CUSTOM_SERVER, enable);
		editor.apply();
	}

	public boolean isCustomServerEnable(){
		return mSharedPreferences.getBoolean(KEY_ENABLE_CUSTOM_SERVER, false);
	}

	public void enableCustomAppkey(boolean enable) {
		editor.putBoolean(KEY_ENABLE_CUSTOM_APPKEY, enable);
		editor.apply();
	}

	public boolean isCustomAppkeyEnabled() {
		return mSharedPreferences.getBoolean(KEY_ENABLE_CUSTOM_APPKEY, false);
	}

	public String getCustomAppkey() {
		return mSharedPreferences.getString(KEY_CUSTOM_APPKEY, "");
	}

	public void setCustomAppkey(String appkey) {
		editor.putString(KEY_CUSTOM_APPKEY, appkey);
		editor.apply();
	}

	public void removeCurrentUserInfo() {
		editor.remove(KEY_CURRENTUSER_NICK);
		editor.remove(KEY_CURRENTUSER_AVATAR);
		editor.apply();
	}

	/**
	 * ----------------------------------------- Call Option -----------------------------------------
	 */

	/**
	 * Min Video kbps
	 * if no value was set, return -1
	 * @return
	 */
	public int getCallMinVideoKbps() {
		return mSharedPreferences.getInt(KEY_CALL_MIN_VIDEO_KBPS, -1);
	}

	public void setCallMinVideoKbps(int minBitRate) {
		editor.putInt(KEY_CALL_MIN_VIDEO_KBPS, minBitRate);
		editor.apply();
	}

	/**
	 * Max Video kbps
	 * if no value was set, return -1
	 * @return
	 */
	public int getCallMaxVideoKbps() {
		return mSharedPreferences.getInt(KEY_CALL_MAX_VIDEO_KBPS, -1);
	}

	public void setCallMaxVideoKbps(int maxBitRate) {
		editor.putInt(KEY_CALL_MAX_VIDEO_KBPS, maxBitRate);
		editor.apply();
	}

	/**
	 * Max frame rate
	 * if no value was set, return -1
	 * @return
	 */
	public int getCallMaxFrameRate() {
		return mSharedPreferences.getInt(KEY_CALL_MAX_FRAME_RATE, -1);
	}

	public void setCallMaxFrameRate(int maxFrameRate) {
		editor.putInt(KEY_CALL_MAX_FRAME_RATE, maxFrameRate);
		editor.apply();
	}

	/**
	 * audio sample rate
	 * if no value was set, return -1
	 * @return
	 */
	public int getCallAudioSampleRate() {
		return mSharedPreferences.getInt(KEY_CALL_AUDIO_SAMPLE_RATE, -1);
	}

	public void setCallAudioSampleRate(int audioSampleRate) {
		editor.putInt(KEY_CALL_AUDIO_SAMPLE_RATE, audioSampleRate);
		editor.apply();
	}

	/**
	 * back camera resolution
	 * format: 320x240
	 * if no value was set, return ""
	 */
	public String getCallBackCameraResolution() {
		return mSharedPreferences.getString(KEY_CALL_BACK_CAMERA_RESOLUTION, "");
	}

	public void setCallBackCameraResolution(String resolution) {
		editor.putString(KEY_CALL_BACK_CAMERA_RESOLUTION, resolution);
		editor.apply();
	}

	/**
	 * front camera resolution
	 * format: 320x240
	 * if no value was set, return ""
	 */
	public String getCallFrontCameraResolution() {
		return mSharedPreferences.getString(KEY_CALL_FRONT_CAMERA_RESOLUTION, "");
	}

	public void setCallFrontCameraResolution(String resolution) {
		editor.putString(KEY_CALL_FRONT_CAMERA_RESOLUTION, resolution);
		editor.apply();
	}

	/**
	 * fixed video sample rate
	 *  if no value was set, return false
	 * @return
     */
	public boolean isCallFixedVideoResolution() {
		return mSharedPreferences.getBoolean(KEY_CALL_FIX_SAMPLE_RATE, false);
	}

	public void setCallFixedVideoResolution(boolean enable) {
		editor.putBoolean(KEY_CALL_FIX_SAMPLE_RATE, enable);
		editor.apply();
	}

	public void setLastLoginUserId(String userId) {
		editor.putString(KEY_LAST_LOGIN_USER_ID, userId);
		editor.apply();
	}
	public void setLastLoginUserPSWHASH(String hashPsw) {
		editor.putString(KEY_LAST_LOGIN_USER_HSH, hashPsw).commit();
	}

	public Integer getLastLoginUserId() {
		return mSharedPreferences.getInt(KEY_LAST_LOGIN_USER_ID, 0);
	}
	public String getLastLoginUserPSWHASH() {
		return mSharedPreferences.getString(KEY_LAST_LOGIN_USER_HSH, null);
	}
}
