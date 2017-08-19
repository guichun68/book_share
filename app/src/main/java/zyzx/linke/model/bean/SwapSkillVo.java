package zyzx.linke.model.bean;

/**
 * Created by austin on 2017/8/18.
 */

public class SwapSkillVo {
    private String swapSkillId;//zyzxz_swap_skill主键
    private String uid;//发起人id
    private String headIcon;
    private String skillTitle;
    private String skillHaveName;
    private String skillWantName;

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
}
