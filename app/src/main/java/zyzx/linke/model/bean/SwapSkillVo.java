package zyzx.linke.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by austin on 2017/8/18.
 */

public class SwapSkillVo implements Parcelable{
    private String swapSkillId;//zyzxz_swap_skill主键
    private String uid;//发起人id
    private String loginName;//发起人用户名
    private String headIcon;
    private String skillTitle;
    private String skillType;//中文名
    private String swapSkillType;//想要交换的技能的类型（中文--取自常量表）
    private String skillHaveName;
    private String skillWantName;

    public SwapSkillVo(){}

    protected SwapSkillVo(Parcel in) {
        swapSkillId = in.readString();
        uid = in.readString();
        headIcon = in.readString();
        skillTitle = in.readString();
        skillType = in.readString();
        swapSkillType = in.readString();
        skillHaveName = in.readString();
        skillWantName = in.readString();
        loginName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(swapSkillId);
        dest.writeString(uid);
        dest.writeString(headIcon);
        dest.writeString(skillTitle);
        dest.writeString(skillType);
        dest.writeString(swapSkillType);
        dest.writeString(skillHaveName);
        dest.writeString(skillWantName);
        dest.writeString(loginName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SwapSkillVo> CREATOR = new Creator<SwapSkillVo>() {
        @Override
        public SwapSkillVo createFromParcel(Parcel in) {
            return new SwapSkillVo(in);
        }

        @Override
        public SwapSkillVo[] newArray(int size) {
            return new SwapSkillVo[size];
        }
    };

    public String getSwapSkillId() {
        return swapSkillId;
    }

    public void setSwapSkillId(String swapSkillId) {
        this.swapSkillId = swapSkillId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
    }

    public String getSkillTitle() {
        return skillTitle;
    }

    public void setSkillTitle(String skillTitle) {
        this.skillTitle = skillTitle;
    }

    public String getSkillHaveName() {
        return skillHaveName;
    }

    public void setSkillHaveName(String skillHaveName) {
        this.skillHaveName = skillHaveName;
    }

    public String getSkillWantName() {
        return skillWantName;
    }

    public void setSkillWantName(String skillWantName) {
        this.skillWantName = skillWantName;
    }

    public String getSkillType() {
        return skillType;
    }

    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getSwapSkillType() {
        return swapSkillType;
    }

    public void setSwapSkillType(String swapSkillType) {
        this.swapSkillType = swapSkillType;
    }
}
