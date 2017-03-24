package zyzx.linke.activity;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import zyzx.linke.R;
import zyzx.linke.adapter.ConversationAdapter;
import zyzx.linke.base.BaseMsgPager;

/**
 * Created by austin on 2017/3/22.
 * Desc: 消息中心-->会话(消息)页面
 */
public class MsgSessionPager extends BaseMsgPager{
    private View mView;
    private EaseConversationListItemClickListener listItemClickListener;
    List<EMConversation> emConversations;
    ConversationAdapter mAdapter;
    private ListView lvConversation;

    public MsgSessionPager(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        mView = View.inflate(mContext, R.layout.msg_session,null);
        lvConversation = (ListView) mView.findViewById(R.id.lv_conversation);
    }

    @Override
    public void initData() {
//        showProgress("加载中,请稍后……");
        emConversations = loadConversationList();
        mAdapter = new ConversationAdapter(mContext,R.layout.ease_row_chat_history,emConversations);
        lvConversation.setAdapter(mAdapter);
    }

    /**
     * load conversation list
     * @return
     */
    protected List<EMConversation> loadConversationList(){
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }
    /**
     * set conversation list item click listener
     * @param listItemClickListener
     */
    public void setConversationListItemClickListener(EaseConversationListItemClickListener listItemClickListener){
        this.listItemClickListener = listItemClickListener;
    }
    public interface EaseConversationListItemClickListener {
        /**
         * click event for conversation list
         * @param conversation -- clicked item
         */
        void onListItemClicked(EMConversation conversation);
    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }


    @Override
    public View getRootView() {
        return mView;
    }
}
