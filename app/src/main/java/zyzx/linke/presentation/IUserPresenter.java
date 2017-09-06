package zyzx.linke.presentation;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

import zyzx.linke.base.IPresenter;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.FeedBack;
import zyzx.linke.model.bean.UserVO;

/**
 * Created by austin on 2017/2/17.
 * Desc: 用户相关逻辑
 */

public abstract class IUserPresenter extends IPresenter{

    public abstract void loginByLoginName(String login_name, String password, CallBack viewCallBack);

    /**
     * 发送登录用的短信验证码
     */
    public abstract void sendLoginSMSVerifyCode(String phone,CallBack viewCallBack);

    /**
     * 找回密码页面发送短信验证码
     * @param phone
     * @param viewCallBack
     */
    public abstract void sendForgetPswSMSVerifyCode(String phone,CallBack viewCallBack);

    /**
     * 注册
     * @param userName
     * @param psw
     * @param phone
     */
    public abstract void regist(String userName, String psw, String phone,CallBack viewCallBack);

    /**
     * 根据环信userId获取用户信息
     * @param userId 环信用userId
     * @param callBack
     */
    public abstract void getUserInfoByUserId(String userId, CallBack callBack);

    //userid
    public abstract void getUserInfoByUserId2(String userId, CallBack callBack);

    /**
     * 根据uid获取用户信息
     * @param uid user's uuid
     * @param callBack
     */
    public abstract void getUserInfoByUid(String uid, CallBack callBack);
    /**
     * 上传头像
     */
    public abstract void uploadHeadIcon(Integer userId,String imagePath,CallBack viewCallBack);
    /**
     * 上传Excel文件
     */
    public abstract void uploadExcelFile(String uid,String filePath,CallBack viewCallBack);

    /**
     * 修改用户签名
     * @param userid
     * @param sig
     * @param callBack
     */
    public abstract void mofiySignature(Integer userid, String sig, CallBack callBack);

    //uuid
    public abstract void getUserInfoByUid2(String uid,CallBack callBack);

    public abstract void searchFriend(String keyWord, int pageNum,CallBack viewCallBack);

    /**
     * 得到当前登录用户的所有联系人
     * @param callBack
     */
    public abstract void getAllMyContacts(EMValueCallBack<List<EaseUser>> callBack);

    /**
     * 删除指定好友（联系人）
     * @param friendUserId 好友userId
     */
    public abstract void delFriend(Integer friendUserId,CallBack viewCallBack);

    /**
     * 添加指定用户到黑名单
     * @param relUid 要把谁添加到我的黑名单
     * @param callBack
     */
    public abstract void addBlackList(String relUid, CallBack callBack);

    /**
     * 检查自己是否已被对方加入黑名单了
     * @param userid 对方的userId
     * @param callBack
     */
    public abstract void checkIfInBlackList(Integer userid, CallBack callBack);

    /**
     * 发起添加userid为好友的请求
     * @param userid
     * @param callBack
     */
    public abstract void addFriend(Integer userid, CallBack callBack);

    /**
     * 得到用户信息（在本类getUserInfo方法基础上外加自己是否已被对方(userId)添加到黑名单等）
     * @param userId
     * @param callBack
     */
    public abstract void getUserInfoInConversation(String userId, CallBack callBack);

    /**
     * 验证短信验证码是否正确(忘记密码时找回密码流程)
     * @param verifyCode 短信验证码
     * @param callBack
     */
    public abstract void verifyForgotPSWSMSCode(String uid,String verifyCode, CallBack callBack);

    /**
     * 重置密码
     * @param newPsw
     * @param callBack
     */
    public abstract void resetPsw(String userId,String newPsw, CallBack callBack);
    //修改密码
    public abstract void modifyPsw(String userid, String oldPsw, String newPsw, CallBack callBack);
    //意见反馈
    public abstract void feedBack(FeedBack mFeedBack, CallBack callBack);
    //获取指定省份(省份id)下的所有地级市
    public abstract void getSubArea(Integer pid, Integer holdFlag,CallBack callBack);

    public abstract void saveUserInfo(UserVO user,CallBack callBack);
    //通过第三方账号登录系统
    public abstract void loginByThirdPlatform(String paraJSON,CallBack callBack);

