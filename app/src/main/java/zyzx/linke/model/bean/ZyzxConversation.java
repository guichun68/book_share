package zyzx.linke.model.bean;

import com.hyphenate.chat.EMConversation;

/**
 * Created by austin on 2017/3/22.
 * Desc: 一个会话（包含EaseMob的conversation【代表和一个用户的对话，包含发送和接收的消息】）
 */

public class ZyzxConversation {
    private EMConversation emConversation;
    private String emUserName;//用户名（环信）
    private String avatarPath;//头像地址

    public EMConversation getEmConversation() {
        return emConversation;
    }

    public void setEmConversation(EMConversation emConversation) {
        this.emConversation = emConversation;
    }

    public String getEmUserName() {
        return emUserName;
    }

    public void setEmUserName(String emUserName) {
        this.emUserName = emUserName;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}
