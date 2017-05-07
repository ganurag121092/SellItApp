package buyerseller.cs646.sdsu.edu.sellit;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by AnuragG on 06-May-17.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListHolder> {
    private ArrayList<ChatModel> chatList;

    class ChatListHolder extends RecyclerView.ViewHolder {
        TextView msgText;

        ChatListHolder(View view) {
            super(view);
            msgText = (TextView) view.findViewById(R.id.messageText);
        }
    }

    ChatListAdapter(ArrayList<ChatModel> chatList) {
        this.chatList = chatList;
    }

    @Override
    public ChatListAdapter.ChatListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_row, parent, false);

        return new ChatListAdapter.ChatListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatListAdapter.ChatListHolder holder, int position) {
        ChatModel chat = chatList.get(position);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("UIDs",uid+ "  "+chat.getSenderUid());
        if(TextUtils.equals(uid,chat.getSenderUid())){
            holder.msgText.setGravity(Gravity.END);
        }

        holder.msgText.setText(chat.message);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}