    /**
     * 分享图书
     * @param shareJson 分享json子串，包含所分享图书bean、分享方式、留言、所在城市 信息
     * @param callBack
     */
    public abstract void shareBook(String shareJson, CallBack callBack);

    //根据zyzx_area ID 获取 记录
    public abstract void getSharerArea(Integer shareAreaId, CallBack callBack);

    //根据zyzx_area ID 获取 省市县字符串名和书籍状态
    public abstract void getSharerArea2BookStatus(Integer shareAreaId,String userBookId, CallBack callBack);

    //获取指定城市所有的分享的书籍
    public abstract void getAllShareBooks(String pro,String city,String county,int pageNo,CallBack callBack);

    //发送求借求赠送消息
    public abstract void sendBegBookMsg(Integer shareType,Integer userId, UserVO friend, BookDetail2 book, CallBack callBack);

    public abstract void getAllBorrowBegs(Integer userId,int pageNo,CallBack callBack);

    /**
     * 设置借阅流状态
     * @param currentUser 书籍所有人
     * @param chatUserId  借阅人
     * @param bookId 书籍id
     * @param status 状态：<br/>
     *                          &#9; 1 借阅者-请求已发送     <br/>
     *                          &#9; 2 所有者-请求已同意    <br/>
     *                          &#9; 3 所有者-请求已被拒绝   <br/>
     *                          &#9; 4 借阅者-约会已同意    <br/>
     *                          &#9; 5 借阅者-约会已同意
     * @param callBack 回调
     */
    public abstract void setBorrowFlowstatus(String userBookId,String currentUser, String chatUserId, String bookId,int status,CallBack callBack);

    /**
     * 图书交换
     * @param userBookid
     * @param bookId
     * @param bookTitle
     * @param bookAuthor
     * @param msg
     * @param callBack
     */
    public abstract void swapBook(String userBookid,String bookId, String bookTitle, String bookAuthor, String msg, CallBack callBack);

    /**
     * 取消书籍交换
     * @param userBookId
     * @param swapId
     * @param callBack
     */
    public abstract void cancelSwapBook(String userBookId, String swapId, CallBack callBack);

    /**
     * 得到交换的技能类型
     */
    public abstract void getSkillClassify(CallBack callBack);

    /**
     * 发布技能交换帖
     * @param title 技能交换标题
     * @param ownSkillType 我拥有的技能类型
     * @param ownSkillName 我拥有的技能名（简短）
     * @param swapSkillType 我想要交换的技能类型
     * @param swapSkillName 我想要交换的技能（简短描述）
     * @param detail 具体描述
     * @param callBack
     */
    public abstract void publishMySkillSwap(String title, String ownSkillType, String ownSkillName, String swapSkillType, String swapSkillName, String detail, CallBack callBack);

    /**
     * 获取单个技能交换详情
     * @param swapSkillId 技能交换表主键
     * @param callBack
     */
    public abstract void getSwapSkillDeatil(String swapSkillId, CallBack callBack);

    public abstract void deleteSwapSkill(String swapSkillId,CallBack callBack);

    public abstract void getBookInfo(String bookId, CallBack callBack);

    /**
     * 添加关注
     * @param uid 当前登录用户（关注人）
     * @param uid1 被关注人
     * @param callBack
     */
    public abstract void addAttention(String uid, String uid1, CallBack callBack);

    //检查当前登陆用户是否已经对uid关注了
    public abstract void checkIfAttentioned(String uid, CallBack callBack);
    //取消关注
    public abstract void cancelAttention(String uid, CallBack callBack);

    //举报
    public abstract void report(String uid, String type, String desc, CallBack callBack);
    //从黑名单删除某人
    public abstract void deleteFromBlackList(String uid, CallBack callBack);


    /**
     *确认图书已经归还
     * @param userBookId userbook表主键
     * @param uid  借阅人环信id
     * @param relUid 拥有者环信id
     * @param bid 书籍id
     * @param callBack
     */
    public abstract void confirmReturnedBook(String userBookId,String uid,Integer relUid, String bid,CallBack callBack);
}
